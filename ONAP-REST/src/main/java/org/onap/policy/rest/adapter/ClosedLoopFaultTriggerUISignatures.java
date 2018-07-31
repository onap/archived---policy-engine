/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.rest.adapter;



public class ClosedLoopFaultTriggerUISignatures {

    private String signatures;

    private String connectSignatures;

    public String getConnectSignatures() {
        return connectSignatures;
    }
    public void setConnectSignatures(String connectSignatures) {
        this.connectSignatures = connectSignatures;
    }
    public String getSignatures() {
        return signatures;
    }
    public void setSignatures(String signatures) {
        this.signatures = signatures;
    }

}
