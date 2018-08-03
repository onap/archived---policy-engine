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


import java.util.Map;

import org.onap.policy.rest.adapter.PolicyRestAdapter;

import io.searchbox.client.JestResult;

public interface ElkConnector {

    public static final String ELK_URL = "http://localhost:9200";
    public static final String ELK_INDEX_POLICY = "policy";

    public enum PolicyIndexType {
        config,
        action,
        decision,
        closedloop,
        all,
    }

    public enum PolicyType {
        Config,
        Action,
        Decision,
        Config_Fault,
        Config_PM,
        Config_FW,
        Config_MS,
        Config_OOF,
        none,
    }

    public enum PolicyBodyType {
        json,
        xml,
        properties,
        txt,
        none,
    }

    public boolean delete(PolicyRestAdapter policyData)
            throws IllegalStateException;

    public JestResult search(PolicyIndexType type, String text)
           throws IllegalStateException, IllegalArgumentException;

    public JestResult search(PolicyIndexType type, String text,
            Map<String, String> searchKeyValue)
               throws IllegalStateException, IllegalArgumentException;

    public boolean update(PolicyRestAdapter policyData) throws IllegalStateException;

    public ElkConnector singleton = new ElkConnectorImpl();

    public static PolicyIndexType toPolicyIndexType(String policyName)
            throws IllegalArgumentException {
        if (policyName == null)
            throw new IllegalArgumentException("Unsupported NULL policy name conversion");

        if (policyName.startsWith("Config_Fault")) {
            return PolicyIndexType.closedloop;
        } else if (policyName.startsWith("Config_PM")) {
            return PolicyIndexType.closedloop;
        } else if (policyName.startsWith("Config_FW")) {
            return PolicyIndexType.config;
        } else if (policyName.startsWith("Config_MS")) {
            return PolicyIndexType.config;
        } else if (policyName.startsWith("Config_OOF")) {
            return PolicyIndexType.config;
        }else if (policyName.startsWith("Action")) {
            return PolicyIndexType.action;
        } else if (policyName.startsWith("Decision")) {
            return PolicyIndexType.decision;
        } else if (policyName.startsWith("Config")) {
            return PolicyIndexType.config;
        } else {
            throw new IllegalArgumentException
            ("Unsupported policy name conversion to index: " +
                    policyName);
        }
    }

}
