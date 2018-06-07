
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

*************
Policy on OOM
*************

.. contents::
    :depth: 3

The new Beijing release capabilities for OOM are described here.

Installation
^^^^^^^^^^^^
Follow the OOM installation instructions at http://onap.readthedocs.io/en/latest/submodules/oom.git/docs/index.html

Overview of the running system
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Upon initialization, you should see the following pods, one instance for each Policy component: PAP, PDP-X, BRMSGW, PDP-D, policydb, and nexus.

Note the "-0" suffix for PDP-X and PDP-D components, which will be increased as they are scaled out to improve runtime performance and reliability.

.. code-block:: bash
   :caption: verify pods

    kubectl get pods --all-namespaces -o=wid
     
    onap    dev-brmsgw-5dbc4c8dc4-llk5s        1/1       Running   0     18m     10.42.120.43    k8sx
    onap    dev-drools-0                       1/1       Running   0     18m     10.42.60.27     k8sx
    onap    dev-nexus-7d96568f5f-qp5td         1/1       Running   0     18m     10.42.172.8     k8sx
    onap    dev-pap-8587696769-vwj6k           2/2       Running   0     18m     10.42.19.137    k8sx
    onap    dev-pdp-0                          2/2       Running   0     18m     10.42.144.218   k8sx
    onap    dev-policydb-587d55bdff-4f5dz      1/1       Running   0     18m     10.42.12.242    k8sx


You will also see a service for every component:

.. code-block:: bash
   :caption: verify services

    kubectl get services --all-namespaces
     
    onap    brmsgw         NodePort    10.43.209.173   <none>     9989:30216/TCP                  24m
    onap    drools         NodePort    10.43.27.92     <none>     6969:30217/TCP,9696:30221/TCP   24m
    onap    nexus          NodePort    10.43.19.171    <none>     8081:30236/TCP                  24m
    onap    pap            NodePort    10.43.9.166     <none>     8443:30219/TCP,9091:30218/TCP   24m
    onap    pdp            ClusterIP   None            <none>     8081/TCP                        24m
    onap    policydb       ClusterIP   None            <none>     3306/TCP                        24m

Config and Decision policy requests will be distributed across PDP-Xs through the *pdp* service.    PDP-X clients (such as DCAE) should configure their URLs to go through the *pdp* service.   Their requests will be distributed across the available PDP-X replicas.    
The PDP-Xs can be also accessed individually (dev-pdp-0, or dev-pdp-x if scaled out), but is preferable for PDP-X external clients to interface through the service.

PDP-Ds are also accessible on a group fashion by using the service IP, but DMaaP is the main means of communication with other ONAP components.


Healthchecks
^^^^^^^^^^^^

Verify that the policy healthcheck passes by the robot framework:

.. code-block:: bash
   :caption: robot healthcheck

    ~/oom/kubernetes/robot/ete-k8s.sh onap health 2> /dev/null | grep PASS
    ..
    Basic Policy Health Check                                             | PASS |
    ..


A policy healthcheck (with more detailed output) can be done directly by invoking the drools service in the policy VM.

.. code-block:: none
   :caption: PDP-D service (more detailed) healthcheck

    # Using default credentials for the healtcheck service.
    # To change the default username and passwords for this service,
    # please modify configuration pre-installation at:
    # oom/kubernetes/policy/charts/drools/resources/config/opt/policy/config/drools/keys/feature-healthcheck.conf
     
    curl --silent --user 'healthcheck:zb!XztG34' -X GET http://localhost:30217/healthcheck | python -m json.tool
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
    

PDP-X Active/Active Pool
^^^^^^^^^^^^^^^^^^^^^^^^

The policy engine UI (console container in the pap pod) can be used to check that the PAP and the PDP-Xs are synchronized.
The console URL is accessible at  ``http://<oom-vm>:30219/onap/login.htm``.   Select the *PDP* menu entry on the left side panel under *Policy*.

    .. image:: srmPdpxPdpMgmt.png

After initialization, there will be no policies loaded into the policy subsystem.    This can be verified by accessing the Editor tab in the UI.


PDP-D Active/Active Pool
^^^^^^^^^^^^^^^^^^^^^^^^

The PDP-Ds replicas will come up with the amsterdam controller installed in brainless mode (no maven coordinates) since the controller has not been associated with a set of drools rules to run (control loop rules).

