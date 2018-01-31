/*-
 * ================================================================================
 * ONAP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
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
 * ================================================================================
 */
package org.onap.portalapp.service;

import java.util.Set;

import org.onap.policy.model.Roles;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.portalapp.service.IAdminAuthExtension;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("adminAuthExtension")
@Transactional
/**
 * Provides empty implementations of the methods in IAdminAuthExtension.
 */
public class AdminAuthExtension implements IAdminAuthExtension {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AdminAuthExtension.class);

	@Autowired
	CommonClassDao commonClassDao;


	/*
	 * (non-Javadoc)
	 * @see org.openecomp.portalapp.service.IAdminAuthExtension#saveUserExtension(org.openecomp.portalsdk.core.domain.User)
	 */
	public void saveUserExtension(User user) {
		logger.debug("saveUserExtension");
		savePolicyRole(null, user);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openecomp.portalapp.service.IAdminAuthExtension#editUserExtension(org.openecomp.portalsdk.core.domain.User)
	 */
	public void editUserExtension(User user) {
		logger.debug("editUserExtension");
	}

	/*
	 * (non-Javadoc)
	 * @see org.openecomp.portalapp.service.IAdminAuthExtension#saveUserRoleExtension(java.util.Set, org.openecomp.portalsdk.core.domain.User)
	 */
	public void saveUserRoleExtension(Set<Role> roles, User user) {
		logger.debug("saveUserRoleExtension");
		savePolicyRole(roles, user);
	}

	private void savePolicyRole(Set<Role> roles, User user){
		System.out.println("User Object Recieved");
		try{
			Roles roles1 = new Roles();
			roles1.setName(user.getFullName());
			roles1.setLoginId(user.getLoginId());
			if(user.getRoles() != null){
				String query = "delete from Roles where loginid='"+user.getLoginId()+"'";
				commonClassDao.updateQuery(query);
				for(Role role : user.getRoles()){
					System.out.println("User Role"+role);
					if(role.getName().trim().equalsIgnoreCase("Policy Super Admin") || role.getName().trim().equalsIgnoreCase("System Administrator") || role.getName().trim().equalsIgnoreCase("Standard User") ){
						roles1.setRole("super-admin");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Super Editor")){
						roles1.setRole("super-editor");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Super Guest")){
						roles1.setRole("super-guest");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Admin")){
						roles1.setRole("admin");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Editor")){
						roles1.setRole("editor");
					}else if(role.getName().trim().equalsIgnoreCase("Policy Guest")){
						roles1.setRole("guest");
					}
					commonClassDao.save(roles1);
				}
			}

			UserInfo userInfo = new UserInfo();
			userInfo.setUserLoginId(user.getLoginId());
			userInfo.setUserName(user.getFullName());
			commonClassDao.save(userInfo);
			System.out.println("User Object Updated Successfully");
		}
		catch(Exception e){
			logger.error("Exception caused while Setting role to Policy DB"+e);
		}
	}

}
