package org.cuckoo.ra.jco;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.sap.conn.jco.ext.JCoSessionReference;

public class CuckooJCoSessionReference implements JCoSessionReference {

	private static final Logger LOG = Logger.getLogger(CuckooJCoSessionTracker.class.getName());

	private static AtomicInteger atomicIntId = new AtomicInteger(0);
	private String id = "session-" + String.valueOf(atomicIntId.addAndGet(1));

	public void contextFinished() {
		LOG.entering("CuckooJCoSessionReference", "contextFinished");
		LOG.fine("ContextFinished so removing session: " + id);
		CuckooJCoSessionTracker.removeJCoSessionReference(this);
	}

	public void contextStarted() {
		LOG.entering("CuckooJCoSessionReference", "contextStarted");
		LOG.fine("ContextStarted for session: " + id);
		CuckooJCoSessionTracker.setJCoSessionReference(this);
	}

	public String getID() {
		return id;
	}
}