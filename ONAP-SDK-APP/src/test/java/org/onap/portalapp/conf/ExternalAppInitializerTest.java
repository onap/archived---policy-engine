package org.onap.portalapp.conf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExternalAppInitializerTest {

	@Test
	public void testExternalAppInitializer(){
		ExternalAppInitializer initializer = new ExternalAppInitializer();
		assertTrue(initializer.getRootConfigClasses()!=null);
		assertTrue(initializer.getServletConfigClasses()!=null);
		assertTrue(initializer.getServletMappings()!=null);
	}
	
	@Test
	public void testExternalAppConfig(){
		ExternalAppConfig appConfig = new ExternalAppConfig();
		assertTrue(appConfig.dataAccessService()!=null);
		assertTrue(appConfig.addTileDefinitions()!=null);
		assertTrue(appConfig.cacheManager()!=null);
		assertTrue(appConfig.loginStrategy()!=null);
		assertTrue(appConfig.viewResolver()!=null);
	}
}
