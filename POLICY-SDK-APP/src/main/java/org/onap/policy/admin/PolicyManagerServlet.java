/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.components.HumanPolicyComponent;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.controller.PolicyExportAndImportController;
import org.onap.policy.rest.XACMLRest;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.utils.UserUtils.Pair;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.util.XACMLPolicyScanner;
import org.onap.portalsdk.core.web.support.UserUtils;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet(value ="/fm/*",  loadOnStartup = 1, initParams = { @WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.admin.properties", description = "The location of the properties file holding configuration information.") })
public class PolicyManagerServlet extends HttpServlet {
    private static final Logger LOGGER	= FlexLogger.getLogger(PolicyManagerServlet.class);
    private static final long serialVersionUID = -8453502699403909016L;
    private static final String VERSION = "version";;
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String SIZE = "size";
    private static final String TYPE = "type";
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
    private static final String EXCEPTION_OCCURED = "Exception Occured";
    private static final String CONFIG = ".Config_";
    private static final String CONFIG1 = ":Config_";
    private static final String ACTION = ".Action_";
    private static final String ACTION1 = ":Action_";
    private static final String DECISION = ".Decision_";
    private static final String DECISION1 = ":Decision_";
    private static final String CONFIG_ = "Config_";
    private static final String ACTION_ = "Action_";
    private static final String DECISION_ = "Decision_";
    private static final String FORWARD_SLASH = "/";
    private static final String BACKSLASH = "\\";
    private static final String ESCAPE_BACKSLASH = "\\\\";
    private static final String BACKSLASH_8TIMES = "\\\\\\\\";
    private static List<String> serviceTypeNamesList = new ArrayList<>();


    private enum Mode {
        LIST, RENAME, COPY, DELETE, EDITFILE, ADDFOLDER, DESCRIBEPOLICYFILE, VIEWPOLICY, ADDSUBSCOPE, SWITCHVERSION, EXPORT, SEARCHLIST
    }

    private static PolicyController policyController;
    public synchronized PolicyController getPolicyController() {
        return policyController;
    }

    public static synchronized void setPolicyController(PolicyController policyController) {
        PolicyManagerServlet.policyController = policyController;
    }

    private static Path closedLoopJsonLocation;
    private static JsonArray policyNames;
    private static String testUserId = null;

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
        XACMLRest.xacmlInit(servletConfig);
        //
        //Initialize ClosedLoop JSON
        //
        PolicyManagerServlet.initializeJSONLoad();
    }

    protected static void initializeJSONLoad() {
        closedLoopJsonLocation = Paths.get(XACMLProperties
                .getProperty(XACMLRestProperties.PROP_ADMIN_CLOSEDLOOP));
        String location = closedLoopJsonLocation.toString();
        if (! location.endsWith("json")) {
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
            LOGGER.error("Exception Occured while initializing the JSONConfig file"+e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("doPost");
        try {
            // if request contains multipart-form-data
            if (ServletFileUpload.isMultipartContent(request)) {
                uploadFile(request, response);
            }
            // all other post request has json params in body
            else {
                fileOperation(request, response);
            }
        } catch (Exception e) {
            try {
                setError(e, response);
            }catch(Exception e1){
                LOGGER.error(EXCEPTION_OCCURED +e1);
            }
        }
    }

    //Set Error Message for Exception
    private void setError(Exception t, HttpServletResponse response) throws IOException {
        try {
            JSONObject responseJsonObject = error(t.getMessage());
            response.setContentType(CONTENTTYPE);
            PrintWriter out = response.getWriter();
            out.print(responseJsonObject);
            out.flush();
        } catch (Exception x) {
            LOGGER.error(EXCEPTION_OCCURED +x);
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, x.getMessage());
        }
    }

    //Policy Import Functionality
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
        if(item.getName().endsWith(".xls") && item.getSize() <= PolicyController.getFileSizeLimit()){
            File file = new File(item.getName());
            try (OutputStream outputStream = new FileOutputStream(file);)
            {
                IOUtils.copy(item.getInputStream(), outputStream);
                newFile = file.toString();
                PolicyExportAndImportController importController = new PolicyExportAndImportController();
                importController.importRepositoryFile(newFile, request);
            }catch(Exception e){
                LOGGER.error("Upload error : " + e);
            }
        }
        else if (!item.getName().endsWith(".xls")) {
            LOGGER.error("Non .xls filetype uploaded: " + item.getName());
        }
        else { //uploaded file size is greater than allowed
            LOGGER.error("Upload file size limit exceeded! File size (Bytes) is: " + item.getSize());
        }
    }

    //File Operation Functionality
    private void fileOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject responseJsonObject = null;
        try (BufferedReader br = request.getReader()) {
            StringBuilder sb = new StringBuilder();

            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }

            JSONObject jObj = new JSONObject(sb.toString());
            JSONObject params = jObj.getJSONObject("params");
            Mode mode = Mode.valueOf(params.getString("mode"));

            String userId = UserUtils.getUserSession(request).getOrgUserId();
            LOGGER.info("****************************************Logging UserID while doing actions on Editor tab*******************************************");
            LOGGER.info("UserId:  " + userId + "Action Mode:  "+ mode.toString() + "Action Params: "+params.toString());
            LOGGER.info("***********************************************************************************************************************************");
            responseJsonObject = operateBasedOnMode(mode, params, request);


        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While doing File Operation" + e);
            responseJsonObject = error(e.getMessage());
        }
        response.setContentType(CONTENTTYPE);
        PrintWriter out = response.getWriter();
        out.print(responseJsonObject);
        out.flush();
    }


    private JSONObject operateBasedOnMode(Mode mode, JSONObject params, HttpServletRequest request) throws ServletException{
        JSONObject responseJsonObject = null;
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
        List<Object> policyData = new ArrayList<>();
        JSONArray policyList = null;
        if(params.has("policyList")){
            policyList = (JSONArray) params.get("policyList");
        }
        PolicyController controller = getPolicyControllerInstance();
        List<JSONObject> resultList = new ArrayList<>();
        try {
            if (!lookupPolicyData(request, policyData, policyList, controller, resultList))
                return error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");

        }catch(Exception e){
            LOGGER.error("Exception occured while reading policy Data from Policy Version table for Policy Search Data"+e);
        }

        return new JSONObject().put(RESULT, resultList);
    }

    private boolean lookupPolicyData(HttpServletRequest request, List<Object> policyData, JSONArray policyList, PolicyController controller, List<JSONObject> resultList){
        List<String> roles;
        Set<String> scopes;//Get the Login Id of the User from Request
        String userId =  UserUtils.getUserSession(request).getOrgUserId();
        List<Object> userRoles = controller.getRoles(userId);
        Pair<Set<String>, List<String>> pair = org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
        roles = pair.u;
        scopes = pair.t;
        if (roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST) ) {
            if(scopes.isEmpty()){
                return false;
            }
            Set<String> tempScopes = scopes;
            for(String scope : tempScopes){
                addScope(scopes, scope);
            }
        }
        if(policyList!= null){
            for(int i = 0; i < policyList.length(); i++){
                String policyName = policyList.get(i).toString().replace(".xml", "");
                String version = policyName.substring(policyName.lastIndexOf('.')+1);
                policyName = policyName.substring(0, policyName.lastIndexOf('.')).replace(".", File.separator);
                parsePolicyList(resultList, controller, policyName, version);
            }
        }else {
            getPolicyDataForSUPERRoles(policyData, controller, resultList, roles, scopes);
        }
        return true;
    }

    private void getPolicyDataForSUPERRoles(List<Object> policyData, PolicyController controller, List<JSONObject> resultList, List<String> roles, Set<String> scopes) {
        if (roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR)   || roles.contains(SUPERGUEST) ){
            policyData = controller.getData(PolicyVersion.class);
        }else{
            List<Object> filterdatas = controller.getData(PolicyVersion.class);
            for(Object filter : filterdatas) {
                addFilterData(policyData, scopes, (PolicyVersion) filter);
            }
        }

        if(!policyData.isEmpty()){
            updateResultList(policyData, resultList);
        }
    }

    private void addFilterData(List<Object> policyData, Set<String> scopes, PolicyVersion filter) {
        try{
            String scopeName = filter.getPolicyName().substring(0, filter.getPolicyName().lastIndexOf(File.separator));
            if(scopes.contains(scopeName)){
                policyData.add(filter);
            }
        }catch(Exception e){
            LOGGER.error("Exception occured while filtering policyversion data"+e);
        }
    }

    private void updateResultList(List<Object> policyData, List<JSONObject> resultList) {
        for(int i =0; i < policyData.size(); i++){
            PolicyVersion policy = (PolicyVersion) policyData.get(i);
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

    private void parsePolicyList(List<JSONObject> resultList, PolicyController controller, String policyName, String version) {
        if(policyName.contains(BACKSLASH)){
            policyName = policyName.replace(BACKSLASH, ESCAPE_BACKSLASH);
        }
        String policyVersionQuery = "From PolicyVersion where policy_name = :policyName  and active_version = :version and id >0";
        SimpleBindings pvParams = new SimpleBindings();
        pvParams.put("policyName", policyName);
        pvParams.put(VERSION, version);
        List<Object> activeData = controller.getDataByQuery(policyVersionQuery, pvParams);
        if(!activeData.isEmpty()){
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
        if(!scopesList.isEmpty()){
            for(int i = 0; i < scopesList.size(); i++){
                PolicyEditorScopes tempScope = (PolicyEditorScopes) scopesList.get(i);
                scopes.add(tempScope.getScopeName());
            }
        }
    }

    //Switch Version Functionality
    private JSONObject switchVersion(JSONObject params, HttpServletRequest request) throws ServletException{
        String path = params.getString("path");
        String userId = null;
        try {
            userId = UserUtils.getUserSession(request).getOrgUserId();
        } catch (Exception e) {
            LOGGER.error("Exception Occured while reading userid from cookie" +e);
        }
        String policyName;
        String removeExtension = path.replace(".xml", "");
        if(path.startsWith(FORWARD_SLASH)){
            policyName = removeExtension.substring(1, removeExtension.lastIndexOf('.'));
        }else{
            policyName = removeExtension.substring(0, removeExtension.lastIndexOf('.'));
        }

        String activePolicy;
        PolicyController controller = getPolicyControllerInstance();
        if(! params.toString().contains("activeVersion")){
            return controller.switchVersionPolicyContent(policyName);
        }
        String activeVersion = params.getString("activeVersion");
        String highestVersion = params.get("highestVersion").toString();
        if(Integer.parseInt(activeVersion) > Integer.parseInt(highestVersion)){
            return error("The Version shouldn't be greater than Highest Value");
        }
        activePolicy = policyName + "." + activeVersion + ".xml";
        String[] splitDBCheckName = modifyPolicyName(activePolicy);
        String peQuery =   "FROM PolicyEntity where policyName = :splitDBCheckName_1 and scope = :splitDBCheckName_0";
        SimpleBindings policyParams = new SimpleBindings();
        policyParams.put("splitDBCheckName_1", splitDBCheckName[1]);
        policyParams.put("splitDBCheckName_0", splitDBCheckName[0]);
        List<Object> policyEntity = controller.getDataByQuery(peQuery, policyParams);
        PolicyEntity pentity = (PolicyEntity) policyEntity.get(0);
        if(pentity.isDeleted()){
            return error("The Policy is Not Existing in Workspace");
        }
        if(policyName.contains(FORWARD_SLASH)){
            policyName = policyName.replace(FORWARD_SLASH, File.separator);
        }
        policyName = policyName.substring(policyName.indexOf(File.separator)+1);
        if(policyName.contains(BACKSLASH)){
            policyName = policyName.replace(File.separator, BACKSLASH);
        }
        policyName = splitDBCheckName[0].replace(".", File.separator)+File.separator+policyName;
        String watchPolicyName = policyName;
        if(policyName.contains(FORWARD_SLASH)){
            policyName = policyName.replace(FORWARD_SLASH, File.separator);
        }
        if(policyName.contains(BACKSLASH)){
            policyName = policyName.replace(BACKSLASH, ESCAPE_BACKSLASH);
        }
        String query = "update PolicyVersion set active_version='"+activeVersion+"' where policy_name ='"+policyName+"'  and id >0";
        //query the database
        controller.executeQuery(query);
        //Policy Notification
        PolicyVersion entity = new PolicyVersion();
        entity.setPolicyName(watchPolicyName);
        entity.setActiveVersion(Integer.parseInt(activeVersion));
        entity.setModifiedBy(userId);
        controller.watchPolicyFunction(entity, activePolicy, "SwitchVersion");
        return success();
    }

    //Describe Policy
    private JSONObject describePolicy(JSONObject params) throws ServletException{
        JSONObject object = null;
        String path = params.getString("path");
        String policyName = null;
        if(path.startsWith(FORWARD_SLASH)){
            path = path.substring(1);
            policyName = path.substring(path.lastIndexOf('/') +1);
            path = path.replace(FORWARD_SLASH, ".");
        }else{
            path = path.replace(FORWARD_SLASH, ".");
            policyName = path;
        }
        if(path.contains(CONFIG_)){
            path = path.replace(CONFIG, CONFIG1);
        }else if(path.contains(ACTION_)){
            path = path.replace(ACTION, ACTION1);
        }else if(path.contains(DECISION_)){
            path = path.replace(DECISION, DECISION1);
        }
        PolicyController controller = getPolicyControllerInstance();
        String[] split = path.split(":");
        String query = "FROM PolicyEntity where policyName = :split_1 and scope = :split_0";
        SimpleBindings peParams = new SimpleBindings();
        peParams.put("split_1", split[1]);
        peParams.put("split_0", split[0]);
        List<Object> queryData = null;
        if(PolicyController.isjUnit()){
            queryData = controller.getDataByQuery(query, null);
        }else{
            queryData = controller.getDataByQuery(query, peParams);
        }
        if(queryData.isEmpty()){
            return error("Error Occured while Describing the Policy - query is empty");
        }
        PolicyEntity entity = (PolicyEntity) queryData.get(0);
        File temp = null;
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
            LOGGER.error("Exception Occured while Describing the Policy"+e);
        }
        object = HumanPolicyComponent.DescribePolicy(temp);
        if(temp != null){
            try {
                Files.delete(temp.toPath());
            } catch (IOException e) {
                LOGGER.warn("Failed to delete " + temp.getName() + e);
            }
        }
        return object;
    }

    //Get the List of Policies and Scopes for Showing in Editor tab
    private JSONObject list(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            return processPolicyList(params, request);
        } catch (Exception e) {
            LOGGER.error("list", e);
            return error(e.getMessage());
        }
    }

    private JSONObject processPolicyList(JSONObject params, HttpServletRequest request) throws ServletException {
        PolicyController controller = getPolicyControllerInstance();
        //Get the Login Id of the User from Request
        String testUserID = getTestUserId();
        String userId =  testUserID != null ? testUserID : UserUtils.getUserSession(request).getOrgUserId();
        List<Object> userRoles = controller.getRoles(userId);
        Pair<Set<String>, List<String>> pair = org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
        List<String> roles = pair.u;
        Set<String> scopes = pair.t;

        List<JSONObject> resultList = new ArrayList<>();
        boolean onlyFolders = params.getBoolean("onlyFolders");
        String path = params.getString("path");
        if(path.contains("..xml")){
            path = path.replaceAll("..xml", "").trim();
        }

        if (roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST) ) {
            if(scopes.isEmpty()){
                return error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
            }else{
                if(!FORWARD_SLASH.equals(path)){
                    String tempScope = path.substring(1, path.length());
                    tempScope = tempScope.replace(FORWARD_SLASH, File.separator);
                    scopes.add(tempScope);
                }
            }
        }

        if (!FORWARD_SLASH.equals(path)) {
            try{
                String scopeName = path.substring(path.indexOf('/') +1);
                activePolicyList(scopeName, resultList, roles, scopes, onlyFolders);
            } catch (Exception ex) {
                LOGGER.error("Error Occured While reading Policy Files List"+ex );
            }
            return new JSONObject().put(RESULT, resultList);
        }

        processRoles(scopes, roles, resultList);

        return new JSONObject().put(RESULT, resultList);
    }

    private void processRoles(Set<String> scopes, List<String> roles, List<JSONObject> resultList) {
        if(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)){
            List<Object> scopesList = queryPolicyEditorScopes(null);
            for(Object list : scopesList){
                PolicyEditorScopes scope = (PolicyEditorScopes) list;
                if(!(scope.getScopeName().contains(File.separator))){
                    JSONObject el = new JSONObject();
                    el.put(NAME, scope.getScopeName());
                    el.put(DATE, scope.getModifiedDate());
                    el.put(SIZE, "");
                    el.put(TYPE, "dir");
                    el.put(CREATED_BY, scope.getUserCreatedBy().getUserName());
                    el.put(MODIFIED_BY, scope.getUserModifiedBy().getUserName());
                    resultList.add(el);
                }
            }
        }else if(roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST)){
            for(Object scope : scopes){
                JSONObject el = new JSONObject();
                List<Object> scopesList = queryPolicyEditorScopes(scope.toString());
                if(!scopesList.isEmpty()){
                    PolicyEditorScopes scopeById = (PolicyEditorScopes) scopesList.get(0);
                    el.put(NAME, scopeById.getScopeName());
                    el.put(DATE, scopeById.getModifiedDate());
                    el.put(SIZE, "");
                    el.put(TYPE, "dir");
                    el.put(CREATED_BY, scopeById.getUserCreatedBy().getUserName());
                    el.put(MODIFIED_BY, scopeById.getUserModifiedBy().getUserName());
                    resultList.add(el);
                }
            }
        }
    }

    private List<Object> queryPolicyEditorScopes(String scopeName){
        String scopeNamequery;
        SimpleBindings params = new SimpleBindings();
        if(scopeName == null){
            scopeNamequery = "from PolicyEditorScopes";
        }else{
            scopeNamequery = "from PolicyEditorScopes where SCOPENAME like :scopeName";
            params.put("scopeName", scopeName + "%");
        }
        PolicyController controller = getPolicyControllerInstance();
        List<Object> scopesList;
        if(PolicyController.isjUnit()){
            scopesList = controller.getDataByQuery(scopeNamequery, null);
        }else{
            scopesList = controller.getDataByQuery(scopeNamequery, params);
        }
        return  scopesList;
    }

    //Get Active Policy List based on Scope Selection form Policy Version table
    private void activePolicyList(String inScopeName, List<JSONObject> resultList, List<String> roles, Set<String> scopes, boolean onlyFolders){
        PolicyController controller = getPolicyControllerInstance();
        String scopeName = inScopeName;
        if(scopeName.contains(FORWARD_SLASH)){
            scopeName = scopeName.replace(FORWARD_SLASH, File.separator);
        }
        if(scopeName.contains(BACKSLASH)){
            scopeName = scopeName.replace(BACKSLASH, ESCAPE_BACKSLASH);
        }
        String query = "from PolicyVersion where POLICY_NAME like :scopeName";
        String scopeNamequery = "from PolicyEditorScopes where SCOPENAME like :scopeName";

        SimpleBindings params = new SimpleBindings();
        params.put("scopeName", scopeName + "%");

        List<Object> activePolicies;
        List<Object> scopesList;
        if(PolicyController.isjUnit()){
            activePolicies = controller.getDataByQuery(query, null);
            scopesList = controller.getDataByQuery(scopeNamequery, null);
        }else{
            activePolicies = controller.getDataByQuery(query, params);
            scopesList = controller.getDataByQuery(scopeNamequery, params);
        }
        for(Object list : scopesList) {
            scopeName = checkScope(resultList, scopeName, (PolicyEditorScopes) list);
        }
        String scopeNameCheck;
        for (Object list : activePolicies) {
            PolicyVersion policy = (PolicyVersion) list;
            String scopeNameValue = policy.getPolicyName().substring(0, policy.getPolicyName().lastIndexOf(File.separator));
            if(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)){
                if(scopeName.contains(ESCAPE_BACKSLASH)){
                    scopeNameCheck = scopeName.replace(ESCAPE_BACKSLASH, File.separator);
                }else{
                    scopeNameCheck = scopeName;
                }
                if(scopeNameValue.equals(scopeNameCheck)){
                    JSONObject el = new JSONObject();
                    el.put(NAME, policy.getPolicyName().substring(policy.getPolicyName().lastIndexOf(File.separator)+1));
                    el.put(DATE, policy.getModifiedDate());
                    el.put(VERSION, policy.getActiveVersion());
                    el.put(SIZE, "");
                    el.put(TYPE, "file");
                    el.put(CREATED_BY, getUserName(policy.getCreatedBy()));
                    el.put(MODIFIED_BY, getUserName(policy.getModifiedBy()));
                    resultList.add(el);
                }
            }else if(!scopes.isEmpty() && scopes.contains(scopeNameValue)){
                JSONObject el = new JSONObject();
                el.put(NAME, policy.getPolicyName().substring(policy.getPolicyName().lastIndexOf(File.separator)+1));
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

    private String checkScope(List<JSONObject> resultList, String scopeName, PolicyEditorScopes scopeById) {
        String scope = scopeById.getScopeName();
        if(scope.contains(File.separator)){
            String targetScope = scope.substring(0, scope.lastIndexOf(File.separator));
            if(scopeName.contains(ESCAPE_BACKSLASH)){
                scopeName = scopeName.replace(ESCAPE_BACKSLASH, File.separator);
            }
            if(scope.contains(File.separator)){
                scope = scope.substring(targetScope.length()+1);
                if(scope.contains(File.separator)){
                    scope = scope.substring(0, scope.indexOf(File.separator));
                }
            }
            if(scopeName.equalsIgnoreCase(targetScope)){
                JSONObject el = new JSONObject();
                el.put(NAME, scope);
                el.put(DATE, scopeById.getModifiedDate());
                el.put(SIZE, "");
                el.put(TYPE, "dir");
                el.put(CREATED_BY, scopeById.getUserCreatedBy().getUserName());
                el.put(MODIFIED_BY, scopeById.getUserModifiedBy().getUserName());
                resultList.add(el);
            }
        }
        return scopeName;
    }

    private String getUserName(String loginId){
        PolicyController controller = getPolicyControllerInstance();
        UserInfo userInfo = (UserInfo) controller.getEntityItem(UserInfo.class, "userLoginId", loginId);
        if(userInfo == null){
            return SUPERADMIN;
        }
        return userInfo.getUserName();
    }

    //Rename Policy
    private JSONObject rename(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            return handlePolicyRename(params, request);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured While Renaming Policy"+e);
            return error(e.getMessage());
        }
    }

    private JSONObject handlePolicyRename(JSONObject params, HttpServletRequest request) throws ServletException {
        boolean isActive = false;
        List<String> policyActiveInPDP = new ArrayList<>();
        Set<String> scopeOfPolicyActiveInPDP = new HashSet<>();
        String userId = UserUtils.getUserSession(request).getOrgUserId();
        String oldPath = params.getString("path");
        String newPath = params.getString("newPath");
        oldPath = oldPath.substring(oldPath.indexOf('/')+1);
        newPath = newPath.substring(newPath.indexOf('/')+1);
        String checkValidation = null;
        if(oldPath.endsWith(".xml")){
            checkValidation = newPath.replace(".xml", "");
            checkValidation = checkValidation.substring(checkValidation.indexOf('_') + 1, checkValidation.lastIndexOf("."));
            checkValidation = checkValidation.substring(checkValidation.lastIndexOf(FORWARD_SLASH)+1);
            if(!PolicyUtils.policySpecialCharValidator(checkValidation).contains("success")){
                return error("Policy Rename Failed. The Name contains special characters.");
            }
            JSONObject result = policyRename(oldPath, newPath, userId);
            if(!(Boolean)(result.getJSONObject("result").get("success"))){
                return result;
            }
        }else{
            String scopeName = oldPath;
            String newScopeName = newPath;
            if(scopeName.contains(FORWARD_SLASH)){
                scopeName = scopeName.replace(FORWARD_SLASH, File.separator);
                newScopeName = newScopeName.replace(FORWARD_SLASH, File.separator);
            }
            checkValidation = newScopeName.substring(newScopeName.lastIndexOf(File.separator)+1);
            if(scopeName.contains(BACKSLASH)){
                scopeName = scopeName.replace(BACKSLASH, BACKSLASH_8TIMES);
                newScopeName = newScopeName.replace(BACKSLASH, BACKSLASH_8TIMES);
            }
            if(!PolicyUtils.policySpecialCharValidator(checkValidation).contains("success")){
                return error("Scope Rename Failed. The Name contains special characters.");
            }
            PolicyController controller = getPolicyControllerInstance();
            String query = "from PolicyVersion where POLICY_NAME like :scopeName";
            String scopeNamequery = "from PolicyEditorScopes where SCOPENAME like :scopeName";
            SimpleBindings pvParams = new SimpleBindings();
            pvParams.put("scopeName", scopeName + "%");
            List<Object> activePolicies = controller.getDataByQuery(query, pvParams);
            List<Object> scopesList = controller.getDataByQuery(scopeNamequery, pvParams);
            for(Object object : activePolicies){
                PolicyVersion activeVersion = (PolicyVersion) object;
                String policyOldPath = activeVersion.getPolicyName().replace(File.separator, FORWARD_SLASH) + "." + activeVersion.getActiveVersion() + ".xml";
                String policyNewPath = policyOldPath.replace(oldPath, newPath);
                JSONObject result = policyRename(policyOldPath, policyNewPath, userId);
                if(!(Boolean)(result.getJSONObject("result").get("success"))){
                    isActive = true;
                    policyActiveInPDP.add(policyOldPath);
                    String scope = policyOldPath.substring(0, policyOldPath.lastIndexOf('/'));
                    scopeOfPolicyActiveInPDP.add(scope.replace(FORWARD_SLASH, File.separator));
                }
            }
            boolean rename = false;
            if(activePolicies.size() != policyActiveInPDP.size()){
                rename = true;
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setUserLoginId(userId);
            if(policyActiveInPDP.isEmpty()){
                renameScope(scopesList, scopeName, newScopeName, controller);
            }else if(rename){
                renameScope(scopesList, scopeName, newScopeName, controller);
                for(String scope : scopeOfPolicyActiveInPDP){
                    PolicyEditorScopes editorScopeEntity = new PolicyEditorScopes();
                    editorScopeEntity.setScopeName(scope.replace(BACKSLASH, BACKSLASH_8TIMES));
                    editorScopeEntity.setUserCreatedBy(userInfo);
                    editorScopeEntity.setUserModifiedBy(userInfo);
                    controller.saveData(editorScopeEntity);
                }
            }
            if(isActive){
                return error("The Following policies rename failed. Since they are active in PDP Groups" +policyActiveInPDP);
            }
        }
        return success();
    }

    private void renameScope(List<Object> scopesList, String inScopeName, String newScopeName, PolicyController controller){
        for(Object object : scopesList){
            PolicyEditorScopes editorScopeEntity = (PolicyEditorScopes) object;
            String scopeName = inScopeName;
            if(scopeName.contains(BACKSLASH_8TIMES)){
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
            String policyName = policyVersionName.substring(0, policyVersionName.lastIndexOf('.')).replace(FORWARD_SLASH, File.separator);

            String oldpolicyVersionName = oldPath.replace(".xml", "");
            String oldpolicyName = oldpolicyVersionName.substring(0, oldpolicyVersionName.lastIndexOf('.')).replace(FORWARD_SLASH, File.separator);
            String newpolicyName = newPath.replace("/", ".");
            String[] newPolicySplit = modifyPolicyName(newPath);

            String[] oldPolicySplit = modifyPolicyName(oldPath);

            //Check PolicyEntity table with newPolicy Name
            String policyEntityquery = "FROM PolicyEntity where policyName = :newPolicySplit_1 and scope = :newPolicySplit_0";
            SimpleBindings policyParams = new SimpleBindings();
            policyParams.put("newPolicySplit_1", newPolicySplit[1]);
            policyParams.put("newPolicySplit_0", newPolicySplit[0]);
            List<Object> queryData = controller.getDataByQuery(policyEntityquery, policyParams);
            if(!queryData.isEmpty()){
                return error("Policy rename failed. Since, the policy with same name already exists.");
            }

            //Query the Policy Entity with oldPolicy Name
            String policyEntityCheck = oldPolicySplit[1].substring(0, oldPolicySplit[1].indexOf('.'));
            String oldpolicyEntityquery = "FROM PolicyEntity where policyName like :policyEntityCheck and scope = :oldPolicySplit_0";
            SimpleBindings params = new SimpleBindings();
            params.put("policyEntityCheck", policyEntityCheck + "%");
            params.put("oldPolicySplit_0", oldPolicySplit[0]);
            List<Object> oldEntityData = controller.getDataByQuery(oldpolicyEntityquery, params);
            if(oldEntityData.isEmpty()){
                return error("Policy rename failed due to policy not able to retrieve from database. Please, contact super-admin.");
            }

            StringBuilder groupQuery = new StringBuilder();
            groupQuery.append("FROM PolicyGroupEntity where (");
            SimpleBindings geParams = new SimpleBindings();
            for(int i=0; i<oldEntityData.size(); i++){
                entity = (PolicyEntity) oldEntityData.get(i);
                if(i == 0){
                    groupQuery.append("policyid = :policyId");
                    geParams.put("policyId", entity.getPolicyId());
                }else{
                    groupQuery.append(" or policyid = :policyId" + i);
                    geParams.put("policyId" + i, entity.getPolicyId());
                }
            }
            groupQuery.append(")");
            List<Object> groupEntityData = controller.getDataByQuery(groupQuery.toString(), geParams);
            if(! groupEntityData.isEmpty()){
                return error("Policy rename failed. Since the policy or its version is active in PDP Groups.");
            }
            for(int i=0; i<oldEntityData.size(); i++){
                entity = (PolicyEntity) oldEntityData.get(i);
                String checkEntityName = entity.getPolicyName().replace(".xml", "");
                checkEntityName = checkEntityName.substring(0, checkEntityName.lastIndexOf('.'));
                String originalPolicyName = oldpolicyName.substring(oldpolicyName.lastIndexOf(File.separator)+1);
                if(checkEntityName.equals(originalPolicyName)){
                    checkOldPolicyEntryAndUpdate(entity, newPolicySplit[0] , newPolicySplit[1], oldPolicySplit[0], oldPolicySplit[1], policyName, newpolicyName, oldpolicyName, userId);
                }
            }

            return success();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured While Renaming Policy"+e);
            return error(e.getMessage());
        }
    }

    private String[] modifyPolicyName(String pathName) {
        return modifyPolicyName(FORWARD_SLASH, pathName);
    }

    private String[] modifyPolicyName(String separator, String pathName) {
        String policyName = pathName.replace(separator, ".");
        if(policyName.contains(CONFIG_)){
            policyName = policyName.replace(CONFIG, CONFIG1);
        }else if(policyName.contains(ACTION_)){
            policyName = policyName.replace(ACTION, ACTION1);
        }else if(policyName.contains(DECISION_)){
            policyName = policyName.replace(DECISION, DECISION1);
        }
        return policyName.split(":");
    }

    private JSONObject checkOldPolicyEntryAndUpdate(PolicyEntity entity, String newScope, String removenewPolicyExtension, String oldScope, String removeoldPolicyExtension,
                                                    String policyName, String  newpolicyName, String oldpolicyName, String userId) throws ServletException{
        try {
            ConfigurationDataEntity configEntity = entity.getConfigurationData();
            ActionBodyEntity actionEntity = entity.getActionBodyEntity();
            PolicyController controller = getPolicyControllerInstance();

            String oldPolicyNameWithoutExtension = removeoldPolicyExtension;
            String newPolicyNameWithoutExtension = removenewPolicyExtension;
            if(removeoldPolicyExtension.endsWith(".xml")){
                oldPolicyNameWithoutExtension = oldPolicyNameWithoutExtension.substring(0, oldPolicyNameWithoutExtension.indexOf('.'));
                newPolicyNameWithoutExtension = newPolicyNameWithoutExtension.substring(0, newPolicyNameWithoutExtension.indexOf('.'));
            }
            entity.setPolicyName(entity.getPolicyName().replace(oldPolicyNameWithoutExtension, newPolicyNameWithoutExtension));
            entity.setPolicyData(entity.getPolicyData().replace(oldScope +"."+oldPolicyNameWithoutExtension, newScope+"."+newPolicyNameWithoutExtension));
            entity.setScope(newScope);
            entity.setModifiedBy(userId);

            String oldConfigurationName = null;
            String newConfigurationName = null;
            if(newpolicyName.contains(CONFIG_)){
                oldConfigurationName = configEntity.getConfigurationName();
                configEntity.setConfigurationName(configEntity.getConfigurationName().replace(oldScope +"."+oldPolicyNameWithoutExtension, newScope+"."+newPolicyNameWithoutExtension));
                controller.updateData(configEntity);
                newConfigurationName = configEntity.getConfigurationName();
                File file = new File(PolicyController.getConfigHome() + File.separator + oldConfigurationName);
                if(file.exists()){
                    File renamefile = new File(PolicyController.getConfigHome() + File.separator + newConfigurationName);
                    file.renameTo(renamefile);
                }
            }else if(newpolicyName.contains(ACTION_)){
                oldConfigurationName = actionEntity.getActionBodyName();
                actionEntity.setActionBody(actionEntity.getActionBody().replace(oldScope +"."+oldPolicyNameWithoutExtension, newScope+"."+newPolicyNameWithoutExtension));
                controller.updateData(actionEntity);
                newConfigurationName = actionEntity.getActionBodyName();
                File file = new File(PolicyController.getActionHome() + File.separator + oldConfigurationName);
                if(file.exists()){
                    File renamefile = new File(PolicyController.getActionHome() + File.separator + newConfigurationName);
                    file.renameTo(renamefile);
                }
            }
            controller.updateData(entity);

            PolicyRestController restController = new PolicyRestController();
            restController.notifyOtherPAPSToUpdateConfigurations("rename", newConfigurationName, oldConfigurationName);
            PolicyVersion versionEntity = (PolicyVersion) controller.getEntityItem(PolicyVersion.class, "policyName", oldpolicyName);
            versionEntity.setPolicyName(policyName);
            versionEntity.setModifiedBy(userId);
            controller.updateData(versionEntity);
            String movePolicyCheck = policyName.substring(policyName.lastIndexOf(File.separator)+1);
            String moveOldPolicyCheck = oldpolicyName.substring(oldpolicyName.lastIndexOf(File.separator)+1);
            if(movePolicyCheck.equals(moveOldPolicyCheck)){
                controller.watchPolicyFunction(versionEntity, oldpolicyName, "Move");
            }else{
                controller.watchPolicyFunction(versionEntity, oldpolicyName, "Rename");
            }
            return success();
        } catch (Exception e) {
            LOGGER.error(EXCEPTION_OCCURED +e);
            return error(e.getMessage());
        }
    }

    private JSONObject cloneRecord(String newpolicyName, String oldScope, String inRemoveoldPolicyExtension, String newScope, String inRemovenewPolicyExtension, PolicyEntity entity, String userId) throws ServletException{
        String queryEntityName;
        PolicyController controller = getPolicyControllerInstance();
        PolicyEntity cloneEntity = new PolicyEntity();
        cloneEntity.setPolicyName(newpolicyName);
        String removeoldPolicyExtension = inRemoveoldPolicyExtension;
        String removenewPolicyExtension = inRemovenewPolicyExtension;
        removeoldPolicyExtension = removeoldPolicyExtension.replace(".xml", "");
        removenewPolicyExtension = removenewPolicyExtension.replace(".xml", "");
        cloneEntity.setPolicyData(entity.getPolicyData().replace(oldScope+"."+removeoldPolicyExtension, newScope+"."+removenewPolicyExtension));
        cloneEntity.setScope(entity.getScope());
        String oldConfigRemoveExtension = removeoldPolicyExtension.replace(".xml", "");
        String newConfigRemoveExtension = removenewPolicyExtension.replace(".xml", "");
        String newConfigurationName = null;
        if(newpolicyName.contains(CONFIG_)){
            ConfigurationDataEntity configurationDataEntity = new ConfigurationDataEntity();
            configurationDataEntity.setConfigurationName(entity.getConfigurationData().getConfigurationName().replace(oldScope+"."+oldConfigRemoveExtension, newScope+"."+newConfigRemoveExtension));
            queryEntityName = configurationDataEntity.getConfigurationName();
            configurationDataEntity.setConfigBody(entity.getConfigurationData().getConfigBody());
            configurationDataEntity.setConfigType(entity.getConfigurationData().getConfigType());
            configurationDataEntity.setDeleted(false);
            configurationDataEntity.setCreatedBy(userId);
            configurationDataEntity.setModifiedBy(userId);
            controller.saveData(configurationDataEntity);
            ConfigurationDataEntity configEntiy = (ConfigurationDataEntity) controller.getEntityItem(ConfigurationDataEntity.class, "configurationName", queryEntityName);
            cloneEntity.setConfigurationData(configEntiy);
            newConfigurationName = configEntiy.getConfigurationName();
            try (FileWriter fw = new FileWriter(PolicyController.getConfigHome() + File.separator + newConfigurationName);
                 BufferedWriter bw = new BufferedWriter(fw)){
                bw.write(configEntiy.getConfigBody());
            } catch (IOException e) {
                LOGGER.error("Exception Occured While cloning the configuration file"+e);
            }
        }else if(newpolicyName.contains(ACTION_)){
            ActionBodyEntity actionBodyEntity = new ActionBodyEntity();
            actionBodyEntity.setActionBodyName(entity.getActionBodyEntity().getActionBodyName().replace(oldScope+"."+oldConfigRemoveExtension, newScope+"."+newConfigRemoveExtension));
            queryEntityName = actionBodyEntity.getActionBodyName();
            actionBodyEntity.setActionBody(entity.getActionBodyEntity().getActionBody());
            actionBodyEntity.setDeleted(false);
            actionBodyEntity.setCreatedBy(userId);
            actionBodyEntity.setModifiedBy(userId);
            controller.saveData(actionBodyEntity);
            ActionBodyEntity actionEntiy = (ActionBodyEntity) controller.getEntityItem(ActionBodyEntity.class, "actionBodyName", queryEntityName);
            cloneEntity.setActionBodyEntity(actionEntiy);
            newConfigurationName = actionEntiy.getActionBodyName();
            try (FileWriter fw = new FileWriter(PolicyController.getActionHome() + File.separator + newConfigurationName);
                 BufferedWriter bw = new BufferedWriter(fw)){
                bw.write(actionEntiy.getActionBody());
            } catch (IOException e) {
                LOGGER.error("Exception Occured While cloning the configuration file"+e);
            }
        }

        cloneEntity.setDeleted(entity.isDeleted());
        cloneEntity.setCreatedBy(userId);
        cloneEntity.setModifiedBy(userId);
        controller.saveData(cloneEntity);

        //Notify others paps regarding clone policy.
        PolicyRestController restController = new PolicyRestController();
        restController.notifyOtherPAPSToUpdateConfigurations("clonePolicy", newConfigurationName, null);
        return success();
    }

    //Clone the Policy
    private JSONObject copy(JSONObject params, HttpServletRequest request) throws ServletException {
        try {
            String userId = UserUtils.getUserSession(request).getOrgUserId();
            String oldPath = params.getString("path");
            String newPath = params.getString("newPath");
            oldPath = oldPath.substring(oldPath.indexOf('/')+1);
            newPath = newPath.substring(newPath.indexOf('/')+1);

            String policyVersionName = newPath.replace(".xml", "");
            String version = policyVersionName.substring(policyVersionName.indexOf('.')+1);
            String policyName = policyVersionName.substring(0, policyVersionName.lastIndexOf('.')).replace(FORWARD_SLASH, File.separator);

            String newpolicyName = newPath.replace(FORWARD_SLASH, ".");

            String orignalPolicyName = oldPath.replace(FORWARD_SLASH, ".");

            String newPolicyCheck = newpolicyName;
            if(newPolicyCheck.contains(CONFIG_)){
                newPolicyCheck = newPolicyCheck.replace(CONFIG, CONFIG1);
            }else if(newPolicyCheck.contains(ACTION_)){
                newPolicyCheck = newPolicyCheck.replace(ACTION, ACTION1);
            }else if(newPolicyCheck.contains(DECISION_)){
                newPolicyCheck = newPolicyCheck.replace(DECISION, DECISION1);
            }
            if(!newPolicyCheck.contains(":")){
                return error("Policy Clone Failed. The Name contains special characters.");
            }
            String[] newPolicySplit = newPolicyCheck.split(":");

            String checkValidation = newPolicySplit[1].replace(".xml", "");
            checkValidation = checkValidation.substring(checkValidation.indexOf('_') + 1, checkValidation.lastIndexOf("."));
            if(!PolicyUtils.policySpecialCharValidator(checkValidation).contains("success")){
                return error("Policy Clone Failed. The Name contains special characters.");
            }

            String[] oldPolicySplit = modifyPolicyName(orignalPolicyName);

            PolicyController controller = getPolicyControllerInstance();

            PolicyEntity entity = null;
            boolean success = false;

            //Check PolicyEntity table with newPolicy Name
            String policyEntityquery = "FROM PolicyEntity where policyName = :newPolicySplit_1 and scope = :newPolicySplit_0";
            SimpleBindings policyParams = new SimpleBindings();
            policyParams.put("newPolicySplit_1", newPolicySplit[1]);
            policyParams.put("newPolicySplit_0", newPolicySplit[0]);
            List<Object> queryData = controller.getDataByQuery(policyEntityquery, policyParams);
            if(!queryData.isEmpty()){
                return error("Policy already exists with same name");
            }

            //Query the Policy Entity with oldPolicy Name
            policyEntityquery = "FROM PolicyEntity where policyName = :oldPolicySplit_1 and scope = :oldPolicySplit_0";
            SimpleBindings peParams = new SimpleBindings();
            peParams.put("oldPolicySplit_1", oldPolicySplit[1]);
            peParams.put("oldPolicySplit_0", oldPolicySplit[0]);
            if(PolicyController.isjUnit()){
                queryData = controller.getDataByQuery(policyEntityquery, null);
            }else{
                queryData = controller.getDataByQuery(policyEntityquery, peParams);
            }
            if(!queryData.isEmpty()){
                entity = (PolicyEntity) queryData.get(0);
            }
            if(entity != null){
                cloneRecord(newPolicySplit[1], oldPolicySplit[0], oldPolicySplit[1],  newPolicySplit[0], newPolicySplit[1], entity, userId);
                success = true;
            }

            if(success){
                PolicyVersion entityItem = new PolicyVersion();
                entityItem.setActiveVersion(Integer.parseInt(version));
                entityItem.setHigherVersion(Integer.parseInt(version));
                entityItem.setPolicyName(policyName);
                entityItem.setCreatedBy(userId);
                entityItem.setModifiedBy(userId);
                entityItem.setModifiedDate(new Date());
                controller.saveData(entityItem);
            }

            LOGGER.debug("copy from: {} to: {}" + oldPath +newPath);

            return success();
        } catch (Exception e) {
            LOGGER.error("copy", e);
            return error(e.getMessage());
        }
    }

    //Delete Policy or Scope Functionality
    private JSONObject delete(JSONObject params, HttpServletRequest request) throws ServletException {
        PolicyController controller = getPolicyControllerInstance();
        PolicyRestController restController = new PolicyRestController();
        PolicyEntity policyEntity = null;
        String policyNamewithoutExtension;
        try {
            String userId = UserUtils.getUserSession(request).getOrgUserId();
            String deleteVersion = "";
            String path = params.getString("path");
            LOGGER.debug("delete {}" +path);
            if(params.has("deleteVersion")){
                deleteVersion  = params.getString("deleteVersion");
            }
            path = path.substring(path.indexOf('/')+1);
            String policyNamewithExtension = path.replace(FORWARD_SLASH, File.separator);
            String policyVersionName = policyNamewithExtension.replace(".xml", "");
            String query;
            SimpleBindings policyParams = new SimpleBindings();
            if(path.endsWith(".xml")){
                policyNamewithoutExtension = policyVersionName.substring(0, policyVersionName.lastIndexOf('.'));
                String[] split = modifyPolicyName(File.separator, policyNamewithoutExtension);
                query = "FROM PolicyEntity where policyName like :split_1 and scope = :split_0";
                policyParams.put("split_1", split[1] + "%");
                policyParams.put("split_0", split[0]);
            }else{
                policyNamewithoutExtension = path.replace(File.separator, ".");
                query = "FROM PolicyEntity where scope like :policyNamewithoutExtension";
                policyParams.put("policyNamewithoutExtension", policyNamewithoutExtension + "%");
            }

            List<Object> policyEntityobjects = controller.getDataByQuery(query, policyParams);
            String activePolicyName = null;
            boolean pdpCheck = false;
            if(path.endsWith(".xml")){
                policyNamewithoutExtension = policyNamewithoutExtension.replace(".", File.separator);
                int version = Integer.parseInt(policyVersionName.substring(policyVersionName.indexOf('.')+1));
                if("ALL".equals(deleteVersion)){
                    if(!policyEntityobjects.isEmpty()){
                        for(Object object : policyEntityobjects){
                            policyEntity = (PolicyEntity) object;
                            String groupEntityquery = "from PolicyGroupEntity where policyid ='"+policyEntity.getPolicyId()+"'";
                            SimpleBindings pgeParams = new SimpleBindings();
                            List<Object> groupobject = controller.getDataByQuery(groupEntityquery, pgeParams);
                            if(!groupobject.isEmpty()){
                                pdpCheck = true;
                                activePolicyName = policyEntity.getScope() +"."+ policyEntity.getPolicyName();
                            }else{
                                //Delete the entity from Elastic Search Database
                                String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
                                restController.deleteElasticData(searchFileName);
                                //Delete the entity from Policy Entity table
                                controller.deleteData(policyEntity);
                                if(policyNamewithoutExtension.contains(CONFIG_)){
                                    Files.deleteIfExists(Paths.get(PolicyController.getConfigHome() + File.separator + policyEntity.getConfigurationData().getConfigurationName()));
                                    controller.deleteData(policyEntity.getConfigurationData());
                                    restController.notifyOtherPAPSToUpdateConfigurations("delete", null, policyEntity.getConfigurationData().getConfigurationName());
                                }else if(policyNamewithoutExtension.contains(ACTION_)){
                                    Files.deleteIfExists(Paths.get(PolicyController.getActionHome() + File.separator + policyEntity.getActionBodyEntity().getActionBodyName()));
                                    controller.deleteData(policyEntity.getActionBodyEntity());
                                    restController.notifyOtherPAPSToUpdateConfigurations("delete", null, policyEntity.getActionBodyEntity().getActionBodyName());
                                }
                            }
                        }
                    }
                    //Policy Notification
                    PolicyVersion versionEntity = new PolicyVersion();
                    versionEntity.setPolicyName(policyNamewithoutExtension);
                    versionEntity.setModifiedBy(userId);
                    controller.watchPolicyFunction(versionEntity, policyNamewithExtension, "DeleteAll");
                    if(pdpCheck){
                        //Delete from policyVersion table
                        String getActivePDPPolicyVersion = activePolicyName.replace(".xml", "");
                        getActivePDPPolicyVersion = getActivePDPPolicyVersion.substring(getActivePDPPolicyVersion.lastIndexOf('.')+1);
                        String policyVersionQuery = "update PolicyVersion set active_version='"+getActivePDPPolicyVersion+"' , highest_version='"+getActivePDPPolicyVersion+"'  where policy_name ='" +policyNamewithoutExtension.replace(BACKSLASH, ESCAPE_BACKSLASH)+"' and id >0";
                        controller.executeQuery(policyVersionQuery);
                        return error("Policies with Same name has been deleted. Except the Active Policy in PDP.     PolicyName: "+activePolicyName);
                    }else{
                        //No Active Policy in PDP. So, deleting all entries from policyVersion table
                        String policyVersionQuery = "delete from PolicyVersion  where policy_name ='" +policyNamewithoutExtension.replace(BACKSLASH, ESCAPE_BACKSLASH)+"' and id >0";
                        controller.executeQuery(policyVersionQuery);
                    }
                }else if("CURRENT".equals(deleteVersion)){
                    String currentVersionPolicyName = policyNamewithExtension.substring(policyNamewithExtension.lastIndexOf(File.separator)+1);
                    String currentVersionScope = policyNamewithExtension.substring(0, policyNamewithExtension.lastIndexOf(File.separator)).replace(File.separator, ".");
                    query = "FROM PolicyEntity where policyName = :currentVersionPolicyName and scope = :currentVersionScope";

                    SimpleBindings peParams = new SimpleBindings();
                    peParams.put("currentVersionPolicyName", currentVersionPolicyName);
                    peParams.put("currentVersionScope", currentVersionScope);

                    List<Object> policyEntitys = controller.getDataByQuery(query, peParams);
                    if(!policyEntitys.isEmpty()){
                        policyEntity = (PolicyEntity) policyEntitys.get(0);
                    }
                    if(policyEntity == null){
                        return success();
                    }

                    String groupEntityquery = "from PolicyGroupEntity where policyid = :policyEntityId and policyid > 0";
                    SimpleBindings geParams = new SimpleBindings();
                    geParams.put("policyEntityId", policyEntity.getPolicyId());
                    List<Object> groupobject = controller.getDataByQuery(groupEntityquery, geParams);
                    if(!groupobject.isEmpty()){
                        return error("Policy can't be deleted, it is active in PDP Groups.     PolicyName: '"+policyEntity.getScope() + "." +policyEntity.getPolicyName()+"'");
                    }

                    //Delete the entity from Elastic Search Database
                    String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
                    restController.deleteElasticData(searchFileName);
                    //Delete the entity from Policy Entity table
                    controller.deleteData(policyEntity);
                    if(policyNamewithoutExtension.contains(CONFIG_)){
                        Files.deleteIfExists(Paths.get(PolicyController.getConfigHome() + File.separator + policyEntity.getConfigurationData().getConfigurationName()));
                        controller.deleteData(policyEntity.getConfigurationData());
                        restController.notifyOtherPAPSToUpdateConfigurations("delete", null, policyEntity.getConfigurationData().getConfigurationName());
                    }else if(policyNamewithoutExtension.contains(ACTION_)){
                        Files.deleteIfExists(Paths.get(PolicyController.getActionHome() + File.separator + policyEntity.getActionBodyEntity().getActionBodyName()));
                        controller.deleteData(policyEntity.getActionBodyEntity());
                        restController.notifyOtherPAPSToUpdateConfigurations("delete", null, policyEntity.getActionBodyEntity().getActionBodyName());
                    }

                    if(version > 1){
                        int highestVersion = 0;
                        if(!policyEntityobjects.isEmpty()){
                            for(Object object : policyEntityobjects){
                                policyEntity = (PolicyEntity) object;
                                String policyEntityName = policyEntity.getPolicyName().replace(".xml", "");
                                int policyEntityVersion = Integer.parseInt(policyEntityName.substring(policyEntityName.lastIndexOf('.')+1));
                                if(policyEntityVersion > highestVersion && policyEntityVersion != version){
                                    highestVersion = policyEntityVersion;
                                }
                            }
                        }

                        //Policy Notification
                        PolicyVersion entity = new PolicyVersion();
                        entity.setPolicyName(policyNamewithoutExtension);
                        entity.setActiveVersion(highestVersion);
                        entity.setModifiedBy(userId);
                        controller.watchPolicyFunction(entity, policyNamewithExtension, "DeleteOne");

                        String updatequery = "";
                        if(highestVersion != 0){
                            updatequery = "update PolicyVersion set active_version='"+highestVersion+"' , highest_version='"+highestVersion+"' where policy_name ='" +policyNamewithoutExtension.replace("\\", "\\\\")+"'";
                        }else{
                            updatequery = "delete from PolicyVersion  where policy_name ='" +policyNamewithoutExtension.replace("\\", "\\\\")+"' and id >0";
                        }
                        controller.executeQuery(updatequery);
                    }else{
                        String policyVersionQuery = "delete from PolicyVersion  where policy_name ='" +policyNamewithoutExtension.replace("\\", "\\\\")+"' and id >0";
                        controller.executeQuery(policyVersionQuery);
                    }
                }
            }else{
                List<String> activePoliciesInPDP = new ArrayList<>();
                if(policyEntityobjects.isEmpty()){
                    String policyScopeQuery = "delete PolicyEditorScopes where SCOPENAME like '"+path.replace(BACKSLASH, ESCAPE_BACKSLASH)+"%' and id >0";
                    controller.executeQuery(policyScopeQuery);
                }
                for(Object object : policyEntityobjects){
                    policyEntity = (PolicyEntity) object;
                    String groupEntityquery = "from PolicyGroupEntity where policyid = :policyEntityId";
                    SimpleBindings geParams = new SimpleBindings();
                    geParams.put("policyEntityId", policyEntity.getPolicyId());
                    List<Object> groupobject = controller.getDataByQuery(groupEntityquery, geParams);
                    if(!groupobject.isEmpty()){
                        pdpCheck = true;
                        activePoliciesInPDP.add(policyEntity.getScope()+"."+policyEntity.getPolicyName());
                    }else{
                        //Delete the entity from Elastic Search Database
                        String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
                        restController.deleteElasticData(searchFileName);
                        //Delete the entity from Policy Entity table
                        controller.deleteData(policyEntity);
                        policyNamewithoutExtension = policyEntity.getPolicyName();
                        if(policyNamewithoutExtension.contains(CONFIG_)){
                            Files.deleteIfExists(Paths.get(PolicyController.getConfigHome() + File.separator + policyEntity.getConfigurationData().getConfigurationName()));
                            controller.deleteData(policyEntity.getConfigurationData());
                            restController.notifyOtherPAPSToUpdateConfigurations("delete", null, policyEntity.getConfigurationData().getConfigurationName());
                        }else if(policyNamewithoutExtension.contains(ACTION_)){
                            Files.deleteIfExists(Paths.get(PolicyController.getActionHome() + File.separator + policyEntity.getActionBodyEntity().getActionBodyName()));
                            controller.deleteData(policyEntity.getActionBodyEntity());
                            restController.notifyOtherPAPSToUpdateConfigurations("delete", null, policyEntity.getActionBodyEntity().getActionBodyName());
                        }
                    }
                }
                //Delete from policyVersion and policyEditor Scope table
                String policyVersionQuery = "delete PolicyVersion where POLICY_NAME like '"+path.replace(BACKSLASH, ESCAPE_BACKSLASH)+"%' and id >0";
                controller.executeQuery(policyVersionQuery);

                //Policy Notification
                PolicyVersion entity = new PolicyVersion();
                entity.setPolicyName(path);
                entity.setModifiedBy(userId);
                controller.watchPolicyFunction(entity, path, "DeleteScope");
                if(pdpCheck){
                    //Add Active Policies List to PolicyVersionTable
                    for(int i =0; i < activePoliciesInPDP.size(); i++){
                        String activePDPPolicyName = activePoliciesInPDP.get(i).replace(".xml", "");
                        int activePDPPolicyVersion = Integer.parseInt(activePDPPolicyName.substring(activePDPPolicyName.lastIndexOf('.')+1));
                        activePDPPolicyName = activePDPPolicyName.substring(0, activePDPPolicyName.lastIndexOf('.')).replace(".", File.separator);
                        PolicyVersion insertactivePDPVersion = new PolicyVersion();
                        insertactivePDPVersion.setPolicyName(activePDPPolicyName);
                        insertactivePDPVersion.setHigherVersion(activePDPPolicyVersion);
                        insertactivePDPVersion.setActiveVersion(activePDPPolicyVersion);
                        insertactivePDPVersion.setCreatedBy(userId);
                        insertactivePDPVersion.setModifiedBy(userId);
                        controller.saveData(insertactivePDPVersion);
                    }

                    return error("All the Policies has been deleted in Scope. Except the following list of Policies:"+activePoliciesInPDP);
                }else{
                    String policyScopeQuery = "delete PolicyEditorScopes where SCOPENAME like '"+path.replace(BACKSLASH, ESCAPE_BACKSLASH)+"%' and id >0";
                    controller.executeQuery(policyScopeQuery);
                }

            }
            return success();
        } catch (Exception e) {
            LOGGER.error("delete", e);
            return error(e.getMessage());
        }
    }

    //Edit the Policy
    private JSONObject editFile(JSONObject params) throws ServletException {
        // get content
        try {
            PolicyController controller = getPolicyControllerInstance();
            String mode = params.getString("mode");
            String path = params.getString("path");
            LOGGER.debug("editFile path: {}"+ path);

            String domain = path.substring(1, path.lastIndexOf('/'));
            domain = domain.replace(FORWARD_SLASH, ".");

            path = path.substring(1);
            path = path.replace(FORWARD_SLASH, ".");

            String[] split = modifyPolicyName(path);

            String query = "FROM PolicyEntity where policyName = :split_1 and scope = :split_0";
            SimpleBindings peParams = new SimpleBindings();
            peParams.put("split_1", split[1]);
            peParams.put("split_0", split[0]);
            List<Object> queryData;
            if(PolicyController.isjUnit()){
                queryData = controller.getDataByQuery(query, null);
            }else{
                queryData = controller.getDataByQuery(query, peParams);
            }
            PolicyEntity entity = (PolicyEntity) queryData.get(0);
            InputStream stream = new ByteArrayInputStream(entity.getPolicyData().getBytes(StandardCharsets.UTF_8));


            Object policy = XACMLPolicyScanner.readPolicy(stream);
            PolicyRestAdapter policyAdapter  = new PolicyRestAdapter();
            policyAdapter.setData(policy);

            if("viewPolicy".equalsIgnoreCase(mode)){
                policyAdapter.setReadOnly(true);
                policyAdapter.setEditPolicy(false);
            }else{
                policyAdapter.setReadOnly(false);
                policyAdapter.setEditPolicy(true);
            }

            policyAdapter.setDomainDir(domain);
            policyAdapter.setPolicyData(policy);
            String policyName = path.replace(".xml", "");
            policyName = policyName.substring(0, policyName.lastIndexOf('.'));
            policyAdapter.setPolicyName(policyName.substring(policyName.lastIndexOf('.')+1));

            PolicyAdapter setpolicyAdapter = PolicyAdapter.getInstance();
            setpolicyAdapter.configure(policyAdapter,entity);

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

    //Add Scopes
    private JSONObject addFolder(JSONObject params, HttpServletRequest request) throws ServletException {
        PolicyController controller = getPolicyControllerInstance();
        String name = "";
        try {
            String userId = UserUtils.getUserSession(request).getOrgUserId();
            String path = params.getString("path");
            try{
                if(params.has("subScopename")){
                    if(! "".equals(params.getString("subScopename"))) {
                        name = params.getString("path").replace(FORWARD_SLASH, File.separator) + File.separator +params.getString("subScopename");
                    }
                }else{
                    name = params.getString(NAME);
                }
            }catch(Exception e){
                name = params.getString(NAME);
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Adding Scope"+e);
            }
            String validateName;
            if(name.contains(File.separator)){
                validateName = name.substring(name.lastIndexOf(File.separator)+1);
            }else{
                validateName = name;
            }
            if(!name.isEmpty()){
                String validate = PolicyUtils.policySpecialCharValidator(validateName);
                if(!validate.contains("success")){
                    return error(validate);
                }
            }
            LOGGER.debug("addFolder path: {} name: {}" + path +name);
            if(! "".equals(name)){
                if(name.startsWith(File.separator)){
                    name = name.substring(1);
                }
                PolicyEditorScopes entity = (PolicyEditorScopes) controller.getEntityItem(PolicyEditorScopes.class, "scopeName", name);
                if(entity == null){
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserLoginId(userId);
                    PolicyEditorScopes newScope = new PolicyEditorScopes();
                    newScope.setScopeName(name);
                    newScope.setUserCreatedBy(userInfo);
                    newScope.setUserModifiedBy(userInfo);
                    controller.saveData(newScope);
                }else{
                    return error("Scope Already Exists");
                }
            }
            return success();
        } catch (Exception e) {
            LOGGER.error("addFolder", e);
            return error(e.getMessage());
        }
    }

    //Return Error Object
    private JSONObject error(String msg) throws ServletException {
        try {
            JSONObject result = new JSONObject();
            result.put("success", false);
            result.put("error", msg);
            return new JSONObject().put(RESULT, result);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    //Return Success Object
    private JSONObject success() throws ServletException {
        try {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("error", (Object) null);
            return new JSONObject().put(RESULT, result);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private PolicyController getPolicyControllerInstance(){
        return policyController != null ? getPolicyController() : new PolicyController();
    }

    public String getTestUserId() {
        return testUserId;
    }

    public static void setTestUserId(String testUserId) {
        PolicyManagerServlet.testUserId = testUserId;
    }
}