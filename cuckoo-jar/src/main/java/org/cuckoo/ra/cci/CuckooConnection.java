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
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.ResultSetInfo;
import org.cuckoo.ra.common.CuckooConnectionMetaData;
import org.cuckoo.ra.spi.CuckooManagedConnection;

public class CuckooConnection implements Connection {

    private static final Logger LOG = Logger.getLogger(CuckooConnection.class.getName());

    private CuckooManagedConnection managedConnection;

    private boolean invalidated = false;

    private boolean closed = false;

    public CuckooConnection(CuckooManagedConnection managedConnection) {
        LOG.entering("CuckooConnection", "CuckooConnection()");
        this.managedConnection = managedConnection;
    }

    /**
     * Initiates close of the connection handle at the application level.
     *
     * @throws ResourceException Exception thrown if close on a connection handle fails.
     */
    public void close() throws ResourceException {
        LOG.entering("CuckooConnection", "close()");

        if (closed) {
            throw new ResourceException("Connection has already been closed");
        }

        managedConnection.notifyConnectionClosed(this);
        closed = true;
    }

    /**
     * Creates an Interaction associated with this Connection.
     *
     * @return Interaction instance
     * @throws ResourceException Failed to create an Interaction
     */
    public Interaction createInteraction() throws ResourceException {
        LOG.entering("CuckooConnection", "createInteraction()");
        checkIfValid();
        return new CuckooInteraction(this);
    }

    /**
     * Returns an LocalTransaction instance that enables a component to
     * demarcate resource manager local transactions on the Connection.
     *
     * @return LocalTransaction instance
     * @throws ResourceException Failed to return a LocalTransaction
     * @throws javax.resource.NotSupportedException Demarcation of Resource manager
     */
    public LocalTransaction getLocalTransaction() throws ResourceException {
        LOG.entering("CuckooConnection", "getLocalTransaction()");
        checkIfValid();
        return new CuckooCciLocalTransaction(managedConnection, this);
    }

    /**
     * Gets the information on the underlying EIS instance represented through an active connection.
     *
     * @return ConnectionMetaData instance representing information about the EIS instance
     * @throws ResourceException Failed to get information about the connected EIS instance.
     */
    public CuckooConnectionMetaData getMetaData() throws ResourceException {
        LOG.entering("CuckooConnection", "getMetaData()");
        checkIfValid();
        return managedConnection.getMetaData();
    }

    /**
     * Gets the information on the ResultSet functionality supported by
     * a connected EIS instance.
     *
     * @return ResultSetInfo instance
     * @throws ResourceException Failed to get ResultSet related information
     * @throws javax.resource.NotSupportedException ResultSet functionality is not supported
     */
    public ResultSetInfo getResultSetInfo() throws ResourceException {
        LOG.entering("CuckooConnection", "getResultSetInfo()");
        throw new NotSupportedException("ResultSetInfo not supported");
    }

    public void associateManagedConnection(CuckooManagedConnection managedConnection) throws ResourceException {
        LOG.entering("CuckooConnection", "associateManagedConnection()");

        this.managedConnection.disassociateConnection(this);
        this.managedConnection = managedConnection;
    }

    public void invalidate() {
        LOG.entering("CuckooConnection", "invalidate()");

        invalidated = true;
    }

    private void checkIfValid() throws ResourceException {
        if (invalidated) {
            throw new ResourceException("Connection handler has already been invalidated");
        }
    }

    MappedRecord executeFunction(String functionName, Record input) throws ResourceException {
        LOG.entering("CuckooConnection", "executeFunction()");

        checkIfValid();
        return managedConnection.executeFunction(functionName, input);
    }
}
