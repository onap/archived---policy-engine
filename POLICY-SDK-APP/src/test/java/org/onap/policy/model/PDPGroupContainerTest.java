/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeListener;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import com.att.research.xacml.api.pap.PAPException;

public class PDPGroupContainerTest {
	private PAPPolicyEngine engine = Mockito.mock(PAPPolicyEngine.class);
	private PDPGroupContainer container = new PDPGroupContainer(engine);
	
	@Test
	public void testContainer() throws PAPException {
		Object itemId = new Object();
		assertEquals(container.isSupported(itemId), false);
		
		container.refreshGroups();
		assertEquals(container.getGroups().size(), 0);
		
		OnapPDPGroup group = Mockito.mock(OnapPDPGroup.class);
		container.makeDefault(group);
		OnapPDPGroup newGroup = Mockito.mock(OnapPDPGroup.class);
		container.removeGroup(group, newGroup);
		OnapPDP pdp = Mockito.mock(OnapPDP.class);
		container.updatePDP(pdp);
		container.updateGroup(group);
		assertNull(container.getContainerPropertyIds());
		assertEquals(container.getItemIds().size(), 0);
		assertEquals(container.getType(itemId), null);
		assertEquals(container.size(), 0);
		assertEquals(container.containsId(itemId), false);
		String name = "testName";
		String description = "testDescription";
		container.addNewGroup(name, description);
		String id = "testID";
		int jmxport = 0;
		container.addNewPDP(id, newGroup, name, description, jmxport);
		container.movePDP(pdp, newGroup);
		ItemSetChangeListener listener = null;
		container.addItemSetChangeListener(listener);
		container.nextItemId(itemId);
		container.prevItemId(itemId);
		container.firstItemId();
		container.lastItemId();
		container.isFirstId(itemId);
		container.isLastId(itemId);
		container.indexOfId(itemId);
		assertEquals(container.getItemIds().size(), 0);
		container.removeItem(itemId);
		container.removePDP(pdp, newGroup);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testAddItem() {
		container.addItem();		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testAddContainerProperty() {
		container.addContainerProperty(null, null, null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveContainerProperty() {
		container.removeContainerProperty(null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveAllItems() {
		container.removeAllItems();
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testAddItemAfter() {
		container.addItemAfter(null);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetIdByIndex() {
		container.getIdByIndex(0);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testAddItemAt() {
		container.addItemAt(0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetItemIds() {
		container.getItemIds(0, 1);
	}
}
