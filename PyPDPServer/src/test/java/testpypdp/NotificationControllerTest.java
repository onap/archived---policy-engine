/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package testpypdp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openecomp.policy.pypdp.notifications.NotificationController;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.RemovedPolicy;
import org.openecomp.policy.api.UpdateType;
import org.openecomp.policy.std.StdLoadedPolicy;
import org.openecomp.policy.std.StdPDPNotification;
import org.openecomp.policy.std.StdRemovedPolicy;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationControllerTest {
	private NotificationController notificationController = new NotificationController();
	private StdPDPNotification notification;

	@Before
	public void setUp() {
		notification = new StdPDPNotification();
		notification.setNotificationType(NotificationType.BOTH);
		Collection<StdRemovedPolicy> removedPolicies = new ArrayList<StdRemovedPolicy>();
		Collection<StdLoadedPolicy> loadedPolicies = new ArrayList<StdLoadedPolicy>();
		StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
		StdLoadedPolicy updatedPolicy = new StdLoadedPolicy();
		removedPolicy.setPolicyName("Test");
		removedPolicy.setVersionNo("1");
		removedPolicies.add(removedPolicy);
		updatedPolicy.setPolicyName("Testing");
		updatedPolicy.setVersionNo("1");
		updatedPolicy.setUpdateType(UpdateType.NEW);
		Map<String, String> matches = new HashMap<String, String>();
		matches.put("key", "value");
		updatedPolicy.setMatches(matches);
		loadedPolicies.add(updatedPolicy);
		notification.setRemovedPolicies(removedPolicies);
		notification.setLoadedPolicies(loadedPolicies);
		NotificationController.record(notification);
	}

	@Test
	public void notificationReceivedUpdateTest() throws Exception{
		StdPDPNotification notification = new StdPDPNotification();
		notification.setNotificationType(NotificationType.UPDATE);
		Collection<StdLoadedPolicy> loadedPolicies = new ArrayList<StdLoadedPolicy>();
		StdLoadedPolicy updatedPolicy = new StdLoadedPolicy();
		updatedPolicy.setPolicyName("Test");
		updatedPolicy.setVersionNo("1");
		updatedPolicy.setUpdateType(UpdateType.NEW);
		Map<String, String> matches = new HashMap<String, String>();
		matches.put("key", "value");
		updatedPolicy.setMatches(matches);
		loadedPolicies.add(updatedPolicy);
		notification.setLoadedPolicies(loadedPolicies);
		notificationController.notificationReceived(notification);
		Boolean result = false;
		PDPNotification newNotification= jsonStringToNotification(NotificationController.record(notification));
		for(LoadedPolicy loadedPolicy: newNotification.getLoadedPolicies()){
			if(loadedPolicy.getPolicyName().equals("Test") && loadedPolicy.getVersionNo().equals("1")){
				result = true;
			}
		}
		assertEquals(result,true);
		/*assertEquals(
				NotificationController.record(notification),
				"{\"removedPolicies\":[],\"updatedPolicies\":[{\"policyName\":\"Test\",\"versionNo\":\"1\",\"matches\":{\"key\":\"value\"},\"updateType\":\"NEW\"},{\"policyName\":\"Testing\",\"versionNo\":\"1\",\"matches\":{\"key\":\"value\"},\"updateType\":\"NEW\"}]}");*/
	}

	@Test
	public void notificationReceivedRemovedTest() throws Exception{
		StdPDPNotification notification = new StdPDPNotification();
		notification.setNotificationType(NotificationType.REMOVE);
		Collection<StdRemovedPolicy> removedPolicies = new ArrayList<StdRemovedPolicy>();
		StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
		removedPolicy.setPolicyName("Testing");
		removedPolicy.setVersionNo("1");
		removedPolicies.add(removedPolicy);
		notification.setRemovedPolicies(removedPolicies);
		notificationController.notificationReceived(notification);
		Boolean result = false;
		PDPNotification newNotification= jsonStringToNotification(NotificationController.record(notification));
		for(RemovedPolicy removed: newNotification.getRemovedPolicies()){
			if(removed.getPolicyName().equals("Testing") && removed.getVersionNo().equals("1")){
				result = true;
			}
		}
		assertEquals(result,true);
		/*assertEquals(
				NotificationController.record(notification),
				"{\"removedPolicies\":[{\"policyName\":\"Test\",\"versionNo\":\"1\"},{\"policyName\":\"Testing\",\"versionNo\":\"1\"}],\"updatedPolicies\":[]}");*/
	}
	
	public StdPDPNotification jsonStringToNotification(String json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return notification = mapper.readValue(json, StdPDPNotification.class);
	}

}
