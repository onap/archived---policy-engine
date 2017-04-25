package org.openecomp.policy.api;

/**
 * PolicyException extends <code>Exception</code> to implement exceptions thrown by {@link org.openecomp.policy.api.PolicyEngine}
 * 
 * @version 0.1
 */
public class PolicyException extends Exception {
	private static final long serialVersionUID = -5006203722296799708L;
	
	public PolicyException() {
	}
	
	public PolicyException(String message) {
		super(message);
	}
	
	public PolicyException(Throwable cause){
		super(cause);
	}
	
	public PolicyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PolicyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
