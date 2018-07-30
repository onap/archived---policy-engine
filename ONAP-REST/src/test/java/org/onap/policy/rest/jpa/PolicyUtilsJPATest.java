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

public class PolicyUtilsJPATest {

    private static Logger logger = FlexLogger.getLogger(PolicyUtilsJPATest.class);
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
    public void testWatchPolicyNotificationTable(){
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

    @Test
    public void testPolicyRoles(){
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

    @Test
    public void testPolicyVersion(){
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
        assertTrue(data.getCreatedDate()!=null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!=null);
        data.equals(data);
        data.hashCode();
    }

    @Test
    public void testSystemLogDB(){
        SystemLogDB data = new SystemLogDB();
        new SystemLogDB(1, "","","","","");
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
        assertTrue(data.getDate()!=null);
    }

    @Test
    public void testRemoteCatalogValues(){
        RemoteCatalogValues data = new RemoteCatalogValues();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setValue("Test");
        assertTrue("Test".equals(data.getValue()));
    }

    @Test
    public void testPolicyScore(){
        PolicyScore data = new PolicyScore();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPolicyName("Test");
        assertTrue("Test".equals(data.getPolicyName()));
        data.setVersionExtension("Test");
        assertTrue("Test".equals(data.getVersionExtension()));
        data.setPolicyScore("Test");
        assertTrue("Test".equals(data.getPolicyScore()));
    }

    @Test
    public void testPolicyEditorScopes(){
        PolicyEditorScopes data = new PolicyEditorScopes();
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setScopeName("Test");
        assertTrue("Test".equals(data.getScopeName()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate()!=null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!=null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!=null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!=null);
    }

    @Test
    public void testDescriptiveScope(){
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
        assertTrue(data.getCreatedDate()!=null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!=null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!=null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!=null);
    }

    @Test
    public void testGlobalRoleSettings(){
        GlobalRoleSettings data = new GlobalRoleSettings();
        new GlobalRoleSettings(true);
        data.setRole("Test");
        assertTrue("Test".equals(data.getRole()));
        data.setLockdown(true);
        assertTrue(data.isLockdown());
    }
}
