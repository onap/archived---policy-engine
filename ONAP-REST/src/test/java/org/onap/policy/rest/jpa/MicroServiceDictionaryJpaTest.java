/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * The Class MicroServiceDictionaryJpaTest.
 */
public class MicroServiceDictionaryJpaTest {

    private static Logger logger = FlexLogger.getLogger(MicroServiceDictionaryJpaTest.class);
    private UserInfo userInfo;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");
        logger.info("setUp: exit");
    }

    /**
     * Test DCAE users.
     */
    @Test
    public void testDcaeUsers() {
        DcaeUsers data = new DcaeUsers();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescriptionValue("Test");
        assertTrue("Test".equals(data.getDescriptionValue()));
    }

    /**
     * Test DCA euuid.
     */
    @Test
    public void testDcaeUuid() {
        DcaeUuid data = new DcaeUuid();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test MS config name.
     */
    @Test
    public void testMsConfigName() {
        MicroServiceConfigName data = new MicroServiceConfigName();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test MS config location.
     */
    @Test
    public void testMsConfigLocation() {
        MicroServiceLocation data = new MicroServiceLocation();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test MS models.
     */
    @Test
    public void testMsModels() {
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
        data.setRefAttributes("Test");
        assertTrue("Test".equals(data.getRefAttributes()));
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setSubAttributes("Test");
        assertTrue("Test".equals(data.getSubAttributes()));
        data.setVersion("Test");
        assertTrue("Test".equals(data.getVersion()));
        assertFalse(data.isDecisionModel());
    }

    /**
     * Test MS attribute dictionary.
     */
    @Test
    public void testMsAttributeDictionary() {
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

    /**
     * Test policy scope service.
     */
    @Test
    public void testPolicyScopeService() {
        PolicyScopeService data = new PolicyScopeService();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test policy scope resource.
     */
    @Test
    public void testPolicyScopeResource() {
        PolicyScopeResource data = new PolicyScopeResource();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test policy scope type.
     */
    @Test
    public void testPolicyScopeType() {
        PolicyScopeType data = new PolicyScopeType();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test policy scope closed loop.
     */
    @Test
    public void testPolicyScopeClosedLoop() {
        PolicyScopeClosedLoop data = new PolicyScopeClosedLoop();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test policy scope group policy scope list.
     */
    @Test
    public void testPolicyScopeGroupPolicyScopeList() {
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

    /**
     * Test closed loops.
     */
    @Test
    public void testClosedLoops() {
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

    /**
     * Test VM type.
     */
    @Test
    public void testVmType() {
        VmType data = new VmType();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

}
