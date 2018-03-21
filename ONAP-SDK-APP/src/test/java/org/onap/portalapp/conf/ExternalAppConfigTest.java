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
