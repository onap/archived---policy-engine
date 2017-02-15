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

package org.openecomp.policy.controller;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.JSONObject;
import org.openecomp.policy.admin.PolicyNotificationMail;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.dao.FunctionDefinitionDao;
import org.openecomp.policy.dao.PolicyEditorScopesDao;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.dao.RolesDao;
import org.openecomp.policy.dao.WatchPolicyNotificationDao;
import org.openecomp.policy.model.PDPGroupContainer;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.XacmlAdminAuthorization;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.Datatype;
import org.openecomp.policy.rest.jpa.FunctionDefinition;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.util.Webapps;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.PAPPolicyEngine;

import com.att.research.xacml.api.pap.PAPEngine;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


@Controller
@RequestMapping("/")
public class PolicyController extends RestrictedBaseController {
	private static final Logger	LOGGER	= FlexLogger.getLogger(PolicyController.class);
	private static UserInfoDao userInfoDao;
	private static PolicyVersionDao policyVersionDao;
	private static RolesDao rolesDao;
	private static PolicyEditorScopesDao policyEditorScopesDao;
	private static WatchPolicyNotificationDao watchPolicyNotificationDao;
	// Our authorization object
	//
	XacmlAdminAuthorization authorizer = new XacmlAdminAuthorization();
	//
	// The PAP Engine
	//
	private static PAPPolicyEngine papEngine;
	private Path repositoryPath =  null;
	private static Path workspacePath;
	private static Path gitPath;
	public static String logTableLimit;
	public static String systemAlertTableLimit;
	public static String CONFIG_HOME = PolicyController.getConfigHome();
	public static String ACTION_HOME = PolicyController.getActionHome();
	protected static Map<String, String> dropDownMap = new HashMap<String, String>();
	public static Map<String, String> getDropDownMap() {
		return dropDownMap;
	}

	public static void setDropDownMap(Map<String, String> dropDownMap) {
		PolicyController.dropDownMap = dropDownMap;
	}

