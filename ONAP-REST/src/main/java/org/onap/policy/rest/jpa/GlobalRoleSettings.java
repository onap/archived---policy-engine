/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * Entity implementation class for Entity: Administration
 *
 */
@Entity
@Table(name="GlobalRoleSettings")
@NamedQuery(name="GlobalRoleSettings.findAll", query="SELECT g FROM GlobalRoleSettings g")
public class GlobalRoleSettings implements Serializable {	
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="role", length=45)
    private String role;

    @Column(name="lockdown")
    private boolean lockdown;

    public GlobalRoleSettings() {
        super();
    }

    public GlobalRoleSettings(boolean lockdown) {
        this.role = org.onap.policy.rest.XacmlAdminAuthorization.Role.ROLE_SUPERADMIN.toString();
        this.lockdown = lockdown;
    }

    /**
     * return the role
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * set role
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * is the system locked down
     *
     * @return
     */
    public boolean isLockdown() {
        return lockdown;
    }

    /**
     * sets lockdown configuration
     *
     * @param lockdown
     */
    public void setLockdown(boolean lockdown) {
        this.lockdown = lockdown;
    }
}
