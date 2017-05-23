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
angular.module("abs").controller('clPMController', ['$scope', '$window', '$timeout', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', function($scope, $window, $timeout, PolicyAppService, PolicyNavigator, modalService, $modal, Notification){
	 $("#dialog").hide();
	 
	 $scope.policyNavigator;
	 $scope.savebutton = true;
	 $scope.refreshCheck = false;
	    
	 $scope.refresh = function(){
	   if($scope.refreshCheck){
	    	$scope.policyNavigator.refresh();
	    }
	    $scope.modal('createNewPolicy', true);
	    $scope.temp.policy = "";
	  };
	    
	  $scope.modal = function(id, hide) {
	      return $('#' + id).modal(hide ? 'hide' : 'show');
	  };
	 
	 PolicyAppService.getData('getDictionary/get_EcompNameDataByName').then(function (data) {
		 var j = data;
		 $scope.data = JSON.parse(j.data);
		 console.log($scope.data);
		 $scope.ecompNameDictionaryDatas = JSON.parse($scope.data.ecompNameDictionaryDatas);
		 console.log($scope.ecompNameDictionaryDatas);
	 }, function (error) {
		 console.log("failed");
	 });

	 PolicyAppService.getData('getDictionary/get_RiskTypeDataByName').then(function (data) {
		 var j = data;
		 $scope.data = JSON.parse(j.data);
		 console.log($scope.data);
		 $scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
		 console.log($scope.riskTypeDictionaryDatas);
	 }, function (error) {
		 console.log("failed");
	 });
			
	     	    
	 PolicyAppService.getData('app/policyApp/JSONDataFiles/JSONConfig.json').then(function(data){
	    	var j = data;
	        $scope.PMData = j;
	    });
	    
	    if($scope.temp.policy.readOnly && $scope.temp.policy.editPolicy){
	    	$scope.temp.policy.verticaMetrics = [];
	 	    $scope.temp.policy.description = [];
	 	    $scope.temp.policy.attributes = [];
	    }
	    
	    $scope.addDataToFields = function(serviceType){
	    	if($scope.PMData == undefined){
	    		$scope.temp.policy.verticaMetrics = [];
	    		$scope.temp.policy.description = [];
	    		$scope.temp.policy.attributes = [];
	    		PolicyAppService.getData('app/policyApp/JSONDataFiles/JSONConfig.json').then(function(data){
	    			var j = data;
	    			$scope.PMData = j;
	    			if(serviceType.serviceTypePolicyName == 'Registration Failure(Trinity)'){
	    				var myNewData = $scope.PMData[0];
	    			}else if(serviceType.serviceTypePolicyName == 'International Fraud(Trinity)'){
	    				var myNewData = $scope.PMData[1];
	    			}else if(serviceType.serviceTypePolicyName == 'No dial tone(Trinity)'){
	    				var myNewData = $scope.PMData[2];
	    			}else if(serviceType.serviceTypePolicyName == 'Call storm(Trinity)'){
	    				var myNewData = $scope.PMData[3];
	    			}else if(serviceType.serviceTypePolicyName == 'Registration storm(Trinity)'){
	    				var myNewData = $scope.PMData[4];
	    			}
	    			
	    			$scope.temp.policy.verticaMetrics = myNewData;
	    			$scope.temp.policy.description = myNewData;
	    			$scope.temp.policy.attributeFields = myNewData;
	    		});
	    	}else{
				$scope.temp.policy.verticaMetrics = serviceType;
				$scope.temp.policy.description = serviceType;
				$scope.temp.policy.attributeFields = serviceType;
	    	}
	    	
	    };
	    
	    $scope.saveCLPMPolicy = function(policy){
	    	if(policy.itemContent != undefined){
	    		$scope.refreshCheck = true; 
	        	$scope.policyNavigator = policy.itemContent;
	        	policy.itemContent = "";
	    	}
	        $scope.savebutton = false;
	        var uuu = "policycreation/save_policy";
			var postData={policyData: policy};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){
						$scope.data=data.policyData;
						if($scope.data == 'success'){
							$scope.temp.policy.readOnly = 'true';
							Notification.success("Policy Saved Successfully.");	
						}else if ($scope.data == 'PolicyExists'){
							$scope.savebutton = true;
							Notification.error("Policy Already Exists with Same Name in Scope.");
						}
					});
					console.log($scope.data);
				},
				error : function(data){
					Notification.error("Error Occured while saving Policy.");
				}
			});
	    };
	    
	    $scope.validatePolicy = function(policy){
	    	console.log(policy);
	    	document.getElementById("validate").innerHTML = "";
	         var uuu = "policyController/validate_policy.htm";
	 		var postData={policyData: policy};
	 		$.ajax({
	 			type : 'POST',
	 			url : uuu,
	 			dataType: 'json',
	 			contentType: 'application/json',
	 			data: JSON.stringify(postData),
	 			success : function(data){
	 				$scope.$apply(function(){
	 					$scope.validateData = data.data.replace(/\"/g, "");
	 						$scope.data=data.data.substring(1,8);
	 						var size = data.data.length;
	 						if($scope.data == 'success'){
	 							Notification.success("Validation Success.");
	 							$scope.savebutton = false;
	 							if (size > 18){
	 								var displayWarning = data.data.substring(19,size);
	 								document.getElementById("validate").innerHTML = "Safe Policy Warning Message  :  "+displayWarning;
	 								document.getElementById("validate").style.color = "white";
	 								document.getElementById("validate").style.backgroundColor = "skyblue";
	 							}	
	 						}else{
	 							Notification.error("Validation Failed.");
	 							document.getElementById("validate").innerHTML = $scope.validateData;
	 							document.getElementById("validate").style.color = "white";
	 							document.getElementById("validate").style.backgroundColor = "red";
	 							$scope.savebutton = true;
	 						}
	 						
	 				});
	 				console.log($scope.data);				
	 			},
	 			error : function(data){
	 				Notification.error("Validation Failed.");
	 				$scope.savebutton = true;
	 			}
	 		});
	    };
	  
}]);