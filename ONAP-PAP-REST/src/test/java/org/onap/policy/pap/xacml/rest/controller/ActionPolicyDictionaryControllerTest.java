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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;

public class ActionPolicyDictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(ActionPolicyDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private ActionPolicyDictionaryController controller = null;
    private MockHttpServletResponse response = null;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
        List<String>  data = new ArrayList<>();
        List<Object>  objectData = new ArrayList<>();
        data.add("Test");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");

        ActionPolicyDict actionData = new ActionPolicyDict();
        actionData.setAttributeName("Test");
        assertTrue("Test".equals(actionData.getAttributeName()));
        actionData.setBody("Test");
        assertTrue("Test".equals(actionData.getBody()));
        actionData.setCreatedDate(new Date());
        assertTrue(actionData.getCreatedDate()!= null);
        actionData.setModifiedDate(new Date());
        assertTrue(actionData.getModifiedDate()!= null);
        actionData.setHeader("Test");
        assertTrue("Test".equals(actionData.getHeader()));
        actionData.setMethod("POST");
        assertTrue("POST".equals(actionData.getMethod()));
        actionData.setType("Test");
        assertTrue("Test".equals(actionData.getType()));
        actionData.setUrl("http://test.com");
        assertTrue("http://test.com".equals(actionData.getUrl()));
        actionData.setUserCreatedBy(userInfo);
        assertTrue(actionData.getUserCreatedBy()!= null);
        actionData.setUserModifiedBy(userInfo);
        assertTrue(actionData.getUserModifiedBy()!= null);

        objectData.add(actionData);
        when(commonClassDao.getDataByColumn(ActionPolicyDict.class, "attributeName")).thenReturn(data);
        when(commonClassDao.getData(ActionPolicyDict.class)).thenReturn(objectData);
        doNothing().when(commonClassDao).delete(new ActionPolicyDict());
        doNothing().when(commonClassDao).save(new ActionPolicyDict());
        controller = new ActionPolicyDictionaryController();
        controller.setCommonClassDao(commonClassDao);
        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @Test
    public void testGetActionEntitybyName(){
        controller.getActionEntitybyName(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionPolicyDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetActionPolicyDictionaryEntityData(){
        controller.getActionPolicyDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionPolicyDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testSaveActionPolicyDictionary(){
        jsonString = "{\"actionPolicyDictionaryData\":{\"attributeName\":\"Test\",\"body\":\"{}\",\"description\":\"test\",\"headers\":[{\"$$hashKey\":\"object:548\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"method\":\"GET\",\"type\":\"REST\",\"url\":\"http://test.com\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveActionPolicyDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionPolicyDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateActionPolicyDictionary(){
        jsonString = "{\"actionPolicyDictionaryData\":{\"id\":1,\"attributeName\":\"Test\",\"body\":\"{}\",\"description\":\"test\",\"headers\":[{\"$$hashKey\":\"object:548\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"method\":\"GET\",\"type\":\"REST\",\"url\":\"http://test.com\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveActionPolicyDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionPolicyDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveActionPolicyDictionary(){
        jsonString = "{\"data\":{\"$$hashKey\":\"uiGrid-003S\",\"attributeName\":\"Test\",\"body\":\"{}\",\"createdDate\":1518195117000,\"description\":\"test\",\"header\":\"test=12\",\"id\":1,\"method\":\"GET\",\"modifiedDate\":1518195489000,\"type\":\"REST\",\"url\":\"http://test.com\",\"userCreatedBy\":{\"userLoginId\":\"demo\",\"userName\":\"Demo\"},\"userModifiedBy\":{\"userLoginId\":\"demo\",\"userName\":\"Demo\"}}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeActionPolicyDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionPolicyDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }
}
