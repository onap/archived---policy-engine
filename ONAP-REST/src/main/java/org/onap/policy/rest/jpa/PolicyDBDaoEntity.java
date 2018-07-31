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
/*
 */
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/*
 * The Entity class to persist a PolicyDBDaoEntity object for registration of PolicyDBDao
 */

/**
 *
 */
@Entity
@Table(name="PolicyDBDaoEntity")

@NamedQueries({
    @NamedQuery(name="PolicyDBDaoEntity.findAll", query="SELECT e FROM PolicyDBDaoEntity e "),
    @NamedQuery(name="PolicyDBDaoEntity.deleteAll", query="DELETE FROM PolicyDBDaoEntity WHERE 1=1")
})

public class PolicyDBDaoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="policyDBDaoUrl", nullable=false, unique=true)
    private String policyDBDaoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date", updatable=false)
    private Date createdDate;

    //username for the pap server that registered this PolicyDBDaoEntity
    @Column(name="username")
    private String username;

    //AES encrypted password for the pap server that registered this PolicyDBDaoEntity
    @Column(name="password")
    private String password;

    //A column to allow some descriptive text.  For example: Atlanta data center
    @Column(name="description", nullable=false, length=2048)
    private String description = "NoDescription";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_date", nullable=false)
    private Date modifiedDate;

    public PolicyDBDaoEntity() {
        super();
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

    /**
     * @return the policyDBDaoUrl
     */
    public String getPolicyDBDaoUrl() {
        return policyDBDaoUrl;
    }

    /**
     * @param url the policyDBDaoUrl to set
     */
    public void setPolicyDBDaoUrl(String url) {
        this.policyDBDaoUrl = url;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @return the modifiedDate
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
