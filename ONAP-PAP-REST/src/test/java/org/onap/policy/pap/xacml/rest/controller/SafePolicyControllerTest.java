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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.RiskType;
import org.onap.policy.rest.jpa.SafePolicyWarning;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;

public class SafePolicyControllerTest {

    private static Logger logger = FlexLogger.getLogger(SafePolicyControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private SafePolicyController controller = null;
    private MockHttpServletResponse response = null;
    private UserInfo userInfo;
    private List<String>  data;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);

        data = new ArrayList<>();
        data.add("Test");

        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");

        doNothing().when(commonClassDao).delete(new RiskType());
        doNothing().when(commonClassDao).save(new RiskType());

        controller = new SafePolicyController();
        controller.setCommonClassDao(commonClassDao);
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
        logger.info("setUp: exit");
    }

    @Test
    public void testGetRiskTypeDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(RiskType.class, "name")).thenReturn(data);
        controller.getRiskTypeDictionaryByNameEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("riskTypeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetRiskTypeDictionaryEntityData(){
        when(commonClassDao.getData(RiskType.class)).thenReturn(new ArrayList<>());
        controller.getRiskTypeDictionaryEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("riskTypeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetSafePolicyWarningEntityDataByName(){
        when(commonClassDao.getDataByColumn(SafePolicyWarning.class, "name")).thenReturn(data);
        controller.getSafePolicyWarningEntityDataByName(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("safePolicyWarningDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetSafePolicyWarningeEntityData(){
        when(commonClassDao.getData(SafePolicyWarning.class)).thenReturn(new ArrayList<>());
        controller.getSafePolicyWarningeEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("safePolicyWarningDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testSaveRiskTypeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"riskTypeDictionaryData\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveRiskTypeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("riskTypeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateRiskTypeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"riskTypeDictionaryData\":{\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveRiskTypeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("riskTypeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveRiskTypeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeRiskTypeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("riskTypeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSaveSafePolicyWarningDictionary(){
        jsonString = "{\"userid\":\"demo\",\"safePolicyWarningData\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveSafePolicyWarningDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("safePolicyWarningDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateSafePolicyWarningDictionary(){
        jsonString = "{\"userid\":\"demo\",\"safePolicyWarningData\":{\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveSafePolicyWarningDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("safePolicyWarningDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveSafePolicyWarningDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeSafePolicyWarningDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("safePolicyWarningDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }
}
