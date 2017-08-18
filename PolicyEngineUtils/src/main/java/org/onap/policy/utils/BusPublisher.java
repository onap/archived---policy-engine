package org.onap.policy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.slf4j.Logger;

import com.att.nsa.mr.client.impl.MRSimplerBatchPublisher;
import com.att.nsa.mr.test.clients.ProtocolTypeConstants;

public interface BusPublisher {
	
	/**
	 * sends a message
	 * 
	 * @param partition id
	 * @param message the message
	 * @return true if success, false otherwise
	 * @throws IllegalArgumentException if no message provided
	 */
	public boolean send(String partitionId, String message) throws IllegalArgumentException;
	
	/**
	 * closes the publisher
	 */
	public void close();
	
	/**
	 * DmaapClient library wrapper
	 */
	public static class DmaapPublisherWrapper implements BusPublisher {
		/**
		 * MR based Publisher
		 */		
		protected MRSimplerBatchPublisher publisher;
		
		public DmaapPublisherWrapper(List<String> servers, String topic,
				                     String aafLogin,
				                     String aafPassword) {
			
			ArrayList<String> dmaapServers = new ArrayList<>();
			for (String server: servers) {
				dmaapServers.add(server + ":3904");
			}
					
			this.publisher = 
				new MRSimplerBatchPublisher.Builder().
			                                againstUrls(dmaapServers).
			                                onTopic(topic).
			                                build();
			
			this.publisher.setProtocolFlag(ProtocolTypeConstants.AAF_AUTH.getValue());
			
			this.publisher.setUsername(aafLogin);
			this.publisher.setPassword(aafPassword);  
			
			Properties props = new Properties();
			props.setProperty("Protocol", "http");
			props.setProperty("contenttype", "application/json");
			
			this.publisher.setProps(props);
			
			this.publisher.setHost(servers.get(0));
			
			if (PolicyLogger.isInfoEnabled())
				PolicyLogger.info(DmaapPublisherWrapper.class.getName(), 
						          "CREATION: " + this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() {
			if (PolicyLogger.isInfoEnabled())
				PolicyLogger.info(DmaapPublisherWrapper.class.getName(), 
				                  "CREATION: " + this);
			
			try {
				this.publisher.close(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				PolicyLogger.warn(DmaapPublisherWrapper.class.getName(), 
				                  "CLOSE: " + this + " because of " + 
								  e.getMessage());
				Logger logger = null;
				logger.error(e.getMessage(),e);
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean send(String partitionId, String message) 
				throws IllegalArgumentException {
			if (message == null)
				throw new IllegalArgumentException("No message provided");
			
			this.publisher.send(partitionId, message);
			return true;
			
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DmaapPublisherWrapper [").
			append("publisher.getAuthDate()=").append(publisher.getAuthDate()).
			append(", publisher.getAuthKey()=").append(publisher.getAuthKey()).
			append(", publisher.getHost()=").append(publisher.getHost()).
			append(", publisher.getProtocolFlag()=").append(publisher.getProtocolFlag()).
			append(", publisher.getUsername()=").append(publisher.getUsername()).
			append(", publisher.getPendingMessageCount()=").append(publisher.getPendingMessageCount()).
			append("]");
			return builder.toString();
		}
	}

}
