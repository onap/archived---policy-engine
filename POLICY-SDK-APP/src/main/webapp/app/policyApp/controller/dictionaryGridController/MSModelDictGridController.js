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
app.controller('msModelsDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
		
    PolicyAppService.getData('getDictionary/get_MicroServiceModelsData').then(function (data) {
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
    		$scope.msModelsDictionaryGrid.columnDefs[0].visible = false;
    		$scope.gridApi.grid.refresh();
    	}else{
    		$scope.msModelsDictionaryGrid.columnDefs[0].visible = true;
    		$scope.gridApi.grid.refresh();
    	}
    },function(error){
    	console.log("failed");
    });
	
    $scope.msModelsDictionaryGrid = {
            data : 'microServiceModelsDictionaryDatas',
            enableFiltering: true,
    		exporterCsvFilename: 'MSPolicyDictionary.csv',
    		enableGridMenu: true,
    		enableSelectAll: true,
            columnDefs: [{
                field: 'id', 
                enableFiltering: false, headerCellTemplate: '' +
                '<button id=\'New\' ng-click="grid.appScope.createNewMSModelsWindow()" class="btn btn-success">' + 'Create</button>',
                cellTemplate:
                    '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteMSModels(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
            },{ field: 'modelName', displayName : 'Micro Service Model', sort: { direction: 'asc', priority: 0 }},
                { field: 'description' },
                { field: 'version', displayName : 'Model Version' },
                { field: 'decisionModel', displayName : 'Decision Model' },
                {field: 'userCreatedBy.userName', displayName : 'Imported By' },
                {field: 'dependency', visible: false}, 
                {field: 'attributes', visible: false},
                {field: 'enumValues', visible: false},
                {field: 'ref_attributes',visible: false},
                {field: 'sub_attributes', visible: false}
            ] ,
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
    
    $scope.editMSmodelName = null;
    $scope.createNewMSModelsWindow = function(){
        $scope.editMSmodelName = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_msModel_popup.html',
            controller: 'editMSModelController',
            resolve: {
                message: function () {
                    var message = {
                        microServiceModelsDictionaryDatas: $scope.editMSmodelName
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceModelsDictionaryDatas=response.microServiceModelsDictionaryDatas;
        });
    };

    $scope.editMSModelsWindow = function(microServiceModelsDictionaryData) {
        $scope.editMSmodelName = microServiceModelsDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_msModel_popup.html',
            controller: 'editMSModelController',
            resolve: {
                message: function () {
                    var message = {
                        microServiceModelsDictionaryData: $scope.editMSmodelName
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceModelsDictionaryDatas = response.microServiceModelsDictionaryDatas;
        });
    };

    $scope.deleteMSModels = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the MicroService Model : "+data.modelName+". Do you want to continue?",
            function(){
                var uuu = "deleteDictionary/ms_dictionary/remove_msModel";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.microServiceModelsDictionaryDatas=data.microServiceModelsDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});
