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

public class GetConfigStringStringTest {

	private PolicyEngine policyEngine = null;
	private String onapComponentName = null;
	private String configName = null;
	private Collection<PolicyConfig> policyConfig = null;
	private static final Logger logger = FlexLogger.getLogger(GetConfigStringStringTest.class);
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
	public void testGetConfigStringStringFail() {
		onapComponentName = null;
		configName = null;
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringFail1() {
		onapComponentName = null;
		configName = "";
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigStringStringFail2() {
		onapComponentName = "";
		configName = null;
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testGetConfigStringStringNotvalid() {
		onapComponentName = "fail";
		configName = "fail";
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
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
	public void testGetConfigStringStringValidJSON() {
		onapComponentName = "JSON";
		configName = "JSONconfig";
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
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
	public void testGetConfigStringStringValidXML() {
		onapComponentName = "XML";
		configName = "XMLconfig";
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
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
	public void testGetConfigStringStringValidProperties() {
		onapComponentName = "Properties";
		configName = "PropConfig" ;
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
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
	public void testGetConfigStringStringValidOther() {
		onapComponentName = "Other";
		configName = "OtherConfig" ;
		try {
			policyConfig = policyEngine.getConfig(onapComponentName, configName);
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
