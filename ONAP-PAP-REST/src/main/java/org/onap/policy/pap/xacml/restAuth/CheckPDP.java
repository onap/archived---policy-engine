/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.restAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.api.pap.PAPException;

public class CheckPDP {

    private static Path pdpPath = null;
    private static Properties pdpProp = null;
    private static Long oldModified = null;
    private static Long newModified = null;
    private static HashMap<String, String> pdpMap = null;
    private static final Logger logger = FlexLogger.getLogger(CheckPDP.class);

    public static boolean validateID(String id) {
        // ReadFile
        try {
            readFile();
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "CheckPDP", "Exception reading file");
            return false;
        }
        // Check ID
        if (pdpMap.containsKey(id)) {
            return true;
        }
        return false;
    }

    private static void readFile() throws PAPException {
        String pdpFile = XACMLPapServlet.getPDPFile();
        if (pdpFile == null) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + "PDP File name is undefined");
            throw new PAPException(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"PDP File name not Valid : " + pdpFile);
        }
        if (pdpPath == null) {
            pdpPath = Paths.get(pdpFile);
            if (Files.notExists(pdpPath)) {
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + "File doesn't exist in the specified Path");
                throw new PAPException(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"File doesn't exist in the specified Path : "+ pdpPath.toString());
            }
            if (pdpPath.toString().endsWith(".properties")) {
                readProps();
            } else {
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + "Not a .properties file");
                throw new PAPException(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Not a .properties file");
            }
        }
        // Check if File is updated recently
        else {
            newModified = pdpPath.toFile().lastModified();
            if (!Objects.equals(newModified, oldModified)) {
                // File has been updated.
                readProps();
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void readProps() throws PAPException {
        InputStream in;
        pdpProp = new Properties();
        try {
            in = new FileInputStream(pdpPath.toFile());
            oldModified = pdpPath.toFile().lastModified();
            pdpProp.load(in);
        } catch (IOException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "CheckPDP", "Cannot load the Properties file");
            throw new PAPException("Cannot Load the Properties file", e);
        }
        // Read the Properties and Load the PDPs and encoding.
        pdpMap = new HashMap<>();
        // Check the Keys for PDP_URLs
        Collection<Object> unsorted = pdpProp.keySet();
        List<String> sorted = new ArrayList(unsorted);
        Collections.sort(sorted);
        for (String propKey : sorted) {
            if (propKey.startsWith("PDP_URL")) {
                String check_val = pdpProp.getProperty(propKey);
                if (check_val == null) {
                    throw new PAPException("Properties file doesn't have the PDP_URL parameter");
                }
                if (check_val.contains(";")) {
                    List<String> pdp_default = new ArrayList<>(Arrays.asList(check_val.split("\\s*;\\s*")));
                    int pdpCount = 0;
                    while (pdpCount < pdp_default.size()) {
                        String pdpVal = pdp_default.get(pdpCount);
                        readPDPParam(pdpVal);
                        pdpCount++;
                    }
                } else {
                    readPDPParam(check_val);
                }
            }
        }
        if (pdpMap == null || pdpMap.isEmpty()) {
            logger.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Cannot Proceed without PDP_URLs");
            throw new PAPException(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Cannot Proceed without PDP_URLs");
        }
    }

    private static void readPDPParam(String pdpVal) throws PAPException{
        if(pdpVal.contains(",")){
            List<String> pdpValues = new ArrayList<>(Arrays.asList(pdpVal.split("\\s*,\\s*")));
            if(pdpValues.size()==3){
                // 1:2 will be UserID:Password
                String userID = pdpValues.get(1);
                String pass = pdpValues.get(2);
                Base64.Encoder encoder = Base64.getEncoder();
                // 0 - PDPURL
                pdpMap.put(pdpValues.get(0), encoder.encodeToString((userID+":"+pass).getBytes(StandardCharsets.UTF_8)));
            }else{
                PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + "No Credentials to send Request");
                throw new PAPException(XACMLErrorConstants.ERROR_PERMISSIONS + "No enough Credentials to send Request. " + pdpValues);
            }
        }else{
            PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + "No Credentials to send Request: " + pdpVal);
            throw new PAPException(XACMLErrorConstants.ERROR_PERMISSIONS +"No enough Credentials to send Request.");
        }
    }

    public static String getEncoding(String pdpID){
        try {
            readFile();
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "CheckPDP", "Exeption reading Properties file");
        }
        String encoding = null;
        if(pdpMap!=null && (!pdpMap.isEmpty())){
            try{
                encoding = pdpMap.get(pdpID);
            } catch(Exception e){
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "CheckPDP", "Exception encoding");
            }
            return encoding;
        }else{
            return null;
        }
    }

}
