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
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;

public class BRMSDictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(BRMSDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private BRMSDictionaryController controller = null;
    private MockHttpServletResponse response = null;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("testUserId");
        userInfo.setUserName("John");
        when(commonClassDao.getEntityItem(UserInfo.class, "userLoginId", "testing")).thenReturn(userInfo);
        List<String>  brms = new ArrayList<String>();
        brms.add("BRMS-Model");
        when(commonClassDao.getDataByColumn(BRMSParamTemplate.class, "name")).thenReturn(brms);
        doNothing().when(commonClassDao).delete(new BRMSParamTemplate());
        doNothing().when(commonClassDao).save(new BRMSParamTemplate());
        controller = new BRMSDictionaryController();
        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @Test
    public void testGetBRMSParamDictionaryByNameEntityData(){
        logger.info("testGetBRMSParamDictionaryByNameEntityData: Entering");
        BRMSDictionaryController.setCommonClassDao(commonClassDao);
        controller.getBRMSParamDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsParamDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }
        logger.info("testGetBRMSParamDictionaryByNameEntityData: exit");
    }

    @Test
    public void testGetBRMSParamDictionaryEntityData() {
        logger.info("testGetBRMSParamDictionaryEntityData: Entering");
        controller.getBRMSParamDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsParamDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }
        logger.info("testGetBRMSParamDictionaryEntityData: exit");
    }

    @Test
    public void testSaveBRMSParamDictionary() {
        logger.info("testSaveBRMSParamDictionary: Entering");
        jsonString = "{\"brmsParamDictionaryData\": {\"ruleName\": \"test\",\"rule\": \"test\"},\"userid\": \"testName\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveBRMSParamDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsParamDictionaryData"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
        logger.info("testSaveBRMSParamDictionary: exit");
    }

    @Test
    public void testRemoveBRMSParamDictionary() {
        logger.info("testRemoveBRMSParamDictionary: Entering");
        jsonString = "{\"data\": {\"ruleName\": \"test\",\"rule\": \"test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeBRMSParamDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsParamDictionaryDatas"));
        } catch (Exception e) {
            fail("Exception: " + e);
        }
        logger.info("testRemoveBRMSParamDictionary: exit");
    }

    @Test
    public void testGetBRMSDependencyDictionaryByNameEntityData(){
        logger.info("testGetBRMSDependencyDictionaryByNameEntityData: Entering");
        BRMSDictionaryController.setCommonClassDao(commonClassDao);
        controller.getBRMSDependencyDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsDependencyDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }
        logger.info("testGetBRMSDependencyDictionaryByNameEntityData: exit");
    }

    @Test
    public void testGetBRMSDependencyDictionaryEntityData(){
        logger.info("testGetBRMSDependencyDictionaryEntityData: Entering");
        BRMSDictionaryController.setCommonClassDao(commonClassDao);
        controller.getBRMSDependencyDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsDependencyDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }

        logger.info("testGetBRMSDependencyDictionaryEntityData: exit");
    }

    @Test
    public void testSaveBRMSDependencyDictionary() {
        logger.info("testSaveBRMSDependencyDictionary: Entering");
            jsonString = "{\"brmsDependencyDictionaryData\": {\"ruleName\": \"test\",\"rule\": \"test\"},\"userid\": \"testName\"}";
            try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
                when(request.getReader()).thenReturn(br);
                controller.saveBRMSDependencyDictionary(request, response);
                logger.info("response.getContentAsString(): " + response.getContentAsString());
                assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsDependencyDictionaryData"));
            } catch (Exception e) {
            fail("Exception: " + e);
        }
        logger.info("testSaveBRMSDependencyDictionary: exit");
    }

    @Test
    public void testRemoveBRMSDependencyDictionary() {
        logger.info("testRemoveBRMSDependencyDictionary: Entering");
        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        jsonString = "{\"data\": {\"ruleName\": \"test\",\"rule\": \"test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeBRMSDependencyDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsDependencyDictionaryDatas"));
        } catch (Exception e) {
            fail("Exception: " + e);
        }
        logger.info("testRemoveBRMSDependencyDictionary: exit");
    }

    @Test
    public void testGetBRMSControllerDictionaryByNameEntityData(){
        logger.info("testGetBRMSControllerDictionaryByNameEntityData: Entering");
        MockHttpServletResponse response =  new MockHttpServletResponse();
        BRMSDictionaryController.setCommonClassDao(commonClassDao);
        controller.getBRMSControllerDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsControllerDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }
        logger.info("testGetBRMSControllerDictionaryByNameEntityData: exit");
    }

    @Test
    public void testGetBRMSControllerDictionaryEntityData(){
        logger.info("testGetBRMSControllerDictionaryEntityData: Entering");
        MockHttpServletResponse response =  new MockHttpServletResponse();
        BRMSDictionaryController.setCommonClassDao(commonClassDao);
        controller.getBRMSControllerDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsControllerDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }
        logger.info("testGetBRMSControllerDictionaryEntityData: exit");
    }

    @Test
    public void testSaveBRMSControllerDictionary() {
        logger.info("testSaveBRMSControllerDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        jsonString = "{\"brmsControllerDictionaryData\": {\"ruleName\": \"test\",\"rule\": \"test\"},\"userid\": \"testName\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveBRMSControllerDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsControllerDictionaryData"));
        } catch (Exception e) {
            fail("Exception: " + e);
        }
        logger.info("testSaveBRMSControllerDictionary: exit");
    }

    @Test
    public void testRemoveBRMSControllerDictionary() {
        logger.info("testRemoveBRMSControllerDictionary: Entering");
        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        jsonString = "{\"data\": {\"ruleName\": \"test\",\"rule\": \"test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeBRMSControllerDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("brmsControllerDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }
        logger.info("testRemoveBRMSControllerDictionary: exit");
    }
}

