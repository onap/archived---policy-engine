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

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentAttributeValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FunctionDefinitionCustomRegexpMatchTest {
    @Test
    public final void testRegexp() {
        // Setup
        final String testVal = "testVal";
        final String testId = "function:testId";
        final IdentifierImpl testFnId = new IdentifierImpl(testId);
        final Identifier identifier = XACML.ID_DATATYPE_STRING;
        final StdAttributeValue<String> attValue = new StdAttributeValue<String>(identifier, testVal);
        final FunctionArgument fArg = new FunctionArgumentAttributeValue(attValue);
        final List<FunctionArgument> listFa = new ArrayList<FunctionArgument>();
        listFa.add(fArg);
        listFa.add(fArg);

        // Try a match
        final FunctionDefinitionCustomRegexpMatch<String> regexpMatch =
                new FunctionDefinitionCustomRegexpMatch<String>(testFnId, DataTypes.DT_STRING);
        final ExpressionResult result = regexpMatch.evaluate(null, listFa);
        assertEquals(result.getStatus().isOk(), true);
    }
}
