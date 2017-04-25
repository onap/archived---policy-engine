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
angular.module('abs').controller('brmsParamPolicyController', function ($scope, $window, PolicyAppService, modalService, $modal, Notification) {
    $("#dialog").hide();

    $scope.savebutton = true;
    $scope.finalPath = null;
    
    $scope.validateSuccess = true;
    var readValue = $scope.temp.policy.readOnly;
    if(readValue){
    	 $scope.validateSuccess = false;
    }
    
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

    PolicyAppService.getData('getDictionary/get_BRMSParamDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.brmsParamDictionaryDatas = JSON.parse($scope.data.brmsParamDictionaryDatas);
    	console.log($scope.brmsParamDictionaryDatas);
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
    
    $scope.temp.policy.dynamicLayoutMap = {};
    $scope.addDataToFields = function(ruleName){
    	console.log(ruleName);   
        if(ruleName != null){
        	 var uuu = "policyController/getBRMSTemplateData.htm";
        	 var postData={policyData: ruleName};
             $.ajax({
                 type : 'POST',
                 url : uuu,
                 dataType: 'json',
                 contentType: 'application/json',
                 data: JSON.stringify(postData),
                 success : function(data){
                     $scope.$apply(function(){
                    	 $scope.temp.policy.dynamicLayoutMap = data.policyData;
                     });
                     console.log( $scope.temp.policy.dynamicLayoutMap);
                 },
                 error : function(data){
                     alert("Error While Retriving the Template Layout Pattren.");
                 }
             });
        }   
    };
    
    $scope.ShowRule = function(policy){
        console.log(policy);
        var uuu = "policyController/ViewBRMSParamPolicyRule.htm";
        var postData={policyData: policy};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                	$scope.datarule = data.policyData;
                	var modalInstance = $modal.open({
                    	backdrop: 'static', keyboard: false,
                        templateUrl : 'brmsshowrule',
                        controller: 'showrulecontroller',
                        resolve: {
                            message: function () {
                                var message = {
                                    datas: $scope.datarule
                                };
                                return message;
                            }
                        }
                    });
                });
            },
            error : function(data){
            	Notification.error("Error Occured while Showing Rule.");
            }
        });
    };
    
    $scope.saveBrmsParamPolicy = function(policy){
        console.log(policy);
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
                    	$scope.safetyChecker = data.policyData.split("#")[2];
                    	if ($scope.safetyChecker!=undefined) {
                    		Notification.success($scope.safetyChecker);
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
 							$scope.validateSuccess = false;
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

app.controller('showrulecontroller' ,  function ($scope, $modalInstance, message){
	if(message.datas!=null){
		$scope.datarule=message.datas;
	}
	
	$scope.close = function() {
        $modalInstance.close();
    };

});