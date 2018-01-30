/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.rest.adapter;


public class ClosedLoopPerformanceMetrics {
	public static final String CLPM_UIFIELD_ONSET_MESSAGE = "Onset Message";
	public static final String CLPM_UIJSON_ONSET_MESSAGE = "attributes.OnsetMessage";
	
	public static final String CLPM_UIFIELD_POLICY_NAME = "PolicyName";
	public static final String CLPM_UIJSON_POLICY_NAME = "attributes.PolicyName";
	
	public static final String CLPM_UIFIELD_ABATEMENT_MESSAGE = "Abatement Message";
	public static final String CLPM_UIJSON_ABATEMENT_MESSAGE = "attributes.AbatementMessage";
	
	public static final String CLPM_UIFIELD_GEOLINK = "Geo Link";	
	public static final String CLPM_UIJSON_GEOLINK = "geoLink";

	private ClosedLoopPerformanceMetrics() {
	    // Empty constructor
    }
}
