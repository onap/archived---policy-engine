/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = { "org.onap.*", "com.*" })
public class PAPRestConfig extends WebMvcConfigurerAdapter {
    private static final Logger LOGGER	= FlexLogger.getLogger(PAPRestConfig.class);

    private static String dbDriver = null;
    private static String dbUrl = null;
    private static String dbUserName = null;
    private static String dbPassword = null;

    @PostConstruct
    public void init(){
        Properties prop = new Properties();
        try(InputStream input = new FileInputStream("xacml.pap.properties")) {
            // load a properties file
            prop.load(input);
            setDbDriver(prop.getProperty("javax.persistence.jdbc.driver"));
            setDbUrl(prop.getProperty("javax.persistence.jdbc.url"));
            setDbUserName(prop.getProperty("javax.persistence.jdbc.user"));
            setDbPassword( CryptoUtils.decryptTxtNoExStr(prop.getProperty("javax.persistence.jdbc.password", "")));
        }catch(Exception e){
            LOGGER.error("Exception Occured while loading properties file"+e);
        }
    }

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(PAPRestConfig.getDbDriver());
        dataSource.setUrl(PAPRestConfig.getDbUrl());
        dataSource.setUsername(PAPRestConfig.getDbUserName());
        dataSource.setPassword(PAPRestConfig.getDbPassword());
        return dataSource;
    }

    @Autowired
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.scanPackages("org.onap.*", "com.*");
        sessionBuilder.addProperties(getHibernateProperties());
        return sessionBuilder.buildSessionFactory();
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return properties;
    }

    @Autowired
    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    public static String getDbDriver() {
        return dbDriver;
    }

    public static void setDbDriver(String dbDriver) {
        PAPRestConfig.dbDriver = dbDriver;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static void setDbUrl(String dbUrl) {
        PAPRestConfig.dbUrl = dbUrl;
    }

    public static String getDbUserName() {
        return dbUserName;
    }

    public static void setDbUserName(String dbUserName) {
        PAPRestConfig.dbUserName = dbUserName;
    }

    public static String getDbPassword() {
        return dbPassword;
    }

    public static void setDbPassword(String dbPassword) {
        PAPRestConfig.dbPassword = CryptoUtils.decryptTxtNoExStr(dbPassword);
    }

}
