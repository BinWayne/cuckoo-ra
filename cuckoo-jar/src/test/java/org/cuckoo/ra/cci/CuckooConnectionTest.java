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

import javax.resource.ResourceException;
import org.assertj.core.api.Assertions;
import org.cuckoo.ra.spi.CuckooManagedConnection;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class CuckooConnectionTest {

    private final CuckooManagedConnection managedConnection = createNiceMock(CuckooManagedConnection.class);

    private final CuckooConnection connection = new CuckooConnection(managedConnection);

    @Test
    public void closeNotifiesManagedConnection() throws Exception {
        managedConnection.notifyConnectionClosed(EasyMock.<CuckooConnection>anyObject());
        replay(managedConnection);

        connection.close();

        verify(managedConnection);
    }

    @Test
    public void closeThrowsResourceExceptionWhenConnectionIsAlreadyClosed() throws ResourceException {
        connection.close();

        try {
            connection.close();
            Assertions.fail("should throw exception");
        } catch (ResourceException e) {
            // expected
        }
    }
}
