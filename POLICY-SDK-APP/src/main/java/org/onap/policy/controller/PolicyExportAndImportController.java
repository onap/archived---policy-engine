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
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;
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

	private ArrayList<String> selectedPolicy;
	private Set<String> scopes = null;
	private List<String> roles = null;
	private static String SUPERADMIN = "super-admin";
	private static String SUPEREDITOR = "super-editor";
	private static String ADMIN = "admin";
	private static String EDITOR = "editor";

	private static CommonClassDao commonClassDao;
	
	private PolicyEntity policyEntity;
	private ConfigurationDataEntity configurationDataEntity;
	private ActionBodyEntity actionBodyEntity;
	private PolicyVersion policyVersion;

	private Workbook workbook;

	private HSSFWorkbook workBook2;
	
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
		try{
			String file;
			selectedPolicy = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyExportAdapter adapter = mapper.readValue(root.get("exportData").toString(), PolicyExportAdapter.class);
			for (Object policyId :  adapter.getPolicyDatas()) {
				LinkedHashMap<?, ?> selected = (LinkedHashMap<?, ?>)policyId;
				String policyWithScope = selected.get("policyName").toString() + "." + selected.get("activeVersion").toString() + ".xml";
				String scope = policyWithScope.substring(0 , policyWithScope.lastIndexOf(File.separator)).replace(File.separator, ".");
				String policyName = policyWithScope.substring(policyWithScope.lastIndexOf(File.separator)+1);
				selectedPolicy.add(policyName+":"+scope);
			}
			List<Object> entityData = commonClassDao.getMultipleDataOnAddingConjunction(PolicyEntity.class, "policyName:scope", selectedPolicy);

			workBook2 = new HSSFWorkbook();
			HSSFSheet sheet = workBook2.createSheet("PolicyEntity");

			HSSFRow headingRow = sheet.createRow(0);
			headingRow.createCell(0).setCellValue("policyName");
			headingRow.createCell(1).setCellValue("scope");
			headingRow.createCell(2).setCellValue("version");
			headingRow.createCell(3).setCellValue("policyData");
			headingRow.createCell(4).setCellValue("description");
			headingRow.createCell(5).setCellValue("configurationbody");
			headingRow.createCell(6).setCellValue("configurationName");

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
						row.createCell(5).setCellValue(policyEntity.getConfigurationData().getConfigBody());
						row.createCell(6).setCellValue(policyEntity.getConfigurationData().getConfigurationName());
					}
					if(policyEntity.getActionBodyEntity() != null){
						row.createCell(5).setCellValue(policyEntity.getActionBodyEntity().getActionBody());
						row.createCell(6).setCellValue(policyEntity.getActionBodyEntity().getActionBodyName());
					}
				}else{
					row.createCell(5).setCellValue("");
					row.createCell(6).setCellValue("");
				}
				rowNo++;
			}

			String tmp = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator + "temp";
			String deleteCheckPath = tmp + File.separator + "PolicyExport.xls";
			File deleteCheck = new File(deleteCheckPath);
			if(deleteCheck.exists()){
				deleteCheck.delete();
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

		List<Object> userRoles = controller.getRoles(userId);
		Pair<Set<String>, List<String>> pair = org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
		roles = pair.u;
		scopes = pair.t;
		
		FileInputStream excelFile = new FileInputStream(new File(file));
		workbook = new HSSFWorkbook(excelFile);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = datatypeSheet.iterator();

		while (rowIterator.hasNext()) {
			finalColumn = false;
			policyEntity = new PolicyEntity();
			configurationDataEntity = new ConfigurationDataEntity();
			actionBodyEntity = new ActionBodyEntity();
			policyVersion = new PolicyVersion();
			Row currentRow = rowIterator.next();
			if (currentRow.getRowNum() == 0) {
				continue;
			}
			Iterator<Cell> cellIterator = currentRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if ("policyName".equalsIgnoreCase(getCellHeaderName(cell))) {
					policyEntity.setPolicyName(cell.getStringCellValue());
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
				if ("configurationbody".equalsIgnoreCase(getCellHeaderName(cell))) {
					if(policyEntity.getPolicyName().contains("Config_")){
						configExists = true;
						configurationDataEntity.setConfigBody(cell.getStringCellValue());
					}else if(policyEntity.getPolicyName().contains("Action_")){
						actionExists = true;
						actionBodyEntity.setActionBody(cell.getStringCellValue());
					}	
				}
				if ("configurationName".equalsIgnoreCase(getCellHeaderName(cell))) {
					finalColumn = true;
					configName = cell.getStringCellValue();
					if(policyEntity.getPolicyName().contains("Config_")){
						configurationDataEntity.setConfigurationName(cell.getStringCellValue());
					}else if(policyEntity.getPolicyName().contains("Action_")){
						actionBodyEntity.setActionBodyName(cell.getStringCellValue());
					}	
				}

				if(finalColumn){
					scope = policyEntity.getScope().replace(".", File.separator);
					String query = "FROM PolicyEntity where policyName = :policyName and scope = :policyScope";
					SimpleBindings params = new SimpleBindings();
					params.put("policyName", policyEntity.getPolicyName());
					params.put("policyScope", policyEntity.getScope());
					List<Object> queryData = controller.getDataByQuery(query, params);
					if(!queryData.isEmpty()){
						continue;
					}
					if (roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR)) {
						//1. if Role contains super admin create scope.
						//2. if Role contains super editor don't create new scope and add to list to show to user.
						
						PolicyEditorScopes policyEditorScope = (PolicyEditorScopes) commonClassDao.getEntityItem(PolicyEditorScopes.class, "scopeName", scope);
						if(policyEditorScope == null){
							if(roles.contains(SUPERADMIN)){
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
					if (roles.contains(ADMIN) || roles.contains(EDITOR)) {
						if(scopes.isEmpty()){
							logger.error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
						}else{
							//1. if Role contains admin, then check if parent scope has role admin, if not don't create a scope and add to list.
							if(roles.contains(ADMIN)){
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
						try(FileWriter fw = new FileWriter(PolicyController.getConfigHome() + File.separator + configName)){
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(configurationDataEntity.getConfigBody());
							bw.close();
						} catch (IOException e) {
							logger.error("Exception Occured While cloning the configuration file",e);
						}
					}
					if(actionExists){
						actionBodyEntity.setDeleted(false);
						actionBodyEntity.setCreatedBy(userId);
						actionBodyEntity.setModifiedBy(userId);
						commonClassDao.save(actionBodyEntity);
						try(FileWriter fw = new FileWriter(PolicyController.getActionHome() + File.separator + actionBodyEntity.getActionBodyName())) {
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(actionBodyEntity.getActionBody());
							bw.close();
						} catch (IOException e) {
							logger.error("Exception Occured While cloning the configuration file",e);
						}
					}
					if(configName != null){
						if(configName.contains("Config_")){
							ConfigurationDataEntity configuration = (ConfigurationDataEntity) commonClassDao.getEntityItem(ConfigurationDataEntity.class, "configurationName", configName);
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
				}
			}
		}
		return null;
	}

	//return the column header name value
	private String getCellHeaderName(Cell cell){
		return cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue().toString();
	}
}
