package org.openecomp.portalsdk.workflow.services;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openecomp.portalsdk.core.MockApplicationContextTestSuite;
import org.openecomp.portalsdk.workflow.domain.WorkflowSchedule;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowScheduleServiceTest extends MockApplicationContextTestSuite {
	
	@Autowired
	WorkflowScheduleService service;
//	@Autowired
//	SchedulerFactoryBean schedulerBean;
	
	@Test
	public void testFire() {
		
		// List<WorkflowSchedule> wfs = service.findAll();
		
		final WorkflowSchedule ws = new WorkflowSchedule();
		ws.setId(999L);
		ws.setWorkflowKey("test");
		ws.setCronDetails("0 38 13 3 5 ? 2016");
		final Calendar instance = Calendar.getInstance();
		instance.add(Calendar.YEAR, 3);
		ws.setEndDateTime(instance.getTime());
		
		ws.setStartDateTime(Calendar.getInstance().getTime());
		try{
			service.saveWorkflowSchedule(ws);
			Assert.assertTrue(true);
		} catch(Exception e) {
			Assert.fail(e.getMessage());
		}
		
	}

}
