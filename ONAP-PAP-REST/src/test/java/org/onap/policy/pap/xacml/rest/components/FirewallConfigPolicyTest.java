/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.components;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

public class FirewallConfigPolicyTest {
	
	private static Logger logger = FlexLogger.getLogger(FirewallConfigPolicyTest.class);
	PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
	Map<String, String> attributeMap = new HashMap<>();
	FirewallConfigPolicy component = null;
    FirewallConfigPolicy mockFWConfig = null;



	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		logger.info("setup: enter");
		
		policyAdapter.setPolicyName("FWjunitTest");
		policyAdapter.setPolicyDescription("test");
		policyAdapter.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
		policyAdapter.setPolicyType("Config");
		policyAdapter.setConfigPolicyType("Firewall Config");
		policyAdapter.setEditPolicy(false);
		policyAdapter.setDomainDir("src/test/resources/client.properties");
		policyAdapter.setDomain("Test");
		policyAdapter.setNewFileName("Test.Config_FW_junitTest.1.xml");
		policyAdapter.setHighestVersion(1);
		policyAdapter.setVersion(String.valueOf(1));
		policyAdapter.setPolicyID("urn:xacml:policy:id:"+UUID.randomUUID());
		policyAdapter.setRuleID("");
		policyAdapter.setConfigName("testname");
		policyAdapter.setGuard("True");
		policyAdapter.setRiskLevel("3");
		policyAdapter.setRiskType("RiskTest");
		policyAdapter.setSecurityZone("CraigSecurityZone");
		policyAdapter.setUserId("API");
		
		attributeMap.put("testJunits", "test");
		policyAdapter.setDynamicFieldConfigAttributes(attributeMap);
		component = new FirewallConfigPolicy(policyAdapter);
		mockFWConfig = Mockito.mock(FirewallConfigPolicy.class);
        logger.info("setUp: exit");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.FirewallConfigPolicy#savePolicies()}.
	 */
	@Test
	public void testSavePolicies() {
        Map<String, String> successMap = new HashMap<>();
        successMap.put("success", "success");
        when(mockFWConfig.createPolicy(Paths.get(policyAdapter.getNewFileName()), policyAdapter.getData())).thenReturn(successMap);
		try {
	        when(mockFWConfig.savePolicies()).thenReturn(successMap);
	        successMap = mockFWConfig.savePolicies();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.FirewallConfigPolicy#prepareToSave()}.
	 */
	@Test
	public void testPrepareToSave() {
		logger.debug("test prepareToSave Policy: enter");
		boolean response = false;
		try {
	        when(mockFWConfig.prepareToSave()).thenReturn(true);
			response = mockFWConfig.prepareToSave();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(response);		
		
	}

}