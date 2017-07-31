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
app.controller('editPSResourceController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.psResourceDictionaryData==null)
        $scope.label='Add New Resource'
    else{
        $scope.label='Edit Resource'
        $scope.disableCd=true;
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editPSResource = message.psResourceDictionaryData;

    $scope.savePSResource = function(psResourceDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(psResourceDictionaryData.name)) {
    		Notification.error("Enter Valid Resource Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/ps_dictionary/save_psResource";
    		var postData={psResourceDictionaryData: psResourceDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.psResourceDictionaryDatas=data.psResourceDictionaryDatas;});
    				if($scope.psResourceDictionaryDatas == "Duplicate"){
    					Notification.error("Resource Dictionary exists with Same Resource Name.")
    				}else{      
    					console.log($scope.psResourceDictionaryDatas);
    					$modalInstance.close({psResourceDictionaryDatas:$scope.psResourceDictionaryDatas});
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