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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

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
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.diff.JsonDiff;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 

public class FirewallConfigPolicy extends Policy {
	/**
	 * Config Fields
	 */
	private static final Logger logger = FlexLogger.getLogger(FirewallConfigPolicy.class);

	public static final String JSON_CONFIG = "JSON";
	public static final String XML_CONFIG = "XML";
	public static final String PROPERTIES_CONFIG = "PROPERTIES";
	public static final String OTHER_CONFIG = "OTHER";
	
	/*
	 * These are the parameters needed for DB access from the PAP
	 */
	private static String papDbDriver = null;
	private static String papDbUrl = null;
	private static String papDbUser = null;
	private static String papDbPassword = null;

	public FirewallConfigPolicy() {
		super();
	}
	
	public FirewallConfigPolicy(PolicyRestAdapter policyAdapter) {
		this.policyAdapter = policyAdapter;
		this.policyAdapter.setConfigType(policyAdapter.getConfigType());
		
	}
	
	// Saving the Configurations file at server location for config policy.
	protected void saveConfigurations(String policyName, String prevPolicyName, String jsonBody) {
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
			String configFileName = getConfigFile(policyName);
			
			File file;
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
			
			//Getting the previous policy Config Json file to be used for updating the dictionary tables
			if (policyAdapter.isEditPolicy()) {
				
				String prevConfigFileName = getConfigFile(prevPolicyName);
				
				File oldFile;
				if(CONFIG_HOME.contains("\\"))
				{
					oldFile = new File(CONFIG_HOME + "\\" + path + "."+ prevConfigFileName);
				}
				else
				{
					oldFile = new File(CONFIG_HOME + "/" + path + "."+ prevConfigFileName);
				}
				
				String filepath = oldFile.toString();
				
				String prevJsonBody = readFile(filepath, StandardCharsets.UTF_8);
				policyAdapter.setPrevJsonBody(prevJsonBody);
			}


			File configHomeDir = new File(CONFIG_HOME);
			File[] listOfFiles = configHomeDir.listFiles();
			if (listOfFiles != null){
				for(File eachFile : listOfFiles){
					if(eachFile.isFile()){
						String fileNameWithoutExtension = FilenameUtils.removeExtension(eachFile.getName());
						String configFileNameWithoutExtension = FilenameUtils.removeExtension(configFileName);
						if (fileNameWithoutExtension.equals(configFileNameWithoutExtension)){
							//delete the file
							eachFile.delete();
						}
					}
				}
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonBody);
			bw.close();
			if (logger.isDebugEnabled()) {
				logger.debug("Configuration is succesfully saved");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Utility to read json data from the existing file to a string
	static String readFile(String path, Charset encoding) throws IOException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
		
	}

	// Here we are adding the extension for the configurations file based on the
	// config type selection for saving.
	private String getConfigFile(String filename) {
		filename = FilenameUtils.removeExtension(filename);
		if (filename.endsWith(".json")) {
			filename = filename.substring(0, filename.length() - 4);
		}

		filename=filename+".json";
		return filename;
	}
	
	
	// Validations for Config form
	public boolean validateConfigForm() {

		// Validating mandatory Fields.
		isValidForm = true;	
		return isValidForm;

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
		Boolean dbIsUpdated = false;
		if (policyAdapter.getApiflag().equalsIgnoreCase("admin")){
			dbIsUpdated = true;
		} else {
			if (policyAdapter.isEditPolicy()) {
				dbIsUpdated = updateFirewallDictionaryData(policyAdapter.getJsonBody(), policyAdapter.getPrevJsonBody());
			} else {
				dbIsUpdated = insertFirewallDicionaryData(policyAdapter.getJsonBody());
			}
		}
		
		if(dbIsUpdated) {
			successMap = createPolicy(newPolicyPath,getCorrectPolicyDataObject() );	
		} else {
			//TODO:EELF Cleanup - Remove logger
			//logger.error("Failed to Update the Database Dictionary Tables.");
			PolicyLogger.error("Failed to Update the Database Dictionary Tables.");
			
			//remove the new json file 
			String jsonBody = policyAdapter.getPrevJsonBody();
			if (jsonBody!=null){
				saveConfigurations(policyName, "", jsonBody);
			} else {
				saveConfigurations(policyName, "", "");
			}
			successMap.put("fwdberror", "DB UPDATE");
		}

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
				prevPolicyName = "Config_FW_" + policyAdapter.getPolicyName() + "." + policyAdapter.getHighestVersion() + ".xml";
			}

			Path newFile = getNextFilename(Paths.get(policyAdapter.getParentPath().toString()),
					(policyAdapter.getPolicyType() + "_FW"), policyAdapter.getPolicyName(), version);

			if (newFile == null) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error("Policy already Exists, cannot create the policy.");
				PolicyLogger.error("Policy already Exists, cannot create the policy.");
				setPolicyExists(true);
				return false;
			}
			policyName = newFile.getFileName().toString();

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
			
