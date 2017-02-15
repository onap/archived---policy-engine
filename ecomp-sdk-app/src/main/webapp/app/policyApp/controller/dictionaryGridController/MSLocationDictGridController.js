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
app.controller('msLocationDictGridController', function ($scope, MSDictionaryService,modalService, $modal, PapUrlService){
    $( "#dialog" ).hide();
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    MSDictionaryService.getMSLocationDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.microServiceLocationDictionaryDatas = JSON.parse($scope.data.microServiceLocationDictionaryDatas);
	        console.log($scope.microServiceLocationDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
		
	});

    $scope.msLocationDictionaryGrid = {
        data : 'microServiceLocationDictionaryDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewMSLocationWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editMSLocationWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteMSLocation(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'name', displayName : 'Name'},
            { field: 'descriptionValue', displayName : 'Description' }
        ]
    };

    $scope.editMSLocation = null;
    $scope.createNewMSLocationWindow = function(){
        $scope.editMSLocation = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_msLocation_popup.html',
            controller: 'editMSLocationController',
            resolve: {
                message: function () {
                    var message = {
                        microServiceLocationDictionaryDatas: $scope.editMSLocation
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceLocationDictionaryDatas=response.microServiceLocationDictionaryDatas;
        });
    };

    $scope.editMSLocationWindow = function(microServiceLocationDictionaryData) {
        $scope.editMSLocation = microServiceLocationDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_msLocation_popup.html',
            controller: 'editMSLocationController',
            resolve: {
                message: function () {
                    var message = {
                        microServiceLocationDictionaryData: $scope.editMSLocation
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.microServiceLocationDictionaryDatas = response.microServiceLocationDictionaryDatas;
        });
    };

    $scope.deleteMSLocation = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Micro Service Location  "+data.name+". Do you want to continue?",
            function(){
                var uuu = papUrl + "/ecomp/ms_dictionary/remove_msLocation.htm";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.microServiceLocationDictionaryDatas=data.microServiceLocationDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };

});