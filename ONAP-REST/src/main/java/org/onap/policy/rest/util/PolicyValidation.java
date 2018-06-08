/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultBody;
import org.onap.policy.rest.adapter.ClosedLoopPMBody;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.OptimizationModels;
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
	public static final String ENFORCER_CONFIG_POLICY = "Enforcer Config";
	public static final String MICROSERVICES = "Micro Service";
	public static final String FIREWALL = "Firewall Config";
	public static final String OPTIMIZATION="Optimization";
	public static final String BRMSPARAM = "BRMS_Param";
	public static final String BRMSRAW = "BRMS_Raw";
	public static final String HTML_ITALICS_LNBREAK = "</i><br>";
	public static final String SUCCESS = "success";
	public static final String EMPTY_COMPONENT_ATTR = "Component Attributes: One or more Fields in Component Attributes is Empty.";
	public static final String ISREQUIRED = " is required";
	public static final String SPACESINVALIDCHARS = " : value has spaces or invalid characters</i><br>";
	public static final String RULEALGORITHMS = "<b>Rule Algorithms</b>:<i>";
	public static final String VALUE = "value";
	
	private static Map<String, String> mapAttribute = new HashMap<>();
	private static Map<String, String> jsonRequestMap = new HashMap<>();
	private static List<String> modelRequiredFieldsList = new ArrayList<>();
	
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
					responseString.append("<b>PolicyName</b>:<i>" +  policyNameValidate + HTML_ITALICS_LNBREAK);
					valid = false;
				}
			}else{
				responseString.append( "<b>PolicyName</b>: PolicyName Should not be empty" + HTML_ITALICS_LNBREAK);
				valid = false;
			}
			if(policyData.getPolicyDescription() != null){
				String descriptionValidate = PolicyUtils.descriptionValidator(policyData.getPolicyDescription());
				if(!descriptionValidate.contains(SUCCESS)){
					responseString.append("<b>Description</b>:<i>" +  descriptionValidate + HTML_ITALICS_LNBREAK);
					valid = false;
				}	
			}

			if(!"API".equals(policyData.getApiflag()) && policyData.getAttributes() != null && !policyData.getAttributes().isEmpty()){
                for(Object attribute : policyData.getAttributes()){
                    if(attribute instanceof LinkedHashMap<?, ?>){
                        String value = null;
                        String key = null;
                        if(((LinkedHashMap<?, ?>) attribute).get("key") != null){
                            key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
                            if(!PolicyUtils.policySpecialCharValidator(key).contains(SUCCESS)){
                                responseString.append("<b>Attributes or Component Attributes</b>:<i>" +  value + SPACESINVALIDCHARS);
                                valid = false;
                            }
                        }else{
                            if(CONFIG_POLICY.equals(policyData.getPolicyType())){
                                if("Base".equals(policyData.getConfigPolicyType())){
                                    responseString.append("<b>Attributes</b>:<i> has one missing Attribute key</i><br>");
                                }
                                if(BRMSPARAM.equals(policyData.getConfigPolicyType()) || BRMSRAW.equals(policyData.getConfigPolicyType())){
                                    responseString.append("<b>Rule Attributes</b>:<i> has one missing Attribute key</i><br>");
                                }
                            }else{
                                responseString.append("<b>Component Attributes</b>:<i> has one missing Component Attribute key</i><br>");
                            }
                            valid = false;
                        }
                        if(((LinkedHashMap<?, ?>) attribute).get(VALUE) != null){
                            value = ((LinkedHashMap<?, ?>) attribute).get(VALUE).toString();
                            if(!PolicyUtils.policySpecialCharValidator(value).contains(SUCCESS)){
                                if(CONFIG_POLICY.equals(policyData.getPolicyType())){
                                    if("Base".equals(policyData.getConfigPolicyType())){
                                        responseString.append("<b>Attributes</b>:<i>" +  value + SPACESINVALIDCHARS);
                                    }
                                    if(BRMSPARAM.equals(policyData.getConfigPolicyType()) || BRMSRAW.equals(policyData.getConfigPolicyType())){
                                        responseString.append("<b>Rule Attributes</b>:<i>" +  value + SPACESINVALIDCHARS);
                                    }
                                }else{
                                    responseString.append("<b>Component Attributes</b>:<i>" +  value + SPACESINVALIDCHARS);
                                }
                                valid = false;
                            }
                        }else{
                            if(CONFIG_POLICY.equals(policyData.getPolicyType())){
                                if("Base".equals(policyData.getConfigPolicyType())){
                                    responseString.append("<b>Attributes</b>:<i> has one missing Attribute value</i><br>");
                                }
                                if(BRMSPARAM.equals(policyData.getConfigPolicyType()) || BRMSRAW.equals(policyData.getConfigPolicyType())){
                                    responseString.append("<b>Rule Attributes</b>:<i> has one missing Attribute value</i><br>");
                                }
                            }else{
                                responseString.append("<b>Component Attributes</b>:<i> has one missing Component Attribute value</i><br>");
                            }
                            valid = false;
                        }
                    }
                }
            }
			
            //Decision Policy Attributes Validation
            if(!"API".equals(policyData.getApiflag()) && policyData.getSettings() != null && !policyData.getSettings().isEmpty()){
                for(Object attribute : policyData.getAttributes()){
                    if(attribute instanceof LinkedHashMap<?, ?>){
                        String value = null;
                        if(((LinkedHashMap<?, ?>) attribute).get("key") == null){
                            responseString.append("<b>Settings Attributes</b>:<i> has one missing Attribute key</i><br>");
                            valid = false;
                        }
                        if(((LinkedHashMap<?, ?>) attribute).get(VALUE) != null){
                            value = ((LinkedHashMap<?, ?>) attribute).get(VALUE).toString();
                            if(!PolicyUtils.policySpecialCharValidator(value).contains(SUCCESS)){
                                responseString.append("<b>Settings Attributes</b>:<i>" +  value + SPACESINVALIDCHARS);
                                valid = false;
                            }
                        }else{
                            responseString.append("<b>Settings Attributes</b>:<i> has one missing Attribute Value</i><br>");
                            valid = false;
                        }
                    }
                }
            }
            
            if(!"API".equals(policyData.getApiflag()) && policyData.getRuleAlgorithmschoices() != null &&  !policyData.getRuleAlgorithmschoices().isEmpty()){
                for(Object attribute : policyData.getRuleAlgorithmschoices()){
                    if(attribute instanceof LinkedHashMap<?, ?>){
                        String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
                        if(((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1") == null){
                            responseString.append(RULEALGORITHMS +  label + " : Field 1 value is not selected</i><br>");
                            valid = false;
                        }
                        if(((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo") == null){
                            responseString.append(RULEALGORITHMS +  label + " : Field 2 value is not selected</i><br>");
                            valid = false;
                        }
                        if(((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2") != null){
                            String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
                            if(!PolicyUtils.policySpecialCharValidator(value).contains(SUCCESS)){
                                responseString.append(RULEALGORITHMS +  label + " : Field 3 value has special characters</i><br>");
                                valid = false;
                            }
                        }else{
                            responseString.append(RULEALGORITHMS +  label + " : Field 3 value is empty</i><br>");
                            valid = false;
                        }
                    }
                }
            }
            
			if(CONFIG_POLICY.equalsIgnoreCase(policyData.getPolicyType())){
				if ("Base".equals(policyData.getConfigPolicyType()) || CLOSEDLOOP_POLICY.equals(policyData.getConfigPolicyType())
						||  CLOSEDLOOP_PM.equals(policyData.getConfigPolicyType()) || ENFORCER_CONFIG_POLICY.equals(policyData.getConfigPolicyType()) 
						|| MICROSERVICES.equals(policyData.getConfigPolicyType()) || OPTIMIZATION.equals(policyData.getConfigPolicyType())) {
					
					if(!Strings.isNullOrEmpty(policyData.getOnapName())) {
						String onapNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getOnapName());
						if(!onapNameValidate.contains(SUCCESS)){
							responseString.append("<b>OnapName</b>:<i>" +  onapNameValidate + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					}else{
						responseString.append("<b>Onap Name</b>: Onap Name Should not be empty" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}

				if(!Strings.isNullOrEmpty(policyData.getRiskType())) {
					String riskTypeValidate = PolicyUtils.policySpecialCharValidator(policyData.getRiskType());
					if(!riskTypeValidate.contains(SUCCESS)){
						responseString.append("<b>RiskType</b>:<i>" +  riskTypeValidate + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}else {
					responseString.append("<b>RiskType</b>: Risk Type Should not be Empty" + HTML_ITALICS_LNBREAK);
					valid = false;
				}

				if(!Strings.isNullOrEmpty(policyData.getRiskLevel())) {
					String validateRiskLevel = PolicyUtils.policySpecialCharValidator(policyData.getRiskLevel());
					if(!validateRiskLevel.contains(SUCCESS)){
						responseString.append("<b>RiskLevel</b>:<i>" +  validateRiskLevel + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}else {
					responseString.append("<b>RiskLevel</b>: Risk Level Should not be Empty" + HTML_ITALICS_LNBREAK);
					valid = false;
				}

				if(!Strings.isNullOrEmpty(policyData.getGuard())) {
					String validateGuard = PolicyUtils.policySpecialCharValidator(policyData.getGuard());
					if(!validateGuard.contains(SUCCESS)){
						responseString.append("<b>Guard</b>:<i>" +  validateGuard + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}else {
					responseString.append("<b>Guard</b>: Guard Value Should not be Empty" + HTML_ITALICS_LNBREAK);
					valid = false;
				}
				
				// Validate Config Base Policy Data
				if("Base".equalsIgnoreCase(policyData.getConfigPolicyType())){
					if(!Strings.isNullOrEmpty(policyData.getConfigName())) {
						String configNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigName());
						if(!configNameValidate.contains(SUCCESS)){
							responseString.append("ConfigName:" +  configNameValidate + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					}else{
						responseString.append("Config Name: Config Name Should not be Empty" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
					if(!Strings.isNullOrEmpty(policyData.getConfigType())) {
						String configTypeValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigType());
						if(!configTypeValidate.contains(SUCCESS)){
							responseString.append("ConfigType:" +  configTypeValidate + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					}else{
						responseString.append("Config Type: Config Type Should not be Empty" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
					if(!Strings.isNullOrEmpty(policyData.getConfigBodyData())) {
						String configBodyData = policyData.getConfigBodyData();
						String configType = policyData.getConfigType();
						if (configType != null) {
							if ("JSON".equals(configType)) {
								if (!PolicyUtils.isJSONValid(configBodyData)) {
									responseString.append("Config Body: JSON Content is not valid" + HTML_ITALICS_LNBREAK);
									valid = false;
								}
							} else if ("XML".equals(configType)) {
								if (!PolicyUtils.isXMLValid(configBodyData)) {
									responseString.append("Config Body: XML Content data is not valid" + HTML_ITALICS_LNBREAK);
									valid = false;
								}
							} else if ("PROPERTIES".equals(configType)) {
								if (!PolicyUtils.isPropValid(configBodyData) || "".equals(configBodyData)) {
									responseString.append("Config Body: Property data is not valid" + HTML_ITALICS_LNBREAK);
									valid = false;
								} 
							} else if ("OTHER".equals(configType) && ("".equals(configBodyData))) {
								responseString.append("Config Body: Config Body Should not be Empty" + HTML_ITALICS_LNBREAK);
								valid = false;
							}
						}
					}else{
						responseString.append("Config Body: Config Body Should not be Empty" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}
				
				// Validate Config Firewall Policy Data
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
				
				// Validate BRMS_Param Policy Data
				if(BRMSPARAM.equalsIgnoreCase(policyData.getConfigPolicyType()) && Strings.isNullOrEmpty(policyData.getRuleName())){
					responseString.append("<b>BRMS Template</b>:<i>BRMS Template is required" + HTML_ITALICS_LNBREAK);
					valid = false;
				}
				
				// Validate BRMS_Raw Policy Data
				if(BRMSRAW.equalsIgnoreCase(policyData.getConfigPolicyType())){
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
				
				// Validate ClosedLoop_PM Policy Data
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
								responseString.append(result + HTML_ITALICS_LNBREAK);
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
							}
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
									}
								}
							}	
						}
					}else{
						responseString.append("<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}
				
				// Validate ClosedLoop_Fault Policy Data
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
						}else{
							if(policyData.getTrapDatas().getTrap1() != null){
								if(policyData.getClearTimeOut() == null){
									responseString.append("<b>Trigger Clear TimeOut</b>: <i>Trigger Clear TimeOut is required when atleast One Trigger Signature is enabled</i><br>");
									valid = false;
								}
								if(policyData.getTrapMaxAge() == null){
									responseString.append("<b>Trap Max Age</b>: <i>Trap Max Age is required when atleast One Trigger Signature is enabled</i><br>");
									valid = false;
								}
							}
							if(policyData.getFaultDatas().getTrap1() != null && policyData.getVerificationclearTimeOut() == null){
								responseString.append("<b>Fault Clear TimeOut</b>: <i>Fault Clear TimeOut is required when atleast One Fault Signature is enabled</i><br>");
								valid = false;
							}
						}

						ClosedLoopFaultBody faultBody = mapper.readValue(policyData.getJsonBody(), ClosedLoopFaultBody.class);
						if(faultBody.getEmailAddress() != null && !faultBody.getEmailAddress().isEmpty()){
							String result = emailValidation(faultBody.getEmailAddress(), responseString.toString());
							if(!SUCCESS.equals(result)){
								responseString.append(result+ HTML_ITALICS_LNBREAK);
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
				
				// Validate MicroServices Policy Data
				if (MICROSERVICES.equals(policyData.getConfigPolicyType())){
					
					if(!Strings.isNullOrEmpty(policyData.getServiceType())){
						
						modelRequiredFieldsList.clear();
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
							MicroServiceModels returnModel = getMSModelData(service, version);
							
							if(returnModel != null) {
								
								String annotation = returnModel.getAnnotation();
								String refAttributes = returnModel.getRef_attributes();
								String subAttributes = returnModel.getSub_attributes();
								String modelAttributes = returnModel.getAttributes();
								
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
								} else {
									// Validate for configName, location, uuid, and policyScope if no annotations exist for this model
									if(Strings.isNullOrEmpty(policyData.getLocation())){
										responseString.append("<b>Micro Service Model</b>:<i> location is required for this model" + HTML_ITALICS_LNBREAK);
										valid = false;
									}
									
									if(Strings.isNullOrEmpty(policyData.getConfigName())){
										responseString.append("<b>Micro Service Model</b>:<i> configName is required for this model" + HTML_ITALICS_LNBREAK);
										valid = false;
									}	
									
									if(Strings.isNullOrEmpty(policyData.getUuid())){
										responseString.append("<b>Micro Service Model</b>:<i> uuid is required for this model" + HTML_ITALICS_LNBREAK);
										valid = false;
									}	
									
									if(Strings.isNullOrEmpty(policyData.getPolicyScope())){
										responseString.append("<b>Micro Service Model</b>:<i> policyScope is required for this model" + HTML_ITALICS_LNBREAK);
										valid = false;
									}	
								}
								
								// If request comes from the API we need to validate required fields in the Micro Service Model 
								// GUI request are already validated from the SDK-APP
								if("API".equals(policyData.getApiflag())){
									// get list of required fields from the sub_Attributes of the Model
									if(!Strings.isNullOrEmpty(subAttributes)) {
										JsonObject subAttributesJson = stringToJsonObject(subAttributes);
										findRequiredFields(subAttributesJson);
									}
									
									// get list of required fields from the attributes of the Model
									if (!Strings.isNullOrEmpty(modelAttributes)) {
										Map<String, String> modelAttributesMap = null;
										if (",".equals(modelAttributes.substring(modelAttributes.length()-1))) {
											String attributeString = modelAttributes.substring(0, modelAttributes.length()-1);
											modelAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(attributeString);
										} else {
											modelAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(modelAttributes);
										}
										String json = new ObjectMapper().writeValueAsString(modelAttributesMap);
										findRequiredFields(stringToJsonObject(json));
									}
									
									// get list of required fields from the ref_Attributes of the Model
									if (!Strings.isNullOrEmpty(refAttributes)) {
										Map<String, String> refAttributesMap = null;
										if (",".equals(refAttributes.substring(refAttributes.length()-1))) {
											String attributesString = refAttributes.substring(0, refAttributes.length()-1);
											refAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(attributesString);
										} else {
											refAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(modelAttributes);
										}
										String json = new ObjectMapper().writeValueAsString(refAttributesMap);
										findRequiredFields(stringToJsonObject(json));
									}
									
									if (modelRequiredFieldsList!=null || !modelRequiredFieldsList.isEmpty()) {
										// create jsonRequestMap with all json keys and values from request
										JsonNode rootNode = (JsonNode) policyData.getPolicyJSON();
										jsonRequestMap.clear();
										pullModelJsonKeyPairs(rootNode);
										
										// validate if the requiredFields are in the request
										for(String requiredField : modelRequiredFieldsList) {
											if (jsonRequestMap.containsKey(requiredField)) {
												String value = jsonRequestMap.get(requiredField);
												if(Strings.isNullOrEmpty(jsonRequestMap.get(requiredField)) || 
														"\"\"".equals(value) || 
														"".equals(jsonRequestMap.get(requiredField))){
													responseString.append("<b>Micro Service Model</b>:<i> " + requiredField + ISREQUIRED + HTML_ITALICS_LNBREAK);
													valid = false; 
												}
											} else {
												responseString.append("<b>Micro Service Model</b>:<i> " + requiredField + ISREQUIRED + HTML_ITALICS_LNBREAK);
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
							responseString.append("<b>Micro Service Version</b>:<i> Micro Service Version is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					} else {
						responseString.append("<b>Micro Service</b>:<i> Micro Service Model is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}

					if(Strings.isNullOrEmpty(policyData.getPriority())){
						responseString.append("<b>Priority</b>:<i> Priority is required" + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}
				
				// Validate Optimization Policy Data
				if (OPTIMIZATION.equals(policyData.getConfigPolicyType())){
					
					if(!Strings.isNullOrEmpty(policyData.getServiceType())){
						
						modelRequiredFieldsList.clear();
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
							OptimizationModels returnModel = getOptimizationModelData(service, version);
							
							if(returnModel != null) {
								
								String annotation = returnModel.getAnnotation();
								String refAttributes = returnModel.getRefattributes();
								String subAttributes = returnModel.getSubattributes();
								String modelAttributes = returnModel.getAttributes();
								
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
												+ " is required for the Optimization model " + service + HTML_ITALICS_LNBREAK);
												valid = false;
											}

										}
									}
								}
								
								// If request comes from the API we need to validate required fields in the Micro Service Model 
								// GUI request are already validated from the SDK-APP
								if("API".equals(policyData.getApiflag())){
									// get list of required fields from the sub_Attributes of the Model
									if(!Strings.isNullOrEmpty(subAttributes)) {
										JsonObject subAttributesJson = stringToJsonObject(subAttributes);
										findRequiredFields(subAttributesJson);
									}
									
									// get list of required fields from the attributes of the Model
									if (!Strings.isNullOrEmpty(modelAttributes)) {
										Map<String, String> modelAttributesMap = null;
										if (",".equals(modelAttributes.substring(modelAttributes.length()-1))) {
											String attributeString = modelAttributes.substring(0, modelAttributes.length()-1);
											modelAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(attributeString);
										} else {
											modelAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(modelAttributes);
										}
										String json = new ObjectMapper().writeValueAsString(modelAttributesMap);
										findRequiredFields(stringToJsonObject(json));
									}
									
									// get list of required fields from the ref_Attributes of the Model
									if (!Strings.isNullOrEmpty(refAttributes)) {
										Map<String, String> refAttributesMap = null;
										if (",".equals(refAttributes.substring(refAttributes.length()-1))) {
											String attributesString = refAttributes.substring(0, refAttributes.length()-1);
											refAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(attributesString);
										} else {
											refAttributesMap = Splitter.on(",").withKeyValueSeparator("=").split(modelAttributes);
										}
										String json = new ObjectMapper().writeValueAsString(refAttributesMap);
										findRequiredFields(stringToJsonObject(json));
									}
									
									if (modelRequiredFieldsList!=null || !modelRequiredFieldsList.isEmpty()) {
										// create jsonRequestMap with all json keys and values from request
										JsonNode rootNode = (JsonNode) policyData.getPolicyJSON();
										jsonRequestMap.clear();
										pullModelJsonKeyPairs(rootNode);
										
										// validate if the requiredFields are in the request
										for(String requiredField : modelRequiredFieldsList) {
											if (jsonRequestMap.containsKey(requiredField)) {
												String value = jsonRequestMap.get(requiredField);
												if(Strings.isNullOrEmpty(jsonRequestMap.get(requiredField)) || 
														"\"\"".equals(value) || 
														"".equals(jsonRequestMap.get(requiredField))){
													responseString.append("<b>Optimization Service Model</b>:<i> " + requiredField + ISREQUIRED + HTML_ITALICS_LNBREAK);
													valid = false; 
												}
											} else {
												responseString.append("<b>Optimization Service Model</b>:<i> " + requiredField + ISREQUIRED + HTML_ITALICS_LNBREAK);
												valid = false; 
											}
										}
									}
								}								
							} else {
								responseString.append("<b>Optimization Service Model</b>:<i> Invalid Model. The model name, " + service + 
										" of version, " + version + " was not found in the dictionary" + HTML_ITALICS_LNBREAK);
								valid = false;
							}
						} else {
							responseString.append("<b>Optimization Service Version</b>:<i> Optimization Service Version is required" + HTML_ITALICS_LNBREAK);
							valid = false;
						}
					} else {
						responseString.append("<b>Optimization Service</b>:<i> Optimization Service Model is required" + HTML_ITALICS_LNBREAK);
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
						responseString.append("OnapName:" +  onapNameValidate + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}else{
					responseString.append("Onap Name: Onap Name Should not be empty" + HTML_ITALICS_LNBREAK);
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
						responseString.append("<b> Guard Params are Required </b>" + HTML_ITALICS_LNBREAK);
						valid = false;
					}else{
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getActor())){
							responseString.append("Guard Params <b>Actor</b> is Required " + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getRecipe())){
							responseString.append("Guard Params <b>Recipe</b> is Required " + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getGuardActiveStart())){
							responseString.append("Guard Params <b>Guard Active Start</b> is Required " + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if(Strings.isNullOrEmpty(policyData.getYamlparams().getGuardActiveEnd())){
							responseString.append("Guard Params <b>Guard Active End</b> is Required " + HTML_ITALICS_LNBREAK);
							valid = false;
						}
						if("GUARD_YAML".equals(policyData.getRuleProvider())){
							if(Strings.isNullOrEmpty(policyData.getYamlparams().getLimit())){
								responseString.append(" Guard Params <b>Limit</b> is Required " + HTML_ITALICS_LNBREAK);
								valid = false;
							}else if(!PolicyUtils.isInteger(policyData.getYamlparams().getLimit())){
								responseString.append(" Guard Params <b>Limit</b> Should be Integer " + HTML_ITALICS_LNBREAK);
								valid = false;
							}
							if(Strings.isNullOrEmpty(policyData.getYamlparams().getTimeWindow())){
								responseString.append("Guard Params <b>Time Window</b> is Required" + HTML_ITALICS_LNBREAK);
								valid = false;
							}else if(!PolicyUtils.isInteger(policyData.getYamlparams().getTimeWindow())){
								responseString.append(" Guard Params <b>Time Window</b> Should be Integer " + HTML_ITALICS_LNBREAK);
								valid = false;
							}
							if(Strings.isNullOrEmpty(policyData.getYamlparams().getTimeUnits())){
								responseString.append("Guard Params <b>Time Units</b> is Required" + HTML_ITALICS_LNBREAK);
								valid = false;
							}
						}else if("GUARD_BL_YAML".equals(policyData.getRuleProvider())){
							if(policyData.getYamlparams().getBlackList()==null || policyData.getYamlparams().getBlackList().isEmpty()){
								responseString.append(" Guard Params <b>BlackList</b> is Required " + HTML_ITALICS_LNBREAK);
								valid = false;
							}else{
								for(String blackList: policyData.getYamlparams().getBlackList()){
									if(blackList==null || !(SUCCESS.equals(PolicyUtils.policySpecialCharValidator(blackList)))){
										responseString.append(" Guard Params <b>BlackList</b> Should be valid String" + HTML_ITALICS_LNBREAK);
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
						responseString.append("<b>ActionPerformer</b>:<i>" +  actionPerformer + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}else{
					responseString.append("<b>ActionPerformer</b>:<i> ActionPerformer Should not be empty" + HTML_ITALICS_LNBREAK);
					valid = false;
				}
	
				if(!Strings.isNullOrEmpty(policyData.getActionAttributeValue())){
					String actionAttribute = PolicyUtils.policySpecialCharValidator(policyData.getActionAttributeValue());
					if(!actionAttribute.contains(SUCCESS)){
						responseString.append("<b>ActionAttribute</b>:<i>" +  actionAttribute + HTML_ITALICS_LNBREAK);
						valid = false;
					}
				}else{
					responseString.append("<b>ActionAttribute</b>:<i> ActionAttribute Should not be empty" + HTML_ITALICS_LNBREAK);
					valid = false;
				}
			}

			if(CONFIG_POLICY.equals(policyData.getPolicyType())){
				String value = "";
				if(valid){
					if(commonClassDao!=null){
						List<Object> spData = commonClassDao.getDataById(SafePolicyWarning.class, "riskType", policyData.getRiskType());
						if (!spData.isEmpty()){
							SafePolicyWarning safePolicyWarningData  = (SafePolicyWarning) spData.get(0);
							value = "<b>Message</b>:<i>" +  safePolicyWarningData.getMessage() +"</i>";
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

	private MicroServiceModels getMSModelData(String name, String version) {	
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
	
	private OptimizationModels getOptimizationModelData(String name, String version) {	
		OptimizationModels workingModel = null;
		try{
			List<Object> optimizationModelsData = commonClassDao.getDataById(OptimizationModels.class, "modelName:version", name+":"+version);
			if(optimizationModelsData != null){
				workingModel = (OptimizationModels) optimizationModelsData.get(0);
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
	
	private void pullModelJsonKeyPairs(JsonNode rootNode) {
		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
		
		while (fieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> field = fieldsIterator.next();
			final String key = field.getKey();
			final JsonNode value = field.getValue();
			
			if (value.isContainerNode() && !value.isArray()) {
				jsonRequestMap.put(key, "containerNode");
				pullModelJsonKeyPairs(value); // RECURSIVE CALL
			} else if (value.isArray()) {
				try {
					jsonRequestMap.put(key, "array");
					String stringValue = StringUtils.replaceEach(value.toString(), new String[]{"[", "]"}, new String[]{"",""});
					ObjectMapper mapper = new ObjectMapper();
					JsonNode newValue = mapper.readTree(stringValue);
					pullModelJsonKeyPairs(newValue);
				} catch (IOException e) {
					LOGGER.info("PolicyValidation: Exception occurred while mapping string to JsonNode " + e);
				}
			} else {
				jsonRequestMap.put(key, value.toString().trim());
			}
		}				
	}
	
    private JsonObject stringToJsonObject(String value) {
    	try(JsonReader jsonReader = Json.createReader(new StringReader(value))){
            return jsonReader.readObject();
        } catch(JsonException| IllegalStateException e){
            LOGGER.info(XACMLErrorConstants.ERROR_DATA_ISSUE+ "Improper JSON format... may or may not cause issues in validating the policy: " + value, e);
            return null;
        }
    }
    
    private void findRequiredFields(JsonObject json) {

    	for(Entry<String, JsonValue> keyMap : json.entrySet()){
    		Object obj = keyMap.getValue();
    		if(obj instanceof JsonObject){
    			JsonObject jsonObj = (JsonObject)obj;
    			for(Entry<String, JsonValue> jsonMap : jsonObj.entrySet()){
    				if(jsonMap.getValue().toString().contains("required-true")){
    					modelRequiredFieldsList.add(jsonMap.getKey());
    				}
    			}
    		} else {
    			if(keyMap.getValue().toString().contains("required-true")){
    				modelRequiredFieldsList.add(keyMap.getKey());
    			}
    		}
    	}
    	    	
    }

}
