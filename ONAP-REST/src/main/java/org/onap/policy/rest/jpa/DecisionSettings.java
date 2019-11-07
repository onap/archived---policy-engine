/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
import javax.persistence.Transient;

/**
 * The Class DecisionSettings.
 */
// @formatter:off
@Entity
@Table(name = "DecisionSettings")
@NamedQuery(
    name = "DecisionSettings.findAll",
    query = "SELECT a FROM DecisionSettings a order by  a.priority asc, a.xacmlId asc"
)
//@formatter:on
public class DecisionSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "description", nullable = true, length = 2048)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    @Column(name = "PRIORITY", nullable = true)
    @OrderBy("asc")
    private String priority;

    @Column(name = "xacml_id", unique = true, nullable = false)
    @OrderBy("asc")
    private String xacmlId = "urn";

    // bi-directional many-to-one association to Datatype
    @ManyToOne
    @JoinColumn(name = "datatype")
    private Datatype datatypeBean;

    @Transient
    private String issuer = null;

    @Transient
    private boolean mustBePresent = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private UserInfo userCreatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modified_by")
    private UserInfo userModifiedBy;

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
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return this.id;
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
     * Gets the created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return this.createdDate;
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
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
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
     * Gets the modified date.
     *
     * @return the modified date
     */
    public Date getModifiedDate() {
        return this.modifiedDate;
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
     * Gets the xacml id.
     *
     * @return the xacml id
     */
    public String getXacmlId() {
        return this.xacmlId;
    }

    /**
     * Sets the xacml id.
     *
     * @param xacmlId the new xacml id
     */
    public void setXacmlId(String xacmlId) {
        this.xacmlId = xacmlId;
    }

    /**
     * Gets the datatype bean.
     *
     * @return the datatype bean
     */
    public Datatype getDatatypeBean() {
        return this.datatypeBean;
    }

    /**
     * Sets the datatype bean.
     *
     * @param datatypeBean the new datatype bean
     */
    public void setDatatypeBean(Datatype datatypeBean) {
        this.datatypeBean = datatypeBean;
    }

    /**
     * Gets the issuer.
     *
     * @return the issuer
     */
    @Transient
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the issuer.
     *
     * @param issuer the new issuer
     */
    @Transient
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Checks if is must be present.
     *
     * @return true, if is must be present
     */
    @Transient
    public boolean isMustBePresent() {
        return mustBePresent;
    }

    /**
     * Sets the must be present.
     *
     * @param mustBePresent the new must be present
     */
    @Transient
    public void setMustBePresent(boolean mustBePresent) {
        this.mustBePresent = mustBePresent;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }
}
