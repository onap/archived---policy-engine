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
app.controller('safePolicyWarningDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
		
    PolicyAppService.getData('getDictionary/get_SafePolicyWarningData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.safePolicyWarningDatas = JSON.parse($scope.data.safePolicyWarningDatas);
    	console.log($scope.safePolicyWarningDatas);
    }, function (error) {
    	console.log("failed");
    });
	
    PolicyAppService.getData('get_LockDownData').then(function(data){
		 var j = data;
		 $scope.data = JSON.parse(j.data);
		 $scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
		 if($scope.lockdowndata[0].lockdown == true){
			 $scope.safePolicyWarningDictionaryGrid.columnDefs[0].visible = false;
			 $scope.gridApi.grid.refresh();
		 }else{
			 $scope.safePolicyWarningDictionaryGrid.columnDefs[0].visible = true;
			 $scope.gridApi.grid.refresh();
		 }
	 },function(error){
		 console.log("failed");
	 });
	
    $scope.safePolicyWarningDictionaryGrid = {
        data : 'safePolicyWarningDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewSafePolicyWarningWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editSafePolicyWarningWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteSafePolicyWarning(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'name', displayName : 'Safe Policy Warning Name', sort: { direction: 'asc', priority: 0 }},
        { field: 'riskType', displayName : 'Risk Type'},{ field: 'message', displayName : 'Message'}
        ],
        onRegisterApi: function(gridApi){
        	$scope.gridApi = gridApi;
        	$scope.gridApi.core.refresh();
        }
    };
   
    $scope.editSafePolicyWarning = null;
    $scope.createNewSafePolicyWarningWindow = function(){
        $scope.editSafePolicyWarning = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_SafePolicyWarning_popup.html',
            controller: 'editSafePolicyWarningController',
            resolve: {
                message: function () {
                    var message = {
                    		safePolicyWarningDatas: $scope.editSafePolicyWarningScope
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.safePolicyWarningDatas=response.safePolicyWarningDatas;
        });
    };

    $scope.editSafePolicyWarningWindow = function(safePolicyWarningData) {
        $scope.editSafePolicyWarning = safePolicyWarningData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_SafePolicyWarning_popup.html',
            controller: 'editSafePolicyWarningController',
            resolve: {
                message: function () {
                    var message = {
                    		safePolicyWarningData: $scope.editSafePolicyWarning
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.groupPolicyScopeListDatas = response.safePolicyWarningData;
        });
    };

    $scope.deleteSafePolicyWarning = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Safe Policy Warning  "+data.name+". Do you want to continue?",
            function(){
                var uuu = "deleteDictionary/sp_dictionary/remove_SafePolicyWarning";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.safePolicyWarningDatas=data.safePolicyWarningDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };
});