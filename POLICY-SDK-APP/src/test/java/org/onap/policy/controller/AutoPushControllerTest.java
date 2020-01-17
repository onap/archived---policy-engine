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
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.att.research.xacml.api.pap.PAPException;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.model.Roles;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class AutoPushControllerTest {
    private PolicyController controller = new PolicyController();
    private AutoPushController apController = new AutoPushController();

    private static HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    private static HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
    private static PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
    private static HttpSession mockSession = Mockito.mock(HttpSession.class);
    private static User mockUser = Mockito.mock(User.class);
    private static List<Object> rolesList = new ArrayList<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setupMocks() {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(AppUtils.getSession(mockRequest)).thenReturn(mockSession);
        when(UserUtils.getUserSession(mockRequest)).thenReturn(mockUser);
        when(mockUser.getOrgUserId()).thenReturn("");
        when(mockPolicyController.getRoles(any(String.class))).thenReturn(rolesList);
    }

    @Test
    public void testAutoPushSetGet() throws IOException {
        apController.setPolicyController(controller);
        assertEquals(apController.getPolicyController(), controller);
    }

    @Test
    public void testNegativeCase1() {
        try {
            apController.getPolicyGroupContainerData(null, null);
        } catch (Exception ex) {
            fail("No exceptions expected, received: " + ex);
        }
    }

    @Test
    public void testNegativeCase2() throws IOException {
        thrown.expect(NullPointerException.class);
        apController.pushPolicyToPDPGroup(null, null);
    }

    @Test
    public void testNegativeCase3() throws IOException {
        thrown.expect(NullPointerException.class);
        apController.removePDPGroup(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testRefreshGroupsNull() throws IOException {
        apController.refreshGroups();
    }

    @PrepareForTest({UserUtils.class})
    @Test
    public void testRequests() throws Exception {
        // Mock user utilities
        PowerMockito.mockStatic(UserUtils.class);
        User user = new User();
        Mockito.when(UserUtils.getUserSession(Mockito.any())).thenReturn(user);

        // Mock policy controller
        PolicyController policyController = Mockito.mock(PolicyController.class);
        PowerMockito.whenNew(PolicyController.class).withNoArguments().thenReturn(policyController);
        Mockito.when(policyController.getRoles(Mockito.any())).thenReturn(null);

        // Test group container
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        apController.getPolicyGroupContainerData(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());

        // Test push
        apController.pushPolicyToPDPGroup(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());

        // Test remove
        apController.removePDPGroup(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
    }

    @Test
    public void testRefreshGroupsSuccess() throws PAPException {
        PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
        PAPPolicyEngine mockPAPPolicyEngine = Mockito.mock(PAPPolicyEngine.class);
        Set<OnapPDPGroup> onapPDPGroups = new HashSet<>();
        when(mockPolicyController.getPapEngine()).thenReturn(mockPAPPolicyEngine);
        when(mockPAPPolicyEngine.getOnapPDPGroups()).thenReturn(onapPDPGroups);
        apController.setPolicyController(mockPolicyController);
        apController.refreshGroups();
        verify(mockPolicyController).getPapEngine();
        verify(mockPAPPolicyEngine).getOnapPDPGroups();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRefreshGroups() throws PAPException {
        PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
        PAPPolicyEngine mockPAPPolicyEngine = Mockito.mock(PAPPolicyEngine.class);
        when(mockPolicyController.getPapEngine()).thenReturn(mockPAPPolicyEngine);
        when(mockPAPPolicyEngine.getOnapPDPGroups()).thenThrow(PAPException.class);
        apController.setPolicyController(mockPolicyController);
        apController.refreshGroups();
        verify(mockPolicyController).getPapEngine();
        verify(mockPAPPolicyEngine).getOnapPDPGroups();
    }

    @Test
    public void testGetPolicyGroupContainerData() throws Exception {
        Roles superAdmin = new Roles();
        Roles superEditor = new Roles();
        Roles superGuest = new Roles();
        rolesList.add(superAdmin);
        rolesList.add(superEditor);
        rolesList.add(superGuest);

        apController.setPolicyController(mockPolicyController);
        apController.getPolicyGroupContainerData(mockRequest, mockResponse);

        verify(mockRequest, atLeast(1)).getSession(false);
        verify(mockUser, atLeast(1)).getOrgUserId();
        verify(mockPolicyController, atLeast(1)).getRoles(any(String.class));
    }

    @Test
    public void testGetPolicyGroupContainerDataWithScope() throws Exception {
        Roles superAdmin = new Roles();
        superAdmin.setScope("super-admin-scope");
        Roles superEditor = new Roles();
        superEditor.setScope("super-editor-scope");
        Roles superGuest = new Roles();
        superGuest.setScope("super-guest-scope");
        rolesList.add(superAdmin);
        rolesList.add(superEditor);
        rolesList.add(superGuest);

        apController.setPolicyController(mockPolicyController);
        apController.getPolicyGroupContainerData(mockRequest, mockResponse);

        verify(mockRequest, atLeast(1)).getSession(false);
        verify(mockUser, atLeast(1)).getOrgUserId();
        verify(mockPolicyController, atLeast(1)).getRoles(any(String.class));
    }

    @Test
    public void testGetPolicyGroupContainerDataWithRole() throws Exception {
        Roles superAdmin = new Roles();
        superAdmin.setRole("super-admin");
        Roles superEditor = new Roles();
        superEditor.setRole("super-editor");
        Roles superGuest = new Roles();
        superGuest.setRole("super-guest");
        rolesList.add(superAdmin);
        rolesList.add(superEditor);
        rolesList.add(superGuest);

        apController.setPolicyController(mockPolicyController);
        apController.getPolicyGroupContainerData(mockRequest, mockResponse);

        verify(mockRequest, atLeast(1)).getSession(false);
        verify(mockUser, atLeast(1)).getOrgUserId();
        verify(mockPolicyController, atLeast(1)).getRoles(any(String.class));
    }
}
