/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.xacml.test.std.pap;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;

public class StdPAPPolicyTest {
	
	private static Logger logger = FlexLogger.getLogger(StdPAPPolicyTest.class);
	private Path repository;
	Properties properties = new Properties();
	StdPAPPolicy stdPAPPolicy;
	
	@Before
	public void setUp(){
	
		repository = Paths.get("src/test/resources/pdps");
		try {
			stdPAPPolicy = new StdPAPPolicy();
		} catch (Exception e) {
			logger.info(e);
		}
	}

	@Test
	public void testGetActionAttribute(){
		try {
			stdPAPPolicy.setActionAttribute("test");
			assertTrue(stdPAPPolicy.getActionAttribute() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetActionBody(){
		try {
			stdPAPPolicy.setActionBody("actionBody");
			assertTrue(stdPAPPolicy.getActionBody() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetActionDictHeader(){
		try {
			stdPAPPolicy.setActionDictHeader("actionDictHeader");
			assertTrue(stdPAPPolicy.getActionDictHeader() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetActionDictMethod(){
		try {
			stdPAPPolicy.setActionDictMethod("actionDictMethod");
			assertTrue(stdPAPPolicy.getActionDictMethod() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetActionDictType(){
		try {
			stdPAPPolicy.setActionDictType("actionDictType");
			assertTrue(stdPAPPolicy.getActionDictType() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetActionDictUrl(){
		try {
			stdPAPPolicy.setActionDictUrl("actionDictUrl");
			assertTrue(stdPAPPolicy.getActionDictUrl() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetActionPerformer(){
		try {
			stdPAPPolicy.setActionPerformer("actionPerformer");
			assertTrue(stdPAPPolicy.getActionPerformer() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetBrmsController(){
		try {
			stdPAPPolicy.setBrmsController("brmsController");
			assertTrue(stdPAPPolicy.getBrmsController() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetBrmsDependency(){
		try {
			stdPAPPolicy.setBrmsDependency(new ArrayList());
			assertTrue(stdPAPPolicy.getBrmsDependency() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetConfigBodyData(){
		try {
			stdPAPPolicy.setConfigBodyData("configBodyData");
			assertTrue(stdPAPPolicy.getConfigBodyData() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	

	@Test
	public void testGetConfigName(){
		try {
			stdPAPPolicy.setConfigName("configName");
			assertTrue(stdPAPPolicy.getConfigName() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetConfigPolicyType(){
		try {
			stdPAPPolicy.setConfigPolicyType("configPolicyType");
			assertTrue(stdPAPPolicy.getConfigPolicyType() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetConfigType(){
		try {
			stdPAPPolicy.setConfigType("configType");
			assertTrue(stdPAPPolicy.getConfigType() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetDataTypeList(){
		try {
			stdPAPPolicy.setDataTypeList(new ArrayList<String>());
			assertTrue(stdPAPPolicy.getDataTypeList() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetDeleteCondition(){
		try {
			stdPAPPolicy.setDeleteCondition("deleteCondition");
			assertTrue(stdPAPPolicy.getDeleteCondition() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	
	@Test
	public void testGetDrlRuleAndUIParams(){
		try {
			stdPAPPolicy.setDrlRuleAndUIParams(new HashMap());
			assertTrue(stdPAPPolicy.getDrlRuleAndUIParams() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetDropDownMap(){
		try {
			stdPAPPolicy.setDropDownMap(new HashMap());
			assertTrue(stdPAPPolicy.getDropDownMap() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetDynamicFieldConfigAttributes(){
		try {
			assertTrue(stdPAPPolicy.getDynamicFieldConfigAttributes() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetDynamicRuleAlgorithmCombo(){
		try {
			stdPAPPolicy.setDynamicRuleAlgorithmCombo(new ArrayList());
			assertTrue(stdPAPPolicy.getDynamicRuleAlgorithmCombo() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetDynamicRuleAlgorithmField1(){
		try {
			stdPAPPolicy.setDynamicRuleAlgorithmField1(new ArrayList());
			assertTrue(stdPAPPolicy.getDynamicRuleAlgorithmField1() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	

	@Test
	public void testGetDictionary(){
		try {
			stdPAPPolicy.setDictionary("dictionary");
			assertTrue(stdPAPPolicy.getDictionary() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetDictionaryFields(){
		try {
			stdPAPPolicy.setDictionaryFields("dictionaryFields");
			assertTrue(stdPAPPolicy.getDictionaryFields() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetDictionaryType(){
		try {
			stdPAPPolicy.setDictionaryType("dictionaryType");
			assertTrue(stdPAPPolicy.getDictionaryType() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetDomainDir(){
		try {
			stdPAPPolicy.setDomainDir("domain");
			assertTrue(stdPAPPolicy.getDomainDir() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testIsDraft(){
		try {
			stdPAPPolicy.setDraft(true);
			assertTrue(stdPAPPolicy.isDraft() == true);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetDynamicRuleAlgorithmLabels(){
		try {
			stdPAPPolicy.setDynamicRuleAlgorithmLabels(new ArrayList());
			assertTrue(stdPAPPolicy.getDynamicRuleAlgorithmLabels() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetDynamicSettingsMap(){
		try {
			stdPAPPolicy.setDynamicSettingsMap(new HashMap());
			assertTrue(stdPAPPolicy.getDynamicSettingsMap() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetDynamicVariableList(){
		try {
			stdPAPPolicy.setDynamicVariableList(new ArrayList());
			assertTrue(stdPAPPolicy.getDynamicVariableList() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetGuard(){
		try {
			stdPAPPolicy.setGuard("domain");
			assertTrue(stdPAPPolicy.getGuard() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetHighestVersion(){
		try {
			stdPAPPolicy.setHighestVersion(123);
			assertTrue(stdPAPPolicy.getHighestVersion() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testgGtJsonBody(){
		try {
			stdPAPPolicy.setJsonBody("jsonBoby");
			assertTrue(stdPAPPolicy.getJsonBody() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetLocation(){
		try {
			stdPAPPolicy.setLocation(new URI("test"));
			assertTrue(stdPAPPolicy.getLocation() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetMsLocation(){
		try {
			stdPAPPolicy.setMsLocation("MsLocation");
			assertTrue(stdPAPPolicy.getMsLocation() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testSetOldPolicyFileName(){
		try {
			stdPAPPolicy.setOldPolicyFileName("domain");
			assertTrue(stdPAPPolicy.getOldPolicyFileName() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetOnapName(){
		try {
			stdPAPPolicy.setOnapName("onap");
			assertTrue(stdPAPPolicy.getOnapName() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetPolicyDescription(){
		try {
			stdPAPPolicy.setPolicyDescription("description test");
			assertTrue(stdPAPPolicy.getPolicyDescription() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetPolicyID(){
		try {
			stdPAPPolicy.setPolicyID("test");
			assertTrue(stdPAPPolicy.getPolicyID() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testSetPolicyName(){
		try {
			stdPAPPolicy.setPolicyName("MsLocation");
			assertTrue(stdPAPPolicy.getPolicyName() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testSetPriority(){
		try {
			stdPAPPolicy.setPriority("domain");
			assertTrue(stdPAPPolicy.getPriority() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetProviderComboBox(){
		try {
			stdPAPPolicy.setProviderComboBox("onap");
			assertTrue(stdPAPPolicy.getProviderComboBox() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetRiskLevel(){
		try {
			stdPAPPolicy.setRiskLevel("test");
			assertTrue(stdPAPPolicy.getRiskLevel() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetRiskType(){
		try {
			stdPAPPolicy.setRiskType("test");
			assertTrue(stdPAPPolicy.getRiskType() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testGetRuleID(){
		try {
			stdPAPPolicy.setRuleID("MsLocation");
			assertTrue(stdPAPPolicy.getRuleID() != null);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testGetServiceType(){
		try {
			stdPAPPolicy.setServiceType("domain");
			assertTrue(stdPAPPolicy.getServiceType() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
	@Test
	public void testGetTTLDate(){
		try {
			stdPAPPolicy.setTTLDate("09/20/17");
			assertTrue(stdPAPPolicy.getTTLDate() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	

	@Test
	public void testGetUuid(){
		try {
			stdPAPPolicy.setUuid("11212122");
			assertTrue(stdPAPPolicy.getUuid() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}	
	@Test
	public void testGetVersion(){
		try {
			stdPAPPolicy.setVersion("testv01");
			assertTrue(stdPAPPolicy.getVersion() != null);
		} catch (Exception e) {
			logger.info(e);
		}
		
	}	
	
	@Test
	public void testIsEditPolicy(){
		try {
			stdPAPPolicy.setEditPolicy(true);
			assertTrue(stdPAPPolicy.isEditPolicy() == true);
		} catch (Exception e) {
			logger.info(e);
		}	
	}	
	@Test
	public void testToString(){
		try {
			assertTrue(stdPAPPolicy.toString() != null);
		} catch (Exception e) {
			logger.info(e);
		}
	}
	
}