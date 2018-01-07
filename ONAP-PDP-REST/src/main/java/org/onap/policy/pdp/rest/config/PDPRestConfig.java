/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.controller.PolicyEngineServices;
import org.onap.policy.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc
@EnableSwagger2
@ComponentScan(basePackages = { "org.onap.*", "com.*" })
public class PDPRestConfig extends WebMvcConfigurerAdapter{
	
	private static final Logger LOGGER	= FlexLogger.getLogger(PDPRestConfig.class);

	private static String dbDriver = null;
	private static String dbUrl = null;
	private static String dbUserName = null;
	private static String dbPassword = null;
	
	@PostConstruct
	public void init(){
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream("xacml.pdp.properties")){
			// load a properties file
			prop.load(input);
			setDbDriver(prop.getProperty("javax.persistence.jdbc.driver"));
			setDbUrl(prop.getProperty("javax.persistence.jdbc.url"));
			setDbUserName(prop.getProperty("javax.persistence.jdbc.user"));
			setDbPassword(CryptoUtils.decryptTxtNoExStr(prop.getProperty("javax.persistence.jdbc.password", "")));
		}catch(Exception e){
			LOGGER.error("Exception Occured while loading properties file"+e);
		}
	}
	
	@Override 
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Policy Engine REST API")
                .description("This API helps to make queries against Policy Engine")
                .version("3.0")
                .build();
    }
    
    @Bean
    public Docket policyAPI(){
        PolicyLogger.info("Setting up Swagger... ");
        return new Docket(DocumentationType.SWAGGER_2)                
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.onap.policy.pdp.rest.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }
    
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName(PDPRestConfig.getDbDriver());
	    dataSource.setUrl(PDPRestConfig.getDbUrl());
	    dataSource.setUsername(PDPRestConfig.getDbUserName());
	    dataSource.setPassword(PDPRestConfig.getDbPassword());
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
    
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        String location = System.getProperty("java.io.tmpdir");
        MultipartConfigElement mp = new MultipartConfigElement(location);
        return mp;
    }

	public static String getDbDriver() {
		return dbDriver;
	}

	public static void setDbDriver(String dbDriver) {
		PDPRestConfig.dbDriver = dbDriver;
	}

	public static String getDbUrl() {
		return dbUrl;
	}

	public static void setDbUrl(String dbUrl) {
		PDPRestConfig.dbUrl = dbUrl;
	}

	public static String getDbUserName() {
		return dbUserName;
	}

	public static void setDbUserName(String dbUserName) {
		PDPRestConfig.dbUserName = dbUserName;
	}

	public static String getDbPassword() {
		return dbPassword;
	}

	public static void setDbPassword(String dbPassword) {
		PDPRestConfig.dbPassword = dbPassword;
	}
}
