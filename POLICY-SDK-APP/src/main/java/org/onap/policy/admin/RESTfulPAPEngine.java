/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.admin;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.utils.CryptoUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPItemSetChangeNotifier;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPStatus;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.onap.policy.common.logging.flexlogger.FlexLogger; 
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * Implementation of the PAPEngine interface that communicates with a PAP engine in a remote servlet
 * through a RESTful interface
 * 
 *
 */
public class RESTfulPAPEngine extends StdPDPItemSetChangeNotifier implements PAPPolicyEngine {
    private static final Logger LOGGER	= FlexLogger.getLogger(RESTfulPAPEngine.class);

    private static final String groupID = "groupId=";

    //
    // URL of the PAP Servlet that this Admin Console talks to
    //
    private String papServletURLString;

    /**
     * Set up link with PAP Servlet and get our initial set of Groups
     * @throws Exception
     */
    public RESTfulPAPEngine (String myURLString) throws PAPException  {
        //
        // Get our URL to the PAP servlet
        //
        this.papServletURLString = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
        if (this.papServletURLString == null || this.papServletURLString.length() == 0) {
            String message = "The property 'POLICYENGINE_ADMIN_ACTIVE' was not set during installation.  Admin Console cannot call PAP.";
            LOGGER.error(message);
            throw new PAPException(message);
        }

        //
        // register this Admin Console with the PAP Servlet to get updates
        //
        Object newURL = sendToPAP("PUT", null, null, null, "adminConsoleURL=" + myURLString);
        if (newURL != null) {
            // assume this was a re-direct and try again
            LOGGER.warn("Redirecting to '" + newURL + "'");
            this.papServletURLString = (String)newURL;
            newURL = sendToPAP("PUT", null, null, null, "adminConsoleURL=" + myURLString);
            if (newURL != null) {
                LOGGER.error("Failed to redirect to " + this.papServletURLString);
                throw new PAPException("Failed to register with PAP");
            }
        }
    }


    //
    // High-level commands used by the Admin Console code through the PAPEngine Interface
    //

    @Override
    public OnapPDPGroup getDefaultGroup() throws PAPException {
        return (OnapPDPGroup)sendToPAP("GET", null, null, StdPDPGroup.class, groupID, "default=");
    }

