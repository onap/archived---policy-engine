/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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

package org.openecomp.policy.pdp.rest;
import org.junit.Before;
import org.junit.Test;
import org.openecomp.policy.pdp.rest.PapUrlResolver;
import org.openecomp.policy.rest.XACMLRestProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Assert;

public class PapUrlResolverTest {

	
	@Test
	public void testPropertyModifications(){
		long currentTime = new Date().getTime();
		DateFormat df = new SimpleDateFormat();
		//first sort order
		String urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		String failed = "-1,-1,-1,-1";
		String succeeded = "-1,-1,-1,-1";
		PapUrlResolver rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertEquals(rs.getProperties().getProperty(XACMLRestProperties.PROP_PAP_URLS),urls);
		
		rs.failed();
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		rs.succeeded();
		Assert.assertFalse(rs.hasMoreUrls());
		Properties prop = rs.getProperties();
		Assert.assertEquals(df.format(new Date())+",-1,-1,-1",prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS));
		Assert.assertEquals("-1,"+df.format(new Date())+",-1,-1",prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS));
		
		failed = prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS);
		succeeded = prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.succeeded();
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		prop = rs.getProperties();
		Assert.assertEquals("-1,-1,-1,-1",prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS));		
		
		failed = prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS);
		succeeded = prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.succeeded();
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		prop = rs.getProperties();
		failed = prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS);
		succeeded = prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.succeeded();
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		prop = rs.getProperties();
		failed = prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS);
		succeeded = prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.succeeded();
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		
		prop = rs.getProperties();
		succeeded = prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
		System.out.println(succeeded);
		//can't test this in a timely manner since it will need to wait a minute, since they all succeeded at the same time
		/*
		prop = rs.getProperties();
		failed = prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS);
		succeeded = prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.succeeded();
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		*/
	}
	@Test
	public void testModifyUrl(){
		String newUrl = PapUrlResolver.modifyUrl("http://mypap1.com/pap?id=987654", "http://mypap2.com:45/pap/");
		int u = 8;
	}
		
	@Test
	public void testSorts(){
		long currentTime = new Date().getTime();
		DateFormat df = new SimpleDateFormat();
		//first sort order
		String urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		String failed = df.format(new Date(currentTime-3600000))+",-1,-1,"+df.format(new Date(currentTime-3200000));
		String succeeded = "-1,8/13/15 5:41 PM,8/13/15 4:41 PM,-1";
		PapUrlResolver rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = "-1,-1,-1,-1";
		succeeded = "-1,"+df.format(new Date(currentTime-3600000))+","+df.format(new Date(currentTime-6600000))+",-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = "-1,-1,-1,-1";
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = df.format(new Date(currentTime-3900000))+","+df.format(new Date(currentTime-5600000))+","+df.format(new Date(currentTime-4600000))+","+df.format(new Date(currentTime-3600000));
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = df.format(new Date(currentTime-(3600000*4)))+",-1,-1,-1";
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = df.format(new Date(currentTime-(3600000*6)))+",-1,-1,-1";
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		
		
		
		//not used because we are no longer sorting based on success time, only failed time. Modified above.
		/*
		//first sort order
		String urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		String failed = df.format(new Date(currentTime-3600000))+",-1,-1,"+df.format(new Date(currentTime-3200000));
		String succeeded = "-1,8/13/15 5:41 PM,8/13/15 4:41 PM,-1";
		PapUrlResolver rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = "-1,-1,-1,-1";
		succeeded = "-1,"+df.format(new Date(currentTime-3600000))+","+df.format(new Date(currentTime-6600000))+",-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = "-1,-1,-1,-1";
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = df.format(new Date(currentTime-3900000))+","+df.format(new Date(currentTime-5600000))+","+df.format(new Date(currentTime-4600000))+","+df.format(new Date(currentTime-3600000));
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = df.format(new Date(currentTime-(3600000*4)))+",-1,-1,-1";
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		
		urls = "http://one.localhost.com,http://two.localhost.com,http://three.localhost.com,http://four.localhost.com";
		failed = df.format(new Date(currentTime-(3600000*6)))+",-1,-1,-1";
		succeeded = "-1,-1,-1,-1";
		rs = PapUrlResolver.getInstance(urls, failed, succeeded);
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://one.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://two.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://three.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertTrue(rs.hasMoreUrls());
		Assert.assertEquals("http://four.localhost.com", rs.getUrl());
		rs.getNext();
		Assert.assertFalse(rs.hasMoreUrls());
		*/
	}
}
