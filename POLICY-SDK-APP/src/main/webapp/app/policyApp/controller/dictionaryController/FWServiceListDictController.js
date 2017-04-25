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
app.controller('editFWServiceListController' ,  function ($scope, $modalInstance, message, PolicyAppService, UserInfoServiceDS2, Notification){
	$scope.protocolListDictionaryDatas =[];

	$scope.tpchoices = [];
	$scope.apchoices = [];
	if(message.serviceListDictionaryData==null){
		$scope.label='Add Service List'    
	}else{
		$scope.label='Edit Service List'
			$scope.disableCd=true;
		var tcpheaders = message.serviceListDictionaryData.serviceTransProtocol;
		var splitEqual = ',';
		if(tcpheaders != null){
			if (tcpheaders.indexOf(splitEqual) >= 0) {
				var splitValue = tcpheaders.split(splitEqual);
				for(i = 0; i < splitValue.length; i++){
					var key  = splitValue[i];
					$scope.tpchoices.push({'id':'choice'+i+1, 'option': key});
				}
			}else{
				var key  = tcpheaders;
				$scope.tpchoices.push({'id':'choice'+1, 'option': key});
			}
		}
		var appheaders = message.serviceListDictionaryData.serviceAppProtocol;
		var splitEqual1 = ',';
		if(appheaders != null){
			if (appheaders.indexOf(splitEqual1) >= 0) {
				var splitValue1 = appheaders.split(splitEqual1);
				for(i = 0; i < splitValue1.length; i++){
					var key1  = splitValue1[i];
					$scope.apchoices.push({'id':'choice'+i+1, 'option': key1});
				}
			}else{
				var key1  = appheaders;
				$scope.apchoices.push({'id':'choice'+1, 'option': key1});
			}
		}
	}

		
	PolicyAppService.getData('getDictionary/get_ProtocolListDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.protocolListDictionaryDatas = JSON.parse($scope.data.protocolListDictionaryDatas);
		console.log($scope.protocolListDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
	$scope.editServiceList = message.serviceListDictionaryData;

	$scope.saveFWServiceList = function(serviceListDictionaryData) {
		var regex = new RegExp("^[a-zA-Z0-9_]*$");
		if(!regex.test(serviceListDictionaryData.serviceName)) {
			Notification.error("Enter Valid ServiceList Name without spaces or special characters");
		}else{
			var addtcpData = extend(serviceListDictionaryData, $scope.attributeTCPDatas[0]);
			var finalData = extend(addtcpData, $scope.attributeAPPDatas[0]);
			var uuu = "saveDictionary/fw_dictionary/save_serviceList";
			var postData={serviceListDictionaryData: finalData, userid: userid};
			$.ajax({
				type : 'POST',
				url : uuu,
				dataType: 'json',
				contentType: 'application/json',
				data: JSON.stringify(postData),
				success : function(data){
					$scope.$apply(function(){
						$scope.serviceListDictionaryDatas=data.serviceListDictionaryDatas;});
					if($scope.serviceListDictionaryDatas == "Duplicate"){
						Notification.error("FW ServiceList Dictionary exists with Same ServiceList Name.")
					}else{      
						console.log($scope.serviceListDictionaryDatas);
						$modalInstance.close({serviceListDictionaryDatas:$scope.serviceListDictionaryDatas});
					}
				},
				error : function(data){
					alert("Error while saving.");
				}
			});
		}
	};

	$scope.close = function() {
		$modalInstance.close();
	};

	function extend(obj, src) {
		for (var key in src) {
			if (src.hasOwnProperty(key)) obj[key] = src[key];
		}
		return obj;
	}

	$scope.attributeTCPDatas = [{"transportProtocols" : $scope.tpchoices}];
	$scope.addTPNewChoice = function() {
		var newItemNo = $scope.tpchoices.length+1;
		$scope.tpchoices.push({'id':'choice'+newItemNo});
	};
	$scope.removeTPChoice = function() {
		var lastItem = $scope.tpchoices.length-1;
		$scope.tpchoices.splice(lastItem);
	};


	$scope.attributeAPPDatas = [{"appProtocols" : $scope.apchoices}];
	$scope.addAPNewChoice = function() {
		var newItemNo = $scope.apchoices.length+1;
		$scope.apchoices.push({'id':'choice'+newItemNo});
	};
	$scope.removeAPChoice = function() {
		var lastItem = $scope.apchoices.length-1;
		$scope.apchoices.splice(lastItem);
	};
});