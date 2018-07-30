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

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the FunctionDefinition database table.
 * 
 */
@Entity
@Table(name="FunctionDefinition")
@NamedQueries({
    @NamedQuery(name="FunctionDefinition.findAll", query="SELECT f FROM FunctionDefinition f")
})
public class FunctionDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="short_name", nullable=false, length=64)
    private String shortname;

    @Column(name="xacml_id", nullable=false, length=255)
    private String xacmlid;

    //bi-directional many-to-one association to Datatype
    @ManyToOne
    @JoinColumn(name="return_datatype", nullable=true)
    private Datatype datatypeBean;

    @Column(name="is_bag_return", nullable=false)
    private Integer isBagReturn;

    @Column(name="is_higher_order", nullable=false)
    private Integer isHigherOrder;

    @Column(name="arg_lb", nullable=false)
    private Integer argLb;

    @Column(name="arg_ub", nullable=false)
    private Integer argUb;

    @Column(name="ho_arg_lb", nullable=true)
    private Integer higherOrderArg_LB;

    @Column(name="ho_arg_ub", nullable=true)
    private Integer higherOrderArg_UB;

    @Column(name="ho_primitive", nullable=true)
    private Character higherOrderIsPrimitive;

    //bi-directional many-to-one association to FunctionArgument
    @OneToMany(mappedBy="functionDefinition")
    private List<FunctionArgument> functionArguments;

    public FunctionDefinition() {
        //An empty constructor
    }

    public int getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getArgLb() {
        return this.argLb;
    }

    public void setArgLb(Integer argLb) {
        this.argLb = argLb;
    }

    public int getArgUb() {
        return this.argUb;
    }

    public void setArgUb(Integer argUb) {
        this.argUb = argUb;
    }

    public int getIsBagReturn() {
        return isBagReturn;
    }

    public void setIsBagReturn(Integer isBagReturn) {
        this.isBagReturn = isBagReturn;
    }

    public int getIsHigherOrder() {
        return isHigherOrder;
    }

    public void setIsHigherOrder(Integer isHigherOrder) {
        this.isHigherOrder = isHigherOrder;
    }

    public Datatype getDatatypeBean() {
        return this.datatypeBean;
    }

    public void setDatatypeBean(Datatype datatypeBean) {
        this.datatypeBean = datatypeBean;
    }

    public String getShortname() {
        return this.shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getXacmlid() {
        return this.xacmlid;
    }

    public void setXacmlid(String xacmlid) {
        this.xacmlid = xacmlid;
    }

    public int getHigherOrderArg_LB() {
        return higherOrderArg_LB;
    }

    public void setHigherOrderArg_LB(Integer higherOrderArg_LB) {
        this.higherOrderArg_LB = higherOrderArg_LB;
    }

    public int getHigherOrderArg_UB() {
        return higherOrderArg_UB;
    }

    public void setHigherOrderArg_UB(Integer higherOrderArg_UB) {
        this.higherOrderArg_UB = higherOrderArg_UB;
    }

    public Character getHigherOrderIsPrimitive() {
        return higherOrderIsPrimitive;
    }

    public void setHigherOrderIsPrimitive(Character higherOrderIsPrimitive) {
        this.higherOrderIsPrimitive = higherOrderIsPrimitive;
    }

    public List<FunctionArgument> getFunctionArguments() {
        return this.functionArguments;
    }

    public void setFunctionArguments(List<FunctionArgument> functionArguments) {
        this.functionArguments = functionArguments;
    }

    public FunctionArgument addFunctionArgument(FunctionArgument functionArgument) {
        getFunctionArguments().add(functionArgument);
        functionArgument.setFunctionDefinition(this);

        return functionArgument;
    }

    public FunctionArgument removeFunctionArgument(FunctionArgument functionArgument) {
        getFunctionArguments().remove(functionArgument);
        functionArgument.setFunctionDefinition(null);

        return functionArgument;
    }

    @Transient
    @Override
    public String toString() {
        return "FunctionDefinition [id=" + id + ", argLb=" + argLb + ", argUb="
                + argUb + ", isBagReturn=" + isBagReturn + ", isHigherOrder="
                + isHigherOrder + ", datatypeBean=" + datatypeBean
                + ", shortname=" + shortname + ", xacmlid=" + xacmlid
                + ", higherOrderArg_LB=" + higherOrderArg_LB
                + ", higherOrderArg_UB=" + higherOrderArg_UB
                + ", higherOrderIsPrimitive=" + higherOrderIsPrimitive
                + ", functionArguments=" + functionArguments + "]";
    }

    @Transient
    public boolean isBagReturn() {
        return this.isBagReturn == 1;
    }

    @Transient
    public boolean isHigherOrder() {
        return this.isHigherOrder == 1;
    }

}
