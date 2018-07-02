/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.NamingModel;
import org.onap.policy.pdp.rest.api.models.NamingProperty;
import org.onap.policy.pdp.rest.api.operations.DecisionBaseOperation;
import org.onap.policy.pdp.rest.api.operations.DecisionNamingSeqOp;
import org.onap.policy.pdp.rest.api.operations.DecisionOperationType;
import org.onap.policy.std.StdDecisionResponse;
import org.onap.policy.xacml.api.XACMLErrorConstants;



/**
 * Naming Model JSON Processor to generate Naming.
 */
public class DecisionMsNamingService {

    private static final Logger LOGGER = FlexLogger.getLogger(DecisionMsNamingService.class.getName());

    private String configStr = null;
    private StdDecisionResponse decisionResponse = null;
    private String requestId = null;
    private String message = null;
    private Map<String, String> decisionAttributes = null;

    private static final String NAMING_TYPE_REQ_PARAM = "naming-type";
    private static final String NAMING_NFROLE_REQ_PARAM = "nfRole";
    private static final String NAMING_CONFIG_JSON_MODELS = "naming-models";

    /** List of all the naming models in the json. */
    private List<NamingModel> namingModels = null;

    /** The selected naming model to be executed for this request. */
    private NamingModel selectedNamingModel = null;

    /** The request model inputs. */
    private Map<String, String> requestModelInputs = null;

    /** The naming type for this request. */
    private String namingType = null;

    /**
     * The nfRole, if present in request, used to identify the naming model to use.
     */
    private String nfRole = null;

    /**
     * Instantiates a new decision MS naming service.
     *
     * @param decisionRequestAttributes the decision request attributes
     * @param configStrParam the config str param
     * @param requestIdParam the request ID param
     */
    public DecisionMsNamingService(Map<String, String> decisionRequestAttributes, String configStrParam,
            String requestIdParam) {
        this.decisionAttributes = decisionRequestAttributes;
        this.requestId = requestIdParam;
        this.configStr = configStrParam;
        decisionResponse = new StdDecisionResponse();
    }

    /**
     * Parses the naming model config json.
     *
     * @return true, if successful
     */
    public boolean parseNamingConfig() {
        boolean ret = true;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode node = mapper.readTree(configStr);

            // Read the config json and retrieve all the naming models in it
            // Based on the getDecision request params, we will later
            // identify the specific naming model
            node = node.findValue(NAMING_CONFIG_JSON_MODELS);
            this.namingModels = mapper.readValue(node.toString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, NamingModel.class));

