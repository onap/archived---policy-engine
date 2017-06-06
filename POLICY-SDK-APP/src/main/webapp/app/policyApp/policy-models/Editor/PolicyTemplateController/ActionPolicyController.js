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
app.controller('actionPolicyController', ['$scope', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', function ($scope, PolicyAppService, PolicyNavigator, modalService, $modal, Notification) {
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
    
    PolicyAppService.getData('getDictionary/get_ActionPolicyDictDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.actionPolicyDictionaryDatas = JSON.parse($scope.data.actionPolicyDictionaryDatas);
    	console.log($scope.actionPolicyDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_AttributeDatabyAttributeName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.attributeDictionaryDatas = JSON.parse($scope.data.attributeDictionaryDatas);
    	console.log($scope.attributeDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('get_FunctionDefinitionDataByName').then(function (data) {
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
			},
			error : function(data){
				Notification.error("Error Occured while saving Policy.");
				$scope.savebutton = true;
			}
		});
    };

    $scope.validatePolicy = function(policy){
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
 						$scope.data=data.data;
 						if($scope.data == '"success"'){
 							Notification.success("Validation Success.");
 							$scope.savebutton = false;
 						}else{
 							Notification.error("Validation Failed.");
 							document.getElementById("validate").innerHTML = $scope.validateData;
 							document.getElementById("validate").style.color = "white";
 							document.getElementById("validate").style.backgroundColor = "red";
 							$scope.savebutton = true;
 						}
 						
 				});
 			},
 			error : function(data){
 				Notification.error("Validation Failed.");
 				$scope.savebutton = true;
 			}
 		});
    };

    if(!$scope.temp.policy.editPolicy && !$scope.temp.policy.readOnly){
    	$scope.temp.policy.attributes = [];
    	$scope.temp.policy.attributes.push({'id':'choice'+1});
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
        }
      $scope.temp.policy.ruleAlgorithmschoices.push({'id':'A'+newItemNo});

    };
    
    $scope.removeRuleAlgorithm = function() {
      var lastItem = $scope.temp.policy.ruleAlgorithmschoices.length-1;
      $scope.ItemNo = lastItem; 
      $scope.temp.policy.ruleAlgorithmschoices.splice(lastItem);
    };
    
}]);