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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
/*
 * The Entity class to persist a policy object Action Body
 */


import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="ActionBodyEntity")
@NamedQueries({
    @NamedQuery(name=" ActionBodyEntity.findAll", query="SELECT e FROM ActionBodyEntity e "),
    @NamedQuery(name="ActionBodyEntity.deleteAll", query="DELETE FROM ActionBodyEntity WHERE 1=1")
})

public class ActionBodyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="actionBodyId")
    @JsonBackReference
    private long actionBodyId;

    @Column(name="actionBodyName", nullable=false, length=255)
    private String actionBodyName = "";

    @Version
    @Column(name="version")
    private int version;

    @Lob
    @Column(name="actionBody", nullable=false, columnDefinition="TEXT")
    private String actionBody = "NoBody";

    @Column(name="created_by", nullable=false, length=255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date", updatable=false)
    private Date createdDate;

    @Column(name="modified_by", nullable=false, length=255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified_date", nullable=false)
    private Date modifiedDate;

    @Column(name="deleted", nullable=false)
    private boolean deleted = false;

    public ActionBodyEntity() {
        //An empty constructor
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
     * @return the configurationDataId
     */
    public long getActionBodyId() {
        return actionBodyId;
    }
    /**
     * @param name the configuration body name to set
     */
    public void setActionBodyName(String name) {
        this.actionBodyName = name;
    }
    public String getActionBodyName(){
        return this.actionBodyName;
    }

    /**
     * @return the actionBody
     */
    public String getActionBody() {
        return actionBody;
    }
    /**
     * @param body the configBody to set
     */
    public void setActionBody(String body) {
        this.actionBody = body;
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
     * @return the modifiedDate
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }
    /**
     * @param modifiedDate the modifiedDate to set
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
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
    return Objects.hash(actionBodyId, actionBodyName, version, actionBody,
            createdBy, createdDate, modifiedBy, modifiedDate, deleted);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof ActionBodyEntity)){
            return false;
        }

        return  actionBodyId == ((ActionBodyEntity) obj).actionBodyId &&
                actionBodyName.equals(((ActionBodyEntity) obj).actionBodyName) &&
                version == ((ActionBodyEntity) obj).version &&
                actionBody.equals(((ActionBodyEntity) obj).actionBody) &&
                createdBy.equals(((ActionBodyEntity) obj).createdBy) &&
                createdDate.equals(((ActionBodyEntity) obj).createdDate) &&
                modifiedBy.equals(((ActionBodyEntity) obj).modifiedBy) &&
                modifiedDate.equals(((ActionBodyEntity) obj).modifiedDate) &&
                deleted == ((ActionBodyEntity) obj).deleted;
    }
}
