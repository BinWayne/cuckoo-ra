package org.cuckoo.ra.jco;

import org.cuckoo.ra.spi.CuckooManagedConnectionImpl;

import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionException;
import com.sap.conn.jco.ext.SessionReferenceProvider;

public class CuckooJCoSessionReferenceProvider implements SessionReferenceProvider {
	public JCoSessionReference getCurrentSessionReference(String scopeType) {
		JCoSessionReference sessionRef = CuckooManagedConnectionImpl.getLocalSessionReference();
		if (sessionRef != null) {
			return sessionRef;
		}
		throw new RuntimeException("Unknown JCo session reference");
	}

	public boolean isSessionAlive(String sessionId) {
		for (CuckooJCoSessionReference ref : CuckooManagedConnectionImpl.getSessions()) {
			if (ref.getID().equals(sessionId)) {
				return true;
			}
		}
		return false;
	}

	public void jcoServerSessionContinued(String sessionId) throws SessionException {
	}

	public void jcoServerSessionFinished(String sessionId) {
	}

	public void jcoServerSessionPassivated(String sessionId) throws SessionException {
	}

	public CuckooJCoSessionReference jcoServerSessionStarted() throws SessionException {
		return null;
	}
}