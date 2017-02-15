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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.policy.adapter.PolicyAdapter;
import org.openecomp.policy.admin.PolicyNotificationMail;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.dao.RemoteCatalogValuesDao;
import org.openecomp.policy.dao.RuleAlgorithmsDao;
import org.openecomp.policy.dao.WatchPolicyNotificationDao;
import org.openecomp.policy.elk.client.PolicyElasticSearchController;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.dao.GroupPolicyScopeListDao;
import org.openecomp.policy.rest.dao.MicroServiceModelsDao;
import org.openecomp.policy.rest.jpa.GroupPolicyScopeList;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.RemoteCatalogValues;
import org.openecomp.policy.rest.jpa.RuleAlgorithms;
import org.openecomp.policy.utils.ConfigurableRESTUtils;
import org.openecomp.policy.utils.ConfigurableRESTUtils.RESQUEST_METHOD;
import org.openecomp.policy.utils.ConfigurableRESTUtils.REST_RESPONSE_FORMAT;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.att.research.xacml.api.XACML3;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Controller
@RequestMapping("/")
public class CreateDcaeMicroServiceController extends RestrictedBaseController {
	private static final Logger logger = FlexLogger.getLogger(CreateDcaeMicroServiceController.class);

	private static RuleAlgorithmsDao ruleAlgorithmsDao;
	private static RemoteCatalogValuesDao remoteCatalogValuesDao;
	private static MicroServiceModelsDao microServiceModelsDao;
	private static PolicyVersionDao policyVersionDao;
	private static GroupPolicyScopeListDao groupPolicyScopeListDao;
	private static WatchPolicyNotificationDao policyNotificationDao;
	
	@Autowired
	private CreateDcaeMicroServiceController(RuleAlgorithmsDao ruleAlgorithmsDao, RemoteCatalogValuesDao remoteCatalogValuesDao,
			MicroServiceModelsDao microServiceModelsDao, PolicyVersionDao policyVersionDao, GroupPolicyScopeListDao groupPolicyScopeListDao,
			WatchPolicyNotificationDao policyNotificationDao){
		CreateDcaeMicroServiceController.groupPolicyScopeListDao = groupPolicyScopeListDao;
		CreateDcaeMicroServiceController.policyVersionDao = policyVersionDao;
		CreateDcaeMicroServiceController.microServiceModelsDao = microServiceModelsDao;
		CreateDcaeMicroServiceController.remoteCatalogValuesDao = remoteCatalogValuesDao;
		CreateDcaeMicroServiceController.ruleAlgorithmsDao = ruleAlgorithmsDao;
		CreateDcaeMicroServiceController.policyNotificationDao = policyNotificationDao;
	}
	
	public CreateDcaeMicroServiceController(){}
	
	protected PolicyAdapter policyAdapter = null;
	private String ruleID = "";
	private int priorityCount; 
	private Map<String, String> attributesListRefMap =  new HashMap<String, String>();
	private Map<String, LinkedList<String>> arrayTextList =  new HashMap<String, LinkedList<String>>();


	public String newPolicyID() {
		return Joiner.on(':').skipNulls().join((PolicyController.getDomain().startsWith("urn") ? null: "urn"),
				PolicyController.getDomain().replaceAll("[/\\\\.]", ":"), "xacml", "policy", "id", UUID.randomUUID());
	}


	@RequestMapping(value={"/policyController/getDCAEMSTemplateData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView getBRMSParamPolicyRuleData(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");
        List<Object>  list = new ArrayList<>();
		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(returnModel);
		JSONObject j = new JSONObject("{dcaeModelData: " + responseString +"}");
		list.add(j);
		out.write(list.toString());
		return null;
	}

	private MicroServiceModels getAttributeObject(String name, String version) {	
		MicroServiceModels workingModel = new MicroServiceModels();
		List<MicroServiceModels> microServiceModelsData = microServiceModelsDao.getMicroServiceModelsData();
		for (int i = 0; i < microServiceModelsData.size(); i++) {
			workingModel = microServiceModelsData.get(i);
			if (version!=null && workingModel.getVersion()!=null){
				if (workingModel.getModelName().equals(name) && workingModel.getVersion().equals(version)){
					break;
				}
			}else{
				if (workingModel.getModelName().equals(name) && workingModel.getVersion() == null){
					break;
				}
			}
		}
		return workingModel;
	}

