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
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyEventException;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class SendEventTest {

	private PolicyEngine policyEngine = null;
	private Map<String,String> eventAttributes = new HashMap<String,String>();
	private Collection<PolicyResponse> policyResponse = null;
	private static final Logger logger = FlexLogger.getLogger(SendEventTest.class);
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
	@SuppressWarnings("deprecation")
	public void testSendEventFail() {
		eventAttributes = null;
		try {
			policyResponse = policyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyResponse);
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testSendEventFailNull() {
		eventAttributes.put("", "");
		try {
			policyResponse = policyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyResponse);
	}

	// deprecated Test.
	/*@Test
	public void testSendEventFailAttribute() {
		eventAttributes.put("Fail.key", "Value");
		try {
			policyResponse = policyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyResponse.getPolicyResponseMessage());
	}*/

	//@Test
	@SuppressWarnings("deprecation")
	public void testSendEventNotValid() {
		eventAttributes.put("Action.fail", "Value");
		try {
			policyResponse = policyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyResponse policyResponse: this.policyResponse){
			logger.info(policyResponse.getPolicyResponseMessage() + " , " + policyResponse.getPolicyResponseStatus());
			assertNotNull(policyResponse);
			assertEquals(PolicyResponseStatus.NO_ACTION_REQUIRED, policyResponse.getPolicyResponseStatus());
			assertNotNull(policyResponse.getPolicyResponseMessage());
			assertNotNull(policyResponse.getRequestAttributes());
			assertNull(policyResponse.getActionTaken());
			assertNull(policyResponse.getActionAdvised());
		}
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testSendEventActionAdvised() {
		eventAttributes.put("Key", "Value");
		eventAttributes.put("cpu", "80");
		try {
			policyResponse = policyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyResponse policyResponse: this.policyResponse){
			logger.info(policyResponse.getPolicyResponseMessage() + " , " + policyResponse.getPolicyResponseStatus());
			assertNotNull(policyResponse);
			assertEquals(PolicyResponseStatus.ACTION_ADVISED, policyResponse.getPolicyResponseStatus());
			assertNotNull(policyResponse.getPolicyResponseMessage());
			assertNotNull(policyResponse.getRequestAttributes());
			assertNull(policyResponse.getActionTaken());
			assertNotNull(policyResponse.getActionAdvised());
		}
	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testSendEventActionTaken() {
		eventAttributes.put("Key", "Value");
		eventAttributes.put("cpu", "91");
		try {
			policyResponse = policyEngine.sendEvent(eventAttributes);
		} catch (PolicyEventException e) {
			logger.warn(e.getMessage());
		}
		for(PolicyResponse policyResponse: this.policyResponse){
			logger.info(policyResponse.getPolicyResponseMessage() + " , " + policyResponse.getPolicyResponseStatus());
			assertNotNull(policyResponse);
			assertEquals(PolicyResponseStatus.ACTION_TAKEN, policyResponse.getPolicyResponseStatus());
			assertNotNull(policyResponse.getPolicyResponseMessage());
			assertNotNull(policyResponse.getRequestAttributes());
			assertNotNull(policyResponse.getActionTaken());
			assertNull(policyResponse.getActionAdvised());
		}
	}
}
