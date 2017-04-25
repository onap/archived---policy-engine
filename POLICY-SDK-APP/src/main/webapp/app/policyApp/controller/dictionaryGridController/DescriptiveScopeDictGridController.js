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
app.controller('descriptiveDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
		
    PolicyAppService.getData('getDictionary/get_DescriptiveScope').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.descriptiveScopeDictionaryDatas = JSON.parse($scope.data.descriptiveScopeDictionaryDatas);
    	console.log($scope.descriptiveScopeDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('get_LockDownData').then(function(data){
		 var j = data;
		 $scope.data = JSON.parse(j.data);
		 $scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
		 if($scope.lockdowndata[0].lockdown == true){
			 $scope.descriptiveDictionaryGrid.columnDefs[0].visible = false;
			 $scope.gridApi.grid.refresh();
		 }else{
			 $scope.descriptiveDictionaryGrid.columnDefs[0].visible = true;
			 $scope.gridApi.grid.refresh();
		 }
	 },function(error){
		 console.log("failed");
	 });
	
    $scope.descriptiveDictionaryGrid = {
        data : 'descriptiveScopeDictionaryDatas',
        enableFiltering: true,
        exporterCsvFilename: 'SearchCriteria.csv',
        enableGridMenu: true,
        enableSelectAll: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewDescriptiveScopeWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editDescriptiveScopeWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteDescriptiveScope(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'scopeName', displayName : 'Descriptive Scope Name', sort: { direction: 'asc', priority: 0 }},
            { field: 'search', displayName : 'Search Criteria' },
            { field: 'description' },
            {field: 'userCreatedBy.userName', displayName : 'Created By' },
            {field: 'userModifiedBy.userName', displayName : 'Modified By'},
            {field: 'createdDate',type: 'date', cellFilter: 'date:\'yyyy-MM-dd\''  },
            {field: 'modifiedDate',type: 'date', cellFilter: 'date:\'yyyy-MM-dd\''  }
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
        exporterPdfOrientation: 'portrait',
        exporterPdfPageSize: 'LETTER',
        exporterPdfMaxGridWidth: 500,
        exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
        onRegisterApi: function(gridApi){
        	$scope.gridApi = gridApi;
        }
    };

    $scope.editDescriptiveScope = null;
    $scope.createNewDescriptiveScopeWindow = function(){
        $scope.editDescriptiveScope = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_descriptiveScope_popup.html',
            controller: 'editDescriptiveScopeController',
            resolve: {
                message: function () {
                    var message = {
                        descriptiveScopeDictionaryDatas: $scope.editDescriptiveScope
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.descriptiveScopeDictionaryDatas=response.descriptiveScopeDictionaryDatas;
        });
    };

    $scope.editDescriptiveScopeWindow = function(descriptiveScopeDictionaryData) {
        $scope.editDescriptiveScope = descriptiveScopeDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_descriptiveScope_popup.html',
            controller: 'editDescriptiveScopeController',
            resolve: {
                message: function () {
                    var message = {
                        descriptiveScopeDictionaryData: $scope.editDescriptiveScope
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.descriptiveScopeDictionaryDatas = response.descriptiveScopeDictionaryDatas;
        });
    };

    $scope.deleteDescriptiveScope = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Descriptive Scope :   "+data.scopeName+". Do you want to continue?",
            function(){
                var uuu = "deleteDictionary/descriptive_dictionary/remove_descriptiveScope";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.descriptiveScopeDictionaryDatas=data.descriptiveScopeDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };
});