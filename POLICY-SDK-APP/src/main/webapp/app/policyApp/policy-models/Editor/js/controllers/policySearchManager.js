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
app.controller('PolicySearchController', [
        '$scope', '$q', '$window', '$cookies', 'policyManagerConfig', 'item', 'policyNavigator', 'policyUploader', 'Notification','PolicyAppService',
        function($scope, $q, $Window, $cookies, policyManagerConfig, Item, PolicyNavigator, PolicyUploader, Notification, PolicyAppService ) {
        	
        $scope.isDisabled = true;
        $scope.superAdminId = false;
        $scope.exportPolicyId = false;
        $scope.importPolicyId = false;
        $scope.createScopeId = false;
        $scope.deleteScopeId = false;
        $scope.renameId = false;
        $scope.createPolicyId = false;
        $scope.cloneId = false;
        $scope.editPolicyId = false;
        $scope.switchVersionId = false;
        $scope.describePolicyId = false;
        $scope.viewPolicyId = false;
        $scope.deletePolicyId = false;
        PolicyAppService.getData('get_LockDownData').then(function(data){
        	var j = data;
        	$scope.data = JSON.parse(j.data);
        	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
        	if($scope.lockdowndata[0].lockdown == true){
        		$scope.isDisabled = true;
        	}else{
        		$scope.isDisabled = false;
        	}
        	console.log($scope.data);
        },function(error){
        	console.log("failed");
        });
        
        PolicyAppService.getData('getDictionary/get_DescriptiveScopeByName').then(function(data){
        	var j = data;
        	$scope.data = JSON.parse(j.data);
        	console.log($scope.data);
        	$scope.descriptiveScopeDictionaryDatas = JSON.parse($scope.data.descriptiveScopeDictionaryDatas);	
        }, function (error) {
        	console.log("failed");
        });

        PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function(data){
        	var j = data;
        	$scope.data = JSON.parse(j.data);
        	console.log($scope.data);
        	$scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);	
        }, function (error) {
        	console.log("failed");
        });

        PolicyAppService.getData('getDictionary/get_VSCLActionDataByName').then(function(data){
        	var j = data;
        	$scope.data = JSON.parse(j.data);
        	console.log($scope.data);
        	$scope.vsclActionDictionaryDatas = JSON.parse($scope.data.vsclActionDictionaryDatas);	
        }, function (error) {
        	console.log("failed");
        });

        PolicyAppService.getData('getDictionary/get_VNFTypeDataByName').then(function(data){
        	var j = data;
        	$scope.data = JSON.parse(j.data);
        	console.log($scope.data);
        	$scope.vnfTypeDictionaryDatas = JSON.parse($scope.data.vnfTypeDictionaryDatas);	
        }, function (error) {
        	console.log("failed");
        });

        
        PolicyAppService.getData('get_UserRolesData').then(function (data) {
        	var j = data;
        	$scope.data = JSON.parse(j.data);
        	console.log($scope.data);
        	$scope.userRolesDatas = JSON.parse($scope.data.userRolesDatas);
        	console.log($scope.userRolesDatas);
        	if($scope.userRolesDatas[0] == 'super-admin'){
        		$scope.superAdminId = true;
        		$scope.createPolicyId = true; 
        		$scope.editPolicyId = true;
        		$scope.describePolicyId = true;
        		$scope.viewPolicyId = true;
        	}else if($scope.userRolesDatas[0] == 'super-editor' || $scope.userRolesDatas[0] == 'editor' || $scope.userRolesDatas[0] == 'admin'){
        		$scope.editPolicyId = true;
        		$scope.createPolicyId = true;
        		$scope.describePolicyId = true;
        		$scope.viewPolicyId = true;
        	}else if($scope.userRolesDatas[0] == 'super-guest' || $scope.userRolesDatas[0] == 'guest'){
        		$scope.describePolicyId = true;
        		$scope.viewPolicyId = true;
        	}
        }, function (error) {
     	      console.log("failed");
     	});
        
        $scope.config = policyManagerConfig;
        $scope.reverse = false;
        $scope.predicate = ['model.type', 'model.name'];
        $scope.order = function(predicate) {
            $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
            $scope.predicate[1] = predicate;
        };

        $scope.query = '';
        $scope.temp = new Item();
        $scope.policyNavigator = new PolicyNavigator();

        $scope.setTemplate = function(name) {
            $scope.viewTemplate = $cookies.viewTemplate = name;
        };

        $scope.touch = function(item) {
            item = item instanceof Item ? item : new Item();
            item.revert();
            $scope.temp = item;
        };

        $scope.smartClick = function(item) {
            if (item.isFolder()) {
                return $scope.policyNavigator.folderClick(item);
            }
            if (item.isEditable()) {
                return $scope.openEditItem(item);
            }
        };

        $scope.openEditItem = function(item) {
            item.getContent();
            $scope.modal('createNewPolicy');
            return $scope.touch(item);
        };

        $scope.modal = function(id, hide) {
            return $('#' + id).modal(hide ? 'hide' : 'show');
        };

        $scope.isInThisPath = function(path) {
            var currentPath = $scope.policyNavigator.currentPath.join('/');
            return currentPath.indexOf(path) !== -1;
        };
       
        $scope.searchPolicy = function(searchContent){
        	if(searchContent != undefined){
        		var uuu = "searchPolicy";
        		var postData = {searchdata : searchContent};
        		$.ajax({
        			type : 'POST',
        			url : uuu,
        			dataType: 'json',
        			contentType: 'application/json',
        			data: JSON.stringify(postData),
        			success : function(data){
        				$scope.$apply(function(){
        					var searchdata = data.result;
        					if(searchdata.length > 0){
        						if(searchdata[0] == "Exception"){
        							Notification.error(searchdata[1]);
        						}else{
        							$scope.policyNavigator.searchrefresh(searchdata);  
        						}
        					}else{
        						Notification.info("No Matches Found with your Search");
        					}
        				});     
        			},
        			error : function(data){
        				Notification.error("Error while Searching.");
        			}
        		});
        	}else{
        		Notification.error("No data has been entered or selected to search");
        	}
        };
       
       $scope.refresh = function(searchData){
    	   $scope.policyNavigator.searchrefresh(null);
       };
         
        $scope.getQueryParam = function(param) {
            var found;
            window.location.search.substr(1).split('&').forEach(function(item) {
                if (param ===  item.split('=')[0]) {
                    found = item.split('=')[1];
                    return false;
                }
            });
            return found;
        };

        $scope.isWindows = $scope.getQueryParam('server') === 'Windows';
        $scope.policyNavigator.searchrefresh(null);
        $scope.policyNavigator.setSearchModalActiveStatus();
    }]);
