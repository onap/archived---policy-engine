/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
 * Modifications Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.onap.policy.std.utils.PolicyCommonConfigConstants.CONFIG_NAME;
import static org.onap.policy.std.utils.PolicyCommonConfigConstants.ONAP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.CLIENT_ID_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.CLIENT_KEY_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.COMMA;
import static org.onap.policy.std.utils.PolicyConfigConstants.CREATE_DICTIONARY_ITEM_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.CREATE_POLICY_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.DELETE_POLICY_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.DMAAP;
import static org.onap.policy.std.utils.PolicyConfigConstants.ENVIRONMENT_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_CONFIG_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_DECISION_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_DICTIONARY_ITEMS_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.GET_METRICS_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.NOTIFICATION_SERVERS_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.NOTIFICATION_TOPIC_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.NOTIFICATION_TYPE_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.PDP_URL_PROP_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.POLICY_ENGINE_IMPORT_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.PUSH_POLICY_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.SEND_EVENT_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.UEB;
import static org.onap.policy.std.utils.PolicyConfigConstants.UPDATE_DICTIONARY_ITEM_RESOURCE_NAME;
import static org.onap.policy.std.utils.PolicyConfigConstants.UPDATE_POLICY_RESOURCE_NAME;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyEventException;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.api.PolicyType;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.models.APIDictionaryResponse;
import org.onap.policy.models.APIPolicyConfigResponse;
import org.onap.policy.std.utils.PolicyConfigConstants;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(StdPolicyEngine.class)
public class StdPolicyEngineTest {

    private static final String ONAP_NAME_VAL = "ONAP_NAME";
    private static final String POLICY_VERSION = "1.0.0";
    private static final String POLICY_NAME = "ONAP";
    private static final String COMMENTS = "";
    private static final UUID REQUEST_UUID = UUID.randomUUID();
    private static final String SERVER_NAME = "localhost.com";
    private static final String PDP_PROP_VALUE = "http://localhost:8092/pdp/ , test, test";
    private static final String PDP_PROP_VALUE_1 = "https://localhost:8091/pdp/ , onap, onap";
    private static final String JSON_CONFIGURATION = "{\"name\":\"value\"}";
    private static final String XML_CONFIGURATION =
            "<map><entry><string>name</string><string>value</string></entry></map>";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String CONFIG_PROPERTIES_FILE = "config_pass.properties";
    private static final String INVALID_CONFIG_PROPERTY_FILE = "config_fail.properties";

