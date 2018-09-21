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
import org.onap.aaf.cadi.locator.PropertyLocator;
import org.onap.aaf.cadi.principal.UnAuthPrincipal;

/**
 * AAF Client: Generic AAF Client implementation to connect to AAF Resources to
 * validate permissions and authorization.
 * 
 */
public class AAFPolicyClientImpl implements AAFPolicyClient {
    private static Logger logger = Logger.getLogger(AAFPolicyClientImpl.class.getName());

    private static final String ENVIRONMENT = "ENVIRONMENT";
    private static AAFPolicyClientImpl instance = null;
    private static Properties cadiprops = new Properties();
    private static AAFCon<?> aafCon = null;
    private static AAFLurPerm aafLurPerm = null;
    private static AAFAuthn<?> aafAuthn = null;
    private static PropAccess access = null;

    private AAFPolicyClientImpl(Properties properties) throws AAFPolicyException {
        setup(properties);
    }

    /**
     * Gets the instance of the AAFClient instance. Needs Proper properties with
     * CLIENT_ID, CLIENT_KEY and ENVIRONMENT
     *
     * @param properties
     *            Properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
     * @return AAFClient instance.
     * @throws AAFPolicyException
     *             Exceptions.
     */
    public static synchronized AAFPolicyClientImpl getInstance(Properties properties) throws AAFPolicyException {
        if (instance == null) {
            logger.info("Creating AAFClient Instance ");
            instance = new AAFPolicyClientImpl(properties);
        }
        return instance;
    }

    // To set Property values && Connections.
    private static void setup(Properties properties) throws AAFPolicyException {
        if (properties != null && !properties.isEmpty()) {
            cadiprops = properties;
            access = new PolicyAccess(cadiprops,
                    Level.valueOf(cadiprops.getProperty("cadi_loglevel", Level.DEBUG.toString())));
        } else {
            logger.error("Required Property value is missing : " + ENVIRONMENT);
            throw new AAFPolicyException("Required Property value is missing : " + ENVIRONMENT);
        }
        setUpAAF();
    }

    /**
     * Updates the Properties file in case if required.
     *
     * @param properties
     *            Properties with CLIENT_ID, CLIENT_KEY and ENVIRONMENT
     * @throws AAFPolicyException
     *             exceptions if any.
     */
    @Override
    public void updateProperties(Properties properties) throws AAFPolicyException {
        setup(properties);
    }

    /**
     * Checks the Authentication and Permissions for the given values.
     *
     * @param userName
     *            Username must be registered under the Name space.
     * @param pass
     *            Password pertaining to the MechID or ATTID.
     * @param type
     *            Permissions Type.
     * @param instance
     *            Permissions Instance.
     * @param action
     *            Permissions Action.
     * @return
     */
    @Override
    public boolean checkAuthPerm(String userName, String pass, String type, String instance, String action) {
        return checkAuth(userName, pass) && checkPerm(userName, pass, type, instance, action);
    }

    /**
     * Checks the Authentication of the UserName and Password Given.
     *
     * @param userName
     *            UserName
     * @param pass
     *            Password.
     * @return True or False.
     */
    @Override
    public boolean checkAuth(String userName, String pass) {
        if (aafAuthn == null) {
            return false;
        }
        try {
            int i = 0;
            do {
                String aafAuthResponse = aafAuthn.validate(userName, pass);
                if (aafAuthResponse==null) {
                    return true;
                } else {
                    logger.warn("User, " + userName + ", failed to authenticate with AAF. \n"
                            + "AAF Response is " + aafAuthResponse);
                }
                i++;
            } while (i < 2);
        } catch (Exception e) {
            logger.error(e.getMessage() + e);
        }

        return false;
    }

    /**
     * Checks Permissions for the given UserName, Password and Type, Instance
     * Action.
     *
     * @param userName
     *            UserName
     * @param pass
     *            Password.
     * @param type
     *            Permissions Type.
     * @param instance
     *            Permissions Instance.
     * @param action
     *            Permissions Action.
     * @return True or False.
     */
    @Override
    public boolean checkPerm(String userName, String pass, String type, String instance, String action) {
        int i = 0;
        Boolean result = false;
        do {
            if (aafCon != null && aafLurPerm != null) {
                try {
                    aafCon.basicAuth(userName, pass);
                    AAFPermission perm = new AAFPermission(cadiprops.getProperty("policy.aaf.namespace"), type,
                            instance, action);
                    final Principal p = new UnAuthPrincipal(userName);
                    result = aafLurPerm.fish(p, perm);
                } catch (CadiException e) {
                    logger.error(e.getMessage() + e);
                    aafLurPerm.destroy();
                }
            }
            i++;
        } while (i < 2 && !result); // Try once more to check if this can be passed. AAF has some issues.
        return result;
    }

    private static boolean setUpAAF() {
        try {
            aafCon = new AAFConHttp(access,
                    new PropertyLocator("https://" + cadiprops.getProperty("aaf_fqdn") + ":8100"));
            aafLurPerm = aafCon.newLur();
            aafAuthn = aafCon.newAuthn(aafLurPerm);
            return true;
        } catch (Exception e) {
            logger.error("Error while setting up AAF Connection " + e.getMessage() + e);
            return false;
        }
    }
}
