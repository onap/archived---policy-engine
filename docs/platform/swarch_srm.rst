
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*****************************************
Scalability, Resiliency and Manageability 
*****************************************

.. contents::
    :depth: 3

The new Beijing release scalability, resiliency, and manageablity are described here.   These capabilities apply to the OOM/Kubernetes installation.

Installation
^^^^^^^^^^^^
Follow the OOM installation instructions at http://onap.readthedocs.io/en/latest/submodules/oom.git/docs/index.html

Overview of the running system
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Upon initialization, you should see pools of 4 PDP-Ds and 2 PDP-Xs:

.. code-block:: bash
   :caption: verify pods

    kubectl get pods --all-namespaces -o=wid
     
    onap    dev-brmsgw-5dbc4c8dc4-llk5s        1/1       Running   0     18m     10.42.120.43    k8sx
    onap    dev-drools-0                       1/1       Running   0     18m     10.42.60.27     k8sx
    onap    dev-drools-1                       1/1       Running   0     16m     10.42.105.190   k8sx
    onap    dev-drools-2                       1/1       Running   0     15m     10.42.139.82    k8sx
    onap    dev-drools-3                       1/1       Running   0     15m     10.42.128.4     k8sx
    onap    dev-nexus-7d96568f5f-qp5td         1/1       Running   0     18m     10.42.172.8     k8sx
    onap    dev-pap-8587696769-vwj6k           2/2       Running   0     18m     10.42.19.137    k8sx
    onap    dev-pdp-0                          2/2       Running   0     18m     10.42.144.218   k8sx
    onap    dev-pdp-1                          2/2       Running   0     15m     10.42.233.111   k8sx
    onap    dev-policydb-587d55bdff-4f5dz      1/1       Running   0     18m     10.42.12.242    k8sx


and a service for every component:

.. code-block:: bash
   :caption: verify services

    kubectl get services --all-namespaces
     
    onap    brmsgw         NodePort    10.43.209.173   <none>     9989:30216/TCP                  24m
    onap    drools         NodePort    10.43.27.92     <none>     6969:30217/TCP,9696:30221/TCP   24m
    onap    nexus          NodePort    10.43.19.171    <none>     8081:30236/TCP                  24m
    onap    pap            NodePort    10.43.9.166     <none>     8443:30219/TCP,9091:30218/TCP   24m
    onap    pdp            ClusterIP   None            <none>     8081/TCP                        24m
    onap    policydb       ClusterIP   None            <none>     3306/TCP                        24m

Config and Decision policy requests will be distributed across PDP-Xs through the *pdp* service.    PDP-X clients (such as DCAE) should configure their URLs to go through the *pdp* service.   Their requests will be distributed across the available PDP-X replicas.    The PDP-Xs can be also accessed individually (dev-pdp-0 and dev-pdp-1 above), but is preferable to that external clients use the service.

PDP-Ds are also accessible on a group fashion by using the service IP.   Nevertheless, as DMaaP is the main means of communication with other ONAP components, the service interface is not used heavily.


Healthchecks
^^^^^^^^^^^^

Verify that the policy healtcheck passes by the robot framework:

.. code-block:: bash
   :caption: ~/oom/kubernetes/robot/ete-k8s.sh onap health 2> /dev/null | grep PASS

    Basic Policy Health Check                                             | PASS |

A policy healthcheck (with more detailed output) can be done directly to the drools service in the policy VM.

.. code-block:: none
   :caption: Healtcheck on the PDP-D service

    curl --silent --user '<username>:<password> -X GET http://localhost:30217/healthcheck | python -m json.tool
     
    {
        "details": [
            {
                "code": 200,
                "healthy": true,
                "message": "alive",
                "name": "PDP-D",
                "url": "self"
            },
            {
                "code": 200,
                "healthy": true,
                "message": "",
                "name": "PAP",
                "url": "http://pap:9091/pap/test"
            },
            {
                "code": 200,
                "healthy": true,
                "message": "",
                "name": "PDP",
                "url": "http://pdp:8081/pdp/test"
            }
        ],
        "healthy": true
    }


PDP-X active/active pool
^^^^^^^^^^^^^^^^^^^^^^^^

