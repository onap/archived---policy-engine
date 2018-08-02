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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DCAEuuid;
import org.onap.policy.rest.jpa.MicroServiceAttribute;
import org.onap.policy.rest.jpa.MicroServiceConfigName;
import org.onap.policy.rest.jpa.MicroServiceLocation;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.MicroserviceHeaderdeFaults;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.util.MSAttributeObject;
import org.onap.policy.rest.util.MSModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Controller
public class MicroServiceDictionaryController {
    private static final Logger LOGGER  = FlexLogger.getLogger(MicroServiceDictionaryController.class);

    private static CommonClassDao commonClassDao;

    private static String successMapKey= "successMapKey";
    private static String successMsg = "success";
    private static String operation = "operation";
    private static String getDictionary = "getDictionary";
    private static String errorMsg = "error";
    private static String dictionaryDBQuery = "dictionaryDBQuery";
    private LinkedHashMap<String,MSAttributeObject > classMap;
    private List<String> modelList = new ArrayList<>();
    private static String apiflag = "apiflag";
    private static String dictionaryFields ="dictionaryFields";
    private static String update = "update";
    private static String duplicateResponseString = "Duplicate";
    private static String microServiceModelsDictionaryDatas = "microServiceModelsDictionaryDatas";
    private static String modelName = "modelName";
    private static String microServiceModelsDictionaryData = "microServiceModelsDictionaryData";
    private static String description = "description";
    private static String version = "version";
    private static String classMapData = "classMap";
    private static String dcaeUUIDDatas = "dcaeUUIDDictionaryDatas";
    private static String microServiceConfigNameDatas = "microServiceConfigNameDictionaryDatas";
    private static String microServiceLocationDatas = "microServiceLocationDictionaryDatas";
    private static String microServiceAttributeDatas = "microServiceAttributeDictionaryDatas";
    private static String microServiceHeaderDefaultDatas = "microServiceHeaderDefaultDatas";

    public MicroServiceDictionaryController(){
        super();
    }	

    private DictionaryUtils getDictionaryUtilsInstance(){
        return DictionaryUtils.getDictionaryUtils();
    }
    
