/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.persistence.PersistenceException;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.dao.PolicyDBException;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandleIncomingNotifications {

    private static final Logger logger = FlexLogger.getLogger(HandleIncomingNotifications.class);

    private static final String POLICY_NOTIFICATION = "policy";
    private static final String PDP_NOTIFICATION = "pdp";
    private static final String GROUP_NOTIFICATION = "group";
    public static final String JSON_CONFIG = "JSON";
    public static final String XML_CONFIG = "XML";
    public static final String PROPERTIES_CONFIG = "PROPERTIES";
    public static final String OTHER_CONFIG = "OTHER";
    public static final String AUDIT_USER = "audit";


    private static SessionFactory sessionfactory;

    @Autowired
    public HandleIncomingNotifications(SessionFactory sessionfactory) {
        HandleIncomingNotifications.sessionfactory = sessionfactory;
    }

    public HandleIncomingNotifications() {
        // Default Constructor
    }

    public void handleIncomingHttpNotification(String url, String entityId, String entityType, String extraData,
            XACMLPapServlet xacmlPapServlet) {
        logger.info("DBDao url: " + url + " has reported an update on " + entityType + " entity " + entityId);
        PolicyDBDaoTransaction transaction = PolicyDBDao.getPolicyDBDaoInstance().getNewTransaction();
        // although its named retries, this is the total number of tries
        int retries;
        try {
            retries = Integer
                    .parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_INCOMINGNOTIFICATION_TRIES));
        } catch (Exception e) {
            logger.error("xacml.rest.pap.incomingnotification.tries property not set, using a default of 3." + e);
            retries = 3;
        }
        // if someone sets it to some dumb value, we need to make sure it will
        // try at least once
        if (retries < 1) {
            retries = 1;
        }
        int pauseBetweenRetries = 1000;
        switch (entityType) {

            case POLICY_NOTIFICATION:
                for (int i = 0; i < retries; i++) {
                    try {
                        handleIncomingPolicyChange(entityId);
                        break;
                    } catch (Exception e) {
                        logger.debug(e);
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                                "Caught exception on handleIncomingPolicyChange(" + url + ", " + entityId + ", "
                                        + extraData + ")");
                    }
                    try {
                        Thread.sleep(pauseBetweenRetries);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                break;
            case PDP_NOTIFICATION:
                for (int i = 0; i < retries; i++) {
                    try {
                        handleIncomingPdpChange(entityId, transaction);
                        break;
                    } catch (Exception e) {
                        logger.debug(e);
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                                "Caught exception on handleIncomingPdpChange(" + url + ", " + entityId + ", "
                                        + transaction + ")");
                    }
                    try {
                        Thread.sleep(pauseBetweenRetries);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                break;
            case GROUP_NOTIFICATION:
                for (int i = 0; i < retries; i++) {
                    try {
                        handleIncomingGroupChange(entityId, extraData, transaction);
                        break;
                    } catch (Exception e) {
                        logger.debug(e);
                        PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                                "Caught exception on handleIncomingGroupChange(" + url + ", " + entityId + ", "
                                        + extraData + ", " + transaction + ", " + xacmlPapServlet + ")");
                    }
                    try {
                        Thread.sleep(pauseBetweenRetries);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                break;
        }
        // no changes should be being made in this function, we still need to
        // close
        transaction.rollbackTransaction();
    }

    private void handleIncomingGroupChange(String groupId, String extraData, PolicyDBDaoTransaction transaction)
            throws PAPException, PolicyDBException {
        GroupEntity groupRecord = null;
        long groupIdLong = -1;
        try {
            groupIdLong = Long.parseLong(groupId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("groupId " + groupId + " cannot be parsed into a long");
        }
        try {
            groupRecord = transaction.getGroup(groupIdLong);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                    "Caught Exception trying to get pdp group record with transaction.getGroup(" + groupIdLong + ");");
            throw new PAPException("Could not get local group " + groupIdLong);
        }
        if (groupRecord == null) {
            throw new PersistenceException("The group record returned is null");
        }
        // compare to local fs
        // does group folder exist
        OnapPDPGroup localGroup = null;
        try {
            localGroup = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getGroup(groupRecord.getGroupId());
        } catch (Exception e) {
            logger.warn("Caught PAPException trying to get local pdp group with papEngine.getGroup(" + groupId + ");",
                    e);
        }
        if (localGroup == null && extraData != null) {
            // here we can try to load an old group id from the extraData
            try {
                localGroup = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getGroup(extraData);
            } catch (Exception e) {
                logger.warn(
                        "Caught PAPException trying to get local pdp group with papEngine.getGroup(" + extraData + ");",
                        e);
            }
        }
        if (localGroup != null && groupRecord.isDeleted()) {
            OnapPDPGroup newLocalGroup = null;
            if (extraData != null) {
                try {
                    newLocalGroup = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getGroup(extraData);
                } catch (PAPException e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to get new pdp group with papEngine.getGroup(" + extraData
                                    + ");");
                }
            }
            try {
                PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().removeGroup(localGroup, newLocalGroup);
            } catch (NullPointerException | PAPException e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                        "Caught PAPException trying to get remove pdp group with papEngine.removeGroup(" + localGroup
                                + ", " + newLocalGroup + ");");
                throw new PAPException("Could not remove group " + groupId);
            }
        } else if (localGroup == null) {
            // creating a new group
            try {
                PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().newGroup(groupRecord.getgroupName(),
                        groupRecord.getDescription());
            } catch (NullPointerException | PAPException e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                        "Caught PAPException trying to create pdp group with papEngine.newGroup(groupRecord.getgroupName(), groupRecord.getDescription());");
                throw new PAPException("Could not create group " + groupRecord);
            }
            try {
                localGroup = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getGroup(groupRecord.getGroupId());
            } catch (PAPException e1) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, PolicyDBDao.POLICYDBDAO_VAR,
                        "Caught PAPException trying to get pdp group we just created with papEngine.getGroup(groupRecord.getGroupId());\nAny PDPs or policies in the new group may not have been added");
                return;
            }
            // add possible pdps to group
            List<?> pdpsInGroup = transaction.getPdpsInGroup(Long.parseLong(groupRecord.getGroupId()));
            for (Object pdpO : pdpsInGroup) {
                PdpEntity pdp = (PdpEntity) pdpO;
                try {
                    PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().newPDP(pdp.getPdpId(), localGroup,
                            pdp.getPdpName(), pdp.getDescription(), pdp.getJmxPort());
                } catch (NullPointerException | PAPException e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to get create pdp with papEngine.newPDP(pdp.getPdpId(), localGroup, pdp.getPdpName(), pdp.getDescription(), pdp.getJmxPort());");
                    throw new PAPException("Could not create pdp " + pdp);
                }
            }
            // add possible policies to group (file system only, apparently)
        } else {
            if (!(localGroup instanceof StdPDPGroup)) {
                throw new PAPException("group is not a StdPDPGroup");
            }
            // clone the object
            // because it will be comparing the new group to its own version
            StdPDPGroup localGroupClone = new StdPDPGroup(localGroup.getId(), localGroup.isDefaultGroup(),
                    localGroup.getName(), localGroup.getDescription(), ((StdPDPGroup) localGroup).getDirectory());
            localGroupClone.setOnapPdps(localGroup.getOnapPdps());
            localGroupClone.setPipConfigs(localGroup.getPipConfigs());
            localGroupClone.setStatus(localGroup.getStatus());
            // we are updating a group or adding a policy or changing default
            // set default if it should be
            if (!localGroupClone.isDefaultGroup() && groupRecord.isDefaultGroup()) {
                try {
                    PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().setDefaultGroup(localGroup);
                    return;
                } catch (PAPException e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to set default group with papEngine.SetDefaultGroup("
                                    + localGroupClone + ");");
                    throw new PAPException("Could not set default group to " + localGroupClone);
                }
            }
            boolean needToUpdate = false;
            if (updateGroupPoliciesInFileSystem(localGroupClone, localGroup, groupRecord, transaction)) {
                needToUpdate = true;
            }
            if (!PolicyDBDao.stringEquals(localGroupClone.getId(), groupRecord.getGroupId())
                    || !PolicyDBDao.stringEquals(localGroupClone.getName(), groupRecord.getgroupName())) {
                // changing ids
                // we do not want to change the id, the papEngine will do this
                // for us, it needs to know the old id
                localGroupClone.setName(groupRecord.getgroupName());
                needToUpdate = true;
            }
            if (!PolicyDBDao.stringEquals(localGroupClone.getDescription(), groupRecord.getDescription())) {
                localGroupClone.setDescription(groupRecord.getDescription());
                needToUpdate = true;
            }
            if (needToUpdate) {
                try {
                    PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().updateGroup(localGroupClone);
                } catch (PAPException e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to update group with papEngine.updateGroup(" + localGroupClone
                                    + ");");
                    throw new PAPException("Could not update group " + localGroupClone);
                }
            }
        }
    }

    // this will also handle removes, since incoming pdpGroup has no policies
    // internally, we are just going to add them all in from the db
    private boolean updateGroupPoliciesInFileSystem(OnapPDPGroup pdpGroup, OnapPDPGroup oldPdpGroup,
            GroupEntity groupRecord, PolicyDBDaoTransaction transaction) throws PAPException, PolicyDBException {
        if (!(pdpGroup instanceof StdPDPGroup)) {
            throw new PAPException("group is not a StdPDPGroup");
        }
        StdPDPGroup group = (StdPDPGroup) pdpGroup;
        // this must always be true since we don't explicitly know when a delete
        // is occuring
        boolean didUpdate = true;
        HashMap<String, PDPPolicy> currentPolicySet = new HashMap<>(oldPdpGroup.getPolicies().size());
        HashSet<PDPPolicy> newPolicySet = new HashSet<>();
        for (PDPPolicy pdpPolicy : oldPdpGroup.getPolicies()) {
            currentPolicySet.put(pdpPolicy.getId(), pdpPolicy);
        }
        for (PolicyEntity policy : groupRecord.getPolicies()) {
            String pdpPolicyName =
                    PolicyDBDao.getPolicyDBDaoInstance().getPdpPolicyName(policy.getPolicyName(), policy.getScope());
            if (group.getPolicy(pdpPolicyName) == null) {
                didUpdate = true;
                if (currentPolicySet.containsKey(pdpPolicyName)) {
                    newPolicySet.add(currentPolicySet.get(pdpPolicyName));
                } else {
                    logger.info(
                            "PolicyDBDao: Adding the new policy to the PDP group after notification: " + pdpPolicyName);
                    InputStream policyStream = new ByteArrayInputStream(policy.getPolicyData().getBytes());
                    group.copyPolicyToFile(pdpPolicyName, policyStream);
                    ((StdPDPPolicy) (group.getPolicy(pdpPolicyName))).setName(PolicyDBDao.getPolicyDBDaoInstance()
                            .removeExtensionAndVersionFromPolicyName(pdpPolicyName));
                    try {
                        policyStream.close();
                    } catch (IOException e) {
                        didUpdate = false;
                        PolicyLogger.error(e.getMessage() + e);
                    }
                }
            }
        }
        logger.info("PolicyDBDao: Adding updated policies to group after notification.");
        if (didUpdate) {
            newPolicySet.addAll(group.getPolicies());
            group.setPolicies(newPolicySet);
        }
        return didUpdate;
    }

    private void handleIncomingPdpChange(String pdpId, PolicyDBDaoTransaction transaction) throws PAPException {
        // get pdp
        long pdpIdLong = -1;
        try {
            pdpIdLong = Long.parseLong(pdpId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("pdpId " + pdpId + " cannot be parsed into a long");
        }
        PdpEntity pdpRecord = null;
        try {
            pdpRecord = transaction.getPdp(pdpIdLong);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                    "Caught Exception trying to get pdp record with transaction.getPdp(" + pdpIdLong + ");");
            throw new PAPException("Could not get local pdp " + pdpIdLong);
        }
        if (pdpRecord == null) {
            throw new PersistenceException("The pdpRecord returned is null");
        }
        OnapPDP localPdp = null;
        try {
            localPdp = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getPDP(pdpRecord.getPdpId());
        } catch (PAPException e) {
            logger.warn("Caught PAPException trying to get local pdp  with papEngine.getPDP(" + pdpId + ");", e);
        }
        if (localPdp != null && pdpRecord.isDeleted()) {
            try {
                PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().removePDP(localPdp);
            } catch (PAPException e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                        "Caught PAPException trying to get remove pdp with papEngine.removePDP(" + localPdp + ");");
                throw new PAPException("Could not remove pdp " + pdpId);
            }
        } else if (localPdp == null) {
            // add new pdp
            // get group
            OnapPDPGroup localGroup = null;
            try {
                localGroup =
                        PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getGroup(pdpRecord.getGroup().getGroupId());
            } catch (PAPException e1) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, PolicyDBDao.POLICYDBDAO_VAR,
                        "Caught PAPException trying to get local group to add pdp to with papEngine.getGroup(pdpRecord.getGroup().getGroupId());");
                throw new PAPException("Could not get local group");
            }
            try {
                PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().newPDP(pdpRecord.getPdpId(), localGroup,
                        pdpRecord.getPdpName(), pdpRecord.getDescription(), pdpRecord.getJmxPort());
            } catch (NullPointerException | PAPException e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                        "Caught PAPException trying to create pdp with papEngine.newPDP(" + pdpRecord.getPdpId() + ", "
                                + localGroup + ", " + pdpRecord.getPdpName() + ", " + pdpRecord.getDescription() + ", "
                                + pdpRecord.getJmxPort() + ");");
                throw new PAPException("Could not create pdp " + pdpRecord);
            }
        } else {
            boolean needToUpdate = false;
            if (!PolicyDBDao.stringEquals(localPdp.getId(), pdpRecord.getPdpId())
                    || !PolicyDBDao.stringEquals(localPdp.getName(), pdpRecord.getPdpName())) {
                // again, we don't want to change the id, the papEngine will do
                // this
                localPdp.setName(pdpRecord.getPdpName());
                needToUpdate = true;
            }
            if (!PolicyDBDao.stringEquals(localPdp.getDescription(), pdpRecord.getDescription())) {
                localPdp.setDescription(pdpRecord.getDescription());
                needToUpdate = true;
            }
            String localPdpGroupId = null;
            try {
                localPdpGroupId = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().getPDPGroup(localPdp).getId();
            } catch (PAPException e) {
                // could be null or something, just warn at this point
                logger.warn(
                        "Caught PAPException trying to get id of local group that pdp is in with localPdpGroupId = papEngine.getPDPGroup(localPdp).getId();",
                        e);
            }
            if (!PolicyDBDao.stringEquals(localPdpGroupId, pdpRecord.getGroup().getGroupId())) {
                OnapPDPGroup newPdpGroup = null;
                try {
                    newPdpGroup = PolicyDBDao.getPolicyDBDaoInstance().getPapEngine()
                            .getGroup(pdpRecord.getGroup().getGroupId());
                } catch (PAPException e) {
                    // ok, now we have an issue. Time to stop things
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to get id of local group to move pdp to with papEngine.getGroup(pdpRecord.getGroup().getGroupId());");
                    throw new PAPException("Could not get local group");
                }
                try {
                    PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().movePDP(localPdp, newPdpGroup);
                } catch (PAPException e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to move pdp with papEngine.movePDP(localPdp, newPdpGroup);");
                    throw new PAPException("Could not move pdp " + localPdp);
                }
            }
            if (localPdp.getJmxPort() != pdpRecord.getJmxPort()) {
                localPdp.setJmxPort(pdpRecord.getJmxPort());
                needToUpdate = true;
            }
            if (needToUpdate) {
                try {
                    PolicyDBDao.getPolicyDBDaoInstance().getPapEngine().updatePDP(localPdp);
                } catch (PAPException e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                            "Caught PAPException trying to update pdp with papEngine.updatePdp(" + localPdp + ");");
                    throw new PAPException("Could not update pdp " + localPdp);
                }
            }
        }
        // compare to local situation
        // call command to update
    }

    private void handleIncomingPolicyChange(String policyId) {
        String policyName = null;
        Session session = sessionfactory.openSession();
        Query getPolicyEntityQuery = session.getNamedQuery("PolicyEntity.FindById");
        getPolicyEntityQuery.setParameter("id", Long.valueOf(policyId));

        @SuppressWarnings("unchecked")
        List<PolicyEntity> policies = getPolicyEntityQuery.list();
        PolicyEntity policy = null;
        if (!policies.isEmpty()) {
            policy = policies.get(0);
        }
        String action = "unknown action";
        try {
            if (policy != null) {
                policyName = policy.getPolicyName();
                logger.info("Deleting old Policy Config File for " + policy.getPolicyName());
                action = "delete";
                Path subFile = null;

                if (policy.getConfigurationData() != null) {
                    subFile =
                            getPolicySubFile(policy.getConfigurationData().getConfigurationName(), PolicyDBDao.CONFIG);
                } else if (policy.getActionBodyEntity() != null) {
                    subFile = getPolicySubFile(policy.getActionBodyEntity().getActionBodyName(), PolicyDBDao.ACTION);
                }

                if (subFile != null) {
                    Files.deleteIfExists(subFile);
                }
                if (policy.getConfigurationData() != null) {
                    writePolicySubFile(policy, PolicyDBDao.CONFIG);
                } else if (policy.getActionBodyEntity() != null) {
                    writePolicySubFile(policy, action);
                }
            }
        } catch (IOException e1) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, PolicyDBDao.POLICYDBDAO_VAR,
                    "Error occurred while performing [" + action + "] of Policy File: " + policyName);
        } finally {
            session.close();
        }
    }

    private boolean writePolicySubFile(PolicyEntity policy, String policyType) {
        logger.info("writePolicySubFile with policyName[" + policy.getPolicyName() + "] and policyType[" + policyType
                + "]");
        String type = null;
        String subTypeName = null;
        String subTypeBody = null;
        if (PolicyDBDao.CONFIG.equalsIgnoreCase(policyType)) {
            type = PolicyDBDao.CONFIG;
            subTypeName = FilenameUtils.removeExtension(policy.getConfigurationData().getConfigurationName());
            subTypeBody = policy.getConfigurationData().getConfigBody();

            String configType = policy.getConfigurationData().getConfigType();

            if (configType != null) {
                if (configType.equals(JSON_CONFIG)) {
                    subTypeName = subTypeName + ".json";
                }
                if (configType.equals(XML_CONFIG)) {
                    subTypeName = subTypeName + ".xml";
                }
                if (configType.equals(PROPERTIES_CONFIG)) {
                    subTypeName = subTypeName + ".properties";
                }
                if (configType.equals(OTHER_CONFIG)) {
                    subTypeName = subTypeName + ".txt";
                }
            }
        } else if (PolicyDBDao.ACTION.equalsIgnoreCase(policyType)) {
            type = PolicyDBDao.ACTION;
            subTypeName = policy.getActionBodyEntity().getActionBodyName();
            subTypeBody = policy.getActionBodyEntity().getActionBody();
        }
        Path filePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS).toString(), type);

        if (subTypeBody == null) {
            subTypeBody = "";
        }
        boolean success = false;
        try {
            Files.deleteIfExists(Paths.get(filePath.toString(), subTypeName));
            File file = Paths.get(filePath.toString(), subTypeName).toFile();
            boolean value = file.createNewFile();
            logger.debug("New file created successfully" + value);
            try (FileWriter fileWriter = new FileWriter(file, false)) {
                // false to overwrite
                fileWriter.write(subTypeBody);
                fileWriter.close();
                success = true;
            }
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, PolicyDBDao.POLICYDBDAO_VAR,
                    "Exception occured while creating Configuration File for Policy : " + policy.getPolicyName());
        }
        return success;
    }

    private Path getPolicySubFile(String inputFileName, String subFileType) {
        String filename = inputFileName;
        logger.info("getPolicySubFile(" + filename + ", " + subFileType + ")");
        Path filePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS), subFileType);
        File file = null;

        filename = FilenameUtils.removeExtension(filename);

        for (File tmpFile : filePath.toFile().listFiles()) {
            if (FilenameUtils.removeExtension(tmpFile.getName()).equals(filename)) {
                file = tmpFile;
            }
        }

        Path finalPath = null;
        if (file != null) {
            finalPath = Paths.get(file.getAbsolutePath());
        }

        logger.info("end of getPolicySubFile: " + finalPath);
        return finalPath;
    }
}
