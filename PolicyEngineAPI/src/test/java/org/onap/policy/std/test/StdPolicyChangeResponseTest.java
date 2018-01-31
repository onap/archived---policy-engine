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

package org.onap.policy.std.test;

import org.junit.*;
import org.onap.policy.std.StdPolicyChangeResponse;

import static org.junit.Assert.*;

/**
 * The class <code>StdPolicyChangeResponseTest</code> contains tests for the class <code>{@link StdPolicyChangeResponse}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class StdPolicyChangeResponseTest {
	/**
	 * Run the StdPolicyChangeResponse() constructor test.
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testStdPolicyChangeResponse_1()
		throws Exception {
		StdPolicyChangeResponse result = new StdPolicyChangeResponse();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the int getResponseCode() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetResponseCode_1()
		throws Exception {
		StdPolicyChangeResponse fixture = new StdPolicyChangeResponse();
		fixture.setResponseMessage("");
		fixture.setResponseCode(1);

		int result = fixture.getResponseCode();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the String getResponseMessage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetResponseMessage_1()
		throws Exception {
		StdPolicyChangeResponse fixture = new StdPolicyChangeResponse();
		fixture.setResponseMessage("");
		fixture.setResponseCode(1);

		String result = fixture.getResponseMessage();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the void setResponseCode(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetResponseCode_1()
		throws Exception {
		StdPolicyChangeResponse fixture = new StdPolicyChangeResponse();
		fixture.setResponseMessage("");
		fixture.setResponseCode(1);
		int responseCode = 1;

		fixture.setResponseCode(responseCode);

		// add additional test code here
	}

	/**
	 * Run the void setResponseMessage(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetResponseMessage_1()
		throws Exception {
		StdPolicyChangeResponse fixture = new StdPolicyChangeResponse();
		fixture.setResponseMessage("");
		fixture.setResponseCode(1);
		String responseMessage = "";

		fixture.setResponseMessage(responseMessage);

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
		new org.junit.runner.JUnitCore().run(StdPolicyChangeResponseTest.class);
	}
}
