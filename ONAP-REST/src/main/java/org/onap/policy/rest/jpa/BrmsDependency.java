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

/**
 * The Class BrmsDependency.
 */
@Entity
@Table(name = "BrmsDependency")
@NamedQuery(name = "BrmsDependency.findAll", query = "SELECT b from BrmsDependency b ")
public class BrmsDependency implements Serializable {
    private static final long serialVersionUID = -7005622785653160761L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "dependency_name", nullable = false, length = 1024, unique = true)
    @OrderBy("asc")
    private String dependencyName;

    @Column(name = "description", nullable = true, length = 1024)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private UserInfo userCreatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modified_by")
    private UserInfo userModifiedBy;

    @Column(name = "dependency", nullable = false)
    private String dependency;

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
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the created date.
     *
     * @param createdDate the new created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets the user created by.
     *
     * @return the user created by
     */
    public UserInfo getUserCreatedBy() {
        return userCreatedBy;
    }

    /**
     * Sets the user created by.
     *
     * @param userCreatedBy the new user created by
     */
    public void setUserCreatedBy(UserInfo userCreatedBy) {
        this.userCreatedBy = userCreatedBy;
    }

    /**
     * Gets the modified date.
     *
     * @return the modified date
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the modified date.
     *
     * @param modifiedDate the new modified date
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Gets the user modified by.
     *
     * @return the user modified by
     */
    public UserInfo getUserModifiedBy() {
        return userModifiedBy;
    }

    /**
     * Sets the user modified by.
     *
     * @param userModifiedBy the new user modified by
     */
    public void setUserModifiedBy(UserInfo userModifiedBy) {
        this.userModifiedBy = userModifiedBy;
    }

    /**
     * Gets the dependency.
     *
     * @return the dependency
     */
    public String getDependency() {
        return dependency;
    }

    /**
     * Sets the dependency.
     *
     * @param dependency the new dependency
     */
    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the dependency name.
     *
     * @return the dependency name
     */
    public String getDependencyName() {
        return dependencyName;
    }

    /**
     * Sets the dependency name.
     *
     * @param dependencyName the new dependency name
     */
    public void setDependencyName(String dependencyName) {
        this.dependencyName = dependencyName;
    }
}
