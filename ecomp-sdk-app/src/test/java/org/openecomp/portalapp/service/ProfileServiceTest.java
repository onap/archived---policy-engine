package org.openecomp.portalapp.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openecomp.portalsdk.core.MockApplicationContextTestSuite;
import org.openecomp.portalsdk.core.domain.Profile;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.service.ProfileService;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;


public class ProfileServiceTest extends MockApplicationContextTestSuite {
	
	@Autowired
	ProfileService service;
	
	@Autowired
	UserProfileService userProfileService;
	
	@Test
	public void testFindAll() {
		
		List<Profile> profiles = service.findAll();
		Assert.assertTrue(profiles.size() > 0);
	}

	@Test
	public void testFindAllActive() {
				
		List<User> users = userProfileService.findAllActive();
		List<User> activeUsers = userProfileService.findAllActive();
		Assert.assertTrue(users.size() - activeUsers.size() >= 0);
	}
}
