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
app.controller('editActionPolicyDictController', function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.actionPolicyDictionaryData==null)
        $scope.label='Add Action Dictionary',
        $scope.choices = [];	
     else{
    	$scope.choices = [];
        $scope.label='Edit Action Dictionary'
        $scope.disableCd=true;
        var headers = message.actionPolicyDictionaryData.header;
        var SplitChars = ':';
        if (headers.indexOf(SplitChars) >= 0) {
            var splitHeader = headers.split(SplitChars);
            var singleHeader  = splitHeader;
            var splitEqual = '=';
            for(i = 0; i < singleHeader.length; i++){
            	 if (singleHeader[i].indexOf(splitEqual) >= 0) {
                 	var splitValue = singleHeader[i].split(splitEqual);
                 	var key  = splitValue[0];
                 	var value = splitValue[1];
                 	var newItemNo = $scope.choices.length+1;
                 	$scope.choices.push({'id':'choice'+newItemNo, 'option': key , 'number' : value });
                 }
            }
        }else{
        	 var splitEqual = '=';
             if (headers.indexOf(splitEqual) >= 0) {
                 var splitValue = headers.split(splitEqual);
                 var key  = splitValue[0];
                 var value = splitValue[1];
                 $scope.choices.push({'id':'choice'+1, 'option': key , 'number' : value });
             }
        }
    }
    
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editActionDict = message.actionPolicyDictionaryData;
    $scope.saveActionDict = function(actionPolicyDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(actionPolicyDictionaryData.attributeName)) {
    		Notification.error("Enter Valid Attribute Name without spaces or special characters");
    	}else{
    		var finalData = extend(actionPolicyDictionaryData, $scope.headerDatas[0]);
    		var uuu = "saveDictionary/action_dictionary/save_ActionDict";
    		var postData={actionPolicyDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.actionPolicyDictionaryDatas=data.actionPolicyDictionaryDatas;});
    				if($scope.actionPolicyDictionaryDatas == "Duplicate"){
    					Notification.error("ActionPolicy Dictionary exists with Same Attribute Name.")
    				}else{      
    					console.log($scope.actionPolicyDictionaryDatas);
    					$modalInstance.close({actionPolicyDictionaryDatas:$scope.actionPolicyDictionaryDatas});
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
    
    $scope.headerDatas = [{"headers" : $scope.choices}];
    $scope.addNewChoice = function() {
        var newItemNo = $scope.choices.length+1;
        $scope.choices.push({'id':'choice'+newItemNo});
    };
    $scope.removeChoice = function() {
        var lastItem = $scope.choices.length-1;
        $scope.choices.splice(lastItem);
    };
    
});