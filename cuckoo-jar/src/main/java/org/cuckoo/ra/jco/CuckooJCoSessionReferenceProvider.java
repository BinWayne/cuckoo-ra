package org.cuckoo.ra.jco;

import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionException;
import com.sap.conn.jco.ext.SessionReferenceProvider;

public class CuckooJCoSessionReferenceProvider implements SessionReferenceProvider {

	public JCoSessionReference getCurrentSessionReference(String scopeType) {
		JCoSessionReference sessionRef = CuckooJCoSessionTracker.getJCoSessionReference();
		if (sessionRef != null) {
			return sessionRef;
		}
		throw new RuntimeException("Unknown JCo session reference");
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