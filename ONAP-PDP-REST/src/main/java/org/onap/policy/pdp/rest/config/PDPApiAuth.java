/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.utils.AAFPolicyClient;
import org.onap.policy.utils.AAFPolicyException;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.util.XACMLProperties;

public class PDPApiAuth {
	private static final Logger LOGGER = FlexLogger.getLogger(PDPApiAuth.class);

	private static String environment = null;
	private static Path clientPath = null;
	private static Map<String,ArrayList<String>> clientMap = null;
	private static Long oldModified = null;
	private static AAFPolicyClient aafClient = null;

	private PDPApiAuth(){
		// Private Constructor
	}

	/*
	 * Set Property by reading the properties File.
	 */
	public static void setProperty() {
		environment = XACMLProperties.getProperty("ENVIRONMENT", "DEVL");
		String clientFile = XACMLProperties.getProperty(XACMLRestProperties.PROP_PEP_IDFILE);
		if(clientFile!=null){
			clientPath = Paths.get(clientFile);
		}
		try {
			aafClient = AAFPolicyClient.getInstance(XACMLProperties.getProperties());
		} catch (AAFPolicyException | IOException e) {
			LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "AAF Client Not instantiated properly.");
		}
	}

	/*
	 * Return Environment value of the PDP servlet. 
	 */
	public static String getEnvironment() {
		if(environment==null){
			setProperty();
		}
		return environment;
	}

	/*
	 * Security check for authentication and authorizations. 
	 */
	public static boolean checkPermissions(String clientEncoding, String requestID,
			String resource) {
		try{
			String[] userNamePass = PolicyUtils.decodeBasicEncoding(clientEncoding);
			if(userNamePass==null || userNamePass.length==0){
				String usernameAndPassword = null;
				byte[] decodedBytes = Base64.getDecoder().decode(clientEncoding);
				usernameAndPassword = new String(decodedBytes, "UTF-8");
				StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
				String username = tokenizer.nextToken();
				String password = tokenizer.nextToken();
				userNamePass=  new String[]{username,  password};
			}
			LOGGER.info("User " + userNamePass[0] + " is Accessing Policy Engine API.");
			Boolean result = false;
			// Check Backward Compatibility. 
			try{
				result = clientAuth(userNamePass);
			}catch(Exception e){
				LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
			}
			if(!result){
				String aafPolicyNameSpace = XACMLProperties.getProperty("policy.aaf.namespace");
				String aafResource = XACMLProperties.getProperty("policy.aaf.root.permission");
				if(!userNamePass[0].contains("@") && aafPolicyNameSpace!= null){
				    userNamePass[0] = userNamePass[0] + "@" + reverseNS(aafPolicyNameSpace);
				}else{
					LOGGER.info("No AAF NameSpace specified in properties");
				}
				if(aafResource != null){
					resource = aafResource + "." + resource;
				}else{
					LOGGER.info("No AAF Resource specified in properties");
				}
				LOGGER.info("Contacting AAF in : "  + environment);
				result = aafClient.checkAuthPerm(userNamePass[0], userNamePass[1], resource, environment, "*");
			}
			return result;
		}catch(Exception e){
			LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
			return false;
		}
	}
	
	private static Boolean clientAuth(String[] userNamePass){
		if(clientPath==null){
			setProperty();
		}
		if (!clientPath.toFile().exists()) {
			return false;
		}else if(clientPath.toString().endsWith(".properties")) {
			try {
				readProps(clientPath);
				if (clientMap.containsKey(userNamePass[0]) && clientMap.get(userNamePass[0]).get(0).equals(userNamePass[1])) {
					return true;
				}
			}catch(PolicyEngineException e){
				LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
				return false;
			}
		}
		return false;
	}
	
	private static String reverseNS(String namespace) {
	    final List<String> components = Arrays.asList(namespace.split("\\."));
	    Collections.reverse(components);   
	    return String.join(".", components);
	}

	private static Map<String, ArrayList<String>> readProps(Path clientPath) throws PolicyEngineException{
		if(oldModified!=null){
			Long newModified = clientPath.toFile().lastModified();
			if (newModified == oldModified) {
				return clientMap;
			}
		}
		InputStream in;
		Properties clientProp = new Properties();
		try {
			in = new FileInputStream(clientPath.toFile());
			clientProp.load(in);
		} catch (IOException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR , e);
			throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Cannot Load the Properties file", e);
		}
		// Read the Properties and Load the Clients and their scopes.
		clientMap = new HashMap<>();
		// 
		for (Object propKey : clientProp.keySet()) {
			String clientID = (String)propKey; 
			String clientValue = clientProp.getProperty(clientID);
			if (clientValue != null && clientValue.contains(",")) {
				ArrayList<String> clientValues = new ArrayList<>(Arrays.asList(clientValue.split("\\s*,\\s*")));
				if(clientValues.get(0)!=null || clientValues.get(1)!=null || clientValues.get(0).isEmpty() || clientValues.get(1).isEmpty()){
					clientMap.put(clientID, clientValues);
				}
			}
		}
		if (clientMap.isEmpty()) {
			LOGGER.debug(XACMLErrorConstants.ERROR_PERMISSIONS + "No Clients ID , Client Key and Scopes are available. Cannot serve any Clients !!");
			throw new PolicyEngineException("Empty Client file");
		}
		oldModified = clientPath.toFile().lastModified();
		return clientMap;
	}
}