.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Policy Release Notes
====================

.. note
..      * This Release Notes must be updated each time the team decides to Release new artifacts.
..      * The scope of these Release Notes are for ONAP POLICY. In other words, each ONAP component has its Release Notes.
..      * This Release Notes is cumulative, the most recently Released artifact is made visible in the top of
..      * this Release Notes.
..      * Except the date and the version number, all the other sections are optional but there must be at least
..      * one section describing the purpose of this new release.
..      * This note must be removed after content has been added.


..      ==========================
..      * * *      DUBLIN    * * *
..      ==========================

Version: 4.0.0
--------------

:Release Date: 2019-06-06 (Dublin Release)

**New Features**

The Dublin release for POLICY delivered the following Epics. For a full list of stories and tasks delivered in the Dublin release, refer to `JiraPolicyDublin`_.

    * [POLICY-1068] - This epic covers the work to cleanup, enhance, fix, etc. any Control Loop based code base.
        - POLICY-1459	PDP-D [Control Loop] : Create a Control Loop flavored PDP-D image
        - POLICY-1397	PDP-D: NOOP Endpoints Support to test Operational Policies.
        - POLICY-1195	Separate model code from drools-applications into other repositories
        - POLICY-1367	Spike - Experimentation for management of Drools templates and Operational Policies

    * [POLICY-1069] - This epic covers the work to harden the codebase for the Policy Framework project.
        - POLICY-1250	Fix issues reported by sonar in policy modules
        - POLICY-1202	policy-engine & apex-pdp are using different version of eclipselink
        - POLICY-1368	Remove hibernate from policy repos
        - POLICY-1007	Remove Jackson from policy framework components
        - POLICY-1457	Use Alpine in base docker images

    * [POLICY-1072] - This epic covers the work to support S3P Performance criteria.

    * [POLICY-1171] - Enhance CLC Facility
        - POLICY-1173	High-level specification of coordination directives

    * [POLICY-1220] - This epic covers the work to support S3P Security criteria
        - Task	POLICY-1538	Upgrade Elasticsearch to 6.4.x to clear security issue

    * [POLICY-1269] - R4 Dublin - ReBuild Policy Infrastructure
        - POLICY-1453	Apex PDP Dmaap Deploy/UnDeploy Functionality
        - POLICY-1452	Apex PDP Dmaap Register/UnRegister Functionality
        - POLICY-1460	Create S3P JMeter Tests for PAP
        - POLICY-1458	Create S3P JMeter Tests for Policy API
        - POLICY-1462	Create S3P JMeter Tests for Policy SDC Distribution
        - POLICY-1461	Create S3P JMeter Tests for Policy XACML Engine (2nd Generation)
        - POLICY-1272	Create the S3P JMeter tests for API, PAP, XACML (2nd Gen)
        - POLICY-1455	Drools PDP Dmaap Deploy/UnDeploy Functionality
        - POLICY-1454	Drools PDP Dmaap Register/UnRegister Functionality
        - POLICY-1474	Modifications of Control Loop Operational Policy to support new Policy Lifecycle API
        - POLICY-1443	PAP Dmaap PDP Register/UnRegister Main Entry Point
        - POLICY-1444	PAP Dmaap Policy Deploy/Undeploy Policies Main Entry Point
        - POLICY-1542	PAP REST API for PDPGroup Deployment, State Management & Health Check
        - POLICY-1541	PAP REST API for PDPGroup Query, Statistics & Delete
        - POLICY-1271	PAP RESTful HealthCheck/Statistics Main Entry Point
        - POLICY-1471	Policy Application Designer - Develop Guard and Control Loop Coordination Policy Type application
        - POLICY-1456	Policy Architecture and Roadmap Documentation
        - POLICY-1442	Policy Lifecycle API RESTful Create/Read Main Entry Point for Concrete Policies
        - POLICY-1441	Policy Lifecycle API RESTful Create/Read Main Entry Point for Policy Types
        - POLICY-1447	Policy Lifecycle API RESTful Delete Main Entry Point for Concrete Policies
        - POLICY-1446	Policy Lifecycle API RESTful Delete Main Entry Point for Policy Types
        - POLICY-1270	Policy Lifecycle API RESTful HealthCheck/Statistics Main Entry Point
        - POLICY-1273	Policy Type Application Design Requirements
        - POLICY-1515	Prototype Policy Lifecycle API Swagger Entry Points
        - POLICY-1516	Prototype the Policy Decision API
        - POLICY-1451	XACML PDP Dmaap Deploy/UnDeploy Functionality
        - POLICY-1449	XACML PDP Dmaap Register/UnRegister Functionality
        - POLICY-1440	XACML PDP RESTful Decision API Main Entry Point
        - POLICY-1436	XACML PDP RESTful HealthCheck/Statistics Main Entry Point
        - POLICY-1445	XACML PDP upgrade to xacml 2.0.0

    * [POLICY-1399] - This epic covers the work to support model drive control loop design as defined by the Control Loop Subcommittee

    * [POLICY-1404] - This epic covers the work to support the CCVPN Use Case for Dublin
        - POLICY-1405	Develop SDNC API for trigger bandwidth

    * [POLICY-1408] - This epic covers the work done with the Casablanca release
        - POLICY-1419	Better multi-role support
        - POLICY-1427	Controller Logging Feature
        - POLICY-1413	Dashboard enhancements
        - POLICY-1422	Enhanced encryption
        - POLICY-1420	Model enhancement to support embedded JSON
        - POLICY-1416	Model enhancements to support CLAMP
        - POLICY-1421	New audit data for push/delete
        - POLICY-1418	PDP APIs - make ClientAuth optional
        - POLICY-1414	Push Policy and DeletePolicy API enhancement
        - POLICY-1417	Resiliency improvements
        - POLICY-1423	Save original model file
        - POLICY-1410	List Policy API
        - POLICY-1499	Mdc Filter Feature
        - POLICY-1489	PDP-D: Nested JSON Event Filtering support with JsonPath

    * [POLICY-1438] - This epic covers the work to support 5G OOF PCI Use Case
        - POLICY-1464	Config related aspects for OOF SON use case
        - POLICY-1463	Functional code changes in Policy for OOF SON use case

    * [POLICY-1450] - This epic covers the work to support the Scale Out Use Case.
        - POLICY-1278	AAI named-queries are being deprecated and should be replaced with custom-queries
        - POLICY-1545	E2E Automation - Parse the newly added model ids from operation policy

    * Additional items delivered with the release.
        - POLICY-1266	A&AI Modularity
        - POLICY-1274	further improvement in PSSD S3P test
        - POLICY-1159	Move expectException to policy-common/utils-test
        - POLICY-1465	Support configurable Heap Memory Settings for JVM processes
        - POLICY-1176	Work on technical debt introduced by CLC POC


