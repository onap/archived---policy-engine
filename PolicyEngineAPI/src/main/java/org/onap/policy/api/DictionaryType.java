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

public enum DictionaryType {
	/**
	 * Indicates Common Dictionaries.
	 */
	Common("Common"),
	/**
	 * Indicates Action Policy Dictionaries
	 */
	Action("Action"),
	/**
	 * Indicates ClosedLoop Policy Dictionaries.
	 */
	ClosedLoop("ClosedLoop"),
	/**
	 * Indicates Firewall Config Policy Dictionaries.
	 */
	Firewall("Firewall"),
	/**
	 * Indicates Decision Policy Dictionaries.
	 */
	Decision("Decision"),
	/**
	 * Indicates BRMS Policy Dictionaries.
	 */
	BRMS("BRMS"),
	/**
	 * Indicates DCAE Micro Service Policy Dictionaries.
	 */
	MicroService("MicroService"),
	/**
	 * Indicates Descriptive Scope Dictionaries
	 */
	DescriptiveScope("DescriptiveScope"),
	/**
	 * Indicates Policy Scope Dictionaries
	 */
	PolicyScope("PolicyScope"),
	/**
	 * Indicates Enforcer Dictionaries
	 */
	Enforcer("Enforcer"),
	/**
	 * Indicates SafePolicy Dictionaries
	 */
	SafePolicy("SafePolicy"),
	/**
	 * Enum support entry to extend dictionary
	 */
	Extended("Extended");

	private final String name;

	private DictionaryType(final String typeName) {
		this.name = typeName;
	}

	/**
	 * Returns the <code>String</code> format of Type for this
	 * <code>PolicyClass</code>
	 * 
	 * @return the <code>String</code> of the Type for this
	 *         <code>PolicyClass</code>
	 */
	@Override
	public String toString() {
		return this.name;
	}

	@JsonCreator
	public static DictionaryType create(final String value) {
		for (final DictionaryType type : values()) {
			if (type.toString().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid value: " + value);
	}

}
