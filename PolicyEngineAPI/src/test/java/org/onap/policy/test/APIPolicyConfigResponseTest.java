/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.test;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyType;
import org.onap.policy.models.APIPolicyConfigResponse;
import junit.framework.TestCase; 

public class APIPolicyConfigResponseTest extends TestCase {
	private final String testKey = "testKey";
	private final String testValue = "testValue";
	private final PolicyType testType = PolicyType.JSON;
	private final PolicyConfigStatus testStatus = PolicyConfigStatus.CONFIG_RETRIEVED;
	private final PolicyConfigType testConfigType = PolicyConfigType.BRMS_PARAM;
	
	@Test
	public final void testSetAndGet() {
		APIPolicyConfigResponse response = new APIPolicyConfigResponse();
		response.setConfig(testValue);
		assertEquals(response.getConfig(), testValue);
		
		response.setType(testType);
		assertEquals(response.getType(), testType);
		
		response.setPolicyConfigStatus(testStatus);
		assertEquals(response.getPolicyConfigStatus(), testStatus);
		
		response.setPolicyConfigMessage(testValue);
		assertEquals(response.getPolicyConfigMessage(), testValue);
		
		response.setPolicyName(testValue);
		assertEquals(response.getPolicyName(), testValue);
		
		response.setPolicyType(testConfigType);
		assertEquals(response.getPolicyType(), testConfigType);
		
		response.setPolicyVersion(testValue);
		assertEquals(response.getPolicyVersion(), testValue);
		
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put(testKey, testValue);
		
		response.setMatchingConditions(testMap);
		assertEquals(response.getMatchingConditions(), testMap);
		
		response.setProperty(testMap);
		assertEquals(response.getProperty(), testMap);
		
		response.setResponseAttributes(testMap);
		assertEquals(response.getResponseAttributes(), testMap);
	}
}
