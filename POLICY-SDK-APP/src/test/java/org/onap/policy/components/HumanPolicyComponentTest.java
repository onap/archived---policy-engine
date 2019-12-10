/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.att.research.xacml.util.XACMLPolicyScanner.CallbackResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class HumanPolicyComponentTest {

    private AttributeIdentifiers attrIds;
    private HtmlProcessor processor;
    private static File temp;
    private static File tempAction;
    private static File tempConfig;

    @BeforeClass
    public static void setup() throws IOException {
        temp = File.createTempFile("tmpFile", ".tmp");
        tempAction = File.createTempFile("Action_test", ".tmp");
        tempConfig = File.createTempFile("Config_test", ".tmp");
        temp.deleteOnExit();
        tempAction.deleteOnExit();
        tempConfig.deleteOnExit();
    }

    @Test
    public void testAttributeIdentifiers() {
        String testCategory = "testCategory";
        String testID = "testId";
        String testType = "testType";
        String newTestType = "testNewType";

        attrIds = new AttributeIdentifiers(testCategory, testType, testID);
        assertEquals(testCategory, attrIds.category);
        assertEquals(testID, attrIds.id);
        assertEquals(testType, attrIds.getType());

        attrIds.setType(newTestType);
        assertEquals(newTestType, attrIds.getType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHumanPolicyComponentException() throws IOException {
        JAXBElement<PolicySetType> mockRoot = Mockito.mock(JAXBElement.class);
        when(mockRoot.getValue()).thenReturn(null);
        assertNull(HumanPolicyComponent.DescribePolicy(temp));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHtmlProcessorNull() throws IOException {
        processor = new HtmlProcessor(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHtmlProcessor() throws IOException {
        File tempFile = File.createTempFile("testFile", ".tmp");
        tempFile.delete();
        processor = new HtmlProcessor(tempFile, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHtmlProcessorInvalidObject() throws IOException {
        processor = new HtmlProcessor(temp, null);
    }

    @Test
    public void testHtmlProcessorConfigPolicySetType() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(tempConfig, mockPolicySetType);
        processor.onFinishScan(mockPolicySetType);
        verify(mockPolicySetType).getVersion();
    }

    @Test
    public void testHtmlProcessorActionPolicySetType() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(tempAction, mockPolicySetType);
        processor.onFinishScan(mockPolicySetType);
        verify(mockPolicySetType).getVersion();
    }

    @Test
    public void testHtmlProcessorConfigPolicyType() throws IOException {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        processor = new HtmlProcessor(tempConfig, mockPolicyType);
        verify(mockPolicyType).getVersion();
    }

    @Test
    public void testHtmlProcessorActionPolicyType() throws IOException {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        processor = new HtmlProcessor(tempAction, mockPolicyType);
        assertNotNull(processor.getAttributeIdentifiersMap());
        verify(mockPolicyType).getVersion();
    }

    @Test
    public void testHtmlProcessorOnPrePostVisitPolicySet() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = Mockito.mock(PolicySetType.class);

        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(Collections.emptyList());
        when(mockPolicySetType.getDescription()).thenReturn(null);

        CallbackResult preResult = processor.onPreVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", preResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();

        CallbackResult postResult = processor.onPostVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPrePostVisitPolicySetNullParent() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = null;
        JAXBElement<?> mockElement = Mockito.mock(JAXBElement.class);

        List<JAXBElement<?>> testList = new ArrayList<JAXBElement<?>>();
        testList.add(mockElement);

        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(testList);
        when(mockPolicySetType.getDescription()).thenReturn("");

        CallbackResult res = processor.onPreVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", res.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();

        CallbackResult postResult = processor.onPostVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPrePostVisitPolicy() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        List<Object> testList = new ArrayList<Object>();
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()).thenReturn(testList);
        when(mockPolicySetType.getDescription()).thenReturn(null);

        CallbackResult preResult = processor.onPreVisitPolicy(mockPolicySetType, mockPolicyType);
        assertEquals("CONTINUE", preResult.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        verify(mockPolicyType, atLeast(1)).getDescription();

        CallbackResult postResult = processor.onPostVisitPolicy(mockPolicySetType, mockPolicyType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        verify(mockPolicyType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPrePostVisitPolicyNullParent() throws IOException {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        PolicySetType mockPolicyParent = null;
        List<Object> testList = new ArrayList<Object>();
        testList.add(new Object());
        processor = new HtmlProcessor(temp, mockPolicyType);

        when(mockPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()).thenReturn(testList);
        when(mockPolicyType.getDescription()).thenReturn("");

        CallbackResult res = processor.onPreVisitPolicy(mockPolicyParent, mockPolicyType);
        assertEquals("CONTINUE", res.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        verify(mockPolicyType, atLeast(1)).getDescription();

        CallbackResult postResult = processor.onPostVisitPolicy(mockPolicyParent, mockPolicyType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        verify(mockPolicyType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorPolicy() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicyType.getRuleCombiningAlgId()).thenReturn(null);
        when(mockPolicyType.getPolicyId()).thenReturn(null);
        when(mockPolicyType.getVersion()).thenReturn(null);
        when(mockPolicyType.getTarget()).thenReturn(null);

        processor.policy(mockPolicyType);
        verify(mockPolicyType).getRuleCombiningAlgId();
        verify(mockPolicyType).getPolicyId();
        verify(mockPolicyType).getVersion();
        verify(mockPolicyType).getTarget();
    }

    @Test
    public void testHtmlProcessorPolicyListEmpty() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicyType.getRuleCombiningAlgId()).thenReturn(null);
        when(mockPolicyType.getPolicyId()).thenReturn(null);
        when(mockPolicyType.getVersion()).thenReturn(null);
        when(mockPolicyType.getTarget()).thenReturn(mockTargetType);
        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);

        processor.policy(mockPolicyType);

        verify(mockPolicyType, atLeast(1)).getRuleCombiningAlgId();
        verify(mockPolicyType, atLeast(1)).getPolicyId();
        verify(mockPolicyType, atLeast(1)).getVersion();
        verify(mockPolicyType, atLeast(1)).getTarget();
        verify(mockTargetType, atLeast(1)).getAnyOf();
    }

    @Test
    public void testHtmlProcessorPolicyListNotEmpty() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        anyOfList.add(new AnyOfType());
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicyType.getRuleCombiningAlgId()).thenReturn(null);
        when(mockPolicyType.getPolicyId()).thenReturn(null);
        when(mockPolicyType.getVersion()).thenReturn(null);
        when(mockPolicyType.getTarget()).thenReturn(mockTargetType);
        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);

        processor.policy(mockPolicyType);
        verify(mockPolicyType, atLeast(1)).getRuleCombiningAlgId();
        verify(mockPolicyType, atLeast(1)).getPolicyId();
        verify(mockPolicyType, atLeast(1)).getVersion();
        verify(mockPolicyType, atLeast(1)).getTarget();
        verify(mockTargetType, atLeast(1)).getAnyOf();
    }

    @Test
    public void testHtmlProcessorPolicyNull() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicyType.getRuleCombiningAlgId()).thenReturn(null);
        when(mockPolicyType.getPolicyId()).thenReturn(null);
        when(mockPolicyType.getVersion()).thenReturn(null);
        when(mockPolicyType.getTarget()).thenReturn(mockTargetType);
        when(mockPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()).thenReturn(null);
        when(mockTargetType.getAnyOf()).thenReturn(null);

        processor.policy(mockPolicyType);
        verify(mockPolicyType, atLeast(1)).getRuleCombiningAlgId();
        verify(mockPolicyType, atLeast(1)).getPolicyId();
        verify(mockPolicyType, atLeast(1)).getVersion();
        verify(mockPolicyType, atLeast(1)).getTarget();
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        verify(mockTargetType, atLeast(1)).getAnyOf();
    }

    @Test
    public void testHtmlProcessorPolicySet() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicyCombiningAlgId()).thenReturn("");
        when(mockPolicySetType.getPolicySetId()).thenReturn("");
        when(mockPolicySetType.getVersion()).thenReturn("");

        processor.policySet(mockPolicySetType, "");
        verify(mockPolicySetType, atLeast(1)).getPolicyCombiningAlgId();
        verify(mockPolicySetType, atLeast(1)).getPolicySetId();
        verify(mockPolicySetType, atLeast(1)).getVersion();
    }

    @Test
    public void testHtmlProcessorPolicySetNull() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getTarget()).thenReturn(mockTargetType);
        when(mockTargetType.getAnyOf()).thenReturn(null);
        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(null);

        processor.policySet(mockPolicySetType, "");
        verify(mockPolicySetType, atLeast(1)).getTarget();
        verify(mockTargetType, atLeast(1)).getAnyOf();
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
    }

    @Test
    public void testHtmlProcessorPolicySetEmpty() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getTarget()).thenReturn(mockTargetType);
        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);
        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(null);

        processor.policySet(mockPolicySetType, "");
        verify(mockPolicySetType, atLeast(1)).getTarget();
        verify(mockTargetType, atLeast(1)).getAnyOf();
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
    }

    @Test
    public void testHtmlProcessorPolicySetNotEmpty() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        anyOfList.add(new AnyOfType());

        when(mockPolicySetType.getTarget()).thenReturn(mockTargetType);
        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);
        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(null);

        processor = new HtmlProcessor(temp, mockPolicySetType);
        processor.policySet(mockPolicySetType, "");
        verify(mockPolicySetType, atLeast(1)).getTarget();
        verify(mockTargetType, atLeast(1)).getAnyOf();
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
    }

    @Test
    public void testHtmlProcessorRule() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        RuleType mockRuleType = Mockito.mock(RuleType.class);
        ConditionType mockConditionType = Mockito.mock(ConditionType.class);
        ObligationExpressionsType mockOESType = Mockito.mock(ObligationExpressionsType.class);
        ObligationExpressionType mockOEType = Mockito.mock(ObligationExpressionType.class);
        EffectType effectTypePermit = EffectType.PERMIT;
        processor = new HtmlProcessor(temp, mockPolicySetType);

        List<ObligationExpressionType> oblList = new ArrayList<ObligationExpressionType>();
        oblList.add(mockOEType);

        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);
        when(mockRuleType.getRuleId()).thenReturn(null);
        when(mockRuleType.getTarget()).thenReturn(null);
        when(mockRuleType.getCondition()).thenReturn(mockConditionType);
        when(mockRuleType.getObligationExpressions()).thenReturn(mockOESType);
        when(mockOESType.getObligationExpression()).thenReturn(oblList);
        when(mockOEType.getFulfillOn()).thenReturn(effectTypePermit);

        processor.rule(mockRuleType);

        verify(mockRuleType, atLeast(1)).getRuleId();
        verify(mockRuleType, atLeast(1)).getTarget();
        verify(mockRuleType, atLeast(1)).getCondition();
        verify(mockRuleType, atLeast(1)).getObligationExpressions();
        verify(mockOESType, atLeast(1)).getObligationExpression();
        verify(mockOEType, atLeast(1)).getFulfillOn();
    }

    @Test
    public void testHtmlProcessorRuleNullEmptyList() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        RuleType mockRuleType = Mockito.mock(RuleType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        EffectType effectTypePermit = EffectType.PERMIT;
        AdviceExpressionsType mockAdviceExsType = Mockito.mock(AdviceExpressionsType.class);
        AdviceExpressionType mockAdviceEx = Mockito.mock(AdviceExpressionType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        List<AdviceExpressionType> adviceExList = new ArrayList<AdviceExpressionType>();
        adviceExList.add(mockAdviceEx);

        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);
        when(mockRuleType.getRuleId()).thenReturn(null);
        when(mockRuleType.getTarget()).thenReturn(mockTargetType);
        when(mockRuleType.getObligationExpressions()).thenReturn(null);
        when(mockRuleType.getAdviceExpressions()).thenReturn(mockAdviceExsType);
        when(mockTargetType.getAnyOf()).thenReturn(null);
        when(mockAdviceExsType.getAdviceExpression()).thenReturn(adviceExList);
        when(mockAdviceEx.getAttributeAssignmentExpression()).thenReturn(null);
        when(mockAdviceEx.getAppliesTo()).thenReturn(effectTypePermit);

        processor.rule(mockRuleType);

        verify(mockRuleType, atLeast(1)).getEffect();
        verify(mockRuleType, atLeast(1)).getRuleId();
        verify(mockRuleType, atLeast(1)).getTarget();
        verify(mockRuleType, atLeast(1)).getCondition();
        verify(mockRuleType, atLeast(1)).getObligationExpressions();
        verify(mockRuleType, atLeast(1)).getAdviceExpressions();
        verify(mockTargetType, atLeast(1)).getAnyOf();
        verify(mockAdviceExsType, atLeast(1)).getAdviceExpression();
        verify(mockAdviceEx, atLeast(1)).getAttributeAssignmentExpression();
        verify(mockAdviceEx, atLeast(1)).getAppliesTo();

        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);
        processor.rule(mockRuleType);
        verify(mockTargetType, atLeast(1)).getAnyOf();
    }

    @Test
    public void testHtmlProcessorRuleNonNullObjects() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        RuleType mockRuleType = Mockito.mock(RuleType.class);
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        AdviceExpressionsType mockAdvice = Mockito.mock(AdviceExpressionsType.class);
        ObligationExpressionsType mockObEx = Mockito.mock(ObligationExpressionsType.class);
        AdviceExpressionType adviceExpTypeMock = Mockito.mock(AdviceExpressionType.class);
        ObligationExpressionType mockObExType = Mockito.mock(ObligationExpressionType.class);
        EffectType effectTypePermit = EffectType.PERMIT;
        processor = new HtmlProcessor(temp, mockPolicySetType);

        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        anyOfList.add(new AnyOfType());

        List<AdviceExpressionType> adviceList = new ArrayList<AdviceExpressionType>();
        adviceList.add(adviceExpTypeMock);

        List<AttributeAssignmentExpressionType> attrList = new ArrayList<AttributeAssignmentExpressionType>();

        List<ObligationExpressionType> obExList = new ArrayList<ObligationExpressionType>();
        obExList.add(mockObExType);

        List<Object> contentList = new ArrayList<>();
        contentList.add(new Object());

        when(mockRuleType.getRuleId()).thenReturn("");
        when(mockRuleType.getTarget()).thenReturn(mockTargetType);
        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);
        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);
        when(mockRuleType.getAdviceExpressions()).thenReturn(mockAdvice);
        when(mockAdvice.getAdviceExpression()).thenReturn(adviceList);
        when(mockRuleType.getObligationExpressions()).thenReturn(mockObEx);
        when(mockObEx.getObligationExpression()).thenReturn(obExList);
        when(mockObExType.getAttributeAssignmentExpression()).thenReturn(null);
        when(mockObExType.getFulfillOn()).thenReturn(effectTypePermit);
        when(adviceExpTypeMock.getAdviceId()).thenReturn("");
        when(adviceExpTypeMock.getAppliesTo()).thenReturn(effectTypePermit);
        when(adviceExpTypeMock.getAttributeAssignmentExpression()).thenReturn(attrList);

        processor.rule(mockRuleType);

        verify(mockRuleType, atLeast(1)).getRuleId();
        verify(mockRuleType, atLeast(1)).getTarget();
        verify(mockRuleType, atLeast(1)).getEffect();
        verify(mockRuleType, atLeast(1)).getAdviceExpressions();
        verify(mockRuleType, atLeast(1)).getObligationExpressions();
        verify(mockTargetType, atLeast(1)).getAnyOf();
        verify(mockObEx, atLeast(1)).getObligationExpression();
        verify(mockObExType, atLeast(1)).getAttributeAssignmentExpression();
        verify(mockObExType, atLeast(1)).getFulfillOn();
        verify(mockAdvice, atLeast(1)).getAdviceExpression();
        verify(adviceExpTypeMock, atLeast(1)).getAdviceId();
        verify(adviceExpTypeMock, atLeast(1)).getAppliesTo();
        verify(adviceExpTypeMock, atLeast(1)).getAttributeAssignmentExpression();
    }

    @Test
    public void testHtmlProcessorOnPreVisitRule() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = null;
        RuleType mockRuleType = Mockito.mock(RuleType.class);
        EffectType effectTypePermit = EffectType.PERMIT;
        TargetType mockTargetType = Mockito.mock(TargetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        anyOfList.add(new AnyOfType());

        when(mockRuleType.getCondition()).thenReturn(null);
        when(mockRuleType.getDescription()).thenReturn(null);
        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);
        when(mockRuleType.getTarget()).thenReturn(mockTargetType);
        when(mockTargetType.getAnyOf()).thenReturn(anyOfList);

        CallbackResult callbackResult = processor.onPreVisitRule(mockPolicyType, mockRuleType);
        assertNotNull(callbackResult);

        verify(mockRuleType, atLeast(1)).getCondition();
        verify(mockRuleType, atLeast(1)).getDescription();
        verify(mockRuleType, atLeast(1)).getEffect();
        verify(mockRuleType, atLeast(1)).getTarget();
        verify(mockRuleType, atLeast(1)).getAdviceExpressions();
        verify(mockTargetType, atLeast(1)).getAnyOf();

        mockPolicyType = Mockito.mock(PolicyType.class);
        when(mockRuleType.getDescription()).thenReturn("");

        callbackResult = processor.onPreVisitRule(mockPolicyType, mockRuleType);
        assertNotNull(callbackResult);
    }

    @Test
    public void testHtmlProcessorOnPostVisitRule() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = null;
        RuleType mockRuleType = Mockito.mock(RuleType.class);

        processor = new HtmlProcessor(temp, mockPolicySetType);
        CallbackResult callbackResult = processor.onPostVisitRule(mockPolicyType, mockRuleType);
        assertNotNull(callbackResult);

        mockPolicyType = Mockito.mock(PolicyType.class);
        callbackResult = processor.onPostVisitRule(mockPolicyType, mockRuleType);
        assertNotNull(callbackResult);
    }
}
