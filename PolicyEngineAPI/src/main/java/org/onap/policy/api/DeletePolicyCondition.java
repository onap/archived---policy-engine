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

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enumeration of the Policy Delete Condition that is used as a part of
 * {@link org.onap.policy.api.DeletePolicyParameters}.
 * 
 * @version 0.1
 */
public enum DeletePolicyCondition {

	/**
	 * Indicates a condition to only delete the current version of the policy.
	 */
	ONE("Current Version"),

	/**
	 * Indicates a condition to delete all versions of the policy.
	 */
	ALL("All Versions");

	private final String name;

	private DeletePolicyCondition(final String name) {
		this.name = name;
	}

	/**
	 * Returns the <code>String</code> format of delete condition for this
	 * Policy
	 * 
	 * @return the <code>String</code> of the delete condition for this Policy
	 */
	@Override
	public String toString() {
		return this.name;
	}

	@JsonCreator
	public static DeletePolicyCondition create(final String value) {
		for (final DeletePolicyCondition type : values()) {
			if (type.toString().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid value: " + value);
	}
}
