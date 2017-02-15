/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.xacml.rest.components;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;

import org.apache.commons.io.FilenameUtils;
import org.openecomp.policy.pap.xacml.rest.adapters.PolicyRestAdapter;
import org.openecomp.policy.pap.xacml.rest.util.JPAUtils;
import org.openecomp.policy.rest.jpa.Datatype;
import org.openecomp.policy.rest.jpa.DecisionSettings;
import org.openecomp.policy.rest.jpa.FunctionDefinition;
import org.openecomp.policy.xacml.std.pip.engines.aaf.AAFEngine;

import com.att.research.xacml.std.IdentifierImpl;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

public class DecisionPolicy extends Policy {

	/**
	 * Config Fields
	 */
	private static final Logger logger = FlexLogger.getLogger(ConfigPolicy.class);

	public static final String JSON_CONFIG = "JSON";
	public static final String XML_CONFIG = "XML";
	public static final String PROPERTIES_CONFIG = "PROPERTIES";
	public static final String OTHER_CONFIG = "OTHER";
	
	public static final String PDP_ACTION = "PDP";
	public static final String PEP_ACTION = "PEP";
	public static final String TYPE_ACTION = "REST";

	public static final String GET_METHOD = "GET";
	public static final String PUT_METHOD = "PUT";
	public static final String POST_METHOD = "POST";

	public static final String PERFORMER_ATTRIBUTEID = "performer";
	public static final String TYPE_ATTRIBUTEID = "type";
	public static final String METHOD_ATTRIBUTEID = "method";
	public static final String HEADERS_ATTRIBUTEID = "headers";
	public static final String URL_ATTRIBUTEID = "url";
	public static final String BODY_ATTRIBUTEID = "body";
	
	public static final String FUNCTION_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";
	
	private static final String AAFProvider = "AAF";
	//private static final String CustomProvider = "Custom";
	
	List<String> dynamicLabelRuleAlgorithms = new LinkedList<String>();
	List<String> dynamicFieldComboRuleAlgorithms = new LinkedList<String>();
	List<String> dynamicFieldOneRuleAlgorithms = new LinkedList<String>();
	List<String> dynamicFieldTwoRuleAlgorithms = new LinkedList<String>();
	//List<Object> dynamicVariableList = new LinkedList<Object>();
	List<String> dataTypeList = new LinkedList<String>();
	
	protected Map<String, String> dropDownMap = new HashMap<String, String>();
	

	public DecisionPolicy() {
		super();
	}
	
	public DecisionPolicy(PolicyRestAdapter policyAdapter){
		this.policyAdapter = policyAdapter;
	}
	
	@Override
	public Map<String, String> savePolicies() throws Exception {
		
		Map<String, String> successMap = new HashMap<String,String>();
		if(isPolicyExists()){
			successMap.put("EXISTS", "This Policy already exist on the PAP");
			return successMap;
		}
		
		if(!isPreparedToSave()){
			//Prep and configure the policy for saving
			prepareToSave();
		}

		// Until here we prepared the data and here calling the method to create xml.
		Path newPolicyPath = null;
		newPolicyPath = Paths.get(policyAdapter.getParentPath().toString(), policyName);
		successMap = createPolicy(newPolicyPath, getCorrectPolicyDataObject());		
		if (successMap.containsKey("success")) {
			Path finalPolicyPath = getFinalPolicyPath();
			policyAdapter.setFinalPolicyPath(finalPolicyPath.toString());
		}
		return successMap;		
	}
	
