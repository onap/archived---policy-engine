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

import java.util.LinkedList;
import java.util.List;

import org.junit.*;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.std.AutoClientUEB;

import static org.junit.Assert.*;

/**
 * The class <code>AutoClientUEBTest</code> contains tests for the class <code>{@link AutoClientUEB}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class AutoClientUEBTest {
	/**
	 * Run the AutoClientUEB(String,List<String>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testAutoClientUEB_1()
		throws Exception {
		String url = "";
		String apiKey = "";
		String apiSecret = "";
		List<String> uebURLList = new LinkedList<String>();

		AutoClientUEB result = new AutoClientUEB(url, uebURLList, apiKey, apiSecret);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.ExceptionInInitializerError
		//       at org.apache.log4j.Logger.getLogger(Logger.java:104)
		//       at org.onap.policy.std.AutoClientUEB.<clinit>(AutoClientUEB.java:39)
		assertNotNull(result);
	}

	/**
	 * Run the String getNotficationType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetNotficationType_1()
		throws Exception {

		String result = AutoClientUEB.getNotficationType();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientUEB
		assertNull(result);
	}

	/**
	 * Run the boolean getStatus() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetStatus_1()
		throws Exception {

		boolean result = AutoClientUEB.getStatus();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientUEB
		assertFalse(result);
	}

	/**
	 * Run the String getURL() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetURL_1()
		throws Exception {

		String result = AutoClientUEB.getURL();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientUEB
		assertNotNull(result);
	}

	/**
	 * Run the void setScheme(NotificationScheme) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetScheme_1()
		throws Exception {
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;

		AutoClientUEB.setScheme(scheme);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientUEB
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
		// add set up code here
		List<String> urlList = new LinkedList<String>();
		urlList.add("test2.com");
		@SuppressWarnings("unused")
        AutoClientUEB client = new AutoClientUEB("test.com", urlList, "testKey", "testSecret");
		NotificationHandler handler = null;
		AutoClientUEB.setAuto(NotificationScheme.AUTO_ALL_NOTIFICATIONS, handler);
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
		new org.junit.runner.JUnitCore().run(AutoClientUEBTest.class);
	}
}
