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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.DescriptiveScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;

import io.searchbox.client.JestResult;

@Controller
@RequestMapping("/")
public class PolicySearchController {
	
	private static final Logger LOGGER = FlexLogger.getLogger(PolicySearchController.class);

	@Autowired
	CommonClassDao commonClassDao;
	
	@RequestMapping(value="/searchPolicy", method= RequestMethod.POST)
	public void elkTransaction(HttpServletRequest request, HttpServletResponse response) {
		try{
			boolean result = false;
			boolean policyResult = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			PolicyRestAdapter policyData = new PolicyRestAdapter();
			PolicyElasticSearchController controller = new PolicyElasticSearchController();
			Map<String, String> searchKeyValue = new HashMap<String, String>();
			List<String> policyList = new ArrayList<String>();
			if(request.getParameter("policyName") != null){
				String policyName = request.getParameter("policyName");
				policyData.setNewFileName(policyName);
				if("delete".equalsIgnoreCase(request.getParameter("action"))){
					result = controller.deleteElk(policyData);
				}else{
					result = controller.updateElk(policyData);
				}
			}
			if("search".equalsIgnoreCase(request.getParameter("action"))){
				try {
					JsonNode root = mapper.readTree(request.getReader());
					SearchData searchData = (SearchData)mapper.readValue(root.get("searchdata").toString(), SearchData.class);

					String policyType = searchData.getPolicyType();
					
					String searchText = searchData.getQuery();
					String descriptivevalue = searchData.getDescriptiveScope();
					if(descriptivevalue != null){
						DescriptiveScope dsSearch = (DescriptiveScope) commonClassDao.getEntityItem(DescriptiveScope.class, "descriptiveScopeName", searchData.getDescriptiveScope());
						if(dsSearch != null){
							String[] descriptiveList =  dsSearch.getSearch().split("AND");
							for(String keyValue : descriptiveList){
								String[] entry = keyValue.split(":");
								searchKeyValue.put(entry[0], entry[1]);
							}
						}
					}
					
					if(searchData.getClosedLooppolicyType() != null){
						String closedLoopType;
						if("Config_Fault".equalsIgnoreCase(searchData.getClosedLooppolicyType())){
							closedLoopType  = "ClosedLoop_Fault";
						}else{
							closedLoopType  = "ClosedLoop_PM";
						}
						searchKeyValue.put("configPolicyType", closedLoopType);
					}
					if(searchData.getEcompName() != null){
						searchKeyValue.put("ecompName", searchData.getEcompName());
					}
					if(searchData.getD2Service() != null){
						searchKeyValue.put("_all", searchData.getD2Service());
					}	
					if(searchData.getVnfType() != null){
						searchKeyValue.put("_all", searchData.getVnfType());					
					}
					if(searchData.getPolicyStatus() != null){
						searchKeyValue.put("_all", searchData.getPolicyStatus());
					}
					if(searchData.getVproAction() != null){
						searchKeyValue.put("_all", searchData.getVproAction());
					}
					if(searchData.getServiceType() != null){
						searchKeyValue.put("_all", searchData.getServiceType());
					}
					if(searchData.getBindTextSearch() != null){
						searchKeyValue.put("_all", searchData.getBindTextSearch());
					}
					PolicyIndexType type = null;
					if(policyType != null){
						if(policyType.equalsIgnoreCase("action")){
							type = ElkConnector.PolicyIndexType.action;
						}else if(policyType.equalsIgnoreCase("decision")){
							type = ElkConnector.PolicyIndexType.decision;
						}else if(policyType.equalsIgnoreCase("config")){
							type = ElkConnector.PolicyIndexType.config;
						}else {
							type = ElkConnector.PolicyIndexType.closedloop;
						}
					}else{
						type = ElkConnector.PolicyIndexType.all;
					}
					JestResult policyResultList = controller.search(type, searchText, searchKeyValue);
					if(policyResultList.isSucceeded()){
						result = true;
						policyResult = true;
						JsonArray resultObject = policyResultList.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
						for(int i =0; i < resultObject.size(); i++){
							String policyName = resultObject.get(i).getAsJsonObject().get("_id").toString();
							policyList.add(policyName);
						}
					}else{
						LOGGER.error("Exception Occured While Searching for Data in Elastic Search Server, Check the Logs");
					}
				}catch(Exception e){
					LOGGER.error("Exception Occured While Searching for Data in Elastic Search Server" + e);
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
			if(policyResult){
				JSONObject k = new JSONObject("{policyresult: " + policyList + "}");
				response.getWriter().write(k.toString());
			}else{
				response.getWriter().write(j.toString());
			}
		}catch(Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.addHeader("error", "Exception Occured While Performing Elastic Transaction");
			LOGGER.error("Exception Occured While Performing Elastic Transaction"+e.getMessage());
			e.printStackTrace();
		}
	}
	
}
