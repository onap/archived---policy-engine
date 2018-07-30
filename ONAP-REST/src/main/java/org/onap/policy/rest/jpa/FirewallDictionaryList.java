/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;


@Entity
@Table(name="parentdictionaryitems")
@NamedQuery(name="FirewallDictionaryList.findAll", query="SELECT e FROM FirewallDictionaryList e")
public class FirewallDictionaryList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="parentItemName", nullable=false)
    @OrderBy("asc")
    private String parentItemName;

    @Column(name="description")
    private String description;

    @Column(name="addressList")
    private String addressList;

    @Column(name="serviceList")
    private String serviceList;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentItemName() {
        return parentItemName;
    }

    public String getDescription() {
        return description;
    }

    public String getAddressList() {
        return addressList;
    }

    public String getServiceList() {
        return serviceList;
    }

    public void setParentItemName(String parentItemName) {
        this.parentItemName = parentItemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public void setServiceList(String serviceList) {
        this.serviceList = serviceList;
    }
}
