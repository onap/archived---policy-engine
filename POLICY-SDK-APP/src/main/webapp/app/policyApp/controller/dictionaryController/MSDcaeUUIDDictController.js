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
app.controller('editDCAEuuidController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.dcaeUUIDDictionaryData==null)
        $scope.label='Add Micro Service UUID'
    else{
        $scope.label='Edit Micro Service UUID'
        $scope.disableCd=true;
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editDCAEuuid = message.dcaeUUIDDictionaryData;

    $scope.saveDCAEUUID = function(dcaeUUIDDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(dcaeUUIDDictionaryData.name)) {
    		Notification.error("Enter Valid DCAEUUID Name without spaces or special characters");
    	}else{
    		var uuu = "saveDictionary/ms_dictionary/save_dcaeUUID";
    		var postData={dcaeUUIDDictionaryData: dcaeUUIDDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.dcaeUUIDDictionaryDatas=data.dcaeUUIDDictionaryDatas;});
    				if($scope.dcaeUUIDDictionaryDatas == "Duplicate"){
    					Notification.error("MS DCAEUUID Dictionary exists with Same DCAEUUID Name.")
    				}else{      
    					console.log($scope.dcaeUUIDDictionaryDatas);
    					$modalInstance.close({dcaeUUIDDictionaryDatas:$scope.dcaeUUIDDictionaryDatas});
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