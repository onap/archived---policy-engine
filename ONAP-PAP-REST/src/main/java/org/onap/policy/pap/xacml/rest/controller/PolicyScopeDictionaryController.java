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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PolicyScopeClosedLoop;
import org.onap.policy.rest.jpa.PolicyScopeResource;
import org.onap.policy.rest.jpa.PolicyScopeService;
import org.onap.policy.rest.jpa.PolicyScopeType;
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
public class PolicyScopeDictionaryController {

    private static final Logger LOGGER  = FlexLogger.getLogger(PolicyScopeDictionaryController.class);

    private static CommonClassDao commonClassDao;
    private static String operation = "operation";
    private static String groupPolicyScopeListData1 = "groupPolicyScopeListData1";
    private static String policyScope= "PolicyScope";
    private static String duplicateResponseString = "Duplicate";
    private static String groupPolicyScopeDatas = "groupPolicyScopeListDatas";
    private static String dictionaryFields = "dictionaryFields";
    private static String psCLDatas = "psClosedLoopDictionaryDatas";
    private static String psServiceDatas = "psServiceDictionaryDatas";
    private static String psTypeDatas = "psTypeDictionaryDatas";
    private static String psResourceDatas = "psResourceDictionaryDatas";

    public PolicyScopeDictionaryController(){
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance(){
        return DictionaryUtils.getDictionaryUtils();
    }

    @Autowired
    public PolicyScopeDictionaryController(CommonClassDao commonClassDao){
        PolicyScopeDictionaryController.commonClassDao = commonClassDao;
    }

    public void setCommonClassDao(CommonClassDao commonClassDao){
        PolicyScopeDictionaryController.commonClassDao = commonClassDao;
    }

    @RequestMapping(value={"/get_GroupPolicyScopeDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getGroupPolicyScopeEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, groupPolicyScopeDatas, "name", GroupPolicyScopeList.class);
    }

    @RequestMapping(value={"/get_GroupPolicyScopeData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getGroupPolicyScopeEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, groupPolicyScopeDatas, GroupPolicyScopeList.class);
    }

    @RequestMapping(value={"/ps_dictionary/save_psGroupPolicyScope"}, method={RequestMethod.POST})
    public ModelAndView savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            GroupPolicyScopeList gpdata = null;
            GroupPolicyScope groupData = null;
            boolean duplicateGroupFlag = false;
            if (fromAPI) {
                gpdata = mapper.readValue(root.get(dictionaryFields).toString(), GroupPolicyScopeList.class);
                try{
                    groupData = mapper.readValue(root.get(groupPolicyScopeListData1).toString(), GroupPolicyScope.class);
                }catch(Exception e){
                    groupData = new GroupPolicyScope();
                    groupData.setResource(root.get(dictionaryFields).get("resource").toString().replace("\"", ""));
                    groupData.setClosedloop(root.get(dictionaryFields).get("closedloop").toString().replace("\"", ""));
                    groupData.setService(root.get(dictionaryFields).get("service").toString().replace("\"", ""));
                    groupData.setType(root.get(dictionaryFields).get("type").toString().replace("\"", ""));
                    LOGGER.error(e);
                }
            } else {
                gpdata = mapper.readValue(root.get("groupPolicyScopeListData").toString(), GroupPolicyScopeList.class);
                try{
                    groupData = mapper.readValue(root.get(groupPolicyScopeListData1).toString(), GroupPolicyScope.class);
                }catch(Exception e){
                    LOGGER.error(e);
                    groupData = new GroupPolicyScope();
                    groupData.setResource(root.get(groupPolicyScopeListData1).get("resource").toString().replace("\"", ""));
                    groupData.setClosedloop(root.get(groupPolicyScopeListData1).get("closedloop").toString().replace("\"", ""));
                    groupData.setService(root.get(groupPolicyScopeListData1).get("service").toString().replace("\"", ""));
                    groupData.setType(root.get(groupPolicyScopeListData1).get("type").toString().replace("\"", ""));
                }
            }
            if(!gpdata.getGroupName().startsWith(policyScope)){
                String name = "PolicyScope_" + gpdata.getGroupName();
                gpdata.setGroupName(name);
            }
            ArrayList<String> valueList = new ArrayList<>();
            String resourceValue = groupData.getResource();
            String typeValue = groupData.getType();
            String serviceValue = groupData.getService();
            String closedLoopValue = groupData.getClosedloop();
            valueList.add("resource=" + resourceValue);
            valueList.add("service=" + serviceValue);
            valueList.add("type=" + typeValue);
            valueList.add("closedLoopControlName="  + closedLoopValue);
            String list = StringUtils.replaceEach(valueList.toString(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
            gpdata.setGroupList(list);

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(gpdata.getGroupName(), "name", GroupPolicyScopeList.class);
            if(duplicateData.isEmpty()){
                duplicateData =  commonClassDao.checkDuplicateEntry(gpdata.getGroupList(), "groupList", GroupPolicyScopeList.class);
                if(duplicateData.isEmpty()){
                    duplicateGroupFlag = true;
                }
            }
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                GroupPolicyScopeList data = (GroupPolicyScopeList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    gpdata.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != gpdata.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag && !duplicateGroupFlag){
                if(gpdata.getId() == 0){
                    commonClassDao.save(gpdata);
                }else{
                    commonClassDao.update(gpdata);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class));
            }else if(duplicateGroupFlag){
                responseString = "DuplicateGroup";
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, groupPolicyScopeDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ps_dictionary/remove_GroupPolicyScope"}, method={RequestMethod.POST})
    public void removePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, groupPolicyScopeDatas, GroupPolicyScopeList.class);
    }

    @RequestMapping(value={"/get_PSClosedLoopDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSClosedLoopEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, psCLDatas, "name", OnapName.class);
    }

    @RequestMapping(value={"/get_PSClosedLoopData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSClosedLoopEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, psCLDatas, PolicyScopeClosedLoop.class);
    }

    @RequestMapping(value={"/ps_dictionary/save_psClosedLoop"}, method={RequestMethod.POST})
    public ModelAndView savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PolicyScopeClosedLoop onapData;
            if(fromAPI){
                onapData = mapper.readValue(root.get(dictionaryFields).toString(), PolicyScopeClosedLoop.class);
            }else{
                onapData = mapper.readValue(root.get("psClosedLoopDictionaryData").toString(), PolicyScopeClosedLoop.class);
            }

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeClosedLoop.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                PolicyScopeClosedLoop data = (PolicyScopeClosedLoop) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    onapData.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != onapData.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(onapData.getId() == 0){
                    commonClassDao.save(onapData);
                }else{
                    commonClassDao.update(onapData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, psCLDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ps_dictionary/remove_PSClosedLoop"}, method={RequestMethod.POST})
    public void removePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, psCLDatas, PolicyScopeClosedLoop.class);
    }

    @RequestMapping(value={"/get_PSServiceDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSServiceEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, psServiceDatas, "name", PolicyScopeService.class);
    }

    @RequestMapping(value={"/get_PSServiceData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSServiceEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, psServiceDatas, PolicyScopeService.class);
    }

    @RequestMapping(value={"/ps_dictionary/save_psService"}, method={RequestMethod.POST})
    public ModelAndView savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PolicyScopeService onapData;
            if (fromAPI) {
                onapData = mapper.readValue(root.get(dictionaryFields).toString(), PolicyScopeService.class);
            } else {
                onapData = mapper.readValue(root.get("psServiceDictionaryData").toString(), PolicyScopeService.class);
            }

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeService.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                PolicyScopeService data = (PolicyScopeService) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    onapData.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != onapData.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(onapData.getId() == 0){
                    commonClassDao.save(onapData);
                }else{
                    commonClassDao.update(onapData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, psServiceDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ps_dictionary/remove_PSService"}, method={RequestMethod.POST})
    public void removePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, psServiceDatas, PolicyScopeService.class);
    }

    @RequestMapping(value={"/get_PSTypeDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSTypeEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, psTypeDatas, "name", PolicyScopeType.class);
    }

    @RequestMapping(value={"/get_PSTypeData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSTypeEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, psTypeDatas, PolicyScopeType.class);
    }

    @RequestMapping(value={"/ps_dictionary/save_psType"}, method={RequestMethod.POST})
    public ModelAndView savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PolicyScopeType onapData;
            if(fromAPI){
                onapData = mapper.readValue(root.get(dictionaryFields).toString(), PolicyScopeType.class);
            }else{
                onapData = mapper.readValue(root.get("psTypeDictionaryData").toString(), PolicyScopeType.class);
            }

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeType.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                PolicyScopeType data = (PolicyScopeType) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    onapData.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != onapData.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(onapData.getId() == 0){
                    commonClassDao.save(onapData);
                }else{
                    commonClassDao.update(onapData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, psTypeDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ps_dictionary/remove_PSType"}, method={RequestMethod.POST})
    public void removePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, psTypeDatas, PolicyScopeType.class);
    }

    @RequestMapping(value={"/get_PSResourceDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSResourceEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, psResourceDatas, "name", PolicyScopeResource.class);
    }

    @RequestMapping(value={"/get_PSResourceData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPSResourceEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, psResourceDatas, PolicyScopeResource.class);
    }

    @RequestMapping(value={"/ps_dictionary/save_psResource"}, method={RequestMethod.POST})
    public ModelAndView savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PolicyScopeResource onapData;
            if (fromAPI) {
                onapData = mapper.readValue(root.get(dictionaryFields).toString(), PolicyScopeResource.class);
            } else {
                onapData = mapper.readValue(root.get("psResourceDictionaryData").toString(), PolicyScopeResource.class);
            }

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeResource.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                PolicyScopeResource data = (PolicyScopeResource) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    onapData.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != onapData.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(onapData.getId() == 0){
                    commonClassDao.save(onapData);
                }else{
                    commonClassDao.update(onapData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, psResourceDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ps_dictionary/remove_PSResource"}, method={RequestMethod.POST})
    public void removePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, psResourceDatas, PolicyScopeResource.class);
    }
}

class GroupPolicyScope{
    String resource;
    String type;
    String service;
    String closedloop;
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public String getClosedloop() {
        return closedloop;
    }
    public void setClosedloop(String closedloop) {
        this.closedloop = closedloop;
    }

}
