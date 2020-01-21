/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018-2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest({UserUtils.class})
public class PolicyNotificationControllerTest {
    @Test
    public void testWatch() throws IOException {
        // Mock user utilities
        PowerMockito.mockStatic(UserUtils.class);
        User user = new User();
        user.setOrgUserId("testID");
        when(UserUtils.getUserSession(any())).thenReturn(user);

        // Mock database
        CommonClassDao dao = Mockito.mock(CommonClassDao.class);
        Mockito.when(dao.getDataByQuery(any(), any())).thenReturn(Collections.emptyList());

        // Test watch
        PolicyNotificationController controller = new PolicyNotificationController();
        controller.commonClassDao = dao;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setBodyContent("{\n\"watchData\": {\"name\": \"testVal\",\"path\": \"testPath\"\n}}\n");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ModelAndView model = controller.watchPolicy(request, response);
        assertNull(model);
        assertEquals(response.getStatusCode(), HttpServletResponse.SC_OK);

        // Negative test watch
        request.setBodyContent("{\n\"watchData\": {\"name\": \"testVal\",\"nopath\": \"testPath\"\n}}\n");
        response = new MockHttpServletResponse();
        model = controller.watchPolicy(request, response);
        assertNull(model);
        assertEquals(response.getStatusCode(), HttpServletResponse.SC_OK);
    }
}
