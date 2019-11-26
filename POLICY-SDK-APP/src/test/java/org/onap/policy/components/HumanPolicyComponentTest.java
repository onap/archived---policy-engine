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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class HumanPolicyComponentTest {

    private AttributeIdentifiers attrIds;
    private HtmlProcessor processor;
    private static File temp;
    private static File tempAction;
    private static File tempConfig;

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

    @BeforeClass
    public static void setup() throws IOException {
        temp = File.createTempFile("tmpFile", ".tmp");
        tempAction = File.createTempFile("Action_test", ".tmp");
        tempConfig = File.createTempFile("Config_test", ".tmp");
        temp.deleteOnExit();
        tempAction.deleteOnExit();
        tempConfig.deleteOnExit();
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
    public void testHtmlProcessorOnPreVisitPolicySet() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = Mockito.mock(PolicySetType.class);
        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(Collections.emptyList());
        processor = new HtmlProcessor(temp, mockPolicySetType);

        CallbackResult preResult = processor.onPreVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", preResult.name());

        CallbackResult postResult = processor.onPostVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", postResult.name());
    }

    @Test
    public void testHtmlProcessorOnPreVisitPolicySetNullParent() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicySetType mockPolicyParent = null;
        JAXBElement<?> mockElement = Mockito.mock(JAXBElement.class);
        List<JAXBElement<?>> testList = new ArrayList<JAXBElement<?>>();
        testList.add(mockElement);
        when(mockPolicySetType.getPolicySetOrPolicyOrPolicySetIdReference()).thenReturn(testList);
        processor = new HtmlProcessor(temp, mockPolicySetType);

        CallbackResult res = processor.onPreVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", res.name());

        CallbackResult postResult = processor.onPostVisitPolicySet(mockPolicyParent, mockPolicySetType);
        assertEquals("CONTINUE", postResult.name());
    }

    @Test
    public void testHtmlProcessorPolicy() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        PolicyType mockPolicyType = Mockito.mock(PolicyType.class);
        when(mockPolicyType.getRuleCombiningAlgId()).thenReturn(null);
        when(mockPolicyType.getPolicyId()).thenReturn(null);
        when(mockPolicyType.getVersion()).thenReturn(null);
        when(mockPolicyType.getTarget()).thenReturn(null);

        processor = new HtmlProcessor(temp, mockPolicySetType);
        processor.policy(mockPolicyType);
        verify(mockPolicyType).getRuleCombiningAlgId();
        verify(mockPolicyType).getPolicyId();
        verify(mockPolicyType).getVersion();
        verify(mockPolicyType).getTarget();
    }

    @Test
    public void testHtmlProcessorPolicySet() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        when(mockPolicySetType.getPolicyCombiningAlgId()).thenReturn("");
        when(mockPolicySetType.getPolicySetId()).thenReturn("");
        when(mockPolicySetType.getVersion()).thenReturn("");

        processor = new HtmlProcessor(temp, mockPolicySetType);
        processor.policySet(mockPolicySetType, "");
        verify(mockPolicySetType, atLeast(1)).getPolicyCombiningAlgId();
        verify(mockPolicySetType, atLeast(1)).getPolicySetId();
        verify(mockPolicySetType, atLeast(1)).getVersion();
    }

    @Test
    public void testHtmlProcessorRule() throws IOException {
        PolicySetType mockPolicySetType = Mockito.mock(PolicySetType.class);
        RuleType mockRuleType = Mockito.mock(RuleType.class);
        ConditionType mockConditionType = Mockito.mock(ConditionType.class);
        ObligationExpressionsType mockOESType = Mockito.mock(ObligationExpressionsType.class);
        ObligationExpressionType mockOEType = Mockito.mock(ObligationExpressionType.class);
        EffectType effectTypePermit = EffectType.PERMIT;

        List<ObligationExpressionType> oblList = new ArrayList<ObligationExpressionType>();
        oblList.add(mockOEType);

        when(mockRuleType.getEffect()).thenReturn(effectTypePermit);

        when(mockRuleType.getRuleId()).thenReturn(null);
        when(mockRuleType.getTarget()).thenReturn(null);
        when(mockRuleType.getCondition()).thenReturn(mockConditionType);
        when(mockRuleType.getObligationExpressions()).thenReturn(mockOESType);
        when(mockOESType.getObligationExpression()).thenReturn(oblList);
        when(mockOEType.getFulfillOn()).thenReturn(effectTypePermit);

        processor = new HtmlProcessor(temp, mockPolicySetType);
        processor.rule(mockRuleType);

        verify(mockRuleType, atLeast(1)).getRuleId();
        verify(mockRuleType, atLeast(1)).getTarget();
        verify(mockRuleType, atLeast(1)).getCondition();
        verify(mockRuleType, atLeast(1)).getObligationExpressions();
        verify(mockOESType, atLeast(1)).getObligationExpression();
        verify(mockOEType, atLeast(1)).getFulfillOn();
    }

}
