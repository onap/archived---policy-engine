/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.portalapp.conf;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.net.MalformedURLException;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.portalapp.scheduler.RegistryAdapter;
import org.onap.portalsdk.core.scheduler.Registerable;
import org.onap.portalsdk.workflow.services.WorkflowScheduleService;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.UrlResource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

public class ExternalAppConfigTest {
  @Test
  public void testConfig() throws MalformedURLException {
    // Setup test data
    ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
    UrlResource value = new UrlResource("http://localhost");
    Mockito.when(ctx.getResource(Mockito.any())).thenReturn(value);
    ResourceHandlerRegistry registry = new ResourceHandlerRegistry(ctx, null);
    InterceptorRegistry interceptor = new InterceptorRegistry();
    RegistryAdapter schedulerRegistryAdapter = new RegistryAdapter();
    Registerable reg = Mockito.mock(Registerable.class);
    Mockito.when(reg.getTriggers()).thenReturn(new Trigger[1]);
    schedulerRegistryAdapter.setRegistry(reg);
    WorkflowScheduleService workflowScheduleService = Mockito.mock(WorkflowScheduleService.class);
    schedulerRegistryAdapter.setWorkflowScheduleService(workflowScheduleService);
    Mockito.when(workflowScheduleService.triggerWorkflowScheduling())
        .thenReturn(Collections.emptyList());

    // Test constructor
    ExternalAppConfig config = new ExternalAppConfig();
    assertNotNull(config);

    // Test set and get
    config.setApplicationContext(ctx);
    assertNotNull(config.viewResolver());
    config.addResourceHandlers(registry);
    assertNotNull(config.dataAccessService());
    assertNotNull(config.addTileDefinitions());
    config.addInterceptors(interceptor);
    assertNotNull(config.cacheManager());
    config.setSchedulerRegistryAdapter(schedulerRegistryAdapter);
    assertNull(config.schedulerFactoryBean());
    assertNotNull(config.loginStrategy());
  }
}
