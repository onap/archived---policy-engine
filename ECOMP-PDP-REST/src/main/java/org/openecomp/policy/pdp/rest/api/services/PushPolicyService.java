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

import java.util.UUID;

import org.openecomp.policy.api.PolicyException;
import org.openecomp.policy.api.PushPolicyParameters;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.std.pap.StdPAPPolicy;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.http.HttpStatus;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;

public class PushPolicyService {
    private static Logger LOGGER = FlexLogger.getLogger(PushPolicyService.class.getName());
    
    private String pushResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private PushPolicyParameters pushPolicyParameters = null;
    private String message = null;
    private String policyName = null;
    private String policyScope = null;
    private String pdpGroup = null;
    private String policyType = null;
    private String filePrefix = null;
    private String clientScope = null; 

    public PushPolicyService(PushPolicyParameters pushPolicyParameters,
            String requestID) {
        this.pushPolicyParameters = pushPolicyParameters;
        if(pushPolicyParameters.getRequestID()==null){
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
            this.pushPolicyParameters.setRequestID(requestUUID);
        }
        try{
            run();
            specialCheck();
        }catch(PolicyException e){
            pushResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void specialCheck() {
        if(pushResult.contains("BAD REQUEST") || pushResult.contains("PE300")){
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyException{
        // Check Validation. 
        if(!getValidation()){
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Process Results. 
        try{
            status = HttpStatus.OK;
            pushResult = processResult();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    private String processResult() throws PolicyException{
        PAPServices papServices = new PAPServices();
        String response = null;
        StdPDPPolicy selectedPolicy = papServices.pushPolicy(policyScope, filePrefix, policyName, clientScope, pdpGroup, pushPolicyParameters.getRequestID());
        if(selectedPolicy==null){
        	 response = XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
                     + "This indicates a problem with getting the version from the PAP or the policy does not exist.";
             LOGGER.error(response);
             return response;
        }
        /* //String activeVersion = papServices.getActiveVersion(policyScope, filePrefix, policyName, clientScope, pushPolicyParameters.getRequestID());
        LOGGER.debug("The active version of " + policyScope + File.separator + filePrefix + policyName + " is " + activeVersion);
        String id = null;
        if ("pe100".equalsIgnoreCase(activeVersion)) {
            response = XACMLErrorConstants.ERROR_PERMISSIONS + "response code of the URL is 403. PEP is not Authorized for making this Request!! "
                    + "Contact Administrator for this Scope. "; 
            LOGGER.error(response);
            return response;
        } else if ("pe300".equalsIgnoreCase(activeVersion)) {
            response = XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
                    + "This indicates a problem with getting the version from the PAP or the policy does not exist.";
            LOGGER.error(response);
            return response;
        }
        if (!activeVersion.equalsIgnoreCase("0")) {
            id = policyScope + "." + filePrefix + policyName + "." + activeVersion + ".xml";
            LOGGER.debug("The policyId is " + id);
        } else {
            response = XACMLErrorConstants.ERROR_DATA_ISSUE + "could not retrieve the activeVersion for this policy.  "
                    + "This indicates the policy does not exist, please verify the policy exists."; 
            LOGGER.error(response);
            return response;
        }
        StdPDPPolicy selectedPolicy = papServices.getGitPath(policyScope, filePrefix, policyName, activeVersion, clientScope, pushPolicyParameters.getRequestID(), id);
        */
        try {
            LOGGER.debug("StdPDPPolicy object contains: " + selectedPolicy.getId() + ", " + selectedPolicy.getName() + ", " + selectedPolicy.getLocation().toString());
            response = copyPolicy(selectedPolicy, pdpGroup, clientScope, pushPolicyParameters.getRequestID());
        } catch (PAPException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+e.getMessage());
            throw new PolicyException(e);
        }
        LOGGER.debug("copyPolicy response:  " + response);
        if(response.contains("successfully")){
            response = (String) papServices.callPAP(selectedPolicy, new String[]{"groupId=" + pdpGroup, "policyId="+selectedPolicy.getId(), "apiflag=addPolicyToGroup", "operation=PUT"}, pushPolicyParameters.getRequestID(), clientScope);
        }
        LOGGER.debug("Final API response: " + response);
        return response;
    }

    private String copyPolicy(PDPPolicy policy, String group, String policyType, UUID requestID) throws PAPException {
        String response = null;
        if (policy == null || group == null) {
            throw new PAPException("Null input policy="+policy+"  group="+group);
        }
        try {
            StdPAPPolicy location = new StdPAPPolicy(policy.getLocation());
            response = copyFile(policy.getId(), group, location, policyType, requestID);
        } catch (Exception e) {
            String message = "Unable to PUT policy '" + policy.getId() + "', e:" + e;
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
            throw new PAPException(message);
        }
        return response;
    }
    
    private String copyFile(String policyId, String group, StdPAPPolicy location, String clientScope, UUID requestID) throws PAPException {
        String response = null;
        // send the policy file to the PAP Servlet
        PAPServices papService = new PAPServices();
        try {
            response = (String) papService.callPAP(location, new String[] {"groupId=" + group, "policyId="+policyId, "apiflag=api", "operation=post"}, requestID, clientScope);
        } catch (Exception e) {
            String message = "Unable to PUT policy '" + policyId + "', e:" + e;
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + message, e);
            throw new PAPException(message);
        }
        return response;
    }

    private boolean getValidation() {
        // While Validating, extract the required values.
        if (pushPolicyParameters.getPolicyName() != null
                && pushPolicyParameters.getPolicyName().contains(".")) {
            policyName = pushPolicyParameters.getPolicyName().substring(pushPolicyParameters.getPolicyName().lastIndexOf(".") + 1,
                    pushPolicyParameters.getPolicyName().length());
            policyScope = pushPolicyParameters.getPolicyName().substring(0,pushPolicyParameters.getPolicyName().lastIndexOf("."));
            LOGGER.info("Name is " + policyName + "   scope is " + policyScope);
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
            return false;
        }
        if (policyName==null||policyName.trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            return false;
        }
        policyType = pushPolicyParameters.getPolicyType();
        if(policyType== null || policyType.trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PolicyType given.";
            return false;
        }

        setClientScope();
        if(clientScope==null){
            return false;
        }
        
        pdpGroup = pushPolicyParameters.getPdpGroup();
        if(pdpGroup==null || pdpGroup.trim().isEmpty()){
        	pdpGroup = "default";
        }
        
        LOGGER.debug("clientScope is " + clientScope);
        LOGGER.debug("filePrefix is " + filePrefix);

        return true;
    }

    private void setClientScope() {
        if ("Firewall".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigFirewall";
            filePrefix = "Config_FW_";
        } else if ("Action".equalsIgnoreCase(policyType)) {
            clientScope = "Action";
            filePrefix = "Action_";
        } else if ("Decision".equalsIgnoreCase(policyType)) {
            clientScope = "Decision";
            filePrefix = "Decision_";
        } else if ("Base".equalsIgnoreCase(policyType)) {
            clientScope = "Config";
            filePrefix = "Config_";
        } else if ("ClosedLoop_Fault".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigClosedLoop";
            filePrefix = "Config_Fault_";
        } else if ("ClosedLoop_PM".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigClosedLoop";
            filePrefix = "Config_PM_";
        } else if ("MicroService".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigMS";
            filePrefix = "Config_MS_";
        } else if ("GOC".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigGoc";
            filePrefix = "Config_GOC_";
        } else if ("HighlandPark".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigGocHP";
            filePrefix = "Config_GOCHP_";
        } else if ("BRMS_RAW".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigBrmsRaw";
            filePrefix = "Config_BRMS_Raw_";
        } else if ("BRMS_PARAM".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigBrmsParam";
            filePrefix = "Config_BRMS_Param_";
        } else {
            clientScope = null;
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + policyType
                    + " is not a valid Policy Type.";
        }
    }

    public String getResult() {
        return pushResult;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
