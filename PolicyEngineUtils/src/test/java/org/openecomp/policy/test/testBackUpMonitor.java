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

package org.openecomp.policy.test;

import static org.junit.Assert.assertNull;
import java.util.Properties;

import org.junit.Test;
import org.openecomp.policy.utils.BackUpMonitor;


public class testBackUpMonitor {

	@Test
	public void backUpMonitorTestFail() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/xacml");
		properties.setProperty("javax.persistence.jdbc.user", "policy_user");
		properties.setProperty("javax.persistence.jdbc.password", "");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoUser() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/xacml");
		properties.setProperty("javax.persistence.jdbc.user", "");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoURL() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoDriver() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/xacml");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoNode() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/xacml");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(null, "brms_test" , properties, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoResource() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/xacml");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), null , properties, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoProperties() throws Exception{
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , null, new Handler());
		assertNull(bum);
	}
	
	@Test
	public void backUpMonitorTestFailNoHandler() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/xacml");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		//properties.setProperty("ping_interval", "500000");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, null);
		assertNull(bum);
	}
}
