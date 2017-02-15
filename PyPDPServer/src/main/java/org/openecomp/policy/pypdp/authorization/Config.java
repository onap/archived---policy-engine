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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.im.IntegrityMonitor;


public class Config {
	private static final String propertyFilePath = "config.properties";
	private static Properties prop = new Properties();
	private static List<String> pdps = null;
	private static List<String> paps = null;
	private static List<String> encoding = null;
	private static List<String> encodingPAP = null;
	private static String pyPDPPass = null;
	private static String pyPDPID = null;
	private static String environment = null;
	private static final Log logger = LogFactory.getLog(Config.class);
	private static String clientFile = null;
	private static boolean test = false;
	
	private static IntegrityMonitor im;
	private static String resourceName = null;
	
	public static String getProperty(String propertyKey) {
		return prop.getProperty(propertyKey);
	}

	/*
	 * Set Property by reading the properties File.
	 */
	public static void setProperty() {
		Path file = Paths.get(propertyFilePath);
		if (Files.notExists(file)) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+ "File doesn't exist in the specified Path "+ file.toString());
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "File doesn't exist in the specified Path "+ file.toString());
		} else {
			InputStream in;
			prop = new Properties();
			try {
				in = new FileInputStream(file.toFile());
				prop.load(in);
			} catch (IOException e) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Cannot Load the Properties file" + e);
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "Cannot Load the Properties file");
			}
		}
		// Initializing the values.
		pdps = new ArrayList<String>();
		paps = new ArrayList<String>();
		encoding = new ArrayList<String>();
		encodingPAP = new ArrayList<String>();
		
		// Check the Keys for PDP_URLs
		Collection<Object> unsorted = prop.keySet();
		List<String> sorted = new ArrayList(unsorted);
		Collections.sort(sorted);
		for (String propKey : sorted) {
			if (propKey.startsWith("PDP_URL")) {
				String check_val = prop.getProperty(propKey);
				logger.debug("Property file value for Key : \"" + propKey + "\" Value is : \"" + check_val + "\"");
				if (check_val == null) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Properties file doesn't have the PDP_URL parameter");
					// TODO:EELF Cleanup - Remove logger
					PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Properties file doesn't have the PDP_URL parameter");
				}	
				if (check_val.contains(";")) {
					List<String> pdp_default = new ArrayList<String>(Arrays.asList(check_val.split("\\s*;\\s*")));
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
				logger.debug("Property file value for Key : \"" + propKey + "\" Value is : \"" + check_val + "\"");
				if (check_val == null) {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Properties file doesn't have the PAP_URL parameter");
					// TODO:EELF Cleanup - Remove logger
					PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Properties file doesn't have the PAP_URL parameter");
				}
				if (check_val.contains(";")) {
					List<String> pap_default = new ArrayList<String>(Arrays.asList(check_val.split("\\s*;\\s*")));
					int papCount=0;
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
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Cannot Proceed without PDP_URLs");
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Cannot Proceed without PDP_URLs");
		}
		
		if (prop.containsKey("PYPDP_ID")) {
			String id = prop.getProperty("PYPDP_ID");
			logger.debug("Property file value key: \"PYPDP_ID\" Value is : \"" + id + "\"");
			if (id == null) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Properties file doesn't have PYPDP_ID parameter");
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Properties file doesn't have PYPDP_ID parameter");
			}
			Config.pyPDPID = id;
		} else {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Properties file doesn't have PYPDP_ID parameter");
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Properties file doesn't have PYPDP_ID parameter");
		}
		if (prop.containsKey("PYPDP_PASSWORD")) {
			String pass = prop.getProperty("PYPDP_PASSWORD");
			logger.debug("Property file value key: \"PYPDP_PASSWORD\" Value is : \"" + pass + "\"");
			if (pass == null) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Properties file doesn't have PYPDP_PASSWORD parameter");
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Properties file doesn't have PYPDP_PASSWORD parameter");
			}
			Config.pyPDPPass = pass;
		} else {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Properties file doesn't have PYPDP_PASSWORD parameter");
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Properties file doesn't have PYPDP_PASSWORD parameter");
		}
		environment = prop.getProperty("ENVIRONMENT", "DEVL");
		logger.info("Property value for Environment " + environment);
		String value = prop.getProperty("Test");
		if(value!= null && value.equalsIgnoreCase("true")){
			test = true;
		}
		if(prop.containsKey("CLIENT_FILE")){
			clientFile = prop.getProperty("CLIENT_FILE");
			logger.debug("Property file value key: \"CLIENT_FILE\" Value is : \"" + clientFile + "\"");
			if(clientFile == null){
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"CLIENT_FILE value is missing.");
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "CLIENT_FILE value is missing.");
			}
		}else{
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"CLIENT_FILE paramter is missing from the property file.");
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "CLIENT_FILE paramter is missing from the property file.");
		}
		logger.info("Trying to set up IntegrityMonitor");
		try {
			logger.info("Trying to set up IntegrityMonitor");
			resourceName = prop.getProperty("RESOURCE_NAME").replaceAll(" ", "");;
			if(resourceName==null){
				logger.warn("RESOURCE_NAME is missing setting default value. ");
				resourceName = "pypdp_pdp01";
			}
			im = IntegrityMonitor.getInstance(resourceName, prop);
		} catch (Exception e) {
			logger.error("Error starting Integerity Monitor: " + e);
		}
	}

	private static void readPDPParam(String pdpVal) {
		if (pdpVal.contains(",")) {
			List<String> pdpValues = new ArrayList<String>(Arrays.asList(pdpVal.split("\\s*,\\s*")));
			if (pdpValues.size() == 3) {
				// 0 - PDPURL
				pdps.add(pdpValues.get(0));
				// 1:2 will be UserID:Password
				String userID = pdpValues.get(1);
				String pass = pdpValues.get(2);
				Base64.Encoder encoder = Base64.getEncoder();
				encoding.add(encoder.encodeToString((userID + ":" + pass)
						.getBytes(StandardCharsets.UTF_8)));
			} else {
				logger.error(XACMLErrorConstants.ERROR_PERMISSIONS+"No enough Credentials to send Request. "+ pdpValues);
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No enough Credentials to send Request. "+ pdpValues);
			}
		} else {
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS+"No enough Credentials to send Request.");
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No enough Credentials to send Request.");
		}
	}
	
	private static void readPAPParam(String papVal) {
		if (papVal.contains(",")) {
			List<String> papValues = new ArrayList<String>(Arrays.asList(papVal.split("\\s*,\\s*")));
			if (papValues.size() == 3) {
				// 0 - PAPURL
				paps.add(papValues.get(0));
				// 1:2 will be UserID:Password
				String userID = papValues.get(1);
				String pass = papValues.get(2);
				Base64.Encoder encoder = Base64.getEncoder();
				encodingPAP.add(encoder.encodeToString((userID + ":" + pass)
						.getBytes(StandardCharsets.UTF_8)));
			} else {
				logger.error(XACMLErrorConstants.ERROR_PERMISSIONS+"Not enough Credentials to send Request. "+ papValues);
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS, "Not enough Credentials to send Request. "+ papValues);
			}
		} else {
			logger.error(XACMLErrorConstants.ERROR_PERMISSIONS+"Not enough Credentials to send Request.");
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS, "Not enough Credentials to send Request.");
		}
	}

	public static List<String> getPDPs() {
		setProperty();
		return Config.pdps;
	}
	
	public static List<String> getPAPs() {
		setProperty();
		return Config.paps;
	}

	public static List<String> getEncoding() {
		return Config.encoding;
	}
	
	public static List<String> getEncodingPAP() {
		return Config.encodingPAP;
	}

	public static String getPYPDPID() {
		return Config.pyPDPID;
	}

	public static String getPYPDPPass() {
		return Config.pyPDPPass;
	}
	
	public static String getEnvironment(){
		return Config.environment;
	}
	
	public static IntegrityMonitor getIntegrityMonitor(){
		if(im==null){
			setProperty();
		}
		return im;
	}
	
	public static String getClientFile() {
		return Config.clientFile;
	}

	public static Boolean isTest() {
		return Config.test;
	}
}
