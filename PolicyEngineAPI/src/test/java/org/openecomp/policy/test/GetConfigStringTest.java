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

package org.openecomp.policy.test;

import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyEngineException;
import org.openecomp.policy.api.PolicyType;

import org.openecomp.policy.common.logging.flexlogger.*; 

public class GetConfigStringTest extends TestCase{
	
	private PolicyEngine policyEngine = null;
	private String eCOMPComponentName = null;
	private Collection<PolicyConfig> policyConfig = null;
	private static final Logger logger = FlexLogger.getLogger(GetConfigStringTest.class);
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
	
	//@Test
	public void testGetConfigStringFail() {
		eCOMPComponentName = null;
		try {
			policyConfig = policyEngine.getConfig(eCOMPComponentName);
		} catch (PolicyConfigException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}
	
	/*@Test
	public void testGetConfigStringNotvalid() {
		eCOMPComponentName = "fail";
		try {
			policyConfig = policyEngine.getConfig(eCOMPComponentName);
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
	*/
	
	/*@Test
	public void testGetConfigStringValidJSON() {
		eCOMPComponentName = "JSON";
		try {
			policyConfig = policyEngine.getConfig(eCOMPComponentName);
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
	*/
	/*@Test
	public void testGetConfigStringValidXML() {
		eCOMPComponentName = "XML";
		try {
			policyConfig = policyEngine.getConfig(eCOMPComponentName);
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
	*/
	/*@Test
	public void testGetConfigStringValidProperties() {
		eCOMPComponentName = "Properties";
		try {
			policyConfig = policyEngine.getConfig(eCOMPComponentName);
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
	*/
	/*@Test
	public void testGetConfigStringValidOther() {
		eCOMPComponentName = "Other";
		try {
			policyConfig = policyEngine.getConfig(eCOMPComponentName);
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
	*/
}