The policy engine UI (console container in the pap pod) can be used to check that the the 2 individual PDP-Xs are synchronized.
The console URL is accessible at  ``http://<oom-vm>:30219/onap/login.htm``.   Select the PDP tab.

    .. image:: srmPdpxPdpMgmt.png

After initialization, there will be no policies loaded into the policy subsystem.    You can verify it, by accessing the Editor tab in the UI.


PDP-D Active/Active Pool
^^^^^^^^^^^^^^^^^^^^^^^^

The PDP-Ds replicas will come up with the amsterdam controller installed in brainless mode (no maven coordinates) since the controller has not been associated with a set of drools rules to run (control loop rules).

The following command can be issued on each of the PDP-D replicas IPs:

.. code-block:: bash
   :caption: Querying the rules association for a PDP-D replica 

    curl --silent --user '<username>:<password>' -X GET http://<drools-replica-ip>:9696/policy/pdp/engine/controllers/amsterdam/drools | python -m json.tool
    
    {
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
    }

Installing Policies
^^^^^^^^^^^^^^^^^^^

The OOM default installation will come with no policies pre-configured.  There is a sample script used by integration teams to load policies to support all 4 use cases at:   /tmp/policy-install/config/push-policies.sh in the pap container within the pap pod.   This script can be modified for your own particular installation, for example if only interested in vCPE use cases, remove those vCPE related API REST calls.   For the vFW use case, you may want to edit the encoded operational policy to point to the proper resourceID in your installation.

The above mentioned push-policies.sh script can be executed as follows:

.. code-block:: bash
   :caption: Installing the default policies

    kubectl exec -it dev-pap-8587696769-vwj6k -c pap -n onap -- bash -c "export PRELOAD_POLICIES=true; /tmp/policy-install/config/push-policies.sh"
     
     
    ..
    Create BRMSParam Operational Policies
    ..
    Create BRMSParamvFirewall Policy
    ..
    Transaction ID: ef08cc65-9950-4478-a4ab-0f3bc2519f60 --Policy with the name com.Config_BRMS_Param_BRMSParamvFirewall.1.xml was successfully created.Create BRMSParamvDNS Policy
    ..
    Transaction ID: 52e33efe-ba66-47de-b404-8d441107d8a9 --Policy with the name com.Config_BRMS_Param_BRMSParamvDNS.1.xml was successfully created.Create BRMSParamVOLTE Policy
    ..
    Transaction ID: f13072b7-6258-4c16-99da-f908d29363ec --Policy with the name com.Config_BRMS_Param_BRMSParamVOLTE.1.xml was successfully created.Create BRMSParamvCPE Policy
    ..
    Transaction ID: 616f970a-b45e-40f7-88cd-d63000d22cca --Policy with the name com.Config_BRMS_Param_BRMSParamvCPE.1.xml was successfully created.Create MicroService Config Policies
    Create MicroServicevFirewall Policy
    ..
    Transaction ID: 4c143a15-20af-408a-9285-bc7940261829 --Policy with the name com.Config_MS_MicroServicevFirewall.1.xml was successfully created.Create MicroServicevDNS Policy
    ..
    Transaction ID: 1e54ae73-509b-490e-bf62-1fea7989fd5f --Policy with the name com.Config_MS_MicroServicevDNS.1.xml was successfully created.Create MicroServicevCPE Policy
    ..
    Transaction ID: 32239868-bab2-4e12-9fd9-81a0ed4a6b1c --Policy with the name com.Config_MS_MicroServicevCPE.1.xml was successfully created.Creating Decision Guard policy
    ..
    Transaction ID: b43cb9d5-42c7-4654-aacf-d4898c4d13bb --Policy with the name com.Decision_AllPermitGuard.1.xml was successfully created.Push Decision policy
    ..
    Transaction ID: 3c1e4ae6-6991-415b-9f2d-c665a8c5a026 --Policy 'com.Decision_AllPermitGuard.1.xml' was successfully pushed to the PDP group 'default'.Pushing BRMSParam Operational policies
    ..
    Transaction ID: 58d26d03-b5b8-4fd3-b2df-1411a1c36420 --Policy 'com.Config_BRMS_Param_BRMSParamvFirewall.1.xml' was successfully pushed to the PDP group 'default'.pushPolicy : PUT : com.BRMSParamvDNS
    ..
    Transaction ID: 0854e54a-504b-4f06-bc2f-30f491cb9f5a --Policy 'com.Config_BRMS_Param_BRMSParamvDNS.1.xml' was successfully pushed to the PDP group 'default'.pushPolicy : PUT : com.BRMSParamVOLTE
    ..
    Transaction ID: d33c7dde-5c99-4dab-b4ff-9988473cd88d --Policy 'com.Config_BRMS_Param_BRMSParamVOLTE.1.xml' was successfully pushed to the PDP group 'default'.pushPolicy : PUT : com.BRMSParamvCPE
    ..
    Transaction ID: e8c8a73e-127c-4318-9e59-3cae9dcbe011 --Policy 'com.Config_BRMS_Param_BRMSParamvCPE.1.xml' was successfully pushed to the PDP group 'default'.Pushing MicroService Config policies
    ..
    Transaction ID: ec0429d7-e35f-4978-8a6c-40d2b5b3be61 --Policy 'com.Config_MS_MicroServicevFirewall.1.xml' was successfully pushed to the PDP group 'default'.pushPolicy : PUT : com.MicroServicevDNS
    ..
    Transaction ID: f7072f05-7b74-45b5-9bd3-99b7f8023e3e --Policy 'com.Config_MS_MicroServicevDNS.1.xml' was successfully pushed to the PDP group 'default'.pushPolicy : PUT : com.MicroServicevCPE
    ..
    Transaction ID: 6d47db63-7956-4f5f-ab34-aeb5a124a90d --Policy 'com.Config_MS_MicroServicevCPE.1.xml' was successfully pushed to the PDP group 'default'.


