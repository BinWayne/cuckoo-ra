package org.cuckoo.ra.jco;

import java.util.logging.Logger;

import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionException;
import com.sap.conn.jco.ext.SessionReferenceProvider;

public class CuckooJCoSessionReferenceProvider implements SessionReferenceProvider {

	private static final Logger LOG = Logger.getLogger(CuckooJCoSessionReferenceProvider.class.getName());

	public JCoSessionReference getCurrentSessionReference(String scopeType) {
		LOG.fine("Active Sessions: " + CuckooJCoSessionTracker.getSessions().toString());

		JCoSessionReference sessionRef = CuckooJCoSessionTracker.getJCoSessionReference();
		if (sessionRef != null) {
			LOG.fine("Found a session reference - " + sessionRef.getID());
			return sessionRef;
		}

		return null;
	}

	public boolean isSessionAlive(String sessionId) {
		return CuckooJCoSessionTracker.isSessionAlive(sessionId);
	}

	public void jcoServerSessionContinued(String sessionId) throws SessionException {
		// Inbound not implemented
	}

	public void jcoServerSessionFinished(String sessionId) {
		// Inbound not implemented
	}

	public void jcoServerSessionPassivated(String sessionId) throws SessionException {
		// Inbound not implemented
	}

	public JCoSessionReference jcoServerSessionStarted() throws SessionException {
		// Inbound not implemented
		return null;
	}
}