    @Test
    public void testStdPolicyEngineInitialize_noException() throws Exception {
        final File file = temporaryFolder.newFile(CONFIG_PROPERTIES_FILE);

        creatPropertyFile(file, getDefaultProperties());

        final StdPolicyEngine policyEngine = new StdPolicyEngine(file.toString(), (String) null);
        policyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);
        assertEquals("TEST", StdPolicyEngine.getEnvironment());
        assertEquals("http://localhost:8092/pdp/", StdPolicyEngine.getPDPURL());
        assertEquals(Arrays.asList(UEB, DMAAP), policyEngine.getNotificationType());
        assertEquals(Arrays.asList(SERVER_NAME, SERVER_NAME),
                policyEngine.getNotificationURLList());
    }

    @Test
    public void testStdPolicyEngineWithPropertiesInitialize_noException() throws Exception {
        final StdPolicyEngine policyEngine =
                new StdPolicyEngine(getDefaultProperties(), (String) null);
        policyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);
        assertEquals("TEST", StdPolicyEngine.getEnvironment());
        assertEquals("http://localhost:8092/pdp/", StdPolicyEngine.getPDPURL());
        assertEquals(Arrays.asList(UEB, DMAAP), policyEngine.getNotificationType());
        assertEquals(Arrays.asList(SERVER_NAME, SERVER_NAME),
                policyEngine.getNotificationURLList());
    }

    @Test
    public void testStdPolicyEngineInitializeWithSingleServerName_noException() throws Exception {
        final File file = temporaryFolder.newFile(CONFIG_PROPERTIES_FILE);

        final Properties properties = new Properties();
        properties.setProperty(PDP_URL_PROP_NAME, PDP_PROP_VALUE);
        properties.setProperty(CLIENT_ID_PROP_NAME, "test");
        properties.setProperty(CLIENT_KEY_PROP_NAME, "test");
        properties.setProperty(NOTIFICATION_TYPE_PROP_NAME, UEB);
        properties.setProperty(NOTIFICATION_SERVERS_PROP_NAME, SERVER_NAME);
        properties.setProperty(NOTIFICATION_TOPIC_PROP_NAME, "test");
        properties.setProperty(ENVIRONMENT_PROP_NAME, "TEST");
        creatPropertyFile(file, properties);

        final StdPolicyEngine policyEngine = new StdPolicyEngine(file.toString(), (String) null);
        policyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);
        assertEquals(Arrays.asList(SERVER_NAME), policyEngine.getNotificationURLList());
    }

    @Test
    public void testStdPolicyEngineInitializeWithSingleNotificationType_noException()
            throws Exception {
        final File file = temporaryFolder.newFile(CONFIG_PROPERTIES_FILE);

        final Properties properties = new Properties();
        properties.setProperty(PDP_URL_PROP_NAME, PDP_PROP_VALUE);
        properties.setProperty(CLIENT_ID_PROP_NAME, "test");
        properties.setProperty(CLIENT_KEY_PROP_NAME, "test");
        properties.setProperty(NOTIFICATION_TYPE_PROP_NAME, UEB);
        properties.setProperty(NOTIFICATION_SERVERS_PROP_NAME, SERVER_NAME);
        properties.setProperty(NOTIFICATION_TOPIC_PROP_NAME, "test");
        properties.setProperty(ENVIRONMENT_PROP_NAME, "TEST");
        creatPropertyFile(file, properties);

        final StdPolicyEngine policyEngine = new StdPolicyEngine(file.toString(), (String) null);
        policyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);
        assertEquals(Arrays.asList(UEB), policyEngine.getNotificationType());
    }

    @Test(expected = PolicyEngineException.class)
    public void testStdPolicyEngineInitialize_InvalidPropertyFile_Exception()
            throws PolicyEngineException {
        new StdPolicyEngine("Invalid.properties", (String) null);
    }

    @Test(expected = PolicyEngineException.class)
    public void testStdPolicyEngineInitialize_InvalidPropertyFileWithExt_Exception()
            throws Exception {
        final File emptyFile = temporaryFolder.newFile("EmptyFile.txt");
        new StdPolicyEngine(emptyFile.toString(), (String) null);
    }

    @Test(expected = PolicyEngineException.class)
    public void testStdPolicyEngineInitialize_NullArguments_Exception() throws Exception {
        new StdPolicyEngine((String) null, (String) null);
    }

    @Test(expected = PolicyEngineException.class)
    public void testStdPolicyEngineWithPropertiesInitialize_NullArguments_Exception()
            throws Exception {
        new StdPolicyEngine((Properties) null, (String) null);
    }

    @Test(expected = PolicyEngineException.class)
    public void testStdPolicyEngineInitialize_PropertyFileMissingMandatoryProperties_Exception()
            throws Exception {
        final File file = temporaryFolder.newFile(INVALID_CONFIG_PROPERTY_FILE);
        final Properties properties = new Properties();
        properties.setProperty(PDP_URL_PROP_NAME, PDP_PROP_VALUE);
        creatPropertyFile(file, properties);

        new StdPolicyEngine(file.toString(), (String) null);
    }

    @Test
    public void testStdPolicyEngineInitialize_MultiplePdp_noException() throws Exception {
        final File file = temporaryFolder.newFile(CONFIG_PROPERTIES_FILE);

        final Properties properties = new Properties();
        properties.setProperty(PDP_URL_PROP_NAME,
                PDP_PROP_VALUE + PolicyConfigConstants.SEMICOLLON + PDP_PROP_VALUE_1);
        properties.setProperty(CLIENT_ID_PROP_NAME, "test");
        properties.setProperty(CLIENT_KEY_PROP_NAME, "test");
        properties.setProperty(NOTIFICATION_TYPE_PROP_NAME, UEB + COMMA + DMAAP);
        properties.setProperty(NOTIFICATION_SERVERS_PROP_NAME, SERVER_NAME + COMMA + SERVER_NAME);
        properties.setProperty(NOTIFICATION_TOPIC_PROP_NAME, "test");
        properties.setProperty(ENVIRONMENT_PROP_NAME, "TEST");
        creatPropertyFile(file, properties);

        final StdPolicyEngine policyEngine = new StdPolicyEngine(file.toString(), (String) null);
        policyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);
        assertEquals("http://localhost:8092/pdp/", StdPolicyEngine.getPDPURL());
        StdPolicyEngine.rotatePDPList();
        assertEquals("https://localhost:8091/pdp/", StdPolicyEngine.getPDPURL());
        assertEquals(Arrays.asList(UEB, DMAAP), policyEngine.getNotificationType());
        assertEquals(Arrays.asList(SERVER_NAME, SERVER_NAME),
                policyEngine.getNotificationURLList());
    }

    @Test(expected = PolicyEngineException.class)
    public void testStdPolicyEngineInitialize_NoPDP_noException() throws Exception {
        final File file = temporaryFolder.newFile(CONFIG_PROPERTIES_FILE);

        final Properties properties = new Properties();
        properties.setProperty(CLIENT_ID_PROP_NAME, "test");
        properties.setProperty(CLIENT_KEY_PROP_NAME, "test");
        properties.setProperty(NOTIFICATION_TYPE_PROP_NAME, UEB + COMMA + DMAAP);
        properties.setProperty(NOTIFICATION_SERVERS_PROP_NAME, SERVER_NAME + COMMA + SERVER_NAME);
        properties.setProperty(NOTIFICATION_TOPIC_PROP_NAME, "test");
        properties.setProperty(ENVIRONMENT_PROP_NAME, "TEST");
        creatPropertyFile(file, properties);

        new StdPolicyEngine(file.toString(), (String) null);
    }

    @Test
    public void testStdPolicyEngineInitializeNotificationTypeDMMAP_noException() throws Exception {
        final File file = temporaryFolder.newFile(CONFIG_PROPERTIES_FILE);

        final Properties properties = new Properties();
        properties.setProperty(PDP_URL_PROP_NAME, PDP_PROP_VALUE);
        properties.setProperty(CLIENT_ID_PROP_NAME, "test");
        properties.setProperty(CLIENT_KEY_PROP_NAME, "test");
        properties.setProperty(NOTIFICATION_TYPE_PROP_NAME, DMAAP);
        properties.setProperty(NOTIFICATION_SERVERS_PROP_NAME, SERVER_NAME + COMMA + SERVER_NAME);
        properties.setProperty(NOTIFICATION_TOPIC_PROP_NAME, "test");
        properties.setProperty(ENVIRONMENT_PROP_NAME, "TEST");
        creatPropertyFile(file, properties);

        final StdPolicyEngine policyEngine = new StdPolicyEngine(file.toString(), (String) null);
        policyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);

        assertEquals(Arrays.asList(DMAAP), policyEngine.getNotificationType());

    }

    @Test
    public void testStdPolicyEngineSendEvent_noException() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doReturn(new ResponseEntity<>(getStdPolicyResponse(), HttpStatus.ACCEPTED))
                .when(spyPolicyEngine)
                .callNewPDP(eq(SEND_EVENT_RESOURCE_NAME), any(), any(), any());

        final Collection<PolicyResponse> actualPolicyResponses =
                spyPolicyEngine.sendEvent(Collections.emptyMap(), REQUEST_UUID);

        assertEquals(1, actualPolicyResponses.size());

    }

    @Test(expected = PolicyEventException.class)
    public void testStdPolicyEngineSendEvent_NullEventRequestParameters_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        spyPolicyEngine.sendEvent((EventRequestParameters) null);
    }

    @Test(expected = PolicyEventException.class)
    public void testStdPolicyEngineSendEvent_EventRequestParameters_CallPDPThrow401Exception_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        Mockito.doThrow(new RuntimeException(new RestClientException("Error 401")))
                .when(spyPolicyEngine).callNewPDP(any(), any(), any(), any());
        spyPolicyEngine.sendEvent(Collections.emptyMap(), REQUEST_UUID);
    }

    @Test
    public void testStdPolicyEngineSendEvent_EventRequestParameters_noException() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<StdPolicyResponse[]> stubbedResponse =
                new ResponseEntity<>(getStdPolicyResponse(), HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(SEND_EVENT_RESOURCE_NAME),
                any(), any(), any());

        final EventRequestParameters eventRequestParameters = new EventRequestParameters();
        eventRequestParameters.setEventAttributes(Collections.emptyMap());
        eventRequestParameters.setRequestID(REQUEST_UUID);

        final Collection<PolicyResponse> actualPolicyResponses =
                spyPolicyEngine.sendEvent(eventRequestParameters);

        assertEquals(1, actualPolicyResponses.size());

    }

    @Test
    public void testStdPolicyEngineGetConfig_ConfigRequestParametersPolicyConfigJSON_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<APIPolicyConfigResponse[]> stubbedResponse = new ResponseEntity<>(
                getAPIPolicyConfigResponse(PolicyType.JSON, JSON_CONFIGURATION),
                HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_CONFIG_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final Collection<PolicyConfig> actualPolicyResponses =
                spyPolicyEngine.getConfig(new ConfigRequestParameters());

        assertEquals(1, actualPolicyResponses.size());

        final PolicyConfig actualPolicyConfig = actualPolicyResponses.iterator().next();
        assertNotNull(actualPolicyConfig.toJSON());

    }

    @Test
    public void testStdPolicyEngineGetConfig_ConfigRequestParametersPolicyConfigOther_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<APIPolicyConfigResponse[]> stubbedResponse = new ResponseEntity<>(
                getAPIPolicyConfigResponse(PolicyType.OTHER, COMMENTS), HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_CONFIG_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final Collection<PolicyConfig> actualPolicyResponses =
                spyPolicyEngine.getConfig(new ConfigRequestParameters());

        assertEquals(1, actualPolicyResponses.size());

        final PolicyConfig actualPolicyConfig = actualPolicyResponses.iterator().next();
        assertNotNull(actualPolicyConfig.toOther());

    }

    @Test
    public void testStdPolicyEngineGetConfig_ConfigRequestParametersPolicyConfigXML_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<APIPolicyConfigResponse[]> stubbedResponse = new ResponseEntity<>(
                getAPIPolicyConfigResponse(PolicyType.XML, XML_CONFIGURATION), HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_CONFIG_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final Collection<PolicyConfig> actualPolicyResponses =
                spyPolicyEngine.getConfig(new ConfigRequestParameters());

        assertEquals(1, actualPolicyResponses.size());

        final PolicyConfig actualPolicyConfig = actualPolicyResponses.iterator().next();
        assertNotNull(actualPolicyConfig.toXML());

    }

    @Test
    public void testStdPolicyEngineGetConfig_ConfigRequestParametersPolicyConfigProperties_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<APIPolicyConfigResponse[]> stubbedResponse = new ResponseEntity<>(
                getAPIPolicyConfigResponse(PolicyType.PROPERTIES, COMMENTS), HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_CONFIG_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final Collection<PolicyConfig> actualPolicyResponses =
                spyPolicyEngine.getConfig(new ConfigRequestParameters());

        assertEquals(1, actualPolicyResponses.size());

        final PolicyConfig actualPolicyConfig = actualPolicyResponses.iterator().next();
        assertNotNull(actualPolicyConfig.toProperties());

    }

    @Test(expected = PolicyConfigException.class)
    public void testStdPolicyEngineSendEvent_ConfigRequestParameters_CallPDPThrow404Exception_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new RuntimeException(new RestClientException("Error 404"))).when(spyPolicyEngine)
                .callNewPDP(any(), any(), any(), any());
        spyPolicyEngine.getConfig(new ConfigRequestParameters());
    }

    @Test
    public void testStdPolicyEngineListConfig_ConfigRequestParametersPolicyConfigProperties_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final APIPolicyConfigResponse[] apiPolicyConfigResponse =
                getAPIPolicyConfigResponse(PolicyType.JSON, JSON_CONFIGURATION);

        final ResponseEntity<APIPolicyConfigResponse[]> stubbedResponse =
                new ResponseEntity<>(apiPolicyConfigResponse, HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_CONFIG_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final Collection<String> actualResponse =
                spyPolicyEngine.listConfig(new ConfigRequestParameters());
        assertEquals(1, actualResponse.size());
        assertNotNull(actualResponse.iterator().next());

    }

    @Test
    public void testStdPolicyEngineListConfig_ConfigRequestParametersMessageConfigContainsPE300_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final APIPolicyConfigResponse[] apiPolicyConfigResponse = getAPIPolicyConfigResponse(
                PolicyType.JSON, JSON_CONFIGURATION, PolicyConfigConstants.PE300);

        final ResponseEntity<APIPolicyConfigResponse[]> stubbedResponse =
                new ResponseEntity<>(apiPolicyConfigResponse, HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_CONFIG_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final Collection<String> actualResponse =
                spyPolicyEngine.listConfig(new ConfigRequestParameters());

        assertEquals(1, actualResponse.size());

    }

    @Test
    public void testStdPolicyEngineListConfig_ConfigRequestParametersWithTestProperty_noException()
            throws Exception {
        final Properties defaultProperties = getDefaultProperties();
        defaultProperties.setProperty(PolicyConfigConstants.JUNIT_PROP_NAME, "test");
        final StdPolicyEngine spyPolicyEngine =
                getSpyPolicyEngine("test" + CONFIG_PROPERTIES_FILE, defaultProperties);

        final Collection<String> actualResponse =
                spyPolicyEngine.listConfig(new ConfigRequestParameters());

        assertEquals(1, actualResponse.size());

    }

    @Test
    public void testStdPolicyEnginGetDecision_PolicyDecision_noException() throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<StdDecisionResponse> stubbedResponse =
                new ResponseEntity<>(getStdDecisionResponse(), HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_DECISION_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final DecisionResponse actualResponse =
                spyPolicyEngine.getDecision(ONAP_NAME_VAL, Collections.emptyMap(), REQUEST_UUID);

        assertNotNull(actualResponse);
    }

    @Test(expected = PolicyDecisionException.class)
    public void testStdPolicyEngineGetDecision_PolicyDecision_CallPDPThrow400Exception_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new RuntimeException(new RestClientException("Error 400"))).when(spyPolicyEngine)
                .callNewPDP(eq(GET_DECISION_RESOURCE_NAME), eq(HttpMethod.POST), any(), any());
        spyPolicyEngine.getDecision(ONAP_NAME_VAL, Collections.emptyMap(), REQUEST_UUID);
    }

    @Test
    public void testStdPolicyEnginGetDecision_DecisionRequestParameters_noException()
            throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<StdDecisionResponse> stubbedResponse =
                new ResponseEntity<>(getStdDecisionResponse(), HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_DECISION_RESOURCE_NAME),
                eq(HttpMethod.POST), any(), any());

        final DecisionRequestParameters requestParameters = new DecisionRequestParameters();
        requestParameters.setOnapName(ONAP_NAME_VAL);
        requestParameters.setRequestID(REQUEST_UUID);
        requestParameters.setDecisionAttributes(Collections.emptyMap());

        final DecisionResponse actualResponse = spyPolicyEngine.getDecision(requestParameters);

        assertNotNull(actualResponse);
    }

    @Test(expected = PolicyDecisionException.class)
    public void ttestStdPolicyEnginGetDecision_NullDecisionRequestParameters_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        spyPolicyEngine.getDecision((DecisionRequestParameters) null);
    }

    @Test
    public void testStdPolicyEnginGetMetrics_MetricsRequestParameters_noException()
            throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Metrics", HttpStatus.ACCEPTED);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(GET_METRICS_RESOURCE_NAME),
                eq(HttpMethod.GET), any(), any());

        final MetricsResponse actualResponse =
                spyPolicyEngine.getMetrics(new MetricsRequestParameters());
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.ACCEPTED.value(), actualResponse.getResponseCode());

    }

    @Test
    public void testStdPolicyEngineGetMetrics_MetricsRequestParametersCallPDPThrowHttpException_ResponseWithHttpCode()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new PolicyException(new HttpClientErrorException(HttpStatus.BAD_GATEWAY)))
                .when(spyPolicyEngine)
                .callNewPDP(eq(GET_METRICS_RESOURCE_NAME), eq(HttpMethod.GET), any(), any());

        final MetricsResponse actualResponse =
                spyPolicyEngine.getMetrics(new MetricsRequestParameters());
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.BAD_GATEWAY.value(), actualResponse.getResponseCode());

    }

    @Test(expected = PolicyException.class)
    public void testStdPolicyEngineGetMetrics_MetricsRequestParametersCallPDPThrowPolicyException_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(PolicyException.class).when(spyPolicyEngine)
                .callNewPDP(eq(GET_METRICS_RESOURCE_NAME), eq(HttpMethod.GET), any(), any());

        spyPolicyEngine.getMetrics(new MetricsRequestParameters());

    }

    @Test
    public void testStdPolicyEnginPushPolicy_PushPolicyParameters_noException() throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(PUSH_POLICY_RESOURCE_NAME),
                eq(HttpMethod.PUT), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.pushPolicy(new PushPolicyParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test
    public void testStdPolicyEnginePushPolicy_PushPolicyParametersThrowsHttpClientErrorException_ResponseWithHttpCode()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new PolicyException(new HttpClientErrorException(HttpStatus.BAD_GATEWAY)))
                .when(spyPolicyEngine)
                .callNewPDP(eq(PUSH_POLICY_RESOURCE_NAME), eq(HttpMethod.PUT), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.pushPolicy(new PushPolicyParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.BAD_GATEWAY.value(), actualResponse.getResponseCode());

    }

    @Test(expected = PolicyException.class)
    public void testStdPolicyEnginePushPolicy_PushPolicyParameters_Exception() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(PolicyException.class).when(spyPolicyEngine)
                .callNewPDP(eq(PUSH_POLICY_RESOURCE_NAME), eq(HttpMethod.PUT), any(), any());

        spyPolicyEngine.pushPolicy(new PushPolicyParameters());

    }

    @Test
    public void testStdPolicyEnginDeletePolicy_DeletePolicyParameters_noException()
            throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(DELETE_POLICY_RESOURCE_NAME),
                eq(HttpMethod.DELETE), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.deletePolicy(new DeletePolicyParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test
    public void testStdPolicyEnginGetDictionaryItem_DictionaryParameters_noException()
            throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<APIDictionaryResponse> stubbedResponse =
                new ResponseEntity<>(getAPIDictionaryResponse(), HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(
                eq(GET_DICTIONARY_ITEMS_RESOURCE_NAME), eq(HttpMethod.POST), any(), any());

        final DictionaryResponse actualResponse =
                spyPolicyEngine.getDictionaryItem(new DictionaryParameters());

        assertNotNull(actualResponse);
    }

    @Test
    public void testStdPolicyGetDictionaryItem_DictionaryParametersWithHttp400ExceptionThrown_ResponseWithHttpCode()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new RuntimeException(
                new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Error 400")))
                        .when(spyPolicyEngine).callNewPDP(eq(GET_DICTIONARY_ITEMS_RESOURCE_NAME),
                                eq(HttpMethod.POST), any(), any());

        final DictionaryResponse actualResponse =
                spyPolicyEngine.getDictionaryItem(new DictionaryParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), actualResponse.getResponseCode());

    }

    @Test
    public void testStdPolicyGetDictionaryItem_DictionaryParametersWithHttp401ExceptionThrown_ResponseWithHttpCode()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new RuntimeException(
                new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Error 401")))
                        .when(spyPolicyEngine).callNewPDP(eq(GET_DICTIONARY_ITEMS_RESOURCE_NAME),
                                eq(HttpMethod.POST), any(), any());

        final DictionaryResponse actualResponse =
                spyPolicyEngine.getDictionaryItem(new DictionaryParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), actualResponse.getResponseCode());

    }

    @Test
    public void testStdPolicyGetDictionaryItem_DictionaryParametersWithRunTimeExceptionThrown_ResponseWithHttpCode()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();
        doThrow(new RuntimeException(
                new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error")))
                        .when(spyPolicyEngine).callNewPDP(eq(GET_DICTIONARY_ITEMS_RESOURCE_NAME),
                                eq(HttpMethod.POST), any(), any());

        final DictionaryResponse actualResponse =
                spyPolicyEngine.getDictionaryItem(new DictionaryParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualResponse.getResponseCode());

    }

    @Test
    public void testStdPolicyEnginCreateDictionaryItem_DictionaryParameters_noException()
            throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(
                eq(CREATE_DICTIONARY_ITEM_RESOURCE_NAME), eq(HttpMethod.PUT), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.createDictionaryItem(new DictionaryParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test
    public void testStdPolicyEnginUpdateDictionaryItem_DictionaryParameters_noException()
            throws Exception {

        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(
                eq(UPDATE_DICTIONARY_ITEM_RESOURCE_NAME), eq(HttpMethod.PUT), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.updateDictionaryItem(new DictionaryParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test
    public void testStdPolicyEnginPolicyEngineImport_ImportParameters_noException()
            throws Exception {
        final File emptyfile = temporaryFolder.newFile("emptyFile.txt");
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(
                eq(POLICY_ENGINE_IMPORT_RESOURCE_NAME), eq(HttpMethod.POST), any(), any());

        final ImportParameters importParameters = new ImportParameters();
        importParameters.setFilePath(emptyfile.toString());
        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.policyEngineImport(importParameters);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test
    public void testStdPolicyEnginCreatePolicy_PolicyParameters_noException() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(CREATE_POLICY_RESOURCE_NAME),
                eq(HttpMethod.PUT), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.createPolicy(new PolicyParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test
    public void testStdPolicyEnginUpdatePolicy_PolicyParameters_noException() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(UPDATE_POLICY_RESOURCE_NAME),
                eq(HttpMethod.PUT), any(), any());

        final PolicyChangeResponse actualResponse =
                spyPolicyEngine.updatePolicy(new PolicyParameters());

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK.value(), actualResponse.getResponseCode());
    }

    @Test(expected = PolicyException.class)
    public void testStdPolicyEnginPushPolicy_NullValues_Exception() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        spyPolicyEngine.pushPolicy(null, null, null, null, null);
    }

    @Test(expected = PolicyException.class)
    public void testStdPolicyEnginPushPolicy_EmptyPolicyScope_Exception() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        spyPolicyEngine.pushPolicy("", null, null, null, null);
    }

    @Test(expected = PolicyException.class)
    public void testStdPolicyEnginPushPolicy_EmptyPolicyName_Exception() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        spyPolicyEngine.pushPolicy("POLICY_SCOPE", "", null, null, null);
    }

    @Test
    public void testStdPolicyEnginPushPolicy_PolicyParameters_noException() throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(PUSH_POLICY_RESOURCE_NAME),
                eq(HttpMethod.PUT), any(), any());

        final String actualResponse = spyPolicyEngine.pushPolicy("POLICY_SCOPE", ONAP_NAME_VAL,
                "POLICY_TYPE", "POLICY_GROUP", REQUEST_UUID);

        assertNotNull(actualResponse);
    }

    @Test
    public void testStdPolicyEnginCreateUpdateConfigPolicy_PolicyParameters_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(UPDATE_POLICY_RESOURCE_NAME),
                eq(HttpMethod.PUT), any(), any());

        final String actualResponse = spyPolicyEngine.createUpdateConfigPolicy("POLICY_NAME",
                ONAP_NAME_VAL, ONAP_NAME_VAL, "CONFIG_NAME", Collections.emptyMap(),
                PolicyType.JSON.toString().toUpperCase(), "", "POLICY_SCOPE", REQUEST_UUID, "", "",
                "", new Date().toString(), true);

        assertNotNull(actualResponse);
    }

    @Test(expected = PolicyException.class)
    public void testStdPolicyEnginCreateUpdateConfigPolicy_NullPolicyName_Exception()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        spyPolicyEngine.createUpdateConfigPolicy(null, null, null, null, null, null, null, null,
                null, null, null, null, null, true);
    }

    @Test
    public void testStdPolicyEnginCreateUpdateConfigFirewallPolicy_PolicyParameters_noException()
            throws Exception {
        final StdPolicyEngine spyPolicyEngine = getSpyPolicyEngine();

        final ResponseEntity<String> stubbedResponse =
                new ResponseEntity<>("Successful", HttpStatus.OK);

        doReturn(stubbedResponse).when(spyPolicyEngine).callNewPDP(eq(UPDATE_POLICY_RESOURCE_NAME),
                eq(HttpMethod.PUT), any(), any());

        final String actualResponse = spyPolicyEngine.createUpdateConfigFirewallPolicy(
                "POLICY_NAME", getJsonObject(JSON_CONFIGURATION), "POLICY_SCOPE", REQUEST_UUID, "",
                "", "", new Date().toString(), true);

        assertNotNull(actualResponse);
    }

    private JsonObject getJsonObject(final String jsonString) {
        try (final JsonReader jsonReader = Json.createReader(new StringReader(jsonString));) {
            return jsonReader.readObject();
        }
    }

    private StdPolicyEngine getSpyPolicyEngine() throws IOException, PolicyEngineException {
        return getSpyPolicyEngine(CONFIG_PROPERTIES_FILE, getDefaultProperties());
    }

    private StdPolicyEngine getSpyPolicyEngine(final String filename, final Properties properties)
            throws IOException, PolicyEngineException {
        final File file = temporaryFolder.newFile(filename);

        creatPropertyFile(file, properties);

        final StdPolicyEngine spyPolicyEngine =
                spy(new StdPolicyEngine(file.toString(), (String) null));
        spyPolicyEngine.setScheme(NotificationScheme.MANUAL_NOTIFICATIONS);
        return spyPolicyEngine;
    }

    private Properties getDefaultProperties() {
        final Properties properties = new Properties();
        properties.setProperty(PDP_URL_PROP_NAME, PDP_PROP_VALUE);
        properties.setProperty(CLIENT_ID_PROP_NAME, "test");
        properties.setProperty(CLIENT_KEY_PROP_NAME, "test");
        properties.setProperty(NOTIFICATION_TYPE_PROP_NAME, UEB + COMMA + DMAAP);
        properties.setProperty(NOTIFICATION_SERVERS_PROP_NAME, SERVER_NAME + COMMA + SERVER_NAME);
        properties.setProperty(NOTIFICATION_TOPIC_PROP_NAME, "test");
        properties.setProperty(ENVIRONMENT_PROP_NAME, "TEST");
        properties.setProperty(PolicyConfigConstants.JUNIT_PROP_NAME, "false");
        return properties;
    }

    private StdDecisionResponse getStdDecisionResponse() {
        final StdDecisionResponse response = new StdDecisionResponse();
        response.setDecision(PolicyDecision.PERMIT);
        response.setDetails(PolicyDecision.PERMIT.name());

        return response;
    }

    private APIDictionaryResponse getAPIDictionaryResponse() {
        final APIDictionaryResponse response = new APIDictionaryResponse();
        response.setResponseCode(0);
        response.setResponseMessage("");
        response.setDictionaryData(Collections.<String, String>emptyMap());
        response.setDictionaryJson(Collections.<String, String>emptyMap());
        return response;
    }

    private StdPolicyResponse[] getStdPolicyResponse() {
        final StdPolicyResponse response = new StdPolicyResponse();
        response.setPolicyResponseStatus(PolicyResponseStatus.ACTION_TAKEN);
        return new StdPolicyResponse[] {response};
    }

    private void creatPropertyFile(final File file, final Properties properties)
            throws IOException {
        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath());) {
            properties.store(bufferedWriter, COMMENTS);
        }
    }

    private APIPolicyConfigResponse[] getAPIPolicyConfigResponse(final PolicyType policyType,
            final String configuration) {

        return getAPIPolicyConfigResponse(policyType, configuration, null);
    }

    private APIPolicyConfigResponse[] getAPIPolicyConfigResponse(final PolicyType policyType,
            final String configuration, final String policyConfigMessage) {
        final APIPolicyConfigResponse configResponse = new APIPolicyConfigResponse();
        configResponse.setConfig(configuration);
        configResponse.setMatchingConditions(getMatchingConditions());
        configResponse.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_RETRIEVED);
        configResponse.setPolicyName(POLICY_NAME);
        configResponse.setPolicyType(PolicyConfigType.BRMS_RAW);
        configResponse.setType(policyType);
        configResponse.setPolicyVersion(POLICY_VERSION);
        configResponse.setProperty(Collections.emptyMap());
        configResponse.setPolicyConfigMessage(policyConfigMessage);

        return new APIPolicyConfigResponse[] {configResponse};
    }

    private Map<String, String> getMatchingConditions() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(ONAP_NAME, POLICY_NAME);
        attributes.put(CONFIG_NAME, "Configuration_name");
        return attributes;
    }

}