The following command can be issued on each of the PDP-D replicas IPs:

.. code-block:: bash
   :caption: Querying the rules association for a PDP-D replica 

    # Using default credentials for the drools telemetry service.
    # To change the default username and passwords for this service,
    # please modify configuration pre-installation at:
    # oom/kubernetes/policy/charts/drools/resources/config/opt/policy/config/drools/base.conf
     
     
    curl --silent --user '@1b3rt:31nst31n' -X GET http://<drools-replica-ip>:9696/policy/pdp/engine/controllers/amsterdam/drools | python -m json.tool
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



Before Installing Policies
^^^^^^^^^^^^^^^^^^^^^^^^^^

It has been experienced in large OOM k8s multi-node full ONAP installations that components DNS and connectivity problems across pods through services.   Eventually, the system becomes stable and ready to be used.   Single node, smaller installations, do not seem to have these issues.   Give the system enough time to make sure it has been initialized properly before pushing policies.

Make sure the policy subsystem is initialized by:

    1. Verify that the "PDP Management" screen shows the 1 pooled PDP-X  "UP_TO_DATE".   If the PDP-X does not show the correct state, restart the faulty one to force re-synchronization with the pap.

    .. code-block:: bash
       :caption: Force re-synchronization of a PDP-X
    
        kubectl exec -it dev-pdp-0 --container pdp -n onap -- bash -c "source /opt/app/policy/etc/profile.d/env.sh; policy.sh stop; policy.sh start"
         
        # bounce the BRMSGW as well since it synchronizes with PDP-Xs via websockets:
         
        kubectl exec -it dev-brmsgw-b877bc567-wbnbz -n onap -- bash -c "source /opt/app/policy/etc/profile.d/env.sh; policy.sh stop; policy.sh start"


    2. Verify service name resolution is OK across policy components

    .. code-block:: bash
       :caption: Verify policy services connectivity

        # pick any policy pod to run these tests from:
        # kubectl get pods --all-namespaces -o=wide
         
        kubectl exec -it dev-brmsgw-b877bc567-wbnbz -n onap -- bash -c "ping policydb"
        kubectl exec -it dev-brmsgw-b877bc567-wbnbz -n onap -- bash -c "ping pdp"
        kubectl exec -it dev-brmsgw-b877bc567-wbnbz -n onap -- bash -c "ping drools"
        kubectl exec -it dev-brmsgw-b877bc567-wbnbz -n onap -- bash -c "ping nexus"
        kubectl exec -it dev-brmsgw-b877bc567-wbnbz -n onap -- bash -c "ping message-router"


Installing Policies
^^^^^^^^^^^^^^^^^^^

The OOM default installation will come with no policies pre-configured.  There is a sample script used by integration teams to load policies to support all four use cases at:   */tmp/policy-install/config/push-policies.sh* in the pap container within the pap pod.   This script can be modified for your own particular installation, for example if only interested in vCPE use cases, remove those vCPE related API REST calls.   For the vFW use case, you may want to edit the encoded operational policy to point to the proper resourceID in your installation.

The above mentioned *push-policies.sh* script can be executed as follows:

.. code-block:: bash
   :caption: Installing the default policies

    # NOTE: If modifications are required to the /tmp/policy-install/config/push-policies.sh, it should be copied 
    # to a different location, for example /tmp as /tmp/policy-install/config directory is read-only.
     
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

The policies pushed could be viewed eventually through the Policy UI:

    .. image:: srmEditor.png

As part of the process pushing of policies through the policy, the brmsgw component will compose drools rules artifacts and publish them to the nexus respository at ``http://<oom-vm>:30236/nexus/``.

    .. image:: srmNexus.png

At the same time each replica of the PDP-Ds will receive notifications for each new version of the policies to run for the Amsterdam controller.   The following command can be run to see how the amsterdam controller is associated with the latest rules version. 

The following command can be used for verifying each replica:


.. code-block:: none
   :caption: Querying the rules association of a PDP-D replica

    # Using default credentials for the drools telemetry service.
    # To change the default username and passwords for this service,
    # please modify configuration pre-installation at:
    # oom/kubernetes/policy/charts/drools/resources/config/opt/policy/config/drools/base.conf
     
    curl --silent --user '@1b3rt:31nst31n' -X GET http://<replica-ip>:9696/policy/pdp/engine/controllers/amsterdam/drools | python -m json.tool

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

