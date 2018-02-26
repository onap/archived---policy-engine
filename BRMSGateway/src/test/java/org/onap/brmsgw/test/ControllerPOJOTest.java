/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.brmsgw.test;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.brmsInterface.ControllerPOJO;

public class ControllerPOJOTest {
	@Test
	public void testPojo() {
		String testKey = "testKey";
		String testVal = "testVal";
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put(testKey, testVal);
		ControllerPOJO pojo = new ControllerPOJO();
		
		pojo.setName(testVal);
		assertEquals(pojo.getName(), testVal);
		pojo.setDrools(testMap);
		assertEquals(pojo.getDrools(), testMap);
		pojo.setOperation(testVal);
		assertEquals(pojo.getOperation(), testVal);
	}
}
