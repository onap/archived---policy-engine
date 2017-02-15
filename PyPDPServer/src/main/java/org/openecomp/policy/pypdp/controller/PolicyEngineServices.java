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

package org.openecomp.policy.pypdp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.DeletePolicyParameters;
import org.openecomp.policy.api.EventRequestParameters;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.api.PolicyResponse;
import org.openecomp.policy.pypdp.ConfigFirewallPolicyRequest;
import org.openecomp.policy.pypdp.ConfigRequest;
import org.openecomp.policy.pypdp.DeletePolicyRequest;
import org.openecomp.policy.pypdp.EventRequest;
import org.openecomp.policy.pypdp.ListConfigRequest;
import org.openecomp.policy.pypdp.PolicyCreateUpdateRequest;
import org.openecomp.policy.pypdp.PushPolicyRequest;
import org.openecomp.policy.pypdp.authorization.AuthenticationService;
import org.openecomp.policy.pypdp.authorization.Config;
import org.openecomp.policy.pypdp.jmx.PyPdpMonitor;
import org.openecomp.policy.pypdp.model_pojo.PepConfigFirewallPolicyRequest;
import org.openecomp.policy.pypdp.model_pojo.PepConfigPolicyNameRequest;
import org.openecomp.policy.pypdp.model_pojo.PepConfigPolicyRequest;
import org.openecomp.policy.pypdp.model_pojo.PepPushPolicyRequest;
import org.openecomp.policy.pypdp.model_pojo.PyPolicyConfig;
import org.openecomp.policy.pypdp.notifications.NotificationController;
import org.openecomp.policy.std.StdPolicyEngine;
import org.openecomp.policy.utils.PolicyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.im.AdministrativeStateException;
import org.openecomp.policy.common.im.StandbyStatusException;

