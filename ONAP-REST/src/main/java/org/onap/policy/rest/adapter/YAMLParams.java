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

import java.util.List;

public class YAMLParams {
    private String actor;
    private String recipe;
    private String clname;
    private String limit;
    private String min;
    private String max;
    private String timeWindow;
    private String timeUnits;
    private String guardActiveStart;
    private String guardActiveEnd;
    private List<String> blackList;
    private List<String> targets;

    public String getActor() {
        return actor;
    }
    public void setActor(String actor) {
        this.actor = actor;
    }
    public String getRecipe() {
        return recipe;
    }
    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }
    public String getLimit() {
        return limit;
    }
    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getTimeWindow() {
        return timeWindow;
    }
    public void setTimeWindow(String timeWindow) {
        this.timeWindow = timeWindow;
    }
    public String getGuardActiveStart() {
        return guardActiveStart;
    }
    public void setGuardActiveStart(String guardActiveStart) {
        this.guardActiveStart = guardActiveStart;
    }
    public String getGuardActiveEnd() {
        return guardActiveEnd;
    }
    public void setGuardActiveEnd(String guardActiveEnd) {
        this.guardActiveEnd = guardActiveEnd;
    }
    public List<String> getBlackList() {
        return blackList;
    }
    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }
    public String getClname() {
        return clname;
    }
    public void setClname(String clname) {
        this.clname = clname;
    }
    public String getTimeUnits() {
        return timeUnits;
    }
    public void setTimeUnits(String timeUnits) {
        this.timeUnits = timeUnits;
    }
    public List<String> getTargets() {
        return targets;
    }
    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}
