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

package org.onap.policy.pdp.rest.api.operations;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * DecisionPostOperation - used to perform string manipulation ops.
 * Currently supports - to_lowercase, to_uppercase, substring
 * substring(a,b) if a < 0 , retrieve from end of str
 */
public class DecisionPostOperation extends DecisionBaseOperation {

    private static final Logger LOGGER = FlexLogger.getLogger(DecisionPostOperation.class.getName());

    public static final String POSTOP_INPUT_STR = "INPUTSTR";

    public static final String POSTOP_TYPE = "OPTYPE";

    public static final String POSTOP_SUBSTR_INDEX = "SUBSTR_INDX";

    /**
     * The Enum PostOpType.
     */
    public enum PostOpType {

        POSTOP_LOWERCASE("to_lowercase"), POSTOP_UPPERCASE("to_uppercase"), POSTOP_SUBSTRING(
                "substring"), POSTOP_CONTAINS("contains");

        String postOperationType;

        /**
         * Instantiates a new post op type.
         *
         * @param r the r
         */
        PostOpType(String r) {
            this.postOperationType = r;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.postOperationType;
        }
    }

    private String inputStr = null;

    private PostOpType opType = null;

    /** Only applicable for substring op. */
    private int beginIndex = -1;

    /** Only applicable for substring op. */
    private int endIndex = -1;

    /** The result of postop. */
    private String result = null;

    /**
     * Instantiates a new decision post operation.
     *
     * @param operationType the operation type
     */
    public DecisionPostOperation(DecisionOperationType operationType) {
        super(operationType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.pdp.rest.api.operations.DecisionOperation# populateOperation(java.util.Map)
     */
    @Override
    public boolean populateOperation(Map<String, Object> opInputData) throws PolicyDecisionException {
        if (opInputData == null || opInputData.isEmpty()) {
            LOGGER.info("DecisionPostOperation:populateOperation - Input Data is empty");
            throw new PolicyDecisionException("DecisionPostOperation:populateOperation - Input Data is empty");
        }
        this.inputStr = (String) opInputData.get(POSTOP_INPUT_STR);
        this.opType = (PostOpType) opInputData.get(POSTOP_TYPE);
        if (PostOpType.POSTOP_SUBSTRING == opType) {
            // retrieve the begin and end index for substr
            String substrIndices = (String) opInputData.get(POSTOP_SUBSTR_INDEX);
            validateSubstrIndx(substrIndices);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.pdp.rest.api.operations.DecisionOperation# performOperation()
     */
    @Override
    public boolean performOperation() {
        switch (opType) {
            case POSTOP_SUBSTRING:
                if (beginIndex >= 0) {
                    result = inputStr.substring(beginIndex, endIndex);
                } else {
                    result = inputStr.substring(inputStr.length() - endIndex);
                }
                break;
            case POSTOP_LOWERCASE:
                result = inputStr.toLowerCase();
                break;
            case POSTOP_UPPERCASE:
                result = inputStr.toUpperCase();
                break;
            case POSTOP_CONTAINS:
            default:
                break;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.pdp.rest.api.operations.DecisionOperation#getResult()
     */
    @Override
    public String getResult() {
        return result;
    }

    /**
     * Validate substr indx.
     *
     * @param indices the indices
     * @throws PolicyDecisionException the policy decision exception
     */
    private void validateSubstrIndx(String indices) throws PolicyDecisionException {
        if (StringUtils.isBlank(indices) || StringUtils.isBlank(inputStr)) {
            LOGGER.info("DecisionPostOperation:validateSubstrIndx - Empty Input String or indices");
            throw new PolicyDecisionException(
                    "DecisionPostOperation:validateSubstrIndx - Empty Input String or indices");
        }

        String[] arr = indices.split(",");
        if (arr.length < 2) {
            LOGGER.info("DecisionPostOperation:validateSubstrIndx - Invalid number of Indices");
            throw new PolicyDecisionException("DecisionPostOperation:validateSubstrIndx - Invalid number of Indices");
        }
        beginIndex = Integer.parseInt(arr[0]);
        endIndex = Integer.parseInt(arr[1]);

        if ((beginIndex == -1 && inputStr.length() < endIndex) || (inputStr.length() < endIndex - beginIndex)) {
            LOGGER.info("DecisionPostOperation:validateSubstrIndx - Invalid Indices");
            throw new PolicyDecisionException("DecisionPostOperation:validateSubstrIndx - Invalid Indices");
        }
    }

}
