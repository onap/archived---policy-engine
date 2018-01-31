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

import java.io.ByteArrayInputStream;
import java.util.UUID;

import javax.json.JsonException;
import javax.json.JsonObject;

import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class CreateUpdateDictionaryService {
    private static final Logger LOGGER = FlexLogger.getLogger(CreateUpdateDictionaryService.class.getName());

    private String dictionaryResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private String message = null;
    private Boolean updateFlag = false;
    private DictionaryParameters dictionaryParameters = null;
    private JsonObject json = null;

    public CreateUpdateDictionaryService(
            DictionaryParameters dictionaryParameters, String requestID,
            boolean updateFlag) {
        this.updateFlag = updateFlag;
        this.dictionaryParameters = dictionaryParameters;
        if(dictionaryParameters.getRequestID()==null){
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
            this.dictionaryParameters.setRequestID(requestUUID);
        }

        try{
            run();
            specialCheck();
        }catch(PolicyException e){
            dictionaryResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void specialCheck() {
        if(dictionaryResult== null || dictionaryResult.contains("BAD REQUEST")||dictionaryResult.contains("PE300")){
            status = HttpStatus.BAD_REQUEST;
        } else if (dictionaryResult.contains("Policy Exist Error")) {
            status = HttpStatus.CONFLICT;
        } else if (dictionaryResult.contains("PE200")){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
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
            dictionaryResult = processResult();
        }catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    private String processResult() throws PolicyException{
        String operation = null;
        if (updateFlag){
            operation = "update";
        } else {
            operation = "create";
        }

        String dictionaryFields = json.toString();
        PAPServices papServices = new PAPServices();
        return (String) papServices.callPAP(new ByteArrayInputStream(dictionaryFields.getBytes()), new String[] {"operation="+operation, "apiflag=api", "dictionaryType="+dictionaryParameters.getDictionary()}, dictionaryParameters.getRequestID(), "dictionaryItem");
    }

    private boolean getValidation() {
    	LOGGER.info("Start validating create or update dictionary request.");
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
        if(dictionaryParameters.getDictionaryJson()==null || dictionaryParameters.getDictionaryJson().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Dictionary JSON given.";
            return false;
        }
        if (updateFlag && "MicroServiceDictionary".equalsIgnoreCase(dictionaryParameters.getDictionary())&& !dictionaryParameters.getDictionaryJson().contains("initialFields")){
        	message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Mising the required field initialFields.";
        	return false;
        }

        try{
            json = PolicyApiUtils.stringToJsonObject(dictionaryParameters.getDictionaryJson());
            String result = PolicyApiUtils.validateDictionaryJsonFields(json.getJsonObject("dictionaryFields"), dictionaryParameters.getDictionary());

            if(!"success".equals(result)) {
            	message = result;
                return false;
            }

        }catch(JsonException| IllegalStateException e){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper Dictionary JSON object : " + dictionaryParameters.getDictionaryJson();
            LOGGER.error(message, e);
            return false;
        }

        LOGGER.info("dictionary API request validation complete and valid.");
        return true;
    }

    public String getResult() {
        return dictionaryResult;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
