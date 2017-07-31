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
app.controller('editSafePolicyWarningController' ,  function ($scope, $modalInstance, message, PolicyAppService, UserInfoServiceDS2, Notification){
	$scope.edit = false;
    if(message.safePolicyWarningData==null)
        $scope.label='Add New Safe Policy Warning'
    else{
        $scope.label='Edit Safe Policy Warning'
        $scope.disableCd=true;
        $scope.edit = true;
    }
	    
    PolicyAppService.getData('getDictionary/get_SafePolicyWarningDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.safePolicyWarningDatas = JSON.parse($scope.data.safePolicyWarningDatas);
    	console.log($scope.safePolicyWarningDatas);
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
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
   
    $scope.editSafePolicyWarning = message.safePolicyWarningData;
    $scope.editSafePolicyWarning1 = {resource: [], type:[], service: [], closedloop: []};
    if($scope.edit){
    	if(message.safePolicyWarningData.groupList != null){
    		var splitValue = message.safePolicyWarningData.groupList.split(",");
    		console.log(splitValue);
    		$scope.splittedGroupListValues = [];
    		var splitResource = splitValue[0].split("=");
    		$scope.editSafePolicyWarningScope1.riskType.push(splitResource[1]);
    	}	
    }
    
    $scope.saveSafePolicyWarning = function(safePolicyWarningData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(safePolicyWarningData.name)) {
    		Notification.error("Enter Valid Safe Policy Warning Name without spaces or special characters");
    	}else{
    		console.log(safePolicyWarningData);
    		var uuu = "saveDictionary/sp_dictionary/save_safePolicyWarning";
    		var postData={safePolicyWarningData: safePolicyWarningData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.safePolicyWarningDatas=data.safePolicyWarningDatas;});
    				if($scope.safePolicyWarningDatas == "Duplicate"){
    					Notification.error("Safe Policy Dictionary exists with Same Name.")
    				}else{      
    					console.log($scope.safePolicyWarningDatas);
    					$modalInstance.close({safePolicyWarningDatas:$scope.safePolicyWarningDatas});
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