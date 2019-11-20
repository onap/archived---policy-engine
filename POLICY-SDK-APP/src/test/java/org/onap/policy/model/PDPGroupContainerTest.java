/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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
import static org.mockito.Mockito.doThrow;

import com.att.research.xacml.api.pap.PAPException;
import java.awt.Checkbox;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeListener;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;


@RunWith(MockitoJUnitRunner.class)
public class PDPGroupContainerTest {
    private PAPPolicyEngine engine = Mockito.mock(PAPPolicyEngine.class);
    private PDPGroupContainer container = new PDPGroupContainer(engine);

    private PDPGroupContainer mockContainer = Mockito.mock(PDPGroupContainer.class);
    private OnapPDPGroup mockGroup = Mockito.mock(OnapPDPGroup.class);
    private OnapPDPGroup mockNewGroup = Mockito.mock(OnapPDPGroup.class);
    private OnapPDP mockPdp = Mockito.mock(OnapPDP.class);

    private Object itemId = new Object();

    @Test
    public void testContainer() throws PAPException {
        assertEquals(container.isSupported(itemId), false);

        container.refreshGroups();
        assertEquals(container.getGroups().size(), 0);

        container.makeDefault(mockGroup);
        container.removeGroup(mockGroup, mockNewGroup);
        container.updatePDP(mockPdp);
        container.updateGroup(mockGroup);
        container.updateGroup(mockGroup, "testUserName");
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
        container.addNewPDP(id, mockNewGroup, name, description, jmxport);
        container.movePDP(mockPdp, mockNewGroup);
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
        container.removePDP(mockPdp, mockNewGroup);
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
    public void testGetIdByIndex() {
        container.getIdByIndex(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddItemAt() {
        container.addItemAt(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetItemIds() {
        container.getItemIds(0, 1);
    }

    @Test(expected = PAPException.class)
    public void testExceptionRefreshGroups() {
        doThrow(PAPException.class).when(mockContainer).refreshGroups();
        mockContainer.refreshGroups();
    }

    @Test(expected = PAPException.class)
    public void testExceptionMakeDefault() {
        doThrow(PAPException.class).when(mockContainer).makeDefault(mockGroup);
        mockContainer.makeDefault(mockGroup);
    }

    @Test(expected = Exception.class)
    public void testExceptionRemoveGroup() throws PAPException{
        doThrow(PAPException.class).when(mockContainer).removeGroup(mockGroup, mockNewGroup);
        mockContainer.removeGroup(mockGroup, mockNewGroup);
    }

    @Test(expected = PAPException.class)
    public void testExceptionRemovePDP() throws PAPException {
        doThrow(PAPException.class).when(mockContainer).removePDP(mockPdp, mockGroup);
        mockContainer.removePDP(mockPdp, mockGroup);
    }

    @Test(expected = PAPException.class)
    public void testExceptionUpdatePDP() {
        doThrow(PAPException.class).when(mockContainer).updatePDP(mockPdp);
        mockContainer.updatePDP(mockPdp);
    }

    @Test(expected = PAPException.class)
    public void testExceptionUpdateGroup() {
        doThrow(PAPException.class).when(mockContainer).updateGroup(mockGroup);
        mockContainer.updateGroup(mockGroup);
    }

    @Test(expected = PAPException.class)
    public void testExceptionUpdateGroupUser() {
        String user = "testUserName";
        doThrow(PAPException.class).when(mockContainer).updateGroup(mockGroup, user);
        mockContainer.updateGroup(mockGroup, user);
    }

    @Test(expected = PAPException.class)
    public void testExceptionMovePDP() {
        doThrow(PAPException.class).when(mockContainer).movePDP(mockPdp, mockGroup);
        mockContainer.movePDP(mockPdp, mockGroup);
    }

    @Test(expected = Exception.class)
    public void testExceptionRemoveItem() {
        doThrow(Exception.class).when(mockContainer).removeItem(itemId);
        mockContainer.removeItem(itemId);
    }

    @Test
    public void testGetType() {
        String propertyId = "Id";
        assertEquals(container.getType(propertyId), String.class);

        propertyId = "Name";
        assertEquals(container.getType(propertyId), String.class);

        propertyId = "Description";
        assertEquals(container.getType(propertyId), String.class);

        propertyId = "Default";
        assertEquals(container.getType(propertyId), Boolean.class);

        propertyId = "Status";
        assertEquals(container.getType(propertyId), String.class);

        propertyId = "PDPs";
        assertEquals(container.getType(propertyId), Set.class);

        propertyId = "Policies";
        assertEquals(container.getType(propertyId), Set.class);

        propertyId = "PIP Configurations";
        assertEquals(container.getType(propertyId), Set.class);

        propertyId = "Selected";
        assertEquals(container.getType(propertyId), Checkbox.class);
    }

}
