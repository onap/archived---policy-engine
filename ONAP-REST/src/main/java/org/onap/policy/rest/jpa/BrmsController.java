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
 * The Class BRMSController.
 */
@Entity
@Table(name = "BrmsController")
@NamedQuery(name = "BrmsController.findAll", query = "SELECT b from BrmsController b ")
public class BrmsController implements Serializable {
    private static final long serialVersionUID = -8666947569754164177L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "controller_name", nullable = false, length = 1024, unique = true)
    @OrderBy("asc")
    private String controllerName;

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

    @Column(name = "controller", nullable = false)
    private String controller;

    /**
     * Called before persisting an instance.
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
     * Gets the controller.
     *
     * @return the controller
     */
    public String getController() {
        return controller;
    }

    /**
     * Sets the controller.
     *
     * @param controller the new controller
     */
    public void setController(String controller) {
        this.controller = controller;
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
     * Gets the controller name.
     *
     * @return the controller name
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * Sets the controller name.
     *
     * @param controllerName the new controller name
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

}
