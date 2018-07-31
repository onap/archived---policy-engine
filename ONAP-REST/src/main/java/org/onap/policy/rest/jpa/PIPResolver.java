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

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.std.pip.engines.StdConfigurableEngine;
import com.google.common.base.Splitter;


/**
 * The persistent class for the PIPResolver database table.
 * 
 */
@Entity
@Table(name="PIPResolver")
@NamedQuery(name="PIPResolver.findAll", query="SELECT p FROM PIPResolver p")
public class PIPResolver implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="DESCRIPTION", nullable=true, length=2048)
    private String description;

    @Column(name="NAME", nullable=false, length=255)
    private String name;

    @Column(name="ISSUER", nullable=true, length=1024)
    private String issuer;

    @Column(name="CLASSNAME", nullable=false, length=2048)
    private String classname;

    @Column(name="READ_ONLY", nullable=false)
    private char readOnly = '0';

    @Column(name="CREATED_BY", nullable=false, length=255)
    private String createdBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATED_DATE", nullable=false, updatable=false)
    private Date createdDate;

    @Column(name="MODIFIED_BY", nullable=false, length=255)
    private String modifiedBy = "guest";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="MODIFIED_DATE", nullable=false)
    private Date modifiedDate;

    //bi-directional many-to-one association to PIPConfiguration
    @ManyToOne
    @JoinColumn(name="PIP_ID")
    private PIPConfiguration pipconfiguration;

    //bi-directional many-to-one association to PIPResolverParam
    @OneToMany(mappedBy="pipresolver", orphanRemoval=true, cascade=CascadeType.REMOVE)
    private Set<PIPResolverParam> pipresolverParams = new HashSet<>();

    public PIPResolver() {
        //An empty constructor
    }

    public PIPResolver(String prefix, Properties properties, String user) throws PIPException {
        this.createdBy = user;
        this.modifiedBy = user;
        this.readOnly = '0';
        this.readProperties(prefix, properties);
    }

    public PIPResolver(PIPResolver resolver) {
        this.name = resolver.name;
        this.description = resolver.description;
        this.issuer = resolver.issuer;
        this.classname = resolver.classname;
        this.readOnly = resolver.readOnly;
        for (PIPResolverParam param : this.pipresolverParams) {
            this.addPipresolverParam(new PIPResolverParam(param));
        }
    }

    @PrePersist
    public void	prePersist() {
        Date date = new Date();
        this.createdDate = date;
        this.modifiedDate = date;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = new Date();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public char getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(char readOnly) {
        this.readOnly = readOnly;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public PIPConfiguration getPipconfiguration() {
        return this.pipconfiguration;
    }

    public void setPipconfiguration(PIPConfiguration pipconfiguration) {
        this.pipconfiguration = pipconfiguration;
    }

    public Set<PIPResolverParam> getPipresolverParams() {
        return this.pipresolverParams;
    }

    public void setPipresolverParams(Set<PIPResolverParam> pipresolverParams) {
        this.pipresolverParams = pipresolverParams;
    }

    public PIPResolverParam addPipresolverParam(PIPResolverParam pipresolverParam) {
        getPipresolverParams().add(pipresolverParam);
        pipresolverParam.setPipresolver(this);

        return pipresolverParam;
    }

    public PIPResolverParam removePipresolverParam(PIPResolverParam pipresolverParam) {
        if (pipresolverParam == null) {
            return pipresolverParam;
        }
        getPipresolverParams().remove(pipresolverParam);
        pipresolverParam.setPipresolver(null);

        return pipresolverParam;
    }

    @Transient
    public void clearParams() {
        while (!this.pipresolverParams.isEmpty()) {
            this.removePipresolverParam(this.pipresolverParams.iterator().next());
        }
    }

    @Transient
    public boolean isReadOnly() {
        return this.readOnly == '1';
    }

    @Transient
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            this.readOnly = '1';
        } else {
            this.readOnly = '0';
        }
    }

    @Transient
    public static Collection<PIPResolver>	importResolvers(String prefix, String list, Properties properties, String user) throws PIPException {
        Collection<PIPResolver> resolvers = new ArrayList<>();
        for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(list)) {
            resolvers.add(new PIPResolver(prefix + "." + id, properties, user));
        }
        return resolvers;
    }

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
                this.addPipresolverParam(new PIPResolverParam(nme.toString().substring(prefix.length() + 1),
                                                            properties.getProperty(nme.toString())));
            }
        }
    }

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
        for (PIPResolverParam param : this.pipresolverParams) {
            map.put(pref + param.getParamName(), param.getParamValue());
        }
        return map;
    }

    @Transient
    public void	generateProperties(Properties props, String prefix) {
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
        for (PIPResolverParam param : this.pipresolverParams) {
            props.setProperty(pref + param.getParamName(), param.getParamValue());
        }
    }

    @Transient
    @Override
    public String toString() {
        return "PIPResolver [id=" + id + ", classname=" + classname + ", name="
                + name + ", description=" + description + ", issuer=" + issuer
                + ", readOnly=" + readOnly + ", createdBy=" + createdBy
                + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy
                + ", modifiedDate=" + modifiedDate + ", pipresolverParams="
                + pipresolverParams + "]";
    }
}
