/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.brms.api;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.ProcessingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.onap.policy.api.PEDependency;
import org.onap.policy.api.PolicyException;
import org.onap.policy.brms.api.nexus.NexusRestSearchParameters;
import org.onap.policy.brms.api.nexus.NexusRestWrapper;
import org.onap.policy.brms.api.nexus.NexusRestWrapperException;
import org.onap.policy.brms.api.nexus.pojo.NexusArtifact;
import org.onap.policy.brms.entity.BrmsGroupInfo;
import org.onap.policy.brms.entity.BrmsPolicyInfo;
import org.onap.policy.brms.entity.DependencyInfo;
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.BackUpHandler;
import org.onap.policy.utils.BackUpMonitor;
import org.onap.policy.utils.BusPublisher;
import org.onap.policy.utils.PeCryptoUtils;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;

/**
 * BRMSPush: Application responsible to push policies to the BRMS PDP Policy Repository (PR).
 * Mavenize and push policy to PR
 *
 * @version 1.0
 */

public class BrmsPush {
    private static final String GROUP_NAMES = "groupNames";
    private static final String DROOLS_APPS_TEMPLATE_GROUP =
            "org.onap.policy.drools-applications.controlloop.templates";
    private static final String DROOLS_APPS_MODEL_GROUP =
            "org.onap.policy.models.policy-models-interactions.model-impl";
    private static final String META_INF = "META-INF";
    private static final String KMODULE_XML_FILE = "kmodule.xml";
    private static final String POM_XML_FILE = "pom.xml";
    private static final String VERSION_0_1_0 = "0.1.0";
    private static final String RULES = "rules";
    private static final String RESOURCES = "resources";
    private static final Logger LOGGER = FlexLogger.getLogger(BrmsPush.class.getName());
    private static final String PROJECTSLOCATION = "RuleProjects";
    private static final String[] GOALS = {"clean", "deploy"};
    private static final String DEFAULT_VERSION = "1.5.4";
    private static final String DEPENDENCY_FILE = "dependency.json";
    private static final String PROP_AES_KEY = "org.onap.policy.encryption.aes.key";
    public static final String BRMSPERSISTENCE = "brmsEclipselink.persistencexml";

    private static Map<String, String> modifiedGroups = new HashMap<>();
    private static IntegrityMonitor im;
    private static BackUpMonitor bm;
    private String defaultName = null;
    private String repId = null;
    private String repName = null;
    private List<String> repUrlList = null;
    private String repUserName = null;
    private String repPassword = null;
    private String policyKeyId = null;
    private boolean createFlag = false;
    private String uebList = null;
    private List<String> dmaapList = null;
    private String pubTopic = null;
    private PublisherBuilder pubBuilder = null;
    protected BusPublisher publisher = null;
    private Long uebDelay = Long.parseLong("20");
    private Long dmaapDelay = Long.parseLong("5000");
    private String notificationType = null;
    private List<ControllerPojo> controllers;
    private Map<String, ArrayList<Object>> groupMap = new HashMap<>();
    private final Map<String, String> policyMap = new HashMap<>();
    private String brmsdependencyversion;
    private EntityManager em;
    private boolean syncFlag = false;

