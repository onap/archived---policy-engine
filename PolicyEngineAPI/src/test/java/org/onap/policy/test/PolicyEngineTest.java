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

package org.onap.policy.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.api.EventRequestParameters;
import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.MetricsRequestParameters;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdPolicyEngine;

public class PolicyEngineTest {

    private static final String ONAP_NAME_VALUE = "ONAP";
    private static final String STD_POLICY_ENGINE_LOCAL_VARIABLE = "stdPolicyEngine";
    private static final UUID REQUEST_UUID = UUID.randomUUID();
    private static final String TEST_CONFIG_PASS_PROPERTIES = "Test/config_pass.properties";
    private static final Logger logger = FlexLogger.getLogger(PolicyEngineTest.class);
    private PolicyEngine policyEngine = null;
    private String filePath = null;

    private final StdPolicyEngine mockedStdPolicyEngine = Mockito.mock(StdPolicyEngine.class);

    @Test
    public void testPolicyEngineForFail() {
        PolicyEngine policyEngine = null;
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNull(policyEngine);
        // Test even for this case.
        filePath = "NotNull";
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNull(policyEngine);
    }

    @Test
    public void testPolicyEngineforPropertyFileError() {
        filePath = "Test/config_error.property";
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNull(policyEngine);
    }

    @Test
    public void testPolicyEngineforPDPURLError() {
        final String filePath = "Test/config_fail.properties";
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNull(policyEngine);
    }

    @Test
    public void testPolicyEngineForPass() {
        final String filePath = TEST_CONFIG_PASS_PROPERTIES;
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNotNull(policyEngine);
    }

    @Test
    public void testPolicyEngineForUEBPass() {
        final String filePath = "Test/config_UEB_pass.properties";
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNotNull(policyEngine);
    }

    @Test
    public void testPolicyEngineForUEBBadType() {
        final String filePath = "Test/config_UEB_bad_type.properties";
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNotNull(policyEngine);
    }

    @Test
    public void testPolicyEngineForUEBBadServerType() {
        final String filePath = "Test/config_UEB_badservers.properties";
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNotNull(policyEngine);
    }

    @Test
    public void testPolicyEngineNotficationAutoUEB() {
        final String filePath = "Test/config_UEB_pass.properties";
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
            policyEngine.setScheme(NotificationScheme.AUTO_ALL_NOTIFICATIONS);
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNotNull(policyEngine);
    }

    @Test
    public void testPolicyEngineNotficationAuto() {
        final String filePath = TEST_CONFIG_PASS_PROPERTIES;
        isFileAvailable(filePath);
        try {
            policyEngine = new PolicyEngine(filePath);
            policyEngine.setScheme(NotificationScheme.AUTO_ALL_NOTIFICATIONS);
            // policyEngine.getNotification();
        } catch (final PolicyEngineException e) {
            logger.warn(e.getMessage());
        }
        assertNotNull(policyEngine);
    }

    @Test
    public void testGetConfig_ConfigRequestParameters_StdPolicyEngineGetConfigCalled()
            throws PolicyEngineException, PolicyConfigException {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getConfig(new ConfigRequestParameters());

        verify(mockedStdPolicyEngine).getConfig(Mockito.any(ConfigRequestParameters.class));

    }

    @Test
    public void testlistConfig_ConfigRequestParameters_StdPolicyEngineListConfigCalled()
            throws PolicyEngineException, PolicyConfigException {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.listConfig(new ConfigRequestParameters());

        verify(mockedStdPolicyEngine).listConfig(any(ConfigRequestParameters.class));
    }

    @Test
    public void testSendEvent_EventAttributes_StdPolicyEngineSendEventCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.sendEvent(Collections.emptyMap());

