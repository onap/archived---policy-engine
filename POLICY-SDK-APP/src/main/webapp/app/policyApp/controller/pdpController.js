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
app.controller('pdpTabController', function ($scope, PolicyAppService, modalService, $modal){
	$( "#dialog" ).hide();

	 $scope.isDisabled = true;
	 $scope.createPdpGroupId = false;
	 $scope.deletePdpGroupId = false;
	 $scope.editPdpGroupId = false;
	 $scope.pdpdatas = [];
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

		 PolicyAppService.getData('get_UserRolesData').then(function(data) {
			 var j = data;
			 $scope.data = JSON.parse(j.data);
			 console.log($scope.data);
			 $scope.userRolesDatas = JSON.parse($scope.data.userRolesDatas);
			 console.log($scope.userRolesDatas);
			 if($scope.isDisabled == false){
				 if($scope.userRolesDatas[0] == 'super-admin' || $scope.userRolesDatas[0] == 'admin'){
					 $scope.createPdpGroupId = true;
					 $scope.deletePdpGroupId = true;
					 $scope.editPdpGroupId = true;
				 }else if($scope.userRolesDatas[0] == 'super-editor' || $scope.userRolesDatas[0] == 'editor'){
					 $scope.editPdpGroupId = true;
				 }else if($scope.userRolesDatas[0] == 'super-guest' || $scope.userRolesDatas[0] == 'guest'){
					 $scope.editPdpGroupId = true;
				 }   
			 } 
			 getPDPGroups();
		 },function (error) {
			 console.log("failed");
		 });
	 },function(error){
		 console.log("failed");
	 });
		 
	
	$scope.addNewPDPGroupFunctionPopup = function() {
		$scope.editPDPGroup = null;
		$( "#dialog" ).dialog({
			modal: true
		});
	};

	$scope.editPDPGroup = null;
	$scope.editPDPGroupFunctionPopup = function(pdpGroupData) {
		$scope.editPDPGroup = pdpGroupData;
		$( "#dialog" ).dialog({
			modal: true
		});
	};

	$scope.editPDPGroupFunctionModalPopup = function(pdpGroupData) {
		$scope.editPDPGroup = pdpGroupData;
		var modalInstance;
		if($scope.userRolesDatas[0] == 'super-guest' || $scope.userRolesDatas[0] == 'guest'){
			modalInstance = $modal.open({
				backdrop: 'static', keyboard: false,
				templateUrl: 'show_policies_pdp_group_popup.html',
				controller: 'editPDPGrouppopupController',
				resolve: {
					message: function () {
						var message = {
								pdpGroupData : $scope.editPDPGroup
						};
						return message;
					}					
				}
			}); 
		}else{
			modalInstance = $modal.open({
				backdrop: 'static', keyboard: false,
				templateUrl: 'edit_pdp_group_popup.html',
				controller: 'editPDPGrouppopupController',
				resolve: {
					message: function () {
						var message = {
								pdpGroupData : $scope.editPDPGroup
						};
						return message;
					}					
				}
			}); 
		}  
		modalInstance.result.then(function(response){
			console.log('response', response);
			getPDPGroups();
		});
	};

	function getPDPGroups(){
		PolicyAppService.getData('get_PDPGroupData').then(function(data){
			var j = data;
			$scope.pdpdatas = JSON.parse(j.data);
			console.log($scope.pdpdatas);
		},function(error){
			console.log("failed");
		});
	}
	
	$scope.addNewPDPGroupPopUpWindow = function(editPDPGroup) {
		$scope.editPDPGroup = null;
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'add_pdp_group_popup.html',
			controller: 'editPDPGrouppopupController',
			resolve: {
				message: function () {
					var message = {
						data: $scope.editPDPGroup
					};
					return message;
				}					
			}
		}); 
		modalInstance.result.then(function(response){
			console.log('response', response);
			$scope.pdpdatas=JSON.parse(response.data);
		});
	};

	$scope.addNewPDPGroupFunctionPopup = function() {
		$scope.editPDPGroup = null;
		$( "#dialog" ).dialog({
			modal: true
		});
	};

	$scope.removePDPGroup = function(pdpGroupData) {
		modalService.popupConfirmWin("Confirm","You are about to delete the PDP Group  "+pdpGroupData.name+". Do you want to continue?",
				function(){
			var uuu = "pdp_Group/remove_pdp_group.htm";
			var postData={pdpGroupData: pdpGroupData};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(response){
					$scope.$apply(function(){$scope.pdpdatas=JSON.parse(response.data);});
				},
				error : function(data){
					console.log(data);
					modalService.showFailure("Fail","Error while deleting: "+ data.responseText);
				}
			});

		})

	};
});
