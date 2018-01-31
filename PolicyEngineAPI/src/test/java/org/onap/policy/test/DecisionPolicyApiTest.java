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
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdPolicyChangeResponse;

import junit.framework.TestCase;

/**
 * The class <code>DecisionPolicyApiTest</code> contains tests for the class
 * {@link <code>PolicyEngine</code>}
 *
 * @pattern JUnit Test Case
 * *
 */
public class DecisionPolicyApiTest extends TestCase {

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

        policyParameters.setPolicyClass(PolicyClass.Decision); //required
        policyParameters.setPolicyName("test.junitTest"); //required
        policyParameters.setOnapName("test");
        policyParameters.setPolicyDescription("testing");  //optional
        //policyParameters.setPolicyScope("test"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI

        //Set the Component Attributes... These are Optional
        Map<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("test", "testing");

        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);

      //Set the settings... These are Optional
/*        Map<String, String> settingsMap = new HashMap<String, String>();
        settingsMap.put("server", "5");

        Map<AttributeType, Map<String,String>> settings = new HashMap<AttributeType, Map<String, String>>();
        settings.put(AttributeType.SETTINGS, settingsMap);
        policyParameters.setSettings(settings);*/

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
	public final void testCreatePolicyNullOnapName() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setOnapName(null);
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
	public final void testUpdatePolicyNullOnapName() {
		response.setResponseMessage("PE500 - Process Flow Issue: :500:");
		policyParameters.setOnapName(null);
		try{
			result = policyEngine.updatePolicy(policyParameters);
		} catch (Exception e){
			logger.warn(e.getMessage());
		}
		assertEquals(result.getResponseMessage(), response.getResponseMessage());
	}
}
