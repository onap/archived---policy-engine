/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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
package org.openecomp.policy.pdp.rest.api.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.openecomp.policy.api.PolicyException;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class CreateUpdatePolicyServiceImpl implements CreateUpdatePolicyService {
	private static Logger LOGGER = FlexLogger.getLogger(CreateUpdatePolicyService.class.getName());
    
    private String policyResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private Boolean updateFlag = false;
    private String message = null;
    private PolicyParameters policyParameters = new PolicyParameters();
    private String policyName = null;
    private String policyScope = null;
    private String date = null;
    
	public CreateUpdatePolicyServiceImpl(PolicyParameters policyParameters,
			String requestID, boolean updateFlag) {
		this.updateFlag = updateFlag;
        this.policyParameters = policyParameters;
        if(policyParameters.getRequestID()==null){
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString());
                }
            }else{
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.policyParameters.setRequestID(requestUUID);
        }
        try{
            run();
            specialCheck();
        }catch(PolicyException e){
            policyResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    public void run() throws PolicyException{
        // Check Validation. 
        if(!getValidation()){
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Get Result. 
        try{
            status = HttpStatus.OK;
            policyResult = processResult();
        }catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }
    
    @SuppressWarnings("incomplete-switch")
    public String processResult() throws PolicyException{
        String response = null;
        if(policyParameters.getPolicyConfigType()!=null){
            // This is a Config Type Policy. 
            switch(policyParameters.getPolicyConfigType()){
            case BRMS_PARAM:
                BRMSParamPolicyService bRMSParamPolicyService = new BRMSParamPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!bRMSParamPolicyService.getValidation()){
                    LOGGER.error(bRMSParamPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return bRMSParamPolicyService.getMessage();
                }
                // Get Result. 
                response = bRMSParamPolicyService.getResult(updateFlag);
                break;
            case BRMS_RAW:
                BRMSRawPolicyService bRMSRawPolicyService = new BRMSRawPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!bRMSRawPolicyService.getValidation()){
                    LOGGER.error(bRMSRawPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return bRMSRawPolicyService.getMessage();
                }
                // Get Result. 
                response = bRMSRawPolicyService.getResult(updateFlag);
                break;
            case Base:
                ConfigPolicyService configPolicyService = new ConfigPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!configPolicyService.getValidation()){
                    LOGGER.error(configPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return configPolicyService.getMessage();
                }
                // Get Result. 
                response = configPolicyService.getResult(updateFlag);
                break;
            case ClosedLoop_Fault:
                ClosedLoopFaultPolicyService closedLoopFaultPolicyService = new ClosedLoopFaultPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!closedLoopFaultPolicyService.getValidation()){
                    LOGGER.error(closedLoopFaultPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return closedLoopFaultPolicyService.getMessage();
                }
                // Get Result. 
                response = closedLoopFaultPolicyService.getResult(updateFlag);
                break;
            case ClosedLoop_PM:
                ClosedLoopPMPolicyService closedLoopPMPolicyService = new ClosedLoopPMPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!closedLoopPMPolicyService.getValidation()){
                    LOGGER.error(closedLoopPMPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return closedLoopPMPolicyService.getMessage();
                }
                // Get Result. 
                response = closedLoopPMPolicyService.getResult(updateFlag);
                break;
            case Firewall:
                FirewallPolicyService firewallPolicyService = new FirewallPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!firewallPolicyService.getValidation()){
                    LOGGER.error(firewallPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return firewallPolicyService.getMessage();
                }
                // Get Result. 
                response = firewallPolicyService.getResult(updateFlag);
                break;
            case MicroService:
                MicroServicesPolicyService microServicesPolicyService = new MicroServicesPolicyService(policyName, policyScope, policyParameters, date);
                // Check Validation. 
                if(!microServicesPolicyService.getValidation()){
                    LOGGER.error(microServicesPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return microServicesPolicyService.getMessage();
                }
                // Get Result. 
                response = microServicesPolicyService.getResult(updateFlag);
                break;
            default:
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " Invalid Config Type Present";
                LOGGER.error(message);
                status = HttpStatus.BAD_REQUEST;
                return message;
            }
        }else if (policyParameters.getPolicyClass()!=null){
            switch (policyParameters.getPolicyClass()){
            case Action:
                ActionPolicyService actionPolicyService = new ActionPolicyService(policyScope, policyName, policyParameters);
                // Check Validation. 
                if(!actionPolicyService.getValidation()){
                    LOGGER.error(actionPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return actionPolicyService.getMessage();
                }
                // Get Result. 
                response = actionPolicyService.getResult(updateFlag);
                break;
            case Decision:
                DecisionPolicyService decisionPolicyService = new DecisionPolicyService(policyScope, policyName, policyParameters);
                // Check Validation. 
                if(!decisionPolicyService.getValidation()){
                    LOGGER.error(decisionPolicyService.getMessage());
                    status = HttpStatus.BAD_REQUEST;
                    return decisionPolicyService.getMessage();
                }
                // Get Result. 
                response = decisionPolicyService.getResult(updateFlag);
                break;
            }
        }else {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Class found.";
            LOGGER.error(message);
            status = HttpStatus.BAD_REQUEST;
            response = message;
        }
        return response;
    }

    protected boolean getValidation() {
        if(policyParameters == null){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy parameters given. ";
            return false;
        }
        if(policyParameters.getPolicyName() == null){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            return false;
        }
        if (policyParameters.getPolicyName().contains(".")) {
            policyName = policyParameters.getPolicyName().substring(policyParameters.getPolicyName().lastIndexOf(".") + 1,
                    policyParameters.getPolicyName().length());
            policyScope = policyParameters.getPolicyName().substring(0,policyParameters.getPolicyName().lastIndexOf("."));
            LOGGER.info("Name is " + policyName + "   scope is " + policyScope);
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            return false;
        }
        if (policyName==null||policyName.trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            return false;
        
        }
        message = PolicyUtils.emptyPolicyValidator(policyScope);
        if(!message.contains("success")){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ message;
            return false;
        }
        message = PolicyUtils.emptyPolicyValidator(policyName);
        if(!message.contains("success")){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ message;
            return false;
        }
        if(policyParameters.getPolicyDescription()!=null){
            message = PolicyUtils.descriptionValidator(policyParameters.getPolicyDescription());
            if(!message.contains("success")){
                message = XACMLErrorConstants.ERROR_DATA_ISSUE+ message;
                return false;
            }
        }
        if(!PolicyApiUtils.validateNONASCIICharactersAndAllowSpaces(policyParameters.toString())){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "This requests contains Non ASCII Characters. Please review your input parameter"
                    + " values and correct the illegal characters.";
            return false;
        }
        // Set some default Values. 
        if (policyParameters.getTtlDate()!=null){
            date = convertDate(policyParameters.getTtlDate());
        }
        return true;
    }
    
    protected String convertDate(Date date) {
        String strDate = null;
        if (date!=null) {
            SimpleDateFormat dateformatJava = new SimpleDateFormat("dd-MM-yyyy");
            strDate = dateformatJava.format(date);
        }
        return (strDate==null) ? "NA": strDate;
    }

    protected void specialCheck() {
        if(policyResult== null || policyResult.contains("BAD REQUEST")||policyResult.contains("PE300")){
            status = HttpStatus.BAD_REQUEST;
        } else if (policyResult.contains("Policy Exist Error")) {
            status = HttpStatus.CONFLICT;
        } else if (policyResult.contains("PE200")||policyResult.contains("PE900")){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public String getResult() {
        return policyResult;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
