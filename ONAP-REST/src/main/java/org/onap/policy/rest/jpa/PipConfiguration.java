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
import com.att.research.xacml.util.XACMLProperties;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.xacml.api.XACMLErrorConstants;

/**
 * The persistent class for the PipConfiguration database table.
 *
 */
@Entity
@Table(name = "PipConfiguration")
@NamedQuery(name = "PipConfiguration.findAll", query = "SELECT p FROM PipConfiguration p")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PipConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Log logger = LogFactory.getLog(PipConfiguration.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "DESCRIPTION", nullable = true, length = 2048)
    private String description;

    @Column(name = "NAME", nullable = false, length = 255)
    private String name;

    @Column(name = "CLASSNAME", nullable = false, length = 2048)
    private String classname;

    @Column(name = "ISSUER", nullable = true, length = 1024)
    private String issuer;

    @Column(name = "READ_ONLY", nullable = false)
    private char readOnly = '0';

    @Column(name = "REQUIRES_RESOLVER", nullable = false)
    private char requiresResolvers;

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

    // bi-directional many-to-one association to PipConfigParam
    @OneToMany(mappedBy = "pipconfiguration", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<PipConfigParam> pipconfigParams = new HashSet<>();

    // bi-directional many-to-one association to PipType
    @ManyToOne
    @JoinColumn(name = "TYPE")
    private PipType piptype;

    // bi-directional many-to-one association to PipResolver
    @OneToMany(mappedBy = "pipconfiguration", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Set<PipResolver> pipresolvers = new HashSet<>();

    /**
     * Instantiates a new PIP configuration.
     *
     * @param config the config
     * @param user the user
     */
    public PipConfiguration(PipConfiguration config, String user) {
        this.description = config.description;
        this.name = config.name;
        this.classname = config.classname;
        this.issuer = config.issuer;
        this.requiresResolvers = config.requiresResolvers;
        this.readOnly = config.readOnly;
        this.piptype = config.piptype;
        for (PipConfigParam param : config.pipconfigParams) {
            this.addPipconfigParam(new PipConfigParam(param));
        }
        for (PipResolver resolver : config.pipresolvers) {
            this.addPipresolver(new PipResolver(resolver));
        }
    }

    /**
     * Instantiates a new PIP configuration.
     *
     * @param id the id
     * @param properties the properties
     * @throws PIPException the PIP exception
     */
    public PipConfiguration(String id, Properties properties) throws PIPException {
        this.readProperties(id, properties);
    }

    /**
     * Instantiates a new PIP configuration.
     *
     * @param id the id
     * @param properties the properties
     * @param user the user
     * @throws PIPException the PIP exception
     */
    public PipConfiguration(String id, Properties properties, String user) throws PIPException {
        this.createdBy = user;
        this.modifiedBy = user;
        this.readProperties(id, properties);
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
     * Adds the pipconfig param.
     *
     * @param pipconfigParam the pipconfig param
     * @return the PIP config param
     */
    public PipConfigParam addPipconfigParam(PipConfigParam pipconfigParam) {
        getPipconfigParams().add(pipconfigParam);
        pipconfigParam.setPipconfiguration(this);

        return pipconfigParam;
    }

    /**
     * Removes the pipconfig param.
     *
     * @param pipconfigParam the pipconfig param
     * @return the PIP config param
     */
    public PipConfigParam removePipconfigParam(PipConfigParam pipconfigParam) {
        if (pipconfigParam == null) {
            return pipconfigParam;
        }
        getPipconfigParams().remove(pipconfigParam);
        pipconfigParam.setPipconfiguration(null);

        return pipconfigParam;
    }

    /**
     * Clear config params.
     */
    @Transient
    public void clearConfigParams() {
        while (!this.pipconfigParams.isEmpty()) {
            this.removePipconfigParam(this.pipconfigParams.iterator().next());
        }
    }

    /**
     * Adds the pipresolver.
     *
     * @param pipresolver the pipresolver
     * @return the PIP resolver
     */
    public PipResolver addPipresolver(PipResolver pipresolver) {
        getPipresolvers().add(pipresolver);
        pipresolver.setPipconfiguration(this);

        return pipresolver;
    }

    /**
     * Removes the pipresolver.
     *
     * @param pipresolver the pipresolver
     * @return the PIP resolver
     */
    public PipResolver removePipresolver(PipResolver pipresolver) {
        getPipresolvers().remove(pipresolver);
        pipresolver.setPipconfiguration(null);

        return pipresolver;
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
     * Sets the read only flag.
     *
     * @param readOnly the new read only flag
     */
    @Transient
    public void setReadOnlyFlag(boolean readOnly) {
        if (readOnly) {
            this.readOnly = '1';
        } else {
            this.readOnly = '0';
        }
    }

    /**
     * Requires resolvers.
     *
     * @return true, if successful
     */
    @Transient
    public boolean requiresResolvers() {
        return this.requiresResolvers == '1';
    }

    /**
     * Sets the requires resolvers flag.
     *
     * @param requires the new requires resolvers flag
     */
    @Transient
    public void setRequiresResolversFlag(boolean requires) {
        if (requires) {
            this.requiresResolvers = '1';
        } else {
            this.requiresResolvers = '0';
        }
    }

    /**
     * Import PIP configurations.
     *
     * @param properties the properties
     * @return the collection
     */
    @Transient
    public static Collection<PipConfiguration> importPipConfigurations(Properties properties) {
        Collection<PipConfiguration> configurations = new ArrayList<>();
        String engines = properties.getProperty(XACMLProperties.PROP_PIP_ENGINES);
        if (engines == null || engines.isEmpty()) {
            return configurations;
        }
        for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(engines)) {
            PipConfiguration configuration;
            try {
                String user = "super-admin";
                configuration = new PipConfiguration(id, properties, user);
                configuration.setCreatedBy(user);
                configuration.setModifiedBy(user);
                configurations.add(configuration);
            } catch (PIPException e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Import failed: " + e.getLocalizedMessage());
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PipConfiguration", "Import failed");
            }
        }

        return configurations;
    }

    /**
     * Read properties.
     *
     * @param id the id
     * @param properties the properties
     * @throws PIPException the PIP exception
     */
    @Transient
    protected void readProperties(String id, Properties properties) throws PIPException {
        //
        // Save the id if we don't have one already
        //

        if (this.id == 0) {
            try {
                this.id = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Convert id to integer failed: " + id);
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PipConfiguration", "Convert id to integer failed");
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
        for (Object propertyKey : properties.keySet()) {
            readProperty(id, properties, propertyKey);
        }
        //
        // Make sure we have a name at least
        //
        if (this.name == null) {
            this.name = id;
        }
    }

    /**
     * Read a property into the PIP configuration.
     *
     * @param id the ID of the property
     * @param properties the properties object to read from
     * @param key the key of the property being checked
     * @throws PIPException on exceptions thrown on reading the property
     */
    private void readProperty(String id, Properties properties, Object key) throws PIPException {
        if (!key.toString().startsWith(id)) {
            return;
        }
        if (key.equals(id + ".classname")) {
            //
            // We already saved this
            //
        } else if (key.equals(id + "." + StdConfigurableEngine.PROP_NAME)) {
            this.name = properties.getProperty(key.toString());
        } else if (key.equals(id + "." + StdConfigurableEngine.PROP_DESCRIPTION)) {
            this.description = properties.getProperty(key.toString());
        } else if (key.equals(id + "." + StdConfigurableEngine.PROP_ISSUER)) {
            this.issuer = properties.getProperty(key.toString());
        } else if (key.equals(id + ".resolvers")) {
            //
            // It has resolvers, make sure this is set to true if
            // it has been already.
            //
            this.setRequiresResolversFlag(true);
            //
            // Parse the resolvers
            //
            Collection<PipResolver> resolvers = PipResolver.importResolvers(id + ".resolver",
                            properties.getProperty(key.toString()), properties, "super-admin");
            for (PipResolver resolver : resolvers) {
                this.addPipresolver(resolver);
            }
        } else if (key.toString().startsWith(id + ".resolver")) {
            //
            // Ignore, the PipResolver will parse these values
            //
        } else {
            //
            // Config Parameter
            //
            this.addPipconfigParam(new PipConfigParam(key.toString().substring(id.length() + 1),
                            properties.getProperty(key.toString())));
        }
    }

    /**
     * Gets the configuration.
     *
     * @param name the name
     * @return the configuration
     */
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

        for (PipConfigParam param : this.pipconfigParams) {
            map.put(prefix + param.getParamName(), param.getParamValue());
        }

        List<String> ids = new ArrayList<>();
        Iterator<PipResolver> iter = this.pipresolvers.iterator();
        while (iter.hasNext()) {
            PipResolver resolver = iter.next();
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

    /**
     * Generate properties.
     *
     * @param name the name
     * @return the properties
     */
    @Transient
    public Properties generateProperties(String name) {
        String prefix;
        if (name == null) {
            prefix = Integer.toString(this.id);
        } else {
            if (name.endsWith(".")) {
                prefix = name;
            } else {
                prefix = name + ".";
                /**
                 * Instantiates a new PIP configuration.
                 */

            }
        }
        Properties props = new Properties();
        props.setProperty("xacml.pip.engines", prefix);
        props.setProperty(prefix + "classname", this.classname);
        props.setProperty(prefix + "name", this.name);
        if (this.description != null) {
            props.setProperty(prefix + "description", this.description);
        }
        if (this.issuer != null && !this.issuer.isEmpty()) {
            props.setProperty(prefix + "issuer", this.issuer);
        }

        for (PipConfigParam param : this.pipconfigParams) {
            props.setProperty(prefix + param.getParamName(), param.getParamValue());
        }

        List<String> ids = new ArrayList<>();
        Iterator<PipResolver> iter = this.pipresolvers.iterator();
        while (iter.hasNext()) {
            PipResolver resolver = iter.next();
            String idd = Integer.toString(resolver.getId());
            resolver.generateProperties(props, prefix + "resolver." + idd);
            ids.add(idd);
        }
        if (!ids.isEmpty()) {
            props.setProperty(prefix + "resolvers", Joiner.on(',').join(ids));
        }
        return props;
    }
}
