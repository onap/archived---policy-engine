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

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class PolicyEntityJPATest {

    private static Logger logger = FlexLogger.getLogger(PolicyEntityJPATest.class);
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
    public void testPolicyGroupEntity(){
        PolicyGroupEntity data = new PolicyGroupEntity();
        data.setGroupKey(1);
        assertTrue(1 == data.getGroupKey());
        data.setPolicyid(1);
        assertTrue(1 == data.getPolicyid());
    }

    @Test
    public void testPolicyDBDaoEntity(){
        PolicyDBDaoEntity data = new PolicyDBDaoEntity();
        data.prePersist();
        data.preUpdate();
        data.setPolicyDBDaoUrl("Test");
        assertTrue("Test".equals(data.getPolicyDBDaoUrl()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
        data.setUsername("Test");
        assertTrue("Test".equals(data.getUsername()));
        data.setPassword("Test");
        assertTrue("Test".equals(data.getPassword()));
    }

    @Test
    public void testDatabaseLockEntity(){
        DatabaseLockEntity data = new DatabaseLockEntity();
        data.setKey(1);
        assertTrue(1 == data.getKey());
    }

    @Test
    public void testPolicyEntity(){
        PolicyEntity data = new PolicyEntity();
        data.prePersist();
        data.preUpdate();
        data.setPolicyName("Test");
        assertTrue("Test".equals(data.getPolicyName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
        data.setPolicyData("Test");
        assertTrue("Test".equals(data.getPolicyData()));
        data.setConfigurationData(new ConfigurationDataEntity());
        assertTrue(data.getConfigurationData()!=null);
        data.setActionBodyEntity(new ActionBodyEntity());
        assertTrue(data.getActionBodyEntity()!=null);
        data.setScope("Test");
        assertTrue("Test".equals(data.getScope()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        data.equals(new PolicyEntity());
        data.hashCode();
    }

    @Test
    public void testActionBodyEntity(){
        ActionBodyEntity data = new ActionBodyEntity();
        data.prePersist();
        data.preUpdate();
        data.setActionBodyName("Test");
        assertTrue("Test".equals(data.getActionBodyName()));
        data.setActionBody("Test");
        assertTrue("Test".equals(data.getActionBody()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setModifiedDate(new Date());
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
        data.equals(new ConfigurationDataEntity());
        data.hashCode();
    }

    @Test
    public void testConfigurationDataEntity(){
        ConfigurationDataEntity data = new ConfigurationDataEntity();
        data.prePersist();
        data.preUpdate();
        data.setConfigurationName("Test");
        assertTrue("Test".equals(data.getConfigurationName()));
        data.setConfigType("Test");
        assertTrue("Test".equals(data.getConfigType()));
        data.setConfigBody("Test");
        assertTrue("Test".equals(data.getConfigBody()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setModifiedDate(new Date());
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
        data.equals(new ConfigurationDataEntity());
        data.hashCode();
    }

    @Test
    public void testPdpEntity(){
        PdpEntity data = new PdpEntity();
        data.prePersist();
        data.preUpdate();
        data.setPdpId("Test");
        assertTrue("Test".equals(data.getPdpId()));
        data.setPdpName("Test");
        assertTrue("Test".equals(data.getPdpName()));
        data.setGroup(new GroupEntity());
        assertTrue(data.getGroup()!=null);
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setJmxPort(1);
        assertTrue(1 == data.getJmxPort());
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
    }

    @Test
    public void testGroupEntity(){
        GroupEntity data = new GroupEntity();
        data.prePersist();
        data.preUpdate();
        data.setGroupId("Test");
        assertTrue("Test".equals(data.getGroupId()));
        data.setGroupName("Test");
        assertTrue("Test".equals(data.getgroupName()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setDefaultGroup(true);
        assertTrue(data.isDefaultGroup());
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        assertTrue(data.getCreatedDate()!=null);
        assertTrue(data.getModifiedDate()!=null);
    }
}
