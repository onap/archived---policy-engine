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

/*
 * 
 * 
 * 
 * */
package org.openecomp.policy.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.components.HumanPolicyComponent;
import org.openecomp.policy.controller.PolicyController;
import org.openecomp.policy.controller.PolicyExportAndImportController;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.XACMLRest;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.jpa.ActionBodyEntity;
import org.openecomp.policy.rest.jpa.ConfigurationDataEntity;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import org.openecomp.portalsdk.core.web.support.UserUtils;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet(value ="/fm/*",  loadOnStartup = 1, initParams = { @WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.admin.properties", description = "The location of the properties file holding configuration information.") })
public class PolicyManagerServlet extends HttpServlet {
	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyManagerServlet.class);
	private static final long serialVersionUID = -8453502699403909016L;

	private enum Mode {
		LIST, RENAME, COPY, DELETE, EDITFILE, ADDFOLDER, DESCRIBEPOLICYFILE, VIEWPOLICY, ADDSUBSCOPE, SWITCHVERSION, EXPORT
	}

	private static String CONTENTTYPE = "application/json";
	private static String SUPERADMIN = "super-admin";
	private static String SUPEREDITOR = "super-editor";
	private static String SUPERGUEST = "super-guest";
	private static String ADMIN = "admin";
	private static String EDITOR = "editor";
	private static String GUEST = "guest";
	private static String RESULT = "result";
	
	private static Path closedLoopJsonLocation;
	private static JsonArray policyNames;
	
	public static JsonArray getPolicyNames() {
		return policyNames;
	}

	public static void setPolicyNames(JsonArray policyNames) {
		PolicyManagerServlet.policyNames = policyNames;
	}

	private static List<String> serviceTypeNamesList = new ArrayList<String>();

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
		FileInputStream inputStream = null;
		String location = closedLoopJsonLocation.toString();
		try {
			inputStream = new FileInputStream(location);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (location.endsWith("json")) {
			JsonReader jsonReader = null;
			jsonReader = Json.createReader(inputStream);
			policyNames = jsonReader.readArray();
			serviceTypeNamesList = new ArrayList<String>();
			for (int i = 0; i < policyNames.size(); i++) {
				javax.json.JsonObject policyName = policyNames.getJsonObject(i);
				String name = policyName.getJsonString("serviceTypePolicyName").getString();
				serviceTypeNamesList.add(name);
			}
			jsonReader.close();
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
			setError(e, response);
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
			response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, x.getMessage());
		}
	}

	//Policy Import Functionality
	private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String newFile;
			Map<String, InputStream> files = new HashMap<String, InputStream>();

			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item : items) {
				if (!item.isFormField()) {
					// Process form file field (input type="file").
					files.put(item.getName(), item.getInputStream());
					if(item.getName().endsWith(".xls")){
						try{
							File file = new File(item.getName());
							OutputStream outputStream = new FileOutputStream(file);
							IOUtils.copy(item.getInputStream(), outputStream);
							outputStream.close();
							newFile = file.toString();
							PolicyExportAndImportController importController = new PolicyExportAndImportController();
							importController.importRepositoryFile(newFile, request);
						}catch(Exception e){
							LOGGER.error("Upload error : " + e);
						}
					}
				}
			}

			JSONObject responseJsonObject = null;
			responseJsonObject = this.success();
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(responseJsonObject);
			out.flush();
		} catch (Exception e) {
			LOGGER.debug("Cannot write file");
			throw new ServletException("Cannot write file", e);
		}
	}

	//File Operation Functionality
	private void fileOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject responseJsonObject = null;
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = request.getReader();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();
			JSONObject jObj = new JSONObject(sb.toString());
			JSONObject params = jObj.getJSONObject("params");
			Mode mode = Mode.valueOf(params.getString("mode"));
			switch (mode) {
			case ADDFOLDER:
				responseJsonObject = addFolder(params, request);
				break;
			case COPY:
				responseJsonObject = copy(params, request);
				break;
			case DELETE:
				responseJsonObject = delete(params, request);
				break;
			case EDITFILE: 
				responseJsonObject = editFile(params);
				break;
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
			case ADDSUBSCOPE:
				responseJsonObject = addFolder(params, request);
				break;
			case SWITCHVERSION:
				responseJsonObject = switchVersion(params, request);
				break;
			default:
				throw new ServletException("not implemented");
			}
			if (responseJsonObject == null) {
				responseJsonObject = error("generic error : responseJsonObject is null");
			}
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While doing File Operation" + e);
			responseJsonObject = error(e.getMessage());
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(responseJsonObject);
		out.flush();
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
		if(path.startsWith("/")){
			policyName = removeExtension.substring(1, removeExtension.lastIndexOf("."));
		}else{
			policyName = removeExtension.substring(0, removeExtension.lastIndexOf("."));
		}

		String activePolicy = null;
		PolicyController controller = new PolicyController();
		if(params.toString().contains("activeVersion")){
			String activeVersion = params.getString("activeVersion");
			String highestVersion = params.get("highestVersion").toString();
			if(Integer.parseInt(activeVersion) > Integer.parseInt(highestVersion)){
				return error("The Version shouldn't be greater than Highest Value");
			}else{
				activePolicy = policyName + "." + activeVersion + ".xml";
				String dbCheckName = activePolicy.replace("/", ".");
				if(dbCheckName.contains("Config_")){
					dbCheckName = dbCheckName.replace(".Config_", ":Config_");
				}else if(dbCheckName.contains("Action_")){
					dbCheckName = dbCheckName.replace(".Action_", ":Action_");
				}else if(dbCheckName.contains("Decision_")){
					dbCheckName = dbCheckName.replace(".Decision_", ":Decision_");
				}
				String[] splitDBCheckName = dbCheckName.split(":");
				String peQuery =   "FROM PolicyEntity where policyName = '"+splitDBCheckName[1]+"' and scope ='"+splitDBCheckName[0]+"'";
				List<Object> policyEntity = controller.getDataByQuery(peQuery);
				PolicyEntity pentity = (PolicyEntity) policyEntity.get(0);
				if(pentity.isDeleted()){
					return error("The Policy is Not Existing in Workspace");
				}else{
					if(policyName.contains("/")){
						policyName = policyName.replace("/", File.separator);
					}
					policyName = policyName.substring(policyName.indexOf(File.separator)+1);
					if(policyName.contains("\\")){
						policyName = policyName.replace(File.separator, "\\");
					}
					policyName = splitDBCheckName[0].replace(".", File.separator)+File.separator+policyName;
					String watchPolicyName = policyName;
					if(policyName.contains("/")){
						policyName = policyName.replace("/", File.separator);
					}
					if(policyName.contains("\\")){
						policyName = policyName.replace("\\", "\\\\");
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
			}
		}
		return controller.switchVersionPolicyContent(policyName);
	}

	//Describe Policy
	private JSONObject describePolicy(JSONObject params) throws ServletException{
		JSONObject object = null;
		String path = params.getString("path");
		String policyName = null;
		if(path.startsWith("/")){
			path = path.substring(1);
			policyName = path.substring(path.lastIndexOf("/") +1);
			path = path.replace("/", ".");
		}else{
			path = path.replace("/", ".");
		}
		if(path.contains("Config_")){
			path = path.replace(".Config_", ":Config_");
		}else if(path.contains("Action_")){
			path = path.replace(".Action_", ":Action_");
		}else if(path.contains("Decision_")){
			path = path.replace(".Decision_", ":Decision_");
		}
		PolicyController controller = new PolicyController();
		String[] split = path.split(":");
		String query = "FROM PolicyEntity where policyName = '"+split[1]+"' and scope ='"+split[0]+"'";
		List<Object> queryData = controller.getDataByQuery(query);
		if(queryData != null){
			PolicyEntity entity = (PolicyEntity) queryData.get(0);
			File temp = null;
			try {
				temp = File.createTempFile(policyName, ".tmp");
				BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
				bw.write(entity.getPolicyData());
				bw.close();
				object = HumanPolicyComponent.DescribePolicy(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				temp.delete();
			}
		}else{
			return error("Error Occured while Describing the Policy");
		}
		
		return object;
	}

	//Get the List of Policies and Scopes for Showing in Editor tab
	private JSONObject list(JSONObject params, HttpServletRequest request) throws ServletException { 
		Set<String> scopes = null;
		List<String> roles = null;
		try {
			//Get the Login Id of the User from Request
			String userId =  UserUtils.getUserSession(request).getOrgUserId();
			//Check if the Role and Scope Size are Null get the values from db. 
			List<Object> userRoles = PolicyController.getRoles(userId);
			roles = new ArrayList<String>();
			scopes = new HashSet<String>();
			for(Object role: userRoles){
				Roles userRole = (Roles) role;
				roles.add(userRole.getRole());
				if(userRole.getScope() != null){
					if(userRole.getScope().contains(",")){
						String[] multipleScopes = userRole.getScope().split(",");
						for(int i =0; i < multipleScopes.length; i++){
							scopes.add(multipleScopes[i]);
						}
					}else{
						scopes.add(userRole.getScope());
					}		
				}
			}
			if (roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST) ) {
				if(scopes.isEmpty()){
					return error("No Scopes has been Assigned to the User. Please, Contact Super-Admin");
				}
			} 

			List<JSONObject> resultList = new ArrayList<JSONObject>();
			boolean onlyFolders = params.getBoolean("onlyFolders");
			String path = params.getString("path");
			if(path.contains("..xml")){
				path = path.replaceAll("..xml", "").trim();
			}


			if("/".equals(path)){
				if(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)){
					List<Object> scopesList = queryPolicyEditorScopes(null);
					for(Object list : scopesList){
						PolicyEditorScopes scope = (PolicyEditorScopes) list;
						if(!(scope.getScopeName().contains(File.separator))){
							JSONObject el = new JSONObject();
							el.put("name", scope.getScopeName());	
							el.put("date", scope.getCreatedDate());
							el.put("size", "");
							el.put("type", "dir");
							el.put("createdBy", scope.getUserCreatedBy().getUserName());
							el.put("modifiedBy", scope.getUserModifiedBy().getUserName());
							resultList.add(el);
						}
					}
				}else if(roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST)){
					for(Object scope : scopes){
						JSONObject el = new JSONObject();
						List<Object> scopesList = queryPolicyEditorScopes(scope.toString());
						PolicyEditorScopes scopeById = (PolicyEditorScopes) scopesList.get(0);
						el.put("name", scopeById.getScopeName());	
						el.put("date", scopeById.getCreatedDate());
						el.put("size", "");
						el.put("type", "dir");
						el.put("createdBy", scopeById.getUserCreatedBy().getUserName());
						el.put("modifiedBy", scopeById.getUserModifiedBy().getUserName());
						resultList.add(el);
					}
				}
			}else{
				try{
					String scopeName = path.substring(path.indexOf("/") +1);
					activePolicyList(scopeName, resultList, roles, scopes, onlyFolders);
				} catch (Exception ex) {
					LOGGER.error("Error Occured While reading Policy Files List"+ex );
				}			
			}

			return new JSONObject().put(RESULT, resultList);
		} catch (Exception e) {
			LOGGER.error("list", e);
			return error(e.getMessage());
		}
	}

	private List<Object> queryPolicyEditorScopes(String scopeName){
		String scopeNamequery = "";
		if(scopeName == null){
			scopeNamequery = "from PolicyEditorScopes";
		}else{
			scopeNamequery = "from PolicyEditorScopes where SCOPENAME like'" +scopeName+"'";
		}
		PolicyController controller = new PolicyController();
		List<Object> scopesList = controller.getDataByQuery(scopeNamequery);
		return  scopesList;
	}

	//Get Active Policy List based on Scope Selection form Policy Version table
	private void activePolicyList(String scopeName, List<JSONObject> resultList, List<String> roles, Set<String> scopes, boolean onlyFolders){
		PolicyController controller = new PolicyController();
		if(scopeName.contains("/")){
			scopeName = scopeName.replace("/", File.separator);
		}
		if(scopeName.contains("\\")){
			scopeName = scopeName.replace("\\", "\\\\\\\\");
		}
		String query = "from PolicyVersion where POLICY_NAME like'" +scopeName+"%'";
		String scopeNamequery = "from PolicyEditorScopes where SCOPENAME like'" +scopeName+"%'";
		List<Object> activePolicies = controller.getDataByQuery(query);
		List<Object> scopesList = controller.getDataByQuery(scopeNamequery);
		for(Object list : scopesList){
			PolicyEditorScopes scopeById = (PolicyEditorScopes) list;
			String scope = scopeById.getScopeName();
			if(scope.contains(File.separator)){
				String checkScope = scope.substring(0, scope.lastIndexOf(File.separator));
				if(scopeName.contains("\\\\")){
					scopeName = scopeName.replace("\\\\", File.separator);
				}
				if(scope.contains(File.separator)){
					scope = scope.substring(checkScope.length()+1);
					if(scope.contains(File.separator)){
						scope = scope.substring(0, scope.indexOf(File.separator));
					}
				}
				if(scopeName.equalsIgnoreCase(checkScope)){
					JSONObject el = new JSONObject();
					el.put("name", scope);	
					el.put("date", scopeById.getModifiedDate());
					el.put("size", "");
					el.put("type", "dir");
					el.put("createdBy", scopeById.getUserCreatedBy().getUserName());
					el.put("modifiedBy", scopeById.getUserModifiedBy().getUserName());
					resultList.add(el);
				}
			}		
		}
		String scopeNameCheck = null;
		for (Object list : activePolicies) {
			PolicyVersion policy = (PolicyVersion) list;
			String scopeNameValue = policy.getPolicyName().substring(0, policy.getPolicyName().lastIndexOf(File.separator));
			if(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)){
				if((scopeName.contains("\\\\"))){
					scopeNameCheck = scopeName.replace("\\\\", File.separator);
				}else{
					scopeNameCheck = scopeName;
				}
				if(scopeNameValue.equals(scopeNameCheck)){
					JSONObject el = new JSONObject();
					el.put("name", policy.getPolicyName().substring(policy.getPolicyName().lastIndexOf(File.separator)+1));	
					el.put("date", policy.getModifiedDate());
					el.put("version", policy.getActiveVersion());
					el.put("size", "");
					el.put("type", "file");
					el.put("createdBy", getUserName(policy.getCreatedBy()));
					el.put("modifiedBy", getUserName(policy.getModifiedBy()));
					resultList.add(el);
				}
			}else if(!scopes.isEmpty()){
				if(scopes.contains(scopeNameValue)){
					JSONObject el = new JSONObject();
					el.put("name", policy.getPolicyName().substring(policy.getPolicyName().lastIndexOf(File.separator)+1));	
					el.put("date", policy.getModifiedDate());
					el.put("version", policy.getActiveVersion());
					el.put("size", "");
					el.put("type", "file");
					el.put("createdBy", getUserName(policy.getCreatedBy()));
					el.put("modifiedBy", getUserName(policy.getModifiedBy()));
					resultList.add(el);
				}
			}
		}	
	}

	private String getUserName(String loginId){
		PolicyController controller = new PolicyController();
		UserInfo userInfo = (UserInfo) controller.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return userInfo.getUserName();
	}

	//Rename Policy
	private JSONObject rename(JSONObject params, HttpServletRequest request) throws ServletException {
		try {
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			String oldPath = params.getString("path");
			String newPath = params.getString("newPath");
			oldPath = oldPath.substring(oldPath.indexOf("/")+1);
			newPath = newPath.substring(newPath.indexOf("/")+1);
			if(oldPath.endsWith(".xml")){
				policyRename(oldPath, newPath, userId);
			}else{
				String scopeName = oldPath;
				String newScopeName = newPath;
				if(scopeName.contains("/")){
					scopeName = scopeName.replace("/", File.separator);
					newScopeName = newScopeName.replace("/", File.separator);
				}
				if(scopeName.contains("\\")){
					scopeName = scopeName.replace("\\", "\\\\\\\\");
					newScopeName = newScopeName.replace("\\", "\\\\\\\\");
				}
				PolicyController controller = new PolicyController();
				String query = "from PolicyVersion where POLICY_NAME like'" +scopeName+"%'";
				String scopeNamequery = "from PolicyEditorScopes where SCOPENAME like'" +scopeName+"%'";
				List<Object> activePolicies = controller.getDataByQuery(query);
				List<Object> scopesList = controller.getDataByQuery(scopeNamequery);
				for(Object object : activePolicies){
					PolicyVersion activeVersion = (PolicyVersion) object;
					String policyOldPath = activeVersion.getPolicyName().replace(File.separator, "/") + "." + activeVersion.getActiveVersion() + ".xml";
					String policyNewPath = policyOldPath.replace(oldPath, newPath);
					policyRename(policyOldPath, policyNewPath, userId);
				}
				for(Object object : scopesList){
					PolicyEditorScopes editorScopeEntity = (PolicyEditorScopes) object;
					if(scopeName.contains("\\\\\\\\")){
						scopeName = scopeName.replace("\\\\\\\\", File.separator);
						newScopeName = newScopeName.replace("\\\\\\\\", File.separator);
					}
					String scope = editorScopeEntity.getScopeName().replace(scopeName, newScopeName);
					editorScopeEntity.setScopeName(scope);
					controller.updateData(editorScopeEntity);
				}
			}
			return success();
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured While Renaming Policy"+e);
			return error(e.getMessage());
		}
	}
	
	private JSONObject policyRename(String oldPath, String newPath, String userId) throws ServletException {
		try {
			PolicyEntity entity = null;
			PolicyController controller = new PolicyController();
			
			String policyVersionName = newPath.replace(".xml", "");
			String policyName = policyVersionName.substring(0, policyVersionName.lastIndexOf(".")).replace("/", File.separator);

			String oldpolicyVersionName = oldPath.replace(".xml", "");
			String oldpolicyName = oldpolicyVersionName.substring(0, oldpolicyVersionName.lastIndexOf(".")).replace("/", File.separator);

			String newpolicyName = newPath.replace("/", ".");
			String newPolicyCheck = newpolicyName;
			if(newPolicyCheck.contains("Config_")){
				newPolicyCheck = newPolicyCheck.replace(".Config_", ":Config_");
			}else if(newPolicyCheck.contains("Action_")){
				newPolicyCheck = newPolicyCheck.replace(".Action_", ":Action_");
			}else if(newPolicyCheck.contains("Decision_")){
				newPolicyCheck = newPolicyCheck.replace(".Decision_", ":Decision_");
			}
			String[] newPolicySplit = newPolicyCheck.split(":");

			String orignalPolicyName = oldPath.replace("/", ".");
			String oldPolicyCheck = orignalPolicyName;
			if(oldPolicyCheck.contains("Config_")){
				oldPolicyCheck = oldPolicyCheck.replace(".Config_", ":Config_");
			}else if(oldPolicyCheck.contains("Action_")){
				oldPolicyCheck = oldPolicyCheck.replace(".Action_", ":Action_");
			}else if(oldPolicyCheck.contains("Decision_")){
				oldPolicyCheck = oldPolicyCheck.replace(".Decision_", ":Decision_");
			}
			String[] oldPolicySplit = oldPolicyCheck.split(":");
			
			//Check PolicyEntity table with newPolicy Name
			String policyEntityquery = "FROM PolicyEntity where policyName = '"+newPolicySplit[1]+"' and scope ='"+newPolicySplit[0]+"'";
			System.out.println(policyEntityquery);
			List<Object> queryData = controller.getDataByQuery(policyEntityquery);
			if(!queryData.isEmpty()){
				entity = (PolicyEntity) queryData.get(0);
			}
			
			if(entity != null){
				//if a policy exists with new name check if it is deleted or not
				if(entity.isDeleted()){
					//Check Policy Group Entity table if policy has been pushed or not
					String query = "from PolicyGroupEntity where policyid = '"+entity.getPolicyId()+"'";
					List<Object> object = controller.getDataByQuery(query);
					if(object.isEmpty()){
						//if PolicyGroupEntity data is empty delete the entry from database
						controller.deleteData(entity);
						//Query the Policy Entity with oldPolicy Name
						String oldpolicyEntityquery = "FROM PolicyEntity where policyName = '"+oldPolicySplit[1]+"' and scope ='"+oldPolicySplit[0]+"'";
						System.out.println(oldpolicyEntityquery);
						List<Object> oldEntityData = controller.getDataByQuery(oldpolicyEntityquery);
						if(!oldEntityData.isEmpty()){
							entity = (PolicyEntity) oldEntityData.get(0);
						}
						checkOldPolicyEntryAndUpdate(entity, newPolicySplit[0], newPolicySplit[1],  oldPolicySplit[0], oldPolicySplit[1], policyName, newpolicyName, oldpolicyName, userId);
					}else{
						return error("Policy rename failed due to policy with new name existing in PDP Group.");
					}
				}else{
					return error("Policy rename failed due to same name existing.");
				}
			}else{
				//Query the Policy Entity with oldPolicy Name
				String oldpolicyEntityquery = "FROM PolicyEntity where policyName = '"+oldPolicySplit[1]+"' and scope ='"+oldPolicySplit[0]+"'";
				System.out.println(oldpolicyEntityquery);
				List<Object> oldEntityData = controller.getDataByQuery(oldpolicyEntityquery);
				if(!oldEntityData.isEmpty()){
					entity = (PolicyEntity) oldEntityData.get(0);
				}
				checkOldPolicyEntryAndUpdate(entity, newPolicySplit[0] , newPolicySplit[1], oldPolicySplit[0], oldPolicySplit[1], policyName, newpolicyName, oldpolicyName, userId);
			}

			return success();
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured While Renaming Policy"+e);
			return error(e.getMessage());
		}
	}

	private JSONObject checkOldPolicyEntryAndUpdate(PolicyEntity entity, String newScope, String removenewPolicyExtension, String oldScope, String removeoldPolicyExtension, 
			String policyName, String  newpolicyName, String oldpolicyName, String userId) throws ServletException{
		try {
			ConfigurationDataEntity configEntity;
			ActionBodyEntity actionEntity;
			PolicyController controller = new PolicyController();
			configEntity = entity.getConfigurationData();
			actionEntity = entity.getActionBodyEntity();
			if(entity != null){
				//Check Policy Group Entity table if policy has been pushed or not
				String query = "from PolicyGroupEntity where policyid = '"+entity.getPolicyId()+"'";
				List<Object> object = controller.getDataByQuery(query);
				if(object == null){
					String oldPolicyNameWithoutExtension = removeoldPolicyExtension;
					String newPolicyNameWithoutExtension = removenewPolicyExtension;
					if(removeoldPolicyExtension.endsWith(".xml")){
						oldPolicyNameWithoutExtension = oldPolicyNameWithoutExtension.substring(0, oldPolicyNameWithoutExtension.indexOf("."));
						newPolicyNameWithoutExtension = newPolicyNameWithoutExtension.substring(0, newPolicyNameWithoutExtension.indexOf("."));
					}
					entity.setPolicyName(entity.getPolicyName().replace(removeoldPolicyExtension, removenewPolicyExtension));
					entity.setPolicyData(entity.getPolicyData().replace(oldScope +"."+oldPolicyNameWithoutExtension, newScope+"."+newPolicyNameWithoutExtension));
					entity.setScope(newScope);
					entity.setModifiedBy(userId);
					String oldConfigRemoveExtension = removeoldPolicyExtension.replace(".xml", "");
					String newConfigRemoveExtension = removenewPolicyExtension.replace(".xml", "");
					if(newpolicyName.contains("Config_")){
						configEntity.setConfigurationName(configEntity.getConfigurationName().replace(oldScope +"."+oldConfigRemoveExtension, newScope+"."+newConfigRemoveExtension));
						controller.updateData(configEntity);
					}else if(newpolicyName.contains("Action_")){
						actionEntity.setActionBody(actionEntity.getActionBody().replace(oldScope +"."+oldConfigRemoveExtension, newScope+"."+newConfigRemoveExtension));
						controller.updateData(actionEntity);
					}
					controller.updateData(entity);
				}else{
					//Mark as Deleted in PolicyEntiy table
					entity.setDeleted(true);
					controller.updateData(entity);
					//Mark as Deleted in ConfigurationDataEntity table
					configEntity.setDeleted(true);
					controller.updateData(configEntity);
					//Mark as Deleted in ActionDataEntity table
					actionEntity.setDeleted(true);
					controller.updateData(actionEntity);
					//Clone New Copy
					cloneRecord(newpolicyName, oldScope, removeoldPolicyExtension, newScope, removenewPolicyExtension, entity, userId);
				}

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
			}
			return success();
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

	private JSONObject cloneRecord(String newpolicyName, String oldScope, String removeoldPolicyExtension, String newScope, String removenewPolicyExtension, PolicyEntity entity, String userId) throws ServletException{
		String queryEntityName = null;
		PolicyController controller = new PolicyController();
		PolicyEntity cloneEntity = new PolicyEntity();
		cloneEntity.setPolicyName(newpolicyName);
		removeoldPolicyExtension = removeoldPolicyExtension.replace(".xml", "");
		removenewPolicyExtension = removenewPolicyExtension.replace(".xml", "");
		cloneEntity.setPolicyData(entity.getPolicyData().replace(oldScope+"."+removeoldPolicyExtension, newScope+"."+removenewPolicyExtension));
		cloneEntity.setScope(entity.getScope());
		String oldConfigRemoveExtension = removeoldPolicyExtension.replace(".xml", "");
		String newConfigRemoveExtension = removenewPolicyExtension.replace(".xml", "");
		if(newpolicyName.contains("Config_")){
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
		}else if(newpolicyName.contains("Action_")){
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
		}
		cloneEntity.setDeleted(entity.isDeleted());
		cloneEntity.setCreatedBy(userId);
		cloneEntity.setModifiedBy(userId);
		controller.saveData(cloneEntity);

		return success();
	}

	//Clone the Policy
	private JSONObject copy(JSONObject params, HttpServletRequest request) throws ServletException {
		try {
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			String oldPath = params.getString("path");
			String newPath = params.getString("newPath");
			oldPath = oldPath.substring(oldPath.indexOf("/")+1);
			newPath = newPath.substring(newPath.indexOf("/")+1);

			String policyVersionName = newPath.replace(".xml", "");
			String version = policyVersionName.substring(policyVersionName.indexOf(".")+1);
			String policyName = policyVersionName.substring(0, policyVersionName.lastIndexOf(".")).replace("/", File.separator);

			String newpolicyName = newPath.replace("/", ".");

			String orignalPolicyName = oldPath.replace("/", ".");

			String newPolicyCheck = newpolicyName;
			if(newPolicyCheck.contains("Config_")){
				newPolicyCheck = newPolicyCheck.replace(".Config_", ":Config_");
			}else if(newPolicyCheck.contains("Action_")){
				newPolicyCheck = newPolicyCheck.replace(".Action_", ":Action_");
			}else if(newPolicyCheck.contains("Decision_")){
				newPolicyCheck = newPolicyCheck.replace(".Decision_", ":Decision_");
			}
			String[] newPolicySplit = newPolicyCheck.split(":");

			String oldPolicyCheck = orignalPolicyName;
			if(oldPolicyCheck.contains("Config_")){
				oldPolicyCheck = oldPolicyCheck.replace(".Config_", ":Config_");
			}else if(oldPolicyCheck.contains("Action_")){
				oldPolicyCheck = oldPolicyCheck.replace(".Action_", ":Action_");
			}else if(oldPolicyCheck.contains("Decision_")){
				oldPolicyCheck = oldPolicyCheck.replace(".Decision_", ":Decision_");
			}
			String[] oldPolicySplit = oldPolicyCheck.split(":");

			PolicyController controller = new PolicyController();

			PolicyEntity entity = null;
			boolean success = false;

			//Check PolicyEntity table with newPolicy Name
			String policyEntityquery = "FROM PolicyEntity where policyName = '"+newPolicySplit[1]+"' and scope ='"+newPolicySplit[0]+"'";
			System.out.println(policyEntityquery);
			List<Object> queryData = controller.getDataByQuery(policyEntityquery);
			if(!queryData.isEmpty()){
				entity = (PolicyEntity) queryData.get(0);
			}
			if(entity != null){
				//if a policy exists with new name check if it is deleted or not
				if(entity.isDeleted()){
					//Check Policy Group Entity table if policy has been pushed or not
					String query = "from PolicyGroupEntity where policyid = '"+entity.getPolicyId()+"'";
					List<Object> object = controller.getDataByQuery(query);
					if(object == null){
						//if PolicyGroupEntity data is empty delete the entry from database
						controller.deleteData(entity);
						//Query the Policy Entity with oldPolicy Name
						policyEntityquery = "FROM PolicyEntity where policyName = '"+oldPolicySplit[1]+"' and scope ='"+oldPolicySplit[0]+"'";
						System.out.println(policyEntityquery);
						queryData = controller.getDataByQuery(policyEntityquery);
						if(!queryData.isEmpty()){
							entity = (PolicyEntity) queryData.get(0);
						}
						if(entity != null){
							cloneRecord(newPolicySplit[1], oldPolicySplit[0], oldPolicySplit[1], newPolicySplit[0], newPolicySplit[1], entity, userId);
							success = true;
						}
					}else{
						return error("Policy Clone failed due to policy with new name existing in PDP Group.");
					}
				}else{
					return error("Policy Clone failed due to same name existing.");
				}
			}else{
				//Query the Policy Entity with oldPolicy Name
				policyEntityquery = "FROM PolicyEntity where policyName = '"+oldPolicySplit[1]+"' and scope ='"+oldPolicySplit[0]+"'";
				System.out.println(policyEntityquery);
				queryData = controller.getDataByQuery(policyEntityquery);
				if(!queryData.isEmpty()){
					entity = (PolicyEntity) queryData.get(0);
				}
				if(entity != null){
					cloneRecord(newPolicySplit[1], oldPolicySplit[0], oldPolicySplit[1],  newPolicySplit[0], newPolicySplit[1], entity, userId);
					success = true;
				}
			}
			if(success){
				PolicyVersion entityItem = new PolicyVersion();
				entityItem.setActiveVersion(Integer.parseInt(version));
				entityItem.setHigherVersion(Integer.parseInt(version));
				entityItem.setPolicyName(policyName);
				entityItem.setCreatedBy(userId);
				entityItem.setModifiedBy(userId);
				controller.saveData(entityItem);
			}

			LOGGER.debug("copy from: {} to: {}" + oldPath +newPath);

			return success();
		} catch (Exception e) {
			LOGGER.error("copy", e);
			return error(e.getMessage());
		}
	}

	//Delete Policy or Scope Functionality
	private JSONObject delete(JSONObject params, HttpServletRequest request) throws ServletException {
		PolicyController controller = new PolicyController();
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
			path = path.substring(path.indexOf("/")+1);
			String policyNamewithExtension = path.replace("/", File.separator);
			String policyVersionName = policyNamewithExtension.replace(".xml", "");
			String query = "";
			if(path.endsWith(".xml")){
				policyNamewithoutExtension = policyVersionName.substring(0, policyVersionName.lastIndexOf("."));
				policyNamewithoutExtension = policyNamewithoutExtension.replace(File.separator, ".");
				String splitPolicyName = null;
				if(policyNamewithoutExtension.contains("Config_")){
					splitPolicyName = policyNamewithoutExtension.replace(".Config_", ":Config_");
				}else if(policyNamewithoutExtension.contains("Action_")){
					splitPolicyName = policyNamewithoutExtension.replace(".Action_", ":Action_");
				}else if(policyNamewithoutExtension.contains("Decision_")){
					splitPolicyName = policyNamewithoutExtension.replace(".Decision_", ":Decision_");
				}
				String[] split = splitPolicyName.split(":");
				query = "FROM PolicyEntity where policyName like '"+split[1]+"%' and scope ='"+split[0]+"'";
			}else{
				policyNamewithoutExtension = path.replace(File.separator, ".");
				query = "FROM PolicyEntity where scope like '"+policyNamewithoutExtension+"%'";
			}
			
			List<Object> policyEntityobjects = controller.getDataByQuery(query);
			boolean pdpCheck = true;
			if(path.endsWith(".xml")){
				policyNamewithoutExtension = policyNamewithoutExtension.replace(".", File.separator);
				int version = Integer.parseInt(policyVersionName.substring(policyVersionName.indexOf(".")+1));
				if("ALL".equals(deleteVersion)){
					if(!policyEntityobjects.isEmpty()){
						for(Object object : policyEntityobjects){
							policyEntity = (PolicyEntity) object;
							String groupEntityquery = "from PolicyGroupEntity where policyid = '"+policyEntity.getPolicyId()+"'";
							List<Object> groupobject = controller.getDataByQuery(groupEntityquery);
							if(groupobject != null){
								pdpCheck = false;
								break;
							}
						}
					}
					if(pdpCheck){
						for(Object object : policyEntityobjects){
							policyEntity = (PolicyEntity) object;
							//Delete the entity from Elastic Search Database
							String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
							restController.deleteElasticData(searchFileName);
							//Delete the entity from Policy Entity table
							controller.deleteData(policyEntity);
							if(policyNamewithoutExtension.contains("Config_")){
								controller.deleteData(policyEntity.getConfigurationData());
							}else if(policyNamewithoutExtension.contains("Action_")){
								controller.deleteData(policyEntity.getActionBodyEntity());
							}
						}
						//Policy Notification
						PolicyVersion versionEntity = new PolicyVersion();
						versionEntity.setPolicyName(policyNamewithoutExtension);
						versionEntity.setModifiedBy(userId);
						controller.watchPolicyFunction(versionEntity, policyNamewithExtension, "DeleteAll");
						//Delete from policyVersion table
						String policyVersionQuery = "delete from PolicyVersion  where policy_name ='" +policyNamewithoutExtension.replace("\\", "\\\\")+"' and id >0";
						if(policyVersionQuery != null){
							controller.executeQuery(policyVersionQuery);
						}
					}else{
						return error("Policy can't be deleted, it is active in PDP Groups.     PolicyName: '"+policyEntity.getScope() + "." +policyEntity.getPolicyName()+"'");
					}
				}else if("CURRENT".equals(deleteVersion)){
					String currentVersionPolicyName = policyNamewithExtension.substring(policyNamewithExtension.lastIndexOf(File.separator)+1);
					String currentVersionScope = policyNamewithExtension.substring(0, policyNamewithExtension.lastIndexOf(File.separator)).replace(File.separator, ".");
					query = "FROM PolicyEntity where policyName = '"+currentVersionPolicyName+"' and scope ='"+currentVersionScope+"'";
					List<Object> policyEntitys = controller.getDataByQuery(query);
					if(!policyEntitys.isEmpty()){
						policyEntity = (PolicyEntity) policyEntitys.get(0);
					}
					if(policyEntity != null){
						String groupEntityquery = "from PolicyGroupEntity where policyid = '"+policyEntity.getPolicyId()+"'";
						List<Object> groupobject = controller.getDataByQuery(groupEntityquery);
						if(groupobject == null){
							//Delete the entity from Elastic Search Database
							String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
							restController.deleteElasticData(searchFileName);
							//Delete the entity from Policy Entity table
							controller.deleteData(policyEntity);
							if(policyNamewithoutExtension.contains("Config_")){
								controller.deleteData(policyEntity.getConfigurationData());
							}else if(policyNamewithoutExtension.contains("Action_")){
								controller.deleteData(policyEntity.getActionBodyEntity());
							}
							
							if(version > 1){
								int highestVersion = 0; 
								if(policyEntityobjects.isEmpty()){
									for(Object object : policyEntityobjects){
										policyEntity = (PolicyEntity) object;
										String policyEntityName = policyEntity.getPolicyName().replace(".xml", "");
										int policyEntityVersion = Integer.parseInt(policyEntityName.substring(policyEntityName.lastIndexOf(".")+1));
										if(policyEntityVersion > highestVersion){
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

								String updatequery = "update PolicyVersion set active_version='"+highestVersion+"' , highest_version='"+highestVersion+"' where policy_name ='" +policyNamewithoutExtension.replace("\\", "\\\\")+"'";
								controller.executeQuery(updatequery);
							}else{
								String policyVersionQuery = "delete from PolicyVersion  where policy_name ='" +policyNamewithoutExtension.replace("\\", "\\\\")+"' and id >0";
								if(policyVersionQuery != null){
									controller.executeQuery(policyVersionQuery);
								}
							}
						}else{
							return error("Policy can't be deleted, it is active in PDP Groups.     PolicyName: '"+policyEntity.getScope() + "." +policyEntity.getPolicyName()+"'");
						}
					}
				}	
			}else{
				if(!policyEntityobjects.isEmpty()){
					for(Object object : policyEntityobjects){
						policyEntity = (PolicyEntity) object;
						String groupEntityquery = "from PolicyGroupEntity where policyid = '"+policyEntity.getPolicyId()+"'";
						List<Object> groupobject = controller.getDataByQuery(groupEntityquery);
						if(groupobject != null){
							pdpCheck = false;
						}
					}
					if(pdpCheck){
						for(Object object : policyEntityobjects){
							policyEntity = (PolicyEntity) object;
							//Delete the entity from Elastic Search Database
							String searchFileName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
							restController.deleteElasticData(searchFileName);
							//Delete the entity from Policy Entity table
							controller.deleteData(policyEntity);
							policyNamewithoutExtension = policyEntity.getPolicyName();
							if(policyNamewithoutExtension.contains("Config_")){
								controller.deleteData(policyEntity.getConfigurationData());
							}else if(policyNamewithoutExtension.contains("Action_")){
								controller.deleteData(policyEntity.getActionBodyEntity());
							}
						}
						
						//Delete from policyVersion and policyEditor Scope table
						String policyVersionQuery = "delete PolicyVersion where POLICY_NAME like '"+path.replace("\\", "\\\\")+"%' and id >0";
						String policyScopeQuery = "delete PolicyEditorScopes where SCOPENAME like '"+path.replace("\\", "\\\\")+"%' and id >0";
						controller.executeQuery(policyVersionQuery);
						controller.executeQuery(policyScopeQuery);
						//Policy Notification
						PolicyVersion entity = new PolicyVersion();
						entity.setPolicyName(path);
						entity.setModifiedBy(userId);
						controller.watchPolicyFunction(entity, path, "DeleteScope");
					}
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
			PolicyController controller = new PolicyController();
			String mode = params.getString("mode");
			String path = params.getString("path");
			LOGGER.debug("editFile path: {}"+ path);
			
			String domain = path.substring(1, path.lastIndexOf("/"));
			domain = domain.replace("/", ".");
			
			path = path.substring(1);
			path = path.replace("/", ".");
			String dbCheckName = path;
			if(dbCheckName.contains("Config_")){
				dbCheckName = dbCheckName.replace(".Config_", ":Config_");
			}else if(dbCheckName.contains("Action_")){
				dbCheckName = dbCheckName.replace(".Action_", ":Action_");
			}else if(dbCheckName.contains("Decision_")){
				dbCheckName = dbCheckName.replace(".Decision_", ":Decision_");
			}
			
			String[] split = dbCheckName.split(":");
			String query = "FROM PolicyEntity where policyName = '"+split[1]+"' and scope ='"+split[0]+"'";
			List<Object> queryData = controller.getDataByQuery(query);
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
			policyAdapter.setDomain(domain);
			policyAdapter.setDomainDir(domain);
			policyAdapter.setPolicyData(policy);
			String policyName = path.replace(".xml", "");
			policyName = policyName.substring(0, policyName.lastIndexOf("."));
			policyAdapter.setPolicyName(policyName.substring(policyName.lastIndexOf(".")+1));

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
		PolicyController controller = new PolicyController();
		String name = "";
		try {
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			String path = params.getString("path");
			try{
				if(params.has("subScopename")){
					if(!params.getString("subScopename").equals("")){
						name = params.getString("path").replace("/", File.separator) + File.separator +params.getString("subScopename");
					}
				}else{
					name = params.getString("name");
				}	
			}catch(Exception e){
				name = params.getString("name");
				LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Adding Scope"+e);
			}
			String validateName;
			if(name.contains(File.separator)){
				validateName = name.substring(name.lastIndexOf(File.separator)+1);
			}else{
				validateName = name;
			}
			if(!name.isEmpty()){
				String validate = PolicyUtils.emptyPolicyValidator(validateName);
				if(!validate.contains("success")){
					return error(validate);
				} 
			}
			LOGGER.debug("addFolder path: {} name: {}" + path +name);
			if(!name.equals("")){
				PolicyEditorScopes entity = (PolicyEditorScopes) controller.getEntityItem(PolicyEditorScopes.class, "scopeName", name);
				if(entity == null){
					UserInfo userInfo = new UserInfo();
					userInfo.setUserLoginId(userId);
					PolicyEditorScopes newScope = new PolicyEditorScopes();
					String scopeName = null;
					if(name.startsWith(File.separator)){
						scopeName = name.substring(1);
					}else{
						scopeName = name;
					} 
					newScope.setScopeName(scopeName);
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
}
