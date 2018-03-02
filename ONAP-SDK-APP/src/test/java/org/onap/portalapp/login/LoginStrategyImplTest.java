/*-
 * ================================================================================
 * ONAP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
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
 * ================================================================================
 */

package org.onap.portalapp.login;

import static org.junit.Assert.assertNull;
import javax.servlet.http.Cookie;
import org.junit.Test;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.springframework.mock.web.MockHttpServletRequest;

public class LoginStrategyImplTest {
	@Test
	public void testLoginStrategyImpl() throws PortalAPIException {
		LoginStrategyImpl impl = new LoginStrategyImpl();
		MockHttpServletRequest request = new MockHttpServletRequest();
		Cookie cookie1 = new Cookie("EPService", "serviceName");
		Cookie cookie2 = new Cookie("UserId", "userName");
		request.setCookies(cookie1, cookie2);
		assertNull(impl.getUserId(request));
	}
}
