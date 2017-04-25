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
app.controller('removeGroupPoliciesController' ,  function ($scope, $modalInstance, message, Notification){
    if(message.selectedPdpGroupData !=null){
        $scope.label='Remove PDP Group Policies'
        $scope.disableCd=true;
    }
    $scope.policies = message.selectedPdpGroupData.policies;
    $scope.pdpGroupData = message.selectedPdpGroupData;

    $scope.removePoliciesGrid = {
        data : 'policies',
        enableFiltering: true,
        columnDefs: [
            { field: 'root', displayName : 'Root', width : '10%'},
            { field: 'name', displayName : 'Name' },
            { field: 'version' , width : '10%'},
            { field: 'id' }
        ]
    };

    $scope.removePoliciesGrid.onRegisterApi = function(gridApi){
        //set gridApi on scope
        $scope.gridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope,function(row){
            var msg = 'row selected ' + row.isSelected;
        });

        gridApi.selection.on.rowSelectionChangedBatch($scope,function(rows){
            var msg = 'rows changed ' + rows.length;
        });
    };

    $scope.removePolicies = function() {
    	$scope.removeGroupData = [];
        angular.forEach($scope.gridApi.selection.getSelectedRows(), function (data, index) {
        	$scope.removeGroupData.push(data);    
        });
        if($scope.removeGroupData.length > 0){
        	 var uuu = "auto_Push/remove_GroupPolicies.htm";
             var postData={data: $scope.removeGroupData,
             		activePdpGroup : $scope.pdpGroupData};
             $.ajax({
                 type : 'POST',
                 url : uuu,
                 dataType: 'json',
                 contentType: 'application/json',
                 data: JSON.stringify(postData),
                 success : function(data){
                     $scope.$apply(function(){
                         $scope.data=data.data;});
                     console.log($scope.data);
                     $modalInstance.close({data:$scope.data});
                     Notification.success("Policy Removed Successfully");
                 },
                 error : function(data){
                 	Notification.error("Error Occured while removing Policy");
                 }
             });
        }else{
        	Notification.error("Please select atleast one Policy to remove");
        }
    };

    $scope.close = function() {
        $modalInstance.close();
    };
});