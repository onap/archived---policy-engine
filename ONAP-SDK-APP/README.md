# ONAP SDK Web Application for Open Source

## Overview

This is a Maven project with the ONAP SDK web application for public release,
containing files specific to requirements of the open-source version.  This 
project uses the Maven war plugin to copy in ("overlay") the contents of the 
ONAP SDK web application overlay files distribution at package time.

Use Apache Maven to build, package and deploy this webapp to a web container
like Apache Tomcat.  Eclipse users must install the M2E-WTP connector, see 
https://www.eclipse.org/m2e-wtp/

## Release Notes

Version 1.4.0, <?day> <?month> 2017
- PORTAL-19 Rename Java package base to org.onap
- PORTAL-42 Use OParent as parent POM
- PORTAL-72 Address Sonar Scan code issues
- PORTAL-79 remove unwanted SDK left menu under Report-sample dashboard
- PORTAL-90 Use approved ONAP license text
- Portal-86 Remove application specific usages from tests and other files (rework)
- Portal-104 Replaced the sql connector to maria db
- Portal-127 Remove GreenSock license code from b2b library
* Put new entries here *

Version 1.3.0, 28 August 2017
- PORTAL-17 removing eye.js and utils.js
- PORTAL-19 Renaming the Group Id in the POM file to org.onap.portal.sdk
- PORTAL-34 Restore required properties in fusion.properties file
- PORTAL-64 Single sign-on from Portal fails for some applications
- PORTAL-21 Added scripts to remove foreign keys of function code in fn_menu
  and fn_restriced_url tables.

Version 1.1.0
- PORTAL-6 Updates to License and Trademark in the PORTAL Source Code
- PORTAL-7 Improvements added as part of the rebasing process
- PORTAL 13 ecompsdk db connection intermittent issue seen for VID app 
- PORTAL 15 Fix Charting and Search Capabilities of EcompSDK Analytics
- PORTAL 23 Updating the SDK version from Snapshot to Release 1.1.0
 
Version 1.0.0
- Initial release
