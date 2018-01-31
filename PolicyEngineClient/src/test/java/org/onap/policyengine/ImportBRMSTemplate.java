/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
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

package org.onap.policyengine;

import java.io.File;
import java.util.UUID;

import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.ImportParameters.IMPORT_TYPE;

public class ImportBRMSTemplate {
	public static void main(String[] args) {
		try{
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        ImportParameters importParameters = new ImportParameters();
	        importParameters.setFilePath("testResources"+File.separator+"test.drl");
	        importParameters.setServiceName("testTemplate");
	        importParameters.setRequestID(UUID.randomUUID());
	        importParameters.setServiceType(IMPORT_TYPE.BRMSPARAM);

            // API method to create Policy or update policy
	        PolicyChangeResponse response = null;
            response = policyEngine.policyEngineImport(importParameters);
            System.out.println(response.getResponseMessage());

	    } catch (Exception e) {
	        System.err.println(e.getMessage());
	    }
	}
}
