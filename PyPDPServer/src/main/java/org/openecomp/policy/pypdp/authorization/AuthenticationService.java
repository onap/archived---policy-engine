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

package org.openecomp.policy.pypdp.authorization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

public class AuthenticationService {
	private String pyPDPID = Config.getPYPDPID();
	private String pyPDPPass = Config.getPYPDPPass();
	private static Path clientPath = null;
	private static HashMap<String, ArrayList<String>> clientMap = null;
	private static Long oldModified = null;
	private static Long newModified = null;
	private static final Log logger = LogFactory.getLog(AuthenticationService.class);
	
	public boolean authenticate(String authCredentials) {

		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic authentication. 
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"	+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "");
			return false;
		}
		try {
			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();

			boolean authenticationStatus = pyPDPID.equals(username)	&& pyPDPPass.equals(password);
			return authenticationStatus;
		} catch (Exception e){
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
			return false;
		}
	}
	
	public static boolean clientAuth(String clientCredentials) {
		if(clientCredentials == null){
			return false; 
		}
		// Decode the encoded Client Credentials. 
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(clientCredentials);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "");
			return false;
		}
		try {
			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken(); 
			return checkClient(username,password);
		} catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "");
			return false;
		}
	}
	
	public static boolean checkClientScope(String clientCredentials, String scope) {
		if(clientCredentials == null){
			return false; 
		}
		// Decode the encoded Client Credentials. 
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(clientCredentials);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "");
			return false;
		}
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		// Read the properties and compare.
		try{
			readFile();
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
			return false;
		}
		// Check ID, Scope
		if (clientMap.containsKey(username) && (clientMap.get(username).get(1).equals(scope) || clientMap.get(username).get(1).equals("MASTER"))) {
			return true;
		}
		return false;
	}
	
	private static boolean checkClient(String username, String password) {
		// Read the properties and compare.
		try{
			readFile();
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
			return false;
		}
		// Check ID, Key
		if (clientMap.containsKey(username) && clientMap.get(username).get(0).equals(password)) {
			return true;
		}
		return false;
	}	
	
	private static void readFile() throws Exception {
		String clientFile =  Config.getClientFile();
		if (clientFile == null) {
			Config.setProperty();
			if(clientFile == null){
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Missing CLIENT_FILE property value: " + clientFile);
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, "Missing CLIENT_FILE property value: " + clientFile);
				throw new Exception(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Missing CLIENT_FILE property value: " + clientFile);
			}
		}
		if (clientPath == null) {
			clientPath = Paths.get(clientFile);
			if (Files.notExists(clientPath)) {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "File doesn't exist in the specified Path : "	+ clientPath.toString());
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, "File doesn't exist in the specified Path : "	+ clientPath.toString());
				throw new Exception(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"File doesn't exist in the specified Path : "+ clientPath.toString());
			} 
			if (clientPath.toString().endsWith(".properties")) {
				readProps();
			} else {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Not a .properties file " + clientFile);
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, "Not a .properties file " + clientFile);
				throw new Exception(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Not a .properties file " + clientFile);
			}
		}
		// Check if File is updated recently
		else {
			newModified = clientPath.toFile().lastModified();
			if (newModified != oldModified) {
				// File has been updated.
				readProps();
			}
		}
	}

	private static void readProps() throws Exception{
		InputStream in;
		Properties clientProp = new Properties();
		try {
			in = new FileInputStream(clientPath.toFile());
			oldModified = clientPath.toFile().lastModified();
			clientProp.load(in);
		} catch (IOException e) {
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
			throw new Exception(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Cannot Load the Properties file", e);
			
		}
		// Read the Properties and Load the PDPs and encoding.
		clientMap = new HashMap<String, ArrayList<String>>();
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
		}
	}
}
