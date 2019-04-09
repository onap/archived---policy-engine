/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import java.util.stream.IntStream;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultBody;
import org.onap.policy.rest.adapter.ClosedLoopFaultTrapDatas;
import org.onap.policy.rest.adapter.ClosedLoopFaultTriggerUISignatures;
import org.onap.policy.rest.adapter.ClosedLoopSignatures;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.VarbindDictionary;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

@Controller
@RequestMapping("/")
public class CreateClosedLoopFaultController extends RestrictedBaseController {

    private static final Logger policyLogger = FlexLogger.getLogger(CreateClosedLoopFaultController.class);
    private static final String FAULT = "Fault";
    private static final String TRAP = "Trap";
    private static final String NOT_BOX = "notBox";
    private static final String OPERATOR_BOX = "operatorBox";
    private static final String CONNECT_TRAP_1 = "connectTrap1";
    private static final String TRAP_COUNT_1 = "trapCount1";
    private static final String CONNECT_TRAP_2 = "connectTrap2";
    private static final String TRAP_COUNT_2 = "trapCount2";
    private static final String TRIGGER_1 = "trigger1";
    private static final String ENC_UTF_8 = "UTF-8";
    private static final String TRIGGER_2 = "trigger2";

    protected PolicyRestAdapter policyAdapter = null;

    private static CommonClassDao commonclassdao;

    @Autowired
    private CreateClosedLoopFaultController(CommonClassDao commonclassdao) {
        CreateClosedLoopFaultController.commonclassdao = commonclassdao;
    }

    public CreateClosedLoopFaultController() {
        // Empty constructor
    }

