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
package org.onap.policy.rest.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultBody;
import org.onap.policy.rest.adapter.ClosedLoopPMBody;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.SafePolicyWarning;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

@Service
public class PolicyValidation {
	
	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyValidation.class);
	
	public static final String CONFIG_POLICY = "Config";
	public static final String ACTION_POLICY = "Action";
	public static final String DECISION_POLICY = "Decision";
	public static final String CLOSEDLOOP_POLICY = "ClosedLoop_Fault";
	public static final String CLOSEDLOOP_PM = "ClosedLoop_PM";
	public static final String ENFORCER_CONFIG_POLICY= "Enforcer Config";
	public static final String MICROSERVICES="Micro Service";
	public static final String FIREWALL="Firewall Config";
	public static final String HTML_ITALICS_LNBREAK = "</i><br>";
	public static final String SUCCESS = "success";
	public static final String EMPTY_COMPONENT_ATTR = "Component Attributes: One or more Fields in Component Attributes is Empty.";
	
	private static Map<String, String> mapAttribute = new HashMap<>();
	
	private static CommonClassDao commonClassDao;
	
	@Autowired
	public PolicyValidation(CommonClassDao commonClassDao){
		PolicyValidation.commonClassDao = commonClassDao;
	}
	
	/*
	 * This is an empty constructor
	 */
	public PolicyValidation(){
		// Empty constructor
	}
	
	
	public StringBuilder validatePolicy(PolicyRestAdapter policyData) throws IOException{
		try{
			boolean valid = true;
			StringBuilder responseString = new StringBuilder();
			ObjectMapper mapper = new ObjectMapper();
			
			if(policyData.getPolicyName() != null){
				String policyNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getPolicyName());
				if(!policyNameValidate.contains(SUCCESS)){
					responseString.append("PolicyName:" +  policyNameValidate + "<br>");
					valid = false;
				};
			}else{
				responseString.append( "PolicyName: PolicyName Should not be empty" + "<br>");
				valid = false;
			}
			if(policyData.getPolicyDescription() != null){
				String descriptionValidate = PolicyUtils.descriptionValidator(policyData.getPolicyDescription());
				if(!descriptionValidate.contains(SUCCESS)){
					responseString.append("Description:" +  descriptionValidate + "<br>");
					valid = false;
				}	
			}

			if(CONFIG_POLICY.equalsIgnoreCase(policyData.getPolicyType())){
				if ("Base".equals(policyData.getConfigPolicyType()) || CLOSEDLOOP_POLICY.equals(policyData.getConfigPolicyType())
						||  CLOSEDLOOP_PM.equals(policyData.getConfigPolicyType()) || ENFORCER_CONFIG_POLICY.equals(policyData.getConfigPolicyType()) 
						|| MICROSERVICES.equals(policyData.getConfigPolicyType())) {
					
					if(!Strings.isNullOrEmpty(policyData.getOnapName())) {
						String onapNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getOnapName());
						if(!onapNameValidate.contains(SUCCESS)){
							responseString.append("OnapName:" +  onapNameValidate + "<br>");
							valid = false;
						}
					}else{
						responseString.append("Onap Name: Onap Name Should not be empty" + "<br>");
						valid = false;
					}
				}

				if(!Strings.isNullOrEmpty(policyData.getRiskType())) {
					String riskTypeValidate = PolicyUtils.policySpecialCharValidator(policyData.getRiskType());
					if(!riskTypeValidate.contains(SUCCESS)){
						responseString.append("RiskType:" +  riskTypeValidate + "<br>");
						valid = false;
					}
				}else {
					responseString.append("Risk Type: Risk Type Should not be Empty" + "<br>");
					valid = false;
				}

				if(!Strings.isNullOrEmpty(policyData.getRiskLevel())) {
					String validateRiskLevel = PolicyUtils.policySpecialCharValidator(policyData.getRiskLevel());
					if(!validateRiskLevel.contains(SUCCESS)){
						responseString.append("RiskLevel:" +  validateRiskLevel + "<br>");
						valid = false;
					}
				}else {
					responseString.append("Risk Level: Risk Level Should not be Empty" + "<br>");
					valid = false;
				}

				if(!Strings.isNullOrEmpty(policyData.getGuard())) {
					String validateGuard = PolicyUtils.policySpecialCharValidator(policyData.getGuard());
					if(!validateGuard.contains(SUCCESS)){
						responseString.append("Guard:" +  validateGuard + "<br>");
						valid = false;
					}
				}else {
					responseString.append("Guard: Guard Value Should not be Empty" + "<br>");
					valid = false;
				}

				if("Base".equalsIgnoreCase(policyData.getConfigPolicyType())){
					if(!Strings.isNullOrEmpty(policyData.getConfigName())) {
						String configNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigName());
						if(!configNameValidate.contains(SUCCESS)){
							responseString.append("ConfigName:" +  configNameValidate + "<br>");
							valid = false;
						}
					}else{
						responseString.append("Config Name: Config Name Should not be Empty" + "<br>");
						valid = false;
					}
					if(!Strings.isNullOrEmpty(policyData.getConfigType())) {
						String configTypeValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigType());
						if(!configTypeValidate.contains(SUCCESS)){
							responseString.append("ConfigType:" +  configTypeValidate + "<br>");
							valid = false;
						}
					}else{
						responseString.append("Config Type: Config Type Should not be Empty" + "<br>");
						valid = false;
					}
					if(!Strings.isNullOrEmpty(policyData.getConfigBodyData())) {
						String configBodyData = policyData.getConfigBodyData();
						String configType = policyData.getConfigType();
						if (configType != null) {
							if ("JSON".equals(configType)) {
								if (!PolicyUtils.isJSONValid(configBodyData)) {
									responseString.append("Config Body: JSON Content is not valid" + "<br>");
									valid = false;
								}
							} else if ("XML".equals(configType)) {
								if (!PolicyUtils.isXMLValid(configBodyData)) {
									responseString.append("Config Body: XML Content data is not valid" + "<br>");
									valid = false;
								}
							} else if ("PROPERTIES".equals(configType)) {
								if (!PolicyUtils.isPropValid(configBodyData)||configBodyData.equals("")) {
									responseString.append("Config Body: Property data is not valid" + "<br>");
									valid = false;
								} 
							} else if ("OTHER".equals(configType) && ("".equals(configBodyData))) {
								responseString.append("Config Body: Config Body Should not be Empty" + "<br>");
								valid = false;
							}
						}
					}else{
						responseString.append("Config Body: Config Body Should not be Empty" + "<br>");
						valid = false;
					}
				}

				if(FIREWALL.equalsIgnoreCase(policyData.getConfigPolicyType())){
					if(policyData.getConfigName() != null && !policyData.getConfigName().isEmpty()){
						String configNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigName());
						if(!configNameValidate.contains(SUCCESS)){
							responseString.append("<b>ConfigName</b>:<i>" +  configNameValidate + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					}else{
						responseString.append("<b>Config Name</b>:<i> Config Name is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
					if(policyData.getSecurityZone() == null || policyData.getSecurityZone().isEmpty()){
						responseString.append("<b>Security Zone</b>:<i> Security Zone is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}
				if("BRMS_Param".equalsIgnoreCase(policyData.getConfigPolicyType()) && Strings.isNullOrEmpty(policyData.getRuleName())){
					responseString.append("<b>BRMS Template</b>:<i>BRMS Template is required" + HTML_ITALICS_LNBREAK);
					valid = false;
				}
				if("BRMS_Raw".equalsIgnoreCase(policyData.getConfigPolicyType())){
					if(policyData.getConfigBodyData() != null && !policyData.getConfigBodyData().isEmpty()){
						String message = PolicyUtils.brmsRawValidate(policyData.getConfigBodyData());
						
						// If there are any error other than Annotations then this is not Valid
						if(message.contains("[ERR")){
							responseString.append("<b>Raw Rule Validate</b>:<i>Raw Rule has error"+ message + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					}else{
						responseString.append("<b>Raw Rule</b>:<i>Raw Rule is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}
				if(CLOSEDLOOP_PM.equalsIgnoreCase(policyData.getConfigPolicyType())){
					try{
						if(Strings.isNullOrEmpty(policyData.getServiceTypePolicyName().get("serviceTypePolicyName").toString())){
							responseString.append("<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required" + HTML_ITALICS_LNBREAK);
							valid = false; 
						}
						
					}catch(Exception e){
					    LOGGER.error("ERROR in ClosedLoop_PM PolicyName" , e);
						responseString.append("<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}

					if(policyData.getJsonBody() != null){
						
						ClosedLoopPMBody pmBody = mapper.readValue(policyData.getJsonBody(), ClosedLoopPMBody.class);
						if(pmBody.getEmailAddress() != null){
							String result = emailValidation(pmBody.getEmailAddress(), responseString.toString());
							if(result != SUCCESS){
								responseString.append(result + "<br>");
								valid = false;
							}
						}
						if((pmBody.isGamma() || pmBody.isMcr() || pmBody.isTrinity() || pmBody.isvDNS() || pmBody.isvUSP()) != true){
							responseString.append("<b>D2/Virtualized Services</b>: <i>Select at least one D2/Virtualized Services" + HTML_ITALICS_LNBREAK);
							valid = false; 
						}
						if(pmBody.getGeoLink() != null && !pmBody.getGeoLink().isEmpty()){
							String result = PolicyUtils.policySpecialCharValidator(pmBody.getGeoLink());
							if(!result.contains(SUCCESS)){
								responseString.append("<b>GeoLink</b>:<i>" +  result + HTML_ITALICS_LNBREAK);
								valid = false;
							};
						}
						if(pmBody.getAttributes() != null && !pmBody.getAttributes().isEmpty()){
							for(Entry<String, String> entry : pmBody.getAttributes().entrySet()){
								String key = entry.getKey();
								String value = entry.getValue();
								if(!key.contains("Message")){
									String attributeValidate = PolicyUtils.policySpecialCharValidator(value);
									if(!attributeValidate.contains(SUCCESS)){
										responseString.append("<b>Attributes</b>:<i>" +  key + " : value has spaces or invalid characters" + HTML_ITALICS_LNBREAK);
										valid = false;
									};
								}
							}	
						}
					}else{
						responseString.append("<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}
				if(CLOSEDLOOP_POLICY.equalsIgnoreCase(policyData.getConfigPolicyType())){
					if(policyData.getJsonBody() != null){

						// For API we need to get the conditions key from the Json request and check it before deserializing to POJO due to the enum
						if("API".equals(policyData.getApiflag())){
							JSONObject json = new JSONObject(policyData.getJsonBody());
							if(!json.isNull("conditions")){
								String apiCondition = (String) json.get("conditions");
								if(Strings.isNullOrEmpty(apiCondition)){
									responseString.append("<b>Conditions</b>: <i>Select At least one Condition" + HTML_ITALICS_LNBREAK);
									return responseString;
								}
							} else {
								responseString.append("<b>Conditions</b>: <i>There were no conditions provided in configBody json" + HTML_ITALICS_LNBREAK);
								return responseString;
							}
						}

						ClosedLoopFaultBody faultBody = mapper.readValue(policyData.getJsonBody(), ClosedLoopFaultBody.class);
						if(faultBody.getEmailAddress() != null && !faultBody.getEmailAddress().isEmpty()){
							String result = emailValidation(faultBody.getEmailAddress(), responseString.toString());
							if(result != SUCCESS){
								responseString.append(result+ "<br>");
								valid = false;
							}
						}
						if((faultBody.isGamma() || faultBody.isMcr() || faultBody.isTrinity() || faultBody.isvDNS() || faultBody.isvUSP()) != true){
							responseString.append("<b>D2/Virtualized Services</b>: <i>Select at least one D2/Virtualized Services" + HTML_ITALICS_LNBREAK);
							valid = false; 
						}
						if(faultBody.getActions() == null || faultBody.getActions().isEmpty()){
							responseString.append("<b>vPRO Actions</b>: <i>vPRO Actions is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getClosedLoopPolicyStatus() == null || faultBody.getClosedLoopPolicyStatus().isEmpty()){
							responseString.append("<b>Policy Status</b>: <i>Policy Status is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getConditions() == null){
							responseString.append("<b>Conditions</b>: <i>Select At least one Condition" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getGeoLink() != null && !faultBody.getGeoLink().isEmpty()){
							String result = PolicyUtils.policySpecialCharWithSpaceValidator(faultBody.getGeoLink());
							if(!result.contains(SUCCESS)){
								responseString.append("<b>GeoLink</b>:<i>" +  result + HTML_ITALICS_LNBREAK);
								valid = false;
							}
						}
						if(faultBody.getAgingWindow() == 0){
							responseString.append("<b>Aging Window</b>: <i>Aging Window is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getTimeInterval() == 0){
							responseString.append("<b>Time Interval</b>: <i>Time Interval is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getRetrys() == 0){
							responseString.append("<b>Number of Retries</b>: <i>Number of Retries is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getTimeOutvPRO() == 0){
							responseString.append("<b>APP-C Timeout</b>: <i>APP-C Timeout is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getTimeOutRuby() == 0){
							responseString.append("<b>TimeOutRuby</b>: <i>TimeOutRuby is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(faultBody.getVnfType() == null || faultBody.getVnfType().isEmpty()){
							responseString.append("<b>Vnf Type</b>: <i>Vnf Type is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					}else{
						responseString.append("<b>D2/Virtualized Services</b>: <i>Select atleast one D2/Virtualized Services" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>vPRO Actions</b>: <i>vPRO Actions is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>Aging Window</b>: <i>Aging Window is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>Policy Status</b>: <i>Policy Status is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>Conditions</b>: <i>Select Atleast one Condition" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>PEP Name</b>: <i>PEP Name is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>PEP Action</b>: <i>PEP Action is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>Time Interval</b>: <i>Time Interval is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>Number of Retries</b>: <i>Number of Retries is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>APP-C Timeout</b>: <i>APP-C Timeout is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>TimeOutRuby</b>: <i>TimeOutRuby is required" + HTML_ITALICS_LNBREAK);
						responseString.append("<b>Vnf Type</b>: <i>Vnf Type is required" + HTML_ITALICS_LNBREAK);
						valid = false; 
					}
				}

				if (MICROSERVICES.equals(policyData.getConfigPolicyType())){
					if(!Strings.isNullOrEmpty(policyData.getServiceType())){
						pullJsonKeyPairs((JsonNode) policyData.getPolicyJSON());

						String service;
						String version;
						if (policyData.getServiceType().contains("-v")){
							service = policyData.getServiceType().split("-v")[0];
							version = policyData.getServiceType().split("-v")[1];
						}else {
							service = policyData.getServiceType();
							version = policyData.getVersion();
						}
						
						if(!Strings.isNullOrEmpty(version)) {
							MicroServiceModels returnModel = getAttributeObject(service, version);
							
							if(returnModel != null) {
								String annotation = returnModel.getAnnotation();
								if (!Strings.isNullOrEmpty(annotation)){ 
									Map<String, String> rangeMap = Splitter.on(",").withKeyValueSeparator("=").split(annotation);
									for (Entry<String, String> rMap : rangeMap.entrySet()){
										if (rMap.getValue().contains("range::")){
											String value = mapAttribute.get(rMap.getKey().trim());
											String[] tempString = rMap.getValue().split("::")[1].split("-");
											int startNum = Integer.parseInt(tempString[0]);
											int endNum = Integer.parseInt(tempString[1]);
											String returnString = "InvalidreturnModel Range:" + rMap.getKey() + " must be between " 
													+ startNum + " - "  + endNum + ",";
											
											if(value != null) {
												if (PolicyUtils.isInteger(value.replace("\"", ""))){
													int result = Integer.parseInt(value.replace("\"", ""));
													if (result < startNum || result > endNum){
														responseString.append(returnString);									
														valid = false;
													}
												}else {
													responseString.append(returnString);
													valid = false;
												}
											} else {
												responseString.append("<b>"+rMap.getKey()+"</b>:<i>" + rMap.getKey() 
												+ " is required for the MicroService model " + service + HTML_ITALICS_LNBREAK);
												valid = false;
											}

										}
									}
								}
							} else {
								responseString.append("<b>Micro Service Model</b>:<i> Invalid Model. The model name, " + service + 
										" of version, " + version + " was not found in the dictionary" + HTML_ITALICS_LNBREAK);
								valid = false;
							}
						} else {
							responseString.append("<b>Micro Version</b>:<i> Micro Service Version is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					} else {
						responseString.append("<b>Micro Service</b>:<i> Micro Service is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}

					if(Strings.isNullOrEmpty(policyData.getPriority())){
						responseString.append("<b>Priority</b>:<i> Priority is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}	
			}
			if (DECISION_POLICY.equalsIgnoreCase(policyData.getPolicyType())){
				if(!Strings.isNullOrEmpty(policyData.getOnapName())){
					String onapNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getOnapName());
					if(!onapNameValidate.contains(SUCCESS)){
						responseString.append("OnapName:" +  onapNameValidate + "<br>");
						valid = false;
					}
				}else{
					responseString.append("Onap Name: Onap Name Should not be empty" + "<br>");
					valid = false;
				}

				if("Rainy_Day".equals(policyData.getRuleProvider())){
					if(policyData.getRainyday()==null){
						responseString.append("<b> Rainy Day Parameters are Required </b><br>");
						valid = false;
					}else{
						if(Strings.isNullOrEmpty(policyData.getRainyday().getServiceType())){
							responseString.append("Rainy Day <b>Service Type</b> is Required<br>");
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getRainyday().getVnfType())){
							responseString.append("Rainy Day <b>VNF Type</b> is Required<br>");
							valid = false;
						}						
						if(Strings.isNullOrEmpty(policyData.getRainyday().getBbid())){
							responseString.append("Rainy Day <b>Building Block ID</b> is Required<br>");
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getRainyday().getWorkstep())){
							responseString.append("Rainy Day <b>Work Step</b> is Required<br>");
							valid = false;
						}
						if(!policyData.getRainyday().getTreatmentTableChoices().isEmpty() &&
								policyData.getRainyday().getTreatmentTableChoices() != null){
							
							for(Object treatmentMap: policyData.getRainyday().getTreatmentTableChoices()){
								String errorCode = null;
								String treatment = null;
								if(treatmentMap instanceof LinkedHashMap<?, ?>){
									
									if(((LinkedHashMap<?, ?>) treatmentMap).containsKey("errorcode")){
										errorCode = ((LinkedHashMap<?, ?>) treatmentMap).get("errorcode").toString();
									}
									if(((LinkedHashMap<?, ?>) treatmentMap).containsKey("treatment")){
										treatment = ((LinkedHashMap<?, ?>) treatmentMap).get("treatment").toString();
									}
									
								}
								if(Strings.isNullOrEmpty(errorCode) && Strings.isNullOrEmpty(treatment)){
									responseString.append("Rainy Day <b>Error Code</b> and <b>Desired Treatment</b> cannot be empty<br>");
									valid = false;
									break;
								}
								if(Strings.isNullOrEmpty(errorCode)){
									responseString.append("Rainy Day <b>Error Code</b> is Required for each Desired Treatment<br>");
									valid = false;
									break;
								}
								if(Strings.isNullOrEmpty(treatment)){
									responseString.append("Rainy Day <b>Desired Treatment</b> is Required for each Error Code<br>");
									valid = false;
									break;
								}
							}
							
						} else {
							responseString.append("Rainy Day <b>Desired Automated Treatments</b> are Required<br>");
							valid = false;
						}
					}
				}
				
				if("GUARD_YAML".equals(policyData.getRuleProvider()) || "GUARD_BL_YAML".equals(policyData.getRuleProvider())){
					if(policyData.getYamlparams()==null){
						responseString.append("<b> Guard Params are Required </b>" + "<br>");
						valid = false;
					}else{
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getActor())){
							responseString.append("Guard Params <b>Actor</b> is Required " + "<br>");
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getRecipe())){
							responseString.append("Guard Params <b>Recipe</b> is Required " + "<br>");
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getGuardActiveStart())){
							responseString.append("Guard Params <b>Guard Active Start</b> is Required " + "<br>");
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getGuardActiveEnd())){
							responseString.append("Guard Params <b>Guard Active End</b> is Required " + "<br>");
							valid = false;
						}
						if("GUARD_YAML".equals(policyData.getRuleProvider())){
							if(Strings.isNullOrEmpty(policyData.getYamlparams().getLimit())){
								responseString.append(" Guard Params <b>Limit</b> is Required " + "<br>");
								valid = false;
							}else if(!PolicyUtils.isInteger(policyData.getYamlparams().getLimit())){
								responseString.append(" Guard Params <b>Limit</b> Should be Integer " + "<br>");
								valid = false;
							}
							if(Strings.isNullOrEmpty(policyData.getYamlparams().getTimeWindow())){
								responseString.append("Guard Params <b>Time Window</b> is Required" + "<br>");
								valid = false;
							}else if(!PolicyUtils.isInteger(policyData.getYamlparams().getTimeWindow())){
								responseString.append(" Guard Params <b>Time Window</b> Should be Integer " + "<br>");
								valid = false;
							}
							if(Strings.isNullOrEmpty(policyData.getYamlparams().getTimeUnits())){
								responseString.append("Guard Params <b>Time Units</b> is Required" + "<br>");
								valid = false;
							}
						}else if("GUARD_BL_YAML".equals(policyData.getRuleProvider())){
							if(policyData.getYamlparams().getBlackList()==null || policyData.getYamlparams().getBlackList().isEmpty()){
								responseString.append(" Guard Params <b>BlackList</b> is Required " + "<br>");
								valid = false;
							}else{
								for(String blackList: policyData.getYamlparams().getBlackList()){
									if(blackList==null || !(SUCCESS.equals(PolicyUtils.policySpecialCharValidator(blackList)))){
										responseString.append(" Guard Params <b>BlackList</b> Should be valid String" + "<br>");
										valid = false;
										break;
									}
								}
							}
						}
					}
				}
			}

			if(ACTION_POLICY.equalsIgnoreCase(policyData.getPolicyType())){
				if(!Strings.isNullOrEmpty(policyData.getActionPerformer())){
					String actionPerformer = PolicyUtils.policySpecialCharValidator(policyData.getActionPerformer());
					if(!actionPerformer.contains(SUCCESS)){
						responseString.append("ActionPerformer:" +  actionPerformer + "<br>");
						valid = false;
					}
				}else{
					responseString.append("ActionPerformer: ActionPerformer Should not be empty" + "<br>");
					valid = false;
				}
				if(policyData.getAttributes() != null){
					for(Object attribute : policyData.getAttributes()){
						if(attribute instanceof LinkedHashMap<?, ?>){
							try{
								//This is for validation check if the value exists or not
								String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
								String value =  ((LinkedHashMap<?, ?>) attribute).get("value").toString();
								if("".equals(key) || "".equals(value)){
									responseString.append(EMPTY_COMPONENT_ATTR + "<br>");
									valid = false;
									break;	
								}
							}catch(Exception e){
								LOGGER.error("This is a Policy Validation check" +e);
								responseString.append(EMPTY_COMPONENT_ATTR + "<br>");
								valid = false;
								break;
							}
						}
					}
				}else{
					responseString.append(EMPTY_COMPONENT_ATTR + "<br>");
					valid = false;
				}
				if(!Strings.isNullOrEmpty(policyData.getActionAttributeValue())){
					String actionAttribute = PolicyUtils.policySpecialCharValidator(policyData.getActionAttributeValue());
					if(!actionAttribute.contains(SUCCESS)){
						responseString.append("ActionAttribute:" +  actionAttribute + "<br>");
						valid = false;
					};
				}else{
					responseString.append("ActionAttribute: ActionAttribute Should not be empty" + "<br>");
					valid = false;
				}
				
				if(!policyData.getRuleAlgorithmschoices().isEmpty()){
					for(Object attribute : policyData.getRuleAlgorithmschoices()){
						if(attribute instanceof LinkedHashMap<?, ?>){
							try{
								String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
								String key = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
								String rule = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
								String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();

								if(Strings.isNullOrEmpty(label) || Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(rule) || Strings.isNullOrEmpty(value)){
									responseString.append("Rule Algorithms: One or more Fields in Rule Algorithms is Empty." + "<br>");
									valid = false;
								}
							}catch(Exception e){
								LOGGER.error("This is a Policy Validation check" +e);
								responseString.append("Rule Algorithms: One or more Fields in Rule Algorithms is Empty." + "<br>");
								valid = false;
								break;
							}
						}
					}
				}
			}

			if(CONFIG_POLICY.equals(policyData.getPolicyType())){
				String value = "";
				if(valid){
					if(commonClassDao!=null){
						List<Object> spData = commonClassDao.getDataById(SafePolicyWarning.class, "riskType", policyData.getRiskType());
						if (!spData.isEmpty()){
							SafePolicyWarning safePolicyWarningData  = (SafePolicyWarning) spData.get(0);
							value = "Message:" +  safePolicyWarningData.getMessage();
						}
					}
					responseString.append(SUCCESS + "@#"+ value);
				}
			}else{
				if(valid){
					responseString.append(SUCCESS);
				}
			}

			return responseString;
		}
		catch (Exception e){
			LOGGER.error("Exception Occured during Policy Validation" +e);
			return null;
		}
	}

	protected String emailValidation(String email, String response){
		String res = response;
		if(email != null){
			String validateEmail = PolicyUtils.validateEmailAddress(email.replace("\"", ""));
			if(!validateEmail.contains(SUCCESS)){
				res  += "<b>Email</b>:<i>" +  validateEmail + HTML_ITALICS_LNBREAK;
			}
			else {
				return SUCCESS;
			}
		}
		return res;
	}

	private MicroServiceModels getAttributeObject(String name, String version) {	
		MicroServiceModels workingModel = null;
		try{
			List<Object> microServiceModelsData = commonClassDao.getDataById(MicroServiceModels.class, "modelName:version", name+":"+version);
			if(microServiceModelsData != null){
				workingModel = (MicroServiceModels) microServiceModelsData.get(0);
			}
		}catch(Exception e){
			String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Template.  The template name, " 
                    + name + " was not found in the dictionary: ";
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message + e);
			return null;
		}

		return workingModel;
	}

	private void pullJsonKeyPairs(JsonNode rootNode) {
		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();

		while (fieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> field = fieldsIterator.next();
			final String key = field.getKey();
			final JsonNode value = field.getValue();
			if (value.isContainerNode() && !value.isArray()) {
				pullJsonKeyPairs(value); // RECURSIVE CALL
			} else {
				if (value.isArray()){
					String newValue = StringUtils.replaceEach(value.toString(), new String[]{"[", "]", "\""}, new String[]{"", "", ""});
					mapAttribute.put(key, newValue);
				}else {
					mapAttribute.put(key, value.toString().trim());
				}
			}
		}
	}

}
