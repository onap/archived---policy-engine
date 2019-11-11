/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="ClosedLoops")
@NamedQueries({
    @NamedQuery(name="ClosedLoops.findAll", query="SELECT e FROM ClosedLoops e"),
    @NamedQuery(name="ClosedLoops.deleteAll", query="DELETE FROM ClosedLoops WHERE 1=1")
})
public class ClosedLoops implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7796845092457926842L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="closedLoopControlName", nullable=false, length=255)
    @OrderBy("asc")
    private String closedLoopControlName;

    @Column(name="alarmConditions", nullable=true, length=255)
    private String alarmConditions;

    @Column(name="yaml", nullable=true, length=1028)
    private String yaml;

    public ClosedLoops() {
        //An empty constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClosedLoopControlName() {
        return closedLoopControlName;
    }

    public void setClosedLoopControlName(String closedLoopControlName) {
        this.closedLoopControlName = closedLoopControlName;
    }

    public String getAlarmConditions() {
        return alarmConditions;
    }

    public void setAlarmConditions(String alarmConditions) {
        this.alarmConditions = alarmConditions;
    }

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }

}