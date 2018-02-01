/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.rest.adapter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ClosedLoopFaultTrapDatasTest {

	@Test
	public void ClosedLoopFaultTrapDatas(){
		List<Object> trap = new ArrayList<>();
		trap.add("Test");
		ClosedLoopFaultTrapDatas closedLoopTrapData = new ClosedLoopFaultTrapDatas();
		closedLoopTrapData.setTrap1(trap);
		assertTrue("Test".equals(closedLoopTrapData.getTrap1().get(0)));
		closedLoopTrapData.setTrap2(trap);
		assertTrue("Test".equals(closedLoopTrapData.getTrap2().get(0)));
		closedLoopTrapData.setTrap3(trap);
		assertTrue("Test".equals(closedLoopTrapData.getTrap3().get(0)));
		closedLoopTrapData.setTrap4(trap);
		assertTrue("Test".equals(closedLoopTrapData.getTrap4().get(0)));
		closedLoopTrapData.setTrap5(trap);
		assertTrue("Test".equals(closedLoopTrapData.getTrap5().get(0)));
		closedLoopTrapData.setTrap6(trap);
		assertTrue("Test".equals(closedLoopTrapData.getTrap6().get(0)));
		
	}
}
