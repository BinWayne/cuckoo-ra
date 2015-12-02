/*
 * Copyright (C) 2010 akquinet tech@spree GmbH
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
package org.cuckoo.ra.jco;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.spi.LocalTransactionException;

import org.cuckoo.ra.cci.ApplicationProperties;
import org.cuckoo.ra.cci.CuckooMappedRecord;
import org.cuckoo.ra.common.CuckooConnectionMetaData;

import com.sap.conn.jco.JCoAttributes;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoCustomDestination;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

public class JCoAdapter {
	private static final Logger LOG = Logger.getLogger(JCoAdapter.class.getName());

	private static final String OUTPUT_RECORD_NAME = "EXPORT";

	/**
	 * @see com.sap.conn.jco.JCoAttributes#getPartnerType()
	 */
	private static final HashMap<Character, String> PARTNER_TYPE_TO_PRODUCT_NAME_MAPPING = new HashMap<Character, String>() {
		{
			put('2', "SAP R/2");
			put('3', "SAP R/3");
			put('E', "External Program");
			put('R', "Registered external program");
			put('F', "Shared memory pipe");
		}
	};

	private final JCoDestination destination;

	private final JCoRecordMapper mapper = new JCoRecordMapper();

	public JCoAdapter(String destinationName, ApplicationProperties applicationProperties) throws ResourceException {
		LOG.entering("JCoAdapter", "JCoAdapter( " + destinationName + " )");

		if (applicationProperties == null) {
			// use the logon properties as configured for the resource adapter
			// or overwritten by
			// the managed connection factory
			destination = getDestination(destinationName);
		} else {
			// use the application provided logon properties and/or the
			// credentials
			// from container-managed security setup.
			destination = getDestination(destinationName).createCustomDestination();
			setCustomLogonData(((JCoCustomDestination) destination).getUserLogonData(), applicationProperties);
		}

		try {
			CuckooJCoSessionReference pingSessionReference = new CuckooJCoSessionReference();
			CuckooJCoSessionTracker.setJCoSessionReference(pingSessionReference);

			LOG.fine("Created session " + pingSessionReference.getID() + " to call destination.ping()");
			destination.ping();

			CuckooJCoSessionTracker.removeJCoSessionReference(pingSessionReference);
			LOG.fine("Removed " + pingSessionReference.getID());
		} catch (JCoException e) {
			if (e.getGroup() == JCoException.JCO_ERROR_LOGON_FAILURE) {
				throw new javax.resource.spi.SecurityException("Failed logging on to SAP", e);
			}
			throw new ResourceException("Error getting Destination", e);
		}
	}

	private JCoDestination getDestination(String destinationName) throws ResourceException {
		LOG.entering("JCoAdapter", "getDestination( " + destinationName + " )");
		try {
			return JCoDestinationManager.getDestination(destinationName);
		} catch (JCoException e) {
			throw new ResourceException("Error getting Destination", e);
		}
	}

	public void disconnect() throws ResourceException {
		LOG.entering("JCoAdapter", "disconnect()");

		// TODO: do we need this?
		if (JCoContext.isStateful(destination)) {
			LOG.warning("JCo destination is still stateful. Ending stateful destination now...");
			try {
				JCoContext.end(destination);
			} catch (JCoException e) {
				throw new ResourceException("Error ending stateful JCo session");
			}
		}
	}

	public MappedRecord executeFunction(String functionName, Record inputRecord) throws ResourceException {
		LOG.info("JCoAdapter.executeFunction( " + functionName + ", " + inputRecord + " )");

		try {
			LOG.finest("Getting JCo repository for destination: " + destination);
			final JCoFunction function = destination.getRepository().getFunction(functionName);

			final JCoParameterList importTableList = function.getTableParameterList();
			final JCoParameterList importList = function.getImportParameterList();
			mapper.populateImportRecord(importList, importTableList, (MappedRecord) inputRecord);

			LOG.finest("function before execute: " + function);

			function.execute(destination);

			LOG.finest("function after execute: " + function);

			mapper.checkForAbapExceptions(function);
			final CuckooMappedRecord outputRecord = new CuckooMappedRecord(OUTPUT_RECORD_NAME);
			mapper.convertExportParameters(function, outputRecord);
			mapper.convertExportTable(function, outputRecord);

			return outputRecord;
		} catch (JCoException e) {
			throw new ResourceException("Error executing JCo function", e);
		}
	}

	public CuckooConnectionMetaData createConnectionMetaData() throws ResourceException {
		LOG.entering("JCoAdapter", "createConnectionMetaData()");

		try {
			final JCoAttributes attributes = destination.getAttributes();
			String productName = determineProductName(attributes.getPartnerType());
			String eisProductVersion = "" + attributes.getPartnerReleaseNumber();
			int maxConnections = destination.getPoolCapacity();
			String userName = attributes.getUser();
			return new CuckooConnectionMetaData(productName, eisProductVersion, maxConnections, userName);
		} catch (JCoException e) {
			throw new ResourceException("Error getting connection meta data", e);
		}
	}

	private void setCustomLogonData(JCoCustomDestination.UserData logonData,
			ApplicationProperties applicationProperties) {
		String aliasUser = applicationProperties.getAliasUser();
		if (aliasUser != null) {
			logonData.setAliasUser(aliasUser);
		}
		String client = applicationProperties.getClient();
		if (client != null) {
			logonData.setClient(client);
		}
		String language = applicationProperties.getLanguage();
		if (language != null) {
			logonData.setLanguage(language);
		}
		String password = applicationProperties.getPassword();
		if (password != null) {
			logonData.setPassword(password);
		}
		String ssoTicket = applicationProperties.getSsoTicket();
		if (ssoTicket != null) {
			logonData.setSSOTicket(ssoTicket);
		}
		String user = applicationProperties.getUser();
		if (user != null) {
			logonData.setUser(user);
		}
		String x509Certificate = applicationProperties.getX509Certificate();
		if (x509Certificate != null) {
			logonData.setX509Certificate(x509Certificate);
		}
	}

	private String determineProductName(char partnerType) {
		String productName = PARTNER_TYPE_TO_PRODUCT_NAME_MAPPING.get(partnerType);
		if (productName == null) {
			productName = "Unknown (" + partnerType + ")";
		}
		return productName;
	}

	public void startTransaction() {
		LOG.finer("Starting stateful SAP session for destination " + destination.getDestinationName());

		JCoContext.begin(destination);
	}

	public void commitTransaction() throws LocalTransactionException {
		LOG.finer("Committing transaction in SAP on destination " + destination.getDestinationName());
		try {
			JCoFunction commitFunction = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT");
			commitFunction.getImportParameterList().setValue("WAIT", "X");
			commitFunction.execute(destination);
			assertNoErrorOccurredDuringCommit(commitFunction);
		} catch (JCoException e) {
			throw new LocalTransactionException("Error committing transaction in SAP", e);
		} finally {
			endStatefulSession();
		}
	}

	private void assertNoErrorOccurredDuringCommit(JCoFunction commitFunction) throws LocalTransactionException {
		final JCoStructure returnStructure = commitFunction.getExportParameterList().getStructure("RETURN");
		String returnType = returnStructure.getString("TYPE");
		if ("E".equals(returnType) || "A".equals(returnType)) {
			String returnMessage = returnStructure.getString("MESSAGE");
			throw new LocalTransactionException("Error committing transaction in SAP. Return type: " + returnType
					+ ", return message: " + returnMessage);
		}
	}

	public void rollbackTransaction() throws LocalTransactionException {
		LOG.finer("Rolling back transaction in SAP on destination " + destination.getDestinationName());
		try {
			JCoFunction rollbackFunction = destination.getRepository().getFunction("BAPI_TRANSACTION_ROLLBACK");
			rollbackFunction.execute(destination);
			// No need to check the RETURN values here. From SAP documentation:
			// "No messages are returned, if an error occurs. If the ROLLBACK
			// WORK command is not successfully executed,
			// the system crashes." :-)
		} catch (JCoException e) {
			throw new LocalTransactionException("Error committing transaction in SAP", e);
		} finally {
			endStatefulSession();
		}
	}

	private void endStatefulSession() throws LocalTransactionException {
		LOG.info("Ending stateful SAP session for destination " + destination.getDestinationName());

		if (!JCoContext.isStateful(destination)) {
			throw new LocalTransactionException("Current JCo session is not stateful");
		}

		try {
			JCoContext.end(destination);
		} catch (JCoException e) {
			throw new LocalTransactionException("Error ending stateful JCo session after commit/rollback", e);
		}
	}
}