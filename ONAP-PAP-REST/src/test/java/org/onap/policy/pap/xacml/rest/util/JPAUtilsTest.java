/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.junit.Test;
import org.mockito.Mockito;

public class JPAUtilsTest {
    @Test(expected = IllegalAccessException.class)
    public void testJPAUtils() throws IllegalAccessException {
        // Setup test data
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        EntityManager em = Mockito.mock(EntityManager.class);
        Query query = Mockito.mock(Query.class);
        Mockito.when(emf.createEntityManager()).thenReturn(em);
        Mockito.when(em.createNamedQuery(Mockito.any())).thenReturn(query);

        // Test lockdown
        JPAUtils utils = JPAUtils.getJPAUtilsInstance(emf);
        assertEquals(utils.dbLockdownIgnoreErrors(), false);
        utils.dbLockdown();
        fail("Expecting an exception");
    }
}
