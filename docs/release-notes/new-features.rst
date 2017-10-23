.. This work is licensed under a Creative Commons Attribution 4.0 International License.

New Features
------------

ONAP POLICY Framework items for: (Last Updated: 10/23/2017)

*    **Version**: Amsterdam Release
*    **Release Date**: 02 November, 2017
*    **Description**: R1

.. contents::
    :depth: 2

Epic
^^^^

    * [POLICY-31] - Stabilization of Seed Code
    * [POLICY-33] - This epic covers the body of work involved in deploying the Policy Platform components
    * [POLICY-34] - This epic covers the work required to support a Policy developer environment in which Policy Developers can create, update policy templates/rules separate from the policy Platform runtime platform.
    * [POLICY-35] - This epic covers the body of work involved in supporting policy that is platform specific.
    * [POLICY-37] - This epic covers the work required to capture, update, extend Policy(s) during Service Design.
    * [POLICY-39] - This epic covers the work required to support the Policy Platform during runtime.
    * [POLICY-76] - This epic covers the body of work involved in supporting R1 Amsterdam Milestone Release Planning Milestone Tasks.

Story
^^^^^

    * [POLICY-25] - Replace any remaining openecomp reference by onap
    * [POLICY-32] - JUnit test code coverage
    * [POLICY-40] - MSB Integration
    * [POLICY-41] - OOM Integration
    * [POLICY-43] - Amsterdam Use Case Template
    * [POLICY-48] - CLAMP Configuration and Operation Policies for vCPE Use Case
    * [POLICY-51] - Runtime Policy Update Support
    * [POLICY-57] - VF-C Actor code development
    * [POLICY-59] - vCPE Use Case - Runtime
    * [POLICY-60] - VOLTE Use Case - Runtime
    * [POLICY-61] - vFW Use Case - Runtime
    * [POLICY-62] - vDNS Use Case - Runtime
    * [POLICY-63] - CLAMP Configuration and Operation Policies for VOLTE Use Case
    * [POLICY-64] - CLAMP Configuration and Operation Policies for vFW Use Case
    * [POLICY-65] - CLAMP Configuration and Operation Policies for vDNS Use Case
    * [POLICY-66] - PDP-D Feature mechanism enhancements
    * [POLICY-67] - Rainy Day Decision Policy
    * [POLICY-68] - TOSCA Parsing for nested objects for Microservice Policies
    * [POLICY-77] - Functional Test case definition for Control Loops
    * [POLICY-93] - Notification API
    * [POLICY-119] - PDP-D: noop sinks
    * [POLICY-121] - Update POM to inherit from oparent
    * [POLICY-124] - Integration with oparent
    * [POLICY-158] - policy/engine:  SQL injection Mitigation
    * [POLICY-161] - Security Event Logging
    * [POLICY-173] - Deployment of Operational Policies Documentation
    * [POLICY-210] - Independent Versioning and Release Process
    * [POLICY-316] - vCPE Use Case - Runtime Testing
    * [POLICY-320] - VOLTE Use Case - Runtime Testing
    * [POLICY-324] - vFW Use Case - Runtime Testing
    * [POLICY-328] - vDNS Use Case - Runtime Testing


