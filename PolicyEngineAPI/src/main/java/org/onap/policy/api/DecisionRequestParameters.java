/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

/**
 * <code>DecisionRequestParameters</code> defines the Decision Policy Request Parameters
 *  which retrieve(s) the response from PDP if the request parameters match with any Decision Policy.
 * 
 * @version 0.1
 */
public class DecisionRequestParameters {
	private String onapComponentName;
	private Map<String,String> decisionAttributes;
	private UUID requestID;
	
	/**
	 * Constructor with no Parameters
	 */
	public DecisionRequestParameters(){
	}
	
	/**
	 * Constructor with Parameters
	 * 
	 * @param onapComponentName the <code>String</code> format of the onapComponentName whose Decision is required.
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that contain the ID and values.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public DecisionRequestParameters(String onapComponentName, Map<String,String> decisionAttributes, UUID requestID){
		this.onapComponentName = onapComponentName;
		this.decisionAttributes = decisionAttributes;
		this.requestID = requestID;
	}
	
	/**
	 * Gets the ONAPComponentName of the Decision Request Parameters. 
	 * 
	 * @return ONAPComponentName the <code>String</code> format of the onapComponentName of the Decision Request Parameters.
	 */
	public String getONAPComponentName() {
		return onapComponentName;
	}
	/**
	 * Sets the ONAPComponentName of the Decision Request parameters.  
	 * 
	 * @param onapComponentName the <code>String</code> format of the onapComponentName whose Decision is required.
	 */
	public void setONAPComponentName(String onapComponentName) {
		this.onapComponentName = onapComponentName;
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
}
