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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import javax.script.SimpleBindings;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.daoimpl.PolicyValidationDaoImpl;
import org.onap.policy.rest.jpa.NamingSequences;
import org.onap.policy.xacml.api.XACMLErrorConstants;


/**
 * The Class NamingModel.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NamingModel implements Serializable {

    private static final Logger LOGGER = FlexLogger.getLogger(NamingModel.class.getName());

    private static final long serialVersionUID = 1L;

    @JsonProperty("naming-properties")
    private List<NamingProperty> namingProperties = new ArrayList<>();

    @JsonProperty("naming-type")
    private String namingType;

    @JsonProperty("naming-recipe")
    private String namingRecipe;

    @JsonProperty("nfRole")
    private String nfRole = null;

    @JsonProperty("name-operation")
    private String nameOperation;

    private String result = null;

    private String requestId = null;

    /**
     * No args constructor for use in serialization.
     */
    public NamingModel() {
        super();
    }

    /**
     * Instantiates a new naming model.
     *
     * @param namingProperties the naming properties
     * @param namingType the naming type
     * @param namingRecipe the naming recipe
     */
    public NamingModel(List<NamingProperty> namingProperties, String namingType, String namingRecipe, String nfRole,
            String nameOperation) {
        super();
        this.namingProperties = namingProperties;
        this.namingType = namingType;
        this.namingRecipe = namingRecipe;
        this.nfRole = nfRole;
        this.nameOperation = nameOperation;
    }

    /**
     * Generate result.
     *
     * @throws PolicyDecisionException the policy decision exception
     */
    public void generateResult() throws PolicyDecisionException {
        // accumulate results of each property and construct the name
        // in the order specified in naming recipe
        String[] props = parseNamingRecipe();
        if (ArrayUtils.isEmpty(props) || namingProperties.isEmpty()) {
            LOGGER.error("ERROR:For Request - " + requestId
                    + ", Either naming recipe or naming props is empty, namingModel - " + this);
            throw new PolicyDecisionException("ERROR:generateResult: " + XACMLErrorConstants.ERROR_DATA_ISSUE);
        }
        LOGGER.info("INFO: For Request - " + requestId + ", Going to generate the name based on naming recipe: "
                + namingRecipe + ", namingProperties: " + namingProperties);
        StringBuilder propValues = new StringBuilder();
        for (String namingRecipeProp : props) {
            // Using default 16 character size
            NamingProperty namingProp = findNamingPropByName(namingRecipeProp);
            if (namingProp != null) {
                propValues.append(namingProp.getResult());
            } else {
                LOGGER.error("ERROR: generateResult: For Request - " + requestId + ", "
                        + XACMLErrorConstants.ERROR_DATA_ISSUE + "The property name in recipe" + namingRecipeProp
                        + " is not found in property list");
                throw new PolicyDecisionException("ERROR: generateResult: " + XACMLErrorConstants.ERROR_DATA_ISSUE
                        + " - The property name in recipe" + namingRecipeProp + " is not found in Policy JSON");
            }
        }

        // now that name is generated, perform the name-operation on it
        performPostOp(propValues.toString());

        if (StringUtils.isBlank(getResult())) {
            LOGGER.error("ERROR: generateResult: For Request - " + requestId + ", "
                    + XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to perform name-operation" + this);
            throw new PolicyDecisionException("ERROR:  generateResult: " + XACMLErrorConstants.ERROR_PROCESS_FLOW
                    + " - Failed to perform name-operation");
        }

        // update the generated name in the db for this seq
        // it is used by updateDictionary to reuse the seq number
        writeNameToDb();
    }

    /**
     * Perform post op.
     *
     * @param res the res
     */
    public void performPostOp(String res) {
        LOGGER.info(" Request - " + requestId + ", Going to perform postop on the result - " + res);
        // if there is nothing to do
        if (StringUtils.isBlank(nameOperation)) {
            LOGGER.info("For the Request - " + requestId + ", No postop defined");
            this.setResult(res);
            return;
        }

        // use the NamingProperty's postop method, for now just create an obj
        NamingProperty doPostOp = new NamingProperty();
        doPostOp.setResult(res);
        doPostOp.setPropertyOperation(getNameOperation());
        boolean ret = doPostOp.performPostOp();
        if (!ret) {
            // log it and continue
            LOGGER.error("For the Request - " + requestId + ", Failed to perform postop on the result - " + res);
            this.setResult(res);
        } else {
            this.setResult(doPostOp.getResult());
        }
    }

    /**
     * Write generated name to DB for being able to lookup for reuse updateDictionary.
     */
    public void writeNameToDb() {
        LOGGER.info("INFO:For Request - " + requestId + ", Going to writeNameToDb - " + getResult());

        // only needed if sequence is in the naming recipe
        if (!namingRecipe.contains("SEQUENCE")) {
            LOGGER.info("INFO:For Request - " + requestId + ", No Sequence in naming recipe.");
            return;
        }

        NamingProperty seqProp = findNamingPropByName("SEQUENCE");


        final String queryString = "FROM NamingSequences WHERE namingtype= :namingtype AND sequencekey = :sequencekey"
                + " AND currentseq = :currentseq AND scope = :scope";
        SimpleBindings params = new SimpleBindings();
        params.put("namingtype", getNamingType());
        params.put("sequencekey", seqProp.getSeqKey());
        if (StringUtils.containsIgnoreCase(seqProp.getIncrementSequence().getSequenceType(),
                IncrementSequence.SEQUENCE_TYPE_ALPHA)) {
            params.put("currentseq", Long.parseLong(seqProp.getResult(), 36));
        } else {
            params.put("currentseq", Long.parseLong(seqProp.getResult()));
        }
        params.put("scope", seqProp.getIncrementSequence().getScope());
        PolicyValidationDaoImpl dbConnection = new PolicyValidationDaoImpl();
        List<Object> res = dbConnection.getDataByQuery(queryString, params);
        if (!res.isEmpty()) {
            NamingSequences seq = (NamingSequences) res.get(0);
            seq.setGeneratedName(getResult());
            dbConnection.update(seq);
            LOGGER.info("Request - " + requestId + ", Updated the generated Name to DB - " + this);
        } else {
            LOGGER.error("Request ID - " + requestId + ", Failed to update the generated Name to DB - " + this);
        }
    }

    /**
     * Find naming prop by name.
     *
     * @param id the id
     * @return the naming property
     */
    public NamingProperty findNamingPropByName(String id) {
        return this.namingProperties.stream().filter(np -> np.getPropertyName().trim().equals(id)).findFirst()
                .orElse(null);
    }

    /**
     * Find naming prop by name with valid value.
     *
     * @param id the id
     * @return the naming property
     */
    public NamingProperty findNamingPropByNameValue(String id) {
        return this.namingProperties.stream()
                .filter(np -> np.getPropertyName().trim().equals(id) && !StringUtils.isBlank(np.getPropertyValue()))
                .findFirst().orElse(null);
    }

    /**
     * Parses the naming recipe.
     *
     * @return the string[]
     */
    public String[] parseNamingRecipe() {
        String[] props = null;
        if (!StringUtils.isBlank(namingRecipe)) {
            try {
                props = namingRecipe.trim().split("\\s*\\|\\s*");
            } catch (PatternSyntaxException e) {
                LOGGER.error(
                        "ERROR:For the Request - " + requestId + ", Parsing the naming recipe - NamingModel - " + this);
            }
        } else {
            LOGGER.error("ERROR: For Request - " + requestId + ", NamingModel is empty - " + this);
        }

        LOGGER.info("For Request - " + requestId + ", Parsed Naming recipe - " + Arrays.toString(props));

        return props;
    }

    /**
     * Gets the seq key.
     *
     * @param prop the prop
     * @param propsFromRecipe the props from recipe
     * @return the seq key
     */
    public String getSeqKey(NamingProperty prop, String[] propsFromRecipe) {
        LOGGER.info("For Request- " + requestId + ", Going to generate the seq key based on property - " + prop
                + ", Parsed Naming recipe - " + Arrays.toString(propsFromRecipe));
        String scope = prop.getIncrementSequence().getScope();
        StringBuilder seqKey = new StringBuilder();
        if (StringUtils.isBlank(scope) || propsFromRecipe == null) {
            LOGGER.error("ERROR: For this Request - " + requestId + ", Sequence scope is empty");
            return null;
        }
        boolean done = false;
        for (String propName : propsFromRecipe) {
            if (done) { // sonar complains abt two breaks.
                break;
            }
            NamingProperty recipeProp = findNamingPropByName(propName);
            if (recipeProp == null) {
                LOGGER.error("ERROR: For Request - " + requestId + ", Unable to find Property - " + propName
                        + ", in config data");
                return null;
            }
            // if we reach the sequence prop and it is preceding scope, we are
            // done. If it is prefix + postfix, keep going.
            if (prop.getPropertyName().equalsIgnoreCase(recipeProp.getPropertyName())) {
                if (IncrementSequence.SEQUENCE_SCOPE_PRECEDING.equalsIgnoreCase(scope)) {
                    done = true;
                }
                // skip the same prop
            } else if (scope.equalsIgnoreCase(recipeProp.getPropertyName())) {
                // if scope is for particular prop, once we reach it, we have our
                // key
                seqKey.append(recipeProp.getResult());
                done = true;
            } else if (IncrementSequence.SEQUENCE_SCOPE_PRECEDING.equalsIgnoreCase(scope)
                    || IncrementSequence.SEQUENCE_SCOPE_SURROUNDING.equalsIgnoreCase(scope)) {
                seqKey.append(recipeProp.getResult());
            }
        }
        LOGGER.info("For Request - " + requestId + ", Generated the seq key - " + seqKey.toString());
        return seqKey.toString();
    }

    /**
     * Gets the naming properties.
     *
     * @return the naming properties
     */
    @JsonProperty("naming-properties")
    public List<NamingProperty> getNamingProperties() {
        return namingProperties;
    }

    /**
     * Sets the naming properties.
     *
     * @param namingProperties the new naming properties
     */
    @JsonProperty("naming-properties")
    public void setNamingProperties(List<NamingProperty> namingProperties) {
        this.namingProperties = namingProperties;
    }

    /**
     * Gets the naming type.
     *
     * @return the naming type
     */
    @JsonProperty("naming-type")
    public String getNamingType() {
        return namingType;
    }

    /**
     * Sets the naming type.
     *
     * @param namingType the new naming type
     */
    @JsonProperty("naming-type")
    public void setNamingType(String namingType) {
        this.namingType = namingType;
    }

    /**
     * Gets the naming recipe.
     *
     * @return the naming recipe
     */
    @JsonProperty("naming-recipe")
    public String getNamingRecipe() {
        return namingRecipe;
    }

    /**
     * Sets the naming recipe.
     *
     * @param namingRecipe the new naming recipe
     */
    @JsonProperty("naming-recipe")
    public void setNamingRecipe(String namingRecipe) {
        this.namingRecipe = namingRecipe;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Gets the nf role.
     *
     * @return the nf role
     */
    public String getNfRole() {
        return nfRole;
    }

    /**
     * Sets the nf role.
     *
     * @param nfRole the new nf role
     */
    public void setNfRole(String nfRole) {
        this.nfRole = nfRole;
    }

    /**
     * Gets the request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request id.
     *
     * @param requestId the new request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getNameOperation() {
        return nameOperation;
    }

    public void setNameOperation(String nameOperation) {
        this.nameOperation = nameOperation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(namingRecipe).append(namingType).append(nameOperation).append(nfRole)
                .append(namingProperties).toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        NamingModel rhs = (NamingModel) obj;

        return new EqualsBuilder().append(namingRecipe, rhs.namingRecipe).append(namingType, rhs.namingType)
                .append(nameOperation, rhs.nameOperation).append(nfRole, rhs.nfRole)
                .append(namingProperties, rhs.namingProperties).isEquals();
    }

}
