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

import javax.resource.spi.ConnectionRequestInfo;

@SuppressWarnings("unused")
public final class ApplicationPropertiesImpl implements ConnectionRequestInfo, ApplicationProperties {

    private String user;
    private String aliasUser;
    private String password;
    private String client;
    private String language;
    private String ssoTicket;
    private String x509Certificate;

    /**
     * Copy constructor.
     *
     * @param applicationProperties The ApplicationProperties that is used to create a copy.
     */
    ApplicationPropertiesImpl(ApplicationPropertiesImpl applicationProperties) {
        this.user = applicationProperties.user;
        this.aliasUser = applicationProperties.aliasUser;
        this.password = applicationProperties.password;
        this.client = applicationProperties.client;
        this.language = applicationProperties.language;
        this.ssoTicket = applicationProperties.ssoTicket;
        this.x509Certificate = applicationProperties.x509Certificate;
    }

    public ApplicationPropertiesImpl() {
    }

    public ApplicationPropertiesImpl(String username, String password) {
        this.user = username;
        this.password = password;
    }

    public ApplicationPropertiesImpl(String user, String password, String language, String client, String aliasUser,
                                     String ssoTicket, String x509Certificate) {
        this(user, password);
        this.language = language;
        this.client = client;
        this.aliasUser = aliasUser;
        this.ssoTicket = ssoTicket;
        this.x509Certificate = x509Certificate;
    }

    public ApplicationProperties setUser(String user) {
        this.user = user;
        return this;
    }

    public ApplicationProperties setAliasUser(String aliasUser) {
        this.aliasUser = aliasUser;
        return this;
    }

    public ApplicationProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public ApplicationProperties setClient(String client) {
        this.client = client;
        return this;
    }

    public ApplicationProperties setLanguage(String language) {
        this.language = language;
        return this;
    }

    public ApplicationProperties setSsoTicket(String ssoTicket) {
        this.ssoTicket = ssoTicket;
        return this;
    }

    public ApplicationProperties setX509Certificate(String x509Certificate) {
        this.x509Certificate = x509Certificate;
        return this;
    }

    public String getUser() {
        return user;
    }

    public String getAliasUser() {
        return aliasUser;
    }

    public String getPassword() {
        return password;
    }

    public String getClient() {
        return client;
    }

    public String getLanguage() {
        return language;
    }

    public String getSsoTicket() {
        return ssoTicket;
    }

    public String getX509Certificate() {
        return x509Certificate;
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

        ApplicationPropertiesImpl that = (ApplicationPropertiesImpl) o;

        if (aliasUser != null ? !aliasUser.equals(that.aliasUser) : that.aliasUser != null) {
            return false;
        }
        if (client != null ? !client.equals(that.client) : that.client != null) {
            return false;
        }
        if (language != null ? !language.equals(that.language) : that.language != null) {
            return false;
        }
        if (password != null ? !password.equals(that.password) : that.password != null) {
            return false;
        }
        if (ssoTicket != null ? !ssoTicket.equals(that.ssoTicket) : that.ssoTicket != null) {
            return false;
        }
        if (user != null ? !user.equals(that.user) : that.user != null) {
            return false;
        }
        if (x509Certificate != null ? !x509Certificate.equals(that.x509Certificate) : that.x509Certificate != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (aliasUser != null ? aliasUser.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (ssoTicket != null ? ssoTicket.hashCode() : 0);
        result = 31 * result + (x509Certificate != null ? x509Certificate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "aliasUser='" + aliasUser + '\'' +
                ", user='" + user + '\'' +
                ", password='***'" +
                ", client='" + client + '\'' +
                ", language='" + language + '\'' +
                ", ssoTicket='" + ssoTicket + '\'' +
                ", x509Certificate='" + x509Certificate + '\'' +
                '}';
    }
}
