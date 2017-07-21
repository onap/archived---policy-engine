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
app.controller('termListDictGridController', function ($scope, PolicyAppService, modalService, $modal){
    $( "#dialog" ).hide();
		
    PolicyAppService.getData('getDictionary/get_TermListData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.termListDictionaryDatas = JSON.parse($scope.data.termListDictionaryDatas);
    	console.log($scope.termListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_TagListData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.fwTagDictionaryDatasByName = JSON.parse($scope.data.fwTagDictionaryDatas);
    	console.log($scope.fwTagDictionaryDatasByName);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('get_LockDownData').then(function(data){
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
    	if($scope.lockdowndata[0].lockdown == true){
    		$scope.termListDictionaryGrid.columnDefs[0].visible = false;
    		$scope.gridApi.grid.refresh();
    	}else{
    		$scope.termListDictionaryGrid.columnDefs[0].visible = true;
    		$scope.gridApi.grid.refresh();
    	}
    },function(error){
    	console.log("failed");
    });
	
    $scope.termListDictionaryGrid = {
        data : 'termListDictionaryDatas',
        enableFiltering: true,
        exporterCsvFilename: 'TermList.csv',
        enableGridMenu: true,
        enableSelectAll: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewFWTermListWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editFWTermListWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteFWTermList(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'termName', displayName : 'Term-Name', sort: { direction: 'asc', priority: 0 }},
            { field: 'termDescription', displayName : 'Term-Description' },
            { field: 'fromZone', displayName : 'From Zone' },
            {field: 'toZone', displayName : 'To Zone' },
            {field: 'srcIPList', displayName : 'Source-IP-List'},
            {field: 'destIPList',displayName : 'Destination-IP-List' },
            {field: 'srcPortList',displayName : 'Source-Port-List' },
            {field: 'destPortList',displayName : 'Destination-Port-List' },
            {field: 'action',displayName : 'Action List' },
            {field: 'userCreatedBy.userName',displayName : 'Created By'  },
            {field: 'userModifiedBy.userName',displayName : 'Modified By'},
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

    $scope.editTermList = null;
    $scope.createNewFWTermListWindow = function(){
        $scope.editTermList = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_fwTermList_popup.html',
            controller: 'editFWTermListController',
            resolve: {
                message: function () {
                    var message = {
                        termListDictionaryDatas: $scope.editTermList
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.termListDictionaryDatas=response.termListDictionaryDatas;
        });
    };

    $scope.editFWTermListWindow = function(termListDictionaryData) {
        $scope.editTermList = termListDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_fwTermList_popup.html',
            controller: 'editFWTermListController',
            resolve: {
                message: function () {
                    var message = {
                        termListDictionaryData: $scope.editTermList
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.termListDictionaryDatas = response.termListDictionaryDatas;
        });
    };

    $scope.deleteFWTermList = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the TermList  "+data.termName+". Do you want to continue?",
            function(){
                var uuu = "deleteDictionary/fw_dictionary/remove_termList";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.termListDictionaryDatas=data.termListDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };


});