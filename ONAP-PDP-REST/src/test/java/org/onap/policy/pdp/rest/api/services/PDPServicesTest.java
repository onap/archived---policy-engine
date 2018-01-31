/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.pdp.rest.api.models.PDPResponse;
import org.onap.policy.pdp.rest.config.PDPRestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PDPRestConfig.class})
@WebAppConfiguration
public class PDPServicesTest {
	/**
	 * Run the PDPServices() constructor test.
	 *
	 * @generatedBy CodePro at 7/20/17 9:26 AM
	 */
	@Test
	public void testPDPServices_1()
		throws Exception {
		PDPServices result = new PDPServices();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the Collection<PDPResponse> generateRequest(String,UUID,boolean,boolean) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 7/20/17 9:26 AM
	 */
	@Test
	public void testGenerateRequest_1()
		throws Exception {
		DecisionRequestParameters pep = new DecisionRequestParameters();
		Map<String,String> eventAttributes = new HashMap<>();
		eventAttributes.put("TEST", "test");
		pep.setOnapName("te123");
		pep.setDecisionAttributes(eventAttributes);
		PDPServices fixture = new PDPServices();

		//Failure Tests.
		String jsonString = getModel(pep).toString();
		UUID requestID = UUID.randomUUID();

		Collection<PDPResponse> result = fixture.generateRequest(jsonString, requestID, false, true);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.pdp.rest.api.services.PDPServices
		assertNotNull(result);

	}

    private JsonObject getModel(DecisionRequestParameters pep) throws PolicyDecisionException{
        JsonArrayBuilder resourceArray = Json.createArrayBuilder();

        Map<String, String> decisionAttributes = pep.getDecisionAttributes();
        for (Entry<String,String> key : decisionAttributes.entrySet()) {
            JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
            if (key.getValue().matches("[0-9]+")) {
            
            	if ((key.getKey().equals("ErrorCode")) || (key.getKey().equals("WorkStep"))) {

            		resourceBuilder.add("Value", key.getValue());

            	} else {
            
                    int val = Integer.parseInt(key.getValue());
                    resourceBuilder.add("Value", val);

            	}
            
            } else {
                resourceBuilder.add("Value", key.getValue());
            }
            resourceBuilder.add("AttributeId", key.getKey());
            resourceArray.add(resourceBuilder);
        }
        return Json.createObjectBuilder()
                .add("Request", Json.createObjectBuilder()
                                .add("AccessSubject", Json.createObjectBuilder()
                                                .add("Attribute", Json.createObjectBuilder()
                                                                .add("Value", pep.getOnapName())
                                                                .add("AttributeId", "ONAPName")))
                                .add("Resource", Json.createObjectBuilder()
                                                .add("Attribute", resourceArray))
                                .add("Action", Json.createObjectBuilder()
                                                .add("Attribute", Json.createObjectBuilder()
                                                                .add("Value", "DECIDE")
                                                                .add("AttributeId", "urn:oasis:names:tc:xacml:1.0:action:action-id"))))
                .build();
    }

	/**
	 * Run the Collection<PDPResponse> generateRequest(String,UUID,boolean,boolean) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 7/20/17 9:26 AM
	 */
	@Test(expected = org.onap.policy.api.PolicyException.class)
	public void testGenerateRequest_2()
		throws Exception {
		PDPServices fixture = new PDPServices();
		fixture.generateRequest("", UUID.randomUUID(), true, true);
		String jsonString = "";
		UUID requestID = UUID.randomUUID();
		boolean unique = true;
		boolean decide = true;

		Collection<PDPResponse> result = fixture.generateRequest(jsonString, requestID, unique, decide);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 7/20/17 9:26 AM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 7/20/17 9:26 AM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 7/20/17 9:26 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(PDPServicesTest.class);
	}
}