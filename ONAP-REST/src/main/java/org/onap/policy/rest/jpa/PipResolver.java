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

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.std.pip.engines.StdConfigurableEngine;
import com.google.common.base.Splitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the PipResolver database table.
 *
 */
@Entity
@Table(name = "PipResolver")
@NamedQuery(name = "PipResolver.findAll", query = "SELECT p FROM PipResolver p")
@Getter
@Setter
@NoArgsConstructor
public class PipResolver implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "DESCRIPTION", nullable = true, length = 2048)
    private String description;

    @Column(name = "NAME", nullable = false, length = 255)
    private String name;

    @Column(name = "ISSUER", nullable = true, length = 1024)
    private String issuer;

    @Column(name = "CLASSNAME", nullable = false, length = 2048)
    private String classname;

    @Column(name = "READ_ONLY", nullable = false)
    private char readOnly = '0';

    @Column(name = "CREATED_BY", nullable = false, length = 255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private Date createdDate;

    @Column(name = "MODIFIED_BY", nullable = false, length = 255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFIED_DATE", nullable = false)
    private Date modifiedDate;

    // bi-directional many-to-one association to PipConfiguration
    @ManyToOne
    @JoinColumn(name = "PIP_ID")
    private PipConfiguration pipconfiguration;

    // bi-directional many-to-one association to PipResolverParam
    @OneToMany(mappedBy = "pipresolver", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<PipResolverParam> pipresolverParams = new HashSet<>();

    /**
     * Instantiates a new PIP resolver.
     *
     * @param prefix the prefix
     * @param properties the properties
     * @param user the user
     * @throws PIPException the PIP exception
     */
    public PipResolver(String prefix, Properties properties, String user) throws PIPException {
        this.createdBy = user;
        this.modifiedBy = user;
        this.readOnly = '0';
        this.readProperties(prefix, properties);
    }

    /**
     * Instantiates a new PIP resolver.
     *
     * @param resolver the resolver
     */
    public PipResolver(PipResolver resolver) {
        this.name = resolver.name;
        this.description = resolver.description;
        this.issuer = resolver.issuer;
        this.classname = resolver.classname;
        this.readOnly = resolver.readOnly;
        for (PipResolverParam param : resolver.pipresolverParams) {
            this.addPipresolverParam(new PipResolverParam(param));
        }
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

    /**
     * Adds the pipresolver param.
     *
     * @param pipresolverParam the pipresolver param
     * @return the PIP resolver param
     */
    public PipResolverParam addPipresolverParam(PipResolverParam pipresolverParam) {
        getPipresolverParams().add(pipresolverParam);
        pipresolverParam.setPipresolver(this);

        return pipresolverParam;
    }

    /**
     * Removes the pipresolver param.
     *
     * @param pipresolverParam the pipresolver param
     * @return the PIP resolver param
     */
    public PipResolverParam removePipresolverParam(PipResolverParam pipresolverParam) {
        if (pipresolverParam == null) {
            return pipresolverParam;
        }
        getPipresolverParams().remove(pipresolverParam);
        pipresolverParam.setPipresolver(null);

        return pipresolverParam;
    }

    /**
     * Clear params.
     */
    @Transient
    public void clearParams() {
        while (!this.pipresolverParams.isEmpty()) {
            this.removePipresolverParam(this.pipresolverParams.iterator().next());
        }
    }

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    @Transient
    public boolean isReadOnly() {
        return this.readOnly == '1';
    }

    /**
     * Sets the read only.
     *
     * @param readOnly the new read only
     */
    @Transient
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            this.readOnly = '1';
        } else {
            this.readOnly = '0';
        }
    }

    /**
     * Import resolvers.
     *
     * @param prefix the prefix
     * @param list the list
     * @param properties the properties
     * @param user the user
     * @return the collection
     * @throws PIPException the PIP exception
     */
    @Transient
    public static Collection<PipResolver> importResolvers(String prefix, String list, Properties properties,
                    String user) throws PIPException {
        Collection<PipResolver> resolvers = new ArrayList<>();
        for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(list)) {
            resolvers.add(new PipResolver(prefix + "." + id, properties, user));
        }
        return resolvers;
    }

    /**
     * Read properties.
     *
     * @param prefix the prefix
     * @param properties the properties
     * @throws PIPException the PIP exception
     */
    @Transient
    protected void readProperties(String prefix, Properties properties) throws PIPException {
        //
        // Get its classname, this MUST exist.
        //
        this.classname = properties.getProperty(prefix + ".classname");
        if (this.classname == null) {
            throw new PIPException("PIP Engine defined without a classname");
        }
        //
        // Go through each property
        //
        for (Object nme : properties.keySet()) {
            if (!nme.toString().startsWith(prefix)) {
                continue;
            }
            if (nme.equals(prefix + ".classname")) {
                //
                // We already saved this
                //
            } else if (nme.equals(prefix + "." + StdConfigurableEngine.PROP_NAME)) {
                this.name = properties.getProperty(nme.toString());
            } else if (nme.equals(prefix + "." + StdConfigurableEngine.PROP_DESCRIPTION)) {
                this.description = properties.getProperty(nme.toString());
            } else if (nme.equals(prefix + "." + StdConfigurableEngine.PROP_ISSUER)) {
                this.issuer = properties.getProperty(nme.toString());
            } else {
                this.addPipresolverParam(new PipResolverParam(nme.toString().substring(prefix.length() + 1),
                                properties.getProperty(nme.toString())));
            }
        }
    }

    /**
     * Gets the configuration.
     *
     * @param prefix the prefix
     * @return the configuration
     */
    @Transient
    public Map<String, String> getConfiguration(String prefix) {
        String pref = prefix;
        Map<String, String> map = new HashMap<>();
        if (!prefix.endsWith(".")) {
            pref = prefix + ".";
        }
        map.put(pref + "classname", this.classname);
        map.put(pref + "name", this.name);
        if (this.description != null) {
            map.put(pref + "description", this.description);
        }
        if (this.issuer != null && this.issuer.isEmpty()) {
            map.put(pref + "issuer", this.issuer);
        }
        for (PipResolverParam param : this.pipresolverParams) {
            map.put(pref + param.getParamName(), param.getParamValue());
        }
        return map;
    }

    /**
     * Generate properties.
     *
     * @param props the props
     * @param prefix the prefix
     */
    @Transient
    public void generateProperties(Properties props, String prefix) {
        String pref = prefix;
        if (!prefix.endsWith(".")) {
            pref = prefix + ".";
        }
        props.setProperty(pref + "classname", this.classname);
        props.setProperty(pref + "name", this.name);
        if (this.description != null) {
            props.setProperty(pref + "description", this.description);
        }
        if (this.issuer != null && this.issuer.isEmpty()) {
            props.setProperty(pref + "issuer", this.issuer);
        }
        for (PipResolverParam param : this.pipresolverParams) {
            props.setProperty(pref + param.getParamName(), param.getParamValue());
        }
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Transient
    @Override
    public String toString() {
        return "PipResolver [id=" + id + ", classname=" + classname + ", name=" + name + ", description=" + description
                        + ", issuer=" + issuer + ", readOnly=" + readOnly + ", createdBy=" + createdBy
                        + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate="
                        + modifiedDate + ", pipresolverParams=" + pipresolverParams + "]";
    }
}
