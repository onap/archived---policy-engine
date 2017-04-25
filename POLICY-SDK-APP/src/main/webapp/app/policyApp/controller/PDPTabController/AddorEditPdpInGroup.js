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
app.controller('pdpInGroupController' ,  function ($scope, $modalInstance, message, Notification){
	$scope.edit = 'false';
    if(message.pdpInGroup==null)
        $scope.label='Add PDP to Group'
    else{
        $scope.label='Edit PDP In Group'
        $scope.disableCd=true;
        $scope.edit = 'true';
    }
    $scope.editPDPInGroup = message.pdpInGroup;
    $scope.editActivePDP = message.activePDP;
    
    $scope.savePDPInGroup = function(pdpInGroup) {
        var uuu = "pdp_Group/save_pdpTogroup.htm";
        var postData={pdpInGroup: pdpInGroup,
        		activePDP: $scope.editActivePDP,
        		update : $scope.edit};
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
            },
            error : function(data){
               Notification.error("Error Occured while Creating/Updating a PDP Group");
            }
        });
    };

    $scope.close = function() {
        $modalInstance.close();
    };
});

