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

package org.onap.policy.api;

/**
 * Enumeration of <code>NotificationScheme</code> describes the Notification Scheme that will be used by the PolicyEngine.
 *
 * @version 0.1
 */
public enum NotificationScheme {
	/**
	 * Notifications for policyUpdates on policy Configs already retrieved
	 */
	AUTO_NOTIFICATIONS("auto_notifications"),
	/**
	 * Subscribing to all notifications from the PDP
	 */
	AUTO_ALL_NOTIFICATIONS("auto_all_notifications"),
	/**
	 * Client can poll for updates that receive policyUpdates on policy Configs that have already been retrieved
	 */
	MANUAL_NOTIFICATIONS("manual_notifications"),
	/**
	 * Client can poll for updates that receive all notifications from the PDP
	 */
	MANUAL_ALL_NOTIFICATIONS("manual_all_notifications")
	;

	private String name;
	private NotificationScheme(String name){
		this.name = name;
	}

	/**
	 * Returns the <code>String</code> name for this <code>NotificationScheme</code>
	 *
	 * @return the <code>String</code> name for this <code>NotificationScheme</code>
	 */
	@Override
	public String toString(){
		return this.name;
	}
}
