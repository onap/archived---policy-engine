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
app.controller('policyRolesController', function ($scope, PolicyAppService, modalService, $modal, Notification){
    $( "#dialog" ).hide();
    
    $scope.isDisabled = true;
    
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
	 
    $scope.scopeDatas = [];
    PolicyAppService.getData('get_RolesData').then(function (data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.rolesDatas = JSON.parse($scope.data.rolesDatas);
        console.log($scope.rolesDatas);
    }, function (error) {
        console.log("failed");
    });

    $scope.rolesTableGrid = {
        data : 'rolesDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, 
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editRolesWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button>' ,  width: '4%'
        },
        { field: 'loginId.userName', displayName : 'Name', sort: { direction: 'asc', priority: 0 }},
        { field: 'scope', displayName : 'Scope' },
        { field: 'role', displayName : 'Role' }
        ]
    };


    $scope.editRoleName = null;
   
    $scope.editRolesWindow = function(editRoleData) {
    	if($scope.lockdowndata[0].lockdown == true){
    		Notification.error("Policy Application has been Locked")
    	}else{
    		$scope.editRoleName = editRoleData;
    		var modalInstance = $modal.open({
    			backdrop: 'static', keyboard: false,
    			templateUrl : 'edit_Role_popup.html',
    			controller: 'editRoleController',
    			resolve: {
    				message: function () {
    					var message = {
    							editRoleData: $scope.editRoleName
    					};
    					return message;
    				}
    			}
    		});
    		modalInstance.result.then(function(response){
    			console.log('response', response);
    		});
    	}
       
    };

});