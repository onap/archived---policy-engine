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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DCAEuuid;
import org.onap.policy.rest.jpa.MicroServiceAttribute;
import org.onap.policy.rest.jpa.MicroServiceConfigName;
import org.onap.policy.rest.jpa.MicroServiceLocation;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.util.MSAttributeObject;
import org.onap.policy.rest.util.MSModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Controller
public class MicroServiceDictionaryController {
	private static final Logger LOGGER  = FlexLogger.getLogger(MicroServiceDictionaryController.class);

	private static CommonClassDao commonClassDao;

    private static String successMapKey= "successMapKey";
    private static String successMsg = "success";
    private static String operation = "operation";
    private static String getDictionary = "getDictionary";
    private static String errorMsg = "error";
    private static String dictionaryDBQuery = "dictionaryDBQuery";
    private HashMap<String,MSAttributeObject > classMap;
    private List<String> modelList = new ArrayList<>();
    private static String apiflag = "apiflag";
	private static String dictionaryFields ="dictionaryFields";
	private static String update = "update";
	private static String duplicateResponseString = "Duplicate";
	private static String successMessage = "Success";
	private static String utf8 = "UTF-8";
	private static String applicationJsonContentType = "application / json";
	private static String existsResponseString = "Exists";
	private static String microServiceModelsDictionaryDatas = "microServiceModelsDictionaryDatas";
	private static String modelName = "modelName";
	private static String microServiceModelsDictionaryData = "microServiceModelsDictionaryData";
	private static String description = "description";
	private static String version = "version";
	private static String classMapData = "classMap";
	/*
	 * This is an empty constructor
	 */
    public MicroServiceDictionaryController(){}

