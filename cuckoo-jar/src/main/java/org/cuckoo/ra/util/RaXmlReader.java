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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.cuckoo.ra.CuckooException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Reads in Resource Adapter properties from the ra.xml file.
 */
public class RaXmlReader {

    private final Document xmlDocument;
    private final XPath xPath;

    /**
     * Contructor
     *
     * @param raXmlFile The location of the ra.xml file in the classpath.
     */
    public RaXmlReader(final String raXmlFile) {
        InputStream inputStream = getInputStream(raXmlFile);

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            documentBuilder.setEntityResolver(new EntityResolverDummy());
            xmlDocument = documentBuilder.parse(inputStream);
            xPath = XPathFactory.newInstance().newXPath();
        } catch (SAXException e) {
            throw new CuckooException("Error reading ra.xml file", e);
        } catch (IOException e) {
            throw new CuckooException("Error reading ra.xml file", e);
        } catch (ParserConfigurationException e) {
            throw new CuckooException("Error creating XML DocumentBuilder for ra.xml file", e);
        }
    }

    private InputStream getInputStream(String raXmlFile) {
        InputStream inputStream = RaXmlReader.class.getResourceAsStream(raXmlFile);

        if (inputStream == null) {
            throw new CuckooException("The ra.xml file can not be loaded");
        }
        return inputStream;
    }

    public String getDescription() {
        return getStringValue("/connector/description");
    }

    public String getDisplayName() {
        return getStringValue("/connector/display-name");
    }

    public String getVendorName() {
        return getStringValue("/connector/vendor-name");
    }

    public String getEisType() {
        return getStringValue("/connector/eis-type");
    }

    public String getResourceAdapterVersion() {
        return getStringValue("/connector/resourceadapter-version");
    }

    public String getSpecVersion() {
        return getStringValue("/connector/@version");
    }

    private String getStringValue(String xpath) {
        try {
            XPathExpression xPathExpression = xPath.compile(xpath);
            String value = (String) xPathExpression.evaluate(xmlDocument, XPathConstants.STRING);

            if (value == null || value.length() == 0) {
                throw new CuckooException("Element '" + xpath + "' in ra.xml is empty or missing");
            }

            return value;
        } catch (XPathExpressionException e) {
            throw new CuckooException("Error parsing ra.xml file, XPath expression: '" + xpath + "'", e);
        }
    }

    private static class EntityResolverDummy implements EntityResolver {

        /**
         * Prevent validation to access external resources.
         */
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }
}
