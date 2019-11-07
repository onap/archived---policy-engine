/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

import com.fasterxml.jackson.annotation.JsonBackReference;

/*
 */
import java.io.Serializable;
import java.util.Date;

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

import lombok.EqualsAndHashCode;

/**
 * The Class ConfigurationDataEntity.
 */
// @formatter:off
@Entity
@Table(name = "ConfigurationDataEntity")
@NamedQueries(
    {
        @NamedQuery(name = "ConfigurationDataEntity.findAll", query = "SELECT e FROM ConfigurationDataEntity e "),
        @NamedQuery(name = "ConfigurationDataEntity.deleteAll", query = "DELETE FROM ConfigurationDataEntity WHERE 1=1")
    }
)
@EqualsAndHashCode
//@formatter:on

public class ConfigurationDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "configurationDataId")
    @JsonBackReference
    private long configurationDataId;

    @Column(name = "configurationName", nullable = false, length = 255)
    private String configurationName = "";

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "configType", nullable = false, length = 255)
    private String configType = "NoType";

    @Lob
    @Column(name = "configBody", nullable = false, columnDefinition = "TEXT")
    private String configBody = "NoBody";

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "description", nullable = false, length = 2048)
    private String description = "NoDescription";

    @Column(name = "modified_by", nullable = false, length = 255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Instantiates a new configuration data entity.
     */
    public ConfigurationDataEntity() {
        // An empty constructor
    }

    /**
     * Pre persist.
     */
    @PrePersist
    public void prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    /**
     * Pre update.
     */
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }

    /**
     * Gets the configuration data id.
     *
     * @return the configurationDataId
     */
    public long getConfigurationDataId() {
        return configurationDataId;
    }

    /**
     * Sets the configuration name.
     *
     * @param configurationName the new configuration name
     */
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * Gets the configuration name.
     *
     * @return the configuration name
     */
    public String getConfigurationName() {
        return this.configurationName;
    }

    /**
     * Gets the config type.
     *
     * @return the configType
     */
    public String getConfigType() {
        return configType;
    }

    /**
     * Sets the config type.
     *
     * @param configType the configType to set
     */
    public void setConfigType(String configType) {
        this.configType = configType;
    }

    /**
     * Gets the config body.
     *
     * @return the configBody
     */
    public String getConfigBody() {
        return configBody;
    }

    /**
     * Sets the config body.
     *
     * @param configBody the configBody to set
     */
    public void setConfigBody(String configBody) {
        this.configBody = configBody;
    }

    /**
     * Gets the created by.
     *
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the modified by.
     *
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the modified by.
     *
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Gets the modified date.
     *
     * @return the modifiedDate
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the modified date.
     *
     * @param modifiedDate the modifiedDate to set
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Gets the created date.
     *
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Checks if is deleted.
     *
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deleted.
     *
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
