/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2020 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.pap.xacml.rest.policycontroller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.ActionPolicy;
import org.onap.policy.pap.xacml.rest.components.ClosedLoopPolicy;
import org.onap.policy.pap.xacml.rest.components.ConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.CreateBrmsParamPolicy;
import org.onap.policy.pap.xacml.rest.components.CreateBrmsRawPolicy;
import org.onap.policy.pap.xacml.rest.components.CreateClosedLoopPerformanceMetrics;
import org.onap.policy.pap.xacml.rest.components.DecisionPolicy;
import org.onap.policy.pap.xacml.rest.components.FirewallConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.MicroServiceConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.OptimizationConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.Policy;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDaoTransaction;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.pap.xacml.rest.util.AbstractPolicyCreation;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.BrmsParamTemplate;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class PolicyCreation.
 */
@RestController
@RequestMapping("/")
public class PolicyCreation extends AbstractPolicyCreation {
    private static final Logger LOGGER = FlexLogger.getLogger(PolicyCreation.class);

    // Recurring constants
    private static final String INVALID_ATTRIBUTE = "invalidAttribute";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String POLICY_NAME = "policyName";

    private String ruleID = "";
    private String clName = null;

    private static CommonClassDao commonClassDao;

    /**
     * Gets the common class dao.
     *
     * @return the common class dao
     */
    public static CommonClassDao getCommonClassDao() {
        return commonClassDao;
    }

