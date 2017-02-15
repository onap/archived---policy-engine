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

package org.openecomp.policy.api;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonObject;

import org.openecomp.policy.api.NotificationHandler;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.std.StdPolicyEngine;

/**
 * PolicyEngine is the Interface that applications use to make policy queries against a PEPEngine 
 * 
 * @version 1.0
 */
public class PolicyEngine{
	private String propertyFilePath = null;
	private StdPolicyEngine stdPolicyEngine;
	private NotificationScheme scheme = null;
	private NotificationHandler handler = null;
	
	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the Policy File Name 
	 * 
	 * @param policyName the <code>String</code> format of the PolicyFile Name whose configuration is required. 
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration. 
	 * @throws PolicyConfigException
	 * @deprecated use {@link #getConfigByPolicyName(String policyName, UUID requestID)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfigByPolicyName(String policyName) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.policyName(policyName,(UUID)null);
		return policyConfig;	
	}
	
	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the Policy File Name 
	 * 
	 * @param policyName the <code>String</code> format of the PolicyFile Name whose configuration is required. 
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration. 
	 * @throws PolicyConfigException
	 * @deprecated use {@link #getConfigByPolicyName(String policyName, UUID requestID)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfigByPolicyName(String policyName, UUID requestID) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.policyName(policyName,requestID);
		return policyConfig;	
	}
	
	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the eCOMPComponentName 
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose configuration is required. 
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException 
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String eCOMPComponentName) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(eCOMPComponentName,(UUID)null);
		return policyConfig;
	}
	
	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the eCOMPComponentName 
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose configuration is required. 
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration.
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @throws PolicyConfigException 
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String eCOMPComponentName, UUID requestID) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(eCOMPComponentName,requestID);
		return policyConfig;
	}
	
	/**
	 * Requests the configuration of the <code>String</code> which represents the eCOMPComponentName and <code>String</code>
	 * which represents the configName and returns the configuration if different Configurations exist for the
	 * particular eCOMPComponentName.  
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration. 
	 * @throws PolicyConfigException
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String eCOMPComponentName, String configName) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(eCOMPComponentName,configName,(UUID)null);
		return policyConfig;
	}
	
	/**
	 * Requests the configuration of the <code>String</code> which represents the eCOMPComponentName and <code>String</code>
	 * which represents the configName and returns the configuration if different Configurations exist for the
	 * particular eCOMPComponentName.  
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration. 
	 * @throws PolicyConfigException
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String eCOMPComponentName, String configName, UUID requestID) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(eCOMPComponentName,configName,requestID);
		return policyConfig;
	}
	
	/**
	 * Requests the configuration of the <code>String</code> which represents the eCOMPComponentName, <code>String</code>
	 * which represents the configName and <code>Map</code> of <code>String,String</code> which has the configAttribute and returns the specific 
	 * configuration related to the configAttributes mentioned.
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @param configAttributes the <code>Map</code> of <code>String,String</code> format of the configuration attributes which are required.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String eCOMPComponentName, String configName, Map<String, String> configAttributes) throws PolicyConfigException{
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(eCOMPComponentName,configName,configAttributes,(UUID)null);
		return policyConfig;
	}
	
	/**
	 * Requests the configuration of the <code>String</code> which represents the eCOMPComponentName, <code>String</code>
	 * which represents the configName and <code>Map</code> of <code>String,String</code> which has the configAttribute and returns the specific 
	 * configuration related to the configAttributes mentioned.
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @param configAttributes the <code>Map</code> of <code>String,String</code> format of the configuration attributes which are required.
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String eCOMPComponentName, String configName, Map<String, String> configAttributes, UUID requestID) throws PolicyConfigException{
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(eCOMPComponentName,configName,configAttributes,requestID);
		return policyConfig;
	}
	
	/**
	 * Requests the configuration of the <code>ConfigRequestParameters</code> which represents the Config policy request parameters 
	 * and returns the specific configuration related to the matching parameters. 
	 * 
	 * @param configRequestParameters {@link org.openecomp.policy.api.ConfigRequestParameters} which represents the Config policy request parameters. 
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException
	 */
	public Collection<PolicyConfig> getConfig(ConfigRequestParameters configRequestParameters)  throws PolicyConfigException{
		Collection<PolicyConfig> policyConfig = stdPolicyEngine.config(configRequestParameters);
		return policyConfig;
	}
	
	/**
	 * Requests the list of policies based on the <code>ConfigRequestParameters</code> which represents the policy request parameters 
	 * and returns the list of policies filtered by the parameters. 
	 * 
	 * @param configRequestParameters {@link org.openecomp.policy.api.ConfigRequestParameters} which represents the List Policy request parameters. 
	 * @return <code>Collection</code> of <code>String</code> which returns the list of policies.
	 * @throws PolicyConfigException
	 */
	public Collection<String> listConfig(ConfigRequestParameters listPolicyRequestParameters)  throws PolicyConfigException{
		Collection<String> policyList = stdPolicyEngine.listConfig(listPolicyRequestParameters);
		return policyList;
	}
	
	
	/**
	 * Sends the Events specified to the PEP and returns back the PolicyResponse. 
	 * 
	 * @param eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that must contain the event ID and values.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyResponse} which has the Response. 
	 * @throws PolicyEventException
	 * @deprecated use {@link #sendEvent(EventRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyResponse> sendEvent(Map<String, String> eventAttributes) throws PolicyEventException {
		Collection<PolicyResponse> policyResponse = stdPolicyEngine.event(eventAttributes, (UUID) null);
		return policyResponse;
	}
	
	/**
	 * Sends the Events specified to the PEP and returns back the PolicyResponse. 
	 * 
	 * @param eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that must contain the event ID and values.
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyResponse} which has the Response. 
	 * @throws PolicyEventException
	 * @deprecated use {@link #sendEvent(EventRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyResponse> sendEvent(Map<String, String> eventAttributes, UUID requestID) throws PolicyEventException {
		Collection<PolicyResponse> policyResponse = stdPolicyEngine.event(eventAttributes, requestID);
		return policyResponse;
	}
	
	/**
	 * Sends the Events specified to the PEP and returns back the PolicyResponse.
	 * 
	 * @param eventRequestParameters {@link org.openecomp.policy.api.EventRequestParameters} which represents the Event Request Parameters. 
	 * @return <code>Collection</code> of {@link org.openecomp.policy.api.PolicyResponse} which has the Response.
	 * @throws PolicyEventException
	 */
	public Collection<PolicyResponse> sendEvent(EventRequestParameters eventRequestParameters) throws PolicyEventException {
		Collection<PolicyResponse> policyResponse = stdPolicyEngine.event(eventRequestParameters);
		return policyResponse;
	}
	
	/**
	 * Sends the decision Attributes specified to the PEP and returns back the PolicyDecision. 
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose Decision is required.
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that must contain the ID and values.
	 * @return {@link org.openecomp.policy.api.DecisionResponse} which has the Decision. 
	 * @throws PolicyDecisionException
	 * @deprecated use {@link #getDecision(DecisionRequestParameters)} Instead.
	 */
	@Deprecated
	public DecisionResponse getDecision(String eCOMPComponentName, Map<String,String> decisionAttributes) throws PolicyDecisionException {
		DecisionResponse policyDecision = stdPolicyEngine.decide(eCOMPComponentName, decisionAttributes, null);
		return policyDecision;
	}
	
	/**
	 * Sends the decision Attributes specified to the PEP and returns back the PolicyDecision. 
	 * 
	 * @param eCOMPComponentName the <code>String</code> format of the eCOMPComponentName whose Decision is required.
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that must contain the ID and values.
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return {@link org.openecomp.policy.api.DecisionResponse} which has the Decision. 
	 * @throws PolicyDecisionException
	 * @deprecated use {@link #getDecision(DecisionRequestParameters)} Instead.
	 */
	@Deprecated
	public DecisionResponse getDecision(String eCOMPComponentName, Map<String,String> decisionAttributes, UUID requestID) throws PolicyDecisionException {
		DecisionResponse policyDecision = stdPolicyEngine.decide(eCOMPComponentName, decisionAttributes, requestID);
		return policyDecision;
	}
	
	/**
	 * Sends the decision Attributes specified to the PEP and returns back the PolicyDecision. 
	 * 
	 * @param decisionRequestParameters {@link org.openecomp.policy.api.DecisionRequestParameters} which represents the Decision Request Parameters.
	 * @return {@link org.openecomp.policy.api.DecisionResponse} which has the Decision.
	 * @throws PolicyDecisionException
	 */
	public DecisionResponse getDecision(DecisionRequestParameters decisionRequestParameters) throws PolicyDecisionException {
		DecisionResponse policyDecision = stdPolicyEngine.decide(decisionRequestParameters);
		return policyDecision;
	}
	
	/**
	 * <code>setNotification</code> allows changes to the Notification Scheme and Notification Handler
	 * 
	 * @param scheme the <code>NotificationScheme</code> of {@link org.openecomp.policy.api.NotificationScheme} which defines the Notification Scheme
	 * @param handler the <code>NotificationHandler</code> of {@link org.openecomp.policy.api.NotificationHandler} which defines what should happen when a notification is received.
	 */
	public void setNotification(NotificationScheme scheme, NotificationHandler handler) {
		this.scheme = scheme;
		this.handler = handler;
		stdPolicyEngine.notification(this.scheme,this.handler);
	}
	
	/**
	 * <code>clearNotification</code> shutsDown the Notification Service if the Auto Scehme Notification service is running.
	 */
	public void clearNotification(){
		stdPolicyEngine.stopNotification();
	}
	
	/**
	 * <code>setNotification</code> allows changes to the Notification Scheme
	 * 
	 * @param scheme the <code>NotificationScheme</code> of {@link org.openecomp.policy.api.NotificationScheme} which defines the Notification Scheme
	 */
	public void setScheme(NotificationScheme scheme){
		this.scheme = scheme;
		stdPolicyEngine.setScheme(this.scheme);
	}
	/**
	 * Gets the <code>PDPNotification</code> if there is one exists. This is used for Polling Patterns. 
	 * 
	 * @return <code>PDPNotification</code> of {@link org.openecomp.policy.api.PDPNotification} which has the Notification. 
	 */
	public PDPNotification getNotification() {
		return stdPolicyEngine.getNotification();
	}
	
	/**
	 * Creates a Config Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param policyDescription the <code>String</code> format of the Policy Description
	 * @param ecompName the <code>String</code> format of the ECOMP Name
	 * @param configName the <code>String</code> format of the Config Name
	 * @param configAttributes the <code>List</code> the <code>Map</code> Attributes that must contain the key and value.
	 * @param configType the <code>String</code> format of the Config Type
	 * @param body the <code>String</code> format of the Policy Body
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @throws Exception 
	 * @return <code>String</code> format of response
	 * @deprecated use {@link #createPolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String createConfigPolicy(String policyName, String policyDescription, String ecompName, String configName, 
												Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID,
												String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		
		String response = stdPolicyEngine.createConfigPolicy(policyName, policyDescription, ecompName, configName, 
																configAttributes, configType, body, policyScope, requestID,
																riskLevel, riskType, guard, ttlDate);
		
		return response;
		
	}
	
	/**
	 * Creates a Config Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param policyDescription the <code>String</code> format of the Policy Description
	 * @param ecompName the <code>String</code> format of the ECOMP Name
	 * @param configName the <code>String</code> format of the Config Name
	 * @param configAttributes the <code>List</code> the <code>Map</code> Attributes that must contain the key and value.
	 * @param configType the <code>String</code> format of the Config Type
	 * @param body the <code>String</code> format of the Policy Body
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @throws Exception 
	 * @return <code>String</code> format of response
	 * @deprecated use {@link #updatePolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String updateConfigPolicy(String policyName, String policyDescription, String ecompName, String configName, 
												Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID,
												String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		
		String response = stdPolicyEngine.updateConfigPolicy(policyName, policyDescription, ecompName, configName, 
																configAttributes, configType, body, policyScope, requestID,riskLevel, riskType, guard, ttlDate);
		
		return response;
		
	}
	
	/**
	 * Creates a Config Firewall Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param firewallJson the <code>JsonObject</code> representation of the Firewall Rules List
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @throws Exception 
	 * @return <code>String</code> format of response.
	 * @deprecated use {@link #createPolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String createConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		
		String response = stdPolicyEngine.createConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID,riskLevel, 
				riskType, guard, ttlDate);
		
		return response;
		
	}
	
	/**
	 * Updates a Config Firewall Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param firewallJson the <code>JsonObject</code> representation of the Firewall Rules List
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @throws Exception 
	 * @return <code>String</code> format of response. 
	 * @deprecated use {@link #updatePolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String updateConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		
		String response = stdPolicyEngine.updateConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID,riskLevel, riskType, guard, ttlDate);
		
		return response;
		
	}
	
	/**
	 * Creates a Dictionary Item based on given Dictionary Parameters
	 * 
	 * @param policyParameters {@link org.openecomp.policy.api.DictionaryParameters} which represents the Dictionary Parameters required to create a Dictionary Item. 
	 * @return {@link org.openecomp.policy.api.PolicyChangeResponse} which consists of the response related to create dictionary item Request. 
	 * @throws Exception
	 */
	public PolicyChangeResponse createDictionaryItem(DictionaryParameters parameters) throws Exception {
		PolicyChangeResponse response = stdPolicyEngine.createDictionaryItem(parameters);
		return response;
	}
	
	/**
	 * Creates a Policy based on given Policy Parameters. 
	 * 
	 * @param policyParameters {@link org.openecomp.policy.api.PolicyParameters} which represents the Policy Parameters required to create a Policy. 
	 * @return {@link org.openecomp.policy.api.PolicyChangeResponse} which consists of the response related to create policy Request. 
	 * @throws Exception
	 */
	public PolicyChangeResponse createPolicy(PolicyParameters policyParameters) throws Exception {
		PolicyChangeResponse response = stdPolicyEngine.createPolicy(policyParameters);
		return response;
	}
	
	/**
	 * Update Policy based on given Policy Parameters. 
	 * 
	 * @param policyParameters {@link org.openecomp.policy.api.PolicyParameters} which represents the Policy Parameters required to update a Policy. 
	 * @return {@link org.openecomp.policy.api.PolicyChangeResponse} which consists of the response related to create policy Request. 
	 * @throws Exception
	 */
	public PolicyChangeResponse updatePolicy(PolicyParameters policyParameters) throws Exception {
		PolicyChangeResponse response = stdPolicyEngine.updatePolicy(policyParameters);
		return response;
	}
	
	/**
	 * Pushes the specified policy to the PDP Group. If no PDP group is selected default is used. 
	 * 
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy is located
	 * @param policyName the <code>String</code> format of the Policy Name being pushed. 
	 * @param policyType the <code>String</code> format of the Policy Type which is being pushed.  
	 * @param pdpGroup the <code>String</code> format of the PDP Group name to which the policy needs to be pushed to. 
	 * @param requestID unique request ID which will be passed throughout the ECOMP components to correlate logging messages.
	 * @return <code>String</code> format of the response related to the push Policy Request. 
	 * @throws Exception
	 * @deprecated use {@link #pushPolicy(PushPolicyParameters)} instead. 
	 */
	@Deprecated
	public String pushPolicy(String policyScope, String policyName, String policyType, String pdpGroup, UUID requestID) throws Exception {
		
		String response = stdPolicyEngine.pushPolicy(policyScope, policyName, policyType, pdpGroup, requestID);
		
		return response;
	}
	
	/**
	 * Pushes the specified policy to the PDP Group. If no PDP group is selected default is used. 
	 * 
	 * @param pushPolicyParameters {@link org.openecomp.policy.api.PushPolicyParameters} which represents the Push Policy parameters required to push a policy. 
	 * @return {@link org.openecomp.policy.api.PolicyChangeResponse} which consists of the response related to the push Policy Request. 
	 * @throws Exception
	 */
	public PolicyChangeResponse pushPolicy(PushPolicyParameters pushPolicyParameters) throws Exception {
		PolicyChangeResponse response = stdPolicyEngine.pushPolicy(pushPolicyParameters);
		return response;
	}
	
	/**
	 * Deletes the specified policy from the PAP or PDP.
	 * 
	 * @param deletePolicyParameters {@link org.openecomp.policy.api.DeletePolicyParameters} which represents the Delete Policy parameters to delete a policy.
	 * @return {@link org.openecomp.policy.api.PolicyChangeResponse} which consists of the response related to the Delete Policy Request.
	 * @throws Exception
	 */
	public PolicyChangeResponse deletePolicy(DeletePolicyParameters deletePolicyParameters) throws Exception {
		PolicyChangeResponse response = stdPolicyEngine.deletePolicy(deletePolicyParameters);
		return response;
	}
	
	/**
	 * PolicyEngine Constructor with <code>String</code> format of propertiesFilePathname
	 * 
	 * @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname 
	 * @throws PolicyEngineException 
	 */
	public PolicyEngine(String propertiesFilePathname) throws PolicyEngineException {
		this.propertyFilePath = propertiesFilePathname ; 
		this.stdPolicyEngine= new StdPolicyEngine(this.propertyFilePath);
	}
	
	/**
	 * PolicyEngine Constructor with <code>String</code> format of PropertiesFilePathname, <code>NotificationScheme</code> and <code>NotificationHandler</code>
	 *  
	 *  @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname 
	 *  @param scheme the <code>NotificationScheme</code> of {@link org.openecomp.policy.api.NotificationScheme} which defines the Notification Scheme
	 *  @param handler the <code>NotificationHandler</code> of {@link org.openecomp.policy.api.NotificationHandler} which defines what should happen when a notification is received.
	 *  @throws PolicyEngineException 
	 */
	public PolicyEngine(String propertiesFilePathname, NotificationScheme scheme, NotificationHandler handler) throws PolicyEngineException {
		this.propertyFilePath = propertiesFilePathname ;
		this.scheme = scheme;
		this.handler = handler;
		this.stdPolicyEngine= new StdPolicyEngine(this.propertyFilePath,this.scheme,this.handler);
	}
	
	/**
	 * Creates a new Policy Service based on given Service Parameters. 
	 * 
	 * @param importParameters {@link org.openecomp.policy.api.ImportParameters} which represents the Service Parameters required to create a Policy Service. 
	 * @return {@link org.openecomp.policy.api.PolicyChangeResponse} which consists of the response related to create import Service. 
	 * @throws Exception
	 */
	public PolicyChangeResponse policyEngineImport(ImportParameters importParameters) throws Exception {
		PolicyChangeResponse response = stdPolicyEngine.policyEngineImport(importParameters);
		return response;
	}
	
	/**
	 * PolicyEngine Constructor with <code>String</code> format of PropertiesFilePathname and <code>NotificationScheme</code>
	 * 
	 * @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname
	 * @param scheme the <code>NotificationScheme</code> of {@link org.openecomp.policy.api.NotificationScheme} which defines the Notification Scheme
	 * @throws PolicyEngineException 
	 */
	public PolicyEngine(String propertiesFilePathname, NotificationScheme scheme) throws PolicyEngineException{
		this.propertyFilePath = propertiesFilePathname;
		this.scheme = scheme;
		this.stdPolicyEngine = new StdPolicyEngine(this.propertyFilePath, this.scheme);
	}
	/**
	 * PolicyEngine Constructor with no parameters.
	 *//*
	public PolicyEngine(){
		
	}
	public void createFirewallPolicy(String filterName, String termName, String preIPSource, String preIPDest, 
										String sourcePort, String destPort, String Port, String protocol, String direction, String action ) throws PolicyDecisionException {
		stdPolicyEngine.createFirewallPolicy(filterName, termName, preIPSource, preIPDest, sourcePort, destPort, Port,
												protocol, direction, action);
	}
	
	public void updateFirewallPolicy(String filterName, String termName, String preIPSource, String preIPDest, 
										String sourcePort, String destPort, String Port, String protocol, String direction, String action ) throws PolicyDecisionException {
			stdPolicyEngine.updateFirewallPolicy(filterName, termName, preIPSource, preIPDest, sourcePort, destPort, Port,
													protocol, direction, action);
	}*/
}
