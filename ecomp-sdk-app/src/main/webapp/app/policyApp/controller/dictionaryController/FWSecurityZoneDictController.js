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
var editfwSecurityZoneController =  function ($scope, $modalInstance, message, PapUrlService, UserInfoService, Notification){
    if(message.securityZoneDictionaryData==null)
        $scope.label='Add Security Zone'
    else{
        $scope.label='Edit Security Zone'
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
	
    $scope.editSecurityZone = message.securityZoneDictionaryData;

    $scope.saveSecurityZone = function(securityZoneDictionaryData) {
        var uuu = papUrl + "/ecomp/fw_dictionary/save_securityZone.htm";
        var postData={securityZoneDictionaryData: securityZoneDictionaryData, loginId: loginId};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.securityZoneDictionaryDatas=data.securityZoneDictionaryDatas;});
                if($scope.securityZoneDictionaryDatas == "Duplicate"){
                	Notification.error("FW SecurityZone Dictionary exists with Same Zone Name.")
                }else{      
                	console.log($scope.securityZoneDictionaryDatas);
                    $modalInstance.close({securityZoneDictionaryDatas:$scope.securityZoneDictionaryDatas});
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