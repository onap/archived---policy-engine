/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.onap.policy.api.PEDependency;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.CreateBRMSRuleTemplate;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.BrmsController;
import org.onap.policy.rest.jpa.BrmsDependency;
import org.onap.policy.rest.jpa.BrmsParamTemplate;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BRMSDictionaryController {

    private static final Logger LOGGER = FlexLogger.getLogger(BRMSDictionaryController.class);

    private static final String VALIDATIONRESPONSE = "Validation";

    private static CommonClassDao commonClassDao;
    private static String rule;
    private static String successMsg = "Success";
    private static String duplicateResponseString = "Duplicate";
    private static String ruleName = "ruleName";
    private static String errorMessage = "Error";
    private static String operation = "operation";
    private static String dictionaryFields = "dictionaryFields";
    private static String userid = "userid";
    private static String dependencyName = "dependencyName";
    private static String controllerName = "controllerName";
    private static String brmsParamDatas = "brmsParamDictionaryDatas";
    private static String brmsDependencyDatas = "brmsDependencyDictionaryDatas";
    private static String brmsControllerDatas = "brmsControllerDictionaryDatas";

    @Autowired
    public BRMSDictionaryController(CommonClassDao commonClassDao) {
        BRMSDictionaryController.commonClassDao = commonClassDao;
    }

    public static void setCommonClassDao(CommonClassDao commonClassDao2) {
        BRMSDictionaryController.commonClassDao = commonClassDao2;
    }

    public BRMSDictionaryController() {
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance() {
        return DictionaryUtils.getDictionaryUtils();
    }

    @GetMapping(
            value = {"/get_BRMSParamDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSParamDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, brmsParamDatas, ruleName, BrmsParamTemplate.class);
    }

    @GetMapping(
            value = {"/get_BRMSParamData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSParamDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, brmsParamDatas, BrmsParamTemplate.class);
    }

    @PostMapping(value = {"/brms_dictionary/set_BRMSParamData"})
    public static void setRuleData(HttpServletRequest request) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer, StandardCharsets.UTF_8);
        String cleanStreamBoundary = writer.toString().replaceFirst("------(.*)(?s).*octet-stream", "");
        rule = cleanStreamBoundary.substring(0, cleanStreamBoundary.lastIndexOf("end") + 4);
    }

    @PostMapping(value = {"/brms_dictionary/save_BRMSParam"})
    public ModelAndView saveBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());

            BrmsParamTemplate bRMSParamTemplateData;
            String userId = null;
            if (fromAPI) {
                bRMSParamTemplateData =
                        mapper.readValue(root.get(dictionaryFields).toString(), BrmsParamTemplate.class);
                userId = "API";
            } else {
                bRMSParamTemplateData =
                        mapper.readValue(root.get("brmsParamDictionaryData").toString(), BrmsParamTemplate.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(bRMSParamTemplateData.getRuleName(),
                    ruleName, BrmsParamTemplate.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                BrmsParamTemplate data = (BrmsParamTemplate) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    bRMSParamTemplateData.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null
                                && (data.getId() != bRMSParamTemplateData.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            boolean validation = false;
            if (rule != null && CreateBRMSRuleTemplate.validateRuleParams(rule)) {
                bRMSParamTemplateData.setRule(rule);
                validation = true;
                if (!duplicateflag) {
                    if (bRMSParamTemplateData.getId() == 0) {
                        bRMSParamTemplateData.setUserCreatedBy(userInfo);
                        commonClassDao.save(bRMSParamTemplateData);
                    } else {
                        commonClassDao.update(bRMSParamTemplateData);
                    }
                    responseString = mapper.writeValueAsString(commonClassDao.getData(BrmsParamTemplate.class));
                } else {
                    responseString = duplicateResponseString;
                }
            }

            if (!validation) {
                responseString = VALIDATIONRESPONSE;
            }
            if (fromAPI) {
                if (responseString != null && !(duplicateResponseString).equals(responseString)
                        && !VALIDATIONRESPONSE.equals(responseString)) {
                    responseString = successMsg;
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                utils.setResponseData(response, brmsParamDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/brms_dictionary/remove_brmsParam"})
    public void removeBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, brmsParamDatas, BrmsParamTemplate.class);
    }

    @GetMapping(
            value = {"/get_BRMSDependencyDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSDependencyDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, brmsDependencyDatas, dependencyName, BrmsDependency.class);
    }

    @GetMapping(
            value = {"/get_BRMSDependencyData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSDependencyDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, brmsDependencyDatas, BrmsDependency.class);
    }

    @PostMapping(value = {"/brms_dictionary/save_BRMSDependencyData"})
    public ModelAndView saveBRMSDependencyDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            LOGGER.debug("DictionaryController:  saveBRMSDependencyDictionary() is called");
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());

            BrmsDependency brmsDependency;
            String userId = null;
            if (fromAPI) {
                brmsDependency = mapper.readValue(root.get(dictionaryFields).toString(), BrmsDependency.class);
                userId = "API";
            } else {
                brmsDependency =
                        mapper.readValue(root.get("brmsDependencyDictionaryData").toString(), BrmsDependency.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(brmsDependency.getDependencyName(),
                    dependencyName, BrmsDependency.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                BrmsDependency data = (BrmsDependency) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    brmsDependency.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != brmsDependency.getId()))) {
                    duplicateflag = true;
                }
            }
            LOGGER.audit("the userId from the onap portal is: " + userId);
            String responseString = null;
            if (brmsDependency.getDependency() != null && !("").equals(brmsDependency.getDependency().trim())) {
                PEDependency dependency = null;
                try {
                    dependency = PolicyUtils.jsonStringToObject(brmsDependency.getDependency(), PEDependency.class);
                } catch (Exception e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID
                            + "wrong data given for BRMS PEDependency Dictionary : " + brmsDependency.getDependency(),
                            e);
                }
                if (dependency == null) {
                    responseString = errorMessage;
                } else {
                    if (!duplicateflag) {
                        brmsDependency.setUserModifiedBy(userInfo);
                        if (brmsDependency.getId() == 0) {
                            brmsDependency.setUserCreatedBy(userInfo);
                            commonClassDao.save(brmsDependency);
                        } else {
                            brmsDependency.setModifiedDate(new Date());
                            commonClassDao.update(brmsDependency);
                        }
                        responseString = mapper.writeValueAsString(commonClassDao.getData(BrmsDependency.class));
                    } else {
                        responseString = duplicateResponseString;
                    }
                }
            }

            if (fromAPI) {
                if (responseString != null && !duplicateResponseString.equals(responseString)
                        && !errorMessage.equals(responseString)) {
                    responseString = successMsg;
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                utils.setResponseData(response, brmsDependencyDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/brms_dictionary/remove_brmsDependency"})
    public void removeBRMSDependencyDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, brmsDependencyDatas, BrmsDependency.class);
    }

    @GetMapping(
            value = {"/get_BRMSControllerDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSControllerDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, brmsControllerDatas, controllerName, BrmsController.class);
    }

    @GetMapping(
            value = {"/get_BRMSControllerData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSControllerDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, brmsControllerDatas, BrmsController.class);
    }

    @PostMapping(value = {"/brms_dictionary/save_BRMSControllerData"})
    public ModelAndView saveBRMSControllerDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            LOGGER.debug("DictionaryController:  saveBRMSControllerDictionary() is called");
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            BrmsController brmsController;
            String userId = null;
            if (fromAPI) {
                brmsController = mapper.readValue(root.get(dictionaryFields).toString(), BrmsController.class);
                userId = "API";
            } else {
                brmsController =
                        mapper.readValue(root.get("brmsControllerDictionaryData").toString(), BrmsController.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(brmsController.getControllerName(),
                    controllerName, BrmsController.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                BrmsController data = (BrmsController) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    brmsController.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != brmsController.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (brmsController.getController() != null && !("").equals(brmsController.getController().trim())) {
                PEDependency dependency = null;
                try {
                    dependency = PolicyUtils.jsonStringToObject(brmsController.getController(), PEDependency.class);
                } catch (Exception e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID
                            + "wrong data given for BRMS Controller Dictionary : " + brmsController.getController(), e);
                }
                if (dependency == null) {
                    responseString = errorMessage;
                } else {
                    if (!duplicateflag) {
                        brmsController.setUserModifiedBy(userInfo);
                        if (brmsController.getId() == 0) {
                            brmsController.setUserCreatedBy(userInfo);
                            commonClassDao.save(brmsController);
                        } else {
                            brmsController.setModifiedDate(new Date());
                            commonClassDao.update(brmsController);
                        }
                        responseString = mapper.writeValueAsString(commonClassDao.getData(OnapName.class));
                    } else {
                        responseString = duplicateResponseString;
                    }
                }
            }
            if (fromAPI) {
                if (responseString != null && !(duplicateResponseString).equals(responseString)
                        && !(errorMessage).equals(responseString)) {
                    responseString = successMsg;
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                utils.setResponseData(response, brmsControllerDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/brms_dictionary/remove_brmsController"})
    public void removeBRMSControllerDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, brmsControllerDatas, BrmsController.class);
    }

    public BrmsDependency getDependencyDataByID(String dependencyName) {
        return (BrmsDependency) commonClassDao.getEntityItem(BrmsDependency.class,
                BRMSDictionaryController.dependencyName, dependencyName);
    }

    public BrmsController getControllerDataByID(String controllerName) {
        return (BrmsController) commonClassDao.getEntityItem(BrmsController.class,
                BRMSDictionaryController.controllerName, controllerName);
    }
}
