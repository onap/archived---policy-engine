
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

****************************
BRMSGW Software Architecture 
****************************

.. contents::
    :depth: 3

Overview
^^^^^^^^
This document provides an architectural overview of the **BRMSGW**. **BRMS** stands for Business Rule Management System which is based on JBoss Business Rule Management software used by PDP-D or Drools based rules and **GW** is short for Gateway. BRMSGW acts as as interface between PDP-D and PDP-X so that the drools rules are managed/assigned to PDP-D. It manages the rules present in PDP-D and is an integral part of the closed loop architecture.

Context
^^^^^^^
Purpose of BRMSGW:

- PDP-D needs rule artifacts in nexus in order to take them. So, BRMSGW converts the BRMS configuration rules from PDP-X .drl format to PDP-D nexus artifact format. 
- BRMSGW Listens to Notifications from PDP for any BRMS based policies. 
- It manages controllers and dependencies for the PDP-D rule artifacts/jars.
- Upon any notification, BRMS rule (.drl) would be extracted from the PDP-X and updated to the maven project
- Maven deploy is executed to create the rule jar and pushed to nexus repository to make it available for PDP-D. 
- DMaaP/UEB notification is sent to PDP-D regarding the new rule artifact

BRMSGW Application software
^^^^^^^^^^^^^^^^^^^^^^^^^^^

- BRMSGW application is a standalone java application. It can support multiple BRMSGW applications running. But, would run in active-standby mode. This is internally managed by BRMSGW backup monitors. 
- It is a policy client application which only acts on PDP's notifications. The action is only taken if the BRMSGW Node is active and during standby mode actions are skipped in order to avoid multiple nexus jars 
- Upon Notification BRMSGW, will be doing a getConfig call back to PDP to retrieve the brms policy configuration which is the .drl rule. This rule is identified by its controller meta data information and added to appropriate controller's maven project in file system. 
- Once the project structure is updated based on update or remove notification the BRMS push is initiated. During this process maven deploy cycle is executed and the rule jar is pushed to nexus repository. 
- Once the rule jar is pushed to nexus, a notification message is sent to PDP-D. 
- MariaDB - In order to maintain sync and other controller, rule information database is used to keep in sync. 
- FileSystem - Properties, configuration and Rule maven projects are stored in file system. 
- Maven - BRMSGW has dependency on maven installation, hence needs maven and maven's settings.xml file to be available in order to be operating successfully. 
- BRMSGW project is available at https://git.onap.org/policy/engine/tree/BRMSGateway 

Architecture Overview 
^^^^^^^^^^^^^^^^^^^^^
BRMSGW flow is shown below: 

    .. image:: Swarch_brmsgw.png

    1. Notification Listener will be listening to notifications from BRMSGW and will be triggered by PDP's Notification messages. 
    2. If the notification is about BRMS rule, Retrieve rule is called. 
    3. Retrieve rule does a getConfig call using the policy API to PDP-X and retrieves the BRMS configuration rule which is the raw .drl rule. 
    4. The .drl rule retrieved is then passed down to Rule project management. 
    5. The rule is either added to the project or removed from the project as per the notification received and the file system is updated. 
    6. Maven deploy execution cycle is called.which creates the rule jar artifact from the updated project.
    7. Maven deploy is going to upload the rule jar to nexus repository. 
    8. Once nexus is updated a DMaaP/UEB notification message is sent to PDP-D about the new rule artifact. 

        .. code-block:: bash
           :caption: Notification Format that is sent to PDP-D:
    
            {
                "requestID": "7f5474ca-16a9-42ac-abc0-d86f62296fbc",
                "entity": "controller",
                "controllers": [{
                    "name": "closed-loop",
                    "drools": {
                        "groupId": "org.onap.policy-engine.drools",
                        "artifactId": "closed-loop",
                        "version": "1.1.0"
                    },
                    "operation": "create"
                }]
            }


Configuration 
^^^^^^^^^^^^^
- All configurations related to the brmsgw are present in config.properties. 
- All dependency configurations related to controllers are maintained in dependency.json file. 


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/BRMSGW+Software+Architecture


