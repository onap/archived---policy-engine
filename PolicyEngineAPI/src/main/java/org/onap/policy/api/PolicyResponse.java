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

import java.util.Map;

/**
 * Defines the objects that represent PolicyEngine Response elements. PolicyResponse communicates the PolicyResponseStatus,
 * PolicyResponseMessage, ActionAdvised, ActionTaken and RequestAttributes.
 *
 * @version 0.3
 */
public interface PolicyResponse {
	/**
	 * Gets the {@link org.onap.policy.api.PolicyResponseStatus} associated with this <code>PolicyResponse</code>.
	 *
	 * @return the <code>PolicyResponseStatus</code> associated with this <code>PolicyResponse</code>
	 */
	public PolicyResponseStatus getPolicyResponseStatus();

	/**
	 * Gets the <code>Map</code> of <code>String,String</code> which consists of ActionAdvised in this <code>PolicyResponse</code>.
	 * If there is no ActionAdvised this method must return an empty <code>Map</code>.
	 *
	 * @return the <code>Map</code> of <code>String,String</code> which consists of AdviceAttributes in <code>PolicyResponse</code>
	 */
	public Map<String,String> getActionAdvised();

	/**
	 * Gets the <code>Map</code> of <code>String,String</code> which consists of ActionTaken in this <code>PolicyResponse</code>.
	 * If there are no ActionTaken this method must return an empty <code>Map</code>.
	 *
	 * @return the <code>Map</code> of <code>String,String</code> which consists of ActionTaken in <code>PolicyResponse</code>
	 */
	public Map<String,String> getActionTaken();

	/**
	 * Gets the <code>Map</code> of <code>String,String</code> which consists of RequestAttributes in this <code>PolicyResponse</code>.
	 *
	 * @return the <code>Map</code> of <code>String,String</code> which consists of RequestAttributes from <code>PolicyResponse</code>
	 */
	public Map<String,String> getRequestAttributes();

	/**
	 * Gets the <code>String</code> of the PolicyResponseMessage from <code>PolicyResponse</code>
	 *
	 * @return the <code>String</code> which consists of PolicyResponseMessage from <code>PolicyResponse</code>
	 */
	public String getPolicyResponseMessage();

	/**
	 * Returns the <code>String</code> version of the <code>PolicyResponse</code> object.
	 *
	 *  @return <code>String</code> of the <code>PolicyResponse</code> Object.
	 */
	@Override
	public String toString();
}
