/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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

package org.onap.policy.xacml.pdp.std.functions;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentAttributeValue;

import org.junit.Test;

public class FunctionDefinitionCustomRegexpMatchTest {
    @Test
    public final void testRegexp() {
        // Setup
        final String testVal = "testVal,testVal2";
        final String testId = "function:testId";
        final IdentifierImpl testFnId = new IdentifierImpl(testId);
        final Identifier identifier = XACML.ID_DATATYPE_STRING;
        final StdAttributeValue<String> attValue = new StdAttributeValue<String>(identifier, testVal);
        final FunctionArgument fArg = new FunctionArgumentAttributeValue(attValue);
        final List<FunctionArgument> listFa = new ArrayList<FunctionArgument>();
        listFa.add(fArg);
        listFa.add(fArg);
        final FunctionDefinitionCustomRegexpMatch<String> regexpMatch =
                new FunctionDefinitionCustomRegexpMatch<String>(testFnId, DataTypes.DT_STRING);

        // Try a match
        final ExpressionResult result = regexpMatch.evaluate(null, listFa);
        assertEquals(true, result.getValue().getValue());

        // Try error case 1
        assertEquals(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                regexpMatch.evaluate(null, null).getStatus().getStatusCode());

        // Try error case 2
        final Identifier identifier2 = XACML.ID_DATATYPE_BOOLEAN;
        final StdAttributeValue<String> attValue2 = new StdAttributeValue<String>(identifier2, testVal);
        final FunctionArgument fArg2 = new FunctionArgumentAttributeValue(attValue2);
        final List<FunctionArgument> listFa2 = new ArrayList<FunctionArgument>();
        listFa2.add(fArg2);
        listFa2.add(fArg2);
        assertEquals(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                regexpMatch.evaluate(null, listFa2).getStatus().getStatusCode());

        // Try error case 3
        final List<FunctionArgument> listFa3 = new ArrayList<FunctionArgument>();
        listFa3.add(fArg);
        listFa3.add(fArg2);
        assertEquals(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                regexpMatch.evaluate(null, listFa3).getStatus().getStatusCode());

        // Try a mismatch
        final String testVal4 = "testVal3";
        final StdAttributeValue<String> attValue4 = new StdAttributeValue<String>(identifier, testVal4);
        final FunctionArgument fArg4 = new FunctionArgumentAttributeValue(attValue4);
        final List<FunctionArgument> listFa4 = new ArrayList<FunctionArgument>();
        listFa4.add(fArg);
        listFa4.add(fArg4);
        assertEquals(false, regexpMatch.evaluate(null, listFa4).getValue().getValue());

        // Try a comma match
        final String testVal5 = "testVal2";
        final StdAttributeValue<String> attValue5 = new StdAttributeValue<String>(identifier, testVal5);
        final FunctionArgument fArg5 = new FunctionArgumentAttributeValue(attValue5);
        final List<FunctionArgument> listFa5 = new ArrayList<FunctionArgument>();
        listFa5.add(fArg);
        listFa5.add(fArg5);
        assertEquals(true, regexpMatch.evaluate(null, listFa5).getValue().getValue());
    }
}
