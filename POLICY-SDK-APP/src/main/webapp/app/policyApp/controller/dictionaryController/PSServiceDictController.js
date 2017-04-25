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
app.controller('editPSServiceController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.psServiceDictionaryData==null)
        $scope.label='Add New Service'
    else{
        $scope.label='Edit Service'
        $scope.disableCd=true;
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editPSService = message.psServiceDictionaryData;

    $scope.savePSService = function(psServiceDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(psServiceDictionaryData.name)) {
    		Notification.error("Enter Valid Service Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/ps_dictionary/save_psService";
    		var postData={psServiceDictionaryData: psServiceDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.psServiceDictionaryDatas=data.psServiceDictionaryDatas;});
    				if($scope.psServiceDictionaryDatas == "Duplicate"){
    					Notification.error("Service Dictionary exists with Same Service Name.")
    				}else{      
    					console.log($scope.psServiceDictionaryDatas);
    					$modalInstance.close({psServiceDictionaryDatas:$scope.psServiceDictionaryDatas});
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