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

import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StdPDPStatus implements Serializable, PDPStatus {
    private static final long serialVersionUID = 1L;

    private Status status = Status.UNKNOWN;

    private Set<String> loadErrors = new HashSet<>();

    private Set<String> loadWarnings = new HashSet<>();

    private transient Set<PDPPolicy> loadedPolicies = new HashSet<>();

    private transient Set<PDPPolicy> loadedRootPolicies = new HashSet<>();

    private transient Set<PDPPolicy> failedPolicies = new HashSet<>();

    private transient Set<PDPPIPConfig> loadedPIPConfigs = new HashSet<>();

    private transient Set<PDPPIPConfig> failedPIPConfigs = new HashSet<>();

    public StdPDPStatus() {
        //
        // Default constructor
        //
    }

    public void set(StdPDPStatus newStatus) {
        this.status = newStatus.status;
        this.loadErrors.clear();
        this.loadErrors.addAll(newStatus.getLoadErrors());
        this.loadWarnings.clear();
        this.loadWarnings.addAll(newStatus.getLoadWarnings());
        this.loadedPolicies.clear();
        this.loadedPolicies.addAll(newStatus.getLoadedPolicies());
        this.loadedRootPolicies.clear();
        this.loadedRootPolicies.addAll(newStatus.getLoadedRootPolicies());
        this.failedPolicies.clear();
        this.failedPolicies.addAll(newStatus.getFailedPolicies());
        this.loadedPIPConfigs.clear();
        this.loadedPIPConfigs.addAll(newStatus.getLoadedPipConfigs());
        this.failedPIPConfigs.clear();
        this.failedPIPConfigs.addAll(newStatus.getFailedPipConfigs());
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Set<String> getLoadErrors() {
        return Collections.unmodifiableSet(this.loadErrors);
    }

    public void setLoadErrors(Set<String> errors) {
        this.loadErrors = errors;
    }

    public void addLoadError(String error) {
        this.loadErrors.add(error);
    }

    @Override
    public Set<String> getLoadWarnings() {
        return Collections.unmodifiableSet(this.loadWarnings);
    }

    public void setLoadWarnings(Set<String> warnings) {
        this.loadWarnings = warnings;
    }

    public void addLoadWarning(String warning) {
        this.loadWarnings.add(warning);
    }

    @Override
    public Set<PDPPolicy> getLoadedPolicies() {
        return Collections.unmodifiableSet(this.loadedPolicies);
    }

    public void setLoadedPolicies(Set<PDPPolicy> policies) {
        this.loadedPolicies = policies;
    }

    public void addLoadedPolicy(PDPPolicy policy) {
        this.loadedPolicies.add(policy);
    }

    @Override
    public Set<PDPPolicy> getLoadedRootPolicies() {
        return Collections.unmodifiableSet(this.loadedRootPolicies);
    }

    public void setLoadedRootPolicies(Set<PDPPolicy> policies) {
        this.loadedRootPolicies = policies;
    }

    public void addRootPolicy(PDPPolicy policy) {
        this.loadedRootPolicies.add(policy);
    }

    public void addAllLoadedRootPolicies(Set<PDPPolicy> policies) {
        this.loadedRootPolicies.addAll(policies);
    }

    @Override
    public Set<PDPPolicy> getFailedPolicies() {
        return Collections.unmodifiableSet(this.failedPolicies);
    }

    public void setFailedPolicies(Set<PDPPolicy> policies) {
        this.failedPolicies = policies;
    }

    public void addFailedPolicy(PDPPolicy policy) {
        this.failedPolicies.add(policy);
    }

    @Override
    public boolean policiesOK() {
        if (!this.failedPolicies.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public Set<PDPPIPConfig> getLoadedPipConfigs() {
        return Collections.unmodifiableSet(this.loadedPIPConfigs);
    }

    public void setLoadedPipConfigs(Set<PDPPIPConfig> configs) {
        this.loadedPIPConfigs = configs;
    }

    public void addLoadedPipConfig(PDPPIPConfig config) {
        this.loadedPIPConfigs.add(config);
    }

    @Override
    public Set<PDPPIPConfig> getFailedPipConfigs() {
        return Collections.unmodifiableSet(this.failedPIPConfigs);
    }

    public void setFailedPipConfigs(Set<PDPPIPConfig> configs) {
        this.failedPIPConfigs = configs;
    }

    public void addFailedPipConfig(PDPPIPConfig config) {
        this.failedPIPConfigs.add(config);
    }

    @Override
    public boolean pipConfigOK() {
        if (!this.failedPIPConfigs.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isOk() {
        if (!this.policiesOK()) {
            return false;
        }
        if (!this.pipConfigOK()) {
            return false;
        }
        return this.status == Status.UP_TO_DATE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (failedPIPConfigs.hashCode());
        result = prime * result + (failedPolicies.hashCode());
        result = prime * result + (loadErrors.hashCode());
        result = prime * result + (loadWarnings.hashCode());
        result = prime * result + (loadedPIPConfigs.hashCode());
        result = prime * result + (loadedPolicies.hashCode());
        result = prime * result + (status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StdPDPStatus other = (StdPDPStatus) obj;
        if (!failedPIPConfigs.equals(other.failedPIPConfigs))
            return false;
        if (!failedPolicies.equals(other.failedPolicies))
            return false;
        if (!loadErrors.equals(other.loadErrors))
            return false;
        if (!loadWarnings.equals(other.loadWarnings))
            return false;
        if (!loadedPIPConfigs.equals(other.loadedPIPConfigs))
            return false;
        if (!loadedPolicies.equals(other.loadedPolicies))
            return false;
        if (!loadedRootPolicies.equals(other.loadedRootPolicies))
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StdPDPStatus [status=" + status + ", loadErrors=" + loadErrors + ", loadWarnings=" + loadWarnings
                + ", loadedPolicies=" + loadedPolicies + ", loadedRootPolicies=" + loadedRootPolicies
                + ", failedPolicies=" + failedPolicies + ", loadedPIPConfigs=" + loadedPIPConfigs
                + ", failedPIPConfigs=" + failedPIPConfigs + "]";
    }

}
