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

import java.util.UUID;

import org.junit.*;
import org.onap.policy.api.DeletePolicyCondition;
import org.onap.policy.api.DeletePolicyParameters;

import static org.junit.Assert.*;

/**
 * The class <code>DeletePolicyParametersTest</code> contains tests for the
 * class <code>{@link DeletePolicyParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class DeletePolicyParametersTest {
	private static final String POLICY_TYPE = "POLICYTYPE";
	private static final String PDP_GROUP = "PDPGROUP";
	private static final String POLICY_NAME = "Apex";
	private static final String POLICY_COMPONENT = "ApexComponent";

	/**
	 * Run the DeletePolicyCondition getDeleteCondition() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetDeleteCondition_1() throws Exception {
		final DeletePolicyParameters fixture = new DeletePolicyParameters();
		fixture.setPolicyComponent("");
		fixture.setPolicyName("");
		fixture.setDeleteCondition(DeletePolicyCondition.ALL);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setPdpGroup("");

		final DeletePolicyCondition result = fixture.getDeleteCondition();

		// add additional test code here
		assertNotNull(result);
		assertEquals("All Versions", result.toString());
		assertEquals("ALL", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the String getPdpGroup() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPdpGroup_1() throws Exception {
		final DeletePolicyParameters fixture = new DeletePolicyParameters();
		fixture.setPolicyComponent("");
		fixture.setPolicyName("");
		fixture.setDeleteCondition(DeletePolicyCondition.ALL);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setPdpGroup("");

		final String result = fixture.getPdpGroup();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getPolicyComponent() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyComponent_1() throws Exception {
		final DeletePolicyParameters fixture = new DeletePolicyParameters();
		fixture.setPolicyComponent("");
		fixture.setPolicyName("");
		fixture.setDeleteCondition(DeletePolicyCondition.ALL);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setPdpGroup("");

		final String result = fixture.getPolicyComponent();

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
	public void testGetRequestID_1() throws Exception {
		final UUID requestID = UUID.fromString("482e90e2-2ad7-4265-9893-4cfe08ef1e3d");

		final DeletePolicyParameters objUnderTest = new DeletePolicyParameters();
		objUnderTest.setPolicyComponent(POLICY_COMPONENT);
		objUnderTest.setPolicyName(POLICY_NAME);
		objUnderTest.setDeleteCondition(DeletePolicyCondition.ALL);
		objUnderTest.setRequestID(requestID);
		objUnderTest.setPdpGroup(PDP_GROUP);
		objUnderTest.setPolicyType(POLICY_TYPE);

		final UUID result = objUnderTest.getRequestID();

		// add additional test code here
		assertNotNull(result);
		assertEquals("482e90e2-2ad7-4265-9893-4cfe08ef1e3d", result.toString());
		assertEquals(4, result.version());
		assertEquals(2, result.variant());
		assertEquals(-7452528304412746179L, result.getLeastSignificantBits());
		assertEquals(5201253920715260517L, result.getMostSignificantBits());
	}

	/**
	 * Run the void setDeleteCondition(DeletePolicyCondition) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetDeleteCondition_1() throws Exception {
		final UUID requestID = UUID.fromString("482e90e2-2ad7-4265-9893-4cfe08ef1e3d");

		final DeletePolicyParameters objUnderTest = new DeletePolicyParameters();
		objUnderTest.setPolicyComponent(POLICY_COMPONENT);
		objUnderTest.setPolicyName(POLICY_NAME);
		objUnderTest.setDeleteCondition(DeletePolicyCondition.ALL);
		objUnderTest.setRequestID(requestID);
		objUnderTest.setPdpGroup(PDP_GROUP);
		objUnderTest.setPolicyType(POLICY_TYPE);

		assertEquals(POLICY_COMPONENT, objUnderTest.getPolicyComponent());
		assertEquals(POLICY_NAME, objUnderTest.getPolicyName());
		assertEquals(POLICY_TYPE, objUnderTest.getPolicyType());
		assertEquals(DeletePolicyCondition.ALL, objUnderTest.getDeleteCondition());
		assertEquals(requestID, objUnderTest.getRequestID());
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
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
	 * @generatedBy CodePro at 6/1/16 1:40 PM
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
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	public static void main(final String[] args) {
		new org.junit.runner.JUnitCore().run(DeletePolicyParametersTest.class);
	}
}
