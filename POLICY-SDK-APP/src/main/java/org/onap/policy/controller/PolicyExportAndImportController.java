/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2019, 2020 AT&T Intellectual Property. All rights reserved.
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.commons.lang3.StringUtils;
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
import org.onap.policy.rest.jpa.DcaeUuid;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.MicroServiceConfigName;
import org.onap.policy.rest.jpa.MicroServiceLocation;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.utils.UserUtils.Pair;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PolicyExportAndImportController extends RestrictedBaseController {

    private static Logger logger = FlexLogger.getLogger(PolicyExportAndImportController.class);

    private static String superAdmin = "super-admin";
    private static String superEditor = "super-editor";
    private static String admin = "admin";
    private static String editor = "editor";
    private static String policyName = "policyName";
    private static String configurationName = "configurationName";
    private static String configurationbody = "configurationbody";
    private static String config = "Config_";
    private static final String CONFIG_MS = "Config_MS_";
    private static final String NOTVALID = " is not valid.";
    private static final String POLICY = "Policy:";
    private static final String BODYSIZE = "bodySize";
    private static final String DECISION_MS = "Decision_MS_";
    private static final String ACTION = "Action_";
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

    public PolicyExportAndImportController() {
        // Empty constructor
    }

    @Autowired
    private PolicyExportAndImportController(CommonClassDao commonClassDao) {
        PolicyExportAndImportController.commonClassDao = commonClassDao;
    }

    /**
     * This is for downloading existing policy.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException error out
     */
    @RequestMapping(
            value = {"/policy_download/exportPolicy.htm"},
            method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public void exportPolicy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (HSSFWorkbook workBook2 = new HSSFWorkbook()) {
            ArrayList<String> selectedPolicy = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            PolicyExportAdapter adapter =
                    mapper.readValue(root.get("exportData").toString(), PolicyExportAdapter.class);
            for (Object policyId : adapter.getPolicyDatas()) {
                LinkedHashMap<?, ?> selected = (LinkedHashMap<?, ?>) policyId;
                String policyWithScope =
                        selected.get(policyName).toString() + "." + selected.get("activeVersion").toString() + ".xml";
                String scope = policyWithScope.substring(0, policyWithScope.lastIndexOf(File.separator))
                        .replace(File.separator, ".");
                String policyNamel = policyWithScope.substring(policyWithScope.lastIndexOf(File.separator) + 1);
                selectedPolicy.add(policyNamel + ":" + scope);
            }

            HSSFSheet sheet = workBook2.createSheet("PolicyEntity");
            HSSFRow headingRow = sheet.createRow(0);
            headingRow.createCell(0).setCellValue(policyName);
            headingRow.createCell(1).setCellValue("scope");
            headingRow.createCell(2).setCellValue("version");
            headingRow.createCell(3).setCellValue("policyData");
            headingRow.createCell(4).setCellValue("description");
            headingRow.createCell(5).setCellValue(configurationName);
            headingRow.createCell(6).setCellValue(BODYSIZE);
            headingRow.createCell(7).setCellValue(configurationbody);

            List<Object> entityData = commonClassDao.getMultipleDataOnAddingConjunction(PolicyEntity.class,
                    "policyName:scope", selectedPolicy);
            processEntityData(entityData, sheet, headingRow); //
            String tmp = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator + "temp";
            String deleteCheckPath = tmp + File.separator + "PolicyExport.xls";
            File deleteCheck = new File(deleteCheckPath);
            if (deleteCheck.exists() && deleteCheck.delete()) {
                logger.info("Deleted the file from system before exporting a new file.");
            }
            File temPath = new File(tmp);
            if (!temPath.exists()) {
                temPath.mkdir();
            }

            String file = temPath + File.separator + "PolicyExport.xls";
            File filepath = new File(file);
            FileOutputStream fos = new FileOutputStream(filepath);
            workBook2.write(fos);
            fos.flush();

            response.setContentType(PolicyUtils.APPLICATION_JSON);
            request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);

            PrintWriter out = response.getWriter();
            String successMap = file.substring(file.lastIndexOf("webapps") + 8);
            String responseString = mapper.writeValueAsString(successMap);
            JSONObject json = new JSONObject("{data: " + responseString + "}");
            out.write(json.toString());
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Exception Occured while Exporting Policies" + e);
        }
    }

    private void processEntityData(List<Object> entityData, HSSFSheet sheet, HSSFRow headingRow) {

        short rowNo = 1;
        for (Object object : entityData) {
            PolicyEntity policyEntity = (PolicyEntity) object;
            HSSFRow row = sheet.createRow(rowNo);
            row.createCell(0).setCellValue(policyEntity.getPolicyName());
            row.createCell(1).setCellValue(policyEntity.getScope());
            row.createCell(2).setCellValue(policyEntity.getVersion());
            row.createCell(3).setCellValue(policyEntity.getPolicyData());
            row.createCell(4).setCellValue(policyEntity.getDescription());
            if (policyEntity.getPolicyName().contains(DECISION_MS)
                    || !policyEntity.getPolicyName().contains("Decision_")) {
                if (policyEntity.getConfigurationData() != null) {
                    row.createCell(5).setCellValue(policyEntity.getConfigurationData().getConfigurationName());
                    String body = policyEntity.getConfigurationData().getConfigBody();
                    row = populateConfigParam(policyEntity, row, headingRow, body);
                }
                populateActionBodyEntity(policyEntity, row);
            } else {
                row.createCell(5).setCellValue("");
                row.createCell(6).setCellValue(0);
                row.createCell(7).setCellValue("");
            }
            rowNo++;
        }
    }

    private HSSFRow populateActionBodyEntity(PolicyEntity policyEntity, HSSFRow row) {
        if (policyEntity.getActionBodyEntity() != null) {
            row.createCell(5).setCellValue(policyEntity.getActionBodyEntity().getActionBodyName());
            row.createCell(6).setCellValue(0);
            row.createCell(7).setCellValue(policyEntity.getActionBodyEntity().getActionBody());
        }
        return row;
    }

    private HSSFRow populateConfigParam(PolicyEntity policyEntity, HSSFRow row, HSSFRow headingRow, String body) {

        if (policyEntity.getPolicyName().contains("Config_BRMS_Param_")) {
            int index = 0;
            int arraySize = 0;
            while (index < body.length()) {
                if (arraySize == 0) {
                    row.createCell(7).setCellValue(body.substring(index, Math.min(index + 30000, body.length())));
                } else {
                    headingRow.createCell(7 + arraySize).setCellValue(configurationbody + arraySize);
                    row.createCell(7 + arraySize)
                            .setCellValue(body.substring(index, Math.min(index + 30000, body.length())));
                }
                index += 30000;
                arraySize += 1;
            }
            row.createCell(6).setCellValue(arraySize);
        } else {
            row.createCell(6).setCellValue(0);
            row.createCell(7).setCellValue(body);
        }
        return row;
    }

    /**
     * This is to upload a policy and save it to database.
     *
     * @param file String
     * @param request HttpServletRequest
     * @return JSONObject
     * @throws IOException error out
     */
    public String importRepositoryFile(String file, HttpServletRequest request) throws IOException {
        boolean configExists = false;
        boolean actionExists = false;
        String configName = null;
        boolean finalColumn;
        PolicyController controller = policyController != null ? getPolicyController() : new PolicyController();
        String userId = UserUtils.getUserSession(request).getOrgUserId();
        UserInfo userInfo = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", userId);

        // Check if the Role and Scope Size are Null get the values from db.
        List<Object> userRoles = controller.getRoles(userId);
        Pair<Set<String>, List<String>> pair = org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
        List<String> roles = pair.second;
        Set<String> scopes = pair.first;

        try (FileInputStream excelFile = new FileInputStream(new File(file));
                HSSFWorkbook workbook = new HSSFWorkbook(excelFile)) {
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = datatypeSheet.iterator();
            String sendResult = null;
            while (rowIterator.hasNext()) {
                finalColumn = false;
                PolicyEntity policyEntity = new PolicyEntity();
                ConfigurationDataEntity configurationDataEntity = new ConfigurationDataEntity();
                ActionBodyEntity actionBodyEntity = new ActionBodyEntity();
                Row currentRow = rowIterator.next();
                if (currentRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = currentRow.cellIterator();
                StringBuilder body = new StringBuilder();
                int bodySize = 0;
                int setBodySize = 0;
                boolean configurationBodySet = false;
                boolean errorFlag = false;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (policyName.equalsIgnoreCase(getCellHeaderName(cell))) {
                        policyEntity.setPolicyName(cell.getStringCellValue());
                        finalColumn = false;
                        configurationBodySet = false;
                        configExists = false;
                        actionExists = false;
                    }
                    policyEntity = populatePolicyEntity(cell, policyEntity);
                    setBodySize = getSetBodySize(policyEntity, cell, setBodySize);
                    finalColumn = isFinalColumn(policyEntity, cell, setBodySize, setBodySize, finalColumn);
                    configurationBodySet =
                            isConfigurationBodySet(policyEntity, cell, setBodySize, setBodySize, configurationBodySet);
                    configExists = isConfigExists(policyEntity, cell, configExists);
                    body = addCellValue(policyEntity, cell, body);
                    actionExists = isActionExists(policyEntity, cell, actionExists);
                    actionBodyEntity = setActionBodyObject(policyEntity, actionBodyEntity, cell);
                    bodySize = getBobySize(bodySize, cell); //
                    configName = getConfigName(cell, configName);
                    configurationDataEntity =
                            populateConfigurationDataEntity(policyEntity, configurationDataEntity, cell);
                    actionBodyEntity = populateActionBodyObject(policyEntity, actionBodyEntity, cell);
                    String response = null;
                    response = validatRequiredValue(policyEntity, body, finalColumn, configurationBodySet);
                    if (!StringUtils.isBlank(response)) {
                        sendResult = sendResult + "\n" + response;
                        errorFlag = true;
                    }
                    if (!StringUtils.isBlank(response) && rowIterator.hasNext() == false) {
                        return sendResult;
                    }
                    savePolicyEntiies(finalColumn, configurationBodySet, configurationDataEntity, policyEntity,
                            controller, roles, userInfo, scopes, configName, userId, configExists, actionExists,
                            actionBodyEntity, body, errorFlag);

                }
            }
        } catch (IOException e) {
            logger.error("Exception Occured While importing the Policy" + e);
        }
        return null;
    }

    private void writeConfigurationFile(ConfigurationDataEntity configurationDataEntity) {
        try (FileWriter fw = new FileWriter(
                PolicyController.getConfigHome() + File.separator + configurationDataEntity.getConfigurationName())) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(configurationDataEntity.getConfigBody());
            bw.close();
        } catch (IOException e) {
            logger.error("Exception Occured While cloning the configuration file", e);
        }
    }

    private void writeActionBodyFile(ActionBodyEntity actionBodyEntity) {
        try (FileWriter fw = new FileWriter(
                PolicyController.getActionHome() + File.separator + actionBodyEntity.getActionBodyName())) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(actionBodyEntity.getActionBody());
            bw.close();
        } catch (IOException e) {
            logger.error("Exception Occured While cloning the configuration file", e);
        }
    }

    // return the column header name value
    private String getCellHeaderName(Cell cell) {
        return cell.getSheet().getRow(0).getCell(cell.getColumnIndex()).getRichStringCellValue().toString();
    }

    /**
     * This is to validate all matching required fields.
     *
     * @param policyName String
     * @param jsonString String
     * @return String
     */
    public String validatMatchRequiredFields(String policyName, String jsonString) {
        String errorMsg = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String confErorMsg = validConfigName(jsonObject.getString("configName"));
            if (!confErorMsg.isEmpty()) {
                errorMsg = errorMsg + "\n POLICY :" + policyName + " at " + confErorMsg;
            }
            String uuidErorMsg = validUuid(jsonObject.getString("uuid"));
            if (!uuidErorMsg.isEmpty()) {
                errorMsg = errorMsg + "\n POLICY :" + policyName + " at " + uuidErorMsg;
            }
            String locErorMsg = validLocation(jsonObject.getString("location"));
            if (!locErorMsg.isEmpty()) {
                errorMsg = errorMsg + "\n POLICY :" + policyName + " at " + locErorMsg;
            }
            String pScopeErorMsg = validPolicyScope(jsonObject.getString("policyScope"));
            if (!pScopeErorMsg.isEmpty()) {
                errorMsg = errorMsg + "\n POLICY :" + policyName + " at " + pScopeErorMsg;
            }
            String msVersion = jsonObject.getString("version");
            String msService = jsonObject.getString("service");
            if (!isAttributeObjectFound(msService, msVersion)) {
                errorMsg = errorMsg + "\n POLICY :" + policyName + " at MS Service: " + msService + " and MS Version: "
                        + msVersion + NOTVALID;
            }

        } catch (Exception e) {
            logger.error("Exception Occured While validating required fields", e);
        }

        return errorMsg;
    }

    private String validatRequiredValue(PolicyEntity policyEntity, StringBuilder body, boolean finalColumn,
            boolean configurationBodySet) {
        if (finalColumn && configurationBodySet && (policyEntity.getPolicyName().contains(CONFIG_MS))) {
            String errorMsg = validatMatchRequiredFields(policyEntity.getPolicyName(), body.toString());
            if (errorMsg != null) {
                logger.error("errorMsg => " + errorMsg);
                JSONObject response = new JSONObject();
                response.append("error", errorMsg);
                return errorMsg;
            }
        }
        return null;
    }

    private String validConfigName(String configName) {
        String message = null;
        if (configName != null) {
            List<String> configNames = commonClassDao.getDataByColumn(MicroServiceConfigName.class, "name");
            if (configNames != null
                    && (!(configNames.stream().filter(o -> o.equals(configName)).findFirst().isPresent()))) {
                message = POLICY + policyName + " configName: " + configName + NOTVALID;
            }
        } else {
            message = POLICY + policyName + "configName is null";
        }
        return message;
    }

    private String validUuid(String uuid) {
        String message = null;
        if (uuid != null) {
            List<String> uuids = commonClassDao.getDataByColumn(DcaeUuid.class, "name");
            if (uuids != null && !(uuids.stream().filter(o -> o.equals(uuid)).findFirst().isPresent())) {
                message = POLICY + policyName + " uuid: " + uuid + NOTVALID;
            }
        } else {
            message = POLICY + policyName + "uuid is null";
        }
        return message;
    }

    private String validLocation(String location) {
        String message = null;
        if (location != null) {
            List<String> locations = commonClassDao.getDataByColumn(MicroServiceLocation.class, "name");
            if ((locations != null && !(locations.stream().filter(o -> o.equals(location)).findFirst().isPresent()))) {
                message = POLICY + policyName + " location: " + location + NOTVALID;
            }
        } else {
            message = POLICY + policyName + "location is null";
        }
        return message;
    }

    private String validPolicyScope(String policyScope) {
        String message = null;
        if (policyScope != null) {
            List<Object> foundData =
                    commonClassDao.checkDuplicateEntry(policyScope, "groupList", GroupPolicyScopeList.class);
            if (foundData == null || foundData.isEmpty()) {
                message = POLICY + policyName + " policyScope: " + policyScope + NOTVALID;
            }
        } else {
            message = POLICY + policyName + "policyScope is null";
        }
        return message;
    }

    private boolean isAttributeObjectFound(String msService, String msVersion) {
        if (msService == null) {
            return false;
        }

        if (msVersion == null) {
            return false;
        }
        MicroServiceModels workingModel = null;
        List<Object> microServiceModelsData =
                commonClassDao.getDataById(MicroServiceModels.class, "modelName", msService);
        if (microServiceModelsData != null) {
            for (int i = 0; i < microServiceModelsData.size(); i++) {
                workingModel = (MicroServiceModels) microServiceModelsData.get(i);
                if (workingModel != null && workingModel.getVersion() != null
                        && workingModel.getVersion().equals(msVersion)) {
                    return true;
                }
            }
        }
        return false;
    }

    private PolicyEntity populatePolicyEntity(Cell cell, PolicyEntity policyEntityObject) {

        if (policyEntityObject == null) {
            return null;
        }

        PolicyEntity policyEntity = policyEntityObject;
        if ("scope".equalsIgnoreCase(getCellHeaderName(cell))) {
            policyEntity.setScope(cell.getStringCellValue());
        }
        if ("policyData".equalsIgnoreCase(getCellHeaderName(cell))) {
            policyEntity.setPolicyData(cell.getStringCellValue());
        }
        if ("description".equalsIgnoreCase(getCellHeaderName(cell))) {
            policyEntity.setDescription(cell.getStringCellValue());
        }

        return policyEntity;
    }

    private void saveConfigurePolicy(String configName, ConfigurationDataEntity configurationDataEntity, String userId,
            boolean configExists) {
        if (configExists) {
            if (configName.endsWith("json")) {
                configurationDataEntity.setConfigType("JSON");
            } else if (configName.endsWith("txt")) {
                configurationDataEntity.setConfigType("OTHER");
            } else if (configName.endsWith("xml")) {
                configurationDataEntity.setConfigType("XML");
            } else if (configName.endsWith("properties")) {
                configurationDataEntity.setConfigType("PROPERTIES");
            }
            configurationDataEntity.setDeleted(false);
            configurationDataEntity.setCreatedBy(userId);
            configurationDataEntity.setModifiedBy(userId);
            commonClassDao.save(configurationDataEntity);
            writeConfigurationFile(configurationDataEntity);
        }
    }

    private void saveActionPolicy(ActionBodyEntity actionBodyEntity, String userId, boolean actionExists) {
        if (actionExists) {
            actionBodyEntity.setDeleted(false);
            actionBodyEntity.setCreatedBy(userId);
            actionBodyEntity.setModifiedBy(userId);
            commonClassDao.save(actionBodyEntity);
            writeActionBodyFile(actionBodyEntity);
        }
    }

    private boolean isContinue(List<String> roles, String scope, UserInfo userInfo) {
        if (roles.contains(superAdmin) || roles.contains(superEditor)) {
            // 1. if Role contains super admin create scope.
            // 2. if Role contains super editor don't create new scope and add to list to show to user.
            PolicyEditorScopes policyEditorScope =
                    (PolicyEditorScopes) commonClassDao.getEntityItem(PolicyEditorScopes.class, "scopeName", scope);
            if (policyEditorScope == null) {
                if (roles.contains(superAdmin)) {
                    PolicyEditorScopes policyEditorScopeEntity = new PolicyEditorScopes();
                    policyEditorScopeEntity.setScopeName(scope);
                    policyEditorScopeEntity.setUserCreatedBy(userInfo);
                    policyEditorScopeEntity.setUserModifiedBy(userInfo);
                    commonClassDao.save(policyEditorScopeEntity);
                } else {
                    // Add Error Message a new Scope Exists, contact super-admin to create a new scope
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean isContinue(List<String> roles, String scope, UserInfo userInfo, Set scopes) {
        if (roles.contains(admin) || roles.contains(editor)) {
            if (scopes.isEmpty()) {
                logger.error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
            } else {
                // 1. if Role contains admin, then check if parent scope has role admin, if not don't
                // create a scope and add to list.
                if (roles.contains(admin)) {
                    String scopeCheck = scope.substring(0, scope.lastIndexOf('.'));
                    if (scopes.contains(scopeCheck)) {
                        PolicyEditorScopes policyEditorScopeEntity = new PolicyEditorScopes();
                        policyEditorScopeEntity.setScopeName(scope);
                        policyEditorScopeEntity.setUserCreatedBy(userInfo);
                        policyEditorScopeEntity.setUserModifiedBy(userInfo);
                        commonClassDao.save(policyEditorScopeEntity);
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isContinue(List<Object> queryData, List<String> roles, String scope, UserInfo userInfo,
            Set<String> scopes) {
        if (!queryData.isEmpty()) {
            return true;
        }
        if (isContinue(roles, scope, userInfo)) {
            return true;
        }
        return isContinue(roles, scope, userInfo, scopes);
    }

    private void savePolicyEntity(PolicyEntity policyEntity, String configName, String userId) {
        if (configName != null) {
            if (configName.contains(config) || configName.contains(DECISION_MS)) {
                ConfigurationDataEntity configuration = (ConfigurationDataEntity) commonClassDao
                        .getEntityItem(ConfigurationDataEntity.class, configurationName, configName);
                policyEntity.setConfigurationData(configuration);
            } else {
                ActionBodyEntity actionBody = (ActionBodyEntity) commonClassDao.getEntityItem(ActionBodyEntity.class,
                        "actionBodyName", configName);
                policyEntity.setActionBodyEntity(actionBody);
            }
        }
        policyEntity.setCreatedBy(userId);
        policyEntity.setModifiedBy(userId);
        policyEntity.setDeleted(false);
        commonClassDao.save(policyEntity);
    }

    private void saveVersion(PolicyEntity policyEntity, String scope, String userId) {
        PolicyVersion policyVersion = new PolicyVersion();
        String policyNamel = policyEntity.getPolicyName().replace(".xml", "");
        int version = Integer.parseInt(policyNamel.substring(policyNamel.lastIndexOf('.') + 1));
        policyNamel = policyNamel.substring(0, policyNamel.lastIndexOf('.'));

        policyVersion.setPolicyName(scope.replace(".", File.separator) + File.separator + policyNamel);
        policyVersion.setActiveVersion(version);
        policyVersion.setHigherVersion(version);
        policyVersion.setCreatedBy(userId);
        policyVersion.setModifiedBy(userId);
        commonClassDao.save(policyVersion);
    }

    private int getSetBodySize(PolicyEntity policyEntity, Cell cell, int setBodySize) {
        int setBodySizel = setBodySize;
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && ((policyEntity.getPolicyName().contains(config)
                        || policyEntity.getPolicyName().contains(DECISION_MS))
                        && (policyEntity.getPolicyName().contains("Config_BRMS_Param_")))) {
            setBodySizel += 1;
        }
        return setBodySizel;
    }

    private boolean isFinalColumn(PolicyEntity policyEntity, Cell cell, int setBodySize, int bodySize,
            boolean finalColumn) {
        boolean finalColumnl = finalColumn;
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(config) || policyEntity.getPolicyName().contains(DECISION_MS))
                && setBodySize == bodySize) {
            finalColumnl = true;
        }
        if (BODYSIZE.equalsIgnoreCase(getCellHeaderName(cell)) && cell.getNumericCellValue() < 1) {
            finalColumnl = true;
        }
        return finalColumnl;
    }

    private boolean isConfigurationBodySet(PolicyEntity policyEntity, Cell cell, int setBodySize, int bodySize,
            boolean configurationBodySet) {
        boolean configurationBodySetl = configurationBodySet;
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(config) || policyEntity.getPolicyName().contains(DECISION_MS))
                && (setBodySize == bodySize)) {
            configurationBodySetl = true;
        }
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(config) || policyEntity.getPolicyName().contains(DECISION_MS))
                && (setBodySize == 0)) {
            configurationBodySetl = true;
        }
        return configurationBodySetl;
    }

    private boolean isConfigExists(PolicyEntity policyEntity, Cell cell, boolean configExists) {
        boolean configExistsl = configExists;
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(config)
                        || policyEntity.getPolicyName().contains(DECISION_MS))) {
            configExistsl = true;
        }
        return configExistsl;
    }

    private boolean isActionExists(PolicyEntity policyEntity, Cell cell, boolean actionExists) {
        boolean actionExistsl = actionExists;
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && policyEntity.getPolicyName().contains(ACTION)) {
            actionExistsl = true;
        }
        return actionExistsl;
    }

    private int getBobySize(int bodySize, Cell cell) {
        int bodySizel = bodySize;
        if (BODYSIZE.equalsIgnoreCase(getCellHeaderName(cell)) && cell.getNumericCellValue() >= 1) {
            bodySizel = (int) cell.getNumericCellValue();
        }
        return bodySizel;
    }

    private StringBuilder addCellValue(PolicyEntity policyEntity, Cell cell, StringBuilder body) {
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(config)
                        || policyEntity.getPolicyName().contains(DECISION_MS))) {
            body.append(cell.getStringCellValue());
        }

        return body;
    }

    private ActionBodyEntity setActionBodyObject(PolicyEntity policyEntity, ActionBodyEntity actionBodyEntity,
            Cell cell) {
        if (configurationbody.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(ACTION))) {
            actionBodyEntity.setActionBody(cell.getStringCellValue());
        }
        return actionBodyEntity;
    }

    private ActionBodyEntity populateActionBodyObject(PolicyEntity policyEntity, ActionBodyEntity actionBodyEntity,
            Cell cell) {
        if (configurationName.equalsIgnoreCase(getCellHeaderName(cell))
                && policyEntity.getPolicyName().contains(ACTION)) {
            actionBodyEntity.setActionBodyName(cell.getStringCellValue());
        }
        return actionBodyEntity;
    }

    private ConfigurationDataEntity populateConfigurationDataEntity(PolicyEntity policyEntity,
            ConfigurationDataEntity configurationDataEntity, Cell cell) {
        if (configurationName.equalsIgnoreCase(getCellHeaderName(cell))
                && (policyEntity.getPolicyName().contains(config)
                        || policyEntity.getPolicyName().contains(DECISION_MS))) {
            configurationDataEntity.setConfigurationName(cell.getStringCellValue());
        }
        return configurationDataEntity;
    }

    private String getConfigName(Cell cell, String configName) {
        String configNameL = configName;
        if (configurationName.equalsIgnoreCase(getCellHeaderName(cell))) {
            configNameL = cell.getStringCellValue();
        }
        return configNameL;
    }

    private void savePolicyEntiies(boolean finalColumn, boolean configurationBodySet,
            ConfigurationDataEntity configurationDataEntity, PolicyEntity policyEntity, PolicyController controller,
            List<String> roles, UserInfo userInfo, Set<String> scopes, String configName, String userId,
            boolean configExists, boolean actionExists, ActionBodyEntity actionBodyEntity, StringBuilder body, boolean errorFlagSent) {

        if (finalColumn && configurationBodySet && errorFlagSent == false) {
            configurationDataEntity.setConfigBody(body.toString());
            String scope = policyEntity.getScope().replace(".", File.separator);
            String query = "FROM PolicyEntity where policyName = :policyName and scope = :policyScope";
            SimpleBindings params = new SimpleBindings();
            params.put(policyName, policyEntity.getPolicyName());
            params.put("policyScope", policyEntity.getScope());
            List<Object> queryData = controller.getDataByQuery(query, params);

            if (isContinue(queryData, roles, scope, userInfo, scopes)) {
                return;
            }
            saveConfigurePolicy(configName, configurationDataEntity, userId, configExists); //
            saveActionPolicy(actionBodyEntity, userId, actionExists); //
            savePolicyEntity(policyEntity, configName, userId);//
            saveVersion(policyEntity, scope, userId); //
            // Notify Other paps regarding Export Policy.
            PolicyRestController restController = new PolicyRestController();
            restController.notifyOtherPapsToUpdateConfigurations("exportPolicy", configName, null);
        }
    }
}
