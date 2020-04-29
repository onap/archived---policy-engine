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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Attribute;
import org.onap.policy.rest.jpa.OnapName;
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
public class DictionaryController {

    private static final Log LOGGER = LogFactory.getLog(DictionaryController.class);

    private static CommonClassDao commonClassDao;
    private static String xacmlId = "xacmlId";
    private static String operation = "operation";
    private static String dictionaryFields = "dictionaryFields";
    private static String duplicateResponseString = "Duplicate";
    private static String onapName = "name";
    private static String attributeDatas = "attributeDictionaryDatas";
    private static String onapNameDatas = "onapNameDictionaryDatas";

    @Autowired
    public DictionaryController(CommonClassDao commonClassDao) {
        DictionaryController.commonClassDao = commonClassDao;
    }

    public DictionaryController() {
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance() {
        return DictionaryUtils.getDictionaryUtils();
    }

    @GetMapping(
            value = {"/get_AttributeDatabyAttributeName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getAttributeDictionaryEntityDatabyAttributeName(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, attributeDatas, xacmlId, Attribute.class);
    }

    // Attribute Dictionary
    @GetMapping(
            value = "/get_AttributeData",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getAttributeDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, attributeDatas, Attribute.class);
    }

    @PostMapping(value = {"/attribute_dictionary/save_attribute"})
    public ModelAndView saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            Attribute attributeData = null;
            AttributeValues attributeValueData = null;
            String userId = null;
            if (fromAPI) {
                attributeData = mapper.readValue(root.get(dictionaryFields).toString(), Attribute.class);
                attributeValueData = mapper.readValue(root.get(dictionaryFields).toString(), AttributeValues.class);
                userId = "API";
            } else {
                attributeData = mapper.readValue(root.get("attributeDictionaryData").toString(), Attribute.class);
                attributeValueData =
                        mapper.readValue(root.get("attributeDictionaryData").toString(), AttributeValues.class);
                userId = root.get("userid").textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);
            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(attributeData.getXacmlId(), xacmlId, Attribute.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                Attribute data = (Attribute) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    attributeData.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != attributeData.getId()))) {
                    duplicateflag = true;
                }
            }
            if (attributeValueData.getUserDataTypeValues() != null
                    && !attributeValueData.getUserDataTypeValues().isEmpty()) {
                attributeData.setAttributeValue(
                        utils.appendKey(attributeValueData.getUserDataTypeValues(), "attributeValues", ","));
            }

            if (attributeData.getDatatypeBean().getShortName() != null) {
                String datatype = attributeData.getDatatypeBean().getShortName();
                attributeData.setDatatypeBean(utils.getDataType(datatype));
            }

            String responseString = null;
            if (!duplicateflag) {
                attributeData.setUserModifiedBy(userInfo);
                if (attributeData.getId() == 0) {
                    attributeData.setCategoryBean(utils.getCategory());
                    attributeData.setUserCreatedBy(userInfo);
                    commonClassDao.save(attributeData);
                } else {
                    attributeData.setModifiedDate(new Date());
                    commonClassDao.update(attributeData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(Attribute.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, attributeDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/attribute_dictionary/remove_attribute"})
    public void removeAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, attributeDatas, Attribute.class);
    }

    // OnapName Dictionary
    @GetMapping(
            value = {"/get_OnapNameDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getOnapNameDictionaryByNameEntityData(HttpServletResponse response) {
        LOGGER.info("get_OnapNameDataByName is called");
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, onapNameDatas, onapName, OnapName.class);
    }

    @GetMapping(
            value = {"/get_OnapNameData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getOnapNameDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, onapNameDatas, OnapName.class);
    }

    @PostMapping(value = {"/onap_dictionary/save_onapName"})
    public ModelAndView saveOnapDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            OnapName onapData;
            String userId = null;
            if (fromAPI) {
                onapData = mapper.readValue(root.get(dictionaryFields).toString(), OnapName.class);
                userId = "API";
            } else {
                onapData = mapper.readValue(root.get("onapNameDictionaryData").toString(), OnapName.class);
                userId = root.get("userid").textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(onapData.getName(), onapName, OnapName.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                OnapName data = (OnapName) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    onapData.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != onapData.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                onapData.setUserModifiedBy(userInfo);
                if (onapData.getId() == 0) {
                    onapData.setUserCreatedBy(userInfo);
                    commonClassDao.save(onapData);
                } else {
                    onapData.setModifiedDate(new Date());
                    commonClassDao.update(onapData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(OnapName.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, onapNameDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/onap_dictionary/remove_onap"})
    public void removeOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, onapNameDatas, OnapName.class);
    }
}


class AttributeValues {
    private List<Object> userDataTypeValues;

    public List<Object> getUserDataTypeValues() {
        return userDataTypeValues;
    }

    public void setUserDataTypeValues(List<Object> userDataTypeValues) {
        this.userDataTypeValues = userDataTypeValues;
    }
}
