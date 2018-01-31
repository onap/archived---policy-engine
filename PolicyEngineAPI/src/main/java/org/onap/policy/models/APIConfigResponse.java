/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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
package org.onap.policy.models;

import java.util.Map;

import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyType;

public interface APIConfigResponse {
    public String getPolicyConfigMessage();
    public PolicyConfigStatus getPolicyConfigStatus();
    public PolicyType getType(); // PolicyType
    public String getConfig();
    public String getPolicyName();
    public String getPolicyVersion();
    public Map<String,String> getMatchingConditions();
    public Map<String,String> getResponseAttributes();
    public Map<String,String> getProperty();
}
