/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

public class PdpPolicyContainer extends PolicyItemSetChangeNotifier implements PolicyContainer.Indexed {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = FlexLogger.getLogger(PdpPolicyContainer.class);

    private static final String PROPERTY_ID = "Id";
    private static final String PROPERTY_NAME = "Name";
    private static final String PROPERTY_VERSION = "Version";
    private static final String PROPERTY_DESCRIPTION = "Description";
    private static final String PROPERTY_ISROOT = "Root";

    /**
     * List of the string identifiers for the available properties.
     */
    private static Collection<String> pDPPolicyProperties;

    private final transient Object data;
    private transient List<PDPPolicy> policies;

    /**
     * Instantiates a new pdp policy container.
     *
     * @param data the data
     */
    @SuppressWarnings("unchecked")
    public PdpPolicyContainer(Object data) {
        super();
        this.data = data;
        if (this.data instanceof PDPGroup) {
            policies = new ArrayList<>(((PDPGroup) this.data).getPolicies());
        }
        if (this.data instanceof PDP) {
            policies = new ArrayList<>(((PDP) this.data).getPolicies());
        }
        if (this.data instanceof Set) {
            policies = new ArrayList<>((Set<PDPPolicy>) data);
        }
        if (this.policies == null) {
            LOGGER.info("NULL policies");
            throw new NullPointerException("PDPPolicyContainer created with unexpected Object type '"
                            + data.getClass().getName() + "'");
        }
        this.setContainer(this);
    }

    /**
     * Next item id.
     *
     * @param itemId the item id
     * @return the object
     */
    @Override
    public Object nextItemId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("nextItemId: " + itemId);
        }
        int index = this.policies.indexOf(itemId);
        if (index == -1 || ((index + 1) >= this.policies.size())) {
            return null;
        }
        return new PdpPolicyItem(this.policies.get(index + 1));
    }

    /**
     * Prev item id.
     *
     * @param itemId the item id
     * @return the object
     */
    @Override
    public Object prevItemId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("prevItemId: " + itemId);
        }
        int index = this.policies.indexOf(itemId);
        if (index <= 0) {
            return null;
        }
        return new PdpPolicyItem(this.policies.get(index - 1));
    }

    /**
     * First item id.
     *
     * @return the object
     */
    @Override
    public Object firstItemId() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("firstItemId: ");
        }
        if (this.policies.isEmpty()) {
            return null;
        }
        return new PdpPolicyItem(this.policies.get(0));
    }

    /**
     * Last item id.
     *
     * @return the object
     */
    @Override
    public Object lastItemId() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("lastItemid: ");
        }
        if (this.policies.isEmpty()) {
            return null;
        }
        return new PdpPolicyItem(this.policies.get(this.policies.size() - 1));
    }

    /**
     * Checks if is first id.
     *
     * @param itemId the item id
     * @return true, if is first id
     */
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

    /**
     * Checks if is last id.
     *
     * @param itemId the item id
     * @return true, if is last id
     */
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

    /**
     * Adds the item after.
     *
     * @param previousItemId the previous item id
     * @return the object
     */
    @Override
    public Object addItemAfter(Object previousItemId) {
        return null;
    }

    /**
     * Gets the container property ids.
     *
     * @return the container property ids
     */
    @Override
    public Collection<?> getContainerPropertyIds() {
        return pDPPolicyProperties;
    }

    /**
     * Gets the item ids.
     *
     * @return the item ids
     */
    @Override
    public Collection<?> getItemIds() {
        final Collection<Object> items = new ArrayList<>();
        items.addAll(this.policies);
        return Collections.unmodifiableCollection(items);
    }

    /**
     * Gets the item ids.
     *
     * @param startIndex the start index
     * @param numberOfItems the number of items
     * @return the item ids
     */
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

    /**
     * Gets the type.
     *
     * @param propertyId the property id
     * @return the type
     */
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

    /**
     * Size.
     *
     * @return the int
     */
    @Override
    public int size() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("size: " + this.policies.size());
        }
        return this.policies.size();
    }

    /**
     * Contains id.
     *
     * @param itemId the item id
     * @return true, if successful
     */
    @Override
    public boolean containsId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("containsId: " + itemId);
        }
        return this.policies.contains(itemId);
    }

    /**
     * Adds the item.
     *
     * @return the object
     */
    @Override
    public Object addItem() {
        throw new UnsupportedOperationException("Cannot add an empty policy.");
    }

    /**
     * Removes the item.
     *
     * @param itemId the item id
     * @return true, if successful
     */
    @Override
    public boolean removeItem(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("removeItem: " + itemId);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StdPDPPolicy pdpPolicy = null;
        try {
            pdpPolicy = mapper.readValue(itemId.toString(), StdPDPPolicy.class);
            for (int i = 0; i < policies.size(); i++) {
                if (policies.get(i).getId().equalsIgnoreCase(pdpPolicy.getId())) {
                    return this.policies.remove(this.policies.get(i));
                }
            }
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Exception Occured While Mapping the Removing Policy from PDP Group to Std Policy" + e);
        }
        return this.policies.remove(itemId);
    }

    /**
     * Adds the container property.
     *
     * @param propertyId the property id
     * @param type the type
     * @param defaultValue the default value
     * @return true, if successful
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) {
        return false;
    }

    /**
     * Removes the container property.
     *
     * @param propertyId the property id
     * @return true, if successful
     */
    @Override
    public boolean removeContainerProperty(Object propertyId) {
        return false;
    }

    /**
     * Removes the all items.
     *
     * @return true, if successful
     */
    @Override
    public boolean removeAllItems() {
        return false;
    }

    /**
     * Index of id.
     *
     * @param itemId the item id
     * @return the int
     */
    @Override
    public int indexOfId(Object itemId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("indexOfId: " + itemId);
        }
        return this.policies.indexOf(itemId);
    }

    /**
     * Gets the id by index.
     *
     * @param index the index
     * @return the id by index
     */
    @Override
    public Object getIdByIndex(int index) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getIdByIndex: " + index);
        }
        return this.policies.get(index);
    }

    /**
     * Adds the item at.
     *
     * @param index the index
     * @return the object
     */
    @Override
    public Object addItemAt(int index) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("addItemAt: " + index);
        }
        return null;
    }

    public class PdpPolicyItem {
        private final PDPPolicy policy;

        /**
         * Instantiates a new PDP policy item.
         *
         * @param itemId the item id
         */
        public PdpPolicyItem(PDPPolicy itemId) {
            this.policy = itemId;
        }

        /**
         * Gets the id.
         *
         * @return the id
         */
        public String getId() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getId: " + this.policy);
            }
            return this.policy.getId();
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getName: " + this.policy);
            }
            return this.policy.getName();
        }

        /**
         * Gets the version.
         *
         * @return the version
         */
        public String getVersion() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getVersion: " + this.policy);
            }
            return this.policy.getVersion();
        }

        /**
         * Gets the description.
         *
         * @return the description
         */
        public String getDescription() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("getDescription: " + this.policy);
            }
            return this.policy.getDescription();
        }

        /**
         * Gets the root.
         *
         * @return the root
         */
        public boolean getRoot() {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("isRoot: " + this.policy);
            }
            return this.policy.isRoot();
        }

        /**
         * Sets the root.
         *
         * @param root the new root
         */
        public void setRoot(Boolean root) {
            ((StdPDPPolicy) this.policy).setRoot(root);
        }

    }
}