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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


@JsonInclude(JsonInclude.Include.NON_NULL)

public class IncrementSequence implements Serializable {
    private static final long serialVersionUID = 1L;
    // do not correct typo for preceding. Model has the typo.
    public static final String SEQUENCE_SCOPE_PRECEDING = "PRECEEDING";
    public static final String SEQUENCE_SCOPE_SURROUNDING = "ENTIRETY";
    public static final String SEQUENCE_TYPE_ALPHA = "alpha";
    public static final String SEQUENCE_TYPE_NUMERIC = "numeric";

    private String scope;
    @JsonProperty("start-value")
    private String startValue;
    private String max;
    private long length;
    private String increment;
    @JsonProperty("sequence-type")
    private String sequenceType;

    /**
     * No args constructor for use in serialization.
     * 
     */
    public IncrementSequence() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param increment step value traversing range
     * @param startValue start of range
     * @param scope used to generate the sequence key
     * @param length length of resulting seq
     */
    public IncrementSequence(String scope, String startValue, String max, long length, String increment,
            String sequenceType) {
        super();
        this.scope = scope;
        this.startValue = startValue;
        this.max = max;
        this.length = length;
        this.increment = increment;
        this.sequenceType = sequenceType;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    @JsonProperty("scope")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("start-value")
    public String getStartValue() {
        return startValue;
    }

    @JsonProperty("start-value")
    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    @JsonProperty("length")
    public long getLength() {
        return length;
    }

    @JsonProperty("length")
    public void setLength(long length) {
        this.length = length;
    }

    @JsonProperty("increment")
    public String getIncrement() {
        return increment;
    }

    @JsonProperty("increment")
    public void setIncrement(String increment) {
        this.increment = increment;
    }


    @JsonProperty("sequence-type")
    public String getSequenceType() {
        return sequenceType;
    }

    @JsonProperty("sequence-type")
    public void setSequenceType(String sequenceType) {
        this.sequenceType = sequenceType;
    }

    public String getEndValue() {
        return max;
    }

    public void setEndValue(String endValue) {
        this.max = endValue;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(increment).append(startValue).append(scope).append(length).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof IncrementSequence)) {
            return false;
        }
        IncrementSequence rhs = ((IncrementSequence) other);
        return new EqualsBuilder().append(increment, rhs.increment).append(startValue, rhs.startValue)
                .append(scope, rhs.scope).append(length, rhs.length).isEquals();
    }

}
