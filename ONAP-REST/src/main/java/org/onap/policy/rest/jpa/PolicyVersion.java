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
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="PolicyVersion")
@NamedQueries({
    @NamedQuery(name="PolicyVersion.findAll", query="SELECT p FROM PolicyVersion p"),
    @NamedQuery(name="PolicyVersion.deleteAll", query="DELETE FROM PolicyVersion WHERE 1=1"),
    @NamedQuery(name="PolicyVersion.findByPolicyName", query="Select p from PolicyVersion p where p.policyName=:pname"),
    @NamedQuery(name="PolicyVersion.findAllCount", query="SELECT COUNT(p) FROM PolicyVersion p")
})
public class PolicyVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Column(name="id")
    private int id;

    @Column(name="POLICY_NAME", nullable=false, length=255)
    private String policyName;

    @Column(name="ACTIVE_VERSION")
    private int activeVersion;

    @Column(name="HIGHEST_VERSION")
    private int higherVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date", nullable=false)
    private Date createdDate;


    public int getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(int activeVersion) {
        this.activeVersion = activeVersion;
    }

    public int getHigherVersion() {
        return higherVersion;
    }

    public void setHigherVersion(int higherVersion) {
        this.higherVersion = higherVersion;
    }

    @Column(name="CREATED_BY", nullable=false, length=45)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_date", nullable=false)
    private Date modifiedDate;


    @Column(name="modified_by", nullable=false, length=45)
    private String modifiedBy;

    public PolicyVersion(String domain, String loginUserId) {
        this(domain);
        this.createdBy = loginUserId;
        this.modifiedBy = loginUserId;
    }

    public PolicyVersion(String domain) {
        this.policyName = domain;
    }

    public PolicyVersion(){
        // Empty constructor
    }

    @PrePersist
    public void	prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate =  new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public int hashCode() {
    return Objects.hash(id, policyName,	activeVersion, higherVersion, createdDate,
            createdBy, modifiedDate, modifiedBy);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof PolicyVersion)){
            return false;
        }

        PolicyVersion p = (PolicyVersion) obj;

        return id == p.id &&
                policyName.equals(p.policyName) &&
                activeVersion == p.activeVersion &&
                higherVersion == p.higherVersion &&
                createdDate.equals(p.createdDate) &&
                createdBy.equals(p.createdBy) &&
                modifiedDate.equals(p.modifiedDate) &&
                modifiedBy.equals(p.modifiedBy);
    }

}

