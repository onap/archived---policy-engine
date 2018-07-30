/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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
package org.onap.policy.rest.util;

import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

public class PolicyValidationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void microServicePolicyTests() throws Exception{
        PolicyValidation validation = new PolicyValidation();
        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
        PolicyParameters policyParameters = new PolicyParameters();

        policyParameters.setPolicyConfigType(PolicyConfigType.MicroService);
        policyParameters.setPolicyName("Test.junitPolicy");
        policyParameters.setPolicyDescription("This is a sample Micro Service policy Create example");
        policyParameters.setOnapName("DCAE");
        policyParameters.setPriority("1");
        String MSjsonString = "{\"service\":\"TOSCA_namingJenny\",\"location\":\"Test  DictMSLoc\",\"uuid\":\"testDict  DCAEUIID\",\"policyName\":\"testModelValidation\",\"description\":\"test\",\"configName\":\"testDict  MSConfName\",\"templateVersion\":\"1607\",\"version\":\"gw12181031\",\"priority\":\"5\",\"policyScope\":\"resource=ResourcetypeVenktest1,service=ServiceName1707,type=Name1707,closedLoopControlName=Retest_retest1\",\"riskType\":\"Test\",\"riskLevel\":\"3\",\"guard\":\"True\",\"content\":{\"police-instance-name\":\"testing\",\"naming-models\":[{\"naming-properties\":[{\"property-value\":\"test\",\"source-endpoint\":\"test\",\"property-name\":\"testPropertyname\",\"increment-sequence\":{\"scope\":\"VNF\",\"start-value\":\"1\",\"length\":\"3\",\"increment\":\"2\"},\"source-system\":\"TOSCA\"}],\"naming-type\":\"testNamingType\",\"naming-recipe\":\"testNamingRecipe\"}]}}";;
        policyParameters.setConfigBody(MSjsonString);
        policyParameters.setRequestID(UUID.randomUUID());
        SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateformat3.parse("15/10/2016");
        policyParameters.setTtlDate(date);
        policyParameters.setGuard(true);
        policyParameters.setRiskLevel("5");
        policyParameters.setRiskType("TEST");
        policyParameters.setRequestID(UUID.randomUUID());


        PolicyRestAdapter policyData = wrapper.populateRequestParameters(policyParameters);
        StringBuilder responseString = validation.validatePolicy(policyData);

        assertNotSame("success", responseString.toString());

    }

    @Test
    public final void testEmailValidation() {
        PolicyValidation validation = new PolicyValidation();
        String result = validation.emailValidation("testemail@test.com", "SUCCESS");
        assertEquals("success", result);
    }

}
