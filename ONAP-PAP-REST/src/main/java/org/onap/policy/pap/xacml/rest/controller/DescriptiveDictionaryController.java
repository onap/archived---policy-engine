/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.adapters.GridData;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DescriptiveScope;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DescriptiveDictionaryController {

	private static final Logger LOGGER  = FlexLogger.getLogger(DescriptiveDictionaryController.class);
	
	private static CommonClassDao commonClassDao;
	
	@Autowired
	public DescriptiveDictionaryController(CommonClassDao commonClassDao){
		DescriptiveDictionaryController.commonClassDao = commonClassDao;
	}
	
	public void setCommonClassDao(CommonClassDao commonClassDao){
		DescriptiveDictionaryController.commonClassDao = commonClassDao;
	}
	
	public DescriptiveDictionaryController(){
		//Empty Constructor
	}
	
	public UserInfo getUserInfo(String loginId){
		return (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
	}

	@RequestMapping(value={"/get_DescriptiveScopeByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDescriptiveDictionaryByNameEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("descriptiveScopeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(DescriptiveScope.class, "descriptiveScopeName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}
	
	@RequestMapping(value={"/get_DescriptiveScope"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDescriptiveDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("descriptiveScopeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(DescriptiveScope.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
		}
	}
	
	@RequestMapping(value={"/descriptive_dictionary/save_descriptive"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response)throws UnsupportedEncodingException, IOException{
		try {
			boolean duplicateflag = false;
            boolean isFakeUpdate = false;
            boolean fromAPI = false;
            if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
            DescriptiveScope descriptiveScope;
            GridData data;
            String userId = null;
            if (fromAPI) {
                descriptiveScope = mapper.readValue(root.get("dictionaryFields").toString(), DescriptiveScope.class);
                data = mapper.readValue(root.get("dictionaryFields").toString(), GridData.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(descriptiveScope.getScopeName(), "descriptiveScopeName", DescriptiveScope.class);
                	int id = 0;
                	DescriptiveScope dbdata = (DescriptiveScope) duplicateData.get(0);
                	id = dbdata.getId();
                	if(id==0){
                		isFakeUpdate=true;
                		descriptiveScope.setId(1);
                	} else {
                		descriptiveScope.setId(id);
                	}
                	descriptiveScope.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	descriptiveScope = mapper.readValue(root.get("descriptiveScopeDictionaryData").toString(), DescriptiveScope.class);
            	data = mapper.readValue(root.get("descriptiveScopeDictionaryData").toString(), GridData.class);
            	userId = root.get("userid").textValue();
            }
			StringBuilder header = new StringBuilder();
			int counter = 0;
			if(!data.getAttributes().isEmpty()){
				for(Object attribute : data.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
						if(counter>0){
							header.append("AND");
						}
						header.append( key + ":" +value);
						counter ++;
					}
				}
			}
			descriptiveScope.setSearch(header.toString());
			if(descriptiveScope.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(descriptiveScope.getScopeName(), "descriptiveScopeName", DescriptiveScope.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					descriptiveScope.setUserCreatedBy(this.getUserInfo(userId));
					descriptiveScope.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(descriptiveScope);
				}
			}else{
				if(!isFakeUpdate){
					descriptiveScope.setUserModifiedBy(this.getUserInfo(userId));
					descriptiveScope.setModifiedDate(new Date());
					commonClassDao.update(descriptiveScope); 
				}
			} 
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString =  mapper.writeValueAsString(commonClassDao.getData(DescriptiveScope.class));
            }
            
            if (fromAPI) {
            	if (responseString!=null && !responseString.equals("Duplicate")) {
            		if(isFakeUpdate){
            			responseString = "Exists";
            		} else {
            			responseString = "Success";
            		}
            	}
            	ModelAndView result = new ModelAndView();
            	result.setViewName(responseString);
            	return result;
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application / json");
                request.setCharacterEncoding("UTF-8");
 
                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{descriptiveScopeDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
 
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/descriptive_dictionary/remove_descriptiveScope"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void removeDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DescriptiveScope descriptiveScope = mapper.readValue(root.get("data").toString(), DescriptiveScope.class);
			commonClassDao.delete(descriptiveScope);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(DescriptiveScope.class));
			JSONObject j = new JSONObject("{descriptiveScopeDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
	}
}

