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
app.controller('optimizationModelsDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
    
    PolicyAppService.getData('getDictionary/get_OptimizationModelsData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.optimizationModelsDictionaryDatas = JSON.parse($scope.data.optimizationModelsDictionaryDatas);
    	console.log($scope.optimizationModelsDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('get_LockDownData').then(function(data){
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    	if($scope.lockdowndata[0].lockdown == true){
    		$scope.optimizationModelsDictionaryGrid.columnDefs[0].visible = false;
    		$scope.gridApi.grid.refresh();
    	}else{
    		$scope.optimizationModelsDictionaryGrid.columnDefs[0].visible = true;
    		$scope.gridApi.grid.refresh();
    	}
    },function(error){
    	console.log("failed");
    });
	
    $scope.optimizationModelsDictionaryGrid = {
            data : 'optimizationModelsDictionaryDatas',
            enableFiltering: true,
    		exporterCsvFilename: 'OptimizationPolicyDictionary.csv',
    		enableGridMenu: true,
    		enableSelectAll: true,
            columnDefs: [{
                field: 'id', 
                enableFiltering: false, headerCellTemplate: '' +
                '<button id=\'New\' ng-click="grid.appScope.createNewOptimizationModelsWindow()" class="btn btn-success">' + 'Create</button>',
                cellTemplate:
                    '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteOptimizationModels(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
            },{ field: 'modelName', displayName : 'ONAP Optimization Model', sort: { direction: 'asc', priority: 0 }},
                { field: 'description' },
                { field: 'version', displayName : 'Model Version' },
                {field: 'userCreatedBy.userName', displayName : 'Imported By' },
                {field: 'dependency', visible: false}, 
                {field: 'attributes', visible: false},
                {field: 'enumValues', visible: false},
                {field: 'ref_attributes',visible: false},
                {field: 'sub_attributes', visible: false}
            ],
    		exporterMenuPdf: false,
    		exporterPdfDefaultStyle: {fontSize: 9},
    		exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
    		exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
    		exporterPdfHeader: { text: "My Header", style: 'headerStyle' },
    		exporterPdfFooter: function ( currentPage, pageCount ) {
    			return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
    		},
    		exporterPdfCustomFormatter: function ( docDefinition ) {
    			docDefinition.styles.headerStyle = { fontSize: 22, bold: true };
    			docDefinition.styles.footerStyle = { fontSize: 10, bold: true };
    			return docDefinition;
    		},
    		exporterFieldCallback: function(grid, row, col, input) {
    	       	 if( col.name == 'createdDate' || col.name == 'modifiedDate') {
    	       		 var date = new Date(input);
    	       		 return date.toString("yyyy-MM-dd HH:MM:ss a");
    	       	 } else {
    	       		 return input;
    	       	 }
    	        },
    		exporterPdfOrientation: 'portrait',
    		exporterPdfPageSize: 'LETTER',
    		exporterPdfMaxGridWidth: 500,
    		exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
    		onRegisterApi: function(gridApi){
    			$scope.gridApi = gridApi;
    		}
    };
    
    $scope.editOptimizationModelName = null;
    $scope.createNewOptimizationModelsWindow = function(){
        $scope.editOptimizationModelName = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_optimizationModel_popup.html',
            controller: 'editOptimizationModelController',
            resolve: {
                message: function () {
                    var message = {
                        optimizationModelsDictionaryDatas: $scope.editOptimizationModelName
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.optimizationModelsDictionaryDatas=response.optimizationModelsDictionaryDatas;
        });
    };

    $scope.editOptimizationModelsWindow = function(optimizationModelsDictionaryData) {
        $scope.editOptimizationModelName = optimizationModelsDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_optimizationModel_popup.html',
            controller: 'editOptimizationModelController',
            resolve: {
                message: function () {
                    var message = {
                        optimizationModelsDictionaryData: $scope.editOptimizationModelName
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.optimizationModelsDictionaryDatas = response.optimizationModelsDictionaryDatas;
        });
    };

    $scope.deleteOptimizationModels = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Optimization Model : "+data.modelName+". Do you want to continue?",
            function(){
                var uuu = "deleteDictionary/oof_dictionary/remove_model";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.optimizationModelsDictionaryDatas=data.optimizationModelsDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});