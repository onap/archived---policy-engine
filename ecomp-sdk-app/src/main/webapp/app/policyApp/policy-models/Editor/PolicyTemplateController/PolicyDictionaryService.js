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
angular.module("abs").factory('PolicyDictionaryService', function ($http, $q) {
    return {
    	    		
        getActionPolicyDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ActionPolicyDictDataByName';
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
        
        getFunctionDefinitionData : function(){
            return $http.get('get_FunctionDefinitionDataByName')
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
			var url = url+'/ecomp/get_AttributeDatabyAttributeName';
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
			var url = url+'/ecomp/get_EcompNameDataByName';
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
			var url = url+'/ecomp/get_BRMSParamDataByName';
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
			var url = url+'/ecomp/get_SettingsDictionaryDataByName';
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
			var url = url+'/ecomp/get_DescriptiveScopeByName';
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
 
	    getPepOptionsDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_PEPOptionsDataByName';
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
			var url = url+'/ecomp/get_VSCLActionDataByName';
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
			var url = url+'/ecomp/get_VNFTypeDataByName';
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
			var url = url+'/ecomp/get_ClosedLoopServicesDataByName';
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
			var url = url+'/ecomp/get_ClosedLoopSiteDataByName';
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
			var url = url+'/ecomp/get_VarbindDictionaryDataByName';
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
			var url = url+'/ecomp/get_SecurityZoneDataByName';
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
			var url = url+'/ecomp/get_TermListDataByName';
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
	
	    getPepOptionsDictionaryDataEntity : function(url){
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

    getFWDictionaryListDictionaryData : function(url){
    	var url = url+'/ecomp/get_FWDictionaryListDataByName';
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
    
    getFWParentListDictionaryData : function(){
        return $http.get('get_FWParentListDataByName')
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
    
        getMSConfigDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_MicroServiceConfigNameDataByName';
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

        getMSLocationDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_MicroServiceLocationDataByName';
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

        getMSServiceModelsDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_MicroServiceModelsDataByName';
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

        getDCAEuuidDataByName : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_DCAEUUIDDataByName';
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

        getPriorityValueData : function(){
            return $http.get('get_DCAEPriorityValues')
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

        getPolicyScopeData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_GroupPolicyScopeDataByName';
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
        
        getRiskTypeDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_RiskTypeDataByName';
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
        getRiskLevelValueData : function(){
            return $http.get('get_RiskLevelValues')
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
        getGuardValueData : function(){
            return $http.get('get_GuardlValues')
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
        getSafePolicyWarningDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_SafePolicyWarningDataByName';
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
        getRCAlarmDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_RCAlarmDataByName';
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
        getServerScopeDictionaryData : function(url){
			console.log("url on the service js: " + url);
			var url = url+'/ecomp/get_ServerScopeDataByName';
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