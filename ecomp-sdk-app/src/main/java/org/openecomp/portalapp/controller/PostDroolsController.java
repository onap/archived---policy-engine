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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.command.PostDroolsBean;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.service.PostDroolsService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class PostDroolsController extends RestrictedBaseController{

	@Autowired
	private PostDroolsService  postDroolsService;
	
	@RequestMapping(value = {"/drools" }, method = RequestMethod.GET)
	public ModelAndView drools(HttpServletRequest request) {
		
	      return new ModelAndView(getViewName());		
	}
	
	
	@RequestMapping(value = {"/getDrools" }, method = RequestMethod.GET)
	public void getDrools(HttpServletRequest request, HttpServletResponse response) {
		 // Map<String, Object> model = new HashMap<String, Object>();
	
	      ObjectMapper mapper = new ObjectMapper();	
			try {
				List<PostDroolsBean>  beanList = postDroolsService.fetchDroolBeans();
				JsonMessage msg = new JsonMessage(mapper.writeValueAsString(beanList));
				JSONObject j = new JSONObject(msg);
				response.getWriter().write(j.toString());
				
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@RequestMapping(value = {"/getDroolDetails" }, method = RequestMethod.GET)
	public void getDroolDetails(HttpServletRequest request, HttpServletResponse response) {
		
	      ObjectMapper mapper = new ObjectMapper();	
			try {
				
				PostDroolsBean postDroolsBean = new PostDroolsBean();
				String selectedFile = request.getParameter("selectedFile");
				postDroolsBean.setDroolsFile(selectedFile);//sample populated
				//postDroolsBean.setSelectedRules("[\"NJ\",\"NY\",\"KY\"]");
				postDroolsBean.setClassName(postDroolsService.retrieveClass(selectedFile));
				
				JsonMessage msg = new JsonMessage(mapper.writeValueAsString(postDroolsBean));
				JSONObject j = new JSONObject(msg);
				response.getWriter().write(j.toString());
				
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
    
	@RequestMapping(value = {"/post_drools/execute" }, method = RequestMethod.POST)
    public ModelAndView search(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
    	try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PostDroolsBean postDroolsBean = mapper.readValue(root.get("postDroolsBean").toString(), PostDroolsBean.class);
			
			String resultsString = postDroolsService.execute(postDroolsBean.getDroolsFile(), postDroolsBean.getClassName(), postDroolsBean.getSelectedRules());
			    
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			//String responseString = mapper.writeValueAsString(resultsString);
			JSONObject j = new JSONObject("{resultsString: "+resultsString+"}");
			
			out.write(j.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;	
    }
	
   
}
