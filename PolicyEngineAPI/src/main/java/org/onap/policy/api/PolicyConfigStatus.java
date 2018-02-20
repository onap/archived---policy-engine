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
 * Enumeration of PolicyConfigStatus that can be returned as a part of
 * {@link org.onap.policy.api.PolicyConfig}.
 * 
 * @version 0.1
 */
public enum PolicyConfigStatus {
	/**
	 * Indicates that the Configuration has been successfully retrieved.
	 */
	CONFIG_RETRIEVED("retrieved"),
	/**
	 * Indicates that there is no Configuration Retrieved from PolicyConfig.
	 */
	CONFIG_NOT_FOUND("not_found");

	private final String name;

	private PolicyConfigStatus(final String name) {
		this.name = name;
	}

	/**
	 * Get the <code>PolicyConfigStatus</code> based on <code>String</code>
	 * representation of <code>PolicyConfig</code>
	 * 
	 * @param configStatus
	 *            the <code>String</code> Configuration Status
	 * @return the <code>PolicyConfigResponse</code> with the name matching
	 *         <code>CONFIG_RETRIEVED</code> or <code>CONFIG_NOT_FOUND</code> if
	 *         no match is found
	 */
	public static PolicyConfigStatus getStatus(final String configStatus) {
		if (CONFIG_RETRIEVED.name.equalsIgnoreCase(configStatus)) {
			return CONFIG_RETRIEVED;
		}
		return CONFIG_NOT_FOUND;
	}

	/**
	 * Returns the <code>String</code> name for this
	 * <code>PolicyConfigStatus</code>
	 * 
	 * @return the <code>String</code> name for this
	 *         <code>PolicyConfigStatus</code>
	 */
	@Override
	public String toString() {
		return this.name;
	}

	@JsonCreator
	public static PolicyConfigStatus create(final String value) {
		for (final PolicyConfigStatus type : values()) {
			if (type.toString().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid value: " + value);
	}
}
