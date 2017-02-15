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

package org.openecomp.policy.pap.xacml.rest.controller;

/*
 * 
 * 
 * 
 * */
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openecomp.policy.pap.xacml.rest.HibernateSession;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

public class CheckDictionaryDuplicateEntries {

	private static final Log logger = LogFactory.getLog(CheckDictionaryDuplicateEntries.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> CheckDuplicateEntry(String value, String columnName, Class class1) {
		Session session = HibernateSession.getSessionFactory();
		Transaction tx = session.beginTransaction();
		List<Object> data = null;
		try {
			Criteria cr = session.createCriteria(class1);
			cr.add(Restrictions.eq(columnName,value));
			data = cr.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying for Duplicate Entries for Table"+e + class1);	
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
