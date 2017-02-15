/*-
 * ================================================================================
 * eCOMP Portal SDK
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
package org.openecomp.portalapp.controller;

import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for geographic map view. The view is restricted to authenticated
 * users. The view name defaults to the request name with no suffix,
 * "leafletMap", which resolves to page leafletMap.jsp.
 */
@Controller
@RequestMapping("/")
public class LeafletMapContoller extends RestrictedBaseController {

	@RequestMapping(value = { "/leafletMap" }, method = RequestMethod.GET)
	public ModelAndView plot() {
		final String defaultViewName = null;
		return new ModelAndView(defaultViewName);
	}

}
