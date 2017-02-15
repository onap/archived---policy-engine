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

package org.openecomp.policy.controller;


import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.adapter.ClosedLoopFaultBody;
import org.openecomp.policy.adapter.ClosedLoopPMBody;
import org.openecomp.policy.adapter.PolicyAdapter;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.rest.dao.SafePolicyWarningDao;
import org.openecomp.policy.rest.jpa.SafePolicyWarning;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;

@Controller
@RequestMapping("/")
public class PolicyValidationController extends RestrictedBaseController {
	
	public static final String CONFIG_POLICY = "Config";
	public static final String ACTION_POLICY = "Action";
	public static final String DECISION_POLICY = "Decision";
	public static final String CLOSEDLOOP_POLICY = "ClosedLoop_Fault";
	public static final String CLOSEDLOOP_PM = "ClosedLoop_PM";
	public static final String ENFORCER_CONFIG_POLICY= "Enforcer Config";
	public static final String MICROSERVICES="DCAE Micro Service";
	private Pattern pattern;
	private Matcher matcher;

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@Autowired
	SafePolicyWarningDao safePolicyWarningDao;
	
	@RequestMapping(value={"/policyController/validate_policy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView validatePolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			boolean valid = true;
			String responseString = "";
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyAdapter policyData = (PolicyAdapter)mapper.readValue(root.get("policyData").toString(), PolicyAdapter.class);
			if(policyData.getPolicyName() != null){
				String policyNameValidate = emptyValidator(policyData.getPolicyName());
				if(!policyNameValidate.contains("success")){
					responseString = responseString + "PolicyName:" +  policyNameValidate;
					valid = false;
				};
			}else{
				responseString = responseString + "PolicyName: PolicyName Should not be empty" + "<br>";
			}
			if(policyData.getPolicyDescription() != null){
				String descriptionValidate = descriptionValidator(policyData.getPolicyDescription());
				if(!descriptionValidate.contains("success")){
					responseString = responseString + "Description:" +  descriptionValidate;
					valid = false;
				}	
			}
			
			if(policyData.getPolicyType().equals(CONFIG_POLICY)){
				if (policyData.getConfigPolicyType().equals("Base") || policyData.getConfigPolicyType().equals(CLOSEDLOOP_POLICY) 
						||  policyData.getConfigPolicyType().equals(CLOSEDLOOP_PM) || policyData.getConfigPolicyType().equals(ENFORCER_CONFIG_POLICY) || policyData.getConfigPolicyType().equals(MICROSERVICES)) {
					if(policyData.getEcompName() != null){
						String ecompNameValidate = emptyValidator(policyData.getEcompName());
						if(!ecompNameValidate.contains("success")){
							responseString = responseString + "EcompName:" +  ecompNameValidate;
							valid = false;
						}
					}else{
						responseString = responseString + "Ecomp Name: Ecomp Name Should not be empty" + "<br>";
					}
				}
				
				if(policyData.getRiskType() != null){
					String riskTypeValidate = emptyValidator(policyData.getRiskType());
					if(!riskTypeValidate.contains("success")){
						responseString = responseString + "RiskType:" +  riskTypeValidate;
						valid = false;
					}else {
						SafePolicyWarning safePolicyWarningData  = safePolicyWarningDao.getSafePolicyWarningDataById(policyData.getRiskType());
						if (safePolicyWarningData!=null){
							safePolicyWarningData.getMessage();
							responseString = responseString + "Messaage:" +  safePolicyWarningData.getMessage();
						}
					}
				}else {
					responseString = responseString + "Risk Type: Risk Type Should not be Empty" + "<br>";
					valid = false;
				}
				
				if(policyData.getRiskLevel() != null){
					String validateRiskLevel = emptyValidator(policyData.getRiskLevel());
					if(!validateRiskLevel.contains("success")){
						responseString = responseString + "RiskLevel:" +  validateRiskLevel;
						valid = false;
					}
				}else {
					responseString = responseString + "Risk Level: Risk Level Should not be Empty" + "<br>";
					valid = false;
				}
				
				if(policyData.getGuard() != null){
					String validateGuard = emptyValidator(policyData.getGuard());
					if(!validateGuard.contains("success")){
						responseString = responseString + "Guard:" +  validateGuard;
						valid = false;
					}
				}else {
					responseString = responseString + "Guard: Guard Value Should not be Empty" + "<br>";
					valid = false;
				}
				
				if(policyData.getConfigPolicyType().equals("Base")){
					if(policyData.getConfigName() != null){
						String configNameValidate = emptyValidator(policyData.getConfigName());
						if(!configNameValidate.contains("success")){
							responseString = responseString + "ConfigName:" +  configNameValidate;
							valid = false;
						}
					}else{
						responseString = responseString + "Config Name: Config Name Should not be Empty" + "<br>";
					}
					if(policyData.getConfigType() != null){
						String configTypeValidate = emptyValidator(policyData.getConfigType());
						if(!configTypeValidate.contains("success")){
							responseString = responseString + "ConfigType:" +  configTypeValidate;
							valid = false;
						}
					}else{
						responseString = responseString + "Config Type: Config Type Should not be Empty" + "<br>";
					}
					if(policyData.getConfigBodyData() != null){
						String policyType = policyData.getPolicyType();
						RESTfulPAPEngine engine = (RESTfulPAPEngine) PolicyController.getPapEngine();
						if(!engine.validatePolicyRequest(policyData, policyType)){
							responseString = responseString + "ConfigBody: Validation Failed";
							valid = false;
						}
					}else{
						responseString = responseString + "Config Body: Config Body Should not be Empty" + "<br>";
					}
				}
				
				
				if(policyData.getJsonBodyData() != null){
					if(policyData.getConfigPolicyType().equals("ClosedLoop_PM")){
						ClosedLoopPMBody pmBody = (ClosedLoopPMBody)mapper.readValue(root.get("policyData").get("jsonBodyData").toString(), ClosedLoopPMBody.class);
						if(pmBody.getEmailAddress() != null){
							String validateEmail = validateEmailAddress(pmBody.getEmailAddress().toString());
							if(!validateEmail.contains("success")){
								responseString = responseString + "Email:" +  validateEmail;
								valid = false;
							}
						}else{
							valid = true;
						}
					}else if(policyData.getConfigPolicyType().equals("ClosedLoop_Fault")){
						ClosedLoopFaultBody faultBody = (ClosedLoopFaultBody)mapper.readValue(root.get("policyData").get("jsonBodyData").toString(), ClosedLoopFaultBody.class);
						if(faultBody.getEmailAddress() != null){
							String validateEmail = validateEmailAddress(faultBody.getEmailAddress().toString());
							if(!validateEmail.contains("success")){
								responseString = responseString + "Email:" +  validateEmail;
								valid = false;
							}
						}else{
							valid = true;
						}
					}	
				}
			}
			if (policyData.getPolicyType().equals(DECISION_POLICY)){
				String ecompNameValidate = emptyValidator(policyData.getEcompName());
				if(!ecompNameValidate.contains("success")){
					responseString = responseString + "EcompName:" +  ecompNameValidate;
					valid = false;
				}
			}
			
			if(policyData.getPolicyType().equals(ACTION_POLICY)){
				String actionPerformer = emptyValidator(policyData.getActionPerformer());
				String actionAttribute = emptyValidator(policyData.getActionAttributeValue());
				if(!actionPerformer.contains("success")){
					responseString = responseString + "ActionPerformer:" +  actionPerformer;
					valid = false;
				};
				if(!actionAttribute.contains("success")){
					responseString = responseString + "ActionAttribute:" +  actionAttribute;
					valid = false;
				};		
			}
			
			if(policyData.getPolicyType().equals(CONFIG_POLICY)){
				if(valid){
					responseString = "success" + "@#"+ responseString;
				}
			}else{
				if(valid){
					responseString = "success";
				}
			}
			
			PrintWriter out = response.getWriter();
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(responseString));
			JSONObject j = new JSONObject(msg);
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	protected String  emptyValidator(String field){
		String error = "success";
		if (field.equals("") || field.contains(" ") || !field.matches("^[a-zA-Z0-9_]*$")) {
			error = "The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}, _' following set of Combinations";
			return error;
		} else {
			if(CharMatcher.ASCII.matchesAllOf((CharSequence) field)){
				 error = "success";
			}else{
				error = "The Value Contains Non ASCII Characters";
				return error;
			}	
		}
		return error;	
	}
	
	protected String descriptionValidator(String field) {
		String error = "success";
		if (field.contains("@CreatedBy:") || field.contains("@ModifiedBy:")) {
			 error = "The value in the description shouldn't contain @CreatedBy: or @ModifiedBy:";
			 return error;
		} else {
			error = "success";
		}
		return error;	
	}
	
	public String validateEmailAddress(String emailAddressValue) {
		String error = "success";
		List<String> emailList = Arrays.asList(emailAddressValue.toString().split(","));
		for(int i =0 ; i < emailList.size() ; i++){
			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(emailList.get(i).trim());
			if(!matcher.matches()){
				error = "Please check the Following Email Address is not Valid ....   " +emailList.get(i).toString();
				return error;
			}else{
				error = "success";
			}
		}
		return error;		
	}
	
}
