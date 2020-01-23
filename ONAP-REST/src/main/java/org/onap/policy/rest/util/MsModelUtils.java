/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.util;

import com.att.research.xacml.util.XACMLProperties;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.json.JSONObject;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DictionaryData;
import org.yaml.snakeyaml.Yaml;

@Getter
@Setter
public class MsModelUtils {

    private static final Log logger = LogFactory.getLog(MsModelUtils.class);

    // String constants
    private static final String BOOLEAN = "boolean";
    private static final String CONFIGURATION = "configuration";
    private static final String DATATYPE = "data_types.policy.data.";
    private static final String DATA_TYPE = "data_types";
    private static final String DEFAULT = ".default";
    private static final String DEFAULTVALUE = ":defaultValue-";
    private static final String DESCRIPTION = ".description";
    private static final String DESCRIPTION_KEY = "description";
    private static final String DESCRIPTION_TOKEN = ":description-";
    private static final String DICTIONARY = "dictionary:";
    private static final String DICTIONARYNAME = "dictionaryName";
    private static final String ERROR = "error";
    private static final String E_PROXY_URI = "eProxyURI:";
    private static final String INTEGER = "integer";
    private static final String JSON_MODEL = "JSON_MODEL";
    private static final String LIST = "list";
    private static final String MANYFALSE = ":MANY-false";
    private static final String MANYTRUE = ":MANY-true";
    private static final String MAP = "map";
    private static final String MATCHABLE = ".matchable";
    private static final String MATCHABLEKEY = "matchable";
    private static final String MATCHINGTRUE = "matching-true";
    private static final String NODE_TYPE = "node_types";
    private static final String PROPERTIES = ".properties.";
    private static final String PROPERTIES_KEY = "properties";
    private static final String REQUIRED = ".required";
    private static final String REQUIREDFALSE = ":required-false";
    private static final String REQUIREDTRUE = ":required-true";
    private static final String REQUIREDVALUE = ":required-";
    private static final String STRING = "string";
    private static final String TOSCA_DEFINITION_VERSION = "tosca_definitions_version";
    private static final String TOSCA_SIMPLE_YAML_1_0_0 = "tosca_simple_yaml_1_0_0";
    private static final String TYPE = ".type";

    private static CommonClassDao commonClassDao;

    private HashMap<String, MsAttributeObject> classMap = new HashMap<>();
    private HashMap<String, String> enumMap = new HashMap<>();
    private HashMap<String, String> matchingClass = new HashMap<>();
    private String onap = "";
    private String policy = "";
    private List<String> orderedElements = new ArrayList<>();
    private String dataOrderInfo = "";
    private Set<String> uniqueDataKeys = new HashSet<>();
    private Set<String> uniqueKeys = new HashSet<>();
    private String listConstraints = null;
    private String referenceAttributes;
    private LinkedHashMap<String, Object> retmap = new LinkedHashMap<>();
    private Map<String, String> matchableValues;
    private StringBuilder dataListBuffer = new StringBuilder();
    private List<String> dataConstraints = new ArrayList<>();
    private String attributeString = null;
    private boolean isDuplicatedAttributes = false;
    private String jsonRuleFormation = null;

    /**
     * The Enum AnnotationType.
     */
    private enum AnnotationType {
        MATCHING,
        VALIDATION,
        DICTIONARY
    }

    /**
     * The Enum ModelType.
     */
    public enum ModelType {
        XMI
    }

    /**
     * The Enum SearchType.
     */
    public enum SearchType {
        TOSCA_DEFINITION_VERSION,
        TOSCA_SIMPLE_YAML_1_0_0,
        NODE_TYPE,
        DATA_TYPE,
        JSON_MODEL
    }

    /**
     * Instantiates a new ms model utils.
     */
    public MsModelUtils() {
        // Default Constructor
    }

    /**
     * Instantiates a new ms model utils.
     *
     * @param commonClassDao the common class dao
     */
    public MsModelUtils(CommonClassDao commonClassDao) {
        MsModelUtils.commonClassDao = commonClassDao;
    }

    /**
     * Instantiates a new ms model utils.
     *
     * @param onap the onap
     * @param policy the policy
     */
    public MsModelUtils(String onap, String policy) {
        this.onap = onap;
        this.policy = policy;
    }

    /**
     * Process epackage.
     *
     * @param file the file
     * @param model the model
     * @return the map
     */
    public Map<String, MsAttributeObject> processEpackage(String file, ModelType model) {
        if (model == ModelType.XMI) {
            processXmiEpackage(file);
        }
        return classMap;
    }

    /**
     * Process XMI epackage.
     *
     * @param xmiFile the xmi file
     */
    private void processXmiEpackage(String xmiFile) {
        EPackage root = getEpackage(xmiFile);
        TreeIterator<EObject> treeItr = root.eAllContents();
        String className;
        String returnValue;

        // Pulling out dependency from file
        while (treeItr.hasNext()) {
            EObject obj = treeItr.next();
            if (obj instanceof EClassifier) {
                EClassifier eclassifier = (EClassifier) obj;
                className = eclassifier.getName();

                if (obj instanceof EEnum) {
                    enumMap.putAll(getEEnum(obj));
                } else if (obj instanceof EClass) {
                    String temp = getDependencyList(eclassifier).toString();
                    returnValue = StringUtils.replaceEach(temp, new String[]
                        { "[", "]" }, new String[]
                        { "", "" });
                    getAttributes(className, returnValue, root);
                }
            }
        }

        if (!enumMap.isEmpty()) {
            addEnumClassMap();
        }
        if (!matchingClass.isEmpty()) {
            checkForMatchingClass();
        }
    }

    /**
     * Check for matching class.
     */
    private void checkForMatchingClass() {
        HashMap<String, String> tempAttribute = new HashMap<>();

        for (Entry<String, String> set : matchingClass.entrySet()) {
            String key = set.getKey();
            if (classMap.containsKey(key)) {
                Map<String, String> listAttributes = classMap.get(key).getAttribute();
                Map<String, String> listRef = classMap.get(key).getRefAttribute();
                for (Entry<String, String> eset : listAttributes.entrySet()) {
                    String key2 = eset.getKey();
                    tempAttribute.put(key2, MATCHINGTRUE);
                }
                for (Entry<String, String> eset : listRef.entrySet()) {
                    String key3 = eset.getKey();
                    tempAttribute.put(key3, MATCHINGTRUE);
                }

            }
            updateMatching(tempAttribute, key);
        }

    }

    /**
     * Update matching.
     *
     * @param tempAttribute the temp attribute
     * @param key the key
     */
    private void updateMatching(HashMap<String, String> tempAttribute, String key) {
        Map<String, MsAttributeObject> newClass = classMap;

        for (Entry<String, MsAttributeObject> updateClass : newClass.entrySet()) {
            Map<String, String> valueMap = updateClass.getValue().getMatchingSet();
            String keymap = updateClass.getKey();
            if (valueMap.containsKey(key)) {
                Map<String, String> modifyMap = classMap.get(keymap).getMatchingSet();
                modifyMap.remove(key);
                modifyMap.putAll(tempAttribute);
                classMap.get(keymap).setMatchingSet(modifyMap);
            }

        }
    }

    /**
     * Adds the enum class map.
     */
    private void addEnumClassMap() {
        for (Entry<String, MsAttributeObject> value : classMap.entrySet()) {
            value.getValue().setEnumType(enumMap);
        }
    }

