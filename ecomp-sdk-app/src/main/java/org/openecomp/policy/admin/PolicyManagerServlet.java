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


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.openecomp.policy.adapter.PolicyAdapter;
import org.openecomp.policy.components.HumanPolicyComponent;
import org.openecomp.policy.controller.ActionPolicyController;
import org.openecomp.policy.controller.CreateBRMSParamController;
import org.openecomp.policy.controller.CreateBRMSRawController;
import org.openecomp.policy.controller.CreateClosedLoopFaultController;
import org.openecomp.policy.controller.CreateClosedLoopPMController;
import org.openecomp.policy.controller.CreateDcaeMicroServiceController;
import org.openecomp.policy.controller.CreateFirewallController;
import org.openecomp.policy.controller.CreatePolicyController;
import org.openecomp.policy.controller.DecisionPolicyController;
import org.openecomp.policy.controller.PolicyController;
import org.openecomp.policy.controller.PolicyExportAndImportController;
import org.openecomp.policy.elk.client.ElkConnector;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.utils.XACMLPolicyWriterWithPapNotify;
import org.openecomp.portalsdk.core.web.support.UserUtils;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PolicyManagerServlet extends HttpServlet {
	private static final Logger LOG	= FlexLogger.getLogger(PolicyManagerServlet.class);
	private static final long serialVersionUID = -8453502699403909016L;

	private enum Mode {
		LIST, RENAME, COPY, DELETE, EDITFILE, ADDFOLDER, DESCRIBEPOLICYFILE, VIEWPOLICY, ADDSUBSCOPE, SWITCHVERSION, EXPORT
	}

	public  static final String REPOSITORY_BASE_PATH = PolicyController.getGitPath().toString();
	private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String CONFIG_HOME = PolicyController.getConfigHome();
	public static final String ACTION_HOME = PolicyController.getActionHome();
	private static String CONTENTTYPE = "application/json";
	private File repofilePath;
	private static String SUPERADMIN = "super-admin";
	private static String SUPEREDITOR = "super-editor";
	private static String SUPERGUEST = "super-guest";
	private static String ADMIN = "admin";
	private static String EDITOR = "editor";
	private static String GUEST = "guest";
	private static String RESULT = "result";
	private static String REPOSITORY = "repository";
	
	private static String CONFIG = "Config_";
	private static String ACTION = "Action_";
	private static String DECISION = "Decision_";
	
	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getParameter("path");
		File file = new File(REPOSITORY_BASE_PATH, path);

		if (!file.isFile()) {
			// if not a file, it is a folder, show this error.  
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
			return;
		}

		response.setHeader("Content-Type", getServletContext().getMimeType(file.getName()));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

		FileInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new FileInputStream(file);
			output = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[8192];
			for (int length = 0; (length = input.read(buffer)) > 0;) {
				output.write(buffer, 0, length);
			}
		} catch (Exception e) {
			LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Reading Imput Stream" + e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Closing Output Stream" + e);
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Closing Input Stream" + e);
				}
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.debug("doPost");
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
					if(item.getName().endsWith(".tar")){
						try{
							File file = new File(item.getName());
							OutputStream outputStream = new FileOutputStream(file);
							IOUtils.copy(item.getInputStream(), outputStream);
							outputStream.close();
							newFile = file.toString();
							PolicyExportAndImportController importController = new PolicyExportAndImportController();
							importController.ImportRepositoryFile(newFile, request);
						}catch(Exception e){
							LOG.error("Upload error : " + e);
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
			LOG.debug("Cannot write file");
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
			LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While doing File Operation" + e);
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
			userId = UserUtils.getUserIdFromCookie(request);
		} catch (Exception e) {
			LOG.error("Exception Occured while reading userid from cookie" +e);
		}
		if(params.toString().contains("activeVersion")){
			String activeVersion = params.getString("activeVersion");
			String highestVersion = params.getString("highestVersion");
			if(Integer.parseInt(activeVersion) > Integer.parseInt(highestVersion)){
				return error("The Version shouldn't be greater than Highest Value");
			}else{
				String removeExtension = path.replace(".xml", "");
				String policyName = removeExtension.substring(0, removeExtension.lastIndexOf("."));
				String activePolicy = policyName + "." + activeVersion + ".xml";
				File file = new File(Paths.get(REPOSITORY_BASE_PATH, activePolicy).toString());
				if(!file.exists()){
					return error("The Policy is Not Existing in Workspace");
				}else{
					if(policyName.contains("/")){
						policyName = policyName.replace("/", File.separator);
					}
					policyName = policyName.substring(policyName.indexOf(File.separator)+1);
					if(policyName.contains("\\")){
						policyName = policyName.replace(File.separator, "\\");
					}
					String query = "update PolicyVersion set active_version='"+activeVersion+"' where policy_name ='" +policyName+"'  and id >0";
					//query the database
					PolicyController.updatePolicyVersion(query);
					//Policy Notification
					PolicyController controller = new PolicyController();
					PolicyVersion entity = new PolicyVersion();
					entity.setPolicyName(policyName);
					entity.setActiveVersion(Integer.parseInt(activeVersion));
					entity.setModifiedBy(userId);
					controller.WatchPolicyFunction(entity, policyName, "SwitchVersion");
				}
			}
		}
		File policyFile = new File(REPOSITORY_BASE_PATH, path);
		PolicyController policyController =  new PolicyController();
		return policyController.SwitchVersionPolicyContent(policyFile);
	}

	//Describe Policy
	private JSONObject describePolicy(JSONObject params){
		String path = params.getString("path");
		File policyFile = new File(REPOSITORY_BASE_PATH, path);
	
		return HumanPolicyComponent.DescribePolicy(policyFile);
	}

	//Get the List of Policies and Scopes for Showing in Editor tab
	private JSONObject list(JSONObject params, HttpServletRequest request) throws ServletException { 
		Set<String> scopes = null;
		List<String> roles = null;
		try {
			//Get the Login Id of the User from Request
			String userId =  UserUtils.getUserIdFromCookie(request);
			//Check if the Role and Scope Size are Null get the values from db. 
			List<Roles> userRoles = PolicyController.getRoles(userId);
			roles = new ArrayList<String>();
			scopes = new HashSet<String>();
			for(Roles userRole: userRoles){
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
			SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
			boolean onlyFolders = params.getBoolean("onlyFolders");
			String path = params.getString("path");
			if(path.contains("..xml")){
				path = path.replaceAll("..xml", "").trim();
			}


			if("/".equals(path)){
				if(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)){
					try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(REPOSITORY_BASE_PATH, path))) {
						for (Path pathObj : directoryStream) {
							BasicFileAttributes attrs = Files.readAttributes(pathObj, BasicFileAttributes.class);
							if (onlyFolders && !attrs.isDirectory()) {
								continue;
							}
							JSONObject el = new JSONObject();
							String fileName = pathObj.getFileName().toString();
							if (!(fileName.equals(".DS_Store") || fileName.contains(".git"))) {
								if(!fileName.endsWith(".xml")){
									el.put("name", fileName);	
									el.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
									el.put("size", attrs.size());
									el.put("type", attrs.isDirectory() ? "dir" : "file");
									resultList.add(el);
								}
							}
						}
					} catch (IOException ex) {
						LOG.error("Error Occured While reading Policy Files List"+ex );
					}			
				}else if(roles.contains(ADMIN) || roles.contains(EDITOR) || roles.contains(GUEST)){
					for(Object scope : scopes){
						JSONObject el = new JSONObject();
						Path filePath = Paths.get(REPOSITORY_BASE_PATH + File.separator + scope);
						if(Files.exists(filePath)){
							el.put("name", scope);	
							el.put("date", dt.format(filePath.toFile().lastModified()));
							el.put("size", "");
							el.put("type", "dir");
							resultList.add(el);
						}
					}
				}
			}else{
				try{
					String scopeName = path.substring(path.indexOf("/") +1);
					activePolicyList(scopeName, resultList, roles, scopes, onlyFolders);
				} catch (Exception ex) {
		        	LOG.error("Error Occured While reading Policy Files List"+ex );
		        }			
			}

			return new JSONObject().put(RESULT, resultList);
		} catch (Exception e) {
			LOG.error("list", e);
			return error(e.getMessage());
		}
	}
	
	//Get Active Policy List based on Scope Selection form Policy Version table
	private void activePolicyList(String scopeName, List<JSONObject> resultList, List<String> roles, Set<String> scopes, boolean onlyFolders){
		if(scopeName.contains("/")){
			scopeName = scopeName.replace("/", File.separator);
		}
		if(scopeName.contains("\\")){
			scopeName = scopeName.replace("\\", "\\\\\\\\");
		}
		String query = "from PolicyVersion where POLICY_NAME like'" +scopeName+"%'";
		String scopeNamequery = "from PolicyEditorScopes where SCOPENAME like'" +scopeName+"%'";
		List<PolicyVersion> activePolicies = PolicyController.getListOfActivePolicies(query);
		List<PolicyEditorScopes> scopesList = PolicyController.getListOfPolicyEditorScopes(scopeNamequery);
		for(PolicyEditorScopes scopeById : scopesList){
			String scope = scopeById.getScopeName();
			if(scope.contains(File.separator)){
				String checkScope = scope.substring(0, scope.lastIndexOf(File.separator));
				if(scopeName.contains("\\\\")){
					scopeName = scopeName.replace("\\\\", File.separator);
				}
				if(scopeName.equalsIgnoreCase(checkScope)){
					JSONObject el = new JSONObject();
					Path filePath = Paths.get(REPOSITORY_BASE_PATH + File.separator + scope);
					if(Files.exists(filePath)){
						el.put("name", filePath.getFileName());	
						el.put("date", scopeById.getModifiedDate());
						el.put("size", "");
						el.put("type", "dir");
						el.put("createdBy", scopeById.getUserCreatedBy().getUserName());
						el.put("modifiedBy", scopeById.getUserModifiedBy().getUserName());
						resultList.add(el);
					}
				}
			}		
		}
		for (PolicyVersion policy : activePolicies) {
			String scopeNameValue = policy.getPolicyName().substring(0, policy.getPolicyName().lastIndexOf(File.separator));
			String activepath = REPOSITORY_BASE_PATH + File.separator + policy.getPolicyName() + "." + policy.getActiveVersion() + ".xml";
			Path pathObj = Paths.get(activepath);
			if(Files.exists(pathObj)){
				BasicFileAttributes attrs;
				try {
					attrs = Files.readAttributes(pathObj, BasicFileAttributes.class);
					if (onlyFolders && !attrs.isDirectory()) {
						continue;
					}
					if(roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST)){	
						readPolicies(pathObj, attrs, scopeName, resultList);
					}else if(!scopes.isEmpty()){
						for(String value : scopes){
							if(scopeNameValue.startsWith(value)){
								readPolicies(pathObj, attrs, scopeName, resultList);
							}
						}
					}
				} catch (Exception e) {
					LOG.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Exception occured while reading File Attributes"+e);
				}
			}
		}	
	}
	
	//Read the Policy File to get Created by and Modified by User Name of Policy 
	public void readPolicies(Path pathObj, BasicFileAttributes attrs, String scopeName, List<JSONObject> resultList){
		JSONObject el = new JSONObject();
		String policyName = "";
		String version = "";
		String scope = "";
		if(scopeName.contains("\\\\")){
			scopeName = scopeName.replace("\\\\", File.separator);
		}
		SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
		String fileName = pathObj.getFileName().toString();
		if (!(fileName.equals(".DS_Store") || fileName.startsWith(".git"))) {
			if(fileName.endsWith(".xml")){
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				//Query the database
				String parent = pathObj.toString().substring(pathObj.toString().indexOf(REPOSITORY)+ 11);
				parent = FilenameUtils.removeExtension(parent);
				version = parent.substring(parent.indexOf(".")+1);
				policyName = parent.substring(0, parent.lastIndexOf("."));
				scope = policyName.substring(0, policyName.lastIndexOf(File.separator));
				if(policyName.contains("\\")){
					policyName = scope + "\\" + policyName.substring(policyName.lastIndexOf("\\"));
				}		
			}
			if(scopeName.equalsIgnoreCase(scope)){
				el.put("name", fileName);
				if(pathObj.toFile().toString().endsWith(".xml")){
					el.put("version", version);
					List<String> createdByModifiedBy;
					try {
						createdByModifiedBy = XACMLPolicyScanner.getCreatedByModifiedBy(pathObj);
					} catch (IOException e) {
						LOG.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while Reading the Policy File" + pathObj.toString() + e.getMessage());
						createdByModifiedBy = Arrays.asList("", "");
					}
					el.put("createdBy", getUserName(createdByModifiedBy.get(0)));
					el.put("modifiedBy", getUserName(createdByModifiedBy.get(1)));
				}		
				el.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
				el.put("size", attrs.size());
				el.put("type", attrs.isDirectory() ? "dir" : "file");
			}
		}

		if(!el.keySet().isEmpty()){
			resultList.add(el);
		}

	}
	
	//Get the User Name based on ID from User Info table
	public String getUserName(String userId) { 
		String userName = "super-admin";
		if("".equals(userId)){
			return userName;
		}
		try{
			return PolicyController.getUserName(userId);
		}catch(Exception e){
			LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Retriving User Name from User Info table"+e);
			return userName;
		}
	}

	//Rename Policy
	private JSONObject rename(JSONObject params, HttpServletRequest request) throws ServletException {
		try {
			String userId = null;
			try {
				userId = UserUtils.getUserIdFromCookie(request);
			} catch (Exception e) {
				LOG.error("Exception Occured while reading userid from cookie" +e);
			}
			String path = params.getString("path");
			String newpath = params.getString("newPath");
			LOG.debug("rename from: {} to: {}" +path + newpath);

			File srcFile = new File(REPOSITORY_BASE_PATH, path);
			File destFile = new File(REPOSITORY_BASE_PATH, newpath);
			if (srcFile.isFile()) {
				renameXMLandConfig(destFile.getPath().toString(), srcFile.getPath().toString(), userId);
			} else {
				FileUtils.moveDirectory(srcFile, destFile);
				String oldScopeName = path.substring(1).replace("/", File.separator);	
				String newScopeName = newpath.substring(1).replace("/", File.separator);
				String scopeNamequery = "from PolicyEditorScopes where SCOPENAME like'" +oldScopeName.replace("\\", "\\\\\\\\")+"%'";
				UserInfo userInfo = new UserInfo();
				userInfo.setUserLoginId(userId);
				List<PolicyEditorScopes> scopesList = PolicyController.getListOfPolicyEditorScopes(scopeNamequery);
				for(PolicyEditorScopes scopes : scopesList){
					String scope = scopes.getScopeName();
					String newScope = scope.replace(oldScopeName, newScopeName);
					scopes.setScopeName(newScope);
					scopes.setUserModifiedBy(userInfo);
					PolicyController.updatePolicyScopeEditor(scopes);
				}
				File[] list = destFile.listFiles();
				if(list.length > 0){
					renameXMLandConfig(destFile.getPath().toString(), srcFile.getPath().toString(), userId);
				}	
			}
			return success();
		} catch (Exception e) {
			LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured While Renaming Policy"+e);
			return error(e.getMessage());
		}
	}

	//rename the xml and config files when renaming scope
	public void renameXMLandConfig(String newPath, String oldPath, String loginId){
		if(!newPath.endsWith(".xml")){
			File dir = new File(newPath);
			File[] listOfFiles = dir.listFiles();
			for(File file : listOfFiles){
				if(file.toString().endsWith(".xml")){
					renameFile(file, oldPath, newPath );
				}else if(file.isDirectory()){
					String oldFilePath = oldPath + File.separator +file.getName();
					renameXMLandConfig(file.toString(), oldFilePath, loginId);
				}
			}
		}else{
			Path parent = Paths.get(oldPath.toString().substring(0, oldPath.toString().lastIndexOf(File.separator)));
			String policyName = oldPath.toString().substring(oldPath.toString().indexOf(REPOSITORY) +11);
			String removeExtension = policyName.replace(".xml", "");
			String dbPolicyName = removeExtension.substring(0, removeExtension.lastIndexOf("."));
			//Policy Notifcation
			PolicyController controller = new PolicyController();
			PolicyVersion entity = new PolicyVersion();
			entity.setPolicyName(dbPolicyName);
			entity.setModifiedBy(loginId);
			controller.WatchPolicyFunction(entity, dbPolicyName, "Rename");
			String filterPolicyName = dbPolicyName.substring(dbPolicyName.lastIndexOf(File.separator)+1);
			FileFilter fileFilter = new WildcardFileFilter(filterPolicyName + "." + "*" + ".xml");
			File[] files = parent.toFile().listFiles(fileFilter);
			for(File file : files){
				String removeNewPathExtension = newPath.replace(".xml", "");
				String removeNewFileVersion = removeNewPathExtension.substring(0, removeNewPathExtension.lastIndexOf("."));
				String oldFile = file.getPath();
				oldFile = oldFile.replace(".xml", "");
				String version = oldFile.substring(oldFile.lastIndexOf(".")+1);
				String finalPath = removeNewFileVersion + "." + version + ".xml";
				File destFile = new File(finalPath);
				try {
					FileUtils.moveFile(file, destFile);
					renameFile(file, oldFile, finalPath);	
				} catch (IOException e) {
					LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Renaming or Moving Policy"+e);
				}
				
			}		
		}
	}

	//Rename File
	private void renameFile(File file, String oldPath, String newPath){
		if(file.toString().contains(CONFIG) || file.toString().contains(ACTION) || file.toString().contains(DECISION)){
			File xmlFileName = new File(newPath);
			String oldfileWithExtension = null;
			String filelocation = null;
			String oldfile = null;
			String newfile = null;
			String extension = null;
			if(newPath.endsWith(".xml")){
				extension = XACMLPolicyWriterWithPapNotify.changeFileNameInXmlWhenRenamePolicy(xmlFileName.toPath());
			}else{
				extension = XACMLPolicyWriterWithPapNotify.changeFileNameInXmlWhenRenamePolicy(file.toPath());
				String fileName = file.getName();
				oldPath = oldPath + File.separator + fileName;
				newPath = newPath + File.separator + fileName;
			}
			
			try{
				if(file.toString().contains(CONFIG)){
					filelocation = PolicyController.getConfigHome();
				}
				if(file.toString().contains(ACTION)){
					filelocation = PolicyController.getActionHome();
				}
				File  oldFilePath = new File(oldPath);
				String oldFileName = oldFilePath.getName().replace(".xml", "");
				File  newFilePath = new File(newPath);
				String newFileName = newFilePath.getName().replace(".xml", "");
				File target = new File(oldPath);
				File newParentScope = new File(newPath);
				if(newParentScope.toString().endsWith(".xml")){
					String newScope = newParentScope.toString().substring(0, newParentScope.toString().lastIndexOf(File.separator));
					newParentScope = new File(newScope);
				}
				String oldParentScope = target.toString().substring(0, target.toString().lastIndexOf(File.separator));
				String oldDomain = oldParentScope.toString().substring(oldParentScope.toString().indexOf(REPOSITORY) + 11);
				if(oldDomain.endsWith(".xml")){
					oldDomain = oldDomain.substring(0, oldDomain.lastIndexOf(File.separator));
				}
				oldfile = oldDomain + File.separator + oldFileName.substring(0, oldFileName.indexOf("."));
				if(oldDomain.contains(File.separator)){
					oldDomain = oldDomain.replace(File.separator, ".");
				}
				String newDomain = newParentScope.toString().substring(newParentScope.toString().indexOf(REPOSITORY) + 11);
				newfile = newDomain + File.separator +newFileName.substring(0, newFileName.indexOf("."));
				if(newDomain.contains(File.separator)){
					newDomain = newDomain.replace(File.separator, ".");
				}
				if(file.toString().contains(CONFIG) || file.toString().contains(ACTION)){
					oldfileWithExtension = oldDomain + "." + oldFileName + "."+ extension;
					String newfilewithExtension = newDomain + "." + newFileName + "." + extension;
					File file1 = new File(filelocation, oldfileWithExtension);
					file1.renameTo(new File(filelocation , newfilewithExtension));
				}		
				String query = "update PolicyVersion set policy_name='"+newfile.replace("\\", "\\\\")+"' where policy_name ='" +oldfile.replace("\\", "\\\\")+"'  and id >0";
				//query the database
				PolicyController.updatePolicyVersion(query);
			}catch(Exception e){
				LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE +"Config file cannot found:" + oldfileWithExtension + e);
			}
		}
	}

	//Clone the Policy
	private JSONObject copy(JSONObject params, HttpServletRequest request) throws ServletException {
		try {
			String path = params.getString("path");
			String newpath = params.getString("newPath");
			LOG.debug("copy from: {} to: {}" + path +newpath);
			File srcFile = new File(REPOSITORY_BASE_PATH, path);
			File destFile = new File(REPOSITORY_BASE_PATH, newpath);
			if (srcFile.isFile()) {
				FileUtils.copyFile(srcFile, destFile);
				cloneXMLandConfig(destFile, srcFile, request);
			} else {
				FileUtils.copyDirectory(srcFile, destFile);
			}
			return success();
		} catch (Exception e) {
			LOG.error("copy", e);
			return error(e.getMessage());
		}
	}

	public void cloneXMLandConfig(File newPath, File oldPath, HttpServletRequest request){
		String userId = null;
		try {
			userId = UserUtils.getUserIdFromCookie(request);
		} catch (Exception e) {
			LOG.error("Exception Occured while reading userid from cookie" +e);
		}
		String newPolicyName = newPath.getPath().toString().substring(newPath.getPath().toString().indexOf(REPOSITORY) + 11);
		newPolicyName = newPolicyName.replace(".xml", "");
		String version = newPolicyName.substring(newPolicyName.lastIndexOf(".") +1);
		String policyName = newPolicyName.substring(0, newPolicyName.indexOf("."));
		newPolicyName = newPolicyName.replace(File.separator, ".");
		//if the user leaves the name of the policy blank
		if (newPolicyName == null) {
			return;
		}else{
			Path newPolicyPath = newPath.toPath();
			File dir = null;
			File[] listOfFiles = null;
			if(newPolicyName.contains(CONFIG)){
				LOG.debug("CONFIG_HOME: "+CONFIG_HOME);
				dir=new File(CONFIG_HOME); 
				listOfFiles = dir.listFiles();
			}else if(newPolicyName.contains(ACTION)){
				LOG.debug("ACTION_HOME: "+ACTION_HOME);
				dir=new File(ACTION_HOME); 
				listOfFiles = dir.listFiles();
			}
			String indexValue = "";
			String orignalPolicyName = oldPath.getPath().toString().substring(oldPath.getPath().toString().indexOf(REPOSITORY) + 11);
			orignalPolicyName = orignalPolicyName.replace(".xml", "");
			orignalPolicyName = orignalPolicyName.replace(File.separator, ".");
			if(orignalPolicyName.contains("Config_Fault_")){
				indexValue = "Config_Fault_";
			} else if(orignalPolicyName.contains("Config_PM_")){
				indexValue = "Config_PM_";
			}else if(orignalPolicyName.contains("Config_FW")){
				indexValue = "Config_FW_";
			}else if(orignalPolicyName.contains("Config_BRMS_Param")){
				indexValue = "Config_BRMS_Param_";
			}else if(orignalPolicyName.contains("Config_BRMS_Raw")){
				indexValue = "Config_BRMS_Raw_";
			} else if(orignalPolicyName.contains("Config_MS")){
				indexValue = "Config_MS_";
			}else if(orignalPolicyName.contains(ACTION)){
				indexValue = ACTION;
			}else if(orignalPolicyName.contains(DECISION)){
				indexValue = DECISION;
			}else{
				indexValue = CONFIG;
			}
			File newConfigFile = null;
			
			//making changes to the xml file
			if(indexValue.contains(CONFIG) || indexValue.contains(ACTION)){
				for (File file : listOfFiles) {
					if (file.isFile()){
						String fileName=file.getName();
						if(fileName.contains(orignalPolicyName)){
							String newConfigFileName=fileName.replaceAll(orignalPolicyName,newPolicyName);
							if(dir.toString().contains(File.separator)){
								newConfigFile=new File(dir.toString()+ File.separator +newConfigFileName);
							}
							try {
								Files.copy(file.toPath(), newConfigFile.toPath());
							} catch (Exception e) {
								LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE +"Error while Cloning the config file" + e);
								return;
							}
						}
					}
				}
				XACMLPolicyWriterWithPapNotify.changeFileNameInXmlWhenRenamePolicy(newPolicyPath);
			}
			//set the clone policy name into policy version database table
			PolicyVersion entityItem = new PolicyVersion();
			entityItem.setActiveVersion(Integer.parseInt(version));
			entityItem.setHigherVersion(Integer.parseInt(version));
			entityItem.setPolicyName(policyName);
			entityItem.setCreatedBy(userId);
			entityItem.setModifiedBy(userId);
			PolicyController.SaveToPolicyVersion(entityItem);


			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ElkConnector.singleton.update(newPolicyPath.toFile());							
						if (LOG.isInfoEnabled()) {
							LOG.info("ELK cloning to " +  newPolicyPath);
						}
					} catch (Exception e) {
						LOG.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": Internal Error: Unsucessful clone: " + e.getMessage(), e);
					}
				}
			}).start();

			//send to pap
			XACMLPolicyWriterWithPapNotify.notifyPapOfCreateUpdate(newPolicyPath.toAbsolutePath().toString());	
			LOG.info("Cloned policy "+newPolicyName+" created successfully.");						
			return;
		}
	}

	//Delete Policy or Scope Functionality
	private JSONObject delete(JSONObject params, HttpServletRequest request) throws ServletException {
		try {
			String userId = UserUtils.getUserIdFromCookie(request);
			String deleteVersion = "";
			String path1 = params.getString("path");
			LOG.debug("delete {}" +path1);
			if(params.has("deleteVersion")){
				deleteVersion  = params.getString("deleteVersion");
			}
		
			this.repofilePath  = new File(REPOSITORY_BASE_PATH, path1);
			File policyFile = new File(REPOSITORY_BASE_PATH, path1);
			if("ALL".equals(deleteVersion)){
				String removexmlExtension = policyFile.toString().substring(0, policyFile.toString().lastIndexOf("."));
				String removeVersion = removexmlExtension.substring(0, removexmlExtension.lastIndexOf("."));
				String notificationName = removeVersion.substring(removeVersion.lastIndexOf(REPOSITORY)+11);
				//Policy Notifcation
				PolicyController controller = new PolicyController();
				PolicyVersion entity = new PolicyVersion();
				entity.setPolicyName(notificationName);
				entity.setModifiedBy(userId);
				controller.WatchPolicyFunction(entity, notificationName, "DeleteAll");
				File dirXML = new File(policyFile.getParent());
				File[] listOfXMLFiles = dirXML.listFiles();
				for (File file : listOfXMLFiles) {
					//delete the xml files from Repository
					if (file.isFile() && file.toString().contains(removeVersion)) { 
						if(XACMLPolicyWriterWithPapNotify.notifyPapOfDelete(file.toString())){
							LOG.info("Policy deleted from database. Continuing with file delete");
						} else {
							LOG.error("Failed to delete Policy from database. Aborting file delete");
						}
						//Elk Update
						updateElkOnPolicyDelete(file);

						if (file.delete()) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("Deleted file: " + file.toString());
							}
						} else {
							LOG.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the policy file in specified location: " + file.getAbsolutePath());						
						}

						// Get tomcat home directory for deleting config data
						String path = getParentPathSubScopeDir();
						path = path.replace('\\', '.');
						if(path.contains("/")){
							path = path.replace('/', '.');
						}
						String fileName = FilenameUtils.removeExtension(file.getName());
						String removeVersionInFileName = fileName.substring(0, fileName.lastIndexOf("."));
						String fileLocation = null;
						if (fileName != null && fileName.contains(CONFIG)) {
							fileLocation = CONFIG_HOME;
						} else if (fileName != null && fileName.contains(ACTION)) {
							fileLocation = ACTION_HOME;
						}
						if (LOG.isDebugEnabled()) {
							LOG.debug("Attempting to rename file from the location: "+ fileLocation);
						}
						if(!file.toString().contains(DECISION)){
							// Get the file from the saved location
							File dir = new File(fileLocation);
							File[] listOfFiles = dir.listFiles();

							for (File file1 : listOfFiles) {
								if (file1.isFile() && file1.getName().contains( path + removeVersionInFileName)) {
									try {
										if (file1.delete() == false) {
											throw new Exception("No known error, Delete failed");
										}
									} catch (Exception e) {
										LOG.error("Failed to Delete file: "+ e.getLocalizedMessage());
									}
								}
							}
						}

						//Delete the Policy from Database Policy Version table
						String removeExtension = removeVersion.substring(removeVersion.indexOf(REPOSITORY)+11);
						String policyVersionQuery = "delete from PolicyVersion  where policy_name ='" +removeExtension.replace("\\", "\\\\")+"' and id >0";
						if(policyVersionQuery != null){
							PolicyController.updatePolicyVersion(policyVersionQuery);
						}
					}
				}
				//If Only Particular version to be deleted 
			}else if("CURRENT".equals(deleteVersion)){
				String removexmlExtension = policyFile.toString().substring(0, policyFile.toString().lastIndexOf("."));
				String getVersion = removexmlExtension.substring(removexmlExtension.indexOf(".")+1);
				String removeVersion = removexmlExtension.substring(0, removexmlExtension.lastIndexOf("."));
				String notificationName = removeVersion.substring(removeVersion.lastIndexOf(REPOSITORY)+11);
				//Policy Notifcation
				PolicyController controller = new PolicyController();
				PolicyVersion entity = new PolicyVersion();
				entity.setPolicyName(notificationName);
				entity.setActiveVersion(Integer.parseInt(getVersion));
				entity.setModifiedBy(userId);
				controller.WatchPolicyFunction(entity, notificationName, "DeleteOne");
				if(XACMLPolicyWriterWithPapNotify.notifyPapOfDelete(policyFile.toString())){
					LOG.info("Policy deleted from database. Continuing with file delete");
				} else {
					LOG.error("Failed to delete Policy from database. Aborting file delete");
				}
				//Elk Update
				updateElkOnPolicyDelete(policyFile);

				if (policyFile.delete()) {
					LOG.debug("Deleted file: " + policyFile.toString());
				} else {
					LOG.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Cannot delete the policy file in specified location: " +policyFile.getAbsolutePath());						
				}

				// Get tomcat home directory for storing action body config data
				String path = getParentPathSubScopeDir();
				path = path.replace('\\', '.');
				if(path.contains("/")){
					path = path.replace('/', '.');
					LOG.info("print the path:" +path);
				}
				final String tempPath = path;
				String fileName = FilenameUtils.removeExtension(policyFile.getName());
				String fileLocation = null;
				if (fileName != null && fileName.contains(CONFIG)) {
					fileLocation = CONFIG_HOME;
				} else if (fileName != null && fileName.contains(ACTION)) {
					fileLocation = ACTION_HOME;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Attempting to delete file from the location: "+ fileLocation);
				}
				if(!policyFile.toString().contains(DECISION)){
					// Get the file from the saved location
					File dir = new File(fileLocation);
					File[] listOfFiles = dir.listFiles();

					for (File file : listOfFiles) {
						if (file.isFile() && file.toString().contains( tempPath + fileName)) {
							try {
								if (file.delete() == false) {
									throw new Exception("No known error, Delete failed");
								}
							} catch (Exception e) {
								LOG.error("Failed to Delete file: "+ e.getLocalizedMessage());
							}
						}
					}
				}
				//Delete the Policy from Database and set Active Version based on the deleted file.
				int highestVersion = 0;
				String removeExtension = removeVersion.substring(removeVersion.indexOf(REPOSITORY)+11);
				PolicyVersion policyVersionEntity = PolicyController.getPolicyEntityFromPolicyVersion(removeExtension);
				if(policyVersionEntity != null){
					highestVersion = policyVersionEntity.getHigherVersion();
				}	
				int i =0;
				int version = Integer.parseInt(getVersion);
				if(version == highestVersion){
					for(i = highestVersion; i >= 1 ; i--){
						highestVersion = highestVersion-1;
						path = removeVersion + "."+ highestVersion  +".xml";
						File file = new File(path);
						if(file.exists()){
							break;
						}
					}
				}
				String updatequery = "update PolicyVersion set active_version='"+highestVersion+"' , highest_version='"+highestVersion+"' where policy_name ='" +removeExtension.replace("\\", "\\\\")+"'";
				PolicyController.updatePolicyVersion(updatequery);
			}else{
				String scopeName = policyFile.getAbsolutePath().substring(policyFile.getAbsolutePath().indexOf(REPOSITORY)+11);
				String policyVersionQuery = "delete PolicyVersion where POLICY_NAME like '"+scopeName.replace("\\", "\\\\")+"%' and id >0";
				String policyScopeQuery = "delete PolicyEditorScopes where SCOPENAME like '"+scopeName.replace("\\", "\\\\")+"%' and id >0";
				PolicyController.updatePolicyVersion(policyVersionQuery);
				PolicyController.updatePolicyScopeEditorWithQuery(policyScopeQuery);
				delete(policyFile);
				//Policy Notifcation
				PolicyController controller = new PolicyController();
				PolicyVersion entity = new PolicyVersion();
				entity.setPolicyName(scopeName);
				entity.setModifiedBy(userId);
				controller.WatchPolicyFunction(entity, scopeName, "DeleteScope");
			}
			return success();
		} catch (Exception e) {
			LOG.error("delete", e);
			return error(e.getMessage());
		}
	}
	
	//Notify ELK on File Delete
	private void updateElkOnPolicyDelete(File file){
		try {
			ElkConnector.singleton.delete(file);
		} catch (Exception e) {
			LOG.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": Cannot delete: " + file.getName() +
					" at " + file.getAbsolutePath() + ": " + e.getMessage(), e);
		}
	}
	//Deletes Files when Scope is Selected to delete
	public  void delete(File file) throws IOException{
		if(file.isDirectory()){
			//directory is empty, then delete it
			if(file.list().length==0){	
				file.delete();
			}else{
				//list all the directory contents
				String[] files = file.list();
				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);
					//delete from Elk first
					if(fileDelete.getAbsolutePath().toString().endsWith(".xml")){
						try {
							String deleteFile= fileDelete.getAbsoluteFile().toString().substring(fileDelete.getAbsoluteFile().toString().indexOf("workspace"));
							File deletePath= new File(deleteFile);
							LOG.debug("Search:"+deletePath);
							ElkConnector.singleton.delete(deletePath);
						} catch (Exception e) {
							LOG.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": Cannot delete: " + fileDelete.getAbsoluteFile().getName() +
									" at " + fileDelete.getAbsoluteFile().getAbsolutePath() + ": " +e.getMessage(), e);
						}
					}

					//recursive delete
					delete(fileDelete);

					//Delete the Configuration files from Config and Action Home Location
					String fileLocation = null;
					String policyName = fileDelete.toString().substring(fileDelete.toString().indexOf(REPOSITORY)+11, fileDelete.toString().lastIndexOf("."));
					if(policyName.contains(CONFIG)){
						fileLocation = PolicyController.getConfigHome();
					}
					if(policyName.contains(ACTION)){
						fileLocation = PolicyController.getActionHome();
					}
					if(policyName.contains(File.separator)){
						policyName = policyName.replace(File.separator, ".");
					}
					if(!fileDelete.toString().contains(DECISION) && fileLocation != null){
						// Get the file from the saved location and delete
						File dir = new File(fileLocation);
						FileFilter fileFilter = new WildcardFileFilter(policyName + ".*");
						File[] configFiles = (dir).listFiles(fileFilter);
						if(configFiles.length > 0){
							configFiles[0].delete();
						}
					}
					//Notify the PAP and Elk database for deleting the Policies Under Scopes
					if(fileDelete.getAbsolutePath().toString().endsWith(".xml")){
						if(!XACMLPolicyWriterWithPapNotify.notifyPapOfDelete(fileDelete.getAbsolutePath().toString())){
							LOG.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Could not delete the policy from the database: "+
									fileDelete.getAbsolutePath().toString());
							throw new IOException("Could not delete the policy from the database: "+
									fileDelete.getAbsolutePath().toString());
						}
					} 
				}    
				//check the directory again, if empty then delete it
				if(file.list().length==0){
					file.delete();
				}
			}	
		}else{
			//if file, then delete it
			file.delete();
		}
	}

	//Get the Parent Scope of File
	protected String getParentPathSubScopeDir() {
		String domain1 = null;
		final Path gitPath = PolicyController.getGitPath();
		String policyDir = this.repofilePath.getAbsolutePath();
		int startIndex = policyDir.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
		policyDir = policyDir.substring(startIndex, policyDir.length());
		if(policyDir.contains(CONFIG)){
			domain1 = policyDir.substring(0,policyDir.indexOf(CONFIG));
		}else if(policyDir.contains(ACTION)){
			domain1 = policyDir.substring(0,policyDir.indexOf(ACTION));	
		}else{
			domain1 = policyDir.substring(0,policyDir.indexOf(DECISION));	
		}
		LOG.info("print the main domain value"+policyDir);		
		return domain1;
	}

	//Edit the Policy
	private JSONObject editFile(JSONObject params) throws ServletException {
		// get content
		try {
			String mode = params.getString("mode");
			String path = params.getString("path");
			LOG.debug("editFile path: {}"+ path);

			File policyFile = new File(REPOSITORY_BASE_PATH, path);

			Object policy = XACMLPolicyScanner.readPolicy(new FileInputStream(policyFile));
			Path fullPath = Paths.get(policyFile.getAbsolutePath(), new String[0]);
			PolicyAdapter policyAdapter  = new PolicyAdapter();		
			policyAdapter.setData(policy);
			String dirPath = fullPath.getParent().toString().substring(fullPath.getParent().toString().lastIndexOf(REPOSITORY)+11);
			policyAdapter.setDirPath(dirPath);
			policyAdapter.setParentPath(fullPath.getParent());

			if("viewPolicy".equalsIgnoreCase(mode)){
				policyAdapter.setReadOnly(true);
				policyAdapter.setEditPolicy(false);
			}else{
				policyAdapter.setReadOnly(false);
				policyAdapter.setEditPolicy(true);
			}

			policyAdapter.setPolicyData(policy);
			policyAdapter.setPolicyName(FilenameUtils.removeExtension(policyFile.getName()));

			String	policyNameValue = null ;
			String	configPolicyName = null ;
			if(policyAdapter.getPolicyName().startsWith("Config_PM")){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "ClosedLoop_PM";
			}else if(policyAdapter.getPolicyName().startsWith("Config_Fault")){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "ClosedLoop_Fault";
			}else if(policyAdapter.getPolicyName().startsWith("Config_FW")){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "Firewall Config";
			}else if(policyAdapter.getPolicyName().startsWith("Config_BRMS_Raw")){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "BRMS_Raw";
			}else if(policyAdapter.getPolicyName().startsWith("Config_BRMS_Param")){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "BRMS_Param";
			}else if(policyAdapter.getPolicyName().startsWith("Config_MS")){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "DCAE Micro Service";
			}else if(policyAdapter.getPolicyName().startsWith("Action") || policyAdapter.getPolicyName().startsWith("Decision") ){
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			}
			else{
				policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
				configPolicyName = "Base";
			}
			if (policyNameValue != null) {
				policyAdapter.setPolicyType(policyNameValue);
			}
			if (configPolicyName != null) {
				policyAdapter.setConfigPolicyType(configPolicyName);
			}

			if("Action".equalsIgnoreCase(policyAdapter.getPolicyType())){
				ActionPolicyController actionController = new ActionPolicyController();
				actionController.PrePopulateActionPolicyData(policyAdapter);
			}
			if("Decision".equalsIgnoreCase(policyAdapter.getPolicyType())){
				DecisionPolicyController decisionController = new DecisionPolicyController();
				decisionController.PrePopulateDecisionPolicyData(policyAdapter);
			}
			if("Config".equalsIgnoreCase(policyAdapter.getPolicyType())){
				if("Base".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreatePolicyController baseController = new CreatePolicyController();
					baseController.PrePopulateBaseConfigPolicyData(policyAdapter);
				}
				else if("BRMS_Raw".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreateBRMSRawController brmsController = new CreateBRMSRawController();
					brmsController.PrePopulateBRMSRawPolicyData(policyAdapter);
				}
				else if("BRMS_Param".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreateBRMSParamController paramController = new CreateBRMSParamController();
					paramController.PrePopulateBRMSParamPolicyData(policyAdapter);
				}
				else if("ClosedLoop_Fault".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreateClosedLoopFaultController newFaultTemplate =  new CreateClosedLoopFaultController();
					newFaultTemplate.PrePopulateClosedLoopFaultPolicyData(policyAdapter);
				}
				else if("ClosedLoop_PM".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreateClosedLoopPMController pmController = new CreateClosedLoopPMController();
					pmController.PrePopulateClosedLoopPMPolicyData(policyAdapter);
				}
				else if("DCAE Micro Service".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreateDcaeMicroServiceController msController = new CreateDcaeMicroServiceController();
					msController.PrePopulateDCAEMSPolicyData(policyAdapter);
				}
				else if("Firewall Config".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
					CreateFirewallController firewallController = new CreateFirewallController();
					firewallController.PrePopulateFWPolicyData(policyAdapter);
				}	
			}


			policyAdapter.setParentPath(null);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(policyAdapter);
			JsonNode jsonNode = mapper.readTree(json);

			return new JSONObject().put(RESULT, jsonNode);
		} catch (Exception e) {
			LOG.error("editFile", e);
			return error(e.getMessage());
		}
	}

	//Add Scopes
	private JSONObject addFolder(JSONObject params, HttpServletRequest request) throws ServletException {
		String name = "";
		
		try {
			String userId = UserUtils.getUserIdFromCookie(request);
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
				LOG.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Exception Occured While Adding Scope"+e);
			}
			
			
			LOG.debug("addFolder path: {} name: {}" + path +name);
			File newDir = new File(REPOSITORY_BASE_PATH, name);
			if(!newDir.exists()){
				if (!newDir.mkdir()) {
					throw new Exception("Can't create directory: " + newDir.getAbsolutePath());
				}
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
				PolicyController.SavePolicyScope(newScope);	
			}else{
				return error("Scope Already Exists");
			}
			
			return success();
		} catch (Exception e) {
			LOG.error("addFolder", e);
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
