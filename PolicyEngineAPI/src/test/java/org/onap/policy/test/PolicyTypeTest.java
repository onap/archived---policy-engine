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
import org.onap.policy.api.PolicyType;

/**
 * The class <code>PolicyTypeTest</code> contains tests for the class
 * <code>{@link PolicyType}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class PolicyTypeTest {
	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testToString_1() throws Exception {
		final PolicyType fixture = PolicyType.JSON;

		final String result = fixture.toString();

		// add additional test code here
		assertEquals("json", result);

		assertEquals(PolicyType.JSON, PolicyType.create("json"));
	}

	@Test
	public void testCreate_EnumName_PolicyTypeEnum() {
		for (final PolicyType policyType : PolicyType.values()) {
			final PolicyType actualPolicyType = PolicyType.create(policyType.name());
			assertEquals(policyType, actualPolicyType);
			assertEquals(policyType.toString(), actualPolicyType.toString());
		}
	}

	@Test
	public void testCreate_StringValue_PolicyTypeEnum() {
		for (final PolicyType policyType : PolicyType.values()) {
			final PolicyType actualPolicyType = PolicyType.create(policyType.toString());
			assertEquals(policyType, actualPolicyType);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testException() {
		PolicyType.create("foobar");
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
		new org.junit.runner.JUnitCore().run(PolicyTypeTest.class);
	}
}
