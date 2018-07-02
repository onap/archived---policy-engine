/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.adapter.RainyDayParams;
import org.onap.policy.rest.adapter.YAMLParams;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
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
	private static final Logger policyLogger = FlexLogger.getLogger(DecisionPolicyController.class);
	
	public DecisionPolicyController(){
		// This constructor is empty
	}

	protected PolicyRestAdapter policyAdapter = null;
	private ArrayList<Object> attributeList;
	private ArrayList<Object> decisionList;
	private ArrayList<Object>  ruleAlgorithmList;
	private ArrayList<Object> treatmentList = null;
	protected LinkedList<Integer> ruleAlgoirthmTracker;
	public static final String FUNCTION_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";

	@SuppressWarnings("unchecked")
	public void prePopulateDecisionPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		attributeList = new ArrayList<>();
		decisionList = new ArrayList<>();
		ruleAlgorithmList = new ArrayList<>();
		treatmentList = new ArrayList<>();
		
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			RainyDayParams rainydayParams = new RainyDayParams();
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());

			if("Decision_MS".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				policyAdapter.setRuleProvider("MicroService_Model");
				policyAdapter.setPolicyName(StringUtils.substringAfter(policyAdapter.getPolicyName(), "Decision_MS_"));
			} else {
				policyAdapter.setPolicyName(StringUtils.substringAfter(policyAdapter.getPolicyName(), "Decision_"));
			}
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				policyLogger.info("General error", e);
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
                                        if(value!=null){
                                            value = value.replaceAll("\\(\\?i\\)", "");
                                        }
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();
										// First match in the target is OnapName, so set that value.
										if ("ONAPName".equals(attributeId)) {
											policyAdapter.setOnapName(value);
										}
										// Component attributes are saved under Target here we are fetching  them back.
										// One row is default so we are not adding dynamic component at index 0.
										if (index >= 1) {	
											Map<String, String> attribute = new HashMap<>();
											attribute.put("key", attributeId);
											attribute.put("value", value);
											attributeList.add(attribute);	
										}
										index++;
									}
								}
								policyAdapter.setAttributes(attributeList);
							}
						}
					}
					// Setting rainy day attributes to the parameters object if they exist 
					boolean rainy = false;
					if(!attributeList.isEmpty()) {
						for(int i=0; i<attributeList.size() ; i++){
							Map<String, String> map = (Map<String,String>)attributeList.get(i);
							if("WorkStep".equals(map.get("key"))){
								rainydayParams.setWorkstep(map.get("value"));
								rainy=true;
							}else if("BB_ID".equals(map.get("key"))){
								rainydayParams.setBbid(map.get("value"));
								rainy=true;
							}else if("ServiceType".equals(map.get("key"))){
								rainydayParams.setServiceType(map.get("value"));
								rainy=true;
							}else if("VNFType".equals(map.get("key"))){
								rainydayParams.setVnfType(map.get("value"));
								rainy=true;
							}
						}	
					}
					if(rainy){
						policyAdapter.setRuleProvider("Rainy_Day");
					}
				}

				List<Object> ruleList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
				int index = 0;
				for (Object object : ruleList) {
					if (object instanceof VariableDefinitionType) {
						VariableDefinitionType variableDefinitionType = (VariableDefinitionType) object;
						Map<String, String> settings = new HashMap<>();
						settings.put("key", variableDefinitionType.getVariableId());
						JAXBElement<AttributeValueType> attributeValueTypeElement = (JAXBElement<AttributeValueType>) variableDefinitionType.getExpression();
						if (attributeValueTypeElement != null) {
							AttributeValueType attributeValueType = attributeValueTypeElement.getValue();
							settings.put("value", attributeValueType.getContent().get(0).toString());
						}
						decisionList.add(settings);
					} else if (object instanceof RuleType) {
						// get the condition data under the rule for rule Algorithms.
						if(((RuleType) object).getEffect().equals(EffectType.DENY)) {
							if(((RuleType) object).getAdviceExpressions()!=null){
								if("AAF".equalsIgnoreCase(((RuleType) object).getAdviceExpressions().getAdviceExpression().get(0).getAdviceId())){
									policyAdapter.setRuleProvider("AAF");
									break;
								}else if("GUARD_YAML".equalsIgnoreCase(((RuleType) object).getAdviceExpressions().getAdviceExpression().get(0).getAdviceId())){
									policyAdapter.setRuleProvider("GUARD_YAML");
								}else if("GUARD_BL_YAML".equalsIgnoreCase(((RuleType) object).getAdviceExpressions().getAdviceExpression().get(0).getAdviceId())){
									policyAdapter.setRuleProvider("GUARD_BL_YAML");
								}
							}else{
								policyAdapter.setRuleProvider("Custom");
							}
							ConditionType condition = ((RuleType) object).getCondition();
							if (condition != null) {
								ApplyType decisionApply = (ApplyType) condition.getExpression().getValue();
								decisionApply = (ApplyType) decisionApply.getExpression().get(0).getValue();
								ruleAlgoirthmTracker = new LinkedList<>();
								if(policyAdapter.getRuleProvider()!=null && ("GUARD_YAML".equals(policyAdapter.getRuleProvider())||("GUARD_BL_YAML".equals(policyAdapter.getRuleProvider())))){
									YAMLParams yamlParams = new YAMLParams();
									for(int i=0; i<attributeList.size() ; i++){
										Map<String, String> map = (Map<String,String>)attributeList.get(i);
										if("actor".equals(map.get("key"))){
											yamlParams.setActor(map.get("value"));
										}else if("recipe".equals(map.get("key"))){
											yamlParams.setRecipe(map.get("value"));
										}else if("target".equals(map.get("key"))){
											yamlParams.setTargets(Arrays.asList(map.get("value").split("\\|")));
										}else if("clname".equals(map.get("key"))){
											yamlParams.setClname(map.get("value"));
										}
									}
									ApplyType apply = (ApplyType)((ApplyType)decisionApply.getExpression().get(0).getValue()).getExpression().get(0).getValue();
									yamlParams.setGuardActiveStart(((AttributeValueType)apply.getExpression().get(1).getValue()).getContent().get(0).toString());
									yamlParams.setGuardActiveEnd(((AttributeValueType)apply.getExpression().get(2).getValue()).getContent().get(0).toString());
									if("GUARD_BL_YAML".equals(policyAdapter.getRuleProvider())){
										apply = (ApplyType)((ApplyType)((ApplyType)decisionApply.getExpression().get(0).getValue()).getExpression().get(1).getValue()).getExpression().get(2).getValue();
										Iterator<JAXBElement<?>> attributes = apply.getExpression().iterator();
										List<String> blackList = new ArrayList<>();
										while(attributes.hasNext()){
											blackList.add(((AttributeValueType)attributes.next().getValue()).getContent().get(0).toString());
										}
										yamlParams.setBlackList(blackList);
									}else{
										ApplyType timeWindowSection = (ApplyType)((ApplyType)decisionApply.getExpression().get(0).getValue()).getExpression().get(1).getValue();
										yamlParams.setLimit(((AttributeValueType)timeWindowSection.getExpression().get(1).getValue()).getContent().get(0).toString());
										String timeWindow = ((AttributeDesignatorType)((ApplyType)timeWindowSection.getExpression().get(0).getValue()).getExpression().get(0).getValue()).getIssuer();
										yamlParams.setTimeUnits(timeWindow.substring(timeWindow.lastIndexOf(':')+1));
										yamlParams.setTimeWindow(timeWindow.substring(timeWindow.indexOf(":tw:")+4,timeWindow.lastIndexOf(':')));
									}
									policyAdapter.setYamlparams(yamlParams);
									policyAdapter.setAttributes(new ArrayList<Object>());
									policyAdapter.setRuleAlgorithmschoices(new ArrayList<Object>());
									break;
								}
								// Populating Rule Algorithms starting from compound.
								prePopulateDecisionCompoundRuleAlgorithm(index, decisionApply);
								policyAdapter.setRuleAlgorithmschoices(ruleAlgorithmList);
							}
						} else if(policyAdapter.getRuleProvider()!=null && "Rainy_Day".equals(policyAdapter.getRuleProvider())&& ((RuleType) object).getEffect().equals(EffectType.PERMIT)) {
							
							TargetType ruleTarget = ((RuleType) object).getTarget();
							AdviceExpressionsType adviceExpression = ((RuleType) object).getAdviceExpressions();
							
							String errorcode = ruleTarget.getAnyOf().get(0).getAllOf().get(0).getMatch().
									get(1).getAttributeValue().getContent().get(0).toString();
							JAXBElement<AttributeValueType> tempTreatmentObj = (JAXBElement<AttributeValueType>) adviceExpression.getAdviceExpression().
									get(0).getAttributeAssignmentExpression().get(0).getExpression();
							String treatment = tempTreatmentObj.getValue().getContent().get(0).toString();
							
							prePopulateRainyDayTreatments(errorcode, treatment);						

						}
					}
				}
			}
			
			rainydayParams.setTreatmentTableChoices(treatmentList);
			policyAdapter.setRainyday(rainydayParams);
			policyAdapter.setSettings(decisionList);
			
			// if it is Decision MS policy, read the config json
			if("Decision_MS".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				new CreateDcaeMicroServiceController().readFile(policyAdapter, entity);
			} 
		}	

	}

	private void prePopulateRainyDayTreatments(String errorcode, String treatment) {
		Map<String, String> ruleMap = new HashMap<>();
		
		ruleMap.put("errorcode", errorcode);
		ruleMap.put("treatment", treatment);
		treatmentList.add(ruleMap);
		
	}
	
	private void prePopulateDecisionRuleAlgorithms(int index, ApplyType decisionApply, List<JAXBElement<?>> jaxbDecisionTypes) {
		Map<String, String> ruleMap = new HashMap<>();
		ruleMap.put("id", "A" + (index +1));
		Map<String, String> dropDownMap = PolicyController.getDropDownMap();
		for (Entry<String, String> entry : dropDownMap.entrySet()) {
			if (entry.getValue().equals(decisionApply.getFunctionId())) {
				ruleMap.put("dynamicRuleAlgorithmCombo", entry.getKey());
			}
		}
		// Populate the key and value fields
		if ((jaxbDecisionTypes.get(0).getValue() instanceof AttributeValueType)) {
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
			if (policyLogger.isDebugEnabled()) {
				policyLogger.debug("Prepopulating rule algoirthm: " + index);
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
			if (policyLogger.isDebugEnabled()) {
				policyLogger.debug("Prepopulating Compound rule algorithm: " + index);
			}
			Map<String, String> rule = new HashMap<>();
			for (String key : PolicyController.getDropDownMap().keySet()) {
				String keyValue = PolicyController.getDropDownMap().get(key);
				if (keyValue.equals(decisionApply.getFunctionId())) {
					rule.put("dynamicRuleAlgorithmCombo", key);
					break;
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
