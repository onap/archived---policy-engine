/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018, 2019 AT&T Intellectual Property. All rights reserved.
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
app.controller('editMSHeaderDefaultValuesController' ,  
    function ($scope, $modalInstance, message, PolicyAppService, UserInfoServiceDS2, Notification){
	   if(message.modelAttributeDictionaryData==null)
	        $scope.label='Set Header Default Values'
	    else{
	        $scope.label='Edit Header Default Values'
	        $scope.disableCd=true;
	    }

	    PolicyAppService.getData('getDictionary/get_MicroServiceHeaderDefaultsData').then(function (data) {
	    	var j = data;
	    	$scope.data = JSON.parse(j.data);
	    	$scope.microServiceHeaderDefaultDatas = JSON.parse($scope.data.microServiceHeaderDefaultDatas);
	    });

	    PolicyAppService.getData('getDictionary/get_MicroServiceModelsDataServiceVersion').then(function (data) {
	    	var j = data;
	    	$scope.data = JSON.parse(j.data);
	    	$scope.microServiceModelsDictionaryDatas = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
	    });
	    
    PolicyAppService.getData('getDictionary/get_RiskTypeDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_RiskTypeDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
    });
    
    PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);
    });

    PolicyAppService.getData('get_DCAEPriorityValues').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	$scope.priorityDatas = JSON.parse($scope.data.priorityDatas);
    });
        
    /*getting user info from session*/
    var userid = null;
    UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
      	.then(function (response) {	      
          userid = response.userid;	  	
     });
    
	    $scope.editHeaderDefaults = message.modelAttributeDictionaryData;
	    $scope.editModelAttribute1 = {microservice: []};
	    if($scope.edit){
	    	if(message.modelAttributeDictionaryData.groupList != null){
	        var splitValue = message.modelAttributeDictionaryData.groupList.split(",");
	    	}	
	    }
	    $scope.saveHeaderDefaults = function(editHeaderDefaultsData) {
	        var uuu = "saveDictionary/ms_dictionary/save_headerDefaults";
	    	var postData={modelAttributeDictionaryData: editHeaderDefaultsData, userid: userid};
	    	$.ajax({
	        	type : 'POST',
	        	url : uuu,
	        	dataType: 'json',
	        	contentType: 'application/json',
	        	data: JSON.stringify(postData),
	        	success : function(data){
	            $scope.$apply(function(){
	            	$scope.microServiceHeaderDefaultDatas=data.microServiceHeaderDefaultDatas;});
	            if($scope.microServiceAttributeDictionaryDatas == "Duplicate"){
	            	Notification.error("Model Attribute Dictionary exists with Same Attribute Name.")
	            }else{      
	            	$modalInstance.close({microServiceHeaderDefaultDatas:$scope.microServiceHeaderDefaultDatas});
	            }
	        	},
	        	error : function(data){
	            Notification.error("Error while saving.");
	        	}
	    	});
	    	
	    };

	    $scope.close = function() {
	        $modalInstance.close();
	    };
	});