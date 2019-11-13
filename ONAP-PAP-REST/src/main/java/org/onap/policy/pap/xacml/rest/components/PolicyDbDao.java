/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.pap.xacml.rest.components;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.PolicyDbException;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.DatabaseLockEntity;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.rest.jpa.PolicyDbDaoEntity;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.utils.PeCryptoUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class PolicyDbDao.
 */
@Component
public class PolicyDbDao {
    private static final Logger logger = FlexLogger.getLogger(PolicyDbDao.class);
    public static final String JSON_CONFIG = "JSON";
    public static final String XML_CONFIG = "XML";
    public static final String PROPERTIES_CONFIG = "PROPERTIES";
    public static final String OTHER_CONFIG = "OTHER";
    public static final String AUDIT_USER = "audit";

    public static final String CONFIG = "Config";
    public static final String ACTION = "Action";
    public static final String GROUP_ID = "groupId";
    public static final String DELETED = "deleted";
    public static final String GROUPENTITY_SELECT =
            "SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted";
    public static final String PDPENTITY_SELECT =
            "SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted";
    public static final String GROUP_NOT_FOUND = "The group could not be found with id ";
    public static final String FOUND_IN_DB_NOT_DEL = " were found in the database that are not deleted";
    public static final String MORE_THAN_ONE_PDP = "Somehow, more than one pdp with the same id ";
    public static final String DELETED_STATUS_FOUND = " and deleted status were found in the database";
    public static final String DUPLICATE_GROUPID = "Somehow, more than one group with the same id ";
    public static final String PDP_ID = "pdpId";
    public static final String QUERY_FAILED_FOR_GROUP = "Query failed trying to check for existing group";
    public static final String QUERY_FAILED_GET_GROUP = "Query failed trying to get group ";
    public static final String SCOPE = "scope";
    public static final String POLICYDBDAO_VAR = "PolicyDBDao";
    public static final String DUP_POLICYID = "Somehow, more than one policy with the id ";
    public static final String FOUND_IN_DB = " were found in the database";

    private static final String AUDIT_STR = "Audit";

    private static PolicyDbDao currentInstance = null;
    private static boolean isJunit = false;
    private static SessionFactory sessionfactory;
    private List<?> otherServers;
    private PAPPolicyEngine papEngine;

    /**
     * Gets the current instance of PolicyDBDao.
     *
     * @return The instance of PolicyDBDao or throws exception if the given instance is null.
     * @throws IllegalStateException if a PolicyDBDao instance is null. Call createPolicyDBDaoInstance
     *         (EntityManagerFactory emf) to get this.
     */
    public static PolicyDbDao getPolicyDbDaoInstance() {
        logger.debug("getPolicyDBDaoInstance() as getPolicyDBDaoInstance() called");
        if (currentInstance != null) {
            return currentInstance;
        } else {
            currentInstance = new PolicyDbDao("init");
        }
        return currentInstance;
    }

    /**
     * Sets the pap engine.
     *
     * @param papEngine2 the new pap engine
     */
    public void setPapEngine(PAPPolicyEngine papEngine2) {
        this.papEngine = papEngine2;
    }

    /**
     * Instantiates a new policy db dao.
     *
     * @param sessionFactory the session factory
     */
    @Autowired
    public PolicyDbDao(SessionFactory sessionFactory) {
        PolicyDbDao.sessionfactory = sessionFactory;
    }

    /**
     * Instantiates a new policy db dao.
     */
    public PolicyDbDao() {
        // Default Constructor
    }

    /**
     * Initialize the DAO.
     *
     * @param init initiation parameters
     */
    public PolicyDbDao(String init) {
        // not needed in this release
        if (!register()) {
            PolicyLogger
                    .error("This server's PolicyDBDao instance could not be registered and may not reveive updates");
        }

        otherServers = getRemotePolicyDbDaoList();
        if (logger.isDebugEnabled()) {
            logger.debug("Number of remote PolicyDBDao instances: " + otherServers.size());
        }
        if (otherServers.isEmpty()) {
            logger.warn("List of PolicyDBDao servers is empty or could not be retrieved");
        }
    }

