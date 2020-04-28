/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * The class <code>CreateDcaeMicroServiceControllerTest</code> contains tests for the class
 *
 * <p/>All JUnits are designed to run in the local development environment where they have write privileges
 * and can execute time-sensitive tasks.
 *
 */

public class CreateDcaeMicroServiceControllerTest {

    private static Logger logger = FlexLogger.getLogger(CreateDcaeMicroServiceControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private String configBodyString = null;
    private HttpServletRequest request = null;

    /**
     * setUp.
     *
     * @throws Exception should not throw one
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = mock(CommonClassDao.class);
        MicroServiceModels testData = new MicroServiceModels();
        testData.setVersion("OpenOnap-Junit");
        testData.setModelName("modelName");
        testData.setRuleFormation("triggerSignature.signaturesAlarm.alarmSignatures.alarmSignature[VnfType, "
                + "Contains, FilterValue]@SymptomTriggerSignature.signaturesSymptom.symptomSignatures."
                + "symptomSignature[symptomVnfType, symptomContains, symptomFilterValue]");
        testData.setAttributes(
                "ParentCorrelation Name=String:defaultValue-null:required-true:MANY-false:description-null,"
                        + "CorrelationWindow=String:defaultValue-null:required-true:MANY-false:description-null,"
                        + "EmailNotification=String:defaultValue-null:required-true:MANY-false:description-null,"
                        + "CorrelationPriority=string:defaultValue-null:required-true:MANY-false:description-null,");
        testData.setRefAttributes("SymptomTriggerSignature=resource-model-symptomEntity:MANY-true:description-null,"
                + "triggerSignature=resource-model-entity:MANY-true:description-null,"
                + "SelectServerScope=SELECTSERVERSCOPE:MANY-false,logicalConnector=LOGICALCONNECTOR:MANY-false,"
                + "ParentCorrelationTraversal=PARENTCORRELATIONTRAVERSAL:MANY-false,");
        testData.setSubAttributes(
                "{\"symptomAlarms\":{\"symptomContains\":\"SYMPTOMCONTAINS:defaultValue-null:required-true:MANY-false:"
                        + "description-null\",\"symptomFilterValue\":\"string:defaultValue-null:"
                        + "required-true:MANY-false:"
                        + "description-null\",\"symptomVnfType\":\"SYMPTOMVNFTYPE:defaultValue-null:"
                        + "required-true:MANY-false:"
                        + "description-null\"},\"symptomElement\":{\"symptomSignatures\":\"symptomRange:required-true:"
                        + "MANY-true:description-null\",\"symptomTraversal\":\"SYMPTOMTRAVERSAL:defaultValue-null:"
                        + "required-true:MANY-false:description-null\"},\"alarms\":{\"Contains\":\"CONTAINS:"
                        + "defaultValue-null:required-true:MANY-false:description-null\",\"VnfType\":\"VNFTYPE:"
                        + "defaultValue-null:required-true:MANY-false:description-null\",\"FilterValue\":\"string:"
                        + "defaultValue-null:required-true:MANY-false:description-null\"},\"resource-model-entity\":"
                        + "{\"signaturesAlarm\":\"element:required-false:MANY-false:description-null\"},\"range\":"
                        + "{\"alarmSignature\":\"alarms:required-true:MANY-false:description-null\"},\"symptomRange\":"
                        + "{\"symptomSignature\":\"symptomAlarms:required-true:MANY-false:description-null\"},"
                        + "\"element\":"
                        + "{\"alarmSignatures\":\"range:required-true:MANY-true:description-null\",\"traversal\":"
                        + "\"TRAVERSAL:" + "defaultValue-null:required-true:MANY-false:description-null\"},"
                        + "\"resource-model-symptomEntity\":"
                        + "{\"signaturesSymptom\":\"symptomElement:required-false:MANY-false:description-null\"}}");
        testData.setAnnotation(
                "alarmSignatures=matching-true, symptomContains=matching-true, symptomSignatures=matching-true, "
                        + "symptomTraversal=matching-true, symptomVnfType=matching-true, Contains=matching-true, "
                        + "SelectServerScope=matching-true, VnfType=matching-true, traversal=matching-true, "
                        + "logicalConnector=matching-true, ParentCorrelationTraversal=matching-true");
        testData.setEnumValues("triggerSignature.signaturesAlarm.alarmSignatures.alarmSignature[VnfType, Contains, "
                + "FilterValue]@SymptomTriggerSignature.signaturesSymptom.symptomSignatures.symptomSignature"
                + "[symptomVnfType, symptomContains, symptomFilterValue]");
        testData.setDataOrderInfo("triggerSignature.signaturesAlarm.alarmSignatures.alarmSignature[VnfType, Contains, "
                + "FilterValue]@SymptomTriggerSignature.signaturesSymptom.symptomSignatures."
                + "symptomSignature[symptomVnfType, symptomContains, symptomFilterValue]");
        List<Object> microServiceModelsData = new ArrayList<Object>();
        microServiceModelsData.add(testData);

        // mock the getDataById() call
        when(commonClassDao.getDataById(MicroServiceModels.class, "modelName:version", "TESTMODEL" + ":" + "TODAY"))
                .thenReturn(microServiceModelsData);

        jsonString = "{\"policyData\":{\"error\":\"\",\"inprocess\":false,\"model\":{\"name\":\"DCAE\","
                + "\"subScopename\":\"\",\"path\":[],\"type\":\"dir\",\"size\":0,\"createdDate\":"
                + "\"2019-02-26 09:56:23.0\",\"modifiedDate\":\"2019-02-26 09:56:23.0\",\"version\":"
                + "\"\",\"createdBy\":\"super-admin\",\"modifiedBy\":\"super-admin\",\"roleType\":\"super-admin\","
                + "\"content\":\"\",\"recursive\":false},\"tempModel\":{\"name\":\"DCAE\",\"subScopename\":\"\","
                + "\"path\":[],\"type\":\"dir\",\"size\":0,\"createdDate\":\"2019-02-26 09:56:23.0\","
                + "\"modifiedDate\":\"2019-02-26 09:56:23.0\",\"version\":\"\",\"createdBy\":\"super-admin\","
                + "\"modifiedBy\":\"super-admin\",\"roleType\":\"super-admin\",\"content\":\"\","
                + "\"recursive\":false},\"$$hashKey\":\"object:354\",\"policy\":{\"policyType\":\"Config\","
                + "\"configPolicyType\":\"Micro Service\",\"serviceType\":\"TESTMODEL\",\"version\":\"TODAY\","
                + "\"ruleGridData\":[\"Correlation Priority\",\"Correlation Window\","
                + "\"Email Notification for failures\",\"Select Server Scope\","
                + "\"Parent Correlation Name\",\"Parent Correlation Traversal\","
                + "\"traversal\",\"FilterValue\"],\"policyName\":\"testttt\",\"onapName\":"
                + "\"asdafadf\",\"guard\":\"True\",\"riskType\":\"sfsgs\",\"riskLevel\":\"1\","
                + "\"priority\":\"1\",\"configName\":\"Search\",\"location\":\"Search\","
                + "\"uuid\":\"Search\",\"policyScope\":\"PolicyScope_ssaaa123\"}},"
                + "\"policyJSON\":{\"DCAEProcessingRules@0.processingRules_json\":"
                + "\"eyJuYW1lIjogIkpvaG4iLCAiYWdlIjogIjI4IiwgImNpdHkiOiAiTmV3IFlvcmsifQ==\"}}";

        configBodyString = "{\"service\":\"SniroPolicyEntityTest\",\"policyName\":\"someone\",\"description\":\"test\","
                + "\"templateVersion\":\"1607\",\"version\":\"HD\",\"priority\":\"2\","
                + "\"content\":{\"lastPolled\":\"1\",\"boolen-test\":\"true\",\"created\":\"test\","
                + "\"retiredDate\":\"test\",\"scope\":\"SNIRO_PLACEMENT_VDHV\",\"name\":\"test\","
                + "\"lastModified\":\"test\",\"state\":\"CREATED\",\"type\":\"CONFIG\",\"intent\":\"test\","
                + "\"target\":\"SNIRO\"}}";

        request = mock(HttpServletRequest.class);
        BufferedReader br = new BufferedReader(new StringReader(jsonString));
        // mock the getReader() call
        when(request.getReader()).thenReturn(br);

        logger.info("setUp: exit");
    }

    /**
     * Run the PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter, JsonNode) method test.
     */

    @Test
    public void testSetDataToPolicyRestAdapter() {

        logger.debug("testSetDataToPolicyRestAdapter: enter");

        CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);

        JsonNode root = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PolicyRestAdapter policyData = null;
        try {
            root = JsonLoader.fromString(jsonString);
            policyData = mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);
        } catch (Exception e) {
            logger.error("testSetDataToPolicyRestAdapter", e);
        }
        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
        PolicyRestAdapter result = controller.setDataToPolicyRestAdapter(policyData, root);
        assertTrue(result != null && result.getJsonBody() != null && !result.getJsonBody().isEmpty());