Task
^^^^

    * [POLICY-3] - Add appropriate unit tests back into the distribution
    * [POLICY-6] - Updates to License and Trademark in the Policy Source Code
    * [POLICY-8] - Fix PDP-D Sonar Blocker Issues
    * [POLICY-11] - Enhancements to extract fact information for a given Drools Application
    * [POLICY-17] - Removal of ECOMP-SDK-APP dbscripts
    * [POLICY-18] - Enhancement: Add additional feature hooks in Drools PDP
    * [POLICY-22] - drools-applications reorganization and template upgrade
    * [POLICY-26] - PDP-D Telemetry REST CLI
    * [POLICY-30] - PDP-D Logging Enhancements
    * [POLICY-53] - Update drools-application docker scripts to find reorganized pom changes
    * [POLICY-54] - POLICY-54 Add HTTP Proxy Support to docker image builds
    * [POLICY-55] - Ensure drools-application JUnit test works on ONAP JJB using in memory database
    * [POLICY-58] - Update Policy Engine dependencies and use case policies to support the latest drools-application code
    * [POLICY-70] - Add vagrant functionality to build/setup policy
    * [POLICY-78] - Update Drools version to 6.5.0.Final
    * [POLICY-80] - Policy Guard
    * [POLICY-87] - Implement Recommended Fix for Portal
    * [POLICY-88] - Delete the yaml sdk and sdc projects located in policy/engine
    * [POLICY-95] - Fix policy/commons sonar blocker issues with exception of integrity-monitor/audit
    * [POLICY-96] - Fix policy/common integrity-[monitor/audit] sonar blockers
    * [POLICY-97] - Fix policy/engine sonar blockers
    * [POLICY-98] - policy/commons critical sonar issues - NON integrity/monitor related
    * [POLICY-100] - fix policy/common critical sonar items - integrity monitor/audit related
    * [POLICY-105] - Common-Modules changes to RefreshStateAudit
    * [POLICY-111] - policy/drools-applications: sonar blockers
    * [POLICY-113] - policy/engine: sonar critical
    * [POLICY-114] - policy/drools-pdp: sonar critical
    * [POLICY-115] - policy/drools-application: sonar critical
    * [POLICY-116] - Remove MojoHaus Maven plug-in from pom file
    * [POLICY-117] - Resolve Policy Sonar Critical issues
    * [POLICY-122] - Policy GUI Fixes for Dictionary and Dashboard tabs
    * [POLICY-125] - Update Project FOSS Table
    * [POLICY-126] - Identify and outline the set of documentations to be delivered in this Release
    * [POLICY-133] - Addition of policy-persistence feature
    * [POLICY-154] - policy/engine finish eclipse warnings for unused imports/variables
    * [POLICY-155] - Addition of state-management feature
    * [POLICY-156] - Addition of active-standby-management feature
    * [POLICY-163] - Add 'install' and 'uninstall' option to DroolsPDP 'features' script
    * [POLICY-167] - Clean additional pom warnings policy/engine
    * [POLICY-168] - Clean pom warnings policy/drools-applications
    * [POLICY-171] - Update policy/docker README to reflect current project paths
    * [POLICY-176] - policy/drools-applications: convert System.out, System.err to use slf4j/logback
    * [POLICY-177] - test-transaction feature to check on the healthiness of policy controllers
    * [POLICY-178] - policy/common: convert integrity monitor to use slf4j/logback as in drools-pdp
    * [POLICY-180] - Upgrade policy/engine release build to use more memory
    * [POLICY-181] - Policy/common change jenkins job to not ignore JUnit tests for verify job.
    * [POLICY-193] - Created a new cleanup process
    * [POLICY-196] - Enhancement on MS JSON File
    * [POLICY-218] - LF Open Source License Issues
    * [POLICY-228] - Create common object to consolidate AAI response 
    * [POLICY-231] - Remove Binary files from Policy Repo
    * [POLICY-237] - Address remaining sonar/critical for policy/common
    * [POLICY-239] - Address sonar major issues
    * [POLICY-247] - Modify docker push-policies to push the Amsterdam Template and latest policies
    * [POLICY-253] - Modify policy/engine pom.xml to remove sonar scan on 3rd party code.
    * [POLICY-261] - policy/drools-pdp last remaining sonar critical
    * [POLICY-266] - feature-state-management JUnit coverage
    * [POLICY-273] - Revert the SDK properties from onap to ecomp
    * [POLICY-274] - .gitignore policy/engine directories/files created after running mvn clean install or mvn test
    * [POLICY-297] - policy/drools-applications stage-site build is failing due to heap space
    * [POLICY-304] - Add ability for Docker scripts to NOT pre-load policies for testing
    * [POLICY-307] - Delete the pre-Amsterdam policy template etc.
    * [POLICY-312] - Change Policy CSIT Integration Tests to utilize the PRELOAD_POLICIES=false
    * [POLICY-335] - Add more documentation details
    * [POLICY-337] - Update CSIT Integration Tests with latest Config/Operational Policies
    * [POLICY-355] - Exclude dependencies for mysql and iText


Additional Information
^^^^^^^^^^^^^^^^^^^^^^

Latest details for Policy Framework Amsterdam Release can be found at `PolicyReleaseNotes`_.

.. _PolicyReleaseNotes: https://jira.onap.org/secure/ReleaseNote.jspa?projectId=10106&version=10300


