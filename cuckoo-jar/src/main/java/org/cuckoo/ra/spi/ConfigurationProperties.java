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

/**
 * Holds properties that are provided by the resource adapter configuration.
 * I. e. configuration properties defined in the ra.xml provided with the resource adapter archive,
 * or the configuration properties defined in the application server specific deployment descriptor.
 * These are set by the container on each managed connection factory instance.
 */
public interface ConfigurationProperties {

    void setDestinationName(String destinationName);

    void setJcoClient(String jcoClient);

    void setUsername(String username);

    void setJcoAliasUser(String jcoAliasUser);

    void setPassword(String password);

    void setJcoLanguage(String jcoLanguage);

    void setJcoSapRouter(String jcoSapRouter);

    void setJcoSystemNumber(String jcoSystemNumber);

    void setJcoApplicationServerHost(String jcoApplicationServerHost);

    void setJcoMessageServerHost(String jcoMessageServerHost);

    void setJcoMessageServerPort(String jcoMessageServerPort);

    void setJcoR3Name(String jcoR3Name);

    void setJcoGroup(String jcoGroup);

    void setJcoTrace(String jcoTrace);

    void setJcoCpicTrace(String jcoCpicTrace);

    void setJcoUseSapgui(String jcoUseSapgui);

    void setJcoCodepage(String jcoCodepage);

    void setJcoSncMode(String jcoSncMode);

    void setJcoSncPartnername(String jcoSncPartnername);

    void setJcoSncQop(String jcoSncQop);

    void setJcoSncMyName(String jcoSncMyName);

    void setJcoSncLibrary(String jcoSncLibrary);

    void setJcoRepositoryDestination(String jcoRepositoryDestination);

    void setJcoRepositoryUser(String jcoRepositoryUser);

    void setJcoRepositoryPassword(String jcoRepositoryPassword);

    void setJcoRepositorySncMode(String jcoRepositorySncMode);

    String getDestinationName();

    String getJcoClient();

    String getUsername();

    String getJcoAliasUser();

    String getPassword();

    String getJcoLanguage();

    String getJcoSapRouter();

    String getJcoSystemNumber();

    String getJcoApplicationServerHost();

    String getJcoMessageServerHost();

    String getJcoMessageServerPort();

    String getJcoR3Name();

    String getJcoGroup();

    String getJcoTrace();

    String getJcoCpicTrace();

    String getJcoUseSapgui();

    String getJcoCodepage();

    String getJcoSncMode();

    String getJcoSncPartnername();

    String getJcoSncQop();

    String getJcoSncMyName();

    String getJcoSncLibrary();

    String getJcoRepositoryDestination();

    String getJcoRepositoryUser();

    String getJcoRepositoryPassword();

    String getJcoRepositorySncMode();
}
