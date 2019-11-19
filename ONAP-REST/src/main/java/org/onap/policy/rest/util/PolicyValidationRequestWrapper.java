/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultTrapDatas;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.adapter.RainyDayParams;
import org.onap.policy.rest.adapter.YAMLParams;
import org.onap.policy.xacml.api.XACMLErrorConstants;

public class PolicyValidationRequestWrapper {
    private static final Logger LOGGER = FlexLogger.getLogger(PolicyValidationRequestWrapper.class);

    private static final String CONFIG_NAME = "configName";
    private static final String CONTENT = "content";
    private static final String GUARD = "guard";
    private static final String INVALIDJSON = " improper JSON format: ";
    private static final String LOCATION = "location";
    private static final String ONAPNAME = "onapname";
    private static final String POLICYSCOPE = "policyScope";
    private static final String PRIORITY = "priority";
    private static final String RISKLEVEL = "riskLevel";
    private static final String RISKTYPE = "riskType";
    private static final String SECURITY_ZONE_ID = "securityZoneId";
    private static final String SERVICE = "service";
    private static final String SERVICETYPE_POLICY_NAME = "serviceTypePolicyName";
    private static final String UUID = "uuid";
    private static final String VERSION = "version";

    /**
     * Populate request parameters.
     *
     * @param request the request
     * @return the policy rest adapter
     */
    public PolicyRestAdapter populateRequestParameters(HttpServletRequest request) {
        PolicyRestAdapter policyData = null;
        ClosedLoopFaultTrapDatas trapDatas = null;
        ClosedLoopFaultTrapDatas faultDatas = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            policyData = mapper.readValue(root.get("policyData").toString(), PolicyRestAdapter.class);
            if (root.get("trapData") != null) {
                trapDatas = mapper.readValue(root.get("trapData").toString(), ClosedLoopFaultTrapDatas.class);
                policyData.setTrapDatas(trapDatas);
            }
            if (root.get("faultData") != null) {
                faultDatas = mapper.readValue(root.get("faultData").toString(), ClosedLoopFaultTrapDatas.class);
                policyData.setFaultDatas(faultDatas);
            }

            JsonObject json = stringToJsonObject(root.toString());
            if (json.containsKey("policyJSON")) {
                policyData.setPolicyJSON(root.get("policyJSON"));
            } else {
                String jsonBodyData = json.getJsonObject("policyData").get("jsonBodyData").toString();
                policyData.setJsonBody(jsonBodyData);
            }
        } catch (Exception e) {
            LOGGER.error("Exception Occured while populating request parameters: " + e);
        }

