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

import java.io.PrintWriter;
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
import org.onap.policy.rest.jpa.ClosedLoopD2Services;
import org.onap.policy.rest.jpa.ClosedLoopSite;
import org.onap.policy.rest.jpa.PEPOptions;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.VNFType;
import org.onap.policy.rest.jpa.VSCLAction;
import org.onap.policy.rest.jpa.VarbindDictionary;
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
public class ClosedLoopDictionaryController{

	private static final Logger LOGGER = FlexLogger.getLogger(ClosedLoopDictionaryController.class);

	private static CommonClassDao commonClassDao;
	
	@Autowired
	public ClosedLoopDictionaryController(CommonClassDao commonClassDao){
		ClosedLoopDictionaryController.commonClassDao = commonClassDao;
	}
	
	public ClosedLoopDictionaryController(){}

	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;
	}


	@RequestMapping(value={"/get_VSCLActionDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVSCLActionDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vsclActionDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(VSCLAction.class, "vsclaction")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}


	@RequestMapping(value={"/get_VSCLActionData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVSCLActionDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vsclActionDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(VSCLAction.class)));
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

	@RequestMapping(value={"/get_VNFTypeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVNFTypeDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vnfTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(VNFType.class, "vnftype")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_VNFTypeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVNFTypeDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vnfTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(VNFType.class)));
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

	@RequestMapping(value={"/get_PEPOptionsDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPEPOptionsDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("pepOptionsDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PEPOptions.class, "pepName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_PEPOptionsData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPEPOptionsDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("pepOptionsDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PEPOptions.class)));
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

	@RequestMapping(value={"/get_VarbindDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVarbindDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("varbindDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(VarbindDictionary.class, "varbindName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_VarbindDictionaryData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVarbindDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("varbindDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(VarbindDictionary.class)));
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

	@RequestMapping(value={"/get_ClosedLoopServicesDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopServiceDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopServiceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(ClosedLoopD2Services.class, "serviceName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_ClosedLoopServicesData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopServiceDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopServiceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(ClosedLoopD2Services.class)));
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

	@RequestMapping(value={"/get_ClosedLoopSiteDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopSiteDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopSiteDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(ClosedLoopSite.class, "siteName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_ClosedLoopSiteData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopSiteDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopSiteDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(ClosedLoopSite.class)));
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

	@RequestMapping(value={"/cl_dictionary/save_vsclAction"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveVSCLAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			VSCLAction vSCLAction;
			String userId = null;
			if (fromAPI) {
				vSCLAction = (VSCLAction)mapper.readValue(root.get("dictionaryFields").toString(), VSCLAction.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(vSCLAction.getVsclaction(), "vsclaction", VSCLAction.class);
					int id = 0;
					VSCLAction data = (VSCLAction) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						vSCLAction.setId(1);
					} else {
						vSCLAction.setId(id);
					}

					vSCLAction.setUserCreatedBy(this.getUserInfo(userId));
				}

			} else {
				vSCLAction = (VSCLAction)mapper.readValue(root.get("vsclActionDictionaryData").toString(), VSCLAction.class);
				userId = root.get("userid").textValue();
			}
			if(vSCLAction.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(vSCLAction.getVsclaction(), "vsclaction", VSCLAction.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					vSCLAction.setUserCreatedBy(this.getUserInfo(userId));
					vSCLAction.setUserModifiedBy(this.getUserInfo(userId));
					vSCLAction.setModifiedDate(new Date());
					commonClassDao.save(vSCLAction);
				}
			}else{
				if(!isFakeUpdate) {
					vSCLAction.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.update(vSCLAction); 
				}
			}

			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(VSCLAction.class));
			}	
			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate) {
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
				JSONObject j = new JSONObject("{vsclActionDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
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

	@RequestMapping(value={"/cl_dictionary/remove_VsclAction"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeVSCLAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			VSCLAction vSCLAction = (VSCLAction)mapper.readValue(root.get("data").toString(), VSCLAction.class);
			commonClassDao.delete(vSCLAction);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(VSCLAction.class));
			JSONObject j = new JSONObject("{vsclActionDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/cl_dictionary/save_vnfType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveVnfType(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			VNFType vNFType;
			String userId = null;

			if (fromAPI) {
				vNFType = (VNFType)mapper.readValue(root.get("dictionaryFields").toString(), VNFType.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(vNFType.getVnftype(), "vnftype", VNFType.class);
					int id = 0;
					VNFType data = (VNFType) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						vNFType.setId(1);
					} else {
						vNFType.setId(id);
					}
					vNFType.setUserCreatedBy(this.getUserInfo(userId));
				}
			} else {
				vNFType = (VNFType)mapper.readValue(root.get("vnfTypeDictionaryData").toString(), VNFType.class);
				userId = root.get("userid").textValue();
			}
			if(vNFType.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(vNFType.getVnftype(), "vnftype", VNFType.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					vNFType.setUserCreatedBy(this.getUserInfo(userId));
					vNFType.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(vNFType);
				}  	 
			}else{
				if(!isFakeUpdate) {
					vNFType.setUserModifiedBy(this.getUserInfo(userId));
					vNFType.setModifiedDate(new Date());
					commonClassDao.update(vNFType); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(VNFType.class));
			} 
			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate) {
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
				JSONObject j = new JSONObject("{vnfTypeDictionaryDatas: " + responseString + "}");
				out.write(j.toString()); 
				return null;
			}
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

	@RequestMapping(value={"/cl_dictionary/remove_vnfType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeVnfType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			VNFType vNFType = (VNFType)mapper.readValue(root.get("data").toString(), VNFType.class);
			commonClassDao.delete(vNFType);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(VNFType.class));
			JSONObject j = new JSONObject("{vnfTypeDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/cl_dictionary/save_pepOptions"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePEPOptions(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
            PEPOptions pEPOptions;
            GridData gridData;
            String userId = null;
            if (fromAPI) {
                pEPOptions = (PEPOptions)mapper.readValue(root.get("dictionaryFields").toString(), PEPOptions.class);
                gridData = (GridData)mapper.readValue(root.get("dictionaryFields").toString(), GridData.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(pEPOptions.getPepName(), "pepName", PEPOptions.class);
                    int id = 0;
                    PEPOptions data = (PEPOptions) duplicateData.get(0);
                    id = data.getId();
                    if(id==0){
                        isFakeUpdate=true;
                        pEPOptions.setId(1);
                    } else {
                        pEPOptions.setId(id);
                    }
                    pEPOptions.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	pEPOptions = (PEPOptions)mapper.readValue(root.get("pepOptionsDictionaryData").toString(), PEPOptions.class);
            	gridData = (GridData)mapper.readValue(root.get("pepOptionsDictionaryData").toString(), GridData.class);
            	userId = root.get("userid").textValue();
            }
			String actions = "";
			int counter = 0;
			if(gridData.getAttributes().size() > 0){
				for(Object attribute : gridData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
						if(counter>0){
							actions = actions + ":#@";
						}
						actions = actions + key + "=#@";
						actions = actions + value;
						counter ++;
					}
				}
			}
			pEPOptions.setActions(actions);
			if(pEPOptions.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(pEPOptions.getPepName(), "pepName", PEPOptions.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					pEPOptions.setUserCreatedBy(this.getUserInfo(userId));
					pEPOptions.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(pEPOptions);
				}   	 
			}else{
				if(!isFakeUpdate){
					pEPOptions.setUserModifiedBy(this.getUserInfo(userId));
					pEPOptions.setModifiedDate(new Date());
					commonClassDao.update(pEPOptions);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(PEPOptions.class));
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
                JSONObject j = new JSONObject("{pepOptionsDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
 
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/cl_dictionary/remove_pepOptions"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePEPOptions(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PEPOptions pEPOptions = (PEPOptions)mapper.readValue(root.get("data").toString(), PEPOptions.class);
			commonClassDao.delete(pEPOptions);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PEPOptions.class));
			JSONObject j = new JSONObject("{pepOptionsDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/cl_dictionary/save_service"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveServiceType(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
            ClosedLoopD2Services serviceData;
            String userId = null;
            if (fromAPI) {
                serviceData = (ClosedLoopD2Services)mapper.readValue(root.get("dictionaryFields").toString(), ClosedLoopD2Services.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(serviceData.getServiceName(), "serviceName", ClosedLoopD2Services.class);
                    int id = 0;
                    ClosedLoopD2Services data = (ClosedLoopD2Services) duplicateData.get(0);
                    id = data.getId();
                    if(id==0){
                        isFakeUpdate=true;
                        serviceData.setId(1);
                    } else {
                        serviceData.setId(id);
                    }
                    serviceData.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	serviceData = (ClosedLoopD2Services)mapper.readValue(root.get("closedLoopServiceDictionaryData").toString(), ClosedLoopD2Services.class);
            	userId = root.get("userid").textValue();
            }
			if(serviceData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(serviceData.getServiceName(), "serviceName", ClosedLoopD2Services.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					serviceData.setUserCreatedBy(this.getUserInfo(userId));
					serviceData.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(serviceData);
				}
			}else{
				if(!isFakeUpdate){
					serviceData.setUserModifiedBy(this.getUserInfo(userId));
					serviceData.setModifiedDate(new Date());
					commonClassDao.update(serviceData); 
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(ClosedLoopD2Services.class));
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
                JSONObject j = new JSONObject("{closedLoopServiceDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/cl_dictionary/remove_Service"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeServiceType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ClosedLoopD2Services closedLoopD2Services = (ClosedLoopD2Services)mapper.readValue(root.get("data").toString(), ClosedLoopD2Services.class);
			commonClassDao.delete(closedLoopD2Services);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(ClosedLoopD2Services.class));
			JSONObject j = new JSONObject("{closedLoopServiceDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/cl_dictionary/save_siteName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveSiteType(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
            ClosedLoopSite siteData;
            String userId = null;
            if (fromAPI) {
                siteData = (ClosedLoopSite)mapper.readValue(root.get("dictionaryFields").toString(), ClosedLoopSite.class);
                userId = "API";
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(siteData.getSiteName(), "siteName", ClosedLoopSite.class);
                    int id = 0;
                    ClosedLoopSite data = (ClosedLoopSite) duplicateData.get(0);
                    id = data.getId();
                    if(id==0){
                        isFakeUpdate=true;
                        siteData.setId(1);
                    } else {
                        siteData.setId(id);
                    }
                    siteData.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	siteData = (ClosedLoopSite)mapper.readValue(root.get("closedLoopSiteDictionaryData").toString(), ClosedLoopSite.class);
            	userId = root.get("userid").textValue();
            }
			if(siteData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(siteData.getSiteName(), "siteName", ClosedLoopSite.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					siteData.setUserCreatedBy(this.getUserInfo(userId));
					siteData.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(siteData);
				}
			}else{
				if(!isFakeUpdate) {
					siteData.setUserModifiedBy(this.getUserInfo(userId));
					siteData.setModifiedDate(new Date());
					commonClassDao.update(siteData);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(ClosedLoopSite.class));
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
                JSONObject j = new JSONObject("{closedLoopSiteDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/cl_dictionary/remove_site"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeSiteType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ClosedLoopSite closedLoopSite = (ClosedLoopSite)mapper.readValue(root.get("data").toString(), ClosedLoopSite.class);
			commonClassDao.delete(closedLoopSite);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(ClosedLoopSite.class));
			JSONObject j = new JSONObject("{closedLoopSiteDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/cl_dictionary/save_varbind"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveVarbind(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
            VarbindDictionary varbindDictionary;
            String userId = null;
            if (fromAPI) {
                varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("dictionaryFields").toString(), VarbindDictionary.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(varbindDictionary.getVarbindName(), "varbindName", VarbindDictionary.class);
                    int id = 0;
                    VarbindDictionary data = (VarbindDictionary) duplicateData.get(0);
                    id = data.getId();
                    if(id==0){
                        isFakeUpdate=true;
                        varbindDictionary.setId(1);
                    } else {
                        varbindDictionary.setId(id);
                    }
                    varbindDictionary.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("varbindDictionaryData").toString(), VarbindDictionary.class);
            	userId = root.get("userid").textValue();
            }
			if(varbindDictionary.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(varbindDictionary.getVarbindName(), "varbindName", VarbindDictionary.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					varbindDictionary.setUserCreatedBy(this.getUserInfo(userId));
					varbindDictionary.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(varbindDictionary);
				}	  
			}else{
				if(!isFakeUpdate){
					varbindDictionary.setUserModifiedBy(this.getUserInfo(userId));
					varbindDictionary.setModifiedDate(new Date());
					commonClassDao.update(varbindDictionary);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(VarbindDictionary.class));
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
                JSONObject j = new JSONObject("{varbindDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/cl_dictionary/remove_varbindDict"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeVarbind(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			VarbindDictionary varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("data").toString(), VarbindDictionary.class);
			commonClassDao.delete(varbindDictionary);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(VarbindDictionary.class));
			JSONObject j = new JSONObject("{varbindDictionaryDatas: " + responseString + "}");
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

}

