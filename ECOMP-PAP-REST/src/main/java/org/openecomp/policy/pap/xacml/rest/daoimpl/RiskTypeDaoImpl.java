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
import org.openecomp.policy.rest.dao.EcompNameDao;
import org.openecomp.policy.rest.dao.RiskTypeDao;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.RiskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

@Service("RiskTypeDao")
public class RiskTypeDaoImpl implements RiskTypeDao {
	private static final Log logger = LogFactory.getLog(RiskTypeDaoImpl.class);
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
	public List<RiskType> getRiskName() {
		System.out.println("RiskTypeDaoImpl:  getRiskName() is called");
		logger.debug("RiskTypeDaoImpl:  getRiskName() is called");
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();	
        List<RiskType> riskTypeData = null;
        try {
        	Criteria cr = session.createCriteria(RiskType.class);
        	riskTypeData = cr.list();
			logger.debug("Data returned from RiskType table:  " + riskTypeData.toString());
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying RiskType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return riskTypeData;
	}

	@Override
	public void Save(RiskType riskName) {
		System.out.println("RiskTypeDaoImpl:  Save() is called");
		logger.debug("RiskTypeDaoImpl:  Save() is called");
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(riskName);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving RiskType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void delete(RiskType riskName) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(riskName);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting RiskType Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void update(RiskType riskName) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		try {
			session.update(riskName);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating RiskType Table"+e);	
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
	public List<String> getRiskTypeDataByName() {
		logger.info("getRiskTypeDataByName is call from the DAO implementation class.");
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();	
        List<String> data = new ArrayList<String>();    
        try {
        	Criteria cr = session.createCriteria(RiskType.class);
            List<RiskType> riskTypeData = cr.list();
            for(int i = 0; i < riskTypeData.size(); i++){
           	 data.add(riskTypeData.get(i).getRiskName());
           }
            logger.info("data retrieved: " + data.toString());
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying RiskType Table"+e);	
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