The policies pushed can be viewed through the Policy UI:

    .. image:: srmEditor.png

As a consequence of pushing the policies, the brmsgw component will compose drools rules artifacts and publish them to the nexus respository at ``http://<oom-vm>:30236/nexus/``

    .. image:: srmNexus.png

At the same time each replica of the PDP-Ds will receive notifications for each new version of the policies to run for the amsterdam controller.   You can run the following command to see how the amsterdam controller is associated with the latest rules version.    The following command can be used for verification for each replica:


.. code-block:: none
   :caption: Querying the rules association of a PDP-D replica

    curl --silent --user '<username><password> -X GET http://<replica-ip>:9696/policy/pdp/engine/controllers/amsterdam/drools | python -m json.tool
    {
        "alive": true,
        "artifactId": "policy-amsterdam-rules",
        "brained": true,
        "groupId": "org.onap.policy-engine.drools.amsterdam",
        "locked": false,
        "modelClassLoaderHash": 1223551265,
        "recentSinkEvents": [],
        "recentSourceEvents": [],
        "sessionCoordinates": [
            "org.onap.policy-engine.drools.amsterdam:policy-amsterdam-rules:0.4.0:closedloop-amsterdam"
        ],
        "sessions": [
            "closedloop-amsterdam"
        ],
        "version": "0.4.0"
    }

Likewise, for verification purposes, each PDP-X replica can be queried directly to retrieve policy information.   The following commands can be used to query a policy through the pdp service:


