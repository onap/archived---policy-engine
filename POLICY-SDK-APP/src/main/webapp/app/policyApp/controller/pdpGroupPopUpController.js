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
app.controller('editPDPGrouppopupController' ,  function ($scope, $modalInstance, message, modalService, $modal, Notification){
	if(message.pdpGroupData==null){
		$scope.label='Add New PDP Group'
	}else{
		$scope.label='Edit PDP Group'
			$scope.disableCd=true;
		$scope.policies = message.pdpGroupData.policies;
		$scope.pdps = message.pdpGroupData.pdps;
		$scope.selectedPdp = message.pdpGroupData;
	}
	
	$scope.editPDPGroup = message.pdpGroupData;

	$scope.policiesGrid = {
		data : 'policies',
		enableFiltering: true,
		columnDefs: [
			{ field: 'root', displayName : 'Root', width : '10%'},
			{ field: 'name', displayName : 'Name' },
			{ field: 'version' , width : '10%'},
			{ field: 'id' }
		]
	};

	$scope.pdpsGrid = {
		data : 'pdps',
		enableFiltering: true,
		columnDefs: [{
			field: 'id', enableFiltering: false, headerCellTemplate: '' +
			'<button id=\'New\' ng-click="grid.appScope.createNewPDPInGroup()" class="btn btn-success">' + 'Create PDP</button>',
			cellTemplate:
			'<button  type="button"  class="btn btn-primary" ng-click="grid.appScope.editPDPInGroup(row.entity)"><i class="fa fa-pencil-square-o"></i></button> ' +
			'<button  type="button"  class="btn btn-danger"  ng-click="grid.appScope.deletePDPFromGroup(row.entity)" ><i class="fa fa-trash-o"></i></button> '+
			'<button  type="button"  class="btn btn-success" ng-click="grid.appScope.statusOfPDP(row.entity.status)" >Status</button>',  width: '25%'
		},
			{ field: 'id', displayName : 'PDP URL'},
			{ field: 'jmxPort', displayName : 'JMX Port' , width : '10%' },
			{ field: 'name' , displayName : 'PDP Name'},
			{ field: 'description' }
		]
	};

	$scope.createNewPDPInGroup = function(pdpInGroup) {
		$scope.pdpInGroup = null;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'create_newPDP_InGroup.html',
			controller: 'pdpInGroupController',
			resolve: {
				message: function () {
					var message = {
						data: $scope.pdpInGroup,
						activePDP : $scope.selectedPdp
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			refreshPDPGroupDatas(response);
		});
	};

	$scope.editPDPInGroup = function(pdpInGroup) {
		$scope.editPDPInGroupData = pdpInGroup;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl: 'create_newPDP_InGroup.html',
			controller: 'pdpInGroupController',
			resolve: {
				message: function () {
					var message = {
						pdpInGroup : $scope.editPDPInGroupData,
						activePDP : $scope.selectedPdp
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			refreshPDPGroupDatas(response);
		});
	};

	$scope.deletePDPFromGroup = function(data){
		modalService.popupConfirmWin("Confirm","You are about to delete the PDP Group :  "+data.name+". Do you want to continue?",
				function(){
			var uuu = "pdp_Group/remove_pdpFromGroup.htm";
			var postData={data: data,
					activePDP : $scope.selectedPdp};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){
						refreshPDPGroupDatas(data);
					});
					Notification.success("PDP Group Deleted Successfully");
				},
				error : function(data){
					console.log(data);
					Notification.error("Error Occured While Deleting a PDP Group");
				}
			});
		})
	};

	$scope.statusOfPDP = function(status){
		$scope.pdpStatus = status;
		console.log($scope.pdpStatus);
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl: 'pdpGroupStatusWindow.html',
			controller: 'pdpGroupStatusController',
			resolve: {
				message: function () {
					var message = {
						status : $scope.pdpStatus,
						policies : $scope.policies
					};
					return message;
				}
			}
		});
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.data=response.data;
		});
	};

	$scope.savePDPGroup = function(pdpGroupData) {
		var uuu = "pdp_Group/save_pdp_group.htm";
		var postData={pdpGroupData: pdpGroupData};
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
				Notification.error("Error while saving PDP Group.");
			}
		});
	};
	
	function refreshPDPGroupDatas(response){
		$scope.selectedPDPName = $scope.selectedPdp.id;
		if(response != undefined){
			$scope.data = JSON.parse(response.data);
			for(var i=0; i< $scope.data.length; i++){
				if($scope.data[i].id === $scope.selectedPDPName){
					$scope.policies = $scope.data[i].policies;
			        $scope.pdps = $scope.data[i].pdps;
				}
			}
		}
	};

	$scope.close = function() {
		$modalInstance.close();
	};
});