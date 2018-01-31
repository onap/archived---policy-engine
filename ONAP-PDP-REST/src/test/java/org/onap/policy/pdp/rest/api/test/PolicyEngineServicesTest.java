/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.DeletePolicyCondition;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.api.DictionaryType;
import org.onap.policy.api.EventRequestParameters;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.pdp.rest.XACMLPdpServlet;
import org.onap.policy.pdp.rest.api.models.ConfigFirewallPolicyAPIRequest;
import org.onap.policy.pdp.rest.api.models.ConfigNameRequest;
import org.onap.policy.pdp.rest.api.models.ConfigPolicyAPIRequest;
import org.onap.policy.pdp.rest.api.services.CreateUpdatePolicyServiceImpl;
import org.onap.policy.pdp.rest.api.services.NotificationService;
import org.onap.policy.pdp.rest.config.PDPRestConfig;
import org.onap.policy.utils.PolicyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.att.research.xacml.util.XACMLProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PDPRestConfig.class})
@WebAppConfiguration
public class PolicyEngineServicesTest {
	private static final String CLIENTAUTHHEADER = "ClientAuth";
	private static final String UUIDHEADER = "X-ECOMP-RequestID";
	// This value is as per test resource code. Don't change this.
	private static final String CLIENTAUTHVALUE = "Basic cHl0aG9uOnRlc3Q=";
	private static final String ERRORCLIENTVALUE = "Basic dGVzdDp0ZXN0MTIz";

