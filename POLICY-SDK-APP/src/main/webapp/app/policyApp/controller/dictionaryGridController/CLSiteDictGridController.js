/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
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
app.controller('siteDictGridController', function ($scope, PolicyAppService, modalService, $modal, Notification){
	$( "#dialog" ).hide();

	PolicyAppService.getData('getDictionary/get_ClosedLoopSiteData').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.closedLoopSiteDictionaryDatas = JSON.parse($scope.data.closedLoopSiteDictionaryDatas);
		console.log($scope.closedLoopSiteDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('get_LockDownData').then(function(data){
		var j = data;
		$scope.data = JSON.parse(j.data);
		$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
		if($scope.lockdowndata[0].lockdown == true){
			$scope.siteDictionaryGrid.columnDefs[0].visible = false;
			$scope.gridApi.grid.refresh();
		}else{
			$scope.siteDictionaryGrid.columnDefs[0].visible = true;
			$scope.gridApi.grid.refresh();
		}
	},function(error){
		console.log("failed");
	});

	$scope.siteDictionaryGrid = {
			data : 'closedLoopSiteDictionaryDatas',
			enableFiltering: true,
			exporterCsvFilename: 'ClosedLoopSite.csv',
			enableGridMenu: true,
			enableSelectAll: true,
			columnDefs: [{
				field: 'id', enableFiltering: false, headerCellTemplate: '' +
				'<button id=\'New\' ng-click="grid.appScope.createNewSiteDictWindow()" class="btn btn-success">' + 'Create</button>',
				cellTemplate:
					'<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editSiteDictWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
					'<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteSiteDict(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
			},
			{ field: 'siteName', displayName : 'Site Name', sort: { direction: 'asc', priority: 0 }},
			{ field: 'description'},
			{field: 'userCreatedBy.userName', displayName : 'Created By'},
			{field: 'userModifiedBy.userName', displayName : 'Modified By'},
			{field: 'createdDate',type: 'date', cellFilter: 'date:\'yyyy-MM-dd\'' },
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

	$scope.editCLSite = null;
	$scope.createNewSiteDictWindow = function(){
		$scope.editCLSite = null;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_CLSite_popup.html',
			controller: 'editCLSiteController',
			resolve: {
				message: function () {
					var message = {
							closedLoopSiteDictionaryDatas: $scope.editCLSite
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.closedLoopSiteDictionaryDatas=response.closedLoopSiteDictionaryDatas;
		});
	};

	$scope.editSiteDictWindow = function(closedLoopSiteDictionaryData) {
		$scope.editCLSite = closedLoopSiteDictionaryData;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_CLSite_popup.html',
			controller: 'editCLSiteController',
			resolve: {
				message: function () {
					var message = {
							closedLoopSiteDictionaryData: $scope.editCLSite
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.closedLoopSiteDictionaryDatas = response.closedLoopSiteDictionaryDatas;
		});
	};

	$scope.deleteSiteDict = function(data) {
		var uuu = "searchDictionary";
		var postData={data: data, type: "clSite"};
		var searchString = "\n";
		$.ajax({
			type : 'POST',
			url : uuu,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(postData),
			success : function(resultList){
				$scope.$apply(function(){
					$scope.list =resultList.result;});
				$scope.searchData = JSON.stringify(resultList.result);
				$scope.searchDatas = JSON.parse($scope.searchData);	   
				$scope.success = true;
				var i;
				if($scope.searchDatas.length > 0){
					for(i = 0 ; i < $scope.searchDatas.length; i++){
						searchString += $scope.searchDatas[i] + "\n";
					}	
				}else{
					searchString += "No Policies is Using this Value"
				}

				console.log($scope.list);
				if($scope.success){
					modalService.popupConfirmWin("Confirm","You are about to delete the Site Dictionary :  "+data.siteName+". \n "+searchString+" \n Do you want to continue?",
							function(){
						var uuu =  "deleteDictionary/cl_dictionary/remove_site";
						var postData={data: data};
						$.ajax({
							type : 'POST',
							url : uuu,
							dataType: 'json',
							contentType: 'application/json',
							data: JSON.stringify(postData),
							success : function(data){
								$scope.$apply(function(){$scope.closedLoopSiteDictionaryDatas=data.closedLoopSiteDictionaryDatas;});
							},
							error : function(data){
								console.log(data);
								modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
							}
						});

					})}
			},
			error : function(data){
				Notification.error("Error while Searching.");
			}
		});
	};


});