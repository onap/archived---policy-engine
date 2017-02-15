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
app.factory('CLDictionaryService', function ($http, $q) {
    return {

        getPepOptionsDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PEPOptionsData';
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

        getVSCLActionDictionaryData: function(url) {
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_VSCLActionData';
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


        getVnfTypeDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_VNFTypeData';
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


        getServiceDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ClosedLoopServicesData';
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


        getSiteDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ClosedLoopSiteData';
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


        getVarbindDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_VarbindDictionaryData';
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