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
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdPolicyChangeResponse;

import junit.framework.TestCase;

public class ConfigFirewallPolicyTest extends TestCase {

	private static final Logger logger = FlexLogger.getLogger(DecisionPolicyApiTest.class);

	private PolicyEngine policyEngine = null;
	private PolicyEngine mockPolicyEngine = null;

	PolicyChangeResponse result = null;
	StdPolicyChangeResponse response = new StdPolicyChangeResponse();
	PolicyParameters policyParameters = new PolicyParameters();
	String json = null;

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

        policyParameters.setPolicyConfigType(PolicyConfigType.Firewall); //required
        policyParameters.setPolicyName("test.junitTest"); //required

        json = "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"rule1607\",\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"/v0/firewall/pan\",\"serviceGroups\":[{\"name\":\"1607Group\",\"description\":null,\"members\":[{\"type\":\"REFERENCE\",\"name\":\"SList\"},{\"type\":\"REFERENCE\",\"name\":\"Syslog\"}]},{\"name\":\"Syslog\",\"description\":\"NA\",\"type\":\"SERVICE\",\"transportProtocol\":\"udp\",\"appProtocol\":null,\"ports\":\"514\"},{\"name\":\"SList\",\"description\":\"Service List\",\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"8080\"}],\"addressGroups\":[{\"name\":\"1607Group\",\"description\":null,\"members\":[{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"},{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"}]},{\"name\":\"PL_CCE3\",\"description\":\"CCE Routers\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"10.11.12.13/14\"}]}],\"firewallRuleList\":[{\"position\":\"1\",\"ruleName\":\"1607Rule\",\"fromZones\":[\"Trusted\"],\"toZones\":[\"Untrusted\"],\"negateSource\":false,\"negateDestination\":false,\"sourceList\":[{\"type\":\"REFERENCE\",\"value\":\"PL_CCE3\"},{\"type\":\"REFERENCE\",\"value\":\"1607Group\"}],\"destinationList\":[{\"type\":\"REFERENCE\",\"value\":\"1607Group\"}],\"sourceServices\":[],\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"1607Group\"}],\"action\":\"accept\",\"description\":\"Rule for 1607 templates\",\"enabled\":true,\"log\":true}]}";

        policyParameters.setConfigBody(buildJSON(json).toString());

        //policyParameters.setPolicyScope("test"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI
        policyParameters.setRequestID(UUID.randomUUID());
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
	 * Run the PolicyChangeResponse createPolicy(PolicyParameters) method test
	 */
	public void testCreatePolicy() {
		response.setResponseMessage("success");
		PolicyChangeResponse result = null;
		try {

			Mockito.when(mockPolicyEngine.createPolicy(policyParameters)).thenReturn(response);
			result = mockPolicyEngine.createPolicy(policyParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the String createConfigFirewallPolicy() method test
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCreateConfigFirewallPolicy() {
		String response = "success";
		String result = null;
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
	 * Run the PolicyChangeResponse updatePolicy(PolicyParameters) method test
	 */
	public void testUpdatePolicy() {
		response.setResponseMessage("success");
		PolicyChangeResponse result = null;
		try {

			Mockito.when(mockPolicyEngine.updatePolicy(policyParameters)).thenReturn(response);
			result = mockPolicyEngine.updatePolicy(policyParameters);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the String updateConfigFirewallPolicy() method test
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testUpdateConfigFirewallPolicy() {
		String response = "success";
		String result = null;
		JsonObject jsonObj = buildJSON(json);
		try {

			Mockito.when(mockPolicyEngine.updateConfigFirewallPolicy("testPolicy",jsonObj, "test", null, null, null, null, null)).thenReturn(response);
			result = mockPolicyEngine.updateConfigFirewallPolicy("testPolicy",jsonObj, "test", null, null, null, null, null);

		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	@Test
	public final void testCreatePolicyNullPolicyName() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setPolicyName(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullPolicyScope() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setPolicyName("test");
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullPolicyConfigBody() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setConfigBody(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyName() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setPolicyName(null);
		try{
			result = policyEngine.updatePolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyScope() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setPolicyName("test");
		try{
			result = policyEngine.updatePolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyConfigBody() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setConfigBody(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}
}
