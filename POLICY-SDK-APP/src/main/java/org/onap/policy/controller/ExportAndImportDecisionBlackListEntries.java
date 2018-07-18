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

import com.google.gson.Gson;
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
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
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
import org.onap.policy.rest.adapter.ReturnBlackList;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



/**
 * This class is used to import and export the black list entries which were used in the Decision Blacklist Guard YAML
 * Policy.
 *
 */
@Controller
@RequestMapping("/")
public class ExportAndImportDecisionBlackListEntries extends RestrictedBaseController {

    private static final Logger policyLogger = FlexLogger.getLogger(ExportAndImportDecisionBlackListEntries.class);
    private static final String BLACKLISTENTRIESDATA = "blackListEntries";
    private static final String ACTION = "Action";
    private static final String BLACKLISTENTRY = "BlackListEntry";

    /**
     * This method is used to Export the Black List entries data from Decision BlackList Guard YAML Policy. So, user can
     * update the file on adding or removing the entries, for updating the policies or using in other Environments.
     * 
     * @param request the request contains the policy data. So, based on that we can populate and read and write the
     *        entries.
     * @param response after reading and writing the blacklist list entries to file, the file is copied to tmp directory
     *        and making available to user to download from GUI.
     * @throws IOException exception throws if anything goes wrong in the process.
     */
    @RequestMapping(value = {"/policycreation/exportDecisionBlackListEntries"}, method = {RequestMethod.POST})
    public void exportBlackList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (HSSFWorkbook workBook = new HSSFWorkbook()) {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            JSONObject root = new JSONObject(requestData);
            PolicyRestAdapter adapter = new Gson().fromJson(root.get("policyData").toString(), PolicyRestAdapter.class);
            DecisionPolicyController controller = new DecisionPolicyController();
            controller.prePopulateDecisionPolicyData(adapter, null);
            List<String> blackLists = adapter.getYamlparams().getBlackList();
            HSSFSheet sheet = workBook.createSheet("BlackList");
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

            String tmpFile = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator + "temp";

            /*
             * Export FileName is the combination of BlacList+Scope+PolicyName+Version+PolicyCreatedDate.
             * 
             */

            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = parseFormat.parse(root.get("date").toString().replaceAll("\"", ""));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String formatedDate = dateFormat.format(date);

            String fileName = "BlackList_Scope_" + adapter.getDomainDir() + "_Name_" + adapter.getPolicyName()
                    + "_Version_" + root.get("version").toString() + "_Date_" + formatedDate + ".xls";

            String deleteCheckPath = tmpFile + File.separator + fileName;
            File deleteCheck = new File(deleteCheckPath);
            if (deleteCheck.exists() && deleteCheck.delete()) {
                policyLogger.info("Deleted the file from system before exporting a new file.");
            }

            File temPath = new File(tmpFile);
            if (!temPath.exists()) {
                temPath.mkdir();
            }

            String file = temPath + File.separator + fileName;
            File filepath = new File(file);
            FileOutputStream fos = new FileOutputStream(filepath);
            workBook.write(fos);
            fos.flush();

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application / json");
            request.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            String successMap = file.substring(file.lastIndexOf("webapps") + 8);
            String responseString = new Gson().toJson(successMap);
            JSONObject jsonResposne = new JSONObject("{data: " + responseString + "}");
            out.write(jsonResposne.toString());
        } catch (Exception e) {
            policyLogger.error(
                    XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Exception Occured while Exporting BlackList Entries" + e);
        }
    }

