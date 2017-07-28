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
app.controller('editFWParentListController' ,  function ($scope, $modalInstance, message, UserInfoServiceDS2, PolicyAppService, Notification){
    if(message.fwDictListDictionaryData==null){
    	$scope.slchoices = [];
    	$scope.alchoices = [];
        $scope.label='Add Parent List'
    }else{
        $scope.label='Edit Parent List'
        $scope.disableCd=true;
        $scope.slchoices = [];
        $scope.alchoices = [];
        var slList = message.fwDictListDictionaryData.serviceList;
        var alList = message.fwDictListDictionaryData.addressList;    
        var splitEqual = ',';
        if(slList != null){
        	if (slList.indexOf(splitEqual) >= 0) {
        		var splitValue = slList.split(splitEqual);
        		for(i = 0; i < splitValue.length; i++){
        			var key  = splitValue[i];
        			$scope.slchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
        		var key  = slList;
    			$scope.slchoices.push({'id':'choice'+1, 'option': key});
        	}
        }
        if(alList != null){
        	if (alList.indexOf(splitEqual) >= 0) {
        		var splitALValue = alList.split(splitEqual);
        		for(i = 0; i < splitALValue.length; i++){
        			var key  = splitALValue[i];
        			$scope.alchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
        		var key  = alList;
    			$scope.alchoices.push({'id':'choice'+1, 'option': key});
        	}
        }
    }

    PolicyAppService.getData('getDictionary/get_ServiceListDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.serviceListDictionaryDatas = JSON.parse($scope.data.serviceListDictionaryDatas);
    	console.log($scope.serviceListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_AddressGroupDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.addressGroupDictionaryDatas = JSON.parse($scope.data.addressGroupDictionaryDatas);
    	console.log($scope.addressGroupDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editParentList = message.fwDictListDictionaryData;

    $scope.saveFWParentList = function(fwDictListDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(fwDictListDictionaryData.parentItemName)) {
    		Notification.error("Enter Valid Parent List Name without spaces or special characters");
    	}else{
    		var addSLData = extend(fwDictListDictionaryData, $scope.attributeDatas[0]);
    		var finalData = extend(addSLData, $scope.attributeALDatas[0]);
    		var uuu = "saveDictionary/fw_dictionary/save_FWDictionaryList";
    		var postData={fwDictListDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.fwDictListDictionaryDatas=data.fwDictListDictionaryDatas;});
    				if($scope.fwDictListDictionaryDatas == "Duplicate"){
    					Notification.error("FW DictionaryList Dictionary exists with Same  Name.")
    				}else{      
    					console.log($scope.fwDictListDictionaryDatas);
    					$modalInstance.close({fwDictListDictionaryDatas:$scope.fwDictListDictionaryDatas});
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
    
 
    $scope.attributeDatas = [{"attributes" : $scope.slchoices}];
    $scope.addServiceGroupNewChoice = function() {
        var newItemNo = $scope.slchoices.length+1;
        $scope.slchoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeServiceGroupChoice = function() {
        var lastItem = $scope.slchoices.length-1;
        $scope.slchoices.splice(lastItem);
    };
    
    $scope.attributeALDatas = [{"alAttributes" : $scope.alchoices}];
    $scope.addAddressGroupNewChoice = function() {
        var newItemNo = $scope.alchoices.length+1;
        $scope.alchoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeAddressGroupChoice = function() {
        var lastItem = $scope.alchoices.length-1;
        $scope.alchoices.splice(lastItem);
    };
});