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

package org.openecomp.policy.api;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enumeration of the Attribute Types that is used as a part of
 * {@link org.openecomp.policy.api.PolicyParameters}.
 * 
 * @version 0.1
 */
public enum RuleProvider {
	/**
	 * Indicates User will be defining the Rule information. 
	 */
	CUSTOM("Custom"),
	/**
	 * Indicates AAF will be providing the Rule information. 
	 */
	AAF("AAF"),
	/**
	 * Indicates Guard YAML will be providing the Rule information.
	 */
	GUARD_YAML("GUARD_YAML")
	;
	
	private String name;
	
	private RuleProvider(String typeName){
		this.name = typeName;
	}
	
	/**
	 * Returns the <code>String</code> format of Type for this <code>AttributeType</code>
	 * @return the <code>String</code> of the Type for this <code>AttributeType</code>
	 */
	public String toString() {
		return this.name;
	}
	
	@JsonCreator
    public static RuleProvider create (String value) {
        for(RuleProvider type: values()){
            if(type.toString().equals(value) || type.equals(RuleProvider.valueOf(value))){
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
