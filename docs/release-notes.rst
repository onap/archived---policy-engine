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


Version: 1.2.3
--------------

:Release Date: 2018-06-07 (Beijing Release)

**New Features**

The Beijing release for POLICY delivered the following Epics. For a full list of stories and tasks delivered in the Beijing release, refer to `JiraPolicyBeijing`_.

    * `[POLICY-390] <https://jira.onap.org/browse/POLICY-390>`_ - This epic covers the work to harden the Policy platform software base (incl 50% JUnit coverage)
        - `POLICY-238 <https://jira.onap.org/browse/POLICY-238>`_	policy/drools-applications: clean up maven structure
        - `POLICY-336 <https://jira.onap.org/browse/POLICY-336>`_	Address Technical Debt
        - `POLICY-338 <https://jira.onap.org/browse/POLICY-338>`_	Address JUnit Code Coverage
        - `POLICY-377 <https://jira.onap.org/browse/POLICY-377>`_	Policy Create API should validate input matches DCAE microservice template
        - `POLICY-389 <https://jira.onap.org/browse/POLICY-389>`_	Cleanup Jenkin's CI/CD process's
        - `POLICY-449 <https://jira.onap.org/browse/POLICY-449>`_	Policy API + Console : Common Policy Validation
        - `POLICY-568 <https://jira.onap.org/browse/POLICY-568>`_	Integration with org.onap AAF project
        - `POLICY-610 <https://jira.onap.org/browse/POLICY-610>`_	Support vDNS scale out for multiple times in Beijing release

    * `[POLICY-391] <https://jira.onap.org/browse/POLICY-391>`_ - This epic covers the work to support Release Planning activities
        - `POLICY-552 <https://jira.onap.org/browse/POLICY-552>`_	ONAP Licensing Scan - Use Restrictions

    * `[POLICY-392] <https://jira.onap.org/browse/POLICY-392>`_ - Platform Maturity Requirements - Performance Level 1
        - `POLICY-529 <https://jira.onap.org/browse/POLICY-529>`_	Platform Maturity Performance - Drools PDP
        - `POLICY-567 <https://jira.onap.org/browse/POLICY-567>`_	Platform Maturity Performance - PDP-X

    * `[POLICY-394] <https://jira.onap.org/browse/POLICY-394>`_ - This epic covers the work required to support a Policy developer environment in which Policy Developers can create, update policy templates/rules separate from the policy Platform runtime platform.
        - `POLICY-488 <https://jira.onap.org/browse/POLICY-488>`_	pap should not add rules to official template provided in drools applications

    * `[POLICY-398] <https://jira.onap.org/browse/POLICY-398>`_ - This epic covers the body of work involved in supporting policy that is platform specific.
        - `POLICY-434 <https://jira.onap.org/browse/POLICY-434>`_	need PDP /getConfig to return an indicator of where to find the config data - in config.content versus config field

    * `[POLICY-399] <https://jira.onap.org/browse/POLICY-399>`_ - This epic covers the work required to policy enable Hardware Platform Enablement
        - `POLICY-622 <https://jira.onap.org/browse/POLICY-622>`_	Integrate OOF Policy Model into Policy Platform

    * `[POLICY-512] <https://jira.onap.org/browse/POLICY-512>`_ - This epic covers the work to support Platform Maturity Requirements - Stability Level 1
        - `POLICY-525 <https://jira.onap.org/browse/POLICY-525>`_	Platform Maturity Stability - Drools PDP
        - `POLICY-526 <https://jira.onap.org/browse/POLICY-526>`_	Platform Maturity Stability - XACML PDP

    * `[POLICY-513] <https://jira.onap.org/browse/POLICY-513>`_ - Platform Maturity Requirements - Resiliency Level 2
        - `POLICY-527 <https://jira.onap.org/browse/POLICY-527>`_	Platform Maturity Resiliency - Policy Engine GUI and PAP
        - `POLICY-528 <https://jira.onap.org/browse/POLICY-528>`_	Platform Maturity Resiliency - Drools PDP
        - `POLICY-569 <https://jira.onap.org/browse/POLICY-569>`_	Platform Maturity Resiliency - BRMS Gateway
        - `POLICY-585 <https://jira.onap.org/browse/POLICY-585>`_	Platform Maturity Resiliency - XACML PDP
        - `POLICY-586 <https://jira.onap.org/browse/POLICY-586>`_	Platform Maturity Resiliency - Planning
        - `POLICY-681 <https://jira.onap.org/browse/POLICY-681>`_	Regression Test Use Cases

    * `[POLICY-514] <https://jira.onap.org/browse/POLICY-514>`_ - This epic covers the work to support Platform Maturity Requirements - Security Level 1
        - `POLICY-523 <https://jira.onap.org/browse/POLICY-523>`_	Platform Maturity Security - CII Badging - Project Website

    * `[POLICY-515] <https://jira.onap.org/browse/POLICY-515>`_ - This epic covers the work to support Platform Maturity Requirements - Escalability Level 1
        - `POLICY-531 <https://jira.onap.org/browse/POLICY-531>`_	Platform Maturity Scalability - XACML PDP
        - `POLICY-532 <https://jira.onap.org/browse/POLICY-532>`_	Platform Maturity Scalability - Drools PDP
        - `POLICY-623 <https://jira.onap.org/browse/POLICY-623>`_	Docker image re-design

    * `[POLICY-516] <https://jira.onap.org/browse/POLICY-516>`_ - This epic covers the work to support Platform Maturity Requirements - Manageability Level 1
        - `POLICY-533 <https://jira.onap.org/browse/POLICY-533>`_	Platform Maturity Manageability L1 - Logging
        - `POLICY-534 <https://jira.onap.org/browse/POLICY-534>`_	Platform Maturity Manageability - Instantiation < 1 hour

    * `[POLICY-517] <https://jira.onap.org/browse/POLICY-517>`_ - This epic covers the work to support Platform Maturity Requirements - Usability Level 1
        - `POLICY-535 <https://jira.onap.org/browse/POLICY-535>`_	Platform Maturity Usability - User Guide
        - `POLICY-536 <https://jira.onap.org/browse/POLICY-536>`_	Platform Maturity Usability - Deployment Documentation
        - `POLICY-537 <https://jira.onap.org/browse/POLICY-537>`_	Platform Maturity Usability - API Documentation

    * `[POLICY-546] <https://jira.onap.org/browse/POLICY-546>`_ - R2 Beijing - Various enhancements requested by clients to the way we handle TOSCA models.


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
 	- `POLICY project page <https://wiki.onap.org/display/DW/Policy+Framework+Project>`_

 	- `Passing Badge information for POLICY <https://bestpractices.coreinfrastructure.org/en/projects/1614>`_

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




