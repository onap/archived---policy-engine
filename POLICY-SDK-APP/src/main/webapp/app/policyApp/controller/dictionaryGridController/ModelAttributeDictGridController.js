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
app.controller('modelAttributeDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
    
    PolicyAppService.getData('getDictionary/get_MicroServiceAttributeData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.microServiceAttributeDictionaryDatas = JSON.parse($scope.data.microServiceAttributeDictionaryDatas);
    	console.log($scope.microServiceAttributeDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_MicroServiceModelsDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.microServiceModelsDictionaryDatas = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
    	console.log($scope.microServiceModelsDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('get_LockDownData').then(function(data){
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    	if($scope.lockdowndata[0].lockdown == true){
    		$scope.modelAttributeDictionaryGrid.columnDefs[0].visible = false;
    		$scope.gridApi.grid.refresh();
    	}else{
    		$scope.modelAttributeDictionaryGrid.columnDefs[0].visible = true;
    		$scope.gridApi.grid.refresh();
    	}
    },function(error){
    	console.log("failed");
    });
	
    $scope.modelAttributeDictionaryGrid = {
        data : 'microServiceAttributeDictionaryDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewModelAttributeWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editModelAttributeWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteModelAttribute(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'name', displayName :'Name', sort: { direction: 'asc', priority: 0 }},
            { field: 'value'}, {field: 'modelName' , displayName :'MicroService' }
        ]
    };

    $scope.editModelAttribute = null;
    $scope.createNewModelAttributeWindow = function(){
        $scope.editModelAttribute = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_modelAttribute_popup.html',
            controller: 'editModelAttributeController',
            resolve: {
                message: function () {
                    var message = {
                    		microServiceAttributeDictionaryDatas: $scope.editModelAttribute
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceAttributeDictionaryDatas=response.microServiceAttributeDictionaryDatas;
        });
    };

    $scope.editModelAttributeWindow = function(modelAttributeDictionaryData) {
        $scope.editModelAttribute = modelAttributeDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_modelAttribute_popup.html',
            controller: 'editModelAttributeController',
            resolve: {
                message: function () {
                    var message = {
                    		modelAttributeDictionaryData: $scope.editModelAttribute
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceAttributeDictionaryDatas = response.microServiceAttributeDictionaryDatas;
        });
    };

    $scope.deleteModelAttribute = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Model Attribute :  "+data.name+". Do you want to continue?",
            function(){
                var uuu =  "deleteDictionary/ms_dictionary/remove_modelAttribute";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.microServiceAttributeDictionaryDatas=data.microServiceAttributeDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});