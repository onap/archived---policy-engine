/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.xacml.std.pap;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroupStatus;
import com.att.research.xacml.api.pap.PDPGroupStatus.Status;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.ByteStreams;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPItemSetChangeNotifier.StdItemSetChangeListener;

@EqualsAndHashCode(callSuper = false)
@ToString
public class StdPDPGroup extends StdPDPItemSetChangeNotifier
        implements OnapPDPGroup, StdItemSetChangeListener, Comparable<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String MSG_GROUPNOTEXIST = "Group directory does NOT exist";
    private static final String MSG_LOADFAILURE = "Failed to load group policy properties file: ";
    private static final String STR_APPEND_NAME = ".name";
    private static final String STR_APPEND_DESCRIPTION = ".description";
    private static final String STR_APPEND_PDPS = ".pdps";
    private static final String STR_CLASS = "StdPDPGroup";
    private static final String PROPS_POLICY = "xacml.policy.properties";
    private static final String PROPS_PIP = "xacml.pip.properties";

    private static Log logger = LogFactory.getLog(StdPDPGroup.class);

    private String id;

    private boolean isDefault = false;

    private String name;

    private String description;

    private transient StdPDPGroupStatus status = new StdPDPGroupStatus(Status.UNKNOWN);

    private transient Set<OnapPDP> pdps = new HashSet<>();

    private transient Set<PDPPolicy> policies = new HashSet<>();

    private transient Set<PDPPolicy> selectedPolicies = new HashSet<>();

    private transient Set<PDPPIPConfig> pipConfigs = new HashSet<>();

    private String operation;

    @JsonIgnore
    private transient Path directory;

    @JsonIgnore
    private Integer jmxport;

    public StdPDPGroup() {
        //
        // Methods needed for JSON deserialization
        //
    }

    /**
     * StdPDPGroup constructor.
     *
     * @param group OnapPDPGroup
     */
    public StdPDPGroup(OnapPDPGroup group) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.isDefault = group.isDefaultGroup();
        this.pdps = group.getOnapPdps();
        this.policies = group.getPolicies();
        this.pipConfigs = group.getPipConfigs();
    }

    public StdPDPGroup(String id, Path directory) {
        this.id = id;
        this.directory = directory;
    }

    public StdPDPGroup(String id, boolean isDefault, Path directory) {
        this(id, directory);
        this.isDefault = isDefault;
    }

    /**
     * StdPDPGroup.
     *
     * @param id String
     * @param isDefault boolean
     * @param name String
     * @param description String
     * @param directory Path
     */
    public StdPDPGroup(String id, boolean isDefault, String name, String description, Path directory) {
        this(id, isDefault, directory);
        this.name = name;
        // force all policies to have a name
        if (name == null) {
            this.name = id;
        }
        this.description = description;
    }

    public StdPDPGroup(String id, String name, String description, Path directory) {
        this(id, false, name, description, directory);
        this.resetStatus();
    }

    /**
     * StdPDPGroup.
     *
     * @param id String
     * @param isDefault boolean
     * @param properties Properties
     * @param directory Path
     * @throws PAPException PAPException
     */
    public StdPDPGroup(String id, boolean isDefault, Properties properties, Path directory) throws PAPException {
        this(id, isDefault, directory);
        this.initialize(properties, directory);
        this.resetStatus();
    }

    private void initialize(Properties properties, Path directory) throws PAPException {
        if (this.id == null || this.id.length() == 0) {
            logger.warn("Cannot initialize with a null or zero length id");
            return;
        }
        //
        // Pull the group's properties
        //
        for (Object key : properties.keySet()) {
            if (key.toString().startsWith(this.id + ".")) {
                if (key.toString().endsWith(STR_APPEND_NAME)) {
                    this.name = properties.getProperty(key.toString());
                } else if (key.toString().endsWith(STR_APPEND_DESCRIPTION)) {
                    this.description = properties.getProperty(key.toString());
                } else if (key.toString().endsWith(STR_APPEND_PDPS)) {
                    String pdpList = properties.getProperty(key.toString());
                    if (pdpList != null && pdpList.length() > 0) {
                        for (String pdpId : Splitter.on(',').omitEmptyStrings().trimResults().split(pdpList)) {
                            StdPDP pdp = new StdPDP(pdpId, properties);
                            pdp.addItemSetChangeListener(this);
                            this.pdps.add(pdp);
                        }
                    }
                }
            }
            // force all policies to have a name
            if (this.name == null) {
                this.name = this.id;
            }
        }
        //
        // Validate our directory
        //
        if (! directory.toFile().exists()) {
            logger.warn("Group directory does NOT exist: " + directory.toString());
            try {
                Files.createDirectory(directory);
                this.status.addLoadWarning(MSG_GROUPNOTEXIST);
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS, MSG_GROUPNOTEXIST);
                this.status.addLoadError(MSG_GROUPNOTEXIST);
                this.status.setStatus(PDPGroupStatus.Status.LOAD_ERRORS);
            }
        }
        //
        // Parse policies
        //
        this.loadPolicies(Paths.get(directory.toString(), PROPS_POLICY));
        //
        // Parse pip config
        //
        this.loadPIPConfig(Paths.get(directory.toString(), PROPS_PIP));
    }

    /**
     * loadPolicies.
     *
     * @param file Path
     * @throws PAPException PAPException
     */
    public void loadPolicies(Path file) throws PAPException {
        //
        // Read the Groups Policies
        //
        Properties policyProperties = new Properties();
        if (!file.toFile().exists()) {
            // need to create the properties file with default values
            policyProperties.setProperty(XACMLProperties.PROP_ROOTPOLICIES, "");
            policyProperties.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
            // save properties to file
            try (OutputStream os = Files.newOutputStream(file)) {
                policyProperties.store(os, "");
            } catch (Exception e) {
                throw new PAPException("Failed to create new default policy properties file '" + file + "'", e);
            }
        } else {
            // load previously existing file
            try {
                //
                // Load the properties
                //
                try (InputStream is = Files.newInputStream(file)) {
                    policyProperties.load(is);
                }
                //
                // Parse the policies
                //
                this.readPolicyProperties(directory, policyProperties);
            } catch (IOException e) {
                logger.warn(MSG_LOADFAILURE + file, e);
                this.status.addLoadError("Not policy properties defined");
                this.status.setStatus(Status.LOAD_ERRORS);
                throw new PAPException(MSG_LOADFAILURE + file);
            }
        }
    }

    /**
     * loadPIPConfig.
     *
     * @param file Path
     * @throws PAPException PAPException
     */
    public void loadPIPConfig(Path file) throws PAPException {
        //
        // Read the Groups' PIP configuration
        //
        Properties pipProperties = new Properties();
        if (!file.toFile().exists()) {
            // need to create the properties file with no values
            pipProperties = setPipProperties(pipProperties);
            // save properties to file
            try {
                try (OutputStream os = Files.newOutputStream(file)) {
                    pipProperties.store(os, "");
                }
            } catch (Exception e) {
                throw new PAPException("Failed to create new default pip properties file '" + file + "'", e);
            }
            // Even if we create a new pip file, we still need to parse and load the properties
            try {
                this.readPipProperties(pipProperties);
            } catch (Exception e) {
                throw new PAPException("Failed to load the new pip properties file", e);
            }
        } else {
            try {
                //
                // Load the properties
                //
                try (InputStream is = Files.newInputStream(file)) {
                    pipProperties.load(is);
                }
                // For all old PIP config's modify to the new PIP Configuration.
                // If PIP is empty add the new values and save it.
                if ("".equals(pipProperties.get(XACMLProperties.PROP_PIP_ENGINES).toString().trim())) {
                    pipProperties = setPipProperties(pipProperties);
                    try (OutputStream os = Files.newOutputStream(file)) {
                        pipProperties.store(os, "");
                    }
                }
                //
                // Parse the pips
                //
                this.readPipProperties(pipProperties);
            } catch (IOException e) {
                logger.warn("Failed to open group PIP Config properties file: " + file, e);
                this.status.addLoadError("Not PIP config properties defined");
                this.status.setStatus(Status.LOAD_ERRORS);
                throw new PAPException(MSG_LOADFAILURE + file);

            }
        }
    }

    /**
     * resetStatus.
     */
    public void resetStatus() {
        //
        // Reset our status object
        //
        this.status.reset();
        //
        // Determine our status
        //
        for (PDP pdp : this.pdps) {
            switch (pdp.getStatus().getStatus()) {
                case OUT_OF_SYNCH:
                    this.status.addOutOfSynchPDP(pdp);
                    break;
                case LAST_UPDATE_FAILED:
                    this.status.addLastUpdateFailedPDP(pdp);
                    break;
                case LOAD_ERRORS:
                    this.status.addFailedPDP(pdp);
                    break;
                case UPDATING_CONFIGURATION:
                    this.status.addUpdatingPDP(pdp);
                    break;
                case UP_TO_DATE:
                    this.status.addInSynchPDP(pdp);
                    break;
                case UNKNOWN:
                case CANNOT_CONNECT:
                case NO_SUCH_HOST:
                default:
                    this.status.addUnknownPDP(pdp);
                    break;
            }
        }

        // priority is worst-cast to best case
        if (!this.status.getUnknownPDPs().isEmpty()) {
            this.status.setStatus(Status.UNKNOWN);
        } else if (!this.status.getFailedPDPs().isEmpty() || !this.status.getLastUpdateFailedPDPs().isEmpty()) {
            this.status.setStatus(Status.LOAD_ERRORS);
        } else if (!this.status.getOutOfSynchPDPs().isEmpty()) {
            this.status.setStatus(Status.OUT_OF_SYNCH);
        } else if (!this.status.getUpdatingPDPs().isEmpty()) {
            this.status.setStatus(Status.UPDATING_CONFIGURATION);
        } else {
            this.status.setStatus(Status.OK);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isDefaultGroup() {
        return this.isDefault;
    }

    /**
     * setDefaultGroup.
     *
     * @param isDefault boolean
     */
    public void setDefaultGroup(boolean isDefault) {
        this.isDefault = isDefault;
        //
        // Cannot fire this because 2 operations have
        // to occur: 1) old default=false (don't want to fire) and
        // then 2) new default=true (yes fire - but we'll have to do that
        // elsewhere.
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String groupName) {
        this.name = groupName;
        this.firePDPGroupChanged(this);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String groupDescription) {
        this.description = groupDescription;
        this.firePDPGroupChanged(this);
    }

    public Path getDirectory() {
        return this.directory;
    }

    public void setDirectory(Path groupDirectory) {
        this.directory = groupDirectory;
        // this is used only for transmission on the RESTful interface, so no need to fire group changed?
    }

    @Override
    public PDPGroupStatus getStatus() {
        return this.status;
    }

    @Override
    public Set<PDPPolicy> getSelectedPolicies() {
        return this.selectedPolicies;
    }

    @Override
    public String getOperation() {
        return this.operation;
    }

    @Override
    public Set<PDP> getPdps() {
        return Collections.unmodifiableSet(pdps);
    }

    public void setOnapPdps(Set<OnapPDP> pdps) {
        this.pdps = pdps;
    }

    @Override
    public Set<OnapPDP> getOnapPdps() {
        return Collections.unmodifiableSet(pdps);
    }

    public boolean addPDP(OnapPDP pdp) {
        return this.pdps.add(pdp);
    }

    public boolean removePDP(PDP pdp) {
        return this.pdps.remove(pdp);
    }

    @Override
    public Set<PDPPolicy> getPolicies() {
        return Collections.unmodifiableSet(this.policies);
    }

    @Override
    public PDPPolicy getPolicy(String id) {
        for (PDPPolicy policy : this.policies) {
            if (policy.getId().equals(id)) {
                return policy;
            }
        }
        return null;
    }

    @Override
    public Properties getPolicyProperties() {
        Properties properties = new Properties() {
            private static final long serialVersionUID = 1L;

            // For Debugging it is helpful for the file to be in a sorted order,
            // any by returning the keys in the natural Alpha order for strings we get close enough.
            // TreeSet is sorted, and this just overrides the normal Properties method to get the keys.
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        List<String> roots = new ArrayList<>();
        List<String> refs = new ArrayList<>();

        for (PDPPolicy policy : this.policies) {
            // for all policies need to tell PDP the "name", which is the base name for the file id
            if (policy.getName() != null) {
                properties.setProperty(policy.getId() + STR_APPEND_NAME, policy.getName());
            }
            // put the policy on the correct list
            if (policy.isRoot()) {
                roots.add(policy.getId());
            } else {
                refs.add(policy.getId());
            }
        }

        properties.setProperty(XACMLProperties.PROP_ROOTPOLICIES, Joiner.on(',').join(roots));
        properties.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, Joiner.on(',').join(refs));

        return properties;
    }

    /**
     * publishPolicy.
     *
     * @param id String
     * @param name String
     * @param isRoot boolean
     * @param policy InputStream
     * @return PDPPolicy
     * @throws PAPException PAPException
     */
    public PDPPolicy publishPolicy(String id, String name, boolean isRoot, InputStream policy) throws PAPException {
        //
        // Does it exist already?
        //
        if (this.getPolicy(id) != null) {
            throw new PAPException("Policy with id " + id + " already exists - unpublish it first.");
        }
        Path tempFile = null;
        try {
            //
            // Copy the policy over
            //
            tempFile = Files.createFile(Paths.get(this.directory.toAbsolutePath().toString(), id));
            long num;
            try (OutputStream os = Files.newOutputStream(tempFile)) {
                num = ByteStreams.copy(policy, os);
            }
            logger.info("Copied " + num + " bytes for policy " + name);

            StdPDPPolicy tempRootPolicy = new StdPDPPolicy(id, isRoot, name, tempFile.toUri());
            if (!tempRootPolicy.isValid()) {
                try {
                    Files.delete(tempFile);
                } catch (Exception ee) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, ee, STR_CLASS,
                            "Policy was invalid, could NOT delete it.");
                }
                throw new PAPException("Policy is invalid");
            }
            //
            // Add it in
            //
            this.policies.add(tempRootPolicy);
            //
            // We are changed
            //
            this.firePDPGroupChanged(this);
            //
            // Return our new object.
            //
            return tempRootPolicy;
        } catch (IOException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, STR_CLASS, "Failed to publishPolicy");
        }
        return null;
    }

    /**
     * Copy one policy file into the Group's directory but do not change the configuration.
     * This is one part of a multi-step process of publishing policies.
     * There may be multiple changes in the group (adding multiple policies, deleting policies, changine
     * root<->referenced)
     * that must be done all at once, so we just copy the file in preparation for a later "update whole group"
     * operation.
     *
     * @param id String
     * @param policy InputStream
     * @throws PAPException PAPException
     */
    public void copyPolicyToFile(String id, InputStream policy) throws PAPException {
        copyPolicyToFile(id, this.name, policy);
    }

    /**
     * Policy Engine API Copy one policy file into the Group's directory but do not change the configuration.
     *
     * @param id String
     * @param fileName String
     * @param policy InputStream
     * @throws PAPException PAPException
     */
    public void copyPolicyToFile(String id, String fileName, InputStream policy) throws PAPException {
        try {
            //
            // Copy the policy over
            //
            long num;
            Path policyFilePath = Paths.get(this.directory.toAbsolutePath().toString(), id);

            Path policyFile;
            if (policyFilePath.toFile().exists()) {
                policyFile = policyFilePath;
            } else {
                policyFile = Files.createFile(policyFilePath);
            }

            try (OutputStream os = Files.newOutputStream(policyFile)) {
                num = ByteStreams.copy(policy, os);
            }

            logger.info("Copied " + num + " bytes for policy " + fileName);
            for (PDPPolicy p : policies) {
                if (p.getId().equals(id)) {
                    // we just re-copied/refreshed/updated the policy file for a policy that already exists in this
                    // group
                    logger.info("Policy '" + id + "' already exists in group '" + getId() + "'");
                    return;
                }
            }

            // policy is new to this group
            StdPDPPolicy tempRootPolicy = new StdPDPPolicy(id, true, fileName, policyFile.toUri());
            if (!tempRootPolicy.isValid()) {
                try {
                    Files.delete(policyFile);
                } catch (Exception ee) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, ee, STR_CLASS,
                            "Policy was invalid, could NOT delete it.");
                }
                throw new PAPException("Policy is invalid");
            }
            //
            // Add it in
            //
            this.policies.add(tempRootPolicy);
            //
            // We are changed
            //
            this.firePDPGroupChanged(this);

        } catch (IOException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS, "Failed to copyPolicyToFile");
            throw new PAPException("Failed to copy policy to file: " + e);
        }
    }

    /**
     * removePolicyFromGroup.
     *
     * @param policy PDPPolicy
     * @return boolean
     */
    public boolean removePolicyFromGroup(PDPPolicy policy) {
        PolicyLogger.info("policy: " + policy.getId());
        PolicyLogger.info("Policy ID:" + policy.getPolicyId());
        PolicyLogger.info("Policy Version: " + policy.getVersion());
        PolicyLogger.info("StdPDPPolicy Class cast: " + this.getPolicy(policy.getId()).toString());
        StdPDPPolicy currentPolicy = (StdPDPPolicy) this.getPolicy(policy.getId());
        if (currentPolicy == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Policy " + policy.getId() + " does not exist.");
            return false;
        }
        try {
            //
            // Remove it from our list
            //
            this.policies.remove(currentPolicy);
            //
            // We are changed
            //
            this.firePDPGroupChanged(this);
            return true;
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS, "Failed to delete policy");
        }
        return false;
    }

    /**
     * removePolicy.
     *
     * @param policy PDPPolicy
     * @return boolean
     */
    public boolean removePolicy(PDPPolicy policy) {
        PDPPolicy currentPolicy = this.getPolicy(policy.getId());
        if (currentPolicy == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Policy " + policy.getId() + " does not exist.");
            return false;
        }
        try {
            //
            // Delete it on disk
            //
            Files.delete(Paths.get(currentPolicy.getLocation()));
            //
            // Remove it from our list
            //
            this.policies.remove(currentPolicy);
            //
            // We are changed
            //
            this.firePDPGroupChanged(this);
            return true;
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS, "Failed to delete policy " + policy);
        }
        return false;
    }

    @Override
    public Set<PDPPIPConfig> getPipConfigs() {
        return Collections.unmodifiableSet(this.pipConfigs);
    }

    @Override
    public PDPPIPConfig getPipConfig(String id) {
        for (PDPPIPConfig config : this.pipConfigs) {
            if (config.getId().equals(id)) {
                return config;
            }
        }
        return null;
    }

    public void setPipConfigs(Set<PDPPIPConfig> pipConfigs) {
        this.pipConfigs = pipConfigs;
        this.firePDPGroupChanged(this);
    }

    public void removeAllPIPConfigs() {
        this.pipConfigs.clear();
        this.firePDPGroupChanged(this);
    }

    @Override
    public Properties getPipConfigProperties() {
        Properties properties = new Properties();
        List<String> configs = new ArrayList<>();

        for (PDPPIPConfig config : this.pipConfigs) {
            configs.add(config.getId());
            properties.putAll(config.getConfiguration());
        }

        properties.setProperty(XACMLProperties.PROP_PIP_ENGINES, Joiner.on(',').join(configs));

        return properties;
    }

    @Override
    public void repair() {
        //
        // Reset the status object
        //
        this.status.reset();
        //
        // Validate our directory
        //
        boolean fire = false;
        if (! directory.toFile().exists()) {
            logger.warn("Group directory does NOT exist: " + directory.toString());
            try {
                Files.createDirectory(directory);
                fire = true;
                this.status.addLoadWarning("Created missing group directory");
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS,
                        "Failed to create missing Group directory.");
                this.status.addLoadError("Failed to create missing Group directory.");
                this.status.setStatus(PDPGroupStatus.Status.LOAD_ERRORS);
            }
        }
        //
        // Validate our PIP config file
        //
        Path pipPropertiesFile = Paths.get(directory.toString(), PROPS_PIP);
        if (! pipPropertiesFile.toFile().exists()) {
            try {
                Files.createFile(pipPropertiesFile);
                fire = true;
                this.status.addLoadWarning("Created missing PIP properties file");
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS,
                        "Failed to create missing PIP properties file");
                this.status.addLoadError("Failed to create missing PIP properties file");
                this.status.setStatus(PDPGroupStatus.Status.LOAD_ERRORS);
            }
        }
        //
        // Valid our policy properties file
        //
        Path policyPropertiesFile = Paths.get(directory.toString(), PROPS_POLICY);
        if (! policyPropertiesFile.toFile().exists()) {
            try {
                Files.createFile(policyPropertiesFile);
                fire = true;
                this.status.addLoadWarning("Created missing Policy properties file");
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS,
                        "Failed to create missing Policy properties file");
                this.status.addLoadError("Failed to create missing Policy properties file");
                this.status.setStatus(PDPGroupStatus.Status.LOAD_ERRORS);
            }
        }
        this.resetStatus();
        if (fire) {
            this.fireChanged();
        }
    }

    private void readPolicyProperties(Path directory, Properties properties) {
        //
        // There are 2 property values that hold policies, root and referenced
        //
        String[] lists = new String[2];
        lists[0] = properties.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        lists[1] = properties.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        //
        // Iterate each policy list
        //
        boolean isRoot = true;
        for (String list : lists) {
            //
            // Was there actually a property?
            //
            if (list == null || list.length() == 0) {
                isRoot = false;
                continue;
            }
            //
            // Parse it out
            //
            Iterable<String> policyList = Splitter.on(',').trimResults().omitEmptyStrings().split(list);
            //
            // Was there actually a list
            //
            if (policyList == null) {
                isRoot = false;
                continue;
            }
            for (String policyId : policyList) {
                //
                // Construct the policy filename
                //
                Path policyPath = Paths.get(directory.toString(), policyId);
                //
                // Create the Policy Object
                //
                StdPDPPolicy policy;
                try {
                    policy = new StdPDPPolicy(id, isRoot, policyPath.toUri(), properties);
                } catch (IOException e) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS,
                            "Failed to create policy object");
                    policy = null;
                }
                //
                // Is it valid?
                //
                if (policy != null && policy.isValid()) {
                    this.policies.add(policy);
                    this.status.addLoadedPolicy(policy);
                } else {
                    this.status.addFailedPolicy(policy);
                    this.status.setStatus(Status.LOAD_ERRORS);
                }
                // force all policies to have a name
                if (policy != null && policy.getName() == null) {
                    policy.setName(policy.getId());
                }
            }
            isRoot = false;
        }
    }

    private void readPipProperties(Properties properties) {
        String list = properties.getProperty(XACMLProperties.PROP_PIP_ENGINES);
        if (list == null || list.length() == 0) {
            return;
        }
        for (String pipId : list.split("[,]")) {
            StdPDPPIPConfig config = new StdPDPPIPConfig(pipId, properties);
            if (config.isConfigured()) {
                this.pipConfigs.add(config);
                this.status.addLoadedPipConfig(config);
            } else {
                this.status.addFailedPipConfig(config);
                this.status.setStatus(Status.LOAD_ERRORS);
            }
        }
    }

    @Override
    public void changed() {

        // save the (changed) properties
        try {
            saveGroupConfiguration();
        } catch (PAPException | IOException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, STR_CLASS,
                    "Unable to save group configuration change");
            // don't notify other things of change if we cannot save it???
            return;
        }

        this.firePDPGroupChanged(this);

    }

    @Override
    public void groupChanged(OnapPDPGroup group) {
        this.changed();
    }

    @Override
    public void pdpChanged(OnapPDP pdp) {
        //
        // If one of the group's PDP's changed, then the group changed
        //
        this.changed();
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setStatus(PDPGroupStatus status) {
        this.status = new StdPDPGroupStatus(status);
    }

    public void setPolicies(Set<PDPPolicy> policies) {
        this.policies = policies;
    }

    public void setSelectedPolicies(Set<PDPPolicy> selectedPolicies) {
        this.selectedPolicies = selectedPolicies;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * saveGroupConfiguration.
     *
     * @throws PAPException PAPException
     * @throws IOException IOException
     */
    public void saveGroupConfiguration() throws PAPException, IOException {

        // First save the Policy properties

        // save the lists of policies
        Properties policyProperties = this.getPolicyProperties();

        // save info about each policy
        for (PDPPolicy policy : this.policies) {
            policyProperties.put(policy.getId() + STR_APPEND_NAME, policy.getName());
        }

        //
        // Now we can save the file
        //
        Path file = Paths.get(this.directory.toString(), PROPS_POLICY);
        try (OutputStream os = Files.newOutputStream(file)) {
            policyProperties.store(os, "");
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "STdPDPGroup", "Group Policies Config save failed");
            throw new PAPException("Failed to save policy properties file '" + file + "'");
        }

        // Now save the PIP Config properties
        Properties pipProperties = this.getPipConfigProperties();

        //
        // Now we can save the file
        //
        file = Paths.get(this.directory.toString(), PROPS_PIP);
        try (OutputStream os = Files.newOutputStream(file)) {
            pipProperties.store(os, "");
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, STR_CLASS, "Group PIP Config save failed");
            throw new PAPException("Failed to save pip properties file '" + file + "'");
        }
    }

    //
    // Comparable Interface
    //
    @Override
    public int compareTo(Object arg0) {
        if (arg0 == null) {
            return -1;
        }
        if (!(arg0 instanceof StdPDPGroup)) {
            return -1;
        }
        if (((StdPDPGroup) arg0).name == null) {
            return -1;
        }
        if (name == null) {
            return 1;
        }

        return name.compareTo(((StdPDPGroup) arg0).name);
    }

    // Adding Default PIP engine(s) while Loading initially. We don't want
    // Programmer intervention with the PIP engines.
    private Properties setPipProperties(Properties props) {
        props.setProperty("AAF.name", "AAFEngine");
        props.setProperty("AAF.description", "AAFEngine to communicate with AAF to take decisions");
        props.setProperty("AAF.classname", "org.onap.policy.xacml.std.pip.engines.aaf.AAFEngine");
        props.setProperty(XACMLProperties.PROP_PIP_ENGINES, "AAF");
        // read from PIP properties file.
        Path file = Paths.get(StdEngine.PIP_PROPERTY_FILE);
        if (file.toFile().exists()) {
            InputStream in;
            Properties prop = new Properties();
            try {
                in = new FileInputStream(file.toFile());
                prop.load(in);
            } catch (IOException e) {
                PolicyLogger.error(
                        XACMLErrorConstants.ERROR_SYSTEM_ERROR + "can not load the pip properties from file" + e);
            }
            props = prop;
        }
        return props;
    }

}