**Bug Fixes**

The following bug fixes have been deployed with this release:

    * `[POLICY-1627] <https://jira.onap.org/browse/POLICY-1627>`_ - APEX does not support specification of a partitioner class for Kafka
    * `[POLICY-1289] <https://jira.onap.org/browse/POLICY-1289>`_ - Apex only considers 200 response codes as successful result codes
    * `[POLICY-1437] <https://jira.onap.org/browse/POLICY-1437>`_ - Fix issues in FileSystemReceptionHandler of policy-distribution component
    * `[POLICY-1501] <https://jira.onap.org/browse/POLICY-1501>`_ - policy-engine JUnit tests are not independent
    * `[POLICY-1241] <https://jira.onap.org/browse/POLICY-1241>`_ - Test failure in drools-pdp if JAVA_HOME is not set

**Security Notes**

*Fixed Security Issues*

    * `[OJSI-117] <https://jira.onap.org/browse/OJSI-117>`_ - In default deployment POLICY (nexus) exposes HTTP port 30236 outside of cluster.
    * `[OJSI-157] <https://jira.onap.org/browse/OJSI-157>`_ - In default deployment POLICY (policy-api) exposes HTTP port 30240 outside of cluster.
    * `[OJSI-118] <https://jira.onap.org/browse/OJSI-118>`_ - In default deployment POLICY (policy-apex-pdp) exposes HTTP port 30237 outside of cluster.
    * `[OJSI-184] <https://jira.onap.org/browse/OJSI-184>`_ - In default deployment POLICY (brmsgw) exposes HTTP port 30216 outside of cluster.

*Known Security Issues*

*Known Vulnerabilities in Used Modules*