.. code-block:: bash
   :caption: Querying the "pdp" service for the vFirewal policy

    ubuntu@k8sx:~$ kubectl exec -it dev-pap-8587696769-vwj6k -c pap -n onap bash
    policy@dev-pap-8587696769-vwj6k:/tmp/policy-install$ curl --silent -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'ClientAuth: cHl0aG9uOnRlc3Q=' --header 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' --header 'Environment: TEST' -d '{"policyName": ".*vFirewall.*"}' http://pdp:8081/pdp/api/getConfig | python -m json.tool
    [
        {
            "config": "{\"service\":\"tca_policy\",\"location\":\"SampleServiceLocation\",\"uuid\":\"test\",\"policyName\":\"MicroServicevFirewall\",\"description\":\"MicroService vFirewall Policy\",\"configName\":\"SampleConfigName\",\"templateVersion\":\"OpenSource.version.1\",\"version\":\"1.1.0\",\"priority\":\"1\",\"policyScope\":\"resource=SampleResource,service=SampleService,type=SampleType,closedLoopControlName=ControlLoop-vFirewall-d0a1dfc6-94f5-4fd4-a5b5-4630b438850a\",\"riskType\":\"SampleRiskType\",\"riskLevel\":\"1\",\"guard\":\"False\",\"content\":{\"tca_policy\":{\"domain\":\"measurementsForVfScaling\",\"metricsPerEventName\":[{\"eventName\":\"vFirewallBroadcastPackets\",\"controlLoopSchemaType\":\"VNF\",\"policyScope\":\"DCAE\",\"policyName\":\"DCAE.Config_tca-hi-lo\",\"policyVersion\":\"v0.0.1\",\"thresholds\":[{\"closedLoopControlName\":\"ControlLoop-vFirewall-d0a1dfc6-94f5-4fd4-a5b5-4630b438850a\",\"version\":\"1.0.2\",\"fieldPath\":\"$.event.measurementsForVfScalingFields.vNicUsageArray[*].receivedTotalPacketsDelta\",\"thresholdValue\":300,\"direction\":\"LESS_OR_EQUAL\",\"severity\":\"MAJOR\",\"closedLoopEventStatus\":\"ONSET\"},{\"closedLoopControlName\":\"ControlLoop-vFirewall-d0a1dfc6-94f5-4fd4-a5b5-4630b438850a\",\"version\":\"1.0.2\",\"fieldPath\":\"$.event.measurementsForVfScalingFields.vNicUsageArray[*].receivedTotalPacketsDelta\",\"thresholdValue\":700,\"direction\":\"GREATER_OR_EQUAL\",\"severity\":\"CRITICAL\",\"closedLoopEventStatus\":\"ONSET\"}]}]}}}",
            "matchingConditions": {
                "ConfigName": "SampleConfigName",
                "Location": "SampleServiceLocation",
                "ONAPName": "DCAE",
                "service": "tca_policy",
                "uuid": "test"
            },
            "policyConfigMessage": "Config Retrieved! ",
            "policyConfigStatus": "CONFIG_RETRIEVED",
            "policyName": "com.Config_MS_MicroServicevFirewall.1.xml",
            "policyType": "MicroService",
            "policyVersion": "1",
            "property": null,
            "responseAttributes": {},
            "type": "JSON"
        },
        {
            "config":  ..... 
            "matchingConditions": {
                "ConfigName": "BRMS_PARAM_RULE",
                "ONAPName": "DROOLS"
            },
            "policyConfigMessage": "Config Retrieved! ",
            "policyConfigStatus": "CONFIG_RETRIEVED",
            "policyName": "com.Config_BRMS_Param_BRMSParamvFirewall.1.xml",
            "policyType": "BRMS_PARAM",
            "policyVersion": "1",
            "property": null,
            "responseAttributes": {
                "controller": "amsterdam"
            },
            "type": "OTHER"
        }
    ]
    

while the following commands could be used to query an specific PDP-X replica:


.. code-block:: bash
   :caption: Querying PDP-X 0 for the vCPE policy

    curl --silent -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'ClientAuth: cHl0aG9uOnRlc3Q=' --header 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' --header 'Environment: TEST' -d '{"policyName": ".*vCPE.*"}' http://10.42.144.218:8081/pdp/api/getConfig | python -m json.tool
    [
        {
            "config": ...,
            "matchingConditions": {
                "ConfigName": "BRMS_PARAM_RULE",
                "ONAPName": "DROOLS"
            },
            "policyConfigMessage": "Config Retrieved! ",
            "policyConfigStatus": "CONFIG_RETRIEVED",
            "policyName": "com.Config_BRMS_Param_BRMSParamvCPE.1.xml",
            "policyType": "BRMS_PARAM",
            "policyVersion": "1",
            "property": null,
            "responseAttributes": {
                "controller": "amsterdam"
            },
            "type": "OTHER"
        },
        {
            "config": "{\"service\":\"tca_policy\",\"location\":\"SampleServiceLocation\",\"uuid\":\"test\",\"policyName\":\"MicroServicevCPE\",\"description\":\"MicroService vCPE Policy\",\"configName\":\"SampleConfigName\",\"templateVersion\":\"OpenSource.version.1\",\"version\":\"1.1.0\",\"priority\":\"1\",\"policyScope\":\"resource=SampleResource,service=SampleService,type=SampleType,closedLoopControlName=ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e\",\"riskType\":\"SampleRiskType\",\"riskLevel\":\"1\",\"guard\":\"False\",\"content\":{\"tca_policy\":{\"domain\":\"measurementsForVfScaling\",\"metricsPerEventName\":[{\"eventName\":\"Measurement_vGMUX\",\"controlLoopSchemaType\":\"VNF\",\"policyScope\":\"DCAE\",\"policyName\":\"DCAE.Config_tca-hi-lo\",\"policyVersion\":\"v0.0.1\",\"thresholds\":[{\"closedLoopControlName\":\"ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e\",\"version\":\"1.0.2\",\"fieldPath\":\"$.event.measurementsForVfScalingFields.additionalMeasurements[*].arrayOfFields[0].value\",\"thresholdValue\":0,\"direction\":\"EQUAL\",\"severity\":\"MAJOR\",\"closedLoopEventStatus\":\"ABATED\"},{\"closedLoopControlName\":\"ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e\",\"version\":\"1.0.2\",\"fieldPath\":\"$.event.measurementsForVfScalingFields.additionalMeasurements[*].arrayOfFields[0].value\",\"thresholdValue\":0,\"direction\":\"GREATER\",\"severity\":\"CRITICAL\",\"closedLoopEventStatus\":\"ONSET\"}]}]}}}",
            "matchingConditions": {
                "ConfigName": "SampleConfigName",
                "Location": "SampleServiceLocation",
                "ONAPName": "DCAE",
                "service": "tca_policy",
                "uuid": "test"
            },
            "policyConfigMessage": "Config Retrieved! ",
            "policyConfigStatus": "CONFIG_RETRIEVED",
            "policyName": "com.Config_MS_MicroServicevCPE.1.xml",
            "policyType": "MicroService",
            "policyVersion": "1",
            "property": null,
            "responseAttributes": {},
            "type": "JSON"
        }
    ]
    
PDP-X Resiliency
^^^^^^^^^^^^^^^^

A PDP-X container failure can be simulated by performing a"policy.sh stop" operation within the PDP-X container, this in fact will shutdown the PDP-X service.    The kubernetes liveness operation will detect that the ports are down, inferring there's a problem with the service, and in turn, will restart the container.   In the following example will cause PDP-X 1 to fail.

.. code-block:: bash
   :caption: Causing PDP-X 1 service to fail

    ubuntu@k8sx:~$ kubectl exec -it dev-pdp-1 --container pdp -n onap -- bash -c "source /opt/app/policy/etc/profile.d/env.sh; policy.sh stop;"
        pdplp: STOPPING ..
        pdp: STOPPING ..

Upon detection of the service being down through the liveness check, the container will be restarted.   Note the restart count when querying the status of the pods:

.. code-block:: bash
   :caption: Checking PDP-X 1 restart count

    ubuntu@k8sx:~$ kubectl get pods --all-namespaces -o=wide
     
    NAMESPACE  NAME                             READY     STATUS    RESTARTS   AGE     IP              NODE

    onap       dev-brmsgw-5dbc4c8dc4-llk5s      1/1       Running   0          3d      10.42.120.43    k8sx
    onap       dev-drools-0                     1/1       Running   0          3d      10.42.60.27     k8sx
    onap       dev-drools-1                     1/1       Running   0          3d      10.42.105.190   k8sx
    onap       dev-drools-2                     1/1       Running   0          3d      10.42.139.82    k8sx
    onap       dev-drools-3                     1/1       Running   0          3d      10.42.128.4     k8sx
    onap       dev-nexus-7d96568f5f-qp5td       1/1       Running   0          3d      10.42.172.8     k8sx
    onap       dev-pap-8587696769-vwj6k         2/2       Running   0          3d      10.42.19.137    k8sx
    onap       dev-pdp-0                        2/2       Running   0          3d      10.42.144.218   k8sx
    onap       dev-pdp-1                        2/2       Running   1          3d      10.42.233.111   k8sx    <--- **
    onap       dev-policydb-587d55bdff-4f5dz    1/1       Running   0          3d      10.42.12.242    k8sx
    

During the restart process, the PAP component, will detect that PDP-X 1 is down and therefore its state being reflected in the PDP-X screen:

    .. image:: srmPdpxResiliencyPdpMgmt1.png

