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
app.controller('editPSClosedLoopController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.psClosedLoopDictionaryData==null)
        $scope.label='Add New Policy Scope ClosedLoop'
    else{
        $scope.label='Edit Policy Scope ClosedLoop'
        $scope.disableCd=true;
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editPSClosedLoop = message.psClosedLoopDictionaryData;

    $scope.savePSClosedLoop = function(psClosedLoopDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(psClosedLoopDictionaryData.name)) {
    		Notification.error("Enter Valid Policy Scope Closed Loop Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/ps_dictionary/save_psClosedLoop";
    		var postData={psClosedLoopDictionaryData: psClosedLoopDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.psClosedLoopDictionaryDatas=data.psClosedLoopDictionaryDatas;});
    				if($scope.psClosedLoopDictionaryDatas == "Duplicate"){
    					Notification.error("ClosedLoop Dictionary exists with Same ClosedLoop Name.")
    				}else{      
    					console.log($scope.psClosedLoopDictionaryDatas);
    					$modalInstance.close({psClosedLoopDictionaryDatas:$scope.psClosedLoopDictionaryDatas});
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