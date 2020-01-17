/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018, 2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.brms.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.api.NotificationType;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.utils.BackUpMonitor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest({Persistence.class, BackUpMonitor.class})
public class BrmsHandlerTest {
    @Test
    public void testNegativeTestNotifications() throws Exception {
        // Mock emf, persistence, and query
        final EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        final EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(emf.createEntityManager()).thenReturn(em);
        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.when(Persistence.createEntityManagerFactory(any(), any())).thenReturn(emf);
        final EntityTransaction et = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(et);
        final TypedQuery<?> query = Mockito.mock(TypedQuery.class);
        Mockito.when(em.createQuery(Mockito.anyString(), Mockito.any())).thenReturn((TypedQuery<Object>) query);

        // Mock backup monitor
        PowerMockito.mockStatic(BackUpMonitor.class);
        final BackUpMonitor monitor = Mockito.mock(BackUpMonitor.class);
        PowerMockito.when(BackUpMonitor.getInstance(any(), any(), any(), any())).thenReturn(monitor);

        // Test constructor
        final StdPDPNotification notification = new StdPDPNotification();
        final String propFile = "config.properties";
        final BrmsHandler handler = new BrmsHandler(propFile);
        final BrmsPush brmsPush = new BrmsPush(propFile, handler);
        handler.setBrmsPush(brmsPush);
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
        } catch (final Exception ex) {
            fail("Not expecting an exception: " + ex);
        }
    }
}
