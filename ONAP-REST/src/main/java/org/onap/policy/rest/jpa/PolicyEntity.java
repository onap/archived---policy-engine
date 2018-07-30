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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/*
 * The Entity class to persist a policy object and its configuration data
 */

/**
 *
 */
@Entity
//Add a non-unique index and a constraint that says the combo of policyName and scopeId must be unique
@Table(name="PolicyEntity")

@NamedQueries({
    @NamedQuery(name="PolicyEntity.findAll", query="SELECT e FROM PolicyEntity e "),
    @NamedQuery(name="PolicyEntity.findAllByDeletedFlag", query="SELECT e FROM PolicyEntity e WHERE e.deleted = :deleted"),
    @NamedQuery(name="PolicyEntity.FindById", query="SELECT e FROM PolicyEntity e WHERE e.policyId = :id"),
    @NamedQuery(name="PolicyEntity.deleteAll", query="DELETE FROM PolicyEntity WHERE 1=1"),
    @NamedQuery(name="PolicyEntity.findByNameAndScope", query="SELECT e FROM PolicyEntity e WHERE e.policyName = :name AND e.scope = :scope")
})

public class PolicyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column (name="policyId")
    @JsonBackReference
    private long policyId;

    @Column(name="policyName", nullable=false, unique=false, length=255)
    private String policyName;

    //The scope is the directory structure in dot notation.  For example: org.onap.myproject
    @Column(name="scope", nullable=false, unique=false, length=255)
    private String scope;

    @Version
    @Column(name="version")
    private int version;

    //not going to be used
    @Column(name="policyVersion")
    private int policyVersion = 0;

    @Lob
    @Column(name="policyData", nullable=false, columnDefinition="TEXT")
    private String policyData = "NoData";

    @OneToOne(optional=true, orphanRemoval=true)
    @JoinColumn(name="configurationDataId")
    @JsonManagedReference
    private ConfigurationDataEntity configurationDataEntity;

    @OneToOne(optional=true, orphanRemoval=true)
    @JoinColumn(name="actionBodyId")
    @JsonManagedReference
    private ActionBodyEntity actionBodyEntity;

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

    public PolicyEntity() {
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
     * @return the policyId
     */
    public long getPolicyId() {
        return policyId;
    }

    /**
     * @param policyId cannot be set
     */

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * @return the policyData
     */
    public String getPolicyData() {
        return policyData;
    }

    /**
     * @param policyData the policyData to set
     */
    public void setPolicyData(String policyData) {
        this.policyData = policyData;
    }

    /**
     * @return the configurationDataEntity
     */
    public ConfigurationDataEntity getConfigurationData() {
        return configurationDataEntity;
    }

    /**
     * @param configurationDataEntity the configurationDataEntity to set
     */
    public void setConfigurationData(ConfigurationDataEntity configurationDataEntity) {
        this.configurationDataEntity = configurationDataEntity;
    }

    /**
     * @return the actionBodyEntity
     */
    public ActionBodyEntity getActionBodyEntity() {
        return actionBodyEntity;
    }

    /**
     * @param actionBodyEntity the actionBodyEntity to set
     */
    public void setActionBodyEntity(ActionBodyEntity actionBodyEntity) {
        this.actionBodyEntity = actionBodyEntity;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
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
    public int getVersion() {
        return version;
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

    @Override
    public int hashCode() {
    return Objects.hash(policyId, policyName, scope, version, policyVersion, policyData, configurationDataEntity,
            actionBodyEntity, createdBy, createdDate, description, modifiedBy, modifiedDate, deleted);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof PolicyEntity)){
            return false;
        }

        PolicyEntity p = (PolicyEntity) obj;

        return policyId == p.policyId &&
                policyName.equals(p.policyName) &&
                scope.equals(p.scope) &&
                version == p.version &&
                policyVersion == p.policyVersion &&
                policyData.equals(p.policyData) &&
                ((configurationDataEntity == null && p.configurationDataEntity == null) || (configurationDataEntity!=null && configurationDataEntity.equals(p.configurationDataEntity))) &&
                ((actionBodyEntity == null && p.actionBodyEntity == null) || (actionBodyEntity!=null && actionBodyEntity.equals(p.actionBodyEntity))) &&
                createdBy.equals(p.createdBy) &&
                createdDate.equals(p.createdDate) &&
                description.equals(p.description) &&
                modifiedBy.equals(p.modifiedBy) &&
                modifiedDate.equals(p.modifiedDate) &&
                deleted == p.deleted;
    }


}