    public PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter policyData, JsonNode root) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClosedLoopFaultTrapDatas trapDatas = mapper
                .readValue(root.get("trapData").toString(), ClosedLoopFaultTrapDatas.class);
            ClosedLoopFaultTrapDatas faultDatas = mapper
                .readValue(root.get("faultData").toString(), ClosedLoopFaultTrapDatas.class);
            ClosedLoopGridJSONData policyJsonData = mapper
                .readValue(root.get("policyData").get("policy").toString(), ClosedLoopGridJSONData.class);
            ClosedLoopFaultBody jsonBody = mapper
                .readValue(root.get("policyData").get("policy").get("jsonBodyData").toString(),
                    ClosedLoopFaultBody.class);

            // Build trapSignatureDatas list from faultData
            List<Object> trapSignatureDatas = new ArrayList<>();
            if (trapDatas.getTrap1() != null) {
                trapSignatureDatas.add(trapDatas);
            }
            // Extract resultBody and set jsonBody with trap ClosedLoopSignatures
            String resultBody = getResultBody(policyJsonData, trapSignatureDatas);
            ClosedLoopSignatures triggerSignatures = new ClosedLoopSignatures();
            triggerSignatures.setSignatures(resultBody);
            if (policyData.getClearTimeOut() != null) {
                triggerSignatures.setTimeWindow(Integer.parseInt(policyData.getClearTimeOut()));
                triggerSignatures.setTrapMaxAge(Integer.parseInt(policyData.getTrapMaxAge()));
                ClosedLoopFaultTriggerUISignatures uiTriggerSignatures = new ClosedLoopFaultTriggerUISignatures();
                if (!trapSignatureDatas.isEmpty()) {
                    uiTriggerSignatures.setSignatures(getUITriggerSignature(TRAP, trapSignatureDatas.get(0)));
                    if (!policyJsonData.getConnecttriggerSignatures().isEmpty()) {
                        uiTriggerSignatures
                            .setConnectSignatures(getUIConnectTraps(policyJsonData.getConnecttriggerSignatures()));
                    }
                }
                jsonBody.setTriggerSignaturesUsedForUI(uiTriggerSignatures);
                jsonBody.setTriggerTimeWindowUsedForUI(Integer.parseInt(policyData.getClearTimeOut()));
                jsonBody.setTrapMaxAge(Integer.parseInt(policyData.getTrapMaxAge()));
            }

            jsonBody.setTriggerSignatures(triggerSignatures);

            // Build faultSignatureData list from faultData
            List<Object> faultSignatureDatas = new ArrayList<>();
            if (faultDatas.getTrap1() != null) {
                faultSignatureDatas.add(faultDatas);
            }
            // Extract faultBody and set jsonBody with fault ClosedLoopSignatures
            String faultBody = getFaultBody(policyJsonData, faultSignatureDatas);
            ClosedLoopSignatures faultSignatures = new ClosedLoopSignatures();
            faultSignatures.setSignatures(faultBody);
            if (policyData.getVerificationclearTimeOut() != null) {
                faultSignatures.setTimeWindow(Integer.parseInt(policyData.getVerificationclearTimeOut()));
                ClosedLoopFaultTriggerUISignatures uifaultSignatures = new ClosedLoopFaultTriggerUISignatures();
                if (!faultSignatureDatas.isEmpty()) {
                    uifaultSignatures.setSignatures(getUITriggerSignature(FAULT, faultSignatureDatas.get(0)));
                    if (!policyJsonData.getConnectVerificationSignatures().isEmpty()) {
                        uifaultSignatures
                            .setConnectSignatures(getUIConnectTraps(policyJsonData.getConnectVerificationSignatures()));
                    }
                }
                jsonBody.setVerificationSignaturesUsedForUI(uifaultSignatures);
                jsonBody.setVerificationTimeWindowUsedForUI(Integer.parseInt(policyData.getVerificationclearTimeOut()));
            }
            jsonBody.setVerificationSignatures(faultSignatures);
            ObjectWriter om = new ObjectMapper().writer();
            String json = om.writeValueAsString(jsonBody);
            policyData.setJsonBody(json);

        } catch (Exception e) {
            policyLogger.error("Exception Occured while setting data to Adapter", e);
        }
        return policyData;
    }

    // TODO: Can getResultBody() and getFaultBody() be merged?
    private String getResultBody(final ClosedLoopGridJSONData policyJsonData, final List<Object> trapSignatureDatas) {
        StringBuilder resultBody = new StringBuilder();
        if (!policyJsonData.getConnecttriggerSignatures().isEmpty()) {
            resultBody.append("(");
            IntStream.range(0, policyJsonData.getConnecttriggerSignatures().size())
                .mapToObj(i -> connectTriggerSignature(i, policyJsonData.getConnecttriggerSignatures(),
                    trapSignatureDatas.get(0))).forEach(resultBody::append);
            resultBody.append(resultBody).append(")");
        } else {
            if (!trapSignatureDatas.isEmpty()) {
                resultBody.append(callTrap("nill", trapSignatureDatas.get(0)));
            }
        }
        return resultBody.toString();
    }

    private String getFaultBody(final ClosedLoopGridJSONData policyJsonData, final List<Object> faultSignatureDatas) {
        StringBuilder faultBody = new StringBuilder();
        if (!policyJsonData.getConnectVerificationSignatures().isEmpty()) {
            faultBody.append("(");
            IntStream.range(0, policyJsonData.getConnectVerificationSignatures().size())
                .mapToObj(i -> connectTriggerSignature(i, policyJsonData.getConnectVerificationSignatures(),
                    faultSignatureDatas.get(0))).forEach(faultBody::append);
            faultBody.append(")");
        } else {
            if (!faultSignatureDatas.isEmpty()) {
                faultBody.append(callTrap("nill", faultSignatureDatas.get(0)));
            }
        }
        return faultBody.toString();
    }

    @SuppressWarnings("unchecked")
    private String connectTriggerSignature(int index, List<Object> triggerSignatures, Object object) {
        StringBuilder resultBody = new StringBuilder();
        Map<String, String> connectTraps = (Map<String, String>) triggerSignatures.get(index);
        try {
            String notBox = "";
            if (connectTraps.keySet().contains(NOT_BOX)) {
                notBox = connectTraps.get(NOT_BOX);
            }
            resultBody.append("(").append(notBox);
        } catch (NullPointerException e) {
            policyLogger.info("General error", e);
            resultBody.append("(");
        }

        try {
            // Append connectTrap1 body to resultBody
            appendTrapToResultBody(triggerSignatures, object, resultBody, connectTraps, CONNECT_TRAP_1);

            // Update trap1 count to resultBody
            String trapCount1 = connectTraps.get(TRAP_COUNT_1);
            resultBody.append(", Time = ").append(trapCount1).append(")");

            // Append connectTrap2 body to resultBody
            appendTrapToResultBody(triggerSignatures, object, resultBody, connectTraps, CONNECT_TRAP_2);

            // Update operatorBox to resultBody
            String operatorBox = connectTraps.get(OPERATOR_BOX);
            resultBody.append(operatorBox).append("(");

            // Update trap2 count to resultBody
            String trapCount2 = connectTraps.get(TRAP_COUNT_2);
            resultBody.append(", Time = ").append(trapCount2).append(")");
        } catch (NullPointerException e) {
            policyLogger.info("General error", e);
        }
        return resultBody.toString();
    }

    private void appendTrapToResultBody(List<Object> triggerSignatures, Object object, StringBuilder resultBody,
        Map<String, String> connectTraps, String connectTrapName) {
        String connectTrap = connectTraps.get(connectTrapName);
        if (connectTrap.startsWith(TRAP) || connectTrap.startsWith(FAULT)) {
            String trapBody = callTrap(connectTrap, object);
            resultBody.append(trapBody);
        } else if (connectTrap.startsWith("C")) {
            for (int i = 0; i <= triggerSignatures.size(); i++) {
                Map<String, String> triggerSignature = (Map<String, String>) triggerSignatures.get(i);
                if (triggerSignature.get("id").equals(connectTrap)) {
                    resultBody.append("(");
                    String connectBody = connectTriggerSignature(i, triggerSignatures, object);
                    resultBody.append(connectBody).append(")");
                } else { // FIXME: Is this a bug and can it be removed?
                    i++;
                }
            }
        }
    }

    private String callTrap(String trap, Object object) {
        String signatureBody = "";
        ClosedLoopFaultTrapDatas trapDatas = (ClosedLoopFaultTrapDatas) object;
        List<Object> attributeList = new ArrayList<>();
        // Read the Trap
        if (!"nill".equals(trap)) {
            List<String> trapTypes = new ArrayList<>();
            if (trap.startsWith(TRAP)) {
                trapTypes = Arrays.asList("Trap1", "Trap2", "Trap3", "Trap4", "Trap5", "Trap6");
            } else if (trap.startsWith(FAULT)) {
                trapTypes = Arrays.asList("Fault1", "Fault2", "Fault3", "Fault4", "Fault5", "Fault6");
            }
            try {
                if (trapTypes.get(0).equals(trap)) {
                    attributeList = trapDatas.getTrap1();
                } else if (trapTypes.get(1).equals(trap)) {
                    attributeList = trapDatas.getTrap2();
                } else if (trapTypes.get(2).equals(trap)) {
                    attributeList = trapDatas.getTrap3();
                } else if (trapTypes.get(3).equals(trap)) {
                    attributeList = trapDatas.getTrap4();
                } else if (trapTypes.get(4).equals(trap)) {
                    attributeList = trapDatas.getTrap5();
                } else if (trapTypes.get(5).equals(trap)) {
                    attributeList = trapDatas.getTrap6();
                }
            } catch (Exception e) {
                policyLogger.warn("Error during callTrap", e);
                return "(" + trap + ")";
            }
        } else {
            if (trapDatas.getTrap1() == null) {
                return "";
            }
            attributeList = trapDatas.getTrap1();
        }
        signatureBody = signatureBody + "(" + readAttributes(attributeList, attributeList.size() - 1) + ")";
        return signatureBody;
    }

    @SuppressWarnings("unchecked")
    private String readAttributes(List<Object> object, int index) {
        String attributes = "";
        Map<String, String> trapSignatures = (Map<String, String>) object.get(index);
        // Read the Elements.
        Object notBox = "";
        if (trapSignatures.keySet().contains(NOT_BOX)) {
            notBox = trapSignatures.get(NOT_BOX);
        }
        if (notBox != null) {
            attributes = attributes + notBox.toString();
        }

        // Get Attributes for trap1 name
        Object trapName1 = trapSignatures.get(TRIGGER_1);
        if (trapName1 == null) {
            return "";
        }
        attributes = getTrapAttributesString(object, attributes, trapName1);

        Object comboBox = trapSignatures.get(OPERATOR_BOX);
        if (comboBox != null) {
            attributes = attributes + comboBox.toString();
        } else {
            return attributes;
        }

        // Get Attributes for trap1 name
        Object trapName2 = trapSignatures.get(TRIGGER_2);
        if (trapName2 != null) {
            attributes = getTrapAttributesString(object, attributes, trapName2);
        }
        return attributes;
    }

    private String getTrapAttributesString(List<Object> objectList, String attributesStr, Object trapName) {
        String trap1Attrib = trapName.toString();
        if (trap1Attrib.startsWith("A")) {
            try {
                int iy = Integer.parseInt(trap1Attrib.substring(1)) - 1;
                attributesStr = attributesStr + "(" + readAttributes(objectList, iy) + ")";
            } catch (NumberFormatException e) {
                try {
                    trap1Attrib = getVarbindOID(trap1Attrib);
                    attributesStr = attributesStr + "(" + URLEncoder.encode(trap1Attrib, ENC_UTF_8) + ")";
                } catch (UnsupportedEncodingException e1) {
                    policyLogger.error("Caused Exception while Encoding Varbind Dictionary Values", e1);
                }
            }
        } else {
            try {
                trap1Attrib = getVarbindOID(trap1Attrib);
                attributesStr = attributesStr + "(" + URLEncoder.encode(trap1Attrib, ENC_UTF_8) + ")";
            } catch (UnsupportedEncodingException e) {
                policyLogger.error("Caused Exception while Encoding Varbind Dictionary Values", e);
            }
        }
        return attributesStr;
    }

    private String getVarbindOID(String attrib) {
        VarbindDictionary varbindId;
        try {
            varbindId = (VarbindDictionary) commonclassdao
                .getEntityItem(VarbindDictionary.class, "varbindName", attrib);
            return varbindId.getVarbindOID();
        } catch (Exception e) {
            policyLogger.error("Error during retrieving varbindName " + attrib, e);
            return attrib;
        }
    }

    //connect traps data set to JSON Body as String
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String getUIConnectTraps(List<Object> connectTrapSignatures) {
        StringBuilder resultBody = new StringBuilder();
        String connectMainBody = "";
        for (Object connectTrapSignature : connectTrapSignatures) {
            Map<String, String> connectTraps = (Map<String, String>) connectTrapSignature;
            String connectBody = "";
            if (connectTraps instanceof LinkedHashMap<?, ?>) {
                String notBox = "";
                String connectTrap1 = "";
                String trapCount1 = "";
                String operatorBox = "";
                String connectTrap2 = "";
                String trapCount2 = "";
                if (((LinkedHashMap) connectTraps).keySet().contains(NOT_BOX)) {
                    notBox = ((LinkedHashMap) connectTraps).get(NOT_BOX).toString();
                }
                if (((LinkedHashMap) connectTraps).get(CONNECT_TRAP_1) != null) {
                    connectTrap1 = ((LinkedHashMap) connectTraps).get(CONNECT_TRAP_1).toString();
                }
                if (((LinkedHashMap) connectTraps).get(TRAP_COUNT_1) != null) {
                    trapCount1 = ((LinkedHashMap) connectTraps).get(TRAP_COUNT_1).toString();
                }
                if (((LinkedHashMap) connectTraps).get(OPERATOR_BOX) != null) {
                    operatorBox = ((LinkedHashMap) connectTraps).get(OPERATOR_BOX).toString();
                }
                if (((LinkedHashMap) connectTraps).get(CONNECT_TRAP_2) != null) {
                    connectTrap2 = ((LinkedHashMap) connectTraps).get(CONNECT_TRAP_2).toString();
                }
                if (((LinkedHashMap) connectTraps).get(TRAP_COUNT_2) != null) {
                    trapCount2 = ((LinkedHashMap) connectTraps).get(TRAP_COUNT_2).toString();
                }
                connectBody =
                    notBox + "@!" + connectTrap1 + "@!" + trapCount1 + "@!" + operatorBox + "@!" + connectTrap2 + "@!"
                        + trapCount2 + "#!?!";
            }
            resultBody.append(connectBody);
        }
        connectMainBody = connectMainBody + resultBody;
        return connectMainBody;
    }

    // get Trigger signature from JSON body
    private String getUITriggerSignature(String trap, Object object2) {
        ClosedLoopFaultTrapDatas trapDatas = (ClosedLoopFaultTrapDatas) object2;
        List<Object> attributeList = new ArrayList<>();
        // Read the Trap
        if (trap.startsWith(TRAP) || trap.startsWith(FAULT)) {
            if (trapDatas.getTrap1() != null) {
                attributeList.add(trapDatas.getTrap1());
            }
            if (trapDatas.getTrap2() != null) {
                attributeList.add(trapDatas.getTrap2());
            }
            if (trapDatas.getTrap3() != null) {
                attributeList.add(trapDatas.getTrap3());
            }
            if (trapDatas.getTrap4() != null) {
                attributeList.add(trapDatas.getTrap4());
            }
            if (trapDatas.getTrap5() != null) {
                attributeList.add(trapDatas.getTrap5());
            }
            if (trapDatas.getTrap6() != null) {
                attributeList.add(trapDatas.getTrap6());
            }
        }
        return getTriggerBody(attributeList);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String getTriggerBody(final List<Object> attributeList) {
        StringBuilder triggerBody = new StringBuilder();
        for (Object o : attributeList) {
            StringBuilder signatureBody = new StringBuilder();
            List<Object> connectTraps = (ArrayList<Object>) o;
            for (Object connectTrap : connectTraps) {
                String connectBody = "";
                if (connectTrap instanceof LinkedHashMap<?, ?>) {
                    String notBox = "";
                    String trigger1 = "";
                    String operatorBox = "";
                    String trigger2 = "";
                    if (((LinkedHashMap) connectTrap).keySet().contains(NOT_BOX)) {
                        notBox = ((LinkedHashMap) connectTrap).get(NOT_BOX).toString();
                    }
                    if (((LinkedHashMap) connectTrap).get(TRIGGER_1) != null) {
                        trigger1 = ((LinkedHashMap) connectTrap).get(TRIGGER_1).toString();
                    }
                    if (((LinkedHashMap) connectTrap).get(OPERATOR_BOX) != null) {
                        operatorBox = ((LinkedHashMap) connectTrap).get(OPERATOR_BOX).toString();
                    }
                    if (((LinkedHashMap) connectTrap).get(TRIGGER_2) != null) {
                        trigger2 = ((LinkedHashMap) connectTrap).get(TRIGGER_2).toString();
                    }
                    connectBody = notBox + "@!" + trigger1 + "@!" + operatorBox + "@!" + trigger2 + "#!";
                }
                signatureBody.append(connectBody);
            }
            triggerBody.append(signatureBody).append("?!");
        }
        return triggerBody.toString();
    }

    public void prePopulateClosedLoopFaultPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        if (policyAdapter.getPolicyData() instanceof PolicyType) {
            PolicyType policy = (PolicyType) policyAdapter.getPolicyData();

            // Set PolicyAdapter policyName, description
            setPolicyAdapterPolicyNameAndDescription(policyAdapter, policy);

            // Set PolicyAdapter JsonBodyData, timeout settings
            setClosedLoopJSONFile(policyAdapter, entity);

            // Get the target data under policy.
            TargetType target = policy.getTarget();
            if (target == null) {
                return;
            }

            // Under target we have AnyOFType
            List<AnyOfType> anyOfList = target.getAnyOf();
            if (anyOfList == null) {
                return;
            }

            // Set PolicyAdapter OnapNameField, riskType, riskLevel, guard, ttlDate from match attributes
            setPolicyAdapterMatchAttributes(policyAdapter, anyOfList);
        }
    }

    private void setPolicyAdapterMatchAttributes(PolicyRestAdapter policyAdapter, List<AnyOfType> anyOfList) {
        anyOfList.stream()
            //Extract nonNull list of AllOfType objs from each AnyOfType obj
            .map(AnyOfType::getAllOf).filter(Objects::nonNull)
            .forEach(allOfList ->
                //Extract nonNull list of MatchType objs from each AllOFType obj
                allOfList.stream().map(AllOfType::getMatch).filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .forEach(match -> {
                        // Under the match we have attribute value and
                        // attributeDesignator. So,finally down to the actual attribute.
                        AttributeValueType attributeValue = match.getAttributeValue();
                        String value = (String) attributeValue.getContent().get(0);
                        AttributeDesignatorType designator = match.getAttributeDesignator();
                        String attributeId = designator.getAttributeId();
                        // First match in the target is OnapName, so set that value.
                        if ("ONAPName".equals(attributeId)) {
                            policyAdapter.setOnapName(value);
                            OnapName onapName = new OnapName();
                            onapName.setOnapName(value);
                            policyAdapter.setOnapNameField(onapName);
                        } else if ("RiskType".equals(attributeId)) {
                            policyAdapter.setRiskType(value);
                        } else if ("RiskLevel".equals(attributeId)) {
                            policyAdapter.setRiskLevel(value);
                        } else if ("guard".equals(attributeId)) {
                            policyAdapter.setGuard(value);
                        } else if ("TTLDate".equals(attributeId) && !value.contains("NA")) {
                            PolicyController controller = new PolicyController();
                            String newDate = controller.convertDate(value);
                            policyAdapter.setTtlDate(newDate);
                        }
                    }));
    }

    private void setPolicyAdapterPolicyNameAndDescription(PolicyRestAdapter policyAdapter, PolicyType policy) {
        policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
        String policyNameValue = policyAdapter.getPolicyName()
            .substring(policyAdapter.getPolicyName().indexOf("Fault_") + 6);
        policyAdapter.setPolicyName(policyNameValue);
        String description;
        try {
            description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
        } catch (Exception e) {
            policyLogger.error(
                "Error during collecting the description tag info for createClosedLoopFault " + policyNameValue, e);
            description = policy.getDescription();
        }
        policyAdapter.setPolicyDescription(description);
    }

    private void setClosedLoopJSONFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ClosedLoopFaultBody closedLoopBody = mapper
                .readValue(entity.getConfigurationData().getConfigBody(), ClosedLoopFaultBody.class);
            if ("ACTIVE".equalsIgnoreCase(closedLoopBody.getClosedLoopPolicyStatus())) {
                closedLoopBody.setClosedLoopPolicyStatus("Active");
            } else {
                closedLoopBody.setClosedLoopPolicyStatus("InActive");
            }
            policyAdapter.setJsonBodyData(closedLoopBody);
            if (closedLoopBody.getTrapMaxAge() != null) {
                policyAdapter.setTrapMaxAge(closedLoopBody.getTrapMaxAge().toString());
            }
            if (closedLoopBody.getTriggerTimeWindowUsedForUI() != null) {
                policyAdapter.setClearTimeOut(closedLoopBody.getTriggerTimeWindowUsedForUI().toString());
            }
            if (closedLoopBody.getVerificationTimeWindowUsedForUI() != null) {
                policyAdapter
                    .setVerificationclearTimeOut(closedLoopBody.getVerificationTimeWindowUsedForUI().toString());
            }
        } catch (Exception e) {
            policyLogger.error("Exception Occured" + e);
        }
    }
}

