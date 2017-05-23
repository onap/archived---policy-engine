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
angular.module('abs').controller('fwPolicyController', ['$scope', '$window', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', function ($scope, $window, PolicyAppService, PolicyNavigator, modalService, $modal, Notification) {
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
		
    PolicyAppService.getData('getDictionary/get_SecurityZoneDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.securityZoneDictionaryDatas = JSON.parse($scope.data.securityZoneDictionaryDatas);
    	console.log($scope.securityZoneDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_TermListDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.termListDictionaryDatas = JSON.parse($scope.data.termListDictionaryDatas);
    	console.log($scope.termListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_FWDictionaryListDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.fwDictListDictionaryDatas = JSON.parse($scope.data.fwDictListDictionaryDatas);
    	console.log($scope.fwDictListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_FWParentListDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.fwParentListDictionaryDatas = JSON.parse($scope.data.fwParentListDictionaryDatas);
    	console.log($scope.fwParentListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_TagPickerNameByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.fwTagPickerDictionaryDatas = JSON.parse($scope.data.fwTagPickerDictionaryDatas);
    	console.log($scope.fwTagPickerDictionaryDatas);
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

    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.viewFWRule = function(policy){
        console.log(policy);
        var uuu = "policyController/ViewFWPolicyRule.htm";
        var postData={policyData: policy};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                	window.alert(data.policyData);
                });
            },
            error : function(data){
            	Notification.error("Error Occured while Showing Rule.");
            }
        });
    };
    
    
    $scope.saveFWPolicy = function(policy){
    	if(policy.itemContent != undefined){
    		$scope.refreshCheck = true; 
        	$scope.policyNavigator = policy.itemContent;
        	policy.itemContent = "";
    	}
    	$scope.savebutton = false;
        console.log(policy);
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
						$scope.pushStatus=data.policyData.split("&")[1];
						if($scope.pushStatus=="successPush"){
							Notification.success("Policy pushed successfully");
						}
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
    
    if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    	$scope.temp.policy.attributes = [];
    	$scope.temp.policy.fwattributes = [];
    }else{
	   if($scope.temp.policy.attributes.length == 0){
		   $scope.temp.policy.attributes = [];
	   }
	   if($scope.temp.policy.fwPolicyType == 'Parent Policy'){
		   if($scope.temp.policy.fwattributes.length == 0){
			   $scope.temp.policy.fwattributes = [];
		   }
	   }
   }
    
    $scope.attributeDatas = [{"ruleSetup" : $scope.temp.policy.attributes}];
    $scope.addNewChoice = function() {
      var newItemNo = $scope.temp.policy.attributes.length+1;
      $scope.temp.policy.attributes.push({'id':'choice'+newItemNo});
    };    
    $scope.removeChoice = function() {
      var lastItem = $scope.temp.policy.attributes.length-1;
      $scope.temp.policy.attributes.splice(lastItem);
    };
    
    $scope.fwattributeDatas = [{"parentSetup" : $scope.temp.policy.fwattributes}];
    $scope.addNewFWDictList = function() {
      var newItemNo = $scope.temp.policy.fwattributes.length+1;
      $scope.temp.policy.fwattributes.push({'id':'choice'+newItemNo});
    };    
    $scope.removefwDictChoice = function() {
      var lastItem = $scope.temp.policy.fwattributes.length-1;
      $scope.temp.policy.fwattributes.splice(lastItem);
    };
        
}]);