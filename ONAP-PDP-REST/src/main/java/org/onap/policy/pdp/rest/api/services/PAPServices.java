/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pdp.rest.api.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.config.PDPApiAuth;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.utils.CryptoUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PAPServices {
    private static final String SUCCESS = "success";
    private static Logger LOGGER = FlexLogger.getLogger(PAPServices.class.getName());

    private int responseCode = 0;
    private static String environment = "DEVL";
    private static Boolean isJunit = false;
    private static List<String> paps = null;
    private static final Object papResourceLock = new Object();
    private String operation = null;
    private String requestMethod = null;
    private String encoding = null;

    public static void setJunit(final boolean isJunit) {
        PAPServices.isJunit = isJunit;
    }

    public PAPServices() {
        environment = PDPApiAuth.getEnvironment();
        if (paps == null) {
            synchronized (papResourceLock) {
                String urlList = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URLS);
                if (urlList == null) {
                    urlList = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
                }
                paps = Arrays.asList(urlList.split(","));
            }
        }
    }

    private String getPAPEncoding() {
        if (encoding == null) {
            final String userID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
            final String pass =
                    CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS));
            final Base64.Encoder encoder = Base64.getEncoder();
            encoding = encoder.encodeToString((userID + ":" + pass).getBytes(StandardCharsets.UTF_8));
        }
        return encoding;
    }

    private void rotatePAPList() {
        synchronized (papResourceLock) {
            Collections.rotate(paps, -1);
        }
    }

    private String getPAP() {
        String result;
        synchronized (papResourceLock) {
            result = paps.get(0);
        }
        return result;
    }

    public static void setPaps(final List<String> paps) {
        PAPServices.paps = paps;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Object callPAP(final Object content, final String[] parameters, UUID requestID, final String clientScope)
            throws PolicyException {
        String response = null;
        HttpURLConnection connection = null;
        responseCode = 0;
        // Checking for the available PAPs is done during the first Request and
        // the List is going to have the connected PAP as first element.
        // This makes it Real-Time to change the list depending on their
        // availability.
        if (paps == null || paps.isEmpty()) {
            final String message = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAPs List is Empty.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        int papsCount = 0;
        boolean connected = false;
        while (papsCount < paps.size()) {
            try {
                String fullURL = getPAP();
                fullURL = checkParameter(parameters, fullURL);
                final URL url = new URL(fullURL);
                LOGGER.debug("--- Sending Request to PAP : " + url.toString() + " ---");
                // Open the connection
                connection = (HttpURLConnection) url.openConnection();
                // Setting Content-Type
                connection.setRequestProperty("Content-Type", "application/json");
                // Adding Authorization
                connection.setRequestProperty("Authorization", "Basic " + getPAPEncoding());
                connection.setRequestProperty("Environment", environment);
                connection.setRequestProperty("ClientScope", clientScope);
                // set the method and headers
                connection.setRequestMethod(requestMethod);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                // Adding RequestID
                if (requestID == null) {
                    requestID = UUID.randomUUID();
                    LOGGER.info("No request ID provided, sending generated ID: " + requestID.toString());
                } else {
                    LOGGER.info("Using provided request ID: " + requestID.toString());
                }
                connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
                if (content != null && (content instanceof InputStream)) {
                    // send current configuration
                    try (OutputStream os = connection.getOutputStream()) {
                        final int count = IOUtils.copy((InputStream) content, os);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("copied to output, bytes=" + count);
                        }
                    }
                } else if (content != null) {
                    // the content is an object to be encoded in JSON
                    final ObjectMapper mapper = new ObjectMapper();
                    if (!isJunit) {
                        mapper.writeValue(connection.getOutputStream(), content);
                    }
                }
                // DO the connect
                connection.connect();
                responseCode = connection.getResponseCode();
                // If Connected to PAP then break from the loop and continue
                // with the Request
                if (connection.getResponseCode() > 0 || isJunit) {
                    connected = true;
                    break;
                } else {
                    LOGGER.debug(XACMLErrorConstants.ERROR_PERMISSIONS + "PAP Response Code : "
                            + connection.getResponseCode());
                    rotatePAPList();
                }
            } catch (final Exception e) {
                // This means that the PAP is not working
                if (isJunit) {
                    connected = true;
                    break;
                }
                LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
                rotatePAPList();
            }
            papsCount++;
        }
        if (connected) {
            // Read the Response
            LOGGER.debug("connected to the PAP : " + getPAP());
            LOGGER.debug("--- Response: ---");
            if (connection != null) {
                final Map<String, List<String>> headers = connection.getHeaderFields();
                for (final String key : headers.keySet()) {
                    LOGGER.debug("Header :" + key + "  Value: " + headers.get(key));
                }

                try {
                    response = checkResponse(connection, requestID);
                } catch (final IOException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
                    response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + e;
                    throw new PolicyException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Decoding the result ", e);
                }
                if (isJunit) {
                    response = SUCCESS;
                }
            } else {
                response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "connection is null";
            }
            return response;
        } else {
            response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Unable to get valid response from PAP(s) " + paps;
            return response;
        }
    }

    public String getActiveVersion(final String policyScope, final String filePrefix, final String policyName,
            final String clientScope, final UUID requestID) {
        String version = null;
        HttpURLConnection connection = null;
        final String[] parameters = {"apiflag=version", "policyScope=" + policyScope, "filePrefix=" + filePrefix,
            "policyName=" + policyName};
        if (paps == null || paps.isEmpty()) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "PAPs List is Empty.");
        } else {
            int papsCount = 0;
            boolean connected = false;
            while (papsCount < paps.size()) {
                try {
                    String fullURL = getPAP();
                    if (parameters != null && parameters.length > 0) {
                        String queryString = "";
                        for (final String p : parameters) {
                            queryString += "&" + p;
                        }
                        fullURL += "?" + queryString.substring(1);
                    }

                    final URL url = new URL(fullURL);

                    // Open the connection
                    connection = (HttpURLConnection) url.openConnection();

                    // Setting Content-Type
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Adding Authorization
                    connection.setRequestProperty("Authorization", "Basic " + getPAPEncoding());

                    connection.setRequestProperty("Environment", environment);
                    connection.setRequestProperty("ClientScope", clientScope);

                    // set the method and headers
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(false);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());

                    // DO the connect
                    connection.connect();

                    // If Connected to PAP then break from the loop and continue with the Request
                    if (connection.getResponseCode() > 0) {
                        connected = true;
                        break;

                    } else {
                        LOGGER.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error");
                    }
                } catch (final Exception e) {
                    // This means that the PAP is not working
                    LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
                    rotatePAPList();
                }
                papsCount++;
            }

            if (connected) {
                // Read the Response
                LOGGER.debug("connected to the PAP : " + getPAP());
                LOGGER.debug("--- Response: ---");
                final Map<String, List<String>> headers = connection.getHeaderFields();
                for (final String key : headers.keySet()) {
                    LOGGER.debug("Header :" + key + "  Value: " + headers.get(key));
                }
                try {
                    if (connection.getResponseCode() == 200) {
                        // Check for successful creation of policy
                        version = connection.getHeaderField("version");
                        LOGGER.debug("ActiveVersion from the Header: " + version);
                    } else if (connection.getResponseCode() == 403) {
                        LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is "
                                + connection.getResponseCode()
                                + ". PEP is not Authorized for making this Request!! \n Contact Administrator for this Scope. ");
                        version = "pe100";
                    } else if (connection.getResponseCode() == 404) {
                        LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is "
                                + connection.getResponseCode()
                                + ". This indicates a problem with getting the version from the PAP");
                        version = "pe300";
                    } else {
                        LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                                + "BAD REQUEST:  Error occured while getting the version from the PAP. The request may be incorrect. The response code of the URL is '"
                                + connection.getResponseCode() + "'");
                    }
                } catch (final IOException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
                }
            } else {
                LOGGER.error(
                        XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get valid response from PAP(s) " + paps);
            }
        }
        return version;
    }

    private String checkResponse(final HttpURLConnection connection, final UUID requestID) throws IOException {
        String response = null;
        if (responseCode == 200 || isJunit) {
            // Check for successful creation of policy
            String isSuccess = null;
            if (!isJunit) { // is this a junit test?
                isSuccess = connection.getHeaderField("successMapKey");
                operation = connection.getHeaderField("operation");
            } else {
                isSuccess = SUCCESS;
            }
            if (SUCCESS.equals(isSuccess)) {
                if ("update".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --Policy with the name "
                            + connection.getHeaderField("policyName") + " was successfully updated. ";
                    if (connection.getHeaderField("safetyChecker") != null) {
                        response = response + "\n\nPolicy Safety Checker Warning: This closedLoopControlName "
                                + "is potentially in conflict with " + connection.getHeaderField("conflictCLName")
                                + " that already exists." + " See detailed information on ClosedLoop Pairs below: "
                                + "\n\n" + connection.getHeaderField("safetyChecker");
                    }
                } else if ("create".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --Policy with the name "
                            + connection.getHeaderField("policyName") + " was successfully created.";
                    if (connection.getHeaderField("safetyChecker") != null) {
                        response = response + "\n\nPolicy Safety Checker Warning: This closedLoopControlName "
                                + "is potentially in conflict with " + connection.getHeaderField("conflictCLName")
                                + " that already exists. " + "See detailed information on ClosedLoop Pairs below: "
                                + "\n\n" + connection.getHeaderField("safetyChecker");
                    }
                } else if ("delete".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --The policy was successfully deleted.";
                } else if ("import".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --The policy engine import for "
                            + connection.getHeaderField("service") + " was successfull.";
                } else if ("createDictionary".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --Dictionary Item was added successfully!";
                } else if ("updateDictionary".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --Dictionary Item was updated successfully!";
                } else if ("getDictionary".equals(operation)) {
                    String json = null;
                    try {

                        // get the json string from the response
                        final InputStream is = connection.getInputStream();

                        // read the inputStream into a buffer (trick found online scans entire input
                        // looking for end-of-file)
                        final java.util.Scanner scanner = new java.util.Scanner(is);
                        scanner.useDelimiter("\\A");
                        json = scanner.hasNext() ? scanner.next() : "";
                        scanner.close();

                    } catch (final IOException e1) {
                        LOGGER.error(e1.getMessage() + e1);
                    }
                    response = "Transaction ID: " + requestID + " --Dictionary Items Retrieved " + json;
                } else if ("getMetrics".equals(operation)) {
                    response = "Transaction ID: " + requestID + " --Policy Metrics Retrieved "
                            + connection.getHeaderField("metrics");
                }
                LOGGER.info(response);
            } else {
                final String message = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Operation unsuccessful, unable to complete the request!";
                LOGGER.error(message);
                response = message;
            }
        } else if (connection.getResponseCode() == 202) {
            if ("delete".equalsIgnoreCase(connection.getHeaderField("operation"))
                    && "true".equals(connection.getHeaderField("lockdown"))) {
                response = "Transaction ID: " + requestID + " --Policies are locked down, please try again later.";
                LOGGER.warn(response);
            }
        } else if (connection.getResponseCode() == 204) {
            if ("push".equals(connection.getHeaderField("operation"))) {
                response = "Transaction ID: " + requestID + " --Policy '" + connection.getHeaderField("policyId")
                        + "' was successfully pushed to the PDP group '" + connection.getHeaderField("groupId") + "'.";
                LOGGER.info(response);
            }
        } else if (connection.getResponseCode() == 400 && connection.getHeaderField("error") != null) {
            response = connection.getHeaderField("error");
            LOGGER.error(response);
        } else if (connection.getResponseCode() == 403) {
            response = XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is "
                    + connection.getResponseCode()
                    + ". PEP is not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
            LOGGER.error(response);
        } else if (connection.getResponseCode() == 404 && connection.getHeaderField("error") != null) {
            if ("UnknownGroup".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + connection.getHeaderField("message")
                        + " Please check the pdpGroup you are requesting to push the policy to.";
                LOGGER.error(response);
            } else if ("policyNotAvailableForEdit".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + connection.getHeaderField("message");
            }
        } else if (connection.getResponseCode() == 409 && connection.getHeaderField("error") != null) {
            if ("modelExistsDB".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Import Value Exist Error:  The import value "
                        + connection.getHeaderField("service") + " already exist on the PAP. "
                        + "Please create a new import value.";
            } else if ("policyExists".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Exist Error:  The Policy "
                        + connection.getHeaderField("policyName") + " already exist on the PAP. "
                        + "Please create a new policy or use the update API to modify the existing one.";
            } else if ("dictionaryItemExists".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Dictionary Item Exist Error:  The Dictionary Item already exist in the database. "
                        + "Please create a new Dictionary Item or use the update API to modify the existing one.";
            } else if ("duplicateGroup".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Group Policy Scope List Exist Error:  The Group Policy Scope List for this Dictionary Item already exist in the database. "
                        + "Duplicate Group Policy Scope Lists for multiple groupNames is not allowed. "
                        + "Please review the request and verify that the groupPolicyScopeListData1 is unique compared to existing groups.";
            } else if ("PolicyInPDP".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Policy Exist Error:  The Policy trying to be deleted is active in PDP. "
                        + "Active PDP Polcies are not allowed to be deleted from PAP. "
                        + "Please First remove the policy from PDP in order to successfully delete the Policy from PAP.";
            }
            LOGGER.error(response);
        } else if (connection.getResponseCode() == 500 && connection.getHeaderField("error") != null) {
            if ("jpautils".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Could not create JPAUtils instance on the PAP";
            } else if ("deleteDB".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to delete Policy from database.";
            } else if ("deleteFile".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the policy file";
            } else if ("groupUpdate".equals(connection.getHeaderField("error"))) {
                response = connection.getHeaderField("message");
            } else if ("unknown".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_UNKNOWN
                        + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.";
            } else if ("deleteConfig".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Cannot delete the configuration or action body file in specified location.";
            } else if ("missing".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Failed to create value in database because service does match a value in file";
            } else if ("importDB".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Database errors during policy engine import";
            } else if ("policyCopyError".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_PROCESS_FLOW + connection.getHeaderField("message");
            } else if ("addGroupError".equals(connection.getHeaderField("error"))) {
                response = connection.getHeaderField("message");
            } else if ("validation".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Validation errors during policy engine "
                        + connection.getHeaderField("operation") + " for " + connection.getHeaderField("service");
            } else if ("error".equals(connection.getHeaderField("error"))) {
                response = XACMLErrorConstants.ERROR_UNKNOWN
                        + "Could not create or update the policy for and unknown reason";
            } else {
                response = XACMLErrorConstants.ERROR_SYSTEM_ERROR
                        + "Error occured while attempting perform this operation.. "
                        + "the request may be incorrect or the PAP is unreachable. "
                        + connection.getHeaderField("error");
            }
            LOGGER.error(response);
        } else {
            response =
                    XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Error occured while attempting perform this operation.. "
                            + "the request may be incorrect or the PAP is unreachable.";
            LOGGER.error(response);
        }
        return response;
    }

    private String checkParameter(final String[] parameters, String fullURL) {
        if (parameters != null && parameters.length > 0) {
            String queryString = "";
            for (final String p : parameters) {
                queryString += "&" + p;
                if (p.equalsIgnoreCase("operation=post")) {
                    requestMethod = "POST";
                } else if (p.equalsIgnoreCase("operation=delete")) {
                    requestMethod = "DELETE";
                    operation = "delete";
                } else if (p.equalsIgnoreCase("operation=get")) {
                    requestMethod = "GET";
                    operation = "get";
                } else if (p.equalsIgnoreCase("operation=put") || p.equalsIgnoreCase("operation=create")
                        || p.equalsIgnoreCase("operation=update") || p.equalsIgnoreCase("operation=createDictionary")) {
                    requestMethod = "PUT";
                    if (p.equalsIgnoreCase("operation=create")) {
                        operation = "create";
                    } else if (p.equalsIgnoreCase("operation=update")) {
                        operation = "update";
                    } else if (p.equalsIgnoreCase("operation=createDictionary")) {
                        operation = "createDictionary";
                    }
                } else if (p.equalsIgnoreCase("importService=MICROSERVICE")
                        || p.equalsIgnoreCase("importService=BRMSPARAM")) {
                    requestMethod = "PUT";
                }
            }
            fullURL += "?" + queryString.substring(1);
        }
        return fullURL;
    }

    public StdPDPPolicy pushPolicy(final String policyScope, final String filePrefix, final String policyName,
            final String clientScope, final String pdpGroup, UUID requestID) throws PolicyException {
        final String json = "{ " + "\"apiflag\": \"api\"," + "\"policyScope\": \"" + policyScope + "\","
                + "\"filePrefix\": \"" + filePrefix + "\"," + "\"policyName\": \"" + policyName + "\","
                + "\"clientScope\": \"" + clientScope + "\"," + "\"pdpGroup\": \"" + pdpGroup + "\"}";

        HttpURLConnection connection = null;
        responseCode = 0;
        if (paps == null || paps.isEmpty()) {
            final String message = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAPs List is Empty.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        int papsCount = 0;
        boolean connected = false;
        while (papsCount < paps.size()) {
            try {
                String fullURL = getPAP();
                fullURL = (fullURL.endsWith("/")) ? fullURL + "onap/pushPolicy" : fullURL + "/onap/pushPolicy";
                final URL url = new URL(fullURL);
                LOGGER.debug("--- Sending Request to PAP : " + url.toString() + " ---");
                // Open the connection
                connection = (HttpURLConnection) url.openConnection();
                // Setting Content-Type
                connection.setRequestProperty("Content-Type", "application/json");
                // Adding Authorization
                connection.setRequestProperty("Authorization", "Basic " + getPAPEncoding());
                connection.setRequestProperty("Environment", environment);
                connection.setRequestProperty("ClientScope", clientScope);
                // set the method and headers
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setDoOutput(true);
                // Adding RequestID
                if (requestID == null) {
                    requestID = UUID.randomUUID();
                    LOGGER.info("No request ID provided, sending generated ID: " + requestID.toString());
                } else {
                    LOGGER.info("Using provided request ID: " + requestID.toString());
                }
                connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
                // DO the connect
                try (OutputStream os = connection.getOutputStream()) {
                    final int count = IOUtils.copy(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), os);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("copied to output, bytes=" + count);
                    }
                }
                connection.connect();
                responseCode = connection.getResponseCode();
                // If Connected to PAP then break from the loop and continue
                // with the Request
                if (connection.getResponseCode() > 0 || isJunit) {
                    connected = true;
                    break;
                } else {
                    LOGGER.debug(XACMLErrorConstants.ERROR_PERMISSIONS + "PAP Response Code : "
                            + connection.getResponseCode());
                    rotatePAPList();
                }
            } catch (final Exception e) {
                // This means that the PAP is not working
                if (isJunit) {
                    connected = true;
                    break;
                }
                LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP connection Error : " + e);
                rotatePAPList();
            }
            papsCount++;
        }
        if (connected) {
            // Read the Response
            LOGGER.debug("connected to the PAP : " + getPAP());
            LOGGER.debug("--- Response: ---");
            if (connection != null) {
                final Map<String, List<String>> headers = connection.getHeaderFields();
                for (final String key : headers.keySet()) {
                    LOGGER.debug("Header :" + key + "  Value: " + headers.get(key));
                }
                try {
                    if (responseCode == 202) {
                        final StdPDPPolicy policy =
                                (StdPDPPolicy) new ObjectInputStream(connection.getInputStream()).readObject();
                        return policy;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
                    throw new PolicyException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Decoding the result ", e);
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
