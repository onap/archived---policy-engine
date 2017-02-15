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

package org.openecomp.policy.brmsInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;
import org.openecomp.policy.utils.BackUpHandler;
import org.openecomp.policy.utils.BackUpMonitor;
import org.openecomp.policy.utils.PolicyUtils;
import org.sonatype.nexus.client.NexusClient;
import org.sonatype.nexus.client.NexusClientException;
import org.sonatype.nexus.client.NexusConnectionException;
import org.sonatype.nexus.client.rest.NexusRestClient;
import org.sonatype.nexus.rest.model.NexusArtifact;

//import org.apache.log4j.Logger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.common.im.AdministrativeStateException;
import org.openecomp.policy.common.im.IntegrityMonitor;
import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientBuilders.PublisherBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;


/**
 * BRMSPush: Application responsible to push policies to the BRMS PDP Policy Repository (PR). 
 * Mavenize and push policy to PR
 * 
 * @version 0.4 
 */
@SuppressWarnings("deprecation")
public class BRMSPush {

	
	private static final Logger logger = FlexLogger.getLogger(BRMSPush.class.getName());
	private static final String projectsLocation = "RuleProjects";
	private static final String goals[] = {"clean", "deploy"};

	private static Map<String, String> modifiedGroups = new HashMap<String, String>();
	private static IntegrityMonitor im;
	private static BackUpMonitor bm;
	private static  String resourceName = null;
	private List<String> groupIDs = null; 
	private List<String> artifactIDs= null; //"test"
	private Map<String,Integer> names= null; // "Rules"
	private String defaultName = null;
	private String repID = null; // "ecomp_policy-3rd-party"
	private String repName = null; // "d2policy-snapshots"
	private String repURL= null; 
	private String repUserName = null;
	private ArrayList<ControllerPOJO> controllers; 
	private HashMap<String,String> versions = new HashMap<String, String>();
	private String repPassword = null;
	private String policyKeyID = null;
	private List<File> matchingList = null;
	private boolean createFlag = false;
	private String uebList = null;
	private String pubTopic = null;
	private PublisherBuilder pubBuilder = null;
	private Long uebDelay = Long.parseLong("5000");
	private static String brmsdependencyversion = null;

