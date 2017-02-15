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

import org.json.JSONObject;
import org.openecomp.portalapp.model.Result;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Suggest;
import io.searchbox.core.SuggestResult;
import io.searchbox.params.Parameters;

/**
 * Controller for views that demonstrate Elastic Search features.
 */
@RestController
public class ElasticSearchController extends RestrictedBaseController{
	 
	@RequestMapping(value = {"/es_search_demo" }, method = RequestMethod.GET)
	public ModelAndView search() {
		return new ModelAndView("es_search_demo");	
	}
	
	@RequestMapping(value = {"/es_suggest_demo" }, method = RequestMethod.GET)
	public ModelAndView suggest() {
		return new ModelAndView("es_suggest_demo");	
	}
	
	@RequestMapping(value="/es_suggest/{task}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)  	
	public ResponseEntity<Result> doSuggest(@PathVariable("task") String task) throws IOException {
		JSONObject obj = new JSONObject(task);
		String searchTerm = obj.getString("data");
		String searchSize = obj.getString("size");
		String searchFuzzy = obj.getString("fuzzy");
		String resultName = obj.getString("resultname");

		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig
			.Builder("http://todo_elastic_search_server")
		    .multiThreaded(true)
		    .build());
		JestClient client = factory.getObject();
		
		
		Suggest suggest = new Suggest.Builder("{\n"
			+"\"" + resultName +"\" : {\n"
				+"\"text\" : \""+ searchTerm +"\",\n"
				+"\"completion\" : {\n"
				+"\"field\" : \"suggest\",\n"
				+"\"size\" : " + searchSize + ",\n"
				+"\"fuzzy\" : \"" + searchFuzzy + "\"\n"
				+"}\n"
    		+"}\n"
		+"}").addIndex("customer").build();
		
		SuggestResult result = client.execute(suggest);
		System.err.println(result.getJsonObject().toString());
		return new ResponseEntity<Result>(new Result(result.getJsonObject().toString()),HttpStatus.OK);
	}
	
	@RequestMapping(value="/es_search/{task}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)  	
	public ResponseEntity<Result> doSearch(@PathVariable("task") String task) throws IOException {
		JSONObject obj = new JSONObject(task);
		String searchTerm = obj.getString("data");
		String searchSize = obj.getString("size");
		// String searchFuzzy = obj.getString("fuzzy");
		
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig
			.Builder("http://todo_elastic_search_server")
		    .multiThreaded(true)
		    .build());
		JestClient client = factory.getObject();
		
		Search search = new Search.Builder("{\n"
			+"\"query\" : {\n"
				+"\"query_string\" : {\n"
					+"\"query\" : \"name:"+ searchTerm +"\"\n"
				+"}\n"
    		+"}\n"
		+"}").addIndex("customer").setParameter(Parameters.SIZE,Integer.valueOf(searchSize)).build();
		
		SearchResult result = client.execute(search);
		System.err.println(result.getJsonObject().toString());
		return new ResponseEntity<Result>(new Result(result.getJsonObject().toString()),HttpStatus.OK);
	}
	
	public ResponseEntity<Result> sendResult(Result result) {
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	@Override
	public boolean isRESTfulCall() {
		return true;
	}
}
