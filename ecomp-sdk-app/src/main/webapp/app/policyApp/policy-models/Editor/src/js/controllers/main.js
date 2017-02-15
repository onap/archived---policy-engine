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

(function(window, angular, $) {
    'use strict';
    angular.module('abs').controller('FileManagerCtrl', [
        '$scope', '$q', '$window', '$translate', '$cookies', 'fileManagerConfig', 'item', 'fileNavigator', 'fileUploader', 'AdminTabService', 'RolesService', 'Notification', 'PolicyDictionaryService', 'PapUrlService',
        function($scope, $q, $Window, $translate, $cookies, fileManagerConfig, Item, FileNavigator, FileUploader, AdminTabService, RolesService, Notification, PolicyDictionaryService, PapUrlService ) {
        	
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
        AdminTabService.getData().then(function(data){
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
        var papUrl;
        PapUrlService.getPapUrl().then(function(data) {
        	var config = data;
        	papUrl = config.PAP_URL;
        	console.log(papUrl); 
        	PolicyDictionaryService.getDescriptiveDictionaryData(papUrl).then(function(data){
        		var j = data;
        		$scope.data = JSON.parse(j.data);
        		console.log($scope.data);
        		$scope.descriptiveScopeDictionaryDatas = JSON.parse($scope.data.descriptiveScopeDictionaryDatas);	
        	}, function (error) {
        		console.log("failed");
        	});
        	
        	PolicyDictionaryService.getEcompDictionaryData(papUrl).then(function(data){
        		var j = data;
        		$scope.data = JSON.parse(j.data);
        		console.log($scope.data);
        		$scope.ecompNameDictionaryDatas = JSON.parse($scope.data.ecompNameDictionaryDatas);	
        	}, function (error) {
        		console.log("failed");
        	});
        	
        	PolicyDictionaryService.getVSCLActionDictionaryData(papUrl).then(function(data){
        		var j = data;
        		$scope.data = JSON.parse(j.data);
        		console.log($scope.data);
        		$scope.vsclActionDictionaryDatas = JSON.parse($scope.data.vsclActionDictionaryDatas);	
        	}, function (error) {
        		console.log("failed");
        	});
        	
        	PolicyDictionaryService.getVnfTypeDictionaryData(papUrl).then(function(data){
        		var j = data;
        		$scope.data = JSON.parse(j.data);
        		console.log($scope.data);
        		$scope.vnfTypeDictionaryDatas = JSON.parse($scope.data.vnfTypeDictionaryDatas);	
        	}, function (error) {
        		console.log("failed");
        	});
        });
        
        RolesService.getRolesData().then(function (data) {
     	   var j = data;
     	   $scope.data = JSON.parse(j.data);
     	   console.log($scope.data);
     	   $scope.userRolesDatas = JSON.parse($scope.data.userRolesDatas);
     	   console.log($scope.userRolesDatas);
     	   if($scope.userRolesDatas[0] == 'super-admin'){
     		  $scope.superAdminId = true;
     		  $scope.exportPolicyId = true;
              $scope.importPolicyId = true;
     		  $scope.createScopeId = true;
     	      $scope.deleteScopeId = true;
     	      $scope.renameId = true;
     	      $scope.createPolicyId = true;
     	      $scope.cloneId = true;
     	      $scope.editPolicyId = true;
     	      $scope.switchVersionId = true;
     	      $scope.describePolicyId = true;
     	      $scope.viewPolicyId = true;
     	      $scope.deletePolicyId = true; 
     	   }else if($scope.userRolesDatas[0] == 'super-editor' || $scope.userRolesDatas[0] == 'editor'){
     		  $scope.exportPolicyId = true;
              $scope.importPolicyId = true; 
   	          $scope.cloneId = true;
   	          $scope.editPolicyId = true;
   	          $scope.createPolicyId = true;
	          $scope.cloneId = true;
	          $scope.editPolicyId = true;
	          $scope.switchVersionId = true;
	          $scope.describePolicyId = true;
	          $scope.viewPolicyId = true;
	          $scope.deletePolicyId = true; 
     	   }else if($scope.userRolesDatas[0] == 'super-guest' || $scope.userRolesDatas[0] == 'guest'){
     		  $scope.describePolicyId = true;
	          $scope.viewPolicyId = true;
     	   }else if($scope.userRolesDatas[0] == 'admin'){
     		  $scope.exportPolicyId = true;
              $scope.importPolicyId = true;
     		  $scope.createScopeId = true;
   	          $scope.renameId = true;
   	          $scope.createPolicyId = true;
   	          $scope.cloneId = true;
   	          $scope.editPolicyId = true;
   	          $scope.switchVersionId = true;
   	          $scope.describePolicyId = true;
   	          $scope.viewPolicyId = true;
   	          $scope.deletePolicyId = true;  
     	   }
     	   }, function (error) {
     	      console.log("failed");
     	});
        
        $scope.config = fileManagerConfig;
        $scope.reverse = false;
        $scope.predicate = ['model.type', 'model.name'];
        $scope.order = function(predicate) {
            $scope.reverse = ($scope.predicate[1] === predicate) ? !$scope.reverse : false;
            $scope.predicate[1] = predicate;
        };

        $scope.query = '';
        $scope.temp = new Item();
        $scope.fileNavigator = new FileNavigator();
        $scope.fileUploader = FileUploader;
        $scope.uploadFileList = [];
        $scope.viewTemplate = $cookies.viewTemplate || 'main-table.html';

        $scope.setTemplate = function(name) {
            $scope.viewTemplate = $cookies.viewTemplate = name;
        };

        $scope.changeLanguage = function (locale) {
            if (locale) {
                return $translate.use($cookies.language = locale);
            }
            $translate.use($cookies.language || fileManagerConfig.defaultLang);
        };

        $scope.touch = function(item) {
            item = item instanceof Item ? item : new Item();
            item.revert();
            $scope.temp = item;
        };

        $scope.smartClick = function(item) {
            if (item.isFolder()) {
                return $scope.fileNavigator.folderClick(item);
            }
            if (item.isImage()) {
                return $scope.openImagePreview(item);
            }
            if (item.isEditable()) {
                return $scope.openEditItem(item);
            }
        };

        $scope.openImagePreview = function(item) {
            item.inprocess = true;
            $scope.modal('imagepreview')
                .find('#imagepreview-target')
                .attr('src', item.getUrl(true))
                .unbind('load error')
                .on('load error', function() {
                    item.inprocess = false;
                    $scope.$apply();
                });
            return $scope.touch(item);
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
            var currentPath = $scope.fileNavigator.currentPath.join('/');
            return currentPath.indexOf(path) !== -1;
        };

        $scope.edit = function(item) {
            item.edit().then(function() {
                $scope.modal('createNewPolicy', true);
            });
        };

        $scope.createNewPolicy = function(item) {
             item.createNewPolicy().then(function() {
                  $scope.modal('createNewPolicy', true);
              });
         };
         
       $scope.watch = function(item){
           var uuu = "watchPolicy";
           var data = {name : item.model.name,
        		   path : item.model.path};
           var postData={watchData: data};
           $.ajax({
               type : 'POST',
               url : uuu,
               dataType: 'json',
               contentType: 'application/json',
               data: JSON.stringify(postData),
               success : function(data){
                   $scope.$apply(function(){
                       $scope.watchData=data.watchData;});
                   Notification.success($scope.watchData);
                   console.log($scope.watchData);
               },
               error : function(data){
                   alert("Error while saving.");
               }
           });
       };
       
	    $scope.Search = function(search){
		    var deferred = $q.defer();
           var uuu = "searchPolicy";
           var postData = {searchdata : search};
           $.ajax({
               type : 'POST',
               url : uuu,
               dataType: 'json',
               contentType: 'application/json',
               data: JSON.stringify(postData),
               success : function(data){
                   $scope.$apply(function(){
                       $scope.searchdata=data.result;});
                   if($scope.searchdata[0].error != undefined){
                	   Notification.info($scope.searchdata[0].error);
                   }else{
                	var j = data;  
           	        $scope.data = JSON.stringify(data.result);
                	   $scope.searchDatas = JSON.parse($scope.data);	   
						var searchString = "Policies List" + "<br>";
						var i;
						for(i = 0 ; i < $scope.searchDatas.length; i++){
							searchString += $scope.searchDatas[i].name + ".xml" + "<br>";
						}
						 var myWindow = window.open("", "MsgWindow", "width=500,height=500");
						 myWindow.document.write("<p>Search List</p>");
						 myWindow.document.write("<p>"+searchString+"</p>");
                   }      
               },
               error : function(data){
                   alert("Error while Searching.");
               }
           });
       };
	   
      
         $scope.describePolicy = function(item){
        	 item.describePolicy().then(function(){
        		 $scope.modal('describePolicy', true);
        	 });
         };
         
         $scope.exportPolicy = function(item){
        	 item.exportPolicy().then(function(){
        		 $scope.modal('exportPolicy', true);
        	 });
         };
         
         $scope.switchVersion = function(item){
        	 if ($scope.fileNavigator.fileNameExists(item.tempModel.content.activeVersion)) {
                 item.error = $translate.instant('error_invalid_filename');
                 return false;
             }
        	 item.getSwitchVersionContent().then(function(){
        		 $scope.fileNavigator.refresh();
        		 $scope.modal('switchVersion', true);
        	 });
         };


         $scope.viewPolicy = function(item){
        	 item.viewPolicy().then(function(){
        		 $scope.modal('createNewPolicy', true);
        	 });
         };

        $scope.copy = function(item) {
            var samePath = item.tempModel.path.join() === item.model.path.join();
            if (samePath && $scope.fileNavigator.fileNameExists(item.tempModel.name)) {
                item.error = $translate.instant('error_invalid_filename');
                return false;
            }
            item.copy().then(function() {
                $scope.fileNavigator.refresh();
                $scope.modal('copy', true);
            });
        };

        $scope.remove = function(item) {
            item.remove().then(function() {
                $scope.fileNavigator.refresh();
                $scope.modal('delete', true);
            });
        };

        $scope.removePolicy = function(item) {
            item.removePolicy().then(function() {
                $scope.fileNavigator.refresh();
                $scope.modal('deletePolicy', true);
            });
        };
        
        $scope.rename = function(item) {
            var samePath = item.tempModel.path.join() === item.model.path.join();
            if (samePath && $scope.fileNavigator.fileNameExists(item.tempModel.name)) {
                item.error = $translate.instant('error_invalid_filename');
                return false;
            }
            item.rename().then(function() {
                $scope.fileNavigator.refresh();
                $scope.modal('rename', true);
            });
        };
        
        $scope.move = function(item) {
            var samePath = item.tempModel.path.join() === item.model.path.join();
            if (samePath && $scope.fileNavigator.fileNameExists(item.tempModel.name)) {
                item.error = $translate.instant('error_invalid_filename');
                return false;
            }
            item.move().then(function() {
                $scope.fileNavigator.refresh();
                $scope.modal('move', true);
            });
        };

        $scope.createFolder = function(item) {
            var name = item.tempModel.name && item.tempModel.name.trim();
            item.tempModel.type = 'dir';
            item.tempModel.path = $scope.fileNavigator.currentPath;
            if (name && !$scope.fileNavigator.fileNameExists(name)) {
                item.createFolder().then(function() {
                    $scope.fileNavigator.refresh();
                    $scope.modal('newfolder', true);
                });
            } else {
                item.error = $translate.instant('error_invalid_filename');
                return false;
            }
        };

        $scope.subScopeFolder = function(item) {
        	var name = item.tempModel.name +"\\" + item.tempModel.subScopename && item.tempModel.name.trim() + "\\"+item.tempModel.subScopename.trim() ;
        	item.tempModel.type = 'dir';
        	item.tempModel.path = $scope.fileNavigator.currentPath;
        	if (name && !$scope.fileNavigator.fileNameExists(name)) {
        		item.getScopeContent().then(function() {
        			$scope.fileNavigator.refresh();
        			$scope.modal('addSubScope', true);
        		});
        	} else {
        		item.error = $translate.instant('error_invalid_filename');
        		return false;
        	}
        };

        $scope.uploadFiles = function() {
            $scope.fileUploader.upload($scope.uploadFileList, $scope.fileNavigator.currentPath).then(function() {
                $scope.fileNavigator.refresh();
                $scope.modal('uploadfile', true);
            }, function(data) {
                var errorMsg = data.result && data.result.error || $translate.instant('error_uploading_files');
                $scope.temp.error = errorMsg;
            });
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

        $scope.changeLanguage($scope.getQueryParam('lang'));
        $scope.isWindows = $scope.getQueryParam('server') === 'Windows';
        $scope.fileNavigator.refresh();
    }]);
})(window, angular, jQuery);
