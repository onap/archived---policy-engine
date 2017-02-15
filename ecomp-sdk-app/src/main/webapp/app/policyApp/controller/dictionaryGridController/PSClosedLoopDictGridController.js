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
app.controller('psClosedLoopDictGridController', function ($scope, PolicyScopeService,modalService, $modal, PapUrlService){
    $( "#dialog" ).hide();
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    PolicyScopeService.getPSClosedLoopDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.psClosedLoopDictionaryDatas = JSON.parse($scope.data.psClosedLoopDictionaryDatas);
	        console.log($scope.psClosedLoopDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
		
	});

    $scope.psClosedLoopDictionaryGrid = {
        data : 'psClosedLoopDictionaryDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewPSClosedLoopWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editPSClosedLoopWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deletePSClosedLoop(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'name', displayName : 'Closed Loop Name'},
            { field: 'descriptionValue' , displayName : 'Description'}
        ]
    };
    
    $scope.editPSClosedLoop = null;
    $scope.createNewPSClosedLoopWindow = function(){
        $scope.editPSClosedLoop = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_PSClosedLoop_popup.html',
            controller: 'editPSClosedLoopController',
            resolve: {
                message: function () {
                    var message = {
                    		psClosedLoopDictionaryDatas: $scope.editPSClosedLoop
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.psClosedLoopDictionaryDatas=response.psClosedLoopDictionaryDatas;
        });
    };

    $scope.editPSClosedLoopWindow = function(psClosedLoopDictionaryData) {
        $scope.editPSClosedLoop = psClosedLoopDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_PSClosedLoop_popup.html',
            controller: 'editPSClosedLoopController',
            resolve: {
                message: function () {
                    var message = {
                    		psClosedLoopDictionaryData: $scope.editPSClosedLoop
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.psClosedLoopDictionaryDatas = response.psClosedLoopDictionaryDatas;
        });
    };

    $scope.deletePSClosedLoop = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Closed Loop  "+data.name+". Do you want to continue?",
            function(){
                var uuu = papUrl + "/ecomp/ps_dictionary/remove_PSClosedLoop.htm";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.psClosedLoopDictionaryDatas=data.psClosedLoopDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };
});