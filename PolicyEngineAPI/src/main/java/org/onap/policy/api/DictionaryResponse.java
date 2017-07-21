package org.onap.policy.api;

import java.util.Map;

import javax.json.JsonObject;

public interface DictionaryResponse {
	
	/**
	 * Gets the <code>String</code> of the DictionaryItemsMessage from <code>DictionaryResponse</code>.
	 * 
	 * @return the <code>String</code> which consists of DictionaryItemsMessage from <code>DictionaryResponse</code>
	 */
	public String getResponseMessage();
	
	/**
	 * Response code of type <code>Integer</code> which corresponds to the HTTP Response code explaining the response from Policy Engine. 
	 * 
	 * @return the responseCode in <code>Integer</code> format corresponding to the HTTP response code from Policy Engine. 
	 */
	public int getResponseCode();

	
	/**
	 * Gets the <code>JsonObject</code> of all the Dictionary data retrieved
	 * 
	 * @return the <code>JsonObject</code> which consists of Dictionary data which has been retrieved.  
	 */
	public JsonObject getDictionaryJson();
	
	
	/**
	 * Gets the Key and Value pairs for each Dictionary item retrieved which can be used in the getDictionaryItems call.
	 * 
	 * @return <code>Map</code> of <code>String, String</code> which consists of the Key and Value pairs for each Dictionary item retrieved. 
	 */
	public Map<String,String> getDictionaryData();
	


}
