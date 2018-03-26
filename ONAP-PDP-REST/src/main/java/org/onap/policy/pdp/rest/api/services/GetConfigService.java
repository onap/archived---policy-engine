/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.PDPResponse;
import org.onap.policy.pdp.rest.api.models.PolicyConfig;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class GetConfigService {
    private static final Logger LOGGER = FlexLogger.getLogger(GetConfigService.class.getName());
    
    private ConfigRequestParameters configRequestParameters = null;
    private String message = null;
    private HttpStatus responseCode = HttpStatus.BAD_REQUEST;
    private Collection<PolicyConfig> policyConfigs = null;
    private boolean unique = false;
    
    public GetConfigService(ConfigRequestParameters configRequestParameters,
            String requestID){
        this.configRequestParameters = configRequestParameters;
        if(configRequestParameters.getRequestID()==null){
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(),e);
                }
            }else{
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.configRequestParameters.setRequestID(requestUUID);
        }
        policyConfigs = new ArrayList<>();
        try{
            run();
            specialCheck();
        }catch(PolicyConfigException e){
        	LOGGER.error(e);
            PolicyConfig policyConfig = new PolicyConfig();
            policyConfig.setPolicyConfigMessage(e.getMessage());
            policyConfig.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
            policyConfigs.add(policyConfig);
            responseCode = HttpStatus.BAD_REQUEST;
        }
    }
    
    private void specialCheck() {
        if(policyConfigs==null || policyConfigs.isEmpty()){
            responseCode = HttpStatus.BAD_REQUEST;
        }else if(policyConfigs.size()==1){
            for(PolicyConfig policyConfig: policyConfigs){
                if(policyConfig.getPolicyConfigMessage()!=null && policyConfig.getPolicyConfigMessage().contains("PE300")){
                    responseCode = HttpStatus.BAD_REQUEST;
                }
            }
        }else{
            responseCode = HttpStatus.OK;
        }
    }

    private void run() throws PolicyConfigException{
        // getValidation. 
        if(!getValidation()){
            LOGGER.error(message);
            throw new PolicyConfigException(message);
        }
        // Generate Request. 
        String modelString = getModel().toString();
        LOGGER.debug("Generated JSON Request is: " + modelString);
        if(configRequestParameters.getUnique()){
            LOGGER.info("Requested for Unique Result only. ");
            unique = true;
        }
        // Process Result. 
        try {
            PDPServices pdpServices = new PDPServices(); 
            responseCode = HttpStatus.OK;
            policyConfigs = configResult(pdpServices.generateRequest(modelString, configRequestParameters.getRequestID(), unique, false));
            // Filter addition. 
            policyConfigs = filterResults(policyConfigs, configRequestParameters);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            responseCode = HttpStatus.BAD_REQUEST;
            throw new PolicyConfigException(XACMLErrorConstants.ERROR_DATA_ISSUE +e);
        }
    }

    private Collection<PolicyConfig> configResult(
            Collection<PDPResponse> generateRequest) {
        Collection<PolicyConfig> result = new HashSet<>();
        if (generateRequest == null) {
            return null;
        }
        if (!generateRequest.isEmpty()) {
            for (PDPResponse stdStatus : generateRequest) {
                PolicyConfig policyConfig = new PolicyConfig();
                policyConfig.setConfig(stdStatus.getConfig());
                policyConfig.setMatchingConditions(stdStatus.getMatchingConditions());
                policyConfig.setPolicyConfigMessage(stdStatus.getPolicyConfigMessage());
                policyConfig.setPolicyConfigStatus(stdStatus.getPolicyConfigStatus());
                policyConfig.setPolicyName(stdStatus.getPolicyName());
                policyConfig.setPolicyVersion(stdStatus.getPolicyVersion());
                policyConfig.setProperty(stdStatus.getProperty());
                policyConfig.setResponseAttributes(stdStatus.getResponseAttributes());
                policyConfig.setType(stdStatus.getType());
                policyConfig.setPolicyType(getPolicyType(stdStatus.getPolicyName()));
                result.add(policyConfig);
            }
        }
        return result;
    }
    
    // Returns PolicyConfigType based on policyName. 
    private PolicyConfigType getPolicyType(String policyName) {
        if(policyName != null) {
            String name = policyName;
            if(name.endsWith(".xml")){
                name = name.substring(0, name.substring(0, name.lastIndexOf('.')).lastIndexOf('.'));
            }
            name = name.substring(name.lastIndexOf('.')+1);
            PolicyConfigType extType = extendedServices(name);
            if(extType != null) return extType;
            if (name.startsWith("Config_BRMS_Param_")) {
                return PolicyConfigType.BRMS_PARAM;
            } else if(name.startsWith("Config_BRMS_Raw_")) {
                return PolicyConfigType.BRMS_RAW;
            } else if(name.startsWith("Config_Fault_")) {
                return PolicyConfigType.ClosedLoop_Fault;
            } else if(name.startsWith("Config_FW_")) {
                return PolicyConfigType.Firewall;
            } else if(name.startsWith("Config_PM_")) {
                return PolicyConfigType.ClosedLoop_PM;
            } else if(name.startsWith("Config_MS_")) {
                return PolicyConfigType.MicroService;
            } else if(name.startsWith("Config_OOF_")) {
            	return PolicyConfigType.Optimization;
            } else if(name.startsWith("Config_")) {
                return PolicyConfigType.Base;
            }
        }
        return null;
    }

    public PolicyConfigType extendedServices(String policyName) {
        // For extended services policyName will be required. 
        return null;
    }

    // Filter logic required for results comparing with requests. 
    private Collection<PolicyConfig> filterResults(
            Collection<PolicyConfig> policyConfigs,
            ConfigRequestParameters configRequestParameters) {
        List<PolicyConfig> policyConfig = new ArrayList<>();
        for(PolicyConfig config: policyConfigs){
            if(config.getPolicyName()!=null && configRequestParameters.getPolicyName()!=null && configRequestParameters.getPolicyName().trim().length()>0){
                if(!config.getPolicyName().matches(configRequestParameters.getPolicyName())){
                    continue;
                }
            }
            if(config.getMatchingConditions()!=null && config.getMatchingConditions().size()>0){
                if(configRequestParameters.getOnapName()!=null && configRequestParameters.getOnapName().trim().length()>0 && config.getMatchingConditions().containsKey("ONAPName")){
                    if(!config.getMatchingConditions().get("ONAPName").matches(configRequestParameters.getOnapName())){
                        continue;
                    }
                }
                if(configRequestParameters.getConfigName()!=null && configRequestParameters.getConfigName().trim().length()>0 && config.getMatchingConditions().containsKey("ConfigName")){
                    if(!config.getMatchingConditions().get("ConfigName").matches(configRequestParameters.getConfigName())){
                        continue;
                    }
                }
                if(configRequestParameters.getConfigAttributes()!=null && configRequestParameters.getConfigAttributes().size()>0){
                    boolean flag = false; 
                    for(String key: configRequestParameters.getConfigAttributes().keySet()){
                    	if(key.equals("RiskType")||key.equals("RiskLevel")||key.equals("guard")||key.equals("TTLDate")){
                    		continue;
                    	}
                        if(config.getMatchingConditions().containsKey(key)){
                        	if(config.getMatchingConditions().get(key).contains(",")){
                        		List<String> elements = Arrays.asList(config.getMatchingConditions().get(key).split(","));
                        		if(!elements.contains(configRequestParameters.getConfigAttributes().get(key))){
                        			flag=true;
                        		}
                        	}else if(!config.getMatchingConditions().get(key).matches(configRequestParameters.getConfigAttributes().get(key))){
                            	flag = true;
                        	}
                        }else{
                        	flag = true;
                        }
                        if(flag){
                        	break;
                        }
                    }
                    if(flag){
                        continue;
                    }
                }
            }
            policyConfig.add(config);
        }
        if(policyConfig.isEmpty()){
            PolicyConfig pConfig = new PolicyConfig();
            pConfig.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
            pConfig.setPolicyConfigMessage(XACMLErrorConstants.ERROR_DATA_ISSUE+"No Match Found, for the parameters sent.");
            policyConfig.add(pConfig);
        }
        return policyConfig;
    }

    private JsonObject getModel() throws PolicyConfigException{
        String policyName = configRequestParameters.getPolicyName();
        if(policyName!= null && !policyName.trim().isEmpty() && !policyName.endsWith("xml")){
            policyName = policyName + ".[\\d].*";
            configRequestParameters.setPolicyName(policyName);
        }
        JsonArrayBuilder subjectArray = Json.createArrayBuilder();
        JsonArrayBuilder resourceArray = Json.createArrayBuilder();
        if(configRequestParameters.getPolicyName()!=null){
            JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
            subjectBuilder.add("Value", policyName);
            subjectBuilder.add("AttributeId", "PolicyName");
            subjectArray.add(subjectBuilder);
        }else{
            LOGGER.info("PolicyName values are not given. ");
        }
        if(configRequestParameters.getOnapName()!=null){
            JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
            subjectBuilder.add("Value", configRequestParameters.getOnapName());
            subjectBuilder.add("AttributeId", "ONAPName");
            subjectArray.add(subjectBuilder);
            if(configRequestParameters.getConfigName()!=null){
                subjectBuilder = Json.createObjectBuilder();
                subjectBuilder.add("Value", configRequestParameters.getConfigName());
                subjectBuilder.add("AttributeId", "ConfigName");
                subjectArray.add(subjectBuilder);
                resourceArray = getResourceArray(configRequestParameters.getConfigAttributes());
            }else{
                LOGGER.info("Config Name is not given. ");
            }
        }else{
            LOGGER.info("Onap Name is not given. ");
        }
        return Json.createObjectBuilder()
                .add("Request",Json.createObjectBuilder()
                                .add("AccessSubject",Json.createObjectBuilder()
                                                .add("Attribute",subjectArray))
                                .add("Action",Json.createObjectBuilder()
                                                .add("Attribute",Json.createObjectBuilder()
                                                                .add("Value","ACCESS")
                                                                .add("AttributeId","urn:oasis:names:tc:xacml:1.0:action:action-id")))
                                .add("Resource",Json.createObjectBuilder()
                                                .add("Attribute",resourceArray
                                                                .add(Json.createObjectBuilder()
                                                                        .add("Value","Config")
                                                                        .add("AttributeId","urn:oasis:names:tc:xacml:1.0:resource:resource-id")))))
                .build();
    }
    
    private JsonArrayBuilder getResourceArray(Map<String, String> configAttributes) throws PolicyConfigException{
        JsonArrayBuilder resourceArray = Json.createArrayBuilder();
        configAttributes = configRequestParameters.getConfigAttributes();
        if (configAttributes != null && !configAttributes.isEmpty()) {
            if(!configAttributes.containsKey("RiskType")){
                configAttributes.put("RiskType", ".*");
            }
            if(!configAttributes.containsKey("RiskLevel")){
                configAttributes.put("RiskLevel", ".*");
            }
            if(!configAttributes.containsKey("guard")){
                configAttributes.put("guard", ".*");
            }
            if(!configAttributes.containsKey("TTLDate")){
                configAttributes.put("TTLDate", ".*");
            }
        }else{
            // ConfigAttributes is Null. So add basic values.
            configAttributes = new HashMap<>();
            configAttributes.put("RiskType", ".*");
            configAttributes.put("RiskLevel", ".*");
            configAttributes.put("guard", ".*");
            configAttributes.put("TTLDate", ".*");
        }
        for (String key : configAttributes.keySet()) {
            if (key.isEmpty()) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot have an Empty Key";
                LOGGER.error(message);
                throw new PolicyConfigException(message);
            }
            JsonObjectBuilder resourceBuilder = Json.createObjectBuilder();
            resourceBuilder.add("Value", configAttributes.get(key));
            resourceBuilder.add("AttributeId", key);
            resourceArray.add(resourceBuilder);
        }
        return resourceArray;
    }
    
    private Boolean getValidation(){
        if(configRequestParameters==null){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No config Request Parameters given.";
            return false;
        }
        if(configRequestParameters.getOnapName()==null && configRequestParameters.getPolicyName()==null){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot proceed without onapName or PolicyName";
            return false;
        }
        return true;
    }

    public Collection<PolicyConfig> getResult() {
        return policyConfigs;
    }

    public HttpStatus getResponseCode() {
        return responseCode;
    }
}