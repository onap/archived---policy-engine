/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

package org.onap.policy.conf;

import static org.junit.Assert.assertNull;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.controller.PolicyController;

public class HibernateSessionTest {

    /**
     * setup.
     */
    @Before
    public void setup() {
        PolicyController.setLogdbUrl("testURL");
        PolicyController.setLogdbUserName("testUser");
        PolicyController.setLogdbPassword("testPass");
        PolicyController.setLogdbDialect("testDialect");
        PolicyController.setLogdbDriver("testDriver");
    }

    @Test
    public void testSession() {
        SessionFactory factory = Mockito.mock(SessionFactory.class);
        HibernateSession.setSession(factory);
        assertNull(HibernateSession.getSession());
    }
}
