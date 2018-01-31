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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.PDPResponse;
import org.onap.policy.std.StdDecisionResponse;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class GetDecisionService {
    private static final Logger LOGGER = FlexLogger.getLogger(GetDecisionService.class.getName());

    private DecisionResponse decisionResponse = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private DecisionRequestParameters decisionRequestParameters = null;
    private String message = null;
    private String onapComponentName = null;
    private Map<String,String> decisionAttributes = null;

    public GetDecisionService(
            DecisionRequestParameters decisionRequestParameters,
            String requestID) {
        this.decisionRequestParameters = decisionRequestParameters;
        if(decisionRequestParameters.getRequestID()==null){
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(),e);
                }
            }else{
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.decisionRequestParameters.setRequestID(requestUUID);
        }
        try{
            run();
        }catch(PolicyDecisionException e){
            StdDecisionResponse decisionResponse = new StdDecisionResponse();
            decisionResponse.setDecision(PolicyDecision.ERROR);
            decisionResponse.setDetails(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            this.decisionResponse = decisionResponse;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyDecisionException{
        // Get Validation.
        if(!getValidation()){
            LOGGER.error(message);
            throw new PolicyDecisionException(message);
        }
        // Generate Request.
        String modelString = getModel().toString();
        LOGGER.debug("Generated JSON Request is: " + modelString);
        // Process Result.
        try {
            PDPServices pdpServices = new PDPServices();
            status = HttpStatus.OK;
            decisionResponse = decisionResult(pdpServices.generateRequest(modelString, decisionRequestParameters.getRequestID(), false, true));
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyDecisionException(e);
        }
    }

    private DecisionResponse decisionResult(
            Collection<PDPResponse> generateRequest) {
        StdDecisionResponse policyDecision = new StdDecisionResponse();
        if (generateRequest == null) {
            return policyDecision;
        }
        if (!generateRequest.isEmpty()) {
            for (PDPResponse stdStatus : generateRequest) {
                policyDecision.setDecision(stdStatus.getDecision());
                policyDecision.setDetails(stdStatus.getDetails());
            }
        }
        return policyDecision;
    }

    private JsonObject getModel() throws PolicyDecisionException{
        JsonArrayBuilder resourceArray = Json.createArrayBuilder();
        for (Entry<String,String> key : decisionAttributes.entrySet()) {
            if (key.getKey().isEmpty()) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an Empty Key";
                LOGGER.error(message);
                throw new PolicyDecisionException(message);
            }
            JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
            if (key.getValue().matches("[0-9]+")) {
            
            	if ((key.getKey().equals("ErrorCode")) || (key.getKey().equals("WorkStep"))) {

            		resourceBuilder.add("Value", key.getValue());

            	} else {
            
                    int val = Integer.parseInt(key.getValue());
                    resourceBuilder.add("Value", val);

            	}
            
            } else {
                resourceBuilder.add("Value", key.getValue());
            }
            resourceBuilder.add("AttributeId", key.getKey());
            resourceArray.add(resourceBuilder);
        }
        return Json.createObjectBuilder()
                .add("Request", Json.createObjectBuilder()
                                .add("AccessSubject", Json.createObjectBuilder()
                                                .add("Attribute", Json.createObjectBuilder()
                                                                .add("Value", onapComponentName)
                                                                .add("AttributeId", "ONAPName")))
                                .add("Resource", Json.createObjectBuilder()
                                                .add("Attribute", resourceArray))
                                .add("Action", Json.createObjectBuilder()
                                                .add("Attribute", Json.createObjectBuilder()
                                                                .add("Value", "DECIDE")
                                                                .add("AttributeId", "urn:oasis:names:tc:xacml:1.0:action:action-id"))))
                .build();
    }

    private boolean getValidation() {
        if(decisionRequestParameters==null){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Request Paramaters";
            return false;
        }
        onapComponentName = decisionRequestParameters.getOnapName();
        decisionAttributes = decisionRequestParameters.getDecisionAttributes();
        if (onapComponentName == null || onapComponentName.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No onapComponentName given : " + onapComponentName;
            return false;
        }
        if (decisionAttributes == null || decisionAttributes.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Attributes Given. ";
            return false;
        }
        return true;
    }

    public DecisionResponse getResult() {
        return decisionResponse;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}

