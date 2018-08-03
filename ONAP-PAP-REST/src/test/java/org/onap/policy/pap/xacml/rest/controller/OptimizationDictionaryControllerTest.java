/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DCAEuuid;
import org.onap.policy.rest.jpa.MicroServiceLocation;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.OptimizationModels;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;


public class OptimizationDictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(OptimizationDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private OptimizationDictionaryController controller = null;
     BufferedReader br = null;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("testUserId");
        userInfo.setUserName("John");
        when(commonClassDao.getEntityItem(UserInfo.class, "userLoginId", "testing")).thenReturn(userInfo);
        
        OptimizationModels optimziationModels = new OptimizationModels();
        
        doNothing().when(commonClassDao).delete(optimziationModels);

        OptimizationDictionaryController.setCommonClassDao(commonClassDao);	

        controller = new OptimizationDictionaryController();
       
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        
        jsonString = "{\"optimizationModelsDictionaryData\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
                + " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
                + " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
                + " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
                + " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
                + "	\"policyDescription\": \"testing input\", \"onapName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
                + "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
                + "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
    
        br = new BufferedReader(new StringReader(jsonString));
        //--- mock the getReader() call
        when(request.getReader()).thenReturn(br);   
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);        
        logger.info("setUp: exit");
    }

    @Test
    public void testGetOptimizationModelsDictionaryEntityData() {
        logger.info("testGetOptimizationModelsDictionaryEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        String modelJson = "{\"optimizationModelsDictionaryData\":[\"modelName\"]}";

        BufferedReader br = new BufferedReader(new StringReader(modelJson));
        request = mock(HttpServletRequest.class);

        try {
            // mock the getReader() call
            when(request.getReader()).thenReturn(br);
            controller.getOptimizationModelsDictionaryEntityData(response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("optimizationModelsDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testGetOptimizationModelsDictionaryEntityData: exit");
    }

    @Test
    public void testSaveOptimizationModelsDictionary() {
        logger.info("testSaveOptimizationModelsDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);

        try {
            // mock the getReader() call
            when(request.getReader()).thenReturn(br);
            controller.saveOptimizationModelsDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("optimizationModelsDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testSaveOptimizationModelsDictionary: exit");
    }

    @Test
    public void testRemoveOptimizationModelsDictionary() {
        logger.info("testRemoveOptimizationModelsDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);

        try {
            // mock the getReader() call
            jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
                    + " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
                    + " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
                    + " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
                    + " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
                    + "	\"policyDescription\": \"testing input\", \"onapName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
                    + "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
                    + "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";

            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.removeOptimizationModelsDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("optimizationModelsDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testRemoveOptimizationModelsDictionary: exit");
    }

}
