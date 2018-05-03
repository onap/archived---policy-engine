.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Installation of Beijing Controller and Policies
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. contents::
    :depth: 2

This article explains how to install the Beijing policy controller and the Beijing policies on a raw ONAP Policy Framework installation running in Docker.

To build a raw Policy Framework installation, please follow either of the HowTo articles below prior to using this HowTo:

    * `Standalone Quick Start <installation.html>`_
    * `Standalone installation in Virtual Machine <installationVM.html>`_

You should have the ONAP Policy Framework running in Docker and started the Policy Framework with *docker-compose*.


Test that the ONAP Policy Framework is up and is empty
------------------------------------------------------

**Step 1:** Run the command below.

	.. code-block:: bash 

	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/beijing | python -m json.tool

	You should get a response similar to the following:

	.. code-block:: bash 

	   {
	       "error": "beijing not found"
	   }

Install the Beijing policy controller
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

	Alternatively, you can build the drools-applications component of the Policy Framework from source and install it from the following location on your build host

	.. code-block:: bash 

	   ~/.m2/repository/org/onap/policy/drools-applications/controlloop/packages/apps-controlloop/1.2.0/apps-controlloop-1.2.0.zip


**Step 4:** Unzip the controller

	.. code-block:: bash 

	   unzip apps-controlloop-1.2.0.zip

**Step 5:** Update the installation script to install the Beijing controller

	.. code-block:: bash 

	   vi apps-controlloop-installer

	   # Change the line
 	   #    "features enable controlloop-amsterdam"
	   # to
	   #    "features enable controlloop-beijing"

 
**Step 6:** Stop the policy engine

	.. code-block:: bash 

	   policy stop

**Step 7:** Install the controller by running the controller installation script

	.. code-block:: bash 

	   ./apps-controlloop-installer
 
**Step 8:** Install the *controlloop-utils* Drools PDP feature to allow standalone execution of control loop policies

	.. code-block:: bash 

	   features install controlloop-utils
	   features enable controlloop-utils
 
**Step 9:** Start the policy engine

	.. code-block:: bash 

	   policy start

	Monitor the CPU for a few minutes with the top command until it settles down after the policy start.

