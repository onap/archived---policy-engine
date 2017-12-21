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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.att.cadi.Access;

/**
 * PolicyAccess used by AAF for logging purposes. 
 *  
 */
public class PolicyAccess implements Access {
	private static final Logger logger = Logger.getLogger(PolicyAccess.class.getName());
	
	private Properties properties = new Properties(); 
	private Access.Level logLevel = Access.Level.INFO;
	
	public PolicyAccess(Properties properties, Level level) {
		this.properties = properties;
		if(level!=null){
			logLevel = level;
		}
	}
	
	@Override
	public ClassLoader classLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public String decrypt(String enc, boolean arg1) throws IOException {
		return enc;
	}

	@Override
	public String getProperty(String prop, String def) {
		return properties.getProperty(prop, def);
	}

	@Override
	public void load(InputStream in) throws IOException {
		properties.load(in);
	}

	@Override
	public void log(Level level, Object... args) {
		if (logLevel.compareTo(level) > 0) {
            return;
        }
		StringBuilder sb = new StringBuilder();
        sb.append(new Date()).append(' ').append(level);
        logtail(sb, args);
	}

	@Override
	public void log(Exception e, Object... args) {
		StringBuilder sb = new StringBuilder();
        sb.append(new Date()).append(" EXCEPTION ").append(e.getMessage());
        logtail(sb, args);
        logger.error(e.getMessage() + e);
	}

	@Override
	public void setLogLevel(Level level) {
		logLevel = level;
	}
	
	private void logtail(StringBuilder sb, Object[] args) {
        for (Object o: args) {
            String s = o.toString();
            if (s.length() > 0) {
                sb.append(' ').append(s);
            }
        }
        logger.info(sb.toString());
    }

	@Override
	public boolean willLog(Level arg0) {
		return false;
	}
}
