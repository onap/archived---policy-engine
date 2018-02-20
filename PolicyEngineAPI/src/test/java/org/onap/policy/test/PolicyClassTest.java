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

import org.junit.Test;
import org.onap.policy.api.PolicyClass;

/**
 * The class <code>PolicyClassTest</code> contains tests for the class
 * <code>{@link PolicyClass}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class PolicyClassTest {

	@Test
	public void testToString_1() {
		final PolicyClass fixture = PolicyClass.Action;

		final String result = fixture.toString();

		// add additional test code here
		assertEquals("Action", result);

		assertEquals(PolicyClass.Decision, PolicyClass.create(PolicyClass.Decision.toString()));
	}

	@Test
	public void testCreate_EnumName_PolicyClassEnum() {
		for (final PolicyClass policyClass : PolicyClass.values()) {
			final PolicyClass actualPolicyClass = PolicyClass.create(policyClass.name());
			assertEquals(policyClass, actualPolicyClass);
			assertEquals(policyClass.toString(), actualPolicyClass.toString());
		}
	}

	@Test
	public void testCreate_StringValue_PolicyClassEnum() {
		for (final PolicyClass policyClass : PolicyClass.values()) {
			final PolicyClass actualPolicyClass = PolicyClass.create(policyClass.toString());
			assertEquals(policyClass, actualPolicyClass);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testException() {
		PolicyClass.create("foobar");
	}

	/**
	 * Launch the test.
	 *
	 * @param args
	 *            the command line arguments
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	public static void main(final String[] args) {
		new org.junit.runner.JUnitCore().run(PolicyClassTest.class);
	}
}
