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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
  * The persistent class for the system log database table.
  * 
*/
@Entity
@Table(name="SystemLogDB")
@NamedQuery(name="SystemLogDB.findAll", query="SELECT o FROM SystemLogDB o")
public class SystemLogDB implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="type", nullable=false)
    private String type;

    @Column(name="system", nullable=false, length=255)
    private String system;

    @Column(name="description", nullable=true, length=2048)
    private String description;

    @Column(name="remote", nullable=false, length=255)
    private String remote;

    @Column(name="logtype", nullable=false, length=255)
    private String logtype;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", nullable=false, updatable=false)
    private Date date;

    public SystemLogDB() {
        super();
    }

    public SystemLogDB(int id, String system, String description, String remote,
            String type, String logtype) {
        this.id = id;
        this.system = system;
        this.description = description;
        this.remote = remote;
        this.type = type;
        this.logtype = logtype;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate(){
        return this.date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;

    }

    public String getSystem() {
        return this.system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getRemote() {
        return this.remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;

    }
    public String getLogtype() {
        return this.logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;

    }
}
