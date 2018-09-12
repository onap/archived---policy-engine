/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONObject;
import org.onap.policy.admin.PolicyRestController;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyExportAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.UserUtils.Pair;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/")
public class PolicyExportAndImportController extends RestrictedBaseController {
	private static Logger	logger	= FlexLogger.getLogger(PolicyExportAndImportController.class);
	
	private static String superAdmin = "super-admin";
	private static String superEditor = "super-editor";
	private static String admin = "admin";
	private static String editor = "editor";
	private static String policyName = "policyName";
	private static String configurationName = "configurationName";
	private static String configurationbody = "configurationbody";
	private static String config = "Config_";

	private static CommonClassDao commonClassDao;

	
	private PolicyController policyController;
	public PolicyController getPolicyController() {
		return policyController;
	}

	public void setPolicyController(PolicyController policyController) {
		this.policyController = policyController;
	}

	public static CommonClassDao getCommonClassDao() {
		return commonClassDao;
	}

	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		PolicyExportAndImportController.commonClassDao = commonClassDao;
	}

	@Autowired
	private PolicyExportAndImportController(CommonClassDao commonClassDao){
		PolicyExportAndImportController.commonClassDao = commonClassDao;
	}

	public PolicyExportAndImportController(){
		// Empty constructor
	}

	@RequestMapping(value={"/policy_download/exportPolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void exportPolicy(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try(HSSFWorkbook workBook2 = new HSSFWorkbook()){
			String file;
			ArrayList<String> selectedPolicy = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyExportAdapter adapter = mapper.readValue(root.get("exportData").toString(), PolicyExportAdapter.class);
			for (Object policyId :  adapter.getPolicyDatas()) {
				LinkedHashMap<?, ?> selected = (LinkedHashMap<?, ?>)policyId;
				String policyWithScope = selected.get(policyName).toString() + "." + selected.get("activeVersion").toString() + ".xml";
				String scope = policyWithScope.substring(0 , policyWithScope.lastIndexOf(File.separator)).replace(File.separator, ".");
				String policyName = policyWithScope.substring(policyWithScope.lastIndexOf(File.separator)+1);
				selectedPolicy.add(policyName+":"+scope);
			}
			List<Object> entityData = commonClassDao.getMultipleDataOnAddingConjunction(PolicyEntity.class, "policyName:scope", selectedPolicy);

			HSSFSheet sheet = workBook2.createSheet("PolicyEntity");

			HSSFRow headingRow = sheet.createRow(0);
			headingRow.createCell(0).setCellValue(policyName);
			headingRow.createCell(1).setCellValue("scope");
			headingRow.createCell(2).setCellValue("version");
			headingRow.createCell(3).setCellValue("policyData");
			headingRow.createCell(4).setCellValue("description");
			headingRow.createCell(5).setCellValue(configurationName);
			headingRow.createCell(6).setCellValue("bodySize");
			headingRow.createCell(7).setCellValue(configurationbody);

			short rowNo = 1;
			for (Object object : entityData) {
				PolicyEntity policyEntity = (PolicyEntity) object;
				HSSFRow row = sheet.createRow(rowNo);
				row.createCell(0).setCellValue(policyEntity.getPolicyName());
				row.createCell(1).setCellValue(policyEntity.getScope());
				row.createCell(2).setCellValue(policyEntity.getVersion());
				row.createCell(3).setCellValue(policyEntity.getPolicyData());
				row.createCell(4).setCellValue(policyEntity.getDescription());
				if(!policyEntity.getPolicyName().contains("Decision_")){
					if(policyEntity.getConfigurationData() != null){
						row.createCell(5).setCellValue(policyEntity.getConfigurationData().getConfigurationName());
						String body = policyEntity.getConfigurationData().getConfigBody();
						if(policyEntity.getPolicyName().contains("Config_BRMS_Param_")){
							int index = 0;
							int arraySize = 0;
							while (index < body.length()) {
								if(arraySize == 0){
									row.createCell(7).setCellValue(body.substring(index, Math.min(index + 30000, body.length())));
								}else{
									headingRow.createCell(7 + arraySize).setCellValue(configurationbody+arraySize);
									row.createCell(7 + arraySize).setCellValue(body.substring(index, Math.min(index + 30000, body.length())));
								}
								index += 30000;
								arraySize += 1;
							}
							row.createCell(6).setCellValue(arraySize);
						}else{	
							row.createCell(6).setCellValue(0);
							row.createCell(7).setCellValue(body);
						}	
					}
					if(policyEntity.getActionBodyEntity() != null){
						row.createCell(5).setCellValue(policyEntity.getActionBodyEntity().getActionBodyName());
						row.createCell(6).setCellValue(0);
						row.createCell(7).setCellValue(policyEntity.getActionBodyEntity().getActionBody());
					}
				}else{
					row.createCell(5).setCellValue("");
					row.createCell(6).setCellValue(0);
					row.createCell(7).setCellValue("");
				}
				rowNo++;
			}

			String tmp = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator + "temp";
			String deleteCheckPath = tmp + File.separator + "PolicyExport.xls";
			File deleteCheck = new File(deleteCheckPath);
			if(deleteCheck.exists() && deleteCheck.delete()){
				logger.info("Deleted the file from system before exporting a new file.");
			}
			File temPath = new File(tmp);
			if(!temPath.exists()){
				temPath.mkdir();
			}

			file =  temPath + File.separator + "PolicyExport.xls";
			File filepath = new File(file);
			FileOutputStream fos = new FileOutputStream(filepath);
			workBook2.write(fos);
			fos.flush();

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String successMap = file.substring(file.lastIndexOf("webapps")+8);
			String responseString = mapper.writeValueAsString(successMap);
			JSONObject j = new JSONObject("{data: " + responseString + "}");
			out.write(j.toString());
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while Exporting Policies"+e);
		}
	}

	//Policy Import 
	public JSONObject importRepositoryFile(String file, HttpServletRequest request) throws IOException{
		boolean configExists = false;
		boolean actionExists = false;
		String configName = null;
		String scope;
		boolean finalColumn;
		PolicyController controller = policyController != null ? getPolicyController() : new PolicyController();
		String userId = UserUtils.getUserSession(request).getOrgUserId();
		UserInfo userInfo = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", userId);

		//Check if the Role and Scope Size are Null get the values from db. 
		List<Object> userRoles = controller.getRoles(userId);
		Pair<Set<String>, List<String>> pair = org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
		List<String> roles = pair.u;
		Set<String> scopes = pair.t;
		String errorMsg = null;
		
		try(FileInputStream excelFile = new FileInputStream(new File(file)); HSSFWorkbook workbook = new HSSFWorkbook(excelFile)){
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = datatypeSheet.iterator();

			while (rowIterator.hasNext()) {
				finalColumn = false;
				PolicyEntity policyEntity = new PolicyEntity();
				ConfigurationDataEntity configurationDataEntity = new ConfigurationDataEntity();
				ActionBodyEntity actionBodyEntity = new ActionBodyEntity();
				PolicyVersion policyVersion = new PolicyVersion();
				Row currentRow = rowIterator.next();
				if (currentRow.getRowNum() == 0) {
					continue;
				}
				Iterator<Cell> cellIterator = currentRow.cellIterator();
				StringBuilder body = new StringBuilder();
				int bodySize = 0;
				int setBodySize = 0;
				boolean configurationBodySet = false;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (policyName.equalsIgnoreCase(getCellHeaderName(cell))) {
						policyEntity.setPolicyName(cell.getStringCellValue());
						finalColumn = false; 
						configurationBodySet = false;
						configExists = false;
						actionExists = false;
					}
					if ("scope".equalsIgnoreCase(getCellHeaderName(cell))) {
						policyEntity.setScope(cell.getStringCellValue());
					}
					if ("policyData".equalsIgnoreCase(getCellHeaderName(cell))) {
						policyEntity.setPolicyData(cell.getStringCellValue());
					}
					if ("description".equalsIgnoreCase(getCellHeaderName(cell))) {
						policyEntity.setDescription(cell.getStringCellValue());
					}
					if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))) {
						if(policyEntity.getPolicyName().contains(config)){
							if(policyEntity.getPolicyName().contains("Config_BRMS_Param_")){
								setBodySize += 1;
							}
							if(setBodySize == bodySize){
								finalColumn = true;
								configurationBodySet = true;
							}else if(setBodySize == 0){
								configurationBodySet = true;
							}
							configExists = true;
							body.append(cell.getStringCellValue());
						}else if(policyEntity.getPolicyName().contains("Action_")){
							actionExists = true;
							actionBodyEntity.setActionBody(cell.getStringCellValue());
						}	
					}
					if ("bodySize".equalsIgnoreCase(getCellHeaderName(cell))) {
						if(cell.getNumericCellValue() < 1){
							finalColumn = true;
						}else{
							bodySize = (int) cell.getNumericCellValue();
						}
					}
					if (configurationName.equalsIgnoreCase(getCellHeaderName(cell))) {
						configName = cell.getStringCellValue();
						if(policyEntity.getPolicyName().contains(config)){
							configurationDataEntity.setConfigurationName(cell.getStringCellValue());
						}else if(policyEntity.getPolicyName().contains("Action_")){
							actionBodyEntity.setActionBodyName(cell.getStringCellValue());
						}	
					}

					if(finalColumn && configurationBodySet){
					    
					    
						configurationDataEntity.setConfigBody(body.toString());
						
	                    if(policyEntity.getPolicyName().contains(CONFIG_MS)){
	                            //validate some required values first
	                            errorMsg = validatRequiredFields(policyEntity.getPolicyName(), body.toString());
	                            if(errorMsg != null){
	                                logger.error("errorMsg => " + errorMsg);
	                                JSONObject response = new JSONObject();
	                                if(errorMsg != null){
	                                   response.append("error", errorMsg);
	                                   return response;
	                                }                           
	                            }
	                    }
						scope = policyEntity.getScope().replace(".", File.separator);
						String query = "FROM PolicyEntity where policyName = :policyName and scope = :policyScope";
						SimpleBindings params = new SimpleBindings();
						params.put(policyName, policyEntity.getPolicyName());
						params.put("policyScope", policyEntity.getScope());
						List<Object> queryData = controller.getDataByQuery(query, params);
						if(!queryData.isEmpty()){
							continue;
						}
						if (roles.contains(superAdmin) || roles.contains(superEditor)) {
							//1. if Role contains super admin create scope.
							//2. if Role contains super editor don't create new scope and add to list to show to user.

							PolicyEditorScopes policyEditorScope = (PolicyEditorScopes) commonClassDao.getEntityItem(PolicyEditorScopes.class, "scopeName", scope);
							if(policyEditorScope == null){
								if(roles.contains(superAdmin)){
									PolicyEditorScopes policyEditorScopeEntity = new PolicyEditorScopes();
									policyEditorScopeEntity.setScopeName(scope);
									policyEditorScopeEntity.setUserCreatedBy(userInfo);
									policyEditorScopeEntity.setUserModifiedBy(userInfo);
									commonClassDao.save(policyEditorScopeEntity);
								}else{
									//Add Error Message a new Scope Exists, contact super-admin to create a new scope
									continue;
								}
							}
						}
						if (roles.contains(admin) || roles.contains(editor)) {
							if(scopes.isEmpty()){
								logger.error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
							}else{
								//1. if Role contains admin, then check if parent scope has role admin, if not don't create a scope and add to list.
								if(roles.contains(admin)){
									String scopeCheck = scope.substring(0, scope.lastIndexOf('.'));
									if(scopes.contains(scopeCheck)){
										PolicyEditorScopes policyEditorScopeEntity = new PolicyEditorScopes();
										policyEditorScopeEntity.setScopeName(scope);
										policyEditorScopeEntity.setUserCreatedBy(userInfo);
										policyEditorScopeEntity.setUserModifiedBy(userInfo);
										commonClassDao.save(policyEditorScopeEntity);
									}else{
										continue;
									}
								}else{
									continue;
								}
							}
						} 	

						if(configExists){
							if(configName.endsWith("json")){
								configurationDataEntity.setConfigType("JSON");
							}else if(configName.endsWith("txt")){
								configurationDataEntity.setConfigType("OTHER");
							}else if(configName.endsWith("xml")){
								configurationDataEntity.setConfigType("XML");
							}else if(configName.endsWith("properties")){
								configurationDataEntity.setConfigType("PROPERTIES");
							}
							configurationDataEntity.setDeleted(false);
							configurationDataEntity.setCreatedBy(userId);
							configurationDataEntity.setModifiedBy(userId);
							commonClassDao.save(configurationDataEntity);
							writeConfigurationFile(configurationDataEntity);
						}
						if(actionExists){
							actionBodyEntity.setDeleted(false);
							actionBodyEntity.setCreatedBy(userId);
							actionBodyEntity.setModifiedBy(userId);
							commonClassDao.save(actionBodyEntity);
							writeActionBodyFile(actionBodyEntity);
						}
						if(configName != null){
							if(configName.contains(config)){
								ConfigurationDataEntity configuration = (ConfigurationDataEntity) commonClassDao.getEntityItem(ConfigurationDataEntity.class, configurationName, configName);
								policyEntity.setConfigurationData(configuration);
							}else{
								ActionBodyEntity actionBody = (ActionBodyEntity) commonClassDao.getEntityItem(ActionBodyEntity.class, "actionBodyName", configName);
								policyEntity.setActionBodyEntity(actionBody);
							}
						}
						policyEntity.setCreatedBy(userId);
						policyEntity.setModifiedBy(userId);
						policyEntity.setDeleted(false);
						commonClassDao.save(policyEntity);

						policyVersion = new PolicyVersion();
						String policyName = policyEntity.getPolicyName().replace(".xml", "");
						int version = Integer.parseInt(policyName.substring(policyName.lastIndexOf('.')+1));
						policyName = policyName.substring(0, policyName.lastIndexOf('.'));

						policyVersion.setPolicyName(scope.replace(".", File.separator) + File.separator + policyName);
						policyVersion.setActiveVersion(version);
						policyVersion.setHigherVersion(version);
						policyVersion.setCreatedBy(userId);
						policyVersion.setModifiedBy(userId);
						commonClassDao.save(policyVersion);

						//Notify Other paps regarding Export Policy.
						PolicyRestController restController = new PolicyRestController();
						restController.notifyOtherPAPSToUpdateConfigurations("exportPolicy", configName, null);
					}
				}
			}
		}catch(IOException e){
			logger.error("Exception Occured While importing the Policy"+e);
		}
		return null;
	}

	private void writeConfigurationFile(ConfigurationDataEntity configurationDataEntity){
		try(FileWriter fw = new FileWriter(PolicyController.getConfigHome() + File.separator + configurationDataEntity.getConfigurationName())){
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(configurationDataEntity.getConfigBody());
			bw.close();
		} catch (IOException e) {
			logger.error("Exception Occured While cloning the configuration file",e);
		}
	}
	
	private void writeActionBodyFile(ActionBodyEntity actionBodyEntity){
		try(FileWriter fw = new FileWriter(PolicyController.getActionHome() + File.separator + actionBodyEntity.getActionBodyName())) {
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(actionBodyEntity.getActionBody());
			bw.close();
		} catch (IOException e) {
			logger.error("Exception Occured While cloning the configuration file",e);
		}
	}
	
	//return the column header name value
	private String getCellHeaderName(Cell cell){
		return cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue().toString();
	}
	
	   private String validatRequiredFields(String policyName, String jsonString){
	       
	        try{
	            
	            JSONObject jsonObject = new JSONObject(jsonString);
	            
	            if(jsonObject != null){
	                
	                String configName = jsonObject.getString("configName");
	                String uuid = jsonObject.getString("uuid");
	                String location = jsonObject.getString("location");
	                String policyScope = jsonObject.getString("policyScope");
	                String msService = jsonObject.getString("service");
	                String msVersion = jsonObject.getString("version");
	                
	                if(configName != null){
	                    List<String> configNames = commonClassDao.getDataByColumn( MicroServiceConfigName.class, "name");
	                    if(configNames != null){
	                        if(!(configNames.stream().filter(o -> o.equals(configName)).findFirst().isPresent())){
	                            return "Policy:"+ policyName+ " configName: "+configName+ " is not valid.";
	                        }
	                    }                   
	                }else{
	                    return "Policy:"+ policyName+ "configName is null";
	                }
	                
	                if(uuid != null){
	                    List<String> uuids = commonClassDao.getDataByColumn( DCAEuuid.class, "name");
	                    if(uuids != null){
	                        if(!(uuids.stream().filter(o -> o.equals(uuid)).findFirst().isPresent())){
	                            return "Policy:"+ policyName+ " uuid: "+uuid+ " is not valid.";
	                        }
	                    }                   
	                }else{
	                    return "Policy:"+ policyName+ "uuid is null";
	                }
	                
	                if(location != null){
	                    List<String> locations = commonClassDao.getDataByColumn( MicroServiceLocation.class, "name");
	                    if(locations != null){
	                        if(!(locations.stream().filter(o -> o.equals(location)).findFirst().isPresent())){
	                            return "Policy:"+ policyName+ " location: "+location+ " is not valid.";
	                        }
	                    }
	                }else{
	                    return "Policy:"+ policyName+ "location is null";
	                }
	                

	                if(policyScope != null){
	                    List<Object> foundData =  commonClassDao.checkDuplicateEntry(policyScope, "groupList", GroupPolicyScopeList.class);
	                    if(foundData == null || foundData.isEmpty()){
	                        return "Policy:"+ policyName+ " policyScope: "+policyScope+ " is not valid.";
	                    }
	                }else{
	                    return "Policy:"+ policyName+ "policyScope is null";
	                }   

	                if(msService == null){
	                    return "Policy:"+ policyName+ "service is null";
	                }
	                
	                if(msVersion == null){
	                    return "Policy:"+ policyName+ "version is null";
	                }
	                
	                if(!isAttributeObjectFound(msService, msVersion)){
	                    return "Policy:"+ policyName+ " MS Service: "+msService+ " and MS Version: " + msVersion + " is not valid.";
	                }
	            }
	            
	        }catch(Exception e){
	            logger.error("Exception Occured While validating required fields",e);
	        }

	        return null;
	    }
	    
	    private boolean isAttributeObjectFound(String name, String version) {   
	        MicroServiceModels workingModel = null;
	        List<Object> microServiceModelsData = commonClassDao.getDataById(MicroServiceModels.class, "modelName", name);
	        if(microServiceModelsData != null){
	            for (int i = 0; i < microServiceModelsData.size(); i++) {
	                workingModel = (MicroServiceModels) microServiceModelsData.get(i);
	                if(workingModel != null){
	                    if (workingModel.getVersion()!=null && workingModel.getVersion().equals(version)){
	                         return true; 
	                    }               
	                }
	            }
	        }
	        return false;
	    }
	}
}