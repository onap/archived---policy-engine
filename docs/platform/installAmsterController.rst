.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Installation of Amsterdam Controller and vCPE Policy 
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. contents::
    :depth: 2

This article explains how to install the Amsterdam policy controller and the Amsterdam policies on a raw ONAP Policy Framework installation running in Docker.

To build a raw Policy Framework installation, please follow either of the HowTo articles below prior to using this HowTo:

    * `Standalone Quick Start <installation.html>`_
    * `Standalone installation in Virtual Machine <installationVM.html>`_

You should have the ONAP Policy Framework running in Docker and started the Policy Framework with *docker-compose*.


Test that the ONAP Policy Framework is up and is empty
------------------------------------------------------

**Step 1:** Run the command below.

	.. code-block:: bash 

	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/amsterdam | python -m json.tool

	You should get a response similar to the following:

	.. code-block:: bash 

	   {
	       "error": "amsterdam not found"
	   }

Install the Amsterdam policy controller
---------------------------------------

**Step 1:** Log onto the Drools PDP.  

	.. code-block:: bash 

	   docker exec -it drools bash

	You now have a shell open in the Drools PDP container running in docker


**Step 2:** Create a temporary directory for controller installation

	.. code-block:: bash

	   mkdir /tmp/apps-controlloop
	   cd /tmp/apps-controlloop

**Step 3:** Download the latest controller from Nexus (1.2.0 at the time of writing)

	.. code-block:: bash 

	   wget https://nexus.onap.org/content/repositories/releases/org/onap/policy/drools-applications/controlloop/packages/apps-controlloop/1.2.0/apps-controlloop-1.2.0.zip
 
**Step 4:** Unzip the controller

	.. code-block:: bash 

	   unzip apps-controlloop-1.2.0.zip
 
**Step 5:** Stop the policy engine

	.. code-block:: bash 

	   policy stop

**Step 6:** Install the controller by running the controller installation script

	.. code-block:: bash 

	   ./apps-controlloop-installer
 
**Step 7:** Install the controlloop-utils Drools PDP feature to allow standalone execution of control loop policies

	.. code-block:: bash 

	   features install controlloop-utils
	   features enable controlloop-utils
 
**Step 8:** Start the policy engine

	.. code-block:: bash 

	   policy start

**Step 9:** Check if the Amsterdam controller is loaded 
	.. code-block:: bash 
 
	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/amsterdam | python -m json.tool

	You should get a response similar to the following:

	.. code-block:: bash 
   	   :caption: Amsterdam Controller JSON Response
	   :linenos:

	   {
	       "alive": true,
	       "drools": {
	           "alive": false,
	           "artifactId": "NO-ARTIFACT-ID",
	           "brained": false,
	           "canonicalSessionNames": [],
	           "container": null,
	           "groupId": "NO-GROUP-ID",
	           "locked": false,
	           "recentSinkEvents": [],
	           "recentSourceEvents": [],
	           "sessionNames": [],
	           "version": "NO-VERSION"
	       },
	       "locked": false,
	       "name": "amsterdam",
	       "topicSinks": [
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "locked": false,
	               "partitionKey": "ea44d32e-e2e6-4a77-862b-aa33437179ed",
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "APPC-CL",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           },
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "locked": false,
	               "partitionKey": "47769d22-03c8-4993-9f67-fe326a491b23",
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "APPC-LCM-READ",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           },
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "locked": false,
	               "partitionKey": "c33ca5ca-6ebd-47d7-a495-5a54f8a2a15a",
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "POLICY-CL-MGT",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           }
	       ],
	       "topicSources": [
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "consumerGroup": "6dcfdfb8-7c54-4dbd-9337-e4f1883083fb",
	               "consumerInstance": "drools",
	               "fetchLimit": 100,
	               "fetchTimeout": 15000,
	               "locked": false,
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "unauthenticated.DCAE_CL_OUTPUT",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           },
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "consumerGroup": "4f558331-3d32-494b-b7dc-4d5a509dda0d",
	               "consumerInstance": "drools",
	               "fetchLimit": 100,
	               "fetchTimeout": 15000,
	               "locked": false,
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "APPC-CL",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           },
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "consumerGroup": "1e3edc1f-afa6-4ae5-907f-a7118ad7a0d2",
	               "consumerInstance": "drools",
	               "fetchLimit": 100,
	               "fetchTimeout": 15000,
	               "locked": false,
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "APPC-LCM-WRITE",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           }
	       ]
	   }


Install the Amsterdam vCPE Policy
---------------------------------

We now install the Amsterdam policies for the vCPE, vFirewall, vDNS & VOLTE use cases.

**Step 1:** Log onto (or remain logged onto) the Drools PDP.  

	.. code-block:: bash 

	   docker exec -it drools bash

	You now have a shell open in the Drools PDP container running in docker

**Step 2:** Check that the Amsterdam policies are not loaded by querying for Drools facts

	.. code-block:: bash 

	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/amsterdam/drools/facts/amsterdam | python -m json.tool

	Expect the response {} indicating no policies are loaded.

**Step 3:** Create a temporary directory for policy installation

	.. code-block:: bash

	   mkdir /tmp/basex-controlloop
	   cd /tmp/basex-controlloop

**Step 4:** Extract the policies from the control loop zip file previously downloaded from Nexus and expanded in steps 3 and 4 above.

	.. code-block:: bash 

	   tar zxvf /tmp/apps-controlloop/basex-controlloop-1.2.0.tar.gz
 
**Step 5:** Install the Amsterdam policy rules, the script asks for a number of input parameters, accept the default for all parameters

	.. code-block:: bash 

	   bin/create-cl-amsterdam

	   - Type Y when asked for confirmation of parameters
	   - Accept /tmp as the install directory
	   - Type Y to agree to creation of a Maven Artifact

 
**Step 6:** Maven artifact creation now proceeds.  After some minutes, confirmation is requested for deployment of rules into Maven. 
	.. code-block:: bash 

	   - Type Y to deploy the rules. 

	The rules are deployed into Maven. Expect the rule deployment process to take a number of minutes, perhaps 10 minutes.


**Step 7:** Copy the Amsterdam properties file into the Drools PDP configuration directory. This file configures the Drools PDP with the Amsterdam policies

	.. code-block:: bash 

	   cp /tmp/amsterdam/amsterdam-controller.properties /opt/app/policy/config
 
**Step 8:** Stop and start the Drools PDP

	.. code-block:: bash 

	   policy stop
	   policy start

**Step 9:** Now verify that the Amsterdam policies are loaded, there should be a fact in the Drools PDP

	.. code-block:: bash 

	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/amsterdam/drools/facts/amsterdam | python -m json.tool

	Expect the response:

	.. code-block:: bash 

	   {
	      "org.onap.policy.controlloop.Params": 1
	   }


Execute the vCPE Use Case Manually
----------------------------------

You can now run the vCPE Policy use case manually using the HowTos below:
    * `Tutorial: Testing the vCPE use case in a standalone PDP-D <tutorial_vCPE.html>`_

.. note:: 
	1. You should check that the topic names you use match those in the Amsterdam configuration file */opt/app/policy/config/amsterdam-controller.properties*.
	2. You should ensure that you change to the directory */tmp/amsterdam* prior to running those HowTos



.. Installation of Amsterdam Controller and vCPE Policy : https://wiki.onap.org/display/DW/ONAP+Policy+Framework%3A+Installation+of+Amsterdam+Controller+and+vCPE+Policy



End of Document

