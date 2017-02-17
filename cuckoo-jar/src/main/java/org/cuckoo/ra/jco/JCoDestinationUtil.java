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

import com.sap.conn.jco.ext.DestinationDataProvider;
import java.util.Properties;
import java.util.logging.Logger;
import org.cuckoo.ra.spi.ConfigurationProperties;

public class JCoDestinationUtil {

    private static final Logger LOG = Logger.getLogger(JCoDestinationUtil.class.getName());

    private JCoDestinationUtil() {
        // use static methods
    }

    public static Properties createJCoDestinationProperties(ConfigurationProperties config) {
        LOG.entering("ConfigurationPropertiesImpl", "createJCoDestinationProperties()");

        final Properties properties = new Properties();
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_CLIENT, config.getJcoClient());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_USER, config.getUsername());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_ALIAS_USER, config.getJcoAliasUser());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_PASSWD, config.getPassword());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_LANG, config.getJcoLanguage());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SAPROUTER, config.getJcoSapRouter());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SYSNR, config.getJcoSystemNumber());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_ASHOST, config.getJcoApplicationServerHost());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_MSHOST, config.getJcoMessageServerHost());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_MSSERV, config.getJcoMessageServerPort());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_R3NAME, config.getJcoR3Name());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_GROUP, config.getJcoGroup());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_TRACE, config.getJcoTrace());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_CPIC_TRACE, config.getJcoCpicTrace());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_USE_SAPGUI, config.getJcoUseSapgui());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_CODEPAGE, config.getJcoCodepage());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SNC_MODE, config.getJcoSncMode());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SNC_PARTNERNAME, config.getJcoSncPartnername());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SNC_QOP, config.getJcoSncQop());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SNC_MYNAME, config.getJcoSncMyName());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_SNC_LIBRARY, config.getJcoSncLibrary());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_REPOSITORY_DEST,
                config.getJcoRepositoryDestination());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_REPOSITORY_USER, config.getJcoRepositoryUser());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_REPOSITORY_PASSWD,
                config.getJcoRepositoryPassword());
        addPropertyNullSafe(properties, DestinationDataProvider.JCO_REPOSITORY_SNC, config.getJcoRepositorySncMode());

        // A pool_capacity value of 0 disables connection pooling
        // The pool shall be maintained by the container, not by JCo
        properties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "0");
        return properties;
    }

    private static void addPropertyNullSafe(Properties properties, String name, String value) {
        if (value != null) {
            properties.setProperty(name, value);
        }
    }
}
