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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The persistent class for the FunctionDefinition database table.
 *
 */
// @formatter:off
@Entity
@Table(name = "FunctionDefinition")
@NamedQueries(
    {
        @NamedQuery(name = "FunctionDefinition.findAll", query = "SELECT f FROM FunctionDefinition f")
    }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
// @formatter:on
public class FunctionDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "short_name", nullable = false, length = 64)
    private String shortname;

    @Column(name = "xacml_id", nullable = false, length = 255)
    private String xacmlid;

    // bi-directional many-to-one association to Datatype
    @ManyToOne
    @JoinColumn(name = "return_datatype", nullable = true)
    private Datatype datatypeBean;

    @Column(name = "is_bag_return", nullable = false)
    private Integer isBagReturn;

    @Column(name = "is_higher_order", nullable = false)
    private Integer isHigherOrder;

    @Column(name = "arg_lb", nullable = false)
    private Integer argLb;

    @Column(name = "arg_ub", nullable = false)
    private Integer argUb;

    @Column(name = "ho_arg_lb", nullable = true)
    private Integer higherOrderArgLb;

    @Column(name = "ho_arg_ub", nullable = true)
    private Integer higherOrderArgUb;

    @Column(name = "ho_primitive", nullable = true)
    private Character higherOrderIsPrimitive;

    // bi-directional many-to-one association to FunctionArgument
    @OneToMany(mappedBy = "functionDefinition")
    private List<FunctionArgument> functionArguments;

    /**
     * Adds the function argument.
     *
     * @param functionArgument the function argument
     * @return the function argument
     */
    public FunctionArgument addFunctionArgument(FunctionArgument functionArgument) {
        getFunctionArguments().add(functionArgument);
        functionArgument.setFunctionDefinition(this);

        return functionArgument;
    }

    /**
     * Removes the function argument.
     *
     * @param functionArgument the function argument
     * @return the function argument
     */
    public FunctionArgument removeFunctionArgument(FunctionArgument functionArgument) {
        getFunctionArguments().remove(functionArgument);
        functionArgument.setFunctionDefinition(null);

        return functionArgument;
    }

    /**
     * Checks if is bag return.
     *
     * @return true, if is bag return
     */
    @Transient
    public boolean isBagReturn() {
        return this.isBagReturn == 1;
    }

    /**
     * Checks if is higher order.
     *
     * @return true, if is higher order
     */
    @Transient
    public boolean isHigherOrder() {
        return this.isHigherOrder == 1;
    }

}
