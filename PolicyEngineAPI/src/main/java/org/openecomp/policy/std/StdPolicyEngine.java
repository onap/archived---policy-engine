/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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


package org.openecomp.policy.std;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
//import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openecomp.policy.api.AttributeType;
import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.DecisionRequestParameters;
import org.openecomp.policy.api.DecisionResponse;
import org.openecomp.policy.api.DeletePolicyParameters;
import org.openecomp.policy.api.DictionaryParameters;
import org.openecomp.policy.api.EventRequestParameters;
import org.openecomp.policy.api.ImportParameters;
import org.openecomp.policy.api.NotificationHandler;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.PolicyChangeResponse;
import org.openecomp.policy.api.PolicyClass;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyConfigType;
import org.openecomp.policy.api.PolicyDecision;
import org.openecomp.policy.api.PolicyDecisionException;
import org.openecomp.policy.api.PolicyEngineException;
import org.openecomp.policy.api.PolicyEventException;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.api.PolicyResponse;
import org.openecomp.policy.api.PolicyResponseStatus;
import org.openecomp.policy.api.PolicyType;
import org.openecomp.policy.api.PushPolicyParameters;
import org.openecomp.policy.api.RuleProvider;
//import org.openecomp.policy.utils.AAFPolicyClient;
//import org.openecomp.policy.utils.AAFPolicyException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import com.att.research.xacml.api.Advice;
import com.att.research.xacml.api.AttributeAssignment;
import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Obligation;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.std.pap.StdPAPPolicy;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;



/**
 * PolicyEngine Implementation class
 * 
 * @version 1.0
 */
public class StdPolicyEngine {
	// Change the default Priority value here. 
	private static final int defaultPriority = 9999;
	
	private String propertyFilePath = null;
	private static List<String> pdps = null;
	private static List<String> paps = null;
	private static String environment= null; 
	private static String userName = null;
	private static String pass = null; 
	private static List<String> encoding = null;
	private static List<String> encodingPAP = null;
	private List<String> pdp_default = null;
	private List<String> pap_default = null;
	private List<String> type_default = null;
	private List<String> notificationType = new ArrayList<String>();
	private List<String> uebURLList = new ArrayList<String>();
	private NotificationScheme scheme = null;
	private NotificationHandler handler = null;
	private Matches match = null;
	private Boolean decide = false;
	private AutoClientUEB UEBClientThread = null;
	private Thread registerUEBThread = null;
	private boolean UEBThread = false;
	private String policyId = null;	
	private String description = null;	
	private String pushVersion = null;
	private boolean isValid = false;
	private int responseCode = 0;
	private boolean unique = false;
	private boolean junit = false;
	//private AAFPolicyClient aafClient = null;
	// Backward code. 
	private String pyPDPClientFile = null;
	
	final private static String uniqueID = UUID.randomUUID ().toString ();
	
	private static Logger logger = FlexLogger.getLogger(StdPolicyConfig.class.getName());
	
	/*
	 * Taking the Property file even if it null.
	 */
	public StdPolicyEngine(String propertyFilePath)
			throws PolicyEngineException {
		setProperty(propertyFilePath);
	}

	/*
	 * Taking the Notification Constructor.
	 */
	public StdPolicyEngine(String propertyFilePath, NotificationScheme scheme,
			NotificationHandler handler) throws PolicyEngineException {
		setProperty(propertyFilePath);
		this.scheme = scheme;
		this.handler = handler;
		if (!notificationType.get(0).equals("ueb")){
			AutoClientEnd.setAuto(scheme, handler);
		}
		notification(scheme, handler);
	}

	/*
	 * Taking the Notification Constructor.
	 */
	public StdPolicyEngine(String propertyFilePath, NotificationScheme scheme)
			throws PolicyEngineException {
		setProperty(propertyFilePath);
		this.scheme = scheme;
		setScheme(scheme);
	}

	// This Call will be used by PyPDP Requests
	public StdPolicyEngine(List<String> configURL, List<String> configPapURL, List<String> encodingPAP, List<String> encoding, NotificationScheme scheme, NotificationHandler handler, String environment, String clientProperties, Boolean isTest) {
		StdPolicyEngine.pdps = configURL;
		StdPolicyEngine.paps = configPapURL;
		StdPolicyEngine.encoding = encoding;
		StdPolicyEngine.encodingPAP = encodingPAP;
		StdPolicyEngine.environment = environment;
		Properties props = new Properties();
		props.setProperty("ENVIRONMENT", environment);
		//Not Supported for 1610 Open Source
		/*try {
			aafClient = AAFPolicyClient.getInstance(props);
		} catch (AAFPolicyException e) {
			logger.error(XACMLErrorConstants.ERROR_UNKNOWN + e.getMessage());
		}*/
		pyPDPClientFile = clientProperties;
		// Default Notification Type for PyPDPServers. 
		notificationType.add("websocket");
		if(!isTest){
			notification(scheme, handler);
		}
	}

	/*
	 * sendEvent API Implementation
	 */
	public Collection<PolicyResponse> event(Map<String, String> eventAttributes, UUID requestID)
			throws PolicyEventException {
		Collection<PolicyResponse> policyResponse = null;
		policyResponse = event(eventAttributes, requestID, userName, pass);
		return policyResponse;
	}
	
	/*
	 * sendEvent API Implementation for eventRequestParameters 
	 */
	public Collection<PolicyResponse> event(EventRequestParameters eventRequestParameters) throws PolicyEventException{
		Collection<PolicyResponse> response = event(eventRequestParameters.getEventAttributes(), eventRequestParameters.getRequestID());
		return response;
	}

