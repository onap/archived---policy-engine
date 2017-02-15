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
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openecomp.policy.conf.HibernateSession;
import org.openecomp.policy.controller.PolicyController;
import org.openecomp.policy.dao.SystemLogDbDao;
import org.openecomp.policy.rest.jpa.SystemLogDB;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


@Service("SystemLogDbDao")
public class SystemLogDbDaoImpl implements SystemLogDbDao {
	private static final Logger logger = FlexLogger.getLogger(SystemLogDbDaoImpl.class);
	@SuppressWarnings("unchecked")
	@Override
	public List<SystemLogDB> getLoggingData() {
		Session session = HibernateSession.getSession();
		Transaction tx = session.beginTransaction();
		List<SystemLogDB> system = null;
        try {
        	String sqlWhere = "date > DATE_SUB(curdate(), INTERVAL 5 DAY) ORDER BY date DESC limit "+PolicyController.logTableLimit+"";
        	Criteria cr = session.createCriteria(SystemLogDB.class);
        	cr.add(Restrictions.sqlRestriction(sqlWhere));
            system = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying SystemLogDB Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return system;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SystemLogDB> getSystemAlertData() {
		Session session = HibernateSession.getSession();;
		Transaction tx = session.beginTransaction();
		List<SystemLogDB> system = null;
        try {
        	String sqlWhere = "date > DATE_SUB(curdate(), INTERVAL 5 DAY) and logtype = 'error' ORDER BY date DESC limit "+PolicyController.systemAlertTableLimit+"";
        	Criteria cr = session.createCriteria(SystemLogDB.class);
        	cr.add(Restrictions.sqlRestriction(sqlWhere));
            system = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying SystemLogDB Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return system;
	}

}
