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
 * The persistent class for the PipResolverParam database table.
 *
 */
@Entity
@Table(name = "PipResolverParams")
@NamedQuery(name = "PipResolverParam.findAll", query = "SELECT p FROM PipResolverParam p")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PipResolverParam implements Serializable {
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
    private String paramDefault;

    @Column(name = "REQUIRED", nullable = false)
    private char required = '0';

    // bi-directional many-to-one association to PipResolver
    @ManyToOne
    @JoinColumn(name = "ID_RESOLVER")
    private PipResolver pipresolver;

    /**
     * Instantiates a new PIP resolver param.
     *
     * @param name the name
     */
    public PipResolverParam(String name) {
        this.paramName = name;
    }

    /**
     * Instantiates a new PIP resolver param.
     *
     * @param name the name
     * @param value the value
     */
    public PipResolverParam(String name, String value) {
        this(name);
        this.paramValue = value;
    }

    /**
     * Instantiates a new PIP resolver param.
     *
     * @param param the param
     */
    public PipResolverParam(PipResolverParam param) {
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
     * Sets the required.
     *
     * @param required the new required
     */
    @Transient
    public void setRequired(boolean required) {
        if (required) {
            this.required = '1';
        } else {
            this.required = '0';
        }
    }
}
