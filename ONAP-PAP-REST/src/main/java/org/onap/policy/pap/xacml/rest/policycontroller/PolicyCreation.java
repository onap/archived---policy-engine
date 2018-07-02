/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.policycontroller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.components.ActionPolicy;
import org.onap.policy.pap.xacml.rest.components.ClosedLoopPolicy;
import org.onap.policy.pap.xacml.rest.components.ConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.CreateBrmsParamPolicy;
import org.onap.policy.pap.xacml.rest.components.CreateBrmsRawPolicy;
import org.onap.policy.pap.xacml.rest.components.CreateClosedLoopPerformanceMetrics;
import org.onap.policy.pap.xacml.rest.components.DecisionPolicy;
import org.onap.policy.pap.xacml.rest.components.FirewallConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.MicroServiceConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.OptimizationConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.Policy;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.pap.xacml.rest.util.AbstractPolicyCreation;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/")
public class PolicyCreation extends AbstractPolicyCreation{

	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyCreation.class);

	private String ruleID = "";
	private PolicyDBDao policyDBDao;
	String CLName = null;
	
	private static CommonClassDao commonClassDao;
	
	public static CommonClassDao getCommonClassDao() {
		return commonClassDao;
	}

	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		PolicyCreation.commonClassDao = commonClassDao;
	}

	@Autowired
	public PolicyCreation(CommonClassDao commonClassDao){
		PolicyCreation.commonClassDao = commonClassDao;
	}

	public PolicyCreation(){}
	
	@RequestMapping(value="/policycreation/save_policy", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> savePolicy(@RequestBody PolicyRestAdapter policyData, HttpServletResponse response){
		String body = null;
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Map<String, String> successMap = new HashMap<>();
		Map<String, String> attributeMap = new HashMap<>();
		PolicyVersion policyVersionDao;
		try {
		
			Policy newPolicy = null;
			String policyConfigType = null;
			String userId = policyData.getUserId();

			if (policyData.getTtlDate()==null){
				policyData.setTtlDate("NA");
			}else{
				String dateTTL = policyData.getTtlDate();
				String newDate = convertDate(dateTTL);
				policyData.setTtlDate(newDate);
			}

			String policyType = policyData.getPolicyType();

			String filePrefix = null;
			if ("Config".equalsIgnoreCase(policyType)) {
				policyConfigType = policyData.getConfigPolicyType();
				if ("Firewall Config".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_FW_";
				}else if ("ClosedLoop_Fault".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_Fault_";
				}else if ("ClosedLoop_PM".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_PM_";
				}else if ("Micro Service".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_MS_";
				}else if ("Optimization".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_OOF_";
				}else if ("BRMS_Raw".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_BRMS_Raw_";
				}else if ("BRMS_Param".equalsIgnoreCase(policyConfigType)) {
					filePrefix = "Config_BRMS_Param_";
				}else {
					filePrefix = "Config_"; 
				}
			} else if ("Action".equalsIgnoreCase(policyType)) {
				filePrefix = "Action_";
			} else if ("Decision".equalsIgnoreCase(policyType)) {
				if ("MicroService_Model".equals(policyData.getRuleProvider())) {
					filePrefix = "Decision_MS_";
				} else {
					filePrefix = "Decision_";
				}
			}

			int version = 0;
			int highestVersion = 0;
			String createdBy;
			String modifiedBy;
			String scopeCheck = policyData.getDomainDir().replace(".", File.separator);
			if(!StringUtils.isBlank(scopeCheck)){
				PolicyEditorScopes policyEditorScope = (PolicyEditorScopes) commonClassDao.getEntityItem(PolicyEditorScopes.class, "scopeName", scopeCheck);
				if(policyEditorScope == null){
					UserInfo userInfo = new UserInfo();
					userInfo.setUserName("API");
					userInfo.setUserLoginId("API");
					PolicyEditorScopes editorScope = new PolicyEditorScopes();
					editorScope.setScopeName(scopeCheck);
					editorScope.setUserCreatedBy(userInfo);
					editorScope.setUserModifiedBy(userInfo);
					commonClassDao.save(editorScope);
				}
			}
			//get the highest version of policy from policy version table.
			String dbCheckPolicyName = policyData.getDomainDir() + File.separator + filePrefix + policyData.getPolicyName();
			PolicyVersion policyVersion = getPolicyVersionData(dbCheckPolicyName);	
			if(policyVersion == null){
				highestVersion = 0;
			}else{
				highestVersion = policyVersion.getHigherVersion();
			}
			
			if(highestVersion != 0 && policyVersion != null){
				if(policyData.isEditPolicy()){
					version = highestVersion +1;
					if(userId ==null){
						modifiedBy = "API";
					}else{
						modifiedBy = userId;
					}
					policyData.setUserId("API");
					createdBy = policyVersion.getCreatedBy();
					policyVersionDao = policyVersion;
					policyVersionDao.setActiveVersion(version);
					policyVersionDao.setHigherVersion(version);
					policyVersionDao.setModifiedBy(modifiedBy);
					policyVersionDao.setModifiedDate(new Date());
				}else{
					body = "policyExists";
					status = HttpStatus.CONFLICT;
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.addHeader("error", "policyExists");
					response.addHeader("policyName", policyData.getPolicyName());
					return new ResponseEntity<>(body, status);
				}		
			}else{
				// if policy does not exist and the request is updatePolicy return error
				if(policyData.isEditPolicy()){
					body = "policyNotAvailableForEdit";
					status = HttpStatus.NOT_FOUND;
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.addHeader("error", body);
					response.addHeader("message", policyData.getPolicyName() + " does not exist on the PAP and cannot be updated.");
					return new ResponseEntity<>(body, status);
				}
				version = 1;
				if(userId == null){
					createdBy = "API";
					modifiedBy = "API";
					policyData.setUserId("API");
				}else{
					createdBy = userId;
					modifiedBy = userId;
					policyData.setUserId("API");
				}
				policyVersionDao = new PolicyVersion();
				policyVersionDao.setPolicyName(dbCheckPolicyName);
				policyVersionDao.setActiveVersion(version);
				policyVersionDao.setHigherVersion(version);
				policyVersionDao.setCreatedBy(createdBy);
				policyVersionDao.setModifiedBy(modifiedBy);
			}
			
			policyData.setPolicyID(newPolicyID());
			policyData.setRuleID(ruleID);
	
			String policyFileName = dbCheckPolicyName.replace(File.separator, ".")+ "." + version + ".xml";
			policyData.setNewFileName(policyFileName);
			policyData.setPolicyDescription(policyData.getPolicyDescription()+ "@CreatedBy:" +createdBy + "@CreatedBy:" + "@ModifiedBy:" +modifiedBy + "@ModifiedBy:");
			policyData.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
			if(policyData.getApiflag() == null){
				//set the Rule Combining Algorithm Id to be sent to PAP-REST via JSON	
				if(policyData.getAttributes() != null && !policyData.getAttributes().isEmpty()){
					for(Object attribute : policyData.getAttributes()){
						if(attribute instanceof LinkedHashMap<?, ?>){
							String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
							String value = ((LinkedHashMap<?, ?>) attribute).get("value").toString();
							attributeMap.put(key, value);	
						}
					}
				}
				policyData.setDynamicFieldConfigAttributes(attributeMap);
			}
			
			policyData.setVersion(String.valueOf(version));
			policyData.setHighestVersion(version);

			// Calling Component class per policy type
			if ("Config".equalsIgnoreCase(policyType)) {
				if ("Firewall Config".equalsIgnoreCase(policyConfigType)) {
					newPolicy = new FirewallConfigPolicy(policyData);
				}else if ("BRMS_Raw".equalsIgnoreCase(policyConfigType)) { 
					policyData.setOnapName("DROOLS");
					policyData.setConfigName("BRMS_RAW_RULE");
					newPolicy = new CreateBrmsRawPolicy(policyData);
				}else if ("BRMS_Param".equalsIgnoreCase(policyConfigType)) {
					policyData.setOnapName("DROOLS");
					policyData.setConfigName("BRMS_PARAM_RULE");
					Map<String, String> drlRuleAndUIParams = new HashMap<>();
					if(policyData.getApiflag() == null){
						// If there is any dynamic field create the matches here
						String key="templateName";
						String value= policyData.getRuleName();
						drlRuleAndUIParams.put(key, value);
						if(policyData.getRuleData().size() > 0){
							for(Object keyValue: policyData.getRuleData().keySet()){
								drlRuleAndUIParams.put(keyValue.toString(), policyData.getRuleData().get(keyValue).toString());
							}
						}
						policyData.setBrmsParamBody(drlRuleAndUIParams);
					}else{
		                drlRuleAndUIParams=policyData.getBrmsParamBody();
		                String modelName= drlRuleAndUIParams.get("templateName");
		                PolicyLogger.info("Template name from API is: "+modelName);
		                
		                BRMSParamTemplate template = (BRMSParamTemplate) commonClassDao.getEntityItem(BRMSParamTemplate.class, "ruleName", modelName);
		                if(template == null){
		                	String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Template.  The template name, " 
		                            + modelName + " was not found in the dictionary.";
		                	body = message;
		                	status = HttpStatus.BAD_REQUEST;
		                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		                    response.addHeader("error", message);
		                    response.addHeader("modelName", modelName);
		                    return new ResponseEntity<String>(body, status);
		                }
					}		
					newPolicy = new CreateBrmsParamPolicy(policyData);
				}else if ("Base".equalsIgnoreCase(policyConfigType)) {
					newPolicy =  new ConfigPolicy(policyData);
				}else if ("ClosedLoop_Fault".equalsIgnoreCase(policyConfigType)) {
					newPolicy = new ClosedLoopPolicy(policyData);
				}else if ("ClosedLoop_PM".equalsIgnoreCase(policyConfigType)) {
					if(policyData.getApiflag() == null){
						policyData.setServiceType(policyData.getServiceTypePolicyName().get("serviceTypePolicyName").toString());
						ObjectMapper jsonMapper = new ObjectMapper();
						String jsonBody = jsonMapper.writeValueAsString(policyData.getJsonBodyData());
						jsonBody = jsonBody.replaceFirst("\\{", "\\{\"serviceTypePolicyName\": \"serviceTypeFieldValue\",");
						jsonBody = jsonBody.replace("serviceTypeFieldValue", policyData.getServiceType());
						policyData.setJsonBody(jsonBody);
					}
					newPolicy = new CreateClosedLoopPerformanceMetrics(policyData);
				}else if ("Micro Service".equalsIgnoreCase(policyConfigType)) {
					newPolicy = new MicroServiceConfigPolicy(policyData);
				}else if ("Optimization".equalsIgnoreCase(policyConfigType)) {
					newPolicy = new OptimizationConfigPolicy(policyData);
				}
			}else if("Action".equalsIgnoreCase(policyType)) {
				if(policyData.getApiflag() == null){
					List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
					List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();


					if(!policyData.getRuleAlgorithmschoices().isEmpty()){
						for(Object attribute : policyData.getRuleAlgorithmschoices()){
							if(attribute instanceof LinkedHashMap<?, ?>){
								String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
								String key = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
								String rule = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
								String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
								dynamicRuleAlgorithmLabels.add(label);
								dynamicRuleAlgorithmField1.add(key);
								dynamicRuleAlgorithmCombo.add(rule);
								dynamicRuleAlgorithmField2.add(value);
							}
						}
					}

					String actionDictValue = policyData.getActionAttributeValue();
					ActionPolicyDict jsonData = ((ActionPolicyDict) commonClassDao.getEntityItem(ActionPolicyDict.class, "attributeName", actionDictValue));
					if(jsonData!=null){
						String actionBodyString = jsonData.getBody();
						String actionDictHeader = jsonData.getHeader();
						String actionDictType = jsonData.getType();
						String actionDictUrl = jsonData.getUrl();
						String actionDictMethod = jsonData.getMethod();
						policyData.setActionDictHeader(actionDictHeader);
						policyData.setActionDictType(actionDictType);
						policyData.setActionDictUrl(actionDictUrl);
						policyData.setActionDictMethod(actionDictMethod);
						if (actionBodyString != null) {
							policyData.setActionBody(actionBodyString);
						}
					}
					policyData.setActionAttribute(actionDictValue);
					policyData.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
					policyData.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
					policyData.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
					policyData.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
				}else{
					// API request. 
					String comboDictValue = policyData.getActionAttribute();
					ActionPolicyDict jsonData = ((ActionPolicyDict) commonClassDao.getEntityItem(ActionPolicyDict.class, "attributeName", comboDictValue));
					if(jsonData!=null){
						policyData.setActionBody(jsonData.getBody());
						policyData.setActionDictHeader(jsonData.getHeader());
						policyData.setActionDictType(jsonData.getType());
						policyData.setActionDictUrl(jsonData.getUrl());
						policyData.setActionDictMethod(jsonData.getMethod());
					}
				}
				newPolicy = new ActionPolicy(policyData, commonClassDao);
			} else if ("Decision".equalsIgnoreCase(policyType)) {
				if(policyData.getApiflag() == null){
					Map<String, String> settingsMap = new HashMap<>();
					Map<String, String> treatmentMap = new HashMap<>();
					List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
					List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();
					List<Object> dynamicVariableList = new LinkedList<>();
					List<String> dataTypeList = new LinkedList<>();
					List<String> errorCodeList = new LinkedList<>();
					List<String> treatmentList = new LinkedList<>();

					if(!policyData.getSettings().isEmpty()){
						for(Object settingsData : policyData.getSettings()){
							if(settingsData instanceof LinkedHashMap<?, ?>){
								String key = ((LinkedHashMap<?, ?>) settingsData).get("key").toString();
								String value = ((LinkedHashMap<?, ?>) settingsData).get("value").toString();
								settingsMap.put(key, value);	
							}
						}
					}
					if(policyData.getRuleAlgorithmschoices()!=null && !policyData.getRuleAlgorithmschoices().isEmpty()){
						for(Object attribute : policyData.getRuleAlgorithmschoices()){
							if(attribute instanceof LinkedHashMap<?, ?>){
								String label = ((LinkedHashMap<?, ?>) attribute).get("id").toString();
								String key = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField1").toString();
								String rule = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmCombo").toString();
								String value = ((LinkedHashMap<?, ?>) attribute).get("dynamicRuleAlgorithmField2").toString();
								dynamicRuleAlgorithmLabels.add(label);
								dynamicRuleAlgorithmField1.add(key);
								dynamicRuleAlgorithmCombo.add(rule);
								dynamicRuleAlgorithmField2.add(value);
							}
						}
					}
					if(policyData.getRuleProvider()!=null && (policyData.getRuleProvider().equals(DecisionPolicy.GUARD_YAML)|| policyData.getRuleProvider().equals(DecisionPolicy.GUARD_BL_YAML)) 
							&& policyData.getYamlparams()!=null){
						attributeMap.put("actor", policyData.getYamlparams().getActor());
						attributeMap.put("recipe", policyData.getYamlparams().getRecipe());
						attributeMap.put("clname", policyData.getYamlparams().getClname());
						attributeMap.put("limit", policyData.getYamlparams().getLimit());
						attributeMap.put("timeWindow", policyData.getYamlparams().getTimeWindow());
						attributeMap.put("timeUnits", policyData.getYamlparams().getTimeUnits());
						attributeMap.put("guardActiveStart", policyData.getYamlparams().getGuardActiveStart());
						attributeMap.put("guardActiveEnd", policyData.getYamlparams().getGuardActiveEnd());
						if(policyData.getYamlparams().getBlackList()!=null){
							String blackList = StringUtils.join(policyData.getYamlparams().getBlackList(), ",");
							attributeMap.put("blackList", blackList);
						}
						if(policyData.getYamlparams().getTargets()!=null){
							String targets = StringUtils.join(policyData.getYamlparams().getTargets(),",");
							attributeMap.put("targets", targets);
						}
					}
					if(policyData.getRuleProvider()!=null && policyData.getRuleProvider().equals(DecisionPolicy.RAINY_DAY)){
						attributeMap.put("ServiceType", policyData.getRainyday().getServiceType());
						attributeMap.put("VNFType", policyData.getRainyday().getVnfType());
						attributeMap.put("BB_ID", policyData.getRainyday().getBbid());
						attributeMap.put("WorkStep", policyData.getRainyday().getWorkstep());
						
						if(policyData.getRainyday().getTreatmentTableChoices()!=null && !policyData.getRainyday().getTreatmentTableChoices().isEmpty()){
							for (Object table : policyData.getRainyday().getTreatmentTableChoices()){
								if(table instanceof LinkedHashMap<?,?>){
									String errorcode = ((LinkedHashMap<?,?>) table).get("errorcode").toString();
									String treatment = ((LinkedHashMap<?,?>) table).get("treatment").toString();
									treatmentMap.put(errorcode, treatment);
								}
							}
						}
					}
					
					policyData.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
					policyData.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
					policyData.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
					policyData.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
					policyData.setDynamicVariableList(dynamicVariableList);
					policyData.setDynamicSettingsMap(settingsMap);
					policyData.setDynamicFieldConfigAttributes(attributeMap);
					policyData.setDataTypeList(dataTypeList);
					policyData.setRainydayMap(treatmentMap);
					policyData.setErrorCodeList(errorCodeList);
					policyData.setTreatmentList(treatmentList);
				}
				newPolicy = new DecisionPolicy(policyData, commonClassDao);
			}

			if(newPolicy != null){
				newPolicy.prepareToSave();
			}else{
				body = "error";
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);								
				response.addHeader("error", "error");
				return new ResponseEntity<>(body, status);
			}
			
			PolicyDBDaoTransaction policyDBDaoTransaction = null;
			try{
				policyDBDao = PolicyDBDao.getPolicyDBDaoInstance(XACMLPapServlet.getEmf());
				policyDBDaoTransaction = policyDBDao.getNewTransaction();
				policyDBDaoTransaction.createPolicy(newPolicy, policyData.getUserId());
				successMap = newPolicy.savePolicies();
				if(successMap.containsKey("success")){
					policyDBDaoTransaction.commitTransaction();
					if(policyData.isEditPolicy()){
						commonClassDao.update(policyVersionDao);
					}else{
						commonClassDao.save(policyVersionDao);
					}
					try{
						PolicyElasticSearchController search= new PolicyElasticSearchController();
						search.updateElk(policyData);
					}catch(Exception e){
						LOGGER.error("Error Occured while saving policy to Elastic Database"+e);
					}
					body = "success";
					status = HttpStatus.OK;
					response.setStatus(HttpServletResponse.SC_OK);								
					response.addHeader("successMapKey", "success");								
					response.addHeader("policyName", policyData.getNewFileName());
					
                    //get message from the SafetyCheckerResults if present
                    String safetyCheckerResponse = policyData.getClWarning();
                    String existingCLName = policyData.getExistingCLName();

                    //if safetyCheckerResponse is not null add a header to send back with response
                    if(safetyCheckerResponse!=null) {
                        PolicyLogger.info("SafetyCheckerResponse message: " + safetyCheckerResponse);
                        response.addHeader("safetyChecker", safetyCheckerResponse);
                        response.addHeader("newCLName", CLName);
                        response.addHeader("conflictCLName", existingCLName);
                    } else {
                        PolicyLogger.info("SafetyCheckerResponse was empty or null.");
                    }
                    
				}else if (successMap.containsKey("invalidAttribute")) {
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Action Attribute";
					LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Could not fine " + policyData.getActionAttribute() + " in the ActionPolicyDict table.");
					body = "invalidAttribute";
					status = HttpStatus.BAD_REQUEST;
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);								
					response.addHeader("invalidAttribute", policyData.getActionAttribute());
					response.addHeader("error", message);
					response.addHeader("policyName", policyData.getPolicyName());
				}else if (successMap.containsKey("fwdberror")) {
					policyDBDaoTransaction.rollbackTransaction();
					body = "fwdberror";
					status = HttpStatus.BAD_REQUEST;
					String message = XACMLErrorConstants.ERROR_DATA_ISSUE
                            + "Error when inserting Firewall ConfigBody data into the database.";
    				PolicyLogger.error(message);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.addHeader("error", message);
					response.addHeader("policyName", policyData.getPolicyName());
				} else if ("Validation Failed".equals(successMap.get("error"))) {
                    policyDBDaoTransaction.rollbackTransaction();
                    String message = XACMLErrorConstants.ERROR_DATA_ISSUE
                			+ "Error Validating the Policy on the PAP.";
                    PolicyLogger.error(message);
                    body = "Validation";
					status = HttpStatus.BAD_REQUEST;
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addHeader("error", message);
                    response.addHeader("policyName", policyData.getPolicyName());
                }else {						
					policyDBDaoTransaction.rollbackTransaction();
					body = "error";
					status = HttpStatus.INTERNAL_SERVER_ERROR;
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);								
					response.addHeader("error", "error");							
				}
			}catch(Exception e){
				LOGGER.error("Exception Occured : ",e);
				if(policyDBDaoTransaction != null){
					policyDBDaoTransaction.rollbackTransaction();
				}
			}
		}
		catch (Exception e){
			LOGGER.error("Exception Occured : "+e.getMessage(),e);
			body = "error";
			response.addHeader("error", e.getMessage());	
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(body, status);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ResponseEntity<String> messageNotReadableExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
		LOGGER.error("Request not readable: {}", exception);
		StringBuilder message = new StringBuilder();
		message.append(exception.getMessage());
		if (exception.getCause() != null) {
			message.append(" Reason Caused: "
					+ exception.getCause().getMessage());
		}
		return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
	}

	public PolicyVersion getPolicyVersionData(String dbCheckPolicyName){
		PolicyVersion entityItem = (PolicyVersion) commonClassDao.getEntityItem(PolicyVersion.class, "policyName", dbCheckPolicyName);
		if (entityItem != null) {		
			if(entityItem.getPolicyName().equals(dbCheckPolicyName)){
				return entityItem;
			}
		}	
		return entityItem;
	}
}
