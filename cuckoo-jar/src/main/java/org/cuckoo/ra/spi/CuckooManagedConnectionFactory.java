/*
 * Copyright (C) 2012-2017 akquinet tech@spree GmbH
 *
 * This file is part of the Cuckoo Resource Adapter for SAP.
 *
 * Cuckoo Resource Adapter for SAP is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Cuckoo Resource Adapter for SAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Cuckoo Resource Adapter for SAP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cuckoo.ra.spi;

import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import org.cuckoo.ra.cci.ApplicationProperties;
import org.cuckoo.ra.cci.ApplicationPropertiesImpl;
import org.cuckoo.ra.cci.CuckooConnectionFactory;
import org.cuckoo.ra.cci.CuckooConnectionFactoryImpl;
import org.cuckoo.ra.jco.JCoDestinationUtil;

public class CuckooManagedConnectionFactory extends ConfigurationPropertiesHolder
        implements ManagedConnectionFactory, ResourceAdapterAssociation {

    private static final Logger LOG = Logger.getLogger(CuckooManagedConnectionFactory.class.getName());

    private CuckooResourceAdapter resourceAdapter;

    private PrintWriter logWriter;

    private boolean initialized = false;

    private final UUID id = createId();

    private UUID createId() {
        final UUID uuid = UUID.randomUUID();
        LOG.finest("CuckooManagedConnectionFactory.createId(), ID=" + uuid);
        return uuid;
    }

    public CuckooManagedConnectionFactory() {
        LOG.finest(toString());
    }

    /**
     * Creates a Connection Factory instance.
     *
     * @param connectionManager ConnectionManager to be associated with created EIS connection factory instance
     * @return EIS-specific Connection Factory instance or javax.resource.cci.ConnectionFactory instance
     * @throws ResourceException Generic exception
     */
    public CuckooConnectionFactory createConnectionFactory(ConnectionManager connectionManager)
            throws ResourceException {
        LOG.entering("CuckooManagedConnectionFactory", "createConnectionFactory(ConnectionManager)");

        if (!initialized) {
            final String destinationName = getDestinationName();

            LOG.info("Registering new SAP destination named " + destinationName);

            resourceAdapter.registerDestination(destinationName,
                    JCoDestinationUtil.createJCoDestinationProperties(getConfigurationProperties()));
            initialized = true;
        }

        return new CuckooConnectionFactoryImpl(connectionManager, this);
    }

    private void assertInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Managed Connection Factory is not yet initialized");
        }
    }

    /**
     * Creates a Connection Factory instance in a non-managed environment
     *
     * @return EIS-specific Connection Factory instance or javax.resource.cci.ConnectionFactory instance
     * @throws ResourceException Generic exception
     */
    public CuckooConnectionFactory createConnectionFactory() throws ResourceException {
        LOG.entering("CuckooManagedConnectionFactory", "createConnectionFactory()");

        return createConnectionFactory(new CuckooConnectionManager());
    }

    /**
     * Creates a new physical connection to the underlying EIS resource manager.
     *
     * @param subject Caller's security information
     * @param connectionRequestInfo Additional resource adapter specific connection request information
     * @return ManagedConnection instance
     * @throws ResourceException generic exception
     */
    public ManagedConnection createManagedConnection(Subject subject,
                                                     ConnectionRequestInfo connectionRequestInfo)
            throws ResourceException {
        LOG.entering("CuckooManagedConnectionFactory", "createManagedConnection(Subject, ConnectionRequestInfo)");
        LOG.finest("Parameters passed by container: subject=" + subject + ", connectionRequestInfo=" +
                connectionRequestInfo);

        assertInitialized();

        ApplicationProperties applicationProperties = (ApplicationProperties) connectionRequestInfo;

        if (subject != null) {
            LOG.finer("Container managed sign-on. Subject: " + subject);

            final Set<PasswordCredential> passwordCredentials = getPasswordCredentials(subject);

            if (passwordCredentials != null && !passwordCredentials.isEmpty()) {
                LOG.finer("Password based sign-on based on PasswordCredentials, size=" + passwordCredentials.size());

                for (final PasswordCredential pc : passwordCredentials) {
                    LOG.finest("ManagedConnectionFactory of PC=" + pc.getManagedConnectionFactory());
                    LOG.finest("ManagedConnectionFactory of ME=" + this);
                    LOG.finest("same()=" + (this == pc.getManagedConnectionFactory()));
                    LOG.finest("equals()=" + this.equals(pc.getManagedConnectionFactory()));

                    if (this.equals(pc.getManagedConnectionFactory())) {
                        if (applicationProperties != null) {
                            LOG.finer(
                                    "Overwriting application defined configuration with user/password from Credentials");
                            applicationProperties.setUser(pc.getUserName());
                            applicationProperties.setPassword(String.valueOf(pc.getPassword()));
                            return new CuckooManagedConnectionImpl(getConfigurationProperties(),
                                    applicationProperties);
                        } else {
                            LOG.finer(
                                    "No application-defined configuration info, using configured properties and user/password from Credentials");

                            applicationProperties = new ApplicationPropertiesImpl(pc.getUserName(),
                                    String.valueOf(pc.getPassword()));
                            return new CuckooManagedConnectionImpl(getConfigurationProperties(),
                                    applicationProperties);
                        }
                    }
                }
                throw new ResourceException(
                        "No PasswordCredential found that corresponds to the ManagedConnectionFactory. Please check your security configuration.");
            }

            // TODO Implement single sign-on with SAP logon ticket using org.ietf.jgss.GSSCredential; see JCA 1.5 spec chapter 9.1.5
            throw new NotSupportedException("Only password based sign-on supported");
        } else if (applicationProperties == null) {
            LOG.finer(
                    "No configuration provided by application, use configured properties");
        } else {
            LOG.finer("Application managed sign-on. Using configuration provided by application");
        }
        return new CuckooManagedConnectionImpl(getConfigurationProperties(), applicationProperties);
    }

    /**
     * Returns a matched connection from the candidate set of connections.
     *
     * @param connectionSet candidate connection set
     * @param subject Caller's security information
     * @param cxRequestInfo Additional resource adapter specific connection request information
     * @return ManagedConnection if resource adapter finds an acceptable match otherwise null
     * @throws ResourceException generic exception
     */
    public ManagedConnection matchManagedConnections(Set connectionSet,
                                                     Subject subject,
                                                     ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        LOG.entering(
                "CuckooManagedConnectionFactory", "matchManagedConnections(Set, Subject, ConnectionRequestInfo)");

        assertInitialized();

        if (cxRequestInfo != null && !(cxRequestInfo instanceof ApplicationPropertiesImpl)) {
            LOG.warning("ConnectionRequestInfo is not an instance of ApplicationProperties, but of " +
                    cxRequestInfo.getClass());
            return null;
        }

        ApplicationProperties applicationProperties = (ApplicationProperties) cxRequestInfo;

        LOG.finer("ConnectionRequestInfo=" + applicationProperties);

        for (final Object object : connectionSet) {
            final CuckooManagedConnectionImpl candidateConnection = (CuckooManagedConnectionImpl) object;
            ApplicationProperties candidateConnectionProperties = candidateConnection.getApplicationProperties();

            LOG.finer("Candidate Connection=" + candidateConnectionProperties);

            //noinspection ConstantConditions
            if (applicationProperties == null) {
                if (candidateConnectionProperties == null) {
                    LOG.finer("Found matching connection (no application properties): " + candidateConnection);
                    return candidateConnection;
                } else {
                    if (hasUsernameAndPasswordAsConfigured(candidateConnectionProperties)) {
                        LOG.finer("Found matching connection (equal user and password): " + candidateConnection);
                    }
                    return candidateConnection;
                }
            } else {
                if (applicationProperties.equals(candidateConnectionProperties)) {
                    LOG.finer("Found matching connection (equal application properties): " + candidateConnection);
                    return candidateConnection;
                }
            }
        }
        LOG.finer("No matching connection found, returning null");
        return null;
    }

    private boolean hasUsernameAndPasswordAsConfigured(ApplicationProperties candidateConnectionProperties) {
        return candidateConnectionProperties.getUser().equals(getConfigurationProperties().getUsername()) &&
                candidateConnectionProperties.getPassword().equals(getConfigurationProperties().getPassword());
    }

    /**
     * Get the log writer for this ManagedConnectionFactory instance.
     *
     * @return PrintWriter
     * @throws ResourceException generic exception
     */
    public PrintWriter getLogWriter() throws ResourceException {
        LOG.entering("CuckooManagedConnectionFactory", "getLogWriter()");
        return logWriter;
    }

    /**
     * Set the log writer for this ManagedConnectionFactory instance.
     *
     * @param out PrintWriter - an out stream for error logging and tracing
     * @throws ResourceException generic exception
     */
    public void setLogWriter(PrintWriter out) throws ResourceException {
        LOG.entering("CuckooManagedConnectionFactory", "setLogWriter(PrintWriter)");

        logWriter = out;
//        logWriter.println( "     ########## LogWriter ###############" );
    }

    /**
     * Get the resource adapter
     *
     * @return The handle
     */
    public CuckooResourceAdapter getResourceAdapter() {
        LOG.entering("CuckooManagedConnectionFactory", "getResourceAdapter()");
        return resourceAdapter;
    }

    /**
     * Set the resource adapter
     *
     * @param ra The handle
     */
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        LOG.entering("CuckooManagedConnectionFactory", "setResourceAdapter(ResourceAdapter)");

        if (!(ra instanceof CuckooResourceAdapter)) {
            throw new IllegalArgumentException(
                    "Got instance of type " + ra.getClass().getName() + " while only type "
                            + CuckooResourceAdapter.class.getName() + " is supported.");
        }
        if (this.resourceAdapter != null) {
            throw new javax.resource.spi.IllegalStateException(
                    "This method was already called, but must be called exactly once");
        }

        this.resourceAdapter = (CuckooResourceAdapter) ra;
    }

    /**
     * Gets the given Subject's PasswordCredentials via AccessController.doPrivileged(), since
     * Subject.getPrivateCredentials() may throw a SecurityException in a secured environment.
     *
     * @param subject The Subject
     * @return A Set of PasswordCredentials, as returned by Subject.getPrivateCredentials().
     */
    public Set<PasswordCredential> getPasswordCredentials(final Subject subject) {
        return AccessController.doPrivileged(new PrivilegedAction<Set<PasswordCredential>>() {
                                                 public Set<PasswordCredential> run() {
                                                     return subject.getPrivateCredentials(PasswordCredential.class);
                                                 }
                                             }
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CuckooManagedConnectionFactory that = (CuckooManagedConnectionFactory) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CuckooManagedConnectionFactory{" +
                "id=" + id +
                "} " + super.toString();
    }
}
