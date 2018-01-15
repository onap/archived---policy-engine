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

The Amsterdam release continued evolving the design driven architecture of and functionality for POLICY.  The following is a list of Epics delivered with the release. For a full list of stories and tasks delivered in the Amsterdam release, refer to `JiraPolicyReleaseNotes`_.

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
    - This is technically the first release of POLICY, previous release was the seed code contribution. As such, the defects fixed in this release were raised during the course of the release. Anything not closed is captured below under Known Issues. For a list of defects fixed in the Amsterdam release, refer to `JiraPolicyReleaseNotes`_.

.. _JiraPolicyReleaseNotes: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10300

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


.. note
..      CHANGE  HISTORY
..      01/15/2018 - Added change for version 1.1.3 to the Amsterdam branch.  Also corrected prior version (1.2.0) to (1.1.1)
..      11/16/2017 - Initial document for Amsterdam release.
 

End of Release Notes



