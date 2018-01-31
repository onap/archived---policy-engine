/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.admin;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.script.SimpleBindings;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.WatchPolicyNotificationTable;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Configurable
public class PolicyNotificationMail{
	private static Logger policyLogger	= FlexLogger.getLogger(PolicyNotificationMail.class);

	@Bean
	public JavaMailSenderImpl javaMailSenderImpl(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(PolicyController.getSmtpHost());
		mailSender.setPort(Integer.parseInt(PolicyController.getSmtpPort()));
		mailSender.setUsername(PolicyController.getSmtpUsername());
		mailSender.setPassword(PolicyController.getSmtpPassword());
		Properties prop = mailSender.getJavaMailProperties();
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.debug", "true");
		return mailSender;
	}

	public void sendMail(PolicyVersion entityItem, String policyName, String mode, CommonClassDao policyNotificationDao) throws MessagingException {
		String from = PolicyController.getSmtpUsername();
		String to = "";
		String subject = "";
		String message = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		if("EditPolicy".equalsIgnoreCase(mode)){
			subject = "Policy has been Updated : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been Updated" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion()
					 + '\n'  + '\n' + "Modified By : " +entityItem.getModifiedBy() + '\n' + "Modified Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if("Rename".equalsIgnoreCase(mode)){
			subject = "Policy has been Renamed : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been Renamed" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion()
					 + '\n'  + '\n' + "Renamed By : " +entityItem.getModifiedBy() + '\n' + "Renamed Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if("DeleteAll".equalsIgnoreCase(mode)){
			subject = "Policy has been Deleted : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been Deleted with All Versions" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n'
					 + '\n'  + '\n' + "Deleted By : " +entityItem.getModifiedBy() + '\n' + "Deleted Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if("DeleteOne".equalsIgnoreCase(mode)){
			subject = "Policy has been Deleted : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been Deleted" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n'  +"Policy Version : " +entityItem.getActiveVersion()
					 + '\n'  + '\n' + "Deleted By : " +entityItem.getModifiedBy() + '\n' + "Deleted Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if("DeleteScope".equalsIgnoreCase(mode)){
			subject = "Scope has been Deleted : "+entityItem.getPolicyName();
			message = "The Scope Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been Deleted" + '\n'  + '\n'  + '\n'+ "Scope + Scope Name  : "  + policyName + '\n'
					 + '\n'  + '\n' + "Deleted By : " +entityItem.getModifiedBy() + '\n' + "Deleted Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if("SwitchVersion".equalsIgnoreCase(mode)){
			subject = "Policy has been SwitchedVersion : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been SwitchedVersion" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion()
					 + '\n'  + '\n' + "Switched By : " +entityItem.getModifiedBy() + '\n' + "Switched Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		if("Move".equalsIgnoreCase(mode)){
			subject = "Policy has been Moved to Other Scope : "+entityItem.getPolicyName();
			message = "The Policy Which you are watching in  " + PolicyController.getSmtpApplicationName() + " has been Moved to Other Scope" + '\n'  + '\n'  + '\n'+ "Scope + Policy Name  : "  + policyName + '\n' + "Active Version  : " +entityItem.getActiveVersion()
					 + '\n'  + '\n' + "Moved By : " +entityItem.getModifiedBy() + '\n' + "Moved Time  : " +dateFormat.format(date) + '\n' + '\n' + '\n' + '\n' + "Policy Notification System  (please don't respond to this email)";
		}
		String policyFileName = entityItem.getPolicyName();
		String checkPolicyName = policyName;
		if(checkPolicyName.endsWith(".xml") || checkPolicyName.contains(".")){
			checkPolicyName = checkPolicyName.substring(0, checkPolicyName.indexOf('.'));
		}
		if(policyFileName.contains("/")){
			policyFileName = policyFileName.substring(0, policyFileName.indexOf('/'));
			policyFileName = policyFileName.replace("/", File.separator);
		}
		if(policyFileName.contains("\\")){
			policyFileName = policyFileName.substring(0, policyFileName.indexOf('\\'));
			policyFileName = policyFileName.replace("\\", "\\\\");
		}

		policyFileName += "%";
		String query = "from WatchPolicyNotificationTable where policyName like:policyFileName";
		boolean sendFlag = false;
		SimpleBindings params = new SimpleBindings();
		params.put("policyFileName", policyFileName);
		List<Object> watchList = null;
		if(PolicyController.isjUnit()){
			watchList = policyNotificationDao.getDataByQuery(query, null);
		}else{
			watchList = policyNotificationDao.getDataByQuery(query, params);
		}
		if(watchList != null && !watchList.isEmpty()){
			for(Object watch : watchList){
				WatchPolicyNotificationTable list = (WatchPolicyNotificationTable) watch;
				String watchPolicyName = list.getPolicyName();
				if(watchPolicyName.contains("Config_") || watchPolicyName.contains("Action_") || watchPolicyName.contains("Decision_")){
					if(watchPolicyName.equals(checkPolicyName)){
						sendFlag = true;
					}else{
						sendFlag = false;
					}
				}
				if(sendFlag){
					try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
						to = list.getLoginIds()+"@"+PolicyController.getSmtpEmailExtension();
						to = to.trim();
						ctx.register(PolicyNotificationMail.class);
						ctx.refresh();
						JavaMailSenderImpl mailSender = ctx.getBean(JavaMailSenderImpl.class);
						MimeMessage mimeMessage = mailSender.createMimeMessage();
						MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage);
						mailMsg.setFrom(new InternetAddress(from, "Policy Notification System"));
						mailMsg.setTo(to);
						mailMsg.setSubject(subject);
						mailMsg.setText(message);
						mailSender.send(mimeMessage);
						if("Rename".equalsIgnoreCase(mode) || mode.contains("Delete") || mode.contains("Move")){
							policyNotificationDao.delete(watch);
						}
					} catch (Exception e) {
						policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Exception Occured in Policy Notification" +e);
					}
				}
			}
		}
	}
}