	public BRMSPush(String propertiesFile, BackUpHandler handler) throws Exception{
		Properties config = new Properties();
		Path file = Paths.get(propertiesFile);
		if(Files.notExists(file)){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Config File doesn't Exist in the specified Path " + file.toString());
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE+"Config File doesn't Exist in the specified Path " + file.toString());
		}else{
			if(file.toString().endsWith(".properties")){
				InputStream in;
				in = new FileInputStream(file.toFile());
				config.load(in);
				// Grab the Properties. 
				defaultName = config.getProperty("defaultName").replaceAll(" ", "");
				if(defaultName==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "defaultName property is missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "defaultName property is missing from the property file");
				}
				repID = config.getProperty("repositoryID").replaceAll(" ", "");
				if(repID==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryID property is missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryID property is missing from the property file ");
				}
				repName = config.getProperty("repositoryName").replaceAll(" ", "");
				if(repName==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryName property is missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryName property is missing from the property file ");
				}
				repURL = config.getProperty("repositoryURL").replaceAll(" ", "");
				if(repURL==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryURL property is missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "repositoryURL property is missing from the property file ");
				}
				repUserName = config.getProperty("repositoryUsername").trim();
				repPassword = config.getProperty("repositoryPassword").trim();
				if(repUserName==null || repPassword==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "repostoryUserName and respositoryPassword properties are required.");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "repostoryUserName and respositoryPassword properties are required.");
				}
				policyKeyID = config.getProperty("policyKeyID").replaceAll(" ", "");
				if(policyKeyID==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "policyKeyID property is missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "policyKeyID property is missing from the property file ");
				}
				brmsdependencyversion = config.getProperty("brms.dependency.version").replaceAll(" ", "");
				if(brmsdependencyversion==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "brmsdependencyversion property is missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "brmsdependencyversion property is missing from the property file ");
				}
				readGroups(config);
				logger.info("Trying to set up IntegrityMonitor");
				try {
					logger.info("Trying to set up IntegrityMonitor");
					resourceName = config.getProperty("RESOURCE_NAME").replaceAll(" ", "");;
					if(resourceName==null){
						logger.warn("RESOURCE_NAME is missing setting default value. ");
						resourceName = "brmsgw_pdp01";
					}
					im = IntegrityMonitor.getInstance(resourceName, config);
				} catch (Exception e) {
					logger.error("Error starting Integerity Monitor: " + e);
				}
				logger.info("Trying to set up BackUpMonitor");
				try {
					bm = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), resourceName, config, handler);
				} catch (Exception e) {
					logger.error("Error starting BackUpMonitor: " + e);
				}
				// Setting up the Publisher for UEB
				uebList = config.getProperty("UEB_URL").trim();
				pubTopic = config.getProperty("UEB_TOPIC").trim();
				String apiKey = config.getProperty("UEB_API_KEY");
				String apiSecret = config.getProperty("UEB_API_SECRET");
				if(uebList==null || pubTopic==null){
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
					throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
				} 
				pubBuilder = new CambriaClientBuilders.PublisherBuilder();
				pubBuilder.usingHosts(uebList)
				.onTopic(pubTopic);
				if(apiKey!=null && !apiKey.isEmpty() && 
						apiSecret!=null && !apiSecret.isEmpty()) {
					apiKey= apiKey.trim();
					apiSecret = apiSecret.trim();
					pubBuilder.authenticatedBy(apiKey, apiSecret);
				}
				String uDelay = config.getProperty("UEB_DELAY");
				if(uDelay!=null && !uDelay.isEmpty()){
					uDelay = uDelay.trim();
					try{
						uebDelay = Long.parseLong(uDelay);
					}catch (NumberFormatException e){
						logger.error("UEB_DELAY not a long format number" + e);
					}
				}
			}
		}
	}

	/**
	 * Will Initialize the variables required for BRMSPush. 
	 */
	public void initiate() {
		modifiedGroups =  new HashMap<String, String>();
		controllers = new ArrayList<ControllerPOJO>();
		try {
			bm.updateNotification();
		} catch (Exception e) {
			logger.error("Error while updating Notification: "  + e.getMessage());
		}
	}

	/**
	 * Will Add rules to projects. Creates necessary folders if required. 
	 */
	public void addRule(String name, String rule, Map<String, String> responseAttributes) {
		// 1 check the response Attributes and determine if this belongs to any projects. 
		// 2 if not create folder // new File("Projects\\test").mkdirs();
		// 3 create pom. 
		// 4 copy the rule. 
		// 5 store the groups that have been updated. 
		String kSessionName=null;
		String selectedName = null;

		if(!responseAttributes.isEmpty()){
			// Pick selected Value
			for(String key: responseAttributes.keySet()){
				if(key.equals(policyKeyID)){
					selectedName = responseAttributes.get(key);
				}
				//kmodule configurations
				else if (key.equals("kSessionName")){
					kSessionName=responseAttributes.get(key);
				}
			}

		}
		// If no Match then pick Default. 
		if(selectedName==null){
			selectedName = defaultName;
		}
		if(names.containsKey(selectedName)){
			//If the key is not got as parameters set by the user, setting the default value for kSessionName as closedLoop
			if(kSessionName==null){
				if(selectedName==defaultName){
					kSessionName="closedloop";
				}else{
					kSessionName= selectedName;
				}
			}
			// create directories if missing.
			createProject(projectsLocation+File.separator+getArtifactID(selectedName)+File.separator+"src"+File.separator+"main"+File.separator+"resources",kSessionName);
			copyDataToFile(projectsLocation+File.separator+getArtifactID(selectedName)+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"rules"+File.separator+name+".drl", rule);
			addModifiedGroup(selectedName, "update"); // Will check for Create Later after generating the Pom. 
		}
	}

	/**
	 * Will Push policies to the PolicyRepo. 
	 * 
	 * @param notificationType <String> type of notification Type. 
	 */
	public void pushRules(){
		// Check how many groups have been updated. 
		// Invoke their Maven process.
		try {
			im.startTransaction();
		} catch (AdministrativeStateException e) {
			logger.error("Error while starting Transaction " + e);
		} catch (Exception e) {
			logger.error("Error while starting Transaction " + e);
		}
		if(!modifiedGroups.isEmpty()){
			Boolean flag = false;
			for(String group: modifiedGroups.keySet()){
				try{
					InvocationRequest request = new DefaultInvocationRequest();
					createPom(group);
					request.setPomFile(new File(projectsLocation+File.separator+getArtifactID(group)+File.separator+"pom.xml"));
					request.setGoals(Arrays.asList(goals));
					Invoker invoker = new DefaultInvoker();
					InvocationResult result = invoker.execute(request);
					if(result.getExecutionException()!=null){
						logger.error(result.getExecutionException());
					}else if(result.getExitCode()!=0){
						logger.error("Maven Invocation failure..!");
					}
					if(result.getExitCode()==0){
						logger.info("Build Completed..!");
						if (createFlag) {
                            addNotification(group, "create");
                        }else{
                            addNotification(group, modifiedGroups.get(group));
                        }
                        flag = true;
					}
				}catch(Exception e){
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Maven Invocation issue for "+getArtifactID(group) + e.getMessage());
				}
			}
			if(flag){
				sendNotification(controllers);
	        }
		}
		im.endTransaction();
	}

	/**
	 * Removes a Rule from Rule Projects. 
	 */
	public void removeRule(String name){
		File file = new File(projectsLocation);
		matchingList = new ArrayList<File>();
		searchFile(file,name);
		for(File matchingFile: matchingList){
			if(matchingFile.delete()){
				logger.info("Deleted File.. " + matchingFile.getAbsolutePath());
				String groupName = getName(matchingFile.toString());
				String ruleFolder= projectsLocation+File.separator+getArtifactID(groupName)+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"rules";
				if(new File(ruleFolder).listFiles().length==0){
					removedRuleModifiedGroup(groupName);
				}else{
					addModifiedGroup(groupName, "update"); // This is an update in terms of PDPD. 
				}
			}
		}
	}

	private void addModifiedGroup(String controllerName, String operation) {
		 modifiedGroups.put(controllerName, operation);
	}

	private void addNotification(String controllerName, String operation) {
		ControllerPOJO controllerPOJO = new ControllerPOJO();
		controllerPOJO.setName(controllerName);
		controllerPOJO.setOperation(operation);
		HashMap<String, String> drools = new HashMap<String, String>();
		drools.put("groupId", getGroupID(controllerName));
		drools.put("artifactId", getArtifactID(controllerName));
		drools.put("version", versions.get(controllerName));
		controllerPOJO.setDrools(drools);
		controllers.add(controllerPOJO);
		try {
			logger.debug("Notification added: "
					+ PolicyUtils.objectToJsonString(controllerPOJO));
		} catch (JsonProcessingException e) {
			logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID
					+ "Json Processing Error " + e);
		}
	}

	private void removedRuleModifiedGroup(String controllerName){
		// This will be sending Notification to PDPD directly to Lock
		ControllerPOJO controllerPOJO = new ControllerPOJO();
		controllerPOJO.setName(controllerName);
		controllerPOJO.setOperation("lock");
		List<ControllerPOJO> controllers = new ArrayList<ControllerPOJO>();
		controllers.add(controllerPOJO);
		sendNotification(controllers);
	}

	private void sendNotification(List<ControllerPOJO> controllers){
		NotificationPOJO notification = new NotificationPOJO();
		String requestId = UUID.randomUUID().toString();
		logger.info("Generating notification RequestID : " + requestId);
		notification.setRequestID(requestId);
		notification.setEntity("controller");
		notification.setControllers(controllers);
		try {
			String notificationJson = PolicyUtils.objectToJsonString(notification);
			logger.info("Sending Notification :\n" + notificationJson);
			sendMessage(notificationJson);
		} catch (IOException | GeneralSecurityException | InterruptedException e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while sending notification to PDP-D " + e.getMessage());
		}
	}

	private void sendMessage(String message) throws IOException, GeneralSecurityException, InterruptedException {
		// Sending Message through UEB interface.
		CambriaBatchingPublisher pub = pubBuilder.build();
		pub.send( "MyPartitionKey", message);
		logger.debug("Message Published on UEB :" + uebList + "for Topic: " + pubTopic);
		Thread.sleep(uebDelay);
		pub.close();
	}

	private void searchFile(File file, String name) {
		if(file.isDirectory()){
			logger.info("Searching Directory..." + file.getAbsolutePath());
			if(file.canRead()){
				for(File temp: file.listFiles()){
					if(temp.isDirectory()){
						// Recursive search. 
						searchFile(temp, name);
					}else{
						if(temp.getName().equals(name+".drl")){
							matchingList.add(temp);
						}
					}
				}
			}
		}
	}

	private void createPom(String name){
		Model model = new Model();
		model.setModelVersion("4.0.0");
		model.setGroupId(getGroupID(name));
		model.setArtifactId(getArtifactID(name));
		model.setVersion(incrementVersion(name));
		model.setName(name);
		DistributionManagement distributionManagement = new DistributionManagement();
		DeploymentRepository repository = new DeploymentRepository();
		repository.setId(repID);
		repository.setName(repName);
		repository.setUrl(repURL);
		distributionManagement.setRepository(repository);
		model.setDistributionManagement(distributionManagement);
		// Depenendency Mangement goes here. 
		List<Dependency> dependencyList= new ArrayList<Dependency>();

		String version= StringEscapeUtils.escapeJava(brmsdependencyversion);

		Dependency demoDependency = new Dependency();
		demoDependency.setGroupId("org.openecomp.policy.drools-applications");
		demoDependency.setArtifactId("demo");
		demoDependency.setVersion(version);
		dependencyList.add(demoDependency);

		Dependency controlloopDependency = new Dependency();
		controlloopDependency.setGroupId("org.openecomp.policy.drools-applications");
		controlloopDependency.setArtifactId("controlloop");
		controlloopDependency.setVersion(version);
		dependencyList.add(controlloopDependency);

		Dependency restDependency = new Dependency();
		restDependency.setGroupId("org.openecomp.policy.drools-applications");
		restDependency.setArtifactId("rest");
		restDependency.setVersion(version);
		dependencyList.add(restDependency);

		Dependency appcDependency = new Dependency();
		appcDependency.setGroupId("org.openecomp.policy.drools-applications");
		appcDependency.setArtifactId("appc");
		appcDependency.setVersion(version);
		dependencyList.add(appcDependency);

		Dependency aaiDependency = new Dependency();
		aaiDependency.setGroupId("org.openecomp.policy.drools-applications");
		aaiDependency.setArtifactId("aai");
		aaiDependency.setVersion(version);
		dependencyList.add(aaiDependency);

		Dependency msoDependency = new Dependency();
		msoDependency.setGroupId("org.openecomp.policy.drools-applications");
		msoDependency.setArtifactId("mso");
		msoDependency.setVersion(version);
		dependencyList.add(msoDependency);

		Dependency trafficgeneratorDependency = new Dependency();
		trafficgeneratorDependency.setGroupId("org.openecomp.policy.drools-applications");
		trafficgeneratorDependency.setArtifactId("trafficgenerator");
		trafficgeneratorDependency.setVersion(version);	
		dependencyList.add(trafficgeneratorDependency);


		model.setDependencies(dependencyList);

		Writer writer = null;
		try{
			writer = WriterFactory.newXmlWriter(new File(projectsLocation+File.separator+getArtifactID(name)+File.separator+"pom.xml"));
			MavenXpp3Writer pomWriter = new MavenXpp3Writer();
			pomWriter.write(writer, model);
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Error while creating POM for " + getArtifactID(name) + e.getMessage());
		}finally{
			IOUtil.close(writer);
		}
	}

	private void createProject(String path,String ksessionName){
		new File(path+File.separator+"rules").mkdirs();
		new File(path+File.separator+"META-INF").mkdirs();
		if(!Files.exists(Paths.get(path+File.separator+"META-INF"+File.separator+"kmodule.xml"))){
			// Hard coding XML for PDP Drools to accept our Rules. 
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"+  
					"<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">" +"\n"+
					"<kbase name=\"rules\" packages=\"rules\">" + "\n" +
					"<ksession name=\""+ ksessionName +"\"/>"+ "\n" + 
					"</kbase></kmodule>";
			copyDataToFile(path+File.separator+"META-INF"+File.separator+"kmodule.xml", xml);
		}
	}

	private void copyDataToFile(String file, String rule) {
		try{
			FileUtils.writeStringToFile(new File(file), rule);
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Error while creating Rule for " + file + e.getMessage());
		}
	}

	private void readGroups(Properties config) throws Exception{
		String[] groupNames = null;
		if(config.getProperty("groupNames").contains(",")){
			groupNames = config.getProperty("groupNames").replaceAll(" ", "").split(",");
		}else{
			groupNames = new String[]{config.getProperty("groupNames").replaceAll(" ", "")};
		}
		if(groupNames==null || groupNames.length==0){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "groupNames property is missing or empty from the property file ");
			throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + "groupNames property is missing or empty from the property file ");
		}
		names = new HashMap<String, Integer>();
		groupIDs = new ArrayList<String>();
		artifactIDs = new ArrayList<String>();
		for(int counter=0; counter < groupNames.length ;counter++){
			String name = groupNames[counter];
			String groupID = config.getProperty(name+".groupID");
			if(groupID==null){
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + name+".groupID property is missing from the property file ");
				throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + name+".groupID property is missing from the property file ");
			}
			String artifactID = config.getProperty(name+".artifactID");
			if(artifactID==null){
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + name+".artifactID property is missing from the property file ");
				throw new Exception(XACMLErrorConstants.ERROR_DATA_ISSUE + name+".artifactID property is missing from the property file ");
			}
			// Add to list if we got all 
			names.put(name, counter);
			groupIDs.add(groupID);
			artifactIDs.add(artifactID);
		}
	}

	private String getGroupID(String name){
		return groupIDs.get(names.get(name));
	}

	private String getArtifactID(String name){
		return artifactIDs.get(names.get(name));
	}

	private String getName(String filePath){
		filePath = filePath.replaceFirst(projectsLocation, "").substring(1);
		String artifactName = filePath.substring(0, filePath.indexOf(File.separator));
		for(String name : names.keySet()){
			if(artifactName.equals(getArtifactID(name))){
				return name;
			}
		}
		// If not found return default
		return defaultName;
	}

	private String incrementVersion(String name) {
		final NexusClient client = new NexusRestClient();
		String newVersion = "0.1.0";
		createFlag = false;
		try {
			client.connect(repURL.substring(0, repURL.indexOf(repURL.split(":[0-9]+\\/nexus")[1])), repUserName, repPassword);
			final NexusArtifact template = new NexusArtifact();
			template.setGroupId(getGroupID(name));
			template.setArtifactId(getArtifactID(name));
			final List<NexusArtifact> artifacts = client.searchByGAV(template);
			int bigMajor = 0;
			int bigMinor = 0;
			for(NexusArtifact artifact : artifacts){
				String version = artifact.getVersion();
				int majorVal = Integer.parseInt(version.substring(0, version.indexOf(".")));
				int minorVal = Integer.parseInt(version.substring(version.indexOf(".")+1,version.lastIndexOf(".")));
				if(majorVal > bigMajor){
					bigMajor = majorVal;
					bigMinor = minorVal;
				}else if((bigMajor==majorVal) && (minorVal > bigMinor)){
					bigMinor = minorVal;
				}
			}
			if(bigMinor>=9){
				bigMajor = bigMajor+1;
				bigMinor = 0;
			}else{
				bigMinor = bigMinor+1;
			}
			if(artifacts.isEmpty()){
				// This is new artifact.
				newVersion = "0.1.0";
			}else{
				newVersion =  bigMajor + "." + bigMinor + artifacts.get(0).getVersion().substring(artifacts.get(0).getVersion().lastIndexOf("."));
			}
		} catch (NexusClientException | NexusConnectionException | NullPointerException e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "version number increment failed will be using default version " +e.getMessage());
		} finally {
			try {
				client.disconnect();
			} catch (NexusClientException | NexusConnectionException e) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "failed to disconnect Connection from Nexus." +e.getMessage());
			}
		}
		if(newVersion.equals("0.1.0")){
			createFlag = true;
		}
		versions.put(name, newVersion);
		logger.info("Controller: " + name + "is on version: "+ newVersion);
		return newVersion;
	}

	// Return BackUpMonitor 
	public static BackUpMonitor getBackUpMonitor(){
		return bm;
	}
}
