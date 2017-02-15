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


package org.openecomp.portalapp.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.UserApp;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.client.AppContextManager;
import org.openecomp.portalsdk.core.onboarding.crossapi.IPortalRestAPIService;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalAPIException;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalTimeoutHandler;
import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.openecomp.portalsdk.core.service.WebServiceCallService;
import org.openecomp.portalsdk.core.util.JSONUtil;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.slf4j.MDC;


public class OnBoardingApiServiceImplPolicy implements IPortalRestAPIService {
	RoleService roleService;
	UserProfileService userProfileService;
	AdminAuthExtension adminAuthExtension;
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnBoardingApiServiceImplPolicy.class);
	
	public OnBoardingApiServiceImplPolicy(){
		roleService = AppContextManager.getAppContext().getBean(RoleService.class);
		userProfileService =  AppContextManager.getAppContext().getBean(UserProfileService.class);
		adminAuthExtension = AppContextManager.getAppContext().getBean(AdminAuthExtension.class);
	}

	private void setCurrentAttributes(User user, EcompUser userJson){
		user.setEmail(userJson.getEmail());
		user.setFirstName(userJson.getFirstName());
		user.setHrid(userJson.getHrid());
		user.setJobTitle(userJson.getJobTitle());
		user.setLastName(userJson.getLastName());
		user.setLoginId(userJson.getLoginId());
		user.setOrgManagerUserId(userJson.getOrgManagerUserId());
		user.setMiddleInitial(userJson.getMiddleInitial());
		user.setOrgCode(userJson.getOrgCode());
		user.setOrgId(userJson.getOrgId());
		user.setPhone(userJson.getPhone());
		user.setOrgUserId(userJson.getOrgUserId());		
		user.setActive(userJson.isActive());
	}
	
	
	@Override
	public void pushUser(EcompUser userJson) throws PortalAPIException {
		
		logger.debug(EELFLoggerDelegate.debugLogger, "pushUser was invoked" + userJson);
		
		User user = new User();
		String response = "";

		logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## userJson: " + userJson);

		try {
			
			//Set input attributes to the object about to be saved
			setCurrentAttributes(user, userJson);
			
			user.setRoles(new TreeSet());
			user.setUserApps(new TreeSet());
			user.setPseudoRoles(new TreeSet());

			userProfileService.saveUser(user);
			adminAuthExtension.saveUserExtension(user);
			logger.debug(EELFLoggerDelegate.debugLogger, "push user success.");
			response = "push user success.";
			response = JSONUtil.convertResponseToJSON(response);
		} catch (Exception e) {
			e.printStackTrace();
			response = "push user failed with error: " + e.getMessage();
			logger.error(EELFLoggerDelegate.debugLogger, response);
			logger.error(EELFLoggerDelegate.errorLogger, "Error happened during OnboardingApiService.pushUser operation: " + response);
			logger.error(EELFLoggerDelegate.debugLogger, "Error happened during OnboardingApiService.pushUser operation: " + response);
	    	logger.info(EELFLoggerDelegate.metricsLogger, "OnboardingApiService.pushUser operation has failed.");
			throw new PortalAPIException(response, e);
		}finally {
			MDC.remove(SystemProperties.MDC_TIMER);
		}
	}

	@Override
	public void editUser(String loginId, EcompUser userJson) throws PortalAPIException {
		
		logger.debug(EELFLoggerDelegate.debugLogger, "OnboardingApi editUser was invoked" + userJson);
	
		User editUser = new User();
		String response = "";
		logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: " + loginId);
		logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## userJson: " + userJson);

		try {

			setCurrentAttributes(editUser, userJson);
			
			if (editUser.getOrgUserId() != null) {
				editUser.setLoginId(editUser.getOrgUserId());
			}

			User domainUser = userProfileService.getUserByLoginId(loginId);
			if (domainUser != null)
				domainUser = JSONUtil.mapToDomainUser(domainUser, editUser);
			else
				domainUser = editUser;
			userProfileService.saveUser(domainUser);
			adminAuthExtension.saveUserExtension(domainUser);

			logger.debug(EELFLoggerDelegate.debugLogger, "edit user success.");
			response = "edit user success.";
			response = JSONUtil.convertResponseToJSON(response);
		} catch (Exception e) {
			e.printStackTrace();
			response = "edit user failed with error: " + e.getMessage();
			logger.error(EELFLoggerDelegate.errorLogger, response);
			logger.error(EELFLoggerDelegate.debugLogger, "Error happened during OnboardingApiService.editUser operation: " + response);
			logger.error(EELFLoggerDelegate.errorLogger, "Error happened during OnboardingApiService.editUser operation: " + response);
			throw new PortalAPIException(response, e);
		}finally {
			MDC.remove(SystemProperties.MDC_TIMER);
		}

		//return response;	
	}

	@Override
	public EcompUser getUser(String loginId) throws PortalAPIException {
		try{
			logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: " + loginId);
		
			User user = userProfileService.getUserByLoginId(loginId);
			
			if(user == null){
				logger.info(EELFLoggerDelegate.debugLogger, "User + " + loginId + " doesn't exist");
				return null;
			}
			else	
				return UserUtils.convertToEcompUser(user);
		}
		catch(Exception e){
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
			return null;	
		}

	}

	@Override
	public List<EcompUser> getUsers() throws PortalAPIException {


		String response = "";
		
		try {
			
			List<User> users = userProfileService.findAllActive();
			List<EcompUser> ecompUsers = new ArrayList<EcompUser>();
			for(User user : users)
				ecompUsers.add(UserUtils.convertToEcompUser(user));
			
			return ecompUsers;
		
		}
		catch (Exception pe){
			
			response = "getUsers failed with error: " + pe.getMessage();
			pe.printStackTrace();
			logger.error(EELFLoggerDelegate.debugLogger, response);
			logger.error(EELFLoggerDelegate.errorLogger, response);
			throw new PortalAPIException(response, pe);
		}
		
		
	}

	@Override
	public List<EcompRole> getAvailableRoles() throws PortalAPIException{

		String response = "";
		
		try{
		List<Role> roles = roleService.getActiveRoles();
		List<EcompRole> ecompRoles = new ArrayList<EcompRole>();
		
		for(Role role : roles)
			ecompRoles.add(UserUtils.convertToEcompRole(role));
		
		return ecompRoles;
		
		}
		catch (Exception pe){
			response = "getUsers failed with error: " + pe.getMessage();
			pe.printStackTrace();
			logger.error(EELFLoggerDelegate.debugLogger, response);
			logger.error(EELFLoggerDelegate.errorLogger, response);
			throw new PortalAPIException(response, pe);
		}	
	}

	@Override
	public void pushUserRole(String loginId, List<EcompRole> rolesJson) throws PortalAPIException {
		
	
		String response = "";
		try {
			logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: " + loginId);
			logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## rolesJson: " + rolesJson);
			User user = userProfileService.getUserByLoginId(loginId);

			SortedSet<Role> roles = new TreeSet();
			for (EcompRole role : rolesJson) {
				roles.add(roleService.getRole(role.getId()));
			}
			// Replace existing roles with new ones
			replaceExistingRoles(roles, user);

			logger.debug(EELFLoggerDelegate.debugLogger, "push user role success.");
			response = "push user role success.";
			response = JSONUtil.convertResponseToJSON(response);

		} catch (Exception e) {
			response = "pushUserRole failed with error: " + e.getMessage();
			e.printStackTrace();
			logger.error(EELFLoggerDelegate.debugLogger, response);
			logger.error(EELFLoggerDelegate.errorLogger, response);
			throw new PortalAPIException(response, e);
		}finally {
			MDC.remove(SystemProperties.MDC_TIMER);
		}

	}

	@Override
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
		
		logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: " + loginId);
		
		List<EcompRole> ecompRoles = new ArrayList<EcompRole>();
		try {
			
			
			User user = userProfileService.getUserByLoginId(loginId);
			
			SortedSet<Role> currentRoles = null;
			if(user != null){
				
				currentRoles = user.getRoles();
				
				if(currentRoles != null)
				for(Role role : currentRoles)
					ecompRoles.add(UserUtils.convertToEcompRole(role));
			}
			return ecompRoles;
		}
		catch (Exception e){
			String response = "getUserRoles failed with error: " + e.getMessage();
			e.printStackTrace();
			logger.error(EELFLoggerDelegate.debugLogger, response);
			logger.debug(EELFLoggerDelegate.errorLogger, response);
			throw new PortalAPIException(response, e);
		}
	}

	private void replaceExistingRoles(SortedSet<Role> roles, User user) {
		// 1. remove existing roles
		Set<UserApp> userApps = user.getUserApps();
		Iterator appsItr = (Iterator) userApps.iterator();
		while (appsItr.hasNext()) {
			UserApp tempUserApp = (UserApp)appsItr.next();
			boolean roleFound = false;
			for (Role role : roles) {
				if (tempUserApp.getRole().getId().equals(role.getId())) {
					roleFound = true;
					break;
				}
			}
			if (!roleFound)
				appsItr.remove();
		}
		user.setUserApps(userApps);
		userProfileService.saveUser(user);

		// 2. add new roles
		user.setRoles(roles);
		userProfileService.saveUser(user);
		adminAuthExtension.saveUserExtension(user);
	}


	@Override
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
		WebServiceCallService securityService = AppContextManager.getAppContext().getBean(WebServiceCallService.class);
		try {
			String appUser = request.getHeader("username");
			String password = request.getHeader("password");
			boolean flag = securityService.verifyRESTCredential(null, appUser, password);		
			return flag;
			
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Failed to authenticate" + e.getMessage());
			throw new PortalAPIException("Failed to authenticate: " + e.getMessage());			
		}
		
	}

	public String getSessionTimeOuts() throws Exception{
		return PortalTimeoutHandler.gatherSessionExtensions();
	}
	
	public void updateSessionTimeOuts(String sessionMap) throws Exception{
		PortalTimeoutHandler.updateSessionExtensions(sessionMap);
	}
}
