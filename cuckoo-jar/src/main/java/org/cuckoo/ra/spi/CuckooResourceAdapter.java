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
package org.cuckoo.ra.spi;

import java.util.Properties;
import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.cuckoo.ra.cci.CuckooRaMetaData;
import org.cuckoo.ra.cci.CuckooRecordFactory;
import org.cuckoo.ra.jco.CuckooDestinationDataProvider;
import org.cuckoo.ra.jco.CuckooJCoSessionReferenceProvider;

import com.sap.conn.jco.ext.Environment;

public final class CuckooResourceAdapter implements ResourceAdapter {
	private static final Logger LOG = Logger.getLogger(CuckooResourceAdapter.class.getName());

	private final CuckooDestinationDataProvider destinationDataProvider;
	private final CuckooRaMetaData resourceAdapterMetaData;
	private final CuckooRecordFactory recordFactory;
	private final CuckooJCoSessionReferenceProvider sapSessionReferenceProvider = new CuckooJCoSessionReferenceProvider();

	public CuckooResourceAdapter() {
		LOG.entering("CuckooResourceAdapter", "CuckooResourceAdapter()");
		resourceAdapterMetaData = new CuckooRaMetaData();
		destinationDataProvider = new CuckooDestinationDataProvider();
		recordFactory = new CuckooRecordFactory();
	}

	/**
	 * This is called during the activation of a message endpoint.
	 *
	 * @param endpointFactory
	 *            a message endpoint factory instance.
	 * @param spec
	 *            an activation spec JavaBean instance.
	 * @throws ResourceException
	 *             generic exception
	 */
	public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec)
			throws ResourceException {
		LOG.entering("CuckooResourceAdapter", "endpointActivation(MessageEndpointFactory, ActivationSpec)");
		throw new NotSupportedException("Inbound calls are not yet implemented");
	}

	/**
	 * This is called when a message endpoint is deactivated.
	 *
	 * @param endpointFactory
	 *            a message endpoint factory instance.
	 * @param spec
	 *            an activation spec JavaBean instance.
	 */
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		LOG.entering("CuckooResourceAdapter", "endpointDeactivation(MessageEndpointFactory, ActivationSpec)");
	}

	/**
	 * This is called when a resource adapter instance is bootstrapped.
	 *
	 * @param ctx
	 *            a bootstrap context containing references
	 * @throws ResourceAdapterInternalException
	 *             indicates bootstrap failure.
	 */
	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
		LOG.info("Starting " + resourceAdapterMetaData.getAdapterName() + " Version "
				+ resourceAdapterMetaData.getAdapterVersion());
		LOG.finer("java.library.path=" + System.getProperty("java.library.path"));
		Environment.registerDestinationDataProvider(destinationDataProvider);
		Environment.registerSessionReferenceProvider(sapSessionReferenceProvider);
	}

	/**
	 * This is called when a resource adapter instance is undeployed or during
	 * application server shutdown.
	 */
	public void stop() {
		LOG.info("Stopping " + resourceAdapterMetaData.getAdapterName() + " Version "
				+ resourceAdapterMetaData.getAdapterVersion());

		destinationDataProvider.removeAllDestinations();
		Environment.unregisterDestinationDataProvider(destinationDataProvider);
		Environment.unregisterSessionReferenceProvider(sapSessionReferenceProvider);
	}

	/**
	 * This method is called by the application server during crash recovery.
	 *
	 * @param specs
	 *            an array of ActivationSpec JavaBeans
	 * @return an array of XAResource objects
	 * @throws ResourceException
	 *             generic exception
	 */
	public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
		LOG.entering("CuckooResourceAdapter", "getXAResources(ActivationSpec[])");
		throw new NotSupportedException("XA-Resources are not supported");
	}

	void registerDestination(String destinationName, Properties properties) throws ResourceException {
		LOG.info("Cuckoo-RA: registering JCo destination '" + destinationName + "'");

		if (destinationDataProvider.wasAdded(destinationName)) {
			// TODO when upgrading the ra to JCA 1.6, lifecycle methods should
			// be used to remove a JCo destination
			// when a MCF config is undeployed. There is no way to get noticed
			// about this before 1.6.
			LOG.warning("A JCo destination with destinationName '" + destinationName + "' was already registered. "
					+ "This should only happen when redeploying the ManagedConnectionFactory configuration.");
			destinationDataProvider.removeDestination(destinationName);
		}

		destinationDataProvider.addDestination(destinationName, properties);
	}

	public CuckooRaMetaData getResourceAdapterMetaData() {
		LOG.entering("CuckooResourceAdapter", "getResourceAdapterMetaData()");

		return resourceAdapterMetaData;
	}

	public CuckooRecordFactory getRecordFactory() {
		LOG.entering("CuckooResourceAdapter", "getRecordFactory()");

		return recordFactory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CuckooResourceAdapter that = (CuckooResourceAdapter) o;

		if (destinationDataProvider != null ? !destinationDataProvider.equals(that.destinationDataProvider)
				: that.destinationDataProvider != null) {
			return false;
		}
		if (recordFactory != null ? !recordFactory.equals(that.recordFactory) : that.recordFactory != null) {
			return false;
		}
		// noinspection RedundantIfStatement
		if (resourceAdapterMetaData != null ? !resourceAdapterMetaData.equals(that.resourceAdapterMetaData)
				: that.resourceAdapterMetaData != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = destinationDataProvider != null ? destinationDataProvider.hashCode() : 0;
		result = 31 * result + (resourceAdapterMetaData != null ? resourceAdapterMetaData.hashCode() : 0);
		result = 31 * result + (recordFactory != null ? recordFactory.hashCode() : 0);
		return result;
	}
}
