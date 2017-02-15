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
app.factory('FWDictionaryService', function ($http, $q) {
    return {

        getActionListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ActionListData';
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

        getAddressGroupDictionaryData: function(url) {
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_AddressGroupData';
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

        getParentListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_FWDictionaryListData';
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

        getPortListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PortListData';
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


        getPrefixListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PrefixListData';
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


        getProtocolListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ProtocolListData';
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


        getSecurityZoneDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_SecurityZoneData';
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


        getServiceGroupDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ServiceGroupData';
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


        getServiceListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ServiceListData';
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


        getTermListDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_TermListData';
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


        getZoneDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ZoneData';
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
        
        getPrefixListDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PrefixListDataByName';
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
        
        getProtocolListDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ProtocolListDataByName';
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
        
        getZoneDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ZoneDictionaryDataByName';
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
        
        getAddressGroupDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_AddressGroupDictionaryDataByName';
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
        
        getServiceListDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ServiceListDictionaryDataByName';
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
        
        getServiceGroupDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ServiceGroupDictionaryDataByName';
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
        
        getActionListDictionaryDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ActionListDictionaryDataByName';
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