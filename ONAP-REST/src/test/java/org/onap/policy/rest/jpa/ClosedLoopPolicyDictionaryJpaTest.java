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

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * The Class ClosedLoopPolicyDictionaryJpaTest.
 */
public class ClosedLoopPolicyDictionaryJpaTest {

    private static Logger logger = FlexLogger.getLogger(ClosedLoopPolicyDictionaryJpaTest.class);
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
     * Test VSCL action.
     */
    @Test
    public void testVsclAction() {
        VsclAction data = new VsclAction();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setAction("Test");
        assertTrue("Test".equals(data.getAction()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
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
     * Test VNF type.
     */
    @Test
    public void testVnfType() {
        VnfType data = new VnfType();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
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
     * Test PEP options.
     */
    @Test
    public void testPepOptions() {
        PepOptions data = new PepOptions();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPepName("Test");
        assertTrue("Test".equals(data.getPepName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
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
     * Test varbind dictionary.
     */
    @Test
    public void testVarbindDictionary() {
        VarbindDictionary data = new VarbindDictionary();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setVarbindName("Test");
        assertTrue("Test".equals(data.getVarbindName()));
        data.setVarbindDescription("Test");
        assertTrue("Test".equals(data.getVarbindDescription()));
        data.setVarbindOid("Test");
        assertTrue("Test".equals(data.getVarbindOid()));
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
     * Test closed loop D 2 services.
     */
    @Test
    public void testClosedLoopD2Services() {
        ClosedLoopD2Services data = new ClosedLoopD2Services();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setServiceName("Test");
        assertTrue("Test".equals(data.getServiceName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
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
     * Test closed loop site.
     */
    @Test
    public void testClosedLoopSite() {
        ClosedLoopSite data = new ClosedLoopSite();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setSiteName("Test");
        assertTrue("Test".equals(data.getSiteName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }
}
