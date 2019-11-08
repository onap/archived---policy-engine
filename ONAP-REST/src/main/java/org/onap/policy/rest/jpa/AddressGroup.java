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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class AddressGroup is a JPA class for address groups.
 */
@Entity
@Table(name = "AddressGroup")
@NamedQuery(name = "AddressGroup.findAll", query = "SELECT e FROM AddressGroup e ")

@Getter
@Setter
@ToString
public class AddressGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    @OrderBy("asc")
    private String name;

    @Column(name = "prefixlist")
    private String prefixList;

    @Column(name = "description")
    private String description;

    /**
     * Get the group name.
     *
     * @return the group name
     */
    public String getGroupName() {
        return name;
    }

    /**
     * Set the group name.
     *
     * @param groupName the group name
     */
    public void setGroupName(final String groupName) {
        this.name = groupName;
    }

    /**
     * Sets the service list.
     *
     * @param prefixList the new service list
     */
    public void setServiceList(String prefixList) {
        this.prefixList = prefixList;
    }
}
