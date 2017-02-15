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
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

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

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import com.att.research.xacml.api.XACML3;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;

@Controller
@RequestMapping("/")
public class DecisionPolicyController extends RestrictedBaseController {
	private static final Logger logger = FlexLogger.getLogger(DecisionPolicyController.class);
	
	private static RuleAlgorithmsDao ruleAlgorithmsDao;
	private static PolicyVersionDao policyVersionDao;
	private static WatchPolicyNotificationDao policyNotificationDao;
	
	@Autowired
	private DecisionPolicyController(RuleAlgorithmsDao ruleAlgorithmsDao, PolicyVersionDao policyVersionDao, WatchPolicyNotificationDao policyNotificationDao){
		DecisionPolicyController.policyVersionDao = policyVersionDao;
		DecisionPolicyController.ruleAlgorithmsDao = ruleAlgorithmsDao;
		DecisionPolicyController.policyNotificationDao = policyNotificationDao;
	}
	
	public DecisionPolicyController(){}

	protected PolicyAdapter policyAdapter = null;
	private static String ruleID = "";
	private ArrayList<Object> attributeList;
	private ArrayList<Object> decisionList;
	private ArrayList<Object>  ruleAlgorithmList;
	protected LinkedList<Integer> ruleAlgoirthmTracker;
	public static final String FUNCTION_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";

	public String newPolicyID() {
		return Joiner.on(':').skipNulls().join((PolicyController.getDomain().startsWith("urn") ? null: "urn"),
				PolicyController.getDomain().replaceAll("[/\\\\.]", ":"), "xacml", "policy", "id", UUID.randomUUID());
	}

