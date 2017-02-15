/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

app.factory('DictionaryService', function ($http, $q) {
	return {
		
		getActionPolicyDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ActionPolicyDictData';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		},

		getAttributeDictionaryData: function(url) {
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_AttributeData';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		},


		getEcompDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_EcompNameData';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		},


		getBRMSParamDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_BRMSParamData';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		},


		getDecisionSettingsDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_SettingsDictionaryData';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		},


		getEnforcerDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_EnforcerTypeData';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		},


		getDescriptiveDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_DescriptiveScope';
			return $http.get(url)
				.then(function(response) {
					if (typeof response.data === 'object') {
						return response.data;
					} else {
						return $q.reject(response.data);
					}

				}, function(response) {
					// something went wrong
					return $q.reject(response.data);
				});
		}
	};
});
