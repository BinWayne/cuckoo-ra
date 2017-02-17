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

import java.util.logging.Logger;
import javax.resource.ResourceException;

public class CuckooSpiLocalTransaction implements javax.resource.spi.LocalTransaction {

    private static final Logger LOG = Logger.getLogger(CuckooSpiLocalTransaction.class.getName());

    private final CuckooManagedConnectionImpl managedConnection;

    public CuckooSpiLocalTransaction(CuckooManagedConnectionImpl managedConnection) {
        this.managedConnection = managedConnection;
    }

    public void begin() {
        LOG.finest("Start transaction");
        managedConnection.startTransaction();
    }

    public void commit() throws ResourceException {
        LOG.finest("Commit transaction");
        managedConnection.commitTransaction();
    }

    public void rollback() throws ResourceException {
        LOG.finest("Rollback transaction");
        managedConnection.rollbackTransaction();
    }
}
