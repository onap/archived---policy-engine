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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.ImportParameters.IMPORT_TYPE;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public class PolicyEngineImportService {
    private static final Logger LOGGER = FlexLogger.getLogger(PolicyEngineImportService.class.getName());

    private String importResponse = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private String message = null;
    private ImportParameters importParameters = null;
    private MultipartFile file = null;

    public PolicyEngineImportService(String importParametersJson,
            MultipartFile file,
            String requestID) {
        try {
            this.importParameters = PolicyUtils.jsonStringToObject(importParametersJson, ImportParameters.class);
        } catch (Exception e) {
            importResponse = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
            // This needs to stop here in case if there a issue here. Avoiding Null pointer exceptions. 
            return; 
        }
        this.file = file;
        if (importParameters.getRequestID() == null) {
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(), e);
                }
            } else {
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.importParameters.setRequestID(requestUUID);
        }
        try {
            run();
            specialCheck();
        } catch (PolicyException e) {
            importResponse = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void specialCheck() {
        if (importResponse == null || importResponse.contains("PE200")) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (importResponse.contains("BAD REQUEST") || importResponse.contains("PE300")) {
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyException {
        // Check Validation.
        if (!getValidation()) {
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Get Result.
        try {
            status = HttpStatus.OK;
            importResponse = processResult();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    private String processResult() throws PolicyException {
        String response = null;
        InputStream targetStream = null;
        String fileName = file.getOriginalFilename();
        switch (importParameters.getServiceType()) {
            case MICROSERVICE:
            case OPTIMIZATION:
                if (fileName.endsWith(".yml") || fileName.endsWith(".xmi") || fileName.endsWith(".zip")) {
                    try {
                        targetStream = new BufferedInputStream(file.getInputStream());
                    } catch (IOException e) {
                        response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in reading in the file provided";
                        LOGGER.error(response + e);
                        return response;
                    }
                } else {
                    response = XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Incorrect File Type Given. Please use a file of type .xmi or .zip.";
                    LOGGER.error(response);
                    return response;
                }
                break;
            case BRMSPARAM:
                if (fileName.endsWith(".drl")) {
                    try {
                        targetStream = new BufferedInputStream(file.getInputStream());
                    } catch (IOException e) {
                        response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in reading in the file provided";
                        LOGGER.error(response + e);
                        return response;
                    }
                } else {
                    response = XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Incorrect File Type Given. Please use a file of type .drl";
                    LOGGER.error(response);
                    return response;
                }
                break;
            default:
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect ServiceType Given. ";
                LOGGER.error(response);
                return response;
        }
        String[] parameters = new String[] {"importService=" + importParameters.getServiceType(),
                "serviceName=" + importParameters.getServiceName(), "fileName=" + fileName,
                "version=" + importParameters.getVersion(), "description=" + importParameters.getDescription(),
                        "decisionModel=" + importParameters.isDecisionModel()};
        PAPServices papServices = new PAPServices();
        response = (String) papServices.callPAP(targetStream, parameters, importParameters.getRequestID(), "importMS");
        return response;
    }

    private boolean getValidation() {
        if (importParameters == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + " no Import Parameters given. ";
            return false;
        }
        if (importParameters.getServiceName() == null || importParameters.getServiceName().trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing service name value.";
            return false;
        }
        if (importParameters.getServiceType() == null
                || importParameters.getServiceType().toString().trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing service Type value.";
            return false;
        }
        if ((IMPORT_TYPE.MICROSERVICE.equals(importParameters.getServiceType())
                || IMPORT_TYPE.OPTIMIZATION.equals(importParameters.getServiceType()))
                && (importParameters.getVersion() == null || importParameters.getVersion().trim().isEmpty())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing version value.";
            return false;
        }
        if (file == null || file.isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing File.";
            return false;
        }
        return true;
    }

    public String getResult() {
        return importResponse;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
