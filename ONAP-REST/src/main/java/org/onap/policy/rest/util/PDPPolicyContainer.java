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

package org.onap.policy.rest.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PDPPolicyContainer extends PolicyItemSetChangeNotifier implements PolicyContainer.Indexed {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER	= FlexLogger.getLogger(PDPPolicyContainer.class);

     /**
     * String identifier of a file's "Id" property.
     */
    private static final String PROPERTY_ID = "Id";

   /**
     * String identifier of a file's "name" property.
     */
    private static final String PROPERTY_NAME = "Name";

    /**
      * String identifier of a file's "name" property.
      */
    private static final String PROPERTY_VERSION = "Version";
     
    /**
     * String identifier of a file's "Description" property.
     */
    private static final String PROPERTY_DESCRIPTION = "Description";
    
    /**
     * String identifier of a file's "IsRoot" property.
     */
    private static final String PROPERTY_ISROOT = "Root";

    /**
     * List of the string identifiers for the available properties.
     */
    private static Collection<String> pDPPolicyProperties;
 
    private final transient Object data;
    private transient List<PDPPolicy> policies;
    
    @SuppressWarnings("unchecked")
    public PDPPolicyContainer(Object data) {
        super();
        this.data = data;
        if (this.data instanceof PDPGroup) {
            policies = new ArrayList<> (((PDPGroup) this.data).getPolicies());
        }
        if (this.data instanceof PDP) {
            policies = new ArrayList<> (((PDP) this.data).getPolicies());
        }
        if (this.data instanceof Set) {
            policies = new ArrayList<> ((Set<PDPPolicy>)data);
        }
        if (this.policies == null) {
            LOGGER.info("NULL policies");
            throw new NullPointerException("PDPPolicyContainer created with unexpected Object type '" + data.getClass().getName() + "'");
        }
        this.setContainer(this);
    }

    @Override
    public Object nextItemId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("nextItemId: " + itemId);
        }
        int index = this.policies.indexOf(itemId);
        if (index == -1 || ((index + 1) >= this.policies.size())) {
            return null;
        }
        return new PDPPolicyItem(this.policies.get(index + 1));
    }

    @Override
    public Object prevItemId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("prevItemId: " + itemId);
        }
        int index = this.policies.indexOf(itemId);
        if (index <= 0) {
            return null;
        }
        return new PDPPolicyItem(this.policies.get(index - 1));
    }

    @Override
    public Object firstItemId() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("firstItemId: ");
        }
        if (this.policies.isEmpty()) {
            return null;
        }
        return new PDPPolicyItem(this.policies.get(0));
    }

    @Override
    public Object lastItemId() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("lastItemid: ");
        }
        if (this.policies.isEmpty()) {
            return null;
        }
        return new PDPPolicyItem(this.policies.get(this.policies.size() - 1));
    }

    @Override
    public boolean isFirstId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isFirstId: " + itemId);
        }
        if (this.policies.isEmpty()) {
            return false;
        }
        return itemId.equals(this.policies.get(0));
    }

    @Override
    public boolean isLastId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isLastId: " + itemId);
        }
        if (this.policies.isEmpty()) {
            return false;
        }
        return itemId.equals(this.policies.get(this.policies.size() - 1));
    }

    @Override
    public Object addItemAfter(Object previousItemId) {
        return null;
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return pDPPolicyProperties;
    }

    @Override
    public Collection<?> getItemIds() {
        final Collection<Object> items = new ArrayList<>();
        items.addAll(this.policies);
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
        if (propertyId.equals(PROPERTY_VERSION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ISROOT)) {
            return Boolean.class;
        }
        return null;
    }

    @Override
    public int size() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("size: " + this.policies.size());
        }
        return this.policies.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("containsId: " + itemId);
        }
        return this.policies.contains(itemId);
    }

    @Override
    public Object addItem() {
        throw new UnsupportedOperationException("Cannot add an empty policy.");
    }

    @Override
    public boolean removeItem(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("removeItem: " + itemId);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StdPDPPolicy pdpPolicy = null;
        try {
            pdpPolicy = mapper.readValue(itemId.toString() , StdPDPPolicy.class);
            for(int i = 0; i< policies.size(); i++){
                if(policies.get(i).getId().equalsIgnoreCase(pdpPolicy.getId())){
                    return this.policies.remove(this.policies.get(i));
                }
            }
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Mapping the Removing Policy from PDP Group to Std Policy"+e);
        }
        return this.policies.remove(itemId);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) {
        return false;
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) {
        return false;
    }

    @Override
    public boolean removeAllItems() {
        return false;
    }

    @Override
    public int indexOfId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("indexOfId: " + itemId);
        }
        return this.policies.indexOf(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getIdByIndex: " + index);
        }
        return this.policies.get(index);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getItemIds: " + startIndex + " " + numberOfItems);
        }
        if (numberOfItems < 0) {
            throw new IllegalArgumentException();
        }
        return this.policies.subList(startIndex, startIndex + numberOfItems);
    }

    @Override
    public Object addItemAt(int index) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("addItemAt: " + index);
        }
        return null;
    }

    public class PDPPolicyItem {
        private final PDPPolicy policy;

        public PDPPolicyItem(PDPPolicy itemId) {
            this.policy = itemId;
        }

        public String getId() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getId: " + this.policy);
            }
            return this.policy.getId();
        }

        public String getName() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getName: " + this.policy);
            }
            return this.policy.getName();
        }

        public String getVersion() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getVersion: " + this.policy);
            }
            return this.policy.getVersion();
        }

        public String getDescription() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getDescription: " + this.policy);
            }
            return this.policy.getDescription();
        }

        public boolean getRoot() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("isRoot: " + this.policy);
            }
            return this.policy.isRoot();
        }

        public void setRoot(Boolean root) {
            ((StdPDPPolicy)this.policy).setRoot(root);
        }

    }
}