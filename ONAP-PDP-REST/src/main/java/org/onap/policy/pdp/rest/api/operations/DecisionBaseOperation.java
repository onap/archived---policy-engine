/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.pdp.rest.api.operations;

import java.util.Map;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * DecisionBaseOperation - Defines the factory method for creating operations and template method to execute them.
 */
public abstract class DecisionBaseOperation implements DecisionOperation {

    private static final Logger LOGGER = FlexLogger.getLogger(DecisionBaseOperation.class.getName());

    /** The operation type. */
    private DecisionOperationType operationType = null;

    /**
     * Instantiates a new decision base operation.
     *
     * @param operationType the operation type
     */
    public DecisionBaseOperation(DecisionOperationType operationType) {
        super();
        this.operationType = operationType;
    }

    /**
     * Creates the operation of type.
     *
     * @param opType the op type
     * @return the decision base operation
     */
    public static DecisionBaseOperation createOperationOfType(DecisionOperationType opType) {
        DecisionBaseOperation operation = null;
        switch (opType) {
            case NAMINGSEQGEN:
                operation = new DecisionNamingSeqOp(opType);
                break;
            case STRINGOP:
                operation = new DecisionPostOperation(opType);
                break;
            default:
                LOGGER.error("Operation not defined");
                break;
        }

        return operation;
    }

    /**
     * Execute operation.
     *
     * @param opData the op data
     * @throws PolicyDecisionException the policy decision exception
     */
    public final void executeOperation(Map<String, Object> opData) throws PolicyDecisionException {
        if (opData == null || opData.isEmpty()) {
            // needed? ops possibly can have no params.
            LOGGER.info("executeOperation:opData passed in is empty for operation of type - " + getOperationType());
            throw new PolicyDecisionException("executeOperation:opData passed in is empty for operation of type - " + getOperationType());
        }
        boolean ret = populateOperation(opData);
        if (!ret) {
            LOGGER.info("populateOperation failed for operation of type - " + getOperationType());
            throw new PolicyDecisionException("populateOperation failed for operation of type - " + getOperationType());
        }

        ret = performOperation();
        if (!ret) {
            LOGGER.info("performOperation failed for operation of type - " + getOperationType());
            throw new PolicyDecisionException("performOperation failed for operation of type - " + getOperationType());
        }
    }

    /**
     * Gets the operation type.
     *
     * @return the operationType
     */
    public DecisionOperationType getOperationType() {
        return operationType;
    }

    /**
     * Sets the operation type.
     *
     * @param operationType the operationType to set
     */
    public void setOperationType(DecisionOperationType operationType) {
        this.operationType = operationType;
    }

}