	private MockMvc mockMvc;
	private HttpHeaders headers;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before()
	public void setup() throws IOException{
		headers = new HttpHeaders();
		headers.add(CLIENTAUTHHEADER, CLIENTAUTHVALUE);
		XACMLProperties.reloadProperties();
		System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/pass.xacml.pdp.properties");
		XACMLProperties.getProperties();
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getConfigAPIFailureTest() throws Exception{
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName(".*");
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(400));
		// Authorization tests.
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, ""))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, ERRORCLIENTVALUE))
				.andExpect(status().isUnauthorized());
		// Set wrong request.
		pep.setPolicyName(null);
		pep.setConfigName("test");
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).headers(headers).header(UUIDHEADER, "123").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void getConfigServiceTest() throws Exception{
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName(".*");
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).headers(headers).header(UUIDHEADER, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		// Without policyName and using onapName and other fields.
		pep.setPolicyName(null);
		pep.setOnapName("test");
		pep.setConfigName("test");
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).headers(headers).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		// with config attributes.
		Map<String, String> configAttributes = new HashMap<>();
		configAttributes.put("test", "test");
		pep.setConfigAttributes(configAttributes);
		pep.makeUnique(true);
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).headers(headers).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

	@Test
	public void getConfigByPolicyNameTest() throws Exception{
		ConfigNameRequest pep = new ConfigNameRequest();
		pep.setPolicyName(".*");
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		// Authorization tests.
		mockMvc.perform(post("/getConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, ""))
		.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/getConfigByPolicyName").content(PolicyUtils.objectToJsonString(pep)).headers(headers).header(UUIDHEADER, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void listConfigTest() throws Exception{
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName(".*");
		mockMvc.perform(post("/listConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		// Authorization tests.
		mockMvc.perform(post("/listConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, ""))
		.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/listConfig").content(PolicyUtils.objectToJsonString(pep)).headers(headers).header(UUIDHEADER, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void getMetricsTest() throws Exception{
		//Failure Tests.
		mockMvc.perform(get("/getMetrics")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/getMetrics").header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(get("/getMetrics").headers(headers).header(UUIDHEADER, "123")).andExpect(status().isOk());
		mockMvc.perform(get("/getMetrics").headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isOk());
		mockMvc.perform(get("/getMetrics").headers(headers)).andExpect(status().isOk());
	}

	@Test
	public void getNotificationAuthFailureTest() throws Exception{
		mockMvc.perform(post("/getNotification").header(CLIENTAUTHHEADER, "").content("test")).andExpect(status().isUnauthorized());
		mockMvc.perform(post("/getNotification").header(CLIENTAUTHHEADER, "Basic test123").content("test")).andExpect(status().isUnauthorized());
		mockMvc.perform(post("/getNotification").header(CLIENTAUTHHEADER, ERRORCLIENTVALUE).content(" ")).andExpect(status().isUnauthorized());
	}

	@Test
	public void getNotificationTopicFailureTest() throws Exception{
		mockMvc.perform(post("/getNotification")).andExpect(status().isBadRequest());
		mockMvc.perform(post("/getNotification").headers(headers).content("")).andExpect(status().isBadRequest());
	}

	@Test
	public void getNotificationTopicValidFailTest() throws Exception{
		// Check failures.
		mockMvc.perform(post("/getNotification").headers(headers).content(" ")).andExpect(status().isBadRequest());
		mockMvc.perform(post("/stopNotification").headers(headers).content(" ")).andExpect(status().isBadRequest());
		mockMvc.perform(post("/sendHeartbeat").headers(headers).content(" ")).andExpect(status().isBadRequest());
	}

	@Test
	public void getNotificationTopicValidPassTest() throws Exception{
        // Values can be polluted due to failure tests and need to be reloaded.
	    XACMLProperties.reloadProperties();
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/notification.xacml.pdp.properties");
        XACMLProperties.getProperties();
        NotificationService.reloadProps();
		// Add a Topic.
		mockMvc.perform(post("/getNotification").headers(headers).header(UUIDHEADER, "123").content("test")).andExpect(status().isOk());
		// Try to add same topic should fail.
		mockMvc.perform(post("/getNotification").headers(headers).header(UUIDHEADER, UUID.randomUUID()).content("test")).andExpect(status().isBadRequest());
		// do a heart beat.
		mockMvc.perform(post("/sendHeartbeat").headers(headers).content("test")).andExpect(status().isOk());
		// remove the added Topic.
		mockMvc.perform(post("/stopNotification").headers(headers).content("test")).andExpect(status().isOk());
		// try to remove again should fail.
		mockMvc.perform(post("/sendHeartbeat").headers(headers).content("test")).andExpect(status().isBadRequest());
	}

	@Test
	public void sendEventTest() throws Exception{
		EventRequestParameters pep = new EventRequestParameters();
		Map<String,String> eventAttributes = new HashMap<>();
		eventAttributes.put("TEST", "test");
		pep.setEventAttributes(eventAttributes);
		//Failure Tests.
		mockMvc.perform(post("/sendEvent")).andExpect(status().isBadRequest());
		mockMvc.perform(post("/sendEvent").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(post("/sendEvent").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "123")).andExpect(status().isOk());
		pep.setEventAttributes(null);
		mockMvc.perform(post("/sendEvent").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setEventAttributes(eventAttributes);
		mockMvc.perform(post("/sendEvent").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isOk());
		pep.setEventAttributes(eventAttributes);
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(post("/sendEvent").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isOk());
	}

	@Test
	public void getDecisionTest() throws Exception{
		DecisionRequestParameters pep = new DecisionRequestParameters();
		Map<String,String> eventAttributes = new HashMap<>();
		eventAttributes.put("TEST", "test");
		pep.setOnapName("te123");
		pep.setDecisionAttributes(eventAttributes);
		//Failure Tests.
		mockMvc.perform(post("/getDecision")).andExpect(status().isBadRequest());
		mockMvc.perform(post("/getDecision").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(post("/getDecision").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "123")).andExpect(status().isOk());
		pep.setDecisionAttributes(null);
		pep.setOnapName(null);
		mockMvc.perform(post("/getDecision").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setOnapName("testing");
		mockMvc.perform(post("/getDecision").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setDecisionAttributes(eventAttributes);
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(post("/getDecision").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isOk());
	}

	@Test
	public void pushPolicyTest() throws Exception{
		PushPolicyParameters pep = new PushPolicyParameters();
		//Failure Tests.
		mockMvc.perform(put("/pushPolicy")).andExpect(status().isBadRequest());
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("scopeless");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyName("testing.test");
		pep.setPolicyType("wrong");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("BRMS_PARAM");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("BRMS_RAW");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("MicroService");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("ClosedLoop_PM");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("ClosedLoop_Fault");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("Base");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("Decision");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("Action");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("Firewall");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPdpGroup("default");
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "123")).andExpect(status().isBadRequest());
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(put("/pushPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void deletePolicyTest() throws Exception{
		DeletePolicyParameters pep = new DeletePolicyParameters();
		//Failure Tests.
		mockMvc.perform(delete("/deletePolicy")).andExpect(status().isBadRequest());
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("testing");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "test123")).andExpect(status().isBadRequest());
		pep.setPolicyName("testscope.name");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyName("testscope.name");
		pep.setPolicyType("wrong");
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("BRMS_PARAM");
		pep.setPolicyComponent("wrong");
		pep.setRequestID(null);
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyType("BRMS_RAW");
		pep.setPolicyComponent("PDP");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyType("MicroService");
		pep.setPolicyComponent("PAP");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyType("ClosedLoop_PM");
		pep.setPolicyComponent("PDP");
		pep.setDeleteCondition(DeletePolicyCondition.ALL);
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyType("ClosedLoop_Fault");
		pep.setDeleteCondition(DeletePolicyCondition.ONE);
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyType("Base");
		pep.setPolicyComponent("PAP");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyType("Decision");
		pep.setPolicyComponent("PDP");
		pep.setPolicyName("test.xml");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyType("Action");
		pep.setPolicyName("scope.Config_test.xml");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPdpGroup("default");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyComponent("PAP");
		pep.setPolicyType("Firewall");
		pep.setDeleteCondition(null);
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyComponent("PAP");
		pep.setDeleteCondition(DeletePolicyCondition.ONE);
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyComponent("fail");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyComponent("PDP");
		pep.setPolicyName("testscope.policyName");
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyComponent(null);
		mockMvc.perform(delete("/deletePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
	}

	@Test
	public void createUpdatePolicyTest() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		//Failure Tests.
		mockMvc.perform(put("/createPolicy")).andExpect(status().isBadRequest());
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		setCreateUpdateImpl();
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("failName");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "test 123")).andExpect(status().isBadRequest());
		pep.setPolicyName("test. name");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setPolicyName("   ");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("test. ");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("te st.name");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("testá");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void brmsPolicyCreationTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for BRMS Param Policy.
		pep.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("test");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		Map<AttributeType, Map<String,String>> attributes = new HashMap<>();
		Map<String,String> matching = new HashMap<>();
		matching.put("key", "value");
		attributes.put(AttributeType.MATCHING, matching);
		pep.setAttributes(attributes);
		pep.setRiskLevel("5");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		// Checks for BRMS Raw Policy
		pep.setPolicyConfigType(PolicyConfigType.BRMS_RAW);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("test");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel(null);
		pep.setConfigBody("package droolsexample\n\n import com.sample.ItemCity;\nimport java.math.BigDecimal;\nrule \"Nagpur Medicine Item\"\n\n   when\n      item : ItemCity(purchaseCity == ItemCity.City.NAGPUR,\n         typeofItem == ItemCity.Type.MEDICINES)\n   then\n      BigDecimal tax = new BigDecimal(0.0);\n      item.setLocalTax(tax.multiply(item.getSellPrice()));\nend");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("5");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void baseConfigTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for Base config Policy.
		pep.setPolicyConfigType(PolicyConfigType.Base);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("testbody");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBodyType(PolicyType.OTHER);
		pep.setRiskLevel("test");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("4");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setOnapName("ec nam-e");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setOnapName("onapName");
		pep.setConfigName("tes config");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigName("configName");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{'test':'test}");
		pep.setConfigBodyType(PolicyType.JSON);
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		Map<AttributeType, Map<String,String>> attributes = new HashMap<>();
		Map<String,String> matching = new HashMap<>();
		matching.put("key", "value");
		attributes.put(AttributeType.MATCHING, matching);
		pep.setAttributes(attributes);
		pep.setConfigBody("testBody");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void closedLoopPolicyTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for Closed loop Policy.
		pep.setPolicyConfigType(PolicyConfigType.ClosedLoop_Fault);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("te stá");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("testBody");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"key\":\"value\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"onapname\":\"\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"onapname\":\"test\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("test");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("4");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void closedLoopPMTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for Closed loop Policy.
		pep.setPolicyConfigType(PolicyConfigType.ClosedLoop_PM);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("te stá");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("testBody");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"key\":\"value\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"onapname\":\"\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"onapname\":\"test\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"onapname\":\"test\", \"serviceTypePolicyName\":\"value\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("test");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("4");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void firewallPolicyTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for Closed loop Policy.
		pep.setPolicyConfigType(PolicyConfigType.Firewall);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("te st");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"test\":\"test\"}");
		pep.setRiskLevel("test");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("4");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"configName\":\"test\"}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void microServicePolicyTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for Closed loop Policy.
		pep.setPolicyConfigType(PolicyConfigType.MicroService);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("te st");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{}");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"test\":\"test\"}");
		pep.setOnapName("   ");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setOnapName("testonap");
		pep.setRiskLevel("fail");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setRiskLevel("4");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setConfigBody("{\"service\":\"test\",\"uuid\":\"test\",\"location\":\"test\",\"configName\":\"test\",\"description\":\"test\",\"priority\":\"test\",\"version\":\"test\"}");
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void actionDecisionPolicyCreationTests() throws Exception{
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test.name");
		pep.setPolicyDescription("good");
		pep.setTtlDate(new Date());
		pep.setRequestID(UUID.randomUUID());
		setCreateUpdateImpl();
		// Checks for action Policy.
		pep.setPolicyClass(PolicyClass.Action);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		Map<AttributeType, Map<String,String>> attributes = new HashMap<>();
		pep.setAttributes(attributes);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		attributes.put(AttributeType.MATCHING, new HashMap<>());
		pep.setAttributes(attributes);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		Map<String,String> matching = new HashMap<>();
		matching.put("key", "value");
		attributes.put(AttributeType.MATCHING, matching);
		pep.setAttributes(attributes);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setActionAttribute("A1");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setActionPerformer("PEX");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setActionPerformer("PEP");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isInternalServerError());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isInternalServerError());
		// Checks for Decision Policy.
		pep.setPolicyClass(PolicyClass.Decision);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setOnapName("xyz");
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		attributes.remove(AttributeType.MATCHING);
		attributes.put(AttributeType.SETTINGS, matching);
		pep.setAttributes(attributes);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		attributes.put(AttributeType.MATCHING, matching);
		pep.setAttributes(attributes);
		mockMvc.perform(put("/createPolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/updatePolicy").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void createUpdateDictionaryTests() throws Exception{
		DictionaryParameters pep = new DictionaryParameters();
		//Failure Tests.
		mockMvc.perform(put("/createDictionaryItem")).andExpect(status().isBadRequest());
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		mockMvc.perform(put("/updateDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setDictionaryType(DictionaryType.MicroService);
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isBadRequest());
		pep.setDictionary("test dict");
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
		pep.setDictionaryJson("{\"test\":\"value\"}");
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID())).andExpect(status().isInternalServerError());
		pep.setDictionaryJson("test123");
		mockMvc.perform(put("/updateDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "123")).andExpect(status().isBadRequest());
		pep.setDictionary("MicroServiceDictionary");
		mockMvc.perform(put("/createDictionaryItem").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isBadRequest());
	}

	@Test
	public void getDictionaryTests() throws Exception{
		DictionaryParameters pep = new DictionaryParameters();
		//Failure Tests.
		mockMvc.perform(post("/getDictionaryItems")).andExpect(status().isBadRequest());
		mockMvc.perform(post("/getDictionaryItems").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(post("/getDictionaryItems").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setDictionaryType(DictionaryType.Common);
		mockMvc.perform(post("/getDictionaryItems").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, UUID.randomUUID().toString())).andExpect(status().isBadRequest());
		pep.setDictionary("OnapName");
		mockMvc.perform(post("/getDictionaryItems").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isInternalServerError());
		pep.setRequestID(UUID.randomUUID());
		mockMvc.perform(post("/getDictionaryItems").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers)).andExpect(status().isInternalServerError());
	}

	@Test
	public void policyEngineImportTests() throws Exception{
		//Failure Tests.
		mockMvc.perform(post("/policyEngineImport")).andExpect(status().isBadRequest());
	}

	@Test
	public void oldConfigAPITests() throws Exception{
		ConfigPolicyAPIRequest pep = new ConfigPolicyAPIRequest();
		//Failure Tests.
		mockMvc.perform(put("/createConfig")).andExpect(status().isBadRequest());
		mockMvc.perform(put("/createConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		mockMvc.perform(put("/updateConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(put("/createConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setPolicyScope("test");
		mockMvc.perform(put("/createConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setPolicyName("name");
		mockMvc.perform(put("/createConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setConfigType("OTHER");
		mockMvc.perform(put("/createConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setTtlDate(new Date().toString());
		mockMvc.perform(put("/updateConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
	}

	@Test
	public void oldFirewallAPITests() throws Exception{
		ConfigFirewallPolicyAPIRequest pep = new ConfigFirewallPolicyAPIRequest();
		//Failure Tests.
		mockMvc.perform(put("/createFirewallConfig")).andExpect(status().isBadRequest());
		mockMvc.perform(put("/createFirewallConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		mockMvc.perform(put("/updateFirewallConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON).header(CLIENTAUTHHEADER, "Basic 123")).andExpect(status().isUnauthorized());
		//Service Tests.
		mockMvc.perform(put("/createFirewallConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setPolicyScope("test");
		mockMvc.perform(put("/createFirewallConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setPolicyName("name");
		mockMvc.perform(put("/createFirewallConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
		pep.setTtlDate(new Date().toString());
		mockMvc.perform(put("/updateFirewallConfig").content(PolicyUtils.objectToJsonString(pep)).contentType(MediaType.APPLICATION_JSON)
				.headers(headers).header(UUIDHEADER, "tes123")).andExpect(status().isBadRequest());
	}

	private void setCreateUpdateImpl() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setter = XACMLPdpServlet.class.getDeclaredMethod("setCreateUpdatePolicyConstructor", String.class);
		setter.setAccessible(true);
		setter.invoke(new XACMLPdpServlet(), CreateUpdatePolicyServiceImpl.class.getName());
	}

	//Health Check Tests
	@Test
	public void getCountTest() throws Exception {
		mockMvc.perform(get("/count"))
		.andExpect(status().isOk());
	}
}