	//This is the method for preparing the policy for saving.  We have broken it out
	//separately because the fully configured policy is used for multiple things
	@Override
	public boolean prepareToSave() throws Exception{

		if(isPreparedToSave()){
			//we have already done this
			return true;
		}
		
		int version = 0;
		String policyID = policyAdapter.getPolicyID();
		
		if (policyAdapter.isEditPolicy()) {
			version = policyAdapter.getHighestVersion() + 1;
		} else {
			version = 1;
		}
		
		// Create the Instance for pojo, PolicyType object is used in marshalling.
		if (policyAdapter.getPolicyType().equals("Decision")) {
			PolicyType policyConfig = new PolicyType();

			policyConfig.setVersion(Integer.toString(version));
			policyConfig.setPolicyId(policyID);
			policyConfig.setTarget(new TargetType());
			policyAdapter.setData(policyConfig);
		}
		
		if (policyAdapter.getData() != null) {
			
			// Save off everything
			// making ready all the required elements to generate the action policy xml.
			// Get the uniqueness for policy name.
			Path newFile = getNextFilename(Paths.get(policyAdapter.getParentPath().toString()), policyAdapter.getPolicyType(), policyAdapter.getPolicyName(), version);
			if (newFile == null) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error("File already exists, cannot create the policy.");
				PolicyLogger.error("File already exists, cannot create the policy.");
				setPolicyExists(true);
				return false;
			}
			policyName = newFile.getFileName().toString();
			
			// Make sure the filename ends with an extension
			if (policyName.endsWith(".xml") == false) {
				policyName = policyName + ".xml";
			}
			
			PolicyType decisionPolicy = (PolicyType)  policyAdapter.getData();
			
			decisionPolicy.setDescription(policyAdapter.getPolicyDescription());
			
			decisionPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());
			AllOfType allOfOne = new AllOfType();
			final Path gitPath = Paths.get(policyAdapter.getUserGitPath().toString());
			String policyDir = policyAdapter.getParentPath().toString();
			int startIndex = policyDir.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
			policyDir = policyDir.substring(startIndex, policyDir.length());
			logger.info("print the main domain value "+policyDir);
			String path = policyDir.replace('\\', '.');
			if(path.contains("/")){
				path = policyDir.replace('/', '.');
				logger.info("print the path:" +path);
			}
			String fileName = FilenameUtils.removeExtension(policyName);
			fileName = path + "." + fileName + ".xml";
			String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
			if ((name == null) || (name.equals(""))) {
				name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
			}
			allOfOne.getMatch().add(createMatch("PolicyName", name));
			
			AllOfType allOf = new AllOfType();
			
			// Match for Ecomp
			allOf.getMatch().add(createMatch("ECOMPName", (policyAdapter.getEcompName())));
			
			Map<String, String> dynamicFieldComponentAttributes = policyAdapter.getDynamicFieldConfigAttributes();
			if(policyAdapter.getProviderComboBox()!=null && policyAdapter.getProviderComboBox().equals(AAFProvider)){
				dynamicFieldComponentAttributes = new HashMap<String,String>();
			}
			
			// If there is any dynamic field attributes create the matches here
			for (String keyField : dynamicFieldComponentAttributes.keySet()) {
				String key = keyField;
				String value = dynamicFieldComponentAttributes.get(key);
				MatchType dynamicMatch = createDynamicMatch(key, value);
				allOf.getMatch().add(dynamicMatch);
			}

			AnyOfType anyOf = new AnyOfType();
			anyOf.getAllOf().add(allOfOne);
			anyOf.getAllOf().add(allOf);

			TargetType target = new TargetType();
			target.getAnyOf().add(anyOf);
			decisionPolicy.setTarget(target);

			Map<String, String> dynamicFieldDecisionSettings = policyAdapter.getDynamicSettingsMap();
			
			//dynamicVariableList = policyAdapter.getDynamicVariableList();
			if(policyAdapter.getProviderComboBox()!=null && policyAdapter.getProviderComboBox().equals(AAFProvider)){
				dynamicFieldDecisionSettings = new HashMap<String,String>();
			}
			
			// settings are dynamic so check how many rows are added and add all
			for (String keyField : dynamicFieldDecisionSettings.keySet()) {
				String key = keyField;
				String value = dynamicFieldDecisionSettings.get(key);
				//String dataType = (String) dynamicVariableList.get(counter);
				String dataType = getDataType(key);
				VariableDefinitionType dynamicVariable = createDynamicVariable(key, value, dataType);
				decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(dynamicVariable);
			}

