/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class ExportAndImportDecisionBlackListEntries extends RestrictedBaseController{
	
	private static final Logger policyLogger = FlexLogger.getLogger(ExportAndImportDecisionBlackListEntries.class);
	private static final String BLACKLISTENTRIESDATA = "blackListEntries";
	private static final String ACTION = "Action";
	private static final String BLACKLISTENTRY = "BlackListEntry";
	
	@RequestMapping(value={"/policycreation/exportDecisionBlackListEntries"}, method={RequestMethod.POST})
	public void exportBlackList(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try(HSSFWorkbook workBook2 = new HSSFWorkbook()){
			String file;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyRestAdapter adapter = mapper.readValue(root.get("policyData").toString(), PolicyRestAdapter.class);
			DecisionPolicyController controller = new DecisionPolicyController();
			controller.prePopulateDecisionPolicyData(adapter, null);
			List<String> blackLists = adapter.getYamlparams().getBlackList();
			HSSFSheet sheet = workBook2.createSheet("BlackList");
			HSSFRow headingRow = sheet.createRow(0);
			headingRow.createCell(0).setCellValue("Action");
			headingRow.createCell(1).setCellValue("BlackListEntry");
			
			short rowNo = 1;
			for (Object object : blackLists) {
				HSSFRow row = sheet.createRow(rowNo);
				row.createCell(0).setCellValue(1);
				row.createCell(1).setCellValue(object.toString());
				rowNo++;
			}

			String tmp = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator + "temp";
			
			/*
			 * Export FileName is the combination of BlacList+Scope+PolicyName+Version+PolicyCreatedDate.
			 * 
			 * */
			
			SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date  = parseFormat.parse(root.get("date").toString().replaceAll("\"", ""));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String formatedDate = dateFormat.format(date);
			
			String fileName = "BlackList_Scope_"+adapter.getDomainDir()+"_Name_"+adapter.getPolicyName()
            					+"_Version_"+root.get("version").toString()+"_Date_"+formatedDate+".xls";
			
			String deleteCheckPath = tmp + File.separator + fileName;
			File deleteCheck = new File(deleteCheckPath);
			if(deleteCheck.exists() && deleteCheck.delete()){
				policyLogger.info("Deleted the file from system before exporting a new file.");
			}
			
			File temPath = new File(tmp);
			if(!temPath.exists()){
				temPath.mkdir();
			}

			file =  temPath + File.separator + fileName;
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
			policyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while Exporting BlackList Entries"+e);
		}
	}
	
	@RequestMapping(value={"/policycreation/importBlackListForDecisionPolicy"}, method={RequestMethod.POST})
	public void importBlackListFile(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		Map<String, InputStream> files = new HashMap<>();
		Set<String> blackListEntries = new HashSet<>();
		Set<String> appendBlackListEntries = new HashSet<>();
		List<String> errorLogs = new ArrayList<>();
		errorLogs.add("error");
		Map<String, Object> model = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		File file = null;
		if(!items.isEmpty()){
			FileItem item = items.get(0);
			files.put(item.getName(), item.getInputStream());
			file = new File(item.getName());
			String fileName = file.getName();
			try (OutputStream outputStream = new FileOutputStream(file);){
				IOUtils.copy(item.getInputStream(), outputStream);
				if(fileName.startsWith("BlackList") && fileName.endsWith(".xls")){
					try(Workbook workbook = WorkbookFactory.create(new File(fileName))){
						Sheet datatypeSheet = workbook.getSheetAt(0);
						Iterator<Row> rowIterator = datatypeSheet.iterator();
						readExcelRows(rowIterator, blackListEntries, appendBlackListEntries, errorLogs);
						if (errorLogs.size() == 1) {
							model.put(BLACKLISTENTRIESDATA, mapper.writeValueAsString(blackListEntries));
							model.put("appendBlackListEntries", mapper.writeValueAsString(appendBlackListEntries));
						} else {
							model.put(BLACKLISTENTRIESDATA, mapper.writeValueAsString(errorLogs));
						}
					}catch(Exception e){
						String error = "Error Occured While Reading File. Please check the format of the file.";
						errorLogs.add(error);
						model.put(BLACKLISTENTRIESDATA, mapper.writeValueAsString(errorLogs));
						policyLogger.error(error +e);
					}
				}else{
					errorLogs.add("The File Name should start with BlackList and must be .xls format.");
					model.put(BLACKLISTENTRIESDATA, mapper.writeValueAsString(errorLogs));
				}
			}
		}
		if(file != null){
			Files.delete(file.toPath());
		}
		JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
		JSONObject j = new JSONObject(msg);
		response.getWriter().write(j.toString());
	}
	
	private void readExcelRows(Iterator<Row> rowIterator, Set<String> blackListEntries, Set<String> appendBlackListEntries, List<String> errorLogs){
		while (rowIterator.hasNext()) {
			boolean actionCheck = false;
			boolean blackListCheck = false;
			Row currentRow = rowIterator.next();

			if (currentRow.getRowNum() == 0) {
				continue;
			}
			String blEntry = "";
			int actionEntry = 0;
			Iterator<Cell> cellIterator = currentRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (ACTION.equalsIgnoreCase(getCellHeaderName(cell))) {
					try {
						actionEntry =  (int)cell.getNumericCellValue();
						actionCheck = true;
					} catch (Exception e){
						policyLogger.error("The column doesn't have either 0 or 1. So, considering to not add." +e);
						actionEntry = 0;
					}
				}
				if (BLACKLISTENTRY.equalsIgnoreCase(getCellHeaderName(cell))) {
					try {
						blEntry = cell.getStringCellValue();
						blackListCheck = true;
					} catch (Exception e){
						policyLogger.error("The column doesn't have blacklist entry. So, considering to not add." +e);
						actionEntry = 0;
					}
				}
			}
			if(actionCheck && blackListCheck){
				if(actionEntry == 1){
					blackListEntries.add(blEntry);
				}else{
					appendBlackListEntries.add(blEntry);
				}
			}
		} 
	}

	//return the column header name value
	private String getCellHeaderName(Cell cell){
		return cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue().toString();
	}
}