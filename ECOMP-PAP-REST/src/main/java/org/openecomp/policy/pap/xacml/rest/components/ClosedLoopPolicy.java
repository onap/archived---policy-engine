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
import java.nio.charset.Charset;
import java.nio.file.Files;
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

import com.att.research.xacml.std.IdentifierImpl;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 

public class ClosedLoopPolicy extends Policy {
	
	/**
	 * Config Fields
	 */
	private static final Logger logger = FlexLogger.getLogger(ConfigPolicy.class);

	public ClosedLoopPolicy() {
		super();
	}
	
	public ClosedLoopPolicy(PolicyRestAdapter policyAdapter){
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
			String body = jsonBody;
			try {
				try{
					//Remove the trapMaxAge in Verification Signature
					body = body.replace(",\"trapMaxAge\":null", "");
				}catch(Exception e){
					logger.debug("No Trap Max Age in JSON body");
				}
				this.policyAdapter.setJsonBody(body);
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
	
	//Utility to read json data from the existing file to a string
	static String readFile(String path, Charset encoding) throws IOException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
		
	}
	
	//create the configuration file based on the policy name on adding the extension as .json 
	private String getConfigFile(String filename) {
		filename = FilenameUtils.removeExtension(filename);
		if (filename.endsWith(".xml")) {
			filename = filename.substring(0, filename.length() - 4);
		}
		filename = filename + ".json";
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
			prepareToSave();
		}
		
		// Until here we prepared the data and here calling the method to create xml.
		Path newPolicyPath = null;
		newPolicyPath = Paths.get(policyAdapter.getParentPath().toString(), policyName);

		successMap = createPolicy(newPolicyPath,getCorrectPolicyDataObject());	
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
			
			//delete the closed loop draft file and configuration file, if validation is success after editing the draft policy
			String prevPolicyName = null;
			if(policyAdapter.isEditPolicy()){
				prevPolicyName = "Config_Fault_" + policyAdapter.getPolicyName() + "." + policyAdapter.getHighestVersion() + ".xml";

				if (policyAdapter.isDraft()) {
					policyName = "Config_Fault_" + policyAdapter.getPolicyName() + "_Draft";
				} else {
					policyName = "Config_Fault_" + policyAdapter.getPolicyName();
				}
				
				//delete the closed loop draft configuration file, if validation is success after editing the draft policy
				final Path gitPath = Paths.get(policyAdapter.getUserGitPath());
				String policyDir = policyAdapter.getParentPath();
				int startIndex = policyDir.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
				policyDir = policyDir.substring(startIndex, policyDir.length());
				logger.info("print the main domain value"+policyDir);
				String path = policyDir.replace('\\', '.');
				if(path.contains("/")){
					path = policyDir.replace('/', '.');
					logger.info("print the path:" +path); 
				}
				String fileName = FilenameUtils.removeExtension(policyName);

				final String tempPath = path;
				String fileLocation = null;
				if (fileName != null && fileName.contains("Config_Fault_")) {
					fileLocation = CONFIG_HOME;
				} 
				// Get the file from the saved location
				File dir = new File(fileLocation);
				File[] listOfFiles = dir.listFiles();
				for (File file : listOfFiles) {
					String configFile = null;
					if(!policyAdapter.isDraft()){
						configFile = fileName + "_Draft";
					}else{
						configFile = fileName;
					}
					if (file.isFile() && file.getName().contains( tempPath + "." + configFile)) {
						try {
							if (file.delete() == false) {
								throw new Exception(
										"No known error, Delete failed");
							}
						} catch (Exception e) {
							logger.error("Failed to Delete file: "
									+ e.getLocalizedMessage());
						}
					}
				}
			}
			
			// Save off everything
			// making ready all the required elements to generate the action policy xml.
			// Get the uniqueness for policy name.
			String policyName1 = null;
			if(policyAdapter.isDraft()){
				policyName1 = policyAdapter.getPolicyName() + "_Draft";
			}else{
				policyName1 = policyAdapter.getPolicyName();
			}
			
			Path newFile = this.getNextLoopFilename(Paths.get(policyAdapter.getParentPath()), policyAdapter.getPolicyType(), policyAdapter.getConfigPolicyType(), policyName1, version);
			if (newFile == null) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error("File already exists, cannot create the policy.");
				PolicyLogger.error("File already exists, cannot create the policy.");
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
			
			PolicyType faultPolicy = (PolicyType) policyAdapter.getData();
			
			faultPolicy.setDescription(policyAdapter.getPolicyDescription());

			faultPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());
			
