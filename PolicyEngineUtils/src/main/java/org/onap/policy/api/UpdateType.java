/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.api;

/**
 * Enumeration of the Update Type that has occurred in the <code>UpdatedPolicy</code> of
 * {@link org.onap.policy.api.LoadedPolicy}
 *
 * @version 0.1
 */
public enum UpdateType {
    /**
     * Indicates that a policy has been updated
     */
    UPDATE("update"),
    /**
     * Indicates that a policy is new
     */
    NEW("new");

    private String name;

    UpdateType(String name) {
        this.name = name;
    }

    /**
     * Returns the <code>String</code> format of the Type for this <code>UpdateType</code>
     * @return the <code>String</code> Type of <code>UpdateType</code>
     */
    @Override
    public String toString() {
        return this.name;
    }
}
