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

public class ClosedLoopPolicyDictionaryJPATest {

    private static Logger logger = FlexLogger.getLogger(ClosedLoopPolicyDictionaryJPATest.class);
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
    public void testVSCLAction(){
        VSCLAction data = new VSCLAction();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setVsclaction("Test");
        assertTrue("Test".equals(data.getVsclaction()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
    }

    @Test
    public void testVNFType(){
        VNFType data = new VNFType();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setVnftype("Test");
        assertTrue("Test".equals(data.getVnftype()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
    }

    @Test
    public void testPEPOptions(){
        PEPOptions data = new PEPOptions();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPepName("Test");
        assertTrue("Test".equals(data.getPepName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
    }

    @Test
    public void testVarbindDictionary(){
        VarbindDictionary data = new VarbindDictionary();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setVarbindName("Test");
        assertTrue("Test".equals(data.getVarbindName()));
        data.setVarbindDescription("Test");
        assertTrue("Test".equals(data.getVarbindDescription()));
        data.setVarbindOID("Test");
        assertTrue("Test".equals(data.getVarbindOID()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
    }

    @Test
    public void testClosedLoopD2Services(){
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
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
    }

    @Test
    public void testClosedLoopSite(){
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
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
    }
}