    /**
     * Gets the epackage.
     *
     * @param xmiFile the xmi file
     * @return the epackage
     */
    private EPackage getEpackage(String xmiFile) {
        ResourceSet resSet = new ResourceSetImpl();
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> objectMap = reg.getExtensionToFactoryMap();
        objectMap.put("xmi", new XMIResourceFactoryImpl());
        Resource resource = resSet.getResource(URI.createFileURI(xmiFile), true);
        try {
            resource.load(Collections.emptyMap());
        } catch (IOException e) {
            logger.error("Error loading Encore Resource for new Model" + e);
        }

        return (EPackage) resource.getContents().get(0);
    }

    /**
     * Gets the e enum.
     *
     * @param obj the obj
     * @return the e enum
     */
    private HashMap<String, String> getEEnum(EObject obj) {
        List<String> valueList = new ArrayList<>();
        HashMap<String, String> returnMap = new HashMap<>();
        EEnum eenum = (EEnum) obj;

        String name = eenum.getName();
        for (EEnumLiteral enumLiteral : eenum.getELiterals()) {
            Enumerator instance = enumLiteral.getInstance();
            String value = instance.getLiteral();
            valueList.add(value);
        }
        returnMap.put(name, valueList.toString());
        return returnMap;
    }

    /**
     * Gets the attributes.
     *
     * @param className the class name
     * @param dependency the dependency
     * @param root the root
     */
    public void getAttributes(String className, String dependency, EPackage root) {
        List<String> dpendList = new ArrayList<>();
        if (dependency != null) {
            dpendList = new ArrayList<>(Arrays.asList(dependency.split(",")));
        }
        MsAttributeObject msAttributeObject = new MsAttributeObject();
        msAttributeObject.setClassName(className);
        String extendClass = getSubTypes(root, className);
        Map<String, String> returnRefList = getRefAttributeList(root, className, extendClass);
        Map<String, String> returnAttributeList = getAttributeList(root, className, extendClass);
        Map<String, Object> returnSubList = getSubAttributeList(root, className, extendClass);
        HashMap<String, String> returnAnnotation = getAnnotation(root, className, extendClass);
        msAttributeObject.setAttribute(returnAttributeList);
        msAttributeObject.setRefAttribute(returnRefList);
        msAttributeObject.setSubClass(returnSubList);
        msAttributeObject.setDependency(dpendList.toString());
        msAttributeObject.addMatchingSet(returnAnnotation);
        msAttributeObject.setPolicyTempalate(isPolicyTemplate(root, className));

        this.classMap.put(className, msAttributeObject);
    }

    /**
     * Gets the annotation.
     *
     * @param root the root
     * @param className the class name
     * @param extendClass the extend class
     * @return the annotation
     */
    private HashMap<String, String> getAnnotation(EPackage root, String className, String extendClass) {
        TreeIterator<EObject> treeItr = root.eAllContents();
        boolean requiredAttribute = false;
        boolean requiredMatchAttribute = false;
        HashMap<String, String> annotationSet = new HashMap<>();

        // Pulling out dependency from file
        while (treeItr.hasNext()) {
            EObject obj = treeItr.next();
            if (obj instanceof EClassifier) {
                requiredAttribute = isRequiredAttribute(obj, className);
                requiredMatchAttribute = isRequiredAttribute(obj, extendClass);
            }

            if (requiredAttribute) {
                if (obj instanceof EStructuralFeature) {
                    checkAnnotation(annotationSet, (EStructuralFeature) obj);
                }
            } else if (requiredMatchAttribute && (obj instanceof EStructuralFeature)) {
                findMatchingAnnotation(annotationSet, obj);
            }
        }
        return annotationSet;
    }

    /**
     * Find matching annotation.
     *
     * @param annotationSet the annotation set
     * @param obj the obj
     */
    private void findMatchingAnnotation(HashMap<String, String> annotationSet, EObject obj) {
        EStructuralFeature estrucClassifier = (EStructuralFeature) obj;
        if (estrucClassifier.getEAnnotations().isEmpty()) {
            return;
        }
        String matching = annotationValue(estrucClassifier, AnnotationType.MATCHING, policy);
        if (matching != null) {
            if (obj instanceof EReference) {
                EClass refType = ((EReference) obj).getEReferenceType();
                annotationSet.put(refType.getName(), matching);
                matchingClass.put(refType.getName(), matching);
            } else {
                annotationSet.put(estrucClassifier.getName(), matching);
            }
        }

    }

    /**
     * Check annotation.
     *
     * @param annotationSet the annotation set
     * @param obj the obj
     */
    private void checkAnnotation(HashMap<String, String> annotationSet, EStructuralFeature obj) {
        EStructuralFeature estrucClassifier = obj;
        if (estrucClassifier.getEAnnotations().isEmpty()) {
            return;
        }
        String matching = annotationValue(estrucClassifier, AnnotationType.MATCHING, policy);
        if (matching != null) {
            annotationSet.put(estrucClassifier.getName(), matching);
        }
        String range = annotationValue(estrucClassifier, AnnotationType.VALIDATION, policy);
        if (range != null) {
            annotationSet.put(estrucClassifier.getName(), range);
        }
        String annotationDict = annotationValue(estrucClassifier, AnnotationType.DICTIONARY, policy);
        if (annotationDict != null) {
            annotationSet.put(estrucClassifier.getName(), annotationDict);
        }
    }

    /**
     * Gets the sub attribute list.
     *
     * @param root the root
     * @param className the class name
     * @param superClass the super class
     * @return the sub attribute list
     */
    private Map<String, Object> getSubAttributeList(EPackage root, String className, String superClass) {
        TreeIterator<EObject> treeItr = root.eAllContents();
        boolean requiredAttribute = false;
        Map<String, Object> subAttribute = new HashMap<>();
        int rollingCount = 0;
        int processClass = 0;

        // Pulling out dependency from file
        while (treeItr.hasNext() && rollingCount < 2) {

            EObject obj = treeItr.next();
            if (obj instanceof EClassifier) {
                requiredAttribute = isRequiredAttribute(obj, className) || isRequiredAttribute(obj, superClass);
                if (requiredAttribute) {
                    processClass++;
                }
                rollingCount = rollingCount + processClass;
            }

            if (requiredAttribute && (obj instanceof EStructuralFeature)) {
                EStructuralFeature estrucClassifier = (EStructuralFeature) obj;
                if (!estrucClassifier.getEAnnotations().isEmpty()) {
                    updateSubAttributes(subAttribute, obj, estrucClassifier);
                }
            }
        }
        return subAttribute;
    }

    /**
     * Update sub attributes.
     *
     * @param subAttribute the sub attribute
     * @param obj the obj
     * @param estrucClassifier the e struc classifier
     */
    private void updateSubAttributes(Map<String, Object> subAttribute, EObject obj,
                    EStructuralFeature estrucClassifier) {
        if (!(obj instanceof EReference)) {
            return;
        }
        if (annotationTest(estrucClassifier, CONFIGURATION, onap)) {
            EClass refType = ((EReference) obj).getEReferenceType();
            if (!refType.toString().contains(E_PROXY_URI)) {
                String required = REQUIREDFALSE;
                if (estrucClassifier.getLowerBound() == 1) {
                    required = REQUIREDTRUE;
                }
                subAttribute.put(estrucClassifier.getName(), refType.getName() + required);
            }
        }
    }

