/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.onap.policy.rest.util.MSModelUtils.MODEL_TYPE;

public class MSModelUtilsTest {
	
	@Test
	public void testMSModelUtils(){
		HashMap<String, MSAttributeObject> classMap = new HashMap<>();
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("DKaTVESPolicy-v1802.xmi").getFile());
		MSModelUtils utils = new MSModelUtils("http://org.onap", "http://org.onap.policy");
		Map<String, MSAttributeObject> tempMap = utils.processEpackage(file.getAbsolutePath().toString(), MODEL_TYPE.XMI);
		classMap.putAll(tempMap);
		MSAttributeObject mainClass = classMap.get("StandardDeviationThreshold");
		String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
		List<String> dependency = new ArrayList<String>(Arrays.asList(dependTemp.split(",")));
		dependency = utils.getFullDependencyList(dependency, classMap);
		String subAttribute = utils.createSubAttributes(dependency, classMap, "StandardDeviationThreshold");
		assertTrue(subAttribute != null);
	}
}
