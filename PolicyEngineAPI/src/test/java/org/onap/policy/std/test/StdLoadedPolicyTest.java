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
import org.onap.policy.api.UpdateType;
import org.onap.policy.std.StdLoadedPolicy;

import static org.junit.Assert.*;

/**
 * The class <code>StdLoadedPolicyTest</code> contains tests for the class <code>{@link StdLoadedPolicy}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class StdLoadedPolicyTest {
	/**
	 * Run the StdLoadedPolicy() constructor test.
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testStdLoadedPolicy_1()
		throws Exception {
		StdLoadedPolicy result = new StdLoadedPolicy();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the Map<String, String> getMatches() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetMatches_1()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());

		Map<String, String> result = fixture.getMatches();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
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
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());

		String result = fixture.getPolicyName();

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
	public void testGetPolicyName_2()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName((String) null);
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the String getPolicyName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyName_3()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the UpdateType getUpdateType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetUpdateType_1()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());

		UpdateType result = fixture.getUpdateType();

		// add additional test code here
		assertNotNull(result);
		assertEquals("new", result.toString());
		assertEquals("NEW", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the String getVersionNo() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetVersionNo_1()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());

		String result = fixture.getVersionNo();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the void setMatches(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetMatches_1()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());
		Map<String, String> matches = new Hashtable<String, String>();

		fixture.setMatches(matches);

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
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());
		String policyName = "";

		fixture.setPolicyName(policyName);

		// add additional test code here
	}

	/**
	 * Run the void setUpdateType(UpdateType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetUpdateType_1()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());
		UpdateType updateType = UpdateType.NEW;

		fixture.setUpdateType(updateType);

		// add additional test code here
	}

	/**
	 * Run the void setVersionNo(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetVersionNo_1()
		throws Exception {
		StdLoadedPolicy fixture = new StdLoadedPolicy();
		fixture.setPolicyName("");
		fixture.setVersionNo("");
		fixture.setUpdateType(UpdateType.NEW);
		fixture.setMatches(new Hashtable<String, String>());
		String versionNo = "";

		fixture.setVersionNo(versionNo);

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
		new org.junit.runner.JUnitCore().run(StdLoadedPolicyTest.class);
	}
}
