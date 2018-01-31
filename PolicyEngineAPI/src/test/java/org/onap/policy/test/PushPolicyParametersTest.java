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
import org.onap.policy.api.PushPolicyParameters;

import static org.junit.Assert.*;

/**
 * The class <code>PushPolicyParametersTest</code> contains tests for the class <code>{@link PushPolicyParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class PushPolicyParametersTest {
	/**
	 * Run the PushPolicyParameters() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testPushPolicyParameters_1()
		throws Exception {

		PushPolicyParameters result = new PushPolicyParameters();

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getPolicyName());
		assertEquals(null, result.getRequestID());
		assertEquals(null, result.getPolicyType());
		assertEquals(null, result.getPdpGroup());
	}

	/**
	 * Run the PushPolicyParameters(String,String,String,UUID) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testPushPolicyParameters_2()
		throws Exception {
		String policyName = "";
		String policyType = "";
		String pdpGroup = "";
		UUID requestID = UUID.randomUUID();

		PushPolicyParameters result = new PushPolicyParameters(policyName, policyType, pdpGroup, requestID);

		// add additional test code here
		assertNotNull(result);
		assertEquals("", result.getPolicyName());
		assertEquals("", result.getPolicyType());
		assertEquals("", result.getPdpGroup());
	}

	/**
	 * Run the String getPdpGroup() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPdpGroup_1()
		throws Exception {
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());

		String result = fixture.getPdpGroup();

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
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getPolicyType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyType_1()
		throws Exception {
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());

		String result = fixture.getPolicyType();

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
	public void testGetRequestID()
		throws Exception {
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.fromString("d1dbaac1-0944-4f07-9ce7-733c697537ea"));

		UUID result = fixture.getRequestID();

		// add additional test code here
		assertNotNull(result);
		assertEquals("d1dbaac1-0944-4f07-9ce7-733c697537ea", result.toString());
		assertEquals(4, result.version());
		assertEquals(2, result.variant());
		assertEquals(-7140611980868110358L, result.getLeastSignificantBits());
		assertEquals(-3324876153822097657L, result.getMostSignificantBits());
	}

	/**
	 * Run the void setPdpGroup(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPdpGroup_1()
		throws Exception {
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());
		String pdpGroup = "";

		fixture.setPdpGroup(pdpGroup);

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
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());
		String policyName = "";

		fixture.setPolicyName(policyName);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyType(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyType_1()
		throws Exception {
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());
		String policyType = "";

		fixture.setPolicyType(policyType);

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
		PushPolicyParameters fixture = new PushPolicyParameters("", "", "", UUID.randomUUID());
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
		new org.junit.runner.JUnitCore().run(PushPolicyParametersTest.class);
	}
}
