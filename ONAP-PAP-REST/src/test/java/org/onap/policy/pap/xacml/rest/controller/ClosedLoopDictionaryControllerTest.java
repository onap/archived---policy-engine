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
import org.onap.policy.rest.jpa.ClosedLoopD2Services;
import org.onap.policy.rest.jpa.ClosedLoopSite;
import org.onap.policy.rest.jpa.PEPOptions;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.VNFType;
import org.onap.policy.rest.jpa.VSCLAction;
import org.onap.policy.rest.jpa.VarbindDictionary;
import org.springframework.mock.web.MockHttpServletResponse;

public class ClosedLoopDictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(ClosedLoopDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private ClosedLoopDictionaryController controller = null;
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

        doNothing().when(commonClassDao).delete(new VSCLAction());
        doNothing().when(commonClassDao).save(new VSCLAction());

        controller = new ClosedLoopDictionaryController();
        controller.setCommonClassDao(commonClassDao);

        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @Test
    public void testGetVSCLActionDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(VSCLAction.class, "vsclaction")).thenReturn(data);
        controller.getVSCLActionDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vsclActionDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetVSCLActionDictionaryEntityData(){
        when(commonClassDao.getData(VSCLAction.class)).thenReturn(new ArrayList<>());
        controller.getVSCLActionDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vsclActionDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetVNFTypeDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(VNFType.class, "vnftype")).thenReturn(data);
        controller.getVNFTypeDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vnfTypeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetVNFTypeDictionaryEntityData(){
        when(commonClassDao.getData(VNFType.class)).thenReturn(new ArrayList<>());
        controller.getVNFTypeDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vnfTypeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPEPOptionsDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(PEPOptions.class, "pepName")).thenReturn(data);
        controller.getPEPOptionsDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("pepOptionsDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPEPOptionsDictionaryEntityData(){
        when(commonClassDao.getData(PEPOptions.class)).thenReturn(new ArrayList<>());
        controller.getPEPOptionsDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("pepOptionsDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetVarbindDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(VarbindDictionary.class, "varbindName")).thenReturn(data);
        controller.getVarbindDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("varbindDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetVarbindDictionaryEntityData(){
        when(commonClassDao.getData(VarbindDictionary.class)).thenReturn(new ArrayList<>());
        controller.getVarbindDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("varbindDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetClosedLoopServiceDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(ClosedLoopD2Services.class, "serviceName")).thenReturn(data);
        controller.getClosedLoopServiceDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopServiceDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetClosedLoopServiceDictionaryEntityData(){
        when(commonClassDao.getData(ClosedLoopD2Services.class)).thenReturn(new ArrayList<>());
        controller.getClosedLoopServiceDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopServiceDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetClosedLoopSiteDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(ClosedLoopSite.class, "siteName")).thenReturn(data);
        controller.getClosedLoopSiteDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopSiteDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetClosedLoopSiteDictionaryEntityData(){
        when(commonClassDao.getData(ClosedLoopSite.class)).thenReturn(new ArrayList<>());
        controller.getClosedLoopSiteDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopSiteDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testSaveVSCLAction(){
        jsonString = "{\"userid\":\"demo\",\"vsclActionDictionaryData\":{\"description\":\"test\",\"vsclaction\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveVSCLAction(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vsclActionDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateVSCLAction(){
        jsonString = "{\"userid\":\"demo\",\"vsclActionDictionaryData\":{\"id\":1,\"description\":\"test\",\"vsclaction\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveVSCLAction(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vsclActionDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveVSCLAction(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"vsclaction\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeVSCLAction(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vsclActionDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSaveVnfType(){
        jsonString = "{\"userid\":\"demo\",\"vnfTypeDictionaryData\":{\"description\":\"test\",\"vnftype\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveVnfType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vnfTypeDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateVnfType(){
        jsonString = "{\"userid\":\"demo\",\"vnfTypeDictionaryData\":{\"id\":1,\"description\":\"test\",\"vnftype\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveVnfType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vnfTypeDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveVnfType(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"vnftype\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeVnfType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("vnfTypeDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSavePEPOptions(){
        jsonString = "{\"pepOptionsDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:257\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"description\":\"test\",\"pepName\":\"Test\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePEPOptions(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("pepOptionsDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdatePEPOptions(){
        jsonString = "{\"pepOptionsDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:257\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"description\":\"test\",\"pepName\":\"Test\",\"id\":1},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePEPOptions(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("pepOptionsDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemovePEPOptions(){
        jsonString = "{\"data\":{\"attributes\":[{\"$$hashKey\":\"object:257\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"description\":\"test\",\"pepName\":\"Test\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removePEPOptions(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("pepOptionsDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSaveServiceType(){
        jsonString = "{\"userid\":\"demo\",\"closedLoopServiceDictionaryData\":{\"description\":\"test\",\"serviceName\":\"Test\",\"id\":1}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveServiceType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopServiceDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateServiceType(){
        jsonString = "{\"userid\":\"demo\",\"closedLoopServiceDictionaryData\":{\"id\":1,\"description\":\"test\",\"serviceName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveServiceType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopServiceDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveServiceType(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"serviceName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeServiceType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopServiceDictionaryData"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSaveSiteType(){
        jsonString = "{\"userid\":\"demo\",\"closedLoopSiteDictionaryData\":{\"description\":\"test\",\"siteName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveSiteType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopSiteDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateSiteType(){
        jsonString = "{\"userid\":\"demo\",\"closedLoopSiteDictionaryData\":{\"id\":1,\"description\":\"test\",\"siteName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveSiteType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopSiteDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveSiteType(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"siteName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeSiteType(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("closedLoopSiteDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSaveVarbind(){
        jsonString = "{\"userid\":\"demo\",\"varbindDictionaryData\":{\"description\":\"test\",\"varbindName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveVarbind(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("varbindDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateVarbind(){
        jsonString = "{\"userid\":\"demo\",\"varbindDictionaryData\":{\"id\":1,\"description\":\"test\",\"varbindName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveVarbind(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("varbindDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveVarbind(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"varbindName\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeVarbind(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("varbindDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }
}
