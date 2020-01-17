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

package org.onap.policy.pap.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.UpdateOthersPAPS;
import org.onap.policy.pap.xacml.rest.adapters.UpdateObjectData;
import org.onap.policy.pap.xacml.rest.components.Policy;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyDbDaoEntity;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xalan.*", "com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(UpdateOthersPAPS.class)
public class UpdateOthersPAPSTest {

    private static Logger logger = FlexLogger.getLogger(UpdateOthersPAPSTest.class);
    private static CommonClassDao commonClassDao;
    private HttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = mock(CommonClassDao.class);

        request = mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
        List<Object> data = new ArrayList<>();
        PolicyDbDaoEntity entity = new PolicyDbDaoEntity();
        entity.setPolicyDbDaoUrl("http://localhost:8070/pap");
        entity.setUsername("test");
        entity.setPassword("test");

        PolicyDbDaoEntity entity1 = new PolicyDbDaoEntity();
        entity1.setPolicyDbDaoUrl("http://localhost:8071/pap");
        entity1.setUsername("test");
        entity1.setPassword("test");

        data.add(entity);
        data.add(entity1);
        System.setProperty("xacml.rest.pap.url", "http://localhost:8070/pap");
        when(commonClassDao.getData(PolicyDbDaoEntity.class)).thenReturn(data);
    }

    @Test
    public void testNotifyOthersPAPsToUpdateConfigurations() {
        UpdateOthersPAPS updateOtherPaps = new UpdateOthersPAPS();
        UpdateOthersPAPS.setCommonClassDao(commonClassDao);
        when(request.getParameter("action")).thenReturn("rename");
        when(request.getParameter("newPolicyName")).thenReturn("com.Config_newTest.1.json");
        when(request.getParameter("oldPolicyName")).thenReturn("com.Config_Test.1.json");
        updateOtherPaps.notifyOthersPAPsToUpdateConfigurations(request, response);
        try {
            JSONObject responseString = new JSONObject(response.getContentAsString());
            assertTrue(responseString.get("data").toString().contains("http://localhost:8071/pap"));
        } catch (Exception e) {
            fail();
        }
    }

    @PrepareForTest({Policy.class})
    @Test
    public void testUpdateConfiguration() throws Exception {
        UpdateOthersPAPS updateOtherPaps = new UpdateOthersPAPS();
        UpdateObjectData data = new UpdateObjectData();
        PowerMockito.mockStatic(Policy.class);
        data.setNewPolicyName("com.Config_newTest.1.json");
        data.setOldPolicyName("com.Config_Test.1.json");
        data.setAction("rename");
        when(Policy.getConfigHome()).thenReturn("test");
        when(Policy.getActionHome()).thenReturn("test");
        File mockedFile = Mockito.mock(File.class);
        Mockito.when(mockedFile.exists()).thenReturn(true);
        PowerMockito.whenNew(File.class).withParameterTypes(String.class).withArguments(anyString())
                .thenReturn(mockedFile);
        updateOtherPaps.updateConfiguration(data, response);
        assertTrue(response.getStatus() == 200);
    }
}
