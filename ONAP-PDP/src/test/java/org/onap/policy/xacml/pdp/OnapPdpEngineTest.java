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

package org.onap.policy.xacml.pdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.api.pdp.ScopeResolver;
import com.att.research.xacmlatt.pdp.eval.EvaluationContextFactory;

import org.junit.Test;
import org.mockito.Mockito;

public class OnapPdpEngineTest {
    @Test
    public void testEngine() throws PDPException {
        final EvaluationContextFactory factory = Mockito.mock(EvaluationContextFactory.class);
        final ScopeResolver resolver = Mockito.mock(ScopeResolver.class);
        final OnapPdpEngine engine = new OnapPdpEngine(factory, resolver);
        assertNotNull(engine);

        final Request pepRequest = Mockito.mock(Request.class);
        final Response response = engine.decide(pepRequest);
        assertEquals(1, response.getResults().size());
    }
}
