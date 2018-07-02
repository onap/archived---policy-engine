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

import java.util.UUID;

import com.google.gson.Gson;

/**
 * <code>PushPolicyParameters</code> defines the Policy Parameters
 *  which are required to Push a Policy to PDPGroup. 
 * 
 * @version 0.1
 */
public class PushPolicyParameters {
	private String policyName;
	private String policyType;
	private String pdpGroup;
	private UUID requestID;
	
	/**
	 * Constructor with no Parameters.
	 */
	public PushPolicyParameters(){
		// Empty constructor
	}
	
	/**
	 * Constructor with Parameters.
	 * 
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param policyType the <code>String</code> format of the Policy Type
	 * @param pdpGroup the <code>String</code> format of the PDPGroup
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public PushPolicyParameters(String policyName, String policyType, String pdpGroup, UUID requestID){
		this.policyName = policyName;
		this.policyType = policyType;
		this.pdpGroup = pdpGroup;
		this.requestID = requestID;
	}
	
	/**
	 * Gets the PolicyName of the Push Policy Parameters. 
	 * 
	 * @return policyName the <code>String</code> format of the Policy Name
	 */
	public String getPolicyName() {
		return policyName;
	}
	
	/**
	 * Sets the policyName of the Push Policy Parameters.
	 * 
	 * @param policyName the <code>String</code> format of the Policy Name
	 */
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	/**
	 * Gets the PolicyType of the Push Policy Parameters. 
	 * 
	 * @return policyType the <code>String</code> format of the Policy Type
	 */
	public String getPolicyType() {
		return policyType;
	}
	
	/**
	 * Sets the policyType of the Push Policy Parameters.
	 * 
	 * @param policyType the <code>String</code> format of the Policy Type
	 */
	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
	
	/**
	 * Gets the PDPGroup of the Push Policy Parameters. 
	 * 
	 * @return pdpGroup the <code>String</code> format of the PDPGroup
	 */
	public String getPdpGroup() {
		return pdpGroup;
	}
	
	/**
	 * Sets the PDPGroup of the Push Policy Parameters.
	 * 
	 * @param pdpGroup the <code>String</code> format of the PDPGroup
	 */
	public void setPdpGroup(String pdpGroup) {
		this.pdpGroup = pdpGroup;
	}
	
	/**
	 * Gets the requestID of the Push Policy Parameters. 
	 * 
	 * @return unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public UUID getRequestID() {
		return requestID;
	}
	
	/**
	 * Sets the requestID of the Push Policy Parameters. 
	 * 
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public void setRequestID(UUID requestID) {
		this.requestID = requestID;
	}
	
	/**
	 * Used to print the input Params for PushPolicy REST call. 
	 * 
	 * @return JSON String of this object. 
	 */
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
}
