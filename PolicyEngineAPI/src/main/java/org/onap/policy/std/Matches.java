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

package org.onap.policy.std;

import java.util.Map;

public class Matches {
	private String onapName = null;
	private String configName = null;
	private Map<String,String> configAttributes = null;
	public String getOnapName() {
		return onapName;
	}
	public void setOnapName(String onapName) {
		this.onapName = onapName;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public Map<String,String> getConfigAttributes() {
		return configAttributes;
	}
	public void setConfigAttributes(Map<String,String> configAttributes) {
		this.configAttributes = configAttributes;
	}

}
