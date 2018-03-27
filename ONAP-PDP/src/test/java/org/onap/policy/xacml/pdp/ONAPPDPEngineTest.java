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
import org.junit.Test;
import org.mockito.Mockito;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.api.pdp.ScopeResolver;
import com.att.research.xacmlatt.pdp.eval.EvaluationContextFactory;

public class ONAPPDPEngineTest {
  @Test
  public void testEngine() throws PDPException {
    EvaluationContextFactory factory = Mockito.mock(EvaluationContextFactory.class);
    ScopeResolver resolver = Mockito.mock(ScopeResolver.class);
    ONAPPDPEngine engine = new ONAPPDPEngine(factory, resolver);
    assertNotNull(engine);

    Request pepRequest = Mockito.mock(Request.class);
    Response response = engine.decide(pepRequest);
    assertEquals(1, response.getResults().size());
  }
}
