/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2020 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
 * Modifications Copyright (C) 2019 Bell Canada
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

package org.onap.policy.admin;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.script.SimpleBindings;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpStatus;
import org.elasticsearch.common.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.components.HumanPolicyComponent;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.controller.PolicyExportAndImportController;
import org.onap.policy.rest.XacmlRest;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PeCryptoUtils;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.utils.UserUtils.Pair;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.util.XACMLPolicyScanner;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.web.support.UserUtils;

@WebServlet(
        value = "/fm/*",
        loadOnStartup = 1,
        initParams = {@WebInitParam(
                name = "XACML_PROPERTIES_NAME",
                value = "xacml.admin.properties",
                description = "The location of the properties file holding configuration information.")})
public class PolicyManagerServlet extends HttpServlet {
    private static final Logger LOGGER = FlexLogger.getLogger(PolicyManagerServlet.class);
    private static final long serialVersionUID = -8453502699403909016L;
    private static final String VERSION = "version";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String SIZE = "size";
    private static final String TYPE = "type";
    private static final String ROLETYPE = "roleType";
    private static final String CREATED_BY = "createdBy";
    private static final String MODIFIED_BY = "modifiedBy";
    private static final String CONTENTTYPE = "application/json";
    private static final String SUPERADMIN = "super-admin";
    private static final String SUPEREDITOR = "super-editor";
    private static final String SUPERGUEST = "super-guest";
    private static final String ADMIN = "admin";
    private static final String EDITOR = "editor";
    private static final String GUEST = "guest";
    private static final String RESULT = "result";
    private static final String DELETE = "delete";
    private static final String EXCEPTION_OCCURED = "Exception Occured";
    private static final String CONFIG = ".Config_";
    private static final String CONFIG1 = ":Config_";
    private static final String ACTION = ".Action_";
    private static final String ACTION1 = ":Action_";
    private static final String DECISION = ".Decision_";
    private static final String DECISION1 = ":Decision_";
    private static final String CONFIG2 = "Config_";
    private static final String ACTION2 = "Action_";
    private static final String DECISION2 = "Decision_";
    private static final String FORWARD_SLASH = "/";
    private static final String BACKSLASH = "\\";
    private static final String ESCAPE_BACKSLASH = "\\\\";
    private static final String BACKSLASH_8TIMES = "\\\\\\\\";
    private static final String DELETE_POLICY_VERSION_WHERE_POLICY_NAME =
            "delete from PolicyVersion where policy_name ='";
    private static final String UPDATE_POLICY_VERSION_SET_ACTIVE_VERSION = "update PolicyVersion set active_version='";
    private static final String SPLIT_1 = "split_1";
    private static final String SPLIT_0 = "split_0";
    private static final String FROM_POLICY_EDITOR_SCOPES_WHERE_SCOPENAME_LIKE_SCOPE_NAME =
            "from PolicyEditorScopes where SCOPENAME like :scopeName";
    private static final String SCOPE_NAME = "scopeName";
    private static final String SUCCESS = "success";
    private static final String SUB_SCOPENAME = "subScopename";
    private static final String ALLSCOPES = "@All@";
    private static final String PERCENT_AND_ID_GT_0 = "%' and id >0";
    private static List<String> serviceTypeNamesList = new ArrayList<>();
    private static JsonArray policyNames;
    private static String testUserId = null;

    private enum Mode {
        LIST,
        RENAME,
        COPY,
        DELETE,
        EDITFILE,
        ADDFOLDER,
        DESCRIBEPOLICYFILE,
        VIEWPOLICY,
        ADDSUBSCOPE,
        SWITCHVERSION,
        EXPORT,
        SEARCHLIST
    }

    private static PolicyController policyController;

    private synchronized PolicyController getPolicyController() {
        return policyController;
    }

    public static synchronized void setPolicyController(PolicyController policyController) {
        PolicyManagerServlet.policyController = policyController;
    }

    public static JsonArray getPolicyNames() {
        return policyNames;
    }

    public static void setPolicyNames(JsonArray policyNames) {
        PolicyManagerServlet.policyNames = policyNames;
    }

    public static List<String> getServiceTypeNamesList() {
        return serviceTypeNamesList;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        //
        // Common initialization
        //
        XacmlRest.xacmlInit(servletConfig);
        // init aes key from prop or env
        PeCryptoUtils.initAesKey(XACMLProperties.getProperty(XacmlRestProperties.PROP_AES_KEY));
        //
        // Initialize ClosedLoop JSON
        //
        PolicyManagerServlet.initializeJsonLoad();
    }

    private static void initializeJsonLoad() {
        Path closedLoopJsonLocation = Paths.get(XACMLProperties.getProperty(XacmlRestProperties.PROP_ADMIN_CLOSEDLOOP));
        String location = closedLoopJsonLocation.toString();
        if (!location.endsWith("json")) {
            LOGGER.warn("JSONConfig file does not end with extension .json");
            return;
        }
        try (FileInputStream inputStream = new FileInputStream(location);
                JsonReader jsonReader = Json.createReader(inputStream)) {
            policyNames = jsonReader.readArray();
            serviceTypeNamesList = new ArrayList<>();
            for (int i = 0; i < policyNames.size(); i++) {
                javax.json.JsonObject policyName = policyNames.getJsonObject(i);
                String name = policyName.getJsonString("serviceTypePolicyName").getString();
                serviceTypeNamesList.add(name);
            }
        } catch (IOException e) {
            LOGGER.error("Exception Occured while initializing the JSONConfig file" + e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("doPost");
        try {
            // if request contains multipart-form-data
            if (isMultipartContent(request)) {
                uploadFile(request, response);
            }
            // all other post request has json params in body
            else {
                fileOperation(request, response);
            }
        } catch (Exception e) {
            try {
                setError(e, response);
            } catch (Exception e1) {
                LOGGER.error(EXCEPTION_OCCURED + e1);
            }
        }
    }

    protected boolean isMultipartContent(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    // Set Error Message for Exception
    private void setError(Exception exception, HttpServletResponse response) throws IOException {
        try {
            JSONObject responseJsonObject = error(exception.getMessage());
            response.setContentType(CONTENTTYPE);
            PrintWriter out = response.getWriter();
            out.print(responseJsonObject);
            out.flush();
        } catch (Exception x) {
            LOGGER.error(EXCEPTION_OCCURED + x);
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, x.getMessage());
        }
    }

    // Policy Import Functionality
    private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            Map<String, InputStream> files = new HashMap<>();

            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    // Process form file field (input type="file").
                    files.put(item.getName(), item.getInputStream());
                    processFormFile(request, item);
                }
            }

