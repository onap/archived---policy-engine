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
 * 
 */
app.controller("policyAdminController", function($scope, AdminTabService, modalService, $modal, Notification){
	$( "#dialog" ).hide();
	
	$scope.isDisabled = true;
	AdminTabService.getData().then(function(data){
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
	
	 $scope.saveLockDownValue = function(lockdownValue){
	        console.log(lockdownValue);
		 	if(lockdownValue == true){
			 	Notification.success("Policy Application has been Locked Successfully");
			 	 $scope.isDisabled = true;
		 	}else{
				Notification.success("Policy Application has been UnLocked Successfully");
				$scope.isDisabled = false;
		 	}
	        var uuu = "adminTabController/save_LockDownValue.htm";
			var postData={lockdowndata: {lockdown : lockdownValue}};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){
							$scope.data=data.data;
					});
					console.log($scope.data);
				},
				error : function(data){
					alert("Error Occured while saving Lockdown Value.");
				}
			});
	    };
});