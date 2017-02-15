/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP
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

package org.openecomp.policy.pdp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
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
 * Tests for various classes containing only one function.
 * 
 * TO RUN - use jUnit
 * In Eclipse select this file or the enclosing directory, right-click and select Run As/JUnit Test
 * 
 *
 */
public class FunctionDefinitionNumberTypeConversionTest {
	
	/*
	 * variables useful in the following tests
	 */
	List<FunctionArgument> arguments = new ArrayList<FunctionArgument>();
	
	@Test
	public void testDouble_to_integer() {
		FunctionArgumentAttributeValue attr1 = null;
		try {
			attr1 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(5.432));

		} catch (Exception e) {
			fail("creating attribute e="+ e);
		}
		
		FunctionDefinitionNumberTypeConversion<?, ?> fd = (FunctionDefinitionNumberTypeConversion<?, ?>) StdFunctions.FD_DOUBLE_TO_INTEGER;
		
		// check identity and type of the thing created
		assertEquals(XACML3.ID_FUNCTION_DOUBLE_TO_INTEGER, fd.getId());
		assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeArgs().getId());
		assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeId());
		
		// just to be safe...  If tests take too long these can probably be eliminated
		assertFalse(fd.returnsBag());
		assertEquals(new Integer(1), fd.getNumArgs());
		
		
		// test normal add
		arguments.add(attr1);
		ExpressionResult res = fd.evaluate(null, arguments);
		assertTrue(res.isOk());
		BigInteger resValue = (BigInteger)res.getValue().getValue();
		assertEquals(BigInteger.valueOf(5), resValue);
	}

	
	@Test
	public void testInteger_to_double() {
		FunctionArgumentAttributeValue attr1 = null;
		try {
			attr1 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(5));

		} catch (Exception e) {
			fail("creating attribute e="+ e);
		}
		
		FunctionDefinitionNumberTypeConversion<?, ?> fd = (FunctionDefinitionNumberTypeConversion<?, ?>) StdFunctions.FD_INTEGER_TO_DOUBLE;
		
		// check identity and type of the thing created
		assertEquals(XACML3.ID_FUNCTION_INTEGER_TO_DOUBLE, fd.getId());
		assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeArgs().getId());
		assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeId());
		
		// just to be safe...  If tests take too long these can probably be eliminated
		assertFalse(fd.returnsBag());
		assertEquals(new Integer(1), fd.getNumArgs());
		
		
		// test normal add
		arguments.add(attr1);
		ExpressionResult res = fd.evaluate(null, arguments);
		assertTrue(res.isOk());
		Double resValue = (Double)res.getValue().getValue();
		assertEquals(new Double(5.0), resValue);
	}
	
	
}
