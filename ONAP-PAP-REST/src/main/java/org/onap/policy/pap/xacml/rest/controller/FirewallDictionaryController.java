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
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.netty.handler.ipfilter.CIDR;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.adapters.GridData;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionList;
import org.onap.policy.rest.jpa.AddressGroup;
import org.onap.policy.rest.jpa.FWTag;
import org.onap.policy.rest.jpa.FWTagPicker;
import org.onap.policy.rest.jpa.FirewallDictionaryList;
import org.onap.policy.rest.jpa.GroupServiceList;
import org.onap.policy.rest.jpa.PortList;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.ProtocolList;
import org.onap.policy.rest.jpa.SecurityZone;
import org.onap.policy.rest.jpa.ServiceList;
import org.onap.policy.rest.jpa.TermList;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.Zone;
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
public class FirewallDictionaryController {

    private static final Logger LOGGER  = FlexLogger.getLogger(FirewallDictionaryController.class);

    private static CommonClassDao commonClassDao;
    private static String prefixListName = "prefixListName";
    private static String successMessage = "success";
    private static String operation = "operation";
    private static String errorMsg	= "error";
    private static String dictionaryFields ="dictionaryFields";
    private static String duplicateResponseString = "Duplicate";
    private static String utf8 = "UTF-8";
    private static String applicationJsonContentType = "application / json";
    private static String protocolName = "protocolName";
    private static String groupNameStart = "Group_";
    private static String option = "option";
    private static String zoneName =  "zoneName";
    private static String serviceName = "serviceName";
    private static String termName = "termName";
    private static String userid = "userid";
    private static String tagPickerName = "tagPickerName";
    private static String pfListDatas = "prefixListDictionaryDatas";
    private static String portListDatas = "portListDictionaryDatas";
    private static String protocolListDatas = "protocolListDictionaryDatas";
    private static String addressGroupDatas = "addressGroupDictionaryDatas";
    private static String actionListDatas = "actionListDictionaryDatas";
    private static String serviceGroupDatas = "serviceGroupDictionaryDatas";
    private static String securityZoneDatas = "securityZoneDictionaryDatas";
    private static String serviceListDatas = "serviceListDictionaryDatas";
    private static String zoneDatas = "zoneDictionaryDatas";
    private static String termListDictDatas = "termListDictionaryDatas";
    private static String fwDictListDatas = "fwDictListDictionaryDatas";
    private static String fwTagPickerDatas = "fwTagPickerDictionaryDatas";
    private static String fwTagDatas = "fwTagDictionaryDatas";


    @Autowired
    public FirewallDictionaryController(CommonClassDao commonClassDao){
        FirewallDictionaryController.commonClassDao = commonClassDao;
    }

    public static void setCommonClassDao(CommonClassDao clDao){
        commonClassDao = clDao;
    }

