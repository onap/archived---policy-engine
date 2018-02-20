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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.UpdateType;
import org.onap.policy.std.NotificationUnMarshal;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The class <code>NotificationUnMarshalTest</code> contains tests for the class
 * <code>{@link NotificationUnMarshal}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class NotificationUnMarshalTest {
	private static final String EMPTY_STRING = "";
	private static final String POLICY_NAME = "ONAP";
	private static final String POLICY_VERSION = "1.0.0";

	/**
	 * Run the StdPDPNotification notificationJSON(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test(expected = JsonMappingException.class)
	public void testNotificationJSON_EmptyString_ShouldThrowException() throws Exception {
		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(EMPTY_STRING);

		assertNotNull(result);
	}

	@Test
	public void testNotificationJSON_StdPDPNotificationJsonStringWithEmptyLoadedAndRemovedPolicies_emptyLoadedPolices()
			throws Exception {
		final String json = getPDPNotificationAsJsonString(new StdPDPNotification());

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		assertNotNull(result);
		assertTrue(result.getLoadedPolicies().isEmpty());
	}

	@Test
	public void testNotificationJSON_StdPDPNotificationJsonStringWithEmptyLoadedpolicy_emptyLoadedPolices()
			throws Exception {
		final StdPDPNotification notification = getPDPNotification(
				Arrays.asList(getRemovedPolicy(POLICY_VERSION, POLICY_NAME)), Collections.emptyList());
		final String json = getPDPNotificationAsJsonString(notification);

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		assertNotNull(result);
		assertTrue(result.getLoadedPolicies().isEmpty());
	}

	@Test
	public void testNotificationJSON_validPDPNotificationJsonStringWithRemovedAndLoadedPolicies_UpdateTypeUpdateAndLoadedPolicyAdded()
			throws Exception {
		final List<StdRemovedPolicy> removedPolicies = Arrays.asList(getRemovedPolicy(POLICY_VERSION, POLICY_NAME));
		final List<StdLoadedPolicy> loadedPolicies = Arrays.asList(getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME));
		final StdPDPNotification notification = getPDPNotification(removedPolicies, loadedPolicies);

		final String json = getPDPNotificationAsJsonString(notification);

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		assertNotNull(result);
		assertFalse(result.getLoadedPolicies().isEmpty());

		final StdLoadedPolicy actualPolicy = (StdLoadedPolicy) result.getLoadedPolicies().iterator().next();
		assertEquals(POLICY_VERSION, actualPolicy.getVersionNo());
		assertEquals(POLICY_NAME, actualPolicy.getPolicyName());
		assertEquals(UpdateType.UPDATE, actualPolicy.getUpdateType());
	}

	/**
	 * Run the StdPDPNotification notificationJSON(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testNotificationJSON_validPDPNotificationJsonStringWithRemovedAndLoadedPolicies_UpdateTypeNewAndLoadedPolicyAdded()
			throws Exception {
		final List<StdRemovedPolicy> removedPolicies = Arrays.asList(getRemovedPolicy(POLICY_VERSION, "Something"));
		final List<StdLoadedPolicy> loadedPolices = Arrays.asList(getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME));
		final StdPDPNotification notification = getPDPNotification(removedPolicies, loadedPolices);

		final String json = getPDPNotificationAsJsonString(notification);

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		assertNotNull(result);
		assertFalse(result.getLoadedPolicies().isEmpty());

		final StdLoadedPolicy actualPolicy = (StdLoadedPolicy) result.getLoadedPolicies().iterator().next();
		assertEquals(POLICY_VERSION, actualPolicy.getVersionNo());
		assertEquals(POLICY_NAME, actualPolicy.getPolicyName());
		assertEquals(UpdateType.NEW, actualPolicy.getUpdateType());
	}

	@Test
	public void testNotificationJSON_validPDPNotificationJsonStringLoadedPoliciesAndNullRemovedPolicies_UpdateTypeNewAndLoadedPolicyAdded()
			throws Exception {
		final List<StdLoadedPolicy> loadedPolices = Arrays.asList(getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME));
		final StdPDPNotification notification = getPDPNotification(null, loadedPolices);

		final String json = getPDPNotificationAsJsonString(notification);

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		assertNotNull(result);
		assertFalse(result.getLoadedPolicies().isEmpty());

		final StdLoadedPolicy actualPolicy = (StdLoadedPolicy) result.getLoadedPolicies().iterator().next();
		assertEquals(POLICY_VERSION, actualPolicy.getVersionNo());
		assertEquals(POLICY_NAME, actualPolicy.getPolicyName());
		assertEquals(UpdateType.NEW, actualPolicy.getUpdateType());
	}

	/**
	 * Run the StdPDPNotification notificationJSON(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
	public void testNotificationJSON_7() throws Exception {
		final String json = EMPTY_STRING;

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the StdPDPNotification notificationJSON(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
	public void testNotificationJSON_8() throws Exception {
		final String json = EMPTY_STRING;

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the StdPDPNotification notificationJSON(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
	public void testNotificationJSON_9() throws Exception {
		final String json = EMPTY_STRING;

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the StdPDPNotification notificationJSON(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
	public void testNotificationJSON_10() throws Exception {
		final String json = EMPTY_STRING;

		final StdPDPNotification result = NotificationUnMarshal.notificationJSON(json);

		// add additional test code here
		assertNotNull(result);
	}

	private String getPDPNotificationAsJsonString(final StdPDPNotification notification) {
		final ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.writeValueAsString(notification);
		} catch (final Exception expection) {
			throw new RuntimeException(expection);
		}

	}

	private StdPDPNotification getPDPNotification(final Collection<StdRemovedPolicy> removedPolicies,
			final Collection<StdLoadedPolicy> loadedPolicies) {
		final StdPDPNotification oldNotification = new StdPDPNotification();
		oldNotification.setLoadedPolicies(loadedPolicies);
		oldNotification.setRemovedPolicies(removedPolicies);
		return oldNotification;
	}

	private StdRemovedPolicy getRemovedPolicy(final String version, final String policyName) {
		return new StdRemovedPolicy() {

			@Override
			public String getVersionNo() {
				return version;
			}

			@Override
			public String getPolicyName() {
				return policyName;
			}
		};
	}

	private StdLoadedPolicy getStdLoadedPolicy(final String version, final String policyName) {
		return new StdLoadedPolicy() {

			@Override
			public String getVersionNo() {
				return version;
			}

			@Override
			public String getPolicyName() {
				return policyName;
			}

			@Override
			public Map<String, String> getMatches() {
				return Collections.emptyMap();
			}
		};
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
		new org.junit.runner.JUnitCore().run(NotificationUnMarshalTest.class);
	}
}
