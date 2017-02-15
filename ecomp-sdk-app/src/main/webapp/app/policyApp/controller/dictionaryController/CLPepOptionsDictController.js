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
var editPEPOptionsController =  function ($scope, $modalInstance, message, PapUrlService, UserInfoService, Notification){
    if(message.pepOptionsDictionaryData==null)
        $scope.label='Add PEP Options',
        $scope.choices = [];
    else{
        $scope.label='Edit PEP Options'
        $scope.disableCd=true;
        $scope.choices = [];
        var headers = message.pepOptionsDictionaryData.actions;
        var SplitChars = ':#@';
        if (headers.indexOf(SplitChars) >= 0) {
            var splitHeader = headers.split(SplitChars);
            var singleHeader  = splitHeader;
            var splitEqual = '=#@';
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
        	 var splitEqual = '=#@';
             if (headers.indexOf(splitEqual) >= 0) {
                 var splitValue = headers.split(splitEqual);
                 var key  = splitValue[0];
                 var value = splitValue[1];
                 $scope.choices.push({'id':'choice'+1, 'option': key , 'number' : value });
             }
        }
    }
    
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
	});
	
	/*getting user info from session*/
	var loginId = null;
	UserInfoService.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		loginId = response.userid;	  	
	 });
	
    $scope.editPEPOptions = message.pepOptionsDictionaryData;

    $scope.saveCLPepOptions = function(pepOptionsDictionaryData) {
    	var finalData = extend(pepOptionsDictionaryData, $scope.actions[0]);
        var uuu = papUrl + "/ecomp/cl_dictionary/save_pepOptions.htm";
        var postData={pepOptionsDictionaryData: finalData, loginId: loginId};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.pepOptionsDictionaryDatas=data.pepOptionsDictionaryDatas;});
					if($scope.pepOptionsDictionaryDatas == "Duplicate"){
						Notification.error("PEP Options Dictionary exists with Same PEP Name.")
					}else{      
						console.log($scope.pepOptionsDictionaryDatas);
						$modalInstance.close({pepOptionsDictionaryDatas:$scope.pepOptionsDictionaryDatas});
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
    
    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.actions = [{"attributes" : $scope.choices}];
    $scope.addNewChoice = function() {
        var newItemNo = $scope.choices.length+1;
        $scope.choices.push({'id':'choice'+newItemNo});
    };
    $scope.removeChoice = function() {
        var lastItem = $scope.choices.length-1;
        $scope.choices.splice(lastItem);
    };

}