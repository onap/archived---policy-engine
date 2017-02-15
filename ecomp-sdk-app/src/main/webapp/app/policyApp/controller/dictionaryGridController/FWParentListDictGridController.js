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
app.controller('parentDictGridController', function ($scope, FWDictionaryService,modalService, $modal, PapUrlService){
	$( "#dialog" ).hide();

	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);

		FWDictionaryService.getParentListDictionaryData(papUrl).then(function (data) {
			var j = data;
			$scope.data = JSON.parse(j.data);
			console.log($scope.data);
			$scope.fwDictListDictionaryDatas = JSON.parse($scope.data.fwDictListDictionaryDatas);
			console.log($scope.fwDictListDictionaryDatas);
		}, function (error) {
			console.log("failed");
		});
	});



	$scope.parentDictionaryGrid = {
			data : 'fwDictListDictionaryDatas',
			enableFiltering: true,
			exporterCsvFilename: 'ParentList.csv',
			enableGridMenu: true,
			enableSelectAll: true,
			columnDefs: [{
				field: 'id', enableFiltering: false, headerCellTemplate: '' +
				'<button id=\'New\' ng-click="grid.appScope.createNewFWParentListWindow()" class="btn btn-success">' + 'Create</button>',
				cellTemplate:
					'<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editFWParentListWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
					'<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteFWParentList(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
			},{ field: 'parentItemName', displayName : 'Parent Name'},
			{ field: 'description', displayName : 'Description' },
			{ field: 'addressList', displayName : 'Address List' },
			{ field: 'serviceList', displayName : 'Service List' }
			],
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

	$scope.editParentList = null;
	$scope.createNewFWParentListWindow = function(){
		$scope.editParentList = null;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_fwParentList_popup.html',
			controller: 'editFWParentListController',
			resolve: {
				message: function () {
					var message = {
							fwDictListDictionaryDatas: $scope.editParentList
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.fwDictListDictionaryDatas=response.fwDictListDictionaryDatas;
		});
	};

	$scope.editFWParentListWindow = function(fwDictListDictionaryData) {
		$scope.editParentList = fwDictListDictionaryData;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_fwParentList_popup.html',
			controller: 'editFWParentListController',
			resolve: {
				message: function () {
					var message = {
							fwDictListDictionaryData: $scope.editParentList
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.fwDictListDictionaryDatas = response.fwDictListDictionaryDatas;
		});
	};

	$scope.deleteFWParentList = function(data) {
		modalService.popupConfirmWin("Confirm","You are about to delete the Parent List:  "+data.parentItemName+". Do you want to continue?",
				function(){
			var uuu = papUrl + "/ecomp/fw_dictionary/remove_FWDictionaryList.htm";
			var postData={data: data};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){$scope.fwDictListDictionaryDatas=data.fwDictListDictionaryDatas;});
				},
				error : function(data){
					console.log(data);
					modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
				}
			});

		})
	};

});