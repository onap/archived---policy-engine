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
 * Enumeration of the Policy Return Types that can be returned as part of a
 * {@link org.onap.policy.api.PolicyConfig}.
 * 
 * @version 0.2
 */
public enum PolicyType {
	/**
	 * Indicates the response is Properties type
	 */
	PROPERTIES("Properties"),
	/**
	 * Indicates the response is JSON type
	 */
	JSON("json"),
	/**
	 * Indicates the response is XML type
	 */
	XML("xml"),
	/**
	 * Indicates the response is Other type
	 */
	OTHER("other");

	private final String name;

	private PolicyType(final String typeName) {
		this.name = typeName;
	}

	/**
	 * Returns the <code>String</code> format of Type for this
	 * <code>PolicyType</code>
	 * 
	 * @return the <code>String</code> of the Type for this
	 *         <code>PolicyType</code>
	 */
	@Override
	public String toString() {
		return this.name;
	}

	@JsonCreator
	public static PolicyType create(final String value) {
		for (final PolicyType type : values()) {
			if (type.toString().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid value: " + value);
	}
}
