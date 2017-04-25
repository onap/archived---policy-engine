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
app.controller('editBRMSParamController' , function ($scope, $modalInstance, message, $http, UserInfoServiceDS2, Notification){
    if(message.brmsParamDictionaryData==null)
        $scope.label='Add BRMS Rule'
    else{
        $scope.label='Edit BRMS Rule'
        $scope.disableCd=true;
    }
   
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
	var valid = true;
    $scope.editBRMSParam = message.brmsParamDictionaryData;
    
    $scope.uploadFile = function(files) {
    	var extn = files[0].name.substr(files[0].name.lastIndexOf('.')+1);
    	if(extn == 'drl'){
    		valid = true;
    		   var fd = new FormData();
    	        fd.append("file", files[0]);
    	        $http.post("saveDictionary/brms_dictionary/set_BRMSParamData", fd, {
    	            withCredentials: false,
    	            headers: {'Content-Type': undefined },
    	            transformRequest: angular.identity
    	        }).success().error( );
    	}else{
    		Notification.error("BRMS Dictionary Upload file should ends with .drl extension");
    		valid = false;
    	}
    };
	
    $scope.MyFile = [];
    $scope.saveBRMSParam = function(brmsParamDictionaryData) {
    	if(valid){
    		var regex = new RegExp("^[a-zA-Z0-9_]*$");
    		if(!regex.test(brmsParamDictionaryData.ruleName)) {
    			Notification.error("Enter Valid Rule Name without spaces or special characters");
    		}else{
    			var file  = $scope.MyFile;
                var uuu = "saveDictionary/brms_dictionary/save_BRMSParam";
                var postData={brmsParamDictionaryData: brmsParamDictionaryData, userid: userid};
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
                    	Notification.error("Error while saving.");
                    }
                });
    		}
    	}else{
    		Notification.error("Please check BRMS Rule Upload file format.");
    	}
    };

    $scope.close = function() {
        $modalInstance.close();
    };
});