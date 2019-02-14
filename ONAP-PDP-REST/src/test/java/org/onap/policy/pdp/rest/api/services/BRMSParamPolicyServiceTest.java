/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;

public class BRMSParamPolicyServiceTest {

    BRMSParamPolicyService service = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
        String succeeded = prop.getProperty("xacml.rest.pap.url");
        List<String> paps = Arrays.asList(succeeded.split(","));
        PAPServices.setPaps(paps);
        PAPServices.setJunit(true);
    }

    @Before
    public void setUp() throws Exception {
        PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        policyParameters.setPolicyName("Test.testBRMSPolicy");
        policyParameters.setRequestID(UUID.randomUUID());
        SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateformat3.parse("15/10/2016");
        policyParameters.setTtlDate(date);
        policyParameters.setGuard(true);
        policyParameters.setRiskLevel("5");
        policyParameters.setRiskType("TEST");

        Map<String, String> ruleAttributes = new HashMap<>();
        ruleAttributes.put("templateName", "Sample");
        ruleAttributes.put("controller", "default");
        ruleAttributes.put("SamPoll", "300");
        ruleAttributes.put("value", "abcd");
        Map<AttributeType, Map<String, String>> attributes = new HashMap<>();
        attributes.put(AttributeType.RULE, ruleAttributes);
        policyParameters.setAttributes(attributes);

        String policyName = "testBRMSPolicy";
        String policyScope = "Test";
        service = new BRMSParamPolicyService(policyName, policyScope, policyParameters, date.toString());
    }

    @AfterClass
    public static void tearDownAfterClass() {
        PAPServices.setPaps(null);
        PAPServices.setJunit(false);
    }

    @Test
    public final void testFirewallPolicyService() {
        assertNotNull(service);
    }

    @Test
    public final void testGetValidation() {
        assertTrue(service.getValidation());
    }

    @Test
    public final void testGetMessage() {
        String message = service.getMessage();
        assertNull(message);
    }

    @Test
    public final void testGetResult() throws PolicyException {
        service.getValidation();
        String result = service.getResult(false);
        assertEquals("success", result);
    }
}
