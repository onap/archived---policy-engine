/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.onap.policy.rest.util.PDPPolicyContainer;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;

import com.att.research.xacml.api.pap.PDPPolicy;

public class RemoveGroupPolicy {


    //Container from where we are fetching the policies
    private static PDPPolicyContainer policyContainer;

    private RemoveGroupPolicy self = this;
    private StdPDPGroup updatedObject;
    private final StdPDPGroup group;
    private boolean isSaved = false;

    public RemoveGroupPolicy(StdPDPGroup group) {

        this.group = group;

    }

    public void prepareToRemove(PDPPolicy policy) {

        if (this.group == null) {
            return;
        }
        setRemoveGroupPolicy(new PDPPolicyContainer(group));

        RemoveGroupPolicy.policyContainer.removeItem(policy);

        self.doSave();

        self.isSaved = true;

    }

    private static void setRemoveGroupPolicy(PDPPolicyContainer pdpPolicyContainer) {
        RemoveGroupPolicy.policyContainer = pdpPolicyContainer;
    }

    @SuppressWarnings("unchecked")
    protected void doSave() {
        if (this.group == null) {
            return;
        }

        StdPDPGroup updatedGroupObject = new StdPDPGroup(
                group.getId(),
                group.isDefaultGroup(),
                group.getName(),
                group.getDescription(),
                null);

        // replace the original set of Policies with the set from the container (possibly modified by the user)
        Set<PDPPolicy> changedPolicies = new HashSet<>();
        changedPolicies.addAll((Collection<PDPPolicy>) RemoveGroupPolicy.policyContainer.getItemIds());
        updatedGroupObject.setPolicies(changedPolicies);
        updatedGroupObject.setOnapPdps(this.group.getOnapPdps());

        // replace the original set of PIP Configs with the set from the container
        updatedGroupObject.setPipConfigs(this.group.getPipConfigs());

        // copy those things that the user cannot change from the original to the new object
        updatedGroupObject.setStatus(this.group.getStatus());

        this.updatedObject = updatedGroupObject;
    }

    public boolean isRemoved() {
        return this.isSaved;
    }

    public OnapPDPGroup getUpdatedObject() {
        return this.updatedObject;
    }

}