class ClosedLoopGridJSONData {

    private String clearTimeOut;
    private String trapMaxAge;
    private String verificationclearTimeOut;
    private List<Object> connecttriggerSignatures;
    private List<Object> connectVerificationSignatures;

    public String getClearTimeOut() {
        return clearTimeOut;
    }

    public void setClearTimeOut(String clearTimeOut) {
        this.clearTimeOut = clearTimeOut;
    }

    public String getTrapMaxAge() {
        return trapMaxAge;
    }

    public void setTrapMaxAge(String trapMaxAge) {
        this.trapMaxAge = trapMaxAge;
    }

    public String getVerificationclearTimeOut() {
        return verificationclearTimeOut;
    }

    public void setVerificationclearTimeOut(String verificationclearTimeOut) {
        this.verificationclearTimeOut = verificationclearTimeOut;
    }


    public List<Object> getConnecttriggerSignatures() {
        return connecttriggerSignatures;
    }

    public void setConnecttriggerSignatures(List<Object> connecttriggerSignatures) {
        this.connecttriggerSignatures = connecttriggerSignatures;
    }

    public List<Object> getConnectVerificationSignatures() {
        return connectVerificationSignatures;
    }

    public void setConnectVerificationSignatures(List<Object> connectVerificationSignatures) {
        this.connectVerificationSignatures = connectVerificationSignatures;
    }
}
