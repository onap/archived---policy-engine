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
 * Enumeration of the Attribute Types that is used as a part of
 * {@link org.onap.policy.api.PolicyParameters}.
 * 
 * @version 0.1
 */
public enum AttributeType {
	/**
	 * Indicates Attributes required to Match the Policy. 
	 */
	MATCHING("matching"),
	/**
	 * Indicates Attributes required to create DRL based Rules. 
	 */
	RULE("rule"),
	/**
	 * Indicates Attributes required to create MicroService policy. 
	 */
	MICROSERVICE("microService"),
	/**
	 * Indicates Attributes required to create Optimization policy.
	 */
	OPTIMIZATION("optimization"),
	/**
	 * Indicates Attributes required to create settings for Decision Policy.
	 */
	SETTINGS("settings"),
	/**
	 * Indicates Attributes required to create dictionary fields for creating Dictionary Items
	 */
	DICTIONARY("dictionary")
	;
	
	
	private String name;
	
	private AttributeType(String typeName){
		this.name = typeName;
	}
	
	/**
	 * Returns the <code>String</code> format of Type for this <code>AttributeType</code>
	 * @return the <code>String</code> of the Type for this <code>AttributeType</code>
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	@JsonCreator
    public static AttributeType create (String value) {
        for(AttributeType type: values()){
            if(type.toString().equalsIgnoreCase(value)){
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
