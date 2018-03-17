/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
app.controller('msHeaderDefaultValuesDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
    
    PolicyAppService.getData('getDictionary/get_MicroServiceHeaderDefaultsData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.microServiceHeaderDefaultDatas = JSON.parse($scope.data.microServiceHeaderDefaultDatas);
    	console.log("microServiceHeaderDefaultDatas: " + $scope.microServiceHeaderDefaultDatas);
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
    
	PolicyAppService.getData('getDictionary/get_RiskTypeDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log("riskTypeDictionaryDatas: " + $scope.data);
		$scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
		console.log($scope.riskTypeDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});
	
	PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);
		console.log($scope.onapNameDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('get_DCAEPriorityValues').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.priorityDatas = JSON.parse($scope.data.priorityDatas);
		console.log($scope.priorityDatas);
	}, function (error) {
		console.log("failed");
	});
	
    PolicyAppService.getData('get_LockDownData').then(function(data){
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    	if($scope.lockdowndata[0].lockdown == true){
    		$scope.msHeaderDefaultValuesDictionaryGrid.columnDefs[0].visible = false;
    		$scope.gridApi.grid.refresh();
    	}else{
    		$scope.msHeaderDefaultValuesDictionaryGrid.columnDefs[0].visible = true;
    		$scope.gridApi.grid.refresh();
    	}
    },function(error){
    	console.log("failed");
    });
	
    $scope.msHeaderDefaultValuesDictionaryGrid = {
        data : 'microServiceHeaderDefaultDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewModelAttributeWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editModelHeaderDefaultsWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteModelAttribute(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'modelName', displayName :'MicroService', sort: { direction: 'asc', priority: 0 }},
            { field: 'guard', displayName :'Guard'}, { field: 'priority', displayName :'Priority'},{ field: 'riskType', displayName :'Risk Type'},{ field: 'riskLevel', displayName :'Risk Level'},{field: 'onapName' , displayName :'Onap Name' }
        ]
    };

    $scope.editModelAttribute = null;
    $scope.createNewModelAttributeWindow = function(){
        $scope.editModelAttribute = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_HeaderDefaultValues_popup.html',
            controller: 'editMSHeaderDefaultValuesController',
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
            $scope.microServiceHeaderDefaultDatas=response.microServiceHeaderDefaultDatas;
        });
    };

    $scope.editModelHeaderDefaultsWindow = function(modelAttributeDictionaryData) {
        $scope.editHeaderDefaults = modelAttributeDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_HeaderDefaultValues_popup.html',
            controller: 'editMSHeaderDefaultValuesController',
            resolve: {
                message: function () {
                    var message = {
                    		modelAttributeDictionaryData: $scope.editHeaderDefaults
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.modelAttributeDictionaryDataa = response.modelAttributeDictionaryDatas;
        });
    };

    $scope.deleteModelAttribute = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Header Default Values :  "+data.name+". Do you want to continue?",
            function(){
                var uuu =  "deleteDictionary/ms_dictionary/remove_headerDefaults";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.microServiceHeaderDefaultDatas=data.microServiceHeaderDefaultDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});