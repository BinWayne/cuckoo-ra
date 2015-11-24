/*
 * Copyright (C) 2010 akquinet tech@spree GmbH
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import org.cuckoo.ra.cci.ApplicationProperties;
import org.cuckoo.ra.cci.CuckooConnection;
import org.cuckoo.ra.common.CuckooConnectionMetaData;
import org.cuckoo.ra.jco.CuckooJCoSessionReference;
import org.cuckoo.ra.jco.JCoAdapter;

public class CuckooManagedConnectionImpl implements CuckooManagedConnection {
	private static final Logger LOG = Logger.getLogger(CuckooManagedConnectionImpl.class.getName());

	private final ConfigurationProperties configurationProperties;
	private final ApplicationProperties applicationProperties;
	private final JCoAdapter jCoAdapter;
	private final List<CuckooConnection> connectionHandlers;
	private final List<ConnectionEventListener> eventListeners;
	private final CuckooSpiLocalTransaction localTransaction;
	private final CuckooConnectionMetaData metaData;

	private boolean inTransaction = false;

	private static CuckooJCoSessionReference localSessionReference = null;

	/*
	 * @return local SapSessionReference for the current thread
	 */
	public static CuckooJCoSessionReference getLocalSessionReference() {
		return localSessionReference;
	}

	public static void setLocalSessionReference(CuckooJCoSessionReference localSessionReference) {
		CuckooManagedConnectionImpl.localSessionReference = localSessionReference;
	}

	private static List<CuckooJCoSessionReference> sessions = new ArrayList<CuckooJCoSessionReference>();

	/*
	 * @return mapping of threads to SapSessionReference objects
	 */
	public static List<CuckooJCoSessionReference> getSessions() {
		return sessions;
	}

	CuckooManagedConnectionImpl(ConfigurationProperties configurationProperties,
			ApplicationProperties applicationProperties) throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "CuckooManagedConnectionImpl()");

		this.configurationProperties = configurationProperties;
		this.applicationProperties = applicationProperties;
		this.localTransaction = new CuckooSpiLocalTransaction(this);
		connectionHandlers = Collections.synchronizedList(new ArrayList<CuckooConnection>());
		eventListeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>());
		jCoAdapter = new JCoAdapter(configurationProperties.getDestinationName(), applicationProperties);
		metaData = jCoAdapter.createConnectionMetaData();
	}

	ApplicationProperties getApplicationProperties() {
		LOG.entering("CuckooManagedConnectionImpl", "getApplicationProperties()");

		return applicationProperties;
	}

	/**
	 * Creates a new connection handle for the underlying physical connection
	 * represented by the ManagedConnection instance.
	 *
	 * @param subject
	 *            security context as JAAS subject
	 * @param connectionRequestInfo
	 *            ConnectionRequestInfo instance
	 * @return generic Object instance representing the connection handle.
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public CuckooConnection getConnection(Subject subject, ConnectionRequestInfo connectionRequestInfo)
			throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "getConnection( Subject, ConnectionRequestInfo )");

		// TODO assert that subject and connectionRequestInfo are the same as in
		// ManagedConnection

		CuckooConnection connection = new CuckooConnection(this);
		connectionHandlers.add(connection);
		return connection;
	}

	/**
	 * Used by the container to change the association of an application-level
	 * connection handle with a ManagedConnection instance.
	 *
	 * @param connection
	 *            Application-level connection handle
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void associateConnection(Object connection) throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "associateConnection(" + connection + ")");

		if (connection == null) {
			throw new ResourceException("Connection is null");
		}

		if (!(connection instanceof CuckooConnection)) {
			throw new ResourceException(
					"Only connections of type " + CuckooConnection.class.getName() + " can be handled. "
							+ "The container provided a connection of type " + connection.getClass().getName());
		}

		final CuckooConnection connectionImpl = (CuckooConnection) connection;
		connectionImpl.associateManagedConnection(this);
		connectionHandlers.add(connectionImpl);
	}

	public void disassociateConnection(final CuckooConnection connection) throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "disassociateConnection( " + connection + " )");

		if (!connectionHandlers.contains(connection)) {
			throw new ResourceException("Connection handler is not associated with the given connection.");
		}
		connectionHandlers.remove(connection);
	}

	/**
	 * Application server calls this method to force any cleanup on the
	 * ManagedConnection instance.
	 *
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void cleanup() throws ResourceException

	{
		LOG.entering("CuckooManagedConnectionImpl", "cleanup()");
		synchronized (connectionHandlers) {
			for (final CuckooConnection connectionImpl : connectionHandlers) {
				connectionImpl.invalidate();
			}
			connectionHandlers.clear();
		}
	}

	/**
	 * Destroys the physical connection to the underlying resource manager.
	 *
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void destroy() throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "destroy()");
		synchronized (connectionHandlers) {
			connectionHandlers.clear();
			jCoAdapter.disconnect();
		}
	}

	/**
	 * Adds a connection event listener to the ManagedConnection instance.
	 *
	 * @param listener
	 *            a new ConnectionEventListener to be registered
	 */
	public void addConnectionEventListener(ConnectionEventListener listener) {
		LOG.entering("CuckooManagedConnectionImpl", "addConnectionEventListener(ConnectionEventListener)");
		synchronized (eventListeners) {
			if (!eventListeners.contains(listener)) {
				eventListeners.add(listener);
			}
		}
	}

	/**
	 * Removes an already registered connection event listener from the
	 * ManagedConnection instance.
	 *
	 * @param listener
	 *            already registered connection event listener to be removed
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		LOG.entering("CuckooManagedConnectionImpl", "removeConnectionEventListener(ConnectionEventListener)");
		synchronized (eventListeners) {
			eventListeners.remove(listener);
		}
	}

	/**
	 * Gets the log writer for this ManagedConnection instance.
	 *
	 * @return Character output stream associated with this Managed-Connection
	 *         instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "getLogWriter()");
		return null;
	}

	/**
	 * Sets the log writer for this ManagedConnection instance.
	 *
	 * @param out
	 *            Character Output stream to be associated
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "setLogWriter(PrintWriter)");
	}

	/**
	 * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
	 *
	 * @return LocalTransaction instance
	 */
	public CuckooSpiLocalTransaction getLocalTransaction() {
		LOG.entering("CuckooManagedConnectionImpl", "getLocalTransaction()");
		return localTransaction;
	}

	public void beginLocalTransaction(Connection connectionHandle) {
		getLocalTransaction().begin();
		notifyLocalTransactionStartedEvent(connectionHandle);
	}

	public void commitLocalTransaction(Connection connectionHandle) throws ResourceException {
		getLocalTransaction().commit();
		notifyLocalTransactionCommittedEvent(connectionHandle);
	}

	public void rollbackLocalTransaction(Connection connectionHandle) throws ResourceException {
		getLocalTransaction().rollback();
		notifyLocalTransactionRolledbackEvent(connectionHandle);
	}

	/**
	 * Returns an <code>javax.transaction.xa.XAResource</code> instance.
	 *
	 * @return XAResource instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public XAResource getXAResource() throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "getXAResource()");
		throw new NotSupportedException("XA is not supported, since SAP does not implement 2-phase-commit");
	}

	/**
	 * Gets the metadata information for this connection's underlying EIS
	 * resource manager instance.
	 *
	 * @return ManagedConnectionMetaData instance
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public CuckooConnectionMetaData getMetaData() throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "getMetaData()");
		return metaData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CuckooManagedConnectionImpl that = (CuckooManagedConnectionImpl) o;

		if (applicationProperties != null ? !applicationProperties.equals(that.applicationProperties)
				: that.applicationProperties != null) {
			return false;
		}
		// noinspection RedundantIfStatement
		if (configurationProperties != null ? !configurationProperties.equals(that.configurationProperties)
				: that.configurationProperties != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = configurationProperties != null ? configurationProperties.hashCode() : 0;
		result = 31 * result + (applicationProperties != null ? applicationProperties.hashCode() : 0);
		return result;
	}

	public void notifyConnectionClosed(CuckooConnection connection) {
		LOG.entering("CuckooManagedConnectionImpl", "notifyConnectionClosed()");

		final ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
		event.setConnectionHandle(connection);

		synchronized (eventListeners) {
			for (final ConnectionEventListener listener : eventListeners) {
				listener.connectionClosed(event);
			}
		}
	}

	public MappedRecord executeFunction(String functionName, Record input) throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "executeFunction()");

		localTransaction.startJCoSession();
		
		if (inTransaction) {
			LOG.finer("Executing function in transaction");

			return jCoAdapter.executeFunction(functionName, input);
		} else {
			LOG.finer("Executing function with auto-commit");

			jCoAdapter.startTransaction();
			try {
				MappedRecord output = jCoAdapter.executeFunction(functionName, input);
				jCoAdapter.commitTransaction();
				return output;
			} catch (ResourceException e) {
				jCoAdapter.rollbackTransaction();
				throw e;
			}
		}
	}

	void startTransaction() {
		LOG.entering("CuckooManagedConnectionImpl", "startTransaction()");

		jCoAdapter.startTransaction();
		inTransaction = true;
	}

	void commitTransaction() throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "commitTransaction()");
		localTransaction.startJCoSession();
		jCoAdapter.commitTransaction();
		inTransaction = false;
	}

	void rollbackTransaction() throws ResourceException {
		LOG.entering("CuckooManagedConnectionImpl", "rollbackTransaction()");
		localTransaction.startJCoSession();
		jCoAdapter.rollbackTransaction();
		inTransaction = false;
	}

	public void notifyLocalTransactionStartedEvent(Connection connectionHandle) {
		LOG.entering("CuckooManagedConnectionImpl", "notifyLocalTransactionStartedEvent()");

		final ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.LOCAL_TRANSACTION_STARTED);
		event.setConnectionHandle(connectionHandle);

		synchronized (eventListeners) {
			for (ConnectionEventListener eventListener : eventListeners) {
				eventListener.localTransactionStarted(event);
			}
		}
	}

	public void notifyLocalTransactionCommittedEvent(Connection connectionHandle) {
		LOG.entering("CuckooManagedConnectionImpl", "notifyLocalTransactionCommittedEvent()");

		final ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.LOCAL_TRANSACTION_COMMITTED);
		event.setConnectionHandle(connectionHandle);

		synchronized (eventListeners) {
			for (ConnectionEventListener eventListener : eventListeners) {
				eventListener.localTransactionCommitted(event);
			}
		}
	}

	public void notifyLocalTransactionRolledbackEvent(Connection connectionHandle) {
		LOG.entering("CuckooManagedConnectionImpl", "notifyLocalTransactionRolledbackEvent()");

		final ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK);
		event.setConnectionHandle(connectionHandle);

		synchronized (eventListeners) {
			for (ConnectionEventListener eventListener : eventListeners) {
				eventListener.localTransactionRolledback(event);
			}
		}
	}
}
