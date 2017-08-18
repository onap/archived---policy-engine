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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.admin.PolicyNotificationMail;
import org.onap.policy.admin.RESTfulPAPEngine;
import org.onap.policy.model.PDPGroupContainer;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.XacmlAdminAuthorization;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.FunctionDefinition;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.UserInfo;
import org.openecomp.policy.model.Roles;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;


@Controller
@RequestMapping("/")
public class PolicyController extends RestrictedBaseController {
	private static final Logger	policyLogger	= FlexLogger.getLogger(PolicyController.class);

	private static CommonClassDao commonClassDao;

	// Our authorization object
	//
	XacmlAdminAuthorization authorizer = new XacmlAdminAuthorization();
	//
	// The PAP Engine
	//
	private static PAPPolicyEngine papEngine;

	private static String logTableLimit;
	private static String systemAlertTableLimit;
	protected static Map<String, String> dropDownMap = new HashMap<>();
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

	//Constant variables used across Policy-sdk
	private static final String policyData = "policyData";
	private static final String characterEncoding = "UTF-8";
	private static final String contentType = "application/json";
	private static final String file = "file";

	//Smtp Java Mail Properties
	private static String smtpHost = null;
	private static String smtpPort = null;
	private static String smtpUsername = null;
	private static String smtpPassword = null;
	private static String smtpApplicationName = null;
	private static String smtpEmailExtension = null;
	//log db Properties
	private static String logdbDriver = null;
	private static String logdbUrl = null;
	private static String logdbUserName = null;
	private static String logdbPassword = null;
	private static String logdbDialect = null;
	//Xacml db properties
	private static String xacmldbUrl = null;
	private static String xacmldbUserName = null;
	private static String xacmldbPassword = null;

	//AutoPush feature.
	private static String autoPushAvailable;
	private static String autoPushDSClosedLoop;
	private static String autoPushDSFirewall;
	private static String autoPushDSMicroservice;
	private static String autoPushPDPGroup;

	//papURL
	private static String papUrl;

	//MicroService Model Properties
	private static String msOnapName;
	private static String msPolicyName;

	//WebApp directories
	private static String configHome;
	private static String actionHome;

