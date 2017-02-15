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

package org.openecomp.policy.controller;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.openecomp.policy.adapter.PolicyExportAdapter;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.elk.client.ElkConnector;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.utils.XACMLPolicyWriterWithPapNotify;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


@Controller
@RequestMapping("/")
public class PolicyExportAndImportController extends RestrictedBaseController {
	private static Logger	logger	= FlexLogger.getLogger(PolicyExportAndImportController.class);
	private ArrayList<File> selectedPolicy;
	public static String CONFIG_HOME = PolicyController.getConfigHome();
	public static String ACTION_HOME = PolicyController.getActionHome();
	
	private Set<String> scopes = null;
	private List<String> roles = null;
	private Boolean superadmin = false;
	private Boolean showMessage = false;
	private static final int BUFFER_SIZE = 4096;
	private Path directory = PolicyController.getGitPath();

	//Scopes Which Super Editor can't import
	private Set<String> sEditorScopesCantCreate = new HashSet<String>();
	//List of scopes in Tar file
	private Set<String> listOfTarNewScopes = new HashSet<String>();
	//List of xml policies
	private ArrayList<String> xacmlFiles = new ArrayList<String>();
	//Scopes of the User based on the Roles 
	private List<String> userScopes = null;
	//Directory names from tar file
	private Set<String> directoryNames = new HashSet<String>();
	//compare the scopes of the user and tar file get unique
	private Set<String> finalScopesToImport = new HashSet<String>();
	//final scopes User can import
	private Set<String> finalScopes = new HashSet<String>();
	//User don't have permissions to import to scopes
	private Set<String> roleIsNotSufficient = new HashSet<String>();
	//User don't have access to import new scopes
	private Set<String> userDontHavePermission = new HashSet<String>();

	private Map<String, Roles> scopeAndRoles = null;

	private static PolicyVersionDao policyVersionDao;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	private PolicyExportAndImportController(PolicyVersionDao policyVersionDao){
		PolicyExportAndImportController.policyVersionDao = policyVersionDao;
	}
	
	public PolicyExportAndImportController(){}
	
