/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.components;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.rest.jpa.OptimizationModels;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.util.MSAttributeObject;
import org.onap.policy.rest.util.MSModelUtils;
import org.onap.policy.rest.util.MSModelUtils.MODEL_TYPE;

public class CreateNewOptimizationModel {
    private static final Logger logger = FlexLogger.getLogger(CreateNewOptimizationModel.class);
    private OptimizationModels newModel = null;
    private HashMap<String, MSAttributeObject> classMap = new HashMap<>();

    private static final String EXTRACTDIR = "ExtractDir";
    private static final String SUCCESS = "success";

    MSModelUtils utils = new MSModelUtils(XACMLPapServlet.getMsOnapName(), XACMLPapServlet.getMsPolicyName());

    public CreateNewOptimizationModel() {
        super();
    }

    public CreateNewOptimizationModel(String importFile, String modelName, String description, String version,
            String randomID) {

        this.newModel = new OptimizationModels();
        this.newModel.setVersion(version);
        this.newModel.setModelName(modelName);
        this.newModel.setDescription(description);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("API");
        this.newModel.setUserCreatedBy(userInfo);
        String cleanUpFile = null;

        Map<String, MSAttributeObject> tempMap = new HashMap<>();
        // Need to delete the file
        if (importFile.contains(".zip")) {
            extractFolder(randomID + ".zip");
            File directory = new File(EXTRACTDIR + File.separator + randomID);
            List<File> fileList = listModelFiles(directory.toString());
            // get all the files from a director
            for (File file : fileList) {
                if (file.isFile()) {
                    processYmlModel(file.toString(), modelName);
                }
            }
            cleanUpFile = EXTRACTDIR + File.separator + randomID + ".zip";
            try {
                FileUtils.deleteDirectory(new File(EXTRACTDIR + File.separator + randomID));
                FileUtils.deleteDirectory(new File(randomID));
                File deleteFile = new File(cleanUpFile);
                FileUtils.forceDelete(deleteFile);
            } catch (IOException e) {
                logger.error("Failed to unzip model file " + randomID, e);
            }
        } else {
            if (importFile.contains(".yml")) {

                processYmlModel(EXTRACTDIR + File.separator + randomID + ".yml", modelName);
                cleanUpFile = EXTRACTDIR + File.separator + randomID + ".yml";

            } else {
                tempMap = utils.processEpackage(EXTRACTDIR + File.separator + randomID + ".xmi", MODEL_TYPE.XMI);
                classMap.putAll(tempMap);
                cleanUpFile = EXTRACTDIR + File.separator + randomID + ".xmi";
            }
            File deleteFile = new File(cleanUpFile);
            deleteFile.delete();
        }
    }

    private void processYmlModel(String fileName, String modelName) {

        try {

            utils.parseTosca(fileName);

            MSAttributeObject msAttributes = new MSAttributeObject();
            msAttributes.setClassName(modelName);

            LinkedHashMap<String, String> returnAttributeList = new LinkedHashMap<>();
            returnAttributeList.put(modelName, utils.getAttributeString());
            msAttributes.setAttribute(returnAttributeList);

            msAttributes.setSubClass(utils.getRetmap());

            msAttributes.setMatchingSet(utils.getMatchableValues());

            LinkedHashMap<String, String> returnReferenceList = new LinkedHashMap<>();

            returnReferenceList.put(modelName, utils.getReferenceAttributes());
            msAttributes.setRefAttribute(returnReferenceList);

            if (!"".equals(utils.getListConstraints())) {
                LinkedHashMap<String, String> enumList = new LinkedHashMap<>();
                String[] listArray = utils.getListConstraints().split("#");
                for (String str : listArray) {
                    String[] strArr = str.split("=");
                    if (strArr.length > 1) {
                        enumList.put(strArr[0], strArr[1]);
                    }
                }
                msAttributes.setEnumType(enumList);
            }

            classMap = new LinkedHashMap<>();
            classMap.put(modelName, msAttributes);

        } catch (Exception e) {
            logger.error("Failed to process yml model" + e);
        }

    }

