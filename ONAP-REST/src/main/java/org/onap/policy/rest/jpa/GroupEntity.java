/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonManagedReference;

/*
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

/**
 * The Entity class to persist a policy object and its configuration data.
 */
// @formatter:off
@Entity
// Add a non-unique index and a constraint that says the combo of policyName and scopeId must be unique
@Table(name = "GroupEntity")

@NamedQueries(
    {
        @NamedQuery(name = "GroupEntity.findAll", query = "SELECT e FROM GroupEntity e "),
        @NamedQuery(name = "GroupEntity.deleteAll", query = "DELETE FROM GroupEntity WHERE 1=1")
    }
)

@Getter
@Setter
//@formatter:on

public class GroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "groupKey", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long groupKey;

    @Column(name = "groupId", nullable = false)
    private String groupId;

    @Column(name = "groupName", nullable = false, unique = false, length = 255)
    private String groupName;

    @Version
    @Column(name = "version")
    private int version;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "PolicyGroupEntity", joinColumns =
        { @JoinColumn(name = "groupKey") }, inverseJoinColumns =
        { @JoinColumn(name = "policyId") })
    @JsonManagedReference
    private List<PolicyEntity> policies;

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

    @Column(name = "defaultGroup", nullable = false)
    private boolean defaultGroup = false;
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Instantiates a new group entity.
     */
    public GroupEntity() {
        super();
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

    /**
     * Adds the policy to group.
     *
     * @param policy the policy
     */
    public void addPolicyToGroup(PolicyEntity policy) {
        if (policies == null) {
            policies = new ArrayList<>();
        }

        if (!this.policies.contains(policy)) {
            this.policies.add(policy);
        }
    }

    /**
     * Removes the policy from group.
     *
     * @param policy the policy
     */
    public void removePolicyFromGroup(PolicyEntity policy) {
        this.policies.remove(policy);

        if (policies.isEmpty()) {
            policies = null;
        }
    }
}
