/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

@Controller
@RequestMapping("/")
public class CreatePolicyController extends RestrictedBaseController {
	private static Logger policyLogger = FlexLogger
			.getLogger(CreatePolicyController.class);
	protected PolicyRestAdapter policyAdapter = null;
	private ArrayList<Object> attributeList;
	boolean isValidForm = false;

	public void prePopulateBaseConfigPolicyData(
			PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		attributeList = new ArrayList<>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			policyAdapter.setConfigType(entity.getConfigurationData()
					.getConfigType());
			policyAdapter.setConfigBodyData(entity.getConfigurationData()
					.getConfigBody());
			String policyNameValue = policyAdapter.getPolicyName().substring(
					policyAdapter.getPolicyName().indexOf('_') + 1);
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try {
				description = policy.getDescription().substring(0,
						policy.getDescription().indexOf("@CreatedBy:"));
			} catch (Exception e) {
				policyLogger.error(
						"Error while collecting the desciption tag in ActionPolicy "
								+ policyNameValue, e);
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			// Get the target data under policy.
			TargetType target = policy.getTarget();
			if (target != null) {
				// Under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AnyOFType we have AllOFType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList
									.iterator();
							int index = 0;
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList
											.iterator();
									while (iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attribute
										// value and
										// attributeDesignator. So,finally down
										// to the actual attribute.
										//
										AttributeValueType attributeValue = match
												.getAttributeValue();
										String value = (String) attributeValue
												.getContent().get(0);
										AttributeDesignatorType designator = match
												.getAttributeDesignator();
										String attributeId = designator
												.getAttributeId();
										// First match in the target is
										// OnapName, so set that value.
										if ("ONAPName".equals(attributeId)) {
											policyAdapter.setOnapName(value);
										}
										if ("RiskType".equals(attributeId)) {
											policyAdapter.setRiskType(value);
										}
										if ("RiskLevel".equals(attributeId)) {
											policyAdapter.setRiskLevel(value);
										}
										if ("guard".equals(attributeId)) {
											policyAdapter.setGuard(value);
										}
										if ("TTLDate".equals(attributeId)
												&& !value.contains("NA")) {
											PolicyController controller = new PolicyController();
											String newDate = controller
													.convertDate(value);
											policyAdapter.setTtlDate(newDate);
										}
										if ("ConfigName".equals(attributeId)) {
											policyAdapter.setConfigName(value);
										}
										// After Onap and Config it is optional
										// to have attributes, so
										// check weather dynamic values or there
										// or not.
										if (index >= 7) {
											Map<String, String> attribute = new HashMap<>();
											attribute.put("key", attributeId);
											attribute.put("value", value);
											attributeList.add(attribute);
										}
										index++;
									}
								}
							}
						}
					}
				}

				policyAdapter.setAttributes(attributeList);
			}
			List<Object> ruleList = policy
					.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
			for (Object o : ruleList) {
				if (o instanceof RuleType) {
					// get the condition data under the rule for rule
					// Algorithms.
					policyAdapter.setRuleID(((RuleType) o).getRuleId());
				}
			}
		}
	}
}
