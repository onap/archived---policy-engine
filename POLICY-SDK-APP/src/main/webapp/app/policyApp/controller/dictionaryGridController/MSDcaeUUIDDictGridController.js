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
app.controller('msDCAEUUIDDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
		
    PolicyAppService.getData('getDictionary/get_DCAEUUIDData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.dcaeUUIDDictionaryDatas = JSON.parse($scope.data.dcaeUUIDDictionaryDatas);
    	console.log($scope.dcaeUUIDDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('get_LockDownData').then(function(data){
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    	if($scope.lockdowndata[0].lockdown == true){
    		$scope.dcaeUUIDDictionaryGrid.columnDefs[0].visible = false;
    		$scope.gridApi.grid.refresh();
    	}else{
    		$scope.dcaeUUIDDictionaryGrid.columnDefs[0].visible = true;
    		$scope.gridApi.grid.refresh();
    	}
    },function(error){
    	console.log("failed");
    });
	
    $scope.dcaeUUIDDictionaryGrid = {
        data : 'dcaeUUIDDictionaryDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewMSDCAEUUIDWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editMSDCAEUUIDWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteMSDCAEUUID(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'name', displayName : 'Name', sort: { direction: 'asc', priority: 0 }},
            { field: 'description' }
        ]
    };

    $scope.editDCAEuuid = null;
    $scope.createNewMSDCAEUUIDWindow = function(){
        $scope.editDCAEuuid = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_DCAEuuid_popup.html',
            controller: 'editDCAEuuidController',
            resolve: {
                message: function () {
                    var message = {
                        dcaeUUIDDictionaryDatas: $scope.editDCAEuuid
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.dcaeUUIDDictionaryDatas=response.dcaeUUIDDictionaryDatas;
        });
    };

    $scope.editMSDCAEUUIDWindow = function(dcaeUUIDDictionaryData) {
        $scope.editDCAEuuid = dcaeUUIDDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_DCAEuuid_popup.html',
            controller: 'editDCAEuuidController',
            resolve: {
                message: function () {
                    var message = {
                        dcaeUUIDDictionaryData: $scope.editDCAEuuid
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.dcaeUUIDDictionaryDatas = response.dcaeUUIDDictionaryDatas;
        });
    };

    $scope.deleteMSDCAEUUID = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the DCAE UUID :  "+data.name+". Do you want to continue?",
            function(){
                var uuu =  "deleteDictionary/ms_dictionary/remove_dcaeuuid";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.dcaeUUIDDictionaryDatas=data.dcaeUUIDDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});