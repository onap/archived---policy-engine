/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.controller;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.openecomp.policy.admin.PolicyManagerServlet;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.adapter.ClosedLoopPMBody;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.jpa.PolicyEntity;

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

										// First match in the target is EcompName, so set that value.
										if (attributeId.equals("ECOMPName")) {
											policyAdapter.setEcompName(value);
										}
										if (attributeId.equals("RiskType")){
											policyAdapter.setRiskType(value);
										}
										if (attributeId.equals("RiskLevel")){
											policyAdapter.setRiskLevel(value);
										}
										if (attributeId.equals("guard")){
											policyAdapter.setGuard(value);
										}
										if (attributeId.equals("TTLDate") && !value.contains("NA")){
											String newDate = convertDate(value, true);
											policyAdapter.setTtlDate(newDate);
										}
										if (attributeId.equals("ServiceType")){
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

	private String convertDate(String dateTTL, boolean portalType) {
		String formateDate = null;
		String[] date;
		String[] parts;

		if (portalType){
			parts = dateTTL.split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0] + "T05:00:00.000Z";
		} else {
			date  = dateTTL.split("T");
			parts = date[0].split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		}
		return formateDate;
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
