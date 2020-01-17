/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.daoImp.CommonClassDaoImpl;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class PolicyExportAndImportControllerTest {
    @Test
    public void testSetAndGet() {
        PolicyExportAndImportController controller = new PolicyExportAndImportController();
        PolicyController policyController = new PolicyController();
        controller.setPolicyController(policyController);
        assertEquals(controller.getPolicyController(), policyController);
        CommonClassDao commonClassDao = new CommonClassDaoImpl();
        PolicyExportAndImportController.setCommonClassDao(commonClassDao);
        assertEquals(PolicyExportAndImportController.getCommonClassDao(), commonClassDao);
    }

    @Test
    public void testExport() throws IOException {
        PolicyExportAndImportController controller = new PolicyExportAndImportController();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setBodyContent("{\n\"exportData\": {}\n}\n");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Test negative case
        controller.exportPolicy(request, response);
        assertEquals(response.getStatusCode(), HttpServletResponse.SC_OK);
    }

    @PrepareForTest({UserUtils.class})
    @Test
    public void testImport() throws IOException {
        // Mock user utilities
        PowerMockito.mockStatic(UserUtils.class);
        User user = new User();
        when(UserUtils.getUserSession(any())).thenReturn(user);

        // Mock dao
        UserInfo info = new UserInfo();
        ConfigurationDataEntity configEntity = new ConfigurationDataEntity();
        CommonClassDao commonClassDao = Mockito.mock(CommonClassDaoImpl.class);
        when(commonClassDao.getEntityItem(eq(UserInfo.class), any(), any())).thenReturn(info);
        when(commonClassDao.getEntityItem(eq(ConfigurationDataEntity.class), any(), any())).thenReturn(configEntity);
        when(commonClassDao.getDataById(any(), any(), any())).thenReturn(Collections.emptyList());

        // Test import
        PolicyController policyController = new PolicyController();
        PolicyController.setCommonClassDao(commonClassDao);
        PolicyExportAndImportController controller = new PolicyExportAndImportController();
        PolicyExportAndImportController.setCommonClassDao(commonClassDao);
        controller.setPolicyController(policyController);
        HttpServletRequest request = new MockHttpServletRequest();
        ClassLoader classLoader = getClass().getClassLoader();

        // Test negative case
        String file = new File(classLoader.getResource("Config_BRMS_Raw_TestBRMSRawPolicy.1.xml").getFile())
                .getAbsolutePath();
        JSONObject json = controller.importRepositoryFile(file, request);
        assertNull(json);

        // Another negative case
        file = new File(classLoader.getResource("PolicyExport.xls").getFile()).getAbsolutePath();
        json = controller.importRepositoryFile(file, request);
        assertNull(json);

        // test validation
        String jsonString = "{ configName:\"abc\", uuid:\"someone\", location:\"somewhere\", policyScope:\"test\", "
                + "service:\"sdnc\", version:\"1810\"}";
        String errorMsg = controller.validatMatchRequiredFields("TestPolicy", jsonString);
        assertTrue(errorMsg != null);
    }
}
