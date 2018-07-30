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
 * The Entity class to persist a policy object configuration data
 */


import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="ConfigurationDataEntity")
@NamedQueries({
    @NamedQuery(name="ConfigurationDataEntity.findAll", query="SELECT e FROM ConfigurationDataEntity e "),
    @NamedQuery(name="ConfigurationDataEntity.deleteAll", query="DELETE FROM ConfigurationDataEntity WHERE 1=1")
})

public class ConfigurationDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="configurationDataId")
    @JsonBackReference
    private long configurationDataId;

    @Column(name="configurationName", nullable=false, length=255)
    private String configurationName = "";

    @Version
    @Column(name="version")
    private int version;

    @Column(name="configType", nullable=false, length=255)
    private String configType = "NoType";

    @Lob
    @Column(name="configBody", nullable=false, columnDefinition="TEXT")
    private String configBody = "NoBody";

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

    public ConfigurationDataEntity() {
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
    public long getConfigurationDataId() {
        return configurationDataId;
    }
    /**
     * @param configurationDataId the configurationDataId to set
     */
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }
    public String getConfigurationName(){
        return this.configurationName;
    }
    /**
     * @return the configType
     */
    public String getConfigType() {
        return configType;
    }
    /**
     * @param configType the configType to set
     */
    public void setConfigType(String configType) {
        this.configType = configType;
    }
    /**
     * @return the configBody
     */
    public String getConfigBody() {
        return configBody;
    }
    /**
     * @param configBody the configBody to set
     */
    public void setConfigBody(String configBody) {
        this.configBody = configBody;
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
    return Objects.hash(configurationDataId, configurationName,	version, configType,
            configBody, createdBy, createdDate, description, modifiedBy, modifiedDate, deleted);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof ConfigurationDataEntity)){
            return false;
        }

        return configurationDataId == ((ConfigurationDataEntity) obj).configurationDataId &&
                configurationName.equals(((ConfigurationDataEntity) obj).configurationName) &&
                version == ((ConfigurationDataEntity) obj).version &&
                configType.equals(((ConfigurationDataEntity) obj).configType) &&
                configBody.equals(((ConfigurationDataEntity) obj).configBody) &&
                createdBy.equals(((ConfigurationDataEntity) obj).createdBy) &&
                createdDate.equals(((ConfigurationDataEntity) obj).createdDate) &&
                description.equals(((ConfigurationDataEntity) obj).description) &&
                modifiedBy.equals(((ConfigurationDataEntity) obj).modifiedBy) &&
                modifiedDate.equals(((ConfigurationDataEntity) obj).modifiedDate) &&
                deleted == ((ConfigurationDataEntity) obj).deleted;
    }
}
