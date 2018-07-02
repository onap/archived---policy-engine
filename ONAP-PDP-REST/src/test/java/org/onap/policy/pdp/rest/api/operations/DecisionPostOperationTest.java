/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pdp.rest.api.operations;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.PolicyDecisionException;


public class DecisionPostOperationTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * Test method for
     * {@link org.onap.policy.pdp.rest.api.operations.DecisionPostOperation#DecisionPostOperation(org.onap.policy.pdp.rest.api.operations.DecisionOperationType)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testDecisionPostOperation() {
        Map<String, Object> opData = new HashMap<>();
        opData.put(DecisionPostOperation.POSTOP_TYPE, DecisionPostOperation.PostOpType.POSTOP_SUBSTRING);
        opData.put(DecisionPostOperation.POSTOP_INPUT_STR, "1234567890");
        opData.put(DecisionPostOperation.POSTOP_SUBSTR_INDEX, "1,5");
        DecisionBaseOperation op = DecisionBaseOperation.createOperationOfType(DecisionOperationType.STRINGOP);
        try {
            op.executeOperation(opData);
        } catch (PolicyDecisionException e) {
            e.printStackTrace();
        }
        assertEquals("2345", op.getResult());

        opData.clear();
        opData.put(DecisionPostOperation.POSTOP_TYPE, DecisionPostOperation.PostOpType.POSTOP_SUBSTRING);
        opData.put(DecisionPostOperation.POSTOP_INPUT_STR, "ABCDEF");
        opData.put(DecisionPostOperation.POSTOP_SUBSTR_INDEX, "-1,3");
        try {
            op.executeOperation(opData);
        } catch (PolicyDecisionException e) {
            e.printStackTrace();
        }
        assertEquals("DEF", op.getResult());


        opData.clear();
        opData.put(DecisionPostOperation.POSTOP_TYPE, DecisionPostOperation.PostOpType.POSTOP_LOWERCASE);
        opData.put(DecisionPostOperation.POSTOP_INPUT_STR, "ABCDEF");
        try {
            op.executeOperation(opData);
        } catch (PolicyDecisionException e) {
            e.printStackTrace();
        }
        assertEquals("abcdef", op.getResult());

        opData.clear();
        opData.put(DecisionPostOperation.POSTOP_TYPE, DecisionPostOperation.PostOpType.POSTOP_UPPERCASE);
        opData.put(DecisionPostOperation.POSTOP_INPUT_STR, "abcdef");
        try {
            op.executeOperation(opData);
        } catch (PolicyDecisionException e) {
            e.printStackTrace();
        }
        assertEquals("ABCDEF", op.getResult());
    }

}
