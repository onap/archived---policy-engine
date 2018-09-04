/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.api.services;

import com.att.research.xacml.api.Advice;
import com.att.research.xacml.api.AttributeAssignment;
import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Obligation;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMResponse;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import com.att.research.xacml.util.XACMLProperties;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.api.PolicyType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.XACMLPdpServlet;
import org.onap.policy.pdp.rest.api.models.PDPResponse;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.std.Matches;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.w3c.dom.Document;


public class PDPServices {
    private static final Logger LOGGER = FlexLogger.getLogger(PDPServices.class.getName());
    // Change the default Priority value here.
    private static final int DEFAULT_PRIORITY = 9999;
    private boolean unique = false;
    private Boolean decide = false;
    private String requestType = null;
    private String requestFormat = DECISION_RAW_XACML_JSON_TYPE;
    private List<String> policyList = null;
    public static final String RAINYDAY_TYPE = "BB_ID";
    public static final String DECISION_MS_NAMING_TYPE = "main-resource-keys";
    public static final String DECISION_RAW_XACML = "raw-xacml-request";
    public static final String DECISION_RAW_XACML_TYPE = "raw-xacml-type";
    public static final String DECISION_RAW_XACML_XML_TYPE = "XML";
    public static final String DECISION_RAW_XACML_JSON_TYPE = "JSON";

    /**
     * Generate request.
     *
     * @param reqStr the json string
     * @param requestId the request id
     * @param unique the unique
     * @param decide the decide
     * @return the collection
     * @throws PolicyException the policy exception
     */
    public Collection<PDPResponse> generateRequest(String reqStr, UUID requestId, boolean unique, boolean decide)
            throws PolicyException {
        this.unique = unique;
        this.decide = decide;
        Collection<PDPResponse> results = null;
        Response response = null;
        // Create Request. We need XACML API here.
        try {
            Request request = null;
            if (DECISION_RAW_XACML_JSON_TYPE.equals(requestFormat)) {
                request = JSONRequest.load(reqStr);
                LOGGER.info("--- Generating Request: ---" + requestId + "\n" + JSONRequest.toString(request));
            } else {
                request = DOMRequest.load(reqStr);
                LOGGER.info("--- Generating Request: ---" + requestId + "\n" + reqStr);
            }

            // Call the PDP
            response = callPdp(request, requestId);
            if (response == null) {
                response = callPdp(request, requestId);
            }

        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
            PDPResponse pdpResponse = new PDPResponse();
            results = new HashSet<>();
            pdpResponse.setPolicyConfigMessage("Unable to Call PDP. Error with the URL");
            pdpResponse.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
            pdpResponse.setPolicyResponseStatus(PolicyResponseStatus.NO_ACTION_REQUIRED);
            results.add(pdpResponse);
            throw new PolicyException(e);
        }

        if (response != null) {
            results = checkResponse(response);
            if (policyList != null) {
                for (String policy : policyList) {
                    XACMLPdpServlet.monitor.policyCountAdd(policy, 1);
                }
            }
        } else {
            LOGGER.info("No Response Received from PDP");
            PDPResponse pdpResponse = new PDPResponse();
            results = new HashSet<>();
            pdpResponse.setPolicyConfigMessage("No Response Received");
            pdpResponse.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
            pdpResponse.setPolicyResponseStatus(PolicyResponseStatus.NO_ACTION_REQUIRED);
            results.add(pdpResponse);
        }
        return results;
    }

