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
angular.module('abs').controller('brmsRawPolicyController', function ($scope, $window, PolicyDictionaryService, PapUrlService, modalService, $modal, Notification) {
    $("#dialog").hide();
    
	$scope.temp.policy.ttlDate = new Date($scope.temp.policy.ttlDate);
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
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
    $scope.saveBrmsRawPolicy = function(policy){
        console.log(policy);
       /* var finalData = extend(policy, $scope.attributeDatas[0]);*/
        var uuu = "policyController/save_BRMSRawPolicy.htm";
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
    
    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }

    if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    	$scope.temp.policy.attributes = [];
    }else{
	   if($scope.temp.policy.attributes.length == 0){
		   $scope.temp.policy.attributes = [];
	   }
   }
    $scope.attributeDatas = [{"attributes" : $scope.temp.policy.attributes}];
    $scope.addNewChoice = function() {
      var newItemNo = $scope.temp.policy.attributes.length+1;
      $scope.temp.policy.attributes.push({'id':'choice'+newItemNo});
    };    
    $scope.removeChoice = function() {
      var lastItem = $scope.temp.policy.attributes.length-1;
      $scope.temp.policy.attributes.splice(lastItem);
    };
});