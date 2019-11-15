/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
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

package org.onap.policyengine;

import java.io.File;
import java.util.UUID;

import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.ImportParameters.IMPORT_TYPE;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyEngine;

public class ImportBRMSTemplate {

    /**
     * main.
     *
     * @param args String[] args
     */
    public static void main(String[] args) {
        try {
            ImportParameters importParameters = new ImportParameters();
            importParameters.setFilePath("resources" + File.separator + "test.drl");
            importParameters.setServiceName("testTemplate");
            importParameters.setRequestID(UUID.randomUUID());
            importParameters.setServiceType(IMPORT_TYPE.BRMSPARAM);

            // API method to create Policy or update policy
            PolicyEngine policyEngine = new PolicyEngine("config.properties");
            PolicyChangeResponse response = policyEngine.policyEngineImport(importParameters);
            System.out.println(response.getResponseMessage());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
