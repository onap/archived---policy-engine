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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the PipType database table.
 *
 */
@Entity
@Table(name = "PipType")
@NamedQuery(name = "PipType.findAll", query = "SELECT p FROM PipType p")
@Getter
@Setter
@NoArgsConstructor
public class PipType implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_SQL = "SQL";
    public static final String TYPE_LDAP = "LDAP";
    public static final String TYPE_CSV = "CSV";
    public static final String TYPE_HYPERCSV = "Hyper-CSV";
    public static final String TYPE_CUSTOM = "Custom";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "type", nullable = false, length = 45)
    private String type;

    // bi-directional many-to-one association to PipConfiguration
    @OneToMany(mappedBy = "piptype")
    private Set<PipConfiguration> pipconfigurations;

    /**
     * Adds the pipconfiguration.
     *
     * @param pipconfiguration the pipconfiguration
     * @return the PIP configuration
     */
    public PipConfiguration addPipconfiguration(PipConfiguration pipconfiguration) {
        getPipconfigurations().add(pipconfiguration);
        pipconfiguration.setPiptype(this);

        return pipconfiguration;
    }

    /**
     * Removes the pipconfiguration.
     *
     * @param pipconfiguration the pipconfiguration
     * @return the PIP configuration
     */
    public PipConfiguration removePipconfiguration(PipConfiguration pipconfiguration) {
        getPipconfigurations().remove(pipconfiguration);
        pipconfiguration.setPiptype(null);

        return pipconfiguration;
    }

    /**
     * Checks if is sql.
     *
     * @return true, if is sql
     */
    @Transient
    public boolean isSql() {
        return this.type.equals(TYPE_SQL);
    }

    /**
     * Checks if is ldap.
     *
     * @return true, if is ldap
     */
    @Transient
    public boolean isLdap() {
        return this.type.equals(TYPE_LDAP);
    }

    /**
     * Checks if is csv.
     *
     * @return true, if is csv
     */
    @Transient
    public boolean isCsv() {
        return this.type.equals(TYPE_CSV);
    }

    /**
     * Checks if is hyper CSV.
     *
     * @return true, if is hyper CSV
     */
    @Transient
    public boolean isHyperCsv() {
        return this.type.equals(TYPE_HYPERCSV);
    }

    /**
     * Checks if is custom.
     *
     * @return true, if is custom
     */
    @Transient
    public boolean isCustom() {
        return this.type.equals(TYPE_CUSTOM);
    }

}
