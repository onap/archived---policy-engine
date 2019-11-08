/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the ConstraintValues database table.
 *
 */
@Entity
@Table(name = "ConstraintValues")
@NamedQuery(name = "ConstraintValue.findAll", query = "SELECT c FROM ConstraintValue c")
@Getter
@Setter
@NoArgsConstructor
public class ConstraintValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "property")
    private String property;

    @Column(name = "value")
    private String value;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    public ConstraintValue(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public ConstraintValue(ConstraintValue value) {
        this.property = value.getProperty();
        this.value = value.getValue();
    }

    @Override
    public ConstraintValue clone() {
        ConstraintValue constraint = new ConstraintValue();

        constraint.property = this.property;
        constraint.value = this.value;
        constraint.attribute = this.attribute;

        return constraint;
    }
}