Likewise, for verification purposes, each PDP-X replica can be queried directly to retrieve policy information.   

The following commands can be used to query a policy through the pdp service:


.. code-block:: bash
   :caption: Querying the "pdp" service for the vFirewal policy

    # Open a shell into the pap pod

    ubuntu@k8sx:~$ kubectl exec -it dev-pap-8587696769-vwj6k -c pap -n onap bash

    # In this example the vFirewall policy is queried.

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
    

While the following commands could be used to query an specific PDP-X replica:


.. code-block:: bash
   :caption: Querying PDP-X 0 for the vCPE policy

    # open a shell into the pap pod
 
    ubuntu@k8sx:~$ kubectl exec -it dev-pap-8587696769-vwj6k -c pap -n onap bash
 
    # in this example the vCPE policy is queried.

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

A PDP-X container failure can be simulated by either:
   a) performing a"policy.sh stop" operation within the PDP-X container, which in fact will shutdown the PDP-X service, and eventually will be detected by the liveness checks, or 
   b) by plainly deleting the corresponding pod.    

In the following example, the PDP-X 0 is forced to fail.

.. code-block:: bash
   :caption: Causing PDP-X 0 service to fail

    # In these scenarios the liveness check will fail and recovery actions will take place.
     
     
    # Alternative 1: In this scenario we shutdown the PDP-X 0 service, so the liveness monitored ports will be down 
    # (but the pod is up) and corrective measures will be applied
    ubuntu@k8sx:~$ kubectl exec -it dev-pdp-0 --container pdp -n onap -- bash -c "source /opt/app/policy/etc/profile.d/env.sh; policy.sh stop;"
        pdplp: STOPPING ..
        pdp: STOPPING ..
     
    # Alternative 2: Brute force delete of the PDP-X 0 pod.
    ubuntu@k8sx:~$ kubectl delete pod dev-pdp-0 -n onap
        pod "dev-pdp-0" deleted

Upon detection of the service being down through the liveness check, the container will be restarted.   Note the **restart count** when querying the status of the pods:

.. code-block:: bash
   :caption: Checking PDP-X 0 restart count

    ubuntu@k8sx:~$ kubectl get pods --all-namespaces -o=wide
     
    NAMESPACE  NAME                             READY     STATUS    RESTARTS   AGE     IP              NODE

    onap       dev-brmsgw-5dbc4c8dc4-llk5s      1/1       Running   0          3d      10.42.120.43    k8sx
    onap       dev-drools-0                     1/1       Running   0          3d      10.42.60.27     k8sx
    onap       dev-nexus-7d96568f5f-qp5td       1/1       Running   0          3d      10.42.172.8     k8sx
    onap       dev-pap-8587696769-vwj6k         2/2       Running   0          3d      10.42.19.137    k8sx
    onap       dev-pdp-0                        2/2       Running   0          3d      10.42.144.218   k8sx
    onap       dev-policydb-587d55bdff-4f5dz    1/1       Running   0          3d      10.42.12.242    k8sx
    

During the restart process, the PAP component, will detect that PDP-X 0 is down and therefore its state being reflected in the PDP-X screen:

    .. image:: srmPdpxResiliencyPdpMgmt1.png

This screen will be updated to reflect PDP-X 0 is back alive, after PDP-X 0 synchronizes itself with the PAP. 

    .. image:: srmPdpxResiliencyPdpMgmt2.png

At that point, the PDP-X is usable either directly or through the service to query for policies.


.. code-block:: bash
   :caption: Query PDP-X 1 for vCPE policy

    # in this example we perform the vCPE query from the OOM VM
    # the default installation credentials are used for querying the vCPE policy

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

A PDP-D container failure can be simulated by either:
   a) performing a"policy stop" operation within the PDP-D pod, which in fact will shutdown the PDP-D service, and eventually will be detected by the liveness checks, or 
   b) by plainly deleting the corresponding pod.    

In the following example, the PDP-D 0 is forced to fail.

