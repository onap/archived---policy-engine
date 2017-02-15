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


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openecomp.policy.dao.PolicyEditorScopesDao;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

@Service("PolicyEditorScopesDao")
public class PolicyEditorScopesDaoImpl implements PolicyEditorScopesDao {
	private static final Log logger = LogFactory.getLog(PolicyEditorScopesDaoImpl.class);
	@Autowired
	SessionFactory sessionfactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyEditorScopes> getPolicyEditorScopesData() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();	
		List<PolicyEditorScopes> scopeNameData = null;
		try {
			Criteria cr = session.createCriteria(PolicyEditorScopes.class);
			scopeNameData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyEditorScopes Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return scopeNameData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPolicyEditorScopesDataByName() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();	
		List<String> data = new ArrayList<String>();    
		try {
			Criteria cr = session.createCriteria(PolicyEditorScopes.class);
			List<PolicyEditorScopes> scopeNameData = cr.list();
			for(int i = 0; i < scopeNameData.size(); i++){
				data.add(scopeNameData.get(i).getScopeName());
			}
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyEditorScopes Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return data;
	}

	@Override
	public void Save(PolicyEditorScopes policyEditorScopes) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(policyEditorScopes);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving PolicyEditorScopes Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}

	}

	@Override
	public void delete(PolicyEditorScopes policyEditorScopes) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(policyEditorScopes);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting PolicyEditorScopes Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}

	}

	@Override
	public void update(PolicyEditorScopes policyEditorScopes) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(policyEditorScopes);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating PolicyEditorScopes Table"+e);	
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
	public List<PolicyEditorScopes> getListOfPolicyScopes(String query) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		List<PolicyEditorScopes> data = null;
		try {
			Query hbquery = session.createQuery(query);
			data = hbquery.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyEditorScopes Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return data;
	}

	@Override
	public void updateQuery(String policyScopeQuery) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();	
		try {
			Query hbquery = session.createQuery(policyScopeQuery);
			hbquery.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating PolicyEditorScopes Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

}
