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

package org.onap.policy.std;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.RemovedPolicy;

public class StdPDPNotification implements PDPNotification {

    private Collection<StdRemovedPolicy> removedPolicies = null;
    private Collection<StdLoadedPolicy> loadedPolicies = null;
    private Collection<RemovedPolicy> removed = null;
    private Collection<LoadedPolicy> updated = null;
    private NotificationType notificationType = null;

    @Override
    public Collection<RemovedPolicy> getRemovedPolicies() {
        removed = new HashSet<>();
        if (removedPolicies != null) {
            removed.addAll(removedPolicies);
            return this.removed;
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<LoadedPolicy> getLoadedPolicies() {
        updated = new HashSet<>();
        if (loadedPolicies != null) {
            updated.addAll(loadedPolicies);
            return updated;
        }
        return Collections.emptyList();
    }

    @Override
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void setLoadedPolicies(Collection<StdLoadedPolicy> loadedPolicies) {
        this.loadedPolicies = loadedPolicies;
    }

    public void setRemovedPolicies(Collection<StdRemovedPolicy> removedPolicies) {
        this.removedPolicies = removedPolicies;
    }
}
