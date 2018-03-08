/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PDPNotification;

public class ManualClientEndDMAAPTest {
	@Test
	public void testStaticMethods() {
		// Negative test result
		PDPNotification notification = ManualClientEndDMAAP.result(NotificationScheme.AUTO_ALL_NOTIFICATIONS);
		assertNull(notification);
		
		// Negative test create and start
		String topic = "testTopic";
		String uniqueID = "testID";
		List<String> dmaapList = new ArrayList<String>();
		String aafLogin = "testLogin";
		String aafPassword = "testPassword";
		try {
			ManualClientEndDMAAP.createTopic(topic, uniqueID, dmaapList, aafLogin, aafPassword);
			ManualClientEndDMAAP.start(dmaapList, topic, aafLogin, aafPassword, uniqueID);
		}
		catch (Exception ex) {
			fail("Not expecting any exception: " + ex);
		}
	}
}
