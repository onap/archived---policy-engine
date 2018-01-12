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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.*;
import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;

import static org.junit.Assert.*;

/**
 * The class <code>StdPDPNotificationTest</code> contains tests for the class <code>{@link StdPDPNotification}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class StdPDPNotificationTest {
	/**
	 * Run the StdPDPNotification() constructor test.
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testStdPDPNotification_1()
		throws Exception {
		StdPDPNotification result = new StdPDPNotification();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the Collection<LoadedPolicy> getLoadedPolicies() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetLoadedPolicies_1()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());

		Collection<LoadedPolicy> result = fixture.getLoadedPolicies();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Collection<LoadedPolicy> getLoadedPolicies() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetLoadedPolicies_2()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());

		Collection<LoadedPolicy> result = fixture.getLoadedPolicies();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Collection<LoadedPolicy> getLoadedPolicies() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetLoadedPolicies_3()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(null);

		Collection<LoadedPolicy> result = fixture.getLoadedPolicies();

		// add additional test code here
		assertEquals(Collections.EMPTY_LIST, result);
	}

	/**
	 * Run the NotificationType getNotificationType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetNotificationType_1()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());

		NotificationType result = fixture.getNotificationType();

		// add additional test code here
		assertNotNull(result);
		assertEquals("both", result.toString());
		assertEquals("BOTH", result.name());
		assertEquals(2, result.ordinal());
	}

	/**
	 * Run the Collection<RemovedPolicy> getRemovedPolicies() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetRemovedPolicies_1()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());

		Collection<RemovedPolicy> result = fixture.getRemovedPolicies();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Collection<RemovedPolicy> getRemovedPolicies() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetRemovedPolicies_2()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());

		Collection<RemovedPolicy> result = fixture.getRemovedPolicies();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Collection<RemovedPolicy> getRemovedPolicies() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetRemovedPolicies_3()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(null);
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());

		Collection<RemovedPolicy> result = fixture.getRemovedPolicies();

		// add additional test code here
		assertEquals(Collections.EMPTY_LIST, result);
	}

	/**
	 * Run the void setNotificationType(NotificationType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetNotificationType_1()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());
		NotificationType notificationType = NotificationType.BOTH;

		fixture.setNotificationType(notificationType);

		// add additional test code here
	}

	/**
	 * Run the void setRemovedPolicies(Collection<StdRemovedPolicy>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetRemovedPolicies_1()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());
		Collection<StdRemovedPolicy> removedPolicies = new LinkedList<StdRemovedPolicy>();

		fixture.setRemovedPolicies(removedPolicies);

		// add additional test code here
	}

	/**
	 * Run the void setUpdatedPolicies(Collection<StdLoadedPolicy>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetUpdatedPolicies_1()
		throws Exception {
		StdPDPNotification fixture = new StdPDPNotification();
		fixture.setRemovedPolicies(new LinkedList<StdRemovedPolicy>());
		fixture.setNotificationType(NotificationType.BOTH);
		fixture.setLoadedPolicies(new LinkedList<StdLoadedPolicy>());
		Collection<StdLoadedPolicy> updatedPolicies = new LinkedList<StdLoadedPolicy>();

		fixture.setLoadedPolicies(updatedPolicies);

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
		new org.junit.runner.JUnitCore().run(StdPDPNotificationTest.class);
	}
}
