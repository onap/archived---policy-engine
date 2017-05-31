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
app.controller('policyPushController', function ($scope, PolicyAppService, modalService, $modal, Notification,$filter){
    $( "#dialog" ).hide();

   $scope.isDisabled = true;
   $scope.loading = true;

   PolicyAppService.getData('get_LockDownData').then(function(data){
        var j = data;
        $scope.data = JSON.parse(j.data);
        $scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
        if($scope.lockdowndata[0].lockdown == true){
            $scope.isDisabled = true;
        }else{
            $scope.isDisabled = false;
        }
        console.log($scope.data);
    },function(error){
        console.log("failed");
    });

    $scope.pdpdata;
    PolicyAppService.getData('get_PDPGroupData').then(function (data) {
        var j = data;
        $scope.pdpdata = JSON.parse(j.data);
        console.log($scope.pdpdata);
        $scope.pushTabPDPGrid.data = $scope.pdpdata;
    }, function (error) {
        console.log("failed");
    });

    $scope.getPDPData = function(){
    	 $scope.pushTabPDPGrid.data = $scope.pdpdata;
    };
    $scope.filterPdpGroup;
    $scope.filterPDPGroupData = function() {
        $scope.pushTabPDPGrid.data = $filter('filter')($scope.pdpdata, $scope.filterPdpGroup, undefined);
    };
    
    $scope.pushTabPDPGrid = {	
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
        },
        enableFiltering: true,
        columnDefs: [
            { field: 'default',displayName : '', enableFiltering : false, enableSorting : false,
                cellTemplate: '<button  type="button"  ng-click="grid.appScope.editPDPGroupWindow(row.entity)"><a class="fa fa-pencil-square-o"></a></i></button> ',
                width: '5%'
            },
            { field: 'id', displayName : 'ID'},
            { field: 'name', displayName : 'Name' },
            { field: 'description' }
        ]
    };


    $scope.editPDPGroupWindow = function (selectedPdpGroupData) {
        $scope.removePDPGroupPolicies = selectedPdpGroupData;
        if($scope.isDisabled){
            Notification.error("Policy Application has been LockDown.");
        }else{
            var modalInstance = $modal.open({
            	backdrop: 'static', keyboard: false,
                templateUrl: 'remove_PDPGroupPolicies_popup.html',
                controller: 'removeGroupPoliciesController',
                resolve: {
                    message: function () {
                        var message = {
                            selectedPdpGroupData: $scope.removePDPGroupPolicies
                        };
                        return message;
                    }
                }
            });
            modalInstance.result.then(function (response) {
                console.log('response', response);
                $scope.pdpdata = JSON.parse(response.data);
                $scope.pushTabPDPGrid.data =  $scope.pdpdata;
            });
        }
    };

    $scope.gridOptions = {
    		data : 'policydatas',
    		 onRegisterApi: function(gridApi) {
    	            $scope.gridPolicyApi = gridApi;
    	        },
    		enableSorting: true,
    		enableFiltering: true,
    		showTreeExpandNoChildren: true,
    		paginationPageSizes: [10, 20, 50, 100],
    		paginationPageSize: 20,
    		columnDefs: [{name: 'policyName', displayName : 'Policy Name', sort: { direction: 'asc', priority: 0 }}, 
    		             {name: 'activeVersion', displayName : 'Version'}, 
    		             {name: 'modifiedDate', displayName : 'Last Modified',type: 'date', cellFilter: 'date:\'yyyy-MM-dd HH:MM:ss a\'' }]
    };
    
   
    PolicyAppService.getData('get_AutoPushPoliciesContainerData').then(function (data) {
    	$scope.loading = false;
    	var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.policydatas =JSON.parse($scope.data.policydatas);
		console.log($scope.policydatas);
       }, function (error) {
        console.log("failed");
    });
   
    $scope.pushPoliciesButton = function(){
    	var policySelection = $scope.gridPolicyApi.selection.getSelectedRows();
    	console.log(policySelection);
    	var currentSelection = $scope.gridApi.selection.getSelectedRows();
    	if(policySelection.length == 0 && currentSelection.length == 0){
    		Notification.error("Please Select Policy and PDP Group to Push");
    	}
    	if(policySelection.length == 0 && currentSelection.length != 0){
    		Notification.error("Please Select Policy to Push");
    	}
    	if(policySelection.length != 0 && currentSelection.length == 0){
    		Notification.error("Please Select PDP Group to Push");
    	}
    	if(policySelection.length != 0 && currentSelection.length != 0){
    		var finalData = {
    				"pdpDatas": currentSelection,
    				"policyDatas": policySelection
    		};
    		console.log(finalData);
    		var uuu = "auto_Push/PushPolicyToPDP.htm";
    		var postData={pushTabData: finalData};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.data=data.data;
    					$scope.pdpdata = JSON.parse(data.data);
    					$scope.pushTabPDPGrid.data =  $scope.pdpdata;
    					Notification.success("Policy Pushed Successfully");
    				});
    				console.log($scope.data);
    			},
    			error : function(data){
    				Notification.error("Error Occured while Pushing Policy.");
    			}
    		});

    	}
    };
  

});
