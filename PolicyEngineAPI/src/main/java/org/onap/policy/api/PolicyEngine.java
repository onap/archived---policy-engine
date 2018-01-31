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

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.json.JsonObject;

import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.std.StdPolicyEngine;

/**
 * PolicyEngine is the Interface that applications use to make policy queries against a PEPEngine
 *
 * @version 2.0
 */
public class PolicyEngine{
	private String propertyFilePath = null;
	private StdPolicyEngine stdPolicyEngine;
	private NotificationScheme scheme = null;
	private NotificationHandler handler = null;

	/**
	 * PolicyEngine Constructor with <code>String</code> format of propertiesFilePathname
	 *
	 * @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname
	 * @throws PolicyEngineException PolicyEngine Exception
	 */
	public PolicyEngine(String propertiesFilePathname) throws PolicyEngineException {
		this.propertyFilePath = propertiesFilePathname ;
		this.stdPolicyEngine= new StdPolicyEngine(this.propertyFilePath, (String)null);
	}

	/**
	 * PolicyEngine Constructor with <code>String</code> format of propertiesFilePathname
	 *
	 * @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname
	 * @param clientKey depicts String format of Password/ Client_Key.
	 * @throws PolicyEngineException PolicyEngine Exception
	 */
	public PolicyEngine(String propertiesFilePathname, String clientKey) throws PolicyEngineException {
		this.propertyFilePath = propertiesFilePathname ;
		this.stdPolicyEngine= new StdPolicyEngine(this.propertyFilePath, clientKey);
	}

	/**
	 * PolicyEngine Constructor with <code>String</code> format of PropertiesFilePathname and <code>NotificationScheme</code>
	 *
	 * @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname
	 * @param scheme the <code>NotificationScheme</code> of {@link org.onap.policy.api.NotificationScheme} which defines the Notification Scheme
	 * @throws PolicyEngineException PolicyEngine Exception
	 */
	public PolicyEngine(String propertiesFilePathname, NotificationScheme scheme) throws PolicyEngineException{
		this.propertyFilePath = propertiesFilePathname;
		this.scheme = scheme;
		this.stdPolicyEngine = new StdPolicyEngine(this.propertyFilePath, this.scheme);
	}