        logger.debug("result.getJsonBody() : " + result.getJsonBody());
        logger.debug("testSetDataToPolicyRestAdapter: exit");
    }

    /**
     * Run the ModelAndView getDCAEMSTemplateData(HttpServletRequest, HttpServletResponse) method test.
     */

    @Test
    public void testGetDcaeMsTemplateData() {

        logger.debug("testGetDCAEMSTemplateData: enter");

        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String msModelJson = "{\"policyData\":\"DkatPolicyBody\"}";
        try {

            CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);

            BufferedReader br = new BufferedReader(new StringReader(msModelJson));
            // mock the getReader() call
            when(request.getReader()).thenReturn(br);

            List<Object> microServiceModelsData = new ArrayList<Object>();
            MicroServiceModels testData = new MicroServiceModels();
            testData.setVersion("1707.4.1.2-Junit");
            microServiceModelsData.add(testData);
            // mock the getDataById() call with the same MS model name
            when(commonClassDao.getDataById(MicroServiceModels.class, "modelName", "DkatPolicyBody"))
                    .thenReturn(microServiceModelsData);

            controller.getDCAEMSTemplateData(request, response);

            assertTrue(
                    response.getContentAsString() != null && response.getContentAsString().contains("dcaeModelData"));

            logger.debug("response: " + response.getContentAsString());

        } catch (Exception e) {
            logger.error("testGetDCAEMSTemplateData", e);
        }

        logger.debug("testGetDCAEMSTemplateData: exit");
    }

    /**
     * Run the ModelAndView getModelServiceVersionData(HttpServletRequest, HttpServletResponse) method test.
     */

    @Test
    public void testGetModelServiceVersionData() {

        logger.debug("testGetModelServiceVersionData: enter");

        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String msModelJson = "{\"policyData\":\"DkatPolicyBody\"}";
        try {

            CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);

            BufferedReader br = new BufferedReader(new StringReader(msModelJson));
            // mock the getReader() call
            when(request.getReader()).thenReturn(br);

            List<Object> microServiceModelsData = new ArrayList<Object>();
            MicroServiceModels testData = new MicroServiceModels();
            testData.setVersion("1707.4.1.2-Junit");
            microServiceModelsData.add(testData);

            // mock the getDataById() call with the same MS model name
            when(commonClassDao.getDataById(MicroServiceModels.class, "modelName", "DkatPolicyBody"))
                    .thenReturn(microServiceModelsData);
            controller.getModelServiceVersionData(request, response);

            assertTrue(response.getContentAsString() != null
                    && response.getContentAsString().contains("1707.4.1.2-Junit"));

            logger.debug("response: " + response.getContentAsString());

        } catch (Exception e) {
            logger.error("testGetModelServiceVersionData", e);
            fail("testGetModelServiceVersionData failed due to: " + e);
        }

        logger.debug("testGetModelServiceVersionData: exit");
    }

    /**
     * Run the void getDCAEPriorityValuesData(HttpServletRequest, HttpServletResponse) method test.
     */

    @Test
    public void testGetDcaePriorityValuesData() {

        logger.debug("testGetDCAEPriorityValuesData: enter");

        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            controller.getDCAEPriorityValuesData(request, response);
            assertTrue(
                    response.getContentAsString() != null && response.getContentAsString().contains("priorityDatas"));
            logger.debug("response: " + response.getContentAsString());
        } catch (Exception e) {
            logger.error("testGetDCAEPriorityValuesData", e);
            fail("testGetDCAEPriorityValuesData failed due to: " + e);
        }

        logger.debug("testGetDCAEPriorityValuesData: exit");
    }

    /**
     * Run the void prePopulateDCAEMSPolicyData(PolicyRestAdapter, PolicyEntity) method test.
     */

    @Test
    public void testPrePopulateDcaeMsPolicyData() {

        logger.debug("testPrePopulateDCAEMSPolicyData: enter");

        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();

        // populate an entity object for testing
        PolicyEntity entity = new PolicyEntity();
        ConfigurationDataEntity configData = new ConfigurationDataEntity();
        configData.setConfigBody(configBodyString);
        entity.setConfigurationData(configData);

        JsonNode root = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PolicyRestAdapter restAdapter = null;

        try {
            root = JsonLoader.fromString(jsonString);
            restAdapter = mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);

            // create guard attribute
            MatchType matchType = new MatchType();
            // set value
            AttributeValueType attributeValue1 = new AttributeValueType();
            attributeValue1.getContent().add("True");
            matchType.setAttributeValue(attributeValue1);
            // set Id
            AttributeDesignatorType designator = new AttributeDesignatorType();
            designator.setAttributeId("guard");
            matchType.setAttributeDesignator(designator);
            AllOfType alltype = new AllOfType();
            alltype.getMatch().add(matchType);

            // add a dummy MatchType object since while (matchList.size()>1 ...)
            MatchType matchDummy = new MatchType();
            // set value
            AttributeValueType dummyValue = new AttributeValueType();
            dummyValue.getContent().add("dummy");
            matchDummy.setAttributeValue(dummyValue);
            // set Id
            AttributeDesignatorType designatorDummy = new AttributeDesignatorType();
            designatorDummy.setAttributeId("dummyId");
            matchDummy.setAttributeDesignator(designatorDummy);

            alltype.getMatch().add(matchDummy);
            AnyOfType anyOfType = new AnyOfType();
            anyOfType.getAllOf().add(alltype);

            TargetType target = new TargetType();
            target.getAnyOf().add(anyOfType);

            // create RiskType attribute
            AnyOfType anyRiskType = new AnyOfType();
            AllOfType allRiskType = new AllOfType();
            MatchType matchRiskType = new MatchType();
            // set value
            AttributeValueType riskTypeValue = new AttributeValueType();
            riskTypeValue.getContent().add("test");
            matchRiskType.setAttributeValue(riskTypeValue);
            // set Id
            AttributeDesignatorType designatorRiskType = new AttributeDesignatorType();
            designatorRiskType.setAttributeId("RiskType");
            matchRiskType.setAttributeDesignator(designatorRiskType);
            allRiskType.getMatch().add(matchRiskType);

            // add a dummy MatchType object since while (matchList.size()>1 ...)
            MatchType matchDummy1 = new MatchType();
            // set value
            AttributeValueType dummy1Value = new AttributeValueType();
            dummy1Value.getContent().add("dummy");
            matchDummy1.setAttributeValue(dummy1Value);
            // set Id
            AttributeDesignatorType designatorDummy1 = new AttributeDesignatorType();
            designatorDummy1.setAttributeId("dummyId");
            matchDummy1.setAttributeDesignator(designatorDummy1);

            allRiskType.getMatch().add(matchDummy1);

            anyRiskType.getAllOf().add(allRiskType);

            target.getAnyOf().add(anyRiskType);

            // create RiskLevel attribute
            MatchType matchRiskLevel = new MatchType();
            // set value
            AttributeValueType riskLevel = new AttributeValueType();
            riskLevel.getContent().add("3");
            matchRiskLevel.setAttributeValue(riskLevel);
            // set Id
            AttributeDesignatorType designatorRiskLevel = new AttributeDesignatorType();
            designatorRiskLevel.setAttributeId("RiskLevel");
            matchRiskLevel.setAttributeDesignator(designatorRiskLevel);
            AllOfType allRiskLevel = new AllOfType();
            allRiskLevel.getMatch().add(matchRiskLevel);

            // add a dummy MatchType object since while (matchList.size()>1 ...)
            MatchType matchDummy2 = new MatchType();
            // set value
            AttributeValueType dummy2Value = new AttributeValueType();
            dummy2Value.getContent().add("dummy");
            matchDummy2.setAttributeValue(dummy2Value);
            // set Id
            AttributeDesignatorType designatorDummy2 = new AttributeDesignatorType();
            designatorDummy2.setAttributeId("dummyId");
            matchDummy2.setAttributeDesignator(designatorDummy2);

            allRiskLevel.getMatch().add(matchDummy2);
            AnyOfType anyRiskLevel = new AnyOfType();
            anyRiskLevel.getAllOf().add(allRiskLevel);
            target.getAnyOf().add(anyRiskLevel);
            PolicyType policyType = new PolicyType();
            policyType.setTarget(target);

            restAdapter.setPolicyData(policyType);

            controller.prePopulateDCAEMSPolicyData(restAdapter, entity);

            logger.error("restAdapter.getRiskType() : " + restAdapter.getRiskType());
            logger.error("restAdapter.getRiskLevel() : " + restAdapter.getRiskLevel());
            logger.error("restAdapter.getGuard() : " + restAdapter.getGuard());

            assertEquals("True", restAdapter.getGuard());
            assertEquals("3", restAdapter.getRiskLevel());
            assertEquals("test", restAdapter.getRiskType());

        } catch (Exception e) {
            logger.error("testPrePopulateDCAEMSPolicyData", e);
            fail("testPrePopulateDCAEMSPolicyData failed due to: " + e);
        }

        logger.debug("testPrePopulateDCAEMSPolicyData: exit");

    }

    /**
     * Run the Map<String,String> convert(String, String) method test.
     */

    @Test
    public void testConvert() {
        logger.debug("testConvert: enter");

        String str = "k1=v1,k2=v2,k3=v3";
        String split = ",";
        Map<String, String> result = new CreateDcaeMicroServiceController().convert(str, split);
        assertTrue(result != null && result.size() == 3);

        logger.debug("testConvert: exit");
    }

    /**
     * Run the Map<String,String> convertMap(Map<String,String>, Map<String,String>) method test.
     */

    @Test
    public void testConvertMap() {
        logger.debug("testConvertMap: enter");
        Map<String, String> attributesMap = new HashMap<String, String>();

        attributesMap.put("keyOne", "valueOne");
        attributesMap.put("keyTwo", "valueTwo");
        attributesMap.put("keyThree", "valueThree");
        Map<String, String> attributesRefMap = new HashMap<String, String>();

        attributesRefMap.put("key4", "value4");
        attributesRefMap.put("key5", "value5");
        attributesRefMap.put("key6", "value6");
        CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
        Map<String, String> attributesListRefMap = controller.getAttributesListRefMap();
        LinkedList<String> list = new LinkedList<String>();

        attributesListRefMap.put("key7", "value7");

        list.add("l1");
        list.add("l2");
        Map<String, LinkedList<String>> arrayTextList = controller.getArrayTextList();
        arrayTextList.put("key8", list);

        Map<String, String> result = controller.convertMap(attributesMap, attributesRefMap);

        assertTrue(result != null && result.size() == 8);

        assertTrue(arrayTextList.get("key8").toString().contains("[l1, l2]"));

        logger.debug("testConvertMap: exit");
    }

    /**
     * Run the void SetMSModelData(HttpServletRequest, HttpServletResponse) method test.
     */

    // @Ignore
    @Test
    public void testSetMsModelData() {

        logger.debug("testSetMSModelData: enter");

        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getContentType())
                .andReturn("multipart/form-data; boundary=----WebKitFormBoundaryWcRUaIbC8kXgjr3p");
        expect(request.getMethod()).andReturn("post");
        expect(request.getHeader("Content-length")).andReturn("7809");

        expect(request.getContentLength()).andReturn(7809);

        try {
            // value of fileName needs to be matched to your local directory
            String fileName = "";
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                fileName = new File(classLoader.getResource("schedulerPolicies-v1707.xmi").getFile()).getAbsolutePath();
            } catch (Exception e1) {
                logger.error("Exception Occured while loading file" + e1);
            }
            expect(request.getInputStream()).andReturn(new MockServletInputStream(fileName));
            expect(request.getCharacterEncoding()).andReturn("UTF-8");
            expect(request.getContentLength()).andReturn(1024);
            replay(request);

        } catch (Exception e) {
            logger.error("testSetMSModelData" + e);
            e.printStackTrace();
        }

        logger.debug("testSetMSModelData: exit");
    }

    /**
     * @ Get File Stream.
     *
     */
    private class MockServletInputStream extends ServletInputStream {

        InputStream fis = null;

        public MockServletInputStream(String fileName) {
            try {
                fis = new FileInputStream(fileName);
            } catch (Exception genExe) {
                genExe.printStackTrace();
            }
        }

        @Override
        public int read() throws IOException {
            if (fis.available() > 0) {
                return fis.read();
            }
            return 0;
        }

        @Override
        public int read(byte[] bytes, int len, int size) throws IOException {
            if (fis.available() > 0) {
                int length = fis.read(bytes, len, size);
                return length;
            }
            return -1;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener arg0) {

        }
    }
}
