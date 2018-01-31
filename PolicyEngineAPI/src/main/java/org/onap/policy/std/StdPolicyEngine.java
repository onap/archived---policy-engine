/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.std;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.api.DictionaryResponse;
import org.onap.policy.api.EventRequestParameters;
import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.MetricsRequestParameters;
import org.onap.policy.api.MetricsResponse;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyEventException;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyType;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.models.APIDictionaryResponse;
import org.onap.policy.models.APIPolicyConfigResponse;
import org.onap.policy.utils.AAFEnvironment;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * PolicyEngine Implementation class
 *
 * @version 1.0
 */
public class StdPolicyEngine {
    private static final String ERROR_AUTH_GET_PERM = "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to: ";
    private static final String DEFAULT_NOTIFICATION = "websocket";
    private static final String ERROR_DATA_ISSUE = "Invalid Data is given.";
    private static final String DMAAP = "dmaap";
    private static final String ERROR_INVALID_PDPS = "Unable to get valid Response from  PDP(s) ";
    private static final String ERROR_WHILE_CONNECTING = "Error while connecting to ";

    private static String clientEncoding = null;
    private String contentType = null;
    private static List<String> pdps = null;
    private static String environment = null;
    private static String userName = null;
    private static String pass = null;
    private static List<String> encoding = null;
    private static boolean junit = false;
    private List<String> notificationType = new ArrayList<>();
    private List<String> notificationURLList = new ArrayList<>();
    private NotificationScheme scheme = null;
    private NotificationHandler handler = null;
    private AutoClientUEB uebClientThread = null;
    private Thread registerUEBThread = null;
    private boolean uebThread = false;
    private AutoClientDMAAP dmaapClientThread = null;
    private Thread registerDMAAPThread = null;
    private boolean dmaapThread = false;
    private String topic = null;
    private String apiKey = null;
    private String apiSecret = null;

    private static final String UNIQUEID = UUID.randomUUID().toString();
    private static final Logger LOGGER = FlexLogger.getLogger(StdPolicyConfig.class.getName());

    /*
     * Taking the Property file even if it null.
     */
    public StdPolicyEngine(String propertyFilePath, String clientKey) throws PolicyEngineException {
        setProperty(propertyFilePath, clientKey);
    }

    /*
     * Taking the Notification Constructor.
     */
    public StdPolicyEngine(String propertyFilePath, NotificationScheme scheme, NotificationHandler handler)
            throws PolicyEngineException {
        setProperty(propertyFilePath, null);
        this.scheme = scheme;
        this.handler = handler;
        if ((!"ueb".equals(notificationType.get(0))) || (!DMAAP.equals(notificationType.get(0)))) {
            AutoClientEnd.setAuto(scheme, handler);
        }
        notification(scheme, handler);
    }

    /*
     * Taking the Notification Constructor.
     */
    public StdPolicyEngine(String propertyFilePath, NotificationScheme scheme) throws PolicyEngineException {
        setProperty(propertyFilePath, null);
        this.scheme = scheme;
        setScheme(scheme);
    }

    /*
     * sendEvent API Implementation
     */
    public Collection<PolicyResponse> sendEvent(Map<String, String> eventAttributes, UUID requestID)
            throws PolicyEventException {
        return sendEventImpl(eventAttributes, requestID);
    }

