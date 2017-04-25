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
app.controller('policyDashboardHealthController', function ($scope, PolicyAppService, modalService, $modal){
	$( "#dialog" ).hide();
    $scope.pdpTableDatas = [];
    $scope.papTableDatas = [];
    $scope.policyActivityTableDatas = [];
    
    PolicyAppService.getData('get_DashboardSystemAlertData').then(function(data){
		var j = data;
  		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
  		$scope.systemAlertsTableDatas =JSON.parse($scope.data.systemAlertsTableDatas);
	},function(error){
		console.log("failed");
	});

    PolicyAppService.getData('get_DashboardPAPStatusData').then(function(data){
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.papTableMapDatas =JSON.parse($scope.data.papTableDatas);
        if($scope.papTableMapDatas != null){
            for(i = 0; i < $scope.papTableMapDatas.length; i++){
                $scope.papTableDatas.push($scope.papTableMapDatas[i].map);
            }
        }
	},function(error){
		console.log("failed");
	});

    PolicyAppService.getData('get_DashboardPDPStatusData').then(function(data){
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
        $scope.pdpTableMapDatas =JSON.parse($scope.data.pdpTableDatas);
        if($scope.pdpTableMapDatas != null) {
            for (i = 0; i < $scope.pdpTableMapDatas.length; i++) {
                $scope.pdpTableDatas.push($scope.pdpTableMapDatas[i].map);
            }
        }
	},function(error){
		console.log("failed");
	});

    PolicyAppService.getData('get_DashboardPolicyActivityData').then(function(data){
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.policyActivityTableMapDatas =JSON.parse($scope.data.policyActivityTableDatas);
        if($scope.policyActivityTableMapDatas != null) {
            for (i = 0; i < $scope.policyActivityTableMapDatas.length; i++) {
                $scope.policyActivityTableDatas.push($scope.policyActivityTableMapDatas[i].map);
            }
        }
	},function(error){
		console.log("failed");
	});
	
	$scope.availableGridHealthDatas = {
			data : 'systemAlertsTableDatas',
			    enableFiltering: true,
			    columnDefs: [{ field: 'id'},
			      { field: 'type'},
			      { field: 'system'},
			      {field: 'logtype'},
			      {field : 'date' ,type: 'date', cellFilter: 'date:\'yyyy-MM-dd\'' },
			      {field : 'description'}
			  ],	  
	};

	$scope.papStatusDatas = {
		data : 'papTableDatas',
		enableFiltering: true,
		columnDefs: [{ field: 'system'},
			{ field: 'status'},
			{ field: 'noOfPolicy'},
			{field: 'noOfConnectedTrap'}
		],
	};

	$scope.pdpStatusDatas = {
		data : 'pdpTableDatas',
		enableFiltering: true,
		columnDefs: [{ field: 'id'},
			{ field: 'name'},
			{ field: 'groupname'},
			{field: 'status'},
			{field : 'description' },
			{field : 'permitCount'},
			{field : 'denyCount'},
			{field : 'naCount'}
		],
	};

	$scope.policyActivityDatas = {
		data : 'policyActivityTableDatas',
		enableFiltering: true,
		columnDefs: [{ field: 'policyId'},
			{ field: 'fireCount'},
			{ field: 'system'}
		],
	};
});