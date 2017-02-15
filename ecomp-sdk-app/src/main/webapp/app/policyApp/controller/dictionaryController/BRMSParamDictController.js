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
var editBRMSParamController =  function ($scope, $modalInstance, message, $http, PapUrlService, UserInfoService, Notification){
    if(message.brmsParamDictionaryData==null)
        $scope.label='Add BRMS Rule'
    else{
        $scope.label='Edit BRMS Rule'
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
	
    $scope.editBRMSParam = message.brmsParamDictionaryData;
    
    $scope.uploadFile = function(files) {
        var fd = new FormData();
        fd.append("file", files[0]);
        $http.post(papUrl + "/ecomp/brms_dictionary/set_BRMSParamData.htm", fd, {
            withCredentials: false,
            headers: {'Content-Type': undefined },
            transformRequest: angular.identity
        }).success().error( );

    };
	
    $scope.MyFile = [];
    $scope.saveBRMSParam = function(brmsParamDictionaryData) {
    	var file  = $scope.MyFile;
        var uuu = papUrl + "/ecomp/brms_dictionary/save_BRMSParam.htm";
        var postData={brmsParamDictionaryData: brmsParamDictionaryData, loginId: loginId};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.brmsParamDictionaryDatas=data.brmsParamDictionaryDatas;});
					if($scope.brmsParamDictionaryDatas == "Duplicate"){
						Notification.error("BRMSParan Dictionary exists with Same Name.")
					}else{      
						console.log($scope.brmsParamDictionaryDatas);
						$modalInstance.close({brmsParamDictionaryDatas:$scope.brmsParamDictionaryDatas});
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