/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.xacml.api.pap;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;

import java.io.InputStream;
import java.util.Set;

public interface PAPPolicyEngine {

    public OnapPDPGroup getDefaultGroup() throws PAPException;

    public void setDefaultGroup(OnapPDPGroup group) throws PAPException;

    public void newPDP(String id, OnapPDPGroup group, String name, String description, int jmxport) throws PAPException;

    public void newGroup(String name, String description) throws PAPException;

    public OnapPDPGroup getGroup(String id) throws PAPException;

    public Set<OnapPDPGroup> getOnapPDPGroups() throws PAPException;

    public OnapPDPGroup getPDPGroup(OnapPDP pdp) throws PAPException;

    public PDPStatus getStatus(OnapPDP pdp) throws PAPException;

    public void movePDP(OnapPDP pdp, OnapPDPGroup newGroup) throws PAPException;

    public void updatePDP(OnapPDP pdp) throws PAPException;

    public void removePDP(OnapPDP pdp) throws PAPException;

    public OnapPDP getPDP(String pdpId) throws PAPException;

    public void updateGroup(OnapPDPGroup group) throws PAPException;

    public void removeGroup(OnapPDPGroup group, OnapPDPGroup newGroup) throws PAPException;

    public void publishPolicy(String id, String name, boolean isRoot, InputStream policy, OnapPDPGroup group)
            throws PAPException;

    // copy the given policy file into the group's directory, but do not include the policy in the group's policy set
    public void copyPolicy(PDPPolicy policy, OnapPDPGroup group, String userId) throws PAPException;

    public void removePolicy(PDPPolicy policy, OnapPDPGroup group) throws PAPException;

    public void updateGroup(OnapPDPGroup group, String userName) throws PAPException;

}
