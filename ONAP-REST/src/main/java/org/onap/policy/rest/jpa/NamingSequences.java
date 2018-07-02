/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import java.util.Date;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "NamingSequences",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"namingtype", "scope", "sequencekey", "currentseq"})})
@NamedQuery(name = "NamingSequences.findAll", query = "SELECT e FROM NamingSequences e")
public class NamingSequences implements Serializable {


    private static final long serialVersionUID = 1L;
    private Integer id;
    private String namingtype;
    private String scope;
    private String sequencekey;
    private String generatedName;
    private long startrange;
    private long endrange;
    private long steprange;
    private long currentseq;
    private Date createdDate;
    private Date modifiedDate;

    public NamingSequences() {
        super();
    }

    public NamingSequences(String namingtype, String scope, String sequencekey, String generatedName, long startrange,
            long endrange, long steprange, long currentseq, Date createdDate, Date modifiedDate) {
        this.namingtype = namingtype;
        this.scope = scope;
        this.sequencekey = sequencekey;
        this.generatedName = generatedName;
        this.startrange = startrange;
        this.endrange = endrange;
        this.steprange = steprange;
        this.currentseq = currentseq;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "namingtype", nullable = false, length = 1024)
    public String getNamingtype() {
        return this.namingtype;
    }

    public void setNamingtype(String namingtype) {
        this.namingtype = namingtype;
    }

    @Column(name = "scope", nullable = false, length = 1024)
    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Column(name = "sequencekey", nullable = false, length = 4096)
    public String getSequencekey() {
        return this.sequencekey;
    }

    public void setSequencekey(String sequencekey) {
        this.sequencekey = sequencekey;
    }

    @Column(name = "generatedName", nullable = true, length = 4096)
    public String getGeneratedName() {
        return generatedName;
    }

    public void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    @Column(name = "startrange", nullable = false)
    public long getStartrange() {
        return this.startrange;
    }

    public void setStartrange(long startrange) {
        this.startrange = startrange;
    }

    @Column(name = "endrange", nullable = false)
    public long getEndrange() {
        return this.endrange;
    }

    public void setEndrange(long endrange) {
        this.endrange = endrange;
    }

    @Column(name = "steprange", nullable = false)
    public long getSteprange() {
        return this.steprange;
    }

    public void setSteprange(long steprange) {
        this.steprange = steprange;
    }

    @Column(name = "currentseq", nullable = false)
    public long getCurrentseq() {
        return this.currentseq;
    }

    public void setCurrentseq(long currentseq) {
        this.currentseq = currentseq;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false, length = 19)
    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = true, length = 19)
    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @PrePersist
    public void prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }

}

