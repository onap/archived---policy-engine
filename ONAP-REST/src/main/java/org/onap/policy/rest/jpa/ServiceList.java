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
/*
 */
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
@Table(name="servicegroup")
@NamedQuery(name="ServiceList.findAll", query="SELECT e FROM ServiceList e ")
public class ServiceList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="name", nullable=false)
    @OrderBy("asc")
    private String serviceName;

    @Column(name="description")
    private String serviceDesc;

    @Column(name="type")
    private String serviceType;

    @Column(name="transportprotocol")
    private String serviceTrasProtocol;

    @Column(name="appprotocol ")
    private String serviceAppProtocol;

    @Column(name="ports")
    private String servicePorts;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;

    }

    public String getServiceDescription() {
        return this.serviceDesc;
    }

    public void setServiceDescription(String serviceDesc) {
        this.serviceDesc = serviceDesc;

    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceTransProtocol() {
        return this.serviceTrasProtocol;
    }

    public void setServiceTransProtocol(String serviceTrasProtocol) {
        this.serviceTrasProtocol = serviceTrasProtocol;

    }

    public String getServiceAppProtocol() {
        return this.serviceAppProtocol;
    }

    public void setServiceAppProtocol(String serviceAppProtocol) {
        this.serviceAppProtocol = serviceAppProtocol;

    }
    public String getServicePorts() {
        return this.servicePorts;
    }

    public void setServicePorts(String servicePorts) {
        this.servicePorts = servicePorts;

    }
}
