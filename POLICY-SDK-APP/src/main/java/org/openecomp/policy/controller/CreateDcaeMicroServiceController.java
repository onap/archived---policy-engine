/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.policy.controller;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.GroupPolicyScopeList;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.util.MSAttributeObject;
import org.openecomp.policy.rest.util.MSModelUtils;
import org.openecomp.policy.rest.util.MSModelUtils.MODEL_TYPE;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.yaml.snakeyaml.Yaml;

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
	private MicroServiceModels newModel;
	private String newFile;
	private String directory;
	private List<String> modelList = new ArrayList<>();
	private List<String> dirDependencyList = new ArrayList<>();
	private HashMap<String,MSAttributeObject > classMap = new HashMap<>();
	//Tosca Model related Datastructure. 
	String referenceAttributes;
	String attributeString;
	String listConstraints;
	String subAttributeString;
	HashMap<String, Object> retmap = new HashMap<>();
	Set<String> uniqueKeys= new HashSet<>();
	Set<String> uniqueDataKeys= new HashSet<>();
	
	@Autowired
	private CreateDcaeMicroServiceController(CommonClassDao commonClassDao){
		CreateDcaeMicroServiceController.commonClassDao = commonClassDao;
	}

	public CreateDcaeMicroServiceController(){}

	protected PolicyRestAdapter policyAdapter = null;
	private int priorityCount; 
	private Map<String, String> attributesListRefMap =  new HashMap<>();
	private Map<String, LinkedList<String>> arrayTextList =  new HashMap<>();

	public PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter policyData, JsonNode root) {
		
		String jsonContent = null;
		try{
			jsonContent = decodeContent(root.get("policyJSON")).toString();
			constructJson(policyData, jsonContent);
		}catch(Exception e){
			LOGGER.error("Error while decoding microservice content");
		}
		
		return policyData;
	}
	
	private GroupPolicyScopeList getPolicyObject(String policyScope) {
		GroupPolicyScopeList groupList= (GroupPolicyScopeList) commonClassDao.getEntityItem(GroupPolicyScopeList.class, "name", policyScope);
		return groupList;
	}
	
	private PolicyRestAdapter constructJson(PolicyRestAdapter policyAdapter, String jsonContent) {
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
			LOGGER.error("Error writing out the object");
		}
		LOGGER.info(json);
		String cleanJson = cleanUPJson(json);
		cleanJson = removeNullAttributes(cleanJson);
		policyAdapter.setJsonBody(cleanJson);
		return policyAdapter;
	}
	
	private String removeNullAttributes(String cleanJson) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode rootNode = mapper.readTree(cleanJson);
			JsonNode returnNode = mapper.readTree(cleanJson);
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
			boolean remove = false;
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> field = fieldsIterator.next();
				final String key = field.getKey();
				final JsonNode value = field.getValue();
				if  (value==null || value.isNull()){
					((ObjectNode) returnNode).remove(key);
					remove = true;
				}
			}
			if (remove){
				cleanJson = returnNode.toString();
			}
		} catch (IOException e) {
			LOGGER.error("Error writing out the JsonNode");
		}
		return cleanJson;
	}
	
	// Second index of dot should be returned. 
	public void stringBetweenDots(String str,String value){
		String stringToSearch=str;
		String[]ss=stringToSearch.split("\\.");
		if(ss!=null){
			int len= ss.length;
			if(len>2){
				uniqueKeys.add(ss[2]);
			}
		}
	}
	
	public void stringBetweenDotsForDataFields(String str,String value){
		String stringToSearch=str;
		String[]ss=stringToSearch.split("\\.");
		if(ss!=null){
			int len= ss.length;

			if(len>2){
				uniqueDataKeys.add(ss[0]+"%"+ss[2]);
			}
		}
	}
	
 
	public Map<String, String> load(String fileName) throws IOException { 
		File newConfiguration = new File(fileName);
		InputStream is = null;
		try {
			is = new FileInputStream(newConfiguration);
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		}

		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		Map<Object, Object> yamlMap = (Map<Object, Object>) yaml.load(is); 
		StringBuilder sb = new StringBuilder(); 
		Map<String, String> settings = new HashMap<>(); 
		if (yamlMap == null) { 
			return settings; 
		} 
		List<String> path = new ArrayList<String>(); 
		serializeMap(settings, sb, path, yamlMap); 
		return settings; 
	} 

	public Map<String, String> load(byte[] source) throws IOException { 
		Yaml yaml = new Yaml(); 
		@SuppressWarnings("unchecked")
		Map<Object, Object> yamlMap = (Map<Object, Object>) yaml.load(source.toString()); 
		StringBuilder sb = new StringBuilder(); 
		Map<String, String> settings = new HashMap<String, String>(); 
		if (yamlMap == null) { 
			return settings; 
		} 
		List<String> path = new ArrayList<String>(); 
		serializeMap(settings, sb, path, yamlMap); 
		return settings; 
	} 

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void serializeMap(Map<String, String> settings, StringBuilder sb, List<String> path, Map<Object, Object> yamlMap) { 
		for (Map.Entry<Object, Object> entry : yamlMap.entrySet()) { 
			if (entry.getValue() instanceof Map) { 
				path.add((String) entry.getKey()); 
				serializeMap(settings, sb, path, (Map<Object, Object>) entry.getValue()); 
				path.remove(path.size() - 1); 
			} else if (entry.getValue() instanceof List) { 
				path.add((String) entry.getKey()); 
				serializeList(settings, sb, path, (List) entry.getValue()); 
				path.remove(path.size() - 1); 
			} else { 
				serializeValue(settings, sb, path, (String) entry.getKey(), entry.getValue()); 
			} 
		} 
	} 

	@SuppressWarnings("unchecked")
	private void serializeList(Map<String, String> settings, StringBuilder sb, List<String> path, List<String> yamlList) { 
		int counter = 0; 
		for (Object listEle : yamlList) { 
			if (listEle instanceof Map) { 
				path.add(Integer.toString(counter)); 
				serializeMap(settings, sb, path, (Map<Object, Object>) listEle); 
				path.remove(path.size() - 1); 
			} else if (listEle instanceof List) { 
				path.add(Integer.toString(counter)); 
				serializeList(settings, sb, path, (List<String>) listEle); 
				path.remove(path.size() - 1); 
			} else { 
				serializeValue(settings, sb, path, Integer.toString(counter), listEle); 
			} 
			counter++; 
		} 
	} 

	private void serializeValue(Map<String, String> settings, StringBuilder sb, List<String> path, String name, Object value) { 
		if (value == null) { 
			return; 
		} 
		sb.setLength(0); 
		for (String pathEle : path) { 
			sb.append(pathEle).append('.'); 
		} 
		sb.append(name); 
		settings.put(sb.toString(), value.toString()); 
	} 
    
    
	public void parseTosca (String fileName){
		Map<String,String> map= new HashMap<>();
		try {
			map=load(fileName);
			for(String key:map.keySet()){
				if(key.contains("policy.nodes.Root")){
					continue;
				}
				else if(key.contains("policy.nodes")){
					String wordToFind = "policy.nodes.";
					int indexForPolicyNode=key.indexOf(wordToFind);
					String subNodeString= key.substring(indexForPolicyNode+13, key.length());

					stringBetweenDots(subNodeString,map.get(key));
				}
				else if(key.contains("policy.data")){
					String wordToFind="policy.data.";
					int indexForPolicyNode=key.indexOf(wordToFind);
					String subNodeString= key.substring(indexForPolicyNode+12, key.length());

					stringBetweenDotsForDataFields(subNodeString,map.get(key));
				}
			}

			String userDefinedIndividualString="";
			String userDefinedString="";

			for(String uniqueDataKey: uniqueDataKeys){
				String[] uniqueDataKeySplit= uniqueDataKey.split("%");
				userDefinedIndividualString=userDefinedIndividualString+uniqueDataKey+"=";
				userDefinedIndividualString=userDefinedIndividualString+"#A:defaultValue-#B:required-#C:MANY-false";
				for(String key:map.keySet()){
					if(key.contains("policy.data")){
						String containsKey= uniqueDataKeySplit[1]+".type";
						if(key.contains(uniqueDataKeySplit[0])){
							if(key.contains("default")){
								userDefinedIndividualString=userDefinedIndividualString.replace("#B", map.get(key));
							}
							else if(key.contains("required")){
								userDefinedIndividualString=userDefinedIndividualString.replace("#C", map.get(key));
							}
							else if(key.contains(containsKey)){
								String typeValue= map.get(key);
								userDefinedIndividualString=userDefinedIndividualString.replace("#A", typeValue);
							} 
						}
					}
				}
				if(userDefinedString!=""){
					userDefinedString=userDefinedString+","+userDefinedIndividualString;
				}else{
					userDefinedString=userDefinedString+userDefinedIndividualString;
				}
				userDefinedIndividualString="";
			}
			LOGGER.info("userDefinedString   :"+userDefinedString);

			HashMap<String,ArrayList<String>> mapKeyUserdefined= new HashMap<>();
			String secondPartString="";
			String firstPartString="";
			for(String value: userDefinedString.split(",")){
				String[] splitWithEquals= value.split("=");
				secondPartString=splitWithEquals[0].substring(splitWithEquals[0].indexOf("%")+1);
				firstPartString=splitWithEquals[0].substring(0, splitWithEquals[0].indexOf("%"));
				ArrayList<String> list;
				if(mapKeyUserdefined.containsKey(firstPartString)){
					list = mapKeyUserdefined.get(firstPartString);
					list.add(secondPartString+"<"+splitWithEquals[1]);
				} else {
					list = new ArrayList<String>();
					list.add(secondPartString+"<"+splitWithEquals[1]);
					mapKeyUserdefined.put(firstPartString, list);
				}
			}

			JSONObject mainObject= new JSONObject();;
			JSONObject json;
			for(String s: mapKeyUserdefined.keySet()){
				json= new JSONObject();
				List<String> value=mapKeyUserdefined.get(s);
				for(String listValue:value){
					String[] splitValue=listValue.split("<");
					json.put(splitValue[0], splitValue[1]);
				}
				mainObject.put(s,json);
			}
			Iterator<String> keysItr = mainObject.keys();
			while(keysItr.hasNext()) {
				String key = keysItr.next();
				String value = mainObject.get(key).toString();
				retmap.put(key, value);
			}

			LOGGER.info("#############################################################################");
			LOGGER.info(mainObject);
			LOGGER.info("###############################################################################");

			HashMap<String,HashMap<String,String>> mapKey= new HashMap<>();
			for(String uniqueKey: uniqueKeys){
				HashMap<String,String> hm;

				for(String key:map.keySet()){
					if(key.contains(uniqueKey)){
						if(mapKey.containsKey(uniqueKey)){
							hm = mapKey.get(uniqueKey);
							String keyStr= key.substring(key.lastIndexOf(".")+1);
							String valueStr= map.get(key);
							if(keyStr.equals("type")){
								if(!key.contains("entry_schema")){
									hm.put(keyStr,valueStr);
								}
							}else{
								hm.put(keyStr,valueStr);
							}
						} else {
							hm = new HashMap<>();
							String keyStr= key.substring(key.lastIndexOf(".")+1);
							String valueStr= map.get(key);
							if(keyStr.equals("type")){
								if(!key.contains("entry_schema")){
									hm.put(keyStr,valueStr);
								}
							}else{
								hm.put(keyStr,valueStr);
							}
							mapKey.put(uniqueKey, hm);
						}
					}
				}
			}

			StringBuilder attributeStringBuilder= new StringBuilder();
			StringBuilder referenceStringBuilder= new StringBuilder();
			StringBuilder listBuffer= new StringBuilder();

			List<String> constraints= new ArrayList<>();
			for(String keySetString: mapKey.keySet()){
				HashMap<String,String> keyValues=mapKey.get(keySetString);
				if(keyValues.get("type").equalsIgnoreCase("string")|| keyValues.get("type").equalsIgnoreCase("integer")){
					StringBuilder attributeIndividualStringBuilder= new StringBuilder();
					attributeIndividualStringBuilder.append(keySetString+"=");
					attributeIndividualStringBuilder.append(keyValues.get("type")+":defaultValue-");
					attributeIndividualStringBuilder.append(keyValues.get("default")+":required-");
					attributeIndividualStringBuilder.append(keyValues.get("required")+":MANY-false");
					attributeStringBuilder.append(attributeIndividualStringBuilder+",");	
				}
				else if(keyValues.get("type").equalsIgnoreCase("list")){
					//List Datatype
					Set<String> keys= keyValues.keySet();
					Iterator<String> itr=keys.iterator();
					while(itr.hasNext()){
						String key= itr.next().toString();
						if((!key.equals("type"))){
							String value= keyValues.get(key);
							//The "." in the value determines if its a string or a user defined type.  
							if (!value.contains(".")){
								//This is string
								constraints.add(keyValues.get(key));
							}else{
								//This is user defined string
								String trimValue=value.substring(value.lastIndexOf(".")+1);
								StringBuilder referenceIndividualStringBuilder= new StringBuilder();
								referenceIndividualStringBuilder.append(keySetString+"="+trimValue+":MANY-true");
								referenceStringBuilder.append(referenceIndividualStringBuilder+",");
							}
						}
					}
				}else{
					//User defined Datatype. 
					String value=keyValues.get("type");
					String trimValue=value.substring(value.lastIndexOf(".")+1);
					StringBuilder referenceIndividualStringBuilder= new StringBuilder();
					referenceIndividualStringBuilder.append(keySetString+"="+trimValue+":MANY-false");
					referenceStringBuilder.append(referenceIndividualStringBuilder+",");
				}
				if(constraints!=null &&constraints.isEmpty()==false){
					//List handling. 
					listBuffer.append(keySetString.toUpperCase()+"=[");
					for(String str:constraints){
						listBuffer.append(str+",");
					}
					listBuffer.append("]#");
					LOGGER.info(listBuffer);


					StringBuilder referenceIndividualStringBuilder= new StringBuilder();
					referenceIndividualStringBuilder.append(keySetString+"="+keySetString.toUpperCase()+":MANY-false");
					referenceStringBuilder.append(referenceIndividualStringBuilder+",");
					constraints.clear();
				}
			}

			LOGGER.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			LOGGER.info("Whole attribute String is:"+attributeStringBuilder);	
			LOGGER.info("Whole reference String is:"+referenceStringBuilder);
			LOGGER.info("List String is:"+listBuffer);
			LOGGER.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			this.listConstraints=listBuffer.toString();
			this.referenceAttributes=referenceStringBuilder.toString();
			this.attributeString=attributeStringBuilder.toString();

		} catch (IOException e) {
			LOGGER.error(e);
		}
	} 

	private String cleanUPJson(String json) {
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
	
	private JSONObject decodeContent(JsonNode jsonNode){
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
		String presKey = null;
		for(String key: element.keySet()){
			if(key.contains(".")){
				presKey = key.substring(0,key.indexOf("."));
			}else if(key.contains("@")){
				presKey = key.substring(0,key.indexOf("@"));
			}else{
				presKey = key;
			}
			// first check if we are different from old.
			LOGGER.info(key+"\n");
			if(jsonArray!=null && jsonArray.length()>0 && key.contains("@") && !key.contains(".") && oldValue!=null){
				if(!oldValue.equals(key.substring(0,key.indexOf("@")))){
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
					nodeKey = key.substring(0,key.indexOf("."));
				}
				if(nodeKey.equals(key.substring(0,key.indexOf(".")))){
					node.put(key.substring(key.indexOf(".")+1), element.get(key));
				}else{
					if(node.size()!=0){
						if(nodeKey.contains("@")){
							if(arryKey==null){
								arryKey = nodeKey.substring(0,nodeKey.indexOf("@"));
							}
							if(nodeKey.endsWith("@0")){
								isArray = true;
								jsonArray = new JSONArray();
							}
							if(jsonArray != null && arryKey.equals(nodeKey.substring(0,nodeKey.indexOf("@")))){
								jsonArray.put(decodeContent(node));
							} 
							if(key.contains("@") && !arryKey.equals(key.substring(0,nodeKey.indexOf("@")))){
								jsonResult.put(arryKey, jsonArray);
								jsonArray = new JSONArray();
							}else if(!key.contains("@")){
								jsonResult.put(arryKey, jsonArray);
								jsonArray = new JSONArray();
							}
							arryKey = nodeKey.substring(0,nodeKey.indexOf("@"));
						}else{
							isArray = false;
							jsonResult.put(nodeKey, decodeContent(node));
						}
						node = nodeFactory.objectNode();
					}
					nodeKey = key.substring(0,key.indexOf("."));
					if(nodeKey.contains("@")){
						arryKey = nodeKey.substring(0,nodeKey.indexOf("@"));
					}
					node.put(key.substring(key.indexOf(".")+1), element.get(key));
				}
			}else if(node.size()!=0){
				if(nodeKey.contains("@")){
					if(arryKey==null){
						arryKey = nodeKey.substring(0,nodeKey.indexOf("@"));
					}
					if(nodeKey.endsWith("@0")){
						isArray = true;
						jsonArray = new JSONArray();
					}
					if(jsonArray != null && arryKey.equals(nodeKey.substring(0,nodeKey.indexOf("@")))){
						jsonArray.put(decodeContent(node));
					}
					jsonResult.put(arryKey, jsonArray);
					jsonArray = new JSONArray();
					arryKey = nodeKey.substring(0,nodeKey.indexOf("@"));
				}else{
					isArray = false;
					jsonResult.put(nodeKey, decodeContent(node));
				}
				node = nodeFactory.objectNode();
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
						oldValue = key.substring(0,key.indexOf("@"));
					}
					if(oldValue!=prevKey){
						oldValue = key.substring(0,key.indexOf("@"));
					}
					if(oldValue.equals(key.substring(0,key.indexOf("@")))){
						jsonArray.put(element.get(key));
					}else{
						jsonResult.put(oldValue, jsonArray);
						jsonArray = new JSONArray();
					}
					oldValue = key.substring(0,key.indexOf("@"));
				}else{
					jsonResult.put(key, element.get(key));
				}
			}else{
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
						oldValue = key.substring(0,key.indexOf("@"));
					}
					if(oldValue!=prevKey){
						oldValue = key.substring(0,key.indexOf("@"));
					}
					if(oldValue.equals(key.substring(0,key.indexOf("@")))){
						jsonArray.put(element.get(key));
					}else{
						jsonResult.put(oldValue, jsonArray);
						jsonArray = new JSONArray();
					}
					oldValue = key.substring(0,key.indexOf("@"));
				}else{
					jsonResult.put(key, element.get(key));
				}
			}
		}
		if(node.size()>0){
			if(nodeKey.contains("@")){
				if(jsonArray==null){
					jsonArray = new JSONArray();
				}
				if(arryKey==null){
					arryKey = nodeKey.substring(0,nodeKey.indexOf("@"));
				}
				jsonArray.put(decodeContent(node));
				jsonResult.put(arryKey, jsonArray);
				isArray = false;;
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
	public ModelAndView getDCAEMSTemplateData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());

		String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
		String  servicename = value.toString().split("-v")[0];
		String version = null;
		if (value.toString().contains("-v")){
			version = value.toString().split("-v")[1];
		}
		MicroServiceModels returnModel = getAttributeObject(servicename, version);
		
		String jsonModel = createMicroSeriveJson(returnModel);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");
		List<Object>  list = new ArrayList<>();
		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(returnModel);
		JSONObject j = new JSONObject("{dcaeModelData: " + responseString + ",jsonValue: " + jsonModel + "}");
		list.add(j);
		out.write(list.toString());
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String createMicroSeriveJson(MicroServiceModels returnModel) {
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
			if (keySet.getValue().split("MANY-")[1].equalsIgnoreCase("true")){
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
				if (keySet.getValue().split("MANY-")[1].equalsIgnoreCase("true")){
					array.put(recursiveReference(value, gsonObject, enumAttribute));
					object.put(keySet.getKey().trim(), array);
				}else {
					object.put(keySet.getKey().trim(), recursiveReference(value, gsonObject, enumAttribute));
				}
			}else {
				if (keySet.getValue().split("MANY-")[1].equalsIgnoreCase("true")){
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
		Map<String, String> map = new HashMap<>();
		Object returnClass = subAttributeMap.get(name);
		map = (Map<String, String>) returnClass; 
		JSONArray array = new JSONArray();
		
		for( Entry<String, String> m:map.entrySet()){  
			String[] splitValue = m.getValue().split(":");
			array = new JSONArray();
			if (subAttributeMap.containsKey(splitValue[0])){
				if (m.getValue().split("MANY-")[1].equalsIgnoreCase("true")){
					array.put(recursiveReference(splitValue[0], subAttributeMap, enumAttribute));
					object.put(m.getKey().trim(), array);
				}else {
					object.put(m.getKey().trim(), recursiveReference(splitValue[0], subAttributeMap, enumAttribute));
				}
			} else{
				if (m.getValue().split("MANY-")[1].equalsIgnoreCase("true")){
					array.put(splitValue[0].trim());
					object.put(m.getKey().trim(), array);
				}else {
					object.put(m.getKey().trim(), splitValue[0].trim());
				}
			}
		  }  
		
		return object;
	}

	
	@RequestMapping(value={"/policyController/getModelServiceVersioneData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView getModelServiceVersionData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());

		String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
		String  servicename = value.toString().split("-v")[0];
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
		MicroServiceModels workingModel = new MicroServiceModels();
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
										// First match in the target is EcompName, so set that value.
										if (attributeId.equals("ECOMPName")) {
											policyAdapter.setEcompName(value);
										}
										if (attributeId.equals("ConfigName")){
											policyAdapter.setConfigName(value);
										}
										if (attributeId.equals("uuid")){
											policyAdapter.setUuid(value);
										}
										if (attributeId.equals("location")){
											policyAdapter.setLocation(value);
										}
										if (attributeId.equals("RiskType")){
											policyAdapter.setRiskType(value);
										}
										if (attributeId.equals("RiskLevel")){
											policyAdapter.setRiskLevel(value);
										}
										if (attributeId.equals("guard")){
											policyAdapter.setGuard(value);
										}
										if (attributeId.equals("TTLDate") && !value.contains("NA")){
											String newDate = convertDate(value, true);
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

	private String convertDate(String dateTTL, boolean portalType) {
		String formateDate = null;
		String[] date  = dateTTL.split("T");
		String[] parts = date[0].split("-");
			
		formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		return formateDate;
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
			DCAEMicroServiceObject msBody = (DCAEMicroServiceObject) mapper.readValue(entity.getConfigurationData().getConfigBody(), DCAEMicroServiceObject.class);
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
	private void readRecursivlyJSONContent(LinkedHashMap<String, ?> map, LinkedHashMap<String, Object> data){
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			Object key =  iterator.next();
			Object value = map.get(key);
			if(value instanceof LinkedHashMap<?, ?>){
				LinkedHashMap<String, Object> secondObjec = new LinkedHashMap<>(); 
				readRecursivlyJSONContent((LinkedHashMap<String, ?>) value, secondObjec);
				for(String objKey: secondObjec.keySet()){
					data.put(key+"." +objKey, secondObjec.get(objKey));
				}
			}else if(value instanceof ArrayList){
				ArrayList<?> jsonArrayVal = (ArrayList<?>)value;
				for(int i = 0; i < jsonArrayVal.size(); i++){
					Object arrayvalue = jsonArrayVal.get(i);
					if(arrayvalue instanceof LinkedHashMap<?, ?>){
						LinkedHashMap<String, Object> newData = new LinkedHashMap<>();   
						readRecursivlyJSONContent((LinkedHashMap<String, ?>) arrayvalue, newData);
						for(String objKey: newData.keySet()){
							data.put(key+"@"+i+"." +objKey, newData.get(objKey));
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

	private String getPolicyScope(String value) {	
		GroupPolicyScopeList pScope = new GroupPolicyScopeList();
		List<Object> groupList= commonClassDao.getData(GroupPolicyScopeList.class);
		if(groupList.size() > 0){
			for(int i = 0 ; i < groupList.size() ; i ++){
				pScope = (GroupPolicyScopeList) groupList.get(i);
				if (pScope.getGroupList().equals(value)){
					break;
				}		
			}
		}
		return pScope.getGroupName();
	}

	//Convert the map values and set into JSON body
	public Map<String, String> convertMap(Map<String, String> attributesMap, Map<String, String> attributesRefMap) {
		Map<String, String> attribute = new HashMap<>();
		String temp = null;
		String key;
		String value;
		for (Entry<String, String> entry : attributesMap.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			attribute.put(key, value);
		}
		for (Entry<String, String> entryRef : attributesRefMap.entrySet()) {
			key = entryRef.getKey();
			value = entryRef.getValue().toString();
			attribute.put(key, value);
		}
		for (Entry<String, String> entryList : attributesListRefMap.entrySet()) {
			key = entryList.getKey();
			value = entryList.getValue().toString();
			attribute.put(key, value);
		}
		for (Entry<String, LinkedList<String>> arrayList : arrayTextList.entrySet()){
			key = arrayList.getKey();
			temp = null;
			for (Object textList : arrayList.getValue()){
				if (temp == null){
					temp = "[" + textList;
				}else{
					temp = temp + "," + textList;
				}
			}
			attribute.put(key, temp+ "]");			
		}

		return  attribute;
	}
	
	@RequestMapping(value={"/ms_dictionary/set_MSModelData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void SetMSModelData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		boolean zip = false;
		boolean yml= false;
		for (FileItem item : items) {
			if(item.getName().endsWith(".zip") || item.getName().endsWith(".xmi")||item.getName().endsWith(".yml")){
				this.newModel = new MicroServiceModels();
				try{
					File file = new File(item.getName());
					OutputStream outputStream = new FileOutputStream(file);
					IOUtils.copy(item.getInputStream(), outputStream);
					outputStream.close();
					this.newFile = file.toString();
					this.newModel.setModelName(this.newFile.toString().split("-v")[0]);
				
					if (this.newFile.toString().contains("-v")){
						if (item.getName().endsWith(".zip")){
							this.newModel.setVersion(this.newFile.toString().split("-v")[1].replace(".zip", ""));
							zip = true;
						}else if(item.getName().endsWith(".yml")){
							this.newModel.setVersion(this.newFile.toString().split("-v")[1].replace(".yml", ""));
							yml = true;
						}
						else {
							this.newModel.setVersion(this.newFile.toString().split("-v")[1].replace(".xmi", ""));
						}
					}
				
				}catch(Exception e){
					LOGGER.error("Upload error : " + e);
				}
			}
			
		}
		List<File> fileList = new ArrayList<>();;
		this.directory = "model";
		if (zip){
			extractFolder(this.newFile);
			fileList = listModelFiles(this.directory);
		}else if (yml==true){
			parseTosca(this.newFile);
		}else {
			File file = new File(this.newFile);
			fileList.add(file);
		}
		String modelType= "";
		if(yml==false){
			modelType="xmi";
			//Process Main Model file first
			classMap = new HashMap<>();
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
			
			HashMap<String, String> returnAttributeList =new HashMap<>();
			returnAttributeList.put(className, this.attributeString);
			msAttributes.setAttribute(returnAttributeList);
			
			msAttributes.setSubClass(this.retmap);
			
			HashMap<String, String> returnReferenceList =new HashMap<>();
			//String[] referenceArray=this.referenceAttributes.split("=");
			returnReferenceList.put(className, this.referenceAttributes);
			msAttributes.setRefAttribute(returnReferenceList);
			
			if(this.listConstraints!=""){
				HashMap<String, String> enumList =new HashMap<>();
				String[] listArray=this.listConstraints.split("#");
                for(String str:listArray){
                    String[] strArr= str.split("=");
                    if(strArr.length>1){
                        enumList.put(strArr[0], strArr[1]);
                    }
                }
				msAttributes.setEnumType(enumList);
			}
			
			classMap=new HashMap<>();
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
		out.write(j.toString());
	}
	
	/*
	 * Unzip file and store in the model directory for processing
	 */
	@SuppressWarnings("rawtypes")
	private void extractFolder(String zipFile )  {
	    int BUFFER = 2048;
	    File file = new File(zipFile);

	    ZipFile zip = null;
		try {
			zip = new ZipFile(file);
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
		            byte data[] = new byte[BUFFER];
		            FileOutputStream fos = new FileOutputStream(destFile);
		            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
		                dest.write(data, 0, currentByte);
		            }
		            dest.flush();
		            dest.close();
		            is.close();
		        }
	
		        if (currentEntry.endsWith(".zip")){
		            extractFolder(destFile.getAbsolutePath());
		        }
		    }
	    } catch (IOException e) {
	    	LOGGER.error("Failed to unzip model file " + zipFile);
		}finally{
			try {
				if(zip != null)
				zip.close();
			} catch (IOException e) {
				LOGGER.error("Exception Occured While closing zipfile " + e);
			}
		}
	}
	
	private void retreiveDependency(String workingFile, Boolean modelClass) {
		
		MSModelUtils utils = new MSModelUtils(PolicyController.msEcompName, PolicyController.msPolicyName);
	    HashMap<String, MSAttributeObject> tempMap = new HashMap<>();
	    
	    tempMap = utils.processEpackage(workingFile, MODEL_TYPE.XMI);
	    
	    classMap.putAll(tempMap);
	    LOGGER.info(tempMap);
	    
	    return;   	
	    
	}
		
	private List<File> listModelFiles(String directoryName) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<>();
		File[] fList = directory.listFiles();
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
	
    private void cleanUp(String path) {
        if (path!=null){
            try {
                FileUtils.forceDelete(new File(path));
            } catch (IOException e) {
            	LOGGER.error("Failed to delete folder " + path);
            }  
        }
    }
 
    private void checkZipDirectory(String zipDirectory) {
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

}

class DCAEMicroServiceObject {

	public String service;
	public String location;
	public String uuid;
	public String policyName;
	public String description;
	public String configName;
	public String templateVersion;
	public String version;
	public String priority;
	public String policyScope;
	public String riskType;
	public String riskLevel; 
	public String guard = null;

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