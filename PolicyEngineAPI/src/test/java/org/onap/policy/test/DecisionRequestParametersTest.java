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
import org.onap.policy.api.DecisionRequestParameters;

import static org.junit.Assert.*;

/**
 * The class <code>DecisionRequestParametersTest</code> contains tests for the class <code>{@link DecisionRequestParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class DecisionRequestParametersTest {
	/**
	 * Run the DecisionRequestParameters() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testDecisionRequestParameters_1()
		throws Exception {

		DecisionRequestParameters result = new DecisionRequestParameters();

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getOnapName());
		assertEquals(null, result.getDecisionAttributes());
		assertEquals(null, result.getRequestID());
	}

	/**
	 * Run the DecisionRequestParameters(String,Map<String,String>,UUID) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testDecisionRequestParameters_2()
		throws Exception {
		String onapComponentName = "";
		Map<String, String> decisionAttributes = new Hashtable<String, String>();
		UUID requestID = UUID.randomUUID();

		DecisionRequestParameters result = new DecisionRequestParameters(onapComponentName, decisionAttributes, requestID);

		// add additional test code here
		assertNotNull(result);
		assertEquals("", result.getOnapName());
	}

	/**
	 * Run the Map<String, String> getDecisionAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetDecisionAttributes_1()
		throws Exception {
		DecisionRequestParameters fixture = new DecisionRequestParameters("", new Hashtable<String, String>(), UUID.randomUUID());

		Map<String, String> result = fixture.getDecisionAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the String getONAPComponentName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetONAPComponentName_1()
		throws Exception {
		DecisionRequestParameters fixture = new DecisionRequestParameters("", new Hashtable<String, String>(), UUID.randomUUID());

		String result = fixture.getOnapName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the UUID getRequestID() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetRequestID_1()
		throws Exception {
		DecisionRequestParameters fixture = new DecisionRequestParameters("", new Hashtable<String, String>(), UUID.fromString("d1db6a6d-7140-4864-8200-6b541261fdd2"));

		UUID result = fixture.getRequestID();

		// add additional test code here
		assertNotNull(result);
		assertEquals("d1db6a6d-7140-4864-8200-6b541261fdd2", result.toString());
		assertEquals(4, result.version());
		assertEquals(2, result.variant());
		assertEquals(-9079138839949083182L, result.getLeastSignificantBits());
		assertEquals(-3324946881598961564L, result.getMostSignificantBits());
	}

	/**
	 * Run the void setDecisionAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetDecisionAttributes_1()
		throws Exception {
		DecisionRequestParameters fixture = new DecisionRequestParameters("", new Hashtable<String, String>(), UUID.randomUUID());
		Map<String, String> decisionAttributes = new Hashtable<String, String>();

		fixture.setDecisionAttributes(decisionAttributes);

		// add additional test code here
	}

	/**
	 * Run the void setONAPComponentName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetONAPComponentName_1()
		throws Exception {
		DecisionRequestParameters fixture = new DecisionRequestParameters("", new Hashtable<String, String>(), UUID.randomUUID());
		String onapComponentName = "";

		fixture.setOnapName(onapComponentName);

		// add additional test code here
	}

	/**
	 * Run the void setRequestID(UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetRequestID_1()
		throws Exception {
		DecisionRequestParameters fixture = new DecisionRequestParameters("", new Hashtable<String, String>(), UUID.randomUUID());
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
	 * @generatedBy CodePro at 6/1/16 1:41 PM
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
	 * @generatedBy CodePro at 6/1/16 1:41 PM
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
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(DecisionRequestParametersTest.class);
	}
}
