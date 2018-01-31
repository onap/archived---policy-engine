/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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

package org.onap.policy.xacml.api.pap;

import java.util.Properties;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.FactoryFinder;

public abstract class ONAPPapEngineFactory{

	/**
	 * Creates a new <code>PAPEngineFactory</code> instance using the given class name and the default thread class loader.
	 *
	 * @param factoryClassName the <code>String</code> name of the factory class to instantiate
	 * @return an instance of an object that extends <code>ONAPPapEngineFactory</code> to use in creating <code>PAPPolicyEngine</code> objects.
	 */
	public static ONAPPapEngineFactory newInstance(String factoryClassName) throws FactoryException {
		return FactoryFinder.newInstance(factoryClassName, ONAPPapEngineFactory.class, null, true);
	}

	/**
	 * Creates a new <code>PAPPolicyEngine</code> based on the configured <code>ONAPPapEngineFactory</code>.
	 *
	 * @return a new <code>PAPPolicyEngine</code>
	 * @throws PAPException
	 */
	public abstract PAPPolicyEngine newEngine() throws FactoryException, PAPException;

	/**
	 * Creates a new <code>PAPPolicyEngine</code> based on the configured <code>ONAPPapEngineFactory</code>.
	 *
	 * @return a new <code>PAPPolicyEngine</code>
	 * @throws PAPException
	 */
	public abstract PAPPolicyEngine newEngine(Properties properties) throws FactoryException, PAPException;


}
