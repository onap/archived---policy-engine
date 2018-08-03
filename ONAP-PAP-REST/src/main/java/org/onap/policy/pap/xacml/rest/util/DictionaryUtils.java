/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pap.xacml.rest.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Category;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DictionaryUtils {

    private static final Log LOGGER	= LogFactory.getLog(DictionaryUtils.class);

    private static String apiflag = "apiflag";
    private static String operation = "operation";
    private static String duplicateResponseString = "Duplicate";
    private static String utf8 = "UTF-8";
    private static String applicationJsonContentType = "application / json";

    private static CommonClassDao commonClassDao;

    private static DictionaryUtils dictionaryUtils;

    public static synchronized DictionaryUtils getDictionaryUtils() {
        return dictionaryUtils != null ? dictionaryUtils : new DictionaryUtils();
    }

    public static synchronized void setDictionaryUtils(DictionaryUtils dictionaryUtils) {
        DictionaryUtils.dictionaryUtils = dictionaryUtils;
    }

    @Autowired
    public DictionaryUtils(CommonClassDao commonClassDao){
        DictionaryUtils.commonClassDao = commonClassDao;
    }

    public DictionaryUtils(){
        super();
    }

    public UserInfo getUserInfo(String loginId){
        return (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
    }

    public boolean isRequestFromAPI(HttpServletRequest request){
        return request.getParameter(apiflag)!=null && "api".equalsIgnoreCase(request.getParameter(apiflag));
    }

    public String appendKey(List<Object> objects, String key1, String appendValue){
        StringBuilder userValue = new StringBuilder();
        int counter = 0;
        for(Object attribute : objects){
            if(attribute instanceof LinkedHashMap<?, ?>){
                String key = ((LinkedHashMap<?, ?>) attribute).get(key1).toString();
                if(counter>0){
                    userValue.append(appendValue);
                }
                userValue.append(key);
                counter ++;
            }
        }
        return userValue.toString();
    }

    public String appendKeyValue(List<Object> objects, String append1, String append2){
        StringBuilder header = new StringBuilder();
        int counter = 0;
        for(Object attribute : objects){
            if(attribute instanceof LinkedHashMap<?, ?>){
                String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
                String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
                if(counter>0){
                    header.append(append1);
                }
                header.append(key).append(append2).append(value);
                counter ++;
            }
        }
        return header.toString();
    }

    public Datatype getDataType(String datatype){
        Datatype a = new Datatype();
        if("string".equalsIgnoreCase(datatype)){
            a.setId(26);
        }else if("integer".equalsIgnoreCase(datatype)){
            a.setId(12);
        }else if("boolean".equalsIgnoreCase(datatype)){
            a.setId(18);
        }else if("double".equalsIgnoreCase(datatype)){
            a.setId(25);
        }else if("user".equalsIgnoreCase(datatype)){
            a.setId(29);
        }
        return a;
    }

    public Category getCategory(){
        return (Category) commonClassDao.getDataById(Category.class, "shortName", "resource").get(0);
    }

    public ModelAndView getResultForApi(String inResponseString){
        String responseString = inResponseString;
        if(responseString!=null && !duplicateResponseString.equals(responseString)){
            responseString = "Success";
        }
        ModelAndView result = new ModelAndView();
        result.setViewName(responseString);
        return result;
    }

    public void setResponseData(HttpServletResponse response, String key, String responseString) throws IOException{
        response.setCharacterEncoding(utf8);
        response.setContentType(applicationJsonContentType);

        PrintWriter out = response.getWriter();
        JSONObject j = new JSONObject("{"+key+":" + responseString + "}");
        out.write(j.toString());
    }

    public void setErrorResponseData(HttpServletResponse response, Exception e) throws IOException{
        LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
        response.setCharacterEncoding(utf8);
        PrintWriter out = response.getWriter();
        out.write(e.getMessage());
    }

    @SuppressWarnings("rawtypes")
    public void getDataByEntity(HttpServletResponse response, String key, String value, Class className){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put(key, mapper.writeValueAsString(commonClassDao.getDataByColumn(className, value)));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.getWriter().write(j.toString());
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
        }
    }

    @SuppressWarnings("rawtypes")
    public void getData(HttpServletResponse response, String key, Class className){
        try{
            Map<String, Object> model = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            model.put(key, mapper.writeValueAsString(commonClassDao.getData(className)));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader(operation, "getDictionary");
            response.getWriter().write(j.toString());
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
        }
    }

    @SuppressWarnings("unchecked")
    public void removeData(HttpServletRequest request, HttpServletResponse response, String key, @SuppressWarnings("rawtypes") Class className) throws IOException{
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            commonClassDao.delete((Object)mapper.readValue(root.get("data").toString(), className));
            String responseString = mapper.writeValueAsString(commonClassDao.getData(className));
            setResponseData(response, key, responseString);
        }catch(Exception e){
            setErrorResponseData(response, e);
        }
    }

}
