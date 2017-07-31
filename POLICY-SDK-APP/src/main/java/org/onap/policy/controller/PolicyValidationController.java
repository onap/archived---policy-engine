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

package org.onap.policy.controller;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.dom4j.util.XMLErrorHandler;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultBody;
import org.onap.policy.rest.adapter.ClosedLoopPMBody;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.SafePolicyWarning;
import org.onap.policy.utils.PolicyUtils;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

@Controller
@RequestMapping("/")
public class PolicyValidationController extends RestrictedBaseController {

	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyValidationController.class);
	
	public static final String CONFIG_POLICY = "Config";
	public static final String ACTION_POLICY = "Action";
	public static final String DECISION_POLICY = "Decision";
	public static final String CLOSEDLOOP_POLICY = "ClosedLoop_Fault";
	public static final String CLOSEDLOOP_PM = "ClosedLoop_PM";
	public static final String ENFORCER_CONFIG_POLICY= "Enforcer Config";
	public static final String MICROSERVICES="Micro Service";
	private Pattern pattern;
	private Matcher matcher;
	private static Map<String, String> mapAttribute = new HashMap<>();

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Autowired
	CommonClassDao commonClassDao;

	@RequestMapping(value={"/policyController/validate_policy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView validatePolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			boolean valid = true;
			StringBuilder responseString = new StringBuilder();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyRestAdapter policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").toString(), PolicyRestAdapter.class);
			if(policyData.getPolicyName() != null){
				String policyNameValidate = emptyValidator(policyData.getPolicyName());
				if(!policyNameValidate.contains("success")){
					responseString.append("PolicyName:" +  policyNameValidate + "<br>");
					valid = false;
				};
			}else{
				responseString.append( "PolicyName: PolicyName Should not be empty" + "<br>");
				valid = false;
			}
			if(policyData.getPolicyDescription() != null){
				String descriptionValidate = descriptionValidator(policyData.getPolicyDescription());
				if(!descriptionValidate.contains("success")){
					responseString.append("Description:" +  descriptionValidate + "<br>");
					valid = false;
				}	
			}

			if(policyData.getPolicyType().equals(CONFIG_POLICY)){
				if (policyData.getConfigPolicyType().equals("Base") || policyData.getConfigPolicyType().equals(CLOSEDLOOP_POLICY) 
						||  policyData.getConfigPolicyType().equals(CLOSEDLOOP_PM) || policyData.getConfigPolicyType().equals(ENFORCER_CONFIG_POLICY) || policyData.getConfigPolicyType().equals(MICROSERVICES)) {
					if(policyData.getOnapName() != null){
						String onapNameValidate = emptyValidator(policyData.getOnapName());
						if(!onapNameValidate.contains("success")){
							responseString.append("OnapName:" +  onapNameValidate + "<br>");
							valid = false;
						}
					}else{
						responseString.append("Onap Name: Onap Name Should not be empty" + "<br>");
						valid = false;
					}
				}

				if(policyData.getRiskType() != null){
					String riskTypeValidate = emptyValidator(policyData.getRiskType());
					if(!riskTypeValidate.contains("success")){
						responseString.append("RiskType:" +  riskTypeValidate + "<br>");
						valid = false;
					}
				}else {
					responseString.append("Risk Type: Risk Type Should not be Empty" + "<br>");
					valid = false;
				}

				if(policyData.getRiskLevel() != null){
					String validateRiskLevel = emptyValidator(policyData.getRiskLevel());
					if(!validateRiskLevel.contains("success")){
						responseString.append("RiskLevel:" +  validateRiskLevel + "<br>");
						valid = false;
					}
				}else {
					responseString.append("Risk Level: Risk Level Should not be Empty" + "<br>");
					valid = false;
				}

				if(policyData.getGuard() != null){
					String validateGuard = emptyValidator(policyData.getGuard());
					if(!validateGuard.contains("success")){
						responseString.append("Guard:" +  validateGuard + "<br>");
						valid = false;
					}
				}else {
					responseString.append("Guard: Guard Value Should not be Empty" + "<br>");
					valid = false;
				}

				if(policyData.getConfigPolicyType().equals("Base")){
					if(policyData.getConfigName() != null){
						String configNameValidate = emptyValidator(policyData.getConfigName());
						if(!configNameValidate.contains("success")){
							responseString.append("ConfigName:" +  configNameValidate + "<br>");
							valid = false;
						}
					}else{
						responseString.append("Config Name: Config Name Should not be Empty" + "<br>");
						valid = false;
					}
					if(policyData.getConfigType() != null){
						String configTypeValidate = emptyValidator(policyData.getConfigType());
						if(!configTypeValidate.contains("success")){
							responseString.append("ConfigType:" +  configTypeValidate + "<br>");
							valid = false;
						}
					}else{
						responseString.append("Config Type: Config Type Should not be Empty" + "<br>");
						valid = false;
					}
					if(policyData.getConfigBodyData() != null){
						String configBodyData = policyData.getConfigBodyData();
						String policyType = policyData.getConfigType();
						if (policyType != null) {
							if (policyType.equals("JSON")) {
								if (!isJSONValid(configBodyData)) {
									responseString.append("Config Body: JSON Content is not valid" + "<br>");
									valid = false;
								}
							} else if (policyType.equals("XML")) {
								if (!isXMLValid(configBodyData)) {
									responseString.append("Config Body: XML Content data is not valid" + "<br>");
									valid = false;
								}
							} else if (policyType.equals("PROPERTIES")) {
								if (!isPropValid(configBodyData)||configBodyData.equals("")) {
									responseString.append("Config Body: Property data is not valid" + "<br>");
									valid = false;
								} 
							} else if (policyType.equals("OTHER")) {
								if (configBodyData.equals("")) {
									responseString.append("Config Body: Config Body Should not be Empty" + "<br>");
									valid = false;
								}
							}
						}
					}else{
						responseString.append("Config Body: Config Body Should not be Empty" + "<br>");
						valid = false;
					}
				}

				if(policyData.getConfigPolicyType().equals("Firewall Config")){
					if(policyData.getConfigName() != null){
						String configNameValidate = PolicyUtils.emptyPolicyValidator(policyData.getConfigName());
						if(!configNameValidate.contains("success")){
							responseString.append("<b>ConfigName</b>:<i>" +  configNameValidate + "</i><br>");
							valid = false;
						}
					}else{
						responseString.append("<b>Config Name</b>:<i> Config Name is required" + "</i><br>");
						valid = false;
					}
					if(policyData.getSecurityZone() == null){
						responseString.append("<b>Security Zone</b>:<i> Security Zone is required" + "</i><br>");
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("BRMS_Param")){
					if(policyData.getRuleName() == null){
						responseString.append("<b>BRMS Template</b>:<i>BRMS Template is required</i><br>");
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("BRMS_Raw")){
					if(policyData.getConfigBodyData() != null){
						String message = PolicyUtils.brmsRawValidate(policyData.getConfigBodyData());
						// If there are any error other than Annotations then this is not Valid
						if(message.contains("[ERR")){
							responseString.append("<b>Raw Rule Validate</b>:<i>Raw Rule has error"+ message +"</i><br>");
							valid = false;
						}
					}else{
						responseString.append("<b>Raw Rule</b>:<i>Raw Rule is required</i><br>");
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("ClosedLoop_PM")){
					try{
						if(root.get("policyData").get("verticaMetrics").get("serviceTypePolicyName") == null && policyData.getServiceTypePolicyName().isEmpty()){
							responseString.append("<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required</i><br>");
							valid = false; 
						}
					}catch(Exception e){
						responseString.append("<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required</i><br>");
						valid = false;
					}

					if(root.get("policyData").get("jsonBodyData") != null){
						ClosedLoopPMBody pmBody = (ClosedLoopPMBody)mapper.readValue(root.get("policyData").get("jsonBodyData").toString(), ClosedLoopPMBody.class);
						if(pmBody.getEmailAddress() != null){
							String result = emailValidation(pmBody.getEmailAddress(), responseString.toString());
							if(result != "success"){
								responseString.append(result + "<br>");
								valid = false;
							}
						}
						if(pmBody.getGeoLink() != null){
							String result = PolicyUtils.emptyPolicyValidator(pmBody.getGeoLink());
							if(!result.contains("success")){
								responseString.append("<b>GeoLink</b>:<i>" +  result + "</i><br>");
								valid = false;
							};
						}
						if(pmBody.getAttributes() != null){
							for(Entry<String, String> entry : pmBody.getAttributes().entrySet()){
								String key = entry.getKey();
								String value = entry.getValue();
								if(!key.contains("Message")){
									String attributeValidate = PolicyUtils.emptyPolicyValidator(value);
									if(!attributeValidate.contains("success")){
										responseString.append("<b>Attributes</b>:<i>" +  key + " : value has spaces</i><br>");
										valid = false;
									};
								}
							}	
						}
					}else{
						responseString.append("<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services</i><br>");
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("ClosedLoop_Fault")){
					if(root.get("policyData").get("jsonBodyData") != null){
						ClosedLoopFaultBody faultBody = (ClosedLoopFaultBody)mapper.readValue(root.get("policyData").get("jsonBodyData").toString(), ClosedLoopFaultBody.class);
						if(faultBody.getEmailAddress() != null){
							String result = emailValidation(faultBody.getEmailAddress(), responseString.toString());
							if(result != "success"){
								responseString.append(result+ "<br>");
								valid = false;
							}
						}
						if((faultBody.isGama() || faultBody.isMcr() || faultBody.isTrinity() || faultBody.isvDNS() || faultBody.isvUSP()) != true){
							responseString.append("<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services</i><br>");
							valid = false; 
						}
						if(faultBody.getActions() == null){
							responseString.append("<b>vPRO Actions</b>:<i>vPRO Actions is required</i><br>");
							valid = false;
						}
						if(faultBody.getClosedLoopPolicyStatus() == null){
							responseString.append("<b>Policy Status</b>:<i>Policy Status is required</i><br>");
							valid = false;
						}
						if(faultBody.getConditions() == null){
							responseString.append("<b>Conditions</b>:<i>Select Atleast one Condition</i><br>");
							valid = false;
						}
						if(faultBody.getGeoLink() != null){
							String result = PolicyUtils.emptyPolicyValidatorWithSpaceAllowed(faultBody.getGeoLink());
							if(!result.contains("success")){
								responseString.append("<b>GeoLink</b>:<i>" +  result + "</i><br>");
								valid = false;
							};
						}

						if(faultBody.getTimeInterval() == 0){
							responseString.append("<b>Time Interval</b>:<i>Time Interval is required</i><br>");
							valid = false;
						}
						if(faultBody.getRetrys() == 0){
							responseString.append("<b>Number of Retries</b>:<i>Number of Retries is required</i><br>");
							valid = false;
						}
						if(faultBody.getTimeOutvPRO() == 0){
							responseString.append("<b>APP-C Timeout</b>:<i>APP-C Timeout is required</i><br>");
							valid = false;
						}
						if(faultBody.getTimeOutRuby() == 0){
							responseString.append("<b>TimeOutRuby</b>:<i>TimeOutRuby is required</i><br>");
							valid = false;
						}
						if(faultBody.getVnfType() == null){
							responseString.append("<b>Vnf Type</b>:<i>Vnf Type is required</i><br>");
							valid = false;
						}
					}else{
						responseString.append("<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services</i><br>");
						responseString.append("<b>vPRO Actions</b>:<i>vPRO Actions is required</i><br>");
						responseString.append("<b>Aging Window</b>:<i>Aging Window is required</i><br>");
						responseString.append("<b>Policy Status</b>:<i>Policy Status is required</i><br>");
						responseString.append("<b>Conditions</b>:<i>Select Atleast one Condition</i><br>");
						responseString.append("<b>PEP Name</b>:<i>PEP Name is required</i><br>");
						responseString.append("<b>PEP Action</b>:<i>PEP Action is required</i><br>");
						responseString.append("<b>Time Interval</b>:<i>Time Interval is required</i><br>");
						responseString.append("<b>Number of Retries</b>:<i>Number of Retries is required</i><br>");
						responseString.append("<b>APP-C Timeout</b>:<i>APP-C Timeout is required</i><br>");
						responseString.append("<b>TimeOutRuby</b>:<i>TimeOutRuby is required</i><br>");
						responseString.append("<b>Vnf Type</b>:<i>Vnf Type is required</i><br>");
						valid = false; 
					}
				}

				if (policyData.getConfigPolicyType().contains("Micro Service")){
					if(policyData.getServiceType() != null){
						pullJsonKeyPairs(root.get("policyJSON"));
						MicroServiceModels returnModel = new MicroServiceModels();
						String service = null;
						String version = null;
						if (policyData.getServiceType().contains("-v")){
							service = policyData.getServiceType().split("-v")[0];
							version = policyData.getServiceType().split("-v")[1];
						}else {
							service = policyData.getServiceType();
							version = policyData.getVersion();
						}
						returnModel = getAttributeObject(service, version);
						String annoation = returnModel.getAnnotation();
						if (!Strings.isNullOrEmpty(annoation)){
							 Map<String, String> rangeMap = new HashMap<>();
							rangeMap = Splitter.on(",").withKeyValueSeparator("=").split(annoation);
							for (Entry<String, String> rMap : rangeMap.entrySet()){
								if (rMap.getValue().contains("range::")){
									String value = mapAttribute.get(rMap.getKey().trim());
									String[] tempString = rMap.getValue().split("::")[1].split("-");
									int startNum = Integer.parseInt(tempString[0]);
									int endNum = Integer.parseInt(tempString[1]);
									String returnString = "Invalid Range:" + rMap.getKey() + " must be between " 
											+ startNum + " - "  + endNum + ",";
									if (PolicyUtils.isInteger(value.replace("\"", ""))){
										int result = Integer.parseInt(value.replace("\"", ""));
										if (result < startNum || result > endNum){
											responseString.append(returnString);									
											valid = false;
										}
									}else {
										responseString.append(returnString);
										valid = false;
									}
								}
							}
						}
					}else{
						responseString.append("<b>Micro Service</b>:<i> Micro Service is required" + "</i><br>");
						valid = false;
					}

					if(policyData.getPriority() == null){
						responseString.append("<b>Priority</b>:<i> Priority is required" + "</i><br>");
						valid = false;
					}
				}	
			}
			if (policyData.getPolicyType().equals(DECISION_POLICY)){
				if(policyData.getOnapName() != null){
					String onapNameValidate = emptyValidator(policyData.getOnapName());
					if(!onapNameValidate.contains("success")){
						responseString.append("OnapName:" +  onapNameValidate + "<br>");
						valid = false;
					}
				}else{
					responseString.append("Onap Name: Onap Name Should not be empty" + "<br>");
					valid = false;
				}
				
				if("Rainy_Day".equals(policyData.getRuleProvider())){
					if(policyData.getRainyday()==null){
						responseString.append("<b> Rainy Day Parameters are Required </b><br>");
						valid = false;
					}else{
						if(policyData.getRainyday().getServiceType()==null){
							responseString.append("Rainy Day <b>Service Type</b> is Required<br>");
							valid = false;
						}
						if(policyData.getRainyday().getVnfType()==null){
							responseString.append("Rainy Day <b>VNF Type</b> is Required<br>");
							valid = false;
						}						
						if(policyData.getRainyday().getBbid()==null){
							responseString.append("Rainy Day <b>Building Block ID</b> is Required<br>");
							valid = false;
						}
						if(policyData.getRainyday().getWorkstep()==null){
							responseString.append("Rainy Day <b>Work Step</b> is Required<br>");
							valid = false;
						}
						if(policyData.getRainyday().getServiceType()==null){
							responseString.append("Rainy Day <b>Error Code</b> is Required<br>");
							valid = false;
						}
					}
				}
				
				if("GUARD_YAML".equals(policyData.getRuleProvider()) || "GUARD_BL_YAML".equals(policyData.getRuleProvider())){
					if(policyData.getYamlparams()==null){
						responseString.append("<b> Guard Params are Required </b>" + "<br>");
						valid = false;
					}else{
						if(policyData.getYamlparams().getActor()==null){
							responseString.append("Guard Params <b>Actor</b> is Required " + "<br>");
							valid = false;
						}
						if(policyData.getYamlparams().getRecipe()==null){
							responseString.append("Guard Params <b>Recipe</b> is Required " + "<br>");
							valid = false;
						}
						if(policyData.getYamlparams().getGuardActiveStart()==null){
							responseString.append("Guard Params <b>Guard Active Start/b>is Required " + "<br>");
							valid = false;
						}
						if(policyData.getYamlparams().getGuardActiveEnd()==null){
							responseString.append("Guard Params <b>Guard Active End</b>is Required " + "<br>");
							valid = false;
						}
						if("GUARD_YAML".equals(policyData.getRuleProvider())){
							if(policyData.getYamlparams().getLimit()==null){
								responseString.append(" Guard Params <b>Limit</b> is Required " + "<br>");
								valid = false;
							}else if(!PolicyUtils.isInteger(policyData.getYamlparams().getLimit())){
								responseString.append(" Guard Params <b>Limit</b> Should be Integer " + "<br>");
								valid = false;
							}
							if(policyData.getYamlparams().getTimeWindow()==null){
								responseString.append("Guard Params <b>Time Window</b> is Required" + "<br>");
								valid = false;
							}else if(!PolicyUtils.isInteger(policyData.getYamlparams().getTimeWindow())){
								responseString.append(" Guard Params <b>Time Window</b> Should be Integer " + "<br>");
								valid = false;
							}
							if(policyData.getYamlparams().getTimeUnits()==null){
								responseString.append("Guard Params <b>Time Units</b> is Required" + "<br>");
								valid = false;
							}
						}else if("GUARD_BL_YAML".equals(policyData.getRuleProvider())){
							if(policyData.getYamlparams().getBlackList()==null || policyData.getYamlparams().getBlackList().isEmpty()){
								responseString.append(" Guard Params <b>BlackList</b> is Required " + "<br>");
								valid = false;
							}else{
								for(String blackList: policyData.getYamlparams().getBlackList()){
									if(blackList==null || !("success".equals(emptyValidator(blackList)))){
										responseString.append(" Guard Params <b>BlackList</b> Should be valid String" + "<br>");
										valid = false;
										break;
									}
								}
							}
						}
					}
				}
			}

			if(policyData.getPolicyType().equals(ACTION_POLICY)){
				if(policyData.getActionPerformer() != null){
					String actionPerformer = emptyValidator(policyData.getActionPerformer());
					if(!actionPerformer.contains("success")){
						responseString.append("ActionPerformer:" +  actionPerformer + "<br>");
						valid = false;
					};
				}else{
					responseString.append("ActionPerformer: ActionPerformer Should not be empty" + "<br>");
					valid = false;
				}
				if(policyData.getAttributes() != null){
					for(Object attribute : policyData.getAttributes()){
						if(attribute instanceof LinkedHashMap<?, ?>){
							try{
								//This is for validation check if the value exists or not
								String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
								String value =  ((LinkedHashMap<?, ?>) attribute).get("value").toString();
								if("".equals(key) || "".equals(value)){
									responseString.append("Component Attributes: One or more Fields in Component Attributes is Empty." + "<br>");
									valid = false;
									break;	
								}
							}catch(Exception e){
								LOGGER.error("This is a Policy Validation check" +e);
								responseString.append("Component Attributes: One or more Fields in Component Attributes is Empty." + "<br>");
								valid = false;
								break;
							}
						}
					}
				}else{
					responseString.append("Component Attributes: One or more Fields in Component Attributes is Empty." + "<br>");
					valid = false;
				}
				if(policyData.getActionAttributeValue() != null){
					String actionAttribute = emptyValidator(policyData.getActionAttributeValue());
					if(!actionAttribute.contains("success")){
						responseString.append("ActionAttribute:" +  actionAttribute + "<br>");
						valid = false;
					};
				}else{
					responseString.append("ActionAttribute: ActionAttribute Should not be empty" + "<br>");
					valid = false;
				}			
			}
			
			if(policyData.getPolicyType().equals(ACTION_POLICY) || policyData.getPolicyType().equals(DECISION_POLICY)){
				if(!policyData.getRuleAlgorithmschoices().isEmpty()){
					for(Object attribute : policyData.getRuleAlgorithmschoices()){
						if(attribute instanceof LinkedHashMap<?, ?>){
							try{
								String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
								String key = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
								String rule = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
								String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
								if("".equals(label) || "".equals(key) || "".equals(rule)  || "".equals(value)){
									responseString.append("Rule Algorithms: One or more Fields in Rule Algorithms is Empty." + "<br>");
									valid = false;
								}
							}catch(Exception e){
								LOGGER.error("This is a Policy Validation check" +e);
								responseString.append("Rule Algorithms: One or more Fields in Rule Algorithms is Empty." + "<br>");
								valid = false;
								break;
							}
						}
					}
				}
			}

			if(policyData.getPolicyType().equals(CONFIG_POLICY)){
				String value = "";
				if(valid){
					List<Object> spData = commonClassDao.getDataById(SafePolicyWarning.class, "riskType", policyData.getRiskType());
					if (!spData.isEmpty()){
						SafePolicyWarning safePolicyWarningData  = (SafePolicyWarning) spData.get(0);
						safePolicyWarningData.getMessage();
						value = "Message:" +  safePolicyWarningData.getMessage();
					}
					responseString.append("success" + "@#"+ value);
				}
			}else{
				if(valid){
					responseString.append("success");
				}
			}

			PrintWriter out = response.getWriter();
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(responseString.toString()));
			JSONObject j = new JSONObject(msg);
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error("Exception Occured while Policy Validation" +e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	protected String  emptyValidator(String field){
		String error;
		if ("".equals(field) || field.contains(" ") || !field.matches("^[a-zA-Z0-9_]*$")) {
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
		String error;
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

	protected String emailValidation(String email, String response){
		if(email != null){
			String validateEmail = PolicyUtils.validateEmailAddress(email.replace("\"", ""));
			if(!validateEmail.contains("success")){
				response += "<b>Email</b>:<i>" +  validateEmail+ "</i><br>";
			}else{
				return "success";
			}
		}
		return response;
	}

	private MicroServiceModels getAttributeObject(String name, String version) {	
		MicroServiceModels workingModel = new MicroServiceModels();
		List<Object> microServiceModelsData = commonClassDao.getDataById(MicroServiceModels.class, "modelName:version", name+":"+version);
		if(microServiceModelsData != null){
			workingModel = (MicroServiceModels) microServiceModelsData.get(0);
		}
		return workingModel;
	}

	private void pullJsonKeyPairs(JsonNode rootNode) {
		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();

		while (fieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> field = fieldsIterator.next();
			final String key = field.getKey();
			final JsonNode value = field.getValue();
			if (value.isContainerNode() && !value.isArray()) {
				pullJsonKeyPairs(value); // RECURSIVE CALL
			} else {
				if (value.isArray()){
					String newValue = StringUtils.replaceEach(value.toString(), new String[]{"[", "]", "\""}, new String[]{"", "", ""});
					mapAttribute.put(key, newValue);
				}else {
					mapAttribute.put(key, value.toString().trim());
				}
			}
		}
	}

	// Validation for json.
	protected static boolean isJSONValid(String data) {
		JsonReader jsonReader = null;
		try {
			new JSONObject(data);
			InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
			jsonReader = Json.createReader(stream);
			LOGGER.info("Json Value is: " + jsonReader.read().toString() );
		} catch (Exception e) {
			LOGGER.error("Exception Occured While Validating"+e);
			return false;
		}finally{
			if(jsonReader != null){
				jsonReader.close();
			}
		}
		return true;
	}

	// Validation for XML.
	private boolean isXMLValid(String data) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try {
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(new XMLErrorHandler());
			reader.parse(new InputSource(new StringReader(data)));
		} catch (Exception e) {
			LOGGER.error("Exception Occured While Validating"+e);
			return false;
		}
		return true;
	}

	// Validation for Properties file.
	public boolean isPropValid(String prop) {
		Scanner scanner = new Scanner(prop);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line = line.replaceAll("\\s+", "");
			if (line.startsWith("#")) {
				continue;
			} else {
				if (line.contains("=")) {
					String[] parts = line.split("=");
					if (parts.length < 2) {
						scanner.close();
						return false;
					}
				} else {
					scanner.close();
					return false;
				}
			}
		}
		scanner.close();
		return true;
	}

}