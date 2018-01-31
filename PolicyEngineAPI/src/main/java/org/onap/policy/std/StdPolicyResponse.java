/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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


package org.onap.policy.std;

import java.util.Map;

import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyResponseStatus;

/**
 * PolicyResponse Implementation class.
 *
 * @version 0.1
 *
 */
public class StdPolicyResponse implements PolicyResponse{
	private PolicyResponseStatus policyResponseStatus;
	private Map<String,String> actionAdvised;
	private Map<String,String> actionTaken;
	private Map<String,String> requestAttributes;
	private String policyResponseMessage;

	@Override
	public PolicyResponseStatus getPolicyResponseStatus() {
		return policyResponseStatus;
	}

	@Override
	public Map<String, String> getActionAdvised() {
		return actionAdvised;
	}

	@Override
	public Map<String, String> getActionTaken() {
		return actionTaken;
	}

	@Override
	public Map<String, String> getRequestAttributes() {
		return requestAttributes;
	}

	@Override
	public String getPolicyResponseMessage() {
		return policyResponseMessage;
	}

	public void setPolicyResponseStatus(PolicyResponseStatus policyResponseStatus) {
		this.policyResponseStatus = policyResponseStatus;
	}

	public void setActionAdvised(Map<String, String> actionAdvised) {
		this.actionAdvised = actionAdvised;
	}

	public void setActionTaken(Map<String, String> actionTaken) {
		this.actionTaken = actionTaken;
	}

	public void setRequestAttributes(Map<String, String> requestAttributes) {
		this.requestAttributes = requestAttributes;
	}

	public void setPolicyResponseMessage(String policyResponseMessage) {
		this.policyResponseMessage = policyResponseMessage;
	}

	public void setPolicyResponseStatus(String policyResponseMessage, PolicyResponseStatus policyResponseStatus) {
		this.policyResponseMessage = policyResponseMessage;
		this.policyResponseStatus = policyResponseStatus;
	}

	@Override
	public String toString() {
		return "PolicyResponse [ policyResponseStatus=" + policyResponseStatus + ", policyResponseMessage=" + policyResponseMessage + ", " +
				""
				+ "]";
	}
}
