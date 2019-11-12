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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class DescriptiveScope.
 */
@Entity
@Table(name = "DescriptiveScope")
@NamedQuery(name = "DescriptiveScope.findAll", query = "Select p from DescriptiveScope p")
@Getter
@Setter
public class DescriptiveScope implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private int id;

    @Column(name = "scopename", nullable = false)
    @OrderBy("asc")
    private String descriptiveScopeName;

    @Column(name = "description", nullable = true, length = 2048)
    private String description;

    @Column(name = "search", nullable = true)
    @OrderBy("asc")
    private String search;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private UserInfo userCreatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modified_by")
    private UserInfo userModifiedBy;

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

    /**
     * Gets the scope name.
     *
     * @return the scope name
     */
    public String getScopeName() {
        return descriptiveScopeName;
    }

    /**
     * Sets the scope name.
     *
     * @param descriptiveScopeName the new scope name
     */
    public void setScopeName(String descriptiveScopeName) {
        this.descriptiveScopeName = descriptiveScopeName;
    }
}
