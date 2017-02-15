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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.std.IdentifierImpl;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 

public class ConfigPolicy extends Policy {

	/**
	 * Config Fields
	 */
	private static final Logger logger = FlexLogger.getLogger(ConfigPolicy.class);

	public static final String JSON_CONFIG = "JSON";
	public static final String XML_CONFIG = "XML";
	public static final String PROPERTIES_CONFIG = "PROPERTIES";
	public static final String OTHER_CONFIG = "OTHER";

	private String configBodyData;

	public ConfigPolicy() {
		super();
	}
	
	public ConfigPolicy(PolicyRestAdapter policyAdapter){
		this.policyAdapter = policyAdapter;
	}

	// Saving the Configurations file at server location for config policy.
	protected void saveConfigurations(String policyName) {
		final Path gitPath = Paths.get(policyAdapter.getUserGitPath().toString());
		String policyDir = policyAdapter.getParentPath().toString();
		int startIndex = policyDir.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
		policyDir = policyDir.substring(startIndex, policyDir.length());
		logger.info("print the main domain value"+policyDir);
		String path = policyDir.replace('\\', '.');
		if(path.contains("/")){
			path = policyDir.replace('/', '.');
			logger.info("print the path:" +path);
		}
		
		try {
			File file;
			String configFileName = getConfigFile(policyName);
			if(CONFIG_HOME.contains("\\"))
			{
			 file = new File(CONFIG_HOME + "\\" + path + "."+ configFileName);
			}
			else
			{
			 file = new File(CONFIG_HOME + "/" + path + "."+ configFileName);
			}
			
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}


			File configHomeDir = new File(CONFIG_HOME);
			File[] listOfFiles = configHomeDir.listFiles();
			if (listOfFiles != null){
				for(File eachFile : listOfFiles){
					if(eachFile.isFile()){
						String fileNameWithoutExtension = FilenameUtils.removeExtension(eachFile.getName());
						String configFileNameWithoutExtension = FilenameUtils.removeExtension(path + "." + configFileName);
						if (fileNameWithoutExtension.equals(configFileNameWithoutExtension)){
							//delete the file
							eachFile.delete();
						}
					}
				}
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(configBodyData);
			bw.close();
			if (logger.isDebugEnabled()) {
				logger.debug("Configuration is succesfully saved");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Here we are adding the extension for the configurations file based on the
	// config type selection for saving.
	private String getConfigFile(String filename) {
		filename = FilenameUtils.removeExtension(filename);
		if (filename.endsWith(".xml")) {
			filename = filename.substring(0, filename.length() - 4);
		}
		String id = policyAdapter.getConfigType();

		if (id != null) {
			if (id.equalsIgnoreCase(JSON_CONFIG)) {
				filename = filename + ".json";
			}
			if (id.equalsIgnoreCase(XML_CONFIG)) {
				filename = filename + ".xml";
			}
			if (id.equalsIgnoreCase(PROPERTIES_CONFIG)) {
				filename = filename + ".properties";
			}
			if (id.equalsIgnoreCase(OTHER_CONFIG)) {
				filename = filename + ".txt";
			}
		}
		return filename;
	}

	// Validations for Config form
	/*
	 * FORM VALIDATION WILL BE DONE BY THE PAP-ADMIN before creating JSON object... 
	 * BODY VALIDATION WILL BE DONE BY THE PAP-REST after receiving and deserializing the JSON object
	 */
	public boolean validateConfigForm() {
		
		isValidForm = true;
		
		/*
		 * Validate Text Area Body
		 */
		configBodyData = policyAdapter.getConfigBodyData();
		String id = policyAdapter.getConfigType();
		if (id != null) {
			if (id.equals(JSON_CONFIG)) {
				if (!isJSONValid(configBodyData)) {
					isValidForm = false;
				}
			} else if (id.equals(XML_CONFIG)) {
				if (!isXMLValid(configBodyData)) {
					isValidForm = false;
				}
			} else if (id.equals(PROPERTIES_CONFIG)) {
				if (!isPropValid(configBodyData)||configBodyData.equals("")) {
					isValidForm = false;
				} 
			} else if (id.equals(OTHER_CONFIG)) {
				if (configBodyData.equals("")) {
					isValidForm = false;
				}
			}
		}
		return isValidForm;

	}

	// Validation for XML.
	private boolean isXMLValid(String data) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try {
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(new XMLErrorHandler());
			reader.parse(new InputSource(new StringReader(data)));
		} catch (ParserConfigurationException e) {
			return false;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;

	}

	// Validation for Properties file.
	public boolean isPropValid(String prop) {

		Scanner scanner = new Scanner(prop);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line.replaceAll("\\s+", "");
			if (line.startsWith("#")) {
				continue;
			} else {
				if (line.contains("=")) {
					String[] parts = line.split("=");
					if (parts.length < 2) {
						scanner.close();
						return false;
					}
				} else {
					scanner.close();
					return false;
				}
			}
		}
		scanner.close();
		return true;

	}

	public class XMLErrorHandler implements ErrorHandler {

		public void warning(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println(e.getMessage());
		}

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
			Path newFile = getNextFilename(Paths.get(policyAdapter.getParentPath().toString()), policyAdapter.getPolicyType(), policyAdapter.getPolicyName(), version);
			if (newFile == null) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error("File already exists");
				PolicyLogger.error("File alrady exists");
				setPolicyExists(true);
				return false;
			}
			policyName = newFile.getFileName().toString();
			
			// Body is optional so checking.
			configBodyData = policyAdapter.getConfigBodyData();
			if (!configBodyData.equals("")) {
				// Save the Configurations file with the policy name with extention based on selection.
				saveConfigurations(policyName);
			}
			
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
			allOfOne.getMatch().add(createMatch("PolicyName", name));
			AllOfType allOf = new AllOfType();
			
			// Adding the matches to AllOfType element Match for Ecomp
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
			// Match for ConfigName
			allOf.getMatch().add(createMatch("ConfigName", policyAdapter.getConfigName()));
			
			Map<String, String> dynamicFieldConfigAttributes = policyAdapter.getDynamicFieldConfigAttributes();
			
			// If there is any dynamic field create the matches here
			for (String keyField : dynamicFieldConfigAttributes.keySet()) {
				String key = keyField;
				String value = dynamicFieldConfigAttributes.get(key);
				MatchType dynamicMatch = createDynamicMatch(key, value);
				allOf.getMatch().add(dynamicMatch);
			}

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
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "ConfigPolicy", "Exception creating ACCESS URI");
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
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "ConfigPolicy", "Exception creating Config URI");
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
		advice.setAdviceId("configID");
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
		if (policyAdapter.getConfigType() != null) {
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

			String content = "$URL" + "/Config/" + path + "." + getConfigFile(policyName);
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

			Map<String, String> dynamicFieldConfigAttributes = policyAdapter.getDynamicFieldConfigAttributes();
			for (String keyField : dynamicFieldConfigAttributes.keySet()) {
				String key = keyField;
				String value = dynamicFieldConfigAttributes.get(key);
				AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
				assignment7.setAttributeId("matching:" + key);
				assignment7.setCategory(CATEGORY_RESOURCE);
				assignment7.setIssuer("");

				AttributeValueType configNameAttributeValue7 = new AttributeValueType();
				configNameAttributeValue7.setDataType(STRING_DATATYPE);
				configNameAttributeValue7.getContent().add(value);
				assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));

