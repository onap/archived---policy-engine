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
 */package org.onap.policy.rest.util;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.adapter.RainyDayParams;
import org.onap.policy.rest.adapter.YAMLParams;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public class PolicyValidationRequestWrapper {
	
	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyValidationRequestWrapper.class);
	public static final String CONFIG_NAME="configName";

	public PolicyRestAdapter populateRequestParameters(HttpServletRequest request) {
		
		PolicyRestAdapter policyData = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			policyData = mapper.readValue(root.get("policyData").toString(), PolicyRestAdapter.class);
			
			JsonObject json = null;
			json = stringToJsonObject(root.toString());
			
			if(json != null){
				if(json.containsKey("policyJSON")){
					policyData.setPolicyJSON(root.get("policyJSON"));
				}else{
					String jsonBodyData = json.getJsonObject("policyData").get("jsonBodyData").toString();
					policyData.setJsonBody(jsonBodyData);
				}
			}			
						
		} catch (Exception e) {
			LOGGER.error("Exception Occured while populating request parameters: " +e);
		}
		
		return policyData;
	}
	
	public PolicyRestAdapter populateRequestParameters(PolicyParameters parameters) {
		
		PolicyRestAdapter policyData = new PolicyRestAdapter();
        
		/*
		 * set policy adapter values for Building JSON object containing policy data
		 */
		//Common among policy types
		policyData.setPolicyName(parameters.getPolicyName());
		policyData.setOnapName(parameters.getOnapName()); 
		
		//Some policies require jsonObject conversion from String for configBody (i.e. MicroService and Firewall)
		JsonObject json = null;
        try{
        	if(parameters.getConfigBody()!= null){
        		json = stringToJsonObject(parameters.getConfigBody());
        	}
        } catch(JsonException| IllegalStateException e){
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + parameters.getConfigBody();
            LOGGER.error(message, e);
            return null;
        }
        
		if(parameters.getPolicyClass()!=null && !"Config".equals(parameters.getPolicyClass().toString())){
			
			policyData.setPolicyType(parameters.getPolicyClass().toString());
			
			//Get Matching attribute values
			Map<AttributeType, Map<String, String>> attributes = parameters.getAttributes();
			Map<String, String> matching = null;
			if(attributes != null){
				matching = attributes.get(AttributeType.MATCHING);
			}
			
			if("Decision".equals(parameters.getPolicyClass().toString())){
				
				String ruleProvider = parameters.getRuleProvider().toString();
				policyData.setRuleProvider(ruleProvider);
				
				if("Rainy_Day".equals(ruleProvider)){
					
					// Set Matching attributes in RainyDayParams in adapter
					RainyDayParams rainyday = new RainyDayParams();
					
					if(matching != null) {
						rainyday.setServiceType(matching.get("ServiceType"));
						rainyday.setVnfType(matching.get("VNFType"));
						rainyday.setBbid(matching.get("BB_ID"));
						rainyday.setWorkstep(matching.get("WorkStep"));
					}

					Map<String, String> treatments = parameters.getTreatments();
					ArrayList<Object> treatmentsTableChoices = new ArrayList<>();
					
					for (String keyField : treatments.keySet()) {
						LinkedHashMap<String, String> treatmentMap = new LinkedHashMap<>();
						String errorcode = keyField;
						String treatment = treatments.get(errorcode);
						treatmentMap.put("errorcode", errorcode);
						treatmentMap.put("treatment", treatment);
						treatmentsTableChoices.add(treatmentMap);
					}
					rainyday.setTreatmentTableChoices(treatmentsTableChoices);
					policyData.setRainyday(rainyday);
					
				}else if("GUARD_YAML".equals(ruleProvider) || "GUARD_BL_YAML".equals(ruleProvider)) {
					
					// Set Matching attributes in YAMLParams in adapter
					YAMLParams yamlparams = new YAMLParams();
					
					if (matching != null) {
						yamlparams.setActor(matching.get("actor"));
						yamlparams.setRecipe(matching.get("recipe"));
						yamlparams.setGuardActiveStart(matching.get("guardActiveStart"));
						yamlparams.setGuardActiveEnd(matching.get("guardActiveEnd"));
						
						if("GUARD_YAML".equals(ruleProvider)){
							yamlparams.setLimit(matching.get("limit"));
							yamlparams.setTimeWindow(matching.get("timeWindow"));
							yamlparams.setTimeUnits(matching.get("timeUnits"));	
						}else{
							
							List<String> blackList = new ArrayList<>();

							if(!Strings.isNullOrEmpty(matching.get("blackList"))){
								String[] blackListArray = matching.get("blackList").split(",");
								for(String element : blackListArray){
									blackList.add(element);
								}					
							}	
							
							yamlparams.setBlackList(blackList);

						}					
					}
					policyData.setYamlparams(yamlparams);
				}
				
			} else if("Action".equals(parameters.getPolicyClass().toString())){
				
				ArrayList<Object> ruleAlgorithmChoices = new ArrayList<Object>();
								
				List<String> dynamicLabelRuleAlgorithms = parameters.getDynamicRuleAlgorithmLabels();
				List<String> dynamicFieldFunctionRuleAlgorithms = parameters.getDynamicRuleAlgorithmFunctions();
				List<String> dynamicFieldOneRuleAlgorithms = parameters.getDynamicRuleAlgorithmField1();
				List<String> dyrnamicFieldTwoRuleAlgorithms = parameters.getDynamicRuleAlgorithmField2();
	            
				if (dynamicLabelRuleAlgorithms != null && dynamicLabelRuleAlgorithms.size() > 0) {
	                int i = dynamicLabelRuleAlgorithms.size() - 1;

	                for (String labelAttr : dynamicLabelRuleAlgorithms) {
	    				LinkedHashMap<String, String> ruleAlgorithm = new LinkedHashMap<>();

	                	String id = dynamicLabelRuleAlgorithms.get(i);
	                	String dynamicRuleAlgorithmField1 = dynamicFieldOneRuleAlgorithms.get(i);
	                	String dynamicRuleAlgorithmCombo = dynamicFieldFunctionRuleAlgorithms.get(i);
	                	String dynamicRuleAlgorithmField2 = dyrnamicFieldTwoRuleAlgorithms.get(i);
	                	
	                	ruleAlgorithm.put("id", id);
	                	ruleAlgorithm.put("dynamicRuleAlgorithmField1", dynamicRuleAlgorithmField1);
	                	ruleAlgorithm.put("dynamicRuleAlgorithmCombo", dynamicRuleAlgorithmCombo);
	                	ruleAlgorithm.put("dynamicRuleAlgorithmField2", dynamicRuleAlgorithmField2);
	                	
	                	ruleAlgorithmChoices.add(ruleAlgorithm);
	                	
	                	i--;
	                	
	                }
	            }
	            
	            policyData.setRuleAlgorithmschoices(ruleAlgorithmChoices);
	            
	            ArrayList<Object> attributeList = new ArrayList<>();
	            if (matching != null) {
		            for (String keyField : matching.keySet()) {
						LinkedHashMap<String, String> attributeMap = new LinkedHashMap<>();
						String key = keyField;
						String value = matching.get(keyField);
						attributeMap.put("key", key);
						attributeMap.put("value", value);
						attributeList.add(attributeMap);
					}	
	            }
	            
	            policyData.setAttributes(attributeList);	    
	            policyData.setActionAttributeValue(parameters.getActionAttribute());
	            policyData.setActionPerformer(parameters.getActionPerformer());
				
			}
		}else {
			
			policyData.setPolicyType("Config");
			policyData.setConfigPolicyType(parameters.getPolicyConfigType().toString());
			
			//Config Specific
			policyData.setConfigBodyData(parameters.getConfigBody()); //Base
			policyData.setConfigType((parameters.getConfigBodyType()!=null) ? parameters.getConfigBodyType().toString().toUpperCase(): null);  //Base
			
			if("FW".equalsIgnoreCase(parameters.getPolicyConfigType().toString())){
				
				policyData.setConfigPolicyType("Firewall Config"); 

		        // get values and attributes from the JsonObject
				if(json != null){
			        if (json.get("securityZoneId")!=null){
			        	String securityZone = json.get("securityZoneId").toString().replace("\"", "");
			        	policyData.setSecurityZone(securityZone);
			        }
			        if (json.get(CONFIG_NAME)!=null){
			            String configName = json.get(CONFIG_NAME).toString().replace("\"", "");
			            policyData.setConfigName(configName);
			        }
				}
								
			}else if("MS".equals(parameters.getPolicyConfigType().toString())){
				
				policyData.setConfigPolicyType("Micro Service");
				
		        // get values and attributes from the JsonObject
				if(json != null){
					if (json.containsKey("content")){
						String content = json.get("content").toString();
						ObjectMapper mapper = new ObjectMapper();
						JsonNode policyJSON = null;
						try {
							policyJSON = mapper.readTree(content);
						} catch (IOException e) {
				            String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + parameters.getConfigBody();
				            LOGGER.error(message, e);
				            return null;					
				        }
						policyData.setPolicyJSON(policyJSON);
					}
			        if (json.containsKey("service")){
			        	String serviceType = json.get("service").toString().replace("\"", "");
			        	policyData.setServiceType(serviceType);
			        }
			        if (json.containsKey("uuid")){
			            String uuid = json.get("uuid").toString().replace("\"", "");
			            policyData.setUuid(uuid);
			        }
			        if (json.containsKey("location")){
			            String msLocation = json.get("location").toString().replace("\"", "");
			            policyData.setMsLocation(msLocation);
			        }
			        if (json.containsKey(CONFIG_NAME)){
			            String configName = json.get(CONFIG_NAME).toString().replace("\"", "");
			            policyData.setConfigName(configName);
			        }
			        if(json.containsKey("priority")){
			        	String priority = json.get("priority").toString().replace("\"", "");
			        	policyData.setPriority(priority);
			        }
			        if(json.containsKey("version")){
			        	String version = json.get("version").toString().replace("\"", "");
			        	policyData.setVersion(version);
			        }
				}
				
			} else if("Fault".equals(parameters.getPolicyConfigType().toString())){
				
				policyData.setConfigPolicyType("ClosedLoop_Fault");
				policyData.setApiflag("API");
				
				if(json != null){
					policyData.setJsonBody(json.toString());
			        if (json.get("onapname")!=null){
			        	String onapName = json.get("onapname").toString().replace("\"", "");
			        	policyData.setOnapName(onapName);
			        }
				}
				
			} else if("PM".equals(parameters.getPolicyConfigType().toString())){
				
				policyData.setConfigPolicyType("ClosedLoop_PM");
				
				if(json != null){
					policyData.setJsonBody(json.toString());
			        if (json.get("onapname")!=null){
			        	String onapName = json.get("onapname").toString().replace("\"", "");
			        	policyData.setOnapName(onapName);
			        }
			        if (json.get("serviceTypePolicyName")!=null){
			        	String serviceType = json.get("serviceTypePolicyName").toString().replace("\"", "");
						LinkedHashMap<String, String> serviceTypePolicyName = new LinkedHashMap<>();
						serviceTypePolicyName.put("serviceTypePolicyName", serviceType);
			        	policyData.setServiceTypePolicyName(serviceTypePolicyName);
			        }
				}
			} else if("BRMS_Param".equals(parameters.getPolicyConfigType().toString())){
				Map<AttributeType, Map<String, String>> drlRuleAndUIParams = parameters.getAttributes();
				Map<String, String> rule = drlRuleAndUIParams.get(AttributeType.RULE);
				policyData.setRuleName(rule.get("templateName"));
				
			}
		}
		
		policyData.setPriority(parameters.getPriority()); //Micro Service
		policyData.setConfigName(parameters.getConfigName());  //Base and Firewall
		policyData.setRiskType(parameters.getRiskType()); //Safe parameters Attributes
		policyData.setRiskLevel(parameters.getRiskLevel());//Safe parameters Attributes
		policyData.setGuard(String.valueOf(parameters.getGuard()));//Safe parameters Attributes
		policyData.setTtlDate(convertDate(parameters.getTtlDate()));//Safe parameters Attributes

		return policyData;
				
	}
	
    private JsonObject stringToJsonObject(String value)
            throws JsonException, IllegalStateException {
    	
    	try{
            JsonReader jsonReader = Json.createReader(new StringReader(value));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();
            return object;
        } catch(JsonException| IllegalStateException e){
            LOGGER.info(XACMLErrorConstants.ERROR_DATA_ISSUE+ "Improper JSON format... may or may not cause issues in validating the policy: " + value, e);
            return null;
        }
    }
    
    private String convertDate(Date date) {
        String strDate = null;
        if (date!=null) {
            SimpleDateFormat dateformatJava = new SimpleDateFormat("dd-MM-yyyy");
            strDate = dateformatJava.format(date);
        }
        return (strDate==null) ? "NA": strDate;
    }

}