            // Log all the models retrieved from policy for debugging
            LOGGER.info(" For request - " + requestId + ", the policy has the following Naming models in it - ");
            for (NamingModel namModel : namingModels) {
                LOGGER.info("NamingProperty:" + namModel);
            }
        } catch (IOException e) {
            LOGGER.error("FATAL_ERROR: Unable to parse config JSON for request - " + requestId);
            message = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Error parsing config json. ";
            ret = false;
        }
        return ret;
    }

    /**
     * Parses the naming request params.
     *
     * @return true, if successful
     */
    public boolean parseNamingRequestParams() {
        boolean ret = true;
        ObjectMapper mapper = null;
        String inputJson = null;
        try {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            inputJson = decisionAttributes.get(PDPServices.DECISION_MS_NAMING_TYPE);

            // convert JSON string to Map
            requestModelInputs = mapper.readValue(inputJson, new TypeReference<Map<String, String>>() {});
            namingType = requestModelInputs.get(NAMING_TYPE_REQ_PARAM);
            nfRole = requestModelInputs.get(NAMING_NFROLE_REQ_PARAM);
        } catch (Exception e) {
            LOGGER.error("Error parsing request json for requestId: " + requestId + ", inputJson: " + inputJson);
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Error parsing request json. ";
            ret = false;
        }
        return ret;
    }

    /**
     * Run - It does the following - Parse Config JSON and getDecision request param JSON. - Identify the naming model
     * to execute based on request param - Parses the naming recipe and execute all operations to populate the
     * individual properties in the recipe - Generates the output name based on recipe
     */
    public void run() {

        try {
            // parse the config json and request json and validate
            if (!parseAndValidate()) {
                setOutputResponse(PolicyDecision.DENY, message);
                return;
            }

            // Each naming recipe may have multiple naming properties to
            // resolve. we need to execute the operation for each one
            // and maintain the order with sequence executed at the end.
            if (!executeNamingRecipe()) {
                setOutputResponse(PolicyDecision.DENY, message);
                return;
            }
            // all the ops and postops are complete. Now generate the result
            // this method also performs postop on generated name, if any
            selectedNamingModel.generateResult();
            logResponse(true);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            setOutputResponse(PolicyDecision.DENY, XACMLErrorConstants.ERROR_DATA_ISSUE + e.getMessage());
        }
    }

    private boolean executeNamingRecipe() {
        // A naming recipe contains multiple pipe delimited properties.
        // For ex, Naming recipe - PropA|PropB|SEQUENCE|PropC
        // We loop through the naming recipe and need to resolve
        // each property to its value and then combine them together
        // to generate the output name.
        // Property value resolution => req params, property value defined in
        // policy,
        // constant defined in policy or sequence involving lookup to DB.
        // req params => prop name in recipe needs to match the req param name
        // Each property value can have a postop specified for them
        // And the combined output name would have postops defined.
        // Refer to DecisionPostOperation for supported postops.

        String[] propsFromRecipe = selectedNamingModel.parseNamingRecipe();
        LOGGER.info("For Request - " + requestId + ", Going to execute Naming recipe, parsed props - "
                + Arrays.toString(propsFromRecipe));
        List<NamingProperty> sequenceOps = new ArrayList<>();
        for (String prop : propsFromRecipe) {
            NamingProperty namingProp = selectedNamingModel.findNamingPropByName(prop);
            if (namingProp == null) {
                // fatal error
                LOGGER.error("ERROR: For the requestId - " + requestId
                        + " , Property from recipe not found in parsed NamingProperties List - " + prop);
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Property : " + prop
                        + " from recipe not found in parsed NamingProperties List";
                return false;
            }
            DecisionOperationType opType;
            try {
                opType = namingProp.identifyOperation(requestModelInputs, selectedNamingModel);
            } catch (PolicyDecisionException e) {
                LOGGER.error(
                        "ERROR: For requestId - " + requestId + " , Unable to resolve value for Property - " + prop);
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Property - " + prop
                        + " - unable to resolve value for Property.";
                return false;
            }
            if (opType == DecisionOperationType.NOOP) {
                // we already have the result, just do post ops.
                // we need to do postop before invoking seq op
                namingProp.performPostOp();
            } else if (opType == DecisionOperationType.NAMINGSEQGEN) {
                // insertion order
                sequenceOps.add(namingProp);
            } else {
                // fatal error - unidentified operation
                LOGGER.error("ERROR: For requestId - " + requestId
                        + " , Unable to identify operation for this property -  " + prop);
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Property - " + prop
                        + ", unable to retrieve value for this.";
                return false;
            }
        }

        // now execute the sequence ops at the end
        if (!executeSeqOps(sequenceOps, propsFromRecipe)) {
            setOutputResponse(PolicyDecision.DENY, message);
            return false;
        }

        return true;
    }

    private boolean executeSeqOps(List<NamingProperty> sequenceOps, String[] propsFromRecipe) {
        // we support only one sequence per naming recipe.
        // Sequence is unique to the combination of
        // naming type and seq key generated based on the scope
        // sequence can have different scopes - PRECEDING or prefix+postfix or
        // individual prop.
        // If Preceding, then seq key will be PropA + PropB
        // ENTIRETY = > PropA + PropB + PropC
        // or it would be one of the properties. PropA
        try {
            for (NamingProperty prop : sequenceOps) {
                String seqKey = selectedNamingModel.getSeqKey(prop, propsFromRecipe);
                if (StringUtils.isBlank(seqKey)) {
                    // seqkey cannot be empty.
                    LOGGER.error("FATAL_ERROR: Sequence key is empty for request - " + requestId + " - Sequence - " + prop.getIncrementSequence());
                    message = XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Sequence key cannot be empty ";
                    return false;
                }
                // save off the key for later updates to DB
                prop.setSeqKey(seqKey);

                Map<String, Object> opData = new HashMap<>();
                opData.put(DecisionNamingSeqOp.NAMING_PROP, prop);
                opData.put(DecisionNamingSeqOp.SEQ_KEY, seqKey);
                opData.put(DecisionNamingSeqOp.NAMING_TYPE, selectedNamingModel.getNamingType());

                DecisionBaseOperation seqOp =
                        DecisionBaseOperation.createOperationOfType(DecisionOperationType.NAMINGSEQGEN);
                seqOp.executeOperation(opData);

                prop.setResult(seqOp.getResult());
            }
        } catch (PolicyDecisionException e) {
            message = e.getMessage();
            return false;
        }
        return true;
    }

    /**
     * Select the naming type.
     *
     * @return true, if successful
     */
    private boolean selectTheNamingType() {
        boolean ret = true;

        // Config json provides list of Naming Models.
        // getDecision req param provides the namingType
        // to identify the particular naming model to execute.
        // Some naming models would require another field, nfRole
        // to identify the model to use. We do this check only if
        // NfRole was passed in getDecision req param.
        // if NfRole does not yield a naming model, but namingType alone
        // does, we use that instead of failing.
        NamingModel onlyNamingTypeMatched = null;
        for (NamingModel namModel : namingModels) {
            if (namingType.equals(namModel.getNamingType())) {
                if (StringUtils.isBlank(nfRole) || nfRole.equalsIgnoreCase(namModel.getNfRole())) {
                    selectedNamingModel = namModel;
                    break;
                } else if (onlyNamingTypeMatched == null) {
                    // we save the first instance of namingType matching,
                    // but not nfRole, in case it is needed later
                    onlyNamingTypeMatched = namModel;
                }
            }
        }

        if (selectedNamingModel == null && onlyNamingTypeMatched != null) {
            // identifying by nfRole failed, do we fail or try to continue???
            LOGGER.info("For request - " + requestId + ", cannot find Naming Model by matching NamingType: "
                    + namingType + ", NfRole: " + nfRole
                    + ". But we match only by NamingType. Continuing with only NamingType matched.");
            selectedNamingModel = onlyNamingTypeMatched;
        }

        if (selectedNamingModel == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "NamingType did not match - " + namingType;
            ret = false;
        } else {
            // set the request id for logging purposes
            selectedNamingModel.setRequestId(requestId);
        }

        return ret;
    }

    /**
     * Parses the and validate.
     *
     * @return true, if successful
     */
    private boolean parseAndValidate() {
        // First, parse the conifg json.
        if (!parseNamingConfig()) {
            LOGGER.error(message);
            return false;
        }

        // Next, parse the getDecision request params
        if (!parseNamingRequestParams()) {
            LOGGER.error(message);
            return false;
        }
        // Validate the inputs.
        if (!validate()) {
            LOGGER.error(message);
            return false;
        }

        // identify the naming type to use. Policy can have multiple but
        // we support only executing one of them per getDecision
        if (!selectTheNamingType()) {
            LOGGER.error(message);
            return false;
        }
        return true;
    }

    /**
     * Log response.
     *
     * @param decision the decision
     */
    private void logResponse(boolean decision) {
        LOGGER.info(" Decision is " + (decision ? "PERMIT" : "DENY") + "for requestId: " + getRequestId()
                + ", Request Inputs - " + getRequestModelInputs() + ", Selected Naming Model - " + selectedNamingModel);
        if (decision) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> out = new HashMap<>();
            out.put(selectedNamingModel.getNamingType(), selectedNamingModel.getResult());
            String outputJson = null;
            try {
                outputJson = mapper.writeValueAsString(out);

                LOGGER.info("Successfully generated name for request: " + getRequestId()
                        + ", for NamingType: selectedNamingModel.getNamingType()" + ", namingRecipe: "
                        + selectedNamingModel.getNamingRecipe() + " outputJson: " + outputJson);

                setOutputResponse(PolicyDecision.PERMIT, outputJson);
            } catch (JsonProcessingException e) {
                LOGGER.info("For request - " + requestId + " - Error Generating Output JSON with Result - "
                        + selectedNamingModel.getResult());
                setOutputResponse(PolicyDecision.DENY,
                        XACMLErrorConstants.ERROR_PROCESS_FLOW + " Error Generating Output JSON");
            }
        } else {
            setOutputResponse(PolicyDecision.DENY,
                    XACMLErrorConstants.ERROR_PROCESS_FLOW + " Unable to generate result.");
            try {
                LOGGER.error(
                        "Failed to generate name for request: " + getRequestId()
                                + ((selectedNamingModel != null)
                                        ? (", for Naming Type:" + selectedNamingModel.getNamingType()
                                                + ", namingRecipe: " + selectedNamingModel.getNamingRecipe() + "")
                                        : ""));
                if (selectedNamingModel != null) {
                    for (NamingProperty prop : selectedNamingModel.getNamingProperties()) {
                        LOGGER.error("Property:" + prop.getPropertyName() + ", result:" + prop.getResult());
                    }
                    LOGGER.error("NamingModel:" + selectedNamingModel.getResult());
                } else {
                    LOGGER.error(
                            "Failed to log the info for failed name generation, selectedNamingModel not available");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to log the info for failed name generation");
            }
        }
    }

    /**
     * Validate the config json and getDecision request params.
     *
     * @return true, if successful
     */
    private boolean validate() {
        // validate that policy has at least one naming model defined
        if (namingModels == null || namingModels.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Decision Attributes Given. ";
            return false;
        }

        // validate that getDecision has req params
        if (requestModelInputs == null || requestModelInputs.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Request Decision Attributes Given. ";
            return false;
        }

        // validate that getDecision has NamingType param
        if (StringUtils.isBlank(namingType)) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No " + NAMING_TYPE_REQ_PARAM
                    + " provided in Request Decision Attributes.";
            return false;
        }

        return true;
    }

    /**
     * Sets the output response.
     *
     * @param decide the decide
     * @param msg the msg
     */
    private void setOutputResponse(PolicyDecision decide, String msg) {
        LOGGER.info(
                "setOutputResponse: For request: " + getRequestId() + " - Decision: " + decide + ", Message - " + msg);
        decisionResponse.setDecision(decide);
        decisionResponse.setDetails(msg);
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public DecisionResponse getResult() {
        return decisionResponse;
    }

    /**
     * Gets the config str.
     *
     * @return the configStr
     */
    public String getConfigStr() {
        return configStr;
    }

    /**
     * Sets the config str.
     *
     * @param configStr the configStr to set
     */
    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }

    /**
     * Gets the decision response.
     *
     * @return the decisionResponse
     */
    public StdDecisionResponse getDecisionResponse() {
        return decisionResponse;
    }

    /**
     * Sets the decision response.
     *
     * @param decisionResponse the decisionResponse to set
     */
    public void setDecisionResponse(StdDecisionResponse decisionResponse) {
        this.decisionResponse = decisionResponse;
    }

    /**
     * Gets the request ID.
     *
     * @return the requestID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID.
     *
     * @param requestID the requestID to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the decision attributes.
     *
     * @return the decisionAttributes
     */
    public Map<String, String> getDecisionAttributes() {
        return decisionAttributes;
    }

    /**
     * Sets the decision attributes.
     *
     * @param decisionAttributes the decisionAttributes to set
     */
    public void setDecisionAttributes(Map<String, String> decisionAttributes) {
        this.decisionAttributes = decisionAttributes;
    }

    /**
     * Gets the naming models.
     *
     * @return the namingModels
     */
    public List<NamingModel> getNamingModels() {
        return namingModels;
    }

    /**
     * Sets the naming models.
     *
     * @param namingModels the namingModels to set
     */
    public void setNamingModels(List<NamingModel> namingModels) {
        this.namingModels = namingModels;
    }

    /**
     * Gets the selected naming model.
     *
     * @return the selectedNamingModel
     */
    public NamingModel getSelectedNamingModel() {
        return selectedNamingModel;
    }

    /**
     * Sets the selected naming model.
     *
     * @param selectedNamingModel the selectedNamingModel to set
     */
    public void setSelectedNamingModel(NamingModel selectedNamingModel) {
        this.selectedNamingModel = selectedNamingModel;
    }

    /**
     * Gets the request model inputs.
     *
     * @return the requestModelInputs
     */
    public Map<String, String> getRequestModelInputs() {
        return requestModelInputs;
    }

    /**
     * Sets the request model inputs.
     *
     * @param requestModelInputs the requestModelInputs to set
     */
    public void setRequestModelInputs(Map<String, String> requestModelInputs) {
        this.requestModelInputs = requestModelInputs;
    }

    /**
     * Gets the naming type.
     *
     * @return the namingType
     */
    public String getNamingType() {
        return namingType;
    }

    /**
     * Sets the naming type.
     *
     * @param namingType the namingType to set
     */
    public void setNamingType(String namingType) {
        this.namingType = namingType;
    }

    /**
     * Gets the vnf type.
     *
     * @return the vnfType
     */
    public String getVnfType() {
        return nfRole;
    }

    /**
     * Sets the vnf type.
     *
     * @param vnfType the vnfType to set
     */
    public void setVnfType(String vnfType) {
        this.nfRole = vnfType;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
