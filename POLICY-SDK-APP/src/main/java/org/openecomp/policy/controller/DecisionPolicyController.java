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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.adapter.YAMLParams;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	public DecisionPolicyController(){}

	protected PolicyRestAdapter policyAdapter = null;
	private ArrayList<Object> attributeList;
	private ArrayList<Object> decisionList;
	private ArrayList<Object>  ruleAlgorithmList;
	protected LinkedList<Integer> ruleAlgoirthmTracker;
	public static final String FUNCTION_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";

	@SuppressWarnings("unchecked")
	public void prePopulateDecisionPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		attributeList = new ArrayList<>();
		decisionList = new ArrayList<>();
		ruleAlgorithmList = new ArrayList<>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("_") + 1);
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
										if (attributeId.equals("ECOMPName")) {
											policyAdapter.setEcompName(value);
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
				}

				List<Object> ruleList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
				int index = 0;
				for (Object object : ruleList) {
					if (object instanceof VariableDefinitionType) {
						VariableDefinitionType variableDefinitionType = (VariableDefinitionType) object;
						Map<String, String> settings = new HashMap<String, String>();
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
								if(((RuleType) object).getAdviceExpressions().getAdviceExpression().get(0).getAdviceId().toString().equalsIgnoreCase("AAF")){
									policyAdapter.setRuleProvider("AAF");
									break;
								}else if(((RuleType) object).getAdviceExpressions().getAdviceExpression().get(0).getAdviceId().toString().equalsIgnoreCase("GUARD_YAML")){
									policyAdapter.setRuleProvider("GUARD_YAML");
								}
							}else{
								policyAdapter.setRuleProvider("Custom");
							}
							ConditionType condition = ((RuleType) object).getCondition();
							if (condition != null) {
								ApplyType decisionApply = (ApplyType) condition.getExpression().getValue();
								decisionApply = (ApplyType) decisionApply.getExpression().get(0).getValue();
								ruleAlgoirthmTracker = new LinkedList<>();
								if(policyAdapter.getRuleProvider()!=null && policyAdapter.getRuleProvider().equals("GUARD_YAML")){
									YAMLParams yamlParams = new YAMLParams();
									for(int i=0; i<attributeList.size() ; i++){
										Map<String, String> map = (Map<String,String>)attributeList.get(i);
										if(map.get("key").equals("actor")){
											yamlParams.setActor(map.get("value"));
										}else if(map.get("key").equals("recipe")){
											yamlParams.setRecipe(map.get("value"));
										}
									}
									ApplyType apply = ((ApplyType)((ApplyType)decisionApply.getExpression().get(0).getValue()).getExpression().get(0).getValue());
									yamlParams.setGuardActiveStart(((AttributeValueType)apply.getExpression().get(1).getValue()).getContent().get(0).toString());
									yamlParams.setGuardActiveEnd(((AttributeValueType)apply.getExpression().get(2).getValue()).getContent().get(0).toString());
									yamlParams.setLimit(((AttributeValueType)((ApplyType)decisionApply.getExpression().get(1).getValue()).getExpression().get(1).getValue()).getContent().get(0).toString());
									String timeWindow = ((AttributeDesignatorType)((ApplyType)((ApplyType)decisionApply.getExpression().get(1).getValue()).getExpression().get(0).getValue()).getExpression().get(0).getValue()).getIssuer();
									yamlParams.setTimeWindow(timeWindow.substring(timeWindow.lastIndexOf(":")+1));
									policyAdapter.setYamlparams(yamlParams);
									policyAdapter.setAttributes(new ArrayList<Object>());
									policyAdapter.setRuleAlgorithmschoices(new ArrayList<Object>());
									break;
								}
								// Populating Rule Algorithms starting from compound.
								prePopulateDecisionCompoundRuleAlgorithm(index, decisionApply);
								policyAdapter.setRuleAlgorithmschoices(ruleAlgorithmList);
							}
						}
					}
				}
			}
			policyAdapter.setSettings(decisionList);	
		}	

	}

	private void prePopulateDecisionRuleAlgorithms(int index, ApplyType decisionApply, List<JAXBElement<?>> jaxbDecisionTypes) {
		Map<String, String> ruleMap = new HashMap<>();
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
