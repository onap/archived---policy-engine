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
app.controller('psGroupPolicyScopeDictGridController', function ($scope, PolicyScopeService,modalService, $modal, PapUrlService){
    $( "#dialog" ).hide();
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    PolicyScopeService.getPSGroupPolicyScopeDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.groupPolicyScopeListDatas = JSON.parse($scope.data.groupPolicyScopeListDatas);
	        console.log($scope.groupPolicyScopeListDatas);
	    }, function (error) {
	        console.log("failed");
	    });
		
	});
	
    $scope.psGroupPolicyScopeDictionaryGrid = {
        data : 'groupPolicyScopeListDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewGroupPolicyScopeWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editGroupPolicyScopeWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteGroupPolicyScope(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'groupName', displayName : 'Policy Scope Group Name'},
        { field: 'groupList', displayName : 'Policy Scope List'}
        ],
        onRegisterApi: function(gridApi){
        	$scope.gridApi = gridApi;
        	$scope.gridApi.core.refresh();
        }
    };
   
    $scope.editPSGroupPolicyScope = null;
    $scope.createNewGroupPolicyScopeWindow = function(){
        $scope.editPSGroupPolicyScope = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_PSGroupPolicyScope_popup.html',
            controller: 'editPSGroupPolicyScopeController',
            resolve: {
                message: function () {
                    var message = {
                    		groupPolicyScopeListDatas: $scope.editPSGroupPolicyScope
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.groupPolicyScopeListDatas=response.groupPolicyScopeListDatas;
        });
    };

    $scope.editGroupPolicyScopeWindow = function(groupPolicyScopeListData) {
        $scope.editPSGroupPolicyScope = groupPolicyScopeListData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_PSGroupPolicyScope_popup.html',
            controller: 'editPSGroupPolicyScopeController',
            resolve: {
                message: function () {
                    var message = {
                    		groupPolicyScopeListData: $scope.editPSGroupPolicyScope
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.groupPolicyScopeListDatas = response.groupPolicyScopeListDatas;
        });
    };

    $scope.deleteGroupPolicyScope = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Group Policy Scope  "+data.name+". Do you want to continue?",
            function(){
                var uuu = papUrl + "/ecomp/ps_dictionary/remove_GroupPolicyScope.htm";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.groupPolicyScopeListDatas=data.groupPolicyScopeListDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };
});