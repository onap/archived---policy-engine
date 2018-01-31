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
import java.util.HashMap;
import java.util.Map;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.ConfigPolicyAPIRequest;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class CreateUpdateConfigPolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(CreateUpdateConfigPolicyService.class.getName());

    private String response = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public CreateUpdateConfigPolicyService(
            ConfigPolicyAPIRequest configPolicyAPIRequest, String requestID,
            boolean updateFlag) {
        try{
            run(configPolicyAPIRequest, requestID, updateFlag);
        }catch(PolicyException e){
            response = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run(ConfigPolicyAPIRequest configPolicyAPIRequest,
            String requestID, boolean updateFlag) throws PolicyException{
        PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyClass(PolicyClass.Config);
        policyParameters.setPolicyConfigType(PolicyConfigType.Base);
        if(configPolicyAPIRequest.getPolicyScope()==null|| configPolicyAPIRequest.getPolicyScope().trim().isEmpty()){
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        if(configPolicyAPIRequest.getPolicyName()==null|| configPolicyAPIRequest.getPolicyName().trim().isEmpty()){
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        policyParameters.setPolicyName(configPolicyAPIRequest.getPolicyScope()+"."+configPolicyAPIRequest.getPolicyName());
        policyParameters.setPolicyDescription(configPolicyAPIRequest.getPolicyDescription());
        policyParameters.setOnapName(configPolicyAPIRequest.getOnapName());
        policyParameters.setConfigName(configPolicyAPIRequest.getConfigName());
        Map<AttributeType, Map<String, String>> attributes = new HashMap<>();
        attributes.put(AttributeType.MATCHING, configPolicyAPIRequest.getConfigAttributes());
        policyParameters.setAttributes(attributes);
        if(configPolicyAPIRequest.getConfigType()==null){
        	String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy ConfigType given.";
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        try{
        	policyParameters.setConfigBodyType(PolicyType.valueOf(configPolicyAPIRequest.getConfigType()));
        }catch(IllegalArgumentException e){
        	String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Improper ConfigType given.";
            LOGGER.error(message, e);
            throw new PolicyException(message);
        }
        policyParameters.setConfigBody(configPolicyAPIRequest.getBody());
        policyParameters.setRiskLevel(configPolicyAPIRequest.getRiskLevel());
        policyParameters.setRiskType(configPolicyAPIRequest.getRiskType());
        policyParameters.setGuard(Boolean.parseBoolean(configPolicyAPIRequest.getGuard()));
        if(configPolicyAPIRequest.getTtlDate()==null){
        	LOGGER.warn("No TTL date given ");
            policyParameters.setTtlDate(null);
        }else{
        	try {
                policyParameters.setTtlDate(new SimpleDateFormat("dd-MM-yyyy").parse(configPolicyAPIRequest.getTtlDate()));
            } catch (ParseException e) {
                LOGGER.warn("Error Parsing date given " + configPolicyAPIRequest.getTtlDate(), e);
                policyParameters.setTtlDate(null);
            }
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
