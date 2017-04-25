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
app.controller('editFWTagListController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
   
	if(message.fwTagDictionaryDatas==null)
        $scope.label='Add Tag',
        $scope.choices = [];
    else{
        $scope.label='Edit Tag'
        $scope.disableCd=true;
        $scope.choices = [];
        var headers = message.fwTagDictionaryDatas.tagValues;
        var SplitChars = ',';
        if (headers.indexOf(SplitChars) >= 0) {
            var splitHeader = headers.split(SplitChars);
            var singleHeader  = splitHeader;
            for(i = 0; i < singleHeader.length; i++){
                 	var splitValue = singleHeader[i];
                 	var newItemNo = $scope.choices.length+1;
                 	$scope.choices.push({'id':'choice'+newItemNo, 'tags': splitValue });
                 }
        }else{ 
             $scope.choices.push({'id':'choice'+1, 'tags': headers });
        }
    }
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editFWTag = message.fwTagDictionaryDatas;

    $scope.saveFWTag = function(fwTagDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(fwTagDictionaryData.fwTagName)) {
    		Notification.error("Enter Valid Tag Name without spaces or special characters");
    	}else{
    		var finalData = extend(fwTagDictionaryData, $scope.attributeDatas[0]);
    		var uuu = "saveDictionary/fw_dictionary/save_fwTag";
    		var postData={fwTagDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.fwTagDictionaryDatas=data.fwTagDictionaryDatas;});
    				if($scope.fwTagDictionaryDatas == "Duplicate"){
    					Notification.error("Tag Dictionary exists with Same Tag Name.")
    				}else{      
    					console.log($scope.fwTagDictionaryDatas);
    					$modalInstance.close({fwTagDictionaryDatas:$scope.fwTagDictionaryDatas});
    				}
    			},
    			error : function(data){
    				alert("Error while saving.");
    			}
    		});
    	}
    };
    
    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.attributeDatas = [{"tags" : $scope.choices}];
    $scope.addNewChoice = function() {
      var newItemNo = $scope.choices.length+1;
      $scope.choices.push({'id':'choice'+newItemNo});
    };    
    $scope.removeChoice = function() {
      var lastItem = $scope.choices.length-1;
      $scope.choices.splice(lastItem);
    };
    

    $scope.close = function() {
        $modalInstance.close();
    };
});