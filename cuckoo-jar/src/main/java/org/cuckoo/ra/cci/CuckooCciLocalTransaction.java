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
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import org.cuckoo.ra.spi.CuckooManagedConnection;

public class CuckooCciLocalTransaction implements javax.resource.cci.LocalTransaction {

    private static final Logger LOG = Logger.getLogger(CuckooCciLocalTransaction.class.getName());

    private final Connection connectionHandle;

    private final CuckooManagedConnection managedConnection;

    CuckooCciLocalTransaction(CuckooManagedConnection managedConnection, Connection connectionHandle) {
        this.managedConnection = managedConnection;
        this.connectionHandle = connectionHandle;
    }

    public void begin() throws ResourceException {
        LOG.entering("CuckooCciLocalTransaction", "begin()");
        managedConnection.beginLocalTransaction(connectionHandle);
    }

    public void commit() throws ResourceException {
        LOG.entering("CuckooCciLocalTransaction", "commit()");
        managedConnection.commitLocalTransaction(connectionHandle);
    }

    public void rollback() throws ResourceException {
        LOG.entering("CuckooCciLocalTransaction", "rollback()");
        managedConnection.rollbackLocalTransaction(connectionHandle);
    }
}
