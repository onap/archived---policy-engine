/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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

package org.onap.policy.pdp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentAttributeValue;
import com.att.research.xacmlatt.pdp.std.StdFunctions;
import com.att.research.xacmlatt.pdp.std.functions.*;

/**
 * Test of PDP Functions (See XACML core spec section A.3)
 * 
 * TO RUN - use jUnit
 * In Eclipse select this file or the enclosing directory, right-click and select Run As/JUnit Test
 * 
 *
 */
public class FunctionDefinitionStringNormalizeTest {
	
	/*
	 * variables useful in the following tests
	 */
	List<FunctionArgument> arguments = new ArrayList<>();
	
	@Test
	public void testString_normalize_space() {
		String initialString = "  First and last are whitespace 	";
		FunctionArgumentAttributeValue attr1 = null;
		try {
			attr1 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue(initialString));
		} catch (Exception e) {
			fail("creating attribute e="+ e);
		}
		
		FunctionDefinitionStringNormalize fd = (FunctionDefinitionStringNormalize) StdFunctions.FD_STRING_NORMALIZE_SPACE;
		
		// check identity and type of the thing created
		assertEquals(XACML3.ID_FUNCTION_STRING_NORMALIZE_SPACE, fd.getId());
		assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());
		assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeId());
		
		// just to be safe...  If tests take too long these can probably be eliminated
		assertFalse(fd.returnsBag());
		assertEquals(new Integer(1), fd.getNumArgs());
		
		
		// test normal add
		arguments.add(attr1);
		ExpressionResult res = fd.evaluate(null, arguments);
		assertTrue(res.isOk());
		String resValue = (String)res.getValue().getValue();
		assertEquals(initialString.length() - 4, resValue.length());
		assertTrue(initialString.trim().equals(resValue));
	}

	
	@Test
	public void testString_normalize_to_lower_case() {
		String initialString = "  First and last are whitespace 	";
		FunctionArgumentAttributeValue attr1 = null;
		try {
			attr1 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue(initialString));
		} catch (Exception e) {
			fail("creating attribute e="+ e);
		}
		
		FunctionDefinitionStringNormalize fd = (FunctionDefinitionStringNormalize) StdFunctions.FD_STRING_NORMALIZE_TO_LOWER_CASE;
		
		// check identity and type of the thing created
		assertEquals(XACML3.ID_FUNCTION_STRING_NORMALIZE_TO_LOWER_CASE, fd.getId());
		assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());
		assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeId());
		
		// just to be safe...  If tests take too long these can probably be eliminated
		assertFalse(fd.returnsBag());
		assertEquals(new Integer(1), fd.getNumArgs());
		
		
		// test normal add
		arguments.add(attr1);
		ExpressionResult res = fd.evaluate(null, arguments);
		assertTrue(res.isOk());
		String resValue = (String)res.getValue().getValue();
		assertTrue(initialString.toLowerCase().equals(resValue));
	}
	
}
