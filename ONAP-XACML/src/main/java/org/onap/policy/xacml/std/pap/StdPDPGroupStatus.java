/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroupStatus;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper=false)
@ToString
public class StdPDPGroupStatus implements PDPGroupStatus {

    private Status status = Status.UNKNOWN;

    private Set<String> loadErrors = new HashSet<>();

    private Set<String> loadWarnings = new HashSet<>();

    private Set<PDPPolicy> loadedPolicies = new HashSet<>();

    private Set<PDPPolicy> failedPolicies = new HashSet<>();

    private Set<PDPPIPConfig> loadedPIPConfigs = new HashSet<>();

    private Set<PDPPIPConfig> failedPIPConfigs = new HashSet<>();

    private Set<PDP> inSynchPDPs = new HashSet<>();

    private Set<PDP> outOfSynchPDPs = new HashSet<>();

    private Set<PDP> failedPDPs = new HashSet<>();

    private Set<PDP> updatingPDPs = new HashSet<>();

    private Set<PDP> lastUpdateFailedPDPs = new HashSet<>();

    private Set<PDP> unknownPDPs = new HashSet<>();

    public StdPDPGroupStatus() {
        //
        // Constructor needed for JSON deserialization
        //
    }

    public StdPDPGroupStatus(Status status) {
        this.status = status;
    }

    public StdPDPGroupStatus(PDPGroupStatus stat) {
        this.status = stat.getStatus();
        this.failedPDPs.clear();
        this.failedPDPs.addAll(stat.getFailedPDPs());
        this.failedPIPConfigs.clear();
        this.failedPIPConfigs.addAll(stat.getFailedPipConfigs());
        this.failedPolicies.clear();
        this.failedPolicies.addAll(stat.getFailedPolicies());
        this.inSynchPDPs.clear();
        this.inSynchPDPs.addAll(stat.getInSynchPDPs());
        this.lastUpdateFailedPDPs.clear();
        this.lastUpdateFailedPDPs.addAll(stat.getLastUpdateFailedPDPs());
        this.loadedPIPConfigs.clear();
        this.loadedPIPConfigs.addAll(stat.getLoadedPipConfigs());
        this.loadedPolicies.clear();
        this.loadedPolicies.addAll(stat.getLoadedPolicies());
        this.loadErrors.clear();
        this.loadErrors.addAll(stat.getLoadErrors());
        this.loadWarnings.clear();
        this.loadWarnings.addAll(stat.getLoadWarnings());
        this.outOfSynchPDPs.clear();
        this.outOfSynchPDPs.addAll(stat.getOutOfSynchPDPs());
        this.unknownPDPs.clear();
        this.unknownPDPs.addAll(stat.getUpdatingPDPs());
        this.updatingPDPs.clear();
        this.updatingPDPs.addAll(stat.getUpdatingPDPs());
    }

    public Set<PDPPIPConfig> getLoadedPIPConfigs() {
        return loadedPIPConfigs;
    }

    public void setLoadedPIPConfigs(Set<PDPPIPConfig> loadedPIPConfigs) {
        this.loadedPIPConfigs = loadedPIPConfigs;
    }

    public Set<PDPPIPConfig> getFailedPIPConfigs() {
        return failedPIPConfigs;
    }

    public void setFailedPIPConfigs(Set<PDPPIPConfig> failedPIPConfigs) {
        this.failedPIPConfigs = failedPIPConfigs;
    }

    public Set<PDP> getUnknownPDPs() {
        return unknownPDPs;
    }

    public void setUnknownPDPs(Set<PDP> unknownPDPs) {
        this.unknownPDPs = unknownPDPs;
    }

    public void setLoadErrors(Set<String> loadErrors) {
        this.loadErrors = loadErrors;
    }

    public void setLoadWarnings(Set<String> loadWarnings) {
        this.loadWarnings = loadWarnings;
    }

    public void setLoadedPolicies(Set<PDPPolicy> loadedPolicies) {
        this.loadedPolicies = loadedPolicies;
    }

    public void setFailedPolicies(Set<PDPPolicy> failedPolicies) {
        this.failedPolicies = failedPolicies;
    }

    public void setInSynchPDPs(Set<PDP> inSynchPDPs) {
        this.inSynchPDPs = inSynchPDPs;
    }

    public void setOutOfSynchPDPs(Set<PDP> outOfSynchPDPs) {
        this.outOfSynchPDPs = outOfSynchPDPs;
    }

    public void setFailedPDPs(Set<PDP> failedPDPs) {
        this.failedPDPs = failedPDPs;
    }

    public void setUpdatingPDPs(Set<PDP> updatingPDPs) {
        this.updatingPDPs = updatingPDPs;
    }

