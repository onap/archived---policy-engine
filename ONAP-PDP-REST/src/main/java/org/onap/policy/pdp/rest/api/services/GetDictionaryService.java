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

import java.util.UUID;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.api.DictionaryResponse;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.std.StdDictionaryResponse;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class GetDictionaryService {
    private static final Logger LOGGER = FlexLogger.getLogger(GetDictionaryService.class.getName());
    
    private DictionaryResponse dictionaryResponse = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private String message = null;
    private DictionaryParameters dictionaryParameters = null;

    public GetDictionaryService(DictionaryParameters dictionaryParameters,
            String requestID) {
        this.dictionaryParameters = dictionaryParameters;
        if(dictionaryParameters.getRequestID()==null){
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(), e);
                }
            }else{
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.dictionaryParameters.setRequestID(requestUUID);
        }
        try{
            run();
            specialCheck();
        }catch(PolicyException e){
            StdDictionaryResponse dictionaryResponse = new StdDictionaryResponse();
            dictionaryResponse.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            this.dictionaryResponse = dictionaryResponse;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void specialCheck() {
        if(dictionaryResponse!=null && (dictionaryResponse.getResponseMessage()!=null && dictionaryResponse.getResponseMessage().contains("PE300"))){
        	status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyException{
     // Check Validation. 
        if(!getValidation()){
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Get Result. 
        try{
            status = HttpStatus.OK;
            dictionaryResponse = processResult();
        }catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    private DictionaryResponse processResult() throws PolicyException{
        StdDictionaryResponse response = new StdDictionaryResponse();
        PAPServices papServices = new PAPServices();
        String result = (String) papServices.callPAP(null, new String[] {"operation=get", "apiflag=api", "dictionaryType="+dictionaryParameters.getDictionary()}, dictionaryParameters.getRequestID(), "dictionaryItem");
        
        if (result!=null && result.contains("data")) {
            String jsonString = formatDictionaryJson(result);
            String responseMessage = result.substring(0, 82);
            JsonObject json = null;
            try{
                json = PolicyApiUtils.stringToJsonObject(jsonString.replace("\\\\\\", "\\"));
                String datas = json.getString("data").replaceFirst("\"\\[", "[");
                int i = datas.lastIndexOf("]");
                if( i>=0 ) {
                	datas = new StringBuilder(datas).replace(i, i+2,"]").toString();
                }
                json = PolicyApiUtils.stringToJsonObject(datas);
            } catch(JsonException| IllegalStateException e){
                message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper Dictionary JSON object : " + dictionaryParameters.getDictionaryJson();
                LOGGER.error(message, e);
                response.setResponseMessage(message);
                response.setResponseCode(400);
                return response;
            }
            response.setResponseCode(papServices.getResponseCode());
            response.setDictionaryJson(json);
            response.setResponseMessage(responseMessage);                     
        } else {
            response.setResponseCode(400);
            response.setResponseMessage(result);
            if(result!=null && result.contains("PE200")){
            	status=HttpStatus.INTERNAL_SERVER_ERROR;
            }else{
            	status=HttpStatus.BAD_REQUEST;
            }
        }
        return response;
    }
    
    public String extendedClientScope(String jsonString,String dictionary){
    		return null;
 	}

    private String formatDictionaryJson(String result) {
        String jsonString = result.substring(82);
        String dictionary = dictionaryParameters.getDictionary();
        
        switch (dictionary) {
        case "OnapName":
            jsonString = jsonString.replace("onapNameDictionaryDatas", "DictionaryDatas");
            break;
        case "Attribute":
            jsonString = jsonString.replace("attributeDictionaryDatas", "DictionaryDatas");
            break;
            case "Action":
            jsonString = jsonString.replace("actionPolicyDictionaryDatas", "DictionaryDatas");
            break;
        case "BRMSParamTemplate":
            jsonString = jsonString.replace("brmsParamDictionaryDatas", "DictionaryDatas");
            break;
        case "VSCLAction":
            jsonString = jsonString.replace("vsclActionDictionaryDatas", "DictionaryDatas");
            break;
        case "VNFType":
            jsonString = jsonString.replace("vnfTypeDictionaryDatas", "DictionaryDatas");
            break;
        case "PEPOptions":
            jsonString = jsonString.replace("pepOptionsDictionaryDatas", "DictionaryDatas");
            break;
        case "Varbind":
            jsonString = jsonString.replace("varbindDictionaryDatas", "DictionaryDatas");
            break;
        case "Service":
            jsonString = jsonString.replace("closedLoopServiceDictionaryDatas", "DictionaryDatas");
            break;
        case "Site":
            jsonString = jsonString.replace("closedLoopSiteDictionaryDatas", "DictionaryDatas");
            break;
        case "Settings":
            jsonString = jsonString.replace("settingsDictionaryDatas", "DictionaryDatas");
            break;
        case "RainyDayTreatments":
        	jsonString = jsonString.replace("rainyDayDictionaryDatas", "DictionaryDatas");
        	break;
        case "DescriptiveScope":
            jsonString = jsonString.replace("descriptiveScopeDictionaryDatas", "DictionaryDatas");
            break;
        case "Enforcer":
            jsonString = jsonString.replace("enforcerDictionaryDatas", "DictionaryDatas");
            break;
        case "ActionList":
            jsonString = jsonString.replace("actionListDictionaryDatas", "DictionaryDatas");
            break;
        case "ProtocolList":
            jsonString = jsonString.replace("protocolListDictionaryDatas", "DictionaryDatas");
            break;
        case "Zone":
            jsonString = jsonString.replace("zoneDictionaryDatas", "DictionaryDatas");
            break;
        case "SecurityZone":
            jsonString = jsonString.replace("securityZoneDictionaryDatas", "DictionaryDatas");
            break;
        case "PrefixList":
            jsonString = jsonString.replace("prefixListDictionaryDatas", "DictionaryDatas");
            break;
        case "AddressGroup":
            jsonString = jsonString.replace("addressGroupDictionaryDatas", "DictionaryDatas");
            break;
        case "ServiceGroup":
            jsonString = jsonString.replace("serviceGroupDictionaryDatas", "DictionaryDatas");
            break;
        case "ServiceList":
            jsonString = jsonString.replace("serviceListDictionaryDatas", "DictionaryDatas");
            break;
        case "TermList":
        case "RuleList":
        case "FirewallRuleList":
        case "Term":
            jsonString = jsonString.replace("termListDictionaryDatas", "DictionaryDatas");
            break;
        case "MicroServiceLocation":
            jsonString = jsonString.replace("microServiceLocationDictionaryDatas", "DictionaryDatas");
            break;
        case "MicroServiceConfigName":
            jsonString = jsonString.replace("microServiceConfigNameDictionaryDatas", "DictionaryDatas");
            break;
        case "DCAEUUID":
            jsonString = jsonString.replace("dcaeUUIDDictionaryDatas", "DictionaryDatas");
            break;
        case "MicroServiceModels":
            jsonString = jsonString.replace("microServiceModelsDictionaryDatas", "DictionaryDatas");
            break;
        case "OptimizationModels":
        	jsonString = jsonString.replace("optmizationModelsDictionaryDatas", "DictionaryDatas");
        	break;
        case "PolicyScopeService":
            jsonString = jsonString.replace("psServiceDictionaryDatas", "DictionaryDatas");
            break;
        case "PolicyScopeResource":
            jsonString = jsonString.replace("psResourceDictionaryDatas", "DictionaryDatas");
            break;
        case "PolicyScopeType":
            jsonString = jsonString.replace("psTypeDictionaryDatas", "DictionaryDatas");
            break;
        case "PolicyScopeClosedLoop":
            jsonString = jsonString.replace("psClosedLoopDictionaryDatas", "DictionaryDatas");
            break;
        case "GroupPolicyScopeList":
            jsonString = jsonString.replace("groupPolicyScopeListDatas", "DictionaryDatas");
            break;
        case "RiskType":
            jsonString = jsonString.replace("riskTypeDictionaryDatas", "DictionaryDatas");
            break;
        case "SafePolicyWarning":
            jsonString = jsonString.replace("safePolicyWarningDatas", "DictionaryDatas");
            break;
        case "MicroServiceDictionary":
            jsonString = jsonString.replace("microServiceDictionaryDatas", "DictionaryDatas");
            break;
        default:
            jsonString=extendedClientScope(jsonString,dictionary);
        }
        return jsonString;
    }

    private boolean getValidation() {
        if(dictionaryParameters==null){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Dictionary Parameters are not given.";
            return false;
        }
        if(dictionaryParameters.getDictionaryType()==null || dictionaryParameters.getDictionaryType().toString().trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary Type given.";
            return false;
        }
        if(dictionaryParameters.getDictionary()==null || dictionaryParameters.getDictionary().trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary given.";
            return false;
        }
        return true;
    }

    public DictionaryResponse getResult() {
        return dictionaryResponse;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
