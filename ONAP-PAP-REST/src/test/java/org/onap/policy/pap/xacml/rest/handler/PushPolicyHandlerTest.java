/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.Query;

@RunWith(PowerMockRunner.class)
public class PushPolicyHandlerTest {
    @PrepareForTest({XACMLPapServlet.class})
    @Test
    public void testGetsAndChecks() {
        // Mock servlet, entity mgr, transaction, and query
        EntityManager em = Mockito.mock(EntityManager.class);
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        PowerMockito.mockStatic(XACMLPapServlet.class);
        PowerMockito.when(XACMLPapServlet.getEmf()).thenReturn(emf);
        Mockito.when(emf.createEntityManager()).thenReturn(em);
        EntityTransaction transaction = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(transaction);
        Query query = Mockito.mock(Query.class);
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(query);

        // Test constructor
        PushPolicyHandler handler = new PushPolicyHandler();
        assertNotNull(handler);

        // Test gets
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.getActiveVersion(request, response);
        assertEquals(response.getStatusCode(), HttpServletResponse.SC_OK);
        request.setupAddParameter("gitPath", "testPath");
        handler.getSelectedURI(request, response);
        assertEquals(response.getStatusCode(), HttpServletResponse.SC_OK);

        // Test check
        StdPDPPolicy policy = new StdPDPPolicy();
        OnapPDPGroup onapPolicy = new StdPDPGroup();
        String configHome = "testVal";
        assertEquals(handler.preSafetyCheck(policy, configHome), true);
        assertEquals(handler.preSafetyCheck(onapPolicy, configHome), true);
    }

    @Test
    public void testGetInstance() {
        PushPolicyHandler handler = PushPolicyHandler.getInstance();
        assertNotNull(handler);
    }
}
