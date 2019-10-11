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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.JAXBElement;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;

public class CreateBRMSRawController {

    private static final Logger logger = FlexLogger.getLogger(CreateBRMSRawController.class);

    protected PolicyRestAdapter policyAdapter = null;

    public void prePopulateBRMSRawPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {

        if (policyAdapter.getPolicyData() instanceof PolicyType) {
            PolicyType policy = (PolicyType) policyAdapter.getPolicyData();
            policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());

            // Set PolicyAdapter name value
            setPolicyAdapterNameValue(policyAdapter);

            // Set PolicyAdapter description.
            setPolicyAdapterDescription(policyAdapter, policy);

            // Set PolicyAdapter attributes.
            setPolicyAdapterAttributes(policyAdapter, policy);

            // Set PolicyAdapter configBodyData
            policyAdapter.setConfigBodyData(entity.getConfigurationData().getConfigBody());

            // Get the target data under policy.
            TargetType target = policy.getTarget();
            if (target == null) {
                return;
            }
            // Under target we have AnyOFType
            List<AnyOfType> anyOfList = target.getAnyOf();
            if (anyOfList == null) {
                return;
            }

            // Set PolicyAdapter riskType, riskLevel, guard, ttlDate from match attributes
            setPolicyAdapterMatchAttributes(policyAdapter, policy.getTarget().getAnyOf());
        }
    }

    private void setPolicyAdapterMatchAttributes(PolicyRestAdapter policyAdapter, List<AnyOfType> anyOfList) {
        anyOfList.stream()
                // Extract nonNull list of AllOfType objs from each AnyOfType obj
                .map(AnyOfType::getAllOf).filter(Objects::nonNull).forEach(allOfList ->
                // Extract nonNull list of MatchType objs from each AllOFType obj
                allOfList.stream().map(AllOfType::getMatch).filter(Objects::nonNull).flatMap(Collection::stream)
                        .forEach(match -> {
                            // Under the match we have attribute value and
                            // attributeDesignator. So,finally down to the actual attribute.
                            AttributeValueType attributeValue = match.getAttributeValue();
                            String value = (String) attributeValue.getContent().get(0);
                            AttributeDesignatorType designator = match.getAttributeDesignator();
                            String attributeId = designator.getAttributeId();
                            if ("RiskType".equals(attributeId)) {
                                policyAdapter.setRiskType(value);
                            } else if ("RiskLevel".equals(attributeId)) {
                                policyAdapter.setRiskLevel(value);
                            } else if ("guard".equals(attributeId)) {
                                policyAdapter.setGuard(value);
                            } else if ("TTLDate".equals(attributeId) && !value.contains("NA")) {
                                PolicyController controller = new PolicyController();
                                String newDate = controller.convertDate(value);
                                policyAdapter.setTtlDate(newDate);
                            }
                        }));
    }

    private void setPolicyAdapterNameValue(final PolicyRestAdapter policyAdapter) {
        // policy name value is the policy name without any prefix and extensions.
        String policyNameValue =
                policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("BRMS_Raw_") + 9);
        if (logger.isDebugEnabled()) {
            logger.debug("Prepopulating form data for BRMS RAW Policy selected:" + policyAdapter.getPolicyName());
        }
        policyAdapter.setPolicyName(policyNameValue);
    }

    private void setPolicyAdapterDescription(final PolicyRestAdapter policyAdapter, final PolicyType policy) {
        String description;
        try {
            description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
        } catch (Exception e) {
            logger.info("Not able to see the createdby in description. So, add generic description", e);
            description = policy.getDescription();
        }
        policyAdapter.setPolicyDescription(description);
    }

    @SuppressWarnings("unchecked")
    private void setPolicyAdapterAttributes(final PolicyRestAdapter policyAdapter, final PolicyType policy) {
        ArrayList<Object> attributeList = new ArrayList<>();
        AdviceExpressionsType expressionTypes =
                ((RuleType) policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().get(0))
                        .getAdviceExpressions();
        for (AdviceExpressionType adviceExpression : expressionTypes.getAdviceExpression()) {
            for (AttributeAssignmentExpressionType attributeAssignment : adviceExpression
                    .getAttributeAssignmentExpression()) {
                if (attributeAssignment.getAttributeId().startsWith("key:")) {
                    Map<String, String> attribute = new HashMap<>();
                    String key = attributeAssignment.getAttributeId().replace("key:", "");
                    attribute.put("key", key);
                    JAXBElement<AttributeValueType> attributeValue =
                            (JAXBElement<AttributeValueType>) attributeAssignment.getExpression();
                    String value = (String) attributeValue.getValue().getContent().get(0);
                    attribute.put("value", value);
                    attributeList.add(attribute);
                } else if (attributeAssignment.getAttributeId().startsWith("dependencies:")) {
                    ArrayList<String> dependencies = new ArrayList<>(Arrays
                            .asList(attributeAssignment.getAttributeId().replace("dependencies:", "").split(",")));
                    dependencies.remove("");
                    policyAdapter.setBrmsDependency(dependencies);
                } else if (attributeAssignment.getAttributeId().startsWith("controller:")) {
                    policyAdapter.setBrmsController(attributeAssignment.getAttributeId().replace("controller:", ""));
                }
            }
            policyAdapter.setAttributes(attributeList);
        }
    }
}
