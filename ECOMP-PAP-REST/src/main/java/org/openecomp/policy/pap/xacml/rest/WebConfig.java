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

package org.openecomp.policy.pap.xacml.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;


public class WebConfig implements WebApplicationInitializer {
	
	private static final Log logger	= LogFactory.getLog(WebConfig.class);
	
	@Override
	public void onStartup(ServletContext container) {
		
		//need to get properties for userid and password on the pap to get authorization string used in URI Mapping
		Properties prop = new Properties();
		String propFileName = "xacml.pap.properties";
		
		try {
			InputStream is = new FileInputStream(propFileName);
			prop.load(is);
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "property file '" + propFileName + "' not found in the classpath");
		}
		
		String papID = prop.getProperty("xacml.rest.pap.userid");
		String papPass = prop.getProperty("xacml.rest.pap.password");
		
		String usernameAndPassword = papID+":"+papPass;		
		String authorizationString = Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
		
		
		XmlWebApplicationContext appContext = new XmlWebApplicationContext();
		appContext.setConfigLocation("classpath:spring.xml");
		
		System.out.println("Spring XML File Location: " + appContext.getConfigLocations());
		logger.info("Spring XML File Location: " + appContext.getConfigLocations());

		ServletRegistration.Dynamic dispatcher =
				container.addServlet("dispatcher", new DispatcherServlet(appContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/@Auth@"+authorizationString+"/ecomp/*");
	}


}
