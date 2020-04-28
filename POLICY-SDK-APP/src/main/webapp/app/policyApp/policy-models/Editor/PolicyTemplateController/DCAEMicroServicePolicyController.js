/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2020 AT&T Intellectual Property. All rights reserved.
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

angular.module('abs').controller('dcaeMicroServiceController', 
    ['$scope', '$window', '$compile', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', 
    function ($scope, $window, $compile, PolicyAppService, PolicyNavigator, modalService, $modal, Notification) {
    $("#dialog").hide();
    
    $scope.policyNavigator;
    $scope.isCheck = false;
    $scope.savebutton = true;
    $scope.refreshCheck = false;
    
    var layer = 0;
    
    if(!$scope.temp.policy.editPolicy  && !$scope.temp.policy.readOnly){
    $scope.temp.policy = {
        policyType : "Config",
        configPolicyType : "Micro Service"
    }
    }
    
    $scope.refresh = function(){
    if($scope.refreshCheck){
        $scope.policyNavigator.refresh();
    }
    $scope.modal('createNewPolicy', true);
    $scope.temp.policy = "";
    };
    
    $scope.modal = function(id, hide) {
        return $('#' + id).modal(hide ? 'hide' : 'show');
    };
    
    $('#ttlDate').datepicker({
    dateFormat: 'dd/mm/yy',
    changeMonth: true,
    changeYear: true,
    onSelect: function(date) {
        angular.element($('#ttlDate')).triggerHandler('input');
    }
    });
    
    if ($scope.temp.policy.editPolicy != undefined|| $scope.temp.policy.readOnly  != undefined){
    if ($scope.temp.policy.configName == undefined){
    $scope.isCheck = false;
    }else{
        $scope.isCheck = true;
    }
    }else {
        $scope.isCheck = false;
    }
    
    PolicyAppService.getData('getDictionary/get_OnapNameDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.onapNameDictionaryDatas = JSON.parse($scope.data.onapNameDictionaryDatas);
    });

    PolicyAppService.getData('get_DCAEPriorityValues').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.priorityDatas = JSON.parse($scope.data.priorityDatas);
    });

    PolicyAppService.getData('getDictionary/get_GroupPolicyScopeDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.groupPolicyScopeListDatas = JSON.parse($scope.data.groupPolicyScopeListDatas);
    });

    PolicyAppService.getData('getDictionary/get_MicroServiceConfigNameDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    if($scope.data.microServiceConfigNameDictionaryDatas){
         $scope.microServiceCongigNameDictionaryDatas = JSON.parse($scope.data.microServiceConfigNameDictionaryDatas);
    }
    });

    PolicyAppService.getData('getDictionary/get_MicroServiceLocationDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.microServiceLocationDictionaryDatas = JSON.parse($scope.data.microServiceLocationDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_MicroServiceModelsDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    var inputModelList = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
    var unique = {};
    var uniqueList = [];
    for(var i = 0; i < inputModelList.length; i++){
    if(typeof unique[inputModelList[i]] == "undefined"){
        unique[inputModelList[i]] = "";
        uniqueList.push(inputModelList[i]);
    }
    }
    $scope.microServiceModelsDictionaryDatas = uniqueList;
    });

    PolicyAppService.getData('getDictionary/get_DCAEUUIDDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.dcaeUUIDDictionaryDatas = JSON.parse($scope.data.dcaeUUIDDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_RiskTypeDataByName').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
    });

    PolicyAppService.getData('getDictionary/get_MicroServiceAttributeData').then(function (data) {
    var j = data;
    $scope.data = JSON.parse(j.data);
    $scope.microServiceAttributeDictionaryDatas = JSON.parse($scope.data.microServiceAttributeDictionaryDatas);
    });
        

     $scope.choices = [];
     $scope.attributeDatas = [{"attributes" : $scope.choices}];
     addNewChoice = function(value) {
     var isFoundInRuleData = false;
     if(value != undefined){
    if (value.startsWith('div.')){
        value = value.replace('div.','');
    }
    
    var parentElement = document.getElementById("div."+value);
    var div = document.getElementById(value+"@0");
    if(div != null){
        var clone = div.cloneNode(true); 
        var addElement = parentElement.childElementCount;
        clone.id = ''+value+'@'+addElement;
        clone.value = '';
        if($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly){ 
        //if it's vi or edit
        if($scope.temp.policy.ruleData[clone.id] || $scope.temp.policy.editPolicy){  
        //Only append child if its value found in ruleData or edit mode    
            if($scope.temp.policy.ruleData[clone.id]){
                clone.value = $scope.temp.policy.ruleData[clone.id];
                isFoundInRuleData = true;
            }
            if(!isFoundInRuleData && isInitViewEdit){
            return;
            }
            if(!clone.className.includes("child_single")){
               clone.className += ' child_single'; // here cloned is single element
            }
            document.getElementById("div."+value).appendChild(clone);
            plainAttributeKeys.push(''+value+'@'+addElement);
        }
        }else{ // not view or edit
        if(!clone.className.includes("child_single")){
            clone.className += ' child_single'; // here cloned is single element
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
            }
            div.className += ' children_group border' + layer; // here is div with a group of children.
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
            if(checkValue == "false"){
                document.getElementById(inputs[i].id).removeAttribute("checked");
                plainAttributeKeys.push(inputs[i].id);
            }
            else if(checkValue == "true"){
                document.getElementById(inputs[i].id).setAttribute("checked", true);
                plainAttributeKeys.push(inputs[i].id);
            }else{
                document.getElementById(inputs[i].id).value = checkValue;
                plainAttributeKeys.push(inputs[i].id);
            }
        } else {
            if(inputs[i].type == "checkbox"){
                inputs[i].checked = false;
            }
            plainAttributeKeys.push(inputs[i].id);
        }
        }else {
        document.getElementById(inputs[i].id).removeAttribute("checked");
        plainAttributeKeys.push(inputs[i].id);
        }
    }
        
        for(var i=0; i<selects.length; i++){
        if ($scope.temp.policy.ruleData!=undefined){
            var checkValue = $scope.temp.policy.ruleData[selects[i].id];
            if (checkValue!=undefined && checkValue!="undefined"){
            if($scope.temp.policy.ruleData != null){
                var checkValue = $scope.temp.policy.ruleData[selects[i].id];
                var option = document.createElement('option');
                option.setAttribute('value', checkValue);
                option.appendChild(document.createTextNode(checkValue));
                document.getElementById(selects[i].id).appendChild(option);
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
     
    
     removeChoice = function(value) {
     if(value != undefined){
     var c = document.getElementById("div."+value).childElementCount;
     
     if(c == 1){
         Notification.error("The original one is not removable.");
         return;
     }    
     document.getElementById("div."+value).removeChild(document.getElementById("div."+value).lastChild);     
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
     if(serviceName != undefined){
     var uuu = "policyController/getModelServiceVersioneData.htm";
     var postData={policyData: serviceName};
     $.ajax({
         type : 'POST',
         url : uuu,
         dataType: 'json',
         contentType: 'application/json',
         data: JSON.stringify(postData),
         success : function(data){
         $scope.$apply(function(){
             $scope.microServiceModelsDictionaryVersionDatas = data[0].dcaeModelVersionData;
         });
         },
         error : function(data){
         Notification.error("Error While Retriving the Template Layout Pattren.");
         }
     });     
     }
     };
    
    var splitEqual = '=';
    var splitComma = ',';
    var splitcolon = ':';
    var splitEnum = "],";
    var plainAttributeKeys = [];
    var matching = [];
    var enumKeyList = [];
    var dictionaryList = [];
    var dictionaryNameList = [];
    var isInitViewEdit = false;
    $scope.addDataToFields = function(serviceName, version){
        if(serviceName != null && version !=null){
        var service=serviceName+"-v"+version;
        var myNode = document.getElementById("DynamicTemplate");
        myNode.innerHTML = '';
            var uuu = "policyController/getDCAEMSTemplateData.htm";
            var postData={policyData: service};
            
            
            var dataOrderInfo = "";
            
            booleanTrueElements = [];

            document.getElementById("msPolicyloader").style.visibility = "visible";
            
            $.ajax({
                type : 'POST',
                url : uuu,
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(postData),
                success : function(data){
                
                document.getElementById("msPolicyloader").style.visibility = "hidden";
                
                    $scope.$apply(function(){
                    $scope.dcaeModelData = data[0].dcaeModelData;
                    $scope.dcaeJsonDate = data[0].jsonValue;
                        $scope.dataOrderInfo = null;
                    $scope.dataOrderInfo = data[0].dataOrderInfo;
                    
                    if(data[0].allManyTrueKeys){
                    }
                    var attributes = $scope.dcaeModelData.attributes;                    
                    var refAttributes = $scope.dcaeModelData.ref_attributes;
                    var subAttributes =     $scope.dcaeModelData.sub_attributes;                    
                    var headDefautlsData  = data[0].headDefautlsData;
                    if(headDefautlsData != null){
                        $scope.temp.policy.onapName = headDefautlsData.onapName;
                        $scope.temp.policy.guard = headDefautlsData.guard;
                        $scope.temp.policy.riskType = headDefautlsData.riskType;
                        $scope.temp.policy.riskLevel = headDefautlsData.riskLevel;
                        $scope.temp.policy.priority = headDefautlsData.priority;
                    }
                    
                       var annotation = $scope.dcaeModelData.annotation;
                       var dictionary = $scope.microServiceAttributeDictionaryDatas;

                       if (annotation == null || annotation.length<1){
                           $scope.isCheck = true;
                       }else {
                           $scope.isCheck = false;
                           var annoationList = annotation.split(splitComma);
                           for (k = 0; k < annoationList.length; k++){
                               var splitAnnotation = annoationList[k].split(splitEqual);
                               if (splitAnnotation[1].includes("matching-true")){
                               matching.push(splitAnnotation[0].trim());
                               }
                           }

                       }

                       if (dictionary!= null && dictionary.length>1){
                           for (m=0; m < dictionary.length; m += 1){
                           var valueCompare = dictionary[m].value;
                           var valueModel = dictionary[m].modelName;
                           var conpairService = serviceName;
                           if (valueModel.includes('-v')){
                               conpairService = service;
                           }
                           if(valueModel.localeCompare(conpairService) == 0){
                               dictionaryList.push(dictionary[m]);
                               if (!dictionaryNameList.includes(dictionary[m].name)){
                               dictionaryNameList.push(dictionary[m].name)
                               }
                           }
                           }
                       }

                    $scope.temp.policy.ruleGridData = [];
                    
                    if($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly){
                    dataOrderInfo = $scope.dataOrderInfo;
                    }
                    
                    $scope.jsonLayout($scope.dcaeJsonDate);
                    
                    });
                    
                    if($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly){  // If it's veiw or edit
                    isInitViewEdit = true;
                    var checkData = [];
                    var data = [];
                        // If ruleData contains extra elements created by clicked add button
                        if($scope.temp.policy.ruleData != null){
                            var propNames = Object.getOwnPropertyNames($scope.temp.policy.ruleData);
                            propNames.forEach(function(name) {
                            if(document.getElementById(name) != null){
                                if(document.getElementById(name).getAttribute("class") == "onoffswitch-checkbox"){
                                    if($scope.temp.policy.ruleData[name] == "true"){
                                        document.getElementById(name).setAttribute("checked", true);
                                        }
                                    else{
                                	    document.getElementById(name).removeAttribute("checked");
                                	}
                                    }
                                }
                            data.push(name);
                            });
                            var extraElements = data;    
                            if(plainAttributeKeys != null){
                            for(var b = 0; b < plainAttributeKeys.length; b++){ // Remove already populated elements from data array
                            var newValue = plainAttributeKeys[b].split("*");
                            for(var a = 0; a < data.length; a++){
                            if(data[a] === newValue[0] || data[a] === (newValue[0]+"@0")){
                                extraElements.splice(a, 1);
                            }
                            }
                        }    
                        // --- Populate these extra elements created by clicked add button
                        for(var a = 0; a < extraElements.length; a++){                
                            if(extraElements[a].includes("@")){
                            var index = extraElements[a].lastIndexOf("@");
                            if(index > 0){
                                // Get the number after @
                                var n = getNumOfDigits(extraElements[a], index+1);
                                var key = extraElements[a].substring(0, index+n+1); // include @x in key also by n+2 since x can be 1,12,etc
                                checkData.push(key);
                            }
                            }
                        }
                            var unique = checkData.filter(onlyUnique);
                            var parentLevelElements = [];
                            if(unique){
                            // --- get all root level exta elments first (only contains one "@")
                            for(var i =0; i < unique.length; i++){
                            var firstIndex = unique[i].indexOf("@");
                            var lastIndex = unique[i].lastIndexOf("@");
                            if(firstIndex == lastIndex){
                                var newKey = unique[i].substring(0, firstIndex);
                                parentLevelElements.push(newKey);
                                unique[i] = "*processed*";
                            }
                            }
                            } 
                          for (var i = 0; i < $scope.labelManyKeys.length; i++) {
                              var label = $scope.labelManyKeys[i];                              
                              if(parentLevelElements){
                              for (var k = 0; k < parentLevelElements.length; k++){
                                  if(label == parentLevelElements[k]){
                                  addNewChoice(label);
                                  }
                              }                                  
                              }                              
                          }
                          
                            // if no layout order info, keep the process as before
                            if(!dataOrderInfo){
                                for(var i =0; i < unique.length; i++){
                                if(unique[i] != "*processed*"){
                                var index = unique[i].lastIndexOf("@");
                                var newKey = unique[i].substring(0, index);
                                
                                var newElement = document.getElementById("div."+unique[j]);
                                // check weather it has been created already
                                                if(newElement != null){
                                                continue;
                                                }else{
                                                newElement = document.getElementById(unique[j]);
                                                if(newElement != null){
                                                    continue;
                                                }
                                                } 
                                                
                                if(newKey){
                                addNewChoice(newKey);
                                }
                                }
                                }
                        }else{
                              // ---reset to default
                              dataOrderInfo = [];
                              $scope.labelManyKeys = [];
                              
                          // ---process none labels
                          for (var j = 0; j < unique.length; j++){
                              if(unique[j] != "*processed*"){
                              // if not created yet
                              if(!document.getElementById(unique[j])){
                                    var index = unique[j].lastIndexOf("@");
                                    var newKey = unique[j].substring(0, index);
                                    
                                    var newElement = document.getElementById("div."+unique[j]);
                                    
                                    // check weather it has been created already
                                                        if(newElement != null){
                                                        continue;
                                                        }else{
                                                        newElement = document.getElementById(unique[j]);
                                                        if(newElement != null){
                                                            continue;
                                                        }
                                                        } 
                                                        // if not created yet,then create it.
                                    addNewChoice(newKey);
                                  
                              }
                              }
                          }
                          }
                        }
                        }

                    }
                    var ele = angular.element(document.getElementById("DynamicTemplate"));
                $compile(ele.contents())($scope);
                    $scope.$apply();
                    isInitViewEdit = false;                   
                    
                },
                error : function(data){
                Notification.error("Error While Retriving the Template Layout Pattren.");
                }
            });

        }
    };
    
    function getNumOfDigits(str_value, index){
    // Get the number after @
    var str = str_value.substring(index, str_value.length);    
    var c = '';
    var n = 0;
    for (var x = 0; x < str.length; x++){                                
        c = str.charAt(x);
        if(!isNaN(c)){ 
                n++;                                                     
        }else{
                break;
            }
    }
    return n;
    }
    
    function getDictionary(attribute){
    var dicName = attribute;
    if(attribute){
       if(attribute.includes(":")){
           dicName = attribute.split(":")[0];
       }
    }
        var dictionaryRegExp = new RegExp(dicName);
        listemunerateValues = [];
        if (dictionaryRegExp.test(dictionaryNameList)) {
    for (p=0; p < dictionaryList.length; p += 1) {
        if (dicName == dictionaryList[p].name) {
        listemunerateValues.push(dictionaryList[p].value);
        }
    }
        }
        return listemunerateValues;
    }
    
    function getList(attribute) {
        var enumName = attribute;
        if(attribute){
           if(attribute.includes(":")){
               enumName = attribute.split(":")[0];
           }
        }   
            var baseEnum = $scope.dcaeModelData.enumValues;
            var enumList = [];
            if(baseEnum != null){
            enumList = baseEnum.split(splitEnum);
            }
        var enumAttributes;
        var patternTest = new RegExp(enumName);
        for (k=0; k < enumList.length; k += 1){
            if(patternTest.test(enumList[k]) == true){
            enumAttributes = enumList[k].trim();
        }
    }

         if(enumAttributes){
            enumAttributes = enumAttributes.replace("[", "");
           enumAttributes = enumAttributes.replace("]", "");
           enumAttributes = enumAttributes.replace(/ /g, '');
    var dropListAfterCommaSplit = enumAttributes.split(splitEqual);
    listemunerateValues  = dropListAfterCommaSplit[1].split(splitComma);
    // enumKeyList.push(attribute);
            return listemunerateValues;
         }
         
            return [];
        }
        
        function getBooleanList(){
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
        
    function deconstructJSON(layOutData, level , name) {

     var array = false;
     var label = level;
     var stringValue = "java.lang.String";
     var string = "string";
     var intValue = "int";
     var integerValue = "integer";
     var double = "double";
     var boolean = "boolean";
     var baseLevel = level;
     
     var attributekey = "";
     
    if (name.length > 1){
        label = label + name + '.';
    }
    
        for (key in layOutData) {
        array = isArray(layOutData[key]);
        
        if (!!layOutData[key] && typeof(layOutData[key])=="object") {
            
            if (array==false && key!=="0"){
            
            if($scope.dataOrderInfo){
                var labelObject = {"label" : key, "level" : label, "array" : array};
                // save it to the list
                $scope.layOutOnlyLableList.push(labelObject);
                
            }else {
                // call label layout
                $scope.labelLayout(label, key, array );
            }
            
            }
            
            if (array == true && key!=0){
            lableList.push(key);
            }
            
            if (lableList.length > 0){
            array = true;
            }
            if ( key==="0"){
            var newKey = lableList.pop();
            
            if($scope.dataOrderInfo){
                
            var labelObject = {"label" : newKey, "level" : baseLevel, "array" : array};
            // save it to the list
            $scope.layOutOnlyLableList.push(labelObject);
            
            }else {
                // call label layout
                $scope.labelLayout(baseLevel, newKey, array );
            }
            
            if (array){
                label = baseLevel + newKey + '@0.';
            } else {
                label = baseLevel + newKey + '.';
            }
            }
            deconstructJSON(layOutData[key] , label, key);
            } else {
            var attirbuteLabel = label;
            var defaultValue='';
            var description='';
            var isRequired = false;
            if (layOutData[key].includes('defaultValue-')){
                defaultValue = layOutData[key].split('defaultValue-')[1];
            }
            
            if (layOutData[key].includes('description-')){
                description = layOutData[key].split('description-')[1];
            }            
            

            if (key==="0"){
                array = true;
                attributekey = lableList.pop();
                attirbuteLabel = baseLevel;
            } else {
                attributekey = key.split();
            }
            
            if (layOutData[key].includes('required-true')){
                isRequired = true;
            }
            
            var subAttributes = $scope.dcaeModelData.subAttributes;
            
            if(subAttributes){            
            var jsonObject = JSON.parse(subAttributes);    
            
                var lablInfo = findVal(jsonObject, attributekey);
            if (lablInfo){
                if(lablInfo.includes('required-true')){
                isRequired = true;
                }    
                if(lablInfo.includes('defaultValue-')){
                defaultValue = lablInfo.split('defaultValue-')[1];
                }
                
                if(lablInfo.includes('description-')){
                description = lablInfo.split('description-')[1];
                }
                
            }else{            
                var allkeys = Object.keys(jsonObject);
               if(allkeys){
                   for (var k = 0; k < allkeys.length; k++) {
                  var keyValue = allkeys[k];
                  if(jsonObject[keyValue]){
                     var tempObject = jsonObject[keyValue];
                     if(tempObject && tempObject[key]){
                         if (tempObject[key].includes('required-true')){
                            isRequired = true;
                         }    
                         
                         if(tempObject[key].includes('defaultValue-')){
                            defaultValue = tempObject[key].split('defaultValue-')[1];
                         }
                         
                         if(tempObject[key].includes('description-')){
                             description = tempObject[key].split('description-')[1];
                          }

                     }
                  }
                   }                
               }            
            } 
            }
            
            var elementObject = {};
            switch (layOutData[key].split(splitcolon)[0]){
            
                case stringValue:
                case string:
                if($scope.dataOrderInfo){                    
                    elementOrderNum++;
                    elementObject = {"id": elementOrderNum,"attributekey" : attributekey, "array": array, "attirbuteLabel" : attirbuteLabel, "defaultValue": defaultValue, "isRequired": isRequired, "type":"text", "description":description};
                    $scope.layOutElementList.push(elementObject);
                }else{
                    $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "text");
                }
                break;                
                case intValue: 
                case integerValue: 
                if($scope.dataOrderInfo){
                elementOrderNum++;
                    elementObject = {"id": elementOrderNum,"attributekey" : attributekey, "array": array, "attirbuteLabel" : attirbuteLabel, "defaultValue": defaultValue,"isRequired": isRequired, "type":"number" , "description":description};
                    $scope.layOutElementList.push(elementObject);
                }else{
                    $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "number");
                }
                break;                
                case double:
                if($scope.dataOrderInfo){
                elementOrderNum++;
                    elementObject = {"id": elementOrderNum,"attributekey" : attributekey, "array": array, "attirbuteLabel" : attirbuteLabel, "defaultValue": defaultValue,"isRequired": isRequired, "type":"double",  "description":description};
                    $scope.layOutElementList.push(elementObject);                    
                }else{
                    $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "double");
                }
                break;
                case boolean:
                if($scope.dataOrderInfo){
                elementOrderNum++;
                    elementObject = {"id": elementOrderNum,"attributekey" : attributekey, "array": array, "attirbuteLabel" : attirbuteLabel, "defaultValue": defaultValue, "isRequired": isRequired, "type":"boolean", "description":description};
                    $scope.layOutElementList.push(elementObject);
                }else{
                    $scope.dropBoxLayout(attirbuteLabel, attributekey, array, layOutData[key], getBooleanList());
                }
                break;
                default:
                if (layOutData[key].includes('dictionary-')){
                    var list = getDictionary(layOutData[key].split('dictionary-')[1]);
                }else{
                    // --- get dropdown values from enumValues
                    var list = getList(layOutData[key]);
                }
                if (list.length===0){ // not dropdown element
                    if($scope.dataOrderInfo){
                    elementOrderNum++;
                    elementObject = {"id": elementOrderNum,"attributekey" : attributekey, "array": array, "attirbuteLabel" : attirbuteLabel, "defaultValue": defaultValue,"isRequired": isRequired, "type":"text", "description":description};
                    $scope.layOutElementList.push(elementObject);
                    
                    }else{
                    $scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired, "text");
                    }                    
                }else{
                    if($scope.dataOrderInfo){
                    elementOrderNum++;
                    elementObject = {"id": elementOrderNum, "attributekey" : attributekey, "array": array, "attirbuteLabel" : attirbuteLabel, "defaultValue": defaultValue,"isRequired": isRequired, "list":list, "type":"dropBox", "description":description};
                    $scope.layOutElementList.push(elementObject);                    
                    }else{
                    $scope.dropBoxLayout(attirbuteLabel, attributekey, array, layOutData[key], list, isRequired);
                    }
                }
                break;
            }
            }
        }
    }  
    
    
        $scope.validContionalRequired = function(parentId) {
            var c = document.getElementById(parentId).children;
            var i;
            var hasValue = false;
            for (i = 0; i < c.length; i++) {
            if(c[i].getAttribute("data-conditional")){
                if(c[i].value != null && c[i].value.trim() != ""){
                hasValue = true;
                }
            }
            }

        for (i = 0; i < c.length; i++) {
            if(c[i].getAttribute("data-conditional")){
            if(hasValue){
                c[i].setAttribute("required", true);
            }else{
                c[i].removeAttribute("required");
            }
            }
        }        
         }
        
        $scope.jsonLayout = function(layOutData){
        
            deconstructJSON(layOutData , "", "");
            
            var orderValue = $scope.dataOrderInfo;
            var layOutElementList = $scope.layOutElementList;
            var labelList = $scope.layOutOnlyLableList;
             
            // reset to default
            elementOrderNum = 0;
            $scope.layOutElementList = [];
            $scope.layOutOnlyLableList = [];
            
            // Only layout in order if order info provided
            if(orderValue){
                
                if(orderValue.includes("[")){
               orderValue = orderValue.replace("[", "") ;
               orderValue = orderValue.replace("]", "") ;
                }
                
                orderValue = orderValue.split(',') ;
                
                for (var i = 0; i < orderValue.length; i++) {
                var key = orderValue[i].trim();
              
                 // --- Create labels first {"label" : newKey, "level" :
					// baseLevel, "array" : array};
                if(labelList){
                    for (var k = 0; k < labelList.length; k++){
                        
                       var label = labelList[k].label.toString().trim();
                       var level = labelList[k].level.toString().trim();
                       var array = labelList[k].array;
                       
                       if(key == label){                       
                      $scope.labelLayout(level, label, array);
                 // in case to have duplicate label names
                      labelList[k].label = "*processed*";
                      break;
                       }
                    }
                }
                // --- then layout each element based on its order defined in YAML file
                      for (var j = 0; j < layOutElementList.length; j++) { 
                          
                          var attributekey = layOutElementList[j].attributekey.toString().trim();                          
                       
                          if(key == attributekey){     

                  var attirbuteLabel = layOutElementList[j].attirbuteLabel.toString().trim();
                  var defaultValue = layOutElementList[j].defaultValue.toString().trim();
                  var description = layOutElementList[j].description;
                  var isRequired = layOutElementList[j].isRequired;
                               
                               if (layOutElementList[j].type == "dropBox"){ 
                         $scope.dropBoxLayout(attirbuteLabel, attributekey, layOutElementList[j].array, defaultValue, layOutElementList[j].list, isRequired, description);
                                    
                   }else{
                    $scope.attributeBox(attributekey, layOutElementList[j].array, attirbuteLabel, defaultValue, isRequired, layOutElementList[j].type, description);    
    
                   }
                               
                               // in case to have duplicate attribute names
                               layOutElementList[j].attributekey = "*processed*";
                        break;
                          }
                      }
                }
            }
        }
        
        
        $scope.attributeBox = function(attibuteKey, attributeManyKey, labelValue, defaultValue, isRequired, dataType,  description){
    $scope.temp.policy.ruleGridData.push(attibuteKey);    
    var br = document.createElement("BR");
    
    var label = document.createElement("Label");
    var labeltext = null;
    var requiredLabName = "";
    if (matching.includes(attibuteKey)){
    labeltext = document.createTextNode(attibuteKey + "*!");    
    isRequired = true;  // set required as true for matching element
    }else {
    if(isRequired){
        requiredLabName = attibuteKey + " * ";
        labeltext = document.createTextNode(requiredLabName);
    }else{
        labeltext = document.createTextNode(attibuteKey);    
    }
    }

    
    var divID = labelValue;
    
    if (labelValue.length  < 1){
    divID = "DynamicTemplate";
    }else if (labelValue.endsWith('.')){
    var divID = 'div.'+ labelValue.substring(0, labelValue.length-1);
    }
    
    label.appendChild(labeltext);
    
    var textField = document.createElement("INPUT");
    
    textField.setAttribute("class" , "form-control");
    if(dataType){
    if(dataType == "double"){
        textField.setAttribute("type" , "number");
        textField.setAttribute("step" , "any");

    }else if(dataType == "boolean"){  // gw1218 testing boolean
        var booleanDiv = document.createElement("div");

        booleanDiv.setAttribute("class" , "onoffswitch");

        // var checkField = document.createElement("INPUT");
        textField.setAttribute("type" , "checkbox");
        textField.setAttribute("name" , "onoffswitch");
        textField.setAttribute("class" , "onoffswitch-checkbox");
        textField.setAttribute("id" , ''+labelValue +attibuteKey+'');
        if(defaultValue.substring(0,defaultValue.indexOf(":")) == "true") {
        textField.setAttribute("checked" , true);
        }else{
        textField.removeAttribute("checked");
        }
            textField.setAttribute("ng-click" , "validateOnAndOff('"+labelValue +attibuteKey+"', $event)");
            var booleanlabel = document.createElement("Label");
            booleanlabel.setAttribute("class" , "onoffswitch-label");
            booleanlabel.setAttribute("for" , ''+labelValue +attibuteKey+'');
        
            var span1 = document.createElement("span");
            span1.setAttribute("class" , "onoffswitch-inner");
            
            var span2 = document.createElement("span");
            span2.setAttribute("class" , "onoffswitch-switch"); 
            
            
            booleanlabel.appendChild(span1);     
            booleanlabel.appendChild(span2);     
            booleanDiv.appendChild(textField);     
            booleanDiv.appendChild(booleanlabel);      
            
            document.getElementById(divID).appendChild(label);  
            document.getElementById(divID).appendChild(booleanDiv);     

            // return;
       } else{
          textField.setAttribute("type" , dataType);
       }
       
        

    }
    
    if(dataType != "boolean"){
    textField.setAttribute("style" , "width:300px;");
    textField.setAttribute("ng-disabled" , "temp.policy.readOnly");
    if(description && description != "null"){
        textField.setAttribute("title", description);
    }
    }

    var checkKey;
    var id = "";
    if(attributeManyKey){
    checkKey = labelValue + attibuteKey+'@0';
    textField.setAttribute("id" , ''+labelValue + attibuteKey+'@0'+''); 
    var divTag = document.createElement("div");
    divTag.setAttribute("id", "div."+ labelValue +attibuteKey);
    var addButton = document.createElement("BUTTON");
    var buttonaddLabel = document.createTextNode("+");       
    addButton.appendChild(buttonaddLabel); 
    addButton.setAttribute("id", labelValue + attibuteKey);
    addButton.setAttribute("class", "btn btn-add-remove");
    addButton.setAttribute("onclick" ,  'addNewChoice("'+labelValue + attibuteKey+'");');
    addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
    var removeButton = document.createElement("BUTTON");
    var buttonremoveLabel = document.createTextNode("-");       
    removeButton.appendChild(buttonremoveLabel); 
    removeButton.setAttribute("class", "btn btn-add-remove");
    removeButton.setAttribute("onclick" ,  'removeChoice("'+labelValue + attibuteKey+'");');
    removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
    document.getElementById(divID).appendChild(addButton); 
    document.getElementById(divID).appendChild(removeButton); 
    document.getElementById(divID).appendChild(label); 
    id = "div."+labelValue+attibuteKey;
    // var divTag = document.createElement("div");
    divTag.setAttribute("id", id); 
    document.getElementById(divID).appendChild(divTag);
    textField.className += ' first_child';    
    if(isRequired){
        textField.setAttribute("required", "true");
    }    
    
    divTag.appendChild(textField);     
    document.getElementById(divID).appendChild(divTag); 
    
    }else if (dataType != "boolean"){
    checkKey = labelValue + attibuteKey;
    textField.setAttribute("id" , ''+labelValue +attibuteKey+'');
    if(document.getElementById(divID).hasAttribute('required') || !document.getElementById(divID).hasAttribute('data-conditional')){
        if(requiredLabName.includes("*") || isRequired){
        textField.setAttribute("required", "true");
        }
    }else if (document.getElementById(divID).hasAttribute('data-conditional')){
        if(requiredLabName.includes("*")){        
        var requiredNode = document.createElement('span');
        requiredNode.setAttribute("class", "mstooltip");
        requiredNode.textContent = "?";
        label.appendChild(requiredNode);
        
        var requiredNodeToolTip = document.createElement('span');
        requiredNodeToolTip.setAttribute("class", "tooltiptext");
        requiredNodeToolTip.textContent = "Conditional Required";
        requiredNode.appendChild(requiredNodeToolTip);
        
        textField.setAttribute("data-conditional", divID);
        textField.setAttribute("ng-blur", "validContionalRequired('"+divID+"')");
        }
    }
            
    document.getElementById(divID).appendChild(label);  
    document.getElementById(divID).appendChild(textField);  
    document.getElementById(divID).appendChild(br); 

    }

    if(dataType != "boolean" && divID.includes("@0") && divID.includes("div.")){
    var firstChild_Id = divID.split("@0")[0];
    var firstChild_element = document.getElementById(firstChild_Id);
    if(firstChild_element){
    
        if(!firstChild_element.className.includes('children_group border')){
        layer++;
        if(layer > 4){ 
            layer = 1
        }
        firstChild_element.className += ' children_group border' + layer; // here is div with a group of children.
        }
    }
    }
    
    if(dataType != "boolean" && defaultValue.length > 0){    
    if(defaultValue.includes(":")){
        defaultValue = defaultValue.split(":")[0];
        if(defaultValue === "NA") {
        defaultValue = "";
        }        
    }
    if(defaultValue != "undefined" && defaultValue != undefined && defaultValue != "null"){
        document.getElementById(checkKey).value = defaultValue;
    }
    }
    
    if($scope.temp.policy.ruleData != null){
    // document.getElementById(checkKey).value =
    // $scope.temp.policy.ruleData[checkKey];
    if (attributeManyKey){
        var newCheckKey = checkKey.replace(attibuteKey + '@0',attibuteKey);
        if($scope.temp.policy.ruleData[newCheckKey +'@0'] != undefined && $scope.temp.policy.ruleData[newCheckKey +'@0'] != "undefined"){
          document.getElementById(newCheckKey +'@0').value = $scope.temp.policy.ruleData[newCheckKey +'@0'];
        }
    }else{
        if($scope.temp.policy.ruleData[checkKey] != undefined && $scope.temp.policy.ruleData[checkKey] != "undefined"){
            document.getElementById(checkKey).value = $scope.temp.policy.ruleData[checkKey];
        }
    }
    } 
    
    if (dataType != "boolean"){
        plainAttributeKeys.push(labelValue + attibuteKey+'*'+attributeManyKey);    
    }else{
        plainAttributeKeys.push(labelValue + attibuteKey+'*'+"boolean");    
    }
    };
    
    $scope.validateOnAndOff = function(id , value) {
        console.log(id, value);
        if (value.target.checked) {
            document.getElementById(id).setAttribute("checked", true);
        } else {
            document.getElementById(id).removeAttribute("checked");
        }
    };
    
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
    
    var subAttributes = $scope.dcaeModelData.subAttributes;
        var jsonObject = JSON.parse(subAttributes);    
        var lablInfo = findVal(jsonObject, lableName);
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
    addButton.setAttribute("onclick" ,  'addNewChoice("'+labelValue + lableName+'");');
    addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
    var removeButton = document.createElement("BUTTON");
    var buttonremoveLabel = document.createTextNode("-");       
    removeButton.appendChild(buttonremoveLabel); 
    removeButton.setAttribute("class", "btn btn-add-remove");
    removeButton.setAttribute("onclick" ,  'removeChoice("'+labelValue +lableName+'");');
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
        }
        divTag.className += ' children_group border' + layer; // here is div with a group of children.
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
        }
        divTag.className += ' children_group border' + layer; // here is div with a group of children.
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

    $scope.dropBoxLayout = function(labelLevel, attributeName, many , defaultValue, listemunerateValues, isRequired, description){
    var br = document.createElement("BR");
    
    if (labelLevel.length  < 1){
        var divID = "DynamicTemplate";
    } else if (labelLevel.endsWith('.')){
        var divID = 'div.'+ labelLevel.substring(0, labelLevel.length-1);
    }    
    
    
    var label = document.createElement("Label")
    
    var refAttributes = $scope.dcaeModelData.refAttributes;
    if(isRequired != true && refAttributes){ // check refAttributes also
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

    if(description && description != "null"){
    listField.setAttribute("title", description);
    }

    if(isRequired){
    if(document.getElementById(divID).hasAttribute('data-conditional')){
        listField.setAttribute("data-conditional", divID);
        listField.setAttribute("ng-blur", "validContionalRequired('"+divID+"')");
    }else{
        listField.setAttribute("required", true);
    }
    }
    if( many != true || isRequired != true){ // add an empty option for not required or not multiple select element
    var optionFirst = document.createElement('option');
    var optionValue = "";
    if($scope.temp.policy.ruleData != null){
        if($scope.temp.policy.ruleData[labelLevel + attributeName] != undefined && $scope.temp.policy.ruleData[labelLevel + attributeName] != "undefined"){
        optionValue = $scope.temp.policy.ruleData[labelLevel + attributeName];  
        }
    } 
    optionFirst.setAttribute('value', optionValue);
    optionFirst.appendChild(document.createTextNode(optionValue));
    listField.appendChild(optionFirst);    
    }
    var ruleFormationCheck = false;
    if(listemunerateValues.length !== 0 && typeof listemunerateValues[0] == "string" && listemunerateValues[0].includes("dictionary:")) {
    var ruleCheck = listemunerateValues[0].split("&");
    var dictParams = ruleCheck[0].split(":");
    var dictParamsSplit = dictParams[1].split("@");
    if (ruleCheck[1] != undefined  && ruleCheck[1] == 'Rule') {
        ruleFormationCheck = true;
    }
    PolicyAppService.getData(dictParamsSplit[0]).then(function (data) {
        var j = data;
        $scope.data = JSON.parse(j.data);
        $scope.listDictionarys = JSON.parse($scope.data[dictParamsSplit[1]]);
        for (i=0; i < $scope.listDictionarys.length; i += 1) {
        option = document.createElement('option');
        option.setAttribute('value', $scope.listDictionarys[i]);
        option.appendChild(document.createTextNode($scope.listDictionarys[i]));
        listField.appendChild(option);
        }
    });

    }
    else{
    for (i=0; i < listemunerateValues.length; i += 1) {
        if(typeof listemunerateValues[i] == "string" && listemunerateValues[i].includes("equal-sign")){
        listemunerateValues[i] = listemunerateValues[i].replace('equal-sign','=');
        }
        
        option = document.createElement('option');
        option.setAttribute('value', listemunerateValues[i]);
        option.appendChild(document.createTextNode(listemunerateValues[i]));
        option.setAttribute('value', listemunerateValues[i]);
        listField.appendChild(option);
    }
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

    if (defaultValue){
        if(defaultValue.includes(':')){
        defaultValue = defaultValue.split(':')[0];
        }
        var location = listemunerateValues.indexOf(defaultValue);
        document.getElementById(labelLevel +attributeName).options[location+1].selected = true;
    }
    }

    if (ruleFormationCheck) {
    var optionInput = document.createElement("INPUT");
    optionInput.setAttribute("type" , "text");
    optionInput.setAttribute("id" , ''+ labelLevel + attributeName + '.input');
    optionInput.setAttribute("name" , "ruleName");
    optionInput.setAttribute("value" , "  ");
    optionInput.setAttribute("style" , "width:300px;");
    optionInput.setAttribute("ng-disabled" , "temp.policy.readOnly");
    optionInput.removeAttribute("required");

    document.getElementById(divID).appendChild(optionInput);

    var optionButton = document.createElement("BUTTON");
    optionButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
    var buttonLabel = document.createTextNode("+");       
    optionButton.appendChild(buttonLabel); 
    optionButton.setAttribute("class", "btn btn-add-remove");
    optionButton.setAttribute("onclick" , 'addDynamicOptions("'+ labelLevel + attributeName + '");');
    optionButton.removeAttribute("required");


    document.getElementById(divID).appendChild(optionButton);
    document.getElementById(divID).appendChild(br); 
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

    addDynamicOptions = function(id) { 
    var option = document.createElement("option");
    var value = document.getElementById(id+".input").value;
    option.setAttribute('value', value);
    option.appendChild(document.createTextNode(value));
    document.getElementById(id).options.add(option);
    document.getElementById(id+".input").value = "";
    };

    function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
    }

    $scope.savePolicy = function(policy){
    if(policy.itemContent != undefined){
    $scope.refreshCheck = true; 
    $scope.policyNavigator = policy.itemContent;
    policy.itemContent = "";
    }
    $scope.savebutton = false;
    var splitAt = '*';
    var dot ='.';
    var jsonPolicy = {};
    if(plainAttributeKeys != null){
    for(a = 0; a < plainAttributeKeys.length; a++){
        var splitPlainAttributeKey = plainAttributeKeys[a].split(splitAt);
        var searchElement = document.getElementById(splitPlainAttributeKey[0]);
        var key = splitPlainAttributeKey[0];
        if(searchElement == null){
        searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
        key = splitPlainAttributeKey[0]+'@0';
        }else if (searchElement.nodeName == 'BUTTON'){
        searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
        key = splitPlainAttributeKey[0]+'@0';
        }
        if(searchElement != null){
        var keySplit = key.split(dot);
        var elumentLocation = keySplit.length;
        var enumKey = key;
        if (elumentLocation > 1){
            enumKey = keySplit[keySplit.length - 1];
        }
        // check it is undefined or not
        if (enumKeyList != undefined && enumKeyList.indexOf(enumKey) != -1){
            if (splitPlainAttributeKey[1]!= undefined && splitPlainAttributeKey[1].indexOf("true") !== -1){
            var multiSlect = [];
            for ( var i = 0; i < searchElement.selectedOptions.length; i++) {
                multiSlect.push(searchElement.selectedOptions[i].value);
                }
            jsonPolicy[key]= multiSlect;
            }else{
            if(splitPlainAttributeKey[1]!= undefined && splitPlainAttributeKey[1] == "boolean"){
                jsonPolicy[key]= false;
                for(var i=0; i<booleanTrueElements.length; i++){                
                if(booleanTrueElements[i] == key){
                    jsonPolicy[key]= true;
                }
                }

            }else{
                jsonPolicy[key]= searchElement.value;
            }
            }
            } else {
            if(searchElement.value != null){
                if(searchElement.parentElement.children[0].checked == true){
                    jsonPolicy[key]= searchElement.checked;
                }
                else if(searchElement.parentElement.children[0].checked == false){
                    jsonPolicy[key]= searchElement.checked;
                }
                else{
                    jsonPolicy[key]= window.btoa(searchElement.value);
                }
                }
            } 
        }
        }
    }
        var uuu = "policycreation/save_policy";
        var postData={policyData: policy, policyJSON : jsonPolicy};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                $scope.data=data.policyData;
                if($scope.data == 'success'){
                    $scope.temp.policy.readOnly = 'true';
                    $scope.safetyChecker = data.policyData.split("#")[2];
                    if ($scope.safetyChecker!=undefined) {
                    Notification.success($scope.safetyChecker);
                    }
                    $scope.pushStatus=data.policyData.split("&")[1];
                    if($scope.pushStatus=="successPush"){
                    Notification.success("Policy pushed successfully");
                    }
                    $scope.temp.policy.readOnly = 'true';
                    $scope.savebutton = true;
                    Notification.success("Policy Saved Successfully.");    
                }else if ($scope.data == 'PolicyExists'){
            $scope.savebutton = true;
            Notification.error("Policy Already Exists with Same Name in Scope.");
        }
                });
            },
            error : function(data){
            Notification.error("Error Occured while saving Policy.");
            }
        });
    };
    
    var booleanTrueElements = [];
    $scope.validatePolicy = function(policy){
    document.getElementById("validate").innerHTML = "";
    var splitAt = '*';
    var jsonPolicy = {};
    if(plainAttributeKeys != null){
        for(a = 0; a < plainAttributeKeys.length; a++){
        var splitPlainAttributeKey = plainAttributeKeys[a].split(splitAt);
        var searchElement = document.getElementById(splitPlainAttributeKey[0]);
        var key = splitPlainAttributeKey[0];
        if(searchElement == null){
            searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
            key = splitPlainAttributeKey[0]+'@0';
        }else if (searchElement.nodeName == 'BUTTON'){
            searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
            key = splitPlainAttributeKey[0]+'@0';
        }
        if(searchElement != null){
            if (enumKeyList.indexOf(key) != -1){
            if (splitPlainAttributeKey[1].indexOf("true") !== -1){
            var multiSlect = [];
            for ( var i = 0; i < searchElement.selectedOptions.length; i++) {
                multiSlect.push(searchElement.selectedOptions[i].value);
                }
            jsonPolicy[key]= multiSlect;
            }else{
            jsonPolicy[key]= searchElement.value;
            }
            if(searchElement.getAttribute("required")){
                if(!searchElement.value){
                return;
                }
            } 
            } else {
                if(searchElement.value != null){
                    if(searchElement.parentElement.children[0].checked == true){
                	jsonPolicy[key]= searchElement.checked;
                	}
                    else if(searchElement.parentElement.children[0].checked == false){
                	jsonPolicy[key]= searchElement.checked;
                	}
                    else{
                	jsonPolicy[key]= searchElement.value;
                	}
                if(searchElement.getAttribute("required")){
                    if(!searchElement.value){
                    return;
                    }
                }                
                }
            }
        }
        }
        
        
        var checkedValue = $('.onoffswitch-checkbox:checked').val();
        
        var x = document.getElementsByClassName("onoffswitch-checkbox");
           
                 
        if(checkedValue){
        for(var i=0; i<x.length; x++){
              booleanTrueElements.push(x[i].id)
        }    
        }
    }
        var uuu = "policyController/validate_policy.htm";

        
        if($scope.isCheck == true){
        if(("configName" in policy) == false){
         Notification.error("Validation Failed: configName is required");
         $scope.savebutton = true;
         return;
        }
        if(("location" in policy) == false){
         Notification.error("Validation Failed: location is required");
         $scope.savebutton = true;
         return;
        }
        if(("uuid" in policy) == false){
         Notification.error("Validation Failed: uuid is required");
         $scope.savebutton = true;
         return;
        }
        if(("policyScope" in policy) == false){
         Notification.error("Validation Failed: policyScope is required");
         $scope.savebutton = true;
         return;
        }
        }        
        
        var postData={policyData: policy, policyJSON : jsonPolicy};
     $.ajax({
     type : 'POST',
     url : uuu,
     dataType: 'json',
     contentType: 'application/json',
     data: JSON.stringify(postData),
     success : function(data){
         $scope.$apply(function(){
         $scope.validateData = data.data.replace(/\"/g, "");
        $scope.data=data.data.substring(1,8);
            var size = data.data.length;
            if($scope.data == 'success'){
            Notification.success("Validation Success.");
            $scope.savebutton = false;
            if (size > 18){
                var displayWarning = data.data.substring(19,size  - 1);
                document.getElementById("validate").innerHTML = "Safe Policy Warning Message  :  "+displayWarning;
                document.getElementById("validate").style.color = "white";
                document.getElementById("validate").style.backgroundColor = "skyblue";
            }
             }else{
             Notification.error("Validation Failed.");
             document.getElementById("validate").innerHTML = $scope.validateData;
             document.getElementById("validate").style.color = "white";
             document.getElementById("validate").style.backgroundColor = "red";
             $scope.savebutton = true;
             }
             
         });
     },
     error : function(data){
         Notification.error("Validation Failed.");
         $scope.savebutton = true;
     }
     });
    };

    // Switch Click
    $('.Switch').click(function() {
    // Check If Enabled (Has 'On' Class)
    if ($(this).hasClass('On')){
        // Try To Find Checkbox Within Parent Div, And Check It
        $(this).parent().find('input:checkbox').attr('checked', true);
        // Change Button Style - Remove On Class, Add Off Class
        $(this).removeClass('On').addClass('Off');
    } else { // If Button Is Disabled (Has 'Off' Class)
        // Try To Find Checkbox Within Parent Div, And Uncheck It
        $(this).parent().find('input:checkbox').attr('checked', false);    
        // Change Button Style - Remove Off Class, Add On Class
        $(this).removeClass('Off').addClass('On');    
    }    
    });
     // Loops Through Each Toggle Switch On Page
    $('.Switch').each(function() {
    // Search of a checkbox within the parent
    if ($(this).parent().find('input:checkbox').length){
        
        // If checkbox doesnt have the show class then hide it
        if (!$(this).parent().find('input:checkbox').hasClass("show")){
        $(this).parent().find('input:checkbox').hide(); }
    
        // Look at the checkbox's checkked state
        if ($(this).parent().find('input:checkbox').is(':checked')){
        // Checkbox is not checked, Remove the On Class and Add the Off Class
        $(this).removeClass('On').addClass('Off');
        } else {     
        // Checkbox Is Checked Remove Off Class, and Add the On Class
        $(this).removeClass('Off').addClass('On');
        }
    }
    });

}]);
