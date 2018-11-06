/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.utils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;
import org.onap.dmaap.mr.client.MRClient.MRApiException;
import org.onap.dmaap.mr.client.impl.MRConsumerImpl;
import org.onap.dmaap.mr.test.clients.ProtocolTypeConstants;

public interface BusConsumer {
	
	/**
	 * fetch messages
	 * 
	 * @return list of messages
	 * @throws MRApiException when error encountered by underlying libraries
	 */
	public Iterable<String> fetch() throws MRApiException;
	
	/**
	 * close underlying library consumer
	 */
	public void close();
	
	/**
	 * MR based consumer
	 */
	public static class DmaapConsumerWrapper implements BusConsumer {
		
		/**
		 * MR Consumer
		 */
		protected MRConsumerImpl consumer;
		
		/**
		 * MR Consumer Wrapper
		 * 
		 * @param servers messaging bus hosts
		 * @param topic topic
		 * @param apiKey API Key
		 * @param apiSecret API Secret
		 * @param aafLogin AAF Login
		 * @param aafPassword AAF Password
		 * @param consumerGroup Consumer Group
		 * @param consumerInstance Consumer Instance
		 * @param fetchTimeout Fetch Timeout
		 * @param fetchLimit Fetch Limit
		 */
		public DmaapConsumerWrapper(List<String> servers, String topic, 
								String aafLogin, String aafPassword,
								String consumerGroup, String consumerInstance,
								int fetchTimeout, int fetchLimit) throws MalformedURLException{
					
			this.consumer = new MRConsumerImpl(servers, topic, 
											   consumerGroup, consumerInstance, 
											   fetchTimeout, fetchLimit, 
									           null, aafLogin, aafPassword);
			
			this.consumer.setUsername(aafLogin);
			this.consumer.setPassword(aafPassword);
			
			this.consumer.setProtocolFlag(ProtocolTypeConstants.AAF_AUTH.getValue());
			
			Properties props = new Properties();
			props.setProperty("Protocol", "http");
			this.consumer.setProps(props);
			this.consumer.setHost(servers.get(0) + ":3904");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterable<String> fetch() throws MRApiException {
			try {
                return this.consumer.fetch();
            } catch (Exception e) {
                throw new MRApiException("Error during MR consumer Fetch ",e);
            }
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() {
			this.consumer.close();
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.
			append("DmaapConsumerWrapper [").
			append("consumer.getAuthDate()=").append(consumer.getAuthDate()).
			append(", consumer.getAuthKey()=").append(consumer.getAuthKey()).
			append(", consumer.getHost()=").append(consumer.getHost()).
			append(", consumer.getProtocolFlag()=").append(consumer.getProtocolFlag()).
			append(", consumer.getUsername()=").append(consumer.getUsername()).
			append("]");
			return builder.toString();
		}
	}

}
