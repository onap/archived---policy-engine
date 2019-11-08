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
 * The persistent class for the FunctionArguments database table.
 *
 */
@Entity
@Table(name = "FunctionArguments")
@NamedQuery(name = "FunctionArgument.findAll", query = "SELECT f FROM FunctionArgument f")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FunctionArgument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "is_bag", nullable = false)
    private int isBag;

    // bi-directional many-to-one association to FunctionDefinition
    @ManyToOne
    @JoinColumn(name = "function_id")
    private FunctionDefinition functionDefinition;

    @Column(name = "arg_index", nullable = false)
    private int argIndex;

    // bi-directional many-to-one association to Datatype
    @ManyToOne
    @JoinColumn(name = "datatype_id")
    private Datatype datatypeBean;

    /**
     * Copy constructor.
     *
     * @param argument the object to copy from
     */
    public FunctionArgument(final FunctionArgument argument) {
        this.argIndex = argument.argIndex;
        this.datatypeBean = argument.datatypeBean;
        this.isBag = argument.isBag;
        this.functionDefinition = argument.functionDefinition;
    }

    public int getIsBag() {
        return isBag;
    }

    @Transient
    public boolean isBag() {
        return this.isBag == 1;
    }
}
