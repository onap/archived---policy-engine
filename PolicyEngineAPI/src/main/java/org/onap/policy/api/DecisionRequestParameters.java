/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.api;

import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

/**
 * <code>DecisionRequestParameters</code> defines the Decision Policy Request Parameters
 *  which retrieve(s) the response from PDP if the request parameters match with any Decision Policy.
 * 
 * @version 0.1
 */
public class DecisionRequestParameters {
	private String onapName;
	private Map<String,String> decisionAttributes;
	private UUID requestID;
	
	/**
	 * Constructor with no Parameters
	 */
	public DecisionRequestParameters(){
		// Empty constructor
	}
	
	/**
	 * Constructor with Parameters
	 * 
	 * @param onapName the <code>String</code> format of the onapName whose Decision is required.
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that contain the ID and values.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public DecisionRequestParameters(String onapName, Map<String,String> decisionAttributes, UUID requestID){
		this.onapName = onapName;
		this.decisionAttributes = decisionAttributes;
		this.requestID = requestID;
	}
	
	/**
	 * Gets the onapName of the Decision Request Parameters. 
	 * 
	 * @return onapName the <code>String</code> format of the onapName of the Decision Request Parameters.
	 */
	public String getOnapName() {
		return onapName;
	}
	
	/**
     * Gets the onapName of the Decision Request Parameters. 
     * 
     * @return onapName the <code>String</code> format of the onapName of the Decision Request Parameters.
     * @deprecated use {@link #getOnapName()} instead. 
     */
	@Deprecated
    public String getECOMPComponentName() {
        return onapName;
    }
	
	/**
	 * Sets the onapName of the Decision Request parameters.  
	 * 
	 * @param onapName the <code>String</code> format of the onapName whose Decision is required.
	 */
	public void setOnapName(String onapName) {
		this.onapName = onapName;
	}
	
	/**
     * Sets the ecompComponentName of the Decision Request parameters.  
     * 
     * @param ecompName the <code>String</code> format of the onapName whose Decision is required.
     * @deprecated use {@link #setOnapName(String)} instead. 
     */
	@Deprecated
    public void setECOMPComponentName(String ecompName) {
        this.onapName = ecompName;
    }
	
	/**
	 * Gets the Decision Attributes from Decision Request Parameters. 
	 * 
	 * @return decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that contain the ID and values.
	 */
	public Map<String,String> getDecisionAttributes() {
		return decisionAttributes;
	}
	/**
	 * Sets the Decision Attributes which contain ID and values for obtaining Decision from PDP. 
	 * 
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that must contain the ID and values.
	 */
	public void setDecisionAttributes(Map<String,String> decisionAttributes) {
		this.decisionAttributes = decisionAttributes;
	}
	/**
	 * Gets the request ID of Decision Request Parameters. 
	 * 
	 * @return the requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public UUID getRequestID() {
		return requestID;
	}
	/**
	 * Sets the ReqestID of Decision Request Parameters which will be passed around ONAP requests.
	 * 
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public void setRequestID(UUID requestID) {
		this.requestID = requestID;
	}
	
	/**
	 * Used to print the input Params for getDecision REST call. 
	 * 
	 * @return JSON String of this object. 
	 */
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
}