.. code-block:: bash
   :caption: Causing PDP-D 0 to fail

    # In these scenarios the liveness check will fail and recovery actions will take place.

    # Alternative 1: in this scenario we shutdown the PDP-D 0 policy process, so the liveness monitored ports 
    # will be down (but the pod is up) and corrective measures will be applied

    ubuntu@k8sx:~/oom/kubernetes$ kubectl exec -it dev-drools-0 --container drools -n onap -- bash -c "source /opt/app/policy/etc/profile.d/env.sh; policy stop"
    [drools-pdp-controllers]
    L []: Stopping Policy Management... Policy Management (pid=3284) is stopping... Policy Management has stopped.


Upon detection of the service being down through the liveness check, the container will be restarted.   Note the restart count when querying the status of the pods:

.. code-block:: bash
   :caption: Checking PDP-D 0 restart count

    ubuntu@k8sx:~$ kubectl get pods --all-namespaces -o=wide | grep drool
    onap    dev-drools-0     0/1    Running   0    1d     10.42.10.21     k8sx
    ..
    ubuntu@k8sx:~$ kubectl get pods --all-namespaces -o=wide | grep drools
    onap    dev-drools-0     1/1    Running   1    1d     10.42.10.21     k8sx  <-- note restart count

Verification that the restarted PDP-D 0 comes up with the appropriate policy loaded can be verified by checking its maven coordinates:

