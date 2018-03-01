/*-
 * ================================================================================
 * ONAP Portal SDK
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property
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

import static org.junit.Assert.*;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;

public class AdminAuthExtensionTest {

	private CommonClassDao commonClassDao;
	private AdminAuthExtension extension;
	private User user;


	@Before
	public void setUp(){
		extension = new AdminAuthExtension();
		commonClassDao = Mockito.mock(CommonClassDao.class);
		Mockito.doNothing().when(commonClassDao).updateQuery("");
		Mockito.doNothing().when(commonClassDao).save(new Object());
		extension.setCommonClassDao(commonClassDao);
		user = new User();
		user.setFirstName("Test");
		user.setLoginId("Test");
	}

	@Test
	public void testAdminAuthExtension(){
		try{
			callSaveUserFunction("Policy Super Admin");
			callSaveUserFunction("Policy Super Editor");
			callSaveUserFunction("Policy Super Guest");
			callSaveUserFunction("Policy Admin");
			callSaveUserFunction("Policy Editor");
			callSaveUserFunction("Policy Guest");
			extension.editUserExtension(user);
			extension.saveUserRoleExtension(null, user);
		}catch(Exception e){
			fail("Not Expecting any exception.");
		}
	}

	@Test
	public void expectException(){
		try{
			extension.saveUserExtension(null);
		}catch(Exception e){
			assertEquals(NullPointerException.class, e.getClass());
		}
	}

	public void callSaveUserFunction(String roleName){
		SortedSet<Role> roles = new TreeSet<>();
		Role role = new Role();
		role.setName(roleName);
		roles.add(role);
		user.setRoles(roles);
		extension.saveUserExtension(user);
	}
}
