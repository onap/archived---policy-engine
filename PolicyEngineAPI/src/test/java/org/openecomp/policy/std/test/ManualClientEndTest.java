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

package org.openecomp.policy.std.test;

import javax.websocket.Session;

import org.junit.*;
import org.mockito.Mockito;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.std.ManualClientEnd;

import static org.junit.Assert.*;

/**
 * The class <code>ManualClientEndTest</code> contains tests for the class <code>{@link ManualClientEnd}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class ManualClientEndTest {
	/**
	 * Run the ManualClientEnd() constructor test.
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testManualClientEnd_1()
		throws Exception {
		ManualClientEnd result = new ManualClientEnd();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the void onClose(Session) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testOnClose()
		throws Exception {
		ManualClientEnd fixture = Mockito.mock(ManualClientEnd.class);
		Session mockSession = Mockito.mock(Session.class);

		fixture.onClose(mockSession);

	}

	/**
	 * Run the void onError(Session,Throwable) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testOnError()
		throws Exception {
		ManualClientEnd fixture = Mockito.mock(ManualClientEnd.class);
		Session mockSession = Mockito.mock(Session.class);
		Throwable e = new Throwable();

		fixture.onError(mockSession, e);
	}

	/**
	 * Run the void onMessage(String,Session) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testOnMessage()
		throws Exception {
		ManualClientEnd mockclient = Mockito.mock(ManualClientEnd.class);
		String message = "";
		Session mockSession = Mockito.mock(Session.class);
		
		Mockito.doNothing().when(mockclient).onMessage(message,mockSession);
		mockclient.onMessage(message,mockSession);
	}

	/**
	 * Run the void onOpen(Session) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testOnOpen()
		throws Exception {
		ManualClientEnd fixture = Mockito.mock(ManualClientEnd.class);
		Session mockSession = Mockito.mock(Session.class);

		fixture.onOpen(mockSession);

	}

	/**
	 * Run the PDPNotification result(NotificationScheme) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testResult_1()
		throws Exception {
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;

		PDPNotification result = ManualClientEnd.result(scheme);

		assertNull(result);
	}


	/**
	 * Run the void start(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testStart_1()
		throws Exception {
		String url = "";

		ManualClientEnd.start(url);

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
		new org.junit.runner.JUnitCore().run(ManualClientEndTest.class);
	}
}
