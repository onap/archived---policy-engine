/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.controller;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.MicroserviceHeaderdeFaults;
import org.onap.policy.rest.jpa.OptimizationModels;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.util.MsAttributeObject;
import org.onap.policy.rest.util.MsModelUtils;
import org.onap.policy.rest.util.MsModelUtils.ModelType;
import org.onap.policy.utils.PolicyUtils;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class CreateOptimizationController extends RestrictedBaseController {
    private static final Logger LOGGER = FlexLogger.getLogger(CreateOptimizationController.class);

    private static final int BUFFER = 2048;

    private static CommonClassDao commonClassDao;

    public static CommonClassDao getCommonClassDao() {
        return commonClassDao;
    }

    private OptimizationModels newModel;
    private String newFile;
    private String directory;
    private List<String> modelList = new ArrayList<>();
    private List<String> dirDependencyList = new ArrayList<>();
    private LinkedHashMap<String, MsAttributeObject> classMap = new LinkedHashMap<>();
    String referenceAttributes;
    String attributeString;
    Set<String> allManyTrueKeys = new HashSet<>();

    public static final String DATATYPE = "data_types.policy.data.";
    public static final String PROPERTIES = ".properties.";
    public static final String TYPE = ".type";
    public static final String STRING = "string";
    public static final String INTEGER = "integer";
    public static final String LIST = "list";
    public static final String DEFAULT = ".default";
    public static final String REQUIRED = ".required";
    public static final String MATCHABLE = ".matchable";
    public static final String MANYFALSE = ":MANY-false";
    public static final String MODEL = "model";
    public static final String MANY = "MANY-";
    public static final String MODELNAME = "modelName";

    @Autowired
    private CreateOptimizationController(CommonClassDao commonClassDao) {
        setCommonClassDao(commonClassDao);
    }

    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        CreateOptimizationController.commonClassDao = commonClassDao;
    }

    public CreateOptimizationController() {
        // Empty Constructor
    }

    protected PolicyRestAdapter policyAdapter = null;
    private Map<String, String> attributesListRefMap = new HashMap<>();
    private Map<String, LinkedList<String>> arrayTextList = new HashMap<>();
    CreateDcaeMicroServiceController msController = new CreateDcaeMicroServiceController();

    /**
     * setDataToPolicyRestAdapter.
     *
     * @param policyData PolicyRestAdapter
     * @param root JsonNode
     * @return PolicyRestAdapter
     */
    public PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter policyData, JsonNode root) {
        String jsonContent = null;
        try {
            LOGGER.info("policyJSON :" + (root.get("policyJSON")).toString());

            String tempJson = root.get("policyJSON").toString();

            // ---replace empty value with the value below before calling decodeContent method.
            String dummyValue = "*empty-value*" + UUID.randomUUID().toString();
            LOGGER.info("dummyValue:" + dummyValue);
            tempJson =
                    StringUtils.replaceEach(tempJson, new String[] {"\"\""}, new String[] {"\"" + dummyValue + "\""});
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tempJsonNode = mapper.readTree(tempJson);
            jsonContent = msController.decodeContent(tempJsonNode).toString();
            constructJson(policyData, jsonContent, dummyValue);
        } catch (Exception e) {
            LOGGER.error("Error while decoding microservice content", e);
        }

        return policyData;
    }

    private PolicyRestAdapter constructJson(PolicyRestAdapter policyAdapter, String jsonContent, String dummyValue) {
        ObjectWriter om = new ObjectMapper().writer();
        String json = "";
        OptimizationObject optimizationObject = setOptimizationObjectValues(policyAdapter);

        optimizationObject.setContent(jsonContent);

        try {
            json = om.writeValueAsString(optimizationObject);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error writing out the object", e);
        }
        LOGGER.info("input json: " + json);
        LOGGER.info("input jsonContent: " + jsonContent);
        String cleanJson = msController.cleanUpJson(json);

        // --- reset empty value back after called cleanUPJson method and before calling removeNullAttributes
        String tempJson =
                StringUtils.replaceEach(cleanJson, new String[] {"\"" + dummyValue + "\""}, new String[] {"\"\""});
        LOGGER.info("tempJson: " + tempJson);
        cleanJson = msController.removeNullAttributes(tempJson);
        policyAdapter.setJsonBody(cleanJson);
        return policyAdapter;
    }

    /**
     * getOptimizationTemplateData.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     * @throws IOException IOException
     */
    @RequestMapping(
            value = {"/policyController/getOptimizationTemplateData.htm"},
            method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView getOptimizationTemplateData(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode root = mapper.readTree(request.getReader());

        String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
        String servicename = value.split("-v")[0];
        String version = null;
        if (value.contains("-v")) {
            version = value.split("-v")[1];
        }

        OptimizationModels returnModel = getAttributeObject(servicename, version);

        MicroserviceHeaderdeFaults returnHeaderDefauls = getHeaderDefaultsObject(value);
        JSONObject jsonHdDefaultObj = null;
        if (returnHeaderDefauls != null) {
            jsonHdDefaultObj = new JSONObject();
            jsonHdDefaultObj.put("onapName", returnHeaderDefauls.getOnapName());
            jsonHdDefaultObj.put("guard", returnHeaderDefauls.getGuard());
            jsonHdDefaultObj.put("riskLevel", returnHeaderDefauls.getRiskLevel());
            jsonHdDefaultObj.put("riskType", returnHeaderDefauls.getRiskType());
            jsonHdDefaultObj.put("priority", returnHeaderDefauls.getPriority());
        }

        String headDefautlsData = "";
        if (jsonHdDefaultObj != null) {
            headDefautlsData = jsonHdDefaultObj.toString();
            LOGGER.info("returnHeaderDefauls headDefautlsData: " + headDefautlsData);
        } else {
            headDefautlsData = "null";
        }

        // Get all keys with "MANY-true" defined in their value from subAttribute
        Set<String> allkeys = null;
        if (returnModel.getSubattributes() != null && !returnModel.getSubattributes().isEmpty()) {
            getAllKeys(new JSONObject(returnModel.getSubattributes()));
            allkeys = allManyTrueKeys;
            allManyTrueKeys = new HashSet<>();
            LOGGER.info("allkeys : " + allkeys);
        }

        // Get element order info
        String dataOrderInfo = returnModel.getDataOrderInfo();
        if (dataOrderInfo != null && !dataOrderInfo.startsWith("\"")) {
            dataOrderInfo = "\"" + dataOrderInfo + "\"";
        }

        String nameOfTrueKeys = "";
        if (allkeys != null) {
            nameOfTrueKeys = allkeys.toString();
        }

        String jsonModel = createOptimizationJson(returnModel);

        JSONObject jsonObject = new JSONObject(jsonModel);

        JSONObject finalJsonObject = null;
        if (allkeys != null) {
            Iterator<String> iter = allkeys.iterator();
            while (iter.hasNext()) {
                // Convert to array values for MANY-true keys
                finalJsonObject = controller.convertToArrayElement(jsonObject, iter.next());
            }
        }

        if (finalJsonObject != null) {
            LOGGER.info(finalJsonObject.toString());
            jsonModel = finalJsonObject.toString();
        }

        // get all properties with "MANY-true" defined in Ref_attributes
        Set<String> manyTrueProperties = controller.getManyTrueProperties(returnModel.getRefattributes());
        JSONObject jsonObj = new JSONObject(jsonModel);
        for (String s : manyTrueProperties) {
            LOGGER.info(s);
            // convert to array element for MANY-true properties
            finalJsonObject = controller.convertToArrayElement(jsonObj, s.trim());
        }

        if (finalJsonObject != null) {
            LOGGER.info(finalJsonObject.toString());
            jsonModel = finalJsonObject.toString();
        }

        response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
        response.setContentType(PolicyUtils.APPLICATION_JSON);
        request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
        List<Object> list = new ArrayList<>();
        String responseString = mapper.writeValueAsString(returnModel);
        JSONObject json = null;
        if ("".equals(nameOfTrueKeys)) {
            json = new JSONObject("{optimizationModelData: " + responseString + ",jsonValue: " + jsonModel
                    + ",dataOrderInfo:" + dataOrderInfo + ",headDefautlsData:" + headDefautlsData + "}");
        } else {
            json = new JSONObject("{optimizationModelData: " + responseString + ",jsonValue: " + jsonModel
                    + ",allManyTrueKeys: " + allManyTrueKeys + ",dataOrderInfo:" + dataOrderInfo + ",headDefautlsData:"
                    + headDefautlsData + "}");
        }
        list.add(json);
        response.getWriter().write(list.toString());
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String createOptimizationJson(OptimizationModels returnModel) {
        Map<String, String> attributeMap = new HashMap<>();
        Map<String, String> refAttributeMap = new HashMap<>();

        String attribute = returnModel.getAttributes();
        if (attribute != null) {
            attribute = attribute.trim();
        }
        String refAttribute = returnModel.getRefattributes();
        if (refAttribute != null) {
            refAttribute = refAttribute.trim();
        }

        String enumAttribute = returnModel.getEnumValues();
        if (enumAttribute != null) {
            enumAttribute = enumAttribute.trim();
        }

        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
        if (!StringUtils.isEmpty(attribute)) {
            attributeMap = controller.convert(attribute, ",");
        }

        if (!StringUtils.isEmpty(refAttribute)) {
            refAttributeMap = controller.convert(refAttribute, ",");
        }

        Gson gson = new Gson();

        String subAttributes = returnModel.getSubattributes();
        if (subAttributes != null) {
            subAttributes = subAttributes.trim();
        } else {
            subAttributes = "";
        }

        Map gsonObject = (Map) gson.fromJson(subAttributes, Object.class);

        JSONObject object = new JSONObject();
        JSONArray array;

        for (Entry<String, String> keySet : attributeMap.entrySet()) {
            array = new JSONArray();
            String value = keySet.getValue();
            if ("true".equalsIgnoreCase(keySet.getValue().split(MANY)[1])) {
                array.put(value);
                object.put(keySet.getKey().trim(), array);
            } else {
                object.put(keySet.getKey().trim(), value.trim());
            }
        }

        for (Entry<String, String> keySet : refAttributeMap.entrySet()) {
            array = new JSONArray();
            String value = keySet.getValue().split(":")[0];
            if (gsonObject.containsKey(value)) {
                if ("true".equalsIgnoreCase(keySet.getValue().split(MANY)[1])) {
                    array.put(recursiveReference(value, gsonObject, enumAttribute));
                    object.put(keySet.getKey().trim(), array);
                } else {
                    object.put(keySet.getKey().trim(), recursiveReference(value, gsonObject, enumAttribute));
                }
            } else {
                if ("true".equalsIgnoreCase(keySet.getValue().split(MANY)[1])) {
                    array.put(value.trim());
                    object.put(keySet.getKey().trim(), array);
                } else {
                    object.put(keySet.getKey().trim(), value.trim());
                }
            }
        }

        return object.toString();
    }

    @SuppressWarnings("unchecked")
    private JSONObject recursiveReference(String name, Map<String, String> subAttributeMap, String enumAttribute) {
        JSONObject object = new JSONObject();
        Map<String, String> map;
        Object returnClass = subAttributeMap.get(name);
        map = (Map<String, String>) returnClass;
        JSONArray array;

        for (Entry<String, String> m : map.entrySet()) {
            String[] splitValue = m.getValue().split(":");
            array = new JSONArray();
            if (subAttributeMap.containsKey(splitValue[0])) {
                if ("true".equalsIgnoreCase(m.getValue().split(MANY)[1])) {
                    array.put(recursiveReference(splitValue[0], subAttributeMap, enumAttribute));
                    object.put(m.getKey().trim(), array);
                } else {
                    object.put(m.getKey().trim(), recursiveReference(splitValue[0], subAttributeMap, enumAttribute));
                }
            } else {
                if ("true".equalsIgnoreCase(m.getValue().split(MANY)[1])) {
                    array.put(splitValue[0].trim());
                    object.put(m.getKey().trim(), array);
                } else {
                    object.put(m.getKey().trim(), splitValue[0].trim());
                }
            }
        }

        return object;
    }

    // call this method to start the recursive
    private Set<String> getAllKeys(JSONObject json) {
        return getAllKeys(json, new HashSet<>());
    }

    private Set<String> getAllKeys(JSONArray arr) {
        return getAllKeys(arr, new HashSet<>());
    }

    private Set<String> getAllKeys(JSONArray arr, Set<String> keys) {
        for (int i = 0; i < arr.length(); i++) {
            Object obj = arr.get(i);
            if (obj instanceof JSONObject) {
                keys.addAll(getAllKeys(arr.getJSONObject(i)));
            }
            if (obj instanceof JSONArray) {
                keys.addAll(getAllKeys(arr.getJSONArray(i)));
            }
        }

        return keys;
    }

    // this method returns a set of keys with "MANY-true" defined in their value.
    private Set<String> getAllKeys(JSONObject json, Set<String> keys) {
        for (String key : json.keySet()) {
            Object obj = json.get(key);
            if (obj instanceof String && ((String) obj).contains("MANY-true")) {
                LOGGER.info("key : " + key);
                LOGGER.info("obj : " + obj);
                allManyTrueKeys.add(key);
            }
            if (obj instanceof JSONObject) {
                keys.addAll(getAllKeys(json.getJSONObject(key)));
            }
            if (obj instanceof JSONArray) {
                keys.addAll(getAllKeys(json.getJSONArray(key)));
            }
        }

        return keys;
    }

    /**
     * getModelServiceVersionData.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     * @throws IOException IOException
     */
    @RequestMapping(
            value = {"/policyController/getModelServiceVersionData.htm"},
            method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView getModelServiceVersionData(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode root = mapper.readTree(request.getReader());

        final String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
        final String servicename = value.split("-v")[0];

        response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
        response.setContentType(PolicyUtils.APPLICATION_JSON);
        request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
        List<Object> list = new ArrayList<>();
        list.add(new JSONObject("{optimizationModelVersionData: "
                + mapper.writeValueAsString(getVersionList(servicename)) + "}"));
        response.getWriter().write(list.toString());
        return null;
    }

    private Set<String> getVersionList(String name) {
        OptimizationModels workingModel;
        Set<String> list = new HashSet<>();
        List<Object> optimizationModelsData = commonClassDao.getDataById(OptimizationModels.class, MODELNAME, name);
        for (int i = 0; i < optimizationModelsData.size(); i++) {
            workingModel = (OptimizationModels) optimizationModelsData.get(i);
            if (workingModel.getVersion() != null) {
                list.add(workingModel.getVersion());
            } else {
                list.add("Default");
            }
        }
        return list;
    }

    private OptimizationModels getAttributeObject(String name, String version) {
        OptimizationModels workingModel = new OptimizationModels();
        List<Object> optimizationModelsData = commonClassDao.getDataById(OptimizationModels.class, MODELNAME, name);
        for (int i = 0; i < optimizationModelsData.size(); i++) {
            workingModel = (OptimizationModels) optimizationModelsData.get(i);
            if (version != null) {
                if (workingModel.getVersion() != null) {
                    if (workingModel.getVersion().equals(version)) {
                        return workingModel;
                    }
                } else {
                    return workingModel;
                }
            } else {
                return workingModel;
            }

        }
        return workingModel;
    }

    private MicroserviceHeaderdeFaults getHeaderDefaultsObject(String modelName) {
        return (MicroserviceHeaderdeFaults) commonClassDao.getEntityItem(MicroserviceHeaderdeFaults.class, MODELNAME,
                modelName);
    }

    /**
     * prePopulatePolicyData.
     *
     * @param policyAdapter PolicyRestAdapter
     * @param entity PolicyEntity
     */
    public void prePopulatePolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        if (! (policyAdapter.getPolicyData() instanceof PolicyType)) {
            return;
        }
        Object policyData = policyAdapter.getPolicyData();
        PolicyType policy = (PolicyType) policyData;
        policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
        String policyNameValue =
                policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("OOF_") + 4);
        policyAdapter.setPolicyName(policyNameValue);
        String description = "";
        try {
            description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
        } catch (Exception e) {
            LOGGER.error("Error while collecting the description tag in " + policyNameValue, e);
            description = policy.getDescription();
        }
        policyAdapter.setPolicyDescription(description);
        // Get the target data under policy.
        TargetType target = policy.getTarget();
        if (target == null) {
            return;
        }
        for (AnyOfType anyOf : target.getAnyOf()) {
            for (AllOfType allOf : anyOf.getAllOf()) {
                for (MatchType match : allOf.getMatch()) {
                    //
                    // Under the match we have attribute value and
                    // attributeDesignator. So,finally down to the actual attribute.
                    //
                    policyAdapter.setupUsingAttribute(match.getAttributeDesignator().getAttributeId(),
                            (String) match.getAttributeValue().getContent().get(0));
                }
                readFile(policyAdapter, entity);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void readFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        String policyScopeName = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            OptimizationObject optimizationBody =
                    mapper.readValue(entity.getConfigurationData().getConfigBody(), OptimizationObject.class);
            policyScopeName = msController.getPolicyScope(optimizationBody.getPolicyScope());
            policyAdapter.setPolicyScope(policyScopeName);

            policyAdapter.setPriority(optimizationBody.getPriority());

            if (optimizationBody.getVersion() != null) {
                policyAdapter.setServiceType(optimizationBody.getService());
                policyAdapter.setVersion(optimizationBody.getVersion());
            } else {
                policyAdapter.setServiceType(optimizationBody.getService());
            }
            if (optimizationBody.getContent() != null) {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                LinkedHashMap<String, ?> map = (LinkedHashMap<String, ?>) optimizationBody.getContent();
                msController.readRecursivlyJSONContent(map, data);
                policyAdapter.setRuleData(data);
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }

    }

    /**
     * setModelData.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException IOException
     * @throws FileUploadException FileUploadException
     */
    @RequestMapping(
            value = {"/oof_dictionary/set_ModelData"},
            method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public void setModelData(HttpServletRequest request, HttpServletResponse response)
            throws IOException, FileUploadException {
        modelList = new ArrayList<>();
        dirDependencyList = new ArrayList<>();
        classMap = new LinkedHashMap<>();
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

        boolean zip = false;
        boolean yml = false;
        String errorMsg = "";
        for (FileItem item : items) {
            if (item.getName().endsWith(".zip") || item.getName().endsWith(".xmi") || item.getName().endsWith(".yml")) {
                this.newModel = new OptimizationModels();
                try {
                    File file = new File(item.getName());
                    OutputStream outputStream = new FileOutputStream(file);
                    IOUtils.copy(item.getInputStream(), outputStream);
                    outputStream.close();
                    this.newFile = file.toString();
                    this.newModel.setModelName(this.newFile.split("-v")[0]);

                    if (this.newFile.contains("-v")) {
                        if (item.getName().endsWith(".zip")) {
                            this.newModel.setVersion(this.newFile.split("-v")[1].replace(".zip", ""));
                            zip = true;
                        } else if (item.getName().endsWith(".yml")) {
                            this.newModel.setVersion(this.newFile.split("-v")[1].replace(".yml", ""));
                            yml = true;
                        } else {
                            this.newModel.setVersion(this.newFile.split("-v")[1].replace(".xmi", ""));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Upload error : ", e);
                    errorMsg = "Upload error:" + e.getMessage();
                }
            }

        }

        if (!errorMsg.isEmpty()) {
            response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            response.setContentType(PolicyUtils.APPLICATION_JSON);
            request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            response.getWriter().write(new JSONObject().put("errorMsg", errorMsg).toString());
            return;
        }

        List<File> fileList = new ArrayList<>();
        MsModelUtils modelUtil = new MsModelUtils();
        this.directory = MODEL;
        if (zip) {
            extractFolder(this.newFile);
            fileList = listModelFiles(this.directory);
        } else if (yml) {
            modelUtil.parseTosca(this.newFile);
        } else {
            File file = new File(this.newFile);
            fileList.add(file);
        }
        String modelType;
        if (!yml) {
            modelType = "xmi";
            // Process Main Model file first
            classMap = new LinkedHashMap<>();
            for (File file : fileList) {
                if (!file.isDirectory() && file.getName().endsWith(".xmi")) {
                    retrieveDependency(file.toString());
                }
            }

            modelList = createList();

            msController.cleanUp(this.newFile);
            msController.cleanUp(directory);
        } else {
            modelType = "yml";
            modelList.add(this.newModel.getModelName());
            String className = this.newModel.getModelName();
            MsAttributeObject optimizationAttributes = new MsAttributeObject();
            optimizationAttributes.setClassName(className);

            LinkedHashMap<String, String> returnAttributeList = new LinkedHashMap<>();
            returnAttributeList.put(className, modelUtil.getAttributeString());
            optimizationAttributes.setAttribute(returnAttributeList);

            optimizationAttributes.setSubClass(modelUtil.getRetmap());

            optimizationAttributes.setMatchingSet(modelUtil.getMatchableValues());

            LinkedHashMap<String, String> returnReferenceList = new LinkedHashMap<>();
            returnReferenceList.put(className, modelUtil.getReferenceAttributes());
            optimizationAttributes.setRefAttribute(returnReferenceList);

            if (!"".equals(modelUtil.getListConstraints())) {
                LinkedHashMap<String, String> enumList = new LinkedHashMap<>();
                String[] listArray = modelUtil.getListConstraints().split("#");
                for (String str : listArray) {
                    String[] strArr = str.split("=");
                    if (strArr.length > 1) {
                        enumList.put(strArr[0], strArr[1]);
                    }
                }
                optimizationAttributes.setEnumType(enumList);
            }

            classMap = new LinkedHashMap<>();
            classMap.put(className, optimizationAttributes);

        }

        response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
        response.setContentType(PolicyUtils.APPLICATION_JSON);
        request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);

        ObjectMapper mapper = new ObjectMapper();
        JSONObject json = new JSONObject();
        json.put("classListDatas", modelList);
        json.put("modelDatas", mapper.writeValueAsString(classMap));
        json.put("modelType", modelType);
        json.put("dataOrderInfo", modelUtil.getDataOrderInfo());

        response.getWriter().write(json.toString());
    }

    /*
     * Unzip file and store in the model directory for processing
     */
    @SuppressWarnings("rawtypes")
    private void extractFolder(String zipFile) {
        File file = new File(zipFile);

        try (ZipFile zip = new ZipFile(file)) {
            String newPath = MODEL + File.separator + zipFile.substring(0, zipFile.length() - 4);
            this.directory = MODEL + File.separator + zipFile.substring(0, zipFile.length() - 4);
            msController.checkZipDirectory(this.directory);
            new File(newPath).mkdir();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(MODEL + File.separator + currentEntry);
                File destinationParent = destFile.getParentFile();

                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    byte[] data = new byte[BUFFER];
                    try (FileOutputStream fos = new FileOutputStream(destFile);
                            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    } catch (IOException e) {
                        LOGGER.error("Failed to write zip contents to {}" + destFile + e);
                        //
                        // PLD should I throw e?
                        //
                        throw e;
                    }
                }

                if (currentEntry.endsWith(".zip")) {
                    extractFolder(destFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to unzip model file " + zipFile, e);
        }
    }

    private void retrieveDependency(String workingFile) {

        MsModelUtils utils = new MsModelUtils(PolicyController.getMsOnapName(), PolicyController.getMsPolicyName());
        Map<String, MsAttributeObject> tempMap;

        tempMap = utils.processEpackage(workingFile, ModelType.XMI);

        classMap.putAll(tempMap);
        LOGGER.info(tempMap);
    }

    private List<File> listModelFiles(String directoryName) {
        File fileDirectory = new File(directoryName);
        List<File> resultList = new ArrayList<>();
        for (File file : fileDirectory.listFiles()) {
            if (file.isFile()) {
                resultList.add(file);
            } else if (file.isDirectory()) {
                dirDependencyList.add(file.getName());
                resultList.addAll(listModelFiles(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    private List<String> createList() {
        List<String> list = new ArrayList<>();
        for (Entry<String, MsAttributeObject> entrySet : classMap.entrySet()) {
            if (entrySet.getValue().isPolicyTempalate()) {
                list.add(entrySet.getKey());
            }

        }

        if (list.isEmpty()) {
            if (classMap.containsKey(this.newModel.getModelName())) {
                list.add(this.newModel.getModelName());
            } else {
                list.add("EMPTY");
            }
        }
        return list;
    }

    public Map<String, String> getAttributesListRefMap() {
        return attributesListRefMap;
    }

    public Map<String, LinkedList<String>> getArrayTextList() {
        return arrayTextList;
    }

    private OptimizationObject setOptimizationObjectValues(PolicyRestAdapter policyAdapter) {
        OptimizationObject optimizationObject = new OptimizationObject();
        optimizationObject.setTemplateVersion(XACMLProperties.getProperty(XacmlRestProperties.TEMPLATE_VERSION_OOF));

        if (policyAdapter.getServiceType() != null) {
            optimizationObject.setService(policyAdapter.getServiceType());
            optimizationObject.setVersion(policyAdapter.getVersion());
        }
        if (policyAdapter.getPolicyName() != null) {
            optimizationObject.setPolicyName(policyAdapter.getPolicyName());
        }
        if (policyAdapter.getPolicyDescription() != null) {
            optimizationObject.setDescription(policyAdapter.getPolicyDescription());
        }
        if (policyAdapter.getPriority() != null) {
            optimizationObject.setPriority(policyAdapter.getPriority());
        } else {
            optimizationObject.setPriority("9999");
        }
        if (policyAdapter.getRiskLevel() != null) {
            optimizationObject.setRiskLevel(policyAdapter.getRiskLevel());
        }
        if (policyAdapter.getRiskType() != null) {
            optimizationObject.setRiskType(policyAdapter.getRiskType());
        }
        if (policyAdapter.getGuard() != null) {
            optimizationObject.setGuard(policyAdapter.getGuard());
        }
        return optimizationObject;
    }
}

@Getter
@Setter
class OptimizationObject {

    private String service;
    private String policyName;
    private String description;
    private String templateVersion;
    private String version;
    private String priority;
    private String policyScope;
    private String riskType;
    private String riskLevel;
    private String guard = null;
    private Object content;
}
