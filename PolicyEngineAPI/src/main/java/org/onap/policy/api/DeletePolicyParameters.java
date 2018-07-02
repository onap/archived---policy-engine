/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.api;

import java.util.UUID;

import com.google.gson.Gson;

/**
 * <code>PushPolicyParameters</code> defines the Policy Parameters which are required to Push a
 * Policy to PDPGroup.
 * 
 * @version 0.1
 */
public class DeletePolicyParameters {

    private String policyName;
    private String policyType;
    private String policyComponent;
    private DeletePolicyCondition deleteCondition;
    private String pdpGroup;
    private UUID requestID;

    /**
     * @return the policyName
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * @param policyName the policyName to set
     */
    public void setPolicyName(final String policyName) {
        this.policyName = policyName;
    }

    /**
     * @return the policyComponent
     */
    public String getPolicyComponent() {
        return policyComponent;
    }

    /**
     * @return the policyType
     */
    public String getPolicyType() {
        return policyType;
    }

    /**
     * @param policyType the policyType to set
     */
    public void setPolicyType(final String policyType) {
        this.policyType = policyType;
    }

    /**
     * @param policyComponent the policyComponent to set
     */
    public void setPolicyComponent(final String policyComponent) {
        this.policyComponent = policyComponent;
    }

    /**
     * @return the deleteCondition
     */
    public DeletePolicyCondition getDeleteCondition() {
        return deleteCondition;
    }

    /**
     * @param deleteCondition the deleteCondition to set
     */
    public void setDeleteCondition(final DeletePolicyCondition deleteCondition) {
        this.deleteCondition = deleteCondition;
    }

    /**
     * @return the requestID
     */
    public UUID getRequestID() {
        return requestID;
    }

    /**
     * @param requestID the requestID to set
     */
    public void setRequestID(final UUID requestID) {
        this.requestID = requestID;
    }

    /**
     * @return the pdpGroup
     */
    public String getPdpGroup() {
        return pdpGroup;
    }

    /**
     * @param pdpGroup the pdpGroup to set
     */
    public void setPdpGroup(final String pdpGroup) {
        this.pdpGroup = pdpGroup;
    }

    /**
     * Used to print the input Params for REST call. 
     * 
     * @return JSON String of this object. 
     */
    @Override
    public String toString() {
	return new Gson().toJson(this);	
    }
}