        return policyData;
    }

    /**
     * Populate request parameters.
     *
     * @param parameters the parameters
     * @return the policy rest adapter
     */
    public PolicyRestAdapter populateRequestParameters(PolicyParameters parameters) {
        PolicyRestAdapter policyData = new PolicyRestAdapter();

        /*
         * set policy adapter values for Building JSON object containing policy data
         */
        // Common Policy Fields
        policyData.setPolicyName(parameters.getPolicyName());
        policyData.setOnapName(parameters.getOnapName());
        policyData.setPriority(parameters.getPriority()); // Micro Service
        policyData.setConfigName(parameters.getConfigName()); // Base and Firewall
        policyData.setRiskType(parameters.getRiskType()); // Safe parameters Attributes
        policyData.setRiskLevel(parameters.getRiskLevel());// Safe parameters Attributes
        policyData.setGuard(String.valueOf(parameters.getGuard()));// Safe parameters Attributes
        policyData.setTtlDate(convertDate(parameters.getTtlDate()));// Safe parameters Attributes
        policyData.setApiflag("API");

        // Some policies require jsonObject conversion from String for configBody (i.e. MicroService and Firewall)
        JsonObject json = null;
        try {
            if (parameters.getConfigBody() != null) {
                json = stringToJsonObject(parameters.getConfigBody());
            }
        } catch (JsonException | IllegalStateException e) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + INVALIDJSON + parameters.getConfigBody();
            LOGGER.error(message, e);
            return null;
        }

        if (parameters.getPolicyClass() == null) {
            parameters.setPolicyClass(PolicyClass.Config);
        }

        policyData.setPolicyType(parameters.getPolicyClass().toString());

        switch (parameters.getPolicyClass()) {
            case Config:
                return populateConfigParameters(parameters, policyData, json);

            case Decision:
                return populateDecisionParameters(parameters, policyData, getMatchingAttributeValues(parameters));

            case Action:
                return populateActionParameters(parameters, policyData, getMatchingAttributeValues(parameters));

            default:
                return null;
        }
    }

    private PolicyRestAdapter populateConfigParameters(PolicyParameters parameters, PolicyRestAdapter policyData,
                    JsonObject json) {
        policyData.setConfigPolicyType(parameters.getPolicyConfigType().toString());

        // Config Specific
        policyData.setConfigBodyData(parameters.getConfigBody()); // Base
        policyData.setConfigType((parameters.getConfigBodyType() != null)
                        ? parameters.getConfigBodyType().toString().toUpperCase()
                        : null); // Base

        switch (parameters.getPolicyConfigType()) {
            case Firewall:
                return populateConfigFirewallParameters(policyData, json);
            case MicroService:
                return populateConfigMicroserviceParameters(parameters, policyData, json);
            case Optimization:
                return populateConfigOptimizationParameters(parameters, policyData, json);
            case ClosedLoop_Fault:
                return populateConfigClosedLoopFaultParameters(policyData, json);
            case ClosedLoop_PM:
                return populateConfigClosedLoopPmParameters(policyData, json);
            case BRMS_PARAM:
                return populateConfigBrmsParameters(parameters, policyData);

            // case BRMS_RAW:, case Base:, and case Extended: handled as default cases
            default:
                return policyData;
        }
    }

    private PolicyRestAdapter populateConfigFirewallParameters(PolicyRestAdapter policyData, JsonObject json) {
        policyData.setConfigPolicyType("Firewall Config");

        // get values and attributes from the JsonObject
        if (json != null) {
            policyData.setSecurityZone(getNewOrExistingKeyValue(json, SECURITY_ZONE_ID, policyData.getSecurityZone()));
            policyData.setConfigName(getNewOrExistingKeyValue(json, CONFIG_NAME, policyData.getConfigName()));
        }
        return policyData;
    }

    private PolicyRestAdapter populateConfigMicroserviceParameters(PolicyParameters parameters,
                    PolicyRestAdapter policyData, JsonObject json) {
        policyData.setConfigPolicyType("Micro Service");

        // Get values and attributes from the JsonObject
        if (json != null) {
            return getJsonObjectValuesAndAttributes(parameters, policyData, json);
        } else {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + INVALIDJSON + parameters.getConfigBody();
            LOGGER.error(message);
            return null;
        }
    }

    private PolicyRestAdapter populateConfigOptimizationParameters(PolicyParameters parameters,
                    PolicyRestAdapter policyData, JsonObject json) {
        policyData.setConfigPolicyType("Optimization");

        // get values and attributes from the JsonObject
        if (json != null) {
            return getJsonObjectValuesAndAttributes(parameters, policyData, json);
        } else {
            return policyData;
        }
    }

    private PolicyRestAdapter populateConfigClosedLoopFaultParameters(PolicyRestAdapter policyData, JsonObject json) {
        policyData.setConfigPolicyType("ClosedLoop_Fault");

        if (json != null) {
            policyData.setJsonBody(json.toString());
            policyData.setOnapName(getNewOrExistingKeyValue(json, ONAPNAME, policyData.getOnapName()));
        }
        return policyData;
    }

    private PolicyRestAdapter populateConfigClosedLoopPmParameters(PolicyRestAdapter policyData, JsonObject json) {
        policyData.setConfigPolicyType("ClosedLoop_PM");

        if (json != null) {
            policyData.setJsonBody(json.toString());
            policyData.setOnapName(getNewOrExistingKeyValue(json, ONAPNAME, policyData.getOnapName()));
            if (json.get(SERVICETYPE_POLICY_NAME) != null) {
                String serviceType = json.get(SERVICETYPE_POLICY_NAME).toString().replace("\"", "");
                LinkedHashMap<String, String> serviceTypePolicyName = new LinkedHashMap<>();
                serviceTypePolicyName.put(SERVICETYPE_POLICY_NAME, serviceType);
                policyData.setServiceTypePolicyName(serviceTypePolicyName);
            }
        }
        return policyData;
    }

    private PolicyRestAdapter populateConfigBrmsParameters(PolicyParameters parameters, PolicyRestAdapter policyData) {
        Map<AttributeType, Map<String, String>> drlRuleAndUiParams = parameters.getAttributes();
        Map<String, String> rule = drlRuleAndUiParams.get(AttributeType.RULE);
        policyData.setRuleName(rule.get("templateName"));

        return policyData;
    }

    private PolicyRestAdapter populateDecisionParameters(PolicyParameters parameters, PolicyRestAdapter policyData,
                    Map<String, String> matching) {
        policyData.setRuleProvider(parameters.getRuleProvider().toString());

        switch (parameters.getRuleProvider()) {
            case RAINY_DAY:
                return populateDecisionRainyDayParameters(parameters, policyData, matching);

            case GUARD_BL_YAML:
            case GUARD_MIN_MAX:
            case GUARD_YAML:
                return populateDecisionGuardParameters(policyData, matching);

            case AAF:
            case CUSTOM:
            case RAW:
            default:
                return policyData;
        }
    }

    private PolicyRestAdapter populateDecisionRainyDayParameters(PolicyParameters parameters,
                    PolicyRestAdapter policyData, Map<String, String> matching) {
        // Set Matching attributes in RainyDayParams in adapter
        RainyDayParams rainyday = new RainyDayParams();

        if (matching != null) {
            rainyday.setServiceType(matching.get("ServiceType"));
            rainyday.setVnfType(matching.get("VNFType"));
            rainyday.setBbid(matching.get("BB_ID"));
            rainyday.setWorkstep(matching.get("WorkStep"));
        }

        Map<String, String> treatments = parameters.getTreatments();
        ArrayList<Object> treatmentsTableChoices = new ArrayList<>();

        for (String keyField : treatments.keySet()) {
            LinkedHashMap<String, String> treatmentMap = new LinkedHashMap<>();
            String errorcode = keyField;
            String treatment = treatments.get(errorcode);
            treatmentMap.put("errorcode", errorcode);
            treatmentMap.put("treatment", treatment);
            treatmentsTableChoices.add(treatmentMap);
        }
        rainyday.setTreatmentTableChoices(treatmentsTableChoices);
        policyData.setRainyday(rainyday);

        return policyData;
    }

    private PolicyRestAdapter populateDecisionGuardParameters(PolicyRestAdapter policyData,
                    Map<String, String> matching) {
        // Set Matching attributes in YAMLParams in adapter
        YAMLParams yamlparams = new YAMLParams();

        if (matching == null) {
            policyData.setYamlparams(yamlparams);
            return policyData;
        }

        yamlparams.setActor(matching.get("actor"));
        yamlparams.setRecipe(matching.get("recipe"));
        yamlparams.setGuardActiveStart(matching.get("guardActiveStart"));
        yamlparams.setGuardActiveEnd(matching.get("guardActiveEnd"));

        yamlparams.setLimit(matching.get("limit"));
        yamlparams.setTimeWindow(matching.get("timeWindow"));
        yamlparams.setTimeUnits(matching.get("timeUnits"));

        yamlparams.setMin(matching.get("min"));
        yamlparams.setMax(matching.get("max"));

        List<String> blackList = new ArrayList<>();

        if (!Strings.isNullOrEmpty(matching.get("blackList"))) {
            String[] blackListArray = matching.get("blackList").split(",");
            for (String element : blackListArray) {
                blackList.add(element);
            }
        }

        yamlparams.setBlackList(blackList);

        policyData.setYamlparams(yamlparams);
        return policyData;
    }

    private PolicyRestAdapter populateActionParameters(PolicyParameters parameters, PolicyRestAdapter policyData,
                    Map<String, String> matching) {
        ArrayList<Object> ruleAlgorithmChoices = new ArrayList<>();

        List<String> dynamicLabelRuleAlgorithms = parameters.getDynamicRuleAlgorithmLabels();
        List<String> dynamicFieldFunctionRuleAlgorithms = parameters.getDynamicRuleAlgorithmFunctions();
        List<String> dynamicFieldOneRuleAlgorithms = parameters.getDynamicRuleAlgorithmField1();
        List<String> dyrnamicFieldTwoRuleAlgorithms = parameters.getDynamicRuleAlgorithmField2();

        if (dynamicLabelRuleAlgorithms != null && !dynamicLabelRuleAlgorithms.isEmpty()) {

            for (int i = dynamicLabelRuleAlgorithms.size() - 1; i >= 0; i--) {
                LinkedHashMap<String, String> ruleAlgorithm = new LinkedHashMap<>();

                String id = dynamicLabelRuleAlgorithms.get(i);
                String dynamicRuleAlgorithmField1 = dynamicFieldOneRuleAlgorithms.get(i);
                String dynamicRuleAlgorithmCombo = dynamicFieldFunctionRuleAlgorithms.get(i);
                String dynamicRuleAlgorithmField2 = dyrnamicFieldTwoRuleAlgorithms.get(i);

                ruleAlgorithm.put("id", id);
                ruleAlgorithm.put("dynamicRuleAlgorithmField1", dynamicRuleAlgorithmField1);
                ruleAlgorithm.put("dynamicRuleAlgorithmCombo", dynamicRuleAlgorithmCombo);
                ruleAlgorithm.put("dynamicRuleAlgorithmField2", dynamicRuleAlgorithmField2);

                ruleAlgorithmChoices.add(ruleAlgorithm);
            }
        }

        policyData.setRuleAlgorithmschoices(ruleAlgorithmChoices);

        ArrayList<Object> attributeList = new ArrayList<>();
        if (matching != null) {
            for (Map.Entry<String, String> entry : matching.entrySet()) {
                LinkedHashMap<String, String> attributeMap = new LinkedHashMap<>();
                String key = entry.getKey();
                String value = entry.getValue();
                attributeMap.put("key", key);
                attributeMap.put("value", value);
                attributeList.add(attributeMap);
            }
        }

        policyData.setAttributes(attributeList);
        policyData.setActionAttributeValue(parameters.getActionAttribute());
        policyData.setActionPerformer(parameters.getActionPerformer());

        return policyData;
    }

    private PolicyRestAdapter getJsonObjectValuesAndAttributes(PolicyParameters parameters,
                    PolicyRestAdapter policyData, JsonObject json) {
        if (json.containsKey(CONTENT)) {
            String content = json.get(CONTENT).toString();
            JsonNode policyJson = null;
            try {
                policyJson = new ObjectMapper().readTree(content);
            } catch (IOException e) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + INVALIDJSON + parameters.getConfigBody();
                LOGGER.error(message, e);
                return null;
            }
            policyData.setPolicyJSON(policyJson);
        }

        // @formatter:off
        policyData.setServiceType(getNewOrExistingKeyValue(json, SERVICE,     policyData.getServiceType()));
        policyData.setUuid(getNewOrExistingKeyValue(       json, UUID,        policyData.getUuid()));
        policyData.setLocation(getNewOrExistingKeyValue(   json, LOCATION,    policyData.getLocation()));
        policyData.setConfigName(getNewOrExistingKeyValue( json, CONFIG_NAME, policyData.getConfigName()));
        policyData.setPriority(getNewOrExistingKeyValue(   json, PRIORITY,    policyData.getPriority()));
        policyData.setVersion(getNewOrExistingKeyValue(    json, VERSION,     policyData.getVersion()));
        policyData.setPolicyScope(getNewOrExistingKeyValue(json, POLICYSCOPE, policyData.getPolicyScope()));
        policyData.setRiskType(getNewOrExistingKeyValue(   json, RISKTYPE,    policyData.getRiskType()));
        policyData.setRiskLevel(getNewOrExistingKeyValue(  json, RISKLEVEL,   policyData.getRiskLevel()));
        policyData.setGuard(getNewOrExistingKeyValue(      json, GUARD,       policyData.getGuard()));
        // @formatter:on

        return policyData;
    }

    private String getNewOrExistingKeyValue(final JsonObject json, final String key, final String existingValue) {
        if (json.containsKey(key)) {
            return json.get(key).toString().replace("\"", "");
        } else {
            return existingValue;
        }
    }

    private Map<String, String> getMatchingAttributeValues(PolicyParameters parameters) {
        // Get Matching attribute values
        Map<AttributeType, Map<String, String>> attributes = parameters.getAttributes();
        Map<String, String> matching = null;
        if (attributes != null) {
            matching = attributes.get(AttributeType.MATCHING);
        }
        return matching;
    }

    private JsonObject stringToJsonObject(String value) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(value))) {
            return jsonReader.readObject();
        } catch (JsonException | IllegalStateException jsonHandlingException) {
            LOGGER.info(XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Improper JSON format... may or may not cause issues in validating the policy: " + value,
                            jsonHandlingException);
            throw jsonHandlingException;
        }
    }

    private String convertDate(Date date) {
        String strDate = null;
        if (date != null) {
            SimpleDateFormat dateformatJava = new SimpleDateFormat("dd-MM-yyyy");
            strDate = dateformatJava.format(date);
        }
        return (strDate == null) ? "NA" : strDate;
    }

}
