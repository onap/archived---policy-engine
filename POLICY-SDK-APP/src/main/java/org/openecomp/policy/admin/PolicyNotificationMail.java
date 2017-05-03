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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.controller.PolicyController;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.WatchPolicyNotificationTable;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Configurable
public class PolicyNotificationMail{
	private static Logger LOGGER	= FlexLogger.getLogger(PolicyNotificationMail.class);
	
	@Bean
	public JavaMailSenderImpl javaMailSenderImpl(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(PolicyController.smtpHost);
		mailSender.setPort(Integer.parseInt(PolicyController.smtpPort));
		mailSender.setUsername(PolicyController.smtpUsername);
		mailSender.setPassword(PolicyController.smtpPassword);
		Properties prop = mailSender.getJavaMailProperties();
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.debug", "true");
		return mailSender;
	}

	@SuppressWarnings("resource")
	public void sendMail(PolicyVersion entityItem, String policyName, String mode, CommonClassDao policyNotificationDao) throws MessagingException {  
		String from = PolicyController.smtpUsername;
		String to = "";
		String subject = "";
		String message = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		if(mode.equalsIgnoreCase("EditPolicy")){
			subject = "Policy has been Updated : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.smtpApplicationName + " has been Updated" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion() 
					 + '\n'  + '\n' + "Modified By : " +entityItem.getModifiedBy() + '\n' + "Modified Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if(mode.equalsIgnoreCase("Rename")){
			subject = "Policy has been Renamed : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.smtpApplicationName + " has been Renamed" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion() 
					 + '\n'  + '\n' + "Renamed By : " +entityItem.getModifiedBy() + '\n' + "Renamed Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if(mode.equalsIgnoreCase("DeleteAll")){
			subject = "Policy has been Deleted : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.smtpApplicationName + " has been Deleted with All Versions" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n'  
					 + '\n'  + '\n' + "Deleted By : " +entityItem.getModifiedBy() + '\n' + "Deleted Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if(mode.equalsIgnoreCase("DeleteOne")){
			subject = "Policy has been Deleted : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.smtpApplicationName + " has been Deleted" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n'  +"Policy Version : " +entityItem.getActiveVersion()
					 + '\n'  + '\n' + "Deleted By : " +entityItem.getModifiedBy() + '\n' + "Deleted Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if(mode.equalsIgnoreCase("DeleteScope")){
			subject = "Scope has been Deleted : "+entityItem.getPolicyName();
			message = "The Scope Which you are watching in  " + PolicyController.smtpApplicationName + " has been Deleted" + '\n'  + '\n'  + '\n'+ "Scope + Scope Name  : "  + policyName + '\n'  
					 + '\n'  + '\n' + "Deleted By : " +entityItem.getModifiedBy() + '\n' + "Deleted Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if(mode.equalsIgnoreCase("SwitchVersion")){
			subject = "Policy has been SwitchedVersion : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.smtpApplicationName + " has been SwitchedVersion" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion() 
					 + '\n'  + '\n' + "Switched By : " +entityItem.getModifiedBy() + '\n' + "Switched Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if(mode.equalsIgnoreCase("Move")){
			subject = "Policy has been Moved to Other Scope : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.smtpApplicationName + " has been Moved to Other Scope" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion() 
					 + '\n'  + '\n' + "Moved By : " +entityItem.getModifiedBy() + '\n' + "Moved Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		String policyFileName = entityItem.getPolicyName();
		String checkPolicyName = policyFileName;
		if(policyFileName.contains("/")){
			policyFileName = policyFileName.substring(0, policyFileName.indexOf("/"));
			policyFileName = policyFileName.replace("/", File.separator);
		}
		if(policyFileName.contains("\\")){
			policyFileName = policyFileName.substring(0, policyFileName.indexOf("\\"));
			policyFileName = policyFileName.replace("\\", "\\\\");
		}
		
		String query = "from WatchPolicyNotificationTable where policyName like'" +policyFileName+"%'";
		boolean sendFlag = false;
		List<Object> watchList = policyNotificationDao.getDataByQuery(query);
		if(watchList != null){
			if(watchList.size() > 0){
				for(Object watch : watchList){
					WatchPolicyNotificationTable list = (WatchPolicyNotificationTable) watch;
					String watchPolicyName = list.getPolicyName();
					if(watchPolicyName.contains("Config_")){
						if(watchPolicyName.equals(checkPolicyName)){
							sendFlag = true;
						}
					}else if(watchPolicyName.contains("Action_")){
						if(watchPolicyName.equals(checkPolicyName)){
							sendFlag = true;
						}
					}else if(watchPolicyName.contains("Decision_")){
						if(watchPolicyName.equals(checkPolicyName)){
							sendFlag = true;
						}
					}else{
						sendFlag = true;
					}
					if(sendFlag){
						to = list.getLoginIds()+"@"+PolicyController.smtpEmailExtension;
						to = to.trim();
						AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
						ctx.register(PolicyNotificationMail.class);
						ctx.refresh();
						JavaMailSenderImpl mailSender = ctx.getBean(JavaMailSenderImpl.class);
						MimeMessage mimeMessage = mailSender.createMimeMessage();
						MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage);
						try {
							mailMsg.setFrom(new InternetAddress(from, "Policy Notification System"));
						} catch (Exception e) {
							LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Exception Occured in Policy Notification" +e);
						}
						mailMsg.setTo(to);
						mailMsg.setSubject(subject);
						mailMsg.setText(message);
						mailSender.send(mimeMessage);
					}
				}
			}
		}
	}
}
