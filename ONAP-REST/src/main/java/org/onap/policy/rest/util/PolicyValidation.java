/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.rest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import lombok.Getter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultBody;
import org.onap.policy.rest.adapter.ClosedLoopPMBody;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.OptimizationModels;
import org.onap.policy.rest.jpa.SafePolicyWarning;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.util.XACMLPolicyScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyValidation {
    private static final Logger LOGGER = FlexLogger.getLogger(PolicyValidation.class);

    private static final String ACTION_POLICY = "Action";
    private static final String BOOLEAN = "boolean";
    private static final String BRMSPARAM = "BRMS_Param";
    private static final String BRMSRAW = "BRMS_Raw";
    private static final String CLOSEDLOOP_PM = "ClosedLoop_PM";
    private static final String CLOSEDLOOP_POLICY = "ClosedLoop_Fault";
    private static final String CONFIG_POLICY = "Config";
    private static final String DECISION_MS_MODEL = "MicroService_Model";
    private static final String DECISION_POLICY = "Decision";
    private static final String DECISION_POLICY_MS = "Decision_MS";
    private static final String ENFORCER_CONFIG_POLICY = "Enforcer Config";
    private static final String FIREWALL = "Firewall Config";
    private static final String HTML_ITALICS_LNBREAK = "</i><br>";
    private static final String INTEGER = "integer";
    private static final String ISREQUIRED = " is required";
    private static final String JAVA = "java";
    private static final String LIST = "list";
    private static final String MAP = "map";
    private static final String MICROSERVICES = "Micro Service";
    private static final String MISSING_COMPONENT_ATTRIBUTE_VALUE = "<b>Component Attributes</b>:"
                    + "<i> has one missing Component Attribute value</i><br>";
    private static final String MISSING_ATTRIBUTE_VALUE = "<b>Rule Attributes</b>:"
                    + "<i> has one missing Attribute value</i><br>";
    private static final String MISSING_COMPONENT_ATTRIBUTE_KEY = "<b>Component Attributes</b>:"
                    + "<i> has one missing Component Attribute key</i><br>";
    private static final String OPTIMIZATION = "Optimization";
    private static final String RAW = "Raw";
    private static final String REQUIRED_ATTRIBUTE = "required-true";
    private static final String RULE_ALGORITHMS = "<b>Rule Algorithms</b>:<i>";
    private static final String SELECT_AT_LEAST_ONE_D2_VIRTUALIZED_SERVICES = "<b>D2/Virtualized Services</b>: "
                    + "<i>Select at least one D2/Virtualized Services";
    private static final String SPACESINVALIDCHARS = " : value has spaces or invalid characters</i><br>";
    private static final String STRING = "string";
    private static final String SUCCESS = "success";
    private static final String VALUE = "value";

    private static Map<String, String> mapAttribute = new HashMap<>();
    private static Map<String, String> jsonRequestMap = new HashMap<>();
    private static List<String> modelRequiredFieldsList = new ArrayList<>();

    @Getter
    private static CommonClassDao commonClassDao;

    private Set<String> allReqTrueKeys = new HashSet<>();
    private Set<String> allOptReqTrueKeys = new HashSet<>();

    @Autowired
    public PolicyValidation(CommonClassDao commonClassDao) {
        PolicyValidation.commonClassDao = commonClassDao;
    }

    /*
     * This is an empty constructor
     */
    public PolicyValidation() {
        // Empty constructor
    }

    /**
     * Validate policy.
     *
     * @param policyData the policy data
     * @return the string builder
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public StringBuilder validatePolicy(PolicyRestAdapter policyData) throws IOException {
        try {
            boolean valid = true;
            StringBuilder responseString = new StringBuilder();
            ObjectMapper mapper = new ObjectMapper();

            if (policyData.getPolicyName() != null) {
                String policyNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getPolicyName());
                if (!policyNameValidate.contains(SUCCESS)) {
                    responseString.append("<b>PolicyName</b>:<i>" + policyNameValidate + HTML_ITALICS_LNBREAK);
                    valid = false;
                }
            } else {
                responseString.append("<b>PolicyName</b>: PolicyName Should not be empty" + HTML_ITALICS_LNBREAK);
                valid = false;
            }
            if (policyData.getPolicyDescription() != null) {
                String descriptionValidate = PolicyUtils.descriptionValidator(policyData.getPolicyDescription());
                if (!descriptionValidate.contains(SUCCESS)) {
                    responseString.append("<b>Description</b>:<i>" + descriptionValidate + HTML_ITALICS_LNBREAK);
                    valid = false;
                }
            }

            if (!"API".equals(policyData.getApiflag()) && policyData.getAttributes() != null
                            && !policyData.getAttributes().isEmpty()) {
                for (Object attribute : policyData.getAttributes()) {
                    if (attribute instanceof LinkedHashMap<?, ?>) {
                        String attValue = null;
                        String key = null;
                        if (((LinkedHashMap<?, ?>) attribute).get("key") != null) {
                            key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
                            if (!PolicyUtils.policySpecialCharWithDashValidator(key).contains(SUCCESS)) {
                                responseString.append("<b>Attributes or Component Attributes</b>:<i>" + attValue
                                                + SPACESINVALIDCHARS);
                                valid = false;
                            }
                        } else {
                            if (CONFIG_POLICY.equals(policyData.getPolicyType())) {
                                if ("Base".equals(policyData.getConfigPolicyType())) {
                                    responseString.append(
                                                    "<b>Attributes</b>:<i> has one missing Attribute key</i><br>");
                                }
                                if (BRMSPARAM.equals(policyData.getConfigPolicyType())
                                                || BRMSRAW.equals(policyData.getConfigPolicyType())) {
                                    responseString.append(
                                                    "<b>Rule Attributes</b>:<i> has one missing Attribute key</i><br>");
                                }
                            } else {
                                responseString.append(MISSING_COMPONENT_ATTRIBUTE_KEY);
                            }
                            valid = false;
                        }
                        if (((LinkedHashMap<?, ?>) attribute).get(VALUE) != null) {
                            attValue = ((LinkedHashMap<?, ?>) attribute).get(VALUE).toString();
                            if (!PolicyUtils.policySpecialCharWithDashValidator(attValue).contains(SUCCESS)) {
                                if (CONFIG_POLICY.equals(policyData.getPolicyType())) {
                                    if ("Base".equals(policyData.getConfigPolicyType())) {
                                        responseString.append("<b>Attributes</b>:<i>" + attValue + SPACESINVALIDCHARS);
                                    }
                                    if (BRMSPARAM.equals(policyData.getConfigPolicyType())
                                                    || BRMSRAW.equals(policyData.getConfigPolicyType())) {
                                        responseString.append(
                                                        "<b>Rule Attributes</b>:<i>" + attValue + SPACESINVALIDCHARS);
                                    }
                                } else {
                                    responseString.append(
                                                    "<b>Component Attributes</b>:<i>" + attValue + SPACESINVALIDCHARS);
                                }
                                valid = false;
                            }
                        } else {
                            if (CONFIG_POLICY.equals(policyData.getPolicyType())) {
                                if ("Base".equals(policyData.getConfigPolicyType())) {
                                    responseString.append(
                                                    "<b>Attributes</b>:<i> has one missing Attribute value</i><br>");
                                }
                                if (BRMSPARAM.equals(policyData.getConfigPolicyType())
                                                || BRMSRAW.equals(policyData.getConfigPolicyType())) {
                                    responseString.append(MISSING_ATTRIBUTE_VALUE);
                                }
                            } else {
                                responseString.append(MISSING_COMPONENT_ATTRIBUTE_VALUE);
                            }
                            valid = false;
                        }
                    }
                }
            }

            // Decision Policy Attributes Validation
            if (!"API".equals(policyData.getApiflag()) && policyData.getSettings() != null
                            && !policyData.getSettings().isEmpty()) {
                for (Object attribute : policyData.getSettings()) {
                    if (attribute instanceof LinkedHashMap<?, ?>) {
                        String value = null;
                        if (((LinkedHashMap<?, ?>) attribute).get("key") == null) {
                            responseString.append(
                                            "<b>Settings Attributes</b>:<i> has one missing Attribute key</i><br>");
                            valid = false;
                        }
                        if (((LinkedHashMap<?, ?>) attribute).get(VALUE) != null) {
                            value = ((LinkedHashMap<?, ?>) attribute).get(VALUE).toString();
                            if (!PolicyUtils.policySpecialCharValidator(value).contains(SUCCESS)) {
                                responseString.append("<b>Settings Attributes</b>:<i>" + value + SPACESINVALIDCHARS);
                                valid = false;
                            }
                        } else {
                            responseString.append(
                                            "<b>Settings Attributes</b>:<i> has one missing Attribute Value</i><br>");
                            valid = false;
                        }
                    }
                }
            }

            if (!"API".equals(policyData.getApiflag()) && policyData.getRuleAlgorithmschoices() != null
                            && !policyData.getRuleAlgorithmschoices().isEmpty()) {
                for (Object attribute : policyData.getRuleAlgorithmschoices()) {
                    if (attribute instanceof LinkedHashMap<?, ?>) {
                        String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
                        if (((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1") == null) {
                            responseString.append(RULE_ALGORITHMS + label + " : Field 1 value is not selected</i><br>");
                            valid = false;
                        }
                        if (((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo") == null) {
                            responseString.append(RULE_ALGORITHMS + label + " : Field 2 value is not selected</i><br>");
                            valid = false;
                        }
                        if (((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2") != null) {
                            String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2")
                                            .toString();
                            if (!PolicyUtils.policySpecialCharValidator(value).contains(SUCCESS)) {
                                responseString.append(RULE_ALGORITHMS + label
                                                + " : Field 3 value has special characters</i><br>");
                                valid = false;
                            }
                        } else {
                            responseString.append(RULE_ALGORITHMS + label + " : Field 3 value is empty</i><br>");
                            valid = false;
                        }
                    }
                }
            }

            if (CONFIG_POLICY.equalsIgnoreCase(policyData.getPolicyType())) {
                if ("Base".equals(policyData.getConfigPolicyType())
                                || CLOSEDLOOP_POLICY.equals(policyData.getConfigPolicyType())
                                || CLOSEDLOOP_PM.equals(policyData.getConfigPolicyType())
                                || ENFORCER_CONFIG_POLICY.equals(policyData.getConfigPolicyType())
                                || MICROSERVICES.equals(policyData.getConfigPolicyType())
                                || OPTIMIZATION.equals(policyData.getConfigPolicyType())) {

                    if (!Strings.isNullOrEmpty(policyData.getOnapName())) {
                        String onapNameValidate = PolicyUtils
                                        .policySpecialCharWithDashValidator(policyData.getOnapName());
                        if (!onapNameValidate.contains(SUCCESS)) {
                            responseString.append("<b>OnapName</b>:<i>" + onapNameValidate + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("<b>Onap Name</b>: Onap Name Should not be empty" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }

                if (!Strings.isNullOrEmpty(policyData.getRiskType())) {
                    String riskTypeValidate = PolicyUtils.policySpecialCharValidator(policyData.getRiskType());
                    if (!riskTypeValidate.contains(SUCCESS)) {
                        responseString.append("<b>RiskType</b>:<i>" + riskTypeValidate + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                } else {
                    responseString.append("<b>RiskType</b>: Risk Type Should not be Empty" + HTML_ITALICS_LNBREAK);
                    valid = false;
                }

                if (!Strings.isNullOrEmpty(policyData.getRiskLevel())) {
                    String validateRiskLevel = PolicyUtils.policySpecialCharValidator(policyData.getRiskLevel());
                    if (!validateRiskLevel.contains(SUCCESS)) {
                        responseString.append("<b>RiskLevel</b>:<i>" + validateRiskLevel + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                } else {
                    responseString.append("<b>RiskLevel</b>: Risk Level Should not be Empty" + HTML_ITALICS_LNBREAK);
                    valid = false;
                }

                if (!Strings.isNullOrEmpty(policyData.getGuard())) {
                    String validateGuard = PolicyUtils.policySpecialCharValidator(policyData.getGuard());
                    if (!validateGuard.contains(SUCCESS)) {
                        responseString.append("<b>Guard</b>:<i>" + validateGuard + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                } else {
                    responseString.append("<b>Guard</b>: Guard Value Should not be Empty" + HTML_ITALICS_LNBREAK);
                    valid = false;
                }

                // Validate Config Base Policy Data
                if ("Base".equalsIgnoreCase(policyData.getConfigPolicyType())) {
                    if (!Strings.isNullOrEmpty(policyData.getConfigName())) {
                        String configNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigName());
                        if (!configNameValidate.contains(SUCCESS)) {
                            responseString.append("ConfigName:" + configNameValidate + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("Config Name: Config Name Should not be Empty" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                    if (!Strings.isNullOrEmpty(policyData.getConfigType())) {
                        String configTypeValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigType());
                        if (!configTypeValidate.contains(SUCCESS)) {
                            responseString.append("ConfigType:" + configTypeValidate + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("Config Type: Config Type Should not be Empty" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                    if (!Strings.isNullOrEmpty(policyData.getConfigBodyData())) {
                        String configBodyData = policyData.getConfigBodyData();
                        String configType = policyData.getConfigType();
                        if (configType != null) {
                            if ("JSON".equals(configType)) {
                                if (!PolicyUtils.isJSONValid(configBodyData)) {
                                    responseString.append(
                                                    "Config Body: JSON Content is not valid" + HTML_ITALICS_LNBREAK);
                                    valid = false;
                                }
                            } else if ("XML".equals(configType)) {
                                if (!PolicyUtils.isXMLValid(configBodyData)) {
                                    responseString.append("Config Body: XML Content data is not valid"
                                                    + HTML_ITALICS_LNBREAK);
                                    valid = false;
                                }
                            } else if ("PROPERTIES".equals(configType)) {
                                if (!PolicyUtils.isPropValid(configBodyData)) {
                                    responseString.append(
                                                    "Config Body: Property data is not valid" + HTML_ITALICS_LNBREAK);
                                    valid = false;
                                }
                            }
                        }
                    } else {
                        responseString.append("Config Body: Config Body Should not be Empty" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }
                // Validate Config Firewall Policy Data
                if (FIREWALL.equalsIgnoreCase(policyData.getConfigPolicyType())) {
                    if (policyData.getConfigName() != null && !policyData.getConfigName().isEmpty()) {
                        String configNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getConfigName());
                        if (!configNameValidate.contains(SUCCESS)) {
                            responseString.append("<b>ConfigName</b>:<i>" + configNameValidate + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("<b>Config Name</b>:<i> Config Name is required" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                    if (policyData.getSecurityZone() == null || policyData.getSecurityZone().isEmpty()) {
                        responseString.append(
                                        "<b>Security Zone</b>:<i> Security Zone is required" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }
                // Validate BRMS_Param Policy Data
                if (BRMSPARAM.equalsIgnoreCase(policyData.getConfigPolicyType())
                                && Strings.isNullOrEmpty(policyData.getRuleName())) {
                    responseString.append("<b>BRMS Template</b>:<i>BRMS Template is required" + HTML_ITALICS_LNBREAK);
                    valid = false;
                }
                // Validate BRMS_Raw Policy Data
                if (BRMSRAW.equalsIgnoreCase(policyData.getConfigPolicyType())) {
                    if (policyData.getConfigBodyData() != null && !policyData.getConfigBodyData().isEmpty()) {
                        String message = PolicyUtils.brmsRawValidate(policyData.getConfigBodyData());

                        // If there are any error other than Annotations then this is not Valid
                        if (message.contains("[ERR")) {
                            responseString.append("<b>Raw Rule Validate</b>:<i>Raw Rule has error" + message
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("<b>Raw Rule</b>:<i>Raw Rule is required" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }
                // Validate ClosedLoop_PM Policy Data
                if (CLOSEDLOOP_PM.equalsIgnoreCase(policyData.getConfigPolicyType())) {
                    try {
                        if (Strings.isNullOrEmpty(policyData.getServiceTypePolicyName().get("serviceTypePolicyName")
                                        .toString())) {
                            responseString.append("<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }

                    } catch (Exception e) {
                        LOGGER.error("ERROR in ClosedLoop_PM PolicyName", e);
                        responseString.append("<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required"
                                        + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }

                    if (policyData.getJsonBody() != null) {

                        ClosedLoopPMBody pmBody = mapper.readValue(policyData.getJsonBody(), ClosedLoopPMBody.class);
                        if (pmBody.getEmailAddress() != null) {
                            String result = emailValidation(pmBody.getEmailAddress(), responseString.toString());
                            if (result != SUCCESS) {
                                responseString.append(result + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                        }
                        if (!pmBody.isGamma() && !pmBody.isMcr() && !pmBody.isTrinity() && !pmBody.isvDNS()
                                        && !pmBody.isvUSP()) {
                            responseString.append(SELECT_AT_LEAST_ONE_D2_VIRTUALIZED_SERVICES + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (pmBody.getGeoLink() != null && !pmBody.getGeoLink().isEmpty()) {
                            String result = PolicyUtils.policySpecialCharValidator(pmBody.getGeoLink());
                            if (!result.contains(SUCCESS)) {
                                responseString.append("<b>GeoLink</b>:<i>" + result + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                        }
                        if (pmBody.getAttributes() != null && !pmBody.getAttributes().isEmpty()) {
                            for (Entry<String, String> entry : pmBody.getAttributes().entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if (!key.contains("Message")) {
                                    String attributeValidate = PolicyUtils.policySpecialCharValidator(value);
                                    if (!attributeValidate.contains(SUCCESS)) {
                                        responseString.append("<b>Attributes</b>:<i>" + key
                                                        + " : value has spaces or invalid characters"
                                                        + HTML_ITALICS_LNBREAK);
                                        valid = false;
                                    }
                                }
                            }
                        }
                    } else {
                        responseString.append(
                                        "<b>D2/Virtualized Services</b>:<i>Select at least one D2/Virtualized Services"
                                                        + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }
                if (CLOSEDLOOP_POLICY.equalsIgnoreCase(policyData.getConfigPolicyType())) {
                    if (policyData.getJsonBody() != null) {

                        // For API we need to get the conditions key from the Json request and check it before
                        // deserializing to POJO due to the enum
                        if ("API".equals(policyData.getApiflag())) {
                            JSONObject json = new JSONObject(policyData.getJsonBody());
                            if (!json.isNull("conditions")) {
                                String apiCondition = (String) json.get("conditions");
                                if (Strings.isNullOrEmpty(apiCondition)) {
                                    responseString.append("<b>Conditions</b>: <i>Select At least one Condition"
                                                    + HTML_ITALICS_LNBREAK);
                                    return responseString;
                                }
                            } else {
                                responseString.append("<b>Conditions</b>:"
                                                + " <i>There were no conditions provided in configBody json"
                                                + HTML_ITALICS_LNBREAK);
                                return responseString;
                            }
                        } else {
                            if (policyData.getTrapDatas().getTrap1() != null) {
                                if (policyData.getClearTimeOut() == null) {
                                    responseString.append("<b>Trigger Clear TimeOut</b>: "
                                                    + "<i>Trigger Clear TimeOut is required when at "
                                                    + "least One Trigger Signature is enabled</i><br>");
                                    valid = false;
                                }
                                if (policyData.getTrapMaxAge() == null) {
                                    responseString.append("<b>Trap Max Age</b>: <i>Trap Max Age is required when at "
                                                    + "least One Trigger Signature is enabled</i><br>");
                                    valid = false;
                                }
                            }
                            if (policyData.getFaultDatas().getTrap1() != null
                                            && policyData.getVerificationclearTimeOut() == null) {
                                responseString.append(
                                                "<b>Fault Clear TimeOut</b>: <i>Fault Clear TimeOut is required when at"
                                                                + "least One Fault Signature is enabled</i><br>");
                                valid = false;
                            }
                        }

                        ClosedLoopFaultBody faultBody = mapper.readValue(policyData.getJsonBody(),
                                        ClosedLoopFaultBody.class);
                        if (faultBody.getEmailAddress() != null && !faultBody.getEmailAddress().isEmpty()) {
                            String result = emailValidation(faultBody.getEmailAddress(), responseString.toString());
                            if (!SUCCESS.equals(result)) {
                                responseString.append(result + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                        }
                        if (!(faultBody.isGamma() || faultBody.isMcr() || faultBody.isTrinity() || faultBody.isvDNS()
                                        || faultBody.isvUSP())) {
                            responseString.append(SELECT_AT_LEAST_ONE_D2_VIRTUALIZED_SERVICES + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getActions() == null || faultBody.getActions().isEmpty()) {
                            responseString.append(
                                            "<b>vPRO Actions</b>: <i>vPRO Actions is required" + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getClosedLoopPolicyStatus() == null
                                        || faultBody.getClosedLoopPolicyStatus().isEmpty()) {
                            responseString.append("<b>Policy Status</b>: <i>Policy Status is required"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getConditions() == null) {
                            responseString.append("<b>Conditions</b>: <i>Select At least one Condition"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getGeoLink() != null && !faultBody.getGeoLink().isEmpty()) {
                            String result = PolicyUtils.policySpecialCharWithSpaceValidator(faultBody.getGeoLink());
                            if (!result.contains(SUCCESS)) {
                                responseString.append("<b>GeoLink</b>:<i>" + result + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                        }
                        if (faultBody.getAgingWindow() == 0) {
                            responseString.append(
                                            "<b>Aging Window</b>: <i>Aging Window is required" + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getTimeInterval() == 0) {
                            responseString.append("<b>Time Interval</b>: <i>Time Interval is required"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getRetrys() == 0) {
                            responseString.append("<b>Number of Retries</b>: <i>Number of Retries is required"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getTimeOutvPRO() == 0) {
                            responseString.append("<b>APP-C Timeout</b>: <i>APP-C Timeout is required"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getTimeOutRuby() == 0) {
                            responseString.append(
                                            "<b>TimeOutRuby</b>: <i>TimeOutRuby is required" + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (faultBody.getVnfType() == null || faultBody.getVnfType().isEmpty()) {
                            responseString.append("<b>Vnf Type</b>: <i>Vnf Type is required" + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append(
                                        "<b>D2/Virtualized Services</b>: <i>Select at least one D2/Virtualized Services"
                                                        + HTML_ITALICS_LNBREAK);
                        responseString.append(
                                        "<b>vPRO Actions</b>: <i>vPRO Actions is required" + HTML_ITALICS_LNBREAK);
                        responseString.append(
                                        "<b>Aging Window</b>: <i>Aging Window is required" + HTML_ITALICS_LNBREAK);
                        responseString.append(
                                        "<b>Policy Status</b>: <i>Policy Status is required" + HTML_ITALICS_LNBREAK);
                        responseString.append(
                                        "<b>Conditions</b>: <i>Select Atleast one Condition" + HTML_ITALICS_LNBREAK);
                        responseString.append("<b>PEP Name</b>: <i>PEP Name is required" + HTML_ITALICS_LNBREAK);
                        responseString.append("<b>PEP Action</b>: <i>PEP Action is required" + HTML_ITALICS_LNBREAK);
                        responseString.append(
                                        "<b>Time Interval</b>: <i>Time Interval is required" + HTML_ITALICS_LNBREAK);
                        responseString.append("<b>Number of Retries</b>: <i>Number of Retries is required"
                                        + HTML_ITALICS_LNBREAK);
                        responseString.append(
                                        "<b>APP-C Timeout</b>: <i>APP-C Timeout is required" + HTML_ITALICS_LNBREAK);
                        responseString.append("<b>TimeOutRuby</b>: <i>TimeOutRuby is required" + HTML_ITALICS_LNBREAK);
                        responseString.append("<b>Vnf Type</b>: <i>Vnf Type is required" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }

                // Validate MicroService Policy Data
                if (MICROSERVICES.equals(policyData.getConfigPolicyType())) {
                    boolean tmpValid = validateMsModel(policyData, responseString);
                    if (!tmpValid) {
                        valid = false;
                    }
                }

                // Validate Optimization Policy Data
                if (OPTIMIZATION.equals(policyData.getConfigPolicyType())) {
                    boolean tmpValid = validateOptimization(policyData, responseString);
                    if (!tmpValid) {
                        valid = false;
                    }
                }
            }

            if ((DECISION_POLICY.equalsIgnoreCase(policyData.getPolicyType()))
                            || (DECISION_POLICY_MS.equalsIgnoreCase(policyData.getPolicyType()))) {
                if (!RAW.equalsIgnoreCase(policyData.getRuleProvider())) {
                    if (!Strings.isNullOrEmpty(policyData.getOnapName())) {
                        String onapNameValidate = PolicyUtils.policySpecialCharValidator(policyData.getOnapName());
                        if (!onapNameValidate.contains(SUCCESS)) {
                            responseString.append("OnapName:" + onapNameValidate + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("Onap Name: Onap Name Should not be empty" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }
                if (RAW.equalsIgnoreCase(policyData.getRuleProvider())) {
                    Object policy = XACMLPolicyScanner.readPolicy(new ByteArrayInputStream(StringEscapeUtils
                                    .unescapeXml(policyData.getRawXacmlPolicy()).getBytes(StandardCharsets.UTF_8)));
                    if (!(policy instanceof PolicySetType || policy instanceof PolicyType)) {
                        responseString.append("Raw XACML: The XACML Content is not valid" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }

                if (DECISION_MS_MODEL.equals(policyData.getRuleProvider())) {
                    LOGGER.info("Validating Decision MS Policy - ");
                    boolean tmpValid = validateMsModel(policyData, responseString);
                    if (!tmpValid) {
                        valid = false;
                    }
                }

                if ("Rainy_Day".equals(policyData.getRuleProvider())) {
                    if (policyData.getRainyday() == null) {
                        responseString.append("<b> Rainy Day Parameters are Required </b><br>");
                        valid = false;
                    } else {
                        if (Strings.isNullOrEmpty(policyData.getRainyday().getServiceType())) {
                            responseString.append("Rainy Day <b>Service Type</b> is Required<br>");
                            valid = false;
                        }
                        if (Strings.isNullOrEmpty(policyData.getRainyday().getVnfType())) {
                            responseString.append("Rainy Day <b>VNF Type</b> is Required<br>");
                            valid = false;
                        }
                        if (Strings.isNullOrEmpty(policyData.getRainyday().getBbid())) {
                            responseString.append("Rainy Day <b>Building Block ID</b> is Required<br>");
                            valid = false;
                        }
                        if (Strings.isNullOrEmpty(policyData.getRainyday().getWorkstep())) {
                            responseString.append("Rainy Day <b>Work Step</b> is Required<br>");
                            valid = false;
                        }
                        if (!policyData.getRainyday().getTreatmentTableChoices().isEmpty()
                                        && policyData.getRainyday().getTreatmentTableChoices() != null) {

                            for (Object treatmentMap : policyData.getRainyday().getTreatmentTableChoices()) {
                                String errorCode = null;
                                String treatment = null;
                                if (treatmentMap instanceof LinkedHashMap<?, ?>) {

                                    if (((LinkedHashMap<?, ?>) treatmentMap).containsKey("errorcode")) {
                                        errorCode = ((LinkedHashMap<?, ?>) treatmentMap).get("errorcode").toString();
                                    }
                                    if (((LinkedHashMap<?, ?>) treatmentMap).containsKey("treatment")) {
                                        treatment = ((LinkedHashMap<?, ?>) treatmentMap).get("treatment").toString();
                                    }

                                }
                                if (Strings.isNullOrEmpty(errorCode) && Strings.isNullOrEmpty(treatment)) {
                                    responseString.append("Rainy Day <b>Error Code</b> and "
                                                    + "<b>Desired Treatment</b> cannot be empty<br>");
                                    valid = false;
                                    break;
                                }
                                if (Strings.isNullOrEmpty(errorCode)) {
                                    responseString.append("Rainy Day <b>Error Code</b> is Required "
                                                    + "for each Desired Treatment<br>");
                                    valid = false;
                                    break;
                                }
                                if (Strings.isNullOrEmpty(treatment)) {
                                    responseString.append("Rainy Day <b>Desired Treatment"
                                                    + "</b> is Required for each Error Code<br>");
                                    valid = false;
                                    break;
                                }
                            }

                        } else {
                            responseString.append("Rainy Day <b>Desired Automated Treatments</b> are Required<br>");
                            valid = false;
                        }
                    }
                }

                if ("GUARD_YAML".equals(policyData.getRuleProvider())
                                || "GUARD_BL_YAML".equals(policyData.getRuleProvider())
                                || "GUARD_MIN_MAX".equals(policyData.getRuleProvider())) {
                    if (policyData.getYamlparams() == null) {
                        responseString.append("<b> Guard Params are Required </b>" + HTML_ITALICS_LNBREAK);
                        valid = false;
                    } else {
                        if (Strings.isNullOrEmpty(policyData.getYamlparams().getActor())) {
                            responseString.append("Guard Params <b>Actor</b> is Required " + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (Strings.isNullOrEmpty(policyData.getYamlparams().getRecipe())) {
                            responseString.append("Guard Params <b>Recipe</b> is Required " + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (Strings.isNullOrEmpty(policyData.getYamlparams().getGuardActiveStart())) {
                            responseString.append("Guard Params <b>Guard Active Start</b> is Required "
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if (Strings.isNullOrEmpty(policyData.getYamlparams().getGuardActiveEnd())) {
                            responseString.append(
                                            "Guard Params <b>Guard Active End</b> is Required " + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                        if ("GUARD_YAML".equals(policyData.getRuleProvider())) {
                            if (Strings.isNullOrEmpty(policyData.getYamlparams().getLimit())) {
                                responseString.append(" Guard Params <b>Limit</b> is Required " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            } else if (!PolicyUtils.isInteger(policyData.getYamlparams().getLimit())) {
                                responseString.append(
                                                " Guard Params <b>Limit</b> Should be Integer " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                            if (Strings.isNullOrEmpty(policyData.getYamlparams().getTimeWindow())) {
                                responseString.append(
                                                "Guard Params <b>Time Window</b> is Required" + HTML_ITALICS_LNBREAK);
                                valid = false;
                            } else if (!PolicyUtils.isInteger(policyData.getYamlparams().getTimeWindow())) {
                                responseString.append(" Guard Params <b>Time Window</b> Should be Integer "
                                                + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                            if (Strings.isNullOrEmpty(policyData.getYamlparams().getTimeUnits())) {
                                responseString.append(
                                                "Guard Params <b>Time Units</b> is Required" + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                        } else if ("GUARD_MIN_MAX".equals(policyData.getRuleProvider())) {
                            if (Strings.isNullOrEmpty(policyData.getYamlparams().getMin())) {
                                responseString.append(" Guard Params <b>Min</b> is Required " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            } else if (!PolicyUtils.isInteger(policyData.getYamlparams().getMin())) {
                                responseString.append(
                                                " Guard Params <b>Min</b> Should be Integer " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                            if (Strings.isNullOrEmpty(policyData.getYamlparams().getMax())) {
                                responseString.append(" Guard Params <b>Max</b> is Required " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            } else if (!PolicyUtils.isInteger(policyData.getYamlparams().getMax())) {
                                responseString.append(
                                                " Guard Params <b>Max</b> Should be Integer " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            }
                        } else if ("GUARD_BL_YAML".equals(policyData.getRuleProvider())
                                        && "Use Manual Entry".equals(policyData.getBlackListEntryType())) {

                            if (policyData.getYamlparams().getBlackList() == null
                                            || policyData.getYamlparams().getBlackList().isEmpty()) {
                                responseString.append(
                                                " Guard Params <b>BlackList</b> is Required " + HTML_ITALICS_LNBREAK);
                                valid = false;
                            } else {
                                for (String blackList : policyData.getYamlparams().getBlackList()) {
                                    if (blackList == null || !(SUCCESS
                                                    .equals(PolicyUtils.policySpecialCharValidator(blackList)))) {
                                        responseString.append(" Guard Params <b>BlackList</b> Should be valid String"
                                                        + HTML_ITALICS_LNBREAK);
                                        valid = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (ACTION_POLICY.equalsIgnoreCase(policyData.getPolicyType())) {
                if (!Strings.isNullOrEmpty(policyData.getActionPerformer())) {
                    String actionPerformer = PolicyUtils.policySpecialCharValidator(policyData.getActionPerformer());
                    if (!actionPerformer.contains(SUCCESS)) {
                        responseString.append("<b>ActionPerformer</b>:<i>" + actionPerformer + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                } else {
                    responseString.append("<b>ActionPerformer</b>:<i> ActionPerformer Should not be empty"
                                    + HTML_ITALICS_LNBREAK);
                    valid = false;
                }

                if (!Strings.isNullOrEmpty(policyData.getActionAttributeValue())) {
                    String actionAttribute = PolicyUtils
                                    .policySpecialCharValidator(policyData.getActionAttributeValue());
                    if (!actionAttribute.contains(SUCCESS)) {
                        responseString.append("<b>ActionAttribute</b>:<i>" + actionAttribute + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                } else {
                    responseString.append("<b>ActionAttribute</b>:<i> ActionAttribute Should not be empty"
                                    + HTML_ITALICS_LNBREAK);
                    valid = false;
                }
            }

            if (CONFIG_POLICY.equals(policyData.getPolicyType())) {
                String value = "";
                if (valid) {
                    if (commonClassDao != null) {
                        List<Object> spData = commonClassDao.getDataById(SafePolicyWarning.class, "riskType",
                                        policyData.getRiskType());
                        if (!spData.isEmpty()) {
                            SafePolicyWarning safePolicyWarningData = (SafePolicyWarning) spData.get(0);
                            value = "<b>Message</b>:<i>" + safePolicyWarningData.getMessage() + "</i>";
                        }
                    }
                    responseString.append(SUCCESS + "@#" + value);
                }
            } else {
                if (valid) {
                    responseString.append(SUCCESS);
                }
            }

            return responseString;
        } catch (Exception e) {
            LOGGER.error("Exception Occured during Policy Validation" + e);
            return null;
        }
    }

    private String emailValidation(String email, String response) {
        String res = response;
        if (email != null) {
            String validateEmail = PolicyUtils.validateEmailAddress(email.replace("\"", ""));
            if (!validateEmail.contains(SUCCESS)) {
                res += "<b>Email</b>:<i>" + validateEmail + HTML_ITALICS_LNBREAK;
            } else {
                return SUCCESS;
            }
        }
        return res;
    }

    private MicroServiceModels getAttributeObject(String name, String version) {
        MicroServiceModels workingModel = null;
        try {
            List<Object> microServiceModelsData = commonClassDao.getDataById(MicroServiceModels.class,
                            "modelName:version", name + ":" + version);
            if (microServiceModelsData != null) {
                workingModel = (MicroServiceModels) microServiceModelsData.get(0);
            }
        } catch (Exception e) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Template.  The template name, " + name
                            + " was not found in the dictionary: ";
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message + e);
            return null;
        }

        return workingModel;
    }

    private OptimizationModels getOptimizationModelData(String name, String version) {
        OptimizationModels workingModel = null;
        try {
            List<Object> optimizationModelsData = commonClassDao.getDataById(OptimizationModels.class,
                            "modelName:version", name + ":" + version);
            if (optimizationModelsData != null) {
                workingModel = (OptimizationModels) optimizationModelsData.get(0);
            }
        } catch (Exception e) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Template.  The template name, " + name
                            + " was not found in the dictionary: ";
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message + e);
            return null;
        }

        return workingModel;
    }

    private void pullJsonKeyPairs(JsonNode rootNode) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            final String key = field.getKey();
            final JsonNode value = field.getValue();
            if (value.isContainerNode() && !value.isArray()) {
                pullJsonKeyPairs(value); // RECURSIVE CALL
            } else {
                if (value.isArray()) {
                    String newValue = StringUtils.replaceEach(value.toString(), new String[]
                        { "[", "]", "\"" }, new String[]
                        { "", "", "" });
                    mapAttribute.put(key, newValue);
                } else {
                    mapAttribute.put(key, value.toString().trim());
                }
            }
        }
    }

    private void pullModelJsonKeyPairs(JsonNode rootNode) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();
            final String key = field.getKey();
            final JsonNode value = field.getValue();

            if (value.isContainerNode() && !value.isArray()) {
                jsonRequestMap.put(key.trim(), value.toString().trim());
                pullModelJsonKeyPairs(value); // RECURSIVE CALL
            } else if (value.isArray()) {
                try {
                    String valueStr = value.toString();
                    String stringValue = valueStr.substring(valueStr.indexOf('[') + 1, valueStr.lastIndexOf(']'));

                    if (stringValue.isEmpty()) {
                        stringValue = "{\"test\":\"test\"}";
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode newValue = mapper.readTree(stringValue);
                    jsonRequestMap.put(key.trim(), value.toString().trim());
                    pullModelJsonKeyPairs(newValue);
                } catch (IOException e) {
                    LOGGER.info("PolicyValidation: Exception occurred while mapping string to JsonNode " + e);
                }
            } else {
                jsonRequestMap.put(key.trim(), value.toString().trim());
            }
        }

    }

    private JsonObject stringToJsonObject(String value) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(value))) {
            return jsonReader.readObject();
        } catch (JsonException | IllegalStateException e) {
            LOGGER.info(XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Improper JSON format... may or may not cause issues in validating the policy: " + value,
                            e);
            return null;
        }
    }

    private void findRequiredFields(JsonObject json) {
        if (json == null) {
            return;
        }
        for (Entry<String, JsonValue> keyMap : json.entrySet()) {
            Object obj = keyMap.getValue();
            String key = keyMap.getKey();
            if (obj instanceof JsonObject) {
                if (allReqTrueKeys.contains(key)) {
                    JsonObject jsonObj = (JsonObject) obj;
                    // only check fields in obj if obj itself is required.
                    for (Entry<String, JsonValue> jsonMap : jsonObj.entrySet()) {
                        if (jsonMap.getValue().toString().contains(REQUIRED_ATTRIBUTE)) {
                            modelRequiredFieldsList.add(jsonMap.getKey().trim());
                        }
                    }
                }
            } else if (keyMap.getValue().toString().contains(REQUIRED_ATTRIBUTE)) {
                modelRequiredFieldsList.add(key.trim());

            }
        }

    }

    // call this method to start the recursive
    private Set<String> getAllKeys(JSONObject json) {
        return getAllKeys(json, new HashSet<>());
    }

    private Set<String> getAllKeys(JSONArray arr) {
        return getAllKeys(arr, new HashSet<>());
    }

    private Set<String> getAllKeys(JSONArray arr, Set<String> keys) {
        for (int i = 0; i < arr.length(); i++) {
            Object obj = arr.get(i);
            if (obj instanceof JSONObject) {
                keys.addAll(getAllKeys(arr.getJSONObject(i)));
            }
            if (obj instanceof JSONArray) {
                keys.addAll(getAllKeys(arr.getJSONArray(i)));
            }
        }

        return keys;
    }

    // this method returns a set of keys with "required-true" defined in their value.
    private Set<String> getAllKeys(JSONObject json, Set<String> keys) {
        for (String key : json.keySet()) {
            Object obj = json.get(key);
            if (obj instanceof String && ((String) obj).contains(REQUIRED_ATTRIBUTE)) {
                LOGGER.debug("key : " + key);
                LOGGER.debug("obj : " + obj);
                allReqTrueKeys.add(key); // For MicroService policies
                allOptReqTrueKeys.add(key); // For Optimization policies

                // get the type from value and add that one also
                String type = StringUtils.substringBefore((String) obj, ":");
                if (!StringUtils.isBlank(type) && !StringUtils.containsAny(type.toLowerCase(), STRING, INTEGER, LIST,
                                MAP, JAVA, BOOLEAN)) {
                    allReqTrueKeys.add(type);
                }
            }
            if (obj instanceof JSONObject) {
                keys.addAll(getAllKeys(json.getJSONObject(key)));
            }
            if (obj instanceof JSONArray) {
                keys.addAll(getAllKeys(json.getJSONArray(key)));
            }
        }

        return keys;
    }

    private boolean validateMsModel(PolicyRestAdapter policyData, StringBuilder responseString)
                    throws JsonProcessingException {
        boolean valid = true;
        if (!Strings.isNullOrEmpty(policyData.getServiceType())) {

            modelRequiredFieldsList.clear();
            pullJsonKeyPairs((JsonNode) policyData.getPolicyJSON());

            String service;
            String version;
            if (policyData.getServiceType().contains("-v")) {
                service = policyData.getServiceType().split("-v")[0];
                version = policyData.getServiceType().split("-v")[1];
            } else {
                service = policyData.getServiceType();
                version = policyData.getVersion();
            }

            if (!Strings.isNullOrEmpty(version)) {
                MicroServiceModels returnModel = getAttributeObject(service, version);

                if (returnModel != null) {

                    String annotation = returnModel.getAnnotation();
                    String refAttributes = returnModel.getRefAttributes();
                    String subAttributes = returnModel.getSubAttributes();
                    String modelAttributes = returnModel.getAttributes();

                    if (!Strings.isNullOrEmpty(annotation)) {
                        Map<String, String> rangeMap = Splitter.on(",").withKeyValueSeparator("=").split(annotation);
                        for (Entry<String, String> raMap : rangeMap.entrySet()) {
                            if (raMap.getValue().contains("range::")) {
                                String value = mapAttribute.get(raMap.getKey().trim());
                                String[] tempString = raMap.getValue().split("::")[1].split("-");
                                int startNum = Integer.parseInt(tempString[0]);
                                int endNum = Integer.parseInt(tempString[1]);
                                String returnString = "InvalidreturnModel Range:" + raMap.getKey() + " must be between "
                                                + startNum + " - " + endNum + ",";

                                if (value != null) {
                                    if (PolicyUtils.isInteger(value.replace("\"", ""))) {
                                        int result = Integer.parseInt(value.replace("\"", ""));
                                        if (result < startNum || result > endNum) {
                                            responseString.append(returnString);
                                            valid = false;
                                        }
                                    } else {
                                        responseString.append(returnString);
                                        valid = false;
                                    }
                                } else {
                                    responseString.append("<b>" + raMap.getKey() + "</b>:<i>" + raMap.getKey()
                                                    + " is required for the MicroService model " + service
                                                    + HTML_ITALICS_LNBREAK);
                                    valid = false;
                                }

                            }
                        }
                    } else if (!DECISION_MS_MODEL.equals(policyData.getRuleProvider())) {
                        // Validate for configName, location, uuid, and policyScope if no annotations exist for this
                        // model
                        if (Strings.isNullOrEmpty(policyData.getLocation())) {
                            responseString.append("<b>Micro Service Model</b>:<i> location is required for this model"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }

                        if (Strings.isNullOrEmpty(policyData.getConfigName())) {
                            responseString.append("<b>Micro Service Model</b>:<i> configName is required for this model"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }

                        if (Strings.isNullOrEmpty(policyData.getUuid())) {
                            responseString.append("<b>Micro Service Model</b>:<i> uuid is required for this model"
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }

                        if (Strings.isNullOrEmpty(policyData.getPolicyScope())) {
                            responseString.append(
                                            "<b>Micro Service Model</b>:<i> policyScope is required for this model"
                                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    }

                    // If request comes from the API we need to validate required fields in the Micro Service Model
                    // GUI request are already validated from the SDK-APP
                    if ("API".equals(policyData.getApiflag())) {
                        // first , get the complete set of required fields
                        populateReqFieldSet(new String[]
                            { refAttributes, modelAttributes }, subAttributes);

                        // ignore req fields in which parent is not reqd
                        populateRequiredFields(new String[]
                            { refAttributes, modelAttributes }, subAttributes, modelAttributes);

                        if (modelRequiredFieldsList != null && !modelRequiredFieldsList.isEmpty()) {
                            // create jsonRequestMap with all json keys and values from request
                            JsonNode rootNode = (JsonNode) policyData.getPolicyJSON();
                            jsonRequestMap.clear();
                            pullModelJsonKeyPairs(rootNode);

                            // validate if the requiredFields are in the request
                            for (String requiredField : modelRequiredFieldsList) {
                                if (jsonRequestMap.containsKey(requiredField)) {
                                    String value = jsonRequestMap.get(requiredField);
                                    if (StringUtils.isBlank(value) || "\"\"".equals(value)) {
                                        responseString.append("<b>Micro Service Model</b>:<i> " + requiredField
                                                        + ISREQUIRED + HTML_ITALICS_LNBREAK);
                                        valid = false;
                                    }
                                } else {
                                    responseString.append("<b>Micro Service Model</b>:<i> " + requiredField + ISREQUIRED
                                                    + HTML_ITALICS_LNBREAK);
                                    valid = false;
                                }
                            }
                        }
                    }
                } else {
                    responseString.append("<b>Micro Service Model</b>:<i> Invalid Model. The model name, " + service
                                    + " of version, " + version + " was not found in the dictionary"
                                    + HTML_ITALICS_LNBREAK);
                    valid = false;
                }
            } else {
                responseString.append("<b>Micro Service Version</b>:<i> Micro Service Version is required"
                                + HTML_ITALICS_LNBREAK);
                valid = false;
            }
        } else {
            responseString.append("<b>Micro Service</b>:<i> Micro Service Model is required" + HTML_ITALICS_LNBREAK);
            valid = false;
        }

        if (Strings.isNullOrEmpty(policyData.getPriority())
                        && !DECISION_MS_MODEL.equals(policyData.getRuleProvider())) {
            responseString.append("<b>Priority</b>:<i> Priority is required" + HTML_ITALICS_LNBREAK);
        }

        return valid;
    }

    private boolean validateOptimization(PolicyRestAdapter policyData, StringBuilder responseString) {
        boolean valid = true;

        // Checks for required policy data in request
        if (Strings.isNullOrEmpty(policyData.getServiceType())) {
            responseString.append("<b>Optimization Service</b>:<i> Optimization policy data is missing or invalid Json."
                            + HTML_ITALICS_LNBREAK);
            return false;
        }

        modelRequiredFieldsList.clear();
        pullJsonKeyPairs((JsonNode) policyData.getPolicyJSON());

        // parse the service and version from the policy data
        String service;
        String version;
        if (policyData.getServiceType().contains("-v")) {
            service = policyData.getServiceType().split("-v")[0];
            version = policyData.getServiceType().split("-v")[1];
        } else {
            service = policyData.getServiceType();
            version = policyData.getVersion();
        }

        // Checks for required version in policy data
        if (Strings.isNullOrEmpty(version)) {
            responseString.append("<b>Optimization Service Version</b>:<i> Optimization Service Version is required"
                            + HTML_ITALICS_LNBREAK);
            return false;
        }

        OptimizationModels returnModel = getOptimizationModelData(service, version);

        // Checks if valid model exists in the database
        if (returnModel == null) {
            responseString.append("<b>Optimization Service Model</b>:<i> Invalid Model. The model name, " + service
                            + " of version, " + version + " was not found in the dictionary" + HTML_ITALICS_LNBREAK);
            return false;
        }

        String annotation = returnModel.getAnnotation();
        String refAttributes = returnModel.getRefattributes();
        String subAttributes = returnModel.getSubattributes();
        String modelAttributes = returnModel.getAttributes();

        if (!Strings.isNullOrEmpty(annotation)) {
            Map<String, String> rangeMap = Splitter.on(",").withKeyValueSeparator("=").split(annotation);
            for (Entry<String, String> rangeMapEntry : rangeMap.entrySet()) {
                if (rangeMapEntry.getValue().contains("range::")) {
                    String value = mapAttribute.get(rangeMapEntry.getKey().trim());
                    String[] tempString = rangeMapEntry.getValue().split("::")[1].split("-");
                    int startNum = Integer.parseInt(tempString[0]);
                    int endNum = Integer.parseInt(tempString[1]);
                    String returnString = "InvalidreturnModel Range:" + rangeMapEntry.getKey() + " must be between "
                                    + startNum + " - " + endNum + ",";

                    if (value != null) {
                        if (PolicyUtils.isInteger(value.replace("\"", ""))) {
                            int result = Integer.parseInt(value.replace("\"", ""));
                            if (result < startNum || result > endNum) {
                                responseString.append(returnString);
                                valid = false;
                            }
                        } else {
                            responseString.append(returnString);
                            valid = false;
                        }
                    } else {
                        responseString.append("<b>" + rangeMapEntry.getKey() + "</b>:<i>" + rangeMapEntry.getKey()
                                        + " is required for the Optimization model " + service + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }

                }
            }
        }

        // If request comes from the API we need to validate required fields in the Micro Service Modelvalid
        // GUI request are already validated from the SDK-APP
        if ("API".equals(policyData.getApiflag())) {
            // first , get the complete set of required fields
            populateReqFieldSet(new String[]
                { refAttributes, modelAttributes }, subAttributes);

            modelRequiredFieldsList.addAll(allOptReqTrueKeys);

            if (modelRequiredFieldsList != null && !modelRequiredFieldsList.isEmpty()) {

                // create jsonRequestMap with all json keys and values from request
                JsonNode rootNode = (JsonNode) policyData.getPolicyJSON();
                jsonRequestMap.clear();
                pullModelJsonKeyPairs(rootNode);

                // validate if the requiredFields are in the request
                for (String requiredField : modelRequiredFieldsList) {
                    if (jsonRequestMap.containsKey(requiredField)) {
                        String value = jsonRequestMap.get(requiredField);
                        if (StringUtils.isBlank(value) || "\"\"".equals(value)) {
                            responseString.append("<b>Optimization Service Model</b>:<i> " + requiredField + ISREQUIRED
                                            + HTML_ITALICS_LNBREAK);
                            valid = false;
                        }
                    } else {
                        responseString.append("<b>Optimization Service Model</b>:<i> " + requiredField + ISREQUIRED
                                        + HTML_ITALICS_LNBREAK);
                        valid = false;
                    }
                }
            }
        }

        if (Strings.isNullOrEmpty(policyData.getPriority())) {
            responseString.append("<b>Priority</b>:<i> Priority is required" + HTML_ITALICS_LNBREAK);
            valid = false;
        }

        return valid;
    }

    private void populateRequiredFields(String[] attrArr, String subAttributes, String modelAttributes)
                    throws JsonProcessingException {
        // get list of required fields from the ref_Attributes of the Model
        for (String attributes : attrArr) {
            if (!StringUtils.isBlank(attributes)) {
                Map<String, String> attributesMap = null;
                if (",".equals(attributes.substring(attributes.length() - 1))) {
                    String attributesString = attributes.substring(0, attributes.length() - 1);
                    attributesMap = Splitter.on(",").splitToList(attributesString).stream().map(kv -> kv.split("="))
                            .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1], (exist, d2) -> exist));
                } else if (!StringUtils.isBlank(modelAttributes)) {
                    attributesMap = Splitter.on(",").withKeyValueSeparator("=").split(modelAttributes);
                } else {
                    continue;
                }
                String json = new ObjectMapper().writeValueAsString(attributesMap);
                findRequiredFields(stringToJsonObject(json));
            }

        }

        // get list of required fields from the sub_Attributes of the Model
        if (!StringUtils.isBlank(subAttributes)) {
            JsonObject subAttributesJson = stringToJsonObject(subAttributes);
            findRequiredFields(subAttributesJson);
        }

    }

    private void populateReqFieldSet(String[] attrArr, String subAttributes) {
        allReqTrueKeys.clear();
        JSONObject jsonSub = new JSONObject(subAttributes);
        // Get all keys with "required-true" defined in their value from subAttribute
        getAllKeys(jsonSub);

        // parse refAttrbutes
        for (String attr : attrArr) {
            if (attr != null) {
                String[] referAarray = attr.split(",");
                String[] element = null;
                for (int i = 0; i < referAarray.length; i++) {
                    element = referAarray[i].split("=");
                    if (element.length > 1 && element[1].contains(REQUIRED_ATTRIBUTE)) {
                        allReqTrueKeys.add(element[0]);
                    }
                }
            }
        }
    }

}
