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

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.junit.*;
import org.onap.policy.api.ConfigRequestParameters;

import static org.junit.Assert.*;

/**
 * The class <code>ConfigRequestParametersTest</code> contains tests for the class <code>{@link ConfigRequestParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class ConfigRequestParametersTest {
	/**
	 * Run the ConfigRequestParameters() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testConfigRequestParameters_1()
		throws Exception {

		ConfigRequestParameters result = new ConfigRequestParameters();

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getPolicyName());
		assertEquals(null, result.getConfigName());
		assertEquals(null, result.getConfigAttributes());
		assertEquals(null, result.getRequestID());
		assertEquals(null, result.getOnapName());
		assertEquals(Boolean.FALSE, result.getUnique());
	}

	/**
	 * Run the ConfigRequestParameters(String,String,String,Map<String,String>,Boolean,UUID) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testConfigRequestParameters_2()
		throws Exception {
		String policyName = "";
		String onapComponentName = "";
		String configName = "";
		Map<String, String> configAttributes = new Hashtable<String, String>();
		Boolean unique = new Boolean(true);
		UUID requestID = UUID.randomUUID();

		ConfigRequestParameters result = createConfigRequest(policyName, onapComponentName, configName, configAttributes, unique, requestID);

		// add additional test code here
		assertNotNull(result);
		assertEquals("", result.getPolicyName());
		assertEquals("", result.getConfigName());
		assertEquals("", result.getOnapName());
		assertEquals(Boolean.TRUE, result.getUnique());
	}

	/**
	 * Run the Map<String, String> getConfigAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetConfigAttributes_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());

		Map<String, String> result = fixture.getConfigAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the String getConfigName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetConfigName_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());

		String result = fixture.getConfigName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getOnapName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetOnapName_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());

		String result = fixture.getOnapName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getPolicyName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyName_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the UUID getRequestID() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetRequestID_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.fromString("6b5aa070-90bc-46a6-9a59-e1fe526df7ae"));

		UUID result = fixture.getRequestID();

		// add additional test code here
		assertNotNull(result);
		assertEquals("6b5aa070-90bc-46a6-9a59-e1fe526df7ae", result.toString());
		assertEquals(4, result.version());
		assertEquals(2, result.variant());
		assertEquals(-7324574836520519762L, result.getLeastSignificantBits());
		assertEquals(7735671715287287462L, result.getMostSignificantBits());
	}

	/**
	 * Run the Boolean getUnique() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetUnique_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());

		Boolean result = fixture.getUnique();

		// add additional test code here
		assertNotNull(result);
		assertEquals("true", result.toString());
		assertEquals(true, result.booleanValue());
	}

	/**
	 * Run the void makeUnique(Boolean) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testMakeUnique_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());
		Boolean unique = new Boolean(true);

		fixture.makeUnique(unique);

		// add additional test code here
	}

	/**
	 * Run the void setConfigAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetConfigAttributes_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());
		Map<String, String> configAttributes = new Hashtable<String, String>();

		fixture.setConfigAttributes(configAttributes);

		// add additional test code here
	}

	/**
	 * Run the void setConfigName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetConfigName_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());
		String configName = "";

		fixture.setConfigName(configName);

		// add additional test code here
	}

	/**
	 * Run the void setOnapName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetOnapName_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());
		String onapComponentName = "";

		fixture.setOnapName(onapComponentName);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyName_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());
		String policyName = "";

		fixture.setPolicyName(policyName);

		// add additional test code here
	}

	/**
	 * Run the void setRequestID(UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetRequestID_1()
		throws Exception {
		ConfigRequestParameters fixture = createConfigRequest("", "", "", new Hashtable<String, String>(), new Boolean(true), UUID.randomUUID());
		UUID requestID = UUID.randomUUID();

		fixture.setRequestID(requestID);

		// add additional test code here
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
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
	 * @generatedBy CodePro at 6/1/16 1:40 PM
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
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ConfigRequestParametersTest.class);
	}

	private ConfigRequestParameters createConfigRequest(String policyName, String onapComponentName, String configName, Map<String,String> configAttributes, Boolean unique, UUID requestID){
		ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
		configRequestParameters.setRequestID(requestID);
		configRequestParameters.setPolicyName(policyName);
		configRequestParameters.setOnapName(onapComponentName);
		configRequestParameters.setConfigName(configName);
		configRequestParameters.setConfigAttributes(configAttributes);
		configRequestParameters.makeUnique(unique);
		return configRequestParameters;
	}
}
