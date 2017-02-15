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

/**
 */
var editPSServiceController =  function ($scope, $modalInstance, message, PapUrlService, UserInfoService, Notification){
    if(message.psServiceDictionaryData==null)
        $scope.label='Add New Service'
    else{
        $scope.label='Edit Service'
        $scope.disableCd=true;
    }
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
	});
	
	/*getting user info from session*/
	var loginId = null;
	UserInfoService.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		loginId = response.userid;	  	
	 });
	
    $scope.editPSService = message.psServiceDictionaryData;

    $scope.savePSService = function(psServiceDictionaryData) {
        var uuu = papUrl + "/ecomp/ps_dictionary/save_psService.htm";
        var postData={psServiceDictionaryData: psServiceDictionaryData, loginId: loginId};
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
    };

    $scope.close = function() {
        $modalInstance.close();
    };
}