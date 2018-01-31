/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.rest.XACMLRestProperties;

import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;

public class Webapps {
	private static String actionHome = null;
	private static String configHome = null;
	private static Log logger	= LogFactory.getLog(Webapps.class);

	private Webapps() {
	}

	public static String getConfigHome(){
		try {
			loadWebapps();
		} catch (PAPException e) {
			logger.error("Exception Occured while loading webapps",e);
			return null;
		}
		return configHome;
	}

	public static String getActionHome(){
		try {
			loadWebapps();
		} catch (PAPException e) {
			logger.error("Exception Occured while loading webapps",e);
			return null;
		}
		return actionHome;
	}

	private static void loadWebapps() throws PAPException{
		String errorMessageName = "Invalid Webapps Path Location property :";
		if(actionHome == null || configHome == null){
			Path webappsPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS));
			//Sanity Check
			if (webappsPath == null) {
				logger.error(errorMessageName + XACMLRestProperties.PROP_PAP_WEBAPPS);
				PolicyLogger.error(errorMessageName + XACMLRestProperties.PROP_PAP_WEBAPPS);
				throw new PAPException(errorMessageName + XACMLRestProperties.PROP_PAP_WEBAPPS);
			}
			Path webappsPathConfig;
			Path webappsPathAction;
			if(webappsPath.toString().contains("\\")){
				webappsPathConfig = Paths.get(webappsPath.toString()+"\\Config");
				webappsPathAction = Paths.get(webappsPath.toString()+"\\Action");
			}else{
				webappsPathConfig = Paths.get(webappsPath.toString()+"/Config");
				webappsPathAction = Paths.get(webappsPath.toString()+"/Action");
			}

			checkConfigActionHomeExists(webappsPathConfig, webappsPathAction);

			actionHome = webappsPathAction.toString();
			configHome = webappsPathConfig.toString();
		}
	}

	private  static void checkConfigActionHomeExists(Path webappsPathConfig, Path webappsPathAction){
		if (!webappsPathConfig.toFile().exists()){
			try {
				Files.createDirectories(webappsPathConfig);
			} catch (IOException e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to create config directory: "
						+ webappsPathConfig.toAbsolutePath().toString(), e);
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "Webapps", "Failed to create config directory");
			}
		}

		if (!webappsPathAction.toFile().exists()){
			try {
				Files.createDirectories(webappsPathAction);
			} catch (IOException e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to create config directory: "
						+ webappsPathAction.toAbsolutePath().toString(), e);
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "Webapps", "Failed to create config directory");
			}
		}
	}

}
