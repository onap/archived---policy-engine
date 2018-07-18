/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.components;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.xacml.util.XACMLPolicyWriter;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacmlatt.pdp.policy.PolicyDef;
import com.att.research.xacmlatt.pdp.policy.dom.DOMPolicyDef;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public abstract class Policy {
	
	private static final Logger LOGGER	= FlexLogger.getLogger(Policy.class);
	

	/**
	 * Common Fields
	 */
	public static final String GET_INT_TYPE = "Integer";
	public static final String GET_STRING_TYPE = "String";

	public static final String ONAPID = "ONAPName";
	public static final String CONFIGID = "ConfigName";
	public static final String CLOSEDLOOPID = "ServiceType";

	public static final String CONFIG_POLICY = "Config";
	public static final String ACTION_POLICY = "Action";
	public static final String DECISION_POLICY = "Decision";

	protected String policyName = null;

	protected boolean isValidForm = true;

	private Path finalPolicyPath = null;

	private boolean preparedToSave = false;

	private boolean policyExists = false;

	public Path getFinalPolicyPath() {
		return finalPolicyPath;
	}

	public void setFinalPolicyPath(Path finalPolicyPath) {
		this.finalPolicyPath = finalPolicyPath;
	}

	// Constants Used in XML Creation
	public static final String CATEGORY_RECIPIENT_SUBJECT = "urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject";
	public static final String CATEGORY_RESOURCE = "urn:oasis:names:tc:xacml:3.0:attribute-category:resource";
	public static final String CATEGORY_ACTION = "urn:oasis:names:tc:xacml:3.0:attribute-category:action";
	public static final String CATEGORY_ACCESS_SUBJECT = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
	public static final String ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";
	public static final String SUBJECT_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	public static final String RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	public static final String FUNTION_INTEGER_ONE_AND_ONLY = "urn:oasis:names:tc:xacml:1.0:function:integer-one-and-only";
	public static final String FUNCTION_STRING_ONE_AND_ONLY = "urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";
	public static final String FUNCTION_BOOLEAN_ONE_AND_ONLY = "urn:oasis:names:tc:xacml:1.0:function:boolean-one-and-only";
	public static final String FUNCTION_STRING_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-equal";
	public static final String FUNCTION_STRING_REGEX_MATCH = "org.onap.function.regex-match";
	public static final String FUNCTION_STRING_REGEXP_MATCH = "urn:oasis:names:tc:xacml:1.0:function:string-regexp-match";
	public static final String FUNCTION_STRING_EQUAL_IGNORE = "urn:oasis:names:tc:xacml:3.0:function:string-equal-ignore-case";
	public static final String INTEGER_DATATYPE = "http://www.w3.org/2001/XMLSchema#integer";
	public static final String BOOLEAN_DATATYPE = "http://www.w3.org/2001/XMLSchema#boolean";
	public static final String STRING_DATATYPE = "http://www.w3.org/2001/XMLSchema#string";
	public static final String URI_DATATYPE = "http://www.w3.org/2001/XMLSchema#anyURI";
	public static final String RULE_VARIABLE = "var:";
	public static final String EMPTY_STRING = "";

	protected static String CONFIG_HOME = null;
	protected static String ACTION_HOME = null;
	protected static String CONFIG_URL = null;

	protected Map<String, String> performer = new HashMap<>();

	private static String actionHome = null;
	private static String configHome = null;

	public PolicyRestAdapter policyAdapter = null;
	String ruleID = "";

	public Policy() {
		CONFIG_HOME = getConfigHome();
		ACTION_HOME = getActionHome();
		CONFIG_URL = "$URL";
		performer.put("PDP", "PDPAction");
		performer.put("PEP", "PEPAction");
	}

	//Each policy type seems to either use policyData or data field policy adapter when
	//getting the xml to save the policy. Instead of keep this hardcoded in the save method,
	//this method makes it usable outside.
	/**
	 * Return the data field of the PolicyAdapter that will be used when saving this policy
	 * with the savePolicies method.
	 * @return Either the PolicyAdapter.getData() or PolicyAdapter.getPolicyData()
	 */
	public abstract Object getCorrectPolicyDataObject();
	public abstract Map<String, String>  savePolicies() throws PAPException;

	//This is the method for preparing the policy for saving.  We have broken it out
	//separately because the fully configured policy is used for multiple things
	public abstract boolean prepareToSave() throws PAPException;


	// create match for onap and config name
	protected MatchType createMatch(String key, String value) {
		MatchType match = new MatchType();

		AttributeValueType attributeValue = new AttributeValueType();
		attributeValue.setDataType(STRING_DATATYPE);
		attributeValue.getContent().add(value);
		match.setAttributeValue(attributeValue);
		AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
		URI uri = null;
		try {
			uri = new URI(key);
		} catch (URISyntaxException e) {
			LOGGER.error("Exception Occured"+e);
		}
		attributeDesignator.setCategory(CATEGORY_ACCESS_SUBJECT);
		attributeDesignator.setDataType(STRING_DATATYPE);
		attributeDesignator.setAttributeId(new IdentifierImpl(uri).stringValue());
		match.setAttributeDesignator(attributeDesignator);
		match.setMatchId(FUNCTION_STRING_REGEX_MATCH);
		return match;
	}

	// Creating the match for dynamically added components.
	protected MatchType createDynamicMatch(String key, String value) {
		MatchType dynamicMatch = new MatchType();
		AttributeValueType dynamicAttributeValue = new AttributeValueType();
		String dataType = null;
		dataType = STRING_DATATYPE;
		dynamicAttributeValue.setDataType(dataType);
		dynamicAttributeValue.getContent().add(value);
		dynamicMatch.setAttributeValue(dynamicAttributeValue);

		AttributeDesignatorType dynamicAttributeDesignator = new AttributeDesignatorType();

		URI dynamicURI = null;
		try {
			dynamicURI = new URI(key);
		} catch (URISyntaxException e) {
			LOGGER.error("Exception Occured"+e);// log msg
		}
		dynamicAttributeDesignator.setCategory(CATEGORY_RESOURCE);
		dynamicAttributeDesignator.setDataType(dataType);
		dynamicAttributeDesignator.setAttributeId(new IdentifierImpl(dynamicURI).stringValue());
		dynamicMatch.setAttributeDesignator(dynamicAttributeDesignator);
		dynamicMatch.setMatchId(FUNCTION_STRING_REGEX_MATCH);

		return dynamicMatch;
	}

	//  the Policy Name as Unique One throws error
	@SuppressWarnings("static-access")
	protected Path getNextFilename(Path parent, String policyType, String polcyFileName, Integer version) {
		policyType = FilenameUtils.removeExtension(policyType);
		polcyFileName = FilenameUtils.removeExtension(polcyFileName);
		Path newFile = null;
		String policyDir = EMPTY_STRING;
		String absolutePath = parent.toString();
		if (absolutePath != null && !absolutePath.equals(EMPTY_STRING)) {
			policyDir = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.length());
			if (policyDir == null || policyDir.equals(EMPTY_STRING)) {
				policyDir = absolutePath.substring(absolutePath.lastIndexOf('/') + 1, absolutePath.length());
			}
		}

		String fileName = "default";
		if (policyDir != null && !policyDir.equals(EMPTY_STRING)) {
			fileName = policyType + "_" + String.format(polcyFileName) + "." + version + ".xml";
		} 
			
		newFile = Paths.get(parent.toString(), fileName);
		if (newFile.toFile().exists()) {
			return newFile;
		}
		return null;
	}

	protected Path getNextLoopFilename(Path parentPath, String policyType, String policyConfigType, String policyFileName, Integer version) {
		policyType = FilenameUtils.removeExtension(policyType);
		policyConfigType = FilenameUtils.removeExtension(policyConfigType);
		policyFileName = FilenameUtils.removeExtension(policyFileName);
		Path newFile = null;
		String policyDir = EMPTY_STRING;
		String absolutePath = parentPath.toString();
		if (absolutePath != null && !absolutePath.equals(EMPTY_STRING)) {
			policyDir = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.length());
			if (policyDir.equals(EMPTY_STRING)) {
				policyDir = absolutePath.substring(absolutePath.lastIndexOf('/') + 1, absolutePath.length());
			}
		}

		String fileName = "default";
		if (!policyDir.equals(EMPTY_STRING)) {
			if("ClosedLoop_PM".equals(policyConfigType)){
				fileName = policyType + "_" + "PM" + "_" +java.lang.String.format(policyFileName) + "." +version +".xml";
			}else if("ClosedLoop_Fault".equals(policyConfigType)){
				fileName = policyType + "_" + "Fault" + "_" +java.lang.String.format(policyFileName) +  "." + version + ".xml";
			}else if("Micro Service".equals(policyConfigType)){
				fileName = policyType + "_" + "MS" + "_" + java.lang.String.format(policyFileName) + "." + version + ".xml";
			}else if("Optimization".equals(policyConfigType)) {
				fileName = policyType + "_" + "OOF" + "_" + java.lang.String.format(policyFileName) + "." + version + ".xml";
			}
		} 

		newFile = Paths.get(parentPath.toString(), fileName);

		if (newFile.toFile().exists()) {
			return newFile;
		}
		return null;
	}


	//create policy once all the validations are completed
	protected Map<String, String> createPolicy(final Path policyPath, final Object policyData) {
		Map<String, String> success = new HashMap<>(); 
		//
		// Is the root a PolicySet or Policy?
		//

		if (policyData instanceof PolicyType) {
			//
			// Write it out
			//
			//Does not need to be XACMLPolicyWriterWithPapNotify since it is already in the PAP
			//and this transaction is intercepted up stream.

			try(InputStream inputStream = XACMLPolicyWriter.getXmlAsInputStream((PolicyType) policyData)) {
				PolicyDef policyDef = DOMPolicyDef.load(inputStream);
				if (policyDef == null) {
					success.put("validation", "PolicyDef Validation Failed");
				}else{
					success.put("success", "success");
				}
			} catch (Exception e) {
				LOGGER.error("PolicyDef Validation failed"+e);
				success.put("error", "Validation Failed");
			}
		} else {
			PolicyLogger.error("Unknown data type sent back.");
			return success;
		}
		return success;
	}

	public static String getConfigHome(){
		try {
			loadWebapps();
		} catch (Exception e) {
			LOGGER.debug(e);
			return null;
		}
		return configHome;
	}

	public static String getActionHome(){
		try {
			loadWebapps();
		} catch (Exception e) {
			LOGGER.debug(e);
			return null;
		}
		return actionHome;
	}

	private static void loadWebapps() throws PAPException{
		if(actionHome == null || configHome == null){
			Path webappsPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS));
			//Sanity Check
			if (webappsPath == null) {
				PolicyLogger.error("Invalid Webapps Path Location property : " + XACMLRestProperties.PROP_PAP_WEBAPPS);
				throw new PAPException("Invalid Webapps Path Location property : " + XACMLRestProperties.PROP_PAP_WEBAPPS);
			}
			Path webappsPathConfig;
			Path webappsPathAction;
			if(webappsPath.toString().contains("\\")){
				webappsPathConfig = Paths.get(webappsPath.toString()+"\\Config");
				webappsPathAction = Paths.get(webappsPath.toString()+"\\Action");
			}else{
				webappsPathConfig = Paths.get(webappsPath.toString()+"/Config");
				webappsPathAction = Paths.get(webappsPath.toString()+"/Action");
			}
			if(!webappsPathConfig.toFile().exists()){
				try {
					Files.createDirectories(webappsPathConfig);
				} catch (IOException e) {
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "Policy", "Failed to create config directory");
				}
			}
			if(!webappsPathAction.toFile().exists()){
				try {
					Files.createDirectories(webappsPathAction);
				} catch (IOException e) {
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "Policy", "Failed to create config directory");
				}
			}
			actionHome = webappsPathAction.toString();
			configHome = webappsPathConfig.toString();
		}
	}
	
	public boolean validateConfigForm() {
		return true;
	}

	/**
	 * @return the preparedToSave
	 */
	public boolean isPreparedToSave() {
		return preparedToSave;
	}

	/**
	 * @param preparedToSave the preparedToSave to set
	 */
	protected void setPreparedToSave(boolean preparedToSave) {
		this.preparedToSave = preparedToSave;
	}

	public boolean isPolicyExists() {
		return policyExists;
	}

	public void setPolicyExists(boolean policyExists) {
		this.policyExists = policyExists;
	}


}