	@Autowired
	private PolicyController(CommonClassDao commonClassDao){
		PolicyController.commonClassDao = commonClassDao;
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
			//pap url
			setPapUrl(prop.getProperty("xacml.rest.pap.url"));
			// get the property values
			setSmtpHost(prop.getProperty("onap.smtp.host"));
			setSmtpPort(prop.getProperty("onap.smtp.port"));
			setSmtpUsername(prop.getProperty("onap.smtp.userName"));
			setSmtpPassword(prop.getProperty("onap.smtp.password"));
			setSmtpApplicationName(prop.getProperty("onap.application.name"));
			setSmtpEmailExtension(prop.getProperty("onap.smtp.emailExtension"));
			//Log Database Properties
			setLogdbDriver(prop.getProperty("xacml.log.db.driver"));
			setLogdbUrl(prop.getProperty("xacml.log.db.url"));
			setLogdbUserName(prop.getProperty("xacml.log.db.user"));
			setLogdbPassword(prop.getProperty("xacml.log.db.password"));
			setLogdbDialect(prop.getProperty("onap.dialect"));
			//Xacml Database Properties
			setXacmldbUrl(prop.getProperty("javax.persistence.jdbc.url"));
			setXacmldbUserName(prop.getProperty("javax.persistence.jdbc.user"));
			setXacmldbPassword(prop.getProperty("javax.persistence.jdbc.password"));
			//AutoPuh
			setAutoPushAvailable(prop.getProperty("xacml.automatic.push"));
			setAutoPushDSClosedLoop(prop.getProperty("xacml.autopush.closedloop"));
			setAutoPushDSFirewall(prop.getProperty("xacml.autopush.firewall"));
			setAutoPushDSMicroservice(prop.getProperty("xacml.autopush.microservice"));
			setAutoPushPDPGroup(prop.getProperty("xacml.autopush.pdpGroup"));
			//Micro Service Properties
			setMsOnapName(prop.getProperty("xacml.policy.msOnapName"));
			setMsPolicyName(prop.getProperty("xacml.policy.msPolicyName"));
			//WebApp directories
			setConfigHome(prop.getProperty("xacml.rest.config.webapps") + "Config");
			setActionHome(prop.getProperty("xacml.rest.config.webapps") + "Action");
			//Get the Property Values for Dashboard tab Limit
			try{
				setLogTableLimit(prop.getProperty("xacml.onap.dashboard.logTableLimit"));
				setSystemAlertTableLimit(prop.getProperty("xacml.onap.dashboard.systemAlertTableLimit"));
			}catch(Exception e){
				policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Dashboard tab Property fields are missing" +e);
				setLogTableLimit("5000");
				setSystemAlertTableLimit("2000");
			}
			System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "xacml.admin.properties");
		} catch (IOException ex) {
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured while reading the Smtp properties from xacml.admin.properties file" +ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Exception Occured while Closing the xacml.admin.properties file" +e);
				}
			}
		}

		//Initialize the FunctionDefinition table at Server Start up
		Map<Datatype, List<FunctionDefinition>> functionMap = getFunctionDatatypeMap();
		for (Datatype id : functionMap.keySet()) {
			List<FunctionDefinition> functionDefinations = functionMap.get(id);
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
		mapDatatype2Function = new HashMap<>();
		mapID2Function = new  HashMap<>();
		List<Object> functiondefinitions = commonClassDao.getData(FunctionDefinition.class);
		for (int i = 0; i < functiondefinitions.size(); i ++) {
			FunctionDefinition value = (FunctionDefinition) functiondefinitions.get(i);
			mapID2Function.put(value.getXacmlid(), value);
			if (!mapDatatype2Function.containsKey(value.getDatatypeBean())) {
				mapDatatype2Function.put(value.getDatatypeBean(), new ArrayList<FunctionDefinition>());
			}
			mapDatatype2Function.get(value.getDatatypeBean()).add(value);
		}
	}

	@RequestMapping(value={"/get_FunctionDefinitionDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getFunctionDefinitionData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("functionDefinitionDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(FunctionDefinition.class, "shortname")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE +"Error while retriving the Function Definition data"+e);
		}
	}

	public PolicyEntity getPolicyEntityData(String scope, String policyName){
		String key = scope + ":" + policyName;
		List<Object> data = commonClassDao.getDataById(PolicyEntity.class, "scope:policyName", key);
		return (PolicyEntity) data.get(0);
	}

	public static Map<String, Roles> getUserRoles(String userId) {
		Map<String, Roles> scopes = new HashMap<>();
		List<Object> roles = commonClassDao.getDataById(Roles.class, "loginId", userId);
		if (roles != null && !roles.isEmpty()) {
			for (Object role : roles) {
				scopes.put(((Roles) role).getScope(), (Roles) role);
			}
		}
		return scopes;
	}

	public List<String> getRolesOfUser(String userId) {
		List<String> rolesList = new ArrayList<>();
		List<Object> roles = commonClassDao.getDataById(Roles.class, "loginId", userId);
		for (Object role: roles) {
			rolesList.add(((Roles) role).getRole());
		}
		return rolesList;
	}

	public List<Object> getRoles(String userId) {
		return commonClassDao.getDataById(Roles.class, "loginId", userId);
	}

	//Get List of User Roles
	@RequestMapping(value={"/get_UserRolesData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getUserRolesEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("userRolesDatas", mapper.writeValueAsString(getRolesOfUser(userId)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			policyLogger.error("Exception Occured"+e);
		}
	}

	//Policy tabs Model and View
	@RequestMapping(value= {"/policy", "/policy/Editor" } , method = RequestMethod.GET)
	public ModelAndView view(HttpServletRequest request){
		String myRequestURL = request.getRequestURL().toString();
		try {
			//
			// Set the URL for the RESTful PAP Engine
			//
			setPapEngine((PAPPolicyEngine) new RESTfulPAPEngine(myRequestURL));
			new PDPGroupContainer((PAPPolicyEngine) new RESTfulPAPEngine(myRequestURL));
		} catch (Exception e) {
			policyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while loading PAP"+e);
		}
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView("policy_Editor","model", model);
	}

	public PAPPolicyEngine getPapEngine() {
		return papEngine;
	}

	public static void setPapEngine(PAPPolicyEngine papEngine) {
		PolicyController.papEngine = papEngine;
	}

	public String getUserName(String createdBy) {
		String loginId = createdBy;
		List<Object> data = commonClassDao.getDataById(UserInfo.class, "loginId", loginId);
		return data.get(0).toString();
	}

	public static boolean getActivePolicy(String query) {
		if(commonClassDao.getDataByQuery(query, new SimpleBindings()).size() > 0){
			return true;
		}else{
			return false;
		}
	}

	public void executeQuery(String query) {
		commonClassDao.updateQuery(query);
	}

	public void saveData(Object cloneEntity) {
		commonClassDao.save(cloneEntity);
	}

	public void updateData(Object entity) {
		commonClassDao.update(entity);
	}

	public void deleteData(Object entity) {
		commonClassDao.delete(entity);
	}

	public List<Object> getData(@SuppressWarnings("rawtypes") Class className){
		return commonClassDao.getData(className);
	}

	public PolicyVersion getPolicyEntityFromPolicyVersion(String query){
		return (PolicyVersion) commonClassDao.getEntityItem(PolicyVersion.class, "policyName", query);
	}

	public List<Object> getDataByQuery(String query, SimpleBindings params){
		return commonClassDao.getDataByQuery(query, params);
	}


	@SuppressWarnings("rawtypes")
	public Object getEntityItem(Class className, String columname, String key){
		return commonClassDao.getEntityItem(className, columname, key);
	}


	public void watchPolicyFunction(PolicyVersion entity, String policyName, String mode){
		PolicyNotificationMail email = new PolicyNotificationMail();
		try {
			email.sendMail(entity, policyName, mode, commonClassDao);
		} catch (MessagingException e) {
			policyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Excepton Occured while Renaming/Deleting a Policy or Scope" + e);
		}
	}

	//Switch Version
	public JSONObject switchVersionPolicyContent(String policyName) {
		String dbCheckName = policyName.replace("/", ".");
		if(dbCheckName.contains("Config_")){
			dbCheckName = dbCheckName.replace(".Config_", ":Config_");
		}else if(dbCheckName.contains("Action_")){
			dbCheckName = dbCheckName.replace(".Action_", ":Action_");
		}else if(dbCheckName.contains("Decision_")){
			dbCheckName = dbCheckName.replace(".Decision_", ":Decision_");
		}
		String[] splitDBCheckName = dbCheckName.split(":");
		String query =   "FROM PolicyEntity where policyName like :splitDBCheckName1 and scope = :splitDBCheckName0";
		SimpleBindings params = new SimpleBindings();
		params.put("splitDBCheckName1", splitDBCheckName[1] + "%");
		params.put("splitDBCheckName0", splitDBCheckName[0]);
		List<Object> policyEntity = commonClassDao.getDataByQuery(query, params);
		List<String> av = new ArrayList<>();
		for(Object entity : policyEntity){
			PolicyEntity pEntity = (PolicyEntity) entity;
			String removeExtension = pEntity.getPolicyName().replace(".xml", "");
			String version = removeExtension.substring(removeExtension.lastIndexOf(".")+1);
			av.add(version);
		}
		if(policyName.contains("/")){
			policyName = policyName.replace("/", File.separator);
		}
		PolicyVersion entity = (PolicyVersion) commonClassDao.getEntityItem(PolicyVersion.class, "policyName", policyName);
		JSONObject el = new JSONObject();
		el.put("activeVersion", entity.getActiveVersion());
		el.put("availableVersions", av);
		el.put("highestVersion", entity.getHigherVersion());
		return el;
	}

	public static String getLogTableLimit() {
		return logTableLimit;
	}

	public static void setLogTableLimit(String logTableLimit) {
		PolicyController.logTableLimit = logTableLimit;
	}

	public static String getSystemAlertTableLimit() {
		return systemAlertTableLimit;
	}

	public static void setSystemAlertTableLimit(String systemAlertTableLimit) {
		PolicyController.systemAlertTableLimit = systemAlertTableLimit;
	}

	public static CommonClassDao getCommonClassDao() {
		return commonClassDao;
	}

	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		PolicyController.commonClassDao = commonClassDao;
	}

	public XacmlAdminAuthorization getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(XacmlAdminAuthorization authorizer) {
		this.authorizer = authorizer;
	}

	public static Map<Datatype, List<FunctionDefinition>> getMapDatatype2Function() {
		return mapDatatype2Function;
	}

	public static void setMapDatatype2Function(Map<Datatype, List<FunctionDefinition>> mapDatatype2Function) {
		PolicyController.mapDatatype2Function = mapDatatype2Function;
	}

	public static Map<String, FunctionDefinition> getMapID2Function() {
		return mapID2Function;
	}

	public static void setMapID2Function(Map<String, FunctionDefinition> mapID2Function) {
		PolicyController.mapID2Function = mapID2Function;
	}

	public static String getSmtpHost() {
		return smtpHost;
	}

	public static void setSmtpHost(String smtpHost) {
		PolicyController.smtpHost = smtpHost;
	}

	public static String getSmtpPort() {
		return smtpPort;
	}

	public static void setSmtpPort(String smtpPort) {
		PolicyController.smtpPort = smtpPort;
	}

	public static String getSmtpUsername() {
		return smtpUsername;
	}

	public static void setSmtpUsername(String smtpUsername) {
		PolicyController.smtpUsername = smtpUsername;
	}

	public static String getSmtpPassword() {
		return smtpPassword;
	}

	public static void setSmtpPassword(String smtpPassword) {
		PolicyController.smtpPassword = smtpPassword;
	}

	public static String getSmtpApplicationName() {
		return smtpApplicationName;
	}

	public static void setSmtpApplicationName(String smtpApplicationName) {
		PolicyController.smtpApplicationName = smtpApplicationName;
	}

	public static String getSmtpEmailExtension() {
		return smtpEmailExtension;
	}

	public static void setSmtpEmailExtension(String smtpEmailExtension) {
		PolicyController.smtpEmailExtension = smtpEmailExtension;
	}

	public static String getLogdbDriver() {
		return logdbDriver;
	}

	public static void setLogdbDriver(String logdbDriver) {
		PolicyController.logdbDriver = logdbDriver;
	}

	public static String getLogdbUrl() {
		return logdbUrl;
	}

	public static void setLogdbUrl(String logdbUrl) {
		PolicyController.logdbUrl = logdbUrl;
	}

	public static String getLogdbUserName() {
		return logdbUserName;
	}

	public static void setLogdbUserName(String logdbUserName) {
		PolicyController.logdbUserName = logdbUserName;
	}

	public static String getLogdbPassword() {
		return logdbPassword;
	}

	public static void setLogdbPassword(String logdbPassword) {
		PolicyController.logdbPassword = logdbPassword;
	}

	public static String getLogdbDialect() {
		return logdbDialect;
	}

	public static void setLogdbDialect(String logdbDialect) {
		PolicyController.logdbDialect = logdbDialect;
	}

	public static String getXacmldbUrl() {
		return xacmldbUrl;
	}

	public static void setXacmldbUrl(String xacmldbUrl) {
		PolicyController.xacmldbUrl = xacmldbUrl;
	}

	public static String getXacmldbUserName() {
		return xacmldbUserName;
	}

	public static void setXacmldbUserName(String xacmldbUserName) {
		PolicyController.xacmldbUserName = xacmldbUserName;
	}

	public static String getXacmldbPassword() {
		return xacmldbPassword;
	}

	public static void setXacmldbPassword(String xacmldbPassword) {
		PolicyController.xacmldbPassword = xacmldbPassword;
	}

	public static String getAutoPushAvailable() {
		return autoPushAvailable;
	}

	public static void setAutoPushAvailable(String autoPushAvailable) {
		PolicyController.autoPushAvailable = autoPushAvailable;
	}

	public static String getAutoPushDSClosedLoop() {
		return autoPushDSClosedLoop;
	}

	public static void setAutoPushDSClosedLoop(String autoPushDSClosedLoop) {
		PolicyController.autoPushDSClosedLoop = autoPushDSClosedLoop;
	}

	public static String getAutoPushDSFirewall() {
		return autoPushDSFirewall;
	}

	public static void setAutoPushDSFirewall(String autoPushDSFirewall) {
		PolicyController.autoPushDSFirewall = autoPushDSFirewall;
	}

	public static String getAutoPushDSMicroservice() {
		return autoPushDSMicroservice;
	}

	public static void setAutoPushDSMicroservice(String autoPushDSMicroservice) {
		PolicyController.autoPushDSMicroservice = autoPushDSMicroservice;
	}

	public static String getAutoPushPDPGroup() {
		return autoPushPDPGroup;
	}

	public static void setAutoPushPDPGroup(String autoPushPDPGroup) {
		PolicyController.autoPushPDPGroup = autoPushPDPGroup;
	}

	public static String getPapUrl() {
		return papUrl;
	}

	public static void setPapUrl(String papUrl) {
		PolicyController.papUrl = papUrl;
	}

	public static String getMsOnapName() {
		return msOnapName;
	}

	public static void setMsOnapName(String msOnapName) {
		PolicyController.msOnapName = msOnapName;
	}

	public static String getMsPolicyName() {
		return msPolicyName;
	}

	public static void setMsPolicyName(String msPolicyName) {
		PolicyController.msPolicyName = msPolicyName;
	}

	public static String getConfigHome() {
		return configHome;
	}

	public static void setConfigHome(String configHome) {
		PolicyController.configHome = configHome;
	}

	public static String getActionHome() {
		return actionHome;
	}

	public static void setActionHome(String actionHome) {
		PolicyController.actionHome = actionHome;
	}

	public static Object getMapaccess() {
		return mapAccess;
	}

	public static String getPolicydata() {
		return policyData;
	}

	public static String getCharacterencoding() {
		return characterEncoding;
	}

	public static String getContenttype() {
		return contentType;
	}

	public static String getFile() {
		return file;
	}
}