    public void setLastUpdateFailedPDPs(Set<PDP> lastUpdateFailedPDPs) {
        this.lastUpdateFailedPDPs = lastUpdateFailedPDPs;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Set<String> getLoadErrors() {
        return Collections.unmodifiableSet(this.loadErrors);
    }

    public void addLoadError(String error) {
        this.loadErrors.add(error);
    }

    @Override
    public Set<String> getLoadWarnings() {
        return Collections.unmodifiableSet(this.loadWarnings);
    }

    public void addLoadWarning(String warning) {
        this.loadWarnings.add(warning);
    }

    @Override
    public Set<PDPPolicy> getLoadedPolicies() {
        return Collections.unmodifiableSet(this.loadedPolicies);
    }

    public void addLoadedPolicy(PDPPolicy policy) {
        this.loadedPolicies.add(policy);
    }

    @Override
    public Set<PDPPolicy> getFailedPolicies() {
        return Collections.unmodifiableSet(this.failedPolicies);
    }

    public void addFailedPolicy(PDPPolicy policy) {
        this.failedPolicies.add(policy);
    }

    @Override
    public boolean policiesOK() {
        return this.failedPolicies.isEmpty();
    }

    @Override
    public Set<PDPPIPConfig> getLoadedPipConfigs() {
        return Collections.unmodifiableSet(this.loadedPIPConfigs);
    }

    public void addLoadedPipConfig(PDPPIPConfig config) {
        this.loadedPIPConfigs.add(config);
    }

    @Override
    public Set<PDPPIPConfig> getFailedPipConfigs() {
        return Collections.unmodifiableSet(this.failedPIPConfigs);
    }

    public void addFailedPipConfig(PDPPIPConfig config) {
        this.failedPIPConfigs.add(config);
    }

    @Override
    public boolean pipConfigOK() {
        return this.failedPIPConfigs.isEmpty();
    }

    @Override
    public Set<PDP> getInSynchPDPs() {
        return Collections.unmodifiableSet(this.inSynchPDPs);
    }

    public void addInSynchPDP(PDP pdp) {
        this.inSynchPDPs.add(pdp);
    }

    @Override
    public Set<PDP> getOutOfSynchPDPs() {
        return Collections.unmodifiableSet(this.outOfSynchPDPs);
    }

    public void addOutOfSynchPDP(PDP pdp) {
        this.outOfSynchPDPs.add(pdp);
    }

    @Override
    public Set<PDP> getFailedPDPs() {
        return Collections.unmodifiableSet(this.failedPDPs);
    }

    public void addFailedPDP(PDP pdp) {
        this.failedPDPs.add(pdp);
    }

    @Override
    public Set<PDP> getUpdatingPDPs() {
        return Collections.unmodifiableSet(this.updatingPDPs);
    }

    public void addUpdatingPDP(PDP pdp) {
        this.updatingPDPs.add(pdp);
    }

    @Override
    public Set<PDP> getLastUpdateFailedPDPs() {
        return Collections.unmodifiableSet(this.lastUpdateFailedPDPs);
    }

    public void addLastUpdateFailedPDP(PDP pdp) {
        this.lastUpdateFailedPDPs.add(pdp);
    }

    @Override
    @JsonIgnore
    public Set<PDP> getUnknownStatusPDPs() {
        return Collections.unmodifiableSet(this.unknownPDPs);
    }

    public void addUnknownPDP(PDP pdp) {
        this.unknownPDPs.add(pdp);
    }

    @Override
    public boolean pdpsOK() {
        if (!this.outOfSynchPDPs.isEmpty()) {
            return false;
        }
        if (!this.failedPDPs.isEmpty()) {
            return false;
        }
        if (!this.lastUpdateFailedPDPs.isEmpty()) {
            return false;
        }
        return this.unknownPDPs.isEmpty();
    }

    @Override
    @JsonIgnore
    public boolean isGroupOk() {
        if (!this.policiesOK()) {
            return false;
        }
        if (!this.pipConfigOK()) {
            return false;
        }
        if (!this.pdpsOK()) {
            return false;
        }
        if (!this.loadErrors.isEmpty()) {
            return false;
        }
        return this.status == Status.OK;
    }

    /**
     * reset.
     */
    public void reset() {
        this.status = Status.OK;

        this.loadErrors.clear();
        this.loadWarnings.clear();
        this.loadedPolicies.clear();
        this.failedPolicies.clear();
        this.loadedPIPConfigs.clear();
        this.failedPIPConfigs.clear();
        this.inSynchPDPs.clear();
        this.outOfSynchPDPs.clear();
        this.failedPDPs.clear();
        this.updatingPDPs.clear();
        this.lastUpdateFailedPDPs.clear();
        this.unknownPDPs.clear();
    }

}
