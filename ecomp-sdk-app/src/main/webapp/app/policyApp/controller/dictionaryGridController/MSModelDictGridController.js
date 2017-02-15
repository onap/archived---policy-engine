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
app.controller('msModelsDictGridController', function ($scope, MSDictionaryService,modalService, $modal, PapUrlService){
    $( "#dialog" ).hide();
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    MSDictionaryService.getMSModelDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.microServiceModelsDictionaryDatas = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
	        console.log($scope.microServiceModelsDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
		
	});

    $scope.msModelsDictionaryGrid = {
        data : 'microServiceModelsDictionaryDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewMSModelsWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
                '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteMSModels(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'modelName', displayName : 'Micro Service Model'},
            { field: 'description' },
            { field: 'version', displayName : 'Model Version' },
            {field: 'userCreatedBy.userName', displayName : 'Imported By' }
        ]
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
                var uuu = papUrl + "/ecomp/ms_dictionary/remove_msModel.htm";
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