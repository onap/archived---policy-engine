
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

**********************************************************
Control Loop Simulation and Injection of Messages Overview 
**********************************************************

.. contents::
    :depth: 3

Telemetry
^^^^^^^^^
The username and password for the Telemetry commands are in *${POLCIY_HOME}/config/policy-engine.properties*.

Injecting messages:
-------------------

To inject messages use the following command.  The injected message will look as if it came in from the specified topic and will be processed accordingly.

Use the command:

    .. code-block:: bash

        http -a <userName>:<password> PUT :9696/policy/pdp/engine/topics/sources/ueb/<topic>/events @<onsetFile> Content-Type:"text/plain"

Alternatively, this command could be used:

    .. code-block:: bash

        curl --silent --user <userName>:<password> -X PUT --header "Content-Type: text/plain" –data @<onsetFile> http://localhost:9696/policy/pdp/engine/topics/sources/ueb/<topic>/events

The topic being used is *unauthenticated.DCAE_CL_OUTPUT*, which is subject to change.  The onset file is a file that contains the data to inject as the onset.  The data contained depends on the use case. This is an example for VoLTE:

    .. code-block:: json
       :caption: VoLTE_Sample_Onset

        {
            "closedLoopControlName": "ControlLoop-VOLTE-2179b738-fd36-4843-a71a-a8c24c70c55b",
            "closedLoopAlarmStart": 1484677482204798,
            "closedLoopEventClient": "DCAE.HolmesInstance",
            "closedLoopEventStatus": "ONSET",
            "requestID": "97964e10-686e-4790-8c45-bdfa61df770f",
            "target_type": "VM",
            "target": "vserver.vserver-name",
            "AAI": {
                "vserver.is-closed-loop-disabled": "false",
                "vserver.vserver-name": "dfw1lb01lb01",
                "service-instance.service-instance-id" : "vserver-name-16102016-aai3255-data-11-1",
                "generic-vnf.vnf-id" : "vnf-id-16102016-aai3255-data-11-1",
                "generic-vnf.vnf-name" : "vnf-name-16102016-aai3255-data-11-1"
            },
            "from": "DCAE",
            "version": "1.0.2"
        }

Getting Information 
-------------------

To get the name of the controller(s) active, use:

    .. code-block:: bash

        curl --silent --user <username>:<password> -X GET http://localhost:9696/policy/pdp/engine/controllers | python -m json.tool

To check the facts currently in memory, use the following command.  There should be 1 each of org.onap.policy.controlloop.PapParams and org.onap.policy.controlloop.Params per policy pushed.

    .. code-block:: bash

        curl --silent --user <username>:<password> -X GET http://localhost:9696/policy/pdp/engine/controllers/<controllerName>/drools/facts/<artifactId> | python -m json.tool

To get additional information about the controller, use:

    .. code-block:: bash

        curl --silent --user <username>:<password> -X GET http://localhost:9696/policy/pdp/engine/controllers/<controllerName> | python -m json.tool


Simulators
^^^^^^^^^^

Currently, there are 4 supported simulators: A&AI, SO, vFC, and guard.  When they are up, they are accessed via localhost on the following ports: A&AI – 6666, SO – 6667, vFC – 6668, and guard – 6669.  They all respond with hard-coded values representing their various success messages except for with certain inputs.  For the A&AI simulator, if the value being queried with a “GET” query is “getFail” the simulator returns an exception message, if the value being queried in a “GET” query is “disableClosedLoop” the simulator returns a response with the value of “is-closed-loop-disabled” set to true, and if the value being queried in a named query is “error” the response from the simulator is A&AI’s failure message.  The other simulator that can return multiple response is the guard simulator, and that returns a deny response if the closed loop control name passed in is “denyGuard” 

Using the Simulators
--------------------

To check the status of the simulators, run the command: "*features status*".  If the feature controlloop-utils is enabled, the simulators are being used, otherwise, they are not.

**Turning on the simulators**

    - First, make sure the controller is off by running the command “*policy stop*”. 
    - Then turn the feature on with the command “*features enable controlloop-utils*”.  
    - Finally restart the controller by running “*policy start*”.  
    - Run “*features status*” again and the *feature controlloop-utils* will be **enabled**.

