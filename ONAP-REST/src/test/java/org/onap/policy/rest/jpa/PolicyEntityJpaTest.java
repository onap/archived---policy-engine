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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
        assertEquals(entity0, entity0);
        assertEquals(entity0, entity1);
        assertNotEquals(entity0, null);
        String helloString = "Hello";
        Object helloObject = helloString;
        assertNotEquals(entity0, helloObject);

        entity0.setPolicyId(1);
        assertNotEquals(entity0, entity1);
        entity1.setPolicyId(1);
        assertEquals(entity0, entity1);
        entity0.setPolicyId(2);
        assertNotEquals(entity0, entity1);
        entity1.setPolicyId(2);
        assertEquals(entity0, entity1);

        entity0.setPolicyName("GoToOz");
        assertNotEquals(entity0, entity1);
        entity1.setPolicyName("GoToOz");
        assertEquals(entity0, entity1);
        entity1.setPolicyName(null);
        assertNotEquals(entity0, entity1);
        entity0.setPolicyName(null);
        assertEquals(entity0, entity1);
        entity1.setPolicyName("GoToOz");
        assertNotEquals(entity0, entity1);
        entity0.setPolicyName("GoToOz");
        assertEquals(entity0, entity1);
        entity1.setPolicyName("GoToOzNow");
        assertNotEquals(entity0, entity1);
        entity0.setPolicyName("GoToOzNow");
        assertEquals(entity0, entity1);

        entity0.setScope("All");
        assertNotEquals(entity0, entity1);
        entity1.setScope("All");
        assertEquals(entity0, entity1);
        entity1.setScope(null);
        assertNotEquals(entity0, entity1);
        entity0.setScope(null);
        assertEquals(entity0, entity1);
        entity1.setScope("All");
        assertNotEquals(entity0, entity1);
        entity0.setScope("All");
        assertEquals(entity0, entity1);
        entity1.setScope("AllIn");
        assertNotEquals(entity0, entity1);
        entity0.setScope("AllIn");
        assertEquals(entity0, entity1);

        entity0.setVersion(1);
        assertNotEquals(entity0, entity1);
        entity1.setVersion(1);
        assertEquals(entity0, entity1);

        entity0.setPolicyVersion(1);
        assertNotEquals(entity0, entity1);
        entity1.setPolicyVersion(1);
        assertEquals(entity0, entity1);
        entity0.setPolicyVersion(2);
        assertNotEquals(entity0, entity1);
        entity1.setPolicyVersion(2);
        assertEquals(entity0, entity1);

        entity0.setPolicyData("SomeData");
        assertNotEquals(entity0, entity1);
        entity1.setPolicyData("SomeData");
        assertEquals(entity0, entity1);
        entity1.setPolicyData(null);
        assertNotEquals(entity0, entity1);
        entity0.setPolicyData(null);
        assertEquals(entity0, entity1);
        entity1.setPolicyData("SomeData");
        assertNotEquals(entity0, entity1);
        entity0.setPolicyData("SomeData");
        assertEquals(entity0, entity1);
        entity1.setPolicyData("SomeMoreData");
        assertNotEquals(entity0, entity1);
        entity0.setPolicyData("SomeMoreData");
        assertEquals(entity0, entity1);

        ConfigurationDataEntity cde0 = new ConfigurationDataEntity();
        entity0.setConfigurationDataEntity(cde0);
        assertNotEquals(entity0, entity1);
        entity1.setConfigurationDataEntity(cde0);
        assertEquals(entity0, entity1);
        entity1.setConfigurationDataEntity(null);
        assertNotEquals(entity0, entity1);
        entity0.setConfigurationDataEntity(null);
        assertEquals(entity0, entity1);
        ConfigurationDataEntity cde1 = new ConfigurationDataEntity();
        entity1.setConfigurationDataEntity(cde1);
        assertNotEquals(entity0, entity1);
        entity0.setConfigurationDataEntity(cde1);
        assertEquals(entity0, entity1);

        ActionBodyEntity abe0 = new ActionBodyEntity();
        entity0.setActionBodyEntity(abe0);
        assertNotEquals(entity0, entity1);
        entity1.setActionBodyEntity(abe0);
        assertEquals(entity0, entity1);
        entity1.setActionBodyEntity(null);
        assertNotEquals(entity0, entity1);
        entity0.setActionBodyEntity(null);
        assertEquals(entity0, entity1);
        entity1.setActionBodyEntity(abe0);
        assertNotEquals(entity0, entity1);
        entity0.setActionBodyEntity(abe0);
        assertEquals(entity0, entity1);
        ActionBodyEntity abe1 = new ActionBodyEntity();
        entity1.setActionBodyEntity(abe1);
        assertNotEquals(entity0, entity1);
        entity0.setActionBodyEntity(abe1);
        assertEquals(entity0, entity1);

        entity0.setDescription("Description");
        assertNotEquals(entity0, entity1);
        entity1.setDescription("Description");
        assertEquals(entity0, entity1);
        entity1.setDescription(null);
        assertNotEquals(entity0, entity1);
        entity0.setDescription(null);
        assertEquals(entity0, entity1);
        entity1.setDescription("Description");
        assertNotEquals(entity0, entity1);
        entity0.setDescription("Description");
        assertEquals(entity0, entity1);
        assertEquals(entity0, entity1);
        entity1.setDescription("Description Extra");
        assertNotEquals(entity0, entity1);
        entity0.setDescription("Description Extra");
        assertEquals(entity0, entity1);

        entity0.setDeleted(true);
        assertNotEquals(entity0, entity1);
        entity1.setDeleted(true);
        assertEquals(entity0, entity1);
        entity0.setDeleted(false);
        assertNotEquals(entity0, entity1);
        entity1.setDeleted(false);
        assertEquals(entity0, entity1);

        entity0.setDeleteReasonCode("NoReason");
        assertNotEquals(entity0, entity1);
        entity1.setDeleteReasonCode("NoReason");
        assertEquals(entity0, entity1);
        entity1.setDeleteReasonCode(null);
        assertNotEquals(entity0, entity1);
        entity0.setDeleteReasonCode(null);
        assertEquals(entity0, entity1);
        entity1.setDeleteReasonCode("NoReason");
        assertNotEquals(entity0, entity1);
        entity0.setDeleteReasonCode("NoReason");
        assertEquals(entity0, entity1);
        assertEquals(entity0, entity1);
        entity1.setDeleteReasonCode("NoOtherReason");
        assertNotEquals(entity0, entity1);
        entity0.setDeleteReasonCode("NoOtherReason");
        assertEquals(entity0, entity1);

        entity0.setCreatedBy("Dorothy");
        assertNotEquals(entity0, entity1);
        entity1.setCreatedBy("Dorothy");
        assertEquals(entity0, entity1);
        entity1.setCreatedBy(null);
        assertNotEquals(entity0, entity1);
        entity0.setCreatedBy(null);
        assertEquals(entity0, entity1);
        entity1.setCreatedBy("Dorothy");
        assertNotEquals(entity0, entity1);
        entity0.setCreatedBy("Dorothy");
        assertEquals(entity0, entity1);
        entity1.setCreatedBy("Toto");
        assertNotEquals(entity0, entity1);
        entity0.setCreatedBy("Toto");
        assertEquals(entity0, entity1);

        entity0.setCreatedDate(new Date(12345L));
        assertNotEquals(entity0, entity1);
        entity1.setCreatedDate(new Date(12345L));
        assertEquals(entity0, entity1);
        entity1.setCreatedDate(null);
        assertNotEquals(entity0, entity1);
        entity0.setCreatedDate(null);
        assertEquals(entity0, entity1);
        entity1.setCreatedDate(new Date(12345L));
        assertNotEquals(entity0, entity1);
        entity0.setCreatedDate(new Date(12345L));
        assertEquals(entity0, entity1);
        assertEquals(entity0, entity1);
        entity1.setCreatedDate(new Date(123456L));
        assertNotEquals(entity0, entity1);
        entity0.setCreatedDate(new Date(123456L));
        assertEquals(entity0, entity1);

        entity0.setModifiedBy("Dorothy");
        assertNotEquals(entity0, entity1);
        entity1.setModifiedBy("Dorothy");
        assertEquals(entity0, entity1);
        entity1.setModifiedBy(null);
        assertNotEquals(entity0, entity1);
        entity0.setModifiedBy(null);
        assertEquals(entity0, entity1);
        entity1.setModifiedBy("Dorothy");
        assertNotEquals(entity0, entity1);
        entity0.setModifiedBy("Dorothy");
        assertEquals(entity0, entity1);
        entity1.setModifiedBy("Toto");
        assertNotEquals(entity0, entity1);
        entity0.setModifiedBy("Toto");
        assertEquals(entity0, entity1);

        entity0.setModifiedDate(new Date(12345L));
        assertNotEquals(entity0, entity1);
        entity1.setModifiedDate(new Date(12345L));
        assertEquals(entity0, entity1);
        entity1.setModifiedDate(null);
        assertNotEquals(entity0, entity1);
        entity0.setModifiedDate(null);
        assertEquals(entity0, entity1);
        entity1.setModifiedDate(new Date(12345L));
        assertNotEquals(entity0, entity1);
        entity0.setModifiedDate(new Date(12345L));
        assertEquals(entity0, entity1);
        assertEquals(entity0, entity1);
        entity1.setModifiedDate(new Date(123456L));
        assertNotEquals(entity0, entity1);
        entity0.setModifiedDate(new Date(123456L));
        assertEquals(entity0, entity1);

        entity0.setDeletedBy("Dorothy");
        assertNotEquals(entity0, entity1);
        entity1.setDeletedBy("Dorothy");
        assertEquals(entity0, entity1);
        entity1.setDeletedBy(null);
        assertNotEquals(entity0, entity1);
        entity0.setDeletedBy(null);
        assertEquals(entity0, entity1);
        entity1.setDeletedBy("Dorothy");
        assertNotEquals(entity0, entity1);
        entity0.setDeletedBy("Dorothy");
        assertEquals(entity0, entity1);
        assertEquals(entity0, entity1);
        entity1.setDeletedBy("Toto");
        assertNotEquals(entity0, entity1);
        entity0.setDeletedBy("Toto");
        assertEquals(entity0, entity1);

        assertNotNull(entity0.hashCode());
        assertNotNull(entity1.hashCode());
        assertEquals(entity0.hashCode(), entity0.hashCode());

        entity0.setDeletedBy("Totp");
        assertNotEquals(entity0, entity1);
        assertNotEquals(entity0.hashCode(), entity1.hashCode());
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
        String helloString = "Hello";
        Object helloObject = helloString;
        assertNotEquals(data, helloObject);
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
