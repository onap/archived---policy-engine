/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
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
 * ================================================================================
 */
package org.openecomp.portalapp.conf;

import java.util.Arrays;

import org.openecomp.portalsdk.core.conf.AppInitializer;

public class ExternalAppInitializer extends  AppInitializer{
	
	
	@Override
    protected Class<?>[] getRootConfigClasses() {
    	return super.getRootConfigClasses();
    }
  
    @Override
    protected Class<?>[] getServletConfigClasses() {
//    	Class<?>[] configClasses =  super.getServletConfigClasses();
//    	Class<?>[] additionalConfigClasses  = Arrays.copyOf(configClasses, configClasses.length);
//    	addConfigClass(additionalConfigClasses, ExternalAppConfig.class);
//    	return additionalConfigClasses;
//    	
    	return new Class[] {ExternalAppConfig.class};
    }
    
    static Class<?>[] addConfigClass(Class<?>[] a, Class<?> e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
  
    /*
     * URL request will direct to the Spring dispatcher for processing
     */
    @Override
    protected String[] getServletMappings() {
       return super.getServletMappings();
    }
 
}