            JSONObject responseJsonObject;
            responseJsonObject = this.success();
            response.setContentType(CONTENTTYPE);
            PrintWriter out = response.getWriter();
            out.print(responseJsonObject);
            out.flush();
        } catch (Exception e) {
            LOGGER.debug("Cannot write file");
            throw new ServletException("Cannot write file", e);
        }
    }

    private void processFormFile(HttpServletRequest request, FileItem item) {
        String newFile;
        if (item.getName().endsWith(".xls") && item.getSize() <= getFileSizeLimit()) {
            File file = new File(item.getName());
            try (OutputStream outputStream = new FileOutputStream(file)) {
                copyStream(item.getInputStream(), outputStream);
                newFile = file.toString();
                PolicyExportAndImportController importController = new PolicyExportAndImportController();
                importController.importRepositoryFile(newFile, request);
            } catch (Exception e) {
                LOGGER.error("Upload error : " + e);
            }
        } else if (!item.getName().endsWith(".xls")) {
            LOGGER.error("Non .xls filetype uploaded: " + item.getName());
        } else { // uploaded file size is greater than allowed
            LOGGER.error("Upload file size limit exceeded! File size (Bytes) is: " + item.getSize());
        }
    }

    protected long copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return IOUtils.copy(inputStream, outputStream);
    }

    protected long getFileSizeLimit() {
        return PolicyController.getFileSizeLimit();
    }

    // File Operation Functionality
    private void fileOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject responseJsonObject;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While doing File Operation" + e);
            responseJsonObject = error(e.getMessage());
            setResponse(response, responseJsonObject);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONObject params = jsonObject.getJSONObject("params");
            Mode mode = Mode.valueOf(params.getString("mode"));

            String userId = getUserSession(request).getOrgUserId();
            LOGGER.info(
                    "********************Logging UserID while doing actions on Editor tab****************************");
            LOGGER.info(
                    "UserId:  " + userId + "Action Mode:  " + mode.toString() + "Action Params: " + params.toString());
            LOGGER.info(
                    "************************************************************************************************");
            responseJsonObject = operateBasedOnMode(mode, params, request);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Processing Json" + e);
            responseJsonObject = error(e.getMessage());
        }
        setResponse(response, responseJsonObject);
    }

    protected User getUserSession(HttpServletRequest request) {
        return UserUtils.getUserSession(request);
    }

    private void setResponse(HttpServletResponse response, JSONObject responseJsonObject) {
        response.setContentType(CONTENTTYPE);
        try (PrintWriter out = response.getWriter()) {
            out.print(responseJsonObject);
            out.flush();
        } catch (IOException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception occured while writing response" + e);
        }
    }

    private JSONObject operateBasedOnMode(Mode mode, JSONObject params, HttpServletRequest request)
            throws ServletException {
        JSONObject responseJsonObject;
        switch (mode) {
            case ADDFOLDER:
            case ADDSUBSCOPE:
                responseJsonObject = addFolder(params, request);
                break;
            case COPY:
                responseJsonObject = copy(params, request);
                break;
            case DELETE:
                responseJsonObject = delete(params, request);
                break;
            case EDITFILE:
            case VIEWPOLICY:
                responseJsonObject = editFile(params);
                break;
            case LIST:
                responseJsonObject = list(params, request);
                break;
            case RENAME:
                responseJsonObject = rename(params, request);
                break;
            case DESCRIBEPOLICYFILE:
                responseJsonObject = describePolicy(params);
                break;
            case SWITCHVERSION:
                responseJsonObject = switchVersion(params, request);
                break;
            case SEARCHLIST:
                responseJsonObject = searchPolicyList(params, request);
                break;
            default:
                throw new ServletException("not implemented");
        }
        if (responseJsonObject == null) {
            responseJsonObject = error("generic error : responseJsonObject is null");
        }
        return responseJsonObject;
    }

    private JSONObject searchPolicyList(JSONObject params, HttpServletRequest request) {
        JSONArray policyList = null;
        if (params.has("policyList")) {
            policyList = (JSONArray) params.get("policyList");
        }
        PolicyController controller = getPolicyControllerInstance();
        List<JSONObject> resultList = new ArrayList<>();
        try {
            if (!lookupPolicyData(request, new ArrayList<>(), policyList, controller, resultList)) {
                return error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
            }
        } catch (Exception e) {
            LOGGER.error(
                    "Exception occured while reading policy Data from Policy Version table for Policy Search Data" + e);
        }

        return new JSONObject().put(RESULT, resultList);
    }

    private boolean lookupPolicyData(HttpServletRequest request, List<Object> policyData, JSONArray policyList,
            PolicyController controller, List<JSONObject> resultList) {
        String userId = getUserSession(request).getOrgUserId();
        List<Object> userRoles = controller.getRoles(userId);
        Pair<Set<String>, List<String>> pair = checkRoleAndScope(userRoles);
        List<String> roles = pair.second;
        Set<String> scopes = pair.first;
        if (roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST)) {
            if (scopes.isEmpty()) {
                return false;
            }
            for (String scope : scopes) {
                addScope(scopes, scope);
            }
        }
        if (policyList != null) {
            for (int i = 0; i < policyList.length(); i++) {
                String policyName = policyList.get(i).toString().replace(".xml", "");
                String version = policyName.substring(policyName.lastIndexOf('.') + 1);
                policyName = policyName.substring(0, policyName.lastIndexOf('.')).replace(".", File.separator);
                parsePolicyList(resultList, controller, policyName, version);
            }
        } else {
            getPolicyDataForSuperRoles(policyData, controller, resultList, roles, scopes);
        }
        return true;
    }

    protected Pair<Set<String>, List<String>> checkRoleAndScope(List<Object> userRoles) {
        return org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
    }

    private void getPolicyDataForSuperRoles(List<Object> policyData, PolicyController controller,
            List<JSONObject> resultList, List<String> roles, Set<String> scopes) {
        if (roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)) {
            policyData = controller.getData(PolicyVersion.class);
        } else {
            List<Object> filterData = controller.getData(PolicyVersion.class);
            for (Object filter : filterData) {
                addFilterData(policyData, scopes, (PolicyVersion) filter);
            }
        }

        if (!policyData.isEmpty()) {
            updateResultList(policyData, resultList);
        }
    }

    private void addFilterData(List<Object> policyData, Set<String> scopes, PolicyVersion filter) {
        try {
            String scopeName = filter.getPolicyName().substring(0, filter.getPolicyName().lastIndexOf(File.separator));
            if (scopes.contains(scopeName)) {
                policyData.add(filter);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured while filtering policyversion data" + e);
        }
    }

    private void updateResultList(List<Object> policyData, List<JSONObject> resultList) {
        for (Object data : policyData) {
            PolicyVersion policy = (PolicyVersion) data;
            JSONObject el = new JSONObject();
            el.put(NAME, policy.getPolicyName().replace(File.separator, FORWARD_SLASH));
            el.put(DATE, policy.getModifiedDate());
            el.put(VERSION, policy.getActiveVersion());
            el.put(SIZE, "");
            el.put(TYPE, "file");
            el.put(CREATED_BY, getUserName(policy.getCreatedBy()));
            el.put(MODIFIED_BY, getUserName(policy.getModifiedBy()));
            resultList.add(el);
        }
    }

    private void parsePolicyList(List<JSONObject> resultList, PolicyController controller, String policyName,
            String version) {
        if (policyName.contains(BACKSLASH)) {
            policyName = policyName.replace(BACKSLASH, ESCAPE_BACKSLASH);
        }
        String policyVersionQuery =
                "From PolicyVersion where policy_name = :policyName  and active_version = :version and id >0";
        SimpleBindings pvParams = new SimpleBindings();
        pvParams.put("policyName", policyName);
        pvParams.put(VERSION, version);
        List<Object> activeData = controller.getDataByQuery(policyVersionQuery, pvParams);
        if (!activeData.isEmpty()) {
            PolicyVersion policy = (PolicyVersion) activeData.get(0);
            JSONObject el = new JSONObject();
            el.put(NAME, policy.getPolicyName().replace(File.separator, FORWARD_SLASH));
            el.put(DATE, policy.getModifiedDate());
            el.put(VERSION, policy.getActiveVersion());
            el.put(SIZE, "");
            el.put(TYPE, "file");
            el.put(CREATED_BY, getUserName(policy.getCreatedBy()));
            el.put(MODIFIED_BY, getUserName(policy.getModifiedBy()));
            resultList.add(el);
        }
    }

    private void addScope(Set<String> scopes, String scope) {
        List<Object> scopesList = queryPolicyEditorScopes(scope);
        if (!scopesList.isEmpty()) {
            for (Object scopeItem : scopesList) {
                PolicyEditorScopes tempScope = (PolicyEditorScopes) scopeItem;
                scopes.add(tempScope.getScopeName());
            }
        }
    }

    // Switch Version Functionality
    private JSONObject switchVersion(JSONObject params, HttpServletRequest request) throws ServletException {
        String path = params.getString("path");
        String userId = null;
        try {
            userId = getUserSession(request).getOrgUserId();
        } catch (Exception e) {
            LOGGER.error("Exception Occured while reading userid from cookie" + e);
        }
        String policyName;
        String removeExtension = path.replace(".xml", "");
        if (path.startsWith(FORWARD_SLASH)) {
            policyName = removeExtension.substring(1, removeExtension.lastIndexOf('.'));
        } else {
            policyName = removeExtension.substring(0, removeExtension.lastIndexOf('.'));
        }

        String activePolicy;
        PolicyController controller = getPolicyControllerInstance();
        if (!params.toString().contains("activeVersion")) {
            return controller.switchVersionPolicyContent(policyName);
        }
        String activeVersion = params.getString("activeVersion");
        String highestVersion = params.get("highestVersion").toString();
        if (Integer.parseInt(activeVersion) > Integer.parseInt(highestVersion)) {
            return error("The Version shouldn't be greater than Highest Value");
        }
        activePolicy = policyName + "." + activeVersion + ".xml";
        String[] splitDbCheckName = modifyPolicyName(activePolicy);
        String peQuery = "FROM PolicyEntity where policyName = :splitDBCheckName_1 and scope = :splitDBCheckName_0";
        SimpleBindings policyParams = new SimpleBindings();
        policyParams.put("splitDBCheckName_1", splitDbCheckName[1]);
        policyParams.put("splitDBCheckName_0", splitDbCheckName[0]);
        List<Object> policyEntity = controller.getDataByQuery(peQuery, policyParams);
        PolicyEntity pentity = (PolicyEntity) policyEntity.get(0);
        if (pentity.isDeleted()) {
            return error("The Policy is Not Existing in Workspace");
        }
        if (policyName.contains(FORWARD_SLASH)) {
            policyName = policyName.replace(FORWARD_SLASH, File.separator);
        }
        policyName = policyName.substring(policyName.indexOf(File.separator) + 1);
        if (policyName.contains(BACKSLASH)) {
            policyName = policyName.replace(File.separator, BACKSLASH);
        }
        policyName = splitDbCheckName[0].replace(".", File.separator) + File.separator + policyName;
        final String watchPolicyName = policyName;
        if (policyName.contains(FORWARD_SLASH)) {
            policyName = policyName.replace(FORWARD_SLASH, File.separator);
        }
        if (policyName.contains(BACKSLASH)) {
            policyName = policyName.replace(BACKSLASH, ESCAPE_BACKSLASH);
        }
        String query = UPDATE_POLICY_VERSION_SET_ACTIVE_VERSION + activeVersion + "' where policy_name ='" + policyName
                + "'  and id >0";
        // query the database
        controller.executeQuery(query);
        // Policy Notification
        PolicyVersion entity = new PolicyVersion();
        entity.setPolicyName(watchPolicyName);
        entity.setActiveVersion(Integer.parseInt(activeVersion));
        entity.setModifiedBy(userId);
        controller.watchPolicyFunction(entity, activePolicy, "SwitchVersion");
        return success();
    }

    // Describe Policy
    private JSONObject describePolicy(JSONObject params) throws ServletException {
        String path = params.getString("path");
        String policyName;
        if (path.startsWith(FORWARD_SLASH)) {
            path = path.substring(1);
            policyName = path.substring(path.lastIndexOf('/') + 1);
            path = path.replace(FORWARD_SLASH, ".");
        } else {
            path = path.replace(FORWARD_SLASH, ".");
            policyName = path;
        }
        if (path.contains(CONFIG2)) {
            path = path.replace(CONFIG, CONFIG1);
        } else if (path.contains(ACTION2)) {
            path = path.replace(ACTION, ACTION1);
        } else if (path.contains(DECISION2)) {
            path = path.replace(DECISION, DECISION1);
        }
        String[] split = path.split(":");
        String query = "FROM PolicyEntity where policyName = :split_1 and scope = :split_0";
        SimpleBindings peParams = new SimpleBindings();
        peParams.put(SPLIT_1, split[1]);
        peParams.put(SPLIT_0, split[0]);
        List<Object> queryData = getDataByQueryFromController(getPolicyControllerInstance(), query, peParams);
        if (queryData.isEmpty()) {
            return error("Error Occured while Describing the Policy - query is empty");
        }
        PolicyEntity entity = (PolicyEntity) queryData.get(0);
        File temp;
        try {
            temp = File.createTempFile(policyName, ".tmp");
        } catch (IOException e) {
            String message = "Failed to create temp file " + policyName + ".tmp";
            LOGGER.error(message + e);
            return error(message);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(entity.getPolicyData());
        } catch (IOException e) {
            LOGGER.error("Exception Occured while Describing the Policy" + e);
        }
        JSONObject object = HumanPolicyComponent.DescribePolicy(temp);
        try {
            Files.delete(temp.toPath());
        } catch (IOException e) {
            LOGGER.warn("Failed to delete " + temp.getName() + e);
        }
        return object;
    }

    // Get the List of Policies and Scopes for Showing in Editor tab
    private JSONObject list(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            return processPolicyList(params, request);
        } catch (Exception e) {
            LOGGER.error("list", e);
            return error(e.getMessage());
        }
    }

    private JSONObject processPolicyList(JSONObject params, HttpServletRequest request) throws ServletException {
        // Get the Login Id of the User from Request
        String testUserID = getTestUserId();
        String userId = testUserID != null ? testUserID : getUserSession(request).getOrgUserId();
        List<Object> userRoles = getPolicyControllerInstance().getRoles(userId);
        Pair<Set<String>, List<String>> pair = checkRoleAndScope(userRoles);
        List<String> roles = pair.second;
        Set<String> scopes = pair.first;
        Map<String, String> roleByScope = org.onap.policy.utils.UserUtils.getRoleByScope(userRoles);

        List<JSONObject> resultList = new ArrayList<>();
        String path = params.getString("path");
        if (path.contains("..xml")) {
            path = path.replaceAll("..xml", "").trim();
        }

        if (roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST)) {
            if (scopes.isEmpty()
                    && !(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST))) {
                return error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
            } else {
                if (!FORWARD_SLASH.equals(path)) {
                    String tempScope = path.substring(1);
                    tempScope = tempScope.replace(FORWARD_SLASH, File.separator);
                    scopes.add(tempScope);
                }
            }
        }

        if (!FORWARD_SLASH.equals(path)) {
            try {
                String scopeName = path.substring(path.indexOf('/') + 1);
                activePolicyList(scopeName, resultList, roles, scopes, roleByScope);
            } catch (Exception ex) {
                LOGGER.error("Error Occurred While reading Policy Files List" + ex);
            }
            return new JSONObject().put(RESULT, resultList);
        }
        processRoles(scopes, roles, resultList, roleByScope);
        return new JSONObject().put(RESULT, resultList);
    }

    private void processRoles(Set<String> scopes, List<String> roles, List<JSONObject> resultList,
            Map<String, String> roleByScope) {
        if (roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)) {
            List<Object> scopesList = queryPolicyEditorScopes(null);
            scopesList.stream().map(list -> (PolicyEditorScopes) list).filter(
                scope -> !(scope.getScopeName().contains(File.separator)) && !scopes.contains(scope.getScopeName()))
                    .forEach(scope -> {
                        JSONObject el = new JSONObject();
                        el.put(NAME, scope.getScopeName());
                        el.put(DATE, scope.getModifiedDate());
                        el.put(SIZE, "");
                        el.put(TYPE, "dir");
                        el.put(CREATED_BY, scope.getUserCreatedBy().getUserName());
                        el.put(MODIFIED_BY, scope.getUserModifiedBy().getUserName());
                        el.put(ROLETYPE, roleByScope.get(ALLSCOPES));
                        resultList.add(el);
                    });
        }
        if (roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST)) {
            scopes.stream().map(this::queryPolicyEditorScopes).filter(scopesList -> !scopesList.isEmpty())
                    .forEach(scopesList -> {
                        JSONObject el = new JSONObject();
                        PolicyEditorScopes scopeById = (PolicyEditorScopes) scopesList.get(0);
                        el.put(NAME, scopeById.getScopeName());
                        el.put(DATE, scopeById.getModifiedDate());
                        el.put(SIZE, "");
                        el.put(TYPE, "dir");
                        el.put(CREATED_BY, scopeById.getUserCreatedBy().getUserName());
                        el.put(MODIFIED_BY, scopeById.getUserModifiedBy().getUserName());
                        if ((resultList).stream()
                                .noneMatch(item -> item.get("name").equals(scopeById.getScopeName()))) {
                            el.put(ROLETYPE, roleByScope.get(scopeById.getScopeName()));
                            resultList.add(el);
                        }
                    });
        }
    }

    private List<Object> queryPolicyEditorScopes(String scopeName) {
        String scopeNameQuery;
        SimpleBindings params = new SimpleBindings();
        if (scopeName == null) {
            scopeNameQuery = "from PolicyEditorScopes";
        } else {
            scopeNameQuery = FROM_POLICY_EDITOR_SCOPES_WHERE_SCOPENAME_LIKE_SCOPE_NAME;
            params.put(SCOPE_NAME, scopeName + "%");
        }
        PolicyController controller = getPolicyControllerInstance();
        return getDataByQueryFromController(controller, scopeNameQuery, params);
    }

    // Get Active Policy List based on Scope Selection from Policy Version table
    private void activePolicyList(String inScopeName, List<JSONObject> resultList, List<String> roles,
            Set<String> scopes, Map<String, String> roleByScope) {
        String scopeName = inScopeName;
        if (scopeName.contains(FORWARD_SLASH)) {
            scopeName = scopeName.replace(FORWARD_SLASH, File.separator);
        }
        if (scopeName.contains(BACKSLASH)) {
            scopeName = scopeName.replace(BACKSLASH, ESCAPE_BACKSLASH);
        }
        String query = "from PolicyVersion where POLICY_NAME like :scopeName";

        SimpleBindings params = new SimpleBindings();
        params.put(SCOPE_NAME, scopeName + "%");

        List<Object> activePolicies = getDataByQueryFromController(getPolicyControllerInstance(), query, params);
        List<Object> scopesList = getDataByQueryFromController(getPolicyControllerInstance(),
                FROM_POLICY_EDITOR_SCOPES_WHERE_SCOPENAME_LIKE_SCOPE_NAME, params);
        for (Object list : scopesList) {
            scopeName = checkScope(resultList, scopeName, (PolicyEditorScopes) list, roleByScope);
        }
        for (Object list : activePolicies) {
            PolicyVersion policy = (PolicyVersion) list;
            String scopeNameValue =
                    policy.getPolicyName().substring(0, policy.getPolicyName().lastIndexOf(File.separator));
            if (roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)) {
                String scopeNameCheck =
                        scopeName.contains(ESCAPE_BACKSLASH) ? scopeName.replace(ESCAPE_BACKSLASH, File.separator)
                                : scopeName;
                if (!scopeNameValue.equals(scopeNameCheck)) {
                    continue;
                }
                JSONObject el = new JSONObject();
                el.put(NAME, policy.getPolicyName().substring(policy.getPolicyName().lastIndexOf(File.separator) + 1));
                el.put(DATE, policy.getModifiedDate());
                el.put(VERSION, policy.getActiveVersion());
                el.put(SIZE, "");
                el.put(TYPE, "file");
                el.put(CREATED_BY, getUserName(policy.getCreatedBy()));
                el.put(MODIFIED_BY, getUserName(policy.getModifiedBy()));
                String roleType = Strings.isNullOrEmpty(roleByScope.get(scopeNameValue)) ? roleByScope.get(ALLSCOPES)
                        : roleByScope.get(scopeNameValue);
                el.put(ROLETYPE, roleType);
                resultList.add(el);
            } else if (!scopes.isEmpty() && scopes.contains(scopeNameValue)) {
                JSONObject el = new JSONObject();
                el.put(NAME, policy.getPolicyName().substring(policy.getPolicyName().lastIndexOf(File.separator) + 1));
                el.put(DATE, policy.getModifiedDate());
                el.put(VERSION, policy.getActiveVersion());
                el.put(SIZE, "");
                el.put(TYPE, "file");
                el.put(CREATED_BY, getUserName(policy.getCreatedBy()));
                el.put(MODIFIED_BY, getUserName(policy.getModifiedBy()));
                resultList.add(el);
            }
        }
    }

    private List<Object> getDataByQueryFromController(final PolicyController controller, final String query,
            final SimpleBindings params) {
        final List<Object> activePolicies;
        if (PolicyController.isjUnit()) {
            activePolicies = controller.getDataByQuery(query, null);
        } else {
            activePolicies = controller.getDataByQuery(query, params);
        }
        return activePolicies;
    }

    private String checkScope(List<JSONObject> resultList, String scopeName, PolicyEditorScopes scopeById,
            Map<String, String> roleByScope) {
        String scope = scopeById.getScopeName();
        if (!scope.contains(File.separator)) {
            return scopeName;
        }
        String targetScope = scope.substring(0, scope.lastIndexOf(File.separator));
        if (scopeName.contains(ESCAPE_BACKSLASH)) {
            scopeName = scopeName.replace(ESCAPE_BACKSLASH, File.separator);
        }
        if (scope.contains(File.separator)) {
            scope = scope.substring(targetScope.length() + 1);
            if (scope.contains(File.separator)) {
                scope = scope.substring(0, scope.indexOf(File.separator));
            }
        }
        if (scopeName.equalsIgnoreCase(targetScope)) {
            JSONObject el = new JSONObject();
            el.put(NAME, scope);
            el.put(DATE, scopeById.getModifiedDate());
            el.put(SIZE, "");
            el.put(TYPE, "dir");
            el.put(CREATED_BY, scopeById.getUserCreatedBy().getUserName());
            el.put(MODIFIED_BY, scopeById.getUserModifiedBy().getUserName());
            String roleType = roleByScope.get(ALLSCOPES); // Set default role type to ALL_SCOPES
            if (!Strings.isNullOrEmpty(roleByScope.get(scopeName))) {
                roleType = roleByScope.get(scopeName);
            } else if (!Strings.isNullOrEmpty(roleByScope.get(scopeName + File.separator + scope))) {
                roleType = roleByScope.get(scopeName + File.separator + scope);
            }
            el.put(ROLETYPE, roleType);
            resultList.add(el);
        }
        return scopeName;
    }

    private String getUserName(String loginId) {
        UserInfo userInfo = (UserInfo) getPolicyControllerInstance().getEntityItem(UserInfo.class, "userLoginId",
                loginId);
        if (userInfo == null) {
            return SUPERADMIN;
        }
        return userInfo.getUserName();
    }

    // Rename Policy
    private JSONObject rename(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            return handlePolicyRename(params, request);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Renaming Policy" + e);
            return error(e.getMessage());
        }
    }

    private JSONObject handlePolicyRename(JSONObject params, HttpServletRequest request) throws ServletException {
        boolean isActive = false;
        List<String> policyActiveInPdp = new ArrayList<>();
        Set<String> scopeOfPolicyActiveInPdp = new HashSet<>();
        String userId = getUserSession(request).getOrgUserId();
        String oldPath = params.getString("path");
        String newPath = params.getString("newPath");
        oldPath = oldPath.substring(oldPath.indexOf('/') + 1);
        newPath = newPath.substring(newPath.indexOf('/') + 1);
        String checkValidation;
        if (oldPath.endsWith(".xml")) {
            checkValidation = newPath.replace(".xml", "");
            checkValidation =
                    checkValidation.substring(checkValidation.indexOf('_') + 1, checkValidation.lastIndexOf('.'));
            checkValidation = checkValidation.substring(checkValidation.lastIndexOf(FORWARD_SLASH) + 1);
            if (!PolicyUtils.policySpecialCharValidator(checkValidation).contains(SUCCESS)) {
                return error("Policy Rename Failed. The Name contains special characters.");
            }
            JSONObject result = policyRename(oldPath, newPath, userId);
            if (!(Boolean) (result.getJSONObject(RESULT).get(SUCCESS))) {
                return result;
            }
        } else {
            String scopeName = oldPath;
            String newScopeName = newPath;
            if (scopeName.contains(FORWARD_SLASH)) {
                scopeName = scopeName.replace(FORWARD_SLASH, File.separator);
                newScopeName = newScopeName.replace(FORWARD_SLASH, File.separator);
            }
            checkValidation = newScopeName.substring(newScopeName.lastIndexOf(File.separator) + 1);
            if (scopeName.contains(BACKSLASH)) {
                scopeName = scopeName.replace(BACKSLASH, BACKSLASH_8TIMES);
                newScopeName = newScopeName.replace(BACKSLASH, BACKSLASH_8TIMES);
            }
            if (!PolicyUtils.policySpecialCharValidator(checkValidation).contains(SUCCESS)) {
                return error("Scope Rename Failed. The Name contains special characters.");
            }
            PolicyController controller = getPolicyControllerInstance();
            String query = "from PolicyVersion where POLICY_NAME like :scopeName";
            SimpleBindings pvParams = new SimpleBindings();
            pvParams.put(SCOPE_NAME, scopeName + "%");
            List<Object> activePolicies = controller.getDataByQuery(query, pvParams);
            List<Object> scopesList =
                    controller.getDataByQuery(FROM_POLICY_EDITOR_SCOPES_WHERE_SCOPENAME_LIKE_SCOPE_NAME, pvParams);
            for (Object object : activePolicies) {
                PolicyVersion activeVersion = (PolicyVersion) object;
                String policyOldPath = activeVersion.getPolicyName().replace(File.separator, FORWARD_SLASH) + "."
                        + activeVersion.getActiveVersion() + ".xml";
                String policyNewPath = policyOldPath.replace(oldPath, newPath);
                JSONObject result = policyRename(policyOldPath, policyNewPath, userId);
                if (!(Boolean) (result.getJSONObject("result").get(SUCCESS))) {
                    isActive = true;
                    policyActiveInPdp.add(policyOldPath);
                    String scope = policyOldPath.substring(0, policyOldPath.lastIndexOf('/'));
                    scopeOfPolicyActiveInPdp.add(scope.replace(FORWARD_SLASH, File.separator));
                }
            }
            boolean rename = activePolicies.size() != policyActiveInPdp.size();
            if (policyActiveInPdp.isEmpty()) {
                renameScope(scopesList, scopeName, newScopeName, controller);
            } else if (rename) {
                renameScope(scopesList, scopeName, newScopeName, controller);
                UserInfo userInfo = new UserInfo();
                userInfo.setUserLoginId(userId);
                scopeOfPolicyActiveInPdp.forEach(scope -> {
                    PolicyEditorScopes editorScopeEntity = new PolicyEditorScopes();
                    editorScopeEntity.setScopeName(scope.replace(BACKSLASH, BACKSLASH_8TIMES));
                    editorScopeEntity.setUserCreatedBy(userInfo);
                    editorScopeEntity.setUserModifiedBy(userInfo);
                    controller.saveData(editorScopeEntity);
                });
            }
            if (isActive) {
                return error("The Following policies rename failed. Since they are active in PDP Groups"
                        + policyActiveInPdp);
            }
        }
        return success();
    }

    private void renameScope(List<Object> scopesList, String inScopeName, String newScopeName,
            PolicyController controller) {
        for (Object object : scopesList) {
            PolicyEditorScopes editorScopeEntity = (PolicyEditorScopes) object;
            String scopeName = inScopeName;
            if (scopeName.contains(BACKSLASH_8TIMES)) {
                scopeName = scopeName.replace(BACKSLASH_8TIMES, File.separator);
                newScopeName = newScopeName.replace(BACKSLASH_8TIMES, File.separator);
            }
            String scope = editorScopeEntity.getScopeName().replace(scopeName, newScopeName);
            editorScopeEntity.setScopeName(scope);
            controller.updateData(editorScopeEntity);
        }
    }

    private JSONObject policyRename(String oldPath, String newPath, String userId) throws ServletException {
        try {
            PolicyEntity entity;
            PolicyController controller = getPolicyControllerInstance();

            String policyVersionName = newPath.replace(".xml", "");
            String policyName = policyVersionName.substring(0, policyVersionName.lastIndexOf('.'))
                    .replace(FORWARD_SLASH, File.separator);

            String oldpolicyVersionName = oldPath.replace(".xml", "");
            String oldpolicyName = oldpolicyVersionName.substring(0, oldpolicyVersionName.lastIndexOf('.'))
                    .replace(FORWARD_SLASH, File.separator);
            String newpolicyName = newPath.replace("/", ".");
            String[] newPolicySplit = modifyPolicyName(newPath);

            final String[] oldPolicySplit = modifyPolicyName(oldPath);

            // Check PolicyEntity table with newPolicy Name
            String policyEntityquery =
                    "FROM PolicyEntity where policyName = :newPolicySplit_1 and scope = :newPolicySplit_0";
            SimpleBindings policyParams = new SimpleBindings();
            policyParams.put("newPolicySplit_1", newPolicySplit[1]);
            policyParams.put("newPolicySplit_0", newPolicySplit[0]);
            List<Object> queryData = controller.getDataByQuery(policyEntityquery, policyParams);
            if (!queryData.isEmpty()) {
                return error("Policy rename failed. Since, the policy with same name already exists.");
            }

            // Query the Policy Entity with oldPolicy Name
            String policyEntityCheck = oldPolicySplit[1].substring(0, oldPolicySplit[1].indexOf('.'));
            String oldPolicyEntityQuery =
                    "FROM PolicyEntity where policyName like :policyEntityCheck and scope = " + ":oldPolicySplit_0";
            SimpleBindings params = new SimpleBindings();
            params.put("policyEntityCheck", policyEntityCheck + "%");
            params.put("oldPolicySplit_0", oldPolicySplit[0]);
            List<Object> oldEntityData = controller.getDataByQuery(oldPolicyEntityQuery, params);
            if (oldEntityData.isEmpty()) {
                return error(
                    "Policy rename failed due to policy not able to retrieve from database. Contact super-admin.");
            }

            StringBuilder groupQuery = new StringBuilder();
            groupQuery.append("FROM PolicyGroupEntity where (");
            SimpleBindings geParams = new SimpleBindings();
            for (int i = 0; i < oldEntityData.size(); i++) {
                entity = (PolicyEntity) oldEntityData.get(i);
                if (i == 0) {
                    groupQuery.append("policyid = :policyId");
                    geParams.put("policyId", entity.getPolicyId());
                } else {
                    groupQuery.append(" or policyid = :policyId").append(i);
                    geParams.put("policyId" + i, entity.getPolicyId());
                }
            }
            groupQuery.append(")");
            List<Object> groupEntityData = controller.getDataByQuery(groupQuery.toString(), geParams);
            if (!groupEntityData.isEmpty()) {
                return error("Policy rename failed. Since the policy or its version is active in PDP Groups.");
            }
            for (Object anOldEntityData : oldEntityData) {
                entity = (PolicyEntity) anOldEntityData;
                String checkEntityName = entity.getPolicyName().replace(".xml", "");
                checkEntityName = checkEntityName.substring(0, checkEntityName.lastIndexOf('.'));
                String originalPolicyName = oldpolicyName.substring(oldpolicyName.lastIndexOf(File.separator) + 1);
                if (checkEntityName.equals(originalPolicyName)) {
                    checkOldPolicyEntryAndUpdate(entity, newPolicySplit[0], newPolicySplit[1], oldPolicySplit[0],
                            oldPolicySplit[1], policyName, newpolicyName, oldpolicyName, userId);
                }
            }
            return success();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Renaming Policy" + e);
            return error(e.getMessage());
        }
    }

    private String[] modifyPolicyName(String pathName) {
        return modifyPolicyName(FORWARD_SLASH, pathName);
    }

    private String[] modifyPolicyName(String separator, String pathName) {
        String policyName = pathName.replace(separator, ".");
        if (policyName.contains(CONFIG2)) {
            policyName = policyName.replace(CONFIG, CONFIG1);
        } else if (policyName.contains(ACTION2)) {
            policyName = policyName.replace(ACTION, ACTION1);
        } else if (policyName.contains(DECISION2)) {
            policyName = policyName.replace(DECISION, DECISION1);
        }
        return policyName.split(":");
    }

    private void checkOldPolicyEntryAndUpdate(PolicyEntity entity, String newScope, String removeNewPolicyExtension,
            String oldScope, String removeOldPolicyExtension, String policyName, String newPolicyName,
            String oldPolicyName, String userId) {
        try {
            ConfigurationDataEntity configEntity = entity.getConfigurationData();
            ActionBodyEntity actionEntity = entity.getActionBodyEntity();
            PolicyController controller = getPolicyControllerInstance();

            String oldPolicyNameWithoutExtension = removeOldPolicyExtension;
            String newPolicyNameWithoutExtension = removeNewPolicyExtension;
            if (removeOldPolicyExtension.endsWith(".xml")) {
                oldPolicyNameWithoutExtension =
                        oldPolicyNameWithoutExtension.substring(0, oldPolicyNameWithoutExtension.indexOf('.'));
                newPolicyNameWithoutExtension =
                        newPolicyNameWithoutExtension.substring(0, newPolicyNameWithoutExtension.indexOf('.'));
            }
            entity.setPolicyName(
                    entity.getPolicyName().replace(oldPolicyNameWithoutExtension, newPolicyNameWithoutExtension));
            entity.setPolicyData(entity.getPolicyData().replace(oldScope + "." + oldPolicyNameWithoutExtension,
                    newScope + "." + newPolicyNameWithoutExtension));
            entity.setScope(newScope);
            entity.setModifiedBy(userId);

            String oldConfigurationName = null;
            String newConfigurationName = null;
            if (newPolicyName.contains(CONFIG2)) {
                oldConfigurationName = configEntity.getConfigurationName();
                configEntity.setConfigurationName(
                        configEntity.getConfigurationName().replace(oldScope + "." + oldPolicyNameWithoutExtension,
                                newScope + "." + newPolicyNameWithoutExtension));
                controller.updateData(configEntity);
                newConfigurationName = configEntity.getConfigurationName();
                File file = new File(PolicyController.getConfigHome() + File.separator + oldConfigurationName);
                if (file.exists()) {
                    File renameFile =
                            new File(PolicyController.getConfigHome() + File.separator + newConfigurationName);
                    file.renameTo(renameFile);
                }
            } else if (newPolicyName.contains(ACTION2)) {
                oldConfigurationName = actionEntity.getActionBodyName();
                actionEntity.setActionBody(
                        actionEntity.getActionBody().replace(oldScope + "." + oldPolicyNameWithoutExtension,
                                newScope + "." + newPolicyNameWithoutExtension));
                controller.updateData(actionEntity);
                newConfigurationName = actionEntity.getActionBodyName();
                File file = new File(PolicyController.getActionHome() + File.separator + oldConfigurationName);
                if (file.exists()) {
                    File renameFile =
                            new File(PolicyController.getActionHome() + File.separator + newConfigurationName);
                    file.renameTo(renameFile);
                }
            }
            controller.updateData(entity);

            PolicyRestController restController = new PolicyRestController();
            restController.notifyOtherPapsToUpdateConfigurations("rename", newConfigurationName, oldConfigurationName);
            PolicyVersion versionEntity =
                    (PolicyVersion) controller.getEntityItem(PolicyVersion.class, "policyName", oldPolicyName);
            versionEntity.setPolicyName(policyName);
            versionEntity.setModifiedBy(userId);
            controller.updateData(versionEntity);
            String movePolicyCheck = policyName.substring(policyName.lastIndexOf(File.separator) + 1);
            String moveOldPolicyCheck = oldPolicyName.substring(oldPolicyName.lastIndexOf(File.separator) + 1);
            if (movePolicyCheck.equals(moveOldPolicyCheck)) {
                controller.watchPolicyFunction(versionEntity, oldPolicyName, "Move");
            } else {
                controller.watchPolicyFunction(versionEntity, oldPolicyName, "Rename");
            }
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_OCCURED + e);
            throw e;
        }
    }

    private void cloneRecord(String newpolicyName, String oldScope, String inRemoveoldPolicyExtension, String newScope,
            String inRemovenewPolicyExtension, PolicyEntity entity, String userId) throws IOException {
        String queryEntityName;
        PolicyController controller = getPolicyControllerInstance();
        PolicyEntity cloneEntity = new PolicyEntity();
        cloneEntity.setPolicyName(newpolicyName);
        String removeoldPolicyExtension = inRemoveoldPolicyExtension;
        String removenewPolicyExtension = inRemovenewPolicyExtension;
        removeoldPolicyExtension = removeoldPolicyExtension.replace(".xml", "");
        removenewPolicyExtension = removenewPolicyExtension.replace(".xml", "");
        cloneEntity.setPolicyData(entity.getPolicyData().replace(oldScope + "." + removeoldPolicyExtension,
                newScope + "." + removenewPolicyExtension));
        cloneEntity.setScope(entity.getScope());
        String oldConfigRemoveExtension = removeoldPolicyExtension.replace(".xml", "");
        String newConfigRemoveExtension = removenewPolicyExtension.replace(".xml", "");
        String newConfigurationName = null;
        if (newpolicyName.contains(CONFIG2)) {
            ConfigurationDataEntity configurationDataEntity = new ConfigurationDataEntity();
            configurationDataEntity.setConfigurationName(entity.getConfigurationData().getConfigurationName()
                    .replace(oldScope + "." + oldConfigRemoveExtension, newScope + "." + newConfigRemoveExtension));
            queryEntityName = configurationDataEntity.getConfigurationName();
            configurationDataEntity.setConfigBody(entity.getConfigurationData().getConfigBody());
            configurationDataEntity.setConfigType(entity.getConfigurationData().getConfigType());
            configurationDataEntity.setDeleted(false);
            configurationDataEntity.setCreatedBy(userId);
            configurationDataEntity.setModifiedBy(userId);
            controller.saveData(configurationDataEntity);
            ConfigurationDataEntity configEntiy = (ConfigurationDataEntity) controller
                    .getEntityItem(ConfigurationDataEntity.class, "configurationName", queryEntityName);
            cloneEntity.setConfigurationData(configEntiy);
            newConfigurationName = configEntiy.getConfigurationName();
            try (FileWriter fw =
                    new FileWriter(PolicyController.getConfigHome() + File.separator + newConfigurationName);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(configEntiy.getConfigBody());
            } catch (IOException e) {
                LOGGER.error("Exception Occured While cloning the configuration file" + e);
                throw e;
            }
        } else if (newpolicyName.contains(ACTION2)) {
            ActionBodyEntity actionBodyEntity = new ActionBodyEntity();
            actionBodyEntity.setActionBodyName(entity.getActionBodyEntity().getActionBodyName()
                    .replace(oldScope + "." + oldConfigRemoveExtension, newScope + "." + newConfigRemoveExtension));
            queryEntityName = actionBodyEntity.getActionBodyName();
            actionBodyEntity.setActionBody(entity.getActionBodyEntity().getActionBody());
            actionBodyEntity.setDeleted(false);
            actionBodyEntity.setCreatedBy(userId);
            actionBodyEntity.setModifiedBy(userId);
            controller.saveData(actionBodyEntity);
            ActionBodyEntity actionEntiy = (ActionBodyEntity) controller.getEntityItem(ActionBodyEntity.class,
                    "actionBodyName", queryEntityName);
            cloneEntity.setActionBodyEntity(actionEntiy);
            newConfigurationName = actionEntiy.getActionBodyName();
            try (FileWriter fw =
                    new FileWriter(PolicyController.getActionHome() + File.separator + newConfigurationName);
                    BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(actionEntiy.getActionBody());
            } catch (IOException e) {
                LOGGER.error("Exception Occured While cloning the configuration file" + e);
                throw e;
            }
        }

        cloneEntity.setDeleted(entity.isDeleted());
        cloneEntity.setCreatedBy(userId);
        cloneEntity.setModifiedBy(userId);
        controller.saveData(cloneEntity);

        // Notify others paps regarding clone policy.
        PolicyRestController restController = new PolicyRestController();
        restController.notifyOtherPapsToUpdateConfigurations("clonePolicy", newConfigurationName, null);
    }

    // Clone the Policy
    private JSONObject copy(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            String userId = getUserSession(request).getOrgUserId();
            String oldPath = params.getString("path");
            String newPath = params.getString("newPath");
            oldPath = oldPath.substring(oldPath.indexOf('/') + 1);
            newPath = newPath.substring(newPath.indexOf('/') + 1);

            String policyVersionName = newPath.replace(".xml", "");
            String version = policyVersionName.substring(policyVersionName.indexOf('.') + 1);
            String policyName = policyVersionName.substring(0, policyVersionName.lastIndexOf('.'))
                    .replace(FORWARD_SLASH, File.separator);

            String newPolicyName = newPath.replace(FORWARD_SLASH, ".");

            String newPolicyCheck = newPolicyName;
            if (newPolicyCheck.contains(CONFIG2)) {
                newPolicyCheck = newPolicyCheck.replace(CONFIG, CONFIG1);
            } else if (newPolicyCheck.contains(ACTION2)) {
                newPolicyCheck = newPolicyCheck.replace(ACTION, ACTION1);
            } else if (newPolicyCheck.contains(DECISION2)) {
                newPolicyCheck = newPolicyCheck.replace(DECISION, DECISION1);
            }
            if (!newPolicyCheck.contains(":")) {
                return error("Policy Clone Failed. The Name contains special characters.");
            }
            String[] newPolicySplit = newPolicyCheck.split(":");

            String checkValidation = newPolicySplit[1].replace(".xml", "");
            checkValidation =
                    checkValidation.substring(checkValidation.indexOf('_') + 1, checkValidation.lastIndexOf('.'));
            if (!PolicyUtils.policySpecialCharValidator(checkValidation).contains(SUCCESS)) {
                return error("Policy Clone Failed. The Name contains special characters.");
            }

            String originalPolicyName = oldPath.replace(FORWARD_SLASH, ".");

            final String[] oldPolicySplit = modifyPolicyName(originalPolicyName);

            PolicyController controller = getPolicyControllerInstance();

            PolicyEntity entity = null;
            boolean success = false;

            // Check PolicyEntity table with newPolicy Name
            String policyEntityquery =
                    "FROM PolicyEntity where policyName = :newPolicySplit_1 and scope = :newPolicySplit_0";
            SimpleBindings policyParams = new SimpleBindings();
            policyParams.put("newPolicySplit_1", newPolicySplit[1]);
            policyParams.put("newPolicySplit_0", newPolicySplit[0]);
            List<Object> queryData = controller.getDataByQuery(policyEntityquery, policyParams);
            if (!queryData.isEmpty()) {
                return error("Policy already exists with same name");
            }

            // Query the Policy Entity with oldPolicy Name
            policyEntityquery = "FROM PolicyEntity where policyName = :oldPolicySplit_1 and scope = :oldPolicySplit_0";
            SimpleBindings peParams = new SimpleBindings();
            peParams.put("oldPolicySplit_1", oldPolicySplit[1]);
            peParams.put("oldPolicySplit_0", oldPolicySplit[0]);
            queryData = getDataByQueryFromController(controller, policyEntityquery, peParams);
            if (!queryData.isEmpty()) {
                entity = (PolicyEntity) queryData.get(0);
            }
            if (entity != null) {
                cloneRecord(newPolicySplit[1], oldPolicySplit[0], oldPolicySplit[1], newPolicySplit[0],
                        newPolicySplit[1], entity, userId);
                success = true;
            }
            if (success) {
                PolicyVersion entityItem = new PolicyVersion();
                entityItem.setActiveVersion(Integer.parseInt(version));
                entityItem.setHigherVersion(Integer.parseInt(version));
                entityItem.setPolicyName(policyName);
                entityItem.setCreatedBy(userId);
                entityItem.setModifiedBy(userId);
                entityItem.setModifiedDate(new Date());
                controller.saveData(entityItem);
            }
            LOGGER.debug("copy from: {} to: {}" + oldPath + newPath);
            return success();
        } catch (Exception e) {
            LOGGER.error("copy", e);
            return error(e.getMessage());
        }
    }

    // Delete Policy or Scope Functionality
    private JSONObject delete(JSONObject params, HttpServletRequest request) throws ServletException {
        PolicyController controller = getPolicyControllerInstance();
        PolicyRestController restController = new PolicyRestController();
        PolicyEntity policyEntity = null;
        String policyNamewithoutExtension;
        try {
            String userId = getUserSession(request).getOrgUserId();
            String deleteVersion = "";
            String path = params.getString("path");
            LOGGER.debug("delete {}" + path);
            if (params.has("deleteVersion")) {
                deleteVersion = params.getString("deleteVersion");
            }
            path = path.substring(path.indexOf('/') + 1);
            String policyNamewithExtension = path.replace(FORWARD_SLASH, File.separator);
            String policyVersionName = policyNamewithExtension.replace(".xml", "");
            String query;
            SimpleBindings policyParams = new SimpleBindings();
            if (path.endsWith(".xml")) {
                policyNamewithoutExtension = policyVersionName.substring(0, policyVersionName.lastIndexOf('.'));
                String[] split = modifyPolicyName(File.separator, policyNamewithoutExtension);
                query = "FROM PolicyEntity where policyName like :split_1 and scope = :split_0";
                policyParams.put(SPLIT_1, split[1] + "%");
                policyParams.put(SPLIT_0, split[0]);
            } else {
                policyNamewithoutExtension = path.replace(File.separator, ".");
                query = "FROM PolicyEntity where scope like :policyNamewithoutExtension or scope = :exactScope";
                policyParams.put("policyNamewithoutExtension", policyNamewithoutExtension + ".%");
                policyParams.put("exactScope", policyNamewithoutExtension);
            }

            List<Object> policyEntityObjects = controller.getDataByQuery(query, policyParams);
            String activePolicyName = null;
            boolean pdpCheck = false;
            if (path.endsWith(".xml")) {
                policyNamewithoutExtension = policyNamewithoutExtension.replace(".", File.separator);
                int version = Integer.parseInt(policyVersionName.substring(policyVersionName.indexOf('.') + 1));
                if ("ALL".equals(deleteVersion)) {
                    if (!policyEntityObjects.isEmpty()) {
                        for (Object object : policyEntityObjects) {
                            policyEntity = (PolicyEntity) object;
                            String groupEntityquery =
                                    "from PolicyGroupEntity where policyid ='" + policyEntity.getPolicyId() + "'";
                            SimpleBindings pgeParams = new SimpleBindings();
                            List<Object> groupobject = controller.getDataByQuery(groupEntityquery, pgeParams);
                            if (!groupobject.isEmpty()) {
                                pdpCheck = true;
                                activePolicyName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
                            } else {
                                deleteEntityFromEsAndPolicyEntityTable(controller, restController, policyEntity,
                                        policyNamewithoutExtension);
                            }
                        }
                    }
                    // Policy Notification
                    PolicyVersion versionEntity = new PolicyVersion();
                    versionEntity.setPolicyName(policyNamewithoutExtension);
                    versionEntity.setModifiedBy(userId);
                    controller.watchPolicyFunction(versionEntity, policyNamewithExtension, "DeleteAll");
                    if (pdpCheck) {
                        // Delete from policyVersion table
                        String getActivePdpPolicyVersion = activePolicyName.replace(".xml", "");
                        getActivePdpPolicyVersion =
                                getActivePdpPolicyVersion.substring(getActivePdpPolicyVersion.lastIndexOf('.') + 1);
                        String policyVersionQuery = UPDATE_POLICY_VERSION_SET_ACTIVE_VERSION + getActivePdpPolicyVersion
                                + "' , highest_version='" + getActivePdpPolicyVersion + "'  where policy_name ='"
                                + policyNamewithoutExtension.replace(BACKSLASH, ESCAPE_BACKSLASH) + "' and id >0";
                        controller.executeQuery(policyVersionQuery);
                        return error(
                            "Policies with Same name has been deleted. Except the Active Policy in PDP. PolicyName: "
                                        + activePolicyName);
                    } else {
                        // No Active Policy in PDP. So, deleting all entries from policyVersion table
                        String policyVersionQuery = DELETE_POLICY_VERSION_WHERE_POLICY_NAME
                                + policyNamewithoutExtension.replace(BACKSLASH, ESCAPE_BACKSLASH) + "' and id >0";
                        controller.executeQuery(policyVersionQuery);
                    }
                } else if ("CURRENT".equals(deleteVersion)) {
                    String currentVersionPolicyName =
                            policyNamewithExtension.substring(policyNamewithExtension.lastIndexOf(File.separator) + 1);
                    String currentVersionScope =
                            policyNamewithExtension.substring(0, policyNamewithExtension.lastIndexOf(File.separator))
                                    .replace(File.separator, ".");
                    query = "FROM PolicyEntity where policyName = :currentVersionPolicyName and "
                            + "scope = :currentVersionScope";

                    SimpleBindings peParams = new SimpleBindings();
                    peParams.put("currentVersionPolicyName", currentVersionPolicyName);
                    peParams.put("currentVersionScope", currentVersionScope);

                    List<Object> policyEntitys = controller.getDataByQuery(query, peParams);
                    if (!policyEntitys.isEmpty()) {
                        policyEntity = (PolicyEntity) policyEntitys.get(0);
                    }
                    if (policyEntity == null) {
                        return success();
                    }

                    String groupEntityquery =
                            "from PolicyGroupEntity where policyid = :policyEntityId and policyid > 0";
                    SimpleBindings geParams = new SimpleBindings();
                    geParams.put("policyEntityId", policyEntity.getPolicyId());
                    List<Object> groupobject = controller.getDataByQuery(groupEntityquery, geParams);
                    if (!groupobject.isEmpty()) {
                        return error("Policy can't be deleted, it is active in PDP Groups.     PolicyName: '"
                                + policyEntity.getScope() + "." + policyEntity.getPolicyName() + "'");
                    }

                    // Delete the entity from Elastic Search Database
                    deleteEntityFromEsAndPolicyEntityTable(controller, restController, policyEntity,
                            policyNamewithoutExtension);

                    if (version > 1) {
                        int highestVersion = 0;
                        if (!policyEntityObjects.isEmpty()) {
                            for (Object object : policyEntityObjects) {
                                policyEntity = (PolicyEntity) object;
                                String policyEntityName = policyEntity.getPolicyName().replace(".xml", "");
                                int policyEntityVersion = Integer
                                        .parseInt(policyEntityName.substring(policyEntityName.lastIndexOf('.') + 1));
                                if (policyEntityVersion > highestVersion && policyEntityVersion != version) {
                                    highestVersion = policyEntityVersion;
                                }
                            }
                        }

                        // Policy Notification
                        PolicyVersion entity = new PolicyVersion();
                        entity.setPolicyName(policyNamewithoutExtension);
                        entity.setActiveVersion(highestVersion);
                        entity.setModifiedBy(userId);
                        controller.watchPolicyFunction(entity, policyNamewithExtension, "DeleteOne");

                        String updatequery;
                        if (highestVersion != 0) {
                            updatequery = UPDATE_POLICY_VERSION_SET_ACTIVE_VERSION + highestVersion
                                    + "' , highest_version='" + highestVersion + "' where policy_name ='"
                                    + policyNamewithoutExtension.replace("\\", "\\\\") + "'";
                        } else {
                            updatequery = DELETE_POLICY_VERSION_WHERE_POLICY_NAME
                                    + policyNamewithoutExtension.replace("\\", "\\\\") + "' and id >0";
                        }
                        controller.executeQuery(updatequery);
                    } else {
                        String policyVersionQuery = DELETE_POLICY_VERSION_WHERE_POLICY_NAME
                                + policyNamewithoutExtension.replace("\\", "\\\\") + "' and id >0";
                        controller.executeQuery(policyVersionQuery);
                    }
                }
            } else {
                List<String> activePoliciesInPdp = new ArrayList<>();
                if (policyEntityObjects.isEmpty()) {
                    String policyScopeQuery = "delete PolicyEditorScopes where SCOPENAME like '"
                            + path.replace(BACKSLASH, ESCAPE_BACKSLASH) + PERCENT_AND_ID_GT_0;
                    controller.executeQuery(policyScopeQuery);
                    return success();
                }
                for (Object object : policyEntityObjects) {
                    policyEntity = (PolicyEntity) object;
                    String groupEntityQuery = "from PolicyGroupEntity where policyid = :policyEntityId";
                    SimpleBindings geParams = new SimpleBindings();
                    geParams.put("policyEntityId", policyEntity.getPolicyId());
                    List<Object> groupobject = controller.getDataByQuery(groupEntityQuery, geParams);
                    if (!groupobject.isEmpty()) {
                        pdpCheck = true;
                        activePoliciesInPdp.add(policyEntity.getScope() + "." + policyEntity.getPolicyName());
                    } else {
                        // Delete the entity from Elastic Search Database
                        String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
                        restController.deleteElasticData(searchFileName);
                        // Delete the entity from Policy Entity table
                        controller.deleteData(policyEntity);
                        policyNamewithoutExtension = policyEntity.getPolicyName();
                        if (policyNamewithoutExtension.contains(CONFIG2)) {
                            Files.deleteIfExists(Paths.get(PolicyController.getConfigHome() + File.separator
                                    + policyEntity.getConfigurationData().getConfigurationName()));
                            controller.deleteData(policyEntity.getConfigurationData());
                            restController.notifyOtherPapsToUpdateConfigurations(DELETE, null,
                                    policyEntity.getConfigurationData().getConfigurationName());
                        } else if (policyNamewithoutExtension.contains(ACTION2)) {
                            Files.deleteIfExists(Paths.get(PolicyController.getActionHome() + File.separator
                                    + policyEntity.getActionBodyEntity().getActionBodyName()));
                            controller.deleteData(policyEntity.getActionBodyEntity());
                            restController.notifyOtherPapsToUpdateConfigurations(DELETE, null,
                                    policyEntity.getActionBodyEntity().getActionBodyName());
                        }
                    }
                }
                // Delete from policyVersion and policyEditor Scope table
                String policyVersionQuery = "delete PolicyVersion where POLICY_NAME like '"
                        + path.replace(BACKSLASH, ESCAPE_BACKSLASH) + File.separator + PERCENT_AND_ID_GT_0;
                controller.executeQuery(policyVersionQuery);

                // Policy Notification
                PolicyVersion entity = new PolicyVersion();
                entity.setPolicyName(path);
                entity.setModifiedBy(userId);
                controller.watchPolicyFunction(entity, path, "DeleteScope");
                if (pdpCheck) {
                    // Add Active Policies List to PolicyVersionTable
                    for (String anActivePoliciesInPdp : activePoliciesInPdp) {
                        String activePdpPolicyName = anActivePoliciesInPdp.replace(".xml", "");
                        int activePdpPolicyVersion = Integer
                                .parseInt(activePdpPolicyName.substring(activePdpPolicyName.lastIndexOf('.') + 1));
                        activePdpPolicyName = activePdpPolicyName.substring(0, activePdpPolicyName.lastIndexOf('.'))
                                .replace(".", File.separator);
                        PolicyVersion insertActivePdpVersion = new PolicyVersion();
                        insertActivePdpVersion.setPolicyName(activePdpPolicyName);
                        insertActivePdpVersion.setHigherVersion(activePdpPolicyVersion);
                        insertActivePdpVersion.setActiveVersion(activePdpPolicyVersion);
                        insertActivePdpVersion.setCreatedBy(userId);
                        insertActivePdpVersion.setModifiedBy(userId);
                        controller.saveData(insertActivePdpVersion);
                    }
                    return error("All the Policies has been deleted in Scope. Except the following list of Policies:"
                            + activePoliciesInPdp);
                } else {
                    String policyScopeQuery = "delete PolicyEditorScopes where SCOPENAME like '"
                            + path.replace(BACKSLASH, ESCAPE_BACKSLASH) + PERCENT_AND_ID_GT_0;
                    controller.executeQuery(policyScopeQuery);
                }

            }
            return success();
        } catch (Exception e) {
            LOGGER.error(DELETE, e);
            return error(e.getMessage());
        }
    }

    private void deleteEntityFromEsAndPolicyEntityTable(final PolicyController controller,
            final PolicyRestController restController, final PolicyEntity policyEntity,
            final String policyNamewithoutExtension) throws IOException {
        // Delete the entity from Elastic Search Database
        String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
        restController.deleteElasticData(searchFileName);
        // Delete the entity from Policy Entity table
        controller.deleteData(policyEntity);
        if (policyNamewithoutExtension.contains(CONFIG2)) {
            Files.deleteIfExists(Paths.get(PolicyController.getConfigHome() + File.separator
                    + policyEntity.getConfigurationData().getConfigurationName()));
            controller.deleteData(policyEntity.getConfigurationData());
            restController.notifyOtherPapsToUpdateConfigurations(DELETE, null,
                    policyEntity.getConfigurationData().getConfigurationName());
        } else if (policyNamewithoutExtension.contains(ACTION2)) {
            Files.deleteIfExists(Paths.get(PolicyController.getActionHome() + File.separator
                    + policyEntity.getActionBodyEntity().getActionBodyName()));
            controller.deleteData(policyEntity.getActionBodyEntity());
            restController.notifyOtherPapsToUpdateConfigurations(DELETE, null,
                    policyEntity.getActionBodyEntity().getActionBodyName());
        }
    }

    // Edit the Policy
    private JSONObject editFile(JSONObject params) throws ServletException {
        // get content
        try {
            final String mode = params.getString("mode");
            String path = params.getString("path");
            LOGGER.debug("editFile path: {}" + path);

            String domain = path.substring(1, path.lastIndexOf('/'));
            domain = domain.replace(FORWARD_SLASH, ".");

            path = path.substring(1);
            path = path.replace(FORWARD_SLASH, ".");

            String[] split = modifyPolicyName(path);

            String query = "FROM PolicyEntity where policyName = :split_1 and scope = :split_0";
            SimpleBindings peParams = new SimpleBindings();
            peParams.put(SPLIT_1, split[1]);
            peParams.put(SPLIT_0, split[0]);
            List<Object> queryData = getDataByQueryFromController(getPolicyControllerInstance(), query, peParams);
            PolicyEntity entity = (PolicyEntity) queryData.get(0);
            InputStream stream = new ByteArrayInputStream(entity.getPolicyData().getBytes(StandardCharsets.UTF_8));

            Object policy = XACMLPolicyScanner.readPolicy(stream);
            PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
            policyAdapter.setData(policy);

            if ("viewPolicy".equalsIgnoreCase(mode)) {
                policyAdapter.setReadOnly(true);
                policyAdapter.setEditPolicy(false);
            } else {
                policyAdapter.setReadOnly(false);
                policyAdapter.setEditPolicy(true);
            }

            policyAdapter.setDomainDir(domain);
            policyAdapter.setPolicyData(policy);
            String policyName = path.replace(".xml", "");
            policyName = policyName.substring(0, policyName.lastIndexOf('.'));
            policyAdapter.setPolicyName(policyName.substring(policyName.lastIndexOf('.') + 1));

            PolicyAdapter setPolicyAdapter = PolicyAdapter.getInstance();
            Objects.requireNonNull(setPolicyAdapter).configure(policyAdapter, entity);

            policyAdapter.setParentPath(null);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(policyAdapter);
            JsonNode jsonNode = mapper.readTree(json);

            return new JSONObject().put(RESULT, jsonNode);
        } catch (Exception e) {
            LOGGER.error("editFile", e);
            return error(e.getMessage());
        }
    }

    // Add Scopes
    private JSONObject addFolder(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            String name = getNameFromParams(params);
            String validateName =
                    name.contains(File.separator) ? name.substring(name.lastIndexOf(File.separator) + 1) : name;
            if (!name.isEmpty()) {
                String validate = PolicyUtils.policySpecialCharValidator(validateName);
                if (!validate.contains(SUCCESS)) {
                    return error(validate);
                }
                LOGGER.debug("addFolder path: {} name: {}" + params.getString("path") + name);
                if (name.startsWith(File.separator)) {
                    name = name.substring(1);
                }
                PolicyEditorScopes entity =
                        (PolicyEditorScopes) getPolicyControllerInstance().getEntityItem(
                                PolicyEditorScopes.class, SCOPE_NAME, name);
                if (entity != null) {
                    return error("Scope Already Exists");
                }
                String userId = getUserSession(request).getOrgUserId();
                UserInfo userInfo = new UserInfo();
                userInfo.setUserLoginId(userId);
                PolicyEditorScopes newScope = new PolicyEditorScopes();
                newScope.setScopeName(name);
                newScope.setUserCreatedBy(userInfo);
                newScope.setUserModifiedBy(userInfo);
                getPolicyControllerInstance().saveData(newScope);
            }
            return success();
        } catch (Exception e) {
            LOGGER.error("addFolder", e);
            return error(e.getMessage());
        }
    }

    private String getNameFromParams(final JSONObject params) {
        String name = "";
        try {
            if (params.has(SUB_SCOPENAME)) {
                if (!"".equals(params.getString(SUB_SCOPENAME))) {
                    name = params.getString("path").replace(FORWARD_SLASH, File.separator) + File.separator
                            + params.getString(SUB_SCOPENAME);
                }
            } else {
                name = params.getString(NAME);
            }
        } catch (Exception e) {
            name = params.getString(NAME);
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occurred While Adding Scope" + e);
        }
        return name;
    }

    // Return Error Object
    private JSONObject error(String msg) throws ServletException {
        try {
            JSONObject result = new JSONObject();
            result.put(SUCCESS, false);
            result.put("error", msg);
            return new JSONObject().put(RESULT, result);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    // Return Success Object
    private JSONObject success() throws ServletException {
        try {
            JSONObject result = new JSONObject();
            result.put(SUCCESS, true);
            result.put("error", (Object) null);
            return new JSONObject().put(RESULT, result);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private PolicyController getPolicyControllerInstance() {
        return policyController != null ? getPolicyController() : new PolicyController();
    }

    private String getTestUserId() {
        return testUserId;
    }

    public static void setTestUserId(String testUserId) {
        PolicyManagerServlet.testUserId = testUserId;
    }
}
