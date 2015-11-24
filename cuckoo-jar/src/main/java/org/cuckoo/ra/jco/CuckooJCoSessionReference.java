package org.cuckoo.ra.jco;

import java.util.concurrent.atomic.AtomicInteger;

import com.sap.conn.jco.ext.JCoSessionReference;

public class CuckooJCoSessionReference implements JCoSessionReference {
	static AtomicInteger atomicIntId = new AtomicInteger(0);
	private String id = "session-" + String.valueOf(atomicIntId.addAndGet(1));

	public void contextFinished() {
		// Do nothing
	}

	public void contextStarted() {
		// Do Nothing
	}

	public String getID() {
		return id;
	}
}