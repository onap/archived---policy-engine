/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Bell Canada
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

package org.onap.policy.admin;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class PolicyUserInfoController extends RestrictedBaseController {

    private static final Logger LOGGER = FlexLogger.getLogger(PolicyUserInfoController.class);

    /**
     * getPolicyUserInfo - fills the HTTP response with user information.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/get_PolicyUserInfo", method = RequestMethod.GET)
    public void getPolicyUserInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = UserUtils.getUserSession(request).getOrgUserId();
            Map<String, Object> model = new HashMap<>();
            model.put("userid", userId);
            response.getWriter().write(new JSONObject(new JsonMessage(
                    new ObjectMapper().writeValueAsString(model))).toString());
        } catch (Exception e) {
            LOGGER.error("Exception Occurred" + e);
        }
    }
}
