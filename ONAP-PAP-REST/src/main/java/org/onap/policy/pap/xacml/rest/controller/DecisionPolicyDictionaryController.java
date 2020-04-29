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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DecisionSettings;
import org.onap.policy.rest.jpa.RainyDayTreatments;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DecisionPolicyDictionaryController {

    private static CommonClassDao commonClassDao;
    private static String xacmlId = "xacmlId";
    private static String bbID = "bbid";
    private static String operation = "operation";
    private static String duplicateResponseString = "Duplicate";
    private static String settingDatas = "settingsDictionaryDatas";
    private static String rainDayDatas = "rainyDayDictionaryDatas";
    private static String dictionaryFields = "dictionaryFields";

    @Autowired
    public DecisionPolicyDictionaryController(CommonClassDao commonClassDao) {
        DecisionPolicyDictionaryController.commonClassDao = commonClassDao;
    }

    public DecisionPolicyDictionaryController() {
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance() {
        return DictionaryUtils.getDictionaryUtils();
    }

    @GetMapping(
            value = {"/get_SettingsDictionaryDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getSettingsDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, settingDatas, xacmlId, DecisionSettings.class);
    }

    @GetMapping(
            value = {"/get_SettingsDictionaryData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getSettingsDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, settingDatas, DecisionSettings.class);
    }

    @PostMapping(value = {"/decision_dictionary/save_Settings"})
    public ModelAndView saveSettingsDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            DecisionSettings decisionSettings;
            String userId = null;

            if (fromAPI) {
                decisionSettings = mapper.readValue(root.get(dictionaryFields).toString(), DecisionSettings.class);
                userId = "API";
            } else {
                decisionSettings =
                        mapper.readValue(root.get("settingsDictionaryData").toString(), DecisionSettings.class);
                userId = root.get("userid").textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);
            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(decisionSettings.getXacmlId(), xacmlId, DecisionSettings.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                DecisionSettings data = (DecisionSettings) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    decisionSettings.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != decisionSettings.getId()))) {
                    duplicateflag = true;
                }
            }
            if (decisionSettings.getDatatypeBean().getShortName() != null) {
                String datatype = decisionSettings.getDatatypeBean().getShortName();
                decisionSettings.setDatatypeBean(utils.getDataType(datatype));
            }
            String responseString = null;
            if (!duplicateflag) {
                decisionSettings.setUserModifiedBy(userInfo);
                if (decisionSettings.getId() == 0) {
                    decisionSettings.setUserCreatedBy(userInfo);
                    commonClassDao.save(decisionSettings);
                } else {
                    decisionSettings.setModifiedDate(new Date());
                    commonClassDao.update(decisionSettings);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(DecisionSettings.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, settingDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/settings_dictionary/remove_settings"})
    public void removeSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, settingDatas, DecisionSettings.class);
    }

    @GetMapping(
            value = {"/get_RainyDayDictionaryDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getRainyDayDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, rainDayDatas, bbID, RainyDayTreatments.class);
    }

    @GetMapping(
            value = {"/get_RainyDayDictionaryData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getRainyDayDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, rainDayDatas, RainyDayTreatments.class);
    }

    @PostMapping(value = {"/decision_dictionary/save_RainyDay"})
    public ModelAndView saveRainyDayDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            RainyDayTreatments decisionRainyDay;
            TreatmentValues treatmentsData = null;
            if (fromAPI) {
                decisionRainyDay = mapper.readValue(root.get(dictionaryFields).toString(), RainyDayTreatments.class);
                treatmentsData = mapper.readValue(root.get(dictionaryFields).toString(), TreatmentValues.class);
            } else {
                decisionRainyDay =
                        mapper.readValue(root.get("rainyDayDictionaryData").toString(), RainyDayTreatments.class);
                treatmentsData = mapper.readValue(root.get("rainyDayDictionaryData").toString(), TreatmentValues.class);
            }
            decisionRainyDay.setTreatments(utils.appendKey(treatmentsData.getUserDataTypeValues(), "treatment", ","));

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(
                    decisionRainyDay.getBbid() + ":" + decisionRainyDay.getWorkstep(), "bbid:workstep",
                    RainyDayTreatments.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                RainyDayTreatments data = (RainyDayTreatments) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    decisionRainyDay.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != decisionRainyDay.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                if (decisionRainyDay.getId() == 0) {
                    commonClassDao.save(decisionRainyDay);
                } else {
                    commonClassDao.update(decisionRainyDay);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(RainyDayTreatments.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, rainDayDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/decision_dictionary/remove_rainyDay"})
    public void removeRainyDayDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, rainDayDatas, RainyDayTreatments.class);
    }

}


class TreatmentValues {
    private List<Object> userDataTypeValues = new ArrayList<>();

    public List<Object> getUserDataTypeValues() {
        return userDataTypeValues;
    }

    public void setUserDataTypeValues(List<Object> userDataTypeValues) {
        this.userDataTypeValues = userDataTypeValues;
    }
}
