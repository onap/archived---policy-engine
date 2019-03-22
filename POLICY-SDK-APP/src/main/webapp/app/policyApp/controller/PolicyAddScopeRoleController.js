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
app.controller('editRoleController' ,  function ($scope, PolicyAppService, $modalInstance, message){
    if (message.editRoleData != null) {
        $scope.label='Edit Role'
        $scope.disableCd=true;
    } else {
        $scope.label='Add Role'
        $scope.disableCd=false;
        message.editRoleData = {
            role : "mechid"
        }
    }

    $scope.editRole = message.editRoleData;

    $scope.activeScopes = [];
    if (message.editRoleData != null && message.editRoleData.scope != null) {
        if (message.editRoleData.scope.constructor === Array) {
            $scope.activeScopes = message.editRoleData.scope;
        } else {
            $scope.activeScopes = message.editRoleData.scope.split(',');
        }
    }

    PolicyAppService.getData('get_PolicyRolesScopeData').then(function (data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.scopeDatas = JSON.parse($scope.data.scopeDatas);
        console.log($scope.scopeDatas);
    }, function (error) {
        console.log("failed");
    });

    $scope.saveRole = function(editRoleData) {
        var uuu = "save_NonSuperRolesData.htm";
        editRoleData.scope = $scope.activeScopes;
        var postData={editRoleData: editRoleData};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.rolesDatas=data.rolesDatas;});
                console.log($scope.rolesDatas);
                $modalInstance.close({rolesDatas:$scope.rolesDatas});
            },
            error : function(data){
                alert("Error while saving Role.");
            }
        });
    };


    $scope.createMechidScope = function(editRoleData) {
        var uuu = "save_NewMechidScopesData";
        editRoleData.scope = $scope.activeScopes;
        var postData={editRoleData: editRoleData};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.rolesDatas=data.rolesDatas;});
                console.log($scope.rolesDatas);
                $modalInstance.close({rolesDatas:$scope.rolesDatas});
            },
            error : function(data) {
                alert("Error while Creating Mechid scopes.");
            }
        });
    };



    $scope.addScope = function(scopes) {
        for (var i = 0; i < scopes.length; i++) {
            if ($.inArray(scopes[i], $scope.activeScopes) === -1) {
                $scope.activeScopes.push(scopes[i]);
            }
        }
    };
    $scope.deleteScope = function(index) {
        $scope.activeScopes.splice(index, 1);
    };
});