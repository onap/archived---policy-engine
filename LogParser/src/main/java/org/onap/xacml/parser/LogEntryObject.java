/*-
 * ============LICENSE_START=======================================================
 * LogParser
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

package org.onap.xacml.parser;

import java.util.Date;

public class LogEntryObject {

    private String system;
    private String description;
    private Date date;
    private String remote;
    private String systemType;
    private LogType logType;

    public enum LogType {
        INFO, DEBUG, ERROR, SEVERE, WARN;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(final String system) {
        this.system = system;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(final String remote) {
        this.remote = remote;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(final String systemType) {
        this.systemType = systemType;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(final LogType logType) {
        this.logType = logType;
    }
}
