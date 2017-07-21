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
app.controller('importDictionaryController' ,  function ($scope, $modalInstance, message, $http, PolicyAppService, UserInfoServiceDS2, Notification){ 

	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	.then(function (response) {	  		
		userid = response.userid;	  	
	});

	$scope.importButton = true;
	var fd;
	$scope.uploadFile = function(files) {
		fd = new FormData();
		fd.append("file", files[0]);
		var fileExtension = files[0].name.split(".")[1];
		if(fileExtension == "csv"){
			$scope.importButton = false;
			$scope.$apply();
		}else{
			Notification.error("Upload the Dictionary file extends with .csv format");
		}
	};
	
	$scope.submitUpload = function(){
		$http.post("saveDictionary/dictionary/import_dictionary", fd,  {
			withCredentials: false,
			headers: {'Content-Type': undefined},
			transformRequest: angular.identity
		}).success(function(){
			Notification.success("Dictionary Uploaded Successfully");
			$scope.importButton = true;
		}).error(function(){
			 Notification.error("Dictionary Upload Failed");
		});
	};

$scope.close = function() {
	$modalInstance.close();
};
});