/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.springframework.mock.web.MockHttpServletResponse;

public class DecisionPolicyDictionaryControllerTest {
    private static Logger logger = FlexLogger.getLogger(DecisionPolicyDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private DecisionPolicyDictionaryController controller = null;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        
        jsonString = "{\"attributeDictionaryDatas\": {\"error\": \"\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
                + " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
                + " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
                + " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
                + " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
                + "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
                + "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
                + "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";

        BufferedReader br = new BufferedReader(new StringReader(jsonString));
        
        //--- mock the getReader() call
        when(request.getReader()).thenReturn(br);   
        
        controller = new DecisionPolicyDictionaryController(commonClassDao);
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetSettingsDictionaryByNameEntityData() {
        logger.info("testGetSettingsDictionaryByNameEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getSettingsDictionaryByNameEntityData(request, response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("settingsDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testGetSettingsDictionaryByNameEntityData: exit");

    }

    @Test
    public void testGetSettingsDictionaryEntityData() {
        logger.info("testGetSettingsDictionaryEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getSettingsDictionaryEntityData(response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("settingsDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testGetSettingsDictionaryEntityData: exit");

    }

    @Test
    public void testSaveSettingsDictionary() {
        logger.info("testSaveSettingsDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        
        try {
            // mock the getReader() call
            jsonString = "{\"settingsDictionaryData\":{\"xacmlId\":\"testMMRestAPI1\",\"datatypeBean\":{\"shortName\":\"string\"},\"description\":\"test\",\"priority\":\"High\"}, \"userid\":\"test\"}";

            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.saveSettingsDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("settingsDictionaryDatas"));

        } catch (Exception e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testSaveSettingsDictionary: exit");

    }

    @Test
    public void testRemoveSettingsDictionary() {
        logger.info("testRemoveSettingsDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);

        try {
            // mock the getReader() call
            jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
                    + " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
                    + " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
                    + " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
                    + " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
                    + "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
                    + "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
                    + "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.removeSettingsDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("settingsDictionaryDatas"));

        } catch (Exception e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testRemoveSettingsDictionary: exit");

    }

    @Test
    public void testGetRainyDayDictionaryByNameEntityData() {
        logger.info("testGetRainyDayDictionaryByNameEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getRainyDayDictionaryByNameEntityData(request, response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("rainyDayDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testGetRainyDayDictionaryByNameEntityData: exit");

    }

    @Test
    public void testGetRainyDayDictionaryEntityData() {
        logger.info("testGetRainyDayDictionaryEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getRainyDayDictionaryEntityData(response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("rainyDayDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testGetRainyDayDictionaryEntityData: exit");

    }

    @Test
    public void testSaveRainyDayDictionary() {
        logger.info("testSaveRainyDayDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        
        try {
            // mock the getReader() call
            jsonString = "{\"rainyDayDictionaryData\":{\"bbid\":\"BB2\",\"workstep\":\"1\",\"userDataTypeValues\":[{\"$$hashKey\":\"object:233\",\"treatment\":\"test1\"},{\"$$hashKey\":\"object:239\",\"treatment\":\"test2\"}]},\"userid\":\"mm117s\"}";

            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.saveRainyDayDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("rainyDayDictionaryDatas"));

        } catch (Exception e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testSaveRainyDayDictionary: exit");

    }

    @Test
    public void testRemoveRainyDayDictionary() {
        logger.info("testRemoveRainyDayDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);

        try {
            // mock the getReader() call
            jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
                    + " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
                    + " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
                    + " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
                    + " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
                    + "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
                    + "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
                    + "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.removeRainyDayDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("rainyDayDictionaryDatas"));

        } catch (Exception e) {
            logger.error("Exception Occured"+e);
            fail("Exception: " + e);
        }

        logger.info("testRemoveRainyDayDictionary: exit");

    }

}