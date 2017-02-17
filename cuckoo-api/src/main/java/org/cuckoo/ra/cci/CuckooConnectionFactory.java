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

import javax.resource.cci.ConnectionFactory;

/**
 * The CuckooConnectionFactory is used by an application component to get a
 * connection to an EIS instance. Furthermore, it gives access to the RecordFactory,
 * connection specific meta data and application specific properties that may be
 * passed when getting the connection.
 * <p/>
 * In the managed scenario (the resource adapter running in a Java EE container),
 * application code either uses injection with the @Resource annotation to get access
 * to a ConnectionFactory instance, or looks it up from JNDI namespace.
 * The instance can then be used to get EIS connections.
 * <p/>
 *
 * @see javax.resource.cci.ConnectionFactory
 * @see javax.resource.cci.Connection
 * @see javax.resource.cci.RecordFactory
 * @see javax.resource.cci.ConnectionMetaData
 * @see ApplicationProperties
 */
public interface CuckooConnectionFactory extends ConnectionFactory {

    /**
     * Creates a new ApplicationProperties instance that may be used by an application
     * component to provide connection specific information (e.g. a user and password)
     * when getting a SAP connection. This is needed in cases the configured values
     * are not sufficient, e.g. when the user enters his SAP user and password in
     * the application.
     *
     * @return A new ApplicationProperties instance.
     */
    ApplicationProperties createApplicationProperties();
}
