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
angular.module('abs').controller('actionPolicyController', function ($scope, PapUrlService, PolicyDictionaryService,modalService, $modal, Notification) {
    $("#dialog").hide();
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    PolicyDictionaryService.getActionPolicyDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.actionPolicyDictionaryDatas = JSON.parse($scope.data.actionPolicyDictionaryDatas);
	        console.log($scope.actionPolicyDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyDictionaryService.getAttributeDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.attributeDictionaryDatas = JSON.parse($scope.data.attributeDictionaryDatas);
	        console.log($scope.attributeDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	});
  
    PolicyDictionaryService.getFunctionDefinitionData().then(function (data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.functionDefinitionDatas = JSON.parse($scope.data.functionDefinitionDatas);
        console.log($scope.functionDefinitionDatas);
    }, function (error) {
        console.log("failed");
    });

    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.saveActionPolicy = function(policy){
        console.log(policy);
        /*var attributeData = extend(policy, $scope.attributeDatas[0]);*/
       // var finalData = extend(policy, $scope.ruleAlgorithmDatas[0]);
        //console.log(finalData);
        var uuu = "policyController/save_Actionpolicy.htm";
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
 						$scope.data=data.data;
 						if($scope.data == '"success"'){
 							Notification.success("Validation Success.");
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

    if(!$scope.temp.policy.editPolicy && !$scope.temp.policy.readOnly){
    	$scope.temp.policy.attributes = [];
    	 $scope.temp.policy.ruleAlgorithmschoices = [];
    }else{
	   if($scope.temp.policy.attributes.length == 0){
		   $scope.temp.policy.attributes = [];
	   }
	   if($scope.temp.policy.ruleAlgorithmschoices.length == 0){
		   $scope.temp.policy.ruleAlgorithmschoices = [];
	   }
   }
  
    $scope.attributeDatas = [{"attributes" : $scope.temp.policy.attributes}];
    $scope.addNewChoice = function() {
        var newItemNo = $scope.temp.policy.attributes .length+1;
        $scope.temp.policy.attributes.push({'id':'choice'+newItemNo});
    };
    $scope.removeChoice = function() {
        var lastItem = $scope.temp.policy.attributes.length-1;
        $scope.temp.policy.attributes.splice(lastItem);
    };
    
   
    $scope.ItemNo = 0;
    $scope.ruleAlgorithmDatas = [{"ruleAlgorithms" : $scope.temp.policy.ruleAlgorithmschoices}];
    
    $scope.addNewRuleAlgorithm = function(){
      var newItemNo = $scope.temp.policy.ruleAlgorithmschoices.length+1;
      $scope.ItemNo = newItemNo;
        if(newItemNo > 1){
            var value = newItemNo-1;
            $scope.attributeDictionaryDatas.push('A'+value);
            $scope.$apply();
        }
      $scope.temp.policy.ruleAlgorithmschoices.push({'id':'A'+newItemNo});

    };
    
    $scope.removeRuleAlgorithm = function() {
      var lastItem = $scope.temp.policy.ruleAlgorithmschoices.length-1;
      $scope.ItemNo = lastItem; 
      $scope.temp.policy.ruleAlgorithmschoices.splice(lastItem);
    };
    
});