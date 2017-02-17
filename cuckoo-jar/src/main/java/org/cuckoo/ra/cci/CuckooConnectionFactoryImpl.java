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
package org.cuckoo.ra.cci;

import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;
import javax.resource.spi.ConnectionManager;
import org.cuckoo.ra.spi.CuckooManagedConnectionFactory;

public class CuckooConnectionFactoryImpl implements CuckooConnectionFactory {

    private static final Logger LOG = Logger.getLogger(CuckooConnectionFactoryImpl.class.getName());

    private Reference reference;
    private ConnectionManager connectionManager;
    private CuckooManagedConnectionFactory managedConnectionFactory;

    // JCA 1.5 Spec, Section: 17.5.1.1: A ConnectionFactory must have a default constructor
    @SuppressWarnings({"UnusedDeclaration"})
    public CuckooConnectionFactoryImpl() {
    }

    /**
     * @param connectionManager ConnectionManager
     * @param managedConnectionFactory The ManagedConnectionFactory instance.
     */
    public CuckooConnectionFactoryImpl(ConnectionManager connectionManager,
                                       CuckooManagedConnectionFactory managedConnectionFactory) {
        LOG.entering("CuckooConnectionFactoryImpl", "CuckooConnectionFactoryImpl( ConnectionManager )");
        this.connectionManager = connectionManager;
        this.managedConnectionFactory = managedConnectionFactory;
    }

    /**
     * Gets a connection to an EIS instance.
     *
     * @return Connection instance the EIS instance.
     * @throws ResourceException Failed to get a connection to
     */
    public CuckooConnection getConnection() throws ResourceException {
        LOG.entering("CuckooConnectionFactoryImpl", "getConnection()");
        return (CuckooConnection) connectionManager.allocateConnection(managedConnectionFactory, null);
    }

    /**
     * Gets a connection to an EIS instance.
     *
     * @param connSpec Connection parameters and security information specified as ConnectionSpec instance
     * @return Connection instance the EIS instance.
     * @throws ResourceException Failed to get a connection to
     */
    public CuckooConnection getConnection(ConnectionSpec connSpec) throws ResourceException {
        LOG.entering("CuckooConnectionFactoryImpl", "getConnection( ConnectionSpec )");
        if (connSpec == null) {
            throw new IllegalArgumentException("The ConnectionSpec passed as an argument must not be null");
        }
        if (!(connSpec instanceof ApplicationPropertiesImpl)) {
            throw new IllegalArgumentException(
                    "The ConnectionSpec passed as an argument must be of type " +
                            ApplicationPropertiesImpl.class.getName());
        }
        final ApplicationPropertiesImpl specImpl = (ApplicationPropertiesImpl) connSpec;
        return (CuckooConnection) connectionManager
                .allocateConnection(managedConnectionFactory, new ApplicationPropertiesImpl(specImpl));
    }

    /**
     * Gets metadata for the Resource Adapter.
     *
     * @return ResourceAdapterMetaData instance
     * @throws ResourceException Failed to get metadata information
     */
    public CuckooRaMetaData getMetaData() throws ResourceException {
        LOG.entering("CuckooConnectionFactoryImpl", "getMetaData()");
        return managedConnectionFactory.getResourceAdapter().getResourceAdapterMetaData();
    }

    /**
     * Gets a RecordFactory instance.
     *
     * @return RecordFactory instance
     * @throws ResourceException Failed to create a RecordFactory
     * @throws javax.resource.NotSupportedException Operation not supported
     */
    public CuckooRecordFactory getRecordFactory() throws ResourceException {
        LOG.entering("CuckooConnectionFactoryImpl", "getRecordFactory()");
        return managedConnectionFactory.getResourceAdapter().getRecordFactory();
    }

    /**
     * Get the Reference instance.
     *
     * @return Reference instance
     */
    public Reference getReference() throws NamingException {
        LOG.entering("CuckooConnectionFactoryImpl", "getReference()");
        return reference;
    }

    /**
     * Set the Reference instance.
     *
     * @param reference A Reference instance
     */
    public void setReference(Reference reference) {
        LOG.entering("CuckooConnectionFactoryImpl", "setReference( Reference )");

        this.reference = reference;
    }

    public ApplicationProperties createApplicationProperties() {
        return new ApplicationPropertiesImpl();
    }
}
