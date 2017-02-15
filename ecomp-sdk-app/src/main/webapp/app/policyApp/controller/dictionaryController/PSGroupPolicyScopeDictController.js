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
var editPSGroupPolicyScopeController =  function ($scope, $modalInstance, message, PolicyScopeService, PapUrlService, UserInfoService, Notification){
	$scope.edit = false;
    if(message.groupPolicyScopeListData==null)
        $scope.label='Add New Group Policy Scope'
    else{
        $scope.label='Edit Group Policy Scope'
        $scope.disableCd=true;
        $scope.edit = true;
    }
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    PolicyScopeService.getPSServiceDictionaryDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.psServiceDictionaryDatas = JSON.parse($scope.data.psServiceDictionaryDatas);
	        console.log($scope.psServiceDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyScopeService.getPSTypeDictionaryDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.psTypeDictionaryDatas = JSON.parse($scope.data.psTypeDictionaryDatas);
	        console.log($scope.psTypeDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyScopeService.getPSResourceDictionaryDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.psResourceDictionaryDatas = JSON.parse($scope.data.psResourceDictionaryDatas);
	        console.log($scope.psResourceDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });

	    PolicyScopeService.getPSClosedLoopDictionaryDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.psClosedLoopDictionaryDatas = JSON.parse($scope.data.psClosedLoopDictionaryDatas);
	        console.log($scope.psClosedLoopDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	});
	
	/*getting user info from session*/
	var loginId = null;
	UserInfoService.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		loginId = response.userid;	  	
	 });
   
    $scope.editPSGroupPolicyScope = message.groupPolicyScopeListData;
    $scope.editPSGroupPolicyScope1 = {resource: [], type:[], service: [], closedloop: []};
    if($scope.edit){
    	if(message.groupPolicyScopeListData.groupList != null){
    		var splitValue = message.groupPolicyScopeListData.groupList.split(",");
    		console.log(splitValue);
    		$scope.splittedGroupListValues = [];
    		var splitResource = splitValue[0].split("=");
    		$scope.editPSGroupPolicyScope1.resource.push(splitResource[1]);
    		var splitType = splitValue[1].split("=");
    		$scope.editPSGroupPolicyScope1.type.push(splitType[1]);
    		var splitService = splitValue[2].split("=");
    		$scope.editPSGroupPolicyScope1.service.push(splitService[1]);
    		var splitCloop = splitValue[3].split("=");
    		$scope.editPSGroupPolicyScope1.closedloop.push(splitCloop[1]);
    	}	
    }
    
    $scope.savePSGroupPolicyScope = function(groupPolicyScopeListData, groupPolicyScopeListData1) {
    	console.log(groupPolicyScopeListData1);
        var uuu = papUrl + "/ecomp/ps_dictionary/save_psGroupPolicyScope.htm";
        var postData={groupPolicyScopeListData: groupPolicyScopeListData,
        		groupPolicyScopeListData1: groupPolicyScopeListData1, loginId: loginId};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
            	$scope.$apply(function(){
            		$scope.groupPolicyScopeListDatas=data.groupPolicyScopeListDatas;});
            	if($scope.groupPolicyScopeListDatas == "Duplicate"){
            		Notification.error("GroupPolicyScope Dictionary exists with Same Group Name.")
            	}else if($scope.groupPolicyScopeListDatas == "DuplicateGroup"){
            		Notification.error("GroupPolicyScope Dictionary exists with Same Group List.")
            	}else{      
            		console.log($scope.groupPolicyScopeListDatas);
            		$modalInstance.close({groupPolicyScopeListDatas:$scope.groupPolicyScopeListDatas});
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