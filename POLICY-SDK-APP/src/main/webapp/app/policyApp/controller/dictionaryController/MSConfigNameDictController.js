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
app.controller('editMSConfigController' ,  function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.microServiceConfigNameDictionaryData==null)
        $scope.label='Add Config Name'
    else{
        $scope.label='Edit Config Name'
        $scope.disableCd=true;
    }
    
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editMSConfig = message.microServiceConfigNameDictionaryData;

    $scope.saveMSConfig = function(microServiceConfigNameDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(microServiceConfigNameDictionaryData.name)) {
    		Notification.error("Enter Valid Config Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/ms_dictionary/save_configName";
    		var postData={microServiceConfigNameDictionaryData: microServiceConfigNameDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.microServiceConfigNameDictionaryDatas=data.microServiceConfigNameDictionaryDatas;});
    				if($scope.microServiceConfigNameDictionaryDatas == "Duplicate"){
    					Notification.error("MS ConfigName Dictionary exists with Same Config Name.")
    				}else{      
    					console.log($scope.microServiceConfigNameDictionaryDatas);
    					$modalInstance.close({microServiceConfigNameDictionaryDatas:$scope.microServiceConfigNameDictionaryDatas});
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