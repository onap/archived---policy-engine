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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.UpdateType;
import org.onap.policy.std.NotificationStore;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;

public class NotificationStoreTest {
    
    @Test
    public void notificationTest(){
        StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.UPDATE);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("1");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        NotificationStore.recordNotification(notification);
        assertEquals(notification, NotificationStore.getNotificationRecord());
        // Add new Notifications. 
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        loadedPolicies = new ArrayList<>();
        loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.UPDATE);
        loadedPolicy.setVersionNo("2");
        matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        List<StdRemovedPolicy> removedPolicies = new ArrayList<>();
        StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.testing");
        removedPolicy.setVersionNo("1");
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
        assertNotNull(NotificationStore.getNotificationRecord());
        // Add new Notifications. 
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        loadedPolicies = new ArrayList<>();
        loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.test.xml");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("2");
        matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        removedPolicies = new ArrayList<>();
        removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.testing.xml");
        removedPolicy.setVersionNo("2");
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
        assertNotNull(NotificationStore.getNotificationRecord());
    }

}
