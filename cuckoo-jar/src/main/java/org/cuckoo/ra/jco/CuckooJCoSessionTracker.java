package org.cuckoo.ra.jco;

import java.util.Hashtable;
import java.util.logging.Logger;

public class CuckooJCoSessionTracker {

	private static final Logger LOG = Logger.getLogger(CuckooJCoSessionTracker.class.getName());

	private static ThreadLocal<CuckooJCoSessionReference> jcoSessionReference = new ThreadLocal<CuckooJCoSessionReference>();
	private static Hashtable<String, CuckooJCoSessionReference> sessions = new Hashtable<String, CuckooJCoSessionReference>();

	public static CuckooJCoSessionReference getJCoSessionReference() {
		return jcoSessionReference.get();
	}

	public static void setJCoSessionReference(CuckooJCoSessionReference sessionReference) {
		LOG.entering("CuckooJCoSessionTracker", "setJCoSessionReference");

		if (sessionReference != null) {
			jcoSessionReference.set(sessionReference);
			sessions.put(sessionReference.getID(), sessionReference);
		}
	}

	public static void removeJCoSessionReference(CuckooJCoSessionReference sessionReference) {
		LOG.entering("CuckooJCoSessionTracker", "removeJCoSessionReference");

		if (sessionReference != null) {
			jcoSessionReference.set(null);
			sessions.remove(sessionReference.getID());
		}
	}

	public static Hashtable<String, CuckooJCoSessionReference> getSessions() {
		return sessions;
	}

	public static boolean isSessionAlive(String sessionId) {
		LOG.entering("CuckooJCoSessionTracker", "isSessionAlive");

		CuckooJCoSessionReference sessionReference = sessions.get(sessionId);
		if (sessionReference != null) {
			return true;
		}
		LOG.fine(sessionId + " has already been removed.");
		return false;
	}
}