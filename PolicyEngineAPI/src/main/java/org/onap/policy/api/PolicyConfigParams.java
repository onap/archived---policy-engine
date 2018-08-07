/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
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
import java.util.UUID;

/**
 * Policy config parameters representing the request object
 */
public class PolicyConfigParams {
    //policyConfigType the {@link PolicyConfigType} Enum format of the Config Type
    private PolicyConfigType policyConfigType;

    //policyName the <code>String</code> format of the Policy Name
    private String policyName;

    //policyDescription the <code>String</code> format of the Policy Description
    private String policyDescription;

    //onapName the <code>String</code> format of the ONAP Name
    private String onapName;

    //configName the <code>String</code> format of the Config Name
    private String configName;

    //attributes the <code>Map</code> Attributes that must contain the AttributeType and Map of key,
    // value pairs corresponding to it.
    private Map<AttributeType, Map<String, String>> attributes;

    //configBodyType the {@link PolicyType} Enum format of the config Body Type.
    private PolicyType configBodyType;

    //configBody the <code>String</code> format of the Policy Body
    private String configBody;

    //requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
    private UUID requestID;

    private PolicyConfigParams() {
    }

    PolicyConfigType getPolicyConfigType() {
        return policyConfigType;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public String getOnapName() {
        return onapName;
    }

    public String getConfigName() {
        return configName;
    }

    public Map<AttributeType, Map<String, String>> getAttributes() {
        return attributes;
    }

    PolicyType getConfigBodyType() {
        return configBodyType;
    }

    public String getConfigBody() {
        return configBody;
    }

    public UUID getRequestID() {
        return requestID;
    }

    public static PolicyConfigParamsBuilder builder() {
        return new PolicyConfigParamsBuilder();
    }

    /**
     * Builder class for policy config parameters
     */
    public static class PolicyConfigParamsBuilder {
        PolicyConfigParams m = new PolicyConfigParams();

        private PolicyConfigParamsBuilder() {

        }

        public PolicyConfigParams build() {
            return m;
        }

        public PolicyConfigParamsBuilder policyConfigType(PolicyConfigType policyConfigType) {
            m.policyConfigType = policyConfigType;
            return this;
        }

        public PolicyConfigParamsBuilder policyName(String policyName) {
            m.policyName = policyName;
            return this;
        }

        public PolicyConfigParamsBuilder policyDescription(String policyDescription) {
            m.policyDescription = policyDescription;
            return this;
        }

        public PolicyConfigParamsBuilder onapName(String onapName) {
            m.onapName = onapName;
            return this;
        }

        public PolicyConfigParamsBuilder configName(String configName) {
            m.configName = configName;
            return this;
        }

        public PolicyConfigParamsBuilder attributes(Map<AttributeType, Map<String, String>> attributes) {
            m.attributes = attributes;
            return this;
        }

        public PolicyConfigParamsBuilder configBodyType(PolicyType configBodyType) {
            m.configBodyType = configBodyType;
            return this;
        }

        public PolicyConfigParamsBuilder configBody(String configBody) {
            m.configBody = configBody;
            return this;
        }

        public PolicyConfigParamsBuilder requestID(UUID requestID) {
            m.requestID = requestID;
            return this;
        }
    }
}
