/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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

package org.onap.policy.xacml.std.pap;

import java.net.URI;

/**
 * Parameters class for StdPDPPolicy
 */
public class StdPDPPolicyParams {
    private String id;
    private boolean isRoot;
    private String name;
    private URI location;
    private boolean isValid;
    private String policyId;
    private String description;
    private String version;

    /**
     * Private constructor
     */
    private StdPDPPolicyParams(){
        super();
    }

    /**
     * Get an instance of builder class
     * @return StdPDPPolicyParamsBuilder
     */
    public static StdPDPPolicyParamsBuilder builder() {
        return new StdPDPPolicyParamsBuilder();
    }

    /**
     * Return id
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Boolean to indicate root
     * @return isRoot
     */
    public boolean isRoot() {
        return isRoot;
    }

    /**
     * Get name of policy
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the uri
     * @return location
     */
    public URI getLocation() {
        return location;
    }

    /**
     * Check policy valid
     * @return isValid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Retrieve policy id
     * @return policy id
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Description of policy
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieve version of policy
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Builder class for std pdp policy params class
     */
    public static class StdPDPPolicyParamsBuilder {
        StdPDPPolicyParams m = new StdPDPPolicyParams();

        /**
         * Build the policy params
         * @return stdPdpPolicyParams object
         */
        public StdPDPPolicyParams build() {
            return m;
        }

        /**
         * Set id
         * @param id - provide id
         * @return builder
         */
        public StdPDPPolicyParamsBuilder id(String id) {
            m.id = id;
            return this;
        }

        /**
         * Set whether isRoot
         * @param isRoot - true/false
         * @return builder
         */
        public StdPDPPolicyParamsBuilder isRoot(boolean isRoot) {
            m.isRoot = isRoot;
            return this;
        }

        /**
         * Set name
         * @param name - name of policy
         * @return builder
         */
        public StdPDPPolicyParamsBuilder name(String name) {
            m.name = name;
            return this;
        }

        /**
         * Set location uri
         * @param uri - for location
         * @return builder
         */
        public StdPDPPolicyParamsBuilder location(URI uri) {
            m.location = uri;
            return this;
        }

        /**
         * Set valid flag
         * @param isValid - whether the policy is valid
         * @return builder
         */
        public StdPDPPolicyParamsBuilder isValid(boolean isValid) {
            m.isValid = isValid;
            return this;
        }

        /**
         * Set policy id
         * @param policyId - policy id
         * @return builder
         */
        public StdPDPPolicyParamsBuilder policyId(String policyId) {
            m.policyId = policyId;
            return this;
        }

        /**
         * Set description of policy
         * @param description - of policy
         * @return builder
         */
        public StdPDPPolicyParamsBuilder description(String description) {
            m.description = description;
            return this;
        }

        /**
         * Set version of policy
         * @param version - of policy
         * @return builder
         */
        public StdPDPPolicyParamsBuilder version(String version) {
            m.version = version;
            return this;
        }
    }
}
