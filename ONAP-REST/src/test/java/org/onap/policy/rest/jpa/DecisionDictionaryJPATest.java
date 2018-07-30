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

public class DecisionDictionaryJPATest {

    private static Logger logger = FlexLogger.getLogger(DecisionDictionaryJPATest.class);
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
    public void testDecisionSettings(){
        DecisionSettings data = new DecisionSettings();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.preUpdate();
        data.prePersist();
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));
        data.setDatatypeBean(new Datatype());
        assertTrue(data.getDatatypeBean()!=null);
        data.setIssuer("Test");
        assertTrue("Test".equals(data.getIssuer()));
        data.setMustBePresent(true);
        assertTrue(data.isMustBePresent());
        data.setPriority("Test");
        assertTrue("Test".equals(data.getPriority()));
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
    public void testRainyDayTreatments(){
        RainyDayTreatments data = new RainyDayTreatments();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setBbid("Test");
        assertTrue("Test".equals(data.getBbid()));
        data.setWorkstep("Test");
        assertTrue("Test".equals(data.getWorkstep()));
        data.setTreatments("Test");
        assertTrue("Test".equals(data.getTreatments()));
    }

}
