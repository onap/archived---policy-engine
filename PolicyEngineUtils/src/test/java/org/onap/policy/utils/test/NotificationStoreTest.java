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

import java.io.IOException;
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
import org.onap.policy.utils.PolicyUtils;

public class NotificationStoreTest {

    @Test
    public void notificationTest() throws IOException{
        // Notification Delta test first.
        NotificationStore.recordNotification(new StdPDPNotification());
        assertEquals("{\"removedPolicies\":[],\"loadedPolicies\":[],\"notificationType\":null}", PolicyUtils.objectToJsonString(NotificationStore.getDeltaNotification(new StdPDPNotification())));
        // Initialize test
        StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.UPDATE);
        loadedPolicy.setVersionNo("2");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        List<StdRemovedPolicy> removedPolicies = new ArrayList<>();
        StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.testing");
        removedPolicy.setVersionNo("1");
        removedPolicies.add(removedPolicy);
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
        assertEquals("{\"removedPolicies\":[{\"policyName\":\"com.testing\",\"versionNo\":\"1\"}],\"loadedPolicies\":[{\"policyName\":\"com.testing\",\"versionNo\":\"2\",\"matches\":{\"test\":\"test\"},\"updateType\":\"UPDATE\"}],\"notificationType\":\"BOTH\"}", PolicyUtils.objectToJsonString(NotificationStore.getNotificationRecord()));
        // Add new Notifications.
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        loadedPolicies = new ArrayList<>();
        loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.test.3.xml");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("3");
        matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        removedPolicies = new ArrayList<>();
        removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.testing.2.xml");
        removedPolicy.setVersionNo("2");
        removedPolicies.add(removedPolicy);
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
        StdPDPNotification check = NotificationStore.getDeltaNotification(PolicyUtils.jsonStringToObject("{\"removedPolicies\":[{\"policyName\":\"com.testing\",\"versionNo\":\"1\"},{\"policyName\":\"com.testing\",\"versionNo\":\"2\"}],\"loadedPolicies\":[{\"policyName\":\"com.test\",\"versionNo\":\"3\",\"matches\":{\"test\":\"test\"},\"updateType\":\"NEW\"}],\"notificationType\":\"BOTH\"}", StdPDPNotification.class));
        assertEquals("{\"removedPolicies\":[],\"loadedPolicies\":[],\"notificationType\":null}", PolicyUtils.objectToJsonString(check));
        // Remove Notifications.
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.REMOVE);
        removedPolicies = new ArrayList<>();
        removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.test.3.xml");
        removedPolicy.setVersionNo("3");
        removedPolicies.add(removedPolicy);
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
        check = NotificationStore.getDeltaNotification(PolicyUtils.jsonStringToObject("{\"removedPolicies\":[{\"policyName\":\"com.test\",\"versionNo\":\"3\"},{\"policyName\":\"com.testing\",\"versionNo\":\"1\"},{\"policyName\":\"com.testing\",\"versionNo\":\"2\"}],\"loadedPolicies\":[],\"notificationType\":\"REMOVE\"}", StdPDPNotification.class));
        assertEquals("{\"removedPolicies\":[],\"loadedPolicies\":[],\"notificationType\":null}", PolicyUtils.objectToJsonString(check));
        // Remove on remove duplicate  Notifications.
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.REMOVE);
        removedPolicies = new ArrayList<>();
        removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.test.3.xml");
        removedPolicy.setVersionNo("3");
        removedPolicies.add(removedPolicy);
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
        check = NotificationStore.getDeltaNotification(PolicyUtils.jsonStringToObject("{\"removedPolicies\":[{\"policyName\":\"com.test\",\"versionNo\":\"3\"},{\"policyName\":\"com.testing\",\"versionNo\":\"1\"},{\"policyName\":\"com.testing\",\"versionNo\":\"2\"}],\"loadedPolicies\":[],\"notificationType\":\"REMOVE\"}", StdPDPNotification.class));
        assertEquals("{\"removedPolicies\":[],\"loadedPolicies\":[],\"notificationType\":null}", PolicyUtils.objectToJsonString(check));
        // Update  Notification
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.UPDATE);
        loadedPolicies = new ArrayList<>();
        loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.test.3.xml");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("3");
        matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        NotificationStore.recordNotification(notification);
        check = NotificationStore.getDeltaNotification(PolicyUtils.jsonStringToObject("{\"removedPolicies\":[{\"policyName\":\"com.testing\",\"versionNo\":\"1\"},{\"policyName\":\"com.testing\",\"versionNo\":\"2\"}],\"loadedPolicies\":[{\"policyName\":\"com.test\",\"versionNo\":\"3\",\"matches\":{\"test\":\"test\"},\"updateType\":\"NEW\"}],\"notificationType\":\"BOTH\"}", StdPDPNotification.class));
        assertEquals("{\"removedPolicies\":[],\"loadedPolicies\":[],\"notificationType\":null}", PolicyUtils.objectToJsonString(check));
        // Update  on update duplicate Notification
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.UPDATE);
        loadedPolicies = new ArrayList<>();
        loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.test.3.xml");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("3");
        matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        NotificationStore.recordNotification(notification);
        check = NotificationStore.getDeltaNotification(PolicyUtils.jsonStringToObject("{\"removedPolicies\":[{\"policyName\":\"com.testing\",\"versionNo\":\"1\"},{\"policyName\":\"com.testing\",\"versionNo\":\"2\"}],\"loadedPolicies\":[{\"policyName\":\"com.test\",\"versionNo\":\"3\",\"matches\":{\"test\":\"test\"},\"updateType\":\"NEW\"}],\"notificationType\":\"BOTH\"}", StdPDPNotification.class));
        assertEquals("{\"removedPolicies\":[],\"loadedPolicies\":[],\"notificationType\":null}", PolicyUtils.objectToJsonString(check));
        //
        // Notification Delta Tests
        //
        notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        loadedPolicies = new ArrayList<>();
        loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("3");
        matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        removedPolicies = new ArrayList<>();
        removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.test.3.xml");
        removedPolicy.setVersionNo("3");
        removedPolicies.add(removedPolicy);
        notification.setRemovedPolicies(removedPolicies);
        check = NotificationStore.getDeltaNotification(notification);
        assertEquals("{\"removedPolicies\":[{\"policyName\":\"com.test\",\"versionNo\":\"3\"}],\"loadedPolicies\":[{\"policyName\":\"com.testing\",\"versionNo\":\"3\",\"matches\":{\"test\":\"test\"},\"updateType\":\"NEW\"}],\"notificationType\":\"BOTH\"}", PolicyUtils.objectToJsonString(check));
    }

}
