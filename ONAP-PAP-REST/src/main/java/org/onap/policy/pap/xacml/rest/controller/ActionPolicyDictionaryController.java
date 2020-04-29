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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ActionPolicyDictionaryController {

    private static CommonClassDao commonClassDao;
    private static String operation = "operation";
    private static String attributeName = "attributeName";
    private static String actionDatas = "actionPolicyDictionaryDatas";

    @Autowired
    public ActionPolicyDictionaryController(CommonClassDao commonClassDao) {
        ActionPolicyDictionaryController.commonClassDao = commonClassDao;
    }

    public void setCommonClassDao(CommonClassDao commonClassDao) {
        ActionPolicyDictionaryController.commonClassDao = commonClassDao;
    }

    public ActionPolicyDictionaryController() {
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance() {
        return DictionaryUtils.getDictionaryUtils();
    }

    @GetMapping(
            value = {"/get_ActionPolicyDictDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getActionEntitybyName(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, actionDatas, attributeName, ActionPolicyDict.class);
    }

    @GetMapping(
            value = {"/get_ActionPolicyDictData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getActionPolicyDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, actionDatas, ActionPolicyDict.class);
    }

    @PostMapping(value = {"/action_dictionary/save_ActionDict"})
    public ModelAndView saveActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            ActionPolicyDict actionPolicyDict = null;
            ActionAdapter adapter = null;
            String userId = null;
            if (fromAPI) {
                actionPolicyDict = mapper.readValue(root.get("dictionaryFields").toString(), ActionPolicyDict.class);
                adapter = mapper.readValue(root.get("dictionaryFields").toString(), ActionAdapter.class);
                userId = "API";
            } else {
                actionPolicyDict =
                        mapper.readValue(root.get("actionPolicyDictionaryData").toString(), ActionPolicyDict.class);
                adapter = mapper.readValue(root.get("actionPolicyDictionaryData").toString(), ActionAdapter.class);
                userId = root.get("userid").textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(actionPolicyDict.getAttributeName(),
                    attributeName, ActionPolicyDict.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                ActionPolicyDict data = (ActionPolicyDict) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    actionPolicyDict.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != actionPolicyDict.getId()))) {
                    duplicateflag = true;
                }
            }
            actionPolicyDict.setHeader(utils.appendKeyValue(adapter.getHeaders(), ":", "="));

            String responseString = null;
            if (!duplicateflag) {
                actionPolicyDict.setUserModifiedBy(userInfo);
                if (actionPolicyDict.getId() == 0) {
                    actionPolicyDict.setUserCreatedBy(userInfo);
                    commonClassDao.save(actionPolicyDict);
                } else {
                    actionPolicyDict.setModifiedDate(new Date());
                    commonClassDao.update(actionPolicyDict);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(ActionPolicyDict.class));
            } else {
                responseString = "Duplicate";
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, actionDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/action_dictionary/remove_actionPolicyDict"})
    public void removeActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, actionDatas, ActionPolicyDict.class);
    }
}


class ActionAdapter {
    private List<Object> headers;

    public List<Object> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Object> headers) {
        this.headers = headers;
    }
}
