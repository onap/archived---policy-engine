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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.admin.PolicyNotificationMail;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.model.PDPGroupContainer;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.XacmlAdminAuthorization;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.Datatype;
import org.openecomp.policy.rest.jpa.FunctionDefinition;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.UserInfo;
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

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


@Controller
@RequestMapping("/")
public class PolicyController extends RestrictedBaseController {
	private static final Logger	LOGGER	= FlexLogger.getLogger(PolicyController.class);

	private static CommonClassDao commonClassDao;
	// Our authorization object
	//
	XacmlAdminAuthorization authorizer = new XacmlAdminAuthorization();
	//
	// The PAP Engine
	//
	private static PAPPolicyEngine papEngine;

	public static String logTableLimit;
	public static String systemAlertTableLimit;
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
	//Xacml db properties
	public static String xacmldbUrl = null;
	public static String xacmldbUserName = null;
	public static String xacmldbPassword = null;

	//AutoPush feature. 
	public static String autoPushAvailable;
	public static String autoPushDSClosedLoop;
	public static String autoPushDSFirewall;
	public static String autoPushDSMicroservice;
	public static String autoPushPDPGroup;
	
	//papURL
	public static String papUrl;
	
	//MicroService Model Properties
	public static String msEcompName;
	public static String msPolicyName;

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
			papUrl = prop.getProperty("xacml.rest.pap.url"); 
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
			//Xacml Database Properties
			xacmldbUrl = prop.getProperty("javax.persistence.jdbc.url");
			xacmldbUserName = prop.getProperty("javax.persistence.jdbc.user");
			xacmldbPassword = prop.getProperty("javax.persistence.jdbc.password");
			//AutoPuh
			autoPushAvailable=prop.getProperty("xacml.automatic.push");
			autoPushDSClosedLoop=prop.getProperty("xacml.autopush.closedloop");
			autoPushDSFirewall=prop.getProperty("xacml.autopush.firewall");
			autoPushDSMicroservice=prop.getProperty("xacml.autopush.microservice");
			autoPushPDPGroup=prop.getProperty("xacml.autopush.pdpGroup");
			//Micro Service Properties
			msEcompName=prop.getProperty("xacml.policy.msEcompName");
			msPolicyName=prop.getProperty("xacml.policy.msPolicyName");
			//Get the Property Values for Dashboard tab Limit 
			try{
				logTableLimit = prop.getProperty("xacml.ecomp.dashboard.logTableLimit");
				systemAlertTableLimit = prop.getProperty("xacml.ecomp.dashboard.systemAlertTableLimit");
			}catch(Exception e){
				LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Dashboard tab Property fields are missing" +e);
				logTableLimit = "5000";
				systemAlertTableLimit = "2000";
			}
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
		List<Object> functiondefinitions = commonClassDao.getData(FunctionDefinition.class);	
		for (int i = 0; i < functiondefinitions.size(); i ++) {
			FunctionDefinition value = (FunctionDefinition) functiondefinitions.get(i);
			mapID2Function.put(value.getXacmlid(), value);
			if (mapDatatype2Function.containsKey(value.getDatatypeBean()) == false) {
				mapDatatype2Function.put(value.getDatatypeBean(), new ArrayList<FunctionDefinition>());
			}
			mapDatatype2Function.get(value.getDatatypeBean()).add(value);
		}
	}

	@RequestMapping(value={"/get_FunctionDefinitionDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getFunctionDefinitionData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("functionDefinitionDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(FunctionDefinition.class, "shortname")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.equals(XACMLErrorConstants.ERROR_DATA_ISSUE +"Error while retriving the Function Definition data"+e);
		}
	}
	
	public PolicyEntity getPolicyEntityData(String scope, String policyName){
		String key = scope + ":" + policyName;
		List<Object> data = commonClassDao.getDataById(PolicyEntity.class, "scope:policyName", key);
		PolicyEntity entity = (PolicyEntity) data.get(0);
		return entity;
	}

	public static Map<String, Roles> getUserRoles(String userId) {
		Map<String, Roles> scopes = new HashMap<String, Roles>();
		List<Object> roles = commonClassDao.getDataById(Roles.class, "loginId", userId);
		if (roles != null && roles.size() > 0) {
			for (Object role : roles) {
				scopes.put(((Roles) role).getScope(), (Roles) role);
			}
		}
		return scopes;
	}

	public static  List<String> getRolesOfUser(String userId) {
		List<String> rolesList = new ArrayList<String>();
		List<Object> roles = commonClassDao.getDataById(Roles.class, "loginId", userId);
		for (Object role: roles) {
			rolesList.add(((Roles) role).getRole());
		}
		return rolesList;
	}

	public static List<Object> getRoles(String userId) {
		return commonClassDao.getDataById(Roles.class, "loginId", userId);
	}

	//Get List of User Roles
	@RequestMapping(value={"/get_UserRolesData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getUserRolesEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			String userId = UserUtils.getUserSession(request).getOrgUserId();
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
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while loading PAP"+e);
		}	
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("policy_Editor","model", model);
	}

	public static PAPPolicyEngine getPapEngine() {
		return papEngine;
	}

	public void setPapEngine(PAPPolicyEngine papEngine) {
		PolicyController.papEngine = papEngine;
	}

	public String getUserName(String createdBy) {
		String loginId = createdBy;
		List<Object> data = commonClassDao.getDataById(UserInfo.class, "loginId", loginId);
		return data.get(0).toString();
	}

	public static boolean getActivePolicy(String query) {
		if(commonClassDao.getDataByQuery(query).size() > 0){
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

	public PolicyVersion getPolicyEntityFromPolicyVersion(String query){
		PolicyVersion policyVersionEntity = (PolicyVersion) commonClassDao.getEntityItem(PolicyVersion.class, "policyName", query);
		return policyVersionEntity;	
	}

	public List<Object> getDataByQuery(String query){
		return commonClassDao.getDataByQuery(query);
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
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Excepton Occured while Renaming/Deleting a Policy or Scope" + e);
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
		String query =   "FROM PolicyEntity where policyName like'"+splitDBCheckName[1]+"%' and scope ='"+splitDBCheckName[0]+"'";
		List<Object> policyEntity = commonClassDao.getDataByQuery(query);
		List<String> av = new ArrayList<String>();
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
}

