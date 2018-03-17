/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pdp.rest.api.services;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;

public class OptimizationPolicyServiceTest {

	OptimizationPolicyService service = null;

	@Before
	public void setUp() throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
		String succeeded = prop.getProperty("xacml.rest.pap.url");
		List<String> paps = Arrays.asList(succeeded.split(","));
		PAPServices.setPaps(paps);
		PAPServices.setJunit(true);
		
		PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyConfigType(PolicyConfigType.Optimization);
        policyParameters.setPolicyName("Test.testOOFPolicy");
		policyParameters.setOnapName("OOF");
        policyParameters.setRequestID(UUID.randomUUID());
      	SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateformat3.parse("15/10/2016");
		policyParameters.setTtlDate(date);
		policyParameters.setGuard(true);
		policyParameters.setRiskLevel("5");
		policyParameters.setRiskType("TEST");
		policyParameters.setConfigBody("{\"optimization\":\"test\"}");
		String policyName = "testOOFPolicy";
		String policyScope = "Test";
		service = new OptimizationPolicyService(policyName, policyScope, policyParameters, date.toString());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testOptimizationPolicyService() {
		assertNotNull(service);
	}

	@Test
	public final void testGetMessage() {
		String message = service.getMessage();
		assertNull(message);
	}

	@Test
	public final void testGetResult() throws PolicyException {
		String result = service.getResult(false);
		assertEquals("success",result);
	}

}
