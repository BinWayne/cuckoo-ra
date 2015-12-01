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

import java.util.logging.Logger;

import javax.resource.ResourceException;

import org.cuckoo.ra.jco.CuckooJCoSessionReference;
import org.cuckoo.ra.jco.CuckooJCoSessionTracker;

public class CuckooSpiLocalTransaction implements javax.resource.spi.LocalTransaction {
	private static final Logger LOG = Logger.getLogger(CuckooSpiLocalTransaction.class.getName());

	private final CuckooManagedConnectionImpl managedConnection;
	private final CuckooJCoSessionReference sapSessionReference;

	public CuckooSpiLocalTransaction(CuckooManagedConnectionImpl managedConnection) {
		this.managedConnection = managedConnection;
		this.sapSessionReference = new CuckooJCoSessionReference();
	}

	public CuckooJCoSessionReference getSapSessionReference() {
		return sapSessionReference;
	}

	public void begin() {
		LOG.finest("Start transaction");
		CuckooJCoSessionTracker.setJCoSessionReference(sapSessionReference);
		managedConnection.startTransaction();
	}

	public void commit() throws ResourceException {
		LOG.finest("Commit transaction");
		CuckooJCoSessionTracker.setJCoSessionReference(sapSessionReference);
		managedConnection.commitTransaction();
	}

	public void rollback() throws ResourceException {
		LOG.finest("Rollback transaction");
		CuckooJCoSessionTracker.setJCoSessionReference(sapSessionReference);
		managedConnection.rollbackTransaction();
	}
}
