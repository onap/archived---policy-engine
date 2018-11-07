
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*************************
Feature: Healthcheck
*************************

.. contents::
    :depth: 3

Summary
^^^^^^^
The Healthcheck feature provides reports used to verify the health of *PolicyEngine.manager* in addition to the construction, operation, and deconstruction of HTTP server/client objects.

Usage
^^^^^

When enabled, the feature takes as input a properties file named "*feature-healtcheck.properties*" (example below). This file should contain configuration properties necessary for the construction of HTTP client and server objects.

Upon initialization, the feature first constructs HTTP server and client objects using the properties from its properties file. A healthCheck operation is then triggered. The logic of the healthCheck verifies that *PolicyEngine.manager* is alive, and iteratively tests each HTTP server object by sending HTTP GET requests using its respective client object. If a server returns a "200 OK" message, it is marked as "healthy" in its individual report. Any other return code results in an "unhealthy" report.

After the testing of the server objects has completed, the feature returns a single consolidated report.


    .. code-block:: bash
       :caption: feature-healthcheck.properties
       :linenos:

        ###
        # ============LICENSE_START=======================================================
        # feature-healthcheck
        # ================================================================================
        # Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
        # ================================================================================
        # Licensed under the Apache License, Version 2.0 (the "License");
        # you may not use this file except in compliance with the License.
        # You may obtain a copy of the License at
        #
        #      http://www.apache.org/licenses/LICENSE-2.0
        #
        # Unless required by applicable law or agreed to in writing, software
        # distributed under the License is distributed on an "AS IS" BASIS,
        # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        # See the License for the specific language governing permissions and
        # limitations under the License.
        # ============LICENSE_END=========================================================
        ###
        
        http.server.services=HEALTHCHECK
        http.server.services.HEALTHCHECK.host=0.0.0.0
        http.server.services.HEALTHCHECK.port=6969
        http.server.services.HEALTHCHECK.restClasses=org.onap.policy.drools.healthcheck.RestHealthCheck
        http.server.services.HEALTHCHECK.managed=false
        http.server.services.HEALTHCHECK.swagger=true
        http.server.services.HEALTHCHECK.userName=healthcheck
        http.server.services.HEALTHCHECK.password=zb!XztG34
        
        http.client.services=PAP,PDP
        
        http.client.services.PAP.host=pap
        http.client.services.PAP.port=9091
        http.client.services.PAP.contextUriPath=pap/test
        http.client.services.PAP.https=false
        http.client.services.PAP.userName=testpap
        http.client.services.PAP.password=alpha123
        http.client.services.PAP.managed=true
         
        http.client.services.PDP.host=pdp
        http.client.services.PDP.port=8081
        http.client.services.PDP.contextUriPath=pdp/test
        http.client.services.PDP.https=false
        http.client.services.PDP.userName=testpdp
        http.client.services.PDP.password=alpha123
        http.client.services.PDP.managed=false


To utilize the healthcheck functionality, first stop policy engine and then enable the feature using the "*features*" command.

    .. code-block:: bash
       :caption: Enabling Healthcheck feature

        policy@hyperion-4:/opt/app/policy$ policy stop
        [drools-pdp-controllers]
         L []: Stopping Policy Management... Policy Management (pid=354) is stopping... Policy Management has stopped.
        policy@hyperion-4:/opt/app/policy$ features enable healthcheck
        name                      version         status
        ----                      -------         ------
        controlloop-utils         1.1.0-SNAPSHOT  disabled
        healthcheck               1.1.0-SNAPSHOT  enabled
        test-transaction          1.1.0-SNAPSHOT  disabled
        eelf                      1.1.0-SNAPSHOT  disabled
        state-management          1.1.0-SNAPSHOT  disabled
        active-standby-management 1.1.0-SNAPSHOT  disabled
        session-persistence       1.1.0-SNAPSHOT  disabled


The output of the enable command will indicate whether or not the feature was enabled successfully.

Policy engine can then be started as usual.

The Healthcheck can also be invoked manually as follows:

    .. code-block:: bash
       :caption: Manual Healthcheck invokation


        # Assuming the healthcheck service credentials have not been changed
        # post-installation within the drools container

        source /opt/app/policy/config/feature-healthcheck.conf.environment
        curl -k --silent --user "${HEALTHCHECK_USER}:${HEALTHCHECK_PASSWORD}" -X GET https://localhost:6969/healthcheck | python -m json.tool


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Feature+Healthcheck
