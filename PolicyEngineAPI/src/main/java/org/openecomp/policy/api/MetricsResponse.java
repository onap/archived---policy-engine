package org.openecomp.policy.api;

public interface MetricsResponse {

	/**
	 * Gets the <code>String</code> of the metrics message from <code>MetricsResponse</code>.
	 * 
	 * @return the <code>String</code> which consists of the metrics message from <code>MetricsResponse</code>
	 */
	public String getResponseMessage();
	
	/**
	 * Gets the response code of type <code>Integer</code> which corresponds to the HTTP Response code explaining the response from Policy Engine. 
	 * 
	 * @return the responseCode in <code>Integer</code> format corresponding to the HTTP response code from Policy Engine. 
	 */
	public int getResponseCode();

	
	/**
	 * Gets the <code>Integer</code> value of the count of policies that reside on the PAP.
	 * 
	 * @return the <code>Integer</code> which consists of count of policies that reside on the PAP.  
	 */
	public int getPapMetrics();
	
	
	/**
	 * Gets the <code>Integer</code> value of the count of policies that reside on the PDP.
	 * 
	 * @return the <code>Integer</code> which consists of count of policies that reside on the PDP.  
	 */
	public int getPdpMetrics();
	
	
	/**
	 * Gets the <code>Integer</code> value of the total count of policies.
	 * 
	 * @return the <code>Integer</code> which consists of the total count of policies.  
	 */
	public int getMetricsTotal();
	


}