	@RequestMapping(value={"/policyController/save_DecisionPolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveDecisionPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			int version = 0;
			int highestVersion = 0;
			int descriptionVersion = 0;
			//get the highest version of policy from policy version table.
			//getting the sub scope domain where the policy is created or updated
			String dbCheckPolicyName = policyData.getDomainDir() + File.separator + "Decision_" + policyData.getPolicyName();
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
			Map<String, String> settingsMap = new HashMap<String, String>();

			List<String> dynamicRuleAlgorithmLabels = new LinkedList<String>();
			List<String> dynamicRuleAlgorithmCombo = new LinkedList<String>();
			List<String> dynamicRuleAlgorithmField1 = new LinkedList<String>();
			List<String> dynamicRuleAlgorithmField2 = new LinkedList<String>();
			List<Object> dynamicVariableList = new LinkedList<Object>();
			List<String> dataTypeList = new LinkedList<String>();

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
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
						attributeMap.put(key, value);	
					}
				}
			}

			if(policyData.getSettings().size() > 0){
				for(Object settingsData : policyData.getSettings()){
					if(settingsData instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) settingsData).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) settingsData).get("number").toString();
						settingsMap.put(key, value);	
					}
				}
			}

			if(policyData.getRuleAlgorithmschoices().size() > 0){
				for(Object attribute : policyData.getRuleAlgorithmschoices()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
						String key = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
						String rule = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
						dynamicRuleAlgorithmLabels.add(label);
						dynamicRuleAlgorithmField1.add(key);
						dynamicRuleAlgorithmCombo.add(rule);
						dynamicRuleAlgorithmField2.add(value);
					}
				}
			}
			
			policyData.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
			policyData.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
			policyData.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
			policyData.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
			policyData.setDynamicVariableList(dynamicVariableList);
			policyData.setDynamicSettingsMap(settingsMap);
			policyData.setDynamicFieldConfigAttributes(attributeMap);
			policyData.setDataTypeList(dataTypeList);
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
					List<PolicyVersion> vesionList = policyVersionDao.getPolicyVersionEntityByName(removeExtension);
					if (vesionList.size() > 0) {		
						for(int i = 0;  i < vesionList.size(); i++) {
							PolicyVersion entityItem = vesionList.get(i);
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

	public void PrePopulateDecisionPolicyData(PolicyAdapter policyAdapter) {
		attributeList = new ArrayList<Object>();
		decisionList = new ArrayList<Object>();
		ruleAlgorithmList = new ArrayList<Object>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("_") + 1, policyAdapter.getPolicyName().lastIndexOf("."));
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			// Get the target data under policy for Action.
			TargetType target = policy.getTarget();
			if (target != null) {
				// under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AntOfType we have AllOfType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOfType we have Mathch.
								List<MatchType> matchList = allOf.getMatch();
								int index = 0;
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
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();
										// First match in the target is EcompName, so set that value.
										if (index == 0) {
											policyAdapter.setEcompName(value);
										}
										// Component attributes are saved under Target here we are fetching  them back.
										// One row is default so we are not adding dynamic componet at index 0.
										if (index >= 1) {	
											Map<String, String> attribute = new HashMap<String, String>();
											attribute.put("option", attributeId);
											attribute.put("number", value);
											attributeList.add(attribute);	
										}
										index++;
									}
								}
								policyAdapter.setAttributes(attributeList);
							}
						}
					}
				}

				List<Object> ruleList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
				int index = 0;
				for (Object object : ruleList) {
					if (object instanceof VariableDefinitionType) {
						VariableDefinitionType variableDefinitionType = (VariableDefinitionType) object;
						Map<String, String> settings = new HashMap<String, String>();
						settings.put("option", variableDefinitionType.getVariableId());
						JAXBElement<AttributeValueType> attributeValueTypeElement = (JAXBElement<AttributeValueType>) variableDefinitionType.getExpression();
						if (attributeValueTypeElement != null) {
							AttributeValueType attributeValueType = attributeValueTypeElement.getValue();
							settings.put("number", attributeValueType.getContent().get(0).toString());
						}
						decisionList.add(settings);
					} else if (object instanceof RuleType) {
						// get the condition data under the rule for rule Algorithms.
						ruleID = ((RuleType) object).getRuleId();
						if (((RuleType) object).getEffect().equals(EffectType.PERMIT)) {
							ConditionType condition = ((RuleType) object).getCondition();
							if (condition != null) {
								ApplyType decisionApply = (ApplyType) condition.getExpression().getValue();
								ruleAlgoirthmTracker = new LinkedList<Integer>();
								// Populating Rule Algorithms starting from compound.
								prePopulateDecisionCompoundRuleAlgorithm(index, decisionApply);
								policyAdapter.setRuleAlgorithmschoices(ruleAlgorithmList);
							}
						}else if(((RuleType) object).getEffect().equals(EffectType.DENY)) {
							if(((RuleType) object).getAdviceExpressions()!=null){
								if(((RuleType) object).getAdviceExpressions().getAdviceExpression().get(0).getAdviceId().toString().equalsIgnoreCase("AAF")){
									policyAdapter.setRuleProvider("AAF");
									break;
								}
							}else{
								policyAdapter.setRuleProvider("Custom");
							}
						}
					}
				}
			}
			policyAdapter.setSettings(decisionList);	
		}	

	}

	private void prePopulateDecisionRuleAlgorithms(int index, ApplyType decisionApply, List<JAXBElement<?>> jaxbDecisionTypes) {
		Map<String, String> ruleMap = new HashMap<String, String>();
		ruleMap.put("id", "A" + (index +1));
		Map<String, String> dropDownMap = PolicyController.getDropDownMap();
		for (String key : dropDownMap.keySet()) {
			String keyValue = dropDownMap.get(key);
			if (keyValue.equals(decisionApply.getFunctionId())) {
				ruleMap.put("dynamicRuleAlgorithmCombo", key);
			}
		}
		// Populate the key and value fields
		if (((jaxbDecisionTypes.get(0).getValue()) instanceof AttributeValueType)) {
			ApplyType innerDecisionApply = (ApplyType) jaxbDecisionTypes.get(1).getValue();
			List<JAXBElement<?>> jaxbInnerDecisionTypes = innerDecisionApply.getExpression();
			if (jaxbInnerDecisionTypes.get(0).getValue() instanceof AttributeDesignatorType) {
				AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) jaxbInnerDecisionTypes.get(0).getValue();
				ruleMap.put("dynamicRuleAlgorithmField1", attributeDesignator.getAttributeId());

				// Get from Attribute Value
				AttributeValueType actionConditionAttributeValue = (AttributeValueType) jaxbDecisionTypes.get(0).getValue();
				String attributeValue = (String) actionConditionAttributeValue.getContent().get(0);
				ruleMap.put("dynamicRuleAlgorithmField2", attributeValue);
			}
		} else if ((jaxbDecisionTypes.get(0).getValue()) instanceof VariableReferenceType) {
			VariableReferenceType variableReference = (VariableReferenceType) jaxbDecisionTypes.get(0).getValue();	
			ruleMap.put("dynamicRuleAlgorithmField1", "S_"+ variableReference.getVariableId());


			// Get from Attribute Value
			AttributeValueType actionConditionAttributeValue = (AttributeValueType) jaxbDecisionTypes.get(1).getValue();
			String attributeValue = (String) actionConditionAttributeValue.getContent().get(0);
			ruleMap.put("dynamicRuleAlgorithmField2", attributeValue);
		}
		ruleAlgorithmList.add(ruleMap);
	}

	private int prePopulateDecisionCompoundRuleAlgorithm(int index, ApplyType decisionApply) {
		boolean isCompoundRule = true;
		List<JAXBElement<?>> jaxbDecisionTypes = decisionApply.getExpression();
		for (JAXBElement<?> jaxbElement : jaxbDecisionTypes) {
			// If There is Attribute Value under Decision Type that means we came to the final child
			if (logger.isDebugEnabled()) {
				logger.debug("Prepopulating rule algoirthm: " + index);
			}
			// Check to see if Attribute Value exists, if yes then it is not a compound rule
			if(jaxbElement.getValue() instanceof AttributeValueType) {
				prePopulateDecisionRuleAlgorithms(index, decisionApply, jaxbDecisionTypes);
				ruleAlgoirthmTracker.addLast(index);
				isCompoundRule = false;
				index++;
			} 
		}
		if (isCompoundRule) {
			// As it's compound rule, Get the Apply types
			for (JAXBElement<?> jaxbElement : jaxbDecisionTypes) {
				ApplyType innerDecisionApply = (ApplyType) jaxbElement.getValue();
				index = prePopulateDecisionCompoundRuleAlgorithm(index, innerDecisionApply);
			}
			// Populate combo box
			if (logger.isDebugEnabled()) {
				logger.debug("Prepopulating Compound rule algorithm: " + index);
			}
			Map<String, String> rule = new HashMap<String, String>();
			for (String key : PolicyController.getDropDownMap().keySet()) {
				String keyValue = PolicyController.getDropDownMap().get(key);
				if (keyValue.equals(decisionApply.getFunctionId())) {
					rule.put("dynamicRuleAlgorithmCombo", key);
				}
			}

			rule.put("id", "A" + (index +1));
			// Populate Key and values for Compound Rule
			rule.put("dynamicRuleAlgorithmField1", "A" + (ruleAlgoirthmTracker.getLast() + 1 ));
			ruleAlgoirthmTracker.removeLast();
			rule.put("dynamicRuleAlgorithmField2", "A" + (ruleAlgoirthmTracker.getLast() + 1));
			ruleAlgoirthmTracker.removeLast();
			ruleAlgoirthmTracker.addLast(index);
			ruleAlgorithmList.add(rule);
			index++;
		}
		return index;
	}
}
