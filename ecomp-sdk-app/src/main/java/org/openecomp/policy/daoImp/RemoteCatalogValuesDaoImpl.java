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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openecomp.policy.dao.RemoteCatalogValuesDao;
import org.openecomp.policy.rest.jpa.RemoteCatalogValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Service("RemoteCatalogValuesDao")
public class RemoteCatalogValuesDaoImpl implements RemoteCatalogValuesDao {
	private static final Logger logger = FlexLogger.getLogger(RemoteCatalogValuesDaoImpl.class);
	@Autowired
	SessionFactory sessionfactory;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RemoteCatalogValues> getRemoteCatalogValuesData() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RemoteCatalogValues> attributeData = null;
        try {
        	Criteria cr = session.createCriteria(RemoteCatalogValues.class);
            attributeData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying RemoteCatalogValues Table"+e);	
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
	public List<String> getRemoteCatalogValuesDataByName() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		List<String> data = new ArrayList<String>();
        try {
        	Criteria cr = session.createCriteria(RemoteCatalogValues.class);
            List<RemoteCatalogValues> attributeData = cr.list();    
            for(int i = 0; i < attributeData.size(); i++){
            	 data.add(attributeData.get(i).getName());
            }
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying RemoteCatalogValues Table"+e);	
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
	public void Save(RemoteCatalogValues attribute) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.persist(attribute);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving RemoteCatalogValues Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
	}

	@Override
	public void delete(RemoteCatalogValues attribute) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(attribute);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting RemoteCatalogValues Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
	}

	@Override
	public void update(RemoteCatalogValues attribute) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(attribute);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating RemoteCatalogValues Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}

}
