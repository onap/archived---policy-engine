/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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
package org.openecomp.policy.pdp.rest.config;

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
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.openecomp.policy.api.PolicyEngineException;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.utils.AAFPolicyClient;
import org.openecomp.policy.utils.AAFPolicyException;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.util.XACMLProperties;

public class PDPApiAuth {
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
	            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "AAF Client Not instantiated properly.");
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
	            PolicyLogger.info("User " + userNamePass[0] + " is Accessing Policy Engine API.");
	            Boolean result = false;
	            // Check Backward Compatibility. 
	            try{
	                result = clientAuth(userNamePass);
	            }catch(Exception e){
	                PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS, e, "");
	            }
	            if(!result){
	                try{
	                	String aafPolicyNameSpace = XACMLProperties.getProperty("policy.aaf.namespace");
	                	String aafResource = XACMLProperties.getProperty("policy.aaf.resource");
	                    if(!userNamePass[0].contains("@") && aafPolicyNameSpace!= null){
	                        userNamePass[0] = userNamePass[0] + "@" + aafPolicyNameSpace;
	                    }
	                    if(aafResource != null){
	                    	 resource = aafResource + resource;
	                    }
	                    PolicyLogger.info("Contacting AAF in : "  + environment);
	                    result = aafClient.checkAuthPerm(userNamePass[0], userNamePass[1], resource, environment, ".*");
	                }catch (NullPointerException e){
	                    result = false;
	                }
	            }
	            return result;
	        }catch(Exception e){
	            PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS, e, "");
	            return false;
	        }
	    }

	    private static Boolean clientAuth(String[] userNamePass) throws Exception{
	        if(clientPath==null){
	            setProperty();
	        }
	        if (Files.notExists(clientPath)) {
	            return false;
	        }else if(clientPath.toString().endsWith(".properties")) {
	            try {
	                readProps(clientPath);
	                if (clientMap.containsKey(userNamePass[0]) && clientMap.get(userNamePass[0]).get(0).equals(userNamePass[1])) {
	                    return true;
	                }
	            }catch(PolicyEngineException e){
	                return false;
	            }
	        }
	        return false;
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
	            PolicyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
	            throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Cannot Load the Properties file", e);
	        }
	        // Read the Properties and Load the Clients and their scopes.
	        clientMap = new HashMap<>();
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
	        if (clientMap.isEmpty()) {
	            PolicyLogger.debug(XACMLErrorConstants.ERROR_PERMISSIONS + "No Clients ID , Client Key and Scopes are available. Cannot serve any Clients !!");
	            throw new PolicyEngineException("Empty Client file");
	        }
	        oldModified = clientPath.toFile().lastModified();
	        return clientMap;
	    }
}
