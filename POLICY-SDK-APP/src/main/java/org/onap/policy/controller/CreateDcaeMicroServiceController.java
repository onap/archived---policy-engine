/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.TreeMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.MicroserviceHeaderdeFaults;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.util.MSAttributeObject;
import org.onap.policy.rest.util.MSModelUtils;
import org.onap.policy.rest.util.MSModelUtils.MODEL_TYPE;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

@Controller
@RequestMapping("/")
public class CreateDcaeMicroServiceController extends RestrictedBaseController {
	private static final Logger LOGGER = FlexLogger.getLogger(CreateDcaeMicroServiceController.class);

	private static CommonClassDao commonClassDao;
	
	public static CommonClassDao getCommonClassDao() {
		return commonClassDao;
	}

	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		CreateDcaeMicroServiceController.commonClassDao = commonClassDao;
	}

	private MicroServiceModels newModel;
	private String newFile;
	private String directory;
	private List<String> modelList = new ArrayList<>();
	private List<String> dirDependencyList = new ArrayList<>();
	private LinkedHashMap<String,MSAttributeObject > classMap = new LinkedHashMap<>();
	String referenceAttributes;
	String attributeString;
	Set<String> allManyTrueKeys= new HashSet <>();

	public static final String DATATYPE  = "data_types.policy.data.";
	public static final String PROPERTIES=".properties.";
	public static final String TYPE=".type";
	public static final String STRING="string";
	public static final String INTEGER="integer";
	public static final String LIST="list";
	public static final String DEFAULT=".default";
	public static final String REQUIRED=".required";
	public static final String MATCHABLE=".matchable";
	public static final String MANYFALSE=":MANY-false";
	
	
	@Autowired
	private CreateDcaeMicroServiceController(CommonClassDao commonClassDao){
		CreateDcaeMicroServiceController.commonClassDao = commonClassDao;
	}

	public CreateDcaeMicroServiceController(){
		// Empty Constructor
	}

	protected PolicyRestAdapter policyAdapter = null;
	private int priorityCount; 
	private Map<String, String> attributesListRefMap =  new HashMap<>();
	private Map<String, LinkedList<String>> arrayTextList =  new HashMap<>();

	public PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter policyData, JsonNode root) {
		
		String jsonContent = null;
		try{
			LOGGER.info("policyJSON :" + (root.get("policyJSON")).toString());
			
			String tempJson = root.get("policyJSON").toString();
			
			//---replace empty value with the value below before calling decodeContent method.
			String dummyValue = "*empty-value*" + UUID.randomUUID().toString();
			LOGGER.info("dummyValue:" + dummyValue);
			tempJson = StringUtils.replaceEach(tempJson, new String[]{"\"\""}, new String[]{"\""+dummyValue+"\""});
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tempJsonNode = mapper.readTree(tempJson);
			jsonContent = decodeContent(tempJsonNode).toString();
			constructJson(policyData, jsonContent, dummyValue);
		}catch(Exception e){
			LOGGER.error("Error while decoding microservice content", e);
		}
		
		return policyData;
	}
	
	private GroupPolicyScopeList getPolicyObject(String policyScope) {
		return (GroupPolicyScopeList) commonClassDao.getEntityItem(GroupPolicyScopeList.class, "name", policyScope);
	}
	
	private PolicyRestAdapter constructJson(PolicyRestAdapter policyAdapter, String jsonContent, String dummyValue) {
		ObjectWriter om = new ObjectMapper().writer();
		String json="";
		DCAEMicroServiceObject microServiceObject = new DCAEMicroServiceObject();
		MicroServiceModels returnModel = new MicroServiceModels();
		microServiceObject.setTemplateVersion(XACMLProperties.getProperty(XACMLRestProperties.TemplateVersion_MS));
		if(policyAdapter.getServiceType() !=null){
			microServiceObject.setService(policyAdapter.getServiceType());
			microServiceObject.setVersion(policyAdapter.getVersion());
			returnModel = getAttributeObject(microServiceObject.getService(), microServiceObject.getVersion());
		}
		if (returnModel.getAnnotation()==null || returnModel.getAnnotation().isEmpty()){
			if(policyAdapter.getUuid()!=null){
				microServiceObject.setUuid(policyAdapter.getUuid());
			}
			if(policyAdapter.getLocation()!=null){
				microServiceObject.setLocation(policyAdapter.getLocation());
			} 
			if(policyAdapter.getConfigName()!=null){
				microServiceObject.setConfigName(policyAdapter.getConfigName());
			}
			GroupPolicyScopeList policyScopeValue = getPolicyObject(policyAdapter.getPolicyScope());
			if(policyScopeValue!=null){
				microServiceObject.setPolicyScope(policyScopeValue.getGroupList());	
			}
		}
		
		if(policyAdapter.getPolicyName()!=null){
			microServiceObject.setPolicyName(policyAdapter.getPolicyName());
		}
		if(policyAdapter.getPolicyDescription()!=null){
			microServiceObject.setDescription(policyAdapter.getPolicyDescription());
		}
		if (policyAdapter.getPriority()!=null){
			microServiceObject.setPriority(policyAdapter.getPriority());
		}else {
			microServiceObject.setPriority("9999");
		}
		
		if (policyAdapter.getRiskLevel()!=null){
			microServiceObject.setRiskLevel(policyAdapter.getRiskLevel());
		}
		if (policyAdapter.getRiskType()!=null){
			microServiceObject.setRiskType(policyAdapter.getRiskType());
		}
		if (policyAdapter.getGuard()!=null){
			microServiceObject.setGuard(policyAdapter.getGuard());
		}
		microServiceObject.setContent(jsonContent);
		
		try {
			json = om.writeValueAsString(microServiceObject);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error writing out the object", e);
		}
		LOGGER.info("input json: " + json);
		LOGGER.info("input jsonContent: " + jsonContent);
		String cleanJson = cleanUPJson(json);
        //--- reset empty value back after called cleanUPJson method and before calling removeNullAttributes
		String tempJson = StringUtils.replaceEach(cleanJson, new String[]{"\""+dummyValue+"\""},  new String[]{"\"\""});
		LOGGER.info("tempJson: " + tempJson);
		cleanJson = removeNullAttributes(tempJson);
		policyAdapter.setJsonBody(cleanJson);
		return policyAdapter;
	}
	
	public String removeNullAttributes(String cleanJson) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode rootNode = mapper.readTree(cleanJson);
			JsonNode returnNode = mapper.readTree(cleanJson);
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
			boolean remove = false;
			JsonObject removed = null;
			boolean contentChanged = false;
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> field = fieldsIterator.next();
				final String key = field.getKey();
				final JsonNode value = field.getValue();
				if("content".equalsIgnoreCase(key)){
					String contentStr = value.toString();
					try(JsonReader reader = Json.createReader(new StringReader(contentStr))){
	                        JsonObject jsonContent = reader.readObject();                 
							removed = removeNull(jsonContent);
							if(!jsonContent.toString().equals(removed.toString())){
							contentChanged = true;  
						}
                    }

					if  (value==null || value.isNull()){
						((ObjectNode) returnNode).remove(key);
						remove = true;
					}
				}
				if (remove){
					cleanJson = returnNode.toString();
				}
				if  (value==null || value.isNull()){
					((ObjectNode) returnNode).remove(key);
					remove = true;
				}
			}
			if (remove){
				cleanJson = returnNode.toString();
			}
			
			if(contentChanged){
				//set modified content to cleanJson
				JSONObject  jObject  =  new JSONObject(cleanJson);	
				jObject.put("content",removed.toString());
				cleanJson = cleanUPJson(jObject.toString());
			}
			
		} catch (IOException e) {
			LOGGER.error("Error writing out the JsonNode",e);
		}
		return cleanJson;
	}
	
	public static JsonArray removeNull(JsonArray array) {
	    JsonArrayBuilder builder = Json.createArrayBuilder();
	    int i = 0;
	    for (Iterator<JsonValue> it = array.iterator(); it.hasNext(); ++i) {
	        JsonValue value = it.next();
	        switch (value.getValueType()) {
	        case ARRAY:
	            JsonArray a = removeNull(array.getJsonArray(i));
	            if (!a.isEmpty())
	                builder.add(a);
	            break;
	        case OBJECT:
	            JsonObject object = removeNull(array.getJsonObject(i));
	            if (!object.isEmpty())
	                builder.add(object);
	            break;
	        case STRING:
	            String s = array.getString(i);
	            if (s != null && !s.isEmpty())
	                builder.add(s);
	            break;
	        case NUMBER:
	            builder.add(array.getJsonNumber(i));
	            break;
	        case TRUE:
	        case FALSE:
	            builder.add(array.getBoolean(i));
	            break;
	        case NULL:
	            break;
	        }
	    }
	    return builder.build();
	}

	public static JsonObject removeNull(JsonObject obj) {
	    JsonObjectBuilder builder = Json.createObjectBuilder();
	    for (Iterator<Entry<String, JsonValue>> it = obj.entrySet().iterator(); it.hasNext();) {
	        Entry<String, JsonValue> e = it.next();
	        String key = e.getKey();
	        JsonValue value = e.getValue();
	        switch (value.getValueType()) {
	        case ARRAY:
	            JsonArray array = removeNull(obj.getJsonArray(key));
	            if (!array.isEmpty())
	                builder.add(key, array);
	            break;
	        case OBJECT:
	            JsonObject object = removeNull(obj.getJsonObject(key));
	            if (!object.isEmpty())
	                builder.add(key, object);
	            break;
	        case STRING:
	            String s = obj.getString(key);
	            if (s != null && !s.isEmpty())
	                builder.add(key, s);
	            break;
	        case NUMBER:
	            builder.add(key, obj.getJsonNumber(key));
	            break;
	        case TRUE:
	        case FALSE:
	            builder.add(key, obj.getBoolean(key));
	            break;
	        case NULL:
	            break;
	        }
	    }
	    return builder.build();
	}
	
	public String cleanUPJson(String json) {
		String cleanJson = StringUtils.replaceEach(json, new String[]{"\\\\", "\\\\\\", "\\\\\\\\"}, new String[]{"\\", "\\", "\\"});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\\\\\\"}, new String[]{"\\"});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\\\\", "[[", "]]"}, new String[]{"\\", "[", "]"});
		
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\\\\\"", "\\\"", "\"[{", "}]\""}, new String[]{"\"", "\"", "[{", "}]"});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"[{", "}]\""}, new String[]{"[{", "}]"});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"[", "]\""}, new String[]{"[", "]"});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"{", "}\""}, new String[]{"{", "}"});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"\"\"", "\"\""}, new String[]{"\"", "\""});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\\\""}, new String[]{""});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"\""}, new String[]{"\""});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"\\\\\\"}, new String[]{"\""});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\\\\\\\""}, new String[]{"\""});
		cleanJson = StringUtils.replaceEach(cleanJson, new String[]{"\"[", "]\""}, new String[]{"[", "]"});
		return cleanJson;
	}
	
	public JSONObject decodeContent(JsonNode jsonNode){
		Iterator<JsonNode> jsonElements = jsonNode.elements();
		Iterator<String> jsonKeys = jsonNode.fieldNames();
		Map<String,String> element = new TreeMap<>();
		while(jsonElements.hasNext() && jsonKeys.hasNext()){
			element.put(jsonKeys.next(), jsonElements.next().toString());
		}
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonArray = null;
		String oldValue = null;
		String nodeKey = null;
		String arryKey = null;
		Boolean isArray = false;
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode node = nodeFactory.objectNode();
		String prevKey = null;
		String presKey;
		for(Entry<String, String> entry: element.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			if(key.contains(".")){
				presKey = key.substring(0,key.indexOf('.'));
			}else if(key.contains("@")){
				presKey = key.substring(0,key.indexOf('@'));
			}else{
				presKey = key;
			}
			// first check if we are different from old.
			LOGGER.info(key+"\n");
			if(jsonArray!=null && jsonArray.length()>0 && key.contains("@") && !key.contains(".") && oldValue!=null){
				if(!oldValue.equals(key.substring(0,key.indexOf('@')))){
					jsonResult.put(oldValue, jsonArray);
					jsonArray = new JSONArray();
				}
			}else if(jsonArray!=null && jsonArray.length()>0 && !presKey.equals(prevKey) && oldValue!=null){ 
				jsonResult.put(oldValue, jsonArray);
				isArray = false;
				jsonArray = new JSONArray();
			}
	
			prevKey = presKey;
			// 
			if(key.contains(".")){
				if(nodeKey==null){
					nodeKey = key.substring(0,key.indexOf('.'));
				}
				if(nodeKey.equals(key.substring(0,key.indexOf('.')))){
					node.put(key.substring(key.indexOf('.')+1), value);
				}else{
					if(node.size()!=0){
						if(nodeKey.contains("@")){
							if(arryKey==null){
								arryKey = nodeKey.substring(0,nodeKey.indexOf('@'));
							}
							if(nodeKey.endsWith("@0")){
								isArray = true;
								jsonArray = new JSONArray();
							}
							if(jsonArray != null && arryKey.equals(nodeKey.substring(0,nodeKey.indexOf('@')))){
								jsonArray.put(decodeContent(node));
							} 
							if((key.contains("@") && !arryKey.equals(key.substring(0,nodeKey.indexOf('@')))) || !key.contains("@")){
								jsonResult.put(arryKey, jsonArray);
								jsonArray = new JSONArray();
							}
							arryKey = nodeKey.substring(0,nodeKey.indexOf('@'));
						}else{
							isArray = false;
							jsonResult.put(nodeKey, decodeContent(node));
						}
						node = nodeFactory.objectNode();
					}
					nodeKey = key.substring(0,key.indexOf('.'));
					if(nodeKey.contains("@")){
						arryKey = nodeKey.substring(0,nodeKey.indexOf('@'));
					}
					node.put(key.substring(key.indexOf('.')+1), value);
				}
			}else {
				if(node.size()!=0){
					if(nodeKey.contains("@")){
						if(arryKey==null){
							arryKey = nodeKey.substring(0,nodeKey.indexOf('@'));
						}
						if(nodeKey.endsWith("@0")){
							isArray = true;
							jsonArray = new JSONArray();
						}
						if(jsonArray != null && arryKey.equals(nodeKey.substring(0,nodeKey.indexOf('@')))){
							jsonArray.put(decodeContent(node));
						}
						jsonResult.put(arryKey, jsonArray);
						jsonArray = new JSONArray();
						arryKey = nodeKey.substring(0,nodeKey.indexOf('@'));
					}else{
						isArray = false;
						jsonResult.put(nodeKey, decodeContent(node));
					}
					node = nodeFactory.objectNode();
				}
				if(key.contains("@")){
					isArray = true;
					if(key.endsWith("@0")|| jsonArray==null){
						jsonArray = new JSONArray();
					}
				}else if(!key.contains("@")){
					isArray = false;
				}
				if(isArray){
					if(oldValue==null){
						oldValue = key.substring(0,key.indexOf('@'));
					}
					if(oldValue!=prevKey){
						oldValue = key.substring(0,key.indexOf('@'));
					}
					if(oldValue.equals(key.substring(0,key.indexOf('@')))){
						jsonArray.put(value);
					}else{
						jsonResult.put(oldValue, jsonArray);
						jsonArray = new JSONArray();
					}
					oldValue = key.substring(0,key.indexOf('@'));
				}else{
					jsonResult.put(key, value);
				}
			}
		}
		if(node.size()>0){
			if(nodeKey.contains("@")){
				if(jsonArray==null){
					jsonArray = new JSONArray();
				}
				if(arryKey==null){
					arryKey = nodeKey.substring(0,nodeKey.indexOf('@'));
				}
				jsonArray.put(decodeContent(node));
				jsonResult.put(arryKey, jsonArray);
				isArray = false;
			}else{
				jsonResult.put(nodeKey, decodeContent(node));
			}
		}
		if(isArray && jsonArray.length() > 0){
			jsonResult.put(oldValue, jsonArray);
		}
		return jsonResult;
	}
	
	@RequestMapping(value={"/policyController/getDCAEMSTemplateData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView getDCAEMSTemplateData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());

		String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
		String  servicename = value.split("-v")[0];
		String version = null;
		if (value.contains("-v")){
			version = value.split("-v")[1];
		}
		MicroServiceModels returnModel = getAttributeObject(servicename, version);
		
		MicroserviceHeaderdeFaults returnHeaderDefauls = getHeaderDefaultsObject(value);
		JSONObject jsonHdDefaultObj = null;
		if(returnHeaderDefauls != null){
			jsonHdDefaultObj = new JSONObject();
			jsonHdDefaultObj.put("onapName", returnHeaderDefauls.getOnapName());
			jsonHdDefaultObj.put("guard", returnHeaderDefauls.getGuard());
			jsonHdDefaultObj.put("riskLevel", returnHeaderDefauls.getRiskLevel());
			jsonHdDefaultObj.put("riskType", returnHeaderDefauls.getRiskType());
			jsonHdDefaultObj.put("priority", returnHeaderDefauls.getPriority());
		}
		String headDefautlsData = "";
		if(jsonHdDefaultObj != null){			
			headDefautlsData = jsonHdDefaultObj.toString();
		}else{
			headDefautlsData = "null";
		}
		
		//Get all keys with "MANY-true" defined in their value from subAttribute 
		Set<String> allkeys = null;
		if(returnModel.getSub_attributes() != null && !returnModel.getSub_attributes().isEmpty()){
			JSONObject json = new JSONObject(returnModel.getSub_attributes());	
			getAllKeys(json); 
			allkeys = allManyTrueKeys;
			allManyTrueKeys = new  HashSet <>();
			LOGGER.info("allkeys : " + allkeys);
		}
		
		//Get element order info 
		String dataOrderInfo = returnModel.getDataOrderInfo();
        if(dataOrderInfo != null && !dataOrderInfo.startsWith("\"")){
            dataOrderInfo = "\"" + dataOrderInfo + "\"";
        }
        LOGGER.info("dataOrderInfo : " + dataOrderInfo);
        
		String allManyTrueKeys = "";
		if(allkeys != null){
			allManyTrueKeys = allkeys.toString();
		}
		
		String jsonModel = createMicroSeriveJson(returnModel, allkeys);
		
		JSONObject jsonObject = new JSONObject(jsonModel);
		
		JSONObject finalJsonObject = null;
		if(allkeys != null){
			Iterator<String> iter = allkeys.iterator();
			while(iter.hasNext()){
				//Convert to array values for MANY-true keys
				finalJsonObject = convertToArrayElement(jsonObject, iter.next());
			}
		}

		if(finalJsonObject != null){
		    LOGGER.info(finalJsonObject.toString());
		    jsonModel  = finalJsonObject.toString();
		}
		
		//get all properties with "MANY-true" defined in Ref_attributes
		Set<String> manyTrueProperties = getManyTrueProperties(returnModel.getRef_attributes());
		if(manyTrueProperties != null){
			JSONObject jsonObj = new JSONObject(jsonModel);
			for (String s : manyTrueProperties) {
				LOGGER.info(s);
				//convert to array element for MANY-true properties
				finalJsonObject = convertToArrayElement(jsonObj, s.trim());
			}
			
			if(finalJsonObject != null){
			    LOGGER.info(finalJsonObject.toString());
			    jsonModel = finalJsonObject.toString();
			}
		}
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");
		List<Object>  list = new ArrayList<>();
		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(returnModel);

		JSONObject j = null;
		
		if("".equals(allManyTrueKeys)){
			j = new JSONObject("{dcaeModelData: " + responseString + ",jsonValue: " + jsonModel + ",dataOrderInfo:" + dataOrderInfo + ",headDefautlsData:" + headDefautlsData +"}");	
		}else{
			j = new JSONObject("{dcaeModelData: " + responseString + ",jsonValue: " + jsonModel + ",allManyTrueKeys: " + allManyTrueKeys+",dataOrderInfo:" + dataOrderInfo + ",headDefautlsData:" + headDefautlsData+ "}");	
		}
		list.add(j);
		out.write(list.toString());
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String createMicroSeriveJson(MicroServiceModels returnModel, Set<String> allkeys) {
		Map<String, String> attributeMap = new HashMap<>();
		Map<String, String> refAttributeMap = new HashMap<>();
		String attribute = returnModel.getAttributes();
		if(attribute != null){
			attribute = attribute.trim();
		}
		String refAttribute = returnModel.getRef_attributes();
		if(refAttribute != null){
			refAttribute = refAttribute.trim();
		}
		String enumAttribute = returnModel.getEnumValues();
		if(enumAttribute != null){
			enumAttribute = enumAttribute.trim();
		}
		if (!StringUtils.isEmpty(attribute)){
			attributeMap = convert(attribute, ",");
		}
		if (!StringUtils.isEmpty(refAttribute)){
			refAttributeMap = convert(refAttribute, ",");
		}

		Gson gson = new Gson();
		
		String subAttributes = returnModel.getSub_attributes();
		if(subAttributes != null){
			subAttributes = subAttributes.trim();
		}else{
			subAttributes = "";
		}
		
		Map gsonObject = (Map) gson.fromJson(subAttributes, Object.class);
		
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		
		for (Entry<String, String> keySet : attributeMap.entrySet()){
			array = new JSONArray();
			String value = keySet.getValue();
			if ("true".equalsIgnoreCase(keySet.getValue().split("MANY-")[1])){
				array.put(value);
				object.put(keySet.getKey().trim(), array);
			}else {
				object.put(keySet.getKey().trim(), value.trim());
			}
		}
		
		for (Entry<String, String> keySet : refAttributeMap.entrySet()){
			array = new JSONArray();
			String value = keySet.getValue().split(":")[0];
			if (gsonObject.containsKey(value)){
				if ("true".equalsIgnoreCase(keySet.getValue().split("MANY-")[1])){
					array.put(recursiveReference(value, gsonObject, enumAttribute));
					object.put(keySet.getKey().trim(), array);
				}else {
					object.put(keySet.getKey().trim(), recursiveReference(value, gsonObject, enumAttribute));
				}
			}else {
				if ("true".equalsIgnoreCase(keySet.getValue().split("MANY-")[1])){
					array.put(value.trim());
					object.put(keySet.getKey().trim(), array);
				}else {
					object.put(keySet.getKey().trim(), value.trim()); 
				}
			}
		}

		return object.toString();
	}

	@SuppressWarnings("unchecked")
	private JSONObject recursiveReference(String name, Map<String,String> subAttributeMap, String enumAttribute) {
		JSONObject object = new JSONObject();
		Map<String, String> map;
		Object returnClass = subAttributeMap.get(name);
		map = (Map<String, String>) returnClass; 
		JSONArray array;
		
		for( Entry<String, String> m:map.entrySet()){  
			String[] splitValue = m.getValue().split(":");
			array = new JSONArray();
			if (subAttributeMap.containsKey(splitValue[0])){
				if ("true".equalsIgnoreCase(m.getValue().split("MANY-")[1])){
					array.put(recursiveReference(splitValue[0], subAttributeMap, enumAttribute));
					object.put(m.getKey().trim(), array);
				}else {
					object.put(m.getKey().trim(), recursiveReference(splitValue[0], subAttributeMap, enumAttribute));
				}
			} else{
				if ("true".equalsIgnoreCase(m.getValue().split("MANY-")[1])){
					array.put(splitValue[0].trim());
					object.put(m.getKey().trim(), array);
				}else {
					object.put(m.getKey().trim(), splitValue[0].trim());
				}
			}
		  }  
		
		return object;
	}
	
	
	public static JSONObject convertToArrayElement(JSONObject json, String keyValue) {
	    return convertToArrayElement(json, new HashSet<>(), keyValue);
	}
	
	private static JSONObject convertToArrayElement(JSONObject json, Set<String> keys, String keyValue) {
	    for (String key : json.keySet()) {
	        Object obj = json.get(key);
	        if(key.equals(keyValue.trim())){
	        	if(!(obj instanceof JSONArray)){
	        		JSONArray newJsonArray = new JSONArray();
	        		newJsonArray.put(obj);
	        		json.put(key, newJsonArray);
	        	}
	        	LOGGER.info("key : " + key);
	        	LOGGER.info("obj : " + obj);
	        	LOGGER.info("json.get(key) : " + json.get(key));
	        	LOGGER.info("keyValue : " + keyValue);
	    	    keys.addAll(json.keySet());
	    	    
	    	    return json;
	        }

	        if (obj instanceof JSONObject) convertToArrayElement(json.getJSONObject(key), keyValue);	    
	    }

	    return json;
	}
	
	// call this method to get all MANY-true properties 
	public static Set<String> getManyTrueProperties(String referAttributes){
		LOGGER.info("referAttributes : " + referAttributes);
		Set<String> manyTrueProperties = new HashSet<>();
		
		if(referAttributes != null){
			String[] referAarray = referAttributes.split(",");
			String []element= null;
			for(int i=0; i<referAarray.length; i++){
				element = referAarray[i].split("=");	  
				if(element.length > 1 && element[1].contains("MANY-true")){
					manyTrueProperties.add(element[0]);
				}
			}		
		}
		
		return manyTrueProperties;
	}
	
	//call this method to start the recursive
	private Set<String> getAllKeys(JSONObject json) {
	    return getAllKeys(json, new HashSet<>());
	}

	private Set<String> getAllKeys(JSONArray arr) {
	    return getAllKeys(arr, new HashSet<>());
	}

	private Set<String> getAllKeys(JSONArray arr, Set<String> keys) {
	    for (int i = 0; i < arr.length(); i++) {
	        Object obj = arr.get(i);
	        if (obj instanceof JSONObject) keys.addAll(getAllKeys(arr.getJSONObject(i)));
	        if (obj instanceof JSONArray) keys.addAll(getAllKeys(arr.getJSONArray(i)));
	    }

	    return keys;
	}
	
    // this method returns a set of keys with "MANY-true" defined in their value.
	private Set<String> getAllKeys(JSONObject json, Set<String> keys) {
	    for (String key : json.keySet()) {
	        Object obj = json.get(key);
	        if(obj instanceof String && ((String) obj).contains("MANY-true")){
	        	LOGGER.info("key : " + key);
	        	LOGGER.info("obj : " + obj);
	        	allManyTrueKeys.add(key);
	        }
	        if (obj instanceof JSONObject) keys.addAll(getAllKeys(json.getJSONObject(key)));
	        if (obj instanceof JSONArray) keys.addAll(getAllKeys(json.getJSONArray(key)));
	    }

	    return keys;
	}

	
	@RequestMapping(value={"/policyController/getModelServiceVersioneData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView getModelServiceVersionData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());

		String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
		String  servicename = value.split("-v")[0];
		Set<String> returnList = getVersionList(servicename);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");
        List<Object>  list = new ArrayList<>();
		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(returnList);
		JSONObject j = new JSONObject("{dcaeModelVersionData: " + responseString +"}");
		list.add(j);
		out.write(list.toString());
		return null;
	}

	private Set<String> getVersionList(String name) {	
		MicroServiceModels workingModel;
		Set<String> list = new HashSet<>();
		List<Object> microServiceModelsData = commonClassDao.getDataById(MicroServiceModels.class, "modelName", name);
		for (int i = 0; i < microServiceModelsData.size(); i++) {
			workingModel = (MicroServiceModels) microServiceModelsData.get(i);
			if (workingModel.getVersion()!=null){
				list.add(workingModel.getVersion());
			}else{
				list.add("Default");
			}
		}
		return list;
	}
	
	private MicroServiceModels getAttributeObject(String name, String version) {	
		MicroServiceModels workingModel = new MicroServiceModels();
		List<Object> microServiceModelsData = commonClassDao.getDataById(MicroServiceModels.class, "modelName", name);
		for (int i = 0; i < microServiceModelsData.size(); i++) {
			workingModel = (MicroServiceModels) microServiceModelsData.get(i);
			if(version != null){
				if (workingModel.getVersion()!=null){
					if (workingModel.getVersion().equals(version)){
						return workingModel;
					}
				}else{
					return workingModel;
				}
			}else{
				return workingModel;
			}
			
		}
		return workingModel;
	}
	
	private MicroserviceHeaderdeFaults getHeaderDefaultsObject(String modelName) {	
		return (MicroserviceHeaderdeFaults) commonClassDao.getEntityItem(MicroserviceHeaderdeFaults.class, "modelName", modelName);
	}	

	@RequestMapping(value={"/get_DCAEPriorityValues"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDCAEPriorityValuesData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			List<String> priorityList = new ArrayList<>();
			priorityCount = 10;
			for (int i = 1; i < priorityCount; i++) {
				priorityList.add(String.valueOf(i));
			}
			model.put("priorityDatas", mapper.writeValueAsString(priorityList));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e);
		}
	}

	public void prePopulateDCAEMSPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("MS_") +3);
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
			    LOGGER.error("Error while collecting the desciption tag in ActionPolicy " + policyNameValue ,e);
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			// Get the target data under policy.
			TargetType target = policy.getTarget();
			if (target != null) {
				// Under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AnyOFType we have AllOFType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (matchList.size()>1 && iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attribute value and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();
										// First match in the target is OnapName, so set that value.
										if ("ONAPName".equals(attributeId)) {
											policyAdapter.setOnapName(value);
										}
										if ("ConfigName".equals(attributeId)){
											policyAdapter.setConfigName(value);
										}
										if ("uuid".equals(attributeId)){
											policyAdapter.setUuid(value);
										}
										if ("location".equals(attributeId)){
											policyAdapter.setLocation(value);
										}
										if ("RiskType".equals(attributeId)){
											policyAdapter.setRiskType(value);
										}
										if ("RiskLevel".equals(attributeId)){
											policyAdapter.setRiskLevel(value);
										}
										if ("guard".equals(attributeId)){
											policyAdapter.setGuard(value);
										}
										if ("TTLDate".equals(attributeId) && !value.contains("NA")){
											PolicyController controller = new PolicyController();
											String newDate = controller.convertDate(value);
											policyAdapter.setTtlDate(newDate);
										}
									}
									readFile(policyAdapter, entity);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static Map<String, String> convert(String str, String split) {
		Map<String, String> map = new HashMap<>();
		for(final String entry : str.split(split)) {
		    String[] parts = entry.split("=");
		    map.put(parts[0], parts[1]);
		}
		return map;
	}


	@SuppressWarnings("unchecked")
	private void readFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		String policyScopeName = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			DCAEMicroServiceObject msBody = mapper.readValue(entity.getConfigurationData().getConfigBody(), DCAEMicroServiceObject.class);
			policyScopeName = getPolicyScope(msBody.getPolicyScope());
			policyAdapter.setPolicyScope(policyScopeName);

			policyAdapter.setPriority(msBody.getPriority());

			if (msBody.getVersion()!= null){
				policyAdapter.setServiceType(msBody.getService());
				policyAdapter.setVersion(msBody.getVersion());
			}else{
				policyAdapter.setServiceType(msBody.getService());
			}
			if(msBody.getContent() != null){
				LinkedHashMap<String, Object>  data = new LinkedHashMap<>();
				LinkedHashMap<String, ?> map = (LinkedHashMap<String, ?>) msBody.getContent();
				readRecursivlyJSONContent(map, data);
				policyAdapter.setRuleData(data);
			}

		} catch (Exception e) {
			LOGGER.error(e);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public void readRecursivlyJSONContent(LinkedHashMap<String, ?> map, LinkedHashMap<String, Object> data){
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			Object key =  iterator.next();
			Object value = map.get(key);
			if(value instanceof LinkedHashMap<?, ?>){
				LinkedHashMap<String, Object> secondObjec = new LinkedHashMap<>(); 
				readRecursivlyJSONContent((LinkedHashMap<String, ?>) value, secondObjec);
				for( Entry<String, Object> entry : secondObjec.entrySet()){
					data.put(key+"." + entry.getKey(), entry.getValue());
				}
			}else if(value instanceof ArrayList){
				ArrayList<?> jsonArrayVal = (ArrayList<?>)value;
				for(int i = 0; i < jsonArrayVal.size(); i++){
					Object arrayvalue = jsonArrayVal.get(i);
					if(arrayvalue instanceof LinkedHashMap<?, ?>){
						LinkedHashMap<String, Object> newData = new LinkedHashMap<>();   
						readRecursivlyJSONContent((LinkedHashMap<String, ?>) arrayvalue, newData);
						for(Entry<String, Object> entry: newData.entrySet()){
							data.put(key+"@"+i+"." +entry.getKey(), entry.getValue());
						}
					}else if(arrayvalue instanceof ArrayList){
						ArrayList<?> jsonArrayVal1 = (ArrayList<?>)value;
						for(int j = 0; j < jsonArrayVal1.size(); j++){
							Object arrayvalue1 = jsonArrayVal1.get(i);
							data.put(key+"@"+j, arrayvalue1.toString());
						}	
					}else{
						data.put(key+"@"+i, arrayvalue.toString());
					}	
				}
			}else{
				data.put(key.toString(), value.toString());
			}	
		}
	}

	public String getPolicyScope(String value) {
		List<Object> groupList= commonClassDao.getDataById(GroupPolicyScopeList.class, "groupList", value);
		if(groupList != null && !groupList.isEmpty()){
			GroupPolicyScopeList pScope = (GroupPolicyScopeList) groupList.get(0);
			return pScope.getGroupName();		
		}
		return null;
	}

	//Convert the map values and set into JSON body
	public Map<String, String> convertMap(Map<String, String> attributesMap, Map<String, String> attributesRefMap) {
		Map<String, String> attribute = new HashMap<>();
		StringBuilder temp;
		String key;
		String value;
		for (Entry<String, String> entry : attributesMap.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			attribute.put(key, value);
		}
		for (Entry<String, String> entryRef : attributesRefMap.entrySet()) {
			key = entryRef.getKey();
			value = entryRef.getValue();
			attribute.put(key, value);
		}
		for (Entry<String, String> entryList : attributesListRefMap.entrySet()) {
			key = entryList.getKey();
			value = entryList.getValue();
			attribute.put(key, value);
		}
		for (Entry<String, LinkedList<String>> arrayList : arrayTextList.entrySet()){
			key = arrayList.getKey();
			temp = null;
			for (Object textList : arrayList.getValue()){
				if (temp == null){
					temp = new StringBuilder();
					temp.append("[" + textList);
				}else{
					temp.append("," + textList);
				}
			}
			attribute.put(key, temp+ "]");			
		}

		return  attribute;
	}
	
	@RequestMapping(value={"/ms_dictionary/set_MSModelData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void SetMSModelData(HttpServletRequest request, HttpServletResponse response) throws IOException, FileUploadException{
		modelList = new ArrayList<>();
		dirDependencyList = new ArrayList<>();
		classMap = new LinkedHashMap<>();
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		boolean zip = false;
		boolean yml= false;
		String errorMsg = "";
		for (FileItem item : items) {
			if(item.getName().endsWith(".zip") || item.getName().endsWith(".xmi")||item.getName().endsWith(".yml")){
				this.newModel = new MicroServiceModels();
				try{
					File file = new File(item.getName());
					OutputStream outputStream = new FileOutputStream(file);
					IOUtils.copy(item.getInputStream(), outputStream);
					outputStream.close();
					this.newFile = file.toString();
					this.newModel.setModelName(this.newFile.split("-v")[0]);
				
					if (this.newFile.contains("-v")){
						if (item.getName().endsWith(".zip")){
							this.newModel.setVersion(this.newFile.split("-v")[1].replace(".zip", ""));
							zip = true;
						}else if(item.getName().endsWith(".yml")){
							this.newModel.setVersion(this.newFile.split("-v")[1].replace(".yml", ""));
							yml = true;
						}
						else {
							this.newModel.setVersion(this.newFile.split("-v")[1].replace(".xmi", ""));
						}
					}				
				}catch(Exception e){
					LOGGER.error("Upload error : ", e);
					errorMsg = "Upload error:" + e.getMessage();
				}
			}
			
		}
		
		if(!errorMsg.isEmpty()){
			
			PrintWriter out = response.getWriter();
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			JSONObject j = new JSONObject();
			j.put("errorMsg", errorMsg);
			out.write(j.toString());
			return;
		}
		
		List<File> fileList = new ArrayList<>();
		MSModelUtils msMLUtils =  new MSModelUtils();
		this.directory = "model";
		if (zip){
			extractFolder(this.newFile);
			fileList = listModelFiles(this.directory);
		}else if (yml==true){
			
			errorMsg = msMLUtils.parseTosca(this.newFile);
			if(errorMsg != null){
				PrintWriter out = response.getWriter();				
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");
				JSONObject j = new JSONObject();
				j.put("errorMsg", errorMsg);
			    out.write(j.toString());
			    return;
			}
			
		}else {
			File file = new File(this.newFile);
			fileList.add(file);
		}
		String modelType= "";
		if(!yml){
			modelType="xmi";
			//Process Main Model file first
			classMap = new LinkedHashMap<>();
			for (File file : fileList) {
				if(!file.isDirectory() && file.getName().endsWith(".xmi")){
	            	retreiveDependency(file.toString(), true);
	            }	
			}
			
			modelList = createList();
			
			cleanUp(this.newFile);
			cleanUp(directory);
		}else{
			modelType="yml";
			modelList.add(this.newModel.getModelName());
			String className=this.newModel.getModelName();
			MSAttributeObject msAttributes= new MSAttributeObject();
			msAttributes.setClassName(className);
			
			LinkedHashMap<String, String> returnAttributeList =new LinkedHashMap<>();
			returnAttributeList.put(className, msMLUtils.getAttributeString());
			msAttributes.setAttribute(returnAttributeList);
			
			msAttributes.setSubClass(msMLUtils.getRetmap());
			
			msAttributes.setMatchingSet(msMLUtils.getMatchableValues());
			
			LinkedHashMap<String, String> returnReferenceList =new LinkedHashMap<>();

			returnReferenceList.put(className, msMLUtils.getReferenceAttributes());
			msAttributes.setRefAttribute(returnReferenceList);
			
			if(msMLUtils.getListConstraints()!=""){
				LinkedHashMap<String, String> enumList =new LinkedHashMap<>();
				String[] listArray=msMLUtils.getListConstraints().split("#");
                for(String str:listArray){
                    String[] strArr= str.split("=");
                    if(strArr.length>1){
                        enumList.put(strArr[0], strArr[1]);
                    }
                }
				msAttributes.setEnumType(enumList);
			}
			
			classMap=new LinkedHashMap<>();
			classMap.put(className, msAttributes);
			
		}
		
		PrintWriter out = response.getWriter();
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		JSONObject j = new JSONObject();
		j.put("classListDatas", modelList);
		j.put("modelDatas", mapper.writeValueAsString(classMap));
		j.put("modelType", modelType);
		j.put("dataOrderInfo", msMLUtils.getDataOrderInfo());
		
		out.write(j.toString());
	}
	
	/*
	 * Unzip file and store in the model directory for processing
	 */
	@SuppressWarnings("rawtypes")
	private void extractFolder(String zipFile )  {
	    int BUFFER = 2048;
	    File file = new File(zipFile);

		try (ZipFile zip = new ZipFile(file)) {
		    String newPath =  "model" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    this.directory = "model" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    checkZipDirectory(this.directory);
		    new File(newPath).mkdir();
		    Enumeration zipFileEntries = zip.entries();
	
		    // Process each entry
		    while (zipFileEntries.hasMoreElements()){
		        // grab a zip file entry
		        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
		        String currentEntry = entry.getName();
		        File destFile = new File("model" + File.separator + currentEntry);
		        File destinationParent = destFile.getParentFile();
		        
		        destinationParent.mkdirs();
	
		        if (!entry.isDirectory()){
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
	
		        if (currentEntry.endsWith(".zip")){
		            extractFolder(destFile.getAbsolutePath());
		        }
		    }
	    } catch (IOException e) {
	    	LOGGER.error("Failed to unzip model file " + zipFile, e);
		}
	}
	
	private void retreiveDependency(String workingFile, Boolean modelClass) {
		
		MSModelUtils utils = new MSModelUtils(PolicyController.getMsOnapName(), PolicyController.getMsPolicyName());
	    Map<String, MSAttributeObject> tempMap;
	    
	    tempMap = utils.processEpackage(workingFile, MODEL_TYPE.XMI);
	    
	    classMap.putAll(tempMap);
	    LOGGER.info(tempMap);
	    
	    return;   	
	    
	}
		
	private List<File> listModelFiles(String directoryName) {
		File fileDirectory = new File(directoryName);
		List<File> resultList = new ArrayList<>();
		File[] fList = fileDirectory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				resultList.add(file);
			} else if (file.isDirectory()) {
				dirDependencyList.add(file.getName());
				resultList.addAll(listModelFiles(file.getAbsolutePath()));
			}
		}
		return resultList;
	}
	
    public void cleanUp(String path) {
        if (path!=null){
            try {
                FileUtils.forceDelete(new File(path));
            } catch (IOException e) {
            	LOGGER.error("Failed to delete folder " + path, e);
            }  
        }
    }
 
    public void checkZipDirectory(String zipDirectory) {
        Path path = Paths.get(zipDirectory);
 
        if (Files.exists(path)) {
            cleanUp(zipDirectory);
        }
    }
	
    private List<String> createList() {
        List<String> list = new  ArrayList<>();
        for (Entry<String, MSAttributeObject> cMap : classMap.entrySet()){
            if (cMap.getValue().isPolicyTempalate()){
                list.add(cMap.getKey());
            }
            
        }
        
        if (list.isEmpty()){
            if (classMap.containsKey(this.newModel.getModelName())){
                list.add(this.newModel.getModelName());
            }else {
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

}

class DCAEMicroServiceObject {

	private String service;
	private String location;
	private String uuid;
	private String policyName;
	private String description;
	private String configName;
	private String templateVersion;
	private String version;
	private String priority;
	private String policyScope;
	private String riskType;
	private String riskLevel; 
	private String guard = null;

	public String getGuard() {
		return guard;
	}
	public void setGuard(String guard) {
		this.guard = guard;
	}
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getPolicyScope() {
		return policyScope;
	}
	public void setPolicyScope(String policyScope) {
		this.policyScope = policyScope;
	}

	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	private Object content;


	public String getPolicyName() {
		return policyName;
	}
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}

	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTemplateVersion() {
		return templateVersion;
	}
	public void setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
	}

}