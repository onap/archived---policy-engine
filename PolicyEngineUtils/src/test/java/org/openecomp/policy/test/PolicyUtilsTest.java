/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.openecomp.policy.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.UpdateType;
import org.openecomp.policy.std.StdLoadedPolicy;
import org.openecomp.policy.std.StdPDPNotification;
import org.openecomp.policy.std.StdRemovedPolicy;
import org.openecomp.policy.utils.PolicyUtils;

public class PolicyUtilsTest {
	
	@Test
	public void testJsonConversions() throws Exception{
		@SuppressWarnings("unused")
		PolicyUtils policyUtils = new PolicyUtils();
		StdPDPNotification notification = new StdPDPNotification();
		notification.setNotificationType(NotificationType.BOTH);
		Collection<StdRemovedPolicy> removedPolicies = new ArrayList<>();
		Collection<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
		StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
		StdLoadedPolicy updatedPolicy = new StdLoadedPolicy();
		removedPolicy.setPolicyName("Test");
		removedPolicy.setVersionNo("1");
		removedPolicies.add(removedPolicy);
		updatedPolicy.setPolicyName("Testing");
		updatedPolicy.setVersionNo("1");
		updatedPolicy.setUpdateType(UpdateType.NEW);
		Map<String, String> matches = new HashMap<>();
		matches.put("key", "value");
		updatedPolicy.setMatches(matches);
		loadedPolicies.add(updatedPolicy);
		notification.setRemovedPolicies(removedPolicies);
		notification.setLoadedPolicies(loadedPolicies);
		
		String json = PolicyUtils.objectToJsonString(notification);
		PDPNotification getBackObject = PolicyUtils.jsonStringToObject(json, StdPDPNotification.class);
		assertEquals(0,getBackObject.getNotificationType().compareTo(notification.getNotificationType()));
		
	}
}
