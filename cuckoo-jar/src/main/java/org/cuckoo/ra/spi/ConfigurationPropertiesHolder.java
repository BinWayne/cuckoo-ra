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
 * Holds an instance of ConfigurationProperties, delegating all calls to this instance.
 * This is used here to avoid mixing the ManagedConnectionFactory business methods with
 * accessor methods for its properties, which is a shortcoming of the Connector API.
 */
public class ConfigurationPropertiesHolder implements ConfigurationProperties {

    private final ConfigurationPropertiesImpl properties = new ConfigurationPropertiesImpl();

    ConfigurationPropertiesImpl getConfigurationProperties() {
        return new ConfigurationPropertiesImpl(properties);
    }

    public void setDestinationName(String destinationName) {
        properties.setDestinationName(destinationName);
    }

    public void setJcoClient(String jcoClient) {
        properties.setJcoClient(jcoClient);
    }

    public void setUsername(String username) {
        properties.setUsername(username);
    }

    public void setJcoAliasUser(String jcoAliasUser) {
        properties.setJcoAliasUser(jcoAliasUser);
    }

    public void setPassword(String password) {
        properties.setPassword(password);
    }

    public void setJcoLanguage(String jcoLanguage) {
        properties.setJcoLanguage(jcoLanguage);
    }

    public void setJcoSapRouter(String jcoSapRouter) {
        properties.setJcoSapRouter(jcoSapRouter);
    }

    public void setJcoSystemNumber(String jcoSystemNumber) {
        properties.setJcoSystemNumber(jcoSystemNumber);
    }

    public void setJcoApplicationServerHost(String jcoApplicationServerHost) {
        properties.setJcoApplicationServerHost(jcoApplicationServerHost);
    }

    public void setJcoMessageServerHost(String jcoMessageServerHost) {
        properties.setJcoMessageServerHost(jcoMessageServerHost);
    }

    public void setJcoMessageServerPort(String jcoMessageServerPort) {
        properties.setJcoMessageServerPort(jcoMessageServerPort);
    }

    public void setJcoR3Name(String jcoR3Name) {
        properties.setJcoR3Name(jcoR3Name);
    }

    public void setJcoGroup(String jcoGroup) {
        properties.setJcoGroup(jcoGroup);
    }

    public void setJcoTrace(String jcoTrace) {
        properties.setJcoTrace(jcoTrace);
    }

    public void setJcoCpicTrace(String jcoCpicTrace) {
        properties.setJcoCpicTrace(jcoCpicTrace);
    }

    public void setJcoUseSapgui(String jcoUseSapgui) {
        properties.setJcoUseSapgui(jcoUseSapgui);
    }

    public void setJcoCodepage(String jcoCodepage) {
        properties.setJcoCodepage(jcoCodepage);
    }

    public void setJcoSncMode(String jcoSncMode) {
        properties.setJcoSncMode(jcoSncMode);
    }

    public void setJcoSncPartnername(String jcoSncPartnername) {
        properties.setJcoSncPartnername(jcoSncPartnername);
    }

    public void setJcoSncQop(String jcoSncQop) {
        properties.setJcoSncQop(jcoSncQop);
    }

    public void setJcoSncMyName(String jcoSncMyName) {
        properties.setJcoSncMyName(jcoSncMyName);
    }

    public void setJcoSncLibrary(String jcoSncLibrary) {
        properties.setJcoSncLibrary(jcoSncLibrary);
    }

    public void setJcoRepositoryDestination(String jcoRepositoryDestination) {
        properties.setJcoRepositoryDestination(jcoRepositoryDestination);
    }

    public void setJcoRepositoryUser(String jcoRepositoryUser) {
        properties.setJcoRepositoryUser(jcoRepositoryUser);
    }

    public void setJcoRepositoryPassword(String jcoRepositoryPassword) {
        properties.setJcoRepositoryPassword(jcoRepositoryPassword);
    }

    public void setJcoRepositorySncMode(String jcoRepositorySncMode) {
        properties.setJcoRepositorySncMode(jcoRepositorySncMode);
    }

    public String getDestinationName() {
        return properties.getDestinationName();
    }

    public String getJcoClient() {
        return properties.getJcoClient();
    }

    public String getUsername() {
        return properties.getUsername();
    }

    public String getJcoAliasUser() {
        return properties.getJcoAliasUser();
    }

    public String getPassword() {
        return properties.getPassword();
    }

    public String getJcoLanguage() {
        return properties.getJcoLanguage();
    }

    public String getJcoSapRouter() {
        return properties.getJcoSapRouter();
    }

    public String getJcoSystemNumber() {
        return properties.getJcoSystemNumber();
    }

    public String getJcoApplicationServerHost() {
        return properties.getJcoApplicationServerHost();
    }

    public String getJcoMessageServerHost() {
        return properties.getJcoMessageServerHost();
    }

    public String getJcoMessageServerPort() {
        return properties.getJcoMessageServerPort();
    }

    public String getJcoR3Name() {
        return properties.getJcoR3Name();
    }

    public String getJcoGroup() {
        return properties.getJcoGroup();
    }

    public String getJcoTrace() {
        return properties.getJcoTrace();
    }

    public String getJcoCpicTrace() {
        return properties.getJcoCpicTrace();
    }

    public String getJcoUseSapgui() {
        return properties.getJcoUseSapgui();
    }

    public String getJcoCodepage() {
        return properties.getJcoCodepage();
    }

    public String getJcoSncMode() {
        return properties.getJcoSncMode();
    }

    public String getJcoSncPartnername() {
        return properties.getJcoSncPartnername();
    }

    public String getJcoSncQop() {
        return properties.getJcoSncQop();
    }

    public String getJcoSncMyName() {
        return properties.getJcoSncMyName();
    }

    public String getJcoSncLibrary() {
        return properties.getJcoSncLibrary();
    }

    public String getJcoRepositoryDestination() {
        return properties.getJcoRepositoryDestination();
    }

    public String getJcoRepositoryUser() {
        return properties.getJcoRepositoryUser();
    }

    public String getJcoRepositoryPassword() {
        return properties.getJcoRepositoryPassword();
    }

    public String getJcoRepositorySncMode() {
        return properties.getJcoRepositorySncMode();
    }

    @SuppressWarnings({"RedundantIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigurationPropertiesHolder that = (ConfigurationPropertiesHolder) o;

        if (properties != null ? !properties.equals(that.properties) : that.properties != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }
}
