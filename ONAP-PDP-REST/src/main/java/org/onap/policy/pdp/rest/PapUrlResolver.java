/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;

import com.att.research.xacml.util.XACMLProperties;

public class PapUrlResolver {
    private static final Logger LOGGER = FlexLogger.getLogger(PapUrlResolver.class);
    // how long to keep a pap failed before making it un-failed, in milli-seconds
    private static final long FAIL_TIMEOUT = 18000000;

    // thread locks
    public static final Object propertyLock = new Object();

    // keeping this here for backward compatibility
    public static String extractIdFromUrl(String url) {
        return extractQuery(url);
    }

    public static String extractQuery(String url) {
        try {
            return URI.create(url).getQuery();
        } catch (Exception e) {
            LOGGER.error("Exception occured while extracting query. So, empty string is returned" + e);
            return "";
        }
    }

    public static String modifyUrl(String idUrl, String serverUrl) {
        URI one = URI.create(idUrl);
        String host = one.getPath() + one.getQuery();
        URI two = URI.create(serverUrl);
        two.resolve(host);
        return two.toString();
    }

    // get an instance of a new PapUrlResolver, using XACMLProperties to get the url lists
    public static PapUrlResolver getInstance() {
        return new PapUrlResolver(null, null, null, true);
    }

    // get an instance of a new PapUrlResolver, using the provides strings for the url lists
    public static PapUrlResolver getInstance(String urlList, String failedList, String succeededList) {
        return new PapUrlResolver(urlList, failedList, succeededList, false);
    }

    // keeps track of our current location in the list of urls, allows for iterating
    private int pointer;

    // should the XACML property lists be updated after anything changes or should we wait for the update
    // method to be called.
    private boolean autoUpdateProperties;

    // this list keeps the sorted, priority of PAP URLs
    private PapUrlNode[] sortedUrlNodes;
    // this list keeps the original list of nodes so that they can be entered into the property list correctly
    private PapUrlNode[] originalUrlNodes;

