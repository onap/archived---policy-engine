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
package org.onap.policy.xacml.pdp.std.functions;


import java.util.List;

import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.std.functions.ConvertedArgument;
import com.att.research.xacmlatt.pdp.std.functions.FunctionDefinitionBase;

/**
 * FunctionDefinitionCustomRegexMatch implements {@link com.att.research.xacmlatt.pdp.policy.FunctionDefinition} to
 * implement the custom 'type'-regex-match predicates as functions taking two arguments, the first of <code>String</code>,
 * and the second of the type for that specific predicate as a regular expression,
 * and returning a <code>Boolean</code> for whether the regular expression matches the string representation of the first argument.
 * 
 * 
 * @version $Revision: 0.1 $
 * 
 * @param <I> the java class for the data type of the function Input arguments
 */
public class FunctionDefinitionCustomRegexpMatch<I> extends FunctionDefinitionBase<Boolean, I> {

	
	/**
	 * Constructor - need dataTypeArgs input because of java Generic type-erasure during compilation.
	 * 
	 * @param idIn
	 * @param dataTypeArgsIn
	 */
	public FunctionDefinitionCustomRegexpMatch(Identifier idIn, DataType<I> dataTypeArgsIn) {
		super(idIn, DataTypes.DT_BOOLEAN, dataTypeArgsIn, false);
	}


	@Override
	public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {

		if (arguments == null || arguments.size() != 2) {
			return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() + " Expected 2 arguments, got " + 
					((arguments == null) ? "null" : arguments.size()) ));
		}
		
		// get the regular expression
		FunctionArgument regexpArgument = arguments.get(0);

		ConvertedArgument<String> convertedArgument = new ConvertedArgument<String>(regexpArgument, DataTypes.DT_STRING, false);
		if ( ! convertedArgument.isOk()) {
			return ExpressionResult.newError(getFunctionStatus(convertedArgument.getStatus()));
		}
		
		// String regexpValue = (String)regexpArgument.getValue().getValue();
		String regexpValue	= convertedArgument.getValue();

		
		// now get the element to match
		FunctionArgument elementArgument = arguments.get(1);
		
		ConvertedArgument<I> convertedElement = new ConvertedArgument<I>(elementArgument, this.getDataTypeArgs(), false);
		if ( ! convertedElement.isOk()) {
			return ExpressionResult.newError(getFunctionStatus(convertedElement.getStatus()));
		}
		
		I elementValueObject = convertedElement.getValue();

		String elementValueString;
		try {
			elementValueString = this.getDataTypeArgs().toStringValue(elementValueObject);
		} catch (DataTypeException e) {
			String message = e.getMessage();
			if (e.getCause() != null) {
				message = e.getCause().getMessage();
			}
			return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() + " " + message));
		}
		
		// ConvertedArgument checks for null value, so do not need to do again here
		
		// Adding this code will Change the Functionality which allows to retrieve Multiple-policy using single request.
		elementValueString = elementValueString + regexpValue ;
		regexpValue = elementValueString.substring(0,(elementValueString.length()- regexpValue.length()));
		elementValueString = elementValueString.substring(regexpValue.length(),(elementValueString.length()));
		// 
		
		if (elementValueString.matches(regexpValue)) {
			return ER_TRUE;
		} else {
			return ER_FALSE;
		}

	}
}
