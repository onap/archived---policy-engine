/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 ================================================================================
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.att.research.xacml.api.pap.PAPException;
import java.awt.Checkbox;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeListener;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;

public class PDPGroupContainerTest {
    private OnapPDPGroup group = Mockito.mock(OnapPDPGroup.class);
    private OnapPDPGroup newGroup = Mockito.mock(OnapPDPGroup.class);
    private OnapPDP pdp = Mockito.mock(OnapPDP.class);
    private PAPPolicyEngine engine = Mockito.mock(PAPPolicyEngine.class);
    private PDPGroupContainer container = new PDPGroupContainer(engine);

    @Test
    public void testContainer() throws PAPException {
        Object itemId = new Object();
        assertEquals(container.isSupported(itemId), false);

        container.refreshGroups();
        assertEquals(container.getGroups().size(), 0);

        container.makeDefault(group);
        container.removeGroup(group, newGroup);
        container.updatePDP(pdp);
        container.updateGroup(group);
        container.updateGroup(group, "testUserName");
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

    @Test(expected = UnsupportedOperationException.class)
    public void testAddItem() {
        container.addItem();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddContainerProperty() {
        container.addContainerProperty(null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveContainerProperty() {
        container.removeContainerProperty(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAllItems() {
        container.removeAllItems();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddItemAfter() {
        container.addItemAfter(null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIdByIndexException() {
        container.getIdByIndex(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddItemAt() {
        container.addItemAt(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetItemIdsException() {
        container.getItemIds(0, 1);
    }

    @Test
    public void testGetType() {
        assertEquals(Boolean.class, container.getType("Default"));
        assertEquals(Checkbox.class, container.getType("Selected"));
        assertEquals(Set.class, container.getType("PDPs"));
        assertEquals(Set.class, container.getType("Policies"));
        assertEquals(Set.class, container.getType("PIP Configurations"));
        assertEquals(String.class, container.getType("Id"));
        assertEquals(String.class, container.getType("Name"));
        assertEquals(String.class, container.getType("Description"));
        assertEquals(String.class, container.getType("Status"));
    }

    @Test
    public void testContainerPAPExceptions() throws PAPException {
        doThrow(PAPException.class).when(engine).getOnapPDPGroups();
        container.refreshGroups();

        doThrow(PAPException.class).when(engine).setDefaultGroup(group);
        container.makeDefault(group);

        doThrow(PAPException.class).when(engine).updatePDP(pdp);
        container.updatePDP(pdp);

        doThrow(PAPException.class).when(engine).updateGroup(group);
        container.updateGroup(group);

        doThrow(PAPException.class).when(engine).updateGroup(group, "testUserName");
        container.updateGroup(group, "testUserName");

        doThrow(PAPException.class).when(engine).movePDP(pdp, group);
        container.movePDP(pdp, group);
    }

    @Test(expected = PAPException.class)
    public void testContainerRemoveGroup() throws PAPException {
        doThrow(PAPException.class).when(engine).removeGroup(group, newGroup);
        container.removeGroup(group, newGroup);
    }

    @Test(expected = PAPException.class)
    public void testContainerRemovePDP() throws PAPException {
        doThrow(PAPException.class).when(engine).removePDP(pdp);
        container.removePDP(pdp, group);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveDefaultGroup() throws PAPException {
        when(group.isDefaultGroup()).thenReturn(true);
        container.removeGroup(group, newGroup);
    }

    @Test
    public void testGetItemIds() {
        assertNotNull(container.getItemIds(0, 0));
    }

    @Test
    public void testContainsId() {
        assertFalse(container.containsId(group));
    }

    @Test
    public void testGroupMethods() {
        container.groups.add(group);

        Object retObj = container.getIdByIndex(0);
        assertTrue(retObj instanceof OnapPDPGroup);
        assertNotNull(retObj);

        Object retFirstItemId = container.firstItemId();
        assertTrue(retFirstItemId instanceof OnapPDPGroup);
        assertNotNull(retFirstItemId);

        Object retLastItemId = container.lastItemId();
        assertTrue(retLastItemId instanceof OnapPDPGroup);
        assertNotNull(retLastItemId);

        assertTrue(container.isFirstId(group));

        assertTrue(container.isLastId(group));
    }

    @Test
    public void testNextItemId() {
        OnapPDPGroup groupNotInList = Mockito.mock(OnapPDPGroup.class);
        Object retObj = null;

        container.groups.add(group);
        container.groups.add(newGroup);

        assertNull(container.nextItemId(groupNotInList));
        assertNull(container.nextItemId(newGroup));

        retObj = container.nextItemId(group);
        assertNotNull(retObj);
        assertTrue(retObj instanceof OnapPDPGroup);
    }

    @Test
    public void testPrevItemId() {
        OnapPDPGroup groupNotInList = Mockito.mock(OnapPDPGroup.class);
        Object retObj = null;

        container.groups.add(group);
        container.groups.add(newGroup);

        assertNull(container.prevItemId(groupNotInList));
        assertNull(container.prevItemId(group));

        retObj = container.prevItemId(newGroup);
        assertNotNull(retObj);
        assertTrue(retObj instanceof OnapPDPGroup);
    }

    @Test
    public void testRemoveNullItem() {
        OnapPDPGroup nullGroup = null;
        assertFalse(container.removeItem(nullGroup));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveDefaultItem() {
        when(group.getId()).thenReturn("Default");
        container.removeItem(group);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveItem() throws PAPException {
        assertTrue(container.removeItem(group));

        PAPPolicyEngine mockPAPPolicyEngine = Mockito.mock(PAPPolicyEngine.class);
        PDPGroupContainer groupContainer = new PDPGroupContainer(mockPAPPolicyEngine);
        when(mockPAPPolicyEngine.getDefaultGroup()).thenThrow(PAPException.class);
        assertFalse(groupContainer.removeItem(group));
    }
}
