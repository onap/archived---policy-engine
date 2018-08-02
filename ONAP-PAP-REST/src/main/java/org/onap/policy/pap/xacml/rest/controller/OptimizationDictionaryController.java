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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.OptimizationModels;
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
public class OptimizationDictionaryController {
    private static CommonClassDao commonClassDao;

    private static String operation = "operation";
    private LinkedHashMap<String,MSAttributeObject > classMap;
    private static String dictionaryFields ="dictionaryFields";
    private static String duplicateResponseString = "Duplicate";
    private static String optimizationModelsDictionaryDatas = "optimizationModelsDictionaryDatas";
    private static String modelName = "modelName";
    private static String optimizationModelsDictionaryData = "optimizationModelsDictionaryData";
    private static String description = "description";
    private static String version = "version";
    private static String classMapData = "classMap";
    private static final String UPDATE = "update";


    public OptimizationDictionaryController(){
        super();
    }	

    private DictionaryUtils getDictionaryUtilsInstance(){
        return DictionaryUtils.getDictionaryUtils();
    }
    
    @Autowired
    public OptimizationDictionaryController(CommonClassDao commonClassDao){
        setCommonClassDao(commonClassDao);
    }
    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        OptimizationDictionaryController.commonClassDao = commonClassDao;
    }

    MSModelUtils utils = new MSModelUtils(XACMLPapServlet.getMsOnapName(), XACMLPapServlet.getMsPolicyName());

    private OptimizationModels newModel;

    @RequestMapping(value={"/get_OptimizationModelsData"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getOptimizationModelsDictionaryEntityData(HttpServletResponse response){
        DictionaryUtils dUtils = getDictionaryUtilsInstance();
        dUtils.getData(response, optimizationModelsDictionaryDatas, OptimizationModels.class);
    }

    @RequestMapping(value={"/get_OptimizationModelsDataByName"}, method={RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getOptimizationModelsDictionaryByNameEntityData(HttpServletResponse response){
        DictionaryUtils dUtils = getDictionaryUtilsInstance();
        dUtils.getDataByEntity(response, optimizationModelsDictionaryDatas, modelName, OptimizationModels.class);
    }

    @RequestMapping(value={"/oof_dictionary/save_model"}, method={RequestMethod.POST})
    public ModelAndView saveOptimizationModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        DictionaryUtils dUtils = getDictionaryUtilsInstance();
        try {
            this.newModel = new OptimizationModels();
            boolean fromAPI = dUtils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            OptimizationModels optimizationModels = new OptimizationModels();
            String userId = null;

            String dataOrderInfo = null;
            if(root.has("dataOrderInfo")){
                dataOrderInfo = root.get("dataOrderInfo").toString();
            }

            if(root.has("modelType")){
                JsonNode dataType = root.get("modelType");
                String modelType= dataType.toString();
                if(modelType.contains("yml")){
                    if (root.has(optimizationModelsDictionaryData)){
                        if (root.get(optimizationModelsDictionaryData).has(description)){
                            optimizationModels.setDescription(root.get(optimizationModelsDictionaryData).get(description).asText().replace("\"", ""));
                        }
                        if (root.get(optimizationModelsDictionaryData).has(modelName)){
                            optimizationModels.setModelName(root.get(optimizationModelsDictionaryData).get(modelName).asText().replace("\"", ""));
                            this.newModel.setModelName(optimizationModels.getModelName());
                        }
                        if (root.get(optimizationModelsDictionaryData).has(version)){
                            optimizationModels.setVersion(root.get(optimizationModelsDictionaryData).get(version).asText().replace("\"", ""));
                            this.newModel.setVersion(optimizationModels.getVersion());
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
                    this.newModel.setSubattributes(value);
                    String attributes= mainClass.getAttribute().toString().replace("{", "").replace("}", "");
                    int equalsIndexForAttributes= attributes.indexOf('=');
                    String atttributesAfterFirstEquals= attributes.substring(equalsIndexForAttributes+1, attributes.length()-1);
                    this.newModel.setAttributes(atttributesAfterFirstEquals);
                    String refAttributes= mainClass.getRefAttribute().toString().replace("{", "").replace("}", "");
                    int equalsIndex= refAttributes.indexOf('=');
                    String refAttributesAfterFirstEquals= refAttributes.substring(equalsIndex+1, refAttributes.length()-1);
                    this.newModel.setRefattributes(refAttributesAfterFirstEquals);
                    this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
                    this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));

                }else{
                    if (fromAPI) {
                        optimizationModels = mapper.readValue(root.get(dictionaryFields).toString(), OptimizationModels.class);
                        userId = "API";
                    } else {
                        if (root.has(optimizationModelsDictionaryData)){
                            if (root.get(optimizationModelsDictionaryData).has(description)){
                                optimizationModels.setDescription(root.get(optimizationModelsDictionaryData).get(description).asText().replace("\"", ""));
                            }
                            if (root.get(optimizationModelsDictionaryData).has(modelName)){
                                optimizationModels.setModelName(root.get(optimizationModelsDictionaryData).get(modelName).asText().replace("\"", ""));
                                this.newModel.setModelName(optimizationModels.getModelName());
                            }
                            if (root.get(optimizationModelsDictionaryData).has(version)){
                                optimizationModels.setVersion(root.get(optimizationModelsDictionaryData).get(version).asText().replace("\"", ""));
                                this.newModel.setVersion(optimizationModels.getVersion());
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
            optimizationModels.setAttributes(this.newModel.getAttributes());
            optimizationModels.setRefattributes(this.newModel.getRefattributes());
            optimizationModels.setDependency(this.newModel.getDependency());
            optimizationModels.setModelName(this.newModel.getModelName());
            optimizationModels.setSubattributes(this.newModel.getSubattributes());
            optimizationModels.setVersion(this.newModel.getVersion());
            optimizationModels.setEnumValues(this.newModel.getEnumValues());
            optimizationModels.setAnnotation(this.newModel.getAnnotation());

            if(dataOrderInfo != null){
                optimizationModels.setDataOrderInfo(dataOrderInfo);
            }

            String checkName = optimizationModels.getModelName() + ":" + optimizationModels.getVersion();
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkName, "modelName:version", OptimizationModels.class);
            boolean duplicateflag = false;
            if(duplicateData!=null && !duplicateData.isEmpty()){
                OptimizationModels data = (OptimizationModels) duplicateData.get(0);
                if(request.getParameter(operation) != null && UPDATE.equals(request.getParameter(operation))){
                    optimizationModels.setId(data.getId());
                }else if((request.getParameter(operation) != null && !UPDATE.equals(request.getParameter(operation))) ||
                        (request.getParameter(operation) == null && (data.getId() != optimizationModels.getId()))){
                    duplicateflag = true;
                }
            }
            UserInfo userInfo = dUtils.getUserInfo(userId);

            String responseString = null;
            if(!duplicateflag){
                optimizationModels.setUserCreatedBy(userInfo);
                if(optimizationModels.getId() == 0){
                    commonClassDao.save(optimizationModels);
                }else{
                    commonClassDao.update(optimizationModels);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(OptimizationModels.class));
            }else{
                responseString = duplicateResponseString;
            }
            if(fromAPI){
                return dUtils.getResultForApi(responseString);
            }else{
                dUtils.setResponseData(response, optimizationModelsDictionaryDatas, responseString);
            }
        }catch (Exception e){
            dUtils.setErrorResponseData(response, e);
        }
        return null;
    }

    @RequestMapping(value={"/oof_dictionary/remove_model"}, method={RequestMethod.POST})
    public void removeOptimizationModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils dUtils = getDictionaryUtilsInstance();
        dUtils.removeData(request, response, optimizationModelsDictionaryDatas, OptimizationModels.class);
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
            this.newModel.setSubattributes(subAttribute);
            this.newModel.setAttributes(mainClass.getAttribute().toString().replace("{", "").replace("}", ""));
            this.newModel.setRefattributes(mainClass.getRefAttribute().toString().replace("{", "").replace("}", ""));
            this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
            this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
        }
    }

    private ArrayList<String> getFullDependencyList(ArrayList<String> dependency) {
        ArrayList<String> returnList = new ArrayList<>();
        ArrayList<String> workingList;
        returnList.addAll(dependency);
        for (String element : dependency ){
            if (classMap.containsKey(element)){
                MSAttributeObject value = classMap.get(element);
                String rawValue = StringUtils.replaceEach(value.getDependency(), new String[]{"[", "]"}, new String[]{"", ""});
                workingList = new ArrayList<>(Arrays.asList(rawValue.split(",")));
                for(String depend : workingList){
                    if (!returnList.contains(depend) && !depend.isEmpty()){
                        returnList.add(depend.trim());
                    }
                }
            }
        }

        return returnList;
    }

}
