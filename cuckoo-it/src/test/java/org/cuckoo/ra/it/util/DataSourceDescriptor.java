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
package org.cuckoo.ra.it.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.DescriptorExportException;

public class DataSourceDescriptor implements Descriptor {

    private String destinationFileName;
    private String fileContents;

    public DataSourceDescriptor(String resourceFileName, String destinationFileName) {
        this.destinationFileName = destinationFileName;
        fileContents = readFile(resourceFileName);
    }

    private String readFile(String resourceFileName) {
        InputStream inputStream = DataSourceDescriptor.class.getResourceAsStream(resourceFileName);
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
    }

    public String getDescriptorName() {
        return destinationFileName;
    }

    public String exportAsString() throws DescriptorExportException {
        return fileContents;
    }

    public void exportTo(OutputStream outputStream) throws DescriptorExportException, IllegalArgumentException {
        try {
            outputStream.write(fileContents.getBytes());
        } catch (IOException e) {
            throw new DescriptorExportException("Error writing file contents to OutputStream", e);
        }
    }
}
