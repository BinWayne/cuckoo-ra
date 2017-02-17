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

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;
import org.cuckoo.ra.cci.CuckooConnection;

// TODO
public class CuckooConnectionManager implements ConnectionManager {

    CuckooConnectionManager() {
        throw new UnsupportedOperationException("Support for non-managed environments not yet implemented");
    }

    /**
     * Allocate a connection
     *
     * @param mcf The managed connection factory
     * @param cri The connection request information
     * @return Object The connection
     * @throws ResourceException Thrown if an error occurs
     */
    public CuckooConnection allocateConnection(ManagedConnectionFactory mcf, ConnectionRequestInfo cri) throws ResourceException {
        return null;
    }
}
