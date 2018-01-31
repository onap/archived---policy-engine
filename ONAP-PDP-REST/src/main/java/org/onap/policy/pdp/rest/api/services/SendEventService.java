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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.onap.policy.api.EventRequestParameters;
import org.onap.policy.api.PolicyEventException;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.PDPResponse;
import org.onap.policy.std.StdPolicyResponse;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class SendEventService {
    private static final Logger LOGGER = FlexLogger.getLogger(SendEventService.class.getName());

    private Collection<PolicyResponse> policyResponses = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private EventRequestParameters eventRequestParameters = null;
    private String message = null;

    public SendEventService(EventRequestParameters eventRequestParameters,
            String requestID) {
        this.eventRequestParameters=eventRequestParameters;
        if(eventRequestParameters.getRequestID()==null){
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(), e);
                }
            }else{
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.eventRequestParameters.setRequestID(requestUUID);
        }
        policyResponses = new ArrayList<>();
        try{
            run();
        }catch(PolicyEventException e){
            StdPolicyResponse policyResponse = new StdPolicyResponse();
            policyResponse.setPolicyResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE+e);
            policyResponse.setPolicyResponseStatus(PolicyResponseStatus.NO_ACTION_REQUIRED);
            policyResponses.add(policyResponse);
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyEventException{
        // getValidation.
        if(!getValidation()){
            LOGGER.error(message);
            throw new PolicyEventException(message);
        }
        // Generate Request.
        String modelString = getModel().toString();
        LOGGER.debug("Generated JSON Request is: " + modelString);
        // Process Result.
        try {
            PDPServices pdpServices = new PDPServices();
            status = HttpStatus.OK;
            policyResponses = eventResult(pdpServices.generateRequest(modelString, eventRequestParameters.getRequestID(),false, false));
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyEventException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
        }
    }

    private Collection<PolicyResponse> eventResult(
            Collection<PDPResponse> generateRequest) {
        Collection<PolicyResponse> result = new HashSet<>();
        if (generateRequest == null) {
            return result;
        }
        if (!generateRequest.isEmpty()) {
            for (PDPResponse stdStatus : generateRequest) {
                StdPolicyResponse policyResponse = new StdPolicyResponse();
                policyResponse.setActionAdvised(stdStatus.getActionAdvised());
                policyResponse.setActionTaken(stdStatus.getActionTaken());
                policyResponse.setPolicyResponseMessage(stdStatus.getPolicyResponseMessage());
                policyResponse.setPolicyResponseStatus(stdStatus.getPolicyResponseStatus());
                policyResponse.setRequestAttributes(eventRequestParameters.getEventAttributes());
                result.add(policyResponse);
            }
        }
        return result;
    }

    private JsonObject getModel() throws PolicyEventException{
        JsonArrayBuilder resourceArray = Json.createArrayBuilder();
        Map<String,String> eventAttributes = eventRequestParameters.getEventAttributes();
        for (Entry<String,String> key : eventAttributes.entrySet()) {
            if (key.getKey().isEmpty()) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an Empty Key";
                LOGGER.error(message);
                throw new PolicyEventException(message);
            }
            JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
            if (key.getValue().matches("[0-9]+")) {
                int val = Integer.parseInt(key.getValue());
                resourceBuilder.add("Value", val);
            } else {
                resourceBuilder.add("Value", key.getValue());
            }
            resourceBuilder.add("AttributeId", key.getKey());
            resourceArray.add(resourceBuilder);
        }
        return Json.createObjectBuilder()
                .add("Request", Json.createObjectBuilder()
                                .add("Resource",Json.createObjectBuilder()
                                                .add("Attribute",resourceArray)))
                .build();
    }

    private boolean getValidation() {
        if (eventRequestParameters == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No event Parameters Given. ";
            return false;
        }
        if (eventRequestParameters.getEventAttributes() == null || eventRequestParameters.getEventAttributes().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No event Attributes Given. ";
            return false;
        }
        return true;
    }

    public Collection<PolicyResponse> getResult() {
        return policyResponses;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
