package org.openecomp.portalapp;

import org.junit.Assert;
import org.junit.Test;
import org.openecomp.portalsdk.core.MockApplicationContextTestSuite;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class SanityTest extends MockApplicationContextTestSuite {	
	
	@Test
	public void testGetAvailableRoles() throws Exception {
		
		ResultActions ra =getMockMvc().perform(MockMvcRequestBuilders.get("/api/roles"));
		//Assert.assertEquals(UrlAccessRestrictedException.class,ra.andReturn().getResolvedException().getClass());
		Assert.assertEquals("application/json",ra.andReturn().getResponse().getContentType());
	}
	

}
