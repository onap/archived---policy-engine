/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.util;

import java.util.HashMap;
import java.util.Map;

public class MSAttributeObject {

	private String className;
	private HashMap<String, String> attribute = new HashMap<>();
	private HashMap<String, String> refAttribute = new HashMap<>();
	private HashMap<String, Object> subClass = new HashMap<>();
	private String dependency;
	private HashMap<String, String> enumType = new HashMap<>();
	private HashMap<String, String> matchingSet = new HashMap<>();
	private boolean policyTempalate;

	public Map<String, String> getRefAttribute() {
		return refAttribute;
	}
	public void setRefAttribute(HashMap<String, String> refAttribute) {
		this.refAttribute = refAttribute;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Map<String, String> getAttribute() {
		return attribute;
	}
	public void setAttribute(HashMap<String, String> attribute) {
		this.attribute = attribute;
	}
	public HashMap<String, String> getEnumType() {
		return enumType;
	}
	public void setEnumType(HashMap<String, String> enumType) {
		this.enumType = enumType;
	}
	public void addAttribute(String key, String value){
		this.attribute.put(key, value);
	}
	public void addRefAttribute(String key, String value){
		this.refAttribute.put(key, value);
	}
	public void addAllAttribute(Map<String, String> map){
		this.attribute.putAll(map);
	}
	public void addAllRefAttribute(Map<String, String> map){
		this.refAttribute.putAll(map);
	}
	public HashMap<String, Object> getSubClass() {
		return subClass;
	}
	public void setSubClass(HashMap<String, Object> subClass) {
		this.subClass = subClass;
	}
	public void addAllSubClass(HashMap<String, Object> subClass){
		this.subClass.putAll(subClass);
	}
	public String getDependency() {
		return dependency;
	}
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	public void addSingleEnum(String key, String value){
		this.enumType.put(key, value);
	}
	public HashMap<String, String> getMatchingSet() {
		return matchingSet;
	}
	public void setMatchingSet(HashMap<String, String> matchingSet) {
		this.matchingSet = matchingSet;
	}
	public void addMatchingSet(String key, String value){
		this.matchingSet.put(key, value);
	}
	public void addMatchingSet(HashMap<String, String> matchingSet){
		this.matchingSet.putAll(matchingSet);
	}
	public boolean isPolicyTempalate() {
		return policyTempalate;
	}
	public void setPolicyTempalate(boolean policyTempalate) {
		this.policyTempalate = policyTempalate;
	}
}