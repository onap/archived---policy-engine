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
 * <code>RemovedPolicy</code> defines the Policy that has been removed
 *
 * @version 0.1
 */
public interface RemovedPolicy {

    /**
     * Gets the <code>String</code> format of the Policy Name that has been removed.
     *
     * @return <code>String</code> format of Policy Name
     */
    String getPolicyName();

    /**
     * Gets the <code>String</code> format of the Policy Version that has been removed.
     *
     * @return <code>String</code> format of Policy Version
     */
    String getVersionNo();
}
