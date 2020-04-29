/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.adapters.UpdateObjectData;
import org.onap.policy.pap.xacml.rest.components.Policy;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyDbDaoEntity;
import org.onap.policy.utils.PeCryptoUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class UpdateOthersPAPS {

    private static final Logger policyLogger = FlexLogger.getLogger(UpdateOthersPAPS.class);

    private static CommonClassDao commonClassDao;

    private static final String contentType = "application/json";
    private static String configType = ".Config_";
    private static String actionType = ".Action_";
    private static String error = "error";

    public static CommonClassDao getCommonClassDao() {
        return commonClassDao;
    }

    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        UpdateOthersPAPS.commonClassDao = commonClassDao;
    }

    @Autowired
    private UpdateOthersPAPS(CommonClassDao commonClassDao) {
        UpdateOthersPAPS.commonClassDao = commonClassDao;
    }

    public UpdateOthersPAPS() {
        // Empty Constructor
    }

    @PostMapping(value = "/notifyOtherPAPs")
    public void notifyOthersPAPsToUpdateConfigurations(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        UpdateObjectData body = new UpdateObjectData();
        body.setAction(request.getParameter("action"));
        body.setNewPolicyName(request.getParameter("newPolicyName"));
        body.setOldPolicyName(request.getParameter("oldPolicyName"));

        String currentPap = XacmlRestProperties.getProperty("xacml.rest.pap.url");
        List<Object> getPAPUrls = commonClassDao.getData(PolicyDbDaoEntity.class);
        if (getPAPUrls != null && !getPAPUrls.isEmpty()) {
            for (int i = 0; i < getPAPUrls.size(); i++) {
                PolicyDbDaoEntity papId = (PolicyDbDaoEntity) getPAPUrls.get(i);
                String papUrl = papId.getPolicyDbDaoUrl();
                if (!papUrl.equals(currentPap)) {
                    String userName = papId.getUsername();
                    String password = papId.getPassword();
                    Base64.Encoder encoder = Base64.getEncoder();
                    String txt;
                    try {
                        PeCryptoUtils.initAesKey(XACMLProperties.getProperty(XacmlRestProperties.PROP_AES_KEY));
                        txt = PeCryptoUtils.decrypt(password);
                    } catch (Exception e) {
                        policyLogger.debug(e);
                        // if we can't decrypt, might as well try it anyway
                        txt = password;
                    }
                    String encoding = encoder.encodeToString((userName + ":" + txt).getBytes(StandardCharsets.UTF_8));
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Basic " + encoding);
                    headers.set("Content-Type", contentType);

                    RestTemplate restTemplate = new RestTemplate();
                    HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);
                    HttpClientErrorException exception = null;

                    try {
                        restTemplate.exchange(papUrl + "onap/updateConfiguration", HttpMethod.POST, requestEntity,
                                String.class);
                    } catch (Exception e) {
                        policyLogger.error(
                                XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while connecting to " + papUrl, e);
                        exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                        if ("409 Conflict".equals(e.getMessage())) {
                            policyLogger.error(e.getMessage());
                            response.addHeader(error, e.getMessage());
                        }
                    }
                    if (exception != null && exception.getStatusCode() != null) {
                        String message;
                        if (exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                            message = XACMLErrorConstants.ERROR_PERMISSIONS + ":" + exception.getStatusCode() + ":"
                                    + "ERROR_AUTH_GET_PERM";
                            policyLogger.error(message);
                        } else if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                            message = XACMLErrorConstants.ERROR_DATA_ISSUE + ":" + exception.getStatusCode() + ":"
                                    + exception.getResponseBodyAsString();
                            policyLogger.error(message);
                        } else if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                            message = XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while connecting to " + papUrl
                                    + exception;
                            policyLogger.error(message);
                        } else {
                            message = XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + exception.getStatusCode() + ":"
                                    + exception.getResponseBodyAsString();
                            policyLogger.error(message);
                        }
                        model.put(papUrl, message);
                    } else {
                        model.put(papUrl, "Success");
                    }
                }
            }
            JsonMessage msg;
            try {
                msg = new JsonMessage(mapper.writeValueAsString(model));
                JSONObject j = new JSONObject(msg);
                response.getWriter().write(j.toString());
            } catch (Exception e) {
                policyLogger.error("Exception Occured" + e);
            }
        }
    }

    @PostMapping(value = "/updateConfiguration")
    @ResponseBody
    public void updateConfiguration(@RequestBody UpdateObjectData data, HttpServletResponse response) {
        String action = data.getAction();
        String newPolicyName = data.getNewPolicyName();
        String oldPolicyName = data.getOldPolicyName();
        try {
            if ("rename".equals(action)) {
                if (oldPolicyName.contains(configType) || oldPolicyName.contains(actionType)) {
                    File file;
                    if (oldPolicyName.contains(configType)) {
                        file = new File(Policy.getConfigHome() + File.separator + oldPolicyName);
                    } else {
                        file = new File(Policy.getActionHome() + File.separator + oldPolicyName);
                    }
                    if (file.exists()) {
                        File renamefile;
                        if (oldPolicyName.contains(configType)) {
                            renamefile = new File(Policy.getConfigHome() + File.separator + newPolicyName);
                        } else {
                            renamefile = new File(Policy.getActionHome() + File.separator + newPolicyName);
                        }
                        if (file.renameTo(renamefile)) {
                            policyLogger.info("Policy has been renamed Successfully" + newPolicyName);
                            response.addHeader("rename", "Success");
                        } else {
                            response.addHeader("rename", "Failure");
                        }
                    }
                }
            } else if ("delete".equals(action)) {
                if (oldPolicyName.contains(configType)) {
                    Files.deleteIfExists(Paths.get(Policy.getConfigHome() + File.separator + oldPolicyName));
                } else if (oldPolicyName.contains("Action_")) {
                    Files.deleteIfExists(Paths.get(Policy.getActionHome() + File.separator + oldPolicyName));
                }
            } else if ("clonePolicy".equals(action) || "exportPolicy".equals(action)) {
                if (newPolicyName.contains(configType)) {
                    ConfigurationDataEntity configEntiy = (ConfigurationDataEntity) commonClassDao
                            .getEntityItem(ConfigurationDataEntity.class, "configurationName", newPolicyName);
                    saveConfigurationData(configEntiy, newPolicyName);
                } else if (newPolicyName.contains(actionType)) {
                    ActionBodyEntity actionEntiy = (ActionBodyEntity) commonClassDao
                            .getEntityItem(ActionBodyEntity.class, "actionBodyName", newPolicyName);
                    saveActionBodyData(actionEntiy, newPolicyName);
                }
            }
        } catch (IOException e) {
            policyLogger.error("Exception Occured While updating Configuration" + e);
        }
    }

    private void saveConfigurationData(ConfigurationDataEntity configEntiy, String newPolicyName) {
        try (FileWriter fw = new FileWriter(Policy.getConfigHome() + File.separator + newPolicyName)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(configEntiy.getConfigBody());
            bw.close();
        } catch (IOException e) {
            policyLogger.error("Exception Occured While closing the File input stream" + e);
        }
    }

    private void saveActionBodyData(ActionBodyEntity actionEntiy, String newPolicyName) {
        try (FileWriter fw = new FileWriter(Policy.getActionHome() + File.separator + newPolicyName)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(actionEntiy.getActionBody());
            bw.close();
        } catch (IOException e) {
            policyLogger.error("Exception Occured While closing the File input stream" + e);
        }
    }
}
