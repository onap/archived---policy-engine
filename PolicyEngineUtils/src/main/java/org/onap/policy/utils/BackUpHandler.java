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

package org.onap.policy.utils;

import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.PDPNotification;

public interface BackUpHandler extends NotificationHandler{

	/**
	 * <code>notificationReceived</code> method will be triggered automatically whenever a Notification is received by the PEP.
	 *
	 * @param notification <code>PDPNotification</code> of {@link org.onap.policy.api.PDPNotification} is the object that has information of the notification.
	 */
	@Override
	public void notificationReceived(PDPNotification notification);

	/**
	 * <code>runOnNotification</code> method will be triggered automatically whenever a Notification is received by the PEP This needs to be the main implementation.
	 *
	 * @param notification <code>PDPNotification</code> of {@link org.onap.policy.api.PDPNotification} is the object that has information of the notification.
	 */
	public void runOnNotification(PDPNotification notification);
}
