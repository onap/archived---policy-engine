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
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ClosedLoopD2Services;
import org.onap.policy.rest.jpa.ClosedLoopSite;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PepOptions;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.VnfType;
import org.onap.policy.rest.jpa.VsclAction;
import org.onap.policy.rest.jpa.VarbindDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClosedLoopDictionaryController {

    private static CommonClassDao commonClassDao;
    private static String vsclaction = "action";
    private static String operation = "operation";
    private static String vnftype = "type";
    private static String pepName = "pepName";
    private static String varbindName = "varbindName";
    private static String serviceName = "serviceName";
    private static String siteName = "siteName";
    private static String dictionaryFields = "dictionaryFields";
    private static String duplicateResponseString = "Duplicate";
    private static String userid = "userid";
    private static String vsclActionDatas = "vsclActionDictionaryDatas";
    private static String vnfTypeDatas = "vnfTypeDictionaryDatas";
    private static String pepOptionDatas = "pepOptionsDictionaryDatas";
    private static String varbindDatas = "varbindDictionaryDatas";
    private static String closedLoopDatas = "closedLoopServiceDictionaryDatas";
    private static String closedLoopSiteDatas = "closedLoopSiteDictionaryDatas";

    @Autowired
    public ClosedLoopDictionaryController(CommonClassDao commonClassDao) {
        ClosedLoopDictionaryController.commonClassDao = commonClassDao;
    }

    public void setCommonClassDao(CommonClassDao commonClassDao) {
        ClosedLoopDictionaryController.commonClassDao = commonClassDao;
    }

    public static void setCommonClassDao(CommonClassDaoImpl commonClassDaoImpl) {
        commonClassDao = commonClassDaoImpl;
    }

    public ClosedLoopDictionaryController() {
        super();
    }

    private DictionaryUtils getDictionaryUtilsInstance() {
        return DictionaryUtils.getDictionaryUtils();
    }

    @GetMapping(
            value = {"/get_VSCLActionDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getVSCLActionDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, vsclActionDatas, vsclaction, VsclAction.class);
    }

    @GetMapping(
            value = {"/get_VSCLActionData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getVSCLActionDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, vsclActionDatas, VsclAction.class);
    }

    @GetMapping(
            value = {"/get_VNFTypeDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getVNFTypeDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, vnfTypeDatas, vnftype, VnfType.class);
    }

    @GetMapping(
            value = {"/get_VNFTypeData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getVNFTypeDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, vnfTypeDatas, VnfType.class);
    }

    @GetMapping(
            value = {"/get_PEPOptionsDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getPEPOptionsDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, pepOptionDatas, pepName, PepOptions.class);
    }

    @GetMapping(
            value = {"/get_PEPOptionsData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getPEPOptionsDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, pepOptionDatas, PepOptions.class);
    }

    @GetMapping(
            value = {"/get_VarbindDictionaryDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getVarbindDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, varbindDatas, varbindName, VarbindDictionary.class);
    }

    @GetMapping(
            value = {"/get_VarbindDictionaryData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getVarbindDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, varbindDatas, VarbindDictionary.class);
    }

    @GetMapping(
            value = {"/get_ClosedLoopServicesDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getClosedLoopServiceDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, closedLoopDatas, serviceName, ClosedLoopD2Services.class);
    }

    @GetMapping(
            value = {"/get_ClosedLoopServicesData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getClosedLoopServiceDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, closedLoopDatas, ClosedLoopD2Services.class);
    }

    @GetMapping(
            value = {"/get_ClosedLoopSiteDataByName"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getClosedLoopSiteDictionaryByNameEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getDataByEntity(response, closedLoopSiteDatas, siteName, ClosedLoopSite.class);
    }

    @GetMapping(
            value = {"/get_ClosedLoopSiteData"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getClosedLoopSiteDictionaryEntityData(HttpServletResponse response) {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.getData(response, closedLoopSiteDatas, ClosedLoopSite.class);
    }

    @PostMapping(value = {"/cl_dictionary/save_vsclAction"})
    public ModelAndView saveVSCLAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            VsclAction vSCLAction;
            String userId = null;
            if (fromAPI) {
                vSCLAction = mapper.readValue(root.get(dictionaryFields).toString(), VsclAction.class);
                userId = "API";
            } else {
                vSCLAction = mapper.readValue(root.get("vsclActionDictionaryData").toString(), VsclAction.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(vSCLAction.getAction(), vsclaction, VsclAction.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                VsclAction data = (VsclAction) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    vSCLAction.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != vSCLAction.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                vSCLAction.setUserModifiedBy(userInfo);
                if (vSCLAction.getId() == 0) {
                    vSCLAction.setUserCreatedBy(userInfo);
                    commonClassDao.save(vSCLAction);
                } else {
                    vSCLAction.setModifiedDate(new Date());
                    commonClassDao.update(vSCLAction);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(VsclAction.class));
            } else {
                responseString = duplicateResponseString;
            }

            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, vsclActionDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/cl_dictionary/remove_VsclAction"})
    public void removeVSCLAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, vsclActionDatas, OnapName.class);
    }

    @PostMapping(value = {"/cl_dictionary/save_vnfType"})
    public ModelAndView saveVnfType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            VnfType vNFType;
            String userId = null;
            if (fromAPI) {
                vNFType = mapper.readValue(root.get(dictionaryFields).toString(), VnfType.class);
                userId = "API";
            } else {
                vNFType = mapper.readValue(root.get("vnfTypeDictionaryData").toString(), VnfType.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(vNFType.getType(), vnftype, VnfType.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                VnfType data = (VnfType) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    vNFType.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != vNFType.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                vNFType.setUserModifiedBy(userInfo);
                if (vNFType.getId() == 0) {
                    vNFType.setUserCreatedBy(userInfo);
                    commonClassDao.save(vNFType);
                } else {
                    vNFType.setModifiedDate(new Date());
                    commonClassDao.update(vNFType);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(VnfType.class));
            } else {
                responseString = duplicateResponseString;
            }

            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, vnfTypeDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/cl_dictionary/remove_vnfType"})
    public void removeVnfType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, vnfTypeDatas, VnfType.class);
    }

    @PostMapping(value = {"/cl_dictionary/save_pepOptions"})
    public ModelAndView savePEPOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PepOptions pEPOptions;
            GridData gridData;
            String userId = null;
            if (fromAPI) {
                pEPOptions = mapper.readValue(root.get(dictionaryFields).toString(), PepOptions.class);
                gridData = mapper.readValue(root.get(dictionaryFields).toString(), GridData.class);
                userId = "API";
            } else {
                pEPOptions = mapper.readValue(root.get("pepOptionsDictionaryData").toString(), PepOptions.class);
                gridData = mapper.readValue(root.get("pepOptionsDictionaryData").toString(), GridData.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            if (!gridData.getAttributes().isEmpty()) {
                pEPOptions.setActions(utils.appendKeyValue(gridData.getAttributes(), ":#@", "=#@"));
            }

            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(pEPOptions.getPepName(), pepName, PepOptions.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                PepOptions data = (PepOptions) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    pEPOptions.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != pEPOptions.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                pEPOptions.setUserModifiedBy(userInfo);
                if (pEPOptions.getId() == 0) {
                    pEPOptions.setUserCreatedBy(userInfo);
                    commonClassDao.save(pEPOptions);
                } else {
                    pEPOptions.setModifiedDate(new Date());
                    commonClassDao.update(pEPOptions);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(PepOptions.class));
            } else {
                responseString = duplicateResponseString;
            }

            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, pepOptionDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/cl_dictionary/remove_pepOptions"})
    public void removePEPOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, pepOptionDatas, VnfType.class);
    }

    @PostMapping(value = {"/cl_dictionary/save_service"})
    public ModelAndView saveServiceType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            ClosedLoopD2Services serviceData;
            String userId = null;
            if (fromAPI) {
                serviceData = mapper.readValue(root.get(dictionaryFields).toString(), ClosedLoopD2Services.class);
                userId = "API";
            } else {
                serviceData = mapper.readValue(root.get("closedLoopServiceDictionaryData").toString(),
                        ClosedLoopD2Services.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(serviceData.getServiceName(), serviceName,
                    ClosedLoopD2Services.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                ClosedLoopD2Services data = (ClosedLoopD2Services) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    serviceData.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != serviceData.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                serviceData.setUserModifiedBy(userInfo);
                if (serviceData.getId() == 0) {
                    serviceData.setUserCreatedBy(userInfo);
                    commonClassDao.save(serviceData);
                } else {
                    serviceData.setModifiedDate(new Date());
                    commonClassDao.update(serviceData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(ClosedLoopD2Services.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, closedLoopDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/cl_dictionary/remove_Service"})
    public void removeServiceType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, closedLoopDatas, VnfType.class);
    }

    @PostMapping(value = {"/cl_dictionary/save_siteName"})
    public ModelAndView saveSiteType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            ClosedLoopSite siteData;
            String userId = null;
            if (fromAPI) {
                siteData = mapper.readValue(root.get(dictionaryFields).toString(), ClosedLoopSite.class);
                userId = "API";
            } else {
                siteData = mapper.readValue(root.get("closedLoopSiteDictionaryData").toString(), ClosedLoopSite.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData =
                    commonClassDao.checkDuplicateEntry(siteData.getSiteName(), siteName, ClosedLoopSite.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                ClosedLoopSite data = (ClosedLoopSite) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    siteData.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != siteData.getId()))) {
                    duplicateflag = true;
                }
            }
            String responseString = null;
            if (!duplicateflag) {
                siteData.setUserModifiedBy(userInfo);
                if (siteData.getId() == 0) {
                    siteData.setUserCreatedBy(userInfo);
                    commonClassDao.save(siteData);
                } else {
                    siteData.setModifiedDate(new Date());
                    commonClassDao.update(siteData);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(ClosedLoopSite.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, closedLoopSiteDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/cl_dictionary/remove_site"})
    public void removeSiteType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, closedLoopSiteDatas, VnfType.class);
    }

    @PostMapping(value = {"/cl_dictionary/save_varbind"})
    public ModelAndView saveVarbind(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        try {
            boolean fromAPI = utils.isRequestFromAPI(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            VarbindDictionary varbindDictionary;
            String userId = null;
            if (fromAPI) {
                varbindDictionary = mapper.readValue(root.get(dictionaryFields).toString(), VarbindDictionary.class);
                userId = "API";
            } else {
                varbindDictionary =
                        mapper.readValue(root.get("varbindDictionaryData").toString(), VarbindDictionary.class);
                userId = root.get(userid).textValue();
            }
            UserInfo userInfo = utils.getUserInfo(userId);

            List<Object> duplicateData = commonClassDao.checkDuplicateEntry(varbindDictionary.getVarbindName(),
                    varbindName, VarbindDictionary.class);
            boolean duplicateflag = false;
            if (!duplicateData.isEmpty()) {
                VarbindDictionary data = (VarbindDictionary) duplicateData.get(0);
                if (request.getParameter(operation) != null && "update".equals(request.getParameter(operation))) {
                    varbindDictionary.setId(data.getId());
                } else if ((request.getParameter(operation) != null
                        && !"update".equals(request.getParameter(operation)))
                        || (request.getParameter(operation) == null && (data.getId() != varbindDictionary.getId()))) {
                    duplicateflag = true;
                }
            }

            String responseString = null;
            if (!duplicateflag) {
                varbindDictionary.setUserModifiedBy(userInfo);
                if (varbindDictionary.getId() == 0) {
                    varbindDictionary.setUserCreatedBy(userInfo);
                    commonClassDao.save(varbindDictionary);
                } else {
                    varbindDictionary.setModifiedDate(new Date());
                    commonClassDao.update(varbindDictionary);
                }
                responseString = mapper.writeValueAsString(commonClassDao.getData(VarbindDictionary.class));
            } else {
                responseString = duplicateResponseString;
            }
            if (fromAPI) {
                return utils.getResultForApi(responseString);
            } else {
                utils.setResponseData(response, varbindDatas, responseString);
            }
        } catch (Exception e) {
            utils.setErrorResponseData(response, e);
        }
        return null;
    }

    @PostMapping(value = {"/cl_dictionary/remove_varbindDict"})
    public void removeVarbind(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DictionaryUtils utils = getDictionaryUtilsInstance();
        utils.removeData(request, response, varbindDatas, VnfType.class);
    }
}
