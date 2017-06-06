/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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
package org.openecomp.policy.pap.xacml.rest.policycontroller;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.XACMLPapServlet;
import org.openecomp.policy.pap.xacml.rest.components.ActionPolicy;
import org.openecomp.policy.pap.xacml.rest.components.ClosedLoopPolicy;
import org.openecomp.policy.pap.xacml.rest.components.ConfigPolicy;
import org.openecomp.policy.pap.xacml.rest.components.CreateBrmsParamPolicy;
import org.openecomp.policy.pap.xacml.rest.components.CreateBrmsRawPolicy;
import org.openecomp.policy.pap.xacml.rest.components.CreateClosedLoopPerformanceMetrics;
import org.openecomp.policy.pap.xacml.rest.components.DecisionPolicy;
import org.openecomp.policy.pap.xacml.rest.components.FirewallConfigPolicy;
import org.openecomp.policy.pap.xacml.rest.components.MicroServiceConfigPolicy;
import org.openecomp.policy.pap.xacml.rest.components.Policy;
import org.openecomp.policy.pap.xacml.rest.components.PolicyDBDao;
import org.openecomp.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.openecomp.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.openecomp.policy.pap.xacml.rest.util.AbstractPolicyCreation;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.ActionPolicyDict;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
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
	
	@Autowired
	public PolicyCreation(CommonClassDao commonClassDao){
		PolicyCreation.commonClassDao = commonClassDao;
	}

	public PolicyCreation(){}
	
	@RequestMapping(value="/policycreation/save_policy", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> savePolicy(@RequestBody PolicyRestAdapter policyData, HttpServletResponse response) throws Exception{
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
				String newDate = convertDate(dateTTL, false);
				policyData.setTtlDate(newDate);
			}

			String policyType = policyData.getPolicyType();

			String filePrefix = null;
			if (policyType.equalsIgnoreCase("Config")) {
				policyConfigType = policyData.getConfigPolicyType();
				if (policyConfigType.equalsIgnoreCase("Firewall Config")) {
					filePrefix = "Config_FW_";
				}else if (policyConfigType.equalsIgnoreCase("ClosedLoop_Fault")) {
					filePrefix = "Config_Fault_";
				}else if (policyConfigType.equalsIgnoreCase("ClosedLoop_PM")) {
					filePrefix = "Config_PM_";
				}else if (policyConfigType.equalsIgnoreCase("Micro Service")) {
					filePrefix = "Config_MS_";
				}else if (policyConfigType.equalsIgnoreCase("BRMS_Raw")) {
					filePrefix = "Config_BRMS_Raw_";
				}else if (policyConfigType.equalsIgnoreCase("BRMS_Param")) {
					filePrefix = "Config_BRMS_Param_";
				}else {
					filePrefix = "Config_"; 
				}
			} else if (policyType.equalsIgnoreCase("Action")) {
				filePrefix = "Action_";
			} else if (policyType.equalsIgnoreCase("Decision")) {
				filePrefix = "Decision_";
			}

			int version = 0;
			int highestVersion = 0;
			String createdBy = "";
			String modifiedBy = userId;
			String scopeCheck = policyData.getDomainDir().replace(".", File.separator);
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
				}else{
					body = "policyExists";
					status = HttpStatus.CONFLICT;
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.addHeader("error", "policyExists");
					response.addHeader("policyName", policyData.getPolicyName());
					return new ResponseEntity<String>(body, status);
				}		
			}else{
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
				if(policyData.getAttributes() != null){
					if(policyData.getAttributes().size() > 0){
						for(Object attribute : policyData.getAttributes()){
							if(attribute instanceof LinkedHashMap<?, ?>){
								String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
								String value = ((LinkedHashMap<?, ?>) attribute).get("value").toString();
								attributeMap.put(key, value);	
							}
						}
					}
				}
				policyData.setDynamicFieldConfigAttributes(attributeMap);
			}
			
			policyData.setVersion(String.valueOf(version));
			policyData.setHighestVersion(version);

			// Calling Component class per policy type
			if (policyType.equalsIgnoreCase("Config")) {
				if (policyConfigType.equalsIgnoreCase("Firewall Config")) {
					newPolicy = new FirewallConfigPolicy(policyData);
				}else if (policyConfigType.equalsIgnoreCase("BRMS_Raw")) { 
					policyData.setEcompName("DROOLS");
					policyData.setConfigName("BRMS_RAW_RULE");
					newPolicy = new CreateBrmsRawPolicy(policyData);
				}else if (policyConfigType.equalsIgnoreCase("BRMS_Param")) {
					policyData.setEcompName("DROOLS");
					policyData.setConfigName("BRMS_PARAM_RULE");
					Map<String, String> drlRuleAndUIParams = new HashMap<>();
					if(policyData.getApiflag() == null){
						// If there is any dynamic field create the matches here
						String key="templateName";
						String value=(String) policyData.getRuleName();
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
				}else if (policyConfigType.equalsIgnoreCase("Base")) {
					newPolicy =  new ConfigPolicy(policyData);
				}else if (policyConfigType.equalsIgnoreCase("ClosedLoop_Fault")) {
					newPolicy = new ClosedLoopPolicy(policyData);
				}else if (policyConfigType.equalsIgnoreCase("ClosedLoop_PM")) {
					if(policyData.getApiflag() == null){
						policyData.setServiceType(policyData.getServiceTypePolicyName().get("serviceTypePolicyName").toString());
						ObjectMapper jsonMapper = new ObjectMapper();
						String jsonBody = jsonMapper.writeValueAsString(policyData.getJsonBodyData());
						jsonBody = jsonBody.replaceFirst("\\{", "\\{\"serviceTypePolicyName\": \"serviceTypeFieldValue\",");
						jsonBody = jsonBody.replace("serviceTypeFieldValue", policyData.getServiceType());
						policyData.setJsonBody(jsonBody);
					}
					newPolicy = new CreateClosedLoopPerformanceMetrics(policyData);
				}else if (policyConfigType.equalsIgnoreCase("Micro Service")) {
					newPolicy = new MicroServiceConfigPolicy(policyData);
				}
			}else if(policyType.equalsIgnoreCase("Action")) {
				if(policyData.getApiflag() == null){
					List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
					List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();


					if(policyData.getRuleAlgorithmschoices().size() > 0){
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
					String actionBodyString = jsonData.getBody();
					String actionDictHeader = jsonData.getHeader();
					String actionDictType = jsonData.getType();
					String actionDictUrl = jsonData.getUrl();
					String actionDictMethod = jsonData.getMethod();
					policyData.setActionDictHeader(actionDictHeader);
					policyData.setActionDictType(actionDictType);
					policyData.setActionDictUrl(actionDictUrl);
					policyData.setActionDictMethod(actionDictMethod);
					policyData.setActionAttribute(actionDictValue);
					policyData.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
					policyData.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
					policyData.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
					policyData.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
					if (actionBodyString != null) {
						policyData.setActionBody(actionBodyString);
					}
				}
				newPolicy = new ActionPolicy(policyData);
			} else if (policyType.equalsIgnoreCase("Decision")) {
				if(policyData.getApiflag() == null){
					Map<String, String> settingsMap = new HashMap<>();
					List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
					List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
					List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();
					List<Object> dynamicVariableList = new LinkedList<>();
					List<String> dataTypeList = new LinkedList<>();

					if(policyData.getSettings().size() > 0){
						for(Object settingsData : policyData.getSettings()){
							if(settingsData instanceof LinkedHashMap<?, ?>){
								String key = ((LinkedHashMap<?, ?>) settingsData).get("key").toString();
								String value = ((LinkedHashMap<?, ?>) settingsData).get("value").toString();
								settingsMap.put(key, value);	
							}
						}
					}
					if(policyData.getRuleAlgorithmschoices()!=null && policyData.getRuleAlgorithmschoices().size() > 0){
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
							&& policyData.getYamlparams()!=null){	attributeMap.put("actor", policyData.getYamlparams().getActor());
						attributeMap.put("recipe", policyData.getYamlparams().getRecipe());
						attributeMap.put("limit", policyData.getYamlparams().getLimit());
						attributeMap.put("timeWindow", policyData.getYamlparams().getTimeWindow());
						attributeMap.put("guardActiveStart", policyData.getYamlparams().getGuardActiveStart());
						attributeMap.put("guardActiveEnd", policyData.getYamlparams().getGuardActiveEnd());
						if(policyData.getYamlparams().getBlackList()!=null){
							String blackList = StringUtils.join(policyData.getYamlparams().getBlackList(), ",");
							attributeMap.put("blackList", blackList);
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
				}
				newPolicy = new DecisionPolicy(policyData);
			}

			if(newPolicy != null){
				newPolicy.prepareToSave();
			}else{
				body = "error";
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);								
				response.addHeader("error", "error");
				return new ResponseEntity<String>(body, status);
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
				} else if (successMap.get("error").equals("Validation Failed")) {
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
				if(policyDBDaoTransaction != null){
					policyDBDaoTransaction.rollbackTransaction();
				}
			}
		}
		catch (Exception e){
			LOGGER.error("Exception Occured"+e);
		}
		return new ResponseEntity<String>(body, status);
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
