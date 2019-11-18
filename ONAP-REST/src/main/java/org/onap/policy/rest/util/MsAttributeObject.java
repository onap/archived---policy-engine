/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017,2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MsAttributeObject {

    private String className;
    private Map<String, String> attribute = new HashMap<>();
    private Map<String, String> refAttribute = new HashMap<>();
    private Map<String, Object> subClass = new HashMap<>();
    private String dependency;
    private Map<String, String> enumType = new HashMap<>();
    private Map<String, String> matchingSet = new HashMap<>();
    private boolean policyTempalate;
    private String ruleFormation;
    private String dataOrderInfo;

    public void addAttribute(String key, String value) {
        this.attribute.put(key, value);
    }

    public void addRefAttribute(String key, String value) {
        this.refAttribute.put(key, value);
    }

    public void addAllAttribute(Map<String, String> map) {
        this.attribute.putAll(map);
    }

    public void addAllRefAttribute(Map<String, String> map) {
        this.refAttribute.putAll(map);
    }

    public void addAllSubClass(Map<String, Object> subClass) {
        this.subClass.putAll(subClass);
    }

    public void addSingleEnum(String key, String value) {
        this.enumType.put(key, value);
    }

    public void addMatchingSet(String key, String value) {
        this.matchingSet.put(key, value);
    }

    public void addMatchingSet(Map<String, String> matchingSet) {
        this.matchingSet.putAll(matchingSet);
    }
}