    /*
     * sendEvent API Implementation for eventRequestParameters
     */
    public Collection<PolicyResponse> sendEvent(EventRequestParameters eventRequestParameters)
            throws PolicyEventException {
        if (eventRequestParameters == null) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No event Request Parameters Given. ";
            LOGGER.error(message);
            throw new PolicyEventException(message);
        }
        return sendEventImpl(eventRequestParameters.getEventAttributes(), eventRequestParameters.getRequestID());
    }

    /*
     * getConfig using configRequestParameters Implementation
     */
    public Collection<PolicyConfig> getConfig(ConfigRequestParameters configRequestParameters)
            throws PolicyConfigException {
        return getConfigImpl(configRequestParameters);
    }

    /*
     * listPolicies using configRequestParameters Implementation
     */
    public Collection<String> listConfig(ConfigRequestParameters listPolicyRequestParameters)
            throws PolicyConfigException {
        return listConfigImpl(listPolicyRequestParameters);
    }

    /*
     * getDecision using the decision Attributes.
     */
    public DecisionResponse getDecision(String onapName, Map<String, String> decisionAttributes, UUID requestID)
            throws PolicyDecisionException {
        return getDecisionImpl(onapName, decisionAttributes, requestID);
    }

    /*
     * getDecision Using decisionRequestParameters.
     */
    public DecisionResponse getDecision(DecisionRequestParameters decisionRequestParameters)
            throws PolicyDecisionException {
        if (decisionRequestParameters == null) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Request Parameters Given. ";
            LOGGER.error(message);
            throw new PolicyDecisionException(message);
        }
        return getDecisionImpl(decisionRequestParameters.getOnapName(),
                decisionRequestParameters.getDecisionAttributes(), decisionRequestParameters.getRequestID());
    }

    /*
     * getMetrics using metricsRequestParameters
     */
    public MetricsResponse getMetrics(MetricsRequestParameters parameters) throws PolicyException {
        return getMetricsImpl(parameters);
    }

    public MetricsResponse getMetricsImpl(MetricsRequestParameters parameters) throws PolicyException {
        StdMetricsResponse response = new StdMetricsResponse();
        String resource = "getMetrics";
        String body = null;
        // Create the Request
        try {
            if (parameters != null) {
                body = PolicyUtils.objectToJsonString(parameters);
            }
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<String> result = callNewPDP(resource, HttpMethod.GET, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (PolicyException exception) {
            if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
                LOGGER.error(exception);
                HttpClientErrorException ex = (HttpClientErrorException) exception.getCause();
                response.setResponseCode(ex.getRawStatusCode());
                response.setResponseMessage(exception.getMessage());
                return response;
            } else {
                String message = XACMLErrorConstants.ERROR_SYSTEM_ERROR
                        + "Error while processing results. please check logs.";
                LOGGER.error(message, exception);
                throw new PolicyException(message, exception);
            }
        }
        return response;
    }

    /*
     * PushPolicy using pushPolicyParameters.
     */
    public PolicyChangeResponse pushPolicy(PushPolicyParameters pushPolicyParameters) throws PolicyException {
        return pushPolicyImpl(pushPolicyParameters);
    }

    public PolicyChangeResponse pushPolicyImpl(PushPolicyParameters pushPolicyParameters) throws PolicyException {
        StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String resource = "pushPolicy";
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(pushPolicyParameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<String> result = callNewPDP(resource, HttpMethod.PUT, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (PolicyException exception) {
            return processException(exception);
        }
        return response;
    }

    /*
     * Delete a Policy using deletePolicyParameters
     */
    public PolicyChangeResponse deletePolicy(DeletePolicyParameters parameters) throws PolicyException {
        return deletePolicyImpl(parameters);
    }

    public PolicyChangeResponse deletePolicyImpl(DeletePolicyParameters parameters) throws PolicyException {
        StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String resource = "deletePolicy";
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(parameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<String> result = callNewPDP(resource, HttpMethod.DELETE, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (PolicyException exception) {
            return processException(exception);
        }
        return response;
    }

    /*
     * getDictionaryItem Using dictionaryParameters
     */
    public DictionaryResponse getDictionaryItem(DictionaryParameters parameters) throws PolicyException {
        return getDictionaryItemImpl(parameters);
    }

    public DictionaryResponse getDictionaryItemImpl(DictionaryParameters parameters) throws PolicyException {
        StdDictionaryResponse response = new StdDictionaryResponse();
        String resource = "getDictionaryItems";
        String body = "{}";
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(parameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<APIDictionaryResponse> result = callNewPDP(resource, HttpMethod.POST, body,
                    APIDictionaryResponse.class);
            // Process response
            response = dictionaryResult(result.getBody());
        } catch (Exception exception) {
            if (exception.getCause().getMessage().contains("401")) {
                String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_AUTH_GET_PERM + resource;
                LOGGER.error(message);
                response.setResponseMessage(message);
                response.setResponseCode(401);
                return response;
            }
            if (exception.getCause().getMessage().contains("400")) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ERROR_DATA_ISSUE;
                response.setResponseMessage(message);
                response.setResponseCode(400);
                return response;
            }
            String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_INVALID_PDPS
                    + pdps;
            LOGGER.error(message, exception);
            response.setResponseMessage(message);
            response.setResponseCode(500);
            return response;
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private StdDictionaryResponse dictionaryResult(APIDictionaryResponse body) {
        StdDictionaryResponse response = new StdDictionaryResponse();
        response.setResponseCode(body.getResponseCode());
        response.setResponseMessage(body.getResponseMessage());
        response.setDictionaryData((Map<String, String>) body.getDictionaryData());
        if (body.getDictionaryJson() != null) {
            Gson objGson = new GsonBuilder().create();
            String mapToJson = objGson.toJson(body.getDictionaryJson());
            JsonReader jsonReader = Json.createReader(new StringReader(mapToJson));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();
            response.setDictionaryJson(object);
        }
        return response;
    }

    /*
     * createDictinaryItem Using dictionaryParameters.
     */
    public PolicyChangeResponse createDictionaryItem(DictionaryParameters parameters) throws PolicyException {
        return createUpdateDictionaryItemImpl(parameters, false);
    }

    /*
     * updateDictinaryItem Using dictionaryParameters.
     */
    public PolicyChangeResponse updateDictionaryItem(DictionaryParameters parameters) throws PolicyException {
        return createUpdateDictionaryItemImpl(parameters, true);
    }

    public PolicyChangeResponse createUpdateDictionaryItemImpl(DictionaryParameters parameters, boolean updateFlag)
            throws PolicyException {
        StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String resource = "createDictionaryItem";
        if (updateFlag) {
            resource = "updateDictionaryItem";
        }
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(parameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<String> result = callNewPDP(resource, HttpMethod.PUT, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (PolicyException exception) {
            return processException(exception);
        }
        return response;
    }

    /*
     * PolicyEngine Import
     */
    public PolicyChangeResponse policyEngineImport(ImportParameters importParameters) throws PolicyException {
        return policyEngineImportImpl(importParameters);
    }

    public PolicyChangeResponse policyEngineImportImpl(ImportParameters importParameters) throws PolicyException {
        StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String resource = "policyEngineImport";
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        // Create Request.
        try {
            String body = PolicyUtils.objectToJsonString(importParameters);
            parameters.set("importParametersJson", body);
            parameters.set("file", new FileSystemResource(importParameters.getFilePath()));
        } catch (Exception e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        contentType = MediaType.MULTIPART_FORM_DATA_VALUE;
        // Get Response.
        try {
            ResponseEntity<String> result = callNewPDP(resource, HttpMethod.POST, parameters, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (PolicyException exception) {
            return processException(exception);
        } finally {
            contentType = null;
        }
        return response;
    }

    /*
     * createPolicy Using policyParameters.
     */
    public PolicyChangeResponse createPolicy(PolicyParameters policyParameters) throws PolicyException {
        return createUpdatePolicyImpl(policyParameters, false);
    }

    /*
     * updatePolicy using policyParameters.
     */
    public PolicyChangeResponse updatePolicy(PolicyParameters policyParameters) throws PolicyException {
        return createUpdatePolicyImpl(policyParameters, true);
    }

    public PolicyChangeResponse createUpdatePolicyImpl(PolicyParameters policyParameters, boolean updateFlag)
            throws PolicyException {
        StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String resource = "createPolicy";
        if (updateFlag) {
            resource = "updatePolicy";
        }
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(policyParameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<String> result = callNewPDP(resource, HttpMethod.PUT, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (PolicyException exception) {
            return processException(exception);
        }
        return response;
    }

    private PolicyChangeResponse processException(PolicyException exception) throws PolicyException {
        StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
            LOGGER.error(exception);
            HttpClientErrorException ex = (HttpClientErrorException) exception.getCause();
            response.setResponseCode(ex.getRawStatusCode());
            response.setResponseMessage(exception.getMessage());
            return response;
        } else {
            String message = XACMLErrorConstants.ERROR_SYSTEM_ERROR
                    + "Error while processing results. please check logs.";
            LOGGER.error(message, exception);
            throw new PolicyException(message, exception);
        }
    }

    public DecisionResponse getDecisionImpl(String onapName, Map<String, String> decisionAttributes, UUID requestID)
            throws PolicyDecisionException {
        String resource = "getDecision";
        StdDecisionResponse response = new StdDecisionResponse();
        String body = null;
        // Create Request.
        try {
            DecisionRequestParameters decisionRequestParameters = new DecisionRequestParameters();
            decisionRequestParameters.setDecisionAttributes(decisionAttributes);
            decisionRequestParameters.setOnapName(onapName);
            decisionRequestParameters.setRequestID(requestID);
            body = PolicyUtils.objectToJsonString(decisionRequestParameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyDecisionException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<StdDecisionResponse> result = callNewPDP(resource, HttpMethod.POST, body,
                    StdDecisionResponse.class);
            // Process response
            response = result.getBody();
        } catch (Exception exception) {
            if (exception.getCause().getMessage().contains("401")) {
                String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_AUTH_GET_PERM + resource;
                LOGGER.error(message);
                throw new PolicyDecisionException(message, exception);
            }
            if (exception.getCause().getMessage().contains("400")) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ERROR_DATA_ISSUE;
                LOGGER.error(message);
                throw new PolicyDecisionException(message, exception);
            }
            String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_INVALID_PDPS
                    + pdps;
            LOGGER.error(message, exception);
            throw new PolicyDecisionException(message, exception);
        }
        return response;
    }

    public Collection<PolicyConfig> getConfigImpl(ConfigRequestParameters configRequestParameters)
            throws PolicyConfigException {
        String resource = "getConfig";
        ArrayList<PolicyConfig> response = null;
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(configRequestParameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyConfigException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<APIPolicyConfigResponse[]> result = callNewPDP(resource, HttpMethod.POST, body,
                    APIPolicyConfigResponse[].class);
            // Process Response
            response = configResult(result.getBody());
        } catch (Exception exception) {
            if (exception.getCause().getMessage().contains("401")) {
                String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_AUTH_GET_PERM + resource;
                LOGGER.error(message);
                throw new PolicyConfigException(message, exception);
            }
            if (exception.getCause().getMessage().contains("400")) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ERROR_DATA_ISSUE;
                LOGGER.error(message);
                throw new PolicyConfigException(message, exception);
            }
            String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_INVALID_PDPS
                    + pdps;
            LOGGER.error(message, exception);
            throw new PolicyConfigException(message, exception);
        }
        return response;
    }

    private ArrayList<PolicyConfig> configResult(APIPolicyConfigResponse[] response) throws PolicyConfigException {
        ArrayList<PolicyConfig> result = new ArrayList<>();
        if (response != null && response.length > 0) {
            for (APIPolicyConfigResponse policyConfigResponse : response) {
                StdPolicyConfig policyConfig = new StdPolicyConfig();
                policyConfig.setConfigStatus(policyConfigResponse.getPolicyConfigMessage());
                policyConfig.setMatchingConditions(policyConfigResponse.getMatchingConditions());
                policyConfig.setPolicyConfigStatus(policyConfigResponse.getPolicyConfigStatus());
                policyConfig.setPolicyName(policyConfigResponse.getPolicyName());
                policyConfig.setPolicyType(policyConfigResponse.getType());
                policyConfig.setPolicyVersion(policyConfigResponse.getPolicyVersion());
                policyConfig.setPolicyType(policyConfigResponse.getPolicyType());
                policyConfig.setResponseAttributes(policyConfigResponse.getResponseAttributes());
                setMatches(policyConfig.getMatchingConditions());
                if (policyConfigResponse.getType() != null) {
                    try {
                        switch (policyConfigResponse.getType()) {
                            case JSON:
                                JsonReader jsonReader = Json
                                        .createReader(new StringReader(policyConfigResponse.getConfig()));
                                JsonObject object = jsonReader.readObject();
                                jsonReader.close();
                                policyConfig.setJsonObject(object);
                                break;
                            case OTHER:
                                policyConfig.setOther(policyConfigResponse.getConfig());
                                break;
                            case PROPERTIES:
                                Properties props = new Properties();
                                props.putAll(policyConfigResponse.getProperty());
                                policyConfig.setProperties(props);
                                break;
                            case XML:
                                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder builder;
                                builder = factory.newDocumentBuilder();
                                policyConfig.setDocument(builder
                                        .parse(new InputSource(new StringReader(policyConfigResponse.getConfig()))));
                                break;
                        }
                    } catch (Exception e) {
                        LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e);
                        throw new PolicyConfigException(
                                XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Unable to parse the config", e);
                    }
                }
                result.add(policyConfig);
            }
        }
        return result;
    }

    private void setMatches(Map<String, String> matchingConditions) {
        Matches match = new Matches();
        HashMap<String, String> configAttributes = new HashMap<>();
        try {
            for (Map.Entry<String,String> entry : matchingConditions.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("ONAPName")) {
                    match.setOnapName(entry.getValue());
                } else if (entry.getKey().equalsIgnoreCase("ConfigName")) {
                    match.setConfigName(entry.getValue());
                } else {
                    configAttributes.put(entry.getKey(), entry.getValue());
                }
            }
            if (!configAttributes.isEmpty()) {
                match.setConfigAttributes(configAttributes);
            }
            MatchStore.storeMatch(match);
        } catch (Exception e) {
            LOGGER.error("StoreMatch failed for Onap:" + match.getOnapName() + " Config: " + match.getConfigName(), e);
        }
    }

    /*
     * Generic Rest Client to call PDP services.
     */
    private <T> ResponseEntity<T> callNewPDP(String resource, HttpMethod method, Object body, Class<T> responseType)
            throws PolicyException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> requestEntity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<T> result = null;
        HttpClientErrorException exception = null;
        int pdpsCount = 0;
        while (pdpsCount < pdps.size()) {
            try {
                result = restTemplate.exchange(pdps.get(0) + "/api/" + resource, method, requestEntity, responseType);
            } catch (HttpClientErrorException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_WHILE_CONNECTING + pdps.get(0), e);
                exception = e;
            } catch (Exception e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_WHILE_CONNECTING + pdps.get(0), e);
                exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            if (result == null) {
                Collections.rotate(pdps, -1);
                Collections.rotate(encoding, -1);
                pdpsCount++;
            } else {
                break;
            }
        }
        if (exception != null && exception.getStatusCode() != null) {
            if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                String message = XACMLErrorConstants.ERROR_PERMISSIONS + ":" + exception.getStatusCode() + ":"
                        + ERROR_AUTH_GET_PERM + resource;
                LOGGER.error(message);
                throw new PolicyException(message, exception);
            }
            if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ":" + exception.getStatusCode() + ":"
                        + exception.getResponseBodyAsString();
                LOGGER.error(message);
                throw new PolicyException(message, exception);
            }
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_WHILE_CONNECTING + pdps
                        + exception;
                LOGGER.error(message);
                throw new PolicyException(message, exception);
            }
            String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + exception.getStatusCode() + ":"
                    + exception.getResponseBodyAsString();
            LOGGER.error(message);
            throw new PolicyException(message, exception);
        }
        return result;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("ClientAuth", "Basic " + clientEncoding);
        headers.set("Authorization", "Basic " + encoding.get(0));
        if (contentType != null) {
            headers.set("Content-Type", contentType);
        } else {
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        }
        headers.set("Environment", environment);
        return headers;
    }

    private static void setClientEncoding() {
        Base64.Encoder encoder = Base64.getEncoder();
        clientEncoding = encoder.encodeToString((userName + ":" + pass).getBytes(StandardCharsets.UTF_8));
    }

    public Collection<String> listConfigImpl(ConfigRequestParameters listRequestParameters)
            throws PolicyConfigException {
        Collection<String> policyList = new ArrayList<>();
        if (junit) {
            policyList.add("Policy Name: listConfigTest");
            return policyList;
        }
        Collection<PolicyConfig> policyConfig = getConfigImpl(listRequestParameters);
        for (PolicyConfig policy : policyConfig) {
            if (policy.getPolicyConfigMessage() != null && policy.getPolicyConfigMessage().contains("PE300")) {
                policyList.add(policy.getPolicyConfigMessage());
            } else {
                policyList.add("Policy Name: " + policy.getPolicyName());
            }
        }
        return policyList;
    }

    public Collection<PolicyResponse> sendEventImpl(Map<String, String> eventAttributes, UUID requestID)
            throws PolicyEventException {
        String resource = "sendEvent";
        ArrayList<PolicyResponse> response = null;
        String body = null;
        // Create Request.
        try {
            // Long way here, can be shortened and will be done.
            EventRequestParameters eventRequestParameters = new EventRequestParameters();
            eventRequestParameters.setEventAttributes(eventAttributes);
            eventRequestParameters.setRequestID(requestID);
            body = PolicyUtils.objectToJsonString(eventRequestParameters);
        } catch (JsonProcessingException e) {
            String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyEventException(message, e);
        }
        // Get Response.
        try {
            ResponseEntity<StdPolicyResponse[]> result = callNewPDP(resource, HttpMethod.POST, body,
                    StdPolicyResponse[].class);
            // Process Response
            response = eventResult(result.getBody());
        } catch (Exception exception) {
            if (exception.getCause().getMessage().contains("401")) {
                String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_AUTH_GET_PERM + resource;
                LOGGER.error(message);
                throw new PolicyEventException(message, exception);
            }
            if (exception.getCause().getMessage().contains("400")) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ERROR_DATA_ISSUE;
                LOGGER.error(message);
                throw new PolicyEventException(message, exception);
            }
            String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_INVALID_PDPS
                    + pdps;
            LOGGER.error(message, exception);
            throw new PolicyEventException(message, exception);
        }
        return response;
    }

    private ArrayList<PolicyResponse> eventResult(StdPolicyResponse[] response){
        ArrayList<PolicyResponse> eventResult = new ArrayList<>();
        if (response != null && response.length > 0) {
            for (StdPolicyResponse policyConfigResponse : response) {
                eventResult.add(policyConfigResponse);
            }
        }
        return eventResult;
    }

    private void setProperty(String propertyFilePath, String clientKey) throws PolicyEngineException {
        if (propertyFilePath == null) {
            throw new PolicyEngineException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Error NO PropertyFile Path provided");
        } else {
            // Adding logic for remote Properties file.
            Properties prop = new Properties();
            if (propertyFilePath.startsWith("http")) {
                URL configURL;
                try {
                    configURL = new URL(propertyFilePath);
                    URLConnection connection = null;
                    connection = configURL.openConnection();
                    prop.load(connection.getInputStream());
                } catch (IOException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
                    throw new PolicyEngineException(
                            XACMLErrorConstants.ERROR_DATA_ISSUE + "Maformed property URL " + e.getMessage());
                }
            } else {
                Path file = Paths.get(propertyFilePath);
                if (!file.toFile().exists()) {
                    throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "File doesn't exist in the specified Path " + file.toString());
                }
                if (file.toString().endsWith(".properties")) {
                    InputStream in;
                    prop = new Properties();
                    try {
                        in = new FileInputStream(file.toFile());
                        prop.load(in);
                    } catch (IOException e) {
                        LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
                        throw new PolicyEngineException(
                                XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Cannot Load the Properties file", e);
                    }
                } else {
                    LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Not a .properties file " + propertyFilePath);
                    throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Not a .properties file");
                }
            }
            // UEB and DMAAP Settings
            String checkType = prop.getProperty("NOTIFICATION_TYPE");
            String serverList = prop.getProperty("NOTIFICATION_SERVERS");
            topic = prop.getProperty("NOTIFICATION_TOPIC");
            apiKey = prop.getProperty("UEB_API_KEY");
            apiSecret = prop.getProperty("UEB_API_SECRET");

            if (checkType == null) {
                notificationType.add(DEFAULT_NOTIFICATION);
                LOGGER.info(
                        "Properties file doesn't have the NOTIFICATION_TYPE parameter system will use defualt websockets");
            } else {
                checkType = checkType.trim();
                if (checkType.contains(",")) {
                    notificationType = new ArrayList<>(Arrays.asList(prop.getProperty("NOTIFICATION_TYPE").split(",")));
                } else {
                    notificationType = new ArrayList<>();
                    notificationType.add(checkType);
                }
            }
            if (serverList == null) {
                notificationType.clear();
                notificationType.add(DEFAULT_NOTIFICATION);
                LOGGER.info(
                        "Properties file doesn't have the NOTIFICATION_SERVERS parameter system will use defualt websockets");
            } else {
                serverList = serverList.trim();
                if (serverList.contains(",")) {
                    notificationURLList = new ArrayList<>(Arrays.asList(serverList.split(",")));
                } else {
                    notificationURLList = new ArrayList<>();
                    notificationURLList.add(serverList);
                }
            }

            if (topic != null) {
                topic = topic.trim();
            } else {
                LOGGER.error("Properties file doesn't have the NOTIFICATION_TOPIC parameter.");
            }

            // Client ID Authorization Settings.
            String clientID = prop.getProperty("CLIENT_ID");
            if (clientKey == null) {
                clientKey = prop.getProperty("CLIENT_KEY");
                try {
                    clientKey = PolicyUtils.decode(clientKey);
                } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS
                            + " Cannot Decode the given Password Proceeding with given Password!!", e);
                }
            }
            if (clientID == null || clientKey == null || clientID.isEmpty() || clientKey.isEmpty()) {
                LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS
                        + " Cannot proceed without the CLIENT_KEY and CLIENT_ID values !!");
                throw new PolicyEngineException(XACMLErrorConstants.ERROR_PERMISSIONS
                        + " Cannot proceed without the CLIENT_KEY and CLIENT_ID values !!");
            } else {
                setClientId(clientID.trim());
                setClientKey(clientKey.trim());
            }
            setEnvironment(prop);
            // Initializing the values.
            init();
            // Check the Keys for PDP_URLs
            Collection<Object> unsorted = prop.keySet();
            @SuppressWarnings({ "rawtypes", "unchecked" })
            List<String> sorted = new ArrayList(unsorted);
            Collections.sort(sorted);
            for (String propKey : sorted) {
                if (propKey.startsWith("PDP_URL")) {
                    String checkVal = prop.getProperty(propKey);
                    if (checkVal == null) {
                        throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE
                                + "Properties file doesn't have the PDP_URL parameter");
                    }
                    if (checkVal.contains(";")) {
                        List<String> pdpDefault = new ArrayList<>(Arrays.asList(checkVal.split("\\s*;\\s*")));
                        int pdpCount = 0;
                        while (pdpCount < pdpDefault.size()) {
                            String pdpVal = pdpDefault.get(pdpCount);
                            readPDPParam(pdpVal);
                            pdpCount++;
                        }
                    } else {
                        readPDPParam(checkVal);
                    }
                }
            }
            if (pdps == null || pdps.isEmpty()) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PDP_URLs");
                throw new PolicyEngineException(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PDP_URLs");
            }
            // Get JUNIT property from properties file when running tests
            checkJunit(prop);
        }
    }

    private static void checkJunit(Properties prop) {
        String junitFlag = prop.getProperty("JUNIT");
        if (junitFlag == null || junitFlag.isEmpty()) {
            LOGGER.info("No JUNIT property provided, this will not be executed as a test.");
        } else {
            if (junitFlag.equalsIgnoreCase("test")) {
                StdPolicyEngine.junit = true;
            } else {
                StdPolicyEngine.junit = false;
            }
        }
    }

    private static void init() {
        pdps = new ArrayList<>();
        encoding = new ArrayList<>();
    }

    private static void setEnvironment(Properties prop) {
        environment = prop.getProperty("ENVIRONMENT", AAFEnvironment.DEVL.toString());
        if (environment.equalsIgnoreCase(AAFEnvironment.TEST.toString())) {
            environment = AAFEnvironment.TEST.toString();
        } else if (environment.equalsIgnoreCase(AAFEnvironment.PROD.toString())) {
            environment = AAFEnvironment.PROD.toString();
        } else {
            environment = AAFEnvironment.DEVL.toString();
        }
    }

    private static void setClientId(String clientID) {
        userName = clientID;
    }

    /*
     * Read the PDP_URL parameter
     */
    private void readPDPParam(String pdpVal) throws PolicyEngineException {
        if (pdpVal.contains(",")) {
            List<String> pdpValues = new ArrayList<>(Arrays.asList(pdpVal.split("\\s*,\\s*")));
            if (pdpValues.size() == 3) {
                // 0 - PDPURL
                pdps.add(pdpValues.get(0));
                // 1:2 will be UserID:Password
                String userID = pdpValues.get(1);
                String userPas = pdpValues.get(2);
                Base64.Encoder encoder = Base64.getEncoder();
                encoding.add(encoder.encodeToString((userID + ":" + userPas).getBytes(StandardCharsets.UTF_8)));
            } else {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Credentials to send Request: " + pdpValues);
                throw new PolicyEngineException(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "No enough Credentials to send Request. " + pdpValues);
            }
        } else {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP value is improper/missing required values: " + pdpVal);
            throw new PolicyEngineException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP value is improper/missing required values.");
        }
    }

    /*
     * Allowing changes to the scheme and Handler.
     */
    public void notification(NotificationScheme scheme, NotificationHandler handler) {
        this.scheme = scheme;
        this.handler = handler;
        LOGGER.debug("Scheme is : " + scheme.toString());
        LOGGER.debug("Handler is : " + handler.getClass().getName());

        if (notificationType.get(0).equals("ueb")) {
            if (this.uebThread) {
                AutoClientUEB.setAuto(scheme, handler);
                this.uebThread = registerUEBThread.isAlive();
            }
        } else if (notificationType.get(0).equals(DMAAP)) {
            if (this.dmaapThread) {
                AutoClientDMAAP.setAuto(scheme, handler);
                this.dmaapThread = registerDMAAPThread.isAlive();
            }
        } else {
            AutoClientEnd.setAuto(scheme, handler);
        }

        if (junit) {
            return;
        }

        if (pdps != null) {
            if (notificationType.get(0).equals("ueb") && !this.uebThread) {
                this.uebClientThread = new AutoClientUEB(pdps.get(0), notificationURLList, apiKey, apiSecret);
                AutoClientUEB.setAuto(scheme, handler);
                this.registerUEBThread = new Thread(this.uebClientThread);
                this.registerUEBThread.start();
                this.uebThread = true;
            } else if (notificationType.get(0).equals(DMAAP) && !this.dmaapThread) {
                this.dmaapClientThread = new AutoClientDMAAP(notificationURLList, topic, userName, pass);
                AutoClientDMAAP.setAuto(scheme, handler);
                this.registerDMAAPThread = new Thread(this.dmaapClientThread);
                this.registerDMAAPThread.start();
                this.dmaapThread = true;
            } else {
                if (pdps.get(0) != null) {
                    if (AutoClientEnd.getURL() == null) {
                        AutoClientEnd.start(pdps.get(0));
                    } else {
                        AutoClientEnd.stop();
                        AutoClientEnd.start(pdps.get(0));
                    }
                }
            }
        }
    }

    /*
     * Gets the Notification if one exists. Used only for Manual Polling purposes.
     */
    public PDPNotification getNotification() {
        // Check if there is proper scheme..
        PDPNotification notification = null;
        if (this.scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)
                || this.scheme.equals(NotificationScheme.MANUAL_NOTIFICATIONS)) {
            if (notificationType.get(0).equals("ueb")) {
                ManualClientEndUEB.start(pdps.get(0), notificationURLList, UNIQUEID);
                notification = ManualClientEndUEB.result(scheme);
            } else if (notificationType.get(0).equals(DMAAP)) {
                ManualClientEndDMAAP.start(notificationURLList, topic, UNIQUEID, userName, pass);
                notification = ManualClientEndDMAAP.result(scheme);
            } else {
                ManualClientEnd.start(pdps.get(0));
                LOGGER.debug("manual notification requested.. : " + scheme.toString());
                notification = ManualClientEnd.result(scheme);
            }
            if (notification == null) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No Notification yet..");
                return null;
            } else {
                return notification;
            }
        } else {
            return null;
        }
    }

    /*
     * Setting the Scheme.
     */
    public void setScheme(NotificationScheme scheme) {
        this.scheme = scheme;
        if (notificationType.get(0).equals("ueb")) {
            AutoClientUEB.setScheme(this.scheme);
            if (this.scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)) {
                ManualClientEndUEB.createTopic(pdps.get(0), UNIQUEID, notificationURLList);
            }
        } else if (notificationType.get(0).equals(DMAAP)) {
            AutoClientDMAAP.setScheme(this.scheme);
            if (this.scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)) {
                ManualClientEndDMAAP.createTopic(topic, UNIQUEID, notificationURLList, userName, pass);
            }
        } else {
            AutoClientEnd.setScheme(this.scheme);
        }
    }

    /*
     * Returns the Scheme
     */
    public NotificationScheme getScheme() {
        return this.scheme;
    }

    /*
     * Returns the NotificationHandler
     */
    public NotificationHandler getNotificationHandler() {
        return this.handler;
    }

    /*
     * Stop the Notification Service if its running.
     */
    public void stopNotification() {
        if (this.scheme != null && this.handler != null && (this.scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS)
                || this.scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS))) {
            LOGGER.info("Clear Notification called.. ");
            if (notificationType.get(0).equals("ueb")) {
                this.uebClientThread.terminate();
                this.uebThread = false;
            } else if (notificationType.get(0).equals(DMAAP)) {
                this.dmaapClientThread.terminate();
                this.dmaapThread = false;
            } else {
                AutoClientEnd.stop();
            }
        }
    }

    /*
     * Push a policy to the PDP API implementation
     */
    public String pushPolicy(String policyScope, String policyName, String policyType, String pdpGroup, UUID requestID)
            throws PolicyException {
        PushPolicyParameters pushPolicyParameters = new PushPolicyParameters();
        if (policyScope == null || policyScope.trim().isEmpty()) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        if (policyName == null || policyName.trim().isEmpty()) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        pushPolicyParameters.setPolicyName(policyScope + "." + policyName);
        pushPolicyParameters.setPolicyType(policyType);
        pushPolicyParameters.setPdpGroup(pdpGroup);
        pushPolicyParameters.setRequestID(requestID);
        return pushPolicyImpl(pushPolicyParameters).getResponseMessage();
    }

    public String createUpdateConfigPolicy(String policyName, String policyDescription, String onapName,
            String configName, Map<String, String> configAttributes, String configType, String body, String policyScope,
            UUID requestID, String riskLevel, String riskType, String guard, String ttlDate, boolean updateFlag)
            throws PolicyException {
        return createUpdateConfigPolicyImpl(policyName, policyDescription, onapName, configName, configAttributes,
                configType, body, policyScope, requestID, riskLevel, riskType, guard, ttlDate, updateFlag);
    }

    /*
     * Create Config Policy API Implementation
     */
    public String createUpdateConfigPolicyImpl(String policyName, String policyDescription, String onapName,
            String configName, Map<String, String> configAttributes, String configType, String body, String policyScope,
            UUID requestID, String riskLevel, String riskType, String guard, String ttlDate, boolean updateFlag)
            throws PolicyException {
        PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyClass(PolicyClass.Config);
        policyParameters.setPolicyConfigType(PolicyConfigType.Base);
        if (policyScope == null || policyScope.trim().isEmpty()) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        if (policyName == null || policyName.trim().isEmpty()) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        policyParameters.setPolicyName(policyScope + "." + policyName);
        policyParameters.setPolicyDescription(policyDescription);
        policyParameters.setOnapName(onapName);
        policyParameters.setConfigName(configName);
        Map<AttributeType, Map<String, String>> attributes = new HashMap<AttributeType, Map<String, String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);
        policyParameters.setConfigBodyType(PolicyType.valueOf(configType));
        policyParameters.setConfigBody(body);
        policyParameters.setRequestID(requestID);
        policyParameters.setRiskLevel(riskLevel);
        policyParameters.setRiskType(riskType);
        policyParameters.setGuard(Boolean.parseBoolean(guard));
        try {
            policyParameters.setTtlDate(new SimpleDateFormat("dd-MM-yyyy").parse(ttlDate));
        } catch (ParseException e) {
            LOGGER.warn("Error Parsing date given " + ttlDate);
            policyParameters.setTtlDate(null);
        }
        return createUpdatePolicyImpl(policyParameters, updateFlag).getResponseMessage();
    }

    public String createUpdateConfigFirewallPolicy(String policyName, JsonObject firewallJson, String policyScope,
            UUID requestID, String riskLevel, String riskType, String guard, String ttlDate, boolean updateFlag)
            throws PolicyException {
        return createUpdateConfigFirewallPolicyImpl(policyName, firewallJson, policyScope, requestID, riskLevel,
                riskType, guard, ttlDate, updateFlag);
    }

    /*
     * Create Update Config Firewall Policy API implementation
     */
    public String createUpdateConfigFirewallPolicyImpl(String policyName, JsonObject firewallJson, String policyScope,
            UUID requestID, String riskLevel, String riskType, String guard, String ttlDate, boolean updateFlag)
            throws PolicyException {
        PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyClass(PolicyClass.Config);
        policyParameters.setPolicyConfigType(PolicyConfigType.Firewall);
        if (policyScope == null || policyScope.trim().isEmpty()) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        if (policyName == null || policyName.trim().isEmpty()) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        policyParameters.setPolicyName(policyScope + "." + policyName);
        policyParameters.setConfigBody(firewallJson.toString());
        policyParameters.setRequestID(requestID);
        policyParameters.setRiskLevel(riskLevel);
        policyParameters.setRiskType(riskType);
        policyParameters.setGuard(Boolean.parseBoolean(guard));
        try {
            policyParameters.setTtlDate(new SimpleDateFormat("dd-MM-yyyy").parse(ttlDate));
        } catch (NullPointerException | ParseException e) {
            LOGGER.warn("Error Parsing date given " + ttlDate, e);
            policyParameters.setTtlDate(null);
        }
        return createUpdatePolicyImpl(policyParameters, updateFlag).getResponseMessage();
    }

    public static void setClientKey(String clientKey) {
        if (clientKey != null && !clientKey.isEmpty()) {
            StdPolicyEngine.pass = clientKey;
            setClientEncoding();
        }
    }

    /*
     * Get the Environment.
     */
    public static String getEnvironment() {
        return environment;
    }

    /*
     * Rotate the PDP list upon WEBsocket Failures
     */
    public static void rotatePDPList() {
        Collections.rotate(pdps, -1);
        Collections.rotate(encoding, -1);
    }

    /*
     * Get the latest PDP
     */
    public static String getPDPURL() {
        return pdps.get(0);
    }
}