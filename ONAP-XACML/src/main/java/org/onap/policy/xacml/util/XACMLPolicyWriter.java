/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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
package org.onap.policy.xacml.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;


import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

/**
 * Helper static class for policy writing.
 * 
 *
 */
public class XACMLPolicyWriter {

    /**
     * Helper static class that does the work to write a policy set to a file on disk.
     *
     *
     */
    public static Path writePolicyFile(Path filename, PolicySetType policySet) {
        JAXBElement<PolicySetType> policySetElement = new ObjectFactory().createPolicySet(policySet);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicySetType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policySetElement, filename.toFile());

            if (Files.exists(filename)) {
                return filename;
            } else {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "File does not exist after marshalling.");
                return null;
            }

        } catch (JAXBException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyWriter", "writePolicyFile failed");
            return null;
        }
    }

    /**
     * Helper static class that does the work to write a policy set to an output stream.
     *
     *
     */
    public static void writePolicyFile(OutputStream os, PolicySetType policySet) {
        JAXBElement<PolicySetType> policySetElement = new ObjectFactory().createPolicySet(policySet);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicySetType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policySetElement, os);
        } catch (JAXBException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyWriter", "writePolicyFile failed");
        }
    }

    /**
     * Helper static class that does the work to write a policy to a file on disk.
     *
     *
     */
    public static Path writePolicyFile(Path filename, PolicyType policy) {
        JAXBElement<PolicyType> policyElement = new ObjectFactory().createPolicy(policy);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicyType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policyElement, filename.toFile());

            if (Files.exists(filename)) {
                return filename;
            } else {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "File does not exist after marshalling.");
                return null;
            }

        } catch (JAXBException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyWriter", "writePolicyFile failed");
            return null;
        }
    }


    /**
     * Helper static class that does the work to write a policy to a file on disk.
     *
     *
     */
    public static InputStream getXmlAsInputStream(Object policy) {
        JAXBElement<?> policyElement;
        if (policy instanceof PolicyType) {
            policyElement = new ObjectFactory().createPolicy((PolicyType) policy); 
            return getByteArrayInputStream(policyElement, PolicyType.class);
        } else if (policy instanceof PolicyType) {
            policyElement = new ObjectFactory().createPolicySet((PolicySetType) policy); 
            return getByteArrayInputStream(policyElement, PolicySetType.class);
        } 
        return null;
    }
    
    /**
     * Helper static class that reads the JAXB element and return policy input stream.
     * @param policyElement
     * @param className (PolicyType or PolicySetType ?).
     * @return ByteArrayInputStream.
     */
    public static InputStream getByteArrayInputStream(JAXBElement<?> policyElement, Class<?> className) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JAXBContext context = JAXBContext.newInstance(className);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policyElement, byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (JAXBException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyWriter", "writePolicyFile failed");
            throw new IllegalArgumentException("XACMLPolicyWriter writePolicyFile failed", e);
        }  
    }
    /**
     * Helper static class that does the work to write a policy set to an output stream.
     *
     *
     */
    public static void writePolicyFile(OutputStream os, PolicyType policy) {
        JAXBElement<PolicyType> policySetElement = new ObjectFactory().createPolicy(policy);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicyType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policySetElement, os);
        } catch (JAXBException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyWriter", "writePolicyFile failed");
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String changeFileNameInXmlWhenRenamePolicy(Path filename) {

        String extension = "";
        String domain = null;
        String repository = "repository";
        if(filename.toString().contains("Config_")){
            domain = filename.toString().substring(filename.toString().indexOf(repository) + (repository.length()+1), filename.toString().indexOf("Config_"));
        }else if(filename.toString().contains("Action_")){
            domain = filename.toString().substring(filename.toString().indexOf(repository) + (repository.length()+1), filename.toString().indexOf("Action_"));
        }else if(filename.toString().contains("Decision_")){
            domain = filename.toString().substring(filename.toString().indexOf(repository) + (repository.length()+1), filename.toString().indexOf("Decision_"));
        }
        if(domain.contains(File.separator)){
            domain =	domain.replace(File.separator, ".");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(PolicyType.class);
            Unmarshaller m = context.createUnmarshaller();
            JAXBElement<PolicyType> policyElement = (JAXBElement<PolicyType>) m.unmarshal(filename.toFile());
            PolicyType policyType = policyElement.getValue();
            if (policyType != null) {
                TargetType targetType = policyType.getTarget();
                List<AnyOfType> anyOfTypes = targetType.getAnyOf();
                for( Iterator anyOfIte = anyOfTypes.iterator(); anyOfIte.hasNext(); ){
                    AnyOfType anyOfType = (AnyOfType) anyOfIte.next();
                    List<AllOfType> allOf = anyOfType.getAllOf();
                    for( Iterator allOfIte = allOf.iterator(); allOfIte.hasNext(); ){
                        AllOfType allOfType = (AllOfType) allOfIte.next();
                        List<MatchType> match = allOfType.getMatch();
                        for( Iterator matchIte = match.iterator(); matchIte.hasNext();) {
                            MatchType  matchType = (MatchType) matchIte.next();
                            if("PolicyName".equals(matchType.getAttributeDesignator().getAttributeId())){
                                AttributeValueType attributeValueType = matchType.getAttributeValue();
                                List<Object> contents = attributeValueType.getContent();
                                if (contents != null && !contents.isEmpty()) {
                                    String tmp = filename.getFileName()+"";
                                    String newName = tmp.substring(0, tmp.lastIndexOf("."));
                                    attributeValueType.getContent().clear();
                                    attributeValueType.getContent().add(domain + newName  + "." + "xml");
                                }
                            }
                        }
                    }
                }
                if(filename.toString().contains("Config_") || filename.toString().contains("Action_")){
                    List<Object> objects = policyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
                    if (objects != null && !objects.isEmpty()) {
                        for (Iterator ite = objects.iterator(); ite.hasNext();) {

                            RuleType  ruleType = (RuleType ) ite.next();
                            AdviceExpressionsType adviceExpressionsType = ruleType.getAdviceExpressions();
                            if (adviceExpressionsType != null) {
                                List<AdviceExpressionType> adviceExpressionTypes = adviceExpressionsType.getAdviceExpression();
                                if (adviceExpressionTypes != null && !adviceExpressionTypes.isEmpty()) {
                                    for (Iterator iterator = adviceExpressionTypes
                                            .iterator(); iterator.hasNext();) {
                                        AdviceExpressionType adviceExpressionType = (AdviceExpressionType) iterator
                                                .next();
                                        if (adviceExpressionType.getAdviceId() != null && !"".equals(adviceExpressionType.getAdviceId()) && ("configID".equals(adviceExpressionType.getAdviceId())
                                                || "faultID".equals(adviceExpressionType.getAdviceId()) || "PMID".equals(adviceExpressionType.getAdviceId())||"firewallConfigID".equals(adviceExpressionType.getAdviceId()) || "OptimizationID".equals(adviceExpressionType.getAdviceId())
                                                || "MSID".equals(adviceExpressionType.getAdviceId())) || "GocID".equals(adviceExpressionType.getAdviceId())||"GocHPID".equals(adviceExpressionType.getAdviceId())||"BRMSRAWID".equals(adviceExpressionType.getAdviceId())
                                                || "BRMSPARAMID".equals(adviceExpressionType.getAdviceId())|| "HPSuppID".equals(adviceExpressionType.getAdviceId()) || "HPFlapID".equals(adviceExpressionType.getAdviceId()) || "HPOverID".equals(adviceExpressionType.getAdviceId()))
                                        {
                                            List<AttributeAssignmentExpressionType> attributeAssignmentExpressionTypes = adviceExpressionType.getAttributeAssignmentExpression();
                                            if (attributeAssignmentExpressionTypes != null && !attributeAssignmentExpressionTypes.isEmpty()) {
                                                for (Iterator iterator2 = attributeAssignmentExpressionTypes
                                                        .iterator(); iterator2.hasNext();) {
                                                    AttributeAssignmentExpressionType attributeAssignmentExpressionType = (AttributeAssignmentExpressionType) iterator2
                                                            .next();
                                                    if ("URLID".equals(attributeAssignmentExpressionType.getAttributeId())) {
                                                        JAXBElement<AttributeValueType> attributeValueType = (JAXBElement<AttributeValueType>) attributeAssignmentExpressionType.getExpression();
                                                        AttributeValueType attributeValueType1 = attributeValueType.getValue();
                                                        String configUrl = "$URL";
                                                        String urlVal = (String) attributeValueType1.getContent().get(0);
                                                        String origExtension = urlVal.substring(urlVal.lastIndexOf('.')+1).trim();
                                                        extension = origExtension;
                                                        attributeValueType1.getContent().clear();
                                                        String txtFileName = filename.getFileName().toString();
                                                        txtFileName = txtFileName.substring(0, txtFileName.lastIndexOf(".")+1) + origExtension;
                                                        txtFileName = configUrl+ File.separator + "Config" + File.separator + domain + txtFileName;
                                                        attributeValueType1.getContent().add(txtFileName);
                                                    } else if ("PolicyName".equals(attributeAssignmentExpressionType.getAttributeId())) {
                                                        JAXBElement<AttributeValueType> attributeValueType = (JAXBElement<AttributeValueType>) attributeAssignmentExpressionType.getExpression();
                                                        AttributeValueType attributeValueType1 = attributeValueType.getValue();
                                                        List<Object> contents = attributeValueType1.getContent();
                                                        if (contents != null && !contents.isEmpty()) {
                                                            String tmp = filename.getFileName()+"";
                                                            String newName = tmp.substring(0, tmp.lastIndexOf("."));
                                                            attributeValueType1.getContent().clear();
                                                            attributeValueType1.getContent().add(domain + newName + "." + "xml");
                                                        }

                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (objects != null && !objects.isEmpty()) {
                            for (Iterator ite1 = objects.iterator(); ite1.hasNext();) {

                                RuleType  ruleType1 = (RuleType ) ite1.next();
                                ObligationExpressionsType obligationExpressionsType = ruleType1.getObligationExpressions();
                                if (obligationExpressionsType != null) {
                                    List<ObligationExpressionType> obligationExpressionType = obligationExpressionsType.getObligationExpression();
                                    if (obligationExpressionType != null && !obligationExpressionType.isEmpty()) {
                                        for (Iterator iterator = obligationExpressionType
                                                .iterator(); iterator.hasNext();) {
                                            ObligationExpressionType obligationExpressionTypes = (ObligationExpressionType) iterator
                                                    .next();
                                            if (obligationExpressionTypes.getObligationId() != null && !"".equals(obligationExpressionTypes.getObligationId())) {
                                                List<AttributeAssignmentExpressionType> attributeAssignmentExpressionTypes = obligationExpressionTypes.getAttributeAssignmentExpression();
                                                if (attributeAssignmentExpressionTypes != null && !attributeAssignmentExpressionTypes.isEmpty()) {
                                                    for (Iterator iterator2 = attributeAssignmentExpressionTypes
                                                            .iterator(); iterator2.hasNext();) {
                                                        AttributeAssignmentExpressionType attributeAssignmentExpressionType = (AttributeAssignmentExpressionType) iterator2
                                                                .next();
                                                        if ("body".equals(attributeAssignmentExpressionType.getAttributeId())) {
                                                            JAXBElement<AttributeValueType> attributeValueType = (JAXBElement<AttributeValueType>) attributeAssignmentExpressionType.getExpression();
                                                            AttributeValueType attributeValueType1 = attributeValueType.getValue();
                                                            String configUrl = "$URL";
                                                            String urlVal = (String) attributeValueType1.getContent().get(0);
                                                            String origExtension = urlVal.substring(urlVal.lastIndexOf('.')+1).trim();
                                                            extension = "json";
                                                            attributeValueType1.getContent().clear();
                                                            String txtFileName = filename.getFileName().toString();
                                                            txtFileName = txtFileName.substring(0, txtFileName.lastIndexOf(".")+1) + origExtension;
                                                            txtFileName = configUrl+ File.separator + "Action" + File.separator + domain + txtFileName;
                                                            attributeValueType1.getContent().add(txtFileName);
                                                        }

                                                    }
                                                }

                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                writePolicyFile(filename, policyType);
            }
        }catch (JAXBException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyWriter", "writePolicyFile failed");
        }

        return extension;
    }

}