POLICY code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The POLICY open Critical security vulnerabilities and their risk assessment have been documented as part of the `project (Dublin Release) <https://wiki.onap.org/pages/viewpage.action?pageId=54723253>`_.

Quick Links:
    - `POLICY project page`_
    - `Passing Badge information for POLICY`_
    - `Project Vulnerability Review Table for POLICY (Dublin Release) <https://wiki.onap.org/pages/viewpage.action?pageId=54723253>`_


**Known Issues**

The following known issues will be addressed in a future release:

    * `[POLICY-1291] - <https://jira.onap.org/browse/POLICY-1291>`_ Maven Error when building Apex documentation in Windows
    * `[POLICY-1650] - <https://jira.onap.org/browse/POLICY-1650>`_ Policy UI doesn't show left menu or any content
    * `[POLICY-1725] - <https://jira.onap.org/browse/POLICY-1725>`_ XACML PDP returns 500 vs 400 for bad syntax JSON
    * `[POLICY-1795] - <https://jira.onap.org/browse/POLICY-1795>`_ PAP: bounced apex and xacml pdps show deleted instance in pdp status through APIs.



..      ==========================
..      * * *   CASABLANCA   * * *
..      ==========================

Version: 3.0.2
--------------

:Release Date: 2019-03-31 (Casablanca Maintenance Release #2)

The following items were deployed with the Casablanca Maintenance Release:

**Bug Fixes**

    * [POLICY-1522] - Policy doesn't send "payload" field to APPC

**Security Fixes**

    * [POLICY-1538] - Upgrade Elasticsearch to 6.4.x to clear security issue

**License Issues**

    * [POLICY-1433] - Remove proprietary licenses in PSSD test CSAR

**Known Issues**

The following known issue will be addressed in a future release.

    * `[POLICY-1650] <https://jira.onap.org/browse/POLICY-1277>`_ - Policy UI doesn't show left menu or any content

A workaround for this issue consists in bypassing the Portal UI when accessing the Policy UI.   See `PAP recipes <https://docs.onap.org/en/casablanca/submodules/policy/engine.git/docs/platform/cookbook.html?highlight=policy%20cookbook#id23>`_ for the specific procedure.


Version: 3.0.1
--------------

:Release Date: 2019-01-31 (Casablanca Maintenance Release)

The following items were deployed with the Casablanca Maintenance Release:

**New Features**

    * [POLICY-1221] - Policy distribution application to support HTTPS communication
    * [POLICY-1222] - Apex policy PDP to support HTTPS Communication

**Bug Fixes**

    * `[POLICY-1282] <https://jira.onap.org/browse/POLICY-1282>`_ - Policy format with some problems
    * `[POLICY-1395] <https://jira.onap.org/browse/POLICY-1395>`_ - Apex PDP does not preserve context on model upgrade


Version: 3.0.0
--------------

:Release Date: 2018-11-30 (Casablanca Release)

**New Features**

The Casablanca release for POLICY delivered the following Epics. For a full list of stories and tasks delivered in the Casablanca release, refer to `JiraPolicyCasablanca`_ (Note: Jira details can also be viewed from this link).

    * [POLICY-701] - This epic covers the work to integrate Policy into the SDC Service Distribution

    The policy team introduced a new application into the framework that provides integration of the Service Distribution Notifications from SDC to Policy.

    * [POLICY-719] - This epic covers the work to build the Policy Lifecycle API
    * [POLICY-726] - This epic covers the work to distribute policy from the PAP to the PDPs into the ONAP platform
    * [POLICY-876] - This epics covers the work to re-build how the PAP organizes the PDP's into groups.

    The policy team did some forward looking spike work towards re-building the Software Architecture.

    * [POLICY-809] - Maintain and implement performance
    * [POLICY-814] - 72 hour stability testing (component and platform)

    The policy team made enhancements to the Drools PDP to further support S3P Performance.
    For the new Policy SDC Distribution application and the newly ingested Apex PDP the team established S3P
    performance standard and performed 72 hour stability tests.

    * [POLICY-824] - maintain and implement security

    The policy team established AAF Root Certificate for HTTPS communication and CADI/AAF integration into the
    MVP applications. In addition, many java dependencies were upgraded to clear CLM security issues.

    * [POLICY-840] - Flexible control loop coordination facility.

    Work towards a POC for control loop coordination policies were implemented.

    * [POLICY-841] - Covers the work required to support HPA

    Enhancements were made to support the HPA use case through the use of the new Policy SDC Service Distribution application.

    * [POLICY-842] - This epic covers the work to support the Auto Scale Out functional requirements

    Enhancements were made to support Scale Out Use Case to enforce new guard policies and updated SO and A&AI APIs.

    * [POLICY-851] - This epic covers the work to bring in the Apex PDP code

    A new Apex PDP engine was ingested into the platform and work was done to ensure code cleared CLM security issues,
    sonar issues, and checkstyle.

    * [POLICY-1081] - This epic covers the contribution for the 5G OOF PCI Optimization use case.

    Policy templates changes were submitted that supported the 5G OOF PCI optimization use case.

    * [POLICY-1182] - Covers the work to support CCVPN use case

    Policy templates changes were submitted that supported the CCVPN use case.

**Bug Fixes**

The following bug fixes have been deployed with this release:

    * `[POLICY-799] <https://jira.onap.org/browse/POLICY-799>`_ - Policy API Validation Does Not Validate Required Parent Attributes in the Model
    * `[POLICY-869] <https://jira.onap.org/browse/POLICY-869>`_ - Control Loop Drools Rules should not have exceptions as well as die upon an exception
    * `[POLICY-872] <https://jira.onap.org/browse/POLICY-872>`_ - investigate potential race conditions during rules version upgrades during call loads
    * `[POLICY-878] <https://jira.onap.org/browse/POLICY-878>`_ - pdp-d: feature-pooling disables policy-controllers preventing processing of onset events
    * `[POLICY-909] <https://jira.onap.org/browse/POLICY-909>`_ - get_ZoneDictionaryDataByName class type error
    * `[POLICY-920] <https://jira.onap.org/browse/POLICY-920>`_ - Hard-coded path in junit test
    * `[POLICY-921] <https://jira.onap.org/browse/POLICY-921>`_ - XACML Junit test cannot find property file
    * `[POLICY-1083] <https://jira.onap.org/browse/POLICY-1083>`_ - Mismatch in action cases between Policy and APPC


**Security Notes**

POLICY code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The POLICY open Critical security vulnerabilities and their risk assessment have been documented as part of the `project (Casablanca Release) <https://wiki.onap.org/pages/viewpage.action?pageId=45300864>`_.

Quick Links:
    - `POLICY project page`_
    - `Passing Badge information for POLICY`_
    - `Project Vulnerability Review Table for POLICY (Casablanca Release) <https://wiki.onap.org/pages/viewpage.action?pageId=45300864>`_

**Known Issues**

    * `[POLICY-1277] <https://jira.onap.org/browse/POLICY-1277>`_ - policy config takes too long time to become retrievable in PDP
    * `[POLICY-1282] <https://jira.onap.org/browse/POLICY-1282>`_ - Policy format with some problems



..      =======================
..      * * *   BEIJING   * * *
..      =======================

Version: 2.0.0
--------------

:Release Date: 2018-06-07 (Beijing Release)

**New Features**

The Beijing release for POLICY delivered the following Epics. For a full list of stories and tasks delivered in the Beijing release, refer to `JiraPolicyBeijing`_.

    * [POLICY-390] - This epic covers the work to harden the Policy platform software base (incl 50% JUnit coverage)
        - POLICY-238	policy/drools-applications: clean up maven structure
        - POLICY-336	Address Technical Debt
        - POLICY-338	Address JUnit Code Coverage
        - POLICY-377	Policy Create API should validate input matches DCAE microservice template
        - POLICY-389	Cleanup Jenkin's CI/CD process's
        - POLICY-449	Policy API + Console : Common Policy Validation
        - POLICY-568	Integration with org.onap AAF project
        - POLICY-610	Support vDNS scale out for multiple times in Beijing release

    * [POLICY-391] - This epic covers the work to support Release Planning activities
        - POLICY-552	ONAP Licensing Scan - Use Restrictions

    * [POLICY-392] - Platform Maturity Requirements - Performance Level 1
        - POLICY-529	Platform Maturity Performance - Drools PDP
        - POLICY-567	Platform Maturity Performance - PDP-X

    * [POLICY-394] - This epic covers the work required to support a Policy developer environment in which Policy Developers can create, update policy templates/rules separate from the policy Platform runtime platform.
        - POLICY-488	pap should not add rules to official template provided in drools applications

    * [POLICY-398] - This epic covers the body of work involved in supporting policy that is platform specific.
        - POLICY-434	need PDP /getConfig to return an indicator of where to find the config data - in config.content versus config field

    * [POLICY-399] - This epic covers the work required to policy enable Hardware Platform Enablement
        - POLICY-622	Integrate OOF Policy Model into Policy Platform

    * [POLICY-512] - This epic covers the work to support Platform Maturity Requirements - Stability Level 1
        - POLICY-525	Platform Maturity Stability - Drools PDP
        - POLICY-526	Platform Maturity Stability - XACML PDP

    * [POLICY-513] - Platform Maturity Requirements - Resiliency Level 2
        - POLICY-527	Platform Maturity Resiliency - Policy Engine GUI and PAP
        - POLICY-528	Platform Maturity Resiliency - Drools PDP
        - POLICY-569	Platform Maturity Resiliency - BRMS Gateway
        - POLICY-585	Platform Maturity Resiliency - XACML PDP
        - POLICY-586	Platform Maturity Resiliency - Planning
        - POLICY-681	Regression Test Use Cases

    * [POLICY-514] - This epic covers the work to support Platform Maturity Requirements - Security Level 1
        - POLICY-523	Platform Maturity Security - CII Badging - Project Website

    * [POLICY-515] - This epic covers the work to support Platform Maturity Requirements - Escalability Level 1
        - POLICY-531	Platform Maturity Scalability - XACML PDP
        - POLICY-532	Platform Maturity Scalability - Drools PDP
        - POLICY-623	Docker image re-design

    * [POLICY-516] - This epic covers the work to support Platform Maturity Requirements - Manageability Level 1
        - POLICY-533	Platform Maturity Manageability L1 - Logging
        - POLICY-534	Platform Maturity Manageability - Instantiation < 1 hour

    * [POLICY-517] - This epic covers the work to support Platform Maturity Requirements - Usability Level 1
        - POLICY-535	Platform Maturity Usability - User Guide
        - POLICY-536	Platform Maturity Usability - Deployment Documentation
        - POLICY-537	Platform Maturity Usability - API Documentation

    * [POLICY-546] - R2 Beijing - Various enhancements requested by clients to the way we handle TOSCA models.


**Bug Fixes**

The following bug fixes have been deployed with this release:

    * `[POLICY-484] <https://jira.onap.org/browse/POLICY-484>`_ - Extend election handler run window and clean up error messages
    * `[POLICY-494] <https://jira.onap.org/browse/POLICY-494>`_ - POLICY EELF Audit.log not in ECOMP Standards Compliance
    * `[POLICY-501] <https://jira.onap.org/browse/POLICY-501>`_ - Fix issues blocking election handler and add directed interface for opstate
    * `[POLICY-509] <https://jira.onap.org/browse/POLICY-509>`_ - Add IntelliJ file to .gitingore
    * `[POLICY-510] <https://jira.onap.org/browse/POLICY-510>`_ - Do not enforce hostname validation
    * `[POLICY-518] <https://jira.onap.org/browse/POLICY-518>`_ - StateManagement creation of EntityManagers.
    * `[POLICY-519] <https://jira.onap.org/browse/POLICY-519>`_ - Correctly initialize the value of allSeemsWell in DroolsPdpsElectionHandler
    * `[POLICY-629] <https://jira.onap.org/browse/POLICY-629>`_ - Fixed a bug on editor screen
    * `[POLICY-684] <https://jira.onap.org/browse/POLICY-684>`_ - Fix regex for brmsgw dependency handling
    * `[POLICY-707] <https://jira.onap.org/browse/POLICY-707>`_ - ONAO-PAP-REST unit tests fail on first build on clean checkout
    * `[POLICY-717] <https://jira.onap.org/browse/POLICY-717>`_ - Fix a bug in checking required fields if the object has include function
    * `[POLICY-734] <https://jira.onap.org/browse/POLICY-734>`_ - Fix Fortify Header Manipulation Issue
    * `[POLICY-743] <https://jira.onap.org/browse/POLICY-743>`_ - Fixed data name since its name was changed on server side
    * `[POLICY-753] <https://jira.onap.org/browse/POLICY-753>`_ - Policy Health Check failed with multi-node cluster
    * `[POLICY-765] <https://jira.onap.org/browse/POLICY-765>`_ - junit test for guard fails intermittently


**Security Notes**

POLICY code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The POLICY open Critical security vulnerabilities and their risk assessment have been documented as part of the `project <https://wiki.onap.org/pages/viewpage.action?pageId=25437092>`_.

Quick Links:
    - `POLICY project page`_
    - `Passing Badge information for POLICY`_
    - `Project Vulnerability Review Table for POLICY <https://wiki.onap.org/pages/viewpage.action?pageId=25437092>`_

**Known Issues**

The following known issues will be addressed in a future release:

    * `[POLICY-522] <https://jira.onap.org/browse/POLICY-522>`_ - PAP REST APIs undesired HTTP response body for 500 responses
    * `[POLICY-608] <https://jira.onap.org/browse/POLICY-608>`_ - xacml components : remove hardcoded secret key from source code
    * `[POLICY-764] <https://jira.onap.org/browse/POLICY-764>`_ - Policy Engine PIP Configuration JUnit Test fails intermittently
    * `[POLICY-776] <https://jira.onap.org/browse/POLICY-776>`_ - OOF Policy TOSCA models are not correctly rendered
    * `[POLICY-799] <https://jira.onap.org/browse/POLICY-799>`_ - Policy API Validation Does Not Validate Required Parent Attributes in the Model
    * `[POLICY-801] <https://jira.onap.org/browse/POLICY-801>`_ - fields mismatch for OOF flavorFeatures between implementation and wiki
    * `[POLICY-869] <https://jira.onap.org/browse/POLICY-869>`_  - Control Loop Drools Rules should not have exceptions as well as die upon an exception
    * `[POLICY-872] <https://jira.onap.org/browse/POLICY-872>`_  - investigate potential race conditions during rules version upgrades during call loads




Version: 1.0.2
--------------

:Release Date: 2018-01-18 (Amsterdam Maintenance Release)

**Bug Fixes**

The following fixes were deployed with the Amsterdam Maintenance Release:

    * `[POLICY-486] <https://jira.onap.org/browse/POLICY-486>`_ - pdp-x api pushPolicy fails to push latest version


Version: 1.0.1
--------------

:Release Date: 2017-11-16 (Amsterdam Release)

**New Features**

The Amsterdam release continued evolving the design driven architecture of and functionality for POLICY.  The following is a list of Epics delivered with the release. For a full list of stories and tasks delivered in the Amsterdam release, refer to `JiraPolicyAmsterdam`_.

    * [POLICY-31] - Stabilization of Seed Code
        - POLICY-25  Replace any remaining openecomp reference by onap
        - POLICY-32  JUnit test code coverage
        - POLICY-66  PDP-D Feature mechanism enhancements
        - POLICY-67  Rainy Day Decision Policy
        - POLICY-93  Notification API
        - POLICY-158  policy/engine: SQL injection Mitigation
        - POLICY-269  Policy API Support for Rainy Day Decision Policy and Dictionaries

    * [POLICY-33] - This epic covers the body of work involved in deploying the Policy Platform components
        - POLICY-40  MSB Integration
        - POLICY-124  Integration with oparent
        - POLICY-41  OOM Integration
        - POLICY-119  PDP-D: noop sinks

    * [POLICY-34] - This epic covers the work required to support a Policy developer environment in which Policy Developers can create, update policy templates/rules separate from the policy Platform runtime platform.
        - POLICY-57  VF-C Actor code development
        - POLICY-43  Amsterdam Use Case Template
        - POLICY-173  Deployment of Operational Policies Documentation

    * [POLICY-35] - This epic covers the body of work involved in supporting policy that is platform specific.
        - POLICY-68  TOSCA Parsing for nested objects for Microservice Policies

    * [POLICY-36] - This epic covers the work required to capture policy during VNF on-boarding.

    * [POLICY-37] - This epic covers the work required to capture, update, extend Policy(s) during Service Design.
        - POLICY-64  CLAMP Configuration and Operation Policies for vFW Use Case
        - POLICY-65  CLAMP Configuration and Operation Policies for vDNS Use Case
        - POLICY-48  CLAMP Configuration and Operation Policies for vCPE Use Case
        - POLICY-63  CLAMP Configuration and Operation Policies for VOLTE Use Case

    * [POLICY-38] - This epic covers the work required to support service distribution by SDC.

    * [POLICY-39] - This epic covers the work required to support the Policy Platform during runtime.
        - POLICY-61  vFW Use Case - Runtime
        - POLICY-62  vDNS Use Case - Runtime
        - POLICY-59  vCPE Use Case - Runtime
        - POLICY-60  VOLTE Use Case - Runtime
        - POLICY-51  Runtime Policy Update Support
        - POLICY-328  vDNS Use Case - Runtime Testing
        - POLICY-324  vFW Use Case - Runtime Testing
        - POLICY-320  VOLTE Use Case - Runtime Testing
        - POLICY-316  vCPE Use Case - Runtime Testing

    * [POLICY-76] - This epic covers the body of work involved in supporting R1 Amsterdam Milestone Release Planning Milestone Tasks.
        - POLICY-77  Functional Test case definition for Control Loops
        - POLICY-387  Deliver the released policy artifacts


**Bug Fixes**
    - This is technically the first release of POLICY, previous release was the seed code contribution. As such, the defects fixed in this release were raised during the course of the release. Anything not closed is captured below under Known Issues. For a list of defects fixed in the Amsterdam release, refer to `JiraPolicyAmsterdam`_.


**Known Issues**
    - The operational policy template has been tested with the vFW, vCPE, vDNS and VOLTE use cases.  Additional development may/may not be required for other scenarios.

    - For vLBS Use Case, the following steps are required to setup the service instance:
       	-  Create a Service Instance via VID.
        -  Create a VNF Instance via VID.
        -  Preload SDNC with topology data used for the actual VNF instantiation (both base and DNS scaling modules). NOTE: you may want to set “vlb_name_0” in the base VF module data to something unique. This is the vLB server name that DCAE will pass to Policy during closed loop. If the same name is used multiple times, the Policy name-query to AAI will show multiple entries, one for each occurrence of that vLB VM name in the OpenStack zone. Note that this is not a limitation, typically server names in a domain are supposed to be unique.
        -  Instantiate the base VF module (vLB, vPacketGen, and one vDNS) via VID. NOTE: The name of the VF module MUST start with ``Vfmodule_``. The same name MUST appear in the SDNC preload of the base VF module topology. We’ll relax this naming requirement for Beijing Release.
        -  Run heatbridge from the Robot VM using ``Vfmodule_`` … as stack name (it is the actual stack name in OpenStack)
        -  Populate AAI with a dummy VF module for vDNS scaling.

**Security Issues**
    - None at this time

**Other**
    - None at this time


.. Links to jira release notes

.. _JiraPolicyDublin: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10464
.. _JiraPolicyCasablanca: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10446
.. _JiraPolicyBeijing: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10349
.. _JiraPolicyAmsterdam: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10300

.. Links to Project related pages

.. _POLICY project page: https://wiki.onap.org/display/DW/Policy+Framework+Project
.. _Passing Badge information for POLICY: https://bestpractices.coreinfrastructure.org/en/projects/1614


.. note
..      CHANGE  HISTORY
..	05/16/2019 - Updated for Dublin Release.
..      01/17/2019 - Updated for Casablanca Maintenance Release.
..      11/19/2018 - Updated for Casablanca.  Also, fixed bugs is a list of bugs where the "Affected Version" is Beijing.
..		Changed version number to use ONAP versions.
..      10/08/2018 - Initial document for Casablanca release.
..		Per Jorge, POLICY-785 did not get done in Casablanca (removed).
..	05/29/2018 - Information for Beijing release.
..      03/22/2018 - Initial document for Beijing release.
..      01/15/2018 - Added change for version 1.1.3 to the Amsterdam branch.  Also corrected prior version (1.2.0) to (1.1.1)
..		Also, Set up initial information for Beijing.
..		Excluded POLICY-454 from bug list since it doesn't apply to Beijing per Jorge.


End of Release Notes

.. How to notes for SS
..	For initial document: list epic and user stories for each, list user stories with no epics.
..     	For Bugs section, list bugs where Affected Version is a prior release (Casablanca, Beijing etc), Fixed Version is the current release (Dublin), Resolution is done.
..     	For Known issues, list bugs that are slotted for a future release.
