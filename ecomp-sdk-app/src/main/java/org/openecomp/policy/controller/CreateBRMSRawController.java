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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.openecomp.policy.adapter.PolicyAdapter;
import org.openecomp.policy.admin.PolicyNotificationMail;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.dao.RuleAlgorithmsDao;
import org.openecomp.policy.dao.WatchPolicyNotificationDao;
import org.openecomp.policy.elk.client.PolicyElasticSearchController;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.RuleAlgorithms;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.att.research.xacml.api.XACML3;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Controller
@RequestMapping("/")
public class CreateBRMSRawController extends RestrictedBaseController {

	private static final Logger logger = FlexLogger.getLogger(CreateBRMSRawController.class);

	private static RuleAlgorithmsDao ruleAlgorithmsDao;
	private static PolicyVersionDao policyVersionDao;
	private static WatchPolicyNotificationDao policyNotificationDao;
	
	@Autowired
	private CreateBRMSRawController(RuleAlgorithmsDao ruleAlgorithmsDao, PolicyVersionDao policyVersionDao,  WatchPolicyNotificationDao policyNotificationDao){
		CreateBRMSRawController.policyVersionDao = policyVersionDao;
		CreateBRMSRawController.ruleAlgorithmsDao = ruleAlgorithmsDao;
		CreateBRMSRawController.policyNotificationDao = policyNotificationDao;
	}

	public CreateBRMSRawController(){}
	protected PolicyAdapter policyAdapter = null;
	private ArrayList<Object> attributeList;
	private String ruleID = "";
	public String newPolicyID() {
		return Joiner.on(':').skipNulls().join((PolicyController.getDomain().startsWith("urn") ? null: "urn"),
				PolicyController.getDomain().replaceAll("[/\\\\.]", ":"), "xacml", "policy", "id", UUID.randomUUID());
	}



	@RequestMapping(value={"/policyController/save_BRMSRawPolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveBRMSRawPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			String userId = UserUtils.getUserIdFromCookie(request);
			RESTfulPAPEngine engine = (RESTfulPAPEngine) PolicyController.getPapEngine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyAdapter policyData = (PolicyAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyAdapter.class);

			if(root.get("policyData").get("model").get("type").toString().replace("\"", "").equals("file")){
				policyData.isEditPolicy = true;
			}
			if(root.get("policyData").get("model").get("path").size() != 0){
				String dirName = "";
				for(int i = 0; i < root.get("policyData").get("model").get("path").size(); i++){
					dirName = dirName.replace("\"", "") + root.get("policyData").get("model").get("path").get(i).toString().replace("\"", "") + File.separator;
				}
				policyData.setDomainDir(dirName.substring(0, dirName.lastIndexOf(File.separator)));
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
			String dbCheckPolicyName = policyData.getDomainDir() + File.separator + "Config_BRMS_Raw_" + policyData.getPolicyName();
			List<PolicyVersion> policyVersionList = policyVersionDao.getPolicyVersionEntityByName(dbCheckPolicyName);
			if (policyVersionList.size() > 0) {		
				for(int i = 0;  i < policyVersionList.size(); i++) {
					PolicyVersion entityItem = policyVersionList.get(i);
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
			Map<String, String> successMap = new HashMap<String, String>();
			Map<String, String> attributeMap = new HashMap<String, String>();
			//set the Rule Combining Algorithm Id to be sent to PAP-REST via JSON
			List<RuleAlgorithms> ruleAlgorithmsList = ruleAlgorithmsDao.getRuleAlgorithms();
			for (int i = 0; i < ruleAlgorithmsList.size(); i++) {
				RuleAlgorithms a = ruleAlgorithmsList.get(i);
				if (a.getXacmlId().equals(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue())) {
					policyData.setRuleCombiningAlgId(a.getXacmlId());
					break;
				}
			}

			if(policyData.getAttributes().size() > 0){
				for(Object attribute : policyData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("value").toString();
						attributeMap.put(key, value);	
					}
				}
			}


			policyData.setEcompName("DROOLS");
			policyData.setConfigName("BRMS_RAW_RULE");
			policyData.setDynamicFieldConfigAttributes(attributeMap);
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
					List<PolicyVersion> versionList = policyVersionDao.getPolicyVersionEntityByName(removeExtension);
					if (versionList.size() > 0) {		
						for(int i = 0;  i < versionList.size(); i++) {
							PolicyVersion entityItem = versionList.get(i);
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
	
	private String convertDate(String dateTTL) {
		String formateDate = null;
		String[] date  = dateTTL.split("T");
		String[] parts = date[0].split("-");
		
		formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		return formateDate;
	}
	
	public void PrePopulateBRMSRawPolicyData(PolicyAdapter policyAdapter) {
		attributeList = new ArrayList<Object>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			PolicyType policy = (PolicyType) policyAdapter.getPolicyData();
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			// policy name value is the policy name without any prefix and
			// Extensions.
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("BRMS_Raw_") +9, policyAdapter.getPolicyName().lastIndexOf("."));
			if (logger.isDebugEnabled()) {
				logger.debug("Prepopulating form data for BRMS RAW Policy selected:" + policyAdapter.getPolicyName());
			}
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			// Set Attributes. 
			AdviceExpressionsType expressionTypes = ((RuleType)policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().get(0)).getAdviceExpressions();
			for( AdviceExpressionType adviceExpression: expressionTypes.getAdviceExpression()){
				for(AttributeAssignmentExpressionType attributeAssignment: adviceExpression.getAttributeAssignmentExpression()){
					if(attributeAssignment.getAttributeId().startsWith("key:")){
						Map<String, String> attribute = new HashMap<String, String>();
						String key = attributeAssignment.getAttributeId().replace("key:", "");
						attribute.put("key", key);
						JAXBElement<AttributeValueType> attributevalue = (JAXBElement<AttributeValueType>) attributeAssignment.getExpression();
						String value = (String) attributevalue.getValue().getContent().get(0);
						attribute.put("value", value);
						attributeList.add(attribute);
					}
				}
				policyAdapter.setAttributes(attributeList);
			}
			String ruleConfigName = policyAdapter.getDirPath().replace(File.separator, ".")+ "." + policyAdapter.getOldPolicyFileName() + ".";
			policyAdapter.setConfigBodyPath(ruleConfigName);
			// Get the target data under policy.
			policyAdapter.setConfigBodyData(readFile(policyAdapter));
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
							int index = 0;
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attributevalue and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);

										if (index ==  3){
											policyAdapter.setRiskType(value);
										}

										if (index ==  4){
											policyAdapter.setRiskLevel(value);
										}
										
										if (index ==  5){
											policyAdapter.setGuard(value);
										}
										if (index == 6 && !value.contains("NA")){
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

	// Here we are reading the configurations file
	protected String readFile(PolicyAdapter policyAdapter) {
		String fileLocation = null;
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
			if (file.isFile() && file.getName().contains(fileName) && file.toString().endsWith(".txt")) {
				// Reading the file
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						sb.append("\n");
						line = br.readLine();
					}
					return sb.toString();
				} catch (FileNotFoundException e) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getMessage());
				} catch (IOException e1) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e1.getMessage());
				}
			}
		}
		return null;
	}

}
