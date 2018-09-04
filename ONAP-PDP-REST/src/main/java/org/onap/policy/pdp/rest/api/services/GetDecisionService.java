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

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMResponse;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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
    private Map<String, String> decisionAttributes = null;
    private UUID requestUuid = null;
    private String requestType = null;

    /**
     * Instantiates a new gets the decision service.
     *
     * @param decisionRequestParameters the decision request parameters
     * @param requestId the request id
     */
    public GetDecisionService(DecisionRequestParameters decisionRequestParameters, String requestId) {
        this.decisionRequestParameters = decisionRequestParameters;
        if (decisionRequestParameters.getRequestID() == null) {
            if (!StringUtils.isBlank(requestId)) {
                try {
                    requestUuid = UUID.fromString(requestId);
                } catch (IllegalArgumentException e) {
                    requestUuid = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUuid.toString(), e);
                }
            } else {
                requestUuid = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUuid.toString());
            }
            this.decisionRequestParameters.setRequestID(requestUuid);
        }
        try {
            run();
        } catch (PolicyDecisionException e) {
            StdDecisionResponse decisionResp = new StdDecisionResponse();
            decisionResp.setDecision(PolicyDecision.ERROR);
            decisionResp.setDetails(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            this.decisionResponse = decisionResp;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyDecisionException {
        // Get Validation.
        if (!getValidation()) {
            LOGGER.error(message);
            throw new PolicyDecisionException(message);
        }

        try {
            // first check whether it is a raw xacml req
            if (!StringUtils.isBlank(requestType) && PDPServices.DECISION_RAW_XACML.equals(requestType)) {
                this.setRequestType(PDPServices.DECISION_RAW_XACML);
                processRawXacmlReq();
                return;
            }

            // Generate Request.
            String modelString = getModel().toString();
            LOGGER.debug("Generated JSON Request is: " + modelString);
            // Process Result.

            PDPServices pdpServices = new PDPServices();
            if (modelString.contains(PDPServices.RAINYDAY_TYPE)) {
                pdpServices.setRequestType(PDPServices.RAINYDAY_TYPE);
                this.setRequestType(PDPServices.RAINYDAY_TYPE);
            } else if (PDPServices.DECISION_MS_NAMING_TYPE.equals(requestType)) {
                pdpServices.setRequestType(PDPServices.DECISION_MS_NAMING_TYPE);
            }
            status = HttpStatus.OK;
            decisionResponse = decisionResult(
                    pdpServices.generateRequest(modelString, decisionRequestParameters.getRequestID(), false, true));
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyDecisionException(e);
        }
    }

    private void processRawXacmlReq() throws PolicyDecisionException {
        Request pdpRequest = null;
        Response pdpResponse = null;
        // Process Result.
        try {
            PDPServices pdpServices = new PDPServices();
            pdpServices.setRequestType(PDPServices.DECISION_RAW_XACML);
            String rawXacmlReq = decisionAttributes.get(PDPServices.DECISION_RAW_XACML);
            if (StringUtils.isBlank(rawXacmlReq)) {
                LOGGER.error("Raw XACML request cannot be empty.");
                throw new PolicyDecisionException(
                        "Raw XACML request cannot be empty. Please provide the XACML request in "
                                + PDPServices.DECISION_RAW_XACML);
            }
            String rawXacmlReqMode = decisionAttributes.get(PDPServices.DECISION_RAW_XACML_TYPE);
            String reqType = PDPServices.DECISION_RAW_XACML_XML_TYPE;
            if (!StringUtils.isBlank(rawXacmlReqMode)
                    && PDPServices.DECISION_RAW_XACML_JSON_TYPE.equalsIgnoreCase(rawXacmlReqMode.trim())) {
                pdpRequest = JSONRequest.load(rawXacmlReq);
                reqType = PDPServices.DECISION_RAW_XACML_JSON_TYPE;
            } else {
                pdpRequest = DOMRequest.load(StringEscapeUtils.unescapeXml(rawXacmlReq));
                pdpServices.setRequestFormat(PDPServices.DECISION_RAW_XACML_XML_TYPE);
            }

            status = HttpStatus.OK;
            pdpResponse = pdpServices.callPdp(pdpRequest, getRequestUuid());

            String outgoingResponseString = null;
            if (PDPServices.DECISION_RAW_XACML_JSON_TYPE.equalsIgnoreCase(reqType)) {
                outgoingResponseString = JSONResponse.toString(pdpResponse, false);
            } else {
                outgoingResponseString = DOMResponse.toString(pdpResponse, false);
                if (!StringUtils.isBlank(outgoingResponseString)) {
                    outgoingResponseString = StringEscapeUtils.escapeXml10(outgoingResponseString);
                }
            }

            LOGGER.info("processRawXacmlReq - Request - \n" + rawXacmlReq + "\n Reponse:\n" + outgoingResponseString);
            StdDecisionResponse decisionResp = new StdDecisionResponse();
            if (!StringUtils.isBlank(outgoingResponseString)
                    && outgoingResponseString.contains(Decision.PERMIT.toString())) {
                decisionResp.setDecision(PolicyDecision.PERMIT);
            } else if (!StringUtils.isBlank(outgoingResponseString)
                    && outgoingResponseString.contains(Decision.DENY.toString())) {
                decisionResp.setDecision(PolicyDecision.DENY);
            } else {
                decisionResp.setDecision(PolicyDecision.ERROR);
            }
            decisionResp.setDetails(outgoingResponseString);
            this.decisionResponse = decisionResp;
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyDecisionException(e);
        }
    }

    private DecisionResponse decisionResult(Collection<PDPResponse> generateRequest) {
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

    private JsonObject getModel() throws PolicyDecisionException {
        JsonArrayBuilder resourceArray = Json.createArrayBuilder();
        for (Entry<String, String> entry : decisionAttributes.entrySet()) {
            if (entry.getKey().isEmpty()) {
                String msg = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an Empty Key";
                LOGGER.error(msg);
                throw new PolicyDecisionException(msg);
            }
            if (PDPServices.DECISION_MS_NAMING_TYPE.equalsIgnoreCase(entry.getKey().trim())) {
                // this is used for only Model execution and not for identifying
                // policy. It is input data for MS Naming model execution and
                // will be parsed in the naming service. Skip here.
                this.setRequestType(PDPServices.DECISION_MS_NAMING_TYPE);
                continue;
            }

            JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
            if (entry.getValue().matches("[0-9]+")) {

                if ((entry.getKey().equals("ErrorCode")) || (entry.getKey().equals("WorkStep"))) {

                    resourceBuilder.add("Value", entry.getValue());

                } else {

                    int val = Integer.parseInt(entry.getValue());
                    resourceBuilder.add("Value", val);

                }

            } else {
                resourceBuilder.add("Value", entry.getValue());
            }
            resourceBuilder.add("AttributeId", entry.getKey());
            resourceArray.add(resourceBuilder);
        }
        return Json.createObjectBuilder().add("Request", Json.createObjectBuilder().add("AccessSubject",
                Json.createObjectBuilder().add("Attribute",
                        Json.createObjectBuilder().add("Value", onapComponentName).add("AttributeId", "ONAPName")))
                .add("Resource", Json.createObjectBuilder().add("Attribute", resourceArray))
                .add("Action", Json.createObjectBuilder().add("Attribute", Json.createObjectBuilder()
                        .add("Value", "DECIDE").add("AttributeId", "urn:oasis:names:tc:xacml:1.0:action:action-id"))))
                .build();
    }

    private boolean getValidation() {
        if (decisionRequestParameters == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Request Paramaters";
            return false;
        }
        onapComponentName = decisionRequestParameters.getOnapName();
        decisionAttributes = decisionRequestParameters.getDecisionAttributes();
        if (decisionAttributes == null || decisionAttributes.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Attributes Given. ";
            return false;
        }
        if (decisionAttributes.containsKey(PDPServices.DECISION_RAW_XACML)) {
            // onapName not mandatory for raw requests
            requestType = PDPServices.DECISION_RAW_XACML;
        } else if (onapComponentName == null || onapComponentName.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No onapComponentName given : " + onapComponentName;
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

    public UUID getRequestUuid() {
        return requestUuid;
    }

    public void setRequestUuid(UUID requestId) {
        this.requestUuid = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

}
