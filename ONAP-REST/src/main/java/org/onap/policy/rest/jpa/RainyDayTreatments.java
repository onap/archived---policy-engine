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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * The persistent class for the RainyDayTreatment database table.
 * 
 */
@Entity
@Table(name="RainyDayTreatments")
@NamedQuery(name="RainyDayTreatments.findAll", query="SELECT e FROM RainyDayTreatments e")
public class RainyDayTreatments implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2491410352490381323L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="bbid", nullable=false, length=255)
    @OrderBy("asc")
    private String bbid;

    @Column(name="workstep", nullable=true, length=255)
    private String workstep;

    @Column(name="treatments", nullable=true, length=1028)
    private String treatments;

    public RainyDayTreatments() {
        // Empty constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the bbid
     */
    public String getBbid() {
        return bbid;
    }

    /**
     * @param bbid the bbid to set
     */
    public void setBbid(String bbid) {
        this.bbid = bbid;
    }

    /**
     * @return the workstep
     */
    public String getWorkstep() {
        return workstep;
    }

    /**
     * @param workstep the workstep to set
     */
    public void setWorkstep(String workstep) {
        this.workstep = workstep;
    }

    /**
     * @return the treatments
     */
    public String getTreatments() {
        return treatments;
    }

    /**
     * @param treatments the treatments to set
     */
    public void setTreatments(String treatments) {
        this.treatments = treatments;
    }


}