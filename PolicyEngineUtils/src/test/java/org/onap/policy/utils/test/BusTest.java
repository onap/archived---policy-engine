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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.Arrays;

import org.junit.Test;
import org.onap.dmaap.mr.client.MRClient.MRApiException;
import org.onap.policy.utils.BusConsumer;
import org.onap.policy.utils.BusPublisher;

public class BusTest {
    
    @Test 
    public void busPublisherTest(){
        BusPublisher bus = new BusPublisher.DmaapPublisherWrapper(Arrays.asList("test"), "test", "test", "test");
        assertTrue(bus.send("test123", "Hello World!"));
        assertEquals("DmaapPublisherWrapper [publisher.getAuthDate()=null, publisher.getAuthKey()=null, publisher.getHost()=test, publisher.getProtocolFlag()=HTTPAAF, publisher.getUsername()=test, publisher.getPendingMessageCount()=1]",bus.toString());
        bus.close();
    }
    
    @Test (expected = MRApiException.class)
    public void busConsumerFailTest() throws MalformedURLException, MRApiException{
        new BusConsumer.DmaapConsumerWrapper(Arrays.asList("test"), "test", "test", "test", "test", "test", 1, 1).fetch();
    }
    
    @Test
    public void busConsumerTest() throws MalformedURLException, MRApiException{
        BusConsumer bus = new BusConsumer.DmaapConsumerWrapper(Arrays.asList("test"), "test", "test", "test", "test", "test", 1, 1);
        assertEquals(bus.toString(),"DmaapConsumerWrapper [consumer.getAuthDate()=null, consumer.getAuthKey()=null, consumer.getHost()=test:3904, consumer.getProtocolFlag()=HTTPAAF, consumer.getUsername()=test]");
        bus.close();
    }

}
