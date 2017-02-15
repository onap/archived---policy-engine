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
var Actiontype = ["REST"];
app.controller('actionPolicyDictGridController', function ($scope, DictionaryService,modalService, $modal, uiGridConstants,Grid, PapUrlService){
    $( "#dialog" ).hide();
    $scope.type = Actiontype;

	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    DictionaryService.getActionPolicyDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.actionPolicyDictionaryDatas = JSON.parse($scope.data.actionPolicyDictionaryDatas);
	        console.log($scope.actionPolicyDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	});

    $scope.actionPolicyDictionaryGrid = {
    		enableFiltering: true,
    		data: 'actionPolicyDictionaryDatas',
    		exporterCsvFilename: 'ActionPolicyDictionary.csv',
    		enableGridMenu: true,
    		enableSelectAll: true,
    		columnDefs: [{
    			field: 'id',
    			enableFiltering: false,
    			headerCellTemplate: '' +
    			'<button id=\'New\' ng-click="grid.appScope.createNewActionPolicyDictWindow()" class="btn btn-success">' + 'Create</button>',
    			cellTemplate: '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editActionPolicyDictWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
    			'<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteActionPolicyDict(row.entity)" ><i class="fa fa-trash-o"></i></button> ',
    			width: '8%'
    		},
    		{field: 'attributeName', displayName: 'Attribute Name'},
    		{field: 'type'},
    		{field: 'url'},
    		{field: 'method'},
    		{field: 'header', displayName: 'Headers'},
    		{field: 'description'},
    		{field: 'userCreatedBy.userName', displayName: 'Created By'},
    		{field: 'userModifiedBy.userName', displayName: 'Modified By'},
    		{field: 'createdDate', type: 'date', cellFilter: 'date:\'yyyy-MM-dd\''},
    		{field: 'modifiedDate', type: 'date', cellFilter: 'date:\'yyyy-MM-dd\''}
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


    $scope.editActionPolicyName = null;
    $scope.createNewActionPolicyDictWindow = function () {
        $scope.editActionPolicyName = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl: 'add_actionPolicyDict_popup.html',
            controller: 'editActionPolicyDictController',
            resolve: {
                message: function () {
                    var message = {
                        actionPolicyDictionaryDatas: $scope.editActionPolicyName
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function (response) {
            console.log('response', response);
            $scope.actionPolicyDictionaryDatas = response.actionPolicyDictionaryDatas;
        });
    };

    $scope.editActionPolicyDictWindow = function (actionPolicyDictionaryData) {
        $scope.editActionPolicyName = actionPolicyDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl: 'add_actionPolicyDict_popup.html',
            controller: 'editActionPolicyDictController',
            resolve: {
                message: function () {
                    var message = {
                        actionPolicyDictionaryData: $scope.editActionPolicyName
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function (response) {
            console.log('response', response);
            $scope.actionPolicyDictionaryDatas = response.actionPolicyDictionaryDatas;
        });
    };

    $scope.deleteActionPolicyDict = function (data) {
    	modalService.popupConfirmWin("Confirm", "You are about to delete the Action Policy Dictionary Item  " + data.attributeName + ". Do you want to continue?",
    			function () {
    		var uuu = papUrl + "/ecomp/action_dictionary/remove_actionPolicyDict.htm";
    		var postData = {data: data};
    		$.ajax({
    			type: 'POST',
    			url: uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success: function (data) {
    				$scope.$apply(function () {
    					$scope.actionPolicyDictionaryDatas = data.actionPolicyDictionaryDatas;
    				});
    			},
    			error: function (data) {
    				console.log(data);
    				modalService.showFailure("Fail", "Error while deleting: " + data.responseText);
    			}
    		});

    	})
    };
});