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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/*
 * The Entity class to persist a policy object and its configuration data
 */

/**
 *
 */
@Entity
//Add a non-unique index and a constraint that says the combo of policyName and scopeId must be unique
@Table(name="PdpEntity")
@NamedQueries({
    @NamedQuery(name="PdpEntity.findAll", query="SELECT e FROM PdpEntity e "),
    @NamedQuery(name="PdpEntity.deleteAll", query="DELETE FROM PdpEntity WHERE 1=1")
})

public class PdpEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seqPdp")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column (name="pdpKey")
    private long pdpKey;

    @Column (name="pdpId", nullable=false, unique=false, length=255)
    private String pdpId;

    @Column(name="pdpName", nullable=false, unique=false, length=255)
    private String pdpName;

    @Column(name="jmxPort", nullable=false, unique=false)
    private int jmxPort;


    @ManyToOne(optional=false)
    @JoinColumn(name="groupKey", referencedColumnName="groupKey")
    private GroupEntity groupEntity;

    @Column(name="created_by", nullable=false, length=255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date", updatable=false)
    private Date createdDate;

    @Column(name="description", nullable=false, length=2048)
    private String description = "NoDescription";

    @Column(name="modified_by", nullable=false, length=255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_date", nullable=false)
    private Date modifiedDate;

    @Column(name="deleted", nullable=false)
    private boolean deleted = false;

    public PdpEntity() {
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

    public long getPdpKey(){
        return pdpKey;
    }
    /**
     * @return the policyId
     */
    public String getPdpId() {
        return pdpId;
    }

    public void setPdpId(String id){
        pdpId = id;
    }
    /**
     * @param policyId cannot be set
     */

    public String getPdpName() {
        return pdpName;
    }

    public void setPdpName(String groupName) {
        this.pdpName = groupName;
    }



    /**
     * @return the configurationDataEntity
     */
    public GroupEntity getGroup() {
        return groupEntity;
    }

    /**
     * @param configurationDataEntity the configurationDataEntity to set
     */
    public void setGroup(GroupEntity group) {
        this.groupEntity = group;
    }



    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the version
     */
    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(int jmxPort){
        this.jmxPort = jmxPort;
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

    /**
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


}
