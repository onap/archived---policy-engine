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
app.controller('attributeDictGridController', function ($scope, PolicyAppService, modalService, $modal){
	$( "#dialog" ).hide();

	PolicyAppService.getData('getDictionary/get_AttributeData').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.attributeDictionaryDatas = JSON.parse($scope.data.attributeDictionaryDatas);
		console.log($scope.attributeDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});


	$scope.exportData = function () {
		var blob = new Blob([document.getElementById('attributeTable').innerHTML], {
			type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
		});
		saveAs(blob, "Attribute.xls");
	};

	PolicyAppService.getData('get_LockDownData').then(function(data){
		var j = data;
		$scope.data = JSON.parse(j.data);
		$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
		if($scope.lockdowndata[0].lockdown == true){
			$scope.attributeDictionaryGrid.columnDefs[0].visible = false;
			$scope.gridApi.grid.refresh();
		}else{
			$scope.attributeDictionaryGrid.columnDefs[0].visible = true;
			$scope.gridApi.grid.refresh();
		}
	},function(error){
		console.log("failed");
	});

	$scope.attributeDictionaryGrid = {
			data : 'attributeDictionaryDatas',
			enableFiltering: true,
			exporterCsvFilename: 'Attribute.csv',
			enableGridMenu: true,
			enableSelectAll: true,
			columnDefs: [{
				field: 'id' , enableFiltering: false, headerCellTemplate: '' +
				'<button id=\'New\' ng-click="grid.appScope.createNewAttributeWindow()" class="btn btn-success">' + 'Create</button>',
				cellTemplate:
					'<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editAttributeWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
					'<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteAttribute(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
			},{ field: 'xacmlId', displayName : 'Attribute ID', sort: { direction: 'asc', priority: 0 }},
			{ field: 'datatypeBean.shortName', displayName : 'Data Type' },
			{ field: 'attributeValue'},
			{ field: 'description' },
			{ field: 'priority' , displayName : 'Priority'},
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


	$scope.editAttributeName = null;
	$scope.createNewAttributeWindow = function(){
		$scope.editAttributeName = null;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_Attribute_popup.html',
			controller: 'editAttributeController',
			resolve: {
				message: function () {
					var message = {
							attributeDictionaryDatas: $scope.editAttributeName
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.attributeDictionaryDatas=response.attributeDictionaryDatas;
		});
	};

	$scope.editAttributeWindow = function(attributeDictionaryData) {
		$scope.editAttributeName = attributeDictionaryData;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_Attribute_popup.html',
			controller: 'editAttributeController',
			resolve: {
				message: function () {
					var message = {
							attributeDictionaryData: $scope.editAttributeName
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.attributeDictionaryDatas = response.attributeDictionaryDatas;
		});
	};

	$scope.deleteAttribute = function(data) {
		var uuu = "searchDictionary";
		var postData={data: data, type: "attribute"};
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
						searchString += $scope.searchDatas[i].name + "\n";
					}	
				}else{
					searchString += "No Policies is Using this Value"
				}

				console.log($scope.list);
				if($scope.success){
					modalService.popupConfirmWin("Confirm","You are about to delete the Attribute Dictionary :  "+data.xacmlId+". \n "+searchString+" \n Do you want to continue?",
							function(){
						var uuu = "deleteDictionary/attribute_dictionary/remove_attribute";
						var postData={data: data};
						$.ajax({
							type : 'POST',
							url : uuu,
							dataType: 'json',
							contentType: 'application/json',
							data: JSON.stringify(postData),
							success : function(data){
								$scope.$apply(function(){$scope.attributeDictionaryDatas=data.attributeDictionaryDatas;});
							},
							error : function(data){
								console.log(data);
								modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
							}
						});

					})}
			},
			error : function(data){
				alert("Error while Searching.");
			}
		});
	};


});