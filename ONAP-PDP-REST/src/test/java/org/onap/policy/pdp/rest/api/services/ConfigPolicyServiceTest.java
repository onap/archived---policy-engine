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

import org.junit.Test;
import org.onap.policy.api.PolicyParameters;

public class ConfigPolicyServiceTest {
	@Test
	public void testRaw() throws FileNotFoundException, IOException  {
		Properties prop = new Properties();
		prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
		String succeeded = prop.getProperty("xacml.rest.pap.url");
		List<String> paps = Arrays.asList(succeeded.split(","));
		PAPServices.setPaps(paps);
		PAPServices.junit = true;
		
		String systemKey = "xacml.properties";
		String testVal = "testVal";
		PolicyParameters testParams = new PolicyParameters();
		
		// Set the system property temporarily
		String oldProperty = System.getProperty(systemKey);
		System.setProperty(systemKey, "xacml.pdp.properties");
		
		ConfigPolicyService service = new ConfigPolicyService(testVal, testVal, testParams, testVal);
		assertEquals(service.getValidation(), false);
		assertEquals(service.getMessage(), "PE300 - Data Issue: No Config Body given.");
		
		// Restore the original system property
		if (oldProperty != null) {
			System.setProperty(systemKey, oldProperty);
		}
		else {
			System.clearProperty(systemKey);
		} 
	} 
}