    /**
     * This method is used to import the BlackList excel file into the system. Which is used to create Decision
     * Blacklist Guard YAML Policy.
     * 
     * @param request the HTTP request contains file upload stream form GUI.
     * @param response the response is send to the GUI after reading the file input stream.
     * @throws FileUploadException throws fileUpload Exception.
     * @throws IOException throws IO Exceptions.
     */
    @RequestMapping(value = {"/policycreation/importBlackListForDecisionPolicy"}, method = {RequestMethod.POST})
    public void importBlackListFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        List<String> errorLogs = new ArrayList<>();
        Gson mapper = new Gson();
        errorLogs.add("error");
        Map<String, Object> model = new HashMap<>();
        if (items.isEmpty()) {
            errorLogs.add("The File doesn't have any content and it is invalid.");
            model.put(BLACKLISTENTRIESDATA, errorLogs);
        } else {
            readItems(items, errorLogs, model);
        }
        JsonMessage msg = new JsonMessage(mapper.toJson(model));
        JSONObject jsonResposne = new JSONObject(msg);
        response.getWriter().write(jsonResposne.toString());
    }

    private void readItems(List<FileItem> items, List<String> errorLogs, Map<String, Object> model) throws Exception {
        Map<String, InputStream> files = new HashMap<>();

        FileItem item = items.get(0);
        files.put(item.getName(), item.getInputStream());
        File file = new File(item.getName());
        String fileName = file.getName();
        try (OutputStream outputStream = new FileOutputStream(file);) {
            IOUtils.copy(item.getInputStream(), outputStream);
            if (fileName.startsWith("BlackList") && fileName.endsWith(".xls")) {
                readWorkBook(fileName, errorLogs, model);
            } else {
                errorLogs.add("The File Name should start with BlackList and must be .xls format.");
                model.put(BLACKLISTENTRIESDATA, errorLogs);
            }
        }
        Files.delete(file.toPath());
    }

    private void readWorkBook(String fileName, List<String> errorLogs, Map<String, Object> model) {
        Set<String> blackListEntries = new HashSet<>();
        Set<String> appendBlackListEntries = new HashSet<>();
        try (Workbook workbook = WorkbookFactory.create(new File(fileName))) {
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = datatypeSheet.iterator();
            readExcelRows(rowIterator, blackListEntries, appendBlackListEntries, errorLogs);
            if (errorLogs.size() == 1) {
                model.put(BLACKLISTENTRIESDATA, blackListEntries);
                model.put("appendBlackListEntries", appendBlackListEntries);
            } else {
                model.put(BLACKLISTENTRIESDATA, errorLogs);
            }
        } catch (Exception e) {
            String error = "Error Occured While Reading File. Please check the format of the file.";
            errorLogs.add(error);
            model.put(BLACKLISTENTRIESDATA, errorLogs);
            policyLogger.error(error + e);
        }
    }

    /**
     * This method is used to read all the rows from imported Excel sheet and set to respective objects.
     * 
     * @param rowIterator Excel Sheet rows are passed as input parameters.
     * @param blackListEntries the data is set to this object, which is going to be added.
     * @param appendBlackListEntries the data is set to this object which is going to be removed.
     * @param errorLogs on adding all incorrect entries, we can let user know what need to fixed.
     */
    private void readExcelRows(Iterator<Row> rowIterator, Set<String> blackListEntries,
            Set<String> appendBlackListEntries, List<String> errorLogs) {
        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();
            if (currentRow.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            readExcelCells(cellIterator, blackListEntries, appendBlackListEntries, errorLogs);
        }
    }

    private void readExcelCells(Iterator<Cell> cellIterator, Set<String> blackListEntries,
            Set<String> appendBlackListEntries, List<String> errorLogs) {
        boolean actionCheck = false;
        boolean blackListCheck = false;
        String blEntry = "";
        int actionEntry = 0;
        int lineNo = 1;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (ACTION.equalsIgnoreCase(getCellHeaderName(cell))) {
                ReturnBlackList returnList = readActionCell(cell, lineNo, errorLogs);
                actionEntry = returnList.getActionValue();
                actionCheck = returnList.isEntryCheck();
            }
            if (BLACKLISTENTRY.equalsIgnoreCase(getCellHeaderName(cell))) {
                ReturnBlackList returnList = readBlackListCell(cell, lineNo, errorLogs);
                blEntry = returnList.getEntryValue();
                blackListCheck = returnList.isEntryCheck();
                actionEntry = returnList.getActionValue();
            }
            lineNo++;
        }
        if (actionCheck && blackListCheck) {
            addBlackListEntries(actionEntry, blackListEntries, appendBlackListEntries, blEntry);
        }
    }

    private ReturnBlackList readActionCell(Cell cell, int lineNo, List<String> errorLogs) {
        ReturnBlackList returnValues = new ReturnBlackList();
        String error = "Entry at row " + lineNo + " not added, the value in the " + ACTION
                + "column is neither \"0\" nor \"1\"";
        int actionEntry = 0;
        try {
            actionEntry = (int) cell.getNumericCellValue();
            returnValues.setEntryCheck(true);
            if (actionEntry != 1 && actionEntry != 0) {
                errorLogs.add(error);
            }
        } catch (Exception e) {
            errorLogs.add(error);
            policyLogger.error(error + e.getMessage() + e);
            actionEntry = 0;
        }
        returnValues.setActionValue(actionEntry);
        return returnValues;
    }

    private ReturnBlackList readBlackListCell(Cell cell, int lineNo, List<String> errorLogs) {
        ReturnBlackList returnValues = new ReturnBlackList();
        String blEntry = "";
        try {
            blEntry = cell.getStringCellValue();
            returnValues.setEntryCheck(true);
        } catch (Exception e) {
            String error = "Entry at row " + lineNo + " not added, the value in the " + BLACKLISTENTRY
                    + " column is not a valid string";
            errorLogs.add(error);
            policyLogger.error(error + e.getMessage() + e);
            returnValues.setActionValue(0);
        }
        returnValues.setEntryValue(blEntry);
        return returnValues;
    }

    private void addBlackListEntries(int actionEntry, Set<String> blackListEntries, Set<String> appendBlackListEntries,
            String blEntry) {
        if (actionEntry == 1) {
            blackListEntries.add(blEntry);
        } else {
            appendBlackListEntries.add(blEntry);
        }
    }

    /**
     * This method is used to identify the header of the cell.
     * 
     * @param cell Excel sheet cell is passed as input parameter.
     * @return the column header name value
     */
    private String getCellHeaderName(Cell cell) {
        return cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue().toString();
    }
}