@RestController
@Api(value="Policy Engine Services")
public class PolicyEngineServices {
	private final NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
	private final NotificationController handler = new NotificationController();
	private final AtomicLong configCounter = PyPdpMonitor.singleton.getAtomicConfigCounter();
	private final AtomicLong eventCounter =  PyPdpMonitor.singleton.getAtomicEventCounter();
	private final AtomicLong configPolicyNameCounter = PyPdpMonitor.singleton.getAtomicConfigPolicyNameCounter();
	private final StdPolicyEngine policyEngine = new StdPolicyEngine(Config.getPDPs(), Config.getPAPs(), Config.getEncodingPAP(), Config.getEncoding(), scheme, handler, Config.getEnvironment(), Config.getClientFile(), Config.isTest());
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Gets the configuration from the PolicyDecisionPoint(PDP)")
	@RequestMapping(value = "/getConfig", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Collection<PyPolicyConfig>> createConfigRequest(@RequestBody ConfigRequestParameters pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding, @RequestHeader(value="X-ECOMP-RequestID", required=false)String requestID) {		
		Collection<PyPolicyConfig> policyConfig = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CONFIG");
		} catch (Exception e1) {
			return new ResponseEntity<Collection<PyPolicyConfig>>(policyConfig, HttpStatus.UNAUTHORIZED);
		}
		ConfigRequest configRequest = new ConfigRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		policyConfig = configRequest.run(pep, requestID, userNamePass[0], userNamePass[1]);
		configCounter.incrementAndGet();
		Config.getIntegrityMonitor().endTransaction();
		for(PyPolicyConfig pythonConfig: policyConfig){
			if(pythonConfig.getPolicyConfigMessage()!=null && pythonConfig.getPolicyConfigMessage().contains("PE300")){
				return new ResponseEntity<Collection<PyPolicyConfig>>(policyConfig, HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Collection<PyPolicyConfig>>(policyConfig, HttpStatus.OK);
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Gets the configuration from the PDP")
	@RequestMapping(value = "/listConfig", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Collection<String>> createListConfigRequest(@RequestBody ConfigRequestParameters pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding, @RequestHeader(value="X-ECOMP-RequestID", required=false)String requestID) {
		Collection<String> policyList = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CONFIG");
		} catch (Exception e1) {
			return new ResponseEntity<Collection<String>>(policyList, HttpStatus.UNAUTHORIZED);
		}
		ListConfigRequest listConfigRequest = new ListConfigRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (StandbyStatusException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		policyList = listConfigRequest.run(pep, requestID, userNamePass[0], userNamePass[1]);
		
		configCounter.incrementAndGet();
		Config.getIntegrityMonitor().endTransaction();
		
		for(String response : policyList){
			if(response!=null && response.contains("PE300")){
				return new ResponseEntity<Collection<String>>(policyList, HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Collection<String>>(policyList, HttpStatus.OK);
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Sends the Events specified to the Policy Engine")
	@RequestMapping(value = "/sendEvent", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Collection<PolicyResponse>> createEventParameterRequest(@RequestBody EventRequestParameters pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding, @RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		Collection<PolicyResponse> policyResponse = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "ACTION");
		} catch (Exception e1) {
			return new ResponseEntity<Collection<PolicyResponse>>(policyResponse, HttpStatus.UNAUTHORIZED);
		}
		EventRequest eventRequest = new EventRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		policyResponse = eventRequest.run(pep, requestID, userNamePass[0], userNamePass[1]);
		eventCounter.incrementAndGet();
		Config.getIntegrityMonitor().endTransaction();
		for(PolicyResponse response: policyResponse ){
			if(response.getPolicyResponseMessage()!=null && response.getPolicyResponseMessage().contains("PE300")){
				return new ResponseEntity<Collection<PolicyResponse>>(policyResponse,HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Collection<PolicyResponse>>(policyResponse,HttpStatus.OK);
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Gets the configuration from the PolicyDecisionPoint(PDP)")
	@RequestMapping(value = "/getConfigByPolicyName", method = RequestMethod.POST)
	@Deprecated
	public @ResponseBody ResponseEntity<Collection<PyPolicyConfig>> createConfigRequest(@RequestBody PepConfigPolicyNameRequest pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding, @RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		Collection<PyPolicyConfig> policyConfig = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CONFIG");
		} catch (Exception e1) {
			return new ResponseEntity<Collection<PyPolicyConfig>>(policyConfig, HttpStatus.UNAUTHORIZED);
		}
		ConfigRequest configRequest = new ConfigRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		policyConfig = configRequest.run(pep, requestID, userNamePass[0], userNamePass[1]);
		configPolicyNameCounter.incrementAndGet();
		Config.getIntegrityMonitor().endTransaction();
		return new ResponseEntity<Collection<PyPolicyConfig>>(policyConfig, HttpStatus.OK);
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value="Pushes the specified policy to the PDP Group.")
	@RequestMapping(value = "/pushPolicy", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> pushPolicyRequest(@RequestBody PepPushPolicyRequest pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}
		PushPolicyRequest pushPolicy = new PushPolicyRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = pushPolicy.run(pep, requestID, userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response.contains("BAD REQUEST")||response.contains("PE300")) {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		}
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value="Deletes the specified policy from the PDP Group or PAP.")
	@RequestMapping(value = "/deletePolicy", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> deletePolicyRequest(@RequestBody DeletePolicyParameters pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "DELETEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}
		DeletePolicyRequest deletePolicy = new DeletePolicyRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = deletePolicy.run(pep, requestID, userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response.contains("BAD REQUEST")||response.contains("PE300")||response.contains("not exist")||response.contains("Invalid policyName")) {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		} else if (response.contains("locked down")){
			return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
		} else if (response.contains("not Authorized")) {
			return new ResponseEntity<String>(response, HttpStatus.FORBIDDEN);
		} else if (response.contains("groupId")) {
			return new ResponseEntity<String>(response, HttpStatus.NOT_FOUND);
		} else if (response.contains("JPAUtils")||response.contains("database")||response.contains("policy file")||
				response.contains("unknown")||response.contains("configuration")) {
			return new ResponseEntity<String>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		}
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Creates a Policy based on given Policy Parameters.")
	@RequestMapping(value = "/createPolicy", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> createRequest(@RequestBody PolicyParameters pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false)String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}
		PolicyCreateUpdateRequest policyCreateUpdateRequest = new PolicyCreateUpdateRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = policyCreateUpdateRequest.run(pep, requestID, "create", userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if(response== null || response.contains("BAD REQUEST")||response.contains("PE300")){
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		}
		else if (response.contains("Policy Exist Error")) {
			return new ResponseEntity<String>(response, HttpStatus.CONFLICT);
		} else if (response.contains("PE200")){
			return new ResponseEntity<String>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		}
		
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Updates a Policy based on given Policy Parameters.")
	@RequestMapping(value = "/updatePolicy", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateRequest(@RequestBody PolicyParameters pep,@RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}
		PolicyCreateUpdateRequest policyCreateUpdateRequest = new PolicyCreateUpdateRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = policyCreateUpdateRequest.run(pep, requestID, "update", userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response==null|| response.contains("BAD REQUEST")||response.contains("PE300")){
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		} else if (response.contains("PE200")){
			return new ResponseEntity<String>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		}
			
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Creates a Config Policy based on given Policy Parameters.")
	@RequestMapping(value = "/createConfig", method = RequestMethod.PUT)
	@Deprecated
	public @ResponseBody ResponseEntity<String> createConfigRequest(@RequestBody PepConfigPolicyRequest pep, @RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}
		PolicyCreateUpdateRequest policyCreateUpdateRequest = new PolicyCreateUpdateRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = policyCreateUpdateRequest.run(pep, requestID, "create", userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response!=null && !response.contains("BAD REQUEST")) {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Updates a Config Policy based on given Policy Parameters.")
	@RequestMapping(value = "/updateConfig", method = RequestMethod.PUT)
	@Deprecated
	public @ResponseBody ResponseEntity<String> updateConfigRequest(@RequestBody PepConfigPolicyRequest pep, @RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}
		PolicyCreateUpdateRequest policyCreateUpdateRequest = new PolicyCreateUpdateRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = policyCreateUpdateRequest.run(pep, requestID, "update", userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response!=null && !response.contains("BAD REQUEST")) {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		}
			
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value = "Creates a Config Firewall Policy")
	@RequestMapping(value = "/createFirewallConfig", method = RequestMethod.PUT)
	@Deprecated
	public @ResponseBody ResponseEntity<String> createFirewallConfigRequest(@RequestBody PepConfigFirewallPolicyRequest pep, @RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}											
		ConfigFirewallPolicyRequest firewallPolicyRequest = new ConfigFirewallPolicyRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = firewallPolicyRequest.run(pep, requestID, "create", userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response!=null && !response.contains("BAD REQUEST")) {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header"),
		@ApiImplicitParam(name ="Environment", required = true, paramType = "Header")
	})
	@ApiOperation(value = "Updates a Config Firewall Policy")
	@RequestMapping(value = "/updateFirewallConfig", method = RequestMethod.PUT)
	@Deprecated
	public @ResponseBody ResponseEntity<String> updateFirewallConfigRequest(@RequestBody PepConfigFirewallPolicyRequest pep, @RequestHeader(value="ClientAuth", required=true)String clientEncoding,
															@RequestHeader(value="X-ECOMP-RequestID", required=false) String requestID) {
		String response = null;
		String[] userNamePass =  null;
		try {
			userNamePass = decodeEncoding(clientEncoding, "CREATEPOLICY");
		} catch (Exception e1) {
			return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
		}																	
		ConfigFirewallPolicyRequest firewallPolicyRequest = new ConfigFirewallPolicyRequest(policyEngine);
		try{
			Config.getIntegrityMonitor().startTransaction();
		} catch (AdministrativeStateException e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			PolicyLogger.error("Error while starting Transaction " + e);
		}
		response = firewallPolicyRequest.run(pep, requestID, "update", userNamePass[0], userNamePass[1]);
		
		Config.getIntegrityMonitor().endTransaction();
		if (response!=null && !response.contains("BAD REQUEST")) {
			return new ResponseEntity<String>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header")
	})
	@ApiOperation(value= "Gets the API Services usage Information")
	@ApiIgnore
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public String getCount() {
		return "Total Config Calls : " + configCounter + "\n"
				+"Total Config calls made using Policy File Name: " + configPolicyNameCounter + "\n"
				+ "Total Event Calls : " + eventCounter;
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header")
	})
	@ApiOperation(value = "Gets the PDPs that are listed to provide service.")
	@RequestMapping(value = "/pdps", method = RequestMethod.GET)
	public List<String> listPDPs() {
		return Config.getPDPs();
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name ="Authorization", required = true, paramType = "Header")
	})
	@ApiOperation(value = "Gets the PAPs that are listed to provide service.")
	@RequestMapping(value = "/paps", method = RequestMethod.GET)
	public List<String> listPAPs() {
		return Config.getPAPs();
	}
	
	/*
	 * Internal Decoding System. to support old and new Calls. 
	 */
	private String[] decodeEncoding(String clientEncoding, String scope) throws Exception{
		String[] userNamePass = PolicyUtils.decodeBasicEncoding(clientEncoding);
		if(userNamePass==null){
			if(AuthenticationService.clientAuth(clientEncoding)){
				if(AuthenticationService.checkClientScope(clientEncoding, scope)){
					String usernameAndPassword = null;
					byte[] decodedBytes = Base64.getDecoder().decode(clientEncoding);
					usernameAndPassword = new String(decodedBytes, "UTF-8");
					StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
					String username = tokenizer.nextToken();
					String password = tokenizer.nextToken();
					userNamePass=  new String[]{username, password};
				}
			}
		}
		if(userNamePass==null){
			throw new Exception("Client is Not authrorized to make this call. Please contact PyPDP Admin.");
		}
		return userNamePass;
	}
}