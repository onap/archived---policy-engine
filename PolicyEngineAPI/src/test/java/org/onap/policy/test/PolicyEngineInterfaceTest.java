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

package org.onap.policy.test;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.mockito.Mockito;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.DeletePolicyCondition;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.EventRequestParameters;
import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.ImportParameters.IMPORT_TYPE;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyEventException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdPolicyChangeResponse;
import org.onap.policy.std.StdPolicyResponse;

import junit.framework.TestCase;

/**
 * The class <code>PolicyEngineInterfaceTest</code> contains tests for the
 * class {@link <code>PolicyEngine</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 5/27/16 10:33 AM
 *
 *
 * @version $Revision$
 */
public class PolicyEngineInterfaceTest extends TestCase {

	private static final Logger logger = FlexLogger.getLogger(PolicyEngineInterfaceTest.class);

	private PolicyEngine policyEngine = null;
	private PolicyEngine mockPolicyEngine = null;
	private Collection<PolicyConfig> policyConfig = null;
	private UUID requestID = UUID.randomUUID();


	PolicyChangeResponse result = null;
	StdPolicyChangeResponse response = new StdPolicyChangeResponse();


	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public PolicyEngineInterfaceTest(String name) {
		super(name);
	}

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	public void setUp() throws Exception {
		try {
			policyEngine = new PolicyEngine("Test/config_pass.properties");
		} catch (PolicyEngineException e) {
			logger.error(e.getMessage());
			fail("PolicyEngine Instantiation Error" + e);
		}
		logger.info("Loaded.. PolicyEngine");

		mockPolicyEngine = Mockito.mock(PolicyEngine.class);
		HttpURLConnection conn = Mockito.mock(HttpURLConnection.class);
		Mockito.when(conn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

	}

	/**
	 * Perform post-test clean up
	 *
	 * @throws Exception
	 *
	 * @see TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		super.tearDown();
		// Add additional tear down code here
	}

	/**
	 * Run the Collection<PolicyConfig> getConfigByPolicyName(String) method
	 * test
	 */
	@SuppressWarnings("deprecation")
	public void testGetConfigByPolicyName() {
		String policyName = null;
		try{
			policyConfig = policyEngine.getConfigByPolicyName(policyName);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	public void testGetConfigByPolicyName2() {
		String policyName = null;

		try{
			policyConfig = policyEngine.getConfigByPolicyName(policyName, requestID);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	/**
	 * Run the Collection<PolicyConfig> getConfig(String) method test
	 */
	@SuppressWarnings("deprecation")
	public void testGetConfig() {
		String onapName = null;

		try{
			policyConfig = policyEngine.getConfig(onapName);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	public void testGetConfig2() {
		String onapName = null;

		try{
			policyConfig = policyEngine.getConfig(onapName,requestID);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}


	@SuppressWarnings("deprecation")
	public void testGetConfig3() {
		String onapName = null;
		String configName = null;

		try{
			policyConfig = policyEngine.getConfig(onapName,configName,requestID);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	public void testGetConfig4() {
		String onapName = null;
		String configName = null;
		Map<String,String> configAttributes = null;

		try{
			policyConfig = policyEngine.getConfig(onapName,configName,configAttributes);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	public void testGetConfig5() {
		String onapName = null;
		String configName = null;
		Map<String,String> configAttributes = null;

		try{
			policyConfig = policyEngine.getConfig(onapName,configName,configAttributes,requestID);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	public void testGetConfig6() {
		ConfigRequestParameters parameters = new ConfigRequestParameters();

		try{
			policyConfig = policyEngine.getConfig(parameters);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}


	/**
	 * Run the Collection<PolicyResponse> sendEvent(Map<String,String>) method
	 * test
	 */
	@SuppressWarnings("deprecation")
	public void testSendEvent()
	{
		Collection<PolicyResponse> result = null;
		Collection<StdPolicyResponse> response = null;
		Map<String,String> eventAttributes = null;

		try {
			Mockito.when(mockPolicyEngine.sendEvent(eventAttributes)).thenReturn(result);
			result = mockPolicyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.error("Exception Occured"+e);
		}


		assertEquals(result,response);

	}

	@SuppressWarnings("deprecation")
	public void testSendEvent2()
	{
		Collection<PolicyResponse> result = null;
		Collection<StdPolicyResponse> response = null;
		Map<String,String> eventAttributes = null;

		try {
			Mockito.when(mockPolicyEngine.sendEvent(eventAttributes,requestID)).thenReturn(result);
			result = mockPolicyEngine.sendEvent(eventAttributes,requestID);
		} catch (PolicyEventException e) {
			logger.error("Exception Occured"+e);
		}


		assertEquals(result,response);

	}

	public void testSendEvent3()
	{
		Collection<PolicyResponse> result = null;
		Collection<StdPolicyResponse> response = null;
		EventRequestParameters parameters = new EventRequestParameters();

		try {
			Mockito.when(mockPolicyEngine.sendEvent(parameters)).thenReturn(result);
			result = mockPolicyEngine.sendEvent(parameters);
		} catch (PolicyEventException e) {
			logger.error("Exception Occured"+e);
		}

		assertEquals(result,response);

	}



	/**
	 * Run the PolicyDecision getDecision(String, Map<String,String>) method
	 * test
	 */
	@SuppressWarnings("deprecation")
	public void testGetDecision()
	{
		String onapComponentName = null;
		Map<String,String> decisionAttributes = null;

		DecisionResponse result = null;

		try {
			Mockito.when(mockPolicyEngine.getDecision(onapComponentName,decisionAttributes)).thenReturn(null);
			result = mockPolicyEngine.getDecision(onapComponentName,decisionAttributes);
		} catch (PolicyDecisionException e) {
			logger.error("Exception Occured"+e);
		}

		assertEquals(result,null);
	}

	@SuppressWarnings("deprecation")
	public void testGetDecision2()
	{
		String onapComponentName = null;
		Map<String,String> decisionAttributes = null;

		DecisionResponse result = null;

		try {
			Mockito.when(mockPolicyEngine.getDecision(onapComponentName,decisionAttributes,requestID)).thenReturn(null);
			result = mockPolicyEngine.getDecision(onapComponentName,decisionAttributes);
		} catch (PolicyDecisionException e) {
			logger.error("Exception Occured"+e);
		}

		assertEquals(result,null);
	}

	public void testGetDecision3()
	{
		DecisionRequestParameters parameters = new DecisionRequestParameters();
		DecisionResponse result = null;

		try {
			Mockito.when(mockPolicyEngine.getDecision(parameters)).thenReturn(null);
			result = mockPolicyEngine.getDecision(parameters);
		} catch (PolicyDecisionException e) {
			logger.error("Exception Occured"+e);
		}

		assertEquals(result,null);
	}

	/**
	 * Run the void setNotification(NotificationScheme, NotificationHandler)
	 * method test
	 */
	public void testSetNotification() {
		// add test code here

		NotificationScheme scheme = null;
		NotificationHandler handler = null;

		Mockito.doNothing().when(mockPolicyEngine).setNotification(scheme, handler);
		mockPolicyEngine.setNotification(scheme, handler);
		//assertTrue(true);
	}

	/**
	 * Run the void clearNotification() method test
	 */
	public void testClearNotification() {
		// add test code here

		Mockito.doNothing().when(mockPolicyEngine).clearNotification();
		mockPolicyEngine.clearNotification();
		//assertTrue(true);
	}

	/**
	 * Run the void setScheme(NotificationScheme) method test
	 */
	public void testSetScheme() {
		NotificationScheme scheme = null;

		Mockito.doNothing().when(mockPolicyEngine).setScheme(scheme);
		mockPolicyEngine.setScheme(scheme);
		//assertTrue(true);
	}

	/**
	 * Run the PDPNotification getNotification() method test
	 */
	public void testGetNotification() {
		PDPNotification result = null;
		StdPDPNotification response = null;
		Mockito.when(mockPolicyEngine.getNotification()).thenReturn(response);
		result = mockPolicyEngine.getNotification();

		assertEquals(result,response);
	}

	/**
	 * Run the String createConfigPolicy(String, String, String, String,
	 * Map<String,String>, String, String, String, UUID) method test
	 */
	@SuppressWarnings("deprecation")
	public void testCreateConfigPolicy()
	{
		String response = "success";
		String result = null;
		try {

			Mockito.when(mockPolicyEngine.createConfigPolicy("testPolicy","test","test","testConfig",null,"OTHER","test","test",null, null, null, null, null)).thenReturn(response);
			result = mockPolicyEngine.createConfigPolicy("testPolicy","test","test","testConfig",null,"OTHER","test","test",null, null, null, null, null);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the String updateConfigPolicy(String, String, String, String,
	 * Map<String,String>, String, String, String, UUID) method test
	 */
	@SuppressWarnings("deprecation")
	public void testUpdateConfigPolicy()
	{
		String response = "success";
		String result = null;
		try {

			Mockito.when(mockPolicyEngine.updateConfigPolicy("testPolicy","test","test","testConfig",null,"OTHER","test","test",null, null, null, null, null)).thenReturn(response);
			result = mockPolicyEngine.updateConfigPolicy("testPolicy","test","test","testConfig",null,"OTHER","test","test",null, null, null, null, null);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the String createConfigFirewallPolicy(String, JsonObject, String,
	 * UUID) method test
	 */
	@SuppressWarnings("deprecation")
	public void testCreateConfigFirewallPolicy() {
		String response = "success";
		String result = null;
		String json = "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"rule1607\",\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"/v0/firewall/pan\",\"serviceGroups\":[{\"name\":\"1607Group\",\"description\":null,\"members\":[{\"type\":\"REFERENCE\",\"name\":\"SList\"},{\"type\":\"REFERENCE\",\"name\":\"Syslog\"}]},{\"name\":\"Syslog\",\"description\":\"NA\",\"type\":\"SERVICE\",\"transportProtocol\":\"udp\",\"appProtocol\":null,\"ports\":\"514\"},{\"name\":\"SList\",\"description\":\"Service List\",\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"8080\"}],\"addressGroups\":[{\"name\":\"1607Group\",\"description\":null,\"members\":[{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"},{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"}]},{\"name\":\"PL_CCE3\",\"description\":\"CCE Routers\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"}]}],\"firewallRuleList\":[{\"position\":\"1\",\"ruleName\":\"1607Rule\",\"fromZones\":[\"Trusted\"],\"toZones\":[\"Untrusted\"],\"negateSource\":false,\"negateDestination\":false,\"sourceList\":[{\"type\":\"REFERENCE\",\"value\":\"PL_CCE3\"},{\"type\":\"REFERENCE\",\"value\":\"1607Group\"}],\"destinationList\":[{\"type\":\"REFERENCE\",\"value\":\"1607Group\"}],\"sourceServices\":[],\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"1607Group\"}],\"action\":\"accept\",\"description\":\"Rule for 1607 templates\",\"enabled\":true,\"log\":true}]}";
		JsonObject jsonObj = buildJSON(json);
		try {

			Mockito.when(mockPolicyEngine.createConfigFirewallPolicy("testPolicy",jsonObj, "test", null, null, null, null, null)).thenReturn(response);
			result = mockPolicyEngine.createConfigFirewallPolicy("testPolicy",jsonObj, "test", null, null, null, null, null);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the String updateConfigFirewallPolicy(String, JsonObject, String,
	 * UUID) method test
	 */
	@SuppressWarnings("deprecation")
	public void testUpdateConfigFirewallPolicy() {
		String response = "success";
		String result = null;
		String json = "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"rule1607\",\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"/v0/firewall/pan\",\"serviceGroups\":[{\"name\":\"1607Group\",\"description\":null,\"members\":[{\"type\":\"REFERENCE\",\"name\":\"SList\"},{\"type\":\"REFERENCE\",\"name\":\"Syslog\"}]},{\"name\":\"Syslog\",\"description\":\"NA\",\"type\":\"SERVICE\",\"transportProtocol\":\"udp\",\"appProtocol\":null,\"ports\":\"514\"},{\"name\":\"SList\",\"description\":\"Service List\",\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"8080\"}],\"addressGroups\":[{\"name\":\"1607Group\",\"description\":null,\"members\":[{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"},{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"}]},{\"name\":\"PL_CCE3\",\"description\":\"CCE Routers\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"}]}],\"firewallRuleList\":[{\"position\":\"1\",\"ruleName\":\"1607Rule\",\"fromZones\":[\"Trusted\"],\"toZones\":[\"Untrusted\"],\"negateSource\":false,\"negateDestination\":false,\"sourceList\":[{\"type\":\"REFERENCE\",\"value\":\"PL_CCE3\"},{\"type\":\"REFERENCE\",\"value\":\"1607Group\"}],\"destinationList\":[{\"type\":\"REFERENCE\",\"value\":\"1607Group\"}],\"sourceServices\":[],\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"1607Group\"}],\"action\":\"accept\",\"description\":\"Rule for 1607 templates\",\"enabled\":true,\"log\":true}]}";
		JsonObject jsonObj = buildJSON(json);
		try {

			Mockito.when(mockPolicyEngine.updateConfigFirewallPolicy("testPolicy",jsonObj, "test", null, null, null, null, null)).thenReturn(response);
			result = mockPolicyEngine.updateConfigFirewallPolicy("testPolicy",jsonObj, "test", null, null, null, null, null);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the PolicyChangeResponse createPolicy(PolicyParameters) method test
	 */
	public void testCreatePolicy() {
		response.setResponseMessage("success");
		PolicyChangeResponse result = null;
		PolicyParameters policyParameters = new PolicyParameters();

        policyParameters.setPolicyClass(PolicyClass.Action); //required
        policyParameters.setPolicyName("test.junitTest"); //required
        policyParameters.setPolicyDescription("testing");  //optional

        //Set the Component Attributes... These are Optional
        Map<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("test", "testing");

        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);

        policyParameters.setActionPerformer("PEP");
        policyParameters.setActionAttribute("testing");
        policyParameters.setRequestID(UUID.randomUUID());

		try {

			//stdPolicyEngine = Mockito.mock(StdPolicyEngine.class);
			//Mockito.when(stdPolicyEngine.callPAP(newPAPPolicy, new String[] {"operation=create", "apiflag=api", "policyType=Action"}, null, "Action")).thenReturn(callPapResponse);
			Mockito.when(mockPolicyEngine.createPolicy(policyParameters)).thenReturn(response);
			result = mockPolicyEngine.createPolicy(policyParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
			logger.error("Exception Occured"+e);
		}
		assertEquals(result, response);
	}

	/**
	 * Run the PolicyChangeResponse updatePolicy(PolicyParameters) method test
	 */
	public void testUpdatePolicy() {
		response.setResponseMessage("success");
		PolicyChangeResponse result = null;
		PolicyParameters policyParameters = new PolicyParameters();

        policyParameters.setPolicyClass(PolicyClass.Action); //required
        policyParameters.setPolicyName("test.junitTest"); //required
        policyParameters.setPolicyDescription("testing");  //optional

        //Set the Component Attributes... These are Optional
        Map<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("test", "testing");

        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);

        policyParameters.setActionPerformer("PEP");
        policyParameters.setActionAttribute("testing");
        policyParameters.setRequestID(UUID.randomUUID());

		try {

			Mockito.when(mockPolicyEngine.updatePolicy(policyParameters)).thenReturn(response);
			result = mockPolicyEngine.updatePolicy(policyParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the String pushPolicy(String, String, String, String, UUID) method
	 * test
	 */
	@SuppressWarnings("deprecation")
	public void testPushPolicy() {
		String response = "Success";
		String result = null;
		try {

			Mockito.when(mockPolicyEngine.pushPolicy("testing","test","Base","default",requestID)).thenReturn(response);
			result = mockPolicyEngine.pushPolicy("testing","test","Base","default",requestID);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		assertEquals(result, response);
	}

	public void testPushPolicy2() {
		PushPolicyParameters policyParameters = new PushPolicyParameters();
		PolicyChangeResponse result = null;

		//String policyScope = null;
		policyParameters.setPolicyName("test.junitTest");
		policyParameters.setPolicyType("Action");
		policyParameters.setPdpGroup("Default");

		try {

			Mockito.when(mockPolicyEngine.pushPolicy(policyParameters)).thenReturn(response);
			result = mockPolicyEngine.pushPolicy(policyParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		assertEquals(result, response);
	}

	/**
	 * Run the PolicyChangeResponse deletePolicy(DeletePolicyParameters) method
	 * test
	 */
	public void testDeletePolicy() {
		DeletePolicyParameters policyParameters = new DeletePolicyParameters();
		PolicyChangeResponse result = null;

		//String policyScope = null;
		policyParameters.setPolicyName("test.junitTest.1.xml");
		policyParameters.setDeleteCondition(DeletePolicyCondition.ALL);
		policyParameters.setPolicyComponent("PAP");
		policyParameters.setPdpGroup("Default");

		try {

			Mockito.when(mockPolicyEngine.deletePolicy(policyParameters)).thenReturn(response);
			result = mockPolicyEngine.deletePolicy(policyParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		assertEquals(result, response);
	}

	/**
	 * Run the PolicyChangeResponse policyEngineImport(ImportParameters) method
	 * test
	 */
	public void testPolicyEngineImport() {
		ImportParameters importParameters = new ImportParameters();
		PolicyChangeResponse result = null;

        importParameters.setFilePath("C:\\Workspaces\\models\\TestingModel\\ControllerServiceSampleSdnlServiceInstance-v0.1.0-SNAPSHOT.zip");
        importParameters.setServiceName("ControllerServiceSampleSdnlServiceInstance");
  	  
        importParameters.setRequestID(UUID.randomUUID());
        importParameters.setServiceType(IMPORT_TYPE.MICROSERVICE);
        importParameters.setVersion("1607-2");


		try {

			Mockito.when(mockPolicyEngine.policyEngineImport(importParameters)).thenReturn(response);
			result = mockPolicyEngine.policyEngineImport(importParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		assertEquals(result, response);
	}

    private static JsonObject buildJSON(String jsonString) {
        JsonObject json = null;;
        if (jsonString != null) {
            StringReader in = null;

            in = new StringReader(jsonString);

            JsonReader jsonReader = Json.createReader(in);
            json = jsonReader.readObject();
        }

        return json;
    }
}