    @Autowired
    public MicroServiceDictionaryController(CommonClassDao commonClassDao){
        MicroServiceDictionaryController.commonClassDao = commonClassDao;
    }
    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        MicroServiceDictionaryController.commonClassDao = commonClassDao;
    }

    MSModelUtils utils = new MSModelUtils(XACMLPapServlet.getMsOnapName(), XACMLPapServlet.getMsPolicyName());

    private MicroServiceModels newModel;

    @RequestMapping(value={"/get_DCAEUUIDDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getDCAEUUIDDictionaryByNameEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, dcaeUUIDDatas, "name", DCAEuuid.class);
    }

    @RequestMapping(value={"/get_DCAEUUIDData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getDCAEUUIDDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, dcaeUUIDDatas, DCAEuuid.class);
    }

    @RequestMapping(value={"/ms_dictionary/save_dcaeUUID"}, method={RequestMethod.POST})
    public ModelAndView saveDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            DCAEuuid dCAEuuid;
            if(fromAPI){
                dCAEuuid = mapper.readValue(root.get(dictionaryFields).toString(), DCAEuuid.class);
            }else{
                dCAEuuid = mapper.readValue(root.get("dcaeUUIDDictionaryData").toString(), DCAEuuid.class);
            }
            
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(dCAEuuid.getName(), "name", DCAEuuid.class);
            boolean duplicateflag = false;
            if(duplicateData != null && !duplicateData.isEmpty()){
                DCAEuuid data = (DCAEuuid) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    dCAEuuid.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != dCAEuuid.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(dCAEuuid.getId() == 0){
                    commonClassDao.save(dCAEuuid);
                }else{
                    commonClassDao.update(dCAEuuid);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(DCAEuuid.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, dcaeUUIDDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ms_dictionary/remove_dcaeuuid"}, method={RequestMethod.POST})
    public void removeDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, dcaeUUIDDatas, DCAEuuid.class);
    }

    @RequestMapping(value={"/get_MicroServiceConfigNameDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceConfigNameByNameDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, microServiceConfigNameDatas, "name", MicroServiceConfigName.class);
    }

    @RequestMapping(value={"/get_MicroServiceConfigNameData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceConfigNameDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, microServiceConfigNameDatas, MicroServiceConfigName.class);
    }

    @RequestMapping(value={"/ms_dictionary/save_configName"}, method={RequestMethod.POST})
    public ModelAndView saveMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            MicroServiceConfigName microServiceConfigName;
            if(fromAPI){
                microServiceConfigName = mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceConfigName.class);
            }else{
                microServiceConfigName = mapper.readValue(root.get("microServiceConfigNameDictionaryData").toString(), MicroServiceConfigName.class);
            }
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(microServiceConfigName.getName(), "name", MicroServiceConfigName.class);
            boolean duplicateflag = false;
            if(duplicateData != null && !duplicateData.isEmpty()){
                MicroServiceConfigName data = (MicroServiceConfigName) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    microServiceConfigName.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != microServiceConfigName.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(microServiceConfigName.getId() == 0){
                    commonClassDao.save(microServiceConfigName);
                }else{
                    commonClassDao.update(microServiceConfigName);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceConfigName.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, microServiceConfigNameDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ms_dictionary/remove_msConfigName"}, method={RequestMethod.POST})
    public void removeMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, microServiceConfigNameDatas, MicroServiceConfigName.class);
    }

    @RequestMapping(value={"/get_MicroServiceLocationDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceLocationByNameDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, microServiceLocationDatas, "name", MicroServiceLocation.class);
    }

    @RequestMapping(value={"/get_MicroServiceLocationData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceLocationDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, microServiceLocationDatas, MicroServiceLocation.class);
    }

    @RequestMapping(value={"/ms_dictionary/save_location"}, method={RequestMethod.POST})
    public ModelAndView saveMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response)throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            MicroServiceLocation microServiceLocation;
            if(fromAPI){
                microServiceLocation = mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceLocation.class);
            }else{
                microServiceLocation = mapper.readValue(root.get("microServiceLocationDictionaryData").toString(), MicroServiceLocation.class);
            }
            
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(microServiceLocation.getName(), "name", MicroServiceLocation.class);
            boolean duplicateflag = false;
            if(duplicateData != null && !duplicateData.isEmpty()){
                MicroServiceLocation data = (MicroServiceLocation) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    microServiceLocation.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != microServiceLocation.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(microServiceLocation.getId() == 0){
                    commonClassDao.save(microServiceLocation);
                }else{
                    commonClassDao.update(microServiceLocation);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceLocation.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, microServiceLocationDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ms_dictionary/remove_msLocation"}, method={RequestMethod.POST})
    public void removeMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, microServiceLocationDatas, MicroServiceLocation.class);
    }

    @RequestMapping(value={"/get_MicroServiceAttributeDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceAttributeByNameDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, microServiceAttributeDatas, "name", MicroServiceAttribute.class);
    }
    
    @RequestMapping(value={"/get_MicroServiceAttributeData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceAttributeDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, microServiceAttributeDatas, MicroServiceAttribute.class);
    }
    
    @RequestMapping(value={"/ms_dictionary/save_modelAttribute"}, method={RequestMethod.POST})
    public ModelAndView saveMicroServiceAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            
            MicroServiceAttribute microServiceAttribute;
            String checkValue;
            if (fromAPI) {
                microServiceAttribute = mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceAttribute.class);
            } else {
                microServiceAttribute = mapper.readValue(root.get("modelAttributeDictionaryData").toString(), MicroServiceAttribute.class);
            }
            checkValue = microServiceAttribute.getName() + ":" + microServiceAttribute.getValue() + ":" + microServiceAttribute.getModelName();
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkValue, "name:value:modelName", MicroServiceAttribute.class);
            boolean duplicateflag = false;
            if(duplicateData != null && !duplicateData.isEmpty()){
                MicroServiceAttribute data = (MicroServiceAttribute) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    microServiceAttribute.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != microServiceAttribute.getId()))){
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if(!duplicateflag){
                if(microServiceAttribute.getId() == 0){
                    commonClassDao.save(microServiceAttribute);
                }else{
                    commonClassDao.update(microServiceAttribute);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceAttribute.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, microServiceAttributeDatas, responseString);
            }
        }
        catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }
 
    @RequestMapping(value={"/ms_dictionary/remove_modelAttribute"}, method={RequestMethod.POST})
    public void removeMicroServiceAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, microServiceAttributeDatas, MicroServiceAttribute.class);
    }
 

    @RequestMapping(value={"/get_MicroServiceModelsDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryByNameEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, microServiceModelsDictionaryDatas, modelName, MicroServiceModels.class);
    }

    @RequestMapping(value={"/get_MicroServiceModelsDataByVersion"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryByVersionEntityData(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request.getReader());
            String modelName = null;
            if (root.get(microServiceModelsDictionaryData).has(modelName)){
                modelName = root.get(microServiceModelsDictionaryData).get(modelName).asText().replace("\"", "");
            }
             if (modelName!=null){
                    model.put(microServiceModelsDictionaryDatas, mapper.writeValueAsString(commonClassDao.getDataById(MicroServiceModels.class, modelName, modelName)));
             } else{
                 model.put(errorMsg, "No model name given");
             }
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.getWriter().write(j.toString());
        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }
    
    @RequestMapping(value={"/get_MicroServiceModelsData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, microServiceModelsDictionaryDatas, MicroServiceModels.class);
    }

    @RequestMapping(value={"/get_MicroServiceModelsDataServiceVersion"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryEntityDataServiceVersion(HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            List<String> data = new ArrayList<>();
            List<Object> datas = commonClassDao.getData(MicroServiceModels.class);
            for(int i = 0; i < datas.size(); i++){
                MicroServiceModels msmodel = (MicroServiceModels) datas.get(i);
                if (!data.contains(msmodel.getModelName())){
                    data.add(msmodel.getModelName() + "-v" + msmodel.getVersion());
                }
            }
            model.put(microServiceModelsDictionaryDatas, mapper.writeValueAsString(data));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader("operation", "getDictionary");
            response.getWriter().write(j.toString());
 
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
            LOGGER.error(e);
        }
    }
    
    @RequestMapping(value={"/get_MicroServiceModelsDataByClass"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryClassEntityData(HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("microServiceModelsDictionaryClassDatas", mapper.writeValueAsString(modelList));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);    
            response.addHeader(operation, getDictionary);
            response.getWriter().write(j.toString());
 
        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
        }
    }
    
    @RequestMapping(value={"/ms_dictionary/save_model"}, method={RequestMethod.POST})
    public ModelAndView saveMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            this.newModel = new MicroServiceModels();
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            MicroServiceModels microServiceModels = new MicroServiceModels();
            String userId = null;

            String dataOrderInfo = null;
            if(root.has("dataOrderInfo")){
                dataOrderInfo = root.get("dataOrderInfo").toString();
            }

            if(root.has("modelType")){
                JsonNode dataType = root.get("modelType");
                String modelType= dataType.toString();
                if(modelType.contains("yml")){
                    if (root.has(microServiceModelsDictionaryData)){
                        if (root.get(microServiceModelsDictionaryData).has(description)){
                            microServiceModels.setDescription(root.get(microServiceModelsDictionaryData).get(description).asText().replace("\"", ""));
                        }
                        if (root.get(microServiceModelsDictionaryData).has(modelName)){
                            microServiceModels.setModelName(root.get(microServiceModelsDictionaryData).get(modelName).asText().replace("\"", ""));
                            this.newModel.setModelName(microServiceModels.getModelName());
                        }
                        if (root.get(microServiceModelsDictionaryData).has(version)){
                            microServiceModels.setVersion(root.get(microServiceModelsDictionaryData).get(version).asText().replace("\"", ""));
                            this.newModel.setVersion(microServiceModels.getVersion());
                        }
                    }

                    classMap = new LinkedHashMap<>();
                    JsonNode data = root.get(classMapData);
                    ObjectMapper mapper1 = new ObjectMapper();
                    String data1 = data.toString().substring(1, data.toString().length()-1);
                    data1 = data1.replace("\\", "");
                    data1=data1.replace("\"{","{");
                    data1=data1.replace("}\"","}");
                    JSONObject jsonObject = new JSONObject(data1);
                    Set<String> keys = jsonObject.keySet();
                    for(String key : keys){
                        String value = jsonObject.get(key).toString();
                        MSAttributeObject msAttributeObject = mapper1.readValue(value, MSAttributeObject.class);
                        classMap.put(key, msAttributeObject);
                    }

                    userId = root.get("userid").textValue();
                    MSAttributeObject mainClass = classMap.get(this.newModel.getModelName());
                    this.newModel.setDependency("[]");
                    String value = new Gson().toJson(mainClass.getSubClass());
                    this.newModel.setSub_attributes(value);
                    String attributes= mainClass.getAttribute().toString().replace("{", "").replace("}", "");
                    int equalsIndexForAttributes= attributes.indexOf('=');
                    String atttributesAfterFirstEquals= attributes.substring(equalsIndexForAttributes+1);
                    this.newModel.setAttributes(atttributesAfterFirstEquals);
                    String refAttributes= mainClass.getRefAttribute().toString().replace("{", "").replace("}", "");
                    int equalsIndex= refAttributes.indexOf("=");
                    String refAttributesAfterFirstEquals= refAttributes.substring(equalsIndex+1);
                    this.newModel.setRef_attributes(refAttributesAfterFirstEquals);
                    this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
                    this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));

                }else{
                    if (fromAPI) {
                        microServiceModels = mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceModels.class);
                        userId = "API";
                    } else {
                        if (root.has(microServiceModelsDictionaryData)){
                            if (root.get(microServiceModelsDictionaryData).has(description)){
                                microServiceModels.setDescription(root.get(microServiceModelsDictionaryData).get(description).asText().replace("\"", ""));
                            }
                            if (root.get(microServiceModelsDictionaryData).has(modelName)){
                                microServiceModels.setModelName(root.get(microServiceModelsDictionaryData).get(modelName).asText().replace("\"", ""));
                                this.newModel.setModelName(microServiceModels.getModelName());
                            }
                            if (root.get(microServiceModelsDictionaryData).has(version)){
                                microServiceModels.setVersion(root.get(microServiceModelsDictionaryData).get(version).asText().replace("\"", ""));
                                this.newModel.setVersion(microServiceModels.getVersion());
                            }
                        }
                        if(root.has(classMapData)){
                            classMap = new LinkedHashMap<>();
                            JsonNode data = root.get(classMapData);
                            ObjectMapper mapper1 = new ObjectMapper();
                            String data1 = data.toString().substring(1, data.toString().length()-1);
                            data1 = data1.replace("\\", "");
                            JSONObject jsonObject = new JSONObject(data1);
                            Set<String> keys = jsonObject.keySet();
                            for(String key : keys){
                                String value = jsonObject.get(key).toString();
                                MSAttributeObject msAttributeObject = mapper1.readValue(value, MSAttributeObject.class);
                                classMap.put(key, msAttributeObject);
                            }
                        }
                        userId = root.get("userid").textValue();
                        addValuesToNewModel(classMap);
                    }
                }
            }
            microServiceModels.setAttributes(this.newModel.getAttributes());
            microServiceModels.setRef_attributes(this.newModel.getRef_attributes());
            microServiceModels.setDependency(this.newModel.getDependency());
            microServiceModels.setModelName(this.newModel.getModelName());
            microServiceModels.setSub_attributes(this.newModel.getSub_attributes());
            microServiceModels.setVersion(this.newModel.getVersion());
            microServiceModels.setEnumValues(this.newModel.getEnumValues());
            microServiceModels.setAnnotation(this.newModel.getAnnotation());
            if(dataOrderInfo != null){
                 microServiceModels.setDataOrderInfo(dataOrderInfo);
            }
            String checkName = microServiceModels.getModelName() + ":" + microServiceModels.getVersion();
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkName, "modelName:version", MicroServiceModels.class);
            boolean duplicateflag = false;
            if(duplicateData != null && !duplicateData.isEmpty()){
                MicroServiceModels data = (MicroServiceModels) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    microServiceModels.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != microServiceModels.getId()))){
                    duplicateflag = true;
                }
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            String responseString = null;
            if(!duplicateflag){
                microServiceModels.setUserCreatedBy(userInfo);
                if(microServiceModels.getId() == 0){
                    commonClassDao.save(microServiceModels);
                }else{
                    commonClassDao.update(microServiceModels);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PrefixList.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, microServiceModelsDictionaryDatas, responseString);
            }
        }catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/ms_dictionary/remove_msModel"}, method={RequestMethod.POST})
    public void removeMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, microServiceModelsDictionaryDatas, MicroServiceModels.class);
    }

    private void addValuesToNewModel(HashMap<String,MSAttributeObject > classMap) {
        //Loop  through the classmap and pull out the required info for the new file.
        String subAttribute = null;

        MSAttributeObject mainClass = classMap.get(this.newModel.getModelName());

        if (mainClass !=null){
            String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
            ArrayList<String> dependency = new ArrayList<>(Arrays.asList(dependTemp.split(",")));
            dependency = getFullDependencyList(dependency);
            for (String element : dependency){
                MSAttributeObject temp = classMap.get(element);
                if (temp!=null){
                    mainClass.addAllRefAttribute(temp.getRefAttribute());
                    mainClass.addAllAttribute(temp.getAttribute());
                }
            }
            subAttribute = utils.createSubAttributes(dependency, classMap, this.newModel.getModelName());
        }else{
            subAttribute = "{}";
            this.newModel.setDependency("");
        }

        if (mainClass != null && mainClass.getDependency()==null){
            mainClass.setDependency("");
        }
        if(mainClass != null){
            this.newModel.setDependency(mainClass.getDependency());
            this.newModel.setSub_attributes(subAttribute);
            this.newModel.setAttributes(mainClass.getAttribute().toString().replace("{", "").replace("}", ""));
            this.newModel.setRef_attributes(mainClass.getRefAttribute().toString().replace("{", "").replace("}", ""));
            this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
            this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
        }
    }

    private ArrayList<String> getFullDependencyList(ArrayList<String> dependency) {
        ArrayList<String> returnList = new ArrayList<>();
        ArrayList<String> workingList = new ArrayList<>();
        returnList.addAll(dependency);
        for (String element : dependency ){
            if (classMap.containsKey(element)){
                MSAttributeObject value = classMap.get(element);
                String rawValue = StringUtils.replaceEach(value.getDependency(), new String[]{"[", "]"}, new String[]{"", ""});
                workingList = new ArrayList<>(Arrays.asList(rawValue.split(",")));
                for(String depend : workingList){
                    if (!returnList.contains(depend) && !depend.isEmpty()){
                        returnList.add(depend.trim());
                        //getFullDepedency(workingList)
                    }
                }
            }
        }

        return returnList;
    }

    @RequestMapping(value={"/get_MicroServiceHeaderDefaultsDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceHeaderDefaultsEntityDataByName(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, microServiceHeaderDefaultDatas, "modelName", MicroserviceHeaderdeFaults.class);
    }

    @RequestMapping(value={"/get_MicroServiceHeaderDefaultsData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceHeaderDefaultsEntityData(HttpServletResponse response){
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, microServiceHeaderDefaultDatas, MicroserviceHeaderdeFaults.class);
    }
    
    
    @RequestMapping(value={"/ms_dictionary/save_headerDefaults"}, method={RequestMethod.POST})
    public ModelAndView saveMicroServiceHeaderDefaultValues(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            
            MicroserviceHeaderdeFaults msHeaderdeFaults;
            if(fromAPI){
                msHeaderdeFaults = mapper.readValue(root.get(dictionaryFields).toString(), MicroserviceHeaderdeFaults.class);
            }else{
                msHeaderdeFaults = mapper.readValue(root.get("modelAttributeDictionaryData").toString(), MicroserviceHeaderdeFaults.class);
            }
            
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(msHeaderdeFaults.getModelName(), "modelName", MicroserviceHeaderdeFaults.class);
            boolean duplicateflag = false;
            if(duplicateData != null && !duplicateData.isEmpty()){
                MicroserviceHeaderdeFaults data = (MicroserviceHeaderdeFaults) duplicateData.get(0);
                if(request.getParameter(operation) != null && "update".equals(request.getParameter(operation))){
                    msHeaderdeFaults.setId(data.getId());
                }else if((request.getParameter(operation) != null && !"update".equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != msHeaderdeFaults.getId()))){
                    duplicateflag = true;
                }
            }
            
            String responseString = null;
            if(!duplicateflag){
                if(msHeaderdeFaults.getId() == 0){
                    commonClassDao.save(msHeaderdeFaults);
                }else{
                    commonClassDao.update(msHeaderdeFaults);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroserviceHeaderdeFaults.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return utils.getResultForApi(responseString);
            }else{
                utils.setResponseData(response, microServiceHeaderDefaultDatas, responseString);
            }
        }
        catch (Exception e){
            utils.setErrorResponseData(response, e);
        }
        return null;
    }
    
    @RequestMapping(value={"/ms_dictionary/remove_headerDefaults"}, method={RequestMethod.POST})
    public void removeMicroServiceHeaderDefaults(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, microServiceHeaderDefaultDatas, MicroserviceHeaderdeFaults.class);
    }
}