    /**
     * Start a synchronized transaction.
     *
     * <p>Not static because we are going to be using the instance's emf waitTime in ms to wait for lock, or -1 to wait
     * forever (no)
     *
     * @param session the session
     * @param waitTime the wait time
     */
    @SuppressWarnings("deprecation")
    public void startTransactionSynced(Session session, int waitTime) {
        logger.debug("\n\nstartTransactionSynced(Hibernate Session,int waitTime) as " + "\n   startTransactionSynced("
                + session + "," + waitTime + ") called\n\n");
        DatabaseLockEntity lock = null;
        session.beginTransaction();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("\n\nstartTransactionSynced():" + "\n   ATTEMPT to get the DB lock" + "\n\n");
            }
            lock = (DatabaseLockEntity) session.get(DatabaseLockEntity.class, 1, LockMode.PESSIMISTIC_WRITE);
            if (logger.isDebugEnabled()) {
                logger.debug("\n\nstartTransactionSynced():" + "\n   GOT the DB lock" + "\n\n");
            }
        } catch (Exception e) {
            logger.error("Exception Occured" + e);
        }
        if (lock == null) {
            throw new IllegalStateException(
                    "The lock row does not exist in the table. Please create a primary key with value = 1.");
        }

    }

    /**
     * Gets the list of other registered PolicyDBDaos from the database.
     *
     * @return List (type PolicyDbDaoEntity) of other PolicyDBDaos
     */
    private List<?> getRemotePolicyDbDaoList() {
        logger.debug("getRemotePolicyDBDaoList() as getRemotePolicyDBDaoList() called");
        List<?> policyDbDaoEntityList = new LinkedList<>();
        Session session = sessionfactory.openSession();
        try {
            Criteria cr = session.createCriteria(PolicyDbDaoEntity.class);
            policyDbDaoEntityList = cr.list();
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, POLICYDBDAO_VAR,
                    "Exception querying for other registered PolicyDBDaos");
            logger.warn("List of remote PolicyDBDaos will be empty", e);
        } finally {
            try {
                session.close();
            } catch (Exception e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement" + e);
            }
        }
        return policyDbDaoEntityList;
    }

    /**
     * Gets the new transaction.
     *
     * @return the new transaction
     */
    public PolicyDbDaoTransaction getNewTransaction() {
        logger.debug("getNewTransaction() as getNewTransaction() called");
        return new PolicyDbDaoTransactionInstance("init");
    }

    /**
     * Get a new audit transaction.
     *
     * <p>Because the normal transactions are not used in audits, we can use the same transaction mechanism to get a
     * transaction and obtain the emlock and the DB lock. We just need to provide different transaction timeout values
     * in ms because the audit will run longer than normal transactions.
     *
     * @return the new audit transaction
     */
    public PolicyDbDaoTransaction getNewAuditTransaction() {
        logger.debug("getNewAuditTransaction() as getNewAuditTransaction() called");
        // Use the standard transaction wait time in ms
        int auditWaitMs = Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_TRANS_WAIT));
        // Use the (extended) audit timeout time in ms
        int auditTimeoutMs = Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_AUDIT_TIMEOUT));
        return new PolicyDbDaoTransactionInstance(auditTimeoutMs, auditWaitMs);
    }

    /**
     * Checks if two strings are equal. Null strings ARE allowed.
     *
     * @param one A String or null to compare
     * @param two A String or null to compare
     * @return true, if successful
     */
    public static boolean stringEquals(String one, String two) {
        logger.debug("stringEquals(String one, String two) as stringEquals(" + one + ", " + two + ") called");
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }

    /**
     * Returns the url of this local pap server, removing the username and password, if they are present.
     *
     * @return The url of this local pap server
     */
    public String[] getPapUrlUserPass() {
        logger.debug("getPapUrl() as getPapUrl() called");
        String url = XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_URL);
        if (url == null) {
            return null;
        }
        return splitPapUrlUserPass(url);
    }

    /**
     * Split the user and password of a PAP URL.
     *
     * @param url the URL
     * @return the user and password
     */
    public String[] splitPapUrlUserPass(String url) {
        String[] urlUserPass = new String[3];
        String[] commaSplit = url.split(",");
        urlUserPass[0] = commaSplit[0];
        if (commaSplit.length > 2) {
            urlUserPass[1] = commaSplit[1];
            urlUserPass[2] = commaSplit[2];
        }
        if (urlUserPass[1] == null || "".equals(urlUserPass[1])) {
            String usernamePropertyValue = XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_USERID);
            if (usernamePropertyValue != null) {
                urlUserPass[1] = usernamePropertyValue;
            }
        }
        if (urlUserPass[2] == null || "".equals(urlUserPass[2])) {
            String passwordPropertyValue =
                    PeCryptoUtils.decrypt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_PASS));
            if (passwordPropertyValue != null) {
                urlUserPass[2] = passwordPropertyValue;
            }
        }
        // if there is no comma, for some reason there is no user name and
        // password, so don't try to cut them off
        return urlUserPass;
    }

    /**
     * Register the PolicyDBDao instance in the PolicyDbDaoEntity table.
     *
     * @return Boolean, were we able to register?
     */
    private boolean register() {
        logger.debug("register() as register() called");
        String[] url = getPapUrlUserPass();
        // --- check URL length
        if (url == null || url.length < 3) {
            return false;
        }
        Session session = sessionfactory.openSession();
        try {
            startTransactionSynced(session, 1000);
        } catch (IllegalStateException e) {
            logger.debug("\nPolicyDBDao.register() caught an IllegalStateException: \n" + e + "\n");
            DatabaseLockEntity lock;
            lock = (DatabaseLockEntity) session.get(DatabaseLockEntity.class, 1);
            if (lock == null) {
                lock = new DatabaseLockEntity();
                lock.setKey(1);
                try {
                    session.persist(lock);
                    session.flush();
                    session.getTransaction().commit();
                    session.close();
                } catch (Exception e2) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, POLICYDBDAO_VAR,
                            "COULD NOT CREATE DATABASELOCK ROW.  WILL TRY ONE MORE TIME");
                }

                session = sessionfactory.openSession();
                try {
                    startTransactionSynced(session, 1000);
                } catch (Exception e3) {
                    String msg = "DATABASE LOCKING NOT WORKING. CONCURRENCY CONTROL NOT WORKING";
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e3, POLICYDBDAO_VAR, msg);
                    throw new IllegalStateException("msg" + "\n" + e3);
                }
            }
        }
        logger.debug("\nPolicyDBDao.register. Database locking and concurrency control is initialized\n");
        PolicyDbDaoEntity foundPolicyDbDaoEntity = null;
        Criteria cr = session.createCriteria(PolicyDbDaoEntity.class);
        cr.add(Restrictions.eq("policyDbDaoUrl", url[0]));
        List<?> data = cr.list();
        if (!data.isEmpty()) {
            foundPolicyDbDaoEntity = (PolicyDbDaoEntity) data.get(0);
        }

        // encrypt the password
        String txt = PeCryptoUtils.encrypt(url[2]);
        if (foundPolicyDbDaoEntity == null) {
            PolicyDbDaoEntity newPolicyDbDaoEntity = new PolicyDbDaoEntity();
            newPolicyDbDaoEntity.setPolicyDbDaoUrl(url[0]);
            newPolicyDbDaoEntity.setDescription("PAP server at " + url[0]);
            newPolicyDbDaoEntity.setUsername(url[1]);
            newPolicyDbDaoEntity.setPassword(txt);
            try {
                session.persist(newPolicyDbDaoEntity);
                session.getTransaction().commit();
            } catch (Exception e) {
                logger.debug(e);
                try {
                    session.getTransaction().rollback();
                } catch (Exception e2) {
                    logger.debug(e2);
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, POLICYDBDAO_VAR,
                            "Could not add new PolicyDBDao to the database");
                }
            }
        } else {
            // just want to update in order to change modified date
            if (url[1] != null && !stringEquals(url[1], foundPolicyDbDaoEntity.getUsername())) {
                foundPolicyDbDaoEntity.setUsername(url[1]);
            }
            if (txt != null && !stringEquals(txt, foundPolicyDbDaoEntity.getPassword())) {
                foundPolicyDbDaoEntity.setPassword(txt);
            }
            foundPolicyDbDaoEntity.preUpdate();
            try {
                session.getTransaction().commit();
            } catch (Exception e) {
                logger.debug(e);
                try {
                    session.getTransaction().rollback();
                } catch (Exception e2) {
                    logger.debug(e2);
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, POLICYDBDAO_VAR,
                            "Could not update PolicyDBDao in the database");
                }
            }
        }
        session.close();
        logger.debug("\nPolicyDBDao.register(). Success!!\n");
        return true;
    }

    /**
     * Synchronize group policies in the fuile system.
     *
     * <p>This method is called during all pushPolicy transactions and makes sure the file system group is in sync with
     * the database group entity
     *
     * @param pdpGroup the pdp group
     * @param groupentity the groupentity
     * @return the std PDP group
     * @throws PAPException the PAP exception
     * @throws PolicyDbException the policy db exception
     */
    public StdPDPGroup synchronizeGroupPoliciesInFileSystem(StdPDPGroup pdpGroup, GroupEntity groupentity)
            throws PAPException, PolicyDbException {

        HashMap<String, PDPPolicy> currentPolicyMap = new HashMap<>();
        HashSet<String> newPolicyIdSet = new HashSet<>();
        HashSet<PDPPolicy> newPolicySet = new HashSet<>();

        for (PDPPolicy pdpPolicy : pdpGroup.getPolicies()) {
            currentPolicyMap.put(pdpPolicy.getId(), pdpPolicy);
        }

        for (PolicyEntity policy : groupentity.getPolicies()) {
            String pdpPolicyId = getPdpPolicyName(policy.getPolicyName(), policy.getScope());
            newPolicyIdSet.add(pdpPolicyId);

            if (currentPolicyMap.containsKey(pdpPolicyId)) {
                newPolicySet.add(currentPolicyMap.get(pdpPolicyId));
            } else {
                // convert PolicyEntity object to PDPPolicy
                String name = pdpPolicyId.replace(".xml", "");
                name = name.substring(0, name.lastIndexOf('.'));
                InputStream policyStream = new ByteArrayInputStream(policy.getPolicyData().getBytes());
                pdpGroup.copyPolicyToFile(pdpPolicyId, name, policyStream);
                URI location = Paths.get(pdpGroup.getDirectory().toAbsolutePath().toString(), pdpPolicyId).toUri();
                StdPDPPolicy newPolicy = null;
                try {
                    newPolicy = new StdPDPPolicy(pdpPolicyId, true,
                            removeExtensionAndVersionFromPolicyName(pdpPolicyId), location);
                    newPolicySet.add(newPolicy);
                    logger.info("Adding new policy to PDPGroup - " + newPolicy.getId() + ", Location - " + location);
                } catch (Exception e) {
                    logger.debug(e);
                    PolicyLogger
                            .error("PolicyDBDao: Exception occurred while creating the StdPDPPolicy newPolicy object "
                                    + e.getMessage());
                }
            }
        }

        for (String id : currentPolicyMap.keySet()) {
            if (!newPolicyIdSet.contains(id)) {
                try {
                    Files.delete(Paths.get(currentPolicyMap.get(id).getLocation()));
                } catch (Exception e) {
                    logger.debug(e);
                    PolicyLogger.error(
                            "PolicyDBDao: Exception occurred while attempting to delete the old version of the policy"
                                    + " file from the group. " + e.getMessage());
                }
            }
        }

        logger.info("PolicyDBDao: Adding new policy set to group to keep filesystem and DB in sync");
        pdpGroup.setPolicies(newPolicySet);

        return pdpGroup;
    }

    /**
     * Removes the extension and version from policy name.
     *
     * @param originalPolicyName the original policy name
     * @return the string
     * @throws PolicyDbException the policy db exception
     */
    public String removeExtensionAndVersionFromPolicyName(String originalPolicyName) throws PolicyDbException {
        return getPolicyNameAndVersionFromPolicyFileName(originalPolicyName)[0];
    }

    /**
     * Splits apart the policy name and version from a policy file path.
     *
     * @param originalPolicyName the original policy name
     * @return An array [0]: The policy name, [1]: the policy version, as a string
     * @throws PolicyDbException the policy db exception
     */
    public String[] getPolicyNameAndVersionFromPolicyFileName(String originalPolicyName) throws PolicyDbException {
        String policyName = originalPolicyName;
        String[] nameAndVersion = new String[2];
        try {
            policyName = removeFileExtension(policyName);
            nameAndVersion[0] = policyName.substring(0, policyName.lastIndexOf('.'));
            if (isNullOrEmpty(nameAndVersion[0])) {
                throw new PolicyDbException();
            }
        } catch (Exception e) {
            nameAndVersion[0] = originalPolicyName;
            logger.debug(e);
        }
        try {
            nameAndVersion[1] = policyName.substring(policyName.lastIndexOf('.') + 1);
            if (isNullOrEmpty(nameAndVersion[1])) {
                throw new PolicyDbException();
            }
        } catch (Exception e) {
            nameAndVersion[1] = "1";
            logger.debug(e);
        }
        return nameAndVersion;
    }

    /**
     * Get the PDP policy by name.
     *
     * @param name The name to get
     * @param scope The scope to use
     * @return the policy.
     */
    public String getPdpPolicyName(String name, String scope) {
        String finalName = "";
        finalName += scope;
        finalName += ".";
        finalName += removeFileExtension(name);
        finalName += ".xml";
        return finalName;
    }

    private String removeFileExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * Audit local database.
     *
     * @param papEngine2 the pap engine 2
     */
    public void auditLocalDatabase(PAPPolicyEngine papEngine2) {
        logger.debug("PolicyDBDao.auditLocalDatabase() is called");
        try {
            deleteAllGroupTables();
            auditGroups(papEngine2);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, POLICYDBDAO_VAR, "auditLocalDatabase() error");
            logger.error("Exception Occured" + e);
        }
    }

    /**
     * Audit local file system.
     *
     * @param group the group
     * @return the std PDP group
     */
    public StdPDPGroup auditLocalFileSystem(StdPDPGroup group) {

        logger.info("Starting Local File System group audit");
        Session session = sessionfactory.openSession();
        session.getTransaction().begin();

        StdPDPGroup updatedGroup = null;
        try {
            Query groupQuery = session.createQuery(GROUPENTITY_SELECT);
            groupQuery.setParameter(GROUP_ID, group.getId());
            groupQuery.setParameter(DELETED, false);
            List<?> groupQueryList = groupQuery.list();
            if (groupQueryList != null && !groupQueryList.isEmpty()) {
                GroupEntity dbgroup = (GroupEntity) groupQueryList.get(0);
                updatedGroup = synchronizeGroupPoliciesInFileSystem(group, dbgroup);
                logger.info("Group was updated during file system audit: " + updatedGroup.toString());
            }
        } catch (PAPException | PolicyDbException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, POLICYDBDAO_VAR,
                    "Caught Exception trying to check if group exists groupQuery.getResultList()");
            throw new PersistenceException("Query failed trying to check if group " + group.getId() + " exists");
        }

        session.getTransaction().commit();
        session.close();

        return updatedGroup;
    }

    /**
     * Synchronize config data in file system.
     */
    public void synchronizeConfigDataInFileSystem() {

        logger.info("Starting Local File System Config data Sync");
        // sync both web apps Config and Action
        syncConfigData(ConfigurationDataEntity.class, CONFIG);
        syncConfigData(ActionBodyEntity.class, ACTION);
    }

    private <T> void syncConfigData(Class<T> cl, String type) {
        Session session = sessionfactory.openSession();
        try {
            final Criteria configDataQuery = session.createCriteria(cl.getName());
            @SuppressWarnings("unchecked")
            final List<T> configDataResult = configDataQuery.list();
            Path webappsPath = Paths.get(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_WEBAPPS), type);

            for (final T configData : configDataResult) {
                String configName = null;
                byte[] configBody;
                try {
                    if (CONFIG.equalsIgnoreCase(type)) {
                        configName = ((ConfigurationDataEntity) configData).getConfigurationName();
                        configBody =
                                (((ConfigurationDataEntity) configData).getConfigBody() != null)
                                        ? ((ConfigurationDataEntity) configData).getConfigBody()
                                                .getBytes(StandardCharsets.UTF_8)
                                        : "".getBytes();
                    } else {
                        configName = ((ActionBodyEntity) configData).getActionBodyName();
                        configBody = (((ActionBodyEntity) configData).getActionBody() != null)
                                ? ((ActionBodyEntity) configData).getActionBody().getBytes(StandardCharsets.UTF_8)
                                : "".getBytes();
                    }
                    Path filePath = Paths.get(webappsPath.toString(), configName);
                    if (!filePath.toFile().exists()) {
                        Files.write(filePath, configBody);
                        logger.info("Created Config File from DB - " + filePath.toString());
                    }
                } catch (Exception e) {
                    // log and keep going
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, POLICYDBDAO_VAR,
                            "Exception occured while creating Configuration File - " + configName);
                }
            }
        } catch (final Exception exception) {
            logger.error("Unable to synchronizeConfigDataInFileSystem", exception);
        }
        session.close();
    }

    /**
     * Delete all group tables.
     */
    public void deleteAllGroupTables() {
        logger.debug("PolicyDBDao.deleteAllGroupTables() called");
        Session session = sessionfactory.openSession();
        session.getTransaction().begin();

        Query deletePdpEntityEntityTableUpdate = session.getNamedQuery("PdpEntity.deleteAll");
        deletePdpEntityEntityTableUpdate.executeUpdate();

        Query deleteGroupEntityTableUpdate = session.getNamedQuery("GroupEntity.deleteAll");
        deleteGroupEntityTableUpdate.executeUpdate();

        session.getTransaction().commit();
        session.close();
    }

    /**
     * Audit groups.
     *
     * @param papEngine2 the pap engine 2
     */
    @SuppressWarnings("unchecked")
    public void auditGroups(PAPPolicyEngine papEngine2) {
        logger.debug("PolicyDBDao.auditGroups() called");

        Session session = sessionfactory.openSession();
        session.getTransaction().begin();
        try {

            Set<OnapPDPGroup> groups = papEngine2.getOnapPDPGroups();

            for (OnapPDPGroup grp : groups) {
                try {
                    GroupEntity groupEntity = new GroupEntity();
                    groupEntity.setGroupName(grp.getName());
                    groupEntity.setDescription(grp.getDescription());
                    groupEntity.setDefaultGroup(grp.isDefaultGroup());
                    groupEntity.setCreatedBy(AUDIT_STR);
                    groupEntity.setGroupId(createNewPdpGroupId(grp.getId()));
                    groupEntity.setModifiedBy(AUDIT_STR);
                    session.persist(groupEntity);
                    Set<OnapPDP> pdps = grp.getOnapPdps();

                    for (OnapPDP pdp : pdps) {
                        PdpEntity pdpEntity = new PdpEntity();
                        pdpEntity.setGroup(groupEntity);
                        pdpEntity.setJmxPort(pdp.getJmxPort());
                        pdpEntity.setPdpId(pdp.getId());
                        pdpEntity.setPdpName(pdp.getName());
                        pdpEntity.setModifiedBy(AUDIT_STR);
                        pdpEntity.setCreatedBy(AUDIT_STR);
                        session.persist(pdpEntity);
                    }

                    Set<PDPPolicy> policies = grp.getPolicies();

                    for (PDPPolicy policy : policies) {
                        try {
                            String[] stringArray = getNameScopeAndVersionFromPdpPolicy(policy.getId());
                            if (stringArray == null) {
                                throw new IllegalArgumentException(
                                        "Invalid input - policyID must contain name, scope and version");
                            }
                            List<PolicyEntity> policyEntityList;
                            Query getPolicyEntitiesQuery = session.getNamedQuery("PolicyEntity.findByNameAndScope");
                            getPolicyEntitiesQuery.setParameter("name", stringArray[0]);
                            getPolicyEntitiesQuery.setParameter(SCOPE, stringArray[1]);

                            policyEntityList = getPolicyEntitiesQuery.list();
                            PolicyEntity policyEntity = null;
                            if (!policyEntityList.isEmpty()) {
                                policyEntity = policyEntityList.get(0);
                            }
                            if (policyEntity != null) {
                                groupEntity.addPolicyToGroup(policyEntity);
                            }
                        } catch (Exception e2) {
                            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, POLICYDBDAO_VAR,
                                    "Exception auditGroups inner catch");
                        }
                    }
                } catch (Exception e1) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, POLICYDBDAO_VAR,
                            "Exception auditGroups middle catch");
                }
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, POLICYDBDAO_VAR, "Exception auditGroups outer catch");
            session.close();
            return;
        }

        session.getTransaction().commit();
        session.close();

    }

    /**
     * Gets the config file.
     *
     * @param filename the filename
     * @param policy the policy
     * @return the config file
     */
    public String getConfigFile(String filename, PolicyRestAdapter policy) {
        if (policy == null) {
            return getConfigFile(filename, (String) null);
        }
        return getConfigFile(filename, policy.getConfigType());
    }

    // copied from ConfigPolicy.java and modified
    // Here we are adding the extension for the configurations file based on the
    /**
     * Gets the config file.
     *
     * @param inputFilename the input filename
     * @param configType the config type
     * @return the config file
     */
    // config type selection for saving.
    public String getConfigFile(String inputFilename, String configType) {
        String filename = inputFilename;
        logger.debug("getConfigFile(String filename, String scope, String configType) as getConfigFile(" + filename
                + ", " + configType + ") called");
        filename = FilenameUtils.removeExtension(filename);
        String id = configType;

        if (id != null) {
            if (id.equals(ConfigPolicy.JSON_CONFIG) || id.contains("Firewall")) {
                filename = filename + ".json";
            }
            if (id.equals(ConfigPolicy.XML_CONFIG)) {
                filename = filename + ".xml";
            }
            if (id.equals(ConfigPolicy.PROPERTIES_CONFIG)) {
                filename = filename + ".properties";
            }
            if (id.equals(ConfigPolicy.OTHER_CONFIG)) {
                filename = filename + ".txt";
            }
        }
        return filename;
    }

    /**
     * Gets the name scope and version from pdp policy.
     *
     * @param fileName the file name
     * @return the name scope and version from pdp policy
     */
    public String[] getNameScopeAndVersionFromPdpPolicy(String fileName) {
        String[] splitByDots = fileName.split("\\.");
        if (splitByDots.length < 3) {
            return null;
        }
        String policyName = splitByDots[splitByDots.length - 3];
        String version = splitByDots[splitByDots.length - 2];
        // policy names now include version
        String scope = "";
        for (int i = 0; i < splitByDots.length - 3; i++) {
            scope += ".".concat(splitByDots[i]);
        }
        // remove the first dot
        if (scope.length() > 0) {
            scope = scope.substring(1);
        }
        String[] returnArray = new String[3];
        returnArray[0] = policyName + "." + version + ".xml";
        returnArray[2] = version;
        returnArray[1] = scope;
        return returnArray;
    }

    /**
     * Creates the new PDP group id.
     *
     * @param name the name
     * @return the string
     */
    public static String createNewPdpGroupId(String name) {
        String id = name;
        // replace "bad" characters with sequences that will be ok for file
        // names and properties keys.
        id = id.replace(" ", "_sp_");
        id = id.replace("\t", "_tab_");
        id = id.replace("\\", "_bksl_");
        id = id.replace("/", "_sl_");
        id = id.replace(":", "_col_");
        id = id.replace("*", "_ast_");
        id = id.replace("?", "_q_");
        id = id.replace("\"", "_quo_");
        id = id.replace("<", "_lt_");
        id = id.replace(">", "_gt_");
        id = id.replace("|", "_bar_");
        id = id.replace("=", "_eq_");
        id = id.replace(",", "_com_");
        id = id.replace(";", "_scom_");

        return id;
    }

    /**
     * Checks if any of the given strings are empty or null.
     *
     * @param strings One or more Strings (or nulls) to check if they are null or empty
     * @return true if one or more of the given strings are empty or null
     */
    public static boolean isNullOrEmpty(String... strings) {
        for (String s : strings) {
            if (s == null || "".equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the other servers.
     *
     * @return the other servers
     */
    public List<?> getOtherServers() {
        return otherServers;
    }

    /**
     * Sets the other servers.
     *
     * @param otherServers the new other servers
     */
    public void setOtherServers(List<?> otherServers) {
        this.otherServers = otherServers;
    }

    /**
     * Gets the pap engine.
     *
     * @return the pap engine
     */
    public PAPPolicyEngine getPapEngine() {
        return papEngine;
    }

    /**
     * Checks if is junit.
     *
     * @return true, if is junit
     */
    public static boolean isJunit() {
        return isJunit;
    }

    /**
     * Sets the junit.
     *
     * @param isJunit the new junit
     */
    public static void setJunit(boolean isJunit) {
        PolicyDbDao.isJunit = isJunit;
    }

    /**
     * Gets the policy DB dao test class.
     *
     * @return the policy DB dao test class
     */
    public static PolicyDbDaoTestClass getPolicyDbDaoTestClass() {
        return new PolicyDbDao().new PolicyDbDaoTestClass();
    }

    /**
     * The Class PolicyDBDaoTestClass.
     */
    final class PolicyDbDaoTestClass {

        /**
         * Gets the config file.
         *
         * @param filename the filename
         * @param scope the scope
         * @param policy the policy
         * @return the config file
         */
        String getConfigFile(String filename, String scope, PolicyRestAdapter policy) {
            return scope + "." + PolicyDbDao.this.getConfigFile(filename, policy);
        }

        /**
         * Gets the policy name and version from policy file name.
         *
         * @param originalPolicyName the original policy name
         * @return the policy name and version from policy file name
         * @throws PolicyDbException the policy db exception
         */
        String[] getPolicyNameAndVersionFromPolicyFileName(String originalPolicyName) throws PolicyDbException {
            return PolicyDbDao.this.getPolicyNameAndVersionFromPolicyFileName(originalPolicyName);
        }

        /**
         * Gets the name scope and version from pdp policy.
         *
         * @param fileName the file name
         * @return the name scope and version from pdp policy
         */
        String[] getNameScopeAndVersionFromPdpPolicy(String fileName) {
            return PolicyDbDao.this.getNameScopeAndVersionFromPdpPolicy(fileName);
        }

        /**
         * Gets the pdp policy name.
         *
         * @param name the name
         * @param scope the scope
         * @return the pdp policy name
         */
        String getPdpPolicyName(String name, String scope) {
            return PolicyDbDao.this.getPdpPolicyName(name, scope);
        }
    }

}
