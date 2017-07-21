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
app.controller('editFWTermListController' , function ($scope, $modalInstance, message, PolicyAppService, UserInfoServiceDS2, Notification){
	 $scope.fromZonechoices = [];
     $scope.toZonechoices = [];
     $scope.sourceListchoices = [];
     $scope.destinationListchoices = [];
     $scope.sourceServicechoices = [];
     $scope.destinationServicechoices = [];
     $scope.actionListchoices = [];
     $scope.groupAddresses=[];
     $scope.groupServices=[];
    if(message.termListDictionaryData==null){
        $scope.label='Add Term List Name'
    }else{
        $scope.label='Edit Term List Name'
        $scope.disableCd=true; 
        var fromZoneheaders = message.termListDictionaryData.fromZone;
        var splitFromZone = ',';
        if(fromZoneheaders != null){
        	if (fromZoneheaders.indexOf(splitFromZone) >= 0) {
        		var splitFromZoneValue = fromZoneheaders.split(splitFromZone);
        		for(i = 0; i < splitFromZoneValue.length; i++){
        			var key  = splitFromZoneValue[i];
        			$scope.fromZonechoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = fromZoneheaders;
                 $scope.fromZonechoices.push({'id':'choice'+1, 'option': key});
            }
        }
        
        var toZoneheaders = message.termListDictionaryData.toZone;
        var splitToZone = ',';
        if(toZoneheaders != null){
        	if (toZoneheaders.indexOf(splitToZone) >= 0) {
        		var splitToZoneValue = toZoneheaders.split(splitToZone);
        		for(i = 0; i < splitToZoneValue.length; i++){
        			var key  = splitToZoneValue[i];
        			$scope.toZonechoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = toZoneheaders;
                 $scope.toZonechoices.push({'id':'choice'+1, 'option': key});
            }
        }
        
        var srcIPheaders = message.termListDictionaryData.srcIPList;
        var splitSrcIP = ',';
        if(srcIPheaders != null){
        	if (srcIPheaders.indexOf(splitSrcIP) >= 0) {
        		var splitSrcIPValue = srcIPheaders.split(splitSrcIP);
        		for(i = 0; i < splitSrcIPValue.length; i++){
        			var key  = splitSrcIPValue[i];
        			$scope.sourceListchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = srcIPheaders;
                 $scope.sourceListchoices.push({'id':'choice'+1, 'option': key});
            }
        }
        
        var desIPheaders = message.termListDictionaryData.destIPList;
        var splitDesIP = ',';
        if(desIPheaders != null){
        	if (desIPheaders.indexOf(splitDesIP) >= 0) {
        		var splitDestIPValue = desIPheaders.split(splitDesIP);
        		for(i = 0; i < splitDestIPValue.length; i++){
        			var key  = splitDestIPValue[i];
        			$scope.destinationListchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = desIPheaders;
                 $scope.destinationListchoices.push({'id':'choice'+1, 'option': key});
            }
        }
        
        var srcServheaders = message.termListDictionaryData.srcPortList;
        var splitSrcServ = ',';
        if(srcServheaders != null){
        	if (srcServheaders.indexOf(splitSrcServ) >= 0) {
        		var splitSrcServValue = srcServheaders.split(splitSrcServ);
        		for(i = 0; i < splitSrcServValue.length; i++){
        			var key  = splitSrcServValue[i];
        			$scope.sourceServicechoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = srcServheaders;
                 $scope.sourceServicechoices.push({'id':'choice'+1, 'option': key});
            }
        }
        
        var desServheaders = message.termListDictionaryData.destPortList;
        var splitdesSer = ',';
        if(desServheaders != null){
        	if (desServheaders.indexOf(splitdesSer) >= 0) {
        		var splitDesSerValue = desServheaders.split(splitdesSer);
        		for(i = 0; i < splitDesSerValue.length; i++){
        			var key  = splitDesSerValue[i];
        			$scope.destinationServicechoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = desServheaders;
                 $scope.destinationServicechoices.push({'id':'choice'+1, 'option': key});
            }
        }
        
        var actionheaders = message.termListDictionaryData.action;
        var splitAction = ',';
        if(actionheaders != null){
        	if (actionheaders.indexOf(splitAction) >= 0) {
        		var splitActionValue = actionheaders.split(splitAction);
        		for(i = 0; i < splitActionValue.length; i++){
        			var key  = splitActionValue[i];
        			$scope.actionListchoices.push({'id':'choice'+i+1, 'option': key});
        		}
        	}else{
            	 var key  = actionheaders;
                 $scope.actionListchoices.push({'id':'choice'+1, 'option': key});
            }
        }
    }

		
    PolicyAppService.getData('getDictionary/get_PrefixListDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.prefixListDictionaryDatas = JSON.parse($scope.data.prefixListDictionaryDatas);
    	console.log($scope.prefixListDictionaryDatas);
    	for(i = 0; i < $scope.prefixListDictionaryDatas.length; i++){
			var key  = $scope.prefixListDictionaryDatas[i];
			$scope.groupAddresses.push(key);
		}
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_ZoneDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.zoneDictionaryDatas = JSON.parse($scope.data.zoneDictionaryDatas);
    	console.log($scope.zoneDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_AddressGroupDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.addressGroupDictionaryDatas = JSON.parse($scope.data.addressGroupDictionaryDatas);
    	console.log($scope.addressGroupDictionaryDatas);
    	for(i = 0; i < $scope.addressGroupDictionaryDatas.length; i++){
			var key  = $scope.addressGroupDictionaryDatas[i];
			$scope.groupAddresses.push(key);
		}
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_ServiceListDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.serviceListDictionaryDatas = JSON.parse($scope.data.serviceListDictionaryDatas);
    	console.log($scope.serviceListDictionaryDatas);
    	for(i = 0; i < $scope.serviceListDictionaryDatas.length; i++){
			var key  = $scope.serviceListDictionaryDatas[i];
			$scope.groupServices.push(key);
		}
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_ServiceGroupDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.serviceGroupDictionaryDatas = JSON.parse($scope.data.serviceGroupDictionaryDatas);
    	console.log($scope.serviceGroupDictionaryDatas);
    	for(i = 0; i < $scope.serviceGroupDictionaryDatas.length; i++){
			var key  = $scope.serviceGroupDictionaryDatas[i];
			$scope.groupServices.push(key);
		}
    }, function (error) {
    	console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_ActionListDictionaryDataByName').then(function (data) {
    	var j = data;
    	$scope.data = JSON.parse(j.data);
    	console.log($scope.data);
    	$scope.actionListDictionaryDatas = JSON.parse($scope.data.actionListDictionaryDatas);
    	console.log($scope.actionListDictionaryDatas);
    }, function (error) {
    	console.log("failed");
    });

	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
	
    $scope.editTermList = message.termListDictionaryData;

    $scope.saveTermName = function(termListDictionaryData) {
    	var regex = new RegExp("^[a-zA-Z0-9_]*$");
    	if(!regex.test(termListDictionaryData.termName)) {
    		Notification.error("Enter Valid Term Name without spaces or special characters");
    	}else{
    		var mergeFromZoneData = extend(termListDictionaryData, $scope.fromZoneDatas[0]);
    		var mergeToData = extend(mergeFromZoneData, $scope.toZoneDatas[0]);
    		var mergeSourceListData = extend(mergeToData, $scope.sourceListDatas[0]);
    		var mergeDListData = extend(mergeSourceListData, $scope.destinationListDatas[0]);
    		var mergeSServicesData = extend(mergeDListData, $scope.sourceServicesDatas[0]);
    		var mergeDServicesData = extend(mergeSServicesData, $scope.destinationServicesDatas[0]);
    		var finalData = extend(mergeDServicesData, $scope.actionListDatas[0]);
    		var uuu = "saveDictionary/fw_dictionary/save_termList";
    		var postData={termListDictionaryData: finalData, userid: userid};
    		$.ajax({
    			type : 'POST',
    			url : uuu,
    			dataType: 'json',
    			contentType: 'application/json',
    			data: JSON.stringify(postData),
    			success : function(data){
    				$scope.$apply(function(){
    					$scope.termListDictionaryDatas=data.termListDictionaryDatas;});
    				if($scope.termListDictionaryDatas == "Duplicate"){
    					Notification.error("FW TermList Dictionary exists with Same Term Name.")
    				}else{      
    					console.log($scope.termListDictionaryDatas);
    					$modalInstance.close({termListDictionaryDatas:$scope.termListDictionaryDatas});
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
    
    $scope.fromZoneDatas = [{"fromZoneDatas" : $scope.fromZonechoices}];
    $scope.addFromZoneNewChoice = function() {
        var newItemNo = $scope.fromZonechoices.length+1;
        $scope.fromZonechoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeFromZoneChoice = function() {
        var lastItem = $scope.fromZonechoices.length-1;
        $scope.fromZonechoices.splice(lastItem);
    };
    
    $scope.toZoneDatas = [{"toZoneDatas" : $scope.toZonechoices}];
    $scope.addToZoneNewChoice = function() {
        var newItemNo = $scope.toZonechoices.length+1;
        $scope.toZonechoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeToZoneChoice = function() {
        var lastItem = $scope.toZonechoices.length-1;
        $scope.toZonechoices.splice(lastItem);
    };
    
    $scope.sourceListDatas = [{"sourceListDatas" : $scope.sourceListchoices}];
    $scope.addSourceListNewChoice = function() {
        var newItemNo = $scope.sourceListchoices.length+1;
        $scope.sourceListchoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeSourceListChoice = function() {
        var lastItem = $scope.sourceListchoices.length-1;
        $scope.sourceListchoices.splice(lastItem);
    };
    
    $scope.destinationListDatas = [{"destinationListDatas" : $scope.destinationListchoices}];
    $scope.addDListNewChoice = function() {
        var newItemNo = $scope.destinationListchoices.length+1;
        $scope.destinationListchoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeDlistChoice = function() {
        var lastItem = $scope.destinationListchoices.length-1;
        $scope.destinationListchoices.splice(lastItem);
    };
    
    $scope.sourceServicesDatas = [{"sourceServiceDatas" : $scope.sourceServicechoices}];
    $scope.addSourceServiceNewChoice = function() {
        var newItemNo = $scope.sourceServicechoices.length+1;
        $scope.sourceServicechoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeSourceServiceChoice = function() {
        var lastItem = $scope.sourceServicechoices.length-1;
        $scope.sourceServicechoices.splice(lastItem);
    };
    
    $scope.destinationServicesDatas = [{"destinationServiceDatas" : $scope.destinationServicechoices}];
    $scope.addDServicesNewChoice = function() {
        var newItemNo = $scope.destinationServicechoices.length+1;
        $scope.destinationServicechoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeDServicesChoice = function() {
        var lastItem = $scope.destinationServicechoices.length-1;
        $scope.destinationServicechoices.splice(lastItem);
    };
    
    $scope.actionListDatas = [{"actionListDatas" : $scope.actionListchoices}];
    $scope.addActionListNewChoice = function() {
        var newItemNo = $scope.actionListchoices.length+1;
        $scope.actionListchoices.push({'id':'choice'+newItemNo});
    };
    $scope.removeActionListChoice = function() {
        var lastItem = $scope.actionListchoices.length-1;
        $scope.actionListchoices.splice(lastItem);
    };
});