    // private constructor to make an instance of a PapUrlResolver, called by static method getInstance.
    // If the list property strings are not defined, we get the values from XACMLProperties.
    // The instance acts as an iterator, with hasNext and next methods, but does not implement Iterable,
    // because it is used for a difference purpose.
    private PapUrlResolver(String urlList, String failedList, String succeededList, boolean autoUpdateProperties) {
        this.autoUpdateProperties = autoUpdateProperties;
        String papUrlLists = urlList;
        String papUrlFailedList = failedList;
        String papUrlSuccessList = succeededList;
        if (papUrlLists == null) {
            papUrlLists = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URLS);
            if (papUrlLists == null) {
                papUrlLists = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
            }
            papUrlFailedList = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS);
            papUrlSuccessList = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS);
        }

        String[] urls = papUrlLists.split(",");
        if (urls.length == 0) {
            // log error
        }
        String[] failed = emptyOrSplit(papUrlFailedList, urls.length);
        String[] succeeded = emptyOrSplit(papUrlSuccessList, urls.length);

        sortedUrlNodes = new PapUrlNode[urls.length];
        for (int i = 0; i < urls.length; i++) {

            String userId = null;
            String pass = null;
            userId = XACMLProperties.getProperty(urls[i] + "." + XACMLRestProperties.PROP_PAP_USERID);
            pass = XACMLProperties.getProperty(urls[i] + "." + XACMLRestProperties.PROP_PAP_PASS);
            if (userId == null || pass == null) {
                userId = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
                pass = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS);
            }
            if (userId == null || pass == null) {
                userId = "";
                pass = "";
            }
            PapUrlNode newNode = new PapUrlNode(urls[i], userId, pass);
            newNode.setFailedTime(failed[i]);
            newNode.setSucceededTime(succeeded[i]);
            if (sortedUrlNodes[i] == null) {
                sortedUrlNodes[i] = newNode;
            }
        }
        originalUrlNodes = sortedUrlNodes.clone();
        sort(sortedUrlNodes);
        pointer = 0;
    }

    // either split a list by commas, or fill an array to the expected length, if the property list is not long enough
    private String[] emptyOrSplit(String list, int expectedLength) {
        String[] ret;
        if (list == null) {
            ret = new String[expectedLength];
            for (int i = 0; i < expectedLength; i++) {
                ret[i] = "-1";
            }
        } else {
            ret = list.split(",");
            if (ret.length != expectedLength) {
                ret = emptyOrSplit(null, expectedLength);
            }
        }
        return ret;
    }

    private void sort(PapUrlNode[] array) {

        // O(n^2) double-loop most likely the best in this case, since number of records will be VERY small
        for (int i = 0; i < array.length; i++) {
            for (int j = i; j < array.length; j++) {
                if (array[j].compareTo(array[i]) < 0) {
                    PapUrlNode temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
    }

    // returns whether this PapUrlResolver object has more PAP urls that can be tried
    public boolean hasMoreUrls() {
        return pointer < sortedUrlNodes.length;
    }

    // sets the current PAP url as being failed
    // this will set the failed time to now and remove any succeeded time
    public void failed() {
        LOGGER.error("PAP Server FAILED: " + sortedUrlNodes[pointer].getUrl());

        sortedUrlNodes[pointer].setFailedTime(new Date());
        sortedUrlNodes[pointer].setSucceededTime(null);
        propertiesUpdated();
    }

    // sets the current PAP url as being working
    // this will set the succeeded time to now and remove any failed time
    // Also, this will cause hasMoreUrls to return false, since a working one has been found

    public void succeeded() {
        registered();
        pointer = sortedUrlNodes.length;
    }

    public void registered() {
        sortedUrlNodes[pointer].setFailedTime(null);
        sortedUrlNodes[pointer].setSucceededTime(new Date());
        LOGGER.info("PAP server SUCCEEDED " + sortedUrlNodes[pointer].getUrl());
        propertiesUpdated();
    }

    // returns a properties object with the properties that pertain to PAP urls
    public Properties getProperties() {
        String failedPropertyString = "";
        String succeededPropertyString = "";
        String urlPropertyString = "";
        for (int i = 0; i < originalUrlNodes.length; i++) {
            failedPropertyString = failedPropertyString.concat(",").concat(originalUrlNodes[i].getFailedTime());
            succeededPropertyString = succeededPropertyString.concat(",")
                    .concat(originalUrlNodes[i].getSucceededTime());
            urlPropertyString = urlPropertyString.concat(",").concat(originalUrlNodes[i].getUrl());
        }
        Properties prop = new Properties();
        failedPropertyString = failedPropertyString.substring(1);
        succeededPropertyString = succeededPropertyString.substring(1);
        urlPropertyString = urlPropertyString.substring(1);
        prop.setProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS, failedPropertyString);
        prop.setProperty(XACMLRestProperties.PROP_PAP_URLS, urlPropertyString);
        prop.setProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS, succeededPropertyString);
        return prop;
    }

    // saves the updates urls to the correct properties
    private void propertiesUpdated() {
        if (!autoUpdateProperties) {
            return;
        }
        Properties prop = getProperties();

        LOGGER.debug("Failed PAP Url List: " + prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS));
        LOGGER.debug("Succeeded PAP Url List: " + prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS));
        XACMLProperties.setProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS,
                prop.getProperty(XACMLRestProperties.PROP_PAP_FAILED_URLS));
        XACMLProperties.setProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS,
                prop.getProperty(XACMLRestProperties.PROP_PAP_SUCCEEDED_URLS));
    }

    // iterates to the next available PAP url, according to the priority order
    public void getNext() {
        pointer++;
    }

    // returns the url of the current PAP server that we are iterating over
    // will append the provided policy id to the url
    public String getUrl(String query) {
        if (sortedUrlNodes[pointer] == null) {
            throw new NoSuchElementException();
        } else {
            return sortedUrlNodes[pointer].getUrl().concat("?").concat(query);
        }
    }

    // returns the url of the current PAP server that we are iterating over
    // Just returns the url, with no id appended to it
    public String getUrl() {
        if (sortedUrlNodes[pointer] == null) {
            throw new NoSuchElementException();
        } else {

            return sortedUrlNodes[pointer].getUrl();
        }
    }

    public String getUserId() {
        if (sortedUrlNodes[pointer] == null) {
            throw new NoSuchElementException();
        } else {

            return sortedUrlNodes[pointer].getUserId();
        }
    }

    public String getPass() {
        if (sortedUrlNodes[pointer] == null) {
            throw new NoSuchElementException();
        } else {

            return sortedUrlNodes[pointer].getPass();
        }
    }

    // This is the class to hold the details of a single PAP URL
    // including: the url itself, the last time it failed, and the last time it succeeded
    // It also includes the custom comparer which can compare based on failed and succeeded times, and takes into
    // account
    // the timeout on failures.
    private class PapUrlNode implements Comparable<PapUrlNode> {
        private String papUrl;
        private Date failedTime;
        private Date succeededTime;
        private String userId;
        private String pass;

        public PapUrlNode(String url, String userId, String pass) {
            this.papUrl = url;
            failedTime = null;
            this.succeededTime = null;
            this.userId = userId;
            this.pass = pass;

        }

        public String getUserId() {
            return this.userId;
        }

        public String getPass() {
            return this.pass;
        }

        public void setFailedTime(Object time) {
            Date failedTimeAsDate = setHandler(time);
            if (failedTimeAsDate == null) {
                this.failedTime = null;
            } else {
                long timeDifference = new Date().getTime() - failedTimeAsDate.getTime();
                if (timeDifference < FAIL_TIMEOUT) {
                    this.failedTime = failedTimeAsDate;
                } else {
                    this.failedTime = null;
                }
            }
        }

        // set the time that this url succeeded at
        public void setSucceededTime(Object time) {
            this.succeededTime = setHandler(time);
        }

        // parses string into a date or a null date, if the url never failed/succeeded (since -1 will be in the
        // property)
        private Date setHandler(Object time) {
            if (time instanceof String) {
                if ("-1".equals((String) time)) {
                    return null;
                }
                try {
                    DateFormat df = new SimpleDateFormat();
                    return df.parse((String) time);
                } catch (ParseException e) {
                    return null;
                }
            }
            if (time instanceof Date) {
                return (Date) time;
            }
            return null;
        }

        public String getFailedTime() {
            return formatTime(this.failedTime);
        }

        public String getSucceededTime() {
            return formatTime(this.succeededTime);
        }

        // formats a Date into a string or a -1 if there is not date (-1 is used in properties for no date)
        private String formatTime(Date d) {
            if (d == null) {
                return "-1";
            }
            DateFormat df = new SimpleDateFormat();
            return df.format(d);
        }

        public String getUrl() {
            return papUrl;
        }

        @Override
        public int compareTo(PapUrlNode other) {
            if (this.failedTime == null && other.failedTime != null) {
                return -1;
            }
            if (this.failedTime != null && other.failedTime == null) {
                return 1;
            }
            if (this.failedTime != null) {
                return this.failedTime.compareTo(other.failedTime);
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof PapUrlNode)) {
                return false;
            }
            PapUrlNode papUrlNode = (PapUrlNode) obj;
            return Objects.equals(papUrlNode.papUrl, papUrl) && Objects.equals(papUrlNode.failedTime, failedTime)
                    && Objects.equals(papUrlNode.succeededTime, succeededTime)
                    && Objects.equals(papUrlNode.userId, userId) && Objects.equals(papUrlNode.pass, pass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(papUrl, failedTime, succeededTime, userId, pass);
        }
    }
}
