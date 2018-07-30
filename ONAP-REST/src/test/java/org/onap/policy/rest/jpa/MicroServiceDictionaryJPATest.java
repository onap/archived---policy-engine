/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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
package org.onap.policy.rest.jpa;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class MicroServiceDictionaryJPATest {

    private static Logger logger = FlexLogger.getLogger(MicroServiceDictionaryJPATest.class);
    private UserInfo userInfo;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");
        logger.info("setUp: exit");
    }

    @Test
    public void testDCAEUsers(){
        DCAEUsers data = new DCAEUsers();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testDCAEuuid(){
        DCAEuuid data = new DCAEuuid();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    @Test
    public void testMSConfigName(){
        MicroServiceConfigName data = new MicroServiceConfigName();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testMSConfigLocation(){
        MicroServiceLocation data = new MicroServiceLocation();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testMSModels(){
        MicroServiceModels data = new MicroServiceModels();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setModelName("Test");
        assertTrue("Test".equals(data.getModelName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setDependency("Test");
        assertTrue("Test".equals(data.getDependency()));
        data.setEnumValues("Test");
        assertTrue("Test".equals(data.getEnumValues()));
        data.setAnnotation("Test");
        assertTrue("Test".equals(data.getAnnotation()));
        data.setAttributes("Test");
        assertTrue("Test".equals(data.getAttributes()));
        data.setRef_attributes("Test");
        assertTrue("Test".equals(data.getRef_attributes()));
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!=null);
        data.setSub_attributes("Test");
        assertTrue("Test".equals(data.getSub_attributes()));
        data.setVersion("Test");
        assertTrue("Test".equals(data.getVersion()));
    }

    @Test
    public void testMSAttributeDictionary(){
        MicroServiceAttribute data = new MicroServiceAttribute();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setValue("Test");
        assertTrue("Test".equals(data.getValue()));
        data.setModelName("Test");
        assertTrue("Test".equals(data.getModelName()));
    }

    @Test
    public void testPolicyScopeService(){
        PolicyScopeService data = new PolicyScopeService();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testPolicyScopeResource(){
        PolicyScopeResource data = new PolicyScopeResource();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testPolicyScopeType(){
        PolicyScopeType data = new PolicyScopeType();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testPolicyScopeClosedLoop(){
        PolicyScopeClosedLoop data = new PolicyScopeClosedLoop();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    @Test
    public void testPolicyScopeGroupPolicyScopeList(){
        GroupPolicyScopeList data = new GroupPolicyScopeList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setGroupName("Test");
        assertTrue("Test".equals(data.getGroupName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setGroupList("Test");
        assertTrue("Test".equals(data.getGroupList()));
    }

    @Test
    public void testClosedLoops(){
        ClosedLoops data = new ClosedLoops();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setClosedLoopControlName("Test");
        assertTrue("Test".equals(data.getClosedLoopControlName()));
        data.setAlarmConditions("Test");
        assertTrue("Test".equals(data.getAlarmConditions()));
        data.setYaml("Test");
        assertTrue("Test".equals(data.getYaml()));
    }

    @Test
    public void testVMType(){
        VMType data = new VMType();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

}