    /**
     * Responsible to push policies to the BRMS PDP Policy Repository (PR).
     *
     * @param propertiesFile the properties file
     * @param handler the {@link BackUpHandler}
     * @throws PolicyException PolicyException related to the operation
     */
    public BrmsPush(final String propertiesFile, final BackUpHandler handler) throws PolicyException {
        if (propertiesFile == null || handler == null) {
            throw new PolicyException("Error no propertiesFile or handler");
        }
        final Properties config = new Properties();
        final Path file = Paths.get(propertiesFile);
        if (Files.notExists(file)) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Config File doesn't Exist in the specified Path "
                    + file.toString());
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "Config File doesn't Exist in the specified Path " + file.toString());
        } else {
            if (file.toString().endsWith(".properties")) {
                // Grab the Properties.
                setProperty(file, config, handler);
            }
        }
    }

    private void setProperty(final Path file, final Properties config, final BackUpHandler handler)
            throws PolicyException {
        InputStream in;
        try {
            in = new FileInputStream(file.toFile());
            config.load(in);
        } catch (final IOException e) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Data/File Read Error while reading from the property file.",
                    e);
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "Data/File Read Error while reading from the property file.");
        }
        // init the aes key from prop or env
        PeCryptoUtils.initAesKey(config.getProperty(PROP_AES_KEY));

        LOGGER.info("Trying to set up IntegrityMonitor");
        String resourceName = null;
        try {
            LOGGER.info("Trying to set up IntegrityMonitor");
            resourceName = config.getProperty("RESOURCE_NAME");
            if (resourceName == null) {
                LOGGER.warn("RESOURCE_NAME is missing setting default value. ");
                resourceName = "brmsgw_pdp01";
            }
            resourceName = resourceName.trim();
            setIntegrityMonitor(IntegrityMonitor.getInstance(resourceName, config));
        } catch (final Exception e) {
            LOGGER.error("Error starting Integerity Monitor: " + e);
        }
        LOGGER.info("Trying to set up BackUpMonitor");
        try {
            setBackupMonitor(BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), resourceName, config,
                    handler));
        } catch (final Exception e) {
            LOGGER.error("Error starting BackUpMonitor: " + e);
        }
        if (!config.containsKey(BRMSPERSISTENCE)) {
            config.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistenceBRMS.xml");
        } else {
            config.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML,
                    config.getProperty(BRMSPERSISTENCE, "META-INF/persistenceBRMS.xml"));
        }
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRMSGW", config);
        em = emf.createEntityManager();
        defaultName = config.getProperty("defaultName");
        if (defaultName == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "defaultName property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "defaultName property is missing from the property file");
        }
        defaultName = defaultName.trim();
        repId = config.getProperty("repositoryID");
        if (repId == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryID property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryID property is missing from the property file ");
        }
        repId = repId.trim();
        repName = config.getProperty("repositoryName");
        if (repName == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repositoryName property is missing from the property file ");
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repositoryName property is missing from the property file ");
        }
        repName = repName.trim();
        final String repUrl = config.getProperty("repositoryURL");
        if (repUrl == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryURL property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryURL property is missing from the property file ");
        }
        if (repUrl.contains(",")) {
            repUrlList = new ArrayList<>(Arrays.asList(repUrl.trim().split(",")));
        } else {
            repUrlList = new ArrayList<>();
            repUrlList.add(repUrl);
        }
        repUserName = config.getProperty("repositoryUsername");
        repPassword = PeCryptoUtils.decrypt(config.getProperty("repositoryPassword"));
        if (repUserName == null || repPassword == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repostoryUserName and respositoryPassword properties are required.");
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repostoryUserName and respositoryPassword properties are required.");
        }
        repUserName = repUserName.trim();
        repPassword = repPassword.trim();
        policyKeyId = config.getProperty("policyKeyID");
        if (policyKeyId == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "policyKeyID property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "policyKeyID property is missing from the property file ");
        }
        policyKeyId = policyKeyId.trim();
        final String syncF = config.getProperty("sync", "false").trim();
        syncFlag = Boolean.parseBoolean(syncF);
        if (syncFlag) {
            PolicyLogger.info("SYNC Flag is turned ON. DB will be given Priority.");
        }
        brmsdependencyversion = config.getProperty("brms.dependency.version");
        if (brmsdependencyversion == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "brmsdependencyversion property is missing from the property file, Using default Version.");
            brmsdependencyversion = DEFAULT_VERSION;
        }
        brmsdependencyversion = brmsdependencyversion.trim();
        readGroups(config);

        // Setup Publisher
        notificationType = config.getProperty("NOTIFICATION_TYPE");
        if ("dmaap".equalsIgnoreCase(notificationType)) {

            LOGGER.info("Notification Type being used is DMaaP... creating instance of BusPublisher.");
            // Setting up the Publisher for DMaaP MR
            String dmaapServers = config.getProperty("NOTIFICATION_SERVERS");
            pubTopic = config.getProperty("NOTIFICATION_TOPIC");
            final String aafLogin = config.getProperty("CLIENT_ID").trim();
            final String aafPassword = config.getProperty("CLIENT_KEY").trim();

            if (dmaapServers == null || pubTopic == null) {
                LOGGER.error(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "DMaaP properties are missing from the property file ");
                throw new PolicyException(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "DMaaP properties are missing from the property file ");
            }

            dmaapServers = dmaapServers.trim();
            pubTopic = pubTopic.trim();

            if (dmaapServers.contains(",")) {
                dmaapList = new ArrayList<>(Arrays.asList(dmaapServers.split("\\s*,\\s*")));
            } else {
                dmaapList = new ArrayList<>();
                dmaapList.add(dmaapServers);
            }

            this.publisher =
                    new BusPublisher.DmaapPublisherWrapper(this.dmaapList, this.pubTopic, aafLogin, aafPassword);

            String notificationDelay = config.getProperty("NOTIFICATION_DELAY");
            if (notificationDelay != null && !notificationDelay.isEmpty()) {
                notificationDelay = notificationDelay.trim();
                try {
                    dmaapDelay = Long.parseLong(notificationDelay);
                } catch (final NumberFormatException e) {
                    LOGGER.error("DMAAP_DELAY not a long format number" + e);
                }
            }
            LOGGER.info("DMAAP BusPublisher is created.");

        } else {
            LOGGER.info("Notification Type being used is UEB... creating instance of PublisherBuilder.");
            // Setting up the Publisher for UEB
            uebList = config.getProperty("NOTIFICATION_SERVERS");
            pubTopic = config.getProperty("NOTIFICATION_TOPIC");
            if (uebList == null || pubTopic == null) {
                LOGGER.error(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
                throw new PolicyException(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
            }
            uebList = uebList.trim();
            pubTopic = pubTopic.trim();
            pubBuilder = new CambriaClientBuilders.PublisherBuilder();
            pubBuilder.usingHosts(uebList).onTopic(pubTopic).usingHttps(true);
            String apiKey = config.getProperty("UEB_API_KEY");
            String apiSecret = config.getProperty("UEB_API_SECRET");
            if (apiKey != null && !apiKey.isEmpty() && apiSecret != null && !apiSecret.isEmpty()) {
                apiKey = apiKey.trim();
                apiSecret = apiSecret.trim();
                pubBuilder.authenticatedBy(apiKey, apiSecret);
            }
            String notificationDelay = config.getProperty("NOTIFICATION_DELAY");
            if (notificationDelay != null && !notificationDelay.isEmpty()) {
                notificationDelay = notificationDelay.trim();
                try {
                    uebDelay = Long.parseLong(notificationDelay);
                } catch (final NumberFormatException e) {
                    LOGGER.error("UEB_DELAY not a long format number" + e);
                }
            }
            LOGGER.info("UEB PublisherBuilder is created.");

        }

    }

    private static void setBackupMonitor(final BackUpMonitor instance) {
        bm = instance;
    }

    private static void setIntegrityMonitor(final IntegrityMonitor instance) {
        im = instance;
    }

    /**
     * Will Initialize the variables required for BRMSPush.
     */
    public void initiate(final boolean flag) {
        resetModifiedGroups();
        controllers = new ArrayList<>();
        try {
            bm.updateNotification();
        } catch (final Exception e) {
            LOGGER.error("Error while updating Notification: " + e.getMessage(), e);
        }
        if (flag) {
            syncGroupInfo();
        }
    }

    public void resetDs() {
        resetModifiedGroups();
        controllers = new ArrayList<>();
    }

    private static void resetModifiedGroups() {
        modifiedGroups = new HashMap<>();
    }

    /**
     * Will Add rules to projects. Creates necessary folders if required.
     */
    public void addRule(final String name, final String rule, final Map<String, String> responseAttributes) {
        // 1 check the response Attributes and determine if this belongs to any projects.
        // 2 if not create folder
        // 3 create pom.
        // 4 copy the rule.
        // 5 store the groups that have been updated.
        String ksessionName = null;
        String selectedName = null;
        if (!responseAttributes.isEmpty()) {
            // Pick selected Value
            String userControllerName = null;
            final ArrayList<PEDependency> userDependencies = new ArrayList<>();
            for (final Map.Entry<String, String> entry : responseAttributes.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                if (key.equals(policyKeyId)) {
                    selectedName = value;
                }
                // kmodule configurations
                else if ("kSessionName".equals(key)) {
                    ksessionName = value;
                }
                // Check User Specific values.
                if ("$controller:".equals(key)) {
                    userControllerName = getUserControllerName(key, value);
                } else if ("$dependency$".equals(key) && value.startsWith("[") && value.endsWith("]")) {
                    updateUserDependencies(userDependencies, value);
                }
            }
            if (userControllerName != null) {
                // Adding custom dependencies here.
                final ArrayList<Object> values = groupMap.get(userControllerName);
                values.add(userDependencies);
                groupMap.put(userControllerName, values);
                selectedName = userControllerName;
            }
        }
        // If no Match then pick Default.
        if (selectedName == null) {
            selectedName = defaultName;
        }
        if (groupMap.containsKey(selectedName)) {
            // If the key is not got as parameters set by the user, setting the default value for
            // kSessionName as
            // closedLoop
            if (ksessionName == null) {
                LOGGER.info("kSessionName is null, selectedName is  : " + selectedName);
                if (selectedName.equalsIgnoreCase(defaultName)) {
                    ksessionName = "closedloop";
                } else {
                    ksessionName = "closedloop-" + selectedName;
                }
            }
            // create directories if missing.
            manageProject(selectedName, ksessionName, name, rule);

            // Will check for Create Later after generating the Pom.
            addModifiedGroup(selectedName, "update");
        }
    }

    private String getUserControllerName(final String key, final String value) {
        String userControllerName = null;
        // Check User Specific values.
        try {
            final PEDependency dependency = PolicyUtils.jsonStringToObject(value, PEDependency.class);
            userControllerName = key.replaceFirst("$controller:", "");
            LOGGER.info("addRule: userControllerName - " + userControllerName + ", dependency: - " + dependency);
            addToGroup(userControllerName, dependency);
        } catch (final Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while resolving Controller: " + e);
        }
        return userControllerName;
    }

    private void updateUserDependencies(final ArrayList<PEDependency> userDependencies, String value) {
        // update the user dependencies supplied as parameter to this method
        value = value.substring(1, value.length() - 1).trim();
        final List<String> dependencyStrings = Arrays.asList(value.split(Pattern.quote("},{")));
        for (final String dependencyString : dependencyStrings) {
            try {
                userDependencies.add(PolicyUtils.jsonStringToObject(dependencyString, PEDependency.class));
            } catch (final Exception e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while resolving Dependencies: " + e);
            }
        }
    }

    private void syncGroupInfo() {
        // Sync DB to JMemory.
        final EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            final TypedQuery<BrmsGroupInfo> groupInfoQuery =
                    em.createQuery("select b from BrmsGroupInfo AS b", BrmsGroupInfo.class);
            final List<BrmsGroupInfo> groupInfoResult = groupInfoQuery.getResultList();
            if (groupInfoResult.size() != groupMap.size()) {
                for (final BrmsGroupInfo brmsGroupInfo : groupInfoResult) {
                    final PEDependency dependency = new PEDependency();
                    dependency.setArtifactId(brmsGroupInfo.getArtifactId());
                    dependency.setGroupId(brmsGroupInfo.getGroupId());
                    dependency.setVersion(brmsGroupInfo.getVersion());
                    final ArrayList<Object> values = new ArrayList<>();
                    values.add(dependency);
                    groupMap.put(brmsGroupInfo.getControllerName(), values);
                }
            }

            final TypedQuery<BrmsPolicyInfo> policyInfoQuery =
                    em.createQuery("select g from BrmsPolicyInfo AS g", BrmsPolicyInfo.class);
            final List<BrmsPolicyInfo> policyInfoResult = policyInfoQuery.getResultList();
            if (policyInfoResult.size() != policyMap.size()) {
                for (final BrmsPolicyInfo brmsPolicyInfo : policyInfoResult) {
                    policyMap.put(brmsPolicyInfo.getPolicyName(),
                            brmsPolicyInfo.getControllerName().getControllerName());
                }
            }
            et.commit();
            LOGGER.info("Updated Local Memory values with values from database.");
        } catch (final Exception exception) {
            LOGGER.error("Unable to sync group info", exception);
            if (et.isActive()) {
                et.rollback();
            }

        }
    }

    private void manageProject(final String selectedName, final String ksessionName, final String name,
            final String rule) {
        // Check if the Project is in Sync. If not get the latest Version.
        syncProject(selectedName);
        createProject(PROJECTSLOCATION + File.separator + getArtifactId(selectedName) + File.separator + "src"
                + File.separator + "main" + File.separator + RESOURCES, ksessionName);
        copyDataToFile(PROJECTSLOCATION + File.separator + getArtifactId(selectedName) + File.separator + "src"
                + File.separator + "main" + File.separator + RESOURCES + File.separator + RULES + File.separator + name
                + ".drl", rule);
        addToPolicy(name, selectedName);
    }

    /*
     * Add Policy to JMemory and DataBase.
     */
    private void addToPolicy(final String policyName, final String controllerName) {

        final EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            boolean create = false;
            final TypedQuery<BrmsPolicyInfo> policyInfoQuery =
                    em.createQuery("select b from BrmsPolicyInfo as b where b.policyName = :pn", BrmsPolicyInfo.class);
            policyInfoQuery.setParameter("pn", policyName);
            final List<BrmsPolicyInfo> policyInfoResultList = policyInfoQuery.getResultList();
            BrmsPolicyInfo brmsPolicyInfo = new BrmsPolicyInfo();
            if (!policyInfoResultList.isEmpty()) {
                // Already exists.
                brmsPolicyInfo = policyInfoResultList.get(0);
                if (!brmsPolicyInfo.getControllerName().getControllerName().equals(controllerName)) {
                    create = true;
                }
            } else {
                create = true;
            }
            if (create) {
                final TypedQuery<BrmsGroupInfo> groupInfoQuery = em.createQuery(
                        "select b from BrmsGroupInfo as b where b.controllerName = :cn", BrmsGroupInfo.class);
                groupInfoQuery.setParameter("cn", controllerName);
                final List<BrmsGroupInfo> groupInfoResultList = groupInfoQuery.getResultList();
                BrmsGroupInfo brmsGroupInfo = new BrmsGroupInfo();
                if (!groupInfoResultList.isEmpty()) {
                    brmsGroupInfo = groupInfoResultList.get(0);
                }
                brmsPolicyInfo.setPolicyName(policyName);
                brmsPolicyInfo.setControllerName(brmsGroupInfo);
                em.persist(brmsPolicyInfo);
                em.flush();
            }
            et.commit();

            policyMap.put(policyName, controllerName);
        } catch (final Exception exception) {
            LOGGER.error("Unable add policy to database", exception);
            et.rollback();
        }
    }

    private void syncProject(final String selectedName) {
        final boolean projectExists = checkProject(selectedName);
        if (projectExists) {
            String version;
            version = getVersion(selectedName);
            if (version == null) {
                LOGGER.error("Error getting local version for the given Controller Name:" + selectedName
                        + " going with Default value");
                version = VERSION_0_1_0;
            }
            final String nextVersion = incrementVersion(version);
            final boolean outOfSync = checkRemoteSync(selectedName, nextVersion);
            if (!outOfSync) {
                return;
            }
        }
        // We are out of Sync or Project is not Present.
        downloadProject(selectedName);
    }

    private void downloadProject(final String selectedName) {
        final NexusArtifact artifact = getLatestArtifactFromNexus(selectedName);
        if (artifact == null) {
            return;
        }
        final String dirName = getDirectoryName(selectedName);
        URL website;
        final String fileName = "rule.jar";
        try {
            website = new URL(artifact.getUrlPath() + ".jar");
            try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                extractJar(fileName, dirName);
                new File(fileName).delete();
            } catch (final IOException e) {
                LOGGER.error("Error while downloading the project to File System. " + e.getMessage(), e);
            }

        } catch (final IOException e1) {
            LOGGER.error("Error while retrieve the artifact. " + e1.getMessage(), e1);
        }
    }

    private void extractJar(final String jarFileName, final String artifactId) {
        try (JarFile jar = new JarFile(jarFileName)) {
            final Enumeration<?> enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                parseJarContents(artifactId, jar, enumEntries);
            }
        } catch (final IOException e) {
            LOGGER.info("exception Occured" + e);
        }
    }

    private void parseJarContents(final String artifactId, final JarFile jar, final Enumeration<?> enumEntries) {
        final JarEntry jarEntry = (JarEntry) enumEntries.nextElement();
        File file = null;
        final String fileName = jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/") + 1);
        if (jarEntry.getName().endsWith(".drl")) {
            final String path = PROJECTSLOCATION + File.separator + artifactId + File.separator + "src" + File.separator
                    + "main" + File.separator + RESOURCES + File.separator + RULES;
            new File(path).mkdirs();
            if (syncFlag && policyMap.containsKey(fileName.replace(".drl", ""))) {
                file = new File(path + File.separator + fileName);
            } else {
                file = new File(path + File.separator + fileName);
            }
        } else if (jarEntry.getName().endsWith(POM_XML_FILE)) {
            final String path = PROJECTSLOCATION + File.separator + artifactId;
            new File(path).mkdirs();
            file = new File(path + File.separator + fileName);
        } else if (jarEntry.getName().endsWith(KMODULE_XML_FILE)) {
            final String path = PROJECTSLOCATION + File.separator + artifactId + File.separator + "src" + File.separator
                    + "main" + File.separator + RESOURCES + File.separator + META_INF;
            new File(path).mkdirs();
            file = new File(path + File.separator + fileName);
        }
        if (file != null) {
            try (InputStream is = jar.getInputStream(jarEntry); FileOutputStream fos = new FileOutputStream(file)) {
                while (is.available() > 0) {
                    fos.write(is.read());
                }
                LOGGER.info(fileName + " Created..");
            } catch (final IOException e) {
                LOGGER.info("exception Occured" + e);
            }
        }
    }

    private NexusArtifact getLatestArtifactFromNexus(final String selectedName) {
        final List<NexusArtifact> artifacts = getArtifactFromNexus(selectedName, null);
        int bigNum = 0;
        int smallNum = 0;
        NexusArtifact result = null;
        for (final NexusArtifact artifact : artifacts) {
            final int majorVal =
                    Integer.parseInt(artifact.getVersion().substring(0, artifact.getVersion().indexOf(".")));
            final int minorVal = Integer.parseInt(artifact.getVersion()
                    .substring(artifact.getVersion().indexOf(".") + 1, artifact.getVersion().lastIndexOf(".")));
            if (majorVal > bigNum) {
                bigNum = majorVal;
                smallNum = minorVal;
            }
            if ((bigNum == majorVal) && (minorVal > smallNum)) {
                smallNum = minorVal;
            }
            if (bigNum == majorVal && minorVal == smallNum) {
                result = artifact;
            }
        }
        return additionalNexusLatestCheck(selectedName, result);
    }

    // Additional Check due to Limitations from Nexus API to check if the artifact is the latest.
    private NexusArtifact additionalNexusLatestCheck(final String selectedName, final NexusArtifact result) {
        if (result == null) {
            return result;
        }
        final String nextVersion = incrementVersion(result.getVersion());
        final List<NexusArtifact> artifact = getArtifactFromNexus(selectedName, nextVersion);
        return artifact.isEmpty() ? result : additionalNexusLatestCheck(selectedName, artifact.get(0));
    }

    private boolean checkRemoteSync(final String selectedName, final String version) {
        final List<NexusArtifact> artifacts = getArtifactFromNexus(selectedName, version);
        return artifacts.isEmpty() ? false : true;
    }

    private List<NexusArtifact> getArtifactFromNexus(final String selectedName, final String version) {
        NexusRestWrapper restWrapper = null;
        int index = 0;
        boolean flag = false;
        while (index < repUrlList.size()) {
            try {
                final String repUrl = repUrlList.get(0);
                restWrapper =
                        new NexusRestWrapper(repUrl.substring(0, repUrl.indexOf(repUrl.split(":[0-9]+\\/nexus")[1])),
                                repUserName, repPassword);
                final NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
                searchParameters.useFilterSearch(getGroupId(selectedName), getArtifactId(selectedName), version, null,
                        null);

                final List<NexusArtifact> resultList = restWrapper.findArtifact(searchParameters).getArtifactList();
                if (resultList != null) {
                    flag = true;
                    return resultList;
                }
            } catch (NexusRestWrapperException | ProcessingException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Connection to remote Nexus has failed. "
                        + e.getMessage(), e);
            } finally {
                if (null != restWrapper) {
                    restWrapper.close();
                }
                if (!flag) {
                    Collections.rotate(repUrlList, -1);
                    index++;
                }
            }
        }
        return new ArrayList<>();
    }

    private String incrementVersion(final String version) {
        int majorVal = Integer.parseInt(version.substring(0, version.indexOf(".")));
        int minorVal = Integer.parseInt(version.substring(version.indexOf(".") + 1, version.lastIndexOf(".")));
        if (minorVal >= 9) {
            majorVal += 1;
            minorVal = 0;
        } else {
            minorVal += 1;
        }
        return majorVal + "." + minorVal + version.substring(version.lastIndexOf("."));
    }

    private boolean checkProject(final String selectedName) {
        return new File(PROJECTSLOCATION + File.separator + getDirectoryName(selectedName)).exists();
    }

    private String getDirectoryName(final String selectedName) {
        return getArtifactId(selectedName);
    }

    /**
     * Will Push policies to the PolicyRepo.
     *
     * @throws PolicyException PolicyException related to the operation
     */
    public void pushRules() throws PolicyException {
        // Check how many groups have been updated.
        // Invoke their Maven process.
        try {
            im.startTransaction();
        } catch (final Exception e) {
            LOGGER.error("Error while starting Transaction " + e);
        }
        if (!modifiedGroups.isEmpty()) {
            if (buildAndGenerateJarFile()) {
                sendNotification(controllers);
            }
        }
        if (im != null) {
            im.endTransaction();
        }
    }

    /**
     * Removes a Rule from Rule Projects.
     */
    public void removeRule(final String name) {
        final String controllerName = getGroupName(name);
        if (controllerName == null) {
            LOGGER.info("Error finding the controllerName for the given Policy: " + name);
            return;
        }
        syncProject(controllerName);
        getNameAndSetRemove(controllerName, name);
    }

    private Boolean buildAndGenerateJarFile() throws PolicyException {
        Boolean flag = false;
        for (final Map.Entry<String, String> entry : modifiedGroups.entrySet()) {
            InvocationResult result = null;
            final String group = entry.getKey();
            try {
                LOGGER.info("PushRules: ModifiedGroups, Key: " + group + ", Value: " + entry.getValue());
                final InvocationRequest request = new DefaultInvocationRequest();
                setVersion(group);
                createPom(group);
                request.setPomFile(new File(
                        PROJECTSLOCATION + File.separator + getArtifactId(group) + File.separator + POM_XML_FILE));
                request.setGoals(Arrays.asList(GOALS));
                final Invoker invoker = new DefaultInvoker();
                result = invoker.execute(request);
                if (result.getExecutionException() != null) {
                    LOGGER.error(result.getExecutionException());
                } else if (result.getExitCode() != 0) {
                    LOGGER.error("Maven Invocation failure..!");
                }
            } catch (final Exception e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Maven Invocation issue for "
                        + getArtifactId(group) + e.getMessage(), e);
            }
            if (result != null && result.getExitCode() == 0) {
                LOGGER.info("Build Completed..!");
                if (createFlag) {
                    addNotification(group, "create");
                } else {
                    addNotification(group, entry.getValue());
                }
                flag = true;
            } else {
                throw new PolicyException(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Maven Invocation failure!");
            }
        }
        return flag;
    }

    private String getGroupName(final String name) {
        if (policyMap.containsKey(name)) {
            return policyMap.get(name);
        } else {
            syncGroupInfo();
            return policyMap.containsKey(name) ? policyMap.get(name) : null;
        }
    }

    private void addModifiedGroup(final String controllerName, final String operation) {
        if (controllerName != null) {
            modifiedGroups.put(controllerName, operation);
        }
    }

    private void addNotification(final String controllerName, final String operation) {
        final ControllerPojo controllerPojo = new ControllerPojo();
        controllerPojo.setName(controllerName);
        controllerPojo.setOperation(operation);
        final HashMap<String, String> drools = new HashMap<>();
        drools.put("groupId", getGroupId(controllerName));
        drools.put("artifactId", getArtifactId(controllerName));
        drools.put("version", getVersion(controllerName));
        controllerPojo.setDrools(drools);
        controllers.add(controllerPojo);
        try {
            LOGGER.debug("Notification added: " + PolicyUtils.objectToJsonString(controllerPojo));
        } catch (final JsonProcessingException e) {
            LOGGER.error(MessageCodes.ERROR_SCHEMA_INVALID + "Json Processing Error " + e);
        }
    }

    private void removedRuleModifiedGroup(final String controllerName) {
        // This will be sending Notification to PDPD directly to Lock
        final ControllerPojo controllerPojo = new ControllerPojo();
        controllerPojo.setName(controllerName);
        controllerPojo.setOperation("lock");
        final List<ControllerPojo> controllerPojos = new ArrayList<>();
        controllerPojos.add(controllerPojo);
        sendNotification(controllerPojos);
    }

    private void sendNotification(final List<ControllerPojo> controllers) {
        final NotificationPojo notification = new NotificationPojo();
        final String requestId = UUID.randomUUID().toString();
        LOGGER.info("Generating notification RequestID : " + requestId);
        notification.setRequestId(requestId);
        notification.setEntity("controller");
        notification.setControllers(controllers);
        try {
            final String notificationJson = PolicyUtils.objectToJsonString(notification);
            LOGGER.info("Sending Notification :\n" + notificationJson);
            sendMessage(notificationJson);
        } catch (final Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while sending notification to PDP-D "
                    + e.getMessage(), e);
        }
    }

    private void sendMessage(final String message) throws IOException, GeneralSecurityException, InterruptedException {

        if ("dmaap".equalsIgnoreCase(notificationType)) {
            // Sending Message through DMaaP Message Router
            LOGGER.debug("DMAAP Publishing Message");

            publisher.send("MyPartitionKey", message);

            LOGGER.debug("Message Published on DMaaP :" + dmaapList.get(0) + "for Topic: " + pubTopic);

            Thread.sleep(dmaapDelay);
            publisher.close();
        } else {
            // Sending Message through UEB interface.
            LOGGER.debug("UEB Publishing Message");

            final CambriaBatchingPublisher pub = pubBuilder.build();
            pub.send("MyPartitionKey", message);

            final List<?> stuck = pub.close(uebDelay, TimeUnit.SECONDS);
            if (!stuck.isEmpty()) {
                LOGGER.error(stuck.size() + " messages unsent");
            } else {
                LOGGER.debug("Clean exit; Message Published on UEB : " + uebList + "for Topic: " + pubTopic);
            }
        }

    }

    private void createPom(final String name) {
        final Model model = new Model();
        model.setModelVersion("4.0.0");
        model.setGroupId(getGroupId(name));
        model.setArtifactId(getArtifactId(name));
        model.setVersion(getVersion(name));
        model.setName(name);
        final DistributionManagement distributionManagement = new DistributionManagement();
        final DeploymentRepository repository = new DeploymentRepository();
        repository.setId(repId);
        repository.setName(repName);
        repository.setUrl(repUrlList.get(0));
        distributionManagement.setRepository(repository);
        model.setDistributionManagement(distributionManagement);
        // Dependency Management goes here.
        List<Dependency> dependencyList = new ArrayList<>();
        if (groupMap.get(name).size() > 1) {
            @SuppressWarnings("unchecked")
            final ArrayList<PEDependency> dependencies = (ArrayList<PEDependency>) groupMap.get(name).get(1);
            for (final PEDependency dependency : dependencies) {
                dependencyList.add(dependency.getDependency());
            }
        } else {
            // Add Default dependencies.
            dependencyList = getDependencies(name);
        }
        model.setDependencies(dependencyList);
        Writer writer = null;
        try {
            writer = WriterFactory.newXmlWriter(
                    new File(PROJECTSLOCATION + File.separator + getArtifactId(name) + File.separator + POM_XML_FILE));
            final MavenXpp3Writer pomWriter = new MavenXpp3Writer();
            pomWriter.write(writer, model);
        } catch (final Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while creating POM for " + getArtifactId(name)
                    + e.getMessage(), e);
        } finally {
            IOUtil.close(writer);
        }
    }

    private List<Dependency> getDependencies(final String controllerName) {
        // Read the Dependency Information from property file.
        final Path file = Paths.get(DEPENDENCY_FILE);
        if (!Files.notExists(file)) {
            try {
                final String dependencyJson = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                final DependencyInfo dependencyInfo =
                        PolicyUtils.jsonStringToObject(dependencyJson, DependencyInfo.class);
                String controller = "default";
                if (dependencyInfo.getDependencies().containsKey(controllerName)) {
                    controller = controllerName;
                }
                final List<Dependency> dependencyList = new ArrayList<>();
                for (final PEDependency dependency : dependencyInfo.getDependencies().get(controller)) {
                    dependencyList.add(dependency.getDependency());
                }
                return dependencyList;
            } catch (IOException | NullPointerException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW
                        + "Error while getting dependecy Information for controller: " + controllerName
                        + e.getMessage(), e);
            }
        }
        return defaultDependencies(controllerName);
    }

    /**
     * Default Dependency Section. Can be changed as required.
     *
     * @param controllerName the controller name
     * @return changed dependency list
     */
    public List<Dependency> defaultDependencies(final String controllerName) {

        final List<Dependency> dependencyList = new ArrayList<>();
        final String version = StringEscapeUtils.escapeJava(brmsdependencyversion);

        final Dependency demoDependency = new Dependency();
        demoDependency.setGroupId(DROOLS_APPS_TEMPLATE_GROUP);
        demoDependency.setArtifactId("template.demo");
        demoDependency.setVersion(version);
        dependencyList.add(demoDependency);

        final Dependency controlloopDependency = new Dependency();
        controlloopDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        controlloopDependency.setArtifactId("events");
        controlloopDependency.setVersion(version);
        dependencyList.add(controlloopDependency);

        final Dependency restDependency = new Dependency();
        restDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        restDependency.setArtifactId("controlloop.common.model-impl.rest");
        restDependency.setVersion(version);
        dependencyList.add(restDependency);

        final Dependency appcDependency = new Dependency();
        appcDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        appcDependency.setArtifactId("controlloop.common.model-impl.appc");
        appcDependency.setVersion(version);
        dependencyList.add(appcDependency);

        final Dependency aaiDependency = new Dependency();
        aaiDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        aaiDependency.setArtifactId("controlloop.common.model-impl.aai");
        aaiDependency.setVersion(version);
        dependencyList.add(aaiDependency);

        final Dependency msoDependency = new Dependency();
        msoDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        msoDependency.setArtifactId("controlloop.common.model-impl.so");
        msoDependency.setVersion(version);
        dependencyList.add(msoDependency);
        return dependencyList;
    }

    private void createProject(final String path, final String ksessionName) {
        new File(path + File.separator + RULES).mkdirs();
        new File(path + File.separator + META_INF).mkdirs();
        if (!Files.exists(Paths.get(path + File.separator + META_INF + File.separator + KMODULE_XML_FILE))) {
            // Hard coding XML for PDP Drools to accept our Rules.
            final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
                    + "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">" + "\n"
                    + "<kbase name=\"rules\" packages=\"rules\">" + "\n" + "<ksession name=\"" + ksessionName + "\"/>"
                    + "\n" + "</kbase></kmodule>";
            copyDataToFile(path + File.separator + META_INF + File.separator + KMODULE_XML_FILE, xml);
        }
    }

    private void copyDataToFile(final String file, final String rule) {
        try {
            FileUtils.writeStringToFile(new File(file), rule);
        } catch (final Exception e) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while creating Rule for " + file + e.getMessage(),
                    e);
        }
    }

    private void readGroups(final Properties config) throws PolicyException {
        String[] groupNames;
        final String groupNamesError = "groupNames property is missing or empty from the property file ";
        if (!config.containsKey(GROUP_NAMES) || config.getProperty(GROUP_NAMES) == null) {
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + groupNamesError);
        }
        if (config.getProperty(GROUP_NAMES).contains(",")) {
            groupNames = config.getProperty(GROUP_NAMES).replaceAll(" ", "").split(",");
        } else {
            groupNames = new String[] {config.getProperty(GROUP_NAMES).replaceAll(" ", "")};
        }
        if (groupNames == null || groupNames.length == 0) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + groupNamesError);
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + groupNamesError);
        }
        groupMap = new HashMap<>();
        for (int counter = 0; counter < groupNames.length; counter++) {
            final String name = groupNames[counter];
            final String groupId = config.getProperty(name + ".groupID");
            if (groupId == null) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".groupID property is missing from the property file ");
                throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".groupID property is missing from the property file ");
            }
            final String artifactId = config.getProperty(name + ".artifactID");
            if (artifactId == null) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".artifactID property is missing from the property file ");
                throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".artifactID property is missing from the property file ");
            }
            final PEDependency dependency = new PEDependency();
            dependency.setArtifactId(artifactId);
            dependency.setGroupId(groupId);
            // Add to list if we got all
            addToGroup(name, dependency);
        }
    }

    private void addToGroup(final String name, final PEDependency dependency) {
        final EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            final TypedQuery<BrmsGroupInfo> query = em
                    .createQuery("select b from BrmsGroupInfo as b where b.controllerName = :cn", BrmsGroupInfo.class);
            query.setParameter("cn", name);
            final List<BrmsGroupInfo> groupList = query.getResultList();
            BrmsGroupInfo brmsGroupInfo = null;
            if (!groupList.isEmpty()) {
                LOGGER.info("Controller name already Existing in DB. Will be updating the DB Values" + name);
                brmsGroupInfo = groupList.get(0);
            }
            if (brmsGroupInfo == null) {
                brmsGroupInfo = new BrmsGroupInfo();
            }
            brmsGroupInfo.setControllerName(name);
            brmsGroupInfo.setGroupId(dependency.getGroupId());
            brmsGroupInfo.setArtifactId(dependency.getArtifactId());
            brmsGroupInfo.setVersion(dependency.getVersion());
            em.persist(brmsGroupInfo);
            em.flush();
            et.commit();

            final ArrayList<Object> values = new ArrayList<>();
            values.add(dependency);
            groupMap.put(name, values);
        } catch (final Exception exception) {
            LOGGER.error("Unable add/update policy group to database for controller name: " + name, exception);
            et.rollback();
        }
    }

    private String getArtifactId(final String name) {
        return ((PEDependency) groupMap.get(name).get(0)).getArtifactId();
    }

    private String getGroupId(final String name) {
        return ((PEDependency) groupMap.get(name).get(0)).getGroupId();
    }

    private String getVersion(final String name) {
        return ((PEDependency) groupMap.get(name).get(0)).getVersion();
    }

    private void getNameAndSetRemove(final String controllerName, final String policyName) {
        final String artifactName = getArtifactId(controllerName);
        final String ruleFolder = PROJECTSLOCATION + File.separator + artifactName + File.separator + "src"
                + File.separator + "main" + File.separator + RESOURCES + File.separator + RULES;
        final File file = new File(ruleFolder + File.separator + policyName + ".drl");
        if (file.delete()) {
            LOGGER.info("Deleted File.. " + file.getAbsolutePath());
            removePolicyFromGroup(policyName, controllerName);
        }
        if (new File(ruleFolder).listFiles().length == 0) {
            removedRuleModifiedGroup(controllerName);
        } else {
            // This is an update in terms of PDPD.
            addModifiedGroup(controllerName, "update");
        }
    }

    // Removes Policy from Memory and Database.
    private void removePolicyFromGroup(final String policyName, final String controllerName) {
        final EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            final TypedQuery<BrmsPolicyInfo> query =
                    em.createQuery("select b from BrmsPolicyInfo as b where b.policyName = :pn", BrmsPolicyInfo.class);
            query.setParameter("pn", policyName);
            final List<BrmsPolicyInfo> pList = query.getResultList();
            BrmsPolicyInfo brmsPolicyInfo;
            if (!pList.isEmpty()) {
                // Already exists.
                brmsPolicyInfo = pList.get(0);
                if (brmsPolicyInfo.getControllerName().getControllerName().equals(controllerName)) {
                    em.remove(brmsPolicyInfo);
                    em.flush();
                }
            }
            et.commit();
            policyMap.remove(policyName);
        } catch (final Exception exception) {
            LOGGER.error("Unable remove policy from group to database for policy name: " + policyName, exception);
            et.rollback();
        }
    }

    private void setVersion(final String selectedName) {
        String newVersion = VERSION_0_1_0;
        createFlag = false;
        final NexusArtifact artifact = getLatestArtifactFromNexus(selectedName);
        if (artifact != null) {
            newVersion = incrementVersion(artifact.getVersion());
        }
        if (VERSION_0_1_0.equals(newVersion)) {
            createFlag = true;
        }
        setVersion(newVersion, selectedName);
        LOGGER.info("Controller: " + selectedName + "is on version: " + newVersion);
    }

    private void setVersion(final String newVersion, final String controllerName) {
        final PEDependency userController = (PEDependency) groupMap.get(controllerName).get(0);
        userController.setVersion(newVersion);
        groupMap.get(controllerName).set(0, userController);
    }

    // Return BackUpMonitor
    public static BackUpMonitor getBackUpMonitor() {
        return bm;
    }

    /**
     * Rotate URLs list.
     */
    public void rotateUrls() {
        if (repUrlList != null) {
            Collections.rotate(repUrlList, -1);
        }
    }

    /**
     * Get URL List Size.
     *
     * @return URL list size
     */
    public int urlListSize() {
        return repUrlList != null ? repUrlList.size() : 0;
    }
}