			createRule(decisionPolicy, true);
			createRule(decisionPolicy, false);
		}

		setPreparedToSave(true);
		return true;
	}
	
	private DecisionSettings findDecisionSettingsBySettingId(String settingId) {
		DecisionSettings decisionSetting = null;
		
		EntityManager em = policyAdapter.getEntityManagerFactory().createEntityManager();
		Query getDecisionSettings = em.createNamedQuery("DecisionSettings.findAll");
		List<?> decisionSettingsList = getDecisionSettings.getResultList();
		
		for (Object id : decisionSettingsList) {
			decisionSetting = (DecisionSettings) id;
			if (decisionSetting.getXacmlId().equals(settingId)) {
				break;
			}
		}
		return decisionSetting;
	}
	
	private void createRule(PolicyType decisionPolicy, boolean permitRule) {
		RuleType rule = new RuleType();
			
		rule.setRuleId(policyAdapter.getRuleID());
			
		if (permitRule) {
			rule.setEffect(EffectType.PERMIT);
		} else {
			rule.setEffect(EffectType.DENY);
		}
		rule.setTarget(new TargetType());

		// Create Target in Rule
		AllOfType allOfInRule = new AllOfType();

		// Creating match for ACCESS in rule target
		MatchType accessMatch = new MatchType();
		AttributeValueType accessAttributeValue = new AttributeValueType();
		accessAttributeValue.setDataType(STRING_DATATYPE);
		accessAttributeValue.getContent().add("DECIDE");
		accessMatch.setAttributeValue(accessAttributeValue);
		AttributeDesignatorType accessAttributeDesignator = new AttributeDesignatorType();
		URI accessURI = null;
		try {
			accessURI = new URI(ACTION_ID);
		} catch (URISyntaxException e) {
			//TODO:EELF Cleanup - Remove logger
			//logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getStackTrace());
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "DecisionPolicy", "Exception creating ACCESS URI");
		}
		accessAttributeDesignator.setCategory(CATEGORY_ACTION);
		accessAttributeDesignator.setDataType(STRING_DATATYPE);
		accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
		accessMatch.setAttributeDesignator(accessAttributeDesignator);
		accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);
		
		dynamicLabelRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmLabels();
		dynamicFieldComboRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmCombo();
		dynamicFieldOneRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmField1();
		dynamicFieldTwoRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmField2();
		dropDownMap = createDropDownMap();
		
		if(policyAdapter.getProviderComboBox()!=null && policyAdapter.getProviderComboBox().equals(AAFProvider)){
			// Values for AAF Provider are here for XML Creation. 
			ConditionType condition = new ConditionType();
			ApplyType decisionApply = new ApplyType();
			String selectedFunction = "boolean-equal";
			
			AttributeValueType value1 = new AttributeValueType();
			value1.setDataType(BOOLEAN_DATATYPE);
			value1.getContent().add("true");
			
			AttributeDesignatorType value2 = new AttributeDesignatorType();
			value2.setAttributeId(AAFEngine.AAF_RESULT);
			value2.setCategory(CATEGORY_RESOURCE);
			value2.setDataType(BOOLEAN_DATATYPE);
			value2.setMustBePresent(false);
			
			ApplyType innerDecisionApply = new ApplyType();
			innerDecisionApply.setFunctionId(FUNCTION_BOOLEAN_ONE_AND_ONLY);
			innerDecisionApply.getExpression().add(new ObjectFactory().createAttributeDesignator(value2));
			
			decisionApply.setFunctionId(dropDownMap.get(selectedFunction));
			decisionApply.getExpression().add(new ObjectFactory().createAttributeValue(value1));
			decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply));
			condition.setExpression(new ObjectFactory().createApply(decisionApply));
			if (!permitRule) {
				ApplyType notOuterApply = new ApplyType();
				notOuterApply.setFunctionId(FUNCTION_NOT);
				notOuterApply.getExpression().add(condition.getExpression());
				condition.setExpression(new ObjectFactory().createApply(notOuterApply));
			}
			rule.setCondition(condition);
			allOfInRule.getMatch().add(accessMatch);

			AnyOfType anyOfInRule = new AnyOfType();
			anyOfInRule.getAllOf().add(allOfInRule);

			TargetType targetInRule = new TargetType();
			targetInRule.getAnyOf().add(anyOfInRule);

			rule.setTarget(targetInRule);
			if(!permitRule){
				AdviceExpressionsType adviceExpressions = new AdviceExpressionsType();
				AdviceExpressionType adviceExpression = new AdviceExpressionType();
				adviceExpression.setAdviceId(AAFProvider);
				adviceExpression.setAppliesTo(EffectType.DENY);
				AttributeAssignmentExpressionType assignment = new AttributeAssignmentExpressionType();
				assignment.setAttributeId("aaf.response");
				assignment.setCategory(CATEGORY_RESOURCE);
				AttributeDesignatorType value = new AttributeDesignatorType();
				value.setAttributeId(AAFEngine.AAF_RESPONSE);
				value.setCategory(CATEGORY_RESOURCE);
				value.setDataType(STRING_DATATYPE);
				value.setMustBePresent(false);
				assignment.setExpression(new ObjectFactory().createAttributeDesignator(value));
				adviceExpression.getAttributeAssignmentExpression().add(assignment);
				adviceExpressions.getAdviceExpression().add(adviceExpression);
				rule.setAdviceExpressions(adviceExpressions);
			}
			decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			policyAdapter.setPolicyData(decisionPolicy);
			
		}else if (dynamicLabelRuleAlgorithms != null && dynamicLabelRuleAlgorithms.size() > 0) {
			boolean isCompound = false;
			ConditionType condition = new ConditionType();
			int index = dynamicFieldOneRuleAlgorithms.size() - 1;
			
			for (String labelAttr : dynamicLabelRuleAlgorithms) {
				// if the rule algorithm as a label means it is a compound
				if (dynamicFieldOneRuleAlgorithms.get(index).toString().equals(labelAttr)) {
					ApplyType decisionApply = new ApplyType();

					String selectedFunction = (String) dynamicFieldComboRuleAlgorithms.get(index).toString();
					String value1 = (String) dynamicFieldOneRuleAlgorithms.get(index).toString();
					String value2 = dynamicFieldTwoRuleAlgorithms.get(index).toString();
					decisionApply.setFunctionId(dropDownMap.get(selectedFunction));
					decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value1)));
					decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value2)));
					condition.setExpression(new ObjectFactory().createApply(decisionApply));
					isCompound = true;
				}

				// if rule algorithm not a compound
				if (!isCompound) {
					condition.setExpression(new ObjectFactory().createApply(getInnerDecisionApply(dynamicLabelRuleAlgorithms.get(index).toString())));
				}
			}
			if (!permitRule) {
				ApplyType notOuterApply = new ApplyType();
				notOuterApply.setFunctionId(FUNCTION_NOT);
				notOuterApply.getExpression().add(condition.getExpression());
				condition.setExpression(new ObjectFactory().createApply(notOuterApply));
			}
			rule.setCondition(condition);
			allOfInRule.getMatch().add(accessMatch);

			AnyOfType anyOfInRule = new AnyOfType();
			anyOfInRule.getAllOf().add(allOfInRule);

			TargetType targetInRule = new TargetType();
			targetInRule.getAnyOf().add(anyOfInRule);

			rule.setTarget(targetInRule);

			decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			policyAdapter.setPolicyData(decisionPolicy);
			
		} else {
			//TODO:EELF Cleanup - Remove logger
			//logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Unsupported data object."+ policyAdapter.getData().getClass().getCanonicalName());
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Unsupported data object."+ policyAdapter.getData().getClass().getCanonicalName());
		}

	}
	
	// if compound setting the inner apply here
	protected ApplyType getInnerDecisionApply(String value1Label) {
		ApplyType decisionApply = new ApplyType();
		int index = 0;
		// check the index for the label.
		for (String labelAttr : dynamicLabelRuleAlgorithms) {
			if (labelAttr.equals(value1Label)) {
				String value1 = (String) dynamicFieldOneRuleAlgorithms.get(index).toString();
				populateDataTypeList(value1);

				// check if the row contains label again
				for (String labelValue : dynamicLabelRuleAlgorithms) {
					if (labelValue.equals(value1)) {
						return getCompoundDecisionApply(index);
					}
				}

				// Getting the values from the form.
				String functionKey = (String) dynamicFieldComboRuleAlgorithms.get(index).toString();
				String value2 = dynamicFieldTwoRuleAlgorithms.get(index).toString();
				decisionApply.setFunctionId(dropDownMap.get(functionKey));
				// if two text field are rule attributes.
				if ((value1.contains(RULE_VARIABLE)) && (value2.contains(RULE_VARIABLE))) {
					ApplyType innerDecisionApply1 = new ApplyType();
					ApplyType	innerDecisionApply2	 = new  ApplyType();							  
					AttributeDesignatorType  attributeDesignator1	  = new AttributeDesignatorType();								 
					AttributeDesignatorType  attributeDesignator2	  = new AttributeDesignatorType();											 
					//If selected function is Integer function set integer functionID
					if(functionKey.toLowerCase().contains("integer")){
						innerDecisionApply1.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY );
						innerDecisionApply2.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
						attributeDesignator1.setDataType(INTEGER_DATATYPE);
						attributeDesignator2.setDataType(INTEGER_DATATYPE);
					} else{
						//If selected function is not a Integer function set String functionID
						innerDecisionApply1.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
						innerDecisionApply2.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
						attributeDesignator1.setDataType(STRING_DATATYPE);
						attributeDesignator2.setDataType(STRING_DATATYPE);
					}
					attributeDesignator1.setCategory(CATEGORY_RESOURCE);
					attributeDesignator2.setCategory(CATEGORY_RESOURCE);
					//Here set actual field values
					attributeDesignator1.setAttributeId(value1.  contains("resource:")?value1.substring( 9):value1.substring(8));
					attributeDesignator2.setAttributeId(value1.  contains("resource:")?value1.substring( 9):value1.substring(8));							 
					innerDecisionApply1.getExpression().add(new ObjectFactory().createAttributeDesignator( attributeDesignator1));
					innerDecisionApply2.getExpression().add(new ObjectFactory().createAttributeDesignator( attributeDesignator2));							  
					decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply1));
					decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply2));							  
				} else {
					// if either of one text field is rule attribute.
					if (!value1.startsWith("S_")) {
						ApplyType innerDecisionApply = new ApplyType();
						AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
						AttributeValueType decisionConditionAttributeValue = new AttributeValueType();

						if (functionKey.toLowerCase().contains("integer")) {
							innerDecisionApply.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
							decisionConditionAttributeValue.setDataType(INTEGER_DATATYPE);
							attributeDesignator.setDataType(INTEGER_DATATYPE);
						} else {
							innerDecisionApply.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
							decisionConditionAttributeValue.setDataType(STRING_DATATYPE);
							attributeDesignator.setDataType(STRING_DATATYPE);
						}

						String attributeId = null;
						String attributeValue = null;

						// Find which textField has rule attribute and set it as
						// attributeId and the other as attributeValue.
						attributeId = value1;
						attributeValue = value2;
			
						if (attributeId != null) {
							attributeDesignator.setCategory(CATEGORY_RESOURCE);
							attributeDesignator.setAttributeId(attributeId);
						}
						decisionConditionAttributeValue.getContent().add(attributeValue);
						innerDecisionApply.getExpression().add(new ObjectFactory().createAttributeDesignator(attributeDesignator));
						decisionApply.getExpression().add(new ObjectFactory().createAttributeValue(decisionConditionAttributeValue));
						decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply));
					} else {
						value1 = value1.substring(2, value1.length());
						VariableReferenceType variableReferenceType = new VariableReferenceType();
						variableReferenceType.setVariableId(value1);
						
						String dataType = dataTypeList.get(index);

						AttributeValueType decisionConditionAttributeValue = new AttributeValueType();
						decisionConditionAttributeValue.setDataType(dataType);
						decisionConditionAttributeValue.getContent().add(value2);
						decisionApply.getExpression().add(new ObjectFactory().createVariableReference(variableReferenceType));
						decisionApply.getExpression().add(new ObjectFactory().createAttributeValue(decisionConditionAttributeValue));
					}
				}
			}
			index++;
		}
		return decisionApply;
	}

	// if the rule algorithm is multiple compound one setting the apply
	protected ApplyType getCompoundDecisionApply(int index) {
		ApplyType decisionApply = new ApplyType();
		String selectedFunction = dynamicFieldComboRuleAlgorithms.get(index).toString();
		String value1 = dynamicFieldOneRuleAlgorithms.get(index).toString();
		String value2 = dynamicFieldTwoRuleAlgorithms.get(index).toString();
		decisionApply.setFunctionId(dropDownMap.get(selectedFunction));
		decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value1)));
		decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value2)));
		return decisionApply;
	}
	
	private VariableDefinitionType createDynamicVariable(String key, String value, String dataType) {
		VariableDefinitionType dynamicVariable = new VariableDefinitionType();
		AttributeValueType dynamicAttributeValue = new AttributeValueType();

		dynamicAttributeValue.setDataType(dataType);
		dynamicAttributeValue.getContent().add(value);

		dynamicVariable.setVariableId(key);
		dynamicVariable.setExpression(new ObjectFactory().createAttributeValue(dynamicAttributeValue));

		return dynamicVariable;

	}	
	
	private void populateDataTypeList(String value1) {
		
		///String value1 = dynamicFieldDecisionOneRuleAlgorithms.get(index).getValue().toString();
		String dataType = null;

		if(value1.contains("S_")) {
			value1 = value1.substring(2, value1.length());
			DecisionSettings decisionSettings = findDecisionSettingsBySettingId(value1);
			if (decisionSettings != null && decisionSettings.getDatatypeBean().getShortName().equals("string")) {
				dataType = STRING_DATATYPE;
			} else if (decisionSettings != null && decisionSettings.getDatatypeBean().getShortName().equals("boolean")) {
				dataType = BOOLEAN_DATATYPE;
			} else {
				dataType = INTEGER_DATATYPE;
			}
		} else {
			dataType = "OTHER";
		}

		dataTypeList.add(dataType);
	}
	
	private Map<String,String> createDropDownMap(){
		JPAUtils jpaUtils = null;
		try {
			jpaUtils = JPAUtils.getJPAUtilsInstance(policyAdapter.getEntityManagerFactory());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Datatype, List<FunctionDefinition>> functionMap = jpaUtils.getFunctionDatatypeMap();
		Map<String, String> dropDownMap = new HashMap<String, String>();
		for (Datatype id : functionMap.keySet()) {
			List<FunctionDefinition> functionDefinitions = (List<FunctionDefinition>) functionMap
					.get(id);
			for (FunctionDefinition functionDef : functionDefinitions) {
				dropDownMap.put(functionDef.getShortname(),functionDef.getXacmlid());
			}
		}
		
		return dropDownMap;
	}
	
	private String getDataType(String key) {
		
		DecisionSettings decisionSettings = findDecisionSettingsBySettingId(key);
		String dataType = null;
		
		if (decisionSettings != null && decisionSettings.getDatatypeBean().getShortName().equals("string")) {
			dataType = STRING_DATATYPE;
		} else if (decisionSettings != null && decisionSettings.getDatatypeBean().getShortName().equals("boolean")) {
			dataType = BOOLEAN_DATATYPE;
		} else {
			dataType = INTEGER_DATATYPE;
		}
		
		return dataType;
	}

	@Override
	public Object getCorrectPolicyDataObject() {
		return policyAdapter.getData();
	}



}
