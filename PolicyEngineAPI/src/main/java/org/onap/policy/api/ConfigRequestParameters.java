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
 * <code>ConfigRequestParameters</code> defines the Config Policy Request Parameters
 *  which retrieve(s) the policy from PDP if the request parameters match with any Config Policy.  
 * 
 * @version 0.1
 */
public class ConfigRequestParameters {
	private String policyName;
	private String onapName;
	private String configName;
	private Map<String,String> configAttributes;
	private UUID requestID;
	private Boolean unique = false;
	
	/**
	 * Sets the PolicyName of the Config policy which needs to be retrieved. 
	 * 
	 * @param policyName the <code>String</code> format of the PolicyFile Name whose configuration is required.
	 */
	public void setPolicyName(String policyName){
		this.policyName = policyName;
	}
	
	/**
	 * Sets the ONAP Component Name of the Config policy which needs to be retrieved. 
	 * 
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 */
	public void setOnapName(String onapName){
		this.onapName = onapName;
	}
	
	/**
     * Sets the ONAP Component Name of the Config policy which needs to be retrieved. 
     * 
     * @param ecompName the <code>String</code> format of the onapName whose configuration is required.
     * @deprecated use {@link #setOnapName(String)} instead.  
     */
	@Deprecated
    public void setEcompName(String ecompName){
        this.onapName = ecompName;
    }
	
	/**
	 * Sets the Config Name of the Config policy which needs to be retrieved.  
	 * 
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 */
	public void setConfigName(String configName){
		this.configName = configName;
	}
	
	/**
	 * Sets the ConfigAttributes of the Config policy which needs to be retrieved. 
	 * 
	 * @param configAttributes the <code>Map</code> of <code>String,String</code> format of the configuration attributes which are required.
	 */
	public void setConfigAttributes(Map<String, String> configAttributes){
		this.configAttributes = configAttributes;
	}
	
	/**
	 * Sets the Request ID of the ONAP request. 
	 * 
	 * @param requestID unique <code>UUID</code> requestID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public void setRequestID(UUID requestID){
		this.requestID = requestID;
	}
	
	/**
	 * Gets the policyName of the Request Parameters.
	 * 
	 * @return <code>String</code> format of the policyName.
	 */
	public String getPolicyName(){
		return policyName;
	}
	
	/**
	 * Gets the ONAP Component Name of the Request Parameters. 
	 * 
	 * @return <code>String</code> format of the ONAP Component Name. 
	 */
	public String getOnapName(){
		return onapName;
	}
	
	/**
     * Gets the ONAP Component Name of the Request Parameters. 
     * 
     * @return <code>String</code> format of the ONAP Component Name. 
     * @deprecated use {@link #getOnapName()} instead. 
     */
	@Deprecated
    public String getEcompName(){
        return onapName;
    }
	
	/**
	 * Gets the Config name of the Request Parameters. 
	 * 
	 * @return <code>String</code> format of the Config Name.  
	 */
	public String getConfigName(){
		return configName;
	}
	
	/**
	 *  Gets the Config Attributes of the Request Parameters. 
	 *  
	 * @return <code>Map</code> of <code>String</code>,<code>String</code> format of the config Attributes. 
	 */
	public Map<String,String> getConfigAttributes(){
		return configAttributes;
	}
	
	/**
	 *  Gets the Request ID of the Request Paramters.
	 *  
	 * @return <code>UUID</code> format of requestID. 
	 */
	public UUID getRequestID(){
		return requestID;
	}
	
	/**
	 * Makes the results Unique, priority based. If set to True. Default Value is set to False. 
	 * 
	 * @param unique flag which is either true or false. 
	 */
	public void makeUnique(Boolean unique){
		this.unique = unique;
	}
	
	/**
	 * Gets the Unique flag value from the Config Request Parameters. 
	 * 
	 * @return unique flag which is either true or false. 
	 */
	public Boolean getUnique(){
		return this.unique;
	}
	
	/**
	 * Used to print the input Params for getCOnfig REST call. 
	 * 
	 * @return JSON String of this object. 
	 */
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}


}
