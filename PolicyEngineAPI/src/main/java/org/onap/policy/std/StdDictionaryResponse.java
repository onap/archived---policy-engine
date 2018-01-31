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

import javax.json.JsonObject;

import org.onap.policy.api.DictionaryResponse;

public class StdDictionaryResponse implements DictionaryResponse {
	private String dictionaryMessage;
	private JsonObject dictionaryJson;
	private Map<String,String> dictionaryData;
	private int responseCode;

	@Override
	public String getResponseMessage() {
		return dictionaryMessage;
	}

	@Override
	public JsonObject getDictionaryJson() {
		return dictionaryJson;
	}

	@Override
	public Map<String, String> getDictionaryData() {
		return dictionaryData;
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseMessage(String dictionaryMessage) {
		this.dictionaryMessage = dictionaryMessage;
	}

	public void setDictionaryJson(JsonObject dictionaryJson) {
		this.dictionaryJson = dictionaryJson;
	}

	public void setDictionaryData(Map<String, String> dictionaryData) {
		this.dictionaryData = dictionaryData;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}



}
