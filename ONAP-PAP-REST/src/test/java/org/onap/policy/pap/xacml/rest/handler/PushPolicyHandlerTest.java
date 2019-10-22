/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.mockrunner.mock.web.MockHttpServletResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

public class PushPolicyHandlerTest {
    @Test
    public void testGetsAndChecks() {
        CommonClassDao commonClassDao = Mockito.mock(CommonClassDao.class);
        // Test constructor
        PushPolicyHandler handler = new PushPolicyHandler(commonClassDao);
        assertNotNull(handler);

        // Test gets
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(MockHttpServletResponse.class);
        Mockito.when(request.getParameter("policyScope")).thenReturn("com");
        Mockito.when(request.getParameter("filePrefix")).thenReturn("Config_");
        Mockito.when(request.getParameter("policyName")).thenReturn("testPush");
        PolicyVersion version = new PolicyVersion();
        version.setActiveVersion(1);
        version.setCreatedBy("API");
        version.setHigherVersion(1);
        version.setId(1);
        version.setModifiedBy("API");
        version.setPolicyName("com" + File.separator + "Config_testPush");
        List<PolicyVersion> list = new ArrayList<>();
        list.add(version);

        doNothing().when(commonClassDao).save(any(PolicyVersion.class));
        doReturn(list).when(commonClassDao).getDataByQuery(any(String.class), any(SimpleBindings.class));

        handler.getActiveVersion(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(response).addHeader("version", "1");

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(MockHttpServletResponse.class);
        Mockito.when(request.getParameter("gitPath")).thenReturn("testPath");
        handler.getSelectedURI(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

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
