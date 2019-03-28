/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
app.controller('MSModelDictionaryDataController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
    
    PolicyAppService.getData('getDictionary/get_MicroServiceDictData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.microServiceDictionaryDatas = JSON.parse($scope.data.microServiceDictionaryDatas);
    	console.log($scope.microServiceDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });
    
    PolicyAppService.getData('get_LockDownData').then(function(data){
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    	$scope.msModelsDictionaryDataGrid.columnDefs[0].visible = !$scope.lockdowndata[0].lockdown;
		$scope.gridApi.grid.refresh();
    },function(error){
    	console.log("failed");
    });
	
    $scope.msModelsDictionaryDataGrid = {
            data : 'microServiceDictionaryDatas',
            enableFiltering: true,
            columnDefs: [{
                field: 'id', enableFiltering: false, headerCellTemplate: '' +
                '<button id=\'New\' ng-click="grid.appScope.createNewMSDictNameWindow()" class="btn btn-success">' + 'Create</button>',
                cellTemplate:
                '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editMSDictNameWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
                '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteMSDictName(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
            },{ field: 'dictionaryName', displayName : 'Dictionary Name', sort: { direction: 'asc', priority: 0 }},
                { field: 'dictionaryUrl', displayName : 'URL' },
                {field: 'dictionaryDataByName', displayName : 'Dictionary Data Name'}
            ],
            onRegisterApi: function(gridApi){
				$scope.gridApi = gridApi;
			}
        };

    $scope.editMSDictData = null;
    $scope.createNewMSDictNameWindow = function(){
        $scope.editMSDictData = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_dictionaryData_popup.html',
            controller: 'editMSDictController',
            resolve: {
                message: function () {
                    var message = {
                    		microServiceDictionaryDatas: $scope.editMSDictData
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceDictionaryDatas=response.microServiceDictionaryDatas;
        });
    };

    $scope.editMSDictNameWindow = function(microServiceDictionaryDatas) {
        $scope.editMSDictData = microServiceDictionaryDatas;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_dictionaryData_popup.html',
            controller: 'editMSDictController',
            resolve: {
                message: function () {
                    var message = {
                    		microServiceDictionaryDatas: $scope.editMSDictData
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceDictionaryDatas = response.microServiceDictionaryDatas;
        });
    };

    $scope.deleteMSDictName = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Micro Service Config Name  "+data.name+". Do you want to continue?",
            function(){
                var uuu = "deleteDictionary/ms_dictionary/remove_msDictionaryData";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.microServiceDictionaryDatas=data.microServiceDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});