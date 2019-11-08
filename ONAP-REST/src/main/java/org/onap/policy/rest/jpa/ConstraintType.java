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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ConstraintType")
@NamedQuery(name = "ConstraintType.findAll", query = "SELECT a FROM ConstraintType a")
@Getter
@Setter
@NoArgsConstructor
public class ConstraintType implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ENUMERATION_TYPE = "Enumeration";
    public static final String RANGE_TYPE = "Range";
    public static final String REGEXP_TYPE = "Regular Expression";

    protected static final Map<String, String> defaults = new HashMap<>();

    static {
        defaults.put(ENUMERATION_TYPE,
                        "Enumerate a set of values that the attribute may be set to during policy creation.");
        defaults.put(RANGE_TYPE, "Set a range of min and/or max integer/double values "
                        + "the attribute can be set to during policy creation.");
        defaults.put(REGEXP_TYPE,
                        "Define a regular expression the attribute must match against during policy creation.");
    }

    private static final String[] RANGE_TYPES =
        { "minExclusive", "minInclusive", "maxExclusive", "maxInclusive" };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "constraint_type", nullable = false, length = 64)
    private String constraintType;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    // bi-directional many-to-one association to Attribute
    @OneToMany(mappedBy = "constraintType")
    private Set<Attribute> attributes = new HashSet<>();

    public ConstraintType(String constraintType) {
        this();
        this.constraintType = constraintType;
    }

    public ConstraintType(String constraintType, String description) {
        this(constraintType);
        this.description = description;
    }

    public static String[] getRangeTypes() {
        return RANGE_TYPES;
    }

}