				advice.getAttributeAssignmentExpression().add(assignment7);
			}
		}
		
		//Risk Attributes
		AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
		assignment8.setAttributeId("RiskType");
		assignment8.setCategory(CATEGORY_RESOURCE);
		assignment8.setIssuer("");

		AttributeValueType configNameAttributeValue8 = new AttributeValueType();
		configNameAttributeValue8.setDataType(STRING_DATATYPE);
		configNameAttributeValue8.getContent().add(policyAdapter.getRiskType());
		assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

		advice.getAttributeAssignmentExpression().add(assignment8);
		
		AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
		assignment9.setAttributeId("RiskLevel");
		assignment9.setCategory(CATEGORY_RESOURCE);
		assignment9.setIssuer("");

		AttributeValueType configNameAttributeValue9 = new AttributeValueType();
		configNameAttributeValue9.setDataType(STRING_DATATYPE);
		configNameAttributeValue9.getContent().add(policyAdapter.getRiskLevel());
		assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

		advice.getAttributeAssignmentExpression().add(assignment9);	

		AttributeAssignmentExpressionType assignment10 = new AttributeAssignmentExpressionType();
		assignment10.setAttributeId("guard");
		assignment10.setCategory(CATEGORY_RESOURCE);
		assignment10.setIssuer("");

		AttributeValueType configNameAttributeValue10 = new AttributeValueType();
		configNameAttributeValue10.setDataType(STRING_DATATYPE);
		configNameAttributeValue10.getContent().add(policyAdapter.getGuard());
		assignment10.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue10));

		advice.getAttributeAssignmentExpression().add(assignment10);
		
		AttributeAssignmentExpressionType assignment11 = new AttributeAssignmentExpressionType();
		assignment11.setAttributeId("TTLDate");
		assignment11.setCategory(CATEGORY_RESOURCE);
		assignment11.setIssuer("");

		AttributeValueType configNameAttributeValue11 = new AttributeValueType();
		configNameAttributeValue11.setDataType(STRING_DATATYPE);
		configNameAttributeValue11.getContent().add(policyAdapter.getTtlDate());
		assignment11.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue11));

		advice.getAttributeAssignmentExpression().add(assignment11);
		

		advices.getAdviceExpression().add(advice);
		return advices;
	}

	@Override
	public Object getCorrectPolicyDataObject() {
		return policyAdapter.getPolicyData();
	}

}
