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
package org.onap.policy.xacml.pdp;

import java.util.Properties;

import org.onap.policy.xacml.action.FindAction;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.api.pdp.ScopeResolver;
import com.att.research.xacml.std.StdMutableResponse;
import com.att.research.xacmlatt.pdp.ATTPDPEngine;
import com.att.research.xacmlatt.pdp.eval.EvaluationContextFactory;

public class ONAPPDPEngine extends ATTPDPEngine {

	public ONAPPDPEngine(EvaluationContextFactory evaluationContextFactoryIn, Decision defaultDecisionIn,
			ScopeResolver scopeResolverIn, Properties properties) {
		super(evaluationContextFactoryIn, defaultDecisionIn, scopeResolverIn, properties);
	}

	public ONAPPDPEngine(EvaluationContextFactory evaluationContextFactoryIn, Decision defaultDecisionIn,
			ScopeResolver scopeResolverIn) {
		super(evaluationContextFactoryIn, defaultDecisionIn, scopeResolverIn);
	}

	public ONAPPDPEngine(EvaluationContextFactory evaluationContextFactoryIn, ScopeResolver scopeResolverIn) {
		super(evaluationContextFactoryIn, scopeResolverIn);
	}

	@Override
	public Response decide(Request pepRequest) throws PDPException {
		Response response = super.decide(pepRequest);

		FindAction findAction = new FindAction();
		return findAction.run((StdMutableResponse) response, pepRequest);
	}


}
