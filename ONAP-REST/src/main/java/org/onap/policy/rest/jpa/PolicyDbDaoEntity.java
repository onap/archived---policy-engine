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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * The Entity class to persist a PolicyDbDaoEntity object for registration of PolicyDBDao.
 */
// @formatter:off
@Entity
@Table(name = "PolicyDbDaoEntity")
@NamedQueries(
    {
        @NamedQuery(name = "PolicyDbDaoEntity.findAll", query = "SELECT e FROM PolicyDbDaoEntity e "),
        @NamedQuery(name = "PolicyDbDaoEntity.deleteAll", query = "DELETE FROM PolicyDbDaoEntity WHERE 1=1")
    }
)
@Getter
@Setter
//@formatter:on
public class PolicyDbDaoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "policyDbDaoUrl", nullable = false, unique = true)
    private String policyDbDaoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    // username for the pap server that registered this PolicyDbDaoEntity
    @Column(name = "username")
    private String username;

    // AES encrypted password for the pap server that registered this PolicyDbDaoEntity
    @Column(name = "password")
    private String password;

    // A column to allow some descriptive text. For example: Atlanta data center
    @Column(name = "description", nullable = false, length = 2048)
    private String description = "NoDescription";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    /**
     * Instantiates a new policy DB dao entity.
     */
    public PolicyDbDaoEntity() {
        super();
    }

    /**
     * Pre persist.
     */
    @PrePersist
    public void prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    /**
     * Pre update.
     */
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }
}
