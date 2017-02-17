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

import org.cuckoo.ra.spi.CuckooManagedConnection;
import org.junit.Test;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class CuckooCciLocalTransactionTest {

    private CuckooManagedConnection managedConnection = createMock(CuckooManagedConnection.class);

    private CuckooCciLocalTransaction cciTransaction = new CuckooCciLocalTransaction(managedConnection, null);

    @Test
    public void transactionGetsStartedOnManagedConnectionWhenBeginIsCalled() throws Exception {
        managedConnection.beginLocalTransaction(null);
        replay(managedConnection);

        cciTransaction.begin();

        verify(managedConnection);
    }

    @Test
    public void transactionGetsCommittedOnManagedConnectionWhenCommitIsCalled() throws Exception {
        managedConnection.commitLocalTransaction(null);
        replay(managedConnection);

        cciTransaction.commit();

        verify(managedConnection);
    }

    @Test
    public void transactionGetsRolledBackOnManagedConnectionWhenRollbackIsCalled() throws Exception {
        managedConnection.rollbackLocalTransaction(null);
        replay(managedConnection);

        cciTransaction.rollback();

        verify(managedConnection);
    }
}