    @Override
    public void setDefaultGroup(OnapPDPGroup group) throws PAPException {
        sendToPAP("POST", null, null, null, groupID + group.getId(), "default=true");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<OnapPDPGroup> getOnapPDPGroups() throws PAPException {
        Set<OnapPDPGroup> newGroupSet;
        newGroupSet = (Set<OnapPDPGroup>) this.sendToPAP("GET", null, Set.class, StdPDPGroup.class, groupID);
        return Collections.unmodifiableSet(newGroupSet);
    }


    @Override
    public OnapPDPGroup getGroup(String id) throws PAPException {
        return (OnapPDPGroup)sendToPAP("GET", null, null, StdPDPGroup.class, groupID + id);
    }

    @Override
    public void newGroup(String name, String description)
            throws PAPException {
        String escapedName = null;
        String escapedDescription = null;
        try {
            escapedName = URLEncoder.encode(name, "UTF-8");
            escapedDescription = URLEncoder.encode(description, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new PAPException("Unable to send name or description to PAP: " + e.getMessage()  +e);
        }

        this.sendToPAP("POST", null, null, null, groupID, "groupName="+escapedName, "groupDescription=" + escapedDescription);
    }


    /**
     * Update the configuration on the PAP for a single Group.
     *
     * @param group
     * @return
     * @throws PAPException
     */
    @Override
    public void updateGroup(OnapPDPGroup group) throws PAPException {

        try {

            //
            // ASSUME that all of the policies mentioned in this group are already located in the correct directory on the PAP!
            //
            // Whenever a Policy is added to the group, that file must be automatically copied to the PAP from the Workspace.
            //


            // Copy all policies from the local machine's workspace to the PAP's PDPGroup directory.
            // This is not efficient since most of the policies will already exist there.
            // However, the policy files are (probably!) not too huge, and this is a good way to ensure that any corrupted files on the PAP get refreshed.


            // now update the group object on the PAP

            sendToPAP("PUT", group, null, null, groupID + group.getId());
        } catch (Exception e) {
            String message = "Unable to PUT policy '" + group.getId() + "', e:" + e;
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
            throw new PAPException(message);
        }
    }


    @Override
    public void removeGroup(OnapPDPGroup group, OnapPDPGroup newGroup)
            throws PAPException {
        String moveToGroupString = null;
        if (newGroup != null) {
            moveToGroupString = "movePDPsToGroupId=" + newGroup.getId();
        }
        sendToPAP("DELETE", null, null, null, groupID + group.getId(), moveToGroupString);
    }

    @Override
    public OnapPDPGroup getPDPGroup(OnapPDP pdp) throws PAPException {
        return getPDPGroup(pdp.getId());
    }


    public OnapPDPGroup getPDPGroup(String pdpId) throws PAPException {
        return (OnapPDPGroup)sendToPAP("GET", null, null, StdPDPGroup.class, groupID, "pdpId=" + pdpId, "getPDPGroup=");
    }

    @Override
    public OnapPDP getPDP(String pdpId) throws PAPException {
        return (OnapPDP)sendToPAP("GET", null, null, StdPDP.class, groupID, "pdpId=" + pdpId);
    }

    @Override
    public void newPDP(String id, OnapPDPGroup group, String name, String description, int jmxport) throws PAPException {
        StdPDP newPDP = new StdPDP(id, name, description, jmxport);
        sendToPAP("PUT", newPDP, null, null, groupID + group.getId(), "pdpId=" + id);
        return;
    }

    @Override
    public void movePDP(OnapPDP pdp, OnapPDPGroup newGroup) throws PAPException {
        sendToPAP("POST", null, null, null, groupID + newGroup.getId(), "pdpId=" + pdp.getId());
        return;
    }

    @Override
    public void updatePDP(OnapPDP pdp) throws PAPException {
        OnapPDPGroup group = getPDPGroup(pdp);
        sendToPAP("PUT", pdp, null, null, groupID + group.getId(), "pdpId=" + pdp.getId());
        return;
    }

    @Override
    public void removePDP(OnapPDP pdp) throws PAPException {
        OnapPDPGroup group = getPDPGroup(pdp);
        sendToPAP("DELETE", null, null, null, groupID + group.getId(), "pdpId=" + pdp.getId());
        return;
    }

    //Validate the Policy Data
    public boolean validatePolicyRequest(PolicyRestAdapter policyAdapter, String policyType) throws PAPException {
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy(policyAdapter.getPolicyName(), policyAdapter.getConfigBodyData(), policyAdapter.getConfigType(), "Base");

        //send JSON object to PAP
        return (Boolean) sendToPAP("PUT", newPAPPolicy, null, null, "operation=validate", "apiflag=admin", "policyType=" + policyType);
    }



    @Override
    public void publishPolicy(String id, String name, boolean isRoot,
            InputStream policy, OnapPDPGroup group) throws PAPException {


        // copy the (one) file into the target directory on the PAP servlet
        copyFile(id, group, policy);

        // adjust the local copy of the group to include the new policy
        PDPPolicy pdpPolicy = new StdPDPPolicy(id, isRoot, name);
        group.getPolicies().add(pdpPolicy);

        // tell the PAP servlet to include the policy in the configuration
        updateGroup(group);

        return;
    }

    /**
     * Copy a single Policy file from the input stream to the PAP Servlet.
     * Either this works (silently) or it throws an exception.
     *
     * @param policyId
     * @param group
     * @param policy
     * @return
     * @throws PAPException
     */
    public void copyFile(String policyId, OnapPDPGroup group, InputStream policy) throws PAPException {
        // send the policy file to the PAP Servlet
        try {
            sendToPAP("POST", policy, null, null, groupID + group.getId(), "policyId="+policyId);
        } catch (Exception e) {
            String message = "Unable to PUT policy '" + policyId + "', e:" + e;
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
            throw new PAPException(message);
        }
    }


    @Override
    public void	copyPolicy(PDPPolicy policy, OnapPDPGroup group) throws PAPException {
        if (policy == null || group == null) {
            throw new PAPException("Null input policy="+policy+"  group="+group);
        }
        try (InputStream is = new FileInputStream(new File(policy.getLocation())) ) {
            copyFile(policy.getId(), group, is );
        } catch (Exception e) {
            String message = "Unable to PUT policy '" + policy.getId() + "', e:" + e;
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
            throw new PAPException(message);
        }
    }

    @Override
    public void	removePolicy(PDPPolicy policy, OnapPDPGroup group) throws PAPException {
        throw new PAPException("NOT IMPLEMENTED");

    }


    /**
     * Special operation - Similar to the normal PAP operations but this one contacts the PDP directly
     * to get detailed status info.
     *
     * @param pdp
     * @return
     * @throws PAPException
     */
    @Override
    public PDPStatus getStatus(OnapPDP pdp) throws PAPException {
        return (StdPDPStatus)sendToPAP("GET", pdp, null, StdPDPStatus.class);
    }


    //
    // Internal Operations called by the PAPEngine Interface methods
    //

    /**
     * Send a request to the PAP Servlet and get the response.
     *
     * The content is either an InputStream to be copied to the Request OutputStream
     * 	OR it is an object that is to be encoded into JSON and pushed into the Request OutputStream.
     *
     * The Request parameters may be encoded in multiple "name=value" sets, or parameters may be combined by the caller.
     *
     * @param method
     * @param content	- EITHER an InputStream OR an Object to be encoded in JSON
     * @param collectionTypeClass
     * @param responseContentClass
     * @param parameters
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object sendToPAP(String method, Object content, Class collectionTypeClass, Class responseContentClass, String... parameters ) throws PAPException {
        HttpURLConnection connection = null;
        String papID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
        LOGGER.info("User Id is " + papID);
        String papPass = CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS));
        Base64.Encoder encoder = Base64.getEncoder();
        String encoding = encoder.encodeToString((papID+":"+papPass).getBytes(StandardCharsets.UTF_8));
        Object contentObj = content;
        LOGGER.info("Encoding for the PAP is: " + encoding);
        try {
            String fullURL = papServletURLString;
            if (parameters != null && parameters.length > 0) {
                StringBuilder queryString = new StringBuilder();
                for (String p : parameters) {
                    queryString.append("&" + p);
                }
                fullURL += "?" + queryString.substring(1);
            }

            // special case - Status (actually the detailed status) comes from the PDP directly, not the PAP
            if ("GET".equals(method) &&	(contentObj instanceof OnapPDP) &&	responseContentClass == StdPDPStatus.class) {
                // Adjust the url and properties appropriately
                String pdpID =((OnapPDP)contentObj).getId();
                fullURL = pdpID + "?type=Status";
                contentObj = null;
                if(CheckPDP.validateID(pdpID)){
                    encoding = CheckPDP.getEncoding(pdpID);
                }
            }


            URL url = new URL(fullURL);

            //
            // Open up the connection
            //
            connection = (HttpURLConnection)url.openConnection();
            //
            // Setup our method and headers
            //
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            //
            // Adding this in. It seems the HttpUrlConnection class does NOT
            // properly forward our headers for POST re-direction. It does so
            // for a GET re-direction.
            //
            // So we need to handle this ourselves.
            //
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            if (contentObj != null) {
                if (contentObj instanceof InputStream) {
                    sendCurrPolicyConfig(method, connection, (InputStream) contentObj);
                } else {
                    // The contentObj is an object to be encoded in JSON
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(connection.getOutputStream(),  contentObj);
                }
            }
            //
            // Do the connect
            //
            connection.connect();
            if (connection.getResponseCode() == 204) {
                LOGGER.info("Success - no content.");
                return null;
            } else if (connection.getResponseCode() == 200) {
                LOGGER.info("Success. We have a return object.");
                String isValidData = connection.getHeaderField("isValidData");
                String isSuccess = connection.getHeaderField("successMapKey");
                Map<String, String> successMap = new HashMap<>();
        if (isValidData != null && "true".equalsIgnoreCase(isValidData)){
                    LOGGER.info("Policy Data is valid.");
                    return true;
        } else if (isValidData != null && "false".equalsIgnoreCase(isValidData)) {
                    LOGGER.info("Policy Data is invalid.");
                    return false;
        } else if (isSuccess != null && "success".equalsIgnoreCase(isSuccess)) {
                    LOGGER.info("Policy Created Successfully!" );
                    String finalPolicyPath = connection.getHeaderField("finalPolicyPath");
                    successMap.put("success", finalPolicyPath);
                    return successMap;
        } else if (isSuccess != null && "error".equalsIgnoreCase(isSuccess)) {
                    LOGGER.info("There was an error while creating the policy!");
                    successMap.put("error", "error");
                    return successMap;
                } else {
                    // get the response content into a String
                    String json = getJsonString(connection);

                    // convert Object sent as JSON into local object
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    if (collectionTypeClass != null) {
                        // collection of objects expected
                        final CollectionType javaType =
                              mapper.getTypeFactory().constructCollectionType(collectionTypeClass, responseContentClass);

                        return mapper.readValue(json, javaType);
                    } else {
                        // single value object expected
                        return mapper.readValue(json, responseContentClass);
                    }
                }

            } else if (connection.getResponseCode() >= 300 && connection.getResponseCode()  <= 399) {
                // redirection
                String newURL = connection.getHeaderField("Location");
                if (newURL == null) {
                    LOGGER.error("No Location header to redirect to when response code="+connection.getResponseCode());
                    throw new IOException("No redirect Location header when response code="+connection.getResponseCode());
                }
                int qIndex = newURL.indexOf('?');
                if (qIndex > 0) {
                    newURL = newURL.substring(0, qIndex);
                }
                LOGGER.info("Redirect seen.  Redirecting " + fullURL + " to " + newURL);
                return newURL;
            } else {
                LOGGER.warn("Unexpected response code: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                throw new IOException("Server Response: " + connection.getResponseCode() + ": " + connection.getResponseMessage());
            }

        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "HTTP Request/Response to PAP: " + e,e);
            throw new PAPException("Request/Response threw :" + e);
        } finally {
            // cleanup the connection
            if (connection != null) {
                try {
                    // For some reason trying to get the inputStream from the connection
                    // throws an exception rather than returning null when the InputStream does not exist.
                    InputStream is = connection.getInputStream();
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ex) {
                    LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to close connection: " + ex, ex);
                }
                connection.disconnect();
            }
        }
    }

    private void sendCurrPolicyConfig(String method, final HttpURLConnection connection, InputStream contentObj) {
        try {
            //
            // Send our current policy configuration
            //
            try (OutputStream os = connection.getOutputStream()) {
            int count = IOUtils.copy(contentObj, os);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("copied to output, bytes="+count);
                }
            }
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to write content in '" + method + "'", e);
        }
    }

    private String getJsonString(final HttpURLConnection connection) throws IOException {
        String json = null;
        // read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
        try(java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream())) {
            scanner.useDelimiter("\\A");
            json = scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to read inputStream from connection: " + e, e);
            throw e;
        }
        LOGGER.info("JSON response from PAP: " + json);
        return json;
    }
}
