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
package org.onap.policy.pdp.rest.api.services;

import javax.json.JsonException;
import javax.json.JsonObject;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;

/**
 * Optimization Policy implementation. 
 * 
 * @version 0.1
 */
public class OptimizationPolicyService{
    private static final Logger LOGGER = FlexLogger.getLogger(OptimizationPolicyService.class.getName());

    private PAPServices papServices = null;
    private PolicyParameters policyParameters = null;
    private String message = null;
    private String policyName = null;
    private String policyScope = null;
    private String date = null;

    public OptimizationPolicyService(String policyName, String policyScope, PolicyParameters policyParameters, String date) {
        this.policyParameters = policyParameters;
        this.policyName = policyName;
        this.policyScope = policyScope;
        this.date = date;
        papServices = new PAPServices();
    }

    public String getMessage() {
        return message;
    }

    public String getResult(boolean updateFlag) throws PolicyException{
        String response = null;
        String operation = null;
        
        if (updateFlag){
            operation = "update";
        } else {
            operation = "create";
        }
        
        // get values and attributes from the JsonObject
        String servicModel = null;
        String policyDescription=null;
        String priority=null;
        String version=null;
        
        String onapName = policyParameters.getOnapName();
        JsonObject optimizationAttributes = null;
        try{
            optimizationAttributes = PolicyApiUtils.stringToJsonObject(policyParameters.getConfigBody());
        } catch(JsonException| IllegalStateException e){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
            LOGGER.error("Error while parsing JSON body for MicroService Policy creation. ", e);
            return null;
        }

        if (optimizationAttributes.get("service")!=null){
            servicModel = optimizationAttributes.get("service").toString().replace("\"", "");
        }
        if(optimizationAttributes.containsKey("description")){
            policyDescription = optimizationAttributes.get("description").toString().replace("\"", "");
        }
        if(optimizationAttributes.containsKey("priority")){
            priority = optimizationAttributes.get("priority").toString().replace("\"", "");
        }
        if(optimizationAttributes.containsKey("version")){
            version = optimizationAttributes.get("version").toString().replace("\"", "");
        }
        
        // Create Policy Object 
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Optimization", policyName, policyDescription, onapName, 
                null, servicModel, null, null, optimizationAttributes.toString(), priority, 
                version, updateFlag, policyScope, 0, policyParameters.getRiskLevel(),
                policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date); 
        
        // Send JSON Object to PAP 
        response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, 
                policyParameters.getRequestID(), "ConfigOptimization");
        LOGGER.info("Response: " + response);
        return response;
    }
}
