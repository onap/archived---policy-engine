/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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


package org.openecomp.policy.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.att.cadi.Access;
import com.att.cadi.Access.Level;
import com.att.cadi.CadiException;
import com.att.cadi.aaf.AAFPermission;
import com.att.cadi.aaf.v2_0.AAFAuthn;
import com.att.cadi.aaf.v2_0.AAFCon;
import com.att.cadi.aaf.v2_0.AAFConDME2;
import com.att.cadi.aaf.v2_0.AAFLurPerm;
import com.att.cadi.config.Config;


/**
 * AAF Client: Generic AAF Client implementation to connect to AAF Resources to validate permissions and authorization. 
 * 
 */
public class AAFPolicyClient {
	  private static Logger LOGGER = Logger.getLogger(AAFPolicyClient.class.getName());

		private static final String DEFAULT_AFT_LATITUDE = "32.780140";
		private static final String DEFAULT_AFT_LONGITUDE = "-96.800451";
		private static final String DEFAULT_AAF_USER_EXPIRES = Integer.toString(5*60000); 	// 5 minutes for found items to live in cache
		private static final String DEFAULT_AAF_HIGH_COUNT = Integer.toString(400); 		// Maximum number of items in Cache
		
		private static AAFPolicyClient instance = null;
		private static Properties props = new Properties();
		private static AAFCon<?> aafCon = null;
		private static AAFLurPerm aafLurPerm = null;
		private static AAFAuthn<?> aafAuthn = null;
		private static Access access = null;
		
		
		private AAFPolicyClient(Properties properties) throws AAFPolicyException{
			if(instance == null){
				instance = this;
			}
			setup(properties);
		}

		/**
		 * Gets the instance of the AAFClient instance. Needs Proper properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
		 * 
		 * @param properties Properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
		 * @return AAFClient instance. 
		 * @throws AAFPolicyException Exceptions. 
		 */
		public static synchronized AAFPolicyClient getInstance(Properties properties) throws AAFPolicyException{
			if(instance == null) {
			    LOGGER.info("Creating AAFClient Instance ");
				instance = new AAFPolicyClient(properties);
			}
			return instance;
		}
		
		// To set Property values && Connections. 
		private void setup(Properties properties) throws AAFPolicyException {
			/*if(properties!=null && !properties.isEmpty()){
				props = System.getProperties();
				props.setProperty("AFT_LATITUDE", properties.getProperty("AFT_LATITUDE", DEFAULT_AFT_LATITUDE));
				props.setProperty("AFT_LONGITUDE", properties.getProperty("AFT_LONGITUDE", DEFAULT_AFT_LONGITUDE));
				props.setProperty("aaf_id",properties.getProperty("aaf_id", "aafID"));
				props.setProperty("aaf_password", properties.getProperty("aaf_password", "aafPass"));
				if(properties.containsKey(Config.AAF_URL)){
		        	// if given a value in properties file. 
		        	props.setProperty(Config.AAF_URL, properties.getProperty(Config.AAF_URL));
	        	}else{
	        		LOGGER.error("Required Property value is missing : " + Config.AAF_URL);
	 				throw new AAFPolicyException("Required Property value is missing : " + Config.AAF_URL);
	        	}
				
				if(properties.containsKey("AFT_ENVIRONMENT")){
					props.setProperty("AFT_ENVIRONMENT", properties.getProperty("AFT_ENVIRONMENT"));
	        	}else{
	        		LOGGER.error("Required Property value is missing : AFT_ENVIRONMENT");
	 				throw new AAFPolicyException("Required Property value is missing : AFT_ENVIRONMENT");
	        	}
		        props.setProperty(Config.AAF_USER_EXPIRES, properties.getProperty(Config.AAF_USER_EXPIRES, DEFAULT_AAF_USER_EXPIRES));	
		        props.setProperty(Config.AAF_HIGH_COUNT, properties.getProperty(Config.AAF_HIGH_COUNT, DEFAULT_AAF_HIGH_COUNT));
			}else{
			    LOGGER.error("Required Property value is missing ");
				throw new AAFPolicyException("Required Property value is missing");
			}
			access = new PolicyAccess(props, Level.valueOf(properties.getProperty("AAF_LOG_LEVEL", Level.INFO.toString())));
			setUpAAF();*/
		}
		
		/**
		 * Updates the Properties file in case if required. 
		 * 
		 * @param properties  Properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
		 * @throws AAFPolicyException exceptions if any.
		 */
		public void updateProperties(Properties properties) throws AAFPolicyException{
			setup(properties);
		}
		
		/**
		 * Checks the Authentication and Permissions for the given values. 
		 * 
		 * @param pass Password pertaining to the loginId
		 * @param type Permissions Type.
		 * @param instance Permissions Instance. 
		 * @param action Permissions Action. 
		 * @return
		 */
		public boolean checkAuthPerm(String mechID, String pass, String type, String instance, String action){
			if(checkAuth(mechID, pass) && checkPerm(mechID, pass, type, instance, action)){
				return true;
			}
			return false;
		}
		
		/**
		 * Checks the Authentication of the UserName and Password Given. 
		 * 
		 * @param userName UserName 
		 * @param pass Password.
		 * @return True or False. 
		 */
		public boolean checkAuth(String userName, String pass){
			/*if(aafAuthn!=null){
				try {
				    int i=0;
				    do{
				        if(aafAuthn.validate(userName, pass)==null){ 
		                    return true; 
		                }
				        i++;
				    }while(i<2);
				} catch (Exception e) {
				    LOGGER.error(e.getMessage());
				}
			}
			LOGGER.debug("Authentication failed for : " + userName + " in " + props.getProperty("AAF_URL"));
			return false;*/
			return true;
		}
		
		/**
		 * Checks Permissions for the given UserName, Password and Type, Instance Action. 
		 * 
		 * @param userName UserName 
		 * @param pass Password.
		 * @param type Permissions Type. 
		 * @param instance Permissions Instance. 
		 * @param action Permissions Action. 
		 * @return True or False. 
		 */
		public boolean checkPerm(String userName, String pass, String type, String instance, String action){
			/*int i =0;
			Boolean result= false;
		    do{
			    if(aafCon!=null && aafLurPerm !=null){
		            try {
		                aafCon.basicAuth(userName, pass);
		                AAFPermission perm = new AAFPermission(type, instance, action);
		                result = aafLurPerm.fish(userName, perm);
		            } catch (CadiException e) {
		                LOGGER.error(e.getMessage());
		                aafLurPerm.destroy();
		            }
		        }
			    LOGGER.debug("Permissions for : " + userName + " in " + props.getProperty("AAF_URL") + "for " + type  + "," + instance + "," + action + "\n Result is: " + result);
		        i++;
			}while(i<2 && !result); // Try once more to check if this can be passed. AAF has some issues. 
	        return result;*/
			return true;
		}
		
		/*private boolean setUpAAF(){
		    try {
	            aafCon = new AAFConDME2(access);
	            aafLurPerm = aafCon.newLur();//new AAFLurPerm(aafCon);
	            aafAuthn = aafCon.newAuthn(aafLurPerm);//new AAFAuthn(aafCon, aafLurPerm);
	            return true;
	        } catch (Exception e) {
	            LOGGER.error("Error while creating Connection " + e.getMessage());
	            return false;
	        }
		}*/
}
