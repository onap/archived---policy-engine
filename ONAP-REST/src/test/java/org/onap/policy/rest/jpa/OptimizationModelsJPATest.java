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

public class OptimizationModelsJPATest {

    private static Logger logger = FlexLogger.getLogger(OptimizationModelsJPATest.class);
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
    public void testMSModels(){
        OptimizationModels data = new OptimizationModels();
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
        data.setRefattributes("Test");
        assertTrue("Test".equals(data.getRefattributes()));
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!=null);
        data.setSubattributes("Test");
        assertTrue("Test".equals(data.getSubattributes()));
        data.setVersion("Test");
        assertTrue("Test".equals(data.getVersion()));
    }


}
