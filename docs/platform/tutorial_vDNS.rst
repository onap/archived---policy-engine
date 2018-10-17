
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*********************************************************
Tutorial: Testing the vDNS Use Case in a standalone PDP-D
*********************************************************

.. contents::
    :depth: 3

In this tutorial we will go over how to access and start up the PDP-D, setup the prerequisites for the vDNS flow, enable/disable the AAI and SO Simulators that will be used in the vDNS flow, and inject messages to trigger the vDNS flow.

Accessing and starting the PDP-D
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

The first step is to access the docker container of name *drools*.

    .. code-block:: bash

        docker exec -it -u 0 drools su - policy

The PDP-D software is installed under the *policy* account, the policy root directory is under *${POLICY_HOME}* environment variable and it may be changed on a per installation basis.   It is typically set up under the */opt/app/policy* directory but can be changed during installation.   All PDP-D software runs with non-root privileges as *policy* is a regular user account.

Once within the drools container, the running status can be observed by using the *policy* command:

    .. code-block:: bash
    
        policy [--debug] status|start|stop
    
The running status of the PDP-D can be observed with *policy status*

    .. code-block:: bash
    
        policy@drools:~$ policy status [drools-pdp-controllers]  L []: Policy Management (pid 1500) is running  1 cron jobs installed.
    

Prerequisites for the vDNS flow
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

In order to trigger the vDNS flow we will need to inject an ONSET message via curl command. We're going to create a temporary *util* directory to store a file that contains the vDNS ONSET message.

Navigate to */tmp* and create directory *util*.  *util* is just a temporary folder we've created to use as our 'workspace'.

    .. code-block:: bash
    
        cd /tmp
        mkdir util


Next, we're going to create a file named *dcae.vdns.onset.json* and edit it to paste the vDNS ONSET message contents.

    .. code-block:: bash
    
        touch dcae.vdns.onset.json
        vi dcae.vdns.onset.json

Here are the contents of the vDNS ONSET message. Copy/paste this into dcae.vdns.onset.json:

    .. code-block:: json
    
        {
          "closedLoopControlName": "ControlLoop-vDNS-6f37f56d-a87d-4b85-b6a9-cc953cf779b3",
          "closedLoopAlarmStart": 1484677482204798,
          "closedLoopEventClient": "DCAE_INSTANCE_ID.dcae-tca",
          "closedLoopEventStatus": "ONSET",
          "requestID": "e4f95e0c-a013-4530-8e59-c5c5f9e539b6",
          "target_type": "VNF",
          "target": "vserver.vserver-name",
          "AAI": {
            "vserver.is-closed-loop-disabled": "false",
            "vserver.prov-status": "ACTIVE",
            "vserver.vserver-name": "dfw1lb01lb01"
          },
          "from": "DCAE",
          "version": "1.0.2"
        }
        


Enabling the AAI and SO Simulators
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

Enabling the *controlloop-utils* feature will enable the simulators. To do this, simply stop the drools pdp, enable the feature, and restart the drools pdp like so: 

    .. code-block:: bash
    
        policy stop
        features enable controlloop-utils
        policy start

Now, in */opt/app/policy/config/* directory, you should see a new properties file named *simulators.properties.environment*. In here you will find the credentials for the AAI and SO simulators.

Injecting an ONSET to trigger the vDNS Flow
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 

We are now ready to inject an ONSET message to trigger the vDNS flow. Simply navigate back to the directory *dcae.vdns.onset.json* file is saved (i.e. cd /tmp/util) and run this curl command:

    .. code-block:: bash
    
        http --verify=no --default-scheme=https -a @1b3rt:31nst31n PUT :9696/policy/pdp/engine/topics/sources/ueb/unauthenticated.DCAE_CL_OUTPUT/events @dcae.vdns.onset.json Content-Type:"text/plain"

You should see some output similar to this:

    .. image:: tutorial_vDNS_1.png

You can view the logs to see the network activity or find any errors that may have occurred. Logs are located in */opt/app/policy/logs*.

Reading the logs
^^^^^^^^^^^^^^^^

Once you've injected the onset message, this should appear in the network.log:

    .. image:: tutorial_vDNS_2.png


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Tutorial%3A+Testing+the+vDNS+Use+Case+in+a+standalone+PDP-D
