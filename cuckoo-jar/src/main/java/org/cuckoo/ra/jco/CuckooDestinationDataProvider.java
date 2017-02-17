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
package org.cuckoo.ra.jco;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class CuckooDestinationDataProvider implements DestinationDataProvider {

    private static final Logger LOG = Logger.getLogger(CuckooDestinationDataProvider.class.getName());

    private Map<String, Properties> propertiesForDestinationName = new HashMap<String, Properties>();

    private DestinationDataEventListener eventListener;

    public void addDestination(String destinationName, Properties properties) {
        LOG.entering("CuckooDestinationDataProvider", "addDestination()");

        if (destinationName == null || destinationName.length() == 0) {
            throw new IllegalArgumentException("destinationName must not be null or empty");
        }
        if (properties == null) {
            throw new IllegalArgumentException("properties must not be null");
        }
        if (wasAdded(destinationName)) {
            LOG.warning("The destination '" + destinationName + "' was already registered, it will be overwritten.");
        }
        propertiesForDestinationName.put(destinationName, properties);

        if (eventListener != null) {
            eventListener.updated(destinationName);
        } else {
            LOG.warning("The eventListener is null!");
        }
    }

    // TODO shall be called on undeployment of ManagedConnectionFactory: how do we get informed about this?
    public void removeDestination(String destinationName) {
        LOG.finer("CuckooDestinationDataProvider.removeDestination( " + destinationName + " )");

        propertiesForDestinationName.remove(destinationName);

        if (eventListener != null) {
            eventListener.deleted(destinationName);
        } else {
            LOG.warning("The eventListener is null!");
        }
    }

    public void removeAllDestinations() {
        LOG.info("CuckooDestinationDataProvider.removeAllDestinations( )");

        for (String destinationName : propertiesForDestinationName.keySet()) {
            removeDestination(destinationName);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Properties getDestinationProperties(String destinationName) {
        LOG.finer("CuckooDestinationDataProvider.getDestinationProperties( " + destinationName + " )");

        if (wasAdded(destinationName)) {
            return propertiesForDestinationName.get(destinationName);
        } else {
            throw new IllegalArgumentException("No JCo destination with name '" + destinationName + "' registered.");
        }
    }

    public boolean wasAdded(String destinationName) {
        return propertiesForDestinationName.containsKey(destinationName);
    }

    /**
     * {@inheritDoc}
     */
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        LOG.finer("CuckooDestinationDataProvider.setDestinationDataEventListener( " + eventListener + " )");
        this.eventListener = eventListener;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsEvents() {
        LOG.finer("CuckooDestinationDataProvider.supportsEvents()");
        return true;
    }
}
