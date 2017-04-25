package org.openecomp.policy.api;

import java.util.UUID;

public class MetricsRequestParameters {
	private UUID requestID;
	
	/**
	 * @return the requestID
	 */
	public UUID getRequestID() {
		return requestID;
	}
	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(UUID requestID) {
		this.requestID = requestID;
	}

}
