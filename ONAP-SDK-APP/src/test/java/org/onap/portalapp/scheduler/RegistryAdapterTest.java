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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.portalsdk.core.scheduler.Registerable;
import org.onap.portalsdk.workflow.services.WorkflowScheduleService;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class RegistryAdapterTest {
	@Rule 
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testRegistryAdapter() {
		RegistryAdapter adapter = new RegistryAdapter();
		SchedulerFactoryBean schedulerBean = new SchedulerFactoryBean();
		Registerable registry = null;
		WorkflowScheduleService workflowScheduleService = null;

		adapter.setSchedulerBean(schedulerBean);
		assertEquals(adapter.getSchedulerBean(), schedulerBean);
		adapter.setRegistry(registry);
		assertEquals(adapter.getRegistry(), registry);
		adapter.setWorkflowScheduleService(workflowScheduleService);
		assertEquals(adapter.getWorkflowScheduleService(), workflowScheduleService);
	}
	
	@Test
	public void testRegistryAdapterNegCase1() {
		thrown.expect(NullPointerException.class);

		RegistryAdapter adapter = new RegistryAdapter();
		adapter.getTriggers();
		fail("Expecting an exception.");
	}
	
	@Test
	public void testRegistryAdapterNegCase2() {
		thrown.expect(NullPointerException.class);

		RegistryAdapter adapter = new RegistryAdapter();
		adapter.addCoreTriggers();
		fail("Expecting an exception.");
	}
}
