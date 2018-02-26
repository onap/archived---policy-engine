package org.onap.policy.pdp.rest.notifications;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RemovedTest {
	@Test
	public void testRemoved() {
		String testVal = "testVal";
		
		Removed removed = new Removed();
		removed.setVersionNo(testVal);
		assertEquals(removed.getVersionNo(), testVal);
		removed.setPolicyName(testVal);
		assertEquals(removed.getPolicyName(), testVal);
	}
}