			// Match for ConfigName
			allOf.getMatch().add(createMatch("ConfigName", policyAdapter.getConfigName()));
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
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "FirewallConfigPolicy", "Exception creating ACCESS URI");
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
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "FirewallConfigPolicy", "Exception creating Config URI");
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
		
		//Firewall Config ID Assignment
		AdviceExpressionsType advices = new AdviceExpressionsType();
		AdviceExpressionType advice = new AdviceExpressionType();
		advice.setAdviceId("firewallConfigID");
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
		
		// For Config file Url if configurations are provided.
		//URL ID Assignment
		final Path gitPath = Paths.get(policyAdapter.getUserGitPath().toString());
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
		String content = CONFIG_URL + "/Config/" + path + "." + getConfigFile(policyName);

		AttributeValue.getContent().add(content);
		assignment2.setExpression(new ObjectFactory().createAttributeValue(AttributeValue));
		advice.getAttributeAssignmentExpression().add(assignment2);
		
		//Policy Name Assignment
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
		System.out.println(fileName);
		String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
		if ((name == null) || (name.equals(""))) {
			name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
		}
		System.out.println(name);
		attributeValue3.getContent().add(name);
		assignment3.setExpression(new ObjectFactory().createAttributeValue(attributeValue3));
		advice.getAttributeAssignmentExpression().add(assignment3);
		
		//Version Number Assignment
		AttributeAssignmentExpressionType assignment4 = new AttributeAssignmentExpressionType();
		assignment4.setAttributeId("VersionNumber");
		assignment4.setCategory(CATEGORY_RESOURCE);
		assignment4.setIssuer("");
		AttributeValueType configNameAttributeValue4 = new AttributeValueType();
		configNameAttributeValue4.setDataType(STRING_DATATYPE);
		configNameAttributeValue4.getContent().add(Integer.toString(version));
		assignment4.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue4));
		advice.getAttributeAssignmentExpression().add(assignment4);
		
		//Ecomp Name Assignment
		AttributeAssignmentExpressionType assignment5 = new AttributeAssignmentExpressionType();
		assignment5.setAttributeId("matching:" + this.ECOMPID);
		assignment5.setCategory(CATEGORY_RESOURCE);
		assignment5.setIssuer("");
		AttributeValueType configNameAttributeValue5 = new AttributeValueType();
		configNameAttributeValue5.setDataType(STRING_DATATYPE);
		assignment5.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue5));
		advice.getAttributeAssignmentExpression().add(assignment5);
		
		//Config Name Assignment
		AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
		assignment6.setAttributeId("matching:" + this.CONFIGID);
		assignment6.setCategory(CATEGORY_RESOURCE);
		assignment6.setIssuer("");
		AttributeValueType configNameAttributeValue6 = new AttributeValueType();
		configNameAttributeValue6.setDataType(STRING_DATATYPE);
		configNameAttributeValue6.getContent().add(policyAdapter.getConfigName());
		assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));
		advice.getAttributeAssignmentExpression().add(assignment6);

		//Risk Attributes
		AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
		assignment7.setAttributeId("RiskType");
		assignment7.setCategory(CATEGORY_RESOURCE);
		assignment7.setIssuer("");

		AttributeValueType configNameAttributeValue7 = new AttributeValueType();
		configNameAttributeValue7.setDataType(STRING_DATATYPE);
		configNameAttributeValue7.getContent().add(policyAdapter.getRiskType());
		assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));

		advice.getAttributeAssignmentExpression().add(assignment7);
				
		AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
		assignment8.setAttributeId("RiskLevel");
		assignment8.setCategory(CATEGORY_RESOURCE);
		assignment8.setIssuer("");

		AttributeValueType configNameAttributeValue8 = new AttributeValueType();
		configNameAttributeValue8.setDataType(STRING_DATATYPE);
		configNameAttributeValue8.getContent().add(policyAdapter.getRiskLevel());
		assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

		advice.getAttributeAssignmentExpression().add(assignment8);	

		AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
		assignment9.setAttributeId("guard");
		assignment9.setCategory(CATEGORY_RESOURCE);
		assignment9.setIssuer("");

		AttributeValueType configNameAttributeValue9 = new AttributeValueType();
		configNameAttributeValue9.setDataType(STRING_DATATYPE);
		configNameAttributeValue9.getContent().add(policyAdapter.getGuard());
		assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

		advice.getAttributeAssignmentExpression().add(assignment9);
				
		AttributeAssignmentExpressionType assignment10 = new AttributeAssignmentExpressionType();
		assignment10.setAttributeId("TTLDate");
		assignment10.setCategory(CATEGORY_RESOURCE);
		assignment10.setIssuer("");

		AttributeValueType configNameAttributeValue10 = new AttributeValueType();
		configNameAttributeValue10.setDataType(STRING_DATATYPE);
		configNameAttributeValue10.getContent().add(policyAdapter.getTtlDate());
		assignment10.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue10));

		advice.getAttributeAssignmentExpression().add(assignment10);
		
		int index = 0;

		advices.getAdviceExpression().add(advice);
		return advices;
	}
	
	
	private Boolean insertFirewallDicionaryData (String jsonBody) throws SQLException {

		
		JsonObject json = null;
		if (jsonBody != null) {
			
			//Read jsonBody to JsonObject
			json = stringToJson(jsonBody);
			
			JsonArray firewallRules = null;
			JsonArray serviceGroup = null;
			JsonArray addressGroup = null;
			String securityZone=null;
			
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			
			/*
			 * Retrieve the property values for db access from the xacml.pap.properties
			 */
			papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
			papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
			papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
			papDbPassword = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD);

			//insert data into tables
			try {
				//Get DB Connection
				Class.forName(papDbDriver);
				con = DriverManager.getConnection(papDbUrl,papDbUser,papDbPassword);
				st = con.createStatement();
				
				firewallRules = json.getJsonArray("firewallRuleList");
				serviceGroup = json.getJsonArray("serviceGroups");
				addressGroup = json.getJsonArray("addressGroups");	
				securityZone=json.getString("primaryParentZoneId").toString();
				
				logger.info("Parent child: securityZone from JSON: "+securityZone);
				String insertQuery = null;
				
				//Inserting childPolicy and its parent to the FWChildToParent DB table
				if(securityZone!=null){
					//Its a child Policy. 
					//Retrieve the parent name from the securityZone Id
					String retrieveParentQuery= "select parent from fwparent where securityZone='";
					
					retrieveParentQuery=retrieveParentQuery+securityZone+"';";
					logger.info("Parent child: Query to retrieve parent "+retrieveParentQuery);
					rs = st.executeQuery(retrieveParentQuery);
					
					String parent=null;
					if(rs.next()){
						parent = rs.getString("parent");
					}
					rs.close();
					
					
					String insertQueryChildTable="INSERT INTO FWChildToParent(child, parent) VALUES ('";
					insertQueryChildTable=insertQueryChildTable+policyAdapter.getPolicyName()+"','"+parent+"');";
					logger.info("Parent child: Insert child and parent to DB: "+insertQueryChildTable);
					st.executeUpdate(insertQueryChildTable);
					
				}
				
				/*
				 * Inserting firewallRuleList data into the Terms, SecurityZone, and Action tables
				 */
				if (firewallRules != null) {
					
					int termID = 0;
					int zoneID = 0;
					int actionID = 0;
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM TERM;");
					if(rs.next()){
						termID = rs.getInt("ID");
					}
					rs.close();
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM ZONE;");
					if(rs.next()){
						zoneID = rs.getInt("ID");
					}
					rs.close();
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM ACTIONLIST;");
					if(rs.next()){
						actionID = rs.getInt("ID");
					}
					rs.close();
					
					int i = 0;
					for(JsonValue jsonValue : firewallRules) {
						
						//increment ID Primary Keys
						termID = termID + 1;
						zoneID = zoneID + 1;
						actionID = actionID + 1;
						
						/*
						 * Populate ArrayLists with values from the JSON
						 */
						//create the JSON object from the JSON Array for each iteration through the for loop
						JsonObject ruleListobj = firewallRules.getJsonObject(i);
												
						//get values from JSON fields of firewallRulesList Array
						String ruleName = ruleListobj.get("ruleName").toString();
						String action = ruleListobj.get("action").toString();
						String description = ruleListobj.get("description").toString();
						
						rs = st.executeQuery("SELECT * FROM TERM WHERE TERMNAME = "+ ruleName + ";");
						
						if (rs.next()) {
							st.executeUpdate("DELETE FROM TERM WHERE TERMNAME = "+ ruleName + ";");
						}
						rs.close();
						
						//getting fromZone Array field from the firewallRulesList
						JsonArray fromZoneArray = ruleListobj.getJsonArray("fromZones");
						String fromZoneString = null;
						
						int fromZoneIndex = 0;
						for (JsonValue fromZoneJsonValue : fromZoneArray) {
							String value = fromZoneArray.get(fromZoneIndex).toString();
							value = value.replace("\"", "");
							
							if (fromZoneString != null) {
								fromZoneString = fromZoneString.concat(",").concat(value);
								
							} else {
								fromZoneString = value;
							}
							
							fromZoneIndex++;
							
						}
						String fromZoneInsert = "'"+fromZoneString+"'";
						
						//getting toZone Array field from the firewallRulesList
						JsonArray toZoneArray = ruleListobj.getJsonArray("toZones");
						String toZoneString = null;
						
						int toZoneIndex = 0;
						for (JsonValue toZoneJsonValue : toZoneArray) {
							String value = toZoneArray.get(toZoneIndex).toString();
							value = value.replace("\"", "");
							
							if (toZoneString != null) {
								toZoneString = toZoneString.concat(",").concat(value);
								
							} else {
								toZoneString = value;
							}
							
							toZoneIndex++;
							
						}
						String toZoneInsert = "'"+toZoneString+"'";
						
						//getting sourceList Array fields from the firewallRulesList
						JsonArray srcListArray = ruleListobj.getJsonArray("sourceList");
						String srcListString = null;
						
						int srcListIndex = 0;
						for (JsonValue srcListJsonValue : srcListArray) {
							JsonObject srcListObj = srcListArray.getJsonObject(srcListIndex);
							String type = srcListObj.get("type").toString().replace("\"", "");
							
							String value = null;
							if(type.equals("REFERENCE")||type.equals("GROUP")){
								value = srcListObj.get("name").toString();
							} else if (type.equalsIgnoreCase("ANY")){
								value = null;
							} else {
								value = srcListObj.get("value").toString();
							}
							
							if (value!=null){
								value = value.replace("\"", "");
							}
							
							if (srcListString != null) {
								srcListString = srcListString.concat(",").concat(value);
								
							} else {
								srcListString = value;
							}
							
							srcListIndex++;
							
						}
						String srcListInsert = "'"+srcListString+"'";
						
						//getting destinationList Array fields from the firewallRulesList
						JsonArray destListArray = ruleListobj.getJsonArray("destinationList");
						String destListString = null;
						
						int destListIndex = 0;
						for (JsonValue destListJsonValue : destListArray) {
							JsonObject destListObj = destListArray.getJsonObject(destListIndex);
							String type = destListObj.get("type").toString().replace("\"", "");
							
							String value = null;
							if(type.equals("REFERENCE")||type.equals("GROUP")){
								value = destListObj.get("name").toString();
							} else if (type.equalsIgnoreCase("ANY")){
								value = null;
							} else {
								value = destListObj.get("value").toString();
							}
							
							if (value!=null){
								value = value.replace("\"", "");
							}
							
							if (destListString != null) {
								destListString = destListString.concat(",").concat(value);
							} else {
								destListString = value;
							}
							
							destListIndex++;
						}
						String destListInsert = "'"+destListString+"'";
						
						//getting destServices Array fields from the firewallRulesList
						JsonArray destServicesArray = ruleListobj.getJsonArray("destServices");
						String destPortListString = null;
						
						int destPortListIndex = 0;
						for (JsonValue destListJsonValue : destServicesArray) {
							JsonObject destServicesObj = destServicesArray.getJsonObject(destPortListIndex);
							String type = destServicesObj.get("type").toString().replace("\"", "");
							
							String value = null;
							if(type.equals("REFERENCE")||type.equals("GROUP")){
								value = destServicesObj.get("name").toString();
							} else if (type.equalsIgnoreCase("ANY")){
								value = null;
							} else {
								value = destServicesObj.get("value").toString();
							}
							
							if (value!=null){
								value = value.replace("\"", "");
							}
							
							if (destPortListString != null) {
								destPortListString = destPortListString.concat(",").concat(value);
							} else {
								destPortListString = value;
							}
							
							destPortListIndex++;
						}
						String destPortListInsert = "'"+destPortListString+"'";					
						
						/*
						 * Create Queries to INSERT data into database tables and execute 
						 */					
                        String termSql = "INSERT INTO Term (ID, TERMNAME, SRCIPLIST, DESTIPLIST, PROTOCOLLIST, PORTLIST, SRCPORTLIST,"
                                 + " DESTPORTLIST, ACTION, DESCRIPTION, FROMZONE, TOZONE, CREATED_BY, MODIFIED_DATE) VALUES ("+termID+","
                                 +ruleName+","+srcListInsert+","+destListInsert+","+ "null"+","+"null"+","+"null"+","+destPortListInsert+","
                                 +action+","+description+","+fromZoneInsert+","+toZoneInsert+",'API',"+ "null"+ "); ";
						termSql = termSql.replace('"', '\'');
						st.addBatch(termSql);

						String actionSql = "INSERT INTO ACTIONLIST (ID, ACTIONNAME, DESCRIPTION) VALUES ("+actionID+","+action+","+action+"); ";
						actionSql = actionSql.replace('"', '\'');
						st.addBatch(actionSql);
						
						st.executeBatch();
						
						i++;
					}
					
				}
				
				/*
				 * Inserting serviceGroups data into the ServiceGroup, ServiceList, ProtocolList, and PortList tables
				 */
				if (serviceGroup != null) {
					
					int serviceGroupID = 0;
					int serviceListID = 0;
					int protocolID = 0;
					int portID = 0;

					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM SERVICEGROUP;");
					if(rs.next()){
						serviceGroupID = rs.getInt("ID");
					}
					rs.close();
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM GROUPSERVICELIST;");
					if(rs.next()){
						serviceListID = rs.getInt("ID");
					}
					rs.close();
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM PROTOCOLLIST;");
					if(rs.next()){
						protocolID = rs.getInt("ID");
					}
					rs.close();
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM PORTLIST;");
					if(rs.next()){
						portID = rs.getInt("ID");
					}
					rs.close();
					
					int i = 0;
					for(JsonValue jsonValue : serviceGroup) {
						
						/*
						 * Populate ArrayLists with values from the JSON
						 */
						//create the JSON object from the JSON Array for each iteration through the for loop
						JsonObject svcGroupListobj = serviceGroup.getJsonObject(i);
												
						String serviceListName = svcGroupListobj.get("name").toString();
						
						String description = null;
						if (svcGroupListobj.containsKey("description")){
							description = svcGroupListobj.get("description").toString();
						}
						
						//getting members Array from the serviceGroup
						JsonArray membersArray = svcGroupListobj.getJsonArray("members");

						//String type = svcGroupListobj.get("type").toString();
						Boolean isServiceGroup = false;
						if (membersArray!=null){
							String membersType = membersArray.getJsonObject(0).get("type").toString();
							if (membersType.contains("REFERENCE")) {
								isServiceGroup = true;
							}
						}
												
						//Insert values into GROUPSERVICELIST table if name begins with Group 
						if (isServiceGroup) {
							
							//increment ID Primary Keys
							serviceListID = serviceListID + 1;

							String name = null;
							
							int membersIndex = 0;
							for (JsonValue membersValue : membersArray) {
								JsonObject membersObj = membersArray.getJsonObject(membersIndex);
								//String value = membersObj.get("name").toString();
								String type = membersObj.get("type").toString().replace("\"", "");
								
								String value = null;
								if(type.equals("REFERENCE")||type.equals("GROUP")||type.equals("SERVICE")){
									value = membersObj.get("name").toString();
								} else if (type.equalsIgnoreCase("ANY")){
									value = null;
								} else {
									value = membersObj.get("value").toString();
								}
								
								if(value != null){
									value = value.replace("\"", "");
								}
								
								if (name != null) {
									name = name.concat(",").concat(value);
								} else {
									name = value;
								}
								
								membersIndex++;
							}
							String nameInsert = "'"+name+"'";		
							
							insertQuery = "INSERT INTO GROUPSERVICELIST (ID, NAME, SERVICELIST) "
									+ "VALUES("+serviceListID+","+serviceListName+","+nameInsert+")";
							
							//Replace double quote with single quote
							insertQuery = insertQuery.replace('"', '\'');
							
							//Execute the queries to Insert data
				            st.executeUpdate(insertQuery);
													
						} else { //Insert JSON data serviceList table, protollist table, and portlist table

							//increment ID Primary Keys
							protocolID = protocolID + 1;
							portID = portID + 1;
							serviceGroupID = serviceGroupID + 1;
							
							String type = svcGroupListobj.get("type").toString();
							String transportProtocol = svcGroupListobj.get("transportProtocol").toString();
							String ports = svcGroupListobj.get("ports").toString();
							
							/*
							 * Create Queries to INSERT data into database table and execute 
							 */
							String serviceSql = "INSERT INTO SERVICEGROUP (ID, NAME, DESCRIPTION, TYPE, TRANSPORTPROTOCOL, APPPROTOCOL, PORTS) "
									+ "VALUES("+serviceGroupID+","+serviceListName+","+description+","+type+","
									+ transportProtocol+","+"null,"+ports+"); ";
							serviceSql = serviceSql.replace('"', '\'');
							st.addBatch(serviceSql);
							
							String protSql = "INSERT INTO PROTOCOLLIST (ID, PROTOCOLNAME, DESCRIPTION) VALUES("+protocolID+","+transportProtocol+","+transportProtocol+"); ";
							protSql = protSql.replace('"', '\'');
							st.addBatch(protSql);
							
							String portSql = "INSERT INTO PORTLIST (ID, PORTNAME, DESCRIPTION) VALUES("+portID+","+ports+","+ports+");";
							portSql = portSql.replace('"', '\'');
							st.addBatch(portSql);
							
							st.executeBatch();

						}		
						
						
						
						i++;
					}
				}
				
				/*
				 * Inserting addressGroup data into the ADDRESSGROUP table
				 */
				if (addressGroup != null) {
					int prefixID = 0;
					int addressID = 0;
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM PREFIXLIST;");
					if(rs.next()){
						prefixID = rs.getInt("ID");
					}
					rs.close();
					
					rs = st.executeQuery("SELECT MAX(ID) AS ID FROM ADDRESSGROUP;");
					if(rs.next()){
						addressID = rs.getInt("ID");
					}
					rs.close();
					
					
					int i = 0;
					for(JsonValue jsonValue : addressGroup) {
						
						/*
						 * Populate ArrayLists with values from the JSON
						 */
						//create the JSON object from the JSON Array for each iteration through the for loop
						JsonObject addressGroupObj = addressGroup.getJsonObject(i);
						
						//create JSON array for members
						JsonArray membersArray = addressGroupObj.getJsonArray("members");
						String addressGroupName = addressGroupObj.get("name").toString();
						
						String description = null;
						if (addressGroupObj.containsKey("description")){
							description = addressGroupObj.get("description").toString();
						}
						
						String prefixIP = null;
						String type = null;
						
						int membersIndex = 0;
						for (JsonValue membersValue : membersArray) {
							JsonObject membersObj = membersArray.getJsonObject(membersIndex);
							//String value = membersObj.get("value").toString();
							type = membersObj.get("type").toString().replace("\"", "");
							
							String value = null;
							if(type.equals("REFERENCE")||type.equals("GROUP")||type.equals("SERVICE")){
								value = membersObj.get("name").toString();
							} else if (type.equalsIgnoreCase("ANY")){
								value = null;
							} else {
								value = membersObj.get("value").toString();
							}
							
							if(value != null){
								value = value.replace("\"", "");
							}
							
							if (prefixIP != null) {
								prefixIP = prefixIP.concat(",").concat(value);
							} else {
								prefixIP = value;
							}
							
							membersIndex++;
						}
						String prefixList = "'"+prefixIP+"'";
						
						Boolean isAddressGroup = type.contains("REFERENCE");
						
						if (isAddressGroup) {								
							//increment ID Primary Keys
							addressID = addressID + 1;
							
							insertQuery = "INSERT INTO ADDRESSGROUP (ID, NAME, DESCRIPTION, PREFIXLIST) "
										+ "VALUES("+addressID+","+addressGroupName+","+description+","+prefixList+")";	
						} else {
							 //increment ID Primary Key
							prefixID = prefixID + 1;
							
							insertQuery = "INSERT INTO PREFIXLIST (ID, PL_NAME, PL_VALUE, DESCRIPTION) "
										+ "VALUES("+prefixID+","+addressGroupName+","+prefixList+","+description+")";
							
						}
		
						
						//Replace double quote with single quote
						insertQuery = insertQuery.replace('"', '\'');
						
						//Execute the queries to Insert data
			            st.executeUpdate(insertQuery);
						
						i++;
					}
					
				}
				
				/*
				 * Remove duplicate values from 'lookup' dictionary tables
				 */
				//ProtocolList Table
				String protoDelete = "DELETE FROM protocollist USING protocollist, protocollist p1 "
						+ "WHERE protocollist.id > p1.id AND protocollist.protocolname = p1.protocolname;";
				st.addBatch(protoDelete);
				
				//PortList Table
				String portListDelete = "DELETE FROM portlist USING portlist, portlist p1 "
						+ "WHERE portlist.id > p1.id AND portlist.portname = p1.portname; ";
				st.addBatch(portListDelete);
				
				//PrefixList Table
				String prefixListDelete = "DELETE FROM prefixlist USING prefixlist, prefixlist p1 "
						+ "WHERE prefixlist.id > p1.id AND prefixlist.pl_name = p1.pl_name AND "
						+ "prefixlist.pl_value = p1.pl_value AND prefixlist.description = p1.description; ";
				st.addBatch(prefixListDelete);
				
				//GroupServiceList
				String groupServiceDelete = "DELETE FROM groupservicelist USING groupservicelist, groupservicelist g1 "
						+ "WHERE groupservicelist.id > g1.id AND groupservicelist.name = g1.name AND "
						+ "groupservicelist.serviceList = g1.serviceList; ";
				st.addBatch(groupServiceDelete);
				
				st.executeBatch();
				
			} catch (ClassNotFoundException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(e.getMessage());
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "FirewallConfigPolicy", "Exception building Firewall queries ");
				System.out.println(e.getMessage());
				return false;
	
			} catch (SQLException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(e.getMessage());
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "FirewallConfigPolicy", "Exception executing Firewall queries");
				System.out.println(e.getMessage());
				return false;
			} catch (Exception e) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "FirewallConfigPolicy", "Exception getting Json values");
				System.out.println(e.getMessage());
				return false;
			} finally {
				try{
					if (con!=null) con.close();
					if (rs!=null) rs.close();
					if (st!=null) st.close();
				} catch (Exception ex){}
			}
			return true;

		} else {
			return false;
		}
		
	}


	private Boolean updateFirewallDictionaryData(String jsonBody, String prevJsonBody) {
		
		JsonObject oldJson = null;
		JsonObject newJson = null;
		
		if (jsonBody != null || prevJsonBody != null) {
			
			oldJson = stringToJson(prevJsonBody);
			newJson = stringToJson(jsonBody);
			
			//if no changes to the json then return true
			if (oldJson.equals(newJson)) {
				return true;
			}
			
			JsonArray firewallRules = null;
			JsonArray serviceGroup = null;
			JsonArray addressGroup = null;
			
			firewallRules = newJson.getJsonArray("firewallRuleList");
			serviceGroup = newJson.getJsonArray("serviceGroups");
			addressGroup = newJson.getJsonArray("addressGroups");	
			
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			
			/*
			 * Retrieve the property values for db access from the xacml.pap.properties
			 */
			papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
			papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
			papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
			papDbPassword = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD);
	
			//insert data into tables
			try {
				
				//Get DB Connection
				Class.forName(papDbDriver);
				con = DriverManager.getConnection(papDbUrl,papDbUser,papDbPassword);
				st = con.createStatement();
				
				JsonNode jsonDiff = createPatch(jsonBody, prevJsonBody);
				
				int i = 0;
				for (JsonNode node : jsonDiff) {
					//String path = jsonDiff.get(i).asText();
					String jsonpatch = jsonDiff.get(i).toString();
					
					JsonObject patchObj = stringToJson(jsonpatch);
					
					String path = patchObj.get("path").toString().replace('"', ' ').trim();
					 					
					if (path.contains("firewallRuleList")) {
						int termID = 0;
						int zoneID = 0;
						int actionID = 0;
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM TERM;");
						if(rs.next()){
							termID = rs.getInt("ID");
						}
						rs.close();
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM ZONE;");
						if(rs.next()){
							zoneID = rs.getInt("ID");
						}
						rs.close();
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM ACTIONLIST;");
						if(rs.next()){
							actionID = rs.getInt("ID");
						}
						rs.close();
						
						String insertQuery = null;
						
						/*
						 * Inserting firewallRuleList data into the Terms, SecurityZone, and Action tables
						 */
						int ri = 0;
						for(JsonValue jsonValue : firewallRules) {
							
							//increment ID Primary Keys
							termID = termID + 1;
							zoneID = zoneID + 1;
							actionID = actionID + 1;
							
							/*
							 * Populate ArrayLists with values from the JSON
							 */
							//create the JSON object from the JSON Array for each iteration through the for loop
							JsonObject ruleListobj = firewallRules.getJsonObject(ri);
							
							//get values from JSON fields of firewallRulesList Array
							String ruleName = ruleListobj.get("ruleName").toString().replace('"', '\'');
							String action = ruleListobj.get("action").toString().replace('"', '\'');
							String description = ruleListobj.get("description").toString().replace('"', '\'');
							
							rs = st.executeQuery("SELECT * FROM TERM WHERE TERMNAME = "+ ruleName + ";");
							
							if (rs.next()) {
								st.executeUpdate("DELETE FROM TERM WHERE TERMNAME = "+ ruleName + ";");
							}
							rs.close();
							
							//getting fromZone Array field from the firewallRulesList
							JsonArray fromZoneArray = ruleListobj.getJsonArray("fromZones");
							String fromZoneString = null;
							
							int fromZoneIndex = 0;
							for (JsonValue fromZoneJsonValue : fromZoneArray) {
								String value = fromZoneArray.get(fromZoneIndex).toString();
								value = value.replace("\"", "");
								
								if (fromZoneString != null) {
									fromZoneString = fromZoneString.concat(",").concat(value);
									
								} else {
									fromZoneString = value;
								}
								
								fromZoneIndex++;
								
							}
							String fromZoneInsert = "'"+fromZoneString+"'";
							
							//getting toZone Array field from the firewallRulesList
							JsonArray toZoneArray = ruleListobj.getJsonArray("toZones");
							String toZoneString = null;
							
							int toZoneIndex = 0;
							for (JsonValue toZoneJsonValue : toZoneArray) {
								String value = toZoneArray.get(toZoneIndex).toString();
								value = value.replace("\"", "");
								
								if (toZoneString != null) {
									toZoneString = toZoneString.concat(",").concat(value);
									
								} else {
									toZoneString = value;
								}
								
								toZoneIndex++;
								
							}
							String toZoneInsert = "'"+toZoneString+"'";
							//getting sourceList Array fields from the firewallRulesList
							JsonArray srcListArray = ruleListobj.getJsonArray("sourceList");
							String srcListString = null;
							
							int srcListIndex = 0;
							for (JsonValue srcListJsonValue : srcListArray) {
								JsonObject srcListObj = srcListArray.getJsonObject(srcListIndex);
								String type = srcListObj.get("type").toString().replace("\"", "");
								
								String value = null;
								if(type.equals("REFERENCE")||type.equals("GROUP")){
									value = srcListObj.get("name").toString();
								} else if (type.equalsIgnoreCase("ANY")){
									value = null;
								} else {
									value = srcListObj.get("value").toString();
								}
								
								if(value != null){
									value = value.replace("\"", "");
								}
								
								if (srcListString != null) {
									srcListString = srcListString.concat(",").concat(value);
									
								} else {
									srcListString = value;
								}
								
								srcListIndex++;
								
							}
							String srcListInsert = "'"+srcListString+"'";
							
							//getting destinationList Array fields from the firewallRulesList
							JsonArray destListArray = ruleListobj.getJsonArray("destinationList");
							String destListString = null;
							
							int destListIndex = 0;
							for (JsonValue destListJsonValue : destListArray) {
								JsonObject destListObj = destListArray.getJsonObject(destListIndex);
								String type = destListObj.get("type").toString().replace("\"", "");
								
								String value = null;
								if(type.equals("REFERENCE")||type.equals("GROUP")){
									value = destListObj.get("name").toString();
								} else if (type.equalsIgnoreCase("ANY")){
									value = null;
								} else {
									value = destListObj.get("value").toString();
								}
								
								if(value != null){
									value = value.replace("\"", "");
								}
								
								if (destListString != null) {
									destListString = destListString.concat(",").concat(value);
								} else {
									destListString = value;
								}
								
								destListIndex++;
							}
							String destListInsert = "'"+destListString+"'";
							
							//getting destServices Array fields from the firewallRulesList
							JsonArray destServicesArray = ruleListobj.getJsonArray("destServices");
							String destPortListString = null;
							
							int destPortListIndex = 0;
							for (JsonValue destListJsonValue : destServicesArray) {
								JsonObject destServicesObj = destServicesArray.getJsonObject(destPortListIndex);
								String type = destServicesObj.get("type").toString().replace("\"", "");
								
								String value = null;
								if(type.equals("REFERENCE")||type.equals("GROUP")){
									value = destServicesObj.get("name").toString();
								} else if (type.equalsIgnoreCase("ANY")){
									value = null;
								} else {
									value = destServicesObj.get("value").toString();
								}
								
								if(value != null){
									value = value.replace("\"", "");
								}
								
								if (destPortListString != null) {
									destPortListString = destPortListString.concat(",").concat(value);
								} else {
									destPortListString = value;
								}
								
								destPortListIndex++;
							}
							String destPortListInsert = "'"+destPortListString+"'";					
							
							/*
							 * Create Queries to INSERT data into database tables and execute 
							 */
							
							//Insert Into Terms table
	                        String termSql = "INSERT INTO Term (ID, TERMNAME, SRCIPLIST, DESTIPLIST, PROTOCOLLIST, PORTLIST, SRCPORTLIST,"
	                                 + " DESTPORTLIST, ACTION, DESCRIPTION, FROMZONE, TOZONE, CREATED_BY, MODIFIED_DATE) VALUES ("+termID+","
	                                 +ruleName+","+srcListInsert+","+destListInsert+","+ "null"+","+"null"+","+"null"+","+destPortListInsert+","
	                                 +action+","+description+","+fromZoneInsert+","+toZoneInsert+",'API',"+ "null"+ "); ";
							
							termSql = termSql.replace('"', '\'');
							st.addBatch(termSql);
							
							rs = st.executeQuery("SELECT * FROM ACTIONLIST WHERE ACTIONNAME = " + action + ";");
							
							String actionSql = null;
							if (rs.next()) {
								//do nothing
							} else {
								actionSql = "INSERT INTO ACTIONLIST (ID, ACTIONNAME, DESCRIPTION) VALUES ("+actionID+","+action+","+action+") ";
								actionSql = actionSql.replace('"', '\'');
								st.addBatch(actionSql);
							}

							st.executeBatch();
							
							ri++;
						}
						
					}
					
					if (path.contains("serviceGroups")) {
						int serviceGroupID = 0;
						int serviceListID = 0;
						int protocolID = 0;
						int portID = 0;

						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM SERVICEGROUP;");
						if(rs.next()){
							serviceGroupID = rs.getInt("ID");
						}
						rs.close();
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM GROUPSERVICELIST;");
						if(rs.next()){
							serviceListID = rs.getInt("ID");
						}
						rs.close();
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM PROTOCOLLIST;");
						if(rs.next()){
							protocolID = rs.getInt("ID");
						}
						rs.close();
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM PORTLIST;");
						if(rs.next()){
							portID = rs.getInt("ID");
						}
						rs.close();
						
						String insertQuery = null;
						
						/*
						 * Inserting serviceGroups data into the ServiceGroup, ServiceList, ProtocolList, and PortList tables
						 */
						int si = 0;
						for(JsonValue jsonValue : serviceGroup) {
							
							/*
							 * Populate ArrayLists with values from the JSON
							 */
							//create the JSON object from the JSON Array for each iteration through the for loop
							JsonObject svcGroupListobj = serviceGroup.getJsonObject(si);
							
							String groupName = svcGroupListobj.get("name").toString().replace('"', '\''); 
							
							String description = null;
							if (svcGroupListobj.containsKey("description")){
								description = svcGroupListobj.get("description").toString().replace('"', '\'');
							}
							
							JsonArray membersArray = svcGroupListobj.getJsonArray("members");

							Boolean isServiceGroup = false;
							if (membersArray!=null){
								String membersType = membersArray.getJsonObject(0).get("type").toString();
								if (membersType.contains("REFERENCE")) {
									isServiceGroup = true;
								}
							}
							
							//Insert values into GROUPSERVICELIST table if name begins with Group 
							if (isServiceGroup) {
								
								rs = st.executeQuery("SELECT * FROM GROUPSERVICELIST WHERE NAME = "+ groupName + ";");
								
								if (rs.next()) {
									st.executeUpdate("DELETE FROM GROUPSERVICELIST WHERE NAME = "+ groupName + ";");
								}
								rs.close();
								//increment ID Primary Keys
								serviceListID = serviceListID + 1;
								
								String name = null;
								int membersIndex = 0;
								for (JsonValue membersValue : membersArray) {
									JsonObject membersObj = membersArray.getJsonObject(membersIndex);
									String type = membersObj.get("type").toString().replace("\"", "");
									
									String value = null;
									if(type.equals("REFERENCE")||type.equals("GROUP")||type.equals("SERVICE")){
										value = membersObj.get("name").toString();
									} else if (type.equalsIgnoreCase("ANY")){
										value = null;
									} else {
										value = membersObj.get("value").toString();
									}
									
									if(value != null){
										value = value.replace("\"", "");
									}
									
									if (name != null) {
										name = name.concat(",").concat(value);
									} else {
										name = value;
									}
									
									membersIndex++;
								}
								String nameInsert = "'"+name+"'";		
								
								insertQuery = "INSERT INTO GROUPSERVICELIST (ID, NAME, SERVICELIST) "
										+ "VALUES("+serviceListID+","+groupName+","+nameInsert+")";
								
								//Replace double quote with single quote
								insertQuery = insertQuery.replace('"', '\'');
								
								//Execute the queries to Insert data
					            st.executeUpdate(insertQuery);
														
							} else { //Insert JSON data serviceGroup table, protocollist table, and portlist table
																
								//increment ID Primary Keys
								protocolID = protocolID + 1;
								portID = portID + 1;
								serviceGroupID = serviceGroupID + 1;
								
								String type = svcGroupListobj.get("type").toString().replace('"', '\'');
								String transportProtocol = svcGroupListobj.get("transportProtocol").toString().replace('"', '\'');
								String ports = svcGroupListobj.get("ports").toString().replace('"', '\'');
								
								rs = st.executeQuery("SELECT * FROM SERVICEGROUP WHERE NAME = "+ groupName + ";");
								
								if (rs.next()) {
									st.executeUpdate("DELETE FROM SERVICEGROUP WHERE NAME = "+ groupName + ";");
								}
								rs.close();
								
								String svcGroupSql = "INSERT INTO SERVICEGROUP (ID, NAME, DESCRIPTION, TYPE, TRANSPORTPROTOCOL, APPPROTOCOL, PORTS) "
											+ "VALUES("+serviceGroupID+","+groupName+","+description+","+type+","
											+ transportProtocol+","+"null,"+ports+"); ";
								svcGroupSql = svcGroupSql.replace('"', '\'');
								st.addBatch(svcGroupSql);
								
								rs = st.executeQuery("SELECT * FROM PROTOCOLLIST WHERE PROTOCOLNAME = " + transportProtocol + ";");
								
								String protoSql = null;
								if (rs.next()) {
									//do nothing
								} else {
									protoSql = "INSERT INTO PROTOCOLLIST (ID, PROTOCOLNAME, DESCRIPTION) "
											+ "VALUES("+protocolID+","+transportProtocol+","+transportProtocol+"); ";
									protoSql = protoSql.replace('"', '\'');
									st.addBatch(protoSql);

								}
								rs.close();

								rs = st.executeQuery("SELECT * FROM PORTLIST WHERE PORTNAME = " + ports + ";");
								
								String portSql = null;
								if (rs.next()) {
									//do nothing
								} else {
									portSql = "INSERT INTO PORTLIST (ID, PORTNAME, DESCRIPTION) VALUES("+portID+","+ports+","+ports+"); ";
									portSql = portSql.replace('"', '\'');
									st.addBatch(portSql);
								}
								rs.close();
								
								st.executeBatch();

							}		
							
							
				            si++;
						}
						
					}
					
					if (path.contains("addressGroups")) {
						/*
						 * Inserting addressGroup data into the ADDRESSGROUP table
						 */
						int prefixID = 0;
						int addressID = 0;
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM PREFIXLIST;");
						if(rs.next()){
							prefixID = rs.getInt("ID");
						}
						rs.close();
						
						rs = st.executeQuery("SELECT MAX(ID) AS ID FROM ADDRESSGROUP;");
						if(rs.next()){
							addressID = rs.getInt("ID");
						}
						rs.close();
						
						String insertQuery = null;
						
						int ai = 0;
						for(JsonValue jsonValue : addressGroup) {
							
							/*
							 * Populate ArrayLists with values from the JSON
							 */
							//create the JSON object from the JSON Array for each iteration through the for loop
							JsonObject addressGroupObj = addressGroup.getJsonObject(ai);
							
							//create JSON array for members
							JsonArray membersArray = addressGroupObj.getJsonArray("members");
							String addressGroupName = addressGroupObj.get("name").toString().replace('"', '\'');
							
							String description = null;
							if (addressGroupObj.containsKey("description")){
								description = addressGroupObj.get("description").toString().replace('"', '\'');
							}
							
							String prefixIP = null;
							String type = null;
							int membersIndex = 0;
							for (JsonValue membersValue : membersArray) {
								JsonObject membersObj = membersArray.getJsonObject(membersIndex);
								type = membersObj.get("type").toString().replace("\"", "");
								
								String value = null;
								if(type.equals("REFERENCE")||type.equals("GROUP")||type.equals("SERVICE")){
									value = membersObj.get("name").toString();
								} else if (type.equalsIgnoreCase("ANY")){
									value = null;
								} else {
									value = membersObj.get("value").toString();
								}
								
								if(value != null){
									value = value.replace("\"", "");
								}
								
								if (prefixIP != null) {
									prefixIP = prefixIP.concat(",").concat(value);
								} else {
									prefixIP = value;
								}
								
								membersIndex++;
							}
							String prefixList = "'"+prefixIP+"'";
							
							Boolean isAddressGroup = type.contains("REFERENCE");
							
							if (isAddressGroup) {		
								
								rs = st.executeQuery("SELECT * FROM ADDRESSGROUP WHERE NAME = "+ addressGroupName + ";");
								
								if (rs.next()) {
									st.executeUpdate("DELETE FROM ADDRESSGROUP WHERE NAME = "+ addressGroupName + ";");
								}
								rs.close();
								//increment ID Primary Keys
								addressID = addressID + 1;
								
								insertQuery = "INSERT INTO ADDRESSGROUP (ID, NAME, DESCRIPTION, PREFIXLIST) "
											+ "VALUES("+addressID+","+addressGroupName+","+description+","+prefixList+")";	
								
								
								
							} else {
								
								rs = st.executeQuery("SELECT * FROM PREFIXLIST WHERE PL_NAME = "+ addressGroupName + ";");
								
								if (rs.next()) {
									st.executeUpdate("DELETE FROM PREFIXLIST WHERE PL_NAME = "+ addressGroupName + ";");
								}
								rs.close();
								 //increment ID Primary Key
								prefixID = prefixID + 1;
								
								insertQuery = "INSERT INTO PREFIXLIST (ID, PL_NAME, PL_VALUE, DESCRIPTION) "
											+ "VALUES("+prefixID+","+addressGroupName+","+prefixList+","+description+")";
								
							}
			
							
							//Replace double quote with single quote
							insertQuery = insertQuery.replace('"', '\'');
							
							//Execute the queries to Insert data
				            st.executeUpdate(insertQuery);
							
							ai++;
						}
						
					}
						
					i++;	
				}
				
				/*
				 * Remove duplicate values from 'lookup' dictionary tables
				 */
				//ProtocolList Table
				String protoDelete = "DELETE FROM protocollist USING protocollist, protocollist p1 "
						+ "WHERE protocollist.id > p1.id AND protocollist.protocolname = p1.protocolname;";
				st.addBatch(protoDelete);
				
				//PortList Table
				String portListDelete = "DELETE FROM portlist USING portlist, portlist p1 "
						+ "WHERE portlist.id > p1.id AND portlist.portname = p1.portname; ";
				st.addBatch(portListDelete);
				
				//PrefixList Table
				String prefixListDelete = "DELETE FROM prefixlist USING prefixlist, prefixlist p1 "
						+ "WHERE prefixlist.id > p1.id AND prefixlist.pl_name = p1.pl_name AND "
						+ "prefixlist.pl_value = p1.pl_value AND prefixlist.description = p1.description; ";
				st.addBatch(prefixListDelete);
				
				//GroupServiceList
				String groupServiceDelete = "DELETE FROM groupservicelist USING groupservicelist, groupservicelist g1 "
						+ "WHERE groupservicelist.id > g1.id AND groupservicelist.name = g1.name AND "
						+ "groupservicelist.serviceList = g1.serviceList; ";
				st.addBatch(groupServiceDelete);
				
				st.executeBatch();
				
			} catch (ClassNotFoundException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(e.getMessage());
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "FirewallConfigPolicy", "Exception building Firewall queries");
				System.out.println(e.getMessage());
				return false;
	
			} catch (SQLException e) {
				//TODO:EELF Cleanup - Remove logger
				//logger.error(e.getMessage());
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "FirewallConfigPolicy", "Exception executing Firewall queries");
				System.out.println(e.getMessage());
				return false;
			} finally {
				try{
					if (con!=null) con.close();
					if (rs!=null) rs.close();
					if (st!=null) st.close();
				} catch (Exception ex){}
			}
			return true;
	
		} else {
			return false;
		}
	
}
	
	private JsonObject stringToJson(String jsonString) {
		
		JsonObject json = null;
		if (jsonString != null) {
			
			//Read jsonBody to JsonObject
			StringReader in = null;
			
			in = new StringReader(jsonString);
			
			JsonReader jsonReader = Json.createReader(in);
			json = jsonReader.readObject();
		}
		
		return json;
	}
		
		
	private JsonNode createPatch(String json, String oldJson) {
		JsonNode oldJason = null;
		JsonNode updatedJason = null;

		try {
		oldJason = JsonLoader.fromString(oldJson);
		updatedJason = JsonLoader.fromString(json);
		} catch (IOException e) {
		e.printStackTrace();
		}

		JsonPatch jsonPatch = JsonDiff.asJsonPatch(oldJason, updatedJason);
		JsonNode patchNode = JsonDiff.asJson(oldJason, updatedJason);
		System.out.println("Sending Patch:" + jsonPatch);
		return patchNode;

		}

	@Override
	public Object getCorrectPolicyDataObject() {
		return policyAdapter.getPolicyData();
	}

}
	
	
	
