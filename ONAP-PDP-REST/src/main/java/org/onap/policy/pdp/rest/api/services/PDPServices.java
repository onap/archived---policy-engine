/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import com.att.research.xacml.api.Advice;
import com.att.research.xacml.api.AttributeAssignment;
import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Obligation;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import com.att.research.xacml.util.XACMLProperties;

public class PDPServices {
    private static Logger LOGGER = FlexLogger.getLogger(PDPServices.class.getName());
    // Change the default Priority value here. 
    private static final int DEFAULT_PRIORITY = 9999;
    private boolean unique = false;
    private Boolean decide = false;
    private Matches match = null;
    
    public Collection<PDPResponse> generateRequest(String jsonString, UUID requestID, boolean unique, boolean decide) throws PolicyException{
        this.unique = unique;
        this.decide = decide;
        Collection<PDPResponse> results = null;
        Response response = null;
        // Create Request. We need XACML API here.
        try {
            Request request = JSONRequest.load(jsonString);
            // Call the PDP
            LOGGER.debug("--- Generating Request: ---\n" + JSONRequest.toString(request));
            response = callPDP(request, requestID);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
            PDPResponse pdpResponse = new PDPResponse();
            results = new HashSet<PDPResponse>();
            pdpResponse.setPolicyConfigMessage("Unable to Call PDP. Error with the URL");
            pdpResponse.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
            pdpResponse.setPolicyResponseStatus(PolicyResponseStatus.NO_ACTION_REQUIRED);
            results.add(pdpResponse);
            throw new PolicyException(e);
        }
        if (response != null) {
            results = checkResponse(response);
        } else {
            LOGGER.debug("No Response Received from PDP");
            PDPResponse pdpResponse = new PDPResponse();
            results = new HashSet<PDPResponse>();
            pdpResponse.setPolicyConfigMessage("No Response Received");
            pdpResponse.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
            pdpResponse.setPolicyResponseStatus(PolicyResponseStatus.NO_ACTION_REQUIRED);
            results.add(pdpResponse);
        }
        return results;
    }