	@Autowired
	public MicroServiceDictionaryController(CommonClassDao commonClassDao){
		MicroServiceDictionaryController.commonClassDao = commonClassDao;
	}
	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		MicroServiceDictionaryController.commonClassDao = commonClassDao;
	}

	public UserInfo getUserInfo(String loginId){
		return (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
	}

	MSModelUtils utils = new MSModelUtils(XACMLPapServlet.getMsOnapName(), XACMLPapServlet.getMsPolicyName());
	private MicroServiceModels newModel;


	@RequestMapping(value={"/get_DCAEUUIDDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDCAEUUIDDictionaryByNameEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("dcaeUUIDDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(DCAEuuid.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/get_DCAEUUIDData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDCAEUUIDDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("dcaeUUIDDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(DCAEuuid.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);
            response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ms_dictionary/save_dcaeUUID"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			boolean duplicateflag = false;
            boolean isFakeUpdate = false;
            boolean fromAPI = false;
            if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
            DCAEuuid dCAEuuid;
            if (fromAPI) {
                dCAEuuid = (DCAEuuid)mapper.readValue(root.get(dictionaryFields).toString(), DCAEuuid.class);

                //check if update operation or create, get id for data to be updated and update attributeData
                if ((update).equals(request.getParameter(operation))) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(dCAEuuid.getName(), "name", DCAEuuid.class);
                	DCAEuuid data = (DCAEuuid) duplicateData.get(0);
                	int id = data.getId();
                	if(id==0){
                		isFakeUpdate=true;
                		dCAEuuid.setId(1);
                	} else {
                		dCAEuuid.setId(id);
                	}
                }
            } else {
            	dCAEuuid = (DCAEuuid)mapper.readValue(root.get("dcaeUUIDDictionaryData").toString(), DCAEuuid.class);
            }
			if(dCAEuuid.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(dCAEuuid.getName(), "name", DCAEuuid.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(dCAEuuid);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(dCAEuuid);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = duplicateResponseString;
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(DCAEuuid.class));
            }

            if (fromAPI) {
                if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
                    if(isFakeUpdate){
                        responseString = existsResponseString;
                    } else {
                        responseString = successMessage;
                    }
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding(utf8);
                response.setContentType(applicationJsonContentType);
                request.setCharacterEncoding(utf8);

                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{dcaeUUIDDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_dcaeuuid"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DCAEuuid dCAEuuid = (DCAEuuid)mapper.readValue(root.get("data").toString(), DCAEuuid.class);
			commonClassDao.delete(dCAEuuid);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(DCAEuuid.class));
			JSONObject j = new JSONObject("{dcaeUUIDDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}


	@RequestMapping(value={"/get_MicroServiceConfigNameDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceConfigNameByNameDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceCongigNameDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(MicroServiceConfigName.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e);
		}
	}



	@RequestMapping(value={"/get_MicroServiceConfigNameData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceConfigNameDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceCongigNameDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(MicroServiceConfigName.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);
            response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ms_dictionary/save_configName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			boolean duplicateflag = false;
            boolean isFakeUpdate = false;
            boolean fromAPI = false;
            if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
            MicroServiceConfigName microServiceConfigName;
            if (fromAPI) {
                microServiceConfigName = (MicroServiceConfigName)mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceConfigName.class);

                //check if update operation or create, get id for data to be updated and update attributeData
                if ((update).equals(request.getParameter(operation))) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(microServiceConfigName.getName(), "name", MicroServiceConfigName.class);
                    MicroServiceConfigName data = (MicroServiceConfigName) duplicateData.get(0);
                    int id = data.getId();

                    if(id==0){
                        isFakeUpdate=true;
                        microServiceConfigName.setId(1);
                    } else {
                        microServiceConfigName.setId(id);
                    }
                }
            } else {
            	microServiceConfigName = (MicroServiceConfigName)mapper.readValue(root.get("microServiceCongigNameDictionaryData").toString(), MicroServiceConfigName.class);
            }
			if(microServiceConfigName.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(microServiceConfigName.getName(), "name", MicroServiceConfigName.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(microServiceConfigName);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(microServiceConfigName);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = duplicateResponseString;
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceConfigName.class));
            }

            if (fromAPI) {
                if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
                    if(isFakeUpdate){
                        responseString = existsResponseString;
                    } else {
                        responseString = successMessage;
                    }
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding(utf8);
                response.setContentType(applicationJsonContentType);
                request.setCharacterEncoding(utf8);

                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{microServiceCongigNameDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_msConfigName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceConfigName microServiceConfigName = (MicroServiceConfigName)mapper.readValue(root.get("data").toString(), MicroServiceConfigName.class);
			commonClassDao.delete(microServiceConfigName);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceConfigName.class));
			JSONObject j = new JSONObject("{microServiceCongigNameDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/get_MicroServiceLocationDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceLocationByNameDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceLocationDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(MicroServiceLocation.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/get_MicroServiceLocationData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceLocationDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceLocationDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(MicroServiceLocation.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);
            response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ms_dictionary/save_location"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response)throws IOException{
		try {
			boolean duplicateflag = false;
            boolean isFakeUpdate = false;
            boolean fromAPI = false;
            if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
            MicroServiceLocation microServiceLocation;
            if (fromAPI) {
                microServiceLocation = (MicroServiceLocation)mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceLocation.class);

                //check if update operation or create, get id for data to be updated and update attributeData
                if ((update).equals(request.getParameter(operation))) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(microServiceLocation.getName(), "name", MicroServiceLocation.class);
                    MicroServiceLocation data = (MicroServiceLocation) duplicateData.get(0);
                    int id = data.getId();

                    if(id==0){
                        isFakeUpdate=true;
                        microServiceLocation.setId(1);
                    } else {
                        microServiceLocation.setId(id);
                    }
                }
            } else {
            	microServiceLocation = (MicroServiceLocation)mapper.readValue(root.get("microServiceLocationDictionaryData").toString(), MicroServiceLocation.class);
            }
			if(microServiceLocation.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(microServiceLocation.getName(), "name", MicroServiceLocation.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(microServiceLocation);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(microServiceLocation);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = duplicateResponseString;
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceLocation.class));
            }

            if (fromAPI) {
                if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
                    if(isFakeUpdate){
                        responseString = existsResponseString;
                    } else {
                        responseString = successMessage;
                    }
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding(utf8);
                response.setContentType(applicationJsonContentType);
                request.setCharacterEncoding(utf8);

                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{microServiceLocationDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
		}catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_msLocation"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceLocation microServiceLocation = (MicroServiceLocation)mapper.readValue(root.get("data").toString(), MicroServiceLocation.class);
			commonClassDao.delete(microServiceLocation);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceLocation.class));
			JSONObject j = new JSONObject("{microServiceLocationDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

    @RequestMapping(value={"/get_MicroServiceAttributeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceAttributeByNameDictionaryEntityData(HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("microServiceAttributeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(MicroServiceAttribute.class, "name")));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.getWriter().write(j.toString());
        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }

    @RequestMapping(value={"/get_MicroServiceAttributeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceAttributeDictionaryEntityData(HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("microServiceAttributeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(MicroServiceAttribute.class)));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);
            response.addHeader(operation, getDictionary);
            response.getWriter().write(j.toString());

        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
        }
    }

    @RequestMapping(value={"/ms_dictionary/save_modelAttribute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView saveMicroServiceAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            boolean duplicateflag = false;
            boolean fromAPI = false;
            if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
                fromAPI = true;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());

            MicroServiceAttribute microServiceAttribute;
            if (fromAPI) {
                microServiceAttribute = (MicroServiceAttribute)mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceAttribute.class);

                //check if update operation or create, get id for data to be updated and update attributeData
                if ((update).equals(request.getParameter(operation))) {
                    MicroServiceAttribute initialAttribute = (MicroServiceAttribute)mapper.readValue(root.get("initialFields").toString(), MicroServiceAttribute.class);

                    String checkValue = initialAttribute.getName() + ":" + initialAttribute.getValue() + ":" + initialAttribute.getModelName();
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkValue, "name:value:modelName", MicroServiceAttribute.class);
                    int id=0;
                    for (int i= 0; i<duplicateData.size(); i++){
                        MicroServiceAttribute data = (MicroServiceAttribute) duplicateData.get(0);
                        id = data.getId();
                    }
                    microServiceAttribute.setId(id);
                }
            } else {
                microServiceAttribute = (MicroServiceAttribute)mapper.readValue(root.get("modelAttributeDictionaryData").toString(), MicroServiceAttribute.class);
            }

            if(microServiceAttribute.getId() == 0){
                String checkValue = microServiceAttribute.getName() + ":" + microServiceAttribute.getValue() + ":" + microServiceAttribute.getModelName();
                List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkValue, "name:value:modelName", MicroServiceAttribute.class);
                if(!duplicateData.isEmpty()){
                    duplicateflag = true;
                }else{
                	commonClassDao.save(microServiceAttribute);
                }
            }else{
            	commonClassDao.update(microServiceAttribute);
            }

            String responseString = "";
            if(duplicateflag){
                responseString = duplicateResponseString;
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceAttribute.class));
            }

            if (fromAPI) {
                if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
                    responseString = successMessage;
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding(utf8);
                response.setContentType(applicationJsonContentType);
                request.setCharacterEncoding(utf8);

                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{microServiceAttributeDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }
        catch (Exception e){
            response.setCharacterEncoding(utf8);
            request.setCharacterEncoding(utf8);
            PrintWriter out = response.getWriter();
            out.write(e.getMessage());
            LOGGER.error(e);
        }
        return null;
    }

    @RequestMapping(value={"/ms_dictionary/remove_modelAttribute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ModelAndView removeMicroServiceAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            MicroServiceAttribute microServiceAttribute = (MicroServiceAttribute)mapper.readValue(root.get("data").toString(), MicroServiceAttribute.class);
            commonClassDao.delete(microServiceAttribute);
            response.setCharacterEncoding(utf8);
            response.setContentType(applicationJsonContentType);
            request.setCharacterEncoding(utf8);

            PrintWriter out = response.getWriter();

            String responseString = mapper.writeValueAsString(MicroServiceDictionaryController.commonClassDao.getData(MicroServiceAttribute.class));
            JSONObject j = new JSONObject("{microServiceAttributeDictionaryDatas: " + responseString + "}");
            out.write(j.toString());

            return null;
        }
        catch (Exception e){
            LOGGER.error(e);
            response.setCharacterEncoding(utf8);
            request.setCharacterEncoding(utf8);
            PrintWriter out = response.getWriter();
            out.write(e.getMessage());
        }
        return null;
    }


	@RequestMapping(value={"/get_MicroServiceModelsDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceModelsDictionaryByNameEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put(microServiceModelsDictionaryDatas, mapper.writeValueAsString(commonClassDao.getDataByColumn(MicroServiceModels.class, modelName)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			 LOGGER.error(e);
		}
	}

    @RequestMapping(value={"/get_MicroServiceModelsDataByVersion"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryByVersionEntityData(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request.getReader());
            String modelName = null;
            if (root.get(microServiceModelsDictionaryData).has(modelName)){
                modelName = root.get(microServiceModelsDictionaryData).get(modelName).asText().replace("\"", "");
            }
             if (modelName!=null){
                    model.put(microServiceModelsDictionaryDatas, mapper.writeValueAsString(commonClassDao.getDataById(MicroServiceModels.class, modelName, modelName)));
             } else{
                 model.put(errorMsg, "No model name given");
             }
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.getWriter().write(j.toString());
        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }

	@RequestMapping(value={"/get_MicroServiceModelsData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceModelsDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put(microServiceModelsDictionaryDatas, mapper.writeValueAsString(commonClassDao.getData(MicroServiceModels.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);
            response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
		}
	}

    @RequestMapping(value={"/get_MicroServiceModelsDataServiceVersion"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryEntityDataServiceVersion(HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            List<String> data = new ArrayList<>();
            List<Object> datas = commonClassDao.getData(MicroServiceModels.class);
            for(int i = 0; i < datas.size(); i++){
            	MicroServiceModels msmodel = (MicroServiceModels) datas.get(i);
                if (!data.contains(msmodel.getModelName())){
                	data.add(msmodel.getModelName() + "-v" + msmodel.getVersion());
                }
            }
            model.put(microServiceModelsDictionaryDatas, mapper.writeValueAsString(data));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success");
            response.addHeader("operation", "getDictionary");
            response.getWriter().write(j.toString());

        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("error", "dictionaryDBQuery");
            LOGGER.error(e);
        }
    }

    @RequestMapping(value={"/get_MicroServiceModelsDataByClass"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
    public void getMicroServiceModelsDictionaryClassEntityData(HttpServletResponse response){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put("microServiceModelsDictionaryClassDatas", mapper.writeValueAsString(modelList));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.addHeader(successMapKey, successMsg);
            response.addHeader(operation, getDictionary);
            response.getWriter().write(j.toString());

        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, dictionaryDBQuery);
            LOGGER.error(e);
        }
    }

	@RequestMapping(value={"/ms_dictionary/save_model"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			boolean duplicateflag = false;
			boolean fromAPI = false;
			this.newModel = new MicroServiceModels();
			if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceModels microServiceModels = new MicroServiceModels();
			String userId = null;

			if(root.has("modelType")){
				JsonNode dataType = root.get("modelType");
				String modelType= dataType.toString();
				if(modelType.contains("yml")){
					if (root.has(microServiceModelsDictionaryData)){
						if (root.get(microServiceModelsDictionaryData).has(description)){
							microServiceModels.setDescription(root.get(microServiceModelsDictionaryData).get(description).asText().replace("\"", ""));
						}
						if (root.get(microServiceModelsDictionaryData).has(modelName)){
							microServiceModels.setModelName(root.get(microServiceModelsDictionaryData).get(modelName).asText().replace("\"", ""));
							this.newModel.setModelName(microServiceModels.getModelName());
						}
						if (root.get(microServiceModelsDictionaryData).has(version)){
							microServiceModels.setVersion(root.get(microServiceModelsDictionaryData).get(version).asText().replace("\"", ""));
							this.newModel.setVersion(microServiceModels.getVersion());
						}
					}

					classMap = new HashMap<>();
					JsonNode data = root.get(classMapData);
					ObjectMapper mapper1 = new ObjectMapper();
					String data1 = data.toString().substring(1, data.toString().length()-1);
					data1 = data1.replace("\\", "");
					data1=data1.replace("\"{","{");
					data1=data1.replace("}\"","}");
					JSONObject jsonObject = new JSONObject(data1);
					Set<String> keys = jsonObject.keySet();
					for(String key : keys){
						String value = jsonObject.get(key).toString();
						MSAttributeObject msAttributeObject = mapper1.readValue(value, MSAttributeObject.class);
						classMap.put(key, msAttributeObject);
					}

					userId = root.get("userid").textValue();
					MSAttributeObject mainClass = classMap.get(this.newModel.getModelName());
					this.newModel.setDependency("[]");
					String value = new Gson().toJson(mainClass.getSubClass());
					this.newModel.setSub_attributes(value);
					String attributes= mainClass.getAttribute().toString().replace("{", "").replace("}", "");
					int equalsIndexForAttributes= attributes.indexOf("=");
					String atttributesAfterFirstEquals= attributes.substring(equalsIndexForAttributes+1);
					this.newModel.setAttributes(atttributesAfterFirstEquals);
					String refAttributes= mainClass.getRefAttribute().toString().replace("{", "").replace("}", "");
					int equalsIndex= refAttributes.indexOf("=");
					String refAttributesAfterFirstEquals= refAttributes.substring(equalsIndex+1);
					this.newModel.setRef_attributes(refAttributesAfterFirstEquals);
					this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
					this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));

				}else{
					if (fromAPI) {
						microServiceModels = (MicroServiceModels)mapper.readValue(root.get(dictionaryFields).toString(), MicroServiceModels.class);
						userId = "API";

						//check if update operation or create, get id for data to be updated and update attributeData
						if ((update).equals(request.getParameter(operation))) {
							String checkName = microServiceModels.getModelName() + ":" + microServiceModels.getVersion();
							List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkName, "modelName:version", MicroServiceModels.class);
							int id = 0;
							for (int i=0; i< duplicateData.size(); i++){
								MicroServiceModels data = (MicroServiceModels) duplicateData.get(0);
								id = data.getId();
							}
							microServiceModels.setId(id);
							microServiceModels.setUserCreatedBy(this.getUserInfo(userId));

						}
					} else {
						if (root.has(microServiceModelsDictionaryData)){
							if (root.get(microServiceModelsDictionaryData).has(description)){
								microServiceModels.setDescription(root.get(microServiceModelsDictionaryData).get(description).asText().replace("\"", ""));
							}
							if (root.get(microServiceModelsDictionaryData).has(modelName)){
								microServiceModels.setModelName(root.get(microServiceModelsDictionaryData).get(modelName).asText().replace("\"", ""));
								this.newModel.setModelName(microServiceModels.getModelName());
							}
							if (root.get(microServiceModelsDictionaryData).has(version)){
								microServiceModels.setVersion(root.get(microServiceModelsDictionaryData).get(version).asText().replace("\"", ""));
								this.newModel.setVersion(microServiceModels.getVersion());
							}
						}
						if(root.has(classMapData)){
							classMap = new HashMap<>();
							JsonNode data = root.get(classMapData);
							ObjectMapper mapper1 = new ObjectMapper();
							String data1 = data.toString().substring(1, data.toString().length()-1);
							data1 = data1.replace("\\", "");
							JSONObject jsonObject = new JSONObject(data1);
							Set<String> keys = jsonObject.keySet();
							for(String key : keys){
								String value = jsonObject.get(key).toString();
								MSAttributeObject msAttributeObject = mapper1.readValue(value, MSAttributeObject.class);
								classMap.put(key, msAttributeObject);
							}
						}
						userId = root.get("userid").textValue();
						addValuesToNewModel(classMap);
					}
				}

			}
			microServiceModels.setAttributes(this.newModel.getAttributes());
			microServiceModels.setRef_attributes(this.newModel.getRef_attributes());
			microServiceModels.setDependency(this.newModel.getDependency());
			microServiceModels.setModelName(this.newModel.getModelName());
			microServiceModels.setSub_attributes(this.newModel.getSub_attributes());
			microServiceModels.setVersion(this.newModel.getVersion());
			microServiceModels.setEnumValues(this.newModel.getEnumValues());
			microServiceModels.setAnnotation(this.newModel.getAnnotation());

			if(microServiceModels.getId() == 0){
				String checkName = microServiceModels.getModelName() + ":" + microServiceModels.getVersion();
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(checkName, "modelName:version", MicroServiceModels.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					microServiceModels.setUserCreatedBy(this.getUserInfo(userId));
					commonClassDao.save(microServiceModels);
				}
			}else{
				commonClassDao.update(microServiceModels);
			}
			String responseString = "";
			if(duplicateflag){
				responseString = duplicateResponseString;
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceModels.class));
			}

			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
					responseString = successMessage;
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding(utf8);
				response.setContentType(applicationJsonContentType);
				request.setCharacterEncoding(utf8);

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{microServiceModelsDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_msModel"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceModels microServiceModels = (MicroServiceModels)mapper.readValue(root.get("data").toString(), MicroServiceModels.class);
			commonClassDao.delete(microServiceModels);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(MicroServiceModels.class));
			JSONObject j = new JSONObject("{microServiceModelsDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	private void addValuesToNewModel(HashMap<String,MSAttributeObject > classMap) {
		//Loop  through the classmap and pull out the required info for the new file.
		String subAttribute = null;

		MSAttributeObject mainClass = classMap.get(this.newModel.getModelName());

		if (mainClass !=null){
			String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
			ArrayList<String> dependency = new ArrayList<>(Arrays.asList(dependTemp.split(",")));
			dependency = getFullDependencyList(dependency);
			for (String element : dependency){
				MSAttributeObject temp = new MSAttributeObject();
				temp = classMap.get(element);
				if (temp!=null){
					mainClass.addAllRefAttribute(temp.getRefAttribute());
					mainClass.addAllAttribute(temp.getAttribute());
				}
			}
			subAttribute = utils.createSubAttributes(dependency, classMap, this.newModel.getModelName());
		}else{
			subAttribute = "{}";
			this.newModel.setDependency("");
		}

		if (mainClass != null && mainClass.getDependency()==null){
			mainClass.setDependency("");
		}
		if(mainClass != null){
			this.newModel.setDependency(mainClass.getDependency());
			this.newModel.setSub_attributes(subAttribute);
			this.newModel.setAttributes(mainClass.getAttribute().toString().replace("{", "").replace("}", ""));
			this.newModel.setRef_attributes(mainClass.getRefAttribute().toString().replace("{", "").replace("}", ""));
			this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
			this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
		}
	}

	private ArrayList<String> getFullDependencyList(ArrayList<String> dependency) {
		ArrayList<String> returnList = new ArrayList<>();
		ArrayList<String> workingList = new ArrayList<>();
		returnList.addAll(dependency);
		for (String element : dependency ){
			if (classMap.containsKey(element)){
				MSAttributeObject value = classMap.get(element);
				String rawValue = StringUtils.replaceEach(value.getDependency(), new String[]{"[", "]"}, new String[]{"", ""});
				workingList = new ArrayList<>(Arrays.asList(rawValue.split(",")));
				for(String depend : workingList){
					if (!returnList.contains(depend) && !depend.isEmpty()){
						returnList.add(depend.trim());
						//getFullDepedency(workingList)
					}
				}
			}
		}

		return returnList;
	}

}
