/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (c) 2019 Nokia
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.Arrays;

import java.util.Collections;
import org.junit.Test;
import org.onap.dmaap.mr.client.MRClient.MRApiException;
import org.onap.dmaap.mr.client.impl.MRConsumerImpl;
import org.onap.policy.utils.BusConsumer.DmaapConsumerWrapper;

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
        //given
        DmaapConsumerWrapper dmaapConsumerWrapper = new DmaapConsumerWrapper(new MockMrConsumer(), "", "", "");

        //when
        dmaapConsumerWrapper.fetch();
    }
    
    @Test
    public void busConsumerTest() throws MalformedURLException, MRApiException{
        BusConsumer bus = new BusConsumer.DmaapConsumerWrapper(Arrays.asList("test"), "test", "test", "test", "test", "test", 1, 1);
        assertEquals(bus.toString(),"DmaapConsumerWrapper [consumer.getAuthDate()=null, consumer.getAuthKey()=null, consumer.getHost()=test:3904, consumer.getProtocolFlag()=HTTPAAF, consumer.getUsername()=test]");
        bus.close();
    }

    private class MockMrConsumer extends MRConsumerImpl{

        MockMrConsumer() throws MalformedURLException {
            super(Collections.singletonList("test"),"", "", "", 1, 1, "", "", "");
        }

        @Override
        public Iterable<String> fetch(int timeoutMs, int limit) throws Exception {
            throw new Exception();
        }
    }

}
