package org.onap.policy.pap.xacml.rest.model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import com.att.research.xacml.api.pap.PDPPolicy;

public class RemoveGroupPolicyTest {
	@Test
	public void testRemove() {
		// Test constructor
		StdPDPGroup group = new StdPDPGroup();
		RemoveGroupPolicy remove = new RemoveGroupPolicy(group);
		assertEquals(remove.isRemoved(), false);
		assertEquals(remove.getUpdatedObject(), null);
		
		// Test remove
		PDPPolicy policy = new StdPDPPolicy();
		remove.prepareToRemove(policy);
		remove.doSave();
		assertEquals(remove.isRemoved(), true);
	}
}
