/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.brmsInterface;

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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

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
import org.onap.policy.brmsInterface.jpa.BRMSGroupInfo;
import org.onap.policy.brmsInterface.jpa.BRMSPolicyInfo;
import org.onap.policy.brmsInterface.jpa.DependencyInfo;
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.BackUpHandler;
import org.onap.policy.utils.BackUpMonitor;
import org.onap.policy.utils.BusPublisher;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.sonatype.nexus.client.NexusClient;
import org.sonatype.nexus.client.NexusClientException;
import org.sonatype.nexus.client.NexusConnectionException;
import org.sonatype.nexus.client.rest.NexusRestClient;
import org.sonatype.nexus.rest.model.NexusArtifact;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * BRMSPush: Application responsible to push policies to the BRMS PDP Policy Repository (PR). Mavenize and push policy
 * to PR
 * 
 * @version 1.0
 */

@SuppressWarnings("deprecation")
public class BRMSPush {
    private static final String GROUP_NAMES = "groupNames";
	private static final String DROOLS_APPS_TEMPLATE_GROUP = "org.onap.policy.drools-applications.controlloop.templates";
	private static final String DROOLS_APPS_MODEL_GROUP    = "org.onap.policy.drools-applications.controlloop.common.model-impl";
	private static final String META_INF = "META-INF";
	private static final String KMODULE_XML_FILE = "kmodule.xml";
	private static final String POM_XML_FILE = "pom.xml";
	private static final String VERSION_0_1_0 = "0.1.0";
	private static final String RULES = "rules";
	private static final String RESOURCES = "resources";
	private static final Logger LOGGER = FlexLogger.getLogger(BRMSPush.class.getName());
    private static final String PROJECTSLOCATION = "RuleProjects";
    private static final String[] GOALS = { "clean", "deploy" };
    private static final String DEFAULT_VERSION = "1.2.0-SNAPSHOT";
    private static final String DEPENDENCY_FILE = "dependency.json";
    private static final String BRMSPERSISTENCE = "brmsEclipselink.persistencexml";

    private static Map<String, String> modifiedGroups = new HashMap<>();
    private static IntegrityMonitor im;
    private static BackUpMonitor bm;
    private String defaultName = null;
    private String repID = null;
    private String repName = null;
    private ArrayList<String> repURLs = null;
    private String repUserName = null;
    private String repPassword = null;
    private String policyKeyID = null;
    private boolean createFlag = false;
    private String uebList = null;
    private List<String> dmaapList = null;
    private String pubTopic = null;
    private PublisherBuilder pubBuilder = null;
    protected BusPublisher publisher = null;
    private Long uebDelay = Long.parseLong("20");
    private Long dmaapDelay = Long.parseLong("5000");
    private String notificationType = null;
    private ArrayList<ControllerPOJO> controllers;
    private HashMap<String, ArrayList<Object>> groupMap = new HashMap<>();
    private Map<String, String> policyMap = new HashMap<>();
    private String brmsdependencyversion;
    private EntityManager em;
    private boolean syncFlag = false;

