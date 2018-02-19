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


import java.awt.Checkbox;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.util.PolicyContainer;
import org.onap.policy.rest.util.PolicyItemSetChangeNotifier;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;

public class PDPGroupContainer extends PolicyItemSetChangeNotifier implements PolicyContainer.Indexed, PolicyContainer.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER	= FlexLogger.getLogger(PDPGroupContainer.class);
	
    /**
     * String identifier of a file's "Id" property.
     */
	private static final String PROPERTY_ID = "Id";

   /**
     * String identifier of a file's "name" property.
     */
	private static final String PROPERTY_NAME = "Name";

    /**
     * String identifier of a file's "Description" property.
     */
	private static final String PROPERTY_DESCRIPTION = "Description";

    /**
     * String identifier of a file's "Default" property.
     */
	private static final String PROPERTY_DEFAULT = "Default";
    /**
     * String identifier of a file's "Status" property.
     */
	private static final String PROPERTY_STATUS = "Status";

    /**
     * String identifier of a file's "PDPs" property.
     */
	private static final String PROPERTY_PDPS = "PDPs";

    /**
     * String identifier of a file's "Policies" property.
     */
	private static final String PROPERTY_POLICIES = "Policies";

    /**
     * String identifier of a file's "PIP Configurations" property.
     */
	private static final String PROPERTY_PIPCONFIG = "PIP Configurations";
    
    /**
     * String identifier of a file's "Selected" property.
     */
	private static final String PROPERTY_SELECTED = "Selected";

    /**
     * List of the string identifiers for the available properties.
     */
	private static Collection<String> pDPProperties;

 	private transient PAPPolicyEngine papEngine = null;
 	protected transient List<OnapPDPGroup> groups = Collections.synchronizedList(new ArrayList<OnapPDPGroup>());
 	
    public PDPGroupContainer(PAPPolicyEngine papPolicyEngine) {
		super();
		this.setContainer(this);
		//
		//
		//
		this.papEngine = papPolicyEngine;
		//
		//
		//
		this.refreshGroups();
	}
    
    public boolean isSupported(Object itemId) {
    	return itemId instanceof OnapPDPGroup;
    }
	
	public synchronized void refreshGroups() {
		synchronized(this.groups) { 
			this.groups.clear();
			try {
				this.groups.addAll(this.papEngine.getOnapPDPGroups());
			} catch (PAPException e) {
				String message = "Unable to retrieve Groups from server: " + e;
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
			}
			LOGGER.info("refreshGroups");
		}
		//
		// Notify that we have changed
		//
		this.fireItemSetChange();
	}
	
	public List<OnapPDPGroup>	getGroups() {
		return Collections.unmodifiableList(this.groups);
	}
	
	public void makeDefault(OnapPDPGroup group) {
		try {
			this.papEngine.setDefaultGroup(group);
		} catch (PAPException e) {
			String message = "Unable to set Default Group on server: " + e;
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
		}
		return;
	}
	
	public void removeGroup(OnapPDPGroup group, OnapPDPGroup newGroup) throws PAPException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("removeGroup: " + group + " new group for PDPs: " + newGroup);
		}
		if (group.isDefaultGroup()) {
			throw new UnsupportedOperationException("You can't remove the Default Group.");
		}
		try {
			this.papEngine.removeGroup(group, newGroup);
		} catch (NullPointerException | PAPException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to removeGroup " + group.getId(), e);
			throw new PAPException("Failed to remove group '" + group.getId()+ "'", e);
		}
	}
	
	public void removePDP(OnapPDP pdp, OnapPDPGroup group) throws PAPException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("removePDP: " + pdp + " from group: " + group);
		}
		try {
			this.papEngine.removePDP(pdp);
		} catch (PAPException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to removePDP " + pdp.getId(), e);
			throw new PAPException("Failed to remove pdp '" + pdp.getId()+ "'", e);
		}
	}
	
	public void updatePDP(OnapPDP pdp) {
		try {
			papEngine.updatePDP(pdp);
		} catch (PAPException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}
	
	public void updateGroup(OnapPDPGroup group) {
		try {
			papEngine.updateGroup(group);
		} catch (PAPException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}
	
	@Override
	public Collection<?> getContainerPropertyIds() {
		return pDPProperties;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<>();
		items.addAll(this.groups);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getItemIds: " + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Class<?> getType(Object propertyId) {
        if (propertyId.equals(PROPERTY_ID)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_NAME)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DEFAULT)) {
            return Boolean.class;
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_PDPS)) {
            return Set.class;
        }
        if (propertyId.equals(PROPERTY_POLICIES)) {
            return Set.class;
        }
        if (propertyId.equals(PROPERTY_PIPCONFIG)) {
            return Set.class;
        }
        if (propertyId.equals(PROPERTY_SELECTED)) {
            return Checkbox.class;
        }
        return null;
	}

	@Override
	public int size() {
		return this.groups.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("containsId: " + itemId);
		}
		if (! this.isSupported(itemId)) {
			return false;
		}
		return this.groups.contains(itemId);
	}

	@Override
	public Object addItem() {
		throw new UnsupportedOperationException("PDP Container cannot add a given item.");
	}
	
	public void addNewGroup(String name, String description) throws PAPException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("addNewGroup " + name + " " + description);
		}
		this.papEngine.newGroup(name, description);
	}
	
	public void addNewPDP(String id, OnapPDPGroup group, String name, String description, int jmxport) throws PAPException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("addNewPDP " + id + " " + name + " " + description + " " + jmxport);
		}
		this.papEngine.newPDP(id, group, name, description, jmxport);
	}
	
	public void movePDP(OnapPDP pdp, OnapPDPGroup group) {
		try {
			this.papEngine.movePDP(pdp, group);
		} catch (PAPException e) {
			String message = "Unable to move PDP to new group on server: " + e;
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
		}
		return;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) {
		throw new UnsupportedOperationException("Cannot add a container property.");
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) {
		throw new UnsupportedOperationException("Cannot remove a container property.");
	}

	@Override
	public boolean removeAllItems() {
		throw new UnsupportedOperationException("PDP Container cannot remove all items. You must have at least the Default group.");
	}

	@Override
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() == null) {
            setItemSetChangeListeners(new LinkedList<PolicyContainer.ItemSetChangeListener>());
        }
        getItemSetChangeListeners().add(listener);	
	}

	@Override
	public Object nextItemId(Object itemId) {
		if (! this.isSupported(itemId)) {
			return null;
		}
		int index = this.groups.indexOf(itemId);
		if (index == -1) {
			//
			// We don't know this group
			//
			return null;
		}
		//
		// Is it the last one?
		//
		if (index == this.groups.size() - 1) {
			//
			// Yes
			//
			return null;
		}
		//
		// Return the next one
		//
		return this.groups.get(index + 1);
	}

	@Override
	public Object prevItemId(Object itemId) {
		if (! this.isSupported(itemId)) {
			return null;
		}
		int index = this.groups.indexOf(itemId);
		if (index == -1) {
			//
			// We don't know this group
			//
			return null;
		}
		//
		// Is it the first one?
		//
		if (index == 0) {
			//
			// Yes
			//
			return null;
		}
		//
		// Return the previous one
		//
		return this.groups.get(index - 1);
	}

	@Override
	public Object firstItemId() {
		synchronized (this.groups) {
			if (!this.groups.isEmpty()) {
				return this.groups.get(0);
			}
		}
		return null;
	}

	@Override
	public Object lastItemId() {
		synchronized (this.groups) {
			if (!this.groups.isEmpty()) {
				return this.groups.get(this.groups.size() - 1);
			}
		}
		return null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		synchronized (this.groups) {
			if (!this.groups.isEmpty()) {
				return this.groups.get(0).equals(itemId);
			}
		}
		return false;
	}

	@Override
	public boolean isLastId(Object itemId) {
		synchronized (this.groups) {
			if (!this.groups.isEmpty()) {
				return this.groups.get(this.groups.size() - 1).equals(itemId);
			}
		}
		return false;
	}

	@Override
	public Object addItemAfter(Object previousItemId) {
		throw new UnsupportedOperationException("Cannot addItemAfter, there really is no real ordering.");
	}

	@Override
	public int indexOfId(Object itemId) {
		return this.groups.indexOf(itemId);
	}

	@Override
	public Object getIdByIndex(int index) {
		return this.groups.get(index);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		synchronized (this.groups) {
			int endIndex = startIndex + numberOfItems;
			if (endIndex > this.groups.size()) {
				endIndex = this.groups.size() - 1;
			}
			return this.groups.subList(startIndex, endIndex);
		}
	}

	@Override
	public Object addItemAt(int index) {
		throw new UnsupportedOperationException("Cannot addItemAt");
	}

	@Override
	public boolean removeItem(Object itemId) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("removeItem: " + itemId);
		}
		if (! this.isSupported(itemId)) {
			return false;
		}
		//
		// You cannot remove the default group
		//
		if (PROPERTY_DEFAULT.equals(((OnapPDPGroup) itemId).getId())) {
			throw new UnsupportedOperationException("You can't remove the Default Group.");
		}
		//
		// Remove PDPGroup and  move any PDP's in it into the default group
		//
		try {
			this.papEngine.removeGroup((OnapPDPGroup) itemId, this.papEngine.getDefaultGroup());
			return true;
		} catch (NullPointerException | PAPException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to remove group", e);
		}
		return false;
	}

	public class PDPGroupItem{
		private final OnapPDPGroup group;
		
		public PDPGroupItem(OnapPDPGroup itemId) {
			this.group = itemId;
		}

		public String getId() {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("getId: " + this.group);
			}
			return this.group.getId();
		}
		
		public String getName() {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("getName: " + this.group);
			}
			return this.group.getName();
		}
		
		public String getDescription() {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("getDescription: " + this.group);
			}
			return this.group.getDescription();
		}
		
		public Boolean getDefault() {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("getDefault: " + this.group);
			}
			return this.group.isDefaultGroup();
		}
		
        
        public String	getStatus() {
			return this.group.getStatus().getStatus().toString();
        }
        
        public Set<PDP>		getPDPs() {
        	return Collections.unmodifiableSet(this.group.getPdps());
        }
        
        public Set<PDPPolicy> getPolicies() {
 			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("getPolicies: " + this.group);
			}
 			return this.group.getPolicies();
        }
        
        public Set<PDPPIPConfig> getPipConfigs() {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("getPIPConfigs: " + this.group);
			}
			return this.group.getPipConfigs();
        }
	}
}