.. code-block:: bash
   :caption: Verifying restarted PDP-D points to policies pre-failure.

    ubuntu@k8sx:~$ curl --silent --user '@1b3rt:31nst31n' -X GET http://10.42.10.21:9696/policy/pdp/engine/controllers/amsterdam/drools | python -m json.tool
    {
        "alive": true,
        "artifactId": "policy-amsterdam-rules",
        "brained": true,
        "groupId": "org.onap.policy-engine.drools.amsterdam",
        "locked": false,
        "modelClassLoaderHash": 189820624,
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


PDP-X Scaling
^^^^^^^^^^^^^

To scale a new PDP-X, set the replica count appropriately.   

In our tests below, we are going to work with the OOM policy component in isolation.   In this exercise, we scale the PDP-X with 1 additional replica, PDP-X 1.  

.. code-block:: bash
   :caption: Scaling a PDP-X

    ubuntu@k8sx:~$ helm upgrade -i dev local/onap --namespace onap --set global.pullPolicy=IfNotPresent --set policy.pdp.replicaCount=2
    Release "dev" has been upgraded. Happy Helming!
    LAST DEPLOYED: Mon Jun  4 15:19:05 2018
    NAMESPACE: onap
    STATUS: DEPLOYED
     
    RESOURCES:
    ==> v1/Service
    NAME                      TYPE       CLUSTER-IP     EXTERNAL-IP  PORT(S)                        AGE
    dbc-pg-primary            ClusterIP  10.43.29.226   <none>       5432/TCP                       2d
    dbc-pg-replica            ClusterIP  10.43.202.168  <none>       5432/TCP                       2d
    dbc-postgres              ClusterIP  10.43.181.134  <none>       5432/TCP                       2d
    dmaap-bc                  NodePort   10.43.254.230  <none>       8080:30241/TCP,8443:30242/TCP  2d
    message-router-kafka      ClusterIP  10.43.69.159   <none>       9092/TCP                       2d
    message-router-zookeeper  ClusterIP  None           <none>       2181/TCP                       2d
    message-router            NodePort   10.43.123.102  <none>       3904:30227/TCP,3905:30226/TCP  2d
    msb-consul                NodePort   10.43.27.77    <none>       8500:30285/TCP                 2d
    msb-discovery             NodePort   10.43.178.20   <none>       10081:30281/TCP                2d
    msb-eag                   NodePort   10.43.77.235   <none>       80:30282/TCP,443:30284/TCP     2d
    msb-iag                   NodePort   10.43.221.196  <none>       80:30280/TCP,443:30283/TCP     2d
    brmsgw                    NodePort   10.43.21.222   <none>       9989:30216/TCP                 2d
    nexus                     NodePort   10.43.159.27   <none>       8081:30236/TCP                 2d
    drools                    NodePort   10.43.233.67   <none>       6969:30217/TCP,9696:30221/TCP  2d
    policydb                  ClusterIP  None           <none>       3306/TCP                       2d
    pdp                       ClusterIP  None           <none>       8081/TCP                       2d
    pap                       NodePort   10.43.110.50   <none>       8443:30219/TCP,9091:30218/TCP  2d
    robot                     NodePort   10.43.172.248  <none>       88:30209/TCP                   2d
     
    ==> v1beta1/Deployment
    NAME                          DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
    dev-dmaap-bus-controller      1        1        1           1          2d
    dev-message-router-kafka      1        1        1           1          2d
    dev-message-router-zookeeper  1        1        1           1          2d
    dev-message-router            1        1        1           1          2d
    dev-kube2msb                  1        1        1           1          2d
    dev-msb-consul                1        1        1           1          2d
    dev-msb-discovery             1        1        1           1          2d
    dev-msb-eag                   1        1        1           1          2d
    dev-msb-iag                   1        1        1           1          2d
    dev-brmsgw                    1        1        1           1          2d
    dev-nexus                     1        1        1           1          2d
    dev-policydb                  1        1        1           1          2d
    dev-pap                       1        1        1           1          2d
    dev-robot                     1        1        1           1          2d
     
    ==> v1beta1/StatefulSet
    NAME        DESIRED  CURRENT  AGE
    dev-dbc-pg  2        2        2d
    dev-drools  1        1        2d
    dev-pdp     2        2        2d
     
    ==> v1/PersistentVolumeClaim
    NAME                          STATUS  VOLUME                        CAPACITY  ACCESS MODES  STORAGECLASS  AGE
    dev-message-router-kafka      Bound   dev-message-router-kafka      2Gi       RWX           2d
    dev-message-router-zookeeper  Bound   dev-message-router-zookeeper  2Gi       RWX           2d
    dev-nexus                     Bound   dev-nexus                     2Gi       RWX           2d
    dev-policydb                  Bound   dev-policydb                  2Gi       RWX           2d
     
    ==> v1/ConfigMap
    NAME                                         DATA  AGE
    dev-dmaap-bus-controller-config              1     2d
    dev-message-router-cadi-prop-configmap       1     2d
    dev-message-router-msgrtrapi-prop-configmap  1     2d
    dev-msb-discovery                            1     2d
    dev-msb-eag                                  1     2d
    dev-msb-iag                                  1     2d
    dev-brmsgw-pe-configmap                      2     2d
    dev-drools-configmap                         6     2d
    dev-drools-log-configmap                     1     2d
    dev-drools-settings-configmap                1     2d
    dev-policydb-configmap                       1     2d
    dev-pdp-log-configmap                        1     2d
    dev-pdp-pe-configmap                         3     2d
    dev-pe-scripts-configmap                     1     2d
    dev-filebeat-configmap                       1     2d
    dev-pe-configmap                             1     2d
    dev-pap-pe-configmap                         7     2d
    dev-pap-sdk-log-configmap                    1     2d
    dev-pap-log-configmap                        1     2d
    dev-robot-resources-configmap                3     2d
    dev-robot-lighttpd-authorization-configmap   1     2d
    dev-robot-eteshare-configmap                 4     2d
     
    ==> v1/PersistentVolume
    NAME                          CAPACITY  ACCESS MODES  RECLAIM POLICY  STATUS  CLAIM                              STORAGECLASS     REASON  AGE
    dev-dbc-pg-data0              1Gi       RWO           Retain          Bound   onap/dev-dbc-pg-data-dev-dbc-pg-0  dev-dbc-pg-data  2d
    dev-dbc-pg-data1              1Gi       RWO           Retain          Bound   onap/dev-dbc-pg-data-dev-dbc-pg-1  dev-dbc-pg-data  2d
    dev-message-router-kafka      2Gi       RWX           Retain          Bound   onap/dev-message-router-kafka      2d
    dev-message-router-zookeeper  2Gi       RWX           Retain          Bound   onap/dev-message-router-zookeeper  2d
    dev-nexus                     2Gi       RWX           Retain          Bound   onap/dev-nexus                     2d
    dev-policydb                  2Gi       RWX           Retain          Bound   onap/dev-policydb                  2d
     
    ==> v1beta1/ClusterRoleBinding
    NAME          AGE
    onap-binding  2d
     
    ==> v1/Pod(related)
    NAME                                          READY  STATUS    RESTARTS  AGE
    dev-dmaap-bus-controller-5bd859c7dc-blzdc     1/1    Running   0         2d
    dev-message-router-kafka-748cdf7b9c-srv7l     1/1    Running   0         2d
    dev-message-router-zookeeper-5b5969f6f-8rk9w  1/1    Running   0         2d
    dev-message-router-b5bdc599c-5h56k            1/1    Running   0         2d
    dev-kube2msb-579fc77c54-m84qx                 1/1    Running   0         2d
    dev-msb-consul-7bc4fcc8-94gsc                 1/1    Running   0         2d
    dev-msb-discovery-768547bcb-2hr7j             2/2    Running   0         2d
    dev-msb-eag-5d95686c67-9lkzs                  2/2    Running   0         2d
    dev-msb-iag-675b649848-pv2gh                  2/2    Running   0         2d
    dev-brmsgw-5675f5877b-wv68s                   1/1    Running   0         2d
    dev-nexus-7d96568f5f-m8c4l                    1/1    Running   0         2d
    dev-policydb-587d55bdff-9gdjv                 1/1    Running   0         2d
    dev-pap-678b44cd87-wxbww                      2/2    Running   0         2d
    dev-robot-589c76bb6b-hrrdn                    1/1    Running   0         2d
    dev-dbc-pg-0                                  1/1    Running   0         2d
    dev-dbc-pg-1                                  1/1    Running   0         2d
    dev-drools-0                                  1/1    Running   1         2d
    dev-pdp-0                                     2/2    Running   1         2d
    dev-pdp-1                                     0/2    Init:0/1  0         0s
     
    ==> v1/Secret
    NAME                       TYPE                     DATA  AGE
    dev-dbc-pg                 Opaque                   3     2d
    dev-message-router-secret  Opaque                   1     2d
    dev-drools-secret          Opaque                   2     2d
    dev-policydb-secret        Opaque                   2     2d
    onap-docker-registry-key   kubernetes.io/dockercfg  1     2d
    

Check Policy Engine UI how the PDP-Xs are coming up and request policies to the PAP.

Eventually the new PDP-X will be connected and serving policies:

    .. image:: srmPdpxScalingPdpMgmt1.png

The new PDP-X should be now ready to serve policies:

.. code-block:: bash
   :caption: Check that the new PDP-X 3 and 4 can serve policies

    ubuntu@k8sx:~/oom/kubernetes$ curl --silent -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'ClientAuth: cHl0aG9uOnRlc3Q=' --header 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' --header 'Environment: TEST' -d '{"policyName": ".*vCPE.*"}' http://10.42.183.0:8081/pdp/api/getConfig | python -m json.tool
    [
        {
            "config": ..
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
     
     
    ubuntu@k8sx:~/oom/kubernetes$ curl --silent -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'ClientAuth: cHl0aG9uOnRlc3Q=' --header 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' --header 'Environment: TEST' -d '{"policyName": ".*vCPE.*"}' http://10.42.137.241:8081/pdp/api/getConfig | python -m json.tool
    [
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
        },
        {
            "config": ...
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
        }
    ]


PDP-D Scaling
^^^^^^^^^^^^^

To scale a new PDP-D, set the replica count appropriately.   In our scenario below, we are going to scale the PDP-D service to add a new pod (2 active PDP-Ds).

.. code-block:: bash
   :caption: Scaling a PDP-D

    #   Note: we also set the PDP-X pool to 2 instances (matching the previous section)
     
     
    ubuntu@k8sx:~$ helm upgrade -i dev local/onap --namespace onap --set global.pullPolicy=IfNotPresent --set policy.pdp.replicaCount=2 --set policy.drools.replicaCount=2
    Release "dev" has been upgraded. Happy Helming!
    LAST DEPLOYED: Mon Jun  4 15:52:46 2018
    NAMESPACE: onap
    STATUS: DEPLOYED
     
    RESOURCES:
    ==> v1/ConfigMap
    NAME                                         DATA  AGE
    dev-dmaap-bus-controller-config              1     2d
    dev-message-router-cadi-prop-configmap       1     2d
    dev-message-router-msgrtrapi-prop-configmap  1     2d
    dev-msb-discovery                            1     2d
    dev-msb-eag                                  1     2d
    dev-msb-iag                                  1     2d
    dev-brmsgw-pe-configmap                      2     2d
    dev-drools-configmap                         6     2d
    dev-drools-log-configmap                     1     2d
    dev-drools-settings-configmap                1     2d
    dev-policydb-configmap                       1     2d
    dev-pdp-pe-configmap                         3     2d
    dev-pdp-log-configmap                        1     2d
    dev-pe-scripts-configmap                     1     2d
    dev-filebeat-configmap                       1     2d
    dev-pe-configmap                             1     2d
    dev-pap-pe-configmap                         7     2d
    dev-pap-log-configmap                        1     2d
    dev-pap-sdk-log-configmap                    1     2d
    dev-robot-resources-configmap                3     2d
    dev-robot-lighttpd-authorization-configmap   1     2d
    dev-robot-eteshare-configmap                 4     2d
     
    ==> v1/PersistentVolume
    NAME                          CAPACITY  ACCESS MODES  RECLAIM POLICY  STATUS  CLAIM                              STORAGECLASS     REASON  AGE
    dev-dbc-pg-data0              1Gi       RWO           Retain          Bound   onap/dev-dbc-pg-data-dev-dbc-pg-0  dev-dbc-pg-data  2d
    dev-dbc-pg-data1              1Gi       RWO           Retain          Bound   onap/dev-dbc-pg-data-dev-dbc-pg-1  dev-dbc-pg-data  2d
    dev-message-router-kafka      2Gi       RWX           Retain          Bound   onap/dev-message-router-kafka      2d
    dev-message-router-zookeeper  2Gi       RWX           Retain          Bound   onap/dev-message-router-zookeeper  2d
    dev-nexus                     2Gi       RWX           Retain          Bound   onap/dev-nexus                     2d
    dev-policydb                  2Gi       RWX           Retain          Bound   onap/dev-policydb                  2d
     
    ==> v1/PersistentVolumeClaim
    NAME                          STATUS  VOLUME                        CAPACITY  ACCESS MODES  STORAGECLASS  AGE
    dev-message-router-kafka      Bound   dev-message-router-kafka      2Gi       RWX           2d
    dev-message-router-zookeeper  Bound   dev-message-router-zookeeper  2Gi       RWX           2d
    dev-nexus                     Bound   dev-nexus                     2Gi       RWX           2d
    dev-policydb                  Bound   dev-policydb                  2Gi       RWX           2d
     
    ==> v1beta1/ClusterRoleBinding
    NAME          AGE
    onap-binding  2d
     
    ==> v1beta1/Deployment
    NAME                          DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
    dev-dmaap-bus-controller      1        1        1           1          2d
    dev-message-router-kafka      1        1        1           1          2d
    dev-message-router-zookeeper  1        1        1           1          2d
    dev-message-router            1        1        1           1          2d
    dev-kube2msb                  1        1        1           1          2d
    dev-msb-consul                1        1        1           1          2d
    dev-msb-discovery             1        1        1           1          2d
    dev-msb-eag                   1        1        1           1          2d
    dev-msb-iag                   1        1        1           1          2d
    dev-brmsgw                    1        1        1           1          2d
    dev-nexus                     1        1        1           1          2d
    dev-policydb                  1        1        1           1          2d
    dev-pap                       1        1        1           1          2d
    dev-robot                     1        1        1           1          2d
     
    ==> v1/Pod(related)
    NAME                                          READY  STATUS    RESTARTS  AGE
    dev-dmaap-bus-controller-5bd859c7dc-blzdc     1/1    Running   0         2d
    dev-message-router-kafka-748cdf7b9c-srv7l     1/1    Running   0         2d
    dev-message-router-zookeeper-5b5969f6f-8rk9w  1/1    Running   0         2d
    dev-message-router-b5bdc599c-5h56k            1/1    Running   0         2d
    dev-kube2msb-579fc77c54-m84qx                 1/1    Running   0         2d
    dev-msb-consul-7bc4fcc8-94gsc                 1/1    Running   0         2d
    dev-msb-discovery-768547bcb-2hr7j             2/2    Running   0         2d
    dev-msb-eag-5d95686c67-9lkzs                  2/2    Running   0         2d
    dev-msb-iag-675b649848-pv2gh                  2/2    Running   0         2d
    dev-brmsgw-5675f5877b-wv68s                   1/1    Running   0         2d
    dev-nexus-7d96568f5f-m8c4l                    1/1    Running   0         2d
    dev-policydb-587d55bdff-9gdjv                 1/1    Running   0         2d
    dev-pap-678b44cd87-wxbww                      2/2    Running   0         2d
    dev-robot-589c76bb6b-hrrdn                    1/1    Running   0         2d
    dev-dbc-pg-0                                  1/1    Running   0         2d
    dev-dbc-pg-1                                  1/1    Running   0         2d
    dev-drools-0                                  1/1    Running   1         2d
    dev-drools-1                                  0/1    Init:0/1  0         1s
    dev-pdp-0                                     2/2    Running   1         2d
    dev-pdp-1                                     2/2    Running   0         33m
     
    ==> v1/Secret
    NAME                       TYPE                     DATA  AGE
    dev-dbc-pg                 Opaque                   3     2d
    dev-message-router-secret  Opaque                   1     2d
    dev-drools-secret          Opaque                   2     2d
    dev-policydb-secret        Opaque                   2     2d
    onap-docker-registry-key   kubernetes.io/dockercfg  1     2d
     
    ==> v1beta1/StatefulSet
    NAME        DESIRED  CURRENT  AGE
    dev-dbc-pg  2        2        2d
    dev-drools  2        2        2d
    dev-pdp     2        2        2d
     
    ==> v1/Service
    NAME                      TYPE       CLUSTER-IP     EXTERNAL-IP  PORT(S)                        AGE
    dbc-postgres              ClusterIP  10.43.181.134  <none>       5432/TCP                       2d
    dbc-pg-replica            ClusterIP  10.43.202.168  <none>       5432/TCP                       2d
    dbc-pg-primary            ClusterIP  10.43.29.226   <none>       5432/TCP                       2d
    dmaap-bc                  NodePort   10.43.254.230  <none>       8080:30241/TCP,8443:30242/TCP  2d
    message-router-kafka      ClusterIP  10.43.69.159   <none>       9092/TCP                       2d
    message-router-zookeeper  ClusterIP  None           <none>       2181/TCP                       2d
    message-router            NodePort   10.43.123.102  <none>       3904:30227/TCP,3905:30226/TCP  2d
    msb-consul                NodePort   10.43.27.77    <none>       8500:30285/TCP                 2d
    msb-discovery             NodePort   10.43.178.20   <none>       10081:30281/TCP                2d
    msb-eag                   NodePort   10.43.77.235   <none>       80:30282/TCP,443:30284/TCP     2d
    msb-iag                   NodePort   10.43.221.196  <none>       80:30280/TCP,443:30283/TCP     2d
    brmsgw                    NodePort   10.43.21.222   <none>       9989:30216/TCP                 2d
    nexus                     NodePort   10.43.159.27   <none>       8081:30236/TCP                 2d
    drools                    NodePort   10.43.233.67   <none>       6969:30217/TCP,9696:30221/TCP  2d
    policydb                  ClusterIP  None           <none>       3306/TCP                       2d
    pdp                       ClusterIP  None           <none>       8081/TCP                       2d
    pap                       NodePort   10.43.110.50   <none>       8443:30219/TCP,9091:30218/TCP  2d
    robot                     NodePort   10.43.172.248  <none>       88:30209/TCP                   2d
    
Verify that the new PDP-D comes up with the latest policy coordinates:

.. code-block:: bash
   :caption: Verify new PDP-D 2 comes up with policies loaded

    ubuntu@k8sx:~$ curl --silent --user '@1b3rt:31nst31n' -X GET http://10.42.172.88:9696/policy/pdp/engine/controllers/amsterdam/drools | python -m json.tool
    {
        "alive": true,
        "artifactId": "policy-amsterdam-rules",
        "brained": true,
        "groupId": "org.onap.policy-engine.drools.amsterdam",
        "locked": false,
        "modelClassLoaderHash": 1657760388,
        "recentSinkEvents": [],
        "recentSourceEvents": [],
        "sessionCoordinates": [
            "org.onap.policy-engine.drools.amsterdam:policy-amsterdam-rules:0.5.0:closedloop-amsterdam"
        ],
        "sessions": [
            "closedloop-amsterdam"
        ],
        "version": "0.5.0"
    }
    

End of Document

.. SSNote: Wiki page ref. https://wiki.onap.org/display/DW/Policy+on+OOM
.. SSNote: Old Wiki page ref. https://wiki.onap.org/display/DW/Scalability%2C+Resiliency+and+Manageability


