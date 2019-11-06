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

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Data;

/*
 * The Entity class to persist a policy object Action Body
 */
// @formatter:off
@Entity
@Table(name = "ActionBodyEntity")
@NamedQueries(
    {
        @NamedQuery(name = " ActionBodyEntity.findAll", query = "SELECT e FROM ActionBodyEntity e "),
        @NamedQuery(name = "ActionBodyEntity.deleteAll", query = "DELETE FROM ActionBodyEntity WHERE 1=1")
    }
)
@Data
//@foramtter:on

public class ActionBodyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "actionBodyId")
    @JsonBackReference
    private long actionBodyId;

    @Column(name = "actionBodyName", nullable = false, length = 255)
    private String actionBodyName = "";

    @Version
    @Column(name = "version")
    private int version;

    @Lob
    @Column(name = "actionBody", nullable = false, columnDefinition = "TEXT")
    private String actionBody = "NoBody";

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "modified_by", nullable = false, length = 255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public ActionBodyEntity() {
        // An empty constructor
    }

    /**
     * Called before an instance is persisted.
     */
    @PrePersist
    public void prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    /**
     * Called before an instance is updated.
     */
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }
}
