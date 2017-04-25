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
var editEnforcerTypeController =  function ($scope, $modalInstance, message, UserInfoServiceDS2){
    if(message.enforcerDictionaryData==null)
        $scope.label='Add Enforcing Type'
    else{
        $scope.label='Edit Enforcing Type'
        $scope.disableCd=true;
    }
    $scope.editEnforcerType = message.enforcerDictionaryData;
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.saveEnforcerType = function(enforcerDictionaryData) {
        var uuu = "saveDictionary/enforcer_dictionary/save_enforcerType";
        var postData={enforcerDictionaryData: enforcerDictionaryData, userid: userid};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.enforcerDictionaryDatas=data.enforcerDictionaryDatas;});
                console.log($scope.enforcerDictionaryDatas);
                $modalInstance.close({enforcerDictionaryDatas:$scope.enforcerDictionaryDatas});
            },
            error : function(data){
                alert("Error while saving.");
            }
        });
    };

    $scope.close = function() {
        $modalInstance.close();
    };
}