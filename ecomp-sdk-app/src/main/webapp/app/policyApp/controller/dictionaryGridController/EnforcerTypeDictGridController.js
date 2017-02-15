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
app.controller('enforcerDictGridController', function ($scope, DictionaryService,modalService, $modal, PapUrlService){
    $( "#dialog" ).hide();
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
		
	    DictionaryService.getEnforcerDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.enforcerDictionaryDatas = JSON.parse($scope.data.enforcerDictionaryDatas);
	        console.log($scope.enforcerDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	});

    $scope.enforcerDictionaryGrid = {
        data : 'enforcerDictionaryDatas',
        enableFiltering: true,
        columnDefs: [{
            field: 'id', enableFiltering: false, headerCellTemplate: '' +
            '<button id=\'New\' ng-click="grid.appScope.createNewEnforcerTypeWindow()" class="btn btn-success">' + 'Create</button>',
            cellTemplate:
            '<button  type="button"  class="btn btn-primary"  ng-click="grid.appScope.editEnforcerTypeWindow(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
            '<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deleteEnforcerType(row.entity)" ><i class="fa fa-trash-o"></i></button> ',  width: '8%'
        },{ field: 'enforcingType', displayName : 'Enforcing Type'},
            { field: 'script' , displayName : 'Script'},
            {field: 'connectionQuery', displayName : 'Connection Query' },
            {field: 'valueQuery', displayName : 'Value Query'}
        ]
    };

    $scope.editEnforcerType = null;
    $scope.createNewEnforcerTypeWindow = function(){
        $scope.editEnforcerType = null;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_EnforcerType_popup.html',
            controller: 'editEnforcerTypeController',
            resolve: {
                message: function () {
                    var message = {
                        enforcerDictionaryDatas: $scope.editEnforcerType
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.enforcerDictionaryDatas=response.enforcerDictionaryDatas;
        });
    };

    $scope.editEnforcerTypeWindow = function(enforcerDictionaryData) {
        $scope.editEnforcerType = enforcerDictionaryData;
        var modalInstance = $modal.open({
        	backdrop: 'static', keyboard: false,
            templateUrl : 'add_EnforcerType_popup.html',
            controller: 'editEnforcerTypeController',
            resolve: {
                message: function () {
                    var message = {
                        enforcerDictionaryData: $scope.editEnforcerType
                    };
                    return message;
                }
            }
        });
        modalInstance.result.then(function(response){
            console.log('response', response);
            $scope.enforcerDictionaryDatas = response.enforcerDictionaryDatas;
        });
    };

    $scope.deleteEnforcerType = function(data) {
        modalService.popupConfirmWin("Confirm","You are about to delete the Enforcing Type  "+data.enforcingType+". Do you want to continue?",
            function(){
                var uuu = papUrl + "/ecomp/enforcer_dictionary/remove_enforcer.htm";
                var postData={data: data};
                $.ajax({
                    type : 'POST',
                    url : uuu,
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(postData),
                    success : function(data){
                        $scope.$apply(function(){$scope.enforcerDictionaryDatas=data.enforcerDictionaryDatas;});
                    },
                    error : function(data){
                        console.log(data);
                        modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
                    }
                });

            })
    };
});