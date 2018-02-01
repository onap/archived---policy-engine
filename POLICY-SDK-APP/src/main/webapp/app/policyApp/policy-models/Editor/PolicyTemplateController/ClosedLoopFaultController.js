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
angular.module("abs").controller('clFaultController', ['$scope', '$window', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', function($scope, $window, PolicyAppService, PolicyNavigator, modalService, $modal, Notification){
	$("#dialog").hide();

	$scope.policyNavigator;
	$scope.savebutton = true;
	$scope.refreshCheck = false;
    
	if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    	$scope.temp.policy = {
    		policyType : "Config",
    		configPolicyType : "ClosedLoop_Fault"
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
    
    $('#ttlDate').datepicker({
    	dateFormat: 'dd/mm/yy',
    	changeMonth: true,
    	changeYear: true,
    	onSelect: function(date) {
    		angular.element($('#ttlDate')).triggerHandler('input');
    	}
    });
	
	if($scope.temp.policy.triggerTrapSignatures == undefined){
		$scope.temp.policy.triggerTrapSignatures = [];
		$scope.temp.policy.triggerfaultSignatures = [];
	}
	var trapCollection = [];
	var faultCollection = [];
	if($scope.varbindDictionaryDatas == undefined){
		$scope.varbindDictionaryDatas = [];
	}
	
	$scope.init = function(data){
		if(data != undefined && $scope.temp.policy.triggerTrapSignatures.length == 0){
			$scope.jsonData = data; 
			if($scope.jsonData.triggerSignaturesUsedForUI != null){
				if($scope.jsonData.triggerSignaturesUsedForUI.signatures != null){
					$scope.temp.policy.triggerTrapSignatures = {Trap1 : [], Trap2 : [], Trap3 : [], Trap4 : [], Trap5 : [], Trap6 : []};
					var splitTraps = $scope.jsonData.triggerSignaturesUsedForUI.signatures.split("#!?!"); 
					if(splitTraps.length > 1){
						$scope.triggerdisabled = false;
						var indexId = "Trap1";
						trapCollection.push(indexId);
						$scope.temp.policy.triggerTrapSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+1});
						var splitTrap1 = splitTraps[0];
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerTrapSignatures["Trap1"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 2){
						var indexId = "Trap2";
						trapCollection.push(indexId);
						$scope.temp.policy.triggerTrapSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+2});
						var splitTrap1 = splitTraps[1]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] == ''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerTrapSignatures["Trap2"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 3){
						var indexId = "Trap3";
						trapCollection.push(indexId);
						$scope.temp.policy.triggerTrapSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+3});
						var splitTrap1 = splitTraps[2]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerTrapSignatures["Trap3"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 4){
						var indexId = "Trap4";
						trapCollection.push(indexId);
						$scope.temp.policy.triggerTrapSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+4});
						var splitTrap1 = splitTraps[3]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerTrapSignatures["Trap4"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 5){
						var indexId = "Trap5";
						trapCollection.push(indexId);
						$scope.temp.policy.triggerTrapSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+5});
						var splitTrap1 = splitTraps[4]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerTrapSignatures["Trap5"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						} 
					}
					if(splitTraps.length > 6){
						var indexId = "Trap6";
						trapCollection.push(indexId);
						$scope.temp.policy.triggerTrapSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+6});
						var splitTrap1 = splitTraps[5]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerTrapSignatures["Trap6"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if($scope.jsonData.triggerSignaturesUsedForUI.connectSignatures != null){
						var splitConnectTraps = $scope.jsonData.triggerSignaturesUsedForUI.connectSignatures.split("#!?!"); 
						for(i=0; i < splitConnectTraps.length; i++){
							if(splitConnectTraps[i] != ""){
								var newConnectTrapItemNo = i+1;
								var connects = splitConnectTraps[i].split("@!");
								if(connects[0] == 'NOT' || connects[0] ==''){
									var notBox = connects[0];
									var connectTrap1 = connects[1];
									var trapCount1 = connects[2];
									var operatorBox = connects[3];
									var connectTrap2 = connects[4];
									var trapCount2 = connects[5];
								}else{
									var notBox = '';
									var connectTrap1 = connects[0];
									var trapCount1 = connects[1];
									var operatorBox = connects[2];
									var connectTrap2 = connects[3];
									var trapCount2 = connects[4]; 
								}
								$scope.temp.policy.connecttriggerSignatures.push({'id':'C'+newConnectTrapItemNo,'notBox' : notBox , 'connectTrap1': connectTrap1,'trapCount1' : trapCount1, 
									'operatorBox': operatorBox, 'connectTrap2': connectTrap2,'trapCount2' : trapCount2}); 
							}
						}				
					}
				}
			}
			if($scope.jsonData.verificationSignaturesUsedForUI != null){
				if($scope.jsonData.verificationSignaturesUsedForUI.signatures != null){
					$scope.temp.policy.triggerfaultSignatures = {Fault1 : [], Fault2 : [],  Fault3 : [],  Fault4 : [],  Fault5 : [],  Fault6 : []};
					var splitTraps = $scope.jsonData.verificationSignaturesUsedForUI.signatures.split("#!?!"); 
					if(splitTraps.length > 1){
						$scope.verificationdisabled = false;
						var indexId = "Fault1";
						faultCollection.push(indexId);
						$scope.temp.policy.triggerfaultSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+1});
						var splitTrap1 = splitTraps[0];
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerfaultSignatures["Fault1"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 2){
						var indexId = "Fault2";
						faultCollection.push(indexId);
						$scope.temp.policy.triggerfaultSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+2});
						var splitTrap1 = splitTraps[1]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] == ''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerfaultSignatures["Fault2"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 3){
						var indexId = "Fault3";
						faultCollection.push(indexId);
						$scope.temp.policy.triggerfaultSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+3});
						var splitTrap1 = splitTraps[2]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerfaultSignatures["Fault3"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 4){
						var indexId = "Fault4";
						faultCollection.push(indexId);
						$scope.temp.policy.triggerfaultSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+4});
						var splitTrap1 = splitTraps[3]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerfaultSignatures["Fault4"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}
					if(splitTraps.length > 5){
						var indexId = "Fault5";
						faultCollection.push(indexId);
						$scope.temp.policy.triggerfaultSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+5});
						var splitTrap1 = splitTraps[4]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerfaultSignatures["Fault5"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						} 
					}
					if(splitTraps.length > 6){
						var indexId = "Fault6";
						faultCollection.push(indexId);
						$scope.temp.policy.triggerfaultSignatures[indexId.replace(/['"]+/g, '')] = [];
						$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+6});
						var splitTrap1 = splitTraps[5]; 
						var splitEachTrap = splitTrap1.split("#!");
						for(i = 0 ; i < splitEachTrap.length; i++){
							var splitEachRow = splitEachTrap[i].split("@!");
							var count = i +1;
							if(splitEachRow[0] == 'NOT' || splitEachRow[0] ==''){
								var notBox = splitEachRow[0];
								var trigger1 = splitEachRow[1];
								var operatorBox = splitEachRow[2];
								var trigger2 = splitEachRow[3];
							}else{
								var notBox = '';
								var trigger1 = splitEachRow[0];
								var operatorBox = splitEachRow[1];
								var trigger2 = splitEachRow[2]; 
							}
							$scope.varbindDictionaryDatas.push('A'+count);
							$scope.temp.policy.triggerfaultSignatures["Fault6"].push({'id':'A'+count, 'notBox' : notBox , 'trigger1': trigger1 , 'operatorBox' : operatorBox, 'trigger2': trigger2}); 		 
						}
					}

					if($scope.jsonData.verificationSignaturesUsedForUI.connectSignatures != null){
						var splitConnectTraps = $scope.jsonData.verificationSignaturesUsedForUI.connectSignatures.split("#!?!"); 
						for(i=0; i < splitConnectTraps.length; i++){
							if(splitConnectTraps[i] != ""){
								var newConnectTrapItemNo = i+1;
								var connects = splitConnectTraps[i].split("@!");
								if(connects[0] == 'NOT' || connects[0] ==''){
									var notBox = connects[0];
									var connectTrap1 = connects[1];
									var trapCount1 = connects[2];
									var operatorBox = connects[3];
									var connectTrap2 = connects[4];
									var trapCount2 = connects[5];
								}else{
									var notBox = '';
									var connectTrap1 = connects[0];
									var trapCount1 = connects[1];
									var operatorBox = connects[2];
									var connectTrap2 = connects[3];
									var trapCount2 = connects[4]; 
								}
								$scope.temp.policy.connectVerificationSignatures.push({'id':'C'+newConnectTrapItemNo,'notBox' : notBox , 'connectTrap1': connectTrap1,'trapCount1' : trapCount1, 
									'operatorBox': operatorBox, 'connectTrap2': connectTrap2,'trapCount2' : trapCount2}); 
							}
						}				
					}
				}
			}
		}

	};

	if($scope.temp.policy.readOnly){
		$scope.triggerdisabled = true;
		$scope.verificationdisabled = true;
	}else{
		$scope.triggerdisabled = false;
		$scope.verificationdisabled = false;
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

	PolicyAppService.getData('getDictionary/get_PEPOptionsDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.pepOptionsDictionaryDatas = JSON.parse($scope.data.pepOptionsDictionaryDatas);
		console.log($scope.pepOptionsDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_PEPOptionsData').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.pepOptionsDictionaryDataEntity = JSON.parse($scope.data.pepOptionsDictionaryDatas);
		console.log($scope.pepOptionsDictionaryDataEntity);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_VarbindDictionaryDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.varbindDictionaryDatas = JSON.parse($scope.data.varbindDictionaryDatas);
		console.log($scope.varbindDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_VNFTypeDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.vnfTypeDictionaryDatas = JSON.parse($scope.data.vnfTypeDictionaryDatas);
		console.log($scope.vnfTypeDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_VSCLActionDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.vsclActionDictionaryDatas = JSON.parse($scope.data.vsclActionDictionaryDatas);
		console.log($scope.vsclActionDictionaryDatas);
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
    
	$scope.pepActionDictionaryDatas = [];

	$scope.getPepActionValues = function(pepOptionValue){
		for (var i = 0; i < $scope.pepOptionsDictionaryDataEntity.length; ++i) {
    	    var obj = $scope.pepOptionsDictionaryDataEntity[i];
    	    if (obj.pepName == pepOptionValue){
    	    	var splitAlarm = obj.actions.split(':#@');
    	    	for (var j = 0; j < splitAlarm.length; ++j) {
    	    		$scope.pepActionDictionaryDatas.push(splitAlarm[j].split('=#@')[0]);
    	    	}
    	    }
    	}
	};

	function trapData(){
		var data = {};
		if($scope.temp.policy.triggerTrapSignatures.length == 1 || $scope.temp.policy.triggerTrapSignatures["Trap1"] != null){
			 data = {trap1 : $scope.temp.policy.triggerTrapSignatures.Trap1 }
		}
		if($scope.temp.policy.triggerTrapSignatures.length == 2 || $scope.temp.policy.triggerTrapSignatures["Trap2"] != null){
			 data = {trap1 : $scope.temp.policy.triggerTrapSignatures.Trap1,  trap2 : $scope.temp.policy.triggerTrapSignatures.Trap2}
		}
		if($scope.temp.policy.triggerTrapSignatures.length == 3 || $scope.temp.policy.triggerTrapSignatures["Trap3"] != null){
			 data = {trap1 : $scope.temp.policy.triggerTrapSignatures.Trap1,  trap2 : $scope.temp.policy.triggerTrapSignatures.Trap2,
					trap3 : $scope.temp.policy.triggerTrapSignatures.Trap3}
		}
		if($scope.temp.policy.triggerTrapSignatures.length == 4 || $scope.temp.policy.triggerTrapSignatures["Trap4"] != null){
			 data = {trap1 : $scope.temp.policy.triggerTrapSignatures.Trap1,  trap2 : $scope.temp.policy.triggerTrapSignatures.Trap2,
					trap3 : $scope.temp.policy.triggerTrapSignatures.Trap3,   trap4 : $scope.temp.policy.triggerTrapSignatures.Trap4}
		}
		if($scope.temp.policy.triggerTrapSignatures.length == 5 || $scope.temp.policy.triggerTrapSignatures["Trap5"] != null){
			 data = {trap1 : $scope.temp.policy.triggerTrapSignatures.Trap1,  trap2 : $scope.temp.policy.triggerTrapSignatures.Trap2,
					trap3 : $scope.temp.policy.triggerTrapSignatures.Trap3,   trap4 : $scope.temp.policy.triggerTrapSignatures.Trap4,
					trap5 : $scope.temp.policy.triggerTrapSignatures.Trap5}
		}
		if($scope.temp.policy.triggerTrapSignatures.length == 6 || $scope.temp.policy.triggerTrapSignatures["Trap6"] != null){
			 data = {trap1 : $scope.temp.policy.triggerTrapSignatures.Trap1,  trap2 : $scope.temp.policy.triggerTrapSignatures.Trap2,
					trap3 : $scope.temp.policy.triggerTrapSignatures.Trap3,   trap4 : $scope.temp.policy.triggerTrapSignatures.Trap4,
					trap5 : $scope.temp.policy.triggerTrapSignatures.Trap5, trap6 : $scope.temp.policy.triggerTrapSignatures.Trap6}
		}	
		return data;
	}
	
	function faultDatas(){
		var faultData = {};
		if($scope.temp.policy.triggerfaultSignatures.length == 1 || $scope.temp.policy.triggerfaultSignatures["Fault1"] != null){
			 faultData = {trap1 : $scope.temp.policy.triggerfaultSignatures.Fault1 }
		}
		if($scope.temp.policy.triggerfaultSignatures.length == 2 || $scope.temp.policy.triggerfaultSignatures["Fault2"] != null){
			 faultData = {trap1 : $scope.temp.policy.triggerfaultSignatures.Fault1,  trap2 : $scope.temp.policy.triggerfaultSignatures.Fault2}
		}
		if($scope.temp.policy.triggerfaultSignatures.length == 3 || $scope.temp.policy.triggerfaultSignatures["Fault3"] != null){
			 faultData = {trap1 : $scope.temp.policy.triggerfaultSignatures.Fault1,  trap2 : $scope.temp.policy.triggerfaultSignatures.Fault2,
					trap3 : $scope.temp.policy.triggerfaultSignatures.Fault3}
		}
		if($scope.temp.policy.triggerTrapSignatures.length == 4 || $scope.temp.policy.triggerfaultSignatures["Fault4"] != null){
			 faultData = {trap1 : $scope.temp.policy.triggerfaultSignatures.Fault1,  trap2 : $scope.temp.policy.triggerfaultSignatures.Fault2,
					trap3 : $scope.temp.policy.triggerfaultSignatures.Fault3,   trap4 : $scope.temp.policy.triggerfaultSignatures.Fault4}
		}
		if($scope.temp.policy.triggerfaultSignatures.length == 5 || $scope.temp.policy.triggerfaultSignatures["Fault5"] != null){
			 faultData = {trap1 : $scope.temp.policy.triggerfaultSignatures.Fault1,  trap2 : $scope.temp.policy.triggerfaultSignatures.Fault2,
					trap3 : $scope.temp.policy.triggerfaultSignatures.Fault3,   trap4 : $scope.temp.policy.triggerfaultSignatures.Fault4,
					trap5 : $scope.temp.policy.triggerfaultSignatures.Fault5}
		}
		if($scope.temp.policy.triggerfaultSignatures.length == 6 || $scope.temp.policy.triggerfaultSignatures["Fault6"] != null){
			 faultData = {trap1 : $scope.temp.policy.triggerfaultSignatures.Fault1,  trap2 : $scope.temp.policy.triggerfaultSignatures.Fault2,
					trap3 : $scope.temp.policy.triggerfaultSignatures.Fault3,   trap4 : $scope.temp.policy.triggerfaultSignatures.Fault4,
					trap5 : $scope.temp.policy.triggerfaultSignatures.Fault5, trap6 : $scope.temp.policy.triggerfaultSignatures.Fault6}
		}
		return faultData;
	}
	
	$scope.saveFaultPolicy = function(policy){
		if(policy.itemContent != undefined){
    		$scope.refreshCheck = true; 
        	$scope.policyNavigator = policy.itemContent;
        	policy.itemContent = "";
    	}
		$scope.savebutton = false;
		var data = trapData();
		var faultData = faultDatas();
		var uuu = "policycreation/save_policy";
		var postData={policyData: policy,
				trapData : data,
				faultData : faultData
		};
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
						$scope.triggerdisabled = true;
						$scope.verificationdisabled = true;
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
		var data = trapData();
		var faultData = faultDatas();
		var postData={policyData: policy, trapData : data, faultData : faultData};
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
	if($scope.connectTriggerTrapsList == undefined){
		$scope.connectTriggerTrapsList = [];
	}
	if($scope.temp.policy.traptriggerSignatures == undefined){
		$scope.temp.policy.traptriggerSignatures = [];
	}
	
	$scope.ItemNo = 0;
	$scope.TriggerSignatureDatas = [{"triggerSignatures" : $scope.temp.policy.traptriggerSignatures}];
	$scope.addTriggerButton = function() {
		$scope.triggerdisabled = false;
		var newItemNo = $scope.temp.policy.traptriggerSignatures.length+1;
		$scope.ItemNo = newItemNo;
		$scope.temp.policy.traptriggerSignatures.push({'id':'Trap'+newItemNo});
		$scope.connectTriggerTrapsList.push('Trap'+newItemNo);
	};
	$scope.removeTriggerButton = function() {
		var lastItem = $scope.temp.policy.traptriggerSignatures.length-1;
		$scope.temp.policy.traptriggerSignatures.splice(lastItem);
		$scope.connectTriggerTrapsList.splice('Trap'+lastItem);
	};

	
	$scope.trapItemNo = 0;
	$scope.TrapTriggerSignatureDatas = [{"triggermainSignatures" : $scope.temp.policy.triggerTrapSignatures}];
	$scope.addTrapTriggerButton = function(indexId) {
		if(trapCollection.indexOf(indexId) !== -1){

		}else{
			$scope.temp.policy.triggerTrapSignatures[indexId] = [];
			trapCollection.push(indexId);
		}	
		var newTrapItemNo = $scope.temp.policy.triggerTrapSignatures[indexId].length+1;
		$scope.trapItemNo = newTrapItemNo;
		$scope.temp.policy.triggerTrapSignatures.push($scope.temp.policy.triggerTrapSignatures[indexId].push({'id':'A'+newTrapItemNo}));
		if(newTrapItemNo > 1){
			var count = newTrapItemNo-1;
			$scope.varbindDictionaryDatas.push('A'+count);
		}
	};
	$scope.removeTrapTriggerButton = function(indexId) {
		var lastTrapItem = $scope.temp.policy.triggerTrapSignatures[indexId].length-1;
		var checkLastTrapItem = lastTrapItem;
		if(checkLastTrapItem == 0){
			trapCollection.splice(indexId);
		}
		$scope.temp.policy.triggerTrapSignatures[indexId].splice(lastTrapItem);
	};

	if($scope.temp.policy.connecttriggerSignatures == undefined){
		$scope.temp.policy.connecttriggerSignatures = [];
	}
	
	$scope.connecttrapItemNo = 0;
	$scope.TrapConnectTriggerSignatureDatas = [{"connecttriggerSignatures" : $scope.temp.policy.connecttriggerSignatures}];
	$scope.addTriggerConnectButton = function() {
		var newConnectTrapItemNo = $scope.temp.policy.connecttriggerSignatures.length+1;
		$scope.connecttrapItemNo = newConnectTrapItemNo;
		$scope.temp.policy.connecttriggerSignatures.push({'id':'C'+newConnectTrapItemNo});
		if(newConnectTrapItemNo >1){
			var count = newConnectTrapItemNo-1;
			$scope.connectTriggerTrapsList.push('C'+count);
		}      
	};
	$scope.removeTriggerConnectButton = function() {
		var lastConnectTrapItem = $scope.temp.policy.connecttriggerSignatures.length-1;
		$scope.temp.policy.connecttriggerSignatures.splice(lastConnectTrapItem);
		if(lastConnectTrapItem  < 1){
			var count = lastConnectTrapItem-1;
			$scope.connectTriggerTrapsList.splice('C'+count);
		}
	};
	if($scope.connectTriggerFaultsList == undefined){
		$scope.connectTriggerFaultsList = [];
	}
	if($scope.temp.policy.faulttriggerSignatures == undefined){
		$scope.temp.policy.faulttriggerSignatures = [];
	}
	
	$scope.FaultItemNo = 0;
	$scope.FaultSignatureDatas = [{"verificationmainSignatures" : $scope.temp.policy.faulttriggerSignatures}];
	$scope.addVerFaultButton = function() {
		var newFaultItemNo = $scope.temp.policy.faulttriggerSignatures.length+1;
		$scope.FaultItemNo = newFaultItemNo;
		$scope.temp.policy.faulttriggerSignatures.push({'id':'Fault'+newFaultItemNo});
		$scope.connectTriggerFaultsList.push('Fault'+newFaultItemNo);
	};
	$scope.removeVerFaultButton = function() {
		var lastFaultItem = $scope.temp.policy.faulttriggerSignatures.length-1;
		$scope.temp.policy.faulttriggerSignatures.splice(lastFaultItem);
		$scope.connectTriggerFaultsList.splice('Fault'+lastFaultItem);
	};
	if($scope.temp.policy.triggerfaultSignatures == undefined){
		$scope.temp.policy.triggerfaultSignatures = [];
	}
	
	$scope.faultItemNo1 = 0;
	$scope.FaultTriggerSignatureDatas = [{"verificationSignatures" : $scope.temp.policy.triggerfaultSignatures}];
	$scope.addVerTriggerButton = function(indexId) {
		$scope.verificationdisabled = false;
		if(faultCollection.indexOf(indexId) !== -1){

		}else{
			$scope.temp.policy.triggerfaultSignatures[indexId] = [];
			faultCollection.push(indexId);
		}	
		var newFaultItemNo1 = $scope.temp.policy.triggerfaultSignatures[indexId].length+1;
		$scope.faultItemNo1 = newFaultItemNo1; 
		$scope.temp.policy.triggerfaultSignatures.push($scope.temp.policy.triggerfaultSignatures[indexId].push({'id':'A'+newFaultItemNo1}));
		if(newFaultItemNo1 > 1){
			var count = newFaultItemNo1-1;
			$scope.varbindDictionaryDatas.push('A'+count);
		}
	};
	$scope.removeVerTriggerButton = function(indexId) {
		var lastFaultItem1 = $scope.temp.policy.triggerfaultSignatures[indexId].length-1;
		var checkLastFaultItem = lastFaultItem1;
		if(checkLastFaultItem == 0){
			faultCollection.splice(indexId);
		}
		$scope.temp.policy.triggerfaultSignatures[indexId].splice(lastFaultItem1);
	};

	if($scope.temp.policy.connectVerificationSignatures == undefined){
		$scope.temp.policy.connectVerificationSignatures = [];
	}
	
	$scope.connectFaultItemNo = 0;
	$scope.FaultConnectTriggerSignatureDatas = [{"connectVerificationSignatures" : $scope.temp.policy.connectVerificationSignatures}];
	$scope.addFaultConnectButton = function() {
		var newConnectFaultItemNo = $scope.temp.policy.connectVerificationSignatures.length+1;
		$scope.connectFaultItemNo = newConnectFaultItemNo;
		$scope.temp.policy.connectVerificationSignatures.push({'id':'C'+newConnectFaultItemNo});
		if(newConnectFaultItemNo >1){
			var count = newConnectFaultItemNo-1;
			$scope.connectTriggerFaultsList.push('C'+count);
		}  
	};
	$scope.removeFaultConnectButton = function() {
		var lastConnectFaultItem = $scope.temp.policy.connectVerificationSignatures.length-1;
		$scope.temp.policy.connectVerificationSignatures.splice(lastConnectFaultItem);
		if(lastConnectFaultItem  < 1){
			var count = lastConnectFaultItem-1;
			$scope.connectTriggerFaultsList.splice('C'+count);
		}
	};


}]);