	/*
	 * getConfig API Implementation
	 */
	public Collection<PolicyConfig> config(String eCOMPComponentName,
			String configName, Map<String, String> configAttributes, UUID requestID)
			throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = null;
		policyConfig = config(eCOMPComponentName, configName, configAttributes, requestID, userName, pass);
		return policyConfig;
	}

	/*
	 * getConfig API Implementation
	 */
	public Collection<PolicyConfig> config(String eCOMPComponentName,
			String configName, UUID requestID) throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = null;
		policyConfig = config(eCOMPComponentName, configName,null, requestID, userName, pass);
		return policyConfig;
	}

	/*
	 * getConfig API Implementation
	 */
	public Collection<PolicyConfig> config(String eCOMPComponentName, UUID requestID)
			throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = null;
		policyConfig = config(eCOMPComponentName, requestID, userName, pass);
		return policyConfig;
	}

	/*
	 * getConfig using the PolicyFileName Implementation
	 */
	public Collection<PolicyConfig> policyName(String policyName, UUID requestID)
			throws PolicyConfigException {
		Collection<PolicyConfig> policyConfig = null;
		policyConfig = configPolicyName(policyName, requestID, userName, pass);
		return policyConfig;
	}
	
	/*
	 * getConfig using configRequestParameters Implementation 
	 */
	public Collection<PolicyConfig> config(ConfigRequestParameters configRequestParameters) throws PolicyConfigException{
		Collection<PolicyConfig> response = null;
		response = configRequest(configRequestParameters, userName, pass);
		return response;
	}
	
	/*
	 * listPolicies using configRequestParameters Implementation
	 */
	public Collection<String> listConfig(ConfigRequestParameters listPolicyRequestParameters) throws PolicyConfigException{
		Collection<String> policyList = new ArrayList<String>();
		policyList = listConfigRequest(listPolicyRequestParameters, userName, pass);
		return policyList;
	}

	/*
	 * getDecision using the decision Attributes.
	 */
	public DecisionResponse decide(String eCOMPComponentName,
			Map<String, String> decisionAttributes, UUID requestID)
			throws PolicyDecisionException {
		DecisionResponse policyDecision = policyDecide(eCOMPComponentName,
				decisionAttributes, requestID, userName, pass);
		return policyDecision;
	}
	
	/*
	 * getDecision Using decisionRequestParameters.  
	 */
	public DecisionResponse decide(DecisionRequestParameters decisionRequestParameters) throws PolicyDecisionException{
		DecisionResponse decision = decide(decisionRequestParameters.getECOMPComponentName(), decisionRequestParameters.getDecisionAttributes(), decisionRequestParameters.getRequestID());
		return decision;
	}
	
	/*
	 * PushPolicy using pushPolicyParameters. 
	 */
	public PolicyChangeResponse pushPolicy(PushPolicyParameters pushPolicyParameters) throws Exception{
		return pushPolicy(pushPolicyParameters, userName, pass);
	}
	
	public PolicyChangeResponse pushPolicy(PushPolicyParameters pushPolicyParameters, String userID, String passcode) throws Exception{
		StdPolicyChangeResponse response = new StdPolicyChangeResponse();
		String resource= "pushPolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseMessage(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseCode(401);
			return response;
		}
		String plainName = null;
		String scope = null;
		try{
			if(pushPolicyParameters.getPolicyName()!=null){
				plainName = pushPolicyParameters.getPolicyName().substring(pushPolicyParameters.getPolicyName().lastIndexOf(".")+1, pushPolicyParameters.getPolicyName().length());
				scope = pushPolicyParameters.getPolicyName().substring(0, pushPolicyParameters.getPolicyName().lastIndexOf("."));
				logger.info("Name is "+ plainName +"   scope is "+ scope);
			}
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
		}

		// check incoming requestID, if null then generate one here so the same id can be used for the multiple transactions for the same Push Policy request  (i.e. POST, PUT)
		UUID requestID = pushPolicyParameters.getRequestID();
		if (requestID == null) {
			requestID = UUID.randomUUID();
			logger.info("Request ID was not provided from input, so sending generated ID: " + requestID.toString());
		} else {	
			logger.info("Request ID was provided from input: " + requestID.toString());
		}
		// now use the local requestID field derived above to pass to the rest of the Push Policy process (below)
		// response.setResponseMessage(pushPolicy(scope, plainName, pushPolicyParameters.getPolicyType(), pushPolicyParameters.getPdpGroup(), pushPolicyParameters.getRequestID()));
		response.setResponseMessage(pushPolicy(scope, plainName, pushPolicyParameters.getPolicyType(), pushPolicyParameters.getPdpGroup(), requestID));
		response.setResponseCode(responseCode);
		return response;
	}
	
	/*
	 * Delete a Policy using deletePolicyParameters
	 */
	public PolicyChangeResponse deletePolicy(DeletePolicyParameters parameters) throws Exception {
		return deletePolicy(parameters, userName, pass);
	}
	
	public PolicyChangeResponse deletePolicy(DeletePolicyParameters parameters, String userID,String passcode) throws Exception {
		StdPolicyChangeResponse response = new StdPolicyChangeResponse();
		String resource= "deletePolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseMessage(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseCode(401);
			return response;
		}
		if (parameters.getPolicyComponent()!=null) {
			if (parameters.getPolicyComponent().equalsIgnoreCase("PAP")) {
				response.setResponseMessage(deletePolicyFromPAP(parameters));
			} else if (parameters.getPolicyComponent().equalsIgnoreCase("PDP")) {
				response.setResponseMessage(deletePolicyFromPDP(parameters));
			} else {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Component does not exist.");
				response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Component does not exist. Please enter either PAP or PDP to delete the policy from a specified Policy Component.");
			}
		} else {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Component given.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Component given.");
		}

		response.setResponseCode(responseCode);
		return response;
	}
	
	/*
	 * createDictionaryItem using dictionaryParameters.
	 */
	public PolicyChangeResponse createDictionaryItem(DictionaryParameters parameters) throws Exception{
		StdPolicyChangeResponse response = new StdPolicyChangeResponse();
		
		if(parameters.getDictionaryType()!=null || parameters.getDictionaryType().equals("")){
			if(parameters.getDictionary()!=null || parameters.getDictionary().equals("")){
				if(parameters.getDictionaryFields()!=null){
					logger.info("Parameters are good... start create dictionary item API...");
					
					Map<String,String> dictionaryFields = parameters.getDictionaryFields().get(AttributeType.DICTIONARY);
					
					StdPAPPolicy newDictionaryItem = new StdPAPPolicy(parameters.getDictionaryType().toString(), parameters.getDictionary(), dictionaryFields);

					String result = (String) callPAP(newDictionaryItem, new String[] {"operation=createDictionary", "apiflag=dictionaryApi"}, parameters.getRequestID(), "dictionaryItem");
					
					response.setResponseCode(responseCode);
					response.setResponseMessage(result);

				}else{
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary Fields given.");
					response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary Fields given.");
				}
			}else{
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary given.");
				response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary given.");
			}
		}else{
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary Type given.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary Type given.");
		}
		try{
			
		}catch(Exception e){

		}
		
		return response;
	}
	
	/*
	 * createPolicy Using policyParameters. 
	 */
	public PolicyChangeResponse createPolicy(PolicyParameters policyParameters) throws Exception{
		return createPolicy(policyParameters, userName, pass);
	}
	
	public PolicyChangeResponse createPolicy(PolicyParameters policyParameters, String userID, String passcode) throws Exception{
		StdPolicyChangeResponse response = new StdPolicyChangeResponse();
		String resource= "createPolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseMessage(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseCode(401);
			return response;
		}
		String plainName = null;
		String scope = null;
		String date = "NA";
		if (policyParameters.getTtlDate()!=null){
			date = ConvertDate(policyParameters.getTtlDate());
		}
		try{
			if(policyParameters.getPolicyName()!=null){
				plainName = policyParameters.getPolicyName().substring(policyParameters.getPolicyName().lastIndexOf(".")+1, policyParameters.getPolicyName().length());
				scope = policyParameters.getPolicyName().substring(0, policyParameters.getPolicyName().lastIndexOf("."));
				logger.info("Name is "+ plainName +"   scope is "+ scope);
			}
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
		}
		if(policyParameters.getPolicyConfigType()!=null){
			// This is Config Class Policy. 
			// Firewall
			if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.Firewall)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						json = stringToJsonObject(policyParameters.getConfigBody());
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						throw new Exception(message);
					}
					response.setResponseMessage(createConfigFirewallPolicy(plainName, json, scope, policyParameters.getRequestID(), userID, passcode,
							policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body given.";
					logger.error(message);
					response.setResponseMessage(message);
				}
			}
			//Base
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.Base)){
				if(policyParameters.getConfigBody()!=null) {
					if(policyParameters.getConfigBodyType()!=null){
						response.setResponseMessage(createConfigPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getConfigName(),
								policyParameters.getAttributes().get(AttributeType.MATCHING), policyParameters.getConfigBodyType().toString(), policyParameters.getConfigBody(), scope, policyParameters.getRequestID(), userID, passcode,
								policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
					} else {
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body Type given.";
						logger.error(message);
						response.setResponseMessage(message);
					}
				} else {
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body given.";
					logger.error(message);
					response.setResponseMessage(message);
				}
			}
			//BRMS Raw
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.BRMS_RAW)){
				if(policyParameters.getConfigBody()!=null){
					
					/*public String createUpdateBRMSRawPolicy(String policyName, String policyDescription, Map<String,String> dyanamicFieldConfigAttributes,
					 * String brmsRawBody, String policyScope, Boolean isEdit, UUID requestID)*/
					response.setResponseMessage(createUpdateBRMSRawPolicy(plainName, policyParameters.getPolicyDescription(),policyParameters.getAttributes(),
							policyParameters.getConfigBody(),scope, false, 
							policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), 
							date));
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Config Body Present";
					logger.error(message);
					throw new Exception(message);
				}
			}
			//BRMS Param
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.BRMS_PARAM)){
				if(policyParameters.getConfigBody()!=null){
			
					response.setResponseMessage(createUpdateBRMSParamPolicy(plainName, policyParameters.getPolicyDescription(),policyParameters.getAttributes(),
							policyParameters.getConfigBody(),scope, false, 
							policyParameters.getRequestID(),policyParameters.getAttributes(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), 
							String.valueOf(policyParameters.getGuard()), date));
				}else{
					response.setResponseMessage(createUpdateBRMSParamPolicy(plainName, policyParameters.getPolicyDescription(),policyParameters.getAttributes(),
							null,scope, false, 
							policyParameters.getRequestID(),policyParameters.getAttributes(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), 
							String.valueOf(policyParameters.getGuard()), date));
				}
			}
			// Micro Services Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.MicroService)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						json = stringToJsonObject(policyParameters.getConfigBody());
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						throw new Exception(message);
					}
					//call Micro Services Create API here
					response.setResponseMessage(createUpdateMicroServicesPolicy(plainName, json, policyParameters.getEcompName(),
													scope, false, policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), 
													String.valueOf(policyParameters.getGuard()), date));
					
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Micro Service or Attributes Config Body Present";
					logger.error(message);
					throw new Exception(message);
				}
			}
			// ClosedLoop_Fault Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.ClosedLoop_Fault)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						if(validateNONASCIICharactersAndAllowSpaces(policyParameters.getConfigBody())){
							json = stringToJsonObject(policyParameters.getConfigBody());
						} else {
							String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "The ClosedLoop JSON Contains Non ASCII Characters.";
							logger.error(message);
							response.setResponseCode(400);
							response.setResponseMessage(message);
							return response;
						}
						
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						response.setResponseCode(400);
						response.setResponseMessage(message);
						return response;
						
					}
					//call ClosedLoop_Fault Create API here
					response.setResponseMessage(createUpdateClosedLoopPolicy(plainName, json, policyParameters.getPolicyDescription(),
													scope, false, policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), 
													String.valueOf(policyParameters.getGuard()), date));
					
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Config Body Present";
					logger.error(message);
					response.setResponseMessage(message);
					response.setResponseCode(400);
					return response;
				}
			}
			// ClosedLoop_PM Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.ClosedLoop_PM)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						if(validateNONASCIICharactersAndAllowSpaces(policyParameters.getConfigBody())){
							json = stringToJsonObject(policyParameters.getConfigBody());
						} else {
							String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "The ClosedLoop PM JSON Contains Non ASCII Characters.";
							logger.error(message);
							response.setResponseMessage(message);
							response.setResponseCode(400);
							return response;
							
						}
						
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						response.setResponseMessage(message);
						response.setResponseCode(400);
						return response;
						
					}
					//call ClosedLoop_Fault Create API here
					response.setResponseMessage(createUpdateClosedLoopPmPolicy(plainName, json, policyParameters.getPolicyDescription(),
													scope, false, policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), 
													String.valueOf(policyParameters.getGuard()), date));
					
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Config Body Present";
					logger.error(message);
					response.setResponseMessage(message);
					response.setResponseCode(400);
					return response;
					
				}
			}
			
		} else if (policyParameters.getPolicyClass()!=null){ 
			if(policyParameters.getPolicyClass().equals(PolicyClass.Action)){
				// call Action Create API here. 
				response.setResponseMessage(createUpdateActionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getAttributes().get(AttributeType.MATCHING), 
						policyParameters.getDynamicRuleAlgorithmLabels(), policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
						policyParameters.getActionPerformer(), policyParameters.getActionAttribute(), scope, false, policyParameters.getRequestID()));
			}else if(policyParameters.getPolicyClass().equals(PolicyClass.Decision)){
				// Call Decision Create API here. 
				if (policyParameters.getAttributes()!=null && policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)) {
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							policyParameters.getAttributes().get(AttributeType.MATCHING), policyParameters.getAttributes().get(AttributeType.SETTINGS), policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, false, policyParameters.getRequestID()));
				}else if(policyParameters.getAttributes()!=null && !policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)){
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							null, policyParameters.getAttributes().get(AttributeType.SETTINGS), policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, false, policyParameters.getRequestID()));
				}else if(policyParameters.getAttributes()!=null && policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && !policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)){
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							policyParameters.getAttributes().get(AttributeType.MATCHING), null, policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, false, policyParameters.getRequestID()));
				}else{
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							null, null, policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, false, policyParameters.getRequestID()));
				}
			}
		} else {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Class found.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Class found.");
		}
		response.setResponseCode(responseCode);
		return response;
	}
	
	/*
	 * updatePolicy using policyParameters. 
	 */
	public PolicyChangeResponse updatePolicy(PolicyParameters policyParameters) throws Exception{
		return updatePolicy(policyParameters, userName, pass);
	}
	
	public PolicyChangeResponse updatePolicy(PolicyParameters policyParameters,String userID, String passcode) throws Exception{
		StdPolicyChangeResponse response = new StdPolicyChangeResponse();
		String resource= "updatePolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseMessage(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseCode(401);
			return response;
		}
		String plainName = null;
		String scope = null;
		String date = "NA";
		if (policyParameters.getTtlDate()!=null){
			date = ConvertDate(policyParameters.getTtlDate());
		}
		try{
			if(policyParameters.getPolicyName()!=null){
				plainName = policyParameters.getPolicyName().substring(policyParameters.getPolicyName().lastIndexOf(".")+1, policyParameters.getPolicyName().length());
				scope = policyParameters.getPolicyName().substring(0, policyParameters.getPolicyName().lastIndexOf("."));
				logger.info("Name is "+ plainName +"   scope is "+ scope);
			}
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
		}
		if(policyParameters.getPolicyConfigType()!=null){
			// This is Config Class Policy.
			//Firewall
			if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.Firewall)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						json = stringToJsonObject(policyParameters.getConfigBody());
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						throw new Exception(message);
					}
					response.setResponseMessage(updateConfigFirewallPolicy(plainName, json, scope, policyParameters.getRequestID(), userID, passcode,policyParameters.getRiskLevel(), 
							policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body given.";
					logger.error(message);
					response.setResponseMessage(message);
				}
			}
			//Base Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.Base)){
				if(policyParameters.getConfigBody()!=null) {
					if(policyParameters.getConfigBodyType()!=null){
						response.setResponseMessage(updateConfigPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getConfigName(),
								policyParameters.getAttributes().get(AttributeType.MATCHING), policyParameters.getConfigBodyType().toString(), policyParameters.getConfigBody(), scope, 
								policyParameters.getRequestID(), userID, passcode, policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
					} else {
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body Type given.";
						logger.error(message);
						response.setResponseMessage(message);
					}
				} else {
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body given.";
					logger.error(message);
					response.setResponseMessage(message);
				}
			}
			//BRMS Raw
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.BRMS_RAW)){
				if(policyParameters.getConfigBody()!=null){
					/*public String createUpdateBRMSRawPolicy(String policyName, String policyDescription, Map<String,String> dyanamicFieldConfigAttributes,
					 * String brmsRawBody, String policyScope, Boolean isEdit, UUID requestID)*/
					response.setResponseMessage(createUpdateBRMSRawPolicy(plainName, policyParameters.getPolicyDescription(),policyParameters.getAttributes(),
							policyParameters.getConfigBody(),scope, true, 
							policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Config Body Present";
					logger.error(message);
					throw new Exception(message);
				}
			}
			//BRMS Param
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.BRMS_PARAM)){
				if(policyParameters.getConfigBody()!=null){
					
				/*	public String createUpdateBRMSParamPolicy(String policyName, String policyDescription, Map<AttributeType, Map<String, String>>  dyanamicFieldConfigAttributes,
							String brmsRawBody, String policyScope, Boolean isEdit, 
							UUID requestID,Map<String,String> drlRuleAndUIParams)*/
					response.setResponseMessage(createUpdateBRMSParamPolicy(plainName, policyParameters.getPolicyDescription(),policyParameters.getAttributes(),
							policyParameters.getConfigBody(),scope, true, 
							policyParameters.getRequestID(),policyParameters.getAttributes(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
				}else{
					response.setResponseMessage(createUpdateBRMSParamPolicy(plainName, policyParameters.getPolicyDescription(),policyParameters.getAttributes(),
							null,scope, true, 
							policyParameters.getRequestID(),policyParameters.getAttributes(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
				}
			}
			// Micro Services Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.MicroService)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						json = stringToJsonObject(policyParameters.getConfigBody());
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						throw new Exception(message);
					}
					//call Micro Services Create API here
					response.setResponseMessage(createUpdateMicroServicesPolicy(plainName, json, policyParameters.getEcompName(),
							scope, true, policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
					
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Micro Service or Attributes Config Body Present";
					logger.error(message);
					throw new Exception(message);
				}
			}
			// ClosedLoop_Fault Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.ClosedLoop_Fault)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						if(validateNONASCIICharactersAndAllowSpaces(policyParameters.getConfigBody())){
							json = stringToJsonObject(policyParameters.getConfigBody());
						} else {
							String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "The ClosedLoop JSON Contains Non ASCII Characters.";
							logger.error(message);
							response.setResponseMessage(message);
							return response;
						}
						
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						response.setResponseMessage(message);
						return response;
					}
					//call ClosedLoop_Fault Create API here
					response.setResponseMessage(createUpdateClosedLoopPolicy(plainName, json, policyParameters.getPolicyDescription(),
													scope, true, policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date));
					
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Config Body Present";
					logger.error(message);
					response.setResponseMessage(message);
				}
			}
			// ClosedLoop_PM Policy
			else if(policyParameters.getPolicyConfigType().equals(PolicyConfigType.ClosedLoop_PM)){
				if(policyParameters.getConfigBody()!=null){
					JsonObject json = null;
					try{
						if(validateNONASCIICharactersAndAllowSpaces(policyParameters.getConfigBody())){
							json = stringToJsonObject(policyParameters.getConfigBody());
						} else {
							String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "The ClosedLoop PM JSON Contains Non ASCII Characters.";
							logger.error(message);
							response.setResponseMessage(message);
							return response;
						}
						
					}catch(Exception e){
						String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
						logger.error(message);
						response.setResponseMessage(message);
						return response;
					}
					//call ClosedLoop_Fault Create API here
					response.setResponseMessage(createUpdateClosedLoopPmPolicy(plainName, json, policyParameters.getPolicyDescription(),
													scope, true, policyParameters.getRequestID(),policyParameters.getRiskLevel(), policyParameters.getRiskType(), 
													String.valueOf(policyParameters.getGuard()), date));
					
				}else{
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Config Body Present";
					logger.error(message);
					response.setResponseMessage(message);
				}
			}
			
		}else{
			
			if(policyParameters.getPolicyClass().equals(PolicyClass.Action)){
				// call Action Update API here. 
				response.setResponseMessage(createUpdateActionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getAttributes().get(AttributeType.MATCHING), 
						policyParameters.getDynamicRuleAlgorithmLabels(), policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
						policyParameters.getActionPerformer(), policyParameters.getActionAttribute(), scope, true, policyParameters.getRequestID()));
			
			}else if(policyParameters.getPolicyClass().equals(PolicyClass.Decision)){
				// Call Decision Create API here. 
				if (policyParameters.getAttributes()!=null && policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)) {
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							policyParameters.getAttributes().get(AttributeType.MATCHING), policyParameters.getAttributes().get(AttributeType.SETTINGS), policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, true, policyParameters.getRequestID()));
				}else if(policyParameters.getAttributes()!=null && !policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)){
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							null, policyParameters.getAttributes().get(AttributeType.SETTINGS), policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, true, policyParameters.getRequestID()));
				}else if(policyParameters.getAttributes()!=null && policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && !policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)){
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							policyParameters.getAttributes().get(AttributeType.MATCHING), null, policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, true, policyParameters.getRequestID()));
				}else{
					response.setResponseMessage(createUpdateDecisionPolicy(plainName, policyParameters.getPolicyDescription(), policyParameters.getEcompName(), policyParameters.getRuleProvider(),
							null, null, policyParameters.getDynamicRuleAlgorithmLabels(), 
							policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField2(), 
							scope, true, policyParameters.getRequestID()));
				}
			}
		}
		response.setResponseCode(responseCode);
		return response;
	}
	
	public DecisionResponse policyDecide(String eCOMPComponentName,
			Map<String, String> decisionAttributes, UUID requestID, String userID, String passcode)
			throws PolicyDecisionException {
		String resource= "getDecision";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyDecisionException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		DecisionResponse policyDecision;
		if (eCOMPComponentName == null || eCOMPComponentName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given : " + eCOMPComponentName);
			throw new PolicyDecisionException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given.");
		}
		if (decisionAttributes != null && !decisionAttributes.isEmpty()) {
			JsonArrayBuilder resourceArray = Json.createArrayBuilder();
			for (String key : decisionAttributes.keySet()) {
				if (key.isEmpty()) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an Empty Key");
					throw new PolicyDecisionException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an empty Key");
				}
				JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
				if (decisionAttributes.get(key).matches("[0-9]+")) {
					int val = Integer.parseInt(decisionAttributes.get(key));
					resourceBuilder.add("Value", val);
				} else {
					resourceBuilder.add("Value", decisionAttributes.get(key));
				}
				resourceBuilder.add("AttributeId", key);
				resourceArray.add(resourceBuilder);
			}
			JsonObject model = Json
					.createObjectBuilder()
					.add("Request",
							Json.createObjectBuilder()
									.add("AccessSubject",
											Json.createObjectBuilder()
													.add("Attribute",
															Json.createObjectBuilder()
																	.add("Value",
																			eCOMPComponentName)
																	.add("AttributeId",
																			"ECOMPName")))
									.add("Resource",
											Json.createObjectBuilder().add(
													"Attribute", resourceArray))
									.add("Action",
											Json.createObjectBuilder()
													.add("Attribute",
															Json.createObjectBuilder()
																	.add("Value",
																			"DECIDE")
																	.add("AttributeId",
																			"urn:oasis:names:tc:xacml:1.0:action:action-id"))))
					.build();
			try {
				decide = true;
				policyDecision = decisionResult(generateRequest(model
						.toString(), requestID));
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
				decide = false;
				throw new PolicyDecisionException(e);
			}
		} else {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Attributes Given. ");
			throw new PolicyDecisionException(XACMLErrorConstants.ERROR_DATA_ISSUE +"No DecisionAttributes Given.");
		}
		decide = false;
		return policyDecision;
	}

	public Collection<PolicyConfig> configPolicyName(String policyName, UUID requestID, String userID, String passcode)
			throws PolicyConfigException {
		String resource= "getConfigByPolicyName";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyConfig> policyConfig = null;
		if (policyName == null || policyName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Policy FileName specified!! : " + policyName);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE+"No Policy FileName specified!!");
		}
		if(policyName!= null && !policyName.trim().equals("") && !policyName.endsWith("xml")){
			policyName = policyName + ".[\\d].*";
		}
		JsonObject model = Json
				.createObjectBuilder()
				.add("Request",
						Json.createObjectBuilder()
								.add("AccessSubject",
										Json.createObjectBuilder().add(
												"Attribute",
												Json.createObjectBuilder()
														.add("Value",
																policyName)
														.add("AttributeId",
																"PolicyName")))
								.add("Action",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"ACCESS")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:action:action-id")))
								.add("Resource",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"Config")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:resource:resource-id"))))
				.build();
		try {
			policyConfig = configResult(generateRequest(model.toString(), requestID));
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
		}
		return policyConfig;
	}

	public Collection<PolicyConfig> config(String eCOMPComponentName, UUID requestID, String userID, String passcode)
			throws PolicyConfigException {
		String resource= "getConfig";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyConfig> policyConfig = null;
		if (eCOMPComponentName == null || eCOMPComponentName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given : " + eCOMPComponentName);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given.");
		}
		JsonObject model = Json
				.createObjectBuilder()
				.add("Request",
						Json.createObjectBuilder()
								.add("AccessSubject",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		eCOMPComponentName)
																.add("AttributeId",
																		"ECOMPName")))
								.add("Action",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"ACCESS")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:action:action-id")))
								.add("Resource",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"Config")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:resource:resource-id"))))
				.build();
		try {
			policyConfig = configResult(generateRequest(model.toString(), requestID));
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
		}
		return policyConfig;
	}

	public Collection<PolicyConfig> config(String eCOMPComponentName,
			String configName, UUID requestID, String userID, String passcode) throws PolicyConfigException {
		String resource= "getConfig";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyConfig> policyConfig = null;
		if (eCOMPComponentName == null || eCOMPComponentName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given : " + eCOMPComponentName);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given.");
		}
		if (configName == null || configName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No configName given : " + configName);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +"No configName given.");
		}
		JsonObject model = Json
				.createObjectBuilder()
				.add("Request",
						Json.createObjectBuilder()
								.add("AccessSubject",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createArrayBuilder()
																.add(Json
																		.createObjectBuilder()
																		.add("Value",
																				eCOMPComponentName)
																		.add("AttributeId",
																				"ECOMPName"))
																.add(Json
																		.createObjectBuilder()
																		.add("Value",
																				configName)
																		.add("AttributeId",
																				"ConfigName"))))
								.add("Action",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"ACCESS")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:action:action-id")))
								.add("Resource",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"Config")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:resource:resource-id"))))
				.build();
		try {
			policyConfig = configResult(generateRequest(model.toString(), requestID));
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
		}

		return policyConfig;
	}

	public Collection<PolicyConfig> config(String eCOMPComponentName,
			String configName, Map<String, String> configAttributes, UUID requestID, String userID, String passcode)
			throws PolicyConfigException {
		String resource= "getConfig";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyConfig> policyConfig = null;
		if (eCOMPComponentName == null || eCOMPComponentName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given : " + eCOMPComponentName);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName given.");
		}
		if (configName == null || configName.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No configName given : " + configName);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +"No configName given.");
		}
		if (configAttributes != null && !configAttributes.isEmpty()) {
			if(!configAttributes.containsKey("RiskType")){
				configAttributes.put("RiskType", ".*");
			}
			if(!configAttributes.containsKey("RiskLevel")){
				configAttributes.put("RiskLevel", ".*");
			}
			if(!configAttributes.containsKey("guard")){
				configAttributes.put("guard", ".*");
			}
			if(!configAttributes.containsKey("TTLDate")){
				configAttributes.put("TTLDate", ".*");
			}
		}else{
			// ConfigAttributes is Null. So add basic values. 
			configAttributes = new HashMap<String,String>();
			configAttributes.put("RiskType", ".*");
			configAttributes.put("RiskLevel", ".*");
			configAttributes.put("guard", ".*");
			configAttributes.put("TTLDate", ".*");
		}
		JsonArrayBuilder resourceArray = Json.createArrayBuilder();
		for (String key : configAttributes.keySet()) {
			if (key.isEmpty()) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an empty Key");
				throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +"Cannot have an empty Key");
			}
			JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
			/*if (configAttributes.get(key).matches("[0-9]+")) {
				int val = Integer.parseInt(configAttributes.get(key));
				resourceBuilder.add("Value", val);
			} else {*/
			resourceBuilder.add("Value", configAttributes.get(key));
			resourceBuilder.add("AttributeId", key);
			resourceArray.add(resourceBuilder);
		}
		JsonObject model = Json
			.createObjectBuilder()
			.add("Request",
					Json.createObjectBuilder()						
							.add("AccessSubject",
									Json.createObjectBuilder()
												.add("Attribute",
													Json.createArrayBuilder()
															.add(Json
																	.createObjectBuilder()
																	.add("Value",
																			eCOMPComponentName)
																	.add("AttributeId",
																			"ECOMPName"))
															.add(Json
																	.createObjectBuilder()
																	.add("Value",
																			configName)
																	.add("AttributeId",
																			"ConfigName"))))
							.add("Action",
									Json.createObjectBuilder()
												.add("Attribute",
													Json.createObjectBuilder()
															.add("Value",
																		"ACCESS")
															.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:action:action-id")))
							.add("Resource",
									Json.createObjectBuilder()
												.add("Attribute",
														resourceArray
																.add(Json.createObjectBuilder()
																		.add("Value",
																				"Config")
																		.add("AttributeId",
																				"urn:oasis:names:tc:xacml:1.0:resource:resource-id")))))
				.build();
		try {
			policyConfig = configResult(generateRequest(model.toString(), requestID));
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
		}
		return policyConfig;
	}
	
	public Collection<PolicyConfig> configRequest(ConfigRequestParameters configRequestParameters, String userID, String passcode) throws PolicyConfigException{
		String resource= "getConfig";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyConfig> policyConfig = null;
		unique = false;
		if(configRequestParameters==null){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No config Request Parameters given ");
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No config Request Parameters given.");
		}
		if(configRequestParameters.getEcompName() == null && configRequestParameters.getPolicyName() == null){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot proceed without eCOMPComponentName or PolicyName");
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No eCOMPComponentName or PolicyName given.");
		}
		String policyName = configRequestParameters.getPolicyName();
		if(policyName!= null && !policyName.trim().equals("") && !policyName.endsWith("xml")){
			policyName = policyName + ".[\\d].*";
		}
		JsonArrayBuilder subjectArray = Json.createArrayBuilder();
		JsonArrayBuilder resourceArray = Json.createArrayBuilder();
		if(configRequestParameters.getPolicyName()!=null){
			JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
			subjectBuilder.add("Value", policyName);
			subjectBuilder.add("AttributeId", "PolicyName");
			subjectArray.add(subjectBuilder);
		}else{
			logger.info("PolicyName values are not given. ");
		}
		if(configRequestParameters.getEcompName()!=null){
			JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
			subjectBuilder.add("Value", configRequestParameters.getEcompName());
			subjectBuilder.add("AttributeId", "ECOMPName");
			subjectArray.add(subjectBuilder);
			if(configRequestParameters.getConfigName()!=null){
				subjectBuilder = Json.createObjectBuilder();
				subjectBuilder.add("Value", configRequestParameters.getConfigName());
				subjectBuilder.add("AttributeId", "ConfigName");
				subjectArray.add(subjectBuilder);
				Map<String,String> configAttributes = configRequestParameters.getConfigAttributes();
				if (configAttributes != null && !configAttributes.isEmpty()) {
					if(!configAttributes.containsKey("RiskType")){
						configAttributes.put("RiskType", ".*");
					}
					if(!configAttributes.containsKey("RiskLevel")){
						configAttributes.put("RiskLevel", ".*");
					}
					if(!configAttributes.containsKey("guard")){
						configAttributes.put("guard", ".*");
					}
					if(!configAttributes.containsKey("TTLDate")){
						configAttributes.put("TTLDate", ".*");
					}
				}else{
					// ConfigAttributes is Null. So add basic values. 
					configAttributes = new HashMap<String,String>();
					configAttributes.put("RiskType", ".*");
					configAttributes.put("RiskLevel", ".*");
					configAttributes.put("guard", ".*");
					configAttributes.put("TTLDate", ".*");
				}
				for (String key : configAttributes.keySet()) {
					if (key.isEmpty()) {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an empty Key");
						throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +"Cannot have an empty Key");
					}
					JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
					/*if (configAttributes.get(key).matches("[0-9]+")) {
						int val = Integer.parseInt(configAttributes.get(key));
						resourceBuilder.add("Value", val);
					} else {*/
					resourceBuilder.add("Value", configAttributes.get(key));
					resourceBuilder.add("AttributeId", key);
					resourceArray.add(resourceBuilder);
				}
			}else{
				logger.info("Config Name is not given. ");
			}
		}else{
			logger.info("Ecomp Name is not given. ");
		}
		JsonObject model = Json
				.createObjectBuilder()
				.add("Request",
						Json.createObjectBuilder()
								.add("AccessSubject",
										Json.createObjectBuilder()
												.add("Attribute",subjectArray))
								.add("Action",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"ACCESS")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:action:action-id")))
								.add("Resource",
										Json.createObjectBuilder()
												.add("Attribute",
														resourceArray
																.add(Json
																		.createObjectBuilder()
																		.add("Value",
																				"Config")
																		.add("AttributeId",
																				"urn:oasis:names:tc:xacml:1.0:resource:resource-id")))))
				.build();
		logger.debug("Generated JSON Request is: " + model.toString());
		if(configRequestParameters.getUnique()){
			logger.info("Requested for Unique Result only. ");
			unique = true;
		}
		try {
			policyConfig = configResult(generateRequest(model.toString(), configRequestParameters.getRequestID()));
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
		}
		return policyConfig;
	}
	
	public Collection<String> listConfigRequest(ConfigRequestParameters listRequestParameters, String userID, String passcode) throws PolicyConfigException{
		String resource= "listConfig";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyConfig> policyConfig = null;
		Collection<String> policyList = new ArrayList<String>();
		
		unique = false;
		if(listRequestParameters==null){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Request Parameters given ");
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Request Parameters given.");
		}
		
		if (junit){
			policyList.add("Policy Name: listConfigTest");
			return policyList;
		}

		String policyName = listRequestParameters.getPolicyName();
		if(policyName!= null && !policyName.trim().equals("") && !policyName.endsWith("xml")){
			policyName = policyName + ".[\\d].*";
		}
		JsonArrayBuilder subjectArray = Json.createArrayBuilder();
		JsonArrayBuilder resourceArray = Json.createArrayBuilder();
		if(listRequestParameters.getPolicyName()!=null){
			JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
			subjectBuilder.add("Value", policyName);
			subjectBuilder.add("AttributeId", "PolicyName");
			subjectArray.add(subjectBuilder);
		}else{
			logger.info("PolicyName values are not given. ");
		}
		if(listRequestParameters.getEcompName()!=null){
			JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
			subjectBuilder.add("Value", listRequestParameters.getEcompName());
			subjectBuilder.add("AttributeId", "ECOMPName");
			subjectArray.add(subjectBuilder);
			if(listRequestParameters.getConfigName()!=null){
				subjectBuilder = Json.createObjectBuilder();
				subjectBuilder.add("Value", listRequestParameters.getConfigName());
				subjectBuilder.add("AttributeId", "ConfigName");
				subjectArray.add(subjectBuilder);
				Map<String,String> configAttributes = listRequestParameters.getConfigAttributes();
				if (configAttributes != null && !configAttributes.isEmpty()) {
					if(!configAttributes.containsKey("RiskType")){
						configAttributes.put("RiskType", ".*");
					}
					if(!configAttributes.containsKey("RiskLevel")){
						configAttributes.put("RiskLevel", ".*");
					}
					if(!configAttributes.containsKey("guard")){
						configAttributes.put("guard", ".*");
					}
					if(!configAttributes.containsKey("TTLDate")){
						configAttributes.put("TTLDate", ".*");
					}
				}else{
					// ConfigAttributes is Null. So add basic values. 
					configAttributes = new HashMap<String,String>();
					configAttributes.put("RiskType", ".*");
					configAttributes.put("RiskLevel", ".*");
					configAttributes.put("guard", ".*");
					configAttributes.put("TTLDate", ".*");
				}
				for (String key : configAttributes.keySet()) {
					if (key.isEmpty()) {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an empty Key");
						throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +"Cannot have an empty Key");
					}
					JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
					/*if (configAttributes.get(key).matches("[0-9]+")) {
						int val = Integer.parseInt(configAttributes.get(key));
						resourceBuilder.add("Value", val);
					} else {*/
					resourceBuilder.add("Value", configAttributes.get(key));
					resourceBuilder.add("AttributeId", key);
					resourceArray.add(resourceBuilder);
				}
			}else{
				logger.info("Config Name is not given. ");
			}
		}else{
			logger.info("Ecomp Name is not given. ");
		}
		JsonObject model = Json
				.createObjectBuilder()
				.add("Request",
						Json.createObjectBuilder()
								.add("AccessSubject",
										Json.createObjectBuilder()
												.add("Attribute",subjectArray))
								.add("Action",
										Json.createObjectBuilder()
												.add("Attribute",
														Json.createObjectBuilder()
																.add("Value",
																		"ACCESS")
																.add("AttributeId",
																		"urn:oasis:names:tc:xacml:1.0:action:action-id")))
								.add("Resource",
										Json.createObjectBuilder()
												.add("Attribute",
														resourceArray
																.add(Json
																		.createObjectBuilder()
																		.add("Value",
																				"Config")
																		.add("AttributeId",
																				"urn:oasis:names:tc:xacml:1.0:resource:resource-id")))))
				.build();
		logger.debug("Generated JSON Request is: " + model.toString());
		if(listRequestParameters.getUnique()){
			logger.info("Requested for Unique Result only. ");
			unique = true;
		}
		try {
			policyConfig = configResult(generateRequest(model.toString(), listRequestParameters.getRequestID()));
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
		}
		for(PolicyConfig policy : policyConfig){
			if(policy.getPolicyConfigMessage()!=null && policy.getPolicyConfigMessage().contains("PE300")){
				policyList.add(policy.getPolicyConfigMessage());
			} else {
				policyList.add("Policy Name: " + policy.getPolicyName());
			}
		}
		return policyList;
	}
	


	public Collection<PolicyResponse> event(Map<String, String> eventAttributes, UUID requestID, String userID, String passcode)
			throws PolicyEventException {
		String resource= "sendEvent";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			throw new PolicyEventException(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		Collection<PolicyResponse> policyResponse = null;
		if (eventAttributes != null && !eventAttributes.isEmpty()) {
			JsonArrayBuilder resourceArray = Json.createArrayBuilder();
			for (String key : eventAttributes.keySet()) {
				if (key.isEmpty()) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an Empty Key");
					throw new PolicyEventException(XACMLErrorConstants.ERROR_DATA_ISSUE +"Cannot have an empty Key");
				}
				JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
				if (eventAttributes.get(key).matches("[0-9]+")) {
					int val = Integer.parseInt(eventAttributes.get(key));
					resourceBuilder.add("Value", val);
				} else {
					resourceBuilder.add("Value", eventAttributes.get(key));
				}
				resourceBuilder.add("AttributeId", key);
				resourceArray.add(resourceBuilder);
			}
			JsonObject model = Json
					.createObjectBuilder()
					.add("Request",
							Json.createObjectBuilder().add(
									"Resource",
									Json.createObjectBuilder().add("Attribute",
											resourceArray))).build();
			// Removed Part can be Useful in Future.
			/*
			 * .add("AccessSubject",Json.createObjectBuilder() .add("Attribute",
			 * subjectArray)) .add("Action", Json.createObjectBuilder()
			 * .add("Attribute", actionArray))
			 */
			// System.out.println(model.toString());
			try {
				// StdPolicyResponse stdPolicyResponse =
				// generateRequest(model.toString());
				// stdPolicyResponse.setRequestAttributes(eventAttributes);
				policyResponse = eventResult(generateRequest(model.toString(), requestID),
						eventAttributes);
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
				throw new PolicyEventException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
			}
		} else {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No event Attributes Given. ");
			throw new PolicyEventException(XACMLErrorConstants.ERROR_DATA_ISSUE +"No EventAttributes Given.");
		}
		return policyResponse;
	}

	private Collection<StdStatus> generateRequest(String Json, UUID requestID) throws Exception {
		Collection<StdStatus> results = null;
		
		Response response = null;
		// Create Request. We need XACML API here.
		try {
			Request request = JSONRequest.load(Json);
			String jRequest = JSONRequest.toString(request);
				
			// Call the PDP
			logger.debug("--- Generating Request: ---\n" + jRequest );
			response = callPDP(new ByteArrayInputStream(jRequest.getBytes()), requestID);

		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
			StdStatus stdStatus = new StdStatus();
			results = new HashSet<StdStatus>();
			stdStatus.setStatus("Unable to Call PDP. Error with the URL",
					PolicyResponseStatus.NO_ACTION_REQUIRED,
					PolicyConfigStatus.CONFIG_NOT_FOUND);
			results.add(stdStatus);
			throw new Exception(e);
		}

		if(this.UEBThread){
			this.UEBThread = registerUEBThread.isAlive();
		}
		if (response != null) {
			results = checkResponse(response);
			// TODO Starting Auto Client Here.
			if (notificationType.get(0).equals("ueb") && !this.UEBThread){
				this.UEBClientThread = new AutoClientUEB(pdps.get(0), uebURLList); 
				this.registerUEBThread = new Thread(this.UEBClientThread);
				this.registerUEBThread.start();
				this.UEBThread = true;
				
			}else {
				if(AutoClientEnd.getURL()==null){
					AutoClientEnd.start(pdps.get(0));
				}else if(AutoClientEnd.getURL()!=pdps.get(0)){
					AutoClientEnd.stop();
					AutoClientEnd.start(pdps.get(0));
				}
			}
		} else {
			logger.debug("No Response Received from PDP");
			StdStatus stdStatus = new StdStatus();
			results = new HashSet<StdStatus>();
			stdStatus.setStatus("No Response Received",
					PolicyResponseStatus.NO_ACTION_REQUIRED,
					PolicyConfigStatus.CONFIG_NOT_FOUND);
			results.add(stdStatus);
		}

		return results;
	}
	
	private Response callPDP(ByteArrayInputStream input, UUID requestID) throws Exception {
		Response response = null;
		HttpURLConnection connection = null;
		responseCode = 0;
		// Checking for the available PDPs is done during the first Request and
		// the List is going to have the connected PDP as first element.
		// This makes it Real-Time to change the list depending on their
		// availability.
		if (pdps == null || pdps.isEmpty()) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDPs List is Empty.");
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"PDPs List is empty.");
		} else {
			int pdpsCount = 0;
			boolean connected = false;
			while (pdpsCount < pdps.size()) {
				input.reset();
				try {
					String urlValue = pdps.get(0);
					URL url = new URL(urlValue);
					logger.debug("--- Sending Request to PDP : "+ url.toString() + " ---");
					connection = (HttpURLConnection) url.openConnection();
					// Setting Content-Type
					connection.setRequestProperty("Content-Type","application/json");
					// Adding Authorization
					connection.setRequestProperty("Authorization", "Basic "	+ encoding.get(0));
					// Adding Environment. 
					connection.setRequestProperty("Environment", environment);
					// Adding RequestID
					if (requestID == null) {
						requestID = UUID.randomUUID();
						logger.info("No request ID provided, sending generated ID: " + requestID.toString());
					} else {
						logger.info("Using provided request ID: " + requestID.toString());
					}
					connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
					// Setting up connection method and headers.
					connection.setRequestMethod("POST");
					connection.setUseCaches(false);
					connection.setInstanceFollowRedirects(false);
					connection.setDoOutput(true);
					connection.setDoInput(true);
					OutputStream os = connection.getOutputStream();
					IOUtils.copy(input, os);
					
					
					connection.connect();
					responseCode = connection.getResponseCode();
					// If Connected to a PDP Then break from the loop and
					// continue with the Request.
					if (connection.getResponseCode() == 200 || junit) {
						connected = true;
						break;
					} else {
						logger.debug(XACMLErrorConstants.ERROR_PERMISSIONS+ "PDP Response Code : "	+ connection.getResponseCode());
						Collections.rotate(pdps, -1);
						Collections.rotate(encoding, -1);
					}
				} catch (Exception e) {
					// This means that the PDP is not working and needs to
					// Re-Order our List and Connect to the next one.
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PDP connection Error : " + e);
					Collections.rotate(pdps, -1);
					Collections.rotate(encoding, -1);
				}
				pdpsCount++;
			}
			if (connected) {
				// Read the Response
				// System.out.println("connected to PDP : " + pdps.get(0));
				logger.debug("connected to PDP : " + pdps.get(0));
				logger.debug("--- Response:  ---");
				Map<String,List<String>> headers = connection.getHeaderFields();
				for(String key : headers.keySet()){
					logger.debug("Header : " + key + "   Value: " + headers.get(key));
				}
				try {
					if (connection.getResponseCode() == 200 || junit) {
						// Read the Response
						ContentType contentType = null;
						try {
							contentType = ContentType.parse(connection
									.getContentType());
							if (contentType.getMimeType().equalsIgnoreCase(
									ContentType.APPLICATION_JSON.getMimeType())) {
								if(junit){
									response = JSONResponse.load(getJsonResponseString());
								} else {
									response = JSONResponse.load(connection.getInputStream());
								}
								logger.debug(response + "\n---");
							} else {
								logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Unknown Content-Type: "
										+ contentType);
								throw new Exception(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Unknown Content-Type: "
										+ contentType);
							}
						} catch (Exception e) {
							String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Parsing Content-Type: "
									+ connection.getContentType() + ", error="
									+ e;
							logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
							throw new Exception(message, e);
						}
					} else {
						throw new Exception(XACMLErrorConstants.ERROR_PERMISSIONS+ "ERROR response code of the URL " + pdps.get(0) + " is "
								+ connection.getResponseCode());
					}
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"Error in Connecting to the  PDP ", e);
				}
				return response;
			} else {
				if(junit){
					response = JSONResponse.load(getJsonResponseString());
					return response;
				}
				throw new Exception(XACMLErrorConstants.ERROR_PERMISSIONS+ "Unable to get valid Response from  PDP(s) " + pdps);
			}
		}
	}
	
	private Collection<StdStatus> checkResponse(Response response)
			throws Exception {

		String pdpConfigURL = null;

		Collection<StdStatus> combinedResult = new HashSet<StdStatus>();
		int priority = defaultPriority;
		Map<Integer, StdStatus> uniqueResult = new HashMap<Integer, StdStatus>();
		for (Result result : response.getResults()) {
			if (!result.getDecision().equals(Decision.PERMIT)) {
				logger.debug("Decision not a Permit. "	+ result.getDecision().toString());
				StdStatus stdStatus = new StdStatus();
				if (decide) {
					stdStatus.setDecision(PolicyDecision.DENY);
					for(Advice advice: result.getAssociatedAdvice()){
						for(AttributeAssignment attribute: advice.getAttributeAssignments()){
							stdStatus.setDetails(attribute.getAttributeValue().getValue().toString());
							break;
						}
					}
					combinedResult.add(stdStatus);
					return combinedResult;
				}
				stdStatus.setStatus(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Params passed: Decision not a Permit.",PolicyResponseStatus.NO_ACTION_REQUIRED,PolicyConfigStatus.CONFIG_NOT_FOUND);
				combinedResult.add(stdStatus);
				return combinedResult;
			} else {
				if (decide) {
					// check for Decision for decision based calls.
					StdStatus stdStatus = new StdStatus();
					stdStatus.setDecision(PolicyDecision.PERMIT);
					stdStatus.setDetails("Decision Permit. OK!");
					combinedResult.add(stdStatus);
					return combinedResult;
				}
				if (!result.getAssociatedAdvice().isEmpty()) {
					// @ TODO Add advice actions
					// Configurations should be in advice. + Also PDP took
					// actions could be here.
					for (Advice advice : result.getAssociatedAdvice()) {
						int config = 0, uri = 0;
						String configURL = null;
						String policyName = null;
						String policyVersion = null;
						Map<String, String> matchingConditions = new HashMap<String, String>();
						match = new Matches();
						Map<String, String> configAttributes = new HashMap<String, String>();
						Map<String, String> responseAttributes = new HashMap<String,String>();
						Map<String, String> actionTaken = new HashMap<String, String>();
						StdStatus stdStatus = new StdStatus();
						Map<String, String> adviseAttributes = new HashMap<String, String>();
						for (AttributeAssignment attribute : advice.getAttributeAssignments()) {
							adviseAttributes.put(attribute.getAttributeId().stringValue(), attribute.getAttributeValue().getValue().toString());
							if (attribute.getAttributeValue().getValue().toString().equalsIgnoreCase("CONFIGURATION")) {
								config++;
							} else if (attribute.getDataTypeId().stringValue().endsWith("anyURI")) {
								uri++;
								if (uri == 1) {
									configURL = attribute.getAttributeValue().getValue().toString();
									String currentUsedPDP = pdps.get(0);
									int pos = (pdps.get(0)).lastIndexOf("/");
									String configURLPath = currentUsedPDP.substring(0, pos);
									int pos1 = configURLPath.lastIndexOf("/");
									String pdpConfigURLPath = configURLPath.substring(0, pos1 + 1);
									pdpConfigURL = configURL.replace("$URL", pdpConfigURLPath);
								} else {
									if (!(attribute.getIssuer().equalsIgnoreCase("PDP"))) {
										throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error having multiple URI in the Policy");
									}
								}
							} else if (attribute.getAttributeId().stringValue()
									.equalsIgnoreCase("PolicyName")) {
								policyName = attribute.getAttributeValue()
										.getValue().toString();
							} else if (attribute.getAttributeId().stringValue()
									.equalsIgnoreCase("VersionNumber")) {
								policyVersion = attribute.getAttributeValue()
										.getValue().toString();
							} else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("Priority")){
								try{
									priority = Integer.parseInt(attribute.getAttributeValue().getValue().toString());
								} catch(Exception e){
									logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+ "Unable to Parse Integer for Priority. Setting to default value");
									priority = defaultPriority;
								}
							} else if (attribute.getAttributeId().stringValue()
									.startsWith("matching")) {
								matchingConditions.put(attribute
										.getAttributeId().stringValue()
										.replaceFirst("(matching).", ""),
										attribute.getAttributeValue()
												.getValue().toString());
								if (attribute.getAttributeId().stringValue()
										.replaceFirst("(matching).", "")
										.equals("ECOMPName")) {
									match.setEcompName(attribute
											.getAttributeValue().getValue()
											.toString());
								} else if (attribute.getAttributeId()
										.stringValue()
										.replaceFirst("(matching).", "")
										.equals("ConfigName")) {
									match.setConfigName(attribute
											.getAttributeValue().getValue()
											.toString());
								} else {
									configAttributes.put(attribute
											.getAttributeId().stringValue()
											.replaceFirst("(matching).", ""),
											attribute.getAttributeValue()
													.getValue().toString());
								}
							} else if (attribute.getAttributeId().stringValue().startsWith("key:")) {
								responseAttributes.put(attribute
										.getAttributeId().stringValue()
										.replaceFirst("(key).", ""),
										attribute.getAttributeValue()
												.getValue().toString());
							}
						}
						if (!configAttributes.isEmpty()) {
							match.setConfigAttributes(configAttributes);
						}
						if ((config == 1) && (uri == 1)) {
							// If there is a configuration.
							try {
								logger.debug("Configuration Call to : "
										+ configURL);
								stdStatus = ConfigCall(pdpConfigURL);
							} catch (Exception e) {
								logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+ e);
								stdStatus
										.setStatus(
												"Error in Calling the Configuration URL "
														+ e,
												PolicyResponseStatus.NO_ACTION_REQUIRED,
												PolicyConfigStatus.CONFIG_NOT_FOUND);
							}
							stdStatus.setPolicyName(policyName);
							stdStatus.setPolicyVersion(policyVersion);
							stdStatus.setMatchingConditions(matchingConditions);
							stdStatus.setResposneAttributes(responseAttributes);
							if(!unique){
								combinedResult.add(stdStatus);
							}else{
								if(!uniqueResult.isEmpty()){
									if(uniqueResult.containsKey(priority)){
										// Not any more unique, check the matching conditions size
										int oldSize = uniqueResult.get(priority).getMatchingConditions().size();
										int newSize = matchingConditions.size();
										if(oldSize < newSize){
											uniqueResult.put(priority, stdStatus);
										}else if(oldSize == newSize){
											stdStatus = new StdStatus();
											stdStatus.setStatus("Two/more Policies have Same Priority and matching conditions, Please correct your policies.", PolicyResponseStatus.NO_ACTION_REQUIRED,
													PolicyConfigStatus.CONFIG_NOT_FOUND);
											combinedResult.add(stdStatus);
											unique = false;
											return combinedResult;
										}
									}else{
										uniqueResult.put(priority, stdStatus);
									}
								}else{
									uniqueResult.put(priority, stdStatus);
								}
							}
						} else {
							// Else it is Action Taken.
							logger.info("Action Taken by PDP. ");
							actionTaken.putAll(adviseAttributes);
							stdStatus.setActionTaken(actionTaken);
							stdStatus.setPolicyResponseStatus(
									"Action Taken by the PDP",
									PolicyResponseStatus.ACTION_TAKEN);
							combinedResult.add(stdStatus);
						}
					}
				}
				if (!result.getObligations().isEmpty()) {
					// @ TODO add Obligation actions
					// Action advised should be in obligations.
					for (Obligation obligation : result.getObligations()) {
						Map<String, String> actionAdvised = new HashMap<String, String>();
						StdStatus stdStatus = new StdStatus();
						for (AttributeAssignment attribute : obligation
								.getAttributeAssignments()) {
							actionAdvised.put(attribute.getAttributeId()
									.stringValue(), attribute
									.getAttributeValue().getValue().toString());
						}
						stdStatus.setActionAdvised(actionAdvised);
						stdStatus.setPolicyResponseStatus(
								"Action has been Advised ",
								PolicyResponseStatus.ACTION_ADVISED);
						combinedResult.add(stdStatus);
					}
				}
			}
		}
		if(unique){
			// Select Unique policy. 
			int minNum = defaultPriority;
			for(int num: uniqueResult.keySet()){
				if(num < minNum){
					minNum = num;
				}
			}
			combinedResult.add(uniqueResult.get(minNum));
			// Turn off Unique
			unique = false;
		}
		
		return combinedResult;
	}

	private StdStatus ConfigCall(String stringURL) throws Exception {
		StdStatus stdStatus = new StdStatus();
		try {
			URL configURL = new URL(stringURL);
			URLConnection connection = null;
			try {
				connection = configURL.openConnection();
				if (stringURL.endsWith("json")) {
					stdStatus.setPolicyType(PolicyType.JSON);
					JsonReader jsonReader = Json.createReader(connection
							.getInputStream());
					stdStatus.setJsonObject(jsonReader.readObject());
					jsonReader.close();
					logger.info("config Retrieved ");
					stdStatus.setStatus("Config Retrieved from: " + configURL,
							PolicyResponseStatus.NO_ACTION_REQUIRED,
							PolicyConfigStatus.CONFIG_RETRIEVED);
					try {
						MatchStore.storeMatch(match);
					} catch (Exception e) {
						logger.info("StoreMatch failed for Ecomp:"
								+ match.getEcompName() + " Config: "
								+ match.getConfigName());
					}
					return stdStatus;
				} else if (stringURL.endsWith("xml")) {
					stdStatus.setPolicyType(PolicyType.XML);
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = null;
					try {
						db = dbf.newDocumentBuilder();
						Document config = db.parse(connection.getInputStream());
						stdStatus.setDocument(config);
					} catch (ParserConfigurationException e) {
						logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
						throw new Exception(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Unable to create Document Object",
								e);
					} catch (SAXException e) {
						logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID+ e);
						throw new Exception(XACMLErrorConstants.ERROR_SCHEMA_INVALID+ "Unable to parse the XML config", e);
					}
					logger.info("config Retrieved ");
					stdStatus.setStatus("Config Retrieved from: " + configURL,
							PolicyResponseStatus.NO_ACTION_REQUIRED,
							PolicyConfigStatus.CONFIG_RETRIEVED);
					try {
						MatchStore.storeMatch(match);
					} catch (Exception e) {
						logger.info("StoreMatch failed for Ecomp:"
								+ match.getEcompName() + " Config: "
								+ match.getConfigName());
					}
					return stdStatus;
				} else if (stringURL.endsWith("properties")) {
					stdStatus.setPolicyType(PolicyType.PROPERTIES);
					Properties configProp = new Properties();
					configProp.load(connection.getInputStream());
					stdStatus.setProperties(configProp);
					logger.info("config Retrieved ");
					stdStatus.setStatus("Config Retrieved from: " + configURL,
							PolicyResponseStatus.NO_ACTION_REQUIRED,
							PolicyConfigStatus.CONFIG_RETRIEVED);
					try {
						MatchStore.storeMatch(match);
					} catch (Exception e) {
						logger.info("StoreMatch failed for Ecomp:"
								+ match.getEcompName() + " Config: "
								+ match.getConfigName());
					}
					return stdStatus;
				} else if (stringURL.endsWith("txt")) {
					stdStatus.setPolicyType(PolicyType.OTHER);
					InputStream in = connection.getInputStream();
					String other = IOUtils.toString(in);
					IOUtils.closeQuietly(in);
					stdStatus.setOther(other);
					logger.info("config Retrieved ");
					stdStatus.setStatus("Config Retrieved from: " + configURL,
							PolicyResponseStatus.NO_ACTION_REQUIRED,
							PolicyConfigStatus.CONFIG_RETRIEVED);
					try {
						MatchStore.storeMatch(match);
					} catch (Exception e) {
						logger.info("StoreMatch failed for Ecomp:"
								+ match.getEcompName() + " Config: "
								+ match.getConfigName());
					}
					return stdStatus;
				} else {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Config Not Found");
					stdStatus
							.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
					stdStatus
							.setConfigStatus("Illegal form of Configuration Type Found.");
					return stdStatus;
				}
			} catch (IOException e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
				throw new Exception(XACMLErrorConstants.ERROR_PROCESS_FLOW +
						"Cannot open a connection to the configURL", e);
			}
		} catch (MalformedURLException e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in ConfigURL", e);
		}
	}

	private void setProperty(String propertyFilePath)
			throws PolicyEngineException {
		this.propertyFilePath = propertyFilePath;
		if (this.propertyFilePath == null) {
			// This is only for testing purpose. Or We will add a default PDP
			// address here.
			// url_default = "http://localhost:8080/pdp/";
			// The General Error Message is Below.
			throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error NO PropertyFile Path provided");
		} else {
			// Adding logic for remote Properties file.
			Properties prop = new Properties();
			if (propertyFilePath.startsWith("http")) {
				URL configURL;
				try {
					configURL = new URL(propertyFilePath);
					URLConnection connection = null;
					connection = configURL.openConnection();
					prop.load(connection.getInputStream());
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
					throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Maformed property URL "+ e.getMessage());
				}
			} else {
				Path file = Paths.get(propertyFilePath);
				if (Files.notExists(file)) {
					throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "File doesn't exist in the specified Path "	+ file.toString());
				} 
				if (file.toString().endsWith(".properties")) {
					InputStream in;
					prop = new Properties();
					try {
						in = new FileInputStream(file.toFile());
						prop.load(in);
					} catch (IOException e) {
						logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
						throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Cannot Load the Properties file", e);
					}
				} else {
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Not a .properties file " + propertyFilePath);
					throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR  + "Not a .properties file");
				}
			}
			// UEB Settings
			String check_type = prop.getProperty("NOTIFICATION_TYPE");
			String serverList = prop.getProperty("NOTIFICATION_UEB_SERVERS");
			if(check_type==null) {
				notificationType.add("websocket");
				logger.info("Properties file doesn't have the NOTIFICATION_TYPE parameter system will use defualt websockets");	
			}else{
				if(check_type.contains(",")) {
					type_default = new ArrayList<String>(Arrays.asList(prop.getProperty("NOTIFICATION_TYPE").split(",")));
					notificationType = type_default; 
				} else {
						notificationType = new ArrayList<String>();
						notificationType.add(check_type);
				}
			}
			if(serverList==null) {
				notificationType.clear();
				notificationType.add("websocket");
				logger.info("Properties file doesn't have the NOTIFICATION_UEB_SERVERS parameter system will use defualt websockets");	
			}else{
				if(serverList.contains(",")) {
					uebURLList = new ArrayList<String>(Arrays.asList(prop.getProperty("NOTIFICATION_UEB_SERVERS").split(","))); 
				} else {
					uebURLList = new ArrayList<String>();
					uebURLList.add(serverList);
				}
			}
			// Client ID Authorization Settings. 
			String clientID = prop.getProperty("CLIENT_ID");
			String clientKey = prop.getProperty("CLIENT_KEY");
			userName = clientID;
			pass = clientKey;
			pyPDPClientFile = prop.getProperty("CLIENT_FILE");
			environment = prop.getProperty("ENVIRONMENT", "DEVL");
			/*try {
				aafClient = AAFPolicyClient.getInstance(prop);
			} catch (AAFPolicyException e) {
				logger.error(XACMLErrorConstants.ERROR_UNKNOWN + e.getMessage());
				throw new PolicyEngineException(XACMLErrorConstants.ERROR_UNKNOWN + e);
			}*/
			// Initializing the values.
			pdps = new ArrayList<String>();
			paps = new ArrayList<String>(); 
			encoding = new ArrayList<String>();
			encodingPAP = new ArrayList<String>();
			// Check the Keys for PDP_URLs
			Collection<Object> unsorted = prop.keySet();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<String> sorted = new ArrayList(unsorted);
			Collections.sort(sorted);
			for (String propKey : sorted) {
				if (propKey.startsWith("PDP_URL")) {
					String check_val = prop.getProperty(propKey);
					if (check_val == null) {
						throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Properties file doesn't have the PDP_URL parameter");
					}
					if (check_val.contains(";")) {
						pdp_default = new ArrayList<String>(Arrays.asList(check_val.split("\\s*;\\s*")));
						int pdpCount = 0;
						while (pdpCount < pdp_default.size()) {
							String pdpVal = pdp_default.get(pdpCount);
							readPDPParam(pdpVal);
							pdpCount++;
						}
					} else {
						readPDPParam(check_val);
					}
				} else if (propKey.startsWith("PAP_URL")) {
					String check_val = prop.getProperty(propKey);
					if (check_val == null) {
						throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Properties file doesn't have the PAP_URL parameter");
					}
					if (check_val.contains(";")) {
						pap_default = new ArrayList<String>(Arrays.asList(check_val.split("\\s*;\\s*")));
						int papCount = 0;
						while (papCount < pap_default.size()) {
							String papVal = pap_default.get(papCount);
							readPAPParam(papVal);
							papCount++;
						}
					} else {
						readPAPParam(check_val);
					}
				}
			}
			if (pdps == null || pdps.isEmpty()) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PDP_URLs");
				throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PDP_URLs");
			}
			
			if (paps == null || paps.isEmpty()) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PAP_URLs");
				throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed with out PAP_URLs");
			}
			
			// Get JUNIT property from properties file when running tests
			String junit = prop.getProperty("JUNIT");
			if(junit == null || junit.isEmpty()){
				logger.info("No JUNIT property provided, this will not be executed as a test.");
			}else{
				if(junit.equals("test")){
					this.junit = true;
				} else {
					this.junit = false;
				}
			}
		}
	}
	
	/*
	 * Read the PDP_URL parameter
	 */
	private void readPDPParam(String pdpVal) throws PolicyEngineException{
		if(pdpVal.contains(",")){
			List<String> pdpValues = new ArrayList<String>(Arrays.asList(pdpVal.split("\\s*,\\s*")));
			if(pdpValues.size()==3){
				// 0 - PDPURL
				pdps.add(pdpValues.get(0));
				// 1:2 will be UserID:Password
				String userID = pdpValues.get(1);
				String pass = pdpValues.get(2);
				Base64.Encoder encoder = Base64.getEncoder();
				encoding.add(encoder.encodeToString((userID+":"+pass).getBytes(StandardCharsets.UTF_8)));
			}else{
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Credentials to send Request: " + pdpValues);
				throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No enough Credentials to send Request. " + pdpValues);
			}
		}else{
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP value is improper/missing required values: " + pdpVal);
			throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP value is improper/missing required values.");
		}
	}
	
	/*
	 * Read the PAP_URL parameter
	 */
	private void readPAPParam(String papVal) throws PolicyEngineException{
		if(papVal.contains(",")){
			List<String> papValues = new ArrayList<String>(Arrays.asList(papVal.split("\\s*,\\s*")));
			if(papValues.size()==3){
				// 0 - PAPURL
				paps.add(papValues.get(0));
				// 1:2 will be UserID:Password
				String userID = papValues.get(1);
				String pass = papValues.get(2);
				Base64.Encoder encoder = Base64.getEncoder();
				encodingPAP.add(encoder.encodeToString((userID+":"+pass).getBytes(StandardCharsets.UTF_8)));
			}else{
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Credentials to send Request: " + papValues);
				throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No enough Credentials to send Request. " + papValues);
			}
		}else{
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Credentials to send Request: " + papVal);
			throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "No enough Credentials to send Request.");
		}
	}
	/*
	 * Allowing changes to the scheme and Handler.
	 */
	public void notification(NotificationScheme scheme, NotificationHandler handler) {
		this.scheme = scheme;
		this.handler = handler;
		logger.debug("Scheme is : " + scheme.toString());
		logger.debug("Handler is : " + handler.getClass().getName());
		if (!notificationType.get(0).equals("ueb")){
			AutoClientEnd.setAuto(scheme, handler);
		}else {
			if (this.UEBThread){
				UEBClientThread.setAuto(scheme, handler);
				this.UEBThread = registerUEBThread.isAlive();
			}
		}
	
		//TODO This could also be a Start point for Auto Notifications..
		if(pdps!=null){
			if (notificationType.get(0).equals("ueb")  && !this.UEBThread){
				this.UEBClientThread = new AutoClientUEB(pdps.get(0), uebURLList); 
				this.UEBClientThread.setAuto(scheme, handler);
				this.registerUEBThread = new Thread(this.UEBClientThread);
				this.registerUEBThread.start();
				this.UEBThread = true;
			}
			if (!notificationType.get(0).equals("ueb")){
				if(pdps.get(0)!=null){
					if(AutoClientEnd.getURL()==null){
						AutoClientEnd.start(pdps.get(0));
					}else {
						AutoClientEnd.stop();
						AutoClientEnd.start(pdps.get(0));
					}
				}
			}
		}
	}

	/*
	 * Gets the Notification if one exists. Used only for Manual Polling
	 * purposes.
	 */
	public PDPNotification getNotification(){
		//TODO manual Polling
		//Check if there is proper scheme.. 
		PDPNotification notification = null;
		if(this.scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS) || this.scheme.equals(NotificationScheme.MANUAL_NOTIFICATIONS)) {
			if (notificationType.get(0).equals("ueb")){
				ManualClientEndUEB.start(pdps.get(0), uebURLList, uniqueID);
				notification = ManualClientEndUEB.result(scheme);
			}else{
				ManualClientEnd.start(pdps.get(0));
				logger.debug("manual notification requested.. : " + scheme.toString());
				notification = ManualClientEnd.result(scheme);
			}
			
			if (notification == null){
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Notification yet..");
				return null;
			} else {
				return notification;
			}
			
		}else {
			return null;
		}
	}

	/*
	 * Setting the Scheme.
	 */
	public void setScheme(NotificationScheme scheme) {
		this.scheme = scheme;
		if (notificationType.get(0).equals("ueb")){
			AutoClientUEB.setScheme(this.scheme);
			if (this.scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)){
				ManualClientEndUEB.createTopic(pdps.get(0), uniqueID, uebURLList);
			}
		}else{
			AutoClientEnd.setScheme(this.scheme);
		}	
	}

	/*
	 * Returns the Scheme
	 */
	public NotificationScheme getScheme() {
		return this.scheme;
	}

	/*
	 * Returns the NotificationHandler
	 */
	public NotificationHandler getNotificationHandler() {
		return this.handler;
	}

	private Collection<PolicyConfig> configResult(
			Collection<StdStatus> generateRequest) {
		Collection<PolicyConfig> result = new HashSet<PolicyConfig>();
		if (generateRequest == null) {
			return null;
		}
		if (!generateRequest.isEmpty()) {
			for (StdStatus stdStatus : generateRequest) {
				PolicyConfig policyConfig = new StdPolicyConfig();
				policyConfig = stdStatus;
				result.add(policyConfig);
			}
		}
		return result;
	}

	private Collection<PolicyResponse> eventResult(
			Collection<StdStatus> generateRequest,
			Map<String, String> eventAttributes) {
		Collection<PolicyResponse> result = new HashSet<PolicyResponse>();
		if (generateRequest == null) {
			return null;
		}
		if (!generateRequest.isEmpty()) {
			for (StdStatus stdStatus : generateRequest) {
				StdPolicyResponse policyResponse = new StdPolicyResponse();
				policyResponse = stdStatus;
				policyResponse.setRequestAttributes(eventAttributes);
				result.add(policyResponse);
			}
		}
		return result;
	}

	private DecisionResponse decisionResult(Collection<StdStatus> generateRequest) {
		StdDecisionResponse policyDecision = new StdDecisionResponse();
		if (generateRequest == null) {
			return policyDecision;
		}
		if (!generateRequest.isEmpty()) {
			for (StdStatus stdStatus : generateRequest) {
				policyDecision.setDecision(stdStatus.getDecision());
				policyDecision.setDetails(stdStatus.getDetails());
			}
		}
		return policyDecision;
	}

	/*
	 * Stop the Notification Service if its running.
	 */
	public void stopNotification() {
		if (this.scheme != null && this.handler != null) {
			if (this.scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS)
					|| this.scheme
							.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
				logger.info("Clear Notification called.. ");
				if (notificationType.get(0).equals("ueb")){
					this.UEBClientThread.terminate();
					this.UEBThread = false;
				}else{
					AutoClientEnd.stop();
				}
			}
		}
	}
	
	/*
	 * Create Config Policy API Implementation
	 */
	public String createConfigPolicy(String policyName, String policyDescription, String ecompName, String configName,
			Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		return createConfigPolicy(policyName,policyDescription, ecompName, configName,
				configAttributes, configType, body, policyScope, requestID, userName , pass, riskLevel, riskType, guard, ttlDate);
	}
	
	public String createConfigPolicy(String policyName, String policyDescription, String ecompName, String configName,
										Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID, String userID, String passcode,
										String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		
		String response = null;
		String configBody = null;
		String resource= "createPolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response = XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource;
			return response;
		}
		
		//check body for JSON form and remove single quotes if present
		if (configType.equalsIgnoreCase("JSON")) {
			if (body.contains("'")) {
				configBody = body.replace("'", "\"");
			} else {
				configBody = body;
			}
		} else {
			configBody = body;
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (ecompName==null||ecompName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.";
		} else if (configName==null||configName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		}else {

			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Base", policyName, policyDescription, ecompName, configName, configAttributes, configType, 
				configBody, false, policyScope,0, riskLevel, riskType, guard, ttlDate);

			//send JSON object to PAP
			response = (String) callPAP(newPAPPolicy, new String[] {"operation=create", "apiflag=api", "policyType=Config"}, requestID, "Config");
		}
		return response;

	}
	
	/*
	 * Create Config Policy API Implementation
	 */
	public String updateConfigPolicy(String policyName, String policyDescription, String ecompName, String configName,
			Map<String, String> configAttributes, String configType, String body, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		return updateConfigPolicy(policyName, policyDescription, ecompName, configName,
				configAttributes, configType, body, policyScope, requestID, userName, pass, riskLevel, riskType, guard, ttlDate);
	}
	
	public String updateConfigPolicy(String policyName, String policyDescription, String ecompName, String configName,
										Map<String, String> configAttributes, String configType, String body, String policyScope, 
										UUID requestID, String userID, String passcode,String riskLevel, String riskType, String guard, 
										String ttlDate) throws Exception {
		
		String response = null;
		String configBody = null;
		String resource= "updatePolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response = XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource;
			return response;
		}
		//check body for JSON form and remove single quotes if present
		if (configType.equalsIgnoreCase("JSON")) {
			if (body.contains("'")) {
				configBody = body.replace("'", "\"");
			} else {
				configBody = body;
			}
		} else {
			configBody = body;
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (ecompName==null||ecompName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.";
		} else if (configName==null||configName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {

			//set values for basic policy information
			String policyType = "Config";
			String configPolicyType = "base";

			StdPAPPolicy newPAPPolicy = new StdPAPPolicy(configPolicyType, policyName, policyDescription, ecompName, configName, configAttributes, configType, 
				configBody, true, policyScope,0, riskLevel, riskType, guard, ttlDate);

			//send JSON object to PAP
			response = (String) callPAP(newPAPPolicy, new String[] {"operation=update", "apiflag=api", "policyType=" + policyType}, requestID, "Config");
		
		}
		return response;

	}
	
	
	/*
	 * Create Config Firewall Policy API implementation
	 */
	public String createConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		return createConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID, userName, pass, riskLevel, riskType, guard, ttlDate);
	}
	
	public String createConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID, String userID, String passcode,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {		
       		
		String response = null;
		String resource= "createPolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response = XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource;
			return response;
		}
		
		//set values for basic policy information
		String configName = firewallJson.get("configName").toString();
        //String configDescription = firewallJson.get("configDescription").toString();
		String configDescription = "";
		String json = firewallJson.toString();
		
		boolean levelCheck = isNumeric(riskLevel);

		if (!isJSONValid(json)) {
			logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Invalid JSON for firewallJson: " + json);
			throw new PolicyDecisionException(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Invalid JSON for firewallJson: " + json);
		}
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {
			
			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Firewall Config", policyName, configDescription, configName, false, policyScope, json, 0, 
					riskLevel, riskType, guard, ttlDate);
			
			//send JSON object to PAP
			response = (String) callPAP(newPAPPolicy, new String[] {"operation=create", "apiflag=api", "policyType=Config"}, requestID, "ConfigFirewall");
		}

		return response;
	} 
	
	/*
	 * Update Config Firewall Policy API implementation
	 */
	public String updateConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID, String riskLevel, String riskType, 
			String guard, String ttlDate) throws Exception {
		return updateConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID, userName, pass, riskLevel, riskType, guard, ttlDate);
	}
	
	public String updateConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope, UUID requestID, String userID, String passcode,
			String riskLevel, String riskType, String guard, String ttlDate) throws Exception {
		
		String response = null;
		String resource= "updatePolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response = XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource;
			return response;
		}
		String configName = firewallJson.get("configName").toString();
	    //String configDescription = firewallJson.get("configDescription").toString();
		String configDescription = "";  //ASK Lak about this...****
		String json = firewallJson.toString();
		boolean levelCheck = isNumeric(riskLevel);
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {
			
			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Firewall Config", policyName, configDescription, configName, true, policyScope, json, 0,
														  riskLevel, riskType, guard, ttlDate);
		
			//send JSON object to PAP
			response = (String) callPAP(newPAPPolicy, new String[] {"operation=update", "apiflag=api", "policyType=Config"}, requestID, "ConfigFirewall");
		}
		
		return response;
	}
	
	/*
	 * Create or Update BRMS Raw Config Policy API implementation
	 */
	public String createUpdateBRMSRawPolicy(String policyName, 
			String policyDescription, 
			Map<AttributeType, Map<String, String>>  dyanamicFieldConfigAttributes,
			String brmsRawBody, 
			String policyScope, 
			Boolean isEdit, 
			UUID requestID,
			String riskLevel, 
			String riskType, 
			String guard, 
			String ttlDate) {
		
		String response = null;
		String operation = null;
		
		
		if (isEdit){
			operation = "update";
		} else {
			operation = "create";
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if(policyDescription==null || policyDescription.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No policyDescription given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No policyDescription given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (brmsRawBody==null ||brmsRawBody.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No rule body given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No rule body given.";
		}  else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {
			/*String configPolicyType, String policyName, String description, 
					String configName, Boolean editPolicy, String domain, 
					Map<String,String> dyanamicFieldConfigAttributes, Integer highestVersion, String eCompName, 
					String configBodyData*/
			
			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("BRMS_Raw",policyName,policyDescription,
															"BRMS_RAW_RULE",isEdit,policyScope, 
															dyanamicFieldConfigAttributes.get(AttributeType.RULE), 0, "DROOLS", 
															brmsRawBody, riskLevel, riskType, guard, ttlDate);
		
			//send JSON object to PAP
			try {
				response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, requestID, "ConfigBrmsRaw");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/*
	 * Create or Update BRMS Param Config Policy API implementation
	 */
	public String createUpdateBRMSParamPolicy(String policyName, 
			String policyDescription, 
			Map<AttributeType, Map<String, String>>  dyanamicFieldConfigAttributes,
			String brmsRawBody, 
			String policyScope, 
			Boolean isEdit, 
			UUID requestID,
			Map<AttributeType, Map<String, String>> drlRuleAndUIParams,
			String riskLevel, String riskType, String guard, String ttlDate) {
		
		String response = null;
		String operation = null;
		
		
		if (isEdit){
			operation = "update";
		} else {
			operation = "create";
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if(policyDescription==null || policyDescription.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No policyDescription given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No policyDescription given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if ((dyanamicFieldConfigAttributes==null)){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Rule Attributes given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Rule Attributes given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		}else {
			/*public StdPAPPolicy (String configPolicyType, String policyName, String description, 
					String configName, Boolean editPolicy, String domain, 
					Map<String,String> dyanamicFieldConfigAttributes, Integer highestVersion, String eCompName, 
					String configBodyData,Map<String,String> drlRuleAndUIParams) */
			
			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("BRMS_Param",policyName,policyDescription,
															"BRMS_PARAM_RULE",isEdit,policyScope, 
															drlRuleAndUIParams.get(AttributeType.MATCHING), 0, "DROOLS", 
															brmsRawBody, drlRuleAndUIParams.get(AttributeType.RULE), riskLevel, riskType, guard, ttlDate);
		
			//send JSON object to PAP
			try {
				response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, requestID, "ConfigBrmsParam");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/*
	 * Create or Update Action Policy API implementation
	 */
	public String createUpdateActionPolicy(String policyName, String policyDescription, Map<String,String> componentAttributes, List<String> dynamicRuleAlgorithmLabels,
								List<String> dynamicRuleAlgorithmField1, List<String> dynamicRuleAlgorithmFunctions, List<String> dynamicRuleAlgorithmField2,
								String actionPerformer, String actionAttribute, String policyScope, Boolean isEdit, UUID requestID) {
		
		String response = null;
		String operation = null;
		
		if (isEdit){
			operation = "update";
		} else {
			operation = "create";
		}
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
			return response;
		} else if (componentAttributes==null||componentAttributes.equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Component Attributes given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Component Attributes given.";
			return response;
		} else if (actionAttribute==null||actionAttribute.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Action Attribute given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Action Attribute given.";
			return response;
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
			return response;
		} else if (actionPerformer==null||actionPerformer.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Action Performer given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Action Performer given.";
			return response;
		} else if (!actionPerformer.equals("PEP")) {
			if (!actionPerformer.equals("PDP")) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Action Performer given.");
				response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Action Performer given.";
				return response;
			}
		}
		
		StdPAPPolicy newPAPPolicy = new StdPAPPolicy(policyName, policyDescription, componentAttributes, dynamicRuleAlgorithmLabels, dynamicRuleAlgorithmFunctions, 
					dynamicRuleAlgorithmField1, dynamicRuleAlgorithmField2, actionPerformer, actionAttribute, isEdit, policyScope, 0);

			//send JSON object to PAP
		try {
			response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Action"}, requestID, "Action");
		} catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return response;

	}
	
	/*
	 * Create or Update Decision Policy implementation
	 */
	private String createUpdateDecisionPolicy(String policyName, String policyDescription, String ecompName, RuleProvider ruleProvider, Map<String,String> componentAttributes, Map<String,String> settings,
			List<String> dynamicRuleAlgorithmLabels, List<String> dynamicRuleAlgorithmField1, List<String> dynamicRuleAlgorithmFunctions, List<String> dynamicRuleAlgorithmField2,
			String policyScope, Boolean isEdit, UUID requestID) {
		
		String response = null;
		String operation = null;
		
		if (isEdit){
			operation = "update";
		} else {
			operation = "create";
		}
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (ecompName==null||ecompName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else {
			
			if (ruleProvider==null) {
				ruleProvider = RuleProvider.CUSTOM ;
			}
			
			StdPAPPolicy newPAPPolicy = new StdPAPPolicy(policyName, policyDescription, ecompName, ruleProvider.toString(), componentAttributes, settings, dynamicRuleAlgorithmLabels, dynamicRuleAlgorithmFunctions, 
									dynamicRuleAlgorithmField1, dynamicRuleAlgorithmField2, null, null, null, isEdit, policyScope, 0);
		
			//send JSON object to PAP
			try {
				response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Decision"}, requestID, "Decision");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	/*
	 * Create or Update ClosedLoop_Fault policy implementation
	 */
	private String createUpdateClosedLoopPolicy(String policyName, JsonObject configBody, String policyDescription, String policyScope, Boolean isEdit, 
			UUID requestID,String riskLevel, String riskType, String guard, String ttlDate) {
		
		String response = null;
		String operation = null;
		String oldPolicyName = null;
		
		if (isEdit){
			operation = "update";
			if (policyName.endsWith("_Draft")) {
				oldPolicyName = policyName + "_Draft.1";
			}
		} else {
			operation = "create";
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		// get values and attributes from the JsonObject
		String ecompName = configBody.get("ecompname").toString().replace("\"", "");
		String jsonBody = configBody.toString();

		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (ecompName==null||ecompName.equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Ecomp Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Ecomp Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {
			

			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("ClosedLoop_Fault", policyName, policyDescription, ecompName, 
					jsonBody, false, oldPolicyName, null, isEdit, policyScope, 0, riskLevel, riskType, guard, ttlDate); 

			//send JSON object to PAP
			try {
				response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, requestID, "ConfigClosedLoop");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return response;
		
	}
	
	private String createUpdateClosedLoopPmPolicy(String policyName, JsonObject configBody, String policyDescription, String policyScope, Boolean isEdit, 
			UUID requestID, String riskLevel, String riskType, String guard, String ttlDate) {
		
		String response = null;
		String operation = null;
		String oldPolicyName = null;
		
		if (isEdit){
			operation = "update";
		} else {
			operation = "create";
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		// get values and attributes from the JsonObject
		String ecompName = configBody.get("ecompname").toString().replace("\"", "");
		String serviceType = configBody.get("serviceTypePolicyName").toString().replace("\"", "");
		String jsonBody = configBody.toString();

		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (ecompName==null||ecompName.equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Ecomp Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Ecomp Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {
			

			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("ClosedLoop_PM", policyName, policyDescription, ecompName, 
					jsonBody, false, oldPolicyName, serviceType, isEdit, policyScope, 0, riskLevel, riskType, guard, ttlDate); 

			//send JSON object to PAP
			try {
				response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, requestID, "ConfigClosedLoop");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return response;
		
	}
	
	public Boolean validateNONASCIICharactersAndAllowSpaces(Object json){
		Boolean isValidForm = false;
		if (json instanceof String) {
			String jsonString = (String)json;
			if (jsonString.isEmpty()) {
				logger.info("JSON String is empty so cannot validate NON ACSII Characters.");
			} else {
				if(CharMatcher.ASCII.matchesAllOf((CharSequence) jsonString)){
					logger.info("The Value does not contain ASCII Characters");
				   	 isValidForm = true;
				}else{
					logger.error("The Value Contains Non ASCII Characters");
					isValidForm = false;
				}	
			}
		} else if (json instanceof JsonObject) {
			JsonObject jsonObj = (JsonObject)json;
			if (jsonObj.isEmpty()){
				logger.info("JSON object is empty so cannot validate NON ACSII Characters.");
			} else {
				if(CharMatcher.ASCII.matchesAllOf((CharSequence) jsonObj.toString())){
					logger.info("The Value does not contain ASCII Characters");
				   	 isValidForm = true;
				}else{
					logger.error("The Value Contains Non ASCII Characters");
					isValidForm = false;
				}	
			}
			
		}
		
		return isValidForm;
	}
	
	private String createUpdateMicroServicesPolicy(String policyName, JsonObject microServiceAttributes, String ecompName, String policyScope, Boolean isEdit, UUID requestID,
			String riskLevel, String riskType, String guard, String ttlDate) {
		
		String response = null;
		String operation = null;
		
		if (isEdit){
			operation = "update";
		} else {
			operation = "create";
		}
		
		boolean levelCheck = isNumeric(riskLevel);
		
		// get values and attributes from the JsonObject
		String microService = microServiceAttributes.get("service").toString().replace("\"", "");
		String uuid = microServiceAttributes.get("uuid").toString().replace("\"", "");
		String msLocation = microServiceAttributes.get("location").toString().replace("\"", "");;
		String policyDescription = microServiceAttributes.get("description").toString().replace("\"", "");
		String configName = microServiceAttributes.get("configName").toString().replace("\"", "");
		String priority = microServiceAttributes.get("priority").toString().replace("\"", "");
		String version = microServiceAttributes.get("version").toString().replace("\"", "");

		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (ecompName==null||ecompName.equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Ecomp Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Ecomp Name given.";
		} else if (configName==null||configName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Configuration Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Configuration Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (!levelCheck){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
		} else {
			

			StdPAPPolicy newPAPPolicy = new StdPAPPolicy("DCAE Micro Service", policyName, policyDescription, ecompName, 
							configName, microService, uuid, msLocation, microServiceAttributes.toString(), priority, 
							version, isEdit, policyScope, 0, riskLevel, riskType, guard, ttlDate); 

			//send JSON object to PAP
			try {
				response = (String) callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, requestID, "ConfigMS");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return response;
		
	}
	
	
	/*
	 * Push a policy to the PDP API implementation
	 */
	public String pushPolicy(String policyScope, String policyName, String policyType, String pdpGroup, UUID requestID) throws Exception {
		return pushPolicy(policyScope, policyName, policyType, pdpGroup, requestID, userName, pass);
	}
	
	public String pushPolicy(String policyScope, String policyName, String policyType, String pdpGroup, UUID requestID, String userID, String passcode) throws Exception {
		String resource= "pushPolicy";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			return (XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
		}
		String response = null;
		String filePrefix = null;
		String clientScope = null;
		String activeVersion = null;
		
		//get the client scope based policy type
		if (policyType.equalsIgnoreCase("Firewall")){
			clientScope = "ConfigFirewall";
			filePrefix = "Config_FW_";
		} else if (policyType.equalsIgnoreCase("Action")) {
			clientScope = "Action";
			filePrefix = "Action_";
		} else if (policyType.equalsIgnoreCase("Decision")){
			clientScope = "Decision";
			filePrefix = "Decision_";
		} else if (policyType.equalsIgnoreCase("Base")){
			clientScope = "Config";
			filePrefix = "Config_";
		} else if (policyType.equalsIgnoreCase("ClosedLoop_Fault")){
			clientScope = "ConfigClosedLoop";
			filePrefix = "Config_Fault_";
		} else if (policyType.equalsIgnoreCase("ClosedLoop_PM")){
			clientScope = "ConfigClosedLoop";
			filePrefix = "Config_PM_";
		} else if (policyType.equalsIgnoreCase("MicroService")) {
			clientScope = "ConfigMS";
			filePrefix = "Config_MS_";
		}else if (policyType.equalsIgnoreCase("BRMS_RAW")){
			clientScope = "ConfigBrmsRaw";
			filePrefix = "Config_BRMS_Raw_";
		} else if (policyType.equalsIgnoreCase("BRMS_PARAM")){
			clientScope = "ConfigBrmsParam";
			filePrefix = "Config_BRMS_Param_";
		} else {
			clientScope = null;
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + policyType + " is not a valid Policy Type.");
			return XACMLErrorConstants.ERROR_DATA_ISSUE + policyType + " is not a valid Policy Type.";
		}
		
		logger.debug("clientScope is " + clientScope);
		logger.debug("filePrefix is " + filePrefix);
		
		if (pdpGroup == null) {
			pdpGroup = "default";
		}
		
		if (policyName==null||policyName.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (policyScope==null||policyScope.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
		} else if (policyType==null||policyType.equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Type given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Type given.";
		} else {
			// requestID null check. 
			if(requestID==null){
				requestID =  UUID.randomUUID();
				logger.debug("Request ID not provided.  Generating request ID " + requestID.toString());
			}

			// change call to getActiveVersion to pass requestID for PAP to receive on the GET process so PAP won't generate another 
	//		activeVersion = getActiveVersion(policyScope, filePrefix, policyName, clientScope);
			activeVersion = getActiveVersion(policyScope, filePrefix, policyName, clientScope, requestID);
			logger.debug("The active version of " + policyScope + File.separator + filePrefix + policyName + " is " + activeVersion);
			
			String id = null;
			if (activeVersion.equalsIgnoreCase("pe100")) {
				logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is 403. PEP is not Authorized for making this Request!! "
						+ "\n Contact Administrator for this Scope. ");
				return XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is 403. PEP is not Authorized for making this Request!! "
						+ "Contact Administrator for this Scope. ";
						
			} else if (activeVersion.equalsIgnoreCase("pe300")) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
						+ "This indicates a problem with getting the version from the PAP or the policy does not exist.");
				return XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
						+ "This indicates a problem with getting the version from the PAP or the policy does not exist.";
			}
			
			
			if (!activeVersion.equalsIgnoreCase("0")) {
				id = policyScope + "." + filePrefix + policyName + "." + activeVersion + ".xml";
				logger.debug("The policyId is " + id);
			} else {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "could not retrieve the activeVersion for this policy.  "
						+ "This indicates the policy does not exist, please verify the policy exists.");
				return XACMLErrorConstants.ERROR_DATA_ISSUE + "could not retrieve the activeVersion for this policy. could not retrieve the activeVersion for this policy.  "
						+ "This indicates the policy does not exist, please verify the policy exists.";
			}

			// change call to getgitPath to pass requestID for PAP to receive on the GET process so PAP won't generate another 
			// String gitPath = getGitPath(policyScope, filePrefix, policyName, activeVersion, clientScope);
			String gitPath = getGitPath(policyScope, filePrefix, policyName, activeVersion, clientScope, requestID);
			logger.debug("Full gitPath policy xml file: " + gitPath);

			// change call to getSelectedURI to pass requestID for PAP to receive on the GET process so PAP won't generate another 
			// URI selectedURI = getSelectedURI(gitPath, clientScope);
			URI selectedURI = getSelectedURI(gitPath, clientScope, requestID);
			
			logger.debug("The selectedURI is : " + selectedURI.toString());
			String name = filePrefix+policyName;
					
			StdPDPPolicy selectedPolicy = new StdPDPPolicy(id, true, name, selectedURI, isValid, policyId, description, pushVersion);
			
			logger.debug("StdPDPPolicy object contains: " + selectedPolicy.getId() + ", " + selectedPolicy.getName() + ", " + selectedPolicy.getLocation().toString());
			
			response = copyPolicy(selectedPolicy, pdpGroup, clientScope, requestID);
			
			logger.debug("copyPolicy response:  " + response);
			
			if(response.contains("successfully")){
				response = (String) callPAP(selectedPolicy, new String[]{"groupId=" + pdpGroup, "policyId="+id, "apiflag=addPolicyToGroup", "operation=PUT"}, requestID, clientScope);
			}
			
			logger.debug("Final API response: " + response);
		}
		
		return response;
		
	}
	
	private String deletePolicyFromPAP(DeletePolicyParameters parameters) {
		String response = null;
		String clientScope = null;
		String pdpGroup = parameters.getPdpGroup();
		
		if (pdpGroup==null){
			pdpGroup="NA";
		}
		
		//get the client scope based policy type
		if (parameters.getPolicyName().contains("Config_FW")){
			clientScope = "ConfigFirewall";
		} else if (parameters.getPolicyName().contains("Action")) {
			clientScope = "Action";
		} else if (parameters.getPolicyName().contains("Decision")){
			clientScope = "Decision";
		} else if (parameters.getPolicyName().contains("Config_Fault")){
			clientScope = "ConfigClosedLoop";
		} else if (parameters.getPolicyName().contains("Config_PM")){
			clientScope = "ConfigClosedLoop";
		} else if (parameters.getPolicyName().contains("Config_MS")){
			clientScope = "ConfigMS";
		} else if (parameters.getPolicyName().contains("Config_BRMS_Raw")){
			clientScope = "ConfigBrmsRaw";
		} else if (parameters.getPolicyName().contains("Config_BRMS_Param")){
			clientScope = "ConfigBrmsParam";
		} else {
			clientScope = "Config";
		}
		
		logger.debug("clientScope is " + clientScope);
		
		if (clientScope==null||clientScope.equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + parameters.getPolicyName() + " is not a valid Policy Name.");
			return XACMLErrorConstants.ERROR_DATA_ISSUE + parameters.getPolicyName() + " is not a valid Policy Name.";
		}
		
		if (parameters.getPolicyName()==null||parameters.getPolicyName().equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (parameters.getDeleteCondition()==null||parameters.getDeleteCondition().equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Delete Condition given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Delete Condition given.";
		} else {
			
			StdPAPPolicy deletePapPolicy = new StdPAPPolicy(parameters.getPolicyName(), parameters.getDeleteCondition().toString()); 

			//send JSON object to PAP
			try {
				response = (String) callPAP(deletePapPolicy, new String[] {"groupId="+pdpGroup, "apiflag=deletePapApi", "operation=delete" }, parameters.getRequestID(), clientScope);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
		}
		
		return response;
	}
	
	private String deletePolicyFromPDP(DeletePolicyParameters parameters) {
		String response = null;
		String clientScope = null;
		String pdpGroup = parameters.getPdpGroup();
		
		if (pdpGroup==null){
			pdpGroup="NA";
		}
		
		//get the client scope based policy type
		if (parameters.getPolicyName().contains("Config_FW")){
			clientScope = "ConfigFirewall";
		} else if (parameters.getPolicyName().contains("Action")) {
			clientScope = "Action";
		} else if (parameters.getPolicyName().contains("Decision")){
			clientScope = "Decision";
		} else if (parameters.getPolicyName().contains("Config_Fault")){
			clientScope = "ConfigClosedLoop";
		} else if (parameters.getPolicyName().contains("Config_PM")){
			clientScope = "ConfigClosedLoop";
		} else if (parameters.getPolicyName().contains("Config_MS")){
			clientScope = "ConfigMS";
		}else if (parameters.getPolicyName().contains("Config_BRMS_Raw")){
			clientScope = "ConfigBrmsRaw";
		} else if (parameters.getPolicyName().contains("Config_BRMS_Param")){
			clientScope = "ConfigBrmsParam";
		} else {
			clientScope = "Config";
		}
		
		logger.debug("clientScope is " + clientScope);
		
		if (clientScope==null||clientScope.equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + parameters.getPolicyName() + " is not a valid Policy Name.");
			return XACMLErrorConstants.ERROR_DATA_ISSUE + parameters.getPolicyName() + " is not a valid Policy Name.";
		}
		
		if (parameters.getPolicyName()==null||parameters.getPolicyName().equalsIgnoreCase("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
		} else if (parameters.getPdpGroup()==null||parameters.getPdpGroup().equals("")){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No PDP Group given.");
			response = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PDP Group given.";
		} else {
			
			//send JSON object to PAP
			try {
				response = (String) callPAP(null, new String[] {"policyName="+parameters.getPolicyName(), "groupId="+pdpGroup, "apiflag=deletePdpApi", "operation=delete" }, parameters.getRequestID(), clientScope);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
		}
		
		return response;
	}
	
	/*
	 * Copy a single Policy file from the input stream to the PAP Servlet.
	 * Either this works (silently) or it throws an exception.
	 * 
	 */
	public String copyFile(String policyId, String group, StdPAPPolicy location, String clientScope, UUID requestID) throws PAPException {
		String response = null;
		//String clientScope = null;
		
		// send the policy file to the PAP Servlet
		try {
			response = (String) callPAP(location, new String[] {"groupId=" + group, "policyId="+policyId, "apiflag=api", "operation=post"}, requestID, clientScope);
		} catch (Exception e) {
			String message = "Unable to PUT policy '" + policyId + "', e:" + e;
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
			throw new PAPException(message);
		}
		
		return response;
	}
	
	public String copyPolicy(PDPPolicy policy, String group, String policyType, UUID requestID) throws PAPException {
		String response = null;
		
		if (policy == null || group == null) {
			throw new PAPException("Null input policy="+policy+"  group="+group);
		}
		try {
			StdPAPPolicy location = new StdPAPPolicy(policy.getLocation());
			response = copyFile(policy.getId(), group, location, policyType, requestID);
		} catch (Exception e) {
			String message = "Unable to PUT policy '" + policy.getId() + "', e:" + e;
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
			throw new PAPException(message);
		}
		
		return response;
	}

	public Object callPAP(Object content, String[] parameters, UUID requestID, String clientScope) throws Exception {
		String response = null;
		HttpURLConnection connection = null;
		String requestMethod = null;
		String operation = null;
		responseCode = 0; 
		// Checking for the available PDPs is done during the first Request and the List is going to have the connected PDP as first element.
		// This makes it Real-Time to change the list depending on their availability.
		if (paps == null || paps.isEmpty()) {
		logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PAPs List is Empty.");
		throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"PAPs List is empty.");
		}else {
			int papsCount = 0;
			boolean connected = false;
			while (papsCount < paps.size()) {
				try {
					String fullURL = paps.get(0);
					if (parameters != null && parameters.length > 0) {
						String queryString = "";
						for (String p : parameters) {
							queryString += "&" + p;
							if (p.equalsIgnoreCase("operation=post")){
								requestMethod = "POST";
							} else if (p.equalsIgnoreCase("operation=delete")){
								requestMethod = "DELETE";
								operation = "delete";
							} else {
								requestMethod = "PUT";
								if (p.equalsIgnoreCase("operation=create")){
									operation = "create";
								} else if (p.equalsIgnoreCase("operation=update")){
									operation = "update";
								} else if (p.equalsIgnoreCase("operation=createDictionary")){
									operation = "createDictionary";
								}
							}
						}
						fullURL += "?" + queryString.substring(1);
					}
	
					URL url = new URL (fullURL);
					
					//Open the connection
					connection = (HttpURLConnection)url.openConnection();
					
					// Setting Content-Type
					connection.setRequestProperty("Content-Type",
							"application/json");
					
					// Adding Authorization
					connection.setRequestProperty("Authorization", "Basic "
							+ encodingPAP.get(0));
					
					connection.setRequestProperty("Environment", environment);
					connection.setRequestProperty("ClientScope", clientScope);
					
					//set the method and headers
					connection.setRequestMethod(requestMethod);
					connection.setUseCaches(false);
					connection.setInstanceFollowRedirects(false);
					connection.setDoOutput(true);
					connection.setDoInput(true);
					// Adding RequestID
					if (requestID == null) {
						requestID = UUID.randomUUID();
						logger.info("No request ID provided, sending generated ID: " + requestID.toString());
					} else {
						logger.info("Using provided request ID: " + requestID.toString());
					}
					connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());

					if (content != null) {
						if (content instanceof InputStream) {
							try {
								//send  current configuration
								try (OutputStream os = connection.getOutputStream()) {
									int count = IOUtils.copy((InputStream)content, os);
									if (logger.isDebugEnabled()) {
										logger.debug("copied to output, bytes=" + count);
									}
								}
							} catch (Exception e) {
								logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Failed to write content in 'PUT'", e);
								throw e;
							}
						} else {
							// the content is an object to be encoded in JSON
							ObjectMapper mapper = new ObjectMapper();
							if(!junit){
								mapper.writeValue(connection.getOutputStream(), content);
							}
						}
					}
	
					//DO the connect
					connection.connect();
					responseCode = connection.getResponseCode();
					// If Connected to PAP then break from the loop and continue with the Request 
					if (connection.getResponseCode() > 0 || junit) {
						connected = true;
						break;

					} else {
						logger.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error");
					}
				} catch (Exception e) {
					// This means that the PAP is not working 
					if (junit) {
						connected = true;
						break;
					}
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
				}
				papsCount++;
			}
	
			if (connected) {
				//Read the Response
				logger.debug("connected to the PAP : " + paps.get(0));
				logger.debug("--- Response: ---");
				Map<String, List<String>> headers = connection.getHeaderFields();
				for (String key : headers.keySet()) {
					logger.debug("Header :" + key + "  Value: " + headers.get(key));
				}
				try {
					if (responseCode == 200 || junit) {
						
						// Check for successful creation of policy
						String isSuccess = null;
						if(!junit){ //is this a junit test?
							isSuccess = connection.getHeaderField("successMapKey");
							operation = connection.getHeaderField("operation");
						} else {
							isSuccess = "success";
						}
						
						if (isSuccess.equals("success")) {
							if (operation.equals("update")) {
								logger.info("Transaction ID: " + requestID + " --Policy Updated Successfully!" );
								response = "Transaction ID: " + requestID + " --Policy with the name " + connection.getHeaderField("policyName") + " was successfully updated.";
							} else if (operation.equals("delete")) {
								logger.info("Transaction ID: " + requestID + " --Policy Deleted Successfully!");
								response = "Transaction ID: " + requestID + " --The policy was successfully deleted.";    
							} else if (operation.equals("import")) {
								logger.info("Transaction ID: " + requestID + " --Policy Engine Import Successful!");
								response = "Transaction ID: " + requestID + " --The policy engine import for " + connection.getHeaderField("service") +  " was successfull.";    
							}else {
								logger.info("Transaction ID: " + requestID + " --Policy Created Successfully!" );
								response = "Transaction ID: " + requestID + " --Policy with the name " + connection.getHeaderField("policyName") + " was successfully created.";
							}
									
						} else {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Unable to Create/Update the Policy!");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Unable to Create/Update the Policy!";
						}
					} else if (connection.getResponseCode() == 202) {
						if (connection.getHeaderField("operation")!=null && connection.getHeaderField("operation").equalsIgnoreCase("delete")){
							if (connection.getHeaderField("lockdown")!=null && connection.getHeaderField("lockdown").equals("true")){
								logger.warn("Transaction ID: " + requestID + "Policies are locked down.");
								response = "Transaction ID: " + requestID + " --Policies are locked down, please try again later.";
							}
						}
					} else if (connection.getResponseCode() == 204) {
						if (connection.getHeaderField("operation")!=null && connection.getHeaderField("operation").equals("push")){
							logger.info("Transaction ID: " + requestID + " --Policy '" + connection.getHeaderField("policyId") +
									"' was successfully pushed to the PDP group '" + connection.getHeaderField("groupId") + "'.");
							response = "Transaction ID: " + requestID + " --Policy '" + connection.getHeaderField("policyId") +
									"' was successfully pushed to the PDP group '" + connection.getHeaderField("groupId") + "'.";
						}
					} else if (connection.getResponseCode() == 400 && connection.getHeaderField("error")!=null){
						if (connection.getHeaderField("error").equals("noPolicyExist")) {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy does not exist on the PDP.");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy does not exist on the PDP.";
						} else if (connection.getHeaderField("error").equals("invalidPolicyName")) {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid policyName... "
        				+ "policyName must be the full name of the file to be deleted including version and extension");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid policyName... "
			        				+ "policyName must be the full name of the file to be deleted including version and extension";
						} else if (connection.getHeaderField("error").equals("actionPolicyDB")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Could not find " + connection.getHeaderField("actionAttribute") + " in the ActionPolicyDict table.");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid actionAttribute given.";
						} else if (connection.getHeaderField("error").equals("serviceModelDB")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Service or Version.  The Service Model, " 
									+ connection.getHeaderField("modelName") + " of version " + connection.getHeaderField("modelVersion") 
									+ " was not found in the dictionary.");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Service or Version.  The Service Model, " 
									+ connection.getHeaderField("modelName") + " of version " + connection.getHeaderField("modelVersion") 
									+ " was not found in the dictionary.";
						} else if (connection.getHeaderField("error").equals("FWDBError")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error when inserting Firewall ConfigBody data into database.");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Error when inserting Firewall ConfigBody data into the database.";
						} else if (connection.getHeaderField("error").equals("savePolicy")){
							logger.error(connection.getHeaderField("message"));
							response = connection.getHeaderField("message");
						}
					} else if (connection.getResponseCode() == 403) {
						logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is " 
									+ connection.getResponseCode() + ". PEP is not Authorized for making this Request!! \n Contact Administrator for this Scope. ");
						response = XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is " 
								+ connection.getResponseCode() + ". PEP is not Authorized for making this Request!! \n Contact Administrator for this Scope. ";	
					} else if (connection.getResponseCode() == 404 && connection.getHeaderField("error")!=null) {
						if (connection.getHeaderField("error").equals("unknownGroupId")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + connection.getHeaderField("message"));
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + connection.getHeaderField("message") +  
									" Please check the pdpGroup you are requesting to move the policy to.";
						}
					} else if (connection.getResponseCode() == 409 && connection.getHeaderField("error")!=null) {
						if (connection.getHeaderField("error").equals("modelExistsDB")) {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Import Value Exist Error");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Import Value Exist Error:  The import value "+connection.getHeaderField("service")+" already exist on the PAP. "
									+ "Please create a new import value.";
						}else if (connection.getHeaderField("error").equals("policyExists")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Exist Error");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Exist Error:  The Policy "+connection.getHeaderField("policyName")+" already exist on the PAP. "
									+ "Please create a new policy or use the update API to modify the existing one.";
						}
					} else if (connection.getResponseCode() == 500 && connection.getHeaderField("error")!=null) {
						if (connection.getHeaderField("error").equals("jpautils")){
							logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Could not create JPAUtils instance on the PAP");
							response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Could not create JPAUtils instance on the PAP";
						} else if (connection.getHeaderField("error").equals("deleteDB")){
							logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to delete Policy from database.");
							response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to delete Policy from database.";
						} else if (connection.getHeaderField("error").equals("deleteFile")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the policy file.");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the policy file";
						} else if (connection.getHeaderField("error").equals("groupUpdate")){
							logger.error(connection.getHeaderField("message"));
							response = connection.getHeaderField("message");
						}else if (connection.getHeaderField("error").equals("unknown")){
							logger.error(XACMLErrorConstants.ERROR_UNKNOWN + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.");
							response = XACMLErrorConstants.ERROR_UNKNOWN + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.";
						} else if (connection.getHeaderField("error").equals("deleteConfig")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the configuration or action body file in specified location");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the configuration or action body file in specified location.";
						}else if (connection.getHeaderField("error").equals("missing")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Failed to create value in database because service does match a value in file");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Failed to create value in database because service does match a value in file";
						}else if (connection.getHeaderField("error").equals("importDB")){
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Database errors during policy engine import");
							response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Database errors during policy engine import";
						}else if (connection.getHeaderField("error").equals("policyCopyError")){
							logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + connection.getHeaderField("message"));
							response = XACMLErrorConstants.ERROR_PROCESS_FLOW + connection.getHeaderField("message");
						}else if (connection.getHeaderField("error").equals("addGroupError")){
							logger.error(connection.getHeaderField("message"));
							response = connection.getHeaderField("message");
						}else if (connection.getHeaderField("error").equals("error")){
							logger.error(XACMLErrorConstants.ERROR_UNKNOWN + "Could not create or update the policy for and unknown reason");
							response = XACMLErrorConstants.ERROR_UNKNOWN + "Could not create or update the policy for and unknown reason";
						}
					} else {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  Error occured while attempting perform this operation.. the request may be incorrect.");
						response = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  Error occured while attempting perform this operation.. the request may be incorrect.";
					}
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
					response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + e;
					throw new Exception(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"ERROR in connecting to the PAP ", e);
				} 
				
				if (junit){
					response = "success";
				}
				return response;
	
			} else {
				response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get valid response from PAP(s) " + paps;
				return response;
			}	
		}
	
	}
	
	// change getSelectedURI method to receive requestID to be used to send to PAP on the GET request so PAP won't generate another
	// private URI getSelectedURI(String gitPath, String clientScope){
	private URI getSelectedURI(String gitPath, String clientScope, UUID requestID){
		//Connect to the PAP
		URI selectedURI = null;
		HttpURLConnection connection = null;
		String [] parameters = {"apiflag=uri", "gitPath="+gitPath};


		// Checking for the available PDPs is done during the first Request and the List is going to have the connected PDP as first element.
		// This makes it Real-Time to change the list depending on their availability.
		if (paps == null || paps.isEmpty()) {
		logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PAPs List is Empty.");
		try {
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"PAPs List is empty.");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		}else {
			int papsCount = 0;
			boolean connected = false;
			while (papsCount < paps.size()) {
				try {
					String fullURL = paps.get(0);
					if (parameters != null && parameters.length > 0) {
						String queryString = "";
						for (String p : parameters) {
							queryString += "&" + p;
						}
						fullURL += "?" + queryString.substring(1);
					}
	
					URL url = new URL (fullURL);
					
					//Open the connection
					connection = (HttpURLConnection)url.openConnection();
					
					// Setting Content-Type
					connection.setRequestProperty("Content-Type",
							"application/json");
					
					// Adding Authorization
					connection.setRequestProperty("Authorization", "Basic "
							+ encodingPAP.get(0));
					
					connection.setRequestProperty("Environment", environment);
					connection.setRequestProperty("ClientScope", clientScope);
					
					//set the method and headers
					connection.setRequestMethod("GET");
					connection.setUseCaches(false);
					connection.setInstanceFollowRedirects(false);
					connection.setDoOutput(true);
					connection.setDoInput(true);

					// set requestID in header properties to be used to send to PAP on the GET request so PAP won't generate another
					connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
	
					//DO the connect
					connection.connect();
					responseCode = connection.getResponseCode();
					// If Connected to PAP then break from the loop and continue with the Request 
					if (connection.getResponseCode() > 0) {
						connected = true;
						break;

					} else {
						logger.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error");
					}
				} catch (Exception e) {
					// This means that the PAP is not working 
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
				}
				papsCount++;
			}
	
			if (connected) {
				//Read the Response
				logger.debug("connected to the PAP : " + paps.get(0));
				logger.debug("--- Response: ---");
				Map<String, List<String>> headers = connection.getHeaderFields();
				for (String key : headers.keySet()) {
					logger.debug("Header :" + key + "  Value: " + headers.get(key));
				}
				try {
					if (connection.getResponseCode() == 200) {
						// Check for successful creation of policy
						String uri = connection.getHeaderField("selectedURI");
						logger.debug("URI from Header: " + uri);
						if (uri != null && !uri.equalsIgnoreCase("")) {
							selectedURI = URI.create(uri);
							return selectedURI;
						} else {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "could not retrieve the gitPath from the PAP");
						}
					} else if (connection.getResponseCode() == 404) {
						logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "response code of the URL is " 
									+ connection.getResponseCode() + ". This indicates a problem with getting the gitPath from the PAP");
					} else {
						logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "BAD REQUEST:  Error occured while getting the gitPath from the PAP. The request may be incorrect.");
					}
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
					try {
						throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"ERROR in connecting to the PAP ", e);
					} catch (Exception e1) {
						logger.error(e1.getMessage());
					}
				} 
	
			} else {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get valid response from PAP(s) " + paps);
				try {
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"ERROR in connecting to the PAP ");
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}	
		}
		return selectedURI;
						
	}
	
	// Make a call to the PAP to get the gitPath
	// change getGitPath method to receive requestID to be used to send to PAP on the GET request so PAP won't generate another
	// private String getGitPath(String policyScope, String filePrefix, String policyName, String activeVersion, String clientScope){
	private String getGitPath(String policyScope, String filePrefix, String policyName, String activeVersion, String clientScope, UUID requestID){

		//Connect to the PAP
		String gitPath = null;
		HttpURLConnection connection = null;
		String [] parameters = {"apiflag=gitPath", "policyScope="+policyScope, "filePrefix="+filePrefix, 
									"policyName="+policyName, "activeVersion="+activeVersion};
		

		// Checking for the available PDPs is done during the first Request and the List is going to have the connected PDP as first element.
		// This makes it Real-Time to change the list depending on their availability.
		if (paps == null || paps.isEmpty()) {
		logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PAPs List is Empty.");
		try {
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"PAPs List is empty.");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		}else {
			int papsCount = 0;
			boolean connected = false;
			while (papsCount < paps.size()) {
				try {
					String fullURL = paps.get(0);
					if (parameters != null && parameters.length > 0) {
						String queryString = "";
						for (String p : parameters) {
							queryString += "&" + p;
						}
						fullURL += "?" + queryString.substring(1);
					}
	
					URL url = new URL (fullURL);
					
					//Open the connection
					connection = (HttpURLConnection)url.openConnection();
					
					// Setting Content-Type
					connection.setRequestProperty("Content-Type",
							"application/json");
					
					// Adding Authorization
					connection.setRequestProperty("Authorization", "Basic "
							+ encodingPAP.get(0));
					
					connection.setRequestProperty("Environment", environment);
					connection.setRequestProperty("ClientScope", clientScope);
					
					//set the method and headers
					connection.setRequestMethod("GET");
					connection.setUseCaches(false);
					connection.setInstanceFollowRedirects(false);
					connection.setDoOutput(true);
					connection.setDoInput(true);

					// set requestID in header properties to be used to send to PAP on the GET request so PAP won't generate another
					connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
	
					//DO the connect
					connection.connect();
	
					// If Connected to PAP then break from the loop and continue with the Request 
					if (connection.getResponseCode() > 0) {
						connected = true;
						break;

					} else {
						logger.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error");
					}
				} catch (Exception e) {
					// This means that the PAP is not working 
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
				}
				papsCount++;
			}
	
			if (connected) {
				//Read the Response
				logger.debug("connected to the PAP : " + paps.get(0));
				logger.debug("--- Response: ---");
				Map<String, List<String>> headers = connection.getHeaderFields();
				for (String key : headers.keySet()) {
					logger.debug("Header :" + key + "  Value: " + headers.get(key));
				}
				try {
					if (connection.getResponseCode() == 200) {
						// Check for successful creation of policy
						gitPath = connection.getHeaderField("gitPath");
						this.policyId = connection.getHeaderField("policyId");
						this.description = connection.getHeaderField("description");
						this.pushVersion = connection.getHeaderField("version");
						String isValid = connection.getHeaderField("isValid");
						this.isValid = Boolean.parseBoolean(isValid);
						
						logger.debug("GitPath from Header: " + gitPath);
						logger.debug("policyId from Header: " + policyId);
						logger.debug("description from Header: " + description);
						logger.debug("version from Header: " + pushVersion);
						logger.debug("isValid from Header: " + isValid);
						
						if (gitPath != null && !gitPath.equalsIgnoreCase("")) {
							return gitPath;
						} else {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "could not retrieve the gitPath from the PAP");
						}
					} else if (connection.getResponseCode() == 404) {
						logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "response code of the URL is " 
									+ connection.getResponseCode() + ". This indicates a problem with getting the gitPath from the PAP");
					} else {
						logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "BAD REQUEST:  Error occured while getting the gitPath from the PAP. The request may be incorrect.");
					}
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
					try {
						throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"ERROR in connecting to the PAP ", e);
					} catch (Exception e1) {
						logger.error(e1.getMessage());
					}
				} 
	
			} else {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get valid response from PAP(s) " + paps);
				try {
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"ERROR in connecting to the PAP ");
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}	
		}
		return gitPath;
						
	}

	// change getActiveVersion method to receive requestID to be used to send to PAP on the GET request so PAP won't generate another
//	private String getActiveVersion(String policyScope, String filePrefix, String policyName, String clientScope) {
	private String getActiveVersion(String policyScope, String filePrefix, String policyName, String clientScope, UUID requestID) {		

		//Connect to the PAP
		String version = null;
		HttpURLConnection connection = null;
		String [] parameters = {"apiflag=version","policyScope="+policyScope, "filePrefix="+filePrefix, "policyName="+policyName};
		

		// Checking for the available PDPs is done during the first Request and the List is going to have the connected PDP as first element.
		// This makes it Real-Time to change the list depending on their availability.
		if (paps == null || paps.isEmpty()) {
		logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PAPs List is Empty.");
		try {
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"PAPs List is empty.");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		}else {
			int papsCount = 0;
			boolean connected = false;
			while (papsCount < paps.size()) {
				try {
					String fullURL = paps.get(0);
					if (parameters != null && parameters.length > 0) {
						String queryString = "";
						for (String p : parameters) {
							queryString += "&" + p;
						}
						fullURL += "?" + queryString.substring(1);
					}
	
					URL url = new URL (fullURL);
					
					//Open the connection
					connection = (HttpURLConnection)url.openConnection();
					
					// Setting Content-Type
					connection.setRequestProperty("Content-Type",
							"application/json");
					
					// Adding Authorization
					connection.setRequestProperty("Authorization", "Basic "
							+ encodingPAP.get(0));
					
					connection.setRequestProperty("Environment", environment);
					connection.setRequestProperty("ClientScope", clientScope);

					
					//set the method and headers
					connection.setRequestMethod("GET");
					connection.setUseCaches(false);
					connection.setInstanceFollowRedirects(false);
					connection.setDoOutput(true);
					connection.setDoInput(true);

					// set requestID in header properties to be used to send to PAP on the GET request so PAP won't generate another
					connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
	
					//DO the connect
					connection.connect();
	
					// If Connected to PAP then break from the loop and continue with the Request 
					if (connection.getResponseCode() > 0) {
						connected = true;
						break;

					} else {
						logger.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error");
					}
				} catch (Exception e) {
					// This means that the PAP is not working 
					logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
				}
				papsCount++;
			}
	
			if (connected) {
				//Read the Response
				logger.debug("connected to the PAP : " + paps.get(0));
				logger.debug("--- Response: ---");
				Map<String, List<String>> headers = connection.getHeaderFields();
				for (String key : headers.keySet()) {
					logger.debug("Header :" + key + "  Value: " + headers.get(key));
				}
				try {
					if (connection.getResponseCode() == 200) {
						// Check for successful creation of policy
						version = connection.getHeaderField("version");
						logger.debug("ActiveVersion from the Header: " + version);
					} else if (connection.getResponseCode() == 403) {
						logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is " 
								+ connection.getResponseCode() + ". PEP is not Authorized for making this Request!! \n Contact Administrator for this Scope. ");
						version = "pe100";
					} else if (connection.getResponseCode() == 404) {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is " 
									+ connection.getResponseCode() + ". This indicates a problem with getting the version from the PAP");
						version = "pe300";
					} else {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  Error occured while getting the version from the PAP. The request may be incorrect.");
					}
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
					try {
						throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"ERROR in connecting to the PAP ", e);
					} catch (Exception e1) {
						logger.error(e1.getMessage());
					}
				} 
	
			} else {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get valid response from PAP(s) " + paps);
				try {
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE +"ERROR in connecting to the PAP ");
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}	
		}
		return version;
	}
	
	// Validation for json inputs
	public static boolean isJSONValid(String data) {
		try {
			new JSONObject(data);
			InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
			JsonReader jsonReader = Json.createReader(stream);
			logger.debug("Json Value is: " + jsonReader.read().toString() );
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/*
	 * Rotate the PDP list upon WEBsocket Failures
	 */
	public static void rotateList() {
		Collections.rotate(pdps, -1);
		Collections.rotate(encoding, -1);
		/* not required for 1510. //TODO uncomment when PAP API has been implemented
		 * This Broke the PyPDP :( Since there is no PAP LIST yet. 
		Collections.rotate(paps,  -1);
		Collections.rotate(encodingPAP, -1);
		*/
	}

	/*
	 * Get the latest PDP
	 */
	public static String getPDPURL() {
		return pdps.get(0);
	}
	
	/*
	 * Get the latest PAP
	 */
	public static String getPAPURL() {
		return paps.get(0);
	}
	
	private JsonObject stringToJsonObject(String value) throws Exception{
		JsonReader jsonReader = Json.createReader(new StringReader(value));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();
		return object;
	}
	
	private String getJsonResponseString() {
		String jsonString = "{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"}},"
				+ "\"AssociatedAdvice\":[{\"AttributeAssignment\":[{\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\","
				+ "\"Issuer\":\"\",\"AttributeId\":\"type\",\"Value\":\"Configuration\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},"
				+ "{\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"URLID\",\"Value\":"
				+ "\"$URL/Config/JunitTest.Config_testing.1.json\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#anyURI\"},{\"Category\":"
				+ "\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"PolicyName\",\"Value\":"
				+ "\"JunitTest.Config_testing.1.xml\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},{\"Category\":"
				+ "\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"VersionNumber\",\"Value\":"
				+ "\"1\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},{\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\","
				+ "\"Issuer\":\"\",\"AttributeId\":\"matching:ECOMPName\",\"Value\":\"test\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},"
				+ "{\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"matching:ConfigName\","
				+ "\"Value\":\"TestName\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},{\"Category\":"
				+ "\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"matching:service\","
				+ "\"Value\":\"ControllerServiceOpendcaeCapsuleServiceInstance\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},"
				+ "{\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"matching:uuid\","
				+ "\"Value\":\"TestUUID\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},{\"Category\":"
				+ "\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"matching:Location\","
				+ "\"Value\":\"Edge\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\"},{\"Category\":"
				+ "\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Issuer\":\"\",\"AttributeId\":\"Priority\",\"Value\":\"1\",\"DataType\":"
				+ "\"http://www.w3.org/2001/XMLSchema#string\"}],\"Id\":\"MSID\"}],\"Decision\":\"Permit\"}]}";
		
		return jsonString;
	}
	
	public PolicyChangeResponse policyEngineImport(ImportParameters importParameters) throws Exception {
		return policyEngineImport(importParameters, userName,  pass);
	}
	
	public PolicyChangeResponse policyEngineImport(ImportParameters importParameters, String userID, String passcode) throws Exception {
		StdPolicyChangeResponse response = new StdPolicyChangeResponse();
		String resource= "policyEngineImport";
		if(!checkPermissions(userID, passcode, resource)){
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseMessage(XACMLErrorConstants.ERROR_PERMISSIONS + "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to:" + resource);
			response.setResponseCode(401);
			return response;
		}
		InputStream targetStream = null;
		if(importParameters.getServiceName()!=null && importParameters.getVersion()!=null && importParameters.getServiceType()!=null){
			// This is Config Class Policy. 
			if(importParameters.getFilePath()!=null){
				File input = new File(importParameters.getFilePath());
				if (input.getName().endsWith(".xmi") ||  input.getName().endsWith(".zip")){
				    try {
				    	if (input.exists()){
				    		targetStream = new FileInputStream(input);
				    	}else {
							logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "File provided in ImportParameters does not exists.");
							response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "File provided in ImportParameters does not exist.");		
							return response;
				    	}
					} catch (Exception e) {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error reading in File");
						response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in reading in the file provided");
					}	
				}else{
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect File Data type.");
					response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect File Type Given. Please use a file of type .xmi or .zip.");		
					return response;
				}
				String[] parameters = new String[] {"importService=" + importParameters.getServiceType(), "serviceName=" 
						+ importParameters.getServiceName(), "fileName=" + input.getName(), "version=" + importParameters.getVersion()};
				String responseMessage =  (String) callPAP(targetStream, parameters, importParameters.getRequestID(), "importMS");
				response.setResponseMessage(responseMessage);
			}else{
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing required ImportParameters value.");
				response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing required ImportParameters value.");		
			}
		}else{
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing required ImportParameters value.");
			response.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing required ImportParameters value.");		
		}	
		return response;
	}
	
	/*
	 * Give userID, Passcode and the Resoruce they are requesting for. 
	 */
	private boolean checkPermissions(String userID, String passcode, String resource){
		Boolean result = false;
		if(pyPDPClientFile!=null){
			// Backward compatible pyPDP called us. So validate the user names and scope.
			Path clientPath = Paths.get(pyPDPClientFile);
			if (Files.notExists(clientPath)) {
				result = false;
			}else if(clientPath.toString().endsWith(".properties")) {
				try {
					HashMap<String, ArrayList<String>> clientMap = readProps(clientPath);
					if (clientMap.containsKey(userID) && clientMap.get(userID).get(0).equals(passcode)) {
						result= true;
					}
				} catch (Exception e) {
					result = false;
				}
			}
		}else{
			//Allowing Every Client who ever don't have access for AAF and Backup Client file
			result = true;
		}
		return result;
	}
	
	private HashMap<String, ArrayList<String>> readProps(Path clientPath) throws Exception{
		InputStream in;
		Properties clientProp = new Properties();
		try {
			in = new FileInputStream(clientPath.toFile());
			clientProp.load(in);
		} catch (IOException e) {
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			throw new Exception(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Cannot Load the Properties file", e);
		}
		// Read the Properties and Load the Clients and their scopes.
		HashMap<String, ArrayList<String>>clientMap = new HashMap<String, ArrayList<String>>();
		// 
		for (Object propKey : clientProp.keySet()) {
			String clientID = (String)propKey; 
			String clientValue = clientProp.getProperty(clientID);
			if (clientValue != null) {
				if (clientValue.contains(",")) {
					ArrayList<String> clientValues = new ArrayList<String>(Arrays.asList(clientValue.split("\\s*,\\s*")));
					if(clientValues.get(0)!=null || clientValues.get(1)!=null || clientValues.get(0).isEmpty() || clientValues.get(1).isEmpty()){
						clientMap.put(clientID, clientValues);
					}
				} 
			}
		}
		if (clientMap == null || clientMap.isEmpty()) {
			logger.debug(XACMLErrorConstants.ERROR_PERMISSIONS + "No Clients ID , Client Key and Scopes are available. Cannot serve any Clients !!");
			throw new Exception("Empty Client file");
		}
		return clientMap;
	}
	
	protected boolean isNumeric(String str)
	{
		for (char c : str.toCharArray())
		{
			if (!Character.isDigit(c)) return false;
		}
		return true;
	} 
	
	private String ConvertDate(Date date){
		String strDate = null;
		if (date!=null)
		{
			SimpleDateFormat dateformatJava = new SimpleDateFormat("dd-MM-yyyy");
			strDate = dateformatJava.format(date);
		}
		
		return strDate;
	}
}