**Turning the simulators off**

    - First, make sure the controller is off by running the command “*policy stop*”. 
    - Then turn the feature off with the command “*features disable controlloop-utils*”.
    - Finally restart the controller by running “*policy start*”.  
    - Run “*features status*” again and the *feature controlloop-utils* will be **disabled**.

**For Junits**

    For Junits, the package *org.onap.policy.simulators* is neeeded.  In the Util class, there are four methods to start the four different simulators: *buildAaiSim()*, *buildSoSim()*, *buildVfcSim()*, and *buildGuardSim()*.  Once the method is called, the simulator should be up and waiting to respond to requests.  To bring down the simulators, call *HttpServletServer.factory.destroy()*.

Responses
---------

**A&AI**

    .. code-block:: bash
       :caption: vnf-GET-response

        {
         "vnf-id": vnfId, //vnfId will be the vnfId you query on.  If you query on a vnfName, the id will be "error" if the name is "error", "5e49ca06-2972-4532-9ed4-6d071588d792" otherwise
         "vnf-name": vnfName, //vnfName will be the vnfName you query on.  If you query on a vnfId, the name will be "USUCP0PCOIL0110UJRT01"
         "vnf-type": "RT",
         "service-id": "d7bb0a21-66f2-4e6d-87d9-9ef3ced63ae4",
         "equipment-role": "UCPE",
         "orchestration-status": "created",
         "management-option": "ATT",
         "ipv4-oam-address": "32.40.68.35",
         "ipv4-loopback0-address": "32.40.64.57",
         "nm-lan-v6-address": "2001:1890:e00e:fffe::1345",
         "management-v6-address": "2001:1890:e00e:fffd::36",
         "in-maint": false,
         "is-closed-loop-disabled": isDisabled, //isDisabled will be true if the vnf name/Id you query on is disableClosedLoop, false otherwise
         "resource-version": "1493389458092",
        
         "relationship-list": {
          "relationship": [{
           "related-to": "service-instance",
           "related-link": "/aai/v11/business/customers/customer/1610_Func_Global_20160817084727/service-subscriptions/service-subscription/uCPE-VMS/service-instances/service-instance/USUCP0PCOIL0110UJZZ01",
           "relationship-data": [{
            "relationship-key": "customer.global-customer-id",
            "relationship-value": "1610_Func_Global_20160817084727"
           }, {
            "relationship-key": "service-subscription.service-type",
            "relationship-value": "uCPE-VMS"
           }, {
            "relationship-key": "service-instance.service-instance-id",
            "relationship-value": "USUCP0PCOIL0110UJZZ01"
           }],
           "related-to-property": [{
            "property-key": "service-instance.service-instance-name"
           }]
          }, {
           "related-to": "vserver",
           "related-link": "/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/att-aic/AAIAIC25/tenants/tenant/USUCP0PCOIL0110UJZZ01%3A%3AuCPE-VMS/vservers/vserver/3b2558f4-39d8-40e7-bfc7-30660fb52c45",
           "relationship-data": [{
            "relationship-key": "cloud-region.cloud-owner",
            "relationship-value": "att-aic"
           }, {
            "relationship-key": "cloud-region.cloud-region-id",
            "relationship-value": "AAIAIC25"
           }, {
            "relationship-key": "tenant.tenant-id",
            "relationship-value": "USUCP0PCOIL0110UJZZ01::uCPE-VMS"
           }, {
            "relationship-key": "vserver.vserver-id",
            "relationship-value": "3b2558f4-39d8-40e7-bfc7-30660fb52c45"
           }],
           "related-to-property": [{
            "property-key": "vserver.vserver-name",
            "property-value": "USUCP0PCOIL0110UJZZ01-vsrx"
           }]
          }]
         }


    .. code-block:: bash
       :caption: vnf-GET-fail

        //This is returned if you query on the value "getFail"
        {
         "requestError": {
          "serviceException": {
           "messageId": "SVC3001",
           "text": "Resource not found for %1 using id %2 (msg=%3) (ec=%4)",
           "variables": ["GET", "network/generic-vnfs/generic-vnf/getFail", "Node Not Found:No Node of type generic-vnf found at network/generic-vnfs/generic-vnf/getFail", "ERR.5.4.6114"]
          }
         }
        }


    .. code-block:: bash
       :caption: vserver-GET-response

        {
         "vserver": [{
          "vserver-id": "d0668d4f-c25e-4a1b-87c4-83845c01efd8",
          "vserver-name": vserverName, // The value you query on
          "vserver-name2": "vjunos0",
          "vserver-selflink": "https://aai-ext1.test.att.com:8443/aai/v7/cloud-infrastructure/cloud-regions/cloud-region/att-aic/AAIAIC25/tenants/tenant/USMSO1SX7NJ0103UJZZ01%3A%3AuCPE-VMS/vservers/vserver/d0668d4f-c25e-4a1b-87c4-83845c01efd8",
          "in-maint": false,
          "is-closed-loop-disabled": isDisabled, // True if the vserverName is "disableClosedLoop", false otherwise
          "resource-version": "1494001931513",
          "relationship-list": {
           "relationship": [{
            "related-to": "generic-vnf",
            "related-link": "/aai/v11/network/generic-vnfs/generic-vnf/e1a41e99-4ede-409a-8f9d-b5e12984203a",
            "relationship-data": [{
             "relationship-key": "generic-vnf.vnf-id",
             "relationship-value": "e1a41e99-4ede-409a-8f9d-b5e12984203a"
            }],
            "related-to-property": [{
             "property-key": "generic-vnf.vnf-name",
             "property-value": "USMSO1SX7NJ0103UJSW01"
            }]
           }, {
            "related-to": "pserver",
            "related-link": "/aai/v11/cloud-infrastructure/pservers/pserver/USMSO1SX7NJ0103UJZZ01",
            "relationship-data": [{
             "relationship-key": "pserver.hostname",
             "relationship-value": "USMSO1SX7NJ0103UJZZ01"
            }],
            "related-to-property": [{
             "property-key": "pserver.pserver-name2"
            }]
           }]
          }
         }]
        }


    .. code-block:: bash
       :caption: vserver-GET-error

        //This is returned if you query on the value "getFail"
        {
         "requestError": {
          "serviceException": {
           "messageId": "SVC3001",
           "text": "Resource not found for %1 using id %2 (msg=%3) (ec=%4)",
           "variables": ["GET", "nodes/vservers", "Node Not Found:No Node of type generic-vnf found at nodes/vservers", "ERR.5.4.6114"]
          }
         }
        }


    .. code-block:: bash
       :caption: vnf-NamedQuery-response

        {
         "inventory-response-item": [{
          "model-name": "service-instance",
          "generic-vnf": {
           "vnf-id": vnfID, // This will be the vnfID you query on
           "vnf-name": "ZRDM2MMEX39",
           "vnf-type": "vMME Svc Jul 14/vMME VF Jul 14 1",
           "service-id": "a9a77d5a-123e-4ca2-9eb9-0b015d2ee0fb",
           "orchestration-status": "active",
           "in-maint": false,
           "is-closed-loop-disabled": false,
           "resource-version": "1503082370097",
           "model-invariant-id": "82194af1-3c2c-485a-8f44-420e22a9eaa4",
           "model-version-id": "46b92144-923a-4d20-b85a-3cbd847668a9"
          },
          "extra-properties": {},
          "inventory-response-items": {
           "inventory-response-item": [{
            "model-name": "service-instance",
            "service-instance": {
             "service-instance-id": "37b8cdb7-94eb-468f-a0c2-4e3c3546578e",
             "service-instance-name": "Changed Service Instance NAME",
             "model-invariant-id": "82194af1-3c2c-485a-8f44-420e22a9eaa4",
             "model-version-id": "46b92144-923a-4d20-b85a-3cbd847668a9",
             "resource-version": "1503082993532",
             "orchestration-status": "Active"
            },
            "extra-properties": {},
            "inventory-response-items": {
             "inventory-response-item": [{
              "model-name": "pnf",
              "generic-vnf": {
               "vnf-id": "jimmy-test",
               "vnf-name": "jimmy-test-vnf",
               "vnf-type": "vMME Svc Jul 14/vMME VF Jul 14 1",
               "service-id": "a9a77d5a-123e-4ca2-9eb9-0b015d2ee0fb",
               "orchestration-status": "active",
               "in-maint": false,
               "is-closed-loop-disabled": false,
               "resource-version": "1504013830207",
               "model-invariant-id": "862b25a1-262a-4961-bdaa-cdc55d69785a",
               "model-version-id": "e9f1fa7d-c839-418a-9601-03dc0d2ad687"
              },
              "extra-properties": {}
             }, {
              "model-name": "service-instance",
              "generic-vnf": {
               "vnf-id": "jimmy-test-vnf2",
               "vnf-name": "jimmy-test-vnf2-named",
               "vnf-type": "vMME Svc Jul 14/vMME VF Jul 14 1",
               "service-id": "a9a77d5a-123e-4ca2-9eb9-0b015d2ee0fb",
               "orchestration-status": "active",
               "in-maint": false,
               "is-closed-loop-disabled": false,
               "resource-version": "1504014833841",
               "model-invariant-id": "Eace933104d443b496b8.nodes.heat.vpg",
               "model-version-id": "46b92144-923a-4d20-b85a-3cbd847668a9"
              },
              "extra-properties": {}
             }]
            }
           }]
          }
         }]
        }


    .. code-block:: bash
       :caption: vserver-NamedQuery-response

        {
         "inventory-response-item": [{
          "extra-properties": {},
          "inventory-response-items": {
           "inventory-response-item": [{
            "extra-properties": {
             "extra-property": [{
              "property-name": "model-ver.model-version-id",
              "property-value": "93a6166f-b3d5-4f06-b4ba-aed48d009ad9"
             }, {
              "property-name": "model-ver.model-name",
              "property-value": "generic-vnf"
             }, {
              "property-name": "model.model-type",
              "property-value": "widget"
             }, {
              "property-name": "model.model-invariant-id",
              "property-value": "acc6edd8-a8d4-4b93-afaa-0994068be14c"
             }, {
              "property-name": "model-ver.model-version",
              "property-value": "1.0"
             }]
            },
            "generic-vnf": {
             "in-maint": false,
             "is-closed-loop-disabled": false,
             "model-invariant-id": "acc6edd8-a8d4-4b93-afaa-0994068be14c",
             "model-version-id": "93a6166f-b3d5-4f06-b4ba-aed48d009ad9",
             "orchestration-status": "Created",
             "resource-version": "1507826325834",
             "service-id": "b3f70641-bdb9-4030-825e-6abb73a1f929",
             "vnf-id": "594e2fe0-48b8-41ff-82e2-3d4bab69b192",
             "vnf-name": "Vnf_Ete_Named90e1ab3-dcd5-4877-9edb-eadfc84e32c8",
             "vnf-type": "8330e932-2a23-4943-8606/c15ce9e1-e914-4c8f-b8bb 1"
            },
            "inventory-response-items": {
             "inventory-response-item": [{
              "extra-properties": {
               "extra-property": [{
                "property-name": "model-ver.model-version-id",
                "property-value": "46b92144-923a-4d20-b85a-3cbd847668a9"
               }, {
                "property-name": "model-ver.model-name",
                "property-value": "service-instance"
               }, {
                "property-name": "model.model-type",
                "property-value": "widget"
               }, {
                "property-name": "model.model-invariant-id",
                "property-value": "82194af1-3c2c-485a-8f44-420e22a9eaa4"
               }, {
                "property-name": "model-ver.model-version",
                "property-value": "1.0"
               }]
              },
              "model-name": "service-instance",
              "service-instance": {
               "model-invariant-id": "82194af1-3c2c-485a-8f44-420e22a9eaa4",
               "model-version-id": "46b92144-923a-4d20-b85a-3cbd847668a9",
               "resource-version": "1507827626200",
               "service-instance-id": "cf8426a6-0b53-4e3d-bfa6-4b2f4d5913a5",
               "service-instance-name": "Service_Ete_Named90e1ab3-dcd5-4877-9edb-eadfc84e32c8"
              }
             }, {
              "extra-properties": {
               "extra-property": [{
                "property-name": "model-ver.model-version-id",
                "property-value": "93a6166f-b3d5-4f06-b4ba-aed48d009ad9"
               }, {
                "property-name": "model-ver.model-name",
                "property-value": "generic-vnf"
               }, {
                "property-name": "model.model-type",
                "property-value": "widget"
               }, {
                "property-name": "model.model-invariant-id",
                "property-value": "acc6edd8-a8d4-4b93-afaa-0994068be14c"
               }, {
                "property-name": "model-ver.model-version",
                "property-value": "1.0"
               }]
              },
              "model-name": "generic-vnf",
              "vf-module": {
               "heat-stack-id": "Vfmodule_Ete_Named90e1ab3-dcd5-4877-9edb-eadfc84e32c8/5845f37b-6cda-4e91-8ca3-f5572d226488",
               "is-base-vf-module": true,
               "model-invariant-id": "acc6edd8-a8d4-4b93-afaa-0994068be14c",
               "model-version-id": "93a6166f-b3d5-4f06-b4ba-aed48d009ad9",
               "orchestration-status": "active",
               "resource-version": "1507826326804",
               "vf-module-id": "b0eff878-e2e1-4947-9597-39afdd0f51dd",
               "vf-module-name": "Vfmodule_Ete_Named90e1ab3-dcd5-4877-9edb-eadfc84e32c8"
              }
             }]
            },
            "model-name": "generic-vnf"
           }, {
            "extra-properties": {},
            "inventory-response-items": {
             "inventory-response-item": [{
              "cloud-region": {
               "cloud-owner": "Rackspace",
               "cloud-region-id": "DFW",
               "cloud-region-version": "v1",
               "cloud-type": "SharedNode",
               "cloud-zone": "CloudZone",
               "owner-defined-type": "OwnerType",
               "resource-version": "1507828410019",
               "sriov-automation": false
              },
              "extra-properties": {}
             }]
            },
            "tenant": {
             "resource-version": "1507828410764",
             "tenant-id": "1015548",
             "tenant-name": "1015548"
            }
           }]
          },
          "vserver": {
           "in-maint": false,
           "is-closed-loop-disabled": false,
           "prov-status": "ACTIVE",
           "resource-version": "1507828410832",
           "vserver-id": "70f081eb-2a87-4c81-9296-4b93d7d145c6",
           "vserver-name": "vlb-lb-32c8",
           "vserver-name2": "vlb-lb-32c8",
           "vserver-selflink": "https://aai.api.simpledemo.openecomp.org:8443/aai/v11/nodes/vservers?vserver-name=vlb-lb-32c8"
          }
         }]
        }


    .. code-block:: bash
       :caption: NamedQuery-error

        // This is returned if you query the value "error"
        {
         "requestError": {
          "serviceException": {
           "messageId": "SVC3001",
           "text": "Resource not found for %1 using id %2 (msg=%3) (ec=%4)",
           "variables": ["POST Search", "getNamedQueryResponse", "Node Not Found:No Node of type generic-vnf found for properties", "ERR.5.4.6114"]
          }
         }
        }


**SO**

    .. code-block:: bash
       :caption: SO-response

        {
         "requestReferences": {
          "instanceId": "ff305d54-75b4-ff1b-bdb2-eb6b9e5460ff",
          "requestId": "rq1234d1-5a33-ffdf-23ab-12abad84e331"
         }
        }


**vFC**

    .. code-block:: bash
       :caption: vFC-POST-response

        {
         "jobId": "1"
        }


    .. code-block:: bash
       :caption: vFC-GET-response

        {
         "jobId": jobId, //The jod id that you query
         "responseDescriptor": {
          "progress": "40",
          "status": "finished",
          "statusDescription": "OMC VMs are decommissioned in VIM",
          "errorCode": null,
          "responseId": 101,
          "responseHistoryList": [{
           "progress": "40",
           "status": "proccessing",
           "statusDescription": "OMC VMs are decommissioned in VIM",
           "errorCode": null,
           "responseId": "1"
          }, {
           "progress": "41",
           "status": "proccessing",
           "statusDescription": "OMC VMs are decommissioned in VIM",
           "errorCode": null,
           "responseId": "2"
          }]
         }
        }


**GUARD**

    .. code-block:: bash
       :caption: permit-response

        {
         "decision": "PERMIT",
         "details": "Decision Permit. OK!"
        }


    .. code-block:: bash
       :caption: deny-response

        //This is returned if the closed loop name is denyGuard
        {
         "decision": "DENY",
         "details": "Decision Deny. You asked for it"
        }


End of Document

.. SSNote: Wiki page ref.  https://wiki.onap.org/pages/viewpage.action?pageId=16003633

