/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import com.google.gson.Gson;

/**
 * The Class PolicyNameType.
 */
public class PolicyNameType {

    private String policyName;

    private String policyScope;

    private String policyType;

    private String filePrefix;

    private String clientScope;

    private String fullPolicyName;


    /**
     * Instantiates a new policy name type.
     *
     * @param policyName the policy name
     * @param policyScope the policy scope
     * @param policyType the policy type
     * @param filePrefix the file prefix
     * @param clientScope the client scope
     */
    public PolicyNameType(String policyName, String policyScope, String policyType, String filePrefix,
            String clientScope) {
        super();
        this.policyName = policyName;
        this.policyScope = policyScope;
        this.policyType = policyType;
        this.filePrefix = filePrefix;
        this.clientScope = clientScope;
    }

    /**
     * Gets the policy name.
     *
     * @return the policy name
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Sets the policy name.
     *
     * @param policyName the new policy name
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * Gets the policy scope.
     *
     * @return the policy scope
     */
    public String getPolicyScope() {
        return policyScope;
    }

    /**
     * Sets the policy scope.
     *
     * @param policyScope the new policy scope
     */
    public void setPolicyScope(String policyScope) {
        this.policyScope = policyScope;
    }

    /**
     * Gets the policy type.
     *
     * @return the policy type
     */
    public String getPolicyType() {
        return policyType;
    }

    /**
     * Sets the policy type.
     *
     * @param policyType the new policy type
     */
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    /**
     * Gets the file prefix.
     *
     * @return the file prefix
     */
    public String getFilePrefix() {
        return filePrefix;
    }

    /**
     * Sets the file prefix.
     *
     * @param filePrefix the new file prefix
     */
    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    /**
     * Gets the client scope.
     *
     * @return the client scope
     */
    public String getClientScope() {
        return clientScope;
    }

    /**
     * Sets the client scope.
     *
     * @param clientScope the new client scope
     */
    public void setClientScope(String clientScope) {
        this.clientScope = clientScope;
    }

    /**
     * Gets the full policy name.
     *
     * @return the full policy name
     */
    public String getFullPolicyName() {
        return fullPolicyName;
    }


    /**
     * Sets the full policy name.
     *
     * @param fullPolicyName the new full policy name
     */
    public void setFullPolicyName(String fullPolicyName) {
        this.fullPolicyName = fullPolicyName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
