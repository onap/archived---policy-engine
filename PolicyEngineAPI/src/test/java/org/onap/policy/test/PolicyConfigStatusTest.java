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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyConfigStatus;

/**
 * The class <code>PolicyConfigStatusTest</code> contains tests for the class
 * <code>{@link PolicyConfigStatus}</code>.
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
	public void testGetStatus_1() throws Exception {
		final String configStatus = "";

		final PolicyConfigStatus result = PolicyConfigStatus.getStatus(configStatus);

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
	public void testGetStatus_2() throws Exception {
		final String configStatus = "retrieved";

		final PolicyConfigStatus result = PolicyConfigStatus.getStatus(configStatus);

		// add additional test code here
		assertNotNull(result);
		assertEquals("retrieved", result.toString());
		assertEquals("CONFIG_RETRIEVED", result.name());
		assertEquals(0, result.ordinal());
	}

	@Test
	public void testCreate_EnumName_PolicyConfigStatusEnum() {
		for (final PolicyConfigStatus policyConfigStatus : PolicyConfigStatus.values()) {
			final PolicyConfigStatus actualPolicyConfigStatus = PolicyConfigStatus.create(policyConfigStatus.name());
			assertEquals(policyConfigStatus, actualPolicyConfigStatus);
			assertEquals(policyConfigStatus.toString(), actualPolicyConfigStatus.toString());
		}
	}

	@Test
	public void testCreate_StringValue_PolicyClassEnum() {
		for (final PolicyConfigStatus policyConfigStatus : PolicyConfigStatus.values()) {
			final PolicyConfigStatus actualPolicyClass = PolicyConfigStatus.create(policyConfigStatus.toString());
			assertEquals(policyConfigStatus, actualPolicyClass);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testException() {
		PolicyConfigStatus.create("foobar");
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args
	 *            the command line arguments
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	public static void main(final String[] args) {
		new org.junit.runner.JUnitCore().run(PolicyConfigStatusTest.class);
	}
}
