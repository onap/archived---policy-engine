/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

package org.onap.policy.xacml.test.util;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.att.research.xacml.api.Advice;
import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.Obligation;
import com.att.research.xacml.util.XACMLPolicyScanner.Callback;
import com.att.research.xacml.util.XACMLPolicyScanner.CallbackResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.IdReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.xacml.util.XACMLPolicyScanner;

public class XACMLPolicyScannerTest {

    private static final Log logger = LogFactory.getLog(XACMLPolicyScannerTest.class);
    private static Path configPolicyPathValue;
    private static Path actionPolicyPathValue;

    /**
     * setUp.
     */
    @Before
    public void setUp() {
        File templateFile;
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            templateFile = new File(classLoader.getResource("Config_SampleTest1206.1.xml").getFile());
            configPolicyPathValue = templateFile.toPath();
            templateFile = new File(classLoader.getResource("Action_TestActionPolicy.1.xml").getFile());
            actionPolicyPathValue = templateFile.toPath();
        } catch (Exception e1) {
            logger.error("Exception Occured" + e1);
        }
    }

    @Test
    public void xacmlPolicyScannerTest() throws IOException {
        Callback callback = null;
        try {
            XACMLPolicyScanner actionScanner = new XACMLPolicyScanner(actionPolicyPathValue, callback);
            assertNotNull(actionScanner.getPolicyObject());
            Object actionObject = actionScanner.scan();
            assertNotNull(actionObject);

            XACMLPolicyScanner scanner = new XACMLPolicyScanner(configPolicyPathValue, callback);
            assertTrue(scanner.getPolicyObject() != null);
            Object object = scanner.scan();
            assertTrue(object != null);
            String id = XACMLPolicyScanner.getID(scanner.getPolicyObject());
            assertTrue(id.equals("urn:com:xacml:policy:id:0b67998b-57e2-4e25-9ea9-f9154bf18df1"));
            String version = XACMLPolicyScanner.getVersion(scanner.getPolicyObject());
            assertTrue(version.equals("1"));
            String versionFromPath = XACMLPolicyScanner.getVersion(configPolicyPathValue);
            assertTrue(versionFromPath.equals("1"));
            List<String> returnValue = XACMLPolicyScanner.getCreatedByModifiedBy(configPolicyPathValue);
            assertTrue(returnValue.get(0).equals("test"));
            String createdBy = XACMLPolicyScanner.getCreatedBy(configPolicyPathValue);
            assertTrue(createdBy.equals("test"));
            String modifiedBy = XACMLPolicyScanner.getModifiedBy(configPolicyPathValue);
            assertTrue(modifiedBy.equals("test"));
        } catch (Exception e) {
            fail();
            logger.error("Exception Occured" + e);
        }
    }

    @Test
    public void badPolicies() throws IOException {
        Path unknown = Paths.get("foobar.xml");
        XACMLPolicyScanner scanner = new XACMLPolicyScanner(unknown, null);
        assertNull(scanner.getPolicyObject());
        assertNull(scanner.scan());

        assertThatExceptionOfType(IOException.class).isThrownBy(() -> {
            XACMLPolicyScanner.getVersion(unknown);
        });

        Path logback = Paths.get("logback-test.xml");
        scanner = new XACMLPolicyScanner(logback, null);
    }

    @Test
    public void testRest() {
        PolicySetType policySet = new PolicySetType();
        policySet.setPolicySetId("id1");
        policySet.setVersion("v1");
        XACMLPolicyScanner scanner = new XACMLPolicyScanner(policySet);
        assertNotNull(scanner);
        assertEquals(policySet, scanner.getPolicyObject());
        assertEquals(policySet.getPolicySetId(), XACMLPolicyScanner.getID(policySet));
        assertEquals(policySet.getVersion(), XACMLPolicyScanner.getVersion(policySet));

        scanner.setCallback(new TestCallback(CallbackResult.CONTINUE));
        assertEquals(policySet, scanner.scan());
        assertEquals(policySet, scanner.scan(new TestCallback(CallbackResult.CONTINUE)));
        assertEquals(policySet, scanner.scan(new TestCallback(CallbackResult.STOP)));

        PolicyType policy = new PolicyType();
        policy.setPolicyId("id2");
        policy.setVersion("v2");
        scanner = new XACMLPolicyScanner(policy);
        assertNotNull(scanner);
        assertEquals(policy, scanner.getPolicyObject());
        assertEquals(policy.getPolicyId(), XACMLPolicyScanner.getID(policy));
        assertEquals(policy.getVersion(), XACMLPolicyScanner.getVersion(policy));
        policy.setVersion(null);
        assertNull(XACMLPolicyScanner.getVersion(policy));

        assertNull(XACMLPolicyScanner.getID(new String()));
        assertNull(XACMLPolicyScanner.getVersion(new String()));

    }

    @Test
    public void testPolicySet() {
        PolicySetType policySet = new PolicySetType();
        policySet.setPolicySetId("id1");
        policySet.setVersion("v1");

    }

    class TestCallback implements Callback {

        CallbackResult begin = CallbackResult.CONTINUE;

        TestCallback(CallbackResult onBegin) {
            begin = onBegin;
        }

        @Override
        public CallbackResult onBeginScan(Object root) {
            return begin;
        }

        @Override
        public void onFinishScan(Object root) {
        }

        @Override
        public CallbackResult onPreVisitPolicySet(PolicySetType parent, PolicySetType policySet) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPostVisitPolicySet(PolicySetType parent, PolicySetType policySet) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPreVisitPolicy(PolicySetType parent, PolicyType policy) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPostVisitPolicy(PolicySetType parent, PolicyType policy) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPreVisitRule(PolicyType parent, RuleType rule) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPostVisitRule(PolicyType parent, RuleType rule) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onAttribute(Object parent, Object container, Attribute attribute) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onObligation(Object parent, ObligationExpressionType expression, Obligation obligation) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onAdvice(Object parent, AdviceExpressionType expression, Advice advice) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onVariable(PolicyType policy, VariableDefinitionType variable) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onCondition(RuleType rule, ConditionType condition) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPolicySetIdReference(IdReferenceType reference, PolicySetType parent) {
            return CallbackResult.CONTINUE;
        }

        @Override
        public CallbackResult onPolicyIdReference(IdReferenceType reference, PolicySetType parent) {
            return CallbackResult.CONTINUE;
        }

    }
}
