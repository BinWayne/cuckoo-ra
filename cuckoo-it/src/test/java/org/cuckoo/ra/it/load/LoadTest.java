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
package org.cuckoo.ra.it.load;

import java.util.HashSet;
import java.util.Set;
import javax.naming.InitialContext;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import org.cuckoo.ra.cci.CuckooIndexedRecord;
import org.cuckoo.ra.cci.CuckooMappedRecord;
import org.cuckoo.ra.it.transaction.TransactionTestEjb;
import org.cuckoo.ra.it.transaction.TransactionTestEjbBean;
import org.cuckoo.ra.it.transaction.TransactionTestEjbRemote;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cuckoo.ra.it.util.ArquillianHelper.createEar;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class LoadTest {

    private static final String CUSTOMER_NUMBER = "00000001";

    private static final int NUMBER_OF_CONCURRENT_CLIENTS = 60;
    private static final int TIME_TO_WAIT_BETWEEN_CALLS = 50;

    private Set<Throwable> errors = new HashSet<Throwable>();


/*
    @Deployment( order = 1, testable = false )
    public static Archive createRarDeployment()
    {
        return createRar();
    }
*/

    @Deployment(order = 2)
    public static EnterpriseArchive createDeployment() {
        final JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, "rartest.jar")
                .addClasses(TransactionTestEjb.class, TransactionTestEjbRemote.class, TransactionTestEjbBean.class,
                        LoadTest.class);

        return createEar(testJar, "/jboss5/cuckoo-jboss-ds.xml");
    }

    @Test
    @RunAsClient
    public void testLoad() throws InterruptedException {
        Thread[] threads = new Thread[NUMBER_OF_CONCURRENT_CLIENTS];

        for (int i = 0; i < NUMBER_OF_CONCURRENT_CLIENTS; i++) {
            SapClient sapClient = new SapClient("SapClient-" + i);
            threads[i] = sapClient;
            sapClient.start();

            // Wait a moment, else SAP would refuse connections on a small system...
            Thread.sleep(TIME_TO_WAIT_BETWEEN_CALLS);
        }
        for (int i = 0; i < NUMBER_OF_CONCURRENT_CLIENTS; i++) {
            threads[i].join();
        }

        System.out.println("##### Test fertig #####");

        assertThat(errors.size(), is(0));
    }

    private class SapClient extends Thread {

        private SapClient(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                callSapFunction();
            } catch (Throwable t) {
                errors.add(t);
                t.printStackTrace();
            }
        }

        private void callSapFunction() throws Exception {
            System.out.println("##### " + Thread.currentThread().getName() + " start #####");
            InitialContext ctx = new InitialContext();
            TransactionTestEjbRemote ejb = (TransactionTestEjbRemote) ctx
                    .lookup("test/TransactionTestEjbBean/remote");
            ctx.close();

            MappedRecord input = createInputRecordForGettingCustomerData();
            MappedRecord output = ejb.callFunctionWithContainerManagedTransaction(input);

            assertNoSapError(output);
            System.out.println("##### " + Thread.currentThread().getName() + " ende #####");
        }

        @SuppressWarnings("unchecked")
        private MappedRecord createInputRecordForGettingCustomerData() {
            final MappedRecord record = new CuckooMappedRecord("BAPI_FLCUST_GETLIST");
            final IndexedRecord customerRangeTable = new CuckooIndexedRecord("CUSTOMER_RANGE");
            record.put("CUSTOMER_RANGE", customerRangeTable);
            final MappedRecord customerRange = new CuckooMappedRecord("CUSTOMER_RANGE");
            customerRangeTable.add(customerRange);
            customerRange.put("SIGN", "I");
            customerRange.put("OPTION", "EQ");
            customerRange.put("LOW", CUSTOMER_NUMBER);
            return record;
        }

        private void assertNoSapError(MappedRecord result) {
            final CuckooIndexedRecord returnTable = (CuckooIndexedRecord) result.get("RETURN");
            for (Object line : returnTable) {
                CuckooMappedRecord returnStructure = (CuckooMappedRecord) line;
                final Object errorType = returnStructure.get("TYPE");
                if ("E".equals(errorType) || "A".equals(errorType)) {
                    throw new AssertionError("SAP returned Error of type " + errorType + " with message:" +
                            returnStructure.get("MESSAGE"));
                }
            }
        }
    }
}

