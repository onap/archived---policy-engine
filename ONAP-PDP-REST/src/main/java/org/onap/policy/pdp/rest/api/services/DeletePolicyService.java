/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.pdp.rest.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyNameType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPAPPolicyParams;
import org.springframework.http.HttpStatus;


/**
 * The Class DeletePolicyService.
 */
public class DeletePolicyService {

    private static final Logger LOGGER = FlexLogger.getLogger(DeletePolicyService.class.getName());
    private static final String PRINT_REQUESTID = " - RequestId - ";
    private String deleteResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private DeletePolicyParameters deletePolicyParameters = null;
    private String message = null;
    private String requestId = null;
    private List<PolicyNameType> policyList = new ArrayList<>();
    protected String filePrefix = null;
    protected String clientScope = null;

    /**
     * Instantiates a new delete policy service.
     *
     * @param deletePolicyParameters the delete policy parameters
     * @param reqId the request id
     * @param mechid the mechid
     */
    public DeletePolicyService(final DeletePolicyParameters deletePolicyParameters, final String reqId) {
        this.deletePolicyParameters = deletePolicyParameters;
        populateRequestId(deletePolicyParameters, reqId);
        try {
            run();
            specialCheck();
        } catch (final PolicyException e) {
            LOGGER.error("Unable to process delete policy request for : " + this.deletePolicyParameters
                    + PRINT_REQUESTID + this.requestId);
            deleteResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    /**
     * Special check.
     */
    private void specialCheck() {
        if (deleteResult == null) {
            return;
        }
        if (deleteResult.contains("BAD REQUEST") || deleteResult.contains("PE300") || deleteResult.contains("PE200")
                || deleteResult.contains("not exist") || deleteResult.contains("Invalid policyName")) {
            if (deleteResult.contains("groupId")) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.BAD_REQUEST;
            }
        } else if (deleteResult.contains("locked down")) {
            status = HttpStatus.ACCEPTED;
        } else if (deleteResult.contains("not Authorized")) {
            status = HttpStatus.FORBIDDEN;
        } else if (deleteResult.contains("JPAUtils") || deleteResult.contains("database")
                || deleteResult.contains("policy file") || deleteResult.contains("unknown")
                || deleteResult.contains("configuration")) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Run.
     *
     * @throws PolicyException the policy exception
     */
    private void run() throws PolicyException {
        // Check Validation.
        if (!getValidation()) {
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Get Result.
        try {
            LOGGER.debug("Processing delete request:  " + deletePolicyParameters.toString() + PRINT_REQUESTID
                    + this.requestId);
            status = HttpStatus.OK;
            deleteResult = processResult();
        } catch (final Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    /**
     * Process result.
     *
     * @return the string
     * @throws PolicyException the policy exception
     */
    private String processResult() throws PolicyException {
        String response = null;
        String pdpGroup = deletePolicyParameters.getPdpGroup();

        final PAPServices papServices = new PAPServices();
        if (!populateFullPolicyName(papServices)) {
            LOGGER.error(message);
            return message;
        }

        if ("PAP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent())) {
            // for deleting policies from the API
            for (PolicyNameType policyData : policyList) {
                // validation ensures there will be one delete policy at a time
                final StdPAPPolicy deletePapPolicy =
                        new StdPAPPolicy(StdPAPPolicyParams.builder().policyName(policyData.getFullPolicyName())
                                .deleteCondition(deletePolicyParameters.getDeleteCondition().toString()).build());
                // send JSON object to PAP
                response = (String) papServices.callPAP(deletePapPolicy,
                        new String[] {"groupId=" + pdpGroup, "apiflag=deletePapApi", "operation=delete"},
                        deletePolicyParameters.getRequestID(), policyData.getClientScope());
            }
        } else if ("PDP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent())) {
            String policies =
                    policyList.stream().map(PolicyNameType::getFullPolicyName).collect(Collectors.joining(","));
            // send JSON object to PAP
            response =
                    (String) papServices.callPAP(null,
                            new String[] {"policyName=" + policies, "groupId=" + pdpGroup, "apiflag=deletePdpApi",
                                    "operation=delete", "userId=API"},
                            deletePolicyParameters.getRequestID(), clientScope);
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Component does not exist."
                    + "Please enter either PAP or PDP to delete the policy from a specified Policy Component.";
            LOGGER.warn(message + PRINT_REQUESTID + requestId);
            response = message;
        }
        return response;
    }

    /**
     * Populate full policy name.
     *
     * @param papServices the pap services
     * @return true, if successful
     */
    private boolean populateFullPolicyName(final PAPServices papServices) {
        for (PolicyNameType policyData : policyList) {
            if (policyData.getPolicyName().contains("xml")) {
                policyData.setFullPolicyName(policyData.getPolicyName());
                continue;
            }

            final String activeVersion = papServices.getActiveVersion(policyData.getPolicyScope(),
                    policyData.getFilePrefix(), policyData.getPolicyName(), policyData.getClientScope(),
                    deletePolicyParameters.getRequestID());

            if ("pe100".equalsIgnoreCase(activeVersion)) {
                message = XACMLErrorConstants.ERROR_PERMISSIONS
                        + "response code of the URL is 403. PEP is not Authorized for making this Request!! "
                        + "Contact Administrator for this Scope. ";
                LOGGER.warn(message + PRINT_REQUESTID + this.requestId);
                return false;
            } else if ("pe300".equalsIgnoreCase(activeVersion)) {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
                        + "This indicates a problem with getting the version"
                        + "from the PAP or the policy does not exist.";
                LOGGER.warn(message + PRINT_REQUESTID + this.requestId);
                return false;
            }
            if ("0".equalsIgnoreCase(activeVersion)) {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "could not retrieve the activeVersion for this policy."
                        + "This indicates the policy does not exist, please verify the policy exists.";
                LOGGER.warn(message + PRINT_REQUESTID + this.requestId);
                return false;
            }

            policyData.setFullPolicyName(policyData.getPolicyScope() + "." + filePrefix + policyData.getPolicyName()
                    + "." + activeVersion + ".xml");
        }
        return true;
    }

    /**
     * Gets the validation.
     *
     * @return the validation
     */
    private boolean getValidation() {
        if (StringUtils.isBlank(deletePolicyParameters.getPolicyName())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            return false;
        }

        String policyType = deletePolicyParameters.getPolicyType();
        if (StringUtils.isBlank(policyType)) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PolicyType given.";
            return false;
        }

        if (StringUtils.isBlank(deletePolicyParameters.getPolicyComponent())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Component given.";
            return false;
        }

        List<String> policyNames = Arrays.asList(deletePolicyParameters.getPolicyName().split(","));
        List<String> policyTypes = Arrays.asList(deletePolicyParameters.getPolicyType().split(","));

        if (policyTypes.size() > 1 && (policyNames.size() != policyTypes.size())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid number of PolicyTypes";
            return false;
        }

        if ((policyNames.stream().anyMatch(StringUtils::isBlank))
                || (policyTypes.stream().anyMatch(StringUtils::isBlank))) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid PolicyName or PolicyType";
            return false;
        }

        if ("PAP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent()) && policyNames.size() > 1) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Only one policy can be deleted from PAP at a time";
            return false;
        }

        if ("PAP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent())
                && (deletePolicyParameters.getDeleteCondition() == null
                        || deletePolicyParameters.getDeleteCondition().toString().trim().isEmpty())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Delete Condition given.";
            LOGGER.warn(message + PRINT_REQUESTID + requestId);
            return false;
        }

        if ("PDP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent())
                && StringUtils.isBlank(deletePolicyParameters.getPdpGroup())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PDP Group given.";
            LOGGER.warn(message + PRINT_REQUESTID + requestId);
            return false;
        }

        if (!validatePolicyNameAndScope(policyNames, policyTypes)) {
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Failed validation in validatePolicyNameAndScope - "
                    + message + PRINT_REQUESTID + requestId);
            return false;
        }

        return true;
    }

    /**
     * Validate policy name and scope.
     *
     * @param policyNames the policy names
     * @param policyTypes the policy types
     * @return true, if successful
     */
    private boolean validatePolicyNameAndScope(List<String> policyNames, List<String> policyTypes) {
        String polType = null;

        if (policyTypes.size() == 1) {
            polType = policyTypes.get(0);
        }

        try {
            for (int i = 0; i < policyNames.size(); i++) {
                String polName = policyNames.get(i);

                if (policyTypes.size() > 1) {
                    polType = policyTypes.get(i);
                }
                String policyName = null;
                String policyScope = null;
                if (polName.contains("xml")) {
                    policyName = polName;
                    policyScope = policyName.substring(0, StringUtils.lastOrdinalIndexOf(policyName, ".", 3));
                } else if (polName.contains(".")) {
                    policyName = polName.substring(polName.lastIndexOf('.') + 1, polName.length());
                    policyScope = polName.substring(0, polName.lastIndexOf('.'));
                } else {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given." + polName;
                    return false;
                }

                clientScope = null;
                filePrefix = null;
                setClientScope(polType.trim());
                if (StringUtils.isBlank(clientScope)) {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + deletePolicyParameters.getPolicyType()
                            + " is not a valid Policy Type.";
                    LOGGER.warn(message + PRINT_REQUESTID + this.requestId);
                    return false;
                }
                PolicyNameType policyData = new PolicyNameType(policyName.trim(), policyScope.trim(), polType.trim(),
                        filePrefix, clientScope);
                policyList.add(policyData);
                LOGGER.info("deletePolicy - RequestId - " + requestId + " , Policy - " + policyData);
            }
        } catch (Exception e) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "validatePolicies - Failed Validation. Please check the input parameters.";
            return false;
        }
        return true;
    }

    /**
     * Sets the client scope.
     *
     * @param policyType the new client scope
     */
    private void setClientScope(String policyType) {
        if ("Firewall".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigFirewall";
            filePrefix = "Config_FW_";
        } else if ("Action".equalsIgnoreCase(policyType)) {
            clientScope = "Action";
            filePrefix = "Action_";
        } else if ("Decision".equalsIgnoreCase(policyType)) {
            clientScope = "Decision";
            filePrefix = "Decision_";
        } else if ("Base".equalsIgnoreCase(policyType)) {
            clientScope = "Config";
            filePrefix = "Config_";
        } else if ("ClosedLoop_Fault".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigClosedLoop";
            filePrefix = "Config_Fault_";
        } else if ("ClosedLoop_PM".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigClosedLoop";
            filePrefix = "Config_PM_";
        } else if ("MicroService".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigMS";
            filePrefix = "Config_MS_";
        } else if ("Optimization".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigOptimization";
            filePrefix = "Config_OOF_";
        } else if ("BRMS_RAW".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigBrmsRaw";
            filePrefix = "Config_BRMS_Raw_";
        } else if ("BRMS_PARAM".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigBrmsParam";
            filePrefix = "Config_BRMS_Param_";
        } else {
            extendedClientScope(policyType);
        }
    }

    /**
     * Populate request id.
     *
     * @param deletePolicyParameters the delete policy parameters
     * @param reqId the request id
     */
    private void populateRequestId(final DeletePolicyParameters deletePolicyParameters, final String reqId) {
        if (deletePolicyParameters.getRequestID() == null) {
            UUID requestUuid = null;
            if (!StringUtils.isBlank(reqId)) {
                try {
                    requestUuid = UUID.fromString(reqId);
                } catch (final IllegalArgumentException e) {
                    requestUuid = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUuid.toString(), e);
                }
            } else {
                requestUuid = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUuid.toString());
            }
            this.deletePolicyParameters.setRequestID(requestUuid);
        }
        this.requestId = this.deletePolicyParameters.getRequestID().toString();
    }

    /**
     * Extended client scope.
     *
     * @param policyType the policy type
     */
    // Note: Don't remove this method, this one is used to extend policies.
    protected void extendedClientScope(String policyType) {
        clientScope = null;
        message = XACMLErrorConstants.ERROR_DATA_ISSUE + policyType + " is not a valid Policy Type.";
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public String getResult() {
        return deleteResult;
    }

    /**
     * Gets the response code.
     *
     * @return the response code
     */
    public HttpStatus getResponseCode() {
        return status;
    }
}
