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

import java.security.PublicKey;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.datatypes.DataTypeBase;

public class DataTypePublicKey extends DataTypeBase<PublicKey> {
	public static final Identifier DT_PUBLICKEY = new IdentifierImpl("urn:com:att:research:xacml:custom:3.0:rsa:public");
	private static final DataTypePublicKey singleInstance = new DataTypePublicKey();
	
	public DataTypePublicKey() {
		super(DT_PUBLICKEY, PublicKey.class);
	}
	
	public static DataTypePublicKey newInstance() {
		return singleInstance;
	}

	@Override
	public PublicKey convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof PublicKey) ) {
			return (PublicKey) source;
		} else if (source instanceof byte[]) {
			return (PublicKey) source;
		} else if (source instanceof String) {
			return (PublicKey) (Object) ((String) source).getBytes();
		}
		throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName());				
	}

}
