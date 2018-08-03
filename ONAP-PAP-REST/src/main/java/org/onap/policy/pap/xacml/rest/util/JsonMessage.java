/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.util;

public class JsonMessage {

    private String data;
    private String data2;
    private String data3;
    public JsonMessage(String data) {
        super();
        this.data = data;
    }
    public JsonMessage(String data,String data2) {
        super();
        this.data = data;
        this.data2 = data2;
    }

    public JsonMessage(String data,String data2,String data3) {
        super();
        this.data = data;
        this.data2 = data2;
        this.data3 = data3;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getData2() {
        return data2;
    }
    public void setData2(String data2) {
        this.data2 = data2;
    }
    public String getData3() {
        return data3;
    }
    public void setData3(String data3) {
        this.data3 = data3;
    }


}

