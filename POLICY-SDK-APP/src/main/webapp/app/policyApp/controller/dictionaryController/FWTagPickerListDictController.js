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
app.controller('editFWTagPickerController' ,  function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification, PolicyAppService){
    if(message.fwTagPickerDictionaryData==null)
        $scope.label='Add TagPicker',
        $scope.choices = [];
    else{
        $scope.label='Edit TagPicker'
        $scope.disableCd=true;
        $scope.choices = [];
        var headers = message.fwTagPickerDictionaryData.tagValues;
        var SplitChars = '#';
        if (headers.indexOf(SplitChars) >= 0) {
            var splitHeader = headers.split(SplitChars);
            var singleHeader  = splitHeader;
            var splitEqual = ':';
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
        	 var splitEqual = ':';
             if (headers.indexOf(splitEqual) >= 0) {
                 var splitValue = headers.split(splitEqual);
                 var key  = splitValue[0];
                 var value = splitValue[1];
                 $scope.choices.push({'id':'choice'+1, 'option': key , 'number' : value });
             }
        }
    }

		
    PolicyAppService.getData('getDictionary/get_TagListData').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.fwTagDictionaryDatas = JSON.parse($scope.data.fwTagDictionaryDatas);
    	console.log($scope.fwTagDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_TagNameByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.fwTagDictionaryKeyDatas = JSON.parse($scope.data.fwTagDictionaryDatas);
    	console.log($scope.fwTagDictionaryKeyDatas);
    }, function (error) {
    	console.log("failed");
    });
		
	
	$scope.fwTagDictionaryDataValues = [];
	$scope.getTagListValues = function(key){
		for (var i = 0; i < $scope.fwTagDictionaryDatas.length; ++i) {
    	    var obj = $scope.fwTagDictionaryDatas[i];
    	    if (obj.fwTagName == key){
    	    	var splitValue = obj.tagValues.split(',');
    	    	for (var j = 0; j < splitValue.length; ++j) {
    	    		$scope.fwTagDictionaryDataValues.push(splitValue[j].split(',')[0]);
    	    	}
    	    }
    	}
	};
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editFWTagPicker = message.fwTagPickerDictionaryData;

    $scope.saveFWTagPicker = function(fwTagPickerDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(fwTagPickerDictionaryData.tagPickerName)) {
    		Notification.error("Enter Valid TagPicker Name without spaces or special characters");
    	}else{
    		var finalData = extend(fwTagPickerDictionaryData, $scope.actions[0]);
    		var uuu = "saveDictionary/fw_dictionary/save_fwTagPicker";
    		var postData={fwTagPickerDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.fwTagPickerDictionaryDatas=data.fwTagPickerDictionaryDatas;});
    				if($scope.fwTagPickerDictionaryDatas == "Duplicate"){
    					Notification.error("TagPicker Dictionary exists with TagPicker Name.")
    				}else{      
    					console.log($scope.fwTagPickerDictionaryDatas);
    					$modalInstance.close({fwTagPickerDictionaryDatas:$scope.fwTagPickerDictionaryDatas});
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
    
    $scope.actions = [{"tags" : $scope.choices}];
    $scope.addNewChoice = function() {
        var newItemNo = $scope.choices.length+1;
        $scope.choices.push({'id':'choice'+newItemNo});
    };
    $scope.removeChoice = function() {
        var lastItem = $scope.choices.length-1;
        $scope.choices.splice(lastItem);
    };

});