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
angular.module('abs').controller('brmsRawPolicyController', ['$scope', '$window', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', function ($scope, $window, PolicyAppService, PolicyNavigator, modalService, $modal, Notification) {
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
    
    $('#ttlDate').datepicker({
    	dateFormat: 'dd/mm/yy',
    	changeMonth: true,
    	changeYear: true,
    	onSelect: function(date) {
    		angular.element($('#ttlDate')).triggerHandler('input');
    	}
    });
    
    PolicyAppService.getData('getDictionary/get_BRMSControllerDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.brmsControllerDatas = JSON.parse($scope.data.brmsControllerDictionaryDatas);
    	console.log($scope.brmsControllerDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_BRMSDependencyDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.brmsDependencyDatas = JSON.parse($scope.data.brmsDependencyDictionaryDatas);
    	console.log($scope.brmsDependencyDatas);
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

    $scope.saveBrmsRawPolicy = function(policy){
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
				$scope.savebutton = true;
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
}]);