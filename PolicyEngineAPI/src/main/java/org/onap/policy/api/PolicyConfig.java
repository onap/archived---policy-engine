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
import java.util.Properties;

import javax.json.JsonObject;

import org.w3c.dom.Document;
/**
 * Defines the objects that represent PolicyEngine config elements. PolicyConfig communicates the PolicyConfigStatus,
 * PolicyConfigMessage, PolicyType, Properties, JsonObject, String and Document.
 *
 * @version 0.7
 */

public interface PolicyConfig {
	/**
	 * Gets the {@link org.onap.policy.api.PolicyType} associated with <code>PolicyConfig</code>
	 *
	 * @return the <code>PolicyType</code> associated with this <code>PolicyConfig</code>
	 */
	public PolicyType getType();

	/**
	 * Gives the <code>Properties</code> response associated with the <code>PolicyConfig</code>
	 *
	 * @return the <code>Properties</code> associated with this <code>PolicyConfig</code>
	 */
	public Properties toProperties();

	/**
	 * Gives the <code>JsonObject</code> response associated with the <code>PolicyConfig</code>
	 *
	 * @return the <code>JsonObject</code> result associated with <code>PolicyConfig</code>
	 */
	public JsonObject toJSON();

	/**
	 * Gives the XML <code>Document</code> result associated with <code>PolicyConfig</code>
	 *
	 * @return the <code>Document</code> result associated with <code>PolicyConfig</code>
	 */
	public Document toXML();

	/**
	 * Gives the Other <code>String</code> response associated with <code>PolicyConfig</code>
	 *
	 * @return the <code>String</code> result associated with <code>PolicyConfig</code>
	 */
	public String toOther();

	/**
	 * Gets the {@link org.onap.policy.api.PolicyConfigStatus} associated with this <code>PolicyConfig</code>.
	 *
	 * @return the <code>PolicyConfigStatus</code> associated with this <code>PolicyConfig</code>
	 */
	public PolicyConfigStatus getPolicyConfigStatus();

	/**
	 * Gets the <code>String</code> of the PolicyConfigMessage from <code>PolicyConfig</code>.
	 *
	 * @return the <code>String</code> which consists of PolicyConfigMessage from <code>PolicyConfig</code>
	 */
	public String getPolicyConfigMessage();

	/**
	 * Gets the <code>String</code> of the PolicyName retrieved.
	 *
	 * @return the <code>String</code> which consists of Policy Name which has been retrieved.
	 */
	public String getPolicyName();


	/**
	 * Gets the <code>String</code> of the PolicyVersion retrieved.
	 *
	 * @return the <code>String</code> which consists of the Policy Version number which has been retrieved.
	 */
	public String getPolicyVersion();

	/**
	 * Gets the Matching Conditions of the policy retrieved which can be used in the getConfig call.
	 *
	 * @return <code>Map</code> of <code>String, String</code> which consists of the Matching conditions of the Policy retrieved.
	 */
	public Map<String,String> getMatchingConditions();

	/**
	 * Gets the Response Attributes of the policy retrieved. Which can hold additional information about the policy retrieved.
	 *
	 * @return <code>Map</code> of <code>String, String</code> which consists of the Response Attributes of the Policy retrieved.
	 */
	public Map<String,String> getResponseAttributes();

	/**
	 * Gets the {@link PolicyConfigType} of the policy that has been retrieved.
	 *
	 * @return the <code>PolicyConfigType</code> of the policy that has been retrieved.
	 */
	public PolicyConfigType getPolicyType();
 
	/**
	 * Returns the <code>String</code> version of the <code>PolicyConfig</code> object.
	 *
	 *  @return <code>String</code> of the <code>PolicyConfig</code> Object.
	 */
	@Override
	public String toString();

}