Version: 1.1.3
--------------

:Release Date: 2018-01-18 (Amsterdam Maintenance Release)

**Bug Fixes**

The following fixes were deployed with the Amsterdam Maintenance Release:

    * `[POLICY-486] <https://jira.onap.org/browse/POLICY-486>`_ - pdp-x api pushPolicy fails to push latest version


Version: 1.1.1
--------------

:Release Date: 2017-11-16 (Amsterdam Release)

**New Features**

The Amsterdam release continued evolving the design driven architecture of and functionality for POLICY.  The following is a list of Epics delivered with the release. For a full list of stories and tasks delivered in the Amsterdam release, refer to `JiraPolicyAmsterdam`_.

    * `[POLICY-31] <https://jira.onap.org/browse/POLICY-31>`_ - Stabilization of Seed Code
        - `POLICY-25 <https://jira.onap.org/browse/POLICY-25>`_  Replace any remaining openecomp reference by onap
        - `POLICY-32 <https://jira.onap.org/browse/POLICY-32>`_  JUnit test code coverage
        - `POLICY-66 <https://jira.onap.org/browse/POLICY-66>`_  PDP-D Feature mechanism enhancements
        - `POLICY-67 <https://jira.onap.org/browse/POLICY-67>`_  Rainy Day Decision Policy
        - `POLICY-93 <https://jira.onap.org/browse/POLICY-93>`_  Notification API
        - `POLICY-158 <https://jira.onap.org/browse/POLICY-158>`_  policy/engine: SQL injection Mitigation
        - `POLICY-269 <https://jira.onap.org/browse/POLICY-269>`_  Policy API Support for Rainy Day Decision Policy and Dictionaries  

    * `[POLICY-33] <https://jira.onap.org/browse/POLICY-33>`_ - This epic covers the body of work involved in deploying the Policy Platform components
        - `POLICY-40 <https://jira.onap.org/browse/POLICY-40>`_  MSB Integration  
        - `POLICY-124 <https://jira.onap.org/browse/POLICY-124>`_  Integration with oparent  
        - `POLICY-41 <https://jira.onap.org/browse/POLICY-41>`_  OOM Integration  
        - `POLICY-119 <https://jira.onap.org/browse/POLICY-119>`_  PDP-D: noop sinks  

    * `[POLICY-34] <https://jira.onap.org/browse/POLICY-34>`_ - This epic covers the work required to support a Policy developer environment in which Policy Developers can create, update policy templates/rules separate from the policy Platform runtime platform.
        - `POLICY-57 <https://jira.onap.org/browse/POLICY-57>`_  VF-C Actor code development  
        - `POLICY-43 <https://jira.onap.org/browse/POLICY-43>`_  Amsterdam Use Case Template  
        - `POLICY-173 <https://jira.onap.org/browse/POLICY-173>`_  Deployment of Operational Policies Documentation  

    * `[POLICY-35] <https://jira.onap.org/browse/POLICY-35>`_ - This epic covers the body of work involved in supporting policy that is platform specific.
        - `POLICY-68 <https://jira.onap.org/browse/POLICY-68>`_  TOSCA Parsing for nested objects for Microservice Policies  

    * `[POLICY-36] <https://jira.onap.org/browse/POLICY-36>`_ - This epic covers the work required to capture policy during VNF on-boarding.

    * `[POLICY-37] <https://jira.onap.org/browse/POLICY-37>`_ - This epic covers the work required to capture, update, extend Policy(s) during Service Design.
        - `POLICY-64 <https://jira.onap.org/browse/POLICY-64>`_  CLAMP Configuration and Operation Policies for vFW Use Case  
        - `POLICY-65 <https://jira.onap.org/browse/POLICY-65>`_  CLAMP Configuration and Operation Policies for vDNS Use Case  
        - `POLICY-48 <https://jira.onap.org/browse/POLICY-48>`_  CLAMP Configuration and Operation Policies for vCPE Use Case 
        - `POLICY-63 <https://jira.onap.org/browse/POLICY-63>`_  CLAMP Configuration and Operation Policies for VOLTE Use Case  

    * `[POLICY-38] <https://jira.onap.org/browse/POLICY-38>`_ - This epic covers the work required to support service distribution by SDC.

    * `[POLICY-39] <https://jira.onap.org/browse/POLICY-39>`_ - This epic covers the work required to support the Policy Platform during runtime.
        - `POLICY-61 <https://jira.onap.org/browse/POLICY-61>`_  vFW Use Case - Runtime  
        - `POLICY-62 <https://jira.onap.org/browse/POLICY-62>`_  vDNS Use Case - Runtime  
        - `POLICY-59 <https://jira.onap.org/browse/POLICY-59>`_  vCPE Use Case - Runtime  
        - `POLICY-60 <https://jira.onap.org/browse/POLICY-60>`_  VOLTE Use Case - Runtime  
        - `POLICY-51 <https://jira.onap.org/browse/POLICY-51>`_  Runtime Policy Update Support  
        - `POLICY-328 <https://jira.onap.org/browse/POLICY-328>`_  vDNS Use Case - Runtime Testing  
        - `POLICY-324 <https://jira.onap.org/browse/POLICY-324>`_  vFW Use Case - Runtime Testing  
        - `POLICY-320 <https://jira.onap.org/browse/POLICY-320>`_  VOLTE Use Case - Runtime Testing  
        - `POLICY-316 <https://jira.onap.org/browse/POLICY-316>`_  vCPE Use Case - Runtime Testing  

    * `[POLICY-76] <https://jira.onap.org/browse/POLICY-76>`_ - This epic covers the body of work involved in supporting R1 Amsterdam Milestone Release Planning Milestone Tasks.
        - `POLICY-77 <https://jira.onap.org/browse/POLICY-77>`_  Functional Test case definition for Control Loops  
        - `POLICY-387 <https://jira.onap.org/browse/POLICY-387>`_  Deliver the released policy artifacts  


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

.. _JiraPolicyBeijing: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10349
.. _JiraPolicyAmsterdam: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10300


.. note
..      CHANGE  HISTORY
..      03/22/2018 - Initial document for Beijing release.
..      01/15/2018 - Added change for version 1.1.3 to the Amsterdam branch.  Also corrected prior version (1.2.0) to (1.1.1)
..		Also, Set up initial information for Beijing.
..	05/29/2018 - Information for Beijing release.
..		Excluded POLICY-454 from bug list since it doesn't apply to Beijing per Jorge.
 

End of Release Notes

.. How to notes for SS 
..	For initial document: list epic and user stories for each, list user stories with no epics.  
..     	For Bugs section, list bugs that are not tied to an epic.  Remove all items with "Won't Do" resolution.
..     	For Known issues, list bugs that are slotted for a future release.


