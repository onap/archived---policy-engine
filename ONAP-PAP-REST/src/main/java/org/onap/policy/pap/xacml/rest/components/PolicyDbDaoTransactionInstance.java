/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.components;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.PolicyDbException;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.rest.jpa.PolicyAuditlog;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.util.XACMLPolicyWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Component
public class PolicyDbDaoTransactionInstance implements PolicyDbDaoTransaction {
    private static final Logger logger = FlexLogger.getLogger(PolicyDbDaoTransactionInstance.class);

    // Recurring constants
    private static final String BRACKET_CALLED = ") called";
    private static final String EXISTS = " exists";
    private static final String GROUP = "group";
    private static final String CAUGHT_EXCEPTION_ON_NOTIFY_OTHERS = "Caught Exception on notifyOthers(";

    private final Object emLock = new Object();
    long policyId;
    long groupId;
    long pdpId;
    String newGroupId;
    private boolean operationRun = false;
    private Thread transactionTimer;
    private static final String POLICY_NOTIFICATION = "policy";
    private static final String PDP_NOTIFICATION = "pdp";
    private static final String GROUP_NOTIFICATION = GROUP;

    private static final String DECISIONMS_MODEL = "MicroService_Model";
    private static boolean isJunit = false;
    Session session;

    /**
     * Instantiates a new policy DB dao transaction instance.
     *
     * @param test the test
     */
    public PolicyDbDaoTransactionInstance(String test) {
        // call the constructor with arguments
        this(Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_TRANS_TIMEOUT)),
                Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_TRANS_WAIT)));
    }

    public PolicyDbDaoTransactionInstance() {
        // Default Constructor
    }

    @Autowired
    public PolicyDbDaoTransactionInstance(SessionFactory sessionfactory) {
        PolicyDbDaoTransactionInstance.sessionfactory = sessionfactory;
    }

    private static SessionFactory sessionfactory;

    /**
     * Instantiates a new policy DB dao transaction instance.
     *
     * @param transactionTimeout the transaction timeout is how long the transaction can sit before rolling back
     * @param transactionWaitTime the transaction wait time is how long to wait for the transaction to start before
     */
    public PolicyDbDaoTransactionInstance(int transactionTimeout, int transactionWaitTime) {
        logger.info("\n\nPolicyDBDaoTransactionInstance() as PolicyDBDaoTransactionInstance() called:"
                + "\n   transactionTimeout = " + transactionTimeout + "\n   transactionWaitTime = "
                + transactionWaitTime + "\n\n");

        policyId = -1;
        groupId = -1;
        pdpId = -1;
        newGroupId = null;
        synchronized (emLock) {
            session = sessionfactory.openSession();
            try {
                PolicyDbDao.getPolicyDbDaoInstance().startTransactionSynced(session, transactionWaitTime);
            } catch (Exception e) {
                logger.error("Could not lock transaction within " + transactionWaitTime + " milliseconds" + e);
                throw new PersistenceException(
                        "Could not lock transaction within " + transactionWaitTime + " milliseconds");
            }
        }
        class TransactionTimer implements Runnable {

            private int sleepTime;

            public TransactionTimer(int timeout) {
                this.sleepTime = timeout;
            }

            @Override
            public void run() {
                if (logger.isDebugEnabled()) {
                    Date date = new java.util.Date();
                    logger.debug("\n\nTransactionTimer.run() - SLEEPING: " + "\n   sleepTime (ms) = " + sleepTime
                            + "\n   TimeStamp  = " + date.getTime() + "\n\n");
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // probably, the transaction was completed, the last thing
                    // we want to do is roll back
                    if (logger.isDebugEnabled()) {
                        Date date = new java.util.Date();
                        logger.debug("\n\nTransactionTimer.run() - WAKE Interrupt: " + "\n   TimeStamp = "
                                + date.getTime() + "\n\n");
                    }
                    Thread.currentThread().interrupt();
                    return;
                }
                if (logger.isDebugEnabled()) {
                    Date date = new java.util.Date();
                    logger.debug("\n\nTransactionTimer.run() - WAKE Timeout: " + "\n   TimeStamp = " + date.getTime()
                            + "\n\n");
                }
                logger.warn("PolicyDBDaoTransactionInstance - TransactionTimer - Rolling back transaction.");
                rollbackTransaction();
            }

        }

        transactionTimer = new Thread(new TransactionTimer(transactionTimeout), "transactionTimerThread");
        transactionTimer.start();

    }

    private void checkBeforeOperationRun() {
        checkBeforeOperationRun(false);
    }

    private void checkBeforeOperationRun(boolean justCheckOpen) {
        if (!isTransactionOpen()) {
            PolicyLogger.warn("checkBeforeOperationRun - There is no transaction currently open");
            throw new IllegalStateException("There is no transaction currently open");
        }
        if (operationRun && !justCheckOpen) {
            PolicyLogger.warn("checkBeforeOperationRun - "
                    + "An operation has already been performed and the current transaction should be committed");
            throw new IllegalStateException(
                    "An operation has already been performed and the current transaction should be committed");
        }
        operationRun = true;
    }

    @Override
    public void commitTransaction() {
        synchronized (emLock) {
            NotifyOtherPaps otherPaps = new NotifyOtherPaps();
            logger.debug("commitTransaction() as commitTransaction() called");
            if (!isTransactionOpen()) {
                logger.warn(
                        "There is no open transaction to commit - PolicyId - " + policyId + ", GroupId - " + groupId);
                try {
                    session.close();
                } catch (Exception e) {
                    logger.error("Exception Occured" + e);
                }
                return;
            }
            try {
                session.getTransaction().commit();
            } catch (RollbackException e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught RollbackException on em.getTransaction().commit()");
                throw new PersistenceException("The commit failed. Message:\n" + e.getMessage());
            }
            session.close();
            // need to revisit
            if (policyId >= 0) {
                if (newGroupId != null) {
                    try {
                        otherPaps.notifyOthers(policyId, POLICY_NOTIFICATION, newGroupId);
                    } catch (Exception e) {
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                CAUGHT_EXCEPTION_ON_NOTIFY_OTHERS + policyId + "," + POLICY_NOTIFICATION + ","
                                        + newGroupId + ")");
                    }
                } else {
                    try {
                        otherPaps.notifyOthers(policyId, POLICY_NOTIFICATION);
                    } catch (Exception e) {
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                CAUGHT_EXCEPTION_ON_NOTIFY_OTHERS + policyId + "," + POLICY_NOTIFICATION + ")");
                    }
                }
            }
            if (groupId >= 0) {
                // we don't want commit to fail just because this does
                if (newGroupId != null) {
                    try {
                        otherPaps.notifyOthers(groupId, GROUP_NOTIFICATION, newGroupId);
                    } catch (Exception e) {
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                CAUGHT_EXCEPTION_ON_NOTIFY_OTHERS + groupId + "," + GROUP_NOTIFICATION + ","
                                        + newGroupId + ")");
                    }
                } else {
                    try {
                        otherPaps.notifyOthers(groupId, GROUP_NOTIFICATION);
                    } catch (Exception e) {
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                CAUGHT_EXCEPTION_ON_NOTIFY_OTHERS + groupId + "," + GROUP_NOTIFICATION + ")");
                    }
                }
            }
            if (pdpId >= 0) {
                // we don't want commit to fail just because this does
                try {
                    otherPaps.notifyOthers(pdpId, PDP_NOTIFICATION);
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                            CAUGHT_EXCEPTION_ON_NOTIFY_OTHERS + pdpId + "," + PDP_NOTIFICATION + ")");
                }
            }
        }
        if (transactionTimer != null) {
            transactionTimer.interrupt();
        }
    }

    @Override
    public void rollbackTransaction() {
        logger.debug("rollbackTransaction() as rollbackTransaction() called");
        synchronized (emLock) {
            if (isTransactionOpen()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                            "Could not rollback transaction");
                }
                try {
                    session.close();
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                            "Could not close Hibernate Session.");
                }

            } else {
                try {
                    session.close();
                } catch (Exception e) {
                    logger.warn("Could not close already closed transaction", e);
                }
            }
        }
        if (transactionTimer != null) {
            transactionTimer.interrupt();
        }
    }

    private void createPolicy(PolicyRestAdapter policy, String username, String policyScope, String inputPolicyName,
            String policyDataString) {
        String policyName = inputPolicyName;
        logger.debug("createPolicy(PolicyRestAdapter policy, String username, String policyScope,"
                + " String policyName, String policyDataString) as createPolicy(" + policy + ", " + username + ", "
                + policyScope + ", " + policyName + ", " + policyDataString + ")  called");
        synchronized (emLock) {
            PolicyDbDao policyDbDao = new PolicyDbDao();
            checkBeforeOperationRun();
            String configName = policyName;
            if (policyName.contains("Config_")) {
                policyName = policyName.replace(".Config_", ":Config_");
            } else if (policyName.contains("Action_")) {
                policyName = policyName.replace(".Action_", ":Action_");
            } else if (policyName.contains("Decision_MS_")) {
                policyName = policyName.replace(".Decision_MS_", ":Decision_MS_");
            } else if (policyName.contains("Decision_")) {
                policyName = policyName.replace(".Decision_", ":Decision_");
            }
            policyName = policyName.split(":")[1];
            Query createPolicyQuery = session
                    .createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:policyName");
            createPolicyQuery.setParameter(PolicyDbDao.SCOPE, policyScope);
            createPolicyQuery.setParameter("policyName", policyName);
            List<?> createPolicyQueryList = createPolicyQuery.list();
            PolicyEntity newPolicyEntity;
            boolean update;
            if (createPolicyQueryList.isEmpty()) {
                newPolicyEntity = new PolicyEntity();
                update = false;
            } else if (createPolicyQueryList.size() > 1) {
                PolicyLogger.error("Somehow, more than one policy with the same "
                        + "scope, name, and deleted status were found in the database");
                throw new PersistenceException("Somehow, more than one policy with the same"
                        + " scope, name, and deleted status were found in the database");
            } else {
                newPolicyEntity = (PolicyEntity) createPolicyQueryList.get(0);
                update = true;
            }

            ActionBodyEntity newActionBodyEntity = null;
            if (policy.getPolicyType().equals(PolicyDbDao.ACTION)) {
                boolean abupdate = false;
                if (newPolicyEntity.getActionBodyEntity() == null) {
                    newActionBodyEntity = new ActionBodyEntity();
                } else {
                    newActionBodyEntity = (ActionBodyEntity) session.get(ActionBodyEntity.class,
                            newPolicyEntity.getActionBodyEntity().getActionBodyId());
                    abupdate = true;
                }

                if (newActionBodyEntity != null) {
                    // build the file path
                    // trim the .xml off the end
                    String policyNameClean = FilenameUtils.removeExtension(configName);
                    String actionBodyName = policyNameClean + ".json";

                    // get the action body
                    String actionBodyString = policy.getActionBody();
                    if (actionBodyString == null) {
                        actionBodyString = "{}";
                    }
                    newActionBodyEntity.setActionBody(actionBodyString);
                    newActionBodyEntity.setActionBodyName(actionBodyName);
                    newActionBodyEntity.setModifiedBy("PolicyDBDao.createPolicy()");
                    newActionBodyEntity.setDeleted(false);
                    if (!abupdate) {
                        newActionBodyEntity.setCreatedBy("PolicyDBDao.createPolicy()");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("\nPolicyDBDao.createPolicy" + "\n   newActionBodyEntity.getActionBody() = "
                                + newActionBodyEntity.getActionBody()
                                + "\n   newActionBodyEntity.getActionBodyName() = "
                                + newActionBodyEntity.getActionBodyName()
                                + "\n   newActionBodyEntity.getModifiedBy() = " + newActionBodyEntity.getModifiedBy()
                                + "\n   newActionBodyEntity.getCreatedBy() = " + newActionBodyEntity.getCreatedBy()
                                + "\n   newActionBodyEntity.isDeleted() = " + newActionBodyEntity.isDeleted()
                                + "\n   FLUSHING to DB");
                    }
                    // push the actionBodyEntity to the DB
                    if (isJunit) {
                        newActionBodyEntity.prePersist();
                    }
                    if (!abupdate) {
                        session.persist(newActionBodyEntity);
                    }
                } else {
                    // newActionBodyEntity == null
                    // We have a actionBody in the policy but we found no
                    // actionBody in the DB
                    String msg = "\n\nPolicyDBDao.createPolicy - Incoming Action policy had an "
                            + "actionBody, but it could not be found in the DB for update." + "\n  policyScope = "
                            + policyScope + "\n  policyName = " + policyName + "\n\n";
                    PolicyLogger.error("PolicyDBDao.createPolicy - Incoming Action policy had an actionBody, "
                            + "but it could not be found in the DB for update: policyName = " + policyName);
                    throw new IllegalArgumentException(msg);
                }
            }

            ConfigurationDataEntity newConfigurationDataEntity;
            if (PolicyDbDao.CONFIG.equals(policy.getPolicyType())
                    || DECISIONMS_MODEL.equals(policy.getRuleProvider())) {
                boolean configUpdate;
                if (newPolicyEntity.getConfigurationData() == null) {
                    newConfigurationDataEntity = new ConfigurationDataEntity();
                    configUpdate = false;
                } else {
                    newConfigurationDataEntity = (ConfigurationDataEntity) session.get(ConfigurationDataEntity.class,
                            newPolicyEntity.getConfigurationData().getConfigurationDataId());
                    configUpdate = true;
                }

                if (newConfigurationDataEntity != null) {
                    if (!PolicyDbDao.stringEquals(newConfigurationDataEntity.getConfigurationName(),
                            policyDbDao.getConfigFile(configName, policy))) {
                        newConfigurationDataEntity.setConfigurationName(policyDbDao.getConfigFile(configName, policy));
                    }
                    if (newConfigurationDataEntity.getConfigType() == null
                            || !newConfigurationDataEntity.getConfigType().equals(policy.getConfigType())) {
                        newConfigurationDataEntity.setConfigType(policy.getConfigType());
                    }
                    if (!configUpdate) {
                        newConfigurationDataEntity.setCreatedBy(username);
                    }
                    if (newConfigurationDataEntity.getModifiedBy() == null
                            || !newConfigurationDataEntity.getModifiedBy().equals(username)) {
                        newConfigurationDataEntity.setModifiedBy(username);
                    }
                    if (newConfigurationDataEntity.getDescription() == null
                            || !newConfigurationDataEntity.getDescription().equals("")) {
                        newConfigurationDataEntity.setDescription("");
                    }
                    if (newConfigurationDataEntity.getConfigBody() == null
                            || newConfigurationDataEntity.getConfigBody().isEmpty()
                            || (!newConfigurationDataEntity.getConfigBody().equals(policy.getConfigBodyData()))) {
                        // hopefully one of these won't be null
                        if (policy.getConfigBodyData() == null || policy.getConfigBodyData().isEmpty()) {
                            newConfigurationDataEntity.setConfigBody(policy.getJsonBody());
                        } else {
                            newConfigurationDataEntity.setConfigBody(policy.getConfigBodyData());
                        }
                    }
                    if (newConfigurationDataEntity.isDeleted()) {
                        newConfigurationDataEntity.setDeleted(false);
                    }
                    if (isJunit) {
                        newConfigurationDataEntity.prePersist();
                    }
                    if (!configUpdate) {
                        session.persist(newConfigurationDataEntity);
                    }
                } else {
                    // We have a configurationData body in the policy but we
                    // found no configurationData body in the DB
                    String msg = "\n\nPolicyDBDao.createPolicy - Incoming Config policy had a "
                            + "configurationData body, but it could not be found in the DB for update."
                            + "\n  policyScope = " + policyScope + "\n  policyName = " + policyName + "\n\n";
                    PolicyLogger
                            .error("PolicyDBDao.createPolicy - Incoming Config policy had a configurationData body, "
                                    + "but it could not be found in the DB for update: policyName = " + policyName);
                    throw new IllegalArgumentException(msg);
                }

            } else {
                newConfigurationDataEntity = null;
            }
            policyId = newPolicyEntity.getPolicyId();

            if (!PolicyDbDao.stringEquals(newPolicyEntity.getPolicyName(), policyName)) {
                newPolicyEntity.setPolicyName(policyName);
            }
            if (!PolicyDbDao.stringEquals(newPolicyEntity.getCreatedBy(), username)) {
                newPolicyEntity.setCreatedBy(username);
            }
            if (!PolicyDbDao.stringEquals(newPolicyEntity.getDescription(), policy.getPolicyDescription())) {
                newPolicyEntity.setDescription(policy.getPolicyDescription());
            }
            if (!PolicyDbDao.stringEquals(newPolicyEntity.getModifiedBy(), username)) {
                newPolicyEntity.setModifiedBy(username);
            }
            if (!PolicyDbDao.stringEquals(newPolicyEntity.getPolicyData(), policyDataString)) {
                newPolicyEntity.setPolicyData(policyDataString);
            }
            if (!PolicyDbDao.stringEquals(newPolicyEntity.getScope(), policyScope)) {
                newPolicyEntity.setScope(policyScope);
            }
            if (newPolicyEntity.isDeleted()) {
                newPolicyEntity.setDeleted(false);
            }
            newPolicyEntity.setConfigurationData(newConfigurationDataEntity);
            newPolicyEntity.setActionBodyEntity(newActionBodyEntity);
            if (isJunit) {
                newPolicyEntity.prePersist();
            }
            if (!update) {
                session.persist(newPolicyEntity);
            }
            session.flush();
            this.policyId = newPolicyEntity.getPolicyId();
        }
        return;
    }

    @Override
    public void createPolicy(Policy policy, String username) {
        InputStream policyXmlStream = null;
        try {
            logger.debug("createPolicy(PolicyRestAdapter policy, String username) as createPolicy(" + policy + ","
                    + username + BRACKET_CALLED);
            String policyScope = policy.policyAdapter.getDomainDir().replace(File.separator, ".");
            // Does not need to be XACMLPolicyWriterWithPapNotify since it is
            // already in the PAP
            // and this transaction is intercepted up stream.
            String policyDataString;

            try {
                if (policy.policyAdapter.getData() instanceof PolicySetType) {
                    policyXmlStream = XACMLPolicyWriter
                            .getPolicySetXmlAsInputStream((PolicySetType) policy.getCorrectPolicyDataObject());
                } else {
                    policyXmlStream = XACMLPolicyWriter.getXmlAsInputStream(policy.getCorrectPolicyDataObject());
                }
                policyDataString = IOUtils.toString(policyXmlStream);
            } catch (IOException e) {
                policyDataString = "could not read";
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught IOException on IOUtils.toString(" + policyXmlStream + ")");
                throw new IllegalArgumentException("Cannot parse the policy xml from the PolicyRestAdapter.");
            }

            IOUtils.closeQuietly(policyXmlStream);
            if (PolicyDbDao.isJunit()) {
            	if (policyDataString != null) {
            		logger.warn("isJUnit will overwrite policyDataString");
            	}
                // Using parentPath object to set policy data.
                policyDataString = policy.policyAdapter.getParentPath();
            }
            String configPath = "";
            if (PolicyDbDao.CONFIG.equalsIgnoreCase(policy.policyAdapter.getPolicyType())) {
                configPath = evaluateXPath(
                        "/Policy/Rule/AdviceExpressions/AdviceExpression[contains(@AdviceId,'ID')]/"
                                + "AttributeAssignmentExpression[@AttributeId='URLID']/AttributeValue/text()",
                        policyDataString);
            } else if (PolicyDbDao.ACTION.equalsIgnoreCase(policy.policyAdapter.getPolicyType())) {
                configPath = evaluateXPath(
                        "/Policy/Rule/ObligationExpressions/ObligationExpression[contains(@ObligationId, "
                                + policy.policyAdapter.getActionAttribute()
                                + ")]/AttributeAssignmentExpression[@AttributeId='body']/AttributeValue/text()",
                        policyDataString);
            } else if (DECISIONMS_MODEL.equalsIgnoreCase(policy.policyAdapter.getRuleProvider())) {
                configPath = evaluateXPath(
                        "/Policy/Rule/AdviceExpressions/AdviceExpression[contains(@AdviceId,'MicroService')]/"
                                + "AttributeAssignmentExpression[@AttributeId='URLID']/AttributeValue/text()",
                        policyDataString);
            }

            String prefix = null;
            if (PolicyDbDao.CONFIG.equalsIgnoreCase(policy.policyAdapter.getPolicyType())
                    || DECISIONMS_MODEL.equalsIgnoreCase(policy.policyAdapter.getRuleProvider())) {
                prefix = configPath.substring(configPath.indexOf(policyScope + ".") + policyScope.concat(".").length(),
                        configPath.lastIndexOf(policy.policyAdapter.getPolicyName()));
                if (PolicyDbDao.isNullOrEmpty(policy.policyAdapter.getConfigBodyData())) {
                    String configData = "";
                    try {
                        String newConfigPath = configPath;
                        try {
                            newConfigPath = processConfigPath(newConfigPath);
                        } catch (Exception e2) {
                            logger.error("Could not process config path: " + newConfigPath, e2);
                        }
                        configData = readConfigFile(newConfigPath);
                    } catch (Exception e) {
                        logger.error("Could not read config body data for " + configPath, e);
                    }
                    policy.policyAdapter.setConfigBodyData(configData);
                }
            } else if (PolicyDbDao.ACTION.equalsIgnoreCase(policy.policyAdapter.getPolicyType())) {
                prefix = "Action_";
            } else if ("Decision".equalsIgnoreCase(policy.policyAdapter.getPolicyType())) {
                prefix = "Decision_";
            }

            if (!(policy.policyAdapter.getData() instanceof PolicyType)
                    && !(policy.policyAdapter.getData() instanceof PolicySetType)) {
                PolicyLogger.error("The data field is not an instance of PolicyType");
                throw new IllegalArgumentException("The data field is not an instance of PolicyType");
            }
            String finalName = policyScope + "." + prefix + policy.policyAdapter.getPolicyName() + "."
                    + policy.policyAdapter.getHighestVersion() + ".xml";
            if (policy.policyAdapter.getConfigType() == null || "".equals(policy.policyAdapter.getConfigType())) {
                // get the config file extension
                String ext = "";
                if (configPath != null && !"".equalsIgnoreCase(configPath)) {
                    ext = configPath.substring(configPath.lastIndexOf('.'), configPath.length());
                }

                if (ext.contains("txt")) {
                    policy.policyAdapter.setConfigType(PolicyDbDao.OTHER_CONFIG);
                } else if (ext.contains("json")) {
                    policy.policyAdapter.setConfigType(PolicyDbDao.JSON_CONFIG);
                } else if (ext.contains("xml")) {
                    policy.policyAdapter.setConfigType(PolicyDbDao.XML_CONFIG);
                } else if (ext.contains("properties")) {
                    policy.policyAdapter.setConfigType(PolicyDbDao.PROPERTIES_CONFIG);
                } else {
                    if (policy.policyAdapter.getPolicyType().equalsIgnoreCase(PolicyDbDao.ACTION)) {
                        policy.policyAdapter.setConfigType(PolicyDbDao.JSON_CONFIG);
                    }
                }
            }

            createPolicy(policy.policyAdapter, username, policyScope, finalName, policyDataString);
        } finally {
            if (policyXmlStream != null) {
                try {
                    policyXmlStream.close();
                } catch (IOException e) {
                    logger.error("Exception Occured while closing input stream" + e);
                }
            }
        }
    }

    public PolicyEntity getPolicy(int policyId) {
        return getPolicy(policyId, null, null);
    }

    public PolicyEntity getPolicy(String policyName, String scope) {
        return getPolicy(-1, policyName, scope);
    }

    private PolicyEntity getPolicy(int policyIdVar, String policyName, String scope) {
        logger.debug("getPolicy(int policyId, String policyName) as " + " getPolicy(" + policyIdVar + "," + policyName
                + BRACKET_CALLED);
        if (policyIdVar < 0 && PolicyDbDao.isNullOrEmpty(policyName, scope)) {
            throw new IllegalArgumentException("policyID must be at least 0 or policyName must be not null or blank");
        }

        synchronized (emLock) {
            checkBeforeOperationRun(true);
            // check if group exists
            String locPolicyId;
            Query policyQuery;
            if (!PolicyDbDao.isNullOrEmpty(policyName, scope)) {
                locPolicyId = policyName;
                policyQuery =
                        session.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName=:name AND p.scope=:scope");
                policyQuery.setParameter("name", locPolicyId);
                policyQuery.setParameter("scope", scope);
            } else {
                locPolicyId = String.valueOf(policyIdVar);
                policyQuery = session.getNamedQuery("PolicyEntity.FindById");
                policyQuery.setParameter("id", locPolicyId);
            }
            List<?> policyQueryList;
            try {
                policyQueryList = policyQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to get policy with policyQuery.getResultList()");
                throw new PersistenceException("Query failed trying to get policy " + locPolicyId);
            }

            if (policyQueryList.isEmpty()) {
                PolicyLogger.error("Policy does not exist with id " + locPolicyId);
                throw new PersistenceException("Group policy is being added to does not exist with id " + locPolicyId);
            } else if (policyQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.DUP_POLICYID + locPolicyId + PolicyDbDao.FOUND_IN_DB);
                throw new PersistenceException(PolicyDbDao.DUP_POLICYID + locPolicyId + PolicyDbDao.FOUND_IN_DB);
            }
            return (PolicyEntity) policyQueryList.get(0);
        }
    }

    @Override
    public GroupEntity getGroup(long groupKey) {
        logger.debug("getGroup(int groupKey) as getGroup(" + groupKey + BRACKET_CALLED);
        if (groupKey < 0) {
            throw new IllegalArgumentException("groupKey must be at least 0");
        }
        synchronized (emLock) {
            checkBeforeOperationRun(true);
            // check if group exists
            Query groupQuery = session.createQuery("SELECT g FROM GroupEntity g WHERE g.groupKey=:groupKey");
            groupQuery.setParameter("groupKey", groupKey);
            List<?> groupQueryList;
            try {
                groupQueryList = groupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to get group with groupQuery.getResultList()");
                throw new PersistenceException(PolicyDbDao.QUERY_FAILED_GET_GROUP + groupKey);
            }
            if (groupQueryList.isEmpty()) {
                PolicyLogger.error("Group does not exist with groupKey " + groupKey);
                throw new PersistenceException("Group does not exist with groupKey " + groupKey);
            } else if (groupQueryList.size() > 1) {
                PolicyLogger
                        .error("Somehow, more than one group with the groupKey " + groupKey + PolicyDbDao.FOUND_IN_DB);
                throw new PersistenceException(
                        "Somehow, more than one group with the groupKey " + groupKey + PolicyDbDao.FOUND_IN_DB);
            }
            return (GroupEntity) groupQueryList.get(0);
        }
    }

    @Override
    public GroupEntity getGroup(String groupId) {
        logger.debug("getGroup(String groupId) as getGroup(" + groupId + BRACKET_CALLED);
        if (PolicyDbDao.isNullOrEmpty(groupId)) {
            throw new IllegalArgumentException("groupId must not be null or empty");
        }
        synchronized (emLock) {
            checkBeforeOperationRun(true);
            // check if group exists
            Query groupQuery = session.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId");
            groupQuery.setParameter(PolicyDbDao.GROUP_ID, groupId);
            List<?> groupQueryList;
            try {
                groupQueryList = groupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to get group with groupQuery.getResultList()");
                throw new PersistenceException(PolicyDbDao.QUERY_FAILED_GET_GROUP + groupId);
            }
            if (groupQueryList.isEmpty()) {
                PolicyLogger.error("Group does not exist with id " + groupId);
                throw new PersistenceException("Group does not exist with id " + groupId);
            } else if (groupQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.DUPLICATE_GROUPID + groupId + PolicyDbDao.FOUND_IN_DB);
                throw new PersistenceException(PolicyDbDao.DUPLICATE_GROUPID + groupId + PolicyDbDao.FOUND_IN_DB);
            }
            return (GroupEntity) groupQueryList.get(0);
        }
    }

    @Override
    public List<?> getPdpsInGroup(long groupKey) {
        logger.debug("getPdpsInGroup(int groupKey) as getPdpsInGroup(" + groupKey + BRACKET_CALLED);
        if (groupKey < 0) {
            throw new IllegalArgumentException("groupId must not be < 0");
        }
        synchronized (emLock) {
            checkBeforeOperationRun(true);
            Query pdpsQuery = session.createQuery("SELECT p FROM PdpEntity p WHERE p.groupEntity=:group");
            pdpsQuery.setParameter(GROUP, getGroup(groupKey));
            return pdpsQuery.list();
        }
    }

    @Override
    public PdpEntity getPdp(long pdpKey) {
        logger.debug("getPdp(int pdpKey) as getPdp(" + pdpKey + BRACKET_CALLED);
        if (pdpKey < 0) {
            throw new IllegalArgumentException("pdpKey must be at least 0");
        }
        synchronized (emLock) {
            checkBeforeOperationRun(true);
            // check if group exists
            Query pdpQuery = session.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpKey=:pdpKey");
            pdpQuery.setParameter("pdpKey", pdpKey);
            List<?> pdpQueryList;
            try {
                pdpQueryList = pdpQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to get pdp with pdpQuery.getResultList()");
                throw new PersistenceException("Query failed trying to get pdp " + pdpKey);
            }
            if (pdpQueryList.isEmpty()) {
                PolicyLogger.error("Pdp does not exist with pdpKey " + pdpKey);
                throw new PersistenceException("Pdp does not exist with pdpKey " + pdpKey);
            } else if (pdpQueryList.size() > 1) {
                PolicyLogger.error("Somehow, more than one pdp with the pdpKey " + pdpKey + PolicyDbDao.FOUND_IN_DB);
                throw new PersistenceException(
                        "Somehow, more than one pdp with the pdpKey " + pdpKey + PolicyDbDao.FOUND_IN_DB);
            }
            return (PdpEntity) pdpQueryList.get(0);
        }
    }

    @Override
    public boolean isTransactionOpen() {
        logger.debug("isTransactionOpen() as isTransactionOpen() called");
        synchronized (emLock) {
            return session.isOpen() && session.getTransaction().isActive();
        }
    }

    private String processConfigPath(String inputConfigPath) {
        String configPath = inputConfigPath;
        String webappsPath = XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_WEBAPPS);
        if (webappsPath == null) {
            logger.error("Webapps property does not exist");
            throw new IllegalArgumentException("Webapps property does not exist");
        }
        configPath = configPath.replace("$URL", webappsPath);
        // make sure the correct slashes are in
        try {
            configPath = Paths.get(configPath).toString();
        } catch (InvalidPathException e) {
            logger.error("Invalid config path: " + configPath, e);
            throw new IllegalArgumentException("Invalid config path: " + configPath);
        }
        return configPath;
    }

    private String readConfigFile(String configPath) {
        String configDataString = null;
        InputStream configContentStream = null;
        try {
            configContentStream = new FileInputStream(configPath);
            configDataString = IOUtils.toString(configContentStream);
        } catch (FileNotFoundException e) {
            logger.error("Caught FileNotFoundException on new FileInputStream(" + configPath + ")", e);
            throw new IllegalArgumentException("The config file path does not exist");
        } catch (IOException e2) {
            logger.error("Caught IOException on newIOUtils.toString(" + configContentStream + ")", e2);
            throw new IllegalArgumentException("The config file path cannot be read");
        } finally {
            IOUtils.closeQuietly(configContentStream);
        }
        if (configDataString == null) {
            throw new IllegalArgumentException("The config file path cannot be read");
        }
        return configDataString;
    }

    @Override
    public void close() {
        synchronized (emLock) {
            if (session.isOpen()) {
                if (session.getTransaction().isActive()) {
                    session.getTransaction().rollback();
                }
                session.close();
            }
            if (transactionTimer != null) {
                transactionTimer.interrupt();
            }
        }
    }

    @Override
    public void createGroup(String groupId, String groupName, String inputGroupDescription, String username) {
        String groupDescription = inputGroupDescription;
        logger.debug("deletePolicy(String policyToDeletes) as createGroup(" + groupId + ", " + groupName + ", "
                + groupDescription + BRACKET_CALLED);
        if (PolicyDbDao.isNullOrEmpty(groupId, groupName, username)) {
            throw new IllegalArgumentException("groupId, groupName, and username must not be null or empty");
        }
        if (groupDescription == null) {
            groupDescription = "";
        }

        synchronized (emLock) {
            checkBeforeOperationRun();
            Query checkGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            checkGroupQuery.setParameter(PolicyDbDao.GROUP_ID, groupId);
            checkGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> checkGroupQueryList;
            try {
                checkGroupQueryList = checkGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception on checkGroupQuery.getResultList()");
                throw new PersistenceException(PolicyDbDao.QUERY_FAILED_FOR_GROUP);
            }
            if (!checkGroupQueryList.isEmpty()) {
                PolicyLogger.error("The group being added already exists with id " + groupId);
                throw new PersistenceException("The group being added already exists with id " + groupId);
            }
            GroupEntity newGroup = new GroupEntity();
            newGroup.setCreatedBy(username);
            newGroup.setModifiedBy(username);
            newGroup.setGroupName(groupName);
            newGroup.setGroupId(groupId);
            newGroup.setDescription(groupDescription);
            if (isJunit) {
                newGroup.prePersist();
            }
            session.persist(newGroup);
            session.flush();
            this.groupId = newGroup.getGroupKey();
        }
    }

    @Override
    public void updateGroup(OnapPDPGroup group, String requestType, String username) {
        logger.info("PolicyDBDao: updateGroup(PDPGroup group) as updateGroup(" + group + "," + requestType + ","
                + username + BRACKET_CALLED);
        if (group == null) {
            throw new IllegalArgumentException("PDPGroup group must not be null");
        }
        if (PolicyDbDao.isNullOrEmpty(group.getId(), requestType)) {
            throw new IllegalArgumentException("group.getId() and username must not be null or empty");
        }

        synchronized (emLock) {
            PolicyDbDao policyDbDaoVar = new PolicyDbDao();
            checkBeforeOperationRun();
            Query getGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            getGroupQuery.setParameter(PolicyDbDao.GROUP_ID, group.getId());
            getGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> getGroupQueryList;
            try {
                getGroupQueryList = getGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception on getGroupQuery.getResultList()");
                throw new PersistenceException(PolicyDbDao.QUERY_FAILED_GET_GROUP + group.getId() + " for editing");
            }
            if (getGroupQueryList.isEmpty()) {
                PolicyLogger.error("The group cannot be found to update with id " + group.getId());
                throw new PersistenceException("The group cannot be found to update with id " + group.getId());
            } else if (getGroupQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.DUPLICATE_GROUPID + group.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
                throw new PersistenceException(
                        PolicyDbDao.DUPLICATE_GROUPID + group.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
            }
            GroupEntity groupToUpdateInDb = (GroupEntity) getGroupQueryList.get(0);
            if (!PolicyDbDao.stringEquals(groupToUpdateInDb.getModifiedBy(), requestType)) {
                groupToUpdateInDb.setModifiedBy(requestType);
            }
            if (group.getDescription() != null
                    && !PolicyDbDao.stringEquals(group.getDescription(), groupToUpdateInDb.getDescription())) {
                groupToUpdateInDb.setDescription(group.getDescription());
            }
            // let's find out what policies have been deleted
            StdPDPGroup oldGroup = null;
            try {
                oldGroup = (StdPDPGroup) PolicyDbDao.getPolicyDbDaoInstance().getPapEngine().getGroup(group.getId());
            } catch (PAPException e1) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, PolicyDbDao.POLICYDBDAO_VAR,
                        "We cannot get the group from the papEngine to delete policies");
            }
            if (oldGroup == null) {
                PolicyLogger.error("We cannot get the group from the papEngine to delete policies");
            } else {
                Set<String> newPolicySet = new HashSet<>(group.getPolicies().size());
                // a multiple of n runtime is faster than n^2, so I am using a
                // hashset to do the comparison
                for (PDPPolicy pol : group.getPolicies()) {
                    newPolicySet.add(pol.getId());
                }
                for (PDPPolicy pol : oldGroup.getPolicies()) {
                    // should be fast since getPolicies uses a HashSet in
                    // StdPDPGroup
                    if (!newPolicySet.contains(pol.getId())) {
                        String[] scopeAndName = policyDbDaoVar.getNameScopeAndVersionFromPdpPolicy(pol.getId());
                        PolicyEntity policyToDelete = null;
                        try {
                            if (scopeAndName != null) {
                                policyToDelete = getPolicy(scopeAndName[0], scopeAndName[1]);
                                if ("XACMLPapServlet.doDelete".equals(requestType)) {
                                    Iterator<PolicyEntity> dbPolicyIt = groupToUpdateInDb.getPolicies().iterator();
                                    String policyName = policyDbDaoVar.getPolicyNameAndVersionFromPolicyFileName(
                                            policyToDelete.getPolicyName())[0];

                                    logger.info("PolicyDBDao: delete policy from GroupEntity");
                                    try {
                                        while (dbPolicyIt.hasNext()) {
                                            PolicyEntity dbpolicy = dbPolicyIt.next();
                                            if (policyToDelete.getScope().equals(dbpolicy.getScope())
                                                    && policyDbDaoVar.getPolicyNameAndVersionFromPolicyFileName(
                                                            dbpolicy.getPolicyName())[0].equals(policyName)) {
                                                dbPolicyIt.remove();
                                                auditPdpOperations(username,
                                                        dbpolicy.getScope() + "." + dbpolicy.getPolicyName(), "Delete");
                                                logger.info("PolicyDBDao: deleting policy from the existing group:\n "
                                                        + "policyName is " + policyToDelete.getScope() + "."
                                                        + policyToDelete.getPolicyName() + "\n" + "group is "
                                                        + groupToUpdateInDb.getGroupId());
                                            }
                                        }
                                    } catch (Exception e) {
                                        logger.debug(e);
                                        PolicyLogger.error("Could not delete policy with name: "
                                                + policyToDelete.getScope() + "." + policyToDelete.getPolicyName()
                                                + "\n ID: " + policyToDelete.getPolicyId());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                    "Could not get policy to remove: " + pol.getId());
                            throw new PersistenceException("Could not get policy to remove: " + pol.getId());
                        }
                    }
                }
            }

            if (group.getName() != null
                    && !PolicyDbDao.stringEquals(group.getName(), groupToUpdateInDb.getGroupName())) {
                // we need to check if the new id exists in the database
                String newGrpId = PolicyDbDao.createNewPdpGroupId(group.getName());
                Query checkGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
                checkGroupQuery.setParameter(PolicyDbDao.GROUP_ID, newGrpId);
                checkGroupQuery.setParameter(PolicyDbDao.DELETED, false);
                List<?> checkGroupQueryList;
                try {
                    checkGroupQueryList = checkGroupQuery.list();
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                            "Caught Exception on checkGroupQuery.getResultList()");
                    throw new PersistenceException(PolicyDbDao.QUERY_FAILED_FOR_GROUP);
                }
                if (!checkGroupQueryList.isEmpty()) {
                    PolicyLogger.error("The new group name already exists, group id " + newGrpId);
                    throw new PersistenceException("The new group name already exists, group id " + newGrpId);
                }
                groupToUpdateInDb.setGroupId(newGrpId);
                groupToUpdateInDb.setGroupName(group.getName());
                this.newGroupId = group.getId();
            }
            session.flush();
            this.groupId = groupToUpdateInDb.getGroupKey();
        }
    }

    @Override
    public void addPdpToGroup(String pdpId, String groupIdVar, String pdpName, String pdpDescription, int pdpJmxPort,
            String username) {
        logger.debug("addPdpToGroup(String pdpID, String groupID, String pdpName, "
                + "String pdpDescription, int pdpJmxPort, String username) as addPdpToGroup(" + pdpId + ", "
                + groupIdVar + ", " + pdpName + ", " + pdpDescription + ", " + pdpJmxPort + ", " + username
                + BRACKET_CALLED);
        if (PolicyDbDao.isNullOrEmpty(pdpId, groupIdVar, pdpName, username)) {
            throw new IllegalArgumentException("pdpID, groupID, pdpName, and username must not be null or empty");
        }
        synchronized (emLock) {
            checkBeforeOperationRun();
            Query checkGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            checkGroupQuery.setParameter(PolicyDbDao.GROUP_ID, groupIdVar);
            checkGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> checkGroupQueryList;
            try {
                checkGroupQueryList = checkGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to check for existing group on checkGroupQuery.getResultList()");
                throw new PersistenceException(PolicyDbDao.QUERY_FAILED_FOR_GROUP);
            }
            if (checkGroupQueryList.size() != 1) {
                PolicyLogger.error("The group does not exist");
                throw new PersistenceException("The group does not exist");
            }
            Query checkDuplicateQuery = session.createQuery(PolicyDbDao.PDPENTITY_SELECT);
            checkDuplicateQuery.setParameter(PolicyDbDao.PDP_ID, pdpId);
            checkDuplicateQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> checkDuplicateList;
            try {
                checkDuplicateList = checkDuplicateQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to check for duplicate PDP " + pdpId
                                + " on checkDuplicateQuery.getResultList()");
                throw new PersistenceException("Query failed trying to check for duplicate PDP " + pdpId);
            }
            PdpEntity newPdp;
            if (!checkDuplicateList.isEmpty()) {
                logger.warn("PDP already exists with id " + pdpId);
                newPdp = (PdpEntity) checkDuplicateList.get(0);
            } else {
                newPdp = new PdpEntity();
            }

            newPdp.setCreatedBy(username);
            newPdp.setDeleted(false);
            newPdp.setDescription(pdpDescription);
            newPdp.setGroup((GroupEntity) checkGroupQueryList.get(0));
            newPdp.setJmxPort(pdpJmxPort);
            newPdp.setModifiedBy(username);
            newPdp.setPdpId(pdpId);
            newPdp.setPdpName(pdpName);
            if (isJunit) {
                newPdp.prePersist();
            }
            session.persist(newPdp);
            session.flush();
            this.pdpId = newPdp.getPdpKey();
        }
    }

    @Override
    public void updatePdp(OnapPDP pdp, String username) {
        logger.debug("updatePdp(PDP pdp, String username) as updatePdp(" + pdp + "," + username + BRACKET_CALLED);
        if (pdp == null) {
            throw new IllegalArgumentException("PDP pdp must not be null");
        }
        if (PolicyDbDao.isNullOrEmpty(pdp.getId(), username)) {
            throw new IllegalArgumentException("pdp.getId() and username must not be null or empty");
        }

        synchronized (emLock) {
            checkBeforeOperationRun();
            Query getPdpQuery = session.createQuery(PolicyDbDao.PDPENTITY_SELECT);
            getPdpQuery.setParameter(PolicyDbDao.PDP_ID, pdp.getId());
            getPdpQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> getPdpQueryList;
            try {
                getPdpQueryList = getPdpQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception on getPdpQuery.getResultList()");
                throw new PersistenceException("Query failed trying to get PDP " + pdp.getId());
            }
            if (getPdpQueryList.isEmpty()) {
                PolicyLogger.error("The pdp cannot be found to update with id " + pdp.getId());
                throw new PersistenceException("The pdp cannot be found to update with id " + pdp.getId());
            } else if (getPdpQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.MORE_THAN_ONE_PDP + pdp.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
                throw new PersistenceException(
                        PolicyDbDao.MORE_THAN_ONE_PDP + pdp.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
            }
            PdpEntity pdpToUpdate = (PdpEntity) getPdpQueryList.get(0);
            if (!PolicyDbDao.stringEquals(pdpToUpdate.getModifiedBy(), username)) {
                pdpToUpdate.setModifiedBy(username);
            }
            if (pdp.getDescription() != null
                    && !PolicyDbDao.stringEquals(pdp.getDescription(), pdpToUpdate.getDescription())) {
                pdpToUpdate.setDescription(pdp.getDescription());
            }
            if (pdp.getName() != null && !PolicyDbDao.stringEquals(pdp.getName(), pdpToUpdate.getPdpName())) {
                pdpToUpdate.setPdpName(pdp.getName());
            }
            if (pdp.getJmxPort() != null && !pdp.getJmxPort().equals(pdpToUpdate.getJmxPort())) {
                pdpToUpdate.setJmxPort(pdp.getJmxPort());
            }

            session.flush();
            this.pdpId = pdpToUpdate.getPdpKey();
        }
    }

    @Override
    public void movePdp(OnapPDP pdp, OnapPDPGroup group, String username) {
        logger.debug("movePdp(PDP pdp, PDPGroup group, String username) as movePdp(" + pdp + "," + group + ","
                + username + BRACKET_CALLED);
        if (pdp == null || group == null) {
            throw new IllegalArgumentException("PDP pdp and PDPGroup group must not be null");
        }
        if (PolicyDbDao.isNullOrEmpty(username, pdp.getId(), group.getId())) {
            throw new IllegalArgumentException("pdp.getId(), group.getId(), and username must not be null or empty");
        }

        synchronized (emLock) {
            checkBeforeOperationRun();
            // check if pdp exists
            Query getPdpQuery = session.createQuery(PolicyDbDao.PDPENTITY_SELECT);
            getPdpQuery.setParameter(PolicyDbDao.PDP_ID, pdp.getId());
            getPdpQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> getPdpQueryList;
            try {
                getPdpQueryList = getPdpQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception on getPdpQuery.getResultList()");
                throw new PersistenceException("Query failed trying to get pdp to move with id " + pdp.getId());
            }
            if (getPdpQueryList.isEmpty()) {
                PolicyLogger.error("The pdp cannot be found to move with id " + pdp.getId());
                throw new PersistenceException("The pdp cannot be found to move with id " + pdp.getId());
            } else if (getPdpQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.MORE_THAN_ONE_PDP + pdp.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
                throw new PersistenceException(
                        PolicyDbDao.MORE_THAN_ONE_PDP + pdp.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
            }

            // check if new group exists
            Query checkGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            checkGroupQuery.setParameter(PolicyDbDao.GROUP_ID, group.getId());
            checkGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> checkGroupQueryList;
            try {
                checkGroupQueryList = checkGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to get group on checkGroupQuery.getResultList()");
                throw new PersistenceException("Query failed trying to get new group " + group.getId());
            }
            if (checkGroupQueryList.size() != 1) {
                PolicyLogger.error("The group " + group.getId() + " does not exist");
                throw new PersistenceException("The group " + group.getId() + " does not exist");
            }
            GroupEntity groupToMoveInto = (GroupEntity) checkGroupQueryList.get(0);
            PdpEntity pdpToUpdate = (PdpEntity) getPdpQueryList.get(0);
            pdpToUpdate.setGroup(groupToMoveInto);
            if (!PolicyDbDao.stringEquals(pdpToUpdate.getModifiedBy(), username)) {
                pdpToUpdate.setModifiedBy(username);
            }

            session.flush();
            this.pdpId = pdpToUpdate.getPdpKey();
        }
    }

    @Override
    public void changeDefaultGroup(OnapPDPGroup group, String username) {
        logger.debug("changeDefaultGroup(PDPGroup group, String username) as changeDefaultGroup(" + group + ","
                + username + BRACKET_CALLED);
        if (group == null) {
            throw new IllegalArgumentException("PDPGroup group must not be null");
        }
        if (PolicyDbDao.isNullOrEmpty(group.getId(), username)) {
            throw new IllegalArgumentException("group.getId() and username must not be null or empty");
        }

        synchronized (emLock) {
            checkBeforeOperationRun();
            Query getGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            getGroupQuery.setParameter(PolicyDbDao.GROUP_ID, group.getId());
            getGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> getGroupQueryList;
            try {
                getGroupQueryList = getGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception on getGroupQuery.getResultList()");
                throw new PersistenceException(PolicyDbDao.QUERY_FAILED_GET_GROUP + group.getId());
            }
            if (getGroupQueryList.isEmpty()) {
                PolicyLogger.error("The group cannot be found to set default with id " + group.getId());
                throw new PersistenceException("The group cannot be found to set default with id " + group.getId());
            } else if (getGroupQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.DUPLICATE_GROUPID + group.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
                throw new PersistenceException(
                        PolicyDbDao.DUPLICATE_GROUPID + group.getId() + PolicyDbDao.DELETED_STATUS_FOUND);
            }
            GroupEntity newDefaultGroup = (GroupEntity) getGroupQueryList.get(0);
            newDefaultGroup.setDefaultGroup(true);
            if (!PolicyDbDao.stringEquals(newDefaultGroup.getModifiedBy(), username)) {
                newDefaultGroup.setModifiedBy(username);
            }

            session.flush();
            this.groupId = newDefaultGroup.getGroupKey();
            Query setAllGroupsNotDefault = session.createQuery("UPDATE GroupEntity g SET g.defaultGroup=:defaultGroup "
                    + "WHERE g.deleted=:deleted AND g.groupKey<>:groupKey");
            // not going to set modified by for all groups
            setAllGroupsNotDefault.setParameter("defaultGroup", false);
            setAllGroupsNotDefault.setParameter(PolicyDbDao.DELETED, false);
            setAllGroupsNotDefault.setParameter("groupKey", newDefaultGroup.getGroupKey());
            try {
                logger.info("set " + setAllGroupsNotDefault.executeUpdate() + " groups as not default");
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception on setAllGroupsNotDefault.executeUpdate()");
                throw new PersistenceException("Could not set all other groups default to false");
            }
            session.flush();
        }
    }

    @Override
    public void deleteGroup(OnapPDPGroup group, OnapPDPGroup moveToGroup, String username) throws PolicyDbException {
        logger.debug("deleteGroup(PDPGroup group, PDPGroup moveToGroup, String username) as deleteGroup(" + group + ", "
                + moveToGroup + "," + username + BRACKET_CALLED);
        if (group == null) {
            throw new IllegalArgumentException("PDPGroup group cannot be null");
        }
        if (PolicyDbDao.isNullOrEmpty(username, group.getId())) {
            throw new IllegalArgumentException("group.getId() and and username must not be null or empty");
        }

        if (group.isDefaultGroup()) {
            PolicyLogger.error("The default group " + group.getId() + " was attempted to be deleted. It cannot be.");
            throw new PolicyDbException("You cannot delete the default group.");
        }
        synchronized (emLock) {
            checkBeforeOperationRun();
            Query deleteGroupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            deleteGroupQuery.setParameter(PolicyDbDao.GROUP_ID, group.getId());
            deleteGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> deleteGroupQueryList;
            try {
                deleteGroupQueryList = deleteGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to check if group exists deleteGroupQuery.getResultList()");
                throw new PersistenceException("Query failed trying to check if group exists");
            }
            if (deleteGroupQueryList.isEmpty()) {
                logger.warn(PolicyDbDao.GROUP_NOT_FOUND + group.getId());
                return;
            } else if (deleteGroupQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.DUPLICATE_GROUPID + group.getId() + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
                throw new PersistenceException(
                        PolicyDbDao.DUPLICATE_GROUPID + group.getId() + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
            }

            Query pdpsInGroupQuery =
                    session.createQuery("SELECT p FROM PdpEntity p WHERE p.groupEntity=:group and p.deleted=:deleted");
            pdpsInGroupQuery.setParameter(GROUP, (deleteGroupQueryList.get(0)));
            pdpsInGroupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> pdpsInGroupList;
            try {
                pdpsInGroupList = pdpsInGroupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to get PDPs in group on pdpsInGroupQuery.getResultList()");
                throw new PersistenceException("Query failed trying to get PDPs in group");
            }
            if (!pdpsInGroupList.isEmpty()) {
                if (moveToGroup != null) {
                    Query checkMoveToGroupQuery = session
                            .createQuery("SELECT o FROM GroupEntity o WHERE o.groupId=:groupId AND o.deleted=:deleted");
                    checkMoveToGroupQuery.setParameter(PolicyDbDao.GROUP_ID, moveToGroup.getId());
                    checkMoveToGroupQuery.setParameter(PolicyDbDao.DELETED, false);
                    List<?> checkMoveToGroupList;
                    try {
                        checkMoveToGroupList = checkMoveToGroupQuery.list();
                    } catch (Exception e) {
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                "Caught Exception trying to check if group exists checkMoveToGroupQuery.getResultList");
                        throw new PersistenceException("Query failed trying to check if group exists");
                    }
                    if (checkMoveToGroupList.isEmpty()) {
                        PolicyLogger.error(PolicyDbDao.GROUP_NOT_FOUND + moveToGroup.getId());
                        throw new PersistenceException(PolicyDbDao.GROUP_NOT_FOUND + moveToGroup.getId());
                    } else if (checkMoveToGroupList.size() > 1) {
                        PolicyLogger.error(
                                PolicyDbDao.DUPLICATE_GROUPID + moveToGroup.getId() + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
                        throw new PersistenceException(
                                PolicyDbDao.DUPLICATE_GROUPID + moveToGroup.getId() + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
                    } else {
                        GroupEntity newGroup = (GroupEntity) checkMoveToGroupList.get(0);
                        for (Object pdpObject : pdpsInGroupList) {
                            PdpEntity pdp = (PdpEntity) pdpObject;
                            pdp.setGroup(newGroup);
                            if (!PolicyDbDao.stringEquals(pdp.getModifiedBy(), username)) {
                                pdp.setModifiedBy(username);
                            }
                            try {
                                session.flush();
                                this.newGroupId = newGroup.getGroupId();
                            } catch (PersistenceException e) {
                                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                                        "Caught PersistenceException trying to set pdp group to null on em.flush()");
                                throw new PersistenceException("Query failed trying to set pdp group to ");
                            }
                        }
                    }
                } else {
                    PolicyLogger.error("Group " + group.getId()
                            + " is trying to be delted with PDPs. No group was provided to move them to");
                    throw new PolicyDbException("Group has PDPs. Must provide a group for them to move to");
                }
            }

            // delete group here
            GroupEntity groupToDelete = (GroupEntity) deleteGroupQueryList.get(0);
            groupToDelete.setDeleted(true);
            if (!PolicyDbDao.stringEquals(groupToDelete.getModifiedBy(), username)) {
                groupToDelete.setModifiedBy(username);
            }
            session.flush();
            this.groupId = groupToDelete.getGroupKey();
        }
    }

    @Override
    public StdPDPGroup addPolicyToGroup(String groupIdVar, String policyIdVar, String requestType, String username)
            throws PolicyDbException {
        logger.info(
                "PolicyDBDao: addPolicyToGroup(String groupID, String policyID, String username) as addPolicyToGroup("
                        + groupIdVar + ", " + policyIdVar + "," + requestType + "," + username + BRACKET_CALLED);
        if (PolicyDbDao.isNullOrEmpty(groupIdVar, policyIdVar, requestType)) {
            throw new IllegalArgumentException("groupID, policyID, and username must not be null or empty");
        }
        synchronized (emLock) {
            checkBeforeOperationRun();
            // check if group exists
            Query groupQuery = session.createQuery(PolicyDbDao.GROUPENTITY_SELECT);
            groupQuery.setParameter(PolicyDbDao.GROUP_ID, groupIdVar);
            groupQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> groupQueryList;
            try {
                groupQueryList = groupQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to check if group exists groupQuery.getResultList()");
                throw new PersistenceException("Query failed trying to check if group " + groupIdVar + EXISTS);
            }
            if (groupQueryList.isEmpty()) {
                PolicyLogger.error("Group policy is being added to does not exist with id " + groupIdVar);
                throw new PersistenceException("Group policy is being added to does not exist with id " + groupIdVar);
            } else if (groupQueryList.size() > 1) {
                PolicyLogger.error(PolicyDbDao.DUPLICATE_GROUPID + groupIdVar + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
                throw new PersistenceException(
                        PolicyDbDao.DUPLICATE_GROUPID + groupIdVar + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
            }

            // we need to convert the form of the policy id that is used groups
            // into the form that is used
            // for the database. (com.Config_mypol.1.xml) to (Config_mypol.xml)
            PolicyDbDao policyDbDao = new PolicyDbDao();
            String[] policyNameScopeAndVersion = policyDbDao.getNameScopeAndVersionFromPdpPolicy(policyIdVar);
            if (policyNameScopeAndVersion == null) {
                throw new IllegalArgumentException("Invalid input - policyID must contain name, scope and version");
            }
            Query policyQuery = session.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName=:policyName "
                    + "AND p.scope=:scope AND p.deleted=:deleted");
            policyQuery.setParameter("policyName", policyNameScopeAndVersion[0]);
            policyQuery.setParameter(PolicyDbDao.SCOPE, policyNameScopeAndVersion[1]);
            policyQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> policyQueryList;
            try {
                policyQueryList = policyQuery.list();
            } catch (Exception e) {
                logger.debug(e);
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to check if policy exists policyQuery.getResultList()");
                throw new PersistenceException(
                        "Query failed trying to check if policy " + policyNameScopeAndVersion[0] + EXISTS);
            }
            if (policyQueryList.isEmpty()) {
                PolicyLogger.error("Policy being added to the group does not exist with policy id "
                        + policyNameScopeAndVersion[0]);
                throw new PersistenceException("Policy being added to the group does not exist with policy id "
                        + policyNameScopeAndVersion[0]);
            } else if (policyQueryList.size() > 1) {
                PolicyLogger.error(
                        PolicyDbDao.DUP_POLICYID + policyNameScopeAndVersion[0] + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
                throw new PersistenceException(
                        PolicyDbDao.DUPLICATE_GROUPID + policyNameScopeAndVersion[0] + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
            }
            logger.info("PolicyDBDao: Getting group and policy from database");
            GroupEntity group = (GroupEntity) groupQueryList.get(0);
            PolicyEntity policy = (PolicyEntity) policyQueryList.get(0);
            Iterator<PolicyEntity> policyIt = group.getPolicies().iterator();
            String policyName = policyDbDao.getPolicyNameAndVersionFromPolicyFileName(policy.getPolicyName())[0];

            logger.info("PolicyDBDao: policyName retrieved is " + policyName);
            try {
                while (policyIt.hasNext()) {
                    PolicyEntity pol = policyIt.next();
                    if (policy.getScope().equals(pol.getScope())
                            && policyDbDao.getPolicyNameAndVersionFromPolicyFileName(pol.getPolicyName())[0]
                                    .equals(policyName)) {
                        policyIt.remove();
                    }
                }
            } catch (Exception e) {
                logger.debug(e);
                PolicyLogger.error("Could not delete old versions for policy " + policy.getPolicyName() + ", ID: "
                        + policy.getPolicyId());
            }
            group.addPolicyToGroup(policy);
            auditPdpOperations(username, policy.getScope() + "." + policy.getPolicyName(), "Push");
            session.flush();

            // After adding policy to the db group we need to make sure the
            // filesytem group is in sync with the db group
            try {
                StdPDPGroup pdpGroup =
                        (StdPDPGroup) PolicyDbDao.getPolicyDbDaoInstance().getPapEngine().getGroup(group.getGroupId());
                return policyDbDao.synchronizeGroupPoliciesInFileSystem(pdpGroup, group);
            } catch (PAPException e) {
                logger.debug(e);
                PolicyLogger.error("PolicyDBDao: Could not synchronize the filesystem group with the database group. "
                        + e.getMessage());
            }
            return null;
        }
    }

    // this means delete pdp not just remove from group
    @Override
    public void removePdpFromGroup(String pdpId, String username) {
        logger.debug("removePdpFromGroup(String pdpID, String username) as removePdpFromGroup(" + pdpId + "," + username
                + BRACKET_CALLED);
        if (PolicyDbDao.isNullOrEmpty(pdpId, username)) {
            throw new IllegalArgumentException("pdpID and username must not be null or empty");
        }
        synchronized (emLock) {
            checkBeforeOperationRun();
            Query pdpQuery = session.createQuery(PolicyDbDao.PDPENTITY_SELECT);
            pdpQuery.setParameter(PolicyDbDao.PDP_ID, pdpId);
            pdpQuery.setParameter(PolicyDbDao.DELETED, false);
            List<?> pdpList;
            try {
                pdpList = pdpQuery.list();
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDbDao.POLICYDBDAO_VAR,
                        "Caught Exception trying to check if pdp exists  pdpQuery.getResultList()");
                throw new PersistenceException("Query failed trying to check if pdp " + pdpId + EXISTS);
            }
            if (pdpList.size() > 1) {
                PolicyLogger.error("Somehow, more than one pdp with the id " + pdpId + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
                throw new PersistenceException(
                        "Somehow, more than one pdp with the id " + pdpId + PolicyDbDao.FOUND_IN_DB_NOT_DEL);
            } else if (pdpList.isEmpty()) {
                PolicyLogger.error("Pdp being removed does not exist with id " + pdpId);
                return;
            }
            PdpEntity pdp = (PdpEntity) pdpList.get(0);
            if (!isJunit) {
                pdp.setGroup(null);
            }

            if (!PolicyDbDao.stringEquals(pdp.getModifiedBy(), username)) {
                pdp.setModifiedBy(username);
            }
            pdp.setDeleted(true);

            session.flush();
            this.pdpId = pdp.getPdpKey();
        }
    }

    private static String evaluateXPath(String expression, String xml) {
        InputSource source = new InputSource(new StringReader(xml));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String description = "";
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(source);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            description = xpath.evaluate(expression, document);
        } catch (Exception e) {
            logger.error("Exception Occured while evaluating path" + e);
        }
        return description;
    }

    public static boolean isJunit() {
        return isJunit;
    }

    public static void setJunit(boolean isJunit) {
        PolicyDbDaoTransactionInstance.isJunit = isJunit;
    }

    /**
     * Audit pdp operations.
     *
     * @param username the username
     * @param policyID the policy ID
     * @param action the action
     */
    public void auditPdpOperations(String username, String policyID, String action) {
        PolicyAuditlog log = new PolicyAuditlog();
        log.setUserName(username);
        log.setActions(action);
        log.setPolicyName(policyID);
        log.setDateAndTime(new Date());
        session.save(log);
    }
}
