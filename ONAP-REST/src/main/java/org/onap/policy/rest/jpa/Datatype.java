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

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
 * The persistent class for the Datatype database table.
 *
 */
@Entity
@Table(name = "Datatype")
@NamedQuery(name = "Datatype.findAll", query = "SELECT d FROM Datatype d")
@Getter
@Setter
@ToString
public class Datatype implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char STANDARD = 'S';
    public static final char CUSTOM = 'C';

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "is_standard", nullable = false)
    private char isStandard;

    @Column(name = "xacml_id", nullable = false, unique = true, length = 255)
    private String xacmlId;

    @Column(name = "short_name", nullable = false, length = 64)
    private String shortName;

    // bi-directional many-to-one association to Attribute
    @OneToMany(mappedBy = "datatypeBean")
    @JsonBackReference
    private Set<Attribute> attributes = new HashSet<>();

    // bi-directional many-to-one association to Attribute
    @OneToMany(mappedBy = "datatypeBean")
    @JsonIgnore
    private Set<FunctionDefinition> functions = new HashSet<>();

    // bi-directional many-to-one association to Attribute
    @OneToMany(mappedBy = "datatypeBean")
    @JsonIgnore
    private Set<FunctionArgument> arguments = new HashSet<>();

    /**
     * Instantiates a new datatype.
     */
    public Datatype() {
        this.xacmlId = XACML3.ID_DATATYPE_STRING.stringValue();
        this.isStandard = Datatype.STANDARD;
    }

    /**
     * Instantiates a new datatype.
     *
     * @param id the id
     * @param dt the dt
     */
    public Datatype(int id, Datatype dt) {
        this.id = id;
        this.isStandard = dt.isStandard;
        this.xacmlId = dt.xacmlId;
        this.shortName = dt.shortName;
        //
        // Make a copy?
        //
        this.attributes = new HashSet<>();
    }

    /**
     * Instantiates a new datatype.
     *
     * @param identifier the identifier
     * @param standard the standard
     */
    public Datatype(Identifier identifier, char standard) {
        if (identifier != null) {
            this.xacmlId = identifier.stringValue();

        }
        this.isStandard = standard;
    }

    /**
     * Instantiates a new datatype.
     *
     * @param identifier the identifier
     */
    public Datatype(Identifier identifier) {
        this(identifier, Datatype.STANDARD);
    }

    /**
     * Adds the attribute.
     *
     * @param attribute the attribute
     * @return the attribute
     */
    public Attribute addAttribute(Attribute attribute) {
        getAttributes().add(attribute);
        attribute.setDatatypeBean(this);

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
        attribute.setDatatypeBean(null);

        return attribute;
    }

    /**
     * Removes the attribute.
     *
     * @param function the function
     * @return the function definition
     */
    public FunctionDefinition removeAttribute(FunctionDefinition function) {
        getFunctions().remove(function);
        function.setDatatypeBean(null);

        return function;
    }

    /**
     * Adds the function.
     *
     * @param function the function
     * @return the function definition
     */
    public FunctionDefinition addFunction(FunctionDefinition function) {
        getFunctions().add(function);
        function.setDatatypeBean(this);

        return function;
    }

    /**
     * Adds the argument.
     *
     * @param argument the argument
     * @return the function argument
     */
    public FunctionArgument addArgument(FunctionArgument argument) {
        getArguments().add(argument);
        argument.setDatatypeBean(this);

        return argument;
    }

    /**
     * Removes the argument.
     *
     * @param argument the argument
     * @return the function argument
     */
    public FunctionArgument removeArgument(FunctionArgument argument) {
        getArguments().remove(argument);
        argument.setDatatypeBean(null);

        return argument;
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

    /**
     * Gets the identifer by short name.
     *
     * @return the identifer by short name
     */
    @Transient
    public Identifier getIdentiferByShortName() {
        return new IdentifierImpl(this.shortName);
    }

    /**
     * Checks if is standard.
     *
     * @return true, if is standard
     */
    @Transient
    public boolean isStandard() {
        return this.isStandard == Datatype.STANDARD;
    }

    /**
     * Checks if is custom.
     *
     * @return true, if is custom
     */
    @Transient
    public boolean isCustom() {
        return this.isStandard == Datatype.CUSTOM;
    }
}
