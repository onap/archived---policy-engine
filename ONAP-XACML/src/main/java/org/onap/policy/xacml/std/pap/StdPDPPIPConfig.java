/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.xacml.std.pap;

import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

@EqualsAndHashCode(callSuper=false)
@ToString
public class StdPDPPIPConfig implements PDPPIPConfig, Serializable {
    private static final long serialVersionUID = 1L;
    private static Logger logger = FlexLogger.getLogger(StdPDPPIPConfig.class);

    private String id;

    private String name;

    private String description;

    private String classname;

    private Map<String, String> config = new HashMap<>();

    public StdPDPPIPConfig() {
        //
        // Default constructor
        //
    }

    public StdPDPPIPConfig(String id) {
        this.id = id;
    }

    /**
     * Constructor.
     *
     * @param id String
     * @param name String
     * @param description String
     */
    public StdPDPPIPConfig(String id, String name, String description) {
        this(id);
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor.
     *
     * @param id String
     * @param properties Properties
     */
    public StdPDPPIPConfig(String id, Properties properties) {
        this(id);
        if (!this.initialize(properties)) {
            throw new IllegalArgumentException("PIP Engine '" + id + "' has no classname property in config");
        }
    }

    /**
     * initialize.
     *
     * @param properties Properties
     * @return boolean
     */
    public boolean initialize(Properties properties) {
        boolean classnameSeen = false;
        for (Object key : properties.keySet()) {
            if (key.toString().startsWith(this.id + ".")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + key);
                }
                if (key.toString().equals(this.id + ".name")) {
                    this.name = properties.getProperty(key.toString());
                } else if (key.toString().equals(this.id + ".description")) {
                    this.description = properties.getProperty(key.toString());
                } else if (key.toString().equals(this.id + ".classname")) {
                    this.classname = properties.getProperty(key.toString());
                    classnameSeen = true;
                }
                // all properties, including the special ones located above, are included in the properties list
                this.config.put(key.toString(), properties.getProperty(key.toString()));
            }
        }
        return classnameSeen;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    @Override
    @JsonIgnore
    public Map<String, String> getConfiguration() {
        return Collections.unmodifiableMap(this.config);
    }

    public void setValues(Map<String, String> config) {
        this.config = config;
    }

    @Override
    @JsonIgnore
    public boolean isConfigured() {
        //
        // Also include this in the JSON I/O if it is a data field rather than calculated
        //
        return true;
    }

    //
    // Methods needed for JSON serialization/deserialization
    //

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

}