    private Collection<PDPResponse> checkResponse(Response response) throws PolicyException {
        String pdpConfigLocation = null;
        Collection<PDPResponse> combinedResult = new HashSet<>();
        int priority = DEFAULT_PRIORITY;
        Map<Integer, PDPResponse> uniqueResult = new HashMap<>();
        for (Result result : response.getResults()) {
            // Process the decision policies. We only return one back for
            // decision
            if (decide) {
                PDPResponse pdpResponse = processDecisionResult(result);
                if (pdpResponse != null) {
                    combinedResult.add(pdpResponse);
                } else {
                    LOGGER.info("processDecisionResult returned null");
                }
                return combinedResult;
            }
            if (!result.getDecision().equals(Decision.PERMIT)) {
                LOGGER.info("Decision not a Permit. " + result.getDecision().toString());
                PDPResponse pdpResponse = new PDPResponse();
                pdpResponse.setStatus(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Params passed: Decision not a Permit.",
                        PolicyResponseStatus.NO_ACTION_REQUIRED, PolicyConfigStatus.CONFIG_NOT_FOUND);
                combinedResult.add(pdpResponse);
                return combinedResult;
            } else {
                if (!result.getAssociatedAdvice().isEmpty()) {
                    // Configurations should be in advice.
                    // Also PDP took actions could be here.
                    for (Advice advice : result.getAssociatedAdvice()) {
                        int config = 0;
                        int uri = 0;
                        String configUrl = null;
                        String policyName = null;
                        String policyVersion = null;
                        Matches match = new Matches();
                        Map<String, String> matchingConditions = new HashMap<>();
                        Map<String, String> configAttributes = new HashMap<>();
                        Map<String, String> responseAttributes = new HashMap<>();
                        Map<String, String> actionTaken = new HashMap<>();
                        PDPResponse pdpResponse = new PDPResponse();
                        Map<String, String> adviseAttributes = new HashMap<>();
                        for (AttributeAssignment attribute : advice.getAttributeAssignments()) {
                            adviseAttributes.put(attribute.getAttributeId().stringValue(),
                                    attribute.getAttributeValue().getValue().toString());
                            if ("CONFIGURATION".equalsIgnoreCase(attribute.getAttributeValue().getValue().toString())) {
                                config++;
                            } else if (attribute.getDataTypeId().stringValue().endsWith("anyURI")) {
                                uri++;
                                if (uri == 1) {
                                    configUrl = attribute.getAttributeValue().getValue().toString();
                                    pdpConfigLocation = configUrl.replace("$URL",
                                            XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_WEBAPPS));
                                } else {
                                    if (!("PDP".equalsIgnoreCase(attribute.getIssuer()))) {
                                        throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                                                + "Error having multiple URI in the Policy");
                                    }
                                }
                            } else if ("PolicyName".equalsIgnoreCase(attribute.getAttributeId().stringValue())) {
                                policyName = attribute.getAttributeValue().getValue().toString();
                                policyList.add(policyName);
                            } else if ("VersionNumber".equalsIgnoreCase(attribute.getAttributeId().stringValue())) {
                                policyVersion = attribute.getAttributeValue().getValue().toString();
                            } else if ("Priority".equalsIgnoreCase(attribute.getAttributeId().stringValue())) {
                                try {
                                    priority = Integer.parseInt(attribute.getAttributeValue().getValue().toString());
                                } catch (Exception e) {
                                    LOGGER.error(
                                            XACMLErrorConstants.ERROR_DATA_ISSUE
                                                    + "Unable to Parse Integer for Priority. Setting to default value",
                                            e);
                                    priority = DEFAULT_PRIORITY;
                                }
                            } else if (attribute.getAttributeId().stringValue().startsWith("matching")) {
                                matchingConditions.put(
                                        attribute.getAttributeId().stringValue().replaceFirst("(matching).", ""),
                                        attribute.getAttributeValue().getValue().toString());
                                if ("ONAPName".equals(
                                        attribute.getAttributeId().stringValue().replaceFirst("(matching).", ""))) {
                                    match.setOnapName(attribute.getAttributeValue().getValue().toString());
                                    matchingConditions.put("ECOMPName",
                                            attribute.getAttributeValue().getValue().toString());
                                } else if ("ConfigName".equals(
                                        attribute.getAttributeId().stringValue().replaceFirst("(matching).", ""))) {
                                    match.setConfigName(attribute.getAttributeValue().getValue().toString());
                                } else {
                                    configAttributes.put(
                                            attribute.getAttributeId().stringValue().replaceFirst("(matching).", ""),
                                            attribute.getAttributeValue().getValue().toString());
                                }
                            } else if (attribute.getAttributeId().stringValue().startsWith("key:")) {
                                responseAttributes.put(
                                        attribute.getAttributeId().stringValue().replaceFirst("(key).", ""),
                                        attribute.getAttributeValue().getValue().toString());
                            } else if (attribute.getAttributeId().stringValue().startsWith("controller:")) {
                                responseAttributes.put("$" + attribute.getAttributeId().stringValue(),
                                        attribute.getAttributeValue().getValue().toString());
                            } else if (attribute.getAttributeId().stringValue().startsWith("dependencies:")) {
                                responseAttributes.put("$dependency$",
                                        attribute.getAttributeValue().getValue().toString());
                            }
                        }
                        if (!configAttributes.isEmpty()) {
                            match.setConfigAttributes(configAttributes);
                        }
                        if ((config == 1) && (uri == 1)) {
                            // If there is a configuration.
                            try {
                                LOGGER.debug("Configuration Call to : " + configUrl);
                                pdpResponse = configCall(pdpConfigLocation);
                            } catch (Exception e) {
                                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
                                pdpResponse.setStatus("Error in Calling the Configuration URL " + e,
                                        PolicyResponseStatus.NO_ACTION_REQUIRED, PolicyConfigStatus.CONFIG_NOT_FOUND);
                            }
                            pdpResponse.setPolicyName(policyName);
                            pdpResponse.setPolicyVersion(policyVersion);
                            pdpResponse.setMatchingConditions(matchingConditions);
                            pdpResponse.setResponseAttributes(responseAttributes);
                            if (!unique) {
                                combinedResult.add(pdpResponse);
                            } else {
                                if (!uniqueResult.isEmpty()) {
                                    if (uniqueResult.containsKey(priority)) {
                                        // Not any more unique, check the
                                        // matching conditions size
                                        int oldSize = uniqueResult.get(priority).getMatchingConditions().size();
                                        int newSize = matchingConditions.size();
                                        if (oldSize < newSize) {
                                            uniqueResult.put(priority, pdpResponse);
                                        } else if (oldSize == newSize) {
                                            pdpResponse = new PDPResponse();
                                            pdpResponse.setStatus(
                                                    "Two/more Policies have Same Priority and matching conditions, Please correct your policies.",
                                                    PolicyResponseStatus.NO_ACTION_REQUIRED,
                                                    PolicyConfigStatus.CONFIG_NOT_FOUND);
                                            combinedResult.add(pdpResponse);
                                            unique = false;
                                            return combinedResult;
                                        }
                                    } else {
                                        uniqueResult.put(priority, pdpResponse);
                                    }
                                } else {
                                    uniqueResult.put(priority, pdpResponse);
                                }
                            }
                        } else {
                            // Else it is Action Taken.
                            LOGGER.info("Action Taken by PDP. ");
                            actionTaken.putAll(adviseAttributes);
                            pdpResponse.setActionTaken(actionTaken);
                            pdpResponse.setPolicyResponseStatus(PolicyResponseStatus.ACTION_TAKEN);
                            pdpResponse.setPolicyResponseMessage("Action Taken by the PDP");
                            combinedResult.add(pdpResponse);
                        }
                    }
                }
                if (!result.getObligations().isEmpty()) {
                    // Obligation actions
                    // Action advised should be in obligations.
                    for (Obligation obligation : result.getObligations()) {
                        Map<String, String> actionAdvised = new HashMap<>();
                        PDPResponse pdpResponse = new PDPResponse();
                        for (AttributeAssignment attribute : obligation.getAttributeAssignments()) {
                            actionAdvised.put(attribute.getAttributeId().stringValue(),
                                    attribute.getAttributeValue().getValue().toString());
                        }
                        pdpResponse.setActionAdvised(actionAdvised);
                        pdpResponse.setPolicyResponseStatus(PolicyResponseStatus.ACTION_ADVISED);
                        pdpResponse.setPolicyResponseMessage("Action has been Advised ");
                        combinedResult.add(pdpResponse);
                    }
                }
            }
        }
        if (unique) {
            // Select Unique policy.
            int minNum = DEFAULT_PRIORITY;
            for (int num : uniqueResult.keySet()) {
                if (num < minNum) {
                    minNum = num;
                }
            }
            combinedResult.add(uniqueResult.get(minNum));
            // Turn off Unique
            unique = false;
        }

        return combinedResult;
    }

    /**
     * Process Decision Result.
     * 
     * @param result input from Response.
     * @return pdpResposne based on result.
     */
    private PDPResponse processDecisionResult(Result result) {
        PDPResponse pdpResponse = new PDPResponse();
        pdpResponse.setDecision(PolicyDecision.DENY);

        if (!result.getDecision().equals(Decision.PERMIT)) {
            LOGGER.info("processDecisionResult: Decision not a Permit. " + result.getDecision().toString());
            String indeterminatePropValue = XACMLProperties.getProperty("decision.indeterminate.response");
            if (result.getDecision().equals(Decision.INDETERMINATE) && indeterminatePropValue != null) {
                if ("PERMIT".equalsIgnoreCase(indeterminatePropValue)) {
                    pdpResponse.setDecision(PolicyDecision.PERMIT);
                } else {
                    pdpResponse.setDecision(PolicyDecision.DENY);
                }
            } else {
                pdpResponse.setDecision(PolicyDecision.DENY);
            }
            for (Advice advice : result.getAssociatedAdvice()) {
                for (AttributeAssignment attribute : advice.getAttributeAssignments()) {
                    pdpResponse.setDetails(attribute.getAttributeValue().getValue().toString());
                    break;
                }
            }
            pdpResponse.setStatus(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Params passed: Decision not a Permit.",
                    PolicyResponseStatus.NO_ACTION_REQUIRED, PolicyConfigStatus.CONFIG_NOT_FOUND);
        } else {
            checkDecision(pdpResponse, result);
        }
        return pdpResponse;
    }


    /**
     * Check for Decision for decision based calls.
     * 
     * @param pdpResponseInput pdpResponse.
     * @param result result.
     * @return pdpResponse.
     */
    private PDPResponse checkDecision(PDPResponse pdpResponseInput, Result result) {
        PDPResponse pdpResponse = pdpResponseInput;
        // check for Decision for decision based calls.
        pdpResponse.setDecision(PolicyDecision.PERMIT);

        // if this is a Rainy Day treatment decision we need to get
        // the selected treatment
        if (!Strings.isNullOrEmpty(requestType) && RAINYDAY_TYPE.equals(requestType)) {
            pdpResponse.setDetails(getRainyDayTreatment(result));
        } else if (!Strings.isNullOrEmpty(requestType) && DECISION_MS_NAMING_TYPE.equals(requestType)) {
            boolean configRetrieved = false;
            for (Advice advice : result.getAssociatedAdvice()) {
                configRetrieved = checkConfig(advice, configRetrieved, pdpResponse);
            }
            if (!configRetrieved) {
                LOGGER.error(" Failed to retrieve Config data for " + DECISION_MS_NAMING_TYPE);
                pdpResponse.setDetails("Error in retrieving Config Data");
            }

        } else {
            pdpResponse.setDetails("Decision Permit. OK!");
        }
        return pdpResponse;
    }

    /**
     * Check if there is a configuration.
     * 
     * @param advice advice.
     * @param configRetrieved boolean.
     * @param pdpResponse pdpResposneInput.
     * @return pdpResponse.
     */
    private boolean checkConfig(Advice advice, boolean configRetrieved, PDPResponse pdpResponse) {
        for (AttributeAssignment attribute : advice.getAttributeAssignments()) {
            if (attribute.getDataTypeId().stringValue().endsWith("anyURI")) {
                String configUrl = attribute.getAttributeValue().getValue().toString();
                String pdpConfigLocation =
                        configUrl.replace("$URL", XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_WEBAPPS));
                // If there is a configuration.
                try {
                    LOGGER.debug("processDecisionResult: Configuration Call to : " + configUrl);
                    pdpResponse = configCall(pdpConfigLocation);
                    pdpResponse.setDecision(PolicyDecision.PERMIT);
                    pdpResponse.setDetails(pdpResponse.getConfig());
                    configRetrieved = true;
                    break;
                } catch (Exception e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
                    LOGGER.error(" Failed to retrieve Config data for " + configUrl);
                    pdpResponse.setDetails("Error in retrieving Config Data from the Configuration URL " + configUrl);
                }
            }
        }
        return configRetrieved;
    }

    private String getRainyDayTreatment(Result result) {
        String treatment = null;
        if (result != null && !result.getAssociatedAdvice().isEmpty()) {
            // Get the desired treatment for requested errorCode from the Advice
            for (Advice advice : result.getAssociatedAdvice()) {
                Map<String, String> adviseAttributes = new HashMap<>();
                for (AttributeAssignment attribute : advice.getAttributeAssignments()) {
                    adviseAttributes.put(attribute.getAttributeId().stringValue(),
                            attribute.getAttributeValue().getValue().toString());
                    if ("treatment".equalsIgnoreCase(attribute.getAttributeId().stringValue())) {
                        treatment = attribute.getAttributeValue().getValue().toString();
                    }
                }
            }
        }
        return treatment;
    }

    private PDPResponse configCall(String pdpConfigLocation) throws PDPException, IOException {
        PDPResponse pdpResponse = new PDPResponse();
        if (pdpConfigLocation.contains("/")) {
            pdpConfigLocation = pdpConfigLocation.replace("/", File.separator);
        }
        try (InputStream inputStream = new FileInputStream(new File(pdpConfigLocation))) {
            if (pdpConfigLocation.endsWith("json")) {
                pdpResponse.setType(PolicyType.JSON);
                try (JsonReader jsonReader = Json.createReader(inputStream)) {
                    pdpResponse.setConfig(jsonReader.readObject().toString());
                }
            } else if (pdpConfigLocation.endsWith("xml")) {
                pdpResponse.setType(PolicyType.XML);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                DocumentBuilder db = null;
                try {
                    db = dbf.newDocumentBuilder();
                    Document document = db.parse(inputStream);
                    DOMSource domSource = new DOMSource(document);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer;
                    transformer = tf.newTransformer();
                    transformer.transform(domSource, result);
                    pdpResponse.setConfig(writer.toString());
                } catch (Exception e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
                    throw new PDPException(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Unable to parse the XML config",
                            e);
                }
            } else if (pdpConfigLocation.endsWith("properties")) {
                pdpResponse.setType(PolicyType.PROPERTIES);
                Properties configProp = new Properties();
                configProp.load(inputStream);
                Map<String, String> propVal = new HashMap<>();
                for (String name : configProp.stringPropertyNames()) {
                    propVal.put(name, configProp.getProperty(name));
                }
                pdpResponse.setProperty(propVal);
            } else if (pdpConfigLocation.endsWith("txt")) {
                pdpResponse.setType(PolicyType.OTHER);
                String other = IOUtils.toString(inputStream);
                IOUtils.closeQuietly(inputStream);
                pdpResponse.setConfig(other);
            } else {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Config Not Found");
                pdpResponse.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
                pdpResponse.setPolicyConfigMessage("Illegal form of Configuration Type Found.");
                inputStream.close();
                return pdpResponse;
            }
            LOGGER.info("config Retrieved " + pdpConfigLocation);
            pdpResponse.setStatus("Config Retrieved! ", PolicyResponseStatus.NO_ACTION_REQUIRED,
                    PolicyConfigStatus.CONFIG_RETRIEVED);
            return pdpResponse;
        } catch (FileNotFoundException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            throw new PDPException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in ConfigURL", e);
        } catch (IOException | ParserConfigurationException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            throw new PDPException(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Cannot open a connection to the configURL",
                    e);
        }
    }

    /**
     * Call pdp.
     *
     * @param request the request
     * @param requestIdParam the request id param
     * @return the response
     */
    public Response callPdp(Request request, UUID requestIdParam) {
        policyList = new ArrayList<>();
        Response response = null;
        // Get the PDPEngine
        if (requestIdParam == null) {
            requestIdParam = UUID.randomUUID();
            LOGGER.debug("No request ID provided, sending generated ID: " + requestIdParam.toString());
        } else {
            LOGGER.debug("Using provided request ID: " + requestIdParam.toString());
        }
        PDPEngine pdpEngine = XACMLPdpServlet.getPDPEngine();
        if (pdpEngine == null) {
            String message = "PDPEngine not loaded.";
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message + "\n RequestId : " + requestIdParam);
            return response;
        }
        XACMLPdpServlet.monitor.pdpEvaluationAttempts();
        // call the PDPEngine to decide and give the response on the Request.
        long timeStart;
        long timeEnd;
        try {
            synchronized (XACMLPdpServlet.getPDPEngineLock()) {
                timeStart = System.currentTimeMillis();
                response = pdpEngine.decide(request);
                timeEnd = System.currentTimeMillis();
            }

            String outgoingResponseString = null;
            if (DECISION_RAW_XACML_JSON_TYPE.equalsIgnoreCase(requestFormat)) {
                outgoingResponseString = JSONResponse.toString(response);
                LOGGER.info("Response from the PDP is : \n" + JSONResponse.toString(response, true) + "\n RequestId : "
                        + requestIdParam + " pdpEngine : " + pdpEngine);
            } else {
                outgoingResponseString = DOMResponse.toString(response);
                LOGGER.info("Response from the PDP is : \n" + DOMResponse.toString(response, true) + "\n RequestId : "
                        + requestIdParam + " pdpEngine : " + pdpEngine);
            }

            XACMLPdpServlet.monitor.computeLatency(timeEnd - timeStart);

            // adding the jmx values for NA, Permit and Deny
            //
            if (outgoingResponseString.contains("NotApplicable")
                    || outgoingResponseString.contains("Decision not a Permit")) {
                XACMLPdpServlet.monitor.pdpEvaluationNA();
            }

            if (outgoingResponseString.contains("Permit")
                    && !outgoingResponseString.contains("Decision not a Permit")) {
                XACMLPdpServlet.monitor.pdpEvaluationPermit();
            }

            if (outgoingResponseString.contains("Deny")) {
                XACMLPdpServlet.monitor.pdpEvaluationDeny();
            }
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e + "\n RequestId : " + requestIdParam);
            return null;
        }
        return response;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * Returns the requestFormat.
     * 
     * @return the requestFormat.
     */
    public String getRequestFormat() {
        return requestFormat;
    }

    /**
     * Set the Request Format.
     * 
     * @param requestMode to set requestFormat.
     */
    public void setRequestFormat(String requestMode) {
        this.requestFormat = requestMode;
    }

}
