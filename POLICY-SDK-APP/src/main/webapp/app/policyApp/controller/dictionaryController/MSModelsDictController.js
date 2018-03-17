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
app.controller('editMSModelController' ,  function ($scope, $modalInstance, message, $http, UserInfoServiceDS2, Notification){
    if(message.microServiceModelsDictionaryData==null)
        $scope.label='Add Micro Service Model'
    else{
        $scope.label='Edit Micro Service Model'
        $scope.disableCd=true;
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
	var valid = true;
    $scope.editMSmodelName = message.microServiceModelsDictionaryData;

    $scope.uploadFile = function(files) {
    	var extn = files[0].name.substr(files[0].name.lastIndexOf('.')+1);
    	if(extn == 'zip' || extn == 'xmi'||  extn == 'yml'){
    		valid = true;
    		var fd = new FormData();
    		fd.append("file", files[0]);
    		$http.post("ms_dictionary/set_MSModelData", fd, {
    			withCredentials: false,
    			headers: {'Content-Type': undefined },
    			transformRequest: angular.identity
    		}).success(function(data){
    			if(data.errorMsg != undefined){
    				Notification.error(data.errorMsg);
    				valid = false;
    				return;
    			}     			
                if(data.classListDatas  == "EMPTY"){
                	Notification.error("No Micro Services Avaialble.")
                }else{      
                	$scope.classListDatas=data.classListDatas;
                	$scope.modalDatas = data.modelDatas;
                	$scope.modelType= data.modelType;
                	$scope.dataOrderInfo= data.dataOrderInfo;
                	console.log($scope.classListDatas);
                }
            }).error( );
    	}else{
    		Notification.error("Micro Service Model Upload file should ends with .zip or .xmi extension");
    		valid = false;
    	}

    };
    
    $scope.saveMSModel = function(microServiceModelsDictionaryData) {
    	if(valid){
    		 var uuu = "saveDictionary/ms_dictionary/save_model";
    	        var postData={microServiceModelsDictionaryData: microServiceModelsDictionaryData, userid: userid, classMap: $scope.modalDatas,modelType:$scope.modelType, dataOrderInfo:$scope.dataOrderInfo};
    	        $.ajax({
    	            type : 'POST',
    	            url : uuu,
    	            dataType: 'json',
    	            contentType: 'application/json',
    	            data: JSON.stringify(postData),
    	            success : function(data){
    	                $scope.$apply(function(){
    	                    $scope.microServiceModelsDictionaryDatas=data.microServiceModelsDictionaryDatas;});
    	                if($scope.microServiceModelsDictionaryDatas == "Duplicate"){
    	                	Notification.error("MS Models Dictionary exists with Same Model Name.")
    	                }else{      
    	                	console.log($scope.microServiceModelsDictionaryDatas);
    	                    $modalInstance.close({microServiceModelsDictionaryDatas:$scope.microServiceModelsDictionaryDatas});
    	                }
    	            },
    	            error : function(data){
    	                Notification.error("Error while saving.");
    	            }
    	        });
    	}else{
    		Notification.error("Please check Micro Service Model Upload file format.");
    	}
       
    };

    $scope.close = function() {
        $modalInstance.close();
    };
});