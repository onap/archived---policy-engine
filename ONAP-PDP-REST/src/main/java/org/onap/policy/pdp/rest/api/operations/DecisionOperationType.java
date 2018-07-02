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

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The Enum DecisionOperationType.
 */
public enum DecisionOperationType {
    /**
     * Indicates a sequence number generation operation for Naming models.
     */
    NAMINGSEQGEN("Sequence Number Generation"),

    /**
     * Indicates an Internal DB operation.
     */
    DBOP("DB Operation"),
    /**
     * Indicates an operation to perform string manipulation. Primarily used for post operations.
     */
    STRINGOP("String manipulation Operation"),

    /** Indicates no operation. */
    NOOP("NOOP");

    private String name;

    /**
     * Instantiates a new decision operation type.
     *
     * @param name the name
     */
    private DecisionOperationType(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Creates the.
     *
     * @param value the value
     * @return the decision operation type
     */
    @JsonCreator
    public static DecisionOperationType create(String value) {
        for (DecisionOperationType type : values()) {
            if (type.toString().equals(value) || type.equals(DecisionOperationType.valueOf(value))) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }

}