    /**
     * Check defult value.
     *
     * @param defultValue the defult value
     * @return the string
     */
    public String checkDefultValue(String defultValue) {
        if (defultValue != null) {
            return DEFAULTVALUE + defultValue;
        }
        return ":defaultValue-NA";

    }

    /**
     * Check required pattern.
     *
     * @param upper the upper
     * @param lower the lower
     * @return the string
     */
    public String checkRequiredPattern(int upper, int lower) {
        String pattern = XACMLProperties.getProperty(XacmlRestProperties.PROP_XCORE_REQUIRED_PATTERN);
        if (pattern != null && upper == Integer.parseInt(pattern.split(",")[1])
                        && lower == Integer.parseInt(pattern.split(",")[0])) {
            return REQUIREDTRUE;
        }
        return REQUIREDFALSE;
    }

    /**
     * Builds the java object.
     *
     * @param map the map
     * @return the JSON object
     */
    public JSONObject buildJavaObject(Map<String, String> map) {
        return new JSONObject(map);
    }

    /**
     * Gets the ref attribute list.
     *
     * @param root the root
     * @param className the class name
     * @param superClass the super class
     * @return the ref attribute list
     */
    public Map<String, String> getRefAttributeList(EPackage root, String className, String superClass) {

        TreeIterator<EObject> treeItr = root.eAllContents();
        boolean requiredAttribute = false;
        HashMap<String, String> refAttribute = new HashMap<>();
        int rollingCount = 0;
        int processClass = 0;
        boolean annotation;
        // Pulling out dependency from file
        while (treeItr.hasNext()) {
            EObject obj = treeItr.next();
            if (obj instanceof EClassifier) {
                requiredAttribute = isRequiredAttribute(obj, className) || isRequiredAttribute(obj, superClass);
                if (requiredAttribute) {
                    processClass++;
                }
                rollingCount = rollingCount + processClass;
            }

            if (requiredAttribute && (obj instanceof EStructuralFeature)) {
                EStructuralFeature estrucClassifier = (EStructuralFeature) obj;
                if (!estrucClassifier.getEAnnotations().isEmpty()) {
                    annotation = annotationTest(estrucClassifier, CONFIGURATION, onap);
                    if (annotation && obj instanceof EReference) {
                        updRefAttributes(refAttribute, (EStructuralFeature) obj, estrucClassifier);
                    } else if (annotation && obj instanceof EAttributeImpl) {
                        updEnumTypeRefAttrib(refAttribute, (EStructuralFeature) obj, estrucClassifier);
                    }
                }
            }
        }

        return refAttribute;
    }

    /**
     * Upd enum type ref attrib.
     *
     * @param refAttribute the ref attribute
     * @param obj the obj
     * @param estrucClassifier the e struc classifier
     */
    private void updEnumTypeRefAttrib(HashMap<String, String> refAttribute, EStructuralFeature obj,
                    EStructuralFeature estrucClassifier) {
        EClassifier refType = ((EAttributeImpl) obj).getEType();
        if (!(refType instanceof EEnumImpl)) {
            return;
        }

        String array = arrayCheck(obj.getUpperBound());
        String required = REQUIREDFALSE;
        if (obj.getLowerBound() == 1) {
            required = REQUIREDTRUE;
        }
        refAttribute.put(estrucClassifier.getName(), refType.getName() + array + required);
    }

    /**
     * Upd ref attributes.
     *
     * @param refAttribute the ref attribute
     * @param obj the obj
     * @param estrucClassifier the e struc classifier
     */
    private void updRefAttributes(HashMap<String, String> refAttribute, EStructuralFeature obj,
                    EStructuralFeature estrucClassifier) {
        EClass refType = ((EReference) obj).getEReferenceType();
        if (refType.toString().contains(E_PROXY_URI)) {
            String one = refType.toString().split(E_PROXY_URI)[1];
            String refValue = StringUtils.replaceEach(one.split("#")[1], new String[]
                { "//", ")" }, new String[]
                { "", "" });
            refAttribute.put(estrucClassifier.getName(), refValue);
        } else {
            String required = REQUIREDFALSE;
            if (obj.getLowerBound() == 1) {
                required = REQUIREDTRUE;
            }
            refAttribute.put(estrucClassifier.getName(),
                            refType.getName() + arrayCheck(obj.getUpperBound()) + required);
        }
    }

