/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

var mainDictionarys = ["Action Policy", "BRMS Policy", "Common Dictionary", "ClosedLoop Policy","Decision Policy", "Descriptive Policy",
	 "Firewall Policy", "MicroService Policy", "Optimization Policy", "Policy Scope", "Safe Policy Dictionary"];
var subDictionarys = [["Action Dictionary"],
	["BRMS Controller" , "BRMS Dependency", "BRMS Param Template"],
	["Attribute Dictionary","OnapName Dictionary"],
	["PEP Options","Site Dictionary","Service Dictionary","Varbind Dictionary", "VNF Type","VSCL Action"],
	["Settings Dictionary","Rainy Day Allowed Treatments"],
	["Descriptive Scope"],
	["Action List", "Address Group", "Parent Dictionary List", "Port List", "Prefix List", "Protocol List", "Security Zone", "Service Group", "Service List", "Tag List", "Tag Picker List", "Term List", "Zone"],
	["DCAE UUID","MicroService ConfigName","MicroService Location", "MicroService Models", "MicroService Dictionary"],
	["ONAP Optimization Models"],
	["Closed Loop", "Group Policy Scope", "Resource", "Service", "Type"],
	["Risk Type", "Safe Policy Warning"]];
app.controller('dictionaryTabController', function ($scope, PolicyAppService, modalService, $modal){
	$( "#dialog" ).hide();
	
	$scope.isDisabled = true;
	PolicyAppService.getData('get_LockDownData').then(function(data){
		 var j = data;
		 $scope.data = JSON.parse(j.data);
		 $scope.lockdowndata = JSON.parse($scope.data.lockdowndata);
		 if($scope.lockdowndata[0].lockdown == true){
			 $scope.isDisabled = true;
			 this.isDisabled = true;
		 }else{
			 $scope.isDisabled = false;
			 this.isDisabled = false;
		 }
		 console.log($scope.data);
	 },function(error){
		 console.log("failed");
	 });
	 
	$scope.options1 = mainDictionarys;
	$scope.options2 = [];
	$scope.getOptions2 = function(){
		var key = $scope.options1.indexOf($scope.option1);
		var myNewOptions = subDictionarys[key];
		$scope.options2 = myNewOptions;
	};
	
	$scope.import = function() {
		$scope.data = {};
		var modalInstance = $modal.open({
			backdrop: 'static', keyboard: false,
			templateUrl : 'import_dictionary_popup.html',
			controller: 'importDictionaryController',
			resolve: {
				message: function () {
					var message = {
						data: $scope.data
					};
					return message;
				}					
			}
		}); 
		modalInstance.result.then(function(response){
			console.log('response', response);
		});
	};

});