	@RequestMapping(value={"/policy_download/exportPolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView ExportPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			selectedPolicy = new ArrayList<File>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyExportAdapter adapter = mapper.readValue(root.get("exportData").toString(), PolicyExportAdapter.class);
			for (Object policyId :  adapter.getPolicyDatas()) {
				LinkedHashMap<?, ?> selected = (LinkedHashMap<?, ?>)policyId;
				Path file =  Paths.get(selected.get("filePath").toString());
				selectedPolicy.add(file.toFile());
			}
			// Grab our working repository
			//
			final Path repoPath = PolicyController.getGitPath().toAbsolutePath();
			if(CONFIG_HOME == null || ACTION_HOME == null ){
				CONFIG_HOME =  PolicyController.getConfigHome();
				ACTION_HOME = PolicyController.getActionHome();
			}
			Path configPath = Paths.get(CONFIG_HOME);
			Path actionPath = Paths.get(ACTION_HOME);
			String tempDir = System.getProperty("catalina.base") + File.separator + "webapps" + File.separator + "temp";
			File temPath = new File(tempDir);
			if(!temPath.exists()){
				temPath.mkdir();
			}
			final Path tarFile = Paths.get(tempDir, "Repository.tar");
			try (OutputStream os = Files.newOutputStream(tarFile)) {
				try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(os)) {
					tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
					Files.walkFileTree(configPath, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							for (File policyId : selectedPolicy) {
								// Get the current selection
								String policyDir = policyId.toString();
								policyDir = policyDir.substring(policyDir.indexOf("repository")+11);
								String path = policyDir.replace('\\', '.');
								if(path.contains("/")){
									path = policyDir.replace('/', '.');
									logger.info("print the path:" +path);
								}
								path = FilenameUtils.removeExtension(path);
								if (path.endsWith(".xml")) {
									path = path.substring(0, path.length() - 4);
								}
								File configFile = new File(path);
								String fileName = FilenameUtils.removeExtension(file.getFileName().toString());
								File configHome = new File(fileName);
								if(configFile.equals(configHome)) {
									Path relative = configPath.relativize(file);
									TarArchiveEntry entry = new TarArchiveEntry(relative.toFile());
									entry.setSize(Files.size(file));
									tarOut.putArchiveEntry(entry);
									try {
										IOUtils.copy(Files.newInputStream(file), tarOut);
									} catch (IOException e) {
										logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
									}
									tarOut.closeArchiveEntry();
									return super.visitFile(file, attrs);
								}
							}
							return super.visitFile(file, attrs);
						}
					});

					Files.walkFileTree(actionPath, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							for (File policyId : selectedPolicy) {
								// Get the current selection
								String policyDir = policyId.toString();
								policyDir = policyDir.substring(policyDir.indexOf("repository")+11);
								String path = policyDir.replace('\\', '.');
								if(path.contains("/")){
									path = policyDir.replace('/', '.');
									logger.info("print the path:" +path);
								}
								path = FilenameUtils.removeExtension(path);
								if (path.endsWith(".xml")) {
									path = path.substring(0, path.length() - 4);
								}
								File actionFile = new File(path);
								String fileName = FilenameUtils.removeExtension(file.getFileName().toString());
								File actionHome = new File(fileName);
								if(actionFile.equals(actionHome)) {
									Path relative = actionPath.relativize(file);
									TarArchiveEntry entry = new TarArchiveEntry(relative.toFile());
									entry.setSize(Files.size(file));
									tarOut.putArchiveEntry(entry);
									try {
										IOUtils.copy(Files.newInputStream(file), tarOut);
									} catch (IOException e) {
										logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
									}
									tarOut.closeArchiveEntry();
									return super.visitFile(file, attrs);
								}
							}
							return super.visitFile(file, attrs);
						}
					});

					Files.walkFileTree(repoPath, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							if (file.getFileName().toString().endsWith(".xml") == false) {
								return super.visitFile(file, attrs);
							}
							for (File policyId : selectedPolicy) {
								// Get the current selection
								if(policyId.getAbsoluteFile().getName().equals(file.getFileName().toString())) {
									Path relative = repoPath.relativize(file);
									TarArchiveEntry entry = new TarArchiveEntry(relative.toFile());
									entry.setSize(Files.size(file));
									tarOut.putArchiveEntry(entry);
									try {
										IOUtils.copy(Files.newInputStream(file), tarOut);
									} catch (IOException e) {
										logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
									}
									tarOut.closeArchiveEntry();
									return super.visitFile(file, attrs);
								}
							}
							return super.visitFile(file, attrs);
						}
					});

					tarOut.closeArchiveEntry();
					tarOut.finish();
					logger.debug("closing the tar file");	
				}				
			} catch (IOException e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "getting IOexception");
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			}
			logger.info("trying to send tar file "+tarFile.toAbsolutePath().toString());
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");
			String successMap = tarFile.toString().substring(tarFile.toString().lastIndexOf("webapps")+8);
			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(successMap);
			JSONObject j = new JSONObject("{data: " + responseString + "}");
			out.write(j.toString());
			
	      
	        return null;
		}catch(Exception e){
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while Exporting Policies"+e);
		}
		return null;	
	}
	
	//Policy Import 
	public JSONObject ImportRepositoryFile(String file, HttpServletRequest request){
		TarArchiveEntry entry = null;
		TarArchiveInputStream extractFile = null;
		try {
			extractFile = new TarArchiveInputStream (new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String userId = null;
		try {
			userId = UserUtils.getUserIdFromCookie(request);
		} catch (Exception e) {
			logger.error("Exception Occured while reading userid from cookie" +e);
		}
		scopeAndRoles = PolicyController.getUserRoles(userId);
		//Check if the Role and Scope Size are Null get the values from db. 
		List<Roles> userRoles = PolicyController.getRoles(userId);
		roles = new ArrayList<String>();
		scopes = new HashSet<String>();
		for(Roles userRole: userRoles){
			roles.add(userRole.getRole());
			scopes.add(userRole.getScope());
		}	
		//Create a loop to read every single entry in TAR file 
		try {
			if (roles.contains("super-admin") || roles.contains("super-editor") || roles.contains("super-guest") ) {
				while ((entry = extractFile.getNextTarEntry()) != null) {
					this.superadmin = true;
					try{
						copyFileToLocation(extractFile, entry, xacmlFiles, null, superadmin);
					}catch(Exception e){
						logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception while Importing Polcies"+e);
					}
				}
			}else{
				this.showMessage = true;
				//get the directory names from tar file and add to the list
				while ((entry = extractFile.getNextTarEntry()) != null) {
					if(entry.getName().endsWith(".xls"))	{
						this.superadmin = true;
						try{
							copyFileToLocation(extractFile, entry, xacmlFiles, finalScopes, superadmin);
						}catch(Exception e){
							logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception while Importing Polcies"+e);
						}
					}
					if(entry.getName().endsWith(".xml")){
						String filename = null;
						if(entry.getName().contains("\\")){
							filename = entry.getName().replace("\\", File.separator);
						}else{
							filename = entry.getName().replace("/", File.separator);
						}
						directoryNames.add(filename.substring(0, filename.lastIndexOf(File.separator)));
					}

					//get the matching scopes on comparing user scopes and tar file scopes
					for(int i=0;i< userScopes.size(); i++){
						for(int j=0; j<directoryNames.size(); j++){
							if(userScopes.toArray()[i].equals(directoryNames.toArray()[j])){
								finalScopesToImport.add(userScopes.toArray()[i].toString());
							}
						}
					}
					//get the scopes for which the user have permission to import
					if(finalScopesToImport.size() != 0){
						for(int i = 0; i < finalScopesToImport.size() ; i++){
							String role = scopeAndRoles.get(finalScopesToImport.toArray()[i]).getRole();
							if(role.equalsIgnoreCase("editor") || role.equalsIgnoreCase("admin")){
								finalScopes.add(finalScopesToImport.toArray()[i].toString());
								if(role.equalsIgnoreCase("super-guest") || role.equalsIgnoreCase("guest")){
									roleIsNotSufficient.remove(finalScopesToImport.toArray()[i].toString());
								}
							}else{
								roleIsNotSufficient.add(finalScopesToImport.toArray()[i].toString());
							}
						}
					}
					//if final Scopes to import set is not 0, then copy the files
					if(finalScopes.size() != 0)	{
						this.superadmin = false;
						try{
							copyFileToLocation(extractFile, entry, xacmlFiles, finalScopes, superadmin);
						}catch(Exception e){
							logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception while Importing Polcies"+e);
						}
					}
				}	
			}
		} catch (Exception e1) {
			logger.info(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Exception:" +e1);
			e1.printStackTrace();
		} 
		// Close TarAchiveInputStream 
		try {
			extractFile.close();
		} catch (IOException e) {
			logger.info(XACMLErrorConstants.ERROR_PROCESS_FLOW+"IO Exception:"+e);
			e.printStackTrace();
		}

		File tarFile1 =  new File(file.toString());
		tarFile1.delete();

		for (String xacmlFile: xacmlFiles) {
			String policyName = xacmlFile.substring(xacmlFile.indexOf("repository")+11);
			if(policyName.endsWith(".xml")){
				policyName = policyName.replace(".xml", "");
				String version = policyName.substring(policyName.lastIndexOf(".")+1);
				String finalPolicyName = policyName.substring(0, policyName.lastIndexOf("."));
				PolicyVersion policyVersion = new PolicyVersion();
				policyVersion.setPolicyName(finalPolicyName);
				policyVersion.setActiveVersion(Integer.parseInt(version));
				policyVersion.setHigherVersion(Integer.parseInt(version));
				policyVersion.setCreatedBy(userId);
				policyVersion.setModifiedBy(userId);
				policyVersionDao.Save(policyVersion);		
			}
			
			//send to pap
			if(!XACMLPolicyWriterWithPapNotify.notifyPapOfCreateUpdate(xacmlFile)){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Failed adding imported policy to database: "+xacmlFile);
				//throw new Exception("Failed adding imported policy to database: "+xacmlFile);
			}
			try {
				File f = new File(xacmlFile);
				ElkConnector.singleton.update(f);
			} catch (Exception e) {
				logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": Cannot ELK import: " + xacmlFile + " " +
						e.getMessage(), e);
			}
		}

		//Filter the Set on Removing the Scopes which are imported
		for(int j=0; j< finalScopesToImport.size(); j++){
			for(int i = 0; i < directoryNames.size(); i++){
				if(roles.contains("admin") || roles.contains("editor")){
					if(!((directoryNames.toArray()[i]).toString()).startsWith(finalScopesToImport.toArray()[j].toString())){
						userDontHavePermission.add(directoryNames.toArray()[i].toString());
					}
				}
			}
		}

		//Show the Warn message to the User, if any of the policies are not Imported to teh Scopes
		if(showMessage){
			if(roleIsNotSufficient.size() == 0 && userDontHavePermission.size() != 0){
				logger.warn(" User don't have Permissions to Import Policies to following Scopes:'"+userDontHavePermission+"'");
			}
			else if(roleIsNotSufficient.size() != 0 && userDontHavePermission.size() == 0){
				logger.warn("Please Contact Super-Admin to Import the New Scopes:'"+roleIsNotSufficient+"'");
			}
			else if(roleIsNotSufficient.size() != 0 && userDontHavePermission.size() != 0){
				logger.warn("Please Contact Super-Admin to Import the New Scopes:'"+roleIsNotSufficient+"' User don't have Permissions to Import Policies to following Scopes:'"+userDontHavePermission+"'");
			}else if(sEditorScopesCantCreate.size() != 0){
				logger.warn("Super-Editor can't create following new Scopes" +sEditorScopesCantCreate);
			}else if(finalScopesToImport.size() == 0 && directoryNames.size() != 0){
				logger.warn(" User don't have Permissions to Import Policies to following Scopes:'"+directoryNames+"'");
			}
		}
		return null;
	}

	//Copy files to Directorys 
	private void copyFileToLocation(TarArchiveInputStream extractFile, TarArchiveEntry entry, ArrayList<String> xacmlFiles, Set<String> finalScopes, Boolean superadminValue ) throws IOException{
		String individualFiles = "";
		int offset = 0;
		FileOutputStream outputFile=null;
		// Get the name of the file
		if(superadminValue){
			individualFiles = entry.getName();
			//Add the list of scopes
			if(entry.getName().endsWith(".xml")){
				String filename = null;
				if(entry.getName().contains("\\")){
					filename = entry.getName().replace("\\", File.separator);
				}else{
					filename = entry.getName().replace("/", File.separator);
				}
				if(roles.contains("super-editor")){
					listOfTarNewScopes.add(filename.substring(0, filename.lastIndexOf(File.separator)));
				}	
			}
		}else{
			for(int i =0; i< finalScopes.size(); i++){
				if(entry.getName().startsWith(finalScopes.toArray()[i].toString())){
					individualFiles = entry.getName();
				}
			}		
		}

		//Filter the new Scopes which Super-Editor can't create
		if(roles.contains("super-editor")){
			for(int j= 0; j< listOfTarNewScopes.size(); j++){
				boolean scopeExistsFlag = false; // This Flag is used to know if the Scope Exists. 
				for(int i=0; i < scopes.size(); i++ ){
					if(scopes.contains(listOfTarNewScopes.toArray()[j])){
						scopeExistsFlag = true;
					}	
				}
				if(!scopeExistsFlag){
					sEditorScopesCantCreate.add(listOfTarNewScopes.toArray()[j].toString());
					showMessage = true;
				}
			}	
		}

		individualFiles = individualFiles.replace("/", File.separator);
		individualFiles = individualFiles.replace("\\", File.separator);

		//Create the path with the entry name 
		String filePath = directory + File.separator + individualFiles;
		String	configPath = CONFIG_HOME + File.separator + individualFiles;
		String	 actionPath = ACTION_HOME + File.separator + individualFiles;
		logger.info("File Name in TAR File is: " + individualFiles);
		logger.info("Xml directory file path: " + filePath);
		logger.info("Config Home directory file path: " + configPath);
		logger.info("Action Home directory file path: " + actionPath);


		// Get Size of the file and create a byte array for the size 
		byte[] content = new byte[(int) entry.getSize()];

		offset=0;
		logger.info("File Name in TAR File is: " + individualFiles);
		logger.info("Size of the File is: " + entry.getSize());                  
		// Read file from the archive into byte array 
		extractFile.read(content, offset, content.length - offset);
		if (!entry.isDirectory()) {
			if(!individualFiles.contains(".Config_") || !individualFiles.contains(".Action_")){
				// if the entry is a file, extracts it
				String filePath1 = filePath.substring(0, filePath.lastIndexOf(File.separator));
				File newFile = new File(filePath1);
				if(!(newFile.exists()) && !(roles.contains("super-editor") || roles.contains("editor"))) {
					File dir = new File(filePath1);
					dir.mkdir();
					extractFile(extractFile, filePath);
				}
			}
		} else {
			// if the entry is a directory, make the directory
			if(!(roles.contains("super-editor") || roles.contains("editor"))){
				File dir = new File(filePath);
				dir.mkdir();
			}
		} 
		// Define OutputStream for writing the file 
		if(individualFiles.contains(".Config_")){
			outputFile=new FileOutputStream(new File(configPath));
		}else if(individualFiles.contains(".Action_")){
			outputFile=new FileOutputStream(new File(actionPath));
		}else{
			if(filePath != null){
				outputFile=new FileOutputStream(new File(filePath));
				xacmlFiles.add(filePath);
			}
		}

		// Use IOUtiles to write content of byte array to physical file 
		IOUtils.write(content,outputFile); 

		// Close Output Stream 
		try {
			outputFile.close();
		} catch (IOException e) {
			logger.info("IOException:" +e);
			e.printStackTrace();
		}
	}

	private void extractFile(TarArchiveInputStream extractFile, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = extractFile.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();

	}
}
