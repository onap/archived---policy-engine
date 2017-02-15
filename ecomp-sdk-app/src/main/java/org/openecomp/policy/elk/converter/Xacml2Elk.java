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

package org.openecomp.policy.elk.converter;


import io.searchbox.core.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;

import com.att.research.xacml.api.Advice;
import com.att.research.xacml.api.AttributeAssignment;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Obligation;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.util.XACMLPolicyScanner;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacml.util.XACMLPolicyScanner.CallbackResult;
import com.att.research.xacml.util.XACMLPolicyScanner.SimpleCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.openecomp.policy.elk.client.ElkConnector;
import org.openecomp.policy.elk.client.ElkConnector.PolicyBodyType;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


@SuppressWarnings("unused")
public class Xacml2Elk {
	public static final String URLID_ATTRIBUTE = "URLID";
	public static final String BODY_ATTRIBUTE = "body";
	
	protected static final Logger logger = FlexLogger.getLogger(Xacml2Elk.class);	
	protected static JAXBContext jaxbContext = jaxbContext();
	
	protected static String toConfigsWebDirectory(String policyType) 
	          throws IllegalArgumentException {
		if (policyType == null || policyType.isEmpty())
			throw new IllegalArgumentException("Unexpected policy type: " + policyType);
		
		ElkConnector.PolicyType type = ElkConnector.PolicyType.valueOf(policyType);
		switch(type) {
		case Config:
			return type.name();
		case Action:
			return type.name();
		case Decision:
			return type.name();
		case Config_Fault:
		case Config_PM:
		case Config_FW:
		case Config_MS:
			return ElkConnector.PolicyType.Config.name();
		default:
			throw new IllegalArgumentException("Unexpected policy type: " + policyType);
		}
	}
	
	protected synchronized static JAXBContext jaxbContext() {
		if (jaxbContext != null) {
			return jaxbContext;
		}
		
		try {
			jaxbContext = JAXBContextFactory.createContext(new Class[] {PolicyType.class}, null);
		} catch (JAXBException e) {
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
		                 "JAXB Context cannot be created");
			return null;
		}
		
