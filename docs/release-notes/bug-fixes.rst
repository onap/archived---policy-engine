.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Bug Fixes
---------

ONAP POLICY Framework items for: (Last Updated: 10/23/2017)

*    **Version**: Amsterdam Release
*    **Release Date**: 02 November, 2017
*    **Description**: R1

Bug
^^^

    * [POLICY-12] - Clean up warnings in drools-pdp project
    * [POLICY-14] - Fix docker-compose to use onap/* instead of openecomp/*
    * [POLICY-15] - 1.1.0-SNAPSHOT bring up issues
    * [POLICY-16] - correct problems introduced by swagger addition to pdp-d
    * [POLICY-19] - PDP-X getConfig API Authentication error
    * [POLICY-20] - PDP-X getConfig - Configuration Policy not found
    * [POLICY-21] - PDP-D occasionally stuck during shutdown
    * [POLICY-23] - PDP-X getConfig issues
    * [POLICY-24] - PDP-X pushPolicy problems
    * [POLICY-27] - excessive long idle timeouts in DB introduced unnecessarily
    * [POLICY-29] - BRMS GW - client configuration
    * [POLICY-74] - policy-engine build failure
    * [POLICY-75] - KieScanner repeatedly updating Drools container
    * [POLICY-84] - Query Name validation before execution
    * [POLICY-91] - drools-applications - vFW/vDNS dependencies issues
    * [POLICY-92] - brmsgw - vFW/vDNS dependency rename issue
    * [POLICY-94] - Health check failure in R1.1.0 in DFW on Rackspace
    * [POLICY-134] - PolicyLogger.info(Object arg0) executes recursively causing stack overflow error.
    * [POLICY-144] - PAP: console: browser access: 500 code
    * [POLICY-145] - Healthcheck functionality not working
    * [POLICY-146] - brmsgw: vFW/vDNS rules jars not generated
    * [POLICY-165] - Support Backward compatibility for the Policy Interface
    * [POLICY-170] - policy build by vagrant fails
    * [POLICY-175] - 0% Code Coverage in some projects
    * [POLICY-191] - PDP-D: Enhancement: feature session-persistence
    * [POLICY-192] - verify and/or merge job hang running JUnit test
    * [POLICY-194] - policy/drools-applications: fix template to enable JUnits
    * [POLICY-195] - policy/drools-pdp/session-persistence: remove new sonar blocker and criticals
    * [POLICY-197] - drools-applications: logger change breaks junit on drl template
    * [POLICY-199] - Policy UI page displays blank in ONAP
    * [POLICY-203] - Test case testGetPropertiesValue fails  if we run test case or build code other than C: 
    * [POLICY-205] - policy/engine: missing license files
    * [POLICY-206] - Missing license for drools-applications
    * [POLICY-211] - Policy Fails Robot Health Check
    * [POLICY-213] - remove sonar blocker/criticals introduced by feature-test-transaction
    * [POLICY-221] - Policy GUI Cosmetic Issues
    * [POLICY-223] - PDP Health Check Fail
    * [POLICY-229] - Policy healthcheck failure in RS ORD ONAP 1.1.0 
    * [POLICY-234] - PDP-D: drools session remains hung on an update
    * [POLICY-275] - pdp-x: automated pushing of policies shows that some operations failed
    * [POLICY-276] - brmsgw: not generating amsterdam rules jar
    * [POLICY-278] - console: sql injection protection not working properly
    * [POLICY-295] - brmsgw: amsterdam controller rules generation cannot be built
    * [POLICY-296] - pdp-d guard db access causes control loop failures
    * [POLICY-298] - pdp-d: drl template drops messages in clean up rules
    * [POLICY-299] - Policy CSIT tests are running over 15 hours long
    * [POLICY-300] - Use correct format for messages from Policy to APPC_LCM_READ
    * [POLICY-301] - DB sessionpersistence.sessioninfo to mediumblob
    * [POLICY-302] - brmsgw: set up rules should not be generated
    * [POLICY-305] - Policy Guard Deny response
    * [POLICY-306] - vDNS yaml that is pushed should contain "SO" as actor not "MSO"
    * [POLICY-310] - Policy GUI Fixes
    * [POLICY-313] - Update MicroService vCPE Config Policy with new values
    * [POLICY-314] - pdp-d: better handling of unsuccessful AAI responses
    * [POLICY-315] - Change back docker compose version to 2 from 3
    * [POLICY-339] - pdp-d apps aai vdns : update aai interface from v8 to v11
    * [POLICY-340] - console: upgrade to latest portal sdk
    * [POLICY-341] - pdp-d apps vfw: subsequent onsets on same resource not discarded
    * [POLICY-342] - docker push-policies : config and operational policies must use the same control loop name
    * [POLICY-343] - console: displaying corporate logo
    * [POLICY-344] - Operations History Persistence Unit Not Found
    * [POLICY-345] - Operations History Table is not written to
    * [POLICY-350] - pdp-d apps drl: resiliency to exceptions and null values returned/thrown from java models
    * [POLICY-353] - Bug in UebTopicSinkFactory
    * [POLICY-354] - Update the latest Portal Properties
    * [POLICY-356] - pdp-d apps: print networked messages over http interface
    * [POLICY-357] - pdp-d db-migrator when ALL dbs flag is used upgrade not working properly 
    * [POLICY-363] - docker pdp-d: set DCAE DMaaP Server default to vm1.mr.simpledemo.openecomp.org
    * [POLICY-364] - Policy template should not reject Event if A&AI lookup fails.



