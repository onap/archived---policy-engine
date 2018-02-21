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

package org.onap.policy.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.api.UpdateType;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationUnMarshal {

    private static final String EMPTY_STRING = "";

    private static final String REGEX = ".(\\d)*.xml";

    private NotificationUnMarshal() {
        // Empty constructor
    }

    public static StdPDPNotification notificationJSON(final String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final StdPDPNotification notification = mapper.readValue(json, StdPDPNotification.class);
        if (notification == null || notification.getLoadedPolicies() == null) {
            return notification;
        }
        final Collection<StdLoadedPolicy> stdLoadedPolicies = new ArrayList<>();
        for (final LoadedPolicy loadedPolicy : notification.getLoadedPolicies()) {
            final StdLoadedPolicy stdLoadedPolicy = (StdLoadedPolicy) loadedPolicy;
            if (notification.getRemovedPolicies() != null) {
                if (isUpdated(notification, stdLoadedPolicy)) {
                    stdLoadedPolicy.setUpdateType(UpdateType.UPDATE);
                } else {
                    stdLoadedPolicy.setUpdateType(UpdateType.NEW);
                }
            } else {
                stdLoadedPolicy.setUpdateType(UpdateType.NEW);
            }
            stdLoadedPolicies.add(stdLoadedPolicy);
        }
        notification.setLoadedPolicies(stdLoadedPolicies);
        return notification;
    }

    private static Boolean isUpdated(final StdPDPNotification notification, final StdLoadedPolicy stdLoadedPolicy) {
        for (final RemovedPolicy removedPolicy : notification.getRemovedPolicies()) {
            final String removedPolicyName = removedPolicy.getPolicyName();
            final String loadedPolicyName = stdLoadedPolicy.getPolicyName();
            if (removedPolicyName.replaceAll(REGEX, EMPTY_STRING)
                    .equals(loadedPolicyName.replaceAll(REGEX, EMPTY_STRING))) {
                return true;
            }
        }
        return false;
    }
}
