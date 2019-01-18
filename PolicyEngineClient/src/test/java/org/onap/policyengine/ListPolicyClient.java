/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
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

package org.onap.policyengine;

import java.util.Collection;
import org.onap.policy.api.ConfigNameRequest;
import org.onap.policy.api.PolicyEngine;

/**
 * List Policy Client Code. policyName : ".*" returns list of policy names from PAP. policyName :
 * "scope + "_" + "policyType" + "_" + policyName" + ".*" returns the matching policy from pap
 * (active version)
 */
public class ListPolicyClient {
    public static void main(String[] args) throws Exception {
        PolicyEngine pe = new PolicyEngine("config.properties");
        ConfigNameRequest listPolicyParams = new ConfigNameRequest();
        listPolicyParams.setPolicyName(".*");
        Collection<String> policies = pe.listPolicy(listPolicyParams);
        for (String policy : policies) {
            System.out.println(policy);
        }
    }
}
