/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2020 AT&T Intellectual Property. All rights reserved.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(DeleteHandler.class)
public class DeleteHandlerTest {
    @Before
    public void setUp() {
        SessionFactory mockedSessionFactory = Mockito.mock(SessionFactory.class);
        Session mockedSession = Mockito.mock(Session.class);
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Mockito.when(mockedSessionFactory.openSession()).thenReturn(mockedSession);
        Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
        CommonClassDaoImpl.setSessionfactory(mockedSessionFactory);
        new DeleteHandler(new CommonClassDaoImpl());
    }

    @Test
    public void testGets() {
        DeleteHandler handler = new DeleteHandler();
        assertNotNull(handler);
        assertEquals(handler.preSafetyCheck(null), true);
        assertNull(handler.getDeletedGroup());
    }

    @Test
    public void testGetInstance() {
        DeleteHandler handler = DeleteHandler.getInstance();
        assertNotNull(handler);
    }

    @PrepareForTest({DeleteHandler.class, XACMLPapServlet.class})
    @Test
    public void testDeletes() throws Exception {
        // Mock request
        DeleteHandler handler = new DeleteHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setBodyContent("{\n\"PAPPolicyType\": \"StdPAPPolicy\"\n}\n");

        // Mock servlet
        PAPPolicyEngine engine = Mockito.mock(StdEngine.class);
        PowerMockito.mockStatic(XACMLPapServlet.class);
        when(XACMLPapServlet.getPAPEngine()).thenReturn(engine);
        when(engine.getGroup(any())).thenReturn(null);

        // Mock elastic search
        PolicyElasticSearchController controller = Mockito.mock(PolicyElasticSearchController.class);
        PowerMockito.whenNew(PolicyElasticSearchController.class).withNoArguments().thenReturn(controller);

        // Test deletion from PAP
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.doApiDeleteFromPap(request, response);

        // Test deletion from PDP
        OnapLoggingContext loggingContext = Mockito.mock(OnapLoggingContext.class);
        handler.doApiDeleteFromPdp(request, response, loggingContext);

        // Test delete entity
        PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setPolicyName("testVal");
        String result = DeleteHandler.deletePolicyEntityData(policyEntity);
        assertEquals(result, "success");

        // Test check entity
        List<?> peResult = Collections.emptyList();
        assertEquals(DeleteHandler.checkPolicyGroupEntity(peResult), false);
    }

    @Test
    public void testDoDeletePap() throws IOException {
        CommonClassDao dao = Mockito.mock(CommonClassDao.class);
        DeleteHandler handler = new DeleteHandler(dao);

        // Request #1
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Config_name.1.xml\", \"deleteCondition\": \"All Versions\"\n}\n");
        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #2
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Action_name.1.xml\", \"deleteCondition\": \"All Versions\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #3
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Decision_name.1.xml\", \"deleteCondition\": \"All Versions\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #4
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Bar_name.1.xml\", \"deleteCondition\": \"All Versions\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #5
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Config_name.1.xml\", \"deleteCondition\": \"Current Version\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #6
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Action_name.1.xml\", \"deleteCondition\": \"Current Version\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #7
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Decision_name.1.xml\", \"deleteCondition\": \"Current Version\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Mock dao
        List<PolicyVersion> pePVs = new ArrayList<PolicyVersion>();
        PolicyVersion pv = new PolicyVersion();
        pePVs.add(pv);
        List<Object> peObjs = new ArrayList<Object>(pePVs);
        List<PolicyEntity> peEnts = new ArrayList<PolicyEntity>();
        PolicyEntity peEnt = new PolicyEntity();
        peEnts.add(peEnt);
        List<Object> peEntObjs = new ArrayList<Object>(peEnts);
        Mockito.when(dao.getDataByQuery(eq("Select p from PolicyVersion p where p.policyName=:pname"), any()))
            .thenReturn(peObjs);
        Mockito.when(
            dao.getDataByQuery(eq("SELECT p FROM PolicyEntity p WHERE p.policyName=:pName and p.scope=:pScope"), any()))
            .thenReturn(peEntObjs);

        // Request #8
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Decision_name.1.xml\", \"deleteCondition\": \"Current Version\"\n}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));

        // Request #9
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Decision_name.1.xml\", \"deleteCondition\": \"Current Version\"\n, \"deleteCondition\": \"All Versions\"}\n");
        handler.doApiDeleteFromPap(request, response);
        assertTrue(response.containsHeader("error"));
    }

    @Test(expected = NullPointerException.class)
    public void testDoDeletePdp() throws IOException {
        CommonClassDao dao = Mockito.mock(CommonClassDao.class);
        DeleteHandler handler = new DeleteHandler(dao);
        OnapLoggingContext loggingContext = new OnapLoggingContext();

        // Mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setBodyContent(
            "{\n\"PAPPolicyType\": \"StdPAPPolicy\", \"policyName\": \"foo.Config_name.1.xml\", \"deleteCondition\": \"All Versions\"\n}\n");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.doApiDeleteFromPdp(request, response, loggingContext);
    }
}
