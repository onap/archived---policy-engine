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

/**
 */
app.factory('PolicyScopeService', function ($http, $q) {
    return {

        getPSClosedLoopDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSClosedLoopData';
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
        
        getPSClosedLoopDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSClosedLoopDataByName';
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

        getPSServiceDictionaryData: function(url) {
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSServiceData';
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

        getPSServiceDictionaryDataByName: function(url) {
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSServiceDataByName';
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


        getPSTypeDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSTypeData';
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

        getPSTypeDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSTypeDataByName';
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


        getPSResourceDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSResourceData';
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
        
        getPSResourceDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PSResourceDataByName';
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


        getPSGroupPolicyScopeDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_GroupPolicyScopeData';
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