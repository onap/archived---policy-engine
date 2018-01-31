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
import org.onap.policy.std.Matches;

import static org.junit.Assert.*;

/**
 * The class <code>MatchesTest</code> contains tests for the class <code>{@link Matches}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class MatchesTest {
	/**
	 * Run the Matches() constructor test.
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testMatches_1()
		throws Exception {
		Matches result = new Matches();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the Map<String, String> getConfigAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetConfigAttributes_1()
		throws Exception {
		Matches fixture = new Matches();
		fixture.setOnapName("");
		fixture.setConfigAttributes(new Hashtable<String, String>());
		fixture.setConfigName("");

		Map<String, String> result = fixture.getConfigAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the String getConfigName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetConfigName_1()
		throws Exception {
		Matches fixture = new Matches();
		fixture.setOnapName("");
		fixture.setConfigAttributes(new Hashtable<String, String>());
		fixture.setConfigName("");

		String result = fixture.getConfigName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getOnapName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetOnapName_1()
		throws Exception {
		Matches fixture = new Matches();
		fixture.setOnapName("");
		fixture.setConfigAttributes(new Hashtable<String, String>());
		fixture.setConfigName("");

		String result = fixture.getOnapName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the void setConfigAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetConfigAttributes_1()
		throws Exception {
		Matches fixture = new Matches();
		fixture.setOnapName("");
		fixture.setConfigAttributes(new Hashtable<String, String>());
		fixture.setConfigName("");
		Map<String, String> configAttributes = new Hashtable<String, String>();

		fixture.setConfigAttributes(configAttributes);

		// add additional test code here
	}

	/**
	 * Run the void setConfigName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetConfigName_1()
		throws Exception {
		Matches fixture = new Matches();
		fixture.setOnapName("");
		fixture.setConfigAttributes(new Hashtable<String, String>());
		fixture.setConfigName("");
		String configName = "";

		fixture.setConfigName(configName);

		// add additional test code here
	}

	/**
	 * Run the void setOnapName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetOnapName_1()
		throws Exception {
		Matches fixture = new Matches();
		fixture.setOnapName("");
		fixture.setConfigAttributes(new Hashtable<String, String>());
		fixture.setConfigName("");
		String onapName = "";

		fixture.setOnapName(onapName);

		// add additional test code here
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
		new org.junit.runner.JUnitCore().run(MatchesTest.class);
	}
}
