/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.xacml.rest.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openecomp.policy.pap.xacml.rest.HibernateSession;
import org.openecomp.policy.rest.dao.PolicyScopeResourceDao;
import org.openecomp.policy.rest.jpa.PolicyScopeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

@Service("PolicyScopeResourceDao")
public class PolicyScopeResourceDaoImpl implements PolicyScopeResourceDao{
	private static final Log logger = LogFactory.getLog(PolicyScopeResourceDaoImpl.class);
	@Autowired
	SessionFactory sessionfactory;
	
	public SessionFactory getSessionfactory() {
		return sessionfactory;
	}

	public void setSessionfactory(SessionFactory sessionfactory) {
		this.sessionfactory = sessionfactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PolicyScopeResource> getPolicyScopeResourceData() {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		List<PolicyScopeResource> attributeData = null;
        try {
        	Criteria cr = session.createCriteria(PolicyScopeResource.class);
            attributeData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyScopeResource Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return attributeData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPolicyScopeResourceDataByName() {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		List<String> data = new ArrayList<String>();
        try {
        	Criteria cr = session.createCriteria(PolicyScopeResource.class);
            List<PolicyScopeResource> attributeData = cr.list();     
            for(int i = 0; i < attributeData.size(); i++){
            	 data.add(attributeData.get(i).getName());
            }
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyScopeResource Table"+e);	
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
	public void Save(PolicyScopeResource attribute) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(attribute);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving PolicyScopeResource Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
	}

	@Override
	public void delete(PolicyScopeResource attribute) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(attribute);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting PolicyScopeResource Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void update(PolicyScopeResource attribute) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.update(attribute);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating PolicyScopeResource Table"+e);	
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
	public List<PolicyScopeResource> CheckDuplicateEntry(String value) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		List<PolicyScopeResource> data = null;
		try {
			Criteria cr = session.createCriteria(PolicyScopeResource.class);
			cr.add(Restrictions.eq("name",value));
			data = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyScopeResource Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return data;
	}
  
}
