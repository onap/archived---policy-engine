package org.openecomp.portalapp.controller;

import org.junit.Assert;
import org.junit.Test;
import org.openecomp.portalsdk.core.MockApplicationContextTestSuite;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class NetMapTest extends MockApplicationContextTestSuite {
	
	@Test
	public void testGetNetMap() throws Exception {
		ResultActions ra =getMockMvc().perform(MockMvcRequestBuilders.get("/net_map"));
		//Assert.assertEquals(UrlAccessRestrictedException.class,ra.andReturn().getResolvedException().getClass());
		Assert.assertEquals("net_map_int",ra.andReturn().getModelAndView().getModel().get("frame_int"));
	}
	

}
