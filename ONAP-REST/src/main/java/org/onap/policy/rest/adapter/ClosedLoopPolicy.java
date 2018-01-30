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


public class ClosedLoopPolicy {
	public static final String CLFAULT_UIFIELD_D2_SERVICES_TRINITY = "Hosted Voice (Trinity)";
	public static final String CLFAULT_UIJSON_D2_SERVICES_TRINITY = "trinity";

	public static final String CLFAULT_UIFIELD_D2_SERVICES_VUSP = "vUSP";
	public static final String CLFAULT_UIJSON_D2_SERVICES_VUSP = "vUSP";

	public static final String CLFAULT_UIFIELD_D2_SERVICES_MCR = "MCR";
	public static final String CLFAULT_UIJSON_D2_SERVICES_MCR = "mcr";

	public static final String CLFAULT_UIFIELD_D2_SERVICES_GAMMA = "Gamma";
	public static final String CLFAULT_UIJSON_D2_SERVICES_GAMMA = "gama";

	public static final String CLFAULT_UIFIELD_D2_SERVICES_VDNS = "vDNS";
	public static final String CLFAULT_UIJSON_D2_SERVICES_VDNS = "vDNS";

	public static final String CLFAULT_UIFIELD_EMAIL_ADDRESS = "Email Address";
	public static final String CLFAULT_UIJSON_EMAIL_ADDRESS = "emailAddress";

	public static final String CLFAULT_UIFIELD_TRIGGER_SIGNATURE = "Trigger Signature";
	public static final String CLFAULT_UIJSON_TRIGGER_SIGNATURE = "triggerSignaturesUsedForUI.signatures";

	public static final String CLFAULT_UIFIELD_VERIFICATION_SIGNATURE = "Verification Signature";
	public static final String CLFAULT_UIJSON_VERIFICATION_SIGNATURE = "verificationSignaturesUsedForUI.signatures";

	public static final String CLFAULT_UIFIELD_CONNECT_ALL_TRAPS = "Connect All Traps";
	public static final String CLFAULT_UIJSON_CONNECT_ALL_TRAPS = "triggerSignaturesUsedForUI.connectSignatures";

	public static final String CLFAULT_UIFIELD_CONNECT_ALL_FAULTS = "Connect All Faults";
	public static final String CLFAULT_UIJSON_CONNECT_ALL_FAULTS = "verificationSignaturesUsedForUI.connectSignatures";

	public static final String CLFAULT_UIFIELD_POLICY_STATUS_ACTIVE = "Active";
	public static final String CLFAULT_UIJSON_POLICY_STATUS_ACTIVE = "ACTIVE";

	public static final String CLFAULT_UIFIELD_POLICY_STATUS_INACTIVE = "InActive";
	public static final String CLFAULT_UIJSON_POLICY_STATUS_INACTIVE = "INACTIVE";

	private ClosedLoopPolicy(){
		// Empty constructor
	}
}
