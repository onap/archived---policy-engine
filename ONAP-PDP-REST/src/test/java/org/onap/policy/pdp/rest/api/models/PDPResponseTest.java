/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.pdp.rest.api.models;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.api.PolicyType;

public class PDPResponseTest {
  @Test
  public void testSetAndGet() {
    // Test values
    String message = "testMessage";
    String config = "testConfig";
    String policyName = "testPolicyName";
    String policyVersion = "1.0";
    PolicyResponseStatus policyResponseStatus = PolicyResponseStatus.ACTION_ADVISED;
    PolicyConfigStatus policyConfigStatus = PolicyConfigStatus.CONFIG_RETRIEVED;
    PolicyType type = PolicyType.JSON;
    Map<String, String> property = new HashMap<String, String>();
    PolicyDecision policyDecision = PolicyDecision.PERMIT;

    PDPResponse response = new PDPResponse();

    response.setStatus(message, policyResponseStatus, policyConfigStatus);
    response.setConfig(config);
    assertEquals(config, response.getConfig());
    response.setType(type);
    assertEquals(type, response.getType());
    response.setPolicyConfigStatus(policyConfigStatus);
    assertEquals(policyConfigStatus, response.getPolicyConfigStatus());
    response.setPolicyConfigMessage(message);
    assertEquals(message, response.getPolicyConfigMessage());
    response.setProperty(property);
    assertEquals(property, response.getProperty());
    response.setPolicyName(policyName);
    assertEquals(policyName, response.getPolicyName());
    response.setPolicyVersion(policyVersion);
    assertEquals(policyVersion, response.getPolicyVersion());
    response.setMatchingConditions(property);
    assertEquals(property, response.getMatchingConditions());
    response.setResponseAttributes(property);
    assertEquals(property, response.getResponseAttributes());
    response.setPolicyResponseStatus(policyResponseStatus);
    assertEquals(policyResponseStatus, response.getPolicyResponseStatus());
    response.setDecision(policyDecision);
    assertEquals(policyDecision, response.getDecision());
    response.setDetails(message);
    assertEquals(message, response.getDetails());
    response.setActionAdvised(property);
    assertEquals(property, response.getActionAdvised());
    response.setActionTaken(property);
    assertEquals(property, response.getActionTaken());
    response.setRequestAttributes(property);
    assertEquals(property, response.getRequestAttributes());
    response.setPolicyResponseMessage(message);
    assertEquals(message, response.getPolicyResponseMessage());
  }
}
