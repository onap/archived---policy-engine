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

package org.onap.policy.pdp.rest.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.operations.DecisionBaseOperation;
import org.onap.policy.pdp.rest.api.operations.DecisionOperationType;
import org.onap.policy.pdp.rest.api.operations.DecisionPostOperation;
import org.onap.policy.pdp.rest.api.operations.DecisionPostOperation.PostOpType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NamingProperty implements Serializable {
    private static final Logger LOGGER = FlexLogger.getLogger(NamingProperty.class.getName());
    private static final long serialVersionUID = 1L;

    @JsonProperty("property-value")
    private String propertyValue;
    @JsonProperty("source-endpoint")
    private String sourceEndpoint;
    @JsonProperty("property-name")
    private String propertyName;
    @JsonProperty("source-system")
    private String sourceSystem;
    @JsonProperty("increment-sequence")
    private IncrementSequence incrementSequence;
    @JsonProperty("property-operation")
    private String propertyOperation;

    private String result = null;

    // only applies to seq properties
    private String seqKey = null;

    /**
     * No args constructor for use in serialization.
     * 
     */
    public NamingProperty() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param propertyName Mandatory
     * @param sourceEndpoint optional
     * @param sourceSystem optional
     * @param propertyValue optional
     * @param incrementSequence optional
     */
    public NamingProperty(String propertyValue, String sourceEndpoint, String propertyName, String sourceSystem,
            IncrementSequence incrementSequence) {
        super();
        this.propertyValue = propertyValue;
        this.sourceEndpoint = sourceEndpoint;
        this.propertyName = propertyName;
        this.sourceSystem = sourceSystem;
        this.incrementSequence = incrementSequence;
    }

    /**
     * Identify operation.
     *
     * @param reqParams the req params
     * @param selectedModel the selected model
     * @return the decision operation type
     * @throws PolicyDecisionException the policy decision exception
     */
    public DecisionOperationType identifyOperation(Map<String, String> reqParams, NamingModel selectedModel)
            throws PolicyDecisionException {

        LOGGER.info("Going to decide operation for property - " + propertyName);
        DecisionOperationType ret = null;
        if (StringUtils.isBlank(propertyName)) {
            // we cant do anything if propertyName is empty
            LOGGER.error("Error identifying the operation for NamingProperty - PropertyName is invalid: " + this);
            throw new PolicyDecisionException(
                    "Error identifying the operation for NamingProperty - PropertyName is invalid");
        }
        if ("CONSTANT_TAG".equalsIgnoreCase(propertyName.trim())) {
            // constant props have their value specified in the policy
            result = propertyValue.trim();
            ret = DecisionOperationType.NOOP;
        } else if (!StringUtils.isBlank(propertyValue)) {
            // some props may have their value defined at time of policy
            // creation like DELIMITER
            ret = DecisionOperationType.NOOP;
            result = propertyValue.trim();
        } else if ("SEQUENCE".equalsIgnoreCase(propertyName)) {
            // we need a seq op to resolve this property
            ret = DecisionOperationType.NAMINGSEQGEN;
        } else {
            // it would be the case where this propertyName occurs
            // multiple times in the recipe but value only provided once.
            NamingProperty res = selectedModel.findNamingPropByNameValue(propertyName);
            if (res != null) {
                // use this prop value
                result = res.getPropertyValue();
                ret = DecisionOperationType.NOOP;
            } else {
                // if nothing matches, we assume it to come from request params
                result = reqParams.get(propertyName.trim());
                ret = DecisionOperationType.NOOP;
            }
        }

        if (StringUtils.isBlank(result) && ret == DecisionOperationType.NOOP) {
            LOGGER.error("Error identifying the operation for NamingProperty: " + this);
            throw new PolicyDecisionException("Error identifying the operation for NamingProperty - " + propertyName);
        }

        return ret;

    }

    /**
     * Perform post op.
     *
     * @return true, if successful
     */
    public boolean performPostOp() {
        if (StringUtils.isBlank(propertyOperation)) {
            LOGGER.info("PropertyOpertation empty - " + this);
            return false;
        }

        PostOpType strOp = null;
        String indx = null;
        if (propertyOperation.toLowerCase().contains("lowercase")) {
            strOp = PostOpType.POSTOP_LOWERCASE;
        } else if (propertyOperation.toLowerCase().contains("uppercase")) {
            strOp = PostOpType.POSTOP_UPPERCASE;
        } else if (propertyOperation.toLowerCase().contains("substr")) {
            strOp = PostOpType.POSTOP_SUBSTRING;
            // substring(4) => Retrieve first n chars
            // substring(-4) => retrieve last n chars
            // the postop expects it as numeric 0,4 or -1,4
            // for first n chars, convert it to 0,4
            // for reverse, convert it to -1,4
            String indexStr = propertyOperation.replaceAll("[^0-9-]", "");

            if (StringUtils.isBlank(indexStr)) {
                LOGGER.error("NamingProperty:performPostOP - Invalid argument for substring post operation - " + this);
                return false;
            }
            int indxInt = Integer.parseInt(indexStr);
            if (indxInt > 0) {
                // retrieve first n chars
                indx = "0," + Integer.toString(indxInt);
            } else if (indxInt < 0) {
                // retrieve last n chars
                indx = "-1," + Integer.toString(indxInt).replace("-", "");
            } else {
                LOGGER.error("NamingProperty:performPostOP - Error parsing the Indices - " + this);
                return false;
            }
        }

        DecisionBaseOperation postOp = DecisionBaseOperation.createOperationOfType(DecisionOperationType.STRINGOP);
        Map<String, Object> opData = new HashMap<>();
        opData.put(DecisionPostOperation.POSTOP_INPUT_STR, result);
        opData.put(DecisionPostOperation.POSTOP_TYPE, strOp);
        opData.put(DecisionPostOperation.POSTOP_SUBSTR_INDEX, indx);

        String res = null;
        try {
            postOp.executeOperation(opData);
            res = postOp.getResult();
            if (!StringUtils.isBlank(res)) {
                result = res;
            } else {
                // continue with the existing result and log that post op failed
                LOGGER.error("NamingProperty:performPostOP - Failed performing postop, continuing with the result - "
                        + this);
            }
        } catch (PolicyDecisionException e) {
            LOGGER.error("NamingProperty:performPostOP - Failed to perform postop - " + e.getMessage());
            return false;
        }

        return true;
    }

    @JsonProperty("property-value")
    public String getPropertyValue() {
        return propertyValue;
    }

    @JsonProperty("property-value")
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @JsonProperty("source-endpoint")
    public String getSourceEndpoint() {
        return sourceEndpoint;
    }

    @JsonProperty("source-endpoint")
    public void setSourceEndpoint(String sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    @JsonProperty("property-name")
    public String getPropertyName() {
        return propertyName;
    }

    @JsonProperty("property-name")
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @JsonProperty("source-system")
    public String getSourceSystem() {
        return sourceSystem;
    }

    @JsonProperty("source-system")
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @JsonProperty("increment-sequence")
    public IncrementSequence getIncrementSequence() {
        return incrementSequence;
    }

    @JsonProperty("increment-sequence")
    public void setIncrementSequence(IncrementSequence incrementSequence) {
        this.incrementSequence = incrementSequence;
    }

    public String getPropertyOperation() {
        return propertyOperation;
    }

    public void setPropertyOperation(String propertyOperation) {
        this.propertyOperation = propertyOperation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSeqKey() {
        return seqKey;
    }

    public void setSeqKey(String seqKey) {
        this.seqKey = seqKey;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(propertyName).append(sourceEndpoint).append(sourceSystem)
                .append(propertyValue).append(incrementSequence).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NamingProperty)) {
            return false;
        }
        NamingProperty rhs = ((NamingProperty) other);
        return new EqualsBuilder().append(propertyName, rhs.propertyName).append(sourceEndpoint, rhs.sourceEndpoint)
                .append(sourceSystem, rhs.sourceSystem).append(propertyValue, rhs.propertyValue)
                .append(incrementSequence, rhs.incrementSequence).isEquals();
    }

}
