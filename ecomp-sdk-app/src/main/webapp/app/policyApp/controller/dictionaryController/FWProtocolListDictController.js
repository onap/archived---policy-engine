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
var editFWProtocolListController =  function ($scope, $modalInstance, message, PapUrlService, UserInfoService, Notification){
    if(message.protocolListDictionaryData==null)
        $scope.label='Add Protocol Name'
    else{
        $scope.label='Edit Protocol Name'
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
	
    $scope.editProtocolList = message.protocolListDictionaryData;

    $scope.saveProtocolList = function(protocolListDictionaryData) {
        var uuu = papUrl + "/ecomp/fw_dictionary/save_protocolList.htm";
        var postData={protocolListDictionaryData: protocolListDictionaryData, loginId: loginId};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.protocolListDictionaryDatas=data.protocolListDictionaryDatas;});
                if($scope.protocolListDictionaryDatas == "Duplicate"){
                	Notification.error("FW ProtocolList Dictionary exists with Same Protocol Name.")
                }else{      
                	console.log($scope.protocolListDictionaryDatas);
                    $modalInstance.close({protocolListDictionaryDatas:$scope.protocolListDictionaryDatas});
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