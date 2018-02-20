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
import org.onap.policy.api.DeletePolicyCondition;

import static org.junit.Assert.*;

/**
 * The class <code>DeletePolicyConditionTest</code> contains tests for the class
 * <code>{@link DeletePolicyCondition}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class DeletePolicyConditionTest {

	@Test
	public void testToString_1() throws Exception {
		final DeletePolicyCondition fixture = DeletePolicyCondition.ALL;

		final String result = fixture.toString();

		// add additional test code here
		assertEquals("All Versions", result);

		assertEquals(DeletePolicyCondition.ALL, DeletePolicyCondition.create(DeletePolicyCondition.ALL.name()));
	}

	@Test
	public void testCreate_EnumName_DeletePolicyConditionEnum() {
		for (final DeletePolicyCondition condition : DeletePolicyCondition.values()) {
			final DeletePolicyCondition actualCondition = DeletePolicyCondition.create(condition.name());
			assertEquals(condition, actualCondition);
			assertEquals(condition.toString(), actualCondition.toString());
		}
	}

	@Test
	public void testCreate_StringValue_DeletePolicyConditionEnum() {
		for (final DeletePolicyCondition condition : DeletePolicyCondition.values()) {
			final DeletePolicyCondition actualCondition = DeletePolicyCondition.create(condition.toString());
			assertEquals(condition, actualCondition);
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testException() {
		DeletePolicyCondition.create("foobar");
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
		new org.junit.runner.JUnitCore().run(DeletePolicyConditionTest.class);
	}
}
