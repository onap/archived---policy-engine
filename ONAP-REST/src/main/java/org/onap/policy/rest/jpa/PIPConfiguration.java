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
import java.util.Iterator;
import java.util.List;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.std.pip.engines.StdConfigurableEngine;
import com.att.research.xacml.util.XACMLProperties;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;


/**
 * The persistent class for the PIPConfiguration database table.
 * 
 */
@Entity
@Table(name="PIPConfiguration")
@NamedQuery(name="PIPConfiguration.findAll", query="SELECT p FROM PIPConfiguration p")
public class PIPConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Log logger	= LogFactory.getLog(PIPConfiguration.class);

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="DESCRIPTION", nullable=true, length=2048)
    private String description;

    @Column(name="NAME", nullable=false, length=255)
    private String name;

    @Column(name="CLASSNAME", nullable=false, length=2048)
    private String classname;

    @Column(name="ISSUER", nullable=true, length=1024)
    private String issuer;

    @Column(name="READ_ONLY", nullable=false)
    private char readOnly = '0';

    @Column(name="REQUIRES_RESOLVER", nullable=false)
    private char requiresResolvers;

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

    //bi-directional many-to-one association to PIPConfigParam
    @OneToMany(mappedBy="pipconfiguration", orphanRemoval=true, cascade=CascadeType.REMOVE)
    private Set<PIPConfigParam> pipconfigParams = new HashSet<>();

    //bi-directional many-to-one association to PIPType
    @ManyToOne
    @JoinColumn(name="TYPE")
    private PIPType piptype;

    //bi-directional many-to-one association to PIPResolver
    @OneToMany(mappedBy="pipconfiguration", orphanRemoval=true, cascade=CascadeType.REMOVE)
    private Set<PIPResolver> pipresolvers = new HashSet<>();

    public PIPConfiguration() {
        //An empty constructor
    }

    public PIPConfiguration(PIPConfiguration config, String user) {
        this.description = config.description;
        this.name = config.name;
        this.classname = config.classname;
        this.issuer = config.issuer;
        this.requiresResolvers = config.requiresResolvers;
        this.readOnly = config.readOnly;
        this.piptype = config.piptype;
        for (PIPConfigParam param : config.pipconfigParams) {
            this.addPipconfigParam(new PIPConfigParam(param));
        }
        for (PIPResolver resolver : config.pipresolvers) {
            this.addPipresolver(new PIPResolver(resolver));
        }
    }

    public PIPConfiguration(String id, Properties properties) throws PIPException {
        this.readProperties(id, properties);
    }

    public PIPConfiguration(String id, Properties properties, String user) throws PIPException {
        this.createdBy = user;
        this.modifiedBy = user;
        this.readProperties(id, properties);
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

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public char getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(char readOnly) {
        this.readOnly = readOnly;
    }

    public char getRequiresResolvers() {
        return requiresResolvers;
    }

    public void setRequiresResolvers(char requireResolvers) {
        this.requiresResolvers = requireResolvers;
    }

    public Set<PIPConfigParam> getPipconfigParams() {
        return this.pipconfigParams;
    }

    public void setPipconfigParams(Set<PIPConfigParam> pipconfigParams) {
        this.pipconfigParams = pipconfigParams;
    }

    public PIPConfigParam addPipconfigParam(PIPConfigParam pipconfigParam) {
        getPipconfigParams().add(pipconfigParam);
        pipconfigParam.setPipconfiguration(this);

        return pipconfigParam;
    }

    public PIPConfigParam removePipconfigParam(PIPConfigParam pipconfigParam) {
        if (pipconfigParam == null) {
            return pipconfigParam;
        }
        getPipconfigParams().remove(pipconfigParam);
        pipconfigParam.setPipconfiguration(null);

        return pipconfigParam;
    }

    @Transient
    public void clearConfigParams() {
        while (!this.pipconfigParams.isEmpty()) {
            this.removePipconfigParam(this.pipconfigParams.iterator().next());
        }
    }

    public PIPType getPiptype() {
        return this.piptype;
    }

    public void setPiptype(PIPType piptype) {
        this.piptype = piptype;
    }

    public Set<PIPResolver> getPipresolvers() {
        return this.pipresolvers;
    }

    public void setPipresolvers(Set<PIPResolver> pipresolvers) {
        this.pipresolvers = pipresolvers;
    }

    public PIPResolver addPipresolver(PIPResolver pipresolver) {
        getPipresolvers().add(pipresolver);
        pipresolver.setPipconfiguration(this);

        return pipresolver;
    }

    public PIPResolver removePipresolver(PIPResolver pipresolver) {
        getPipresolvers().remove(pipresolver);
        pipresolver.setPipconfiguration(null);

        return pipresolver;
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
    public boolean requiresResolvers() {
        return this.requiresResolvers == '1';
    }

    @Transient
    public void	setRequiresResolvers(boolean requires) {
        if (requires) {
            this.requiresResolvers = '1';
        } else {
            this.requiresResolvers = '0';
        }
    }

    @Transient
    public static Collection<PIPConfiguration>		importPIPConfigurations(Properties properties) {
        Collection<PIPConfiguration> configurations = new ArrayList<>();
        String engines = properties.getProperty(XACMLProperties.PROP_PIP_ENGINES);
        if (engines == null || engines.isEmpty()) {
            return configurations;
        }
        for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(engines)) {
            PIPConfiguration configuration;
            try {
                String user = "super-admin";
                configuration = new PIPConfiguration(id, properties, user);
                configuration.setCreatedBy(user);
                configuration.setModifiedBy(user);
                configurations.add(configuration);
            } catch (PIPException e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Import failed: " + e.getLocalizedMessage());
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PIPConfiguration", "Import failed");
            }
        }

        return configurations;
    }

    @Transient
    protected	void		readProperties(String id, Properties properties) throws PIPException {
        //
        // Save the id if we don't have one already
        //

        if (this.id == 0) {
            try {
                this.id = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Convert id to integer failed: " + id);
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PIPConfiguration", "Convert id to integer failed");
            }
        }
        //
        // Get its classname, this MUST exist.
        //
        this.classname = properties.getProperty(id + ".classname");
        if (this.classname == null) {
            throw new PIPException("PIP Engine defined without a classname");
        }
        //
        // Go through each property
        //
        for (Object nme : properties.keySet()) {
            if (!nme.toString().startsWith(id)) {
                continue;
            }
            if (nme.equals(id + ".classname")) {
                //
                // We already saved this
                //
            } else if (nme.equals(id + "." + StdConfigurableEngine.PROP_NAME)) {
                this.name = properties.getProperty(nme.toString());
            } else if (nme.equals(id + "." + StdConfigurableEngine.PROP_DESCRIPTION)) {
                this.description = properties.getProperty(nme.toString());
            } else if (nme.equals(id + "." + StdConfigurableEngine.PROP_ISSUER)) {
                this.issuer = properties.getProperty(nme.toString());
            } else if (nme.equals(id + ".resolvers")) {
                //
                // It has resolvers, make sure this is set to true if
                // it has been already.
                //
                this.setRequiresResolvers(true);
                //
                // Parse the resolvers
                //
                Collection<PIPResolver> resolvers = PIPResolver.importResolvers(id + ".resolver",
                                                                        properties.getProperty(nme.toString()),
                                                                        properties,"super-admin"
                                                                        );
                for (PIPResolver resolver : resolvers) {
                    this.addPipresolver(resolver);
                }
            } else if (nme.toString().startsWith(id + ".resolver")) {
                //
                // Ignore, the PIPResolver will parse these values
                //
            } else {
                //
                // Config Parameter
                //
                this.addPipconfigParam(new PIPConfigParam(nme.toString().substring(id.length() + 1),
                                                    properties.getProperty(nme.toString())));
            }
        }
        //
        // Make sure we have a name at least
        //
        if (this.name == null) {
            this.name = id;
        }
    }


    @Transient
    public Map<String, String> getConfiguration(String name) {
        String prefix;
        if (name == null) {
            prefix = Integer.toString(this.id);
        } else {
            prefix = name;
        }
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        Map<String, String> map = new HashMap<>();
        map.put(prefix + "classname", this.classname);
        map.put(prefix + "name", this.name);
        if (this.description != null) {
            map.put(prefix + "description", this.description);
        }
        if (this.issuer != null) {
            map.put(prefix + "issuer", this.issuer);
        }

        for (PIPConfigParam param : this.pipconfigParams) {
            map.put(prefix + param.getParamName(), param.getParamValue());
        }

        List<String> ids = new ArrayList<>();
        Iterator<PIPResolver> iter = this.pipresolvers.iterator();
        while (iter.hasNext()) {
            PIPResolver resolver = iter.next();
            String idd = Integer.toString(resolver.getId());
            Map<String, String> resolverMap = resolver.getConfiguration(prefix + "resolver." + idd);
            map.putAll(resolverMap);
            ids.add(idd);
        }
        if (!ids.isEmpty()) {
            map.put(prefix + "resolvers", Joiner.on(',').join(ids));
        }
        return map;
    }

    @Transient
    public Properties	generateProperties(String name) {
        String prefix;
        if (name == null) {
            prefix = Integer.toString(this.id);
        } else {
            if (name.endsWith(".")) {
                prefix = name;
            } else {
                prefix = name + ".";
            }
        }
        Properties props = new Properties();
        props.setProperty("xacml.pip.engines", name);
        props.setProperty(prefix + "classname", this.classname);
        props.setProperty(prefix + "name", this.name);
        if (this.description != null) {
            props.setProperty(prefix + "description", this.description);
        }
        if (this.issuer != null && !this.issuer.isEmpty()) {
            props.setProperty(prefix + "issuer", this.issuer);
        }

        for (PIPConfigParam param : this.pipconfigParams) {
            props.setProperty(prefix + param.getParamName(), param.getParamValue());
        }

        List<String> ids = new ArrayList<>();
        Iterator<PIPResolver> iter = this.pipresolvers.iterator();
        while (iter.hasNext()) {
            PIPResolver resolver = iter.next();
            String idd = Integer.toString(resolver.getId());
            resolver.generateProperties(props, prefix + "resolver." + idd);
            ids.add(idd);
        }
        if (!ids.isEmpty()) {
            props.setProperty(prefix + "resolvers", Joiner.on(',').join(ids));
        }
        return props;
    }

    @Transient
    @Override
    public String toString() {
        return "PIPConfiguration [id=" + id + ", piptype=" + piptype
                + ", classname=" + classname + ", name=" + name
                + ", description=" + description + ", issuer=" + issuer
                + ", readOnly=" + readOnly + ", requiresResolvers="
                + requiresResolvers + ", createdBy=" + createdBy
                + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy
                + ", modifiedDate=" + modifiedDate + ", pipconfigParams="
                + pipconfigParams + ", pipresolvers=" + pipresolvers + "]";
    }
}