	/**
	 * PolicyEngine Constructor with <code>String</code> format of PropertiesFilePathname, <code>NotificationScheme</code> and <code>NotificationHandler</code>
	 *
	 *  @param propertiesFilePathname the <code>String</code> format of the propertiesFilePathname
	 *  @param scheme the <code>NotificationScheme</code> of {@link org.onap.policy.api.NotificationScheme} which defines the Notification Scheme
	 *  @param handler the <code>NotificationHandler</code> of {@link org.onap.policy.api.NotificationHandler} which defines what should happen when a notification is received.
	 *  @throws PolicyEngineException PolicyEngine Exception
	 */
	public PolicyEngine(String propertiesFilePathname, NotificationScheme scheme, NotificationHandler handler) throws PolicyEngineException {
		this.propertyFilePath = propertiesFilePathname ;
		this.scheme = scheme;
		this.handler = handler;
		this.stdPolicyEngine= new StdPolicyEngine(this.propertyFilePath,this.scheme,this.handler);
	}

	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the Policy File Name
	 *
	 * @param policyName the <code>String</code> format of the PolicyFile Name whose configuration is required.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters configRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfigByPolicyName(String policyName) throws PolicyConfigException {
		return getConfig(setConfigRequestParameters(policyName, null, null, null, null));
	}

	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the Policy File Name
	 *
	 * @param policyName the <code>String</code> format of the PolicyFile Name whose configuration is required.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters configRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfigByPolicyName(String policyName, UUID requestID) throws PolicyConfigException {
		return getConfig(setConfigRequestParameters(policyName, null, null, null, requestID));
	}

	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the onapName
	 *
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String onapName) throws PolicyConfigException {
		return getConfig(setConfigRequestParameters(null, onapName, null, null, null));
	}

	/**
	 * Gets the configuration from the PolicyDecisionPoint(PDP) for the <code>String</code> which represents the onapName
	 *
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String onapName, UUID requestID) throws PolicyConfigException {
		return getConfig(setConfigRequestParameters(null, onapName, null, null, requestID));
	}

	/**
	 * Requests the configuration of the <code>String</code> which represents the onapName and <code>String</code>
	 * which represents the configName and returns the configuration if different Configurations exist for the
	 * particular onapName.
	 *
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String onapName, String configName) throws PolicyConfigException {
		return getConfig(setConfigRequestParameters(null, onapName, configName, null, null));
	}

	/**
	 * Requests the configuration of the <code>String</code> which represents the onapName and <code>String</code>
	 * which represents the configName and returns the configuration if different Configurations exist for the
	 * particular onapName.
	 *
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String onapName, String configName, UUID requestID) throws PolicyConfigException {
		return getConfig(setConfigRequestParameters(null, onapName, configName, null, requestID));
	}

	/**
	 * Requests the configuration of the <code>String</code> which represents the onapName, <code>String</code>
	 * which represents the configName and <code>Map</code> of <code>String,String</code> which has the configAttribute and returns the specific
	 * configuration related to the configAttributes mentioned.
	 *
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @param configAttributes the <code>Map</code> of <code>String,String</code> format of the configuration attributes which are required.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String onapName, String configName, Map<String, String> configAttributes) throws PolicyConfigException{
		return getConfig(setConfigRequestParameters(null, onapName, configName, configAttributes, null));
	}

	/**
	 * Requests the configuration of the <code>String</code> which represents the onapName, <code>String</code>
	 * which represents the configName and <code>Map</code> of <code>String,String</code> which has the configAttribute and returns the specific
	 * configuration related to the configAttributes mentioned.
	 *
	 * @param onapName the <code>String</code> format of the onapName whose configuration is required.
	 * @param configName the <code>String</code> format of the configurationName whose configuration is required.
	 * @param configAttributes the <code>Map</code> of <code>String,String</code> format of the configuration attributes which are required.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 * @deprecated use {@link #getConfig(ConfigRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyConfig> getConfig(String onapName, String configName, Map<String, String> configAttributes, UUID requestID) throws PolicyConfigException{
		return getConfig(setConfigRequestParameters(null, onapName, configName, configAttributes, requestID));
	}

	/**
	 * Requests the configuration of the <code>ConfigRequestParameters</code> which represents the Config policy request parameters
	 * and returns the specific configuration related to the matching parameters.
	 *
	 * @param configRequestParameters {@link org.onap.policy.api.ConfigRequestParameters} which represents the Config policy request parameters.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyConfig} which has the configuration.
	 * @throws PolicyConfigException PolicyConfig Exception
	 */
	public Collection<PolicyConfig> getConfig(ConfigRequestParameters configRequestParameters)  throws PolicyConfigException{
		return stdPolicyEngine.getConfig(configRequestParameters);
	}

	/**
	 * Requests the list of policies based on the <code>ConfigRequestParameters</code> which represents the policy request parameters
	 * and returns the list of policies filtered by the parameters.
	 *
	 * @param configRequestParameters {@link org.onap.policy.api.ConfigRequestParameters} which represents the List Policy request parameters.
	 * @return <code>Collection</code> of <code>String</code> which returns the list of policies.
	 * @throws PolicyConfigException PolicyConfig Exception
	 */
	public Collection<String> listConfig(ConfigRequestParameters listPolicyRequestParameters)  throws PolicyConfigException{
		return stdPolicyEngine.listConfig(listPolicyRequestParameters);
	}


	/**
	 * Sends the Events specified to the PEP and returns back the PolicyResponse.
	 *
	 * @param eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that must contain the event ID and values.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyResponse} which has the Response.
	 * @throws PolicyEventException PolicyEvent Exception
	 * @deprecated use {@link #sendEvent(EventRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyResponse> sendEvent(Map<String, String> eventAttributes) throws PolicyEventException {
		return stdPolicyEngine.sendEvent(eventAttributes, (UUID) null);
	}

	/**
	 * Sends the Events specified to the PEP and returns back the PolicyResponse.
	 *
	 * @param eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that must contain the event ID and values.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyResponse} which has the Response.
	 * @throws PolicyEventException PolicyEvent Exception
	 * @deprecated use {@link #sendEvent(EventRequestParameters)} Instead.
	 */
	@Deprecated
	public Collection<PolicyResponse> sendEvent(Map<String, String> eventAttributes, UUID requestID) throws PolicyEventException {
		return stdPolicyEngine.sendEvent(eventAttributes, requestID);
	}

	/**
	 * Sends the Events specified to the PEP and returns back the PolicyResponse.
	 *
	 * @param eventRequestParameters {@link org.onap.policy.api.EventRequestParameters} which represents the Event Request Parameters.
	 * @return <code>Collection</code> of {@link org.onap.policy.api.PolicyResponse} which has the Response.
	 * @throws PolicyEventException PolicyEvent Exception
	 */
	public Collection<PolicyResponse> sendEvent(EventRequestParameters eventRequestParameters) throws PolicyEventException {
		return stdPolicyEngine.sendEvent(eventRequestParameters);
	}

	/**
	 * Sends the decision Attributes specified to the PEP and returns back the PolicyDecision.
	 *
	 * @param onapName the <code>String</code> format of the onapName whose Decision is required.
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that must contain the ID and values.
	 * @return {@link org.onap.policy.api.DecisionResponse} which has the Decision.
	 * @throws PolicyDecisionException PolicyDecision Exception
	 * @deprecated use {@link #getDecision(DecisionRequestParameters)} Instead.
	 */
	@Deprecated
	public DecisionResponse getDecision(String onapName, Map<String,String> decisionAttributes) throws PolicyDecisionException {
		return stdPolicyEngine.getDecision(onapName, decisionAttributes, null);
	}

	/**
	 * Sends the decision Attributes specified to the PEP and returns back the PolicyDecision.
	 *
	 * @param onapName the <code>String</code> format of the onapName whose Decision is required.
	 * @param decisionAttributes the <code>Map</code> of <code>String,String</code> format of the decisionAttributes that must contain the ID and values.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @return {@link org.onap.policy.api.DecisionResponse} which has the Decision.
	 * @throws PolicyDecisionException PolicyDecision Exception
	 * @deprecated use {@link #getDecision(DecisionRequestParameters)} Instead.
	 */
	@Deprecated
	public DecisionResponse getDecision(String onapName, Map<String,String> decisionAttributes, UUID requestID) throws PolicyDecisionException {
		return stdPolicyEngine.getDecision(onapName, decisionAttributes, requestID);
	}

	/**
	 * Sends the decision Attributes specified to the PEP and returns back the PolicyDecision.
	 *
	 * @param decisionRequestParameters {@link org.onap.policy.api.DecisionRequestParameters} which represents the Decision Request Parameters.
	 * @return {@link org.onap.policy.api.DecisionResponse} which has the Decision.
	 * @throws PolicyDecisionException PolicyDecision Exception
	 */
	public DecisionResponse getDecision(DecisionRequestParameters decisionRequestParameters) throws PolicyDecisionException {
		return stdPolicyEngine.getDecision(decisionRequestParameters);
	}

	/**
	 * Retrieves the count of policies on the PAP, PDP, and Policy Engine as a whole
	 *
	 * @param parameters {@link  org.onap.policy.api.MetricsRequestParameters} which represents the Parameters required to get the Policy Metrics
	 * @return {@link org.onap.policy.api.MetricsResponse} which consists of the response related to getMetrics Request.
	 * @throws PolicyException PolicyException related to the operation
	 *
	 * */
	public MetricsResponse getMetrics(MetricsRequestParameters parameters) throws PolicyException {
		return stdPolicyEngine.getMetrics(parameters);
	}

	/**
	 * Creates a Config Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param policyDescription the <code>String</code> format of the Policy Description
	 * @param onapName the <code>String</code> format of the ONAP Name
	 * @param configName the <code>String</code> format of the Config Name
	 * @param configAttributes the <code>List</code> the <code>Map</code> Attributes that must contain the key and value.
	 * @param configType the <code>String</code> format of the Config Type
	 * @param body the <code>String</code> format of the Policy Body
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @param riskLevel the <code>String</code> value of risk Level.
	 * @param riskType the <code>String</code> value of risk Type.
	 * @param guard the <code>String</code> value of guard.
	 * @param ttlDate the <code>String</code> value of time to live Date.
	 * @throws PolicyException PolicyException related to the operation.
	 * @return <code>String</code> format of response
	 * @deprecated use {@link #createPolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String createConfigPolicy(String policyName, String policyDescription, String onapName, String configName,
												Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID,
												String riskLevel, String riskType, String guard, String ttlDate) throws PolicyException {
		return stdPolicyEngine.createUpdateConfigPolicy(policyName, policyDescription, onapName, configName,
				configAttributes, configType, body, policyScope, requestID,
				riskLevel, riskType, guard, ttlDate, false);
	}

	/**
	 * Creates a Config Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param policyDescription the <code>String</code> format of the Policy Description
	 * @param onapName the <code>String</code> format of the ONAP Name
	 * @param configName the <code>String</code> format of the Config Name
	 * @param configAttributes the <code>List</code> the <code>Map</code> Attributes that must contain the key and value.
	 * @param configType the <code>String</code> format of the Config Type
	 * @param body the <code>String</code> format of the Policy Body
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @param riskLevel the <code>String</code> value of risk Level.
	 * @param riskType the <code>String</code> value of risk Type.
	 * @param guard the <code>String</code> value of guard.
	 * @param ttlDate the <code>String</code> value of time to live Date.
	 * @throws PolicyException PolicyException related to the operation.
	 * @return <code>String</code> format of response
	 * @deprecated use {@link #updatePolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String updateConfigPolicy(String policyName, String policyDescription, String onapName, String configName,
												Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID,
												String riskLevel, String riskType, String guard, String ttlDate) throws PolicyException {
		return stdPolicyEngine.createUpdateConfigPolicy(policyName, policyDescription, onapName, configName,
				configAttributes, configType, body, policyScope, requestID,riskLevel, riskType, guard, ttlDate, true);
	}

	/**
	 * Creates a Config Firewall Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param firewallJson the <code>JsonObject</code> representation of the Firewall Rules List
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @param riskLevel the <code>String</code> value of risk Level.
	 * @param riskType the <code>String</code> value of risk Type.
	 * @param guard the <code>String</code> value of guard.
	 * @param ttlDate the <code>String</code> value of time to live Date.
	 * @throws PolicyException PolicyException related to the operation.
	 * @return <code>String</code> format of response.
	 * @deprecated use {@link #createPolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String createConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws PolicyException {
		return stdPolicyEngine.createUpdateConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID,riskLevel,
				riskType, guard, ttlDate, false);
	}

	/**
	 * Updates a Config Firewall Policy based on given arguments
	 * @param policyName the <code>String</code> format of the Policy Name
	 * @param firewallJson the <code>JsonObject</code> representation of the Firewall Rules List
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy will be created and stored
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * A different request ID should be passed for each request.
	 * @param riskLevel the <code>String</code> value of risk Level.
	 * @param riskType the <code>String</code> value of risk Type.
	 * @param guard the <code>String</code> value of guard.
	 * @param ttlDate the <code>String</code> value of time to live Date.
	 * @throws PolicyException PolicyException related to the operation.
	 * @return <code>String</code> format of response.
	 * @deprecated use {@link #updatePolicy(PolicyParameters)} Instead.
	 */
	@Deprecated
	public String updateConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws PolicyException {
		return stdPolicyEngine.createUpdateConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID,riskLevel, riskType, guard, ttlDate, true);
	}

	/**
	 * Retrieves Dictionary Items for a specified dictionary
	 *
	 * @param parameters {@link org.onap.policy.api.DictionaryParameters} which represents the Dictionary Parameters required to create a Dictionary Item.
	 * @return {@link org.onap.policy.api.DictionaryResponse} which consists of the response related to create dictionary item Request.
	 * @throws PolicyException PolicyException related to the operation
	 *
	 * */
	public DictionaryResponse getDictionaryItem(DictionaryParameters parameters) throws PolicyException {
		return stdPolicyEngine.getDictionaryItem(parameters);
	}

	/**
	 * Creates a Dictionary Item based on given Dictionary Parameters
	 *
	 * @param parameters {@link org.onap.policy.api.DictionaryParameters} which represents the Dictionary Parameters required to create a Dictionary Item.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to create dictionary item Request.
	 * @throws PolicyException  PolicyException related to the operation.
	 */
	public PolicyChangeResponse createDictionaryItem(DictionaryParameters parameters) throws PolicyException {
		return stdPolicyEngine.createDictionaryItem(parameters);
	}

	/**
	 * Updates a Dictionary Item based on given Dictionary Parameters
	 *
	 * @param  parameters {@link org.onap.policy.api.DictionaryParameters} which represents the Dictionary Parameters required to update a Dictionary Item.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to update dictionary item Request.
	 * @throws PolicyException PolicyException related to the operation.
	 */
	public PolicyChangeResponse updateDictionaryItem(DictionaryParameters parameters) throws PolicyException {
		return stdPolicyEngine.updateDictionaryItem(parameters);
	}

	/**
	 * Creates a Policy based on given Policy Parameters.
	 *
	 * @param policyParameters {@link org.onap.policy.api.PolicyParameters} which represents the Policy Parameters required to create a Policy.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to create policy Request.
	 * @throws PolicyException  PolicyException related to the operation.
	 */
	public PolicyChangeResponse createPolicy(PolicyParameters policyParameters) throws PolicyException {
		return stdPolicyEngine.createPolicy(policyParameters);
	}

	/**
	 * Update Policy based on given Policy Parameters.
	 *
	 * @param policyParameters {@link org.onap.policy.api.PolicyParameters} which represents the Policy Parameters required to update a Policy.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to create policy Request.
	 * @throws PolicyException  PolicyException related to the operation.
	 */
	public PolicyChangeResponse updatePolicy(PolicyParameters policyParameters) throws PolicyException {
		return stdPolicyEngine.updatePolicy(policyParameters);
	}

	/**
	 * Pushes the specified policy to the PDP Group. If no PDP group is selected default is used.
	 *
	 * @param policyScope the <code>String</code> value of the sub scope directory where the policy is located
	 * @param policyName the <code>String</code> format of the Policy Name being pushed.
	 * @param policyType the <code>String</code> format of the Policy Type which is being pushed.
	 * @param pdpGroup the <code>String</code> format of the PDP Group name to which the policy needs to be pushed to.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * @return <code>String</code> format of the response related to the push Policy Request.
	 * @throws PolicyException  PolicyException related to the operation.
	 * @deprecated use {@link #pushPolicy(PushPolicyParameters)} instead.
	 */
	@Deprecated
	public String pushPolicy(String policyScope, String policyName, String policyType, String pdpGroup, UUID requestID) throws PolicyException {
		return stdPolicyEngine.pushPolicy(policyScope, policyName, policyType, pdpGroup, requestID);
	}

	/**
	 * Pushes the specified policy to the PDP Group. If no PDP group is selected default is used.
	 *
	 * @param pushPolicyParameters {@link org.onap.policy.api.PushPolicyParameters} which represents the Push Policy parameters required to push a policy.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to the push Policy Request.
	 * @throws PolicyException  PolicyException related to the operation.
	 */
	public PolicyChangeResponse pushPolicy(PushPolicyParameters pushPolicyParameters) throws PolicyException {
		return stdPolicyEngine.pushPolicy(pushPolicyParameters);
	}

	/**
	 * Deletes the specified policy from the PAP or PDP.
	 *
	 * @param deletePolicyParameters {@link org.onap.policy.api.DeletePolicyParameters} which represents the Delete Policy parameters to delete a policy.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to the Delete Policy Request.
	 * @throws PolicyException PolicyException related to the operation.
	 */
	public PolicyChangeResponse deletePolicy(DeletePolicyParameters deletePolicyParameters) throws PolicyException {
		return stdPolicyEngine.deletePolicy(deletePolicyParameters);
	}

	/**
	 * Creates a new Policy Service based on given Service Parameters.
	 *
	 * @param importParameters {@link org.onap.policy.api.ImportParameters} which represents the Service Parameters required to create a Policy Service.
	 * @return {@link org.onap.policy.api.PolicyChangeResponse} which consists of the response related to create import Service.
	 * @throws PolicyException PolicyException related to the operation.
	 */
	public PolicyChangeResponse policyEngineImport(ImportParameters importParameters) throws PolicyException {
		return stdPolicyEngine.policyEngineImport(importParameters);
	}

	/**
	 * <code>setNotification</code> allows changes to the Notification Scheme and Notification Handler
	 *
	 * @param scheme the <code>NotificationScheme</code> of {@link org.onap.policy.api.NotificationScheme} which defines the Notification Scheme
	 * @param handler the <code>NotificationHandler</code> of {@link org.onap.policy.api.NotificationHandler} which defines what should happen when a notification is received.
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
	 * @param scheme the <code>NotificationScheme</code> of {@link org.onap.policy.api.NotificationScheme} which defines the Notification Scheme
	 */
	public void setScheme(NotificationScheme scheme){
		this.scheme = scheme;
		stdPolicyEngine.setScheme(this.scheme);
	}

	/**
	 * Gets the <code>PDPNotification</code> if there is one exists. This is used for Polling Patterns.
	 *
	 * @return <code>PDPNotification</code> of {@link org.onap.policy.api.PDPNotification} which has the Notification.
	 */
	public PDPNotification getNotification() {
		return stdPolicyEngine.getNotification();
	}

	/**
	 * setClientKey allows the client to use their own implementation logic for Password Protection
	 * and will be used to set the clear text password, this will be used while making Requests.
	 *
	 * @param clientKey depicts String format of Password/ Client_Key.
	 */
	public void setClientKey(String clientKey){
		StdPolicyEngine.setClientKey(clientKey);
	}

	// Internal Setter Method to help build configRequestParameters.
	private ConfigRequestParameters setConfigRequestParameters(String policyName, String onapName, String configName, Map<String, String> configAttributes, UUID requestID){
		ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
		configRequestParameters.setPolicyName(policyName);
		configRequestParameters.setOnapName(onapName);
		configRequestParameters.setConfigName(configName);
		configRequestParameters.setConfigAttributes(configAttributes);
		configRequestParameters.setRequestID(requestID);
		return configRequestParameters;
	}
}