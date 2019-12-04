/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.admin;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.mock.web.MockHttpServletResponse;

public class PolicyUserInfoControllerTest {

    private HttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        HttpSession mockSession = mock(HttpSession.class);
        request = mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
        User user = new User();
        user.setOrgUserId("Test");
        Mockito.when(mockSession.getAttribute(SystemProperties.getProperty("user_attribute_name"))).thenReturn(user);
        Mockito.when(request.getSession(false)).thenReturn(mockSession);
    }

    @Test
    public final void testGetPolicyUserInfo() {
        PolicyUserInfoController controller = new PolicyUserInfoController();
        controller.getPolicyUserInfo(request, response);
        try {
            assertTrue(response.getStatus() == 200);
        } catch (Exception e) {
            fail();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetPolicyUserInfoException() throws IOException {
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        when(mockResponse.getWriter()).thenThrow(IOException.class);
        PolicyUserInfoController controller = new PolicyUserInfoController();
        controller.getPolicyUserInfo(request, mockResponse);
        verify(mockResponse, atLeast(1)).getWriter();
    }
}
