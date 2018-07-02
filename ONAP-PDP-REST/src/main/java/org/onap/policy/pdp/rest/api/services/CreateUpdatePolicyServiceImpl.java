/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.util.PolicyValidation;
import org.onap.policy.rest.util.PolicyValidationRequestWrapper;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

import com.google.common.base.Strings;

public class CreateUpdatePolicyServiceImpl implements CreateUpdatePolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(CreateUpdatePolicyServiceImpl.class.getName());

    private String policyResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private Boolean updateFlag = false;
    private String message = null;
    private PolicyParameters policyParameters = new PolicyParameters();
    private String policyName = null;
    private String policyScope = null;
    private String date = null;

    public CreateUpdatePolicyServiceImpl(PolicyParameters policyParameters, String requestID, boolean updateFlag) {
        this.updateFlag = updateFlag;
        this.policyParameters = policyParameters;
        if (policyParameters.getRequestID() == null) {
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(), e);
                }
            } else {
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.policyParameters.setRequestID(requestUUID);
        }
        try {
            run();
            specialCheck();
        } catch (PolicyException e) {
            policyResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    public void run() throws PolicyException {
        // Check Validation.
        if (!getValidation()) {
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Get Result.
        try {
            status = HttpStatus.OK;
            policyResult = processResult();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    public String processResult() throws PolicyException {
        String response = null;
        PolicyService policyService = null;
        if (policyParameters.getPolicyConfigType() != null) {
            // This is a Config Policy.
            switch (policyParameters.getPolicyConfigType()) {
                case BRMS_PARAM:
                    policyService = new BRMSParamPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case BRMS_RAW:
                    policyService = new BRMSRawPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case Base:
                    policyService = new ConfigPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case ClosedLoop_Fault:
                    policyService = new ClosedLoopFaultPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case ClosedLoop_PM:
                    policyService = new ClosedLoopPMPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case Firewall:
                    policyService = new FirewallPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case MicroService:
                    policyService = new MicroServicesPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case Optimization:
                    policyService = new OptimizationPolicyService(policyName, policyScope, policyParameters, date);
                    break;
                case Extended:
                    response = extendedOptions();
                    break;
                default:
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Invalid Config Type Present";
                    LOGGER.error(message);
                    status = HttpStatus.BAD_REQUEST;
                    response = message;
            }
        } else if (policyParameters.getPolicyClass() != null) {
            switch (policyParameters.getPolicyClass()) {
                case Action:
                    policyService = new ActionPolicyService(policyScope, policyName, policyParameters);
                    break;
                case Decision:
                    policyService = new DecisionPolicyService(policyScope, policyName, policyParameters);
                    break;
                default:
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Invalid Config Type Present";
                    LOGGER.error(message);
                    status = HttpStatus.BAD_REQUEST;
                    response = message;
            }
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Class found.";
            LOGGER.error(message);
            status = HttpStatus.BAD_REQUEST;
            response = message;
        }

        // execute the service
        if (policyService != null) {
        	// Check Validation.

        	if (!"Raw".equalsIgnoreCase(policyParameters.getRuleProvider().toString()) && !policyService.getValidation()) {
        		LOGGER.error(policyService.getMessage());
        		status = HttpStatus.BAD_REQUEST;
        		return policyService.getMessage();
        	}

        	// Get Result.
        	response = policyService.getResult(updateFlag);
        }
        return response;
    }

    protected String extendedOptions() throws PolicyException {
        message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Extended Option Found.";
        LOGGER.error(message);
        status = HttpStatus.BAD_REQUEST;
        return message;
    }

    protected boolean getValidation() {

        PolicyValidation validation = new PolicyValidation();

        StringBuilder responseString;

        if (policyParameters == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy parameters given. ";
            return false;
        }


        if (!Strings.isNullOrEmpty(policyParameters.getPolicyName())) {
            if (policyParameters.getPolicyName().contains(".")) {
                policyName = policyParameters.getPolicyName().substring(
                        policyParameters.getPolicyName().lastIndexOf('.') + 1,
                        policyParameters.getPolicyName().length());
                policyScope = policyParameters.getPolicyName().substring(0,
                        policyParameters.getPolicyName().lastIndexOf('.'));
                policyParameters.setPolicyName(policyName);
                LOGGER.info("Name is " + policyName + "   scope is " + policyScope);
            } else {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy Scope: No Policy Scope given";
                LOGGER.error("Common validation did not return success:  " + message);
                return false;
            }
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "PolicyName: PolicyName Should not be empty";
            LOGGER.error("Common validation did not return success:  " + message);
            return false;
        }

        if (policyParameters.getPolicyClass() != null
                && "Config".equals(policyParameters.getPolicyClass().toString())) {
            String policyConfigType = policyParameters.getPolicyConfigType().toString();
            if (!"BRMS_Param".equalsIgnoreCase(policyConfigType)
                    && Strings.isNullOrEmpty(policyParameters.getConfigBody())) {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "ConfigBody: No Config Body given";
                LOGGER.error("Common validation did not return success:  " + message);
                return false;
            }
        }

        try {
            PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
            PolicyRestAdapter policyData = wrapper.populateRequestParameters(policyParameters);
            if (policyData != null) {
                responseString = validation.validatePolicy(policyData);
            } else {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + " improper JSON object : "
                        + policyParameters.getConfigBody();
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Exception Occured during Policy Validation" + e);
            if (e.getMessage() != null) {
                if ("Action".equals(policyParameters.getPolicyClass().toString())
                        && e.getMessage().contains("Index:")) {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Rule Algorithms: One or more Fields in Rule Algorithms is Empty.";
                } else {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured During Policy Validation: " + e;
                }
            }
            return false;
        }


        // Set some default Values.
        if (policyParameters.getTtlDate() != null) {
            date = convertDate(policyParameters.getTtlDate());
        }

        if (responseString != null) {
            String response = responseString.toString().substring(0, 7);
            if ("success".equals(response)) {
                return true;
            } else {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + PolicyApiUtils.formatResponse(responseString);
                LOGGER.error("Common validation did not return success:  " + message);
                return false;
            }
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Unknown Error Occured During Policy Validation";
            LOGGER.error(message);
            return false;
        }

    }

    protected String convertDate(Date date) {
        String strDate = null;
        if (date != null) {
            SimpleDateFormat dateformatJava = new SimpleDateFormat("dd-MM-yyyy");
            strDate = dateformatJava.format(date);
        }
        return (strDate == null) ? "NA" : strDate;
    }

    protected void specialCheck() {
        if (policyResult == null || policyResult.contains("BAD REQUEST") || policyResult.contains("PE300")) {
            status = HttpStatus.BAD_REQUEST;
        } else if (policyResult.contains("Policy Exist Error")) {
            status = HttpStatus.CONFLICT;
        } else if (policyResult.contains("PE200") || policyResult.contains("PE900")) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public String getResult() {
        return policyResult;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

    public String getPolicyResult() {
        return policyResult;
    }

    public void setPolicyResult(String policyResult) {
        this.policyResult = policyResult;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Boolean getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Boolean updateFlag) {
        this.updateFlag = updateFlag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PolicyParameters getPolicyParameters() {
        return policyParameters;
    }

    public void setPolicyParameters(PolicyParameters policyParameters) {
        this.policyParameters = policyParameters;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyScope() {
        return policyScope;
    }

    public void setPolicyScope(String policyScope) {
        this.policyScope = policyScope;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
