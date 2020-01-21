/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019-2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.daoImp;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.conf.HibernateSession;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.jpa.SystemLogDb;

public class SystemLogDbDaoImplTest {
    private static Session mockSession = Mockito.mock(Session.class);
    private static SessionFactory sfMock = Mockito.mock(SessionFactory.class);
    private static Transaction mockTransaction = Mockito.mock(Transaction.class);
    private SystemLogDbDaoImpl sysLogImpl = new SystemLogDbDaoImpl();

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setup() {
        HibernateSession.setSession(sfMock);

        when(sfMock.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);

        doThrow(HibernateException.class).when(mockTransaction).commit();
        when(mockSession.close()).thenThrow(HibernateException.class);

        PolicyController.setjUnit(true);
    }

    @Test
    public void testGettingLoggingData() {
        List<SystemLogDb> loggingData = sysLogImpl.getLoggingData();
        assertNull(loggingData);
    }

    @Test
    public void testGetSystemAlertData() {
        List<SystemLogDb> systemAlertData = sysLogImpl.getSystemAlertData();
        assertNull(systemAlertData);
    }

    @AfterClass
    public static void resetjUnit() {
        PolicyController.setjUnit(false);
    }
}
