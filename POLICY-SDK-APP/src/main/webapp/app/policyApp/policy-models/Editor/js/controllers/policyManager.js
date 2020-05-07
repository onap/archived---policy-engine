/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
app.controller('PolicyManagerController', [
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
    PolicyAppService.getData('get_LockDownData').then(function(data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    if ($scope.lockdowndata[0].lockdown == true) {
         $scope.isDisabled = true;
    } else {
         $scope.isDisabled = false;
    }
    }, function(error) {
    });

    PolicyAppService.getData('getDictionary/get_DescriptiveScopeByName').then(function(data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.descriptiveScopeDictionaryDatas = JSON.parse($scope.data.descriptiveScopeDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function(data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_VSCLActionDataByName').then(function(data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.vsclActionDictionaryDatas = JSON.parse($scope.data.vsclActionDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_VNFTypeDataByName').then(function(data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.vnfTypeDictionaryDatas = JSON.parse($scope.data.vnfTypeDictionaryDatas);	
    });


    PolicyAppService.getData('get_UserRolesData').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.userRolesDatas = JSON.parse($scope.data.userRolesDatas);
    if ($scope.userRolesDatas[0] == 'super-admin') {
         $scope.superAdminId = true;
         $scope.exportPolicyId = true;
         $scope.importPolicyId = true;
        } else if ($scope.userRolesDatas[0] == 'super-editor' || $scope.userRolesDatas[0] == 'editor' || $scope.userRolesDatas[0] == 'admin') {
        $scope.exportPolicyId = true;
        $scope.importPolicyId = true; 
        }
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
    $scope.policyUploader = PolicyUploader;
    $scope.uploadFileList = [];

    $scope.setTemplate = function(name) {
    $scope.viewTemplate = $cookies.viewTemplate = name;
    };

    $scope.touch = function(item) {
    item = item instanceof Item ? item : new Item();
    item.revert();
    $scope.temp = item;
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
    if ($scope.temp.model.roleType == 'super-admin') {
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
    } else if ($scope.temp.model.roleType == 'super-editor' || $scope.temp.model.roleType == 'editor') {
         $scope.cloneId = true;
         $scope.editPolicyId = true;
         $scope.createPolicyId = true;
         $scope.switchVersionId = true;
         $scope.describePolicyId = true;
         $scope.viewPolicyId = true;
         $scope.deletePolicyId = true; 
    } else if ($scope.temp.model.roleType == 'super-guest' || $scope.temp.model.roleType == 'guest') {
         $scope.describePolicyId = true;
         $scope.viewPolicyId = true;
    } else if ($scope.temp.model.roleType == 'admin') {
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
      
         $scope.watchPolicy = function(item) {
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
        success : function(data) {
       $scope.$apply(function() {
      $scope.watchData=data.watchData;});
       Notification.success($scope.watchData);
        },
        error : function(data) {
      Notification.error("Error while saving.");
        }
        });
         };

         $scope.refresh = function() {
        $scope.policyNavigator.refresh();
         };

      $scope.switchVersion = function(item) {
     if ($scope.policyNavigator.fileNameExists(item.tempModel.content.activeVersion)) {
    item.error = 'Invalid filename or already exists, specify another name';
    return false;
     }
     item.getSwitchVersionContent().then(function(){
    $scope.policyNavigator.refresh();
    $scope.modal('switchVersion', true);
     });
      };

    $scope.copy = function(item) {
    var samePath = item.tempModel.path.join() === item.model.path.join();
    if (samePath && $scope.policyNavigator.fileNameExists(item.tempModel.name)) {
         item.error = 'Invalid filename or already exists, specify another name';
         return false;
    }
    item.copy().then(function() {
         $scope.policyNavigator.refresh();
         $scope.modal('copy', true);
    });
    };

    $scope.remove = function(item) {
    item.remove().then(function() {
         $scope.policyNavigator.refresh();
         $scope.modal('delete', true);
    });
    };

    $scope.removePolicy = function(item) {
    item.removePolicy().then(function() {
         $scope.policyNavigator.refresh();
         $scope.modal('deletePolicy', true);
    });
    };

    $scope.rename = function(item) {
    var samePath = item.tempModel.path.join() === item.model.path.join();
    if (samePath && $scope.policyNavigator.fileNameExists(item.tempModel.name)) {
         item.error = 'Invalid filename or already exists, specify another name';
         return false;
    }
    item.rename().then(function() {
         $scope.policyNavigator.refresh();
         $scope.modal('rename', true);
    });
    };

    $scope.move = function(item) {
    var samePath = item.tempModel.path.join() === item.model.path.join();
    if (samePath && $scope.policyNavigator.fileNameExists(item.tempModel.name)) {
         item.error = 'Invalid filename or already exists, specify another name';
         return false;
    }
    item.move().then(function() {
         $scope.policyNavigator.refresh();
         $scope.modal('move', true);
    });
    };

    $scope.createFolder = function(item) {
    var name = item.tempModel.name && item.tempModel.name.trim();
    item.tempModel.type = 'dir';
    item.tempModel.path = $scope.policyNavigator.currentPath;
    if (name && !$scope.policyNavigator.fileNameExists(name)) {
         item.createFolder().then(function() {
        $scope.policyNavigator.refresh();
        $scope.modal('newfolder', true);
         });
    } else {
         item.error = 'Invalid filename or already exists, specify another name';
         return false;
    }
    };

    $scope.subScopeFolder = function(item) {
    var name = item.tempModel.name +"\\" + item.tempModel.subScopename && item.tempModel.name.trim() + "\\"+item.tempModel.subScopename.trim() ;
    item.tempModel.type = 'dir';
    item.tempModel.path = $scope.policyNavigator.currentPath;
    if (name && !$scope.policyNavigator.fileNameExists(name)) {
         item.getScopeContent().then(function() {
        $scope.policyNavigator.refresh();
        $scope.modal('addSubScope', true);
         });
    } else {
         item.error = 'Invalid filename or already exists, specify another name';
         return false;
    }
    };

    $scope.closefunction = function(fianlPath) {
    $scope.policyNavigator.policyrefresh(fianlPath);
    };

    $scope.uploadFiles = function() {
    $scope.policyUploader.upload($scope.uploadFileList, $scope.policyNavigator.currentPath).then(function() {
         $scope.policyNavigator.refresh();
         Notification.success('Policy Import Complete');
         $scope.modal('uploadfile', true);
    }, function(data) {
         var errorMsg = data.result && data.result.error || 'Error Occured while Uploading....';
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

    $scope.isWindows = $scope.getQueryParam('server') === 'Windows';
    $scope.policyNavigator.refresh();
     }]);
