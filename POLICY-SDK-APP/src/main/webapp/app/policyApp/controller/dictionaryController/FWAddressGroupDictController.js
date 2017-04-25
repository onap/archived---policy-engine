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
app.controller('editFWAddressGroupController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, PolicyAppService, Notification){
    if(message.addressGroupDictionaryData==null)
        $scope.label='Add Address Group',
        $scope.apchoices = [];
    else{
        $scope.label='Edit Address Group'
        $scope.disableCd=true;
        $scope.apchoices = [];
        var headers = message.addressGroupDictionaryData.prefixList;
        var splitEqual = ',';
        if(headers != null){
        	if (headers.indexOf(splitEqual) >= 0) {
        		var splitValue = headers.split(splitEqual);
        		for(i = 0; i < splitValue.length; i++){
        			var key  = splitValue[i];
        			$scope.apchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
        		var key  = headers;
    			$scope.apchoices.push({'id':'choice'+1, 'option': key});
        	}
        }
    }
    

    PolicyAppService.getData('getDictionary/get_PrefixListDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.prefixListDictionaryDatas = JSON.parse($scope.data.prefixListDictionaryDatas);
    	console.log($scope.prefixListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });
	    
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
    
    $scope.editAddressGroup = message.addressGroupDictionaryData;

    $scope.saveFWAddressGroup = function(addressGroupDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(addressGroupDictionaryData.groupName)) {
    		Notification.error("Enter Valid Address Group Name without spaces or special characters");
    	}else{
    		var finalData = extend(addressGroupDictionaryData, $scope.attributeDatas[0]);
    		var uuu = "saveDictionary/fw_dictionary/save_addressGroup";
    		var postData={addressGroupDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.addressGroupDictionaryDatas=data.addressGroupDictionaryDatas;});
    				if($scope.addressGroupDictionaryDatas == "Duplicate"){
    					Notification.error("FW AddressGroup Dictionary exists with Same Address group Name.")
    				}else{      
    					console.log($scope.addressGroupDictionaryDatas);
    					$modalInstance.close({addressGroupDictionaryDatas:$scope.addressGroupDictionaryDatas});
    				}    
    			},
    			error : function(data){
    				alert("Error while saving.");
    			}
    		});
    	}
    };

    $scope.close = function() {
        $modalInstance.close();
    };
    
    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.attributeDatas = [{"attributes" : $scope.apchoices}];
    $scope.addAPNewChoice = function() {
        var newItemNo = $scope.apchoices.length+1;
        $scope.apchoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeAPChoice = function() {
        var lastItem = $scope.apchoices.length-1;
        $scope.apchoices.splice(lastItem);
    };
});