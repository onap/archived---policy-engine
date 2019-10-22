/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
 * Modifications Copyright (C) 2019 Bell Canada
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

package org.onap.policy.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class CreateBRMSParamController extends RestrictedBaseController {

    private static final Logger policyLogger = FlexLogger.getLogger(CreateBRMSParamController.class);

    private static CommonClassDao commonClassDao;

    public static CommonClassDao getCommonClassDao() {
        return commonClassDao;
    }

    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        CreateBRMSParamController.commonClassDao = commonClassDao;
    }

    @Autowired
    private CreateBRMSParamController(CommonClassDao commonClassDao) {
        CreateBRMSParamController.commonClassDao = commonClassDao;
    }

    public CreateBRMSParamController() {
        // Empty constructor
    }

    protected PolicyRestAdapter policyAdapter = null;

    private HashMap<String, String> dynamicLayoutMap;

    private static String brmsTemplateVlaue = "<$%BRMSParamTemplate=";
    private static String string = "String";

    /**
     * getBRMSParamPolicyRuleData.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = {"/policyController/getBRMSTemplateData.htm"}, method = {RequestMethod.POST})
    public void getBRMSParamPolicyRuleData(HttpServletRequest request, HttpServletResponse response) {
        try {
            dynamicLayoutMap = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            String rule = findRule(root.get(PolicyController.getPolicydata()).toString().replaceAll("^\"|\"$", ""));
            generateUI(rule);
            response.setCharacterEncoding(PolicyController.getCharacterencoding());
            response.setContentType(PolicyController.getContenttype());
            request.setCharacterEncoding(PolicyController.getCharacterencoding());

            response.getWriter().write(new JSONObject("{policyData: " + mapper.writeValueAsString(dynamicLayoutMap)
                + "}").toString());
        } catch (Exception e) {
            policyLogger.error("Exception Occured while getting BRMS Rule data", e);
        }
    }

    private String findRule(String ruleTemplate) {
        List<Object> datas = commonClassDao.getDataById(BRMSParamTemplate.class, "ruleName", ruleTemplate);
        if (CollectionUtils.isNotEmpty(datas)) {
            return ((BRMSParamTemplate) datas.get(0)).getRule();
        }
        return null;
    }

    private void generateUI(String rule) {
        if (rule == null) {
            return;
        }
        try {
            processRule(rule);
        } catch (Exception e) {
            policyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
        }

    }

    private void processRule(String rule) {
        StringBuilder params = getParamsBuilderFromRule(rule);
        params = new StringBuilder(
                params.toString().replace("declare Params", "").replace("end", "").replaceAll("\\s+", ""));
        String[] components = params.toString().split(":");
        String caption = "";
        for (int i = 0; i < components.length; i++) {
            String type;
            if (i == 0) {
                caption = components[i];
            }
            if ("".equals(caption)) {
                break;
            }
            String nextComponent;
            try {
                nextComponent = components[i + 1];
            } catch (Exception e) {
                policyLogger.info("Error when procesing rule: " + e);
                nextComponent = components[i];
            }
            if (nextComponent.startsWith(string)) {
                type = "String";
                createField(caption, type);
                caption = nextComponent.replace(string, "");
            } else if (nextComponent.startsWith("int")) {
                type = "int";
                createField(caption, type);
                caption = nextComponent.replace("int", "");
            }
        }
    }

    private StringBuilder getParamsBuilderFromRule(final String rule) {
        StringBuilder params = new StringBuilder();
        boolean flag = false;
        boolean comment = false;
        String[] lines = rule.split("\n");
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            if (line.startsWith("/*")) {
                comment = true;
                continue;
            }
            if (line.contains("//")) {
                line = splitSingleLineComment(line);
            }
            if (line.contains("/*")) {
                comment = true;
                if (line.contains("*/")) {
                    comment = false;
                    line = processMultiLineFullComment(line);
                } else {
                    line = splitMultiLineStartComment(line);
                }
            }
            if (line.contains("*/")) {
                comment = false;
                line = processEndComment(line);
            }
            if (comment) {
                continue;
            }
            if (flag) {
                params.append(line);
            }
            if (line.contains("declare Params")) {
                params.append(line);
                flag = true;
            }
            if (line.contains("end") && flag) {
                break;
            }
        }
        return params;
    }

    private String splitMultiLineStartComment(String line) {
        return line.split("\\/\\*")[0];
    }

    private String splitMultiLineEndComment(String line) {
        return line.split("\\*\\/")[1].replace("*/", "");
    }

    private String splitSingleLineComment(String line) {
        return line.split("\\/\\/")[0];
    }

    private void createField(String caption, String type) {
        dynamicLayoutMap.put(caption, type);
    }

    /**
     * prePopulateBRMSParamPolicyData.
     * When the User Click Edit or View Policy the following method will get invoked for setting the data to
     * PolicyRestAdapter.
     * Which is used to bind the data in GUI.
     *
     * @param policyAdapter PolicyRestAdapter
     * @param entity PolicyEntity
     */
    public void prePopulateBRMSParamPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        dynamicLayoutMap = new HashMap<>();
        if (! (policyAdapter.getPolicyData() instanceof PolicyType)) {
            return;
        }
        PolicyType policy = (PolicyType) policyAdapter.getPolicyData();
        policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
        // policy name value is the policy name without any prefix and
        // Extensions.
        String policyNameValue =
                policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("BRMS_Param_") + 11);
        if (policyLogger.isDebugEnabled()) {
            policyLogger
                    .debug("Prepopulating form data for BRMS RAW Policy selected:" + policyAdapter.getPolicyName());
        }
        policyAdapter.setPolicyName(policyNameValue);
        String description;
        try {
            description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
        } catch (Exception e) {
            policyLogger.info("Error getting description: " + e);
            description = policy.getDescription();
        }
        policyAdapter.setPolicyDescription(description);
        setDataAdapterFromAdviceExpressions(policy, policyAdapter);

        // Generate Param UI
        try {
            paramUiGenerate(policyAdapter, entity);
        } catch (Exception e) {
            policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getMessage() + e);
        }

        // Get the target data under policy.
        policyAdapter.setDynamicLayoutMap(dynamicLayoutMap);
        if (policyAdapter.getDynamicLayoutMap().size() > 0) {
            LinkedHashMap<String, String> drlRule = policyAdapter.getDynamicLayoutMap().keySet().stream()
                    .collect(Collectors.toMap(String::toString,
                        keyValue -> policyAdapter.getDynamicLayoutMap().get(keyValue), (a, b) -> b,
                            LinkedHashMap::new));
            policyAdapter.setRuleData(drlRule);
        }
        TargetType target = policy.getTarget();
        if (target != null) {
            setDataToAdapterFromTarget(target, policyAdapter);
        }
    }

    private void setDataAdapterFromAdviceExpressions(PolicyType policy, PolicyRestAdapter policyAdapter) {
        ArrayList<Object> attributeList = new ArrayList<>();
        // Set Attributes.
        AdviceExpressionsType expressionTypes =
                ((RuleType) policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().get(0))
                        .getAdviceExpressions();
        for (AdviceExpressionType adviceExpression : expressionTypes.getAdviceExpression()) {
            for (AttributeAssignmentExpressionType attributeAssignment : adviceExpression
                    .getAttributeAssignmentExpression()) {
                if (attributeAssignment.getAttributeId().startsWith("key:")) {
                    Map<String, String> attribute = new HashMap<>();
                    String key = attributeAssignment.getAttributeId().replace("key:", "");
                    attribute.put("key", key);
                    @SuppressWarnings("unchecked")
                    JAXBElement<AttributeValueType> attributeValue =
                            (JAXBElement<AttributeValueType>) attributeAssignment.getExpression();
                    String value = (String) attributeValue.getValue().getContent().get(0);
                    attribute.put("value", value);
                    attributeList.add(attribute);
                } else if (attributeAssignment.getAttributeId().startsWith("dependencies:")) {
                    ArrayList<String> dependencies = new ArrayList<>(Arrays
                            .asList(attributeAssignment.getAttributeId().replace("dependencies:", "").split(",")));
                    dependencies.remove("");
                    policyAdapter.setBrmsDependency(dependencies);
                } else if (attributeAssignment.getAttributeId().startsWith("controller:")) {
                    policyAdapter.setBrmsController(attributeAssignment.getAttributeId().replace("controller:", ""));
                }
            }
            policyAdapter.setAttributes(attributeList);
        }
    }

    private void setDataToAdapterFromTarget(TargetType target, PolicyRestAdapter policyAdapter) {
        // Under target we have AnyOFType
        if (target.getAnyOf() == null) {
            return;
        }
        target.getAnyOf().stream().map(AnyOfType::getAllOf).filter(Objects::nonNull).flatMap(Collection::stream)
                .forEach(allOf -> setDataToAdapterFromMatchList(allOf.getMatch(), policyAdapter));
    }

    private void setDataToAdapterFromMatchList(List<MatchType> matchList, PolicyRestAdapter policyAdapter) {
        if (matchList == null) {
            return;
        }
        for (final MatchType match : matchList) {
            //
            // Under the match we have attribute value and
            // attributeDesignator. So,finally down to the actual attribute.
            //
            AttributeValueType attributeValue = match.getAttributeValue();
            String value = (String) attributeValue.getContent().get(0);
            AttributeDesignatorType designator = match.getAttributeDesignator();
            String attributeId = designator.getAttributeId();
            policyAdapter.setupUsingAttribute(attributeId, value);
        }
    }

    // This method generates the UI from rule configuration
    private void paramUiGenerate(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        String data = entity.getConfigurationData().getConfigBody();
        if (data == null) {
            return;
        }
        StringBuilder params = new StringBuilder();
        boolean flag = false;
        boolean comment = false;
        for (String line : data.split("\n")) {
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            if (line.contains(brmsTemplateVlaue)) {
                String value = line.substring(line.indexOf("<$%"), line.indexOf("%$>"));
                value = value.replace(brmsTemplateVlaue, "");
                policyAdapter.setRuleName(value);
            }
            if (line.contains("<%$Values=")) {
                String value = line.substring(line.indexOf("<%$"), line.indexOf("$%>"));
                value = value.replaceAll("<%\\$Values=", "");
                Arrays.stream(value.split(":\\|:")).map(keyValue -> keyValue.split(":-:"))
                        .filter(pair -> pair.length > 0)
                        .forEach(pair -> dynamicLayoutMap.put(pair[0], (pair.length > 1) ? pair[1] : ""));
                return;
            }
            if (line.startsWith("/*")) {
                comment = true;
                continue;
            }
            if ((line.contains("//")) && (!(line.contains("http://") || line.contains("https://")))) {
                line = splitSingleLineComment(line);
            }
            if (line.contains("/*")) {
                comment = true;
                if (line.contains("*/")) {
                    comment = false;
                    line = processMultiLineFullComment(line);
                } else {
                    line = splitMultiLineStartComment(line);
                }
            }
            if (line.contains("*/")) {
                comment = false;
                line = processEndComment(line);
            }
            if (comment) {
                continue;
            }
            if (flag) {
                params.append(line);
            }
            if (line.contains("rule") && line.contains(".Params\"")) {
                params.append(line);
                flag = true;
            }
            if (line.contains("end") && flag) {
                break;
            }
        }
        params = new StringBuilder(params.substring(params.indexOf(".Params\"") + 11));
        params = new StringBuilder(
                params.toString().replaceAll("\\s+", "").replace("salience1000whenthenParamsparams=newParams();", "")
                        .replace("insert(params);end", "").replace("params.set", ""));
        updateCaptionToDynamicLayoutMap(params);
    }

    private void updateCaptionToDynamicLayoutMap(final StringBuilder params) {
        String[] components = params.toString().split("\\);");
        if (components.length > 0) {
            for (int i = 0; i < components.length; i++) {
                String value;
                components[i] = components[i] + ")";
                String caption = components[i].substring(0, components[i].indexOf('('));
                caption = caption.substring(0, 1).toLowerCase() + caption.substring(1);
                if (components[i].contains("(\"")) {
                    value = components[i].substring(components[i].indexOf("(\""), components[i].indexOf("\")"))
                            .replace("(\"", "").replace("\")", "");
                } else {
                    value = components[i].substring(components[i].indexOf('('), components[i].indexOf(')'))
                            .replace("(", "").replace(")", "");
                }
                dynamicLayoutMap.put(caption, value);
            }
        }
    }

    private String processEndComment(String line) {
        try {
            line = splitMultiLineEndComment(line);
        } catch (Exception e) {
            policyLogger.info("Just for Logging" + e);
            line = "";
        }
        return line;
    }

    private String processMultiLineFullComment(String line) {
        try {
            line = splitMultiLineStartComment(line) + splitMultiLineEndComment(line);
        } catch (Exception e) {
            policyLogger.info("Just for Logging" + e);
            line = splitMultiLineStartComment(line);
        }
        return line;
    }

    /**
     * setViewRule.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"/policyController/ViewBRMSParamPolicyRule.htm"}, method = {RequestMethod.POST})
    public void setViewRule(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PolicyRestAdapter policyData = mapper.readValue(
                    root.get(PolicyController.getPolicydata()).get("policy").toString(), PolicyRestAdapter.class);
            policyData.setDomainDir(
                    root.get(PolicyController.getPolicydata()).get("model").get("name").toString().replace("\"", ""));
            if (root.get(PolicyController.getPolicydata()).get("model").get("type").toString().replace("\"", "")
                    .equals(PolicyController.getFile())) {
                policyData.setEditPolicy(true);
            }

            String body = findRule(policyData.getRuleName()) + "\n";
            StringBuilder generatedMetadata = new StringBuilder().append(
                    "/* Autogenerated Code Please Don't change/remove this comment section. "
                            + "This is for the UI purpose. \n\t ")
                    .append(brmsTemplateVlaue).append(policyData.getRuleName()).append("%$> \n */ \n");

            if (policyData.getDynamicLayoutMap().size() > 0) {
                generatedMetadata.append("/* <%$Values=");
                for (Entry<?, ?> entry : policyData.getRuleData().entrySet()) {
                    String uiKey = (String) entry.getKey();
                    if (!"templateName".equals(uiKey)) {
                        generatedMetadata.append(uiKey).append(":-:").append(entry.getValue()).append(":|:");
                    }
                }
                generatedMetadata.append("$%> \n*/ \n");
            }
            policyLogger.info("Metadata generated with :" + generatedMetadata.toString());
            body = generatedMetadata.toString() + body;
            // Expand the body.
            Map<String, String> copyMap =
                    new HashMap<>((Map<? extends String, ? extends String>) policyData.getRuleData());
            copyMap.put("policyName",
                    policyData.getDomainDir().replace("\\", ".") + ".Config_BRMS_Param_" + policyData.getPolicyName());
            copyMap.put("policyScope", policyData.getDomainDir().replace("\\", "."));
            copyMap.put("policyVersion", "1");
            // Finding all the keys in the Map data-structure.
            Set<String> keySet = copyMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                // Converting the first character of the key into a lower case.
                String input = iterator.next();
                String output = Character.toLowerCase(input.charAt(0)) + (input.length() > 1 ? input.substring(1) : "");
                // Searching for a pattern in the String using the key.
                Pattern pattern = Pattern.compile("\\$\\{" + output + "\\}");
                Matcher matcher = pattern.matcher(body);
                // Replacing the value with the inputs provided by the user in the editor.
                body = matcher.replaceAll(copyMap.get(input));
            }
            response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            response.setContentType(PolicyUtils.APPLICATION_JSON);
            request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);

            response.getWriter().write(new JSONObject("{policyData: " + mapper.writeValueAsString(body)
                + "}").toString());
        } catch (Exception e) {
            policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
        }
    }
}
