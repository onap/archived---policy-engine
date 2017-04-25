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
app.controller('pdpGroupStatusController',  function ($scope, $modalInstance, message){
    if(message.status==null) {
        $scope.label = 'No Status to Display'
    }else{
        $scope.label='Status'
        $scope.disableCd=true;
        $scope.policies = message.policies;
        $scope.pdpStatusDatas = message.status;
    }

    $scope.policiesGrid = {
    		data : 'policies',
    		enableFiltering: true,
    		columnDefs: [	
    			{ field: 'name', displayName : 'Name' }	
    		]
    };

    $scope.close = function() {
        $modalInstance.close();
    };
});