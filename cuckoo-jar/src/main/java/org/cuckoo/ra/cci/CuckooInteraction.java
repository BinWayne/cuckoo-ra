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
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

public class CuckooInteraction implements Interaction {

    private static final Logger LOG = Logger.getLogger(CuckooInteraction.class.getName());

    private CuckooConnection connection;

    private boolean closed = false;

    CuckooInteraction(CuckooConnection cuckooCciConnection) {
        LOG.entering("CuckooInteraction", "CuckooInteraction(CuckooConnection)");
        connection = cuckooCciConnection;
    }

    public void close() throws ResourceException {
        LOG.entering("CuckooInteraction", "close()");
        assertNotClosed();
        closed = true;
    }

    public Connection getConnection() {
        LOG.entering("CuckooInteraction", "getConnection()");
        return connection;
    }

    public boolean execute(final InteractionSpec ispec, final Record input, final Record output)
            throws ResourceException {
        LOG.entering("CuckooInteraction", "execute(InteractionSpec, Record, Record)");
        throw new NotSupportedException("This method is not supported. Please use execute(InteractionSpec, Record)");
    }

    public Record execute(final InteractionSpec interactionSpec, final Record inputRecord) throws ResourceException {
        LOG.entering("CuckooInteraction", "execute(InteractionSpec, Record)");

        assertNotClosed();
        assertCorrectType(interactionSpec);
        assertCorrectType(inputRecord);

        CuckooInteractionSpec cuckooInteractionSpec = (CuckooInteractionSpec) interactionSpec;
        MappedRecord mappedRecord = (MappedRecord) inputRecord;

        final String functionName = getFunctionName(cuckooInteractionSpec, mappedRecord);

        return connection.executeFunction(functionName, inputRecord);
    }

    private void assertCorrectType(Record inputRecord)
            throws ResourceException {
        if (inputRecord == null) {
            throw new ResourceException("Input record must not be null");
        }
        if (!(inputRecord instanceof MappedRecord)) {
            throw new ResourceException("Input record must be of type " + MappedRecord.class.getName());
        }
    }

    public ResourceWarning getWarnings() throws ResourceException {
        LOG.entering("CuckooInteraction", "getWarnings()");
        return null;
    }

    public void clearWarnings() throws ResourceException {
        LOG.entering("CuckooInteraction", "clearWarnings()");
        // nothing to do
    }

    /**
     * If an InteractionSpec is given, get function name from there, else get it from record name of the inputRecord.
     * Using the inputRecord name has the advantage that the client does not need a dependency to the resource adapter implementation.
     *
     * @param interactionSpec An object of type CuckooInteractionSpec
     * @param inputRecord The input record
     * @return The name of the SAP function module
     */
    private String getFunctionName(final CuckooInteractionSpec interactionSpec, final MappedRecord inputRecord) {
        if (interactionSpec != null) {
            return interactionSpec.getFunctionName();
        } else {
            return inputRecord.getRecordName();
        }
    }

    private void assertCorrectType(final InteractionSpec interactionSpec) throws ResourceException {
        if (interactionSpec != null && !(interactionSpec instanceof CuckooInteractionSpec)) {
            throw new ResourceException("interactionSpec must be of type " + CuckooInteractionSpec.class.getName());
        }
    }

    private void assertNotClosed() throws ResourceException {
        if (closed) {
            throw new ResourceException("Interaction was already closed");
        }
    }
}
