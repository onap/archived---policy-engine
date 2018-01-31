/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.ConfigFirewallPolicyAPIRequest;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class CreateUpdateFirewallPolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(CreateUpdateFirewallPolicyService.class.getName());

    private String response = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public CreateUpdateFirewallPolicyService(
            ConfigFirewallPolicyAPIRequest configFirewallPolicyAPIRequest,
            String requestID, boolean updateFlag) {
        try{
            run(configFirewallPolicyAPIRequest, requestID, updateFlag);
        }catch(PolicyException e){
            response = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run(
            ConfigFirewallPolicyAPIRequest configFirewallPolicyAPIRequest,
            String requestID, boolean updateFlag) throws PolicyException{
        PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyClass(PolicyClass.Config);
        policyParameters.setPolicyConfigType(PolicyConfigType.Firewall);
        if(configFirewallPolicyAPIRequest.getPolicyScope()==null|| configFirewallPolicyAPIRequest.getPolicyScope().trim().isEmpty()){
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        if(configFirewallPolicyAPIRequest.getPolicyName()==null|| configFirewallPolicyAPIRequest.getPolicyName().trim().isEmpty()){
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        policyParameters.setPolicyName(configFirewallPolicyAPIRequest.getPolicyScope()+"."+configFirewallPolicyAPIRequest.getPolicyName());
        policyParameters.setConfigBody(configFirewallPolicyAPIRequest.getFirewallJson());
        policyParameters.setRiskLevel(configFirewallPolicyAPIRequest.getRiskLevel());
        policyParameters.setRiskType(configFirewallPolicyAPIRequest.getRiskType());
        policyParameters.setGuard(Boolean.parseBoolean(configFirewallPolicyAPIRequest.getGuard()));
        try {
            policyParameters.setTtlDate(new SimpleDateFormat("dd-MM-yyyy").parse(configFirewallPolicyAPIRequest.getTtlDate()));
        } catch (NullPointerException | ParseException e) {
            LOGGER.warn("Error Parsing date given " + configFirewallPolicyAPIRequest.getTtlDate(), e);
            policyParameters.setTtlDate(null);
        }
        CreateUpdatePolicyService createUpdatePolicyService = new CreateUpdatePolicyServiceImpl(policyParameters, requestID, updateFlag);
        status = createUpdatePolicyService.getResponseCode();
        response = createUpdatePolicyService.getResult();
    }

    public String getResult() {
        return response;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}