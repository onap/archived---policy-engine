/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package testpypdp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.openecomp.policy.pypdp.model_pojo.PepConfigFirewallPolicyRequest;
import org.openecomp.policy.pypdp.model_pojo.PepConfigPolicyNameRequest;
import org.openecomp.policy.pypdp.model_pojo.PepConfigPolicyRequest;
import org.openecomp.policy.pypdp.model_pojo.PepPushPolicyRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.DeletePolicyParameters;
import org.openecomp.policy.api.EventRequestParameters;
import org.openecomp.policy.api.PolicyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.openecomp.policy.pypdp.controller.Application;
import org.openecomp.policy.pypdp.controller.PolicyEngineServices;

/**
 * Test for Policy Engine REST Services
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		PolicyEngineServices.class })
@WebAppConfiguration
public class PolicyEngineServicesTest {
	private MockMvc mockMvc;
	// Don't Change this.
	private static final String CONFIG_ERROR_MESSAGE = "[{\"policyConfigMessage\": \"PE300 - Data Issue: PolicyFile Name is empty\","
			+ "\"policyConfigStatus\": \"CONFIG_NOT_FOUND\","
			+ "\"type\": null,"
			+ "\"config\": null,"
			+ "\"policyName\": null,"
			+ "\"policyVersion\": null,"
			+ "\"matchingConditions\": null,"
			+ "\"responseAttributes\": null," + "\"property\": null" + "}]";
	private static final String VALID_JSON = "{\"serviceTypeId\": \"/v0/firewall/pan\",\"configName\": \"rule1607\",\"deploymentOption\":{\"deployNow\": false},\"securityZoneId\": \"/v0/firewall/pan\",\"serviceGroups\": [{\"name\": \"1607Group\",\"description\": null,\"members\": [{\"type\": \"REFERENCE\",\"name\": \"SList\"},{\"type\": \"REFERENCE\",\"name\": \"Syslog\"}]}, {\"name\": \"Syslog\",\"description\": \"NA\",\"type\": \"SERVICE\",\"transportProtocol\": \"udp\",\"appProtocol\": null,\"ports\": \"514\"}, {\"name\": \"SList\",\"description\": \"Service List\",\"type\": \"SERVICE\",\"transportProtocol\": \"tcp\",\"appProtocol\": null,\"ports\": \"8080\"}],\"addressGroups\": [{\"name\": \"1607Group\",\"description\": null,\"members\": [{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"},{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"}]},{\"name\": \"PL_CCE3\",\"description\": \"CCE Routers\",\"members\":[{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"}]}],\"firewallRuleList\": [{\"position\": \"1\",\"ruleName\": \"1607Rule\",\"fromZones\": [\"Trusted\"],\"toZones\": [\"Untrusted\"],\"negateSource\": false,\"negateDestination\": false,\"sourceList\": [{\"type\": \"REFERENCE\",\"name\": \"PL_CCE3\"}, {\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"destinationList\": [{\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"sourceServices\": [],\"destServices\": [{\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"action\": \"accept\",\"description\": \"Rule for 1607 templates\",\"enabled\": true,\"log\": true}]}";
	private static final String INVALID_JSON = "{\"test\": \"value}";

	@Autowired
	private PolicyEngineServices policyEngineServicesMock;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

	}

	// Tests for getConfig API
	@Test
	public void getConfigUsingNoHeader() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName(".*");
		mockMvc.perform(
				post("/getConfig").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().is(400));
	}

	@Test
	public void getConfigUsingErrorHeader() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName(".*");
		mockMvc.perform(
				post("/getConfig").header("X-ECOMP-RequestID", "Error123")
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
	}

	@Test
	public void getConfigEmptyEcompName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setEcompName("");
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void getConfigEmptyPolicyName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName("");
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void getConfigUsingErrorPolicyName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName("test");
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void getConfigUsingErrorEcompName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setEcompName("test");
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void getConfigUsingALLPolicyName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setPolicyName(".*");
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
	}

	@Test
	public void getConfigUsingNullPolicyName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void getConfigUsingNullEcompName() throws Exception {
		ConfigRequestParameters pep = new ConfigRequestParameters();
		pep.setEcompName(null);
		mockMvc.perform(
				post("/getConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for GetConfigByPolicyName API
	@Test
	public void getConfigByPolicyNameUsingErrorHeader() throws Exception {
		PepConfigPolicyNameRequest pep = new PepConfigPolicyNameRequest();
		pep.setPolicyName(null);
		mockMvc.perform(
				post("/getConfigByPolicyName")
						.header("X-ECOMP-RequestID", "ERROR123")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().is(400));
	}

	@Test
	public void getConfigByPolicyNameUsingNoHeader() throws Exception {
		PepConfigPolicyNameRequest pep = new PepConfigPolicyNameRequest();
		pep.setPolicyName(null);
		mockMvc.perform(
				post("/getConfigByPolicyName").content(
						this.ObjectToJsonString(pep)).contentType(
						MediaType.APPLICATION_JSON)).andExpect(status().is(400));
	}

	@Test
	public void getConfigByPolicyNameUsingEmptyPolicyName() throws Exception {
		PepConfigPolicyNameRequest pep = new PepConfigPolicyNameRequest();
		pep.setPolicyName("");
		mockMvc.perform(
				post("/getConfigByPolicyName")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(CONFIG_ERROR_MESSAGE));
	}

	@Test
	public void getConfigByPolicyNameUsingNullPolicyName() throws Exception {
		PepConfigPolicyNameRequest pep = new PepConfigPolicyNameRequest();
		pep.setPolicyName(null);
		mockMvc.perform(
				post("/getConfigByPolicyName")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(CONFIG_ERROR_MESSAGE));
	}

	@Test
	public void getConfigByPolicyNameUsingALLPolicyName() throws Exception {
		PepConfigPolicyNameRequest pep = new PepConfigPolicyNameRequest();
		pep.setPolicyName(".*");
		mockMvc.perform(
				post("/getConfigByPolicyName")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
	}

	// Tests for SendEvent API
	@Test
	public void sendEventUsingNoHeader() throws Exception {
		EventRequestParameters pep = new EventRequestParameters();
		pep.setEventAttributes(null);
		mockMvc.perform(
				post("/sendEvent").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void sendEventUsingErrorHeader() throws Exception {
		EventRequestParameters pep = new EventRequestParameters();
		pep.setEventAttributes(null);
		mockMvc.perform(
				post("/sendEvent").header("X-ECOMP-RequestID", "ERROR123")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void sendEventUsingNullEventAttributes() throws Exception {
		EventRequestParameters pep = new EventRequestParameters();
		pep.setEventAttributes(null);
		mockMvc.perform(
				post("/sendEvent")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void sendEventUsingEmptyEventAttributes() throws Exception {
		EventRequestParameters pep = new EventRequestParameters();
		Map<String, String> emptyMap = new HashMap<String, String>();
		pep.setEventAttributes(emptyMap);
		mockMvc.perform(
				post("/sendEvent")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void sendEventUsingErrorEventAttributes() throws Exception {
		EventRequestParameters pep = new EventRequestParameters();
		Map<String, String> eventMap = new HashMap<String, String>();
		eventMap.put("key", "value");
		pep.setEventAttributes(eventMap);
		mockMvc.perform(
				post("/sendEvent")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for Push Policy API
	@Test
	public void pushPolicyUsingNoHeader() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		mockMvc.perform(
				put("/pushPolicy").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void pushPolicyUsingErrorHeader() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		mockMvc.perform(
				put("/pushPolicy").header("X-ECOMP-RequestID", "Error123")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void pushPolicyUsingNullRequest() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		mockMvc.perform(
				put("/pushPolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void pushPolicyUsingNoScope() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		pep.setPolicyName("Tarun");
		mockMvc.perform(
				put("/pushPolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void pushPolicyUsingScopeinName() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		pep.setPolicyName("Test.PolicyName");
		mockMvc.perform(
				put("/pushPolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void pushPolicyUsingPolicyType() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		pep.setPolicyName("Test.PolicyName");
		pep.setPolicyType("CONFIG BASE");
		mockMvc.perform(
				put("/pushPolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void pushPolicyUsingErrorRequest() throws Exception {
		PepPushPolicyRequest pep = new PepPushPolicyRequest();
		pep.setPolicyName("Test.PolicyName");
		pep.setPolicyType("CONFIG BASE");
		pep.setPdpGroup("default");
		mockMvc.perform(
				put("/pushPolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for Delete Policy API
	@Test
	public void deletePolicyUsingNoHeader() throws Exception {
		DeletePolicyParameters pep = new DeletePolicyParameters();
		mockMvc.perform(
				delete("/deletePolicy").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void deletePolicyUsingErrorHeader() throws Exception {
		DeletePolicyParameters pep = new DeletePolicyParameters();
		mockMvc.perform(
				delete("/deletePolicy").header("X-ECOMP-RequestID", "ERROR123")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void deletePolicyUsingErrorPolicyName() throws Exception {
		DeletePolicyParameters pep = new DeletePolicyParameters();
		pep.setPolicyName("test");
		mockMvc.perform(
				delete("/deletePolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void deletePolicyUsingErrorPolicyComponent() throws Exception {
		DeletePolicyParameters pep = new DeletePolicyParameters();
		pep.setPolicyName("test");
		pep.setPolicyComponent("test");
		mockMvc.perform(
				delete("/deletePolicy")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for CreatePolicy API
	@Test
	public void createPolicyUsingNoHeader() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/createPolicy").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createPolicyUsingErrorHeader() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/createPolicy").content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID", "Error123")
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createPolicyUsingNullParameters() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		mockMvc.perform(
				put("/createPolicy")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createPolicyUsingEmptyPolicyName() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("");
		mockMvc.perform(
				put("/createPolicy")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for UpdatePolicy API
	@Test
	public void updatePolicyUsingNoHeader() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/updatePolicy").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updatePolicyUsingErrorHeader() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/updatePolicy").content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID", "Error123")
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updatePolicyUsingNullParameters() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		mockMvc.perform(
				put("/updatePolicy")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updatePolicyUsingEmptyPolicyName() throws Exception {
		PolicyParameters pep = new PolicyParameters();
		pep.setPolicyName("");
		mockMvc.perform(
				put("/updatePolicy")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for createConfig API
	@Test
	public void createConfigUsingNoHeader() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/createConfig").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createConfigUsingErrorHeader() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/createConfig").content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID", "Error123")
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createConfigUsingNullParameters() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		mockMvc.perform(
				put("/createConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createConfigUsingEmptyPolicyName() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("");
		mockMvc.perform(
				put("/createConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createConfigUsingEmptyConfigName() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		pep.setEcompName("ecomp");
		mockMvc.perform(
				put("/createConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createConfigUsingEmptyPolicyScope() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		pep.setEcompName("ecomp");
		pep.setConfigName("config");
		mockMvc.perform(
				put("/createConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createConfigUsingErrorPolicyScope() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		pep.setEcompName("ecomp");
		pep.setConfigName("config");
		pep.setPolicyScope("test");
		mockMvc.perform(
				put("/createConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Test API for updateConfig API
	@Test
	public void updateConfigUsingNoHeader() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/updateConfig").content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateConfigUsingErrorHeader() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/updateConfig").content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID", "Error123")
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateConfigUsingNullParameters() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		mockMvc.perform(
				put("/updateConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateConfigUsingEmptyPolicyName() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("");
		mockMvc.perform(
				put("/updateConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateConfigUsingEmptyConfigName() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		pep.setEcompName("ecomp");
		mockMvc.perform(
				put("/updateConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateConfigUsingEmptyPolicyScope() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		pep.setEcompName("ecomp");
		pep.setConfigName("config");
		mockMvc.perform(
				put("/updateConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateConfigUsingErrorPolicyScope() throws Exception {
		PepConfigPolicyRequest pep = new PepConfigPolicyRequest();
		pep.setPolicyName("test");
		pep.setEcompName("ecomp");
		pep.setConfigName("config");
		pep.setPolicyScope("test");
		mockMvc.perform(
				put("/updateConfig")
						.content(this.ObjectToJsonString(pep))
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	// Tests for createFirewallConfig API
	@Test
	public void createFirewallConfigUsingNoHeader() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/createFirewallConfig").content(
						this.ObjectToJsonString(pep)).contentType(
						MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createFirewallConfigUsingErrorHeader() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/createFirewallConfig")
						.header("X-ECOMP-RequestID", "Error123")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createFirewallConfigUsingNullParameters() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		mockMvc.perform(
				put("/createFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createFirewallConfigUsingEmptyPolicyName() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("");
		mockMvc.perform(
				put("/createFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createFirewallConfigUsingEmptyPolicyScope() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		pep.setFirewallJson(VALID_JSON);
		pep.setPolicyScope("");
		mockMvc.perform(
				put("/createFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createFirewallConfigUsingInValidJSON() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		pep.setFirewallJson(INVALID_JSON);
		pep.setPolicyScope("test");
		mockMvc.perform(
				put("/createFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void createFirewallConfigUsingValidJSON() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		pep.setFirewallJson(VALID_JSON);
		pep.setPolicyScope("test");
		mockMvc.perform(
				put("/createFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
	}

	// Tests for updateFirewallConfig API 
	@Test
	public void updateFirewallConfigUsingNoHeader() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/updateFirewallConfig").content(
						this.ObjectToJsonString(pep)).contentType(
						MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateFirewallConfigUsingErrorHeader() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		mockMvc.perform(
				put("/updateFirewallConfig")
						.header("X-ECOMP-RequestID", "Error123")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateFirewallConfigUsingNullParameters() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		mockMvc.perform(
				put("/updateFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateFirewallConfigUsingEmptyPolicyName() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("");
		mockMvc.perform(
				put("/updateFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateFirewallConfigUsingEmptyPolicyScope() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		pep.setFirewallJson(VALID_JSON);
		pep.setPolicyScope("");
		mockMvc.perform(
				put("/updateFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateFirewallConfigUsingInValidJSON() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		pep.setFirewallJson(INVALID_JSON);
		pep.setPolicyScope("test");
		mockMvc.perform(
				put("/updateFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isBadRequest());
	}

	@Test
	public void updateFirewallConfigUsingValidJSON() throws Exception {
		PepConfigFirewallPolicyRequest pep = new PepConfigFirewallPolicyRequest();
		pep.setPolicyName("test");
		pep.setFirewallJson(VALID_JSON);
		pep.setPolicyScope("test");
		mockMvc.perform(
				put("/updateFirewallConfig")
						.header("X-ECOMP-RequestID",
								UUID.randomUUID().toString())
						.header("ClientAuth", "Basic bTAzNzQyOlBvbGljeVIwY2sk")
						.content(this.ObjectToJsonString(pep))
						.contentType(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
	}
	
	//Health Check Tests
	@Test
	public void getCountTest() throws Exception {
		mockMvc.perform(get("/count"))
		.andExpect(status().isOk());
	}
	
	@Test
	public void getPDPsTest() throws Exception {
		mockMvc.perform(get("/pdps"))
		.andExpect(status().isOk());
	}
	
	@Test
	public void getPAPsTest() throws Exception {
		mockMvc.perform(get("/paps"))
		.andExpect(status().isOk());
	}
	
	// Helper Method to create JSONString from a given Object.
	public String ObjectToJsonString(Object o) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(o);
	}

}
