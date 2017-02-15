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
var editCLVarbindController =  function ($scope, $modalInstance, message, PapUrlService, UserInfoService, Notification){
    if(message.varbindDictionaryData==null)
        $scope.label='Add Varbind '
    else{
        $scope.label='Edit Varbind'
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
	
    $scope.editCLVarbind = message.varbindDictionaryData;

    $scope.saveCLVarbind = function(varbindDictionaryData) {
        var uuu = papUrl + "/ecomp/cl_dictionary/save_varbind.htm";
        var postData={varbindDictionaryData: varbindDictionaryData, loginId: loginId};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.varbindDictionaryDatas=data.varbindDictionaryDatas;});
                if($scope.varbindDictionaryDatas == "Duplicate"){
                	Notification.error("ClosedLoop Varbind Dictionary exists with Same Varbind Name.")
                }else{      
                	console.log($scope.varbindDictionaryDatas);
                    $modalInstance.close({varbindDictionaryDatas:$scope.varbindDictionaryDatas});
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