    public BRMSPush(String propertiesFile, BackUpHandler handler) throws PolicyException {
        if(propertiesFile==null || handler==null){
            throw new PolicyException("Error no propertiesFile or handler");
        }
        Properties config = new Properties();
        Path file = Paths.get(propertiesFile);
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

    private void setProperty(Path file, Properties config, BackUpHandler handler) throws PolicyException {
        InputStream in;
        try {
            in = new FileInputStream(file.toFile());
            config.load(in);
        } catch (IOException e) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Data/File Read Error while reading from the property file.",
                    e);
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "Data/File Read Error while reading from the property file.");
        }
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
        } catch (Exception e) {
            LOGGER.error("Error starting Integerity Monitor: " + e);
        }
        LOGGER.info("Trying to set up BackUpMonitor");
        try {
            setBackupMonitor(BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), resourceName, config,
                    handler));
        } catch (Exception e) {
            LOGGER.error("Error starting BackUpMonitor: " + e);
        }
        if(!config.containsKey(BRMSPERSISTENCE)){
            config.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistenceBRMS.xml");
        } else {
            config.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, config.getProperty(BRMSPERSISTENCE,"META-INF/persistenceBRMS.xml"));
        }
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRMSGW", config);
        em = emf.createEntityManager();
        defaultName = config.getProperty("defaultName");
        if (defaultName == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "defaultName property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "defaultName property is missing from the property file");
        }
        defaultName = defaultName.trim();
        repID = config.getProperty("repositoryID");
        if (repID == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryID property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryID property is missing from the property file ");
        }
        repID = repID.trim();
        repName = config.getProperty("repositoryName");
        if (repName == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repositoryName property is missing from the property file ");
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repositoryName property is missing from the property file ");
        }
        repName = repName.trim();
        String repURL = config.getProperty("repositoryURL");
        if (repURL == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryURL property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryURL property is missing from the property file ");
        }
        if (repURL.contains(",")) {
            repURLs = new ArrayList<>(Arrays.asList(repURL.trim().split(",")));
        } else {
            repURLs = new ArrayList<>();
            repURLs.add(repURL);
        }
        repUserName = config.getProperty("repositoryUsername");
        repPassword = config.getProperty("repositoryPassword");
        if (repUserName == null || repPassword == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repostoryUserName and respositoryPassword properties are required.");
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "repostoryUserName and respositoryPassword properties are required.");
        }
        repUserName = repUserName.trim();
        repPassword = repPassword.trim();
        policyKeyID = config.getProperty("policyKeyID");
        if (policyKeyID == null) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "policyKeyID property is missing from the property file ");
            throw new PolicyException(
                    XACMLErrorConstants.ERROR_DATA_ISSUE + "policyKeyID property is missing from the property file ");
        }
        policyKeyID = policyKeyID.trim();
        String syncF = config.getProperty("sync", "false").trim();
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
            String aafLogin = config.getProperty("CLIENT_ID").trim();
            String aafPassword = config.getProperty("CLIENT_KEY").trim();

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

            this.publisher = new BusPublisher.DmaapPublisherWrapper(this.dmaapList, this.pubTopic, aafLogin,
                    aafPassword);

            String dDelay = config.getProperty("NOTIFICATION_DELAY");
            if (dDelay != null && !dDelay.isEmpty()) {
                dDelay = dDelay.trim();
                try {
                    dmaapDelay = Long.parseLong(dDelay);
                } catch (NumberFormatException e) {
                    LOGGER.error("DMAAP_DELAY not a long format number" + e);
                }
            }
            LOGGER.info("DMAAP BusPublisher is created.");

        } else {
            LOGGER.info("Notification Type being used is UEB... creating instance of PublisherBuilder.");
            // Setting up the Publisher for UEB
            uebList = config.getProperty("NOTIFICATION_SERVERS");
            pubTopic = config.getProperty("NOTIFICATION_TOPIC");
            String apiKey = config.getProperty("UEB_API_KEY");
            String apiSecret = config.getProperty("UEB_API_SECRET");
            if (uebList == null || pubTopic == null) {
                LOGGER.error(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
                throw new PolicyException(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
            }
            uebList = uebList.trim();
            pubTopic = pubTopic.trim();
            pubBuilder = new CambriaClientBuilders.PublisherBuilder();
            pubBuilder.usingHosts(uebList).onTopic(pubTopic);
            if (apiKey != null && !apiKey.isEmpty() && apiSecret != null && !apiSecret.isEmpty()) {
                apiKey = apiKey.trim();
                apiSecret = apiSecret.trim();
                pubBuilder.authenticatedBy(apiKey, apiSecret);
            }
            String uDelay = config.getProperty("NOTIFICATION_DELAY");
            if (uDelay != null && !uDelay.isEmpty()) {
                uDelay = uDelay.trim();
                try {
                    uebDelay = Long.parseLong(uDelay);
                } catch (NumberFormatException e) {
                    LOGGER.error("UEB_DELAY not a long format number" + e);
                }
            }
            LOGGER.info("UEB PublisherBuilder is created.");

        }

    }

    private static void setBackupMonitor(BackUpMonitor instance) {
        bm = instance;
    }

    private static void setIntegrityMonitor(IntegrityMonitor instance) {
        im = instance;
    }

    /**
     * Will Initialize the variables required for BRMSPush.
     */
    public void initiate(boolean flag) {
        resetModifiedGroups();
        controllers = new ArrayList<>();
        try {
            bm.updateNotification();
        } catch (Exception e) {
            LOGGER.error("Error while updating Notification: " + e.getMessage(), e);
        }
        if (flag)
            syncGroupInfo();
    }
    
    public void resetDS(){
    	resetModifiedGroups();
        controllers = new ArrayList<>();
    }

    private static void resetModifiedGroups() {
        modifiedGroups = new HashMap<>();
    }

    /**
     * Will Add rules to projects. Creates necessary folders if required.
     */
    public void addRule(String name, String rule, Map<String, String> responseAttributes) {
        // 1 check the response Attributes and determine if this belongs to any projects.
        // 2 if not create folder
        // 3 create pom.
        // 4 copy the rule.
        // 5 store the groups that have been updated.
        String kSessionName = null;
        String selectedName = null;
        if (!responseAttributes.isEmpty()) {
            // Pick selected Value
            String userControllerName = null;
            ArrayList<PEDependency> userDependencies = new ArrayList<>();
            for (Map.Entry<String, String> entry: responseAttributes.entrySet()) {
            	String key = entry.getKey();
            	String value = entry.getValue();
                if (key.equals(policyKeyID)) {
                    selectedName = value;
                }
                // kmodule configurations
                else if ("kSessionName".equals(key)) {
                    kSessionName = value;
                }
                // Check User Specific values.
                if ("$controller:".equals(key)) {
                    try {
                        PEDependency dependency = PolicyUtils.jsonStringToObject(value,
                                PEDependency.class);
                        userControllerName = key.replaceFirst("$controller:", "");
                        LOGGER.info("addRule: userControllerName - " + userControllerName + ", dependency: - " + dependency);
                        addToGroup(userControllerName, dependency);
                    } catch (Exception e) {
                        LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while resolving Controller: " + e);
                    }

                } else if ("$dependency$".equals(key) && value.startsWith("[") && value.endsWith("]")) {
                    value = value.substring(1, value.length() - 1).trim();
                    List<String> dependencyStrings = Arrays.asList(value.split("},{"));
                    for (String dependencyString : dependencyStrings) {
                        try {
                            userDependencies
                                    .add(PolicyUtils.jsonStringToObject(dependencyString, PEDependency.class));
                        } catch (Exception e) {
                            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW
                                    + "Error while resolving Dependencies: " + e);
                        }
                    }
                }
            }
            if (userControllerName != null) {
                // Adding custom dependencies here.
                ArrayList<Object> values = groupMap.get(userControllerName);
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
            // If the key is not got as parameters set by the user, setting the default value for kSessionName as
            // closedLoop
            if (kSessionName == null) {
            	LOGGER.info("kSessionName is null, selectedName is  : " + selectedName );
                if (selectedName == defaultName) {
                    kSessionName = "closedloop";
                } else {
                    kSessionName = "closedloop-" + selectedName;
                }
            }
            // create directories if missing.
            manageProject(selectedName, kSessionName, name, rule);
            addModifiedGroup(selectedName, "update"); // Will check for Create Later after generating the Pom.
        }
    }

    private void syncGroupInfo() {
        // Sync DB to JMemory.
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BRMSGroupInfo AS b");
        List<?> bList = query.getResultList();
        if (bList.size() != groupMap.size()) {
            for (Object value : bList) {
                BRMSGroupInfo brmsGroupInfo = (BRMSGroupInfo) value;
                PEDependency dependency = new PEDependency();
                dependency.setArtifactId(brmsGroupInfo.getArtifactId());
                dependency.setGroupId(brmsGroupInfo.getGroupId());
                dependency.setVersion(brmsGroupInfo.getVersion());
                ArrayList<Object> values = new ArrayList<>();
                values.add(dependency);
                groupMap.put(brmsGroupInfo.getControllerName(), values);
            }
        }
        query = em.createQuery("select g from BRMSPolicyInfo AS g");
        bList = query.getResultList();
        if (bList.size() != policyMap.size()) {
            for (Object value : bList) {
                BRMSPolicyInfo brmsPolicyInfo = (BRMSPolicyInfo) value;
                policyMap.put(brmsPolicyInfo.getPolicyName(), brmsPolicyInfo.getControllerName().getControllerName());
            }
        }
        et.commit();
        LOGGER.info("Updated Local Memory values with values from database.");
    }

    private void manageProject(String selectedName, String kSessionName, String name, String rule) {
        // Check if the Project is in Sync. If not get the latest Version.
        syncProject(selectedName);
        createProject(PROJECTSLOCATION + File.separator + getArtifactID(selectedName) + File.separator + "src"
                + File.separator + "main" + File.separator + RESOURCES, kSessionName);
        copyDataToFile(PROJECTSLOCATION + File.separator + getArtifactID(selectedName) + File.separator + "src"
                + File.separator + "main" + File.separator + RESOURCES + File.separator + RULES + File.separator
                + name + ".drl", rule);
        addToPolicy(name, selectedName);
    }

    /*
     * Add Policy to JMemory and DataBase.
     */
    private void addToPolicy(String policyName, String controllerName) {
        policyMap.put(policyName, controllerName);
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BRMSPolicyInfo as b where b.policyName = :pn");
        query.setParameter("pn", policyName);
        List<?> pList = query.getResultList();
        boolean create = false;
        BRMSPolicyInfo brmsPolicyInfo = new BRMSPolicyInfo();
        if (!pList.isEmpty()) {
            // Already exists.
            brmsPolicyInfo = (BRMSPolicyInfo) pList.get(0);
            if (!brmsPolicyInfo.getControllerName().getControllerName().equals(controllerName)) {
                create = true;
            }
        } else {
            create = true;
        }
        if (create) {
            query = em.createQuery("select b from BRMSGroupInfo as b where b.controllerName = :cn");
            query.setParameter("cn", controllerName);
            List<?> bList = query.getResultList();
            BRMSGroupInfo brmsGroupInfo = new BRMSGroupInfo();
            if (!bList.isEmpty()) {
                brmsGroupInfo = (BRMSGroupInfo) bList.get(0);
            }
            brmsPolicyInfo.setPolicyName(policyName);
            brmsPolicyInfo.setControllerName(brmsGroupInfo);
            em.persist(brmsPolicyInfo);
            em.flush();
        }
        et.commit();
    }

    private void syncProject(String selectedName) {
        boolean projectExists = checkProject(selectedName);
        if (projectExists) {
            String version;
            version = getVersion(selectedName);
            if (version == null) {
                LOGGER.error("Error getting local version for the given Controller Name:" + selectedName
                        + " going with Default value");
                version = VERSION_0_1_0;
            }
            String nextVersion = incrementVersion(version);
            boolean outOfSync = checkRemoteSync(selectedName, nextVersion);
            if (!outOfSync) {
                return;
            }
        }
        // We are out of Sync or Project is not Present.
        downloadProject(selectedName);
    }

    private void downloadProject(String selectedName) {
        NexusArtifact artifact = getLatestArtifactFromNexus(selectedName);
        if (artifact == null)
            return;
        String dirName = getDirectoryName(selectedName);
        URL website;
        String fileName = "rule.jar";
        try {
            website = new URL(artifact.getResourceURI());
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            extractJar(fileName, dirName);
            new File(fileName).delete();
        } catch (IOException e) {
            LOGGER.error("Error while downloading the project to File System. " + e.getMessage(), e);
        }
    }

    private void extractJar(String jarFileName, String artifactId) throws IOException {
        JarFile jar = new JarFile(jarFileName);
        Enumeration<?> enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            File f = null;
            String fileName = file.getName().substring(file.getName().lastIndexOf("/") + 1);
            if (file.getName().endsWith(".drl")) {
                String path = PROJECTSLOCATION + File.separator + artifactId + File.separator + "src" + File.separator
                        + "main" + File.separator + RESOURCES + File.separator + RULES;
                new File(path).mkdirs();
                if (syncFlag && policyMap.containsKey(fileName.replace(".drl", ""))) {
                    f = new File(path + File.separator + fileName);
                } else {
                    f = new File(path + File.separator + fileName);
                }
            } else if (file.getName().endsWith(POM_XML_FILE)) {
                String path = PROJECTSLOCATION + File.separator + artifactId;
                new File(path).mkdirs();
                f = new File(path + File.separator + fileName);
            } else if (file.getName().endsWith(KMODULE_XML_FILE)) {
                String path = PROJECTSLOCATION + File.separator + artifactId + File.separator + "src" + File.separator
                        + "main" + File.separator + RESOURCES + File.separator + META_INF;
                new File(path).mkdirs();
                f = new File(path + File.separator + fileName);
            }
            if (f != null) {
                InputStream is = jar.getInputStream(file);
                FileOutputStream fos = new FileOutputStream(f);
                while (is.available() > 0) {
                    fos.write(is.read());
                }
                fos.close();
                is.close();
                LOGGER.info(fileName + " Created..");
            }
        }
        jar.close();
    }

    private NexusArtifact getLatestArtifactFromNexus(String selectedName) {
        List<NexusArtifact> artifacts = getArtifactFromNexus(selectedName, null);
        int bigNum = 0;
        int smallNum = 0;
        NexusArtifact result = null;
        for (NexusArtifact artifact : artifacts) {
            int majorVal = Integer.parseInt(artifact.getVersion().substring(0, artifact.getVersion().indexOf(".")));
            int minorVal = Integer.parseInt(artifact.getVersion().substring(artifact.getVersion().indexOf(".") + 1,
                    artifact.getVersion().lastIndexOf(".")));
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
    private NexusArtifact additionalNexusLatestCheck(String selectedName, NexusArtifact result) {
        if(result==null){
            return result;
        }
        String nextVersion = incrementVersion(result.getVersion());
        List<NexusArtifact> artifact = getArtifactFromNexus(selectedName, nextVersion);
        return artifact.isEmpty() ? result : additionalNexusLatestCheck(selectedName, artifact.get(0));
    }

    private boolean checkRemoteSync(String selectedName, String version) {
        List<NexusArtifact> artifacts = getArtifactFromNexus(selectedName, version);
        return artifacts.isEmpty() ? false : true;
    }

    private List<NexusArtifact> getArtifactFromNexus(String selectedName, String version) {
        final NexusClient client = new NexusRestClient();
        int i = 0;
        boolean flag = false;
        while (i < repURLs.size()) {
            try {
                String repURL = repURLs.get(0);
                client.connect(repURL.substring(0, repURL.indexOf(repURL.split(":[0-9]+\\/nexus")[1])), repUserName,
                        repPassword);
                final NexusArtifact template = new NexusArtifact();
                template.setGroupId(getGroupID(selectedName));
                template.setArtifactId(getArtifactID(selectedName));
                if (version != null) {
                    template.setVersion(version);
                }
                List<NexusArtifact> resultList = client.searchByGAV(template);
                if (resultList != null) {
                    flag = true;
                    return resultList;
                }
            } catch (NexusClientException | NexusConnectionException | NullPointerException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Connection to remote Nexus has failed. "
                        + e.getMessage(), e);
            } finally {
                try {
                    client.disconnect();
                } catch (NexusClientException | NexusConnectionException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "failed to disconnect Connection from Nexus."
                            + e.getMessage(), e);
                }
                if (!flag) {
                    Collections.rotate(repURLs, -1);
                    i++;
                }
            }
        }
        return new ArrayList<>();
    }

    private void setVersion(String selectedName) {
        String newVersion = VERSION_0_1_0;
        createFlag = false;
        NexusArtifact artifact = getLatestArtifactFromNexus(selectedName);
        if (artifact != null) {
            newVersion = incrementVersion(artifact.getVersion());
        }
        if (VERSION_0_1_0.equals(newVersion)) {
            createFlag = true;
        }
        setVersion(newVersion, selectedName);
        LOGGER.info("Controller: " + selectedName + "is on version: " + newVersion);
    }

    private String incrementVersion(String version) {
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

    private boolean checkProject(String selectedName) {
        return new File(PROJECTSLOCATION + File.separator + getDirectoryName(selectedName)).exists();
    }

    private String getDirectoryName(String selectedName) {
        return getArtifactID(selectedName);
    }

    /**
     * Will Push policies to the PolicyRepo.
     * 
     * @param notificationType
     *            <String> type of notification Type.
     * @throws PolicyException
     */
    public void pushRules() throws PolicyException {
        // Check how many groups have been updated.
        // Invoke their Maven process.
        try {
            im.startTransaction();
        } catch (Exception e) {
            LOGGER.error("Error while starting Transaction " + e);
        }
        if (!modifiedGroups.isEmpty()) {
            Boolean flag = false;
            for (Map.Entry<String, String> entry : modifiedGroups.entrySet()) {
                InvocationResult result = null;
		String group = entry.getKey();
                try {
                	LOGGER.info("PushRules: ModifiedGroups, Key: " + group + ", Value: " + entry.getValue());
                    InvocationRequest request = new DefaultInvocationRequest();
                    setVersion(group);
                    createPom(group);
                    request.setPomFile(new File(
                            PROJECTSLOCATION + File.separator + getArtifactID(group) + File.separator + POM_XML_FILE));
                    request.setGoals(Arrays.asList(GOALS));
                    Invoker invoker = new DefaultInvoker();
                    result = invoker.execute(request);
                    if (result.getExecutionException() != null) {
                        LOGGER.error(result.getExecutionException());
                    } else if (result.getExitCode() != 0) {
                        LOGGER.error("Maven Invocation failure..!");
                    }
                } catch (Exception e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Maven Invocation issue for "
                            + getArtifactID(group) + e.getMessage(), e);
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
            if (flag) {
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
    public void removeRule(String name) {
        String controllerName = getGroupName(name);
        if (controllerName == null) {
            LOGGER.info("Error finding the controllerName for the given Policy: " + name);
            return;
        }
        syncProject(controllerName);
        getNameAndSetRemove(controllerName, name);
    }

    private String getGroupName(String name) {
        if (policyMap.containsKey(name)) {
            return policyMap.get(name);
        } else {
            syncGroupInfo();
            return policyMap.containsKey(name) ? policyMap.get(name) : null;
        }
    }

    private void addModifiedGroup(String controllerName, String operation) {
        if (controllerName != null) {
            modifiedGroups.put(controllerName, operation);
        }
    }

    private void addNotification(String controllerName, String operation) {
        ControllerPOJO controllerPOJO = new ControllerPOJO();
        controllerPOJO.setName(controllerName);
        controllerPOJO.setOperation(operation);
        HashMap<String, String> drools = new HashMap<>();
        drools.put("groupId", getGroupID(controllerName));
        drools.put("artifactId", getArtifactID(controllerName));
        drools.put("version", getVersion(controllerName));
        controllerPOJO.setDrools(drools);
        controllers.add(controllerPOJO);
        try {
            LOGGER.debug("Notification added: " + PolicyUtils.objectToJsonString(controllerPOJO));
        } catch (JsonProcessingException e) {
            LOGGER.error(MessageCodes.ERROR_SCHEMA_INVALID + "Json Processing Error " + e);
        }
    }

    private void removedRuleModifiedGroup(String controllerName) {
        // This will be sending Notification to PDPD directly to Lock
        ControllerPOJO controllerPOJO = new ControllerPOJO();
        controllerPOJO.setName(controllerName);
        controllerPOJO.setOperation("lock");
        List<ControllerPOJO> controllerPojos = new ArrayList<>();
        controllerPojos.add(controllerPOJO);
        sendNotification(controllerPojos);
    }

    private void sendNotification(List<ControllerPOJO> controllers) {
        NotificationPOJO notification = new NotificationPOJO();
        String requestId = UUID.randomUUID().toString();
        LOGGER.info("Generating notification RequestID : " + requestId);
        notification.setRequestID(requestId);
        notification.setEntity("controller");
        notification.setControllers(controllers);
        try {
            String notificationJson = PolicyUtils.objectToJsonString(notification);
            LOGGER.info("Sending Notification :\n" + notificationJson);
            sendMessage(notificationJson);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while sending notification to PDP-D "
                    + e.getMessage(), e);
        }
    }

    private void sendMessage(String message) throws IOException, GeneralSecurityException, InterruptedException {

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

            CambriaBatchingPublisher pub = pubBuilder.build();
            pub.send("MyPartitionKey", message);

            final List<?> stuck = pub.close(uebDelay, TimeUnit.SECONDS);
            if (!stuck.isEmpty()) {
                LOGGER.error(stuck.size() + " messages unsent");
            } else {
                LOGGER.debug("Clean exit; Message Published on UEB : " + uebList + "for Topic: " + pubTopic);
            }
        }

    }

    private void createPom(String name) {
        Model model = new Model();
        model.setModelVersion("4.0.0");
        model.setGroupId(getGroupID(name));
        model.setArtifactId(getArtifactID(name));
        model.setVersion(getVersion(name));
        model.setName(name);
        DistributionManagement distributionManagement = new DistributionManagement();
        DeploymentRepository repository = new DeploymentRepository();
        repository.setId(repID);
        repository.setName(repName);
        repository.setUrl(repURLs.get(0));
        distributionManagement.setRepository(repository);
        model.setDistributionManagement(distributionManagement);
        // Dependency Management goes here.
        List<Dependency> dependencyList = new ArrayList<>();
        if (groupMap.get(name).size() > 1) {
            @SuppressWarnings("unchecked")
            ArrayList<PEDependency> dependencies = (ArrayList<PEDependency>) groupMap.get(name).get(1);
            for (PEDependency dependency : dependencies) {
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
                    new File(PROJECTSLOCATION + File.separator + getArtifactID(name) + File.separator + POM_XML_FILE));
            MavenXpp3Writer pomWriter = new MavenXpp3Writer();
            pomWriter.write(writer, model);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while creating POM for " + getArtifactID(name)
                    + e.getMessage(), e);
        } finally {
            IOUtil.close(writer);
        }
    }

    private List<Dependency> getDependencies(String controllerName) {
        // Read the Dependency Information from property file.
        Path file = Paths.get(DEPENDENCY_FILE);
        if (!Files.notExists(file)) {
            try {
                String dependencyJSON = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                DependencyInfo dependencyInfo = PolicyUtils.jsonStringToObject(dependencyJSON, DependencyInfo.class);
                String controller = "default";
                if (dependencyInfo.getDependencies().containsKey(controllerName)) {
                    controller = controllerName;
                }
                List<Dependency> dependencyList = new ArrayList<>();
                for (PEDependency dependency : dependencyInfo.getDependencies().get(controller)) {
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

    // Default Dependency Section. Can be changed as required.
    public List<Dependency> defaultDependencies(String controllerName) {

        List<Dependency> dependencyList = new ArrayList<>();
        String version = StringEscapeUtils.escapeJava(brmsdependencyversion);

        Dependency demoDependency = new Dependency();
        demoDependency.setGroupId(DROOLS_APPS_TEMPLATE_GROUP);
        demoDependency.setArtifactId("template.demo");
        demoDependency.setVersion(version);
        dependencyList.add(demoDependency);

        Dependency controlloopDependency = new Dependency();
        controlloopDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        controlloopDependency.setArtifactId("events");
        controlloopDependency.setVersion(version);
        dependencyList.add(controlloopDependency);

        Dependency restDependency = new Dependency();
        restDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        restDependency.setArtifactId("controlloop.common.model-impl.rest");
        restDependency.setVersion(version);
        dependencyList.add(restDependency);

        Dependency appcDependency = new Dependency();
        appcDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        appcDependency.setArtifactId("controlloop.common.model-impl.appc");
        appcDependency.setVersion(version);
        dependencyList.add(appcDependency);

        Dependency aaiDependency = new Dependency();
        aaiDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        aaiDependency.setArtifactId("controlloop.common.model-impl.aai");
        aaiDependency.setVersion(version);
        dependencyList.add(aaiDependency);

        Dependency msoDependency = new Dependency();
        msoDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        msoDependency.setArtifactId("controlloop.common.model-impl.so");
        msoDependency.setVersion(version);
        dependencyList.add(msoDependency);

        Dependency trafficgeneratorDependency = new Dependency();
        trafficgeneratorDependency.setGroupId(DROOLS_APPS_MODEL_GROUP);
        trafficgeneratorDependency.setArtifactId("controlloop.common.model-impl.trafficgenerator");
        trafficgeneratorDependency.setVersion(version);
        dependencyList.add(trafficgeneratorDependency);
        return dependencyList;
    }

    private void createProject(String path, String ksessionName) {
        new File(path + File.separator + RULES).mkdirs();
        new File(path + File.separator + META_INF).mkdirs();
        if (!Files.exists(Paths.get(path + File.separator + META_INF + File.separator + KMODULE_XML_FILE))) {
            // Hard coding XML for PDP Drools to accept our Rules.
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
                    + "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">" + "\n"
                    + "<kbase name=\"rules\" packages=\"rules\">" + "\n" + "<ksession name=\"" + ksessionName + "\"/>"
                    + "\n" + "</kbase></kmodule>";
            copyDataToFile(path + File.separator + META_INF + File.separator + KMODULE_XML_FILE, xml);
        }
    }

    private void copyDataToFile(String file, String rule) {
        try {
            FileUtils.writeStringToFile(new File(file), rule);
        } catch (Exception e) {
            LOGGER.error(
                    XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while creating Rule for " + file + e.getMessage(),
                    e);
        }
    }

    private void readGroups(Properties config) throws PolicyException {
        String[] groupNames;
        String groupNamesError = "groupNames property is missing or empty from the property file ";
		if (!config.containsKey(GROUP_NAMES) || config.getProperty(GROUP_NAMES)==null){
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + groupNamesError);
        }
        if (config.getProperty(GROUP_NAMES).contains(",")) {
            groupNames = config.getProperty(GROUP_NAMES).replaceAll(" ", "").split(",");
        } else {
            groupNames = new String[] { config.getProperty(GROUP_NAMES).replaceAll(" ", "") };
        }
        if (groupNames == null || groupNames.length == 0) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + groupNamesError);
            throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE
                    + groupNamesError);
        }
        groupMap = new HashMap<>();
        for (int counter = 0; counter < groupNames.length; counter++) {
            String name = groupNames[counter];
            String groupID = config.getProperty(name + ".groupID");
            if (groupID == null) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".groupID property is missing from the property file ");
                throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".groupID property is missing from the property file ");
            }
            String artifactID = config.getProperty(name + ".artifactID");
            if (artifactID == null) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".artifactID property is missing from the property file ");
                throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + name
                        + ".artifactID property is missing from the property file ");
            }
            PEDependency dependency = new PEDependency();
            dependency.setArtifactId(artifactID);
            dependency.setGroupId(groupID);
            // Add to list if we got all
            addToGroup(name, dependency);
        }
    }

    private void addToGroup(String name, PEDependency dependency) {
        ArrayList<Object> values = new ArrayList<>();
        values.add(dependency);
        groupMap.put(name, values);
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BRMSGroupInfo as b where b.controllerName = :cn");
        query.setParameter("cn", name);
        List<?> groupList = query.getResultList();
        BRMSGroupInfo brmsGroupInfo = null;
        if (!groupList.isEmpty()) {
            LOGGER.info("Controller name already Existing in DB. Will be updating the DB Values" + name);
            brmsGroupInfo = (BRMSGroupInfo) groupList.get(0);
        }
        if (brmsGroupInfo == null) {
            brmsGroupInfo = new BRMSGroupInfo();
        }
        brmsGroupInfo.setControllerName(name);
        brmsGroupInfo.setGroupId(dependency.getGroupId());
        brmsGroupInfo.setArtifactId(dependency.getArtifactId());
        brmsGroupInfo.setVersion(dependency.getVersion());
        em.persist(brmsGroupInfo);
        em.flush();
        et.commit();
    }

    private String getArtifactID(String name) {
        return ((PEDependency) groupMap.get(name).get(0)).getArtifactId();
    }

    private String getGroupID(String name) {
        return ((PEDependency) groupMap.get(name).get(0)).getGroupId();
    }

    private String getVersion(String name) {
        return ((PEDependency) groupMap.get(name).get(0)).getVersion();
    }

    private void getNameAndSetRemove(String controllerName, String policyName) {
        String artifactName = getArtifactID(controllerName);
        String ruleFolder = PROJECTSLOCATION + File.separator + artifactName + File.separator + "src" + File.separator
                + "main" + File.separator + RESOURCES + File.separator + RULES;
        File file = new File(ruleFolder + File.separator + policyName + ".drl");
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
    private void removePolicyFromGroup(String policyName, String controllerName) {
        policyMap.remove(policyName);
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BRMSPolicyInfo as b where b.policyName = :pn");
        query.setParameter("pn", policyName);
        List<?> pList = query.getResultList();
        BRMSPolicyInfo brmsPolicyInfo;
        if (!pList.isEmpty()) {
            // Already exists.
            brmsPolicyInfo = (BRMSPolicyInfo) pList.get(0);
            if (brmsPolicyInfo.getControllerName().getControllerName().equals(controllerName)) {
                em.remove(brmsPolicyInfo);
                em.flush();
            }
        }
        et.commit();
    }

    private void setVersion(String newVersion, String controllerName) {
        PEDependency userController = (PEDependency) groupMap.get(controllerName).get(0);
        userController.setVersion(newVersion);
        groupMap.get(controllerName).set(0, userController);
    }

    // Return BackUpMonitor
    public static BackUpMonitor getBackUpMonitor() {
        return bm;
    }

    public void rotateURLs() {
        if (repURLs != null) {
            Collections.rotate(repURLs, -1);
        }
    }

    public int urlListSize() {
        if (repURLs != null) {
            return repURLs.size();
        } else
            return 0;
    }
}
