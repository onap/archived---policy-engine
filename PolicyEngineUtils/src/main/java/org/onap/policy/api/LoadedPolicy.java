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

import java.util.Map;

/**
 * <code>LoadedPolicy</code> defines the Policy that has been Loaded into the PDP.
 *
 * @version 0.2
 */
public interface LoadedPolicy {

    /**
     * Gets the <code>String</code> format of the Policy Name that has been Loaded into the PDP.
     *
     * @return <code>String</code> format of Policy Name
     */
    String getPolicyName();

    /**
     * Gets the <code>String</code> format of the Policy Version that has been Loaded into the PDP.
     *
     * @return <code>String</code> format of the Policy Version.
     */
    String getVersionNo();

    /**
     * Gets the <code>Map</code> of <code>String,String</code> format of the Matches if the policy Loaded is of Config Type.
     *
     * @return the <code>Map</code> of <code>String,String</code> format of the matches in the policy.
     */
    Map<String, String> getMatches();

    /**
     * Gets the <code>UpdateType</code> of {@link org.onap.policy.api.UpdateType} received.
     *
     * @return <code>UpdateType</code> associated with this <code>PDPNotification</code>
     */
    UpdateType getUpdateType();
}
