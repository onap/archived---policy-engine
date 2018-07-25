/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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
package org.onap.policy.utils;

import java.security.Principal;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.onap.aaf.cadi.Access.Level;
import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.cadi.aaf.AAFPermission;
import org.onap.aaf.cadi.aaf.v2_0.AAFAuthn;
import org.onap.aaf.cadi.aaf.v2_0.AAFCon;
import org.onap.aaf.cadi.aaf.v2_0.AAFConHttp;
import org.onap.aaf.cadi.aaf.v2_0.AAFLurPerm;
import org.onap.aaf.cadi.config.Config;
import org.onap.aaf.cadi.locator.PropertyLocator;
import org.onap.aaf.cadi.principal.UnAuthPrincipal;



/**
 * AAF Client: Generic AAF Client implementation to connect to AAF Resources to validate permissions and authorization. 
 * 
 */
public class AAFPolicyClientImpl implements AAFPolicyClient{
    private static Logger logger = Logger.getLogger(AAFPolicyClientImpl.class.getName());

    private static final String ENVIRONMENT = "ENVIRONMENT";

    // Warning Please don't Change these Values. Confirm with AAF team.
    private static final String DEVL_AAF_URL = "";
    private static final String TEST_AAF_URL = "";
    private static final String PROD_AAF_URL = "";
    private static final String DEFAULT_AFT_LATITUDE = "32.780140";
    private static final String DEFAULT_AFT_LONGITUDE = "-96.800451";
    private static final String TEST_AFT_ENVIRONMENT = "AFTUAT";
    private static final String PROD_AFT_ENVIRONMENT = "AFTPRD";
    private static final String DEFAULT_AAF_USER_EXPIRES = Integer.toString(5*60000); 	// 5 minutes for found items to live in cache
    private static final String DEFAULT_AAF_HIGH_COUNT = Integer.toString(400); 		// Maximum number of items in Cache

    private static AAFPolicyClientImpl instance = null;

    private static Properties props = new Properties();
    private static AAFCon<?> aafCon = null;
    private static AAFLurPerm aafLurPerm = null;
    private static AAFAuthn<?> aafAuthn = null;
    private static PropAccess access = null;

    private AAFPolicyClientImpl(Properties properties) throws AAFPolicyException{
        setup(properties);
    }

    /**
     * Gets the instance of the AAFClient instance. Needs Proper properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
     *
     * @param properties Properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
     * @return AAFClient instance.
     * @throws AAFPolicyException Exceptions.
     */
    public static synchronized AAFPolicyClientImpl getInstance(Properties properties) throws AAFPolicyException{
        if(instance == null) {
            logger.info("Creating AAFClient Instance ");
            instance = new AAFPolicyClientImpl(properties);
        }
        return instance;
    }

