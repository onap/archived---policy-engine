/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pdp.rest.api.services;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;

public class DecisionPolicyServiceTest {

    DecisionPolicyService service = null;

	@Before
	public void setUp() throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
		String succeeded = prop.getProperty("xacml.rest.pap.url");
		List<String> paps = Arrays.asList(succeeded.split(","));
		PAPServices.setPaps(paps);
		PAPServices.setJunit(true);
		
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyClass(PolicyClass.Decision);
        policyParameters.setPolicyName("Test.testDecisionPolicy");
        policyParameters.setOnapName("MSO");
        policyParameters.setPolicyDescription("This is a sample Decision policy UPDATE example with Settings");
        
        Map<String, String> configAttributes = new HashMap<>(); 
        configAttributes.put("Template", "UpdateTemplate");
        configAttributes.put("controller", "default"); 
        configAttributes.put("SamPoll", "30");
        configAttributes.put("value", "abcd"); 
        Map<AttributeType, Map<String,String>> attributes = new HashMap<>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        Map<String, String> settingsMap = new HashMap<>();
        settingsMap.put("server", "5");
        attributes.put(AttributeType.SETTINGS, settingsMap);
        policyParameters.setAttributes(attributes);
        
		List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
		List<String> dynamicRuleAlgorithmFunctions = new LinkedList<>();
		List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
		List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();
		dynamicRuleAlgorithmLabels = Arrays.asList("A1","A2","A3","A4","A5","A6","A7");
		dynamicRuleAlgorithmField1 = Arrays.asList("S_server","cap","cobal","A2","Config","A4","A1");
		dynamicRuleAlgorithmFunctions = Arrays.asList("integer-equal","string-contains","integer-equal","and","integer-greater-than","or","and");
		dynamicRuleAlgorithmField2 = Arrays.asList("90","ca","90","A3","45","A5","A6");      
		policyParameters.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
		policyParameters.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
		policyParameters.setDynamicRuleAlgorithmFunctions(dynamicRuleAlgorithmFunctions);
		policyParameters.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
		
        policyParameters.setRequestID(UUID.randomUUID());
		policyParameters.setGuard(true);
		policyParameters.setRiskLevel("5");
		policyParameters.setRiskType("TEST");
		String policyName = "testDecisionPolicy";
		String policyScope = "Test";
		service = new DecisionPolicyService(policyName, policyScope, policyParameters);
	}

	@After
	public void tearDown() throws Exception {
		PAPServices.setPaps(null);
		PAPServices.setJunit(false);
	}

	@Test
	public final void testDecisionPolicyService() {
		assertNotNull(service);
	}

	@Test
	public final void testGetValidation() {
		assertTrue(service.getValidation());
	}

	@Test
	public final void testGetMessage() {
		String message = service.getMessage();
		assertNull(message);	}

	@Test
	public final void testGetResult() throws PolicyException {
		service.getValidation();
		String result = service.getResult(false);
		assertEquals("success",result);	}

}