This screen will be updated to reflect PDP-X 1 is back alive, after PDP-X 1 synchronizes itself with the PAP. 

    .. image:: srmPdpxResiliencyPdpMgmt2.png

At that point, PDP-X is usable either directly or through the service to query for policies.


.. code-block:: bash
   :caption: Query PDP-X 1 for vCPE policy

    ubuntu@k8sx:~$ curl --silent -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'ClientAuth: cHl0aG9uOnRlc3Q=' --header 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' --header 'Environment: TEST' -d '{"policyName": ".*vCPE.*"}' http://10.42.233.111:8081/pdp/api/getConfig | python -m json.tool
    [
        {
            "config": "..",
            "matchingConditions": {
                "ConfigName": "BRMS_PARAM_RULE",
                "ONAPName": "DROOLS"
            },
            "policyConfigMessage": "Config Retrieved! ",
            "policyConfigStatus": "CONFIG_RETRIEVED",
            "policyName": "com.Config_BRMS_Param_BRMSParamvCPE.1.xml",
            "policyType": "BRMS_PARAM",
            "policyVersion": "1",
            "property": null,
            "responseAttributes": {
                "controller": "amsterdam"
            },
            "type": "OTHER"
        },
        {
            "config": "{\"service\":\"tca_policy\",\"location\":\"SampleServiceLocation\",\"uuid\":\"test\",\"policyName\":\"MicroServicevCPE\",\"description\":\"MicroService vCPE Policy\",\"configName\":\"SampleConfigName\",\"templateVersion\":\"OpenSource.version.1\",\"version\":\"1.1.0\",\"priority\":\"1\",\"policyScope\":\"resource=SampleResource,service=SampleService,type=SampleType,closedLoopControlName=ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e\",\"riskType\":\"SampleRiskType\",\"riskLevel\":\"1\",\"guard\":\"False\",\"content\":{\"tca_policy\":{\"domain\":\"measurementsForVfScaling\",\"metricsPerEventName\":[{\"eventName\":\"Measurement_vGMUX\",\"controlLoopSchemaType\":\"VNF\",\"policyScope\":\"DCAE\",\"policyName\":\"DCAE.Config_tca-hi-lo\",\"policyVersion\":\"v0.0.1\",\"thresholds\":[{\"closedLoopControlName\":\"ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e\",\"version\":\"1.0.2\",\"fieldPath\":\"$.event.measurementsForVfScalingFields.additionalMeasurements[*].arrayOfFields[0].value\",\"thresholdValue\":0,\"direction\":\"EQUAL\",\"severity\":\"MAJOR\",\"closedLoopEventStatus\":\"ABATED\"},{\"closedLoopControlName\":\"ControlLoop-vCPE-48f0c2c3-a172-4192-9ae3-052274181b6e\",\"version\":\"1.0.2\",\"fieldPath\":\"$.event.measurementsForVfScalingFields.additionalMeasurements[*].arrayOfFields[0].value\",\"thresholdValue\":0,\"direction\":\"GREATER\",\"severity\":\"CRITICAL\",\"closedLoopEventStatus\":\"ONSET\"}]}]}}}",
            "matchingConditions": {
                "ConfigName": "SampleConfigName",
                "Location": "SampleServiceLocation",
                "ONAPName": "DCAE",
                "service": "tca_policy",
                "uuid": "test"
            },
            "policyConfigMessage": "Config Retrieved! ",
            "policyConfigStatus": "CONFIG_RETRIEVED",
            "policyName": "com.Config_MS_MicroServicevCPE.1.xml",
            "policyType": "MicroService",
            "policyVersion": "1",
            "property": null,
            "responseAttributes": {},
            "type": "JSON"
        }
    ]

PDP-D Resiliency
^^^^^^^^^^^^^^^^

A PDP-D container failure can be simulated by performing a"policy stop" operation within the PDP-D container, this in fact will shutdown the PDP-D service.    The kubernetes liveness operation will detect that the ports are down, inferring there's a problem with the service, and in turn, will restart the container.   In the following example will cause PDP-D 3 to fail.

