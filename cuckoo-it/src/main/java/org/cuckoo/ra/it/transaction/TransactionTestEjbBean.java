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

import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.monitor.JCoDestinationMonitor;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.MappedRecord;

@Stateless
public class TransactionTestEjbBean implements TransactionTestEjb//, TransactionTestEjbRemote
{

    private static final String RA_JNDI_NAME = "java:/eis/sap/NSP";
    private static final Logger LOG = Logger.getLogger(TransactionTestEjbBean.class.getName());

    @Resource(mappedName = RA_JNDI_NAME)
    private ConnectionFactory cf;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public MappedRecord callFunctionWithoutTransaction(MappedRecord input) throws ResourceException {
        final Connection connection = cf.getConnection();
        try {
            return callSapFunction(connection, input);
        } finally {
            connection.close();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public MappedRecord callFunctionWithLocalTransaction(MappedRecord input) throws ResourceException {
        final Connection connection = cf.getConnection();
        LocalTransaction transaction = null;
        try {
            transaction = connection.getLocalTransaction();
            transaction.begin();
            final MappedRecord output = callSapFunction(connection, input);
            transaction.commit();
            return output;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Exception occurred", e);
        } finally {
            connection.close();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MappedRecord callFunctionWithContainerManagedTransaction(MappedRecord input) throws ResourceException {
        LOG.info("############## ejb=" + this);

        Connection connection = null;
        try {
            connection = cf.getConnection();
            return callSapFunction(connection, input);
        } catch (ResourceException e) {
            throw new RuntimeException("Error getting Connection", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
            monitorJCoConnections();
        }
    }

    private void monitorJCoConnections() {
        try {
            JCoDestinationMonitor monitor = JCoDestinationManager.getDestination("A12").getMonitor();
            StringBuilder sb = new StringBuilder("JCoDestinationMonitor[");
            sb.append("peakLimit=").append(monitor.getPeakLimit()).append(';');
            sb.append("maxUsedCount=").append(monitor.getMaxUsedCount()).append(';');
            sb.append("usedConnectionCount=").append(monitor.getUsedConnectionCount()).append(']');
            LOG.info(sb.toString());
        } catch (JCoException e) {
            e.printStackTrace();
        }
    }

    public MappedRecord callFunctionWithContainerManagedTransactionAndThrowRuntimeException(MappedRecord input)
            throws ResourceException {
        Connection connection = null;
        try {
            connection = cf.getConnection();
            callSapFunction(connection, input);
            throw new RuntimeException(
                    "Some error happened after calling SAP function, changes shall automatically be rolled back in SAP");
        } catch (ResourceException e) {
            throw new RuntimeException("Error getting Connection", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public MappedRecord callFunctionWithLocalTransactionAndThrowRuntimeException(MappedRecord input)
            throws ResourceException {
        final Connection connection = cf.getConnection();
        LocalTransaction transaction = null;
        try {
            transaction = connection.getLocalTransaction();
            transaction.begin();
            final MappedRecord output = callSapFunction(connection, input);
            callSapFunction(null, null);
            return output;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Exception occurred", e);
        } finally {
            connection.close();
        }
    }

    private MappedRecord callSapFunction(Connection connection, MappedRecord input) {
        try {
            final Interaction interaction = connection.createInteraction();
            try {
                return (MappedRecord) interaction.execute(null, input);
            } finally {
                interaction.close();
            }
        } catch (ResourceException e) {
            throw new RuntimeException("Error calling SAP system", e);
        }
    }
}