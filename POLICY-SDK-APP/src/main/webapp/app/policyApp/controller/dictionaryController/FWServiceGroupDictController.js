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
app.controller('editFWServiceGroupController' ,  function ($scope, $modalInstance, message, UserInfoServiceDS2, PolicyAppService, Notification){
    if(message.serviceGroupDictionaryData==null){
    	$scope.slchoices = [];
        $scope.label='Add Service Group'
    }else{
        $scope.label='Edit Service Group'
        $scope.disableCd=true;
        $scope.slchoices = [];
        var headers = message.serviceGroupDictionaryData.serviceList;
        var splitEqual = ',';
        if(headers != null){
        	if (headers.indexOf(splitEqual) >= 0) {
        		var splitValue = headers.split(splitEqual);
        		for(i = 0; i < splitValue.length; i++){
        			var key  = splitValue[i];
        			$scope.slchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
        		var key  = headers;
    			$scope.slchoices.push({'id':'choice'+1, 'option': key});
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

	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editServiceGroup = message.serviceGroupDictionaryData;

    $scope.saveFWServiceGroup = function(serviceGroupDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(serviceGroupDictionaryData.groupName)) {
    		Notification.error("Enter Valid Service Group Name without spaces or special characters");
    	}else{
    		var finalData = extend(serviceGroupDictionaryData, $scope.attributeDatas[0]);
    		var uuu = "saveDictionary/fw_dictionary/save_serviceGroup";
    		var postData={serviceGroupDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.serviceGroupDictionaryDatas=data.serviceGroupDictionaryDatas;});
    				if($scope.serviceGroupDictionaryDatas == "Duplicate"){
    					Notification.error("FW Service Group Dictionary exists with Same Group Name.")
    				}else{      
    					console.log($scope.serviceGroupDictionaryDatas);
    					$modalInstance.close({serviceGroupDictionaryDatas:$scope.serviceGroupDictionaryDatas});
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
});