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
app.controller('editFWZoneController' ,  function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.zoneDictionaryData==null)
        $scope.label='Add Zone Name'
    else{
        $scope.label='Edit Zone Name'
        $scope.disableCd=true;
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editZoneName = message.zoneDictionaryData;

    $scope.saveZoneName = function(zoneDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(zoneDictionaryData.zoneName)) {
    		Notification.error("Enter Valid Zone Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/fw_dictionary/save_zoneName";
    		var postData={zoneDictionaryData: zoneDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.zoneDictionaryDatas=data.zoneDictionaryDatas;});
    				if($scope.zoneDictionaryDatas == "Duplicate"){
    					Notification.error("FW Zone Dictionary exists with Same Zone Name.")
    				}else{      
    					console.log($scope.zoneDictionaryDatas);
    					$modalInstance.close({zoneDictionaryDatas:$scope.zoneDictionaryDatas});
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