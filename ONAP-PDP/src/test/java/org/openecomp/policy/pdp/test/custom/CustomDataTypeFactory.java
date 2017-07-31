/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP
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

package org.openecomp.policy.pdp.test.custom;

import java.util.HashMap;
import java.util.Map;

import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.DataTypeFactory;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.datatypes.DataTypes;

public class CustomDataTypeFactory extends DataTypeFactory {
	private static final Map<Identifier,DataType<?>> mapIdentifiersToDataTypes	= new HashMap<>();
	private static boolean mapNeedsInit												= true;
	
	public static final DataTypePrivateKey				DT_PRIVATEKEY				= DataTypePrivateKey.newInstance();
	public static final DataTypePublicKey				DT_PUBLICKEY				= DataTypePublicKey.newInstance();
	
	private static void registerDataType(DataType<?> dataType) {
		if (dataType != null && dataType.getId() != null) {
			mapIdentifiersToDataTypes.put(dataType.getId(), dataType);
		}
	}
	
	private static void initMap() {
		if (mapNeedsInit) {
			synchronized(mapIdentifiersToDataTypes) {
				if (mapNeedsInit) {
					registerDataType(DataTypes.DT_ANYURI);
					registerDataType(DataTypes.DT_BASE64BINARY);
					registerDataType(DataTypes.DT_BOOLEAN);
					registerDataType(DataTypes.DT_DATE);
					registerDataType(DataTypes.DT_DATETIME);
					registerDataType(DataTypes.DT_DAYTIMEDURATION);
					registerDataType(DataTypes.DT_DNSNAME);
					registerDataType(DataTypes.DT_DOUBLE);
					registerDataType(DataTypes.DT_HEXBINARY);
					registerDataType(DataTypes.DT_INTEGER);
					registerDataType(DataTypes.DT_IPADDRESS);
					registerDataType(DataTypes.DT_RFC822NAME);
					registerDataType(DataTypes.DT_STRING);
					registerDataType(DataTypes.DT_TIME);
					registerDataType(DataTypes.DT_X500NAME);
					registerDataType(DataTypes.DT_XPATHEXPRESSION);
					registerDataType(DataTypes.DT_YEARMONTHDURATION);
					//
					// These are the custom data types!
					//
					registerDataType(DT_PRIVATEKEY);
					registerDataType(DT_PUBLICKEY);
					//
					// Done
					//
					mapNeedsInit	= false;
				}
			}
		}
	}

	public CustomDataTypeFactory() {
		initMap();
	}

	@Override
	public DataType<?> getDataType(Identifier dataTypeId) {
		return mapIdentifiersToDataTypes.get(dataTypeId);
	}

}
