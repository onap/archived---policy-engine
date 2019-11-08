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

/*
 */
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
/*
 * The Entity class to persist a policy object configuration data
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class ConfigurationDataEntity.
 */
// @formatter:off
@Entity
@Table(name = "ConfigurationDataEntity")
@NamedQueries(
    {
        @NamedQuery(name = "ConfigurationDataEntity.findAll", query = "SELECT e FROM ConfigurationDataEntity e "),
        @NamedQuery(name = "ConfigurationDataEntity.deleteAll", query = "DELETE FROM ConfigurationDataEntity WHERE 1=1")
    }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
//@formatter:on

public class ConfigurationDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "configurationDataId")
    @JsonBackReference
    private long configurationDataId;

    @Column(name = "configurationName", nullable = false, length = 255)
    private String configurationName = "";

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "configType", nullable = false, length = 255)
    private String configType = "NoType";

    @Lob
    @Column(name = "configBody", nullable = false, columnDefinition = "TEXT")
    private String configBody = "NoBody";

    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "description", nullable = false, length = 2048)
    private String description = "NoDescription";

    @Column(name = "modified_by", nullable = false, length = 255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = false)
    private Date modifiedDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

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
