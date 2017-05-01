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
package org.openecomp.policy.models;

public class APIDictionaryResponse {
    private Object dictionaryData;
    private Object dictionaryJson;
    private int responseCode;
    private String responseMessage;
    public Object getDictionaryData() {
        return dictionaryData;
    }
    public void setDictionaryData(Object dictionaryData) {
        this.dictionaryData = dictionaryData;
    }
    public Object getDictionaryJson() {
        return dictionaryJson;
    }
    public void setDictionaryJson(Object dictionaryJson) {
        this.dictionaryJson = dictionaryJson;
    }
    public int getResponseCode() {
        return responseCode;
    }
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    
}
