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

import java.util.logging.Logger;
import javax.resource.cci.ResourceAdapterMetaData;
import org.cuckoo.ra.util.RaXmlReader;

public class CuckooRaMetaData implements ResourceAdapterMetaData {

    private static final Logger LOG = Logger.getLogger(CuckooRaMetaData.class.getName());

    private static final String RA_XML_FILE = "/META-INF/ra.xml";

    private final String adapterVersion;
    private final String adapterVendorName;
    private final String adapterName;
    private final String adapterShortDescription;
    private final String specVersion;

    public CuckooRaMetaData() {
        LOG.entering("CuckooRaMetaData", "CuckooRaMetaData()");

        RaXmlReader xmlReader = new RaXmlReader(RA_XML_FILE);
        adapterVersion = xmlReader.getResourceAdapterVersion();
        adapterVendorName = xmlReader.getVendorName();
        adapterName = xmlReader.getDisplayName();
        adapterShortDescription = xmlReader.getDescription();
        specVersion = xmlReader.getSpecVersion();
    }

    /**
     * Gets the version of the resource adapter.
     *
     * @return String representing version of the resource adapter
     */
    public String getAdapterVersion() {
        return adapterVersion;
    }

    /**
     * Gets the name of the vendor that has provided the resource adapter.
     *
     * @return String representing name of the vendor
     */
    public String getAdapterVendorName() {
        return adapterVendorName;
    }

    /**
     * Gets a tool displayable name of the resource adapter.
     *
     * @return String representing the name of the resource adapter
     */
    public String getAdapterName() {
        return adapterName;
    }

    /**
     * Gets a tool displayable short desription of the resource adapter.
     *
     * @return String describing the resource adapter
     */
    public String getAdapterShortDescription() {
        return adapterShortDescription;
    }

    /**
     * Returns a string representation of the version
     *
     * @return String representing the supported version of the connector architecture
     */
    public String getSpecVersion() {
        return specVersion;
    }

    /**
     * Returns an array of fully-qualified names of InteractionSpec
     *
     * @return Array of fully-qualified class names of InteractionSpec classes
     */
    public String[] getInteractionSpecsSupported() {
        return new String[]{CuckooInteractionSpec.class.getName()};
    }

    /**
     * Returns true if the implementation class for the Interaction
     *
     * @return boolean depending on method support
     */
    public boolean supportsExecuteWithInputAndOutputRecord() {
        return false;
    }

    /**
     * Returns true if the implementation class for the Interaction
     *
     * @return boolean depending on method support
     */
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    /**
     * Returns true if the resource adapter implements the LocalTransaction
     *
     * @return true if resource adapter supports resource manager local transaction demarcation
     */
    public boolean supportsLocalTransactionDemarcation() {
        return true;
    }
}
