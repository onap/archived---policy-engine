/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;
/*
 * JPA for the Micro Service Models.
 *
 * @version: 0.1
 */

import lombok.Getter;
import lombok.Setter;

// @formatter:off
@Entity
@Table(name = "MicroServiceModels")
@NamedQueries(
    {
        @NamedQuery(name = "MicroServiceModels.findAll", query = "SELECT b FROM MicroServiceModels b "),
        @NamedQuery(
                        name = "MicroServiceModels.findAllDecision",
                        query = "SELECT b FROM MicroServiceModels b WHERE b.decisionModel=1")
    }
)
@Getter
@Setter
// @formatter:on
public class MicroServiceModels implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "modelName", nullable = false, unique = true)
    @OrderBy("asc")
    private String modelName;

    @Column(name = "description", nullable = true, length = 2048)
    private String description;

    @Column(name = "dependency", nullable = true, length = 2048)
    private String dependency;

    @Column(name = "attributes", nullable = false, length = 255)
    private String attributes;

    @Column(name = "ref_attributes", nullable = false, length = 255)
    private String refAttributes;

    @Column(name = "sub_attributes", nullable = false, length = 2000)
    private String subAttributes;

    @Column(name = "dataOrderInfo", nullable = true, length = 2000)
    private String dataOrderInfo;

    @Column(name = "version", nullable = false, length = 2000)
    private String version;

    @Column(name = "enumValues", nullable = false, length = 2000)
    private String enumValues;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @Column(name = "decisionModel", nullable = true)
    private Boolean decisionModel = false;

    @Column(name = "ruleFormation", nullable = true)
    private String ruleFormation;

    @ManyToOne
    @JoinColumn(name = "imported_by")
    private UserInfo userCreatedBy;

    public boolean isDecisionModel() {
        return decisionModel;
    }
}
