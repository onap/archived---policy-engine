/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.att.research.xacml.api.pap.PAPException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PushPolicyController {
    private static final Logger LOGGER = FlexLogger.getLogger(PushPolicyController.class);

    private static CommonClassDao commonClassDao;
    private static String policyNames = "policyName";
    private static String errorMsg = "error";
    private static String operation = "operation";
    private static String messageContent = "message";

    private static final String REGEX = "[0-9a-zA-Z._ ]*";

    @Autowired
    public PushPolicyController(CommonClassDao commonClassDao) {
        PushPolicyController.commonClassDao = commonClassDao;
    }

    public void setCommonClassDao(CommonClassDao commonClassDao) {
        PushPolicyController.commonClassDao = commonClassDao;
    }

    /*
     * This is an empty constructor
     */
    public PushPolicyController() {
    }

    @RequestMapping(value = "/pushPolicy", method = RequestMethod.POST)
    public void pushPolicy(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode root = mapper.readTree(request.getInputStream());
            String policyScope = root.get("policyScope").asText();
            String filePrefix = root.get("filePrefix").asText();
            String policyName = root.get(policyNames).asText();
            String pdpGroup = root.get("pdpGroup").asText();
            String requestID = request.getHeader("X-ECOMP-RequestID");
            if (requestID == null) {
                requestID = UUID.randomUUID().toString();
                LOGGER.info("No request ID provided, sending generated ID: " + requestID);
            }
            LOGGER.info("Push policy Request to get the selectedPolicy : " + root.asText());
            String policyVersionName = policyScope.replace(".", File.separator) + File.separator
                    + filePrefix + policyName;
            List<?> policyVersionObject =
                    commonClassDao.getDataById(PolicyVersion.class, policyNames, policyVersionName);
            if (policyVersionObject != null) {
                PolicyVersion policyVersion = (PolicyVersion) policyVersionObject.get(0);
                String policyID = policyVersionName.replace(File.separator, "."); // This is before adding version.
                policyVersionName += "." + policyVersion.getActiveVersion() + ".xml";
                addPolicyToGroup(policyScope, policyID, policyVersionName.replace(File.separator, "."), pdpGroup,
                        response);
            } else {
                String message = "Unknown Policy '" + policyName + "'";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                response.addHeader(errorMsg, "unknownPolicy");
                response.addHeader(operation, "push");
                response.addHeader(messageContent, message);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NullPointerException | IOException e) {
            LOGGER.error(e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.addHeader(errorMsg, "unknown");
            response.addHeader(operation, "push");
        }
    }

    private void addPolicyToGroup(String policyScope, String policyID, String policyName, String pdpGroup,
                                  HttpServletResponse response) {
        StdPDPGroup selectedPDPGroup = null;
        StdPDPPolicy selectedPolicy = null;
        //Get the selected PDP Group to push the policy
        try {
            selectedPDPGroup = (StdPDPGroup) XACMLPapServlet.getPAPEngine().getGroup(pdpGroup);
        } catch (PAPException e1) {
            PolicyLogger.error(e1);
        }
        if (selectedPDPGroup == null) {
            String message = "Unknown groupId '" + selectedPDPGroup + "'";
            if (!message.matches(REGEX)) {
                message = "Unknown groupId";
            }
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
            response.addHeader(errorMsg, "unknownGroupId");
            response.addHeader(operation, "push");
            response.addHeader(messageContent, message);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //Get PolicyEntity from DB;
        EntityManager em = XACMLPapServlet.getEmf().createEntityManager();
        Query createPolicyQuery =
                em.createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:policyName");
        createPolicyQuery.setParameter("scope", policyScope);
        createPolicyQuery.setParameter(policyNames, policyName.substring(policyScope.length() + 1));
        List<?> createPolicyQueryList = createPolicyQuery.getResultList();
        PolicyEntity policyEntity = null;
        if (!createPolicyQueryList.isEmpty()) {
            policyEntity = (PolicyEntity) createPolicyQueryList.get(0);
        } else {
            PolicyLogger
                    .error("Somehow, more than one policy with the same scope, name, and deleted status were found in" +
                            " the database");
            String message = "Unknown Policy '" + policyName + "'";
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
            response.addHeader(errorMsg, "unknownPolicy");
            response.addHeader(operation, "push");
            response.addHeader(messageContent, message);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File temp = new File(policyName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(policyEntity.getPolicyData());
            URI selectedURI = temp.toURI();
            // Create the policy Object
            selectedPolicy = new StdPDPPolicy(policyName, true, policyID, selectedURI);
        } catch (IOException e) {
            LOGGER.error("Unable to get policy '" + policyName + "': " + e.getMessage(), e);
        }
        try {
            new ObjectOutputStream(response.getOutputStream()).writeObject(selectedPolicy);
        } catch (IOException e) {
            LOGGER.error(e);
            response.addHeader(errorMsg, "policyCopyError");
            response.addHeader(messageContent, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        response.addHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.addHeader(operation, "push");
        response.addHeader("policyId", policyName);
        // TODO : Check point to push policies within PAP.
    }
}
