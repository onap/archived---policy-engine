package org.onap.policy.utils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;

import com.att.nsa.mr.client.MRClient.MRApiException;
import com.att.nsa.mr.client.impl.MRConsumerImpl;
import com.att.nsa.mr.test.clients.ProtocolTypeConstants;

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
								int fetchTimeout, int fetchLimit)  throws MalformedURLException{
					
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
