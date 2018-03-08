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

package org.onap.policy.brmsInterface;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.api.NotificationType;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.utils.BackUpMonitor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class BRMSHandlerTest {
	@PrepareForTest({Persistence.class, BackUpMonitor.class})
	@Test
	public void negativeTestNotifications() throws Exception {
		// Mock emf, persistence, and query
		EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
		EntityManager em = Mockito.mock(EntityManager.class);
		Mockito.when(emf.createEntityManager()).thenReturn(em);
		PowerMockito.mockStatic(Persistence.class);
		PowerMockito.when(Persistence.createEntityManagerFactory(any(), any())).thenReturn(emf);
		EntityTransaction et = Mockito.mock(EntityTransaction.class);
		Mockito.when(em.getTransaction()).thenReturn(et);		
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(query);		
		
		// Mock backup monitor
		PowerMockito.mockStatic(BackUpMonitor.class);
		BackUpMonitor monitor = Mockito.mock(BackUpMonitor.class);
		PowerMockito.when(BackUpMonitor.getInstance(any(), any(), any(), any())).thenReturn(monitor);

		// Test constructor
		StdPDPNotification notification = new StdPDPNotification();
		String propFile = "config.properties";
		BRMSHandler handler = new BRMSHandler(propFile);
		BRMSPush brmsPush = new BRMSPush(propFile, handler);
		handler.setBRMSPush(brmsPush);
		assertNotNull(handler);
		assertNotNull(brmsPush);
		assertNull(handler.getPolicyEngine());

		try {
			// Test update
			notification.setNotificationType(NotificationType.UPDATE);
			handler.runOnNotification(notification);
			handler.notificationReceived(notification);
			
			// Test remove
			notification.setNotificationType(NotificationType.REMOVE);
			handler.runOnNotification(notification);
			handler.notificationReceived(notification);
			
			// Test both
			notification.setNotificationType(NotificationType.BOTH);
			handler.runOnNotification(notification);
			handler.notificationReceived(notification);		
		}
		catch (Exception ex) {
			fail("Not expecting an exception: " + ex);
		}
	}
}
