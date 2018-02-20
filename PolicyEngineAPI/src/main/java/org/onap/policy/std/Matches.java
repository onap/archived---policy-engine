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
	private Map<String, String> configAttributes = null;

	public String getOnapName() {
		return onapName;
	}

	public void setOnapName(final String onapName) {
		this.onapName = onapName;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(final String configName) {
		this.configName = configName;
	}

	public Map<String, String> getConfigAttributes() {
		return configAttributes;
	}

	public void setConfigAttributes(final Map<String, String> configAttributes) {
		this.configAttributes = configAttributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configAttributes == null) ? 0 : configAttributes.hashCode());
		result = prime * result + ((configName == null) ? 0 : configName.hashCode());
		result = prime * result + ((onapName == null) ? 0 : onapName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Matches) {
			final Matches other = (Matches) obj;
			return isEqual(onapName, other.onapName) && isEqual(configName, other.configName)
					&& isEqual(configAttributes, other.configAttributes);
		}
		return false;
	}

	private boolean isEqual(final Object objectA, final Object objectB) {
		if (objectA == null) {
			return objectB == null;
		}
		return objectA.equals(objectB);
	}

}
