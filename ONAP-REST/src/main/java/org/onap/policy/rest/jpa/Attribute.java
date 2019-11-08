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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the Attribute database table.
 *
 */
@Entity
@Table(name = "Attribute")
@NamedQuery(name = "Attribute.findAll", query = "SELECT a FROM Attribute a order by  a.priority asc, a.xacmlId asc")

@Getter
@Setter
@NoArgsConstructor
public class Attribute implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    // bi-directional many-to-one association to Category
    @ManyToOne
    @JoinColumn(name = "constraint_type", nullable = true)
    @JsonIgnore
    private ConstraintType constraintType;

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

    @Column(name = "ATTRIBUTE_VALUE", nullable = true)
    @OrderBy("asc")
    private String attributeValue;

    @Column(name = "xacml_id", unique = true, nullable = false)
    @OrderBy("asc")
    private String xacmlId = "urn";

    // bi-directional many-to-one association to ConstraintValue
    @OneToMany(mappedBy = "attribute", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<ConstraintValue> constraintValues = new HashSet<>();

    // bi-directional many-to-one association to Category
    @ManyToOne
    @JoinColumn(name = "category")
    @JsonIgnore
    private Category categoryBean;

    // bi-directional many-to-one association to Datatype
    @ManyToOne
    @JoinColumn(name = "datatype")
    private Datatype datatypeBean;

    @Column(name = "is_designator", nullable = false)
    @JsonIgnore
    private char isDesignator = '1';

    @Column(name = "selector_path", nullable = true, length = 2048)
    private String selectorPath;

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
     * Constructor with domain.
     *
     * @param domain the domain to use to construct the object
     */
    public Attribute(String domain) {
        this.xacmlId = domain;
    }

    /**
     * Copy constructor.
     *
     * @param copy the copy to copy from
     */
    public Attribute(Attribute copy) {
        this(copy.getXacmlId() + ":(0)");
        this.constraintType = copy.getConstraintType();
        this.categoryBean = copy.getCategoryBean();
        this.datatypeBean = copy.getDatatypeBean();
        this.description = copy.getDescription();
        for (ConstraintValue value : copy.getConstraintValues()) {
            ConstraintValue newValue = new ConstraintValue(value);
            newValue.setAttribute(this);
            this.addConstraintValue(newValue);
        }
    }

    /**
     * Called before an instance is persisted.
     */
    @PrePersist
    public void prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    /**
     * Called before an instance is updated.
     */
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }

    /**
     * Sets the constraint values.
     *
     * @param constraintValues the new constraint values
     */
    public void setConstraintValues(Set<ConstraintValue> constraintValues) {
        if (this.constraintValues == null) {
            this.constraintValues = new HashSet<>();
        }
        for (ConstraintValue value : this.constraintValues) {
            value.setAttribute(this);
        }
        this.constraintValues = constraintValues;
    }

    /**
     * Adds the constraint value.
     *
     * @param constraintValue the constraint value
     * @return the constraint value
     */
    public ConstraintValue addConstraintValue(ConstraintValue constraintValue) {
        if (this.constraintValues == null) {
            this.constraintValues = new HashSet<>();
        }
        this.constraintValues.add(constraintValue);
        constraintValue.setAttribute(this);

        return constraintValue;
    }

    /**
     * Removes the constraint value.
     *
     * @param constraintValue the constraint value
     * @return the constraint value
     */
    public ConstraintValue removeConstraintValue(ConstraintValue constraintValue) {
        this.constraintValues.remove(constraintValue);
        constraintValue.setAttribute(null);

        return constraintValue;
    }

    /**
     * Removes the all constraint values.
     */
    public void removeAllConstraintValues() {
        if (this.constraintValues == null) {
            return;
        }
        for (ConstraintValue value : this.constraintValues) {
            value.setAttribute(null);
        }
        this.constraintValues.clear();
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
     * Checks if is designator.
     *
     * @return true, if is designator
     */
    @Transient
    public boolean isDesignator() {
        return this.isDesignator == '1';
    }

    /**
     * Sets the checks if is designator.
     *
     * @param is the new checks if is designator
     */
    @Transient
    public void setIsDesignator(boolean is) {
        if (is) {
            this.isDesignator = '1';
        } else {
            this.isDesignator = '0';
        }
    }
}
