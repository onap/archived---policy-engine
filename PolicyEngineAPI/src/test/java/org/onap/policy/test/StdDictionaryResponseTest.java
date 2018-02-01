/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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
package org.onap.policy.test;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;
import org.onap.policy.std.StdDictionaryResponse;

public class StdDictionaryResponseTest {

	@Test
	public void test() {
		StdDictionaryResponse r = new StdDictionaryResponse();
		r.setResponseCode(0);
		assertEquals(0, r.getResponseCode());
		r.setResponseMessage("msg");
		assertEquals("msg", r.getResponseMessage());
		r.setDictionaryData(Collections.emptyMap());
		assertEquals(0, r.getDictionaryData().size());
		r.setDictionaryJson(null);
		assertNull(r.getDictionaryJson());
	}

}
