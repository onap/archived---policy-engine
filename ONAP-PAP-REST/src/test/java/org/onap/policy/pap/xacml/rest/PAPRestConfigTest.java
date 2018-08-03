/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest;

import static org.junit.Assert.assertEquals;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.onap.policy.pap.xacml.rest.PAPRestConfig;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

public class PAPRestConfigTest {
    @Test
    public void testSetAndGet() {
        String testVal = "testVal";

        PAPRestConfig.setDbDriver(testVal);
        assertEquals(PAPRestConfig.getDbDriver(), testVal);
        PAPRestConfig.setDbUrl(testVal);
        assertEquals(PAPRestConfig.getDbUrl(), testVal);
        PAPRestConfig.setDbUserName(testVal);
        assertEquals(PAPRestConfig.getDbUserName(), testVal);
        PAPRestConfig.setDbPassword(testVal);
        assertEquals(PAPRestConfig.getDbPassword(), testVal);
    }

    @Test
    public void testInit() {
        String driver = "org.mariadb.jdbc.Driver";
        String url = "jdbc:mariadb://localhost:3306/onap_sdk?connectTimeout=30000&socketTimeout=60000&log=true&sessionVariables=max_statement_time=30";
        String user = "policy_user";
        String password = "policy_user";

        PAPRestConfig config = new PAPRestConfig();
        config.init();

        assertEquals(PAPRestConfig.getDbDriver(), driver);
        assertEquals(PAPRestConfig.getDbUrl(), url);
        assertEquals(PAPRestConfig.getDbUserName(), user);
        assertEquals(PAPRestConfig.getDbPassword(), password);

        // Test hibernate
        BasicDataSource source = (BasicDataSource)config.getDataSource();
        assertEquals(source.getDriverClassName(), driver);
        assertEquals(source.getUrl(), url);
        assertEquals(source.getUsername(), user);
        assertEquals(source.getPassword(), password);

        SessionFactory factory = config.getSessionFactory(source);
        assertEquals(factory.isClosed(), false);

        HibernateTransactionManager manager = config.getTransactionManager(factory);
        assertEquals(manager.getSessionFactory(), factory);
    }
}
