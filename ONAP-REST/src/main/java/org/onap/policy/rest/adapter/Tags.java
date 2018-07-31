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

package org.onap.policy.rest.adapter;


import java.util.List;


public class Tags {
    String ruleName;
    List<TagDefines> tags;
    String tagPickerName;
    String networkRole;

    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public List<TagDefines> getTags() {
        return tags;
    }
    public void setTags(List<TagDefines> tags) {
        this.tags = tags;
    }
    public String getTagPickerName() {
        return tagPickerName;
    }

    public void setTagPickerName(String tagPickerName) {
        this.tagPickerName = tagPickerName;
    }
    public String getNetworkRole() {
        return networkRole;
    }
    public void setNetworkRole(String networkRole) {
        this.networkRole = networkRole;
    }

}
