/*-
 * ================================================================================
 * ONAP Portal SDK
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property
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
 * ================================================================================
 */

package org.onap.portalapp.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class RegisterTest {
	@Rule 
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testRegister() {
		Register register = new Register();
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		Trigger trigger = triggerBuilder.build();
		List<Trigger> triggers = new ArrayList<Trigger>();
		triggers.add(trigger);
		
		//register.registerTriggers();
		register.setScheduleTriggers(triggers);
		assertEquals(register.getScheduleTriggers(), triggers);
		System.out.println(register.getTriggers().length);
	}
	
	@Test
	public void testRegisterNegativeCase() {
		thrown.expect(NullPointerException.class);
		Register register = new Register();
		register.registerTriggers();
		fail("Expecting an exception.");
	}
}