**Step 10:** Check if the Beijing controller is loaded 
	.. code-block:: bash 
 
	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/beijing | python -m json.tool

	You should get a response similar to the following:

	.. code-block:: bash 
   	   :caption: Beijing Controller JSON Response
	   :linenos:

	       "alive": true,
	       "drools": {
	           "alive": true,
	           "artifactId": "controller-beijing",
	           "brained": true,
	           "groupId": "org.onap.policy.drools-applications.controlloop.common",
	           "locked": false,
	           "modelClassLoaderHash": 1562533966,
	           "recentSinkEvents": [],
	           "recentSourceEvents": [],
	           "sessionCoordinates": [
	               "org.onap.policy.drools-applications.controlloop.common:controller-beijing:1.2.0:beijing"
	           ],
	           "sessions": [
	               "beijing"
	           ],
	           "version": "1.2.0"
	       },
	       "locked": false,
	       "name": "beijing",
	       "topicSinks": [
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "locked": false,
	               "partitionKey": "86d1234b-e431-4191-b7c6-56d2d2909a97",
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
	               "partitionKey": "dea0f440-0232-4f63-b79e-6d51f3674d35",
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
	               "partitionKey": "2918d779-870d-429b-b469-78677d027deb",
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
	               "consumerGroup": "31740f8e-f878-4347-849e-3b3352c28dff",
	               "consumerInstance": "drools",
	               "fetchLimit": 100,
	               "fetchTimeout": 15000,
	               "locked": false,
	               "recentEvents": [],
	               "servers": [
	                   "vm1.mr.simpledemo.openecomp.org"
	               ],
	               "topic": "PDPD-CONFIGURATION",
	               "topicCommInfrastructure": "UEB",
	               "useHttps": false
	           },
	           {
	               "alive": true,
	               "allowSelfSignedCerts": false,
	               "apiKey": "",
	               "apiSecret": "",
	               "consumerGroup": "429aa858-633e-43dc-8619-7004e133d650",
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
	               "consumerGroup": "1bf6854d-a0f1-4d03-baaf-084e6f365a86",
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
	               "consumerGroup": "3f0d7fdf-956d-4749-be54-1adb32ccfa4f",
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



Install the Beijing vCPE Policy
---------------------------------

We now install the Beijing policies for the vCPE, vFirewall, vDNS & VOLTE use cases.

**Step 1:** Log onto (or remain logged onto) the Drools PDP.  

	.. code-block:: bash 

	   docker exec -it drools bash

	You now have a shell open in the Drools PDP container running in docker

**Step 2:** Check that the Beijing policies are not loaded by querying for Drools facts

	.. code-block:: bash 

	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/beijing/drools/facts/beijing | python -m json.tool

	Expect the response {} indicating no policies are loaded.

**Step 3:** Create a temporary directory for policy installation

	.. code-block:: bash

	   mkdir /tmp/basex-controlloop
	   cd /tmp/basex-controlloop

**Step 4:** Extract the policies from the control loop zip file previously downloaded from Nexus and expanded in steps 3 and 4 above.

	.. code-block:: bash 

	   tar zxvf /tmp/apps-controlloop/basex-controlloop-1.2.0.tar.gz
 
**Step 5:** Install the Beijing policy rules, the script asks for a number of input parameters, accept the default for all parameters

	.. code-block:: bash 

	   bin/create-cl-beijing

	   # - Type Y when asked for confirmation of parameters
	   # - Accept /tmp as the install directory
	   # - Type Y to agree to creation of a Maven Artifact

 
**Step 6:** Maven artifact creation now proceeds.  After some minutes, confirmation is requested for deployment of rules into Maven. 
	.. code-block:: bash 

	   # - Type Y to deploy the rules. 

	The rules are deployed into Maven. Expect the rule deployment process to take a number of minutes, perhaps 10 minutes.


**Step 7:** Copy the Beijing properties file into the Drools PDP configuration directory. This file configures the Drools PDP with the Beijing policies

	.. code-block:: bash 

	   cp /tmp/beijing/beijing-controller.properties /opt/app/policy/config
 
**Step 8:** Stop and start the Drools PDP

	.. code-block:: bash 

	   policy stop
	   policy start

	Again, monitor the processes with top and wait for the PDP to fully start.

**Step 9:** Push the Beijing policy facts to the Drools PDP

	.. code-block:: bash 

	   bin/push-policies-beijing

	   # When the script prompts for the path to the properties file, enter the following:
	   #    /opt/app/policy/config/beijing-controller.properties


**Step 10:** Now verify that the Beijing policies are loaded, there should be four facts (one per use case) in the Drools PDP

	.. code-block:: bash 

	   curl --silent --user @1b3rt:31nst31n -X GET http://localhost:9696/policy/pdp/engine/controllers/beijing/drools/facts/beijing | python -m json.tool

	Expect the response:

	.. code-block:: bash 

	   {
	      "org.onap.policy.controlloop.params.ControlLoopParams": 4
	   }


Execute the vCPE Use Case Manually
----------------------------------

You can now run the vCPE Policy use case manually using the HowTos below:
    * `Tutorial: Testing the vCPE use case in a standalone PDP-D <tutorial_vCPE.html>`_
    * `Tutorial: Testing the vDNS Use Case in a standalone PDP-D <tutorial_vDNS.html>`_
    * `Tutorial: Testing the vFW flow in a standalone PDP-D <tutorial_vFW.html>`_
    * `Tutorial: Testing the VOLTE Use Case in a standalone PDP-D <tutorial_VOLTE.html>`_


.. note:: 
	1. You should check that the topic names you use match those in the Beijing configuration file */opt/app/policy/config/beijing-controller.properties*.
	2. You should ensure that you change to the directory */tmp/beijing* prior to running those HowTos



.. Installation of Beijing Controller and Policies : https://wiki.onap.org/display/DW/ONAP+Policy+Framework%3A+Installation+of+Beijing+Controller+and+Policies



End of Document

