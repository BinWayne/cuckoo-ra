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
package org.cuckoo.ra.util;

import org.cuckoo.ra.CuckooException;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class RaXmlReaderTest {

    private static final RaXmlReader xmlReader = new RaXmlReader("/META-INF/ra.xml");

    @Test
    public void readsDescription() throws Exception {
        assertThat(xmlReader.getDescription()).isEqualTo("Description");
    }

    @Test
    public void readsDisplayName() throws Exception {
        assertThat(xmlReader.getDisplayName()).isEqualTo("Display Name");
    }

    @Test
    public void readsEisType() throws Exception {
        assertThat(xmlReader.getEisType()).isEqualTo("EIS Type");
    }

    @Test
    public void readsVendorName() throws Exception {
        assertThat(xmlReader.getVendorName()).isEqualTo("Vendor Name");
    }

    @Test
    public void readsResourceAdapterVersion() throws Exception {
        assertThat(xmlReader.getResourceAdapterVersion()).isEqualTo("Resource Adapter Version");
    }

    @Test
    public void readsSpecVersion() throws Exception {
        assertThat(xmlReader.getSpecVersion()).isEqualTo("1.5");
    }

    @Test(expected = CuckooException.class)
    public void throwsCuckooExceptionWhenRaXmlFileCanNotBeFound() throws Exception {
        new RaXmlReader("not_there");
    }

    @Test
    public void throwsExceptionWhenElementInRaXmlFileIsMissing() throws Exception {
        RaXmlReader reader = new RaXmlReader("/META-INF/ra_broken.xml");
        try {
            reader.getDescription();
            fail("Expected: Cuckoo Exception");
        } catch (CuckooException e) {
            // expected
        }
    }

    @Test
    public void throwsExceptionWhenElementInRaXmlFileIsEmpty() throws Exception {
        RaXmlReader reader = new RaXmlReader("/META-INF/ra_broken.xml");
        try {
            reader.getDisplayName();
            fail("Expected: Cuckoo Exception");
        } catch (CuckooException e) {
            // expected
        }
    }
}
