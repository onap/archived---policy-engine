/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="VarbindDictionary")
@NamedQuery(name = "VarbindDictionary.findAll", query = "Select v FROM VarbindDictionary v")
public class VarbindDictionary implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int id;

    @Column(name ="varbind_Name", nullable = false, unique = true)
    @OrderBy("asc")
    private String varbindName;

    @Column(name = "varbind_Description", nullable = true, length = 2048)
    private String varbindDescription;

    @Column(name = "varbind_oid", nullable = false)
    private String varbindOID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date", updatable=false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_date", nullable=false)
    private Date modifiedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name="created_by")
    private UserInfo userCreatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name="modified_by")
    private UserInfo userModifiedBy;

    public UserInfo getUserCreatedBy() {
        return userCreatedBy;
    }

    public void setUserCreatedBy(UserInfo userCreatedBy) {
        this.userCreatedBy = userCreatedBy;
    }

    public UserInfo getUserModifiedBy() {
        return userModifiedBy;
    }

    public void setUserModifiedBy(UserInfo userModifiedBy) {
        this.userModifiedBy = userModifiedBy;
    }

    @PrePersist
    public void	prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVarbindName() {
        return varbindName;
    }

    public void setVarbindName(String varbindName) {
        this.varbindName = varbindName;
    }

    public String getVarbindDescription() {
        return varbindDescription;
    }

    public void setVarbindDescription(String varbindDescription) {
        this.varbindDescription = varbindDescription;
    }

    public String getVarbindOID() {
        return varbindOID;
    }

    public void setVarbindOID(String varbindOID) {
        this.varbindOID = varbindOID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


}
