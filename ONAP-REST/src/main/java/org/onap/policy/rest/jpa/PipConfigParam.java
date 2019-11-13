/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.rest.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The persistent class for the PipConfigParams database table.
 *
 */
@Entity
@Table(name = "PipConfigParams")
@NamedQuery(name = "PipConfigParam.findAll", query = "SELECT p FROM PipConfigParam p")
@Getter
@Setter
@ToString
/**
 * Instantiates a new PIP config param.
 */
@NoArgsConstructor
public class PipConfigParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "PARAM_NAME", nullable = false, length = 1024)
    private String paramName;

    @Column(name = "PARAM_VALUE", nullable = false, length = 2048)
    private String paramValue;

    @Column(name = "PARAM_DEFAULT", nullable = true, length = 2048)
    private String paramDefault = null;

    @Column(name = "REQUIRED", nullable = false)
    private char required = '0';

    // bi-directional many-to-one association to PipConfiguration
    @ManyToOne
    @JoinColumn(name = "PIP_ID")
    private PipConfiguration pipconfiguration;

    /**
     * Instantiates a new PIP config param.
     *
     * @param param the param
     */
    public PipConfigParam(String param) {
        this.paramName = param;
    }

    /**
     * Instantiates a new PIP config param.
     *
     * @param param the param
     * @param value the value
     */
    public PipConfigParam(String param, String value) {
        this(param);
        this.paramValue = value;
    }

    /**
     * Instantiates a new PIP config param.
     *
     * @param param the param
     */
    public PipConfigParam(PipConfigParam param) {
        this(param.getParamName(), param.getParamValue());
        this.paramDefault = param.getParamDefault();
        this.required = param.required;
    }

    /**
     * Checks if is required.
     *
     * @return true, if is required
     */
    @Transient
    public boolean isRequired() {
        return this.required == '1';
    }

    /**
     * Sets the required flag.
     *
     * @param required the new required flag
     */
    @Transient
    public void setRequiredFlag(boolean required) {
        if (required) {
            this.setRequired('1');
        } else {
            this.setRequired('0');
        }
    }
}
