/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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

package org.onap.policy.xacml.test.std.pip.engines;

import static org.junit.Assert.assertEquals;
import java.util.Collection;
import org.junit.Test;
import org.onap.policy.xacml.std.pip.engines.OperationHistoryEngine;
import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.api.pip.PIPResponse;
import com.att.research.xacml.std.StdMutableAttribute;
import com.att.research.xacml.std.pip.StdPIPRequest;

public class OperationHistoryEngineTest {
	@Test
	public void testBaseNegativeCase() throws PIPException {
		OperationHistoryEngine engine = new OperationHistoryEngine();
		Collection<PIPRequest> required = engine.attributesRequired();
		assertEquals(required.size(), 0);
		Collection<PIPRequest> provided = engine.attributesProvided();
		assertEquals(provided.size(), 0);
		
		Attribute attribute = new StdMutableAttribute();
		PIPRequest pipRequest = new StdPIPRequest(attribute);
		PIPResponse response = engine.getAttributes(pipRequest, null);
		assertEquals(response.getStatus().isOk(), true);
	}
}
