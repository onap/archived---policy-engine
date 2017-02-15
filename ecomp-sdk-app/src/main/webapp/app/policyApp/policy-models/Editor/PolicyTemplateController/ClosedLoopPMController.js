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
angular.module("abs").controller('clPMController', function($scope, $window, $timeout, PapUrlService, ClosedLoopPMService, PolicyDictionaryService, modalService, $modal, Notification){
	 $("#dialog").hide();
	 
		$scope.temp.policy.ttlDate = new Date($scope.temp.policy.ttlDate);
		var papUrl;
		PapUrlService.getPapUrl().then(function(data) {
			var config = data;
			papUrl = config.PAP_URL;
			console.log(papUrl);
			
		    PolicyDictionaryService.getEcompDictionaryData(papUrl).then(function (data) {
		        var j = data;
		        $scope.data = JSON.parse(j.data);
		        console.log($scope.data);
		        $scope.ecompNameDictionaryDatas = JSON.parse($scope.data.ecompNameDictionaryDatas);
		        console.log($scope.ecompNameDictionaryDatas);
		    }, function (error) {
		        console.log("failed");
		    });
		    PolicyDictionaryService.getRiskTypeDictionaryData(papUrl).then(function (data) {
		        var j = data;
		        $scope.data = JSON.parse(j.data);
		        console.log($scope.data);
		        $scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
		        console.log($scope.riskTypeDictionaryDatas);
		    }, function (error) {
		        console.log("failed");
		    });
		    PolicyDictionaryService.getRiskLevelValueData().then(function (data) {
		        var j = data;
		        $scope.data = JSON.parse(j.data);
		        console.log($scope.data);
		        $scope.riskLevelDatas = JSON.parse($scope.data.riskLevelDatas);
		        console.log($scope.riskLevelDatas);
		    }, function (error) {
		        console.log("failed");
		    });
		    PolicyDictionaryService.getGuardValueData().then(function (data) {
		        var j = data;
		        $scope.data = JSON.parse(j.data);
		        console.log($scope.data);
		        $scope.guardDatas = JSON.parse($scope.data.guardDatas);
		        console.log($scope.guardDatas);
		    }, function (error) {
		        console.log("failed");
		    });
			
		});
	     	    
	    ClosedLoopPMService.getPMJSONBodyData().then(function(data){
	    	var j = data;
	        $scope.PMData = j;
	    });
	    
	    $scope.temp.policy.verticaMetrics = [];
	    $scope.temp.policy.description = [];
	    $scope.temp.policy.attributes = [];
	    
	    $scope.addDataToFields = function(serviceType){
	    	if($scope.PMData == undefined){
	    		$scope.temp.policy.verticaMetrics = [];
	    		$scope.temp.policy.description = [];
	    		$scope.temp.policy.attributes = [];
	    		ClosedLoopPMService.getPMJSONBodyData().then(function(data){
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
	    		var key = $scope.PMData.indexOf(serviceType);
				var myNewData = $scope.PMData[key];
				$scope.temp.policy.verticaMetrics = myNewData;
				$scope.temp.policy.description = myNewData;
				$scope.temp.policy.attributeFields = myNewData;
	    	}
	    	
	    };
	    
	    $scope.saveCLPMPolicy = function(policy){
	        console.log(policy);
	        console.log();
	        var uuu = "policyController/save_PMPolicy.htm";
			var postData={policyData: policy};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){
							$scope.data=data.data;
							$scope.temp.policy.readOnly = 'true';
							Notification.success("Policy Saved Successfully.");
					});
					console.log($scope.data);
					$modalInstance.close();
					
				},
				error : function(data){
					Notification.error("Error Occured while saving Policy.");
				}
			});
	    };
	    
	    $scope.validatePolicy = function(policy){
	    	console.log(policy);
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
	 						$scope.data=data.data.substring(1,8);
	 						var size = data.data.length;
	 						if($scope.data == 'success'){
	 							Notification.success("Validation Success.");
	 							if (size > 18){
	 								var displayWarning = data.data.substring(19,size);
	 								window.alert(displayWarning);
	 							}	
	 						}else{
	 							Notification.error("Validation Failed.");
	 						}
	 						
	 				});
	 				console.log($scope.data);
	 				/*$modalInstance.close();*/
	 				
	 			},
	 			error : function(data){
	 				Notification.error("Validation Failed.");
	 			}
	 		});
	    };
	  
})