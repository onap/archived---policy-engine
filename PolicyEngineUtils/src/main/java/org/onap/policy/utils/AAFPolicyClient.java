/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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
package org.onap.policy.utils;

import java.lang.reflect.Method;
import java.util.Properties;

public interface AAFPolicyClient {

	public boolean checkAuth(String userName, String pass);
	public void updateProperties(Properties properties) throws AAFPolicyException;
	public boolean checkAuthPerm(String mechID, String pass, String type, String instance, String action);
	public boolean checkPerm(String userName, String pass, String type, String instance, String action);
	public static AAFPolicyClient getInstance(Properties properties) throws AAFPolicyException{
		try {
			Class<?> aafPolicyClient = Class.forName(properties.getProperty("aafClient.impl.className", AAFPolicyClientImpl.class.getName()));
			Method method =  aafPolicyClient.getMethod("getInstance", Properties.class);
			return (AAFPolicyClient) method.invoke(null, properties);
		} catch (Exception e) {
			throw new AAFPolicyException(e);
		}
	}
}
