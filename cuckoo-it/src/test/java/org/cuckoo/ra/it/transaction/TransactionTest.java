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
package org.cuckoo.ra.it.transaction;

import java.util.Date;
import javax.ejb.EJB;
import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import org.cuckoo.ra.cci.CuckooIndexedRecord;
import org.cuckoo.ra.cci.CuckooMappedRecord;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cuckoo.ra.it.util.ArquillianHelper.createEar;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class TransactionTest {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTest.class.getName());

    private static final String CUSTOMER_NUMBER = "00000001";

    @EJB
    private TransactionTestEjb ejb;

//    @Deployment( order = 1, testable = false )
//    public static Archive createRarDeployment()
//    {
//        return createRar();
//    }

    @Deployment //( name = "ear", order = 2 )
    public static EnterpriseArchive createEarDeployment() {
        final JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, "rartest.jar")
                .addClasses(
                        TransactionTestEjb.class, TransactionTestEjbRemote.class,
                        TransactionTestEjbBean.class, TransactionTest.class);

        return createEar(testJar, "/jboss5/cuckoo-jboss-ds.xml");
    }

    @Test
    //@OperateOnDeployment( "ear" )
    public void autoCommitsWhenCalledWithoutTransaction() throws ResourceException {
        // Changing phone number to new value...
        final String newPhoneNo = "" + System.currentTimeMillis();
        LOG.info("testTransactionalCallWithoutTransaction(): changing phone number to: " + newPhoneNo);
        MappedRecord record = createInputRecordForChangingCustomerPhoneNumber(newPhoneNo);
        MappedRecord result = ejb.callFunctionWithoutTransaction(record);

        assertNoSapError(result);

        // Test: Phone number should be changed in SAP
        record = createInputRecordForGettingCustomerData();
        result = ejb.callFunctionWithoutTransaction(record);

        assertNoSapError(result);

        final IndexedRecord customerList = (IndexedRecord) result.get("CUSTOMER_LIST");
        assertThat(customerList.size(), is(1));
        final MappedRecord customerData = (MappedRecord) customerList.get(0);
        assertThat((String) customerData.get("PHONE"), equalTo(newPhoneNo));
    }

    @Test
    //@OperateOnDeployment( "ear" )
    public void commitsWhenCalledWithContainerManagedTransaction() throws ResourceException {
        // Changing phone number to new value...
        final String newPhoneNo = "" + System.currentTimeMillis();
        LOG.info("testTransactionalCallWithCMT(): changing phone number to: " + newPhoneNo);
        MappedRecord record = createInputRecordForChangingCustomerPhoneNumber(newPhoneNo);
        MappedRecord result = ejb.callFunctionWithContainerManagedTransaction(record);

        assertNoSapError(result);

        // Test: was it really changed and committed?
        record = createInputRecordForGettingCustomerData();
        result = ejb.callFunctionWithoutTransaction(record);

        assertNoSapError(result);

        final IndexedRecord customerList = (IndexedRecord) result.get("CUSTOMER_LIST");
        assertThat(customerList.size(), is(1));
        final MappedRecord customerData = (MappedRecord) customerList.get(0);
        assertThat((String) customerData.get("PHONE"), equalTo(newPhoneNo));
    }

    @Test
    //@OperateOnDeployment( "ear" )
    public void rollsBackWhenCallWithContainerManagedTransactionAndAnErrorHappens() throws ResourceException {
        // Changing phone number to new value...
        final String newPhoneNo = "" + System.currentTimeMillis();
        LOG.info("testTransactionalCallWithCMT(): changing phone number to: " + newPhoneNo);
        MappedRecord record = createInputRecordForChangingCustomerPhoneNumber(newPhoneNo);
        try {
            ejb.callFunctionWithContainerManagedTransactionAndThrowRuntimeException(record);
            fail();
        } catch (RuntimeException e) {
            e.printStackTrace();
            // expected
        }
        // Test: changes should have been rolled back in SAP
        record = createInputRecordForGettingCustomerData();
        MappedRecord result = ejb.callFunctionWithoutTransaction(record);

        assertNoSapError(result);

        final IndexedRecord customerList = (IndexedRecord) result.get("CUSTOMER_LIST");
        assertThat(customerList.size(), is(1));
        final MappedRecord customerData = (MappedRecord) customerList.get(0);
        assertThat((String) customerData.get("PHONE"), not(equalTo(newPhoneNo)));
    }

    @Test
    //@OperateOnDeployment( "ear" )
    public void commitsWhenCalledWithLocalTransaction() throws ResourceException {
        // Changing phone number to new value...
        final String newPhoneNo = "" + new Date().getTime();
        LOG.info("testTransactionalCallWithLocalTransaction(): changing phone number to: " + newPhoneNo);
        MappedRecord record = createInputRecordForChangingCustomerPhoneNumber(newPhoneNo);
        MappedRecord result = ejb.callFunctionWithLocalTransaction(record);

        assertNoSapError(result);

        // Test: was it really changes and committed?
        record = createInputRecordForGettingCustomerData();
        result = ejb.callFunctionWithLocalTransaction(record);

        assertNoSapError(result);

        final IndexedRecord customerList = (IndexedRecord) result.get("CUSTOMER_LIST");
        assertThat(customerList.size(), is(1));
        final MappedRecord customerData = (MappedRecord) customerList.get(0);
        assertThat((String) customerData.get("PHONE"), equalTo(newPhoneNo));
    }

    @Test
    //@OperateOnDeployment( "ear" )
    public void rollsBackWhenCalledWithLocalTransactionAndErrorHappens() throws ResourceException {
        // Changing phone number to new value...
        final String newPhoneNo = "" + new Date().getTime();
        LOG.info("testTransactionalCallWithLocalTransaction(): changing phone number to: " + newPhoneNo);
        MappedRecord record = createInputRecordForChangingCustomerPhoneNumber(newPhoneNo);

        try {
            ejb.callFunctionWithLocalTransactionAndThrowRuntimeException(record);
            fail();
        } catch (RuntimeException e) {
            // expected
        }

        // Test: was it really changes and committed?
        record = createInputRecordForGettingCustomerData();
        MappedRecord result = ejb.callFunctionWithLocalTransaction(record);

        assertNoSapError(result);

        final IndexedRecord customerList = (IndexedRecord) result.get("CUSTOMER_LIST");
        assertThat(customerList.size(), is(1));
        final MappedRecord customerData = (MappedRecord) customerList.get(0);
        assertThat((String) customerData.get("PHONE"), not(equalTo(newPhoneNo)));
    }

    @SuppressWarnings("unchecked")
    private MappedRecord createInputRecordForChangingCustomerPhoneNumber(String newPhoneNo) {
        final MappedRecord record = new CuckooMappedRecord("BAPI_FLCUST_CHANGE");
        record.put("CUSTOMERNUMBER", CUSTOMER_NUMBER);
        final MappedRecord customerData = new CuckooMappedRecord("CUSTOMER_DATA");
        record.put("CUSTOMER_DATA", customerData);
        customerData.put("PHONE", newPhoneNo);
        final MappedRecord customerDataX = new CuckooMappedRecord("CUSTOMER_DATA_X");
        record.put("CUSTOMER_DATA_X", customerDataX);
        customerDataX.put("PHONE", "X");
        return record;
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
