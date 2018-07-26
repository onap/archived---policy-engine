/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.std;

import static org.onap.policy.std.utils.PolicyConfigConstants.BAD_REQUEST_STATUS_CODE;
import static org.onap.policy.std.utils.PolicyConfigConstants.CLIENT_ID_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.CLIENT_KEY_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.COMMA;
import static org.onap.policy.std.utils.PolicyConfigConstants.CREATE_DICTIONARY_ITEM_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.CREATE_POLICY_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.DATE_FORMAT;
import static org.onap.policy.std.utils.PolicyConfigConstants.DEFAULT_NOTIFICATION;
import static org.onap.policy.std.utils.PolicyConfigConstants.DELETE_POLICY_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.DMAAP;
import static org.onap.policy.std.utils.PolicyConfigConstants.ENVIRONMENT_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.ERROR_AUTH_GET_PERM;
import static org.onap.policy.std.utils.PolicyConfigConstants.ERROR_DATA_ISSUE;
import static org.onap.policy.std.utils.PolicyConfigConstants.ERROR_INVALID_PDPS;
import static org.onap.policy.std.utils.PolicyConfigConstants.ERROR_WHILE_CONNECTING;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_CONFIG_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_DECISION_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_DICTIONARY_ITEMS_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_METRICS_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.HTTP;
import static org.onap.policy.std.utils.PolicyConfigConstants.JUNIT_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.NOTIFICATION_SERVERS_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.NOTIFICATION_TOPIC_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.NOTIFICATION_TYPE_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.PDP_URL_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.PDP_VALUE_REGEX;
import static org.onap.policy.std.utils.PolicyConfigConstants.PE300;
import static org.onap.policy.std.utils.PolicyConfigConstants.POLICY_ENGINE_IMPORT_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.PUSH_POLICY_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.REGEX;
import static org.onap.policy.std.utils.PolicyConfigConstants.SEMICOLLON;
import static org.onap.policy.std.utils.PolicyConfigConstants.SEND_EVENT_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.TEST_POLICY_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.UEB;
import static org.onap.policy.std.utils.PolicyConfigConstants.UEB_API_KEY_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.UEB_API_SECRET_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.UNAUTHORIZED_STATUS_CODE;
import static org.onap.policy.std.utils.PolicyConfigConstants.UPDATE_DICTIONARY_ITEM_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.UPDATE_POLICY_RESOURCE_NAME;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.onap.policy.std.utils.PolicyCommonConfigConstants;
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
    public StdPolicyEngine(final String propertyFilePath, final String clientKey) throws PolicyEngineException {
        setProperty(propertyFilePath, clientKey);
    }
    
    /*
     * Taking the Property structure even if it null.
     */
    public StdPolicyEngine(final Properties properties, final String clientKey) throws PolicyEngineException {
        setProperty(properties, clientKey);
    }

    /*
     * Taking the Notification Constructor.
     */
    public StdPolicyEngine(final String propertyFilePath, final NotificationScheme scheme,
            final NotificationHandler handler) throws PolicyEngineException {
        setProperty(propertyFilePath, null);
        this.scheme = scheme;
        this.handler = handler;
        if ((!UEB.equals(notificationType.get(0))) || (!DMAAP.equals(notificationType.get(0)))) {
            AutoClientEnd.setAuto(scheme, handler);
        }
        notification(scheme, handler);
    }

    /*
     * Taking the Notification Constructor.
     */
    public StdPolicyEngine(final String propertyFilePath, final NotificationScheme scheme)
            throws PolicyEngineException {
        setProperty(propertyFilePath, null);
        this.scheme = scheme;
        setScheme(scheme);
    }

    /*
     * sendEvent API Implementation
     */
    public Collection<PolicyResponse> sendEvent(final Map<String, String> eventAttributes, final UUID requestID)
            throws PolicyEventException {
        return sendEventImpl(eventAttributes, requestID);
    }

    /*
     * sendEvent API Implementation for eventRequestParameters
     */
    public Collection<PolicyResponse> sendEvent(final EventRequestParameters eventRequestParameters)
            throws PolicyEventException {
        if (eventRequestParameters == null) {
            final String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No event Request Parameters Given. ";
            LOGGER.error(message);
            throw new PolicyEventException(message);
        }
        return sendEventImpl(eventRequestParameters.getEventAttributes(), eventRequestParameters.getRequestID());
    }

    /*
     * getConfig using configRequestParameters Implementation
     */
    public Collection<PolicyConfig> getConfig(final ConfigRequestParameters configRequestParameters)
            throws PolicyConfigException {
        return getConfigImpl(configRequestParameters);
    }

    /*
     * listPolicies using configRequestParameters Implementation
     */
    public Collection<String> listConfig(final ConfigRequestParameters listPolicyRequestParameters)
            throws PolicyConfigException {
        return listConfigImpl(listPolicyRequestParameters);
    }

    /*
     * getDecision using the decision Attributes.
     */
    public DecisionResponse getDecision(final String onapName, final Map<String, String> decisionAttributes,
            final UUID requestID) throws PolicyDecisionException {
        return getDecisionImpl(onapName, decisionAttributes, requestID);
    }

    /*
     * getDecision Using decisionRequestParameters.
     */
    public DecisionResponse getDecision(final DecisionRequestParameters decisionRequestParameters)
            throws PolicyDecisionException {
        if (decisionRequestParameters == null) {
            final String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Request Parameters Given. ";
            LOGGER.error(message);
            throw new PolicyDecisionException(message);
        }
        return getDecisionImpl(decisionRequestParameters.getOnapName(),
                decisionRequestParameters.getDecisionAttributes(), decisionRequestParameters.getRequestID());
    }

    /*
     * getMetrics using metricsRequestParameters
     */
    public MetricsResponse getMetrics(final MetricsRequestParameters parameters) throws PolicyException {
        return getMetricsImpl(parameters);
    }

    public MetricsResponse getMetricsImpl(final MetricsRequestParameters parameters) throws PolicyException {
        String body = null;
        // Create the Request
        try {
            if (parameters != null) {
                body = PolicyUtils.objectToJsonString(parameters);
            }
        } catch (final JsonProcessingException jsonProcessingException) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + jsonProcessingException;
            LOGGER.error(message);
            throw new PolicyException(message, jsonProcessingException);
        }

        final StdMetricsResponse response = new StdMetricsResponse();
        // Get Response.
        try {
            final ResponseEntity<String> result =
                    callNewPDP(GET_METRICS_RESOURCE_NAME, HttpMethod.GET, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
            return response;
        } catch (final PolicyException exception) {
            if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
                LOGGER.error(exception);
                final HttpClientErrorException ex = (HttpClientErrorException) exception.getCause();
                response.setResponseCode(ex.getRawStatusCode());
                response.setResponseMessage(exception.getMessage());
                return response;
            }
            final String message =
                    XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Error while processing results. please check logs.";
            LOGGER.error(message, exception);
            throw new PolicyException(message, exception);
        }
    }

    /*
     * PushPolicy using pushPolicyParameters.
     */
    public PolicyChangeResponse pushPolicy(final PushPolicyParameters pushPolicyParameters) throws PolicyException {
        return pushPolicyImpl(pushPolicyParameters);
    }

    public PolicyChangeResponse pushPolicyImpl(final PushPolicyParameters pushPolicyParameters) throws PolicyException {
        final StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(pushPolicyParameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<String> result =
                    callNewPDP(PUSH_POLICY_RESOURCE_NAME, HttpMethod.PUT, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
            return response;
        } catch (final PolicyException exception) {
            return processException(exception);
        }
    }

    /*
     * Delete a Policy using deletePolicyParameters
     */
    public PolicyChangeResponse deletePolicy(final DeletePolicyParameters parameters) throws PolicyException {
        return deletePolicyImpl(parameters);
    }

    private PolicyChangeResponse deletePolicyImpl(final DeletePolicyParameters parameters) throws PolicyException {
        final StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(parameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<String> result =
                    callNewPDP(DELETE_POLICY_RESOURCE_NAME, HttpMethod.DELETE, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
        } catch (final PolicyException exception) {
            return processException(exception);
        }
        return response;
    }

    /*
     * getDictionaryItem Using dictionaryParameters
     */
    public DictionaryResponse getDictionaryItem(final DictionaryParameters parameters) throws PolicyException {
        return getDictionaryItemImpl(parameters);
    }

    private DictionaryResponse getDictionaryItemImpl(final DictionaryParameters parameters) throws PolicyException {
        final StdDictionaryResponse response = new StdDictionaryResponse();
        String body = "{}";
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(parameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<APIDictionaryResponse> result =
                    callNewPDP(GET_DICTIONARY_ITEMS_RESOURCE_NAME, HttpMethod.POST, body, APIDictionaryResponse.class);
            // Process response
            return dictionaryResult(result.getBody());
        } catch (final Exception exception) {
            if (exception.getCause().getMessage().contains(UNAUTHORIZED_STATUS_CODE)) {
                final String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_AUTH_GET_PERM
                        + GET_DICTIONARY_ITEMS_RESOURCE_NAME;
                LOGGER.error(message);
                response.setResponseMessage(message);
                response.setResponseCode(HttpStatus.UNAUTHORIZED.value());
                return response;
            }
            if (exception.getCause().getMessage().contains(BAD_REQUEST_STATUS_CODE)) {
                final String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ERROR_DATA_ISSUE;
                response.setResponseMessage(message);
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }
            final String message = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_INVALID_PDPS + pdps;
            LOGGER.error(message, exception);
            response.setResponseMessage(message);
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }
    }

    @SuppressWarnings("unchecked")
    private StdDictionaryResponse dictionaryResult(final APIDictionaryResponse body) {
        final StdDictionaryResponse response = new StdDictionaryResponse();
        response.setResponseCode(body.getResponseCode());
        response.setResponseMessage(body.getResponseMessage());
        response.setDictionaryData((Map<String, String>) body.getDictionaryData());
        if (body.getDictionaryJson() != null) {
            final Gson objGson = new GsonBuilder().create();
            final String mapToJson = objGson.toJson(body.getDictionaryJson());
            try (final JsonReader jsonReader = Json.createReader(new StringReader(mapToJson));) {
                final JsonObject object = jsonReader.readObject();
                response.setDictionaryJson(object);
            }
        }
        return response;
    }

    /*
     * createDictinaryItem Using dictionaryParameters.
     */
    public PolicyChangeResponse createDictionaryItem(final DictionaryParameters parameters) throws PolicyException {
        return createUpdateDictionaryItemImpl(parameters, false);
    }

    /*
     * updateDictinaryItem Using dictionaryParameters.
     */
    public PolicyChangeResponse updateDictionaryItem(final DictionaryParameters parameters) throws PolicyException {
        return createUpdateDictionaryItemImpl(parameters, true);
    }

    private PolicyChangeResponse createUpdateDictionaryItemImpl(final DictionaryParameters parameters,
            final boolean updateFlag) throws PolicyException {

        final String resource = getDictionaryResouceName(updateFlag);
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(parameters);
        } catch (final JsonProcessingException jsonProcessingException) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + jsonProcessingException;
            LOGGER.error(message);
            throw new PolicyException(message, jsonProcessingException);
        }
        // Get Response.
        try {
            final StdPolicyChangeResponse response = new StdPolicyChangeResponse();
            final ResponseEntity<String> result = callNewPDP(resource, HttpMethod.PUT, body, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
            return response;
        } catch (final PolicyException exception) {
            return processException(exception);
        }
    }

    private String getDictionaryResouceName(final boolean updateFlag) {
        return updateFlag ? UPDATE_DICTIONARY_ITEM_RESOURCE_NAME : CREATE_DICTIONARY_ITEM_RESOURCE_NAME;
    }

    /*
     * PolicyEngine Import
     */
    public PolicyChangeResponse policyEngineImport(final ImportParameters importParameters) throws PolicyException {
        return policyEngineImportImpl(importParameters);
    }

    private PolicyChangeResponse policyEngineImportImpl(final ImportParameters importParameters) throws PolicyException {
        final LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        // Create Request.
        try {
            final String body = PolicyUtils.objectToJsonString(importParameters);
            parameters.set("importParametersJson", body);
            parameters.set("file", new FileSystemResource(importParameters.getFilePath()));
        } catch (final Exception e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        contentType = MediaType.MULTIPART_FORM_DATA_VALUE;
        // Get Response.
        try {
            final StdPolicyChangeResponse response = new StdPolicyChangeResponse();
            final ResponseEntity<String> result =
                    callNewPDP(POLICY_ENGINE_IMPORT_RESOURCE_NAME, HttpMethod.POST, parameters, String.class);
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
            return response;
        } catch (final PolicyException exception) {
            return processException(exception);
        } finally {
            contentType = null;
        }
    }

    /*
     * createPolicy Using policyParameters.
     */
    public PolicyChangeResponse createPolicy(final PolicyParameters policyParameters) throws PolicyException {
        return createUpdatePolicyImpl(policyParameters, false);
    }

    /*
     * updatePolicy using policyParameters.
     */
    public PolicyChangeResponse updatePolicy(final PolicyParameters policyParameters) throws PolicyException {
        return createUpdatePolicyImpl(policyParameters, true);
    }

    private PolicyChangeResponse createUpdatePolicyImpl(final PolicyParameters policyParameters,
            final boolean updateFlag) throws PolicyException {
        final String resource = getPolicyResourceName(updateFlag);
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(policyParameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<String> result = callNewPDP(resource, HttpMethod.PUT, body, String.class);
            final StdPolicyChangeResponse response = new StdPolicyChangeResponse();
            // Process response
            response.setResponseMessage(result.getBody());
            response.setResponseCode(result.getStatusCode().value());
            return response;
        } catch (final PolicyException exception) {
            return processException(exception);
        }
    }

    private String getPolicyResourceName(final boolean updateFlag) {
        return updateFlag ? UPDATE_POLICY_RESOURCE_NAME : CREATE_POLICY_RESOURCE_NAME;
    }

    private PolicyChangeResponse processException(final PolicyException exception) throws PolicyException {
        final StdPolicyChangeResponse response = new StdPolicyChangeResponse();
        if (exception.getCause() != null && exception.getCause() instanceof HttpClientErrorException) {
            LOGGER.error(exception);
            final HttpClientErrorException ex = (HttpClientErrorException) exception.getCause();
            response.setResponseCode(ex.getRawStatusCode());
            response.setResponseMessage(exception.getMessage());
            return response;
        } else {
            final String message =
                    XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Error while processing results. please check logs.";
            LOGGER.error(message, exception);
            throw new PolicyException(message, exception);
        }
    }

    private DecisionResponse getDecisionImpl(final String onapName, final Map<String, String> decisionAttributes,
            final UUID requestID) throws PolicyDecisionException {
        String body = null;
        // Create Request.
        try {
            final DecisionRequestParameters decisionRequestParameters = new DecisionRequestParameters();
            decisionRequestParameters.setDecisionAttributes(decisionAttributes);
            decisionRequestParameters.setOnapName(onapName);
            decisionRequestParameters.setRequestID(requestID);
            body = PolicyUtils.objectToJsonString(decisionRequestParameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyDecisionException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<StdDecisionResponse> result =
                    callNewPDP(GET_DECISION_RESOURCE_NAME, HttpMethod.POST, body, StdDecisionResponse.class);
            // Process response
            return result.getBody();
        } catch (final Exception exception) {
            final String defaulMessage = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_INVALID_PDPS + pdps;
            final String message = getErrorMessage(exception, defaulMessage, GET_DECISION_RESOURCE_NAME);
            LOGGER.error(message, exception);
            throw new PolicyDecisionException(message, exception);
        }
    }

    private Collection<PolicyConfig> getConfigImpl(final ConfigRequestParameters configRequestParameters)
            throws PolicyConfigException {
        String body = null;
        // Create Request.
        try {
            body = PolicyUtils.objectToJsonString(configRequestParameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyConfigException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<APIPolicyConfigResponse[]> result =
                    callNewPDP(GET_CONFIG_RESOURCE_NAME, HttpMethod.POST, body, APIPolicyConfigResponse[].class);
            // Process Response
            return configResult(result.getBody());
        } catch (final Exception exception) {
            final String defaulMessage = XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_INVALID_PDPS + pdps;
            final String message = getErrorMessage(exception, defaulMessage, GET_CONFIG_RESOURCE_NAME);
            LOGGER.error(message, exception);
            throw new PolicyConfigException(message, exception);
        }
    }

    private ArrayList<PolicyConfig> configResult(final APIPolicyConfigResponse[] response)
            throws PolicyConfigException {
        final ArrayList<PolicyConfig> result = new ArrayList<>();
        if (response == null) {
            return result;
        }

        for (final APIPolicyConfigResponse policyConfigResponse : response) {
            final StdPolicyConfig policyConfig = new StdPolicyConfig();
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
                            final StringReader reader = new StringReader(policyConfigResponse.getConfig());
                            try (final JsonReader jsonReader = Json.createReader(reader)) {
                                final JsonObject object = jsonReader.readObject();
                                policyConfig.setJsonObject(object);
                            }
                            break;
                        case OTHER:
                            policyConfig.setOther(policyConfigResponse.getConfig());
                            break;
                        case PROPERTIES:
                            final Properties props = new Properties();
                            props.putAll(policyConfigResponse.getProperty());
                            policyConfig.setProperties(props);
                            break;
                        case XML:
                            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            final DocumentBuilder builder = factory.newDocumentBuilder();
                            final StringReader stringReader = new StringReader(policyConfigResponse.getConfig());
                            policyConfig.setDocument(builder.parse(new InputSource(stringReader)));
                            break;
                    }
                } catch (final Exception exception) {
                    LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + exception);
                    throw new PolicyConfigException(
                            XACMLErrorConstants.ERROR_SCHEMA_INVALID + "Unable to parse the config", exception);
                }
            }
            result.add(policyConfig);
        }
        return result;
    }

    private void setMatches(final Map<String, String> matchingConditions) {
        final Matches match = new Matches();
        final HashMap<String, String> configAttributes = new HashMap<>();
        try {
            for (final Map.Entry<String, String> entry : matchingConditions.entrySet()) {
                if (PolicyCommonConfigConstants.ONAP_NAME.equalsIgnoreCase(entry.getKey())) {
                    match.setOnapName(entry.getValue());
                } else if (PolicyCommonConfigConstants.CONFIG_NAME.equalsIgnoreCase(entry.getKey())) {
                    match.setConfigName(entry.getValue());
                } else {
                    configAttributes.put(entry.getKey(), entry.getValue());
                }
            }
            if (!configAttributes.isEmpty()) {
                match.setConfigAttributes(configAttributes);
            }
            MatchStore.storeMatch(match);
        } catch (final Exception e) {
            LOGGER.error("StoreMatch failed for Onap:" + match.getOnapName() + " Config: " + match.getConfigName(), e);
        }
    }

    /*
     * Generic Rest Client to call PDP services.
     */
    <T> ResponseEntity<T> callNewPDP(final String resource, final HttpMethod method, final Object body,
            final Class<T> responseType) throws PolicyException {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpEntity<?> requestEntity = new HttpEntity<>(body, getHeaders());
        ResponseEntity<T> result = null;
        HttpClientErrorException exception = null;
        int pdpsCount = 0;
        while (pdpsCount < pdps.size()) {
            try {
                result = restTemplate.exchange(pdps.get(0) + "/api/" + resource, method, requestEntity, responseType);
            } catch (final HttpClientErrorException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_WHILE_CONNECTING + pdps.get(0), e);
                exception = e;
            } catch (final Exception e) {
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
        if (exception == null || exception.getStatusCode() == null) {
            return result;
        }

        final String message = getHttpErrorMessage(exception, resource);
        LOGGER.error(message);
        throw new PolicyException(message, exception);
    }

    private String getHttpErrorMessage(final HttpClientErrorException exception, final String resource) {
        if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            return XACMLErrorConstants.ERROR_PERMISSIONS + ":" + exception.getStatusCode() + ":" + ERROR_AUTH_GET_PERM
                    + resource;
        }
        if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            return XACMLErrorConstants.ERROR_DATA_ISSUE + ":" + exception.getStatusCode() + ":"
                    + exception.getResponseBodyAsString();
        }
        if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return XACMLErrorConstants.ERROR_PROCESS_FLOW + ERROR_WHILE_CONNECTING + pdps + exception;
        }
        return XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + exception.getStatusCode() + ":"
                + exception.getResponseBodyAsString();
    }

    private HttpHeaders getHeaders() {
        final HttpHeaders headers = new HttpHeaders();
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
        final Base64.Encoder encoder = Base64.getEncoder();
        clientEncoding = encoder.encodeToString((userName + ":" + pass).getBytes(StandardCharsets.UTF_8));
    }

    private Collection<String> listConfigImpl(final ConfigRequestParameters listRequestParameters)
            throws PolicyConfigException {
        final Collection<String> policyList = new ArrayList<>();
        if (junit) {
            policyList.add(TEST_POLICY_NAME);
            return policyList;
        }
        final Collection<PolicyConfig> policyConfig = getConfigImpl(listRequestParameters);
        for (final PolicyConfig policy : policyConfig) {
            if (policy.getPolicyConfigMessage() != null && policy.getPolicyConfigMessage().contains(PE300)) {
                policyList.add(policy.getPolicyConfigMessage());
            } else {
                policyList.add("Policy Name: " + policy.getPolicyName());
            }
        }
        return policyList;
    }

    private Collection<PolicyResponse> sendEventImpl(final Map<String, String> eventAttributes, final UUID requestID)
            throws PolicyEventException {
        String body = null;
        // Create Request.
        try {
            // Long way here, can be shortened and will be done.
            final EventRequestParameters eventRequestParameters = new EventRequestParameters();
            eventRequestParameters.setEventAttributes(eventAttributes);
            eventRequestParameters.setRequestID(requestID);
            body = PolicyUtils.objectToJsonString(eventRequestParameters);
        } catch (final JsonProcessingException e) {
            final String message = XACMLErrorConstants.ERROR_SCHEMA_INVALID + e;
            LOGGER.error(message);
            throw new PolicyEventException(message, e);
        }
        // Get Response.
        try {
            final ResponseEntity<StdPolicyResponse[]> result =
                    callNewPDP(SEND_EVENT_RESOURCE_NAME, HttpMethod.POST, body, StdPolicyResponse[].class);
            // Process Response
            return eventResult(result.getBody());
        } catch (final Exception exception) {
            final String defaulMessage = XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_INVALID_PDPS + pdps;
            final String message = getErrorMessage(exception, defaulMessage, SEND_EVENT_RESOURCE_NAME);
            LOGGER.error(message, exception);
            throw new PolicyEventException(message, exception);
        }
    }

    private String getErrorMessage(final Exception exception, final String defaulMessage, final String resource) {
        final Throwable cause = exception.getCause();
        if (cause != null && cause.getMessage().contains(UNAUTHORIZED_STATUS_CODE)) {
            return XACMLErrorConstants.ERROR_PERMISSIONS + ERROR_AUTH_GET_PERM + resource;
        }
        if (cause != null && exception.getCause().getMessage().contains(BAD_REQUEST_STATUS_CODE)) {
            return XACMLErrorConstants.ERROR_DATA_ISSUE + ERROR_DATA_ISSUE;
        }
        return defaulMessage;
    }

    private ArrayList<PolicyResponse> eventResult(final StdPolicyResponse[] response) {
        final ArrayList<PolicyResponse> eventResult = new ArrayList<>();
        if (response != null && response.length > 0) {
            for (final StdPolicyResponse policyConfigResponse : response) {
                eventResult.add(policyConfigResponse);
            }
        }
        return eventResult;
    }

    private void setProperty(final String propertyFilePath, String clientKey) throws PolicyEngineException {
        if (propertyFilePath == null) {
            throw new PolicyEngineException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Error NO PropertyFile Path provided");
        }
        final Properties prop = getProperties(propertyFilePath);
        setProperty(prop,clientKey);
    }
    
    private void setProperty(final Properties properties, String clientKey) throws PolicyEngineException {
        if (properties == null) {
            throw new PolicyEngineException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "NO properties provided, the value is NULL");
        }

        // UEB and DMAAP Settings
        final String notificationTypeValue = properties.getProperty(NOTIFICATION_TYPE_PROP_NAME);
        final String serverList = properties.getProperty(NOTIFICATION_SERVERS_PROP_NAME);
        topic = properties.getProperty(NOTIFICATION_TOPIC_PROP_NAME);
        apiKey = properties.getProperty(UEB_API_KEY_PROP_NAME);
        apiSecret = properties.getProperty(UEB_API_SECRET_PROP_NAME);

        setNotificationType(notificationTypeValue);

        if (serverList == null) {
            notificationType.clear();
            notificationType.add(DEFAULT_NOTIFICATION);
            LOGGER.info(
                    "Properties file doesn't have the NOTIFICATION_SERVERS parameter system will use defualt websockets");
        } else {
            notificationURLList = getPropertyValueAsList(serverList.trim());
        }

        if (topic != null) {
            topic = topic.trim();
        } else {
            LOGGER.error("Properties file doesn't have the NOTIFICATION_TOPIC parameter.");
        }

        // Client ID Authorization Settings.
        final String clientID = properties.getProperty(CLIENT_ID_PROP_NAME);
        if (clientKey == null) {
            clientKey = getClientKeyFromProperties(properties);
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
        setEnvironment(properties);
        // Initializing the values.
        init();
        readPdpProperites(properties);
        // Get JUNIT property from properties file when running tests
        checkJunit(properties);
    }

    private void readPdpProperites(final Properties prop) throws PolicyEngineException {
        // Check the Keys for PDP_URLs
        for (final String propertyKey : prop.stringPropertyNames()) {
            if (propertyKey.startsWith(PDP_URL_PROP_NAME)) {
                readPDPPropertyURL(prop, propertyKey);
            }
        }
        if (pdps == null || pdps.isEmpty()) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PDP_URLs");
            throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot Proceed without PDP_URLs");
        }
    }

    private void readPDPPropertyURL(Properties prop, String propertyKey) throws PolicyEngineException {
        final String propertyValue = prop.getProperty(propertyKey);
        if (propertyValue == null) {
            throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "Properties file doesn't have the PDP_URL parameter");
        }
        if (propertyValue.contains(SEMICOLLON)) {
            final List<String> pdpDefault = Arrays.asList(propertyValue.split(REGEX));
            for (final String pdpVal : pdpDefault) {
                readPDPParam(pdpVal);
            }
        } else {
            readPDPParam(propertyValue);
        }
    }

    private void setNotificationType(final String propertyValue) {
        if (propertyValue == null) {
            notificationType.add(DEFAULT_NOTIFICATION);
            LOGGER.info(
                    "Properties file doesn't have the NOTIFICATION_TYPE parameter system will use defualt websockets");
        } else {
            notificationType = getPropertyValueAsList(propertyValue.trim());
        }
    }

    private String getClientKeyFromProperties(final Properties prop) {
        final String clientKeyValue = prop.getProperty(CLIENT_KEY_PROP_NAME);
        try {
            return PolicyUtils.decode(clientKeyValue);
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS
                    + " Cannot Decode the given Password Proceeding with given Password!!", e);
        }
        return clientKeyValue;
    }

    private Properties getProperties(final String propertyFilePath) throws PolicyEngineException {
        // Adding logic for remote Properties file.
        if (propertyFilePath.startsWith(HTTP)) {
            return getRemoteProperties(propertyFilePath);
        }
        return getFileProperties(propertyFilePath);
    }

    private Properties getFileProperties(final String propertyFilePath) throws PolicyEngineException {
        final Path file = Paths.get(propertyFilePath);
        if (!file.toFile().exists()) {
            throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "File doesn't exist in the specified Path " + file.toString());
        }
        if (file.toString().endsWith(".properties")) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(file);) {
                final Properties prop = new Properties();
                prop.load(bufferedReader);
                return prop;
            } catch (final IOException exception) {
                LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + exception);
                throw new PolicyEngineException(
                        XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Cannot Load the Properties file", exception);
            }
        }
        LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Not a .properties file " + propertyFilePath);
        throw new PolicyEngineException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Not a .properties file");
    }

    private Properties getRemoteProperties(final String propertyFilePath) throws PolicyEngineException {
        try {
            final URL configURL = new URL(propertyFilePath);
            final URLConnection connection = configURL.openConnection();
            final Properties prop = new Properties();
            prop.load(connection.getInputStream());
            return prop;
        } catch (final IOException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            throw new PolicyEngineException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Maformed property URL " + e.getMessage());
        }
    }

    private List<String> getPropertyValueAsList(final String propertyValue) {
        if (propertyValue.contains(COMMA)) {
            return Arrays.asList(propertyValue.split(COMMA));
        }
        final List<String> valuesList = new ArrayList<>();
        valuesList.add(propertyValue);
        return valuesList;
    }

    private static void checkJunit(final Properties prop) {
        final String junitFlag = prop.getProperty(JUNIT_PROP_NAME);
        if (junitFlag == null || junitFlag.isEmpty()) {
            LOGGER.info("No JUNIT property provided, this will not be executed as a test.");
        } else {
            if ("test".equalsIgnoreCase(junitFlag)) {
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

    private static void setEnvironment(final Properties prop) {
        environment = prop.getProperty(ENVIRONMENT_PROP_NAME, AAFEnvironment.DEVL.toString());
        if (environment.equalsIgnoreCase(AAFEnvironment.TEST.toString())) {
            environment = AAFEnvironment.TEST.toString();
        } else if (environment.equalsIgnoreCase(AAFEnvironment.PROD.toString())) {
            environment = AAFEnvironment.PROD.toString();
        } else {
            environment = AAFEnvironment.DEVL.toString();
        }
    }

    private static void setClientId(final String clientID) {
        userName = clientID;
    }

    /*
     * Read the PDP_URL parameter
     */
    private void readPDPParam(final String pdpVal) throws PolicyEngineException {
        if (pdpVal.contains(COMMA)) {
            final List<String> pdpValues = Arrays.asList(pdpVal.split(PDP_VALUE_REGEX));
            if (pdpValues.size() == 3) {
                // 0 - PDPURL
                pdps.add(pdpValues.get(0));
                // 1:2 will be UserID:Password
                final String userID = pdpValues.get(1);
                final String userPas = pdpValues.get(2);
                final Base64.Encoder encoder = Base64.getEncoder();
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
    public void notification(final NotificationScheme scheme, final NotificationHandler handler) {
        this.scheme = scheme;
        this.handler = handler;
        LOGGER.debug("Scheme is : " + scheme.toString());
        LOGGER.debug("Handler is : " + handler.getClass().getName());

        if (UEB.equals(notificationType.get(0))) {
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
        if (pdps == null) {
            return;
        }

        if (UEB.equals(notificationType.get(0)) && !this.uebThread) {
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
                if (AutoClientEnd.getUrl() == null) {
                    AutoClientEnd.start(pdps.get(0));
                } else {
                    AutoClientEnd.stop();
                    AutoClientEnd.start(pdps.get(0));
                }
            }
        }
    }

    /*
     * Gets the Notification if one exists. Used only for Manual Polling purposes.
     */
    public PDPNotification getNotification() {
        // Check if there is proper scheme..
        PDPNotification notification;
        if (this.scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)
                || this.scheme.equals(NotificationScheme.MANUAL_NOTIFICATIONS)) {
            if (UEB.equals(notificationType.get(0))) {
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
            }
            return notification;
        }
        return null;
    }

    /*
     * Setting the Scheme.
     */
    public void setScheme(final NotificationScheme scheme) {
        this.scheme = scheme;
        if (UEB.equals(notificationType.get(0))) {
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
        if (this.scheme != null && this.handler != null
                && (this.scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS)
                        || this.scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS))) {
            LOGGER.info("Clear Notification called.. ");
            if (UEB.equals(notificationType.get(0))) {
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
    public String pushPolicy(final String policyScope, final String policyName, final String policyType,
            final String pdpGroup, final UUID requestID) throws PolicyException {
        validateParameters(policyName, policyScope);

        final PushPolicyParameters pushPolicyParameters = new PushPolicyParameters();
        pushPolicyParameters.setPolicyName(policyScope + "." + policyName);
        pushPolicyParameters.setPolicyType(policyType);
        pushPolicyParameters.setPdpGroup(pdpGroup);
        pushPolicyParameters.setRequestID(requestID);
        return pushPolicyImpl(pushPolicyParameters).getResponseMessage();
    }

    private boolean isNotValid(final String value) {
        return value == null || value.trim().isEmpty();
    }

    public String createUpdateConfigPolicy(final String policyName, final String policyDescription,
            final String onapName, final String configName, final Map<String, String> configAttributes,
            final String configType, final String body, final String policyScope, final UUID requestID,
            final String riskLevel, final String riskType, final String guard, final String ttlDate,
            final boolean updateFlag) throws PolicyException {
        return createUpdateConfigPolicyImpl(policyName, policyDescription, onapName, configName, configAttributes,
                configType, body, policyScope, requestID, riskLevel, riskType, guard, ttlDate, updateFlag);
    }

    /*
     * Create Config Policy API Implementation
     */
    private String createUpdateConfigPolicyImpl(final String policyName, final String policyDescription,
            final String onapName, final String configName, final Map<String, String> configAttributes,
            final String configType, final String body, final String policyScope, final UUID requestID,
            final String riskLevel, final String riskType, final String guard, final String ttlDate,
            final boolean updateFlag) throws PolicyException {

        validateParameters(policyName, policyScope);

        final PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyClass(PolicyClass.Config);
        policyParameters.setPolicyConfigType(PolicyConfigType.Base);
        policyParameters.setPolicyName(policyScope + "." + policyName);
        policyParameters.setPolicyDescription(policyDescription);
        policyParameters.setOnapName(onapName);
        policyParameters.setConfigName(configName);

        final Map<AttributeType, Map<String, String>> attributes = new HashMap<>();
        attributes.put(AttributeType.MATCHING, configAttributes);

        policyParameters.setAttributes(attributes);
        policyParameters.setConfigBodyType(PolicyType.valueOf(configType));
        policyParameters.setConfigBody(body);
        policyParameters.setRequestID(requestID);
        policyParameters.setRiskLevel(riskLevel);
        policyParameters.setRiskType(riskType);
        policyParameters.setGuard(Boolean.parseBoolean(guard));
        policyParameters.setTtlDate(toDate(ttlDate));
        return createUpdatePolicyImpl(policyParameters, updateFlag).getResponseMessage();
    }

    private Date toDate(final String dateString) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
        } catch (final ParseException e) {
            LOGGER.warn("Error Parsing date given " + dateString);
        }
        return null;
    }

    public String createUpdateConfigFirewallPolicy(final String policyName, final JsonObject firewallJson,
            final String policyScope, final UUID requestID, final String riskLevel, final String riskType,
            final String guard, final String ttlDate, final boolean updateFlag) throws PolicyException {
        return createUpdateConfigFirewallPolicyImpl(policyName, firewallJson, policyScope, requestID, riskLevel,
                riskType, guard, ttlDate, updateFlag);
    }

    /*
     * Create Update Config Firewall Policy API implementation
     */
    private String createUpdateConfigFirewallPolicyImpl(final String policyName, final JsonObject firewallJson,
            final String policyScope, final UUID requestID, final String riskLevel, final String riskType,
            final String guard, final String ttlDate, final boolean updateFlag) throws PolicyException {
        validateParameters(policyName, policyScope);

        final PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyClass(PolicyClass.Config);
        policyParameters.setPolicyConfigType(PolicyConfigType.Firewall);
        policyParameters.setPolicyName(policyScope + "." + policyName);
        policyParameters.setConfigBody(firewallJson.toString());
        policyParameters.setRequestID(requestID);
        policyParameters.setRiskLevel(riskLevel);
        policyParameters.setRiskType(riskType);
        policyParameters.setGuard(Boolean.parseBoolean(guard));
        policyParameters.setTtlDate(toDate(ttlDate));
        return createUpdatePolicyImpl(policyParameters, updateFlag).getResponseMessage();
    }

    private void validateParameters(final String policyName, final String policyScope) throws PolicyException {
        if (isNotValid(policyScope)) {
            final String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        if (isNotValid(policyName)) {
            final String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
    }

    public static void setClientKey(final String clientKey) {
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

    // Added for test
    String getTopic() {
        return topic;
    }

    // Added for test
    List<String> getNotificationType() {
        return notificationType;
    }

    // Added for test
    List<String> getNotificationURLList() {
        return notificationURLList;
    }

}
