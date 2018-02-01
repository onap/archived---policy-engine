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

import org.junit.*;
import org.onap.policy.api.PolicyConfigStatus;

import static org.junit.Assert.*;

/**
 * The class <code>PolicyConfigStatusTest</code> contains tests for the class <code>{@link PolicyConfigStatus}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class PolicyConfigStatusTest {
	/**
	 * Run the PolicyConfigStatus getStatus(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetStatus_1()
		throws Exception {
		String configStatus = "";

		PolicyConfigStatus result = PolicyConfigStatus.getStatus(configStatus);

		// add additional test code here
		assertNotNull(result);
		assertEquals("not_found", result.toString());
		assertEquals("CONFIG_NOT_FOUND", result.name());
		assertEquals(1, result.ordinal());
		
		assertEquals(PolicyConfigStatus.CONFIG_RETRIEVED, PolicyConfigStatus.create("retrieved"));
	}

	/**
	 * Run the PolicyConfigStatus getStatus(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetStatus_2()
		throws Exception {
		String configStatus = "retrieved";

		PolicyConfigStatus result = PolicyConfigStatus.getStatus(configStatus);

		// add additional test code here
		assertNotNull(result);
		assertEquals("retrieved", result.toString());
		assertEquals("CONFIG_RETRIEVED", result.name());
		assertEquals(0, result.ordinal());
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testToString_1()
		throws Exception {
		PolicyConfigStatus fixture = PolicyConfigStatus.CONFIG_NOT_FOUND;

		String result = fixture.toString();

		// add additional test code here
		assertEquals("not_found", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testException () {
		PolicyConfigStatus.create("foobar");
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
		new org.junit.runner.JUnitCore().run(PolicyConfigStatusTest.class);
	}
}
