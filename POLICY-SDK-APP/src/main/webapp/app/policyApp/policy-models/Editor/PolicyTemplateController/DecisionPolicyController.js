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
angular.module('abs').controller('decisionPolicyController', ['$scope', '$compile', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', '$http', function ($scope, $compile, PolicyAppService, PolicyNavigator, modalService, $modal, Notification, $http) {
    $("#dialog").hide();

    $scope.policyNavigator;
    $scope.savebutton = true;
    $scope.refreshCheck = false;
    $scope.disableOnCreate = false;
    $scope.validateButton = false;
    $scope.isCheck = false;
    $scope.notRawPolicy = true;
    var layer = 0;
    
    if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    	$scope.disableOnCreate = true;
    	$scope.temp.policy = {
    			policyType : "Decision"
    	}
    };
    
    if(!$scope.temp.policy.editPolicy  && $scope.temp.policy.readOnly){
        $scope.validateButton = true;
     }


    $scope.refresh = function() {
        if ($scope.refreshCheck) {
            $scope.policyNavigator.refresh();
        }
        $scope.modal('createNewPolicy', true);
        $scope.temp.policy = "";
    };

    $scope.modal = function(id, hide) {
        return $('#' + id).modal(hide ? 'hide' : 'show');
    };

    if ($scope.temp.policy.ruleProvider == undefined) {
        $scope.temp.policy.ruleProvider = "Custom";
    }

	if($scope.temp.policy.blackListEntryType==undefined){
		$scope.temp.policy.blackListEntryType="Use Manual Entry";
	}

    if ($scope.temp.policy.editPolicy != undefined || $scope.temp.policy.readOnly != undefined) {
        if ($scope.temp.policy.configName == undefined) {
            $scope.isCheck = false;
        } else {
            $scope.isCheck = true;
        }
    } else {
        $scope.isCheck = false;
    }

    PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);
        console.log($scope.onapNameDictionaryDatas);
    }, function(error) {
        console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_SettingsDictionaryDataByName').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.settingsDictionaryDatas = JSON.parse($scope.data.settingsDictionaryDatas);
        console.log($scope.settingsDictionaryDatas);
    }, function(error) {
        console.log("failed");
    });

    PolicyAppService.getData('get_FunctionDefinitionDataByName').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.functionDefinitionDatas = JSON.parse($scope.data.functionDefinitionDatas);
        console.log($scope.functionDefinitionDatas);
    }, function(error) {
        console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_AttributeDatabyAttributeName').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.attributeDictionaryDatas = JSON.parse($scope.data.attributeDictionaryDatas);
        console.log($scope.attributeDictionaryDatas);
    }, function(error) {
        console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_RainyDayDictionaryDataByName').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.rainyDayDictionaryDatas = JSON.parse($scope.data.rainyDayDictionaryDatas);
        console.log($scope.rainyDayDictionaryDatas);
    }, function(error) {
        console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_RainyDayDictionaryData').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        $scope.rainyDayDictionaryDataEntity = JSON.parse($scope.data.rainyDayDictionaryDatas);
        console.log($scope.rainyDayDictionaryDataEntity);
    }, function(error) {
        console.log("failed");
    });

    PolicyAppService.getData('getDictionary/get_DecisionMSModelsDataByName').then(function(data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        console.log($scope.data);
        var inputModelList = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
        var unique = {};
        var uniqueList = [];
        for (var i = 0; i < inputModelList.length; i++) {
            if (typeof unique[inputModelList[i]] == "undefined") {
                unique[inputModelList[i]] = "";
                uniqueList.push(inputModelList[i]);
            }
        }
        $scope.microServiceModelsDictionaryDatas = uniqueList;
        console.log($scope.microServiceModelsDictionaryDatas);
    }, function(error) {
        console.log("failed");
    });

    $scope.choices = [];
    $scope.attributeDatas = [{
        "attributes": $scope.choices
    }];
    $scope.isInitEditTemplate = true; // just initially create the edit
    // template, didn't click add button
    // yet.
    addNewChoiceMS = function(value) {
		 console.log("input value : " + value);
		 if(value != undefined){
			if (value.startsWith('div.')){
				value = value.replace('div.','');
			}
			
			console.log(" document.getElementById : div."+value);
			var parentElement = document.getElementById("div."+value);
			console.log("parentElement : " + parentElement);
			var div = document.getElementById(value+"@0");
			if(div != null){
				var clone = div.cloneNode(true); 
				var addElement = parentElement.childElementCount;
				clone.id = ''+value+'@'+addElement;
				clone.value = '';
				if($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly){ 
					// if its view or edit
					if($scope.temp.policy.ruleData[clone.id] || ($scope.temp.policy.editPolicy && !$scope.isInitEditTemplate)){  
						// Only append child if its valie found in ruleData or
						// edit more
						if($scope.temp.policy.ruleData[clone.id]){
						    clone.value = $scope.temp.policy.ruleData[clone.id];
						}
						if(!clone.className.includes("child_single")){
						   clone.className += ' child_single'; // here cloned
																// is single
																// element
						}
						document.getElementById("div."+value).appendChild(clone);
						plainAttributeKeys.push(''+value+'@'+addElement);
					}
				}else{ // not view or edit
					if(!clone.className.includes("child_single")){
					    clone.className += ' child_single'; // here cloned is
															// single element
					}
					document.getElementById("div."+value).appendChild(clone);
					plainAttributeKeys.push(''+value+'@'+addElement);
				}
			}else{
				
				if(parentElement == null){
					return;
				}
				div = document.getElementById("div."+value+"@0");
				if(div){
					
					if(!div.className.includes('children_group border')){
						layer++;
						if(layer > 4){ 
							layer = 1
						};
						// here is div with group of children
			    	    div.className += ' children_group border' + layer;
					}
				}
				var childElement = parentElement.firstElementChild;
				var countParent = parentElement.childElementCount;
				var childElementString = childElement.innerHTML;
				var find = value+"@0";
				var re = new RegExp(find, 'g');
				childElementString = childElementString.replace(re,value+'@' + countParent);
				var clone = childElement.cloneNode(true);
				for (var ii = 0; ii < parentElement.childNodes.length; ii++){
		            var childId = parentElement.childNodes[ii].id;
		            if(ii = parentElement.childNodes.length){
		            	var childnewId = childId.slice(0, -1);
		            	var count = childId.slice(-1);
		            }
		        }
				var countvalue = parseInt(count) + 1;
				clone.id = childnewId+countvalue;
				clone.value = '';
				clone.innerHTML=childElementString;
				document.getElementById("div."+value).appendChild(clone);
				var selects = clone.getElementsByTagName("select");
				var inputs = clone.getElementsByTagName("input");
				var removeValues = [];
				for(var i=0; i<inputs.length; i++){
					if ($scope.temp.policy.ruleData!=undefined){
						var checkValue = $scope.temp.policy.ruleData[inputs[i].id];
						if (checkValue!=undefined && checkValue != "undefined"){
							document.getElementById(inputs[i].id).value = checkValue;
								plainAttributeKeys.push(inputs[i].id);
						} else {
							plainAttributeKeys.push(inputs[i].id);
						}
					}else {
						plainAttributeKeys.push(inputs[i].id);
					}
				}
				
				for(var i=0; i<selects.length; i++){
					if ($scope.temp.policy.ruleData!=undefined){
						var checkValue = $scope.temp.policy.ruleData[selects[i].id];
						if (checkValue!=undefined && checkValue!="undefined"){
							if($scope.temp.policy.ruleData != null){
								var checkValue = $scope.temp.policy.ruleData[selects[i].id];
								document.getElementById(selects[i].id).value = $scope.temp.policy.ruleData[selects[i].id];
								plainAttributeKeys.push(selects[i].id);
							}
						} else {
							plainAttributeKeys.push(selects[i].id);
						}
					}else {
						plainAttributeKeys.push(selects[i].id);
					}
				}

				for (var k=0; k<removeValues.length; k++){
					var elem = document.getElementById(removeValues[k]);
					elem.parentNode.removeChild(elem);
				}
				var ele = angular.element(document.getElementById("div."+value));
	    		$compile(ele.contents())($scope)
			}
		 }
	 };
	 
    removeChoiceMS = function(value) {
        console.log(value);
        if (value != undefined) {
            var c = document.getElementById("div." + value).childElementCount;

            if (c == 1) {
                Notification.error("The original one is not removable.");
                return;
            }
            document.getElementById("div." + value).removeChild(document.getElementById("div." + value).lastChild);
        }
    };

    function findVal(object, key) {
        var value;
        Object.keys(object).some(function(k) {
            if (k === key) {
                value = object[k];
                return true;
            }
            if (object[k] && typeof object[k] === 'object') {
                value = findVal(object[k], key);
                return value !== undefined;
            }
        });
        return value;
    }

    $scope.pullVersion = function(serviceName) {
        console.log(serviceName);
        if (serviceName != undefined) {
            var uuu = "policyController/getModelServiceVersioneData.htm";
            var postData = {
                policyData: serviceName,
                modelType: 'decision'
            };
            $.ajax({
                type: 'POST',
                url: uuu,
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(postData),
                success: function(data) {
                    $scope.$apply(function() {
                        $scope.microServiceModelsDictionaryVersionDatas = data[0].dcaeModelVersionData;
                    });
                },
                error: function(data) {
                    alert("Error While Retriving the Template Layout Pattren.");
                }
            });
        }
    };

    var splitDash = '-';
    var splitEqual = '=';
    var splitComma = ',';
    var splitcolon = ':';
    var splitsemicolon = ";";
    var splitEnum = "],";
    var plainAttributeKeys = [];
    var matching = [];
    var enumKeyList = [];
    var dictionaryList = [];
    var dictionaryNameList = [];
    $scope.addDataToFields = function(serviceName, version) {
        if (serviceName != null && version != null) {
            var service = serviceName + "-v" + version;
            var myNode = document.getElementById("DynamicTemplate");
            myNode.innerHTML = '';
            var uuu = "policyController/getDCAEMSTemplateData.htm";
            var postData = {
                policyData: service
            };
            var dataOrderInfo = "";
            
            document.getElementById("msPolicyloader").style.visibility = "visible";

            $.ajax({
                type: 'POST',
                url: uuu,
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(postData),
                success: function(data) {
                	document.getElementById("msPolicyloader").style.visibility = "hidden";
                    $scope.$apply(function() {
                        var plainAttributeKeys = [];
                        $scope.dcaeModelData = data[0].dcaeModelData;
                        $scope.dcaeJsonDate = data[0].jsonValue;
                        $scope.dataOrderInfo = null;
                        $scope.dataOrderInfo = data[0].dataOrderInfo;
                        console.log("data[0].dataOrderInfo: " + data[0].dataOrderInfo);
                        console.log("$scope.dataOrderInfo: " + $scope.dataOrderInfo);

                        if (data[0].allManyTrueKeys) {
                            console.log("$scope.allManyTrueKeys: " + $scope.allManyTrueKeys);
                        }
                        console.log("$scope.dcaeJsonDate: " + $scope.dcaeJsonDate);
                        var attributes = $scope.dcaeModelData.attributes;
                        var refAttributes = $scope.dcaeModelData.ref_attributes;
                        var subAttributes = $scope.dcaeModelData.sub_attributes;
                        console.log("subAttributes: " + subAttributes);
                        console.log("refAttributes: " + refAttributes);
                        var headDefautlsData = data[0].headDefautlsData;
                        if (headDefautlsData != null) {
                            $scope.temp.policy.onapName = headDefautlsData.onapName;
                            $scope.temp.policy.guard = headDefautlsData.guard;
                            $scope.temp.policy.riskType = headDefautlsData.riskType;
                            $scope.temp.policy.riskLevel = headDefautlsData.riskLevel;
                            $scope.temp.policy.priority = headDefautlsData.priority;
                        } 

                        var enumAttributes = $scope.dcaeModelData.enumValues;
                        var annotation = $scope.dcaeModelData.annotation;
                        var dictionary = $scope.microServiceAttributeDictionaryDatas;

                        if (annotation == null || annotation.length < 1) {
                            $scope.isCheck = true;
                        } else {
                            $scope.isCheck = false;
                            var annoationList = annotation.split(splitComma);
                            for (k = 0; k < annoationList.length; k++) {
                                var splitAnnotation = annoationList[k].split(splitEqual);
                                if (splitAnnotation[1].includes("matching-true")) {
                                    matching.push(splitAnnotation[0].trim());
                                }
                            }

                        }

                        if (dictionary != null && dictionary.length > 1) {
                            for (m = 0; m < dictionary.length; m += 1) {
                                var keyCompare = dictionary[m].name;
                                var valueCompare = dictionary[m].value;
                                var valueModel = dictionary[m].modelName;
                                var conpairService = serviceName;
                                if (valueModel.includes('-v')) {
                                    conpairService = service;
                                }
                                if (valueModel.localeCompare(conpairService) == 0) {
                                    console.log(valueCompare);
                                    dictionaryList.push(dictionary[m]);
                                    if (!dictionaryNameList.includes(dictionary[m].name)) {
                                        dictionaryNameList.push(dictionary[m].name)
                                    }
                                }
                            }
                        }

                        $scope.temp.policy.ruleGridData = [];

                        if ($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly) {
                            dataOrderInfo = $scope.dataOrderInfo;
                        }

                        $scope.jsonLayout($scope.dcaeJsonDate);

                    });

                    if ($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly) { // If
                        // it's
                        // veiw
                        // or
                        // edit

                        if ($scope.temp.policy.editPolicy) {
                            $scope.isInitEditTemplate = true;
                        }

                        var checkData = [];
                        var data = [];
                        // If ruleData contains extra elements created by
                        // clicked add button
                        if ($scope.temp.policy.ruleData != null) {
                            var propNames = Object.getOwnPropertyNames($scope.temp.policy.ruleData);
                            propNames.forEach(function(name) {
                                data.push(name);
                            });

                            var extraElements = data;

                            if (plainAttributeKeys != null) {
                                for (var b = 0; b < plainAttributeKeys.length; b++) { 
                                	// Remove already populated elements from data array
                                    var newValue = plainAttributeKeys[b].split("*");
                                    for (var a = 0; a < data.length; a++) {
                                        if (data[a] === newValue[0] || data[a] === (newValue[0] + "@0")) {
                                            extraElements.splice(a, 1);
                                        }
                                    }
                                }

                                // --- Populate these extra elements created
                                // by clicked add button
                                for (var a = 0; a < extraElements.length; a++) {
                                    if (extraElements[a].includes("@")) {
                                        var index = extraElements[a].lastIndexOf("@");
                                        if (index > 0) {
                                            // Get the number after @
                                            var n = getNumOfDigits(extraElements[a], index + 1);

                                            var key = extraElements[a].substring(0, index + n + 1); 
                                            // include @x in key also by n+2
											// since x can be 1,12, etc
                                            console.log("key: " + key);
                                            checkData.push(key);
                                        }
                                    }
                                }
                                var unique = checkData.filter(onlyUnique);
                                var parentLevelElements = [];
	                    		if(unique){
	                    			// --- get all root level exta elments first
									// (only contains one "@")
	                    			for(var i =0; i < unique.length; i++){
		                    			var firstIndex = unique[i].indexOf("@");
		                    			var lastIndex = unique[i].lastIndexOf("@");
		                    			if(firstIndex == lastIndex){	
			                    			var newKey = unique[i].substring(0, firstIndex);
			                    			console.log("root element: " + newKey);
			                    			parentLevelElements.push(newKey);
			                    			unique[i] = "*processed*";
		                    			}
		                    		}		                    			
	                    		}
                                // if no layout order info, keep the process
                                // as before
                                if (!dataOrderInfo) {
                                    for (var i = 0; i < unique.length; i++) {
                                        // remove @x and let addNewChoiceMS
                                        // add @1 or @2...
                                        // var newKey = unique[i].substring(0,
                                        // unique[i].length-2);
                                        var index = unique[i].lastIndexOf("@");
                                        var newKey = unique[i].substring(0, index);
                                        console.log("newKey: " + newKey);
                                        addNewChoiceMS(newKey);
                                    }
                                } else {

                                    for (var i = 0; i < $scope.labelManyKeys.length; i++) {
                                        // console.log("dataOrderInfo["+i+"]"+
                                        // dataOrderInfo[i]);
                                        var label = $scope.labelManyKeys[i];
                                        if(parentLevelElements){
		              	    		    	for (var k = 0; k < parentLevelElements.length; k++){
		              	    		    		if(label == parentLevelElements[k]){
		              	    		    			addNewChoiceMS(label);
		              	    		    		}
		              	    		    	}
		              	    		    	
		              	    		    }
                                      
                                    }

                                    // ---reset to default
                                    dataOrderInfo = [];
                                    $scope.labelManyKeys = [];

                                    // ---process none labels
                                    for (var j = 0; j < unique.length; j++) {
                                        if (unique[j] != "*processed*") {
                                            // if not created yet
                                            if (!document.getElementById(unique[j])) {
                                                var index = unique[j].lastIndexOf("@");
                                                var newKey = unique[j].substring(0, index);
                                                var newElement = document.getElementById("div."+unique[j]);
				                    			
				                    			// check weather it has been
												// created already
                                                if(newElement != null){
                                                	continue;
                                                }else{
                                                	newElement = document.getElementById(unique[j]);
                                                	if(newElement != null){
                                                		continue;
                                                	}
                                                } 
                                                // if not created yet, then
												// create it.
                                                addNewChoiceMS(newKey);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // After initially create the edit template, reset it to
                        // false.
                        $scope.isInitEditTemplate = false;


                        // if ($scope.temp.policy.editPolicy) {
                            // clean all the events of addNewChoice
                            // $scope.$on('$destroy', addNewChoiceMS);
                        // }
                    }
                    var ele = angular.element(document.getElementById("DynamicTemplate"));
                    $compile(ele.contents())($scope);
                    $scope.$apply();

                },
                error: function(data) {
                    alert("Error While Retriving the Template Layout Pattren.");
                }
            });
        }
    };


    function getNumOfDigits(str_value, index) {
        // Get the number after @
        var str = str_value.substring(index, str_value.length);
        var c = '';
        var n = 0;
        for (var x = 0; x < str.length; x++) {
            c = str.charAt(x);
            if (!isNaN(c)) {
                n++;
            } else {
                break;
            }
        }
        return n;
    }

    function getDictionary(attribute) {
        var dicName = attribute;
        if (attribute) {
            if (attribute.includes(":")) {
                dicName = attribute.split(":")[0];
            }
        }
        var dictionaryRegExp = new RegExp(dicName);
        listemunerateValues = [];
        if (dictionaryRegExp.test(dictionaryNameList)) {
            for (p = 0; p < dictionaryList.length; p += 1) {
                if (dicName == dictionaryList[p].name) {
                    listemunerateValues.push(dictionaryList[p].value);
                }
            }
        }
        return listemunerateValues;
    }

    function getList(attribute) {
        var enumName = attribute;
        console.log("In getList: attribute => " + attribute);
        if (attribute) {
            if (attribute.includes(":")) {
                enumName = attribute.split(":")[0];
            }
        }
        var baseEnum = $scope.dcaeModelData.enumValues;
        var enumList = [];
        if (baseEnum != null) {
            enumList = baseEnum.split(splitEnum);
        }
        var enumAttributes;
        var patternTest = new RegExp(enumName);
        for (k = 0; k < enumList.length; k += 1) {
            if (patternTest.test(enumList[k]) == true) {
                enumAttributes = enumList[k].trim();
            }
        }

        if (enumAttributes) {
            enumAttributes = enumAttributes.replace("[", "");
            enumAttributes = enumAttributes.replace("]", "");
            enumAttributes = enumAttributes.replace(/ /g, '');
            var dropListAfterCommaSplit = enumAttributes.split(splitEqual);
            listemunerateValues = dropListAfterCommaSplit[1].split(splitComma);
            // enumKeyList.push(attribute);
            return listemunerateValues;
        }

        return [];
    }

    function getBooleanList() {
        var booleanList = [];
        booleanList.push(true);
        booleanList.push(false);
        return booleanList;
    }


    function isArray(arrayTest) {
        return Object.prototype.toString.call(arrayTest) === '[object Array]';
    }
    var lableList = [];

    $scope.layOutElementList = [];
    $scope.layOutOnlyLableList = [];

    var elementOrderNum = 0;

    function deconstructJSON(layOutData, level, name) {

        var array = false;
        var label = level;
        var stringValue = "java.lang.String";
        var string = "string";
        var intValue = "int";
        var integerValue = "integer";
        var double = "double";
        var boolean = "boolean";
        var baseLevel = level;
        var list = "list";
        var String = "String";

        var attributekey = "";

        if (name.length > 1) {
            label = label + name + '.';
        }

        for (key in layOutData) {
            array = isArray(layOutData[key]);
            console.log("key: " + key, "value: " + layOutData[key]);

            if (!!layOutData[key] && typeof(layOutData[key]) == "object") {

                if (array == false && key !== "0") {

                    if ($scope.dataOrderInfo) {
                        var labelObject = {
                            "label": key,
                            "level": label,
                            "array": array
                        };
                        // save it to the list
                        $scope.layOutOnlyLableList.push(labelObject);

                    } else {
                        // call label layout
                        $scope.labelLayout(label, key, array);
                    }

                }

                if (array == true && key != 0) {
                    lableList.push(key);
                }

                if (lableList.length > 0) {
                    array = true;
                }
                if (key === "0") {
                    var newKey = lableList.pop();

                    if ($scope.dataOrderInfo) {

                        var labelObject = {
                            "label": newKey,
                            "level": baseLevel,
                            "array": array
                        };
                        // save it to the list
                        $scope.layOutOnlyLableList.push(labelObject);

                    } else {
                        // call label layout
                        $scope.labelLayout(baseLevel, newKey, array);
                    }

                    if (array) {
                        label = baseLevel + newKey + '@0.';
                    } else {
                        label = baseLevel + newKey + '.';
                    }
                }
                deconstructJSON(layOutData[key], label, key);
            } else {
                var attirbuteLabel = label;
                var defaultValue = '';
                var isRequired = false;
                if (layOutData[key].includes('defaultValue-')) {
                    defaultValue = layOutData[key].split('defaultValue-')[1];
                }

                if (key === "0") {
                    array = true;
                    attributekey = lableList.pop();
                    attirbuteLabel = baseLevel;
                } else {
                    attributekey = key.split();
                }

                if (layOutData[key].includes('required-true')) {
                    isRequired = true;
                }

                var subAttributes = $scope.dcaeModelData.sub_attributes;

                if (subAttributes) {
                    var jsonObject = JSON.parse(subAttributes);
                    var lablInfo = findVal(jsonObject, attributekey);
	    			console.log("deconstructJSON:findValue : " + attributekey +": "+ lablInfo);
		        	if (lablInfo){
		        		if(lablInfo.includes('required-true')){
		        			isRequired = true;
		        		}	
		        	}else{	
		        		var allkeys = Object.keys(jsonObject);
		        		if (allkeys) {
		        			for (var k = 0; k < allkeys.length; k++) {
		        				var keyValue = allkeys[k];
		        				console.log(" keyValue:jsonObject[" + keyValue + "]: " + jsonObject[keyValue]);
		        				if (jsonObject[keyValue]) {
		        					var tempObject = jsonObject[keyValue];
		        					if (tempObject && tempObject[key]) {
		        						if (tempObject[key].includes('required-true')) {
		        							isRequired = true;
		        						}
		        					}
		        				}
		        			}
		        		}
		        	}
                }

                var elementObject = {};
                switch (layOutData[key].split(splitcolon)[0]) {

                    case stringValue:
                    case string:
                        if ($scope.dataOrderInfo) {
                            elementOrderNum++;
                            elementObject = {
                                "id": elementOrderNum,
                                "attributekey": attributekey,
                                "array": array,
                                "attirbuteLabel": attirbuteLabel,
                                "defaultValue": defaultValue,
                                "isRequired": isRequired,
                                "type": "text"
                            };
                            $scope.layOutElementList.push(elementObject);
                        } else {
                            $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "text");
                        }
                        break;
                    case intValue:
                    case integerValue:
                        if ($scope.dataOrderInfo) {
                            elementOrderNum++;
                            elementObject = {
                                "id": elementOrderNum,
                                "attributekey": attributekey,
                                "array": array,
                                "attirbuteLabel": attirbuteLabel,
                                "defaultValue": defaultValue,
                                "isRequired": isRequired,
                                "type": "number"
                            };
                            $scope.layOutElementList.push(elementObject);
                        } else {
                            $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "number");
                        }
                        break;
                    case double:
                        if ($scope.dataOrderInfo) {
                            elementOrderNum++;
                            elementObject = {
                                "id": elementOrderNum,
                                "attributekey": attributekey,
                                "array": array,
                                "attirbuteLabel": attirbuteLabel,
                                "defaultValue": defaultValue,
                                "isRequired": isRequired,
                                "type": "double"
                            };
                            $scope.layOutElementList.push(elementObject);
                        } else {
                            $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "double");
                        }
                        break;
                    case boolean:
                        if ($scope.dataOrderInfo) {
                            elementOrderNum++;
                            elementObject = {
                                "id": elementOrderNum,
                                "attributekey": attributekey,
                                "array": array,
                                "attirbuteLabel": attirbuteLabel,
                                "defaultValue": layOutData[key],
                                "list": getBooleanList,
                                "isRequired": isRequired,
                                "type": "dropBox"
                            };
                            $scope.layOutElementList.push(elementObject);
                        } else {
                            $scope.dropBoxLayout(attirbuteLabel, attributekey, array, layOutData[key], getBooleanList());
                        }
                        break;
                    default:
                        if (layOutData[key].includes('dictionary-')) {
                            var list = getDictionary(layOutData[key].split('dictionary-')[1]);
                        } else {
                            // --- get dropdown values from enumValues
                            var list = getList(layOutData[key]);
                        }
                        if (list.length === 0) { // not dropdown element
                            if ($scope.dataOrderInfo) {
                                elementOrderNum++;
                                elementObject = {
                                    "id": elementOrderNum,
                                    "attributekey": attributekey,
                                    "array": array,
                                    "attirbuteLabel": attirbuteLabel,
                                    "defaultValue": defaultValue,
                                    "isRequired": isRequired,
                                    "type": "text"
                                };
                                $scope.layOutElementList.push(elementObject);

                            } else {
                                $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "text");
                            }
                        } else {
                            if ($scope.dataOrderInfo) {
                                elementOrderNum++;
                                elementObject = {
                                    "id": elementOrderNum,
                                    "attributekey": attributekey,
                                    "array": array,
                                    "attirbuteLabel": attirbuteLabel,
                                    "defaultValue": layOutData[key],
                                    "isRequired": isRequired,
                                    "list": list,
                                    "type": "dropBox"
                                };
                                $scope.layOutElementList.push(elementObject);
                            } else {
                                $scope.dropBoxLayout(attirbuteLabel, attributekey, array, layOutData[key], list, isRequired);
                            }
                        }
                        break;
                }
            }
        }
    }


    $scope.validContionalRequired = function(parentId) {
        console.log("ng-blur event: parentId : " + parentId);
        var c = document.getElementById(parentId).children;
        var i;
        var hasValue = false;
        for (i = 0; i < c.length; i++) {
            if (c[i].getAttribute("data-conditional")) {
                console.log(c[i].getAttribute("data-conditional"));
                console.log(c[i].value);
                if (c[i].value != null && c[i].value.trim() != "") {
                    hasValue = true;
                }
            }
        }

        for (i = 0; i < c.length; i++) {
            if (c[i].getAttribute("data-conditional")) {
                if (hasValue) {
                    c[i].setAttribute("required", true);
                } else {
                    c[i].removeAttribute("required");
                }
            }
        }
    }

    $scope.jsonLayout = function(layOutData) {

        deconstructJSON(layOutData, "", "");

        var orderValue = $scope.dataOrderInfo;
        var layOutElementList = $scope.layOutElementList;
        var labelList = $scope.layOutOnlyLableList;

        // reset to default
        elementOrderNum = 0;
        $scope.layOutElementList = [];
        $scope.layOutOnlyLableList = [];

        // Only layout in order if order info provided
        if (orderValue) {

            if (orderValue.includes("[")) {
                orderValue = orderValue.replace("[", "");
                orderValue = orderValue.replace("]", "");
            }

            orderValue = orderValue.split(',');

            for (var i = 0; i < orderValue.length; i++) {
                console.log("orderValue[" + i + "]" + orderValue[i]);
                var key = orderValue[i].trim();

                // --- Create labels first {"label" : newKey, "level" :
                // baseLevel, "array" : array};
                if (labelList) {
                    for (var k = 0; k < labelList.length; k++) {

                        var label = labelList[k].label.toString().trim();
                        var level = labelList[k].level.toString().trim();
                        var array = labelList[k].array;

                        if (key == label) {
                            $scope.labelLayout(level, label, array);
                            // in case to have duplicate label names
                            labelList[k].label = "*processed*";
                            break;
                        }
                    }
                }
                // --- then layout each element based on its order
                // defined in YAML file
                for (var j = 0; j < layOutElementList.length; j++) {

                    var attributekey = layOutElementList[j].attributekey.toString().trim();

                    if (key == attributekey) {

                        var attirbuteLabel = layOutElementList[j].attirbuteLabel.toString().trim();
                        var defaultValue = layOutElementList[j].defaultValue.toString().trim();
                        var isRequired = layOutElementList[j].isRequired;

                        console.log("layOutElementList[" + j + "]: id:" + layOutElementList[j].id + ", attributekey:" + layOutElementList[j].attributekey + ", attirbuteLabel:" + layOutElementList[j].attirbuteLabel);
                        console.log("layOutElementList[" +j+ "]: type:" + layOutElementList[j].type);
                        
                        if (layOutElementList[j].type == "dropBox") {
                            $scope.dropBoxLayout(attirbuteLabel, attributekey, layOutElementList[j].array, defaultValue, layOutElementList[j].list, isRequired);

                        } else {
                            $scope.attributeBox(attributekey, layOutElementList[j].array, attirbuteLabel, defaultValue, isRequired, layOutElementList[j].type);

                        }

                        // in case to have duplicate attribute names
                        layOutElementList[j].attributekey = "*processed*";
                        break;
                    }
                }
            }
        }
    }


    $scope.attributeBox = function(attibuteKey, attributeManyKey, labelValue, defaultValue, isRequired, dataType) {
        $scope.temp.policy.ruleGridData.push(attibuteKey);
        var br = document.createElement("BR");

        var label = document.createElement("Label");
        var labeltext = null;
        var requiredLabName = "";
        if (matching.includes(attibuteKey)) {
            labeltext = document.createTextNode(attibuteKey + "*!");
            isRequired = true; // set required as true for matching element
        } else {
            if (isRequired) {
                requiredLabName = attibuteKey + " * ";
                labeltext = document.createTextNode(requiredLabName);
            } else {
                labeltext = document.createTextNode(attibuteKey);
            }
        }


        var divID = labelValue;

        if (labelValue.length < 1) {
            divID = "DynamicTemplate";
        } else if (labelValue.endsWith('.')) {
            var divID = 'div.' + labelValue.substring(0, labelValue.length - 1);
        }

        label.appendChild(labeltext);

        var textField = document.createElement("INPUT");

        textField.setAttribute("class", "form-control");
        if (dataType) {
            if (dataType == "double") {
                textField.setAttribute("type", "number");
                textField.setAttribute("step", "any");
            } else {
                textField.setAttribute("type", dataType);
            }
        }
        textField.setAttribute("style", "width:300px;");
        textField.setAttribute("ng-disabled", "temp.policy.readOnly");
        var checkKey;
        var id = "";
        if (attributeManyKey) {
            checkKey = labelValue + attibuteKey + '@0';
            textField.setAttribute("id", '' + labelValue + attibuteKey + '@0' + '');
            var divTag = document.createElement("div");
            divTag.setAttribute("id", "div." + labelValue + attibuteKey);
            var addButton = document.createElement("BUTTON");
            var buttonaddLabel = document.createTextNode("+");
            addButton.appendChild(buttonaddLabel);
            addButton.setAttribute("id", labelValue + attibuteKey);
            addButton.setAttribute("class", "btn btn-add-remove");
            addButton.setAttribute("onclick" ,  'addNewChoiceMS("'+labelValue + attibuteKey+'");');
            addButton.setAttribute("ng-disabled", "temp.policy.readOnly");
            var removeButton = document.createElement("BUTTON");
            var buttonremoveLabel = document.createTextNode("-");
            removeButton.appendChild(buttonremoveLabel);
            removeButton.setAttribute("class", "btn btn-add-remove");
            removeButton.setAttribute("onclick" ,  'removeChoiceMS("'+labelValue + attibuteKey+'");');
            removeButton.setAttribute("ng-disabled", "temp.policy.readOnly");
            document.getElementById(divID).appendChild(addButton);
            document.getElementById(divID).appendChild(removeButton);
            document.getElementById(divID).appendChild(label);
            id = "div." + labelValue + attibuteKey;
            // var divTag = document.createElement("div");
            divTag.setAttribute("id", id);
            document.getElementById(divID).appendChild(divTag);
            textField.className += ' first_child';
            if (isRequired) {
                textField.setAttribute("required", "true");
            }
            divTag.appendChild(textField);
            document.getElementById(divID).appendChild(divTag);

        } else {
            checkKey = labelValue + attibuteKey;
            textField.setAttribute("id", '' + labelValue + attibuteKey + '');
            if (document.getElementById(divID).hasAttribute('required') || !document.getElementById(divID).hasAttribute('data-conditional')) {
                if (requiredLabName.includes("*")) {
                    textField.setAttribute("required", "true");
                }
            } else if (document.getElementById(divID).hasAttribute('data-conditional')) {
                if (requiredLabName.includes("*")) {
                    var requiredNode = document.createElement('span');
                    requiredNode.setAttribute("class", "mstooltip");
                    requiredNode.textContent = "?";
                    label.appendChild(requiredNode);

                    var requiredNodeToolTip = document.createElement('span');
                    requiredNodeToolTip.setAttribute("class", "tooltiptext");
                    requiredNodeToolTip.textContent = "Conditional Required";
                    requiredNode.appendChild(requiredNodeToolTip);

                    textField.setAttribute("data-conditional", divID);
                    textField.setAttribute("ng-blur", "validContionalRequired('" + divID + "')");
                }
            }

            document.getElementById(divID).appendChild(label);
            document.getElementById(divID).appendChild(textField);
            document.getElementById(divID).appendChild(br);

        }

        if (divID.includes("@0") && divID.includes("div.")) {
            var firstChild_Id = divID.split("@0")[0];
            var firstChild_element = document.getElementById(firstChild_Id);
            if (firstChild_element) {
            	if(!firstChild_element.className.includes('children_group border')){
					layer++;
					if(layer > 4){ 
						layer = 1
					};
					// here is div with group of children
					firstChild_element.className += ' children_group border' + layer; 
				}
            }
        }
        console.log('firstChild_Id: ' + firstChild_Id);
        console.log('divID: ' + divID);

        if (defaultValue.length > 0) {
            if (defaultValue.includes(":")) {
                defaultValue = defaultValue.split(":")[0];
                if (defaultValue === "NA") {
                    defaultValue = "";
                }
            }
            if (defaultValue != "undefined" && defaultValue != undefined && defaultValue != "null") {
                document.getElementById(checkKey).value = defaultValue;
            }
        }

        if ($scope.temp.policy.ruleData != null) {
            // document.getElementById(checkKey).value =
            // $scope.temp.policy.ruleData[checkKey];
            if (attributeManyKey) {
                var newCheckKey = checkKey.replace(attibuteKey + '@0', attibuteKey);
                if ($scope.temp.policy.ruleData[newCheckKey + '@0'] != undefined && $scope.temp.policy.ruleData[newCheckKey + '@0'] != "undefined") {
                    document.getElementById(newCheckKey + '@0').value = $scope.temp.policy.ruleData[newCheckKey + '@0'];
                }
            } else {
                if ($scope.temp.policy.ruleData[checkKey] != undefined && $scope.temp.policy.ruleData[checkKey] != "undefined") {
                    document.getElementById(checkKey).value = $scope.temp.policy.ruleData[checkKey];
                }
            }
        }
        plainAttributeKeys.push(labelValue + attibuteKey + '*' + attributeManyKey);
    };



    $scope.labelManyKeys = [];
    $scope.labelManyKeys = [];
    $scope.labelLayout = function(labelValue, lableName, labelManyKey ){
		var label = document.createElement("Label")
		var divID = labelValue;
		if (labelValue.endsWith('.')){
			var workingLabel = labelValue.substring(0, labelValue.length-1);
		}else {
			var workingLabel = labelValue;
		}
		
		if (labelValue.length  < 1){
			divID = "DynamicTemplate";
		} else if (labelValue.endsWith('.')){
			var divID = 'div.'+ labelValue.substring(0, labelValue.length-1);
		}
		
		var subAttributes = $scope.dcaeModelData.sub_attributes;
	    var jsonObject = JSON.parse(subAttributes);	
	    var lablInfo = findVal(jsonObject, lableName);
		console.log("findValue : " + lableName +": "+ lablInfo);
		var star = "";
		var required = null;
		if(lablInfo){
			if(typeof lablInfo === 'string' ){
			  if(lablInfo.includes("required-true")){
			     	star = " *";
				    required = true;
			  }else if (lablInfo.includes("required-false")){
				required = false
			  }
		    }
		}
		
		var labeltext = document.createTextNode(lableName + star);
	
		label.appendChild(labeltext);

		if(labelManyKey){
			var addButton = document.createElement("BUTTON");
			var buttonLabel = document.createTextNode("+");       
			addButton.appendChild(buttonLabel); 
			addButton.setAttribute("class", "btn btn-add-remove");
			addButton.setAttribute("onclick" ,  'addNewChoiceMS("'+labelValue + lableName+'");');
			addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			var removeButton = document.createElement("BUTTON");
			var buttonremoveLabel = document.createTextNode("-");       
			removeButton.appendChild(buttonremoveLabel); 
			removeButton.setAttribute("class", "btn btn-add-remove");
			removeButton.setAttribute("onclick" ,  'removeChoiceMS("'+labelValue +lableName+'");');
			removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly"); 
			document.getElementById(divID).appendChild(addButton); 
			document.getElementById(divID).appendChild(removeButton);
			document.getElementById(divID).appendChild(label);
			var id = "div."+labelValue+lableName;
			var divTag = document.createElement("div");
			divTag.setAttribute("id", id); 
			document.getElementById(divID).appendChild(divTag);
			
			var divTag = document.createElement("div");
			divTag.setAttribute("id", id +'@0');  
			
			if(!divTag.className.includes('children_group border')){
				layer++;
				if(layer > 4){ 
					layer = 1
				};
				// here is div with group of children.
				divTag.className += ' children_group border' + layer; 
			}
			
			if(required){
			   divTag.setAttribute("required", required);  
			}else if(required == false){
			   divTag.setAttribute("data-conditional", "yes");  
			}
			
			document.getElementById(id).appendChild(divTag);
			
			$scope.labelManyKeys.push(lableName);
			
		}else{
			var divTag = document.createElement("div");
			divTag.setAttribute("id", "div."+labelValue+lableName);
			if(!divTag.className.includes('children_group border')){
				layer++;
				if(layer > 4){ 
					layer = 1
				};
				// here is div with group of children.
				divTag.className += ' children_group border' + layer; 
			}
			if(required){
			    divTag.setAttribute("required", required);  
			}else if(required == false){
				divTag.setAttribute("data-conditional", "yes");  
			}  
			document.getElementById(divID).appendChild(label);  
			document.getElementById(divID).appendChild(divTag);			
		}
    };

    $scope.dropBoxLayout = function(labelLevel, attributeName, many , refValue, listemunerateValues, isRequired){
			var br = document.createElement("BR");
		
			if (labelLevel.length  < 1){
					var divID = "DynamicTemplate";
			} else if (labelLevel.endsWith('.')){
					var divID = 'div.'+ labelLevel.substring(0, labelLevel.length-1);
			}	
		
		
			var label = document.createElement("Label")
			
			var refAttributes = $scope.dcaeModelData.ref_attributes;
			if(isRequired != true && refAttributes){ // check refAttributes
														// also
			   		var refAttributesList = refAttributes.split(splitComma);
			   		for (k = 0; k < refAttributesList.length; k++){
			       		var refAttribute = refAttributesList[k].split(splitEqual);	       		
			       		if (attributeName == refAttribute[0].trim() && refAttribute[1].includes("required-true")){
			       			isRequired = true;
			       		}
			   		}
			}
			
			if (matching.includes(attributeName)){
				var labeltext = document.createTextNode(attributeName + "*!");
				label.appendChild(labeltext);
				isRequired = true;  // set required as true for matching element
			}else {
				var labeltext = document.createTextNode(attributeName);		
				if(isRequired){
				    var requiredLabName = attributeName+ " * ";
					labeltext = document.createTextNode(requiredLabName);
				}else{
				    labeltext = document.createTextNode(attributeName);	
				}
			
			    label.appendChild(labeltext);		
			}
			label.appendChild(labeltext);
			// if this field is required, but its parent is not required
			if(isRequired && document.getElementById(divID).hasAttribute('data-conditional')){
			   	var requiredNode = document.createElement('span');
				requiredNode.setAttribute("class", "mstooltip");
				requiredNode.textContent = "?";
				label.appendChild(requiredNode);
					
				var requiredNodeToolTip = document.createElement('span');
				requiredNodeToolTip.setAttribute("class", "tooltiptext");
				requiredNodeToolTip.textContent = "Conditional Required";
				requiredNode.appendChild(requiredNodeToolTip);

			}
		
			var listField = document.createElement("SELECT");
			listField.setAttribute("class" , "form-control");
			listField.setAttribute("style" , "width:300px;");
			listField.setAttribute("ng-disabled" , "temp.policy.readOnly");
			
			if(isRequired){
			    if(document.getElementById(divID).hasAttribute('data-conditional')){
			    	listField.setAttribute("data-conditional", divID);
			    	listField.setAttribute("ng-blur", "validContionalRequired('"+divID+"')");
			    }else{
					listField.setAttribute("required", true);
			    }
			}
			if( many != true || isRequired != true){ // add an empty option
														// for not required or
														// not multiple select
														// element
				var optionFirst = document.createElement('option');
				optionFirst.setAttribute('value', "");
				listField.appendChild(optionFirst);	
			}
			
			for (i=0; i < listemunerateValues.length; i += 1) {
			    option = document.createElement('option');
			    option.setAttribute('value', listemunerateValues[i]);
			    option.appendChild(document.createTextNode(listemunerateValues[i]));
			    option.setAttribute('value', listemunerateValues[i]);
			    listField.appendChild(option);
			}
			listField.setAttribute("id" , ''+ labelLevel + attributeName + '');
			
			enumKeyList.push(attributeName);
			
			document.getElementById(divID).appendChild(label);  
			document.getElementById(divID).appendChild(br);	
					
			if(many == true){
				document.getElementById(divID).appendChild(listField).multiple = true;
				plainAttributeKeys.push(labelLevel + attributeName+'*'+true);
			}else {
				document.getElementById(divID).appendChild(listField).multiple = false;
				plainAttributeKeys.push(labelLevel + attributeName+'*'+false);
			}
		
			if($scope.temp.policy.ruleData != null){
				if (many == true){
					document.getElementById(labelLevel +attributeName).options[0].selected = false;
					for (i=0; i < listemunerateValues.length; i += 1) {
						var testValue = $scope.temp.policy.ruleData[labelLevel +attributeName+'@' + i];
						if (testValue === undefined){
							testValue = $scope.temp.policy.ruleData[labelLevel +attributeName];
							}
						var location = listemunerateValues.indexOf(testValue);
						if (location!=-1){
							document.getElementById(labelLevel +attributeName).options[location].selected = true;
							}
						}			
					}else {
						    if($scope.temp.policy.ruleData[labelLevel + attributeName] != undefined && $scope.temp.policy.ruleData[labelLevel + attributeName] != "undefined"){
			                    document.getElementById(labelLevel + attributeName).value = $scope.temp.policy.ruleData[labelLevel + attributeName];	
						    }
					}
				}
		    };

    function onlyUnique(value, index, self) {
        return self.indexOf(value) === index;
    };


    function checkDictionary(value) {
        for (i = 0; i < $scope.microServiceAttributeDictionaryDatas.length; i++) {
            if ($scope.microServiceAttributeDictionaryDatas[i].name.localeCompare(value)) {
                return true;
            }
        }

    }

    $scope.saveDecisionPolicy = function(policy) {
        if (policy.itemContent != undefined) {
            $scope.refreshCheck = true;
            $scope.policyNavigator = policy.itemContent;
            policy.itemContent = "";
        }
        $scope.savebutton = false;
        console.log(policy);
        if (policy.policy.ruleProvider == "MicroService_Model") {
            var splitAt = '*';
            var dot = '.';
            var jsonPolicy = {};
            if (plainAttributeKeys != null) {
                for (a = 0; a < plainAttributeKeys.length; a++) {
                    var splitPlainAttributeKey = plainAttributeKeys[a].split(splitAt);
                    console.log("splitPlainAttributeKey: " + splitPlainAttributeKey);
                    var searchElement = document.getElementById(splitPlainAttributeKey[0]);
                    var key = splitPlainAttributeKey[0];
                    if (searchElement == null) {
                        searchElement = document.getElementById(splitPlainAttributeKey[0] + '@0');
                        key = splitPlainAttributeKey[0] + '@0';
                    } else if (searchElement.nodeName == 'BUTTON') {
                        searchElement = document.getElementById(splitPlainAttributeKey[0] + '@0');
                        key = splitPlainAttributeKey[0] + '@0';
                    }
                    if (searchElement != null) {
                        var keySplit = key.split(dot);
                        var elumentLocation = keySplit.length;
                        var enumKey = key;
                        if (elumentLocation > 1) {
                            enumKey = keySplit[keySplit.length - 1];
                        }
                        // check it is undefined or not
                        if (enumKeyList != undefined && enumKeyList.indexOf(enumKey) != -1) {
                            if (splitPlainAttributeKey[1] != undefined && splitPlainAttributeKey[1].indexOf("true") !== -1) {
                                var multiSlect = [];
                                for (var i = 0; i < searchElement.selectedOptions.length; i++) {
                                    multiSlect.push(searchElement.selectedOptions[i].value);
                                }
                                jsonPolicy[key] = multiSlect;
                            } else {
                                console.log(" searchElement.value = > " + searchElement.value);
                                jsonPolicy[key] = searchElement.value;
                            }
                        } else {
                            if (searchElement.value != null) {
                                console.log(" searchElement.value = > " + searchElement.value);
                                jsonPolicy[key] = searchElement.value;
                            }
                        }
                    }
                }
            }
            var postData = {
                policyData: policy,
                policyJSON: jsonPolicy
            };
        } else {
            var postData = {
                policyData: policy
            };
        }
        var uuu = "policycreation/save_policy";

        $.ajax({
            type: 'POST',
            url: uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success: function(data) {
                $scope.$apply(function() {
                    $scope.data = data.policyData;
                    if ($scope.data == 'success') {
                        $scope.temp.policy.readOnly = 'true';
                        Notification.success("Policy Saved Successfully.");
                    } else if ($scope.data == 'PolicyExists') {
                        $scope.savebutton = true;
                        Notification.error("Policy Already Exists with Same Name in Scope.");
                    }
                });
                console.log($scope.data);

            },
            error: function(data) {
                Notification.error("Error Occured while saving Policy.");
            }
        });
    };

    $scope.validatePolicy = function(policy) {
        console.log(policy);
        document.getElementById("validate").innerHTML = "";
        if (policy.ruleProvider == "MicroService_Model") {
            var splitAt = '*';

            var dot = '.';
            var jsonPolicy = {};
            if (plainAttributeKeys != null) {
                for (a = 0; a < plainAttributeKeys.length; a++) {
                    var splitPlainAttributeKey = plainAttributeKeys[a].split(splitAt);
                    console.log(splitPlainAttributeKey[1]);
                    var searchElement = document.getElementById(splitPlainAttributeKey[0]);
                    var key = splitPlainAttributeKey[0];
                    if (searchElement == null) {
                        searchElement = document.getElementById(splitPlainAttributeKey[0] + '@0');
                        key = splitPlainAttributeKey[0] + '@0';
                    } else if (searchElement.nodeName == 'BUTTON') {
                        searchElement = document.getElementById(splitPlainAttributeKey[0] + '@0');
                        key = splitPlainAttributeKey[0] + '@0';
                    }
                    if (searchElement != null) {
                        if (enumKeyList.indexOf(key) != -1) {
                            if (splitPlainAttributeKey[1].indexOf("true") !== -1) {
                                var multiSlect = [];
                                for (var i = 0; i < searchElement.selectedOptions.length; i++) {
                                    multiSlect.push(searchElement.selectedOptions[i].value);
                                }
                                jsonPolicy[key] = multiSlect;
                            } else {
                                jsonPolicy[key] = searchElement.value;
                            }
                            if (searchElement.getAttribute("required")) {
                                if (!searchElement.value) {
                                    return;
                                }
                            }
                        } else {
                            if (searchElement.value != null) {
                                jsonPolicy[key] = searchElement.value;
                                if (searchElement.getAttribute("required")) {
                                    if (!searchElement.value) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            var postData = {
                policyData: policy,
                policyJSON: jsonPolicy
            };
        } else {
            var postData = {
                policyData: policy
            };
        }
        var uuu = "policyController/validate_policy.htm";
        $.ajax({
            type: 'POST',
            url: uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success: function(data) {
                $scope.$apply(function() {
                    $scope.validateData = data.data.replace(/\"/g, "");
                    $scope.data = data.data;
                    if ($scope.data == '"success"') {
                        Notification.success("Validation Success.");
                        $scope.savebutton = false;
                    } else {
                        Notification.error("Validation Failed.");
                        document.getElementById("validate").innerHTML = $scope.validateData;
                        document.getElementById("validate").style.color = "white";
                        document.getElementById("validate").style.backgroundColor = "red";
                        $scope.savebutton = true;
                    }

                });
                console.log($scope.data);
            },
            error: function(data) {
                Notification.error("Validation Failed.");
                $scope.savebutton = true;
            }
        });
    };
   
    if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    	$scope.temp.policy.attributes = [];
    	$scope.temp.policy.settings = [];
    	$scope.temp.policy.ruleAlgorithmschoices = [];
    	if(!$scope.temp.policy.yamlparams){
    		$scope.temp.policy.yamlparams = {};
    	}
    	if(!$scope.temp.policy.yamlparams.targets){
    		$scope.temp.policy.yamlparams.targets = [];
    	}
    	if(!$scope.temp.policy.yamlparams.blackList){
    		$scope.temp.policy.yamlparams.blackList = [];
    	}
    	if(!$scope.temp.policy.rainyday){
    		$scope.temp.policy.rainyday = {};
    	}
    	if(!$scope.temp.policy.rainyday.treatmentTableChoices){
    		$scope.temp.policy.rainyday.treatmentTableChoices = [];
    	}
    
    }else if($scope.temp.policy.ruleProvider=="Custom"){
	   if($scope.temp.policy.attributes.length == 0){
		   $scope.temp.policy.attributes = [];    
	   }
	   if($scope.temp.policy.settings.length == 0){
		   $scope.temp.policy.settings = [];
	   }
	   if($scope.temp.policy.ruleAlgorithmschoices == null || $scope.temp.policy.ruleAlgorithmschoices.length == 0){
		   $scope.temp.policy.ruleAlgorithmschoices = [];
	   }
    }else if($scope.temp.policy.ruleProvider=="GUARD_BL_YAML"){
	   if($scope.temp.policy.yamlparams.blackList == null || $scope.temp.policy.yamlparams.blackList.length==0){
		   $scope.temp.policy.yamlparams.blackList = [];
	   }
	   if($scope.temp.policy.blackListEntries == null || $scope.temp.policy.blackListEntries.length==0){
		   $scope.temp.policy.blackListEntries = [];
	   }
	   $scope.blackListEntries = [];
	   $scope.temp.policy.appendBlackListEntries = [];
	   $scope.blackListEntries = arrayUnique($scope.temp.policy.blackListEntries.concat($scope.temp.policy.yamlparams.blackList));
    }else if($scope.temp.policy.ruleProvider=="GUARD_YAML"){
    	if($scope.temp.policy.yamlparams.targets.length==0){
 		   $scope.temp.policy.yamlparams.targets = [];
 	   	}
    }else if($scope.temp.policy.ruleProvider=="Rainy_Day"){
    	if($scope.temp.policy.rainyday.treatmentTableChoices == null || $scope.temp.policy.rainyday.treatmentTableChoices.length == 0){
    		$scope.temp.policy.rainyday.treatmentTableChoices = [];
    	}
    }
    
    $scope.attributeDatas = [{
        "attributes": $scope.temp.policy.attributes
    }];
    $scope.addNewChoice = function() {
        var newItemNo = $scope.temp.policy.attributes.length + 1;
        $scope.temp.policy.attributes.push({
            'id': 'choice' + newItemNo
        });
    };
    $scope.removeChoice = function() {
        var lastItem = $scope.temp.policy.attributes.length - 1;
        $scope.temp.policy.attributes.splice(lastItem);
    };

    $scope.settingsDatas = [{
        "settings": $scope.temp.policy.settings
    }];
    $scope.addNewSettingsChoice = function() {
        var newItemNo = $scope.temp.policy.settings.length + 1;
        $scope.temp.policy.settings.push({
            'id': 'choice' + newItemNo
        });
    };
    $scope.removeSettingsChoice = function() {
        var lastItem = $scope.temp.policy.settings.length - 1;
        $scope.temp.policy.settings.splice(lastItem);
    };

    $scope.addNewTarget = function() {
        $scope.temp.policy.yamlparams.targets.push('');
    };
    $scope.removeTarget = function() {
        var lastItem = $scope.temp.policy.yamlparams.targets.length - 1;
        $scope.temp.policy.yamlparams.targets.splice(lastItem);
    };

    $scope.addNewBL = function() {
        $scope.temp.policy.yamlparams.blackList.push('');
    };

    $scope.removeBL = function(id) {
    	$scope.temp.policy.yamlparams.blackList = $scope.temp.policy.yamlparams.blackList.filter(function (obj){
			return obj !== id;
		});
    };

    if (typeof $scope.temp.policy.rainyday != 'undefined' && $scope.temp.policy.rainyday &&
        typeof $scope.temp.policy.rainyday.treatmentTableChoices != 'undefined') {
        $scope.treatmentDatas = [{
            "treatmentValues": $scope.temp.policy.rainyday.treatmentTableChoices
        }];
    }

    $scope.addNewTreatment = function() {
        $scope.temp.policy.rainyday.treatmentTableChoices.push({});
    };
    $scope.removeTreatment = function() {
        var lastItem = $scope.temp.policy.rainyday.treatmentTableChoices.length - 1;
        $scope.temp.policy.rainyday.treatmentTableChoices.splice(lastItem);
    };

    $scope.workstepDictionaryDatas = [];
    $scope.getWorkstepValues = function(bbidValue) {
        for (var i = 0; i < $scope.rainyDayDictionaryDataEntity.length; ++i) {
            var obj = $scope.rainyDayDictionaryDataEntity[i];
            if (obj.bbid == bbidValue) {
                $scope.workstepDictionaryDatas.push(obj.workstep);
            }
        }
    };

    $scope.allowedTreatmentsDatas = [];
    $scope.getTreatmentValues = function(bbidValue, workstepValue) {
        for (var i = 0; i < $scope.rainyDayDictionaryDataEntity.length; ++i) {
            var obj = $scope.rainyDayDictionaryDataEntity[i];
            if (obj.bbid == bbidValue && obj.workstep == workstepValue) {
                var splitAlarm = obj.treatments.split(',');
                for (var j = 0; j < splitAlarm.length; ++j) {
                    $scope.allowedTreatmentsDatas.push(splitAlarm[j]);
                }
            }
        }
    };

    $scope.ItemNo = 0;
    $scope.ruleAlgorithmDatas = [{
        "ruleAlgorithms": $scope.temp.policy.ruleAlgorithmschoices
    }];

    $scope.addNewRuleAlgorithm = function() {
        if ($scope.temp.policy.ruleAlgorithmschoices != null) {
            var newItemNo = $scope.temp.policy.ruleAlgorithmschoices.length + 1;
        } else {
            var newItemNo = 1;
        }
        if (newItemNo > 1) {
            var value = newItemNo - 1;
            $scope.attributeDictionaryDatas.push('A' + value);
        }
        $scope.temp.policy.ruleAlgorithmschoices.push({
            'id': 'A' + newItemNo
        });
    };

    $scope.removeRuleAlgorithm = function() {
        var lastItem = $scope.temp.policy.ruleAlgorithmschoices.length - 1;
        $scope.temp.policy.ruleAlgorithmschoices.splice(lastItem);
    };

    $scope.providerListener = function(ruleProvider) {
        if (ruleProvider != "Custom") {
            $scope.temp.policy.ruleAlgorithmschoices = [];
            $scope.temp.policy.settings = [];
            $scope.temp.policy.attributes = [];
        }
        if (ruleProvider === "Raw") {
        	$scope.notRawPolicy = false;
        }
    };
    
    $scope.importButton = true;
    var fd;
	$scope.uploadBLFile = function(files) {
		fd = new FormData();
		fd.append("file", files[0]);
		var fileExtension = files[0].name.split(".")[1];
		if(fileExtension == "xls"){
			$scope.importButton = false;
			$scope.$apply();
		}else{
			Notification.error("Upload the BlackList file which extends with .xls format.");
		}
	};
	
	function arrayUnique(array) {
	    var a = array.concat();
	    for(var i=0; i<a.length; ++i) {
	        for(var j=i+1; j<a.length; ++j) {
	            if(a[i] === a[j])
	                a.splice(j--, 1);
	        }
	    }
	    return a;
	}
	
	$scope.submitUpload = function(){
		$http.post("policycreation/importBlackListForDecisionPolicy", fd,  {
			withCredentials: false,
			headers: {'Content-Type': undefined},
			transformRequest: angular.identity
		}).success(function(data){
			$scope.data = JSON.parse(data.data);
			$scope.temp.policy.blackListEntries = JSON.parse($scope.data.blackListEntries);
			if($scope.temp.policy.blackListEntries[0] !== "error"){
				$scope.blackListEntries = arrayUnique($scope.temp.policy.blackListEntries.concat($scope.temp.policy.yamlparams.blackList));
				$scope.temp.policy.appendBlackListEntries = JSON.parse($scope.data.appendBlackListEntries);
				$scope.blackListEntries = $scope.blackListEntries.filter(function (obj){
					return !$scope.temp.policy.appendBlackListEntries.includes(obj);
				});
				if($scope.blackListEntries.length == 0){
					$scope.validateButton = true;
					Notification.error("Black Lists are empty. Minimum one entry required.");
				}else{
					$scope.temp.policy.blackListEntries = $scope.blackListEntries;
					Notification.success("Blacklist File Uploaded Successfully.");
					$scope.validateButton = false;
					$scope.importButton = true;
				}
			}else{
				 Notification.error("Blacklist File Upload Failed." + $scope.temp.policy.blackListEntries[1]);
			}
		}).error(function(data){
			 Notification.error("Blacklist File Upload Failed.");
		});
	};
	
	$scope.initializeBlackList = function(){
		if($scope.temp.policy.blackListEntryType === "Use File Upload"){
			 $scope.validateButton = true;	
		} else {
			 $scope.validateButton = false;	
		}
		$("#importFile").val('');
	};
	
	$scope.exportBlackListEntries = function(){
		var uuu = "policycreation/exportDecisionBlackListEntries";
        var postData={policyData: $scope.temp.policy, date : $scope.temp.model.modifiedDate, version : $scope.temp.model.version};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.data=data.data;
                    var url = '../' + $scope.data;
                    window.location = url;
                    Notification.success("BlackList Entries Exported Successfully.");
                });
                console.log($scope.data);
            },
            error : function(data){      	
                Notification.error("Error Occured while Exporting BlackList Entries.");
            }
        });
	};
}]);
