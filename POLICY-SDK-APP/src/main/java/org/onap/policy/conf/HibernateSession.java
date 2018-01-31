/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.conf;

/*
 *
 *
 * */
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.jpa.SystemLogDB;

@SuppressWarnings("deprecation")
public class HibernateSession{

	private static final Logger LOGGER	= FlexLogger.getLogger(HibernateSession.class);

	private static SessionFactory logSessionFactory;

	static {
		try {
			Properties prop= new Properties();
			prop.setProperty("hibernate.connection.url", PolicyController.getLogdbUrl());
			prop.setProperty("hibernate.connection.username", PolicyController.getLogdbUserName());
			prop.setProperty("hibernate.connection.password", PolicyController.getLogdbPassword());
			prop.setProperty("dialect", PolicyController.getLogdbDialect());
			prop.setProperty("hibernate.connection.driver_class", PolicyController.getLogdbDriver());
			prop.setProperty("show_sql", "false");
			logSessionFactory = new Configuration().addPackage("org.onap.policy.*").addProperties(prop)
				   .addAnnotatedClass(SystemLogDB.class).buildSessionFactory();
		} catch (Exception ex) {
			LOGGER.error("Exception Occured while creating Log database Hibernate session"+ex);
		}
	}

	private HibernateSession(){
          /**
           empty implementation
          */
	}

	public static Session getSession(){
		return logSessionFactory.openSession();
	}

	public static void setSession(SessionFactory logSessionFactory1){
		logSessionFactory = logSessionFactory1;
	}


}