    private List<File> listModelFiles(String directoryName) {
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<>();
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                resultList.add(file);
            } else if (file.isDirectory()) {
                resultList.addAll(listModelFiles(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    @SuppressWarnings("rawtypes")
    private void extractFolder(String zipFile) {
        int buffer = 2048;
        File file = new File(zipFile);

        try (ZipFile zip = new ZipFile(EXTRACTDIR + File.separator + file);) {
            String newPath = zipFile.substring(0, zipFile.length() - 4);
            new File(newPath).mkdir();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(EXTRACTDIR + File.separator + newPath + File.separator + currentEntry);
                File destinationParent = destFile.getParentFile();

                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    int currentByte;

                    byte[] data = new byte[buffer];
                    try (FileOutputStream fos = new FileOutputStream(destFile);
                            BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                            BufferedOutputStream dest = new BufferedOutputStream(fos, buffer)) {

                        while ((currentByte = is.read(data, 0, buffer)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    }
                }

                if (currentEntry.endsWith(".zip")) {
                    extractFolder(destFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to unzip model file " + zipFile + e);
        }
    }

    public Map<String, String> addValuesToNewModel() {

        Map<String, String> successMap = new HashMap<>();
        MSAttributeObject mainClass;

        if (!classMap.containsKey(this.newModel.getModelName())) {
            logger.error(
                    "Model Provided does not contain the service name provided in request. Unable to import new model");
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "AddValuesToNewModel",
                    "Unable to pull out required values, file missing service name provided in request");
            successMap.put("error", "MISSING");
            return successMap;
        }
        mainClass = classMap.get(this.newModel.getModelName());
        newModel.setDependency("[]");
        if (mainClass.getSubClass() != null) {
            String value = new Gson().toJson(mainClass.getSubClass());
            newModel.setSubattributes(value);
        }

        if (mainClass.getAttribute() != null) {
            String attributes = mainClass.getAttribute().toString().replace("{", "").replace("}", "");
            int equalsIndexForAttributes = attributes.indexOf('=');
            String atttributesAfterFirstEquals = attributes.substring(equalsIndexForAttributes + 1);
            this.newModel.setAttributes(atttributesAfterFirstEquals);
        }

        if (mainClass.getRefAttribute() != null) {
            String refAttributes = mainClass.getRefAttribute().toString().replace("{", "").replace("}", "");
            int equalsIndex = refAttributes.indexOf('=');
            String refAttributesAfterFirstEquals = refAttributes.substring(equalsIndex + 1);
            this.newModel.setRefattributes(refAttributesAfterFirstEquals);
        }

        if (mainClass.getEnumType() != null) {
            this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
        }

        if (mainClass.getMatchingSet() != null) {
            this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
        }

        successMap.put(SUCCESS, SUCCESS);
        return successMap;

    }

    public Map<String, String> saveImportService() {
        String modelName = this.newModel.getModelName();
        String importedBy = "API";
        String version = this.newModel.getVersion();
        Map<String, String> successMap = new HashMap<>();
        CommonClassDaoImpl dbConnection = new CommonClassDaoImpl();
        List<Object> result =
                dbConnection.getDataById(OptimizationModels.class, "modelName:version", modelName + ":" + version);
        if (result.isEmpty()) {
            OptimizationModels model = new OptimizationModels();
            model.setModelName(modelName);
            model.setVersion(version);
            model.setAttributes(this.newModel.getAttributes());
            model.setAnnotation(this.newModel.getAnnotation());
            model.setDependency(this.newModel.getDependency());
            model.setDescription(this.newModel.getDescription());
            model.setEnumValues(this.newModel.getEnumValues());
            model.setRefattributes(this.newModel.getRefattributes());
            model.setSubattributes(this.newModel.getSubattributes());
            model.setDataOrderInfo(this.newModel.getDataOrderInfo());
            UserInfo userInfo = new UserInfo();
            userInfo.setUserLoginId(importedBy);
            userInfo.setUserName(importedBy);
            model.setUserCreatedBy(userInfo);
            dbConnection.save(model);
            successMap.put(SUCCESS, SUCCESS);
        } else {
            successMap.put("DBError", "EXISTS");
            logger.error("Import new service failed.  Service already exists");
        }
        return successMap;
    }
}
