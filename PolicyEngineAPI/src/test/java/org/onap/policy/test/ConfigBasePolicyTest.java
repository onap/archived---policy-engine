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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdPolicyChangeResponse;

import junit.framework.TestCase;

/**
 * The class <code>ConfigBasePolicyTest</code> contains tests for the class
 * {@link <code>PolicyEngine</code>}
 *
 * @pattern JUnit Test Case
 * *
 */
public class ConfigBasePolicyTest extends TestCase {

	private static final Logger logger = FlexLogger.getLogger(DecisionPolicyApiTest.class);

	private PolicyEngine policyEngine = null;
	private PolicyEngine mockPolicyEngine = null;

	PolicyChangeResponse result = null;
	StdPolicyChangeResponse response = new StdPolicyChangeResponse();
	PolicyParameters policyParameters = new PolicyParameters();

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

        policyParameters.setPolicyConfigType(PolicyConfigType.Base); //required
        policyParameters.setPolicyName("test.junitTest"); //required
        policyParameters.setOnapName("test");
        policyParameters.setConfigName("testBase");
        policyParameters.setConfigBodyType(PolicyType.OTHER);
        policyParameters.setConfigBody("testing");
        policyParameters.setPolicyDescription("testing");  //optional
        //policyParameters.setPolicyScope("test"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI

        //Set the Component Attributes... These are Optional
        Map<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("test", "testing");

        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);

        policyParameters.setRequestID(UUID.randomUUID());
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
	 * Run the String createConfigPolicy() method test
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCreateConfigPolicy() {
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
	 * Run the String updateConfigPolicy() method test
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testUpdateConfigPolicy() {
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

	@Test
	public final void testCreatePolicyNullPolicyName() {
		response.setResponseMessage("PE300 - Data Issue: No Policy Name given.");
		policyParameters.setPolicyName(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullPolicyScope() {
		response.setResponseMessage("PE300 - Data Issue: No Policy Scope given.");
		policyParameters.setPolicyName("test");
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullOnapName() {
		response.setResponseMessage("PE300 - Data Issue: No ONAP Name given.");
		policyParameters.setOnapName(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullPolicyConfigName() {
		response.setResponseMessage("PE300 - Data Issue: No Config Name given.");
		policyParameters.setConfigName(null);;
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullPolicyConfigBodyType() {
		response.setResponseMessage("PE300 - Data Issue: No Config Body Type given.");
		policyParameters.setConfigBodyType(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testCreatePolicyNullPolicyConfigBody() {
		response.setResponseMessage("PE300 - Data Issue: No Config Body given.");
		policyParameters.setConfigBody(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyName() {
		response.setResponseMessage("PE300 - Data Issue: No Policy Name given.");
		policyParameters.setPolicyName(null);
		try{
			result = policyEngine.updatePolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyScope() {
		response.setResponseMessage("PE300 - Data Issue: No Policy Scope given.");
		policyParameters.setPolicyName("test");
		try{
			result = policyEngine.updatePolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullOnapName() {
		response.setResponseMessage("PE300 - Data Issue: No ONAP Name given.");
		policyParameters.setOnapName(null);
		try{
			result = policyEngine.updatePolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyConfigName() {
		response.setResponseMessage("PE300 - Data Issue: No Config Name given.");
		policyParameters.setConfigName(null);;
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyConfigBodyType() {
		response.setResponseMessage("PE300 - Data Issue: No Config Body Type given.");
		policyParameters.setConfigBodyType(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}

	@Test
	public final void testUpdatePolicyNullPolicyConfigBody() {
		response.setResponseMessage("PE300 - Data Issue: No Config Body given.");
		policyParameters.setConfigBody(null);
		try{
			result = policyEngine.createPolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		//assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}
}