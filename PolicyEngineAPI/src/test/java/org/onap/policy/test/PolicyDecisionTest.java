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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.api.PolicyDecision;

/**
 * The class <code>PolicyDecisionTest</code> contains tests for the class
 * <code>{@link PolicyDecision}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class PolicyDecisionTest {

    @Test
    public void testCreate_EnumName_PolicyDecisionEnum() {
        for (final PolicyDecision policyDecision : PolicyDecision.values()) {
            final PolicyDecision actualPolicyDecision = PolicyDecision.create(policyDecision.name());
            assertEquals(policyDecision, actualPolicyDecision);
            assertEquals(policyDecision.toString(), actualPolicyDecision.toString());
        }
    }

    @Test
    public void testCreate_StringValue_PolicyDecisionEnum() {
        for (final PolicyDecision policyDecision : PolicyDecision.values()) {
            final PolicyDecision autalPolicyDecision = PolicyDecision.create(policyDecision.toString());
            assertEquals(policyDecision, autalPolicyDecision);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        PolicyDecision.create("foobar");
    }

    /**
     * Launch the test.
     *
     * @param args
     *            the command line arguments
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    public static void main(final String[] args) {
        new org.junit.runner.JUnitCore().run(PolicyDecisionTest.class);
    }
}
