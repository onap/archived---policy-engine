/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name="MicroserviceHeaderdeFaults")
@NamedQuery(name="MicroserviceHeaderdeFaults.findAll", query="SELECT e FROM MicroserviceHeaderdeFaults e ")
public class MicroserviceHeaderdeFaults implements Serializable {
    private static final long serialVersionUID = 1L;

    private static String domain;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="onapName")
    private String onapName;

    @Column(name="guard")
    private String guard ;

    @Column(name="priority")
    private String priority;

    @Column(name="riskType")
    private String riskType ;

    @Column(name="riskLevel")

    private String riskLevel;

    @Column(name="modelName", nullable=false)
    @OrderBy("asc")
    private String modelName;

    @PrePersist
    public void	prePersist() {

    }
    @PreUpdate
    public void preUpdate() {
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public String getOnapName() {
        return onapName;
    }
    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }
    public String getGuard() {
        return guard;
    }
    public void setGuard(String guard) {
        this.guard = guard;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getRiskType() {
        return riskType;
    }
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
    public String getRiskLevel() {
        return riskLevel;
    }
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

}