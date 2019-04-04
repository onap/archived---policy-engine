/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017,2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pdp.rest.restauth;

import com.att.research.xacml.util.XACMLProperties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import javax.servlet.ServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.utils.AAFPolicyClient;
import org.onap.policy.utils.AAFPolicyException;
import org.onap.policy.utils.PeCryptoUtils;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;

public class AuthenticationService {
    private static final Logger LOGGER = FlexLogger.getLogger(AuthenticationService.class);
    private static String environment = null;
    private static Path clientPath = null;
    private static Map<String, ArrayList<String>> clientMap = null;
    private static Long oldModified = null;
    private static AAFPolicyClient aafClient = null;

    private AuthenticationService() {
        // Private Constructor
    }

    /*
     * Set Property by reading the properties File.
     */
    private static void setProperty() {
        environment = XACMLProperties.getProperty("ENVIRONMENT", "DEVL");
        String clientFile = XACMLProperties.getProperty(XACMLRestProperties.PROP_PEP_IDFILE);
        if (clientFile != null) {
            clientPath = Paths.get(clientFile);
        }
        try {
            aafClient = AAFPolicyClient.getInstance(XACMLProperties.getProperties());
        } catch (AAFPolicyException | IOException e) {
            LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "AAF Client Not instantiated properly.");
        }
    }

    /**
     * Gets the environment.
     *
     * @return the environment
     */
    public static String getEnvironment() {
        if (environment == null) {
            setProperty();
        }
        return environment;
    }

    private static String reverseNamespace(String namespace) {
        final List<String> components = Arrays.asList(namespace.split("\\."));
        Collections.reverse(components);
        return String.join(".", components);
    }

    /**
     * Security check for authentication and authorizations.
     *
     * @param clientAuthHeader the client auth header
     * @param authHeader the auth header
     * @param resource the resource
     * @param env the env
     * @return true, if successful
     */
    public static boolean checkPermissions(String clientAuthHeader, String authHeader, String resource, String env,
            ServletRequest request) {
        boolean result = false;
        // check whether env matches
        result = checkEnv(env);
        if (!result) {
            LOGGER.info(XACMLErrorConstants.ERROR_PERMISSIONS + " invalid Environment Header");
            return result;
        }
        // decode the user/pwd from the request header
        String[] userNamePass = getUserInfo(authHeader, clientAuthHeader);

        try {
            // Check Backward Compatibility.
            request.setAttribute("Mechid", "");
            result = false;
            /*
             * If AAF is NOT enabled in the properties we will allow the user to continue to use the client.properties
             * file to authenticate. Note: Disabling AAF is for testing purposes and not intended for production.
             */
            if ("false".equals(XACMLProperties.getProperty("enable_aaf"))) {
                result = clientAuth(userNamePass);
            }
            if (!result) {
                result = aafAuth(userNamePass, resource);
                request.setAttribute("Mechid", userNamePass[0]);
            }
        } catch (Exception e) {
            LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
            result = false;
        }
        return result;

    }

    private static boolean checkEnv(String env) {
        if (StringUtils.isBlank(env)) {
            // must be old type of req
            return true;
        } else {
            return env.trim().equalsIgnoreCase(getEnvironment());
        }

    }

    private static boolean aafAuth(String[] userNamePass, String resource) {
        boolean result = false;
        String permission = getPermission(resource);
        try {
            String aafPolicyNameSpace = XACMLProperties.getProperty("policy.aaf.namespace");
            if (!userNamePass[0].contains("@") && aafPolicyNameSpace != null) {
                userNamePass[0] = userNamePass[0] + "@" + reverseNamespace(aafPolicyNameSpace);
            } else {
                LOGGER.info("No AAF NameSpace specified in properties");
            }

            LOGGER.info("Contacting AAF in : " + environment);
            result = aafClient.checkAuthPerm(userNamePass[0], userNamePass[1], permission, environment, "*");

            return result;
        } catch (Exception e) {
            LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
            return false;
        }
    }

    private static String getPermission(String resource) {
        String aafResource = XACMLProperties.getProperty("policy.aaf.root.permission");
        String perm = resource;
        if (StringUtils.containsIgnoreCase(perm, "Notification")) {
            perm = "notification";
        } else if (StringUtils.containsIgnoreCase(perm, "heartbeat")) {
            perm = "notification";
        } else if (StringUtils.containsIgnoreCase(perm, "createDictionary")) {
            perm = "createDictionary";
        } else if (StringUtils.containsIgnoreCase(perm, "updateDictionary")) {
            perm = "updateDictionary";
        } else if (StringUtils.containsIgnoreCase(perm, "getDictionary")) {
            perm = "getDictionary";
        } else if (StringUtils.containsIgnoreCase(perm, "create")) {
            perm = "createPolicy";
        } else if (StringUtils.containsIgnoreCase(perm, "update")) {
            perm = "updatePolicy";
        }

        if (!StringUtils.isBlank(aafResource)) {
            perm = aafResource + "." + perm;
        } else {
            LOGGER.info("No AAF Resource specified in properties");
        }
        return perm;
    }

    private static Boolean clientAuth(String[] userNamePass) {
        if (clientPath == null) {
            setProperty();
        }
        if (!clientPath.toFile().exists()) {
            return false;
        } else if (clientPath.toString().endsWith(".properties")) {
            try {
                readProps(clientPath);
                if (clientMap.containsKey(userNamePass[0])
                        && clientMap.get(userNamePass[0]).get(0).equals(userNamePass[1])) {
                    return true;
                }
            } catch (PolicyEngineException e) {
                LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
                return false;
            }
        }
        return false;
    }

    private static Map<String, ArrayList<String>> readProps(Path clientPath) throws PolicyEngineException {
        if (oldModified != null) {
            Long newModified = clientPath.toFile().lastModified();
            if (oldModified.equals(newModified)) {
                return clientMap;
            }
        }
        InputStream in;
        Properties clientProp = new Properties();
        try {
            in = new FileInputStream(clientPath.toFile());
            clientProp.load(in);
        } catch (IOException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR, e);
            throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Cannot Load the Properties file",
                    e);
        }
        // Read the Properties and Load the Clients and their scopes.
        clientMap = new HashMap<>();
        //
        for (Object propKey : clientProp.keySet()) {
            String clientId = (String) propKey;
            String clientValue = clientProp.getProperty(clientId);
            if (clientValue != null && clientValue.contains(",")) {
                ArrayList<String> clientValues = new ArrayList<>(Arrays.asList(clientValue.split("\\s*,\\s*")));
                if (!StringUtils.isBlank(clientValues.get(0))) {
                    clientValues.set(0, PeCryptoUtils.decrypt(clientValues.get(0)));
                    clientMap.put(clientId, clientValues);
                }
            }
        }
        if (clientMap.isEmpty()) {
            LOGGER.debug(XACMLErrorConstants.ERROR_PERMISSIONS
                    + "No Clients ID , Client Key and Scopes are available. Cannot serve any Clients !!");
            throw new PolicyEngineException("Empty Client file");
        }
        oldModified = clientPath.toFile().lastModified();
        return clientMap;
    }

    private static String[] getUserInfo(final String authHeader, final String clientAuthHeader) {
        String userInfo = authHeader;
        if (!StringUtils.isBlank(clientAuthHeader)) {
            userInfo = clientAuthHeader;
        }

        String[] userNamePass = null;

        try {
            userNamePass = PolicyUtils.decodeBasicEncoding(userInfo);
            if (userNamePass == null || userNamePass.length == 0) {
                String usernameAndPassword = null;
                byte[] decodedBytes = Base64.getDecoder().decode(userInfo);
                usernameAndPassword = new String(decodedBytes, StandardCharsets.UTF_8);
                StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
                String username = tokenizer.nextToken();
                String password = tokenizer.nextToken();
                userNamePass = new String[] {username, password};
            }
            LOGGER.info("User " + userNamePass[0] + " is Accessing Policy Engine API - ");
        } catch (Exception e) {
            LOGGER.error(MessageCodes.ERROR_PERMISSIONS, e);
            return new String[0];
        }
        return userNamePass;
    }

}
