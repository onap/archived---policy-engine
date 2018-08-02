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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.RiskType;
import org.onap.policy.rest.jpa.SafePolicyWarning;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SafePolicyController {

    private static CommonClassDao commonClassDao;
    private static String duplicateResponseString = "Duplicate";
    private static String operation = "operation";
    private static String riskTypeDatas = "riskTypeDictionaryDatas";
    private static String safePolicyWarningDatas = "safePolicyWarningDatas";

    @Autowired
    public SafePolicyController(CommonClassDao commonClassDao){
        SafePolicyController.commonClassDao = commonClassDao;
    }

    public void setCommonClassDao(CommonClassDao commonClassDao){
        SafePolicyController.commonClassDao = commonClassDao;
    }

    public SafePolicyController(){
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance(){
        return DictionaryUtils.getDictionaryUtils();
    }

    @RequestMapping(value = { "/get_RiskTypeDataByName" }, method = {RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getRiskTypeDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, riskTypeDatas, "name", RiskType.class);
    }

    @RequestMapping(value = { "/get_RiskTypeData" }, method = {RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getRiskTypeDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, riskTypeDatas, RiskType.class);
    }

    @RequestMapping(value = { "/sp_dictionary/save_riskType" }, method = {RequestMethod.POST })
    public ModelAndView saveRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            RiskType riskTypeData;
            String userId = null;
            if (fromAPI) {
                riskTypeData = mapper.readValue(root.get("dictionaryFields").toString(), RiskType.class);
                userId = "API";
            } else {
                riskTypeData = mapper.readValue(root.get("riskTypeDictionaryData").toString(), RiskType.class);
                userId = root.get("userid").textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(riskTypeData.getRiskName(), "name", RiskType.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                RiskType data = (RiskType) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    riskTypeData.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != riskTypeData.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                riskTypeData.setUserModifiedBy(userInfo);
                if(riskTypeData.getId() == 0){
                    riskTypeData.setUserCreatedBy(userInfo);
                    commonClassDao.save(riskTypeData);
                }else{
                    riskTypeData.setModifiedDate(new Date());
                    commonClassDao.update(riskTypeData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(RiskType.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, riskTypeDatas, responseString);
            }
        }catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value = { "/sp_dictionary/remove_riskType" }, method = {RequestMethod.POST })
    public void removeRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, riskTypeDatas, RiskType.class);
    }

    @RequestMapping(value = { "/get_SafePolicyWarningDataByName" }, method = {RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getSafePolicyWarningEntityDataByName(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, safePolicyWarningDatas, "name", SafePolicyWarning.class);
    }

    @RequestMapping(value = { "/get_SafePolicyWarningData" }, method = {RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getSafePolicyWarningeEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, safePolicyWarningDatas, SafePolicyWarning.class);
    }

    @RequestMapping(value = { "/sp_dictionary/save_safePolicyWarning" }, method = {RequestMethod.POST })
    public ModelAndView saveSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            SafePolicyWarning safePolicyWarning;
            if (fromAPI) {
                safePolicyWarning = mapper.readValue(root.get("dictionaryFields").toString(), SafePolicyWarning.class);
            } else {
                safePolicyWarning = mapper.readValue(root.get("safePolicyWarningData").toString(), SafePolicyWarning.class);
            }

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(safePolicyWarning.getName(), "name", SafePolicyWarning.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                SafePolicyWarning data = (SafePolicyWarning) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    safePolicyWarning.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != safePolicyWarning.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(safePolicyWarning.getId() == 0){
                    commonClassDao.save(safePolicyWarning);
                }else{
                    commonClassDao.update(safePolicyWarning);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(SafePolicyWarning.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, safePolicyWarningDatas, responseString);
            }
        }catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value = { "/sp_dictionary/remove_SafePolicyWarning" }, method = {RequestMethod.POST })
    public void removeSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, safePolicyWarningDatas, SafePolicyWarning.class);
    }

}