    /**
     * Sets the common class dao.
     *
     * @param commonClassDao the new common class dao
     */
    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        PolicyCreation.commonClassDao = commonClassDao;
    }

    /**
     * Instantiates a new policy creation.
     *
     * @param commonClassDao the common class dao
     */
    @Autowired
    public PolicyCreation(CommonClassDao commonClassDao) {
        PolicyCreation.commonClassDao = commonClassDao;
    }

    /**
     * Instantiates a new policy creation.
     */
    public PolicyCreation() {}

    /**
     * Save policy.
     *
     * @param policyData the policy data
     * @param response the response
     * @return the response entity
     */
    @RequestMapping(value = "/policycreation/save_policy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> savePolicy(@RequestBody PolicyRestAdapter policyData, HttpServletResponse response) {
        String body = null;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> successMap = new HashMap<>();
        Map<String, String> attributeMap = new HashMap<>();
        PolicyVersion policyVersionDao;
        try {

            Policy newPolicy = null;
            String policyConfigType = null;
            String userId = policyData.getUserId();

            if (policyData.getTtlDate() == null) {
                policyData.setTtlDate("NA");
            } else {
                String dateTtl = policyData.getTtlDate();
                String newDate = convertDate(dateTtl);
                policyData.setTtlDate(newDate);
            }

            String policyType = policyData.getPolicyType();

            String filePrefix = null;
            if ("Config".equalsIgnoreCase(policyType)) {
                policyConfigType = policyData.getConfigPolicyType();
                if ("Firewall Config".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_FW_";
                } else if ("ClosedLoop_Fault".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_Fault_";
                } else if ("ClosedLoop_PM".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_PM_";
                } else if ("Micro Service".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_MS_";
                } else if ("Optimization".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_OOF_";
                } else if ("BRMS_Raw".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_BRMS_Raw_";
                } else if ("BRMS_Param".equalsIgnoreCase(policyConfigType)) {
                    filePrefix = "Config_BRMS_Param_";
                } else {
                    filePrefix = "Config_";
                }
            } else if ("Action".equalsIgnoreCase(policyType)) {
                filePrefix = "Action_";
            } else if ("Decision".equalsIgnoreCase(policyType)) {
                filePrefix = "Decision_";
            }

            int version = 0;
            int highestVersion = 0;
            String createdBy;
            String modifiedBy;
            String scopeCheck = policyData.getDomainDir().replace(".", File.separator);
            PolicyEditorScopes policyEditorScope = (PolicyEditorScopes) commonClassDao
                    .getEntityItem(PolicyEditorScopes.class, "scopeName", scopeCheck);
            if (policyEditorScope == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserName("API");
                userInfo.setUserLoginId("API");
                PolicyEditorScopes editorScope = new PolicyEditorScopes();
                editorScope.setScopeName(scopeCheck);
                editorScope.setUserCreatedBy(userInfo);
                editorScope.setUserModifiedBy(userInfo);
                commonClassDao.save(editorScope);
            }
            // get the highest version of policy from policy version table.
            String dbCheckPolicyName =
                    policyData.getDomainDir() + File.separator + filePrefix + policyData.getPolicyName();
            PolicyVersion policyVersion = getPolicyVersionData(dbCheckPolicyName);
            if (policyVersion == null) {
                highestVersion = 0;
            } else {
                highestVersion = policyVersion.getHigherVersion();
            }

            if (highestVersion != 0 && policyVersion != null) {
                if (policyData.isEditPolicy()) {
                    version = highestVersion + 1;
                    if (userId == null) {
                        modifiedBy = "API";
                    } else {
                        modifiedBy = userId;
                    }
                    policyData.setUserId("API");
                    createdBy = policyVersion.getCreatedBy();
                    policyVersionDao = policyVersion;
                    policyVersionDao.setActiveVersion(version);
                    policyVersionDao.setHigherVersion(version);
                    policyVersionDao.setModifiedBy(modifiedBy);
                    policyVersionDao.setModifiedDate(new Date());
                } else {
                    body = "policyExists";
                    status = HttpStatus.CONFLICT;
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.addHeader(ERROR, "policyExists");
                    response.addHeader(POLICY_NAME, policyData.getPolicyName());
                    return new ResponseEntity<>(body, status);
                }
            } else {
                // if policy does not exist and the request is updatePolicy return error
                if (policyData.isEditPolicy()) {
                    body = "policyNotAvailableForEdit";
                    status = HttpStatus.NOT_FOUND;
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.addHeader(ERROR, body);
                    response.addHeader("message",
                            policyData.getPolicyName() + " does not exist on the PAP and cannot be updated.");
                    return new ResponseEntity<>(body, status);
                }
                version = 1;
                if (userId == null) {
                    createdBy = "API";
                    modifiedBy = "API";
                    policyData.setUserId("API");
                } else {
                    createdBy = userId;
                    modifiedBy = userId;
                    policyData.setUserId("API");
                }
                policyVersionDao = new PolicyVersion();
                policyVersionDao.setPolicyName(dbCheckPolicyName);
                policyVersionDao.setActiveVersion(version);
                policyVersionDao.setHigherVersion(version);
                policyVersionDao.setCreatedBy(createdBy);
                policyVersionDao.setModifiedBy(modifiedBy);
            }

            policyData.setPolicyID(newPolicyID());
            policyData.setRuleID(ruleID);

            String policyFileName = dbCheckPolicyName.replace(File.separator, ".") + "." + version + ".xml";
            policyData.setNewFileName(policyFileName);
            policyData.setPolicyDescription(policyData.getPolicyDescription() + "@CreatedBy:" + createdBy
                    + "@CreatedBy:" + "@ModifiedBy:" + modifiedBy + "@ModifiedBy:");
            policyData.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
            if (policyData.getApiflag() == null) {
                // set the Rule Combining Algorithm Id to be sent to PAP-REST via JSON
                if (policyData.getAttributes() != null && !policyData.getAttributes().isEmpty()) {
                    for (Object attribute : policyData.getAttributes()) {
                        if (attribute instanceof LinkedHashMap<?, ?>) {
                            String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
                            String value = ((LinkedHashMap<?, ?>) attribute).get("value").toString();
                            attributeMap.put(key, value);
                        }
                    }
                }
                policyData.setDynamicFieldConfigAttributes(attributeMap);
            }

            policyData.setVersion(String.valueOf(version));
            policyData.setHighestVersion(version);

            // Calling Component class per policy type
            if ("Config".equalsIgnoreCase(policyType)) {
                if ("Firewall Config".equalsIgnoreCase(policyConfigType)) {
                    newPolicy = new FirewallConfigPolicy(policyData);
                } else if ("BRMS_Raw".equalsIgnoreCase(policyConfigType)) {
                    policyData.setOnapName("DROOLS");
                    policyData.setConfigName("BRMS_RAW_RULE");
                    newPolicy = new CreateBrmsRawPolicy(policyData);
                } else if ("BRMS_Param".equalsIgnoreCase(policyConfigType)) {
                    policyData.setOnapName("DROOLS");
                    policyData.setConfigName("BRMS_PARAM_RULE");
                    Map<String, String> drlRuleAndUiParams = new HashMap<>();
                    if (policyData.getApiflag() == null) {
                        // If there is any dynamic field create the matches here
                        String key = "templateName";
                        String value = policyData.getRuleName();
                        drlRuleAndUiParams.put(key, value);
                        if (policyData.getRuleData().size() > 0) {
                            for (Object keyValue : policyData.getRuleData().keySet()) {
                                drlRuleAndUiParams.put(keyValue.toString(),
                                        policyData.getRuleData().get(keyValue).toString());
                            }
                        }
                        policyData.setBrmsParamBody(drlRuleAndUiParams);
                    } else {
                        drlRuleAndUiParams = policyData.getBrmsParamBody();
                        String modelName = drlRuleAndUiParams.get("templateName");
                        PolicyLogger.info("Template name from API is: " + modelName);

                        BrmsParamTemplate template = (BrmsParamTemplate) commonClassDao
                                .getEntityItem(BrmsParamTemplate.class, "ruleName", modelName);
                        if (template == null) {
                            String message =
                                    XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Template.  The template name, "
                                            + modelName + " was not found in the dictionary.";
                            body = message;
                            status = HttpStatus.BAD_REQUEST;
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.addHeader(ERROR, message);
                            response.addHeader("modelName", modelName);
                            return new ResponseEntity<>(body, status);
                        }
                    }
                    newPolicy = new CreateBrmsParamPolicy(policyData);
                } else if ("Base".equalsIgnoreCase(policyConfigType)) {
                    newPolicy = new ConfigPolicy(policyData);
                } else if ("ClosedLoop_Fault".equalsIgnoreCase(policyConfigType)) {
                    newPolicy = new ClosedLoopPolicy(policyData);
                } else if ("ClosedLoop_PM".equalsIgnoreCase(policyConfigType)) {
                    if (policyData.getApiflag() == null) {
                        policyData.setServiceType(
                                policyData.getServiceTypePolicyName().get("serviceTypePolicyName").toString());
                        ObjectMapper jsonMapper = new ObjectMapper();
                        String jsonBody = jsonMapper.writeValueAsString(policyData.getJsonBodyData());
                        jsonBody = jsonBody.replaceFirst("\\{",
                                "\\{\"serviceTypePolicyName\": \"serviceTypeFieldValue\",");
                        jsonBody = jsonBody.replace("serviceTypeFieldValue", policyData.getServiceType());
                        policyData.setJsonBody(jsonBody);
                    }
                    newPolicy = new CreateClosedLoopPerformanceMetrics(policyData);
                } else if ("Micro Service".equalsIgnoreCase(policyConfigType)) {
                    newPolicy = new MicroServiceConfigPolicy(policyData);
                } else if ("Optimization".equalsIgnoreCase(policyConfigType)) {
                    newPolicy = new OptimizationConfigPolicy(policyData);
                }
            } else if ("Action".equalsIgnoreCase(policyType)) {
                if (policyData.getApiflag() == null) {
                    List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
                    List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
                    List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
                    List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();

                    if (!policyData.getRuleAlgorithmschoices().isEmpty()) {
                        for (Object attribute : policyData.getRuleAlgorithmschoices()) {
                            if (attribute instanceof LinkedHashMap<?, ?>) {
                                String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
                                dynamicRuleAlgorithmLabels.add(label);
                                String key =
                                        ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
                                dynamicRuleAlgorithmField1.add(key);
                                String rule =
                                        ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
                                dynamicRuleAlgorithmCombo.add(rule);
                                String value =
                                        ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
                                dynamicRuleAlgorithmField2.add(value);
                            }
                        }
                    }

                    String actionDictValue = policyData.getActionAttributeValue();
                    ActionPolicyDict jsonData = ((ActionPolicyDict) commonClassDao.getEntityItem(ActionPolicyDict.class,
                            "attributeName", actionDictValue));
                    if (jsonData != null) {
                        String actionDictHeader = jsonData.getHeader();
                        policyData.setActionDictHeader(actionDictHeader);
                        String actionDictType = jsonData.getType();
                        policyData.setActionDictType(actionDictType);
                        String actionDictUrl = jsonData.getUrl();
                        policyData.setActionDictUrl(actionDictUrl);
                        String actionDictMethod = jsonData.getMethod();
                        policyData.setActionDictMethod(actionDictMethod);
                        String actionBodyString = jsonData.getBody();
                        if (actionBodyString != null) {
                            policyData.setActionBody(actionBodyString);
                        }
                    }
                    policyData.setActionAttribute(actionDictValue);
                    policyData.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
                    policyData.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
                    policyData.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
                    policyData.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
                } else {
                    // API request.
                    String comboDictValue = policyData.getActionAttribute();
                    ActionPolicyDict jsonData = ((ActionPolicyDict) commonClassDao.getEntityItem(ActionPolicyDict.class,
                            "attributeName", comboDictValue));
                    if (jsonData != null) {
                        policyData.setActionBody(jsonData.getBody());
                        policyData.setActionDictHeader(jsonData.getHeader());
                        policyData.setActionDictType(jsonData.getType());
                        policyData.setActionDictUrl(jsonData.getUrl());
                        policyData.setActionDictMethod(jsonData.getMethod());
                    }
                }
                newPolicy = new ActionPolicy(policyData, commonClassDao);
            } else if ("Decision".equalsIgnoreCase(policyType)) {
                if (policyData.getApiflag() == null) {
                    Map<String, String> treatmentMap = new HashMap<>();
                    Map<String, String> settingsMap = new HashMap<>();
                    if (!policyData.getSettings().isEmpty()) {
                        for (Object settingsData : policyData.getSettings()) {
                            if (settingsData instanceof LinkedHashMap<?, ?>) {
                                String key = ((LinkedHashMap<?, ?>) settingsData).get("key").toString();
                                String value = ((LinkedHashMap<?, ?>) settingsData).get("value").toString();
                                settingsMap.put(key, value);
                            }
                        }
                    }

                    List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
                    List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
                    List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
                    List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();
                    if (policyData.getRuleAlgorithmschoices() != null
                            && !policyData.getRuleAlgorithmschoices().isEmpty()) {
                        for (Object attribute : policyData.getRuleAlgorithmschoices()) {
                            if (attribute instanceof LinkedHashMap<?, ?>) {
                                String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
                                dynamicRuleAlgorithmLabels.add(label);
                                String key =
                                        ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
                                dynamicRuleAlgorithmField1.add(key);
                                String rule =
                                        ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
                                dynamicRuleAlgorithmCombo.add(rule);
                                String value =
                                        ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
                                dynamicRuleAlgorithmField2.add(value);
                            }
                        }
                    }
                    if (policyData.getRuleProvider() != null
                            && (policyData.getRuleProvider().equals(DecisionPolicy.GUARD_YAML)
                                    || policyData.getRuleProvider().equals(DecisionPolicy.GUARD_BL_YAML)
                                    || policyData.getRuleProvider().equals(DecisionPolicy.GUARD_MIN_MAX))
                            && policyData.getYamlparams() != null) {
                        attributeMap.put("actor", policyData.getYamlparams().getActor());
                        attributeMap.put("recipe", policyData.getYamlparams().getRecipe());
                        attributeMap.put("clname", policyData.getYamlparams().getClname());
                        attributeMap.put("limit", policyData.getYamlparams().getLimit());
                        attributeMap.put("min", policyData.getYamlparams().getMin());
                        attributeMap.put("max", policyData.getYamlparams().getMax());
                        attributeMap.put("timeWindow", policyData.getYamlparams().getTimeWindow());
                        attributeMap.put("timeUnits", policyData.getYamlparams().getTimeUnits());
                        attributeMap.put("guardActiveStart", policyData.getYamlparams().getGuardActiveStart());
                        attributeMap.put("guardActiveEnd", policyData.getYamlparams().getGuardActiveEnd());
                        if (policyData.getYamlparams().getBlackList() != null) {
                            String blackList = StringUtils.join(policyData.getYamlparams().getBlackList(), ",");
                            attributeMap.put("blackList", blackList);
                        }
                        if (DecisionPolicy.GUARD_BL_YAML.equals(policyData.getRuleProvider())
                                && "Use File Upload".equals(policyData.getBlackListEntryType())) {
                            if (policyData.getBlackListEntries() != null
                                    && !policyData.getBlackListEntries().isEmpty()) {
                                String blackList = StringUtils.join(policyData.getBlackListEntries(), ",");
                                attributeMap.put("blackList", blackList);
                            }
                            if (policyData.getAppendBlackListEntries() != null
                                    && !policyData.getAppendBlackListEntries().isEmpty()) {
                                String blackList = StringUtils.join(policyData.getAppendBlackListEntries(), ",");
                                attributeMap.put("appendBlackList", blackList);
                            }
                        }
                        if (policyData.getYamlparams().getTargets() != null) {
                            String targets = StringUtils.join(policyData.getYamlparams().getTargets(), ",");
                            attributeMap.put("targets", targets);
                        }
                    }
                    if (policyData.getRuleProvider() != null
                            && policyData.getRuleProvider().equals(DecisionPolicy.RAINY_DAY)) {
                        attributeMap.put("ServiceType", policyData.getRainyday().getServiceType());
                        attributeMap.put("VNFType", policyData.getRainyday().getVnfType());
                        attributeMap.put("BB_ID", policyData.getRainyday().getBbid());
                        attributeMap.put("WorkStep", policyData.getRainyday().getWorkstep());

                        if (policyData.getRainyday().getTreatmentTableChoices() != null
                                && policyData.getRainyday().getTreatmentTableChoices().isEmpty()) {
                            for (Object table : policyData.getRainyday().getTreatmentTableChoices()) {
                                if (table instanceof LinkedHashMap<?, ?>) {
                                    String errorcode = ((LinkedHashMap<?, ?>) table).get("errorcode").toString();
                                    String treatment = ((LinkedHashMap<?, ?>) table).get("treatment").toString();
                                    treatmentMap.put(errorcode, treatment);
                                }
                            }
                        }
                    }

                    List<Object> dynamicVariableList = new LinkedList<>();
                    List<String> dataTypeList = new LinkedList<>();
                    List<String> errorCodeList = new LinkedList<>();
                    List<String> treatmentList = new LinkedList<>();

                    policyData.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
                    policyData.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
                    policyData.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
                    policyData.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
                    policyData.setDynamicVariableList(dynamicVariableList);
                    policyData.setDynamicSettingsMap(settingsMap);
                    policyData.setDynamicFieldConfigAttributes(attributeMap);
                    policyData.setDataTypeList(dataTypeList);
                    policyData.setRainydayMap(treatmentMap);
                    policyData.setErrorCodeList(errorCodeList);
                    policyData.setTreatmentList(treatmentList);
                }
                newPolicy = new DecisionPolicy(policyData);
            }

            if (newPolicy != null) {
                newPolicy.prepareToSave();
            } else {
                body = ERROR;
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(ERROR, ERROR);
                return new ResponseEntity<>(body, status);
            }

            PolicyDbDaoTransaction policyDbDaoTransaction = null;
            try {
                PolicyDbDao policyDbDao = PolicyDbDao.getPolicyDbDaoInstance();
                policyDbDaoTransaction = policyDbDao.getNewTransaction();
                policyDbDaoTransaction.createPolicy(newPolicy, policyData.getUserId());
                successMap = newPolicy.savePolicies();
                if (successMap.containsKey(SUCCESS)) {
                    policyDbDaoTransaction.commitTransaction();
                    if (policyData.isEditPolicy()) {
                        commonClassDao.update(policyVersionDao);
                    } else {
                        commonClassDao.save(policyVersionDao);
                    }
                    try {
                        PolicyElasticSearchController search = new PolicyElasticSearchController();
                        search.updateElk(policyData);
                    } catch (Exception e) {
                        LOGGER.error("Error Occured while saving policy to Elastic Database" + e);
                    }
                    body = SUCCESS;
                    status = HttpStatus.OK;
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.addHeader("successMapKey", SUCCESS);
                    response.addHeader(POLICY_NAME, policyData.getNewFileName());

                    // get message from the SafetyCheckerResults if present
                    String safetyCheckerResponse = policyData.getClWarning();
                    String existingClName = policyData.getExistingCLName();

                    // if safetyCheckerResponse is not null add a header to send back with response
                    if (safetyCheckerResponse != null) {
                        PolicyLogger.info("SafetyCheckerResponse message: " + safetyCheckerResponse);
                        response.addHeader("safetyChecker", safetyCheckerResponse);
                        response.addHeader("newCLName", clName);
                        response.addHeader("conflictCLName", existingClName);
                    } else {
                        PolicyLogger.info("SafetyCheckerResponse was empty or null.");
                    }

                } else if (successMap.containsKey(INVALID_ATTRIBUTE)) {
                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Could not fine "
                            + policyData.getActionAttribute() + " in the ActionPolicyDict table.");
                    body = INVALID_ATTRIBUTE;
                    status = HttpStatus.BAD_REQUEST;
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addHeader(INVALID_ATTRIBUTE, policyData.getActionAttribute());

                    String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Action Attribute";
                    response.addHeader(ERROR, message);
                    response.addHeader(POLICY_NAME, policyData.getPolicyName());
                } else if (successMap.containsKey("fwdberror")) {
                    policyDbDaoTransaction.rollbackTransaction();
                    body = "fwdberror";
                    status = HttpStatus.BAD_REQUEST;
                    String message = XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Error when inserting Firewall ConfigBody data into the database.";
                    PolicyLogger.error(message);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addHeader(ERROR, message);
                    response.addHeader(POLICY_NAME, policyData.getPolicyName());
                } else if (successMap.get(ERROR).equals("Validation Failed")) {
                    policyDbDaoTransaction.rollbackTransaction();
                    String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Error Validating the Policy on the PAP.";
                    PolicyLogger.error(message);
                    body = "Validation";
                    status = HttpStatus.BAD_REQUEST;
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addHeader(ERROR, message);
                    response.addHeader(POLICY_NAME, policyData.getPolicyName());
                } else {
                    policyDbDaoTransaction.rollbackTransaction();
                    body = ERROR;
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.addHeader(ERROR, ERROR);
                }
            } catch (Exception e) {
                LOGGER.error("Exception Occured : ", e);
                if (policyDbDaoTransaction != null) {
                    policyDbDaoTransaction.rollbackTransaction();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception Occured : " + e.getMessage(), e);
            body = ERROR;
            //
            // Because we are catching any old exception instead of a dedicated exception,
            // its possible the e.getMessage() returns a null value. You cannot add a header
            // to the response with a null value, it will throw an exception. This is something
            // this is undesirable.
            //
            response.addHeader(ERROR, (e.getMessage() == null ? "missing exception message" : e.getMessage()));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(body, status);
    }

    /**
     * Message not readable exception handler.
     *
     * @param req the req
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    public ResponseEntity<String> messageNotReadableExceptionHandler(HttpServletRequest req,
            HttpMessageNotReadableException exception) {
        LOGGER.error("Request not readable: {}", exception);
        StringBuilder message = new StringBuilder();
        message.append(exception.getMessage());
        if (exception.getCause() != null) {
            message.append(" Reason Caused: " + exception.getCause().getMessage());
        }
        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Gets the policy version data.
     *
     * @param dbCheckPolicyName the db check policy name
     * @return the policy version data
     */
    public PolicyVersion getPolicyVersionData(String dbCheckPolicyName) {
        PolicyVersion entityItem =
                (PolicyVersion) commonClassDao.getEntityItem(PolicyVersion.class, POLICY_NAME, dbCheckPolicyName);
        if (entityItem != null && entityItem.getPolicyName().equals(dbCheckPolicyName)) {
            return entityItem;
        }
        return entityItem;
    }
}
