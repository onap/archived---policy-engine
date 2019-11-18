/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2019 Nordix Foundation.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.rest.util;

import java.util.Collection;

public class DummyPolicyContainer implements PolicyContainer {
    private static final long serialVersionUID = 1L;

    @Override
    public Collection<?> getContainerPropertyIds() {
        return null;
    }

    @Override
    public Collection<?> getItemIds() {
        return null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean containsId(Object itemId) {
        return false;
    }

    @Override
    public Object addItem() {
        return null;
    }

    @Override
    public boolean removeItem(Object itemId) {
        return false;
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) {
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
}
