/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.util.PolicyValidation;
import org.onap.policy.rest.util.PolicyValidationRequestWrapper;
import org.onap.policy.utils.PolicyUtils;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class PolicyValidationController extends RestrictedBaseController {

    private static final Logger LOGGER = FlexLogger.getLogger(PolicyValidationController.class);

    /**
     * validatePolicy.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     * @throws IOException IOException
     */
    @RequestMapping(
            value = {"/policyController/validate_policy.htm"},
            method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView validatePolicy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            PolicyValidation validation = new PolicyValidation();
            PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
            StringBuilder responseString;
            ObjectMapper mapper = new ObjectMapper();

            PolicyRestAdapter policyData = wrapper.populateRequestParameters(request);
            responseString = validation.validatePolicy(policyData);

            response.getWriter().write(new JSONObject(
                    new JsonMessage(mapper.writeValueAsString(responseString.toString()))).toString());
        } catch (Exception e) {
            LOGGER.error("Exception Occured During Policy Validation" + e);
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.write(PolicyUtils.CATCH_EXCEPTION);
        }
        return null;
    }
}
