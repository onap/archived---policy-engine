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

import java.util.Hashtable;
import java.util.Map;

import org.junit.*;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.std.StdPolicyResponse;

import static org.junit.Assert.*;

/**
 * The class <code>StdPolicyResponseTest</code> contains tests for the class <code>{@link StdPolicyResponse}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class StdPolicyResponseTest {
	/**
	 * Run the Map<String, String> getActionAdvised() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetActionAdvised_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getActionAdvised();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Map<String, String> getActionTaken() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetActionTaken_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getActionTaken();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the String getPolicyResponseMessage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyResponseMessage_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyResponseMessage();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the PolicyResponseStatus getPolicyResponseStatus() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyResponseStatus_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());

		PolicyResponseStatus result = fixture.getPolicyResponseStatus();

		// add additional test code here
		assertNotNull(result);
		assertEquals("action_advised", result.toString());
		assertEquals("ACTION_ADVISED", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the Map<String, String> getRequestAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetRequestAttributes_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getRequestAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the void setActionAdvised(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetActionAdvised_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> actionAdvised = new Hashtable<String, String>();

		fixture.setActionAdvised(actionAdvised);

		// add additional test code here
	}

	/**
	 * Run the void setActionTaken(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetActionTaken_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> actionTaken = new Hashtable<String, String>();

		fixture.setActionTaken(actionTaken);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyResponseMessage(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyResponseMessage_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());
		String policyResponseMessage = "";

		fixture.setPolicyResponseMessage(policyResponseMessage);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyResponseStatus(PolicyResponseStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyResponseStatus_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());
		PolicyResponseStatus policyResponseStatus = PolicyResponseStatus.ACTION_ADVISED;

		fixture.setPolicyResponseStatus(policyResponseStatus);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyResponseStatus(String,PolicyResponseStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyResponseStatus_2()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());
		String policyResponseMessage = "";
		PolicyResponseStatus policyResponseStatus = PolicyResponseStatus.ACTION_ADVISED;

		fixture.setPolicyResponseStatus(policyResponseMessage, policyResponseStatus);

		// add additional test code here
	}

	/**
	 * Run the void setRequestAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetRequestAttributes_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> requestAttributes = new Hashtable<String, String>();

		fixture.setRequestAttributes(requestAttributes);

		// add additional test code here
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testToString_1()
		throws Exception {
		StdPolicyResponse fixture = new StdPolicyResponse();
		fixture.setPolicyResponseStatus("", PolicyResponseStatus.ACTION_ADVISED);
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.toString();

		// add additional test code here
		assertEquals("PolicyResponse [ policyResponseStatus=action_advised, policyResponseMessage=, ]", result);
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
		new org.junit.runner.JUnitCore().run(StdPolicyResponseTest.class);
	}
}
