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
import org.junit.Test;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentAttributeValue;

public class FunctionDefinitionCustomRegexpMatchTest {
	@Test
	public final void testRegexp() {
		// Setup
		String testVal = "testVal";
		String testId = "function:testId";
		IdentifierImpl testFnId = new IdentifierImpl(testId);
		Identifier identifier = XACML.ID_DATATYPE_STRING;
		StdAttributeValue<String> attValue = new StdAttributeValue<String>(identifier, testVal);
		FunctionArgument fArg = new FunctionArgumentAttributeValue(attValue);
		List<FunctionArgument> listFA = new ArrayList<FunctionArgument>();
		listFA.add(fArg);
		listFA.add(fArg);
		
		// Try a match
		FunctionDefinitionCustomRegexpMatch<String> regexpMatch = new FunctionDefinitionCustomRegexpMatch<String>(testFnId, DataTypes.DT_STRING);
		ExpressionResult result = regexpMatch.evaluate(null, listFA);
		assertEquals(result.getStatus().isOk(), true);
	}
}
