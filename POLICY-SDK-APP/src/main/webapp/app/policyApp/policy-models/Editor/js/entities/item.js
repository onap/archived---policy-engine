/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
angular.module('abs').factory('item', ['$http', '$q', 'policyManagerConfig', function($http, $q, policyManagerConfig) {

        var Item = function(model, path) {
            var rawModel = {
                name: model && model.name || '',
                subScopename: model && model.subScopename || '',
                path: path || [],
                type: model && model.type || 'file',
                size: model && parseInt(model.size || 0),
                date: model && model.date,
                version: model && model.version || '',
                createdBy: model && model.createdBy || '',
                modifiedBy: model && model.modifiedBy || '',
                content: model && model.content || '',
                recursive: false,
                sizeKb: function() {
                    return Math.round(this.size / 1024, 1);
                },
                fullPath: function() {
                    if(this.version == ""){
                        return ('/' + this.path.join('/') + '/' + this.name).replace(/\/\//, '/');
                    }else{
                        return ('/' + this.path.join('/') + '/' + this.name + '.' + this.version + '.xml').replace(/\/\//, '/');
                    }
                }
            };

            this.error = '';
            this.inprocess = false;

            this.model = angular.copy(rawModel);
            this.tempModel = angular.copy(rawModel);

            function parseMySQLDate(mysqlDate) {
                var d = (mysqlDate || '').toString().split(/[- :]/);
                return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5]);
            }
        };

        Item.prototype.update = function() {
            angular.extend(this.model, angular.copy(this.tempModel));
        };

        Item.prototype.revert = function() {
            angular.extend(this.tempModel, angular.copy(this.model));
            this.error = '';
        };

        Item.prototype.deferredHandler = function(data, deferred, defaultMsg) {
            if (!data || typeof data !== 'object') {
                this.error = 'Bridge response error, please check the docs';
            }
            if (data.result && data.result.error) {
                this.error = data.result.error;
            }
            if (!this.error && data.error) {
                this.error = data.error.message;
            }
            if (!this.error && defaultMsg) {
                this.error = defaultMsg;
            }
            if (this.error) {
                return deferred.reject(data);
            }
            this.update();
            return deferred.resolve(data);
        };
		
        Item.prototype.createFolder = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'ADDFOLDER',
                path: self.tempModel.path.join('/'),
                name: self.tempModel.name
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.createFolderUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While Creating Scope');
            })['finally'](function() {
                self.inprocess = false;
            });
        
            return deferred.promise;
        };

        Item.prototype.rename = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'RENAME',
                path: self.model.fullPath(),
                newPath: self.tempModel.fullPath()
            }};
            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.renameUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While Renaming');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        
        Item.prototype.move = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'RENAME',
                path: self.model.fullPath(),
                newPath: self.tempModel.fullPath()
            }};
            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.renameUrl, data).success(function(data) {
            	if(data.result.error != undefined){
            		var value = data.result.error;
            		value = value.replace("rename" , "move");
            		data.result.error = value;
            	}
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While Moving');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
        
        Item.prototype.copy = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'COPY',
                path: self.model.fullPath(),
                newPath: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.copyUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While Cloning');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        
        Item.prototype.getContent = function(policyNavigator) {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'EDITFILE',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.getContentUrl, data).success(function(data) {
                self.tempModel.content =  self.model.content = data.result;
                var json = data.result;
                var policy = JSON.parse(json);
                self.policy = policy;
                self.itemContent = policyNavigator;
                console.log(policy);
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While retrieving the Policy Data');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.getViewPolicyContent = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'VIEWPOLICY',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.viewPolicyUrl, data).success(function(data) {
                self.tempModel.content =  self.model.content = data.result;
                var json = data.result;
                var policy = JSON.parse(json);
                self.policy = policy;
                console.log(data.result);
                console.log(policy);
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While retrieving the Policy Data');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.getSwitchVersionContent = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'SWITCHVERSION',
                path: self.tempModel.fullPath(),
                activeVersion : self.tempModel.content.activeVersion,
                highestVersion : self.tempModel.content.highestVersion
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.switchVersionUrl, data).success(function(data) {
                self.tempModel.content =  self.model.content = data;
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While retrieving the Switch Version Data');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
        
        Item.prototype.getDescribePolicyContent = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'DESCRIBEPOLICYFILE',
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.describePolicyUrl, data).success(function(data) {
                self.tempModel.content =  self.model.content = data.html;
                var describeTemplate =  self.tempModel.content;
             
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While retrieving the Policy Data to Describe');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.getScopeContent = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'ADDSUBSCOPE',
                name: self.tempModel.name,
                subScopename: self.tempModel.subScopename,
                path: self.tempModel.fullPath()
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.addSubScopeUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While Creating the Sub Scope');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.remove = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'DELETE',
                path: self.tempModel.fullPath(),
                deleteVersion : self.model.versions
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.removeUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While retrieving the Deleting Scope');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };
        
        Item.prototype.removePolicy = function() {
            var self = this;
            var deferred = $q.defer();
            var data = {params: {
                mode: 'DELETE',
                path: self.tempModel.fullPath(),
                deleteVersion : self.model.versions
            }};

            self.inprocess = true;
            self.error = '';
            $http.post(policyManagerConfig.removeUrl, data).success(function(data) {
                self.deferredHandler(data, deferred);
            }).error(function(data) {
                self.deferredHandler(data, deferred, 'Error Occured While retrieving the Deleting Policy');
            })['finally'](function() {
                self.inprocess = false;
            });
            return deferred.promise;
        };

        Item.prototype.isFolder = function() {
            return this.model.type === 'dir';
        };

        Item.prototype.isEditable = function() {
            return !this.isFolder() && policyManagerConfig.isEditableFilePattern.test(this.model.name);
        };

        return Item;
    }]);
