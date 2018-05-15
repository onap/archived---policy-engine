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


Version: 1.2.0
--------------

:Release Date: 2018-05-24 (Beijing Release)

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

    * POLICY-454	brmsgw 1.1.2 policy rules generation in a 1.1.1 docker image
    * POLICY-484	Extend election handler run window and clean up error messages
    * POLICY-494	POLICY EELF Audit.log not in ECOMP Standards Compliance
    * POLICY-501	Fix issues blocking election handler and add directed interface for opstate
    * POLICY-509	Add IntelliJ file to .gitingore
    * POLICY-510	Do not enforce hostname validation
    * POLICY-518	StateManagement creation of EntityManagers.
    * POLICY-519	Correctly initialize the value of allSeemsWell in DroolsPdpsElectionHandler
    * POLICY-629	Fixed a bug on editor screen
    * POLICY-684	Fix regex for brmsgw dependency handling
    * POLICY-707	ONAO-PAP-REST unit tests fail on first build on clean checkout 
    * POLICY-717	Fix a bug in checking required fields if the object has include function
    * POLICY-734	Fix Fortify Header Manipulation Issue
    * POLICY-743	Fixed data name since its name was changed on server side
    * POLICY-753	Policy Health Check failed with multi-node cluster
    * POLICY-765	junit test for guard fails intermittently
    * POLICY-795	PDP-D allow configuration on OOM install to survive upgrades


**Security Issues**

   * Please see the `Policy R2 Beijing Security/Vulnerability Threat <https://wiki.onap.org/pages/viewpage.action?pageId=25437092>`_ page for security related issues.


Version: 1.1.3
--------------

:Release Date: 2018-01-18 (Amsterdam Maintenance Release)

**Bug Fixes**

The following bugs were deployed with the Amsterdam Maintenance Release:

    * `[POLICY-486] <https://jira.onap.org/browse/POLICY-486>`_ - pdp-x api pushPolicy fails to push latest version


Version: 1.1.1
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

.. _JiraPolicyBeijing: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10349
.. _JiraPolicyAmsterdam: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10300


.. note
..      CHANGE  HISTORY
..      03/22/2018 - Initial document for Beijing release.
..		For initial document: list epic and user stories for each, list user stories with no epics.  
..      	For Bugs section, list bugs that are not tied to an epic.  Remove all items with "Won't Do" resolution.
..      01/15/2018 - Added change for version 1.1.3 to the Amsterdam branch.  Also corrected prior version (1.2.0) to (1.1.1)
..      11/16/2017 - Initial document for Amsterdam release.
 

End of Release Notes



