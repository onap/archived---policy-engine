/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2020 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.json.JsonArray;
import javax.script.SimpleBindings;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controller.CreateDcaeMicroServiceController;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.model.Roles;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.UserUtils.Pair;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PolicyManagerServletTest extends Mockito {

    private static Logger logger = FlexLogger.getLogger(PolicyManagerServletTest.class);
    private List<String> headers = new ArrayList<String>();

    private static List<Object> rolesdata;
    private static List<Object> basePolicyData;
    private static List<Object> policyEditorScopes;
    private static List<Object> policyVersion;
    private static CommonClassDao commonClassDao;
    private ConfigurationDataEntity configurationEntity;
    private HttpServletRequest request;
    private MockHttpServletResponse response;

    /**
     * setUp.
     *
     * @throws Exception should not get one
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");

        request = mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();

        PolicyController.setjUnit(true);
        UserInfo userinfo = new UserInfo();
        userinfo.setUserLoginId("Test");
        userinfo.setUserName("Test");
        // Roles Data
        rolesdata = new ArrayList<>();
        Roles roles = new Roles();
        roles.setLoginId("Test");
        roles.setRole("super-admin");
        Roles roles1 = new Roles();
        roles1.setId(1);
        roles1.setName("Test");
        assertTrue("Test".equals(roles1.getName()));
        roles1.setLoginId("Test");
        roles1.setRole("admin");
        roles1.setScope("['com','Test']");
        rolesdata.add(roles);
        rolesdata.add(roles1);

        // PolicyEntity Data
        basePolicyData = new ArrayList<>();
        String policyContent = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_SampleTest.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody("Sample Test");
        configurationEntity.setConfigType("OTHER");
        configurationEntity.setConfigurationName("com.Config_SampleTest1206.1.txt");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        basePolicyData.add(entity);

        // PolicyEditorScopes data
        policyEditorScopes = new ArrayList<>();
        PolicyEditorScopes scopes = new PolicyEditorScopes();
        scopes.setScopeName("com");
        scopes.setUserCreatedBy(userinfo);
        scopes.setUserModifiedBy(userinfo);
        PolicyEditorScopes scopes1 = new PolicyEditorScopes();
        scopes1.setScopeName("com\\Test");
        scopes1.setUserCreatedBy(userinfo);
        scopes1.setUserModifiedBy(userinfo);
        policyEditorScopes.add(scopes);
        policyEditorScopes.add(scopes1);

        // PolicyVersion data
        policyVersion = new ArrayList<>();
        PolicyVersion policy = new PolicyVersion();
        policy.setPolicyName("com\\Config_SampleTest1206");
        policy.setActiveVersion(1);
        policy.setHigherVersion(1);
        policy.setCreatedBy("Test");
        policy.setModifiedBy("Test");
        policyVersion.add(policy);

        HttpSession mockSession = mock(HttpSession.class);
        User user = new User();
        user.setOrgUserId("Test");
        Mockito.when(mockSession.getAttribute(SystemProperties.getProperty("user_attribute_name"))).thenReturn(user);
        Mockito.when(request.getSession(false)).thenReturn(mockSession);
        commonClassDao = mock(CommonClassDao.class);

    }

    @Test
    public void test01Init() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        ServletConfig servletConfig = mock(ServletConfig.class);
        try {
            when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
            when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("xacml.admin.properties");
            System.setProperty("xacml.rest.admin.closedLoopJSON",
                    new File(".").getCanonicalPath() + File.separator + "src" + File.separator + "test" + File.separator
                            + "resources" + File.separator + "JSONConfig.json");
            servlet.init(servletConfig);

            assertTrue(PolicyManagerServlet.getServiceTypeNamesList().size() > 0);
            assertTrue(PolicyManagerServlet.getPolicyNames().size() > 0);

        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
            fail();
        }
    }

    @Test
    public void test02BadInitJson() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        ServletConfig servletConfig = mock(ServletConfig.class);
        try {
            when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
            when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("xacml.admin.properties");
            System.setProperty("xacml.rest.admin.closedLoopJSON",
                    new File(".").getCanonicalPath() + File.separator + "src" + File.separator + "test" + File.separator
                            + "resources" + File.separator + "JSONConfig.foo");
            servlet.init(servletConfig);
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
            fail();
        }
    }

    @Test
    public void test03BadInitJsonInvalidFile() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        ServletConfig servletConfig = mock(ServletConfig.class);
        try {
            when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
            when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("xacml.admin.properties");
            System.setProperty("xacml.rest.admin.closedLoopJSON",
                    new File(".").getCanonicalPath() + File.separator + "src" + File.separator + "test" + File.separator
                            + "resources" + File.separator + "IDonotExist.json");
            servlet.init(servletConfig);
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
            fail();
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test04DescribePolicy() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        BufferedReader reader = new BufferedReader(
                new StringReader("{params: { mode: 'DESCRIBEPOLICYFILE', path: 'com.Config_SampleTest1206.1.xml'}}"));
        try {
            when(request.getReader()).thenReturn(reader);
            String query = "FROM PolicyEntity where policyName = :split_1 and scope = :split_0";
            when(controller.getDataByQuery(query, null)).thenReturn(basePolicyData);
            servlet.setPolicyController(controller);
            servlet.doPost(request, response);
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
            fail();
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test05PolicyScopeList() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'LIST', path: '/', onlyFolders: false}}");
        list.add("{params: { mode: 'LIST', path: '/com', onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("from PolicyEditorScopes", null)).thenReturn(policyEditorScopes);
                when(controller.getDataByQuery("from PolicyEditorScopes where SCOPENAME like :scopeName", null))
                        .thenReturn(policyEditorScopes);
                when(controller.getDataByQuery("from PolicyVersion where POLICY_NAME like :scopeName", null))
                        .thenReturn(policyVersion);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test06editBasePolicyTest() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_SampleTest1206.1.xml', onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(basePolicyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test07editBRMSParamPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent =
                    IOUtils.toString(classLoader.getResourceAsStream("Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml"));
            configData = IOUtils
                    .toString(classLoader.getResourceAsStream("com.Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.txt"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody(configData);
        configurationEntity.setConfigType("OTHER");
        configurationEntity.setConfigurationName("com.Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.txt");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml',"
                + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test08editBRMSRawPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent =
                    IOUtils.toString(classLoader.getResourceAsStream("Config_BRMS_Raw_TestBRMSRawPolicy.1.xml"));
            configData =
                    IOUtils.toString(classLoader.getResourceAsStream("com.Config_BRMS_Raw_TestBRMSRawPolicy.1.txt"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_BRMS_Raw_TestBRMSRawPolicy.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody(configData);
        configurationEntity.setConfigType("OTHER");
        configurationEntity.setConfigurationName("com.Config_BRMS_Raw_TestBRMSRawPolicy.1.txt");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_BRMS_Raw_TestBRMSRawPolicy.1.xml',"
                + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test09editClosedLoopFaultPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent =
                    IOUtils.toString(classLoader.getResourceAsStream("Config_Fault_TestClosedLoopPolicy.1.xml"));
            configData =
                    IOUtils.toString(classLoader.getResourceAsStream("com.Config_Fault_TestClosedLoopPolicy.1.json"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_Fault_TestClosedLoopPolicy.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody(configData);
        configurationEntity.setConfigType("JSON");
        configurationEntity.setConfigurationName("com.Config_Fault_TestClosedLoopPolicy.1.json");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_Fault_TestClosedLoopPolicy.1.xml',"
                + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test10editClosedLoopPMPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils.toString(classLoader.getResourceAsStream("Config_PM_TestClosedLoopPMPolicy.1.xml"));
            configData =
                    IOUtils.toString(classLoader.getResourceAsStream("com.Config_PM_TestClosedLoopPMPolicy.1.json"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_PM_TestClosedLoopPMPolicy.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody(configData);
        configurationEntity.setConfigType("JSON");
        configurationEntity.setConfigurationName("com.Config_PM_TestClosedLoopPMPolicy.1.json");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_PM_TestClosedLoopPMPolicy.1.xml',"
                + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test11editMicroServicePolicyTest() {
        GroupPolicyScopeList groupData = new GroupPolicyScopeList();
        groupData.setGroupName("Test");
        groupData.setGroupList("resource=SampleResource,service=SampleService,type=SampleType,"
                + "closedLoopControlName=SampleClosedLoop");
        List<Object> groupListData = new ArrayList<>();
        groupListData.add(groupData);
        commonClassDao = mock(CommonClassDao.class);
        CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils.toString(classLoader.getResourceAsStream("Config_MS_vFirewall.1.xml"));
            configData = IOUtils.toString(classLoader.getResourceAsStream("com.Config_MS_vFirewall.1.json"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_MS_vFirewall.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody(configData);
        configurationEntity.setConfigType("JSON");
        configurationEntity.setConfigurationName("com.Config_MS_vFirewall.1.json");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_MS_vFirewall.1.xml', onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(commonClassDao.getDataById(GroupPolicyScopeList.class, "groupList",
                        "resource=SampleResource,service=SampleService,type=SampleType,"
                                + "closedLoopControlName=SampleClosedLoop")).thenReturn(groupListData);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test12editFirewallPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils.toString(classLoader.getResourceAsStream("Config_FW_TestFireWallPolicy.1.xml"));
            configData = IOUtils.toString(classLoader.getResourceAsStream("com.Config_FW_TestFireWallPolicy.1.json"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_FW_TestFireWallPolicy.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody(configData);
        configurationEntity.setConfigType("JSON");
        configurationEntity.setConfigurationName("com.Config_FW_TestFireWallPolicy.1.json");
        configurationEntity.setDescription("test");
        entity.setConfigurationData(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Config_FW_TestFireWallPolicy.1.xml',"
                + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test13editActionPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        String configData = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils.toString(classLoader.getResourceAsStream("Action_TestActionPolicy.1.xml"));
            configData = IOUtils.toString(classLoader.getResourceAsStream("com.Action_TestActionPolicy.1.json"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Action_TestActionPolicy.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        ActionBodyEntity configurationEntity = new ActionBodyEntity();
        configurationEntity.setActionBody(configData);
        configurationEntity.setActionBodyName("com.Action_TestActionPolicy.1.json");
        entity.setActionBodyEntity(configurationEntity);
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Action_TestActionPolicy.1.xml'," + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void test14editDecisionPolicyTest() {
        List<Object> policyData = new ArrayList<>();
        String policyContent = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils
                    .toString(classLoader.getResourceAsStream("Decision_TestDecisionPolicyWithRuleAlgorithms.1.xml"));
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Decision_TestDecisionPolicyWithRuleAlgorithms.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        policyData.add(entity);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'EDITFILE', path: '/com/Decision_TestDecisionPolicyWithRuleAlgorithms.1.xml',"
                + " onlyFolders: false}}");
        for (int i = 0; i < list.size(); i++) {
            BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
                when(request.getReader()).thenReturn(reader);
                when(controller.getRoles("Test")).thenReturn(rolesdata);
                when(controller.getDataByQuery("FROM PolicyEntity where policyName = :split_1 and scope = :split_0",
                        null)).thenReturn(policyData);
                servlet.setPolicyController(controller);
                servlet.setTestUserId("Test");
                servlet.doPost(request, response);
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @Test
    public void test15AddScope() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<BufferedReader> readers = new ArrayList<>();
        readers.add(new BufferedReader(new StringReader("{params: { mode: 'ADDFOLDER', path: '/', name: 'Test'}}")));
        readers.add(new BufferedReader(new StringReader("{params: { mode: 'ADDFOLDER', path: '/', name: 'Test*&'}}")));
        readers.add(new BufferedReader(
                new StringReader("{params: { mode: 'ADDFOLDER', path: '/Test', subScopename: 'Test1'}}")));
        for (int i = 0; i < readers.size(); i++) {
            try {
                when(request.getReader()).thenReturn(readers.get(i));
                PolicyManagerServlet.setPolicyController(controller);
                servlet.doPost(request, response);
                assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("success"));
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @Test
    public void test16Clone() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<BufferedReader> readers = new ArrayList<>();
        when(controller.getEntityItem(ConfigurationDataEntity.class, "configurationName",
                "com.Config_SampleTest1206.1.txt")).thenReturn(configurationEntity);
        when(controller.getDataByQuery(
                "FROM PolicyEntity where policyName = :oldPolicySplit_1 and scope = :oldPolicySplit_0", null))
                        .thenReturn(basePolicyData);
        readers.add(new BufferedReader(new StringReader("{params: { mode: 'COPY', path: 'com.Config_test.1.xml',"
                + " newPath: 'com.Config_testClone.1.xml'}}")));
        for (int i = 0; i < readers.size(); i++) {
            try {
                when(request.getReader()).thenReturn(readers.get(i));
                PolicyManagerServlet.setPolicyController(controller);
                servlet.doPost(request, response);
                assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("success"));
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @Test
    public void test17Rename() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<BufferedReader> readers = new ArrayList<>();
        when(controller.getEntityItem(ConfigurationDataEntity.class, "configurationName",
                "com.Config_SampleTest1206.1.txt")).thenReturn(configurationEntity);
        when(controller.getDataByQuery(
                "FROM PolicyEntity where policyName = :oldPolicySplit_1 and scope = :oldPolicySplit_0", null))
                        .thenReturn(basePolicyData);
        readers.add(new BufferedReader(new StringReader("{params: { mode: 'RENAME', path: 'com.Config_test.1.xml',"
                + " newPath: 'com.Config_testClone.1.xml'}}")));
        for (int i = 0; i < readers.size(); i++) {
            try {
                when(request.getReader()).thenReturn(readers.get(i));
                PolicyManagerServlet.setPolicyController(controller);
                servlet.doPost(request, response);
                assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("success"));
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }
    }

    @Test
    public void test18RenameScope() throws Exception {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController controller = mock(PolicyController.class);
        List<BufferedReader> readers = new ArrayList<>();
        readers.add(new BufferedReader(new StringReader("{params: { mode: 'RENAME', path: 'com', newPath: 'Test'}}")));
        for (int i = 0; i < readers.size(); i++) {
            try {
                when(request.getReader()).thenReturn(readers.get(i));
                PolicyManagerServlet.setPolicyController(controller);
                servlet.doPost(request, response);
                assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("success"));
            } catch (Exception e1) {
                logger.error("Exception Occured" + e1);
                fail();
            }
        }

        String inScopeName = "\\\\\\\\inScopeName";
        String newScopeName = "\\\\\\\\newScopeName";
        List<Object> scopesList = new ArrayList<Object>();
        PolicyEditorScopes mockPolicyEditorScope = Mockito.mock(PolicyEditorScopes.class);
        scopesList.add(mockPolicyEditorScope);
        when(mockPolicyEditorScope.getScopeName()).thenReturn("inScopeName");
        Whitebox.invokeMethod(servlet, "renameScope", scopesList, inScopeName, newScopeName, controller);
        verify(mockPolicyEditorScope, atLeast(1)).getScopeName();
    }

    @Test
    public void test19SetPolicyNames() {
        JsonArray mockJsonArray = Mockito.mock(JsonArray.class);
        PolicyManagerServlet.setPolicyNames(mockJsonArray);
        assertEquals(mockJsonArray, PolicyManagerServlet.getPolicyNames());
    }

    @Test
    public void test20DoPostSetErrorException() throws IOException {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        doThrow(IOException.class).when(mockRequest).getReader();
        doThrow(IOException.class).when(mockResponse).sendError(any(Integer.class), any(String.class));
        servlet.doPost(mockRequest, mockResponse);
        verify(mockRequest).getReader();
    }

    @Test
    public void test21DoPostException() throws IOException {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);

        doThrow(IOException.class).when(mockRequest).getReader();
        doThrow(IOException.class).when(mockResponse).sendError(any(Integer.class), any(String.class));
        doThrow(IOException.class).when(mockResponse).getWriter();

        servlet.doPost(mockRequest, mockResponse);
        verify(mockRequest).getReader();
        verify(mockResponse).getWriter();
    }

    @Test
    public void test22DoPostSuccess() throws IOException {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        PrintWriter mockPrintWriter = Mockito.mock(PrintWriter.class);

        doThrow(IOException.class).when(mockRequest).getReader();
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);

        servlet.doPost(null, mockResponse);
        verify(mockResponse).getWriter();
    }

    @PrepareForTest(ServletFileUpload.class)
    @Test
    public void test23DoPostUploadFileException() {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        PowerMockito.mockStatic(ServletFileUpload.class);
        when(ServletFileUpload.isMultipartContent(mockRequest)).thenReturn(true);
        servlet.doPost(mockRequest, mockResponse);
    }

    @PrepareForTest({PolicyController.class, IOUtils.class})
    @Test
    public void test24ProcessFormFile() throws Exception {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        FileItem mockFileItem = Mockito.mock(FileItem.class);
        PowerMockito.mockStatic(PolicyController.class);
        PowerMockito.mockStatic(IOUtils.class);
        InputStream mockInputStream = Mockito.mock(InputStream.class);

        long fileSizeLimit = 10;
        when(PolicyController.getFileSizeLimit()).thenReturn(fileSizeLimit);
        when(mockFileItem.getName()).thenReturn("testFileName.xls");
        when(mockFileItem.getInputStream()).thenReturn(mockInputStream);
        when(mockFileItem.getSize()).thenReturn(fileSizeLimit + 1);

        Whitebox.invokeMethod(servlet, "processFormFile", mockRequest, mockFileItem);
        verify(mockFileItem, atLeast(1)).getName();
        verify(mockFileItem, atLeast(1)).getSize();

        when(mockFileItem.getName()).thenReturn("testFileName.txt");
        Whitebox.invokeMethod(servlet, "processFormFile", mockRequest, mockFileItem);
        verify(mockFileItem, atLeast(1)).getName();

        when(mockFileItem.getSize()).thenReturn(fileSizeLimit);
        when(mockFileItem.getName()).thenReturn("testFileName.xls");
        when(mockFileItem.getInputStream()).thenThrow(IOException.class);
        Whitebox.invokeMethod(servlet, "processFormFile", mockRequest, mockFileItem);
        verify(mockFileItem, atLeast(1)).getName();
        verify(mockFileItem, atLeast(1)).getInputStream();
        verify(mockFileItem, atLeast(1)).getSize();
    }

    @Test
    public void test25SearchPolicyList() throws Exception {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        JSONObject mockJSONObject = Mockito.mock(JSONObject.class);

        mockJSONObject.append("policyList", "sampleValue");

        Object res = Whitebox.invokeMethod(servlet, "searchPolicyList", mockJSONObject, mockRequest);
        assert (res instanceof JSONObject);
        assertNotNull(((JSONObject) res).get("result"));
    }

    @PrepareForTest({UserUtils.class, org.onap.policy.utils.UserUtils.class})
    @Test
    public void test26LookupPolicyData() throws Exception {
        PowerMockito.mockStatic(UserUtils.class);
        PowerMockito.mockStatic(org.onap.policy.utils.UserUtils.class);
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        User mockUser = Mockito.mock(User.class);
        UserInfo mockUserInfo = Mockito.mock(UserInfo.class);
        PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
        List<JSONObject> resultList = new ArrayList<>();
        JSONObject mockJSONObject = Mockito.mock(JSONObject.class);
        resultList.add(mockJSONObject);

        Date mockDate = Mockito.mock(Date.class);
        List<Object> policyDataList = new ArrayList<>();
        PolicyVersion mockPolicyVersion = Mockito.mock(PolicyVersion.class);
        policyDataList.add(mockPolicyVersion);
        JSONArray mockJSONArray = Mockito.mock(JSONArray.class);

        List<Object> rolesList = new ArrayList<>();
        Roles adminRole = Mockito.mock(Roles.class);
        Roles editorRole = Mockito.mock(Roles.class);
        Roles guestRole = Mockito.mock(Roles.class);
        adminRole.setRole("admin");
        editorRole.setRole("editor");
        guestRole.setRole("guest");

        List<Object> filterDataList = new ArrayList<>();
        PolicyVersion mockPolicyVersionFilter = Mockito.mock(PolicyVersion.class);
        filterDataList.add(mockPolicyVersionFilter);
        List<String> listOfRoles = new ArrayList<String>();
        Set<String> setOfScopes = new HashSet<String>();
        Pair<Set<String>, List<String>> pairList = new Pair<Set<String>, List<String>>(setOfScopes, listOfRoles);

        PolicyManagerServlet.setPolicyController(mockPolicyController);
        PowerMockito.when(UserUtils.getUserSession(mockRequest)).thenReturn(mockUser);
        when(mockPolicyController.getRoles(any(String.class))).thenReturn(rolesList);
        PowerMockito.when(org.onap.policy.utils.UserUtils.checkRoleAndScope(rolesList)).thenReturn(pairList);
        when(mockPolicyController.getData(any(Class.class))).thenReturn(filterDataList);
        when(mockPolicyVersion.getPolicyName()).thenReturn("sampleName");
        when(mockPolicyVersion.getModifiedDate()).thenReturn(mockDate);
        when(mockPolicyVersion.getActiveVersion()).thenReturn(1);
        when(mockPolicyVersion.getCreatedBy()).thenReturn("sampleUserName");
        when(mockPolicyVersion.getModifiedBy()).thenReturn("sampleUserName");
        when(mockPolicyController.getEntityItem(UserInfo.class, "userLoginId", "sampleUserName"))
                .thenReturn(mockUserInfo);
        when(mockUserInfo.getUserName()).thenReturn("testUserName");

        Whitebox.invokeMethod(servlet, "getPolicyControllerInstance");

        boolean result = Whitebox.invokeMethod(servlet, "lookupPolicyData", mockRequest, policyDataList, null,
                mockPolicyController, resultList);

        assertTrue(result);
        verify(mockPolicyController, atLeast(1)).getRoles(any());
        verify(mockPolicyController, atLeast(1)).getRoles(any());
        verify(mockPolicyController, atLeast(1)).getData(any());
        verify(mockPolicyController, atLeast(1)).getEntityItem(UserInfo.class, "userLoginId", "sampleUserName");
        verify(mockPolicyVersion, atLeast(1)).getPolicyName();
        verify(mockPolicyVersion, atLeast(1)).getModifiedDate();
        verify(mockPolicyVersion, atLeast(1)).getActiveVersion();
        verify(mockPolicyVersion, atLeast(1)).getCreatedBy();
        verify(mockPolicyVersion, atLeast(1)).getModifiedBy();
        verify(mockUserInfo, atLeast(1)).getUserName();

        when(mockPolicyVersionFilter.getPolicyName()).thenReturn("testAdminScope" + File.separator);
        result = Whitebox.invokeMethod(servlet, "lookupPolicyData", mockRequest, policyDataList, null,
                mockPolicyController, resultList);
        assertTrue(result);
        verify(mockPolicyVersionFilter, atLeast(1)).getPolicyName();

        setOfScopes.add("testAdminScope");
        result = Whitebox.invokeMethod(servlet, "lookupPolicyData", mockRequest, policyDataList, null,
                mockPolicyController, resultList);
        assertTrue(result);
        verify(mockPolicyVersionFilter, atLeast(1)).getPolicyName();

        listOfRoles.add("super-admin");
        listOfRoles.add("super-editor");
        listOfRoles.add("super-guest");
        filterDataList.clear();

        result = Whitebox.invokeMethod(servlet, "lookupPolicyData", mockRequest, policyDataList, null,
                mockPolicyController, resultList);
        assertTrue(result);
        verify(mockPolicyController, atLeast(1)).getData(any(Class.class));

        listOfRoles.clear();
        listOfRoles.add("admin");
        listOfRoles.add("editor");
        listOfRoles.add("guest");
        setOfScopes.clear();
        result = Whitebox.invokeMethod(servlet, "lookupPolicyData", mockRequest, policyDataList, null,
                mockPolicyController, resultList);
        assertFalse(result);

        setOfScopes.add("testScope");
        result = Whitebox.invokeMethod(servlet, "lookupPolicyData", mockRequest, policyDataList, mockJSONArray,
                mockPolicyController, resultList);
        assertTrue(result);
    }

    @Test
    public void test27DeleteEntityFromEsAndPolicyEntityTable() throws Exception {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
        PolicyRestController mockPolicyRestController = Mockito.mock(PolicyRestController.class);
        PolicyEntity mockPolicyEntity = Mockito.mock(PolicyEntity.class);
        ConfigurationDataEntity mockConfigDataEntity = Mockito.mock(ConfigurationDataEntity.class);
        ActionBodyEntity mockActionBodyEntity = Mockito.mock(ActionBodyEntity.class);

        String policyNamewithoutExtension = "Config_";
        String configName = "";
        String actionBodyName = "";

        when(mockPolicyEntity.getScope()).thenReturn("");
        when(mockPolicyEntity.getPolicyName()).thenReturn("");
        Mockito.doNothing().when(mockPolicyRestController).deleteElasticData(any(String.class));
        Mockito.doNothing().when(mockPolicyController).deleteData(mockPolicyEntity);
        when(mockPolicyEntity.getConfigurationData()).thenReturn(mockConfigDataEntity);
        when(mockPolicyEntity.getActionBodyEntity()).thenReturn(mockActionBodyEntity);
        when(mockConfigDataEntity.getConfigurationName()).thenReturn(configName);
        when(mockActionBodyEntity.getActionBodyName()).thenReturn(actionBodyName);
        when(mockPolicyRestController.notifyOtherPapsToUpdateConfigurations("delete", null, configName)).thenReturn("");

        Whitebox.invokeMethod(servlet, "deleteEntityFromEsAndPolicyEntityTable", mockPolicyController,
                mockPolicyRestController, mockPolicyEntity, policyNamewithoutExtension);

        verify(mockPolicyEntity, atLeast(1)).getScope();
        verify(mockPolicyEntity, atLeast(1)).getPolicyName();
        verify(mockPolicyEntity, atLeast(1)).getConfigurationData();
        verify(mockConfigDataEntity, atLeast(1)).getConfigurationName();

        policyNamewithoutExtension = "Action_";
        when(mockPolicyRestController.notifyOtherPapsToUpdateConfigurations("delete", null, actionBodyName))
                .thenReturn("");

        Whitebox.invokeMethod(servlet, "deleteEntityFromEsAndPolicyEntityTable", mockPolicyController,
                mockPolicyRestController, mockPolicyEntity, policyNamewithoutExtension);

        verify(mockPolicyEntity, atLeast(1)).getScope();
        verify(mockPolicyEntity, atLeast(1)).getPolicyName();
        verify(mockPolicyEntity, atLeast(1)).getActionBodyEntity();
        verify(mockActionBodyEntity, atLeast(1)).getActionBodyName();

        policyNamewithoutExtension = "Other_";
        Whitebox.invokeMethod(servlet, "deleteEntityFromEsAndPolicyEntityTable", mockPolicyController,
                mockPolicyRestController, mockPolicyEntity, policyNamewithoutExtension);

        verify(mockPolicyEntity, atLeast(1)).getScope();
        verify(mockPolicyEntity, atLeast(1)).getPolicyName();
        verify(mockPolicyRestController, atLeast(1)).deleteElasticData(any(String.class));
        verify(mockPolicyController, atLeast(1)).deleteData(mockPolicyEntity);
    }

    @PrepareForTest(UserUtils.class)
    @Test
    public void test28Delete() throws Exception {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        JSONObject mockJSONObject = Mockito.mock(JSONObject.class);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        User mockUser = Mockito.mock(User.class);
        PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
        PowerMockito.mockStatic(UserUtils.class);
        List<Object> policyEntityList = new ArrayList<Object>();
        PolicyEntity mockPolicyEntity = Mockito.mock(PolicyEntity.class);
        policyEntityList.add(mockPolicyEntity);
        long policyId = 1;

        PowerMockito.when(UserUtils.getUserSession(mockRequest)).thenReturn(mockUser);
        PolicyManagerServlet.setPolicyController(mockPolicyController);
        when(mockUser.getOrgUserId()).thenReturn("sampleUserId");
        when(mockJSONObject.getString("path")).thenReturn("/sampleScope:samplePolicyName.1.xml");
        when(mockJSONObject.has("deleteVersion")).thenReturn(true);
        when(mockJSONObject.getString("deleteVersion")).thenReturn("ALL");
        when(mockPolicyController.getDataByQuery(any(String.class), any(SimpleBindings.class)))
                .thenReturn(policyEntityList);
        when(mockPolicyEntity.getPolicyId()).thenReturn(policyId);
        when(mockPolicyEntity.getScope()).thenReturn("sampleScope");
        when(mockPolicyEntity.getPolicyName()).thenReturn("samplePolicyName");

        JSONObject returnObj = Whitebox.invokeMethod(servlet, "delete", mockJSONObject, mockRequest);
        assertTrue(returnObj.has("result"));
        verify(mockUser, atLeast(1)).getOrgUserId();
        verify(mockJSONObject, atLeast(1)).getString(any(String.class));
        verify(mockJSONObject, atLeast(1)).has(any(String.class));
        verify(mockPolicyController, atLeast(1)).getDataByQuery(any(String.class), any(SimpleBindings.class));
        verify(mockPolicyEntity, atLeast(1)).getPolicyId();
        verify(mockPolicyEntity, atLeast(1)).getScope();
        verify(mockPolicyEntity, atLeast(1)).getPolicyName();

        when(mockJSONObject.getString("path")).thenReturn("/sampleScope\\:samplePolicyName.1.xml");
        when(mockJSONObject.getString("deleteVersion")).thenReturn("CURRENT");
        returnObj = Whitebox.invokeMethod(servlet, "delete", mockJSONObject, mockRequest);
        assertTrue(returnObj.has("result"));
        verify(mockJSONObject, atLeast(1)).getString(any(String.class));

        when(mockJSONObject.getString("path")).thenReturn("/sampleScope:samplePolicyName.2.txt");
        when(mockJSONObject.has("deleteVersion")).thenReturn(false);
        returnObj = Whitebox.invokeMethod(servlet, "delete", mockJSONObject, mockRequest);
        assertTrue(returnObj.has("result"));
        verify(mockJSONObject, atLeast(1)).getString("path");
        verify(mockJSONObject, atLeast(1)).has("deleteVersion");
    }

    @Ignore
    @Test
    public void test29ParsePolicyList() throws Exception {
        PolicyManagerServlet servlet = new PolicyManagerServlet();
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        PolicyController mockPolicyController = Mockito.mock(PolicyController.class);
        String policyName = "sampleName\\";
        String policyVersion = "sampleVersion";
        List<Object> activeDataList = new ArrayList<Object>();
        PolicyVersion mockPolicyVersion = Mockito.mock(PolicyVersion.class);
        activeDataList.add(mockPolicyVersion);
        Date mockDate = Mockito.mock(Date.class);

        when(mockPolicyController.getDataByQuery(any(String.class), any(SimpleBindings.class)))
                .thenReturn(activeDataList);
        when(mockPolicyVersion.getPolicyName()).thenReturn("testPolicyName");
        when(mockPolicyVersion.getModifiedDate()).thenReturn(mockDate);
        when(mockPolicyVersion.getActiveVersion()).thenReturn(1);
        when(mockPolicyVersion.getCreatedBy()).thenReturn("sampleUserName");
        when(mockPolicyVersion.getModifiedBy()).thenReturn("sampleUserName");
        //
        // This intermittently throws an NPE, even when fixing the method order
        //
        Whitebox.invokeMethod(servlet, "parsePolicyList", resultList, mockPolicyController, policyName, policyVersion);
        verify(mockPolicyController, atLeast(1)).getDataByQuery(any(String.class), any(SimpleBindings.class));
        verify(mockPolicyVersion, atLeast(1)).getPolicyName();
        verify(mockPolicyVersion, atLeast(1)).getModifiedDate();
        verify(mockPolicyVersion, atLeast(1)).getActiveVersion();
        verify(mockPolicyVersion, atLeast(1)).getCreatedBy();
        verify(mockPolicyVersion, atLeast(1)).getModifiedBy();
    }
}
