/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.CreateBRMSRuleTemplate;
import org.onap.policy.pap.xacml.rest.components.CreateNewMicroServiceModel;
import org.onap.policy.pap.xacml.rest.components.CreateNewOptimizationModel;

public class ImportService {
    private static final Logger logger = FlexLogger.getLogger(ImportService.class);
    private static String errorMessage = "Error in reading in file from API call";
    private static String errorMsg = "error";
    private static String operation = "operation";
    private static String importHeader = "import";
    private static String service = "service";
    private static String extractDir = "ExtractDir";
    private static String successMessage = "success";
    private static String invalidServiceName = "Invalid ServiceName";
    private static final String REGEX = "[0-9a-zA-Z._ ]*";
    private static final String MISSING = "missing";

    public void doImportMicroServicePut(HttpServletRequest request, HttpServletResponse response) {
        String importServiceCreation = request.getParameter("importService");
        String fileName = request.getParameter("fileName");
        String version = request.getParameter("version");
        String serviceName = request.getParameter("serviceName");
        boolean decisionModel = Boolean.parseBoolean(request.getParameter("decisionModel"));

        if (serviceName == null || serviceName.isEmpty() || !serviceName.matches(REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, MISSING);
            response.addHeader(operation, importHeader);
            response.addHeader(service, invalidServiceName);
            return;
        }

        String description = request.getParameter("description");
        Map<String, String> successMap = new HashMap<>();
        if (("BRMSPARAM").equals(importServiceCreation)) {
            StringBuilder builder = new StringBuilder();
            int ch;
            try {
                while ((ch = request.getInputStream().read()) != -1) {
                    builder.append((char) ch);
                }
            } catch (IOException e) {
                logger.error(e);
                PolicyLogger.error(errorMessage);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(errorMsg, MISSING);
                response.addHeader(operation, importHeader);
                response.addHeader(service, serviceName);
            }
            CreateBRMSRuleTemplate brmsRuleTemplate = new CreateBRMSRuleTemplate();
            successMap = brmsRuleTemplate.addRule(builder.toString(), serviceName, description, "API");
        } else if (("MICROSERVICE").equals(importServiceCreation)) {
            CreateNewMicroServiceModel newMS = null;
            String randomID = UUID.randomUUID().toString();
            String type = ".xmi";
            if (fileName != null) {
                File extracDir = new File(extractDir);
                if (!extracDir.exists()) {
                    extracDir.mkdirs();
                }
                if (fileName.contains(".xmi") || fileName.contains(".yml")) {
                    if (fileName.contains(".yml")) {
                        type = ".yml";
                    }
                    // get the request content into a String
                    String xmi = null;
                    java.util.Scanner scanner;
                    try {
                        scanner = new java.util.Scanner(request.getInputStream());
                        scanner.useDelimiter("\\A");
                        xmi = scanner.hasNext() ? scanner.next() : "";
                        scanner.close();
                    } catch (IOException e1) {
                        logger.error(e1);
                        PolicyLogger.error(errorMessage);
                        return;
                    }
                    PolicyLogger.info("Request from API to import new Service");
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(extractDir + File.separator + randomID + type), "utf-8"))) {
                        writer.write(xmi);
                    } catch (IOException e) {
                        logger.error(e);
                        PolicyLogger.error(errorMessage);
                        return;
                    }
                } else {
                    InputStream inputStream = null;
                    try (FileOutputStream outputStream =
                            new FileOutputStream(extractDir + File.separator + randomID + ".zip")) {
                        inputStream = request.getInputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        PolicyLogger.error("Error in reading in Zip File from API call" + e);
                        return;
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            PolicyLogger.error("Exception Occured while closing the input/output stream" + e);
                        }
                    }
                }

                newMS = new CreateNewMicroServiceModel(fileName, serviceName, "API", version, randomID, decisionModel);

                successMap = newMS.addValuesToNewModel(type);

                if (successMap.containsKey(successMessage)) {
                    successMap.clear();
                    successMap = newMS.saveImportService();
                }
            }
        } else if (("OPTIMIZATION").equals(importServiceCreation)) {
            CreateNewOptimizationModel newOOF = null;
            String randomID = UUID.randomUUID().toString();
            if (fileName != null) {
                File extracDir = new File(extractDir);
                if (!extracDir.exists()) {
                    extracDir.mkdirs();
                }

                String type = ".yml";

                // get the request content into a String
                String yml = null;
                try (java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());) {
                    scanner.useDelimiter("\\A");
                    yml = scanner.hasNext() ? scanner.next() : "";
                } catch (IOException e1) {
                    logger.error(e1);
                    PolicyLogger.error(errorMessage);
                    return;
                }
                PolicyLogger.info("Request from API to import new Optimization Service Model");
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(extractDir + File.separator + randomID + type), "utf-8"))) {
                    writer.write(yml);
                } catch (IOException e) {
                    logger.error(e);
                    PolicyLogger.error(errorMessage);
                    return;
                }

                newOOF = new CreateNewOptimizationModel(fileName, serviceName, "API Import Service", version, randomID);
                successMap = newOOF.addValuesToNewModel();
                if (successMap.containsKey(successMessage)) {
                    successMap.clear();
                    successMap = newOOF.saveImportService();
                }
            }
        }

        // return a response to the PAP
        if (successMap.containsKey(successMessage)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("successMapKey", successMessage);
            response.addHeader(operation, importHeader);
            response.addHeader(service, serviceName);
        } else if (successMap.containsKey("DBError")) {
            if (successMap.get("DBError").contains("EXISTS")) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.addHeader(service, serviceName);
                response.addHeader(errorMsg, "modelExistsDB");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(errorMsg, "importDB");
            }
            response.addHeader(operation, importHeader);
            response.addHeader(service, serviceName);
        } else if (successMap.get(errorMsg).contains("MISSING")) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.addHeader(errorMsg, MISSING);
            response.addHeader(operation, importHeader);
            response.addHeader(service, serviceName);
        } else if (successMap.get(errorMsg).contains("VALIDATION")) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.addHeader(errorMsg, "validation");
            response.addHeader(operation, importHeader);
            response.addHeader(service, serviceName);
        }
    }

}
