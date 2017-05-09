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
package org.openecomp.policy.pap.xacml.rest.components;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.controlloop.policy.guard.ControlLoopGuard;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class SafePolicyBuilder {

	public static ControlLoopGuard loadYamlGuard(String specification) {
		//
		// Read the yaml into our Java Object
		//
		PolicyLogger.info("Requested YAML to convert : " + specification);
		Yaml yaml = new Yaml(new Constructor(ControlLoopGuard.class));
		Object obj = yaml.load(specification);
		return (ControlLoopGuard) obj;
	}
	
	public static String	generateXacmlGuard(String xacmlFileContent,Map<String, String> generateMap) {
		for(String key: generateMap.keySet()){
			Pattern p = Pattern.compile("\\$\\{" +key +"\\}");
			Matcher m = p.matcher(xacmlFileContent);
			String finalInput = generateMap.get(key);
			if(finalInput.contains("$")){
				finalInput = finalInput.replace("$", "\\$");
			}
			xacmlFileContent=m.replaceAll(finalInput);
		}
		PolicyLogger.info("Generated XACML from the YAML Spec: \n" + xacmlFileContent);

		return xacmlFileContent;
	}
}