.. code-block:: bash
   :caption: Causing PDP-D 3 to fail

    ubuntu@k8sx:~/oom/kubernetes$ kubectl exec -it dev-drools-3 --container drools -n onap -- bash -c "source /opt/app/policy/etc/profile.d/env.sh; policy stop"
    [drools-pdp-controllers]
    L []: Stopping Policy Management... Policy Management (pid=3284) is stopping... Policy Management has stopped.


Upon detection of the service being down through the liveness check, the container will be restarted.   Note the restart count when querying the status of the pods:

.. code-block:: bash
   :caption: Checking PDP-D 3 restart count

    ubuntu@k8sx:~/oom/kubernetes$ kubectl get pods --all-namespaces -o=wide
    
    NAMESPACE  NAME                             READY     STATUS    RESTARTS   AGE     IP              NODE

    onap       dev-brmsgw-5549d99466-7989k      1/1       Running   0          1h      10.42.252.245   k8sx
    onap       dev-drools-0                     1/1       Running   0          1h      10.42.30.52     k8sx
    onap       dev-drools-1                     1/1       Running   0          1h      10.42.9.245     k8sx
    onap       dev-drools-2                     1/1       Running   0          1h      10.42.95.0      k8sx
    onap       dev-drools-3                     1/1       Running   1          1h      10.42.224.52    k8sx
    onap       dev-nexus-6558979c95-xlxcc       1/1       Running   0          1h      10.42.142.36    k8sx
    onap       dev-pap-64b67f66b9-lc8vl         2/2       Running   0          1h      10.42.187.255   k8sx
    onap       dev-pdp-0                        2/2       Running   0          1h      10.42.164.57    k8sx
    onap       dev-pdp-1                        2/2       Running   0          1h      10.42.155.145   k8sx
    onap       dev-policydb-7d4b75869-qd8n5     1/1       Running   0          1h      10.42.148.37    k8sx
   

PDP-X Scaling
^^^^^^^^^^^^^

To scale a new PDP-X, set the replica count appropriately.   In our scenario below, we are going to scale the PDP-X with a new replica, PDP-X 2, to have a pool of 3 PDP-X.

.. code-block:: bash
   :caption: Scaling a PDP-X

    helm upgrade -i dev local/onap --namespace onap --set policy.pdp.replicaCount=3
     
    Release "dev" has been upgraded. Happy Helming!
    LAST DEPLOYED: Mon May 14 01:37:03 2018
    NAMESPACE: onap
    STATUS: DEPLOYED
    ..
     
    kubectl get pods --all-namespaces -o=wide
     
    NAMESPACE  NAME                             READY     STATUS    RESTARTS   AGE     IP              NODE
    ..
    onap       dev-pdp-0                        2/2       Running   0          1h      10.42.164.57    k8sx
    onap       dev-pdp-1                        2/2       Running   0          1h      10.42.155.145   k8sx
    onap       dev-pdp-2                        2/2       Running   0          1m      10.42.47.58     k8sx
    ..


PDP-D Scaling
^^^^^^^^^^^^^

To scale a new PDP-D, set the replica count appropriately.   In our scenario below, we are going to scale the PDP-D service with a new replica, PDP-D 4, to have a pool of 5 PDP-D.

.. code-block:: bash
   :caption: Scaling a PDP-D

    helm upgrade -i dev local/onap --namespace onap --set policy.drools.replicaCount=5
    Release "dev" has been upgraded. Happy Helming!
    LAST DEPLOYED: Mon May 14 01:45:19 2018
    NAMESPACE: onap
    STATUS: DEPLOYED
     
    ubuntu@k8sx:~/oom/kubernetes$ kubectl get pods --all-namespaces -o=wide
    NAMESPACE  NAME                             READY     STATUS    RESTARTS   AGE     IP              NODE
    ..
    onap       dev-drools-0                     1/1       Running   0          1h      10.42.30.52     k8sx
    onap       dev-drools-1                     1/1       Running   0          1h      10.42.9.245     k8sx
    onap       dev-drools-2                     1/1       Running   0          1h      10.42.95.0      k8sx
    onap       dev-drools-3                     1/1       Running   1          1h      10.42.224.52    k8sx
    onap       dev-drools-4                     1/1       Running   0          1m      10.42.237.251   k8sx
    ..
    
        


End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Scalability%2C+Resiliency+and+Manageability


