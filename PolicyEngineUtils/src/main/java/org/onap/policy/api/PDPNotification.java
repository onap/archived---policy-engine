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

package org.onap.policy.api;

import java.util.Collection;

/**
 * Defines the <code>Notification</code> event sent from PDP to Client PEP.
 *
 * @version 0.2
 */
public interface PDPNotification {

    /**
     * Gets the <code>Collection</code> of {@link org.onap.policy.api.RemovedPolicy} objects received.
     *
     * @return the <code>Collection</code> which consists of <code>RemovedPolicy</code> objects.
     */
    Collection<RemovedPolicy> getRemovedPolicies();

    /**
     * Gets the <code>Collection</code> of {@link org.onap.policy.api.LoadedPolicy} objects receieved.
     *
     * @return the <code>Collection</code> which consists of <code>UpdatedPolicy</code> objects.
     */
    Collection<LoadedPolicy> getLoadedPolicies();

    /**
     * Gets the <code>NotificationType</code> of {@link org.onap.policy.api.NotificationType} received.
     *
     * @return <code>NotificationType</code> associated with this <code>PDPNotification</code>
     */
    NotificationType getNotificationType();
}
