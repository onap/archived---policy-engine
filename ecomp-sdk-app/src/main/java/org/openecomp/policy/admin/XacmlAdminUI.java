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

package org.openecomp.policy.admin;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openecomp.policy.rest.XACMLRest;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.rest.util.Webapps;
import org.openecomp.policy.xacml.api.pap.PAPPolicyEngine;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.research.xacml.util.XACMLProperties;
import com.google.common.base.Splitter;



public class XacmlAdminUI extends HttpServlet implements PAPNotificationBroadcaster.PAPNotificationBroadcastListener{

	private static final long serialVersionUID = 1L;
	//
	// The PAP Engine
	//
	private PAPPolicyEngine papEngine;
	private static Path repositoryPath;
	private static Repository repository;
	
	@Autowired
	UserInfoDao userInfoDao;
	
	@Autowired
	SessionFactory sessionfactory;
	
	@WebServlet(value = "/policy#/*", description = "XACML Admin Console", asyncSupported = true, loadOnStartup = 1, initParams = { @WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.admin.properties", description = "The location of the properties file holding configuration information.") })
	public static class Servlet extends HttpServlet {
		private static final long serialVersionUID = -5274600248961852835L;

		@Override
		public void init(ServletConfig servletConfig) throws ServletException {
			super.init(servletConfig);
			//
			// Common initialization
			//
			XACMLRest.xacmlInit(servletConfig);
			//
			// Initialize GIT repository.
			//
			XacmlAdminUI.initializeGitRepository();
			//
			// Read the Props
			// The webapps Action and Config are read when getActionHome or getConfigHome are called
			try {
				getConfigHome();
			} catch (Exception e) {
				throw new ServletException(e);
			}

		}


		@Override
		public void destroy() {
			if (XacmlAdminUI.repository != null) {
				XacmlAdminUI.repository.close();
			}
			super.destroy();
		}
	}
	
	private static void initializeGitRepository() throws ServletException {
		
		try {
			XacmlAdminUI.repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_REPOSITORY));
		} catch (Exception e) {
			XACMLProperties.reloadProperties();
			XacmlAdminUI.repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_REPOSITORY));
		}
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			XacmlAdminUI.repository = builder.setGitDir(XacmlAdminUI.repositoryPath.toFile()).readEnvironment().findGitDir().setBare().build();
			if (Files.notExists(XacmlAdminUI.repositoryPath)|| Files.notExists(Paths.get(XacmlAdminUI.repositoryPath.toString(), "HEAD"))) {
				//
				// Create it if it doesn't exist. As a bare repository
				XacmlAdminUI.repository.create();
				//
				// Add the magic file so remote works.
				//
				Path daemon = Paths.get(XacmlAdminUI.repositoryPath.toString(), "git-daemon-export-ok");
				Files.createFile(daemon);
			}
		} catch (IOException e) {
			throw new ServletException(e.getMessage(), e.getCause());
		}
		//
		// Make sure the workspace directory is created
		//
		Path workspace = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_WORKSPACE));
		workspace = workspace.toAbsolutePath();
		if (Files.notExists(workspace)) {
			try {
				Files.createDirectory(workspace);
			} catch (IOException e) {
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		//
		// Create the user workspace directory
		//
		workspace = Paths.get(workspace.toString(), "admin");
		
		if (Files.notExists(workspace)) {
			try {
				Files.createDirectory(workspace);
			} catch (IOException e) {
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		//
		// Get the path to where the repository is going to be
		//
		Path gitPath = Paths.get(workspace.toString(), XacmlAdminUI.repositoryPath.getFileName().toString());
		if (Files.notExists(gitPath)) {
			try {
				Files.createDirectory(gitPath);
			} catch (IOException e) {
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		//
		// Initialize the domain structure
		//
		String base = null;
		String domain = XacmlAdminUI.getDomain();
		if (domain != null) {
			for (String part : Splitter.on(':').trimResults().split(domain)) {
				if (base == null) {
					base = part;
				}
				Path subdir = Paths.get(gitPath.toString(), part);
				if (Files.notExists(subdir)) {
					try {
						Files.createDirectory(subdir);
						Files.createFile(Paths.get(subdir.toString(), ".svnignore"));
					} catch (IOException e) {
						throw new ServletException(e.getMessage(), e.getCause());
					}
				}
			}
		} else {
			try {
				Files.createFile(Paths.get(workspace.toString(), ".svnignore"));
				base = ".svnignore";
			} catch (IOException e) {
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		try {
			//
			// These are the sequence of commands that must be done initially to
			// finish setting up the remote bare repository.
			//
			Git git = Git.init().setDirectory(gitPath.toFile()).setBare(false).call();
			git.add().addFilepattern(base).call();
			git.commit().setMessage("Initialize Bare Repository").call();
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", XacmlAdminUI.repositoryPath.toAbsolutePath().toString());
			config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
			config.save();
			git.push().setRemote("origin").add("master").call();
			/*
			 * This will not work unless
			 * git.push().setRemote("origin").add("master").call(); is called
			 * first. Otherwise it throws an exception. However, if the push()
			 * is called then calling this function seems to add nothing.
			 * 
			 * git.branchCreate().setName("master")
			 * .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
			 * .setStartPoint("origin/master").setForce(true).call();
			 */
		} catch (GitAPIException | IOException e) {
			throw new ServletException(e.getMessage(), e.getCause());
		}
	}

	public UserInfo getUserNameFromUserInfoTable(String createdBy){
		String loginId = createdBy;
		Object user = null;
		Session session = sessionfactory.openSession();
		user = session.load(UserInfo.class, loginId);
		return (UserInfo) user;
	}
	
	@Override
	public void updateAllGroups() {

	}

	public PAPPolicyEngine getPapEngine() {
		return papEngine;
	}

	public void setPapEngine(PAPPolicyEngine papEngine) {
		this.papEngine = papEngine;
	}
	
	public static String getConfigHome() {
		return Webapps.getConfigHome();
	}
	
	public static String getDomain() {
		return XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_DOMAIN, "urn");
	}
	
	// get the repository path from property file
	public static Path getRepositoryPath() {
		if(repositoryPath == null){
			try {
				initializeGitRepository();
			} catch (ServletException e) {

			}
		}
		return repositoryPath;
	}
	
	
}

