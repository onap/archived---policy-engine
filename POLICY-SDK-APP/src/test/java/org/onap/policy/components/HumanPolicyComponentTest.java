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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;
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
    public void testHumanPolicyComponentException() {
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
    public void testHtmlProcessorConfigPolicySetType() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(tempConfig, mockPolicySetType);
        processor.onFinishScan(mockPolicySetType);
        verify(mockPolicySetType).getVersion();
    }

    @Test
    public void testHtmlProcessorActionPolicySetType() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(tempAction, mockPolicySetType);
        processor.onFinishScan(mockPolicySetType);
        verify(mockPolicySetType).getVersion();
    }

    @Test
    public void testHtmlProcessorConfigPolicyType() {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        processor = new HtmlProcessor(tempConfig, mockPolicyType);
        verify(mockPolicyType).getVersion();
    }

    @Test
    public void testHtmlProcessorActionPolicyType() {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        processor = new HtmlProcessor(tempAction, mockPolicyType);
        assertNotNull(processor.getAttributeIdentifiersMap());
        verify(mockPolicyType).getVersion();
    }

    @Test
    public void testHtmlProcessorOnPreVisitPolicySet() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = Mockito.mock(PolicySetType.class);

        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(Collections.emptyList());
        when(mockPolicySetType.getDescription()).thenReturn(null);

        CallbackResult preResult = processor.onPreVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", preResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPreVisitPolicySetNullParent() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = null;
        JAXBElement<?> mockElement = Mockito.mock(JAXBElement.class);

        List<JAXBElement<?>> testList = new ArrayList<JAXBElement<?>>();
        testList.add(mockElement);

        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(testList);
        when(mockPolicySetType.getDescription()).thenReturn("");

        CallbackResult preResult = processor.onPreVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", preResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPostVisitPolicySet() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = Mockito.mock(PolicySetType.class);

        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(Collections.emptyList());
        when(mockPolicySetType.getDescription()).thenReturn(null);

        CallbackResult postResult = processor.onPostVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPostVisitPolicySetNullParent() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = null;
        JAXBElement<?> mockElement = Mockito.mock(JAXBElement.class);

        List<JAXBElement<?>> testList = new ArrayList<JAXBElement<?>>();
        testList.add(mockElement);

        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(testList);
        when(mockPolicySetType.getDescription()).thenReturn("");

        CallbackResult postResult = processor.onPostVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicySetType, atLeast(1)).getPolicySetOrPolicyOrPolicySetIdReference();
        verify(mockPolicySetType, atLeast(1)).getDescription();
    }

    @Test
    public void testHtmlProcessorOnPreVisitPolicy() {
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
    }

    @Test
    public void testHtmlProcessorOnPreVisitPolicyNullParent() {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        PolicySetType mockPolicyParent = null;
        List<Object> testList = new ArrayList<Object>();
        testList.add(new Object());
        processor = new HtmlProcessor(temp, mockPolicyType);

        when(mockPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()).thenReturn(testList);
        when(mockPolicyType.getDescription()).thenReturn("");

        CallbackResult preResult = processor.onPreVisitPolicy(mockPolicyParent, mockPolicyType);
        assertEquals("CONTINUE", preResult.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        verify(mockPolicyType, atLeast(1)).getDescription();

    }

    @Test
    public void testHtmlProcessorOnPostVisitPolicy() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        List<Object> testList = new ArrayList<Object>();
        processor = new HtmlProcessor(temp, mockPolicySetType);

        when(mockPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()).thenReturn(testList);

        CallbackResult postResult = processor.onPostVisitPolicy(mockPolicySetType, mockPolicyType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
    }

    @Test
    public void testHtmlProcessorOnPostVisitPolicyNullParent() {
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        PolicySetType mockPolicyParent = null;
        List<Object> testList = new ArrayList<Object>();
        testList.add(new Object());
        processor = new HtmlProcessor(temp, mockPolicyType);

        when(mockPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()).thenReturn(testList);

        CallbackResult postResult = processor.onPostVisitPolicy(mockPolicyParent, mockPolicyType);
        assertEquals("CONTINUE", postResult.name());
        verify(mockPolicyType, atLeast(1)).getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
    }

    @Test
    public void testHtmlProcessorPolicy() {
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
    public void testHtmlProcessorPolicyListEmpty() {
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
    public void testHtmlProcessorPolicyListNotEmpty() {
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
    public void testHtmlProcessorPolicyNull() {
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
    public void testHtmlProcessorPolicySet() {
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
    public void testHtmlProcessorRule() {
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

        JAXBElement<?> mockJaxBElement = Mockito.mock(JAXBElement.class);
        Object mockValueObject = Mockito.mock(Object.class);

        doReturn(mockJaxBElement).when(mockConditionType).getExpression();
        doReturn(mockValueObject).when(mockJaxBElement).getValue();

        try {
            processor.rule(mockRuleType);
            fail();
        } catch (IllegalArgumentException e) {
            verify(mockConditionType, atLeast(1)).getExpression();
            verify(mockJaxBElement, atLeast(1)).getValue();
        }
    }

    @Test
    public void testHtmlProcessorRuleNullEmptyList() {
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

    @Test
    public void testHtmlProcessorProcessAttributeAssignments() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        RuleType mockRuleType = Mockito.mock(RuleType.class);
        EffectType effectTypePermit = EffectType.PERMIT;
        ObligationExpressionsType mockOblExsType = Mockito.mock(ObligationExpressionsType.class);

        ObligationExpressionType mockOblExTypeListObj = Mockito.mock(ObligationExpressionType.class);
        List<ObligationExpressionType> oblExTypeList = new ArrayList<ObligationExpressionType>();
        oblExTypeList.add(mockOblExTypeListObj);

        AttributeAssignmentExpressionType mockattrAssignListObj = Mockito.mock(AttributeAssignmentExpressionType.class);
        List<AttributeAssignmentExpressionType> attrAssignList = new ArrayList<AttributeAssignmentExpressionType>();
        attrAssignList.add(mockattrAssignListObj);

        JAXBElement<?> jaxbElementMock = Mockito.mock(JAXBElement.class);
        AttributeValueType attrValTypeMock = Mockito.mock(AttributeValueType.class);
        AttributeDesignatorType attrDesignTypeMock = Mockito.mock(AttributeDesignatorType.class);
        AttributeSelectorType attrSelTypeMock = Mockito.mock(AttributeSelectorType.class);
        ApplyType applyTypeMock = Mockito.mock(ApplyType.class);
        Object genericObjectMock = Mockito.mock(Object.class);

        Object mockContentListObject = Mockito.mock(Object.class);
        List<Object> contentList = new ArrayList<Object>();
        contentList.add(mockContentListObject);
        contentList.add(mockContentListObject);

        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);
        when(mockRuleType.getObligationExpressions()).thenReturn(mockOblExsType);
        when(mockOblExsType.getObligationExpression()).thenReturn(oblExTypeList);
        when(mockOblExTypeListObj.getAttributeAssignmentExpression()).thenReturn(attrAssignList);
        when(mockOblExTypeListObj.getFulfillOn()).thenReturn(effectTypePermit);
        when(mockattrAssignListObj.getCategory()).thenReturn("");
        when(mockattrAssignListObj.getAttributeId()).thenReturn("");
        doReturn(jaxbElementMock).when(mockattrAssignListObj).getExpression();
        doReturn(attrValTypeMock).when(jaxbElementMock).getValue();
        when(attrValTypeMock.getContent()).thenReturn(contentList);

        processor.rule(mockRuleType);

        verify(mockRuleType, atLeast(1)).getEffect();
        verify(mockRuleType, atLeast(1)).getObligationExpressions();
        verify(mockOblExsType, atLeast(1)).getObligationExpression();
        verify(mockOblExTypeListObj, atLeast(1)).getAttributeAssignmentExpression();
        verify(mockOblExTypeListObj, atLeast(1)).getFulfillOn();
        verify(mockattrAssignListObj, atLeast(1)).getExpression();
        verify(jaxbElementMock, atLeast(1)).getValue();
        verify(attrValTypeMock, atLeast(1)).getContent();

        doReturn(attrDesignTypeMock).when(jaxbElementMock).getValue();
        processor.rule(mockRuleType);
        verify(jaxbElementMock, atLeast(1)).getValue();

        doReturn(attrSelTypeMock).when(jaxbElementMock).getValue();
        processor.rule(mockRuleType);
        verify(jaxbElementMock, atLeast(1)).getValue();

        doReturn(applyTypeMock).when(jaxbElementMock).getValue();
        processor.rule(mockRuleType);
        verify(jaxbElementMock, atLeast(1)).getValue();

        doReturn(genericObjectMock).when(jaxbElementMock).getValue();
        processor.rule(mockRuleType);
        verify(jaxbElementMock, atLeast(1)).getValue();
    }

    @Test
    public void testHtmlProcessorTarget() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        processor.target(null);

        AnyOfType mockAnyOfType = Mockito.mock(AnyOfType.class);
        List<AnyOfType> anyOfList = new ArrayList<AnyOfType>();
        anyOfList.add(mockAnyOfType);
        anyOfList.add(mockAnyOfType); // adding duplicate element

        AllOfType mockAllOfType = Mockito.mock(AllOfType.class);
        List<AllOfType> allOfTypeList = new ArrayList<AllOfType>();
        allOfTypeList.add(mockAllOfType);
        allOfTypeList.add(mockAllOfType); // adding duplicate element

        MatchType mockMatchType = Mockito.mock(MatchType.class);
        List<MatchType> matchTypeList = new ArrayList<MatchType>();
        matchTypeList.add(mockMatchType);
        matchTypeList.add(mockMatchType); // adding duplicate element

        AttributeValueType mockAttrValType = Mockito.mock(AttributeValueType.class);
        AttributeDesignatorType mockAttrDesType = Mockito.mock(AttributeDesignatorType.class);
        AttributeSelectorType mockAttrSelType = Mockito.mock(AttributeSelectorType.class);

        List<Object> contentList = new ArrayList<Object>();

        when(mockAnyOfType.getAllOf()).thenReturn(allOfTypeList);
        when(mockAllOfType.getMatch()).thenReturn(matchTypeList);
        when(mockMatchType.getAttributeValue()).thenReturn(mockAttrValType);
        when(mockMatchType.getMatchId()).thenReturn("urn:oasis:names:tc:xacml:1.0:function:string-equal");
        when(mockMatchType.getAttributeDesignator()).thenReturn(mockAttrDesType);
        when(mockAttrValType.getDataType()).thenReturn("");
        when(mockAttrValType.getContent()).thenReturn(contentList);
        when(mockAttrDesType.getCategory()).thenReturn("");
        when(mockAttrDesType.getAttributeId()).thenReturn("");
        when(mockAttrDesType.getIssuer()).thenReturn("");
        when(mockAttrDesType.getDataType()).thenReturn("");
        processor.target(anyOfList);
        verify(mockAnyOfType, atLeast(1)).getAllOf();
        verify(mockAllOfType, atLeast(1)).getMatch();
        verify(mockMatchType, atLeast(1)).getAttributeValue();
        verify(mockMatchType, atLeast(1)).getMatchId();
        verify(mockMatchType, atLeast(1)).getAttributeDesignator();
        verify(mockAttrValType, atLeast(1)).getDataType();
        verify(mockAttrValType, atLeast(1)).getContent();
        verify(mockAttrDesType, atLeast(1)).getCategory();
        verify(mockAttrDesType, atLeast(1)).getAttributeId();
        verify(mockAttrDesType, atLeast(1)).getIssuer();
        verify(mockAttrDesType, atLeast(1)).getDataType();

        when(mockMatchType.getAttributeDesignator()).thenReturn(null);
        when(mockMatchType.getAttributeSelector()).thenReturn(mockAttrSelType);
        when(mockAttrSelType.getCategory()).thenReturn("");
        when(mockAttrSelType.getContextSelectorId()).thenReturn("");
        when(mockAttrSelType.getDataType()).thenReturn("");
        processor.target(anyOfList);
        verify(mockMatchType, atLeast(1)).getAttributeDesignator();
        verify(mockMatchType, atLeast(1)).getAttributeSelector();
        verify(mockAttrSelType, atLeast(1)).getCategory();
        verify(mockAttrSelType, atLeast(1)).getContextSelectorId();
        verify(mockAttrSelType, atLeast(1)).getDataType();

        when(mockMatchType.getAttributeDesignator()).thenReturn(null);
        when(mockMatchType.getAttributeSelector()).thenReturn(null);
        processor.target(anyOfList);
        verify(mockMatchType, atLeast(1)).getAttributeDesignator();
        verify(mockMatchType, atLeast(1)).getAttributeSelector();
    }

    @Test
    public void testHtmlProcessorStringifyExpression() {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        RuleType mockRuleType = Mockito.mock(RuleType.class);
        ConditionType mockConditionType = Mockito.mock(ConditionType.class);
        JAXBElement<?> mockJAXBElement = Mockito.mock(JAXBElement.class);
        EffectType effectTypePermit = EffectType.PERMIT;
        Object mockExpressObject = Mockito.mock(Object.class);
        AttributeDesignatorType mockAttrDesType = Mockito.mock(AttributeDesignatorType.class);
        AttributeSelectorType mockAttrSelType = Mockito.mock(AttributeSelectorType.class);
        AttributeValueType mockAttrValType = Mockito.mock(AttributeValueType.class);
        VariableReferenceType mockVarRefType = Mockito.mock(VariableReferenceType.class);

        Object mockContentObject = Mockito.mock(Object.class);
        List<Object> contentList = new ArrayList<Object>();
        contentList.add(mockContentObject);

        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);
        when(mockRuleType.getCondition()).thenReturn(mockConditionType);
        doReturn(mockJAXBElement).when(mockConditionType).getExpression();
        doReturn(mockExpressObject).when(mockJAXBElement).getValue();

        try {
            processor.rule(mockRuleType);
            fail();
        } catch (IllegalArgumentException e) {
            verify(mockRuleType, atLeast(1)).getEffect();
            verify(mockRuleType, atLeast(1)).getCondition();
            verify(mockConditionType, atLeast(1)).getExpression();
            verify(mockJAXBElement, atLeast(1)).getValue();
        }

        doReturn(mockAttrDesType).when(mockJAXBElement).getValue();
        when(mockAttrDesType.getCategory()).thenReturn("");
        when(mockAttrDesType.getAttributeId()).thenReturn("");
        when(mockAttrDesType.getDataType()).thenReturn("");
        processor.rule(mockRuleType);
        verify(mockJAXBElement, atLeast(1)).getValue();
        verify(mockAttrDesType, atLeast(1)).getCategory();
        verify(mockAttrDesType, atLeast(1)).getAttributeId();
        verify(mockAttrDesType, atLeast(1)).getDataType();

        doReturn(mockAttrSelType).when(mockJAXBElement).getValue();
        when(mockAttrSelType.getPath()).thenReturn("SamplePath/text()");
        processor.rule(mockRuleType);
        verify(mockJAXBElement, atLeast(1)).getValue();
        verify(mockAttrSelType, atLeast(1)).getPath();

        when(mockAttrSelType.getPath()).thenReturn("");
        processor.rule(mockRuleType);
        verify(mockJAXBElement, atLeast(1)).getValue();
        verify(mockAttrSelType, atLeast(1)).getPath();

        doReturn(mockAttrValType).when(mockJAXBElement).getValue();
        when(mockAttrValType.getContent()).thenReturn(contentList);
        processor.rule(mockRuleType);
        verify(mockJAXBElement, atLeast(1)).getValue();
        verify(mockAttrValType, atLeast(1)).getContent();

        doReturn(mockVarRefType).when(mockJAXBElement).getValue();
        processor.rule(mockRuleType);
        verify(mockJAXBElement, atLeast(1)).getValue();
    }
}
