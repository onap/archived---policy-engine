/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Bell Canada
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

package org.onap.policy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import javax.json.JsonArray;
import javax.json.JsonObject;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.onap.policy.admin.PolicyManagerServlet;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopPMBody;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;

public class CreateClosedLoopPMController {

    private static final Logger LOGGER = FlexLogger.getLogger(CreateClosedLoopPMController.class);
    private static final String KEY_SERVICE_TYPE_POLICY_NAME = "serviceTypePolicyName";

    protected PolicyRestAdapter policyAdapter = null;

    /**
     * prePopulateClosedLoopPMPolicyData.
     *
     * @param policyAdapter PolicyRestAdapter
     * @param entity PolicyEntity
     */
    public void prePopulateClosedLoopPMPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        if (! (policyAdapter.getPolicyData() instanceof PolicyType)) {
            return;
        }
        Object policyData = policyAdapter.getPolicyData();
        PolicyType policy = (PolicyType) policyData;

        // Set oldPolicyFileName to PolicyAdapter
        policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());

        // Set policyNameValue and description to PolicyAdapter
        setPolicyAdapterNameValueAndDescription(policyAdapter, policy);

        // Set PolicyAdapter JsonBodyData
        setClosedLoopJsonFile(policyAdapter, entity);

        // Get the target data under policy.
        TargetType target = policy.getTarget();
        if (target == null) {
            return;
        }
        // Set PolicyAdapter OnapNameField, riskType, riskLevel, guard, ttlDate, ServiceType from match attributes
        setPolicyAdapterMatchAttributes(policyAdapter, target.getAnyOf());
    }

    private void setPolicyAdapterNameValueAndDescription(PolicyRestAdapter policyAdapter, PolicyType policy) {
        String policyNameValue =
                policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("PM_") + 3);
        policyAdapter.setPolicyName(policyNameValue);
        String description;
        try {
            description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
        } catch (Exception e) {
            LOGGER.info("General error", e);
            description = policy.getDescription();
        }
        policyAdapter.setPolicyDescription(description);
    }

    private void setClosedLoopJsonFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        try {
            ClosedLoopPMBody closedLoopBody =
                    new ObjectMapper().readValue(entity.getConfigurationData().getConfigBody(), ClosedLoopPMBody.class);
            policyAdapter.setJsonBodyData(closedLoopBody);
        } catch (IOException e) {
            LOGGER.error("Exception Occured" + e);
        }
    }

    private void setPolicyAdapterMatchAttributes(final PolicyRestAdapter policyAdapter,
            final List<AnyOfType> anyOfList) {
        anyOfList.stream()
                // Extract nonNull list of AllOfType objs from each AnyOfType obj
                .map(AnyOfType::getAllOf).filter(Objects::nonNull).forEach(allOfList ->
                // Extract nonNull list of MatchType objs from each AllOFType obj
                allOfList.stream().map(AllOfType::getMatch).filter(Objects::nonNull)
                        .forEach(matchList -> matchList.forEach(match -> {
                            // Under the match we have attribute value and
                            // attributeDesignator. So,finally down to the actual attribute.
                            AttributeValueType attributeValue = match.getAttributeValue();
                            String value = (String) attributeValue.getContent().get(0);
                            AttributeDesignatorType designator = match.getAttributeDesignator();
                            String attributeId = designator.getAttributeId();
                            // First match in the target is OnapName, so set that value.
                            policyAdapter.setupUsingAttribute(attributeId, value);
                            if ("ServiceType".equals(attributeId)) {
                                LinkedHashMap<String, String> serviceTypePolicyName1 = new LinkedHashMap<>();
                                serviceTypePolicyName1.put(KEY_SERVICE_TYPE_POLICY_NAME, value);
                                policyAdapter.setServiceTypePolicyName(serviceTypePolicyName1);
                                LinkedHashMap<String, String> vertica = new LinkedHashMap<>();
                                vertica.put("verticaMetrics", getVertica(value));
                                policyAdapter.setVerticaMetrics(vertica);
                                LinkedHashMap<String, String> desc = new LinkedHashMap<>();
                                desc.put("policyDescription", getDescription(value));
                                policyAdapter.setDescription(desc);
                                LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
                                attributes.put("attributes", getAttributes(value));
                                policyAdapter.setAttributeFields(attributes);
                            }
                        })));
    }

    // get vertica metrics data from the table
    private String getVertica(String policyName) {
        JsonArray data = PolicyManagerServlet.getPolicyNames();
        for (int i = 0; i < data.size(); i++) {
            if (policyName.equals(data.getJsonObject(i).getJsonString(KEY_SERVICE_TYPE_POLICY_NAME).getString())) {
                return data.getJsonObject(i).getJsonString("verticaMetrics").getString();
            }
        }
        return null;
    }

    // get policy description from the table
    private String getDescription(String policyName) {
        JsonArray data = PolicyManagerServlet.getPolicyNames();
        for (int i = 0; i < data.size(); i++) {
            if (policyName.equals(data.getJsonObject(i).getJsonString(KEY_SERVICE_TYPE_POLICY_NAME).getString())) {
                return data.getJsonObject(i).getJsonString("policyDescription").getString();
            }
        }
        return null;
    }

    // get Attributes
    private JsonObject getAttributes(String policyName) {
        JsonArray data = PolicyManagerServlet.getPolicyNames();
        for (int i = 0; i < data.size(); i++) {
            if (policyName.equals(data.getJsonObject(i).getJsonString(KEY_SERVICE_TYPE_POLICY_NAME).getString())) {
                return data.getJsonObject(i).getJsonObject("attributes");
            }
        }
        return null;
    }
}