        verify(mockedStdPolicyEngine).sendEvent(eq(Collections.emptyMap()), eq(null));
    }

    @Test
    public void testSendEvent_EventAttributesWithUUID_StdPolicyEngineSendEventCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.sendEvent(Collections.emptyMap(), REQUEST_UUID);

        verify(mockedStdPolicyEngine).sendEvent(eq(Collections.emptyMap()), eq(REQUEST_UUID));
    }

    @Test
    public void testSendEvent_EventRequestParameters_StdPolicyEngineSendEventCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.sendEvent(new EventRequestParameters());

        verify(mockedStdPolicyEngine).sendEvent(any(EventRequestParameters.class));
    }

    @Test
    public void testGetDecision_RequestParameters_StdPolicyEngineGetDecisionCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getDecision(ONAP_NAME_VALUE, Collections.emptyMap());

        verify(mockedStdPolicyEngine).getDecision(eq(ONAP_NAME_VALUE), eq(Collections.emptyMap()), eq(null));
    }

    @Test
    public void testGetDecision_RequestParametersWithUUID_StdPolicyEngineGetDecisionCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getDecision(ONAP_NAME_VALUE, Collections.emptyMap(), REQUEST_UUID);

        verify(mockedStdPolicyEngine).getDecision(eq(ONAP_NAME_VALUE), eq(Collections.emptyMap()), eq(REQUEST_UUID));
    }

    @Test
    public void testGetDecision_DecisionRequestParameters_StdPolicyEngineGetDecisionCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getDecision(new DecisionRequestParameters());

        verify(mockedStdPolicyEngine).getDecision(any(DecisionRequestParameters.class));
    }

    @Test
    public void testGetDecision_MetricsRequestParameters_StdPolicyEngineGetMetricsCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getMetrics(new MetricsRequestParameters());

        verify(mockedStdPolicyEngine).getMetrics(any(MetricsRequestParameters.class));
    }

    @Test
    public void testGetDecision_DictionaryParameters_StdPolicyEngineGetDictionaryItemCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getDictionaryItem(new DictionaryParameters());

        verify(mockedStdPolicyEngine).getDictionaryItem(any(DictionaryParameters.class));
    }

    @Test
    public void testCreateDictionaryItem_DictionaryParameters_StdPolicyEngineCreateDictionaryItemCalled()
            throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.createDictionaryItem(new DictionaryParameters());

        verify(mockedStdPolicyEngine).createDictionaryItem(any(DictionaryParameters.class));
    }

    @Test
    public void testUpdateDictionaryItem_DictionaryParameters_StdPolicyEngineDictionaryItemCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.updateDictionaryItem(new DictionaryParameters());

        verify(mockedStdPolicyEngine).updateDictionaryItem(any(DictionaryParameters.class));
    }

    @Test
    public void testCreatePolicy_PolicyParameters_StdPolicyEngineCreatePolicyCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.createPolicy(new PolicyParameters());

        verify(mockedStdPolicyEngine).createPolicy(any(PolicyParameters.class));
    }

    @Test
    public void testUpdatePolicy_PolicyParameters_StdPolicyEngineUpdatePolicyCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.updatePolicy(new PolicyParameters());

        verify(mockedStdPolicyEngine).updatePolicy(any(PolicyParameters.class));
    }

    @Test
    public void testPushPolicy_PushPolicyParameters_StdPolicyEnginePushPolicyCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.pushPolicy(new PushPolicyParameters());

        verify(mockedStdPolicyEngine).pushPolicy(any(PushPolicyParameters.class));
    }

    @Test
    public void testDeletePolicy_DeletePolicyParameters_StdPolicyEngineDeletePolicyCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.deletePolicy(new DeletePolicyParameters());

        verify(mockedStdPolicyEngine).deletePolicy(any(DeletePolicyParameters.class));
    }

    @Test
    public void testPolicyEngineImport_DictionaryParameters_StdPolicyEnginePolicyEngineImportCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.policyEngineImport(new ImportParameters());

        verify(mockedStdPolicyEngine).policyEngineImport(any(ImportParameters.class));
    }

    @Test
    public void testGetNotification_StdPolicyEngineGetNotificationCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.getNotification();

        verify(mockedStdPolicyEngine, Mockito.atLeastOnce()).getNotification();
    }

    @Test
    public void testClearNotification_StdPolicyEngineClearNotificationCalled() throws Exception {
        isFileAvailable(TEST_CONFIG_PASS_PROPERTIES);

        final PolicyEngine objUnderTest = new PolicyEngine(TEST_CONFIG_PASS_PROPERTIES);
        Whitebox.setInternalState(objUnderTest, STD_POLICY_ENGINE_LOCAL_VARIABLE, mockedStdPolicyEngine);

        objUnderTest.clearNotification();

        verify(mockedStdPolicyEngine, Mockito.atLeastOnce()).stopNotification();
    }

    public void isFileAvailable(final String filePath) {
        final Path file = Paths.get(filePath);
        if (Files.notExists(file)) {
            logger.error("File Doesn't Exist " + file.toString());
            fail("File: " + filePath + " Not found");
        }
    }
}
