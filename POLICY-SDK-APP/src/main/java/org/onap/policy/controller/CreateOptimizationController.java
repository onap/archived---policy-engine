/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;


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
import java.util.ArrayList;
import java.util.Arrays;
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
import org.onap.policy.rest.jpa.OptimizationModels;
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
import org.yaml.snakeyaml.Yaml;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
public class CreateOptimizationController extends RestrictedBaseController {
	private static final Logger LOGGER = FlexLogger.getLogger(CreateOptimizationController.class);
	private Map<String, String>  matchableValues;
	private static CommonClassDao commonClassDao;
	
	public static CommonClassDao getCommonClassDao() {
		return commonClassDao;
	}

	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		CreateOptimizationController.commonClassDao = commonClassDao;
	}

	private OptimizationModels newModel;
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
	StringBuilder dataListBuffer=new StringBuilder();
	List<String> dataConstraints= new ArrayList <>();
	Set<String> allManyTrueKeys= new HashSet <>();
	
	public static final String DATATYPE  = "data_types.policy.data.";
	public static final String PROPERTIES=".properties.";
	public static final String TYPE=".type";
	public static final String STRING="string";
	public static final String INTEGER="integer";
	public static final String LIST="list";
	public static final String DEFAULT=".default";
	public static final String REQUIRED=".required";
	public static final String MANYFALSE=":MANY-false";
	public static final String MATCHABLE=".matchable";
	
	
	@Autowired
	private CreateOptimizationController(CommonClassDao commonClassDao){
		CreateOptimizationController.commonClassDao = commonClassDao;
	}

	public CreateOptimizationController(){
		// Empty Constructor
	}

	protected PolicyRestAdapter policyAdapter = null;
	private Map<String, String> attributesListRefMap =  new HashMap<>();
	private Map<String, LinkedList<String>> arrayTextList =  new HashMap<>();
	CreateDcaeMicroServiceController msController = new CreateDcaeMicroServiceController();

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
			jsonContent = msController.decodeContent(tempJsonNode).toString();
			constructJson(policyData, jsonContent, dummyValue);
		}catch(Exception e){
			LOGGER.error("Error while decoding microservice content", e);
		}
		
		return policyData;
	}
	
	private PolicyRestAdapter constructJson(PolicyRestAdapter policyAdapter, String jsonContent, String dummyValue) {
		ObjectWriter om = new ObjectMapper().writer();
		String json="";
		OptimizationObject optimizationObject = setOptimizationObjectValues(policyAdapter);
		
		optimizationObject.setContent(jsonContent);

		try {
			json = om.writeValueAsString(optimizationObject);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error writing out the object", e);
		}
		LOGGER.info("input json: " + json);
		LOGGER.info("input jsonContent: " + jsonContent);
		String cleanJson = msController.cleanUPJson(json);
		
        //--- reset empty value back after called cleanUPJson method and before calling removeNullAttributes
		String tempJson = StringUtils.replaceEach(cleanJson, new String[]{"\""+dummyValue+"\""},  new String[]{"\"\""});
		LOGGER.info("tempJson: " + tempJson);
		cleanJson = msController.removeNullAttributes(tempJson);
		policyAdapter.setJsonBody(cleanJson);
		return policyAdapter;
	}
		
	// Second index of dot should be returned. 
	public int stringBetweenDots(String str){
		String stringToSearch=str;
		String[]ss=stringToSearch.split("\\.");
		if(ss!=null){
			int len= ss.length;
			if(len>2){
				uniqueKeys.add(ss[2]);
			}
		}
		
		return uniqueKeys.size();
	}
	
	public void stringBetweenDotsForDataFields(String str){
		String stringToSearch=str;
		String[]ss=stringToSearch.split("\\.");
		if(ss!=null){
			int len= ss.length;

			if(len>2){
				uniqueDataKeys.add(ss[0]+"%"+ss[2]);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> load(String fileName) throws IOException { 
		File newConfiguration = new File(fileName);
		Yaml yaml = new Yaml();
		Map<Object, Object> yamlMap = null;
		try(InputStream is = new FileInputStream(newConfiguration)){
			yamlMap = (Map<Object, Object>) yaml.load(is); 
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		}

		StringBuilder sb = new StringBuilder(); 
		Map<String, String> settings = new HashMap<>(); 
		if (yamlMap == null) { 
			return settings; 
		} 
		List<String> path = new ArrayList <>(); 
		serializeMap(settings, sb, path, yamlMap); 
		return settings; 
	} 

	public Map<String, String> load(byte[] source) { 
		Yaml yaml = new Yaml(); 
		@SuppressWarnings("unchecked")
		Map<Object, Object> yamlMap = (Map<Object, Object>) yaml.load(Arrays.toString(source)); 
		StringBuilder sb = new StringBuilder(); 
		Map<String, String> settings = new HashMap <>(); 
		if (yamlMap == null) { 
			return settings; 
		} 
		List<String> path = new ArrayList <>(); 
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
    
	void parseDataAndPolicyNodes(Map<String,String> map){
		for(String key:map.keySet()){
			if(key.contains("policy.nodes.Root"))
			{
				continue;
			}
			else if(key.contains("policy.nodes")){
				String wordToFind = "policy.nodes.";
				int indexForPolicyNode=key.indexOf(wordToFind);
				String subNodeString= key.substring(indexForPolicyNode+13, key.length());

				stringBetweenDots(subNodeString);
			}
			else if(key.contains("policy.data")){
				String wordToFind="policy.data.";
				int indexForPolicyNode=key.indexOf(wordToFind);
				String subNodeString= key.substring(indexForPolicyNode+12, key.length());

				stringBetweenDotsForDataFields(subNodeString);
			}
		}
	}
	
	HashMap<String,String> parseDataNodes(Map<String,String> map){
		HashMap<String,String> dataMapForJson=new HashMap <>(); 
		matchableValues = new HashMap<>();
		for(String uniqueDataKey: uniqueDataKeys){
			if(uniqueDataKey.contains("%")){
				String[] uniqueDataKeySplit= uniqueDataKey.split("%");
				String findType=DATATYPE+uniqueDataKeySplit[0]+PROPERTIES+uniqueDataKeySplit[1]+TYPE;
				String typeValue=map.get(findType);
				LOGGER.info(typeValue);
				if(typeValue != null && typeValue.equalsIgnoreCase(STRING)||
						typeValue.equalsIgnoreCase(INTEGER)){
					String findDefault=DATATYPE+uniqueDataKeySplit[0]+PROPERTIES+uniqueDataKeySplit[1]+DEFAULT;
					String defaultValue= map.get(findDefault);
					LOGGER.info("defaultValue is:"+ defaultValue);
					
					String findRequired=DATATYPE+uniqueDataKeySplit[0]+PROPERTIES+uniqueDataKeySplit[1]+REQUIRED;
					String requiredValue= map.get(findRequired);
					LOGGER.info("requiredValue is:"+ requiredValue);
					
					String matchable =DATATYPE+uniqueDataKeySplit[0]+PROPERTIES+uniqueDataKeySplit[1]+MATCHABLE;

					String matchableValue= map.get(matchable);

					if(matchableValue != null && "true".equalsIgnoreCase(matchableValue)){
						if(uniqueDataKey.contains("%")){
							String[] keys= uniqueDataKey.split("%");
							String key=keys[keys.length -1];
							matchableValues.put(key, "matching-true");
						}else{
							matchableValues.put(uniqueDataKey, "matching-true");
						}
					}
					
					StringBuilder attributeIndividualStringBuilder= new StringBuilder();
					attributeIndividualStringBuilder.append(typeValue+":defaultValue-");
					attributeIndividualStringBuilder.append(defaultValue+":required-");
					attributeIndividualStringBuilder.append(requiredValue+MANYFALSE);
					dataMapForJson.put(uniqueDataKey, attributeIndividualStringBuilder.toString());		
				}
				else if(typeValue != null && typeValue.equalsIgnoreCase(LIST)){
					String findList= DATATYPE+uniqueDataKeySplit[0]+PROPERTIES+uniqueDataKeySplit[1]+".entry_schema.type";
					String listValue=map.get(findList);
					if(listValue!=null){
						LOGGER.info("Type of list is:"+ listValue);
						//Its userdefined
						if(listValue.contains(".")){
							String trimValue=listValue.substring(listValue.lastIndexOf('.')+1);
							StringBuilder referenceIndividualStringBuilder= new StringBuilder();
							referenceIndividualStringBuilder.append(trimValue+":MANY-true");
							dataMapForJson.put(uniqueDataKey, referenceIndividualStringBuilder.toString());
						}//Its string
						else{
							StringBuilder stringListItems= new StringBuilder();
							stringListItems.append(uniqueDataKeySplit[1].toUpperCase()+MANYFALSE);
							dataMapForJson.put(uniqueDataKey, stringListItems.toString());
							dataListBuffer.append(uniqueDataKeySplit[1].toUpperCase()+"=[");
							for(int i=0;i<10;i++){
								String findConstraints= DATATYPE+uniqueDataKeySplit[0]+PROPERTIES+uniqueDataKeySplit[1]+".entry_schema.constraints.0.valid_values."+i;
								String constraintsValue=map.get(findConstraints);
								LOGGER.info(constraintsValue);
								if(constraintsValue==null){
									break;
								}
								else{
									if(constraintsValue.contains("=")){
										constraintsValue = constraintsValue.replace("=", "equal-sign");
									}
									dataConstraints.add(constraintsValue);
									dataListBuffer.append(constraintsValue+",");
								}
							}
							dataListBuffer.append("]#");
							LOGGER.info(dataListBuffer);
						}
					}
				}
				else{
					String findUserDefined=DATATYPE+uniqueDataKeySplit[0]+"."+"properties"+"."+uniqueDataKeySplit[1]+TYPE;
					String userDefinedValue=map.get(findUserDefined);
					String trimValue=userDefinedValue.substring(userDefinedValue.lastIndexOf('.')+1);
					StringBuilder referenceIndividualStringBuilder= new StringBuilder();
					referenceIndividualStringBuilder.append(trimValue+MANYFALSE);
					dataMapForJson.put(uniqueDataKey, referenceIndividualStringBuilder.toString());
				}
			}
		}
		return dataMapForJson;
	}
	
	void constructJsonForDataFields(HashMap<String,String> dataMapForJson){
		HashMap<String,HashMap<String,String>> dataMapKey= new HashMap <>();
		HashMap<String,String> hmSub;
		for(Map.Entry<String, String> entry: dataMapForJson.entrySet()){
			String uniqueDataKey= entry.getKey();
			String[] uniqueDataKeySplit=uniqueDataKey.split("%");
			String value= dataMapForJson.get(uniqueDataKey);
			if(dataMapKey.containsKey(uniqueDataKeySplit[0])){
				hmSub = dataMapKey.get(uniqueDataKeySplit[0]);
				hmSub.put(uniqueDataKeySplit[1], value);
			}
			else{
				hmSub=new HashMap <>();
				hmSub.put(uniqueDataKeySplit[1], value);
			}
				
			dataMapKey.put(uniqueDataKeySplit[0], hmSub);
		}
				
		JSONObject mainObject= new JSONObject();
		JSONObject json;
		for(Map.Entry<String,HashMap<String,String>> entry: dataMapKey.entrySet()){
			String s=entry.getKey();
			json= new JSONObject();
			HashMap<String,String> jsonHm=dataMapKey.get(s);
			for(Map.Entry<String,String> entryMap:jsonHm.entrySet()){
				String key=entryMap.getKey();
				json.put(key, jsonHm.get(key));
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
	}
		
	HashMap<String,HashMap<String,String>> parsePolicyNodes(Map<String,String> map){
		HashMap<String,HashMap<String,String>> mapKey= new HashMap <>();
		for(String uniqueKey: uniqueKeys){
			HashMap<String,String> hm;

			for(Map.Entry<String,String> entry:map.entrySet()){
				String key=entry.getKey();
				if(key.contains(uniqueKey) && key.contains("policy.nodes")){
					if(mapKey.containsKey(uniqueKey)){
						hm = mapKey.get(uniqueKey);
						String keyStr= key.substring(key.lastIndexOf('.')+1);
						String valueStr= map.get(key);
						if(("type").equals(keyStr)){
							if(!key.contains("entry_schema"))
							{
								hm.put(keyStr,valueStr);
							}
						}else{
							hm.put(keyStr,valueStr);
						}

					} else {
						hm = new HashMap <>();
						String keyStr= key.substring(key.lastIndexOf('.')+1);
						String valueStr= map.get(key);
						if(("type").equals(keyStr)){
							if(!key.contains("entry_schema"))
							{
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
		return mapKey;
	}

	void createAttributes(HashMap<String,HashMap<String,String>> mapKey){
		StringBuilder attributeStringBuilder= new StringBuilder();
		StringBuilder referenceStringBuilder= new StringBuilder();
		StringBuilder listBuffer= new StringBuilder();
		List<String> constraints= new ArrayList<>();
		for(Map.Entry<String,HashMap<String,String>> entry: mapKey.entrySet()){
			String keySetString= entry.getKey();
			HashMap<String,String> keyValues=mapKey.get(keySetString);
			if(keyValues.get("type") != null && keyValues.get("type").equalsIgnoreCase(STRING)||
					keyValues.get("type") != null && keyValues.get("type").equalsIgnoreCase(INTEGER)
					){
				StringBuilder attributeIndividualStringBuilder= new StringBuilder();
				attributeIndividualStringBuilder.append(keySetString+"=");
				attributeIndividualStringBuilder.append(keyValues.get("type")+":defaultValue-");
				attributeIndividualStringBuilder.append(keyValues.get("default")+":required-");
				attributeIndividualStringBuilder.append(keyValues.get("required")+MANYFALSE);
				attributeStringBuilder.append(attributeIndividualStringBuilder+",");	
				if(keyValues.get("matchable") != null && "true".equalsIgnoreCase(keyValues.get("matchable"))){
				    matchableValues.put(keySetString, "matching-true");
                }
			}
			else if(keyValues.get("type") != null && LIST.equalsIgnoreCase(keyValues.get("type"))){
				
				if(keyValues.get("matchable") != null && "true".equalsIgnoreCase(keyValues.get("matchable"))){
				    matchableValues.put(keySetString, "matching-true");
                }
				
				//List Datatype
				Set<String> keys= keyValues.keySet();
				Iterator<String> itr=keys.iterator();
				boolean isDefinedType = false;
				while(itr.hasNext()){
					String key= itr.next();
					if(!("type").equals(key) ||("required").equals(key))
					{
						String value= keyValues.get(key);
						//The "." in the value determines if its a string or a user defined type.  
						if (!value.contains(".")){
							//This is string
							if(StringUtils.isNumeric(key) ){  //only integer key for the value of Constrains 
							    constraints.add(keyValues.get(key));
							}
						}else{
							//This is user defined type
							String trimValue=value.substring(value.lastIndexOf('.')+1);
							StringBuilder referenceIndividualStringBuilder= new StringBuilder();
							referenceIndividualStringBuilder.append(keySetString+"="+trimValue+":MANY-true");
							referenceStringBuilder.append(referenceIndividualStringBuilder+",");
							isDefinedType = true;
						}
					}				

				}

				if(!isDefinedType && keyValues.get("type").equalsIgnoreCase(LIST) &&
					(constraints == null || constraints.isEmpty()) ) {   //type is list but no constraints defined.
						referenceStringBuilder.append(keySetString+"=MANY-true"+",");
				}
			}else{
				//User defined Datatype. 
				if(keyValues.get("matchable") != null && "true".equalsIgnoreCase(keyValues.get("matchable"))){
				    matchableValues.put(keySetString, "matching-true");
                }
				String value=keyValues.get("type");
				if(value != null && !value.isEmpty()){
					String trimValue=value.substring(value.lastIndexOf('.')+1);
					StringBuilder referenceIndividualStringBuilder= new StringBuilder();
					referenceIndividualStringBuilder.append(keySetString+"="+trimValue+":MANY-false");
					referenceStringBuilder.append(referenceIndividualStringBuilder+",");
				}else{
					LOGGER.info("keyValues.get(type) is null/empty");
				}

			}
			if(constraints!=null && ! constraints.isEmpty()){
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
		
		dataListBuffer.append(listBuffer);
		

		LOGGER.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		LOGGER.info("Whole attribute String is:"+attributeStringBuilder);	
		LOGGER.info("Whole reference String is:"+referenceStringBuilder);
		LOGGER.info("List String is:"+listBuffer);
		LOGGER.info("Data list buffer is:"+dataListBuffer);
		LOGGER.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		
		this.listConstraints=dataListBuffer.toString();
		this.referenceAttributes=referenceStringBuilder.toString();
		this.attributeString=attributeStringBuilder.toString();
	}
	   

	public void parseTosca (String fileName){
		Map<String,String> map= new HashMap<>();
    
    	try {
			map=load(fileName);
			
			parseDataAndPolicyNodes(map);
			
			HashMap<String,String> dataMapForJson=parseDataNodes(map);
			
			constructJsonForDataFields(dataMapForJson);	
			
			HashMap<String,HashMap<String,String>> mapKey= parsePolicyNodes(map);
			
			createAttributes(mapKey);
		
    	} catch (IOException e) {
    		LOGGER.error(e);
    	}
	} 
	
	@RequestMapping(value={"/policyController/getOptimizationTemplateData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView getOptimizationTemplateData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());

		String value = root.get("policyData").toString().replaceAll("^\"|\"$", "");
		String  servicename = value.split("-v")[0];
		String version = null;
		if (value.contains("-v")){
			version = value.split("-v")[1];
		}
		
		OptimizationModels returnModel = getAttributeObject(servicename, version);
		
		//Get all keys with "MANY-true" defined in their value from subAttribute 
		Set<String> allkeys = null;
		if(returnModel.getSubattributes() != null && !returnModel.getSubattributes().isEmpty()){
			JSONObject json = new JSONObject(returnModel.getSubattributes());	
			getAllKeys(json); 
			allkeys = allManyTrueKeys;
			allManyTrueKeys = new  HashSet <>();
			LOGGER.info("allkeys : " + allkeys);
		}
		
		String nameOfTrueKey = "";
		if(allkeys != null){
			nameOfTrueKey = allkeys.toString();
		}
		
		String jsonModel = createOptimizationJson(returnModel, allkeys);
		
		JSONObject jsonObject = new JSONObject(jsonModel);
		
		JSONObject finalJsonObject = null;
		if(allkeys != null){
			Iterator<String> iter = allkeys.iterator();
			while(iter.hasNext()){
				//Convert to array values for MANY-true keys
				finalJsonObject = CreateDcaeMicroServiceController.convertToArrayElement(jsonObject, iter.next());
			}
		}

		if(finalJsonObject != null){
		    LOGGER.info(finalJsonObject.toString());
		    jsonModel  = finalJsonObject.toString();
		}
		
		//get all properties with "MANY-true" defined in Ref_attributes
		Set<String> manyTrueProperties = CreateDcaeMicroServiceController.getManyTrueProperties(returnModel.getRefattributes());
		JSONObject jsonObj = new JSONObject(jsonModel);
		for (String s : manyTrueProperties) {
			LOGGER.info(s);
			//convert to array element for MANY-true properties
			finalJsonObject = CreateDcaeMicroServiceController.convertToArrayElement(jsonObj, s.trim());
		}
		
		if(finalJsonObject != null){
		    LOGGER.info(finalJsonObject.toString());
		    jsonModel = finalJsonObject.toString();
		}
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");
		List<Object>  list = new ArrayList<>();
		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(returnModel);
		JSONObject j;
		if("".equals(nameOfTrueKey)){
			j = new JSONObject("{optimizationModelData: " + responseString + ",jsonValue: " + jsonModel + "}");	
		}else{
			j = new JSONObject("{optimizationModelData: " + responseString + ",jsonValue: " + jsonModel + ",allManyTrueKeys: " + nameOfTrueKey+ "}");	
		}
		list.add(j);
		out.write(list.toString());
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	private String createOptimizationJson(OptimizationModels returnModel, Set<String> allkeys) {
		Map<String, String> attributeMap = new HashMap<>();
		Map<String, String> refAttributeMap = new HashMap<>();
		
		String attribute = returnModel.getAttributes();
		if(attribute != null){
			attribute = attribute.trim();
		}
		String refAttribute = returnModel.getRefattributes();
		if(refAttribute != null){
			refAttribute = refAttribute.trim();
		}
		
		String enumAttribute = returnModel.getEnumValues();
		if(enumAttribute != null){
			enumAttribute = enumAttribute.trim();
		}
		
		if (!StringUtils.isEmpty(attribute)){
			attributeMap = CreateDcaeMicroServiceController.convert(attribute, ",");
		}
		
		if (!StringUtils.isEmpty(refAttribute)){
			refAttributeMap = CreateDcaeMicroServiceController.convert(refAttribute, ",");
		}

		Gson gson = new Gson();
		
		String subAttributes = returnModel.getSubattributes();
		if(subAttributes != null){
			subAttributes = subAttributes.trim();
		}else{
			subAttributes = "";
		}
		
		Map gsonObject = (Map) gson.fromJson(subAttributes, Object.class);
		
		JSONObject object = getModelAttributesObject(gsonObject,attributeMap,refAttributeMap, enumAttribute);

		return object.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject getModelAttributesObject(Map gsonObject, Map<String,String> attributeMap, 
			Map<String,String> refAttributeMap, String enumAttribute) {
		JSONObject object = new JSONObject();
		JSONArray array;
		
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
		
		return object;
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
	
	@RequestMapping(value={"/policyController/getModelServiceVersionData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
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
		JSONObject j = new JSONObject("{optimizationModelVersionData: " + responseString +"}");
		list.add(j);
		out.write(list.toString());
		return null;
	}

	private Set<String> getVersionList(String name) {	
		OptimizationModels workingModel;
		Set<String> list = new HashSet<>();
		List<Object> optimizationModelsData = commonClassDao.getDataById(OptimizationModels.class, "modelName", name);
		for (int i = 0; i < optimizationModelsData.size(); i++) {
			workingModel = (OptimizationModels) optimizationModelsData.get(i);
			if (workingModel.getVersion()!=null){
				list.add(workingModel.getVersion());
			}else{
				list.add("Default");
			}
		}
		return list;
	}
	
	private OptimizationModels getAttributeObject(String name, String version) {	
		OptimizationModels workingModel = new OptimizationModels();
		List<Object> optimizationModelsData = commonClassDao.getDataById(OptimizationModels.class, "modelName", name);
		for (int i = 0; i < optimizationModelsData.size(); i++) {
			workingModel = (OptimizationModels) optimizationModelsData.get(i);
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

	@RequestMapping(value={"/get_PriorityValues"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPriorityValuesData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			List<String> priorityList = new ArrayList<>();
			int priorityCount = 10;
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

	public void prePopulatePolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("OOF_") +3);
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
			    LOGGER.error("Error while collecting the description tag in " + policyNameValue ,e);
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

	@SuppressWarnings("unchecked")
	private void readFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		String policyScopeName = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			OptimizationObject optimizationBody = mapper.readValue(entity.getConfigurationData().getConfigBody(), OptimizationObject.class);
			policyScopeName = msController.getPolicyScope(optimizationBody.getPolicyScope());
			policyAdapter.setPolicyScope(policyScopeName);

			policyAdapter.setPriority(optimizationBody.getPriority());

			if (optimizationBody.getVersion()!= null){
				policyAdapter.setServiceType(optimizationBody.getService());
				policyAdapter.setVersion(optimizationBody.getVersion());
			}else{
				policyAdapter.setServiceType(optimizationBody.getService());
			}
			if(optimizationBody.getContent() != null){
				LinkedHashMap<String, Object>  data = new LinkedHashMap<>();
				LinkedHashMap<String, ?> map = (LinkedHashMap<String, ?>) optimizationBody.getContent();
				msController.readRecursivlyJSONContent(map, data);
				policyAdapter.setRuleData(data);
			}

		} catch (Exception e) {
			LOGGER.error(e);
		}

	}
	
	@RequestMapping(value={"/oof_dictionary/set_ModelData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void SetModelData(HttpServletRequest request, HttpServletResponse response) throws IOException, FileUploadException{
		modelList = new ArrayList<>();
		dirDependencyList = new ArrayList<>();
		classMap = new HashMap<>();
		retmap = new HashMap<>();
		uniqueKeys= new HashSet<>();
		uniqueDataKeys= new HashSet<>();
		dataListBuffer=new StringBuilder();
		dataConstraints= new ArrayList <>();
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		boolean zip = false;
		boolean yml= false;
		String errorMsg = "";
		for (FileItem item : items) {
			if(item.getName().endsWith(".zip") || item.getName().endsWith(".xmi")||item.getName().endsWith(".yml")){
				this.newModel = new OptimizationModels();
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
		this.directory = "model";
		if (zip){
			extractFolder(this.newFile);
			fileList = listModelFiles(this.directory);
		}else if (yml){
			parseTosca(this.newFile);
		}else {
			File file = new File(this.newFile);
			fileList.add(file);
		}
		String modelType;
		if(! yml){
			modelType="xmi";
			//Process Main Model file first
			classMap = new HashMap<>();
			for (File file : fileList) {
				if(!file.isDirectory() && file.getName().endsWith(".xmi")){
	            	retrieveDependency(file.toString());
	            }	
			}
			
			modelList = createList();
			
			msController.cleanUp(this.newFile);
			msController.cleanUp(directory);
		}else{
			modelType="yml";
			modelList.add(this.newModel.getModelName());
			String className=this.newModel.getModelName();
			MSAttributeObject optimizationAttributes= new MSAttributeObject();
			optimizationAttributes.setClassName(className);
			
			HashMap<String, String> returnAttributeList =new HashMap<>();
			returnAttributeList.put(className, this.attributeString);
			optimizationAttributes.setAttribute(returnAttributeList);
			
			optimizationAttributes.setSubClass(this.retmap);
			optimizationAttributes.setMatchingSet(matchableValues);
			
			HashMap<String, String> returnReferenceList =new HashMap<>();
			returnReferenceList.put(className, this.referenceAttributes);
			optimizationAttributes.setRefAttribute(returnReferenceList);
			
			if(this.listConstraints!=""){
				HashMap<String, String> enumList =new HashMap<>();
				String[] listArray=this.listConstraints.split("#");
                for(String str:listArray){
                    String[] strArr= str.split("=");
                    if(strArr.length>1){
                        enumList.put(strArr[0], strArr[1]);
                    }
                }
                optimizationAttributes.setEnumType(enumList);
			}
			
			classMap=new HashMap<>();
			classMap.put(className, optimizationAttributes);
			
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

		try (ZipFile zip = new ZipFile(file)) {
		    String newPath =  "model" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    this.directory = "model" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    msController.checkZipDirectory(this.directory);
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
	
	private void retrieveDependency(String workingFile) {
		
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

	private OptimizationObject setOptimizationObjectValues(PolicyRestAdapter policyAdapter) {
		OptimizationObject optimizationObject = new OptimizationObject();
		optimizationObject.setTemplateVersion(XACMLProperties.getProperty(XACMLRestProperties.TemplateVersion_OOF));

		if(policyAdapter.getServiceType() !=null){
			optimizationObject.setService(policyAdapter.getServiceType());
			optimizationObject.setVersion(policyAdapter.getVersion());
		}
		if(policyAdapter.getPolicyName()!=null){
			optimizationObject.setPolicyName(policyAdapter.getPolicyName());
		}
		if(policyAdapter.getPolicyDescription()!=null){
			optimizationObject.setDescription(policyAdapter.getPolicyDescription());
		}
		if (policyAdapter.getPriority()!=null){
			optimizationObject.setPriority(policyAdapter.getPriority());
		}else {
			optimizationObject.setPriority("9999");
		}
		if (policyAdapter.getRiskLevel()!=null){
			optimizationObject.setRiskLevel(policyAdapter.getRiskLevel());
		}
		if (policyAdapter.getRiskType()!=null){
			optimizationObject.setRiskType(policyAdapter.getRiskType());
		}
		if (policyAdapter.getGuard()!=null){
			optimizationObject.setGuard(policyAdapter.getGuard());
		}		
		return optimizationObject;
	}
}

class OptimizationObject {

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