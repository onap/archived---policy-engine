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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class GetConfigStringStringMapTest {

	private PolicyEngine policyEngine = null;
	private String onapComponentName = null;
	private String configName = null;
	private Map<String,String> configAttributes = new HashMap<String,String>();
	private Collection<PolicyConfig> policyConfig = null;
	private static final Logger logger = FlexLogger.getLogger(GetConfigStringStringMapTest.class);
	@Before
	public void setUp() {
		try {
			policyEngine = new PolicyEngine("Test/config_pass.properties");
		} catch (PolicyEngineException e) {
			logger.error(e.getMessage());
			fail("PolicyEngine Instantiation Error" + e);
		}
		logger.info("Loaded.. PolicyEngine");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringMapFail() {
		onapComponentName = null;
		configName = null;
		configAttributes = null;
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringMapFail1() {
		onapComponentName = null;
		configName = "testFail";
		configAttributes.put("TestValue", "Fail");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringMapFail2() {
		onapComponentName = "TestFail";
		configName = null;
		configAttributes.put("TestValue", "Fail");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringMapFail3() {
		onapComponentName = "TestFail";
		configName = "configFail";
		configAttributes= null;
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringMapfail4() {
		onapComponentName = "TestFail";
		configName = "configFail";
		configAttributes.put("", "");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testGetConfigStringStringMapNotValid() {
		onapComponentName = "TestFail";
		configName = "configFail";
		configAttributes.put("Action:com.test.fail", "Value");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyConfig policyConfig : this.policyConfig){
			logger.info(policyConfig.getPolicyConfigMessage() + " , " +policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig);
			assertEquals(PolicyConfigStatus.CONFIG_NOT_FOUND,policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig.getPolicyConfigMessage());
			assertNull(policyConfig.getType());
			assertNull(policyConfig.toJSON());
			assertNull(policyConfig.toProperties());
			assertNull(policyConfig.toXML());
			assertNull(policyConfig.toOther());
		}
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testGetConfigStringStringMapValidJSON() {
		onapComponentName = "JSON";
		configName = "JSONconfig";
		configAttributes.put("Resource.com:test:resource:json", "Test");
		configAttributes.put("Action.com:test:action:json", "TestJSON");
		configAttributes.put("Subject.com:test:subject:json", "TestSubject");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyConfig policyConfig : this.policyConfig){
			logger.info(policyConfig.getPolicyConfigMessage() + " , " +policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig);
			assertEquals(PolicyConfigStatus.CONFIG_RETRIEVED,policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig.getPolicyConfigMessage());
			assertEquals(PolicyType.JSON,policyConfig.getType());
			assertNotNull(policyConfig.toJSON());
			assertNull(policyConfig.toProperties());
			assertNull(policyConfig.toXML());
			assertNull(policyConfig.toOther());
		}
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testGetConfigStringStringMapValidXML() {
		onapComponentName = "XML";
		configName = "XMLconfig";
		configAttributes.put("Resource.com:test:resource:json", "Test");
		configAttributes.put("Action.com:test:action:json", "TestJSON");
		configAttributes.put("Subject.com:test:subject:json", "TestSubject");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyConfig policyConfig : this.policyConfig){
			logger.info(policyConfig.getPolicyConfigMessage() + " , " +policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig);
			assertEquals(PolicyConfigStatus.CONFIG_RETRIEVED,policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig.getPolicyConfigMessage());
			assertEquals(PolicyType.XML,policyConfig.getType());
			assertNull(policyConfig.toJSON());
			assertNull(policyConfig.toProperties());
			assertNotNull(policyConfig.toXML());
			assertNull(policyConfig.toOther());
		}
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testGetConfigStringStringMapValidProperties() {
		onapComponentName = "Properties";
		configName = "PropConfig" ;
		configAttributes.put("Resource.com:test:resource:json", "Test");
		configAttributes.put("Action.com:test:action:json", "TestJSON");
		configAttributes.put("Subject.com:test:subject:json", "TestSubject");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyConfig policyConfig : this.policyConfig){
			logger.info(policyConfig.getPolicyConfigMessage() + " , " +policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig);
			assertEquals(PolicyConfigStatus.CONFIG_RETRIEVED,policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig.getPolicyConfigMessage());
			assertEquals(PolicyType.PROPERTIES,policyConfig.getType());
			assertNull(policyConfig.toJSON());
			assertNotNull(policyConfig.toProperties());
			assertNull(policyConfig.toXML());
			assertNull(policyConfig.toOther());
		}
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testGetConfigStringStringMapValidOther() {
		onapComponentName = "Other";
		configName = "OtherConfig" ;
		configAttributes.put("Resource.com:test:resource:json", "Test");
		configAttributes.put("Action.com:test:action:json", "TestJSON");
		configAttributes.put("Subject.com:test:subject:json", "TestSubject");
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName, configAttributes);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyConfig policyConfig : this.policyConfig){
			logger.info(policyConfig.getPolicyConfigMessage() + " , " +policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig);
			assertEquals(PolicyConfigStatus.CONFIG_RETRIEVED,policyConfig.getPolicyConfigStatus());
			assertNotNull(policyConfig.getPolicyConfigMessage());
			assertEquals(PolicyType.OTHER,policyConfig.getType());
			assertNull(policyConfig.toJSON());
			assertNull(policyConfig.toProperties());
			assertNull(policyConfig.toXML());
			assertNotNull(policyConfig.toOther());
		}
	}
}
