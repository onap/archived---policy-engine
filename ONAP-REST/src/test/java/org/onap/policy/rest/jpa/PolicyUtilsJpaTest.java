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
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * The Class PolicyUtilsJpaTest.
 */
public class PolicyUtilsJpaTest {

    private static Logger logger = FlexLogger.getLogger(PolicyUtilsJpaTest.class);
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
     * Test watch policy notification table.
     */
    @Test
    public void testWatchPolicyNotificationTable() {
        WatchPolicyNotificationTable data = new WatchPolicyNotificationTable();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPolicyName("Test");
        assertTrue("Test".equals(data.getPolicyName()));
        data.setLoginIds("Test");
        assertTrue("Test".equals(data.getLoginIds()));
        data.equals(data);
        data.hashCode();
    }

    /**
     * Test policy roles.
     */
    @Test
    public void testPolicyRoles() {
        PolicyRoles data = new PolicyRoles();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setScope("Test");
        assertTrue("Test".equals(data.getScope()));
        data.setRole("Test");
        assertTrue("Test".equals(data.getRole()));
        data.setLoginId(userInfo);
        assertTrue("Test".equals(data.getLoginId().getUserLoginId()));
    }

    /**
     * Test policy version.
     */
    @Test
    public void testPolicyVersion() {
        PolicyVersion data = new PolicyVersion();
        new PolicyVersion("Test", "Test");
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setActiveVersion(1);
        assertTrue(1 == data.getActiveVersion());
        data.setHigherVersion(1);
        assertTrue(1 == data.getHigherVersion());
        data.setPolicyName("Test");
        assertTrue("Test".equals(data.getPolicyName()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);

        assertNotNull(data.hashCode());

        PolicyVersion version0 = new PolicyVersion();
        PolicyVersion version1 = new PolicyVersion();
        assertTrue(version0.equals(version0));
        assertTrue(version0.equals(version1));
        assertFalse(version0.equals(null));
        String helloString = "Hello";
        Object helloObject = helloString;
        assertFalse(version0.equals(helloObject));

        version0.setId(1);
        assertFalse(version0.equals(version1));
        version1.setId(1);
        assertTrue(version0.equals(version1));
        version0.setActiveVersion(1);
        assertFalse(version0.equals(version1));
        version1.setActiveVersion(1);
        assertTrue(version0.equals(version1));
        version0.setCreatedBy("Dorothy");
        assertFalse(version0.equals(version1));
        version1.setCreatedBy("Dorothy");
        assertTrue(version0.equals(version1));
        version1.setCreatedBy(null);
        assertFalse(version0.equals(version1));
        version0.setCreatedBy(null);
        assertTrue(version0.equals(version1));
        version1.setCreatedBy("Dorothy");
        assertFalse(version0.equals(version1));
        version0.setCreatedBy("Dorothy");
        assertTrue(version0.equals(version1));
        version0.setCreatedDate(new Date(12345L));
        assertFalse(version0.equals(version1));
        version1.setCreatedDate(new Date(12345L));
        assertTrue(version0.equals(version1));
        version1.setCreatedDate(null);
        assertFalse(version0.equals(version1));
        version0.setCreatedDate(null);
        assertTrue(version0.equals(version1));
        version1.setCreatedDate(new Date(12345L));
        assertFalse(version0.equals(version1));
        version0.setCreatedDate(new Date(12345L));
        assertTrue(version0.equals(version1));
        version0.setHigherVersion(1);
        assertFalse(version0.equals(version1));
        version1.setHigherVersion(1);
        assertTrue(version0.equals(version1));
        version0.setModifiedBy("Dorothy");
        assertFalse(version0.equals(version1));
        version1.setModifiedBy("Dorothy");
        assertTrue(version0.equals(version1));
        version1.setModifiedBy(null);
        assertFalse(version0.equals(version1));
        version0.setModifiedBy(null);
        assertTrue(version0.equals(version1));
        version1.setModifiedBy("Dorothy");
        assertFalse(version0.equals(version1));
        version0.setModifiedBy("Dorothy");
        assertTrue(version0.equals(version1));
        version0.setModifiedDate(new Date(12345L));
        assertFalse(version0.equals(version1));
        version1.setModifiedDate(new Date(12345L));
        assertTrue(version0.equals(version1));
        version1.setModifiedDate(null);
        assertFalse(version0.equals(version1));
        version0.setModifiedDate(null);
        assertTrue(version0.equals(version1));
        version1.setModifiedDate(new Date(12345L));
        assertFalse(version0.equals(version1));
        version0.setModifiedDate(new Date(12345L));
        assertTrue(version0.equals(version1));
        version0.setPolicyName("GoToOz");
        assertFalse(version0.equals(version1));
        version1.setPolicyName("GoToOz");
        assertTrue(version0.equals(version1));
        version1.setPolicyName(null);
        assertFalse(version0.equals(version1));
        version0.setPolicyName(null);
        assertTrue(version0.equals(version1));
        version1.setPolicyName("GoToOz");
        assertFalse(version0.equals(version1));
        version0.setPolicyName("GoToOz");
        assertTrue(version0.equals(version1));

        assertNotNull(version0.hashCode());
        assertNotNull(version1.hashCode());
    }

    /**
     * Test system log DB.
     */
    @Test
    public void testSystemLogDb() {
        SystemLogDb data = new SystemLogDb();
        new SystemLogDb(1, "", "", "", "", "");
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
        data.setSystem("Test");
        assertTrue("Test".equals(data.getSystem()));
        data.setRemote("Test");
        assertTrue("Test".equals(data.getRemote()));
        data.setLogtype("Test");
        assertTrue("Test".equals(data.getLogtype()));
        data.setDate(new Date());
        assertTrue(data.getDate() != null);
    }

    /**
     * Test remote catalog values.
     */
    @Test
    public void testRemoteCatalogValues() {
        RemoteCatalogValues data = new RemoteCatalogValues();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setValue("Test");
        assertTrue("Test".equals(data.getValue()));
    }

    /**
     * Test policy score.
     */
    @Test
    public void testPolicyScore() {
        PolicyScore data = new PolicyScore();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPolicyName("Test");
        assertTrue("Test".equals(data.getPolicyName()));
        data.setVersionExtension("Test");
        assertTrue("Test".equals(data.getVersionExtension()));
        data.setScore("Test");
        assertTrue("Test".equals(data.getScore()));
    }

    /**
     * Test policy editor scopes.
     */
    @Test
    public void testPolicyEditorScopes() {
        PolicyEditorScopes data = new PolicyEditorScopes();
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setScopeName("Test");
        assertTrue("Test".equals(data.getScopeName()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }

    /**
     * Test descriptive scope.
     */
    @Test
    public void testDescriptiveScope() {
        DescriptiveScope data = new DescriptiveScope();
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setScopeName("Test");
        assertTrue("Test".equals(data.getScopeName()));
        data.setSearch("Test");
        assertTrue("Test".equals(data.getSearch()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }

    /**
     * Test global role settings.
     */
    @Test
    public void testGlobalRoleSettings() {
        GlobalRoleSettings data = new GlobalRoleSettings();
        new GlobalRoleSettings(true);
        data.setRole("Test");
        assertTrue("Test".equals(data.getRole()));
        data.setLockdown(true);
        assertTrue(data.isLockdown());
    }
}