    /**
     * Annotation test.
     *
     * @param estrucClassifier the e struc classifier
     * @param annotation the annotation
     * @param type the type
     * @return true, if successful
     */
    private boolean annotationTest(EStructuralFeature estrucClassifier, String annotation, String type) {
        String annotationType;
        EAnnotation eannotation;
        String onapType;
        String onapValue;

        EList<EAnnotation> value = estrucClassifier.getEAnnotations();

        for (int i = 0; i < value.size(); i++) {
            annotationType = value.get(i).getSource();
            eannotation = estrucClassifier.getEAnnotations().get(i);
            onapType = eannotation.getDetails().get(0).getValue();
            onapValue = eannotation.getDetails().get(0).getKey();

            if (annotationType.contains(type) && onapType.contains(annotation)) {
                return true;
            }

            if (annotationType.contains(type) && onapValue.contains(annotation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Annotation value.
     *
     * @param estrucClassifier the e struc classifier
     * @param annotation the annotation
     * @param type the type
     * @return the string
     */
    private String annotationValue(EStructuralFeature estrucClassifier, AnnotationType annotation, String type) {
        String annotationType;
        EAnnotation eannotation;
        String onapType;
        String onapValue = null;

        EList<EAnnotation> value = estrucClassifier.getEAnnotations();

        for (int i = 0; i < value.size(); i++) {
            annotationType = value.get(i).getSource();
            eannotation = estrucClassifier.getEAnnotations().get(i);
            onapType = eannotation.getDetails().get(0).getKey();
            if (annotationType.contains(type) && onapType.compareToIgnoreCase(annotation.toString()) == 0) {
                onapValue = eannotation.getDetails().get(0).getValue();
                if (annotation == AnnotationType.VALIDATION) {
                    return onapValue;
                } else {
                    return onapType + "-" + onapValue;
                }
            }
        }

        return onapValue;
    }

    /**
     * Checks if is required attribute.
     *
     * @param obj the obj
     * @param className the class name
     * @return true, if is required attribute
     */
    public boolean isRequiredAttribute(EObject obj, String className) {
        EClassifier eclassifier = (EClassifier) obj;
        String workingClass = eclassifier.getName().trim();
        return workingClass.equalsIgnoreCase(className);
    }

    /**
     * Checks if is policy template.
     *
     * @param root the root
     * @param className the class name
     * @return true, if is policy template
     */
    private boolean isPolicyTemplate(EPackage root, String className) {
        boolean result = false;
        for (EClassifier classifier : root.getEClassifiers()) {
            if (classifier instanceof EClass) {
                EClass eclass = (EClass) classifier;
                if (eclass.getName().contentEquals(className)) {
                    result = checkPolicyTemplate(eclass);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Check policy template.
     *
     * @param eclass the e class
     * @return true, if successful
     */
    private boolean checkPolicyTemplate(EClass eclass) {
        EList<EAnnotation> value = eclass.getEAnnotations();
        for (EAnnotation workingValue : value) {
            EMap<String, String> keyMap = workingValue.getDetails();
            if (keyMap.containsKey("policyTemplate")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the sub types.
     *
     * @param root the root
     * @param className the class name
     * @return the sub types
     */
    private String getSubTypes(EPackage root, String className) {
        String returnSubTypes = null;
        for (EClassifier classifier : root.getEClassifiers()) {
            if (classifier instanceof EClass) {
                returnSubTypes = findSubTypes(className, returnSubTypes, (EClass) classifier);
            }
        }
        return returnSubTypes;
    }

    /**
     * Find sub types.
     *
     * @param className the class name
     * @param returnSubTypes the return sub types
     * @param classifier the classifier
     * @return the string
     */
    private String findSubTypes(String className, String returnSubTypes, EClass classifier) {
        EClass eclass = classifier;

        for (EClass esuperType : eclass.getEAllSuperTypes()) {
            if (eclass.getName().contentEquals(className)) {
                returnSubTypes = esuperType.getName();
            }
        }
        return returnSubTypes;
    }

    /**
     * Gets the attribute list.
     *
     * @param root the root
     * @param className the class name
     * @param superClass the super class
     * @return the attribute list
     */
    public Map<String, String> getAttributeList(EPackage root, String className, String superClass) {

        TreeIterator<EObject> treeItr = root.eAllContents();
        boolean requiredAttribute = false;
        HashMap<String, String> refAttribute = new HashMap<>();

        // Pulling out dependency from file
        while (treeItr.hasNext()) {
            EObject obj = treeItr.next();
            if (obj instanceof EClassifier) {
                requiredAttribute = isRequiredAttribute(obj, className) || isRequiredAttribute(obj, superClass);
            }

            if (requiredAttribute && (obj instanceof EStructuralFeature)) {
                EStructuralFeature estrucClassifier = (EStructuralFeature) obj;
                if (!estrucClassifier.getEAnnotations().isEmpty()) {
                    checkStrucClassifier(refAttribute, obj, estrucClassifier);
                }
            }
        }
        return refAttribute;

    }

    /**
     * Check struc classifier.
     *
     * @param refAttribute the ref attribute
     * @param obj the obj
     * @param estrucClassifier the e struc classifier
     */
    private void checkStrucClassifier(HashMap<String, String> refAttribute, EObject obj,
                    EStructuralFeature estrucClassifier) {
        EClassifier refType = ((EStructuralFeature) obj).getEType();
        boolean annotation = annotationTest(estrucClassifier, CONFIGURATION, onap);
        boolean dictionaryTest = annotationTest(estrucClassifier, DICTIONARY, policy);
        if (annotation && !(obj instanceof EReference) && !(refType instanceof EEnumImpl)) {
            updEReferenceAttrib(refAttribute, dictionaryTest, (EStructuralFeature) obj, estrucClassifier);
        }
    }

    /**
     * Upd E reference attrib.
     *
     * @param refAttribute the ref attribute
     * @param dictionaryTest the dictionary test
     * @param obj the obj
     * @param estrucClassifier the e struc classifier
     */
    private void updEReferenceAttrib(HashMap<String, String> refAttribute, boolean dictionaryTest,
                    EStructuralFeature obj, EStructuralFeature estrucClassifier) {
        String etype;
        String name = estrucClassifier.getName();
        if (dictionaryTest) {
            etype = annotationValue(estrucClassifier, AnnotationType.DICTIONARY, policy);
        } else {
            etype = estrucClassifier.getEType().getInstanceClassName();
        }
        String defaultValue = checkDefultValue(obj.getDefaultValueLiteral());
        String array = arrayCheck(obj.getUpperBound());
        String required = checkRequiredPattern(obj.getUpperBound(), obj.getLowerBound());
        refAttribute.put(name, etype + defaultValue + required + array);
    }

    /**
     * Array check.
     *
     * @param upperBound the upper bound
     * @return the string
     */
    public String arrayCheck(int upperBound) {

        if (upperBound == -1) {
            return MANYTRUE;
        }

        return MANYFALSE;
    }

    /**
     * Gets the dependency list.
     *
     * @param eclassifier the e classifier
     * @return the dependency list
     */
    public List<String> getDependencyList(EClassifier eclassifier) {
        List<String> returnValue = new ArrayList<>();
        ;
        EList<EClass> somelist = ((EClass) eclassifier).getEAllSuperTypes();
        if (somelist.isEmpty()) {
            return returnValue;
        }
        for (EClass depend : somelist) {
            if (depend.toString().contains(E_PROXY_URI)) {
                String one = depend.toString().split(E_PROXY_URI)[1];
                String value = StringUtils.replaceEach(one.split("#")[1], new String[]
                    { "//", ")" }, new String[]
                    { "", "" });
                returnValue.add(value);
            }
        }

        return returnValue;
    }

    /**
     * Builds the sub list.
     *
     * @param subClassAttributes the sub class attributes
     * @param classMap the class map
     * @param className the class name
     * @return the map
     */
    public Map<String, String> buildSubList(Map<String, String> subClassAttributes,
                    Map<String, MsAttributeObject> classMap, String className) {
        Map<String, String> missingValues = new HashMap<>();
        Map<String, String> workingMap;
        boolean enumType;

        for (Entry<String, String> map : classMap.get(className).getRefAttribute().entrySet()) {
            String value = map.getValue().split(":")[0];
            if (value != null) {
                classMap.get(className).getEnumType();
                enumType = classMap.get(className).getEnumType().containsKey(value);
                if (!enumType) {
                    workingMap = classMap.get(value).getRefAttribute();
                    for (Entry<String, String> subMab : workingMap.entrySet()) {
                        String value2 = subMab.getValue().split(":")[0];
                        if (!subClassAttributes.containsValue(value2)) {
                            missingValues.put(subMab.getKey(), subMab.getValue());
                        }
                    }

                }
            }
        }

        return missingValues;
    }

    /**
     * Recursive reference.
     *
     * @param classMap the class map
     * @param className the class name
     * @return the map
     */
    public Map<String, Map<String, String>> recursiveReference(Map<String, MsAttributeObject> classMap,
                    String className) {

        Map<String, Map<String, String>> returnObject = new HashMap<>();
        Map<String, String> returnClass = getRefclass(classMap, className);
        returnObject.put(className, returnClass);
        for (Entry<String, String> reAttribute : returnClass.entrySet()) {
            if (reAttribute.getValue().split(":")[1].contains("MANY")
                            && classMap.get(reAttribute.getValue().split(":")[0]) != null) {
                returnObject.putAll(recursiveReference(classMap, reAttribute.getValue().split(":")[0]));
            }

        }

        return returnObject;

    }

    /**
     * Creates the json.
     *
     * @param classMap the class map
     * @param className the class name
     * @return the string
     */
    public String createJson(Map<String, MsAttributeObject> classMap, String className) {
        boolean enumType;
        Map<String, Map<String, String>> myObject = new HashMap<>();
        for (Entry<String, String> map : classMap.get(className).getRefAttribute().entrySet()) {
            String value = map.getValue().split(":")[0];
            if (value != null) {
                enumType = classMap.get(className).getEnumType().containsKey(value);
                if (!enumType && map.getValue().split(":")[1].contains("MANY")) {
                    Map<String, Map<String, String>> testRecursive = recursiveReference(classMap,
                                    map.getValue().split(":")[0]);
                    myObject.putAll(testRecursive);
                }
            }
        }

        Gson gson = new Gson();
        return gson.toJson(myObject);
    }

    /**
     * Gets the refclass.
     *
     * @param classMap the class map
     * @param className the class name
     * @return the refclass
     */
    public Map<String, String> getRefclass(Map<String, MsAttributeObject> classMap, String className) {
        HashMap<String, String> missingValues = new HashMap<>();

        if (classMap.get(className).getAttribute() != null || !classMap.get(className).getAttribute().isEmpty()) {
            missingValues.putAll(classMap.get(className).getAttribute());
        }

        if (classMap.get(className).getRefAttribute() != null || !classMap.get(className).getRefAttribute().isEmpty()) {
            missingValues.putAll(classMap.get(className).getRefAttribute());
        }

        return missingValues;
    }

    /**
     * Creates the sub attributes.
     *
     * @param dependency the dependency
     * @param classMap the class map
     * @param modelName the model name
     * @return the string
     */
    public String createSubAttributes(List<String> dependency, Map<String, MsAttributeObject> classMap,
                    String modelName) {

        HashMap<String, Object> workingMap = new HashMap<>();
        MsAttributeObject tempObject;
        if (dependency != null) {
            if (dependency.isEmpty()) {
                return "{}";
            }
            dependency.add(modelName);
            for (String element : dependency) {
                tempObject = classMap.get(element);
                if (tempObject != null) {
                    workingMap.putAll(classMap.get(element).getSubClass());
                }
            }
        }

        return createJson(classMap, modelName);
    }

    /**
     * Gets the full dependency list.
     *
     * @param dependency the dependency
     * @param classMap the class map
     * @return the full dependency list
     */
    public List<String> getFullDependencyList(List<String> dependency, Map<String, MsAttributeObject> classMap) {
        ArrayList<String> returnList = new ArrayList<>();
        ArrayList<String> workingList;
        returnList.addAll(dependency);
        for (String element : dependency) {
            if (classMap.containsKey(element)) {
                MsAttributeObject value = classMap.get(element);
                String rawValue = StringUtils.replaceEach(value.getDependency(), new String[]
                    { "[", "]" }, new String[]
                    { "", "" });
                workingList = new ArrayList<>(Arrays.asList(rawValue.split(",")));
                for (String depend : workingList) {
                    updDependencyList(returnList, depend);
                }
            }
        }

        return returnList;
    }

    /**
     * Upd dependency list.
     *
     * @param returnList the return list
     * @param depend the depend
     */
    private void updDependencyList(ArrayList<String> returnList, String depend) {
        if (!returnList.contains(depend) && !depend.isEmpty()) {
            returnList.add(depend.trim());
        }
    }

    /**
     * Parses the TOSCA model.
     *
     * @param fileName the file name
     * @return the string
     */
    public String parseTosca(String fileName) {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            map = load(fileName);
            if (map != null && map.get(ERROR) != null) {
                return map.get(ERROR);
            }
            parseDataAndPolicyNodes(map);
            LinkedHashMap<String, String> dataMapForJson = parseDataNodes(map);
            constructJsonForDataFields(dataMapForJson);
            LinkedHashMap<String, LinkedHashMap<String, String>> mapKey = parsePolicyNodes(map);
            createAttributes(mapKey);

        } catch (IOException e) {
            logger.error(e);
        } catch (ParserException e) {
            logger.error(e);
            return e.getMessage();
        }

        return null;
    }

    /**
     * Load.
     *
     * @param fileName the file name
     * @return the map
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserException the parser exception
     */
    public Map<String, String> load(String fileName) throws IOException, ParserException {
        File newConfiguration = new File(fileName);
        StringBuilder orderInfo = new StringBuilder("[");
        Yaml yaml = new Yaml();
        LinkedHashMap<Object, Object> yamlMap = null;
        try (InputStream is = new FileInputStream(newConfiguration)) {
            yamlMap = yaml.load(is);
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
            throw new ParserException("Invalid TOSCA Model format. Please make sure it is a valid YAML file");
        }

        LinkedHashMap<String, String> settings = new LinkedHashMap<>();
        if (yamlMap == null) {
            return settings;
        }

        String message = validations(yamlMap);

        if (message != null) {
            settings.put(ERROR, message);
            return settings;
        }

        findNode(yamlMap);

        if (!isDuplicatedAttributes && orderedElements != null && !orderedElements.isEmpty()) {
            orderedElements.stream().forEach(string -> {
                orderInfo.append(string);
                orderInfo.append(",");
                logger.info("Content: " + string);
            });

            orderInfo.append("]");

            dataOrderInfo = orderInfo.toString();
            dataOrderInfo = dataOrderInfo.replace(",]", "]");

            logger.info("dataOrderInfo :" + dataOrderInfo);
        }

        List<String> path = new ArrayList<>();
        serializeMap(settings, new StringBuilder(), path, yamlMap);
        return settings;
    }

    /**
     * Validations.
     *
     * @param yamlMap the yaml map
     * @return the string
     */
    @SuppressWarnings("unchecked")
    private String validations(@SuppressWarnings("rawtypes") Map yamlMap) {

        boolean isNoteTypeFound = false;
        boolean isDataTypeFound = false;
        boolean isToscaVersionKeyFound = false;
        boolean isToscaVersionValueFound = false;
        @SuppressWarnings("rawtypes")
        Map m1 = new HashMap();
        short order = 0;
        if (yamlMap != null) {
            // Get a set of the entries
            @SuppressWarnings("rawtypes")
            Set<Entry> entries = yamlMap.entrySet();
            for (Map.Entry<Object, Object> me : entries) {
                if (TOSCA_SIMPLE_YAML_1_0_0.equals(me.getValue())) {
                    isToscaVersionValueFound = true;
                }

                switch (me.getKey().toString()) {
                    case TOSCA_DEFINITION_VERSION:
                        isToscaVersionKeyFound = true;
                        order++;
                        m1.put(TOSCA_DEFINITION_VERSION, order);
                        break;
                    case NODE_TYPE:
                        isNoteTypeFound = true;
                        order++;
                        m1.put(NODE_TYPE, order);
                        break;
                    case DATA_TYPE:
                        isDataTypeFound = true;
                        order++;
                        m1.put(DATA_TYPE, order);
                        break;
                    case JSON_MODEL:
                        setJsonRuleFormation(me.getValue().toString());
                        break;
                    default:
                        break;
                }
            }
            if (!isDataTypeFound) {
                return "data_types are missing or invalid.";
            }
            if (!isToscaVersionKeyFound || !isToscaVersionValueFound) {
                return "tosca_definitions_version is missing or invalid.";
            }

            if (!isNoteTypeFound) {
                return "node_types are missing or invalid.";
            }

            short version = (short) m1.get(TOSCA_DEFINITION_VERSION);

            if (version > 1) {
                return "tosca_definitions_version should be defined first.";
            }

            short data = (short) m1.get(DATA_TYPE);
            short node = (short) m1.get(NODE_TYPE);
            if (isDataTypeFound && node > data) {
                return "node_types should be defined before data_types.";
            }

        }

        return null;
    }

    /**
     * Serialize map.
     *
     * @param settings the settings
     * @param sb the sb
     * @param path the path
     * @param yamlMap the yaml map
     */
    @SuppressWarnings(
        { "unchecked", "rawtypes" })
    private void serializeMap(LinkedHashMap<String, String> settings, StringBuilder sb, List<String> path,
                    Map<Object, Object> yamlMap) {
        for (Map.Entry<Object, Object> entry : yamlMap.entrySet()) {

            if (entry.getValue() instanceof Map) {
                path.add((String) entry.getKey());
                serializeMap(settings, sb, path, (Map<Object, Object>) entry.getValue());
                path.remove(path.size() - 1);
            } else if (entry.getValue() instanceof List) {
                path.add((String) entry.getKey());
                serializeList(settings, sb, path, (List) entry.getValue());
                path.remove(path.size() - 1);
            } else {
                serializeValue(settings, sb, path, (String) entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Serialize list.
     *
     * @param settings the settings
     * @param sb the sb
     * @param path the path
     * @param yamlList the yaml list
     */
    @SuppressWarnings("unchecked")
    private void serializeList(LinkedHashMap<String, String> settings, StringBuilder sb, List<String> path,
                    List<String> yamlList) {
        int counter = 0;
        for (Object listEle : yamlList) {
            if (listEle instanceof Map) {
                path.add(Integer.toString(counter));
                serializeMap(settings, sb, path, (Map<Object, Object>) listEle);
                path.remove(path.size() - 1);
            } else if (listEle instanceof List) {
                path.add(Integer.toString(counter));
                serializeList(settings, sb, path, (List<String>) listEle);
                path.remove(path.size() - 1);
            } else {
                serializeValue(settings, sb, path, Integer.toString(counter), listEle);
            }
            counter++;
        }
    }

    /**
     * Serialize value.
     *
     * @param settings the settings
     * @param sb the sb
     * @param path the path
     * @param name the name
     * @param value the value
     */
    private void serializeValue(LinkedHashMap<String, String> settings, StringBuilder sb, List<String> path,
                    String name, Object value) {
        if (value == null) {
            return;
        }
        sb.setLength(0);
        for (String pathEle : path) {
            sb.append(pathEle).append('.');
        }
        sb.append(name);
        settings.put(sb.toString(), value.toString());
    }

    /**
     * Parses the data and policy nodes.
     *
     * @param map the map
     */
    void parseDataAndPolicyNodes(Map<String, String> map) {
        for (String key : map.keySet()) {
            if (key.contains("policy.nodes.Root")) {
                continue;
            } else if (key.contains("policy.nodes")) {
                String wordToFind = "policy.nodes.";
                int indexForPolicyNode = key.indexOf(wordToFind);
                String subNodeString = key.substring(indexForPolicyNode + 13, key.length());

                stringBetweenDots(subNodeString);
            } else if (key.contains("policy.data")) {
                String wordToFind = "policy.data.";
                int indexForPolicyNode = key.indexOf(wordToFind);
                String subNodeString = key.substring(indexForPolicyNode + 12, key.length());

                stringBetweenDotsForDataFields(subNodeString);
            }
        }
    }

    /**
     * String between dots.
     *
     * @param str the str
     * @return the int
     */
    // Second index of dot should be returned.
    public int stringBetweenDots(String str) {
        String stringToSearch = str;
        String[] ss = stringToSearch.split("\\.");
        if (ss != null) {
            int len = ss.length;
            if (len > 2) {
                uniqueKeys.add(ss[2]);
            }
        }

        return uniqueKeys.size();
    }

    /**
     * String between dots for data fields.
     *
     * @param str the str
     */
    public void stringBetweenDotsForDataFields(String str) {
        String stringToSearch = str;
        String[] ss = stringToSearch.split("\\.");
        if (ss != null) {
            int len = ss.length;

            if (len > 2) {
                uniqueDataKeys.add(ss[0] + "%" + ss[2]);
            }
        }
    }

    /**
     * Construct json for data fields.
     *
     * @param dataMapForJson the data map for json
     */
    void constructJsonForDataFields(LinkedHashMap<String, String> dataMapForJson) {
        LinkedHashMap<String, LinkedHashMap<String, String>> dataMapKey = new LinkedHashMap<>();
        LinkedHashMap<String, String> hmSub;
        for (Map.Entry<String, String> entry : dataMapForJson.entrySet()) {
            String uniqueDataKey = entry.getKey();
            String[] uniqueDataKeySplit = uniqueDataKey.split("%");
            String value = dataMapForJson.get(uniqueDataKey);
            if (dataMapKey.containsKey(uniqueDataKeySplit[0])) {
                hmSub = dataMapKey.get(uniqueDataKeySplit[0]);
                hmSub.put(uniqueDataKeySplit[1], value);
            } else {
                hmSub = new LinkedHashMap<>();
                hmSub.put(uniqueDataKeySplit[1], value);
            }

            dataMapKey.put(uniqueDataKeySplit[0], hmSub);
        }

        JSONObject mainObject = new JSONObject();
        JSONObject json;
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : dataMapKey.entrySet()) {
            String keyString = entry.getKey();
            json = new JSONObject();
            HashMap<String, String> jsonHm = dataMapKey.get(keyString);
            for (Map.Entry<String, String> entryMap : jsonHm.entrySet()) {
                String key = entryMap.getKey();
                json.put(key, jsonHm.get(key));
            }
            mainObject.put(keyString, json);
        }
        Iterator<String> keysItr = mainObject.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            String value = mainObject.get(key).toString();
            retmap.put(key, value);
        }

        logger.info("#############################################################################");
        logger.info(mainObject);
        logger.info("###############################################################################");
    }

    /**
     * Parses the data nodes.
     *
     * @param map the map
     * @return the linked hash map
     */
    LinkedHashMap<String, String> parseDataNodes(Map<String, String> map) {
        LinkedHashMap<String, String> dataMapForJson = new LinkedHashMap<>();
        matchableValues = new HashMap<>();
        for (String uniqueDataKey : uniqueDataKeys) {
            if (uniqueDataKey.contains("%")) {
                String[] uniqueDataKeySplit = uniqueDataKey.split("%");
                String findType = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1] + TYPE;
                String typeValue = map.get(findType);
                logger.info(typeValue);

                String findRequired = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1] + REQUIRED;
                String requiredValue = map.get(findRequired);

                String matchable = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1] + MATCHABLE;

                String matchableValue = map.get(matchable);

                if ("true".equalsIgnoreCase(matchableValue)) {
                    String key = uniqueDataKeySplit[uniqueDataKeySplit.length - 1];
                    matchableValues.put(key, MATCHINGTRUE);
                }

                if (requiredValue == null || requiredValue.isEmpty()) {
                    requiredValue = "false";
                }
                if (INTEGER.equalsIgnoreCase(typeValue) || STRING.equalsIgnoreCase(typeValue)
                                || typeValue.equalsIgnoreCase(BOOLEAN)) {
                    String findDefault = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1]
                                    + DEFAULT;
                    String defaultValue = map.get(findDefault);
                    logger.info("defaultValue is:" + defaultValue);
                    logger.info("requiredValue is:" + requiredValue);

                    StringBuilder attributeIndividualStringBuilder = new StringBuilder();
                    attributeIndividualStringBuilder.append(typeValue + DEFAULTVALUE);
                    attributeIndividualStringBuilder.append(defaultValue + REQUIREDVALUE);
                    attributeIndividualStringBuilder.append(requiredValue + MANYFALSE);

                    String findDescription = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1]
                                    + DESCRIPTION;
                    attributeIndividualStringBuilder.append(DESCRIPTION_TOKEN + map.get(findDescription));
                    dataMapForJson.put(uniqueDataKey, attributeIndividualStringBuilder.toString());
                } else if (LIST.equalsIgnoreCase(typeValue) || MAP.equalsIgnoreCase(typeValue)) {
                    logger.info("requiredValue is:" + requiredValue);
                    String findList = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1]
                                    + ".entry_schema.type";
                    String findDefaultValue = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1]
                                    + ".entry_schema.default";
                    String findDescription = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES + uniqueDataKeySplit[1]
                                    + ".entry_schema.description";
                    String listValue = map.get(findList);
                    String defaultValue = map.get(findDefaultValue);
                    String description = map.get(findDescription);
                    if (listValue != null) {
                        logger.info("Type of list is:" + listValue);
                        // Its userdefined
                        if (listValue.contains(".")) {
                            String trimValue = listValue.substring(listValue.lastIndexOf('.') + 1);
                            StringBuilder referenceIndividualStringBuilder = new StringBuilder();
                            referenceIndividualStringBuilder.append(trimValue + REQUIREDVALUE);
                            referenceIndividualStringBuilder.append(requiredValue + MANYTRUE);
                            referenceIndividualStringBuilder.append(DESCRIPTION_TOKEN + description);
                            dataMapForJson.put(uniqueDataKey, referenceIndividualStringBuilder.toString());
                        } else { // Its string
                            StringBuilder stringListItems = new StringBuilder();
                            if (LIST.equalsIgnoreCase(typeValue)) {
                                stringListItems.append(uniqueDataKeySplit[1].toUpperCase() + DEFAULTVALUE + defaultValue
                                                + REQUIREDVALUE + requiredValue + MANYFALSE + DESCRIPTION_TOKEN
                                                + description);
                            } else if (MAP.equalsIgnoreCase(typeValue)) {
                                stringListItems.append(uniqueDataKeySplit[1].toUpperCase() + DEFAULTVALUE + defaultValue
                                                + REQUIREDVALUE + requiredValue + MANYTRUE + DESCRIPTION_TOKEN
                                                + description);
                            }
                            dataMapForJson.put(uniqueDataKey, stringListItems.toString());
                            dataListBuffer.append(uniqueDataKeySplit[1].toUpperCase() + "=[");
                            for (int i = 0; i < 10; i++) {
                                String findConstraints = DATATYPE + uniqueDataKeySplit[0] + PROPERTIES
                                                + uniqueDataKeySplit[1] + ".entry_schema.constraints.0.valid_values."
                                                + i;
                                String constraintsValue = map.get(findConstraints);
                                logger.info(constraintsValue);
                                boolean ruleCheck = false;
                                if (constraintsValue == null) {
                                    break;
                                } else if (constraintsValue.startsWith(DICTIONARY)) {
                                    List<Object> dictFromDB = null;
                                    String[] dictionaryNameValRule;
                                    String[] dictionaryName = constraintsValue.split(":");
                                    String dictionaryNameVal = dictionaryName[1];
                                    if (dictionaryNameVal.contains("#Rules")) {
                                        ruleCheck = true;
                                        dictionaryNameValRule = dictionaryNameVal.split("#");
                                        dictFromDB = commonClassDao.getDataById(DictionaryData.class, DICTIONARYNAME,
                                                        dictionaryNameValRule[0]);
                                    } else {
                                        dictFromDB = commonClassDao.getDataById(DictionaryData.class, DICTIONARYNAME,
                                                        dictionaryName[1]);
                                    }
                                    if (dictFromDB != null && !dictFromDB.isEmpty()) {
                                        DictionaryData data = (DictionaryData) dictFromDB.get(0);
                                        if (ruleCheck) {
                                            constraintsValue = DICTIONARY + data.getDictionaryUrl() + "@"
                                                            + data.getDictionaryDataByName() + "&Rule";
                                        } else {
                                            constraintsValue = DICTIONARY + data.getDictionaryUrl() + "@"
                                                            + data.getDictionaryDataByName();
                                        }
                                    }
                                    dataListBuffer.append(constraintsValue + ",");
                                } else {
                                    logger.info("constraintsValue => " + constraintsValue);
                                    if (constraintsValue.contains("=")) {
                                        constraintsValue = constraintsValue.replace("=", "equal-sign");
                                    }
                                    dataConstraints.add(constraintsValue);
                                    dataListBuffer.append(constraintsValue + ",");
                                }
                            }
                            dataListBuffer.append("]#");
                            logger.info(dataListBuffer);
                        }
                    }
                } else {
                    String findUserDefined = DATATYPE + uniqueDataKeySplit[0] + "." + PROPERTIES_KEY + "."
                                    + uniqueDataKeySplit[1] + TYPE;
                    String findDescription = DATATYPE + uniqueDataKeySplit[0] + "." + PROPERTIES_KEY + "."
                                    + uniqueDataKeySplit[1] + DESCRIPTION;
                    String userDefinedValue = map.get(findUserDefined);
                    String description = map.get(findDescription);
                    String trimValue = userDefinedValue.substring(userDefinedValue.lastIndexOf('.') + 1);
                    StringBuilder referenceIndividualStringBuilder = new StringBuilder();
                    referenceIndividualStringBuilder.append(trimValue + REQUIREDVALUE);
                    referenceIndividualStringBuilder.append(requiredValue + MANYFALSE);
                    referenceIndividualStringBuilder.append(DESCRIPTION_TOKEN + description);
                    dataMapForJson.put(uniqueDataKey, referenceIndividualStringBuilder.toString());

                }
            } else {
                matchableValues.put(uniqueDataKey, MATCHINGTRUE);
            }
        }

        return dataMapForJson;
    }

    /**
     * Parses the policy nodes.
     *
     * @param map the map
     * @return the linked hash map
     * @throws ParserException the parser exception
     */
    LinkedHashMap<String, LinkedHashMap<String, String>> parsePolicyNodes(Map<String, String> map)
                    throws ParserException {
        LinkedHashMap<String, LinkedHashMap<String, String>> mapKey = new LinkedHashMap<>();
        for (String uniqueKey : uniqueKeys) {
            LinkedHashMap<String, String> hm;

            for (Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key.contains(uniqueKey) && key.contains("policy.nodes")) {
                    if (mapKey.containsKey(uniqueKey)) {
                        hm = mapKey.get(uniqueKey);
                        String keyStr = key.substring(key.lastIndexOf('.') + 1);
                        String valueStr = map.get(key);
                        if ("type".equalsIgnoreCase(keyStr) && key.contains("entry_schema.0.type")
                                        || key.contains("entry_schema.type") && valueStr.contains("policy.data.")) {
                            throw new ParserException("For user defined object type,"
                                            + " Please make sure no space between 'type:' and object " + valueStr);

                        }
                        if ("type".equals(keyStr)) {
                            if (!key.contains("entry_schema")) {
                                hm.put(keyStr, valueStr);
                            }
                        } else {
                            hm.put(keyStr, valueStr);
                        }

                    } else {
                        hm = new LinkedHashMap<>();
                        String keyStr = key.substring(key.lastIndexOf('.') + 1);
                        String valueStr = map.get(key);

                        if (("type").equals(keyStr)) {
                            if (!key.contains("entry_schema")) {
                                hm.put(keyStr, valueStr);
                            }
                        } else {
                            hm.put(keyStr, valueStr);
                        }
                        mapKey.put(uniqueKey, hm);
                    }
                }
            }
        }
        return mapKey;
    }

    /**
     * Creates the attributes.
     *
     * @param mapKey the map key
     */
    private void createAttributes(LinkedHashMap<String, LinkedHashMap<String, String>> mapKey) {
        StringBuilder attributeStringBuilder = new StringBuilder();
        StringBuilder referenceStringBuilder = new StringBuilder();
        StringBuilder listBuffer = new StringBuilder();
        List<String> constraints = new ArrayList<>();
        for (Map.Entry<String, LinkedHashMap<String, String>> entry : mapKey.entrySet()) {
            String keySetString = entry.getKey();
            LinkedHashMap<String, String> keyValues = mapKey.get(keySetString);
            if (keyValues.get("type") != null && (STRING.equalsIgnoreCase(keyValues.get("type"))
                            || INTEGER.equalsIgnoreCase(keyValues.get("type"))
                            || BOOLEAN.equalsIgnoreCase(keyValues.get("type")))) {
                StringBuilder attributeIndividualStringBuilder = new StringBuilder();
                attributeIndividualStringBuilder.append(keySetString + "=");
                attributeIndividualStringBuilder.append(keyValues.get("type") + DEFAULTVALUE);
                attributeIndividualStringBuilder.append(keyValues.get("default") + REQUIREDVALUE);
                attributeIndividualStringBuilder.append(keyValues.get("required") + MANYFALSE);
                attributeIndividualStringBuilder.append(DESCRIPTION_TOKEN + keyValues.get(DESCRIPTION_KEY));
                attributeStringBuilder.append(attributeIndividualStringBuilder + ",");
                if (keyValues.get(MATCHABLEKEY) != null && "true".equalsIgnoreCase(keyValues.get(MATCHABLEKEY))) {
                    matchableValues.put(keySetString, MATCHINGTRUE);
                }
            } else if (LIST.equalsIgnoreCase(keyValues.get("type"))) {
                if ("true".equalsIgnoreCase(keyValues.get(MATCHABLEKEY))) {
                    matchableValues.put(keySetString, MATCHINGTRUE);
                }
                // List Data type
                Set<String> keys = keyValues.keySet();
                Iterator<String> itr = keys.iterator();
                boolean isDefinedType = false;
                while (itr.hasNext()) {
                    String key = itr.next();
                    if ((!("type").equals(key) || ("required").equals(key))) {
                        String value = keyValues.get(key);
                        // The "." in the value determines if its a string or a user defined type.
                        if (!value.contains(".")) {
                            // This is string
                            if (StringUtils.isNumeric(key)) { // only integer key for the value of Constrains
                                constraints.add(keyValues.get(key));
                            }
                        } else {
                            // This is user defined type
                            String trimValue = value.substring(value.lastIndexOf('.') + 1);
                            StringBuilder referenceIndividualStringBuilder = new StringBuilder();
                            referenceIndividualStringBuilder.append(keySetString + "=" + trimValue + MANYTRUE
                                            + DESCRIPTION_TOKEN + keyValues.get(DESCRIPTION_KEY));
                            referenceStringBuilder.append(referenceIndividualStringBuilder + ",");
                            isDefinedType = true;
                        }
                    }

                }

                if (!isDefinedType && LIST.equalsIgnoreCase(keyValues.get("type"))
                                && (constraints == null || constraints.isEmpty())) {
                    referenceStringBuilder.append(keySetString + "=MANY-true" + ",");
                }
            } else {
                // User defined Datatype.
                if ("true".equalsIgnoreCase(keyValues.get(MATCHABLEKEY))) {
                    matchableValues.put(keySetString, MATCHINGTRUE);
                }
                String value = keyValues.get("type");
                if (value != null && !value.isEmpty()) {
                    String trimValue = value.substring(value.lastIndexOf('.') + 1);
                    StringBuilder referenceIndividualStringBuilder = new StringBuilder();
                    referenceIndividualStringBuilder.append(keySetString + "=" + trimValue + MANYFALSE
                                    + DESCRIPTION_TOKEN + keyValues.get(DESCRIPTION_KEY));
                    referenceStringBuilder.append(referenceIndividualStringBuilder + ",");
                } else {
                    logger.info("keyValues.get(type) is null/empty");
                }

            }
            if (constraints != null && !constraints.isEmpty()) {
                // List handling.
                listBuffer.append(keySetString.toUpperCase() + "=[");
                for (String str : constraints) {
                    if (str.contains(DICTIONARY)) {
                        String[] dictionaryName = str.split(":");
                        List<Object> dictFromDB = commonClassDao.getDataById(DictionaryData.class, DICTIONARYNAME,
                                        dictionaryName[1]);
                        if (dictFromDB != null && !dictFromDB.isEmpty()) {
                            DictionaryData data = (DictionaryData) dictFromDB.get(0);
                            str = DICTIONARY + data.getDictionaryUrl() + "@" + data.getDictionaryDataByName();
                        }
                    }
                    listBuffer.append(str + ",");
                }
                listBuffer.append("]#");
                logger.info(listBuffer);

                StringBuilder referenceIndividualStringBuilder = new StringBuilder();
                referenceIndividualStringBuilder.append(keySetString + "=" + keySetString.toUpperCase() + MANYFALSE);
                referenceStringBuilder.append(referenceIndividualStringBuilder + ",");
                constraints.clear();
            }
        }

        dataListBuffer.append(listBuffer);

        logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        logger.info("Whole attribute String is:" + attributeStringBuilder);
        logger.info("Whole reference String is:" + referenceStringBuilder);
        logger.info("List String is:" + listBuffer);
        logger.info("Data list buffer is:" + dataListBuffer);
        logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

        this.listConstraints = dataListBuffer.toString();
        this.referenceAttributes = referenceStringBuilder.toString();
        this.attributeString = attributeStringBuilder.toString();
    }

    /**
     * Find node.
     *
     * @param map the map
     */
    @SuppressWarnings("unchecked")
    private void findNode(LinkedHashMap<Object, Object> map) {

        map.forEach((key, value) -> {
            // if the value is properties and its type is map object, then save all the keys
            if (key.equals(PROPERTIES_KEY) && value instanceof Map) {
                saveNodes((LinkedHashMap<?, ?>) value);
                if (isDuplicatedAttributes) {
                    orderedElements = new ArrayList<>();
                    return;
                }
            }

            if (!"policy.nodes.Root".equals(key) && value instanceof Map) {
                // value is a Map object, then make a recursive call
                findNode((LinkedHashMap<Object, Object>) value);
            }
        });

    }

    /**
     * Save nodes.
     *
     * @param map the map
     */
    private void saveNodes(LinkedHashMap<?, ?> map) {

        for (Entry<?, ?> entry : map.entrySet()) {

            if (orderedElements.indexOf(entry.getKey()) >= 0) { // duplicated attribute names
                isDuplicatedAttributes = true;
                return;
            } else {
                orderedElements.add((String) entry.getKey());
            }
        }
    }
}
