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
public class DeletePolicyService extends PdpApiService {

    private static final Logger LOGGER = FlexLogger.getLogger(DeletePolicyService.class.getName());
    private DeletePolicyParameters deletePolicyParameters = null;
    private String result = null;
    private List<PolicyNameType> policyList = new ArrayList<>();


    /**
     * Instantiates a new delete policy service.
     *
     * @param deletePolicyParameters the delete policy parameters
     * @param reqId the request id
     */
    public DeletePolicyService(final DeletePolicyParameters deletePolicyParameters, final String reqId) {
        super();
        this.deletePolicyParameters = deletePolicyParameters;
        deletePolicyParameters.setRequestID(populateRequestId(deletePolicyParameters.getRequestID(), reqId));
        try {
            run();
            specialCheck();
        } catch (final PolicyException e) {
            LOGGER.error("Unable to process delete policy request for : " + this.deletePolicyParameters
                    + PRINT_REQUESTID + this.requestId);
            result = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    /**
     * Special check.
     */
    private void specialCheck() {
        if (result == null) {
            return;
        }
        if (result.contains("BAD REQUEST") || result.contains("PE300") || result.contains("PE200")
                || result.contains("not exist") || result.contains("Invalid policyName")) {
            if (result.contains("groupId")) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.BAD_REQUEST;
            }
        } else if (result.contains("locked down")) {
            status = HttpStatus.ACCEPTED;
        } else if (result.contains("not Authorized")) {
            status = HttpStatus.FORBIDDEN;
        } else if (result.contains("JPAUtils") || result.contains("database")
                || result.contains("policy file") || result.contains("unknown")
                || result.contains("configuration")) {
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
            result = processResult();
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
        	if (policyData.getPolicyName().endsWith("xml")) {
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

            policyData.setFullPolicyName(policyData.getPolicyScope() + "." + policyData.getFilePrefix()
                    + policyData.getPolicyName() + "." + activeVersion + ".xml");
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
                        || StringUtils.isBlank(deletePolicyParameters.getDeleteCondition().toString()))) {
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

        if (!validatePolicyNameAndScope(policyNames, policyTypes, policyList)) {
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Failed validation in validatePolicyNameAndScope - "
                    + message + PRINT_REQUESTID + requestId);
            return false;
        }

        return true;
    }

    public String getResult() {
        return result;
    }
}
