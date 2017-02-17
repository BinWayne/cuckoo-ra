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
package org.cuckoo.ra.common;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.spi.ManagedConnectionMetaData;

public class CuckooConnectionMetaData implements ManagedConnectionMetaData, ConnectionMetaData {

    private final String eisProductName;
    private final String eisProductVersion;
    private final int maxConnections;
    private final String userName;

    public CuckooConnectionMetaData(String eisProductName, String eisProductVersion, int maxConnections,
                                    String userName) {
        this.eisProductName = eisProductName;
        this.eisProductVersion = eisProductVersion;
        this.maxConnections = maxConnections;
        this.userName = userName;
    }

    /**
     * Returns Product name of the underlying EIS instance connected through the ManagedConnection.
     *
     * @return Product name of the EIS instance
     * @throws ResourceException Thrown if an error occurs
     */
    public String getEISProductName() throws ResourceException {
        return eisProductName;
    }

    /**
     * Returns Product version of the underlying EIS instance connected through the ManagedConnection.
     *
     * @return Product version of the EIS instance
     * @throws ResourceException Thrown if an error occurs
     */
    public String getEISProductVersion() throws ResourceException {
        return eisProductVersion;
    }

    /**
     * Returns maximum limit on number of active concurrent connections
     *
     * @return Maximum limit for number of active concurrent connections, a value of '0' means unlimited
     * @throws ResourceException Thrown if an error occurs
     */
    public int getMaxConnections() throws ResourceException {
        return maxConnections;
    }

    /**
     * Returns name of the user associated with the ManagedConnection instance
     *
     * @return name of the user
     * @throws ResourceException Thrown if an error occurs
     */
    public String getUserName() throws ResourceException {
        return userName;
    }
}