		return jaxbContext;
	}
	
	protected static class CLIOptions {
		@Option(name="-t", usage="policy type", aliases={"-type", "--type"}, required=true)
		protected String type;
		
		@Option(name="-n", usage="policy name", aliases={"-name", "--name"}, required=true)
		protected String name;
		
		@Option(name="-o", usage="git repository owner", aliases={"-owner", "--owner"}, required=true)
		protected String owner;
		
		@Option(name="-s", usage="enclosing scope", aliases={"-scope", "--scope"}, required=true)
		protected String scope;
		
		@Option(name="-x", usage="xacml input file", aliases={"-xacml", "--xacml"}, required=true, metaVar="<xacml input file>")
		protected File xacmlFile;
		
		@Option(name="-p", usage="payload body type", aliases={"-payloadType", "--payloadType"}, required=false, metaVar="<bayload body type>", depends={"-b"})
		protected PolicyBodyType bodyType;
		
		@Option(name="-b", usage="payload body", aliases={"-body", "--body"}, required=false, metaVar="<bayload body type>", depends={"-p"})
		protected String body;
		
		@Option(name="-d", usage="elk record output directory", aliases={"-d", "--directory"}, required=true, metaVar="<elk directory>")
		protected File elkDirectory;
		
		@Option(name = "-h", aliases = {"-help", "--help"}, usage = "print this message")
		private boolean help = false;		
	};
	
	class AttributeAssignmentFinderProcessor extends SimpleCallback {
		protected final String attributeId;
		protected String attributeValue = null;
		
		public AttributeAssignmentFinderProcessor(String attributeId) {
			this.attributeId = attributeId;
		}
				
		public String getAttributeValue() {
			return attributeValue;
		}
		
		public boolean isAttributeValue() {
			return (this.attributeValue != null && !this.attributeValue.isEmpty());
		}	
		
		private boolean processAssignments(
				List<AttributeAssignmentExpressionType> assignments) {
			if (logger.isTraceEnabled())
				logger.trace("ENTER");
			
			for (AttributeAssignmentExpressionType assignment : assignments) {
				if (!assignment.getAttributeId().equals(attributeId)) {
					if (logger.isDebugEnabled())
						logger.debug("Ignoring: " + assignment.getAttributeId());
					continue;
				}
				
				if (logger.isDebugEnabled())
					logger.debug("Found Attribute ID: " + assignment.getAttributeId());
				
				JAXBElement<?> jaxbExp = assignment.getExpression();
				Object assignmentObject = jaxbExp.getValue();
				if (assignmentObject instanceof AttributeValueType) {
					AttributeValueType avt = (AttributeValueType) assignmentObject;
					if (avt.getContent().size() <= 0) {
						logger.warn("Ignoring: " + assignment.getAttributeId() + ": No values");
						continue;
					}

					this.attributeValue = avt.getContent().get(0).toString();
					if (logger.isInfoEnabled())
						logger.info("Found: " + this.attributeValue);
					
					return true;
				}
			}
			return false;
		}	

		@Override
		public CallbackResult onAdvice(Object parent, AdviceExpressionType expression, Advice advice) {
			if (logger.isTraceEnabled())
				logger.trace("ENTER");
			
			List<AttributeAssignmentExpressionType> assignments = 
					expression.getAttributeAssignmentExpression();

			if (assignments != null) {
				boolean found = processAssignments(assignments);
				if (found)
					return CallbackResult.STOP;
			}
			
			return super.onAdvice(parent, expression, advice);
		}
		
		@Override
		public CallbackResult onObligation(Object parent, ObligationExpressionType expression, Obligation obligation) {
			if (logger.isTraceEnabled())
				logger.trace("ENTER");
			
			List<AttributeAssignmentExpressionType> assignments = 
					expression.getAttributeAssignmentExpression();

			if (assignments != null) {
				boolean found = processAssignments(assignments);
				if (found)
					return CallbackResult.STOP;
			}
			
			return super.onObligation(parent, expression, obligation);
			
		}
	}
	
	final protected String type;
	final protected String name;
	final protected String owner;
	final protected String scope;
	final protected File xacmlFile;
	final protected File elkDirectory;
	
	final protected JAXBElement<PolicyType> policy;

	protected PolicyBodyType bodyType;
	protected String body;
	
	
	public Xacml2Elk(String type, String name, 
			         String owner, String scope, 
			         File xacmlFile, File elkDirectory) 
	       throws IllegalArgumentException {
		
		this.type = type;
		this.name = name;
		this.owner = owner;
		this.scope = scope;
		this.xacmlFile = xacmlFile;
		this.elkDirectory = elkDirectory;
		
		this.policy = jaxbXacml(xacmlFile);
		
		this.body = "";
		this.bodyType = PolicyBodyType.none;
		bodyFromXacml();	
	}
	
	public Xacml2Elk(CLIOptions args) throws IllegalArgumentException {
		this.type = args.type;
		this.name = args.name;
		this.owner = args.owner;
		this.scope = args.scope;
		this.xacmlFile = args.xacmlFile;
		this.elkDirectory = args.elkDirectory;
		
		this.policy = jaxbXacml(xacmlFile);
		
		if (args.body == null || args.body.isEmpty()) {
			this.body = "";
			this.bodyType = PolicyBodyType.none;
			bodyFromXacml();
		} else {		
			this.body = args.body;
			this.bodyType = args.bodyType;
		}
	}
	
	public Xacml2Elk(String type, String name, String owner,
			String scope, File xacmlFile, PolicyBodyType bodyType,
			String body, File destinationDir) 
		throws IllegalArgumentException {
		this.type = type;
		this.name = name;
		this.owner = owner;
		this.scope = scope;
		this.xacmlFile = xacmlFile;
		this.bodyType = bodyType;
		this.body = body;
		this.elkDirectory = destinationDir;
		
		this.policy = jaxbXacml(xacmlFile);
	}
	
	public Xacml2Elk(File xacmlFile, boolean skipBody) 
			throws IllegalArgumentException {		
		this.policy = jaxbXacml(xacmlFile);
		PolicyType jPolicy = this.policy.getValue();
		
		this.xacmlFile = xacmlFile;
		
		this.type = ElkConnector.toPolicyType(xacmlFile.getName()).name();
		String fileName = xacmlFile.getName().replaceFirst(this.type + "_", "");
		if (fileName.indexOf(".") > 0)
			this.name = fileName.substring(0, fileName.lastIndexOf("."));
		else
			this.name = fileName;
		
		this.owner = "admin";
		this.scope = getScope(xacmlFile.getParent());
		this.elkDirectory = null;
		
		this.body = "";
		this.bodyType = PolicyBodyType.none;
		if (!skipBody) {
			bodyFromXacml();
		} 
	}
	
	@SuppressWarnings("unchecked")
	protected JAXBElement<PolicyType> jaxbXacml(File xacmlFile) throws IllegalArgumentException {
		Path xacmlPath = xacmlFile.toPath();
		if (!Files.isReadable(xacmlPath) || !Files.isRegularFile(xacmlPath)) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error: " + xacmlPath + " is invalid.");
			}
			throw new IllegalArgumentException("Error: " + xacmlPath + " is invalid.");
		}

		try {
			Unmarshaller u = jaxbContext.createUnmarshaller();
			return (JAXBElement<PolicyType>) u.unmarshal(xacmlFile);
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + " - error: " + xacmlPath + " is invalid.");
			}
			throw new IllegalArgumentException(xacmlFile.getAbsolutePath() + " does not contain valid XACML");
		}
	}
	
	public JAXBElement<PolicyType> getPolicy() {
		return policy;
	}
	
	protected String getScope(String xacmlDirPath) {
		if (logger.isTraceEnabled()) logger.trace("ENTER");		
		
		xacmlDirPath = xacmlDirPath.replaceAll("//", "/");
		xacmlDirPath = xacmlDirPath.replaceAll("\\\\", "/");
		xacmlDirPath = xacmlDirPath.replace('\\', '/');
		
		String ws = XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_WORKSPACE);
		String adminRepo = XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_REPOSITORY);
		Path wsPath = Paths.get(ws, "admin", adminRepo);
		File repoDir = wsPath.toFile();
		String repoPath = repoDir.getPath();
		repoPath = repoPath.replaceAll("//", "/");
		repoPath = repoPath.replaceAll("\\\\", "/");
		repoPath = repoPath.replace('\\', '/');
		
		int startIndex = xacmlDirPath.indexOf(repoPath.toString()) + repoPath.toString().length() + 1;
		String scope = xacmlDirPath.substring(startIndex, xacmlDirPath.length());
		
		if (logger.isInfoEnabled())
			logger.info("xacml-policy-path=" + xacmlDirPath + "|" + 
		                "repository-path=" + repoPath + "|" + 
					     "scope=" + scope);
		
		return scope;
	}
	
	@SuppressWarnings("deprecation")
	private boolean bodyFromXacml() {
		if (logger.isTraceEnabled())
			logger.trace("ENTER");
		
		String urlAttribute = URLID_ATTRIBUTE;
		try {
			switch (ElkConnector.toPolicyType(this.type)) {
			case Action:
				urlAttribute = BODY_ATTRIBUTE;
				break;
			case Decision:
			case none:
				/* no body attached to decision policies */
				if (logger.isInfoEnabled())
					logger.info("No body attached for this type of policy: " + this.xacmlFile.getAbsolutePath());
				return false;
			default:
				/* a flavour of a config policy - default is fine */
				break;
			}
		} catch (IllegalArgumentException iae) {
			if (logger.isWarnEnabled()) {
				logger.warn(this.type + " cannot be converted to a valid type: " + iae.getMessage(), iae);
			}
			return false;
		}
		
		AttributeAssignmentFinderProcessor
			processor = new AttributeAssignmentFinderProcessor(urlAttribute);
		XACMLPolicyScanner xacmlScanner = 
				new XACMLPolicyScanner(this.policy.getValue(), processor);
		xacmlScanner.scan();
		if (!processor.isAttributeValue()) {
			if (logger.isInfoEnabled())
				logger.info(urlAttribute + " not found in " + this.xacmlFile.getAbsolutePath());
			return false;
		}
		
		String configsUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_CONFIG_URL);
		if (configsUrl == null || configsUrl.isEmpty() || !configsUrl.startsWith("http")) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLRestProperties.PROP_CONFIG_URL + " property is not set.");
			}
			configsUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
			if (configsUrl == null || configsUrl.isEmpty() || !configsUrl.startsWith("http")) {
				if (logger.isWarnEnabled()) {
					logger.warn(XACMLRestProperties.PROP_PAP_URL + " property is not set.");
				}
				return false;
			} else {
				configsUrl = configsUrl.replaceFirst("/pap", "");
			}		
		}
		
		if (!configsUrl.endsWith("/")) {
			configsUrl += "/";
		}
		
		String urlXacml = processor.getAttributeValue();
		if (logger.isDebugEnabled()) {
			logger.debug("configs url is " + configsUrl + "and url in xacml is " + urlXacml);
		}
		
		if (urlXacml.startsWith("http")) {
			// assuming this an unescaped url
		} else if (urlXacml.startsWith("$URLConfig/")) {
			urlXacml = urlXacml.replace("$URLConfig/", configsUrl);
		} else if (urlXacml.startsWith("$URL/")) {
			urlXacml = urlXacml.replace("$URL/", configsUrl);
		} else{
			if (logger.isWarnEnabled()) {
				logger.warn("XACML url is not in the expected format: " + urlXacml);
			}
			return false;
		}
		
		if (urlXacml.endsWith(".properties")) {
			this.bodyType = PolicyBodyType.properties;
		} else if (urlXacml.endsWith(".json")) {
			this.bodyType = PolicyBodyType.json;
		} else if (urlXacml.endsWith(".xml")) {
			this.bodyType = PolicyBodyType.xml;
		} else if (urlXacml.endsWith(".txt")) {
			this.bodyType = PolicyBodyType.txt;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("converted url from xacml is " + urlXacml + ", body-type is " + this.bodyType);
		}
		
		InputStream inConfigs = null;
		try {
			URL url = new URL(urlXacml);
			URLConnection connection = url.openConnection();
			inConfigs = connection.getInputStream();
			String encoding = connection.getContentEncoding();
			encoding = (encoding == null ? "UTF-8" : encoding);
			this.body = IOUtils.toString(inConfigs, encoding);			
			if (logger.isInfoEnabled()) {
				logger.info("The following document of type " + this.bodyType.toString() + 
						    " has been fetched from " + urlXacml + System.lineSeparator() +
						    this.body);
			}
			try {
				inConfigs.close();
			} catch (IOException e) {
				// ignore
				logger.warn("Unexpected error closing stream to " + urlXacml, e);
			}
			return true;
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
						    "- XACML url is not in the expected format: " + e.getMessage() + 
						    ": " + urlXacml, e);
			}	
			// continue
		} finally {
			if (inConfigs != null) {
				try {
					inConfigs.close();
				} catch (IOException e) {
					// ignore
					logger.warn("Unexpected error closing stream to " + urlXacml, e);
				}
			}
		}
		
		// if retrieval through PAP url was not possible, try to retrieve it from 
		// filesystem instead.
		
		if (this.body == null || this.body.isEmpty()) {
			Path webappsPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS));
			if (webappsPath == null) {
				logger.error("Invalid Webapps Path Location property : " + 
			                 XACMLRestProperties.PROP_PAP_WEBAPPS);
				return false;
			}
			String waPath = webappsPath.toFile().getAbsolutePath();
			
			String typeDir = null;
			try {
				typeDir = Xacml2Elk.toConfigsWebDirectory(this.type);
			} catch (IllegalArgumentException iae) {
				if (logger.isWarnEnabled()) {
					logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
							    "- " + this.type + 
							    " cannot be converted to body-directory: " + iae.getMessage(), 
							    iae);
				}
				this.bodyType = PolicyBodyType.none;
				return false;
			}
			
			String scopePrefix = this.scope.replace('/', '.');
			Path bodyPath = Paths.get(waPath, 
					                  typeDir, 
					                  scopePrefix + "." + this.type + "_" + 
					                  this.name + "." + this.bodyType.name());
			File bodyFile = bodyPath.toFile();
			if (Files.notExists(bodyPath)) {
				if (logger.isWarnEnabled()) {
					logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
							    "The following document of type " + this.bodyType.toString() + 
							    " does not exist at filesystem location " + bodyFile.getAbsolutePath());
				}
				this.bodyType = PolicyBodyType.none;
				return false;
			} else {			
				if (logger.isInfoEnabled()) {
					logger.info("The following document of type " + this.bodyType.toString() + 
							    " will be fetched from filesystem location " + bodyFile.getAbsolutePath());
				}
			}
			
			try {
				inConfigs = new FileInputStream(bodyFile);
				this.body = IOUtils.toString(inConfigs);			
				inConfigs.close();
				if (logger.isInfoEnabled()) {
					logger.info("The document of type " + this.bodyType.toString() + 
							    " has been found at filesystem location " + bodyFile.getAbsolutePath());
				}
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
							    "- XACML Body File cannot be read: " + bodyFile.getAbsolutePath(), e);
				}
				this.bodyType = PolicyBodyType.none;
				return false;
			} finally {
				if (inConfigs != null) {
					try {
						inConfigs.close();
					} catch (IOException e) {
						// ignore
						logger.warn("Unexpected error closing stream to " + urlXacml, e);
					}
				} else {
					return false;
				}
			}			
		} 
		return true;
	}

	public boolean attachJsonBody(JsonNode jPolicy) {
		if (this.body == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
						    "- JSON Body expected but none provided from " +
						    this.scope + ":" + this.type + ":" + this.name);
			}
			return true;
		}
		
		ObjectNode jPolicyRoot = (ObjectNode) jPolicy;
		
		// verify the json is valid
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode jBodyContent;
		try {
			jBodyContent = mapper.readTree(this.body);
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
				        "- JSON Body is invalid in " +
				        this.scope + ":" + this.type + ":" + this.name + ":" +
						e.getMessage() + System.lineSeparator() + this.body, e);
			return false;
		}
		
		String jBodyName = this.type + "_" + "Body";
		ObjectNode jBodyContainer = mapper.createObjectNode();
		jBodyContainer.set(jBodyName, jBodyContent);		
		
		jPolicyRoot.set("Body", jBodyContainer);
		
		if (logger.isDebugEnabled())
			logger.debug("Attaching JSON to " +
		                  this.scope + ":" + 
		                  this.type + ":" + this.name + ":" + 
		                  jBodyName + System.lineSeparator() +
		                  jBodyContent);
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean attachXmlBody(JsonNode jPolicy) {
		if (this.body == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
						    "- JSON Body expected but none provided from " +
						    this.scope + ":" + this.type + ":" + this.name);
			}
			return true;
		}
		
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.setConfig(xmlMapper.getSerializationConfig().withRootName(""));
		Map<Object, Object> map;
		try {
			map = xmlMapper.readValue(this.body, Map.class);
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
			        "- XML Body is invalid in " +
			        this.scope + ":" + this.type + ":" + this.name + ":" +
					e.getMessage() + System.lineSeparator() + this.body, e);
			return false;
		}

		final ObjectMapper mapper = new ObjectMapper();
		String jXmlBody;
		try {
			jXmlBody = mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
			        "- Cannot convert XML Body to JSON in " +
			        this.scope + ":" + this.type + ":" + this.name + ":" +
					e.getMessage() + System.lineSeparator() + this.body, e);
			return false;
		}
		
		if (logger.isDebugEnabled())
			logger.debug("XML-to-JSON Body conversion: " + this.scope + ":" + 
		                  this.type + ":" + this.name +jXmlBody);

		JsonNode jBodyContent;
		try {
			jBodyContent = mapper.readTree(jXmlBody);
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
				        "- JSON Body (converted from XML) is invalid in " +
				        this.scope + ":" + this.type + ":" + this.name  + ":" +
						e.getMessage() + System.lineSeparator() + jXmlBody, e);
			return false;
		}
		
		ObjectNode jPolicyRoot = (ObjectNode) jPolicy;		

		String jBodyName = this.type + "_" + "Body";
		ObjectNode jBodyContainer = mapper.createObjectNode();
		jBodyContainer.set(jBodyName, jBodyContent);		
		
		jPolicyRoot.set("Body", jBodyContainer);
		
		
		if (logger.isDebugEnabled())
			logger.debug("Attaching JSON to " +
		                  this.scope + ":" + 
		                  this.type + ":" + this.name + ":" + 
		                  jBodyName + System.lineSeparator() +
		                  jBodyContent);
		
		return true;
	}	
	
	protected boolean attachPropertiesBody(JsonNode jPolicy) {
		if (this.body == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
						    "- JSON Body expected but none provided from " +
						    this.scope + ":" + this.type + ":" + this.name);
			}
			return true;
		}
		
	    final Properties propBody = new Properties();
	    try {
			propBody.load(new StringReader(this.body));
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
			        "- JSON Body is invalid in " +
			        this.scope + ":" + this.type + ":" + this.name + ":" +
			        e.getMessage() + System.lineSeparator() + this.body, e);
			return false;
		}
		
	    if (propBody.isEmpty()) {
	    	if (logger.isInfoEnabled()) {
				logger.info("Empty set of properties: " +
					        this.scope + ":" + this.type + ":" + this.name +
					        System.lineSeparator() + this.body);
	    	}
	    	return true;
	    }
	    
		
		final ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jPolicyRoot = (ObjectNode) jPolicy;	
		ObjectNode jBody = jPolicyRoot.putObject("Body");
		String jBodyName = this.type + "_" + "Body";
		ObjectNode jBodyContainer = jBody.putObject(jBodyName);
		
		// ObjectNode jBodyContainer = mapper.createObjectNode();
		
		for(String key : propBody.stringPropertyNames()) {
			String value = propBody.getProperty(key);
	    	if (logger.isDebugEnabled()) {
				logger.debug("Attaching JSON field to " + jBodyName + " for " + 
					        this.type.toString() + ":" + 
						    this.scope + ":" + this.name + ":" + jBodyName + ":" +
					        " <" + key +"," + value + ">");
	    	}
	    	jBodyContainer.put(key, propBody.getProperty(key));
		}

		return true;
	}
	
	public boolean attachTextBody(JsonNode jPolicy) {
		if (this.body == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
						    "- JSON Body expected but none provided from " +
						    this.scope + ":" + this.type + ":" + this.name);
			}
			return true;
		}
		
		final ObjectMapper mapper = new ObjectMapper();
		StringWriter jsonEscapedTextWriter = new StringWriter();
		try {
			mapper.writer().writeValue(jsonEscapedTextWriter, this.body);
		} catch (IOException e) {
			if (logger.isWarnEnabled()) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
						    "- Text Body cannot be converted from " +
						    this.scope + ":" + this.type + ":" + this.name + ":" +
						    e.getMessage() + ":" + jsonEscapedTextWriter , e);
			}
			return false;
		}
		String jTextBody = jsonEscapedTextWriter.toString();
		
		if (logger.isDebugEnabled())
			logger.debug("XML 2JSON Body conversion: " + this.scope + ":" + 
		                  this.type + ":" + this.name + ":" + jTextBody);
		
		ObjectNode jPolicyRoot = (ObjectNode) jPolicy;		
		
		String jBodyName = this.type + "_" + "Body";	
		ObjectNode jBodyContainer = mapper.createObjectNode();
		jBodyContainer.put(jBodyName, jTextBody);		
		
		jPolicyRoot.set("Body", jBodyContainer);
		
		if (logger.isDebugEnabled())
			logger.debug("Attaching JSON to " +
		                  this.scope + ":" + 
		                  this.type + ":" + this.name + ":" + 
		                  jBodyName + ":" +
		                  jTextBody);

		return true;
	}		
	
	protected boolean attachBody(JsonNode jPolicy) {
		if (logger.isTraceEnabled()) logger.trace("ENTER");
		
		if (this.bodyType == PolicyBodyType.none) {
			if (logger.isInfoEnabled())
				logger.info("No body to attach for policy " + 
						this.scope + "/" + this.type +  "_" + this.name);
			
			return true;
		}
		
		if (this.body == null || this.body.isEmpty()) {
			if (logger.isWarnEnabled())
				logger.warn("No body to attach for policy " + 
			                this.bodyType + this.type + this.scope + this.name);
			
			return true;
		}
		
		switch (this.bodyType) {
		case json:
			return attachJsonBody(jPolicy);
		case properties:
			return attachPropertiesBody(jPolicy);
		case xml:
			return attachXmlBody(jPolicy);
		case txt:
			return attachTextBody(jPolicy);			
		case none:
		default:
			if (logger.isWarnEnabled())
				logger.warn("Unexpected body type: " +  this.bodyType + 
			                this.bodyType + this.type + this.scope + this.name);
			return false;
		}
	}
	
	public ElkRecord record() throws JAXBException, JsonProcessingException, 
	                                 IOException, IllegalArgumentException {
		if (logger.isTraceEnabled()) logger.trace("ENTER");
		
		Marshaller m = jaxbContext.createMarshaller();		
		m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);
		m.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, false);
		
		StringWriter policyStringWriter = new StringWriter();
		m.marshal(policy, policyStringWriter);
		
		// add metadata to elk record
		
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode jRoot = mapper.readTree(policyStringWriter.toString());
		JsonNode jPolicy = jRoot.path("Policy");
		if (jPolicy.isMissingNode()) {
			logger.warn("Aborting: Policy root node is missing.");
			throw new IllegalArgumentException("Missing policy root node");
		}
		
		((ObjectNode) jPolicy).put("PolicyType", this.type.toString());
		((ObjectNode) jPolicy).put("PolicyName", this.name);		
		((ObjectNode) jPolicy).put("Owner", this.owner);
		((ObjectNode) jPolicy).put("Scope", this.scope);
		
		JsonNode jPolicyId = jPolicy.path("PolicyId");
		if (jPolicyId.isMissingNode()) {
			logger.warn("Aborting: Policy ID node is missing.");
			throw new IllegalArgumentException("Missing policy id");
		}	
		
		if (!jPolicyId.isTextual() || !jPolicyId.isValueNode()) {
			logger.warn("Aborting: Policy ID invalid.");
			throw new IllegalArgumentException("Invalid policy id");
		}
		
		String xacmlPolicyId = jPolicyId.asText();
		String policyId = xacmlPolicyId.substring(xacmlPolicyId.lastIndexOf(":")+1);
		
		boolean success = attachBody(jPolicy);
		
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
		mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);		

		String recordText = mapper.writeValueAsString(jRoot);
		if (logger.isDebugEnabled()) {
			logger.debug("ELK Record: " + System.lineSeparator() + recordText);
		}
		
		ElkRecord elkRecord = new ElkRecord(policyId, recordText, jRoot, success);
		return elkRecord;
	}
	
	public void store(String policyId, String record) throws IOException {
		if (logger.isTraceEnabled()) logger.trace("ENTER");
		
		if (this.elkDirectory != null) {
			Files.createDirectories(this.elkDirectory.toPath());;
			Path elkPolicyFile = Paths.get(this.elkDirectory.getPath(), policyId + ".json");
			
			if (logger.isDebugEnabled()) {
				logger.info("Output: " + elkPolicyFile.toAbsolutePath().toString());
				logger.info("---------------------------------------------------");
			}
			
			Files.write(elkPolicyFile, record.getBytes());
		}		
	}
	
	public static void main(String args[]) 
			throws JAXBException, IOException, CmdLineException, IllegalStateException {
		
		CLIOptions cliOptions = new CLIOptions();
		CmdLineParser cliParser= new CmdLineParser(cliOptions);
		
		try {
			cliParser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println("Usage: Xacml2elk");
			cliParser.printUsage(System.err);
			throw e;
		}
		
		System.out.println("---------------------------------------------------");
		System.out.println("Converting " + cliOptions.xacmlFile.getName() + " to ELK format");
		System.out.println("Metadata=" + "[type:" + cliOptions.type + 
				           "|name:" + cliOptions.name + 
				           "|owner:" + cliOptions.owner + 
				           "|scope:" + cliOptions.scope + "]");
		
		// generate json from jaxb input file
		
		Path xacmlPath = cliOptions.xacmlFile.toPath();
		if (!Files.isReadable(xacmlPath) || !Files.isRegularFile(xacmlPath)) {
			System.out.println("Error: " + xacmlPath + " is invalid.");
			throw new IllegalArgumentException("Error: " + xacmlPath + " is invalid.");
		}
		
		
		Xacml2Elk convertor = new Xacml2Elk(cliOptions);
		ElkRecord elkRecord = convertor.record();
		System.out.println(elkRecord.record);
		
		Path elkOutDir = cliOptions.elkDirectory.toPath();
		if (!Files.isReadable(elkOutDir) || !Files.isDirectory(elkOutDir) || 
			!Files.isWritable(elkOutDir) || !Files.isExecutable(elkOutDir)) {
			System.out.println("Error: " + elkOutDir.getFileName() + " is invalid.");
			throw new IllegalArgumentException("Error: " + elkOutDir.getFileName() + " is invalid.");
		}
		
		convertor.store(elkRecord.policyId, elkRecord.record);
	}
}