    // To set Property values && Connections.
    private static void setup(Properties properties) throws AAFPolicyException {
        if(properties!=null && !properties.isEmpty()){
            props = System.getProperties();
            props.setProperty("AFT_LATITUDE", properties.getProperty("AFT_LATITUDE", DEFAULT_AFT_LATITUDE));
            props.setProperty("AFT_LONGITUDE", properties.getProperty("AFT_LONGITUDE", DEFAULT_AFT_LONGITUDE));
            String aftEnv = TEST_AFT_ENVIRONMENT;
            props.setProperty("aaf_id",properties.getProperty("aaf_id", "aafID"));
            props.setProperty("aaf_password", properties.getProperty("aaf_password", "aafPass"));
            if(properties.containsKey(Config.AAF_URL)){
                // if given a value in properties file.
                props.setProperty(Config.AAF_URL, properties.getProperty(Config.AAF_URL));
            }else{
                // Set Default values.
                if(properties.getProperty(ENVIRONMENT, "DEVL").equalsIgnoreCase(AAFEnvironment.TEST.toString())){
                    props.setProperty(Config.AAF_URL, TEST_AAF_URL);
                }else if(properties.getProperty(ENVIRONMENT, "DEVL").equalsIgnoreCase(AAFEnvironment.PROD.toString())){
                    props.setProperty(Config.AAF_URL, PROD_AAF_URL);
                    aftEnv = PROD_AFT_ENVIRONMENT;
                }else{
                    props.setProperty(Config.AAF_URL, DEVL_AAF_URL);
                }
            }
            props.setProperty("AFT_ENVIRONMENT", properties.getProperty("AFT_ENVIRONMENT", aftEnv));
            props.setProperty(Config.AAF_USER_EXPIRES, properties.getProperty(Config.AAF_USER_EXPIRES, DEFAULT_AAF_USER_EXPIRES));
            props.setProperty(Config.AAF_HIGH_COUNT, properties.getProperty(Config.AAF_HIGH_COUNT, DEFAULT_AAF_HIGH_COUNT));
        }else{
            logger.error("Required Property value is missing : " + ENVIRONMENT);
            throw new AAFPolicyException("Required Property value is missing : " + ENVIRONMENT);
        }
        access = new PolicyAccess(props, Level.valueOf(properties.getProperty("AAF_LOG_LEVEL", Level.ERROR.toString())));
        setUpAAF();
    }

    /**
     * Updates the Properties file in case if required.
     *
     * @param properties  Properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
     * @throws AAFPolicyException exceptions if any.
     */
    @Override
    public void updateProperties(Properties properties) throws AAFPolicyException{
        setup(properties);
    }

    /**
     * Checks the Authentication and Permissions for the given values.
     *
     * @param mechID MechID or ATT ID must be registered under the Name space.
     * @param pass Password pertaining to the MechID or ATTID.
     * @param type Permissions Type.
     * @param instance Permissions Instance.
     * @param action Permissions Action.
     * @return
     */
    @Override
    public boolean checkAuthPerm(String mechID, String pass, String type, String instance, String action){
        return checkAuth(mechID, pass) && checkPerm(mechID, pass, type, instance, action);
    }

    /**
     * Checks the Authentication of the UserName and Password Given.
     *
     * @param userName UserName or MechID
     * @param pass Password.
     * @return True or False.
     */
    @Override
    public boolean checkAuth(String userName, String pass){
        if (aafAuthn == null) {
            return false;
        }
        try {
            int i=0;
            do{
                if(aafAuthn.validate(userName, pass)==null){
                    return true;
                }
                i++;
            }while(i<2);
        } catch (Exception e) {
            logger.error(e.getMessage() + e);
        }

        return false;
    }

    /**
     * Checks Permissions for the given UserName, Password and Type, Instance Action.
     *
     * @param userName UserName or MechID
     * @param pass Password.
     * @param type Permissions Type.
     * @param instance Permissions Instance.
     * @param action Permissions Action.
     * @return True or False.
     */
    @Override
    public boolean checkPerm(String userName, String pass, String type, String instance, String action){
        int i =0;
        Boolean result= false;
        do{
            if(aafCon!=null && aafLurPerm !=null){
                try {
                    aafCon.basicAuth(userName, pass);
                    AAFPermission perm = new AAFPermission(type, instance, action);
                    final Principal p = new UnAuthPrincipal(userName);
                    result = aafLurPerm.fish(p, perm);
                } catch (CadiException e) {
                    logger.error(e.getMessage() + e);
                    aafLurPerm.destroy();
                }
            }
            i++;
        }while(i<2 && !result); // Try once more to check if this can be passed. AAF has some issues.
        return result;
    }

    private static boolean setUpAAF(){
        try {
            aafCon = new AAFConHttp(access,new PropertyLocator("https://aaf-onap-beijing-test.osaaf.org:8100"));
            aafLurPerm = aafCon.newLur();
            aafAuthn = aafCon.newAuthn(aafLurPerm);
            return true;
        } catch (Exception e) {
            logger.error("Error while setting up AAF Connection " + e.getMessage() + e);
            return false;
        }
    }
}