			AllOfType allOfOne = new AllOfType();
			File policyFilePath = new File(policyAdapter.getParentPath().toString(), policyName);
			String policyDir = policyFilePath.getParentFile().getName();
			String fileName = FilenameUtils.removeExtension(policyName);
			fileName = policyDir + "." + fileName + ".xml";
			String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
			if ((name == null) || (name.equals(""))) {
				name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
			}
			allOfOne.getMatch().add(createMatch("PolicyName", name));
			AllOfType allOf = new AllOfType();
			// Adding the matches to AllOfType element
			// Match for Ecomp
			allOf.getMatch().add(createMatch("ECOMPName", policyAdapter.getEcompName()));
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
			faultPolicy.setTarget((TargetType) target);

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
				//logger.error(e.getStackTrace());
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "CreateClosedLoopPolicy", "Exception creating ACCESS URI");
			}
			accessAttributeDesignator.setCategory(CATEGORY_ACTION);
			accessAttributeDesignator.setDataType(STRING_DATATYPE);
			accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
			accessMatch.setAttributeDesignator(accessAttributeDesignator);
			accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

			// Creating Config Match in rule Target
			MatchType closedMatch = new MatchType();
			AttributeValueType closedAttributeValue = new AttributeValueType();
			closedAttributeValue.setDataType(STRING_DATATYPE);
			closedAttributeValue.getContent().add("Config");
			closedMatch.setAttributeValue(closedAttributeValue);
			AttributeDesignatorType closedAttributeDesignator = new AttributeDesignatorType();
			URI closedURI = null;
			try {
				closedURI = new URI(RESOURCE_ID);
			} catch (URISyntaxException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(e.getStackTrace());
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "CreateClosedLoopPolicy", "Exception creating closed URI");
			}
			closedAttributeDesignator.setCategory(CATEGORY_RESOURCE);
			closedAttributeDesignator.setDataType(STRING_DATATYPE);
			closedAttributeDesignator.setAttributeId(new IdentifierImpl(closedURI).stringValue());
			closedMatch.setAttributeDesignator(closedAttributeDesignator);
			closedMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

			allOfInRule.getMatch().add(accessMatch);
			allOfInRule.getMatch().add(closedMatch);

			AnyOfType anyOfInRule = new AnyOfType();
			anyOfInRule.getAllOf().add(allOfInRule);

			TargetType targetInRule = new TargetType();
			targetInRule.getAnyOf().add(anyOfInRule);

			rule.setTarget(targetInRule);
			rule.setAdviceExpressions(getAdviceExpressions(version, policyName));

			faultPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
			policyAdapter.setPolicyData(faultPolicy);

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
		advice.setAdviceId("faultID");
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
		
		//Risk Attributes
		AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
		assignment6.setAttributeId("RiskType");
		assignment6.setCategory(CATEGORY_RESOURCE);
		assignment6.setIssuer("");

		AttributeValueType configNameAttributeValue6 = new AttributeValueType();
		configNameAttributeValue6.setDataType(STRING_DATATYPE);
		configNameAttributeValue6.getContent().add(policyAdapter.getRiskType());
		assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));

		advice.getAttributeAssignmentExpression().add(assignment6);
		
		AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
		assignment7.setAttributeId("RiskLevel");
		assignment7.setCategory(CATEGORY_RESOURCE);
		assignment7.setIssuer("");

		AttributeValueType configNameAttributeValue7 = new AttributeValueType();
		configNameAttributeValue7.setDataType(STRING_DATATYPE);
		configNameAttributeValue7.getContent().add(policyAdapter.getRiskLevel());
		assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));

		advice.getAttributeAssignmentExpression().add(assignment7);	

		AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
		assignment8.setAttributeId("guard");
		assignment8.setCategory(CATEGORY_RESOURCE);
		assignment8.setIssuer("");

		AttributeValueType configNameAttributeValue8 = new AttributeValueType();
		configNameAttributeValue8.setDataType(STRING_DATATYPE);
		configNameAttributeValue8.getContent().add(policyAdapter.getGuard());
		assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

		advice.getAttributeAssignmentExpression().add(assignment8);
		
		AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
		assignment9.setAttributeId("TTLDate");
		assignment9.setCategory(CATEGORY_RESOURCE);
		assignment9.setIssuer("");

		AttributeValueType configNameAttributeValue9 = new AttributeValueType();
		configNameAttributeValue9.setDataType(STRING_DATATYPE);
		configNameAttributeValue9.getContent().add(policyAdapter.getTtlDate());
		assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

		advice.getAttributeAssignmentExpression().add(assignment9);


		
		advices.getAdviceExpression().add(advice);
		return advices;
	}

	@Override
	public Object getCorrectPolicyDataObject() {
		return policyAdapter.getPolicyData();
	}


}
