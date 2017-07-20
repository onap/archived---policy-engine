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
appDS2.controller('brmsControllerDictGridController', function ($scope, PolicyAppService,modalService, $modal){
	$( "#dialog" ).hide();


	PolicyAppService.getData('getDictionary/get_BRMSControllerData').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.brmsControllerDictionaryDatas = JSON.parse($scope.data.brmsControllerDictionaryDatas);
		console.log($scope.brmsControllerDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});


	PolicyAppService.getData('get_LockDownData').then(function(data){
		var j = data;
		$scope.data = JSON.parse(j.data);
		$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
		if($scope.lockdowndata[0].lockdown == true){
			$scope.brmsControllerDictionaryGrid.columnDefs[0].visible = false;
			$scope.gridApi.grid.refresh();
		}else{
			$scope.brmsControllerDictionaryGrid.columnDefs[0].visible = true;
			$scope.gridApi.grid.refresh();
		}
	},function(error){
		console.log("failed");
	});

	$scope.brmsControllerDictionaryGrid = {
			data : 'brmsControllerDictionaryDatas',
			enableFiltering: true,
			exporterCsvFilename: 'BRMSControllerDictionary.csv',
			enableGridMenu: true,
			enableSelectAll: true,
			columnDefs: [{
				field: 'id', enableFiltering: false, headerCellTemplate: '' +
				'<button id=\'New\' ng-click="grid.appScope.createNewBRMSControllerWindow()" class="btn btn-success">' + 'Create</button>',
				cellTemplate:
					'<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.viewBRMSControllerWindow(row.entity)"><i class="glyphicon glyphicon-eye-open"></i></button> ' +
					'<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editBRMSControllerWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
					'<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteBRMSController(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '12%'
			},
			{ field: 'controllerName', displayName : 'Controller Name', sort: { direction: 'asc', priority: 0 }},
			{ field: 'description'},
			{field: 'userCreatedBy.userName', displayName : 'Created By'},
			{field: 'createdDate',type: 'date', cellFilter: 'date:\'yyyy-MM-dd\'' },
			{field: 'userModifiedBy.userName', displayName : 'Modified By'},
			{field: 'modifiedDate',type: 'date', cellFilter: 'date:\'yyyy-MM-dd\'' }
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

	$scope.editBRMSController = null;
	$scope.createNewBRMSControllerWindow = function(){
		$scope.editBRMSController = null;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_brmsController_popup.html',
			controller: 'editBRMSControllerController',
			resolve: {
				message: function () {
					var message = {
							brmsControllerDictionaryDatas: $scope.editBRMSController,
							disabled : false
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.brmsControllerDictionaryDatas=response.brmsControllerDictionaryDatas;
		});
	};

	$scope.viewBRMSControllerWindow = function(brmsControllerDictionaryData) {
		$scope.editBRMSController = brmsControllerDictionaryData;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_brmsController_popup.html',
			controller: 'editBRMSControllerController',
			resolve: {
				message: function () {
					var message = {
							brmsControllerDictionaryDatas: $scope.editBRMSController,
							disabled : true
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.brmsControllerDictionaryDatas=response.brmsControllerDictionaryDatas;
		});
	}; 

	$scope.editBRMSControllerWindow = function(brmsControllerDictionaryData) {
		$scope.editBRMSController = brmsControllerDictionaryData;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_brmsController_popup.html',
			controller: 'editBRMSControllerController',
			resolve: {
				message: function () {
					var message = {
							brmsControllerDictionaryDatas: $scope.editBRMSController,
							disabled : false
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.brmsControllerDictionaryDatas=response.brmsControllerDictionaryDatas;
		});
	};

	$scope.deleteBRMSController = function(data) {
		modalService.popupConfirmWin("Confirm","You are about to delete the BRMS Controller "+data.controllerName+". Do you want to continue?",
				function(){
			var uuu = "deleteDictionary/brms_dictionary/remove_brmsController";
			var postData={data: data};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){$scope.brmsControllerDictionaryDatas=data.brmsControllerDictionaryDatas;});
				},
				error : function(data){
					console.log(data);
					modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
				}
			});

		})
	};

});