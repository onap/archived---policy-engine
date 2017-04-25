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
app.controller('editModelAttributeController' ,  function ($scope, $modalInstance, message, PolicyAppService, UserInfoServiceDS2, Notification){
    if(message.modelAttributeDictionaryData==null)
        $scope.label='Add New Dictionary Entry'
    else{
        $scope.label='Edit Dictionary Entry'
        $scope.disableCd=true;
    }
		
    PolicyAppService.getData('getDictionary/get_MicroServiceAttributeData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.microServiceAttributeDictionaryDatas = JSON.parse($scope.data.microServiceAttributeDictionaryDatas);
    	console.log($scope.microServiceAttributeDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_MicroServiceModelsDataServiceVersion').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.microServiceModelsDictionaryDatas = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
    	console.log($scope.microServiceModelsDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editModelAttribute = message.modelAttributeDictionaryData;
    $scope.editModelAttribute1 = {microservice: []};
    if($scope.edit){
    	if(message.modelAttributeDictionaryData.groupList != null){
    		var splitValue = message.modelAttributeDictionaryData.groupList.split(",");
    		console.log(splitValue);
    	}	
    }
    $scope.saveModelAttribute = function(modelAttributeDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(modelAttributeDictionaryData.name)) {
    		Notification.error("Enter Valid Micro Service Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/ms_dictionary/save_modelAttribute";
    		var postData={modelAttributeDictionaryData: modelAttributeDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.microServiceAttributeDictionaryDatas=data.microServiceAttributeDictionaryDatas;});
    				if($scope.microServiceAttributeDictionaryDatas == "Duplicate"){
    					Notification.error("Model Attribute Dictionary exists with Same Attribute Name.")
    				}else{      
    					console.log($scope.microServiceAttributeDictionaryDatas);
    					$modalInstance.close({microServiceAttributeDictionaryDatas:$scope.microServiceAttributeDictionaryDatas});
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
});