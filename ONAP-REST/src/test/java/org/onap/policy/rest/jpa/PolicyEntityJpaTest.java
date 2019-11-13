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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * The Class PolicyEntityJpaTest.
 */
public class PolicyEntityJpaTest {

    private static Logger logger = FlexLogger.getLogger(PolicyEntityJpaTest.class);
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
     * Test policy group entity.
     */
    @Test
    public void testPolicyGroupEntity() {
        PolicyGroupEntity data = new PolicyGroupEntity();
        data.setGroupKey(1);
        assertTrue(1 == data.getGroupKey());
        data.setPolicyid(1);
        assertTrue(1 == data.getPolicyid());
    }

    /**
     * Test policy DB dao entity.
     */
    @Test
    public void testPolicyDbDaoEntity() {
        PolicyDbDaoEntity data = new PolicyDbDaoEntity();
        data.prePersist();
        data.preUpdate();
        data.setPolicyDbDaoUrl("Test");
        assertTrue("Test".equals(data.getPolicyDbDaoUrl()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
        data.setUsername("Test");
        assertTrue("Test".equals(data.getUsername()));
        data.setPassword("Test");
        assertTrue("Test".equals(data.getPassword()));
    }

    /**
     * Test database lock entity.
     */
    @Test
    public void testDatabaseLockEntity() {
        DatabaseLockEntity data = new DatabaseLockEntity();
        data.setKey(1);
        assertTrue(1 == data.getKey());
    }

    /**
     * Test policy entity.
     */
    @Test
    public void testPolicyEntity() {
        PolicyEntity data = new PolicyEntity();
        data.prePersist();
        data.preUpdate();
        data.setPolicyName("Test");
        assertTrue("Test".equals(data.getPolicyName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
        data.setPolicyData("Test");
        assertTrue("Test".equals(data.getPolicyData()));
        data.setConfigurationData(new ConfigurationDataEntity());
        assertTrue(data.getConfigurationData() != null);
        data.setActionBodyEntity(new ActionBodyEntity());
        assertTrue(data.getActionBodyEntity() != null);
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

        PolicyEntity entity0 = new PolicyEntity();
        PolicyEntity entity1 = new PolicyEntity();
        assertTrue(entity0.equals(entity0));
        assertTrue(entity0.equals(entity1));
        assertFalse(entity0.equals(null));
        String helloString = "Hello";
        Object helloObject = helloString;
        assertFalse(entity0.equals(helloObject));

        entity0.setPolicyId(1);
        assertFalse(entity0.equals(entity1));
        entity1.setPolicyId(1);
        assertTrue(entity0.equals(entity1));
        entity0.setPolicyId(2);
        assertFalse(entity0.equals(entity1));
        entity1.setPolicyId(2);
        assertTrue(entity0.equals(entity1));

        entity0.setPolicyName("GoToOz");
        assertFalse(entity0.equals(entity1));
        entity1.setPolicyName("GoToOz");
        assertTrue(entity0.equals(entity1));
        entity1.setPolicyName(null);
        assertFalse(entity0.equals(entity1));
        entity0.setPolicyName(null);
        assertTrue(entity0.equals(entity1));
        entity1.setPolicyName("GoToOz");
        assertFalse(entity0.equals(entity1));
        entity0.setPolicyName("GoToOz");
        assertTrue(entity0.equals(entity1));
        entity1.setPolicyName("GoToOzNow");
        assertFalse(entity0.equals(entity1));
        entity0.setPolicyName("GoToOzNow");
        assertTrue(entity0.equals(entity1));

        entity0.setScope("All");
        assertFalse(entity0.equals(entity1));
        entity1.setScope("All");
        assertTrue(entity0.equals(entity1));
        entity1.setScope(null);
        assertFalse(entity0.equals(entity1));
        entity0.setScope(null);
        assertTrue(entity0.equals(entity1));
        entity1.setScope("All");
        assertFalse(entity0.equals(entity1));
        entity0.setScope("All");
        assertTrue(entity0.equals(entity1));
        entity1.setScope("AllIn");
        assertFalse(entity0.equals(entity1));
        entity0.setScope("AllIn");
        assertTrue(entity0.equals(entity1));

        entity0.setVersion(1);
        assertFalse(entity0.equals(entity1));
        entity1.setVersion(1);
        assertTrue(entity0.equals(entity1));

        entity0.setPolicyVersion(1);
        assertFalse(entity0.equals(entity1));
        entity1.setPolicyVersion(1);
        assertTrue(entity0.equals(entity1));
        entity0.setPolicyVersion(2);
        assertFalse(entity0.equals(entity1));
        entity1.setPolicyVersion(2);
        assertTrue(entity0.equals(entity1));

        entity0.setPolicyData("SomeData");
        assertFalse(entity0.equals(entity1));
        entity1.setPolicyData("SomeData");
        assertTrue(entity0.equals(entity1));
        entity1.setPolicyData(null);
        assertFalse(entity0.equals(entity1));
        entity0.setPolicyData(null);
        assertTrue(entity0.equals(entity1));
        entity1.setPolicyData("SomeData");
        assertFalse(entity0.equals(entity1));
        entity0.setPolicyData("SomeData");
        assertTrue(entity0.equals(entity1));
        entity1.setPolicyData("SomeMoreData");
        assertFalse(entity0.equals(entity1));
        entity0.setPolicyData("SomeMoreData");
        assertTrue(entity0.equals(entity1));

        ConfigurationDataEntity cde0 = new ConfigurationDataEntity();
        entity0.setConfigurationDataEntity(cde0);
        assertFalse(entity0.equals(entity1));
        entity1.setConfigurationDataEntity(cde0);
        assertTrue(entity0.equals(entity1));
        entity1.setConfigurationDataEntity(null);
        assertFalse(entity0.equals(entity1));
        entity0.setConfigurationDataEntity(null);
        assertTrue(entity0.equals(entity1));
        ConfigurationDataEntity cde1 = new ConfigurationDataEntity();
        entity1.setConfigurationDataEntity(cde1);
        assertFalse(entity0.equals(entity1));
        entity0.setConfigurationDataEntity(cde1);
        assertTrue(entity0.equals(entity1));

        ActionBodyEntity abe0 = new ActionBodyEntity();
        entity0.setActionBodyEntity(abe0);
        assertFalse(entity0.equals(entity1));
        entity1.setActionBodyEntity(abe0);
        assertTrue(entity0.equals(entity1));
        entity1.setActionBodyEntity(null);
        assertFalse(entity0.equals(entity1));
        entity0.setActionBodyEntity(null);
        assertTrue(entity0.equals(entity1));
        entity1.setActionBodyEntity(abe0);
        assertFalse(entity0.equals(entity1));
        entity0.setActionBodyEntity(abe0);
        assertTrue(entity0.equals(entity1));
        ActionBodyEntity abe1 = new ActionBodyEntity();
        entity1.setActionBodyEntity(abe1);
        assertFalse(entity0.equals(entity1));
        entity0.setActionBodyEntity(abe1);
        assertTrue(entity0.equals(entity1));

        entity0.setDescription("Description");
        assertFalse(entity0.equals(entity1));
        entity1.setDescription("Description");
        assertTrue(entity0.equals(entity1));
        entity1.setDescription(null);
        assertFalse(entity0.equals(entity1));
        entity0.setDescription(null);
        assertTrue(entity0.equals(entity1));
        entity1.setDescription("Description");
        assertFalse(entity0.equals(entity1));
        entity0.setDescription("Description");
        assertTrue(entity0.equals(entity1));
        assertTrue(entity0.equals(entity1));
        entity1.setDescription("Description Extra");
        assertFalse(entity0.equals(entity1));
        entity0.setDescription("Description Extra");
        assertTrue(entity0.equals(entity1));

        entity0.setDeleted(true);
        assertFalse(entity0.equals(entity1));
        entity1.setDeleted(true);
        assertTrue(entity0.equals(entity1));
        entity0.setDeleted(false);
        assertFalse(entity0.equals(entity1));
        entity1.setDeleted(false);
        assertTrue(entity0.equals(entity1));

        entity0.setDeleteReasonCode("NoReason");
        assertFalse(entity0.equals(entity1));
        entity1.setDeleteReasonCode("NoReason");
        assertTrue(entity0.equals(entity1));
        entity1.setDeleteReasonCode(null);
        assertFalse(entity0.equals(entity1));
        entity0.setDeleteReasonCode(null);
        assertTrue(entity0.equals(entity1));
        entity1.setDeleteReasonCode("NoReason");
        assertFalse(entity0.equals(entity1));
        entity0.setDeleteReasonCode("NoReason");
        assertTrue(entity0.equals(entity1));
        assertTrue(entity0.equals(entity1));
        entity1.setDeleteReasonCode("NoOtherReason");
        assertFalse(entity0.equals(entity1));
        entity0.setDeleteReasonCode("NoOtherReason");
        assertTrue(entity0.equals(entity1));

        entity0.setCreatedBy("Dorothy");
        assertFalse(entity0.equals(entity1));
        entity1.setCreatedBy("Dorothy");
        assertTrue(entity0.equals(entity1));
        entity1.setCreatedBy(null);
        assertFalse(entity0.equals(entity1));
        entity0.setCreatedBy(null);
        assertTrue(entity0.equals(entity1));
        entity1.setCreatedBy("Dorothy");
        assertFalse(entity0.equals(entity1));
        entity0.setCreatedBy("Dorothy");
        assertTrue(entity0.equals(entity1));
        entity1.setCreatedBy("Toto");
        assertFalse(entity0.equals(entity1));
        entity0.setCreatedBy("Toto");
        assertTrue(entity0.equals(entity1));

        entity0.setCreatedDate(new Date(12345L));
        assertFalse(entity0.equals(entity1));
        entity1.setCreatedDate(new Date(12345L));
        assertTrue(entity0.equals(entity1));
        entity1.setCreatedDate(null);
        assertFalse(entity0.equals(entity1));
        entity0.setCreatedDate(null);
        assertTrue(entity0.equals(entity1));
        entity1.setCreatedDate(new Date(12345L));
        assertFalse(entity0.equals(entity1));
        entity0.setCreatedDate(new Date(12345L));
        assertTrue(entity0.equals(entity1));
        assertTrue(entity0.equals(entity1));
        entity1.setCreatedDate(new Date(123456L));
        assertFalse(entity0.equals(entity1));
        entity0.setCreatedDate(new Date(123456L));
        assertTrue(entity0.equals(entity1));

        entity0.setModifiedBy("Dorothy");
        assertFalse(entity0.equals(entity1));
        entity1.setModifiedBy("Dorothy");
        assertTrue(entity0.equals(entity1));
        entity1.setModifiedBy(null);
        assertFalse(entity0.equals(entity1));
        entity0.setModifiedBy(null);
        assertTrue(entity0.equals(entity1));
        entity1.setModifiedBy("Dorothy");
        assertFalse(entity0.equals(entity1));
        entity0.setModifiedBy("Dorothy");
        assertTrue(entity0.equals(entity1));
        entity1.setModifiedBy("Toto");
        assertFalse(entity0.equals(entity1));
        entity0.setModifiedBy("Toto");
        assertTrue(entity0.equals(entity1));

        entity0.setModifiedDate(new Date(12345L));
        assertFalse(entity0.equals(entity1));
        entity1.setModifiedDate(new Date(12345L));
        assertTrue(entity0.equals(entity1));
        entity1.setModifiedDate(null);
        assertFalse(entity0.equals(entity1));
        entity0.setModifiedDate(null);
        assertTrue(entity0.equals(entity1));
        entity1.setModifiedDate(new Date(12345L));
        assertFalse(entity0.equals(entity1));
        entity0.setModifiedDate(new Date(12345L));
        assertTrue(entity0.equals(entity1));
        assertTrue(entity0.equals(entity1));
        entity1.setModifiedDate(new Date(123456L));
        assertFalse(entity0.equals(entity1));
        entity0.setModifiedDate(new Date(123456L));
        assertTrue(entity0.equals(entity1));

        entity0.setDeletedBy("Dorothy");
        assertFalse(entity0.equals(entity1));
        entity1.setDeletedBy("Dorothy");
        assertTrue(entity0.equals(entity1));
        entity1.setDeletedBy(null);
        assertFalse(entity0.equals(entity1));
        entity0.setDeletedBy(null);
        assertTrue(entity0.equals(entity1));
        entity1.setDeletedBy("Dorothy");
        assertFalse(entity0.equals(entity1));
        entity0.setDeletedBy("Dorothy");
        assertTrue(entity0.equals(entity1));
        assertTrue(entity0.equals(entity1));
        entity1.setDeletedBy("Toto");
        assertFalse(entity0.equals(entity1));
        entity0.setDeletedBy("Toto");
        assertTrue(entity0.equals(entity1));

        assertNotNull(entity0.hashCode());
        assertNotNull(entity1.hashCode());
    }

    /**
     * Test action body entity.
     */
    @Test
    public void testActionBodyEntity() {
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
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
        data.equals(new ConfigurationDataEntity());
        data.hashCode();
    }

    /**
     * Test configuration data entity.
     */
    @Test
    public void testConfigurationDataEntity() {
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
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
        data.setDeleted(true);
        assertTrue(data.isDeleted());
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
        data.equals(new ConfigurationDataEntity());
        data.hashCode();
    }

    /**
     * Test pdp entity.
     */
    @Test
    public void testPdpEntity() {
        PdpEntity data = new PdpEntity();
        data.prePersist();
        data.preUpdate();
        data.setPdpId("Test");
        assertTrue("Test".equals(data.getPdpId()));
        data.setPdpName("Test");
        assertTrue("Test".equals(data.getPdpName()));
        data.setGroup(new GroupEntity());
        assertTrue(data.getGroup() != null);
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
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);
    }

    /**
     * Test group entity.
     */
    @Test
    public void testGroupEntity() {
        GroupEntity data = new GroupEntity();
        data.prePersist();
        data.preUpdate();
        data.setGroupId("Test");
        assertTrue("Test".equals(data.getGroupId()));
        data.setGroupName("Test");
        assertTrue("Test".equals(data.getGroupName()));
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
        assertTrue(data.getCreatedDate() != null);
        assertTrue(data.getModifiedDate() != null);

        assertNull(data.getPolicies());
        PolicyEntity policy0 = new PolicyEntity();
        policy0.setPolicyName("PolicyName0");
        data.addPolicyToGroup(policy0);
        PolicyEntity policy1 = new PolicyEntity();
        policy1.setPolicyName("PolicyName1");
        assertTrue(data.getPolicies().contains(policy0));
        assertFalse(data.getPolicies().contains(policy1));
        data.addPolicyToGroup(policy1);
        assertTrue(data.getPolicies().contains(policy0));
        assertTrue(data.getPolicies().contains(policy1));
        data.addPolicyToGroup(policy1);
        assertTrue(data.getPolicies().contains(policy0));
        assertTrue(data.getPolicies().contains(policy1));
        data.removePolicyFromGroup(policy0);
        assertFalse(data.getPolicies().contains(policy0));
        assertTrue(data.getPolicies().contains(policy1));
        data.removePolicyFromGroup(policy0);
        assertFalse(data.getPolicies().contains(policy0));
        assertTrue(data.getPolicies().contains(policy1));
        data.removePolicyFromGroup(policy1);
        assertNull(data.getPolicies());
    }
}