	public static String getDomain() {
		return XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_DOMAIN, "urn");
	}

	private static final Object mapAccess = new Object();
	private static Map<Datatype, List<FunctionDefinition>> mapDatatype2Function = null;
	private static Map<String, FunctionDefinition> mapID2Function = null;

	private static FunctionDefinitionDao functionDefinitionDao;
	
	//Smtp Java Mail Properties
	public static String smtpHost = null;
	public static String smtpPort = null;
	public static String smtpUsername = null;
	public static String smtpPassword = null;
	public static String smtpApplicationName = null;
	public static String smtpEmailExtension = null;
	//log db Properties
	public static String logdbDriver = null;
	public static String logdbUrl = null;
	public static String logdbUserName = null;
	public static String logdbPassword = null;
	public static String logdbDialect = null;


	@Autowired
	private PolicyController(UserInfoDao userinfoDao, PolicyVersionDao policyVersionDao, FunctionDefinitionDao functionDefinitionDao,
			RolesDao rolesDao, PolicyEditorScopesDao policyEditorScopesDao, WatchPolicyNotificationDao watchPolicyNotificationDao){
		PolicyController.userInfoDao = userinfoDao;
		PolicyController.policyVersionDao = policyVersionDao;
		PolicyController.functionDefinitionDao = functionDefinitionDao;
		PolicyController.rolesDao = rolesDao;
		PolicyController.policyEditorScopesDao = policyEditorScopesDao;
		PolicyController.watchPolicyNotificationDao = watchPolicyNotificationDao;
	}

	public PolicyController() {
	}

	@PostConstruct
	public void init(){
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("xacml.admin.properties");
			// load a properties file
			prop.load(input);
			// get the property values
			smtpHost = prop.getProperty("ecomp.smtp.host");
			smtpPort = prop.getProperty("ecomp.smtp.port");
			smtpUsername = prop.getProperty("ecomp.smtp.userName");
			smtpPassword = prop.getProperty("ecomp.smtp.password");
			smtpApplicationName = prop.getProperty("ecomp.application.name");
			smtpEmailExtension = prop.getProperty("ecomp.smtp.emailExtension");
			//Log Database Properties
			logdbDriver = prop.getProperty("xacml.log.db.driver");
			logdbUrl = prop.getProperty("xacml.log.db.url");
			logdbUserName = prop.getProperty("xacml.log.db.user");
			logdbPassword = prop.getProperty("xacml.log.db.password");
			logdbDialect = prop.getProperty("ecomp.dialect");
			//Get the Property Values for Dashboard tab Limit 
			try{
				logTableLimit = prop.getProperty("xacml.ecomp.dashboard.logTableLimit");
				systemAlertTableLimit = prop.getProperty("xacml.ecomp.dashboard.systemAlertTableLimit");
			}catch(Exception e){
				LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Dashboard tab Property fields are missing" +e);
				logTableLimit = "5000";
				systemAlertTableLimit = "2000";
			}
			repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_REPOSITORY));
			PolicyController.workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_WORKSPACE), getDefaultWorkspace());
			setGitPath(Paths.get(workspacePath.toString(), repositoryPath.getFileName().toString()));
			System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "xacml.admin.properties");
		} catch (IOException ex) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured while reading the Smtp properties from xacml.admin.properties file" +ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured while Closing the xacml.admin.properties file" +e);
				}
			}
		}
		
		//Initialize the FunctionDefinition table at Server Start up 
		Map<Datatype, List<FunctionDefinition>> functionMap = getFunctionDatatypeMap();
		for (Datatype id : functionMap.keySet()) {
			List<FunctionDefinition> functionDefinations = (List<FunctionDefinition>) functionMap.get(id);
			for (FunctionDefinition functionDef : functionDefinations) {
				dropDownMap.put(functionDef.getShortname(),functionDef.getXacmlid());
			}
		}

	}

	public static  Map<Datatype, List<FunctionDefinition>>	getFunctionDatatypeMap() {				
		synchronized(mapAccess) {
			if (mapDatatype2Function == null) {
				buildFunctionMaps();
			}
		}
		return mapDatatype2Function;
	}

	public static Map<String, FunctionDefinition> getFunctionIDMap() {
		synchronized(mapAccess) {
			if (mapID2Function == null) {
				buildFunctionMaps();
			}
		}
		return mapID2Function;
	}
	
	private static  void buildFunctionMaps() {
		mapDatatype2Function = new HashMap<Datatype, List<FunctionDefinition>>();
		mapID2Function = new  HashMap<String, FunctionDefinition>(); 
		List<FunctionDefinition> functiondefinitions = functionDefinitionDao.getFunctionDefinition();	
		for (int i = 0; i < functiondefinitions.size(); i ++) {
			FunctionDefinition value = functiondefinitions.get(i);
			mapID2Function.put(value.getXacmlid(), value);
			if (mapDatatype2Function.containsKey(value.getDatatypeBean()) == false) {
				mapDatatype2Function.put(value.getDatatypeBean(), new ArrayList<FunctionDefinition>());
			}
			mapDatatype2Function.get(value.getDatatypeBean()).add(value);
		}
	}
	
	public static Map<String, Roles> getUserRoles(String userId) {
		Map<String, Roles> scopes = new HashMap<String, Roles>();
		List<Roles> roles = rolesDao.getUserRoles(userId);
		if (roles != null && roles.size() > 0) {
			for (Roles role : roles) {
				scopes.put(role.getScope(), role);
			}
		}
		return scopes;
	}
	
	public static  List<String> getRolesOfUser(String userId) {
		List<String> rolesList = new ArrayList<String>();
		List<Roles> roles = rolesDao.getUserRoles(userId);
		for (Roles role: roles) {
			rolesList.add(role.getRole());
		}
		return rolesList;
	}

	public static List<Roles> getRoles(String userId) {
		return rolesDao.getUserRoles(userId);
	}

	//Get List of User Roles
	@RequestMapping(value={"/get_UserRolesData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getUserRolesEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			String userId = UserUtils.getUserIdFromCookie(request);
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("userRolesDatas", mapper.writeValueAsString(getRolesOfUser(userId)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	//Policy tabs Model and View 
	@RequestMapping(value= {"/policy", "/policy/*" }, method = RequestMethod.GET)
	public ModelAndView view(HttpServletRequest request){
		String myRequestURL = request.getRequestURL().toString();
		try {
			//
			// Set the URL for the RESTful PAP Engine
			//	
			setPapEngine((PAPPolicyEngine) new RESTfulPAPEngine(myRequestURL));
			new PDPGroupContainer((PAPPolicyEngine) new RESTfulPAPEngine(myRequestURL));
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while loading PAP"+e);
		}	
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("policy_Editor","model", model);
	}

	public static String getDefaultWorkspace() {
		return "admin";
	}

	public static PAPPolicyEngine getPapEngine() {
		return papEngine;
	}

	public void setPapEngine(PAPPolicyEngine papEngine) {
		PolicyController.papEngine = papEngine;
	}
    
	//Config and Action Directory's
	public static String getConfigHome() {
		return Webapps.getConfigHome();
	}

	public static String getActionHome() {
		return Webapps.getActionHome();
	}

	public static Path getGitPath() {
		return gitPath;
	}

	public static void setGitPath(Path gitPath) {
		PolicyController.gitPath = gitPath;
	}

	public static String getUserName(String createdBy) {
		String loginId = createdBy;
		return userInfoDao.getUserName(loginId);
	}

	public static boolean getActivePolicy(String query) {
		if(policyVersionDao.getActiveVersionPolicy(query).size() > 0){
			return true;
		}else{
			return false;
		}

	}

	//Get the Active Version of Policy List from Policy Version table
	public static List<PolicyVersion> getListOfActivePolicies(String query){
		return policyVersionDao.getActiveVersionPolicy(query);
	}

	public static void updatePolicyVersion(String query) {
		policyVersionDao.updateQuery(query);	
	}

	public static void SaveToPolicyVersion(PolicyVersion policyversion) {
		policyVersionDao.Save(policyversion);	
	}

	public static PolicyVersion getPolicyEntityFromPolicyVersion(String query){
		PolicyVersion policyVersionEntity = policyVersionDao.getPolicyVersionEntityByName(query).get(0);
		return policyVersionEntity;	
	}
	
	public static void SavePolicyScope(PolicyEditorScopes policyScope){
		policyEditorScopesDao.Save(policyScope);
	}
	
	public static List<PolicyEditorScopes> getListOfPolicyEditorScopes(String query){
		return policyEditorScopesDao.getListOfPolicyScopes(query);
	}
	
	public static void updatePolicyScopeEditorWithQuery(String policyScopeQuery) {
		policyEditorScopesDao.updateQuery(policyScopeQuery);
		
	}

	public static void updatePolicyScopeEditor(PolicyEditorScopes policyScopeQuery) {
		policyEditorScopesDao.update(policyScopeQuery);
		
	}
	
	public void WatchPolicyFunction(PolicyVersion entity, String policyName, String mode){
		PolicyNotificationMail email = new PolicyNotificationMail();
		try {
			email.sendMail(entity, policyName, mode, watchPolicyNotificationDao);
		} catch (MessagingException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Excepton Occured while Renaming/Deleting a Policy or Scope" + e);
		}
	}

	//Switch Version
	public JSONObject SwitchVersionPolicyContent(File policyFile) {
		Path parent = Paths.get(policyFile.toString().substring(0, policyFile.toString().lastIndexOf(File.separator)));
		String policyName = policyFile.toString().substring(policyFile.toString().indexOf("repository") +11);
		String removeExtension = policyName.replace(".xml", "");
		String activeVersion = removeExtension.substring(removeExtension.lastIndexOf(".")+1);
		String dbPolicyName = removeExtension.substring(0, removeExtension.lastIndexOf("."));
		String filterPolicyName = dbPolicyName.substring(dbPolicyName.lastIndexOf(File.separator)+1);
		FileFilter fileFilter = new WildcardFileFilter(filterPolicyName + "." + "*" + ".xml");
		File[] files = ((File) parent.toFile()).listFiles(fileFilter);
		List<String> av = new ArrayList<String>();
		for(File file : files){
			String fileName = file.toString().substring(file.toString().indexOf("repository") +11);
			String removeXMLExtension = fileName.replace(".xml", "");
			String availableVersion = removeXMLExtension.substring(removeXMLExtension.lastIndexOf(".")+1);
			av.add(availableVersion);
		}
		PolicyVersion entity = (PolicyVersion) policyVersionDao.getPolicyVersionEntityByName(dbPolicyName).get(0);
		String highestVersion = Integer.toString(entity.getHigherVersion());
		JSONObject el = new JSONObject();
		el.put("activeVersion", activeVersion);
		el.put("availableVersions", av);
		el.put("highestVersion", highestVersion);
		return el;
	}

	public static Path getUserWorkspace() {
		return PolicyController.workspacePath;
	}

}

