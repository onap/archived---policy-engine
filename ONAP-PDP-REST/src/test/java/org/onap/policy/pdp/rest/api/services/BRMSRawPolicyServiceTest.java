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

import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyParameters;

public class BRMSRawPolicyServiceTest {

	private static final String SYSTEM_KEY = "xacml.properties";
	String oldProperty;

	@Before
	public void setUp() throws Exception {

		Properties prop = new Properties();
		prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
		String succeeded = prop.getProperty("xacml.rest.pap.url");
		List<String> paps = Arrays.asList(succeeded.split(","));
		PAPServices.setPaps(paps);
		PAPServices.setJunit(true);
		prop.clear();
		oldProperty = System.getProperty(SYSTEM_KEY);
	}

	@After
	public void tearDown() {
		PAPServices.setPaps(null);
		PAPServices.setJunit(false);
		// Restore the original system property
		if (oldProperty != null) {
			System.setProperty(SYSTEM_KEY, oldProperty);
		} else {
			System.clearProperty(SYSTEM_KEY);
		}
	}

	@Test
	public void testRaw() throws FileNotFoundException, IOException {

		String testVal = "testVal";
		PolicyParameters testParams = new PolicyParameters();

		// Set the system property temporarily
		System.setProperty(SYSTEM_KEY, "xacml.pdp.properties");

		BRMSRawPolicyService service = new BRMSRawPolicyService(testVal, testVal, testParams, testVal);
		assertEquals(false, service.getValidation());
		assertEquals("PE300 - Data Issue:  No Rule Body given", service.getMessage());
	}
}
