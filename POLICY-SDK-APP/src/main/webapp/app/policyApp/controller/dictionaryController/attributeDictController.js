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
app.controller('editAttributeController' ,function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){
    if(message.attributeDictionaryData==null)
        $scope.label='Add New Attribute',
        $scope.attributes = [];
    else{
    	$scope.attributes = [];
        $scope.label='Edit Attribute'
        $scope.disableCd=true;
        var headers = message.attributeDictionaryData.attributeValue;
        var splitEqual = ',';
        if(headers != null){
        	if (headers.indexOf(splitEqual) >= 0) {
        		var splitValue = headers.split(splitEqual);
        		for(i = 0; i < splitValue.length; i++){
        			var key  = splitValue[i];
        			$scope.attributes.push({'id':'choice'+i+1, 'attributeValues': key});
        		}
        	}else{
            	 var key  = headers;
                 $scope.attributes.push({'id':'choice'+1, 'attributeValues': key});
            }
        }
    }
    
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editAttributeName = message.attributeDictionaryData;

    $scope.saveAttributeName = function(attributeDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(attributeDictionaryData.xacmlId)) {
    		Notification.error("Enter Valid Attribute Name without spaces or special characters");
    	}else{
    		var finalData = extend(attributeDictionaryData, $scope.attributeDatas[0]);
    		var uuu =  "saveDictionary/attribute_dictionary/save_attribute";
    		var postData={attributeDictionaryData: attributeDictionaryData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.attributeDictionaryDatas=data.attributeDictionaryDatas;});
    				if($scope.attributeDictionaryDatas == "Duplicate"){
    					Notification.error("Attribute Dictionary exists with Same Attribute Name.")
    				}else{      
    					console.log($scope.attributeDictionaryDatas);
    					$modalInstance.close({attributeDictionaryDatas:$scope.attributeDictionaryDatas});
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
    
    $scope.attributeDatas = [{"userDataTypeValues" : $scope.attributes}];
    $scope.addNewChoice = function() {
      var newItemNo = $scope.attributes.length+1;
      $scope.attributes.push({'id':'choice'+newItemNo});
    };    
    $scope.removeChoice = function() {
      var lastItem = $scope.attributes.length-1;
      $scope.attributes.splice(lastItem);
    };
    
    $scope.close = function() {
        $modalInstance.close();
    };
});