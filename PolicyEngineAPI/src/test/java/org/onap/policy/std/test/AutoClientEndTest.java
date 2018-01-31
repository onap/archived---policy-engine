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

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.std.AutoClientEnd;
import org.onap.policy.std.StdPolicyEngine;

/**
 * The class <code>AutoClientEndTest</code> contains tests for the class <code>{@link AutoClientEnd}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class AutoClientEndTest {
	/**
	 * Run the AutoClientEnd() constructor test.
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testAutoClientEnd_1()
		throws Exception {
		AutoClientEnd result = new AutoClientEnd();
		assertNotNull(result);
		// add additional test code here
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

		boolean result = AutoClientEnd.getStatus();

		assertNotNull(result);
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

		String result = AutoClientEnd.getURL();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientEnd
		assertNotNull(result);
	}


	/**
	 * Run the void setAuto(NotificationScheme,NotificationHandler) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetAuto()
		throws Exception {
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
		NotificationHandler handler = null;

		AutoClientEnd.setAuto(scheme, handler);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.ExceptionInInitializerError
		//       at org.apache.log4j.Logger.getLogger(Logger.java:104)
		//       at org.onap.policy.std.AutoClientEnd.<clinit>(AutoClientEnd.java:39)
	}

	/**
	 * Run the void setScheme(NotificationScheme) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetScheme()
		throws Exception {

		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
		AutoClientEnd.setScheme(scheme);

	}

	/**
	 * Run the void start(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testStart()
		throws Exception {
		String url = "http://test.com";

		AutoClientEnd.start(url);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientEnd
	}


	/**
	 * Run the void start(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testStart_2()
		throws Exception {
		String url = null;

		AutoClientEnd.start(url);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientEnd
	}

	/**
	 * Run the void stop() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testStop_1()
		throws Exception {

		AutoClientEnd.stop();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.onap.policy.std.AutoClientEnd
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
		StdPolicyEngine policyEngine = new StdPolicyEngine("Test/config_pass.properties", (String) null);

		NotificationHandler handler = policyEngine.getNotificationHandler();
		AutoClientEnd.setAuto(NotificationScheme.AUTO_ALL_NOTIFICATIONS, handler);
		AutoClientEnd.start("http://testurl.com");

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
		new org.junit.runner.JUnitCore().run(AutoClientEndTest.class);
	}
}
