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

package org.onap.policy.pdp.rest.notifications;

import java.util.Collection;

import org.onap.policy.api.NotificationType;

/**
 * Notification is the POJO which will be used to send the Notifications to the Server.
 * Notification must contain the Removal and Updated policies.
 *
 * @version 0.2
 *
 */
public class Notification {
	private Collection<Removed> removedPolicies = null;
	private Collection<Updated> loadedPolicies = null;
	private NotificationType notificationType= null;

	public Collection<Removed> getRemovedPolicies() {
		return removedPolicies;
	}

	public void setRemovedPolicies(Collection<Removed> removedPolicies) {
		this.removedPolicies = removedPolicies;
	}

	public Collection<Updated> getLoadedPolicies() {
		return loadedPolicies;
	}

	public void setLoadedPolicies(Collection<Updated> loadedPolicies) {
		this.loadedPolicies = loadedPolicies;
	}

	public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType){
        this.notificationType= notificationType;
    }
}
