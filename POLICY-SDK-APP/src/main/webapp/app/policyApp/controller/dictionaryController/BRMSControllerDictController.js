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
app.controller('editBRMSControllerController' ,  function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.brmsControllerDictionaryDatas==null)
        $scope.label='Add New BRMS Controller'
    else{
    	if(message.disabled){
    		$scope.label='View BRMS Controller'
    	}else{
    		$scope.label='Edit BRMS Controller'
    	}
        $scope.disableCd=true;
    }
    $scope.editBRMSController = message.brmsControllerDictionaryDatas;
    $scope.disabled = message.disabled;
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
   
    $scope.saveBRMSController = function(brmsControllerDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
		if(!regex.test(brmsControllerDictionaryData.controllerName)) {
			Notification.error("Enter Valid Controller Name without spaces or special characters");
		}else{
			var uuu =  "saveDictionary/brms_dictionary/save_BRMSControllerData";
			if(brmsControllerDictionaryData && brmsControllerDictionaryData.controllerName){
				if(brmsControllerDictionaryData.controller){
					var postData={brmsControllerDictionaryData: brmsControllerDictionaryData, userid: userid};
					$.ajax({
						type : 'POST',
						url : uuu,
						dataType: 'json',
						contentType: 'application/json',
						data: JSON.stringify(postData),
						success : function(data){
							$scope.$apply(function(){
								$scope.brmsControllerDictionaryDatas=data.brmsControllerDictionaryDatas;});
							if($scope.brmsControllerDictionaryDatas == "Duplicate"){
								Notification.error("BRMS Controller Dictionary exists with Same Controller Name.")
							}else if($scope.brmsControllerDictionaryDatas == "Error"){
								Notification.error("BRMS Controller is not in proper required format");
							}else{      
								console.log($scope.brmsControllerDictionaryDatas);
								$modalInstance.close({brmsControllerDictionaryDatas:$scope.brmsControllerDictionaryDatas});
							}
						},
						error : function(data){
							alert("Error while saving.");
						}
					});
				}else{
					Notification.error("Controller Field is Empty.");
				}
			}else{
				Notification.error("BRMS Controller Name should be given.");
			}
		}
    };

    $scope.close = function() {
        $modalInstance.close();
    };
});