	@RequestMapping(value={"/get_DCAEPriorityValues"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDCAEPriorityValuesData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			List<String> priorityList = new ArrayList<String>();
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
			e.printStackTrace();
		}
	}


	@RequestMapping(value={"/policyController/save_DCAEMSPolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveDCAEMSPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			String userId = UserUtils.getUserIdFromCookie(request);
			RESTfulPAPEngine engine = (RESTfulPAPEngine) PolicyController.getPapEngine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			String jsonContent = null;
			try{
				jsonContent = decodeContent(root.get("policyJSON")).toString();
			}catch(Exception e){
				logger.error("Error while decoding microservice content");
			}
			PolicyAdapter policyData = (PolicyAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyAdapter.class);		
			policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			if(root.get("policyData").get("model").get("type").toString().replace("\"", "").equals("file")){
				policyData.isEditPolicy = true;
			}
			
			if(root.get("policyData").get("model").get("path").size() != 0){
				String dirName = "";
				for(int i = 0; i < root.get("policyData").get("model").get("path").size(); i++){
					dirName = dirName.replace("\"", "") + root.get("policyData").get("model").get("path").get(i).toString().replace("\"", "") + File.separator;
				}
				if(policyData.isEditPolicy){
					policyData.setDomainDir(dirName.substring(0, dirName.lastIndexOf(File.separator)));
				}else{
					policyData.setDomainDir(dirName + root.get("policyData").get("model").get("name").toString().replace("\"", ""));
				}
			}else{
				policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			}
			
			if (policyData.getTtlDate()==null){
				policyData.setTtlDate("NA");
			}else{
				String dateTTL = policyData.getTtlDate();
				String newDate = convertDate(dateTTL);
				policyData.setTtlDate(newDate);
			}
			
			int version = 0;
			int highestVersion = 0;
			int descriptionVersion = 0;
			//get the highest version of policy from policy version table.
			//getting the sub scope domain where the policy is created or updated
			String dbCheckPolicyName = policyData.getDomainDir() + File.separator + "Config_MS_" + policyData.getPolicyName();
			List<PolicyVersion> versionList = policyVersionDao.getPolicyVersionEntityByName(dbCheckPolicyName);
			if (versionList.size() > 0) {		
				for(int i = 0;  i < versionList.size(); i++) {
					PolicyVersion entityItem = versionList.get(i);
					if(entityItem.getPolicyName().equals(dbCheckPolicyName)){
						highestVersion = entityItem.getHigherVersion();
					}
				}
			}		
			if(highestVersion != 0){
				version = highestVersion;
				descriptionVersion = highestVersion +1;		
			}else{
				version = 1;
				descriptionVersion = 1;
			}
			
			//set policy adapter values for Building JSON object containing policy data
			String createdBy = "";
			String modifiedBy = userId;
			if(descriptionVersion == 1){
				createdBy = userId;
			}else{
				String policyName = PolicyController.getGitPath().toAbsolutePath().toString() + File.separator + policyData.getDomainDir() + File.separator + policyData.getOldPolicyFileName() + ".xml";
				File policyPath = new File(policyName);
				try {
					createdBy =	XACMLPolicyScanner.getCreatedBy(policyPath.toPath());
				} catch (IOException e) {
					createdBy = "guest";
				}
			}
			
			policyData.setPolicyDescription(policyData.getPolicyDescription()+ "@CreatedBy:" +createdBy + "@CreatedBy:" + "@ModifiedBy:" +modifiedBy + "@ModifiedBy:");
			policyData.setEcompName(policyData.getEcompName());
			//get the jsonBody 
			String jsonBody = null;
			try {
				jsonBody = constructJson(policyData, jsonContent);
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			}
			policyData.setJsonBody(jsonBody);
			Map<String, String> successMap = new HashMap<String, String>();
			//set the Rule Combining Algorithm Id to be sent to PAP-REST via JSON
			List<RuleAlgorithms> ruleAlgorithmsList = ruleAlgorithmsDao.getRuleAlgorithms();
			for (int i = 0; i < ruleAlgorithmsList.size(); i++) {
				RuleAlgorithms a = ruleAlgorithmsList.get(i);
				if (a.getXacmlId().equals(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue())) {
					policyData.setRuleCombiningAlgId(a.getXacmlId());
					break;
				}
			}
			if (policyData.isEditPolicy()){
				//increment the version and set in policyAdapter
				policyData.setVersion(String.valueOf(version));
				policyData.setHighestVersion(version);
				policyData.setPolicyID(this.newPolicyID());
				policyData.setRuleID(ruleID);
				successMap = engine.updatePolicyRequest(policyData);
			} else {
				//send it for policy creation
				policyData.setVersion(String.valueOf(version));
				policyData.setHighestVersion(version);
				successMap = engine.createPolicyRequest(policyData);

			}

			if (successMap.containsKey("success")) {
				// Add it into our tree
				Path finalPolicyPath = null;
				finalPolicyPath = Paths.get(successMap.get("success"));
				PolicyElasticSearchController controller = new PolicyElasticSearchController();
				controller.updateElk(finalPolicyPath.toString());
				File file = finalPolicyPath.toFile();
				if(file != null){
					String policyName = file.toString();
					String removePath = policyName.substring(policyName.indexOf("repository")+11);
					String removeXml = removePath.replace(".xml", "");
					String removeExtension = removeXml.substring(0, removeXml.indexOf("."));
					List<PolicyVersion> policyVersionList = policyVersionDao.getPolicyVersionEntityByName(removeExtension);
					if (policyVersionList.size() > 0) {		
						for(int i = 0;  i < policyVersionList.size(); i++) {
							PolicyVersion entityItem = policyVersionList.get(i);
							if(entityItem.getPolicyName().equals(removeExtension)){
								version = entityItem.getHigherVersion() +1;
								entityItem.setActiveVersion(version);
								entityItem.setHigherVersion(version);
								entityItem.setModifiedBy(userId);
								policyVersionDao.update(entityItem);
								if(policyData.isEditPolicy){
									PolicyNotificationMail email = new PolicyNotificationMail();
									String mode = "EditPolicy";
									String policyNameForEmail = policyData.getDomainDir() + File.separator + policyData.getOldPolicyFileName() + ".xml";
									email.sendMail(entityItem, policyNameForEmail, mode, policyNotificationDao);
								}
							}
						}
					}else{
						PolicyVersion entityItem = new PolicyVersion();
						entityItem.setActiveVersion(version);
						entityItem.setHigherVersion(version);
						entityItem.setPolicyName(removeExtension);
						entityItem.setCreatedBy(userId);
						entityItem.setModifiedBy(userId);
						policyVersionDao.Save(entityItem);
					}
				}
			}
			
			System.out.println(root);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(successMap);
			JSONObject j = new JSONObject("{policyData: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	private JSONObject decodeContent(JsonNode jsonNode){
		Iterator<JsonNode> jsonElements = jsonNode.elements();
		Iterator<String> jsonKeys = jsonNode.fieldNames();
		Map<String,String> element = new TreeMap<String,String>();
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
			System.out.println(key+"\n");
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
			/*if(node.size()!=0 && key.contains("@")){
				
			}else{
				if(node.size()!=0){
					
				}
			}*/
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
							if(arryKey.equals(nodeKey.substring(0,nodeKey.indexOf("@")))){
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
					if(arryKey.equals(nodeKey.substring(0,nodeKey.indexOf("@")))){
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

	public void PrePopulateDCAEMSPolicyData(PolicyAdapter policyAdapter) {
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("MS_") +3 , policyAdapter.getPolicyName().lastIndexOf("."));
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			String jsonBodyName = policyAdapter.getDirPath().replace(File.separator, ".")+ "." + policyAdapter.getOldPolicyFileName() + ".";
			policyAdapter.setConfigBodyPath(jsonBodyName);
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
									int index = 0;
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (matchList.size()>1 && iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attributevalue and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);

										// First match in the target is EcompName, so set that value.
										if (index == 0) {
											policyAdapter.setEcompName(value);
										}
										if (index == 1){
											policyAdapter.setConfigName(value);
										}
										if (index == 2){
											if(value != null){
												readFile(policyAdapter);
											}
										}
										if (index == 3){
											policyAdapter.setUuid(value);
										}
										if (index == 4){
											policyAdapter.setLocation(value);
										}
										if (index ==  5){
											policyAdapter.setRiskType(value);
										}

										if (index ==  6){
											policyAdapter.setRiskLevel(value);
										}
										
										if (index ==  7){
											policyAdapter.setGuard(value);
										}
										if (index == 8 && !value.contains("NA")){
											String newDate = convertDate(value, true);
											policyAdapter.setTtlDate(newDate);
										}
										index++;
									}
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
		String[] date;
		String[] parts;
		
		if (portalType){
			parts = dateTTL.split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0] + "T05:00:00.000Z";
		} else {
			date  = dateTTL.split("T");
			parts = date[0].split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		}
		return formateDate;
	}
	
	@SuppressWarnings("unchecked")
	private String readFile(PolicyAdapter policyAdapter) {
		String fileLocation = null;
		String policyScopeName = null;
		String fileName = policyAdapter.getConfigBodyPath();
		if (fileName != null ) {
			fileLocation = PolicyController.getConfigHome();
		}		
		if (fileLocation == null) {
			return fileLocation;
		}
		File dir = new File(fileLocation);
		File[] listOfFiles = dir.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(fileName)) {
				FileInputStream inputStream = null;
				String location = file.toString();
				try {
					inputStream = new FileInputStream(location);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if (location.endsWith("json")) {
					JsonReader jsonReader = null;
					jsonReader = Json.createReader(inputStream);
					ObjectMapper mapper = new ObjectMapper();
					try {
						DCAEMicroServiceObject msBody = (DCAEMicroServiceObject) mapper.readValue(jsonReader.read().toString(), DCAEMicroServiceObject.class);
						policyScopeName = getPolicyScope(msBody.getPolicyScope());
						policyAdapter.setPolicyScope(policyScopeName);

						policyAdapter.setPriority(msBody.getPriority());
						
						if (msBody.getVersion()!= null){
							policyAdapter.setServiceType(msBody.getService() + "-v" + msBody.getVersion());
						}else{
							policyAdapter.setServiceType(msBody.getService());
						}
						if(msBody.getContent() != null){
							LinkedHashMap<String, Object>  data = new LinkedHashMap<String, Object>();
							LinkedHashMap<String, ?> map = (LinkedHashMap<String, ?>) msBody.getContent();
							readRecursivlyJSONContent(map, data);
							policyAdapter.setRuleData(data);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					jsonReader.close();
				}
			}
		}
		return fileName;
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void readRecursivlyJSONContent(LinkedHashMap<String, ?> map, LinkedHashMap<String, Object> data){
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			Object key =  iterator.next();
			Object value = map.get(key);
			if(value instanceof LinkedHashMap<?, ?>){
				readRecursivlyJSONContent((LinkedHashMap<String, ?>) value, data);
			}else if(value instanceof ArrayList){
				ArrayList<?> jsonArrayVal = (ArrayList<?>)value;
		       	for(int i = 0; i < jsonArrayVal.size(); i++){
		       		Object arrayvalue = jsonArrayVal.get(i);
		       		if(arrayvalue instanceof LinkedHashMap<?, ?>){
		       			LinkedHashMap<String, Object> newData = new LinkedHashMap<String, Object>();   
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
		List<GroupPolicyScopeList> groupList= groupPolicyScopeListDao.getGroupPolicyScopeListData();
		if(groupList.size() > 0){
			for(int i = 0 ; i < groupList.size() ; i ++){
				pScope = groupList.get(i);
				if (pScope.getGroupList().equals(value)){
					break;
				}		
			}
		}
		return pScope.getGroupName();
	}
	
	private GroupPolicyScopeList getPolicyObject(String policyScope) {
		GroupPolicyScopeList pScope = new GroupPolicyScopeList();
		List<GroupPolicyScopeList> groupList = groupPolicyScopeListDao.getGroupPolicyScopeListData();
		if(groupList.size() > 0){
			for(int i = 0 ; i < groupList.size() ; i ++){
				pScope = groupList.get(i);
				if (pScope.getGroupName().equals(policyScope)){
					break;
				}		
			}
		}
		return pScope;
	}
	
	private String constructJson(PolicyAdapter policyAdapter, String jsonContent) {
		ObjectWriter om = new ObjectMapper().writer();
		String json="";
		DCAEMicroServiceObject microServiceObject = new DCAEMicroServiceObject();

		microServiceObject.setTemplateVersion(XACMLProperties.getProperty(XACMLRestProperties.TemplateVersion_MS));
		if(policyAdapter.getServiceType() !=null){
			microServiceObject.setService(policyAdapter.getServiceType().toString().split("-v")[0]);
			if (policyAdapter.getServiceType().toString().contains("-v")){
				microServiceObject.setVersion(policyAdapter.getServiceType().toString().split("-v")[1]);
			}
		}
		if(policyAdapter.getUuid()!=null){
			microServiceObject.setUuid(policyAdapter.getUuid());
		}

		if(policyAdapter.getLocation()!=null){
			microServiceObject.setLocation(policyAdapter.getLocation());
		}
		if(policyAdapter.getPolicyName()!=null){
			microServiceObject.setPolicyName(policyAdapter.getPolicyName());
		}

		if(policyAdapter.getConfigName()!=null){
			microServiceObject.setConfigName(policyAdapter.getConfigName());
		}
		if(policyAdapter.getPolicyDescription()!=null){
			microServiceObject.setDescription(policyAdapter.getPolicyDescription());
		}
		if (policyAdapter.getPriority()!=null){
			microServiceObject.setPriority(policyAdapter.getPriority());
		}else {
			microServiceObject.setPriority("9999");
		}
		microServiceObject.setContent(jsonContent);
		GroupPolicyScopeList policyScopeValue = getPolicyObject(policyAdapter.getPolicyScope());
		microServiceObject.setPolicyScope(policyScopeValue.getGroupList());		
		try {
			json = om.writeValueAsString(microServiceObject);
		} catch (JsonProcessingException e) {
			logger.error("Error writing out the object");
		}
		System.out.println(json);
		String cleanJson = cleanUPJson(json);
		return cleanJson;
	}
	
	private String cleanUPJson(String json) {
		String cleanJason = StringUtils.replaceEach(json, new String[]{"\\\\", "\\\\\\", "\\\\\\\\"}, new String[]{"\\", "\\", "\\"});
		cleanJason = StringUtils.replaceEach(json, new String[]{"\\\\\\"}, new String[]{"\\"});
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\\\\", "[[", "]]"}, new String[]{"\\", "[", "]"});
		
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\\\\\"", "\\\"", "\"[{", "}]\""}, new String[]{"\"", "\"", "[{", "}]"});
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\"[{", "}]\""}, new String[]{"[{", "}]"});
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\"[", "]\""}, new String[]{"[", "]"});
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\"{", "}\""}, new String[]{"{", "}"});
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\"\"\"", "\"\""}, new String[]{"\"", "\""});
		cleanJason = StringUtils.replaceEach(cleanJason, new String[]{"\\\""}, new String[]{""});
		return cleanJason;
	}

	//Convert the map values and set into JSON body
		public Map<String, String> convertMap(Map<String, String> attributesMap, Map<String, String> attributesRefMap) {
			Map<String, String> attribute = new HashMap<String, String>();
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
		
		private String convertDate(String dateTTL) {
			String formateDate = null;
			String[] date  = dateTTL.split("T");
			String[] parts = date[0].split("-");
			
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
			return formateDate;
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