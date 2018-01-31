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


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.onap.policy.admin.PolicyManagerServlet;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopPMBody;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class CreateClosedLoopPMController{

	private static final Logger LOGGER	= FlexLogger.getLogger(CreateClosedLoopPMController.class);

	protected PolicyRestAdapter policyAdapter = null;

	public void prePopulateClosedLoopPMPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("PM_") +3);
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				LOGGER.info("General error" , e);
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
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (matchList.size()>1 && iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attribute value and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();

										// First match in the target is OnapName, so set that value.
										if ("ONAPName".equals(attributeId)) {
											policyAdapter.setOnapName(value);
										}
										if ("RiskType".equals(attributeId)){
											policyAdapter.setRiskType(value);
										}
										if ("RiskLevel".equals(attributeId)){
											policyAdapter.setRiskLevel(value);
										}
										if ("guard".equals(attributeId)){
											policyAdapter.setGuard(value);
										}
										if ("TTLDate".equals(attributeId) && !value.contains("NA")){
											PolicyController controller = new PolicyController();
											String newDate = controller.convertDate(value);
											policyAdapter.setTtlDate(newDate);
										}
										if ("ServiceType".equals(attributeId)){
											LinkedHashMap<String, String> serviceTypePolicyName1 = new LinkedHashMap<>();
											String key = "serviceTypePolicyName";
											serviceTypePolicyName1.put(key, value);
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
									}
								}
							}
						}
					}
				}
			}
			readClosedLoopJSONFile(policyAdapter, entity);
		}
	}

	protected void readClosedLoopJSONFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			ClosedLoopPMBody closedLoopBody = mapper.readValue(entity.getConfigurationData().getConfigBody(), ClosedLoopPMBody.class);
			policyAdapter.setJsonBodyData(closedLoopBody);
		} catch (IOException e) {
			LOGGER.error("Exception Occured"+e);
		}
	}

	//get vertica metrics data from the table
	private String getVertica(String policyName){
		String verticas = null;
		JsonArray data = PolicyManagerServlet.getPolicyNames();
		for(int i=0 ; i< data.size(); i++){
			if(policyName.equals(data.getJsonObject(i).getJsonString("serviceTypePolicyName").getString())){
				verticas = data.getJsonObject(i).getJsonString("verticaMetrics").getString();
				return verticas;
			}
		}
		return verticas;
	}

	//get policy description from the table
	private String getDescription(String policyName){
		String description = null;
		JsonArray data = PolicyManagerServlet.getPolicyNames();
		for(int i=0 ; i< data.size(); i++){
			if(policyName.equals(data.getJsonObject(i).getJsonString("serviceTypePolicyName").getString())){
				description = data.getJsonObject(i).getJsonString("policyDescription").getString();
				return description;
			}
		}
		return description;
	}

	//get Attributes
	private JsonObject getAttributes(String policyName){
		JsonObject attributes = null;
		JsonArray data = PolicyManagerServlet.getPolicyNames();
		for(int i=0 ; i< data.size(); i++){
			if(policyName.equals(data.getJsonObject(i).getJsonString("serviceTypePolicyName").getString())){
				attributes = data.getJsonObject(i).getJsonObject("attributes");
				return attributes;
			}
		}
		return attributes;
	}

}