    private Collection<PDPResponse> checkResponse(Response response) throws PolicyException{
        String pdpConfigLocation = null;
        Collection<PDPResponse> combinedResult = new HashSet<>();
        int priority = DEFAULT_PRIORITY;
        Map<Integer, PDPResponse> uniqueResult = new HashMap<>();
        for (Result result : response.getResults()) {
            if (!result.getDecision().equals(Decision.PERMIT)) {
                LOGGER.debug("Decision not a Permit. "  + result.getDecision().toString());
                PDPResponse pdpResponse = new PDPResponse();
                if (decide) {
                    pdpResponse.setDecision(PolicyDecision.DENY);
                    for(Advice advice: result.getAssociatedAdvice()){
                        for(AttributeAssignment attribute: advice.getAttributeAssignments()){
                            pdpResponse.setDetails(attribute.getAttributeValue().getValue().toString());
                            break;
                        }
                    }
                    combinedResult.add(pdpResponse);
                    return combinedResult;
                }
                pdpResponse.setStatus(XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Params passed: Decision not a Permit.",PolicyResponseStatus.NO_ACTION_REQUIRED,PolicyConfigStatus.CONFIG_NOT_FOUND);
                combinedResult.add(pdpResponse);
                return combinedResult;
            } else {
                if (decide) {
                    // check for Decision for decision based calls.
                    PDPResponse pdpResponse = new PDPResponse();
                    pdpResponse.setDecision(PolicyDecision.PERMIT);
                    pdpResponse.setDetails("Decision Permit. OK!");
                    combinedResult.add(pdpResponse);
                    return combinedResult;
                }
                if (!result.getAssociatedAdvice().isEmpty()) {
                    // Configurations should be in advice. 
                    // Also PDP took actions could be here.
                    for (Advice advice : result.getAssociatedAdvice()) {
                        int config = 0, uri = 0;
                        String configURL = null;
                        String policyName = null;
                        String policyVersion = null;
                        match = new Matches();
                        Map<String, String> matchingConditions = new HashMap<>();
                        Map<String, String> configAttributes = new HashMap<>();
                        Map<String, String> responseAttributes = new HashMap<>();
                        Map<String, String> actionTaken = new HashMap<>();
                        PDPResponse pdpResponse = new PDPResponse();
                        Map<String, String> adviseAttributes = new HashMap<>();
                        for (AttributeAssignment attribute : advice.getAttributeAssignments()) {
                            adviseAttributes.put(attribute.getAttributeId().stringValue(), attribute.getAttributeValue().getValue().toString());
                            if (attribute.getAttributeValue().getValue().toString().equalsIgnoreCase("CONFIGURATION")) {
                                config++;
                            } else if (attribute.getDataTypeId().stringValue().endsWith("anyURI")) {
                                uri++;
                                if (uri == 1) {
                                    configURL = attribute.getAttributeValue().getValue().toString();
                                    pdpConfigLocation = configURL.replace("$URL", XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_WEBAPPS));
                                } else {
                                    if (!(attribute.getIssuer().equalsIgnoreCase("PDP"))) {
                                        throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error having multiple URI in the Policy");
                                    }
                                }
                            } else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("PolicyName")) {
                                policyName = attribute.getAttributeValue().getValue().toString();
                            } else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("VersionNumber")) {
                                policyVersion = attribute.getAttributeValue().getValue().toString();
                            } else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("Priority")){
                                try{
                                    priority = Integer.parseInt(attribute.getAttributeValue().getValue().toString());
                                } catch(Exception e){
                                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+ "Unable to Parse Integer for Priority. Setting to default value");
                                    priority = DEFAULT_PRIORITY;
                                }
                            } else if (attribute.getAttributeId().stringValue().startsWith("matching")) {
                                matchingConditions.put(attribute.getAttributeId().stringValue()
                                        .replaceFirst("(matching).", ""),attribute.getAttributeValue().getValue().toString());
                                if (attribute.getAttributeId().stringValue()
                                        .replaceFirst("(matching).", "").equals("ONAPName")) {
                                    match.setOnapName(attribute.getAttributeValue().getValue().toString());
                                } else if (attribute.getAttributeId().stringValue()
                                        .replaceFirst("(matching).", "").equals("ConfigName")) {
                                    match.setConfigName(attribute.getAttributeValue().getValue().toString());
                                } else {
                                    configAttributes.put(attribute.getAttributeId().stringValue()
                                            .replaceFirst("(matching).", ""),attribute.getAttributeValue().getValue().toString());
                                }
                            } else if (attribute.getAttributeId().stringValue().startsWith("key:")) {
                                responseAttributes.put(attribute.getAttributeId().stringValue().replaceFirst("(key).", ""),
                                        attribute.getAttributeValue().getValue().toString());
                            } else if (attribute.getAttributeId().stringValue().startsWith("controller:")) {
                                responseAttributes.put("$"+ attribute.getAttributeId().stringValue(),
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
                                LOGGER.debug("Configuration Call to : " + configURL);
                                pdpResponse = configCall(pdpConfigLocation);
                            } catch (Exception e) {
                                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+ e);
                                pdpResponse.setStatus("Error in Calling the Configuration URL "+ e,
                                                PolicyResponseStatus.NO_ACTION_REQUIRED,
                                                PolicyConfigStatus.CONFIG_NOT_FOUND);
                            }
                            pdpResponse.setPolicyName(policyName);
                            pdpResponse.setPolicyVersion(policyVersion);
                            pdpResponse.setMatchingConditions(matchingConditions);
                            pdpResponse.setResponseAttributes(responseAttributes);
                            if(!unique){
                                combinedResult.add(pdpResponse);
                            }else{
                                if(!uniqueResult.isEmpty()){
                                    if(uniqueResult.containsKey(priority)){
                                        // Not any more unique, check the matching conditions size
                                        int oldSize = uniqueResult.get(priority).getMatchingConditions().size();
                                        int newSize = matchingConditions.size();
                                        if(oldSize < newSize){
                                            uniqueResult.put(priority, pdpResponse);
                                        }else if(oldSize == newSize){
                                            pdpResponse = new PDPResponse();
                                            pdpResponse.setStatus("Two/more Policies have Same Priority and matching conditions, Please correct your policies.",
                                                    PolicyResponseStatus.NO_ACTION_REQUIRED,
                                                    PolicyConfigStatus.CONFIG_NOT_FOUND);
                                            combinedResult.add(pdpResponse);
                                            unique = false;
                                            return combinedResult;
                                        }
                                    }else{
                                        uniqueResult.put(priority, pdpResponse);
                                    }
                                }else{
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
        if(unique){
            // Select Unique policy. 
            int minNum = DEFAULT_PRIORITY;
            for(int num: uniqueResult.keySet()){
                if(num < minNum){
                    minNum = num;
                }
            }
            combinedResult.add(uniqueResult.get(minNum));
            // Turn off Unique
            unique = false;
        }
        
        return combinedResult;
    }

    private PDPResponse configCall(String pdpConfigLocation) throws Exception{
        PDPResponse pdpResponse = new PDPResponse();
        if(pdpConfigLocation.contains("/")){
            pdpConfigLocation = pdpConfigLocation.replace("/", File.separator);
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(pdpConfigLocation));
            try {
                if (pdpConfigLocation.endsWith("json")) {
                    pdpResponse.setType(PolicyType.JSON);
                    JsonReader jsonReader = Json.createReader(inputStream);
                    pdpResponse.setConfig(jsonReader.readObject().toString());
                    jsonReader.close();
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
                        LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID+ e);
                        throw new Exception(XACMLErrorConstants.ERROR_SCHEMA_INVALID+ "Unable to parse the XML config", e);
                    }
                } else if (pdpConfigLocation.endsWith("properties")) {
                    pdpResponse.setType(PolicyType.PROPERTIES);
                    Properties configProp = new Properties();
                    configProp.load(inputStream);
                    Map<String, String> propVal = new HashMap<>();
                    for(String name: configProp.stringPropertyNames()) {
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
                pdpResponse.setStatus("Config Retrieved! ",
                        PolicyResponseStatus.NO_ACTION_REQUIRED,
                        PolicyConfigStatus.CONFIG_RETRIEVED);
                return pdpResponse;
            } catch (IOException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
                throw new Exception(XACMLErrorConstants.ERROR_PROCESS_FLOW +
                        "Cannot open a connection to the configURL", e);
            }
        } catch (MalformedURLException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in ConfigURL", e);
        }finally{
        	if(inputStream != null){
            	inputStream.close();
        	}
        }
    }

    private Response callPDP(Request request,
            UUID requestID) throws Exception{
        Response response = null;
        // Get the PDPEngine
        if (requestID == null) {
            requestID = UUID.randomUUID();
            LOGGER.debug("No request ID provided, sending generated ID: " + requestID.toString());
        } else {
            LOGGER.debug("Using provided request ID: " + requestID.toString());
        }
        PDPEngine pdpEngine = XACMLPdpServlet.getPDPEngine();
        if (pdpEngine == null) {
            String message = "PDPEngine not loaded.";
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message);
            return response;
        }
        // call the PDPEngine to decide and give the response on the Request.
        try {
            response = pdpEngine.decide(request);
            LOGGER.debug("Response from the PDP is: \n" + JSONResponse.toString(response));
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            return null;
        }
        return response;
    }

}