    public FirewallDictionaryController(){
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance(){
        return DictionaryUtils.getDictionaryUtils();
    }

    @RequestMapping(value={"/get_PrefixListDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPrefixListDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, pfListDatas, prefixListName, PrefixList.class);
    }

    @RequestMapping(value={"/get_PrefixListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPrefixListDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, pfListDatas, PrefixList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_prefixList"}, method={RequestMethod.POST})
    public ModelAndView savePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PrefixList prefixList;
            if (fromAPI) {
                prefixList = mapper.readValue(root.get(dictionaryFields).toString(), PrefixList.class);
            } else {
                prefixList = mapper.readValue(root.get("prefixListDictionaryData").toString(), PrefixList.class);
            }

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(prefixList.getPrefixListName(), prefixListName, PrefixList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                PrefixList data = (PrefixList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    prefixList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != prefixList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(prefixList.getId() == 0){
                    commonClassDao.save(prefixList);
                }else{
                    commonClassDao.update(prefixList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PrefixList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, pfListDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_PrefixList"}, method={RequestMethod.POST})
    public void removePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, pfListDatas, PrefixList.class);
    }

    @RequestMapping(value={"/fw_dictionary/validate_prefixList"}, method={RequestMethod.POST})
    public void validatePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PrefixList prefixList = mapper.readValue(root.get("prefixListDictionaryData").toString(), PrefixList.class);
            String responseValidation = successMessage;
            try{
                CIDR.newCIDR(prefixList.getPrefixListValue());
            }catch(UnknownHostException e){
                LOGGER.error(e);
                responseValidation = errorMsg;
            }
            response.setCharacterEncoding(utf8);
            response.setContentType(applicationJsonContentType);
            request.setCharacterEncoding(utf8);

            PrintWriter out = response.getWriter();
            JSONObject j = new JSONObject("{result: " + responseValidation + "}");
            out.write(j.toString());
        }
        catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
    }

    @RequestMapping(value={"/get_PortListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getPortListDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, portListDatas, PortList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_portName"}, method={RequestMethod.POST})
    public ModelAndView savePortListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PortList portList;
            if(fromAPI){
                portList = mapper.readValue(root.get(dictionaryFields).toString(), PortList.class);
            }else{
                portList = mapper.readValue(root.get("portListDictionaryData").toString(), PortList.class);
            }
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(portList.getPortName(), "portName", PortList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                PortList data = (PortList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    portList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != portList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(portList.getId() == 0){
                    commonClassDao.save(portList);
                }else{
                    commonClassDao.update(portList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PortList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, portListDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_PortList"}, method={RequestMethod.POST})
    public void removePortListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, portListDatas, PortList.class);
    }

    @RequestMapping(value={"/get_ProtocolListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getProtocolListDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, protocolListDatas, ProtocolList.class);
    }

    @RequestMapping(value={"/get_ProtocolListDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getProtocolListDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, protocolListDatas, protocolName, ProtocolList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_protocolList"}, method={RequestMethod.POST})
    public ModelAndView saveProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            ProtocolList protocolList;
            if(fromAPI){
                protocolList = mapper.readValue(root.get(dictionaryFields).toString(), ProtocolList.class);
            }else{
                protocolList = mapper.readValue(root.get("protocolListDictionaryData").toString(), ProtocolList.class);
            }
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(protocolList.getProtocolName(), protocolName, ProtocolList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                ProtocolList data = (ProtocolList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    protocolList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != protocolList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(protocolList.getId() == 0){
                    commonClassDao.save(protocolList);
                }else{
                    commonClassDao.update(protocolList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(ProtocolList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, protocolListDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_protocol"}, method={RequestMethod.POST})
    public void removeProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, protocolListDatas, ProtocolList.class);
    }

    @RequestMapping(value={"/get_AddressGroupDictionaryDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getAddressGroupDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, addressGroupDatas, "name", AddressGroup.class);
    }

    @RequestMapping(value={"/get_AddressGroupData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getAddressGroupDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, addressGroupDatas, AddressGroup.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_addressGroup"}, method={RequestMethod.POST})
    public ModelAndView saveAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            AddressGroup addressGroup;
            GridData gridData;
            if(fromAPI){
                addressGroup = mapper.readValue(root.get(dictionaryFields).toString(), AddressGroup.class);
                gridData = mapper.readValue(root.get(dictionaryFields).toString(), GridData.class);
            }else{
                addressGroup = mapper.readValue(root.get("addressGroupDictionaryData").toString(), AddressGroup.class);
                gridData = mapper.readValue(root.get("addressGroupDictionaryData").toString(), GridData.class);
            }
            if(!addressGroup.getGroupName().startsWith(groupNameStart)){
                String groupName = groupNameStart+addressGroup.getGroupName();
                addressGroup.setGroupName(groupName);
            }
            addressGroup.setServiceList(utils.appendKey(gridData.getAttributes(), option, ","));
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(addressGroup.getGroupName(), "name", AddressGroup.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                AddressGroup data = (AddressGroup) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    addressGroup.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != addressGroup.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(addressGroup.getId() == 0){
                    commonClassDao.save(addressGroup);
                }else{
                    commonClassDao.update(addressGroup);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(AddressGroup.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, addressGroupDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_AddressGroup"}, method={RequestMethod.POST})
    public void removeAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, addressGroupDatas, AddressGroup.class);
    }

    @RequestMapping(value={"/get_ActionListDictionaryDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getActionListDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, actionListDatas, "actionName", ActionList.class);
    }

    @RequestMapping(value={"/get_ActionListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getActionListDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, actionListDatas, ActionList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_ActionList"}, method={RequestMethod.POST})
    public ModelAndView saveActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            ActionList actionList;
            if (fromAPI) {
                actionList = mapper.readValue(root.get(dictionaryFields).toString(), ActionList.class);
            } else {
                actionList = mapper.readValue(root.get("actionListDictionaryData").toString(), ActionList.class);
            }
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(actionList.getActionName(), "actionName", ActionList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                ActionList data = (ActionList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    actionList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != actionList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(actionList.getId() == 0){
                    commonClassDao.save(actionList);
                }else{
                    commonClassDao.update(actionList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(ActionList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, actionListDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_ActionList"}, method={RequestMethod.POST})
    public void removeActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, actionListDatas, ActionList.class);
    }

    @RequestMapping(value={"/get_ServiceGroupData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getServiceGroupDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, serviceGroupDatas, GroupServiceList.class);
    }

    @RequestMapping(value={"/get_ServiceGroupDictionaryDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getServiceGroupDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, serviceGroupDatas, "name", GroupServiceList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_serviceGroup"}, method={RequestMethod.POST})
    public ModelAndView saveServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            GroupServiceList groupServiceList;
            GridData gridData;
            if(fromAPI){
                groupServiceList = mapper.readValue(root.get(dictionaryFields).toString(), GroupServiceList.class);
                gridData = mapper.readValue(root.get(dictionaryFields).toString(), GridData.class);
            }else{
                groupServiceList = mapper.readValue(root.get("serviceGroupDictionaryData").toString(), GroupServiceList.class);
                gridData = mapper.readValue(root.get("serviceGroupDictionaryData").toString(), GridData.class);
            }
            if(!groupServiceList.getGroupName().startsWith(groupNameStart)){
                String groupName = groupNameStart+groupServiceList.getGroupName();
                groupServiceList.setGroupName(groupName);
            }
            groupServiceList.setServiceList(utils.appendKey(gridData.getAttributes(), option, ","));
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(groupServiceList.getGroupName(), "name", GroupServiceList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                GroupServiceList data = (GroupServiceList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    groupServiceList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != groupServiceList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(groupServiceList.getId() == 0){
                    commonClassDao.save(groupServiceList);
                }else{
                    commonClassDao.update(groupServiceList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(GroupServiceList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, serviceGroupDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_serviceGroup"}, method={RequestMethod.POST})
    public void removeServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, serviceGroupDatas, GroupServiceList.class);
    }

    @RequestMapping(value={"/get_SecurityZoneDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getSecurityZoneDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, securityZoneDatas, zoneName, SecurityZone.class);
    }

    @RequestMapping(value={"/get_SecurityZoneData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getSecurityZoneDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, securityZoneDatas, SecurityZone.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_securityZone"}, method={RequestMethod.POST})
    public ModelAndView saveSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            SecurityZone securityZone;
            if(fromAPI){
                securityZone = mapper.readValue(root.get(dictionaryFields).toString(), SecurityZone.class);
            }else{
                securityZone = mapper.readValue(root.get("securityZoneDictionaryData").toString(), SecurityZone.class);
            }
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(securityZone.getZoneName(), zoneName, SecurityZone.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                SecurityZone data = (SecurityZone) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    securityZone.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != securityZone.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(securityZone.getId() == 0){
                    commonClassDao.save(securityZone);
                }else{
                    commonClassDao.update(securityZone);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(SecurityZone.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, securityZoneDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_securityZone"}, method={RequestMethod.POST})
    public void removeSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, securityZoneDatas, SecurityZone.class);
    }


    @RequestMapping(value={"/get_ServiceListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getServiceListDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, serviceListDatas, ServiceList.class);
    }

    @RequestMapping(value={"/get_ServiceListDictionaryDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getServiceListDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, serviceListDatas, serviceName, ServiceList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_serviceList"}, method={RequestMethod.POST})
    public ModelAndView saveServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            ServiceList serviceList;
            GridData serviceListGridData;
            if(fromAPI){
                serviceList = mapper.readValue(root.get(dictionaryFields).toString(), ServiceList.class);
                serviceListGridData = mapper.readValue(root.get(dictionaryFields).toString(), GridData.class);
            }else{
                serviceList = mapper.readValue(root.get("serviceListDictionaryData").toString(), ServiceList.class);
                serviceListGridData = mapper.readValue(root.get("serviceListDictionaryData").toString(), GridData.class);
            }
            serviceList.setServiceTransProtocol(utils.appendKey(serviceListGridData.getTransportProtocols(), option, ","));
            serviceList.setServiceAppProtocol(utils.appendKey(serviceListGridData.getAppProtocols(), option, ","));
            serviceList.setServiceType("SERVICE");
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(serviceList.getServiceName(), serviceName, ServiceList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                ServiceList data = (ServiceList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    serviceList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != serviceList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(serviceList.getId() == 0){
                    commonClassDao.save(serviceList);
                }else{
                    commonClassDao.update(serviceList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(ServiceList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, serviceListDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_serviceList"}, method={RequestMethod.POST})
    public void removeServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, serviceListDatas, ServiceList.class);
    }

    @RequestMapping(value={"/get_ZoneData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getZoneDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, zoneDatas, Zone.class);
    }

    @RequestMapping(value={"/get_ZoneDictionaryDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getZoneDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, zoneDatas, zoneName, Zone.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_zoneName"}, method={RequestMethod.POST})
    public ModelAndView saveZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            Zone zone;
            if (fromAPI) {
                zone = mapper.readValue(root.get(dictionaryFields).toString(), Zone.class);
            } else {
                zone = mapper.readValue(root.get("zoneDictionaryData").toString(), Zone.class);
            }
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(zone.getZoneName(), zoneName, Zone.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                Zone data = (Zone) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    zone.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != zone.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(zone.getId() == 0){
                    commonClassDao.save(zone);
                }else{
                    commonClassDao.update(zone);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(Zone.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, zoneDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_zone"}, method={RequestMethod.POST})
    public void removeZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, zoneDatas, Zone.class);
    }

    @RequestMapping(value={"/get_TermListDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getTermListDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, termListDictDatas, termName, TermList.class);
    }

    @RequestMapping(value={"/get_TermListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getTermListDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, termListDictDatas, TermList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_termList"}, method={RequestMethod.POST})
    public ModelAndView saveTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            TermList termList;
            TermListData termListDatas;
            String userId = null;
            if(fromAPI){
                termList = mapper.readValue(root.get(dictionaryFields).toString(), TermList.class);
                termListDatas = mapper.readValue(root.get(dictionaryFields).toString(), TermListData.class);
                userId = "API";
            }else{
                termList = mapper.readValue(root.get("termListDictionaryData").toString(), TermList.class);
                termListDatas = mapper.readValue(root.get("termListDictionaryData").toString(), TermListData.class);
                userId = root.get(userid).textValue();
            }

            termList.setFromZones(utils.appendKey(termListDatas.getFromZoneDatas(), option, ","));
            termList.setToZones(utils.appendKey(termListDatas.getToZoneDatas(), option, ","));
            termList.setSrcIPList(utils.appendKey(termListDatas.getSourceListDatas(), option, ","));
            termList.setDestIPList(utils.appendKey(termListDatas.getDestinationListDatas(), option, ","));
            termList.setSrcPortList(utils.appendKey(termListDatas.getSourceServiceDatas(), option, ","));
            termList.setDestPortList(utils.appendKey(termListDatas.getDestinationServiceDatas(), option, ","));
            termList.setAction(utils.appendKey(termListDatas.getActionListDatas(), option, ","));

            UserInfo userInfo = utils.getUserInfo(userId);
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(termList.getTermName(), termName, TermList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                TermList data = (TermList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    termList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != termList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                termList.setUserModifiedBy(userInfo);
                if(termList.getId() == 0){
                    termList.setUserCreatedBy(userInfo);
                    commonClassDao.save(termList);
                }else{
                    termList.setModifiedDate(new Date());
                    commonClassDao.update(termList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(TermList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, termListDictDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_termList"}, method={RequestMethod.POST})
    public void removeTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, termListDictDatas, TermList.class);
    }

    //ParentList Dictionary Data
    @RequestMapping(value={"/get_FWDictionaryListDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getFWDictListDictionaryEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, fwDictListDatas, "parentItemName", FirewallDictionaryList.class);
    }

    @RequestMapping(value={"/get_FWDictionaryListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getFWDictionaryListEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, fwDictListDatas, FirewallDictionaryList.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_FWDictionaryList"}, method={RequestMethod.POST})
    public ModelAndView saveFWDictionaryList(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            FirewallDictionaryList fwDictList;
            GridData gridData;
            if (fromAPI) {
                fwDictList = mapper.readValue(root.get(dictionaryFields).toString(), FirewallDictionaryList.class);
                gridData = mapper.readValue(root.get(dictionaryFields).toString(), GridData.class);
            } else {
                fwDictList = mapper.readValue(root.get("fwDictListDictionaryData").toString(), FirewallDictionaryList.class);
                gridData = mapper.readValue(root.get("fwDictListDictionaryData").toString(), GridData.class);
            }

            fwDictList.setServiceList(utils.appendKey(gridData.getAttributes(), option, ","));
            fwDictList.setAddressList(utils.appendKey(gridData.getAlAttributes(), option, ","));

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwDictList.getParentItemName(), "parentItemName", FirewallDictionaryList.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                FirewallDictionaryList data = (FirewallDictionaryList) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    fwDictList.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != fwDictList.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(fwDictList.getId() == 0){
                    commonClassDao.save(fwDictList);
                }else{
                    commonClassDao.update(fwDictList);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(FirewallDictionaryList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, fwDictListDatas, responseString);
            }
        }
        catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_FWDictionaryList"}, method={RequestMethod.POST})
    public void removeFWDictionaryList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, fwDictListDatas, FirewallDictionaryList.class);
    }


    @RequestMapping(value={"/get_TagPickerNameByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getTagPickerNameEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, fwTagPickerDatas, tagPickerName, FWTagPicker.class);
    }

    @RequestMapping(value={"/get_TagPickerListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getTagPickerDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, fwTagPickerDatas, FWTagPicker.class);
    }

    @RequestMapping(value={"/fw_dictionary/save_fwTagPicker"}, method={RequestMethod.POST})
    public ModelAndView saveFirewallTagPickerDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            FWTagPicker fwTagPicker;
            TagGridValues data;
            String userId = "";
            if (fromAPI) {
                fwTagPicker = mapper.readValue(root.get(dictionaryFields).toString(), FWTagPicker.class);
                data = mapper.readValue(root.get(dictionaryFields).toString(), TagGridValues.class);
                userId = "API";
            } else {
                fwTagPicker = mapper.readValue(root.get("fwTagPickerDictionaryData").toString(), FWTagPicker.class);
                data = mapper.readValue(root.get("fwTagPickerDictionaryData").toString(), TagGridValues.class);
                userId = root.get(userid).textValue();
            }
            fwTagPicker.setTagValues(utils.appendKeyValue(data.getTags(), "#", ":"));

            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwTagPicker.getTagPickerName(), tagPickerName, FWTagPicker.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                FWTagPicker data1 = (FWTagPicker) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    fwTagPicker.setId(data1.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data1.getId() != fwTagPicker.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                fwTagPicker.setUserModifiedBy(userInfo);
                if(fwTagPicker.getId() == 0){
                    fwTagPicker.setUserCreatedBy(userInfo);
                    commonClassDao.save(fwTagPicker);
                }else{
                    fwTagPicker.setModifiedDate(new Date());
                    commonClassDao.update(fwTagPicker);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(FWTagPicker.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, fwTagPickerDatas, responseString);
            }
        }
        catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_tagPicker"}, method={RequestMethod.POST})
    public void removeFirewallTagPickerDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, fwTagPickerDatas, FWTagPicker.class);
    }

    @RequestMapping(value={"/get_TagListData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getTagDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, fwTagDatas, FWTag.class);
    }

    @RequestMapping(value={"/get_TagNameByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getTagNameEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, fwTagDatas, "fwTagName", FWTag.class);
    }


    @RequestMapping(value={"/fw_dictionary/save_fwTag"}, method={RequestMethod.POST})
    public ModelAndView saveFirewallTagDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            FWTag fwTag;
            TagGridValues tagGridValues;
            String userId="";
            if(fromAPI){
                fwTag = mapper.readValue(root.get(dictionaryFields).toString(), FWTag.class);
                tagGridValues = mapper.readValue(root.get(dictionaryFields).toString(), TagGridValues.class);
                userId = "API";
            }else{
                fwTag = mapper.readValue(root.get("fwTagDictionaryData").toString(), FWTag.class);
                tagGridValues = mapper.readValue(root.get("fwTagDictionaryData").toString(), TagGridValues.class);
                userId = root.get(userid).textValue();
            }
            fwTag.setTagValues(utils.appendKey(tagGridValues.getTags(), "tags", ","));

            UserInfo userInfo = utils.getUserInfo(userId);
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwTag.getFwTagName(), "fwTagName", FWTag.class);
            boolean duplicateflag = false;
            if(!duplicateData.isEmpty()){
                FWTag data = (FWTag) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    fwTag.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != fwTag.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                fwTag.setUserModifiedBy(userInfo);
                if(fwTag.getId() == 0){
                    fwTag.setUserCreatedBy(userInfo);
                    commonClassDao.save(fwTag);
                }else{
                    fwTag.setModifiedDate(new Date());
                    commonClassDao.update(fwTag);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(FWTag.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, fwTagDatas, responseString);
            }
        }
        catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/fw_dictionary/remove_tagList"}, method={RequestMethod.POST})
    public void removeFirewallTagDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, fwTagDatas, FWTag.class);
    }
}

class TagGridValues{
    private List<Object> tags;

    public List<Object> getTags() {
        return tags;
    }

    public void setTags(List<Object> tags) {
        this.tags = tags;
    }
}

class AGGridData{
    private List<Object> attributes;

    public List<Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Object> attributes) {
        this.attributes = attributes;
    }
}

class TermListData{
    private List<Object> fromZoneDatas;
    private List<Object> toZoneDatas;
    private List<Object> sourceListDatas;
    private List<Object> destinationListDatas;
    private List<Object> sourceServiceDatas;
    private List<Object> destinationServiceDatas;
    private List<Object> actionListDatas;
    public List<Object> getFromZoneDatas() {
        return fromZoneDatas;
    }
    public void setFromZoneDatas(List<Object> fromZoneDatas) {
        this.fromZoneDatas = fromZoneDatas;
    }
    public List<Object> getToZoneDatas() {
        return toZoneDatas;
    }
    public void setToZoneDatas(List<Object> toZoneDatas) {
        this.toZoneDatas = toZoneDatas;
    }
    public List<Object> getSourceListDatas() {
        return sourceListDatas;
    }
    public void setSourceListDatas(List<Object> sourceListDatas) {
        this.sourceListDatas = sourceListDatas;
    }
    public List<Object> getDestinationListDatas() {
        return destinationListDatas;
    }
    public void setDestinationListDatas(List<Object> destinationListDatas) {
        this.destinationListDatas = destinationListDatas;
    }
    public List<Object> getSourceServiceDatas() {
        return sourceServiceDatas;
    }
    public void setSourceServiceDatas(List<Object> sourceServiceDatas) {
        this.sourceServiceDatas = sourceServiceDatas;
    }
    public List<Object> getDestinationServiceDatas() {
        return destinationServiceDatas;
    }
    public void setDestinationServiceDatas(List<Object> destinationServiceDatas) {
        this.destinationServiceDatas = destinationServiceDatas;
    }
    public List<Object> getActionListDatas() {
        return actionListDatas;
    }
    public void setActionListDatas(List<Object> actionListDatas) {
        this.actionListDatas = actionListDatas;
    }
}