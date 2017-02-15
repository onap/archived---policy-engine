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


import org.openecomp.policy.dao.RolesDao;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Service("adminAuthExtension")
@Transactional
public class AdminAuthExtension {
	
	@Autowired
	RolesDao rolesDAO;
	
	@Autowired
	UserInfoDao  userInfoDao;
	
	private static Logger logger = FlexLogger.getLogger(AdminAuthExtension.class);
	public void saveUserExtension(User user){
		System.out.println("User Object Recieved");
		try{
			Roles roles = new Roles();
			roles.setName(user.getFullName());
			roles.setLoginId(user.getLoginId());
			if(user.getRoles() != null){
				rolesDAO.delete(roles);
				for(Role role : user.getRoles()){ 
					if(role.getName().trim().equalsIgnoreCase("Policy Super Admin") || role.getName().trim().equalsIgnoreCase("System Administrator") || role.getName().trim().equalsIgnoreCase("Standard User") ){
						roles.setRole("super-admin");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Super Editor")){
						roles.setRole("super-editor");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Super Guest")){
						roles.setRole("super-guest");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Admin")){
						roles.setRole("admin");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Editor")){
						roles.setRole("editor");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Guest")){
						roles.setRole("guest");
					}	
					rolesDAO.save(roles);
				}	
			}
			
			UserInfo userInfo = new UserInfo();
			userInfo.setUserLoginId(user.getLoginId());
			userInfo.setUserName(user.getFullName());
			userInfoDao.save(userInfo);
		}
		catch(Exception e){
			logger.error("Exception caused while Setting role to Policy DB"+e);
		}
	}

}
