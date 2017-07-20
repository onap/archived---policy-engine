/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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

package org.onap.policy.pdp.test.custom;

import java.security.PrivateKey;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.datatypes.DataTypeBase;

public class DataTypePrivateKey extends DataTypeBase<PrivateKey> {
	public static final Identifier DT_PRIVATEKEY = new IdentifierImpl("urn:com:att:research:xacml:custom:3.0:rsa:private");
	private static final DataTypePrivateKey singleInstance = new DataTypePrivateKey();
	
	private DataTypePrivateKey() {
		super(DT_PRIVATEKEY, PrivateKey.class);
	}

	public static DataTypePrivateKey newInstance() {
		return singleInstance;
	}
	
	@Override
	public PrivateKey convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof PrivateKey) ) {
			return (PrivateKey) source;
		} else if (source instanceof byte[]) {
			return (PrivateKey) source;
		} else if (source instanceof String) {
			return (PrivateKey) (Object) ((String) source).getBytes();
		}
		throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName());				
	}

}
