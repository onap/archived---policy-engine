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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.json.stream.JsonGenerationException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.pap.xacml.rest.adapters.PolicyRestAdapter;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.std.IdentifierImpl;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 

public class MicroServiceConfigPolicy extends Policy {
	
	/**
	 * Config Fields
	 */
	private static final Logger logger = FlexLogger.getLogger(ConfigPolicy.class);

	public MicroServiceConfigPolicy() {
		super();
	}
	
	public MicroServiceConfigPolicy(PolicyRestAdapter policyAdapter){
		this.policyAdapter = policyAdapter;
	}

	//save configuration of the policy based on the policyname
	private void saveConfigurations(String policyName, String prevPolicyName, String jsonBody) {
		String domain = getParentPathSubScopeDir();
		String path = domain.replace('\\', '.');
		if(path.contains("/")){
			path = domain.replace('/', '.');
			logger.info("print the path:" +path);
		}
		try {
			String body = null;
			try {
				body = jsonBody;
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println(body);
			if(policyName.endsWith(".xml")){
				policyName	 = policyName.substring(0, policyName.lastIndexOf(".xml"));	
			}
			PrintWriter out = new PrintWriter(CONFIG_HOME + File.separator+path + "."+ policyName +".json");
			out.println(body);
			out.close();

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//getting the policy name and setting to configuration on adding .json
	private String getConfigFile(String filename) {
		filename = FilenameUtils.removeExtension(filename);
		if (filename.endsWith(".xml")) {
			filename = filename.substring(0, filename.length() - 4);
		}
		filename = filename +".json";
		return filename;
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
		successMap = createPolicy(newPolicyPath,getCorrectPolicyDataObject() );		
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
		if (policyAdapter.getPolicyType().equals("Config")) {
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
			String prevPolicyName = null;
			if(policyAdapter.isEditPolicy()){
				prevPolicyName = "Config_MS_" + policyAdapter.getPolicyName() + "." + policyAdapter.getHighestVersion() + ".xml";
			}
			
			Path newFile = this.getNextLoopFilename(Paths.get(policyAdapter.getParentPath()), policyAdapter.getPolicyType(), 
					policyAdapter.getConfigPolicyType(), policyAdapter.getPolicyName(), version);
			
			if (newFile == null) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error("Policy already Exists, cannot create the policy.");
				PolicyLogger.error("Policy already Exists, cannot create the policy.");
				setPolicyExists(true);
				return false;
			}
			
			policyName = newFile.getFileName().toString();
			
			// Save the Configurations file with the policy name with extention based on selection.
			String jsonBody = policyAdapter.getJsonBody();
			saveConfigurations(policyName, prevPolicyName, jsonBody);
			
			// Make sure the filename ends with an extension
			if (policyName.endsWith(".xml") == false) {
				policyName = policyName + ".xml";
			}
			
	
			PolicyType configPolicy = (PolicyType) policyAdapter.getData();
			
			configPolicy.setDescription(policyAdapter.getPolicyDescription());

			configPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());
			
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
			
			// Match for policyName
			allOfOne.getMatch().add(createMatch("PolicyName", name));
			
			AllOfType allOf = new AllOfType();
		
			// Adding the matches to AllOfType element Match for Ecomp
			allOf.getMatch().add(createMatch("ECOMPName", policyAdapter.getEcompName()));
			// Match for ConfigName
			allOf.getMatch().add(createMatch("ConfigName", policyAdapter.getConfigName()));
			// Match for Service
			allOf.getMatch().add(createDynamicMatch("service", policyAdapter.getServiceType()));
			// Match for uuid
			allOf.getMatch().add(createDynamicMatch("uuid", policyAdapter.getUuid()));
			// Match for location
			allOf.getMatch().add(createDynamicMatch("location", policyAdapter.getLocation()));
			// Match for riskType
			allOf.getMatch().add(
					createDynamicMatch("RiskType", policyAdapter.getRiskType()));
			// Match for riskLevel
			allOf.getMatch().add(
					createDynamicMatch("RiskLevel", String.valueOf(policyAdapter.getRiskLevel())));
			// Match for riskguard
			allOf.getMatch().add(
					createDynamicMatch("guard", policyAdapter.getGuard()));
			// Match for ttlDate
			allOf.getMatch().add(
					createDynamicMatch("TTLDate", policyAdapter.getTtlDate()));

			AnyOfType anyOf = new AnyOfType();
			anyOf.getAllOf().add(allOfOne);
			anyOf.getAllOf().add(allOf);

			TargetType target = new TargetType();
			((TargetType) target).getAnyOf().add(anyOf);
			
			// Adding the target to the policy element
			configPolicy.setTarget((TargetType) target);

			RuleType rule = new RuleType();
			rule.setRuleId(policyAdapter.getRuleID());
			
			rule.setEffect(EffectType.PERMIT);
			
			// Create Target in Rule
			AllOfType allOfInRule = new AllOfType();

			// Creating match for ACCESS in rule target
			MatchType accessMatch = new MatchType();
			AttributeValueType accessAttributeValue = new AttributeValueType();
			accessAttributeValue.setDataType(STRING_DATATYPE);
			accessAttributeValue.getContent().add("ACCESS");
			accessMatch.setAttributeValue(accessAttributeValue);
			AttributeDesignatorType accessAttributeDesignator = new AttributeDesignatorType();
			URI accessURI = null;
			try {
				accessURI = new URI(ACTION_ID);
			} catch (URISyntaxException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getStackTrace());
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "MicroServiceConfigPolicy", "Exception creating ACCESS URI");
			}
			accessAttributeDesignator.setCategory(CATEGORY_ACTION);
			accessAttributeDesignator.setDataType(STRING_DATATYPE);
			accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
			accessMatch.setAttributeDesignator(accessAttributeDesignator);
			accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

			// Creating Config Match in rule Target
			MatchType configMatch = new MatchType();
			AttributeValueType configAttributeValue = new AttributeValueType();
			configAttributeValue.setDataType(STRING_DATATYPE);
			configAttributeValue.getContent().add("Config");
			configMatch.setAttributeValue(configAttributeValue);
			AttributeDesignatorType configAttributeDesignator = new AttributeDesignatorType();
			URI configURI = null;
			try {
				configURI = new URI(RESOURCE_ID);
			} catch (URISyntaxException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getStackTrace());
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "MicroServiceConfigPolicy", "Exception creating Config URI");
			}
			configAttributeDesignator.setCategory(CATEGORY_RESOURCE);
			configAttributeDesignator.setDataType(STRING_DATATYPE);
			configAttributeDesignator.setAttributeId(new IdentifierImpl(configURI).stringValue());
			configMatch.setAttributeDesignator(configAttributeDesignator);
			configMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

			allOfInRule.getMatch().add(accessMatch);
			allOfInRule.getMatch().add(configMatch);

			AnyOfType anyOfInRule = new AnyOfType();
			anyOfInRule.getAllOf().add(allOfInRule);

			TargetType targetInRule = new TargetType();
			targetInRule.getAnyOf().add(anyOfInRule);

			rule.setTarget(targetInRule);
			rule.setAdviceExpressions(getAdviceExpressions(version, policyName));

			configPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			policyAdapter.setPolicyData(configPolicy);

		} else {
			//TODO:EELF Cleanup - Remove logger
			//logger.error("Unsupported data object." + policyAdapter.getData().getClass().getCanonicalName());
			PolicyLogger.error("Unsupported data object." + policyAdapter.getData().getClass().getCanonicalName());
		}
		setPreparedToSave(true);
		return true;
	}
	
	// Data required for Advice part is setting here.
	private AdviceExpressionsType getAdviceExpressions(int version, String fileName) {
		AdviceExpressionsType advices = new AdviceExpressionsType();
		AdviceExpressionType advice = new AdviceExpressionType();
		advice.setAdviceId("MSID");
		advice.setAppliesTo(EffectType.PERMIT);
		// For Configuration
		AttributeAssignmentExpressionType assignment1 = new AttributeAssignmentExpressionType();
		assignment1.setAttributeId("type");
		assignment1.setCategory(CATEGORY_RESOURCE);
		assignment1.setIssuer("");

		AttributeValueType configNameAttributeValue = new AttributeValueType();
		configNameAttributeValue.setDataType(STRING_DATATYPE);
		configNameAttributeValue.getContent().add("Configuration");
		assignment1.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue));

		advice.getAttributeAssignmentExpression().add(assignment1);
		final Path gitPath = Paths.get(policyAdapter.getUserGitPath().toString());
		// For Config file Url if configurations are provided.
		AttributeAssignmentExpressionType assignment2 = new AttributeAssignmentExpressionType();
		assignment2.setAttributeId("URLID");
		assignment2.setCategory(CATEGORY_RESOURCE);
		assignment2.setIssuer("");

		AttributeValueType AttributeValue = new AttributeValueType();
		AttributeValue.setDataType(URI_DATATYPE);
		String policyDir1 = policyAdapter.getParentPath().toString();
		int startIndex1 = policyDir1.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
		policyDir1 = policyDir1.substring(startIndex1, policyDir1.length());
		logger.info("print the main domain value"+policyDir1);
		String path = policyDir1.replace('\\', '.');
		if(path.contains("/")){
			path = policyDir1.replace('/', '.');
			logger.info("print the path:" +path);
		}
		String content = CONFIG_URL +"/Config/" + path + "." + getConfigFile(policyName);
		System.out.println("URL value :" + content);
		AttributeValue.getContent().add(content);
		assignment2.setExpression(new ObjectFactory().createAttributeValue(AttributeValue));

		advice.getAttributeAssignmentExpression().add(assignment2);
		AttributeAssignmentExpressionType assignment3 = new AttributeAssignmentExpressionType();
		assignment3.setAttributeId("PolicyName");
		assignment3.setCategory(CATEGORY_RESOURCE);
		assignment3.setIssuer("");

		AttributeValueType attributeValue3 = new AttributeValueType();
		attributeValue3.setDataType(STRING_DATATYPE);
		String policyDir = policyAdapter.getParentPath().toString();
		int startIndex = policyDir.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
		policyDir = policyDir.substring(startIndex, policyDir.length());
		StringTokenizer tokenizer = null;
		StringBuffer buffer = new StringBuffer();
		if (policyDir.contains("\\")) {
			tokenizer = new StringTokenizer(policyDir, "\\");
		} else {
			tokenizer = new StringTokenizer(policyDir, "/");
		}
		if (tokenizer != null) {
			while (tokenizer.hasMoreElements()) {
				String value = tokenizer.nextToken();
				buffer.append(value);
				buffer.append(".");
			}
		}
		fileName = FilenameUtils.removeExtension(fileName);
		fileName = buffer.toString() + fileName + ".xml";
		String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
		if ((name == null) || (name.equals(""))) {
			name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
		}
		attributeValue3.getContent().add(name);
		assignment3.setExpression(new ObjectFactory().createAttributeValue(attributeValue3));
		advice.getAttributeAssignmentExpression().add(assignment3);

		AttributeAssignmentExpressionType assignment4 = new AttributeAssignmentExpressionType();
		assignment4.setAttributeId("VersionNumber");
		assignment4.setCategory(CATEGORY_RESOURCE);
		assignment4.setIssuer("");

		AttributeValueType configNameAttributeValue4 = new AttributeValueType();
		configNameAttributeValue4.setDataType(STRING_DATATYPE);
		configNameAttributeValue4.getContent().add(Integer.toString(version));
		assignment4.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue4));

		advice.getAttributeAssignmentExpression().add(assignment4);

		AttributeAssignmentExpressionType assignment5 = new AttributeAssignmentExpressionType();
		assignment5.setAttributeId("matching:" + this.ECOMPID);
		assignment5.setCategory(CATEGORY_RESOURCE);
		assignment5.setIssuer("");

		AttributeValueType configNameAttributeValue5 = new AttributeValueType();
		configNameAttributeValue5.setDataType(STRING_DATATYPE);
		configNameAttributeValue5.getContent().add(policyAdapter.getEcompName());
		assignment5.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue5));

		advice.getAttributeAssignmentExpression().add(assignment5);

		AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
		assignment6.setAttributeId("matching:" + this.CONFIGID);
		assignment6.setCategory(CATEGORY_RESOURCE);
		assignment6.setIssuer("");

		AttributeValueType configNameAttributeValue6 = new AttributeValueType();
		configNameAttributeValue6.setDataType(STRING_DATATYPE);
		configNameAttributeValue6.getContent().add(policyAdapter.getConfigName());
		assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));

		advice.getAttributeAssignmentExpression().add(assignment6);
		AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
		assignment7.setAttributeId("matching:service");
		assignment7.setCategory(CATEGORY_RESOURCE);
		assignment7.setIssuer("");

		AttributeValueType configNameAttributeValue7 = new AttributeValueType();
		configNameAttributeValue7.setDataType(STRING_DATATYPE);
		configNameAttributeValue7.getContent().add(policyAdapter.getServiceType());
		assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));

		advice.getAttributeAssignmentExpression().add(assignment7);
		
		AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
		assignment8.setAttributeId("matching:uuid");
		assignment8.setCategory(CATEGORY_RESOURCE);
		assignment8.setIssuer("");

		AttributeValueType configNameAttributeValue8 = new AttributeValueType();
		configNameAttributeValue8.setDataType(STRING_DATATYPE);
		configNameAttributeValue8.getContent().add(policyAdapter.getUuid());
		assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

		advice.getAttributeAssignmentExpression().add(assignment8);
        
		AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
		assignment9.setAttributeId("matching:Location");
		assignment9.setCategory(CATEGORY_RESOURCE);
		assignment9.setIssuer("");

		AttributeValueType configNameAttributeValue9 = new AttributeValueType();
		configNameAttributeValue9.setDataType(STRING_DATATYPE);
		configNameAttributeValue9.getContent().add(policyAdapter.getLocation());
		assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

		advice.getAttributeAssignmentExpression().add(assignment9);
		
		AttributeAssignmentExpressionType assignment10 = new AttributeAssignmentExpressionType();
		assignment10.setAttributeId("Priority");
		assignment10.setCategory(CATEGORY_RESOURCE);
		assignment10.setIssuer("");

		AttributeValueType configNameAttributeValue10 = new AttributeValueType();
		configNameAttributeValue10.setDataType(STRING_DATATYPE);
		configNameAttributeValue10.getContent().add(policyAdapter.getPriority());
		assignment10.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue10));

		advice.getAttributeAssignmentExpression().add(assignment10);
		
		//Risk Attributes
		AttributeAssignmentExpressionType assignment11 = new AttributeAssignmentExpressionType();
		assignment11.setAttributeId("RiskType");
		assignment11.setCategory(CATEGORY_RESOURCE);
		assignment11.setIssuer("");

		AttributeValueType configNameAttributeValue11 = new AttributeValueType();
		configNameAttributeValue11.setDataType(STRING_DATATYPE);
		configNameAttributeValue11.getContent().add(policyAdapter.getRiskType());
		assignment11.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue11));

		advice.getAttributeAssignmentExpression().add(assignment11);
		
		AttributeAssignmentExpressionType assignment12 = new AttributeAssignmentExpressionType();
		assignment12.setAttributeId("RiskLevel");
		assignment12.setCategory(CATEGORY_RESOURCE);
		assignment12.setIssuer("");

		AttributeValueType configNameAttributeValue12 = new AttributeValueType();
		configNameAttributeValue12.setDataType(STRING_DATATYPE);
		configNameAttributeValue12.getContent().add(policyAdapter.getRiskLevel());
		assignment12.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue12));

		advice.getAttributeAssignmentExpression().add(assignment12);	

		AttributeAssignmentExpressionType assignment13 = new AttributeAssignmentExpressionType();
		assignment13.setAttributeId("guard");
		assignment13.setCategory(CATEGORY_RESOURCE);
		assignment13.setIssuer("");

		AttributeValueType configNameAttributeValue13 = new AttributeValueType();
		configNameAttributeValue13.setDataType(STRING_DATATYPE);
		configNameAttributeValue13.getContent().add(policyAdapter.getRiskLevel());
		assignment13.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue13));

		advice.getAttributeAssignmentExpression().add(assignment13);
		
		AttributeAssignmentExpressionType assignment14 = new AttributeAssignmentExpressionType();
		assignment14.setAttributeId("TTLDate");
		assignment14.setCategory(CATEGORY_RESOURCE);
		assignment14.setIssuer("");

		AttributeValueType configNameAttributeValue14 = new AttributeValueType();
		configNameAttributeValue14.setDataType(STRING_DATATYPE);
		configNameAttributeValue14.getContent().add(policyAdapter.getTtlDate());
		assignment14.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue14));

		advice.getAttributeAssignmentExpression().add(assignment14);

		advices.getAdviceExpression().add(advice);
		return advices;
	}

	@Override
	public Object getCorrectPolicyDataObject() {
		return policyAdapter.getPolicyData();
	}	


}
