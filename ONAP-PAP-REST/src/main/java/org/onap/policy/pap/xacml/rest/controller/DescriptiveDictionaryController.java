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

import org.onap.policy.pap.xacml.rest.adapters.GridData;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DescriptiveScope;
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
public class DescriptiveDictionaryController {

    private static CommonClassDao commonClassDao;
    private static String operation = "operation";
    private static String dScopeName = "descriptiveScopeName";
    private static String descriptiveDatas = "descriptiveScopeDictionaryDatas";

    @Autowired
    public DescriptiveDictionaryController(CommonClassDao commonClassDao) {
        DescriptiveDictionaryController.commonClassDao = commonClassDao;
    }

    public void setCommonClassDao(CommonClassDao commonClassDao) {
        DescriptiveDictionaryController.commonClassDao = commonClassDao;
    }

    public DescriptiveDictionaryController() {
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance() {
        return DictionaryUtils.getDictionaryUtils();
    }

    @GetMapping(
            value = {"/get_DescriptiveScopeByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getDescriptiveDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, descriptiveDatas, dScopeName, DescriptiveScope.class);
    }

    @GetMapping(
            value = {"/get_DescriptiveScope"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getDescriptiveDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, descriptiveDatas, DescriptiveScope.class);
    }

    @PostMapping(value = {"/descriptive_dictionary/save_descriptive"})
    public ModelAndView saveDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            DescriptiveScope descriptiveScope;
            GridData data;
            String userId = null;
            if (fromAPI) {
                descriptiveScope = mapper.readValue(root.get("dictionaryFields").toString(), DescriptiveScope.class);
                data = mapper.readValue(root.get("dictionaryFields").toString(), GridData.class);
                userId = "API";
            } else {
                descriptiveScope =
                        mapper.readValue(root.get("descriptiveScopeDictionaryData").toString(), DescriptiveScope.class);
                data = mapper.readValue(root.get("descriptiveScopeDictionaryData").toString(), GridData.class);
                userId = root.get("userid").textValue();
            }
            descriptiveScope.setSearch(utils.appendKeyValue(data.getAttributes(), "AND", ":"));
            UserInfo userInfo = utils.getUserInfo(userId);
            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(descriptiveScope.getScopeName(), dScopeName,
                    DescriptiveScope.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                DescriptiveScope data1 = (DescriptiveScope) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    descriptiveScope.setId(data1.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data1.getId() != descriptiveScope.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                descriptiveScope.setUserModifiedBy(userInfo);
                if (descriptiveScope.getId() == 0) {
                    descriptiveScope.setUserCreatedBy(userInfo);
                    commonClassDao.save(descriptiveScope);
                } else {
                    descriptiveScope.setModifiedDate(new Date());
                    commonClassDao.update(descriptiveScope);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(DescriptiveScope.class));
            } else {
                responseString = "Duplicate";
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, descriptiveDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/descriptive_dictionary/remove_descriptiveScope"})
    public void removeDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, descriptiveDatas, DescriptiveScope.class);
    }
}
