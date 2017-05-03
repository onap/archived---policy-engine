/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
package org.openecomp.policy.pap.xacml.rest.elk.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class PolicySearchController {
	
	private static final Logger LOGGER = FlexLogger.getLogger(PolicySearchController.class);

	@RequestMapping(value="/searchPolicy", method= RequestMethod.POST)
	public void elkTransaction(HttpServletRequest request, HttpServletResponse response) {
		try{
			boolean result = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			PolicyRestAdapter policyData = new PolicyRestAdapter();
			if(request.getParameter("policyName") != null){
				String policyName = request.getParameter("policyName");
				policyData.setNewFileName(policyName);
				PolicyElasticSearchController controller = new PolicyElasticSearchController();
				if("delete".equalsIgnoreCase(request.getParameter("action"))){
					result = controller.deleteElk(policyData);
				}else{
					result = controller.updateElk(policyData);
				}
			}
			String message="";
			if(result){
				message = "Elastic Server Transaction is success";
			}else{
				message = "Elastic Server Transaction is failed, please check the logs";
			}
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(message));
			JSONObject j = new JSONObject(msg);
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("success", "success"); 
			response.getWriter().write(j.toString());
		}catch(Exception e){
			 response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			 response.addHeader("error", "Exception Occured While Performing Elastic Transaction");
			LOGGER.error("Exception Occured While Performing Elastic Transaction"+e.getMessage());
			e.printStackTrace();
		}
	}
	
}
