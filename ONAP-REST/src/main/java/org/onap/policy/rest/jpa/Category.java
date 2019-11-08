/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The persistent class for the Categories database table.
 *
 */
@Entity
@Table(name = "Category")
@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c")
@Getter
@Setter
@ToString
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char STANDARD = 'S';
    public static final char CUSTOM = 'C';

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "grouping", nullable = false, length = 64)
    private String grouping;

    @Column(name = "is_standard", nullable = false)
    private char isStandard;

    @Column(name = "xacml_id", nullable = false, unique = true, length = 255)
    private String xacmlId;

    @Column(name = "short_name", nullable = false, length = 64)
    private String shortName;

    // bi-directional many-to-one association to Attribute
    @OneToMany(mappedBy = "categoryBean")
    @JsonBackReference
    private Set<Attribute> attributes = new HashSet<>();

    /**
     * Instantiates a new category.
     */
    public Category() {
        this.xacmlId = XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue();
        this.grouping = "subject";
        this.isStandard = Category.STANDARD;
        this.shortName = "subject";
    }

    /**
     * Instantiates a new category.
     *
     * @param cat the cat
     * @param grouping the grouping
     * @param isStandard the is standard
     */
    public Category(Identifier cat, String grouping, char isStandard) {
        if (cat != null) {
            this.xacmlId = cat.stringValue();
        }
        this.isStandard = isStandard;
        if (grouping != null) {
            this.grouping = grouping;
        } else {
            this.grouping = Category.extractGrouping(this.xacmlId);
        }
    }

    /**
     * Instantiates a new category.
     *
     * @param cat the cat
     * @param grouping the grouping
     */
    public Category(Identifier cat, String grouping) {
        this(cat, grouping, Category.STANDARD);
    }

    /**
     * Instantiates a new category.
     *
     * @param cat the cat
     * @param standard the standard
     */
    public Category(Identifier cat, char standard) {
        this(cat, null, standard);
    }

    /**
     * Instantiates a new category.
     *
     * @param cat the cat
     */
    public Category(Identifier cat) {
        this(cat, Category.STANDARD);
    }

    /**
     * Adds the attribute.
     *
     * @param attribute the attribute
     * @return the attribute
     */
    public Attribute addAttribute(Attribute attribute) {
        getAttributes().add(attribute);
        attribute.setCategoryBean(this);

        return attribute;
    }

    /**
     * Removes the attribute.
     *
     * @param attribute the attribute
     * @return the attribute
     */
    public Attribute removeAttribute(Attribute attribute) {
        getAttributes().remove(attribute);
        attribute.setCategoryBean(null);

        return attribute;
    }

    /**
     * Checks if is standard.
     *
     * @return true, if is standard
     */
    @Transient
    public boolean isStandard() {
        return this.isStandard == Category.STANDARD;
    }

    /**
     * Checks if is custom.
     *
     * @return true, if is custom
     */
    @Transient
    public boolean isCustom() {
        return this.isStandard == Category.CUSTOM;
    }

    /**
     * Extract grouping.
     *
     * @param xacmlId the xacml id
     * @return the string
     */
    @Transient
    public static String extractGrouping(String xacmlId) {
        if (xacmlId == null) {
            return null;
        }
        String[] parts = xacmlId.split("[:]");
        if (xacmlId.matches(".*:attribute\\-category:.*")) {
            return parts[parts.length - 1];
        } else if (xacmlId.matches(".*:[a-zA-Z]+[\\-]category:.*")) {
            for (String part : parts) {
                int index = part.indexOf("-category");
                if (index > 0) {
                    return part.substring(0, index);
                }
            }
        }
        return null;
    }

    /**
     * Gets the identifer.
     *
     * @return the identifer
     */
    @Transient
    public Identifier getIdentifer() {
        return new IdentifierImpl(this.xacmlId);
    }
}
