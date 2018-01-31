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

package org.onap.policy.api;

/**
 * Defines the Object that represents the Policy Decision Response elements.
 * DecisionResponse communicates the decision and details
 *
 * @version 0.1
 */
public interface DecisionResponse {
	/**
	 * Gets the Decision of the Policy, Either a Permit or Deny.
	 *
	 * @return {@link org.onap.policy.api.PolicyDecision} Enumeration.
	 */
	public PolicyDecision getDecision();

	/**
	 * Gets the details of the result. Would be required in case of Deny.
	 *
	 * @return <code>String</code> format of the details of Deny cause.
	 */
	public String getDetails();
}
