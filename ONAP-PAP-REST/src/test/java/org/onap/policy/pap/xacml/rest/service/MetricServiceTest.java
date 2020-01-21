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

package org.onap.policy.pap.xacml.rest.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.mockrunner.mock.web.MockHttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(CommonClassDao.class)
public class MetricServiceTest {
    @PrepareForTest({XACMLPapServlet.class})
    @Test
    public void testNegativeGet() {
        // Mock pap servlet
        PowerMockito.mockStatic(XACMLPapServlet.class);
        when(XACMLPapServlet.getPAPEngine()).thenReturn(null);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MetricService.doGetPolicyMetrics(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testService() {
        CommonClassDao dao = Mockito.mock(CommonClassDao.class);
        List<Object> value = new ArrayList<Object>();
        when(dao.getData(PolicyVersion.class)).thenReturn(value);
        MetricService service = new MetricService(dao);
        assertNotNull(service);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MetricService.doGetPolicyMetrics(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatusCode());
    }
}
