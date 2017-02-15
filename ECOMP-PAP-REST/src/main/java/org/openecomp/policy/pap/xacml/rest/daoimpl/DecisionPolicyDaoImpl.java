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
import org.openecomp.policy.pap.xacml.rest.HibernateSession;
import org.openecomp.policy.rest.dao.DecisionPolicyDao;
import org.openecomp.policy.rest.jpa.DecisionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

@Service("DecisionSettingsDao")
public class DecisionPolicyDaoImpl implements DecisionPolicyDao{
	private static final Log logger = LogFactory.getLog(DecisionPolicyDaoImpl.class);
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
	public List<DecisionSettings> getDecisionSettingsData() {
			Session session = HibernateSession.getSessionFactory();
			Transaction tx = session.beginTransaction();	
	        List<DecisionSettings> decisionSettingsData = null;
	        try {
	        	Criteria cr = session.createCriteria(DecisionSettings.class);
	        	decisionSettingsData = cr.list();
				tx.commit();
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying DecisionSettings Table"+e);	
			}finally{
				try{
					session.close();
				}catch(Exception e1){
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
				}
			}
			return decisionSettingsData;
			
		}

		@Override
		public void Save(DecisionSettings decisionSettings) {
			Session session = HibernateSession.getSessionFactory();
			Transaction tx = session.beginTransaction();
			try {
				session.persist(decisionSettings);
				tx.commit();	
			}catch(Exception e){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving DecisionSettings Table"+e);	
			}finally{
				try{
					session.close();
				}catch(Exception e1){
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
				}
			}
			
		}

		@Override
		public void delete(DecisionSettings decisionSettings) {
			Session session = HibernateSession.getSessionFactory();
			Transaction tx = session.beginTransaction();
			try {
				session.delete(decisionSettings);
				tx.commit();	
			}catch(Exception e){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting DecisionSettings Table"+e);	
			}finally{
				try{
					session.close();
				}catch(Exception e1){
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
				}
			}
		}

		@Override
		public void update(DecisionSettings decisionSettings) {
			Session session = HibernateSession.getSessionFactory();
			Transaction tx = session.beginTransaction();
			try {
				session.update(decisionSettings);
				tx.commit();	
			}catch(Exception e){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating DecisionSettings Table"+e);	
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
		public List<String> getDecisionDataByName() {
			Session session = HibernateSession.getSessionFactory();
			Transaction tx = session.beginTransaction();		
			List<String> data = new ArrayList<String>();
			try {
				Criteria cr = session.createCriteria(DecisionSettings.class);
				List<DecisionSettings> decisionSettingsData = cr.list();
				for(int i = 0; i < decisionSettingsData.size(); i++){
					data.add(decisionSettingsData.get(i).getXacmlId());
				}
				tx.commit();
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying DecisionSettings Table"+e);	
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
