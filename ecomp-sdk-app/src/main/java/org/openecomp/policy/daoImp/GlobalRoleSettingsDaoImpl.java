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
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openecomp.policy.dao.GlobalRoleSettingsDao;
import org.openecomp.policy.rest.jpa.GlobalRoleSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;


@Service("GlobalRoleSettingsDao")
public class GlobalRoleSettingsDaoImpl implements GlobalRoleSettingsDao{
	private static final Logger logger = FlexLogger.getLogger(GlobalRoleSettingsDaoImpl.class);
	@Autowired
	SessionFactory sessionfactory;
	
	

	@SuppressWarnings("unchecked")
	@Override
	public List<GlobalRoleSettings> getGlobalRoleSettings() {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
        List<GlobalRoleSettings> lockdownData = null;
        try {
        	Criteria cr = session.createCriteria(GlobalRoleSettings.class);
            lockdownData = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying GlobalRoleSettings Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return lockdownData;
	}



	@Override
	public void update(GlobalRoleSettings globalRoleSettings) {
		Session session = sessionfactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			session.update(globalRoleSettings);
			tx.commit();	
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating GlobalRoleSettings Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}		
	}

}
