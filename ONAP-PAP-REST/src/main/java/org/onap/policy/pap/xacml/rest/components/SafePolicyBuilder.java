/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.components;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.controlloop.policy.guard.ControlLoopGuard;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class SafePolicyBuilder {

    private SafePolicyBuilder(){
        //Private Constructor.
    }

    public static ControlLoopGuard loadYamlGuard(String specification) {
        //
        // Read the yaml into our Java Object
        //
        PolicyLogger.info("Requested YAML to convert : " + specification);
        Yaml yaml = new Yaml(new Constructor(ControlLoopGuard.class));
        Object obj = yaml.load(specification);
        return (ControlLoopGuard) obj;
    }

    public static String generateXacmlGuard(String xacmlFileContent,Map<String, String> generateMap, List<String> blacklist, List<String> targets) {
        //Setup default values and Targets.
        StringBuilder targetRegex= new StringBuilder(".*|");
        if(targets!=null && !targets.isEmpty()){
            targetRegex = new StringBuilder();
            for(String t : targets){
                targetRegex.append(t + "|");
            }
        }
        if(generateMap.get("clname")==null|| generateMap.get("clname").isEmpty()){
            generateMap.put("clname",".*");
        }
        generateMap.put("targets", targetRegex.toString().substring(0, targetRegex.length()-1));
        // Replace values.
        for(Map.Entry<String,String> map: generateMap.entrySet()){
            Pattern p = Pattern.compile("\\$\\{" +map.getKey() +"\\}");
            Matcher m = p.matcher(xacmlFileContent);
            String finalInput = map.getValue();
            if(finalInput.contains("$")){
                finalInput = finalInput.replace("$", "\\$");
            }
            xacmlFileContent=m.replaceAll(finalInput);
        }
        if(blacklist!=null && !blacklist.isEmpty()){
            StringBuilder rule = new StringBuilder();
            for(String blackListName : blacklist){
                if(blackListName.contains("$")){
                    blackListName = blackListName.replace("$", "\\$");
                }
                rule.append("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">"+blackListName+"</AttributeValue>");
            }
            Pattern p = Pattern.compile("\\$\\{blackListElement\\}");
            Matcher m = p.matcher(xacmlFileContent);
            xacmlFileContent=m.replaceAll(rule.toString());
        }
        PolicyLogger.info("Generated XACML from the YAML Spec: \n" + xacmlFileContent);
        return xacmlFileContent;
    }
}
