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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.dom4j.util.XMLErrorHandler;
import org.json.JSONObject;
import org.openecomp.policy.rest.adapter.ClosedLoopFaultBody;
import org.openecomp.policy.rest.adapter.ClosedLoopPMBody;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.SafePolicyWarning;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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

	public static final String CONFIG_POLICY = "Config";
	public static final String ACTION_POLICY = "Action";
	public static final String DECISION_POLICY = "Decision";
	public static final String CLOSEDLOOP_POLICY = "ClosedLoop_Fault";
	public static final String CLOSEDLOOP_PM = "ClosedLoop_PM";
	public static final String ENFORCER_CONFIG_POLICY= "Enforcer Config";
	public static final String MICROSERVICES="Micro Service";
	private Pattern pattern;
	private Matcher matcher;

	private static Map<String, String> rangeMap = new HashMap<String,String>();
	private static Map<String, String> mapAttribute = new HashMap<String,String>();

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Autowired
	CommonClassDao commonClassDao;

	@RequestMapping(value={"/policyController/validate_policy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView validatePolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			boolean valid = true;
			String responseString = "";
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyRestAdapter policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").toString(), PolicyRestAdapter.class);
			if(policyData.getPolicyName() != null){
				String policyNameValidate = emptyValidator(policyData.getPolicyName());
				if(!policyNameValidate.contains("success")){
					responseString = responseString + "PolicyName:" +  policyNameValidate + "<br>";
					valid = false;
				};
			}else{
				responseString = responseString + "PolicyName: PolicyName Should not be empty" + "<br>";
				valid = false;
			}
			if(policyData.getPolicyDescription() != null){
				String descriptionValidate = descriptionValidator(policyData.getPolicyDescription());
				if(!descriptionValidate.contains("success")){
					responseString = responseString + "Description:" +  descriptionValidate + "<br>";
					valid = false;
				}	
			}

			if(policyData.getPolicyType().equals(CONFIG_POLICY)){
				if (policyData.getConfigPolicyType().equals("Base") || policyData.getConfigPolicyType().equals(CLOSEDLOOP_POLICY) 
						||  policyData.getConfigPolicyType().equals(CLOSEDLOOP_PM) || policyData.getConfigPolicyType().equals(ENFORCER_CONFIG_POLICY) || policyData.getConfigPolicyType().equals(MICROSERVICES)) {
					if(policyData.getEcompName() != null){
						String ecompNameValidate = emptyValidator(policyData.getEcompName());
						if(!ecompNameValidate.contains("success")){
							responseString = responseString + "EcompName:" +  ecompNameValidate + "<br>";
							valid = false;
						}
					}else{
						responseString = responseString + "Ecomp Name: Ecomp Name Should not be empty" + "<br>";
						valid = false;
					}
				}

				if(policyData.getRiskType() != null){
					String riskTypeValidate = emptyValidator(policyData.getRiskType());
					if(!riskTypeValidate.contains("success")){
						responseString = responseString + "RiskType:" +  riskTypeValidate + "<br>";
						valid = false;
					}
				}else {
					responseString = responseString + "Risk Type: Risk Type Should not be Empty" + "<br>";
					valid = false;
				}

				if(policyData.getRiskLevel() != null){
					String validateRiskLevel = emptyValidator(policyData.getRiskLevel());
					if(!validateRiskLevel.contains("success")){
						responseString = responseString + "RiskLevel:" +  validateRiskLevel + "<br>";
						valid = false;
					}
				}else {
					responseString = responseString + "Risk Level: Risk Level Should not be Empty" + "<br>";
					valid = false;
				}

				if(policyData.getGuard() != null){
					String validateGuard = emptyValidator(policyData.getGuard());
					if(!validateGuard.contains("success")){
						responseString = responseString + "Guard:" +  validateGuard + "<br>";
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
							responseString = responseString + "ConfigName:" +  configNameValidate + "<br>";
							valid = false;
						}
					}else{
						responseString = responseString + "Config Name: Config Name Should not be Empty" + "<br>";
						valid = false;
					}
					if(policyData.getConfigType() != null){
						String configTypeValidate = emptyValidator(policyData.getConfigType());
						if(!configTypeValidate.contains("success")){
							responseString = responseString + "ConfigType:" +  configTypeValidate + "<br>";
							valid = false;
						}
					}else{
						responseString = responseString + "Config Type: Config Type Should not be Empty" + "<br>";
						valid = false;
					}
					if(policyData.getConfigBodyData() != null){
						String configBodyData = policyData.getConfigBodyData();
						String policyType = policyData.getConfigType();
						if (policyType != null) {
							if (policyType.equals("JSON")) {
								if (!isJSONValid(configBodyData)) {
									responseString = responseString + "Config Body: JSON Content is not valid" + "<br>";
									valid = false;
								}
							} else if (policyType.equals("XML")) {
								if (!isXMLValid(configBodyData)) {
									responseString = responseString + "Config Body: XML Content data is not valid" + "<br>";
									valid = false;
								}
							} else if (policyType.equals("PROPERTIES")) {
								if (!isPropValid(configBodyData)||configBodyData.equals("")) {
									responseString = responseString + "Config Body: Property data is not valid" + "<br>";
									valid = false;
								} 
							} else if (policyType.equals("OTHER")) {
								if (configBodyData.equals("")) {
									responseString = responseString + "Config Body: Config Body Should not be Empty" + "<br>";
									valid = false;
								}
							}
						}
					}else{
						responseString = responseString + "Config Body: Config Body Should not be Empty" + "<br>";
						valid = false;
					}
				}

				if(policyData.getConfigPolicyType().equals("Firewall Config")){
					if(policyData.getConfigName() != null){
						String configNameValidate = PolicyUtils.emptyPolicyValidator(policyData.getConfigName());
						if(!configNameValidate.contains("success")){
							responseString = responseString + "<b>ConfigName</b>:<i>" +  configNameValidate + "</i><br>";
							valid = false;
						}
					}else{
						responseString = responseString + "<b>Config Name</b>:<i> Config Name is required" + "</i><br>";
						valid = false;
					}
					if(policyData.getSecurityZone() == null){
						responseString = responseString + "<b>Security Zone</b>:<i> Security Zone is required" + "</i><br>";
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("BRMS_Param")){
					if(policyData.getRuleName() == null){
						responseString = responseString + "<b>BRMS Template</b>:<i>BRMS Template is required</i><br>";
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("BRMS_Raw")){
					if(policyData.getConfigBodyData() != null){
						String message = PolicyUtils.brmsRawValidate(policyData.getConfigBodyData());
						// If there are any error other than Annotations then this is not Valid
						if(message.contains("[ERR")){
							responseString = responseString + "<b>Raw Rule Validate</b>:<i>Raw Rule has error"+ message +"</i><br>";
							valid = false;
						}
					}else{
						responseString = responseString + "<b>Raw Rule</b>:<i>Raw Rule is required</i><br>";
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("ClosedLoop_PM")){
					try{
						if(root.get("policyData").get("verticaMetrics").get("serviceTypePolicyName") == null && policyData.getServiceTypePolicyName().isEmpty()){
							responseString = responseString + "<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required</i><br>";
							valid = false; 
						}
					}catch(Exception e){
						responseString = responseString + "<b>ServiceType PolicyName</b>:<i>ServiceType PolicyName is required</i><br>";
						valid = false;
					}

					if(root.get("policyData").get("jsonBodyData") != null){
						ClosedLoopPMBody pmBody = (ClosedLoopPMBody)mapper.readValue(root.get("policyData").get("jsonBodyData").toString(), ClosedLoopPMBody.class);
						if(pmBody.getEmailAddress() != null){
							String result = emailValidation(pmBody.getEmailAddress(), responseString);
							if(result != "success"){
								responseString = result + "<br>";
								valid = false;
							}
						}
						if(pmBody.getGeoLink() != null){
							String result = PolicyUtils.emptyPolicyValidator(pmBody.getGeoLink());
							if(!result.contains("success")){
								responseString = responseString + "<b>GeoLink</b>:<i>" +  result + "</i><br>";
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
										responseString = responseString + "<b>Attributes</b>:<i>" +  key + " : value has spaces</i><br>";
										valid = false;
									};
								}
							}	
						}
					}else{
						responseString = responseString + "<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services</i><br>";
						valid = false;
					}
				}
				if(policyData.getConfigPolicyType().equals("ClosedLoop_Fault")){
					if(root.get("policyData").get("jsonBodyData") != null){
						ClosedLoopFaultBody faultBody = (ClosedLoopFaultBody)mapper.readValue(root.get("policyData").get("jsonBodyData").toString(), ClosedLoopFaultBody.class);
						if(faultBody.getEmailAddress() != null){
							String result = emailValidation(faultBody.getEmailAddress(), responseString);
							if(result != "success"){
								responseString = result+ "<br>";
								valid = false;
							}
						}
						if((faultBody.isGama() || faultBody.isMcr() || faultBody.isTrinity() || faultBody.isvDNS() || faultBody.isvUSP()) != true){
							responseString = responseString + "<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services</i><br>";
							valid = false; 
						}
						if(faultBody.getActions() == null){
							responseString = responseString + "<b>vPRO Actions</b>:<i>vPRO Actions is required</i><br>";
							valid = false;
						}
						if(faultBody.getAgingWindow() == 0){
							responseString = responseString + "<b>Aging Window</b>:<i>Aging Window is required</i><br>";
							valid = false;
						}
						if(faultBody.getClosedLoopPolicyStatus() == null){
							responseString = responseString + "<b>Policy Status</b>:<i>Policy Status is required</i><br>";
							valid = false;
						}
						if(faultBody.getConditions() == null){
							responseString = responseString + "<b>Conditions</b>:<i>Select Atleast one Condition</i><br>";
							valid = false;
						}
						if(faultBody.getGeoLink() != null){
							String result = PolicyUtils.emptyPolicyValidator(faultBody.getGeoLink());
							if(!result.contains("success")){
								responseString = responseString + "<b>GeoLink</b>:<i>" +  result + "</i><br>";
								valid = false;
							};
						}

						if(faultBody.getTimeInterval() == 0){
							responseString = responseString + "<b>Time Interval</b>:<i>Time Interval is required</i><br>";
							valid = false;
						}
						if(faultBody.getRetrys() == 0){
							responseString = responseString + "<b>Number of Retries</b>:<i>Number of Retries is required</i><br>";
							valid = false;
						}
						if(faultBody.getTimeOutvPRO() == 0){
							responseString = responseString + "<b>APP-C Timeout</b>:<i>APP-C Timeout is required</i><br>";
							valid = false;
						}
						if(faultBody.getTimeOutRuby() == 0){
							responseString = responseString + "<b>TimeOutRuby</b>:<i>TimeOutRuby is required</i><br>";
							valid = false;
						}
						if(faultBody.getVnfType() == null){
							responseString = responseString + "<b>Vnf Type</b>:<i>Vnf Type is required</i><br>";
							valid = false;
						}
					}else{
						responseString = responseString + "<b>D2/Virtualized Services</b>:<i>Select atleast one D2/Virtualized Services</i><br>";
						responseString = responseString + "<b>vPRO Actions</b>:<i>vPRO Actions is required</i><br>";
						responseString = responseString + "<b>Aging Window</b>:<i>Aging Window is required</i><br>";
						responseString = responseString + "<b>Policy Status</b>:<i>Policy Status is required</i><br>";
						responseString = responseString + "<b>Conditions</b>:<i>Select Atleast one Condition</i><br>";
						responseString = responseString + "<b>PEP Name</b>:<i>PEP Name is required</i><br>";
						responseString = responseString + "<b>PEP Action</b>:<i>PEP Action is required</i><br>";
						responseString = responseString + "<b>Time Interval</b>:<i>Time Interval is required</i><br>";
						responseString = responseString + "<b>Number of Retries</b>:<i>Number of Retries is required</i><br>";
						responseString = responseString + "<b>APP-C Timeout</b>:<i>APP-C Timeout is required</i><br>";
						responseString = responseString + "<b>TimeOutRuby</b>:<i>TimeOutRuby is required</i><br>";
						responseString = responseString + "<b>Vnf Type</b>:<i>Vnf Type is required</i><br>";
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
							rangeMap = new HashMap<String,String>();
							rangeMap = Splitter.on(",").withKeyValueSeparator("=").split(annoation);
							for (Entry<String, String> rMap : rangeMap.entrySet()){
								if (rMap.getValue().contains("range::")){
									String value = mapAttribute.get(rMap.getKey().trim());
									String[] tempString = rMap.getValue().split("::")[1].split("-");
									int startNum = Integer.parseInt(tempString[0]);
									int endNum = Integer.parseInt(tempString[1]);
									String returnString = "Invalid Range:" + rMap.getKey() + " must be between " 
											+ startNum + " - "  + endNum + ",";
									if (isType(value.replace("\"", ""))){
										int result = Integer.parseInt(value.replace("\"", ""));


										if (result < startNum || result > endNum){
											responseString = responseString + returnString;									
											valid = false;
										}
									}else {
										responseString = responseString + returnString;
										valid = false;
									}
								}
							}
						}
						//for continue testing for Dkat, just blocked this validation until fixing it. gw1218 on 3/30/17
						//if (!checkAttributeValues()){
						//responseString = responseString + "<b>Micro Service</b>:<i>  Attribute Values Missing" + "</i><br>";
						//valid = false;
						//} 

					}else{
						responseString = responseString + "<b>Micro Service</b>:<i> Micro Service is required" + "</i><br>";
						valid = false;
					}

					if(policyData.getPriority() == null){
						responseString = responseString + "<b>Priority</b>:<i> Priority is required" + "</i><br>";
						valid = false;
					}
				}	
			}
			if (policyData.getPolicyType().equals(DECISION_POLICY)){
				if(policyData.getEcompName() != null){
					String ecompNameValidate = emptyValidator(policyData.getEcompName());
					if(!ecompNameValidate.contains("success")){
						responseString = responseString + "EcompName:" +  ecompNameValidate + "<br>";
						valid = false;
					}
				}else{
					responseString = responseString + "Ecomp Name: Ecomp Name Should not be empty" + "<br>";
					valid = false;
				}

			}

			if(policyData.getPolicyType().equals(ACTION_POLICY)){
				if(policyData.getActionPerformer() != null){
					String actionPerformer = emptyValidator(policyData.getActionPerformer());
					if(!actionPerformer.contains("success")){
						responseString = responseString + "ActionPerformer:" +  actionPerformer + "<br>";
						valid = false;
					};
				}else{
					responseString = responseString + "ActionPerformer: ActionPerformer Should not be empty" + "<br>";
					valid = false;
				}
				if(policyData.getActionAttributeValue() != null){
					String actionAttribute = emptyValidator(policyData.getActionAttributeValue());
					if(!actionAttribute.contains("success")){
						responseString = responseString + "ActionAttribute:" +  actionAttribute + "<br>";
						valid = false;
					};
				}else{
					responseString = responseString + "ActionAttribute: ActionAttribute Should not be empty" + "<br>";
					valid = false;
				}			
			}

			if(policyData.getPolicyType().equals(CONFIG_POLICY)){
				if(valid){
					List<Object> spData = commonClassDao.getDataById(SafePolicyWarning.class, "riskType", policyData.getRiskType());
					if (!spData.isEmpty()){
						SafePolicyWarning safePolicyWarningData  = (SafePolicyWarning) spData.get(0);
						safePolicyWarningData.getMessage();
						responseString = responseString + "Messaage:" +  safePolicyWarningData.getMessage();
					}
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

	protected String emailValidation(String email, String responseString){
		if(email != null){
			String validateEmail = PolicyUtils.validateEmailAddress(email.replace("\"", ""));
			if(!validateEmail.contains("success")){
				responseString = responseString + "<b>Email</b>:<i>" +  validateEmail+ "</i><br>";
			}else{
				return "success";
			}
		}
		return responseString;
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

	private Boolean isType(String testStr) {
		try {
			Integer.parseInt(testStr);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	// Validation for json.
	protected static boolean isJSONValid(String data) {
		try {
			new JSONObject(data);
			InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
			JsonReader jsonReader = Json.createReader(stream);
			System.out.println("Json Value is: " + jsonReader.read().toString() );
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
		} catch (ParserConfigurationException e) {
			return false;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	// Validation for Properties file.
	public boolean isPropValid(String prop) {
		Scanner scanner = new Scanner(prop);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line.replaceAll("\\s+", "");
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
