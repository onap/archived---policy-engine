/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
angular.module('abs').controller('decisionPolicyController', ['$scope', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', '$http', function ($scope, PolicyAppService, PolicyNavigator, modalService, $modal, Notification, $http) {
    $("#dialog").hide();
    
    $scope.policyNavigator;
    $scope.savebutton = true;
    $scope.refreshCheck = false;
    $scope.disableOnCreate = false;
    
    if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    	$scope.disableOnCreate = true;
    	$scope.temp.policy = {
    			policyType : "Decision"
    	}
    };
    
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

	if($scope.temp.policy.ruleProvider==undefined){
		$scope.temp.policy.ruleProvider="Custom";
	}
	
	if($scope.temp.policy.blackListEntryType==undefined){
		$scope.temp.policy.blackListEntryType="Use Manual Entry";
	}
	
	PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);
		console.log($scope.onapNameDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_SettingsDictionaryDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.settingsDictionaryDatas = JSON.parse($scope.data.settingsDictionaryDatas);
		console.log($scope.settingsDictionaryDatas);
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

	PolicyAppService.getData('getDictionary/get_AttributeDatabyAttributeName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.attributeDictionaryDatas = JSON.parse($scope.data.attributeDictionaryDatas);
		console.log($scope.attributeDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_RainyDayDictionaryDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
    	$scope.rainyDayDictionaryDatas = JSON.parse($scope.data.rainyDayDictionaryDatas);
		console.log($scope.rainyDayDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});
	
    PolicyAppService.getData('getDictionary/get_RainyDayDictionaryData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.rainyDayDictionaryDataEntity = JSON.parse($scope.data.rainyDayDictionaryDatas);
    	console.log($scope.rainyDayDictionaryDatasEntity);
    }, function (error) {
    	console.log("failed");
    });

    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.saveDecisionPolicy = function(policy){
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
    	$scope.temp.policy.settings = [];
    	$scope.temp.policy.ruleAlgorithmschoices = [];
    	if(!$scope.temp.policy.yamlparams){
    		$scope.temp.policy.yamlparams = {};
    	}
    	if(!$scope.temp.policy.yamlparams.targets){
    		$scope.temp.policy.yamlparams.targets = [];
    	}
    	if(!$scope.temp.policy.yamlparams.blackList){
    		$scope.temp.policy.yamlparams.blackList = [];
    	}
    	if(!$scope.temp.policy.rainyday){
    		$scope.temp.policy.rainyday = {};
    	}
    	if(!$scope.temp.policy.rainyday.treatmentTableChoices){
    		$scope.temp.policy.rainyday.treatmentTableChoices = [];
    	}
    
    }else if($scope.temp.policy.ruleProvider=="Custom"){
	   if($scope.temp.policy.attributes.length == 0){
		   $scope.temp.policy.attributes = [];    
	   }
	   if($scope.temp.policy.settings.length == 0){
		   $scope.temp.policy.settings = [];
	   }
	   if($scope.temp.policy.ruleAlgorithmschoices == null || $scope.temp.policy.ruleAlgorithmschoices.length == 0){
		   $scope.temp.policy.ruleAlgorithmschoices = [];
	   }
    }else if($scope.temp.policy.ruleProvider=="GUARD_BL_YAML"){
    	if($scope.temp.policy.yamlparams.blackList == null || $scope.temp.policy.yamlparams.blackList.length==0){
    		$scope.temp.policy.yamlparams.blackList = [];
    	}
    	if($scope.temp.policy.blackListEntries == null || $scope.temp.policy.blackListEntries.length==0){
    		$scope.temp.policy.blackListEntries = [];
    	}
    	$scope.blackListEntries = [];
    	$scope.temp.policy.appendBlackListEntries = [];
    	$scope.blackListEntries = arrayUnique($scope.temp.policy.blackListEntries.concat($scope.temp.policy.yamlparams.blackList));
    }else if($scope.temp.policy.ruleProvider=="GUARD_YAML"){
    	if($scope.temp.policy.yamlparams.targets.length==0){
 		   $scope.temp.policy.yamlparams.targets = [];
 	   	}
    }else if($scope.temp.policy.ruleProvider=="Rainy_Day"){
    	if($scope.temp.policy.rainyday.treatmentTableChoices == null || $scope.temp.policy.rainyday.treatmentTableChoices.length == 0){
    		$scope.temp.policy.rainyday.treatmentTableChoices = [];
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
    
    $scope.settingsDatas = [{"settings" : $scope.temp.policy.settings}];
    $scope.addNewSettingsChoice = function() {
      var newItemNo = $scope.temp.policy.settings.length+1;
      $scope.temp.policy.settings.push({'id':'choice'+newItemNo});
    };    
    $scope.removeSettingsChoice = function() {
      var lastItem = $scope.temp.policy.settings.length-1;
      $scope.temp.policy.settings.splice(lastItem);
    };
    
    $scope.addNewTarget = function(){
    	$scope.temp.policy.yamlparams.targets.push('');
    };
    $scope.removeTarget = function(){
    	var lastItem = $scope.temp.policy.yamlparams.targets.length-1;
    	$scope.temp.policy.yamlparams.targets.splice(lastItem);
    };
    
    $scope.addNewBL = function() {
    	$scope.temp.policy.yamlparams.blackList.push('');
    };
    
    $scope.removeBL = function(id) {
    	$scope.temp.policy.yamlparams.blackList = $scope.temp.policy.yamlparams.blackList.filter(function (obj){
			return obj !== id;
		});
    };
    
    $scope.treatmentDatas = [{"treatmentValues" : $scope.temp.policy.rainyday.treatmentTableChoices}];
    $scope.addNewTreatment = function() {
    	$scope.temp.policy.rainyday.treatmentTableChoices.push({});
    };
    $scope.removeTreatment = function() {
    	var lastItem = $scope.temp.policy.rainyday.treatmentTableChoices.length-1;
    	$scope.temp.policy.rainyday.treatmentTableChoices.splice(lastItem);
    };
    
	$scope.workstepDictionaryDatas = [];
	$scope.getWorkstepValues = function(bbidValue){
		for (var i = 0; i < $scope.rainyDayDictionaryDataEntity.length; ++i) {
    	    var obj = $scope.rainyDayDictionaryDataEntity[i];
    	    if (obj.bbid == bbidValue){
    	    	$scope.workstepDictionaryDatas.push(obj.workstep);
    	    }
    	}
	};
	
	$scope.allowedTreatmentsDatas = [];
	$scope.getTreatmentValues = function(bbidValue, workstepValue){
		for (var i = 0; i < $scope.rainyDayDictionaryDataEntity.length; ++i) {
    	    var obj = $scope.rainyDayDictionaryDataEntity[i];
    	    if (obj.bbid == bbidValue && obj.workstep == workstepValue){
    	    	var splitAlarm = obj.treatments.split(',');
    	    	for (var j = 0; j < splitAlarm.length; ++j) {
    	    		$scope.allowedTreatmentsDatas.push(splitAlarm[j]);
    	    	}
    	    }
    	}	
	};
    
    $scope.ItemNo = 0;
    $scope.ruleAlgorithmDatas = [{"ruleAlgorithms" : $scope.temp.policy.ruleAlgorithmschoices }];
    
    $scope.addNewRuleAlgorithm = function() {
    	if($scope.temp.policy.ruleAlgorithmschoices != null){
    		var newItemNo = $scope.temp.policy.ruleAlgorithmschoices.length+1;
    	}else{
    		var newItemNo = 1;
    	}
    	if(newItemNo > 1){
    		var value = newItemNo-1;
    		$scope.attributeDictionaryDatas.push('A'+value);
    	}
    	$scope.temp.policy.ruleAlgorithmschoices.push({'id':'A'+newItemNo});
    };
    
    $scope.removeRuleAlgorithm = function() {
      var lastItem = $scope.temp.policy.ruleAlgorithmschoices.length-1;
      $scope.temp.policy.ruleAlgorithmschoices.splice(lastItem);
    };
    
    $scope.providerListener = function(ruleProvider) {
    	if (ruleProvider!="Custom"){
    		$scope.temp.policy.ruleAlgorithmschoices  = [];
    		$scope.temp.policy.settings = [];
    		$scope.temp.policy.attributes = [];
    	}
    };
    
    $scope.importButton = true;
    var fd;
	$scope.uploadBLFile = function(files) {
		fd = new FormData();
		fd.append("file", files[0]);
		var fileExtension = files[0].name.split(".")[1];
		if(fileExtension == "xls"){
			$scope.importButton = false;
			$scope.$apply();
		}else{
			Notification.error("Upload the BlackList file which extends with .xls format.");
		}
	};
	
	function arrayUnique(array) {
	    var a = array.concat();
	    for(var i=0; i<a.length; ++i) {
	        for(var j=i+1; j<a.length; ++j) {
	            if(a[i] === a[j])
	                a.splice(j--, 1);
	        }
	    }
	    return a;
	}
	
	$scope.submitUpload = function(){
		$http.post("policycreation/importBlackListForDecisionPolicy", fd,  {
			withCredentials: false,
			headers: {'Content-Type': undefined},
			transformRequest: angular.identity
		}).success(function(data){
			$scope.data = JSON.parse(data.data);
			$scope.temp.policy.blackListEntries = $scope.data.blackListEntries;
			if($scope.temp.policy.blackListEntries[0] !== "error"){
				$scope.blackListEntries = arrayUnique($scope.temp.policy.blackListEntries.concat($scope.temp.policy.yamlparams.blackList));
				$scope.temp.policy.appendBlackListEntries = $scope.data.appendBlackListEntries;
				$scope.blackListEntries = $scope.blackListEntries.filter(function (obj){
					return !$scope.temp.policy.appendBlackListEntries.includes(obj);
				});
				if($scope.blackListEntries.length == 0){
					$scope.validateButton = true;
					Notification.error("Black Lists are empty. Minimum one entry required.");
				}else{
					$scope.temp.policy.blackListEntries = $scope.blackListEntries;
					Notification.success("Blacklist File Uploaded Successfully.");
					$scope.validateButton = false;
					$scope.importButton = true;
				}
			}else{
				 Notification.error("Blacklist File Upload Failed." + $scope.temp.policy.blackListEntries[1]);
			}
		}).error(function(data){
			 Notification.error("Blacklist File Upload Failed.");
		});
	};
	
	$scope.initializeBlackList = function(){
		if($scope.temp.policy.blackListEntryType === "Use File Upload"){
			 $scope.validateButton = true;	
		} else {
			 $scope.validateButton = false;	
		}
		$("#importFile").val('');
	};
	
	$scope.exportBlackListEntries = function(){
		var uuu = "policycreation/exportDecisionBlackListEntries";
		var postData={policyData: $scope.temp.policy, date : $scope.temp.model.modifiedDate, version : $scope.temp.model.version};
		$.ajax({
			type : 'POST',
			url : uuu,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(postData),
			success : function(data){
				$scope.$apply(function(){
					$scope.data=data.data;
					var url = '../' + $scope.data;
					window.location = url;
					Notification.success("BlackList Entries Exported Successfully.");
				});
				console.log($scope.data);
			},
			error : function(data){      	
				Notification.error("Error Occured while Exporting BlackList Entries.");
			}
		});
	};	
}]);