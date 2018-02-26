/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.notifications.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.onap.policy.api.NotificationType;
import org.onap.policy.pdp.rest.notifications.Notification;
import org.onap.policy.pdp.rest.notifications.Removed;
import org.onap.policy.pdp.rest.notifications.Updated;

public class NotificationTest {
	

	@Test
	public void test() {
		ArrayList<Removed> removedPolicies = new ArrayList<Removed>();
		ArrayList<Updated> loadedPolicies = new ArrayList<Updated>();
		
		Updated u1 = new Updated();
		Removed r1 = new Removed();
		Notification n = new Notification();

		u1.setPolicyName("update");
		u1.setVersionNo("1");
		u1.setMatches(null);
		u1.setUpdateType(null);
		
		assertEquals("update", u1.getPolicyName());
		assertEquals("1", u1.getVersionNo());
		assertEquals(null, u1.getMatches());
		assertEquals(null, u1.getUpdateType());
		
		loadedPolicies.add(u1);
		
		r1.setPolicyName("removed");
		r1.setVersionNo("1");
		assertEquals(r1.getPolicyName(),"removed");
		assertEquals(r1.getVersionNo(),"1");
		
		removedPolicies.add(r1);
		
		n.setLoadedPolicies(loadedPolicies);
		n.setRemovedPolicies(removedPolicies);
		n.setNotificationType(NotificationType.BOTH);
		
		assertEquals(removedPolicies, n.getRemovedPolicies());
		assertEquals(loadedPolicies, n.getLoadedPolicies());
		assertEquals(NotificationType.BOTH, n.getNotificationType());
		
		
		
	} 
}
