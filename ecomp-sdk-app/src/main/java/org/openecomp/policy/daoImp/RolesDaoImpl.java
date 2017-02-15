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

package org.openecomp.policy.daoImp;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openecomp.policy.dao.RolesDao;
import org.openecomp.policy.model.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Service("RolesDao")
public class RolesDaoImpl  implements RolesDao{
	private static final Logger logger = FlexLogger.getLogger(RolesDaoImpl.class);
	@Autowired
	SessionFactory sessionfactory;
	
	@Override
	public void save(Roles role) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(role);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving Roles Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
	}
	
	@Override
	public void delete(Roles role) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();	
		try {
			System.out.println("delete from Roles where loginid = '"+role.getLoginId()+"'");
			Query q = session.createQuery(" delete from Roles where loginid = '"+role.getLoginId()+"'");
			q.executeUpdate();
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting Roles Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Roles> getUserRoles(String userId) {
		System.out.println("User Id:"+userId);
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		List<Roles> rolesData = null;
        try {
        	Criteria cr = session.createCriteria(Roles.class);
        	cr = cr.add(Restrictions.eq("loginId",userId));
            rolesData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying Roles Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return rolesData;
	}
}
