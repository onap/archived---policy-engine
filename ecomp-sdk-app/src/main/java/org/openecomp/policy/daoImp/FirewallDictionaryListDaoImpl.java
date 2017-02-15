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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openecomp.policy.rest.dao.FirewallDictionaryListDao;
import org.openecomp.policy.rest.jpa.FirewallDictionaryList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Service("FirewallDictionaryListDao")
public class FirewallDictionaryListDaoImpl implements FirewallDictionaryListDao {
	private static final Logger logger = FlexLogger.getLogger(FirewallDictionaryListDaoImpl.class);
	@Autowired
	SessionFactory sessionfactory;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FirewallDictionaryList> getFWDictionaryListData() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		List<FirewallDictionaryList> attributeData =  null;
        try {
        	Criteria cr = session.createCriteria(FirewallDictionaryList.class);
            attributeData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying FirewallDictionaryList Table"+e);	
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
	public List<String> getFWDictionaryListDataByName() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		List<String> data = new ArrayList<String>();
        try {
        	Criteria cr = session.createCriteria(FirewallDictionaryList.class);
            List<FirewallDictionaryList> attributeData = cr.list();    
            for(int i = 0; i < attributeData.size(); i++){
            	 data.add(attributeData.get(i).getParentItemName());
            }
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying FirewallDictionaryList Table"+e);	
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
	public void Save(FirewallDictionaryList firewallDictionaryList) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(firewallDictionaryList);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving FirewallDictionaryList Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void delete(FirewallDictionaryList firewallDictionaryList) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(firewallDictionaryList);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting FirewallDictionaryList Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void update(FirewallDictionaryList firewallDictionaryList) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(firewallDictionaryList);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating FirewallDictionaryList Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public void updateQuery(String query) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();	
		try {
			Query hbquery = session.createQuery(query);
			hbquery.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating FirewallDictionaryList Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

	@Override
	public FirewallDictionaryList getFWDictionaryDataById(String value) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		FirewallDictionaryList data = null;
        try {
        	Criteria cr = session.createCriteria(FirewallDictionaryList.class);
        	cr = cr.add(Restrictions.eq("parentItemName",value));
        	data = (FirewallDictionaryList) cr.list().get(0);
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying FirewallDictionaryList Table"+e);	
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
