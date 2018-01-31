/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.admin;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.controller.CreateFirewallController;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionList;
import org.onap.policy.rest.jpa.AddressGroup;
import org.onap.policy.rest.jpa.FWTagPicker;
import org.onap.policy.rest.jpa.GroupServiceList;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.SecurityZone;
import org.onap.policy.rest.jpa.ServiceList;
import org.onap.policy.rest.jpa.TermList;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.mock.web.MockHttpServletResponse;

public class PolicyRestControllerTest {

	private String clRequestString;
	private String fwRequestString;
	private String fwViewRequestString;
	private HttpServletRequest request;
	private MockHttpServletResponse response;
	private static CommonClassDao commonClassDao;
	private List<Object> prefixListData;
	private List<Object> actionListData;
	private List<Object> serviceListData;
	private List<Object> addressGroupData;
	private List<Object> securityZoneData;
	private List<Object> serviceGroupData;
	private List<Object> tagListData;
	private List<Object> termListData;

	@Before
	public void setUp() throws Exception {
		commonClassDao = mock(CommonClassDao.class);
		HttpSession mockSession = mock(HttpSession.class);
		request = mock(HttpServletRequest.class);
		response =  new MockHttpServletResponse();
		User user = new User();
		user.setOrgUserId("Test");
		Mockito.when(mockSession.getAttribute(SystemProperties.getProperty("user_attribute_name"))).thenReturn(user);
		Mockito.when(request.getSession(false)).thenReturn(mockSession);
		clRequestString = "{\"policyData\":{\"error\":\"\",\"inprocess\":false,\"model\":{\"name\":\"com\","
				+ "\"subScopename\":\"\",\"path\":[],\"type\":\"dir\",\"size\":0,\"date\":\"2017-06-01T15:45:36.000Z\","
				+ "\"version\":\"\",\"createdBy\":\"Demo\",\"modifiedBy\":\"Demo\",\"content\":\"\",\"recursive\":false},"
				+ "\"tempModel\":{\"name\":\"com\",\"subScopename\":\"\",\"path\":[],\"type\":\"dir\",\"size\":0,\"date\":\"2017-06-01T15:45:36.000Z\","
				+ "\"version\":\"\",\"createdBy\":\"Demo\",\"modifiedBy\":\"Demo\",\"content\":\"\",\"recursive\":false},\"$$hashKey\":\"object:1439\","
				+ "\"policy\":{\"policyType\":\"Config\",\"configPolicyType\":\"ClosedLoop_Fault\",\"triggerTrapSignatures\":[1,1,2,3],"
				+ "\"triggerfaultSignatures\":[1,1,2,3],\"traptriggerSignatures\":[{\"id\":\"Trap1\",\"$$hashKey\":\"object:1526\"},"
				+ "{\"id\":\"Trap2\",\"$$hashKey\":\"object:1534\"}],\"connecttriggerSignatures\":[{\"id\":\"C1\",\"$$hashKey\":\"object:1554\","
				+ "\"notBox\":\"NOT\",\"connectTrap1\":\"Trap1\",\"trapCount1\":\"12\",\"operatorBox\":\"AND\",\"connectTrap2\":\"Trap2\","
				+ "\"trapCount2\":\"14\"}],\"faulttriggerSignatures\":[{\"id\":\"Fault1\",\"$$hashKey\":\"object:1566\"},{\"id\":\"Fault2\","
				+ "\"$$hashKey\":\"object:1575\"}],\"connectVerificationSignatures\":[{\"id\":\"C1\",\"$$hashKey\":\"object:1595\","
				+ "\"notBox\":\"NOT\",\"connectTrap1\":\"Fault1\",\"trapCount1\":\"11\",\"operatorBox\":\"AND\",\"connectTrap2\":\"Fault2\","
				+ "\"trapCount2\":\"12\"}],\"jsonBodyData\":{\"trapMaxAge\":\"300\",\"vnfType\":\"Test\",\"closedLoopPolicyStatus\":\"Active\","
				+ "\"vUSP\":true,\"trinity\":true,\"vDNS\":true,\"mcr\":true,\"gamma\":true,\"actions\":\"Test\",\"timeInterval\":\"11\","
				+ "\"timeOutvPRO\":\"11\",\"timeOutRuby\":\"11\",\"retrys\":\"1\",\"agingWindow\":\"12\",\"geoLink\":\"test\","
				+ "\"emailAddress\":\"aa@test.com\",\"pepName\":\"Test\",\"pepAction\":\"test\",\"conditions\":\"SEND\"},"
				+ "\"policyName\":\"SampleTest\",\"policyDescription\":\"SampleTest\",\"riskType\":\"SampleRiskType\",\"riskLevel\":\"1\","
				+ "\"guard\":\"True\",\"onapName\":\"SampleDemo\",\"ttlDate\":\"14/09/2017\",\"clearTimeOut\":\"123\",\"trapMaxAge\":\"11\","
				+ "\"verificationclearTimeOut\":\"13\"}},\"trapData\":{\"trap1\":[{\"id\":\"A1\",\"$$hashKey\":\"object:1528\","
				+ "\"notBox\":\"NOT\",\"trigger1\":\"Test\",\"operatorBox\":\"AND\",\"trigger2\":\"Test\"}],\"trap2\":[{\"id\":\"A1\","
				+ "\"$$hashKey\":\"object:1536\",\"notBox\":\"NOT\",\"trigger1\":\"Test\",\"operatorBox\":\"AND\",\"trigger2\":\"Test\"},"
				+ "{\"id\":\"A2\",\"$$hashKey\":\"object:1542\",\"notBox\":\"NOT\",\"trigger1\":\"A1\",\"operatorBox\":\"AND\",\"trigger2\":"
				+ "\"Test\"},{\"id\":\"A3\",\"$$hashKey\":\"object:1548\",\"notBox\":\"NOT\",\"trigger1\":\"A1\",\"operatorBox\":\"OR\","
				+ "\"trigger2\":\"A2\"}]},\"faultData\":{\"trap1\":[{\"id\":\"A1\",\"$$hashKey\":\"object:1568\",\"notBox\":\"NOT\","
				+ "\"trigger1\":\"Test\",\"operatorBox\":\"AND\",\"trigger2\":\"Test\"}],\"trap2\":[{\"id\":\"A1\",\"$$hashKey\":\"object:1577\","
				+ "\"notBox\":\"NOT\",\"trigger1\":\"Test\",\"operatorBox\":\"AND\",\"trigger2\":\"Test\"},{\"id\":\"A2\",\"$$hashKey\":"
				+ "\"object:1583\",\"notBox\":\"NOT\",\"trigger1\":\"Test\",\"operatorBox\":\"OR\",\"trigger2\":\"Test\"},{\"id\":\"A3"
				+ "\",\"$$hashKey\":\"object:1589\",\"notBox\":\"NOT\",\"trigger1\":\"A1\",\"operatorBox\":\"AND\",\"trigger2\":\"A2\"}]}}";


		fwRequestString = "{\"policyData\":{\"error\":\"\",\"inprocess\":false,\"model\":{\"name\":\"com\",\"subScopename\":\"\",\"path\":[],"
				+ "\"type\":\"dir\",\"size\":0,\"date\":\"2017-06-01T15:45:36.000Z\",\"version\":\"\",\"createdBy\":\"Demo\",\"modifiedBy\":"
				+ "\"Demo\",\"content\":\"\",\"recursive\":false},\"tempModel\":{\"name\":\"com\",\"subScopename\":\"\",\"path\":[],\"type\":"
				+ "\"dir\",\"size\":0,\"date\":\"2017-06-01T15:45:36.000Z\",\"version\":\"\",\"createdBy\":\"Demo\",\"modifiedBy\":\"Demo\","
				+ "\"content\":\"\",\"recursive\":false},\"$$hashKey\":\"object:260\",\"policy\":{\"policyType\":\"Config\",\"configPolicyType"
				+ "\":\"Firewall Config\",\"attributes\":[{\"id\":\"choice1\",\"$$hashKey\":\"object:338\",\"key\":\"Test\",\"value\":\"Test\"}],"
				+ "\"fwattributes\":[],\"policyName\":\"SampleTest\",\"policyDescription\":\"SampleTest\",\"riskType\":\"SampleRiskType\","
				+ "\"riskLevel\":\"1\",\"guard\":\"True\",\"configName\":\"SampleTest\",\"ttlDate\":\"14/09/2017\",\"securityZone\":\"Test\"}}}";

		fwViewRequestString = "{\"policyData\":{\"policyType\":\"Config\",\"configPolicyType\":\"Firewall Config\",\"attributes\":[{\"id\":"
				+ "\"choice1\",\"$$hashKey\":\"object:338\",\"key\":\"Test\",\"value\":\"Test\"}],\"fwattributes\":[],\"policyName\":"
				+ "\"SampleTest\",\"policyDescription\":\"SampleTest\",\"riskType\":\"SampleRiskType\",\"riskLevel\":\"1\",\"guard\":\"True\","
				+ "\"configName\":\"SampleTest\",\"ttlDate\":\"14/09/2017\",\"securityZone\":\"Test\"}}";



		prefixListData = new ArrayList<>();
		PrefixList prefixList = new PrefixList();
		prefixList.setPrefixListName("Test");
		prefixList.setPrefixListValue("10.10.10.10/12");
		prefixListData.add(prefixList);
		when(commonClassDao.getData(PrefixList.class)).thenReturn(prefixListData);

		actionListData = new ArrayList<>();
		ActionList actionList = new ActionList();
		actionList.setActionName("Test");
		actionListData.add(actionList);
		when(commonClassDao.getData(ActionList.class)).thenReturn(actionListData);

		serviceListData = new ArrayList<>();
		ServiceList serviceList = new ServiceList();
		serviceList.setServiceName("Test");
		serviceList.setServiceType("SERVICE");
		serviceList.setServiceTransProtocol("Test");
		serviceList.setServiceAppProtocol("Test");
		serviceList.setServicePorts("8080");
		serviceListData.add(serviceList);
		when(commonClassDao.getData(ServiceList.class)).thenReturn(serviceListData);

		addressGroupData = new ArrayList<>();
		AddressGroup addressGroup = new AddressGroup();
		addressGroup.setGroupName("Group_Test");
		addressGroup.setServiceList("Test");
		addressGroupData.add(addressGroup);
		when(commonClassDao.getData(AddressGroup.class)).thenReturn(addressGroupData);

		securityZoneData = new ArrayList<>();
		SecurityZone securityZone = new SecurityZone();
		securityZone.setZoneName("Test");
		securityZone.setZoneValue("Test");
		securityZoneData.add(securityZone);
		when(commonClassDao.getData(SecurityZone.class)).thenReturn(securityZoneData);

		serviceGroupData = new ArrayList<>();
		GroupServiceList serviceGroup = new GroupServiceList();
		serviceGroup.setGroupName("Group_Test");
		serviceGroup.setServiceList("Test");
		serviceGroupData.add(serviceGroup);
		when(commonClassDao.getData(GroupServiceList.class)).thenReturn(serviceGroupData);

		tagListData = new ArrayList<>();
		FWTagPicker fwPicker = new FWTagPicker();
		fwPicker.setTagPickerName("Test");
		fwPicker.setTagValues("Test:8080");
		tagListData.add(fwPicker);
		when(commonClassDao.getData(FWTagPicker.class)).thenReturn(tagListData);

		termListData = new ArrayList<>();
		TermList termList = new TermList();
		termList.setTermName("Test");
		termList.setFromZones("Test");
		termList.setToZones("Test");
		termList.setSrcIPList("Test");
		termList.setDestIPList("Test");
		termList.setSrcPortList("Test");
		termList.setDestPortList("Test");
		termList.setAction("Test");
		termListData.add(termList);
		when(commonClassDao.getData(TermList.class)).thenReturn(termListData);
		when(commonClassDao.getDataById(TermList.class, "termName", "Test")).thenReturn(termListData);
	}

	@Test
	public final void testPolicyCreationController() {
		PolicyRestController controller = new PolicyRestController();
		BufferedReader reader = new BufferedReader(new StringReader(clRequestString));
		try {
			Mockito.when(request.getReader()).thenReturn(reader);
			controller.policyCreationController(request, response);
		} catch (IOException e) {
			fail();
		}
		PolicyRestController controller1 = new PolicyRestController();
		CreateFirewallController.setCommonClassDao(commonClassDao);
		BufferedReader reader1 = new BufferedReader(new StringReader(fwRequestString));
		try {
			Mockito.when(request.getReader()).thenReturn(reader1);
			controller1.policyCreationController(request, response);
		} catch (IOException e) {
			fail();
		}

		CreateFirewallController fwController = new CreateFirewallController();
		CreateFirewallController.setCommonClassDao(commonClassDao);
		BufferedReader reader2 = new BufferedReader(new StringReader(fwViewRequestString));
		try {
			Mockito.when(request.getReader()).thenReturn(reader2);
			fwController.setFWViewRule(request, response);
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public final void testDeleteElasticData() {
		PolicyRestController controller = new PolicyRestController();
		try {
			controller.deleteElasticData("Test");
		} catch (Exception e) {
			fail();
		}
	}

}
