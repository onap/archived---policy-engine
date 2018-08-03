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
package org.onap.policy.pap.xacml.rest.elk.client;

public class PolicyLocator {
    public final String policyType;
    public final String policyName;
    public final String owner;
    public final String scope;
    public final String policyId;
    public final String version;

    public PolicyLocator(String policyType, String policyName,
                         String owner, String scope, String policyId,
                         String version) {
        this.policyType = policyType;
        this.policyName= policyName;
        this.owner = owner;
        this.scope = scope;
        this.policyId = policyId;
        this.version = version;
    }

    public String toString() {
        return "[" +
               this.owner + "|" +
               this.scope + "|" +
               this.policyType + "|" +
               this.policyName + "|" +
               this.policyId + "|" +
               "v" + this.version + "|" + "]";

    }
}
