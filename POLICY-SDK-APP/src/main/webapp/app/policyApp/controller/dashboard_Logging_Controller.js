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
app.controller('policyDashboardController', function ($scope, PolicyAppService, modalService, $modal, uiGridConstants,Grid){
	$( "#dialog" ).hide();

	$scope.loading = true;
	PolicyAppService.getData('get_DashboardLoggingData').then(function(data){
		$scope.loading = false;
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.availableLoggingDatas =JSON.parse($scope.data.availableLoggingDatas);
		console.log($scope.availableLoggingDatas);
	},function(error){
		console.log("failed");
		//reloadPageOnce();
	});

	$scope.availableGridLoggingDatas = {
			data : 'availableLoggingDatas',
			enableFiltering: true,
			columnDefs: [{ field: 'id'},
			             { field: 'type'},
			             { field: 'system'},
			             {field: 'logtype'},
			             {field : 'date' ,type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:MM:ss a\'' },
			             {field : 'description'}
			             ],
			             enableGridMenu: true,
			             enableSelectAll: true,
			             exporterCsvFilename: 'DashboardLogging.csv',
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
			            	 if( col.name == 'date') {
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

});

