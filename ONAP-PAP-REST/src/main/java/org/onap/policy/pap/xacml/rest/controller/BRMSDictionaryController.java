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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.onap.policy.api.PEDependency;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.BRMSController;
import org.onap.policy.rest.jpa.BRMSDependency;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
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
public class BRMSDictionaryController{
	
	private static final Logger LOGGER  = FlexLogger.getLogger(BRMSDictionaryController.class);

	
	private static CommonClassDao commonClassDao;
	
	@Autowired
	public BRMSDictionaryController(CommonClassDao commonClassDao){
		BRMSDictionaryController.commonClassDao = commonClassDao;
	}
	
	public BRMSDictionaryController() {}

	private String rule;
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}

	@RequestMapping(value={"/get_BRMSParamDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getBRMSParamDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("brmsParamDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(BRMSParamTemplate.class, "ruleName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			 LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}
	
	@RequestMapping(value={"/get_BRMSParamData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getBRMSParamDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("brmsParamDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(BRMSParamTemplate.class)));
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
	
	@RequestMapping(value={"/brms_dictionary/set_BRMSParamData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void SetRuleData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream() , writer, StandardCharsets.UTF_8);
		String cleanStreamBoundary =  writer.toString().replaceFirst("------(.*)(?s).*octet-stream", "");
		rule = cleanStreamBoundary.substring(0, cleanStreamBoundary.lastIndexOf("end")+4);
	}
	
	@RequestMapping(value={"/brms_dictionary/save_BRMSParam"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		try {
			boolean duplicateflag = false;
            boolean fromAPI = false;
            if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			
            BRMSParamTemplate bRMSParamTemplateData;
            String userId = null;
            if(fromAPI) {
                bRMSParamTemplateData = (BRMSParamTemplate)mapper.readValue(root.get("dictionaryFields").toString(), BRMSParamTemplate.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(bRMSParamTemplateData.getRuleName(), "ruleName", BRMSParamTemplate.class);
                	int id = 0;
                	BRMSParamTemplate data = (BRMSParamTemplate) duplicateData.get(0);
                	id = data.getId();
                	bRMSParamTemplateData.setId(id);
                	bRMSParamTemplateData.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
                bRMSParamTemplateData = (BRMSParamTemplate)mapper.readValue(root.get("brmsParamDictionaryData").toString(), BRMSParamTemplate.class);
                userId = root.get("userid").textValue();
            }
            
			bRMSParamTemplateData.setRule(rule);
			if(bRMSParamTemplateData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(bRMSParamTemplateData.getRuleName(), "ruleName", BRMSParamTemplate.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					bRMSParamTemplateData.setUserCreatedBy(this.getUserInfo(userId));
					commonClassDao.save(bRMSParamTemplateData);
				}	
			}else{
				commonClassDao.update(bRMSParamTemplateData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(BRMSDictionaryController.commonClassDao.getData(BRMSParamTemplate.class));
			}
            if(fromAPI) {
                if (responseString!=null && !responseString.equals("Duplicate")) {
                    responseString = "Success";
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
            	response.setCharacterEncoding("UTF-8");
            	response.setContentType("application / json");
            	request.setCharacterEncoding("UTF-8");

            	PrintWriter out = response.getWriter();
            	JSONObject j = new JSONObject("{brmsParamDictionaryDatas: " + responseString + "}");
            	out.write(j.toString());
            	return null;
            }
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW , e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/brms_dictionary/remove_brmsParam"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response)throws UnsupportedEncodingException, IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			BRMSParamTemplate bRMSParamTemplateData = (BRMSParamTemplate)mapper.readValue(root.get("data").toString(), BRMSParamTemplate.class);
			commonClassDao.delete(bRMSParamTemplateData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(BRMSDictionaryController.commonClassDao.getData(BRMSParamTemplate.class));
			JSONObject j = new JSONObject("{brmsParamDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			 LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
    @RequestMapping(value={"/get_BRMSDependencyDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSDependencyDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("brmsDependencyDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(BRMSDependency.class, "dependencyName")));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.getWriter().write(j.toString());
        }
        catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
        }
    }
    
    @RequestMapping(value={"/get_BRMSDependencyData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSDependencyDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("brmsDependencyDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(BRMSDependency.class)));
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
    
    @RequestMapping(value={"/brms_dictionary/save_BRMSDependencyData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView saveBRMSDependencyDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        try {
            boolean duplicateflag = false;
            LOGGER.debug("DictionaryController:  saveBRMSDependencyDictionary() is called");
            
            boolean fromAPI = false;
            if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
                fromAPI = true;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            
            BRMSDependency brmsDependency;
            String userId = null;
            if (fromAPI) {
                brmsDependency = (BRMSDependency)mapper.readValue(root.get("dictionaryFields").toString(), BRMSDependency.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated
                if (request.getParameter("operation").equals("update")) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(brmsDependency.getDependencyName(), "dependencyName", BRMSDependency.class);
                	int id = 0;
                	BRMSDependency data = (BRMSDependency) duplicateData.get(0);
                	id = data.getId();
                	brmsDependency.setId(id);
                	brmsDependency.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
                brmsDependency = (BRMSDependency)mapper.readValue(root.get("brmsDependencyDictionaryData").toString(), BRMSDependency.class);
                userId = root.get("userid").textValue();
            }
            
            LOGGER.audit("the userId from the onap portal is: " + userId);
            String responseString = null;
            
            if(brmsDependency.getDependency()!=null && !brmsDependency.getDependency().trim().equals("")){
                PEDependency dependency = null;
                try{
                    dependency = PolicyUtils.jsonStringToObject(brmsDependency.getDependency(), PEDependency.class);
                }catch(Exception e){
                    LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "wrong data given for BRMS PEDependency Dictionary : " + brmsDependency.getDependency(),e);
                }
                if(dependency==null){
                    responseString = "Error";
                }else{
                    if(brmsDependency.getId() == 0){
                        List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(brmsDependency.getDependencyName(), "dependencyName", BRMSDependency.class);
                        if(!duplicateData.isEmpty()){
                            duplicateflag = true;
                        }else{
                            brmsDependency.setUserCreatedBy(getUserInfo(userId));
                            brmsDependency.setUserModifiedBy(getUserInfo(userId));
                            LOGGER.audit("DictionaryController:  got the user info now about to call Save() method on brmsDependencydao");
                            commonClassDao.save(brmsDependency);
                        }
                    }else{
                        brmsDependency.setUserModifiedBy(this.getUserInfo(userId));
                        brmsDependency.setModifiedDate(new Date());
                        commonClassDao.update(brmsDependency); 
                    }
                    if(duplicateflag) {
                        responseString = "Duplicate";
                    } else {
                        responseString = mapper.writeValueAsString(commonClassDao.getData(BRMSDependency.class));
                    }
                }
            }
            if (fromAPI) {
                if (responseString!=null && !responseString.equals("Duplicate") && !responseString.equals("Error")) {
                    responseString = "Success";
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application / json");
                request.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{brmsDependencyDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
 
                return null;
            }
        } catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.write(e.getMessage());
        }
        return null;
    }
 
    @RequestMapping(value={"/brms_dictionary/remove_brmsDependency"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView removeBRMSDependencyDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            BRMSDependency brmsDependency = (BRMSDependency)mapper.readValue(root.get("data").toString(), BRMSDependency.class);
            commonClassDao.delete(brmsDependency);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application / json");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String responseString = mapper.writeValueAsString(commonClassDao.getData(BRMSDependency.class));
            JSONObject j = new JSONObject("{brmsDependencyDictionaryDatas: " + responseString + "}");
            out.write(j.toString());
            return null;
        }
        catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.write(e.getMessage());
        }
        return null;
    }
    
    
    @RequestMapping(value={"/get_BRMSControllerDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSControllerDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("brmsControllerDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(BRMSController.class, "controllerName")));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.getWriter().write(j.toString());
        }
        catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
        }
    }
    
    @RequestMapping(value={"/get_BRMSControllerData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getBRMSControllerDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("brmsControllerDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(BRMSController.class)));
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
    
    @RequestMapping(value={"/brms_dictionary/save_BRMSControllerData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView saveBRMSControllerDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException{
        try {
            boolean duplicateflag = false;
            LOGGER.debug("DictionaryController:  saveBRMSControllerDictionary() is called");
            boolean fromAPI = false;
            if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
                fromAPI = true;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            BRMSController brmsController;
            String userId = null;
            if (fromAPI) {
                brmsController = (BRMSController)mapper.readValue(root.get("dictionaryFields").toString(), BRMSController.class);
                userId = "API";
                //check if update operation or create, get id for data to be updated
                if (request.getParameter("operation").equals("update")) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(brmsController.getControllerName(), "controllerName", BRMSController.class);
                	int id = 0;
                	BRMSController data = (BRMSController) duplicateData.get(0);
                	id = data.getId();
                	brmsController.setId(id);
                	brmsController.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
                brmsController = (BRMSController)mapper.readValue(root.get("brmsControllerDictionaryData").toString(), BRMSController.class);
                userId = root.get("userid").textValue();
            }
            LOGGER.audit("the userId from the onap portal is: " + userId);
            String responseString = null;
            if(brmsController.getController()!=null && !brmsController.getController().trim().equals("")){
                PEDependency dependency = null;
                try{
                    dependency = PolicyUtils.jsonStringToObject(brmsController.getController(), PEDependency.class);
                }catch(Exception e){
                    LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "wrong data given for BRMS Controller Dictionary : " + brmsController.getController(),e);
                }
                if(dependency==null){
                    responseString = "Error";
                }else{
                    if(brmsController.getId() == 0){
                        List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(brmsController.getControllerName(), "controllerName", BRMSController.class);
                        if(!duplicateData.isEmpty()){
                            duplicateflag = true;
                        }else{
                            brmsController.setUserCreatedBy(getUserInfo(userId));
                            brmsController.setUserModifiedBy(getUserInfo(userId));
                            LOGGER.audit("DictionaryController:  got the user info now about to call Save() method on brmsControllerdao");
                            commonClassDao.save(brmsController);
                        }
                    }else{
                        brmsController.setUserModifiedBy(this.getUserInfo(userId));
                        brmsController.setModifiedDate(new Date());
                        commonClassDao.update(brmsController); 
                    }
                    if(duplicateflag) {
                        responseString = "Duplicate";
                    } else {
                        responseString = mapper.writeValueAsString(commonClassDao.getData(BRMSController.class));
                    }
                }
            }
            if (fromAPI) {
                if (responseString!=null && !responseString.equals("Duplicate") && !responseString.equals("Error")) {
                    responseString = "Success";
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application / json");
                request.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{brmsControllerDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        } catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.write(e.getMessage());
        }
        return null;
    }
 
    @RequestMapping(value={"/brms_dictionary/remove_brmsController"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView removeBRMSControllerDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException{
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            BRMSController brmsController = (BRMSController)mapper.readValue(root.get("data").toString(), BRMSController.class);
            commonClassDao.delete(brmsController);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application / json");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String responseString = mapper.writeValueAsString(commonClassDao.getData(BRMSController.class));
            JSONObject j = new JSONObject("{brmsControllerDictionaryDatas: " + responseString + "}");
            out.write(j.toString());
            return null;
        }
        catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.write(e.getMessage());
        }
        return null;
    }
    
    public BRMSDependency getDependencyDataByID(String dependencyName){
        return (BRMSDependency) commonClassDao.getEntityItem(BRMSDependency.class, "dependencyName", dependencyName);
    }
    
    public BRMSController getControllerDataByID(String controllerName){
        return (BRMSController) commonClassDao.getEntityItem(BRMSController.class, "controllerName", controllerName);
    }
	
}
