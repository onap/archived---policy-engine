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

import javax.servlet.MultipartConfigElement;

import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.pdp.rest.api.controller.PolicyEngineServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
@ComponentScan(basePackageClasses = PolicyEngineServices.class)
public class PDPRestConfig extends WebMvcConfigurerAdapter{
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
    
    @Bean
    public MultipartConfigElement multipartConfigElement(){
        String location = System.getProperty("java.io.tmpdir");
        MultipartConfigElement mp = new MultipartConfigElement(location);
        return mp;
    }
}
