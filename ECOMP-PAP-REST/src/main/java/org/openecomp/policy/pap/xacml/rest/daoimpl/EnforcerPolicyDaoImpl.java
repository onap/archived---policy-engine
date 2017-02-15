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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openecomp.policy.pap.xacml.rest.HibernateSession;
import org.openecomp.policy.rest.dao.EnforcerPolicyDao;
import org.openecomp.policy.rest.jpa.EnforcingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

@Service("EnforcerPolicyDao")
public class EnforcerPolicyDaoImpl implements EnforcerPolicyDao {
	private static final Log logger = LogFactory.getLog(EnforcerPolicyDaoImpl.class);
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
	public List<EnforcingType> getEnforcingTypeData() {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		List<EnforcingType> enforcingTypeData = null;
        try {
        	Criteria cr = session.createCriteria(EnforcingType.class);
            enforcingTypeData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying EnforcingType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return enforcingTypeData;
		
	}

	@Override
	public void Save(EnforcingType enforcingType) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(enforcingType);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving EnforcingType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void delete(EnforcingType enforcingType) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(enforcingType);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting EnforcingType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
	}

	@Override
	public void update(EnforcingType enforcingType) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.update(enforcingType);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating EnforcingType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

}
